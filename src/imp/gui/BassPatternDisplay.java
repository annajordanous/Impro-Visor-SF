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

import imp.Constants;
import imp.com.CommandManager;
import imp.com.PlayScoreCommand;
import imp.data.*;
import java.awt.Color;
import java.awt.Dimension;
import polya.Polylist;
   
/**
 * Creates a GUI that displays a bass pattern used in styles.
 * Created Summer 2007
 * @authors  Brandy McMenamy, Robert Keller
 */
public class BassPatternDisplay extends PatternDisplay 
        implements Constants, Playable, Displayable {
   
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
    private int weight = INITIAL_WEIGHT;
    
    //The number added to the title of this object to help the user distinguish it from others.
    private int titleNumber = 0; 

    //The dimension to use when the pane is expanded.  By default, it is the size of the entire panel
    private Dimension expandedDimension;
    
    //The dimension to use when the pane is collapsed.  By default, it is the size of the northern panel's preferred size.
    private Dimension collapsedDimension;
    
    //True if the pattern information is displayed, false otherwise
    boolean isExpanded = false;   
       
    /**
     * Constructs a new BassPatternDisplay JPanel with default weight 3 and an empty pattern.
     **/
    public BassPatternDisplay(Notate parent, CommandManager cm, StyleEditor styleEditor) {
      super(parent, cm, styleEditor);
        initialize(null, 3);
    }
    
    /**
     * Constructs a new BassPatternDisplay JPanel with weight and rule parameters.
     **/
    public BassPatternDisplay(String rule, float weight, Notate parent, CommandManager cm, StyleEditor styleParent) {
      super(parent, cm, styleParent);
        initialize(rule, weight);
    }

    /**
     * Initializes all elements and components for the BassPatternDisplay GUI and collapses the pane.
     **/
    private void initialize(String rule, float weight) {
        /*Ensures that useful items like rhythm durations for notes are ready for use even
          if the user has not yet generated a style from midi*/
        if(!MIDIBeast.invoked) {
            MIDIBeast.invoke();
        }
       
        initComponents();
        initWeight();
        
        setWeight(weight);
        setDisplayText(rule);
        
        //Initializes attributes needed for collapsing panes and collapses the BassPatternDisplay object.
        expandedDimension = this.getPreferredSize();
        collapsedDimension = northPanel.getPreferredSize();
        southPanel.setVisible(false);   
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
        return bassPatternText.getText().trim();
    }
 
    /**
     * @return the text and weight formatted with bass-pattern syntax used by the style classes 
     **/    
    public String getPattern() {
        return "(bass-pattern (rules " + getDisplayText() + ")(weight " + getWeight() + "))";
    }
    
    /**
     * @return the most recent error message that occurred.
     **/
    public String getRuleError() {
        return errorMsg;
    }        
    
    /**
     * @return the value found in the weight spinner or lowestWeight if the value is not an Integer.
     **/
    public int getWeight() {
        return weight;
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
        return includeBox.isSelected();
    }
 
    //Mutators:
    
    /**
     * Sets the number in the title to num.
     **/ 
    public void setTitleNumber(int num) {
        nameTitle.setText("Bass Pattern " + num + ":");        
        titleNumber = num;
    }
    
    /**
     * Sets the text in the text field to the parameter text and updates the user feedback information.
     **/ 
    public void setDisplayText(String text) {
        bassPatternText.setText(text);
        updateElements();
    }

    /**
     * Sets the weight in the spinner to the parameter weight if it is within the range of lowestWeight to heighestWeight.
     **/    
    public void setWeight(float _weight) {
        if( _weight < lowestWeight ) {
            weight = (int) lowestWeight;
        }
        else if( _weight > highestWeight) {
            weight = (int) highestWeight;
        }
        else
            weight = (int)_weight;
    }

    /**
     * Creates the SpinnerModel used for the weight field.
     **/
    private void initWeight() {
        weight = INITIAL_WEIGHT;
    }

    /**
     * Changes the appearance of this BassPatternDisplay to "deselected"
     **/
    public void setDeselectedAppearance() {

    }
 
   /**
     * Changes the appearance of this BassPatternDisplay to "selected"
    **/
    public void setSelectedAppearance() {
        styleParent.setSelectedBass(this);  
    }

   /**
     * Update the length title in the northern pane to reflect changes in text.
     **/    
    public void updateLength() {
       double duration = MIDIBeast.numBeatsInBassRule(getDisplayText()); 
       if(duration == -1)
            lengthTitle.setText("Unknown length");
       else
            lengthTitle.setText(((double) duration/MIDIBeast.beat) + " beats"); 
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
     * Checks the pattern for correctness for the given time signature.
     * Changes icons, tooltips, and errorMsg to appropriate error feedback information.
     * @return true if the pattern is a correctly formed and therefore playable by Impro-Visor. Returns false otherwise.
     **/ 
    public boolean checkStatus() {       
        String displayText = getDisplayText();
	String rule = getPattern();      

    return true;
    }
    
    /**
     * Collapses and expands the pattern information pane.
     **/
    private void expand() {

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
        includePlayPanel = new javax.swing.JPanel();
        playPatternBtn = new javax.swing.JButton();
        includeBox = new javax.swing.JCheckBox();
        southPanel = new javax.swing.JPanel();
        patternPanel = new javax.swing.JPanel();
        ruleLabel = new javax.swing.JLabel();
        bassPatternText = new javax.swing.JTextField();

        setBackground(new java.awt.Color(255, 255, 255));
        setBorder(new javax.swing.border.LineBorder(new java.awt.Color(170, 196, 255), 1, true));
        setMinimumSize(new java.awt.Dimension(525, 77));
        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(527, 77));
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                formMousePressed(evt);
            }
        });
        setLayout(new java.awt.GridBagLayout());

        northPanel.setBackground(new java.awt.Color(170, 196, 255));
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

        titlePanel.setBackground(new java.awt.Color(170, 196, 255));
        titlePanel.setToolTipText("Double click to expand pattern information.");
        titlePanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                titlePanelMouseClicked(evt);
            }
        });
        titlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        nameTitle.setBackground(new java.awt.Color(170, 196, 255));
        nameTitle.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        nameTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/goodpattern.png"))); // NOI18N
        nameTitle.setText("Bass Pattern");
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

        lengthTitle.setBackground(new java.awt.Color(170, 196, 255));
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

        includePlayPanel.setBackground(new java.awt.Color(170, 196, 255));
        includePlayPanel.setToolTipText("Double click to expand pattern information.");
        includePlayPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                includePlayPanelMouseClicked(evt);
            }
        });

        playPatternBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/play.png"))); // NOI18N
        playPatternBtn.setToolTipText("Click to play pattern.");
        playPatternBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playPatternBtn.setIconTextGap(0);
        playPatternBtn.setMaximumSize(new java.awt.Dimension(20, 20));
        playPatternBtn.setMinimumSize(new java.awt.Dimension(20, 20));
        playPatternBtn.setPreferredSize(new java.awt.Dimension(20, 20));
        playPatternBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnActionPerformed(evt);
            }
        });
        includePlayPanel.add(playPatternBtn);

        includeBox.setBackground(new java.awt.Color(170, 196, 255));
        includeBox.setSelected(true);
        includeBox.setText("include");
        includeBox.setToolTipText("Click to exclude this pattern from style.");
        includeBox.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        includeBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        includeBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                includeBoxMouseClicked(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                includeBoxMousePressed(evt);
            }
        });
        includeBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                includeBoxActionPerformed(evt);
            }
        });
        includePlayPanel.add(includeBox);

        northPanel.add(includePlayPanel, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(northPanel, gridBagConstraints);

        southPanel.setBackground(new java.awt.Color(255, 255, 255));
        southPanel.setMinimumSize(new java.awt.Dimension(525, 35));
        southPanel.setPreferredSize(new java.awt.Dimension(525, 35));
        southPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                southPanelMousePressed(evt);
            }
        });
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

        bassPatternText.setMinimumSize(new java.awt.Dimension(500, 20));
        bassPatternText.setPreferredSize(new java.awt.Dimension(500, 20));
        bassPatternText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                bassPatternTextMousePressed(evt);
            }
        });
        bassPatternText.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassPatternTextActionPerformed(evt);
            }
        });
        bassPatternText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                bassPatternTextFocusLost(evt);
            }
        });
        patternPanel.add(bassPatternText, java.awt.BorderLayout.EAST);

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

    private void ruleLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ruleLabelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_ruleLabelMousePressed

    private void patternPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patternPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_patternPanelMousePressed

    private void southPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_southPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_southPanelMousePressed

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed

    private void bassPatternTextMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_bassPatternTextMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_bassPatternTextMousePressed

    private void bassPatternTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bassPatternTextFocusLost
        updateElements();
        cm.changedSinceLastSave(true);        
    }//GEN-LAST:event_bassPatternTextFocusLost
    
    /**
     * @return the actual text displpayed in the text field
     **/    
    public String toString() {
        return bassPatternText.getText();
    }
 

    private void bassPatternTextActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassPatternTextActionPerformed
        updateElements();
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassPatternTextActionPerformed

private void northPanelMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_northPanelMousePressed
  {//GEN-HEADEREND:event_northPanelMousePressed
    setSelectedAppearance();
  }//GEN-LAST:event_northPanelMousePressed

private void northPanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_northPanelMouseClicked
  {//GEN-HEADEREND:event_northPanelMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_northPanelMouseClicked

private void includePlayPanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_includePlayPanelMouseClicked
  {//GEN-HEADEREND:event_includePlayPanelMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_includePlayPanelMouseClicked

private void includeBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_includeBoxActionPerformed
  {//GEN-HEADEREND:event_includeBoxActionPerformed
    //do nothing.  Actually including or excluding from a style is handled in the save sections
    setSelectedAppearance();
    if( includeBox.isSelected() )
      {
        includeBox.setToolTipText("Click to exclude this pattern from style.");
      }
    else
      {
        includeBox.setToolTipText("Click to include this pattern from style.");
      }
  }//GEN-LAST:event_includeBoxActionPerformed

private void includeBoxMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_includeBoxMousePressed
  {//GEN-HEADEREND:event_includeBoxMousePressed
    setSelectedAppearance();
  }//GEN-LAST:event_includeBoxMousePressed

private void includeBoxMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_includeBoxMouseClicked
  {//GEN-HEADEREND:event_includeBoxMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_includeBoxMouseClicked

private void playPatternBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playPatternBtnActionPerformed
  {//GEN-HEADEREND:event_playPatternBtnActionPerformed
    playMe();
  }

public boolean playMe(double swingVal)
  {
    return playMe(swingVal, 0);
  }

/**
 * If the pattern is legal, creates a style with one chordPart consisting of a
 * single chord and adds the entire pattern to that style. Uses the volume,
 * tempo, and chord info from the toolbar.
 */
public boolean playMe(double swingVal, int loopCount)
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
            parent.reloadStyles();

            String chord = styleParent.getChord();
            ChordPart c = new ChordPart();
            boolean muteChord = styleParent.isChordMuted();
            int duration = tempStyle.getBP().get(0).getDuration();

            c.addChord(chord, duration);
            c.setStyle(tempStyle);

            Score s = new Score(4); // Why 4??
            s.setChordProg(c);

            if( muteChord )
              {
                s.setChordVolume(0);
              }
            else
              {
                s.setChordVolume(styleParent.getVolume());
              }
            s.setBassVolume(styleParent.getVolume());
            s.setTempo(styleParent.getTempo());

            MidiSynth synth = parent.getMidiSynth();

            s.setVolumes(synth);

            //System.out.println("c = " + c);
            //System.out.println("s = " + s);
            parent.cm.execute(new PlayScoreCommand(s, 0, true, synth, parent.getTransposition())); // REVISIT!
            styleParent.setStatus("OK");

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
  }//GEN-LAST:event_playPatternBtnActionPerformed

private void titlePanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_titlePanelMouseClicked
  {//GEN-HEADEREND:event_titlePanelMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_titlePanelMouseClicked

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

private void lengthTitleMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_lengthTitleMouseClicked
  {//GEN-HEADEREND:event_lengthTitleMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_lengthTitleMouseClicked
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bassPatternText;
    private javax.swing.JCheckBox includeBox;
    private javax.swing.JPanel includePlayPanel;
    private javax.swing.JLabel lengthTitle;
    private javax.swing.JLabel nameTitle;
    private javax.swing.JPanel northPanel;
    private javax.swing.JPanel patternPanel;
    private javax.swing.JButton playPatternBtn;
    private javax.swing.JLabel ruleLabel;
    private javax.swing.JPanel southPanel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
