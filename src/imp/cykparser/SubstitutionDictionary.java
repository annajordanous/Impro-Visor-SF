/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

import imp.brickdictionary.*;
import java.util.LinkedList;

/**
 *
 * @author ImproVisor
 */
public class SubstitutionDictionary {
    
    private LinkedList<UnaryProduction> subList;
    
    public SubstitutionDictionary()
    {
        subList = new LinkedList<UnaryProduction>();
    }
    
    public void addRule(UnaryProduction u) {
        subList.add(u);
    }
    
    public SubstituteList checkSubstitution(Chord c) {
        SubstituteList subs = new SubstituteList();
        for (UnaryProduction u : subList) {
            subs.addAll(u.checkSubstitution(c));
        }
        return subs;
    }  
}
