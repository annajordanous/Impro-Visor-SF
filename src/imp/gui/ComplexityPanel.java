/*
 * 
 */

package imp.gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Handling code for any sort of user-adjustable complexity panel.
 * @author Julia Botev
 */
public class ComplexityPanel extends JPanel  {

    /** Buffer for off-screen buffering of the image */
    private Image buffer;
    /** Graphics component for off-screen buffering of the image */
    private Graphics2D graphics;
    private int width;
    /** Bounds of the chord and bar number boxes */
    private int upperY, lowerY;
    private int barHeight;
    /** Unit of musical duration that corresponds to each bar, i.e. half bar, full bar, single beat */
    private int granularity;
    /** Total number of beats in the section of music to generate over */
    private int totalNumBeats;
    /** A list of the starting coordinate of each bar, size is the number of bars */
    private ArrayList<BarDimensions> bars;
    private Color color;
    /** Indicates whether or not this component is enabled or disabled */
    private boolean enabled;
    /** User-defined min and max bounds on the curve */
    private int maxUpper, minLower;
    public JTextField upperLimitField, lowerLimitField;
    public JCheckBox noComputeBox;

    /** Width of each bar in the graph */
    private static final int BAR_WIDTH = 30; //set for every graph
    private static final int TOTAL_HEIGHT = 200;
    private static final int GAP = 5;

    /** Alpha composite for painting a transparent grayed out image for disabled graphs */
    private AlphaComposite composite;

    public ComplexityPanel(int gran, int tot) {
        upperY = 25;
        lowerY = TOTAL_HEIGHT-25;
        barHeight = lowerY-upperY;
        totalNumBeats = tot;
        granularity = gran;
        int numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;
        instantiateBars(numBars);

        setSize(width, TOTAL_HEIGHT);
        color = Color.RED;

        float alpha = .75f;
        int type = AlphaComposite.SRC_OVER;
        composite = AlphaComposite.getInstance(type, alpha);
    }

    public void setTextFields(JTextField lower, JTextField upper) {
        lowerLimitField = lower;
        upperLimitField = upper;
        minLower = lowerY;
        maxUpper = upperY;

        lowerLimitField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int min = Integer.parseInt(lowerLimitField.getText());
                if (min < 0) {
                    min = 0;
                    lowerLimitField.setText(Integer.toString(min));
                }
                min = -1*(min-150)+upperY;
                if(min < maxUpper + GAP) {
                    min = maxUpper+GAP;
                    lowerLimitField.setText(Integer.toString(-1*((min-upperY)-150)));
                }
                minLower = min;
                resizeBars();
                drawAll();
            }
        });
        upperLimitField.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int max = Integer.parseInt(upperLimitField.getText());
                if (max > 150) {
                    max = 150;
                    upperLimitField.setText(Integer.toString(max));
                }
                max = -1*(max-150)+upperY;
                if (max > minLower-GAP) {
                    max = minLower-GAP;
                    upperLimitField.setText(Integer.toString(-1*((max-upperY)-150)));
                }
                maxUpper = max;
                resizeBars();
                drawAll();
            }
        });
    }
    public void setCheckBox(JCheckBox box) {
        noComputeBox = box;
        noComputeBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (!toCompute()) {
                    upperLimitField.setEnabled(false);
                    lowerLimitField.setEnabled(false);
                }
                else {
                    upperLimitField.setEnabled(true);
                    lowerLimitField.setEnabled(true);
                }
            }
        });
    }
    public void initBuffer(Color c) {
        this.buffer = this.createImage(width, TOTAL_HEIGHT);
        this.setGraphics();
        color = c;
    }
    public void setGraphics() {
        graphics = (Graphics2D) buffer.getGraphics();
    }
    @Override
    public void setEnabled(boolean e) {
        enabled = e;
    }
    public boolean toCompute() {
        if (noComputeBox.isSelected()) {
            return false;
        }
        return true;
    }

    private void instantiateBars(int numBars) {
        BarDimensions d;
        bars = new ArrayList<BarDimensions>(numBars);
        for(int i = 0; i <= width; i+=BAR_WIDTH) {
            d = new BarDimensions(i, barHeight/2+upperY, lowerY); //TODO: give each bar a different starting height
            bars.add(d);
        }
    }

    /** Returns an ArrayList of ArrayLists of two elements representing the upper and lower bounds of
      * each bar in the graph. Used for computing the complexity specified by the user */
    public ArrayList<ArrayList> valueRange() {
        ArrayList allVals = new ArrayList(bars.size());
        Integer lower, upper;
        for (int i = 0; i<bars.size(); i++) {
            ArrayList pair = new ArrayList(2);
            lower = -1*((bars.get(i).getLowerBound()-upperY)-150);
            upper = -1*((bars.get(i).getUpperBound()-upperY)-150);
            pair.add(lower);
            pair.add(upper);
        }
        return allVals;
    }

//////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Updates the positions of all the bars on the screen.
     */
    private void drawAll() {
        Composite oldComp = graphics.getComposite();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, TOTAL_HEIGHT);

        graphics.setColor(new Color(194, 194, 194)); // gray out part of the graph for a user-specified hard limit
        graphics.fillRect(0, upperY, width, maxUpper - upperY); //Fill the top range limit
        graphics.fillRect(0, minLower, width, lowerY - minLower); //Fill the bottom range limit

        //Fill in the bars of the graph
        for (int i = 0; i < bars.size(); i++) {
            graphics.setColor(color);
            graphics.fillRect((int) bars.get(i).getBarStart(), (int) bars.get(i).getUpperBound(),
                    BAR_WIDTH, (int) bars.get(i).getLowerBound() - (int) bars.get(i).getUpperBound());
            graphics.setColor(Color.black);
            graphics.drawRect((int) bars.get(i).getBarStart(), upperY, BAR_WIDTH, barHeight);
        }
        //draw bar numbers
        drawBarNumbers();
        graphics.drawRect(0, 0, width, upperY); //Bar Number box
        //draw chords
        graphics.drawRect(0, lowerY, width, TOTAL_HEIGHT); //Chord box
        graphics.drawRect(0, 0, width - 1, TOTAL_HEIGHT - 1);

        if (!toCompute()) {
            graphics.setComposite(composite);
            graphics.setColor(new Color(190, 190, 190)); //dark gray for non-computed attributes
            graphics.fillRect(0, 0, width, TOTAL_HEIGHT);
            graphics.setComposite(oldComp);
        }
        else if (!enabled) {
            graphics.setComposite(composite);
            graphics.setColor(new Color(230, 230, 230)); //lighter gray for disabled attributes
            graphics.fillRect(0, 0, width, TOTAL_HEIGHT);
            graphics.setComposite(oldComp);
        }

        repaint();
    }
    /** Redraws the number of bars when the user specifies a new granularity */
    public void redraw(int newGran) {
        clear();
        granularity = newGran;
        int numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;
        instantiateBars(numBars);
        setSize(width, TOTAL_HEIGHT);
        update(graphics);
        repaint();
    }

    private void drawBarNumbers() {
        for (int i = 0; i < bars.size(); i++) {
            graphics.drawString(((Integer)i).toString(), bars.get(i).getBarStart(), upperY-GAP);
        }
    }
    /**
     * Override the paint method to draw the buffer image on this panel's graphics.
     * This method is called implicitly whenever repaint() is called.
     */
    @Override
    public void paint(Graphics g) {
        drawAll();
        g.drawImage(buffer, 0, 0 , null);
    }
    private void clear() {
        graphics.clearRect(0, upperY, width, lowerY);
    }
    @Override
    public void update(Graphics g) {
        paint(g);
    }


///////////////////////////////////////////////////////////////////////////////////////////

    /** Resizes the upper limit of all the bars when the max range is changed so
     * none of the bars exceed the max value */
    public void resizeBars() {
        for (int i = 0; i < bars.size(); i++) {
            if (bars.get(i).getUpperBound() > minLower)
                bars.get(i).setUpperBound(minLower-GAP);
            if (bars.get(i).getUpperBound() < maxUpper)
                bars.get(i).setUpperBound(maxUpper);
            if (bars.get(i).getLowerBound() < maxUpper)
                bars.get(i).setLowerBound(maxUpper+GAP);
            if (bars.get(i).getLowerBound() > minLower)
                bars.get(i).setLowerBound(minLower);
        }
    }
    /**
     * Adjusts the Upper bound position of a single bar in the graph
     */
    public void moveUpperBound(int bar, int y) {
        clear();
        if (y < maxUpper) {
            y = maxUpper;
        } else if (y > minLower) {
            y = minLower - GAP;
        } else if (y > bars.get(bar).getLowerBound()) {
            y = bars.get(bar).getLowerBound() - GAP;
        }
        bars.get(bar).setUpperBound(y);
        repaint();
    }
    /**
     * Adjusts the lower bound position of a single bar in the graph
     */
    public void moveLowerBound(int bar, int y) {
        clear();
        if (y > minLower) {
            y = minLower;
        } else if (y < maxUpper) {
            y = maxUpper + GAP;
        } else if (y < bars.get(bar).getUpperBound()) {
            y = bars.get(bar).getUpperBound() + GAP;
        }
        bars.get(bar).setLowerBound(y);
        repaint();
    }
    /** Gets the upper edge of a specific bar */
    public int getBarUpper(int x) {
        int bar = determineBar(x);
        return bars.get(bar).getUpperBound();
    }
    /** Gets the lower edge of a specific bar */
    public int getBarLower(int x) {
        int bar = determineBar(x);
        return bars.get(bar).getLowerBound();
    }
    /**
     * Given an x coordinate, determines which bar is closest to where the mouse is
     */
    public int determineBar(int x) {
        return (x - 1) / BAR_WIDTH;
    }
    /**
     * Adjusts the bars in the graph according to the mouse position.
     */
    public void mouseHandler(MouseEvent e) {

        if (!(e.getX() > width || e.getY() > TOTAL_HEIGHT)
                && !(e.getX() < 0 || e.getY() < 0)) {
            int bar = determineBar(e.getX());
            int yCoord = e.getY();
            //Holding down the shift key moves the lower bound
            if (e.isShiftDown()) { // lower bound
                moveLowerBound(bar, yCoord);
            }
            else { 
                moveUpperBound(bar, yCoord);
            }
        }
    }
}