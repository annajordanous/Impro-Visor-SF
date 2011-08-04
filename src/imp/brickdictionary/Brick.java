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

import imp.cykparser.PostProcessor;
import imp.util.ErrorLog;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ListIterator;
import polya.*;

/**
 * purpose: Brick definition
 * @author Zachary Merritt
 */
public class Brick extends Block {
    
    
    private ArrayList<Block> subBlocks; // Components of a Brick
    private String type;                // The class of Brick (e.g. "Cadence")
    private String qualifier = "";      // The qualifier of a Brick name, if it
                                        // shares a name with another Brick
    
    /** Brick / 7
     * Constructs a Brick based on a qualifier and a complete BrickLibrary
     * 
     * @param brickName, a String
     * @param brickQualifier, a String
     * @param brickKey, a long
     * @param brickType, a String
     * @param contents, a Polylist describing bricks and chords
     * @param bricks, a BrickLibrary
     * @param m, the mode (a String)
     */
     public Brick(String brickName, String brickQualifier, long brickKey, String brickType,
         Polylist contents, BrickLibrary bricks, String m) {
         super(brickName, brickKey, m);
         qualifier = brickQualifier;
         subBlocks = new ArrayList<Block>();
         this.addSubBlocks(contents, bricks);
         type = brickType;
         duration = this.getDuration();
         endValue = getSectionEnd();
     }
     
    /** Brick / 6
     * Constructs a Brick based on a complete BrickLibrary
     * 
     * @param brickName, a String
     * @param brickKey, a long
     * @param brickType, a String
     * @param contents, a Polylist describing bricks and chords
     * @param bricks, a BrickLibrary
     * @param m, the mode (a String)
     */
     public Brick(String brickName, long brickKey, String brickType,
         Polylist contents, BrickLibrary bricks, String m) {
         super(brickName, brickKey, m);
         subBlocks = new ArrayList<Block>();
         this.addSubBlocks(contents, bricks);
         type = brickType;
         duration = this.getDuration();
         endValue = getSectionEnd();
     }
     
    /** Brick / 8
     * Constructs a new Brick based on construction details + qualifier
     * mid-dictionary-creation
     * 
     * @param brickName, a String
     * @param brickQualifier, a String
     * @param brickKey, a long
     * @param brickType, a String
     * @param contents, a Polylist describing bricks and chords
     * @param bricks, a BrickLibrary
     * @param m, the mode (a String)
     * @param polymap, a LinkedHashMap<String, LinkedList<Polylist>> storing
      *                definitions of other Bricks
     */
     public Brick(String brickName, String brickQualifier, long brickKey, String brickType, 
             Polylist contents, BrickLibrary bricks, String m, 
             LinkedHashMap<String, LinkedList<Polylist>> polymap) {
         super(brickName, brickKey, m);
         qualifier = brickQualifier;
         subBlocks = new ArrayList<Block>();
         this.addSubBlocks(contents, bricks, polymap);
         type = brickType;
         duration = this.getDuration();
         endValue = getSectionEnd();
     }
     
    /** Brick / 7
     * Constructs a new Brick based on construction details + qualifier
     * mid-dictionary-creation
     * 
     * @param brickName, a String
     * @param brickKey, a long
     * @param brickType, a String
     * @param contents, a Polylist describing bricks and chords
     * @param bricks, a BrickLibrary
     * @param m, the mode (a String)
     * @param polymap, a LinkedHashMap<String, LinkedList<Polylist>> storing
      *                definitions of other Bricks
     */
     public Brick(String brickName, long brickKey, String brickType, 
             Polylist contents, BrickLibrary bricks, String m, 
             LinkedHashMap<String, LinkedList<Polylist>> polymap) {
         super(brickName, brickKey, m);
         subBlocks = new ArrayList<Block>();
         this.addSubBlocks(contents, bricks, polymap);
         type = brickType;
         duration = this.getDuration();
         endValue = getSectionEnd();
     }
    
    /** Brick / 6
     * Constructs a brick with predefined contents and a qualifier
     * 
     * @param brickName, a String
     * @param brickQualifier, a String
     * @param brickKey, a long
     * @param brickType, a String
     * @param contents, an ArrayList of component blocks
     * @param m, the mode as a String
     */
    public Brick(String brickName, String brickQualifier, long brickKey, String brickType, 
            ArrayList<Block> contents, String m) {
        super(brickName, brickKey, m);
        qualifier = brickQualifier;
        subBlocks = contents;
        type = brickType;
        duration = this.getDuration();
        endValue = getSectionEnd();
    }
    
    
    /** Brick / 5
     * Constructs a brick with predefined contents
     * 
     * @param brickName, a String
     * @param brickKey, a long
     * @param brickType, a String
     * @param contents, an ArrayList of component blocks
     * @param m, the mode as a String
     */
    public Brick(String brickName, long brickKey, String brickType, 
            ArrayList<Block> contents, String m) {
        super(brickName, brickKey, m);
        subBlocks = new ArrayList<Block>();
        for (Block b : contents)
        {
            if (b.getName().contains("Launcher"))
                subBlocks.addAll(b.flattenBlock());
            else
                subBlocks.add(b);
        }
        type = brickType;
        duration = this.getDuration();
        endValue = getSectionEnd();
    }

    /** Brick / 1
     * Copy constructor for a brick. Makes a deep copy.
     * 
     * @param brick, a Brick
     */
    public Brick(Brick brick) {
        super(brick.name, brick.getKey());
        qualifier = brick.getQualifier();
        subBlocks = new ArrayList<Block>();
        
        // Loop through all the subblocks, making copies of each
        ListIterator blockIter = brick.getSubBlocks().listIterator();
        while (blockIter.hasNext()) {
            Block block = (Block)blockIter.next();
            Block newBlock;
            if (block instanceof Brick) {
                newBlock = new Brick((Brick) block);
                
            }
            else {
                newBlock = new ChordBlock((ChordBlock) block);
            }
            subBlocks.add(newBlock); 
        }
        
        type = brick.getType();
        duration = this.getDuration();
        mode = brick.getMode();
        endValue = getSectionEnd();
    }
    
    /** Brick / 2
     * Makes a brick based only on a name and a list of subblocks
     * 
     * @param name, a String
     * @param brickList, subblocks for a brick
     */
    public Brick(String brickName, long brickKey, String type, List<Block> brickList) {
        super(brickName, brickKey, modeHelper(brickList, brickKey)); 
                                  //TODO account for not finding key
        this.type = type;
        subBlocks = new ArrayList<Block>();
        
        
        ListIterator blockIter = brickList.listIterator();
        while (blockIter.hasNext()) {
            Block block = (Block)blockIter.next();
            Block newBlock;
            if (block instanceof Brick) {
                newBlock = new Brick((Brick) block);
            }
            else {
                newBlock = new ChordBlock((ChordBlock) block);
            }
            duration += block.getDuration();
            subBlocks.add(newBlock); 
        }
        endValue = getSectionEnd();
    }
    
    /** Brick (Launcher constructor)
     * Creates a Launcher from a single chord
     * 
     * @param c : a ChordBlock
     * @param m : the new brick's mode
     */
    public Brick(ChordBlock c, String m) {
        super("Launcher");
        key = (c.getKey() + PostProcessor.DOM_ADJUST) % PostProcessor.OCTAVE;
        type = "Launcher";
        ArrayList<Block> singleton = new ArrayList<Block>();
        singleton.add(c);
        subBlocks = singleton;
        duration = c.getDuration();
        mode = m;
        endValue = c.getSectionEnd();
    }
  
    /** modeHelper
     * Used to determine the mode of a list of Blocks
     * @param brickList, a list of Blocks to be analyzed
     * @param key, a long describing the key
     * @return a String describing the mode
     */
    private static String modeHelper(List<Block> brickList, long key)
    {
        int ind = brickList.lastIndexOf(key);
        if( ind != -1)
            return brickList.get(ind).getMode();
        else
            return brickList.get(brickList.size()-1).getMode();
    }
       
    /** transpose / 1
     * Takes a brick and transposes all of its elements up by the difference
     * specified
     * 
     * @param diff, a long indicating semitones ascending difference
     */
    @Override
    public void transpose(long diff) {
        key = (key + diff + 12)%12;
        ListIterator iter = subBlocks.listIterator();
        while (iter.hasNext()){
            Block block = (Block)iter.next();
            block.transpose(diff);
        }
    }
    
    /** setName
     * Set name of a brick
     * @param s : String with which to replace brick's current name
     */
    public void setName(String s) {
        this.name = s;
    }
    
    /** setType
     * Set type of a brick
     * @param s : String with which to replace brick's current name
     */
    public void setType(String s) {
        this.type = s;
    }
            
    /** getSubBlocks
     * Gets all of the component blocks by reference
     * 
     * @return ArrayList<Block> of subblocks
     */ 
    @Override
    public ArrayList<Block> getSubBlocks() {
        return this.subBlocks;
    }
    
    /** isOverlap
     * Tells if a Brick includes an overlap
     * @return a boolean
     */
    @Override
    public boolean isOverlap() {
        if (this.getDuration() == 0)
            return true;
        return subBlocks.get(subBlocks.size() - 1).isOverlap();
    }
    
    /** addSubBlocks / 2
     * Constructs the subblocks of a brick by reading in a PolyList and using 
     * a BrickLibrary to convert it to bricks with appropriate subbricks.
     * 
     * @param contents, a PolyList of subbricks
     * @param bricks, a BrickLibrary
     */
    private void addSubBlocks(Polylist contents, BrickLibrary bricks) {
        
        List<Block> subBlockList = new ArrayList<Block>();
        
        while(contents.nonEmpty())
        {
            Object obj = contents.first();
            contents = contents.rest();
            if(obj instanceof Polylist)
            {
                Polylist pList = (Polylist)obj;
                String blockType = pList.first().toString();
                pList = pList.rest();
                
                // If a subblock is a brick, split it into components and then
                // look up the corresponding brick in the library to construct
                // the necessary new brick.
                if(blockType.equals("Brick"))
                {
                    String brickName = BrickLibrary.dashless(pList.first().toString());
                    pList = pList.rest();
                    String brickQualifier = "";
                    if (pList.first() instanceof Polylist) {
                        brickQualifier = ((Polylist)pList.first()).first().toString();
                        pList = pList.rest();
                    }
                    String brickKeyString = pList.first().toString();
                    pList = pList.rest();
                    Object durObj = pList.first();
                    pList = pList.rest();
                    if(durObj instanceof Long)
                    {
                        int dur = Arith.long2int((Long)durObj);
                        long brickKeyNum = 
                                BrickLibrary.keyNameToNum(brickKeyString);
                        Brick subBrick;
                        if (brickQualifier.equals("")) {
                            subBrick = bricks.getBrick(brickName, brickQualifier, 
                                                       brickKeyNum, dur);
                        }
                        else {
                            subBrick = bricks.getBrick(brickName, brickKeyNum, 
                                                       dur);
                        }
                        subBlockList.add(subBrick);
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.FATAL, brickName + ": " +
                                "Duration not of type long: " + durObj, true);
                    }
                }
                
                // If a subblock is a chord, make an appropriate Chord object
                else if(blockType.equals("Chord"))
                {
                    String chordName = pList.first().toString();
                    pList = pList.rest();
                    Object durObj = pList.first();
                    pList = pList.rest();
                    if(durObj instanceof Long)
                    {
                        int dur = Arith.long2int((Long)durObj);
                        ChordBlock subBlockChord = new ChordBlock(chordName, dur);
                        subBlockList.add(subBlockChord);
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.FATAL, chordName + ": " +
                                "Duration not of type long: " + durObj, true);
                    }
                }
            }
        }
        
        subBlocks.addAll(subBlockList);
    }
    
        /** addSubBlocks / 2
     * Constructs the subblocks of a brick by reading in a PolyList and using 
     * a BrickLibrary to convert it to bricks with appropriate subbricks.
     * 
     * @param contents, a PolyList of subbricks
     * @param bricks, a BrickLibrary
     */
    private void addSubBlocks(Polylist contents, BrickLibrary bricks, 
            LinkedHashMap<String, LinkedList<Polylist>> polymap) {
        
        List<Block> subBlockList = new ArrayList<Block>();
        
        while(contents.nonEmpty())
        {
            Object obj = contents.first();
            contents = contents.rest();
            if(obj instanceof Polylist)
            {
                Polylist pList = (Polylist)obj;
                String blockType = pList.first().toString();
                pList = pList.rest();
                
                // If a subblock is a brick, split it into components and then
                // look up the corresponding brick in the library to construct
                // the necessary new brick.
                if(blockType.equals("Brick"))
                {
                    String subBrickName = BrickLibrary.dashless(pList.first().toString());
                    pList = pList.rest();
                    
                    String subBrickQualifier = "";
                    if (pList.first() instanceof Polylist) {
                        subBrickQualifier = ((Polylist)pList.first()).first().toString();
                        pList = pList.rest();
                    }
                    
                    String subBrickKeyString = pList.first().toString();
                    pList = pList.rest();
                    Object durObj = pList.first();
                    pList = pList.rest();
                    if(durObj instanceof Long)
                    {
                        int dur = Arith.long2int((Long)durObj);
                        long subBrickKeyNum = 
                                BrickLibrary.keyNameToNum(subBrickKeyString);
                        Brick subBrick = null;
                        if (bricks.hasBrick(subBrickName)) {
                            if (!subBrickQualifier.equals(""))
                                subBrick = bricks.getBrick(subBrickName, 
                                                           subBrickQualifier,
                                                           subBrickKeyNum, dur);
                            
                            else
                                subBrick = bricks.getBrick(subBrickName, 
                                                           subBrickKeyNum, dur);
                        }
                        else if (polymap.containsKey(subBrickName)) {
                            LinkedList<Polylist> tokenList = polymap.get(subBrickName);
                            Polylist tokens = null;
                            if (subBrickQualifier.equals("")) {
                                tokens = tokenList.getFirst();
                            }
                            else {
                                for (Polylist p : tokenList) {
                                    Object qualifier = p.rest().rest().first();
                                    if (qualifier instanceof Polylist &&
                                        ((Polylist)qualifier).first().toString()
                                            .equals(subBrickQualifier)) {
                                        tokens = p;
                                        break;
                                    }
                                }
                                if (tokens.equals(null))
                                {
                                    ErrorLog.log(ErrorLog.SEVERE, 
                                            "Dictionary does not contain " +
                                            subBrickName + qualifier.toString());
                                }
                            }
                                
                            String brickName = BrickLibrary.dashless(subBrickName);
                            tokens = tokens.rest();
                            tokens = tokens.rest();
                            
                            String brickQualifier = "";
                            if (tokens.first() instanceof Polylist) {
                                brickQualifier = ((Polylist)tokens.first()).first().toString();
                                tokens = tokens.rest();
                            }
                            String brickMode = tokens.first().toString();
                            tokens = tokens.rest();
                            String brickType = tokens.first().toString();
                            tokens = tokens.rest();
                            String brickKeyString = tokens.first().toString();
                            tokens = tokens.rest();
                            long brickKeyNum = 
                                    BrickLibrary.keyNameToNum(brickKeyString);

                            subBrick = new Brick(brickName, brickQualifier, brickKeyNum,
                                brickType, tokens, bricks, brickMode, polymap);
                            subBrick.transpose(
                                    Arith.long2int(subBrickKeyNum - brickKeyNum));
                            subBrick.replaceDuration(dur);
                        }
                        else
                        {
                            ErrorLog.log(ErrorLog.FATAL, "Dictionary does "
                                    + "not contain " + subBrickName, true);
                        }

                        subBlockList.add(subBrick);
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.FATAL, subBrickName + ": " +
                                "Duration not of type long: " + durObj, true);
                    }
                }
                
                // If a subblock is a chord, make an appropriate Chord object
                else if(blockType.equals("Chord"))
                {
                    String chordName = pList.first().toString();
                    pList = pList.rest();
                    Object durObj = pList.first();
                    pList = pList.rest();
                    if(durObj instanceof Long)
                    {
                        int dur = Arith.long2int((Long)durObj);
                        ChordBlock subBlockChord = new ChordBlock(chordName, dur);
                        subBlockList.add(subBlockChord);
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.FATAL, chordName + ": " +
                                "Duration not of type long: " + durObj, true);
                    }
                }
            }
        }
        
        subBlocks.addAll(subBlockList);
    }
    
    /** getType
     * Return the type of Brick this is (e.g. "Cadence")
     * @return a String
     */
    @Override
    public String getType() {
        return this.type;
    }
    
    /** getDuration
     * Returns the duration after recalculating it.
     * @return an int describing the Brick's duration
     */
    @Override
    public final int getDuration() {
        setDuration(); //TODO not this.
        return duration;
    }
    
    /** setDuration
     * Sets the duration based upon the durations of the subblocks
     */
    private void setDuration() {
        int dur = 0;
        for(Block b : this.getSubBlocks())
        {
            dur += b.getDuration();
        }
        
        duration = dur;
    }
    
    /** getQualifier
     * Returns the qualifier of the Brick, or an empty String if it has none
     * @return a String
     */
    public String getQualifier() 
    {
        return this.qualifier;
    }
    
    /** flattenBlock
     * Returns this Brick as a list of ChordBlocks
     * @return an ArrayList<ChordBlock> describing the Brick's contents
     */
    @Override
    public ArrayList<ChordBlock> flattenBlock() {
        ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();
        
        ArrayList<Block> currentList = this.getSubBlocks();
        
        Iterator<Block> iter = currentList.iterator();
        
        // Iterate through subblocks. If a block is a chord, just add it. If it 
        // is a brick, recursively flatten.
        while(iter.hasNext()) {
            Block currentBlock = iter.next();

            chordList.addAll(currentBlock.flattenBlock());
        }
        
        if (chordList.size() > 0)
            chordList.get(chordList.size() - 1).setSectionEnd(endValue);
        
        return chordList;
    }
    
    /** scaleDuration
     * Scales the duration by recursing through the subblocks and scaling each
     * of them
     * @param scale, the int scale factor (positive for growth, negative for 
     *        shrinking)
     */
    @Override
    public void scaleDuration(int scale) {
        
        duration = 0;
        
        List<Block> currentSubBlocks = getSubBlocks();
        Iterator<Block> subBlockIter = currentSubBlocks.iterator();
        
        while(subBlockIter.hasNext()) {
            Block currentBlock = subBlockIter.next();
            currentBlock.scaleDuration(scale);
            duration += currentBlock.duration;
        }
        
    }
    
    /** replaceDuration
     * Changes the duration to be as close as possible to the newly specified
     * duration
     * @param newDuration, an int duration.
     */
    @Override
    public void replaceDuration(int newDuration) {
        float newDurFloat = newDuration;
        float ratio = (newDurFloat / this.getDuration());

        List<Block> currentSubBlocks = this.getSubBlocks();
        Iterator<Block> subBlockIter = currentSubBlocks.iterator();
        ArrayList<Block> adjustedSubBlocks = new ArrayList<Block>();
        
        while(subBlockIter.hasNext())
        {
            Block currentBlock = subBlockIter.next();
            if(currentBlock instanceof ChordBlock) {
                ((ChordBlock)currentBlock).changeChordDuration(ratio);
                adjustedSubBlocks.add(currentBlock);
            }
            else if (currentBlock instanceof Brick) {
                Brick adjustedSubBrick = (Brick)currentBlock;
                int newDur = 
                        Math.round(ratio * adjustedSubBrick.getDuration());
                adjustedSubBrick.replaceDuration(newDur);
                adjustedSubBlocks.add(adjustedSubBrick);
            }
        }
        
        this.subBlocks = adjustedSubBlocks;
        this.getDuration();
    }
    
    /** toString
     * Returns a String representation of the Brick
     * @return a String
     */
    @Override
    public String toString() {
        return name + " " + BrickLibrary.keyNumToName(key) + " " + duration;
    }
    
    /** printBrick
     * Prints a String representation of a Brick with its subblocks to the
     * error printstream
     */
    public void printBrick() {
        String brickName = this.getName();
        if (!this.getQualifier().isEmpty())
            brickName += "(" + this.getQualifier() + ")";
        String brickKey = BrickLibrary.keyNumToName(this.getKey());
        long brickDur = this.getDuration();
        String brickType = this.getType();
        System.err.println(brickName + " " + brickType + " " + brickKey 
                + " " + brickDur);
        
        ArrayList<Block> subBlockList = this.getSubBlocks();
        Iterator<Block> blockIter = subBlockList.iterator();
        
        while(blockIter.hasNext())
        {
            Block currentBlock = blockIter.next();
            
            if(currentBlock instanceof Brick)
            {
                Brick currentBrick = (Brick)currentBlock;
                String currentBrickName = currentBrick.getName();
                Long currentBrickKey = currentBrick.getKey();
                String currentBrickKeyString = 
                        BrickLibrary.keyNumToName(currentBrickKey);
                long dur = currentBrick.getDuration();
                System.err.println("\t" + currentBrickName + " " 
                        + currentBrickKeyString + " " + dur);
            }
            
            else if (currentBlock instanceof ChordBlock)
            {
                ChordBlock currentChord = (ChordBlock)currentBlock;
                String currentChordName = currentChord.getName();
                int currentDuration = currentChord.getDuration();
                System.err.println("\t" + currentChordName + " " 
                        + currentDuration);
            }
        }
    }
    
    /** reduceDurations
     * Reduces durations to lowest terms
     */
    public void reduceDurations() {
        scaleDuration(-getReductionFactor());
    }
    
    /** getReductionFactor
     * Finds the GCD (greatest common divisor) of all the durations
     * @return an int of the GCD
     */
    public int getReductionFactor()
    {
        ArrayList<ChordBlock> chords = flattenBlock();
        int currentGCD = subBlocks.get(0).duration;
        int currentDur;
        
        for( Iterator<ChordBlock> it = chords.iterator(); it.hasNext(); ) {
            currentDur = it.next().duration;
            System.out.print("("+currentGCD+","+currentDur+") = ");
            currentGCD = gcd(currentGCD, currentDur);
            System.out.println(currentGCD);
        }
        
        return currentGCD;
    }
    
    /** gcd
     * Returns the GCD of two numbers
     * @param a, the first number (an int)
     * @param b, the second number (an int)
     * @return the GCD (an int)
     */
    public static int gcd(int a, int b) {
        int r = a%b;
        
        if ( r == 0)
            return b;
                    
        return gcd(b, r);
    }
    
    /** setSectionEnd
     * Sets the type of section end to the appropriate int value (among NO_END,
     * SECTION_END and PHRASE_END)
     * @param value, one of the ints above
     */
    @Override
    public void setSectionEnd(int value) {
        endValue = value;
        if(this.isOverlap()) {
            subBlocks.get(subBlocks.size() - 2).setSectionEnd(value);
        }
        else
            subBlocks.get(subBlocks.size() - 1).setSectionEnd(value);
    }
    
    /** setSectionEnd
     * Sets the type of section end to either be no end or a section end
     * @param value, a boolean (true implies a section end)
     */
    @Override
    public void setSectionEnd(boolean value) {
        if(value)
            endValue = Block.SECTION_END;
        else
            endValue = Block.NO_END;
        if(this.isOverlap() && subBlocks.size() > 1) {
            subBlocks.get(subBlocks.size() - 2).setSectionEnd(value);
        }
        else
            subBlocks.get(subBlocks.size() - 1).setSectionEnd(value);
    }
    
    /** isSectionEnd
     * Returns whether or not the Brick marks the end of a phrase or section
     * @return a boolean
     */
    @Override
    public boolean isSectionEnd()
    {
        if(this.isOverlap() && subBlocks.size() > 1)
            return subBlocks.get(subBlocks.size() - 2).isSectionEnd();
        return subBlocks.get(subBlocks.size() - 1).isSectionEnd();
    }
    
    /** getSectionEnd
     * Returns an int describing what kind of section end this is, if any
     * @return an int matching NO_END, SECTION_END or PHRASE_END
     */
    @Override
    public int getSectionEnd()
    {
        if(this.isOverlap() && subBlocks.size() > 1)
            return subBlocks.get(subBlocks.size() - 2).getSectionEnd();
        return subBlocks.get(subBlocks.size() - 1).getSectionEnd();
    }

    /** isChord
     * Describes whether or not this object is a ChordBlock
     * @return a boolean
     */
    @Override
    public final boolean isChord()
    {
        return false;
    }
    
    /** isBrick
     * Describes whether or not this object is a Brick
     * @return a boolean
     */
    @Override
    public final boolean isBrick()
    {
        return true;
    }
    
    /** toPolylist
     * Returns a Polylist representation of a Brick.
     * @return a Polylist containing the Brick's contents
     */
    
    @Override
    public Polylist toPolylist()
    {
        return Polylist.list("Brick", dashed(name), 
                             BrickLibrary.keyNumToName(key), duration);
    }

    /** toBrickDefinition
     * Returns a Polylist formatted specifically to replicate the Brick's 
     * original definition format
     * @return a Polylist containing the Brick's definition information
     */
    public Polylist toBrickDefinition()
    {
        PolylistBuffer buffer = new PolylistBuffer();
        
        for ( Block b: getSubBlocks() )
        {
            buffer.append(b.toPolylist());
        }
        if (qualifier != "") {
            return Polylist.list("Def-Brick", dashed(name)+"("+qualifier+")", 
                    mode, dashed(type), BrickLibrary.keyNumToName(key)
                    ).append(buffer.toPolylist());
        }
        else return Polylist.list("Def-Brick", dashed(name), mode, dashed(type),
                    BrickLibrary.keyNumToName(key)).append(buffer.toPolylist());
    }
    
    public static String dashed(String s) {
        return s.replace(' ', '-');
    }
     // end of class Brick
}

/** Class BrickComparator
 * Allows direct lexicographic comparison of Bricks
 * @author ImproVisor
 */
class BrickComparator implements Comparator {
    @Override
    public int compare(Object b1, Object b2) {
        String name1 = ((Brick)b1).getName();
        String name2 = ((Brick)b2).getName();
        return name1.compareTo(name2);
    }
            
}