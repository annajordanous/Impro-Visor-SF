/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/** EquivalenceDictionary
 *
 * Handles equivalent chords in interpretation of bricks
 * 
 * @author Xanda Schofield
 */
public class EquivalenceDictionary {
    private LinkedList<ArrayList<String>> dict;
    
    /** Default constructor
     * Constructs a new EquivalenceDictionary with an empty dict.
     */
    public EquivalenceDictionary() {
        dict = new LinkedList<ArrayList<String>>();
    }
    
    /** loadDictionary
     * Takes in a filename as a String and loads in every line as an equivalence
     * class of chord qualities in the dictionary
     * 
     * @param filename, a String
     */
    public void loadDictionary(String filename) {
        FileReader in = null;
        try {
            in = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        // read in equivalence classes as lines
        BufferedReader equivIn = new BufferedReader(in);
        try {
            while (equivIn.ready()) {
                String[] newArray = equivIn.readLine().split(" ");
                if (newArray.length != 0) {
                    ArrayList<String> newClass = new ArrayList();
                    newClass.addAll(Arrays.asList(newArray));
                    dict.add(newClass);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**checkEquivalences / 1
     * Takes in a string for a quality and checks its equivalence classes for 
     * an appropriate class.
     * 
     * @param quality: a String describing a chord quality
     * @return an ArrayList of possibly qualities equivalent to quality, 
     * including quality itself
     */
    public ArrayList<String> checkEquivalence(String quality)
    {
        ListIterator iter = dict.listIterator();
        while (iter.hasNext()) {
            ArrayList<String> currentClass = (ArrayList<String>)iter.next();
            if (currentClass.contains(quality)) {
                return currentClass;
            }
        }
        ArrayList<String> noequiv = new ArrayList<String>();
        noequiv.add(quality);
        return noequiv;
    }
    
}
