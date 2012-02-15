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
       
    String bassPatternText = "";
    
    /**
     * Constructs a new BassPatternDisplay JPanel with default weight 3 and an empty pattern.
     **/
    public BassPatternDisplay(Notate notate, CommandManager cm, StyleEditor styleEditor) {
      super(notate, cm, styleEditor);
        initialize("", 3);
    }
    
    /**
     * Constructs a new BassPatternDisplay JPanel with weight and rule parameters.
     **/
    public BassPatternDisplay(String rule, float weight, Notate notate, CommandManager cm, StyleEditor styleEditor) {
      super(notate, cm, styleEditor);
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
    }
    
    //Accessors:
    
    /**
     * @return the actual text displpayed in the text field
     **/    
    public String getDisplayText() {
        return bassPatternText.trim();
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
        return true;  // FIX
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
        bassPatternText = text;
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
        styleEditor.setSelectedBass(this);  
    }

   /**
     * Update the length title in the northern pane to reflect changes in text.
     **/    
    public void updateLength() {
       double duration = MIDIBeast.numBeatsInBassRule(getDisplayText()); 
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
        includePlayPanel = new javax.swing.JPanel();
        playPatternBtn = new javax.swing.JButton();

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
            public void mousePressed(java.awt.event.MouseEvent evt) {
                northPanelMousePressed(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                northPanelMouseClicked(evt);
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

        northPanel.add(includePlayPanel, java.awt.BorderLayout.EAST);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        add(northPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void formMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_formMousePressed
    
    /**
     * @return the actual text displpayed in the text field
     **/    
    public String toString() {
        return bassPatternText.trim();
    }
 

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
            notate.reloadStyles();

            String chord = styleEditor.getChord();
            ChordPart c = new ChordPart();
            boolean muteChord = styleEditor.isChordMuted();
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
                s.setChordVolume(styleEditor.getVolume());
              }
            s.setBassVolume(styleEditor.getVolume());
            s.setTempo(styleEditor.getTempo());

            MidiSynth synth = notate.getMidiSynth();

            s.setVolumes(synth);

            //System.out.println("c = " + c);
            //System.out.println("s = " + s);
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
  }//GEN-LAST:event_playPatternBtnActionPerformed

private void titlePanelMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_titlePanelMouseClicked
  {//GEN-HEADEREND:event_titlePanelMouseClicked
    setSelectedAppearance();
    if( evt.getClickCount() == 2 )
      {
        expand();
      }
  }//GEN-LAST:event_titlePanelMouseClicked
       
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel includePlayPanel;
    private javax.swing.JPanel northPanel;
    private javax.swing.JButton playPatternBtn;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables
}
