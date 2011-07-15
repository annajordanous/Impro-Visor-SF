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
import imp.util.ErrorLog;
import java.util.Collection;
import polya.*;
import java.io.*;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * purpose: Methods relating to the brick library (dictionary)
 * @author Zachary Merritt
 */
public class BrickLibrary {
    
    private static final String[] KEY_NAME_ARRAY = {"C", "Db", "D", "Eb", "E",
        "F", "Gb", "G", "Ab", "A", "Bb", "B"};
    
    private LinkedHashMap<String, Brick> brickMap;
    
    // Construct BrickLibrary as a HashMap associating a brick's name with its
    // contents
    public BrickLibrary() {
        brickMap = new LinkedHashMap<String, Brick>();
    }
    
    public String[] getNames() {
        return brickMap.keySet().toArray(new String[0]);
    }
    
    // Add a brick to the dictionary
    public void addBrick(Brick brick) {
        if(brickMap.containsKey(brick.getName()))
        {
            ErrorLog.log(ErrorLog.WARNING, "Dictionary already contains " +
                    brick.getName(), true);
        }
        else
            this.brickMap.put(brick.name, brick);
    }
    
    // Get a brick from the dictionary.
    public Brick getBrick(String s, long k) {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s));
            brick.transpose((k-brick.getKey() + 12)%12);
            return brick;
        }
        else
        {
            ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                    s, true);
            return null;
        }
    }
    
    public Brick getBrick(String s, long k, long d) {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s));
            brick.transpose((k-brick.getKey() + 12)%12);
            brick.adjustBrickDuration(d);
            return brick;
        }
        else
        {
            ErrorLog.log(ErrorLog.WARNING, "Dictionary does not contain " +
                    s, true);
            return null;
        }
    }
    
    public boolean hasBrick(String s)
    {
        return (brickMap.containsKey(s));
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
        }
    }
    
    // Convert brick's key (as a string) to a long indicating semitones above C
    public static String keyNumToName(long keyNum) {
        if(keyNum >= 0 && keyNum < 12) {
            return KEY_NAME_ARRAY[(int)keyNum];
        }
        else if (keyNum == -1)
            return "";
        else
        {
            ErrorLog.log(ErrorLog.FATAL, "Incorrect key number: " + keyNum);
            return "";
        }
    }
        
    // Read in dictionary file, parse into bricks, and build the dictionary
    public static BrickLibrary processDictionary() throws IOException {
        
        FileInputStream fis = new FileInputStream("vocab/BrickDictionary.txt");
        Tokenizer in = new Tokenizer(fis);
        in.slashSlashComments(true);
        in.slashStarComments(true);
        Object token;
        
        BrickLibrary dictionary = new BrickLibrary();
        LinkedHashMap<String, Polylist> polymap = new LinkedHashMap<String, Polylist>();
        
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
                    ErrorLog.log(ErrorLog.WARNING, "Improper formatting for"
                            + "a BrickDictionary Polylist", true);
                }
                else
                {
                    String blockCategory = contents.first().toString();
                    contents = contents.rest();
                    
                    // For reading in dictionary, should only encounter bricks
                    if (blockCategory.equals("Brick"))
                    {
                        String brickName = dashless(contents.first().toString());
                        contents = contents.rest();
                      /*String brickMode = contents.first().toString();
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
                       */
                        
                        polymap.put(brickName, (Polylist)token);
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.WARNING, "Improper type for"
                            + "a BrickDictionary Polylist", true);
                    }
                }
            }
            else
            {
                ErrorLog.log(ErrorLog.WARNING, "Improper formatting for"
                    + "a Polylist token", true);
            }
        }

        for (Polylist contents : polymap.values()) {
            contents = contents.rest();
            String brickName = dashless(contents.first().toString());
            contents = contents.rest();
            if (!dictionary.hasBrick(brickName)) {
                String brickMode = contents.first().toString();
                contents = contents.rest();
                String brickType = contents.first().toString();
                contents = contents.rest();
                String brickKeyString = contents.first().toString();
                contents = contents.rest();
                long brickKeyNum = keyNameToNum(brickKeyString);
                
                Brick currentBrick = new Brick(brickName, brickKeyNum,
                           brickType, contents, dictionary, brickMode, polymap);
                dictionary.addBrick(currentBrick);
            }
        }
        return dictionary;
    }
    
        
    public static String dashless(String s) {
        return s.replace('-', ' ');
    }
    public static void main(String[] args) throws IOException 
            {
        BrickLibrary dictionary;
        dictionary = BrickLibrary.processDictionary();
        
        dictionary.printDictionary();
    }
}
