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
    
    public static int BLOCK_HEIGHT = RoadMapPanel.LINE_HEIGHT/3;
    public static int MEASURE_LENGTH = RoadMapPanel.MEASURE_LENGTH;
    public static int LINE_LENGTH = RoadMapPanel.BARS_PER_LINE*MEASURE_LENGTH;
    public static int CUTOFF_POINT = LINE_LENGTH + RoadMapPanel.X_OFFSET;
    public static int X_OFFSET = RoadMapPanel.X_OFFSET;
    public static int LINE_SPACING = RoadMapPanel.LINE_SPACING;
        
    public static Color[] KEY_COLORS = {new Color(250, 220, 100), // C
                                        new Color(200, 110, 255),  // Db
                                        new Color(200, 255, 100), // D
                                        new Color(255, 150, 150), // Eb
                                        new Color(90, 220, 255), // E
                                        new Color(255, 200, 100),  // F
                                        new Color(155, 155, 255), // Gb
                                        new Color(255, 255, 100), // G
                                        new Color(255, 150, 255), // Ab
                                        new Color(150, 255, 220), // A
                                        new Color(255, 180, 150),  // Bb
                                        new Color(100, 170, 255)};// B
    
    public static Color LINE_COLOR = Color.BLACK;
    public static Color SELECTED_COLOR = new Color(181, 213, 255);
    public static Color BG_COLOR = Color.WHITE;
    public static Color TEXT_COLOR = Color.BLACK;
    
    private int x = 0;
    private int y = 0;
    
    private long duration = 0;
    
    private String name;
    private long key;
    
    public Boolean selected = false;
    
    private Block brick;
    
    
    public GraphicBrick()
    {
    }

    public GraphicBrick(Block brick)
    {
        name = brick.getName();
        
        key = brick.getKey();
        
        this.brick = brick;
        
        duration = brick.getDuration();
    }
    
    public GraphicBrick(GraphicBrick brick)
    {
        name = brick.name;
        
        key = brick.key;
        
        if(brick.brick instanceof Brick)
            this.brick = new Brick((Brick)brick.brick);
        else
            this.brick = new Chord((Chord)brick.brick);
        
        duration = brick.duration;
    }
    
    public void draw(Graphics g)
    {   
        drawAt(g, x, y);
    }
    
    public void drawNoWrap(Graphics g)
    {
        if(selected)
            g.setColor(SELECTED_COLOR);
        else
            g.setColor(BG_COLOR);

        int totalLength = ((int)duration*MEASURE_LENGTH)/480;
        
        g.fillRect(x, y, totalLength, 3*BLOCK_HEIGHT);

        g.setColor(KEY_COLORS[(int)key]);
        
        g.fillRect(x, y, totalLength, BLOCK_HEIGHT);

        ArrayList<Chord> chords = (ArrayList) brick.flattenBlock();

        if( chords.size() > 1 )   // distinguish between chords and bricks
        {                               // possibly unideal
            g.setColor(TEXT_COLOR);
            
            //Key
            g.drawRect(x, y, totalLength, BLOCK_HEIGHT);
            g.drawString(keyName(), x+5, y+BLOCK_HEIGHT/2+5);

            //Name
            g.drawRect(x, y+BLOCK_HEIGHT, totalLength, BLOCK_HEIGHT);
            g.drawString(name, x+5, y+3*BLOCK_HEIGHT/2+5);
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
       
    public void drawAt(Graphics g, int x, int y)
    {
        drawBackground(g);
        drawLines(g);
    }  
    
    private void drawLines(Graphics g)
    {
        ArrayList<Chord> chords = (ArrayList) brick.flattenBlock();
        
        int xOffset = this.x;
        int yOffset = this.y;
        boolean isBrick = chords.size() > 1;
        
        Color textColor = TEXT_COLOR;
        
        //if(selected)
        //    textColor = Color.WHITE;
        
        if(isBrick) {
            g.setColor(textColor);
            //g.drawString(keyName(), x+5, y+BLOCK_HEIGHT/2+5);
            g.drawString(name, x+5, y+3*BLOCK_HEIGHT/2+5);
        }
        
        g.setColor(LINE_COLOR);
        g.drawLine(xOffset, yOffset, xOffset, yOffset+3*BLOCK_HEIGHT);
        
        for( Iterator<Chord> it = chords.iterator(); it.hasNext(); )  
        {
            Chord chord = it.next();
            int length = (int)(chord.getDuration() * MEASURE_LENGTH)/480;
            
            g.drawString(chord.getName(), xOffset+5, yOffset+5*BLOCK_HEIGHT/2+5);
            g.drawLine(xOffset, yOffset+2*BLOCK_HEIGHT, xOffset, yOffset+3*BLOCK_HEIGHT);
            
            while ( xOffset + length > CUTOFF_POINT ) {
                System.out.println("Breaking line");
                
                if(isBrick) {
                    //g.drawLine(xOffset, yOffset, CUTOFF_POINT, yOffset);
                    g.drawLine(xOffset, yOffset+BLOCK_HEIGHT, CUTOFF_POINT, yOffset+BLOCK_HEIGHT);
                }
                
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
        if(brick.isSectionEnd())
            g.drawLine(xOffset-6, yOffset, xOffset-6, yOffset+3*BLOCK_HEIGHT);
    }
    
    private void drawBackground(Graphics g)
    {
        int xOffset = x;
        int yOffset = y;
        int length = ((int)duration * MEASURE_LENGTH)/480;
        
        Color bgColor = BG_COLOR;
        
        if(selected)
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
    
    public void setDuration(long duration)
    {   
        brick.adjustDuration(duration);
        this.duration = brick.getDuration();
    }
    
    public long duration()
    {
        return duration;
    }
    
    public void setKey(String key)
    {
        this.key = BrickLibrary.keyNameToNum(key);
    }
    
    public void setKey(long key)
    {
        long diff = (12 - this.key + key)%12;
        this.key = key;
        brick.transpose(diff);
    }
    
    public void transpose(long diff)
    {
        brick.transpose(diff);
        this.key = brick.getKey();
    }
    
    public long key()
    {
        return key;
    }
    
    public String keyName()
    {
        return BrickLibrary.keyNumToName(key);
    }
    
    public String name()
    {
        return name;
    }
    
    public String type()
    {
        return ((Brick)brick).getType();
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public ArrayList<GraphicBrick> seperate()
    {
        ArrayList<Block> bricks = (ArrayList)brick.getSubBlocks();
        System.out.println(bricks);
        ArrayList<GraphicBrick> pieces = new ArrayList<GraphicBrick>();
        
        for(Iterator<Block> it = bricks.iterator(); it.hasNext(); ) {
            pieces.add(new GraphicBrick(it.next()));
        }
        
        return pieces;
    }
    
    public ArrayList<GraphicBrick> flatten()
    {
        ArrayList<Block> bricks = (ArrayList)brick.flattenBlock();
        
        ArrayList<GraphicBrick> pieces = new ArrayList<GraphicBrick>();
        
        for(Iterator<Block> it = bricks.iterator(); it.hasNext(); ) {
            pieces.add(new GraphicBrick(it.next()));
        }
        
        return pieces;
    }
        
    public Block getBlock()
    {
        return brick;
    }
    
    public int x()
    {
        return x;
    }
    
    public int y()
    {
        return y;
    }
    
    public void setPos(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    
    public Boolean contains(int x, int y)
    {
        //System.out.println("Finding " + x + " " + y);
        
        if( x > CUTOFF_POINT || x < X_OFFSET )
            return false;
        
        ArrayList<Chord> chords = (ArrayList) brick.flattenBlock();
        
        int xOffset = this.x;
        int yOffset = this.y;

        for( Chord chord : chords ) {
            int length = (int)(chord.getDuration() * MEASURE_LENGTH)/480;
            
            //System.out.println("Checking " + xOffset + " " + yOffset);
            
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
    

    public boolean isValidScale(long scale)
    {
        ArrayList<Chord> chords = (ArrayList) brick.flattenBlock();
        
        for( Chord chord : chords )
            if( chord.getDuration()%scale != 0)
                return false;
        
        return true;
    }
    
    public void adjustDuration(long scale)
    {
        brick.adjustDuration(scale);
        duration = brick.getDuration();
    }
    
    public void setSelected(boolean selected)
    {
        this.selected = selected;
    }
    

}
