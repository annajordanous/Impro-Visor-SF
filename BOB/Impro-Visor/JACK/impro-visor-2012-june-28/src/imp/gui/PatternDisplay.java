/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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
import imp.com.CommandManager;
import imp.data.Score;
import imp.util.ErrorNonModal;
import java.awt.Color;

/**
 * Base class for various pattern display classes:
 * BassPatternDisplay, ChordPatternDisplay, DrumPatternDisplay
 * Note that the display part of this is no longer used.
 *
 * @author keller
 */
abstract class PatternDisplay
        extends javax.swing.JPanel
        implements Constants, Playable
{
  public static double NOSWING = 0.5;
  
  protected boolean playable = true;

  //Useful notate containers.
  protected StyleEditor styleEditor;

  protected Notate notate;

  protected CommandManager cm;
  
  float weight = 0;

  public PatternDisplay(Notate notate,
                        CommandManager cm,
                        StyleEditor styleEditor)
    {
    this.notate = notate;
    this.cm = cm;
    this.styleEditor = styleEditor;
    }

   // FIX: Not so useful to not use swing from GUI
     
    public boolean playMe()
    {
      return playMe(styleEditor == null ? NOSWING : styleEditor.getAccompanimentSwingValue(),
                    styleEditor == null ? 0 : styleEditor.getLoopValue());
    }

  
  //abstract public boolean playMe(double swingValue);
  
public boolean playMe(double swingVal)
    {
    return playMe(swingVal, getLoopValue());
    }
  
public boolean playMe(double swingVal, int loopCount)
    {
        return playMe(swingVal, loopCount, styleEditor.getTempo());
    }
      
public boolean playMe(double swingVal, int loopCount, double tempo)
{
    return playMe(swingVal, loopCount, tempo, new Score(4));
}

 abstract public boolean playMe(double swingVal, int loopCount, double tempo, Score s);
    
  public void stopPlaying()
    {
      styleEditor.stopPlaying();
    }
  
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
  
  public int getLoopValue()
    {
      return styleEditor.getLoopValue();
    }
  
  public void cannotPlay()
    {
      cannotPlay("no reason given");
    }
  
  public void cannotPlay(String reason)
  {
    playable = false;
  //ErrorNonModal.log("Cannot play because " + reason);
  }

  public void canPlay()
  {
    playable = true;
    //styleEditor.setStatus("OK");
  }

  public float getWeight()
    {
      return weight;
    }
  
  public void setWeight(float weight)
    {
      this.weight = weight;
    }
  
  abstract public double getBeats();

}
