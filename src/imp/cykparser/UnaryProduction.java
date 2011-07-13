/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

/** UnaryProduction
 * Deprecated Production class extending AbstractProduction, meant to handle
 * terminals.
 * @author ImproVisor
 */

import imp.brickdictionary.*;
import java.util.ArrayList;
import polya.*;

public class UnaryProduction {
    
    public static final int NODUR = 0;
    public final boolean SUBRULE = false;
    public final boolean EQUIVRULE = true;
   
    private Chord head;
    private ArrayList<Chord> terminals;
    private int cost = 1100;
    private boolean type;
    
    UnaryProduction(String h, Polylist contents)
    {
        head = new Chord(h, NODUR);
        
        terminals = new ArrayList<Chord>(); 
        Chord newChord;
        while (contents.nonEmpty()) {
            newChord = new Chord(contents.first().toString(), NODUR);
            terminals.add(newChord);
            contents = contents.rest();
        }
    }

    public String getHead() {
        return head.toString();
    }
    
    public String getBody() {
        return terminals.toString();
    }
    
    public UnaryProduction(String s) {
        String[] rule = s.split(" ");
        head = new Chord(rule[0], NODUR);
        
        Chord eqChord;
        for (int i = 1; i < rule.length; i++)
        {
            eqChord = new Chord(rule[i], NODUR);
            terminals.add(eqChord);
        }
    }
    
    public SubstituteList checkSubstitution(Chord c) {
        SubstituteList subs = new SubstituteList();
        
        long diff;
        for (Chord sub : terminals) {
            diff = sub.matches(c);
            if (diff >= 0) {
                subs.add(head, diff);
            }
        }
        
        return subs;
    }
    

   
}
