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

package imp.cykparser;
import imp.brickdictionary.*;
import imp.roadmap.*;
import imp.util.ErrorLog;
import java.util.ArrayList;
import java.util.Arrays;
import polya.*;


/**
 * purpose: gain further information from list of blocks
 * @author Zachary Merritt
 */
public class PostProcessing {
    
    public static final int DOM_ADJUST = 5;
    public static final int OCTAVE = 12;
    public static final Integer[] DIM_INTERVALS = {11, 8, 5, 2, 1, 10, 7, 4};
    public static final String[] JOINS = {"Bootstrap", "Stella", "Backslider", 
        "Half Nelson", "Sidewinder", "New Horizon", "Downwinder", "Homer", 
        "Cherokee", "Woody", "Highjump", "Bauble"};
    public static final String DOGLEG = "Dogleg";
    private static final String[] MAJOR_MODES = {"major", "lydian"};
    private static final String[] MINOR_MODES = {"aeolian", "dorian", "phrygian"};
    private static final String[] DIMINISHED_MODES = {"locrian"};
    private static final String[] DOMINANT_MODES = {"mixolydian", "blues"};
    
     /** findKeys
     * Method groups consecutive block of same key for overarching key sections
     * @param roadmap : a RoadMap
     * @return newMap : an altered RoadMap
     */
    public static RoadMap findKeys(RoadMap roadmap) {
        ArrayList<KeySpan> keymap = new ArrayList<KeySpan>();
        
        // Initialize key, mode, and duration of current block
        KeySpan current = new KeySpan();
        ArrayList<Block> blocks = roadmap.getBlocks();
        
        if(blocks.isEmpty()) {
            roadmap.getKeyMap().clear();
            return roadmap;
        }
        
        Block[] blockArray = blocks.toArray(new Block[0]);
//        current = new KeySpan(blockArray[blockArray.length - 1].getKey(),
//                blockArray[blockArray.length - 1].getMode(),
//                blockArray[blockArray.length - 1].getDuration());
        
        current.setKey(blockArray[blockArray.length - 1].getKey());
        current.setMode(blockArray[blockArray.length - 1].getMode());
        current.setDuration(blockArray[blockArray.length - 1].getDuration());
            
        for(int i = blockArray.length - 2; i >= 0; i--) {
            if(blockArray[i].isSectionEnd()) {
                if(blockArray[i].getType().equals("Approach") || 
                        blockArray[i].getType().equals("Launcher")) {
                    ArrayList<ChordBlock> cFirstList = 
                            (ArrayList<ChordBlock>)blockArray[i].flattenBlock();
                    ChordBlock cFirst = cFirstList.get(cFirstList.size() - 1);
                    ArrayList<ChordBlock> cSecondList = 
                            (ArrayList<ChordBlock>)blockArray[i + 1].flattenBlock();
                    ChordBlock cSecond = cSecondList.get(0);
                    
//                    System.out.println("Going into doesResolve");
                    boolean dR = doesResolve(cFirst, cSecond);
//                    System.out.println(cFirst.toString() + " resolves to " + 
//                            cSecond.toString() + "?: " + dR);
                    if(dR) {
                        blockArray[i].setMode(cSecond.getMode());               //This is where that thing happens with launcher modes
                    }
                }
                
                KeySpan entry = current;
                keymap.add(0, entry);
                
                current = new KeySpan(blockArray[i].getKey(),
                            blockArray[i].getMode(), blockArray[i].getDuration());
                
            }
            else if(blockArray[i] instanceof Brick) {
                if(current.getKey() == blockArray[i].getKey() &&
                        current.getMode().equals(blockArray[i].getMode())) {
                    current.setDuration(current.getDuration() +
                            blockArray[i].getDuration());
                }
                else if(blockArray[i].getType().equals("Approach") || 
                        blockArray[i].getType().equals("Launcher")) {
                    ArrayList<ChordBlock> cFirstList = 
                            (ArrayList<ChordBlock>)blockArray[i].flattenBlock();
                    ChordBlock cFirst = cFirstList.get(cFirstList.size() - 1);
                    ArrayList<ChordBlock> cSecondList = 
                            (ArrayList<ChordBlock>)blockArray[i + 1].flattenBlock();
                    ChordBlock cSecond = cSecondList.get(0);
                    
//                    System.out.println("Going into doesResolve");
                    boolean dR = doesResolve(cFirst, cSecond);
//                    System.out.println(cFirst.toString() + " resolves to " + 
//                            cSecond.toString() + "?: " + dR);
                    if(dR) {
                        blockArray[i].setMode(cSecond.getMode());
                        current.setDuration(current.getDuration() +
                            blockArray[i].getDuration());
                    }
                    else {
                        KeySpan entry = current;
                        keymap.add(0, entry);

                        current = new KeySpan(blockArray[i].getKey(), 
                                blockArray[i].getMode(), 
                                blockArray[i].getDuration());
                    }
                }
                else {
                // End of current key -- add to the list
                    KeySpan entry = current;
                    keymap.add(0, entry);

                    current = new KeySpan(blockArray[i].getKey(),
                            blockArray[i].getMode(), blockArray[i].getDuration());
                }
            }
            else {
                ChordBlock c = (ChordBlock)blockArray[i];
//                KeyMode km = chordKeyFind(c, blockArray[i + 1]);
//                if(current.getKey() == km.getKey() && 
//                        current.getMode().equals(km.getMode())) {
//                if(blockArray[i+1].getType().equals("On-Off")) {
//                    System.out.println("If in ChordBlock - " + i);
//                    ChordBlock on = blockArray[i+1].flattenBlock().get(0);
//                    if(c.getKey() == on.getKey() &&
//                            c.getMode().equals(on.getMode())) {
//                        System.out.println("Chord " + c.getName() + " is on");
//                        // blockArray[i] = new Brick(c);
//                    }
//                    else {
//                        System.out.println("Chord " + c.getName() + " is off");
//                        // blockArray[i] = new Brick(c, current);
//                    }
//                }
                if(diatonicChordCheck(c, current.getKey(), current.getMode())) {
                    current.setDuration(current.getDuration() + c.getDuration());
//                    if(c.getKey() == current.getKey()) {
//                        // Consider this chord "on"
//                         blockArray[i] = new Brick(c);
//                    }
//                    else {
//                        // Consider this chord "off"
//                         blockArray[i] = new Brick(c, current);
//                    }
                }
                else if (c.isDiminished()) {
//                    ArrayList<ChordBlock> postList = (ArrayList<ChordBlock>)blockArray[i + 1].flattenBlock();
//                    ChordBlock post = postList.get(0);
//                    
//                    if(diminishedCheck(c, post)) {
//                        current.setDuration(current.getDuration() + c.getDuration());
//                    }
//                    else {
//                        // Start a new key
//                    KeySpan entry = current;
//                    keymap.add(0, entry);
//            
//                    current = new KeySpan(c.getKey(), c.getMode(), 
//                            c.getDuration());
//                    }
                    
                    current.setDuration(current.getDuration() + c.getDuration());
                }
                else {
                    // Start a new key
                    KeySpan entry = current;
                    keymap.add(0, entry);
            
                    current = new KeySpan(c.getKey(), c.getMode(), 
                            c.getDuration());
                }
            }
        }
        
        keymap.add(0, current);
        blocks = new ArrayList<Block>(Arrays.asList(blockArray));
        
        RoadMap newMap = new RoadMap(blocks);
        newMap.setKeyMap(keymap);
        
//        for(KeySpan ks : keymap) {
//            System.out.println(ks.toString());
//        }
        
        return newMap;
    }
    
    /** findLaunchers
     * Analyze ArrayList of Blocks to find approaches that could be launchers
     * @param blocks : ArrayList of blocks
     * @return alteredList : ArrayList of blocks adjusted for any launchers 
     *                       found
     */
    public static ArrayList<Block> findLaunchers(ArrayList<Block> blocks) {
        // Rebuilding original list, but with launchers in the appropriate 
        // places
        ArrayList<Block> alteredList = new ArrayList<Block>();
        
        for(int i = 0; i < blocks.size(); i++) {
            
            // If the current block is a brick, check if it could be a launcher
            if(blocks.get(i) instanceof Brick) {
                Brick b = (Brick)blocks.get(i);
                ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();
                
                // If the brick is not the last one in the list, get chords from
                // next block
                if(i != blocks.size() - 1) {
                    chordList = (ArrayList<ChordBlock>)blocks.get(i + 1).flattenBlock();
                }
                // Otherwise, loop around and get chords from first block
                else {
                    chordList = (ArrayList<ChordBlock>)blocks.get(0).flattenBlock();
                }
                    
                String brickName = b.getName();
                
                // Check if brick is an approach that resolves to next block
                if(b.getType().equals("Approach") && b.isSectionEnd() && 
                        doesResolve(b, chordList.get(0))) {
                    // If the name has "Approach", replace it with "Launcher"
                    if(brickName.contains("Approach")) 
                        brickName = brickName.replace("Approach", "Launcher");
                    // If not, append "-Launcher" to the end
                    else
                        brickName = brickName + " Launcher";
                    b.setName(brickName);
                    b.setType("Launcher");
                    
                    // Add altered brick to the list
                    alteredList.add(b);
                }
                // If brick is not an approach or does not resolve, add it to 
                // the list 
                else 
                    alteredList.add(b);
            }
            
            else if (((ChordBlock)blocks.get(i)).getSymbol().startsWith("7")) {
                ChordBlock b = (ChordBlock)blocks.get(i);
                Block postBlock;
                ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();
                
                // If the brick is not the last one in the list, get chords from
                // next block
                if(i != blocks.size() - 1)  {
                    postBlock = blocks.get(i + 1); 
                    chordList = (ArrayList<ChordBlock>)postBlock.flattenBlock();
                }
                // Otherwise, loop around and get chords from first block
                else {
                    postBlock = blocks.get(0); 
                    chordList = (ArrayList<ChordBlock>)postBlock.flattenBlock();
                }
                
                if(doesResolve(b, chordList.get(0)) && b.isSectionEnd()) {
                    Brick uniLauncher = new Brick(b, chordList.get(0).getMode()); // This is where that thing happens again
                    
                    alteredList.add(uniLauncher);
                }
                else
                    alteredList.add(b);
            }
                    
            // If the block is a non-dominant chord, add it to the list
            else
                alteredList.add(blocks.get(i));
        }
        
        return alteredList;
    }
    
     /** findJoins
     * A method that finds joins between bricks
     * @param blocks : ArrayList of Blocks (Chords or Bricks)
     * @return joinList : ArrayList of joins between blocks
     */
    public static ArrayList<String> findJoins(ArrayList<Block> blocks) {

        String[] joinArray = null;
        if(blocks.size() >= 1) {
            joinArray = new String[blocks.size() - 1];
        }
        else {
            //ErrorLog.log(ErrorLog.WARNING, "Cannot find joins in an ArrayList "
            //        + "of size 0");
            return null;
        }
        for(int i = 0; i < blocks.size() - 1; i++) {
            Block b = blocks.get(i);
            Block c = blocks.get(i + 1);
            // Check if current and next block are both bricks
            if (c instanceof Brick) {
                if(checkDogleg(b, (Brick)c)) {
                    joinArray[i] = DOGLEG;
                }
                // Check that the two bricks are joinable
                else if(checkJoinability(b, ((Brick)c))) {
                    ArrayList<ChordBlock> subList = 
                            (ArrayList<ChordBlock>) c.flattenBlock();
                    // Block firstBlock = subList.get(0);
                    // ArrayList<ChordBlock> chordList = (ArrayList<ChordBlock>)firstBlock.flattenBlock();
                    
                    // long domKey = (subList.get(0).getKey() + 5) % OCTAVE; 
                    
                    // Default to dominant of brick's overall key
                    long domKey = (c.getKey() + 7) % OCTAVE;
                    
                    for(ChordBlock cb : subList) {
                        if(cb.getQuality().startsWith("7")) {
                            domKey = cb.getKey();
                            break;
                        }
//                        else if(cb.getQuality().startsWith("m")) {
//                            
//                            
//                            domKey = (cb.getKey() + 5) % OCTAVE;
//                            break;
//                        }
                    }
                    
//                    boolean inFirstBlock = false;
//                    // keyDiff used in joinLookup is based on last dominant in 
//                    // first subblock
//                    for(ChordBlock j : chordList) {
//                        if(j.getQuality().equals("7"))
//                        {
//                            domKey = j.getKey();
//                            inFirstBlock = true;
//                        }
//                    }
//                    
//                    if(!inFirstBlock && !subList.isEmpty()) {
//                        for(Block subListBlock : subList) {
//                            ArrayList<ChordBlock> subChordList = (
//                                    ArrayList<ChordBlock>)subListBlock.flattenBlock();
//                            for(ChordBlock subChord : subChordList) {
//                                if(subChord.getQuality().startsWith("7")) {
//                                    domKey = subChord.getKey();
//                                }
//                            }
//                        }
//                    }
                    
                    long keyDiff = (domKey - b.getKey() + OCTAVE) % OCTAVE;
                    joinArray[i] = joinLookup(keyDiff);
                }
                else
                    joinArray[i] = "";
            }
            else
                joinArray[i] = "";
        }
        ArrayList<String> joinList = new ArrayList(Arrays.asList(joinArray));
        return joinList;
    }
    
    /** checkJoinability
     * Checks if two Blocks are joinable
     * @param first : a Block
     * @param second : a Brick
     * @return joinable : a boolean indicating whether or not first and second 
     *                    are joinable
     */
    public static boolean checkJoinability(Block first, Brick second) {
        boolean joinable = false;
        
        ArrayList<ChordBlock> firstList = 
                (ArrayList<ChordBlock>)first.flattenBlock();
        ArrayList<ChordBlock> secondList = second.flattenBlock();
        
        ChordBlock firstToCheck = firstList.get(firstList.size() - 1);
        ChordBlock secondToCheck = secondList.get(0);
        
        EquivalenceDictionary dict = new EquivalenceDictionary();
        dict.loadDictionary(CYKParser.DICTIONARY_NAME);
        
        SubstituteList firstEquivs = 
                dict.checkEquivalence(firstToCheck);
        SubstituteList secondEquivs =
                dict.checkEquivalence(secondToCheck);
        
        String firstMode = first.getMode();
        String secondMode = second.getMode();
        
        boolean firstStable = false;
        boolean secondStable = false;
        
        if(firstEquivs.contains(firstMode) || 
                first.getType().equals("Cadence")) {
            firstStable = true;
        }
        
        if(second.getType().equals("Approach") || 
                second.getType().equals("Cadence") || 
                second.getType().equals("Launcher")) {
            secondStable = false;
        }
        
        else if(secondEquivs.contains(secondMode)) {
            secondStable = true;
        }
        
        if(firstStable && !secondStable) {
            joinable = true;
        }
        
        return joinable;
    }
    
    /** checkDogleg
     * Checks whether a possible join is a dogleg
     * @param first : a Block (before join)
     * @param second : a Brick (after join)
     * @return isDogleg : a boolean indicating whether or not join is dogleg
     */
    public static boolean checkDogleg(Block first, Brick second) {
        boolean isDogleg = false;
        
        ArrayList<ChordBlock> firstList = 
                (ArrayList<ChordBlock>)first.flattenBlock();
        ArrayList<ChordBlock> secondList = second.flattenBlock();
        
        ChordBlock firstToCheck = firstList.get(firstList.size() - 1);
        ChordBlock secondToCheck = secondList.get(0);
        
        long firstKey = firstToCheck.getKey();
        String firstSymbol = firstToCheck.getSymbol();
        
        long secondKey = secondToCheck.getKey();
        String secondSymbol = secondToCheck.getSymbol();
        
        if(firstKey == secondKey && firstSymbol.startsWith("7") && 
                secondSymbol.equals("m7")) {
            isDogleg = true;
        }
        
        return isDogleg;
    }
    
    /** joinLookup
     * Retrieve name of join based on difference between keys of two bricks
     * @param keyDiff : difference of keys between two bricks
     * @return  Name of join
     */
    public static String joinLookup (long keyDiff) {
        if(keyDiff >= 0 && keyDiff < 12)
            return JOINS[(int)keyDiff];
        else
        {
            ErrorLog.log(ErrorLog.FATAL, "Difference between keys is incorrect");
            return "";
        }
    }
    
    /** diminishedCheck
     * Checks if diminished ChordBlock c resolves to ChordBlock post 
     * (used in findKeys)
     * @param c : a ChordBlock (diminished)
     * @param post : the ChordBlock to which c is compared
     * @return inKey : a boolean indicating whether or not KeySpan should 
     *                 continue
     */
    public static boolean diminishedCheck(ChordBlock c, ChordBlock post) {
        boolean inKey = false;
        
        long cKey = c.getKey();
        long pKey = post.getKey();
        
        int diff = (int)((pKey - cKey + OCTAVE) % OCTAVE);
        
        if(Arrays.asList(DIM_INTERVALS).contains(diff)) {
            inKey = true;
        }
        
        return inKey;
    }
    
    /**diatonicChordCheck
     * Checks to see if chord fits diatonically within key
     * @param c : chord to be checked
     * @param key : key that chord is checked against
     * @param mode : String that determines qualities of chords in key
     * @return isInKey : whether or not chord is in key
     */
    public static boolean diatonicChordCheck(ChordBlock c, Long key, String mode) {
        boolean isInKey = false;
        
        Polylist formScales = c.getChord().getChordForm().getScales();
        String[] modeScales = determineScales(mode);
        
        for(PolylistEnum e = formScales.elements(); e.hasMoreElements();) {
            String eString = e.nextElement().toString();
            for(String s : modeScales) {
                if(eString.contains(" " + s) && 
                        eString.contains(BrickLibrary.keyNumToName(key))) {
                    isInKey = true;
                    System.out.println("isInKey: " + isInKey);
                    return isInKey;
                }
            }
        }
        
        return isInKey;
    }
    
    /** determineScales
     * Finds what scales to compare against those of a ChordForm based on 
     * brick's mode
     * @param mode : a String; determines what scales to use for comparison
     * @return an array of strings containing scales to use for comparison
     */
    private static String[] determineScales(String mode) {
        if(mode.equalsIgnoreCase("Major")) {
            return MAJOR_MODES;
        }
        else if(mode.equalsIgnoreCase("Minor")) {
            return MINOR_MODES;
        }
        else if(mode.equalsIgnoreCase("Dominant")) {
            return DOMINANT_MODES;
        }
        else if(mode.equalsIgnoreCase("Diminished")) {
            return DIMINISHED_MODES;
        }
        else {
            return new String[0];
        }
    }
    
    /** doesResolve
     * Check if brick resolves to a certain block
     * @param b1 : brick to be checked
     * @param b2 : possible tonic of b1
     * @return resolves : whether or not b1 resolves to b2
     */
    
       public static boolean doesResolve(Brick b1, Block b2) {
        boolean resolves = false;

        if (b2 instanceof Brick) {
            Brick b2Brick = (Brick) b2;
            String b2Mode = b2.getMode();
            Long relative;

            if (b2Mode.equals("Major")) {
                relative = (b2.getKey() - 3) % OCTAVE;
            } else if (b2Mode.equals("minor")) {
                relative = (b2.getKey() + 3) % OCTAVE;
            } else {
                relative = (b2.getKey() + 5) % OCTAVE;
            }

            if (b1.getKey() == b2.getKey()/* && b1.getMode().equals(b2Mode)*/) {
                resolves = true;
            } /*else if (b1.getKey() == relative && !b1.getMode().equals(b2Mode)) {
                resolves = true;
            }*/
        } else {
//            String b2Mode = findModeFromQuality(((ChordBlock) b2).getQuality());
            String b2Mode = ((ChordBlock)b2).findModeFromQuality();
            Long relative;

            if (b2Mode.equals("Major")) {
                relative = (b2.getKey() - 3) % OCTAVE;
            } else if (b2Mode.equals("minor")) {
                relative = (b2.getKey() + 3) % OCTAVE;
            } else {
                relative = (b2.getKey() + 5) % OCTAVE;
            }

            if (b1.getKey() == b2.getKey() /*&& b1.getMode().equals(b2Mode)*/) {
                resolves = true;
            } /*else if (b1.getKey() == relative && !b1.getMode().equals(b2Mode)) {
                resolves = true;
            }*/
        }

        return resolves;
    }
       
       /** doesResolve
     * Check if brick resolves to a certain block
     * @param b1 : brick to be checked
     * @param b2 : possible tonic of b1
     * @return resolves : whether or not b1 resolves to b2
     */
       public static boolean doesResolve(ChordBlock b1, Block b2) {
        boolean resolves = false;

        if (b2 instanceof Brick) {
            Brick b2Brick = (Brick) b2;
            String b2Mode = b2.getMode();
            Long relative;

            if (b2Mode.equals("Major")) {
                relative = (b2.getKey() - 3) % OCTAVE;
            } else if (b2Mode.equals("minor")) {
                relative = (b2.getKey() + 3) % OCTAVE;
            } else {
                relative = (b2.getKey() + 5) % OCTAVE;
            }

            if ((b1.getKey() + DOM_ADJUST)%OCTAVE == b2.getKey()/* && b1.getMode().equals(b2Mode)*/) {
                resolves = true;
            } /*else if (b1.getKey() == relative && !b1.getMode().equals(b2Mode)) {
                resolves = true;
            }*/
        } else {
//            String b2Mode = findModeFromQuality(((ChordBlock) b2).getQuality());
            String b2Mode = ((ChordBlock)b2).findModeFromQuality();
            Long relative;

            if (b2Mode.equals("Major")) {
                relative = (b2.getKey() - 3) % OCTAVE;
            } else if (b2Mode.equals("minor")) {
                relative = (b2.getKey() + 3) % OCTAVE;
            } else {
                relative = (b2.getKey() + 5) % OCTAVE;
            }

            if ((b1.getKey() + DOM_ADJUST)%OCTAVE == b2.getKey() /*&& b1.getMode().equals(b2Mode)*/) {
                resolves = true;
            } /*else if (b1.getKey() == relative && !b1.getMode().equals(b2Mode)) {
                resolves = true;
            }*/
        }

        return resolves;
    }
}
     
