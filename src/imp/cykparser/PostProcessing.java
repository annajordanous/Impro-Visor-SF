
package imp.cykparser;
import imp.brickdictionary.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * purpose: gain further information from list of blocks
 * @author Zachary Merritt
 */
public class PostProcessing {
    
    private static final int OCTAVE = 12;
    private static final String[] JOINS = {"Bootstrap", "Stella", "Backslider", 
        "Half Nelson", "Sidewinder", "New Horizon", "Downwinder", "Homer", 
        "Cherokee", "Woody", "Highjump", "Bauble"};
    
     /** findKeys
     * Method groups consecutive block of same key for overarching key sections
     * @param blocks : ArrayList of blocks (like output from CYKParser)
     * @return keymap : ArrayList of KeySpans, each containing key, mode, and 
     *                  a corresponding total duration
     */
    public static ArrayList<KeySpan> findKeys(ArrayList<Block> blocks) {
        ArrayList<KeySpan> keymap = new ArrayList<KeySpan>();
        
        // Initialize key, mode, and duration of current block
        KeySpan current = new KeySpan();
        
        for(Block b : blocks) {
            
            // If two consecutive blocks in same key, add new block's duration 
            // to total
            if(current.getKey() == b.getKey() && 
                    current.getMode().equals(b.getMode())) {
                current.setDuration(current.getDuration() + b.getDuration());
            }
            else {
                // End of current key -- add to the list
                KeySpan entry = current;
                keymap.add(entry);

                // Create new entry for the next key
                current = new KeySpan(b.getKey(), b.getMode(), b.getDuration());
            }
        }
        
        keymap.add(current);
        
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
        
        for(int i = 0; i < blocks.size(); i++) {
            
            // If the current block is a brick, check if it could be a launcher
            if(blocks.get(i) instanceof Brick) {
                Brick b = (Brick)blocks.get(i);
                ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();
                
                // If the brick is not the last one in the list, get chords from
                // next block
                if(i != blocks.size() - 1) 
                    chordList = (ArrayList<ChordBlock>)blocks.get(i + 1).flattenBlock();
                // Otherwise, loop around and get chords from first block
                else
                    chordList = (ArrayList<ChordBlock>)blocks.get(0).flattenBlock();
                    
                String brickName = b.getName();
                
                // Check if brick is an approach that resolves to next block
                if(b.getType().equals("Approach") && b.isSectionEnd() && 
                        doesResolve(b, chordList.get(0))) {
                    // If the name has "Approach", replace it with "Launcher"
                    if(brickName.contains("Approach")) 
                        brickName = brickName.replace("Approach", "Launcher");
                    // If not, append "-Launcher" to the end
                    else
                        brickName = brickName + "-Launcher";
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
            // If the block is a chord, add it to the list
            else
                alteredList.add(blocks.get(i));
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
                    ArrayList<ChordBlock> chordList = 
                            (ArrayList<ChordBlock>) c.flattenBlock();
                    long firstDominantKey = (c.getKey() + 7)%OCTAVE;
                    for(ChordBlock j : chordList) {
                        if(j.getQuality().equals("7")) {
                            firstDominantKey = j.getKey();
                            break;
                        }
                    }
                    long keyDiff = (firstDominantKey - b.getKey() + 12) % 12;
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
        
        ArrayList<ChordBlock> firstList = first.flattenBlock();
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
        
        if(firstEquivs.contains(firstMode)) {
            firstStable = true;
        }
        
        if(second.getType().equals("Approach") || second.getType().equals("Cadence")) {
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
    
    
    
    /** chordKeyFind
     * Check to see if chord is diatonically within current ambient key
     * @param b1 : block directly before chord
     * @param c: chord to be checked
     * @param b2 : block directly after chord
     * @return km : the key and mode of Chord c
     */
    
    /*
    public static String[] chordKeyFind(Block b1, Chord c, Block b2) {
        String[] keyAndMode = new String[2];
        
        Long preKey = b1.getKey();
        Long postKey = b2.getKey();
        
        String preMode = b1.getMode();
        String postMode = b2.getMode();
        
        boolean isInKey = false;
        
        if(preKey == postKey && preMode.equals(postMode)) {
            
            isInKey = diatonicChordCheck(c, preKey, preMode);
            
            if(isInKey) {
                String keyString = BrickLibrary.keyNumToName(preKey);
                keyAndMode = new String[]{keyString, preMode};
            }
            else {
                String keyString = BrickLibrary.keyNumToName(c.getKey());
                String mode = findModeFromQuality(c.getQuality());
                
                keyAndMode = new String[]{keyString, mode};
            }
        }
        else {
            //Default to using b1 for comparison
            // TO DO : change to after instead of before
            keyAndMode = chordKeyFind(b1, c);
        }
        
        return keyAndMode;
    }
     * 
     */
    
    /** chordKeyFind
     * Check to see if chord is diatonically within current ambient key
     * @param b : block directly before chord
     * @param c: chord to be checked
     * @return keyAndMode : the key and mode of Chord c
     */
    
    /*
    public static String[] chordKeyFind(Block b, Chord c) {
        String[] keyAndMode = new String[2];
        
        Long preKey = b.getKey();
        String preMode;
        
        if(!b.getMode().isEmpty()) {
            preMode = b.getMode();
        }
        else {
            preMode = findModeFromQuality(((Chord)b).getQuality());            
        }
        
        boolean isInKey = false;
        
        isInKey = diatonicChordCheck(c, preKey, preMode);
        
        
        if(isInKey) {
            String keyString = BrickLibrary.keyNumToName(preKey);
            keyAndMode = new String[]{keyString, preMode};
        }
        
        return keyAndMode;
    }
    
     * 
     */
    
    /**diatonicChordCheck
     * Checks to see if chord fits diatonically within key
     * @param c : chord to be checked
     * @param key : key that chord is checked against
     * @param mode : String that determines qualities of chords in key
     * @return isInKey : whether or not chord is in key
     * @throws IOException 
     */
    
    /*
    public static boolean diatonicChordCheck(Chord c, Long key, String mode) {
        FileInputStream fis = null;
        try {
            boolean isInKey = false;
            fis = new FileInputStream("vocab/diatonic.txt");
            Tokenizer in = new Tokenizer(fis);
            in.slashSlashComments(true);
            in.slashStarComments(true);
            Object token;
            while((token = in.nextSexp()) != Tokenizer.eof) {
                if(token instanceof Polylist) {
                    Polylist contents = (Polylist)token;
                    if (contents.length() < 4) {
                        Error e = new Error("Not enough arguments in Polylist: ");
                        System.err.println(e.getMessage() + contents.toString());
                    }
                    else {
                        String polylistTag = contents.first().toString();
                        contents = contents.rest();
                        
                        if (polylistTag.equals("diatonic")) {
                            String diatonicMode = contents.first().toString();
                            contents = contents.rest();
                            String diatonicKey = contents.first().toString();
                            contents = contents.rest();
                            if (diatonicMode.equals(mode)) {
                                Long diff = key - 
                                        BrickLibrary.keyNameToNum(diatonicKey);
                                while(contents.nonEmpty()) {
                                    Polylist diatonicElement = 
                                            (Polylist) contents.first();
                                    Long elementKey = BrickLibrary.keyNameToNum
                                            (diatonicElement.first().toString());
                                    String elementQuality = 
                                            diatonicElement.last().toString();
                                    elementKey = (elementKey + diff)%OCTAVE;
                                    if(c.getKey() == elementKey && 
                                            c.getQuality().equals(elementQuality)) {
                                        isInKey = true;
                                        return isInKey;
                                    }
                                }
                            }
                        }
                        else {
                            Error e = new Error("No diatonic tag on Polylist: ");
                            System.err.println(e.getMessage() + polylistTag);
                        }
                    }
                }
            }
            return isInKey;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PostProcessing.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(PostProcessing.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        return false;
    }
     * 
     */
    
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
            } else if (b2Mode.equals("Minor")) {
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
            String b2Mode = findModeFromQuality(((ChordBlock) b2).getQuality());
            Long relative;

            if (b2Mode.equals("Major")) {
                relative = (b2.getKey() - 3) % OCTAVE;
            } else if (b2Mode.equals("Minor")) {
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

    /** findModeFromQuality
     * Find mode of a block using quality of a chord
     * @param quality : String used to find mode
     * @return mode : String that determines overall tonicity of block
     */
    public static String findModeFromQuality(String quality) {
        String mode;

        if (quality.startsWith("M") || quality.equals("")) {
            mode = "Major";
        } else if (quality.startsWith("7")) {
            mode = "Dominant";
        } else {
            mode = "Minor";
        }

        return mode;
    }
}
     
