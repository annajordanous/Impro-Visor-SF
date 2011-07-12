/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

import imp.brickdictionary.*;
import java.awt.*;
import java.awt.Point;

/**
 *
 * @author ImproVisor
 */
public class RoadMapSettings {
    public int xOffset = 50;
    public int yOffset = 50;
    public int barsPerLine = 8;
    public int lineHeight = 60;
    public int measureLength = 80;
    public int lineSpacing = 20;
    public int beatsPerMeasure = 480;
    
    public Color gridLineColor = new Color(150,150,150);
    public Color gridBGColor = new Color(225,225,225);
    public Color lineColor = Color.BLACK;
    public Color textColor = Color.BLACK;
    public Color selectedColor = new Color(181, 213, 255);
    public Color brickBGColor = Color.WHITE;
    
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
    
    public int getBlockLength(Block block)
    {
        return (int) (block.getDuration() * measureLength)/beatsPerMeasure;
    }
    
    public int getLength(long dur)
    {
        return (int) (dur * measureLength)/beatsPerMeasure;
    }
    
    public int getCutoff()
    {
        return xOffset + getLineLength();
    }
    
    public long getCutoffBeat()
    {
        return beatsPerMeasure*barsPerLine;
    }
    
    public int getLineLength()
    {
        return barsPerLine * measureLength;
    }
    
    public int getLineOffset()
    {
        return lineHeight + lineSpacing;
    }
    
    public int getBlockHeight()
    {
        return lineHeight/3;
    }
    
    public Color getKeyColor(long key)
    {
        return keyColors[(int)key];
    }
    
    public int getLines(long beats)
    {
        int lines = (int) (beats/getCutoffBeat());
        return lines;
    }
    
    public Point getPosFromBeats(long beats)
    {
        int line = (int)getCutoffBeat();
        int numLines = (int)beats/line;
        int y = yOffset + numLines * (lineHeight + lineSpacing);
        int x = xOffset + getLength(beats % line);
        
        return new Point(x,y);
    }
}
