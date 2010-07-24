/*
 * 
 */

package imp.gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.JPanel;

/**
 * Handling code for any sort of user-adjustable complexity panel.
 * @author Julia Botev
 */
public class ComplexityPanel extends JPanel  {

    /** Buffer for off-screen buffering of the image */
    private Image buffer;
    /** Graphics component for off-screen buffering of the image */
    private Graphics graphics;
    private int width;
    private int upperY;
    private int bottomY;
    private int barHeight;
    /** Unit of musical duration that corresponds to each bar, i.e. half bar, full bar, single beat */
    private int granularity;
    /** Total number of beats in the section of music to generate over */
    private int totalNumBeats;
    /** A list of the starting coordinate of each bar, size is the number of bars */
    private ArrayList<BarDimensions> bars;
    private Color color;
    /** Indicates whether or not the user wants Impro-Visor to compute this attribute */
    private boolean compute;
    /** User-defined min and max bounds on the curve */
    private int maxUpper, minLower;

    /** Width of each bar in the graph */
    private static final int BAR_WIDTH = 30; //set for every graph
    private static final int TOTAL_HEIGHT = 200;

    public ComplexityPanel(int gran, int tot) {
        compute = true;
        upperY = 25;
        bottomY = TOTAL_HEIGHT-25;
        barHeight = bottomY-upperY;
        maxUpper = upperY;
        minLower = bottomY;
        totalNumBeats = tot;
        granularity = gran;
        int numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;
        instantiateBars(numBars);

        setSize(width, TOTAL_HEIGHT);
        color = Color.RED;
    }

    public void initBuffer(Color c) {
        this.buffer = this.createImage(width, TOTAL_HEIGHT);
        this.setGraphics();
        color = c;
    }
    public void setGraphics() {
        graphics = buffer.getGraphics();
    }
    public boolean compute() {
        return compute;
    }
    public void setCompute(boolean c) {
        compute = c;
    }
    public void setMax(int max) {
        maxUpper = max;
    }
    public void setMin(int min) {
        minLower = min;
    }

    public void redraw(int newGran) {
        granularity = newGran;
        int numBars = totalNumBeats/granularity;
        width = numBars*BAR_WIDTH;
        instantiateBars(numBars);
        setSize(width, TOTAL_HEIGHT);
        update(graphics);
    }

    private void instantiateBars(int numBars) {
        BarDimensions d;
        bars = new ArrayList<BarDimensions>(numBars);
        for(int i = 0; i <= width; i+=BAR_WIDTH) {
            d = new BarDimensions(i, barHeight/2+upperY, bottomY); //TODO: give each bar a different starting height
            bars.add(d);
        }
//        System.out.println("width: "+width);
//        System.out.println("barwidth: "+BAR_WIDTH);
//        System.out.println("bars: "+bars.toString());
    }
    /**
     * Override the paint method to draw the buffer image on this panel's graphics.
     * This method is called implicitly whenever repaint() is called.
     */
    @Override
    public void paint(Graphics g) {
//        System.out.println("Paint called.");
        drawAll();
        g.drawImage(buffer, 0, 0 , null);
    }

    /**
     * Updates the positions of all the bars on the screen.
     */
    private void drawAll() {
//        System.out.println("draw all called");
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, TOTAL_HEIGHT);
        graphics.setColor(Color.GRAY);
        graphics.fillRect(0, upperY, width, maxUpper-upperY); //Fill the top range limit
        graphics.fillRect(0, minLower, width, bottomY-minLower); //Fill the bottom range limit
        for (int i = 0; i < bars.size(); i++) {
            graphics.setColor(color);
            graphics.fillRect((int)bars.get(i).getBarStart(), (int)bars.get(i).getUpperBound(),
                    BAR_WIDTH, (int)bars.get(i).getLowerBound()-(int)bars.get(i).getUpperBound());
            graphics.setColor(Color.black);
            graphics.drawRect((int)bars.get(i).getBarStart(), upperY, BAR_WIDTH, barHeight);
        }
        drawBarNumbers();
        graphics.drawRect(0, 0, width, upperY); //Bar Number box
        //draw chords
        graphics.drawRect(0, bottomY, width, TOTAL_HEIGHT); //Chord box
        graphics.drawRect(0, 0, width-1, TOTAL_HEIGHT-1);
        repaint();
    }

    private void drawBarNumbers() {
        for (int i = 0; i < bars.size(); i++) {
            graphics.drawString(((Integer)i).toString(), bars.get(i).getBarStart(), upperY-2);
        }
    }

    private void clear() {
//        System.out.println("clear");
        graphics.clearRect(0, upperY, width, bottomY);
    }
    @Override
    public void update(Graphics g) {
//        System.out.println("Update");
        paint(g);
    }

    /**
     * Adjusts the Upper bound position of a single bar in the graph
     */
    public void moveUpperBound(int bar, int y) {
        clear();
        if (y > bars.get(bar).getLowerBound()) {
            bars.get(bar).setUpperBound(bars.get(bar).getLowerBound()-2);
        }
        else {
            bars.get(bar).setUpperBound(y);
        }
        repaint();
    }
    /**
     * Adjusts the lower bound position of a single bar in the graph
     */
    public void moveLowerBound(int bar, int y) {
        clear();
        if (y < bars.get(bar).getUpperBound()) {
            bars.get(bar).setLowerBound(bars.get(bar).getUpperBound()+2);
        }
        else {
            bars.get(bar).setLowerBound(y);
        }
        repaint();
    }

    public int getBarUpper(int x) {
        int bar = determineBar(x);
        return bars.get(bar).getUpperBound();
    }
    public int getBarLower(int x) {
        int bar = determineBar(x);
        return bars.get(bar).getLowerBound();
    }

    /**
     * Given an x coordinate, determines which bar is closest
     */
    public int determineBar(int x) {
        return (x - 1) / BAR_WIDTH;
    }
    /**
     * Adjusts the bars in the graph according to the mouse position.
     */
    public void mouseHandler(MouseEvent e) {
//        System.out.println("mouse dragged");
        if (!(e.getX() > width || e.getY() > TOTAL_HEIGHT)
                && !(e.getX() < 0 || e.getY() < 0)) {
            int bar = determineBar(e.getX());
            int yCoord = e.getY();
            if (yCoord > minLower) {
                yCoord = minLower;
            }
            else if(yCoord < maxUpper) {
                yCoord = maxUpper;
            }
            //Holding down the shift key moves the lower bound
            if (e.isShiftDown()) { 
                moveLowerBound(bar, yCoord);
            }
            else { 
                moveUpperBound(bar, yCoord);
            }
        }
    }
}