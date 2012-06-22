/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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
import imp.ImproVisor;
import java.util.Arrays;
import polya.Polylist;

/**
 * Extracts chords for a given array of MelodyParts from a MIDI input
 * @author research
 */
public class ChordExtract implements Constants{

    private String[] chordList;
    private int lengthOfChordList;
    private boolean[][] bitChordList;
    private int[] bass;
    private boolean[][] bitChords;
    private final static int FIFTH = 7;

    
    // add support for other resolutions, possibly by "building up" from the smallest resolution
    // implement inversions
    // implement chord duration
    public ChordExtract() {

        //initialize a chordlist of all the chord names
        Polylist chordnames = ImproVisor.getChordNames();
        chordList = (String[]) chordnames.toStringArray();
        
        lengthOfChordList = chordList.length;
        
        //convert the listOfChords into a listOfBitChords
        bitChordList = new boolean[lengthOfChordList][12];
        for (int i = 0; i < lengthOfChordList; i++) {
            String chordName = chordList[i];
            ChordSymbol chordsymbol = ChordSymbol.makeChordSymbol(chordName);
            ChordForm chordform = chordsymbol.getChordForm();
            Key key = chordform.getKey();
            boolean[] chordbit = chordform.getSpellVector(CROOT, key);
            bitChordList[i] = chordbit;
            //System.out.println(NoteSymbol.showContents(chordbit));
            //System.out.println();
        }
    }
    
    /**
     * Matches a bit chord to the chord list
     * @param bitChord
     * @param transpose
     * @return 
     */
    private Chord matchBitChordToChord(boolean[] bitChord, int transpose) {
        //check if the bitChord is empty
        if (checkEmpty(bitChord)) {
            Chord noChord = new Chord("NC");
            return noChord;
        }
        for (int i = 0; i < lengthOfChordList; i++) {
            //check simple pattern matching
            if (Arrays.equals(bitChord, bitChordList[i])) {
                String chordName = chordList[i];
                Chord chord = new Chord(chordName);
                //Chord chord = Chord.makeChord(symbol, duration);
                chord.transpose(transpose);
                return chord;
            }
            //System.out.println(NoteSymbol.showContents(bitChord));
            //System.out.println(transpose);
        }
        //check if missing fifth
        if (bitChord[FIFTH] == false) {
            bitChord[FIFTH] = true;
            for (int i = 0; i < lengthOfChordList; i++) {
                if (Arrays.equals(bitChord, bitChordList[i])) {
                    String chordName = chordList[i];
                    Chord chord = new Chord(chordName);
                    //Chord chord = Chord.makeChord(symbol, duration);
                    chord.transpose(transpose);
                    return chord;
                }
                //System.out.println(NoteSymbol.showContents(bitChord));
                //System.out.println(transpose);
            }
        }
        //things to add: inversions, duration?
        return null;
    }
    
    /**
     * Converts a Note into a number that corresponds to a 12-bit address
     * @param note
     * @return 
     */
    private int noteToBitAddress(Note note) {
        if(note==null)
        {
            return -1;
        }
        if(note.nonRest())
        {
            int num = note.getPitch();
            num = num % 12;
            return num;
        }
            return -1;
    }
    
    /**
     * Shifts an array of booleans by a certain number, used to transpose a chord
     * into root position
     * @param bitChord
     * @param shift
     * @return 
     */
    private boolean[] shiftBitsLeft(boolean[] bitChord, int shift)
    {
        int numShift = 0;
        while (numShift < shift)
        {
            boolean temp = bitChord[0];
            for (int i = 0; i < 11; i++)
            {
                bitChord[i] = bitChord[i+1];
            }
            bitChord[11] = temp;
            numShift++;
        }
        return bitChord;
    }

    /**
     * Finds the max of the array of MelodyParts
     * @param arrayMelodyParts
     * @return 
     */
    private int findMax(MelodyPart[] arrayMelodyParts)
    {
        int max = 0;
        for(int i = 0; i < arrayMelodyParts.length; i++){
            int length = arrayMelodyParts[i].getSize();
        
            if(max<length){
                max = length;
            }
        }
        //System.out.println(max);
        return max;
    }
    
    /**
     * Checks if a bitChord is empty or not
     * @param bitChord
     * @return 
     */
    private boolean checkEmpty(boolean[] bitChord)
    {
        for(int i=0; i<bitChord.length; i++)
        {
            if(bitChord[i]==true)
            {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Returns a union of two bit chords
     * @param first
     * @param second
     * @return 
     */
    private boolean[] union(boolean[] first, boolean[] second)
    {
        boolean[] combined = new boolean[12];
        for (int i = 0; i < first.length; i++)
        {
            if(first[i]==true||second[i]==true)
            {
                combined[i] = true;
            }
        }
        return combined;
    }
    
    /**
     * Normalizes the accompaniment to fix offbeat comping
     * @param bitChords
     * @param bass
     */
    private void normalizeComp (boolean[][] bitChords, int[] bass)
    {
        //normalizeComp deals with any offbeat comping, in this order:
        // quarter note after beat, eighth note after beat, eighth note before beat
        
        //!!currently set for an eigth note resolution
        int halfmeasure = 4;
        
        int length = bitChords.length;
        
        //iterate through every downbeat (1 & 3)
        for(int i = 0; i < length; i = i + halfmeasure)
        {
            int beat = i;
            int eighth = 1;
            int quarter = 2;
            
            //add conditions if needed:
            // bass and comp are empty?
            // bass is empty?
            
            //comp is empty, but bass is not
            if(checkEmpty(bitChords[beat]) && bass[beat]!=-1)
            {
                //check quarter note after beat
                if(beat+quarter<length && !checkEmpty(bitChords[beat+quarter]))
                {
                    bitChords[beat] = bitChords[beat+quarter];
                }
                //check eighth note after beat
                else if(beat+eighth<length && !checkEmpty(bitChords[beat+eighth]))
                {
                    bitChords[beat] = bitChords[beat+eighth];
                }
                //check eighth note before beat
                else if(beat!=0 && !checkEmpty(bitChords[beat-eighth]))
                {
                    bitChords[beat] = bitChords[beat-eighth];
                }
            }
        }
    }
    
    /**
     * Converts an array of MelodyParts to a ChordPart
     * @param arrayMelodyParts
     * @param chordResolution
     * @param noteResolution
     * @return 
     */
    public ChordPart arrayMelodyPartsToChordPart(MelodyPart[] arrayMelodyParts, int chordResolution, int noteResolution) {


        //find max length of arrayMelodyParts to avoid out of bounds error
        int max = findMax(arrayMelodyParts);

        //numChords is the number of chords that we want for the given resolution
        //!!currently at an eighth note resolution
        int numChords = max / EIGHTH;
        
        //initialize an array consisting of the 12-bit chord representation
        bitChords = new boolean[numChords + 1][12];
        //initialize an array containing the bass notes
        bass = new int[numChords + 1];

        //adds notes to the array of bitChords
        //!!currently at an eighth note resolution
        for (int j = 0; j < arrayMelodyParts.length; j++) {
            for (int i = 0; i < arrayMelodyParts[j].size(); i = i + EIGHTH) {
                int chordCount = i / EIGHTH;
                //bass MelodyPart:
                if (j == 0) {
                    Note note = arrayMelodyParts[j].getNote(i);
                    int bassAddress = noteToBitAddress(note);
                    if (bassAddress != -1) {
                    bass[chordCount] = bassAddress;
                    //System.out.println("Bass at " + i);
                    //System.out.println("Note is " + bassAddress);
                    }
                }
                //accompaniment MelodyParts:
                else {
                    Note note = arrayMelodyParts[j].getNote(i);
                    int noteAddress = noteToBitAddress(note);
                    if (noteAddress != -1) {
                        bitChords[chordCount][noteAddress] = true;
                        //System.out.println("Index at " + i);
                        //System.out.println("Note is " + noteAddress);
                    }
                }
            }
        }
        
        //fix any offbeat comping
        normalizeComp(bitChords, bass);

        //adds chords to chordpart
        ChordPart chordpart = new ChordPart(max);
        for (int i = 0; i < max; i = i + chordResolution) {
            //!!optimized for eighth note resolution... fix later!
            int chordCount = 4 * (i / chordResolution);
            boolean bitChord[];
            //System.out.println(NoteSymbol.showContents(bitChords[i/resolution]));
            
            int root = bass[chordCount];
            
            //check if bass is on the beat
            if (bass[chordCount] != -1) {
                
                //check if it is the first downbeat of measure
                if (chordCount % 8 == 0 && chordResolution * (chordCount + 4) / 4 < max) {
                    
                    //if first beat comp is equal to third beat comp
                    if (Arrays.equals(bitChords[chordCount], bitChords[chordCount + 4])) {
                        bitChord = bitChords[chordCount];
                        
                        //very crude arpeggiation fix: adds the 3rd downbeat bass to the chord
                        if (bass[chordCount + 4] != -1) {
                            bitChord[bass[chordCount + 4]] = true;
                        }
                        
                        //!!implement duration
                        //adds root to the chord
                        bitChord[root] = true;
                        boolean shiftedBitChord[] = shiftBitsLeft(bitChord, root);
                        Chord chord = matchBitChordToChord(shiftedBitChord, root);
                        chordpart.setChord(i, chord);
                        //System.out.println(i);
                        //System.out.println(chord.toString());
                        //System.out.println(NoteSymbol.showContents(bitChords[i/resolution]));
                        chordpart.setChord(i + chordResolution, chord);
                        i = i + chordResolution;

                    }
                    
                    //if first beat comp is not equal to second beat comp
                    else {
                        bitChord = bitChords[chordCount];
                        bitChord[root] = true;
                        boolean shiftedBitChord[] = shiftBitsLeft(bitChord, root);
                        Chord chord = matchBitChordToChord(shiftedBitChord, root);
                        chordpart.setChord(i, chord);
                        //System.out.println(i);
                        //System.out.println(chord.toString());
                        //System.out.println(NoteSymbol.showContents(bitChords[i/resolution]));
                    }
                }
                
                //if first beat comp is not equal to third beat comp
                else {
                    bitChord = bitChords[chordCount];
                    bitChord[root] = true;
                    boolean shiftedBitChord[] = shiftBitsLeft(bitChord, root);
                    Chord chord = matchBitChordToChord(shiftedBitChord, root);
                    chordpart.setChord(i, chord);
                    //System.out.println(i);
                    //System.out.println(chord.toString());
                    //System.out.println(NoteSymbol.showContents(bitChords[i/resolution]));
                }
            }
        }
        return chordpart;
    }
}

