/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cluster;

import static imp.Constants.BEAT;
import imp.brickdictionary.Block;
import imp.brickdictionary.Brick;
import static imp.cluster.CreateBrickGrammar.writeBrickGrammar;
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
            int totalDurationPlusThisBlock = totalDuration + currentBlock.getDuration();
            //System.out.printf("Block number %d: %s \n", (i + 1), currentBlock.getName());
            if (currentBlock instanceof Brick) { //if we only want to learn based on bricks not general chordBlocks also; 
                //otherwise leave this condition out
                //this will keep track of what kind of bricks we have in the tune
                brickKinds.add(currentBlock.getName());
                //if (production != null) {
                    //System.out.println("location: " + totalDuration);
                    //frame.writeProduction(production, currentBlock.getDuration()/BEAT, totalDuration, true, currentBlock.getName());
                //}
                MelodyPart blockMelody = melPart.extract(totalDuration, totalDurationPlusThisBlock - 1, true); //-1 to prevent bleeding over into start of next measure
                ChordPart blockChords = chordProg.extract(totalDuration, totalDurationPlusThisBlock - 1);
                String blockAbstract = imp.lickgen.NotesToRelativePitch.melodyToAbstract(blockMelody, blockChords, (i == 0), notate, notate.getLickGen());
                //System.out.println(blockAbstract);
                String relMel = imp.lickgen.NotesToRelativePitch.melPartToRelativePitch(blockMelody, blockChords);
                //System.out.println(relMel);
                if (blockAbstract != null) {
                    System.out.println("Writing production for a brick of type " + currentBlock.getName());
                    frame.writeProduction(blockAbstract, currentBlock.getDuration()/BEAT, totalDuration, true, currentBlock.getName());
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
    }

    /**
     * create 
     * Learn a grammar based on fragments gleaned from solos clustered
     * together by similarity within types of harmonic bricks Based off of
     * CreateGrammar.create()
     *
     * @param chordProg the chord progression
     * @param infile the file we're getting initial rules from (?)
     * @param outfile the file we're writing the grammar to
     * @param repsPerCluster how many representatives to choose from each kind
     * of cluster (needed?)
     * @param notate used to do things(what?)
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
        int brickListsSize = brickKindsArray.length + 1; //an extra one to hold non-bricks
        for (int i = 0; i < brickListsSize; ++i) {
            brickLists.add(new Vector<DataPoint>());
        }

        System.out.println("Processing rules");
        //store the data
        //NOTE: vectors are out of date, but we continue to use them to build off cluster methods that use them
        for (int i = 0; i < rules.length; i++) {
            //processRule in CreateGrammar
            System.out.printf("Rule number %d of %d\n",(i+1), rules.length);
            System.out.println("Rule string: " + ruleStrings[i]);
            DataPoint temp = processRule(rules[i], ruleStrings[i], Integer.toString(i));
            if (useHead) {
                if (temp.isHead()) {
                    headData.add(temp);
                } else {
                    //store data in the vector in the list of vectors corresponding to a specific brick type (as indexed in the brick types array)
                    System.out.println("This brick's type: " + temp.getBrickType());
                    if (!temp.getBrickType().equals("None")) {
                        brickLists.get(java.util.Arrays.asList(brickKindsArray).indexOf(temp.getBrickType())).add(temp);
                    }
                    else {
                        brickLists.get(brickLists.size() - 1).add(temp);
                    }
                }
            } else {
                System.out.println("This brick's type: " + temp.getBrickType());
                if (!temp.getBrickType().equals("None")) {
                    brickLists.get(java.util.Arrays.asList(brickKindsArray).indexOf(temp.getBrickType())).add(temp);
                }
                else {
                    brickLists.get(brickLists.size() - 1).add(temp);
                }
            }
        }
        notate.setLickGenStatus("Wrote " + rules.length + " grammar rules.");
        System.out.println("Finished processing rules");
        //cluster the data
        //TODO: use a new distance metric that's tailored to bricks
        int numberOfOutlines = 0;
        System.out.println("Size of brickLists: " + brickLists.size());
        for (Vector<DataPoint> brickData : brickLists) { //add clusters by type of brick--in the same order that the brick types are stored in brickLists
            double[] averages = calcAverage(brickData);
            averageVector(brickData, averages);

            Cluster[] clusters = getClusters(brickData, averages, brickData.size() / repsPerCluster);
            allClusters.add(clusters);

            //get the sets of similar clusters
            Vector<ClusterSet> clusterSets = getClusterSets(clusters);
            allClusterSets.add(clusterSets);

            //get the cluster orders so we can get outlines (so we can create soloist files)
            Vector<Vector<DataPoint>> orders = getClusterOrder(clusters, brickData);

            //get the outlines
            Vector<Vector<ClusterSet>> outlines = getOutlines(orders, clusters, clusterSets);
            allOutlines.add(outlines);
            numberOfOutlines += outlines.size();

            DataPoint[] reps = getClusterReps(clusters, repsPerCluster);
            allReps.add(reps);
        }

        //no need for a .soloist file
        System.out.println("finished clustering data");
        writeBrickGrammar(true, outFile);
        System.out.println("finished writing grammar");
    }

    public static void writeBrickGrammar(boolean useRelative, String outfile) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(outfile, true));
            System.out.println("will be writing grammar");
            String rule;
            int totalDuration = 0; //so we can keep track of where we are in the tune
            for (int i = 0; i < blocks.size(); ++i) {
                Block currentBlock = blocks.get(i);
                if (currentBlock instanceof Brick) { //if we only want to learn based on bricks not general chordBlocks also; 
                    //otherwise leave this condition out
                    String brickName = currentBlock.getName();
                    int brickNumber = 0; //find which brick this is in our array of bricks
                    for (int j = 0; j < brickKindsArray.length; ++j) {
                        if (brickKindsArray[j].equals(brickName)) {
                            brickNumber = j;
                            break;
                        }
                    }
                    DataPoint rep = getClusterReps(allClusters.get(brickNumber), 1)[0]; //only need one representative per brick
                    if (useRelative) {
                        rule = rep.getRelativePitchMelody();
                    } else {
                        rule = rep.getObjData();
                    }
                    rule = rule.substring(0, rule.length() - 1);
                    out.write(rule + "\n");
                } else { 
                    //how to deal with parts that could not be classified as bricks
                    //use grammar (abstract or X notation as user desires)
                    //do you use Markov chains then if you have a long sequence of ChordBlocks to cover for?
                    String production = frame.addMeasureToAbstractMelody(totalDuration, currentBlock.getDuration()/BEAT, i==0);
                    if (production != null) {
                        //will still write with relative pitches if appropriate checkbox is selected in the lickgenframe gui
                        frame.writeProduction(production, currentBlock.getDuration(), totalDuration, true, currentBlock.getName());
                    }
                }
                
                totalDuration += currentBlock.getDuration();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
