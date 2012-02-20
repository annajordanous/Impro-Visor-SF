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
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import polya.Polylist;

/**
 * Creates a GUI that displays a chord pattern used in styles.
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
    
     //True if the pattern information is displayed, false otherwise 
    boolean isExpanded = false;
    
    boolean playable = false;
    
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
        initWeight();
        
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
        buffer.append((Integer) weightSpinner.getValue());
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
        return includeBox.isSelected();
    }
 
    //Mutators:
    
    /**
     * Sets the number in the title to num.
     **/         
    public void setTitleNumber(int num) {
        nameTitle.setText("Chord Pattern " + num + ":");
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
     * Creates the SpinnerModel used for the weight field.
     **/
    private void initWeight() {
        SpinnerModel model = new SpinnerNumberModel(lowestWeight, lowestWeight, highestWeight, 1);
        weightSpinner.setModel(model);
    }

   /**
     * Changes the appearance of this ChordPatternDisplay to "deselected"
     **/
    public void setDeselectedAppearance() {
        this.setBackground(new java.awt.Color(255, 255, 255));
        this.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(91,181,137), 1, true));
        northPanel.setBackground(new java.awt.Color(138,214,177));
        includeBox.setBackground(new java.awt.Color(138,214,177));
        nameTitle.setBackground(new java.awt.Color(138,214,177));
        lengthTitle.setBackground(new java.awt.Color(138,214,177));
        includePlayPanel.setBackground(new java.awt.Color(138,214,177));
        titlePanel.setBackground(new java.awt.Color(138,214,177));
    }
    
   /**
     * Changes the appearance of this ChordPatternDisplay to "selected"
    **/
    public void setSelectedAppearance() {
        styleEditor.setSelectedChord(this);
        this.northPanel.setBackground(new java.awt.Color(72,164,120));
        northPanel.setBackground(new java.awt.Color(72,164,120));
        includeBox.setBackground(new java.awt.Color(72,164,120));
        nameTitle.setBackground(new java.awt.Color(72,164,120));
        lengthTitle.setBackground(new java.awt.Color(72,164,120));
        includePlayPanel.setBackground(new java.awt.Color(72,164,120));
        titlePanel.setBackground(new java.awt.Color(72,164,120));
        this.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, 
                new java.awt.Color(164, 244, 198), new java.awt.Color(164, 244, 198), new java.awt.Color(2, 83, 42), 
                new java.awt.Color(2, 83, 42)));         
    }
    
    /**
     * Update the length title in the northern pane to reflect changes in text.
     **/    
    public void updateLength() {
       int duration = (int) MIDIBeast.numBeatsInRule(getDisplayText());
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
        
	try{
            //Check for null rules.
            if(displayText.equals("")){
                String badText = "WARNING: Please enter a pattern.";
                chordPatternText.setToolTipText(badText);
                ruleLabel.setIcon(badRule);
                ruleLabel.setToolTipText(badText);
                nameTitle.setIcon(badPattern);
                nameTitle.setToolTipText(unsafeMsgPattern);
                errorMsg = "Chord pattern " + this.getTitleNumber() + " is empty";
                return false;
            }                
            
            StringTokenizer st = new StringTokenizer(displayText, " ");
            Vector<String> tokenizedRule = new Vector<String>();
            while (st.hasMoreTokens()) {
                tokenizedRule.add(st.nextToken());
            }
            
            //For every element, check for invalid pitch items, incorrect formatting of X-notations, and 
            //invalid rhythm durations.  By checking each element, we are able to give clearer feedback about errors
            for(int i = 0; i < tokenizedRule.size(); i++){                            
                if(!(java.lang.Character.toString(tokenizedRule.get(i).charAt(0)).equals("X")) && !(java.lang.Character.toString(tokenizedRule.get(i).charAt(0)).equals("R"))){
                        String badText = "WARNING: The character " + java.lang.Character.toString(tokenizedRule.get(i).charAt(0)) + " is not valid.";
                        chordPatternText.setToolTipText(badText);
                        ruleLabel.setIcon(badRule);
                        ruleLabel.setToolTipText(badText);
                        nameTitle.setIcon(badPattern);
                        nameTitle.setToolTipText(unsafeMsgPattern);
                        errorMsg = "Chord pattern " + displayText + " contains an invalid character";
                        return false;
                }
                try{
                    Integer.parseInt(java.lang.Character.toString(tokenizedRule.get(i).charAt(1)));
                    if(MIDIBeast.numBeatsInRule(displayText) == -1){
                        String badText = "WARNING: This rule contains an unrecognized rhythm duration";
                        chordPatternText.setToolTipText(badText);
                        ruleLabel.setIcon(badRule);
                        ruleLabel.setToolTipText(badText);
                        nameTitle.setIcon(badPattern);
                        nameTitle.setToolTipText(unsafeMsgPattern);
                        errorMsg = "Chord pattern " + displayText + " has an unrecognized rhythm duration";
                        return false;                                                
                    }
                }catch(NumberFormatException e){
                    String badText = "WARNING: Expected a rhythm value.  Found the character " + java.lang.Character.toString(tokenizedRule.get(i).charAt(1)) + " instead!";
                    chordPatternText.setToolTipText(badText);
                    ruleLabel.setIcon(badRule);
                    ruleLabel.setToolTipText(badText);
                    nameTitle.setIcon(badPattern);
                    nameTitle.setToolTipText(unsafeMsgPattern);
                    errorMsg = "Chord pattern " + displayText + " is syntactically incorrect";
                    return false;
                }                                  
           }                     
            if(Style.makeStyle(Notate.parseListFromString(rule)) == null){
                String badText = "WARNING: This rule is syntactically incorrect.";
                chordPatternText.setToolTipText(badText);
                ruleLabel.setIcon(badRule);
                ruleLabel.setToolTipText(badText);
                nameTitle.setIcon(badPattern);
                nameTitle.setToolTipText(unsafeMsgPattern);
                errorMsg = "Chord pattern " + displayText + " is syntactically incorrect";
                return false;
            }
            else {
                String goodText = "To preview this pattern, press the play button.";
                chordPatternText.setToolTipText(goodText);
                ruleLabel.setIcon(goodRule);
                ruleLabel.setToolTipText(goodText);
                nameTitle.setIcon(goodPattern);
                nameTitle.setToolTipText(safeMsgPattern);  
                errorMsg = safeMsgRule;
                return true;
            }
	}catch(Exception e) {
            String badText = "WARNING: Unknown error...unable to include rule in style.";
            chordPatternText.setToolTipText(badText);
            ruleLabel.setIcon(badRule);
            ruleLabel.setToolTipText(badText);
            nameTitle.setIcon(badPattern);
            nameTitle.setToolTipText(unsafeMsgPattern);
            errorMsg = "Chord pattern " + displayText + " is syntactically incorrect";
            return false;
	}
    }
    
    /**
    * Collapses and expands the pattern information pane.
    **/   
    private void expand() {
        if(isExpanded == false) {
            southPanel.setVisible(true);
       //     this.setPreferredSize(expandedDimension);
       //     this.setMaximumSize(expandedDimension);
            
            northPanel.setToolTipText("Double click to collapse pattern information.");
            titlePanel.setToolTipText("Double click to collapse pattern information.");
            lengthTitle.setToolTipText("Double click to collapse pattern information.");
            includePlayPanel.setToolTipText("Double click to collapse pattern information.");
            isExpanded = true;
            
        } else {
            southPanel.setVisible(false);
      //      this.setPreferredSize(collapsedDimension);
      //      this.setMaximumSize(collapsedDimension);

            northPanel.setToolTipText("Double click to expand pattern information.");
            titlePanel.setToolTipText("Double click to expand pattern information.");
            lengthTitle.setToolTipText("Double click to expand pattern information.");
            includePlayPanel.setToolTipText("Double click to expand pattern information.");

            isExpanded = false;
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
    patternPanel = new javax.swing.JPanel();
    ruleLabel = new javax.swing.JLabel();
    chordPatternText = new javax.swing.JTextField();

    setBackground(new java.awt.Color(255, 255, 255));
    setBorder(new javax.swing.border.LineBorder(new java.awt.Color(138, 214, 177), 1, true));
    setMinimumSize(new java.awt.Dimension(525, 77));
    setPreferredSize(new java.awt.Dimension(525, 77));
    addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        formMousePressed(evt);
      }
    });
    setLayout(new java.awt.GridBagLayout());

    northPanel.setBackground(new java.awt.Color(138, 214, 177));
    northPanel.setToolTipText("Double click to expand pattern information.");
    northPanel.setMinimumSize(new java.awt.Dimension(525, 30));
    northPanel.setPreferredSize(new java.awt.Dimension(525, 30));
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

    titlePanel.setBackground(new java.awt.Color(138, 214, 177));
    titlePanel.setToolTipText("Double click to expand pattern information.");
    titlePanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mouseClicked(java.awt.event.MouseEvent evt)
      {
        titlePanelMouseClicked(evt);
      }
    });

    nameTitle.setBackground(new java.awt.Color(164, 244, 198));
    nameTitle.setFont(new java.awt.Font("Tahoma", 1, 11));
    nameTitle.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/goodpattern.png"))); // NOI18N
    nameTitle.setText("Chord Pattern");
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

    includePlayPanel.setBackground(new java.awt.Color(138, 214, 177));
    includePlayPanel.setToolTipText("Double click to expand pattern information.");

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

    includeBox.setBackground(new java.awt.Color(138, 214, 177));
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
    itemPanel.setMinimumSize(new java.awt.Dimension(521, 33));
    itemPanel.setOpaque(false);
    itemPanel.setPreferredSize(new java.awt.Dimension(521, 33));
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
    southPanel.setMinimumSize(new java.awt.Dimension(521, 35));
    southPanel.setPreferredSize(new java.awt.Dimension(521, 35));
    southPanel.setRequestFocusEnabled(false);
    southPanel.setLayout(new java.awt.GridBagLayout());

    patternPanel.setBackground(new java.awt.Color(255, 255, 255));
    patternPanel.setMinimumSize(new java.awt.Dimension(525, 25));
    patternPanel.setPreferredSize(new java.awt.Dimension(525, 25));
    patternPanel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        patternPanelMousePressed(evt);
      }
    });
    patternPanel.setLayout(new java.awt.BorderLayout());

    ruleLabel.setText(" ");
    ruleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
    ruleLabel.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
        ruleLabelMousePressed(evt);
      }
    });
    patternPanel.add(ruleLabel, java.awt.BorderLayout.WEST);

    chordPatternText.setText("B4 B4 B4 B4");
    chordPatternText.setMinimumSize(new java.awt.Dimension(500, 20));
    chordPatternText.setPreferredSize(new java.awt.Dimension(500, 20));
    chordPatternText.addActionListener(new java.awt.event.ActionListener()
    {
      public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        chordPatternTextActionPerformed(evt);
      }
    });
    chordPatternText.addFocusListener(new java.awt.event.FocusAdapter()
    {
      public void focusLost(java.awt.event.FocusEvent evt)
      {
        chordPatternTextFocusLost(evt);
      }
    });
    chordPatternText.addMouseListener(new java.awt.event.MouseAdapter()
    {
      public void mousePressed(java.awt.event.MouseEvent evt)
      {
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

    private void includeBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_includeBoxMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_includeBoxMouseClicked

    private void lengthTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lengthTitleMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_lengthTitleMouseClicked

    private void nameTitleMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameTitleMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_nameTitleMouseClicked

    private void titlePanelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_titlePanelMouseClicked
        setSelectedAppearance();
        if(evt.getClickCount() == 2) {
            expand();
        }
    }//GEN-LAST:event_titlePanelMouseClicked

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

    private void weightSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_weightSpinnerStateChanged
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_weightSpinnerStateChanged

    private void chordPatternTextMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_chordPatternTextMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_chordPatternTextMousePressed

    private void ruleLabelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ruleLabelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_ruleLabelMousePressed

    private void patternPanelMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_patternPanelMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_patternPanelMousePressed

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

    private void includeBoxMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_includeBoxMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_includeBoxMousePressed

    private void nameTitleMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_nameTitleMousePressed
        setSelectedAppearance();
    }//GEN-LAST:event_nameTitleMousePressed

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

    private void playPatternBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnActionPerformed
       /* If the pattern is legal, creates a style with one chordPart consisting of a single chord and adds
          the pattern to that style.  Uses the volume, tempo, and chord info from the toolbar. */
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
                String r = this.getPattern();
                Polylist rule = Notate.parseListFromString(r); 
                if(rule.isEmpty()) {
                    cannotPlay();
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
                
                Score s = new Score(4);
                s.setChordProg(c);
                s.setChordVolume(styleEditor.getVolume());
                s.setTempo(styleEditor.getTempo());
                s.setVolumes(notate.getMidiSynth());
                
                new PlayScoreCommand(s, 0, true, notate.getMidiSynth(), loopCount, notate.getTransposition()).execute();
                styleEditor.setStatus("OK");
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

    /**
     * @return the actual text displpayed in the text field
     **/    
    public String toString() {
        return chordPatternText.getText();
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
  private javax.swing.JTextField chordPatternText;
  private javax.swing.JCheckBox includeBox;
  private javax.swing.JPanel includePlayPanel;
  private javax.swing.JPanel itemPanel;
  private javax.swing.JLabel lengthTitle;
  private javax.swing.JLabel nameTitle;
  private javax.swing.JPanel northPanel;
  private javax.swing.JPanel patternPanel;
  private javax.swing.JButton playPatternBtn;
  private javax.swing.JLabel ruleLabel;
  private javax.swing.JPanel southPanel;
  private javax.swing.JPanel titlePanel;
  private javax.swing.JLabel weightLabel;
  private javax.swing.JSpinner weightSpinner;
  // End of variables declaration//GEN-END:variables
}
