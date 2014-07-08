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
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Unit;
import imp.gui.Notate;
import imp.util.ErrorLog;
import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author Mark Heimann
 */
public class NotesToRelativePitch {

    private static final int octave = 12; //number of semitones in an octave
    private static final int rootOffset = 60; //middle C (the MIDI number for middle C is 60, +- n for +- n semitones) for now
    /*chord families: minor, minor7, major, dominant, half-diminished, diminished, augmented
    create arrays for each chord family that store the scale degree corresponding to each number of half steps
    Note: I tried to give each scale degree a name that was logical and/or corresponded to common practice.
    For some pitches (#5 or b13?  2 or 9, 4 or 11, 6 or 13?) there was more ambiguity.  Some scales (e.g. diminished, augmented)
    also caused weirdness by virtue of how their accidentals are arranged.  Thus, there was definitely room for interpretation.
    However, this nomenclature system should be consistent and thus suitable for its primary use, 
    which is internal (grammar learning with bricks, transformational grammars).
    Further explanatory comments provided as needed.*/
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

    /**
     * noteToRelativePitch Conversion of note to relative pitch
     *
     * @param note, an absolute pitch and duration
     * @param chord, the chord that note is over
     * @return the note in relative pitch form
     */
    public static Polylist noteToRelativePitch(Note note, Chord chord) {
        String chordFamily = chord.getFamily(); //whether chord is major, minor, etc.
        Polylist relativeNote = Polylist.nil; //this will be our relative note

        //the root offset transposes notes so they're in a "normal" pitch range, though this probably isn't strictly necessary
        int pitch = note.getPitch() % octave + rootOffset;
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
        } else if (chordFamily.equals("minor7")) {
            relativeNote = relativeNote.addToEnd(minor7ScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("major")) {
            relativeNote = relativeNote.addToEnd(majorScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("dominant") || chordFamily.equals("sus4") || chordFamily.equals("alt")) { //treat sus4 chords like dominant
            relativeNote = relativeNote.addToEnd(dominantScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("half-diminished")) {
            relativeNote = relativeNote.addToEnd(halfDimScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("diminished")) {
            relativeNote = relativeNote.addToEnd(dimScaleDegrees[pitchOffset]);
        } else if (chordFamily.equals("augmented")) {
            relativeNote = relativeNote.addToEnd(augScaleDegrees[pitchOffset]);
        } else {
            ErrorLog.log(ErrorLog.COMMENT, "Unrecognized chord family");
            return null;
        }

        //Part 3 of the note construction: add the rhythm amount
        String noteLength = note.getDurationString(note.getRhythmValue());
        relativeNote = relativeNote.addToEnd(noteLength);
        return relativeNote;
    }

    //method to test that the conversion works
    /**
     * testConversion Test the conversion process to see if it produces xNote,
     * the note you think it will
     *
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

    //returns a string representation of a relative pitch melody, given a melody (use this, for example in writing productions)
    public static String melStringToRelativePitch(int slotsPerSection, ChordPart chordProg, String exactMelody) {
        ArrayList<Chord> allChords = chordProg.getChords();

        //split up the string containing melody info
        String[] exactMelodyData = exactMelody.split(" ");

        String relativePitchMelody = "";

        //first item is tells us the starting slot of this section of melody
        int startSlot = Integer.parseInt(exactMelodyData[0]);

        int chordNumber = 0; //index of the i-th chord in this measure we've looked at as a possible match for this note
        int totalChordDurationInMeasure = allChords.get(0).getRhythmValue(); //total number of slots belonging to chords we've looked at as a possible match for this note
        int totalNoteDurationInMeasure = 0; //total number of slots that have gone by in this measure up to this note
        for (int i = 1; i < exactMelodyData.length; i += 2) {
            int pitch = Integer.parseInt(exactMelodyData[i]); //every odd index item is a note
            int duration = Integer.parseInt(exactMelodyData[i + 1]); //every even index item (after 0) is a duration
            while (totalNoteDurationInMeasure >= totalChordDurationInMeasure) { //we need to move on to the next chord
                chordNumber++;
                totalChordDurationInMeasure += allChords.get(chordNumber).getRhythmValue();
            }
            try {
                if (pitch >= 0) { //pitch is a note
                    Note note = new Note(pitch, duration);
                    Polylist relativePitch = noteToRelativePitch(note, allChords.get(chordNumber));
                    relativePitchMelody = relativePitchMelody.concat(relativePitch.toString());
                } else { //"pitch" is a rest
                    String rest = "R" + imp.data.Note.getDurationString(duration) + " ";
                    relativePitchMelody = relativePitchMelody.concat(rest.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            totalNoteDurationInMeasure += duration;
        }
        return relativePitchMelody;
    }
    
    //given a slice of melody as a MelodyPart, convert it to a series of relative pitches
    public static String melPartToRelativePitch(MelodyPart melPart, ChordPart chordPart) {
        String relMel = "";
        int totalDuration = 0;
        int melodySize = melPart.getSize();
        while (totalDuration < melodySize) {
            Note note = melPart.getNote(totalDuration);
            if (note.getPitch() >= 0) { //"note" is actually a note
                Chord chord = chordPart.getCurrentChord(totalDuration);
                Polylist relativePitch = noteToRelativePitch(note, chord);
                relMel = relMel.concat(relativePitch.toString());
            } else { //"note" is a rest
                String rest = "R" + imp.data.Note.getDurationString(note.getRhythmValue()) + " ";
                relMel = relMel.concat(rest.toString());
            }
            totalDuration += note.getRhythmValue();
        }
        return relMel;
    }

    public static Polylist noteToAbstract(int noteIndex, Notate notate) {
        //get type of note
        ChordPart chordProg = notate.getChordProg();
        imp.lickgen.LickGen lickgen = notate.getLickGen();
        MelodyPart part = notate.getCurrentMelodyPart().copy();
        Note note = part.getNote(noteIndex);
        Polylist rhythmString = Polylist.nil;
        StringBuilder sb = new StringBuilder();
        int value = note.getDurationString(sb, note.getRhythmValue()); //originally passed sb in as well
        int pitch = note.getPitch();
        if (note.isRest()) {
            rhythmString = rhythmString.addToEnd("R" + sb.substring(1));
        } else {

            //add pitch to notes
            //get note type
            char notetype;
            
            int[] notetone = lickgen.getNoteTypes(noteIndex, pitch, pitch,
                    chordProg);
            switch (notetone[0]) {
                case LickGen.CHORD:
                    notetype = 'C';
                    break;
                case LickGen.COLOR:
                    notetype = 'L';
                    break;
                default:
                    notetype = 'X';
                    break;
            }
            if (notetype == 'X' && part.getNextNote(noteIndex) != null) {

                int nextPitch = part.getNextNote(noteIndex).getPitch();
                int nextIndex = part.getNextIndex(noteIndex);
                if (nextIndex <= noteIndex) {
                    int pitchdiff = nextPitch - pitch;
                    if (Math.abs(pitchdiff) == 1) {
                        notetype = 'A';
                    }
                }
            }
            rhythmString = rhythmString.addToEnd(notetype + sb.substring(1));
        }
        return rhythmString;
    }

    public static String melodyToAbstract(MelodyPart melPart, ChordPart chordPart, boolean isSongStart, Notate notate, LickGen lickgen) {
        if (melPart.melodyIsEmpty(0, melPart.getSize())) {
            StringBuilder sb = new StringBuilder();
            Note n = new Note(72, 1);
            n.getDurationString(sb, melPart.getSize());
            String returnString = "((slope 0 0 R" + sb.substring(1) + "))";
            if (isSongStart) {
                returnString = returnString.concat("STARTER");
            }
            return returnString;
        }

        int current = 0;

        Polylist rhythmString = new Polylist();

        //pitches of notes in measure not including rests
        ArrayList<Integer> notes = new ArrayList<Integer>();

        boolean tiedAtStart = false, tiedAtEnd = false;

        //untie first note if it is tied from last measure
        if (melPart.getPrevNote(current) != null && melPart.getPrevNote(current).getRhythmValue() > current - melPart.getPrevIndex(current)) {

            tiedAtStart = true;
            //untie and set the previous note
            Note untiedNote = melPart.getPrevNote(current).copy();
            int originalRhythmVal = untiedNote.getRhythmValue();
            int rhythmVal = melPart.getSize() - melPart.getPrevIndex(current) % melPart.getSize();
            untiedNote.setRhythmValue(rhythmVal);
            melPart.setNote(melPart.getPrevIndex(current), untiedNote);

            //set the current note
            rhythmVal = originalRhythmVal - rhythmVal;
            Note currNote = melPart.getPrevNote(current).copy();
            currNote.setRhythmValue(rhythmVal);
            melPart.setNote(current, currNote);
        }

        if (melPart.getPrevNote(melPart.getSize()) != null) {
            //untie notes at end of measure and beginning of next measure
            if (melPart.getPrevNote(melPart.getSize()).getRhythmValue() > melPart.getSize() - melPart.getPrevIndex(
                    melPart.getSize())) {
                tiedAtEnd = true;
                int tracker = melPart.getPrevIndex(melPart.getSize());
                Note untiedNote = melPart.getNote(tracker).copy();
                int originalRhythmVal = untiedNote.getRhythmValue();
                int rhythmVal = melPart.getSize() - (tracker % melPart.getSize());
                untiedNote.setRhythmValue(rhythmVal);
                melPart.setNote(tracker, untiedNote);
                int secondRhythmVal = originalRhythmVal - rhythmVal;
                untiedNote = melPart.getNote(tracker).copy();
                untiedNote.setRhythmValue(secondRhythmVal);
                melPart.setNote(melPart.getSize(), untiedNote);
            }
        }

        if (melPart.getPrevNote(1) != null) {
            if ((melPart.getPrevIndex(1) != 0) && !(melPart.getPrevNote(1).isRest())) {
                return null;
            }
        }

        while (current < melPart.getSize()) {

            //if null note, make it a rest
            if (melPart.getNote(current) == null) {
                int next = melPart.getNextIndex(current);
                Note n = Note.makeRest(next - current);
                melPart.setNote(current, n);
            }

            StringBuilder sb = new StringBuilder();


            int value = melPart.getNote(current).getDurationString(sb, melPart.getNote(
                    current).getRhythmValue());

            int pitch = melPart.getNote(current).getPitch();

            int rhythm = 0;



            if (melPart.getNote(current).isRest()) {
                rhythmString = rhythmString.cons("R" + sb.substring(1));
            } else {

                //add pitch to notes
                notes.add(pitch);
                //get note type
                char notetype;
                int[] notetone = lickgen.getNoteTypes(current, pitch, pitch,
                        notate.getChordProg());
                switch (notetone[0]) {
                    case LickGen.CHORD:
                        notetype = 'C';
                        break;
                    case LickGen.COLOR:
                        notetype = 'L';
                        break;
                    default:
                        notetype = 'X';
                        break;
                }
                if (notetype == 'X' && melPart.getNextNote(current) != null) {

                    int nextPitch = melPart.getNextNote(current).getPitch();
                    int nextIndex = melPart.getNextIndex(current);
                    if (nextIndex <= melPart.getSize()) {
                        int pitchdiff = nextPitch - pitch;
                        if (Math.abs(pitchdiff) == 1) {
                            notetype = 'A';
                        }
                    }
                }
                rhythmString = rhythmString.cons(notetype + sb.substring(1));
            }


            current = melPart.getNextIndex(current);

        }

        rhythmString = rhythmString.reverse();

        //process intervals
        ArrayList<Integer> intervals = new ArrayList<Integer>();
        intervals.add(0);
        for (int i = 1; i < notes.size(); i++) {
            intervals.add(notes.get(i) - notes.get(i - 1));
        }

        //process slopes
        ArrayList<int[]> slopes = new ArrayList<int[]>();
        int[] slope = new int[3];
        int tracker = 0;


        //get the slope from the note before this section to the first note in the measure
        int prevIndex = melPart.getPrevIndex(0);
        Note lastNote = melPart.getNote(prevIndex);
        while (lastNote != null && lastNote.isRest()) {
            prevIndex = melPart.getPrevIndex(prevIndex);
            lastNote = melPart.getNote(prevIndex);
        }
        int lastpitch = 0;
        if (lastNote != null && !lastNote.isRest()) {
            lastpitch = lastNote.getPitch();
        }
        int pitch = notes.get(0);
        int pitchChange;
        if (lastpitch == 0) {
            pitchChange = 0;
        } else {
            pitchChange = pitch - lastpitch;
        }
        int minPitchChange = 0, maxPitchChange = 0;
        //avoid random notes and repeated notes
        if (pitchChange != 0) {
            if (pitchChange == 1) {
                minPitchChange = 1;
                maxPitchChange = 2;
            } else if (pitchChange == -1) {
                minPitchChange = -2;
                maxPitchChange = -1;
            } else {
                minPitchChange = pitchChange - 1;
                maxPitchChange = pitchChange + 1;
            }
        }

        //if there is only 1 note, return it with its slope
        if (intervals.size() <= 1) {

            String rhythm = rhythmString.toString();
            rhythm = rhythm.substring(1, rhythm.length() - 1);

            //handle case of only 1 note
            if (rhythm.equals("")) {
                char thisPitch = lickgen.getNoteType(0, notes.get(0), notes.get(
                        0), notate.getChordProg());
                String len = Note.getDurationString(melPart.getSize());
                rhythm = thisPitch + len;
            }
            String returnString =
                    "((slope " + minPitchChange + " " + maxPitchChange + " " + rhythm + "))";
            if (isSongStart) {
                returnString = returnString.concat("STARTER");
            }
            if (tiedAtEnd) {
                returnString = returnString.concat(" ENDTIED");
            }
            if (tiedAtStart) {
                returnString = returnString.concat(" STARTTIED");
            }
            return returnString;
        }

        for (int i = 0; i < intervals.size(); i++) {
            tracker = i;
            if (intervals.get(i) != 0) {
                i = intervals.size();
            }
        }


        //direction is -1 if slope is going down, 0 for repeated note, 1 for up
        int direction = 0;
        if (intervals.get(tracker) > 0) {
            direction = 1;
        } else if (intervals.get(tracker) < 0) {
            direction = -1;
        }
        //initialize stuff - first note is in its own slope
        slope[0] = minPitchChange;
        slope[1] = maxPitchChange;
        slope[2] = 1;
        slopes.add(slope.clone());

        slope[0] = intervals.get(1);
        slope[1] = intervals.get(1);
        slope[2] = 0;
        for (int i = 1; i < intervals.size(); i++) {
            //slope was going up but not any more
            if (direction == 1 && intervals.get(i) <= 0) {
                if (intervals.get(i) == 0) {
                    direction = 0;
                } else {
                    direction = -1;
                }
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }

                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
                //slope was going down but not any more
            } else if (direction == -1 && intervals.get(i) >= 0) {
                if (intervals.get(i) == 0) {
                    direction = 0;
                } else {
                    direction = 1;
                }
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }
                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
                //slope was 0 but not any more
            } else if (direction == 0 && intervals.get(i) != 0) {
                if (intervals.get(i) > 0) {
                    direction = 1;
                } else {
                    direction = -1;
                }
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }
                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
            } else {
                slope[2]++;
                if (intervals.get(i) > slope[1]) {
                    slope[1] = intervals.get(i);
                }
                if (intervals.get(i) < slope[0]) {
                    slope[0] = intervals.get(i);
                }
            }

            if (i == intervals.size() - 1) {
                if (slope[2] != 0) {
                    slopes.add(slope.clone());
                }
            }
        }

        //add in slopes
        StringBuilder strbuf = new StringBuilder();
        strbuf.append("(");
        Polylist tempString = rhythmString;
        for (int i = 0; i < slopes.size(); i++) {
            slope = slopes.get(i);
            strbuf.append("(slope ");
            strbuf.append(slope[0]);
            strbuf.append(" ");
            strbuf.append(slope[1]);
            strbuf.append(" ");

            int j = 0;
            //get all of notes if last slope
            if (i == slopes.size() - 1) {
                while (tempString.nonEmpty()) {
                    strbuf.append(tempString.first().toString());
                    strbuf.append(" ");
                    tempString = tempString.rest();
                }
            } else {
                while (j < slope[2]) {
                    String temp = tempString.first().toString();
                    strbuf.append(temp);
                    strbuf.append(" ");
                    //System.out.println(strbuf.toString());
                    tempString = tempString.rest();
                    if (temp.charAt(0) != 'R') {
                        j++;
                    }
                }
            }
            strbuf.deleteCharAt(strbuf.length() - 1);
            strbuf.append(")");
        }
        strbuf.append(")");
        {
            //Mark measure as 'songStarter' if it is the first of a song
            if (isSongStart) {
                strbuf.append("STARTER");
            }
            strbuf.append("CHORDS ");

            ChordPart chords = notate.getChordProg().extract(0,
                    melPart.getSize() - 1);
            ArrayList<Unit> chordList = chords.getUnitList();
            if (chordList.isEmpty()) {
                System.out.println("No chords");
            }
            for (int i = 0; i < chordList.size(); i++) {
                String nextChord = ((Chord) chordList.get(i)).toLeadsheet();
                strbuf.append(nextChord);
                strbuf.append(" ");
            }

            return strbuf.toString();
        }
    }
    
}
