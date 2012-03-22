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
import imp.data.ChordPart;
import imp.data.MIDIBeast;
import imp.data.Score;
import imp.data.Style;
import imp.util.ErrorLog;
import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import polya.Polylist;

/**
 * Created Summer 2007 @authors Brandy McMenamy, Sayuri Soejima.
 * Robert Keller removed unused GUI component
 */
public class DrumPatternDisplay
        extends PatternDisplay
        implements Playable, Constants
{
//The number added to the title of this object to help the user distinguish it from others.
private int titleNumber = 0;

//The currently selected DrumRuleDisplay
private DrumRuleDisplay curSelectedRule = null;

//True if the pattern information is displayed, false otherwise
boolean isExpanded = false;
private ArrayList<DrumRuleDisplay> rules = new ArrayList<DrumRuleDisplay>();

// To satisfy interface only
@Override
public java.awt.Color getColor()
  {
    return null;
  }

/**
 * Constructs a new DrumPatternDisplay JPanel with default weight 3 and an empty
 * pattern.
     *
 */
public DrumPatternDisplay(Notate parent, CommandManager cm, StyleEditor styleParent)
  {
    super(parent, cm, styleParent);
    initialize(3);
  }

/**
 * Constructs a new BassPatternDisplay JPanel with weight and rule parameters.
     *
 */
public DrumPatternDisplay(float weight, Notate parent, CommandManager cm, StyleEditor styleParent)
  {
    super(parent, cm, styleParent);
    initialize(weight);
  }

/**
 * Initializes all elements and components for the DrumPatternDisplay GUI and
 * collapses the pane.
     *
 */
private void initialize(float weight)
  {
    /*
     * Ensures that useful items like rhythm durations for notes are ready for
     * use even if the user has not yet generated a style from midi
     */
    if( !MIDIBeast.invoked )
      {
        MIDIBeast.invoke();
      }

    setWeight(weight);

    checkStatus();
  }


/**
 * @return titleNumber
   *
 */
public int getTitleNumber()
  {
    return titleNumber;
  }

/**
 * @param requireChecked means that the instrument must be checked in the
 * StyleEditor in order for this pattern to be non-empty. For example,
 * checked is not required for count-in, so this method is called with argument
 * false in that case.
 * @return all legal drum rules and weight formatted with the drum-pattern
 * syntax used by the style classes
 */

public String getPattern(boolean requireChecked)
  {
    StringBuilder buffer = new StringBuilder();
    
    buffer.append("(drum-pattern ");

    for( Iterator<DrumRuleDisplay> e = rules.iterator(); e.hasNext(); )
      {
        try
          {
            DrumRuleDisplay d = e.next();
            // See if instrument is to be included per checkbox in editor
            // FIX: This is round-about, and should be changed to iterate directly over
            // table column, rather than going through drumRuleHolder.

            int instrumentNumber = d.getInstrumentNumber();

            if( !requireChecked || styleEditor.isDrumInstrumentNumberIncluded(instrumentNumber) )
              {
                String rep = d.getRule();
                if( d.checkStatus() )
                  {
                    buffer.append("\n\t\t"); // pretty-printing
                    buffer.append(rep);
                  }
                else
                  {
                    ErrorLog.log(ErrorLog.WARNING, "error in drum rule: " + rep);
                  }
              }
          }
        catch( ClassCastException ex )
          {
          }
      }
    buffer.append("\n\t\t(weight ");
    buffer.append(getWeight());
    buffer.append(")\n\t)");
    
    return buffer.toString();
  }

/**
 * This is not used currently, but needed to fulfill the interface requirement.
 *
 * @param string
 */
public void setDisplayText(String string)
  {
  }


/**
 * @return the number of components in the drumRuleHolder pane (this is where
 * DrumRuleDisplay objects are added)
 */

public int getNumComponents()
  {
    return rules.size();
  }

/**
 * @return the ith components in the drumRuleHolder pane if there is an ith
 * component. Return null otherwise.
 */
public Component getComponentAt(int i)
  {
    return null;
  }

/**
 * @return the length of the pattern, which is the length of the first
 * DrumRuleDisplay rule. Returns -1 if the rule is malformed or there are no
 * rules in the pane. Checks for equal lengths between rules are handled
 * elsewhere for speed reasons.
 *
 * NOTE: Length here is in slots, not beats!!
 */
public double getPatternLength()
  {
    double maxLength = 0;

    for( DrumRuleDisplay d : rules )
      {
        if( d.checkStatus() )
          {
            double ruleLength = d.getPatternLength();
            if( ruleLength > maxLength )
              {
                maxLength = ruleLength;
              }
          }
      }
    return maxLength;
  }


/**
 * @return the number of beats in the pattern.
 */
public double getBeats()
  {
    double patternLength = getPatternLength();

    return patternLength >= 0 ? patternLength / BEAT : -1;
  }

/**
 * @return curSelectedRule
 *
 */

public DrumRuleDisplay getSelectedRule()
  {
    return curSelectedRule;
  }


/**
 * Sets the number in the title to num.
 */

public void setTitleNumber(int num)
  {
    titleNumber = num;
  }


/**
 * Adds rule to the correct pane and updates pertinent information. Need to make
 * sure there is only one rule for a given instrument.
 *
 */

public void addRule(DrumRuleDisplay rule)
  {
    if( rule != null )
      {
        int instrumentNumber = rule.getInstrumentNumber();
        for( DrumRuleDisplay d : rules )
          {
            if( d.getInstrumentNumber() == instrumentNumber )
              {
                rules.remove(d);
                break;
              }
          }
        rules.add(rule);
        checkStatus();
      }
  }


/**
 * Removes rule 
 *
 */

public void removeRule(DrumRuleDisplay rule)
  {
    if( rule != null )
      {
      rules.remove(rule);
      }
  }


/**
 * Return the number of rules in this pattern
 */

public int getRuleCount()
  {
    return rules.size();
  }


/**
 * Checks the pattern for correctness. Changes icons, tooltips, and errorMsg to
 * appropriate error feedback information.
 *
 * @return true if the pattern is a correctly formed (all rules are legal and
 * have the same number of beats) and therefore playable by Impro-Visor. Returns
 * false otherwise.
 *
 */

public boolean checkStatus()
  {
    return true;
  }


/**
 * Makes a copy of the currently selected rule before removing it.
 *
 */

public void cutSelectedRule()
  {
    if( curSelectedRule != null )
      {
        styleEditor.copyDrumRule(curSelectedRule);

        curSelectedRule = null;
      }
  }


/**
 * Cut a rule by object identity, through the API, not GUI.
 *
 */

public void cutRule(DrumRuleDisplay ruleToCut)
  {

    curSelectedRule = null;

  }

/**
 * Creates a new DrumRuleDisplay object with all attributes of pasteMe and then
 * adds it to this DrumPatternDisplay object via the addRule method.
     *
 */
public void pasteRule(DrumRuleDisplay pasteMe)
  {
    DrumRuleDisplay newRule = new DrumRuleDisplay(pasteMe.getDisplayText(), 
                                                  pasteMe.getInstrument(), 
                                                  notate, 
                                                  cm, 
                                                  this, 
                                                  styleEditor);
    addRule(newRule);
  }



/**
 * @return true if all rules in the pattern have the same number of beats.
 * Returns false otherwise. Changes icons, tooltips, and errorMsg to appropriate
 * error feedback information.
     *
 */

public String getDisplayText()
  {
    StringBuilder buffer = new StringBuilder();

    for( DrumRuleDisplay d : rules )
      {
        try
          {
            buffer.append("(");
            buffer.append(d.getDisplayText());
            buffer.append(") ");
          }
        catch( ClassCastException e )
          {
          }
      }
    /*
     * This is changed for benifit of updateLength() The decision to change
     * tooltip and icon is handled by the next step in checkPatternLength
     */
    return buffer.toString();
  }

@Override
public String toString()
  {
    return "DrumPatternDisplay with " + rules.size() + " rules";
  }


/**
 * If the pattern is legal, creates a style with one chordPart consisting of a
 * single chord and adds the entire pattern to that style. Uses the volume,
 * tempo, and chord info from the toolbar.
 */

public ChordPart makeCountIn(double swingVal, int loopCount, double tempo)
  {
    canPlay();

    if( checkStatus() )
      {
        try
          {
            String p = this.getPattern(false); 
            // false means checked instrument not required.
            Polylist rule = Notate.parseListFromString(p);
            if( rule.isEmpty() )
              {
                cannotPlay();
                return null;
              }
            Style tempStyle = Style.makeStyle(rule);
            tempStyle.setName("count-in");
            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);

            Style.setStyle("count-in", tempStyle);

            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            String chord = "NC"; // styleEditor.getChord();

            ChordPart chordPart = new ChordPart();

            boolean muteChord = styleEditor.isChordMuted();

            int duration = tempStyle.getDrumPatternDuration();
            chordPart.addChord(chord, duration);
            chordPart.setStyle(tempStyle);
            return chordPart;
          }
        catch( Exception e )
          {
            cannotPlay();
            return null;
          }
      }
    else
      {
        cannotPlay();
        return null;
      }
  }


/**
 * If the pattern is legal, creates a style with one chordPart consisting of a
 * single chord and adds the entire pattern to that style. Uses the volume,
 * tempo, and chord info from the toolbar.
 */

public boolean playMe(double swingVal, int loopCount, double tempo, Score s)
  {
    canPlay();
//System.out.println("playing " + this);
    if( checkStatus() )
      {
        try
          {
            String p = this.getPattern(false);
            Polylist rule = Notate.parseListFromString(p);
            //System.out.println("pattern = " + p + "\nrule = " + rule);
            if( rule.isEmpty() )
              {
                cannotPlay();
                return false;
              }
            Style tempStyle = Style.makeStyle(rule);
            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);
            tempStyle.setName("drumPattern");
            Style.setStyle("drumPattern", tempStyle);
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            String chord = styleEditor.getChord();
            ChordPart c = new ChordPart();
            boolean muteChord = styleEditor.isChordMuted();

            int duration = tempStyle.getDrumPatternDuration();
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

            new PlayScoreCommand(s,
                                 0,
                                 true,
                                 notate.getMidiSynth(),
                                 loopCount,
                                 notate.getTransposition()).execute();
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


// Only to satisfy interface: FIX

public Color getPlayableColor()
  {
    return null;
  }

// Only to satisfy interface: FIX

public Color getUnplayableColor()
  {
    return null;
  }

}
