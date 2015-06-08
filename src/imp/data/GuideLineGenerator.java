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
import static imp.lickgen.NoteConverter.noteToRelativePitch;
import java.util.Iterator;

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

public class GuideLineGenerator {
    
    //minor (i.e. melodic minor)
    private static final String[] minorScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "b7", "7"};
    //minor 7 (i.e. Dorian)
    private static final String[] minor7ScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "b6", "6", "7", "#7"};
    //major (i.e. Ionian)
    private static final String[] majorScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "5", "#5", "6", "b7", "7"};
    //dominant (i.e. Mixolydian)
    private static final String[] dominantScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "5", "#5", "6", "7", "#7"};
    //half diminished
    //note: 6 half steps above the root has to be b5--otherwise, what do you call a perfect 5? No, I think it should be 5.
    private static final String[] halfDimScaleDegrees = {"1", "2", "#2", "3", "#3", "4", "5", "#5", "6", "#6", "7", "#7"};
    //diminished
    //note: 8 half steps above root is a 6th because a fully diminished 7th is the same intervals as a major 6th
    //so also, nine half steps becomes a seventh
    private static final String[] dimScaleDegrees = {"1", "b2", "2", "3", "#3", "4", "b5", "5", "6", "7", "#7", "b8"};
    //augmented (e.g. major#5)
    //here, 7 half steps is called a b5 (because it's flat relative to the augmented fifth) even though the interval is a perfect fifth
    private static final String[] augScaleDegrees = {"1", "b2", "2", "b3", "3", "4", "#4", "b5", "5", "6", "b7", "7"};
    
    
    private final ChordPart chordPart;
    private final int direction;
    //range that guide tone line is restricted to
    //should change to whatever improvisor uses
    private final int lowLimit = Constants.F3;
    private final int highLimit = Constants.C6;
    
    private final int ASCENDING = 1;
    private final int DESCENDING = -1;
    private final int NOPREFERENCE = 0;
    
    
    
    public GuideLineGenerator(ChordPart inputChordPart, int direction) 
    {
        chordPart = inputChordPart;
        this.direction = direction;
    }
    
    //returns a MelodyPart that is a guide tone line based on the ChordPart
    public MelodyPart makeGuideLine()
    {
        ArrayList<Note> guideLine = guideLine();
        MelodyPart guideLineMelody = new MelodyPart();
        for(Note n : guideLine){
            guideLineMelody.addNote(n);
        }
        return guideLineMelody;
    }
    
    //returns and ArrayList of Notes that form a guide tone line
    private ArrayList<Note> guideLine ()
    {
        //old code - not buggy
        /*ArrayList<Chord> chords = chordPart.getChords();
        ArrayList<Note> guideLine = new ArrayList<Note>();
        
        Chord firstChord = chords.remove(0);
        Note first = firstNote(firstChord);
        guideLine.add(first);
                
        Note prevNote = first;
        for(Chord c : chords){
            Note noteToAdd = nextNote(prevNote,c);
            guideLine.add(noteToAdd);
            prevNote = noteToAdd;
        }
        
        return guideLine;*/
        
        
        //new code - buggy?
        ArrayList<Note> guideLine = new ArrayList<Note>();
        ArrayList<Integer> startIndices = chordPart.getSectionInfo().getSectionStartIndices();
        
        Integer chordIndex = startIndices.get(0);
        Chord firstChord = chordPart.getChord(chordIndex);
       
        
        ArrayList<Integer> durations = chordPart.getChordDurations();
        
        int index = startIndices.get(0);
        Note prevNote = firstNote(firstChord);
        
        //DEBUGGING
       /* System.out.println("Start Indices (generated using getSectionStartIndices):");
        for(Integer i: startIndices){
            System.out.println("Index: "+i+";\tisSectionStart: "+chordPart.getSectionInfo().isSectionStart(i)+";\tChord: "+chordPart.getChord(i).getName()+";\tMeasure:"+chordPart.getSectionInfo().slotIndexToMeasure(i));
        }
        System.out.println();
        System.out.println("Start Indices (generated using isSectionStart):");*/
        //END DEBUGGING
        
        for(Integer duration : durations){
            Chord currentChord = chordPart.getChord(index);
            
            //DEBUGGING
           if(startIndices.contains(new Integer(index))){
               System.out.println("Index: "+index+";\tisSectionStart: "+chordPart.getSectionInfo().isSectionStart(index)+";\tChord: "+currentChord.getName()+";\tMeasure:"+chordPart.getSectionInfo().slotIndexToMeasure(index));
           }
            
            //END DEBUGGING
            //System.out.println("currentChord = " + currentChord);
            if(startIndices.contains(new Integer(index))){
                Note first = firstNote(currentChord);
                guideLine.add(first);
                prevNote = first;
            }
            else{
                Note noteToAdd = nextNote(prevNote, currentChord);
                guideLine.add(noteToAdd);
                prevNote = noteToAdd;
            }
            index+=duration;
        }
        return guideLine;
    }
    
    
    //Picks a note of the chord to be the seed for the guide tone line
    //currently, just chooses the 2nd note in the spelling, usually the 3rd
    //puts it in a reasonable range (currently 
    private Note firstNote(Chord c){
        Polylist noteList = c.getSpell();
        
        NoteSymbol third;
        if( noteList.length() < 2 ){
            third = (NoteSymbol) noteList.first();
        }
        else{
            third = (NoteSymbol) noteList.second();
        }
        
        PitchClass pc = third.getPitchClass();
        //Currently using octave 1, not sure if this should change or not
        int octave = direction==ASCENDING?0:1;
        NoteSymbol ns = new NoteSymbol(pc, octave, c.getRhythmValue());
        return ns.toNote();
    }
    
    //choose the best possible next note
    private Note nextNote(Note n, Chord c)
    { 
        ArrayList<Note> possibleNotes = possibleNotes(n,c);
        return bestNote(n, c, possibleNotes);
    }
    
    //return best note in list of possible notes
     //return best note in list of possible notes
    private Note bestNote(Note n, Chord c, ArrayList<Note> possibleNotes)
    {
        if(possibleNotes.isEmpty()){
            //SHOULD DEFINITELY CHANGE
            //currently choosing the third of the chord if there are no
            //possible notes
            return firstNote(c);
        }
        else{
            Note bestNote = possibleNotes.get(0);
            double minScore = score(n, c, possibleNotes.get(0));
            for(Note currNote : possibleNotes){
                double currScore = score(n,c,currNote);
                //IMPLICIT TIE BREAK, could be <=
                if(currScore<minScore){
                    minScore = currScore;
                    bestNote = currNote;
                }
            }
            return bestNote;
        }
    }
    /*
    private Note bestNote(Note n, Chord c, ArrayList<Note> possibleNotes)
    {
        if(possibleNotes.isEmpty()){
            //SHOULD DEFINITELY CHANGE
            //currently choosing the third of the chord if there are no
            //possible notes
            return firstNote(c);
        }
        else{
            Note bestNote = possibleNotes.get(0);
            int minDist = distance(n, possibleNotes.get(0));
            for(Note currNote : possibleNotes){
                int currDist = distance(n,currNote);
                //IMPLICIT TIE BREAK, could be <=
                if(currDist<minDist){
                    minDist = currDist;
                    bestNote = currNote;
                }
            }
            return bestNote;
        }
    }
    */
    
    //lowest score is the best
    private double score(Note prev, Chord c, Note note){
        Polylist pl = noteToRelativePitch(note, c);
        String degree = (String) pl.second();
        //char accidental = degree.charAt(0);
        double score;
        
        //colorfulness
        if(degree.contains("3")||degree.contains("7")){
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
    
    //returns distance in semitones between two notes
    //order doesn't matter
    private int distance(Note n1, Note n2)
    {
        return Math.abs(n1.getPitch()-n2.getPitch());
    }
    
    //returns distance in semitones between two notes
    //positive indicated ascending, negative descending
    //order matters - first note, second note
    private int directionalDist(Note n1, Note n2){
        return n2.getPitch()-n1.getPitch();
    }
    
    //returns arraylist of possible next notes given one note and next chord
    private ArrayList<Note> possibleNotes(Note n, Chord c)
    {
        return notesInChord(fiveNotes(n,c.getRhythmValue()),c);
    }
    
    //return only those notes which belong to the chord (see below)
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
    
    //return whether a note belongs to a chord
    //use method from other class??
    private boolean belongsTo(Note n, Chord c)
    {
        if( c.getTypeIndex(n) == Constants.CHORD_TONE ){
            return true;
        }
        else{
            return false;
        }
    }
    
    //returns an ArrayList of five notes
    //that are within 2 halfsteps of the note
    private ArrayList<Note> fiveNotes(Note n, int rhythmValue)
    {
        ArrayList<Note> fiveNextNotes = new ArrayList<Note>();
        for(int i=-2; i<=2; i++){
            fiveNextNotes.add(new Note(n.getPitch()+i, rhythmValue));
        }
        return fiveNextNotes;
    }
    
    private Note scaleDegreeToNote(String degree, Chord c){
        PitchClass rootPc = c.getRootPitchClass();
        int octave = direction==ASCENDING?0:1;
        NoteSymbol rns = new NoteSymbol(rootPc, octave, c.getRhythmValue());
        int rootMidi = rns.getMIDI();
        int semitonesAboveRoot = indexOf(degree,familyToArray(c.getFamily()));
        return new Note(rootMidi+semitonesAboveRoot, c.getRhythmValue());//need to specify sharp or flat - how?
    }
    
    //Should be in NoteConverter.java
    private String [] familyToArray(String family){
        if(family.equals("minor")){
            return minorScaleDegrees;
        }else if(family.equals("minor7")){
            return minor7ScaleDegrees;
        }else if(family.equals("major")){
            return majorScaleDegrees;
        }else if(family.equals("dominant")
                ||family.equals("sus4")
                ||family.equals("alt")){
            return dominantScaleDegrees;
        }else if(family.equals("half-diminished")){
            return halfDimScaleDegrees;
        }else if(family.equals("diminished")){
            return dimScaleDegrees;
        }else if(family.equals("augmented")){
            return augScaleDegrees;
        }else{
            return null;
        }
    }
    
    private int indexOf(String degree, String[]scale){
        int index = -1;
        for(int i=0; i<scale.length; i++){
            if(scale[i].equals(degree)){
                index = i;
            }
        }
        return index;
    }
    
}
