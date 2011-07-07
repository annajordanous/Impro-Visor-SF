package imp.roadmap;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.awt.Color;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.FontMetrics;
import java.awt.Image;
import javax.swing.Scrollable;
import java.util.ArrayList;
import java.util.Iterator;
import imp.brickdictionary.*;
import imp.cykparser.PostProcessing;

/**
 *
 * @author August Toman-Yih
 */
public class RoadMapPanel extends JPanel{
    
    public static int X_OFFSET = 50;
    public static int Y_OFFSET = 50;
    public static int BARS_PER_LINE = 8;
    public static int DIVIDER_THICKNESS = 1;
    public static int LINE_HEIGHT = 60;
    public static int MEASURE_LENGTH = 80;
    public static int LINE_SPACING = 20;
    
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
    
    private Image buffer;
    
    private ArrayList<GraphicBrick> roadMap = new ArrayList();
    private ArrayList<String> joinList = new ArrayList();
    private ArrayList<long[]> keyMap = new ArrayList();
    
    RoadMapFrame view;

    /** Creates new form RoadMapPanel */
    public RoadMapPanel(RoadMapFrame view)
    {
        this.view = view;
    }
    
    public void setBuffer(Image buffer)
    {
        this.buffer = buffer;
    }
    
    public void draw()
    {
       view.setBackground(buffer);
       drawGrid();
       drawBricks();
       repaint();
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
    
    public void add(GraphicBrick brick)
    {
        roadMap.add(brick);
        //durations.add(brick.duration);
    }
    
    public void addAll(int index, ArrayList<GraphicBrick> bricks)
    {
        roadMap.addAll(index, bricks);
    }
    
    public void addAll( ArrayList<GraphicBrick> bricks)
    {
        roadMap.addAll(bricks);
    }
    
    public void insert(int index, GraphicBrick brick)
    {
        roadMap.add(index, brick);
        placeBricks();
    }
  
    public void placeBricks()
    {
        int currentX = 0;// X_OFFSET;
        int currentY = Y_OFFSET;
        //int currentBars = 0;
        numLines = 1;
               
        for( Iterator<GraphicBrick> it = roadMap.iterator(); it.hasNext(); ) {
            GraphicBrick brick = it.next();
            brick.setPos(currentX+X_OFFSET, currentY);
            
            currentX += (brick.duration() * MEASURE_LENGTH)/480;
            
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
        ArrayList<Block> blocks = getBlockList();
        
        joinList = PostProcessing.findJoins(blocks);
        keyMap = PostProcessing.findKeys(blocks);
        
        draw();
    }
    
    public void drawBricks()
    {        
        
        Graphics g = buffer.getGraphics();
        
        for( int ind = 0; ind < roadMap.size(); ind++ ) {
            GraphicBrick brick = roadMap.get(ind);           
            brick.draw(g);
            
            if(ind > 0 && !joinList.get(ind-1).isEmpty()) {
                drawJoin(joinList.get(ind-1), brick.x(), brick.y()+LINE_HEIGHT);
            }
        }
        
        drawKeyMap();
    }
    
    public void drawBrick(int ind)
    {
        roadMap.get(ind).draw(buffer.getGraphics());
        repaint();
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
        
        for( long[] keyPair : keyMap ) {
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
            
            
            int length = ((int)dur*MEASURE_LENGTH)/480;
            
            while( xOffset + length > cutoffPoint ) {
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
    
    public GraphicBrick getBrick(int index)
    {
        return roadMap.get(index);
    }
    
    public ArrayList<Block> getBlockList()
    {
        ArrayList<Block> blocks = new ArrayList<Block>();
        
        for(Iterator<GraphicBrick> it = roadMap.iterator(); it.hasNext();) {
            blocks.add(it.next().getBlock());
        }
        
        return blocks;
    }
    
    public GraphicBrick getBrickAt(int x, int y)
    {
        for ( Iterator<GraphicBrick> it = roadMap.iterator(); it.hasNext(); ) {
            GraphicBrick brick = it.next();
            
            if( brick.contains(x, y) )
                return brick;
        }
        return null;
    }
    
    public int getBrickIndexAt(int x, int y)
    {
        int index = 0;
        for ( Iterator<GraphicBrick> it = roadMap.iterator(); it.hasNext(); ) {
            GraphicBrick brick = it.next();
            
            if( brick.contains(x, y) )
                return index;
            
            index++;
            
        }
        return -1;
    }
    
    public int getSlotAt(int x, int y)
    {
        int index = 0;
        for ( Iterator<GraphicBrick> it = roadMap.iterator(); it.hasNext(); )
        {
            GraphicBrick brick = it.next();
            
            if( brick.contains(x, y) )
            {
                return index;
            }
            
            if( y < brick.y() )
                return index;
            
            index++;
        }
        
        return index;
    }
    
    public GraphicBrick removeBrickAt(int x, int y)
    {
        GraphicBrick brick = getBrickAt(x,y);
        removeBrick(brick);
        return brick;
    }
    
    public void removeBrick(GraphicBrick brick)
    {
        roadMap.remove(brick);
        //placeBricks();
    }
    
    public void removeBrick(int index)
    {
        roadMap.remove(index);
        //placeBricks();
    }
    
    public ArrayList<GraphicBrick> removeBricks(int start, int end)
    {
        ArrayList bricks = new ArrayList(roadMap.subList(start, end+1));
        roadMap.subList(start, end+1).clear();
        return bricks;
    }
    
    public ArrayList<GraphicBrick> removeBricks()
    {
        ArrayList bricks = new ArrayList(roadMap);
        roadMap.clear();
        return bricks;
    }
    
    public ArrayList<GraphicBrick> getBricks(int start, int end)
    {
        return new ArrayList(roadMap.subList(start, end+1));
    }
    
    public ArrayList<GraphicBrick> getBricks()
    {
        return roadMap;
    }
    
    public void addBlocks(ArrayList<Block> blocks) //not accurate name
    {
        roadMap.clear();
        for(Iterator<Block> it = blocks.iterator(); it.hasNext(); ) {
            GraphicBrick gBrick = new GraphicBrick(it.next());
            roadMap.add(gBrick);
        }
    }
}
