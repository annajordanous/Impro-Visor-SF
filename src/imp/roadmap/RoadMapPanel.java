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

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import imp.brickdictionary.*;
import java.util.Random;


/**
 *
 * @author August Toman-Yih
 */
public class RoadMapPanel extends JPanel{
   
    public int numLines = 1;
    
    public int selectionStart = -1;
    public int selectionEnd = -1;
    
    public int insertLineIndex = -1;
    
    private Image buffer;
    
    private RoadMap roadMap = new RoadMap();
    private ArrayList<GraphicBrick> graphicMap = new ArrayList();
    
    RoadMapSettings settings;
    
    RoadMapFrame view;
    
    /** Creates new form RoadMapPanel */
    public RoadMapPanel(RoadMapFrame view)
    {
        this.view = view;
        settings = view.getSettings();
    }
    
    public RoadMap getRoadMap()
    {
        return roadMap;
    }
    
    public void setRoadMap(RoadMap roadMap)
    {
        this.roadMap = roadMap;
        graphicMap = makeBricks(roadMap.getBricks());
        roadMap.process();
    }
    
    public int getNumBlocks()
    {
        return roadMap.size();
    }
    
    public void placeBricks() //TODO implement line breaks for section breaks
    {
        long currentBeats = 0;
               
        for( GraphicBrick brick : graphicMap ) {
            brick.setPos(settings.getPosFromBeats(currentBeats));
            currentBeats += brick.getBrick().getDuration();
        }
        numLines = settings.getLines(currentBeats)+1;
        updateBricks();
    }
    
    public void updateBricks()
    {
        draw();
    }
    
    public void addBlock(Block block)
    {
        roadMap.add(block);
        graphicMap.add(new GraphicBrick(block, settings));
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
            bricks.add(new GraphicBrick(block, settings));
        
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
    
    public ArrayList<Block> removeSelectionNoUpdate()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            ArrayList<Block> blocks = new ArrayList(roadMap.getBricks().subList(selectionStart, selectionEnd+1));
            roadMap.getBricks().subList(selectionStart, selectionEnd+1).clear();
            graphicMap.subList(selectionStart, selectionEnd+1).clear();
            return blocks;
        }
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
        
        // This is just for testing purposes. It should be eliminated later.
        System.out.println("selected " + roadMap.toString());

    }
    
    public void deselectBricks()
    {
        for(GraphicBrick brick : graphicMap)
            brick.setSelected(false);
        
        draw();
        
        selectionStart = selectionEnd = -1;
    }
  
    public boolean hasSelection()
    {
        return selectionStart != -1 && selectionEnd != -1;
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
        if(selectionStart != -1 && selectionEnd != -1) {
            for(Block block : roadMap.getBricks(selectionStart, selectionEnd + 1))
                block.transpose(diff);
            roadMap.process();
            draw();
        }
        
    }
    
    public void scaleSelection(long scale)
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            for(Block block : roadMap.getBricks(selectionStart, selectionEnd + 1))
                block.adjustDuration(scale);
            roadMap.process();
        }
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
            graphicMap.add(selectionStart, new GraphicBrick(newBrick, settings));
            
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
        selectionStart = selectionEnd = index;
        selectBricks(index + bricks.size() - 1);
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
            
            if( y < brick.y() || x < brick.x() && y > brick.y() && y < brick.y() + settings.lineHeight )
                return index;
            
            index++;
        }
        
        return index;
    }
    
    public void toggleSection()
    {
        if(selectionStart != -1 && selectionEnd != -1) {
            roadMap.getBrick(selectionEnd).setSectionEnd(!roadMap.getBrick(selectionEnd).isSectionEnd());
            drawBrick(selectionEnd);
            drawKeyMap();
        }
    }
    
    public void setInsertLine(int x, int y)
    {
        insertLineIndex = getSlotAt(x,y);
    }
    
    public void setInsertLine(int index)
    {
        insertLineIndex = index;
    }
    
    /* Drawing and junk */
    
    public void setBuffer(Image buffer)
    {
        this.buffer = buffer;
    }
    
    public void draw()
    { 
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
            g.setColor(settings.gridBGColor);
            g.fillRect(settings.xOffset,
                    settings.yOffset + i*(settings.lineHeight + settings.lineSpacing),
                    settings.getLineLength(), settings.lineHeight);
            
            for(int j = 0; j <= settings.barsPerLine; ) {
                g.setColor(settings.gridLineColor);
                g.drawLine(settings.xOffset + j*settings.measureLength,
                        settings.yOffset + i*(settings.lineHeight + settings.lineSpacing) - 5,
                        settings.xOffset + j*settings.measureLength,
                        settings.yOffset + (i+1)*settings.lineHeight + i*settings.lineSpacing + 5);
                j++;
            }
        }
        
        setSize(WIDTH, numLines * (settings.lineHeight+settings.lineSpacing));
    }
    
    public void drawBrick(int ind)
    {
        graphicMap.get(ind).draw(buffer.getGraphics());
        repaint();
    }
    
    public void drawBricks()
    {        
        Graphics2D g = (Graphics2D)buffer.getGraphics();
        
        ArrayList<String> joinList = roadMap.getJoins();
        
        //Random r = new Random(System.nanoTime());
        for( int ind = 0; ind < graphicMap.size(); ind++ ) {
            GraphicBrick brick = graphicMap.get(ind);      
            //g.shear((r.nextDouble()-.5)/100, (r.nextDouble()-.5)/100);
            //g.setStroke(new BasicStroke(r.nextFloat()*4));
            
            int x = brick.x();
            int y = brick.y();
            
            brick.draw(g);
            
            if(ind < joinList.size() && !joinList.get(ind).isEmpty()) { //JOINS
                String joinName = joinList.get(ind);
                int length = settings.getBlockLength(brick.getBrick());
                
                FontMetrics metrics = g.getFontMetrics();
                
                int width = metrics.stringWidth(joinName) + 4;
                int offset = metrics.getAscent();
                
                int joinX = x + length - width;
                int joinY = y + joinX/settings.getLineLength() * settings.getLineOffset() +
                        settings.lineHeight;
                joinX = joinX%settings.getLineLength();
        
                g.setColor(settings.brickBGColor);
                g.setStroke(settings.basicLine);
                
                g.fillRect(joinX+2,joinY+2, width, settings.lineSpacing - 4);
        
                g.setColor(settings.lineColor);
                g.drawRect(joinX+2,joinY+2, width, settings.lineSpacing - 4);
                
                g.setColor(settings.textColor);
                g.drawString(joinName,joinX+4, joinY+2+offset);
            }
                
            
            if( ind == insertLineIndex ) {
                g.setColor(Color.RED);
                g.setStroke(new BasicStroke(2));
                g.drawLine(x, y-5, x, y+settings.lineHeight+5);
                g.setStroke(new BasicStroke(1));
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

    
    public void drawKeyMap()
    {
        
        Graphics g = buffer.getGraphics();
        
        int blockHeight = settings.getBlockHeight();
        
        FontMetrics metrics = g.getFontMetrics();
        int fontOffset = (blockHeight + metrics.getAscent())/2;
        
        long currentBeats = 0;
        
        for( KeySpan keySpan : roadMap.getKeyMap() ) {
            long key = keySpan.getKey();
            String keyName = BrickLibrary.keyNumToName(key) + keySpan.getMode();
            long dur = keySpan.getDuration();
            
            Color keyColor = settings.brickBGColor;
            
            if(key != -1) {
                if(keySpan.getMode().equals("Minor"))
                    keyColor = settings.getKeyColor(key+3);
                else if (keySpan.getMode().equals("Dominant"))
                    keyColor = settings.getKeyColor(key+5);
                else
                    keyColor = settings.getKeyColor(key);
            }
            
            Point startPos = settings.getPosFromBeats(currentBeats);
            Point endPos = settings.getPosFromBeats(currentBeats + dur);
            int x = (int)startPos.x;
            int y = (int)startPos.y;
            int endX = (int)endPos.x;
            int endY = (int)endPos.y;
            
            long num = currentBeats % settings.getBeatsPerLine() + dur;
            int lines = (int) (num / settings.getBeatsPerLine());
            
            if(lines > 0) {
                g.setColor(keyColor);
                g.fillRect(x, y, settings.getCutoff() - x, settings.getBlockHeight());
                g.fillRect(settings.xOffset, endY,
                        endX-settings.xOffset, settings.getBlockHeight());
                
                g.setColor(settings.lineColor);
                g.drawLine(x,y,settings.getCutoff(),y);
                g.drawLine(x,y+settings.getBlockHeight(),settings.getCutoff(),y+settings.getBlockHeight());
                
                g.drawLine(settings.xOffset, endY, endX, endY);
                g.drawLine(settings.xOffset, endY+settings.getBlockHeight(),
                                       endX, endY+settings.getBlockHeight());
                for(int line = 1; line < lines; line++) {
                    g.setColor(keyColor);
                    g.fillRect(settings.xOffset, y+line*settings.getLineOffset(),
                            settings.getLineLength(), settings.getBlockHeight());
                    
                    g.setColor(settings.lineColor);
                    g.drawLine(settings.xOffset,
                            y+line*settings.getLineOffset(),
                            settings.getCutoff(),
                            y+line*settings.getLineOffset());
                    g.drawLine(settings.xOffset,
                            y+line*settings.getLineOffset() + settings.getBlockHeight(),
                            settings.getCutoff(),
                            y+line*settings.getLineOffset() + settings.getBlockHeight());
                }
            } else {
                g.setColor(keyColor);
                g.fillRect(x,y, endX - x, settings.getBlockHeight());
                
                g.setColor(settings.textColor);
                g.drawLine(x,y,endPos.x,y);
                g.drawLine(x,y+settings.getBlockHeight(),endPos.x,y+settings.getBlockHeight());
            }
            
            g.drawLine(endX, endY, endX, endY+settings.getBlockHeight());
            g.drawLine(x, y, x, y+blockHeight);
            
            g.setColor(settings.textColor);
            g.drawString(keyName, x+2, y+fontOffset);
            
            currentBeats += dur;
        }
            
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
