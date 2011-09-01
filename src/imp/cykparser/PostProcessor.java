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
import imp.data.ChordSymbol;
import imp.roadmap.*;
import imp.util.ErrorLog;
import java.util.ArrayList;
import java.util.Arrays;
import polya.*;


/**
 * purpose: gain further information from list of blocks
 * @author Zachary Merritt
 */
public class PostProcessor {
    
    public static final int DOM_ADJUST = 5; // adjustment to root (by semitones)
                                            // when dealing with dominants
    public static final int OCTAVE = 12;    // semitones in octave
    // Names of joins, arranged by difference in keys between which they
    // transition (with reference to dominant in first block)
    public static final String[] JOINS = {"Bootstrap", "Stella", "Backslider", 
        "Half Nelson", "Sidewinder", "New Horizon", "Downwinder", "Homer", 
        "Cherokee", "Woody", "Highjump", "Bauble"};
    public static final String[] RESOLUTIONS = {"","Happenstance","Yardbird","",
        "","","","","","","","Tritone"};
    
    //public static String[] FIRST_UNSTABLE = {"Approach", "Launcher"};

    public static String[] FIRST_STABLE = {"Cadence", "Dropback", "Ending", "On", "On-Off+", "Opening", "Overrun"};

    public static String[] SECOND_UNSTABLE = {"Approach", "Cadence", "Launcher", "Misc", "SPOT"};
    
    // Rules for finding representative chord in diatonicChordCheck
    private static ArrayList<Polylist> equivalenceRules;
    // Rules for which chords are diatonic depending on mode
    private static ArrayList<Polylist> diatonicRules;
    
    /** Default constructor
     * Constructs a new PostProcessor with empty rules lists
     */
    public PostProcessor() {
        equivalenceRules = new ArrayList<Polylist>();
        diatonicRules = new ArrayList<Polylist>();
    }
    
    /** PostProcessor / 2
     * Create rules lists from input
     * @param e : ArrayList of Polylists, each one of which is an equivalence rule
     * @param d : ArrayList of Polylists, each one of which is a diatonic rule
     */
    public PostProcessor(ArrayList<Polylist> e, ArrayList<Polylist> d) {
        equivalenceRules = e;
        diatonicRules = d;
    }
    
    /** getEquivalenceRules
     * Get the stored equivalence rules
     * @return a set of equivalence rules (ArrayList of Polylists)
     */
    public ArrayList<Polylist> getEquivalenceRules() {
        return equivalenceRules;
    }
    
    /** getDiatonicRules
     * Get the stored diatonic rules
     * @return a set of diatoniv rules (ArrayList of Polylists)
     */
    public ArrayList<Polylist> getDiatonicRules() {
        return diatonicRules;
    }
    
    /** setEquivalenceRules
     * set the equivalence rules based on input
     * @param e : a set of equivalence rules (ArrayList of Polylists)
     */
    public void setEquivalenceRules(ArrayList<Polylist> e) {
        equivalenceRules = e;
    }
    
    /** setDiatonicRules
     * set the diatonic rules based on input
     * @param e : a set of diatonic rules (ArrayList of Polylists)
     */
    public void setDiatonicRules(ArrayList<Polylist> d) {
        diatonicRules = d;
    }
    
     /** findKeys
     * Method groups consecutive block of same key for overarching key sections
     * @param roadmap : a RoadMap
     * @return newMap : an altered RoadMap
     */
    public static RoadMap findKeys(RoadMap roadmap) {
        
        //System.out.println("findKeys in " + roadmap);
        
        ArrayList<KeySpan> keymap = new ArrayList<KeySpan>();
        
        // Initialize key, mode, and duration of current block
        KeySpan current = new KeySpan();
        ArrayList<Block> blocks = roadmap.getBlocks();
        
        // Check for an empty roadmap
        if(blocks.isEmpty() || 
                // special case for a new leadsheet
                (blocks.size() == 1 && blocks.get(0).isChord() &&
                ((ChordBlock)blocks.get(0)).getChord().getChordSymbol().isNOCHORD())) {
            roadmap.getKeyMap().clear();
            return roadmap;
        }
        
        // Create array so we can loop through correctly
        Block[] blockArray = blocks.toArray(new Block[0]);
        
        boolean ncFlag = false;
        int ncDuration = 0;
        
        int index = 1;
        Block lastBlock = blockArray[blockArray.length - index];
        
        while(lastBlock.isChord() && ((ChordBlock)lastBlock).getChord()
                .getChordSymbol().isNOCHORD()) {
            ncDuration += lastBlock.getDuration();
            index++;
            lastBlock = blockArray[blockArray.length - index];
        }
        
        // Initialize KeySpan using last block
        current.setKey(lastBlock.getKey());
        current.setMode(lastBlock.getMode());
        current.setDuration(lastBlock.getDuration() + ncDuration);
        ncDuration = 0;
        
        // Loop through blocks backwards, 
        for(int i = blockArray.length - index - 1; i >= 0; i--) {
            // Create new KeySpan for new section
            if(blockArray[i].isSectionEnd()) {
                // Match mode to second block if first block is an approach or
                // launcher that resolves to second block
                if(blockArray[i].getType().equals("Approach") || 
                        blockArray[i].getType().equals("Launcher")) {
                    ArrayList<ChordBlock> cFirstList = 
                            (ArrayList<ChordBlock>)blockArray[i].flattenBlock();
                    ChordBlock cFirst = cFirstList.get(cFirstList.size() - 1);
                    ArrayList<ChordBlock> cSecondList = 
                            (ArrayList<ChordBlock>)blockArray[i + 1].flattenBlock();
                    ChordBlock cSecond = cSecondList.get(0);
                    
                    boolean dR = doesResolve(cFirst, cSecond);
                    if(dR) {
                        blockArray[i].setMode(cSecond.getMode());
                    }
                }
                
                KeySpan entry = current;
                keymap.add(0, entry);
                
                current = new KeySpan(blockArray[i].getKey(),
                            blockArray[i].getMode(), blockArray[i].getDuration());
                
                if(ncFlag) {
                    current.setDuration(current.getDuration() + ncDuration);
                    ncFlag = false;
                    ncDuration = 0;
                }
            }
            // Case in which first block is a brick
            else if(blockArray[i] instanceof Brick) {
                // Check if current block can roll into current KeySpan
                if(current.getKey() == blockArray[i].getKey() &&
                        current.getMode().equals(blockArray[i].getMode())) {
                    current.setDuration(current.getDuration() +
                            blockArray[i].getDuration());
                }
                // Match mode to second block if first block is an approach or
                // launcher that resolves to second block
                else if(blockArray[i].getType().equals("Approach") || 
                        blockArray[i].getType().equals("Launcher")) {
                    ArrayList<ChordBlock> cFirstList = 
                            (ArrayList<ChordBlock>)blockArray[i].flattenBlock();
                    ChordBlock cFirst = cFirstList.get(cFirstList.size() - 1);
                    ArrayList<ChordBlock> cSecondList = 
                            (ArrayList<ChordBlock>)blockArray[i + 1].flattenBlock();
                    ChordBlock cSecond = cSecondList.get(0);
                    
                    boolean dR = doesResolve(cFirst, cSecond);
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
                        
                        if(ncFlag) {
                            current.setDuration(current.getDuration() + ncDuration);
                            ncFlag = false;
                            ncDuration = 0;
                        }
                    }
                }
                // End of current key -- add to the list
                else {
                    KeySpan entry = current;
                    keymap.add(0, entry);

                    current = new KeySpan(blockArray[i].getKey(),
                            blockArray[i].getMode(), blockArray[i].getDuration());
                    
                    if(ncFlag) {
                        current.setDuration(current.getDuration() + ncDuration);
                        ncFlag = false;
                        ncDuration = 0;
                    }
                }
            }
            // Case in which first block is a chord
            else {
                ChordBlock c = (ChordBlock)blockArray[i];
                
                if(c.getChord().getChordSymbol().isNOCHORD()) {
                    ncDuration += c.getDuration();
                    ncFlag = true;
                }
                // Check if chord is diatonically within current KeySpan
                else if(diatonicChordCheck(c, current.getKey(), current.getMode())) {
                    current.setDuration(current.getDuration() + c.getDuration());
                }
                // If chord is diminished, add it onto current KeySpan
                else if (c.isDiminished()) {
                    current.setDuration(current.getDuration() + c.getDuration());
                }
                // End of current key -- add to the list
                else {
                    KeySpan entry = current;
                    keymap.add(0, entry);
            
                    current = new KeySpan(c.getKey(), c.getMode(), 
                            c.getDuration());
                    if(ncFlag) {
                        current.setDuration(current.getDuration() + ncDuration);
                        ncFlag = false;
                        ncDuration = 0;
                    }
                }
            }
        }
        
        // Special case for NC chord at beginning of song
        if(ncFlag) {
            keymap.add(0, current);
            current = new KeySpan(-1, "", ncDuration);
        }
        // Add first KeySpan in song to list
        keymap.add(0, current);
        blocks = new ArrayList<Block>(Arrays.asList(blockArray));
        
        // Replace current RoadMap with one that has properly merged KeySpans
        RoadMap newMap = new RoadMap(blocks);
        newMap.setKeyMap(keymap);
        
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
                Block postBlock;
                
                // If the brick is not the last one in the list, get chords from
                // next block
                if(i != blocks.size() - 1) {
                    postBlock = blocks.get(i + 1);
                    chordList = (ArrayList<ChordBlock>)postBlock.flattenBlock();
                }
                // Otherwise, loop around and get chords from first block
                else {
                    postBlock = blocks.get(0);
                    chordList = (ArrayList<ChordBlock>)postBlock.flattenBlock();
                }
                    
                String brickName = b.getName();
                
                if(brickName.equals("Straight Approach")) {
                    String altResolution = getAlternateResolution(b, chordList.get(0));
                    if(!altResolution.isEmpty()) {
                        brickName = brickName.replace("Straight", altResolution);
                        b.setName(brickName);
                        b.setKey(postBlock.getKey());
                        b.setMode(postBlock.getMode());
                    }
                }
                
                // Check if brick is an approach that resolves to next block
                if(b.getType().equals("Approach") && b.isSectionEnd() && 
                        doesResolve(b, chordList.get(0))) {
                    // If the name has "Approach", replace it with "Launcher"
                    if(brickName.contains("Approach")) 
                        brickName = brickName.replace("Approach", "Launcher");
                    // If not, append "(Launcher)" to the end
                    else
                        brickName = brickName + " (Launcher)";
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
            
            // Case in which current block is a dominant chord
            else if (((ChordBlock)blocks.get(i)).getSymbol().startsWith("7")) {
                ChordBlock c = (ChordBlock)blocks.get(i);
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
                
                String altResolution = getAlternateResolution(c, chordList.get(0));
                Block b;
                if(!altResolution.isEmpty()) {
                    b = new Brick(c, altResolution, "Dominant");
                }
                else {
                    b = new ChordBlock(c);
                }
                
                
                // Check for single-chord launcher
                if(doesResolve(c, chordList.get(0)) && c.isSectionEnd()) {
                    Brick uniLauncher = new Brick(c, chordList.get(0).getMode());
                    
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
    
    public static String getAlternateResolution(Block approach, ChordBlock target) {
        ArrayList<ChordBlock> chordList = (ArrayList<ChordBlock>) approach.flattenBlock();
        long domRoot = chordList.get(chordList.size() - 1).getKey();
        int domRootInt = Long.valueOf(domRoot).intValue();
        long resRoot = target.getKey();
        int resRootInt = Long.valueOf(resRoot).intValue();
        
        int diff = (resRootInt - domRootInt + OCTAVE) % OCTAVE;
        
        String altResolution = RESOLUTIONS[diff];
        
        return altResolution;
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
            return null;
        }
        for(int i = 0; i < blocks.size() - 1; i++) {
            Block b = blocks.get(i);
            Block c = blocks.get(i + 1);
            // Check if current and next block are both bricks
            if (c instanceof Brick) {
                // Check for special Dogleg join
                if(checkDogleg(b, (Brick)c)) {
                    joinArray[i] = "Dogleg";
                }
                // Check that the two bricks are joinable
                else if(checkJoinability(b, ((Brick)c))) {
                    ArrayList<ChordBlock> subList = 
                            (ArrayList<ChordBlock>) c.flattenBlock();
                    
                    // Default to dominant of brick's overall key
                    long domKey = (c.getKey() + 7) % OCTAVE;
                    
                    // Try to use first dominant in second brick
                    for(ChordBlock cb : subList) {
                        if(cb.isDominant()) {
                            domKey = cb.getKey();
                            break;
                        }
                    }
                    // Determine which join based on difference between first 
                    // block's key and dominant found in previous step
                    long keyDiff = (domKey - b.getKey() + OCTAVE) % OCTAVE;
                    joinArray[i] = joinLookup(keyDiff);
                }
                // Enter empty string if the two blocks are not joinable
                else
                    joinArray[i] = "";
            }
            // Enter empty string if second block is a chord
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
        
        // Comparing last chord of first block and first chord of second block
        ChordBlock firstToCheck = firstList.get(firstList.size() - 1);
        ChordBlock secondToCheck = secondList.get(0);
        
        // Create equivalence dictionary and get equivalences for the two chords
        
        // Create dictionary checkJoinability is called ?? Seems slow.
        
        EquivalenceDictionary dict = new EquivalenceDictionary();
        dict.loadDictionary(CYKParser.DICTIONARY_NAME);
        
        SubstituteList firstEquivs = dict.checkEquivalence(firstToCheck);
        SubstituteList secondEquivs = dict.checkEquivalence(secondToCheck);
        
        String firstMode = first.getMode();
        String secondMode = second.getMode();
        
        String firstType = first.getType();
        String secondType = second.getType();
        
        //System.out.print("joinable? " + firstType + " to " + secondType);
        
        // Determine stability of first block
        // This is necesary if the blocks are to be joinable.
        
        if( !member(firstType, FIRST_STABLE) )
          {
            //System.out.println(" NO, first not stable");
            return false; // No point in checking further
          }
        else if( firstEquivs.hasMode(firstMode) )
          {
            // Otherwise, continue if the first block is stable.
          }
        else
          {
            //System.out.println(" NO, first wrong mode");
            return false; // Otherwise, condsider non-joinable.
          }

  
        // Determine stability of second block
        
        if( member(secondType, SECOND_UNSTABLE) )
          {
            //System.out.println(" YES");
            return true;
          }
         else if( secondEquivs.hasMode(secondMode)) 
           {
            //System.out.println(" NO, second wrong ");
            return false;
           }
        
        //System.out.println(" YES");
        return true;

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
        
        // Comparing last chord of first block and first block of second block
        ChordBlock firstToCheck = firstList.get(firstList.size() - 1);
        ChordBlock secondToCheck = secondList.get(0);
        
        long firstKey = firstToCheck.getKey();
        String firstSymbol = firstToCheck.getSymbol();
        
        long secondKey = secondToCheck.getKey();
        String secondSymbol = secondToCheck.getSymbol();
        
        // Dogleg join is characterized by a dominant to a m7
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
    
    /**diatonicChordCheck
     * Checks to see if chord fits diatonically within key
     * @param c : chord to be checked
     * @param key : key that chord is checked against
     * @param mode : String that determines qualities of chords in key
     * @param dict : a BrickLibrary
     * @return isInKey : whether or not chord is in key
     */
    public static boolean diatonicChordCheck(ChordBlock c, Long key, 
            String mode) {
        
        //System.out.println("diatonicChordCheck " + c);
        
        boolean isInKey = false;
        ChordBlock cTemp = new ChordBlock(c);
        
        // Adjust for second brick's key
        cTemp.transpose(OCTAVE - key);
        Long offset = cTemp.getKey();
        
        // Transpose chord down to C
        cTemp.transpose(OCTAVE - offset);
        
        ChordSymbol cSym = null;
        
        // Get representative chord for c and save it in cSym
         
        for(Polylist p : equivalenceRules)
        {
            if(c.getChord().getChordSymbol().enhMember(p))
            {
                cSym = ChordSymbol.makeChordSymbol(p.first().toString());
                break;
            }
        };
        
        // Transpose cSym and c back by offset saved earlier
        if(cSym != null) {
            cSym = cSym.transpose(offset.intValue());
        }
        
        // Check if cSym is diatonically within key according to mode of second
        // block
        for(Polylist p : diatonicRules)
        {
            String modeTag = p.first().toString();
            p = p.rest();
            if(modeTag.equals(mode))
            {
                if(cSym != null && cSym.enhMember(p))
                {
                    isInKey = true;
                    break;
                }
            }
        }
        return isInKey;
    }
    
    /** doesResolve
     * Check if brick resolves to a certain block
     * @param b1 : brick to be checked
     * @param b2 : possible tonic of b1
     * @return whether or not b1 resolves to b2
     */
    
    public static boolean doesResolve(Brick b1, Block b2) {

        if (b1.getKey() == b2.getKey()) {
            return true;
        }

        return false;
    }
       
       /** doesResolve
     * Check if brick resolves to a certain block
     * @param b1 : ChordBlock to be checked
     * @param b2 : possible tonic of b1
     * @return whether or not b1 resolves to b2
     */
    public static boolean doesResolve(ChordBlock b1, Block b2) {
        
        // Adjust for the dominant
        if ((b1.getKey() + DOM_ADJUST)%OCTAVE == b2.getKey()) {
            return true;
        }

        return false;
    }
    
    /** 
     * Treating an array of Strings as a set, determines whether or not
     * element occurs in the array.
     * @param array
     * @param element
     * @return 
     */
    public static boolean member(String element, String array[])
      {
        for( String x: array )
          {
            if( x.equals(element) )
              {
                return true;
              }
          }       
        return false;
      }
}
     
