/*
 * 
 */

package imp.gui;

import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.BorderFactory;
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
    private int width, height;
    /** Width of each bar in the graph */
    private int barWidth;
    /** Unit of musical duration that corresponds to each bar, i.e. half bar, full bar, single beat */
    private int granularity;
    /** Total number of beats in the section of music to generate over */
    private int totalNumBeats;
    /** A list of the starting coordinate of each bar, size is the number of bars */
    private ArrayList<Dimension> bars;
    private Color color;


    public ComplexityPanel(int w, int h, int gran, int tot) {
        width = w;
        height = h;
        setSize(w, h);
        instantiateBars(gran, tot);
        setVisible(true);
        this.setBackground(Color.white);
        color = Color.RED;
        this.setBorder(BorderFactory.createLineBorder(Color.black));
    }

    /**
     * Set the buffer of this ComplexityPanel.
     */
    public void setBuffer(Image buffer) {
        this.buffer = buffer;
    }
    public void setGraphics() {
        graphics = buffer.getGraphics();
    }
    public void setColor(Color c) {
        color = c;
    }

    private void instantiateBars(int gran, int tot) {
        Dimension d;
        totalNumBeats = tot;
        granularity = gran;
        int numBars = totalNumBeats/granularity;
        barWidth = width/(numBars); //plus numbars to account for the gap
        bars = new ArrayList<Dimension>(numBars);
        for(int i = 0; i < width; i+=barWidth) {
            d = new Dimension(i, height/2); //TODO: give each bar a different starting height
            bars.add(d);
        }
        System.out.println("width: "+width);
        System.out.println("barwidth: "+barWidth);
        System.out.println("bars: "+bars.toString());
    }
    /**
     * Override the paint method to draw the buffer image on this panel's graphics.
     * This method is called implicitly whenever repaint() is called.
     */
    @Override
    public void paint(Graphics g) {
        System.out.println("Paint called.");
        drawAll();
        g.drawImage(buffer, 0, 0 , null);
    }

    /**
     * Updates the positions of all the bars on the screen.
     */
    private void drawAll() {
        System.out.println("draw all called");
        for (int i = 0; i < bars.size(); i++) {
            graphics.setColor(color);
            graphics.fillRect((int)bars.get(i).getWidth(), (int)bars.get(i).getHeight(), barWidth, height);
            graphics.setColor(Color.black);
            graphics.drawRect((int)bars.get(i).getWidth(), 0, barWidth, height);
        }
        repaint();
    }

    private void clear() {
        System.out.println("clear");
        graphics.clearRect(0, 0, width, height);
    }
    @Override
    public void update(Graphics g) {
        System.out.println("Update");
        paint(g);
    }

    /**
     * Adjusts the position of a single bar in the graph
     */
    public void moveSingleBar(int bar, int y, int h) {
        clear();
        bars.get(bar).setSize(bars.get(bar).getWidth(), y);
        repaint();
    }

    /**
     * Given an x coordinate, determines which bar is closest
     */
    public int determineBar(int x) {
        System.out.println("in determine bar, x is: " + x);
        System.out.println("in determine bar, x/barWidth is: " + x / barWidth);
        return (x - 1) / barWidth;
    }
    /**
     * Adjusts the bars in the graph according to the mouse position.
     */
    public void mouseHandler(MouseEvent e) {
        System.out.println("mouse dragged");
        if (!(e.getX() > width || e.getY() > height)
                && !(e.getX() < 0 || e.getY() < 0)) {
            int bar = determineBar(e.getX());
            moveSingleBar(bar, e.getY(), getHeight());
        }
    }
}
