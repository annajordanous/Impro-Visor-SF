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
import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import java.awt.Color;
import polya.Polylist;

/**
 * Creates a GUI that displays a bass pattern used in styles. Created Summer
 * 2007 @authors Brandy McMenamy; Robert Keller removed the GUI component
 */
public class BassPatternDisplay extends PatternDisplay
        implements Constants, Playable, Displayable
{

public static Color playableColor = Color.orange;
public static Color unplayableColor = Color.red;
//Various standard messages displayed in tooltips and error dialogs.
private String safeMsgRule = "This rule is legal.  Click play button to preview it.";
private String safeMsgPattern = "This pattern is legal.  Click play button to preview it.";
private String unsafeMsgPattern = "This pattern contains an illegal rule.";
//Contains a specialized error message for any error that just occurred.
private String errorMsg = safeMsgRule;
//The lowest weight allowed for a pattern.
private int lowestWeight = 1;
//The highest weight allowed for a pattern.
private int highestWeight = 100;
private int INITIAL_WEIGHT = 10;
private float weight = INITIAL_WEIGHT;
//The number added to the title of this object to help the user distinguish it from others.
private int titleNumber = 0;

String bassPatternText = "";

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
    /*
     * Ensures that useful items like rhythm durations for notes are ready for
     * use even if the user has not yet generated a style from midi
     */
    if( !MIDIBeast.invoked )
      {
        MIDIBeast.invoke();
      }

    initWeight();

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
    canPlay();

    if( checkStatus() )
      {
        try
          {
            String r = this.getPattern();
            Polylist rule = Notate.parseListFromString(r);
            if( rule.isEmpty() )
              {
                cannotPlay();
                return false;
              }

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

            s.setChordProg(c);

            if( muteChord )
              {
                s.setChordVolume(0);
              }
            else
              {
                s.setChordVolume(styleEditor.getVolume());
              }
            s.setBassVolume(styleEditor.getVolume());
            s.setTempo(tempo);

            MidiSynth synth = notate.getMidiSynth();

            s.setVolumes(synth);

            new PlayScoreCommand(s, 0, true, synth, notate.getTransposition()).execute();
            styleEditor.setStatus("OK");
          }
        catch( Exception e )
          {
            cannotPlay();
            return false;
          }
      }
    else
      {
        cannotPlay();
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

/**
 * @return the most recent error message that occurred.
     *
 */
public String getRuleError()
  {
    return errorMsg;
  }


/**
 * @return the beats in this pattern.
     *
 */
public double getBeats()
  {
    double slots = MIDIBeast.numBeatsInBassRule(getDisplayText());
    if( slots < 0 )
      {
        return -1;
      }
    return slots / BEAT;
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
    bassPatternText = text;
    updateElements();
  }



/**
 * Creates the SpinnerModel used for the weight field.
     *
 */
private void initWeight()
  {
    weight = INITIAL_WEIGHT;
  }

/**
 * Changes the appearance of this BassPatternDisplay to "deselected"
     *
 */
public void setDeselectedAppearance()
  {
  }

/**
 * Changes the appearance of this BassPatternDisplay to "selected"
    *
 */
public void setSelectedAppearance()
  {
    styleEditor.setSelectedBass(this);
  }

/**
 * Update the length title in the northern pane to reflect changes in text.
     *
 */
public void updateLength()
  {
    double duration = MIDIBeast.numBeatsInBassRule(getDisplayText());
  }

/**
 * Update the user feedback items.
     *
 */
public void updateElements()
  {
    checkStatus();
    updateLength();
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
    String displayText = getDisplayText();
    String rule = getPattern();

    return true;
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

// Variables declaration - do not modify
// End of variables declaration
}
