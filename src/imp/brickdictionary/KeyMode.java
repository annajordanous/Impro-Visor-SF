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

import polya.Polylist;
import static polya.Polylist.list;

/**
 * purpose: Object for key/mode pairs corresponding to blocks
 * @author Zachary Merritt
 */
public class KeyMode {
    
    private long key = -1;
    private String mode = "";
    
    public KeyMode(long k, String m) {
        key = k;
        mode = m;
    }
    
    public KeyMode() {}
    
    public long getKey() {
        return key;
    }
    
    public void setKey(long k) {
        key = k;
    }
    
    public String getMode() {
        return mode;
    }
    
    public void setMode(String m) {
        mode = m;
    }
    
    /** 
     * Returns a Polylist representation of a KeyMode.
     * @return 
     */
    
    public Polylist toPolylist()
      {
        return list(mode, key);
      }
    
    /** 
     * Returns a String representation of a KaySpan.
     * @return 
     */
    
    public String toString()
      {
        return toPolylist().toString();
      }

}
