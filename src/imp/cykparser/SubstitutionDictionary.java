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
import java.util.LinkedList;

/** SubstitutionDictionary
 * A structure handling unidirectional substitution rules as UnaryProductions.
 * 
 * @author Xanda Schofield
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
    public SubstituteList checkSubstitution(ChordBlock c) {
        SubstituteList subs = new SubstituteList();
        for (UnaryProduction u : subList) {
            subs.addAll(u.checkSubstitution(c));
        }
        return subs;
    }  
}
