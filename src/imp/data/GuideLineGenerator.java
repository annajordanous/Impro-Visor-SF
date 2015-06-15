/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2014 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.data;

import imp.Constants;
import java.util.ArrayList;
import polya.Polylist;
import imp.lickgen.NoteConverter;

/**
 * The GuideLineGenerator class is used to create a leadsheet of guide tones
 * for a given chord progression to help a player in their improvisation.
 * 
 * The generator uses a chord part to create a melody part that is added to
 * the choruses in notate.
 * 
 * @see ChordPart and MelodyPart
 * @author Mikayla Konst and Carli Lessard
 * 
 */

public class GuideLineGenerator implements Constants {
    
    //The chords used to create the guide tone line
    private final ChordPart chordPart;
    
    //Direction the user wants the guide tone line to go
    //The direction of the guide tone line is reset to the original direction
    //at the beginning of each section
    private final int originalDirection;
    
    //Actual direction of the guide tone line
    //Initialized to be the originalDirection, but
    //can be changed if the guide tone line threatens to go too high/low
    private int direction;
    
    //constants that correspond to the integers that represent direction
    private final int ASCENDING = 1;
    private final int DESCENDING = -1;
    private final int NOPREFERENCE = 0;
    
    //Scale degrees that are frequently used
    private final String FOUR = "4";
    private final String THREE = "3";
    
    private final String SEVEN = "7";
    private final String SIX = "6";
    
    private final String NINE = "2";
    private final String EIGHT = "1";
    
    private final String SHARPFOUR = "#4";
    private final String FLATFIVE = "b5";
    private final String FIVE = "5";
    
    private final String FOURTHREE = "4-3";
    private final String SEVENSIX = "7-6";
    private final String NINEEIGHT = "9-8";
    private final String NOSUSPENSION = "noSus";
    private final String FOURFIVE = "4-5";
    private final String SIXFIVE = "6-5";
    
    //Which scale degree to start on
    private final String startDegree;
    //Whether or not there is one line or two lines
    private final boolean mix;
    //Determines the shape of the two lines
    private final boolean alternating;
    //MIDI values of lower and upper limits for guide tone line
    private final int lowLimit, highLimit;
    private final int maxDuration;
    private final boolean durationSpecified;
    
    private static final boolean DISALLOW_SAME = true;
    private static final boolean SAME_OKAY = false;
    
    public GuideLineGenerator(ChordPart inputChordPart, int direction, String startDegree, boolean alternating, int lowLimit, int highLimit, int maxDuration) 
    {
        chordPart = inputChordPart;
        this.originalDirection = direction;
        this.direction = direction;
        this.startDegree = startDegree;
        this.alternating = alternating;
        if(startDegree.equals("mix")){
            mix=true;
        }else{
            mix=false;
        }
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
        this.maxDuration = maxDuration;
        if(maxDuration==0){
            durationSpecified = false;
        }else{
            durationSpecified = true;
        }
    }
    
    /**
     * tests whether a note's duration is greater than the given duration
     * @param n1 Note whose duration is being tested
     * @param duration duration to be tested against
     * @return true if n1's duration is strictly greater than duration, false otherwise
     */
    private boolean greaterThan(Note n1, int duration){
        int noteDuration = n1.getRhythmValue();
        if(noteDuration>duration){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Splits up a note into a list of notes so that no note has a duration greater than maxDuration
     * all notes in the list have the same pitch value as the note passed in
     * @param n the note to be split up
     * @return 
     */
    private ArrayList<Note> splitUpNote(Note n){

        int pitch = n.getPitch();
        ArrayList<Note> notes = new ArrayList<Note>();
        int durationRemaining;
        for(durationRemaining = n.getRhythmValue(); durationRemaining > maxDuration; durationRemaining -= maxDuration){
            notes.add(new Note(pitch, maxDuration));
        }
        
        notes.add(new Note(pitch, durationRemaining));
        return notes;
    }
    
    /**
     * Generates a list of notes to add from a note to be split up and a chord
     * Possibly switches direction of guide tone line at addition of each note
     * Replaces repeated 3's, 6's, and 8's with 4-3, 7-6, and 9-8 suspensions
     * @param note note to be converted to an ArrayList of notes
     * @param c the chord that the notes are to be played over
     * @return an ArrayList of notes to be played over the chord generated
     * using the nextNote method disallowing same note
     * 
     */
    private ArrayList<Note> notesToAdd(Note note, Chord c){
        
        ArrayList<Note> notes = splitUpNote(note);
        ArrayList<Note> notesToAdd = new ArrayList<Note>();
        
        Note prevNote = notes.remove(0);
        
        notesToAdd.add(prevNote);
        possibleDirectionSwitch(prevNote);
        for(Note n: notes){
            
            //find next Note, making same notes have really bad scores
            Note noteToAdd = nextNote(prevNote, c, DISALLOW_SAME);
            noteToAdd.setRhythmValue(n.getRhythmValue());
            
            //Old way to deal with same notes - finds "best" color tone
            /*if(noteToAdd.getPitch()==prevNote.getPitch()){
                //recalculate noteToAdd, allowing color tones
                noteToAdd = nextColorNote(prevNote, c, DISALLOW_SAME);
                noteToAdd.setRhythmValue(n.getRhythmValue());
            }*/
            
            
            
            //Check if same as last note. If yes, see if a suspension applies
            String suspension = suspensionCheck(prevNote, noteToAdd, c);
            
            if(!suspension.equals(NOSUSPENSION)){
                int duration = prevNote.getRhythmValue();
                //remove prevNote
                notesToAdd.remove(notesToAdd.size()-1);
                Note replacement;
                if(suspension.equals(FOURTHREE)){
                    replacement = scaleDegreeToNote(NoteConverter.familyToFourThree(c.getFamily())[0], c, noteToAdd, duration);
                    //replacement = scaleDegreeToNote(FOUR, c, noteToAdd, duration);
                    
                    //add replacement
                    notesToAdd.add(replacement);
                    possibleDirectionSwitch(prevNote);
                }else if(suspension.equals(SEVENSIX)){
                    
                    replacement = scaleDegreeToNote(SEVEN, c, noteToAdd, duration);
                    
                    //add replacement
                    notesToAdd.add(replacement);
                    possibleDirectionSwitch(prevNote);
                }else if(suspension.equals(NINEEIGHT)){
                    replacement = scaleDegreeToNote(NoteConverter.familyToNineEight(c.getFamily())[0], c, noteToAdd, duration);
                    //replacement = scaleDegreeToNote(NINE, c, noteToAdd, duration);
                    
                    //add replacement
                    notesToAdd.add(replacement);
                    possibleDirectionSwitch(prevNote);
                }else if(suspension.equals(FOURFIVE)){
                    
                    //Try b5, #4, then 4 in that order
                    
                    replacement = scaleDegreeToNote(FLATFIVE, c, noteToAdd, duration);
                    if(replacement==null){
                        replacement = scaleDegreeToNote(SHARPFOUR, c, noteToAdd, duration);
                        if (replacement==null){
                            replacement = scaleDegreeToNote(FOUR, c, noteToAdd, duration);
                        }
                    }
                    
                    //add replacement
                    notesToAdd.add(replacement);
                    possibleDirectionSwitch(prevNote);
                }else if(suspension.equals(SIXFIVE)){
                    replacement = scaleDegreeToNote(NoteConverter.familyToSixFive(c.getFamily())[0], c, noteToAdd, duration);
                    //replacement = scaleDegreeToNote(SIX, c, noteToAdd, duration);
                    
                    //add replacement
                    notesToAdd.add(replacement);
                    possibleDirectionSwitch(prevNote);
                }
                else{
                    //currently no other suspensions
                }
            }
            
            
            
            
            //Add note, possibly switch direction of line, store note as prevNote
            notesToAdd.add(noteToAdd);
            possibleDirectionSwitch(noteToAdd);
            prevNote = noteToAdd;
        }
        
        return notesToAdd;
    }
    
    private Note scaleDegreeToNote(String degree, Chord chord, Note closeNote, int rhythmValue){
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(closeNote);
        int octave = ns.getOctave(); //NEED TO FIX
        Note closest = NoteConverter.scaleDegreeToNote(degree, chord, octave-1, rhythmValue);
        if(closest==null){
            return null;
        }
        int closestMIDI = closest.getPitch();
        for(int oct = octave-1; oct<=octave+1; oct++){
            Note n = NoteConverter.scaleDegreeToNote(degree, chord, oct, rhythmValue);
            int midi = n.getPitch();
            if(Math.abs(closeNote.getPitch()-midi)<Math.abs(closeNote.getPitch()-closestMIDI)){
                closestMIDI = midi;
                closest = n;
            }
        }
        
        return closest;
    }
    
    
    
    private String suspensionCheck(Note n1, Note n2, Chord c){
        //using getPitchClassName because octaves could differ
        if(n1.getPitch()!=n2.getPitch()){
            return NOSUSPENSION;
        }else if(scaleDegreeToNote(THREE, c).getPitchClassName().equals(n2.getPitchClassName())){
            return FOURTHREE;
        }else if(scaleDegreeToNote(SIX, c).getPitchClassName().equals(n2.getPitchClassName())){
            return SEVENSIX;
        }else if(scaleDegreeToNote(EIGHT, c).getPitchClassName().equals(n2.getPitchClassName())){
            return NINEEIGHT;
        }else if(scaleDegreeToNote(FIVE, c).getPitchClassName().equals(n2.getPitchClassName())){
            return SIXFIVE;
        }
        else{
            
            //unfortunately, even those these notes are the same
            //we don't have a suspension for this situation
            return NOSUSPENSION;
        }
    }
    
    
    
    private Note getLast(ArrayList<Note> notes){
        int size = notes.size();
        if(size>0){
            return notes.get(size-1);
        }else{
            return null; //if list is empty
        }
    }
    
    private Note removeLast(ArrayList<Note> notes){
        int size = notes.size();
        if(size>0){
            return notes.remove(size-1);
        }
        else{
            return null; //if list is empty
        }
    }
    
    /**
     * Creates a MelodyPart that contains the guide tone line.
     * @return MelodyPart 
     */
    public MelodyPart makeGuideLine()
    {
        ArrayList<Note> guideLine;
        
        if(mix){
            guideLine = twoGuideLine();
        } else{
            guideLine = oneGuideLine();
        }
        
        MelodyPart guideLineMelody = new MelodyPart();
        for(Note n : guideLine){
            guideLineMelody.addNote(n);
        }
        return guideLineMelody;
    }
    
    /**
     * Used by makeGuideLine() to generate a list of notes to be used in the
     * guide tone line
     * @return ArrayList<Note> The notes of the guide tone line
     */
    private ArrayList<Note> oneGuideLine()
    {
        ArrayList<Note> guideLine = new ArrayList<Note>();
        ArrayList<Integer> startIndices = chordPart.getSectionInfo().getSectionStartIndices();
        
        Integer chordIndex = startIndices.get(0);
        Chord firstChord = chordPart.getChord(chordIndex);
        
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        int index = startIndices.get(0);
        
        Note prevNote = firstNote(firstChord);
        
        for(Integer duration : durations){
            Chord currentChord = chordPart.getChord(index);
            Note noteToAdd;
            if(startIndices.contains(index)){
                //at the start of each section, restore direction
                //to the user's original intended direction
                direction = originalDirection;
                //call firstNote method (as oppossed to nextNote method)
                noteToAdd = firstNote(currentChord);
            }
            else {
                noteToAdd = nextNote(prevNote, currentChord, SAME_OKAY);
            }
            if(durationSpecified && greaterThan(noteToAdd, maxDuration)){
                
                ArrayList<Note> notesToAdd = notesToAdd(noteToAdd, currentChord);
                for(Note n: notesToAdd){
                    guideLine.add(n);
                }
                prevNote = notesToAdd.get(notesToAdd.size()-1);
            }else{
                guideLine.add(noteToAdd);
                possibleDirectionSwitch(noteToAdd);
                prevNote = noteToAdd;
            }
            
            index+=duration;
        }
        
        return guideLine;
    }
    
    public void possibleDirectionSwitch(Note n){
        int inRange = inRange(n.getPitch(), lowLimit, highLimit);
        if(inRange!=0){
            if(inRange==-1){
                direction = ASCENDING;
            }else if(inRange==1){
                direction = DESCENDING;
            }
        }
    }
    
    /**
     * returns whether or not a number lies in a given range
     * @param n number to test
     * @param low low end of range
     * @param high high end of range
     * @return 1 if n is above the range, -1 if below, 0 if within
     */
    private static int inRange(int n, int low, int high){
        int toreturn = 0;
        if(n>=high){
            toreturn = 1;
        }else if(n<=low){
            toreturn = -1;
        }
        return toreturn;
    }
    
    /**
     * Used by makeGuideLine() to generate a list of notes to be used in the
     * guide tone line
     * @return ArrayList<Note> The notes of the guide tone line
     */
    private ArrayList<Note> twoGuideLine()
    {
        ArrayList<Note> guideLine = new ArrayList<Note>();
        ArrayList<Integer> startIndices = chordPart.getSectionInfo().getSectionStartIndices();
        
        Integer chordIndex = startIndices.get(0);
        Chord firstChord = chordPart.getChord(chordIndex);
        
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        int index = startIndices.get(0);
        
        Note prevFirstNote = scaleDegreeToNote(THREE, firstChord);
        Note prevSecondNote = scaleDegreeToNote(SEVEN, firstChord);
        Note firstNoteToAdd;
        Note secondNoteToAdd;
        boolean threeFirst = true;
        
        prevFirstNote.setRhythmValue(firstChord.getRhythmValue()/2);
        prevSecondNote.setRhythmValue(firstChord.getRhythmValue()/2);
        
        for(Integer duration : durations){
            Chord currentChord = chordPart.getChord(index);
            
            if(startIndices.contains(index)){
                //sections always start on degree three
                threeFirst = true;
                //at the beginning of each section, restore direction to user's orignal choice
                direction = originalDirection;
                
                //Set the two next notes to be half the length of the chord
                firstNoteToAdd = scaleDegreeToNote(THREE, currentChord);
                firstNoteToAdd.setRhythmValue(currentChord.getRhythmValue()/2);
                
                secondNoteToAdd = scaleDegreeToNote(SEVEN, currentChord);
                secondNoteToAdd.setRhythmValue(currentChord.getRhythmValue()/2);
                
            }
            else{
                firstNoteToAdd = nextNote(prevFirstNote, currentChord, SAME_OKAY);
                firstNoteToAdd.setRhythmValue(currentChord.getRhythmValue()/2);
                
                secondNoteToAdd = nextNote(prevSecondNote, currentChord, SAME_OKAY);
                secondNoteToAdd.setRhythmValue(currentChord.getRhythmValue()/2);
            }
            boolean firstNoteTooLong = greaterThan(firstNoteToAdd, maxDuration);
            boolean secondNoteTooLong = greaterThan(secondNoteToAdd, maxDuration);
            if(durationSpecified && (firstNoteTooLong || secondNoteTooLong)){
                if(threeFirst){
                    if(firstNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(firstNoteToAdd, currentChord);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevFirstNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(firstNoteToAdd);
                        possibleDirectionSwitch(firstNoteToAdd);
                        prevFirstNote = firstNoteToAdd;
                    }
                    
                    if(secondNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(secondNoteToAdd, currentChord);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevSecondNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(secondNoteToAdd);
                        possibleDirectionSwitch(secondNoteToAdd);
                        prevSecondNote = secondNoteToAdd;
                    }
                    
                }else{
                    if(secondNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(secondNoteToAdd, currentChord);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevSecondNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(secondNoteToAdd);
                        possibleDirectionSwitch(secondNoteToAdd);
                        prevSecondNote = secondNoteToAdd;
                    }
                    
                    if(firstNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(firstNoteToAdd, currentChord);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevFirstNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(firstNoteToAdd);
                        possibleDirectionSwitch(firstNoteToAdd);
                        prevFirstNote = firstNoteToAdd;
                    }
                }
                
            }else{
                //add notes
                // Alternate the order the notes are added to the guide line
                if(threeFirst){
                    guideLine.add(firstNoteToAdd);
                    guideLine.add(secondNoteToAdd);
                }else{
                    guideLine.add(secondNoteToAdd);
                    guideLine.add(firstNoteToAdd);
                }
                
                //store notes
                prevFirstNote = firstNoteToAdd;
                prevSecondNote = secondNoteToAdd;
                
                //possibly switch direction if out of range
                possibleDirectionSwitch(firstNoteToAdd);
                possibleDirectionSwitch(secondNoteToAdd);
            }
            
            //jump ahead to next chord
            index+=duration;
            //switch which line comes first if alternating
            if(alternating){
                threeFirst = !threeFirst;
            }
        }
        
        return guideLine;
    }

    /**
     * Creates the first note for the line to be based off of
     * @param c The first chord of the chord part
     * @return Note of the scale degree that is chosen by the user
     */
    private Note firstNote(Chord c){
        return scaleDegreeToNote(startDegree, c);
    }
    
    /**
     * Generates the next note in the sequence
     * @param n The last note chosen for the array part
     * @param c The next chord in the chord part
     * @return The next note to be used in the guide tone line
     */
    private Note nextNote(Note n, Chord c, boolean disallowSame)
    { 
        ArrayList<Note> possibleNotes = possibleNotes(n,c);
        return bestNote(n, c, possibleNotes, disallowSame);
    }
    
    private Note nextColorNote(Note n, Chord c, boolean disallowSame){
        ArrayList<Note> possibleColorNotes = possibleColorNotes(n,c);
        return bestNote(n, c, possibleColorNotes, disallowSame);
    }
    
    /**
     * Uses scoring to determine the note that will fit best with the previous
     * note and the current chord
     * @param n The previous note in the guide tone line
     * @param c The next chord in the progression
     * @param possibleNotes The notes in the chord that are closest to the 
     *                      previous note
     * @return The note with the best score to fit in the guide tone line
     */
    private Note bestNote(Note n, Chord c, ArrayList<Note> possibleNotes, boolean disallowSame)
    {
        if(possibleNotes.isEmpty()){
            //SHOULD DEFINITELY CHANGE
            //currently choosing the third of the chord if there are no
            //possible notes
            return firstNote(c);
        }
        else{
            Note bestNote = possibleNotes.get(0);
            
            //initialize minScore
            double minScore;
            if(disallowSame){
                minScore = disallowSameNoteScore(n, c, possibleNotes.get(0));
            }else{
                minScore = score(n, c, possibleNotes.get(0));
            }
            
            for(Note currNote : possibleNotes){
                double currScore;
                if(disallowSame){
                    currScore = disallowSameNoteScore(n,c,currNote);
                }else{
                    currScore = score(n,c,currNote);
                }
                
                //IMPLICIT TIE BREAK, could be <=
                if(currScore<minScore){
                    minScore = currScore;
                    bestNote = currNote;
                }
            }
            return bestNote;
        }
    }
    
    /**
     * Gives a score for a note based on its distance from the previous note
     * and which scale degree it is relative to the chord
     * @param prev the last note in the guide tone line
     * @param c the chord the note is chosen from
     * @param note the note being scored
     * @return The relative score of the note, lower means it is a better fit
     */
    private double score(Note prev, Chord c, Note note){
        Polylist pl = NoteConverter.noteToRelativePitch(note, c);
        String degree = (String) pl.second();
        //char accidental = degree.charAt(0);
        double score;
        
        //colorfulness
        if(degree.contains(THREE)||degree.contains(SEVEN)){
            score = 0;
        }else if(degree.contains("5")||degree.contains("9")){
            score = 1;
        }
        else if(degree.contains("b") || degree.contains("#")){
            score = 2;
        }
        else if(degree.equals("5")){
            score = 4;
        }
        else{
            score = 3;
        }
        
        //distance
        int distance;
        if(direction==NOPREFERENCE){
            distance = distance(prev,note);
            if(distance==0){
                score += 1;
            }
            else if(distance==1){
                score += 2;
            }else{
                score += 3;
            }
        }
        //DISTANCE: -2 -1 0 1 2
        //UP SCORE:  3 2.5 2 1.5 1
        //UP SCORE:  5 4 3 2 1
        //DOWN SCORE: 1 1.5 2 2.5 3
        //DOWN SCORE: 1 2 3 4 5
        else{
            distance = directionalDist(prev,note);
            if(distance==-2){
                score += direction==ASCENDING?5:1;
            }
            if(distance==-1){
                score += direction==ASCENDING?4:2;
            }
            if(distance==0){
                score += 3;
            }
            if(distance==1){
                score += direction==ASCENDING?2:4;
            }
            if(distance==2){
                score += direction==ASCENDING?1:5;
            }
        }
        
        return score;
    }
    
    private double disallowSameNoteScore(Note prev, Chord c, Note note){
        if(prev.getPitch()==note.getPitch()){
            return Double.MAX_VALUE;
        }else{
            return score(prev, c, note);
        }
    }
    
    /**
     * Returns the number of semitones between two notes
     * @param n1 note one
     * @param n2 note two
     * @return the absolute distance in semitones between the two notes
     */
    private int distance(Note n1, Note n2)
    {
        return Math.abs(n1.getPitch()-n2.getPitch());
    }
    
    /**
     * Returns the number of semitones note two is above note one
     * Positive indicates note two is above note one, negative means it is below
     * @param n1 note one
     * @param n2 note two
     * @return integer representing the directional distance between two notes
     */
    private int directionalDist(Note n1, Note n2){
        return n2.getPitch()-n1.getPitch();
    }
    
    /**
     * Given one note and the next chord, it returns the closest chord tones
     * to that note
     * @param n note
     * @param c chord
     * @return A list of the chord tones closest to the note
     */
    private ArrayList<Note> possibleNotes(Note n, Chord c)
    {
        return notesInChord(fiveNotes(n,c.getRhythmValue()), c);
    }
    
    /**
     * Given one note and next chord, returns list of color tones that belong
     * to next chord and are within 2 half steps of the note
     * @param n note
     * @param c chord
     * @return list of color tones of chord c close to note n
     */
    private ArrayList<Note> possibleColorNotes(Note n, Chord c){
        return colorTones(fiveNotes(n,c.getRhythmValue()), c);
    }
    
    /**
     * From the five closest notes to another note, it returns a list of which 
     * ones are in a chord c.
     * @param fiveNotes ArrayList of five different notes
     * @param c a chord
     * @return A list of which of the five notes are in the chord
     */
    private ArrayList<Note> notesInChord(ArrayList<Note> fiveNotes, Chord c)
    {
        ArrayList<Note> notesInChord = new ArrayList<Note>();
        for(Note note: fiveNotes){
            if(belongsTo(note, c)){
                notesInChord.add(note);
            }
        }
        return notesInChord;
    }
    
    private ArrayList<Note> colorTones(ArrayList<Note> fiveNotes, Chord c)
    {
        ArrayList<Note> colorTones = new ArrayList<Note>();
        for(Note note: fiveNotes){
            if(colorTone(note, c)){
                colorTones.add(note);
            }
        }
        return colorTones;
    }
    
    /**
     * Determines whether a given note is in a given chord
     * @param n note
     * @param c chord
     * @return Boolean
     */
    private boolean belongsTo(Note n, Chord c)
    {
        if( c.getTypeIndex(n) == Constants.CHORD_TONE ){
            return true;
        }
        else{
            return false;
        }
    }
    
    /**
     * Determines whether a note is a color tone of a certain chord
     * @param n note
     * @param c chord
     * @return boolean - true if note is color tone, false otherwise
     */
    private boolean colorTone(Note n, Chord c){
        if(c.getTypeIndex(n) == Constants.COLOR_TONE){
            return true;
        }else{
            return false;
        }
    }
    
    /**
     * Returns an ArrayList of notes that are within two half steps of the 
     * given note
     * @param n note
     * @param rhythmValue length of notes to be put in the list
     * @return ArrayList of notes
     */
    private ArrayList<Note> fiveNotes(Note n, int rhythmValue)
    {
        ArrayList<Note> fiveNextNotes = new ArrayList<Note>();
        for(int i=-2; i<=2; i++){
            fiveNextNotes.add(new Note(n.getPitch()+i, rhythmValue));
        }
        return fiveNextNotes;
    }
    
    //IMPORTANT: does not specify whether to represent note as sharp or flat
    //if there is ambiguity - I think it defaults to sharp...
    private Note scaleDegreeToNote(String degree, Chord c){
        int octave = direction==ASCENDING?0:1;
        if(mix && degree.equals(SEVEN)){
            octave = 0;
        }
        return NoteConverter.scaleDegreeToNote(degree, c, octave, c.getRhythmValue());
    }  
}
