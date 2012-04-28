/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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
import java.util.ArrayList;
import java.util.Arrays;

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

    // Convert each jMusic Note to an Impro-Visor Note.

    for( jm.music.data.Note note : origNoteArray ) 
      {
        double origRhythmValue = note.getRhythmValue();
         if( note.isRest() )
          {
          int rhythmValue = precision*(int)((BEAT*(time + origRhythmValue) - slot)/precision);
          Note newRest = Note.makeRest(rhythmValue);
          partOut.addNote(newRest);
          slot += rhythmValue;
         }
        else
          {
          Note newNote = new Note(note.getPitch(),
                  precision*(int)Math.round((BEAT * note.getRhythmValue()) / precision));
          partOut.addNote(newNote);
        //System.out.println("beat " + slot/FBEAT + ": " + note.getDuration() + " -> " + newNote);
          int rhythmValue = newNote.getRhythmValue();
          slot += rhythmValue;
          }
        time += origRhythmValue;
      }
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



}