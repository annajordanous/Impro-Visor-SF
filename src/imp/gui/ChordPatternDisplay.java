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
import imp.data.ChordPart;
import imp.data.MIDIBeast;
import imp.data.Score;
import imp.data.Style;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import polya.Polylist;

/**
 * Note: The GUI part of this is defunct, subsumed in StyleEditor now.
 * Created Summer 2007
 * @authors  Brandy McMenamy, Sayuri Soejima
 */
public class ChordPatternDisplay 
        extends PatternDisplay 
        implements Constants, Playable, Displayable {
  
    public static Color playableColor = Color.green;
    public static Color unplayableColor = Color.red;
    
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

    //The number added to the title of this object to help the user distinguish it from others.
    private int titleNumber = 0;
    
        //The dimension to use when the pane is expanded.  By default, it is the size of the entire panel
    private Dimension expandedDimension;
    //The dimension to use when the pane is collapsed.  By default, it is the size of the northern panel's preferred size.
    private Dimension collapsedDimension;
    
     //True if the pattern information is displayed, false otherwise 
    boolean isExpanded = false;
    
    String pushString = "";
    
   /**
     * Constructs a new ChordPatternDisplay JPanel with default weight 3 and an empty pattern.
     **/
    public ChordPatternDisplay(Notate parent, CommandManager cm, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        initialize(null, 3, "");
    }
    
   /**
     * Constructs a new ChordPatternDisplay JPanel with weight and rule parameters.
     **/   
    public ChordPatternDisplay(String rule, float weight, String pushString, Notate parent, CommandManager cm, StyleEditor styleParent) {
        super(parent, cm, styleParent);
        initialize(rule, weight, pushString);
    }
    
    /**
     * Initializes all elements and components for the BassPatternDisplay GUI and collapses the pane.
     **/
    private void initialize(String rule, float weight, String pushString) {
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
        
        setWeight(weight);
        setDisplayText(rule);
        
        this.pushString = pushString;
        
        //Initializes attributes needed for collapsing panes and collapses the BassPatternDisplay object.
        expandedDimension = this.getPreferredSize();
        collapsedDimension = northPanel.getPreferredSize();
        southPanel.setVisible(false);
     //   this.setPreferredSize(collapsedDimension);
     //   this.setMaximumSize(collapsedDimension);     
    }
    
    //Accessors:
   
    /**
     * @return titleNumber
     **/    
    public int getTitleNumber() {
        return titleNumber;
    }
    
   /**
     * @return the actual text displpayed in the text field
     **/       
    public String getDisplayText() {
        return chordPatternText.getText().trim();
    }
    
    /**
     * This is used for saving the pattern to a file, among possibly other uses.
     * @return the text and weight formatted with bass-pattern syntax used by the style classes 
     **/        
    public String getPattern() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("(chord-pattern (rules " );
        buffer.append(getDisplayText());
        buffer.append(")(weight ");
        buffer.append(getWeight());
        String trimmed = pushString.trim();
        if( !trimmed.equals("") )
          {
            buffer.append(")(push ");
            buffer.append(pushString);
          }
        buffer.append("))");
        return buffer.toString();
    }    
   
    /**
     * @return the most recent error message that occurred.
     **/
    public String getRuleError() {
        return errorMsg;
    }   

    
    public void setPushString(String pushString) {
        this.pushString = pushString;
    }
    
    public String getPushString() {
        return pushString;
    }
    
    /**
     * @return the beats in this pattern.
     **/    
    public double getBeats() {
     double slots = MIDIBeast.numBeatsInBassRule(getDisplayText());
     if( slots < 0 )
       return -1;
     return slots/BEAT; 
     }
    

    /**
     * @return the selected value of the checkbox marked "include" in the upper right corner
     **/
    public boolean getIncludedStatus() {
        return true;
    }
 
    //Mutators:
    
    /**
     * Sets the number in the title to num.
     **/         
    public void setTitleNumber(int num) {

        titleNumber = num;
    }

    /**
     * Sets the text in the text field to the parameter text and updates the user feedback information.
     **/ 
    public void setDisplayText(String text) {
        chordPatternText.setText(text);
        updateElements();
    }
    



   /**
     * Changes the appearance of this ChordPatternDisplay to "deselected"
     **/
    public void setDeselectedAppearance() {

    }
    
   /**
     * Changes the appearance of this ChordPatternDisplay to "selected"
    **/
    public void setSelectedAppearance() {
        styleEditor.setSelectedChord(this);
     
    }
    
    /**
     * Update the length title in the northern pane to reflect changes in text.
     **/    
    public void updateLength() {

    }
   
    /**
     * Update the user feedback items.
     **/
    public void updateElements() {
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
 * Checks the pattern for correctness for the given time signature. Changes
 * icons, tooltips, and errorMsg to appropriate error feedback information.
 *
 * @return true if the pattern is a correctly formed and therefore playable by
 * Impro-Visor. Returns false otherwise.
     *
 */
public boolean checkStatus()
  {
    String displayText = getDisplayText();

    playable = true;

    try
      {
        //Check for null rules.
        if( displayText.equals("") )
          {
            return false;
          }

        Polylist l = Notate.parseListFromString(displayText);
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
                cannotPlay("unknown character in pattern");
                return false;
              }
          }

        if( Style.makeStyle(l) == null )
          {
            cannotPlay("can't make style");
            return false;
          }
        else if( MIDIBeast.numBeatsInRule(displayText) == -1 )
          {
            cannotPlay("can't compute beats");
            return false;
          }
        else
          {
            return true;
          }
      }
    catch( Exception e )
      {
        cannotPlay("exception " + e);
        return false;
      }
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
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        northPanel = new javax.swing.JPanel();
        titlePanel = new javax.swing.JPanel();
        nameTitle = new javax.swing.JLabel();
        lengthTitle = new javax.swing.JLabel();
        southPanel = new javax.swing.JPanel();
        patternPanel = new javax.swing.JPanel();
        ruleLabel = new javax.swing.JLabel();
        chordPatternText = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 214, 177), 1, true));
        setMinimumSize(new java.awt.Dimension(525, 77));
        setPreferredSize(new java.awt.Dimension(525, 77));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        northPanel.setBackground(new java.awt.Color(138, 214, 177));
        northPanel.setToolTipText("Double click to expand pattern information.");
        northPanel.setMinimumSize(new java.awt.Dimension(525, 30));
        northPanel.setPreferredSize(new java.awt.Dimension(525, 30));
        northPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                northPanelMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                northPanelMousePressed(evt);
            }
        });
        northPanel.setLayout(new java.awt.BorderLayout());

        titlePanel.setBackground(new java.awt.Color(138, 214, 177));
        titlePanel.setToolTipText("Double click to expand pattern information.");
        titlePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                titlePanelMouseClicked(evt);
            }
        });

        nameTitle.setBackground(new java.awt.Color(164, 244, 198));
        nameTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        nameTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/goodpattern.png"))); // NOI18N
        nameTitle.setText("Chord Pattern");
        nameTitle.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nameTitle.setIconTextGap(10);
        nameTitle.setMaximumSize(new java.awt.Dimension(118, 14));
        nameTitle.setMinimumSize(new java.awt.Dimension(118, 14));
        nameTitle.setPreferredSize(new java.awt.Dimension(118, 14));
        nameTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                nameTitleMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                nameTitleMousePressed(evt);
            }
        });
        titlePanel.add(nameTitle);

        lengthTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        lengthTitle.setText("Length: unknown");
        lengthTitle.setToolTipText("Double click to expand pattern information.");
        lengthTitle.setMaximumSize(new java.awt.Dimension(115, 14));
        lengthTitle.setMinimumSize(new java.awt.Dimension(115, 14));
        lengthTitle.setPreferredSize(new java.awt.Dimension(115, 14));
        lengthTitle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lengthTitleMouseClicked(evt);
            }
        });
        titlePanel.add(lengthTitle);

        northPanel.add(titlePanel, java.awt.BorderLayout.WEST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(northPanel, gridBagConstraints);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setMinimumSize(new java.awt.Dimension(521, 35));
        southPanel.setPreferredSize(new java.awt.Dimension(521, 35));
        southPanel.setRequestFocusEnabled(false);
        southPanel.setLayout(new java.awt.GridBagLayout());

        patternPanel.setBackground(new java.awt.Color(255, 255, 255));
        patternPanel.setMinimumSize(new java.awt.Dimension(525, 25));
        patternPanel.setPreferredSize(new java.awt.Dimension(525, 25));
        patternPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                patternPanelMousePressed(evt);
            }
        });
        patternPanel.setLayout(new java.awt.BorderLayout());

        ruleLabel.setText(" ");
        ruleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        ruleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                ruleLabelMousePressed(evt);
            }
        });
        patternPanel.add(ruleLabel, java.awt.BorderLayout.WEST);

        chordPatternText.setText("B4 B4 B4 B4");
        chordPatternText.setMinimumSize(new java.awt.Dimension(500, 20));
        chordPatternText.setPreferredSize(new java.awt.Dimension(500, 20));
        chordPatternText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordPatternTextActionPerformed(evt);
            }
        });
        chordPatternText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                chordPatternTextFocusLost(evt);
            }
        });
        chordPatternText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                chordPatternTextMousePressed(evt);
            }
        });
        patternPanel.add(chordPatternText, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        southPanel.add(patternPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(southPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void northPanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_northPanelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_northPanelMouseClicked

    private void chordPatternTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chordPatternTextFocusLost
        updateElements();        
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_chordPatternTextFocusLost

    private void chordPatternTextMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chordPatternTextMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_chordPatternTextMousePressed

    private void ruleLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ruleLabelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_ruleLabelMousePressed

    private void patternPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patternPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_patternPanelMousePressed

    private void northPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_northPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_northPanelMousePressed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed

    private void chordPatternTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordPatternTextActionPerformed
        updateElements();
        cm.changedSinceLastSave(true);        
    }//GEN-LAST:event_chordPatternTextActionPerformed

private void titlePanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_titlePanelMouseClicked
  {//GEN-HEADEREND:event_titlePanelMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_titlePanelMouseClicked

private void lengthTitleMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_lengthTitleMouseClicked
  {//GEN-HEADEREND:event_lengthTitleMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_lengthTitleMouseClicked

private void nameTitleMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_nameTitleMousePressed
  {//GEN-HEADEREND:event_nameTitleMousePressed
    setSelectedAppearance();
  }//GEN-LAST:event_nameTitleMousePressed

private void nameTitleMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_nameTitleMouseClicked
  {//GEN-HEADEREND:event_nameTitleMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_nameTitleMouseClicked

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
                cannotPlay("empty rule");
                return false;
              }
            Style tempStyle = Style.makeStyle(rule);
            tempStyle.setSwing(swingVal);
            tempStyle.setAccompanimentSwing(swingVal);
            tempStyle.setName("chordPattern");
            Style.setStyle("chordPattern", tempStyle);
            // This is necessary so that the StyleListModel menu in notate is reset.
            // Without it, the contents will be emptied.
            notate.reloadStyles();

            ChordPart c = new ChordPart();
            String chord = styleEditor.getChord();
            boolean muteChord = styleEditor.isChordMuted();
            int duration = tempStyle.getCP().get(0).getDuration();
            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            s.setChordProg(c);
            s.setChordVolume(styleEditor.getVolume());
            s.setTempo(tempo);
            s.setVolumes(notate.getMidiSynth());

            new PlayScoreCommand(s, 0, true, notate.getMidiSynth(), loopCount, notate.getTransposition()).execute();
            styleEditor.setStatus("OK");
          }
        catch( Exception e )
          {
            cannotPlay("exception " + e);
            return false;
          }
      }
    else
      {
        cannotPlay("check status failed");
        return false;
      }
    return true;
  }

    /**
     * @return the actual text displpayed in the text field
     **/    
    @Override
    public String toString() {
        return chordPatternText.getText();
    }
      
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField chordPatternText;
    private javax.swing.JLabel lengthTitle;
    private javax.swing.JLabel nameTitle;
    private javax.swing.JPanel northPanel;
    private javax.swing.JPanel patternPanel;
    private javax.swing.JLabel ruleLabel;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
