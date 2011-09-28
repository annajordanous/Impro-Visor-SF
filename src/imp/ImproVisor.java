/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
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

package imp;

import java.io.*;

import imp.data.*;
import imp.com.*;
import imp.gui.*;
import imp.util.*;

/**
 * Impro-Visor main class
 *
 * @author Stephen Jones, Aaron Wolin, Robert Keller
 * @version 5
 */

public class ImproVisor implements Constants {
    
    public static final String version = "5.01";
    
    private static String lastLeadsheetFileStem = null;
    
    static private Notate currentWindow = null;

    private static int initialXopen = 0;
    private static int initialYopen = 0;

    private static String ruleFilePath;
    private static String ruleFileName;

    private static File ruleFile;
    
    private static String recentFilesFilename =  "vocab" + File.separator + "recentFiles.txt";

    private static String prefsFileName = "My.prefs";

    public static File getRuleFile()
      {
      return ruleFile;
      }
    
    private static MidiManager midiManager;
    private static MidiSynth midiSynth;
    
     
    /**
     * Indicate whether or not to play notes that are inserted, modified, etc.
     */
    private static boolean playInsertions = false;
    
    /**
     * Insertion volume.
     */
    private static int entryVolume = 85;
    
   /**
     * Indicate whether or not to show advice.
     */
    private static boolean showAdvice = false;
    
    /**
     * Static int indicating chords should be pasted
     */
    public static int CHORDS = 0; 
    
    /**
     * Static int indicating notes should be pasted
     */
    public static int NOTES = 1; 
    
    /**
     * Global clipboard for cut, copy, and paste Melody
     */
    private MelodyPart melodyClipboard;
    
    /**
     * Global clibboard for cut, copy, and paste
     */
    private ChordPart chordsClipboard;
    
    /**
     * If the clipboard is holding a selection of chords or notes
     */
    private int pasteType;
        
    /**
     * Single Advisor for now.
     */

    private static Advisor advisor;
    
    public static MidiManager getMidiManager() {
        return midiManager;
    }
    
    public static MidiSynth getLastMidiSynth() {
        return getCurrentWindow().getMidiSynth();
    }

    /**
     * Get the version string of this version
     */

    public static String getVersion()
      {
      return version;
      }

    /**
     * Get the singleton Advisor for this instance of ImproVisor.
     */

    public static Advisor getAdvisor()
      {
      return advisor;
      }

    /**
     * Get the indication of whether to play insertions.
     */

    public static boolean getPlay()
      {
      return playInsertions;
      }
    
    public static void playCurrentSelection(boolean toEnd, int loop)
      {
      playCurrentSelection(toEnd, loop, true);
      }

    /**
     * Play the current selection
     */

    public static void playCurrentSelection(boolean toEnd, int loop, boolean useDrums)
      {
      getCurrentWindow().getCurrentStave().playSelection(toEnd, loop, useDrums);
      }
    
    /**
     * Get the entry-note volume.
     */
    
    public static int getEntryVolume()
    {
        return entryVolume;
    }
    
    /**
     * Get the indication of whether to play insertions.
     */

    public static boolean getShowAdvice()
      {
      return showAdvice;
      }

    /**
     * Set the indication of whether to play insertions.
     */

    public static void setPlayEntrySounds(boolean x)
      {
      playInsertions = x;
      }
    
    /**
     * Set the entry volume.
     */
    
    public static void setEntryVolume(int x)
    {
        entryVolume = x;
    }

    /**
     * Set the indication of whether to show advice.
     */

    public static void setShowAdvice(boolean x)
      {
      showAdvice = x;
      }
    
    private static ImproVisor instance = null;
    public static ImproVisor getInstance() {
        if(instance == null) {
            instance = new ImproVisor();
        }
        return instance;
    }
    
    /** 
     * Creates a new instance of ImproVisor. Initializes the clipboard and 
     * creates a default Notation window with 64 blank, 4/4 
     * measures.
     */
    private ImproVisor() {
        this(null);
    }

/** 
 * Creates a new instance of ImproVisor. Initializes the clipboard and 
 * creates a default Notation window with 64 blank, 4/4 
 * measures.  
 * @param leadsheet to be initially loaded; if null, will open new leadsheet
 */
    
private ImproVisor(String leadsheet)
  {
    Trace.log(2, "construct ImproVisor");

//        try {
//            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
//        } catch (Exception e) {
//        }

    midiManager = new MidiManager();

    
    // Make sure to load preferences before loading the Advisor
    
    // Note that loadPreferences will generate a preference file if there is none.
    
    Preferences.loadPreferences();

    advisor = new Advisor();

    // Create global clipboards
    this.melodyClipboard = new MelodyPart();
    this.chordsClipboard = new ChordPart();

    // Load the default rule file from the Preferences file
    ruleFilePath = Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE);
    if( ruleFilePath.lastIndexOf(File.separator) == -1 )
      {
        ruleFileName = ruleFilePath;
      }
    else
      {
        ruleFileName = ruleFilePath.substring(ruleFilePath.lastIndexOf(File.separator), ruleFilePath.length());
      }

    LoadAdviceCommand loadAdvice = null;

    //Trace.log(2, "Loading: " + ruleFileDir + " :: " + ruleFileName);
 
    ruleFile = new File(getVocabDirectory(), ruleFileName);
    
    loadAdvice = new LoadAdviceCommand(ruleFile, advisor, null, true, false);

   if( loadAdvice != null )
      {
        loadAdvice.setLoadDialogText("Loading Vocabulary ...");
        loadAdvice.execute();

        synchronized(loadAdvice)
          {
            while( !loadAdvice.hasLoaded() )
              {
                try
                  {
                    loadAdvice.wait();
                  }
                catch( InterruptedException e )
                  {
                  }
              }
          }

      }
    
    // FIrst open a blank Notate window

    // Create a score with default measures in default meter
    Score score = new Score(Notate.defaultBarsPerPart * (BEAT * Notate.defaultMetre));

    String fontSizePref = Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE);

    score.setChordFontSize(Integer.valueOf(fontSizePref).intValue());

    // Create notate frame.
    
    Notate notate = new Notate(score, advisor, this, initialXopen, initialYopen);
    
    notate.setNotateFrameHeight();
    
    
    boolean createRoadMap = Preferences.getPreference(Preferences.CREATE_ROADMAP).equals("y");
    
    notate.setRoadMapCheckBox(createRoadMap);
    
    // Close the splash window.

    if( loadAdvice != null )
      {
        loadAdvice.hideLoadDialog();
      }    
    
    
    // Load most recent file, if there is one.

    RecentFiles recFiles = new RecentFiles();
    String pathName = recFiles.getFirstPathName();
    if( pathName != null )
      {
        File f = new File(pathName);
        
        if( f.exists() )
          {
            notate.setupLeadsheet(f, false);
          }
      }

     notate.makeVisible(createRoadMap);

     currentWindow = notate;


//        Stuff from Julia Botev that was never integrated.
//        ComplexityFrame attributeFrame = new ComplexityFrame();
//        attributeFrame.setVisible(true);
//

    if( Trace.atLevel(3) )
      {
        advisor.listChords(System.out);	// option to list all chord types
      }
  }
       
    
 static public void windowHasFocus(Notate window)
  {
    currentWindow = window;
  }

static public Notate getCurrentWindow()
  {
    return currentWindow;
  }

/**
 * Returns the melody clipboard.
 * @return MelodyPart             the melody clipboard
 */
public MelodyPart getMelodyClipboard()
  {
    return melodyClipboard;
  }

/**
 * Returns the chord clipboard.
 * @return ChordPart             the clipboard
 */
public ChordPart getChordsClipboard()
  {
    return chordsClipboard;
  }

/**
 * Indicates whether the melody clipboard is non-empty
 * @return indication of whether the melody clipboard is non-empty
 */
public boolean melodyClipboardNonEmpty()
  {
    return melodyClipboard.size() > 0;
  }

/**
 * Indicates whether the chord clipboard is non-empty
 * @return indication of whether the chord clipboard is non-empty
 */
public boolean chordsClipboardNonEmpty()
  {
    return chordsClipboard.size() > 0;
  }

/**
 * Sets the pasting type to be for chords or notes
 * @param type              the type of paste
 */
public void setPasteType(int type)
  {
    this.pasteType = type;
  }

/**
 * Gets the pasting type
 * @return int              the type of paste
 */
public int getPasteType()
  {
    return pasteType;
  }

    
/**
 * Main Impro-Visor program. Creates an ImproVisor instance, which will 
 * initialize the array of Notate frames.
 */
public static void main(String[] args)
  {
    String leadsheet = null;
    if( args.length > 0 )
      {
        //System.out.println("sees argument");
        leadsheet = args[0];
      }

    // preload images
    ToolkitImages.getInstance();

    // Establish user directory and copy vocab files.

    getUserDirectory();

    // create ImproVisor instance... this seems to violate some principle
    // of instances, we set instance directly instead of call getInstance()
    // but it does allow us to pass in a leadsheet parameter...
    instance = new ImproVisor(leadsheet);
  }


/**
 * Get the directory where user Impro-Visor files are stored.
 * If this directory does not exist, then it is created and populated with
 * directories and files from the master installation.
 * @return 
 */

public static File getUserDirectory()
  {
  String userHome = System.getProperty("user.home");
  //System.out.println("User Home Path: "+ userHome);

  
  File homeDir = new File(userHome, Directories.improHome);
    
  if( !homeDir.exists() )
    {
      establishUserDirectory(homeDir);
     }
  
  return homeDir;
  }


/**
 * Establish Impro-Visor home directory in user's space.
 * This should be done only once per installation.
 * @param homeDir 
 */
public static void establishUserDirectory(File homeDir)
  {
    System.out.println("Creating new folder for impro-visor files: " + Directories.improHome);

    homeDir.mkdir();

    copyDir(Directories.vocabDirName,        homeDir);
    copyDir(Directories.leadsheetDirName,    homeDir);
    copyDir(Directories.grammarDirName,      homeDir);
    copyDir(Directories.styleDirName,        homeDir);
    copyDir(Directories.styleExtractDirName, homeDir);
  }

/**
 * Copy master sub-directory into users home directory.
 * @param homeDir
 * @param name 
 */

public static void copyDir(String subDirName, File homeDir)
  {
    File masterDir = new File(subDirName);

    File userDir = new File(homeDir, subDirName);

    if( masterDir != null )
      {
        try
          {
            FileUtilities.copyDirectory(masterDir, userDir);
          }
        catch( IOException e )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Error in copying folder " 
                           + subDirName + " to user directory.");
          }
      }
  }


public static File getVocabDirectory()
  {
  return new File(getUserDirectory(), Directories.vocabDirName);
  }

public static File getLeadsheetDirectory()
  {
  return new File(getUserDirectory(), Directories.leadsheetDirName);
  }

public static File getStyleDirectory()
  {
   return new File(getUserDirectory(), Directories.styleDirName);
  }

public static File getStyleExtractDirectory()
  {
  return new File(getUserDirectory(), Directories.styleExtractDirName);
  }

public static File getGrammarDirectory()
  {
  return new File(getUserDirectory(), Directories.grammarDirName);
  }

public static File getDictionaryDirectory()
  {
  return new File(getUserDirectory(), Directories.dictionaryDirName);
  }

public static File getProfileDirectory()
  {
  return new File(getUserDirectory(), Directories.profileDirName);
  }

public static File getPrefsFile()
  {
  return new File(getVocabDirectory(), prefsFileName); 
  }
    
public static File getGrammarFile()
  {
  return new File(getGrammarDirectory(), "My.grammar"); 
  }
     
public static File getRecentFilesFile()
  {
    String filename = getUserDirectory() + File.separator + recentFilesFilename;
    File file = new File(filename);
    try
      {
        file.createNewFile();
        //System.out.println("recentFiles in " + filename);
        return file;
      }
    catch( IOException e )
      {
        ErrorLog.log(ErrorLog.WARNING, "Cannot create recentFiles file: " + filename);
        return null;
      }
  }

public static void setLastLeadsheetFileStem(String stem)
  {
    if( stem != null )
      {
      lastLeadsheetFileStem = stem;
      }
  }

public static String getLastLeadsheetFileStem()
  {
    return lastLeadsheetFileStem;
  }
}
