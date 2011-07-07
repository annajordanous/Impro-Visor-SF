
package imp.cykparser;
import imp.brickdictionary.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * purpose: gain further information from list of blocks
 * @author Zachary Merritt
 */
public class PostProcessing {
    
    private static final String[] JOINS = {"Homer", "Cherokee", "Woody", 
        "Highjump", "Bauble", "Bootstrap", "Stella", "Backslider", 
        "Half Nelson", "Sidewinder", "New Horizon", "Downwinder"};
    
        /** findKeys
     * Method groups consecutive block of same key for overarching key sections
     * @param blocks : ArrayList of blocks (like output from CYKParser)
     * @return keymap : ArrayList of long[] (size 2), each containing key and 
     *                  its corresponding total duration
     */
    public static ArrayList<long[]> findKeys(ArrayList<Block> blocks) {
        ArrayList<long[]> keymap = new ArrayList<long[]>();
        
        // Initialize key and duration of current block
        long currentKey = -1;
        long currentDuration = 0;
        
        for(Block b : blocks) {
            
            // If two consecutive blocks in same key, add new block's duration 
            // to total
            if(currentKey == b.getKey()) {
                currentDuration += b.getDuration();
            }
            else {
                // Check if still using key that was initialiized
                if(currentKey >= 0) {
                    // End of current key -- add to the list
                    long[] entry = {currentKey, currentDuration};
                    keymap.add(entry);
                }
                // Create new entry for the next key
                currentKey = b.getKey();
                currentDuration = b.getDuration();
            }
        }
        
        if(currentKey != -1)
            keymap.add( new long[]{currentKey, currentDuration} );
        
        return keymap;
    }
    
    /** mergeBlockKeys
     * Method groups consecutive block of same key for overarching key sections
     * @param blocks : ArrayList of blocks (like output from CYKParser)
     * @return keymap : ArrayList of long[] (size 2), each containing key and 
     *                  its corresponding total duration
     */
    public static ArrayList<long[]> mergeBlockKeys(ArrayList<Block> blocks) {
        ArrayList<long[]> keymap = new ArrayList<long[]>();
        
        // Initialize key and duration of current block
        long currentKey = -1;
        long currentDuration = 0;
        
        for(Block b : blocks) {
            
            // If two consecutive blocks in same key, add new block's duration 
            // to total
            if(currentKey == b.getKey()) {
                currentDuration += b.getDuration();
            }
            else {
                // Check if still using key that was initialiized
                if(currentKey >= 0) {
                    // End of current key -- add to the list
                    long[] entry = {currentKey, currentDuration};
                    keymap.add(entry);
                }
                // Create new entry for the next key
                currentKey = b.getKey();
                currentDuration = b.getDuration();
            }
        }
        
        if(currentKey != -1)
            keymap.add( new long[]{currentKey, currentDuration} );
        
        return keymap;
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
        
        // Initialization of brick that will be compared to one immediately 
        // after it
        Brick brickToCheck = null;
        
        for(Block b : blocks) {
            if(b instanceof Brick) {
                // Flatten the block so we can easily look at specific chords
                ArrayList<Chord> chordList = (ArrayList)b.flattenBlock();
                
                // Check if brick is not null and if it resolves to first chord 
                // of current brick
                if(brickToCheck != null && brickToCheck.getKey() == 
                        chordList.get(0).getKey()) {
                    String brickName = brickToCheck.getName();
                    
                    // Replace the word "Approach" in its name (if it's there)
                    // with "Launcher" 
                    if(brickName.contains("Approach")) {
                        brickName.replace("Approach", "Launcher");
                        brickToCheck.setName(brickName);
                    }
                    // Otherwise, just append "Launcher" to the end
                    else {
                        brickName = brickName + "Launcher";
                    }
                    // Change the type from approach to launcher and add to list
                    brickToCheck.setType("Launcher");
                    alteredList.add(brickToCheck);
                    brickToCheck = null;
                }
                // If it doesn't resolve, add to list unaltered and clear brick 
                // so later comparisions don't get messed up
                else {
                    alteredList.add(brickToCheck);
                    brickToCheck = null;
                }
                
                // Check if current brick is an approach at the end of a section
                // If so, save the brick to compare to the next one for 
                // resolution
                if(chordList.get(chordList.size() - 1).isSectionEnd() && 
                        ((Brick)b).getType().equals("Approach")) {
                    brickToCheck = (Brick)b;
                }
                // If not, add it to the list
                else
                    alteredList.add(b);
            }
            // If it's a chord, just add it in
            else
                alteredList.add(b);
        }
        
        return alteredList;
    }
    
     /** findJoins
     * A method that finds joins between bricks
     * Note: There has to be a more elegant way to do this
     * @param blocks : ArrayList of Blocks (Chords or Bricks)
     * @return joinList : ArrayList of joins between blocks
     */
    public static ArrayList<String> findJoins(ArrayList<Block> blocks) {

        String[] joinArray = null;
        if(blocks.size() >= 1) {
            joinArray = new String[blocks.size() - 1];
        }
        else {
            System.err.println("Can't find joins in ArrayList of size 0");
            return null;
        }
        for(int i = 0; i < blocks.size() - 1; i++) {
            Block b = blocks.get(i);
            Block c = blocks.get(i + 1);
            // Check if current and next block are both bricks
            if (b instanceof Brick && c instanceof Brick) {
                if(checkJoinability(((Brick)b), ((Brick)c))) {
                    // If so, find the difference between the two keys 
                    long keyDiff = (c.getKey() - b.getKey() + 12) % 12;
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
    
    public static boolean checkJoinability(Brick first, Brick second) {
        boolean joinable = false;
        
        ArrayList<Chord> firstList = first.flattenBlock();
        ArrayList<Chord> secondList = second.flattenBlock();
        
        Chord firstToCheck = firstList.get(firstList.size() - 1);
        Chord secondToCheck = secondList.get(0);
        
        EquivalenceDictionary dict = new EquivalenceDictionary();
        dict.loadDictionary(CYKParser.DICTIONARY_NAME);
        
        ArrayList<String> firstEquivs = 
                dict.checkEquivalence(firstToCheck.getQuality());
        ArrayList<String> secondEquivs =
                dict.checkEquivalence(secondToCheck.getQuality());
        
        String firstMode = first.getMode();
        String secondMode = second.getMode();
        
        boolean firstStable = false;
        boolean secondStable = false;
        
        if(firstEquivs.contains(firstMode)) {
            firstStable = true;
        }
        
        if(secondEquivs.contains(secondMode)) {
            secondStable = true;
        }
        
        if(firstStable && !secondStable) {
            joinable = true;
        }
        
        return joinable;
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
            System.err.println("joinLookup: incorrect keyDiff formatting " 
                    + keyDiff);
            System.exit(-1);
            return "";
        }
    }
    
    /** keyCheck
     * More in depth check to see if two blocks are in the same key
     * @param b1 : first block
     * @param b2 : second block
     * @return sameKey : boolean telling whether in same key or not
     */
    public static boolean keyCheck(Block b1, Block b2) {
        // TO DO: figure out where this will be useful before you write it
        boolean sameKey = false;
        
        return sameKey;
    }
}
