/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

import javax.swing.JPanel;
import java.awt.*;
import java.util.ArrayList;
import imp.brickdictionary.*;
import java.util.Random;

/**
 *
 * @author ImproVisor
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
    
    public void placeBricks()
    {
        int currentX = 0;// X_OFFSET;
        int currentY = settings.yOffset;
        int currentBeats = 0;
        numLines = 1;
               
        for( GraphicBrick brick : graphicMap ) {
            
            currentX = settings.getLength(currentBeats);
            currentY = settings.yOffset;
            numLines = 0;
            
            while( currentX >= settings.getLineLength() ) {
                currentX = currentX - (settings.getLineLength());
                currentY += settings.lineHeight + settings.lineSpacing;
                numLines++;
            }
            brick.setPos(currentX+settings.xOffset, currentY);
            currentBeats += brick.getBrick().getDuration();
            
            //currentX += settings.getBlockLength(brick.getBrick());
            
            //while( currentX >= settings.getLineLength() ) {
            //    currentX = currentX - (settings.getLineLength());
            //    currentY += settings.lineHeight + settings.lineSpacing;
            //    numLines++;
            //}
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
            drawBricks();
            repaint();
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
        System.out.println(roadMap.size()+ " - " + graphicMap.size());
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
        
        //System.out.println(roadMap.getBricks().size() + " " + joinList.size());
        //Random r = new Random();
        for( int ind = 0; ind < graphicMap.size(); ind++ ) {
            GraphicBrick brick = graphicMap.get(ind);      
            //g.shear((r.nextDouble()-.5)/100, (r.nextDouble()-.5)/100);
            //g.setStroke(new BasicStroke(r.nextFloat()*4));
            brick.draw(g);
            
            int x = brick.x();
            int y = brick.y();
            
            if(ind > 0 && !joinList.get(ind-1).isEmpty()) {
                drawJoin(joinList.get(ind-1), x, y+settings.lineHeight);
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
    
    public void drawJoin(String name, int x, int y)
    {
        Graphics g = buffer.getGraphics();
        
        FontMetrics metrics = g.getFontMetrics();
        
        int width = metrics.stringWidth(name);
        int offset = metrics.getAscent();
        
        g.setColor(Color.WHITE);
        
        g.fillRect(x+2,y+2, width + 4, settings.lineSpacing - 4);
        
        g.setColor(Color.BLACK);
        
        g.drawRect(x+2,y+2, width + 4, settings.lineSpacing - 4);
        g.drawString(name,x+4, y+2+offset);
    }
    
    public void drawKeyMap()
    {
        /*
        Graphics g = buffer.getGraphics();
        
        int blockHeight = settings.getBlockHeight();
        
        long currentBeats = 0;
        
        for( KeySpan keySpan : roadMap.getKeyMap() ) {
            long key = keySpan.getKey();
            String keyName = BrickLibrary.keyNumToName(key) + keySpan.getMode();
            long dur = keySpan.getDuration();
            
            Point startPos = settings.getPosFromBeats(currentBeats);
            int x = (int)startPos.x;
            int y = (int)startPos.y;
            
            g.drawLine(x, y, x, y+blockHeight);
            
            g.setColor(settings.textColor);
            g.drawString(keyName, x, y);
            
            long num = currentBeats % settings.getCutoffBeat() + dur;
            
            
            currentBeats += dur;
        }*/
        
        
        Graphics g = buffer.getGraphics();
        
        int x = settings.xOffset;
        int y = settings.yOffset;
        
        int cutoffLine = settings.getCutoff();
        int blockHeight = settings.getBlockHeight();
        
        for( KeySpan keySpan : roadMap.getKeyMap() ) {
            long key = keySpan.getKey();
            String keyName = BrickLibrary.keyNumToName(key) + keySpan.getMode();
            long dur = keySpan.getDuration();
          
            
            if( cutoffLine - x < 5 ) {
                x = settings.xOffset;
                y += settings.lineHeight + settings.lineSpacing;
            }
            
            int xOffset = x;
            int yOffset = y;
            
            g.setColor(settings.lineColor);
            g.drawLine(xOffset,yOffset,xOffset,yOffset+blockHeight);
            
            
            int length = settings.getLength(dur);
            
            while( xOffset + length > cutoffLine ) {
                if (key == Chord.NC)
                    g.setColor(Color.WHITE);
                else
                    g.setColor(settings.getKeyColor(key));
                g.fillRect(xOffset + 1, yOffset,
                     cutoffLine - xOffset, blockHeight);
                
                g.setColor(settings.lineColor);
                g.drawLine(xOffset, yOffset, cutoffLine, yOffset);
                g.drawLine(xOffset, yOffset + blockHeight,
                        cutoffLine, yOffset + blockHeight);
                
                length -= cutoffLine - xOffset;
                
                xOffset = settings.xOffset;
                yOffset += settings.lineHeight + settings.lineSpacing;
            }
            
            if (key == Chord.NC)
                g.setColor(settings.brickBGColor);
            else
                g.setColor(settings.getKeyColor(key));
            g.fillRect(xOffset, yOffset, length, blockHeight);
            
            g.setColor(settings.lineColor);
            g.drawLine(xOffset, yOffset, xOffset+length, yOffset);
            g.drawLine(xOffset, yOffset + blockHeight,
                    xOffset+length, yOffset + blockHeight);
            g.drawLine(xOffset + length, yOffset,
                    xOffset + length, yOffset + blockHeight);

            g.drawString(keyName, x+2, y + 15); // TODO: use font metrics
            
            x = xOffset + length;
            y = yOffset;
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
