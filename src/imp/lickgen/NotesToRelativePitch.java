/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014 Robert Keller and Harvey Mudd College
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

package imp.lickgen;

import imp.data.Chord;
import imp.data.Note;
import polya.Polylist;

/**
 *
 * @author Mark Heimann
 */

public class NotesToRelativePitch { 
    private static final int octave = 12; //number of semitones in an octave
    private static final int rootOffset = 60; //middle C (the MIDI number for middle C is 60, +- n for +- n semitones) for now
    
    //chord families: minor, minor7, major, dominant, half-diminished, diminished, augmented
    //create arrays for each chord family that store the scale degree corresponding to each number of half steps
        
    //Note: I tried to give each scale degree a name that was logical and/or corresponded to common practice.
    //For some pitches (#5 or b13?  2 or 9, 4 or 11, 6 or 13?) there was more ambiguity.  Some scales (e.g. diminished, augmented)
    //also caused weirdness by virtue of how their accidentals are arranged.  Thus, there was definitely room for interpretation.
    //However, this nomenclature system should be consistent and thus suitable for its primary use, 
    //which is internal (grammar learning with bricks, transformational grammars).
    //Further explanatory comments provided as needed.
    
    //minor (i.e. melodic minor)
    private static final String[] minorScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "b7", "7"};
        
    //minor 7 (i.e. Dorian)
    private static final String[] minor7ScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "7", "#7"};
        
    //major (i.e. Ionian)
    private static final String[] majorScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "5", "#5", "6", "b7", "7"};
        
    //dominant (i.e. Mixolydian)
    private static final String[] dominantScaleDegrees = {"1", "b9", "9", "#9", "3", "4", "b5", "5", "b13", "13", "7", "#7"};
        
    //half diminished
    //note: 6 half steps above the root has to be b5--otherwise, what do you call a perfect 5?
    private static final String[] halfDimScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "7", "#7"};
        
    //diminished
    //note: 8 half steps above root is a 6th because a fully diminished 7th is the same intervals as a major 6th
    //so also, nine half steps becomes a seventh
    private static final String[] dimScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "6", "7", "#7", "b8"};
      
    //augmented (e.g. major#5)
    //here, seven half steps is called a b5 (because it's flat relative to the augmented fifth) even though the interval is a perfect fifth
    private static final String[] augScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "b5", "5", "6", "b7", "7"};
    
    
    /** noteToRelativePitch
     * Conversion of note to relative pitch
     * @param note, an absolute pitch and duration 
     * @param chord, the chord that note is over
     * @return the note in relative pitch form 
     */
    public static Polylist noteToRelativePitch(Note note, Chord chord) {
        String chordFamily = chord.getFamily(); //whether chord is major, minor, etc.
        Polylist relativeNote = Polylist.nil; //this will be our relative note
        
        //the root offset transposes notes so they're in a "normal" pitch range, though this probably isn't strictly necessary
        int pitch = note.getPitch()%octave + rootOffset;
        int root = chord.getRootSemitones() + rootOffset; //gives number of semitones the pitch of the root is above a C, plus the offset (which makes it above middle C)
        //make sure the note is at least as high in pitch as the root (otherwise transpose up)
        if (pitch - root < 0) { 
            pitch += octave;
        }
        
        //Part 1 of the note construction: add an X to signify that it's a relative pitch
        relativeNote = relativeNote.addToEnd("X");
        
        //Part 2 of the note construction: add scale degree
        int pitchOffset = pitch - root; //note: this has been normalized to be between 0 and 11
        if (chordFamily.equals("minor")) {
            relativeNote = relativeNote.addToEnd(minorScaleDegrees[pitchOffset]);
        }
        else if (chordFamily.equals("minor7")) {
            relativeNote = relativeNote.addToEnd(minor7ScaleDegrees[pitchOffset]);
        }
        else if (chordFamily.equals("major")) {
            relativeNote = relativeNote.addToEnd(majorScaleDegrees[pitchOffset]);
        }
        else if (chordFamily.equals("dominant")) {
            relativeNote = relativeNote.addToEnd(dominantScaleDegrees[pitchOffset]);
        }
        else if (chordFamily.equals("half-diminished")) {
            relativeNote = relativeNote.addToEnd(halfDimScaleDegrees[pitchOffset]);
        }
        else if (chordFamily.equals("diminished")) {
            relativeNote = relativeNote.addToEnd(dimScaleDegrees[pitchOffset]);
        }
        else if (chordFamily.equals("augmented")) {
            relativeNote = relativeNote.addToEnd(augScaleDegrees[pitchOffset]);
        }
        
        //Part 3 of the note construction: add the rhythm amount
        String noteLength = Integer.toString(note.getRhythmValue());
        relativeNote = relativeNote.addToEnd(noteLength);
        return relativeNote;
    }
    
    //method to test that the conversion works
    /** testConversion
     * Test the conversion process to see if it produces xNote, the note you think it will
     * @param pitch the pitch of a note to be converted
     * @param natural whether or not the note to be converted is natural
     * @param sharp whether or not the note to be converted is sharp or flat
     * @param length the length of the note to be converted
     * @param chordName the name of the corresponding chord
     * @param xNote the String representation of the desired relative pitch note
     * @return true if the conversion works
     */
    public static boolean testConversion(int pitch, boolean natural, boolean sharp, int length, String chordName, String xNote) {
        Note testNote = new Note(pitch, natural, sharp, length);
        Chord testChord = new Chord(chordName);
        Polylist testConvert = noteToRelativePitch(testNote, testChord);
        return testConvert.toString().equals(xNote);
    }
}

