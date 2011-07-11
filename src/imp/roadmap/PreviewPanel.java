/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package imp.roadmap;

import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Image;
import imp.brickdictionary.*;
import java.util.ArrayList;

/**
 *
 * @author August Toman-Yih
 */
public class PreviewPanel extends JPanel
{
    
    private Image buffer;
    
    public GraphicBrick currentBrick;
    private Block protoBrick;
    
    
    private long currentKey = 0;
    private long currentDuration = 480;
    
    RoadMapFrame view;
    
    /** Required if we are going to make a bean from this. */
    
    public PreviewPanel()
     {
      
     }

    /** Creates new form PreviewPanel */
    public PreviewPanel(RoadMapFrame view)
    {
        this.view = view;        
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
    
    public void draw()
    {
        System.out.println(view);
        view.setBackground(buffer);
        if( currentBrick != null )
        {
            System.out.println("Drawing Brick ");
            currentBrick.drawAt(buffer.getGraphics(),0,0);
        }
        repaint();
    }
    
    public void setBuffer(Image buffer)
    {
        System.out.println(this + " buffer set.");
        this.buffer = buffer;
    }
    
    public void setBrick(Block brick)
    {
        if (brick instanceof Brick)
            protoBrick = new Brick((Brick)brick);
        else if (brick instanceof Chord)
            protoBrick = new Chord((Chord)brick);
        
        brick.adjustDuration(currentDuration);
        currentBrick = new GraphicBrick(brick);
    }

    public GraphicBrick getBrick()
    {
        return currentBrick;
    }
    
    public void setKey(long key)
    {
        if(currentBrick != null)
            currentBrick.getBrick().transpose(currentKey - key);
        currentKey = key;
        draw();
    }
    
    public void setKey(String key)
    {
        long newKey = BrickLibrary.keyNameToNum(key);
        if(currentBrick != null)
            currentBrick.getBrick().transpose(currentKey - newKey);
        currentKey = newKey;
        draw();
    }
    
    public void setDuration(long duration)
    {
        currentDuration = duration;
        if(protoBrick != null)
            setBrick(protoBrick);
    }

}