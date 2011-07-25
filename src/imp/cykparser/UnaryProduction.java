/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;
import imp.brickdictionary.*;

/** BinaryProduction
 *A production rule for a brick music grammar with two nonterminals as the body.
 * 
 * @author Xanda Schofield
 */



public class UnaryProduction extends AbstractProduction {
    
    public static final int TOTAL_SEMITONES = 12;
    
    // The block is assumed to be in the key of C, represented as the long 0.
    private String head;        // the header symbol of the rule
    private String type;        // the type of brick the rule describes
    private long key;          // the symbol's key in a C-based block
    private long termKey;
    private String name;       // the symbol itself, a quality or brick
    private long cost;           // how much the header brick costs
    private String mode = "";   // the mode of the brick in the production
    private boolean toPrint;    // whether the brick is a user-side viewable one
    
    
    // NOTE: Assumes it's a production in C
    /** Unary Production / 6
     * Standard constructor based upon a block and production data
     * @param h, the head symbol (a String)
     * @param t, the type of production (a String)
     * @param b, the composing Block
     * @param p, whether the production results in a printable Brick
     * @param m, the mode (a String)
     */
    public UnaryProduction(String h, String t, long k, Block b, boolean p,
            String m, BrickLibrary bricks)
    {
        head = h;
        type = t;
        key = k; 
        name = b.getSymbol();
        termKey = b.getKey();
        toPrint = p;
        mode = m;
        cost = bricks.getCost(type);
    }
 
    public String getHead() {
        return head;
    }
    
    /** getBody
     * Returns the reconstructed body of the rule
     * @return a String of the body  
     */
    public String getBody() {
        return key + " " + name + " " + cost;
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
     * @param t, a TreeNode
     * @return an long representing the difference between the two chords (-1 if
     * failed, otherwise 0 through 11)
     */
    public long checkProduction(TreeNode t, 
            EquivalenceDictionary e, SubstitutionDictionary s) 
    {
        
        if (t.getSymbol().equals(name))   
                return modKeys(t.getKey() - termKey - key);
        // in the event that the production is incorrect (most of the time)
        return -1;
    }
    
    // Helper function - returns i mod 12 and assures it is be positive
    private long modKeys(long i) {
        return (i + TOTAL_SEMITONES)%TOTAL_SEMITONES;
    }
    
}
