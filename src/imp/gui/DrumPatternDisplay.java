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

import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.Constants;
import imp.data.*;
import java.awt.*;
import javax.swing.*;
import polya.Polylist;


/**
 * Creates a GUI that displays a drum pattern used in styles.
 * Created Summer 2007
 * @authors  Brandy McMenamy, Sayuri Soejima
 */
public class DrumPatternDisplay 
        extends PatternDisplay 
        implements Playable, Constants {
    //The image that is visible when the pane is collapsed and the pattern is legal.
    private static ImageIcon goodPattern;  
    //The image that is visible when the pane is collapsed and the pattern is illegal.
    private static ImageIcon badPattern;
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
    
    // To satisfy interface only
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
        
        goodPattern = new ImageIcon("src/imp/gui/graphics/icons/goodpattern.png");
        badPattern = new ImageIcon("src/imp/gui/graphics/icons/badPattern.png");
        goodRule = new ImageIcon("src/imp/gui/graphics/greenCircle.png");
	badRule = new ImageIcon("src/imp/gui/graphics/redSquare.png");   

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
    
    /**
     * Fills the DrumPatternDisplay with three empty DrumRuleDisplay objects with default instruments.
     **/
    public void fill() {
        DrumRuleDisplay d = new DrumRuleDisplay(null, "Acoustic Bass", this.parent, this.cm, this, styleParent);
        d.setDisplayText("X4");
        this.addRule(d);
        d = new DrumRuleDisplay(null, "Low Floor Tom", this.parent, this.cm, this, styleParent);
        d.setDisplayText("X4");
        this.addRule(d);
        d = new DrumRuleDisplay(null, "Closed Hi-Hat", this.parent, this.cm, this, styleParent);
        d.setDisplayText("X4");        
        this.addRule(d);
        d.updateUI();
        this.updateUI();
        setDeselectedAppearance();
    }
    
    //Accessors:
    
  /**
   * @return titleNumber
   **/    
   public int getTitleNumber() {
        return titleNumber;
    }

    /**
     * @return all legal drum rules and weight formatted with the drum-pattern syntax used by the style classes 
     **/    
    public String getPattern() {
        String pattern = "(drum-pattern ";
        Component[] allRules = drumRuleHolder.getComponents();
        for(int i = 0; i < allRules.length; i++) {
            try {
                DrumRuleDisplay d = (DrumRuleDisplay) allRules[i];
                //System.out.println("rule " + d.getInstrument());
                // See if instrument is to be included per checkbox in editor
                // FIX: This is round-about, and should be changed to iterate directly over
                // table column, rather than going through drumRuleHolder.
                if( styleParent.isInstrumentIncluded(d.getInstrument()) )
                {
                //System.out.println("instrument included: " + d.getInstrument());
                if(d.checkStatus() ) {
                    pattern += "\n\t\t" + d.getRule();
                }
                else {
                    MIDIBeast.addSaveError(d.getErrorMsgRule() + " and was not inclued in Drum Pattern " + this.getTitleNumber());
                }          
                }
                else
                {
                //System.out.println("instrument NOT included: " + d.getInstrument());
                }
            }catch(ClassCastException e) {}
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
    public int getWeight() {
        try{
            Integer weight = (Integer) weightSpinner.getValue();
            return weight;
        }catch(ClassCastException e) {
            return lowestWeight;
        }
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
                 float ruleLength = (float)MIDIBeast.numBeatsInRule(d.getDisplayText());
                  if( ruleLength > maxBeats )
                  {
                    maxBeats = ruleLength;
                  }
                }
            }
       return maxBeats;
    }

    /* old version
         public int getPatternLength() {
        Component[] allRules = drumRuleHolder.getComponents();
        if(allRules.length > 0) {
            try {
                DrumRuleDisplay d = (DrumRuleDisplay) allRules[0];
                if(d.checkRuleStatus())
                    return (int) MIDIBeast.numBeatsInRule(d.getDisplayText());                
            }catch(ClassCastException e) {}
        }
        return -1;
    }
    */
    
    
   /**
    * @return the number of beats in the pattern.
    * RK: This is stubbed until issues can be worked out. We want it to be the maximum number of beats in a rule.
    * I don't it matters whether the number of beats in different rules are unequal.
    */
    
    public double getBeats() {
      float patternLength = getPatternLength();
      
      return patternLength >= 0 ? patternLength/BEAT : -1;
    }
    
   /**
     * @return the selected value of the checkbox marked "include" in the upper right corner
     **/
    public boolean getIncludedStatus() {
        return includeBox.isSelected();
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
        nameTitle.setText("Drum Pattern " + num + ":");        
        titleNumber = num;
    }     
   
    /**
     * Sets the weight in the spinner to the parameter weight if it is within the range of lowestWeight to heighestWeight.
     **/        
    public void setWeight(float weight) {
        if(weight < lowestWeight) {
            weightSpinner.setValue((Integer) lowestWeight);
        }
        else if(weight > highestWeight) {
            weightSpinner.setValue((Integer) highestWeight);
        }
        else
            weightSpinner.setValue((Integer) (int)weight);
    }
    
    /**
     * Adds rule to the correct pane and updates pertinent information.
     **/
    public void addRule(DrumRuleDisplay rule) {
        if(rule != null) {
            drumRuleHolder.add(rule);
            drumRuleHolder.updateUI();
            unselectInstrument(rule);    
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
     * Sets curSelectedRule to selectMe and deselects former selected rule if appropriate.
     **/
    public void setSelectedInstrument(DrumRuleDisplay selectMe) {
        lastSelectedRule = curSelectedRule;
        curSelectedRule = selectMe;
        if(lastSelectedRule != null && lastSelectedRule != curSelectedRule) {
             unselectInstrument(lastSelectedRule);
        }
    }
    
    /**
     * Changes appearance of deselectMe to deselected mode.
     **/
    private void unselectInstrument(DrumRuleDisplay deselectMe) {
        if(deselectMe != null) {
            deselectMe.setDeselectedAppearance();
        }        
    }
    
   /**
     * Recalculate the length of the pattern text given the time signuatre.
     * Displays "unknown" if the pattern is incorrectly formatted or a rule has unknown rhythm durations or
     * all rules do not have the same number of beats.
     **/
    public void updateLength() {
      /*
       if(errorMsg.equals(unequalBeatsMsg)) {
          lengthTitle.setText("Unknown length");
          return;
       }
       */

       float duration = getPatternLength();
       if(duration == -1){
           lengthTitle.setText("Unknown length");
       }
       else {
           lengthTitle.setText(duration/MIDIBeast.beat + " beats");
       }
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
        northPanel.setBackground(new java.awt.Color(255,171,87));
        includeBox.setBackground(new java.awt.Color(255,171,87));
        nameTitle.setBackground(new java.awt.Color(255,171,87));
        lengthTitle.setBackground(new java.awt.Color(255,171,87));
        includePlayPanel.setBackground(new java.awt.Color(255,171,87));
        titlePanel.setBackground(new java.awt.Color(255,171,87));
        if(curSelectedRule != null) {
            unselectInstrument(curSelectedRule);
        }
    }
    
   /**
     * Changes the appearance of this DrumPatternDisplay to "selected"
    **/    
    public void setSelectedAppearance() {
       styleParent.setSelectedDrum(this);
       northPanel.setBackground(new java.awt.Color(255,102,0));
       includeBox.setBackground(new java.awt.Color(255,102,0));
       nameTitle.setBackground(new java.awt.Color(255,102,0));
       lengthTitle.setBackground(new java.awt.Color(255,102,0));
       includePlayPanel.setBackground(new java.awt.Color(255,102,0));
       titlePanel.setBackground(new java.awt.Color(255,102,0));
       this.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED,
            new java.awt.Color(255,232,186), new java.awt.Color(255,232,186), new java.awt.Color(255,102,0),
            new java.awt.Color(255,102,0)));         
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
        
        /* Eliminate this check
        for(int i = 0; i < allRules.length; i++) {
            try {        
                DrumRuleDisplay d = (DrumRuleDisplay) allRules[i];
                if(!d.checkRuleStatus()) {
                    errorMsg = unequalBeatsMsg;
                    nameTitle.setIcon(badPattern);
                    nameTitle.setToolTipText(unsafeMsgPattern);
                    return false;
                }
            }
            catch(ClassCastException e) {}
        }
        */
          
        errorMsg = safeMsgPattern;
        nameTitle.setIcon(goodPattern);
        nameTitle.setToolTipText(safeMsgPattern);
        return true;
    }

    
    /**
     * @return true if all rules in the pattern have the same number of beats. Returns false otherwise.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     **/      
    private boolean checkPatternLength() {
/* RK Temporarily disable this check           
        Component[] allRules = drumRuleHolder.getComponents();
        int previousDur = -1;
        for(int i = 0; i < allRules.length; i++) {
            try {        
                DrumRuleDisplay d = (DrumRuleDisplay) allRules[i];
                 int nextDur = (int) MIDIBeast.numBeatsInRule(d.getDisplayText());
                if(previousDur == -1) 
                    previousDur = nextDur;
                if(nextDur != previousDur) {
                    errorMsg = "Drum pattern " + this.getTitleNumber() + " contains rules without the same number of beats";
                    nameTitle.setIcon(badPattern);
                    nameTitle.setToolTipText(unequalBeatsMsg);
                    return false;
                }     
            }catch(ClassCastException e) {}
        }
 */
       /*This is changed for benifit of updateLength()
          The decision to change tooltip and icon is handled by the next step in checkPatternLength */
        errorMsg = safeMsgRule;
        return true;
    }
    
    /**
     * Makes a copy of the currently selected rule before removing it.
     **/
    public void cutSelectedRule() {
        if(curSelectedRule != null) {
            styleParent.copyDrumRule(curSelectedRule);
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
        DrumRuleDisplay newRule = new DrumRuleDisplay(pasteMe.getDisplayText(), pasteMe.getInstrument(), parent, cm, this, styleParent);
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
            
            northPanel.setToolTipText("Double click to collapse pattern information.");
            titlePanel.setToolTipText("Double click to collapse pattern information.");
            lengthTitle.setToolTipText("Double click to collapse pattern information.");
            includePlayPanel.setToolTipText("Double click to collapse pattern information.");
            isExpanded = true;
            
        } else {
            southPanel.setVisible(false);
      //      this.setPreferredSize(collapsedDimension);
       //     this.setMaximumSize(collapsedDimension);

            northPanel.setToolTipText("Double click to expand pattern information.");
            titlePanel.setToolTipText("Double click to expand pattern information.");
            lengthTitle.setToolTipText("Double click to expand pattern information.");
            includePlayPanel.setToolTipText("Double click to expand pattern information.");

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
  private void initComponents()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    northPanel = new javax.swing.JPanel();
    titlePanel = new javax.swing.JPanel();
    nameTitle = new javax.swing.JLabel();
    lengthTitle = new javax.swing.JLabel();
    includePlayPanel = new javax.swing.JPanel();
    playPatternBtn = new javax.swing.JButton();
    includeBox = new javax.swing.JCheckBox();
    itemPanel = new javax.swing.JPanel();
    weightLabel = new javax.swing.JLabel();
    weightSpinner = new javax.swing.JSpinner();
    southPanel = new javax.swing.JPanel();
    centerRulePane = new javax.swing.JScrollPane();
    drumRuleHolder = new javax.swing.JPanel();

    setBackground(new java.awt.Color(255, 255, 255));
    setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 171, 87), 1, true));
    setMinimumSize(new java.awt.Dimension(527, 200));
    setPreferredSize(new java.awt.Dimension(527, 250));
    addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        formMousePressed(evt);
      }
    });
    setLayout(new java.awt.GridBagLayout());

    northPanel.setBackground(new java.awt.Color(255, 171, 87));
    northPanel.setMinimumSize(new java.awt.Dimension(527, 30));
    northPanel.setPreferredSize(new java.awt.Dimension(527, 30));
    northPanel.setRequestFocusEnabled(false);
    northPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        northPanelMouseClicked(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        northPanelMousePressed(evt);
      }
    });
    northPanel.setLayout(new java.awt.BorderLayout());

    titlePanel.setBackground(new java.awt.Color(255, 171, 87));
    titlePanel.setToolTipText("Double click to expand pattern information.");
    titlePanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        titlePanelMouseClicked(evt);
      }
    });

    nameTitle.setBackground(new java.awt.Color(255, 171, 87));
    nameTitle.setFont(new java.awt.Font("Tahoma", 1, 11));
    nameTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/goodpattern.png"))); // NOI18N
    nameTitle.setText("Drum Pattern");
    nameTitle.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    nameTitle.setIconTextGap(10);
    nameTitle.setMaximumSize(new java.awt.Dimension(118, 14));
    nameTitle.setMinimumSize(new java.awt.Dimension(118, 14));
    nameTitle.setPreferredSize(new java.awt.Dimension(118, 14));
    nameTitle.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        nameTitleMouseClicked(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        nameTitleMousePressed(evt);
      }
    });
    titlePanel.add(nameTitle);

    lengthTitle.setBackground(new java.awt.Color(255, 171, 87));
    lengthTitle.setFont(new java.awt.Font("Tahoma", 1, 11));
    lengthTitle.setText("Length: unknown");
    lengthTitle.setToolTipText("Double click to expand pattern information.");
    lengthTitle.setMaximumSize(new java.awt.Dimension(115, 14));
    lengthTitle.setMinimumSize(new java.awt.Dimension(115, 14));
    lengthTitle.setPreferredSize(new java.awt.Dimension(115, 14));
    lengthTitle.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        lengthTitleMouseClicked(evt);
      }
    });
    titlePanel.add(lengthTitle);

    northPanel.add(titlePanel, java.awt.BorderLayout.WEST);

    includePlayPanel.setBackground(new java.awt.Color(255, 171, 87));
    includePlayPanel.setToolTipText("Double click to expand pattern information.");
    includePlayPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        includePlayPanelMouseClicked(evt);
      }
    });

    playPatternBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/play.png"))); // NOI18N
    playPatternBtn.setToolTipText("Click to play pattern.");
    playPatternBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    playPatternBtn.setIconTextGap(0);
    playPatternBtn.setMaximumSize(new java.awt.Dimension(20, 20));
    playPatternBtn.setMinimumSize(new java.awt.Dimension(20, 20));
    playPatternBtn.setOpaque(false);
    playPatternBtn.setPreferredSize(new java.awt.Dimension(20, 20));
    playPatternBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        playPatternBtnActionPerformed(evt);
      }
    });
    playPatternBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        playPatternBtnMousePressed(evt);
      }
    });
    includePlayPanel.add(playPatternBtn);

    includeBox.setBackground(new java.awt.Color(255, 171, 87));
    includeBox.setSelected(true);
    includeBox.setText("include");
    includeBox.setToolTipText("Click to exclude this pattern from style.");
    includeBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
    includeBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
    includeBox.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        includeBoxMouseClicked(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        includeBoxMousePressed(evt);
      }
    });
    includeBox.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        includeBoxActionPerformed(evt);
      }
    });
    includePlayPanel.add(includeBox);

    northPanel.add(includePlayPanel, java.awt.BorderLayout.EAST);

    itemPanel.setBackground(new java.awt.Color(255, 255, 255));
    itemPanel.setMinimumSize(new java.awt.Dimension(515, 33));
    itemPanel.setOpaque(false);
    itemPanel.setPreferredSize(new java.awt.Dimension(515, 33));
    itemPanel.setRequestFocusEnabled(false);
    itemPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        itemPanelMouseClicked(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        itemPanelMousePressed(evt);
      }
    });
    itemPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

    weightLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
    weightLabel.setText("Weight:");
    weightLabel.setToolTipText("The higher the weight, the greater the likelihood this pattern will play during a song.");
    weightLabel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        weightLabelMouseClicked(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        weightLabelMousePressed(evt);
      }
    });
    itemPanel.add(weightLabel);

    weightSpinner.setMinimumSize(new java.awt.Dimension(35, 18));
    weightSpinner.setPreferredSize(new java.awt.Dimension(35, 18));
    weightSpinner.addChangeListener(new javax.swing.event.ChangeListener()
    {
      public void stateChanged(javax.swing.event.ChangeEvent evt)
      {
        weightSpinnerStateChanged(evt);
      }
    });
    weightSpinner.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
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
    southPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        southPanelMousePressed(evt);
      }
    });
    southPanel.setLayout(new java.awt.GridBagLayout());

    centerRulePane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    centerRulePane.setMinimumSize(new java.awt.Dimension(515, 200));
    centerRulePane.setPreferredSize(new java.awt.Dimension(515, 200));
    centerRulePane.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        centerRulePaneMousePressed(evt);
      }
    });

    drumRuleHolder.setBackground(new java.awt.Color(255, 255, 255));
    drumRuleHolder.setAutoscrolls(true);
    drumRuleHolder.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseExited(java.awt.event.MouseEvent evt)
      {
        drumRuleHolderMouseExited(evt);
      }
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
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

    private void weightLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_weightLabelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_weightLabelMouseClicked

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

    private void titlePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titlePanelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_titlePanelMouseClicked

    private void nameTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameTitleMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_nameTitleMouseClicked

    private void lengthTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lengthTitleMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_lengthTitleMouseClicked

    private void includePlayPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_includePlayPanelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_includePlayPanelMouseClicked

    private void includeBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_includeBoxMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_includeBoxMouseClicked

    private void weightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_weightSpinnerStateChanged
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_weightSpinnerStateChanged

    private void drumRuleHolderMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drumRuleHolderMouseExited
// ignore this one...can't delete!!!! :'(
    }//GEN-LAST:event_drumRuleHolderMouseExited

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed

    private void playPatternBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playPatternBtnMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_playPatternBtnMousePressed

    private void weightSpinnerMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_weightSpinnerMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_weightSpinnerMousePressed

    private void weightLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_weightLabelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_weightLabelMousePressed

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

    private void includeBoxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_includeBoxMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_includeBoxMousePressed

    private void nameTitleMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameTitleMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_nameTitleMousePressed

    private void northPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_northPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_northPanelMousePressed
  
    private void playPatternBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnActionPerformed
       playMe();
    }
    
    public boolean playMe(double swingVal)
    {
    return playMe(swingVal, 0);
    }

      /**
       * If the pattern is legal, creates a style with one chordPart consisting of a single chord and adds
       *   the entire pattern to that style.  Uses the volume, tempo, and chord info from the toolbar.
       */

    public boolean playMe(double swingVal, int loopCount)
    {
        canPlay();
        
        if(checkStatus()) {
            try{
                String p = this.getPattern();
                Polylist rule = Notate.parseListFromString(p); 
                if(rule.isEmpty()) {
                    cannotPlay();
                    return false;
                }
                Style tempStyle = Style.makeStyle(rule);
                tempStyle.setSwing(swingVal);
                tempStyle.setAccompanimentSwing(swingVal);
               
                String chord = styleParent.getChord();
                ChordPart c = new ChordPart();
                boolean muteChord = true; // preferred not to play styleParent.isChordMuted();
 
                int duration = tempStyle.getDrumPatternDuration();
                c.addChord(chord, duration);
                c.setStyle(tempStyle);
                
                 Score s = new Score(4);
                if(muteChord)s.setChordVolume(0);
                else s.setChordVolume(styleParent.getVolume());
                s.setBassVolume(styleParent.getVolume());
                s.setTempo(styleParent.getTempo());
                s.setVolumes(parent.getMidiSynth());
                s.setChordProg(c);
                
                /* if(styleParent.isLooped()) parent.cm.execute(new PlayScoreCommand(s, 0, true, parent.getMidiSynth(), styleParent.getLoopCount()));
                else */ parent.cm.execute(new PlayScoreCommand(s, 0, true, parent.getMidiSynth(), loopCount, parent.getTransposition()));
            }
            catch(Exception e) {
                cannotPlay();
                return false;                    
           }
        }
        else {
                cannotPlay();
                return false;                    
        }        
        return true;
    }//GEN-LAST:event_playPatternBtnActionPerformed

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
   

    private void includeBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_includeBoxActionPerformed
        //do nothing.  Actually including or excluding from a style is handled in the save sections
        setSelectedAppearance();
        if(includeBox.isSelected()) {
            includeBox.setToolTipText("Click to exclude this pattern from style.");
        }
        else {
            includeBox.setToolTipText("Click to include this pattern from style.");
        }
    }//GEN-LAST:event_includeBoxActionPerformed
  
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JScrollPane centerRulePane;
  private javax.swing.JPanel drumRuleHolder;
  private javax.swing.JCheckBox includeBox;
  private javax.swing.JPanel includePlayPanel;
  private javax.swing.JPanel itemPanel;
  private javax.swing.JLabel lengthTitle;
  private javax.swing.JLabel nameTitle;
  private javax.swing.JPanel northPanel;
  private javax.swing.JButton playPatternBtn;
  private javax.swing.JPanel southPanel;
  private javax.swing.JPanel titlePanel;
  private javax.swing.JLabel weightLabel;
  private javax.swing.JSpinner weightSpinner;
  // End of variables declaration//GEN-END:variables
}
