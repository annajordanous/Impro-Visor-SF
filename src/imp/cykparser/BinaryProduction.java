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

/** BinaryProduction
 *A production rule for a brick music grammar with two nonterminals as the body.
 * 
 * @author Xanda Schofield
 */



public class BinaryProduction extends AbstractProduction {
    
    public static final int TOTAL_SEMITONES = 12;
    // BRICK_COSTS in order: 
    public static final long NC = -1;
    
    
    // All production rules are put into the key of C.
    private String head;        // the header symbol of the rule
    private String type;        // the type of brick the rule describes
    private long key1;          // the first symbol's key in a C-based block
    private long key2;          // the second symbol's key in a C-based block
    private String name1;       // the first symbol itself, a quality or brick
    private String name2;       // the second symbol itself, a quality or brick
    private long cost;           // how much the header brick costs
    private String mode = "";   // the mode of the brick in the production
    private boolean toPrint;    // whether the brick is a user-side viewable one
    
    
    /** Binary Production / 6
     * Standard constructor based upon two blocks and production data
     * @param h, the head symbol (a String)
     * @param t, the type of production (a String)
     * @param b1, the first composing Block
     * @param b2, the second composing Block
     * @param p, whether the production results in a printable Brick
     * @param m, the mode (a String)
     * 
     * Note: this assumes that a production is relative to C.
     */
    public BinaryProduction(String h, String t, long k, Block b1, Block b2, boolean p,
            String m, BrickLibrary bricks)
    {
        head = h;
        type = t;
        key1 = modKeys(b1.getKey() - k);
        if (b1 instanceof ChordBlock) 
            name1 = ((ChordBlock)b1).getSymbol();
        else
            name1 = b1.getName();
        
        key2 = modKeys(b2.getKey() - k);
        if (b2 instanceof ChordBlock) 
            name2 = ((ChordBlock)b2).getSymbol();
        else
            name2 = b2.getName();
        
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
    }
    
    /** BinaryProduction / 6
     * An alternate constructor for building off of previously created productions
     * 
     * @param h, the head symbol (a String)
     * @param t, the type of brick (a String)
     * @param pStart, the first composing production (a BinaryProduction)
     * @param b, the Block to add in this production
     * @param p, whether this brick is printable
     * @param m, the mode of the brick (a String)
     */
    public BinaryProduction(String h, String t, long k, BinaryProduction pStart, 
            Block b, boolean p, String m, BrickLibrary bricks) {
        head = h;
        type = t;
        key1 = 0;
        name1 = pStart.getHead();
        
        key2 = modKeys(b.getKey() - k);
        if (b instanceof ChordBlock) {
            name2 = ((ChordBlock)b).getSymbol();
        }
        else {
            name2 = b.getName();
        }
        
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
    }
    /** getHead
     * Returns the header symbol for the production
     * @return a String of the head
     */
    public String getHead() {
        return head;
    }
    
    /** getBody
     * Returns the reconstructed body of the rule
     * @return a String of the body  
     */
    public String getBody() {
        return key1 + " " + name1 + " " + key2 + " " + name2 + " " + cost;
    }
    
    // Getters for BinaryProductions.
    public long getCost() {
        return cost;
    }
    
    public String getType() {
        return type;
    }
    
    public String getMode() {
        return mode;
    }
    /** checkProduction
     * Tests whether a production fits with a given ordered pair of TreeNodes. 
     * If so, it returns a positive chord difference between these and the 
     * rule's original key (C). Otherwise, it returns -1.
     * 
     * @param a, the first TreeNode
     * @param b, the second TreeNode
     * @return a long representing the difference between the two chords (-1 if
     * failed, otherwise 0 through 11)
     */
    public long checkProduction(TreeNode a, TreeNode b) 
    {
        // Conditions:
        // - TreeNodes a and b must have a key
        // - a must not mark a section end (the block cannot span a section end)
        // - if the rule is an On-Off, there cannot be any substitutions
        // - the relative difference in key must be the same for the TreeNodes
        //   and the two halves of the production
        // - a and b must have names corresponding to the two composing symbols
        //   of the production
        if (a.getKey() != NC && b.getKey() != NC && !(a.isSectionEnd()) &&
                !a.isOverlap() && !b.isOverlap() &&
                modKeys(key2 - key1) == modKeys(b.getKey() - a.getKey()) &&
                a.getSymbol().equals(name1) && b.getSymbol().equals(name2))   
            return modKeys(b.getKey() - key2);
        
        // In the event that the production is incorrect (most of the time)
        return -1;
    }
    
    // Helper function - returns i mod 12 and assures it is be positive
    private long modKeys(long i) {
        return (i + TOTAL_SEMITONES)%TOTAL_SEMITONES;
    }
    
}
