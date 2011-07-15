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

import imp.brickdictionary.ChordBlock;
import java.util.ArrayList;

/** SubstituteList
 * A class describing a list of possible chords to replace a chord of a given
 * key and quality
 * 
 * @author Xanda Schofield
 */
public class SubstituteList {
    private ArrayList<String> names;
    private ArrayList<Long> keys;
    
    // Default constructor
    public SubstituteList() {
        names = new ArrayList<String>();
        keys = new ArrayList<Long>();
    }
    
    // Getters for data members and characteristics of the SubstituteList
    public ArrayList<String> getNames() {
        return names;
    }
    
    public ArrayList<Long> getKeys() {
        return keys;
    }
    
    public String getName(int i) {
        return names.get(i);
    }
    
    public long getKey(int i) {
        return keys.get(i);
    }
    
    public int length() {
        return keys.size();
    }
    
    /** add / 2
     * Adds a single chord the appropriate difference away to a SubstituteList
     * @param c, a Chord
     * @param diff, the transposition required of the Chord
     */
    public void add(ChordBlock c, long diff) {
        names.add(c.getSymbol());
        keys.add(modKeys(c.getKey() + diff));
    }
    
    /** addAll / 1
     * Adds all chords as described by a SubstituteList to itself
     * 
     * @param l, a filled in and appropriately transposed SubstituteList
     */
    public void addAll(SubstituteList l)
    {
        names.addAll(l.getNames());
        keys.addAll(l.getKeys());
    }
    
    /** contains / 1
     * Checks if a given mode is contained in the list of chords
     * 
     * @param mode, a String describing a mode (as a chord quality)
     * @return whether mode is in the list of chord qualities
     */
    public boolean contains(String mode)
    {
        return names.contains(mode);
    }
    
    // modKeys 
    // A helper function which returns i mod 12 and assures it is be positive
    private long modKeys(long i) {
        return (i + BinaryProduction.TOTAL_SEMITONES)
                % BinaryProduction.TOTAL_SEMITONES;
    }
    
}
