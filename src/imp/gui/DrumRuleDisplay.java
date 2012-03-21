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

import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import java.awt.Color;
import java.util.ArrayList;
import java.util.StringTokenizer;
import polya.Polylist;

/**
 * Created Summer 2007 @authors Brandy McMenamy, Sayuri Soejima
 */

public class DrumRuleDisplay extends PatternDisplay implements Playable, Displayable
{

public static Color playableColor = Color.yellow;
public static Color unplayableColor = Color.red;

//Useful notate containers.
private DrumPatternDisplay myParentHolder = null;
private String instrumentString = "";
private int instrumentNumber = -1;
private String ruleText = "";

/**
 * Constructs a new DrumRuleDisplay JPanel with default empty rule and
 * instrument "Acoustic Bass Drum".
     *
 */
public DrumRuleDisplay(Notate parent, CommandManager cm, DrumPatternDisplay myParentHolder, StyleEditor styleParent)
  {
    super(parent, cm, styleParent);
    this.myParentHolder = myParentHolder;
    initialize(null, "Acoustic Bass Drum");
  }

/**
 * Constructs a new DrumRuleDisplay JPanel with rule and instrument parameters
     *
 */
public DrumRuleDisplay(String rule, String instrument, Notate parent, CommandManager cm, DrumPatternDisplay myParentHolder, StyleEditor styleParent)
  {
    super(parent, cm, styleParent);
    this.myParentHolder = myParentHolder;

    //System.out.println("new DrumRuleDisplay " + rule + " " + instrument);
    initialize(rule, instrument);
  }

/**
 * Initializes all elements and components for the DrumRuleDisplay GUI
     *
 */
private void initialize(String rule, String instrument)
  {
    /*
     * Ensures that useful items like rhythm durations for notes are ready for
     * use even if the user has not yet generated a style from midi
     */
    if( !MIDIBeast.invoked )
      {
        MIDIBeast.invoke();
      }

    setRuleText(rule);
    setInstrument(instrument);
  }

//Accessors:
/**
 * @return the actual text displpayed in the text field
     *
 */
public String getDisplayText()
  {
    return ruleText.trim();
  }

/**
 * @return the text formatted with drum-rule syntax to be included with the
 * overall drum-pattern
 */

public String getRule()
  {

    String rule = "(drum " + getInstrumentNumber() + " " + getDisplayText() + ")";

    //System.out.println("rule = " + rule);

    return rule;
  }



public void setRuleText(String text)
  {
    //System.out.println("setting rule text to " + text);
    ruleText = text;
  }

/**
 * @return the text formatted as if it were the only rule in a drum-pattern.
 * Used to play the rule.
 */

public String getPlayRule()
  {
    return "(drum-pattern " + getRule() + "(weight 100))";
  }


/**
 * @return the instrument selected in the combo box
 */
public String getInstrument()
  {
    return instrumentString;
    //return (String) drumInstrumentBox.getSelectedItem();
  }

/**
 * @return the number of the instrument*
 */
public int getInstrumentNumber()
  {
    return instrumentNumber;
  }

//Mutators:
/**
 * Sets the displayed text to parameter rule and updates its legality feedback
 */
public void setDisplayText(String rule)
  {
    setRuleText(rule);
    //checkStatus();
  }

/**
 * Sets the selected instrument to parameter instrument if it exists in the
 */
public void setInstrument(String instrument)
  {
    instrumentString = instrument;
    instrumentNumber = MIDIBeast.getDrumInstrumentNumber(instrument);

    //System.out.println("getting instrument number for " + instrument + " = " + instrumentNumber);

    if( instrumentNumber < 0 )
      {
        ErrorLog.log(ErrorLog.WARNING, "Instrument has no corresponding number: " + instrument);
      }

  }

public boolean playMe(double swingVal, int loopCount, double tempo, Score s)
  {
    canPlay();
    try
      {
        if( checkStatus() )
          {
            String r = this.getPlayRule();
            Polylist rule = Notate.parseListFromString(r);
            if( rule.isEmpty() )
              {
                styleEditor.setStatus("Can't play incorrect pattern");
                return false;
              }
            Style tempStyle = Style.makeStyle(rule);

            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);
            tempStyle.setName("drumRule");
            Style.setStyle("drumRule", tempStyle);
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            String chord = styleEditor.getChord();
            boolean muteChord = styleEditor.isChordMuted();
            int duration = tempStyle.getDP().get(0).getDuration();
            ChordPart c = new ChordPart();
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
            s.setVolumes(notate.getMidiSynth());

            new PlayScoreCommand(s, 0, true, notate.getMidiSynth(), notate.getTransposition()).execute();
            styleEditor.setStatus("OK");
          }
        else
          {
            styleEditor.setStatus("Can't play incorrect pattern");
            /*
             * ErrorLog.setDialogTitle("Can't Play");
             * ErrorLog.log(ErrorLog.WARNING, "Unable to play the selected rule
             * because " + errorMsgRule); ErrorLog.setDialogTitle("");
             */
            return false;
          }

      }
    catch( Exception e )
      {
        styleEditor.setStatus("Can't play incorrect pattern");
        /*
         * ErrorLog.setDialogTitle("Can't Play"); ErrorLog.log(ErrorLog.WARNING,
         * "Unable to play the selected rule."); ErrorLog.setDialogTitle("");
         */
        return false;
      }
    return isPlayable();
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
 * @return the actual text displpayed in the text field
     *
 */
@Override
public String toString()
  {
    return ruleText;
  }

/**
 * Checks the rule for correctness (uses the time signature in MIDIBeast, which
 * is appropriately updated from a DrumPatternDisplay notate). Changes icons,
 * tooltips, and errorMsgRule to appropriate error feedback information.
 *
 * @return true if the rule is a correctly formed and therefore playable by
 * Impro-Visor. Returns false otherwise.
 */

public boolean checkStatus()
  {
    String displayText = getDisplayText();
    String rule = getRule();
    playable = true;

    try
      {
        //Check for null rules.
        if( displayText.equals("") )
          {
            return false;
          }

        Polylist l = Notate.parseListFromString(getPlayRule());
        StringTokenizer tokenS = new StringTokenizer(displayText, " ");
        ArrayList<String> tokenizedRule = new ArrayList<String>();

        while( tokenS.hasMoreTokens() )
          {
            tokenizedRule.add(tokenS.nextToken());
          }

        //For every element, check for invalid "hit" or "rest" items, and 
        //invalid rhythm durations.  By checking each element, we are able to give clearer feedback about errors         
        for( int i = 0; i < tokenizedRule.size(); i++ )
          {
            String charString = java.lang.Character.toString(tokenizedRule.get(i).charAt(0));
            if( !(charString.equals("X"))
             && !(charString.equals("R"))
             && !(charString.equals("V")) )
              {
                cannotPlay();
                return false;
              }
          }

        if( Style.makeStyle(l) == null )
          {
            cannotPlay();
            return false;
          }
        else if( MIDIBeast.numBeatsInRule(displayText) == -1 )
          {
            cannotPlay();
            return false;
          }
        else
          {
            return true;
          }
      }
    catch( Exception e )
      {
        cannotPlay();
        return false;
      }
  }


public double getPatternLength()
  {
    return DrumRuleRep.makeDrumRuleRep(getRule()).getDuration(); //Duration.getDuration(getDisplayText());
  }

public double getBeats()
  {
    return getPatternLength() / BEAT;
  }

}
