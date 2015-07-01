/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;

import imp.Constants;
import imp.data.*;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.TrendSegment;
import java.util.ArrayList;

/**
 *
 * @author muddCS15
 */
public class ArpeggioTrend extends Trend{

    //doesn't matter what the interval is
    public boolean stopCondition(Note n1, Note n2) {
        return false;
    }

    //returns whether a note is a chord tone of a chord (false if chord is no chord or note is rest)
    public static boolean chordTone(Note n, Chord c){
        return !c.isNOCHORD() && !n.isRest() && c.getTypeIndex(n) == Constants.CHORD_TONE;
    }
    
    //trend continues as long as the notes are chord tones
    public boolean stopCondition(Note n, Chord c) {
        return !chordTone(n, c);
    }

    //no weight given to priority, lots to strong beat, some to duration
    public double[] weights() {
        double [] weights = {0, 1, .5};
        return weights;
    }

    //2 sections for now - will change
    public int numberOfSections() {
        return 2;
    }

}
