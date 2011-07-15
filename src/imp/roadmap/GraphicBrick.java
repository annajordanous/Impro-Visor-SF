/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *

 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
        
        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();
        
        int xOffset = this.x;
        int yOffset = this.y;

        for( ChordBlock chord : chords ) {
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
    
    /* Drawing and junk lies below. DANGER: Extreme ugliness ahead */
    
    /**
     * Draws the brick at its current position
     * @param g graphics on which to draw the brick
     */
    public void draw(Graphics g)
    {
        // I split this up like this because drawing junk is a pain in the monk
        // - August
        drawBrick(g);
        drawChords(g);
    }
    
    /**
     * Draws the background and outline of the brick
     * @param g graphics on which to draw
     */
    private void drawBrick(Graphics g)
    {   
        Color bgColor;
        if(!isSelected)
            bgColor = settings.brickBGColor;
        else
            bgColor = settings.selectedColor;
        
        int[] wrap = settings.wrap(x+settings.getBlockLength(block));
        int endX = wrap[0];
        int lines = wrap[1];
        int endY = y+lines*settings.getLineOffset();
        int blockHeight = settings.getBlockHeight();
        int cutoff = settings.getCutoff();
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(settings.brickOutline);
        g2d.setColor(bgColor);
        
        if(lines > 0) {
            g2d.fillRect(x, y+blockHeight, cutoff - x, 2*blockHeight);
            
            g2d.setColor(settings.lineColor);
            g2d.drawLine(x, y+blockHeight, x, y+3*blockHeight);
            g2d.drawLine(x, y+3*blockHeight, cutoff, y+3*blockHeight);
            
            for( int line = 1; line < lines; line++ ) {
                int currentY = y+line*settings.getLineOffset();
                g2d.setColor(bgColor);
                g2d.fillRect(settings.xOffset,
                        currentY + blockHeight,
                        settings.getLineLength(), 2*blockHeight);
                
                g2d.setColor(settings.lineColor);
                g2d.drawLine(settings.xOffset, currentY + 3*blockHeight,
                        cutoff, currentY + 3*blockHeight);
            }
            
            g2d.setColor(bgColor);
            g2d.fillRect(settings.xOffset,
                    endY + blockHeight,
                    endX-settings.xOffset, 2*blockHeight);
            
            g2d.setColor(settings.lineColor);
            g2d.drawLine(settings.xOffset, endY + 3*blockHeight,
                    endX, endY + 3*blockHeight);
            g2d.drawLine(endX, endY + blockHeight, endX, endY + 3*blockHeight);
        } else {
            g2d.fillRect(x, y+blockHeight, endX - x, 2*blockHeight);
            
            g2d.setColor(settings.lineColor);
            g2d.drawLine(x, y+blockHeight, x, y+3*blockHeight);
            g2d.drawLine(x, y + 3*blockHeight,
                    endX, y + 3*blockHeight);
            g2d.drawLine(endX, y + blockHeight, endX, y + 3*blockHeight);
        }
        if(block.isSectionEnd())
            g2d.drawLine(endX-3, endY + blockHeight, endX-3, endY + 3*blockHeight);
    }
    
    /**
     * Draws the chords of the brick
     * @param g graphics on which to draw
     */
    private void drawChords(Graphics g)
    {
        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();
 
        int blockHeight = settings.getBlockHeight();
        int cutoff = settings.getCutoff();
        int xOffset = settings.xOffset;
        
        Graphics2D g2d = (Graphics2D)g;
        FontMetrics metrics = g2d.getFontMetrics();
        int fontOffset = (blockHeight + metrics.getAscent())/2;
        long currentBeats = 0;
        
        if(chords.size() > 1) {
            g2d.setColor(settings.textColor);
            g2d.drawString(block.getName(), x+2, y+blockHeight + fontOffset);
        }
        
        g2d.setStroke(settings.basicLine);
        
        g2d.setColor(settings.lineColor);
        
        for( ChordBlock chord : chords ) {
            int[] wrap = settings.wrap(x + settings.getLength(currentBeats));
            int currentX = wrap[0];
            int currentY = y + wrap[1] * settings.getLineOffset() + 2*blockHeight;
            
            int length = settings.getBlockLength(chord);
            int[] endWrap = settings.wrap(currentX + length);
            int endX = endWrap[0];
            int lines = endWrap[1];
            
            g2d.setColor(settings.textColor);
            g2d.drawString(chord.getName(), currentX+2, currentY + fontOffset);
            
            g2d.setColor(settings.lineColor);
            if(lines > 0) {
                g2d.drawLine(currentX, currentY, cutoff, currentY);
                
                for(int line = 1; line < lines; line++) {
                    g2d.drawLine(xOffset, currentY + line*settings.getLineOffset(),
                            cutoff, currentY + line*settings.getLineOffset());
                }
                
                g2d.drawLine(xOffset, currentY + lines*settings.getLineOffset(),
                        endX, currentY + lines*settings.getLineOffset());
            } else {
                g2d.drawRect(currentX, currentY, length, blockHeight);
            }
            
            currentBeats += chord.getDuration();
        }
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

        ArrayList<ChordBlock> chords = (ArrayList) block.flattenBlock();

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

        for( ChordBlock chord : chords )
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
