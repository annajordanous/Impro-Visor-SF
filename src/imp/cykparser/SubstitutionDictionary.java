/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

import imp.brickdictionary.*;
import java.util.LinkedList;

/** SubstitutionDictionary
 * A structure handling unidirectional substitution rules as UnaryProductions.
 * 
 * @author ImproVisor
 */
public class SubstitutionDictionary {
    // The list of rules in the dictionary, subList, is the only data member
    private LinkedList<UnaryProduction> subList; 
    
    // Default constructor: makes an empty dictionary
    public SubstitutionDictionary()
    {
        subList = new LinkedList<UnaryProduction>();
    }
    
    /** addRule / 1
     * Adds a given UnaryProduction to the list of rules.
     * 
     * @param u, a UnaryProduction describing a set of unidirectional 
     *           substitutions
     */
    public void addRule(UnaryProduction u) {
        subList.add(u);
    }
    
    /** checkSubstitution / 1
     * Determines the possible chords that a given chord could replace
     * 
     * @param c, a Chord
     * @return a SubstituteList of all the chords that c could have replaced
     *         in a chord brick
     */
    public SubstituteList checkSubstitution(Chord c) {
        SubstituteList subs = new SubstituteList();
        for (UnaryProduction u : subList) {
            subs.addAll(u.checkSubstitution(c));
        }
        return subs;
    }  
}
