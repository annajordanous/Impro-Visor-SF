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

import imp.util.ErrorLog;
import java.util.ArrayList;

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

private ArrayList<Note> originalMelodyNotes = new ArrayList<Note>();
private jm.music.data.Score score;
private jm.music.data.Part parts[];
boolean debug = false;
/**
 * The phrase of sequential, non-chordal notes for the melody line
 */

private jm.music.data.Phrase mainPhrase;


/**
 * The initial notes from the melody line
 */

private ArrayList<jm.music.data.Note> noteArray = new ArrayList<jm.music.data.Note>();


/**
 * The final notes that are used as the "original notes" by each successive step
 * in the style generation. (Contains corrected rhythm durations).
 */

private ArrayList<Note> roundedNoteArray = new ArrayList<Note>();


/**
 * constructor. Reads a score and adjusts the rhythm durations in the melody
 * line.
 */

public ImportMelody(jm.music.data.Score score)
  {
    this.score = score;
    parts = score.getPartArray();
  }


public void convertToImpPart(jm.music.data.Part melodyPart,
                             int trackNumber,
                             MelodyPart partOut)
  {
    noteArray = new ArrayList<jm.music.data.Note>();
 
    try
      {
        mainPhrase = melodyPart.getPhraseArray()[trackNumber];
        getNoteArray();
        //if(MIDIBeast.mergeMelodyRests) mergeRests();

        // This is a key step in getting melodies to be acceptable to Impro-Visor
        
        roundedNoteArray = roundDurations(MIDIBeast.precision, noteArray);
        
        if( roundedNoteArray.isEmpty() )
          {
            ErrorLog.log(ErrorLog.WARNING, "note array of size zero, unable to continue");
          }
        else
          {
            originalMelodyNotes = roundedNoteArray;
          }
      }
    catch( ArrayIndexOutOfBoundsException e )
      {
        ErrorLog.log(ErrorLog.WARNING, "An array index out of bounds exception was raised");
      }

    // Handle the case where the phrase does not start immediately.

    double startTime = mainPhrase.getStartTime();

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


/**
 * Reads the notes of the main phrase into noteArray
 */

public void getNoteArray()
  {
    jm.music.data.Note[] notes = mainPhrase.getNoteArray();
    for( int i = 0; i < notes.length; i++ )
      {
        noteArray.add(notes[i]);
      }
  }


public void mergeRests()
  {
    for( int i = 1; i < noteArray.size(); i++ )
      {
        if( !(noteArray.get(i).isRest()) )
          {
            continue;
          }
        noteArray.get(i - 1).setRhythmValue(noteArray.get(i).getRhythmValue() + noteArray.get(i - 1).getRhythmValue());
        noteArray.remove(i);
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
        Note noteOut = convertToImpNote(noteArray.get(i));
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