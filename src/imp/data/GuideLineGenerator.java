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
    
    //intervals
    private static final int SAME_NOTE = 0;
    private static final int HALF_STEP = 1;
    private static final int WHOLE_STEP = 2;
    private static final int MINOR_THIRD = 3;
    private static final int MAJOR_THIRD = 4;
    private static final int TRITONE = 5;
    
    private final boolean allowColor;
    
    //a score for each of the 6 possbile distances (same note through tritone)
    private static final int distanceScores[] = 
    //  same note   half step   whole step  minor 3rd   major 3rd   tritone
    {   1,          1,          1,          2,          2,          3};
    
    private static final int directionScores[][] = 
    //  down    same    up
    {   {0,     0,      1},         //DESCENDING
        {0,     0,      0},         //NOPREFERENCE
        {1,     0,      0}};        //ASCENDING
    
    private static final int colorScores[] =
    //  SEMITONES ABOVE ROOT
    //  0   1   2   3   4   5   6   7   8   9   10  11
    {   0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0};

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
    public GuideLineGenerator(ChordPart inputChordPart, int direction, String startDegree1, String startDegree2, boolean alternating, int lowLimit, int highLimit, int maxDuration, boolean mix, boolean allowColor) 
    {
        chordPart = inputChordPart;
        this.originalDirection = direction;
        this.direction = direction;
        this.direction1 = direction;
        this.direction2 = direction;
        this.startDegree = startDegree1;
        this.alternating = alternating;
        
        //pass in "mix" as the startDegree to signify two lines
        this.mix = mix;
       
        
        this.lowLimit = lowLimit;
        this.highLimit = highLimit;
        this.maxDuration = maxDuration;
        
        //pass in 0 or less to signify no duration specified
        durationSpecified = maxDuration>0;
        
        this.allowColor = allowColor;
        
        this.startDegree1 = startDegree1;
        this.startDegree2 = startDegree2;

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
        int index1 = getDirection(line)+1;
        int index2 = compareTo(next.getPitch(), prev.getPitch())+1;
        return directionScores[index1][index2];
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
        int lastIndex = distanceScores.length-1;
        
        if(dist<=lastIndex&&dist>=0){//avoid array index out of bounds exception
            if(dist==SAME_NOTE){
                score = disallowSame?Integer.MAX_VALUE:distanceScores[dist];//this score depends on disallowSame
            }else{
                score = distanceScores[dist]; 
            }
        }else{//if exception, use last score in array
            score = distanceScores[lastIndex];
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
        //returns the scale degree as a note in octave 0 or, if unavailable, the highest priority note in the chord
        Note first =  scaleDegreeToNote(start, chord, duration);
        
        //Check if it's a chord/color tone, change it to the highestPriority note if it's not
        if(!belongsTo(first, chord, CHORD_TONE)){
            if(allowColor){
                if(!belongsTo(first, chord, COLOR_TONE)){
                    first = NoteConverter.highestPriority(chord, duration);
                }
            }else{
                first = NoteConverter.highestPriority(chord, duration);
            }
            
        }
        
        //puts note close to middle
        first = closestToMiddle(first, chord, line);//uses duration of first
        return first;
    }

//    private boolean tiebreak(){
//        ArrayList<Integer> options = new ArrayList<Integer>();
//        options.add(change);
//        options.add(nochange);
//        Collections.shuffle(options);
//        Integer choice = options.get(0);
//        return choice.equals(change);
//    }


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
        return NoteConverter.scaleDegreeToNote(degree, c, 0, duration);
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
            }else{//above guaranteed to be in range because we limit the user to an octave
                pitch = closestAbove;
            }
        }else if(lineDirection == DESCENDING){
            if(aboveInRange){
                pitch = closestAbove;
            }else{//below guaranteed in range because we limit the user to an octave
                pitch = closestBelow;
            }
        }else{//NO PREFERENCE
            if(belowInRange && aboveInRange){
                int middle = middleOfRange();
                //closest of the two - tiebreak goes to above note if distances equal
                pitch = ((middle-closestBelow)<(closestAbove-middle)?closestBelow:closestAbove);
            }else if(belowInRange){
                pitch = closestBelow;
            }else{//above guaranteed to be in range because we limit the user to an octave
                pitch = closestAbove;
            }
        }
        return new Note(pitch, rv);
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
