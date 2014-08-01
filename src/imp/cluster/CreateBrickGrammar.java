/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cluster;

import static imp.Constants.BEAT;
import imp.brickdictionary.Block;
import imp.brickdictionary.Brick;
import static imp.cluster.CreateGrammar.averageVector;
import static imp.cluster.CreateGrammar.calcAverage;
import static imp.cluster.CreateGrammar.getClusterReps;
import static imp.cluster.CreateGrammar.getClusters;
import static imp.cluster.CreateGrammar.getRuleStringsFromFile;
import static imp.cluster.CreateGrammar.getRulesFromFile;
import static imp.cluster.CreateGrammar.processRule;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.gui.LickgenFrame;
import imp.gui.Notate;
import imp.gui.Stave;
import imp.gui.StaveScrollPane;
import imp.lickgen.NoteConverter;
import imp.util.ErrorLog;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Arrays;
import polya.Polylist;

/**
 *
 * @author Mark Heimann
 */
public class CreateBrickGrammar {
    private static HashSet<String> brickKinds = new HashSet<String>();
    private static String[] brickKindsArray;
    private static HashSet<Integer> brickDurations = new HashSet<Integer>();
    private static int[] brickDurationsArray;
    private static ArrayList<Block> blocks = new ArrayList<Block>();
    private static List<Vector<DataPoint>> brickLists = new ArrayList<Vector<DataPoint>>();
    private static ArrayList<Cluster[]> allClusters = new ArrayList<Cluster[]>();
    private static ArrayList<Vector<ClusterSet>> allClusterSets = new ArrayList<Vector<ClusterSet>>();
    private static ArrayList<Vector<Vector<ClusterSet>>> allOutlines = new ArrayList<Vector<Vector<ClusterSet>>>();
    private static ArrayList<DataPoint[]> allReps = new ArrayList<DataPoint[]>();
    
    private static int MEASURE_LENGTH; //so we can determine if bricks start on measures
    

    /**
     * processByBrick Scan the tune one brick at a time and write info to an
     * output file
     */
    public static void processByBrick(Notate notate, LickgenFrame frame) {
        //step 1: roadmap the tune to find out what bricks it uses and where
        ArrayList<Block> currentBlocks = notate.getRoadMapBlocks(); //blocks from the current tune we're processing
        for (Block block : currentBlocks) {
            blocks.add(block); //blocks keeps track of all blocks from all tunes in the corpus
        }
        //PartIterator iterates through choruses Score.size() Score.getPart()
        MEASURE_LENGTH = notate.getCurrentMelodyPart().getMeasureLength();
        int chorusCount = 1;
        for (StaveScrollPane ssp : notate.getStaveScrollPane()) {
            Stave s = ssp.getStave();
            MelodyPart melPart = notate.getMelodyPart(s);
            ChordPart chordProg = notate.getChordProg();

            //step 2: scan melodies one brick at a time
            int totalDuration = 0; //so we can keep track of where we are in the tune
            for (int i = 0; i < currentBlocks.size(); ++i) {
                Block currentBlock = currentBlocks.get(i); //the block that we're currently processing within the current tune
                
                //-1 to prevent spillover into next measure
                int totalDurationPlusThisBlock = totalDuration + currentBlock.getDuration() - 1; 
                                                
                //if we only want to learn based on bricks not general chordBlocks also;
                //otherwise leave this condition out
                //note: we only want to use bricks that start at the beginning of measures
                //for the sake of QC (who knows what's up with little fractional measure bricks)
                if (currentBlock instanceof Brick
                    && (totalDuration % MEASURE_LENGTH == 0)
                    && (currentBlock.getDuration() % MEASURE_LENGTH == 0)) { 
                    
                    //this will keep track of what kind of bricks we have in the tune
                    brickKinds.add(currentBlock.getDashedName());
                    brickDurations.add(currentBlock.getDuration());
                    MelodyPart blockMelody = melPart.extract(totalDuration, totalDurationPlusThisBlock, true); 
                    ChordPart blockChords = chordProg.extract(totalDuration, totalDurationPlusThisBlock);
                    String blockAbstract = NoteConverter.melodyToAbstract(blockMelody, 
                                                                                blockChords, 
                                                                                (i == 0), 
                                                                                notate, 
                                                                                notate.getLickGen());
                    int location = totalDuration % melPart.size(); //tell production writing method how far we are
                                                                   //in a given chorus and which chorus
                    if (blockAbstract != null) {
                        frame.writeProduction(blockAbstract,
                                currentBlock.getDuration()/BEAT,
                                location,
                                true,
                                currentBlock.getDashedName(),
                                chorusCount);
                    }
                }
                totalDuration += currentBlock.getDuration();
            }
            chorusCount++;
        }
        
        //for convenience (to make it easier to refer to a specific brick type), store brick types in array
        brickKindsArray = new String[brickKinds.size()];
        int index = 0;
        Iterator iter = brickKinds.iterator();
        while (iter.hasNext()) {
            brickKindsArray[index] = (String) iter.next();
            ++index;
        }
        
        brickDurationsArray = new int[brickDurations.size()];
        int indexDur = 0;
        Iterator iterDur = brickDurations.iterator();
        while (iterDur.hasNext()) {
            brickDurationsArray[indexDur] = (Integer) iterDur.next();
            ++indexDur;
        }
        Arrays.sort(brickDurationsArray); //so that we have unique durations in sorted order
    }

    /**
     * create 
     * Learn a grammar based on fragments gleaned from solos clustered
     * together by similarity within types of harmonic bricks
     *
     * @param chordProg the chord progression
     * @param infile the file we're getting initial rules from
     * @param outfile the file we're writing the grammar to
     * @param repsPerCluster how many representatives to choose from each kind
     * of cluster
     * @param notate used to process melody by brick
     */
    public static void create(ChordPart chordProg,
            String inFile,
            String outFile,
            int repsPerCluster,
            boolean useRelative,
            boolean useAbstract,
            Notate notate,
            LickgenFrame frame) {
        
        //do processing by brick
        if (brickKindsArray.length == 0) { //must be a pretty strange tune for this to happen...
            ErrorLog.log(ErrorLog.COMMENT, "No bricks found in the tune. "
                    + "Please try again with windows");
            return;
        }
        
        //initial overhead
        notate.setLickGenStatus("Writing grammar rules: " + outFile);

        //if useHead is true, we will add datapoints from the head into
        //a separate ArrayList, and we will not use them in clustering
        boolean useHead = false;

        //make initial calls to read from the file
        Polylist[] rules = getRulesFromFile(inFile);
        String[] ruleStrings = getRuleStringsFromFile(inFile);
        
        //create a list of lists, each holding DataPoints corresponding to a certain type of brick
        ArrayList<DataPoint> headData = new ArrayList<DataPoint>();
        //List<Vector<DataPoint>> brickLists = new ArrayList<Vector<DataPoint>>();
        int brickListsSize = brickKindsArray.length + 1; //an extra one to hold non-bricks if needed
        for (int i = 0; i < brickListsSize; ++i) {
            brickLists.add(new Vector<DataPoint>());
        }
        
        //store the data
        //NOTE: vectors are out of date, but we continue to use them to build off existing cluster methods that use them
        for (int i = 0; i < rules.length; i++) {
            DataPoint temp = processRule(rules[i], ruleStrings[i], Integer.toString(i));
            String brickName = temp.getBrickType();
            
            //if we care about separating out the head, AND if rule belongs to the head, store its data separately
            if (useHead && temp.isHead()) { 
                headData.add(temp);
            } else {
                //store data in the vector in the list of vectors
                //corresponding to a specific brick type (as indexed in the brick types array)
                if (!brickName.equals("None")) {
                    int brickTypeIndex = java.util.Arrays.asList(brickKindsArray).indexOf(brickName);
                    brickLists.get(brickTypeIndex).add(temp);
                }
                else {
                    brickLists.get(brickLists.size() - 1).add(temp);
                }
            }
        }
        notate.setLickGenStatus("Wrote " + rules.length + " grammar rules.");
        
        //cluster the data
        //add clusters by type of brick in the same order that the brick types are stored in brickLists
        for (Vector<DataPoint> brickData : brickLists) { 
            if (brickData.size() > 0) {
                double[] averages = calcAverage(brickData);
                averageVector(brickData, averages);
                
                //so we don't try to choose more representatives from a cluster than physically possible
                if (repsPerCluster > brickData.size()) {
                    repsPerCluster = brickData.size(); 
                }
                
                Cluster[] clusters = getClusters(brickData, averages, brickData.size() / repsPerCluster);
                allClusters.add(clusters);

                DataPoint[] reps = getClusterReps(clusters, repsPerCluster);
                allReps.add(reps);
            }
        }

        //note: no need for a .soloist file
        writeBrickGrammar(useRelative, useAbstract, outFile, notate);
    }

    public static void writeBrickGrammar(boolean useRelative, boolean useAbstract, String outfile, Notate notate) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfile, true));
            //overhead rules that specify how many duration slots to subtract off for bricks of different durations
            for (int dur = 0; dur < brickDurationsArray.length; ++dur) {
                out.write("\n(rule (P Y) ((BRICK "
                        + brickDurationsArray[dur]
                        + ") (P (- Y "
                        + brickDurationsArray[dur]
                        + "))) "
                        + Math.pow(10, dur) + ")");
            }
            out.write("\n");

            //when writing rules, keep track of how many bricks of each kind we've written rules for
//            int[] brickKindsCount = new int[brickKindsArray.length];
//            for (int i = 0; i < brickKindsCount.length; i++) {
//                brickKindsCount[i] = 0;
//            }
            
            //write rules by brick type
            for (Vector<DataPoint> list : brickLists) {
                for (DataPoint point : list) {
                    if (useRelative) { //determine how we want to represent melody info
                        writeRule(point.getRelativePitchMelody(), point, out);
                    }
                    if (useAbstract) {
                        writeRule(point.getObjData(), point, out);
                    }
                    if (!(useRelative || useAbstract)) {
                        ErrorLog.log(ErrorLog.COMMENT, "No note option specified."
                                + "Please try again using relative pitches and/or abstract melodies for bricks");
                        return;
                    }
                }
             }
            
//            for (int s = 0; s < notate.getStaveScrollPane().length; s++) {
//                int totalDuration = 0; //so we can keep track of where we are in the tune
//                for (int i = 0; i < blocks.size(); ++i) {
//                    Block currentBlock = blocks.get(i);
//                    if (currentBlock instanceof Brick
//                            && (totalDuration % MEASURE_LENGTH == 0)
//                            && (currentBlock.getDuration() % MEASURE_LENGTH == 0)) { 
//                        String brickName = currentBlock.getDashedName();
//                        
//                        int brickNumber = 0; //find which brick this is in our array of bricks
//                        for (int j = 0; j < brickKindsArray.length; ++j) {
//                            if (brickKindsArray[j].equals(brickName)) {
//                                brickNumber = j;
//                                break;
//                            }
//                        }
//
//                        DataPoint[] brickClusterReps = allReps.get(brickNumber);
//                        int r = brickKindsCount[brickNumber]; //how many rules we've already written for this kind of brick
//                        DataPoint rep = brickClusterReps[r]; //choose the next representative
//                        
//                        if (useRelative) { //determine how we want to represent melody info
//                            writeRule(rep.getRelativePitchMelody(), rep, out);
//                        } 
//                        if (useAbstract) {
//                            writeRule(rep.getObjData(), rep, out);
//                        } 
//                        if (!(useRelative || useAbstract)) {
//                            ErrorLog.log(ErrorLog.COMMENT, "No note option specified."
//                                    + "Please try again using relative pitches and/or abstract melodies for bricks");
//                            return;
//                        }
//                        
//                        ++brickKindsCount[brickNumber]; //we've come across one more occurence of this kind of brick
//                    } 
//
//                    totalDuration += currentBlock.getDuration();
//                }
//            }
            out.close();
        } catch (Exception e) {
            System.out.println("Exception writing grammar: " + e.toString());
            e.printStackTrace();
        }
    }
    
    public static void writeRule(String rule, DataPoint rep, BufferedWriter out) {
        try {
            out.write("(rule (BRICK "
                            + (rep.getSegLength()*BEAT)
                            + ") ("
                            + rule
                            + ") (builtin brick " //evaluates to 1 if brick type is this brick's type; 0 otherwise
                            + rep.getBrickType()
                            + "))\n");
                        
        } catch (Exception e) {
            System.out.println("IO exception: " + e.toString());
        }
    }
}
