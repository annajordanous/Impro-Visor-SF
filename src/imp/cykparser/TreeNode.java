package imp.cykparser;

/** TreeNode
 * Designed to create nodes in a parsing tree for keymapping
 * 
 * @Author Xanda Schofield
 * 
 */

import java.util.*;
import imp.brickdictionary.*;
import java.util.logging.Level;
import java.util.logging.Logger;
/** TreeNode
 *
 * A class designed to track possible parse trees while running CYK. TreeNodes
 * will be listed in a CYK table, where each node will represent a nonterminal
 * and have children leading to other nonterminals.
 */
public class TreeNode {
    
    // An int cost for a default chord
    public static final long CHORD_COST = 1000;
    
    // Constructors for TreeNodes
    
    /** Default Constructor / 0
     * 
     * Constructs an empty TreeNode
     * 
     */
    public TreeNode()
    {
        child1 = null;
        child2 = null;
        symbol = null;
        block = null;
        chords = new ArrayList<Chord>();
        cost = Double.POSITIVE_INFINITY; // To ensure it isn't added to a parse
        mode = null;
        toPrint = false;
        isEnd = false;
        start = 0;
    }
    
    /** Chord Constructor / 2
     * Takes in a chord and makes a default chord-based TreeNode
     * 
     * @param chord, a Chord
     * @param s, the start position in a piece
     */
    public TreeNode(Chord chord, long s)
    {
        child1 = null;
        child2 = null;
        block = chord;
        chords = new ArrayList<Chord>();
        chords.add(chord);
        symbol = chord.getSymbol();
        cost = CHORD_COST;
        toPrint = true;
        isEnd = chord.isSectionEnd();
        start = s;
    }
    
    /** Chord Constructor / 3
     * Takes in a chord and makes a TreeNode with cost c
     * 
     * @param chord, a Chord
     * @param c, a long describing the Chord's cost
     */
        public TreeNode(Chord chord, double c, long s)
    {
        child1 = null;
        child2 = null;
        block = chord;
        mode = chord.getMode();
        chords = new ArrayList<Chord>();
        chords.add(chord);
        symbol = chord.getSymbol();
        cost = c;
        toPrint = true;
        isEnd = chord.isSectionEnd();
        start = s;
    }
    
    /** Terminal Constructor / 4
     * 
     * Constructs a TreeNode for a terminal
     * 
     * s = symbol name, a string
     * c = chords, a list of strings
     * d = durations, a list of ints
     */
    public TreeNode(String sym, ArrayList<Chord> c, double co, long k, long s)
    {
        child1 = null;
        child2 = null;
        symbol = sym;
        mode = "";
        chords = new ArrayList<Chord>();
        chords.addAll(c);
        
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        subBlocks.addAll(c);
        
        block = new Brick(sym, k, "", subBlocks);
        cost = co;
        toPrint = true;
        isEnd = false;
        start = s;
    }
    
    /** Nonterminal Constructor / 5
     * 
     * Constructs a TreeNode for a nonterminal
     * 
     * s = symbol name, a string
     * c1 = first child TreeNode
     * c2 = second child TreeNode
     */
    public TreeNode(String sym, String type, String m, 
                    TreeNode c1, TreeNode c2, 
                    double co, long k)
    {
        child1 = c1;
        child2 = c2;
        symbol = sym;
        mode = m;
        
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        subBlocks.addAll(c1.getBlocks());
        subBlocks.addAll(c2.getBlocks());
                
        chords = new ArrayList<Chord>();
        chords.addAll(c1.getChords());
        chords.addAll(c2.getChords());

        block = new Brick(sym, k, type, subBlocks, m);

        cost = co;
        if (type.equals(""))
            toPrint = false;
        else toPrint = true;

        isEnd = c2.isSectionEnd();

        start = c1.getStart();
    }
    
    
    /** Nonterminal Constructor / 2
     * Creates a TreeNode whose name won't be printed for the purposes
     * of assembling other named TreeNodes.
     * @param c1: the first child TreeNode
     * @param c2: the second child TreeNode
     */
    
    public TreeNode(TreeNode c1, TreeNode c2)
    {
        child1 = c1;
        child2 = c2;
        symbol = null;
        mode = "";
        
        chords = new ArrayList<Chord>();
        chords.addAll(c1.getChords());
        chords.addAll(c2.getChords());
        
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        subBlocks.addAll(c1.getBlocks());
        subBlocks.addAll(c2.getBlocks());
        
        block = new Brick("", c2.getKey(), "", subBlocks, "");
        
        cost = child1.getCost() + child2.getCost();
        toPrint = false;
        isEnd = c2.isSectionEnd();
        start = c1.getStart();
    }
    
   /** overlapCopy
     * Makes a copy of a TreeNode's contents with the last chord set to duration
     * 0. Used to facilitate the creation and parsing of overlapping bricks.
     * @return a copy of a TreeNode with the last chord at 0 duration
     */
    public TreeNode overlapCopy()
    {
        TreeNode newNode;
        if (child1 == null && child2 == null) {
            Chord zeroChord = new Chord(block.getName(), 0, true);
            newNode = new TreeNode(zeroChord, cost + 5, start);
        }
        else {
            newNode = new TreeNode(symbol, block.getType(), mode, 
                    child1, child2.overlapCopy(), cost + 5, block.getKey());
        }
        return newNode;
    }
    
    // Getters for the data members of a TreeNode
    public TreeNode getFirstChild()
    {
        return child1;
    }
    
    public TreeNode getSecondChild()
    {
        return child2;
    }
    
    public String getSymbol()
    {
        return symbol;
    }
    
    public String getMode()
    {
        return mode;
    }
    
    public ArrayList<Chord> getChords()
    {
        return chords;
    }
    
    public double getCost() 
    { 
        return cost; 
    }
    
    public long getKey()
    {
        return block.getKey();
    }
    
    public long getDuration()
    {
        return block.getDuration();
    }
    
    public long getStart()
    {
        return start;
    }
    
    public boolean isTerminal()
    {
        return !(block instanceof Brick);
    }
    
    public boolean isSectionEnd()
    {
        return isEnd;
    }
    
    // Gets the significant types of blocks from a TreeNode.
    public ArrayList<Block> getBlocks()
    {
        ArrayList<Block> blocks = new ArrayList<Block>();
        if (toPrint) blocks.add(block);
        else {
            blocks.addAll(child1.getBlocks());
            blocks.addAll(child2.getBlocks());
        }
        return blocks;
        
    }
    
    // Setters for a TreeNode
    public void setChildren (TreeNode c1, TreeNode c2)
    {
        child1 = c1;
        child2 = c2;
    }
   
    /** toString()
     * Generates the string representation of the entire subtree of a
     * TreeNode recursively. It prints names based upon whether the block
     * that the TreeNode would represent the top level of is one whose name
     * should be printed, and whether or not it is a terminal.
     */
    @Override
    public String toString() 
    {
        if (!toPrint) {
            return child1.toString() + "\n" + child2.toString();
        }
        else if (isTerminal()) {
            return "(" + BrickLibrary.keyNumToName(getKey()) + symbol + " " 
                + getDuration() + ")";
        }
        else {
            return "(" + symbol + " " + BrickLibrary.keyNumToName(getKey()) + 
                " " + getDuration() + " " + child1.toString() + " " + 
                child2.toString() + ")";
        }
    }
    
    /** toBlocks()
     * Converts a TreeNode into its constituent blocks for use when the Parser
     * returns
     * 
     * @return an ArrayList of Blocks.
     */
    public ArrayList<Block> toBlocks()
    {
        ArrayList<Block> blocks = new ArrayList<Block>();
        if (!toPrint) {
            blocks.addAll(child1.toBlocks());
            blocks.addAll(child2.toBlocks());
        }
        else { // if this is a printable brick
            blocks.add(block);
        }
        return blocks;
    }
            
                

    /******************************************************************/
    // Data members for a TreeNode
    private TreeNode child1;          // First nonterminal from a tree
    private TreeNode child2;          // Second nonterminal from a tree
    private String symbol;            // Nonterminal symbol of current node
    private ArrayList<Chord> chords;  // Chords contained within the node
    private Block block;              // The structure holding all of the
                                      // TreeNode's contents
    private double cost;              // Value of the top-level block
    private boolean toPrint;          // Whether the brick name will print
    private long start;               // The start position of the first brick
    private boolean isEnd;            // If the node ends a section
    private String mode;
                                      

}

class NodeComparator implements Comparator {
    @Override
    public int compare(Object t1, Object t2) {
        double cost1 = ((TreeNode)t1).getCost();
        double cost2 = ((TreeNode)t2).getCost();
        if (cost1 < cost2)
            return -1;
        else if (cost1 > cost2)
            return 1;
        else
            return 0;
    }
            
}

