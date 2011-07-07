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

import java.lang.Integer;

public class UnaryProduction extends AbstractProduction {
    
    private String head;
    private String terminal;
    private int cost;

    @Override
    public String getHead() {
        return head;
    }
            
    @Override
    public String getBody() {
        return terminal;
    }
    
    @Override
    public int getCost() {
        return cost;
    }
    
    public UnaryProduction() {
        head = null;
        terminal = null;
    }
    
    public UnaryProduction(String s) {
        String[] rule = s.split(" ", 3);
        head = rule[0];
        terminal = rule[1];
        cost = Integer.parseInt(rule[2]);
    }
    
    @Override
    public boolean isUnary() { return true; }
    @Override
    public boolean isBinary() { return false; }
    

    
    
    
    
}
