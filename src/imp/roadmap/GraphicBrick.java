/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import imp.brickdictionary.*;

/**
 *
 * @author August Toman-Yih
 */
public class GraphicBrick {
    
    //TODO make these all dependent on which roadmap
    public static int BLOCK_HEIGHT = RoadMapPanel.LINE_HEIGHT/3;
    public static int MEASURE_LENGTH = RoadMapPanel.MEASURE_LENGTH;
    public static int LINE_LENGTH = RoadMapPanel.BARS_PER_LINE*MEASURE_LENGTH;
    public static int CUTOFF_POINT = LINE_LENGTH + RoadMapPanel.X_OFFSET;
    public static int X_OFFSET = RoadMapPanel.X_OFFSET;
    public static int LINE_SPACING = RoadMapPanel.LINE_SPACING;
    public static int BEATS_PER_MEASURE = 480;
            
    public static Color LINE_COLOR = Color.BLACK;
    public static Color SELECTED_COLOR = new Color(181, 213, 255);
    public static Color BG_COLOR = Color.WHITE;
    public static Color TEXT_COLOR = Color.BLACK;
    
    private Block block;
    private int x = 0;
    private int y = 0;
    private boolean isSelected = false;
    
    /**
     * Constructor to create a GraphicBrick from a block
     * @param block 
     * block to be graphically represented
     */
    public GraphicBrick(Block block)
    {
        this.block = block;
    }
    
    public Block getBrick()
    {
        return block;
    }
    
    /**
     * returns the x position
     * @return the x position
     */
    public int x()
    {
        return x;
    }
    
    /**
     * returns the y position
     * @return the y position
     */
    public int y()
    {
        return y;
    }
    
    /**
     * set the x and y coordinates of the brick
     * @param x the new x coordinate
     * @param y the new y coordinate
     */
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    /**
     * returns whether or not the brick is currently selected
     * @return whether the brick is selected
     */
    public boolean isSelected()
    {
        return isSelected;
    }
    
    /**
     * Sets whether the brick is selected
     * @param value selected or not
     */
    public void setSelected(boolean value)
    {
        isSelected = value;
    }
         
    /**
     * Checks if the point x,y is contained within the brick
     * @param x x-coordinate of the point
     * @param y y-coordinate of the point
     * @return if the point is within the brick
     */
    public boolean contains(int x, int y)
    {
        if( x > CUTOFF_POINT || x < X_OFFSET )
            return false;
        
        ArrayList<Chord> chords = (ArrayList) block.flattenBlock();
        
        int xOffset = this.x;
        int yOffset = this.y;

        for( Chord chord : chords ) {
            int length = (int)(chord.getDuration() * MEASURE_LENGTH)/BEATS_PER_MEASURE;
            
            if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + 3*BLOCK_HEIGHT)
                return true;
            
            while ( xOffset + length >= CUTOFF_POINT )
            {
                xOffset -= LINE_LENGTH;
                yOffset += 3*BLOCK_HEIGHT + LINE_SPACING;
                
                if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + 3*BLOCK_HEIGHT)
                    return true;
            }
            
            xOffset += length;
            
        }
        
        return false;
    }
    
    public int getLength()
    {
        return (int)(MEASURE_LENGTH * block.getDuration())/BEATS_PER_MEASURE;
    }
    
    /* Drawing and junk lies below */
    
    /**
     * Draws the brick at its current position
     * @param g graphics on which to draw the brick
     */
    public void draw(Graphics g)
    {
        drawBackground(g);
        drawLines(g);
    }
    
    /**
     * Draws the background of the brick
     * @param g graphics on which to draw
     */
    private void drawBackground(Graphics g)
    {
        int xOffset = x;
        int yOffset = y;
        int length = (int)(block.getDuration() * MEASURE_LENGTH)/BEATS_PER_MEASURE;
        
        Color bgColor = BG_COLOR;
        
        if(isSelected)
            bgColor = SELECTED_COLOR;

        while ( xOffset + length > CUTOFF_POINT ) {
            
            g.setColor(bgColor);
            g.fillRect(xOffset, yOffset, CUTOFF_POINT-xOffset, 3*BLOCK_HEIGHT);
            
            length -= CUTOFF_POINT - xOffset;
            
            xOffset = X_OFFSET;
            yOffset += 3*BLOCK_HEIGHT + LINE_SPACING;
        }
        
        g.setColor(bgColor);
        g.fillRect(xOffset, yOffset, length, 3*BLOCK_HEIGHT);
    }
    
    /**
     * Draws the lines of the brick
     * @param g graphics on which to draw
     */
    private void drawLines(Graphics g)
    {
        ArrayList<Chord> chords = (ArrayList) block.flattenBlock();
        
        int xOffset = this.x;
        int yOffset = this.y;
        boolean isBrick = chords.size() > 1;
        
        Color textColor = TEXT_COLOR;
        
        if(isBrick) {
            g.setColor(textColor);
            g.drawString(block.getName(), x+5, y+3*BLOCK_HEIGHT/2+5);
        }
        
        g.setColor(LINE_COLOR);
        g.drawLine(xOffset, yOffset, xOffset, yOffset+3*BLOCK_HEIGHT);
        
        for( Iterator<Chord> it = chords.iterator(); it.hasNext(); )  
        {
            Chord chord = it.next();
            int length = (int)(chord.getDuration() * MEASURE_LENGTH)/BEATS_PER_MEASURE;
            
            g.drawString(chord.getName(), xOffset+5, yOffset+5*BLOCK_HEIGHT/2+5);
            g.drawLine(xOffset, yOffset+2*BLOCK_HEIGHT, xOffset, yOffset+3*BLOCK_HEIGHT);
            
            while ( xOffset + length > CUTOFF_POINT ) {
                System.out.println("Breaking line");
                
                if(isBrick) 
                    g.drawLine(xOffset, yOffset+BLOCK_HEIGHT, CUTOFF_POINT, yOffset+BLOCK_HEIGHT);
                
                g.drawLine(xOffset, yOffset+2*BLOCK_HEIGHT, CUTOFF_POINT, yOffset+2*BLOCK_HEIGHT);
                g.drawLine(xOffset, yOffset+3*BLOCK_HEIGHT, CUTOFF_POINT, yOffset+3*BLOCK_HEIGHT);
                
                length -= CUTOFF_POINT - xOffset;
                
                xOffset = X_OFFSET;
                yOffset += 3*BLOCK_HEIGHT + LINE_SPACING;
            }
            
            if(isBrick) {
                //g.drawLine(xOffset, yOffset, xOffset+length, yOffset);
                g.drawLine(xOffset, yOffset+BLOCK_HEIGHT, xOffset+length, yOffset+BLOCK_HEIGHT);
            }

            g.drawLine(xOffset, yOffset+2*BLOCK_HEIGHT, xOffset+length, yOffset+2*BLOCK_HEIGHT);
            g.drawLine(xOffset, yOffset+3*BLOCK_HEIGHT, xOffset+length, yOffset+3*BLOCK_HEIGHT);
            
            xOffset += length;
            
            g.drawLine(xOffset, yOffset+2*BLOCK_HEIGHT, xOffset, yOffset+3*BLOCK_HEIGHT);
            
            if(xOffset >= CUTOFF_POINT && it.hasNext()) {
                xOffset = X_OFFSET;
                yOffset += 3*BLOCK_HEIGHT+LINE_SPACING;
            }
        }
        
        g.drawLine(xOffset, yOffset, xOffset, yOffset+3*BLOCK_HEIGHT);
        g.drawLine(xOffset-2, yOffset, xOffset-2, yOffset+3*BLOCK_HEIGHT);
        if(block.isSectionEnd())
            g.drawLine(xOffset-6, yOffset, xOffset-6, yOffset+3*BLOCK_HEIGHT);
    }
    
    /**
     * Draws the bricks at a specified location without wrapping
     * @param g graphics on which to draw
     * @param x x-coordinate
     * @param y y-coordinate
     */
    public void drawAt(Graphics g, int x, int y)
    {
        if(isSelected)
            g.setColor(SELECTED_COLOR);
        else
            g.setColor(BG_COLOR);

        int totalLength = (int)(block.getDuration()*MEASURE_LENGTH)/BEATS_PER_MEASURE;
        
        g.fillRect(x, y, totalLength, 3*BLOCK_HEIGHT);

        g.setColor(RoadMapPanel.KEY_COLORS[(int)block.getKey()]);
        
        g.fillRect(x, y, totalLength, BLOCK_HEIGHT);

        ArrayList<Chord> chords = (ArrayList) block.flattenBlock();

        if( chords.size() > 1 )   // distinguish between chords and bricks
        {                               // possibly unideal
            g.setColor(TEXT_COLOR);
            
            //Key
            g.drawRect(x, y, totalLength, BLOCK_HEIGHT);
            g.drawString(block.getKeyName(), x+5, y+BLOCK_HEIGHT/2+5);

            //Name
            g.drawRect(x, y+BLOCK_HEIGHT, totalLength, BLOCK_HEIGHT);
            g.drawString(block.getName(), x+5, y+3*BLOCK_HEIGHT/2+5);
        }
        int xOffset = 0;

        for( Chord chord : chords )
        {      
            g.setColor(LINE_COLOR);
            int length = (int)(chord.getDuration() * MEASURE_LENGTH)/480;
            g.drawRect(x+xOffset, y+2*BLOCK_HEIGHT, length, BLOCK_HEIGHT);
            
            g.setColor(TEXT_COLOR);
            String chordName = chord.getName();
            g.drawString(chordName, x+xOffset+5, y+5*BLOCK_HEIGHT/2+5);

            xOffset += length;
        }
    }
}
