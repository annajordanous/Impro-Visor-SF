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
import java.util.ArrayList;
import java.util.StringTokenizer;
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
    
    private String patternText;
    
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
        
        initComponents();
        
        setWeight(weight);
        setDisplayText(rule);
        
        this.pushString = pushString;   
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
        return patternText.trim();
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
     double slots = getChordPattern().getDuration();
     return slots/BEAT; 
     }
    
    public ChordPattern getChordPattern()
      {
        Polylist list = (Polylist)Polylist.PolylistFromString(getPattern());
        ChordPattern chordPattern = ChordPattern.makeChordPattern(list.rest());
//System.out.println("pattern = " + getPattern() + ", list = " + list +", ChordPattern = " + chordPattern);
        return chordPattern;
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
        patternText = text;
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

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
    }// </editor-fold>                        

    private void formMousePressed(java.awt.event.MouseEvent evt) {                                  
        setSelectedAppearance();
    }                                 

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
        return patternText;
    }
           
}
