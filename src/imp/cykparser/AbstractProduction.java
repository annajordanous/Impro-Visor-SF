/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.cykparser;

/**AbstractProduction
 * An abstract class for production rules
 * @author Xanda
 */


public abstract class AbstractProduction {

    // Getters for data members of the production
    abstract public String getHead();
    abstract public String getBody();
    
    // Tests for rule type
    abstract public boolean isUnary();
    abstract public boolean isBinary();
    
    // Getter for the cost of a brick/node produced by this rule
    abstract public int getCost();
}
