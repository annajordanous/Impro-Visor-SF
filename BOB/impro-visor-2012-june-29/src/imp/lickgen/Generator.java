/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen;

import java.util.Arrays;
import polya.Polylist;

/**
 *
 * @author David Halpern 2012
 */
public class Generator {

    private static int[] WEIGHTS = {0, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5, -1, -5, -4, -5, -3, -5, -4, -5, -2, -5, -4, -5, -3, -5, -4, -5};
    private static int NUM_SLOTS = WEIGHTS.length;
    private static int MOD = 8;
    private static int NUM_QUARTERS = 4;
    private static int NUM_SLOTS2 = 120;
    private static double HIGHTEST_NOTE_PROBABILITY = .5;
    private static int THIRTY_SECOND = 1;
    private static String THIRTY_SECOND_NOTE = "C32";
    private static String THIRTY_SECOND_REST = "R32";
    private static int SIXTEENTH = 2;
    private static String SIXTEENTH_NOTE = "C16";
    private static String SIXTEENTH_NOTE_APPROACH = "A16";
    private static String SIXTEENTH_REST = "R16";
    private static int EIGHTH = 4;
    private static String EIGHTH_NOTE = "C8";
    private static String EIGHTH_NOTE_APPROACH = "A8";
    private static String EIGHTH_REST = "R8";
    private static int QUARTER = 8;
    private static String QUARTER_NOTE = "C4";
    private static String QUARTER_REST = "R4";
    private static int HALF = 16;
    private static String HALF_NOTE = "C2";
    private static String HALF_REST = "R2";
    private static int WHOLE = 32;
    private static String WHOLE_NOTE = "C1";
    private static String WHOLE_REST = "R1";

    public Generator() {
    }

    /**
     * Generates a string for use in grammars from a given rhythm
     *
     * @param rhythm
     * @return
     */
    public static String[] generateString(int[] rhythm) {
        String rhythms = "";
        int prev = -1;
        for (int i = 0; i < rhythm.length; i++) {
            if (rhythm[i] == 1) {
                if (prev != -1) {
                    int diff = i - prev;
                    if((WEIGHTS[prev % NUM_SLOTS] * -1) >= 3)
                    {
                        rhythms += getString(diff, false, true);
                    }
                    else
                    {
                        rhythms += getString(diff, false, false);
                    }
                    prev = i;
                } 
                else if (i > 0) 
                {
                    rhythms += getString(i, true, false);
                }
                prev = i;
            }
        }
        if (prev == -1) 
        {
            rhythms += getString(rhythm.length, true, false);
        } 
        else if (prev != rhythm.length) 
        {
            int diff = rhythm.length - prev;
            if((WEIGHTS[prev % NUM_SLOTS] * -1) >= 3)
            {
                rhythms += getString(diff, false, true);
            }
            else
            {
                rhythms += getString(diff, false, false);
            }
        }
        String[] rhythmsArray = rhythms.split("\\s+");
        return rhythmsArray;
    }

    /**
     * Generates a string for a note length given the inter-onset interval and
     * whether or not it is a rest
     *
     * @param diff
     * @param rest
     * @return
     */
    private static String getString(int diff, boolean rest, boolean offbeat) {
        if (!rest) {
            if (diff >= WHOLE) {
                return WHOLE_NOTE + " " + getString(diff - WHOLE, true, false) + " ";
            } else if (diff >= HALF) {
                return HALF_NOTE + " " + getString(diff - HALF, true, false) + " ";
            } else if (diff >= QUARTER) {
                return QUARTER_NOTE + " " + getString(diff - QUARTER, true, false) + " ";
            } else if (diff >= EIGHTH) 
            {
                if(offbeat)
                {
                    if(Math.random() > .5)
                    {
                        return EIGHTH_NOTE_APPROACH + " " + getString(diff - EIGHTH, true, false) + " ";
                    }
                    else
                    {
                        return EIGHTH_NOTE + " " + getString(diff - EIGHTH, true, false) + " ";
                    }
                }
                else
                {
                    return EIGHTH_NOTE + " " + getString(diff - EIGHTH, true, false) + " ";
                }
            } 
            else if (diff >= SIXTEENTH) 
            {
                if(offbeat)
                {
                    if(Math.random() > .5)
                    {
                        return SIXTEENTH_NOTE_APPROACH + " " + getString(diff - SIXTEENTH, true, false) + " ";
                    }
                    else
                    {
                        return SIXTEENTH_NOTE + " " + getString(diff - SIXTEENTH, true, false) + " ";
                    }
                }
                else
                {
                    return SIXTEENTH_NOTE + " " + getString(diff - SIXTEENTH, true, false) + " ";
                }
            } 
            else if (diff >= THIRTY_SECOND) {
                return THIRTY_SECOND_NOTE + " " + getString(diff - THIRTY_SECOND, true, false) + " ";
            } else {
                return "";
            }
        } else {
            if (diff >= WHOLE) {
                return WHOLE_REST + " " + getString(diff - WHOLE, true, false);
            } else if (diff >= HALF) {
                return HALF_REST + " " + getString(diff - HALF, true, false);
            } else if (diff >= QUARTER) {
                return QUARTER_REST + " " + getString(diff - QUARTER, true, false);
            } else if (diff >= EIGHTH) {
                return EIGHTH_REST + " " + getString(diff - EIGHTH, true, false);
            } else if (diff >= SIXTEENTH) {
                return SIXTEENTH_REST + " " + getString(diff - SIXTEENTH, true, false);
            } else if (diff >= THIRTY_SECOND) {
                return THIRTY_SECOND_REST + " " + getString(diff - THIRTY_SECOND, true, false);
            } else {
                return "";
            }
        }
    }

    /**
     * Generates a rhythm that has a length of measures and a syncopation value
     * of mySynco
     *
     * @param measures
     * @param mySynco
     * @return
     */
    public static int[] generateSyncopation(int measures, int mySynco) {
        int[] rhythm = generateRhythm(measures);
        int synco = Tension.getSyncopation(rhythm, measures);
        while (synco > mySynco)
        {
//            int i = (int)(Math.random() * MOD * measures);
//	    int index = i * NUM_QUARTERS;
//	    if(i < NUM_SLOTS * measures - 1)
//	    {
//	        rhythm[index] = 1;
//	    }
            int i = (int) (Math.random() * NUM_SLOTS * measures);
            if (i < NUM_SLOTS * measures - 1) {
                int index = i;
                int desiredWeight = (WEIGHTS[index % NUM_SLOTS] * -1) + 1;
                if (2 < desiredWeight && desiredWeight < 5) {
                    while ((WEIGHTS[i % NUM_SLOTS] * -1) != desiredWeight) {
                        i++;
                    }
                    if (i < rhythm.length) 
                    {
                        if(rhythm[index] == 0)
                        {
                            rhythm[index] = 1;
                        }
                        rhythm[i] = 0;
                    }
                }
            }
            synco = Tension.getSyncopation(rhythm, measures);
        }
        while (synco < mySynco) {
            int i = (int) (Math.random() * NUM_SLOTS * measures);
            if (i < NUM_SLOTS * measures - 1) {
                int index = i;
                int desiredWeight = (WEIGHTS[index % NUM_SLOTS] * -1) - 1;
                if (0 <= desiredWeight && desiredWeight < 3) {
                    while ((WEIGHTS[i % NUM_SLOTS] * -1) != desiredWeight) {
                        i++;
                    }
                    if (i < rhythm.length) 
                    {
                        rhythm[index] = 0;
                        rhythm[i] = 1;
                    }
                }
            }
            synco = Tension.getSyncopation(rhythm, measures);
        }
        return rhythm;
    }

    /**
     * Generates a random rhythm of measures length
     *
     * @param measures
     * @return
     */
    public static int[] generateRhythm(int measures) {
        int[] rhythm = new int[measures * NUM_SLOTS];
        for (int i = 0; i < rhythm.length; i++) {
            int invMetHier = (WEIGHTS[i % NUM_SLOTS] * -1) + 1;
            double weight = 0;
            if (invMetHier != 6) {
                weight = (double) 1 / invMetHier;
            }
            double random = Math.random();
            if (random <= (weight * HIGHTEST_NOTE_PROBABILITY)) {
                rhythm[i] = 1;
            } else {
                rhythm[i] = 0;
            }
        }
        return rhythm;
    }
}
