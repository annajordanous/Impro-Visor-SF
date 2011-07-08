
package imp.brickdictionary;
import java.util.HashMap;
import java.util.Collection;
import polya.*;
import java.io.*;
import java.util.Iterator;

/**
 * purpose: Methods relating to the brick library (dictionary)
 * @author Zachary Merritt
 */
public class BrickLibrary {
    
    private static final String[] KEY_NAME_ARRAY = {"C", "Db", "D", "Eb", "E",
        "F", "Gb", "G", "Ab", "A", "Bb", "B"};
    
    private HashMap<String, Brick> brickMap;
    
    // Construct BrickLibrary as a HashMap associating a brick's name with its
    // contents
    public BrickLibrary() {
        brickMap = new HashMap<String, Brick>();
    }
    
    public String[] getNames() {
        return brickMap.keySet().toArray(new String[0]);
    }
    
    // Add a brick to the dictionary
    public void addBrick(Brick brick) throws DictionaryException {
        if(brickMap.containsKey(brick.getName()))
        {
            throw new DictionaryException("Dictionary already contains " 
                    + brick.getName());
            
            //System.err.println("Dictionary already contains " + brick.getName());
        }
        else
            this.brickMap.put(brick.name, brick);
    }
    
    // Get a brick from the dictionary.
    public Brick getBrick(String s, long k) throws DictionaryException {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s));
            brick.transpose((k-brick.getKey() + 12)%12);
            return brick;
        }
        else
        {
            throw new DictionaryException("Dictionary does not contain " + s);
            
//            Error e = new Error("Dictionary does not contain " + s);
//            System.err.println(e);
//            return null;
        }
    }
    
    public Brick getBrick(String s, long k, long d) throws DictionaryException {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s));
            brick.transpose((k-brick.getKey() + 12)%12);
            brick.adjustBrickDuration(d);
            return brick;
        }
        else
        {
            throw new DictionaryException("Dictionary does not contain " + s);
            
//            Error e = new Error("Dictionary does not contain " + s);
//            System.err.println(e);
//            return null;
        }
    }
    
    public Collection<Brick> getMap() {
        return brickMap.values();
    }
    
    // Remove brick from dictionary (pass in actual brick)
    public void removeBrick(Brick brick) {
        this.brickMap.remove(brick.name);
    }
    
    // Remove brick from dictionary (pass in brick's name)
    public void removeBrick(String brickName) {
        this.brickMap.remove(brickName);
    }
    
    // Print contents of dictionary
    public void printDictionary() {
        Iterator iter = this.brickMap.keySet().iterator();
        
        while(iter.hasNext())
        {
            String brickName = iter.next().toString();
            Brick currentBrick = this.brickMap.get(brickName);
            currentBrick.printBrick();
        }
    }
    
    public static Boolean isValidKey(String keyName) {
        return keyName.equals("C") || keyName.equals("B#") || 
                keyName.equals("C#") || keyName.equals("Db") ||
                keyName.equals("D") ||
                keyName.equals("D#") || keyName.equals("Eb") ||
                keyName.equals("E") || keyName.equals("Fb") ||
                keyName.equals("F") || keyName.equals("E#") ||
                keyName.equals("F#") || keyName.equals("Gb") ||
                keyName.equals("G") ||
                keyName.equals("G#") || keyName.equals("Ab") ||
                keyName.equals("A") ||
                keyName.equals("A#") || keyName.equals("Bb")||
                keyName.equals("B") || keyName.equals("Cb");
    }
    
    // Convert brick's key (as a string) to a long indicating semitones above C
    public static long keyNameToNum(String keyName) {
        if(keyName.equals(""))
            return -1;
        if(keyName.equals("C") || keyName.equals("B#"))
            return 0;
        if(keyName.equals("C#") || keyName.equals("Db"))
            return 1;
        if(keyName.equals("D"))
            return 2;
        if(keyName.equals("D#") || keyName.equals("Eb"))
            return 3;
        if(keyName.equals("E") || keyName.equals("Fb"))
            return 4;
        if(keyName.equals("F") || keyName.equals("E#"))
            return 5;
        if(keyName.equals("F#") || keyName.equals("Gb"))
            return 6;
        if(keyName.equals("G"))
            return 7;
        if(keyName.equals("G#") || keyName.equals("Ab"))
            return 8;
        if(keyName.equals("A"))
            return 9;
        if(keyName.equals("A#") || keyName.equals("Bb"))
            return 10;
        if(keyName.equals("B") || keyName.equals("Cb"))
            return 11;
        else
        {
            System.err.println("keyNameToNum: Incorrect key formatting " 
                    + keyName);
            System.exit(-1);
            return 0;
//            Error e = new Error("keyNameToNum: Incorrect key formatting "
//                    + keyName);
//            System.err.println(e);
//            return -1;
        }
    }
    
    // Convert brick's key (as a string) to a long indicating semitones above C
    public static String keyNumToName(long keyNum) {
        if(keyNum >= 0 && keyNum < 12) {
            return KEY_NAME_ARRAY[(int)keyNum];
        }
        
//        if(keyNum == 0)
//            return "C";
//        if(keyNum == 1)
//            return "Db";
//        if(keyNum == 2)
//            return "D";
//        if(keyNum == 3)
//            return "Eb";
//        if(keyNum == 4)
//            return "E";
//        if(keyNum == 5)
//            return "F";
//        if(keyNum == 6)
//            return "Gb";
//        if(keyNum == 7)
//            return "G";
//        if(keyNum == 8)
//            return "Ab";
//        if(keyNum == 9)
//            return "A";
//        if(keyNum == 10)
//            return "Bb";
//        if(keyNum == 11)
//            return "B";
        else if (keyNum == -1)
            return "";
        else
        {
            System.err.println("keyNumToName: Incorrect key formatting " 
                    + keyNum);
            System.exit(-1);
            return "";
            
//            Error e = new Error("keyNumToName: Incorrect key formatting " + 
//                    keyNum);
//            System.err.println(e);
//            return "";
        }
    }
        
    // Read in dictionary file, parse into bricks, and build the dictionary
    public static BrickLibrary processDictionary() throws IOException, 
            DictionaryException {
        
        FileInputStream fis = new FileInputStream("vocab/BrickDictionary.txt");
        Tokenizer in = new Tokenizer(fis);
        in.slashSlashComments(true);
        in.slashStarComments(true);
        Object token;
        
        BrickLibrary dictionary = new BrickLibrary();
        
        // Read in S expressions until end of file is reached
        while ((token = in.nextSexp()) != Tokenizer.eof)
        {
            if (token instanceof Polylist) 
            {
                Polylist contents = (Polylist)token;
                
                // Check that polylist has enough fields to be a brick
                // Needs BlockType (i.e. "Brick"), name, key, and contents
                if (contents.length() < 4)
                {
                    throw new DictionaryException("Improper formatting for "
                            + "Polylist");
                    
//                    Error e = new Error("Improper formatting for Polylist");
//                    System.err.println(e);
                }
                else
                {
                    String blockCategory = contents.first().toString();
                    contents = contents.rest();
                    
                    // For reading in dictionary, should only encounter bricks
                    if (blockCategory.equals("Brick"))
                    {
                        String brickName = contents.first().toString();
                        contents = contents.rest();
                        String brickMode = contents.first().toString();
                        contents = contents.rest();
                        String brickType = contents.first().toString();
                        contents = contents.rest();
                        String brickKeyString = contents.first().toString();
                        contents = contents.rest();
                        long brickKeyNum = keyNameToNum(brickKeyString);
                        
                        // Create new brick using info gathered from text and
                        // add to dictionary
                        Brick currentBrick = new Brick(brickName, brickKeyNum,
                                brickType, contents, dictionary, brickMode);
                        dictionary.addBrick(currentBrick);
                        
                    }
                    else
                    {
                        throw new DictionaryException("blockCategory not of "
                                + "correct type");
                        
//                        Error e4 = new Error("blockCategory not of correct type");
//                        System.err.println(e4);
                    }
                }
            }
            else
            {
                throw new DictionaryException("Improper formatting for token");
                
//                Error e2 = new Error("Improper formatting for token");
//                System.err.println(e2);
//                System.exit(-1);
            }
        }
        
        return dictionary;
    }
    
    public static void main(String[] args) throws IOException, 
            DictionaryException {
        BrickLibrary dictionary;
        dictionary = BrickLibrary.processDictionary();
        
        dictionary.printDictionary();
        
//        Brick testBrick = dictionary.getBrick("II-n-Back", 0);
//        testBrick.adjustBrickDuration(8);
//        testBrick.printBrick();
    }
}
