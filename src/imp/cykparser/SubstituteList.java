/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

import imp.brickdictionary.Chord;
import java.util.ArrayList;

/**
 *
 * @author ImproVisor
 */
public class SubstituteList {
    private ArrayList<String> names;
    private ArrayList<Long> keys;
    
    public SubstituteList() {
        names = new ArrayList<String>();
        keys = new ArrayList<Long>();
    }
    
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
    
    public void add(Chord c, long diff) {
        names.add(c.getSymbol());
        keys.add(modKeys(c.getKey() + diff));
    }
    
    public void addAll(SubstituteList l)
    {
        names.addAll(l.getNames());
        keys.addAll(l.getKeys());
    }
    
    public boolean contains(String mode)
    {
        return names.contains(mode);
    }
    
    // Helper function - returns i mod 12 and assures it is be positive
    private long modKeys(long i) {
        return (i + BinaryProduction.TOTAL_SEMITONES)
                % BinaryProduction.TOTAL_SEMITONES;
    }
    
}
