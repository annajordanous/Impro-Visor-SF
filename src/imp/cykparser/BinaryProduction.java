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
 * A class used to describe production rules for a brick music grammar with 
 * two nonterminals as the body of the production.
 * 
 * @author Xanda Schofield
 */



public class BinaryProduction extends AbstractProduction {
    
    // Constants //
    public static final int TOTAL_SEMITONES = 12; // the number of semitones in 
                                                  // an octave
    public static final long NC = -1;             // the key of a No Chord
    
    
    
    
    // NB: All production rules are put into the key of C when they are 
    // constructed. This does not necessitate the original Brick being written
    // in C.
    
    // Data Members //
    private String head;        // the header symbol of the rule
    private String type;        // the type of brick the rule describes
    private long key1;          // the first symbol's key in a C-based block
    private long key2;          // the second symbol's key in a C-based block
    private String name1;       // the first symbol itself, a quality or brick
    private String name2;       // the second symbol itself, a quality or brick
    private long cost;          // how much the header brick costs
    private String mode = "";   // the mode of the brick in the production
    private boolean toPrint;    // whether the brick is a user-side viewable one
    
    
    // BinaryProduction Constructors // 
    
    /** Standard constructor
     * Constructs a BinaryProduction from two blocks and circumstantial data
     * 
     * @param h, a String describing the "head" symbol, or the name of the 
     *        resulting Brick the rule produces
     * @param t, a String describing type of Brick formed
     * @param k, a long denoting the key of the resulting Brick
     * @param b1, the first Block forming the resulting Brick
     * @param b2, the second Block forming the resulting brick
     * @param p, a boolean describing whether this Brick should be seen by users
     * @param m, a String describing the mode of the Brick
     * @param bricks, a BrickLibrary
     */
    public BinaryProduction(String h, String t, long k, Block b1, Block b2, boolean p,
            String m, BrickLibrary bricks)
    {
        head = h;
        type = t;
        
        key1 = modKeys(b1.getKey() - k);
        name1 = b1.getSymbol();
        
        key2 = modKeys(b2.getKey() - k);
        name2 = b2.getSymbol();
        
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
    }
    
    /** Higher-level constructor
     * Constructs a BinaryProduction from a BinaryProduction of the first part 
     * of the Brick and a Block of the second part of the Brick.
     * 
     * @param h, a String describing the "head" symbol, or the name of the 
     *        resulting Brick the rule produces
     * @param t, a String describing type of Brick formed
     * @param k, a long denoting the key of the resulting Brick
     * @param pStart, a BinaryProduction describing the first Block of this 
     *        production's Brick
     * @param b, the second Block forming the resulting Brick
     * @param p, a boolean describing whether this Brick should be seen by users
     * @param m, a String describing the mode of the Brick
     * @param bricks, a BrickLibrary
     */
    public BinaryProduction(String h, String t, long k, BinaryProduction pStart, 
            Block b, boolean p, String m, BrickLibrary bricks) {
        head = h;
        type = t;
        key1 = 0;
        name1 = pStart.getHead();
        
        key2 = modKeys(b.getKey() - k);
        name2 = b.getSymbol();
        
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
    }
    
    // Getters for a BinaryProduction //
    
    /** getHead
     * Returns the header symbol for the production
     * @return a String of the head
     */
    public String getHead() {
        return head;
    }
    
    /** getBody
     * Returns the reconstructed body of the rule
     * @return a String of the body with form
     * "[firstBlockName] [firstBlockKey] [secondBlockName] [secondBlockKey] [cost]"
     */
    public String getBody() {
        return name1 + " " + key1 + " " + name2 + " " + key2 + " " + cost;
    }
    
    /** getCost
     * Returns the base cost for a parser to use the resulting Brick
     * @return a long of the Brick's cost
     */
    public long getCost() {
        return cost;
    }
    
    /** getType
     * Gets the type of Brick formed (e.g. "Cadence")
     * @return a String of the Brick's type
     */
    public String getType() {
        return type;
    }
    
    /** getMode
     * Gets the mode of the Brick formed (e.g. "Major")
     * @return a String of the Brick's mode
     */
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
        // - the relative difference in key must be the same for the TreeNodes
        //   and the two halves of the production
        // - a and b must have names corresponding to the two composing symbols
        //   of the production
        if (a.getKey() != NC && b.getKey() != NC &&
                modKeys(key2 - key1) == modKeys(b.getKey() - a.getKey()) &&
                (a.getSymbol().equals(name1) || a.getTrimmedSymbol().equals(name1)) && 
                (b.getSymbol().equals(name2)))
            return modKeys(b.getKey() - key2);
        
        // In the event that the production is incorrect (most of the time)
        return -1;
    }
    
    /** modKeys
     * Takes a key and assures it to be a positive number between 0 and 11.
     * @param i, a long representing a key
     * @return a long representing a key in the correct range (0 to 11)
     */
    private long modKeys(long i) {
        return (i + TOTAL_SEMITONES)%TOTAL_SEMITONES;
    }
    
    // end of BinaryProduction class
}
