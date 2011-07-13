/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

import java.io.*;
import imp.brickdictionary.*;
import polya.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/** EquivalenceDictionary
 *
 * Handles equivalent chords in interpretation of bricks
 * 
 * @author Xanda Schofield
 */
public class EquivalenceDictionary {
    private LinkedList<ArrayList<Chord>> dict;
    
    /** Default constructor
     * Constructs a new EquivalenceDictionary with an empty dict.
     */
    public EquivalenceDictionary() {
        dict = new LinkedList<ArrayList<Chord>>();
    }
    
    /** loadDictionary
     * Takes in a filename as a String and loads in every line as an equivalence
     * class of chord qualities in the dictionary
     * 
     * @param filename, a String
     */
    public void addRule(ArrayList<Chord> rule) {
        dict.add(rule);
        }
    
    /**checkEquivalences / 1
     * Takes in a string for a quality and checks its equivalence classes for 
     * an appropriate class.
     * 
     * @param quality: a String describing a chord quality
     * @return an ArrayList of possibly qualities equivalent to quality, 
     * including quality itself
     */
    public SubstituteList checkEquivalence(Chord c)
    {
        SubstituteList equivalences = new SubstituteList();
        
        for (ArrayList<Chord> rule : dict)
        {
            for (Chord eq : rule)
            {
                long diff = eq.matches(c);
                if (diff >= 0)
                {
                    for (Chord sub : rule)
                    {
                        equivalences.add(sub, diff);
            }
                    break;
        }
    }
        }
        return equivalences;
    }
    
    public void loadDictionary(String filename) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            Tokenizer in = new Tokenizer(fis);
            in.slashSlashComments(true);
            in.slashStarComments(true);
            Object token;

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
                        Error e = new Error("Improper formatting for dictionary"
                                + "rule");
                        System.err.println(e);
}
                    else
                    {
                        String eqCategory = contents.first().toString();
                        contents = contents.rest();
                        
                        if (eqCategory.equals("equiv"))
                        {
                            ArrayList<Chord> newEq = new ArrayList<Chord>();
                            while (contents.nonEmpty())
                            {
                                String chordName = contents.first().toString();
                                contents = contents.rest();
                                Chord nextChord = new Chord(chordName, 
                                                        UnaryProduction.NODUR);
                                newEq.add(nextChord);
                            }
                            addRule(newEq);
                        }
                    }
                }
                else
                {
                    Error e2 = new Error("Improper formatting for token");
                    System.err.println(e2);
                    System.exit(-1);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                Logger.getLogger(CYKParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
