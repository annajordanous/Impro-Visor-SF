/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations.trends;

import java.util.ArrayList;
import imp.data.*;
/**
 *
 * @author muddCS15
 */
public class DescendingTrend extends Trend{
    
    //directional distance between two notes
    public static int dist(Note n1, Note n2){
        return n2.getPitch() - n1.getPitch();
    }
    
    //staying on the same note is okay (not strictly descending)
    public static boolean descending(Note n1, Note n2){
        return dist(n1, n2) <= 0;
    }

    //trend continues so long as direction is descending
    public boolean stopCondition(Note n1, Note n2) {
        return !descending(n1, n2);
    }

    //doesn't matter what role the note plays in the chord
    public boolean stopCondition(Note n, Chord c) {
        return false;
    }

    //TODO
    public ArrayList<Note> importantNotes(ArrayList<Note> notes) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
