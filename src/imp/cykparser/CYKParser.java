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
import java.util.*;
import imp.brickdictionary.*;
import imp.util.ErrorLog;
import java.io.*;
import polya.*;

/** CYKParser
 * 
 * Parses a sequence of chords given chords and durations using
 * the CYK algorithm
 * 
 * @author Xanda Schofield
 */



public class CYKParser
{

    // Useful constants for the length of a bar and the load file for the
    // equivalence dictionary
    public static final int BAR_DURATION = 480;
    public static final String DICTIONARY_NAME = "vocab/substitutions_sameroot.txt";
    public static final String NONBRICK = "";
    /**
     * Data Members
     */
    private ArrayList<ChordBlock> chords;         // list of chords
    
    private LinkedList<TreeNode>[][] cykTable; // table for CYK analysis
    private boolean tableFilled;               // test for full table
    
    // Terminal and Nonterminal grammar rules will be imported as lists of
    // strings. These will serve in separate phases of the parsing.
    
    // Nonterminal rules will have the form:
    //    (startSymbol, endSymbol1, endSymbol2)
    private LinkedList<BinaryProduction> nonterminalRules;
    
    // 
    private EquivalenceDictionary edict;
    private SubstitutionDictionary sdict;
    
    /**
     * CYKParser / 0
     * The default CYKParser constructor; initializes it to an empty chord
     * sequence.
     */
    public CYKParser()
    {
        chords = new ArrayList<ChordBlock>();
        cykTable = null;
        nonterminalRules = new LinkedList<BinaryProduction>();
        tableFilled = false;
        edict = new EquivalenceDictionary();
        sdict = new SubstitutionDictionary();
        loadDictionaries(DICTIONARY_NAME);
    }
    
    /**
     * CYKParser / 2
     * A CYKParser built with chords and durations
     * @param c: chords, an ArrayList of Strings
     * @param d: durations, an ArrayList of Integers (the int wrapper class)
     */
    public CYKParser(ArrayList<ChordBlock> c)
    {
        chords = new ArrayList<ChordBlock>();
        chords.addAll(c);
        int size = chords.size();
        tableFilled = false;
        
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
        nonterminalRules = new LinkedList<BinaryProduction>();
        
        edict = new EquivalenceDictionary();
        sdict = new SubstitutionDictionary();
        loadDictionaries(DICTIONARY_NAME);
    }
    
    /** newChords
     * newChords allows you to change the chords that a CYKParser is analyzing
     * @param c: chords to replace those currently in a parser
     * @param d: durations to correspond with the chords in c
     */
    
    public void newChords(ArrayList<ChordBlock> c)
    {
        int size = c.size();
        
        chords.clear();
        chords.addAll(c);
        tableFilled = false;
        
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
    }
    
    private void loadDictionaries(String filename)
    {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);
            Tokenizer in = new Tokenizer(fis);
            in.slashSlashComments(true);
            in.slashStarComments(true);
            Object token;

            // Read in S expressions until end of file is reached
            while ((token = in.nextSexp()) != Tokenizer.eof)
            {
                if (token instanceof Polylist) 
                {
                    Polylist contents = (Polylist)token;
                    
                    // Check that polylist has enough fields to be a brick
                    // Needs BlockType (i.e. "Brick"), name, key, and contents
                    if (contents.length() < 3)
                    {
                        ErrorLog.log(ErrorLog.SEVERE, "Improper substitution "
                                + "dictionary format", true);
                    }
                    else
                    {
                        String eqCategory = contents.first().toString();
                        contents = contents.rest();
                        
                        // For reading in dictionary, should only encounter bricks
                        if (eqCategory.equals("sub"))
                        {
                            String head = contents.first().toString();
                            contents = contents.rest();
                            UnaryProduction newSub = 
                                    new UnaryProduction(head, contents);
                            sdict.addRule(newSub);
                        }
                        else if (eqCategory.equals("equiv"))
                        {
                            ArrayList<ChordBlock> newEq = new ArrayList<ChordBlock>();
                            while (contents.nonEmpty())
                            {
                                String chordName = contents.first().toString();
                                contents = contents.rest();
                                ChordBlock nextChord = new ChordBlock(chordName, 
                                                        UnaryProduction.NODUR);
                                newEq.add(nextChord);
                            }
                            edict.addRule(newEq);
                        }
                        else
                        {
                            ErrorLog.log(ErrorLog.WARNING, 
                                    "Incorrect rule type " + eqCategory, true);
                        }
                    }
                }
                else
                {
                    ErrorLog.log(ErrorLog.WARNING, "Improper formatting for "
                            + "a token " + token.toString(), true);
                }
            }
        } catch (FileNotFoundException ex) {
            ErrorLog.log(ErrorLog.SEVERE, "Substitution dictionary not found", 
                    true);
        } finally {
            try {
                fis.close();
            } catch (IOException ex) {
                ErrorLog.log(ErrorLog.FATAL, "Filestream cannot close", true);
            }
        }
        
    }
    
    /** createRules
     * Creates binary productions for a BrickLibrary and loads them in to the
     * CYKParser's rule list
     * @param lib: a BrickLibrary
     */
    public void createRules(BrickLibrary lib) {
        Collection<Brick> bricks = lib.getMap();
        Iterator bIter = bricks.iterator();
        while (bIter.hasNext()) {
            Brick b = (Brick)bIter.next();
            String name = b.getName();

            // Rule parsing of rules is dependent on the number of subBlocks
            // in a given brick.
            ArrayList<Block> subBlocks = b.getSubBlocks();
            String currentName;
            int size = subBlocks.size();
            String mode = b.getMode();
            
            // Error case: a Brick contains one or fewer subBlocks. Rather than 
            // dealing with unary production rules for bricks, we take in chords
            // as terminals and use only binary production rules for everything
            // else. Any brick composed of one or fewer subBricks should not 
            // be used.
            if (size < 2)
                ErrorLog.log(ErrorLog.WARNING, "Error: brick of size " + size, 
                        true);
                    
            // Perfect case: a Brick of two subBricks. Only one rule is needed,
            // a BinaryProduction with the name of the resulting brick as its 
            // head and each of the subBricks in its body.
            else if (size == 2) {
                BinaryProduction p = new BinaryProduction(name, b.getType(), 
                                         b.getKey(), subBlocks.get(0), 
                                         subBlocks.get(1), true, mode, lib);
                    nonterminalRules.add(p);
            }
            
            // Larger case: a Brick of three or more subBricks. The Brick gets
            // divided into a starting Brick whose name won't be printed in a
            // parse tree. Each following rule will have two body blocks, one
            // being the name of the previous production and one being the next
            // subBlock. The last rule gets the name of the final Brick.
            else {
                // first rule
                currentName = name + "1";
                BinaryProduction[] prods = new BinaryProduction[size];
                prods[0] = new BinaryProduction(currentName, NONBRICK, b.getKey(),
                        subBlocks.get(0), subBlocks.get(1), false, mode, lib);
                nonterminalRules.add(prods[0]);
                // second through next to last rules
                for (int i = 2; i < size - 1; i++) {
                    currentName = name + i;
                    prods[i-1] = new BinaryProduction(currentName, NONBRICK,
                            b.getKey(), prods[i-2], subBlocks.get(i), false, mode, lib);
                    nonterminalRules.add(prods[i-1]);
                }
                // final rule
                prods[size-2] = new BinaryProduction(name, b.getType(), b.getKey(),
                        prods[size-3], subBlocks.get(size-1), true, mode, lib);
                nonterminalRules.add(prods[size-2]);
            }
        
        }
    }
    
    /** fillTable
     * fillTable takes the chord sequence and duration lists and uses them to 
     * fill in the CYK algorithm table. First, it fills in the diagonal
     * with all the possible symbols which could produce the given terminals
     * with that diagonal's index; then, it constructs each level up by finding
     * pairwise combinations of terminals to assemble larger portions of the
     * chord sequence into one symbol.
     */
    public void fillTable()
    {
        // Create nodes for the terminals, and put them into the table.
        int size = this.chords.size();
        long currentStart = 0;
        for (int i=0; i < size; i++) {
            findTerminal(i, currentStart);
            currentStart += chords.get(i).getDuration();
        }
        
        // Iterate through the table by degrees parallel to the diagonal.
        // We use the column where each diagonal starts to determine where
        // the next cell is. We start at 1 because the 0-column diagonal
        // is all ready by the previous step.
        for (int startCol = 1; startCol < size; startCol++) 
        {
            for (int startRow = 0; startRow < (size - startCol); 
                     startRow++) 
            {
                findNonterminal(startRow, startCol+startRow);
            }
        }
        
        tableFilled = true;
    }
    
    /** findSolutions
     * findSolutions takes a filled-in table in a CYKParser and returns the 
     * best parse tree (the lowest cost one) as a list of Bricks.
     */
    public ArrayList<Block> findSolution(BrickLibrary lib) {
        
        // First, we assemble the table of minimum-value nodes.
        assert(tableFilled);
        int size = this.chords.size();
        TreeNode[][] minVals = new TreeNode[size][size];
        


        // This loops through every occupied cell in the cykTable and finds 
        // the lowest-cost Node in the list, for the construction of optimal
        // trees later.
        for (int row = 0; row < size; row++) {
            for (int col = row; col < size; col++) {
                
                    minVals[row][col] = new TreeNode();

                    ListIterator node = cykTable[row][col].listIterator();
                    while (node.hasNext()) {
                        TreeNode nextNode = (TreeNode) node.next();
                    if (nextNode.toShow() &&
                            nextNode.getCost() < minVals[row][col].getCost())
                            minVals[row][col] = nextNode;
                 
                    }
                }
            }
        
        
        // This is a cost-minimization algorithm looking for the lowest cost
        // way to account for the chords in order with possible brick parses.
        for (int i = size - 2; i >= 0; i--) {
            for (int j = i + 1; j < size; j++) {
                for (int k = i + 1; k <= j; k++) {
                    if (minVals[i][k-1].getCost() + minVals[k][j].getCost() 
                            < minVals[i][j].getCost()){
                        minVals[i][j] = new TreeNode(minVals[i][k-1], minVals[k][j]);
                    }
                }
                    
            }
        }
    
        // The shortest path in the top right cell gets printed as the best
        // explanation for the whole chord progression
        return PostProcessing.findLaunchers(minVals[0][size - 1].toBlocks());
            
    }
    
    public String printTable() {
        String output = new String();
        
        for (int i = 0; i < cykTable.length; i++)
            for (int j = i; j < cykTable.length; j++)
            {
                output += "(" + i + ", " + j + ")\n";
                for (TreeNode t : cykTable[i][j])
                {
                    output += t.getSymbol() + " in " 
                            + BrickLibrary.keyNumToName(t.getKey()) + " ";
                    if (!t.isTerminal())
                        output += t.getFirstChild().getSymbol() + " in " + 
                                  BrickLibrary.keyNumToName(t.getFirstChild().getKey()) 
                                  + ", " + t.getSecondChild().getSymbol() + " in " +
                                  BrickLibrary.keyNumToName(t.getSecondChild().getKey());
                    
                    output += "\n";
                }         
                output += "----------------------\n";
            }
        return output;
    }
    
    /** findTerminal
     * findTerminal is a helper function which, for a given index i takes the
     * ith chord and ith duration and fills the [i, i] space in the 2D List 
     * array with the symbols which could generate that chord.
     */
    private void findTerminal(int index, long start)
    {
        // First, the table cell is initialized and the original terminal placed
        // in it as a TreeNode.
        cykTable[index][index] = new LinkedList<TreeNode>();
        ChordBlock currentChord = chords.get(index);
        TreeNode currentNode = new TreeNode(currentChord, start);
        cykTable[index][index].add(currentNode);
        
        
        // Then, every chord which the terminal could be substituted for is also
        // added to that same cell as a TreeNode with the original Chord but a
        // different name for the TreeNode.
        SubstituteList subs = edict.checkEquivalence(currentChord);
        subs.addAll(sdict.checkSubstitution(currentChord));
        for (int i = 0; i < subs.length(); i++)
        {
            currentNode = new TreeNode(subs.getName(i), subs.getKey(i),
                                                currentChord, start);
            cykTable[index][index].add(currentNode);
    }
    }
    
    /** findNonterminal
     * findNontermimal looks at every single possible combination of 
     * nonterminals from previously filled cells and sees if there is a 
     * production which will generate those two nonterminals. If so, it adds
     * that new single symbol to the current cell as a TreeNode.
     * @param row: the row number in the table currently being filled
     * @param col: the column number in the table currently being filled
     */
    private void findNonterminal(int row, int col)
    {

        cykTable[row][col] = new LinkedList<TreeNode>();
        
        LinkedList<TreeNode> overlaps = new LinkedList<TreeNode>();
        
        // We make sure that the code loops through the different possible cell
        // pairs, starting at the leftmost and topmost.
        for(int index = 0; index < (col - row); index++) {
            assert(row+index < this.chords.size());
            
            ListIterator iter1 = cykTable[row][row+index].listIterator();
            
            while(iter1.hasNext()) {
                TreeNode symbol1 = (TreeNode)iter1.next();
                
                ListIterator iter2 = cykTable[row+index+1][col].listIterator();
            
                while(iter2.hasNext()) {
                    TreeNode symbol2 = (TreeNode)iter2.next();
                    
                    ListIterator iterRule = nonterminalRules.listIterator();

                    // We check every rule against each pair of symbols.
                    while (iterRule.hasNext()) { 
                        BinaryProduction rule = 
                                (BinaryProduction) iterRule.next();
                        
                        long newKey = rule.checkProduction(symbol1, 
                                                           symbol2);
                        // If newKey comes up with an appropriate key distance,
                        // make a new TreeNode for the current two TreeNodes.
                        if (!(newKey < 0)) {
                            
                            // The cost becomes larger for the final TreeNode if
                            // either the first or second TreeNode uses a chord
                            // substitute
                            long cost = rule.getCost();
                            if (symbol1.isSub())
                                cost += 5;
                            if (symbol2.isSub())
                                cost += 5;
                            if (symbol2.isOverlap())
                                cost += TreeNode.OVERLAP_COST;
                            
                            TreeNode newNode = new TreeNode(rule.getHead(),
                                    rule.getType(), rule.getMode(), 
                                    symbol1, symbol2, cost, newKey);
                            cykTable[row][col].add(newNode);
                            
                            // Additionally, if this block could overlap with 
                            // another later one, then we store a TreeNode 
                            // with a 0-duration final chord to put in the 
                            // table later.
                            
                            if (!(rule.getType().equals("On-Off")) && 
                                    !(symbol2.isSectionEnd()) &&
                                    !(symbol2.isOverlap()))
                                overlaps.add(newNode.overlapCopy());
                        }
                    }
                }
            }
        }
        // TreeNodes in overlaps, due to the zero duration of the last chord, 
        // justify one fewer chords than those in [row][col]. They are placed
        // one cell to the left.
        cykTable[row][col-1].addAll(overlaps);
    }
    /** parse / 2
     * A method taking in blocks and a BrickLibrary and parsing them, returning
     * the parsed version.
     * @param blocks: an ArrayList of Blocks to be parsed
     * @param lib: a BrickLibrary of Bricks to guide parsing
     * @return parsed chords as bricks
     */
    public ArrayList<Block> parse(ArrayList<Block> blocks, BrickLibrary lib) {
        
        // Load in chords
        ArrayList<ChordBlock> ch = new ArrayList<ChordBlock>();
        for (Block b: blocks)
            ch.addAll(b.flattenBlock());
        newChords(ch);
        
        // Read in rules and parse
        createRules(lib);
        fillTable();
        return findSolution(lib);
    }
}