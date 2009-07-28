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

import imp.data.*;
import imp.Constants;
import polya.*;

import javax.swing.table.*;
import javax.swing.JTable;
import java.util.Vector;

/**
 *
 * @author Robert Keller 
 */

public class StyleTableModel extends DefaultTableModel implements TableModel, Constants {
  
    /** These determine number of table columns and rows */
  
    /* Caution: If initialNumberOfPatterns is less than 92, the header gets out of sync
       with the table when new columns are added. I have no idea why. It could be a bug
       in Java.  They sync up again if the column boundary is adjusted in any minor way
       and the scrollbar is scrolled slightly, but not unless this is done.  Also, adding
       a new column disturbs the width of the initial columns.
     */
  
    public static int initialNumberOfPatterns = 92;
    public static int initialExtraColumns = 2;
    
    int initialPercussionInstruments = 30;  
    // FIX: An out-of-bounds error can occur if table is enlarged sufficiently
    //       at imp.gui.StyleTableModel.isChordCell(StyleTableModel.java:333)
    //    at imp.gui.StyleEditor.isChordCell(StyleEditor.java:2017)
    //    at imp.gui.StyleCellEditor.getCellEditorValue(StyleCellEditor.java:281)
    // Rows look like they are being added when the table is stretched, but
    // apparently they are  not.

    
    int patternColumnCount = 0;
    int rowCount = 0;

    /* Pattern categories */
    public static String BASS = "Bass";
    public static String CHORD = "Chord";
    public static String PERCUSSION = "Percussion";
    
    public static String categoryName[] = {BASS, CHORD, PERCUSSION};
    
    public static final int BassCategory = 0;
    public static final int ChordCategory = 1;
    public static final int PercussionCategory = 2;
    
    private static String emptyCell = ""; // Change to make emptiness visible
    
    public static String BLANK = "";   // For an intentionally-blank cell
    public static String PATTERN = ""; // Prefix for naming patterns numerically
    
    public static int numberFixedRowHeaders = 8;
    
    /* 
     * In the following, the literal string is used for table lookup, so
     * these names should not be changed casually.  In particular, the method
     * addPatternColumn will need to be changed to conform.
     */
    
    public static String initialRowHeaders[] =
      {
      "Bass Beats",
      "Bass Weight",
      BASS,
      "Chord Beats",
      "Chord Weight",
      CHORD,
      "Drum Beats",
      "Drum Weight",
      "Acoustic Bass Drum",
      "Acoustic Snare",
      "Ride Cymbal 1",
      "Closed Hi-Hat",
      "Open Hi-Hat",
      PERCUSSION
     };
    
    /** Defining list of row names */
    Vector<String> rowNames;

    Vector<Long> instrumentNumbers;
    
    int minRowCount;
    
    JTable theTable;
    
    public static final String  UNNAMED_PATTERN_NAME = "";
    public static final Integer DEFAULT_PATTERN_WEIGHT = 10;
    public static final Integer DEFAULT_PATTERN_BEATS = 0;
    public static final String  DEFAULT_INSTRUMENT = PERCUSSION;
    
    // Designated columns

    public static final int INSTRUMENT_INCLUDE_COLUMN = 0;
    public static final int INSTRUMENT_VOLUME_COLUMN = 1;
    public static final int FIRST_PATTERN_COLUMN = 2;
    public static final int PATTERN_COLUMN_BASE = FIRST_PATTERN_COLUMN - 1;
    
    // Designated rows

    public static final int BASS_PATTERN_BEATS_ROW          = 0;
    public static final int BASS_PATTERN_WEIGHT_ROW         = 1;
    public static final int BASS_PATTERN_ROW                = 2;
    
    public static final int CHORD_PATTERN_BEATS_ROW         = 3;
    public static final int CHORD_PATTERN_WEIGHT_ROW        = 4;
    public static final int CHORD_PATTERN_ROW               = 5;
    
    public static final int DRUM_PATTERN_BEATS_ROW          = 6;
    public static final int DRUM_PATTERN_WEIGHT_ROW         = 7;

    public static final int FIRST_INSTRUMENT_ROW            = 2;
    public static final int FIRST_PERCUSSION_INSTRUMENT_ROW = 8;
    
    int lastPatternColumn = PATTERN_COLUMN_BASE;
    int lastPercussionrowUsed = FIRST_PERCUSSION_INSTRUMENT_ROW - 1;
    
    public static Boolean POSITIVE_INCLUDE_VALUE = Boolean.TRUE;
    public static Boolean NEGATIVE_INCLUDE_VALUE = Boolean.FALSE;
    public static Boolean INITIAL_INCLUDE_VALUE = POSITIVE_INCLUDE_VALUE;
    
    public static Integer INITIAL_INSTRUMENT_VOLUME = 100;

    private static boolean setValueTraceValue = false;
    
    /**
   * Creates a new instance of StyleTableModel
   */
    
    public StyleTableModel(JTable theTable) {
      resetPatterns();
      this.theTable = theTable;
      minRowCount = initialRowHeaders.length + initialPercussionInstruments;
      // doesn't help: theTable.setAutoCreateColumnsFromModel(true);
      
      theTable.setColumnModel(new StyleTableColumnModel(theTable));

       // Create row headers. These will determine how long columns are.
      
      initRowHeaders();
            
       // Create the  non-pattern columns

        addEmptyColumn("Use");
        addEmptyColumn("Volume");

        // Add columns  for initial blank patterns
      
      while( patternColumnCount < initialNumberOfPatterns  )
        {
        newPatternColumn();
        }

     
     // Indicate int two columns that all instruments are included and set their volumes to the default
     setValueAt(INITIAL_INCLUDE_VALUE,     BASS_PATTERN_ROW,  INSTRUMENT_INCLUDE_COLUMN);
     setValueAt(INITIAL_INSTRUMENT_VOLUME, BASS_PATTERN_ROW,  INSTRUMENT_VOLUME_COLUMN);
     setValueAt(INITIAL_INCLUDE_VALUE,     CHORD_PATTERN_ROW, INSTRUMENT_INCLUDE_COLUMN);
     setValueAt(INITIAL_INSTRUMENT_VOLUME, CHORD_PATTERN_ROW, INSTRUMENT_VOLUME_COLUMN);
     int nrows =  getRowCount();
     for( int i = FIRST_PERCUSSION_INSTRUMENT_ROW; i < nrows; i++ )
      {
      setValueAt(INITIAL_INCLUDE_VALUE,     i, INSTRUMENT_INCLUDE_COLUMN);
      setValueAt(INITIAL_INSTRUMENT_VOLUME, i, INSTRUMENT_VOLUME_COLUMN);
      }
     
    }
    
   /** Defining list of row names */

    public void initRowHeaders()
  {
    rowNames = new Vector<String>();
    rowCount = 0;
    
    for( ; rowCount < initialRowHeaders.length; rowCount++ )
        {
        rowNames.addElement(initialRowHeaders[rowCount]);
        }

      // Create header rows for percussion instruments
      
      for( int i = 1; i <= initialPercussionInstruments; i++ )
        {
        rowNames.addElement(DEFAULT_INSTRUMENT);
        rowCount++;
        }
   }

    
  public Vector<String> getRowHeaders()
  {
    return rowNames;
  }

                
 /** Initialize one pattern column: used when loading style file. */

   public void initializePatternColumn(int j)
   {
   // Set default values for pattern
   setValueAt(DEFAULT_PATTERN_BEATS,    BASS_PATTERN_BEATS_ROW,    j);
   setValueAt(DEFAULT_PATTERN_WEIGHT,   BASS_PATTERN_WEIGHT_ROW,   j);
   setValueAt(BLANK,                    BASS_PATTERN_ROW,          j);
   setValueAt(DEFAULT_PATTERN_BEATS,    CHORD_PATTERN_BEATS_ROW,   j);
   setValueAt(DEFAULT_PATTERN_WEIGHT,   CHORD_PATTERN_WEIGHT_ROW,  j);
   setValueAt(BLANK,                    CHORD_PATTERN_ROW,         j);
   setValueAt(DEFAULT_PATTERN_BEATS,    DRUM_PATTERN_BEATS_ROW,    j);
   setValueAt(DEFAULT_PATTERN_WEIGHT,   DRUM_PATTERN_WEIGHT_ROW,   j);
   int numRows = getRowCount();
   for( int row = FIRST_PERCUSSION_INSTRUMENT_ROW; row < numRows; row ++ )
     {
     setValueAt(emptyCell, row, j);
     }
   }
 
 public void newPatternColumn()
 {
   addPatternColumn(PATTERN + (++patternColumnCount));
 }
 
/** Add one empty column, with size determined by the number of rows. */

 public void addEmptyColumn(String name)
   {
    
    Vector<Object> columnContents = new Vector<Object>();
    
    //columnContents.addElement(name);
    
    int numRows = Math.max(minRowCount, getRowCount());
    for( int j = 0; j < numRows; j++)
      {
      columnContents.addElement(emptyCell);
      }
    
    addColumn(name, columnContents);

     }
 
 /** Add one pattern column, with size determined by the number of rows. 
  *  Note that this needs to be changed if there is any change in the order
  *  of rows. See also the "designated row" constants.
  */

 public void addPatternColumn(String name)
   {
    // Initialized pattern column
   
    Vector<Object> columnContents = new Vector<Object>();
    
    // For Bass
    columnContents.addElement(DEFAULT_PATTERN_BEATS);
    columnContents.addElement(DEFAULT_PATTERN_WEIGHT);
    columnContents.addElement(BLANK);  // To allow for instrument sub-header
    
    // For Chord
    columnContents.addElement(DEFAULT_PATTERN_BEATS);
    columnContents.addElement(DEFAULT_PATTERN_WEIGHT);
    columnContents.addElement(BLANK);  // To allow for instrument sub-header
    
    // For Drum
    columnContents.addElement(DEFAULT_PATTERN_BEATS);
    columnContents.addElement(DEFAULT_PATTERN_WEIGHT);
    
    int numRows = Math.max(minRowCount, getRowCount());
    for( int j = FIRST_PERCUSSION_INSTRUMENT_ROW; j < numRows; j++)
      {
      columnContents.addElement(emptyCell);
      }
    
    addColumn(name, columnContents);
    
    }
 
/**
 * All column additions should go through here, for monitoring.
 * However, the work is done in super.
 */
 
public void addColumn(String name, Vector<Object> contents)
{
  super.addColumn(name, contents);
}

public int getNumColumns()
{
    return super.getColumnCount()-1;
}

 public void newRow()
 {
   addRow(PERCUSSION);
 }
 
 /**
  * Add a new row to the bottom of the table.
  */
 
 public void addRow(String rowHeader)
  {
    rowNames.addElement(rowHeader);
    rowCount++;
    
    int size = getColumnCount();
    //System.out.println("add row named " + rowHeader + ", size = " + size);
    Vector<Object> row = new Vector<Object>(size);
    row.addElement(INITIAL_INCLUDE_VALUE);
    row.addElement(INITIAL_INSTRUMENT_VOLUME);
    for( int j = 2; j < size; j++)
    {
      row.addElement(emptyCell);
    }
    super.addRow(row);
    int rowNumber = getRowCount()-1;
    // System.out.println("new row " + rowNumber + " is " + row);
    fireTableRowsInserted(rowNumber, rowNumber);
    theTable.addNotify();
    theTable.tableChanged(null);
  }
 
 public void setValueTrace(boolean value)
 {
   setValueTraceValue = value;
 }
  
 /**
  * The raw interface to setting a value in a cell.
  * Consider using setCell rather than this, as it performs appropriate
  * conversions.
  *
  @param value the Object to be stored in the cell
  @param row the row in which the value is stored
  @param col the column in which the value is stored
  */
 
  public void setValueAt(Object value, int row, int col)
  {
    if( setValueTraceValue )
    {
    System.out.println("setValue at row = " + row + ", col = " + col + " to " + value);
    }
    
    try
    {
    super.setValueAt(value, row, col);
    }
    catch(ArrayIndexOutOfBoundsException e)
    {
      System.out.println("Ignoring array out of bounds setValue at " + row + ", " + col + ": " + value);
    }
  }
  
  public int lastColumnIndex() {
        return getColumnCount() - 1;
    }
    
   public int lastrowIndex() {
        return getRowCount() - 1;
    }
    
   /** 
    * Determines which cells are editable
    */
   
    public boolean isCellEditable(int row, int col) {
        // the data/cell address is constant, no matter where the cell appears onscreen.
        return ( col >= FIRST_PATTERN_COLUMN 
                  && (  row == BASS_PATTERN_WEIGHT_ROW 
                     || row == BASS_PATTERN_ROW 
                     || row == CHORD_PATTERN_WEIGHT_ROW 
                     || row == CHORD_PATTERN_ROW 
                     || row == DRUM_PATTERN_WEIGHT_ROW 
                     || row >= FIRST_PERCUSSION_INSTRUMENT_ROW
                     )
               )
            || ( (col == INSTRUMENT_INCLUDE_COLUMN || col == INSTRUMENT_VOLUME_COLUMN) 
                  && (  row == BASS_PATTERN_ROW 
                     || row == CHORD_PATTERN_ROW 
                     || row >= FIRST_PERCUSSION_INSTRUMENT_ROW
                     )
               )
            ;
    }
    
  public boolean isChordCell(int row, int col)
    {
    return getRowHeaders().elementAt(row).equals(CHORD);
    }
  
  public boolean isBassCell(int row, int col)
    {
    return getRowHeaders().elementAt(row).equals(BASS);
    }
  
  public boolean isDrumCell(int row, int col)
  {
    return row >= FIRST_PERCUSSION_INSTRUMENT_ROW;
   }
   
 /**
  * Reset the pattern counter in preparation for loading file.
  * Note for the future: Not resetting is a way to add more patterns for a file to an existing set.
  */
 
 public void resetPatterns()
 {
   for( int col = FIRST_PATTERN_COLUMN; col <= lastPatternColumn; col++ )
     {
     initializePatternColumn(col);
     }
   
   rowNames = getRowHeaders();
 }
    
 /**
  * Ensure there is space when loading patterns from file.
  */
 
 public void ensurePatternSpace()
 {
   lastPatternColumn++;
   if( lastPatternColumn >= getColumnCount() )
   {
     addColumn(PATTERN + lastPatternColumn);
   }
 }
 

public void setBassPatternWeight(float weight, int column)
 {
 setValueAt(weight, BASS_PATTERN_WEIGHT_ROW, column);
 }

public void setBassPatternBeats(double beats, int column)
 {
 setValueAt(beats, BASS_PATTERN_BEATS_ROW, column);
  }

public void setChordPatternWeight(float weight, int column)
 {
 setValueAt(weight, CHORD_PATTERN_WEIGHT_ROW, column);
 }

public void setChordPatternBeats(double beats, int column)
 {
 setValueAt(beats, CHORD_PATTERN_BEATS_ROW, column);
  }

public void setDrumPatternWeight(float weight, int column)
 {
 setValueAt(weight, DRUM_PATTERN_WEIGHT_ROW, column);
 }

public void setDrumPatternBeats(double beats, int column)
 {
 setValueAt(beats, DRUM_PATTERN_BEATS_ROW, column);
  }

/**
 * Adapting from Brandy McMenamy's code:
 * Creates one BassPattern column for each element of bassPatterns with its weight and pattern text.
 */
 
/** Put bass patterns into table, used by ExtractionEditor_1 */

// May not longer be needed, since we are getting the patterns without it.
// In any case, using lastPatternColumn is wrong. The column is now on a
// different variable for each instrument type.
 
   public void loadBassPatterns(Vector<BassPattern> bassPatterns) {
                
           for(int i = 0; i < bassPatterns.size(); i++) {
               ensurePatternSpace();
               
               setValueTrace(false);
               /*
               BassPattern pattern = bassPatterns.get(i);
               
               setValueAt(pattern, BASS_PATTERN_ROW, lastPatternColumn);
               setBassPatternWeight(pattern.getWeight(), lastPatternColumn);
               setBassPatternBeats(pattern.getDuration()/BEAT, lastPatternColumn);
               */
               setValueTrace(false);
           }

     }
   
   
/** Put chord patterns into table, used by ExtractionEditor_1 */
// May not longer be needed, since we are getting the patterns without it.
// In any case, using lastPatternColumn is wrong.
 
    public void loadChordPatterns(Vector<ChordPattern>  chordPatterns) {
         for(int i = 0; i < chordPatterns.size(); i++) {
               ensurePatternSpace();
               
              setValueTrace(false);
              /*
                ChordPattern pattern = chordPatterns.get(i);
               
               setValueAt(pattern, CHORD_PATTERN_ROW, lastPatternColumn);
               setChordPatternWeight(pattern.getWeight(), lastPatternColumn);
               setChordPatternBeats(pattern.getDuration()/BEAT, lastPatternColumn);
               */
               setValueTrace(false);
         }
     }     

    public Vector<Long> getInstrumentNumbers()
    {
        return instrumentNumbers;
    }

/** Put drum patterns into table, used by ExtractionEditor_1 */
     
     public void loadDrumPatterns(Vector<DrumPattern> drumPatterns) {
        // Keep track of instrument numbers for proper placement into rows

         instrumentNumbers = new Vector<Long>();
        
        for(int i = 0; i < drumPatterns.size(); i++) {
            ensurePatternSpace();
            
            DrumPattern pattern = drumPatterns.get(i);

            setDrumPatternWeight(pattern.getWeight(), lastPatternColumn);
            setDrumPatternBeats(pattern.getDuration()/BEAT, lastPatternColumn);
        
            //Add the curPat rules into newPat as DrumRuleDisplay objects
           Polylist rules = pattern.getDrums();
           
           int row = FIRST_PERCUSSION_INSTRUMENT_ROW;  
           // temporarily ignore which instrument is which
           
           while( rules.nonEmpty() ) {
               Polylist rule = (Polylist)rules.first();
               Long instrumentNumber = (Long)rule.first();
               
               // Find instrument number in vector
               int instIndex = instrumentNumbers.indexOf(instrumentNumber);
               if( instIndex < 0 )
               {
                 // not found; add it in
                 instIndex = instrumentNumbers.size();
                 instrumentNumbers.add(instrumentNumber);
                 String instrumentName = MIDIBeast.drumNames[instrumentNumber.intValue()-35]; // FIX!
                 ++lastPercussionrowUsed;       // One more percussion instrument

                 if( lastPercussionrowUsed < getRowCount() )
                   {
                   }
                 else
                   {
                   Vector<Object> newRow = new Vector<Object>();  // FIX!
                   addRow(newRow);  // Add percussion row
                   }
                 int instRow = FIRST_PERCUSSION_INSTRUMENT_ROW + instIndex;
                 getRowHeaders().setElementAt(instrumentName, instRow);
                 setValueAt(INITIAL_INSTRUMENT_VOLUME, instRow, INSTRUMENT_VOLUME_COLUMN);
                 setValueAt(POSITIVE_INCLUDE_VALUE, instRow, INSTRUMENT_INCLUDE_COLUMN);
               
                 //System.out.println("percussion: " + instrumentName);  
               }
               
               String ruleContent = rule.fifth().toString();
               // Hack to strip outer parens
               ruleContent = ruleContent.substring(0, ruleContent.length()-1).substring(1);
               
               DrumRule drumrule = new DrumRule(ruleContent, instIndex);
               
               setValueAt(drumrule, instIndex + FIRST_PERCUSSION_INSTRUMENT_ROW, lastPatternColumn);  // get rule as polylist: hack
               row++;   // Fix: For testing only
               rules = rules.rest();
             }
        }
     }
}
