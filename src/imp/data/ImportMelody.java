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
public class ImportMelody
{

//private ArrayList<Note> originalMelodyNotes = new ArrayList<Note>();

private jm.music.data.Part parts[];

boolean debug = false;


/**
 * The initial notes from the melody line
 */

private ArrayList<jm.music.data.Note> origNoteArray;


/**
 * The final notes that are used as the "original notes" by each successive step
 * in the style generation. (Contains corrected rhythm durations).
 */

private ArrayList<Note> roundedNoteArray;


/**
 * constructor. Reads a score and adjusts the rhythm durations in the melody
 * line.
 */

public ImportMelody(jm.music.data.Score score)
  {
    parts = score.getPartArray();
  }


public void convertToImpPart(jm.music.data.Part melodyPart,
                             int trackNumber,
                             MelodyPart partOut)
  {
    origNoteArray = new ArrayList<jm.music.data.Note>();

    jm.music.data.Phrase phrase = melodyPart.getPhraseArray()[trackNumber];

    jm.music.data.Note[] notes = phrase.getNoteArray();
    
    origNoteArray.addAll(Arrays.asList(notes));

    //if(MIDIBeast.mergeMelodyRests) mergeRests();

    // This is a key step in getting melodies to be acceptable to Impro-Visor

    roundedNoteArray = roundDurations(MIDIBeast.precision, origNoteArray);

    // Handle the case where the phrase does not start immediately.

    double startTime = phrase.getStartTime();

    if( startTime > 0 )
      {
        int restSlots = MIDIBeast.doubleValToSlots(startTime);

        partOut.addRest(new Rest(restSlots));
      }

    // Convert each jMusic Note to an Impro-Visor Note.

    for( Note note : roundedNoteArray )
      {
        partOut.addNote(note);
      }
  }


public static Note convertToImpNote(jm.music.data.Note noteIn)
  {
    int pitch = noteIn.getPitch();
    int numberOfSlots = MIDIBeast.doubleValToSlots(noteIn.getRhythmValue());
    if( pitch < 0 )
      {
        return new Rest(numberOfSlots);
      }

    Note note = new Note(pitch, numberOfSlots);
    note.setVolume(noteIn.getDynamic());
    return note;
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


public void mergeRests()
  {
    for( int i = 1; i < origNoteArray.size(); i++ )
      {
        if( !(origNoteArray.get(i).isRest()) )
          {
            continue;
          }
        origNoteArray.get(i - 1).setRhythmValue(origNoteArray.get(i).getRhythmValue() + origNoteArray.get(i - 1).getRhythmValue());
        origNoteArray.remove(i);
        i--;
      }
  }


/**
 * Rounds the rhythm duration of each note to its closest musical value
 * equivalent using a 120 slots per beat format.
 *
 * @param precision
 */

public ArrayList<Note> roundDurations(int precision, ArrayList<jm.music.data.Note> noteArray)
  {
    roundedNoteArray = new ArrayList<Note>();

    for( int i = 0; i < noteArray.size(); i++ )
      {
        jm.music.data.Note noteIn = noteArray.get(i);
        Note noteOut = convertToImpNote(noteIn);
//        if( noteIn.getPitch() >= 0 )
//          {
//          System.out.println(noteIn + " -> " + noteOut);
//          }
        roundedNoteArray.add(noteOut);
      }

    if( debug )
      {
        System.out.println("## After roundDurations() ##");
        int totalNoteDuration = 0;
        for( int i = 0; i < roundedNoteArray.size(); i++ )
          {
            totalNoteDuration += roundedNoteArray.get(i).getRhythmValue();
          }
      }
    return roundedNoteArray;
  }

}