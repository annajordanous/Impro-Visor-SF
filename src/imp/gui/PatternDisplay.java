/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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

package imp.gui;

import imp.Constants;
import imp.com.*;
import java.awt.Color;

/**
 * Base class for various pattern display classes:
 * BassPatternDisplay, ChordPatternDisplay, DrumPatternDisplay
 *
 * @author keller
 */
abstract class PatternDisplay
        extends javax.swing.JPanel
        implements Constants, Playable
{
  
  public static double NOSWING = 0.5;
  
  protected boolean playable = true;

  //Useful parent containers.
  protected StyleEditor styleParent;

  protected Notate parent;

  protected CommandManager cm;

  public PatternDisplay(Notate parent,
                         CommandManager cm,
                         StyleEditor styleParent)
    {
    this.parent = parent;
    this.cm = cm;
    this.styleParent = styleParent;
    }

   // FIX: Not so useful to not use swing from GUI
     
    public boolean playMe()
    {
      return playMe(styleParent == null ? NOSWING : styleParent.getSwingValue());
    }
    

  abstract public boolean playMe(double swingValue);
  
  abstract public boolean checkStatus();
  
  public java.awt.Color getColor()
  {
    return isPlayable() ? getPlayableColor() : getUnplayableColor();
  }

  abstract public Color getPlayableColor();
  abstract public Color getUnplayableColor();
 
  public boolean isPlayable()
  {
    canPlay();
    playable = checkStatus();
    return playable;
  }
  
  public void cannotPlay()
  {
    playable = false;
    styleParent.setStatus("Can't play incorrect pattern");
  }

  public void canPlay()
  {
    playable = true;
    styleParent.setStatus("OK");
  }

  abstract public int getWeight();
  
  abstract public double getBeats();

}
