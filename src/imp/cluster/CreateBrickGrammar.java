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
import static imp.cluster.CreateGrammar.createSoloistFile;
import static imp.cluster.CreateGrammar.getClusterOrder;
import static imp.cluster.CreateGrammar.getClusterReps;
import static imp.cluster.CreateGrammar.getClusters;
import static imp.cluster.CreateGrammar.getRuleStringsFromFile;
import static imp.cluster.CreateGrammar.getRulesFromFile;
import static imp.cluster.CreateGrammar.processRule;
import static imp.cluster.CreateGrammar.getClusterSets;
import static imp.cluster.CreateGrammar.getOutlines;
import imp.com.CommandManager;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Score;
import imp.gui.LickgenFrame;
import imp.gui.Notate;
import imp.lickgen.LickGen;
import imp.lickgen.NotesToRelativePitch;
import imp.roadmap.RoadMap;
import imp.roadmap.RoadMapFrame;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;
import java.util.Arrays;
import polya.Polylist;

/**
 *
 * @author Mark Heimann
 */
public class CreateBrickGrammar {
    private static RoadMap roadmap;
    private static LickgenFrame frame;
    private static HashSet<String> brickKinds = new HashSet<String>();
    private static String[] brickKindsArray;
    private static HashSet<Integer> brickDurations = new HashSet<Integer>();
    private static int[] brickDurationsArray;
    private static ArrayList<Block> blocks;
    private static ArrayList<Cluster[]> allClusters = new ArrayList<Cluster[]>();
    private static ArrayList<Vector<ClusterSet>> allClusterSets = new ArrayList<Vector<ClusterSet>>();
    private static ArrayList<Vector<Vector<ClusterSet>>> allOutlines = new ArrayList<Vector<Vector<ClusterSet>>>();
    private static ArrayList<DataPoint[]> allReps = new ArrayList<DataPoint[]>();

    /**
     * processByBrick Scan the tune one brick at a time and write info to an
     * output file
     */
    public static void processByBrick(Notate notate) {
        //step 1: roadmap the tune to find out what bricks it uses and where
        blocks = notate.getRoadMapBlocks();
        MelodyPart melPart = notate.getCurrentMelodyPart();
        ChordPart chordProg = notate.getChordProg();
        
        frame = new LickgenFrame(notate, notate.getLickGen(), notate.cm);
        //step 2: scan melodies one brick at a time
        int totalDuration = 0; //so we can keep track of where we are in the tune
        for (int i = 0; i < blocks.size(); ++i) {
            Block currentBlock = blocks.get(i);
            int totalDurationPlusThisBlock = totalDuration + currentBlock.getDuration() - 1; //-1 to prevent spillover into next measure
            if (currentBlock instanceof Brick) { //if we only want to learn based on bricks not general chordBlocks also; 
                                                 //otherwise leave this condition out
                //this will keep track of what kind of bricks we have in the tune
                brickKinds.add(currentBlock.getDashedName());
                brickDurations.add(currentBlock.getDuration());
                MelodyPart blockMelody = melPart.extract(totalDuration, totalDurationPlusThisBlock, true); 
                ChordPart blockChords = chordProg.extract(totalDuration, totalDurationPlusThisBlock);
                String blockAbstract = NotesToRelativePitch.melodyToAbstract(blockMelody, blockChords, (i == 0), notate, notate.getLickGen());
                String relMel = NotesToRelativePitch.melPartToRelativePitch(blockMelody, blockChords);
                if (blockAbstract != null) {
                    frame.writeProduction(blockAbstract, currentBlock.getDuration()/BEAT, totalDuration, true, currentBlock.getDashedName());
                }
            }
            totalDuration += currentBlock.getDuration();
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
     * together by similarity within types of harmonic bricks Based off of
     * CreateGrammar.create()
     *
     * @param chordProg the chord progression
     * @param infile the file we're getting initial rules from
     * @param outfile the file we're writing the grammar to
     * @param repsPerCluster how many representatives to choose from each kind
     * of cluster
     * @param notate used to process melody by brick
     */
    public static void create(ChordPart chordProg, String inFile, String outFile, int repsPerCluster, boolean useRelative, Notate notate) {
        //do processing by brick
        processByBrick(notate);
        
        //initial overhead
        notate.setLickGenStatus("Writing grammar rules: " + outFile);

        //if useHead is true, we will add datapoints from the head into
        //a separate ArrayList, and we will not use them in clustering
        boolean useHead = false;

        //make initial calls to read from the file
        Polylist[] rules = getRulesFromFile(inFile);
        String[] ruleStrings = getRuleStringsFromFile(inFile);

        //create a list of lists, where each list in the list will hold DataPoints corresponding to a certain type of brick
        ArrayList<DataPoint> headData = new ArrayList<DataPoint>();
        List<Vector<DataPoint>> brickLists = new ArrayList<Vector<DataPoint>>();
        int brickListsSize = brickKindsArray.length + 1; //an extra one to hold non-bricks if needed
        for (int i = 0; i < brickListsSize; ++i) {
            brickLists.add(new Vector<DataPoint>());
        }
        
        //store the data
        //NOTE: vectors are out of date, but we continue to use them to build off cluster methods that use them
        for (int i = 0; i < rules.length; i++) {
            DataPoint temp = processRule(rules[i], ruleStrings[i], Integer.toString(i));
            String brickName = temp.getBrickType();
            if (useHead && temp.isHead()) { //if we care about separating out the head, AND if rule belongs to the head, store its data separately
                headData.add(temp);
            } else {
                //store data in the vector in the list of vectors corresponding to a specific brick type (as indexed in the brick types array)
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
        //TODO: use a new distance metric that's tailored to bricks
        int numberOfOutlines = 0;
        for (Vector<DataPoint> brickData : brickLists) { //add clusters by type of brick--in the same order that the brick types are stored in brickLists
            if (brickData.size() > 0) {
                double[] averages = calcAverage(brickData);
                averageVector(brickData, averages);
                if (repsPerCluster > brickData.size()) {
                    repsPerCluster = brickData.size(); //so we don't try to choose more representatives from a cluster than physically possible
                }
                Cluster[] clusters = getClusters(brickData, averages, brickData.size() / repsPerCluster);
                allClusters.add(clusters);

                DataPoint[] reps = getClusterReps(clusters, repsPerCluster);
                allReps.add(reps);
            }
        }

        //note: no need for a .soloist file
        writeBrickGrammar(useRelative, outFile);
//        CreateGrammar.create(chordProg, inFile, outFile, 
//                repsPerCluster, frame.useMarkovSelected(), frame.getMarkovFieldLength(), 
//                useRelative, notate);
    }

    public static void writeBrickGrammar(boolean useRelative, String outfile) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfile, true));
            //boilerplate rules that specify how many duration slots to subtract off for bricks of different durations
            for (int dur = 0; dur < brickDurationsArray.length; ++dur) {
                out.write("\n(rule (P Y) (START"
                        + brickDurationsArray[dur]
                        + " (P (- Y "
                        + brickDurationsArray[dur]
                        + "))) "
                        + Math.pow(10, dur) + ")");
            }
            out.write("\n");
            
            //when writing rules, keep track of how many bricks of each kind we've written rules for
            int[] brickKindsCount = new int[brickKindsArray.length];
            for (int i = 0; i < brickKindsCount.length; i++) {
                brickKindsCount[i] = 0;
            }
            
            
            String rule;
            int totalDuration = 0; //so we can keep track of where we are in the tune
            for (int i = 0; i < blocks.size(); ++i) {
                Block currentBlock = blocks.get(i);
                if (currentBlock instanceof Brick) { //if we only want to learn based on bricks not general chordBlocks also; 
                    //otherwise leave this condition out
                    String brickName = currentBlock.getDashedName();
                    int brickNumber = 0; //find which brick this is in our array of bricks
                    for (int j = 0; j < brickKindsArray.length; ++j) {
                        if (brickKindsArray[j].equals(brickName)) {
                            brickNumber = j;
                            break;
                        }
                    }
                    
                    //Cluster[] brickCluster = allClusters.get(brickNumber); //cluster to which this brick belongs
                    //DataPoint[] brickClusterReps = getClusterReps(brickCluster, 1);
                    DataPoint[] brickClusterReps = allReps.get(brickNumber);
                    //for (int r = 0; r < brickClusterReps.length; r++) {
                        int r = brickKindsCount[brickNumber];
                        DataPoint rep = brickClusterReps[r]; 
                        if (useRelative) {
                            rule = rep.getRelativePitchMelody();
                        } else {
                            rule = rep.getObjData();
                        }
                        out.write("(rule (START"
                                + (rep.getSegLength()*BEAT)
                                + ") ("
                                + rule
                                + ") (builtin brick " //evaluates to 1 if brick type is this brick's type; 0 otherwise
                                + rep.getBrickType()
                                + "))\n");
                        ++brickKindsCount[brickNumber]; //we've come across one more occurence of this kind of brick
                    //}
                    
                } else { 
                    //TODO: how to deal with parts that could not be classified as bricks
                    //IDEA: use grammar (abstract or X notation as user desires)
                    String production = frame.addMeasureToAbstractMelody(totalDuration, currentBlock.getDuration()/BEAT, i==0);
                    if (production != null) {
                        frame.writeProduction(production, currentBlock.getDuration(), totalDuration, true, currentBlock.getDashedName());
                    }
                }
                
                totalDuration += currentBlock.getDuration();
            }
            out.close();
        } catch (Exception e) {
            System.out.println("Exception writing grammar: " + e.toString());
        }
    }
}
