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
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.ImageIcon;
import polya.Polylist;


/**
 * Creates a GUI that displays a drum pattern used in styles.
 * Created Summer 2007
 * @authors  Brandy McMenamy, Sayuri Soejima
 */
public class DrumPatternDisplay 
        extends PatternDisplay 
        implements Playable, Constants {

    //The image next to the pattern text if the pattern is legal
    private static ImageIcon goodRule;
    //The image next to the pattern text if the pattern is illegal
    private static ImageIcon badRule;
    
    //Various standard messages displayed in tooltips and error dialogs.
    private String safeMsgRule = "This rule is legal.  Click play button to preview it.";
    private String safeMsgPattern = "This pattern is legal.  Click play button to preview it.";
    private String unsafeMsgPattern = "This pattern contains an illegal rule.";

    //Contains a specialized error message for any error that just occurred.
    private String errorMsg = safeMsgRule;
    private String unequalBeatsMsg = "All drum rules must have the same number of beats";
   
    //The lowest weight allowed for a pattern.
    private int lowestWeight = 1;
    //The highest weight allowed for a pattern.
    private int highestWeight = 100;
    //The number added to the title of this object to help the user distinguish it from others.
    private int titleNumber = 0; 

    //The dimension to use when the pane is expanded.  By default, it is the size of the entire panel
    private Dimension expandedDimension;
    //The dimension to use when the pane is collapsed.  By default, it is the size of the northern panel's preferred size.
    private Dimension collapsedDimension;
    
    //The currently selected DrumRuleDisplay
    private DrumRuleDisplay curSelectedRule = null;
    //The previously selected DrumRuleDisplay
    private DrumRuleDisplay lastSelectedRule = null;
    
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
     * Constructs a new DrumPatternDisplay JPanel with default weight 3 and an empty pattern.
     **/
    public DrumPatternDisplay(Notate parent, CommandManager cm, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        initialize(3);
    }
    
    /**
     * Constructs a new BassPatternDisplay JPanel with weight and rule parameters.
     **/
    public DrumPatternDisplay(float weight, Notate parent, CommandManager cm, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        initialize(weight);
    }
 
   /**
     * Initializes all elements and components for the DrumPatternDisplay GUI and collapses the pane.
     **/
    private void initialize(float weight){
        /*Ensures that useful items like rhythm durations for notes are ready for use even
          if the user has not yet generated a style from midi*/
        if(!MIDIBeast.invoked) {
            MIDIBeast.invoke();
        }

        initComponents();
         
        setWeight(weight);
        
        //Initializes attributes needed for collapsing panes and collapses the BassPatternDisplay object.
        expandedDimension = this.getPreferredSize();
        
        checkStatus();
    } 
    

    
    //Accessors:
    
  /**
   * @return titleNumber
   **/    
   public int getTitleNumber() {
        return titleNumber;
    }

    /**
     * @param requireChecked means that the instrument must be checked in the StyleEditor in order
     * for this pattern to be non-empty.
     * @return all legal drum rules and weight formatted with the drum-pattern syntax used by the style classes 
     * 
     **/    
    public String getPattern(boolean requireChecked) {
        String pattern = "(drum-pattern ";

        for( Iterator<DrumRuleDisplay> e = rules.iterator(); e.hasNext(); )
        {
            try {
                DrumRuleDisplay d = e.next();
                // See if instrument is to be included per checkbox in editor
                // FIX: This is round-about, and should be changed to iterate directly over
                // table column, rather than going through drumRuleHolder.

                int instrumentNumber = d.getInstrumentNumber();
 
                //System.out.println("d = " + d.getRule());

                int volume = 127; // FIX
                
                if( styleEditor.isDrumInstrumentNumberIncluded(instrumentNumber) ) // NOT WORKING
                  {
                    if( d.checkStatus() )
                         {
                         pattern += "\n\t\t" + d.getRule(volume);
                         }
                else {
                    ErrorLog.log(ErrorLog.WARNING, "error in drum rule: " + d.getRule(volume));
                }
                  }
             }catch(ClassCastException ex) {}
        }      
        pattern += "\n\t\t(weight " + getWeight() + ")\n\t)";       
        return pattern;
    }
    
    /**
     * This is not used currently, but needed to fulfill the interface requirement.
     @param string
     */

    public void setDisplayText(String string)
    {

    }
    
    /**
     * @return the most recent error message that occurred for the entire pattern
     **/ 
     public String getPatternError() {
       /* Disable
        if(errorMsg.equals(unequalBeatsMsg))
            errorMsg = "Drum pattern " + this.getTitleNumber() + " contains an illegal rule";
        */
         return errorMsg;
    } 
    

    /**
     * @return the number of components in the drumRuleHolder pane (this is where DrumRuleDisplay objects are added)
     **/
    public int getNumComponents() {
        return rules.size();
    }
    
    /**
     * @return the ith components in the drumRuleHolder pane if there is an 
     * ith component.  Return null otherwise.
     **/    
    public Component getComponentAt(int i) {
        return null;
    }
    
   /**
    * @return the length of the pattern, which is the length of the first 
    * DrumRuleDisplay rule. Returns -1 if the rule is malformed
    *  or there are no rules in the pane.
    * Checks for equal lengths between rules are handled elsewhere for speed reasons.
    *
    * NOTE: Length here is in slots, not beats!!
    **/      

public double getPatternLength()
  {
    double maxBeats = 0;

    for( DrumRuleDisplay d : rules )
      {
        if( d.checkStatus() )
          {
            double ruleLength = d.getPatternLength();
            if( ruleLength > maxBeats )
              {
                maxBeats = ruleLength;
              }
          }
      }
    return maxBeats;
  }
     
   /**
    * @return the number of beats in the pattern.
    */
    
    public double getBeats() {
      double patternLength = getPatternLength();
      
      return patternLength >= 0 ? patternLength/BEAT : -1;
    }

    
    /**
     * @return curSelectedRule
     **/
    public DrumRuleDisplay getSelectedRule() {
        return curSelectedRule;
    }       
    
    //Mutators:
    
    /**
     * Sets the number in the title to num.
     **/ 
    public void setTitleNumber(int num) {
        titleNumber = num;
    }     
   

    /**
     * Adds rule to the correct pane and updates pertinent information.
     * Need to make sure there is only one rule for a given instrument.
     **/
    public void addRule(DrumRuleDisplay rule) {
        if(rule != null) {
            int instrumentNumber = rule.getInstrumentNumber();
            for( DrumRuleDisplay d: rules )
              {
                if( d.getInstrumentNumber() == instrumentNumber )
                  {
                    rules.remove(d);
                    break;
                  }
              }
            rules.add(rule);
            //drumRuleHolder.add(rule);
            checkStatus();
        }
    }
    
    /**
     * Removes rule 
     **/
    public void removeRule(DrumRuleDisplay rule) {
        if(rule != null) {

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
     * Recalculate the length of the pattern text given the time signuatre.
     * Displays "unknown" if the pattern is incorrectly formatted or a rule has unknown rhythm durations or
     * all rules do not have the same number of beats.
     **/
    public void updateLength() {

       double duration = getPatternLength();

    }
     

    /**
     * Changes the appearance of this DrumPatternDisplay to "deselected" and unselects any selected rule.
     **/
    public void setDeselectedAppearance() {

    }
    
   /**
     * Changes the appearance of this DrumPatternDisplay to "selected"
    **/    
    public void setSelectedAppearance() {
        
    } 
     
    /**
     * Checks the pattern for correctness.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     * @return true if the pattern is a correctly formed (all rules are legal and have the same number of beats)
     *  and therefore playable by Impro-Visor. Returns false otherwise.
     **/    
    public boolean checkStatus() {
        boolean patternLength = checkPatternLength();
        updateLength();
        if(!patternLength)
            return false;
          
        errorMsg = safeMsgPattern;
        return true;
    }

    
    /**
     * @return true if all rules in the pattern have the same number of beats. Returns false otherwise.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     **/      
    private boolean checkPatternLength() {

        errorMsg = safeMsgRule;
        return true;
    }
    
    /**
     * Makes a copy of the currently selected rule before removing it.
     **/
    public void cutSelectedRule() {
        if(curSelectedRule != null) {
            styleEditor.copyDrumRule(curSelectedRule);

            curSelectedRule = null;

        }
    }
    
    /**
     * Cut a rule by object identity, through the API, not GUI.
     **/
    
    public void cutRule(DrumRuleDisplay ruleToCut) {

            curSelectedRule = null;

        }

    /**
     * Creates a new DrumRuleDisplay object with all attributes of pasteMe and then adds it to this
     * DrumPatternDisplay object via the addRule method.
     **/
    public void pasteRule(DrumRuleDisplay pasteMe) {
        DrumRuleDisplay newRule = new DrumRuleDisplay(pasteMe.getDisplayText(), pasteMe.getInstrument(), notate, cm, this, styleEditor);
        addRule(newRule);
    }
    /**
    * Collapses and expands the pattern information pane.
    **/   
    private void expand() {
        if(isExpanded == false) {
            
            isExpanded = true;
            
        } else {
             isExpanded = false;
        }        
    }
    
    /**
     * @return true if all rules in the pattern have the same number of beats. Returns false otherwise.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     **/      
    public String getDisplayText() {
        StringBuilder buffer = new StringBuilder();

        for( DrumRuleDisplay d: rules ) {
            try {        
                buffer.append("(");
                buffer.append(d.getDisplayText());
                buffer.append(") ");
            }catch(ClassCastException e) {}
        }
        /*This is changed for benifit of updateLength()
          The decision to change tooltip and icon is handled by the next step in checkPatternLength */
        return buffer.toString();
    }
    
    @Override
    public String toString()
    {
      return "DrumPatternDisplay with " + rules.size() + " rules";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 171, 87), 1, true));
        setMinimumSize(new java.awt.Dimension(527, 200));
        setPreferredSize(new java.awt.Dimension(527, 250));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed
  

/**
 * If the pattern is legal, creates a style with one chordPart consisting of a single chord and adds
 *   the entire pattern to that style.  Uses the volume, tempo, and chord info from the toolbar.
 */

public ChordPart makeCountIn(double swingVal, int loopCount, double tempo)
  {
    canPlay();

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

            ChordPart c = new ChordPart();

            boolean muteChord = styleEditor.isChordMuted();

            int duration = tempStyle.getDrumPatternDuration();
            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            return c;
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
 * If the pattern is legal, creates a style with one chordPart consisting of a single chord and adds
 *   the entire pattern to that style.  Uses the volume, tempo, and chord info from the toolbar.
 */

public boolean playMe(double swingVal, int loopCount, double tempo, Score s)
  {
    canPlay();

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
   
  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
