/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import java.awt.Color;
import polya.Polylist;

/**
 * Created Summer 2007 @authors Brandy McMenamy; 
 * Robert Keller removed the unused GUI component
 */
public class BassPatternDisplay extends PatternDisplay
        implements Constants, Playable, Displayable
{

public static Color playableColor = Color.orange;
public static Color unplayableColor = Color.red;

//The number added to the title of this object to help the user distinguish it from others.
private int titleNumber = 0;

String bassPatternText = "";
BassPattern bassPattern;

/**
 * Constructs a new BassPatternDisplay JPanel with default weight 3 and an empty
 * pattern.
     *
 */
public BassPatternDisplay(Notate notate, CommandManager cm, StyleEditor styleEditor)
  {
    super(notate, cm, styleEditor);
    initialize("", 10);
  }

/**
 * Constructs a new BassPatternDisplay JPanel with weight and rule parameters.
     *
 */
public BassPatternDisplay(String rule, float weight, Notate notate, CommandManager cm, StyleEditor styleEditor)
  {
    super(notate, cm, styleEditor);
    initialize(rule, weight);
  }

/**
 * Initializes all elements and components for the BassPatternDisplay GUI and
 * collapses the pane.
     *
 */
private void initialize(String rule, float weight)
  {
    setWeight(weight);
    setDisplayText(rule);
  }

@Override
public boolean playMe(double swingVal)
  {
    return playMe(swingVal, 0);
  }

/**
 * If the pattern is legal, creates a style with one chordPart consisting of a
 * single chord and adds the entire pattern to that style. Uses the volume,
 * tempo, and chord info from the toolbar.
 */

public boolean playMe(double swingVal, int loopCount, double tempo, Score s)
  {
    if( checkStatus() )
      {
        try
          {
            String r = this.getPattern();
            Polylist rule = Notate.parseListFromString(r);

            Style tempStyle = Style.makeStyle(rule);
            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);
            tempStyle.setName("bassPattern");
            Style.setStyle("bassPattern", tempStyle);
            
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            String chord = styleEditor.getChord();
            ChordPart c = new ChordPart();
            boolean muteChord = styleEditor.isChordMuted();
            int duration = tempStyle.getBP().get(0).getDuration();
            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            s.setChordPart(c);

            if( muteChord )
              {
                notate.setChordVolume(0);
              }
            else
              {
                notate.setChordVolume(styleEditor.getVolume());
              }
            notate.setBassVolume(styleEditor.getVolume());
            s.setTempo(tempo);

            MidiSynth synth = notate.getMidiSynth();

            //notate.setVolumes(synth);
            new PlayScoreCommand(s, 0, true, synth, ImproVisor.getCurrentWindow(), 0, notate.getTransposition()).execute();
          }
        catch( Exception e )
          {
            cannotPlay("Exception " + e);
            return false;
          }
      }
    else
      {
        cannotPlay(bassPattern.getErrorMessage());
        return false;
      }
    return true;
  }

//Accessors:
/**
 * @return the actual text displpayed in the text field
 *
 */
public String getDisplayText()
  {
    return bassPatternText.trim();
  }

/**
 * @return the text and weight formatted with bass-pattern syntax used by the
 * style classes 
     *
 */
public String getPattern()
  {
    return "(bass-pattern (rules " + getDisplayText() + ")(weight " + getWeight() + "))";
  }



public BassPattern getBassPattern()
    {
    return bassPattern;
    }


public int getPatternLength()
  {
    return getBassPattern().getDuration();
  }

public double getBeats()
  {
    return ((double)getPatternLength()) / BEAT;
  }


/**
 * @return the selected value of the checkbox marked "include" in the upper
 * right corner
     *
 */
public boolean getIncludedStatus()
  {
    return true;  // FIX
  }

//Mutators:
/**
 * Sets the number in the title to num.
     *
 */
public void setTitleNumber(int num)
  {
    titleNumber = num;
  }

/**
 * Sets the text in the text field to the parameter text and updates the user
 * feedback information.
     *
 */
public void setDisplayText(String text)
  {
    bassPatternText = text.trim();
    if( bassPatternText.equals("") )
      {
        return;
      }
    Polylist list = Polylist.PolylistFromString('(' + bassPatternText + ')');
    bassPattern = BassPattern.makeBassPattern(Polylist.list(((Polylist)list.first()).cons("rules")));
    if( !bassPattern.getStatus() )
      {
        cannotPlay(bassPattern.getErrorMessage());
      }
  }


public Color getPlayableColor()
  {
    return playableColor;
  }

public Color getUnplayableColor()
  {
    return unplayableColor;
  }


/**
 * Checks the pattern for correctness for the given time signature. 
 *
 * @return true if the pattern is a correctly formed and therefore playable by
 * Impro-Visor. Returns false otherwise.
 *
 */

public boolean checkStatus()
  {
    return bassPattern.getStatus();
  }


/**
 * @return the actual text displpayed in the text field
 *
 */

@Override
public String toString()
  {
    return bassPatternText.trim();
  }

}
