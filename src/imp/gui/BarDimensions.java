/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imp.gui;

/**
 * For use with the complexity graph panel
 * @author Julia Botev
 */
public class BarDimensions {
    private int barStart, UpperBound, lowerBound;

    public BarDimensions(int b, int t, int l) {
        barStart = b;
        UpperBound = t;
        lowerBound = l;
    }

    public void setUpperBound(int t) {
        UpperBound = t;
    }

    public void setLowerBound(int l) {
        lowerBound = l;
    }

    public int getBarStart() {
        return barStart;
    }

    public int getUpperBound() {
        return UpperBound;
    }

    public int getLowerBound() {
        return lowerBound;
    }
}
