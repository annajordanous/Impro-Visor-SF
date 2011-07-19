/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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


package imp.brickdictionary;

import java.util.ArrayList;
import java.util.List;

import imp.data.Chord;
import polya.Arith;

import polya.Polylist;

/**
 * purpose: Definition definition
 * @author Zachary Merritt
 */


public class ChordBlock extends Block{
    
    // Type of chord, ie. "m7b5" or "7"
    private Chord chord;
    private long NC = -1;
    private boolean NO_SLASH = false;
    private boolean SLASH = true;
    
    // Constructor for chord
    // Uses parseChordName to interpret chord's name, finding root (key) and
    // quality
    public ChordBlock(String chordName, int duration) {
        super(chordName);
        this.duration = duration;
        chord = new Chord(chordName, duration);
        key = fixRoot(chord.getRoot());
        isEnd = false;
        mode = this.findModeFromQuality();
    }
    
    public ChordBlock(String chordName, int duration, boolean sectionend) {
        super(chordName);
        this.duration = duration;
        chord = new Chord(chordName, this.duration);
        key = fixRoot(chord.getRoot());
        isEnd = sectionend;
        mode = findModeFromQuality();
    }
    
    public ChordBlock(ChordBlock ch) {
        super(ch.name);
        this.duration = ch.duration;
        chord = new Chord(ch.name, this.duration);
        key = fixRoot(chord.getRoot());
        isEnd = ch.isSectionEnd();
        mode = findModeFromQuality();
        isEnd = ch.isEnd;
    }

    public ChordBlock(Chord ch) {
        super(ch.getName());
        duration = ch.getRhythmValue();
        chord = ch.copy();
        key = fixRoot(chord.getRoot());
        mode = findModeFromQuality();
    }
    
 

    // Get duration of current chord
    @Override
    public int getDuration() {
        return this.duration;
    }
    
    @Override
    public void adjustDuration(int scale) {
        if(scale > 0)
            duration = duration * scale;
        else
            duration = duration / -scale;
        chord.setRhythmValue(duration);
    }
    
    public void changeChordDuration(float ratio) {
        duration = Math.round(ratio * duration);
        chord.setRhythmValue(duration);
    }
    
    // Get current chord's quality (ie. "mM7")
    public String getQuality() {
        if (name.equals(Chord.NOCHORD))
            return name;
        return parseChordName(SLASH);
    }
    
    public String getSymbol() {
        if (name.equals(Chord.NOCHORD))
            return name;
        return parseChordName(NO_SLASH);
    }
    
    public boolean isSlashChord() {
        return chord.getChordSymbol().isSlashChord();
    }
    
    @Override
    public boolean isOverlap() {
        return (duration == 0);
    }
    
    @Override
    public List<Block> getSubBlocks() {
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        if (this.duration != 0)
            subBlocks.add(this);
        return subBlocks;
    }
    
    // Return a one member list of this chord
    // Overrides corresponding methid in Block
    @Override
    public List<ChordBlock> flattenBlock() {
        List<ChordBlock> chordList = new ArrayList<ChordBlock>();
        if (this.duration != 0)
            chordList.add(this);
        
        return chordList;
    }
    
    // Change chord's root (key) by diff
    @Override
    public void transpose(long diff) {
        this.key = moduloSteps(this.key + diff);
        this.chord.transpose(Arith.long2int(diff));
        this.name = chord.getName();
    }
    
    public String transposeName(int diff) {
        this.chord.transpose(diff);
        String tranName = this.chord.getName().intern();
        this.chord.transpose(-1*diff);
        return tranName;
    }
    
    @Override
    public String toString() {
        return name + " " + duration;
    }
    
    
    public long matches(ChordBlock c) {
        if (c.getQuality().equals(this.getQuality()) || 
                (c.isSlashChord() && 
                c.getQuality().split("/")[0].equals(this.getQuality())))
            return moduloSteps(c.getKey() - key );
        return NC;
    }
    
    // Extract chord's root (key) and quality from its name
    private String parseChordName(boolean slash) {
        String chordName = this.getName();
        String quality;

        if(chordName.length() > 1 && (chordName.charAt(1) == 'b'|| 
                chordName.charAt(1) == '#'))
        {
            String chordKeyString = chordName.substring(0, 2);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (!(chordName.equals(chordKeyString))) {
                quality = chordName.substring(2);
            }
            else
                quality = "";
        }
        else
        {
            String chordKeyString = chordName.substring(0, 1);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (chordName.length() > 1) {
                quality = chordName.substring(1);
            }
            else
                quality = "";
        }
        
        if (this.isSlashChord() && !slash) {
            String[] qualitySplit = quality.split("/");
            quality = qualitySplit[0];
        }
    
        return quality;
    }
    
    public long moduloSteps(long l) {
        return (l + 12)%12;
    }
    
/**
 * returns a Polylist representation of a ChordBlock
 * @return 
 */
    
    @Override
    public Polylist toPolylist()
    {
        return Polylist.list("chord", name, duration, getQuality(), isSlashChord(), 
                             NC, key, mode, isEnd);
    }

/** findModeFromQuality
     * Find mode of a block using quality of a chord
     * @param quality : String used to find mode
     * @return mode : String that determines overall tonicity of block
     */
    public final String findModeFromQuality() {
        String m;
        String q = this.getQuality();
        
        if(q.startsWith("M") || q.equals("") || q.startsWith("6"))
            m = "Major";
        else if(q.startsWith("7"))
            m = "Dominant";
        else
            m = "Minor";
        
        return m;
    }
    
    public static long fixRoot(String s) {
        s = s.replaceFirst(s.substring(0,1), s.substring(0, 1).toUpperCase());
        return BrickLibrary.keyNameToNum(s);
    }
   
}
