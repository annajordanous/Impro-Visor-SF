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

package imp.util;

import imp.ImproVisor;
import imp.com.Command;
import imp.com.CommandManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import javax.swing.JCheckBox;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.Tokenizer;

/**
 * @author David Morrison, Robert Keller
 */

public class Preferences implements imp.Constants
  {
  public static final String YES = "y";
  
  public static final String NO = "n";
  
  public static final char TRUE_CHECK_BOX = 'y';

  public static final char FALSE_CHECK_BOX = 'n';
  
  public static final int ALWAYS_USE_BASS = 0;

  public static final int ALWAYS_USE_CHORD = 1;

  public static final int ALWAYS_USE_MELODY = 2;

  public static final int ALWAYS_USE_STAVE = 3;

  private static PrintStream outputStream;

  private static FileInputStream inputStream;

  protected static CommandManager cm = new CommandManager();
  
  //public static String vocabDir = "vocab";

  protected static Polylist prefs = Polylist.nil;

  public static final String ADV_CACHE_SIZE = "advice-cache-size";

  public static final String ACS_VAL = "10";

  public static final String ADV_CACHE_ENABLED = "cache-enabled";

  public static final String ACE_VAL = YES;

  public static final String DEFAULT_LOAD_STAVE = "default-load-stave";

  public static final String DLS_VAL = "1";
  
  public static final int DEFAULT_STAVE_TYPE = 1;

  public static final String DEFAULT_MELODY_INSTRUMENT =
          "default-melody-instrument";

  public static final String DMI_VAL = "12";

  public static final String DEFAULT_CHORD_INSTRUMENT =
          "default-chord-instrument";

  public static final String DCI_VAL = "1";

  public static final String DEFAULT_BASS_INSTRUMENT =
          "default-bass-instrument";

  public static final String DBI_VAL = "34";

  public static final String DEFAULT_MIXER_ALL = "default-mixer-all";

  public static final String DMA_VAL = "127";

  public static final String DEFAULT_MIXER_ENTRY = "default-mixer-entry";

  public static final String DME_VAL = "50";

  public static final String DEFAULT_MIXER_BASS = "default-mixer-bass";

  public static final String DMB_VAL = "60";

  public static final String DEFAULT_MIXER_CHORDS = "default-mixer-chords";

  public static final String DMC_VAL = "40";

  public static final String DEFAULT_MIXER_DRUMS = "default-mixer-drums";

  public static final String DMD_VAL = "60";

  public static final String DEFAULT_MIXER_MELODY = "default-mixer-melody";

  public static final String DMM_VAL = "127";

  public static final String DEFAULT_STYLE = "default-style";

  public static final String DS_VAL = "swing";

  public static final String DEFAULT_TEMPO = "default-tempo";

  public static final String DT_VAL = "160";

  public static final String DEFAULT_VOCAB_FILE = "default-vocab-file";

  public static final String DVF_VAL = "My.voc"; 

  public static final String DEFAULT_GRAMMAR_FILE = "default-grammar-file";

  public static final String DVF_GRAMMAR_VAL = "My.grammar";

  public static final String DEFAULT_STYLE_DIRECTORY = "default-style-directory";

  public static final String DSD_VAL = "styles";

  public static final String VIS_ADV_COMPONENTS = "visible-advice-components";

  public static final String VAC_VAL = "1023";

  public static final String CHORD_DIST_ABOVE_ROOT = "chord-dist-above-root";

  public static final String CDAR_VAL = "10";

  public static final String MAX_NOTES_IN_VOICING = "max-notes-in-voicing";

  public static final String MNIV_VAL = "5";

  public static final String NOTE_COLORING = "note-coloring";

  public static final String NC_VAL = "1342";

  public static final String SHOW_TRACKING_LINE = "show-tracking-line";

  public static final String STL_VAL = YES;

  public static final String TRACKER_DELAY = "tracker-delay";

  public static final String TD_VAL = "0";

  public static final String DRAWING_TONES = "contour-drawing-tones";

  public static final String DRAWING_TONES_VAL = "1xx";

  public static final String DEFAULT_DRAWING_MUTED = "default-drawing-muted";

  public static final String DDM_VAL = "true";

  public static final String ALWAYS_USE_BUTTONS = "always-use-buttons";
  
  public static final String CREATE_ROADMAP = "create-roadmap";
  
  public static final String CR_VAL = YES;
  
  public static final String TREBLE_STRING = "1";
  public static final String BASS_STRING   = "2";
  public static final String GRAND_STRING  = "3";
  public static final String AUTO_STRING   = "4";

  public static final String DEFAULT_CHORD_FONT_SIZE = "default-chord-font-size";

  public static final String DEFAULT_CHORD_FONT_SIZE_VALUE = "16";
  
  public static final String DEFAULT_STAVES_PER_PAGE = "8";
  
  public static final String MIDI_IN = "midi-in";
  
  public static final String MIDI_OUT = "midi-out";

  public static final String MIDI_ECHO = "midi-echo";

  public static final String MIDI_SEND_BANK_SELECT = "midi-send-bank-select";

  public static final String MIDI_IN_LATENCY = "midi-in-latency";
  
  public static final String RECENT_STYLE_FILE = "recent-style-file";

  public static final String DEFAULT_MIDI_IN = "RealTimeSequencer";
  
  public static final String DEFAULT_MIDI_OUT = "JavaSoundSynthesizer";

  public static final String DEFAULT_MIDI_ECHO = YES;

  public static final String DEFAULT_MIDI_IN_LATENCY = "0";
  
  public static final String DEFAULT_MIDI_SEND_BANK_SELECT = NO;
  
  public static final String DEFAULT_RECENT_STYLE_FILE = "swing.sty";
  /**
   * The ALWAYS_USE_BUTTONS are y or n standing for CHORD, BASS, DRUMS, STAVE.
   */
  public static final String DEFAULT_ALWAYS_USE_BUTTONS = "nnnn";


  public static String booleanToYesNo(boolean value)
    {
      return value ? YES : NO;
    }
  
  public static boolean yesNoToBoolean(String value)
    {
      return value.equals(YES);
    }
  
  public static void loadPreferences()
    {
    Polylist test = new Polylist();
    File prefsFile = ImproVisor.getPrefsFile();
    try
      {
      inputStream = new FileInputStream(prefsFile);
      cm.execute(new LoadPrefsCommand(inputStream));
      }
    catch( Exception e )
      {
      /*
      ErrorLog.log(ErrorLog.WARNING, "Cannot open preferences file; " +
              "generating default preference file 'vocab/My.prefs'.");

      */
      makeDefaultPrefsFile();

      try
        {
        inputStream = new FileInputStream(prefsFile);
        cm.execute(new LoadPrefsCommand(inputStream));

        }
      catch( Exception j )
        {
        ErrorLog.log(ErrorLog.WARNING, "Failure generating default " +
                "preference file 'My.prefs'.");
        }
      }
    }

  public static void savePreferences()
    {
    File file = ImproVisor.getPrefsFile();

    //System.out.println("saving preferences");
    try
      {
      file.createNewFile();
      outputStream = new PrintStream(new FileOutputStream(file));
      cm.execute(new SavePrefsCommand(outputStream));
      }
    catch( Exception e )
      {
      ErrorLog.log(ErrorLog.WARNING, "Cannot open or create preferences file: " + file);
      }
    }

  /*
   * Search through the preferences Polylist to find a particular value to set.
   * Note that preferences in the file are in the following form:
   *    (preference-name (preference-value))
   */
  
  public static void setPreference(String pref, String value)
    {
    //System.out.println("setting preference for " + pref + " to " + value);
    Polylist search = prefs;

    // While the search list isn't empty...
    while( search.nonEmpty() )
      {
      // Look at the next pref, make sure it's a string.
      Polylist nextPref = (Polylist)search.first();
      if( !(nextPref.first() instanceof String) )
        {
        ErrorLog.log(ErrorLog.SEVERE, "Malformed Preferences File.");
        }
      // If it is, see if it's the string we're looking for.
      else
        {
        if( pref.equals((String)nextPref.first()) )
          {
          // If so, we can set the new value of the preference.
          nextPref = nextPref.rest();
          nextPref.setFirst(value);

          break;
          }
        }

      search = search.rest();
      }

    if( search.isEmpty() )
      {
      // Add new preference that was not there

      Polylist newPref = Polylist.list(pref, value);

      // System.out.println("adding preference" + newPref);
      prefs = prefs.cons(newPref);
      }

    // Now we need to save the new Polylist
    savePreferences();
    }

  public static String getPreference(String pref)
    {
    Polylist search = prefs;

    //System.out.println("\ngetPreference for " + pref);

    // While the search list isn't empty...
    while( search.nonEmpty() )
      {
      // Look at the next pref, make sure it's a string.
      Polylist nextPref = (Polylist)search.first();
      //System.out.println("nextPref = " + nextPref);
      if( !(nextPref.first() instanceof String) )
        {
        ErrorLog.log(ErrorLog.SEVERE, "Malformed Preferences File.");
        }
      // If it is, see if it's the string we're looking for, then return the value.
      else if( pref.equals((String)nextPref.first()) )
        {
        String value = nextPref.second().toString();

        //System.out.println("getting preference for " + pref + " as " + value);
        
        return value;
        }

      search = search.rest();
      }

    ErrorLog.log(ErrorLog.WARNING, "Preference " + pref + " does not exist");
    return "";
    }
  
public static StaveType getStavePreference(String staveString, boolean useDefault)
  {
  if( useDefault || getAlwaysUseStave() )
    {
    staveString = getPreference(DEFAULT_LOAD_STAVE);
    }
  if( staveString.equals(TREBLE_STRING) )
    {
    return StaveType.TREBLE;
    }
 if( staveString.equals(BASS_STRING) )
    {
    return StaveType.BASS;
    }
  if( staveString.equals(GRAND_STRING)  )
    {
    return StaveType.GRAND;
    }
  return StaveType.AUTO;
  }

 public static StaveType getStaveTypeFromPreferences()
  {
  return StaveType.values()[
          defaultingIntFromString(DEFAULT_STAVE_TYPE, 
                                  getPreference(DEFAULT_LOAD_STAVE))];
  }
 
public static String getMidiIn()
   {
     return getPreference(MIDI_IN);
   }
  
public static String getMidiOut()
   {
     return getPreference(MIDI_OUT);
   }


public static boolean getMidiEcho()
   {
     return getPreference(MIDI_ECHO).equals(YES);
   }

public static void setMidiEcho(boolean value)
  {
    setPreference(MIDI_ECHO, value ? YES : NO);
  }
  
public static boolean getMidiSendBankSelect()
  {
    return getPreference(MIDI_SEND_BANK_SELECT).equals(YES);
  }

public static void setMidiSendBankSelect(boolean value)
  {
    setPreference(MIDI_SEND_BANK_SELECT, value ? YES : NO);
  }

public static double getMidiInLatency()
   {
     return Double.parseDouble(getPreference(MIDI_IN_LATENCY));
   }

public static void setMidiInLatency(double value)
  {
    setPreference(MIDI_IN_LATENCY, "" + value);
  }
  

public static boolean getAlwaysUse(int index)
 {
 String alwaysUseButtons = getPreference(ALWAYS_USE_BUTTONS);
 if( index >= alwaysUseButtons.length() )
   {
   return false;
   }
 return alwaysUseButtons.charAt(index) == TRUE_CHECK_BOX;
 }

 public static boolean getAlwaysUseStave()
 {
 return getAlwaysUse(ALWAYS_USE_STAVE);
 }

  /**
   * Set the indexed preference with a value from the corresponding CheckBox.
  @param index
  @param checkbox
   */
 
  public static void setCheckBoxPreferences(int index, JCheckBox checkbox)
    {
    char boxStates[] =
            getPreference(ALWAYS_USE_BUTTONS).toCharArray();      
    boxStates[index] = checkbox.isSelected() ? TRUE_CHECK_BOX : FALSE_CHECK_BOX;
    
    setPreference(ALWAYS_USE_BUTTONS, new String(boxStates));
    }

  public static void makeDefaultPrefsFile()
    {
    File file = ImproVisor.getPrefsFile();
    try
      {
      FileOutputStream newFile;

      newFile = new FileOutputStream(file); //"vocab" + File.separator + "My.prefs");
      PrintStream out = new PrintStream(newFile);
      out.println("(" + ADV_CACHE_SIZE + " " + ACS_VAL + ")");

      out.println("(" + ADV_CACHE_ENABLED + " " + ACE_VAL + ")");
      out.println("(" + DEFAULT_LOAD_STAVE + " " + DLS_VAL + ")");
      out.println("(" + DEFAULT_MELODY_INSTRUMENT + " " + DMI_VAL + ")");
      out.println("(" + DEFAULT_CHORD_INSTRUMENT + " " + DCI_VAL + ")");
      out.println("(" + DEFAULT_BASS_INSTRUMENT + " " + DBI_VAL + ")");

      out.println("(" + DEFAULT_MIXER_ALL + " " + DMA_VAL + ")");
      out.println("(" + DEFAULT_MIXER_ENTRY + " " + DME_VAL + ")");
      out.println("(" + DEFAULT_MIXER_BASS + " " + DMB_VAL + ")");
      out.println("(" + DEFAULT_MIXER_CHORDS + " " + DMC_VAL + ")");
      out.println("(" + DEFAULT_MIXER_DRUMS + " " + DMD_VAL + ")");
      out.println("(" + DEFAULT_MIXER_MELODY + " " + DMM_VAL + ")");

      out.println("(" + DEFAULT_STYLE + " " + DS_VAL + ")");
      out.println("(" + DEFAULT_TEMPO + " " + DT_VAL + ")");
      out.println("(" + DEFAULT_VOCAB_FILE + " " + DVF_VAL + ")");
      out.println("(" + DEFAULT_GRAMMAR_FILE + " " + DVF_GRAMMAR_VAL + ")");
      out.println("(" + DEFAULT_STYLE_DIRECTORY + " " + DSD_VAL + ")");
      out.println("(" + VIS_ADV_COMPONENTS + " " + VAC_VAL + ")");
      out.println("(" + CHORD_DIST_ABOVE_ROOT + " " + CDAR_VAL + ")");
      out.println("(" + DEFAULT_CHORD_FONT_SIZE + " " + DEFAULT_CHORD_FONT_SIZE_VALUE + ")");
      out.println("(" + MAX_NOTES_IN_VOICING + " " + MNIV_VAL + ")");
      out.println("(" + NOTE_COLORING + " " + NC_VAL + ")");
      out.println("(" + SHOW_TRACKING_LINE + " " + STL_VAL + ")");
      out.println("(" + TRACKER_DELAY + " " + TD_VAL + ")");
      out.println("(" + DRAWING_TONES + " " + DRAWING_TONES_VAL + ")");
      out.println("(" + DEFAULT_DRAWING_MUTED + " " + DDM_VAL + ")");
      out.println("(" + ALWAYS_USE_BUTTONS + " " + DEFAULT_ALWAYS_USE_BUTTONS + ")");
      out.println("(" + CREATE_ROADMAP + " " + CR_VAL + ")");      
      out.println("(" + MIDI_IN + " " + DEFAULT_MIDI_IN + ")");      
      out.println("(" + MIDI_OUT + " " + DEFAULT_MIDI_OUT + ")");      
      out.println("(" + MIDI_ECHO + " " + DEFAULT_MIDI_ECHO + ")"); 
      out.println("(" + MIDI_SEND_BANK_SELECT + " " + DEFAULT_MIDI_SEND_BANK_SELECT + ")"); 
      out.println("(" + RECENT_STYLE_FILE + DEFAULT_RECENT_STYLE_FILE + ")");
      }
    catch( Exception e )
      {
      ErrorLog.log(ErrorLog.WARNING, "*** Error: Could not generate preferences file.");
      }
    }

  /**
 * Parses into from string, defaulting to specified value if parsing fails
 * @param string 
 */
  
public static int defaultingIntFromString(int defaultInt, String string)
  {
     try
      {
        return Integer.parseInt(string);
      }
    catch( NumberFormatException e )
      {
      return defaultInt;
      }
  }

public static boolean getShowTrackingLine()
  {
    return yesNoToBoolean(getPreference(Preferences.SHOW_TRACKING_LINE));
  }

public static void setShowTrackingLine(boolean value)
  {
    setPreference(SHOW_TRACKING_LINE, booleanToYesNo(value));
  }
  
  
  
  public static class SavePrefsCommand
          implements Command
    {
    /**
     * the File to save to
     */
    private PrintStream file;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    /**
     * Creates a new Command that can save a Score to a File.
     * @param file      the File to save to
     * @param score     the Score to save
     */
    public SavePrefsCommand(PrintStream file)
      {
      this.file = file;
      }

    /**
     * Saves the Preferences to the File.
     */
    public void execute()
      {
      // FIX: This is executed multiple time for what should be one save. Why??

      // System.out.println("saving prefs = " + prefs);
      Polylist out = prefs;
      while( out.nonEmpty() )
        {
        file.println(out.first());
        out = out.rest();
        }
      }

    /**
     * Undo unsupported for SaveLeadsheetCommand.
     */
    public void undo()
      {
      throw new UnsupportedOperationException("Undo unsupported for SaveLeadsheet.");
      }

    /**
     * Redo unsupported for SaveLeadsheetCommand.
     */
    public void redo()
      {
      throw new UnsupportedOperationException("Redo unsupported for SaveLeadsheet.");
      }

    public boolean isUndoable()
      {
      return undoable;
      }

    }

  public static class LoadPrefsCommand
          implements Command
    {
    /**
     * the File to save to
     */
    private FileInputStream file;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;

    public LoadPrefsCommand(FileInputStream file)
      {
      this.file = file;
      }

    /**
     * Saves the Preferences to the File.
     */
    public void execute()
      {
      Tokenizer in = new Tokenizer(file);
      Object ob;

      Object prevOb = null;
      while( (ob = in.nextSexp()) != Tokenizer.eof )
        {
        if( ob.equals(prevOb) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "Two consecutive preferences are identical: " + ob);
          }
        if( ob instanceof Polylist )
          {
          prefs = prefs.cons((Polylist)ob);
          }	// FIX: Need more form checking here.
        prevOb = ob;
        }

      prefs = prefs.reverse();
      }

    /**
     * Undo unsupported for SaveLeadsheetCommand.
     */
    public void undo()
      {
      throw new UnsupportedOperationException("Undo unsupported for SaveLeadsheet.");
      }

    /**
     * Redo unsupported for SaveLeadsheetCommand.
     */
    public void redo()
      {
      throw new UnsupportedOperationException("Redo unsupported for SaveLeadsheet.");
      }

    public boolean isUndoable()
      {
      return undoable;
      }
  }
}
  
  

