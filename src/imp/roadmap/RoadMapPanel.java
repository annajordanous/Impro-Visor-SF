/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import imp.brickdictionary.*;
import imp.cykparser.PostProcessing;

/**
 *
 * @author ImproVisor
 */
public class RoadMapPanel extends JPanel{
    
    public static int X_OFFSET = 50;
    public static int Y_OFFSET = 50;
    public static int BARS_PER_LINE = 8;
    public static int DIVIDER_THICKNESS = 1;
    public static int LINE_HEIGHT = 60;
    public static int MEASURE_LENGTH = 80;
    public static int LINE_SPACING = 20;
    public static int BEATS_PER_MEASURE = 480;
    
    public static Color GRID_LINE_COLOR = new Color(150,150,150);
    public static Color GRID_BG_COLOR = new Color(225,225,225);
    public static Color LINE_COLOR = Color.BLACK;
    
    public static Color[] KEY_COLORS = {new Color(250, 220, 100), // C
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
    
    public int numLines = 1;
    
    public int selectionStart = -1;
    public int selectionEnd = -1;
    
    private Image buffer;
    
    private RoadMap roadMap = new RoadMap();
    private ArrayList<GraphicBrick> graphicMap = new ArrayList();
    
    RoadMapFrame view;
    
    /** Creates new form RoadMapPanel */
    public RoadMapPanel(RoadMapFrame view)
    {
        this.view = view;
    }
    
    public RoadMap getRoadMap()
    {
        return roadMap;
    }
    
    public int getNumBlocks()
    {
        return roadMap.size();
    }
    
    public void placeBricks()
    {
        int currentX = 0;// X_OFFSET;
        int currentY = Y_OFFSET;
        //int currentBars = 0;
        numLines = 1;
               
        for( GraphicBrick brick : graphicMap ) {
            brick.setPos(currentX+X_OFFSET, currentY);
            
            currentX += (brick.getBrick().getDuration() * MEASURE_LENGTH)/480;
            
            while( currentX >= BARS_PER_LINE*MEASURE_LENGTH ) {
                currentX = currentX - (BARS_PER_LINE*MEASURE_LENGTH);
                currentY += LINE_HEIGHT + LINE_SPACING;
                numLines++;
            }
        }
        updateBricks();
    }
    
    public void updateBricks()
    {
        draw();
    }
    
    public void addBlock(Block block)
    {
        roadMap.add(block);
        graphicMap.add(new GraphicBrick(block));
    }
    
    public void addBlocks(ArrayList<Block> blocks)
    {
        roadMap.addAll(blocks);
        graphicMap.addAll(makeBricks(blocks));
    }
    
    public void addBlocks(int ind, ArrayList<Block> blocks)
    {
        roadMap.addAll(ind, blocks);
        graphicMap.addAll(ind, makeBricks(blocks));
    }
    
    public ArrayList<GraphicBrick> makeBricks(ArrayList<Block> blocks)
    {
        ArrayList<GraphicBrick> bricks = new ArrayList();
        
        for( Block block : blocks )
            bricks.add(new GraphicBrick(block));
        
        return bricks;
    }
    
    public ArrayList<Block> makeBlocks(ArrayList<GraphicBrick> bricks)
    {
        ArrayList<Block> blocks = new ArrayList();
        
        for( GraphicBrick brick : bricks )
            blocks.add(brick.getBrick());
        
        return blocks;
    }
    
    public ArrayList<Block> getSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1)
            return getBlocks(selectionStart, selectionEnd+1);
        return new ArrayList();
    }
    
    public ArrayList<Block> removeSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1)
            return removeBlocks(selectionStart, selectionEnd+1);
        return new ArrayList();
    }
    
    public ArrayList<Block> getBlocks(int start, int end)
    {
        return roadMap.getBricks(start, end);
    }
    
    public ArrayList<Block> removeBlocks()
    {
        graphicMap.clear();
        return roadMap.removeBricks();
    }
    
    public ArrayList<Block> removeBlocks(int start, int end)
    {
        ArrayList<Block> blocks = roadMap.removeBricks(start, end);
        graphicMap.subList(start, end).clear();
        return blocks;
    }
    
    public void selectBricks(int index)
    {
        if(selectionStart == -1 && selectionEnd == -1)
            selectionStart = selectionEnd = index;
        else {
            if(index < selectionStart)
                selectionStart = index;
            else if (index > selectionEnd)
                selectionEnd = index;
            else {
                selectBrick(index);
            }
        }
        

        for(int i = selectionStart; i <= selectionEnd; ) {
            graphicMap.get(i).setSelected(true);
            drawBrick(i);
            i++;
        }
        
        drawKeyMap();
    }
    
    public void selectBrick(int index)
    {
        deselectBricks();
        selectionStart = selectionEnd = index;
        graphicMap.get(index).setSelected(true);
        drawBrick(index);
        
        drawKeyMap();
    }
    
    public void selectAll()
    {
        selectionStart = selectionEnd = 0;
        selectBricks(graphicMap.size()-1);
    }
    
    public void deselectBricks()
    {
        for(GraphicBrick brick : graphicMap)
            brick.setSelected(false);
        
        draw();
        
        selectionStart = selectionEnd = -1;
    }
  
    
    public void analyzeSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> bricks = view.analyze(removeSelection());
            addBlocks(selectionStart, bricks);
            selectionEnd = selectionStart;
            selectBricks(selectionStart + bricks.size() - 1);
        } else {
            ArrayList<Block> blocks = view.analyze(removeBlocks());
            addBlocks(blocks);
        }
        placeBricks();
    }
    
    public void transposeSelection(long diff)
    {
        if(selectionStart != -1 && selectionEnd != -1)
            for(Block block : roadMap.getBricks(selectionStart, selectionEnd + 1))
                block.transpose(diff);
    }
    
    public void scaleSelection(long scale)
    {
        if(selectionStart != -1 && selectionEnd != -1)
            for(Block block : roadMap.getBricks(selectionStart, selectionEnd + 1))
                block.adjustDuration(scale);
        placeBricks();
    }
    
    public void deleteSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1)
            deleteRange(selectionStart, selectionEnd+1);
        selectionStart = selectionEnd = -1;
    }
    
    public void deleteRange(int start, int end)
    {
        roadMap.removeBricks(start, end);
        graphicMap.subList(start, end).clear();
        placeBricks();
    }
    
    public void breakSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = roadMap.removeBricks(selectionStart, selectionEnd + 1);
            ArrayList<Block> newBlocks = new ArrayList();
            
            graphicMap.subList(selectionStart, selectionEnd + 1).clear();
            
            for( Block block : blocks )
                newBlocks.addAll(block.getSubBlocks());
            
            roadMap.addAll(selectionStart, newBlocks);
            graphicMap.addAll(selectionStart, makeBricks(newBlocks));
            
            selectionEnd = selectionStart;
            
            selectBricks(selectionStart + newBlocks.size() - 1);
            
            placeBricks();
        }       
    }
    
    public void flattenSelection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = roadMap.removeBricks(selectionStart, selectionEnd+1);
            ArrayList<Block> newBlocks = new ArrayList(RoadMap.getChords(blocks));
            
            graphicMap.subList(selectionStart, selectionEnd + 1).clear();
            
            roadMap.addAll(selectionStart, newBlocks);
            graphicMap.addAll(selectionStart, makeBricks(newBlocks));
            
            selectionEnd = selectionStart;
            
            selectBricks(selectionStart + newBlocks.size() - 1);
            
            placeBricks();
        }
    }
    
    public Brick makeBrickFromSelection(String name, long key)
    {
        if(selectionStart != -1 && selectionEnd != -1 && selectionStart != selectionEnd) {
            ArrayList<Block> blocks = roadMap.removeBricks(selectionStart, selectionEnd+1);
            graphicMap.subList(selectionStart, selectionEnd+1).clear();
            Brick newBrick = new Brick(name, key, "UserDefined", blocks, "Major");

            
            roadMap.add(selectionStart, newBrick);
            graphicMap.add(selectionStart, new GraphicBrick(newBrick));
            
            selectBrick(selectionStart);
            placeBricks();
            return newBrick;
        }
        return null;
    }
       
    public void dropBricks(int index, ArrayList<GraphicBrick> bricks)
    {
        graphicMap.addAll(index, bricks);
        roadMap.addAll(index, makeBlocks(bricks));
        System.out.println(roadMap.size()+ " - " + graphicMap.size());
        selectionStart = selectionEnd = index;
        selectBricks(index + bricks.size() - 1);
    }
    
    /* Drawing and junk */
    
    public void setBuffer(Image buffer)
    {
        this.buffer = buffer;
    }
    
    public void draw()
    {
       System.out.println(roadMap.size() + " " + graphicMap.size());
        
       view.setBackground(buffer);
       drawGrid();
       drawBricks();
       drawKeyMap();
       repaint();
    }
    
    public void drawGrid()
    {
        Graphics g = buffer.getGraphics();
        
        for(int i = 0; i < numLines; i++) {
            g.setColor(GRID_BG_COLOR);
            g.fillRect(X_OFFSET, Y_OFFSET + i*(LINE_HEIGHT + LINE_SPACING),
                    BARS_PER_LINE * MEASURE_LENGTH, LINE_HEIGHT);
            
            for(int j = 0; j <= BARS_PER_LINE; ) {
                g.setColor(GRID_LINE_COLOR);
                g.drawLine(X_OFFSET + j*MEASURE_LENGTH,
                        Y_OFFSET + i*(LINE_HEIGHT + LINE_SPACING) - 5,
                        X_OFFSET + j*MEASURE_LENGTH,
                        Y_OFFSET + (i+1)*LINE_HEIGHT + i*LINE_SPACING + 5);
                j++;
            }
        }
        
        setSize(WIDTH, numLines * (LINE_HEIGHT+LINE_SPACING));
    }
    
    public void drawBrick(int ind)
    {
        graphicMap.get(ind).draw(buffer.getGraphics());
        repaint();
    }
    
    public void drawBricks()
    {        
        Graphics g = buffer.getGraphics();
        
        ArrayList<String> joinList = roadMap.getJoins();
        
        //System.out.println(roadMap.getBricks().size() + " " + joinList.size());
        
        for( int ind = 0; ind < graphicMap.size(); ind++ ) {
            GraphicBrick brick = graphicMap.get(ind);           
            brick.draw(g);
            
            if(ind > 0 && !joinList.get(ind-1).isEmpty()) {
                drawJoin(joinList.get(ind-1), brick.x(), brick.y()+LINE_HEIGHT);
            }
        }
        
        drawKeyMap();
    }
    
    public void drawBricksAt(ArrayList<GraphicBrick> bricks, int x, int y)
    {
        Graphics g = buffer.getGraphics();
        
        int xOffset = x;
        
        for(GraphicBrick brick : bricks) {
            brick.drawAt(g, xOffset, y);
            xOffset+=brick.getLength();
        }
    }
    
    public void drawJoin(String name, int x, int y)
    {
        Graphics g = buffer.getGraphics();
        
        FontMetrics metrics = g.getFontMetrics();
        
        int width = metrics.stringWidth(name);
        int offset = metrics.getAscent();
        
        g.setColor(Color.WHITE);
        
        g.fillRect(x+2,y+2, width + 4, LINE_SPACING - 4);
        
        g.setColor(Color.BLACK);
        
        g.drawRect(x+2,y+2, width + 4, LINE_SPACING - 4);
        g.drawString(name,x+4, y+2+offset);
    }
    
    public void drawKeyMap()
    {
        Graphics g = buffer.getGraphics();
        
        int x = X_OFFSET;
        int y = Y_OFFSET;
        
        int cutoffPoint = X_OFFSET + BARS_PER_LINE*MEASURE_LENGTH;
        
        for( long[] keyPair : roadMap.getKeyMap() ) {
            long key = keyPair[0];
            String keyName = BrickLibrary.keyNumToName(key);
            long dur = keyPair[1];
          
            
            if( cutoffPoint - x < 5 ) {
                x = X_OFFSET;
                y += LINE_HEIGHT + LINE_SPACING;
            }
            
            int xOffset = x;
            int yOffset = y;
            
            g.setColor(LINE_COLOR);
            g.drawLine(xOffset,yOffset,xOffset,yOffset+LINE_HEIGHT/3);
            
            
            int length = ((int)dur*MEASURE_LENGTH)/BEATS_PER_MEASURE;
            
            while( xOffset + length > cutoffPoint ) {
                if (key == Chord.NC)
                    g.setColor(Color.WHITE);
                else
                    g.setColor(KEY_COLORS[(int)key]);
                g.fillRect(xOffset + 1, yOffset,
                     cutoffPoint - xOffset, LINE_HEIGHT/3);
                
                g.setColor(LINE_COLOR);
                g.drawLine(xOffset, yOffset, cutoffPoint, yOffset);
                g.drawLine(xOffset, yOffset + LINE_HEIGHT/3,
                        cutoffPoint, yOffset + LINE_HEIGHT/3);
                
                length -= cutoffPoint - xOffset;
                
                xOffset = X_OFFSET;
                yOffset += LINE_HEIGHT + LINE_SPACING;
            }
            
            if (key == Chord.NC)
                g.setColor(Color.WHITE);
            else
                g.setColor(KEY_COLORS[(int)key]);
            g.fillRect(xOffset, yOffset, length, LINE_HEIGHT/3);
            
            g.setColor(LINE_COLOR);
            g.drawLine(xOffset, yOffset, xOffset+length, yOffset);
            g.drawLine(xOffset, yOffset + LINE_HEIGHT/3,
                    xOffset+length, yOffset + LINE_HEIGHT/3);
            g.drawLine(xOffset + length, yOffset,
                    xOffset + length, yOffset + LINE_HEIGHT/3);

            g.drawString(keyName, x+2, y + 15); // TODO: use font metrics
            
            x = xOffset + length;
            y = yOffset;
        }
            
    }
    
    public int getBrickIndexAt(int x, int y)
    {
        int index = 0;
        for ( GraphicBrick brick : graphicMap) {
            if( brick.contains(x, y) )
                return index;
            index++;
        }
        return -1;
    }
    
    public int getSlotAt(int x, int y)
    {
        int index = 0;
        for ( GraphicBrick brick : graphicMap ) {
            if( brick.contains(x, y) )
                return index;
            
            if( y < brick.y() )
                return index;
            
            index++;
        }
        
        return index;
    }
    
    /**
    * Override the paint method to draw the buffer image on this panel's graphics.
    * This method is called implicitly whenever repaint() is called.
    */
    @Override
    public void paint(Graphics g) 
    {
        g.drawImage(buffer, 0, 0, null);
    }
    
}
