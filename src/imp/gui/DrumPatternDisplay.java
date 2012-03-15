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
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import polya.Polylist;


/**
 * Creates a GUI that displays a drum pattern used in styles.
 * Created Summer 2007
 * @authors  Brandy McMenamy, Sayuri Soejima
 */
public class DrumPatternDisplay 
        extends PatternDisplay 
        implements Playable, Constants {

    float weight = 0;
    
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
    
    /**g
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
        initWeight();
        
        setWeight(weight);
        
        //Initializes attributes needed for collapsing panes and collapses the BassPatternDisplay object.
        expandedDimension = this.getPreferredSize();
        collapsedDimension = northPanel.getPreferredSize();
        southPanel.setVisible(false);
      //  this.setPreferredSize(collapsedDimension);
      //  this.setMaximumSize(collapsedDimension);
        setDeselectedAppearance();
        
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

        //Component[] allRules = drumRuleHolder.getComponents();
        //for(int i = 0; i < allRules.length; i++) {

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
        pattern += "\n\t\t(weight " + ((Integer) weightSpinner.getValue()) + ")\n\t)";       
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
     * @return the value found in the weight spinner or lowestWeight if the value is not an Integer.
     **/
    public float getWeight() {
           return weight;
    }

    /**
     * @return the number of components in the drumRuleHolder pane (this is where DrumRuleDisplay objects are added)
     **/
    public int getNumComponents() {
        return drumRuleHolder.getComponentCount();
    }
    
    /**
     * @return the ith components in the drumRuleHolder pane if there is an ith component.  Return null otherwise.
     **/    
    public Component getComponentAt(int i) {
        if(i >= 0 && i < drumRuleHolder.getComponentCount())
            return drumRuleHolder.getComponent(i);
        return null;
    }
    
   /**
    * @return the length of the pattern, which is the length of the first DrumRuleDisplay rule. Returns -1 if the rule is malformed
    *  or there are no rules in the pane.
    * Checks for equal lengths between rules are handled elsewhere for speed reasons.
    *
    * NOTE: Length here is in slots, not beats!!
    **/      
    public float getPatternLength() {
        Component[] allRules = drumRuleHolder.getComponents();
        int numRules = allRules.length;
        
        float maxBeats = 0;
        
        for( int i = 0; i < numRules; i++ )
            {
                DrumRuleDisplay d = (DrumRuleDisplay) allRules[i];
                if(d.checkStatus())
                {
                 float ruleLength = Duration.getDuration(d.getDisplayText());
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
    * RK: This is stubbed until issues can be worked out. We want it to be the maximum number of beats in a rule.
    * I don't think it matters whether the number of beats in different rules are unequal.
    */
    
    public double getBeats() {
      float patternLength = getPatternLength();
      
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
     * Sets the weight in the spinner to the parameter weight if it is within the range of lowestWeight to heighestWeight.
     **/        
    public void setWeight(float weight) {
        this.weight = (float)weight;
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
            drumRuleHolder.add(rule);
            checkStatus();
        }
    }
    
    /**
     * Removes rule 
     **/
    public void removeRule(DrumRuleDisplay rule) {
        if(rule != null) {
            drumRuleHolder.remove(rule);
            drumRuleHolder.updateUI();
        }
    }
    
    
    /**
     * Return the number of rules in this pattern
     */
    
    public int getRuleCount()
    {
      return drumRuleHolder.getComponentCount();
    }
  
    
   /**
     * Recalculate the length of the pattern text given the time signuatre.
     * Displays "unknown" if the pattern is incorrectly formatted or a rule has unknown rhythm durations or
     * all rules do not have the same number of beats.
     **/
    public void updateLength() {

       float duration = getPatternLength();

    }
    
     /**
     * Creates the SpinnerModel used for the weight field.
     **/   
    private void initWeight() {
        SpinnerModel model = new SpinnerNumberModel(lowestWeight, lowestWeight, highestWeight, 1);
        weightSpinner.setModel(model);
    }    

    /**
     * Changes the appearance of this DrumPatternDisplay to "deselected" and unselects any selected rule.
     **/
    public void setDeselectedAppearance() {
        this.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255,171,87), 1, true));


    }
    
   /**
     * Changes the appearance of this DrumPatternDisplay to "selected"
    **/    
    public void setSelectedAppearance() {
       styleEditor.setSelectedDrum(this);        
    } 
     
    /**
     * Checks the pattern for correctness.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     * @return true if the pattern is a correctly formed (all rules are legal and have the same number of beats)
     *  and therefore playable by Impro-Visor. Returns false otherwise.
     **/    
    public boolean checkStatus() {
        Component[] allRules = drumRuleHolder.getComponents();
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
            drumRuleHolder.remove(curSelectedRule);
            curSelectedRule = null;
            drumRuleHolder.updateUI();
        }
    }
    
    /**
     * Cut a rule by object identity, through the API, not GUI.
     **/
    
    public void cutRule(DrumRuleDisplay ruleToCut) {
            drumRuleHolder.remove(ruleToCut);
            curSelectedRule = null;
            drumRuleHolder.updateUI();
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
            southPanel.setVisible(true);
      //      this.setPreferredSize(expandedDimension);
      //      this.setMaximumSize(expandedDimension);
            
            isExpanded = true;
            
        } else {
            southPanel.setVisible(false);
      //      this.setPreferredSize(collapsedDimension);
       //     this.setMaximumSize(collapsedDimension);

            isExpanded = false;
        }        
    }
    
    /**
     * @return true if all rules in the pattern have the same number of beats. Returns false otherwise.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     **/      
    public String getDisplayText() {
        StringBuffer buffer = new StringBuffer();
        Component[] allRules = drumRuleHolder.getComponents();
        for(int i = 0; i < allRules.length; i++) {
            try {        
                DrumRuleDisplay d = (DrumRuleDisplay) allRules[i];
                buffer.append("(");
                buffer.append(d.getDisplayText());
                buffer.append(") ");
            }catch(ClassCastException e) {}
        }
        /*This is changed for benifit of updateLength()
          The decision to change tooltip and icon is handled by the next step in checkPatternLength */
        return buffer.toString();
    }
    
    public String toString()
    {
      return "DrumPatternDisplay with " + drumRuleHolder.getComponents().length + " rules";
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        northPanel = new javax.swing.JPanel();
        itemPanel = new javax.swing.JPanel();
        weightSpinner = new javax.swing.JSpinner();
        southPanel = new javax.swing.JPanel();
        centerRulePane = new javax.swing.JScrollPane();
        drumRuleHolder = new javax.swing.JPanel();

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

        northPanel.setBackground(new java.awt.Color(255, 171, 87));
        northPanel.setMinimumSize(new java.awt.Dimension(527, 30));
        northPanel.setPreferredSize(new java.awt.Dimension(527, 30));
        northPanel.setRequestFocusEnabled(false);
        northPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                northPanelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                northPanelMousePressed(evt);
            }
        });
        northPanel.setLayout(new java.awt.BorderLayout());

        itemPanel.setBackground(new java.awt.Color(255, 255, 255));
        itemPanel.setMinimumSize(new java.awt.Dimension(515, 33));
        itemPanel.setOpaque(false);
        itemPanel.setPreferredSize(new java.awt.Dimension(515, 33));
        itemPanel.setRequestFocusEnabled(false);
        itemPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                itemPanelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                itemPanelMousePressed(evt);
            }
        });
        itemPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        weightSpinner.setMinimumSize(new java.awt.Dimension(35, 18));
        weightSpinner.setPreferredSize(new java.awt.Dimension(35, 18));
        weightSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                weightSpinnerStateChanged(evt);
            }
        });
        weightSpinner.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                weightSpinnerMousePressed(evt);
            }
        });
        itemPanel.add(weightSpinner);

        northPanel.add(itemPanel, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(northPanel, gridBagConstraints);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setMinimumSize(new java.awt.Dimension(490, 200));
        southPanel.setOpaque(false);
        southPanel.setPreferredSize(new java.awt.Dimension(490, 200));
        southPanel.setRequestFocusEnabled(false);
        southPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                southPanelMousePressed(evt);
            }
        });
        southPanel.setLayout(new java.awt.GridBagLayout());

        centerRulePane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        centerRulePane.setMinimumSize(new java.awt.Dimension(515, 200));
        centerRulePane.setPreferredSize(new java.awt.Dimension(515, 200));
        centerRulePane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                centerRulePaneMousePressed(evt);
            }
        });

        drumRuleHolder.setBackground(new java.awt.Color(255, 255, 255));
        drumRuleHolder.setAutoscrolls(true);
        drumRuleHolder.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                drumRuleHolderMouseExited(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                drumRuleHolderMousePressed(evt);
            }
        });
        drumRuleHolder.setLayout(new javax.swing.BoxLayout(drumRuleHolder, javax.swing.BoxLayout.Y_AXIS));
        centerRulePane.setViewportView(drumRuleHolder);

        southPanel.add(centerRulePane, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        add(southPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void itemPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_itemPanelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_itemPanelMouseClicked

    private void northPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_northPanelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_northPanelMouseClicked

    private void weightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_weightSpinnerStateChanged
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_weightSpinnerStateChanged

    private void drumRuleHolderMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drumRuleHolderMouseExited
// ignore this one...can't delete!!!! :'(
    }//GEN-LAST:event_drumRuleHolderMouseExited

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed

    private void weightSpinnerMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_weightSpinnerMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_weightSpinnerMousePressed

    private void itemPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_itemPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_itemPanelMousePressed

    private void drumRuleHolderMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drumRuleHolderMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_drumRuleHolderMousePressed

    private void centerRulePaneMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_centerRulePaneMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_centerRulePaneMousePressed

    private void southPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_southPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_southPanelMousePressed

    private void northPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_northPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_northPanelMousePressed
  

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
    private javax.swing.JScrollPane centerRulePane;
    private javax.swing.JPanel drumRuleHolder;
    private javax.swing.JPanel itemPanel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JPanel southPanel;
    private javax.swing.JSpinner weightSpinner;
    // End of variables declaration//GEN-END:variables
}
