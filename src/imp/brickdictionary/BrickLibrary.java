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
import java.util.logging.Level;
import java.util.logging.Logger;
import polya.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

/**
 * purpose: Methods relating to the brick library (dictionary)
 * @author Zachary Merritt
 */
public class BrickLibrary {
    
    private static final String[] KEY_NAME_ARRAY = {"C", "Db", "D", "Eb", "E",
        "F", "Gb", "G", "Ab", "A", "Bb", "B"};
    private static final long DEFAULT_COST = 40;
    public static final long NONBRICK = 2000;
    public static final String DICTIONARY_FILE = "vocab/BrickDictionary.txt";
   
    private LinkedHashMap<String, LinkedList<Brick>> brickMap;
    private LinkedHashMap<String, Long> costMap;
    
    // Construct BrickLibrary as a HashMap associating a brick's name with its
    // contents
    public BrickLibrary() {
        brickMap = new LinkedHashMap<String, LinkedList<Brick>>();
        costMap = new LinkedHashMap<String, Long>();
    }
    
    public String[] getNames() {
        return brickMap.keySet().toArray(new String[0]);
    }
    
    public void addBrickDefinition(Brick brick)
    {
        addBrick(brick);
        
        Polylist defn = brick.toBrickDefinition();
        String defnString = defn.toString();
        defnString = defnString.replaceAll(" \\(", "\n        \\(");
        
        try {
            FileOutputStream out = new FileOutputStream(DICTIONARY_FILE, true);
            out.write("\n".getBytes());
            out.write(defnString.getBytes());
            out.write("\n".getBytes());
            out.close();
        } catch (IOException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Cannot write to dictionary");
        }
        
    }
    
    // Add a brick to the dictionary
    public void addBrick(Brick brick) {
        if(brickMap.containsKey(brick.getName()))
        {
            LinkedList<Brick> sameStemList = brickMap.get(brick.getName());
            for (Brick sameStem : sameStemList)
            {
                if (sameStem.getQualifier().equals(brick.getQualifier()))
                {
                    ErrorLog.log(ErrorLog.WARNING, "Dictionary already contains " +
                    brick.getName() + "(" + brick.getQualifier() + ")", true);
                    break;
                }
            }
            sameStemList.add(brick);
        }
        else
        {
            LinkedList<Brick> brickList = new LinkedList<Brick>();
            brickList.add(brick);
            this.brickMap.put(brick.name, brickList);
        }
        
        // special rule for creating overruns
        if (brick.getType().equals("Cadence")) {
            String overrunName = brick.getName() + " with Overrun";
            long overrunKeyNum = brick.getKey();
            String overrunType = "Overrun";
            String overrunMode = brick.getMode();
            String overrunQualifier = brick.getQualifier();

            // take blocks from regular cadence and add the next chord
            // in the circle of fifths with the same quality as the
            // resolution
            ArrayList<Block> overrunBlocks = new ArrayList<Block>();
            overrunBlocks.addAll(brick.getSubBlocks());
            ArrayList<ChordBlock> chords = brick.flattenBlock();
            ChordBlock prevChord = chords.get(chords.size() - 1);
            ChordBlock overrunChord = 
                       new ChordBlock(prevChord.transposeName(5), 
                                      prevChord.getDuration());
            overrunBlocks.add(overrunChord);

            // make a new brick from this list of blocks
            Brick overrun = new Brick(overrunName, overrunQualifier, overrunKeyNum,
                    overrunType, overrunBlocks, overrunMode);
            addBrick(overrun);
                    
            String dropbackName = brick.getName() + " with Dropback";
            long dropbackKeyNum = brick.getKey();
            String dropbackType = "Dropback";
            String dropbackMode = brick.getMode();
            String dropbackQualifier = brick.getQualifier();

            // take blocks from regular cadence and add the next chord
            // in the circle of fifths with the same quality as the
            // resolution
            ArrayList<Block> dropbackBlocks = new ArrayList<Block>();
            dropbackBlocks.addAll(brick.getSubBlocks());
            String dropbackChordName = keyNumToName((prevChord.getKey() + 9) % 12);
            if (dropbackMode.equals("minor"))
                dropbackChordName += "7b5";
            else
                dropbackChordName += 7;
            ChordBlock dropbackChord = 
                       new ChordBlock(dropbackChordName, 
                                      prevChord.getDuration());
            dropbackBlocks.add(dropbackChord);

            // make a new brick from this list of blocks
            Brick dropback = new Brick(dropbackName, dropbackQualifier, dropbackKeyNum,
                    dropbackType, dropbackBlocks, dropbackMode);
            addBrick(dropback);
        }
    }
    
    // Get a brick from the dictionary.
    public Brick getBrick(String s, long k) {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s).getFirst());
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
    
    public Brick getBrick(String s, String q, long k) {
        if(brickMap.containsKey(s))
        {
            Brick brick = brickMap.get(s).getFirst();
            for(Brick variant : brickMap.get(s)) {
                if(variant.getQualifier().equals(q)) {
                    brick = new Brick(variant);
                    break;
                }
            }
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
    
    public Brick getBrick(String s, long k, int d) {
        if(brickMap.containsKey(s))
        {
            Brick brick = new Brick(brickMap.get(s).getFirst());
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
    
    public Collection<Brick> getFullMap() {
        LinkedList<Brick> values = new LinkedList<Brick>();
        for (LinkedList<Brick> brickname : brickMap.values())
        {
            values.addAll(brickname);
        }
        
        return values;
    }
    
    public LinkedList<LinkedList<Brick>> getMap() {
        LinkedList<LinkedList<Brick>> values = new LinkedList();
        for (LinkedList<Brick> brickname : brickMap.values())
        {
            values.add(brickname);
        }
        
        return values;
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
        Iterator iter = getMap().iterator();
        
        while(iter.hasNext())
        {
            Brick currentBrick = (Brick)iter.next();
            currentBrick.printBrick();
        }
    }
    
    public void addType(String t) {
        costMap.put(t, DEFAULT_COST);
    }
    
    public void addType(String t, long c) {
        costMap.put(t, c);
    }
    
    public long getCost(String t) {
        if (hasType(t)) 
            return costMap.get(t);
        else {
            return NONBRICK;
        }
    }
    
    public boolean hasType(String t) {
        return costMap.containsKey(t);
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
            ErrorLog.log(ErrorLog.FATAL, "Incorrect key formatting: " + keyName);
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
        
        FileInputStream fis = new FileInputStream(DICTIONARY_FILE);
        Tokenizer in = new Tokenizer(fis);
        in.slashSlashComments(true);
        in.slashStarComments(true);
        Object token;
        
        BrickLibrary dictionary = new BrickLibrary();
        LinkedHashMap<String, LinkedList<Polylist>> polymap = 
                new LinkedHashMap<String, LinkedList<Polylist>>();
        
        // Read in S expressions until end of file is reached
        while ((token = in.nextSexp()) != Tokenizer.eof)
        {
            if (token instanceof Polylist) 
            {
                Polylist contents = (Polylist)token;
                
                // Check that polylist has enough fields to be a brick 
                // Needs BlockType (i.e. "Brick"), name, key, and contents
                if (contents.length() < 3)
                {
                    ErrorLog.log(ErrorLog.WARNING, "Improper formatting for"
                            + " a BrickDictionary Polylist", true);
                }
                        
                else
                {
                    String blockCategory = contents.first().toString();
                    contents = contents.rest();
                    
                    if (blockCategory.equals("brick-type"))
                    {
                        if (contents.length() != 2 && contents.length() != 1)
                            ErrorLog.log(ErrorLog.WARNING, "Not a correct"
                                    + "brick-type declaration");
                        else {
                            String type = contents.first().toString();
                            contents = contents.rest();
                            if (contents.isEmpty()) {
                                dictionary.addType(type);
                            }
                            else
                            {
                                Object cost = contents.first();
                                if (cost instanceof Long)
                                    dictionary.addType(type, (Long)cost);
                                else {
                                    ErrorLog.log(ErrorLog.WARNING, "Incorrect"
                                            + "cost for brick type" + type);
                                    dictionary.addType(type);
                                }
                            }
                        }
                    }
                    
                    // For reading in dictionary, should only encounter bricks
                    else if (blockCategory.equals("Brick"))
                    {
                        String brickName = dashless(contents.first().toString());
                        contents = contents.rest();
                        if (polymap.containsKey(brickName))
                                polymap.get(brickName).add((Polylist)token);
                        else {
                            LinkedList<Polylist> newKey = new LinkedList<Polylist>();
                            newKey.add((Polylist)token);
                            polymap.put(brickName, newKey);
                        }
                    }
                    else
                    {
                        ErrorLog.log(ErrorLog.WARNING, "Improper type for "
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

        for (LinkedList<Polylist> brickStem : polymap.values()) {
            
            for (Polylist contents : brickStem) {
            
                contents = contents.rest();
                String brickName = dashless(contents.first().toString());
                contents = contents.rest();

                String brickQualifier = "";
                if (contents.first() instanceof Polylist)
                {
                    brickQualifier = ((Polylist)contents.first()).first().toString();
                    contents = contents.rest();
                }

                boolean hadBrick = dictionary.hasBrick(brickName);

                String brickMode = contents.first().toString();
                contents = contents.rest();

                String brickType = contents.first().toString();
                contents = contents.rest();
                if (!dictionary.hasType(brickType))
                    ErrorLog.log(ErrorLog.WARNING, brickName + " is of "
                                + "uninitialized type " + brickType + 
                                "; will register as non-brick");

                String brickKeyString = contents.first().toString();
                contents = contents.rest();
                long brickKeyNum = keyNameToNum(brickKeyString);

                Brick currentBrick = new Brick(brickName, brickQualifier, brickKeyNum,
                           brickType, contents, dictionary, brickMode, polymap);
                dictionary.addBrick(currentBrick);
            }
            
            
            
        }
        return dictionary;
    }
    
        
    public static String dashless(String s) {
        return s.replace('-', ' ');
    }
    
    public void writeDictionary(String filename) {
        FileWriter fstream;
        try {
            fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            
            out.write("\\\\ Type Definitions\n\n");
            
            Set<String> types = costMap.keySet();
            for (String type : types)
            {
                long cost = costMap.get(type);
                Polylist brickType = Polylist.list("brick-type", type, cost);
                out.write(brickType.toString());
                out.write("\n");
            }
            
            out.write("\n\n\\\\ Brick Definitions\n\n");
            
            for (Brick brick : getFullMap())
            {
                out.write(brick.toPolylist().toString());
                out.write("\n\n");
            }
            
        } catch (IOException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Could not write dictionary file.");
        }  
    }
    
    public static void main(String[] args) throws IOException 
            {
        BrickLibrary dictionary;
        dictionary = BrickLibrary.processDictionary();
        
        dictionary.printDictionary();
    }
}
