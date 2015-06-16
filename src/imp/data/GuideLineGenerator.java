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
    private final String THREE = "3";
    private final String SEVEN = "7";

    //Which scale degree to start on
    private final String startDegree;
    
    //Whether or not there is one line or two lines
    private final boolean mix;
    
    //alternating: Determines the shape of the two lines - 
    //if alternating, line looks like this: /\/\/\
    //Else, line looks like this: /////
    private final boolean alternating;
    
    //MIDI values of lower and upper limits for guide tone line
    private final int lowLimit, highLimit;
    
    //Max duration and whether or not a max duration was specified
    private final int maxDuration;
    private final boolean durationSpecified;
    
    //Constants that represent whether two of the same note in a row is allowed
    private static final boolean DISALLOW_SAME = true;
    private static final boolean SAME_OKAY = false;
    
    //Constants that represent where a note is w.r.t. a given pitch range
    private static final int IN_RANGE = 0;
    private static final int BELOW_RANGE = -1;
    private static final int ABOVE_RANGE = 1;
    
    //Array that contains scores. Low scores are good.
    private static final int[][] scores = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {1, 2, 3, 4, 5},        //DESCENDING
        {3, 2, 1, 2, 3},        //NOPREFERENCE
        {5, 4, 3, 2, 1}     };  //ASCENDING
    
    /**
     * Constructor
     * @param inputChordPart ChordPart from score
     * @param direction ASCENDING, DESCENDING, or NOPREFERENCE
     * @param startDegree a string 1 through 7, or "mix" for two lines
     * @param alternating true for /\/\, false for ////
     * @param lowLimit MIDI value of low limit
     * @param highLimit MIDI value of high limit
     * @param maxDuration maxDuration of any note, 0 or less if not specified
     */
    public GuideLineGenerator(ChordPart inputChordPart, int direction, String startDegree, boolean alternating, int lowLimit, int highLimit, int maxDuration) 
    {
        chordPart = inputChordPart;
        this.originalDirection = direction;
        this.direction = direction;
        this.startDegree = startDegree;
        this.alternating = alternating;
        
        //pass in "mix" as the startDegree to signify two lines
        if(startDegree.equals("mix")){
            mix=true;
        }else{
            mix=false;
        }
        
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
        this.maxDuration = maxDuration;
        
        //pass in 0 or less to signify no duration specified
        if(maxDuration<=0){
            durationSpecified = false;
        }else{
            durationSpecified = true;
        }
    }
    
    /**
     * tests whether a note duration is greater than the given duration
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
     * Splits up a note (or rest) into a list of notes so that no note (or rest)
     * has a duration greater than maxDuration
     * all notes in the list have the same pitch value as the note passed in
     * @param n the note to be split up
     * @return list of notes or rests split up using maxDuration
     */
    private ArrayList<Note> splitUpNote(Note n){

        int pitch = n.getPitch();
        ArrayList<Note> notes = new ArrayList<Note>();
        int durationRemaining;
        for(durationRemaining = n.getRhythmValue(); durationRemaining > maxDuration; durationRemaining -= maxDuration){
            if(!(pitch==REST)){
                notes.add(new Note(pitch, maxDuration));
            }else{
                notes.add(new Note(pitch, Accidental.NOTHING, maxDuration));
            }
           
        }
        if(!(pitch==REST)){
           notes.add(new Note(pitch, durationRemaining)); 
        }else{
            notes.add(new Note(pitch, Accidental.NOTHING, durationRemaining));
        }
        
        return notes;
    }
    
    /**
     * Generates a list of notes to add from a note to be split up and a chord
     * Splits up note into list of notes of the same pitch
     * Avoids repeated notes by first only allowing chord tones but making
     * repeated notes have really bad scores, then by resorting to color tones
     * Possibly switches direction of guide tone line at addition of each note
     * @param note note to be converted to an ArrayList of notes
     * @param c the chord that the notes are to be played over
     * @return an ArrayList of notes to be played over the chord generated
     * using the nextNote method disallowing same notes
     * 
     */
    private ArrayList<Note> notesToAdd(Note note, Chord c){
        
        ArrayList<Note> notes = splitUpNote(note);
        ArrayList<Note> notesToAdd = new ArrayList<Note>();
        
        Note prevNote = notes.remove(0);
        
        notesToAdd.add(prevNote);
        possibleDirectionSwitch(prevNote);
        for(Note n: notes){
            
            //find next chord tone, making same notes have really bad scores
            Note noteToAdd = nextNote(prevNote, c, DISALLOW_SAME, CHORD_TONE);
            
            //If the note is still the same, find the "best" color tone
            if(noteToAdd.getPitch()==prevNote.getPitch()){
                //recalculate noteToAdd, allowing color tones
                noteToAdd = nextNote(prevNote, c, DISALLOW_SAME, COLOR_TONE);
            }
            
            //set note's rhythm value
            noteToAdd.setRhythmValue(n.getRhythmValue());
            //add note, possibly switching direction of line
            notesToAdd.add(noteToAdd);
            possibleDirectionSwitch(noteToAdd);
            //store note as prevNote
            prevNote = noteToAdd;
        }
        
        return notesToAdd;
    }
    
    /**
     * Returns last Note in ArrayList of Notes
     * @param notes ArrayList of Notes
     * @return last note, or null if list is empty
     */
    private Note getLast(ArrayList<Note> notes){
        int size = notes.size();
        if(size>0){
            return notes.get(size-1);
        }else{
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
     * Used by makeGuideLine() to generate a list of notes to be used in
     * a single guide tone line
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
                noteToAdd = nextNote(prevNote, currentChord, SAME_OKAY, CHORD_TONE);
            }
            if(durationSpecified && greaterThan(noteToAdd, maxDuration)){
                
                ArrayList<Note> notesToAdd = notesToAdd(noteToAdd, currentChord);
                for(Note n: notesToAdd){
                    guideLine.add(n);
                }
                prevNote = getLast(notesToAdd);
            }else{
                guideLine.add(noteToAdd);
                possibleDirectionSwitch(noteToAdd);
                prevNote = noteToAdd;
            }
            
            index+=duration;
        }
        
        return guideLine;
    }
    
    /**
     * Executes a possible direction switch if note being added is out of range
     * @param n Note to be tested
     */
    public void possibleDirectionSwitch(Note n){
        int inRange = inRange(n.getPitch(), lowLimit, highLimit);
        if(inRange!=IN_RANGE){
            if(inRange==BELOW_RANGE){
                direction = ASCENDING;
            }else if(inRange==ABOVE_RANGE){
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
        int toreturn = IN_RANGE;
        if(n>=high){
            toreturn = ABOVE_RANGE;
        }else if(n<=low){
            toreturn = BELOW_RANGE;
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
                firstNoteToAdd = nextNote(prevFirstNote, currentChord, SAME_OKAY, CHORD_TONE);
                firstNoteToAdd.setRhythmValue(currentChord.getRhythmValue()/2);
                
                secondNoteToAdd = nextNote(prevSecondNote, currentChord, SAME_OKAY, CHORD_TONE);
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
     * returns root of chord if startDegree = "mix" or
     * if the user-specified degree is not in the chord's associated scale
     * returns a rest if the chord isNOCHORD
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
    private Note nextNote(Note n, Chord c, boolean disallowSame, int chordOrColor)
    { 
        if(n.isRest()){
            return firstNote(c);
        }else{
            ArrayList<Note> possibleNotes = possibleNotes(n,c, chordOrColor);
            return bestNote(n, c, possibleNotes, disallowSame); 
        }
        
    }
    
    /**
     * Uses scoring to determine the note that will fit best with the previous
     * note and the current chord
     * @param n The previous note in the guide tone line
     * @param c The next chord in the progression
     * @param possibleNotes The notes in the chord that are available to 
     * travel to
     * @return The note with the best score to fit in the guide tone line
     */
    private Note bestNote(Note n, Chord c, ArrayList<Note> possibleNotes, boolean disallowSame)
    {
        if(possibleNotes.isEmpty()){
            //SHOULD DEFINITELY CHANGE
            //currently choosing the note that would be the first note
            return firstNote(c);
        }
        else{
            
            Note bestNote = possibleNotes.get(0);
            
            //initialize minScore
            double minScore = score(n, c, possibleNotes.get(0), disallowSame);
            
            for(Note currNote : possibleNotes){
                double currScore = score(n, c, currNote, disallowSame);
                
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
    private double score(Note prev, Chord c, Note note, boolean disallowSame){
        
        //if note is the same as the previous note and disallowSame, 
        //return really high (bad) score
        if(prev.getPitch()==note.getPitch()&&disallowSame){
            return Double.MAX_VALUE;
        }
        
        //initialize score to 0
        double score = 0;
        
        //color
        score+=colorScore(c, note);
        
        //distance
        score+=distanceScore(prev, note);
        
        //final score
        return score;
    }
   
    /**
     * Scores a note based on how colorful it is in the chord
     * @param c the chord
     * @param note the note
     * @return a score - low for colorful, high for not
     */
    private double colorScore(Chord c, Note note){
        Polylist pl = NoteConverter.noteToRelativePitch(note, c);
        String degree = pl.second().toString();
        int score;
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
        return score;
    }
    
    /**
     * Scores a note based on how close it is to the previous note
     * @param prev the previous note
     * @param note the current note
     * @return low score for good, high score for bad
     */
    private double distanceScore(Note prev, Note note){
        
        int distance = directionalDist(prev, note);
        int index = distance+2;//index to be used in scores array
        int score = 0;
        
        if(direction==DESCENDING){
            score+=scores[0][index];
        }
        else if(direction==NOPREFERENCE){
            score+=scores[1][index];
        }else if(direction==ASCENDING){
            score+=scores[2][index];
        }
        
        return score;
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
    private ArrayList<Note> possibleNotes(Note n, Chord c, int chordOrColor)
    {
        return chordOrColorNotes(fiveNotes(n,c.getRhythmValue()), c, chordOrColor);
    }
    
    /**
     * From the five closest notes to another note, it returns a list of which 
     * ones are either chord tones or color tones in chord c
     * @param fiveNotes ArrayList of five different notes
     * @param c a chord
     * @param chordOrColor CHORD_TONE to choose chord tones,
     * COLOR_TONE to choose color tones
     * @return A list of which of the five notes are available
     */
    private ArrayList<Note> chordOrColorNotes(ArrayList<Note> fiveNotes, Chord c, int chordOrColor)
    {
        ArrayList<Note> notesInChord = new ArrayList<Note>();
        for(Note note: fiveNotes){
            if(belongsTo(note, c, chordOrColor)){
                notesInChord.add(note);
            }
        }
        return notesInChord;
    }
   
    /**
     * Determines whether a given note is a chord tone or color tone
     * @param n note
     * @param c chord
     * @param chordOrColor CHORD_TONE or COLOR_TONE
     * @return Boolean - true if n's type matches chordOrColor
     */
    private boolean belongsTo(Note n, Chord c, int chordOrColor)
    {
        if( c.getTypeIndex(n) == chordOrColor && !c.isNOCHORD()){
            return true;
        }
        else{
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
        int pitch = n.getPitch();
        if(pitch==REST){
            return fiveNextNotes;//return empty list if note is rest
        }
        for(int i=-2; i<=2; i++){
            fiveNextNotes.add(new Note(pitch+i, rhythmValue));
        }
        return fiveNextNotes;
    }
    
    //IMPORTANT: does not specify whether to represent note as sharp or flat
    //if there is ambiguity - I think it defaults to sharp...
    /**
     * Converts scale degree to note, using chord's rhythm value
     * IMPORTANT: does not specify whether to represent note as sharp or flat
     * @param degree
     * @param c
     * @return Note that is the specified degree of the chord
     */
    private Note scaleDegreeToNote(String degree, Chord c){
        int octave = direction==ASCENDING?0:1;
        if(mix && degree.equals(SEVEN)){
            octave = 0;
        }
        return NoteConverter.scaleDegreeToNote(degree, c, octave, c.getRhythmValue());
    }  
}
