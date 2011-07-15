/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

/** UnaryProduction
 * Production class meant to deal with finding terminal symbols corresponding
 * to a given chord. Used to deal with chord substitutions.
 * @author ImproVisor
 */

import imp.brickdictionary.*;
import java.util.ArrayList;
import polya.*;

public class UnaryProduction {
    
    // Constants for use
    public static final int NODUR = 0;
   
    // Data members
    private ChordBlock head;                     // the chord to replace
    private ArrayList<ChordBlock> terminals;     // the substitute chords possible
    
    /** Constructor / 2
     * Makes a UnaryProduction based on a PolyList describing a substitution
     * @param h
     * @param contents 
     */
    UnaryProduction(String h, Polylist contents)
    {
        head = new ChordBlock(h, NODUR);
        
        // Each chord following the first one is read in as a subsitution
        terminals = new ArrayList<ChordBlock>(); 
        ChordBlock newChord;
        while (contents.nonEmpty()) {
            newChord = new ChordBlock(contents.first().toString(), NODUR);
            terminals.add(newChord);
            contents = contents.rest();
        }
    }

    // Getters
    public String getHead() {
        return head.toString();
    }
    
    public String getBody() {
        return terminals.toString();
    }
    
    /** checkSubstitution / 1
     * Checks a given chord against the substitution rule to see if could replace
     * the head.
     * 
     * @param c, a Chord which may or may not be a substitute for the head
     * @return a SubstituteList containing either the head - if the head could
     *         have c as a substitute - or no chords at all.
     */
    public SubstituteList checkSubstitution(ChordBlock c) {
        SubstituteList subs = new SubstituteList();
        
        long diff;
        for (ChordBlock sub : terminals) {
            diff = sub.matches(c);
            if (diff >= 0) {
                subs.add(head, diff);
            }
        }
        
        return subs;
    }
      
}
