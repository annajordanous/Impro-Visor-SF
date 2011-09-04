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

import polya.Polylist;

/** Block
 * purpose: Describes a unit in a roadmap, either a ChordBlock or a Brick. 
 * Provides an inheritance structure for each.
 * @author Zachary Merritt
 */
public class Block {
    
    private static final String OVERLAP_STRING = " + ...";
    // Data members //
    protected String name;           // block's name
    protected int duration;          // how long block lasts (not in absolute 
                                     // units)
    protected long key;              // key is pitch-class relative to C, e.g.  
                                     // C=0, D=2, B=11, etc.
    protected String mode = null;    // Broad quality of block (e.g. Major, 
                                     // Minor, or Dominant)
    protected int endValue = NO_END; // a descriptor of what kind of end the
                                     // block might be, if any
    
    // Constants //
    public final static int NO_END = 0;      // int to set a block as not an end
    public final static int SECTION_END = 1; // int to set a block as a section
                                             // end in a leadsheet
    public final static int PHRASE_END = 2;  // int to set a block as a phrase
                                             // end in a leadsheet
    

    /** Block / 3
     * Fills in the name, key and mode information of a Block
     * @param blockname, the block's name or chord quality (a String)
     * @param blockkey, the key of the block (a long)
     * @param mode, the mode of the block (a String)
     */
    public Block(String blockname, long blockkey, String mode) {
        this.name = blockname;
        this.key = blockkey;
        this.mode = mode;
    }
    
    /** Block / 2
     * Fills in the name and key information of a Block
     * @param blockname, the block's name or chord quality (a String)
     * @param blockkey, the key of the block (a long)
     */
    public Block(String blockname, long blockkey) {
        this.name = blockname;
        this.key = blockkey;
    }
    
    /** Block / 1
     * Fills in the name of a block
     * @param blockname, the block's name or chord quality (a String)
     */
    public Block(String blockname) {
        this.name = blockname;
    }
    
    /** getKey
     * Get key of brick or root of chord as a long (0 is C, 1 is C#, etc.)
     * @return the key (a long)
     */
    public long getKey() {
        return this.key;
    }
    
    /** setKey
     * Set key of brick or root of ChordBlock
     * @param k : a long
     */
    public void setKey(long k) {
        this.key = k;
    }
    
    /** getKeyName
     * Get key of brick or root of chord as a String (e.g. "C")
     * @return the key (a String)
     */
    public String getKeyName() {
        return BrickLibrary.keyNumToName(this.key);
    }
    
    /** getName
     * Get block's name (ie. "Yardbird" or "Am7")
     * @return the name (a String)
     */
    public String getName() {
        String fullName = new String();
        fullName += name;
        // in the case of an overlap, we want an indication in the name
        if (this.isOverlap())
                fullName += OVERLAP_STRING;
        return fullName;
    }
    
    /** getDuration
     * Gets the duration of a block.
     * @return the duration (an int)
     */
    public int getDuration() {
        return this.duration;
    }
    
    /** isOverlap
     * Describes if the Block has an overlap chord in it. Overridden in Brick 
     * and ChordBlock.
     * @return a boolean
     */
    public boolean isOverlap() {
        return (this.getDuration() == 0);
    }
    
    
    /** getSubBlocks
     * Returns the subblocks comprising a Block. Overridden in Brick and
     * ChordBlock.
     * @return a List of Blocks
     */
    public List<Block> getSubBlocks() {
        return null;
    }
    
    /** getMode
     * Returns the mode of the block (e.g. "Major", "minor").
     * @return the mode (a String)
     */
    public String getMode() {
        return this.mode;
    }
    
    /** setMode
     * Sets the mode of the Block to the newly given mode.
     * @param s, a mode (as a String)
     */
    public void setMode(String s) {
        this.mode = s;
    }
    
    /** getType
     * Returns the type of the Block.
     * @return the type (a String)
     */
    public String getType() {
        return this.mode;
    }
    
    /** getSymbol
     * Gets the symbol for parsing a given block
     * @return the symbol (a String)
     */
    public String getSymbol() {
        return name;
    }
    
    /** transpose
     * Transposes a Block up a given number of steps.
     * @param diff, the difference in key by semitones (a long)
     */
    public void transpose(long diff) {
        if (key >= 0)
            key = (key + diff)%12;
    }
    /** flattenBlock
     * Returns the individual chords that constitute this Block
     * Overridden by the corresponding method in Brick or Chord
     * @return a List of ChordBlocks.
     */
    public ArrayList<ChordBlock> flattenBlock() {
        return null;
    }
    
    /** scaleDuration
     * Alters the duration of the total Block
     * Overridden by the corresponding method in Brick or Chord
     * @param factor, the factor by which the Block will be scaled (an int)
     */
    public void scaleDuration(int factor) {
        duration = duration * factor;
    }
    
    /** setDuration
     * Adjusts the Block's duration to a new value
     * @param newDur, the new duration of the brick
     */
    public void setDuration(int newDur) {
        duration = newDur;
    }
    
    /** isSectionEnd
     * Tells whether or not a given Block ends a section or phrase
     * @return a boolean
     */
    public boolean isSectionEnd() {
        return endValue != NO_END;
    }
    
    /** Returns true if the block is a phrase end.
     * @return 
     */
    public boolean isPhraseEnd() {
        return endValue == PHRASE_END;
    }
    
    /** getSectionEnd
     * Gets the kind of ending the Block has (NO_END, SECTION_END or PHRASE_END)
     * @return an int representing one of the above values
     */
    public int getSectionEnd() {
        return endValue;
    }
    
    /** setSectionEnd
     * Changes a Block to have the specified section end value
     * @param value, an int among NO_END, SECTION_END and PHRASE_END
     */
    public void setSectionEnd(int value) {
        endValue = value;
    }
    
    /** setSectionEnd
     * Changes a Block to be either a section end or no end
     * @param value, a boolean (true marks a section end)
     */
    public void setSectionEnd(boolean value) {
        if(value)
            endValue = SECTION_END;
        else
            endValue = NO_END;
    }
    
    /** setPhraseEnd
     * Changes a Block to be either a phrase end or no end
     * @param value, a boolean (true marks a phrase end)
     */
    public void setPhraseEnd(boolean value) {
        if(value)
            endValue = PHRASE_END;
        else
            endValue = NO_END;
    }
   
    /** isChord
     * Describes if a Block is a ChordBlock. Overridden in derived classes.
     * @return a boolean
     */
    public boolean isChord() 
    {
        return false;
    }
    
    /** isBrick
     * Describes if a Block is a Brick. Overridden in derived classes.
     * @return a boolean
     */
    public boolean isBrick()
    {
        return false;
    }
    
    /** setName
     * Changes the name of a Block
     * @param s, the new Block name (a string)
     */
    public void setName(String s) {
        this.name = s;
    }
    
    /** toPolylist
     * Describes a Block as a Polylist. Overridden in derived classes
     * @return 
     */

    public Polylist toPolylist()
      {
        return Polylist.list("block");
      }
    
    /** Returns the chord at the specified index within the brick.
     * @param index
     * @return 
     */
    public ChordBlock getChord(int index)
    {
        return flattenBlock().get(index);
    }
    
    public ChordBlock getFirstChord()
      {
        return flattenBlock().get(0);
      }
    
    public ChordBlock getLastChord()
      {
        List<ChordBlock> list = flattenBlock();
        return list.get(list.size()-1);
      }
    
    public String endValueString()
      {
        switch( endValue )
          {
            case NO_END: return "";
            case SECTION_END: return "Section End";
            case PHRASE_END: return "Phrase End";
          }
        return "";
      }
    
// end of class Block
}
