/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012-2014 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.data;

import imp.Constants;
import static imp.Constants.BEAT;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/**
 * ImportMelody is adapted by Robert Keller from ImportBass, by Brandy McMenamy
 * and James Herold 7/18/2007 Reads and parses a channel of a midi file as a
 * melody line. The program assumes that all notes are played in sequential
 * order (no chords) and therefore only reads the first Phrase. It then
 * interprets the rhythm duration of each note as the closest musical value that
 * can be represented by 120 slots per beat for the given time signature.
 * Pitches are kept as integers representing Hertz values. The final melody was
 * originally stored in MIDIBeast.originalBassNotes, which of course breaks
 * encapsulation.
 */
public class ImportMelody implements Constants
{
static int FACTOR = 120;

//private ArrayList<Note> originalMelodyNotes = new ArrayList<Note>();

private jm.music.data.Part parts[];

/**
 * constructor. Reads a score and adjusts the rhythm durations in the melody
 * line.
 */

public ImportMelody(jm.music.data.Score score)
  {
    parts = score.getPartArray();
  }


/**
 * Convert the designated track of a jm Part to an Impro-Visor part
 * @param melodyPart
 * @param trackNumber
 * @param partOut
 * @param precision 
 */
public static void convertToImpPart(jm.music.data.Part melodyPart,
                                    int trackNumber,
                                    MelodyPart partOut,
                                    int precision)
  {
     convertToImpPart(melodyPart.getPhraseArray()[trackNumber], 
                      partOut, 
                      precision);
  }
    

/*
 * Convert a phrase of a jm Part to an Impro-Visor part
 */
public static void convertToImpPart(jm.music.data.Phrase phrase,
                                    MelodyPart partOut,
                                    int precision)
  {
    jm.music.data.Note[] notes = phrase.getNoteArray();
    
    ArrayList<jm.music.data.Note> origNoteArray = new ArrayList<jm.music.data.Note>();
    
    origNoteArray.addAll(Arrays.asList(notes));

    //if(MIDIBeast.mergeMelodyRests) mergeRests();

    // This is a key step in getting melodies to be acceptable to Impro-Visor

    // Handle the case where the phrase does not start immediately.

    double startTime = phrase.getStartTime();
    double time = startTime;
    int slot = 0;

    if( startTime > 0 )
      {
      int restSlots = precision*(int)((BEAT*startTime)/precision);
 
      if( restSlots > 0 )
        {
        partOut.addRest(new Rest(restSlots));
        }
      
      slot += restSlots; 
      }

   noteArray2ImpPart(origNoteArray, time, partOut, slot, precision);
   // returned slot is ignored.
  }


/**
 * Convert notes in jm Note array to Impro-Visor notes and add them to melody part.
 * @param origNoteArray
 * @param time
 * @param partOut
 * @param slot
 * @param quantum 
 */

public static int noteArray2ImpPart(ArrayList<jm.music.data.Note> origNoteArray,
                                double time,
                                MelodyPart partOut,
                                int slot,
                                int quantum)
  {
    //System.out.println("\nquantum = " + quantum);
    int notesLost = 0;
    
    Iterator<jm.music.data.Note> origNotes = origNoteArray.iterator();
    int endLastNote = slot;
    int accumulatedRestSlots = 0;
    while( origNotes.hasNext() )
      {
        double scaledTime = time*FACTOR;
        jm.music.data.Note note = origNotes.next();
        double origRhythmValue = note.getRhythmValue();
        //System.out.println("\nslot: " + slot + " scaledTime: " + scaledTime + " note: " + note);
        if( !note.isRest() )
          {
            // Rests in the original are ignored. Rests in the new part are
            // created when a note end is strictly less than the next note onset.
            if( slot >= scaledTime + quantum )
              {
                // The note cannot be placed in the current quantum, so it is ignored.
                //System.out.println("slot: " + slot + " > scaledTime: " + scaledTime + " lost");
                notesLost++;
              }
            else
              {
              // "catch up" slot to scaledTime
              if( slot < scaledTime )
                {
                  slot = quantum*(int)Math.ceil(scaledTime/quantum);
                }
              
              if( scaledTime <= (slot + quantum) )
                {
                // Can the note be scheduled in the current quantum?
                //System.out.println("slot: " + slot + " endLastNote: " + endLastNote + " note lost");
                if( slot >= endLastNote )
                  {
                  // Using this slot should not cut into the previous note.
                  int duration = quantum*(int)Math.ceil((origRhythmValue*FACTOR)/quantum);
                  int pitch = note.getPitch();
                  Note newNote = new Note(pitch, duration);
                  int gap = slot - endLastNote;
                  if( gap > 0 )
                    {
                    // Fill in gap with rest if needed
                    partOut.addRest(new Rest(gap));
                    }
                  partOut.addNote(newNote);
                  endLastNote = slot + duration;
                  //System.out.println("slot: " + slot + " endLastNote " + endLastNote + " placing: " + newNote);
                  slot = endLastNote;
                  }
                }
              else
                {
                notesLost++;
                }
              }
            }
          time += origRhythmValue;
          }

    System.out.println("notes lost in quantization: " + notesLost);
    return slot;
  }

int size()
  {
    return parts.length;
  }


/**
 * Finds the ith part.
 */

public jm.music.data.Part getPart(int i)
  {
    return parts[i];
  }


public void mergeRests(ArrayList<jm.music.data.Note> origNoteArray)
  {
    for( int i = 1; i < origNoteArray.size(); i++ )
      {
        if( !(origNoteArray.get(i).isRest()) )
          {
            continue;
          }
        origNoteArray.get(i - 1).setRhythmValue(origNoteArray.get(i).getRhythmValue() 
                                              + origNoteArray.get(i - 1).getRhythmValue());
        origNoteArray.remove(i);
        i--;
      }
  }


/**
 * Convert an Impro-Visor MelodyPart to a jMusic score
 */
public static jm.music.data.Score impMelody2jmScore(MelodyPart partIn)
  {
    jm.music.data.Phrase phrase = new jm.music.data.Phrase();

    int num_slots = partIn.size();
    
    for( int slot = 0; slot < num_slots; slot++ )
      {
        Note impNote = partIn.getNote(slot);
        //System.out.println("note: " + impNote);
        if( impNote != null )
          {
            double duration = ((double)impNote.getRhythmValue())/FACTOR;
            if( impNote.isRest() )
              {
                phrase.addRest(new jm.music.data.Rest(duration));
              }
            else
              {
              phrase.addNote(impNote.getPitch(), duration);
              }
          }
      }

   jm.music.data.Part part = new jm.music.data.Part(phrase);
   return new jm.music.data.Score(part);
  }

}