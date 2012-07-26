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
    private final static String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};

    
    // add support for other resolutions, possibly by "building up" from the smallest resolution
    // implement inversions
    // implement chord duration
    public ChordExtract() {

        //initialize a chordlist of all the chord names
        Polylist chordnames = ImproVisor.getChordNames();
        chordnames = chordnames.reverse();
        chordList = (String[]) chordnames.toStringArray();
        
        lengthOfChordList = chordList.length;
        
        //convert the listOfChords into a listOfBitChords
        bitChordList = new boolean[lengthOfChordList][12];
        for (int i = 0; i < lengthOfChordList; i++) {
            String chordName = chordList[i];
            //System.out.println(chordName);
            ChordSymbol chordsymbol = ChordSymbol.makeChordSymbol(chordName);
            ChordForm chordform = chordsymbol.getChordForm();
            Key key = chordform.getKey();
            boolean[] chordbit = chordform.getSpellVector(CROOT, key);
            bitChordList[i] = chordbit;
            
            if ("_".equals(chordName.substring(chordName.length()-1)))
            {
                //System.out.println(chordName);
                //System.out.println(chordName.substring(chordName.length()-1));
                //System.out.println(chordName.substring(0,chordName.length()-1));
                chordList[i]=chordName.substring(0,chordName.length()-1);
            }
            if (chordName.matches("(?i).*Major*."))
            {
                chordList[i] = chordName.replace("Major", "M");
            }
            if (chordName.matches("(?i).*minor*."))
            {
                chordList[i] = chordName.replace("minor", "m");
            }
            if (chordName.matches("(?i).*Dominant*."))
            {
                chordList[i] = chordName.replace("Dominant", "7");
            }
            if (chordName.matches("(?i).*augmented*."))
            {
                chordList[i] = chordName.replace("augmented", "+");
            }
            if (chordName.matches("(?i).*diminished*."))
            {
                chordList[i] = chordName.replace("diminished", "o");
            }
            if (chordName.matches("(?i).*half-diminished*."))
            {
                chordList[i] = chordName.replace("half-diminished", "m7b5");
            }
            
            //System.out.println(NoteSymbol.showContents(chordbit));
            //System.out.println(chordList[i]);
            //System.out.println(chordName);
            //System.out.println(chordform);
            //System.out.println(chordsymbol);
        }
    }
    
    /**
     * Matches a bit chord to the chord list
     * @param bitChord
     * @param transpose
     * @return 
     */
    private Chord matchBitChordToChord(boolean[] bitChord, int transpose) {

        //check if the bitChord is 1>
        if (checkChord(bitChord)) {
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
        }
        if (bitChord[FIFTH]==false)
        {
            bitChord[FIFTH]=true;
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
        }
        if (!checkChord(bitChord)) {

            Chord noChord = new Chord("NC");
            return noChord;
        }
        else
        {
            int root = transpose;
            int totalShift = 0;
            int numNotes = 0;
            for (int i = 0; i < 12; i++)
            {
                if (bitChord[i]==true)
                {
                    numNotes = numNotes+1;
                }
            }
            int count = numNotes - 1;
            while (count != 0) {
            int shift = 0;
            //get next
                for (int i = 1; i < 12; i++)
                {
                    if (bitChord[i]==true)
                    {
                        shift = i;
                        break;
                    }
                }
                totalShift = shift + totalShift;
                //shift to next
                shiftBitsLeft(bitChord, shift);
                //match chords
                    for (int j = 0; j < lengthOfChordList; j++) {
                        //check simple pattern matching
                        if (Arrays.equals(bitChord, bitChordList[j])) {
                            String chordName = chordList[j];
                            //keeps track of which note is bass
                            //find bass location
                            Chord chord = new Chord(chordName);
                            chord.transpose((transpose+totalShift)%12);
                            String name = chord.getName();
                            name = name + "/" + notes[root];
                            chord.setName(name);
                            return chord;
                        }
                    }
                count = count - 1;
            }
        }
        //things to add: inversions, duration?
        Chord noChord = new Chord("NC");
        return noChord;
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
        return max;
    }
    
    /**
     * Checks if a bitChord is empty or not
     * @param bitChord
     * @return 
     */
    private boolean checkEmpty(boolean[] bitChord)
    {
        if (bitChord==null)
        {
            return true;
        }
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
     * Checks if a bitChord is chord or not
     * @param bitChord
     * @return 
     */
    private boolean checkChord(boolean[] bitChord)
    {
        int count = 0;
        if (bitChord==null)
        {
            return false;
        }
        for(int i=0; i<bitChord.length; i++)
        {
            if(bitChord[i]==true)
            {
                count++;
            }
        }
        if(count>1) return true;
        else return false;
    }
    
    /**
     * Normalizes the accompaniment to fix offbeat comping
     * @param bitChords
     * @param bass
     */
        private void normalizeComp (boolean[][] bitChords, int[] bass)
    {
        //improve it a lot!
        //normalizeComp deals with any offbeat comping, in this order:
        // quarter note after beat, eighth note after beat, eighth note before beat
        
        //!!currently set for an eigth note resolution
        int eighth = 1;
        int quarter = 2;
        
        int length = bitChords.length;
        
        int beat;
        //iterate through every quarter note
        for(int i = 0; i < length; i = i + quarter)
        {
            beat = i;
            //add conditions if needed:
            // bass and comp are empty?
            // bass is empty?
            
            
            //bass
            if(bass[beat]==-1)
            {
                //check forward movement of very first beat
                if(beat==0 && beat+eighth<length && bass[beat+eighth]!=-1)
                {
                    bass[beat] = bass[beat + eighth];
                    bass[beat + eighth] = -1;
                }
                //check if forward movement
                else if(beat+eighth < length && bass[beat+eighth]!=-1)
                {
                    bass[beat] = bass[beat + eighth];
                    bass[beat+eighth] = -1;
                }
                //check harmonic anticipation
                else if(beat>1 && bass[beat-eighth]!=-1)
                {
                    if(bass[beat-eighth]==bass[beat-quarter])
                    {
                        bass[beat] = bass[beat-eighth];
                        bass[beat-eighth] = -1;
                    }
                }
            }
            
            //comp is empty, but bass is not
            if(!checkChord(bitChords[beat]) && bass[beat]!=-1)
            {
                //check forward movement of very first beat
                if(beat==0 && beat+eighth<length && checkChord(bitChords[beat+eighth]))
                {
                    bitChords[beat] = bitChords[beat+eighth];
                    bitChords[beat+eighth] = null;
                }
                //check forward movement
                else if(beat+eighth<length && checkChord(bitChords[beat+eighth]))
                {
                    bitChords[beat] = bitChords[beat+eighth];
                    bitChords[beat+eighth] = null;
                }
                //check harmonic anticipation
                else if(beat>1 && checkChord(bitChords[beat-eighth]))
                {
                    if(!Arrays.equals(bitChords[beat-eighth], bitChords[beat-quarter]))
                    {
                        bitChords[beat] = bitChords[beat - eighth];
                        bitChords[beat-eighth] = null;
                    }
                }
                else if(beat+quarter<length && checkChord(bitChords[beat+quarter]))
                {
                    bitChords[beat] = bitChords[beat + quarter];
                }
            }
            

        }
        //quickfix
        
        for(int i = 0; i < length; i = i + quarter)
        {
            beat = i;
            if(beat%4==0 && !checkChord(bitChords[beat]) && bass[beat]!=-1)
            {
                if(beat==0 && beat+quarter<length && checkChord(bitChords[beat+quarter]))
                {
                    bitChords[beat] = bitChords[beat+quarter];
                }
                //check forward movement QUICKFIX
                else if (beat + quarter < length && checkChord(bitChords[beat + quarter]))
                {
                    bitChords[beat] = bitChords[beat + quarter];
                }
            }
        }
    }
    
 
    public ChordPart importChords(MelodyPart[] bassMelodyParts, MelodyPart[] chordMelodyParts, int chordResolution)
    {
        ChordPart chords = null;
        //combine basslines
        getBassline(bassMelodyParts);
        
        //combine bass and chordpart into one array...? not needed
        MelodyPart[] arrayMelodyParts = new MelodyPart[chordMelodyParts.length+bassMelodyParts.length];
        for(int i = 0; i < bassMelodyParts.length; i++)
        {
            arrayMelodyParts[i] = bassMelodyParts[i];
        }
        for (int i = bassMelodyParts.length; i < arrayMelodyParts.length; i++)
        {
            arrayMelodyParts[i] = chordMelodyParts[i-bassMelodyParts.length];
        }
        
        //normalize each melody part
        for(int j = 0; j < arrayMelodyParts.length; j++)
        {
            normalize(EIGHTH, arrayMelodyParts[j]);
        }
        chords = arrayMelodyPartsToChordPart(arrayMelodyParts, chordResolution);
        return chords;
    }
    
    private void getBassline(MelodyPart[] bassMelodyParts) {
        Note bassnote;
        Note currentnote;
        MelodyPart bassline = bassMelodyParts[0];
        MelodyPart currentBassTrack;
        if (bassMelodyParts.length > 1) {
            for (int i = 0; i < bassline.size; i = i + EIGHTH) {
                for (int j = 1; j < bassMelodyParts.length; j++) {
                    bassnote = bassline.getNote(i);
                    currentBassTrack = bassMelodyParts[j];
                    currentnote = currentBassTrack.getNote(i);
                    if (bassnote!=null && currentnote!=null
                            && bassnote.nonRest() && currentnote.nonRest()
                            && currentnote.getPitch() < bassnote.getPitch()) {
                        bassMelodyParts[0].setNote(i, currentnote);
                        bassMelodyParts[j].setNote(i, bassnote);
                    }
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
    private ChordPart arrayMelodyPartsToChordPart(MelodyPart[] arrayMelodyParts, int chordResolution) {


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
                    bass[chordCount] = bassAddress;
                    //System.out.println("Bass at " + i);
                    //System.out.println("Note is " + bassAddress);
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
        
        int chordCount;
        boolean prevBitChord[] = new boolean[12];
        boolean bitChord[];
        boolean shiftedBitChord[];
        int root;
        for (int i = 0; i < max; i = i + chordResolution) {
            //!!optimized for eighth note resolution... fix later?
            chordCount = 8 * (i / 480);
            
            root = bass[chordCount];
            bitChord = bitChords[chordCount];
            
            if (root !=-1 && checkChord(bitChord) && Arrays.equals(bitChord, prevBitChord))
            {
                //do nothing
            }
            else if (root != -1) {
                if (checkChord(bitChord)) {
                    for (int j = 0; j < 12; j++) {
                        prevBitChord[j] = bitChord[j];
                    }
                }
                bitChord[root] = true;
                shiftedBitChord = shiftBitsLeft(bitChord, root);
                Chord chord = matchBitChordToChord(shiftedBitChord, root);
                chordpart.setChord(i, chord);
            }
            
        }
        chordpart.fixDuplicateChords(chordpart, chordResolution);
        return chordpart;
    }

    private void normalize(int resolution, MelodyPart melodyPart) {
        for (int i = 0; i < melodyPart.size; i = i + resolution) {
            Note note = melodyPart.getNote(i);
            if (note == null && i == 0) {
                //do nothing
            }
            if (note == null) {
                Note prevNote = melodyPart.getNote(i - resolution);
                prevNote.setRhythmValue(resolution);
                melodyPart.setNote(i, prevNote);
            }
        }
    }
}