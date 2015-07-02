/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;

import java.util.ArrayList;
import polya.Polylist;
import imp.data.*;
import imp.lickgen.transformations.NoteChordPair;
import imp.lickgen.transformations.TrendSegment;
/**
 *
 * @author muddCS15
 */
public class DiatonicTrend extends Trend{

    //doesn't matter what the interval is
    public boolean stopCondition(Note n1, Note n2) {
        return false;
    }

    //returns whether a note belongs to a chord's primary scale
    public boolean diatonic(Note n, Chord c){
        
        if(n.isRest() || c.isNOCHORD()){
            return false;
        }
        
        Polylist firstScale = c.getFirstScale();
        if(firstScale == null){
            return false;
        }
        
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(n);
        return ns.enhMember(firstScale);
    }
    
    //trend continues so long as notes belong to chord's first scale
    public boolean stopCondition(Note n, Chord c) {
        return !diatonic(n, c);
    }

    //priority important, then strong beat and duration
    public double[] weights() {
        double [] weights = {1, .5, .5};
        return weights;
    }

    //2 for now - will change
    public int numberOfSections() {
        return 2;
    }

    public String getName() {
        return "DIATONIC";
    }
    
}