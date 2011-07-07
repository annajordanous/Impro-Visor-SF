/** file: CYKParser.java
 * @author Xanda Schofield
 * purpose: parsing chords using the CYK algorithm
 */
package imp.cykparser;
import java.util.*;
import imp.brickdictionary.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/** CYKParser
 * Parses a sequence of chords given chords and durations using
 * the CYK algorithm
 */



public class CYKParser
{

    // Useful constants for the length of a bar and the load file for the
    // equivalence dictionary
    public static final int BAR_DURATION = 480;
    public static final String DICTIONARY_NAME = "vocab/equivalences.txt";
    public static final String NONBRICK = "";
    /**
     * Data Members
     */
    private ArrayList<Chord> chords;         // list of chords
    
    private LinkedList<TreeNode>[][] cykTable; // table for CYK analysis
    private boolean tableFilled;               // test for full table
    
    // Terminal and Nonterminal grammar rules will be imported as lists of
    // strings. These will serve in separate phases of the parsing.
    
    // Nonterminal rules will have the form:
    //    (startSymbol, endSymbol1, endSymbol2)
    private LinkedList<BinaryProduction> nonterminalRules;
    
    // 
    private EquivalenceDictionary edict;
    
    /**
     * CYKParser / 0
     * The default CYKParser constructor; initializes it to an empty chord
     * sequence.
     */
    public CYKParser()
    {
        chords = new ArrayList<Chord>();
        cykTable = null;
        nonterminalRules = new LinkedList<BinaryProduction>();
        tableFilled = false;
        edict = new EquivalenceDictionary();
        edict.loadDictionary(DICTIONARY_NAME);
    }
    
    /**
     * CYKParser / 2
     * A CYKParser built with chords and durations
     * @param c: chords, an ArrayList of Strings
     * @param d: durations, an ArrayList of Integers (the int wrapper class)
     */
    public CYKParser(ArrayList<Chord> c)
    {
        chords = new ArrayList<Chord>();
        chords.addAll(c);
        int size = chords.size();
        tableFilled = false;
        
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
        nonterminalRules = new LinkedList<BinaryProduction>();
        edict.loadDictionary(DICTIONARY_NAME);
    }
    
    /** newChords
     * newChords allows you to change the chords that a CYKParser is analyzing
     * @param c: chords to replace those currently in a parser
     * @param d: durations to correspond with the chords in c
     */
    
    public void newChords(ArrayList<Chord> c)
    {
        int size = c.size();
        
        chords.clear();
        chords.addAll(c);
        tableFilled = false;
        
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
    }
    
    /** newRule
     * newRule allows the addition of a rule to the CYKParser's rule library
     * @param binary: rule type indicator (true = binary, false = unary)
     * @param rule: a String to be parsed into a rule
     */
    public void newRule(boolean binary, String rule) {
        if (binary) {
            BinaryProduction newRule = new BinaryProduction(rule);
            nonterminalRules.add(newRule);
        } else {
            UnaryProduction newRule = new UnaryProduction(rule);
        }
        
        tableFilled = false;
    }
    
    /** readChords
     * Reads in chords and durations from a .txt file to make Chord 
     * objects to parse. 
     * @param filename: the name of a chord input file, a String 
     */
    public void readChords(String filename)
    {
        chords.clear();
        FileReader in = null;
        try {
            in = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        BufferedReader chordsIn = new BufferedReader(in);
        try {
            while (chordsIn.ready()) {
                String newChord = chordsIn.readLine();
                String[] chordChunks = newChord.split(" ");
                if (chordChunks.length == 2) {

                    chords.add(new Chord(chordChunks[0], 
                            Integer.parseInt(chordChunks[1])));

                } else if (chordChunks.length == 3) {

                    chords.add(new Chord(chordChunks[0], 
                            Integer.parseInt(chordChunks[1]), 
                            chordChunks[2].equals("@")));
                } else if (newChord.length() != 0) {
                    Error e1 = new Error(newChord + " is not a chord.");
                    System.err.println(e1);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int size = chords.size();
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
        tableFilled = false;
        
    }
    
    /** readChordsls
     * Reads in chords without durations from a leadsheet file
     * @param filename , a String
     */
    public void readChordsls(String filename)
    {
        chords.clear();
        FileReader in = null;
        try {
            in = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        BufferedReader chordsIn = new BufferedReader(in);
        try {
            while (chordsIn.ready()) {
                String newLine = chordsIn.readLine();
                if (newLine.contains("|")) {
                    addChords(newLine);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        int size = chords.size();
        cykTable = (LinkedList<TreeNode>[][]) new LinkedList[size][size];
        tableFilled = false;
        
    }
    
    // FILLER CODE - NOT PARSING CORRECTLY YET //
    /** addChords
     * Takes in a string from the chord part of a leadsheet and pulls out chords
     * 
     * @param newLine, a line from a leadsheet file
     */
    public void addChords(String newLine)
    {
        String[] chordsList = newLine.split(" ");
        for (int index = 0; index<chordsList.length; index++) {
            if (!chordsList[index].matches("[/|]")) {
                Chord newChord = new Chord(chordsList[index], 480);
                chords.add(newChord);
            }
        }
    }
    /** populateRules
     * Reads in binary productions from a .txt file
     * @param filename: the name of a productions file, a String
     */
    public void populateRules(String filename) 
    {
        FileReader in = null;
        try {
            in = new FileReader(filename);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        // read in input line by line
        BufferedReader rulesIn = new BufferedReader(in);
        try {
            while (rulesIn.ready()) {
                String newRule = rulesIn.readLine();
                String[] ruleChunks = newRule.split(" ");
                if (ruleChunks.length == 8) {
                    newRule(true, newRule);
                } else if (ruleChunks.length != 0) {
                    Error e2 = new Error(newRule + " is not a rule.");
                    System.err.println(e2);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(
                    CYKParser.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        tableFilled = false;
        
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
                System.err.println("Error: bad brick.");
                    
            // Perfect case: a Brick of two subBricks. Only one rule is needed,
            // a BinaryProduction with the name of the resulting brick as its 
            // head and each of the subBricks in its body.
            else if (size == 2) {
                BinaryProduction p = new BinaryProduction(name, b.getType(),
                        subBlocks.get(0), subBlocks.get(1), true, mode);
                    nonterminalRules.add(p);
            }
            
            // Larger case: a Brick of three or more subBricks. The Brick gets
            // divided into a starting Brick whose name won't be printed in a
            // parse tree. Each following rule will have two body blocks, one
            // being the name of the previous production and one being the next
            // subBlock. The last rule gets the name of the final Brick.
            else {
                currentName = name + "1";
                BinaryProduction[] prods = new BinaryProduction[size];
                prods[0] = new BinaryProduction(currentName, NONBRICK,
                        subBlocks.get(0), subBlocks.get(1), false, mode);
                nonterminalRules.add(prods[0]);
                for (int i = 2; i < size - 1; i++) {
                    currentName = name + i;
                    prods[i-1] = new BinaryProduction(currentName, 
                            NONBRICK, prods[i-2], subBlocks.get(i), false, mode);
                    nonterminalRules.add(prods[i-1]);
                }
                prods[size-2] = new BinaryProduction(name, b.getType(),
                        prods[size-3], subBlocks.get(size-1), true, mode);
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
                if (cykTable[row][col].isEmpty()) {
                    minVals[row][col] = new TreeNode();
                }
                else {
                    ListIterator node = cykTable[row][col].listIterator();
                    minVals[row][col] = (TreeNode) node.next();
                    while (node.hasNext()) {
                        TreeNode nextNode = (TreeNode) node.next();
                        if (nextNode.getCost() < minVals[row][col].getCost())
                            minVals[row][col] = nextNode;
                    }
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
        return minVals[0][size - 1].toBlocks();
            
    }
    
    /** printSolutions
     * printSolutions takes a filled-in table in a CYKParser and prints all 
     * possible parse trees with their respective costs. It then returns the 
     * best parse tree (the lowest cost one).
     */
    public String printSolution(BrickLibrary lib) {
        
        // First, we assemble the table of minimum-value nodes.
        assert(tableFilled);
        int size = this.chords.size();
        TreeNode[][] minVals = new TreeNode[size][size];
        
	// If this is an empty table, we're done	
	if (size == 0)
	    return new ArrayList<Block>().toString();
        
	// This loops through every occupied cell in the cykTable and finds 
        // the lowest-cost Node in the list, for the construction of optimal
        // trees later.
        for (int row = 0; row < size; row++) {
            for (int col = row; col < size; col++) {
                if (cykTable[row][col].isEmpty()) {
                    minVals[row][col] = new TreeNode();
                }
                else {
                    ListIterator node = cykTable[row][col].listIterator();
                    minVals[row][col] = (TreeNode) node.next();
                    while (node.hasNext()) {
                        TreeNode nextNode = (TreeNode) node.next();
                        if (nextNode.getCost() < minVals[row][col].getCost())
                            minVals[row][col] = nextNode;
                    }
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
        return minVals[0][size - 1].toString();
            
    }
    
    private ArrayList<TreeNode> findSolutions(BrickLibrary lib) {
        // First, we assemble the table of minimum-value nodes.
        assert(tableFilled);
        int size = this.chords.size();
        ArrayList<TreeNode>[][] minVals = 
                (ArrayList<TreeNode>[][]) new ArrayList[size][size];
        
        
        // This loops through every occupied cell in the cykTable and finds 
        // the lowest-cost Node(s) in the list, for the construction of optimal
        // trees later.
        for (int row = 0; row < size; row++) {
            for (int col = row; col < size; col++) {
                minVals[row][col] = new ArrayList<TreeNode>();
                if (!(cykTable[row][col].isEmpty())) {
                    for(TreeNode node : cykTable[row][col]) {
                        if (minVals[row][col].isEmpty())
                            minVals[row][col].add(node);
                        else if (node.getCost() < 
                                minVals[row][col].get(0).getCost()) {
                            minVals[row][col].clear();
                            minVals[row][col].add(node);
                        }
                        else if (node.getCost() == 
                                minVals[row][col].get(0).getCost()) {
                            minVals[row][col].add(node);
                        }
                    }
                }
            }
        }
        
        // This fills a table with possible parses, building up to the top right
        // corner for a total parse. It limits each cell's number of possible
        // parses to the number of chords in the parser.
        for (int i = size - 2; i >= 0; i--) {
            for (int j = i + 1; j < size; j++) {
                for (int k = i + 1; k <= j; k++) {
                    for (TreeNode node1 : minVals[i][k-1]) {
                        for (TreeNode node2 : minVals[k][j]) {
                            if (minVals[i][j].isEmpty() || 
                                    minVals[i][j].size() < size &&
                                    node1.getCost() + node2.getCost() 
                                        < minVals[i][j].get(0).getCost()) {
                                TreeNode newNode = new TreeNode(node1, node2);
                                minVals[i][j].add(newNode);
                            }
                        }
                    }
                }
            }
        }

        Collections.sort(minVals[0][size-1], new NodeComparator());

    
        // The shortest path in the top right cell gets printed as the best
        // explanation for the whole chord progression
        System.err.println(minVals[0][size-1]);
        return minVals[0][size - 1];
    }
    
    /** findTerminal
     * findTerminal is a helper function which, for a given index i takes the
     * ith chord and ith duration and fills the [i, i] space in the 2D List 
     * array with the symbols which could generate that chord.
     */
    private void findTerminal(int index, long start)
    {
        cykTable[index][index] = new LinkedList<TreeNode>();
        Chord currentChord = chords.get(index);
        TreeNode currentNode = new TreeNode(currentChord, start);
        cykTable[index][index].add(currentNode);
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
        
        // We make sure that the code loops through the different possible cell
        // pairs, starting at the leftmost and topmost.
        for(int index = 0; index < (col - row); index++) {
            assert(row+index < this.chords.size());
            ListIterator iter1 = cykTable[row][row+index].listIterator(0);
            
            // We loop through all the possible symbols in each cell.
            while (iter1.hasNext()) {
                TreeNode symbol1 = (TreeNode) iter1.next();
                
                ListIterator iter2 = 
                        cykTable[row+1+index][col].listIterator();
                
                while (iter2.hasNext()) {
                    TreeNode symbol2 = (TreeNode) iter2.next();
                    
                    ListIterator iterRule = nonterminalRules.listIterator();

                    // We check every rule against each pair of symbols.
                    while (iterRule.hasNext()) { 
                        BinaryProduction rule = 
                                (BinaryProduction) iterRule.next();
                        
                        long newKey = rule.checkProduction(symbol1, 
                                                           symbol2, edict);
                        if (!(newKey < 0)) {
                            TreeNode newNode = new TreeNode(rule.getHead(),
                                    rule.getType(), rule.getMode(), 
                                    symbol1, symbol2, rule.getCost(), newKey);
                            cykTable[row][col].add(newNode);
                            
                        }
                    }
                }
            }
        }
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
        ArrayList<Chord> ch = new ArrayList<Chord>();
        for (Block b: blocks)
            ch.addAll(b.flattenBlock());
        newChords(ch);
        
        // Read in rules and parse
        createRules(lib);
        fillTable();
        return findSolution(lib);
    }
    
    public void testFiles() throws IOException {
        String name = "testfiles/test" + ((System.currentTimeMillis() / 100000) 
                - 13000000) + ".txt";
        try {
            File dir = new File("pseudo-leadsheets");
            String[] filenames = dir.list();
            
            FileWriter fstream = new FileWriter(name);
            BufferedWriter out = new BufferedWriter(fstream);
            BrickLibrary lib = BrickLibrary.processDictionary();
            createRules(lib);
            long time1;
            long time2;
            for (String file : filenames) {
                if (!file.startsWith(".")) {
                    System.err.println("Testing " + file + ": ");
                    time1 = System.currentTimeMillis();
                    readChords("pseudo-leadsheets/" + file);
                    fillTable();
                    out.write(file + "\n\n" + printSolution(lib) + "\n\n");
                    time2 = System.currentTimeMillis();
                    System.err.println("took "+ (time2 - time1) + " ms");
                }
            }
            out.close();
        } catch (Exception e){ //Catch exception if any
                System.err.println("Error: " + e.getMessage());
        }
            
    }
            
    // Testing code
    public static void main(String[] args) throws IOException 
    {   
        CYKParser parser = new CYKParser();
        
        /*
        try {
            File dir = new File("pseudo-leadsheets");
            String[] filenames = dir.list();
            BrickLibrary lib = BrickLibrary.processDictionary();
            parser.createRules(lib);
            long time1;
            long time2;
            for (String file : filenames) {
                if (!file.startsWith(".")) {
                    System.err.println("Testing " + file + ": ");
                    time1 = System.currentTimeMillis();
                    parser.readChords("pseudo-leadsheets/" + file);
                    parser.fillTable();
                    parser.findSolutions(lib);
                    time2 = System.currentTimeMillis();
                    System.err.println("took " + (time2 - time1) + " ms");
                }
            }
        } catch (Exception e){ //Catch exception if any
                System.err.println("Error: " + e.getMessage());
        }
        */
        
        parser.testFiles();
    }
}

/****************************************
 *            TO DO LIST                *
 ****************************************
 * 
 * - Get durations to read in correctly
 *(-)Get additional results from the CYK table?
 * - Overlapping cadences need to work, too
 * 
 */
