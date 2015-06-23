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
import static imp.Constants.OCTAVE;
import static imp.data.NoteSymbol.makeNoteSymbol;
import java.util.ArrayList;
import imp.lickgen.NoteConverter;
import polya.PolylistEnum;

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
    
    //lowest note on keyboard
    private final int A = 21;
    
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
    
    //each guide tone line should have a separate direction
    //this fixes range issues
    private int direction1;
    private int direction2;
    
    private final int HIGHER = 1;
    private final int LOWER = -1;
    private final int SAME = 0;
    
    //types of line - for two lines
    private final int THREE_SEVEN = 0;
    private final int SEVEN_THREE = 1;
    private final int FIVE_NINE = 2;
    private final int NINE_FIVE = 3;
    private final String startDegree1;
    private final String startDegree2;
    
    //Line identifiers so we know which direction to switch
    private final int ONLY_LINE = 0;
    private final int LINE_ONE = 1;
    private final int LINE_TWO = 2;
    
    //constants that correspond to the integers that represent direction
    private final int ASCENDING = 1;
    private final int DESCENDING = -1;
    private final int NOPREFERENCE = 0;
    private final int NOCHANGE = -2;
    
    //Scale degrees that are frequently used
    private final String THREE = "3";
    private final String SEVEN = "7";
    private final String FIVE = "5";
    private final String NINE = "2";
    private final String ELEVEN = "4";
    private final String THIRTEEN = "6";
    private final String ONE = "1";
    
    //Strings representing sharp and flat
    //private final String SHARP = "#";
    //private final String FLAT = "b";
    
    //Options
    private final Integer change = 1;
    private final Integer nochange = 0;

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
    
    private static final int SAME_NOTE = 0;
    private static final int HALF_STEP = 1;
    private static final int WHOLE_STEP = 2;
    private static final int MINOR_THIRD = 3;
    private static final int MAJOR_THIRD = 4;
    private static final int BIG_INTERVAL = 3;
    
    private final boolean allowColor;
    
    //a score for each of the 11 distances
    private static final int scores[] = {1, 1, 1, 2, 2, 3, 3, 3, 3, 3, 3, 3};
    
    //Array that contains scores. Low scores are good.
//    private static final int[][] scores = 
//    //  -2 -1  0  1  2          //Directional Distance
//    {   {1, 2, 3, 4, 5},        //DESCENDING
//        {5, 3, 1, 3, 5},        //NOPREFERENCE
//        {5, 4, 3, 2, 1}     };  //ASCENDING
//    
    //in all the below, no preference uses 5 3 1 3 5 instead of 3 2 1 2 3
    
    //One line, descending, quarter notes, low limit b-4, aint misbehavin:
    //scores1 (w>h>s) and scores4 (w>s>h) act similarly (BAD)
    //scores2 (h>w>s) and scores3 (s>h>w) and scores5 (h>s>w) act similarly
    //scores6 seems pretty unique (s>w>h) (BAD)
    
    //whole>half>same
    private static final int[][] scores1 = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {1, 2, 3, 4, 5},        //DESCENDING
        {5, 3, 1, 3, 5},        //NOPREFERENCE
        {5, 4, 3, 2, 1}     };  //ASCENDING
    
    //half>whole>same
    private static final int[][] scores2 = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {2, 1, 3, 4, 5},        //DESCENDING
        {5, 3, 1, 3, 5},        //NOPREFERENCE
        {5, 4, 3, 1, 2}     };  //ASCENDING
    
    //same>half>whole
    private static final int[][] scores3 = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {3, 2, 1, 4, 5},        //DESCENDING
        {5, 3, 1, 3, 5},        //NOPREFERENCE
        {5, 4, 1, 2, 3}     };  //ASCENDING
    
    //whole>same>half
    private static final int[][] scores4 = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {1, 3, 2, 4, 5},        //DESCENDING
        {5, 3, 1, 3, 5},        //NOPREFERENCE
        {5, 4, 2, 3, 1}     };  //ASCENDING
    
    //half>same>whole
    private static final int[][] scores5 = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {3, 1, 2, 4, 5},        //DESCENDING
        {5, 3, 1, 3, 5},        //NOPREFERENCE
        {5, 4, 2, 1, 3}     };  //ASCENDING
    
    //same>whole>half
    private static final int[][] scores6 = 
    //  -2 -1  0  1  2          //Directional Distance
    {   {2, 3, 1, 4, 5},        //DESCENDING
        {5, 3, 1, 3, 5},        //NOPREFERENCE
        {5, 4, 1, 3, 2}     };  //ASCENDING
    
    /**
     * Constructor
     * @param inputChordPart ChordPart from score
     * @param direction ASCENDING, DESCENDING, or NOPREFERENCE
     * @param startDegree a string 1 through 7, or "mix" for two lines
     * @param alternating true for /\/\, false for ////
     * @param lowLimit MIDI value of low limit
     * @param highLimit MIDI value of high limit
     * @param maxDuration maxDuration of any note, 0 or less if not specified
     * @param lineType the type of line (i.e. 3-7, 5-9, etc)
     */
    public GuideLineGenerator(ChordPart inputChordPart, int direction, String startDegree, boolean alternating, int lowLimit, int highLimit, int maxDuration, int lineType, boolean allowColor) 
    {
        chordPart = inputChordPart;
        this.originalDirection = direction;
        this.direction = direction;
        this.direction1 = direction;
        this.direction2 = direction;
        this.startDegree = startDegree;
        this.alternating = alternating;
        
        //pass in "mix" as the startDegree to signify two lines
        mix = startDegree.equals("mix");
       
        
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
        this.maxDuration = maxDuration;
        
        //pass in 0 or less to signify no duration specified
        durationSpecified = maxDuration>0;
        
        this.allowColor = allowColor;
        
        if(lineType==THREE_SEVEN){
            startDegree1 = THREE; startDegree2 = SEVEN;
        }else if(lineType==SEVEN_THREE){
            startDegree1 = SEVEN; startDegree2 = THREE;
        }else if(lineType==FIVE_NINE){
            startDegree1 = FIVE; startDegree2 = NINE;
        }else if(lineType==NINE_FIVE){
            startDegree1 = NINE; startDegree2 = FIVE;
        }else{
            startDegree1 = THREE; startDegree2 = SEVEN;
        }
    }
    
    /**
     * Returns an ArrayList of notes that are chord tones of the given chord
     * @param chord chord from which chord tones are to be extracted
     * @param duration duration that these notes are to have
     * @return ArrayList of chord tones - note: default pitches used
     */
    private ArrayList<Note> chordTones(Chord chord, int duration){
        PolylistEnum noteList = chord.getSpell().elements();
        ArrayList<Note> chordTones = new ArrayList<Note>();
        while(noteList.hasMoreElements()){
            Note note = ((NoteSymbol)noteList.nextElement()).toNote();
            note.setRhythmValue(duration);
            chordTones.add(note);
        }
        if(allowColor){
            PolylistEnum colorList = chord.getColor().elements();
            while(colorList.hasMoreElements()){
                Note note = ((NoteSymbol)colorList.nextElement()).toNote();
                note.setRhythmValue(duration);
                chordTones.add(note);
            }
        }
        return chordTones;
    }
    
    private static Note highestPriority(Chord chord, int duration){
        PolylistEnum priorityList = chord.getPriority().elements();
        Note highestPriority = ((NoteSymbol)priorityList.nextElement()).toNote();
        highestPriority.setRhythmValue(duration);
        return highestPriority;
    }
    
    /**
     * 
     * @param chord
     * @param prev
     * @param duration
     * @return 
     */
    private ArrayList<Note> closestChordTones(Chord chord, Note prev, int duration, int line){
        ArrayList<Note> chordTones = chordTones(chord, duration);
        ArrayList<Note> closestChordTones = new ArrayList<Note>();
        for(Note note : chordTones){
            note = getClosest(prev, note, line);
            closestChordTones.add(note);
        }
        return closestChordTones;
    }
    
    private int getMod(Note note){
        return (note.getPitch())%OCTAVE;
    }
    
    /**
     * Returns the note of the same pitch class as next that is closest in pitch to prev
     * In the case that prev and next's pitch classes are a tritone apart, chooses the note *above* prev
     * Retains the RhythmValue of next
     * @param prev the note that we're coming from
     * @param next a note that we're trying to change to be closer to prev
     * @return a note with the same pitch class and rhythm value as next that is closest to prev
     */
    private Note getClosest(Note prev, Note next, int line){
        
        int prevPitch = prev.getPitch();
        
        int prevMod = getMod(prev);
        int nextMod = getMod(next);
        int compareMods = compareTo(nextMod, prevMod);
        
        int dist1 = Math.abs(prevMod-nextMod);
        int dist2 = OCTAVE-dist1;
        int compareDists = compareTo(dist2, dist1);
        
        int pitch = prevPitch;
        //careful that pitch does not go out of range - or worse, into the negatives / past the end of the keyboard!
        if(compareDists == HIGHER){//dist2>dist1 - use dist1
            if(compareMods == HIGHER){//nextMod>prevMod
                pitch+=dist1;
            }else if(compareMods == LOWER){//prevMod>nextMod
                pitch-=dist1;
            }else{
                //leave pitch the same
            }
        }else if(compareDists == LOWER){//dist1>dist2 - use dist2
            if(compareMods == HIGHER){//nextMod>prevMod
                pitch-=dist2;
            }else if(compareMods == LOWER){//prevMod>nextMod
                pitch+=dist2;
            }else{
                //leave pitch the same
            }
        }else{
            //tritone - tiebreak, go up for now - fix
            int lineDirection = getDirection(line);
            if(lineDirection==ASCENDING){
                pitch+=dist1;
            }else if(lineDirection==DESCENDING){
                pitch-=dist1;
            }else{
                pitch+=dist1;//ARBITRARY TIE BREAK
            }
            
        }
        
        //temporary fix - shouldn't happen unless guide tone line goes way out of range
        if(pitch<0){
            pitch = prevPitch;
        }
        //makes range limits hard as opposed to soft
        int inRange = inRange(pitch);
        if(inRange!=IN_RANGE){
            if(inRange==ABOVE_RANGE){
                pitch-=OCTAVE;
            }else{
                pitch+=OCTAVE;
            }
        }
        return new Note(pitch, next.getRhythmValue());
    }
    
    private int compareTo(int a, int b){
        if(a>b){
            return HIGHER;
        }else if(b>a){
            return LOWER;
        }else{
            return SAME;
        }
    }
    
    private int score(Note prev, Note next, int line, boolean disallowSame){
        return directionScore(prev, next, line)
                + distanceScore(prev, next, disallowSame);//could be Integer.MAX_VALUE if disallowSame==true
        //addition won't cause overflow because if distanceScore is Integer.MAX_VALUE, directionScore will be 0
    }
    
    private Note bestNote(Note prev, ArrayList<Note> notes, Chord chord, int line, boolean disallowSame){
        Note bestNote = notes.get(0);
        int bestScore = score(prev, bestNote, line, disallowSame);
        for(Note note : notes){
            int score = score(prev, note, line, disallowSame);
            if(score<bestScore){
                bestScore = score;
                bestNote = note;
            }else if(score==bestScore){
                //use priority to tiebreak
                int compare = compareTo(priorityScore(note, chord), priorityScore(bestNote, chord));
                if(compare==LOWER){
                    bestNote = note;//only update best note if priority score of note is strictly less than priority score of current best. Two priority scores should never be equal.
                }
            }
        }
        return bestNote;
    }
    
    private Note nextNote(Note prev, Chord chord, int line, int duration, boolean disallowSame){
        if(chord.isNOCHORD()){
            return new Note(REST, Accidental.NOTHING, duration);
        }if(prev.isRest()){
            return firstNote(chord, getStartDegree(line), line, duration);
        }
        return bestNote(prev, closestChordTones(chord, prev, duration, line), chord, line, disallowSame);
    }
    
    private String getStartDegree(int line){
        if(line==ONLY_LINE){
            return startDegree;
        }else if(line==LINE_ONE){
            return startDegree1;
        }else if(line==LINE_TWO){
            return startDegree2;
        }else{
            //shouldn't happen
            return startDegree;
        }
    }
    
    /**
     * Returns 0 for correct direction, 1 for incorrect direction
     * @param prev Note we're coming from
     * @param next Note we're going to
     * @param line the line we're picking a note for - need its direction
     * @return 0 for correct direction or same note, 1 for incorrect direction.
     * In the case of no preference, returns 0 for anything.
     */
    private int directionScore(Note prev, Note next, int line){
        int lineDirection = getDirection(line);
        int compare = compareTo(next.getPitch(), prev.getPitch());
        if(lineDirection==ASCENDING){
            return compare>=SAME?0:1;
        }else if(lineDirection==DESCENDING){
            return compare<=SAME?0:1;
        }else{
            return 0;//direction doesn't matter
        }
    }
    
    /**
     * Returns 0 for same, 1 for half/whole step, 2 for minor/major 3rd, 3 for anything else
     * @param prev note we're coming from
     * @param next note we're going to
     * @return score based on distance - lower score means better/closer
     */
    private int distanceScore(Note prev, Note next, boolean disallowSame){
        int score;
        
        int dist = dist(prev, next);
        int lastIndex = scores.length-1;
        
        if(dist<=lastIndex&&dist>=0){//avoid array index out of bounds exception
            if(dist==SAME_NOTE){
                score = disallowSame?Integer.MAX_VALUE:scores[dist];//this score depends on disallowSame
            }else{
                score = scores[dist]; 
            }
        }else{//if exception, use last score in array
            score = scores[lastIndex];
        }
        return score;
    }
    
    private int priorityScore(Note next, Chord chord){
        PolylistEnum priorityList = chord.getPriority().elements();
        int priority = 0;
        PitchClass nextPc = makeNoteSymbol(next).getPitchClass();
        while(priorityList.hasMoreElements()){
            NoteSymbol ns = ((NoteSymbol)priorityList.nextElement());
            if(ns.getPitchClass().enharmonic(nextPc)){
                break;
            }
            priority++;
        }
        return priority;//TODO
    }
    
    private int dist(Note n1, Note n2){
        return dist(n1.getPitch(), n2.getPitch());
    }
    
    private int dist(int n1, int n2){
        return Math.abs(n1-n2);
    }
    
    /**
     * Gets the direction of a given line
     * @param line
     * @return 
     */
    private int getDirection(int line){
        if(line==ONLY_LINE){
            return direction;
        }else if(line==LINE_ONE){
            return direction1;
        }else if(line==LINE_TWO){
            return direction2;
        }else{
            return originalDirection;
        }
    }
    
//    private static ArrayList<Note> chordTones(Note prev, Chord chord, int duration){
//        PolylistEnum noteList = chord.getSpell().elements();
//        ArrayList<Note> chordTones = new ArrayList<Note>();
//        while(noteList.hasMoreElements()){
//            chordTones.add(((NoteSymbol)noteList.nextElement()).toNote());
//        }
//        return chordTones;
//    }
//    
    /**
     * tests whether a note duration is greater than the given duration
     * @param n1 Note whose duration is being tested
     * @param duration duration to be tested against
     * @return true if n1's duration is strictly greater than duration, false otherwise
     */
    private boolean greaterThan(Note n1, int duration){
        int noteDuration = n1.getRhythmValue();
        return noteDuration>duration;
    }
    
    private ArrayList<Integer> splitUp(int duration){
        int durationRemaining;
        ArrayList<Integer> durations = new ArrayList<Integer>();
        for(durationRemaining = duration; durationRemaining > maxDuration; durationRemaining -= maxDuration){
            durations.add(maxDuration);
        }
        durations.add(durationRemaining);
        return durations;
    }
    
    /**
     * Splits up a note (or rest) into a list of notes so that no note (or rest)
     * has a duration greater than maxDuration
     * all notes in the list have the same pitch value as the note passed in
     * @param n the note to be split up
     * @return list of notes or rests split up using maxDuration
     */
//    private ArrayList<Note> splitUpNote(Note n){
//
//        int pitch = n.getPitch();
//        ArrayList<Note> notes = new ArrayList<Note>();
//        int durationRemaining;
//        for(durationRemaining = n.getRhythmValue(); durationRemaining > maxDuration; durationRemaining -= maxDuration){
//            if(!(pitch==REST)){
//                notes.add(new Note(pitch, maxDuration));
//            }else{
//                notes.add(new Note(pitch, Accidental.NOTHING, maxDuration));
//            }
//           
//        }
//        if(!(pitch==REST)){
//           notes.add(new Note(pitch, durationRemaining)); 
//        }else{
//            notes.add(new Note(pitch, Accidental.NOTHING, durationRemaining));
//        }
//        
//        return notes;
//    }
//    
    private ArrayList<Note> notesToAdd(Note note, Chord chord, int line){
        ArrayList<Note> notesToAdd = new ArrayList<Note>();
        ArrayList<Integer> durations = splitUp(note.getRhythmValue());
        
        
        Note prevNote = new Note(note.getPitch(), durations.remove(0));
        notesToAdd.add(prevNote);
        
        for(int duration : durations){
            Note noteToAdd = nextNote(prevNote, chord, line, duration, DISALLOW_SAME);
            notesToAdd.add(noteToAdd);
            prevNote = noteToAdd;
            possibleDirectionSwitch(noteToAdd, line);
        }
        return notesToAdd;
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
//    private ArrayList<Note> notesToAdd(Note note, Chord c, int line){
//        
//        ArrayList<Note> notes = splitUpNote(note);
//        ArrayList<Note> notesToAdd = new ArrayList<Note>();
//        
//        Note prevNote = notes.remove(0);
//        
//        notesToAdd.add(prevNote);
//        possibleDirectionSwitch(prevNote, line);
//        for(Note n: notes){
//            
//            //find next chord tone, making same notes have really bad scores
//            Note noteToAdd = nextNote(prevNote, c, DISALLOW_SAME, CHORD_TONE, null, line);
//            
//            //If the note is still the same, find the "best" color tone
//            if(noteToAdd.getPitch()==prevNote.getPitch()){
//                //recalculate noteToAdd, allowing color tones
//                noteToAdd = nextNote(prevNote, c, DISALLOW_SAME, COLOR_TONE, null, line);
//            }
//            
//            //set note's rhythm value
//            noteToAdd.setRhythmValue(n.getRhythmValue());
//            //add note, possibly switching direction of line
//            notesToAdd.add(noteToAdd);
//            possibleDirectionSwitch(noteToAdd, line);
//            //store note as prevNote
//            prevNote = noteToAdd;
//        }
//        
//        //post-processing: if notesToAdd has a multiple of 4 notes in it
//        //make sure it doesn't do back-and-forth motion by correcting the 3rd note
//        //by disallowing its original pitch and the same note as the previous pitch
//        if(notesToAdd.size()>4){
//            for(int i = 0; i+4<notesToAdd.size(); i+=4){
//                //if first and third note of phrase are the same, you'll have back and forth motion because it's deterministic
//                if(notesToAdd.get(i).getPitch()==notesToAdd.get(i+2).getPitch()){
//                    
//                    //find next chord tone, making same notes have really bad scores AND its original pitch have a really bad score
//                    Note noteToAdd = nextNote(notesToAdd.get(i+1), c, DISALLOW_SAME, CHORD_TONE, notesToAdd.get(i+2), line);
//
//                    //If the note is still the same OR its original pitch, find the "best" color tone
//                    if(noteToAdd.getPitch()==notesToAdd.get(i+1).getPitch()||noteToAdd.getPitch()==notesToAdd.get(i+2).getPitch()){
//                        //recalculate noteToAdd, allowing color tones
//                        noteToAdd = nextNote(notesToAdd.get(i+1), c, DISALLOW_SAME, COLOR_TONE, notesToAdd.get(i+2), line);
//                    }
//                    
//                    //set note's rhythm value
//                    noteToAdd.setRhythmValue(notesToAdd.get(i+2).getRhythmValue());
//                    //remove bad note
//                    notesToAdd.remove(i+2);
//                    //add note to place where it was removed from
//                    notesToAdd.add(i+2, noteToAdd);
//                    //possibleDirectionSwitch(noteToAdd);
//                    
//                }
//            }
//        }
//        
//        return notesToAdd;
//    }
    
   
    
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
        //guideline, list of start indices of sections, list of chord durations
        ArrayList<Note> guideLine = new ArrayList<Note>();
        ArrayList<Integer> startIndices = chordPart.getSectionInfo().getSectionStartIndices();
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        //initialize index
        int index = startIndices.get(0);
        Chord currentChord = chordPart.getChord(index);
        Note prevNote = firstNote(currentChord, startDegree, ONLY_LINE, currentChord.getRhythmValue());
        
        //iterate through chords using their durations
        for(Integer duration : durations){
            
            currentChord = chordPart.getChord(index);
            Note noteToAdd;
            
            //chord is at start of section
            if(startIndices.contains(index)){
                //at the start of each section, restore direction
                //to the user's original intended direction
                direction = originalDirection;
                //call firstNote method (as oppossed to nextNote method)
                noteToAdd = firstNote(currentChord, startDegree, ONLY_LINE, currentChord.getRhythmValue());
            }
            //chord is not at start of section
            else {
                noteToAdd = nextNote(prevNote, currentChord, ONLY_LINE, currentChord.getRhythmValue(), SAME_OKAY);
            }
            
            //need to split up note before adding it
            boolean noteTooLong = durationSpecified && greaterThan(noteToAdd, maxDuration);
            if(noteTooLong && !noteToAdd.isRest()){
                
                ArrayList<Note> notesToAdd = notesToAdd(noteToAdd, currentChord, ONLY_LINE);
                for(Note n: notesToAdd){
                    guideLine.add(n);
                }
                prevNote = getLast(notesToAdd);
            //don't need to split up note before adding it
            }else{
                guideLine.add(noteToAdd);
                possibleDirectionSwitch(noteToAdd, ONLY_LINE);
                prevNote = noteToAdd;
            }
            //increment index by chord duration to get to the next chord
            index+=duration;
        }
        
        return guideLine;
    }
    
    /**
     * Executes a possible direction switch if note being added is out of range
     * @param n Note to be tested
     * @param line Line whose direction could be switched
     */
    public void possibleDirectionSwitch(Note n, int line){
        int newDirection = newDirection(n);
        
        if(newDirection!=NOCHANGE){
            if(line==ONLY_LINE){
                direction = newDirection;
            }else if(line==LINE_ONE){
                direction1 = newDirection;
            }else if(line==LINE_TWO){
                direction2 = newDirection;
            }
        }
    }
    
    private int newDirection(Note n){
        int newDirection;
        int pitch = n.getPitch();
        if(dist(pitch, lowLimit)<=HALF_STEP){
            newDirection = ASCENDING;
        }else if(dist(pitch, highLimit)<=HALF_STEP){
            newDirection = DESCENDING;
        }else{
            newDirection = NOCHANGE;
        }
        return newDirection;
    }
    
    /**
     * returns whether or not a number lies in a given range
     * @param n number to test
     * @param low low end of range
     * @param high high end of range
     * @return 1 if n is above the range, -1 if below, 0 if within
     */
    private int inRange(int n){
        int toreturn = IN_RANGE;
        if(n>highLimit){
            toreturn = ABOVE_RANGE;
        }else if(n<lowLimit){
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
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        int index = startIndices.get(0);
        Chord firstChord = chordPart.getChord(index);
        
        Note prevFirstNote = firstNote(firstChord, startDegree1, LINE_ONE, firstChord.getRhythmValue()/2);
        Note prevSecondNote = firstNote(firstChord, startDegree2, LINE_TWO, firstChord.getRhythmValue()/2);
        
        Note firstNoteToAdd;
        Note secondNoteToAdd;
        
        boolean threeFirst = true;

        for(Integer duration : durations){
            Chord currentChord = chordPart.getChord(index);
            
            if(startIndices.contains(index)){
                //sections always start on degree three
                threeFirst = true;
                //at the beginning of each section, restore directions to user's orignal choice
                direction1 = originalDirection;
                direction2 = originalDirection;
                
                //Set the two next notes to be half the length of the chord
                firstNoteToAdd = firstNote(currentChord, startDegree1, LINE_ONE, currentChord.getRhythmValue()/2);
                
                secondNoteToAdd = firstNote(currentChord, startDegree2, LINE_TWO, currentChord.getRhythmValue()/2);

            }
            else{
                firstNoteToAdd = nextNote(prevFirstNote, currentChord, LINE_ONE, currentChord.getRhythmValue()/2, SAME_OKAY);
                secondNoteToAdd = nextNote(prevSecondNote, currentChord, LINE_TWO, currentChord.getRhythmValue()/2, SAME_OKAY);
            }
            boolean firstNoteTooLong = greaterThan(firstNoteToAdd, maxDuration) && !firstNoteToAdd.isRest();
            boolean secondNoteTooLong = greaterThan(secondNoteToAdd, maxDuration) && !secondNoteToAdd.isRest();
            if(durationSpecified && (firstNoteTooLong || secondNoteTooLong)){
                if(threeFirst){
                    if(firstNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(firstNoteToAdd, currentChord, LINE_ONE);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevFirstNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(firstNoteToAdd);
                        possibleDirectionSwitch(firstNoteToAdd, LINE_ONE);
                        prevFirstNote = firstNoteToAdd;
                    }
                    
                    if(secondNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(secondNoteToAdd, currentChord, LINE_TWO);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevSecondNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(secondNoteToAdd);
                        possibleDirectionSwitch(secondNoteToAdd, LINE_TWO);
                        prevSecondNote = secondNoteToAdd;
                    }
                    
                }else{
                    if(secondNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(secondNoteToAdd, currentChord, LINE_TWO);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevSecondNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(secondNoteToAdd);
                        possibleDirectionSwitch(secondNoteToAdd, LINE_TWO);
                        prevSecondNote = secondNoteToAdd;
                    }
                    
                    if(firstNoteTooLong){
                        ArrayList<Note> notesToAdd = notesToAdd(firstNoteToAdd, currentChord, LINE_ONE);
                        for(Note n: notesToAdd){
                            guideLine.add(n);
                        }
                        prevFirstNote = getLast(notesToAdd);
                    }else{
                        guideLine.add(firstNoteToAdd);
                        possibleDirectionSwitch(firstNoteToAdd, LINE_ONE);
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
                possibleDirectionSwitch(firstNoteToAdd, LINE_ONE);
                possibleDirectionSwitch(secondNoteToAdd, LINE_TWO);
            }
            
            //if the lines are on the same note and in the same direction, alter their directions
            fixConvergingLines(prevFirstNote, prevSecondNote);
            
            //jump ahead to next chord
            index+=duration;
            //switch which line comes first if alternating
            if(alternating){
                threeFirst = !threeFirst;
            }
        }
        
        return guideLine;
    }

    private void fixConvergingLines(Note n1, Note n2){
        if(n1.getPitch()==n2.getPitch()&&direction1==direction2){
            direction1 = ASCENDING;
            direction2 = DESCENDING;
        }
    }
    
    /**
     * Creates the first note for the line to be based off of
     * @param c The first chord of the chord part
     * @return Note of the scale degree that is chosen by the user
     * returns root of chord if startDegree = "mix" or
     * if the user-specified degree is not in the chord's associated scale
     * returns a rest if the chord isNOCHORD
     */
    private Note firstNote(Chord chord, String start, int line, int duration){
        if(chord.isNOCHORD()){
            return new Note(REST, Accidental.NOTHING, duration);
        }
        Note first =  scaleDegreeToNote(start, chord, duration);
        //first = putInRange(first, c);
        first = closestToMiddle(first, chord, line);//uses duration of first
        return first;
    }
    
    /**
     * Generates the next note in the sequence
     * @param n The last note chosen for the array part
     * @param c The next chord in the chord part
     * @return The next note to be used in the guide tone line
     */
//    private Note nextNote(Note n, Chord c, boolean disallowSame, int chordOrColor, Note notThisOne, int line)
//    { 
//        if(c.isNOCHORD()){
//            return new Note(REST, Accidental.NOTHING, c.getRhythmValue());
//        }
//        if(n.isRest()){
//            return firstNote(c, startDegree, line);
//        }else{
//            ArrayList<Note> possibleNotes = possibleNotes(n,c, chordOrColor);
//            return bestNote(n, c, possibleNotes, disallowSame, notThisOne, line); 
//        }
//        
//    }
    
    /**
     * Uses scoring to determine the note that will fit best with the previous
     * note and the current chord
     * @param n The previous note in the guide tone line
     * @param c The next chord in the progression
     * @param possibleNotes The notes in the chord that are available to 
     * travel to
     * @return The note with the best score to fit in the guide tone line
     */
//    private Note bestNote(Note n, Chord c, ArrayList<Note> possibleNotes, boolean disallowSame, Note notThisOne, int line)
//    {
//        //if there are not possible notes
//        if(possibleNotes.isEmpty()){
//            //go through all the options, from best to worst
//            possibleNotes = possibleNotes(n, c, CHORD_TONE);
//            if(possibleNotes.isEmpty()){
//                possibleNotes = possibleNotes(n, c, COLOR_TONE);
//                if(possibleNotes.isEmpty()){
//                    possibleNotes = possibleNotes(n, c, APPROACH_TONE);
//                    if(possibleNotes.isEmpty()){
//                        return firstNote(c, startDegree, line, c.getRhythmValue());
//                    }else{
//                        return bestNote(n, c, possibleNotes, disallowSame, notThisOne, line);
//                    }
//                }else{
//                    return bestNote(n, c, possibleNotes, disallowSame, notThisOne, line);
//                }
//            }else{
//                return bestNote(n, c, possibleNotes, disallowSame, notThisOne, line);
//            }
//        }
//        else{
//            
//            Note bestNote = possibleNotes.remove(0);
//            
//            //initialize minScore
//            double minScore = score(n, c, bestNote, disallowSame, notThisOne, line);
//            
//            //possible notes iterated through in order of low to high pitch
//            for(Note currNote : possibleNotes){
//                double currScore = score(n, c, currNote, disallowSame, notThisOne, line);
//                
//                //IMPLICIT TIE BREAK, could be <=
//                //<= prefers higher notes, < prefers lower notes
//                if(currScore<=minScore){
//                    if(currScore==minScore){
//                        minScore = currScore;
//                        bestNote = currNote;
//                        /*
//                        System.out.println("Tie break: Coming from: "+n.getPitchClassName()+"; Going to: Old Best ("+bestNote.getPitchClassName()+") or New Best ("+currNote.getPitchClassName()+")\n");
//                        //tiebreak - favors higher notes
//                        if(tiebreak()){
//                            minScore = currScore;
//                            bestNote = currNote;
//                        }
//                        */
//                    }else{
//                        //no tie break neccessary - replace old minScore
//                        minScore = currScore;
//                        bestNote = currNote; 
//                    }
//                    
//                }
//            }
//            return bestNote;
//        }
//    }
    
//    private boolean tiebreak(){
//        ArrayList<Integer> options = new ArrayList<Integer>();
//        options.add(change);
//        options.add(nochange);
//        Collections.shuffle(options);
//        Integer choice = options.get(0);
//        return choice.equals(change);
//    }
    
    /**
     * Gives a score for a note based on its distance from the previous note
     * and which scale degree it is relative to the chord
     * @param prev the last note in the guide tone line
     * @param c the chord the note is chosen from
     * @param note the note being scored
     * @return The relative score of the note, lower means it is a better fit
     */
//    private double score(Note prev, Chord c, Note note, boolean disallowSame, Note notThisOne, int line){
//        
//        //if note is the same as the previous note and disallowSame, 
//        //return really high (bad) score
//        if(prev.getPitch()==note.getPitch()&&disallowSame){
//            return Double.MAX_VALUE;
//        }
//        if(notThisOne!=null&&note.getPitch()==notThisOne.getPitch()){
//            return Double.MAX_VALUE;
//        }
//        //initialize score to 0
//        double score = 0;
//        
//        //color
//        //score+=colorScore2(c, note);
//        
//        //distance
//        score+=distanceScore(prev, note, line);
//        
//        //final score
//        return score;
//    }
//   
    /**
     * Scores a note based on how colorful it is in the chord
     * @param c the chord
     * @param note the note
     * @return a score - low for colorful, high for not
     */
    /*
    private double colorScore(Chord c, Note note){
        Polylist pl = NoteConverter.noteToRelativePitch(note, c);
        String degree = pl.second().toString();
        int score;
        if(degree.contains(THREE)||degree.contains(SEVEN)){
            score = 0;
        }else if(degree.contains(FIVE)||degree.contains(NINE)){
            score = 1;
        }
        else if(degree.contains(FLAT) || degree.contains(SHARP)){
            score = 2;
        }
        else if(degree.equals(FIVE)){
            score = 4;
        }
        else{
            score = 3;
        }
        System.out.println("Degree: "+degree+"; Score: "+score);
        return score;
    }
    */
    
//    private double colorScore(Chord c, Note n){
//        Polylist pl = NoteConverter.noteToRelativePitch(n, c);
//        String degree = pl.second().toString();
//        double score;
//        
//        if(belongsTo(n, c, CHORD_TONE)){
//            if(degree.contains(THREE)||degree.contains(SEVEN)){
//                score = 1;
//            }else if(degree.contains(FIVE)||degree.contains(ONE)){
//                score = 3;
//            }else{
//                score = 5;
//            }
//        }else if(belongsTo(n, c, COLOR_TONE)){
//            if(degree.contains(SEVEN)||degree.contains(NINE)){
//                score = 1;
//            }else if(degree.contains(ELEVEN)||degree.contains(THIRTEEN)){
//                score = 3;
//            }else{
//                score = 5;
//            } 
//        }else if(belongsTo(n, c, APPROACH_TONE)){
//            score = 5;
//        }else{
//            score = 5;
//        }
//        return score;
//    }
//    
//    
    /**
     * Scores a note based on how close it is to the previous note
     * @param prev the previous note
     * @param note the current note
     * @return low score for good, high score for bad
     */
//    private double distanceScore(Note prev, Note note, int line){
//
//        int distance = directionalDist(prev, note);
//        int index1 = getDirection(line)+1;
//        int index2 = distance+2;//index to be used in scores array
//        int score = 0;
//        score+=scores[index1][index2];
//        //when no max duration is specified: scores 3, 5, 6 are the same, 1, 2, 4 are the same
//        return score;
//    }
//    
    /**
     * Returns the number of semitones note two is above note one
     * Positive indicates note two is above note one, negative means it is below
     * @param n1 note one
     * @param n2 note two
     * @return integer representing the directional distance between two notes
     */
//    private int directionalDist(Note n1, Note n2){
//        return n2.getPitch()-n1.getPitch();
//    }
//    
    /**
     * Given one note and the next chord, it returns the closest chord tones
     * to that note
     * @param n note
     * @param c chord
     * @return A list of the chord tones closest to the note
     */
//    private ArrayList<Note> possibleNotes(Note n, Chord c, int chordOrColor)
//    {
//        return chordOrColorNotes(fiveNotes(n,c.getRhythmValue()), c, chordOrColor);
//    }
//    
    /**
     * From the five closest notes to another note, it returns a list of which 
     * ones are either chord tones or color tones in chord c
     * @param fiveNotes ArrayList of five different notes
     * @param c a chord
     * @param chordOrColor CHORD_TONE to choose chord tones,
     * COLOR_TONE to choose color tones
     * @return A list of which of the five notes are available
     */
//    private ArrayList<Note> chordOrColorNotes(ArrayList<Note> fiveNotes, Chord c, int chordOrColor)
//    {
//        ArrayList<Note> notesInChord = new ArrayList<Note>();
//        for(Note note: fiveNotes){
//            if(belongsTo(note, c, chordOrColor)){
//                notesInChord.add(note);
//            }
//        }
//        return notesInChord;
//    }
//   
    /**
     * Determines whether a given note is a chord tone or color tone
     * @param n note
     * @param c chord
     * @param chordOrColor CHORD_TONE or COLOR_TONE
     * @return Boolean - true if n's type matches chordOrColor
     */
    private boolean belongsTo(Note n, Chord c, int chordOrColor)
    {
        return (c.getTypeIndex(n)==chordOrColor && !c.isNOCHORD());
    }
    
    /**
     * Puts a note in the user-specified range
     * @param n the note to adjust
     * @return the adjusted note
     * NOTE: this note's pitch will be unaltered if it was either already in the range,
     * OR if the user selected a range smaller than an octave so there is no instance of that note in the range
     */
//    private Note putInRange(Note n, Chord c){
//        int pitch = n.getPitch();
//        int rv = n.getRhythmValue();
//        int inRange = inRange(pitch, lowLimit, highLimit);
//        if(inRange!=IN_RANGE){
//            if(inRange==BELOW_RANGE){
//                for( ; (inRange != IN_RANGE) && (pitch <= highLimit); pitch+=OCTAVE, inRange = inRange(pitch, lowLimit, highLimit)){
//                    
//                }
//            }else if(inRange==ABOVE_RANGE){
//                for( ; (inRange != IN_RANGE) && (pitch >=lowLimit); pitch-=OCTAVE, inRange = inRange(pitch, lowLimit, highLimit)){
//                    
//                }
//            }
//            
//            //if the note is STILL not in range (no option available, range less than an octave):
//            if(inRange != IN_RANGE){
//                for(pitch = lowLimit, n = new Note(pitch); !belongsTo(n, c, CHORD_TONE)&&pitch<=highLimit; pitch++, n = new Note(pitch)){
//                    
//                }
//                if(belongsTo(n, c, CHORD_TONE)){
//                    //pitch is good
//                }else{
//                    for(pitch = lowLimit, n = new Note(pitch); !belongsTo(n, c, COLOR_TONE)&&pitch<=highLimit; pitch++, n = new Note(pitch)){
//                    
//                    }
//                    if(belongsTo(n, c, COLOR_TONE)){
//                        //pitch is good
//                    }else{
//                        for(pitch = lowLimit, n = new Note(pitch); !belongsTo(n, c, APPROACH_TONE)&&pitch<=highLimit; pitch++, n = new Note(pitch)){
//                    
//                        }
//                        if(belongsTo(n, c, APPROACH_TONE)){
//                            //pitch is good
//                        }else{
//                            pitch = lowLimit;//last resort
//                        }
//                    }
//                }
//            }
//            n = new Note(pitch, rv);
//        }
//        return n;
//    }
//    
    /**
     * Returns an ArrayList of notes that are within two half steps of the 
     * given note
     * @param n note
     * @param rhythmValue length of notes to be put in the list
     * @return ArrayList of notes
     */
//    private ArrayList<Note> fiveNotes(Note n, int rhythmValue)
//    {
//        ArrayList<Note> fiveNextNotes = new ArrayList<Note>();
//        int pitch = n.getPitch();
//        if(pitch==REST){
//            return fiveNextNotes;//return empty list if note is rest
//        }
//        for(int i=-2; i<=2; i++){
//            fiveNextNotes.add(new Note(pitch+i, rhythmValue));
//        }
//        return fiveNextNotes;
//    }
//    
    //IMPORTANT: does not specify whether to represent note as sharp or flat
    //if there is ambiguity - I think it defaults to sharp...
    /**
     * Converts scale degree to note, using chord's rhythm value
     * IMPORTANT: does not specify whether to represent note as sharp or flat
     * @param degree
     * @param c
     * @return Note that is the specified degree of the chord
     */
    private Note scaleDegreeToNote(String degree, Chord c, int duration){
        int octave = direction==ASCENDING?0:1;
        if(mix && degree.equals(SEVEN)){
            octave = 0;
        }
        return NoteConverter.scaleDegreeToNote(degree, c, octave, duration);
    }  

    private int middleOfRange(){
        return lowLimit+((highLimit-lowLimit)/2);//rounds down for odd numbers
    }
    
    private Note closestToMiddle(Note n, Chord c, int line){
        
        int rv = n.getRhythmValue();
        int lineDirection = getDirection(line);
        
        int closestBelow = closestBelowMiddle(n);
        boolean belowInRange = inRange(closestBelow)==IN_RANGE;
        int closestAbove = closestAboveMiddle(n);
        boolean aboveInRange = inRange(closestAbove)==IN_RANGE;
        
        int pitch;
        if(lineDirection == ASCENDING){
            if(belowInRange){
                pitch = closestBelow;
            }else if(aboveInRange){
                pitch = closestAbove;
            }else{
                pitch = lastResortPitch(c);
            }
        }else if(lineDirection == DESCENDING){
            if(aboveInRange){
                pitch = closestAbove;
            }else if(belowInRange){
                pitch = closestBelow;
            }else{
                pitch = lastResortPitch(c);
            }
        }else{
            if(belowInRange && aboveInRange){
                int middle = middleOfRange();
                //closest of the two
                pitch = ((middle-closestBelow)<(closestAbove-middle)?closestBelow:closestAbove);
            }else if(belowInRange){
                pitch = closestBelow;
            }else if(aboveInRange){
                pitch = closestAbove;
            }else{
                pitch = lastResortPitch(c);
            }
        }
        return new Note(pitch, rv);
    }
    
    private int lastResortPitch(Chord c){
        int pitch;
        Note n;
        for(pitch = lowLimit, n = new Note(pitch); !belongsTo(n, c, CHORD_TONE)&&pitch<=highLimit; pitch++, n = new Note(pitch)){
                    
        }
        if(belongsTo(n, c, CHORD_TONE)){
            //pitch is good
        }else{
            for(pitch = lowLimit, n = new Note(pitch); !belongsTo(n, c, COLOR_TONE)&&pitch<=highLimit; pitch++, n = new Note(pitch)){

            }
            if(belongsTo(n, c, COLOR_TONE)){
                //pitch is good
            }else{
                for(pitch = lowLimit, n = new Note(pitch); !belongsTo(n, c, APPROACH_TONE)&&pitch<=highLimit; pitch++, n = new Note(pitch)){

                }
                if(belongsTo(n, c, APPROACH_TONE)){
                    //pitch is good
                }else{
                    pitch = lowLimit;//last resort
                }
            }
        }
        return pitch;
    }
    
    private int closestBelowMiddle(Note n){
        int notePitch = n.getPitch();
        int middle = middleOfRange();
        int pitch;
        for(pitch = middle; !samePitchClass(pitch, notePitch); pitch--){
                
        }
        return pitch;
    }
    
    private int closestAboveMiddle(Note n){
        int notePitch = n.getPitch();
        int middle = middleOfRange();
        int pitch;
        for(pitch = middle; !samePitchClass(pitch, notePitch); pitch++){
                
        }
        return pitch;
    }
    
    //problems if you pass in a negative pitch...
    private boolean samePitchClass(int pitch1, int pitch2){
        return ((pitch1-A)%12) == ((pitch2-A)%12);
    }
    
}
