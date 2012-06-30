/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen;

import java.util.ArrayList;
import java.util.Arrays;

/**
 *
 * @author David Halpern 2012
 */
public class Tension 
{
    private static int[] WEIGHTS = {0, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5, -1, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5};
    private static int[] WEIGHTS_4 = {0, -2, -1, -2};
    private static double[] C1SAME_C2SAME = {0, 0};
    private static double[] C1NOT_C2SAME = {1, 0};
    private static double[] C1SAME_C2NOT = {0, 1};
    private static double[] C1UP_C2DOWN = {0.83, 0.17};
    private static double[] C1DOWN_C2UP = {0.71, 0.29};
    private static double[] C1UP_C2UP = {0.33, 0.67};
    private static double[] C1DOWN_C2DOWN = {0.67, 0.33};
    private static int SLOTS_PER_MEASURE = 32;
    
    public Tension()
    {
    }
    
    /**
     * Calculates the percentage of angular intervals in notes
     * @param notes - ArrayList of notes
     * @return 
     */
    public static double getAngularity(int[] notes)
    {
        double angularIntervals = 0;
        for(int i = 1; i < notes.length; i ++)
        {
            if(Math.abs(notes[i] - notes[i-1]) > 3) 
            {
                angularIntervals ++;
            }
        }
        return angularIntervals / (notes.length - 1);
    }
    
    /**
     * Returns average pitch height
     * @param notes
     * @return 
     */
    public static int getAveragePitchHeight(int[] notes)
    {
        int averageNote = 0;
        for(int i: notes)
        {
            averageNote += i;
        }
        return averageNote/notes.length;
    }
    
    /**
     * Returns the syncopation value for every set of bars of size windowSize
     * @param onsets
     * @param measures
     * @param windowSize
     * @return 
     */
    public static int[] getWindowedSyncopation(int[] onsets, int measures, int windowSize)
    {
        int slotsPerWindow = SLOTS_PER_MEASURE * windowSize;
        int outputIndex = 0;
        int[] output = new int[measures/windowSize];
        for(int onsetIndex = 0; onsetIndex < onsets.length - 1; onsetIndex += slotsPerWindow)
        {
            int[] syncoArray = new int[slotsPerWindow];
            System.arraycopy(onsets, onsetIndex, syncoArray, 0, slotsPerWindow);
            output[outputIndex] = getSyncopation(syncoArray, windowSize);
            outputIndex ++;
        }
        return output;
    }
    
    /**
     * Returns the syncopation value of a melody according to the Longuet-Higgins and Lee (1984) algorithm
     * @param onsets - array of containing a slot for each 32nd note with a 1 if there is an onset and a 0 otherwise
     * @param measures - number of measures in onset array
     * @return - syncopation value
     */
    public static int getSyncopation(int[] onsets, int measures)
    {
        int[] w = getWeightArray(measures, WEIGHTS);
        int synco = 0;
        for(int i = 0; i < onsets.length; i ++)
        {
            if(onsets[i] == 0)
            {
                int nPos = i;
                while(onsets[nPos] == 0 && nPos > 0)
                {
                    nPos = nPos - 1;
                }
                if(!(onsets[nPos] == 0))
                {
                    int syncoValue = w[i] - w[nPos];
                    if(syncoValue > 0)
                    {
                        synco = synco + syncoValue;
                    }
                }
            }
        }
        return synco;
    }
    
    /**
     * Returns a weight array for a specific number of measures
     * @param measures
     * @return 
     */
    private static int[] getWeightArray(int measures, int[] weights)
    {
        int totalLength = weights.length * measures;
        int[] weightArray = Arrays.copyOf(weights, totalLength);
        int offset = weights.length;
        for(int i = measures; i > 1; i --)
        {
            System.arraycopy(weights, 0, weightArray, offset, weights.length);
            offset += weights.length;
        }
        return weightArray;
    }
    
//    public static int[] getMelodicAccent(int[] notes)
//    {
//        int[][] values = new int[2][notes.length];
//        for(int i = 0; i < (notes.length - 3); i++)
//        {
//            
//        }
//    }
}
