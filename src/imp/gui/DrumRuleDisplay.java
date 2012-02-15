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

import javax.swing.ImageIcon;
import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import java.awt.Color;
import java.util.StringTokenizer;
import java.util.Vector;
import polya.Polylist;


/**
 * Creates a GUI that displays a drum instrument rule used in styles.
 * Intended to be placed in the drumRuleHolder pane of DrumPatternDisplay
 * Created Summer 2007
 * @authors  Brandy McMenamy, Sayuri Soejima
 */
public class DrumRuleDisplay extends PatternDisplay implements Playable, Displayable {
    
    public static Color playableColor = Color.yellow;
    public static Color unplayableColor = Color.red;

    //The image that is visible when the pane is collapsed and the pattern is legal.
    private static ImageIcon goodPattern;  
    //The image that is visible when the pane is collapsed and the pattern is illegal.
    private static ImageIcon badPattern;
    //The image next to the pattern text if the pattern is legal
    private static ImageIcon goodRule;
    //The image next to the pattern text if the pattern is illegal
    private static ImageIcon badRule;
    
    //The message displayed in tooltips and error dialogs if the rule is legal
    private String safeMsgRule = "This rule is legal.  Click play button to preview it.";
    //Contains a specialized error message for any error that just occurred.
    private String errorMsgRule = safeMsgRule;
    
    //Useful notate containers.
    private DrumPatternDisplay myParentHolder = null;
    
    private boolean playable = true;

    private String instrumentString = "";

    private int instrumentNumber = -1;

    private String ruleText = "";
    
    /**
     * Constructs a new DrumRuleDisplay JPanel with default empty rule and instrument "Acoustic Bass Drum".
     **/
    public DrumRuleDisplay(Notate parent, CommandManager cm, DrumPatternDisplay myParentHolder, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        this.myParentHolder = myParentHolder;
        initialize(null, "Acoustic Bass Drum");      
    }

    /**
     * Constructs a new DrumRuleDisplay JPanel with rule and instrument parameters
     **/    
    public DrumRuleDisplay(String rule, String instrument, Notate parent, CommandManager cm, DrumPatternDisplay myParentHolder, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        this.myParentHolder = myParentHolder;

        //System.out.println("new DrumRuleDisplay " + rule + " " + instrument);
        initialize(rule, instrument);
    }
    
     /**
     * Initializes all elements and components for the DrumRuleDisplay GUI
     **/   
    private void initialize(String rule, String instrument) {
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
        
        setRuleText(rule);
        setInstrument(instrument);
    }

     
    //Accessors:
   
    /**
     * @return the actual text displpayed in the text field
     **/  
    public String getDisplayText() {
        return getRuleText(); // drumRuleText.getText().trim();
    }

    /**
     * @return the text formatted with drum-rule syntax to be included with the overall drum-pattern
     **/
    public String getRule() {

        String rule = "(drum " + getInstrumentNumber() + " " + getDisplayText() + ")";

        //System.out.println("rule = " + rule);
        
        return  rule;
    }

    public String getRuleText()
    {
        return ruleText;
    }

    public void setRuleText(String text)
    {
        //System.out.println("setting rule text to " + text);
        ruleText = text;
        drumRuleText.setText(text);
    }
    
    /**
     * @return the text formatted as if it were the only rule in a drum-pattern.  Used to play the rule.
     **/    
    public String getPlayRule() {
        return "(drum-pattern " + getRule() + "(weight 100))";
    }
    
    /**
     * @return the most recent error message that occurred.
     **/
    public String getErrorMsgRule() {
        return errorMsgRule;
    }    
    
   /**
     * @return the instrument selected in the combo box
     **/
    public String getInstrument() {
        return instrumentString;
        //return (String) drumInstrumentBox.getSelectedItem();
    }
    
   /**
     * @return the number of the instrument
     **/
    public int getInstrumentNumber() {
        return instrumentNumber;
    }

    //Mutators:
    
    /**
     * Sets the displayed text to parameter rule and updates its legality feedback
     **/
    public void setDisplayText(String rule) {
        setRuleText(rule);
        //checkStatus();
    }
    
    /**
     * Sets the selected instrument to parameter instrument if it exists in the combo box
     **/
    public void setInstrument(String instrument) {
        instrumentString = instrument;
        instrumentNumber = MIDIBeast.getDrumInstrumentNumber(instrument);

        //System.out.println("getting instrument number for " + instrument + " = " + instrumentNumber);
        
        if( instrumentNumber < 0 )
        {
            ErrorLog.log(ErrorLog.WARNING, "Instrument has no corresponding number: " + instrument);
        }

        //drumInstrumentBox.setSelectedItem((Object) instrument);
    }
    
    /*
     * Adds every drum instrument specified by MIDIBeast.drumNames to the instrument box.

    private void initInstrumentBox(){
        drumInstrumentBox.removeAllItems();
        for(int i = 0; i < MIDIBeast.drumNames.length; i++){
            drumInstrumentBox.addItem(MIDIBeast.drumNames[i]);
        }      
    }
    */

   /**
     * Changes the appearance of this DrumRuleDisplay to "deselected"
    **/   
    public void setDeselectedAppearance() {
       this.rulePanel.setBackground(new java.awt.Color(255,255,255));
    }
    
    /**
     * Changes the appearance of this DrumRuleDisplay to "selected" and notifies
     * DrumPatternDisplay notate (if it exists) to deselect any other rules in the pattern.
    **/
    private void setSelectedAppearance() {
       this.rulePanel.setBackground(new java.awt.Color(255,220,112));
       
       if(myParentHolder != null) {
            myParentHolder.setSelectedInstrument(this);
            myParentHolder.setSelectedAppearance();
       }
    } 
    
    public boolean playMe(double swingVal)
    {
    return playMe(swingVal, 0);
    }

    public boolean playMe(double swingVal, int loopCount)
    {
        canPlay();
        setSelectedAppearance();
        try{
            if(checkStatus()) {
                String r = this.getPlayRule();
                Polylist rule = Notate.parseListFromString(r); 
                if(rule.isEmpty()) {
                    styleEditor.setStatus("Can't play incorrect pattern");
                    /*
                    ErrorLog.setDialogTitle("Can't Play");
                    ErrorLog.log(ErrorLog.WARNING, "Unable to play the selected rule.");
                    ErrorLog.setDialogTitle("");
                    */
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
                
                Score s = new Score(4);
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
                s.setTempo(styleEditor.getTempo());
                s.setVolumes(notate.getMidiSynth());
                 
                /* if(styleEditor.isLooped()) notate.cm.execute(new PlayScoreCommand(s, 0, true, notate.getMidiSynth(), styleEditor.getLoopCount())); 
                else*/ notate.cm.execute(new PlayScoreCommand(s, 0, true, notate.getMidiSynth(), notate.getTransposition())); 
                styleEditor.setStatus("OK");
            }
            else {
                styleEditor.setStatus("Can't play incorrect pattern");
                /*            
                ErrorLog.setDialogTitle("Can't Play");
                ErrorLog.log(ErrorLog.WARNING, "Unable to play the selected rule because " + errorMsgRule);
                ErrorLog.setDialogTitle("");
                */
                return false; 
            }
 
        }
        catch(Exception e) {
             styleEditor.setStatus("Can't play incorrect pattern");
             /*
             ErrorLog.setDialogTitle("Can't Play");
             ErrorLog.log(ErrorLog.WARNING, "Unable to play the selected rule.");
             ErrorLog.setDialogTitle("");
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
     **/    
    public String toString() {
        return drumRuleText.getText();
    }


    /**
     * Checks the rule for correctness (uses the time signature in MIDIBeast, which is appropriately updated from a DrumPatternDisplay notate).
     * Changes icons, tooltips, and errorMsgRule to appropriate error feedback information.
     * @return true if the rule is a correctly formed and therefore playable by Impro-Visor. Returns false otherwise.
     **/     
    public boolean checkStatus() {
        String displayText = getDisplayText();
        String rule = getRule();
        playable = true;
        
        try {  
            //Check for null rules.
            if(displayText.equals("")){
                String badText = "WARNING: Please enter a rule.";
                drumRuleText.setToolTipText(badText);
                ruleLabel.setIcon(badRule);
                ruleLabel.setToolTipText(badText);
                errorMsgRule = "drum instrument rule " + displayText + " is empty";
                return false;
            }               
            
            Polylist l = Notate.parseListFromString(getPlayRule());            
            StringTokenizer tokenS = new StringTokenizer(displayText, " ");
            Vector<String> tokenizedRule = new Vector<String>(); 
 
            while (tokenS.hasMoreTokens()) {
                tokenizedRule.add(tokenS.nextToken());
            }
            
            //For every element, check for invalid "hit" or "rest" items, and 
            //invalid rhythm durations.  By checking each element, we are able to give clearer feedback about errors         
            for(int i = 0; i < tokenizedRule.size(); i++){                            
                if(!(java.lang.Character.toString(tokenizedRule.get(i).charAt(0)).equals("X")) &&
                   !(java.lang.Character.toString(tokenizedRule.get(i).charAt(0)).equals("R"))){
                        cannotPlay();
                        String badText = "WARNING: The character " + java.lang.Character.toString(tokenizedRule.get(i).charAt(0)) + " is not valid.";
                        drumRuleText.setToolTipText(badText);
                        ruleLabel.setIcon(badRule);
                        ruleLabel.setToolTipText(badText);
                        errorMsgRule = "drum instrument rule " + displayText + " contains an invalid character";
                        return false;
                }
            }
            
            if(Style.makeStyle(l) == null){
                cannotPlay();
                String badText = "WARNING: This rule is syntactically incorrect";
                drumRuleText.setToolTipText(badText);
                ruleLabel.setIcon(badRule);
                ruleLabel.setToolTipText(badText);
                errorMsgRule = "drum instrument rule " + displayText + " is syntactically incorrect";
               return false;
            } 
           else if(MIDIBeast.numBeatsInRule(displayText) == -1){
                cannotPlay();
                String badText = "WARNING: This rule has an unrecognized rhythm duration";
                drumRuleText.setToolTipText(badText);
                ruleLabel.setIcon(badRule);
                ruleLabel.setToolTipText(badText);
                errorMsgRule = "drum instrument rule " + displayText + "rule " + displayText + " has an unrecognized rhythm duration";
                return false;
            }    
            else {
                String goodText = "To preview this rule, press the play button.";
                drumRuleText.setToolTipText(goodText);
                ruleLabel.setIcon(goodRule);
                ruleLabel.setToolTipText(goodText);
                errorMsgRule = safeMsgRule;
                return true;
            }
        }catch(Exception e) {
            cannotPlay();
            String badText = "WARNING: Unknown error...unable to include rule in style.";
            drumRuleText.setToolTipText(badText);
            ruleLabel.setIcon(badRule);
            ruleLabel.setToolTipText(badText);
            errorMsgRule = "drum instrument rule " + displayText + " is syntactically incorrect"; 
            return false;
        }
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

    rulePanel = new javax.swing.JPanel();
    ruleLabel = new javax.swing.JLabel();
    drumInstrumentBox = new javax.swing.JComboBox();
    playRuleBtn = new javax.swing.JButton();
    drumRuleText = new javax.swing.JTextField();

    setMinimumSize(new java.awt.Dimension(495, 30));
    setOpaque(false);
    setPreferredSize(new java.awt.Dimension(495, 30));
    addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        formMousePressed(evt);
      }
    });

    rulePanel.setBackground(new java.awt.Color(255, 255, 255));
    rulePanel.setMinimumSize(new java.awt.Dimension(495, 25));
    rulePanel.setPreferredSize(new java.awt.Dimension(495, 25));
    rulePanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        rulePanelMousePressed(evt);
      }
    });
    rulePanel.setLayout(new java.awt.GridBagLayout());

    ruleLabel.setText(" ");
    ruleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    ruleLabel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        ruleLabelMousePressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    rulePanel.add(ruleLabel, gridBagConstraints);

    drumInstrumentBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Acoustic Bass Drum", "Bass Drum 1", "Side Stick", "Acoustic Snare", "Hand Clap", "Electric Snare", "Low Floor Tom", "Closed Hi-Hat", "High Floor Tom", "Pedal Hi-Hat", "Low Tom", "Open Hi-Hat", "Low-Mid Tom", "Hi-Mid Tom" }));
    drumInstrumentBox.setPreferredSize(new java.awt.Dimension(135, 22));
    drumInstrumentBox.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        drumInstrumentBoxMousePressed(evt);
      }
    });
    drumInstrumentBox.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        drumInstrumentBoxActionPerformed(evt);
      }
    });
    drumInstrumentBox.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusGained(java.awt.event.FocusEvent evt)
      {
        drumInstrumentBoxFocusGained(evt);
      }
    });
    rulePanel.add(drumInstrumentBox, new java.awt.GridBagConstraints());

    playRuleBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/play.png"))); // NOI18N
    playRuleBtn.setToolTipText("Click to play rule.");
    playRuleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    playRuleBtn.setIconTextGap(0);
    playRuleBtn.setMaximumSize(new java.awt.Dimension(20, 20));
    playRuleBtn.setMinimumSize(new java.awt.Dimension(20, 20));
    playRuleBtn.setOpaque(false);
    playRuleBtn.setPreferredSize(new java.awt.Dimension(20, 20));
    playRuleBtn.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        playRuleBtnMousePressed(evt);
      }
    });
    playRuleBtn.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        playRuleBtnActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 0;
    rulePanel.add(playRuleBtn, gridBagConstraints);

    drumRuleText.setMinimumSize(new java.awt.Dimension(300, 20));
    drumRuleText.setPreferredSize(new java.awt.Dimension(300, 20));
    drumRuleText.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        drumRuleTextActionPerformed(evt);
      }
    });
    drumRuleText.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusGained(java.awt.event.FocusEvent evt)
      {
        drumRuleTextFocusGained(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt)
      {
        drumRuleTextFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    rulePanel.add(drumRuleText, gridBagConstraints);

    add(rulePanel);
  }// </editor-fold>//GEN-END:initComponents

    private void drumRuleTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drumRuleTextFocusGained
        setSelectedAppearance();
    }//GEN-LAST:event_drumRuleTextFocusGained

    private void drumInstrumentBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drumInstrumentBoxFocusGained
        setSelectedAppearance();
    }//GEN-LAST:event_drumInstrumentBoxFocusGained

    private void drumRuleTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drumRuleTextActionPerformed
        setSelectedAppearance();
        checkStatus();
        if(myParentHolder != null)
            myParentHolder.checkStatus();
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_drumRuleTextActionPerformed

    private void drumRuleTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_drumRuleTextFocusLost
        checkStatus();
        if(myParentHolder != null)
            myParentHolder.checkStatus();
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_drumRuleTextFocusLost

    private void playRuleBtnMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_playRuleBtnMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_playRuleBtnMousePressed

    private void drumInstrumentBoxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drumInstrumentBoxMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_drumInstrumentBoxMousePressed

    private void ruleLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ruleLabelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_ruleLabelMousePressed

    private void rulePanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rulePanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_rulePanelMousePressed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed

    private void drumInstrumentBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drumInstrumentBoxActionPerformed
        setSelectedAppearance();
        cm.changedSinceLastSave(true);        
    }//GEN-LAST:event_drumInstrumentBoxActionPerformed

    // To satisfy interface for now: FIX
    public double getBeats()
    {
      return 0;
    }
    
 // To satisfy interface for now: FIX
    public int getWeight()
    {
      return 0;
    }
    
    private void playRuleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playRuleBtnActionPerformed
       playMe();

    }//GEN-LAST:event_playRuleBtnActionPerformed
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JComboBox drumInstrumentBox;
  private javax.swing.JTextField drumRuleText;
  private javax.swing.JButton playRuleBtn;
  private javax.swing.JLabel ruleLabel;
  private javax.swing.JPanel rulePanel;
  // End of variables declaration//GEN-END:variables
   
}
