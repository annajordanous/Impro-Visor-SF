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



public class BinaryProduction {
    
    public static final int TOTAL_SEMITONES = 12;
    // BRICK_COSTS in order: 
    public static final int NONBRICK = 1000;
    public static final int CADENCE = 30;
    public static final int APPROACH = 45;
    public static final int DROPBACK = 30;
    public static final int TURNAROUND = 20;
    public static final int LAUNCHER = 30;
    public static final int ONOFF = 55;
    public static final int MISC = 40;
    public static final long NC = -1;
    
    
    // The block is assumed to be in the key of C, represented as the long 0.
    private String head;        // the header symbol of the rule
    private String type;        // the type of brick the rule describes
    private long key1;          // the first symbol's key in a C-based block
    private long key2;          // the second symbol's key in a C-based block
    private String name1;       // the first symbol itself, a quality or brick
    private String name2;       // the second symbol itself, a quality or brick
    private long dur1;          // the relative duration of the first symbol
    private long dur2;          // the relative duration of the second symbol
    private int cost;           // how much the header brick costs
    private String mode = "";   // the mode of the brick in the production
    private boolean toPrint;    // whether the brick is a user-side viewable one
    
    /** String Constructor (deprecated)
     * Takes in a String with all the information for a Binary rule and splits
     * it into its constituent components
     * 
     * @param s, a String
     */
    public BinaryProduction(String s) {
        String[] rule = s.split(" ", 8);
        head = rule[0];
        type = rule[1];
        key1 = Integer.parseInt(rule[2]);
        name1 = rule[3];
        dur1 = Long.parseLong(rule[4]);
        key2 = Integer.parseInt(rule[5]);
        name2 = rule[6];
        dur2 = Long.parseLong(rule[7]);
        cost = typeToCost(type);
        toPrint = true;
    }
    
    // NOTE: Assumes it's a production in C
    /** Binary Production / 6
     * Standard constructor based upon two blocks and production data
     * @param h, the head symbol (a String)
     * @param t, the type of production (a String)
     * @param b1, the first composing Block
     * @param b2, the second composing Block
     * @param p, whether the production results in a printable Brick
     * @param m, the mode (a String)
     */
    public BinaryProduction(String h, String t, Block b1, Block b2, boolean p,
            String m)
    {
        head = h;
        type = t;
        key1 = b1.getKey();
        dur1 = b1.getDuration();   
        if (b1 instanceof Chord) 
            name1 = ((Chord)b1).getSymbol();
        else
            name1 = b1.getName();
        
        key2 = b2.getKey();
        dur2 = b2.getDuration();
        if (b2 instanceof Chord) 
            name2 = ((Chord)b2).getSymbol();
        else
            name2 = b2.getName();
        
        toPrint = p;
        mode = m;
        cost = typeToCost(type);
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
    public BinaryProduction(String h, String t, BinaryProduction pStart, Block b, 
            boolean p, String m) {
        head = h;
        type = t;
        key1 = 0;
        name1 = pStart.getHead();
        dur1 = pStart.getDur();
        
        key2 = b.getKey();
        dur2 = b.getDuration();
        if (b instanceof Chord) {
            name2 = ((Chord)b).getSymbol();
        }
        else {
            name2 = b.getName();
        }
        
        toPrint = p;
        mode = m;
        cost = typeToCost(type);
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
        return key1 + " " + name1 + " " + dur1 + " " +
               key2 + " " + name2 + " " + dur2 + " " + cost;
    }
    
    // Getters for BinaryProductions.
    public int getCost() {
        return cost;
    }
    
    public long getDur() {
        return dur1 + dur2;
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
     * @return an int representing the difference between the two chords (-1 if
     * failed, otherwise 0 through 11)
     */
    public long checkProduction(TreeNode a, TreeNode b) 
    {
        
        if (a.getKey() != NC && b.getKey() != NC && !(a.isSectionEnd()) &&
                !(type.equals("On-Off") && (a.isSub() || b.isSub())) &&
                modKeys(key2 - key1) == modKeys(b.getKey() - a.getKey()) &&
                a.getSymbol().equals(name1) && b.getSymbol().equals(name2))   
            return modKeys(b.getKey() - key2);
        
        // in the event that the production is incorrect (most of the time)
        return -1;
    }
    
    // Helper function - returns i mod 12 and assures it is be positive
    private long modKeys(long i) {
        return (i + TOTAL_SEMITONES)%TOTAL_SEMITONES;
    }
            
    
    private int typeToCost(String s) {
        if (s.equals("Cadence"))
            return CADENCE;
        else if (s.equals("Approach"))
            return APPROACH;
        else if (s.equals("Dropback"))
            return DROPBACK;
        else if (s.equals("Turnaround"))
            return TURNAROUND;
        else if (s.equals("Launcher"))
            return LAUNCHER;
        else if (s.equals("On-Off"))
            return ONOFF;
        else if (s.equals("Misc"))
            return MISC;
        else return NONBRICK;
    }
    
}
