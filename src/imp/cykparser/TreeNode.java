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

/** 
 * TreeNode
 * Designed to create nodes in a parsing tree for keymapping
 * 
 * @Author Xanda Schofield
 */

import java.util.*;
import imp.brickdictionary.*;

/** TreeNode
 *
 * A class designed to track possible parse trees while running CYK. TreeNodes
 * will be listed in a CYK table, where each node will represent a nonterminal
 * and have children leading to other nonterminals.
 */
public class TreeNode {
    
    // An int cost for a default chord
    public static final double CHORD_COST = 1000;
    public static final double CHORD_SUB_COST = 1050;
    public static final double OVERLAP_COST = 5;
    
    // Constructors for TreeNodes
    
    /** Default Constructor / 0
     * 
     * Constructs an empty TreeNode
     */
    public TreeNode()
    {
        child1 = null;
        child2 = null;
        symbol = null;
        block = null;
        chords = new ArrayList<ChordBlock>();
        cost = Double.POSITIVE_INFINITY; // To ensure it isn't added to a parse
        mode = null;
        key = -1;
        toPrint = false;
        isEnd = false;
        start = 0;
        isSub = false;
        height = 0;
    }
    
    /** Chord Constructor / 2
     * Takes in a chord and makes a default chord-based TreeNode
     * 
     * @param chord, a Chord
     * @param s, the start position in a piece
     */
    public TreeNode(ChordBlock chord, long s)
    {
        child1 = null;
        child2 = null;
        block = chord;
        mode = chord.getMode();
        chords = new ArrayList<ChordBlock>();
        chords.add(chord);
        key = block.getKey();
        symbol = chord.getSymbol();
        cost = CHORD_COST;
        toPrint = true;
        isEnd = chord.isSectionEnd();
        start = s;
        isSub = false;
        height = 0;
    }
    
    /** Chord Constructor / 3
     * Takes in a chord and makes a TreeNode with cost c
     * 
     * @param chord, a Chord
     * @param c, a long describing the Chord's cost
     * @param s, the start position in a piece
     */
        public TreeNode(ChordBlock chord, double c, long s)
    {
        child1 = null;
        child2 = null;
        block = chord;
        mode = chord.getMode();
        chords = new ArrayList<ChordBlock>();
        chords.add(chord);
        key = block.getKey();
        symbol = chord.getSymbol();
        cost = c;
        toPrint = true;
        isEnd = chord.isSectionEnd();
        start = s;
        isSub = false;
        height = 0;
    }
    
        
    public TreeNode(Brick brick, ArrayList<ChordBlock> newChords, double c, long s)
    {
        child1 = null;
        child2 = null;
        block = brick;
        mode = brick.getMode();
        chords = newChords;
        key = brick.getKey();
        symbol = brick.getSymbol();
        cost = c;
        toPrint = !(brick.getType().equals(CYKParser.INVISIBLE));
        isEnd = brick.isSectionEnd();
        start = s;
        isSub = false;
        height = 0;
    }
    /** Chord Constructor / 5
     * Takes in a chord and makes a TreeNode with cost c, but for a substituted
     * chord name and root
     * 
     * @param name, a String describing the replacement chord quality
     * @param k, a long describing the replacement chord key
     * @param chord, a Chord
     * @param c, a long describing the Chord's cost
     */
        public TreeNode(String name, long k, ChordBlock chord, long s)
    {
        child1 = null;
        child2 = null;
        block = chord;
        mode = chord.getMode();
        chords = new ArrayList<ChordBlock>();
        chords.add(chord);
        key = k;
        symbol = name;
        cost = CHORD_SUB_COST;
        toPrint = true;
        isEnd = chord.isSectionEnd();
        start = s;
        isSub = true;
        height = 0;
    }    
    
        
    /** TreeNode / 6
     * Makes a TreeNode for a unary brick
     *
     * @param sym, a String of the Node symbol
     * @param type, a String of the Node's type
     * @param m, a String describing the Node's mode
     * @param t, the TreeNode whose block is the center of this TreeNode
     * @param co, the cost
     * @param k, the key of the block
     */
    public TreeNode(String sym, String type, String m, 
                    TreeNode t, double co, long k)
    {
        child1 = null;
        child2 = null;
        symbol = sym;
        mode = m;
        
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        subBlocks.addAll(t.getBlocks());
        
        chords = new ArrayList<ChordBlock>();
        chords.addAll(t.getChords());
        
        block = new Brick(sym, k, type, subBlocks, m);
        key = k;

        cost = co;
        toPrint = !(type.equals(CYKParser.INVISIBLE));

        isEnd = t.isSectionEnd();
        start = t.getStart();
        height = t.getHeight() + 1;
    }
    
    
    
    /** TreeNode / 7
     * Makes a TreeNode for a nonterminal
     *
     * @param sym, a String of the Node symbol
     * @param type, a String of the Node's type
     * @param m, a String describing the Node's mode
     * @param c1, the first child TreeNode
     * @param c2, the second child TreeNode
     * @param co, the cost
     * @param k, the key of the block
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
        
        chords = new ArrayList<ChordBlock>();
        chords.addAll(c1.getChords());
        chords.addAll(c2.getChords());
        
        block = new Brick(sym, k, type, subBlocks, m);
        key = k;

        cost = co;
        toPrint = !(type.equals(CYKParser.INVISIBLE));

        isEnd = c2.isSectionEnd();
        isSub = (c1.isSub() || c2.isSub());

        start = c1.getStart();
        if (c1.getHeight() < c2.getHeight())
            height = c2.getHeight();
        else
            height = c1.getHeight();
        height++;
    }
    
    
    /** Nonterminal Constructor / 2
     * Creates a TreeNode whose name won't be printed for the purposes
     * of assembling other named TreeNodes.
     * 
     * @param c1: the first child TreeNode
     * @param c2: the second child TreeNode
     */
    
    public TreeNode(TreeNode c1, TreeNode c2)
    {
        child1 = c1;
        child2 = c2;
        symbol = null;
        mode = "";
        
        chords = new ArrayList<ChordBlock>();
        chords.addAll(c1.getChords());
        chords.addAll(c2.getChords());
        
        ArrayList<Block> subBlocks = new ArrayList<Block>();
        subBlocks.addAll(c1.getBlocks());
        subBlocks.addAll(c2.getBlocks());
        
        block = new Brick("", c2.getKey(), "", subBlocks, "");
        key = c2.getKey();
        
        cost = child1.getCost() + child2.getCost();
        toPrint = false;
        isEnd = c2.isSectionEnd();
        isSub = (c1.isSub() || c2.isSub());

        start = c1.getStart();
        if (c1.getHeight() < c2.getHeight())
            height = c2.getHeight();
        else
            height = c1.getHeight();
        height++;
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
            if (block instanceof ChordBlock) {
                ChordBlock overlapChord = new ChordBlock(block.getName(), 0, 
                                                         block.isSectionEnd());
                newNode = new TreeNode(overlapChord, cost + 5, start);
            }
            else {
                ArrayList<ChordBlock> newChords = new ArrayList<ChordBlock>();
                newChords.addAll(block.flattenBlock());
                ChordBlock lastChord = 
                        (ChordBlock)newChords.remove(newChords.size() - 1);
                ChordBlock zeroChord = new ChordBlock(lastChord.getName(), 0, 
                                                      block.isSectionEnd());
                newChords.add(zeroChord);
                ArrayList<Block> newBlocks = new ArrayList<Block>();
                newBlocks.addAll(newChords);
                Brick newBrick = new Brick(block.getName(), ((Brick)block).getQualifier(),
                        block.getKey(), block.getType(), newBlocks, block.getMode());
                newNode = new TreeNode(newBrick, newChords, cost + 5, start);
            }
        }
        else {
            newNode = new TreeNode(symbol, block.getType(), mode, child1, 
                    child2.overlapCopy(), cost + OVERLAP_COST + 5, block.getKey());
        }
        return newNode;
    }
    
    // Booleans to test a TreeNode
    public boolean isOverlap()
    {
        if (child2 == null)
            return block.isOverlap();
        return child2.isOverlap();
    }
    
    public boolean isSub()
    {
        return isSub;
    }
    
    public boolean isTerminal()
    {
        return (child1 == null);
    }
    
    public boolean isSectionEnd()
    {
        return isEnd;
    }
    
    public boolean toShow()
    {
        return toPrint;
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
    
    public String getTrimmedSymbol()
    {
        String trimmedSymbol = symbol.replace(" with Overrun", "");
        trimmedSymbol = trimmedSymbol.replace(" with Dropback", "");
        return trimmedSymbol;
    }
    
    public String getMode()
    {
        return mode;
    }
    
    public ArrayList<ChordBlock> getChords()
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
    
    public Block getBlock() {
        return block;
    }
    
    public int getHeight() {
        return height;
    }
    
    // Gets the significant types of blocks from a TreeNode.
    public ArrayList<Block> getBlocks()
    {
        ArrayList<Block> blocks = new ArrayList<Block>();
        if (toPrint) blocks.add(block);
        else if (child1 != null && child2 != null) {
            blocks.addAll(child1.getBlocks());
            blocks.addAll(child2.getBlocks());
        }
        else
            blocks.addAll(block.flattenBlock());
        return blocks;
        
    }
    
    // Setter for a TreeNode
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
    private TreeNode child1;              // First nonterminal from a tree
    private TreeNode child2;              // Second nonterminal from a tree
    private String symbol;                // Nonterminal symbol of current node
    private ArrayList<ChordBlock> chords; // Chords contained within the node
    private Block block;              // The structure holding all of the
                                      // TreeNode's contents
    private long key;                 // the key of the Node's nominal contents
    private double cost;              // Value of the top-level block
    private boolean toPrint;          // Whether the brick name will print
    private long start;               // The start position of the first brick
    private boolean isEnd;            // If the node ends a section
    private String mode;              // the mode of the Node's nominal contents
    private boolean isSub;            // whether the Node has a substitution
    
    private int height;               // height of the tree at the TreeNode
                                      

}

// A simple Comparator class to compare TreeNodes by total cost.
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

