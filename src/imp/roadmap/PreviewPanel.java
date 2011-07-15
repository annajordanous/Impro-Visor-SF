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
        else if (brick instanceof ChordBlock)
            protoBrick = new ChordBlock((ChordBlock)brick);
        
        brick.adjustDuration(currentDuration);
        brick.transpose(currentKey);
        currentBrick = new GraphicBrick(brick, view.getSettings());
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
            currentBrick.getBrick().transpose(newKey - currentKey);
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