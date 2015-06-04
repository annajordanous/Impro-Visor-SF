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
    
    private final ChordPart chordPart;
    
    //range that guide tone line is restricted to
    //should change to whatever improvisor uses
    private final int lowLimit = Constants.F3;
    private final int highLimit = Constants.C6;
    
    public GuideLineGenerator(ChordPart inputChordPart) 
    {
        chordPart = inputChordPart;
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
        ArrayList<Chord> chords = chordPart.getChords();
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
        NoteSymbol ns = new NoteSymbol(pc, 1, c.getRhythmValue());
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
            int minScore = score(n, c, possibleNotes.get(0));
            for(Note currNote : possibleNotes){
                int currScore = score(n,c,currNote);
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
    private int score(Note prev, Chord c, Note note){
        Polylist pl = noteToRelativePitch(note, c);
        String degree = (String) pl.second();
        char accidental = degree.charAt(0);
        int score;
        
        //colorfulness
        if(accidental == 'b' || accidental == '#'){
            score = 2;
        }
        else if(degree.equals("3")||degree.equals("7")){
            score = 1;
        }
        else if(degree.equals("5")){
            score = 4;
        }
        else{
            score = 3;
        }
        
        //distance
        if(distance(prev,note)==0){
            score += 1;
        }
        else if(distance(prev,note)==1){
            score += 2;
        }else{
            score += 3;
        }
        return score;
    }
    
    //returns distance in semitones between two notes
    //order doesn't matter
    private int distance(Note n1, Note n2)
    {
        return Math.abs(n1.getPitch()-n2.getPitch());
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
    
}
