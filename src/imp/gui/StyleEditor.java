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
import imp.ImproVisor;
import imp.com.CommandManager;
import imp.com.OpenLeadsheetCommand;
import imp.data.*;
import imp.util.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.table.TableColumnModel;
import polya.Polylist;
import polya.Tokenizer;

/**
 * A spreadsheet GUI for editing Impro-Visor styles.
 * Note that some of the structures used herein are legacy from 
 * the original design by Brandy McMenamy. There are more graphic
 * object than need be, and these should be replaced with non-graphic
 * counterparts as time permits. Bob Keller did the conversion to
 * the spreadsheet form, using JTable.
 * @author Robert Keller, Jim Herold, Brandy McMenamy, Sayuri Soejima  
 */

public class StyleEditor
        extends javax.swing.JFrame
        implements ActionListener
  {
  static public String EMPTY = "";
  
  /** On-color for play/mute buttons */
  static public Color ON_COLOR = Color.GREEN;
  
  /** Off-color for play/mute buttons */
  static public Color OFF_COLOR = Color.RED;
  
  static public final String HIT_STRING = "X";
  
  static public final String REST_STRING = "R";

  static public final String VOLUME_STRING = "V";

  static public final String BASS_STRING = "B";

  int nextPattern = 0;
  
  static int NOTE_COMBO_ITEMS_TO_DISPLAY = 12;

  static int CHORD_ITEMS_TO_DISPLAY = 30;

  static int STYLE_TABLE_ROW_HEIGHT = 20;

  // Specified column widths
  static int defaultColumnWidth = 200;

  static int INCLUDE_COLUMN_WIDTH = 80;

  static String NO_CHANGE = "No change";
  
  static private final String MIDDLE_OCTAVE = "*";

  Object lastRuleClicked = null;

  ListSelectionModel columnSelectionModel;

  TableColumnModel columnModel;
  
  String styleName = "New Style";

  private int selectedColumn = 0;

  static public final boolean PLAY = true;

  static public final boolean SILENT = false;


   /**
   * Standard sub-directory for styles
   */
  File styleDir;       // set within constructor

  /**
   * Standard sub-directory for importing styles 
  from combination of midi and leadsheet
   */
  File styleExtractDir; // set within constructor

  private Notate notate;

  private CommandManager cm;

  //FileChoosers and their attributes for loading and saving styles
  private String styleExt = ".sty";

  private File savedStyle = null;

  private JFileChooser saveStyle = new JFileChooser();

  private JFileChooser openStyle = new JFileChooser();

  private JFileChooser chordFileChooser = new JFileChooser();

  private JFileChooser midiFileChooser = new JFileChooser();

  //Stores user-selected and -copied pattern objects.
  private BassPatternDisplay curSelectedBass = null;

  private BassPatternDisplay lastSelectedBass = null;

  private BassPatternDisplay copiedBass = null;

  private DrumPatternDisplay curSelectedDrum = null;

  private DrumPatternDisplay lastSelectedDrum = null;

  private DrumPatternDisplay copiedDrum = null;

  private DrumRuleDisplay copiedInstrument = null;

  private ChordPatternDisplay curSelectedChord = null;

  private ChordPatternDisplay lastSelectedChord = null;

  private ChordPatternDisplay copiedChord = null;

  //Octave and pitch values used by the attributes tab to check for legality of user choices
  private ArrayList<String> attrOctaves = new ArrayList<String>();

  private ArrayList<String> attrPitches = new ArrayList<String>();

  private double defaultSwing = 0.5;

  private double defaultAccompanimentSwing = 0.5;

  /* Number of recent rules to remember and display */
  static int numRecentRules = 3;

  private Displayable[] recentRules = new Displayable[numRecentRules];

  private int[] recentRows = new int[numRecentRules];

  private int[] recentColumns = new int[numRecentRules];

  private int currentRow = -1;

  private int currentColumn = -1;

  private String currentCellText = null;
  
  /**
   * Effectively this is the "clipboard" contents.
   */

  private static Polylist copiedCells = Polylist.nil;

  /**
   * Sets of all patterns of each type
   */
  private PatternSet allBassPatterns;

  private PatternSet allChordPatterns;

  private PatternSet allDrumPatterns;

  /* array of percussion instrument names */
  
  ArrayList<String> instrumentIdByRow;
  
  /** Header for rows of table */
  protected JList rowHeader;

  private ArrayList<String> rowHeaderLabels;

  private RowHeaderRenderer rowHeaderRenderer;

  protected int selectedRowIndex = -1;

  
  public StyleEditor(Notate notate, File styleFile)
      {
        this(notate);
        loadFromFile(styleFile);
      }

  /**
   * Constructs a new StyleGeneratorEditor JFramel
   */
  public StyleEditor(Notate notate)
    {
    // Establish Directories

    styleDir = ImproVisor.getStyleDirectory();
    styleExtractDir = ImproVisor.getStyleExtractDirectory();

    this.notate = notate;
    cm = new CommandManager();

    this.setTitle("Style Editor: New Style");

    initComponents();
    
    // Set combo boxes to avoid scrolling small number of items
    bassLowNote.setMaximumRowCount(NOTE_COMBO_ITEMS_TO_DISPLAY);
    bassHighNote.setMaximumRowCount(NOTE_COMBO_ITEMS_TO_DISPLAY);
    bassBaseNote.setMaximumRowCount(NOTE_COMBO_ITEMS_TO_DISPLAY);
    chordLowNote.setMaximumRowCount(NOTE_COMBO_ITEMS_TO_DISPLAY);
    chordHighNote.setMaximumRowCount(NOTE_COMBO_ITEMS_TO_DISPLAY);
    chordPitchComboBox.setMaximumRowCount(NOTE_COMBO_ITEMS_TO_DISPLAY);
    chordTypeComboBox.setMaximumRowCount(CHORD_ITEMS_TO_DISPLAY);

    initFileChoosers();
    initToolbars();
    setAttributes();

    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    cm.changedSinceLastSave(false);
    newStyle();

    newTable();

    recentRules[0] = recentRules[1] = recentRules[2] = null;

    styleTable.addMouseListener(new MouseAdapter()
      {
      @Override
      public void mouseClicked(MouseEvent evt)
        {
        setStatus("OK");
        Point pt = evt.getPoint();
        int rowIndex = styleTable.rowAtPoint(pt);
        int colIndex = styleTable.columnAtPoint(pt);
        //System.out.println("clicked row = " + rowIndex + ", col = " + colIndex);
        enterFromCell(rowIndex, 
                      colIndex, 
                      evt.isControlDown(), 
                      evt.isShiftDown());

        if( trackWithPianoRoll.isSelected() )
          {
            usePianoRoll(colIndex);
          }
        }
      });
    }


  /**
  * Enter data through a spreadsheet cell.
  * Called only from the mouseClicked method above.
  @param rowIndex
  @param colIndex
  @param controlDown
  */
  
public void enterFromCell(int rowIndex, 
                          int colIndex, 
                          boolean controlDown, 
                          boolean shiftDown)
  {
    //System.out.println("clicked at row = " + rowIndex + ", col = " + colIndex);

    if( rowIndex >= styleTable.getRowCount()
            || colIndex >= styleTable.getColumnCount()
            || rowIndex < 0
            || colIndex < 0 )
      {
        // probably clicked outside the table
        return;
      }

    if( shiftDown && controlDown && colIndex >= 1 )
      {
        usePianoRoll(colIndex);
        return;
      }

    currentRow = rowIndex;
    currentColumn = colIndex;

    Object currentContents = styleTable.getValueAt(currentRow, currentColumn);
    //System.out.println("currentContents = " + currentContents + " class " + currentContents.getClass());
    currentCellText = currentContents == null ? " " : currentContents.toString();

    if( controlDown )
      {
        /*
         * Control down implies select all percussion instruments and play
         * entire pattern, unless shift is also down, in which case we 
         * transferred to pianoroll above and returned.
         */

      playPercussionColumn(colIndex);           
      }
    else
      {
        // Control is not down.
        // Play the cell if playable

        maybePlayAt(rowIndex, colIndex);
      }
  }


  private void maybePlayAt(int rowIndex, int colIndex)
    {
        Object rule = styleTable.getValueAt(rowIndex, colIndex);

        maybePlay(rule);

        updateMirror(rowIndex, colIndex, rule);      
    }
  
  
  
  private void maybePlay(Object ob)
    {
      if( ob != null && ob instanceof Playable && isPlayed() )
        {
          ((Playable)ob).playMe();
        }
    }
  
  
  boolean looping = false;
  
  public int getLoopValue()
    {
      return looping ? -1 : 0;
    }
  
  public void setLooping(boolean value)
    {
    looping = value;
    }
  
  
  /**
   * Play the percussion pattern in the designated column.
   * @param colIndex index of the column to play
   */

void playPercussionColumn(int colIndex)
  {
    int count = styleTable.getRowCount();

    if( count != 0 )
      {
        styleTable.setRowSelectionInterval(
                StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW,
                count - 1);
      }
    ListSelectionModel selection =
            styleTable.getTableHeader().getColumnModel().getSelectionModel();

    selection.setSelectionInterval(colIndex, colIndex);

    maybePlay(allDrumPatterns.get(colIndex));
  }


/**
 * Play the chord pattern in the designated column.
 *
 * @param colIndex index of the column to play
 */

void playChordColumn(int colIndex)
  {
    int count = styleTable.getRowCount();

    if( count != 0 )
      {
        styleTable.setRowSelectionInterval(
                StyleTableModel.CHORD_PATTERN_ROW,
                count - 1);
      }
    ListSelectionModel selection =
            styleTable.getTableHeader().getColumnModel().getSelectionModel();

    selection.setSelectionInterval(colIndex, colIndex);

    maybePlay(allChordPatterns.get(colIndex));
  }


/**
 * Play the bass pattern in the designated column.
 * @param colIndex index of the column to play
 */

void playBassColumn(int colIndex)
  {
    int count = styleTable.getRowCount();

    if( count != 0 )
      {
        styleTable.setRowSelectionInterval(
                StyleTableModel.BASS_PATTERN_ROW,
                count - 1);
      }
    ListSelectionModel selection =
            styleTable.getTableHeader().getColumnModel().getSelectionModel();

    selection.setSelectionInterval(colIndex, colIndex);

    maybePlay(allBassPatterns.get(colIndex));
  }


  /**
   * Update the "cache", a few rows above the actual spreadsheet,
   * showing cell contents for convenience in editing.
   @param rowIndex
   @param colIndex
   @param rule
   */
  
  void updateMirror(int rowIndex, int colIndex, Object rule)
    {
    // This part makes the clicked patterns show up in the textfields above the jtable.
    // As different cells are clicked, the patterns are shifted upward.
    
    String text = rule == null ? " " : rule.toString(); //.toUpperCase();

    if( rule instanceof Displayable ) // && lastRuleClicked != rule )
      {
      lastRuleClicked = rule;

      recentRules[2] = recentRules[1];
      recentRules[1] = recentRules[0];
      recentRules[0] = (Displayable)rule;

      recentRows[2] = recentRows[1];
      recentRows[1] = recentRows[0];
      recentRows[0] = rowIndex;

      recentColumns[2] = recentColumns[1];
      recentColumns[1] = recentColumns[0];
      recentColumns[0] = colIndex;
      }

    // Set coloration of cache entries
    
      if( recentRules[0] != null )
      {
          Object contents = styleTable.getValueAt(recentRows[0], recentColumns[0]);
          styleTextField0.setText(contents.toString());
          if( contents instanceof PatternDisplay )
            {
            beatsField0.setText("" + ((PatternDisplay)contents).getBeats());
            }
          rowField0.setText("" + rowHeaderLabels.get(recentRows[0]));
          columnField0.setText("" + styleTable.getColumnName(recentColumns[0]));
          setTextFieldColor(contents, beatsField0);
          setTextFieldColor(contents, styleTextField0);
          setTextFieldColor(contents, rowField0);
          setTextFieldColor(contents, columnField0);
      }

        if( recentRules[1] != null )
        {
          Object contents = styleTable.getValueAt(recentRows[1], recentColumns[1]);
          styleTextField1.setText(contents.toString());
          if( contents instanceof PatternDisplay )
            {
              beatsField1.setText("" + ((PatternDisplay)contents).getBeats());
            }
          rowField1.setText("" + rowHeaderLabels.get(recentRows[1]));
          columnField1.setText("" + styleTable.getColumnName(recentColumns[1]));
          setTextFieldColor(contents, beatsField1);
          setTextFieldColor(contents, styleTextField1);
          setTextFieldColor(contents, rowField1);
          setTextFieldColor(contents, columnField1);
        }
      
        if( recentRules[2] != null )
        {
          Object contents = styleTable.getValueAt(recentRows[2], recentColumns[2]);
          styleTextField2.setText(contents.toString());
          if( contents instanceof PatternDisplay )
            {
              beatsField2.setText("" + ((PatternDisplay)contents).getBeats());
            }
          rowField2.setText("" + rowHeaderLabels.get(recentRows[2]));
          columnField2.setText("" + styleTable.getColumnName(recentColumns[2]));
          setTextFieldColor(contents, beatsField2);
          setTextFieldColor(contents, styleTextField2);
          setTextFieldColor(contents, rowField2);
          setTextFieldColor(contents, columnField2);
        }      
    }

  
  public void setTextFieldColor(Object contents, JTextField field)
    {
    if( contents instanceof Playable )
      {
      Color color = ((Playable)contents).getColor();
      field.setBackground(color);
      }
    }
  
  public ArrayList<String> getRowHeaderLabels()
    {
    return rowHeaderLabels;
    }

  public void addHeaderLabel(String text)
    {
    rowHeaderLabels.add(text);
    }

  public StyleTableModel getTableModel()
    {
    return (StyleTableModel)styleTable.getModel();
    }

  public TableColumnModel getColumns()
    {
    return styleTable.getTableHeader().getColumnModel();
    }

  /**
   * @return the Notate for this object
   */
  
  public Notate getNotate()
    {
    return notate;
    }

  /**
   * @return the CommandManager for this object
   */
  
  public CommandManager getCM()
    {
    return cm;
    }

  /**
   * Set status field with message to user.
   */
  
  public void setStatus(String msg)
    {
    styleEditorStatusTF.setText(msg);
    }

  /**
   * @return the swing value specified by the user in the text box
   */
  
  public double getSwingValue()
    {
    String swingVal = swingTextField.getText().trim();
    try
      {
      double swing = Double.parseDouble(swingVal);
      if( swing < 0.0 || swing > 1.0 )
        {
        return 0.5;
        }
      return swing;
      }
    catch( NumberFormatException e )
      {
      return 0.5;
      }
    }

  /**
   * @return the comp-swing value specified by the user in the text box
   */
  
  public double getAccompanimentSwingValue()
    {
    String accompanimentSwingVal = accompanimentSwingTextField.getText().trim();
    try
      {
      double accompanimentSwing = Double.parseDouble(accompanimentSwingVal);
      if( accompanimentSwing < 0.0 || accompanimentSwing > 1.0 )
        {
        return 0.5;
        }
      return accompanimentSwing;
      }
    catch( NumberFormatException e )
      {
      return 0.5;
      }
    }


  /**
   * @return a correctly formmatted String with all legal bass patterns displayed that are marked "include"
   * Saves a pattern's error message to MIDIBeast if a pattern is incorrectly formmatted.
   */
  
  public void getBassPatterns(StringBuilder buffer)
    {
    Iterator pats = allBassPatterns.iterator();
    while( pats.hasNext() )
      {
      try
        {
        Object ob = pats.next();
        if( ob instanceof BassPatternDisplay )
          {
          BassPatternDisplay b = (BassPatternDisplay)ob;
          if( b.getIncludedStatus() )
            {
            if( b.checkStatus() )
              {
              buffer.append("\t");
              buffer.append(b.getPattern());
              buffer.append("\n");
              }
            }
          }
        }
      catch( ClassCastException e )
        {
        }
      }
    }

  
  /**
   * @return a correctly formmatted String with all legal drum patterns displayed that are marked "include"
   * Saves a pattern's error message to MIDIBeast if a pattern is incorrectly formmatted.
   */
  
  public void getDrumPatterns(StringBuilder buffer)
    {
    Iterator pats = allDrumPatterns.iterator();
    while( pats.hasNext() )
      {
      try
        {
        Object ob = pats.next();
        if( ob instanceof DrumPatternDisplay )
          {
          DrumPatternDisplay d = (DrumPatternDisplay)ob;
          
          //System.out.println("d = " + d.getPattern(true) );

            if( d.checkStatus() )
              {
              buffer.append("\t");
              buffer.append(d.getPattern(true));
              buffer.append("\n");
              }
          }
        }
      catch( ClassCastException e )
        {
        }
      }
    }

  
  /**
   * @return a correctly formatted String with all legal chord patterns 
   * displayed that are marked "include".  Saves a pattern's error message 
   * to MIDIBeast if a pattern is incorrectly formatted.
   */
  
  public void getChordPatterns(StringBuilder buffer)
    {
    Iterator pats = allChordPatterns.iterator();
    while( pats.hasNext() )
      {
      try
        {
        Object ob = pats.next();
        if( ob instanceof ChordPatternDisplay )
          {
          ChordPatternDisplay b = (ChordPatternDisplay)ob;
          if( b.getIncludedStatus() )
            {
            if( b.checkStatus() )
              {
              buffer.append("\t");
              buffer.append(b.getPattern());
              buffer.append("\n");
              }
            }
          }
        }
      catch( ClassCastException e )
        {
        }
      }
    }

  
  /**
   * @return a correctly formmatted String with all user-specified attributes
   * Saves an error message to MIDIBeast if illegal combinations are used (ex: BassHigh less than BassLow)
   */
  
  public String getAttributes()
    {
    String attributes = "";
    String octaveBH = (String)bassHighOctave.getValue();
    octaveBH = octaveBH.replaceAll(" -", "-");
    if( octaveBH.equals(MIDDLE_OCTAVE) )
      {
      attributes += "\t(bass-high " + bassHighNote.getSelectedItem() + ")\n";
      }
    else
      {
      attributes +=
              "\t(bass-high " + bassHighNote.getSelectedItem() + octaveBH + ")\n";
      }

    String octaveBL = (String)bassLowOctave.getValue();
    octaveBL = octaveBL.replaceAll(" -", "-");
    if( octaveBL.equals(MIDDLE_OCTAVE) )
      {
      attributes += "\t(bass-low " + bassLowNote.getSelectedItem() + ")\n";
      }
    else
      {
      attributes +=
              "\t(bass-low " + bassLowNote.getSelectedItem() + octaveBL + ")\n";
      }

    String octaveBB = (String)bassBaseOctave.getValue();
    octaveBB = octaveBB.replaceAll(" -", "-");
    if( octaveBB.equals(MIDDLE_OCTAVE) )
      {
      attributes += "\t(bass-base " + bassBaseNote.getSelectedItem() + ")\n";
      }
    else
      {
      attributes +=
              "\t(bass-base " + bassBaseNote.getSelectedItem() + octaveBB + ")\n";
      }

    //error-checking: in case bassHigh lower than the bassLow.
    if( attrOctaves.indexOf(octaveBH) < attrOctaves.indexOf(octaveBL) )
      {
      MIDIBeast.addSaveError("The Bass High note must be higher than the Bass Low note.");
      }
    //error-checking: in case bassBase is lower than bassLow.
    if( attrOctaves.indexOf(octaveBB) < attrOctaves.indexOf(octaveBL) )
      {
      MIDIBeast.addSaveError("The Bass Nominal note must be higher than the Bass Low note.");
      }
    //error-checking: in case bassBase is higher than bassHigh.
    if( attrOctaves.indexOf(octaveBH) < attrOctaves.indexOf(octaveBB) )
      {
      MIDIBeast.addSaveError("The Bass Nominal note must be lower than the Bass High note.");
      }
    //error-checking: in case the octave is the same but the bassHigh pitch is lower than the bassLow pitch.
    if( attrOctaves.indexOf(octaveBH) == attrOctaves.indexOf(octaveBL) && attrPitches.indexOf(octaveBH) < attrPitches.indexOf(octaveBL) )
      {
      MIDIBeast.addSaveError("The Bass High note must be higher than the Bass Low note.");
      }
    //error-checking: in case the octave is the same but the bassBase is lower than bassLow.
    if( attrOctaves.indexOf(octaveBB) == attrOctaves.indexOf(octaveBL) && attrPitches.indexOf(octaveBB) < attrPitches.indexOf(octaveBL) )
      {
      MIDIBeast.addSaveError("The Bass Nominal note must be higher than the Bass Low note.");
      }
    //error-checking: in case the octave is the same but the bassBase is higher than bassHigh.
    if( attrOctaves.indexOf(octaveBB) == attrOctaves.indexOf(octaveBH) && attrPitches.indexOf(octaveBH) < attrOctaves.indexOf(octaveBB) )
      {
      MIDIBeast.addSaveError("The Bass Nominal note must be lower than the Base High note.");
      }
    //error-checking: make sure the bassHigh and the bassLow are at least an octave apart.
    if( attrOctaves.indexOf(octaveBL) == attrOctaves.indexOf(octaveBH) && attrPitches.indexOf(octaveBL) < attrPitches.indexOf(octaveBH) )
      {
      MIDIBeast.addSaveError("The Bass High note and the Bass Low note must be at least an octave apart.");
      }
    //error-checking: make sure the bassHigh and the bassLow are at least an octave apart.
    if( (attrOctaves.indexOf(octaveBH) - attrOctaves.indexOf(octaveBL)) == 1 && attrPitches.indexOf(octaveBH) < attrPitches.indexOf(octaveBL) )
      {
      MIDIBeast.addSaveError("The Bass High note and the Bass Low note must be at least an octave apart.");
      }

    String swingVal = swingTextField.getText().trim();
    double swing = Double.parseDouble(swingVal);
    if( swing > 0.0 && swing < 1.0 )
      {
      attributes += "\t(swing " + swingTextField.getText() + ")\n";
      }
    else
      {
      MIDIBeast.addSaveError("Invalid swing value.  Using the default swing value of 0.5 instead.");
      attributes += "\t(swing 0.5)\n";
      }

    String accompanimentSwingVal = accompanimentSwingTextField.getText().trim();
    double accompanimentSwing = Double.parseDouble(accompanimentSwingVal);
    if( accompanimentSwing > 0.0 && accompanimentSwing < 1.0 )
      {
      attributes +=
              "\t(comp-swing " + accompanimentSwingTextField.getText() + ")\n";
      }
    else
      {
      MIDIBeast.addSaveError("Invalid comp-swing value.  Using the default comp-swing value of 0.5 instead.");
      attributes += "\t(comp-swing 0.5)\n";
      }

    attributes += "\t(voicing-type " + voicingType.getSelectedItem() + ")\n";
    //TODO: Correctly implement voicing type.

    String octaveCH = (String)chordHighOctave.getValue();
    octaveCH = octaveCH.replaceAll(" -", "-");
    if( octaveCH.equals(MIDDLE_OCTAVE) )
      {
      attributes += "\t(chord-high " + chordHighNote.getSelectedItem() + ")\n";
      }
    else
      {
      attributes +=
              "\t(chord-high " + chordHighNote.getSelectedItem() + octaveCH + ")\n";
      }

    String octaveCL = (String)chordLowOctave.getValue();
    octaveCL = octaveCL.replaceAll(" -", "-");
    if( octaveCL.equals(MIDDLE_OCTAVE) )
      {
      attributes += "\t(chord-low " + chordLowNote.getSelectedItem() + ")\n";
      }
    else
      {
      attributes +=
              "\t(chord-low " + chordLowNote.getSelectedItem() + octaveCL + ")\n";
      }

    //error-checking: in case chordHigh lower than the chordLow.
    if( attrOctaves.indexOf(octaveCH) < attrOctaves.indexOf(octaveCL) )
      {
      MIDIBeast.addSaveError("The Chord High note must be higher than the Chord Low note.");
      }
    //error-checking: in case the octave is the same but the chordHigh pitch is lower than the chordLow pitch.
    if( attrOctaves.indexOf(octaveCH) == attrOctaves.indexOf(octaveCL) 
     && attrPitches.indexOf(octaveCH) < attrPitches.indexOf(octaveCL) )
      {
      MIDIBeast.addSaveError("The Chord High note must be higher than the Chord Low note.");
      }
    //error-checking: make sure the chordHigh and the chordLow are at least an octave apart.
    if( attrOctaves.indexOf(octaveCL) == attrOctaves.indexOf(octaveCH) 
     && attrPitches.indexOf(octaveCL) < attrPitches.indexOf(octaveCH) )
      {
      MIDIBeast.addSaveError("The Chord High note and the Chord Low note must be at least an octave apart.");
      }
    //error-checking: make sure the chordHigh and the chordLow are at least an octave apart.
    if( (attrOctaves.indexOf(octaveCH) - attrOctaves.indexOf(octaveCL)) == 1 
      && attrPitches.indexOf(octaveCH) < attrPitches.indexOf(octaveCL) )
      {
      MIDIBeast.addSaveError("The Chord High note and the Chord Low note must be at least an octave apart.");
      }

    attributes += "\t(comments " + commentArea.getText() + ")\n";

    return attributes;
    }

  
  /**
   * Fills the preview toolbar with all options for playback.
   */
  
  public void initToolbars()
    {
    Polylist p = Advisor.getAllChords();
    ArrayList<String> chordNames = new ArrayList<String>();
    while( p.nonEmpty() )
      {
      if( (p.first() instanceof Polylist) )
        {
        Polylist item = (Polylist)p.first();
        String chord = (String)item.first();
        chord = chord.substring(1, chord.length());
        chordNames.add(chord);
        p = p.rest();
        }
      }
    DefaultComboBoxModel model = new DefaultComboBoxModel(chordNames.toArray());
    chordTypeComboBox.setModel(model);

    masterVolumeSlider.setMaximum(Constants.MAX_VOLUME);
    masterVolumeSlider.setMinimum(0);
    masterVolumeSlider.setValue(Constants.MAX_VOLUME / 2);
    tempoComboBox.setSelectedIndex(13);
    }

  
  /**
   * Saves the current style to file and updates the list of available styles 
   * so that the new style is available in the Style Preferences dialog
   */
  
  public void saveStyle(File file)
    {
    MIDIBeast.newSave();
    String name = file.getName();

    try
      {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));

      //Try to reomve the expected ".sty" extension from the file path for the name field
      try
        {
        name = name.substring(0, name.length() - 4);
        }
      catch( ArrayIndexOutOfBoundsException e )
        {
        }

      StringBuilder buffer = new StringBuilder();

      buffer.append("(style\n");
      buffer.append("\t(name ");
      buffer.append(name);
      buffer.append(")\n");

      String attributes = getAttributes();
      buffer.append(attributes);

      if( isInstrumentIncluded(StyleTableModel.BASS_PATTERN_ROW) )
        {
        getBassPatterns(buffer);
        }

      getDrumPatterns(buffer);

      if( isInstrumentIncluded(StyleTableModel.CHORD_PATTERN_ROW) )
        {
        getChordPatterns(buffer);
        }

      buffer.append(")");
      
      String styleResult = buffer.toString();
      
      Polylist p = Notate.parseListFromString(styleResult);
      Polylist t = (Polylist)p.first();
      Advisor.updateStyle(t.rest());
      notate.styleListModel.reset();
      out.write(styleResult);
      out.close();

      setStatus("Style saved.");
      this.setTitle("Style Editor: " + name + styleExt);
      if( cm != null )
        {
        cm.changedSinceLastSave(false);
        }
      ImproVisor.setRecentStyleFile(file);
      notate.reCaptureCurrentStyle(); // In case this style is being used currently
      }
    catch( Exception e )
      {
      MIDIBeast.addSaveError("An unknown error occurred when attempting to save style " + name);
      }

    styleName = name;
    }

  
  /**
   * Opens a browser for saving current style with a user-specified name and place. 
   * @return 0 if the user cancels, 1 otherwise.
   */
  
  public int saveStyleAs()
    {
    saveStyle.setCurrentDirectory(styleDir);

    if( saveStyle.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {
      boolean noErrors = true;
      if( saveStyle.getSelectedFile().getName().endsWith(styleExt) )
        {
        savedStyle = saveStyle.getSelectedFile();
        if( savedStyle.exists() )
          {
          //prompt for overwriting files
          int result = JOptionPane.showConfirmDialog(this,
                  "File exists. Over-write?", "Overwrite",
                  JOptionPane.YES_NO_OPTION);
          if( result == JOptionPane.YES_OPTION )
            {
            saveStyle(savedStyle);
            }
          }
        else
          {
          // Doesn't exist, but has extension
          String file = saveStyle.getSelectedFile().getAbsolutePath();
          savedStyle = new File(file);
          saveStyle(savedStyle);
          }
        }
      else
        {
        // Doesn't end with extension
        String file = saveStyle.getSelectedFile().getAbsolutePath();
        file += styleExt;
        savedStyle = new File(file);
        saveStyle(savedStyle);
        }
      }
    else
      {
      savedStyle = null;  // to prevent subsequent attempts from not querying
      return 0;
      }
    return 1;
    }

  /**
   * A controller that opens the Save Style As dialog 
   * if the user has not previously saved the current style
   * @return 0 if the user cancels, 1 otherwise
   */
  
  public int saveStyle()
    {
    if( savedStyle != null )
      {
      saveStyle(savedStyle);
      return 1;
      }
    else
      {
      return saveStyleAs();
      }
    }

  /**
   * Prompts the user to save changes if information will be lost if the program closes
   * @return 1 if the user selects "save before closing/opening"
   *         0 if the user selects "cancel"
   *        -1 if the user selects "don't save before closing/opening"
   */
  public int unsavedStyle()
    {
    //If we are going to lose changes upon closing the style editor, prompt the user

    Object[] options = {"<html><b><u>Y</u>es</b>, save these style changes</html>",
                        "<html><b><u>N</u>o</b>, do not save these style changes</html>",
                        "<html><b>Cancel</b>, do not close the style editor</html>"
    };
    UnsavedChanges dialog = new UnsavedChanges(this,
            "Save changes before closing?", options);
    dialog.setTitle("Unsaved Changes");
    dialog.setMsg("There are unsaved changes in the current style that will be lost if you continue.");
    dialog.setVisible(true);
    dialog.dispose();
    UnsavedChanges.Value choice = dialog.getValue();

    switch( choice )
      {
      case YES:   // save before closing/opening
        return 1;
      case NO:    // close without saving/opening
        return -1;
      case CANCEL:// don't close/open              
        return 0;
      }
    return 0;
    }

  /**
   * Controls the closing operations of the StyleGenerator
   * Prompts the user to save changes if information will be lost if the program closes
   * @return -1 if the program should ultimately close.  Return 1 otherwise.
   */
  private int closeWindow()
    {
    notate.setNormalMode();
    if( cm.changedSinceLastSave() )
      {
      int userInput = unsavedStyle();
      if( userInput == 1 )
        {
        int inputTwo = saveStyle();
        if( inputTwo != 0 )
          {
          dispose();
          return -1;
          }
        }
      else if( userInput == -1 )
        {
        dispose();
        return -1;
        }
      }
    else
      {
      dispose();
      return -1;
      }
    return 1;
    }

  /**
   * Override dispose so as to unregister this window first.
   */
  
  public void dispose()
    {
    WindowRegistry.unregisterWindow(this);
    super.dispose();
    }
  
  /**
   * Reorders the numbers on the bass titles to the order currently displayed on screen
   * (Originally intended to update titles after various user-requested sorting operations.
   * These operations were removed from the GUI because they seemed slow and not too useful.)
   * Currently used when cutting a pattern.
   */
  private void updateBassTitles()
    {
    Component[] allItems = bassHolderPane.getComponents();
    for( int i = 0; i < allItems.length; i++ )
      {
      try
        {
        BassPatternDisplay b = (BassPatternDisplay)allItems[i];
        b.setTitleNumber(i + 1);
        }
      catch( ClassCastException e )
        {
        }
      }
    }

  /**
   * Reorders the numbers on the drum titles to the order currently displayed on screen
   * (Originally intended to update titles after various user-requested sorting operations.
   * These operations were removed from the GUI because they seemed slow and not too useful.)
   *  Currently used when cutting a pattern.
   */
  private void updateDrumTitles()
    {
    Component[] allItems = drumHolderPane.getComponents();
    for( int i = 0; i < allItems.length; i++ )
      {
      try
        {
        DrumPatternDisplay d = (DrumPatternDisplay)allItems[i];
        d.setTitleNumber(i + 1);
        }
      catch( ClassCastException e )
        {
        }
      }
    }

  /**
   * Reorders the numbers on the chord titles to the order currently displayed on screen
   * (Originally intended to update titles after various user-requested sorting operations.
   * These operations were removed from the GUI because they seemed slow and not too useful.)
   * Currently used when cutting a pattern.
   */
  private void updateChordTitles()
    {
    Component[] allItems = chordHolderPane.getComponents();
    for( int i = 0; i < allItems.length; i++ )
      {
      try
        {
        ChordPatternDisplay c = (ChordPatternDisplay)allItems[i];
        c.setTitleNumber(i + 1);
        }
      catch( ClassCastException e )
        {
        }
      }
    }

  /**
   * Sets up the file browsers and their attributes used by this GUI
   */
  private void initFileChoosers()
    {
    LeadsheetFileView styView = new LeadsheetFileView();

    saveStyle.setCurrentDirectory(styleDir);
    saveStyle.setDialogType(JFileChooser.SAVE_DIALOG);
    saveStyle.setDialogTitle("Save Style As");
    saveStyle.setFileSelectionMode(JFileChooser.FILES_ONLY);
    saveStyle.resetChoosableFileFilters();
    saveStyle.addChoosableFileFilter(new StyleFilter());
    saveStyle.setFileView(styView);

    openStyle.setCurrentDirectory(styleDir);
    openStyle.setDialogType(JFileChooser.OPEN_DIALOG);
    openStyle.setDialogTitle("Open Style");
    openStyle.setFileSelectionMode(JFileChooser.FILES_ONLY);
    openStyle.resetChoosableFileFilters();
    openStyle.addChoosableFileFilter(new StyleFilter());
    openStyle.setFileView(styView);

    midiFileChooser.setCurrentDirectory(styleExtractDir);
    midiFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    midiFileChooser.setDialogTitle("Open MIDI");
    midiFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    midiFileChooser.resetChoosableFileFilters();
    midiFileChooser.addChoosableFileFilter(new MidiFilter());
    midiFileChooser.setFileView(styView);

    chordFileChooser.setCurrentDirectory(styleExtractDir);
    chordFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    chordFileChooser.setDialogTitle("Open Leadsheet");
    chordFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    chordFileChooser.resetChoosableFileFilters();
    chordFileChooser.addChoosableFileFilter(new LeadsheetFilter());
    chordFileChooser.setFileView(styView);

    }

  /**
   * Shows the Open File dialog and calls loadFromFile() to parse and display the style.
   */
  public void openStyle()
    {
    if( openStyle.getCurrentDirectory().getAbsolutePath().equals("/") )
      {
      openStyle.setCurrentDirectory(styleDir);
      }
    //show open file dialog
    if( openStyle.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
      {
      // The opened file becomes the saved file, in case of save.
      savedStyle = openStyle.getSelectedFile();
      
      // Load the file.
      loadFromFile(savedStyle);
      
      //clear undo/redo history
      cm.clearHistory();
      
      //mark style as saved in its current state (no unsaved changes)
      cm.changedSinceLastSave(false);
      }
    //refreshAll();
    }

  /**
   *
   * Reads file, parses style, and changes the three patterns of a style into objects expected in the styleTable.
   */
  public void loadFromFile(File file)
    {
    reset();

    this.setTitle("Style Editor: " + file.getName());

    // Parse style.
    String s = OpenLeadsheetCommand.fileToString(file);
    // The parens that open and close a style need to be removed so that the 
    // polylist parses correctly.  The parens are included in the file 
    // to maintain backwards compatability with the Style Textual Editor.
    
    if( s == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Unable to open style file: " + file.getName());
        return;
      }
    
    savedStyle = file;
    ImproVisor.setRecentStyleFile(file);
    
    s = s.substring(1, s.length() - 1);
    Polylist poly = Notate.parseListFromString(s);
    Style style = Style.makeStyle(Notate.parseListFromString(s));

    // want to change these...
    ArrayList<BassPattern> bp = style.getBP();
    ArrayList<DrumPattern> dp = style.getDP();
    ArrayList<ChordPattern> cp = style.getCP();

    getTableModel().resetPatterns();

    loadAttributes(style);

    // ...into these
    ArrayList<RepresentativeDrumRules.DrumPattern> drumP =
            new ArrayList<RepresentativeDrumRules.DrumPattern>();
    
    ArrayList<RepresentativeBassRules.BassPatternObj> bassP =
            new ArrayList<RepresentativeBassRules.BassPatternObj>();
    
    ArrayList<RepresentativeChordRules.ChordPattern> chordP =
            new ArrayList<RepresentativeChordRules.ChordPattern>();


    // Change drums, which use a Polylist notation that must be disected for the table.
    
    RepresentativeDrumRules d = new RepresentativeDrumRules(true);
    
    for( int i = 0; i < dp.size(); i++ )
      {
      RepresentativeDrumRules.DrumPattern aDrumPattern = d.makeDrumPattern();
      DrumPattern curPat = dp.get(i);
      
      for( DrumRuleRep drumPat : curPat.getDrums() )
        { 
        RepresentativeDrumRules.DrumRule aDrumRule = d.makeDrumRule();
        
        aDrumRule.setInstrumentNumber(drumPat.getInstrument());
        
        // This syntax checking should be done in the DrumRule constructor.
        
        for( DrumRuleRep.Element element: drumPat.getElements() )
          {
          String ele = "";
          String suffix = element.getSuffix();
          switch( element.getType() )
            {
              case 'X': ele = HIT_STRING    + suffix; break;
              case 'R': ele = REST_STRING   + suffix; break;
              case 'V': ele = VOLUME_STRING + suffix; break;
              default: assert false;
            }
  
          aDrumRule.addElement(ele);
          }

        aDrumPattern.addRule(aDrumRule);
        }
      drumP.add(aDrumPattern);
      aDrumPattern.setWeight(curPat.getWeight());
      }
    
    int minDuration = getMinDuration();

    // Change bass
    RepresentativeBassRules r = new RepresentativeBassRules(true);
    for( int i = 0; i < bp.size(); i++ )
      {
      String rule = bp.get(i).forGenerator();
      float weight = bp.get(i).getWeight();
      bassP.add(r.makeBassPatternObj(rule, weight));
      }
    // Change chords
    RepresentativeChordRules c = new RepresentativeChordRules(true, minDuration);
    for( int i = 0; i < cp.size(); i++ )
      {
      ChordPattern cpi = cp.get(i);
      String rule = cpi.forGenerator();
      float weight = cpi.getWeight();
      //chordP.add(c.makeChordPattern(rule, weight));
      chordP.add(c.makeChordPattern(cpi));
      }

    // Set up for loading patterns into table

    loadDrumPatterns(drumP);
    loadBassPatterns(bassP);
    loadChordPatterns(chordP);

    loadAttributes(style);
    
    styleName = file.getName();
    }

  /**
   * Loads the attribute information from style into the Attributes tab
   * Uses a default if unknown information is encountered
   */
  private void loadAttributes(Style style)
    {
    String infoBH = style.getBassHigh().toString();
    String infoBHPitch = style.getBassHigh().getPitchString();
    if( infoBH != null )
      {
      String note = String.valueOf(infoBH.charAt(0));
      int octInfo = 1;
      if( infoBH.length() > 1 && infoBH.charAt(1) == '#' )
        { //our combo box model does not allow flats
        note += "#";
        octInfo = 2;
        }
      bassHighNote.setSelectedItem((Object)note);
      //takes out the 8 that appears at the end of the infoBH.
      String octaveInfo = infoBH.substring(octInfo, infoBH.length() - 1);
      octaveInfo = octaveInfo.toString();
      if( octaveInfo.equals("") )
        {
        //if the octave is the middle, puts the default option, which is the * mark.
        bassHighOctave.setValue(MIDDLE_OCTAVE);
        }
      else
        {
        octaveInfo = octaveInfo.replaceAll("-", "- ");
        octaveInfo = octaveInfo.trim();
        bassHighOctave.setValue(octaveInfo);
        }
      }

    String infoBL = style.getBassLow().toString();
    if( infoBL != null )
      {
      String note = String.valueOf(infoBL.charAt(0));
      int octInfo = 1;
      if( infoBL.length() > 1 && infoBL.charAt(1) == '#' )
        { //our combo box model does not allow flats
        note += "#";
        octInfo = 2;
        }
      bassLowNote.setSelectedItem((Object)note);
      String octaveInfo = infoBL.substring(octInfo, infoBL.length() - 1);
      octaveInfo = octaveInfo.toString();
      if( octaveInfo.equals("") )
        {
        bassLowOctave.setValue(MIDDLE_OCTAVE);
        }
      else
        {
        octaveInfo = octaveInfo.replaceAll("-", "- ");
        octaveInfo = octaveInfo.trim();
        bassLowOctave.setValue(octaveInfo);
        }
      }

    String infoBB = style.getBassBase().toString();
    if( infoBB != null )
      {
      String note = String.valueOf(infoBB.charAt(0));
      int octInfo = 1;
      if( infoBB.length() > 1 && infoBB.charAt(1) == '#' )
        { //our combo box model does not allow flats
        note += "#";
        octInfo = 2;
        }
      bassBaseNote.setSelectedItem((Object)note);
      String octaveInfo = infoBB.substring(octInfo, infoBB.length() - 1);
      octaveInfo = octaveInfo.toString();
      if( octaveInfo.equals("") )
        {
        bassBaseOctave.setValue(MIDDLE_OCTAVE);
        }
      else
        {
        octaveInfo = octaveInfo.replaceAll("-", "- ");
        octaveInfo = octaveInfo.trim();
        bassBaseOctave.setValue(octaveInfo);
        }
      }

    String infoCH = style.getChordHigh().toString();
    if( infoCH != null )
      {
      String note = String.valueOf(infoCH.charAt(0));
      int octInfo = 1;
      if( infoCH.length() > 1 && infoCH.charAt(1) == '#' )
        { //our combo box model does not allow flats
        note += "#";
        octInfo = 2;
        }
      chordHighNote.setSelectedItem((Object)note);
      String octaveInfo = infoCH.substring(octInfo, infoCH.length() - 1);
      octaveInfo = octaveInfo.toString();
      if( octaveInfo.equals("") )
        {
        chordHighOctave.setValue(MIDDLE_OCTAVE);
        }
      else
        {
        octaveInfo = octaveInfo.replaceAll("-", "- ");
        octaveInfo = octaveInfo.trim();
        chordHighOctave.setValue(octaveInfo);
        }
      }

    String infoCL = style.getChordLow().toString();
    if( infoCL != null )
      {
      String note = String.valueOf(infoCL.charAt(0));
      int octInfo = 1;
      if( infoCL.length() > 1 && infoCL.charAt(1) == '#' )
        { //our combo box model does not allow flats
        note += "#";
        octInfo = 2;
        }
      chordLowNote.setSelectedItem((Object)note);
      String octaveInfo = infoCL.substring(octInfo, infoCL.length() - 1);
      octaveInfo = octaveInfo.toString();
      if( octaveInfo.equals("") )
        {
        chordLowOctave.setValue(MIDDLE_OCTAVE);
        }
      else
        {
        octaveInfo = octaveInfo.replaceAll("-", "- ");
        octaveInfo = octaveInfo.trim();
        chordLowOctave.setValue(octaveInfo);
        }
      }

    String vType = style.getVoicingType();
    voicingType.setSelectedItem(vType);

    String swingValue = String.valueOf(style.getSwing());
    swingTextField.setText(swingValue);

    String accompanimentSwingValue =
            String.valueOf(style.getAccompanimentSwing());
    accompanimentSwingTextField.setText(accompanimentSwingValue);

    commentArea.setText(style.getComments());
    }

  /**
   * Creates a Jpanel displayed when opening or generating a style does not produce any patterns of a particular type.
   */
  private JPanel createEmptyPatternPanel(String type)
    {
    JPanel emptyPat = new JPanel();
    JLabel emptyLabel = new JLabel("Did not find any " + type + " patterns");
    emptyPat.add(emptyLabel);
    return emptyPat;
    }

  /*
   * Creates one BassPatternDisplay object for each element of bassPatterns with its weight and pattern text.
   * Removes all previous information from the bassHolderPane and adds each of the new display objects
   */
  public void loadBassPatterns(ArrayList<RepresentativeBassRules.BassPatternObj> bassPatterns)
    {
    bassHolderPane.removeAll();
    if( bassPatterns.size() < 1 )
      {
      JPanel emptyPat = createEmptyPatternPanel("bass");
      bassHolderPane.add(emptyPat);
      }

    for( int i = 0; i < bassPatterns.size(); i++ )
      {
      float weight = bassPatterns.get(i).getWeight();
      BassPatternDisplay b =
              new BassPatternDisplay(bassPatterns.get(i).getRule(), weight,
              notate, cm, this);
      b.setTitleNumber((i + 1));
      bassHolderPane.add(b);

      int patternIndex = allBassPatterns.newPattern();
      styleTable.setValueAt(b, StyleTableModel.BASS_PATTERN_ROW, patternIndex);
      StyleTableModel model = getTableModel();
      model.setBassPatternWeight(weight, patternIndex);
      model.setBassPatternBeats(b.getBeats(), patternIndex);
      //System.out.println("loaded bass pattern at column " + patternIndex);
      allBassPatterns.set(patternIndex, b);

      styleTable.setValueAt(b.getWeight(),
              StyleTableModel.BASS_PATTERN_WEIGHT_ROW,
              patternIndex);
      }

    bassHolderPane.revalidate();
    }

  /*
   * Creates one ChordPatternDisplay object for each element of bassPatterns with its weight and pattern text.
   * Removes all previous information from the bassHolderPane and adds each of the new display objects
   */
  public void loadChordPatterns(ArrayList<RepresentativeChordRules.ChordPattern> chordPatterns)
    {
    chordHolderPane.removeAll();
    if( chordPatterns.size() < 1 )
      {
      JPanel emptyPat = createEmptyPatternPanel("chord");
      chordHolderPane.add(emptyPat);
      }

    for( int i = 0; i < chordPatterns.size(); i++ )
      {
      RepresentativeChordRules.ChordPattern cp = chordPatterns.get(i);
      float weight = cp.getWeight();
      ChordPatternDisplay c =
              new ChordPatternDisplay(cp.getRule(), weight, cp.getPush(),
              notate, cm, this);
      c.setTitleNumber((i + 1));
      chordHolderPane.add(c);

      int patternIndex = allChordPatterns.newPattern();
      styleTable.setValueAt(c, StyleTableModel.CHORD_PATTERN_ROW, patternIndex);
      StyleTableModel model = getTableModel();
      model.setChordPatternWeight(weight, patternIndex);
      model.setChordPatternBeats(c.getBeats(), patternIndex);
      model.setChordPatternPush(c.getPushString(), patternIndex);
      //System.out.println("loaded chord pattern at column " + patternIndex);
      allChordPatterns.set(patternIndex, c);
      }
    chordHolderPane.revalidate();
    }

  /*
   * Creates one DrumPatternDisplay object for each element of bassPatterns with its weight and pattern text.
   * Removes all previous information from the bassHolderPane and adds each of the new display objects
   */
  public void loadDrumPatterns(ArrayList<RepresentativeDrumRules.DrumPattern> drumPatterns)
    {
    drumHolderPane.removeAll();
    if( drumPatterns.size() < 1 )
      {
      JPanel emptyPat = createEmptyPatternPanel("drum");
      drumHolderPane.add(emptyPat);
      }

    instrumentIdByRow = new ArrayList<String>();

    for( int i = 0; i < drumPatterns.size(); i++ )
      {
      //Want to change information from curPat into DrumPatternDisplay newPat
      RepresentativeDrumRules.DrumPattern curPat = drumPatterns.get(i);
      float weight = curPat.getWeight();
      DrumPatternDisplay newPat =
              new DrumPatternDisplay(weight, notate, cm, this);

      //Add the curPat rules into newPat as DrumRuleDisplay objects

      int patternIndex = allDrumPatterns.newPattern();
      StyleTableModel model = getTableModel();
      model.setDrumPatternWeight(weight, patternIndex);

      ArrayList<RepresentativeDrumRules.DrumRule> theRules = curPat.getRules();
      for( int j = 0; j < theRules.size(); j++ )
        {
        RepresentativeDrumRules.DrumRule curRule = theRules.get(j);
        String rule = curRule.getDisplayRule();
        int instrumentNumber = curRule.getInstrumentNumber();
        
        String instrument = MIDIBeast.spacelessDrumNameFromNumber(instrumentNumber);
        
        DrumRuleDisplay newRule = new DrumRuleDisplay(rule, 
                                                      instrument,
                                                      notate, 
                                                      cm,  
                                                      this);
        newPat.addRule(newRule);

        // Find row of instrument in table, or create a new rorw
        int k;
        int instrumentRow = 0; // for compilation sake
        for( k = 0; k < instrumentIdByRow.size(); k++ )
          {
          if( instrument.equals(instrumentIdByRow.get(k)) )
            {
            // found this instrument
            instrumentRow = k + StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW;
            break;
            }
          }

        if( k >= instrumentIdByRow.size() )
          {
          // instrument was not found, so add it and set the row header
          // FIX: need to ensure there is a row

          instrumentRow = k + StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW;
          instrumentIdByRow.add(instrument);
          rowHeaderLabels.set(instrumentRow, instrument);
          }

        styleTable.setValueAt(newRule, instrumentRow, patternIndex);
        //System.out.println("loaded percussion pattern at row " + instrumentRow + ", column " + patternIndex);
        }
      newPat.setTitleNumber((i + 1));
      drumHolderPane.add(newPat);
      allDrumPatterns.set(patternIndex, newPat);
      double beats = newPat.getBeats();
      model.setDrumPatternBeats(beats, patternIndex);
      }
    drumHolderPane.revalidate();
    }

    public int findInstrumentRow(String instrumentName) {
        // Find row of instrument in table, or create a new rorw
        int numRows = rowHeaderRenderer.getNumRows();
        for( int k = 0; k < numRows; k++) {
            if( instrumentName.equals(rowHeaderRenderer.getValue(k)) ) {
                // found this instrument
                return k;
            }
        }
        return -1; // not found
    }
    

    
    public boolean isInstrumentIncluded(String instrumentName)
    {
        //System.out.println("instrument = " + instrumentName);
        int row = findInstrumentRow(instrumentName);
        //System.out.println("row = " + row);
        
        boolean result;
        if( row < 0 )
        {
            result = false;
        }
        else
        {
            result = isInstrumentIncluded(row);
        }
        return result;
    }
    

    
    public boolean isDrumInstrumentNumberIncluded(int instrumentNumber)
      {
        int row = getRowByDrumInstrumentNumber(instrumentNumber);
        if( row == -1 )
          {
            return false;
          }
        return isInstrumentIncluded(row);
      }
  

  /**
   * Vestigal: Prevents user from selecting items in the edit menu if they 
   * are unavailable given the current stat 
   * can't cut a pattern if none are selected, etc.)
   */
  private void setEditMenuStatus()
    {
    }

  /**
   * Copies the currently selected bass, drum, or chord object for the tab that is showing.
   */
  private void copyPatternMI()
    {
    if( bassTabPanel.isVisible() )
      {
      if( curSelectedBass != null )
        {
        copiedBass = curSelectedBass;
        }
      }
    else if( drumTabPanel.isVisible() )
      {
      if( curSelectedDrum != null )
        {
        copiedDrum = curSelectedDrum;
        }
      }
    else if( chordTabPanel.isVisible() )
      {
      if( curSelectedChord != null )
        {
        copiedChord = curSelectedChord;
        }
      }
    }

  /**
   *  Resets the Attributes tab in the Style Specification panel to defaults.
   */
  private void setAttributes()
    {

    //creating the array of octaves so that we can compare the octaves for error-checking when we save the attributes tab.
    attrOctaves.add("----");
    attrOctaves.add("---");
    attrOctaves.add("--");
    attrOctaves.add("-");
    attrOctaves.add(MIDDLE_OCTAVE);
    attrOctaves.add("+");
    attrOctaves.add("++");
    attrOctaves.add("+++");
    attrOctaves.add("++++");

    //creating the array of pitches so that we can compare the actual pitches for error-checking when we save the attributes tab.
    attrPitches.add("c");
    attrPitches.add("c#");
    attrPitches.add("d");
    attrPitches.add("d#");
    attrPitches.add("e");
    attrPitches.add("f");
    attrPitches.add("f#");
    attrPitches.add("g");
    attrPitches.add("g#");
    attrPitches.add("a");
    attrPitches.add("a#");
    attrPitches.add("b");


    // Just edits the JSpinners, sine the JComboBox code is in the generated code, and thus must be edited through the Design, not through the Source.
    String[] octaves = {"- - - -", "- - -", "- -", "-", MIDDLE_OCTAVE, "+", "++", "+++",
                        "++++"
    };
    SpinnerListModel bassHighModel  = new SpinnerListModel(octaves);
    SpinnerListModel bassLowModel   = new SpinnerListModel(octaves);
    SpinnerListModel bassBaseModel  = new SpinnerListModel(octaves);
    SpinnerListModel chordHighModel = new SpinnerListModel(octaves);
    SpinnerListModel chordLowModel  = new SpinnerListModel(octaves);
    SpinnerListModel chordBaseModel = new SpinnerListModel(octaves);

    bassHighModel.setValue("-");
    bassHighOctave.setModel(bassHighModel);
    bassLowModel.setValue("- -");
    bassLowOctave.setModel(bassLowModel);
    bassBaseModel.setValue("- -");
    bassBaseOctave.setModel(bassBaseModel);

    chordHighModel.setValue(MIDDLE_OCTAVE);
    chordHighOctave.setModel(chordHighModel);
    chordLowModel.setValue("-");
    chordLowOctave.setModel(chordLowModel);

    swingTextField.setText(String.valueOf(defaultSwing));
    accompanimentSwingTextField.setText(String.valueOf(defaultAccompanimentSwing));

    }

  /**
   * @return the chord selected in the preview options
   */
  public String getChord()
    {
    return chordPitchComboBox.getSelectedItem().toString() + chordTypeComboBox.getSelectedItem().toString();
    }

  /**
   * @return the mute option in the preview options
   */
  public boolean isChordMuted()
    {
    return !muteChordToggle.isSelected();
    }

  /**
   * @return the volume setting in the preview options
   */
  public int getVolume()
    {
    return masterVolumeSlider.getValue();
    }

  /**
   * @return the tempo setting in the preview options
   */
  public int getTempo()
    {
    return Integer.parseInt(tempoComboBox.getSelectedItem().toString());
    }

  public void setTempo(String string)
  {
      tempoComboBox.setSelectedItem(string);
  }

  /**
   * @return true if the play button is selected and we are not
   * in the process of exporting to the pianoroll.
   */
  public boolean isPlayed()
    {
    boolean value = playToggle.isSelected() && !exportingToPianoRoll;
    
    // System.out.println("isPlayed = " + value);
    return value;
    }

  /**
   * Clears all tabs, resets attributes to defaults, and resets all selected information to null
   */
  public void reset()
    {

    newTable();
    bassHolderPane.removeAll();
    drumHolderPane.removeAll();
    chordHolderPane.removeAll();
    setAttributes();

    curSelectedBass = null;
    lastSelectedBass = null;

    curSelectedDrum = null;
    lastSelectedDrum = null;

    curSelectedChord = null;
    lastSelectedChord = null;

    refreshAll();
    }

  /**
   * Updates the UI for every pane
   */
  private void refreshAll()
    {
    bassHolderPane.updateUI();
    drumHolderPane.updateUI();
    chordHolderPane.updateUI();
    }

  /**
   * Creates a new style by reseting the UI and then filling each pattern pane with three
   * empty patterns.  Also triggers saving options if changes would be lost when creating
   * a new style.
   */
  private void newStyle()
    {
    /*
    int feedback = -1;
    if( cm.changedSinceLastSave() )
    {
    feedback = unsavedStyle();
    }
    if( feedback == 1 )
    {
    saveStyle();
    }
    else if( feedback != 0 )
    {
     */
    reset();

    /* For illustration of how to add patterns only:
    //Add one PatternDisplay objects for each type of pattern to the GUI
    for(int i = 0; i < 1; i++) {
    BassPatternDisplay b = new BassPatternDisplay(notate, cm, this);
    b.setTitleNumber(i+1);
    b.setDisplayText("B4");
    DrumPatternDisplay d = new DrumPatternDisplay(notate, cm, this);
    d.fill();
    d.setTitleNumber(i+1);
    ChordPatternDisplay c = new ChordPatternDisplay(notate, cm, this);
    c.setTitleNumber(i+1);
    c.setDisplayText("X4");
    bassHolderPane.add(b);                
    drumHolderPane.add(d);
    chordHolderPane.add(c);
    }
     */

    refreshAll();
    this.setTitle("Style Editor: New Style");
    cm.changedSinceLastSave(false);
    //  }
    }

  /**
   * PatternSets to be kept in sync with the table
   */
  public PatternDisplay getBassPattern(int index)
    {
    return allBassPatterns.get(index);
    }

  public PatternDisplay getChordPattern(int index)
    {
    return allChordPatterns.get(index);
    }

  public PatternDisplay getDrumPattern(int index)
    {
    return allDrumPatterns.get(index);
    }

  private void newTable()
    {
    allBassPatterns = new PatternSet(StyleTableModel.BASS, styleTable);
    allChordPatterns = new PatternSet(StyleTableModel.CHORD, styleTable);
    allDrumPatterns = new PatternSet(StyleTableModel.PERCUSSION, styleTable);

    styleTable.setModel(new StyleTableModel(styleTable));

    columnModel = styleTable.getColumnModel();

    columnModel.setColumnSelectionAllowed(true);
    columnSelectionModel = new DefaultListSelectionModel();
    columnModel.setSelectionModel(columnSelectionModel);

    columnSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
    // for example: columnSelectionModel.setSelectionInterval(2, 2);

    setColumnWidths();

    styleTable.setRowHeight(STYLE_TABLE_ROW_HEIGHT);

    styleTable.setRowSelectionAllowed(true);

    // Not effective:
    // styleTable.getTableHeader().setBackground(Color.blue.brighter());

    // It seems very difficult to keep the column headers in sync with the columns on scrolling

    styleScrollpane.setColumnHeaderView(styleTable.getTableHeader());

    // Render checkboxes for inclusion indicators

    styleTable.setDefaultRenderer(Object.class,
            new StyleCellRenderer());

    styleTable.setRequestFocusEnabled(true);

    // not needed? styleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);


    rowHeaderLabels = getTableModel().getRowHeaders();

    // This array is a dummy. The headers are filled in by RowHeaderRenderer

    String header[] = new String[styleTable.getRowCount()];

    for( int i = 0; i < header.length; i++ )
      {
      header[i] = "";
      }

    rowHeader = new JList(header);

    rowHeader.setFixedCellWidth(100);

    rowHeader.setFixedCellHeight(STYLE_TABLE_ROW_HEIGHT);

    rowHeaderRenderer = new RowHeaderRenderer(rowHeaderLabels, styleTable);

    rowHeader.setCellRenderer(rowHeaderRenderer);

    javax.swing.ListSelectionModel selectionModel =
            new javax.swing.DefaultListSelectionModel();

    selectionModel.setSelectionMode(javax.swing.DefaultListSelectionModel.SINGLE_SELECTION);

    rowHeader.setSelectionModel(selectionModel);

    styleScrollpane.setRowHeaderView(rowHeader);

    styleTable.setDefaultEditor(Object.class,
            new StyleCellEditor(new JTextField(), this));

    final JPopupMenu instrumentMenu = new JPopupMenu();

    // CAUTION: Names of percussion instruments have to agree with those in MIDIBeast.java

    instrumentMenu.add(StyleTableModel.PERCUSSION);
    instrumentMenu.add(NO_CHANGE);
    final JMenu bassDrumMenu = new JMenu("Bass Drums");
    addSubMenu(instrumentMenu, bassDrumMenu);
    addMenuLeaf(bassDrumMenu, "Acoustic_Bass_Drum");
    addMenuLeaf(bassDrumMenu, "Bass_Drum_1");

    final JMenu snareDrumMenu = new JMenu("Snare Drums");
    addSubMenu(instrumentMenu, snareDrumMenu);
    addMenuLeaf(snareDrumMenu, "Acoustic_Snare");
    addMenuLeaf(snareDrumMenu, "Electric_Snare");
    addMenuLeaf(snareDrumMenu, "Side_Stick");

    final JMenu tomMenu = new JMenu("Tom-Toms");
    addSubMenu(instrumentMenu, tomMenu);
    addMenuLeaf(tomMenu, "Low_Tom");
    addMenuLeaf(tomMenu, "Low-Mid_Tom");
    addMenuLeaf(tomMenu, "Hi-Mid_Tom");
    addMenuLeaf(tomMenu, "High_Tom");
    addMenuLeaf(tomMenu, "Low_Floor_Tom");
    addMenuLeaf(tomMenu, "High_Floor_Tom");

    final JMenu cymbalMenu = new JMenu("Cymbals");
    addSubMenu(instrumentMenu, cymbalMenu);
    addMenuLeaf(cymbalMenu, "Ride_Cymbal_1");
    addMenuLeaf(cymbalMenu, "Ride_Cymbal_2");
    addMenuLeaf(cymbalMenu, "Closed_Hi-Hat");
    addMenuLeaf(cymbalMenu, "Open_Hi-Hat");
    addMenuLeaf(cymbalMenu, "Pedal_Hi-Hat");
    addMenuLeaf(cymbalMenu, "Crash_Cymbal_1");
    addMenuLeaf(cymbalMenu, "Crash_Cymbal_2");
    addMenuLeaf(cymbalMenu, "Splash_Cymbal");
    addMenuLeaf(cymbalMenu, "Chinese_Cymbal");

    final JMenu bongoMenu = new JMenu("Bongos/Congas/Timbales");
    addSubMenu(instrumentMenu, bongoMenu);
    addMenuLeaf(bongoMenu, "Hi_Bongo");
    addMenuLeaf(bongoMenu, "Low_Bongo");
    addMenuLeaf(bongoMenu, "Low_Conga");
    addMenuLeaf(bongoMenu, "Mute_Hi_Conga");
    addMenuLeaf(bongoMenu, "Open_Hi_Conga");
    addMenuLeaf(bongoMenu, "Low_Timbale");
    addMenuLeaf(bongoMenu, "High_Timbale");

    final JMenu latinMenu = new JMenu("Other Latin Percussion");
    addSubMenu(instrumentMenu, latinMenu);
    addMenuLeaf(latinMenu, "Cabasa");
    addMenuLeaf(latinMenu, "Claves");
    addMenuLeaf(latinMenu, "Maracas");
    addMenuLeaf(latinMenu, "Low_Wood_Block");
    addMenuLeaf(latinMenu, "Hi_Wood_Block");
    addMenuLeaf(latinMenu, "Mute_Cuica");
    addMenuLeaf(latinMenu, "Open_Cuica");
    addMenuLeaf(latinMenu, "Short_Guiro");
    addMenuLeaf(latinMenu, "Long_Guiro");

    final JMenu bellMenu = new JMenu("Bells");
    addSubMenu(instrumentMenu, bellMenu);
    addMenuLeaf(bellMenu, "Ride_Bell");
    addMenuLeaf(bellMenu, "Low_Agogo");
    addMenuLeaf(bellMenu, "High_Agogo");
    addMenuLeaf(bellMenu, "Cowbell");
    addMenuLeaf(bellMenu, "Tambourine");
    addMenuLeaf(bellMenu, "Mute_Triangle");
    addMenuLeaf(bellMenu, "Open_Triangle");

    final JMenu miscMenu = new JMenu("Miscellaneous");
    addSubMenu(instrumentMenu, miscMenu);
    addMenuLeaf(miscMenu, "Hand_Clap");
    addMenuLeaf(miscMenu, "Vibraslap");
    addMenuLeaf(miscMenu, "Short_Whistle");
    addMenuLeaf(miscMenu, "Long_Whistle");

    MouseListener mouseListener = new MouseAdapter()
      {
      @Override
      public void mouseClicked(MouseEvent e)
        {
        // selectedRowIndex is not local, for communication with actionPerformed
        selectedRowIndex = rowHeader.locationToIndex(e.getPoint());
        if( selectedRowIndex >= StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW )
          {
          // System.out.println("Pressed on header row " + selectedRowIndex);
          instrumentMenu.show(rowHeader, 0,
                  selectedRowIndex * STYLE_TABLE_ROW_HEIGHT);
          }
        }

      };

    rowHeader.addMouseListener(mouseListener);
    }

  void addMenuLeaf(JMenu menu, String text)
    {
    JMenuItem item = new JMenuItem(text);
    item.addActionListener(this);
    menu.add(item);
    }

  void addSubMenu(JPopupMenu menu, JMenu sub)
    {
    menu.add(sub);
    }

  public void actionPerformed(ActionEvent e)
    {
    JMenuItem source = (JMenuItem)(e.getSource());
    String text = source.getText();
    // System.out.println("text = " + text + ", selectedRowIndex = " + selectedRowIndex);
    if( !text.equals(NO_CHANGE) )
      {
      rowHeaderRenderer.setValue(text, selectedRowIndex);
      }
    }

  public void setColumnWidth(int column, int width)
    {
    columnModel.getColumn(column).setPreferredWidth(width);
    //System.out.println("setting column " + column + " width to " + width);
    }

  public void setColumnWidths()
    {
    setColumnWidth(StyleTableModel.INSTRUMENT_INCLUDE_COLUMN,
            INCLUDE_COLUMN_WIDTH);

    int ncols = columnModel.getColumnCount();
    for( int i = StyleTableModel.FIRST_PATTERN_COLUMN; i < ncols; i++ )
      {
      setColumnWidth(i, defaultColumnWidth);
      }
    }

  public ChordPatternDisplay newChordPatternDisplay(int column,
                                                       String contents)
    {
    ChordPatternDisplay display = new ChordPatternDisplay(notate, cm, this);
    chordHolderPane.add(display);
    display.setDisplayText(contents);
    display.setTitleNumber(0); // NEEDED?
    allChordPatterns.set(column, display);
    return display;
    }

  public BassPatternDisplay newBassPatternDisplay(int column, String contents)
    {
    BassPatternDisplay display = new BassPatternDisplay(notate, cm, this);
    bassHolderPane.add(display);
    display.setDisplayText(contents);
    display.setTitleNumber(0); // NEEDED?
    allBassPatterns.set(column, display);
    return display;
    }

  public DrumPatternDisplay newDrumPatternDisplay()
    {
    DrumPatternDisplay display = new DrumPatternDisplay(notate, cm, this);
    drumHolderPane.add(display);
    drumHolderPane.updateUI();
    return display;
    }

  public DrumRuleDisplay newDrumRuleDisplay(int row, int column,
                                               String contents)
    {
    // Get instrument from first column

    String instrument = getRowHeaderLabels().get(row);

    // How to create DrumPattern when none exists?

    DrumPatternDisplay curDrum;

    if( column >= allDrumPatterns.size() )
      {
      allDrumPatterns.add(newDrumPatternDisplay());
      }

    PatternDisplay curPattern = allDrumPatterns.get(column);

    // Use existing drum pattern display, or create new one.

    if( curPattern != null && curPattern instanceof DrumPatternDisplay )
      {
      curDrum = (DrumPatternDisplay)curPattern;
      Object oldContents = styleTable.getValueAt(row, column);
      if( oldContents != null && oldContents instanceof DrumRuleDisplay )
        {
        DrumRuleDisplay oldRule = (DrumRuleDisplay)oldContents;
        curDrum.cutRule(oldRule);
        }
      }
    else
      {
      curDrum = newDrumPatternDisplay();
      }

    DrumRuleDisplay display = new DrumRuleDisplay("", instrument,
            notate, cm, this);
    display.setDisplayText(contents);
    curDrum.addRule(display);

    allDrumPatterns.set(column, curDrum);
    return display;
    }

  /**
   * Makes a copy of the currently selected pattern (if it exists) for the
   * tab that is visible and then removes it from the GUI.
   */
  private void cutPattern()
    {
    copyPatternMI();
    if( bassTabPanel.isVisible() )
      {
      if( curSelectedBass != null )
        {
        bassHolderPane.remove(curSelectedBass);
        curSelectedBass = null;
        updateBassTitles();
        bassHolderPane.updateUI();
        cm.changedSinceLastSave(true);
        }
      }
    else if( drumTabPanel.isVisible() )
      {
      if( curSelectedDrum != null )
        {
        drumHolderPane.remove(curSelectedDrum);
        curSelectedDrum = null;
        updateDrumTitles();
        drumHolderPane.updateUI();
        cm.changedSinceLastSave(true);
        }

      }
    else if( chordTabPanel.isVisible() )
      {
      if( curSelectedChord != null )
        {
        chordHolderPane.remove(curSelectedChord);
        curSelectedChord = null;
        updateChordTitles();
        chordHolderPane.updateUI();
        cm.changedSinceLastSave(true);
        }
      }
    }

  /**
   * Pastes the copied pattern, if it exists, for the currently visible tab
   */
  private void pastePattern()
    {
    if( bassTabPanel.isVisible() )
      {
      if( copiedBass != null )
        {
        //Remove panel displaying "no bass patterns found" if it exists.
        try
          {
          Component com = bassHolderPane.getComponent(0);
          try
            {
            BassPatternDisplay b = (BassPatternDisplay)com;
            }
          catch( ClassCastException e )
            {
            //WARNING: If, in future, items other than BassPatternDisplay are included, this method of removal must change.
            //Therefore b is the JPanel that says "No bass patterns found."  We need to delete it before adding.
            bassHolderPane.remove(com);
            }
          }
        catch( ArrayIndexOutOfBoundsException e )
          {
          }

        /*Must create a new object with the same stats or else the copied pattern is
        just moved to the end of the list. */
        String text = copiedBass.getDisplayText();
        float weight = copiedBass.getWeight();
        BassPatternDisplay b = new BassPatternDisplay(text, weight, notate, cm, this);
        b.setTitleNumber(bassHolderPane.getComponentCount() + 1);
        bassHolderPane.add(b);
        bassHolderPane.updateUI();
        }
      }
    else if( drumTabPanel.isVisible() )
      {
      if( copiedDrum != null )
        {
        //Remove panel displaying "no drum patterns found" if it exists.
        try
          {
          Component com = drumHolderPane.getComponent(0);
          try
            {
            DrumPatternDisplay d = (DrumPatternDisplay)com;
            }
          catch( ClassCastException e )
            {
            //WARNING: If, in future, items other than BassPatternDisplay are included, this method of removal must change.
            //Therefore b is the JPanel that says "No bass patterns found."  We need to delete it before adding.
            drumHolderPane.remove(com);
            }
          }
        catch( ArrayIndexOutOfBoundsException e )
          {
          }

        /*Must create a new object with the same stats or else the copied pattern is
        just moved to the end of the list. */
        int numRules = copiedDrum.getNumComponents();
        DrumPatternDisplay d = new DrumPatternDisplay(notate, cm, this);
        d.setWeight(copiedDrum.getWeight());
        d.setTitleNumber(drumHolderPane.getComponentCount() + 1);
        for( int i = 0; i < numRules; i++ )
          {
          Component r = copiedDrum.getComponentAt(i);
          if( r != null )
            {
            try
              {
              DrumRuleDisplay rule = (DrumRuleDisplay)r;
              String text = rule.getDisplayText();
              String instrument = rule.getInstrument();
              DrumRuleDisplay newRule =
                      new DrumRuleDisplay(text, instrument, notate, cm, this);
              d.addRule(newRule);
              }
            catch( ClassCastException e )
              {
              }
            }
          }
        drumHolderPane.add(d);
        drumHolderPane.updateUI();
        }
      }
    else if( chordTabPanel.isVisible() )
      {
      if( copiedChord != null )
        {
        //Remove panel displaying "no bass patterns found" if it exists.
        try
          {
          Component com = chordHolderPane.getComponent(0);
          try
            {
            ChordPatternDisplay d = (ChordPatternDisplay)com;
            }
          catch( ClassCastException e )
            {
            //WARNING: If, in future, items other than BassPatternDisplay are included, this method of removal must change.
            //Therefore b is the JPanel that says "No bass patterns found."  We need to delete it before adding.
            chordHolderPane.remove(com);
            }
          }
        catch( ArrayIndexOutOfBoundsException e )
          {
          }

        /*Must create a new object with the same stats or else the copied pattern is
        just moved to the end of the list. */
        String text = copiedChord.getDisplayText();
        float weight = copiedChord.getWeight();
        String pushString = copiedChord.getPushString();
        ChordPatternDisplay c =
                new ChordPatternDisplay(text, weight, pushString, notate, cm, this);
        c.setTitleNumber(chordHolderPane.getComponentCount() + 1);
        chordHolderPane.add(c);
        chordHolderPane.updateUI();
        }
      }
    cm.changedSinceLastSave(true);
    }

  /**
   * Add an empty drum rule to the currently selected drum pattern.
   */
  private void addDrumRule()
    {
    if( drumTabPanel.isVisible() )
      {
      if( curSelectedDrum != null )
        {
        curSelectedDrum.addRule(new DrumRuleDisplay(notate, cm, this));
        curSelectedDrum.updateUI();
        }
      }
    }

  /**
   * Copy DrumRuleDisplay copyMe
   */
  public void copyDrumRule(DrumRuleDisplay copyMe)
    {
    if( drumTabPanel.isVisible() )
      {
      copiedInstrument = copyMe;
      }
    }

  /**
   * Copy the currently selected drum rule for the currently selected drum pattern.
   */
  private void copyDrumRule()
    {
    if( drumTabPanel.isVisible() )
      {
      if( curSelectedDrum != null )
        {
        copiedInstrument = curSelectedDrum.getSelectedRule();
        }
      }
    }

  /**
   * Cut the currently selected drum rule
   */
  private void cutDrumRule()
    {
    if( drumTabPanel.isVisible() )
      {
      if( curSelectedDrum != null )
        {
        curSelectedDrum.cutSelectedRule();
        }
      }
    }

  /**
   * Paste the copiedInstrument rule into the currently selected drum panel
   */
  private void pasteDrumRule()
    {
    if( drumTabPanel.isVisible() )
      {
      if( curSelectedDrum != null && copiedInstrument != null )
        {
        curSelectedDrum.pasteRule(copiedInstrument);
        }
      }
    }

  /**
   * Build selected array of cells as Polylist, column by column.
   * Store the result in copiedCells.
   */
  public void copyCurrentCells()
    {
    int rows[] = styleTable.getSelectedRows();
    int cols[] = styleTable.getSelectedColumns();

    // build array column by column, back to front

    Polylist patternArray = Polylist.nil;
    for( int col = cols[cols.length - 1]; col >= cols[0]; col-- )
      {
      Polylist patternColumn = Polylist.nil;
      for( int row = rows[rows.length - 1]; row >= rows[0]; row-- )
        {
        Object contents = styleTable.getValueAt(row, col);
        patternColumn = patternColumn.cons(processCellContents(contents, row,
                col));
        }
      patternArray = patternArray.cons(patternColumn);
      }

    copiedCells = patternArray;
    clipboardTextField.setText(patternArray.toString());
    clipboardTextField.setCaretPosition(0);
    }

  /**
   * Convert cell contents for a copy operation.
  @param contents
  @return
   */
  public Object processCellContents(Object contents, int row, int col)
    {
    if( contents == null )
      {
      contents = "";
      }
    else if( isIncludeCell(row, col) )
      {
      // should be Boolean
      }
    else
      {
      // Some kind of pattern
      contents = Polylist.list(contents.toString());
      }
    return contents;
    }

  /**
   * First copy the selected cells.
   * Then replace each editable cell with EMPTY.
   */
  public void cutCurrentCells()
    {
    int rows[] = styleTable.getSelectedRows();
    int cols[] = styleTable.getSelectedColumns();

    copyCurrentCells();

    for( int col = cols[cols.length - 1]; col >= cols[0]; col-- )
      {
      for( int row = rows[rows.length - 1]; row >= rows[0]; row-- )
        {
        if( getTableModel().isCellEditable(row, col) )
          {
          setCell(EMPTY, row, col, SILENT);
          }
        }
      }
    }

  /**
   * Paste copied cells, taking care not to exceed boundaries of the table.
   */
  public void pasteCopiedCells()
    {
    int lastRow = styleTable.getRowCount() - 1;
    int lastCol = styleTable.getColumnCount() - 1;

    int col;

    int selectedRows[] = styleTable.getSelectedRows();
    int selectedCols[] = styleTable.getSelectedColumns();

    if( selectedRows.length == 0 || selectedCols.length == 0 || copiedCells.isEmpty() || ((Polylist)copiedCells.first()).isEmpty() )
      {
      return;
      }

    int colIndex = 0;
    Polylist columns = copiedCells;

    // copy copied cells cyclically from source columns and rows,
    // until we run out of target columns and rows

    while( colIndex < selectedCols.length )
      {
      col = selectedCols[colIndex];
      if( columns.isEmpty() )
        {
        columns = copiedCells;
        }

      Polylist column = (Polylist)columns.first();

      int rowIndex = 0;
      while( rowIndex < selectedRows.length )
        {
        int row = selectedRows[rowIndex];

        if( column.isEmpty() )
          {
          column = (Polylist)columns.first();
          }

        if( getTableModel().isCellEditable(row, col) )
          {
          // Allow pasting of all cells, not just pattern cells
          // (although these may cause errors)
          Object first = column.first();
          if( first instanceof Polylist )
            {
            setCell(((Polylist)first).toStringSansParens(), row, col, SILENT);
            }
          else
            {
            setCell(first.toString(), row, col, SILENT);
            }
          }
        rowIndex++;
        column = column.rest();
        }
      columns = columns.rest();
      colIndex++;
      }
    }

 
  /**
   * General interface for setting values in cells
  @param text used to determine the contents of the cell
  @param row row of the cell
  @param column column of the cell
  @param play whether or not to play the contents, if playable
  @return the object actually put in the cell
   */
  public Object setCell(String text, int row, int column, boolean play)
    {
    Object oldContents = styleTable.getValueAt(row, column);

    // The actual value set in the cell depends on the indices of the cell
    // and the kind of value expected.
    if( isIncludeCell(row, column) )
      {
      Boolean value;
      try
        {
        value = new Boolean(text);
        }
      catch( Exception e )
        {
        value = new Boolean(false);
        }
      styleTable.setValueAt(value, row, column);
      return value;
      }

    if( oldContents instanceof DrumRuleDisplay )
      {
      DrumRuleDisplay drumRule = (DrumRuleDisplay)oldContents;
      DrumPatternDisplay drumPattern =
              (DrumPatternDisplay)allDrumPatterns.get(column);
      drumPattern.removeRule(drumRule);
      //System.out.println("removing rule, count becomes: " + drumPattern.getRuleCount());
      if( drumPattern.getRuleCount() == 0 )
        {
        allDrumPatterns.removePattern(drumPattern);
        getTableModel().setDrumPatternBeats(0, column);
        }
      }
    else if( oldContents instanceof ChordPatternDisplay )
      {
      allChordPatterns.removePattern((ChordPatternDisplay)oldContents);
      getTableModel().setChordPatternBeats(0, column);
      }
    else if( oldContents instanceof BassPatternDisplay )
      {
      allBassPatterns.removePattern((BassPatternDisplay)oldContents);
      getTableModel().setBassPatternBeats(0, column);
      }

    if( text == null || text.trim().equals(EMPTY) )
      {
      Object newValue = StyleCellRenderer.NULL_DATA_RENDERING;
      styleTable.setValueAt(newValue, row, column);
      return newValue;
      }

    Object beingSet;
    Float weight;
    // FIX: This replicates stuff in StyleCellEditor.java. The latter should be changed to use this code.
    // Also, some of the branches below can be collapsed into one.
    if( isChordCell(row, column) )
      {
      ChordPatternDisplay contents = newChordPatternDisplay(column, text);
      beingSet = contents;
      Object weightCell =
              styleTable.getValueAt(StyleTableModel.CHORD_PATTERN_WEIGHT_ROW,
              column);
      weight = new Float(weightCell.toString());  // FIX: Check that it is the right type of value

      contents.setWeight(weight.floatValue());
      double beats = contents.getBeats();
      styleTable.setValueAt(beats, StyleTableModel.CHORD_PATTERN_BEATS_ROW,
              column);

      if( play )
        {
          maybePlay(contents);
        }

      }
    else if( isBassCell(row, column) )
      {
      BassPatternDisplay contents = newBassPatternDisplay(column, text);
      beingSet = contents;
      Object weightCell =
              styleTable.getValueAt(StyleTableModel.BASS_PATTERN_WEIGHT_ROW,
              column);
      weight = new Float(weightCell.toString());  // FIX: Check that it is the right type of value
      contents.setWeight(weight.floatValue());
      double beats = contents.getBeats();
      styleTable.setValueAt(beats, StyleTableModel.BASS_PATTERN_BEATS_ROW,
              column);

      if( play )
        {
          maybePlay(contents);
        }

      }
    else if( isDrumCell(row, column) )
      {
      DrumRuleDisplay contents = newDrumRuleDisplay(row, column, text);
      beingSet = contents;

      DrumPatternDisplay pattern =
              (DrumPatternDisplay)getDrumPattern(column);
      double beats = pattern.getBeats();
      Object weightCell =
              styleTable.getValueAt(StyleTableModel.CHORD_PATTERN_WEIGHT_ROW,
              column);
      try
        {
        weight = new Float(weightCell.toString());  // FIX: Check that it is the right type of value
        }
      catch( Exception e )
        {
        weight = new Float(0);
        }

      pattern.setWeight(weight.floatValue());

      if( play )
        {
          maybePlay(contents);
        }

      styleTable.setValueAt(beats, StyleTableModel.DRUM_PATTERN_BEATS_ROW,
              column);
      }
    else
      {
      beingSet = text;
      }

    styleTable.setValueAt(beingSet, row, column);
    updateMirror(row, column, beingSet); // styleTable.getValueAt(row, column));
    return beingSet;
    }

  /**
   * Adds an empty bass/drum/chord PatternDisplay objecct to the currently visible tab
   */
  public void addPattern()
    {
    //Remove panel displaying "no bass patterns found" if it exists.
    if( bassTabPanel.isVisible() )
      {
      try
        {
        Component com = bassHolderPane.getComponent(0);
        try
          {
          BassPatternDisplay b = (BassPatternDisplay)com;
          }
        catch( ClassCastException e )
          {
          //WARNING: If, in future, items other than BassPatternDisplay are included, this method of removal must change.
          //Therefore b is the JPanel that says "No bass patterns found."  We need to delete it before adding.
          bassHolderPane.remove(com);
          }
        }
      catch( ArrayIndexOutOfBoundsException e )
        {
        }

      BassPatternDisplay b = new BassPatternDisplay(notate, cm, this);
      b.setTitleNumber(bassHolderPane.getComponentCount() + 1);
      b.setDisplayText("B4");
      bassHolderPane.add(b);
      b.checkStatus();
      bassHolderPane.updateUI();
      }
    else if( drumTabPanel.isVisible() )
      {
      try
        {
        Component com = drumHolderPane.getComponent(0);
        try
          {
          DrumPatternDisplay d = (DrumPatternDisplay)com;
          }
        catch( ClassCastException e )
          {
          //WARNING: If, in future, items other than BassPatternDisplay are included, this method of removal must change.
          //Therefore b is the JPanel that says "No bass patterns found."  We need to delete it before adding.
          drumHolderPane.remove(com);
          }
        }
      catch( ArrayIndexOutOfBoundsException e )
        {
        }
      DrumPatternDisplay d = new DrumPatternDisplay(notate, cm, this);
      // not desired, I think d.fill();
      d.setTitleNumber(drumHolderPane.getComponentCount() + 1);
      drumHolderPane.add(d);
      d.checkStatus();
      drumHolderPane.updateUI();
      }
    else if( chordTabPanel.isVisible() )
      {
      try
        {
        Component com = chordHolderPane.getComponent(0);
        try
          {
          ChordPatternDisplay c = (ChordPatternDisplay)com;
          }
        catch( ClassCastException e )
          {
          //WARNING: If, in future, items other than BassPatternDisplay are included, this method of removal must change.
          //Therefore b is the JPanel that says "No bass patterns found."  We need to delete it before adding.
          chordHolderPane.remove(com);
          }
        }
      catch( ArrayIndexOutOfBoundsException e )
        {
        }

      ChordPatternDisplay c = new ChordPatternDisplay(notate, cm, this);
      c.setTitleNumber(chordHolderPane.getComponentCount() + 1);
      c.setDisplayText("X4");
      chordHolderPane.add(c);
      c.checkStatus();
      chordHolderPane.updateUI();
      }
    }

  public Long getInstrumentNumberByRow(int row)
  {
      return getTableModel().getInstrumentNumbers().get(row);
  }
  
  public int getRowByDrumInstrumentNumber(int number)
    {
      String instrumentName = MIDIBeast.spacelessDrumNameFromNumber(number);
      
      int row = findInstrumentRow(instrumentName);

      //System.out.println("instrumentNumber = " + number + ", name = " + instrumentName + ", row = " + row);
      
      return row;
    }

  public boolean isIncludeCell(int row, int col)
    {
    return col == StyleTableModel.INSTRUMENT_INCLUDE_COLUMN;
    }

  public boolean isInstrumentIncluded(int row)
    {
    Object value = styleTable.getValueAt(row, StyleTableModel.INSTRUMENT_INCLUDE_COLUMN);
    
    //System.out.println("row = " + row + " value = " + value);
    return styleTable.getValueAt(row, StyleTableModel.INSTRUMENT_INCLUDE_COLUMN).equals(Boolean.TRUE);
    }

  public boolean isChordCell(int row, int col)
    {
    return row == StyleTableModel.CHORD_PATTERN_ROW;
    }

  public boolean isBassCell(int row, int col)
    {
    return row == StyleTableModel.BASS_PATTERN_ROW;
    }

  public boolean isDrumCell(int row, int col)
    {
    return getTableModel().isDrumCell(row, col);
    }

  public boolean isPatternCell(int row, int col)
    {
    return isDrumCell(row, col) || isBassCell(row, col) || isChordCell(row, col);
    }

 

  /**
   * Asks user for a .mid and .ls file, then runs the generating classes for bass, drums, and chords
   * and displays the results.
   */
  public void generateStyleFromMidi()
    {
    //Prompt user to save if generating will lose changes
    int feedback = -1;
    if( cm.changedSinceLastSave() )
      {
      feedback = unsavedStyle();
      }

    if( feedback != 0 )
      {
      reset();

      //Get files from user
      String chordFile = "", midiFile = "";
      String nameForDisplay;
      File midiFileEntire = null;
      int midiChoice = midiFileChooser.showOpenDialog(this);
      if( midiChoice == JFileChooser.CANCEL_OPTION )
        {
        return;
        }
      if( midiChoice == JFileChooser.APPROVE_OPTION )
        {
        midiFileEntire = midiFileChooser.getSelectedFile();
        midiFile = midiFileEntire.getAbsolutePath();
        }
      nameForDisplay = midiFileChooser.getSelectedFile().getName();
          if (MIDIBeast.importDrums) {
              // use chord file with same name as midi file, IF one exists
              String path = midiFileEntire.getAbsolutePath();
              String stem = path.substring(0, path.length()-4);
              // stem hacks off .MID extension: FIX
              chordFile = stem + ".ls";
              File testChordFile = new File(chordFile);
              if (!testChordFile.exists()) {
                  int chordChoice = chordFileChooser.showOpenDialog(this);
                  if (chordChoice == JFileChooser.CANCEL_OPTION) {
                      return;
                  }
                  if (chordChoice == JFileChooser.APPROVE_OPTION) {
                      chordFile = chordFileChooser.getSelectedFile().getAbsolutePath();
                  }
              }
          }

      MIDIBeast.initialize(midiFile, chordFile);

      newStyle();
      newTable();

      /*
      generationProgress.setLocationRelativeTo(this);
      generationProgress.setSize(400, 300);
      generationProgress.setVisible(true);  // Show the "Generating style..." JDialog.
      stylePic.setVisible(true);
       */

      int minDuration = getMinDuration();
      
      // Note: The order of pattern generation (drums, bass, chords ) needs to be invariant now.

      //Generate drum patterns
      if( MIDIBeast.importDrums )
        {
        try
          {
          RepresentativeDrumRules d = new RepresentativeDrumRules();
          MIDIBeast.repDrumRules = d;
          if( MIDIBeast.showExtraction )
            {
            ExtractionEditor drumExtraction = new ExtractionEditor(null,
                    false, this, cm, 1);
            drumExtraction.setLocationRelativeTo(this);
            drumExtraction.setVisible(true);
            }
          else
            {
            loadDrumPatterns(d.getRepresentativePatterns());
            }
          }
        catch( Exception e )
          {
          //messages are already recorded by the other drum classes
          }
        }

      //Generate bass patterns
      if( MIDIBeast.importBass )
        {
        try
          {
          RepresentativeBassRules r = new RepresentativeBassRules();
          MIDIBeast.repBassRules = r;
          if( MIDIBeast.showExtraction )
            {
            ExtractionEditor bassExtraction = new ExtractionEditor(null,
                    false, this, cm, 0);
            bassExtraction.setLocationRelativeTo(this);
            bassExtraction.setVisible(true);
            }
          else
            {
            loadBassPatterns(MIDIBeast.repBassRules.getBassRules());
            }
          }
        catch( Exception e )
          {
          //messages are already recorded by the other bass classes
          }
        }

      //Generate chord patterns
      if( MIDIBeast.importChords )
        {
        try
          {
          RepresentativeChordRules c = new RepresentativeChordRules(minDuration);
          MIDIBeast.repChordRules = c;
          if( MIDIBeast.showExtraction )
            {
            ExtractionEditor chordExtraction = new ExtractionEditor(null,
                    false, this, cm, 2, minDuration);
            chordExtraction.setLocationRelativeTo(this);
            chordExtraction.setVisible(true);
            }
          else
            {
            loadChordPatterns(c.getChordPattern());
            }
          }
        catch( Exception e )
          {
          //messages are already recorded by the other chord classes
          }
        }


      generationProgress.dispose(); // close the JDialogue that pops up to show the progress bar, once everything has been generated and displayed.            

      //Display any errors that occurred during generation.
      ArrayList<String> errors = MIDIBeast.errors;
      if( errors.size() > 0 )
        {
        String allErrors = "";
        for( int i = 0; i < errors.size(); i++ )
          {
          allErrors += "\n" + errors.get(i);
          }
        ErrorLog.setDialogTitle("Generation Errors");
        ErrorLog.log(ErrorLog.WARNING, allErrors);
        ErrorLog.setDialogTitle(null);
        }

      cm.changedSinceLastSave(true);
      refreshAll();

      this.setTitle("Style Editor & Extractor - Generated style from " + nameForDisplay);
      savedStyle = null; // to prevent accidental clobering
      }
    }
  
  /**
   * Get the minimum duration for style extraction from the text field.
   @return the minimum duration to be used for style extraction
   */
  
  private int getMinDuration()
  {
  String minDurationString = minDurationTF.getText();
      
  // Note that getDuration just returns a default if the argument is ill-formed.
  // This should be fixed eventually.
      
  int minDuration = Duration.getDuration(minDurationString);
   
  return minDuration;
  }

  /** This method is called from within the constructor to
   * initialize the form.
   * WARNING: Do NOT modify this code. The content of this method is
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        generatePref = new javax.swing.JDialog();
        showExtractionCheckBox = new javax.swing.JCheckBox();
        chordTonesCheckBox = new javax.swing.JCheckBox();
        mergeBassRestsCheckBox = new javax.swing.JCheckBox();
        jPanel1 = new javax.swing.JPanel();
        importBassCheckBox = new javax.swing.JCheckBox();
        importDrumCheckBox = new javax.swing.JCheckBox();
        importChordCheckBox = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        maxPatternLengthComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        maxChordPatternLengthComboBox = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        maxDrumPatternLengthComboBox = new javax.swing.JComboBox();
        jPanel4 = new javax.swing.JPanel();
        minDurationTF = new javax.swing.JTextField();
        generationProgress = new javax.swing.JDialog();
        stylePic = new javax.swing.JLabel();
        helpDialog = new javax.swing.JDialog();
        helpTabbedPane = new javax.swing.JTabbedPane();
        generalPane = new javax.swing.JScrollPane();
        patternHelp = new javax.swing.JTextArea();
        editingPane = new javax.swing.JScrollPane();
        menuList1 = new javax.swing.JTextArea();
        fileMenuPane = new javax.swing.JScrollPane();
        menuList = new javax.swing.JTextArea();
        bassPane = new javax.swing.JScrollPane();
        bassText = new javax.swing.JTextArea();
        chordPane = new javax.swing.JScrollPane();
        chordText = new javax.swing.JTextArea();
        percussionPane = new javax.swing.JScrollPane();
        percussionText = new javax.swing.JTextArea();
        fileFormatPane = new javax.swing.JScrollPane();
        fileFormatList = new javax.swing.JTextArea();
        extractionPane = new javax.swing.JScrollPane();
        extractionList = new javax.swing.JTextArea();
        commentsPanel = new javax.swing.JPanel();
        commentScrollPane = new javax.swing.JScrollPane();
        commentArea = new javax.swing.JTextArea();
        globalAttrPanel = new javax.swing.JPanel();
        bassAttrPanel = new javax.swing.JPanel();
        bassHighLabel = new javax.swing.JLabel();
        bassLowLabel = new javax.swing.JLabel();
        bassBaseLabel = new javax.swing.JLabel();
        bassHighNote = new javax.swing.JComboBox();
        bassLowNote = new javax.swing.JComboBox();
        bassBaseNote = new javax.swing.JComboBox();
        bassHighOctave = new javax.swing.JSpinner();
        bassLowOctave = new javax.swing.JSpinner();
        bassBaseOctave = new javax.swing.JSpinner();
        bassOctaveLabel = new javax.swing.JLabel();
        chordAttrPanel = new javax.swing.JPanel();
        chordHighLabel = new javax.swing.JLabel();
        chordLowLabel = new javax.swing.JLabel();
        chordHighNote = new javax.swing.JComboBox();
        chordLowNote = new javax.swing.JComboBox();
        chordHighOctave = new javax.swing.JSpinner();
        chordLowOctave = new javax.swing.JSpinner();
        chordOctaveLabel = new javax.swing.JLabel();
        voicingLabel = new javax.swing.JLabel();
        voicingType = new javax.swing.JComboBox();
        melodySwingPanel = new javax.swing.JPanel();
        swingTextField = new javax.swing.JTextField();
        compSwingPanel1 = new javax.swing.JPanel();
        accompanimentSwingTextField = new javax.swing.JTextField();
        chordPanel = new javax.swing.JPanel();
        muteChordToggle = new javax.swing.JToggleButton();
        chordPitchComboBox = new javax.swing.JComboBox();
        chordTypeComboBox = new javax.swing.JComboBox();
        playPanel = new javax.swing.JPanel();
        playToggle = new javax.swing.JToggleButton();
        volLabel = new javax.swing.JLabel();
        masterVolumeSlider = new javax.swing.JSlider();
        tempoComboBox = new javax.swing.JComboBox();
        bpmLabel = new javax.swing.JLabel();
        toolbarPanel = new javax.swing.JPanel();
        filePanel = new javax.swing.JPanel();
        newButton = new javax.swing.JButton();
        openButton = new javax.swing.JButton();
        saveButton = new javax.swing.JButton();
        columnPanel = new javax.swing.JPanel();
        cutColumnButton = new javax.swing.JButton();
        copyColumnButton = new javax.swing.JButton();
        pasteColumnButton = new javax.swing.JButton();
        addColumnButton = new javax.swing.JButton();
        rowPanel = new javax.swing.JPanel();
        cutRowButton = new javax.swing.JButton();
        copyRowButton = new javax.swing.JButton();
        pasteRowButton = new javax.swing.JButton();
        newRowButton = new javax.swing.JButton();
        cellsPanel = new javax.swing.JPanel();
        cutCellsButton = new javax.swing.JButton();
        copyCellsButton = new javax.swing.JButton();
        pasteCellsButton = new javax.swing.JButton();
        timeSigPanel = new javax.swing.JPanel();
        numField = new javax.swing.JTextField();
        slashLabel = new javax.swing.JLabel();
        denomField = new javax.swing.JTextField();
        remotePanel = new javax.swing.JPanel();
        saveStyleBtn1 = new javax.swing.JButton();
        playBtn = new javax.swing.JButton();
        pauseBtn = new javax.swing.JToggleButton();
        stopBtn = new javax.swing.JButton();
        statusPanel = new javax.swing.JPanel();
        styleEditorStatusTF = new javax.swing.JTextField();
        clipboardPanel = new javax.swing.JPanel();
        clipboardTextField = new javax.swing.JTextField();
        stylePanel = new javax.swing.JPanel();
        rowLabel = new javax.swing.JLabel();
        columnLabel = new javax.swing.JLabel();
        beatsLabel = new javax.swing.JLabel();
        contentLabel = new javax.swing.JLabel();
        beatsField2 = new javax.swing.JTextField();
        rowField2 = new javax.swing.JTextField();
        columnField2 = new javax.swing.JTextField();
        styleTextField2 = new javax.swing.JTextField();
        beatsField1 = new javax.swing.JTextField();
        rowField1 = new javax.swing.JTextField();
        columnField1 = new javax.swing.JTextField();
        styleTextField1 = new javax.swing.JTextField();
        beatsField0 = new javax.swing.JTextField();
        rowField0 = new javax.swing.JTextField();
        columnField0 = new javax.swing.JTextField();
        styleTextField0 = new javax.swing.JTextField();
        editInstructionsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        styleScrollpane = new javax.swing.JScrollPane();
        panelInStyleScrollpane = new javax.swing.JPanel();
        styleTable = new javax.swing.JTable();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextPane1 = new javax.swing.JTextPane();
        closeButtonPanel = new javax.swing.JPanel();
        stopPlaying = new javax.swing.JButton();
        closeBtn = new javax.swing.JButton();
        saveStyleBtn = new javax.swing.JButton();
        styleSpecificationPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        bassTabPanel = new javax.swing.JPanel();
        bassStyleSpecScrollPane = new javax.swing.JScrollPane();
        bassHolderPane = new javax.swing.JPanel();
        drumTabPanel = new javax.swing.JPanel();
        drumStyleSpecScrollPane = new javax.swing.JScrollPane();
        drumHolderPane = new javax.swing.JPanel();
        chordTabPanel = new javax.swing.JPanel();
        chordStyleSpecScrollPane = new javax.swing.JScrollPane();
        chordHolderPane = new javax.swing.JPanel();
        styMenuBar = new javax.swing.JMenuBar();
        styFile = new javax.swing.JMenu();
        newStyleMI = new javax.swing.JMenuItem();
        openStyleMI = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        saveStyleMI = new javax.swing.JMenuItem();
        saveStyleAs = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JSeparator();
        exitStyleGenMI = new javax.swing.JMenuItem();
        styEdit = new javax.swing.JMenu();
        pianoRollCheckBox = new javax.swing.JCheckBoxMenuItem();
        trackWithPianoRoll = new javax.swing.JCheckBoxMenuItem();
        cutCellsMI = new javax.swing.JMenuItem();
        copyCellsMI = new javax.swing.JMenuItem();
        pasteCellsMI = new javax.swing.JMenuItem();
        styGenerate = new javax.swing.JMenu();
        generateMI = new javax.swing.JMenuItem();
        generatePrefMI = new javax.swing.JMenuItem();
        styHelp = new javax.swing.JMenu();
        styHelpMI = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();

        generatePref.getContentPane().setLayout(new java.awt.GridBagLayout());

        showExtractionCheckBox.setText(" Show Extraction");
        showExtractionCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        generatePref.getContentPane().add(showExtractionCheckBox, gridBagConstraints);

        chordTonesCheckBox.setSelected(true);
        chordTonesCheckBox.setText(" Bass Chord Tones");
        chordTonesCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        chordTonesCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordTonesCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        generatePref.getContentPane().add(chordTonesCheckBox, gridBagConstraints);

        mergeBassRestsCheckBox.setText("Merge Bass Rests");
        mergeBassRestsCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        generatePref.getContentPane().add(mergeBassRestsCheckBox, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Import"));
        jPanel1.setMinimumSize(new java.awt.Dimension(200, 112));
        jPanel1.setPreferredSize(new java.awt.Dimension(200, 121));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        importBassCheckBox.setSelected(true);
        importBassCheckBox.setText("Bass");
        importBassCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 16);
        jPanel1.add(importBassCheckBox, gridBagConstraints);

        importDrumCheckBox.setSelected(true);
        importDrumCheckBox.setText("Drums");
        importDrumCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        importDrumCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importDrumCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 8);
        jPanel1.add(importDrumCheckBox, gridBagConstraints);

        importChordCheckBox.setSelected(true);
        importChordCheckBox.setText("Chords");
        importChordCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 9, 5);
        jPanel1.add(importChordCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 5, 0);
        generatePref.getContentPane().add(jPanel1, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(22, 0, 0, 0);
        generatePref.getContentPane().add(okButton, gridBagConstraints);

        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(22, 86, 0, 0);
        generatePref.getContentPane().add(cancelButton, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Max Pattern Length"));
        jPanel3.setMinimumSize(new java.awt.Dimension(200, 112));
        jPanel3.setPreferredSize(new java.awt.Dimension(200, 200));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        jLabel4.setText("Bass");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel4, gridBagConstraints);

        maxPatternLengthComboBox.setEditable(true);
        maxPatternLengthComboBox.setMaximumRowCount(17);
        maxPatternLengthComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
        maxPatternLengthComboBox.setSelectedIndex(4);
        maxPatternLengthComboBox.setMinimumSize(new java.awt.Dimension(70, 22));
        maxPatternLengthComboBox.setPreferredSize(new java.awt.Dimension(70, 22));
        maxPatternLengthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxPatternLengthComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 17, 0, 0);
        jPanel3.add(maxPatternLengthComboBox, gridBagConstraints);

        jLabel5.setText("Chord");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel5, gridBagConstraints);

        maxChordPatternLengthComboBox.setEditable(true);
        maxChordPatternLengthComboBox.setMaximumRowCount(17);
        maxChordPatternLengthComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
        maxChordPatternLengthComboBox.setSelectedIndex(4);
        maxChordPatternLengthComboBox.setMinimumSize(new java.awt.Dimension(70, 22));
        maxChordPatternLengthComboBox.setPreferredSize(new java.awt.Dimension(70, 22));
        maxChordPatternLengthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxChordPatternLengthComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 17, 0, 0);
        jPanel3.add(maxChordPatternLengthComboBox, gridBagConstraints);

        jLabel6.setText("Drum");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel3.add(jLabel6, gridBagConstraints);

        maxDrumPatternLengthComboBox.setEditable(true);
        maxDrumPatternLengthComboBox.setMaximumRowCount(17);
        maxDrumPatternLengthComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "None", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15", "16" }));
        maxDrumPatternLengthComboBox.setSelectedIndex(4);
        maxDrumPatternLengthComboBox.setMinimumSize(new java.awt.Dimension(70, 22));
        maxDrumPatternLengthComboBox.setPreferredSize(new java.awt.Dimension(70, 22));
        maxDrumPatternLengthComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxDrumPatternLengthComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 17, 0, 0);
        jPanel3.add(maxDrumPatternLengthComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(9, 0, 0, 0);
        generatePref.getContentPane().add(jPanel3, gridBagConstraints);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Min Note Duration"));
        jPanel4.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanel4.setPreferredSize(new java.awt.Dimension(200, 50));
        jPanel4.setLayout(new java.awt.GridBagLayout());

        minDurationTF.setText("16");
        minDurationTF.setMinimumSize(new java.awt.Dimension(40, 19));
        minDurationTF.setPreferredSize(new java.awt.Dimension(40, 19));
        minDurationTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minDurationTFActionPerformed(evt);
            }
        });
        jPanel4.add(minDurationTF, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(9, 0, 0, 0);
        generatePref.getContentPane().add(jPanel4, gridBagConstraints);

        generationProgress.setTitle("Extracting style...");
        generationProgress.setAlwaysOnTop(true);
        generationProgress.setCursor(new java.awt.Cursor(java.awt.Cursor.WAIT_CURSOR));
        generationProgress.getContentPane().setLayout(new java.awt.GridBagLayout());

        stylePic.setBackground(new java.awt.Color(255, 255, 255));
        stylePic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        stylePic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/style.png"))); // NOI18N
        stylePic.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stylePic.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 352;
        gridBagConstraints.ipady = 252;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        generationProgress.getContentPane().add(stylePic, gridBagConstraints);

        helpDialog.setTitle("Style Editor Help");
        helpDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        helpTabbedPane.setToolTipText("Style Editor & Extractor Help");
        helpTabbedPane.setMinimumSize(new java.awt.Dimension(500, 700));
        helpTabbedPane.setPreferredSize(new java.awt.Dimension(600, 800));

        generalPane.setToolTipText("General Style Patterns");
        generalPane.setMinimumSize(new java.awt.Dimension(500, 800));
        generalPane.setPreferredSize(new java.awt.Dimension(600, 900));

        patternHelp.setColumns(20);
        patternHelp.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        patternHelp.setRows(5);
        patternHelp.setText("Help for the Patterns in Style Editor & Extractor\n\nEach pattern is defined by a set of rules.  The rules are similar to\nleadsheet notation, except the pitch is replaced with a capital letter\nindicating the rule.  Each pattern also has a weight that determines how\noften that pattern is used in relation to other patterns of the same length.\n\nNote: The notation is case-sensitive.\n\nThe duration of a specific note in the pattern is specified by numbers, \nas in the leadsheet notation:\n\n    1  = whole note\n    2  = half note\n    4  = quarter note\n    8  = eighth note\n    16 = sixteenth note\n    32 = thirty-second note\n\nFollowing a number by a dot (.) multiplies the value by 1.5. \n\nFollowing a number by /3 gives the value of a triplet (2/3 of the original value).\n\n**Swing values\n   - There are two forms of swing: melody swing and accompaniment swing.\n   - The accompaniment swing controls the swing value of the bass, chords, and percussion.\n   - The melody swing controls the swing of the melody, which is what appears on the lead sheet.\n   - The reason for there being two swings is because of the style extraction process.\n\tWhen a style is extracted, the rhythms are taken verbatim from the MIDI--and,\n\tin the case that there is some swing in the MIDI performance, there is already\n\ta swing value incorporated into the extracted accompaniment style.\n\tThus, for the melody to match the accompaniment, it is necessary to be\n\table to adjust both the swing for the accompaniment as well as the swing for\n\tthe melody.\n\n\n**Editing Patterns in Style Editor & Extractor\n   - Double click on the table cell which you want to edit.\n   - To exclude certain patterns from a style, uncheck the include button \n      or delete with the cut pattern option.\n   - Preview each pattern by clicking the table cell once.  Make sure the \"play\" toggle is set to on.\n\n");
        generalPane.setViewportView(patternHelp);

        helpTabbedPane.addTab("General", generalPane);

        editingPane.setMinimumSize(new java.awt.Dimension(500, 800));
        editingPane.setPreferredSize(new java.awt.Dimension(600, 900));

        menuList1.setColumns(20);
        menuList1.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        menuList1.setRows(5);
        menuList1.setText("Menu items:\n\n* Playing\n       Clicking a cell ............................................ plays the corresponding pattern\n       Control-clicking a precussion cell ......................... plays all percussion patterns\n       in that column together, which is the way the pattern will sound when used in accompaniment.\n\nNOTE: Currently the user must ensure that all patterns in a column are the same length (number of beats).\nIn the future, we will provide better automation for this.\n\n*Edit\n       Add Column................. add a new pattern column\n       Add Row.................... add a new percussion instrument row \n       Copy Pattern............... copy the selected pattern\n       Cut Pattern................ cut the selected pattern\n       Paste...................... paste the pattern\n \nArbitrary blocks in the spreadsheet can be cut and pasted. \nUnlike some spreadsheets, however, you must pre-select the area for pasting.\nThe cut or copied cell contents will tile the area into which you paste.\nIf the cut or copied block is larger than the paste area, it will be truncated.\n");
        editingPane.setViewportView(menuList1);

        helpTabbedPane.addTab("Editing cells", editingPane);

        fileMenuPane.setMinimumSize(new java.awt.Dimension(500, 800));
        fileMenuPane.setPreferredSize(new java.awt.Dimension(600, 900));

        menuList.setColumns(20);
        menuList.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        menuList.setRows(5);
        menuList.setText("Menu items:\n\n*File\n       New Style.................. start creating a new style\n       Open Style................. open an existing style\n       Save Style................. save the style that is currently open\n       Save Style As.............. save the style to a specific location\n       Exit....................... close Style Editor & Extractor\n\n*Extract\n       Extract Style from MIDI.... extract a style from pair of .mid and .ls files\n       Extraction Preferences..... preferences for the extraction option\n\n");
        fileMenuPane.setViewportView(menuList);

        helpTabbedPane.addTab("File menu items", fileMenuPane);

        bassPane.setToolTipText("About bass patterns");
        bassPane.setMinimumSize(new java.awt.Dimension(500, 800));
        bassPane.setPreferredSize(new java.awt.Dimension(600, 900));

        bassText.setColumns(20);
        bassText.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        bassText.setRows(5);
        bassText.setText("Bass Patterns\n\nBass patterns determine both the rhythm of the bassline and the type of\nnotes that are chosen.\n\nBass patterns use the following rules, where d is a duration, such as\n4 for a quarter-note, 8 for an eighth-note, etc.\n\nBd      The bass note (either the root of the chord, or the note specified\n        as part of a slash chord)\n\nCd      A note from the current chord\n\nSd      A note from the first scale associated with the current chord\n\nAd      A tone that approaches the bass note of the next chord\n\nNd      The bass note of the next chord (if at the end of a pattern\n        it will tie over to the next pattern)\n\nRd      A rest of duration d\n\n(X n d) A specific note from the first scale given for a chord in\n        the vocabulary. For example (X 5 4) will give a quarter-note\n        on the fifth-degree of the scale.\n");
        bassPane.setViewportView(bassText);

        helpTabbedPane.addTab("Bass", bassPane);

        chordPane.setToolTipText("About chord patterns");
        chordPane.setMinimumSize(new java.awt.Dimension(500, 800));
        chordPane.setPreferredSize(new java.awt.Dimension(600, 900));

        chordText.setColumns(20);
        chordText.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        chordText.setRows(5);
        chordText.setText("\n**Chord Patterns\n\nChord patterns determine only the rhythm of the chord accompaniment. (The \nvoicings to use are decided based on a voice-leading algorithm.)\n\nChord patterns use the following rules:\n\nX       Strike the chord\nR       A rest\n");
        chordPane.setViewportView(chordText);

        helpTabbedPane.addTab("Chords", chordPane);

        percussionPane.setToolTipText("About drum patterns");
        percussionPane.setMinimumSize(new java.awt.Dimension(500, 800));
        percussionPane.setPreferredSize(new java.awt.Dimension(600, 900));

        percussionText.setColumns(20);
        percussionText.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        percussionText.setRows(5);
        percussionText.setText("**Percussion Patterns\n\nPercussion patterns are a bit more complicated in that for one pattern you must\nspecify a drumline for each drum you want in the pattern.  Here is the\nformat for a drumline specification:\n\n(drum DRUM-MIDI-NUMBER RULE RULE ...)\n\nDrum patterns use the following rules:\n\nX       Strike the drum\nR       A rest\n\nGeneral MIDI Percussion Numbers:\n\n35      Acoustic Bass Drum      59      Ride Cymbal 2\n36      Bass Drum 1             60      Hi Bongo\n37      Side Stick              61      Low Bongo\n38      Acoustic Snare          62      Mute Hi Conga\n39      Hand Clap               63      Open Hi Conga\n40      Electric Snare          64      Low Conga\n41      Low Floor Tom           65      High Timbale\n42      Closed Hi-Hat           66      Low Timbale\n43      High Floor Tom          67      High Agogo\n44      Pedal Hi-Hat            68      Low Agogo\n45      Low Tom                 69      Cabasa\n46      Open Hi-Hat             70      Maracas\n47      Low-Mid Tom             71      Short Whistle\n48      Hi-Mid Tom              72      Long Whistle\n49      Crash Cymbal 1          73      Short Guiro\n50      High Tom                74      Long Guiro\n51      Ride Cymbal 1           75      Claves\n52      Chinese Cymbal          76      Hi Wood Block\n53      Ride Bell               77      Low Wood Block\n54      Tambourine              78      Mute Cuica\n55      Splash Cymbal           79      Open Cuica\n56      Cowbell                 80      Mute Triangle\n57      Crash Cymbal 2          81      Open Triangle\n58      Vibraslap\n");
        percussionPane.setViewportView(percussionText);

        helpTabbedPane.addTab("Percussion", percussionPane);

        fileFormatPane.setMinimumSize(new java.awt.Dimension(500, 800));
        fileFormatPane.setPreferredSize(new java.awt.Dimension(600, 900));

        fileFormatList.setColumns(20);
        fileFormatList.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        fileFormatList.setRows(5);
        fileFormatList.setText("Help for Style Notation\n\nStyles are responsible for Impro-Visor's playback accompaniment.  Styles \nare specified in the vocabulary file as a set of patterns for each instrument\nof the accompaniment.  There are also style parameters that determine how\nthe patterns are used.\n\nThese parameters are found in the Attributes tab in \nImpro-Visor's Style Editor & Extractor.\n\nStyle Parameters\n\nname            The name of the style\nswing           The swing value for the style\ncomp-swing\tThe swing value of the accompaniment part for the style\nbass-high       The highest note in the bass's range\nbass-low        The lowest note in the bass's range\nbass-base       A note that specifies the starting area for the bassline\nchord-high      The highest note in the chord's range\nchord-low       The lowest note in the chord's range\nvoicing-type    Sets the voicing type to be used\n\nExample Style\n\n(style\n    (name swing)\n    (swing 0.67)\n    (comp-swing 0.67)\n    \n    (bass-high g-)\n    (bass-low g---)\n    (bass-base c--)\n    \n    (bass-pattern (rules B4 S4 C4 A4) (weight 10))\n    (bass-pattern (rules B4 C4 C4 A4) (weight 5))\n    (bass-pattern (rules B4 S4 C4 S4) (weight 3))\n    (bass-pattern (rules B4 S4 C4 A8 A8) (weight 3))\n    (bass-pattern (rules B4 S4 C4 S8 A8) (weight 3))\n    (bass-pattern (rules B4 C4) (weight 5))\n    (bass-pattern (rules B4 C8 A8) (weight 5))\n    (bass-pattern (rules B4 S4) (weight 3))\n    (bass-pattern (rules B4) (weight 5))\n    (bass-pattern (rules B4 A4) (weight 2))\n    \n    (drum-pattern\n        (drum 51 X4 X8 X8 X4 X8 X8)\n        (weight 10)\n        )\n    (drum-pattern\n        (drum 51 X4 X8 X8)\n        (weight 10)\n        )\n    (drum-pattern\n        (drum 51 X4)\n        (weight 10)\n        )\n        \n    (voicing-type closed)\n    (chord-high a)\n    (chord-low c-)\n    \n    (chord-pattern (rules X1) (weight 7))\n    (chord-pattern (rules X2) (weight 7))\n    (chord-pattern (rules X4) (weight 7))\n    (chord-pattern (rules X2+4 X4) (weight 5))\n    (chord-pattern (rules X4 R2+4) (weight 5))\n    (chord-pattern (rules X2 R2) (weight 7))\n    (chord-pattern (rules R4) (weight 1))\n    (chord-pattern (rules R8 X8+4 X4 R4) (weight 3))\n    (chord-pattern (rules R4 X8+4 X8 R4) (weight 5))\n    (chord-pattern (rules X8+2 X4 X8) (weight 2))\n    )\n\nStyle Tips\n\n   * The lengths of the patterns are important.  The software will attempt\n     to fill space with the largest pattern it can.  Try to correspond pattern\n     lengths to the probable chord durations.  If chords will mostly last for\n     4 beats, then have a 4 beat pattern.  If chords will last for 8 beats,\n     the software can fill that in with two 4 beat patterns.  Having a 1 beat\n     pattern for each accompaniment instrument is a good idea as a failsafe\n     for unconventional chord durations.\n     (Note that the length of the pattern, or number of beats, is displayed\n       in the bar of each pattern.)\n\n   * Adding the parameter (no-style) to your style will cause the software\n     to leave out any bassline or drum part and play chords exactly where \n     they are specified on the leadsheet.\n\n   * Look to the styles included in the vocabulary file as a guide to\n     creating your own styles.\n\n   * Don't be afraid to experiment, but also don't be afraid to keep \n     things simple.\n");
        fileFormatPane.setViewportView(fileFormatList);

        helpTabbedPane.addTab("File format", fileFormatPane);

        extractionPane.setFont(new java.awt.Font("Lucida Sans Typewriter", 0, 13)); // NOI18N
        extractionPane.setMinimumSize(new java.awt.Dimension(500, 800));
        extractionPane.setPreferredSize(new java.awt.Dimension(600, 900));

        extractionList.setColumns(20);
        extractionList.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        extractionList.setRows(5);
        extractionList.setText("Style Extraction:\n\nThe process of extracting a style from a MIDI file attempts to capture the many\npatterns (also called \"rules\" herein) found in the performance, to obtain a\nselect number of patterns that best represent that style.  By picking\nExtraction->Extraction Preferences the user may change various aspects of this\nprocess.\n\n1. Bass Chord Tones\n\n    When this box is checked, it will cause bass notes to be classified as 'C'\n    in preference to '(X # d)' when applicable. Please refer to the bass bass section of this\n    help for more information on note classifications.  \n\n2. Merge Bass Rests\n\n    When this box is checked, rests in bass patterns will be removed and their\n    durations will be added to the notes preceeding them. This may lead to\n    smoother bass patterns.  \n\n3. Import\n\n    Boxes in this section that are not checked will not have patterns extracted.  \n\n4. Show Extraction\n\n    The process of extraction works by forming many candidate patterns from a MIDI file.\n    Those patterns are then put into groups of similar patterns. One pattern from each\n    group is then picked to represent that group. In this way, from many patterns, a\n    select few are able to represent the style of the song. By checking the show\n    extraction box, the user may see and interact with the grouping process.\n\nExtraction Editor \n\n1. Raw Rules\n    This pane shows every pattern that came from the MIDI file. Patterns are organized\n    by length and then by the group of which they are a part (a cluster). The add rule\n    button will add the pattern to the final group of selected pattern.  \n\n2. Selected Rules\n    This pane shows the selected patterns that came from the raw rules that will be\n    used to represent the style of the MIDI file. Patterns that the user does not want\n    included may be removed with the Remove Rule button.  \n\n3. Re-Generation\n    This button will cause the selected patterns to be created again. By changing\n    the following options, results differing results may be obtained:\n\n         a. Maximum Number of Clusters\n\n             By setting a maximum number of clusters, more patterns will be\n             put in the same group, resulting in fewer more unique select\n             patterns.\n\n         b. Start/End Beat\n\n            This changes where in the song the raw patterns are created from. \n            This attruibute is set by default to where the instrument\n            starts and stops.\n\n         c. Selected Part\n\n             This pane lists all instruments were found in the MIDI file. \n             Whichever instrument is selected is the one that will be used to\n             generate the raw patterns.   \n");
        extractionList.setMinimumSize(new java.awt.Dimension(632, 1200));
        extractionList.setPreferredSize(new java.awt.Dimension(632, 1200));
        extractionPane.setViewportView(extractionList);

        helpTabbedPane.addTab("Extraction", extractionPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        helpDialog.getContentPane().add(helpTabbedPane, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        commentsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Comments Saved with Style"));
        commentsPanel.setMinimumSize(new java.awt.Dimension(600, 80));
        commentsPanel.setPreferredSize(new java.awt.Dimension(900, 100));
        commentsPanel.setLayout(new java.awt.GridBagLayout());

        commentScrollPane.setMinimumSize(new java.awt.Dimension(600, 50));
        commentScrollPane.setPreferredSize(new java.awt.Dimension(900, 120));

        commentArea.setColumns(20);
        commentArea.setRows(5);
        commentArea.setToolTipText("Enter comments describing style here.");
        commentArea.setMinimumSize(new java.awt.Dimension(200, 100));
        commentArea.setPreferredSize(new java.awt.Dimension(900, 300));
        commentArea.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                commentAreaPropertyChange(evt);
            }
        });
        commentScrollPane.setViewportView(commentArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        commentsPanel.add(commentScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(commentsPanel, gridBagConstraints);

        globalAttrPanel.setFocusable(false);
        globalAttrPanel.setMinimumSize(new java.awt.Dimension(525, 130));
        globalAttrPanel.setPreferredSize(new java.awt.Dimension(525, 130));
        globalAttrPanel.setLayout(new java.awt.GridBagLayout());

        bassAttrPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Bass Attributes"));
        bassAttrPanel.setMinimumSize(new java.awt.Dimension(250, 110));
        bassAttrPanel.setPreferredSize(new java.awt.Dimension(250, 115));
        bassAttrPanel.setLayout(new java.awt.GridBagLayout());

        bassHighLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        bassHighLabel.setText("High:");
        bassHighLabel.setToolTipText("The upper range for the bass part.");
        bassHighLabel.setMaximumSize(new java.awt.Dimension(64, 14));
        bassHighLabel.setMinimumSize(new java.awt.Dimension(64, 14));
        bassHighLabel.setPreferredSize(new java.awt.Dimension(64, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        bassAttrPanel.add(bassHighLabel, gridBagConstraints);

        bassLowLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        bassLowLabel.setText("Low:");
        bassLowLabel.setToolTipText("The lower range for the base part.");
        bassLowLabel.setMaximumSize(new java.awt.Dimension(61, 14));
        bassLowLabel.setMinimumSize(new java.awt.Dimension(61, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        bassAttrPanel.add(bassLowLabel, gridBagConstraints);

        bassBaseLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        bassBaseLabel.setText("Nominal:");
        bassBaseLabel.setToolTipText("The base bass note from which to start a bass line.");
        bassBaseLabel.setMaximumSize(new java.awt.Dimension(66, 14));
        bassBaseLabel.setMinimumSize(new java.awt.Dimension(66, 14));
        bassBaseLabel.setPreferredSize(new java.awt.Dimension(66, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 3, 0, 0);
        bassAttrPanel.add(bassBaseLabel, gridBagConstraints);
        bassBaseLabel.getAccessibleContext().setAccessibleName("Bass Nominal:");

        bassHighNote.setMaximumRowCount(12);
        bassHighNote.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "b", "a#", "a", "g#", "g", "f#", "f", "e", "d#", "d", "c#", "c" }));
        bassHighNote.setSelectedIndex(11);
        bassHighNote.setMinimumSize(new java.awt.Dimension(70, 22));
        bassHighNote.setPreferredSize(new java.awt.Dimension(70, 22));
        bassHighNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassHighNoteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        bassAttrPanel.add(bassHighNote, gridBagConstraints);

        bassLowNote.setMaximumRowCount(12);
        bassLowNote.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "b", "a#", "a", "g#", "g", "f#", "f", "e", "d#", "d", "c#", "c" }));
        bassLowNote.setSelectedIndex(11);
        bassLowNote.setMinimumSize(new java.awt.Dimension(70, 22));
        bassLowNote.setPreferredSize(new java.awt.Dimension(70, 22));
        bassLowNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassLowNoteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        bassAttrPanel.add(bassLowNote, gridBagConstraints);

        bassBaseNote.setMaximumRowCount(12);
        bassBaseNote.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "b", "a#", "a", "g#", "g", "f#", "f", "e", "d#", "d", "c#", "c" }));
        bassBaseNote.setSelectedItem("g");
        bassBaseNote.setMinimumSize(new java.awt.Dimension(70, 22));
        bassBaseNote.setPreferredSize(new java.awt.Dimension(70, 22));
        bassBaseNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassBaseNoteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        bassAttrPanel.add(bassBaseNote, gridBagConstraints);

        bassHighOctave.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        bassHighOctave.setMinimumSize(new java.awt.Dimension(100, 18));
        bassHighOctave.setPreferredSize(new java.awt.Dimension(100, 18));
        bassHighOctave.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bassHighOctaveStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        bassAttrPanel.add(bassHighOctave, gridBagConstraints);

        bassLowOctave.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        bassLowOctave.setMinimumSize(new java.awt.Dimension(100, 18));
        bassLowOctave.setPreferredSize(new java.awt.Dimension(100, 18));
        bassLowOctave.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bassLowOctaveStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 2, 0, 0);
        bassAttrPanel.add(bassLowOctave, gridBagConstraints);

        bassBaseOctave.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        bassBaseOctave.setMinimumSize(new java.awt.Dimension(100, 18));
        bassBaseOctave.setPreferredSize(new java.awt.Dimension(100, 18));
        bassBaseOctave.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bassBaseOctaveStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 2, 0, 0);
        bassAttrPanel.add(bassBaseOctave, gridBagConstraints);

        bassOctaveLabel.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
        bassOctaveLabel.setText("Octave:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 4, 0);
        bassAttrPanel.add(bassOctaveLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        globalAttrPanel.add(bassAttrPanel, gridBagConstraints);

        chordAttrPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Attributes"));
        chordAttrPanel.setMinimumSize(new java.awt.Dimension(250, 110));
        chordAttrPanel.setPreferredSize(new java.awt.Dimension(250, 115));
        chordAttrPanel.setLayout(new java.awt.GridBagLayout());

        chordHighLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chordHighLabel.setText("High:");
        chordHighLabel.setToolTipText("The upper range for the chord part.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        chordAttrPanel.add(chordHighLabel, gridBagConstraints);

        chordLowLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        chordLowLabel.setText("Low:");
        chordLowLabel.setToolTipText("The lower range for the chord part.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        chordAttrPanel.add(chordLowLabel, gridBagConstraints);

        chordHighNote.setMaximumRowCount(12);
        chordHighNote.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "b", "a#", "a", "g#", "g", "f#", "f", "e", "d#", "d", "c#", "c" }));
        chordHighNote.setSelectedIndex(11);
        chordHighNote.setMinimumSize(new java.awt.Dimension(70, 22));
        chordHighNote.setPreferredSize(new java.awt.Dimension(70, 22));
        chordHighNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordHighNoteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 7, 0, 0);
        chordAttrPanel.add(chordHighNote, gridBagConstraints);

        chordLowNote.setMaximumRowCount(12);
        chordLowNote.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "b", "a#", "a", "g#", "g", "f#", "f", "e", "d#", "d", "c#", "c" }));
        chordLowNote.setSelectedIndex(11);
        chordLowNote.setMinimumSize(new java.awt.Dimension(52, 22));
        chordLowNote.setPreferredSize(new java.awt.Dimension(56, 22));
        chordLowNote.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordLowNoteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        chordAttrPanel.add(chordLowNote, gridBagConstraints);

        chordHighOctave.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        chordHighOctave.setMinimumSize(new java.awt.Dimension(100, 18));
        chordHighOctave.setPreferredSize(new java.awt.Dimension(100, 18));
        chordHighOctave.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chordHighOctaveStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        chordAttrPanel.add(chordHighOctave, gridBagConstraints);

        chordLowOctave.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        chordLowOctave.setMinimumSize(new java.awt.Dimension(100, 18));
        chordLowOctave.setPreferredSize(new java.awt.Dimension(100, 18));
        chordLowOctave.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chordLowOctaveStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 2, 0, 0);
        chordAttrPanel.add(chordLowOctave, gridBagConstraints);

        chordOctaveLabel.setFont(new java.awt.Font("Tahoma", 1, 9)); // NOI18N
        chordOctaveLabel.setText("Octave:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 4, 0);
        chordAttrPanel.add(chordOctaveLabel, gridBagConstraints);

        voicingLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        voicingLabel.setText("Voicing Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        chordAttrPanel.add(voicingLabel, gridBagConstraints);

        voicingType.setMaximumRowCount(10);
        voicingType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "any", "closed", "open", "quartal", "shout" }));
        voicingType.setMinimumSize(new java.awt.Dimension(150, 22));
        voicingType.setPreferredSize(new java.awt.Dimension(150, 22));
        voicingType.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingTypeActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(3, 7, 0, 0);
        chordAttrPanel.add(voicingType, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        globalAttrPanel.add(chordAttrPanel, gridBagConstraints);

        melodySwingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Melody Swing"));
        melodySwingPanel.setMinimumSize(new java.awt.Dimension(100, 60));
        melodySwingPanel.setPreferredSize(new java.awt.Dimension(100, 60));

        swingTextField.setText("0.5");
        swingTextField.setPreferredSize(new java.awt.Dimension(50, 19));
        swingTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                swingTextFieldKeyTyped(evt);
            }
        });
        melodySwingPanel.add(swingTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        globalAttrPanel.add(melodySwingPanel, gridBagConstraints);

        compSwingPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Comp Swing"));
        compSwingPanel1.setMinimumSize(new java.awt.Dimension(100, 60));
        compSwingPanel1.setPreferredSize(new java.awt.Dimension(100, 60));
        compSwingPanel1.setRequestFocusEnabled(false);

        accompanimentSwingTextField.setText("0.5");
        accompanimentSwingTextField.setPreferredSize(new java.awt.Dimension(50, 19));
        accompanimentSwingTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                accompanimentSwingTextFieldKeyTyped(evt);
            }
        });
        compSwingPanel1.add(accompanimentSwingTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        globalAttrPanel.add(compSwingPanel1, gridBagConstraints);

        chordPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chord played over pattern", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        chordPanel.setMaximumSize(new java.awt.Dimension(300, 60));
        chordPanel.setMinimumSize(new java.awt.Dimension(250, 60));
        chordPanel.setPreferredSize(new java.awt.Dimension(300, 60));
        chordPanel.setLayout(new java.awt.GridBagLayout());

        muteChordToggle.setBackground(new java.awt.Color(255, 0, 0));
        muteChordToggle.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        muteChordToggle.setText("Play");
        muteChordToggle.setToolTipText("Mute/unmute the preview chord.");
        muteChordToggle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        muteChordToggle.setFocusPainted(false);
        muteChordToggle.setFocusable(false);
        muteChordToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        muteChordToggle.setMaximumSize(new java.awt.Dimension(60, 20));
        muteChordToggle.setMinimumSize(new java.awt.Dimension(60, 20));
        muteChordToggle.setOpaque(true);
        muteChordToggle.setPreferredSize(new java.awt.Dimension(60, 20));
        muteChordToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                muteChordToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        chordPanel.add(muteChordToggle, gridBagConstraints);

        chordPitchComboBox.setMaximumRowCount(12);
        chordPitchComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "B", "A#", "A", "G#", "G", "F#", "F", "E", "D#", "D", "C#", "C" }));
        chordPitchComboBox.setSelectedIndex(11);
        chordPitchComboBox.setToolTipText("Change the preview chord");
        chordPitchComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordPitchComboBoxActionPerformed(evt);
            }
        });
        chordPanel.add(chordPitchComboBox, new java.awt.GridBagConstraints());

        chordTypeComboBox.setMaximumRowCount(40);
        chordTypeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "M", "M7", "m", "m7" }));
        chordTypeComboBox.setToolTipText("Change the previewed chord.");
        chordTypeComboBox.setMaximumSize(new java.awt.Dimension(32767, 27));
        chordTypeComboBox.setPreferredSize(new java.awt.Dimension(110, 27));
        chordTypeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordTypeComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        chordPanel.add(chordTypeComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        globalAttrPanel.add(chordPanel, gridBagConstraints);

        playPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Play pattern when cell clicked", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        playPanel.setMaximumSize(new java.awt.Dimension(350, 60));
        playPanel.setMinimumSize(new java.awt.Dimension(350, 60));
        playPanel.setPreferredSize(new java.awt.Dimension(350, 60));
        playPanel.setRequestFocusEnabled(false);
        playPanel.setLayout(new java.awt.GridBagLayout());

        playToggle.setBackground(new java.awt.Color(0, 250, 0));
        playToggle.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        playToggle.setSelected(true);
        playToggle.setText("Mute");
        playToggle.setToolTipText("Play/don't play the pattern in a cell when clicked.");
        playToggle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playToggle.setFocusPainted(false);
        playToggle.setFocusable(false);
        playToggle.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playToggle.setMaximumSize(new java.awt.Dimension(60, 20));
        playToggle.setMinimumSize(new java.awt.Dimension(60, 20));
        playToggle.setOpaque(true);
        playToggle.setPreferredSize(new java.awt.Dimension(60, 20));
        playToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playToggleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        playPanel.add(playToggle, gridBagConstraints);
        playToggle.getAccessibleContext().setAccessibleDescription("Play or don't play cell when selected.\n");

        volLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        volLabel.setText("Volume");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        playPanel.add(volLabel, gridBagConstraints);

        masterVolumeSlider.setToolTipText("Adjust preview volume");
        masterVolumeSlider.setMaximumSize(new java.awt.Dimension(80, 20));
        masterVolumeSlider.setMinimumSize(new java.awt.Dimension(60, 20));
        masterVolumeSlider.setPreferredSize(new java.awt.Dimension(80, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        playPanel.add(masterVolumeSlider, gridBagConstraints);

        tempoComboBox.setMaximumRowCount(30);
        tempoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "30", "40", "50", "60", "70", "80", "90", "100", "110", "120", "130", "140", "150", "160", "170", "180", "190", "200", "210", "220", "230", "240", "250", "260", "270", "280", "290", "300", " " }));
        tempoComboBox.setToolTipText("Change tempo for the Style Editor.\n");
        tempoComboBox.setMinimumSize(new java.awt.Dimension(100, 27));
        tempoComboBox.setPreferredSize(new java.awt.Dimension(100, 27));
        tempoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempoComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        playPanel.add(tempoComboBox, gridBagConstraints);

        bpmLabel.setText("BPM");
        bpmLabel.setToolTipText("Tempo in Beats Per Minute");
        bpmLabel.setRequestFocusEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 3);
        playPanel.add(bpmLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        globalAttrPanel.add(playPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(globalAttrPanel, gridBagConstraints);

        toolbarPanel.setPreferredSize(new java.awt.Dimension(1050, 64));
        toolbarPanel.setLayout(new java.awt.GridBagLayout());

        filePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "File", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        filePanel.setMaximumSize(new java.awt.Dimension(120, 60));
        filePanel.setMinimumSize(new java.awt.Dimension(120, 60));
        filePanel.setPreferredSize(new java.awt.Dimension(120, 60));
        filePanel.setLayout(new java.awt.GridBagLayout());

        newButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/new.gif"))); // NOI18N
        newButton.setToolTipText("Create new style");
        newButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        newButton.setMaximumSize(new java.awt.Dimension(32, 32));
        newButton.setMinimumSize(new java.awt.Dimension(32, 32));
        newButton.setPreferredSize(new java.awt.Dimension(32, 32));
        newButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        filePanel.add(newButton, gridBagConstraints);

        openButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/open.gif"))); // NOI18N
        openButton.setToolTipText("Open a style file");
        openButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openButton.setMaximumSize(new java.awt.Dimension(32, 32));
        openButton.setMinimumSize(new java.awt.Dimension(32, 32));
        openButton.setPreferredSize(new java.awt.Dimension(32, 32));
        openButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        filePanel.add(openButton, gridBagConstraints);

        saveButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/save.gif"))); // NOI18N
        saveButton.setToolTipText("Save style");
        saveButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        saveButton.setMaximumSize(new java.awt.Dimension(32, 32));
        saveButton.setMinimumSize(new java.awt.Dimension(32, 32));
        saveButton.setPreferredSize(new java.awt.Dimension(32, 32));
        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        filePanel.add(saveButton, gridBagConstraints);

        toolbarPanel.add(filePanel, new java.awt.GridBagConstraints());

        columnPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Column Edit\n", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        columnPanel.setMaximumSize(new java.awt.Dimension(150, 60));
        columnPanel.setMinimumSize(new java.awt.Dimension(150, 60));
        columnPanel.setPreferredSize(new java.awt.Dimension(150, 60));
        columnPanel.setLayout(new java.awt.GridBagLayout());

        cutColumnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/cut.gif"))); // NOI18N
        cutColumnButton.setToolTipText("Cut contents of entire columns (leaving columns empty).");
        cutColumnButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cutColumnButton.setMaximumSize(new java.awt.Dimension(32, 32));
        cutColumnButton.setMinimumSize(new java.awt.Dimension(32, 32));
        cutColumnButton.setPreferredSize(new java.awt.Dimension(32, 32));
        cutColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutColumnButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        columnPanel.add(cutColumnButton, gridBagConstraints);
        cutColumnButton.getAccessibleContext().setAccessibleDescription("Cut entire columns' contents (columns stay).");

        copyColumnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/copy.gif"))); // NOI18N
        copyColumnButton.setToolTipText("Copy contents of entire columns (leaving columns as is).");
        copyColumnButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        copyColumnButton.setMaximumSize(new java.awt.Dimension(32, 32));
        copyColumnButton.setMinimumSize(new java.awt.Dimension(32, 32));
        copyColumnButton.setPreferredSize(new java.awt.Dimension(32, 32));
        copyColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyColumnButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        columnPanel.add(copyColumnButton, gridBagConstraints);
        copyColumnButton.getAccessibleContext().setAccessibleDescription("Copy entire columns' contents.");

        pasteColumnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/paste.gif"))); // NOI18N
        pasteColumnButton.setToolTipText("Paste copied columns, into selected columns only.");
        pasteColumnButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pasteColumnButton.setMaximumSize(new java.awt.Dimension(32, 32));
        pasteColumnButton.setMinimumSize(new java.awt.Dimension(32, 32));
        pasteColumnButton.setPreferredSize(new java.awt.Dimension(32, 32));
        pasteColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteColumnButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        columnPanel.add(pasteColumnButton, gridBagConstraints);
        pasteColumnButton.getAccessibleContext().setAccessibleDescription("Paste copied columns inside selected area.");

        addColumnButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/blue.gif"))); // NOI18N
        addColumnButton.setToolTipText("Add a new column at the right end of the table.");
        addColumnButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addColumnButton.setMaximumSize(new java.awt.Dimension(32, 32));
        addColumnButton.setMinimumSize(new java.awt.Dimension(32, 32));
        addColumnButton.setPreferredSize(new java.awt.Dimension(32, 32));
        addColumnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addColumnButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        columnPanel.add(addColumnButton, gridBagConstraints);

        toolbarPanel.add(columnPanel, new java.awt.GridBagConstraints());

        rowPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Row Edit", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        rowPanel.setToolTipText("The row corresponding to an instrument");
        rowPanel.setMaximumSize(new java.awt.Dimension(150, 60));
        rowPanel.setMinimumSize(new java.awt.Dimension(150, 60));
        rowPanel.setPreferredSize(new java.awt.Dimension(150, 60));
        rowPanel.setLayout(new java.awt.GridBagLayout());

        cutRowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/cut.gif"))); // NOI18N
        cutRowButton.setToolTipText("Cut contents of rows, leaving rows empty.");
        cutRowButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cutRowButton.setMaximumSize(new java.awt.Dimension(32, 32));
        cutRowButton.setMinimumSize(new java.awt.Dimension(32, 32));
        cutRowButton.setPreferredSize(new java.awt.Dimension(32, 32));
        cutRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutRowButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        rowPanel.add(cutRowButton, gridBagConstraints);

        copyRowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/copy.gif"))); // NOI18N
        copyRowButton.setToolTipText("Copy contents of selected rows.");
        copyRowButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        copyRowButton.setMaximumSize(new java.awt.Dimension(32, 32));
        copyRowButton.setMinimumSize(new java.awt.Dimension(32, 32));
        copyRowButton.setPreferredSize(new java.awt.Dimension(32, 32));
        copyRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyRowButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 2);
        rowPanel.add(copyRowButton, gridBagConstraints);

        pasteRowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/paste.gif"))); // NOI18N
        pasteRowButton.setToolTipText("Paste contents of copied rows, into selected rows only.");
        pasteRowButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pasteRowButton.setMaximumSize(new java.awt.Dimension(32, 32));
        pasteRowButton.setMinimumSize(new java.awt.Dimension(32, 32));
        pasteRowButton.setPreferredSize(new java.awt.Dimension(32, 32));
        pasteRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteRowButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 2);
        rowPanel.add(pasteRowButton, gridBagConstraints);

        newRowButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/orange.gif"))); // NOI18N
        newRowButton.setToolTipText("Add a new row at the bottom of the table.");
        newRowButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        newRowButton.setMaximumSize(new java.awt.Dimension(32, 32));
        newRowButton.setMinimumSize(new java.awt.Dimension(32, 32));
        newRowButton.setPreferredSize(new java.awt.Dimension(32, 32));
        newRowButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newInstrumentButtonClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        rowPanel.add(newRowButton, gridBagConstraints);

        toolbarPanel.add(rowPanel, new java.awt.GridBagConstraints());

        cellsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cell Edit\n", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        cellsPanel.setMaximumSize(new java.awt.Dimension(120, 60));
        cellsPanel.setMinimumSize(new java.awt.Dimension(120, 60));
        cellsPanel.setPreferredSize(new java.awt.Dimension(120, 60));
        cellsPanel.setLayout(new java.awt.GridBagLayout());

        cutCellsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/cut.gif"))); // NOI18N
        cutCellsButton.setToolTipText("Cut the contents of selected cells, leaving them empty.");
        cutCellsButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cutCellsButton.setMaximumSize(new java.awt.Dimension(32, 32));
        cutCellsButton.setMinimumSize(new java.awt.Dimension(32, 32));
        cutCellsButton.setPreferredSize(new java.awt.Dimension(32, 32));
        cutCellsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutCellsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        cellsPanel.add(cutCellsButton, gridBagConstraints);

        copyCellsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/copy.gif"))); // NOI18N
        copyCellsButton.setToolTipText("Copy the contents of selected cells.");
        copyCellsButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        copyCellsButton.setMaximumSize(new java.awt.Dimension(32, 32));
        copyCellsButton.setMinimumSize(new java.awt.Dimension(32, 32));
        copyCellsButton.setPreferredSize(new java.awt.Dimension(32, 32));
        copyCellsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyCellsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 2);
        cellsPanel.add(copyCellsButton, gridBagConstraints);

        pasteCellsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/paste.gif"))); // NOI18N
        pasteCellsButton.setToolTipText("Paste copied contents, into selected cells only.");
        pasteCellsButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pasteCellsButton.setMaximumSize(new java.awt.Dimension(32, 32));
        pasteCellsButton.setMinimumSize(new java.awt.Dimension(32, 32));
        pasteCellsButton.setPreferredSize(new java.awt.Dimension(32, 32));
        pasteCellsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteCellsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 2);
        cellsPanel.add(pasteCellsButton, gridBagConstraints);

        toolbarPanel.add(cellsPanel, new java.awt.GridBagConstraints());

        timeSigPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createTitledBorder("Time")));
        timeSigPanel.setMaximumSize(new java.awt.Dimension(80, 60));
        timeSigPanel.setMinimumSize(new java.awt.Dimension(80, 60));
        timeSigPanel.setPreferredSize(new java.awt.Dimension(80, 60));
        timeSigPanel.setLayout(new java.awt.GridBagLayout());

        numField.setText("4");
        numField.setMinimumSize(new java.awt.Dimension(25, 20));
        numField.setPreferredSize(new java.awt.Dimension(25, 20));
        numField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                numFieldActionPerformed(evt);
            }
        });
        numField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                numFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        timeSigPanel.add(numField, gridBagConstraints);

        slashLabel.setText("/");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        timeSigPanel.add(slashLabel, gridBagConstraints);

        denomField.setText("4");
        denomField.setMinimumSize(new java.awt.Dimension(25, 20));
        denomField.setPreferredSize(new java.awt.Dimension(25, 20));
        denomField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                denomFieldActionPerformed(evt);
            }
        });
        denomField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                denomFieldKeyTyped(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 0, 0);
        timeSigPanel.add(denomField, gridBagConstraints);

        toolbarPanel.add(timeSigPanel, new java.awt.GridBagConstraints());

        remotePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Play Saved Style\n"));
        remotePanel.setToolTipText("Play the parent leadsheet.");
        remotePanel.setMaximumSize(new java.awt.Dimension(110, 2147483647));
        remotePanel.setMinimumSize(new java.awt.Dimension(150, 60));
        remotePanel.setPreferredSize(new java.awt.Dimension(150, 60));
        remotePanel.setRequestFocusEnabled(false);
        remotePanel.setLayout(new java.awt.GridBagLayout());

        saveStyleBtn1.setText("Save");
        saveStyleBtn1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        saveStyleBtn1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveStyleBtn1.setMaximumSize(new java.awt.Dimension(40, 20));
        saveStyleBtn1.setMinimumSize(new java.awt.Dimension(40, 20));
        saveStyleBtn1.setPreferredSize(new java.awt.Dimension(40, 20));
        saveStyleBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveStyleBtn1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        remotePanel.add(saveStyleBtn1, gridBagConstraints);

        playBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
        playBtn.setToolTipText("Play the entire leadsheet.");
        playBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        playBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        playBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        playBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        remotePanel.add(playBtn, gridBagConstraints);

        pauseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pause.gif"))); // NOI18N
        pauseBtn.setToolTipText("Pause or resume playback.");
        pauseBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pauseBtn.setEnabled(false);
        pauseBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        pauseBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        pauseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        pauseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        remotePanel.add(pauseBtn, gridBagConstraints);

        stopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/stop.gif"))); // NOI18N
        stopBtn.setToolTipText("Stop playback.");
        stopBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stopBtn.setEnabled(false);
        stopBtn.setMaximumSize(new java.awt.Dimension(32, 32));
        stopBtn.setMinimumSize(new java.awt.Dimension(32, 32));
        stopBtn.setPreferredSize(new java.awt.Dimension(32, 32));
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        remotePanel.add(stopBtn, gridBagConstraints);

        toolbarPanel.add(remotePanel, new java.awt.GridBagConstraints());

        statusPanel.setMinimumSize(new java.awt.Dimension(190, 60));
        statusPanel.setPreferredSize(new java.awt.Dimension(190, 60));

        styleEditorStatusTF.setBackground(new java.awt.Color(238, 238, 238));
        styleEditorStatusTF.setEditable(false);
        styleEditorStatusTF.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        styleEditorStatusTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        styleEditorStatusTF.setText("Normal");
        styleEditorStatusTF.setToolTipText("Tells what the editor is doing.");
        styleEditorStatusTF.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Editor Status", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        styleEditorStatusTF.setMargin(new java.awt.Insets(0, 5, 2, 4));
        styleEditorStatusTF.setMaximumSize(new java.awt.Dimension(300, 50));
        styleEditorStatusTF.setMinimumSize(new java.awt.Dimension(180, 50));
        styleEditorStatusTF.setPreferredSize(new java.awt.Dimension(180, 50));
        styleEditorStatusTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleEditorStatusTFActionPerformed(evt);
            }
        });
        styleEditorStatusTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                styleEditorStatusTFFocusLost(evt);
            }
        });
        statusPanel.add(styleEditorStatusTF);

        toolbarPanel.add(statusPanel, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.ipadx = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        getContentPane().add(toolbarPanel, gridBagConstraints);

        clipboardPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Clipboard"));
        clipboardPanel.setMinimumSize(new java.awt.Dimension(800, 50));
        clipboardPanel.setPreferredSize(new java.awt.Dimension(1200, 50));
        clipboardPanel.setLayout(new java.awt.GridBagLayout());

        clipboardTextField.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        clipboardTextField.setMinimumSize(new java.awt.Dimension(650, 19));
        clipboardTextField.setPreferredSize(new java.awt.Dimension(650, 19));
        clipboardTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clipboardTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 3, 3, 3);
        clipboardPanel.add(clipboardTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        getContentPane().add(clipboardPanel, gridBagConstraints);

        stylePanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Mirrored patterns, most recent pattern at the bottom\n"));
        stylePanel.setToolTipText("The last 3 patterns selected appear here, with the most recent at the bottom.\n");
        stylePanel.setMinimumSize(new java.awt.Dimension(1000, 200));
        stylePanel.setPreferredSize(new java.awt.Dimension(1300, 400));
        stylePanel.setLayout(new java.awt.GridBagLayout());

        rowLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        rowLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        rowLabel.setText("Row");
        rowLabel.setToolTipText("Most recent rule selected");
        rowLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        rowLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        rowLabel.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        stylePanel.add(rowLabel, gridBagConstraints);

        columnLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        columnLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        columnLabel.setText("Column");
        columnLabel.setToolTipText("Least recent rule selected");
        columnLabel.setMaximumSize(new java.awt.Dimension(60, 14));
        columnLabel.setMinimumSize(new java.awt.Dimension(60, 14));
        columnLabel.setPreferredSize(new java.awt.Dimension(60, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        stylePanel.add(columnLabel, gridBagConstraints);

        beatsLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        beatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        beatsLabel.setText("Beats\n");
        beatsLabel.setToolTipText("Computed number of beats\n");
        beatsLabel.setMaximumSize(new java.awt.Dimension(100, 14));
        beatsLabel.setMinimumSize(new java.awt.Dimension(100, 14));
        beatsLabel.setPreferredSize(new java.awt.Dimension(100, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        stylePanel.add(beatsLabel, gridBagConstraints);

        contentLabel.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        contentLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        contentLabel.setText("Pattern\n");
        contentLabel.setToolTipText("Previous rule selected");
        contentLabel.setMaximumSize(new java.awt.Dimension(900, 14));
        contentLabel.setMinimumSize(new java.awt.Dimension(900, 14));
        contentLabel.setPreferredSize(new java.awt.Dimension(900, 14));
        stylePanel.add(contentLabel, new java.awt.GridBagConstraints());

        beatsField2.setEditable(false);
        beatsField2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        beatsField2.setMaximumSize(new java.awt.Dimension(100, 2147483647));
        beatsField2.setMinimumSize(new java.awt.Dimension(100, 22));
        beatsField2.setPreferredSize(new java.awt.Dimension(100, 22));
        beatsField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beatsField2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(beatsField2, gridBagConstraints);

        rowField2.setEditable(false);
        rowField2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rowField2.setMinimumSize(new java.awt.Dimension(150, 22));
        rowField2.setPreferredSize(new java.awt.Dimension(150, 22));
        rowField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rowField2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(rowField2, gridBagConstraints);

        columnField2.setEditable(false);
        columnField2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        columnField2.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        columnField2.setMinimumSize(new java.awt.Dimension(60, 22));
        columnField2.setPreferredSize(new java.awt.Dimension(60, 22));
        columnField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnField2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        stylePanel.add(columnField2, gridBagConstraints);

        styleTextField2.setToolTipText("Edit the cell indicated on the left.");
        styleTextField2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        styleTextField2.setMinimumSize(new java.awt.Dimension(400, 22));
        styleTextField2.setPreferredSize(new java.awt.Dimension(650, 22));
        styleTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleTextField2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        stylePanel.add(styleTextField2, gridBagConstraints);

        beatsField1.setEditable(false);
        beatsField1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        beatsField1.setMaximumSize(new java.awt.Dimension(100, 2147483647));
        beatsField1.setMinimumSize(new java.awt.Dimension(100, 22));
        beatsField1.setPreferredSize(new java.awt.Dimension(100, 22));
        beatsField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beatsField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(beatsField1, gridBagConstraints);

        rowField1.setEditable(false);
        rowField1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rowField1.setMinimumSize(new java.awt.Dimension(150, 22));
        rowField1.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(rowField1, gridBagConstraints);

        columnField1.setEditable(false);
        columnField1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        columnField1.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        columnField1.setMinimumSize(new java.awt.Dimension(60, 22));
        columnField1.setPreferredSize(new java.awt.Dimension(60, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        stylePanel.add(columnField1, gridBagConstraints);

        styleTextField1.setToolTipText("Edit the cell indicated on the left.");
        styleTextField1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        styleTextField1.setMinimumSize(new java.awt.Dimension(400, 22));
        styleTextField1.setPreferredSize(new java.awt.Dimension(650, 22));
        styleTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleTextField1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        stylePanel.add(styleTextField1, gridBagConstraints);

        beatsField0.setEditable(false);
        beatsField0.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        beatsField0.setMaximumSize(new java.awt.Dimension(100, 2147483647));
        beatsField0.setMinimumSize(new java.awt.Dimension(100, 22));
        beatsField0.setPreferredSize(new java.awt.Dimension(100, 22));
        beatsField0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beatsField0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(beatsField0, gridBagConstraints);

        rowField0.setEditable(false);
        rowField0.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        rowField0.setMinimumSize(new java.awt.Dimension(150, 22));
        rowField0.setPreferredSize(new java.awt.Dimension(150, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(rowField0, gridBagConstraints);

        columnField0.setEditable(false);
        columnField0.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        columnField0.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        columnField0.setMinimumSize(new java.awt.Dimension(60, 22));
        columnField0.setPreferredSize(new java.awt.Dimension(60, 22));
        columnField0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                columnField0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        stylePanel.add(columnField0, gridBagConstraints);

        styleTextField0.setToolTipText("Edit the cell indicated on the left.");
        styleTextField0.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        styleTextField0.setMinimumSize(new java.awt.Dimension(400, 22));
        styleTextField0.setPreferredSize(new java.awt.Dimension(650, 22));
        styleTextField0.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleTextField0ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 0);
        stylePanel.add(styleTextField0, gridBagConstraints);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel1.setText("<html><b>Double-click</b> table cell to edit pattern. <b>Control-click</b> a column of table to play percussion simultaneously. <b>Control-shift-click</b> a column to use piano roll editor. </html>\n");
        editInstructionsPanel.add(jLabel1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        stylePanel.add(editInstructionsPanel, gridBagConstraints);

        styleScrollpane.setPreferredSize(new java.awt.Dimension(1200, 300));

        panelInStyleScrollpane.setMinimumSize(new java.awt.Dimension(7200, 800));
        panelInStyleScrollpane.setPreferredSize(new java.awt.Dimension(7200, 800));
        panelInStyleScrollpane.setLayout(new java.awt.GridBagLayout());

        styleTable.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        styleTable.setToolTipText("Set style patterns here.");
        styleTable.setColumnSelectionAllowed(true);
        styleTable.setGridColor(new java.awt.Color(153, 153, 153));
        styleTable.setMaximumSize(new java.awt.Dimension(7200, 1200));
        styleTable.setMinimumSize(new java.awt.Dimension(4800, 800));
        styleTable.setPreferredSize(new java.awt.Dimension(7200, 800));
        styleTable.setSurrendersFocusOnKeystroke(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        panelInStyleScrollpane.add(styleTable, gridBagConstraints);

        jScrollPane1.setViewportView(jTextPane1);

        panelInStyleScrollpane.add(jScrollPane1, new java.awt.GridBagConstraints());

        styleScrollpane.setViewportView(panelInStyleScrollpane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        stylePanel.add(styleScrollpane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(stylePanel, gridBagConstraints);

        closeButtonPanel.setLayout(new java.awt.GridBagLayout());

        stopPlaying.setText("Stop Playing");
        stopPlaying.setToolTipText("Stop Playing");
        stopPlaying.setDefaultCapable(false);
        stopPlaying.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        stopPlaying.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopPlaying.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayingActionPerformed(evt);
            }
        });
        closeButtonPanel.add(stopPlaying, new java.awt.GridBagConstraints());

        closeBtn.setText("Close");
        closeBtn.setDefaultCapable(false);
        closeBtn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        closeBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        closeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeBtnActionPerformed(evt);
            }
        });
        closeButtonPanel.add(closeBtn, new java.awt.GridBagConstraints());

        saveStyleBtn.setText("Save Style");
        saveStyleBtn.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        saveStyleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        saveStyleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveStyleBtnActionPerformed(evt);
            }
        });
        closeButtonPanel.add(saveStyleBtn, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        getContentPane().add(closeButtonPanel, gridBagConstraints);

        styleSpecificationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Style Specification"));
        styleSpecificationPanel.setMaximumSize(new java.awt.Dimension(1200, 1200));
        styleSpecificationPanel.setMinimumSize(new java.awt.Dimension(0, 0));
        styleSpecificationPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        styleSpecificationPanel.setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(550, 570));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1000, 570));

        bassTabPanel.setMinimumSize(new java.awt.Dimension(470, 670));
        bassTabPanel.setPreferredSize(new java.awt.Dimension(470, 670));
        bassTabPanel.setLayout(new java.awt.GridBagLayout());

        bassStyleSpecScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        bassStyleSpecScrollPane.setMinimumSize(new java.awt.Dimension(540, 540));
        bassStyleSpecScrollPane.setPreferredSize(new java.awt.Dimension(540, 540));

        bassHolderPane.setBackground(new java.awt.Color(255, 255, 255));
        bassHolderPane.setRequestFocusEnabled(false);
        bassHolderPane.setLayout(new javax.swing.BoxLayout(bassHolderPane, javax.swing.BoxLayout.Y_AXIS));
        bassStyleSpecScrollPane.setViewportView(bassHolderPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        bassTabPanel.add(bassStyleSpecScrollPane, gridBagConstraints);

        jTabbedPane1.addTab("Bass", bassTabPanel);

        drumTabPanel.setMinimumSize(new java.awt.Dimension(470, 670));
        drumTabPanel.setPreferredSize(new java.awt.Dimension(470, 670));
        drumTabPanel.setLayout(new java.awt.GridBagLayout());

        drumStyleSpecScrollPane.setMinimumSize(new java.awt.Dimension(540, 540));
        drumStyleSpecScrollPane.setPreferredSize(new java.awt.Dimension(540, 540));

        drumHolderPane.setBackground(new java.awt.Color(255, 255, 255));
        drumHolderPane.setLayout(new javax.swing.BoxLayout(drumHolderPane, javax.swing.BoxLayout.Y_AXIS));
        drumStyleSpecScrollPane.setViewportView(drumHolderPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        drumTabPanel.add(drumStyleSpecScrollPane, gridBagConstraints);

        jTabbedPane1.addTab("Drum", drumTabPanel);

        chordTabPanel.setMinimumSize(new java.awt.Dimension(470, 670));
        chordTabPanel.setPreferredSize(new java.awt.Dimension(470, 670));
        chordTabPanel.setLayout(new java.awt.GridBagLayout());

        chordStyleSpecScrollPane.setMinimumSize(new java.awt.Dimension(540, 540));
        chordStyleSpecScrollPane.setPreferredSize(new java.awt.Dimension(540, 540));

        chordHolderPane.setBackground(new java.awt.Color(255, 255, 255));
        chordHolderPane.setLayout(new javax.swing.BoxLayout(chordHolderPane, javax.swing.BoxLayout.Y_AXIS));
        chordStyleSpecScrollPane.setViewportView(chordHolderPane);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        chordTabPanel.add(chordStyleSpecScrollPane, gridBagConstraints);

        jTabbedPane1.addTab("Chords", chordTabPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        styleSpecificationPanel.add(jTabbedPane1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        getContentPane().add(styleSpecificationPanel, gridBagConstraints);

        styFile.setMnemonic('F');
        styFile.setText("File");
        styFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styFileActionPerformed(evt);
            }
        });

        newStyleMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        newStyleMI.setMnemonic('n');
        newStyleMI.setText("New Style");
        newStyleMI.setToolTipText("Create a new style.");
        newStyleMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newStyleMIActionPerformed(evt);
            }
        });
        styFile.add(newStyleMI);

        openStyleMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openStyleMI.setMnemonic('o');
        openStyleMI.setText("Open style");
        openStyleMI.setToolTipText("Open an existing style.");
        openStyleMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openStyleMIActionPerformed(evt);
            }
        });
        styFile.add(openStyleMI);
        styFile.add(jSeparator2);

        saveStyleMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveStyleMI.setMnemonic('s');
        saveStyleMI.setText("Save Style");
        saveStyleMI.setToolTipText("Save the current style.");
        saveStyleMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveStyleMIActionPerformed(evt);
            }
        });
        styFile.add(saveStyleMI);

        saveStyleAs.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        saveStyleAs.setMnemonic('w');
        saveStyleAs.setText("Save Style As");
        saveStyleAs.setToolTipText("Save the style, possibly under a new name.");
        saveStyleAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveStyleAsActionPerformed(evt);
            }
        });
        styFile.add(saveStyleAs);
        styFile.add(jSeparator3);

        exitStyleGenMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        exitStyleGenMI.setMnemonic('x');
        exitStyleGenMI.setText("Exit");
        exitStyleGenMI.setToolTipText("Close the style editor.");
        exitStyleGenMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitStyleGenMIActionPerformed(evt);
            }
        });
        styFile.add(exitStyleGenMI);

        styMenuBar.add(styFile);

        styEdit.setMnemonic('E');
        styEdit.setText("Edit");
        styEdit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                styEditMouseClicked(evt);
            }
        });
        styEdit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styEditActionPerformed(evt);
            }
        });
        styEdit.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                styEditFocusGained(evt);
            }
        });
        styEdit.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                styEditKeyPressed(evt);
            }
        });

        pianoRollCheckBox.setText("Use Piano Roll Editor");
        pianoRollCheckBox.setToolTipText("Open the piano roll editor for graphical editing of columns.\n(Shift click to set the column.)\n");
        pianoRollCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pianoRollCheckBoxActionPerformed(evt);
            }
        });
        styEdit.add(pianoRollCheckBox);

        trackWithPianoRoll.setText("Track Columns with Piano Roll when Piano Roll is open.\n");
        trackWithPianoRoll.setToolTipText("If the piano roll editor is open, change its column as spreadsheet columns are clicked.");
        trackWithPianoRoll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trackWithPianoRollActionPerformed(evt);
            }
        });
        styEdit.add(trackWithPianoRoll);

        cutCellsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutCellsMI.setMnemonic('a');
        cutCellsMI.setText("Cut Cells Contents");
        cutCellsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutCellsMIActionPerformed(evt);
            }
        });
        styEdit.add(cutCellsMI);

        copyCellsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyCellsMI.setMnemonic('r');
        copyCellsMI.setText("Copy Cells Contents");
        copyCellsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyCellsMIActionPerformed(evt);
            }
        });
        styEdit.add(copyCellsMI);

        pasteCellsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pasteCellsMI.setMnemonic('u');
        pasteCellsMI.setText("Paste Cells Contents");
        pasteCellsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteCellsMIActionPerformed(evt);
            }
        });
        styEdit.add(pasteCellsMI);

        styMenuBar.add(styEdit);

        styGenerate.setMnemonic('g');
        styGenerate.setText("Extract");
        styGenerate.setActionCommand("Generate");

        generateMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        generateMI.setMnemonic('e');
        generateMI.setText("Extract Style from MIDI");
        generateMI.setActionCommand("Generate Style from MIDI");
        generateMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateMIActionPerformed(evt);
            }
        });
        styGenerate.add(generateMI);

        generatePrefMI.setMnemonic('p');
        generatePrefMI.setText("Extraction Preferences");
        generatePrefMI.setActionCommand("Generation Preferences");
        generatePrefMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generatePrefMIActionPerformed(evt);
            }
        });
        styGenerate.add(generatePrefMI);

        styMenuBar.add(styGenerate);

        styHelp.setMnemonic('H');
        styHelp.setText("Help");

        styHelpMI.setMnemonic('h');
        styHelpMI.setText("Help");
        styHelpMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styHelpMIActionPerformed(evt);
            }
        });
        styHelp.add(styHelpMI);

        styMenuBar.add(styHelp);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window");
        windowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                windowMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window");
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows");
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(windowMenuSeparator);

        styMenuBar.add(windowMenu);

        setJMenuBar(styMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents
  private void styleEditorStatusTFFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_styleEditorStatusTFFocusLost
  {//GEN-HEADEREND:event_styleEditorStatusTFFocusLost
// TODO add your handling code here:
  }//GEN-LAST:event_styleEditorStatusTFFocusLost

  private void styleEditorStatusTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_styleEditorStatusTFActionPerformed
  {//GEN-HEADEREND:event_styleEditorStatusTFActionPerformed
// TODO add your handling code here:
  }//GEN-LAST:event_styleEditorStatusTFActionPerformed

  private void newInstrumentButtonClicked(java.awt.event.ActionEvent evt)//GEN-FIRST:event_newInstrumentButtonClicked
  {//GEN-HEADEREND:event_newInstrumentButtonClicked
    getTableModel().newRow();
    rowHeader.setListData(rowHeaderLabels.toArray());
  }//GEN-LAST:event_newInstrumentButtonClicked

  private void styleTextField0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_styleTextField0ActionPerformed
  {//GEN-HEADEREND:event_styleTextField0ActionPerformed
    if( recentRows[0] >= 0 && recentColumns[0] >= 0 )
      {
      String revisedContent = styleTextField0.getText(); //.toUpperCase();
      setCell(revisedContent, recentRows[0], recentColumns[0], PLAY);
      updateMirror(recentRows[0], recentColumns[0], revisedContent);
      }
}//GEN-LAST:event_styleTextField0ActionPerformed

  private void styleTextField1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_styleTextField1ActionPerformed
  {//GEN-HEADEREND:event_styleTextField1ActionPerformed
    if( recentRows[1] >= 0 && recentColumns[1] >= 0 )
      {
      String revisedContent = styleTextField1.getText(); //.toUpperCase();
      setCell(revisedContent, recentRows[1], recentColumns[1], PLAY);
      updateMirror(recentRows[1], recentColumns[1], revisedContent);
    }
}//GEN-LAST:event_styleTextField1ActionPerformed

  private void styleTextField2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_styleTextField2ActionPerformed
  {//GEN-HEADEREND:event_styleTextField2ActionPerformed
     if( recentRows[2] >= 0 && recentColumns[2] >= 0 )
      {
      String revisedContent = styleTextField2.getText(); //.toUpperCase();
      setCell(revisedContent, recentRows[2], recentColumns[2], PLAY);
      updateMirror(recentRows[2], recentColumns[2], revisedContent);
      }
}//GEN-LAST:event_styleTextField2ActionPerformed

    private void playToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playToggleActionPerformed
    if( playToggle.isSelected() )
      {
      playToggle.setText("Mute");
      playToggle.setBackground(ON_COLOR);
      }
    else
     {
     playToggle.setText("Play");
     playToggle.setBackground(OFF_COLOR);
     }       
    }//GEN-LAST:event_playToggleActionPerformed

    private void pasteRowButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pasteRowButtonActionPerformed
    {//GEN-HEADEREND:event_pasteRowButtonActionPerformed
      int rows[] = styleTable.getSelectedRows();
      pasteRows(rows);
}//GEN-LAST:event_pasteRowButtonActionPerformed
  public void setMuted(boolean value)
    {
      playToggle.setSelected(value);
    }
  /**
   * Paste copied cells, taking care not to exceed boundaries of the table.
   */
  public void pasteRows(int rows[])
    {
    if( rows.length == 0 || copiedCells.isEmpty() || ((Polylist)copiedCells.first()).isEmpty() )
      {
      return;
      }

    int cols = styleTable.getColumnCount();

    Polylist columns = copiedCells;

    for( int col = 0; col < cols && columns.nonEmpty(); col++ )
      {
      Polylist column = (Polylist)columns.first();

      for( int rowIndex = 0; rowIndex < rows.length && column.nonEmpty(); rowIndex++ )
        {
        int row = rows[rowIndex];

        if( getTableModel().isCellEditable(row, col) )
          {
          // Allow pasting of all cells, not just pattern cells
          // (although these may cause errors)
          Object first = column.first();
          if( first instanceof Polylist )
            {
            String contents = ((Polylist)first).toStringSansParens();
            setCell(contents, row, col, SILENT);
            }
          else
            {
            setCell(first.toString(), row, col, SILENT);
            }
          }
        column = column.rest();
        }
      columns = columns.rest();
      }
    }

    private void copyRowButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyRowButtonActionPerformed
    {//GEN-HEADEREND:event_copyRowButtonActionPerformed
      int rows[] = styleTable.getSelectedRows();
      copyRows(rows);
}//GEN-LAST:event_copyRowButtonActionPerformed

  /**
   * Copy selected rows.
   * Build selected array of cells as Polylist, column by column.
   * Store the result in copiedCells.
   */
  public void copyRows(int rows[])
    {
    int cols = styleTable.getColumnCount();

    // build array column by column, back to front

    Polylist patternArray = Polylist.nil;
    for( int col = cols - 1; col >= 0; col-- )
      {
      Polylist patternColumn = Polylist.nil;
      for( int rowIndex = rows.length - 1; rowIndex >= 0; rowIndex-- )
        {
        int row = rows[rowIndex];
        Object contents = styleTable.getValueAt(row, col);
        patternColumn = patternColumn.cons(processCellContents(contents, row,
                col));
        }
      patternArray = patternArray.cons(patternColumn);
      }

    copiedCells = patternArray;
    clipboardTextField.setText(patternArray.toString());
    }

  private void cutRowButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cutRowButtonActionPerformed
  {//GEN-HEADEREND:event_cutRowButtonActionPerformed
    int rows[] = styleTable.getSelectedRows();
    cutRows(rows);
}//GEN-LAST:event_cutRowButtonActionPerformed

  /**
   * Empty out selected rows, after first copying content.
   */
  public void cutRows(int rows[])
    {
    copyRows(rows);

    int cols = styleTable.getColumnCount();

    // build array column by column, back to front

    for( int col = cols - 1; col >= 0; col-- )
      {
      for( int rowIndex = rows.length - 1; rowIndex >= 0; rowIndex-- )
        {
        int row = rows[rowIndex];
        setCell("", row, col, SILENT);
        }
      }
    }

    private void tempoComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tempoComboBoxActionPerformed
    {//GEN-HEADEREND:event_tempoComboBoxActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_tempoComboBoxActionPerformed

    private void pasteColumnButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pasteColumnButtonActionPerformed
    {//GEN-HEADEREND:event_pasteColumnButtonActionPerformed
      int selectedColumns[] = columnModel.getSelectedColumns();
      pasteColumns(selectedColumns);
}//GEN-LAST:event_pasteColumnButtonActionPerformed

  private void chordTypeComboBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chordTypeComboBoxActionPerformed
  {//GEN-HEADEREND:event_chordTypeComboBoxActionPerformed
  if( lastRuleClicked instanceof ChordPatternDisplay )
    {
    maybePlay((ChordPatternDisplay)lastRuleClicked);
    }
  }//GEN-LAST:event_chordTypeComboBoxActionPerformed

    private void accompanimentSwingTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_accompanimentSwingTextFieldKeyTyped
      //Prevent user from entering non-numerical data.
      int k = evt.getKeyChar();
      if( (k > 47 && k < 58) || (k == 8) || (k == 46) )
        {
        }
      else
        {
        evt.setKeyChar((char)KeyEvent.VK_CLEAR);
        }
    }//GEN-LAST:event_accompanimentSwingTextFieldKeyTyped

    private void swingTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_swingTextFieldKeyTyped
      //Prevent user from entering non-numerical data.
      int k = evt.getKeyChar();
      if( (k > 47 && k < 58) || (k == 8) || (k == 46) )
        {
        }
      else
        {
        evt.setKeyChar((char)KeyEvent.VK_CLEAR);
        }
    }//GEN-LAST:event_swingTextFieldKeyTyped

    private void denomFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_denomFieldKeyTyped
      //Prevent user from entering non-numerical data.
      int k = evt.getKeyChar();
      if( (k > 47 && k < 58) || (k == 8) )
        {
        }
      else
        {
        evt.setKeyChar((char)KeyEvent.VK_CLEAR);
        }
    }//GEN-LAST:event_denomFieldKeyTyped

    private void numFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_numFieldKeyTyped
      //Prevent user from entering non-numerical data.
      int k = evt.getKeyChar();
      if( (k > 47 && k < 58) || (k == 8) )
        {
        }
      else
        {
        evt.setKeyChar((char)KeyEvent.VK_CLEAR);
        }
    }//GEN-LAST:event_numFieldKeyTyped

    private void denomFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_denomFieldActionPerformed
      try
        {
        int denominator = Integer.parseInt(denomField.getText());
        MIDIBeast.changeDenomSig(denominator);
        }
      catch( NumberFormatException e )
        {
        }
    }//GEN-LAST:event_denomFieldActionPerformed

    private void numFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_numFieldActionPerformed
      try
        {
        int numerator = Integer.parseInt(numField.getText());
        MIDIBeast.changeNumSig(numerator);
        }
      catch( NumberFormatException e )
        {
        }
    }//GEN-LAST:event_numFieldActionPerformed

    private void pasteCellsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pasteCellsButtonActionPerformed
    {//GEN-HEADEREND:event_pasteCellsButtonActionPerformed
      pasteCopiedCells();
}//GEN-LAST:event_pasteCellsButtonActionPerformed

    private void copyCellsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyCellsButtonActionPerformed
    {//GEN-HEADEREND:event_copyCellsButtonActionPerformed
      copyCurrentCells();
}//GEN-LAST:event_copyCellsButtonActionPerformed

    private void cutCellsButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cutCellsButtonActionPerformed
    {//GEN-HEADEREND:event_cutCellsButtonActionPerformed
      cutCurrentCells();
}//GEN-LAST:event_cutCellsButtonActionPerformed

    private void commentAreaPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_commentAreaPropertyChange
      // Causes false indication. cm.changedSinceLastSave(true);
    }//GEN-LAST:event_commentAreaPropertyChange

    private void bassBaseOctaveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bassBaseOctaveStateChanged
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassBaseOctaveStateChanged

    private void bassLowOctaveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bassLowOctaveStateChanged
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassLowOctaveStateChanged

    private void bassHighOctaveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bassHighOctaveStateChanged
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassHighOctaveStateChanged

    private void bassHighNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassHighNoteActionPerformed
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassHighNoteActionPerformed

    private void bassLowNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassLowNoteActionPerformed
        cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassLowNoteActionPerformed

    private void bassBaseNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassBaseNoteActionPerformed
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_bassBaseNoteActionPerformed

    private void chordHighNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordHighNoteActionPerformed
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_chordHighNoteActionPerformed

    private void chordLowNoteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordLowNoteActionPerformed
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_chordLowNoteActionPerformed

    private void voicingTypeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingTypeActionPerformed
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_voicingTypeActionPerformed

    private void chordHighOctaveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chordHighOctaveStateChanged
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_chordHighOctaveStateChanged

    private void chordLowOctaveStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chordLowOctaveStateChanged
      cm.changedSinceLastSave(true);
    }//GEN-LAST:event_chordLowOctaveStateChanged

    private void copyColumnButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyColumnButtonActionPerformed
    {//GEN-HEADEREND:event_copyColumnButtonActionPerformed
      int selectedColumns[] = columnModel.getSelectedColumns();
      copyColumns(selectedColumns);
}//GEN-LAST:event_copyColumnButtonActionPerformed

  /** 
   * Cut selected column, provided only one column selected.
   */
    private void cutColumnButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cutColumnButtonActionPerformed
    {//GEN-HEADEREND:event_cutColumnButtonActionPerformed
      int selectedColumns[] = columnModel.getSelectedColumns();
      cutColumns(selectedColumns);
}//GEN-LAST:event_cutColumnButtonActionPerformed

  public void cutColumns(int cols[])
    {
    copyColumns(cols);

    for( int col = cols[cols.length - 1]; col >= cols[0]; col-- )
      {
      for( int row = styleTable.getRowCount() - 1; row >= 0; row-- )
        {
        if( getTableModel().isCellEditable(row, col) )
          {
          setCell(EMPTY, row, col, SILENT);
          }
        }
      }

    }

  public void copyColumns(int cols[])
    {
    Polylist patternArray = Polylist.nil;
    for( int col = cols[cols.length - 1]; col >= cols[0]; col-- )
      {
      Polylist patternColumn = Polylist.nil;
      for( int row = styleTable.getRowCount() - 1; row >= 0; row-- )
        {
        Object contents = styleTable.getValueAt(row, col);
        if( contents == null )
          {
          contents = "";
          }
        patternColumn = patternColumn.cons(Polylist.list(contents.toString()));
        }
      patternArray = patternArray.cons(patternColumn);
      }

    copiedCells = patternArray;
    clipboardTextField.setText(patternArray.toString());

    }

  PianoRoll pianoRoll = null;
        
  /**
   * Extract the first selected column to the piano roll.
   * If there is no column selected, return silently.
   * If there is no piano roll yet, create one.
   @param cols
   */
  public void styleEditorColumnToPianoRoll(int cols[])
  {
    if( cols.length <= 0 )
      {
      return;
      }
    
    int col = cols[0];    
    
    if( pianoRoll == null )
      {
      pianoRoll = new PianoRoll(this, getNewXlocation(), getNewYlocation());
      }
    
    styleEditorColumnToPianoRoll(col, pianoRoll);
  }

  public void styleEditorColumnToPianoRoll(int col, PianoRoll pianoRoll)
    {
    int tableCol = col+1;

    if( tableCol < StyleTableModel.FIRST_PATTERN_COLUMN )
    {
        return; // can't extract from row stubs
    }
    
    setExporting(true);
    
    pianoRoll.clearBars();

    int pianoRollRow = 0; // add bars to this row
    
    // Extract the bass into the Piano Roll
    int bassRow = StyleTableModel.BASS_PATTERN_ROW;
    styleEditorBassToPianoRoll(col, pianoRoll, bassRow, pianoRollRow);
    pianoRollRow += 1;

    // Extract the chord into the Piano Roll
    int chordRow = StyleTableModel.CHORD_PATTERN_ROW;
    styleEditorChordAndDrumsToPianoRoll(col, pianoRoll, chordRow, pianoRollRow);
    pianoRollRow += 1;
    
    // Extract the drums into the Piano Roll
    int drumStartRow = StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW;
    for( int row = drumStartRow; row < styleTable.getRowCount(); row++ )
      {
      styleEditorChordAndDrumsToPianoRoll(col, pianoRoll, row, pianoRollRow);
      pianoRollRow += 1;
      }  

    pianoRoll.setColumnsInOut(col, styleName);

    setExporting(false);

    pianoRoll.display();
    
    pianoRoll.updatePlayablePercussion();
    }
  
  /**
   * Since chords and drums have basically the same structures for
   * notes and rests, the two use the same code for purposes of extraction,
   * except that one would need to loop through rows in the Style Editor to
   * get all of the drums in the case where there are multiple drums.
   * @param col - column number from the Style Editor from which to extract
   * @param pianoRoll - the PianoRoll into which we want to extract column vals
   * @param styleEditorRow - the row in the Style Editor from which to extract
   *                         the pattern
   * @param pianoRollRow - the row in the PianoRoll into which the extracted
   *                       pattern's bar representation should be added
   */
  private void styleEditorChordAndDrumsToPianoRoll(int col, PianoRoll pianoRoll, int styleEditorRow, int pianoRollRow)
    {
    Color barColor, borderColor;
    int slots = 0;
    Object ob;
    
    Object contents = styleTable.getValueAt(styleEditorRow, col); 
    
    if(contents instanceof ChordPatternDisplay)
      {
      barColor = PianoRoll.CHORDCOLOR;
      borderColor = PianoRoll.BARBORDERCOLOR;
      }
    else if(contents instanceof DrumRuleDisplay)
      {
      barColor = PianoRoll.DRUMSCOLOR;
      borderColor = PianoRoll.BARBORDERCOLOR;        
      }
    else
      {
      return;
      }
      
    //System.out.println("Using pattern " + contents + ".");
      
    StringReader patternReader = new StringReader(contents.toString());

    Tokenizer in = new Tokenizer(patternReader);

    int volume = 127;
    boolean volumeImplied = true;
    
    int itemSlots;
    
    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
      if( ob instanceof String )
        {
        String item = (String)ob;
        if( item.length() > 1 )
          {
           switch( Character.toLowerCase(item.charAt(0)) )
            {
            case 'r':
              itemSlots = Duration.getDuration(item.substring(1));
             //System.out.println("\tadding rest of " + itemSlots + " slots.");
              slots += itemSlots;  // skip space
              break;

            case 'x':
              //System.out.println("\tadding hit of " + itemSlots + " slots.");
              itemSlots = Duration.getDuration(item.substring(1));
              pianoRoll.addBar(pianoRollRow, 
                               slots, 
                               itemSlots, 
                               "x", 
                               barColor,
                               borderColor, 
                               volume,
                               volumeImplied);
              volumeImplied = true;
              slots += itemSlots;
              break;
                
            case 'v':
              volume = Integer.parseInt(item.substring(1));
              volumeImplied = false;

              break;
             }
          }
        }
      }

    pianoRoll.placeEndBlock(pianoRollRow, slots);
    }
  
  /**
   * The bass is separated because it uses a different notation for the hits
   * in the pattern.
   * @param col - column number from the Style Editor from which to extract
   * @param pianoRoll - the PianoRoll into which we want to extract column vals
   * @param styleEditorRow - the row in the Style Editor from which to extract
   *                         the pattern
   * @param pianoRollRow - the row in the PianoRoll into which the extracted
   *                       pattern's bar representation should be added
   */
private void styleEditorBassToPianoRoll(int col, 
                                        PianoRoll pianoRoll, 
                                        int styleEditorRow,
                                        int pianoRollRow)
  {
    Object contents = styleTable.getValueAt(styleEditorRow, col);

    //System.out.println("exportingToPianoRoll bass pattern " + contents + ".");    

    StringReader patternReader = new StringReader(contents.toString());

    Tokenizer in = new Tokenizer(patternReader);

    Object ob;

    int slots = 0;

    boolean patternExists = false;
    
    int volume = 127;
    boolean volumeImplied = true;

    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
        BassPatternElement element 
                = BassPatternElement.makeBassPatternElement(ob);

        //System.out.println("export ob = " + ob +  ", bassPatternElement = " + element);

        if( element != null )
            {
            // null could be due to a reported error.
            if( element.nonRest() )
            {
                //System.out.println("element = " + element);
                if( element.getNoteType() == BassPatternElement.BassNoteType.VOLUME )
                  {
                    volume = Integer.parseInt(element.getDurationString());
                    volumeImplied = false;
                  }
                else
                  {
                  PianoRollBassBar bar = new PianoRollBassBar(slots, 
                                                              element, 
                                                              volume,
                                                              volumeImplied,
                                                              pianoRoll);
                  volumeImplied = true;
                  pianoRoll.addBar(bar);
                  patternExists = true;
                  slots += element.getSlots();
                  }
            }
            else
              {
              slots += element.getSlots();
              }
            }
      }
    if( patternExists )
      {
      pianoRoll.placeEndBlock(PianoRoll.BASS_ROW, slots);
      }
  }
           
  
  /**
   * Import the first selected column from the piano roll.
   * If there is no column selected, create one.
   * If there is no piano roll yet, return silently.
   * @param cols
   * @param pianoRoll
   */

public void pianoRollToStyleEditorColumn(PianoRoll pianoRoll, int col)
  {
    if( pianoRoll == null )
      {
        return;
      }

    int tableCol = col;       // for playing from pianoroll

    ArrayList<PianoRollBar> bars = pianoRoll.getSortedBars();

    int styleEditorRow = StyleTableModel.BASS_PATTERN_ROW;
    int lastPianoRollRow = 0;

    StringBuilder patternBuffer = new StringBuilder();
    int nextSlot = 0;
    int volume = 127;
    
    for( Iterator e = bars.iterator(); e.hasNext(); )
      {
        PianoRollBar bar = (PianoRollBar) e.next();

        int barRow = bar.getRow();

        for( ; barRow > lastPianoRollRow; lastPianoRollRow++ )
          {
            // A new row is starting.

            // Flush the pattern to the StyleEditor
            setCell(patternBuffer.toString(), styleEditorRow, tableCol, SILENT);
            patternBuffer = new StringBuilder();

            // Increment the row of the style editor
            switch( styleEditorRow )
              {
                case StyleTableModel.BASS_PATTERN_ROW:
                    styleEditorRow = StyleTableModel.CHORD_PATTERN_ROW;
                    break;

                case StyleTableModel.CHORD_PATTERN_ROW:
                    styleEditorRow = StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW;
                    break;

                default:
                    styleEditorRow++;
                    break;
              }
            
          nextSlot = 0;
          }

        int gap = bar.getStartSlot() - nextSlot;
        if( gap > 0 )
          {
            patternBuffer.append(REST_STRING);
            patternBuffer.append(Note.getDurationString(gap));
            patternBuffer.append(" ");
          }

        if( !(bar instanceof PianoRollEndBlock) )
          {
          if( !bar.getVolumeImplied() ) //&& bar.getVolume() != volume )
            {
            volume = bar.getVolume();
            patternBuffer.append("V");
            patternBuffer.append(volume);
            patternBuffer.append(" ");
            }
          patternBuffer.append(bar.getText());
          patternBuffer.append(" ");
          nextSlot = bar.getEndSlot() + 1;
          }
      }

    // Final flush
    setCell(patternBuffer.toString(), styleEditorRow, tableCol, SILENT);

    pianoRoll.setColumnOut(col);
  }


/**
 * Create a pattern for immediate playing.
 * This looks similar to other code.
 @param pianoRoll
 @param desiredRow
 @return
 */

public Playable getPlayableFromPianoRollRow(PianoRoll pianoRoll, int desiredRow)
  {
    ArrayList<PianoRollBar> bars = pianoRoll.getSortedBars();

    StringBuilder patternBuffer = new StringBuilder();
    int nextSlot = 0;

    int volume = 127;
    
    for( Iterator e = bars.iterator(); e.hasNext(); )
      {
        PianoRollBar bar = (PianoRollBar) e.next();

        int barRow = bar.getRow();

        if( barRow == desiredRow )
          {

        int gap = bar.getStartSlot() - nextSlot;
        if( gap > 0 )
          {
            patternBuffer.append(REST_STRING);
            patternBuffer.append(Note.getDurationString(gap));
            patternBuffer.append(" ");
          }

        if( !(bar instanceof PianoRollEndBlock) )
          {
          if( !bar.getVolumeImplied() ) //bar.getVolume() != volume )
            {
            volume = bar.getVolume();
            patternBuffer.append("V");
            patternBuffer.append(volume);
            patternBuffer.append(" ");
            }
          patternBuffer.append(bar.getText());
          patternBuffer.append(" ");
          nextSlot = bar.getEndSlot() + 1;
          }
        }
      }
//System.out.println("row " + desiredRow + " pattern = " + patternBuffer.toString());

Playable display;

    switch( desiredRow )
      {
        case 0: display = new BassPatternDisplay(notate, cm, this); 
        break;

        case 1: display = new ChordPatternDisplay(notate, cm, this); 
        break;

        default:
             display = new DrumRuleDisplay(notate, cm, this);
             String instrument = getRowHeaders().get(desiredRow - PianoRoll.BASS_CHORD_ROWS + StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW);

             ((DrumRuleDisplay)display).setInstrument(instrument);
             break;
      }

    display.setDisplayText(patternBuffer.toString());

    return display;
  }


/**
 * Create a drum pattern for immediate playing.
 * Selected drums are determined by the array rowButton,
 * which is passed in from PianoRoll.
 *
 @param pianoRoll
 @param desiredRow
 @return
 */

public Playable getPlayablePercussionFromPianoRoll(PianoRoll pianoRoll, 
                                                   AbstractButton rowButton[])
  {
    ArrayList<PianoRollBar> bars = pianoRoll.getSortedBars();

    DrumPatternDisplay drumPatternDisplay = new DrumPatternDisplay(notate, cm, this);

    DrumRuleDisplay rule;
    StringBuffer patternBuffer = new StringBuffer();

    int nextSlot = 0;

    int row = 2;    // Start of percussion rows

    int volume = 127;
    
    for( Iterator e = bars.iterator(); e.hasNext(); )
      {

        PianoRollBar bar = (PianoRollBar) e.next();

        int barRow = bar.getRow();

        if( barRow > row || !e.hasNext() )
          {
            // Possibly dump accumulated pattern

            if( rowButton[row].isSelected() && !patternBuffer.toString().trim().equals("") )
              {
                // Dump only if non-empty
                rule = new DrumRuleDisplay(notate, cm, this);
                String instrument = getRowHeaders().get(
                    row - PianoRoll.BASS_CHORD_ROWS + StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW);

                rule.setInstrument(instrument);
                rule.setDisplayText(patternBuffer.toString());
                drumPatternDisplay.addRule(rule);

                //System.out.println("rule " + instrument + " = " + rule.getDisplayText());
              }

            // Start new row
            patternBuffer = new StringBuffer();
            nextSlot = 0;
            row = barRow;
          }

        if( barRow == row )
          {
            // Accumulate pattern in row
            int gap = bar.getStartSlot() - nextSlot;
            if( gap > 0 )
              {
                patternBuffer.append(REST_STRING);
                patternBuffer.append(Note.getDurationString(gap));
                patternBuffer.append(" ");
              }

            if( !(bar instanceof PianoRollEndBlock) )
              {
                 if( !bar.getVolumeImplied() ) // bar.getVolume() != volume )
                    {
                    volume = bar.getVolume();
                    patternBuffer.append("V");
                    patternBuffer.append(volume);
                    patternBuffer.append(" ");
                    }
                patternBuffer.append(bar.getText());
                patternBuffer.append(" ");
                nextSlot = bar.getEndSlot() + 1;
              }
          }
      }
    return drumPatternDisplay;
  }


  public void pasteColumns(int cols[])
    {
    Polylist columns = copiedCells;
    int rows = styleTable.getRowCount();

    for( int colIndex = 0; colIndex < cols.length && columns.nonEmpty(); colIndex++ )
      {
      int col = cols[colIndex];
      if( col >= styleTable.getColumnCount() )
        {
        return;
        }

      Polylist column = (Polylist)columns.first();

      for( int row = 0; row < rows && column.nonEmpty(); row++ )
        {
        if( getTableModel().isCellEditable(row, col) )
          {
          // Allow pasting of all cells, not just pattern cells
          // (although these may cause errors)
          Object first = column.first();
          if( first instanceof Polylist )
            {
            setCell(((Polylist)first).toStringSansParens(), row, col, SILENT);
            }
          else
            {
            setCell(first.toString(), row, col, SILENT);
            }
          }
        column = column.rest();
        }
      columns = columns.rest();
      }
    }
  
    /**
     * @return The Style Editor JTable, called styleTable.
     */
    public JTable getStyleTable() {
        return styleTable;
    }

    public String getStyleName()
    {
        return styleName;
    }
    
    private void newButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newButtonActionPerformed
      newStyle();
    }//GEN-LAST:event_newButtonActionPerformed

    private void saveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveButtonActionPerformed
      saveStyle();
    }//GEN-LAST:event_saveButtonActionPerformed

    private void openButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openButtonActionPerformed
      openStyle();
    }//GEN-LAST:event_openButtonActionPerformed

    private void addColumnButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addColumnButtonActionPerformed
    {//GEN-HEADEREND:event_addColumnButtonActionPerformed
      getTableModel().newPatternColumn();
}//GEN-LAST:event_addColumnButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      int op = closeWindow();
      if( op != 1 )
        {
        dispose();
        }
    }//GEN-LAST:event_formWindowClosing

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
      generatePref.setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

      if( importBassCheckBox.isSelected() )
        {
        MIDIBeast.importBass = true;
        }
      else
        {
        MIDIBeast.importBass = false;
        }

      if( importChordCheckBox.isSelected() )
        {
        MIDIBeast.importChords = true;
        }
      else
        {
        MIDIBeast.importChords = false;
        }

      if( importDrumCheckBox.isSelected() )
        {
        MIDIBeast.importDrums = true;
        }
      else
        {
        MIDIBeast.importDrums = false;
        }

      if( showExtractionCheckBox.isSelected() )
        {
        MIDIBeast.showExtraction = true;
        }
      else
        {
        MIDIBeast.showExtraction = false;
        }

      if( chordTonesCheckBox.isSelected() )
        {
        MIDIBeast.chordTones = true;
        }
      else
        {
        MIDIBeast.chordTones = false;
        }

      if( mergeBassRestsCheckBox.isSelected() )
        {
        MIDIBeast.mergeBassRests = true;
        }
      else
        {
        MIDIBeast.mergeBassRests = false;
        }

      MIDIBeast.maxBassPatternLength =
              maxPatternLengthComboBox.getSelectedIndex();  // FIX: Not good to use a global this way.
      
      MIDIBeast.maxChordPatternLength =
              maxChordPatternLengthComboBox.getSelectedIndex();
      
      MIDIBeast.maxDrumPatternLength =
              maxDrumPatternLengthComboBox.getSelectedIndex();

      generatePref.setVisible(false);
    }//GEN-LAST:event_okButtonActionPerformed

    public int getNumColumns()
    {
        return getTableModel().getNumColumns();
    }

    private void importDrumCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importDrumCheckBoxActionPerformed
      //do nothing
    }//GEN-LAST:event_importDrumCheckBoxActionPerformed

    private void chordTonesCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordTonesCheckBoxActionPerformed
      //do nothing
    }//GEN-LAST:event_chordTonesCheckBoxActionPerformed

    private void styEditKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_styEditKeyPressed
      setEditMenuStatus();
    }//GEN-LAST:event_styEditKeyPressed

    private void styEditFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_styEditFocusGained
      setEditMenuStatus();
    }//GEN-LAST:event_styEditFocusGained

    private void styEditActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styEditActionPerformed
      setEditMenuStatus();
    }//GEN-LAST:event_styEditActionPerformed

    private void styEditMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_styEditMouseClicked
      setEditMenuStatus();
    }//GEN-LAST:event_styEditMouseClicked

    private void styFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styFileActionPerformed
      //do nothing.
    }//GEN-LAST:event_styFileActionPerformed

    private void openStyleMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openStyleMIActionPerformed
      openStyle();
    }//GEN-LAST:event_openStyleMIActionPerformed

    private void saveStyleAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveStyleAsActionPerformed
      saveStyleAs();
    }//GEN-LAST:event_saveStyleAsActionPerformed

    private void styHelpMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styHelpMIActionPerformed
      helpDialog.setSize(800, 500);
      helpDialog.setLocationRelativeTo(this);
      helpDialog.setVisible(true);
    }//GEN-LAST:event_styHelpMIActionPerformed

  private boolean initLocationPreviewPreferences = false;

    private void generatePrefMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generatePrefMIActionPerformed
      generatePref.setLocationRelativeTo(this);
      generatePref.setSize(300, 500);
      generatePref.setTitle("Extraction Preferences");
      generatePref.setVisible(true);
    }//GEN-LAST:event_generatePrefMIActionPerformed

    private void generateMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateMIActionPerformed
      generateStyleFromMidi();
    }//GEN-LAST:event_generateMIActionPerformed

    private void pasteCellsMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pasteCellsMIActionPerformed
    {//GEN-HEADEREND:event_pasteCellsMIActionPerformed
      pasteCopiedCells();
      styleTable.editingCanceled(null);
}//GEN-LAST:event_pasteCellsMIActionPerformed

    private void copyCellsMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyCellsMIActionPerformed
    {//GEN-HEADEREND:event_copyCellsMIActionPerformed
      copyCurrentCells();
      styleTable.editingCanceled(null);
}//GEN-LAST:event_copyCellsMIActionPerformed

    private void exitStyleGenMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitStyleGenMIActionPerformed
      closeWindow();
    }//GEN-LAST:event_exitStyleGenMIActionPerformed

    private void saveStyleMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveStyleMIActionPerformed
      saveStyle();
    }//GEN-LAST:event_saveStyleMIActionPerformed

    private void newStyleMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newStyleMIActionPerformed
      // open a new StyleEditor Window
      StyleEditor m = new StyleEditor(notate);
      m.pack();
      m.setLocationRelativeTo(this);
      m.setLocation(m.getX() + WindowRegistry.defaultXnewWindowStagger, 
                    m.getY() + WindowRegistry.defaultYnewWindowStagger);
      m.setVisible(true);

    }//GEN-LAST:event_newStyleMIActionPerformed

    private void cutCellsMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cutCellsMIActionPerformed
    {//GEN-HEADEREND:event_cutCellsMIActionPerformed
      cutCurrentCells();
      styleTable.editingCanceled(null);
}//GEN-LAST:event_cutCellsMIActionPerformed

    private void closeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeBtnActionPerformed
      closeWindow();
    }//GEN-LAST:event_closeBtnActionPerformed

    private void saveStyleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveStyleBtnActionPerformed
      saveStyle();
    }//GEN-LAST:event_saveStyleBtnActionPerformed

    private void clipboardTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clipboardTextFieldActionPerformed
    {//GEN-HEADEREND:event_clipboardTextFieldActionPerformed
      // TODO add your handling code here:
}//GEN-LAST:event_clipboardTextFieldActionPerformed

    private void rowField2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rowField2ActionPerformed
    {//GEN-HEADEREND:event_rowField2ActionPerformed
      // TODO add your handling code here:
}//GEN-LAST:event_rowField2ActionPerformed

    private void columnField0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_columnField0ActionPerformed
    {//GEN-HEADEREND:event_columnField0ActionPerformed
      // TODO add your handling code here:
}//GEN-LAST:event_columnField0ActionPerformed

    private void columnField2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_columnField2ActionPerformed
    {//GEN-HEADEREND:event_columnField2ActionPerformed
      // TODO add your handling code here:
}//GEN-LAST:event_columnField2ActionPerformed

    private void chordPitchComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordPitchComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chordPitchComboBoxActionPerformed

    private void muteChordToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_muteChordToggleActionPerformed
    if( muteChordToggle.isSelected() )
      {
      muteChordToggle.setText("Mute");
      muteChordToggle.setBackground(ON_COLOR);
      }
    else
     {
     muteChordToggle.setText("Play");
     muteChordToggle.setBackground(OFF_COLOR);
     }
    }//GEN-LAST:event_muteChordToggleActionPerformed

    private void playBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playBtnActionPerformed
    {//GEN-HEADEREND:event_playBtnActionPerformed
      pauseBtn.setEnabled(true);
      stopBtn.setEnabled(true);
      notate.playScore();
    }//GEN-LAST:event_playBtnActionPerformed

    private void pauseBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pauseBtnActionPerformed
    {//GEN-HEADEREND:event_pauseBtnActionPerformed
      pauseBtn.setEnabled(false);
      stopBtn.setEnabled(true);
      playBtn.setEnabled(true);
      notate.pauseScore();
    }//GEN-LAST:event_pauseBtnActionPerformed

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopBtnActionPerformed
    {//GEN-HEADEREND:event_stopBtnActionPerformed
    stopPlaying();
    }//GEN-LAST:event_stopBtnActionPerformed

    private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowMIActionPerformed
    {//GEN-HEADEREND:event_closeWindowMIActionPerformed
      closeWindow();
    }//GEN-LAST:event_closeWindowMIActionPerformed

    private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMIActionPerformed
    {//GEN-HEADEREND:event_cascadeMIActionPerformed
      WindowRegistry.cascadeWindows(this);
    }//GEN-LAST:event_cascadeMIActionPerformed

    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt)//GEN-FIRST:event_windowMenuMenuSelected
    {//GEN-HEADEREND:event_windowMenuMenuSelected
      windowMenu.removeAll();
      
      windowMenu.add(closeWindowMI);
      
      windowMenu.add(cascadeMI);
      
      windowMenu.add(windowMenuSeparator);
      
      for(WindowMenuItem w : WindowRegistry.getWindows())
      {
        windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
      }
      
      windowMenu.repaint();
    }//GEN-LAST:event_windowMenuMenuSelected

private void maxPatternLengthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxPatternLengthComboBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_maxPatternLengthComboBoxActionPerformed

private void maxChordPatternLengthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxChordPatternLengthComboBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_maxChordPatternLengthComboBoxActionPerformed

private void maxDrumPatternLengthComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDrumPatternLengthComboBoxActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_maxDrumPatternLengthComboBoxActionPerformed

private void minDurationTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minDurationTFActionPerformed
// TODO add your handling code here:
}//GEN-LAST:event_minDurationTFActionPerformed

private void saveStyleBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveStyleBtn1ActionPerformed
saveStyleBtnActionPerformed(null);
}//GEN-LAST:event_saveStyleBtn1ActionPerformed

private void pianoRollCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pianoRollCheckBoxActionPerformed
if( pianoRollCheckBox.isSelected() ) 
  {
    usePianoRoll();
  }
else
  {
    unusePianoRoll();
    trackWithPianoRoll.setSelected(false);
    pianoRoll.setVisible(false);
  }
}//GEN-LAST:event_pianoRollCheckBoxActionPerformed

private void stopPlayingActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopPlayingActionPerformed
  {//GEN-HEADEREND:event_stopPlayingActionPerformed
    stopPlaying();
  }//GEN-LAST:event_stopPlayingActionPerformed

private void beatsField0ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_beatsField0ActionPerformed
  {//GEN-HEADEREND:event_beatsField0ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_beatsField0ActionPerformed

private void beatsField1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_beatsField1ActionPerformed
  {//GEN-HEADEREND:event_beatsField1ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_beatsField1ActionPerformed

private void beatsField2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_beatsField2ActionPerformed
  {//GEN-HEADEREND:event_beatsField2ActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_beatsField2ActionPerformed

private void trackWithPianoRollActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_trackWithPianoRollActionPerformed
  {//GEN-HEADEREND:event_trackWithPianoRollActionPerformed
    
  }//GEN-LAST:event_trackWithPianoRollActionPerformed

private void usePianoRoll()
{
  int selectedColumns[] = columnModel.getSelectedColumns();
  usePianoRoll(selectedColumns);
}

private void usePianoRoll(int column)
{
  int selectedColumns[] = {column};
  usePianoRoll(selectedColumns);
}

private void usePianoRoll(int selectedColumns[])
{
  styleEditorColumnToPianoRoll(selectedColumns);
  pianoRollCheckBox.setSelected(true);
}


/**
 * This should be called from PianoRoll when closing.
 */

public void unusePianoRoll()
{
  pianoRollCheckBox.setSelected(false);
  trackWithPianoRoll.setSelected(false);
}


  public ArrayList<String> getRowHeaders()
  {
    return getTableModel().getRowHeaders();
  }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accompanimentSwingTextField;
    private javax.swing.JButton addColumnButton;
    private javax.swing.JPanel bassAttrPanel;
    private javax.swing.JLabel bassBaseLabel;
    private javax.swing.JComboBox bassBaseNote;
    private javax.swing.JSpinner bassBaseOctave;
    private javax.swing.JLabel bassHighLabel;
    private javax.swing.JComboBox bassHighNote;
    private javax.swing.JSpinner bassHighOctave;
    private javax.swing.JPanel bassHolderPane;
    private javax.swing.JLabel bassLowLabel;
    private javax.swing.JComboBox bassLowNote;
    private javax.swing.JSpinner bassLowOctave;
    private javax.swing.JLabel bassOctaveLabel;
    private javax.swing.JScrollPane bassPane;
    private javax.swing.JScrollPane bassStyleSpecScrollPane;
    private javax.swing.JPanel bassTabPanel;
    private javax.swing.JTextArea bassText;
    private javax.swing.JTextField beatsField0;
    private javax.swing.JTextField beatsField1;
    private javax.swing.JTextField beatsField2;
    private javax.swing.JLabel beatsLabel;
    private javax.swing.JLabel bpmLabel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JPanel cellsPanel;
    private javax.swing.JPanel chordAttrPanel;
    private javax.swing.JLabel chordHighLabel;
    private javax.swing.JComboBox chordHighNote;
    private javax.swing.JSpinner chordHighOctave;
    private javax.swing.JPanel chordHolderPane;
    private javax.swing.JLabel chordLowLabel;
    private javax.swing.JComboBox chordLowNote;
    private javax.swing.JSpinner chordLowOctave;
    private javax.swing.JLabel chordOctaveLabel;
    private javax.swing.JScrollPane chordPane;
    private javax.swing.JPanel chordPanel;
    private javax.swing.JComboBox chordPitchComboBox;
    private javax.swing.JScrollPane chordStyleSpecScrollPane;
    private javax.swing.JPanel chordTabPanel;
    private javax.swing.JTextArea chordText;
    private javax.swing.JCheckBox chordTonesCheckBox;
    private javax.swing.JComboBox chordTypeComboBox;
    private javax.swing.JPanel clipboardPanel;
    private javax.swing.JTextField clipboardTextField;
    private javax.swing.JButton closeBtn;
    private javax.swing.JPanel closeButtonPanel;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JTextField columnField0;
    private javax.swing.JTextField columnField1;
    private javax.swing.JTextField columnField2;
    private javax.swing.JLabel columnLabel;
    private javax.swing.JPanel columnPanel;
    private javax.swing.JTextArea commentArea;
    private javax.swing.JScrollPane commentScrollPane;
    private javax.swing.JPanel commentsPanel;
    private javax.swing.JPanel compSwingPanel1;
    private javax.swing.JLabel contentLabel;
    private javax.swing.JButton copyCellsButton;
    private javax.swing.JMenuItem copyCellsMI;
    private javax.swing.JButton copyColumnButton;
    private javax.swing.JButton copyRowButton;
    private javax.swing.JButton cutCellsButton;
    private javax.swing.JMenuItem cutCellsMI;
    private javax.swing.JButton cutColumnButton;
    private javax.swing.JButton cutRowButton;
    private javax.swing.JTextField denomField;
    private javax.swing.JPanel drumHolderPane;
    private javax.swing.JScrollPane drumStyleSpecScrollPane;
    private javax.swing.JPanel drumTabPanel;
    private javax.swing.JPanel editInstructionsPanel;
    private javax.swing.JScrollPane editingPane;
    private javax.swing.JMenuItem exitStyleGenMI;
    private javax.swing.JTextArea extractionList;
    private javax.swing.JScrollPane extractionPane;
    private javax.swing.JTextArea fileFormatList;
    private javax.swing.JScrollPane fileFormatPane;
    private javax.swing.JScrollPane fileMenuPane;
    private javax.swing.JPanel filePanel;
    private javax.swing.JScrollPane generalPane;
    private javax.swing.JMenuItem generateMI;
    private javax.swing.JDialog generatePref;
    private javax.swing.JMenuItem generatePrefMI;
    private javax.swing.JDialog generationProgress;
    private javax.swing.JPanel globalAttrPanel;
    private javax.swing.JDialog helpDialog;
    private javax.swing.JTabbedPane helpTabbedPane;
    private javax.swing.JCheckBox importBassCheckBox;
    private javax.swing.JCheckBox importChordCheckBox;
    private javax.swing.JCheckBox importDrumCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JSlider masterVolumeSlider;
    private javax.swing.JComboBox maxChordPatternLengthComboBox;
    private javax.swing.JComboBox maxDrumPatternLengthComboBox;
    private javax.swing.JComboBox maxPatternLengthComboBox;
    private javax.swing.JPanel melodySwingPanel;
    private javax.swing.JTextArea menuList;
    private javax.swing.JTextArea menuList1;
    private javax.swing.JCheckBox mergeBassRestsCheckBox;
    private javax.swing.JTextField minDurationTF;
    private javax.swing.JToggleButton muteChordToggle;
    private javax.swing.JButton newButton;
    private javax.swing.JButton newRowButton;
    private javax.swing.JMenuItem newStyleMI;
    private javax.swing.JTextField numField;
    private javax.swing.JButton okButton;
    private javax.swing.JButton openButton;
    private javax.swing.JMenuItem openStyleMI;
    private javax.swing.JPanel panelInStyleScrollpane;
    private javax.swing.JButton pasteCellsButton;
    private javax.swing.JMenuItem pasteCellsMI;
    private javax.swing.JButton pasteColumnButton;
    private javax.swing.JButton pasteRowButton;
    private javax.swing.JTextArea patternHelp;
    private javax.swing.JToggleButton pauseBtn;
    private javax.swing.JScrollPane percussionPane;
    private javax.swing.JTextArea percussionText;
    private javax.swing.JCheckBoxMenuItem pianoRollCheckBox;
    private javax.swing.JButton playBtn;
    private javax.swing.JPanel playPanel;
    private javax.swing.JToggleButton playToggle;
    private javax.swing.JPanel remotePanel;
    private javax.swing.JTextField rowField0;
    private javax.swing.JTextField rowField1;
    private javax.swing.JTextField rowField2;
    private javax.swing.JLabel rowLabel;
    private javax.swing.JPanel rowPanel;
    private javax.swing.JButton saveButton;
    private javax.swing.JMenuItem saveStyleAs;
    private javax.swing.JButton saveStyleBtn;
    private javax.swing.JButton saveStyleBtn1;
    private javax.swing.JMenuItem saveStyleMI;
    private javax.swing.JCheckBox showExtractionCheckBox;
    private javax.swing.JLabel slashLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JButton stopBtn;
    private javax.swing.JButton stopPlaying;
    private javax.swing.JMenu styEdit;
    private javax.swing.JMenu styFile;
    private javax.swing.JMenu styGenerate;
    private javax.swing.JMenu styHelp;
    private javax.swing.JMenuItem styHelpMI;
    private javax.swing.JMenuBar styMenuBar;
    private javax.swing.JTextField styleEditorStatusTF;
    private javax.swing.JPanel stylePanel;
    private javax.swing.JLabel stylePic;
    private javax.swing.JScrollPane styleScrollpane;
    private javax.swing.JPanel styleSpecificationPanel;
    private javax.swing.JTable styleTable;
    private javax.swing.JTextField styleTextField0;
    private javax.swing.JTextField styleTextField1;
    private javax.swing.JTextField styleTextField2;
    private javax.swing.JTextField swingTextField;
    private javax.swing.JComboBox tempoComboBox;
    private javax.swing.JPanel timeSigPanel;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JCheckBoxMenuItem trackWithPianoRoll;
    private javax.swing.JLabel voicingLabel;
    private javax.swing.JComboBox voicingType;
    private javax.swing.JLabel volLabel;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables
  
    
/**
 * Get X location for new frame cascaded from original.
 * @return 
 */

public int getNewXlocation()
  {
    return (int)getLocation().getX() + WindowRegistry.defaultXnewWindowStagger;
  }


/**
 * Get Y location for new frame cascaded from original.
 * @return 
 */

public int getNewYlocation()
  {
    return (int)getLocation().getY() + WindowRegistry.defaultYnewWindowStagger;
  }

public void stopPlaying()
  {
      pauseBtn.setEnabled(false);
      stopBtn.setEnabled(false);
      playBtn.setEnabled(true);

      notate.stopPlaying();
  }

boolean exportingToPianoRoll = false;
boolean pianoRollWasLooping  = false;

private void setExporting(boolean value)
  {
    //System.out.println("exporting = " + value);
    exportingToPianoRoll = value;
    
    if( value )
      {
        pianoRollWasLooping = pianoRoll.getLooping();
        pianoRoll.setLooping(false);
        stopPlaying();
      }
    else if( pianoRollWasLooping )
      {
        pianoRoll.setLooping(true);
      }
  }

}

