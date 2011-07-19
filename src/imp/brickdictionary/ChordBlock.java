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

import polya.Polylist;

/**
 * purpose: Definition definition
 * @author Zachary Merritt
 */


public class ChordBlock extends Block{
    
    // Type of chord, ie. "m7b5" or "7"
    private String quality;
    private long slashChord;
    public static final long NC = -1;
    
    // Constructor for chord
    // Uses parseChordName to interpret chord's name, finding root (key) and
    // quality
    public ChordBlock(String chordName, long duration) {
        super(chordName);
        this.duration = duration;
        this.parseChordName();
        isEnd = false;
    }
    
    public ChordBlock(String chordName, long duration, boolean sectionend) {
        super(chordName);
        this.duration = duration;
        this.parseChordName();
        isEnd = sectionend;
    }
    
    public ChordBlock(ChordBlock chord) {
        super(chord.name);
        this.duration = chord.duration;
        this.parseChordName();
        isEnd = chord.isEnd;
    }

    public ChordBlock(Chord chord) {
        super(chord.getName());
        duration = chord.getRhythmValue();
        this.parseChordName();
    }
    
 

    // Get duration of current chord
    @Override
    public Long getDuration() {
        return this.duration;
    }
    
    @Override
    public void adjustDuration(long scale) {
        if(scale > 0)
            duration = duration * scale;
        else
            duration = duration / -scale;
    }
    
    public void changeChordDuration(float ratio) {
        duration = Math.round(ratio * duration);
    }
    
    // Get current chord's quality (ie. "mM7")
    public String getQuality() {
        if (isSlashChord()) 
            return this.quality + "/" + BrickLibrary.keyNumToName(slashChord);
        return this.quality;
    }
    
    public String getSymbol() {
        if (isSlashChord()) 
            return this.quality + "/" + slashChord;
        return this.quality;
    }
    
    public boolean isSlashChord() {
        if (this.slashChord != this.key) return true;
        return false;
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
        this.slashChord = moduloSteps(this.slashChord + diff);
        this.name = BrickLibrary.keyNumToName(key) + this.getQuality();
    }
    
    public String transposeName(long diff) {
        transpose(diff);
        String tranName = this.name.intern();
        transpose(-1*diff);
        return tranName;
    }
    
    @Override
    public String toString() {
        return name + " " + duration;
    }
    
    
    public long matches(ChordBlock c) {
        if (c.getQuality().equals(quality) || 
                (c.isSlashChord() && 
                c.getQuality().split("/")[0].equals(quality)))
            return moduloSteps(c.getKey() - key );
        return NC;
    }
    
    // Extract chord's root (key) and quality from its name
    private void parseChordName() {
        String chordName = this.getName();
        if(chordName.startsWith("NC")) {
            this.key = -1;
            this.quality = "";
        }
        else if(chordName.length() > 1 && (chordName.charAt(1) == 'b'|| 
                chordName.charAt(1) == '#'))
        {
            String chordKeyString = chordName.substring(0, 2);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (!(chordName.equals(chordKeyString))) {
                this.quality = chordName.substring(2);
            }
            else
                this.quality = "";
        }
        else
        {
            String chordKeyString = chordName.substring(0, 1);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (chordName.length() > 1) {
                this.quality = chordName.substring(1);
            }
            else
                this.quality = "";
        }
        
        this.slashChord = this.key;
        String[] qualitySplit = this.quality.split("/", 2);
        if (qualitySplit.length > 1) {
            quality = qualitySplit[0];
            slashChord = BrickLibrary.keyNameToNum(qualitySplit[1]);
        }
        
        this.mode = this.findModeFromQuality();
            
    }
    
    public long moduloSteps(long l) {
        return (l + 12)%12;
}
    
/**
 * returns a Polylist representation of a ChordBlock
 * @return 
 */
    
public Polylist toPolylist()
  {
    return Polylist.list("chord", name, duration, quality, slashChord, NC, key, mode, isEnd);
  }

/** findModeFromQuality
     * Find mode of a block using quality of a chord
     * @param quality : String used to find mode
     * @return mode : String that determines overall tonicity of block
     */
    public String findModeFromQuality() {
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
   
}
