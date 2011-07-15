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

/**
 * purpose: Object for key/mode pairs with durations -- mainly for drawing
 * @author Zachary Merritt
 */
public class KeySpan {
    private KeyMode keymode = new KeyMode();
    private long duration = 0;
    
    public KeySpan(KeyMode km, long d) {
        keymode = km;
        duration = d;
    }
    
    public KeySpan(long k, String m, long d) {
        KeyMode km = new KeyMode(k, m);
        keymode = km;
        duration = d;
    }
    
    public KeySpan() {}
    
    public long getKey() {
        return keymode.getKey();
    }
    
    public void setKey(long k) {
        keymode.setKey(k);
    }
    
    public String getMode() {
        return keymode.getMode();
    }
    
    public void setMode(String m) {
        keymode.setMode(m);
    }
    
    public long getDuration() {
        return duration;
    }
    
    public void setDuration(long d) {
        duration = d;
    }
    
    /** 
     * Returns a Polylist representation of a KeySpan.
     * @return 
     */
    
    public Polylist toPolylist()
      {
        return keymode.toPolylist().addToEnd(duration);
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
