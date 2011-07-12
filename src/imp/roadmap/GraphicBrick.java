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
        
    private Block block;
    private int x = 0;
    private int y = 0;
    private boolean isSelected = false;
    
    private RoadMapSettings settings;
    
    /**
     * Constructor to create a GraphicBrick from a block
     * @param block 
     * block to be graphically represented
     */
    public GraphicBrick(Block block, RoadMapSettings settings)
    {
        this.block = block;
        this.settings = settings;
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
     * set the x and y coordinates of the brick
     * @param point the point
     */
    public void setPos(Point point)
    {
        this.x = point.x;
        this.y = point.y;
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
        int cutoffLine = settings.getCutoff();
        
        if( x > cutoffLine || x < settings.xOffset )
            return false;
        
        ArrayList<Chord> chords = (ArrayList) block.flattenBlock();
        
        int xOffset = this.x;
        int yOffset = this.y;

        for( Chord chord : chords ) {
            int length = settings.getBlockLength(chord);
            
            if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + settings.lineHeight)
                return true;
            
            while ( xOffset + length >= cutoffLine )
            {
                xOffset -= settings.getLineLength();
                yOffset += settings.lineHeight + settings.lineSpacing;
                
                if ( x > xOffset && x < xOffset + length && 
                    y > yOffset && y < yOffset + settings.lineHeight)
                    return true;
            }
            
            xOffset += length;
            
        }
        
        return false;
    }
    
    public int getLength()
    {
        return settings.getBlockLength(block);
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
    private void drawBackground(Graphics g) //TODO fix to use beats instead of just coordinates
    {
        int xOffset = x;
        int yOffset = y;
        int cutoffLine = settings.getCutoff();
        int length = getLength();
        
        Color bgColor = settings.brickBGColor;
        
        if(isSelected)
            bgColor = settings.selectedColor;

        while ( xOffset + length > cutoffLine ) {
            
            g.setColor(bgColor);
            g.fillRect(xOffset, yOffset, cutoffLine-xOffset, settings.lineHeight);
            
            length -= cutoffLine - xOffset;
            
            xOffset = settings.xOffset;
            yOffset += settings.lineHeight + settings.lineSpacing;
        }
        
        g.setColor(bgColor);
        g.fillRect(xOffset, yOffset, length, settings.lineHeight);
    }
    
    /**
     * Draws the lines of the brick
     * @param g graphics on which to draw
     */
    private void drawLines(Graphics g) //TODO fix to use beats instead of just coordinates
    {
        ArrayList<Chord> chords = (ArrayList) block.flattenBlock();
        
        Graphics2D g2d = (Graphics2D)g;
        
        int xOffset = this.x;
        int yOffset = this.y;
        boolean isBrick = chords.size() > 1;
        int cutoffLine = settings.getCutoff();
        int blockHeight = settings.getBlockHeight();
        
        Color textColor = settings.textColor;
        
        if(isBrick) {
            g2d.setColor(textColor);
            g2d.drawString(block.getName(), x+5, y+settings.lineHeight/2+5);
        }
        g2d.setStroke(settings.brickOutline);
        
        g2d.setColor(settings.lineColor);
        g2d.drawLine(xOffset, yOffset+blockHeight, xOffset, yOffset+settings.lineHeight);
        
        for( Iterator<Chord> it = chords.iterator(); it.hasNext(); )  
        {
            Chord chord = it.next();
            int length = settings.getBlockLength(chord);
            
            g2d.setStroke(settings.basicLine);
            g2d.drawString(chord.getName(), xOffset+5, yOffset+5*blockHeight/2+5);
            g2d.drawLine(xOffset, yOffset+2*blockHeight, xOffset, yOffset+settings.lineHeight);
            
            while ( xOffset + length > cutoffLine ) {
                System.out.println("Breaking line");
                
                if(isBrick) 
                    g.drawLine(xOffset, yOffset+blockHeight, cutoffLine, yOffset+blockHeight);
                
                g2d.drawLine(xOffset, yOffset+2*blockHeight, cutoffLine, yOffset+2*blockHeight);
                
                g2d.setStroke(settings.brickOutline);
                g2d.drawLine(xOffset, yOffset+3*blockHeight, cutoffLine, yOffset+3*blockHeight);
                
                length -= cutoffLine - xOffset;
                
                xOffset = settings.xOffset;
                yOffset += settings.lineHeight + settings.lineSpacing;
            }
            
            g2d.setStroke(settings.basicLine);
            if(isBrick) {
                g2d.drawLine(xOffset, yOffset+blockHeight, xOffset+length, yOffset+blockHeight);
            }

            g2d.drawLine(xOffset, yOffset+2*blockHeight, xOffset+length, yOffset+2*blockHeight);
            
            g2d.setStroke(settings.brickOutline);
            g2d.drawLine(xOffset, yOffset+3*blockHeight, xOffset+length, yOffset+3*blockHeight);
            
            xOffset += length;
            
            g2d.setStroke(settings.basicLine);
            g2d.drawLine(xOffset, yOffset+2*blockHeight, xOffset, yOffset+3*blockHeight);
            
            if(xOffset >= cutoffLine && it.hasNext()) {
                xOffset = settings.xOffset;
                yOffset += settings.lineHeight+settings.lineSpacing;
            }
        }
        
        g2d.setStroke(settings.brickOutline);
        g2d.drawLine(xOffset, yOffset+blockHeight, xOffset, yOffset+settings.lineHeight);
        //g2d.drawLine(xOffset-2, yOffset, xOffset-2, yOffset+settings.lineHeight);
        if(block.isSectionEnd())
            g2d.drawLine(xOffset-6, yOffset+blockHeight, xOffset-6, yOffset+settings.lineHeight);
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
            g.setColor(settings.selectedColor);
        else
            g.setColor(settings.brickBGColor);

        int totalLength = settings.getBlockLength(block);
        int blockHeight = settings.getBlockHeight();
        g.fillRect(x, y, totalLength, settings.lineHeight);

        g.setColor(settings.getKeyColor(block.getKey()));
        
        g.fillRect(x, y, totalLength, blockHeight);

        ArrayList<Chord> chords = (ArrayList) block.flattenBlock();

        if( chords.size() > 1 )   // distinguish between chords and bricks
        {                               // possibly unideal
            g.setColor(settings.textColor); //TODO use metrics
            
            //Key
            g.drawRect(x, y, totalLength, blockHeight);
            g.drawString(block.getKeyName(), x+5, y+blockHeight/2+5);

            //Name
            g.drawRect(x, y+blockHeight, totalLength, blockHeight);
            g.drawString(block.getName(), x+5, y+3*blockHeight/2+5);
        }
        int xOffset = 0;

        for( Chord chord : chords )
        {      
            g.setColor(settings.lineColor);
            int length = settings.getBlockLength(chord);
            g.drawRect(x+xOffset, y+2*blockHeight, length, blockHeight);
            
            g.setColor(settings.textColor); //TODO use metrics
            String chordName = chord.getName();
            g.drawString(chordName, x+xOffset+5, y+5*blockHeight/2+5);

            xOffset += length;
        }
    }
}
