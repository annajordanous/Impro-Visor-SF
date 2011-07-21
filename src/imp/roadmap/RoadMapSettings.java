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

import imp.brickdictionary.*;
import java.awt.*;

/**
 *
 * @author August Toman-Yih
 */
public class RoadMapSettings {
    public int xOffset = 50;
    public int yOffset = 70;
    public int barsPerLine = 8;
    public int lineHeight = 60;
    public int measureLength = 80;
    public int lineSpacing = 20;
    public int slotsPerMeasure = 480;
    public int beatsPerMeasure = 4;
    
    public Color gridLineColor = new Color(150,150,150);
    public Color gridBGColor = new Color(225,225,225);
    public Color lineColor = Color.BLACK;
    public Color textColor = Color.BLACK;
    public Color selectedColor = new Color(181, 213, 255);
    public Color brickBGColor = Color.WHITE;
    public Color joinBGColor = new Color(255, 255, 171);
    
    public Color[] keyColors = {new Color(250, 220, 100), // C
                                        new Color(200, 110, 255), // Db
                                        new Color(200, 255, 100), // D
                                        new Color(255, 150, 150), // Eb
                                        new Color(90, 220, 255),  // E
                                        new Color(255, 200, 100), // F
                                        new Color(155, 155, 255), // Gb
                                        new Color(255, 255, 100), // G
                                        new Color(255, 150, 255), // Ab
                                        new Color(150, 255, 220), // A
                                        new Color(255, 180, 150), // Bb
                                        new Color(100, 170, 255)};// B
    
    public BasicStroke brickOutline = new BasicStroke(2);
    public BasicStroke basicLine    = new BasicStroke(1);
    public BasicStroke cursorLine   = new BasicStroke(2);
    
    public Font basicFont = new Font("Dialog", Font.PLAIN, 12);
    public Font titleFont = new Font("Dialog", Font.PLAIN, 24);
    
    //Not sure if a lot of this belongs here
    
    /**
     * returns the length of the block in the current settings
     * @param block the block
     * @return the length
     */
    public int getBlockLength(Block block)
    {
        return (int) (block.getDuration() * measureLength)/slotsPerMeasure;
    }
    
    /**
     * returns the length of a duration in the current settings
     * @param dur the duration
     * @return the length
     */
    public int getLength(long dur)
    {
        return (int) (dur * measureLength)/slotsPerMeasure;
    }
    
    /**
     * returns the x cutoff in the current settings
     * @return the x cutoff
     */
    public int getCutoff()
    {
        return xOffset + getLineLength();
    }
    
    /**
     * returns the number of beats per line
     * @return the number of beats per line
     */
    public long getSlotsPerLine()
    {
        return slotsPerMeasure*barsPerLine;
    }
    
    /**
     * returns the length of a line
     * @return the length
     */
    public int getLineLength()
    {
        return barsPerLine * measureLength;
    }
    
    /**
     * returns the distance between each line
     * @return the distance between each line
     */
    public int getLineOffset()
    {
        return lineHeight + lineSpacing;
    }
    
    /**
     * returns the height of a block
     * @return the height of a block
     */
    public int getBlockHeight()
    {
        return lineHeight/3;
    }
    
    /**
     * returns the color of the given key
     * @param key the key
     * @return the color
     */
    public Color getKeyColor(long key)
    {
        return keyColors[(int)key % 12];
    }
    
    /**
     * returns the number of lines taken up by this number of beats
     * @param beats number of beats
     * @return number of lines
     */
    public int getLines(long beats)
    {
        int lines = (int) (beats/getSlotsPerLine());
        return lines;
    }
    
    /**
     * 
     * @param beats
     * @return 
     */
    public Point getPosFromSlots(long beats)
    {
        int line = (int)getSlotsPerLine();
        int numLines = (int)beats/line;
        int y = yOffset + numLines * (lineHeight + lineSpacing);
        int x = xOffset + getLength(beats % line);
        
        return new Point(x,y);
    }
    
    public int getSlotsPerBeat()
    {
        return slotsPerMeasure/beatsPerMeasure;
    }
    
    /**
     * Wraps x coordinate
     * @param x
     * @return the new x coordinate, the number of lines
     */
    public int[] wrap(int x)
    {
        int lines = (x - xOffset)/getLineLength();
        int endX = xOffset + ((x - xOffset) % getLineLength());
        return new int[]{endX, lines};
    }
    
    public long[] wrapFromSlots(long b)
    {
        long lines = getLines(b);
        long endX = (b%getSlotsPerLine());
        return new long[]{endX, lines};
    }

    public static String trimString(String string, int length, FontMetrics metrics)
    {
        int stringLength = metrics.stringWidth(string);
        if(stringLength < length)
            return string;
        //System.out.println(string+" is too long. Length: " + stringLength + " Desired: " + length);
        return string.substring(0, (string.length() * length)/stringLength - 2).concat("É");
    }
}