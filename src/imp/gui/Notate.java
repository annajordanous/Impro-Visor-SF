/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2010 Robert Keller and Harvey Mudd College
 * XML export code is also Copyright (C) 2009-2010 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modifyc
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

import java.awt.*;
import java.awt.event.*;
import javax.sound.midi.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;

import imp.Constants;
import imp.Constants.ExtractMode;
import imp.Constants.StaveType;
import imp.Directories;
import imp.ImproVisor;
import imp.data.*;
import imp.data.musicXML.ChordDescription;
import imp.cluster.*;
import imp.com.*;
import imp.util.*;
import imp.lickgen.*;
import imp.util.MidiManager;
import imp.util.LeadsheetFileView;
import imp.util.LeadsheetPreview;
import imp.util.MusicXMLFilter;

import polya.*;

/**
 *
 * Notate creates a Java JFrame that allows the user to interact with
 * Impro-Visor through a GUI interface, while also displaying the notation of a
 * selected or created part on the screen.
 *
 * The toolbar is using "Java look and feel Graphics Repository" icons,
 *
 * http://java.sun.com/developer/techDocs/hi/repository/
 *
 * <p>
 *
 * The JFrame and other components of Notate have been created with NetBeans
 *
 *
 * @author      Aaron Wolin, Bob Keller
 *
 * Music XMLaspects contributed by Lasconic (Nicolas Froment) Aug. 15, 2009.
 *
 */

public class Notate
        extends javax.swing.JFrame
        implements Constants, MidiPlayListener
  {

  LickgenFrame lickgenFrame;

  static int LEADSHEET_EDITOR_ROWS = 1000;

  static int GRAMMAR_EDITOR_ROWS = 10000;
  
  AboutDialog aboutDialog = new AboutDialog(this, false);

  ErrorDialog errorDialog = ErrorLog.getDialog();
  
  HelpDialog helpDialog = new HelpDialog(this, false);
  
  int ADVICE_SCROLL_LIST_ITEMS_VISIBLE = 10;

  /**
   * trackerDelay delays the tracker by offsetting a specified number of seconds.
   * This is for compatibility with midi delays introduced by different operating
   * systems. If trackerDelay is positive, the playback will have to have gone by
   * the corresponding number of slots before the tracker starts moving. If it is negative, 
   * the tracker will start with the corresponding number of slots from the time playback is triggered.
   */
  double trackerDelay = 0;

  public static final int defaultBarsPerPart = 72;

  public static final int defaultMetre = 4;

  public static final Dimension preferencesDialogDimension =
          new Dimension(775, 625);

  public static final Dimension leadsheetEditorDimension = new Dimension(500,
          600);

  private boolean noteColoration = true;

  private boolean smartEntry = true;

  private int parallax = 0;

  public static final char TRUE_CHECK_BOX = 'y';

  public static final char FALSE_CHECK_BOX = 'n';
  
  private static final int ALWAYS_USE_BASS_INDEX = 0;
  private static final int ALWAYS_USE_CHORD_INDEX = 1;
  private static final int ALWAYS_USE_MELODY_INDEX = 2;
  private static final int ALWAYS_USE_STAVE_INDEX = 3;
  
  private static int MIN_TEMPO = 30;
  private static int MAX_TEMPO = 300;

  private static int aboutDialogWidth  = 600;
  private static int aboutDialogHeight = 750;


   private static int million = 1000000;

   private int loopsRemaining;

  /**
   *
   * Tell whether advice is initially open or not.
   *
   */
  public static boolean adviceInitiallyOpen = false;

  /**
   *
   * Used as a prefix on window titles
   *
   */
  public static String windowTitlePrefix = "Impro-Visor";

  public static String windowTitlePrefixSeparator = ": ";


  
  /**
   *
   * Used as a prefix on leadsheet titles
   *
   */
  public static String leadsheetTitlePrefix = "Leadsheet: ";

  /**
   *
   * The current working path, same as the application normally.
   *
   */
  String basePath;

  /**
   *
   * Used to establish basePath, by creating a dummy file, then getting its canonical path.
   *
   */
  File baseDir = new File("dummy");

  /**
   *
   * Sub-directory for vocabulary
   *
   */
  File vocabDir; // set within constructor

  /**
   *
   * Standard file for vocabulary
   *
   */
  public String vocFile = "My.voc";

 /**
  *
  * file for musicXML chord description
  *
  */
  public String musicxmlFile = "chord_musicxml.xml";


  /**
   *
   * Sub-directory for grammars
   *
   */
  File grammarDir; // set within constructor

  /**
   *
   * Standard file for vocabulary
   *
   */

  public String grammarFile = "vocab" + File.separator + "My.grammar"; // original

  // attempted new public String grammarFile = Directories.grammarDirName + File.separator + "My.grammar";

   /**
   *
   * Standard file for leadsheet
   *
   */
  public String lsFile = "untitled";

  /**
   *
   * Counter for untitled leadsheets
   *
   */
  private int lsCount = 1;

  /**
   *
   * Standard sub-directory for leadsheets and midi
   *
   */
  String leadsheetDirName = "leadsheets";

  File leadsheetDir;

  String midiDir = "leadsheets" + File.separator + "midi";

  /**
   *
   * Standard extension vocabulary
   *
   */
  public String vocabExt = ".voc";

  /**
   *
   * Standard extension for leadsheets
   *
   */
  public String leadsheetExt = ".ls";

  /**
   *
   * Midi extension
   *
   */
  public String midiExt = ".mid";

 /**
  *
  * MusicXML extension
  *
  */
  public String musicxmlExt = ".xml";


  /**
   *
   * Default Filenames
   *
   */
  public String lsDef = "untitled.ls";

  public String midDef = "untitled.mid";

  public String musicxmlDef = "untitled.xml";


  /**
   *
   * The maximum number of measures per line.  I'm not sure quite why, but things
   *
   * break if this is exceeded.
   *
   */
  public static final int maxMeasuresPerLine = 15;

  /**
   *
   * The default stave type.
   *
   */
  public StaveType DEFAULT_STAVE_TYPE = Preferences.getStaveTypeFromPreferences();

  protected Polylist adviceList;

  ArrayList<Object> adviceMenuItemsScales;

  ArrayList<Object> adviceMenuItemsCells;

  ArrayList<Object> adviceMenuItemsIdioms;

  ArrayList<Object> adviceMenuItemsLicks;

  ArrayList<Object> adviceMenuItemsQuotes;

  /**
   *
   * The array of JScrollPanes that hold scoreBG panels, which hold Staves.
   *
   */
  protected StaveScrollPane[] staveScrollPane;

  /**
   *
   * beatValue is represents how many slots a beat takes up.  To account for different
   *
   * time signatures, it is scaled to (BEAT*4)/(timeSignatureBottom).  (Thus, for 4/4,
   *
   * beatValue is simply BEAT, and for 6/8, beatValue is BEAT/2.
   *
   */
  private int beatValue = BEAT;

  /**
   *
   * measureLength is the number of slots a measure takes up.  It's basically timeSignatureTop * beatValue.
   *
   * Thus, for 4/4, it's 4 * BEAT.
   *
   */
  private int measureLength = 4 * BEAT;

  /**
   *
   * Default starting score length (in measures)
   *
   */
  private int scoreLength = defaultBarsPerPart;

  /**
   *
   * The Score to be displayed
   *
   */
  protected Score score;

  /**
   *
   * An array of Parts in the Score to be displayed
   *
   */
  protected PartList partList;

  /**
   *
   * next part to be pasted during recurrent loops
   *
   */
  protected MelodyPart nextPart;

  /**
   *
   * The chord progression of the Score
   *
   */
  protected ChordPart chordProg;

  /**
   *
   * The main Impro-Visor class. Needs to be passed for the clipboard
   *
   * variables.
   *
   */
  protected ImproVisor impro;

  /**
   *
   * Command manager for the Notate frame
   *
   */
  public CommandManager cm;

  /**
   *
   * Advisor for improvising
   *
   */
  private Advisor adv;

  /**
   *
   * Chord to insert from popup
   */

  private String chordToInsert = null;

 /**
   *
   * Lick Generator
   *
   */
  private LickGen lickgen;
  
  private Vector<String> headData;
  
  private ArrayList<String> melodyData = new ArrayList<String>();


  private static LogDialog logDialog = new LogDialog(false);
  
  /**
   * this will be set to true during extraction of all measures in a corpus
   */

  private boolean allMeasures = false;

  /**
   *
   * Default values pertinent to lick generation
   *
   */
  
  private double roundTo = BEAT;
  
  private int paddingSlots = BEAT/2;
 
  private int minPitch = 60;

  private int maxPitch = 82;

  private int minInterval = 0;

  private int maxInterval = 6;

  private int minDuration = 8;

  private int maxDuration = 8;

  private double totalBeats = 8;

  private int totalSlots = (int)(BEAT*totalBeats);

  private double restProb = 0.1;

  private double leapProb = 0.2;

  private double chordToneWeight = 0.7;

  private double scaleToneWeight = 0.1;

  private double colorToneWeight = 0.05;

  private double chordToneDecayRate = 0.1;

  private boolean avoidRepeats = true;

  private boolean useGrammar = true;

  private boolean autoFill = true;

  private int ignoreDuplicateLick;

  private boolean cancelTruncation = true;

  private ExtractMode saveSelectionMode = ExtractMode.LICK;

  private int themeLength = 8;

  private double themeProb = 0.4;

  private double transposeProb = 0.5;

  private double invertProb = 0.1;

  private double reverseProb = 0.1;

  private boolean toLoop = false;

  private int loopCount = 1;
  
  private int stopPlaybackAtSlot = 16*BEAT; // in case StyleEditor used first

  private static int QUANTUM = BEAT/2;

  public void setPlaybackStop(int slot)
  {
   stopPlaybackAtSlot = slot;
  }

  /**
   *
   * The file chooser for opening and saving leadsheets.
   *
   */
  private JFileChooser openLSFC;

  private JFileChooser saveLSFC;

  private FileDialog saveAWT;

  private JFileChooser revertLSFC;

  private LeadsheetPreview lsOpenPreview;

  private LeadsheetPreview lsSavePreview;

  /**
   *
   * The file chooser for opening and saving vocabulary
   *
   */
  private JFileChooser vocfc;

  /**
   *
   * The file chooser for opening and saving midi files
   *
   */
  private JFileChooser midfc;

 /**
  *
  * The file chooser for opening and saving musicXML files
  *
  */
  private JFileChooser musicxmlfc;


  /**
   *
   * The file chooser for opening and saving the grammar
   *
   */
  private JFileChooser grammarfc;

  /**
   *
   * The width of the main frame
   *
   */
  private int fWidth = 1100;

  /**
   *
   * The height of the main frame
   *
   */
  private int fHeight = 700;

  /**
   *
   * The width of the Score's internal frame
   *
   */
  private int sWidth = 800;

  /**
   *
   * The height of the Score's internal frame
   *
   */
  private int sHeight = 500;

  /**
   *
   * Index of the current tab
   *
   */
  private int currTabIndex;

  /**
   *
   * The locked number of measures on each line
   *
   */
  protected int[] lockedMeasures = null;

  /**
   *
   * If the stave layout is locked or not
   *
   */
  private boolean autoAdjustStaves = false;

  /**
   *
   * If pasting should always overwrite notes
   *
   */
  private boolean alwaysPasteOver = true;

  /**
   *
   * Two intermediate points for many-point line drawing; they'll leap-frog one
   *
   * another as we draw the contour 'curve'
   *
   */
  private Point curvePta,  curvePtb;

  /**
   *
   * Flag for whether the entry sound was muted before drawing mode.
   *
   * Remembered between mode toggles.
   *
   */
  private boolean preDrawingEntryMuted = false;

  /**
   *
   * Flag for entry sound muting during drawing.
   *
   * Initially based on default preferences.
   *
   */
  private boolean drawingEntryMuted =
          (Preferences.getPreference(Preferences.DEFAULT_DRAWING_MUTED).equals("true"));

  /**
   *
   * Flag:
   *
   * I couldn't find a way to determine whether a changeEvent originated from
   *
   * a user event, or a setValue() call, so the 'hack' is simply to set a flag
   *
   * before calling setValue that certain listeners will check before running.
   *
   * This value should be set to true right before calling a setValue function
   *
   * and then immediately set back to false.  Since events dispatched on the
   *
   * setValue call are not threaded, this is garunteed to work and not
   *
   * interfere, even if this boolean is used for multiple jSliders.  The
   *
   * possible exception is if one changeEvent listener fires another
   *
   * changeEvent listener, but this probably won't happen anyway.
   *
   */
  private boolean jSliderIgnoreStateChangedEvt = false;

  /**
   *
   * Flag for if the user has just pasted. Needed to display blue/green
   *
   * construction lines
   *
   */
  protected boolean justPasted = false;

  /**
   *
   * The file of the leadsheet if it is saved.
   *
   * Null if it is not saved.
   *
   */
  private File savedLeadsheet;

  /**
   *
   * The file of the advice if it is saved.
   *
   * Null if it is not saved.
   *
   */
  private File savedVocab;

  private File savedMidi;

  private File savedMusicXML;

  private String lickTitle = "unnamed";

  /**
   *
   * Midi Preferences reference to the midiManager and JComboBox models
   *
   */
  private MidiSynth midiSynth = null; // one midiSynth is created for each Notote instance for volume control and MIDI sequencing

  private MidiManager midiManager = null; // reference to global midiManager contained in ImproVisor

  private MidiDeviceChooser midiIn,  midiOut; // combo box models for device choosing in the midi preferences

  private MidiNoteActionHandler midiRecorder = null; // action handler for recording from midi

  private MidiStepEntryActionHandler midiStepInput = null; // action handler for step input from midi

  private boolean stepInputActive = false;

  /**
   *
   * Stores the index of stave tab where the playback indicator is currently
   *
   * located
   *
   */
  private int currentPlaybackTab = 0;

  /**
   *
   * Icons for the record button
   *
   */
  private ImageIcon recordImageIcon =
          new ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/record.gif"));

  private ImageIcon recordActiveImageIcon =
          new ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/recordActive.gif"));

  private StyleComboBoxModel defStyleComboBoxModel = new StyleComboBoxModel();

  public StyleListModel styleListModel = new StyleListModel();

  private SectionListModel sectionListModel = new SectionListModel();

  private SectionInfo sectionInfo;

  private VoicingTableModel voicingTableModel = new VoicingTableModel();
  
  private DefaultListModel voicingSequenceListModel = new DefaultListModel();
  
  private imp.gui.VoicingKeyboard keyboard = null;
 
 
  private static DefaultTableCellRenderer voicingRenderer = new DefaultTableCellRenderer()
    {
    public Component getTableCellRendererComponent(
            JTable table,
            Object value,
            boolean isSelected,
            boolean hasFocus,
            int row,
            int column)
      {

      Component c = super.getTableCellRendererComponent(
              table, value, isSelected, hasFocus, row, column);



      c.setEnabled((Boolean)table.getModel().getValueAt(row, 5));



      return c;

      }

    };

  /**
   *
   * Various Instrument Chooser objects for the different preferences
   *
   */
  private InstrumentChooser melodyInst,  chordInst,  bassInst,  defMelodyInst,  defChordInst,  defBassInst;

  private SourceEditorDialog leadsheetEditor = null;;

  private SourceEditorDialog grammarEditor = null;;

  private StyleEditor styleEditor = null;

  /**
   *
   * Various input mode names
   *
   */
  public enum Mode
    {
    NORMAL,
    DRAWING,
    RECORDING

    }

  /**
   *
   * current mode, previous mode, play status
   *
   */
  private Mode mode = Mode.NORMAL;

  private Mode previousMode = Mode.NORMAL;

  private MidiPlayListener.Status isPlaying = MidiPlayListener.Status.STOPPED;

  /**
   *
   * latency measurement tool for the preferences dialog
   *
   */
  private MidiLatencyMeasurementTool midiLatencyMeasurement;

  /**
   *
   * If playback indicator goes off the screen, autoscroll to show it again
   *
   * if this value is true.  This is always set to true when playback starts,
   *
   * but if the user scrolls it is temporarily set to false until the next
   *
   * playback.
   *
   */
  private boolean autoScrollOnPlayback;

  private CriticDialog criticDialog;

  /**
   *
   * Handles slider events and playback time label updates
   *
   */
  PlaybackSliderManager playbackManager;

  ActionListener repainter;
  
  int recurrentIteration = 1;



  /**
   *
   * Constructs a new Notate JFrame.
   *
   * Recursively calls Notate(Score, int, int) with a default blank Score and
   *
   * the (0,0) origin.
   *
   */
  public Notate()
    {
    this(new Score(), -1, -1);
    }

  /**
   *
   * Constructs a new Notate JFrame.
   *
   * Recursively calls Notate(Score, int, int) with a default blank part and
   *
   * the given x, y-coordinates.
   *
   *
   *
   * @param x         x-coordiante of the top left corner of the frame
   *
   * @param y         y-coordinate of the top left corner of the frame
   *
   */
  public Notate(int x, int y)
    {
    this(new Score(), x, y);
    }

  /**
   *
   * Constructs a new Notate JFrame.
   *
   * Recursively calls Notate(Score, int, int) with a given part inputted and
   *
   * the (0,0) origin.
   *
   *
   *
   * @param score      the score to be displayed in the scoreFrame
   *
   */
  public Notate(Score score)
    {
    this(score, -1, -1);
    }

  public Notate(Score score, Advisor adv, ImproVisor impro)
    {
    this(score, adv, impro, -1, -1);
    }

  /**
   *
   * Needed in order to call the constructor with advice
   *
   *
   *
   * @param score     the score to be displayed in the scoreFrame
   *
   * @param x         x-coordiante of the top left corner of the frame
   *
   * @param y         y-coordinate of the top left corner of the frame
   *
   */
  public Notate(Score score, int x, int y)
    {
    this(score, null, ImproVisor.getInstance(), x, y);
    }

  /**
   *
   * Constructs a new Notate JFrame.
   *
   * Sets the title to the score title, the starting location of the frame to
   *
   * the x, y-coordinates.
   *
   *
   *
   * @param score     the score to be displayed in the scoreFrame
   *
   * @param adv       the advice directory to be used
   *
   * @param impro     the main Impro-Visor program
   *
   * @param x         x-coordiante of the top left corner of the frame
   *
   * @param y         y-coordinate of the top left corner of the frame
   *
   *
   *
   * @see #initComponents()
   *
   * @see #setupArrays()
   *
   */

  public Notate(Score score, Advisor adv, ImproVisor impro, int x, int y)
    {
    super();

    setTitle(score.getTitle());
    
    setTransposition(score.getTransposition());

    // all windows should be registered when created so that the window menu can construct a list of windows

    WindowRegistry.registerWindow(this);

    // the glass pane, when set visible, will disable mouse events

    this.getRootPane().setGlassPane(new CapturingGlassPane());

    this.getRootPane().getGlassPane().setVisible(false);

    this.score = score;

    this.adv = adv;

    beatValue = BEAT * 4 / 4;

    measureLength = beatValue * 4;

    this.impro = impro;

    this.cm = new CommandManager();

    openLSFC = new JFileChooser();

    saveLSFC = new JFileChooser();

    saveAWT = new FileDialog(this, "Save Leadsheet As...", FileDialog.SAVE);

    revertLSFC = new JFileChooser();

    vocfc = new JFileChooser();

    midfc = new JFileChooser();

    musicxmlfc = new JFileChooser();

    grammarfc = new JFileChooser();

    midiLatencyMeasurement = new MidiLatencyMeasurementTool(this);

    /* No longer used
    styleEditor = new SourceEditorDialog(null, false, this, cm,
            SourceEditorDialog.STYLE);
    */

    leadsheetEditor = new SourceEditorDialog(null, false, this, cm,
            SourceEditorDialog.LEADSHEET);

    leadsheetEditor.setRows(LEADSHEET_EDITOR_ROWS);

    grammarEditor = new SourceEditorDialog(null, false, this, cm,
            SourceEditorDialog.GRAMMAR);

    grammarEditor.setRows(GRAMMAR_EDITOR_ROWS);


    // MIDI Preferences Dialog init

    midiManager = imp.ImproVisor.getMidiManager();

    midiIn = new MidiDeviceChooser(midiManager.getMidiInInfo());

    midiOut = new MidiDeviceChooser(midiManager.getMidiOutInfo());

    midiIn.setSelectedItem(midiManager.getInDeviceInfo());

    midiOut.setSelectedItem(midiManager.getOutDeviceInfo());


    midiSynth = new MidiSynth(midiManager);

    midiRecorder = new MidiNoteActionHandler(this);

    midiStepInput = new MidiStepEntryActionHandler(this);

    setStepInput(false);

    criticDialog = new CriticDialog(lickgenFrame);

    // Establish Directories

    try
      {
      basePath = baseDir.getCanonicalPath().substring(0,
              baseDir.getCanonicalPath().length() - 5);
      }
    catch( Exception e )
      {
      assert (false);
      }

//        System.out.println("basePath = " + basePath);          

    vocabDir = new File(basePath + Directories.vocabDirName);

    grammarDir = new File(basePath + Directories.grammarDirName);

    leadsheetDir = new File(basePath + leadsheetDirName);

    midiDir = leadsheetDir + File.separator + "midi";


    // setup the file choosers' initial paths

    LeadsheetFileView lsfv = new LeadsheetFileView();

    lsOpenPreview = new LeadsheetPreview(openLSFC);

    lsOpenPreview.getCheckbox().setText("Open in new window");


    openLSFC.setCurrentDirectory(leadsheetDir);

    openLSFC.setDialogType(JFileChooser.OPEN_DIALOG);

    openLSFC.setDialogTitle("Open Leadsheet");

    openLSFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

    openLSFC.resetChoosableFileFilters();

    openLSFC.addChoosableFileFilter(new LeadsheetFilter());

    openLSFC.setFileView(lsfv);

    openLSFC.setAccessory(lsOpenPreview);



    lsSavePreview = new LeadsheetPreview(saveLSFC);

    lsSavePreview.getCheckbox().setVisible(false);

    saveLSFC.setCurrentDirectory(leadsheetDir);

    saveLSFC.setDialogType(JFileChooser.SAVE_DIALOG);

    saveLSFC.setDialogTitle("Save Leadsheet As");

    saveLSFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

    saveLSFC.resetChoosableFileFilters();

    saveLSFC.addChoosableFileFilter(new LeadsheetFilter());

    saveLSFC.setSelectedFile(new File(lsDef));

    saveLSFC.setFileView(lsfv);

    saveLSFC.setAccessory(lsSavePreview);

    saveAWT.setDirectory(leadsheetDir.getAbsolutePath());

    revertLSFC.setDialogType(JFileChooser.CUSTOM_DIALOG);

    revertLSFC.setDialogTitle("Revert without saving?");

    revertLSFC.setFileSelectionMode(JFileChooser.FILES_ONLY);


    // Set directories of file choosers

    vocfc.setCurrentDirectory(vocabDir);

    midfc.setCurrentDirectory(leadsheetDir);

    midfc.setDialogType(JFileChooser.SAVE_DIALOG);

    midfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    midfc.resetChoosableFileFilters();

    midfc.addChoosableFileFilter(new MidiFilter());

    musicxmlfc.setCurrentDirectory(leadsheetDir);

    musicxmlfc.setDialogType(JFileChooser.SAVE_DIALOG);

    musicxmlfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    musicxmlfc.resetChoosableFileFilters();

    musicxmlfc.addChoosableFileFilter(new MusicXMLFilter());


    grammarfc.setCurrentDirectory(vocabDir); // original

    //attempted change grammarfc.setCurrentDirectory(grammarDir);

    // set the initial file to be null

    savedLeadsheet = null;

    savedVocab =
            new File(Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE));

    melodyInst = new InstrumentChooser();

    chordInst = new InstrumentChooser();

    bassInst = new InstrumentChooser();

    defMelodyInst = new InstrumentChooser();

    defChordInst = new InstrumentChooser();

    defBassInst = new InstrumentChooser();

    // Use to decide whether to trigger scrolling early.
    // Declare final, as it is accessed from inner class

    final int earlyScrollMargin = 160;

    repainter = new ActionListener()
    {
      public void actionPerformed(ActionEvent evt)
        {
            if( getPlaying() == MidiPlayListener.Status.STOPPED )
            {
                if (keyboard != null && keyboard.isVisible())
                {
                    keyboard.setPlayback(false);
                }
                //return;
            }

        int slotDelay = 
                (int)(midiSynth.getTotalSlots() * (1e6 * trackerDelay / midiSynth.getTotalMicroseconds()));
        
        int slotInPlayback = midiSynth.getSlot() - slotDelay;
        int slot = slotInPlayback;
        int totalSlots = midiSynth.getTotalSlots();
        
        //System.out.println("Total Slots: " + midiSynth.getTotalSlots());
        //System.out.println("Slot in playback: " + slotInPlayback);
                
        // Exploratory set up for recurrent generation of choruses:
        
        // Gap between the start of next generation and end of previous playback.
        
        int gap = lickgenFrame.getGap();
        
        //slotInPlayback += playbackOffset;

        int chorusSize = getScore().getLength();

        int tab = currentPlaybackTab;
        
        currentPlaybackTab = slotInPlayback / chorusSize;
        
        //slotInPlayback %= chorusSize;
        
        int slotInChorus = slotInPlayback % chorusSize;
        
        Chord currentChord = chordProg.getCurrentChord(slotInChorus);
        
        if (keyboard != null && keyboard.isVisible() && keyboard.isPlaying())
        {
            keyboardPlayback(currentChord, tab, slotInChorus, slot, totalSlots);
        }

        // Recurrent generation option

            if ( lickgenFrame.getRecurrent()  // recurrentCheckbox.isSelected()
                && (slotInPlayback >= totalSlots - gap) )
            {
                setLickGenStatus("Chorus " + getRecurrentIteration());
                generate(lickgen);
            }

        // if( midiSynth.finishedPlaying() ) original

        // The following variant was originally added to stop playback at the end of a selection
        // However, it also truncates the drum patterns etc. so that needs to be fixed.

        if( midiSynth.finishedPlaying() )
          {
          return;
          }

        // Stop playback when a specified slot is reached.
        
        if( slotInPlayback > stopPlaybackAtSlot )
          {
//        System.out.println("stop at " + slotInPlayback + " vs. " + stopPlaybackAtSlot);
          
          stopPlaying();
          return;
          }
 

        if( autoScrollOnPlayback && currentPlaybackTab != currTabIndex && showPlayLine() )
          {
          playbackGoToTab(currentPlaybackTab);
          }

        Stave stave = getCurrentStave();

        // only draw the playback indicator if it is on the current tab

        if( currentPlaybackTab == currTabIndex )
          {
          stave.repaintDuringPlayback(slotInChorus);

          if( autoScrollOnPlayback && showPlayLine() )
            {

            Rectangle playline = stave.getPlayLine();

            if( playline.height == 0 )
              {
              return;
              }
            
            Rectangle viewport = getCurrentScrollPosition();

            // It should be noted that the early scroll button has an invverted sense.

            boolean earlyScroll = !earlyScrollBtn.isSelected();

            Rectangle adjustedPlayline = (Rectangle)playline.clone();

            if( earlyScroll
             && viewport.getY() + viewport.getHeight() + earlyScrollMargin < stave.getHeight() )
              {
              adjustedPlayline.y += earlyScrollMargin;
              }

            if( !viewport.contains(playline) )
              {
              // If out of view, try adjusting x-coordinate first

              int adjust = stave.leftMargin + 10;

              if( viewport.width < adjust )
                {
                adjust = 0;
                }

              viewport.x = playline.x - adjust;

              if( viewport.x < 0 )
                {
                viewport.x = 0;
                }
              }

              // If still out of view, try adjusting the y-coordinate

              if( !viewport.contains(adjustedPlayline) )
                {
                viewport.y = playline.y;

                if( playline.y < 0 )
                  {
                  playline.y = 0;
                  }
                }

              if( viewport.contains(playline) )
                {
                setCurrentScrollPosition(viewport);
                }
              
            }
          }
        }
      };



    lickgen = new LickGen("vocab" + File.separator + "My.grammar", this); //orig

    ChordDescription.load(vocabDir + File.separator + musicxmlFile);

    
    initComponents();


    lickgenFrame = new LickgenFrame(this, lickgen, cm);

    populateNotateGrammarMenu();

    postInitComponents();
    
    globalBtn.setFocusPainted(false);
    
    errorDialog.setLocationRelativeTo(this);
    
    // sets the title of the frames to the score title

    setTitle(score.getTitle());

    // setup the stave and part arrays and draw them

    setupArrays();

    setTempo(score.getTempo());

    // set the menu and button states

    setItemStates();

    //okErrorBtn = ErrorLog.setDialog(errorDialog);

    LickLog.setDialog(duplicateLickDialog, duplicateLickText, this);

    setDefaultButton(duplicateLickDialog, ignoreDuplicate);

    score.getChordProg().setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));

    sectionInfo = score.getChordProg().getSectionInfo().copy();

    showAdviceButton.setSelected(adviceInitiallyOpen);

    impro.setPlayEntrySounds(true);

    impro.setShowAdvice(adviceInitiallyOpen);

    getCurrentOrigPart().setInstrument(
            Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MELODY_INSTRUMENT)) - 1);

    score.setChordInstrument(
            Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_CHORD_INSTRUMENT)) - 1);

    score.setBassInstrument(
            Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_BASS_INSTRUMENT)) - 1);

    String setAlwaysUseString =
            Preferences.getPreference(Preferences.ALWAYS_USE_BUTTONS);

    // Set checkbox states.

    alwaysUseBass.setSelected(setAlwaysUseString.charAt(ALWAYS_USE_BASS_INDEX) == TRUE_CHECK_BOX);
    alwaysUseChord.setSelected(setAlwaysUseString.charAt(ALWAYS_USE_CHORD_INDEX) == TRUE_CHECK_BOX);
    alwaysUseMelody.setSelected(setAlwaysUseString.charAt(ALWAYS_USE_MELODY_INDEX) == TRUE_CHECK_BOX);
    alwaysUseStave.setSelected(Preferences.getAlwaysUseStave());

    setTrackerDelay(Preferences.getPreference(Preferences.TRACKER_DELAY));

    setChordFontSizeSpinner(score.getChordFontSize());


    //setBars(defaultBarsPerPart);

    setBars(score.getBarsPerChorus());

    updateSelection();
    
    Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener()
      {
      public void eventDispatched(AWTEvent event)
        {
        if( ((KeyEvent)event).getKeyCode() == KeyEvent.VK_ESCAPE )
          {
          if( preferencesDialog.isVisible() && preferencesDialog.isActive() )
            {
            preferencesDialog.setVisible(false);
            }

          if( lickgenFrame.isVisible() && lickgenFrame.isActive() )
            {
            lickgenFrame.setVisible(false);
            }

          if( voicingTestFrame.isVisible() && voicingTestFrame.isActive() )
            {
            voicingTestFrame.setVisible(false);
            }
          }
        }
      }, 
      AWTEvent.KEY_EVENT_MASK);

      setVolumeDefaults();

    cm.changedSinceLastSave(false);

    if( x == -1 && y == -1 )
      {
      this.setLocationRelativeTo(null);
      }
    else
      {
      this.setLocation(x, y);
      }

    setVisible(true);
    staveRequestFocus();
    
    }

  public static void setDefaultButton(JDialog dialog, JButton button)
    {
    dialog.getRootPane().setDefaultButton(button);
    }


  /**
   * Additional initializations not covered by the IDE-generated code
   */

  public void postInitComponents()
    {
    // Set the size of this Notate frame
    // replaced by RK: 6/13/2010: this.setSize(fWidth, fHeight);

    // Set height to full screen

    setNotateFrameHeight(this);


    voicingTestFrame.pack();

    voicingTestFrame.setSize(875, 525);

    voicingTestFrame.setLocationRelativeTo(this);

    melodyInst.setDialog(preferencesDialog);

    chordInst.setDialog(preferencesDialog);

    bassInst.setDialog(preferencesDialog);

    defMelodyInst.setDialog(preferencesDialog);

    defChordInst.setDialog(preferencesDialog);

    defBassInst.setDialog(preferencesDialog);

    changePrefTab(leadsheetBtn, leadsheetPreferences);

    preferencesDialog.setSize(preferencesDialogDimension);

    preferencesDialog.setLocationRelativeTo(this);

    lickgenFrame.pack();
    
    lickgenFrame.resetTriageParameters(false);

    // NOTE: This is tricky, because the IDE doesn't do it for you.

    // resize the frames and make the appropriate frames visible


    adviceFrame.setSize(400, 500);

    adviceFrame.setLocationRelativeTo(this);

    /*
     * configure the playback manager with the components that it should manage
     */

    playbackManager = new PlaybackSliderManager(midiSynth, playbackTime,
            playbackTotalTime, playbackSlider, repainter);

    }

  public Advisor getAdvisor()
    {
    return adv;
    }

  public Score getScore()
    {
    return score;
    }

  /**
   *
   * Returns if the staves continuously auto-adjust their layout
   *
   * @return boolean          flag for if the staves continuously adjust
   *
   *                          their layout
   *
   */
  public boolean getAutoAdjust()
    {
    return autoAdjustStaves;
    }

  private boolean drawPlayLine;

  public void setShowPlayLine(boolean showLine)
    {
    drawPlayLine = showLine;
    }

  public boolean showPlayLine()
    {
    return trackCheckBox.isSelected() && drawPlayLine;
    }

  private String windowTitle;

  public void setTitle(String title)
    {
    super.setTitle(windowTitlePrefix + (title.trim().equals("") ? "" : windowTitlePrefixSeparator) + title.trim());

    if( score != null )
      {
      score.setTitle(title);
      }

    windowTitle = title;
    }

  public String getTitle()
    {
    return windowTitle;
    }

  /**
   *
   * Returns whether paste should always overwrite notes
   *
   * @return boolean          true if paste should always overwrite
   *
   */
  public boolean getAlwaysPasteOver()
    {
    return alwaysPasteOver;
    }

  /**
   *
   * Returns the currently selected tab
   *
   * @return int              the currently selected tab
   *
   */
  public int getCurrTabIndex()
    {
    return currTabIndex;
    }

  /**
   *
   * Returns the Stave at the given tab index
   *
   * @param index             the index of the StaveScrollPane
   *
   * @return Stave            the Stave at the given index
   *
   */
  public Stave getStaveAtTab(int index)
    {
    if( index >= 0 && index < staveScrollPane.length )
      {
      return staveScrollPane[index].getStave();
      }
    else
      {
      return null;
      }
    }

  /**
   *
   * This method is called from within the constructor to
   *
   * initialize the form. 
   *
   * WARNING: Do NOT modify this code. The content of this method is
   *
   * always regenerated by the Form Editor.
   */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        preferencesDialog = new javax.swing.JDialog();
        buttonPanel = new javax.swing.JPanel();
        globalBtn = new javax.swing.JToggleButton();
        leadsheetBtn = new javax.swing.JToggleButton();
        chorusBtn = new javax.swing.JToggleButton();
        styleBtn = new javax.swing.JToggleButton();
        midiBtn = new javax.swing.JToggleButton();
        contourBtn = new javax.swing.JToggleButton();
        okcancelPanel = new javax.swing.JPanel();
        cancelBtn = new javax.swing.JButton();
        resetBtn = new javax.swing.JButton();
        savePrefsBtn = new javax.swing.JButton();
        preferencesScrollPane = new javax.swing.JScrollPane();
        globalPreferences = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        globalTabs = new javax.swing.JTabbedPane();
        defaultsTab = new javax.swing.JPanel();
        defVocabFileLabel = new javax.swing.JLabel();
        defVocabFile = new javax.swing.JTextField();
        defMelodyInstLabel = new javax.swing.JLabel();
        chordDistLabel = new javax.swing.JLabel();
        defChordInstLabel = new javax.swing.JLabel();
        chordDist = new javax.swing.JTextField();
        defBassInstLabel = new javax.swing.JLabel();
        defStyleLabel = new javax.swing.JLabel();
        defStyleComboBox = new javax.swing.JComboBox();
        defChordPanel = defChordInst;
        alwaysUseChord = new javax.swing.JCheckBox();
        defMelodyPanel = defMelodyInst;
        alwaysUseMelody = new javax.swing.JCheckBox();
        defBassInstPanel = defBassInst;
        alwaysUseBass = new javax.swing.JCheckBox();
        voicingLabel = new javax.swing.JLabel();
        voicing = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        defVolumes = new javax.swing.JPanel();
        defAllPanel = new javax.swing.JPanel();
        defMasterVolSlider = new javax.swing.JSlider();
        jSeparator30 = new javax.swing.JSeparator();
        defEntryPanel = new javax.swing.JPanel();
        defEntryVolSlider = new javax.swing.JSlider();
        jSeparator31 = new javax.swing.JSeparator();
        defBassVolPanel = new javax.swing.JPanel();
        defBassVolSlider = new javax.swing.JSlider();
        defDrumVolPanel = new javax.swing.JPanel();
        defDrumVolSlider = new javax.swing.JSlider();
        defChordVolPanel = new javax.swing.JPanel();
        defChordVolSlider = new javax.swing.JSlider();
        defMelodyVolPanel = new javax.swing.JPanel();
        defMelodyVolSlider = new javax.swing.JSlider();
        defaultStaveTypePanel = new javax.swing.JPanel();
        trebleStave = new javax.swing.JRadioButton();
        bassStave = new javax.swing.JRadioButton();
        grandStave = new javax.swing.JRadioButton();
        autoStave = new javax.swing.JRadioButton();
        alwaysUseStave = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        trackerDelayLabel = new javax.swing.JLabel();
        trackerDelayTextField = new javax.swing.JTextField();
        defaultTempoTF = new javax.swing.JTextField();
        defTempoLabel = new javax.swing.JLabel();
        appearanceTab = new javax.swing.JPanel();
        visAdvicePanel = new javax.swing.JPanel();
        cells = new javax.swing.JCheckBox();
        chordTones = new javax.swing.JCheckBox();
        quotes = new javax.swing.JCheckBox();
        colorTones = new javax.swing.JCheckBox();
        licks = new javax.swing.JCheckBox();
        idioms = new javax.swing.JCheckBox();
        chordExtns = new javax.swing.JCheckBox();
        approachTones = new javax.swing.JCheckBox();
        chordSubs = new javax.swing.JCheckBox();
        scaleTones = new javax.swing.JCheckBox();
        noteColoringLabel = new javax.swing.JLabel();
        chordToneLabel = new javax.swing.JLabel();
        colorToneLabel = new javax.swing.JLabel();
        approachToneLabel = new javax.swing.JLabel();
        otherLabel = new javax.swing.JLabel();
        blackChordBtn = new javax.swing.JRadioButton();
        blackLabel = new javax.swing.JLabel();
        redLabel = new javax.swing.JLabel();
        greenLabel = new javax.swing.JLabel();
        blueLabel = new javax.swing.JLabel();
        blackColorBtn = new javax.swing.JRadioButton();
        blackApproachBtn = new javax.swing.JRadioButton();
        blackOtherBtn = new javax.swing.JRadioButton();
        redChordBtn = new javax.swing.JRadioButton();
        redColorBtn = new javax.swing.JRadioButton();
        redApproachBtn = new javax.swing.JRadioButton();
        redOtherBtn = new javax.swing.JRadioButton();
        greenChordBtn = new javax.swing.JRadioButton();
        greenColorBtn = new javax.swing.JRadioButton();
        greenApproachBtn = new javax.swing.JRadioButton();
        greenOtherBtn = new javax.swing.JRadioButton();
        blueChordBtn = new javax.swing.JRadioButton();
        blueColorBtn = new javax.swing.JRadioButton();
        blueApproachBtn = new javax.swing.JRadioButton();
        blueOtherBtn = new javax.swing.JRadioButton();
        visAdviceLabel = new javax.swing.JLabel();
        trackCheckBox = new javax.swing.JCheckBox();
        trackCheckBox.setSelected(Boolean.parseBoolean(Preferences.getPreference(Preferences.SHOW_TRACKING_LINE)));
        defaultChordFontSizeSpinner = new javax.swing.JSpinner();
        cacheTab = new javax.swing.JPanel();
        cachePanel = new javax.swing.JPanel();
        enableCache = new javax.swing.JCheckBox();
        defCacheSizeLabel = new javax.swing.JLabel();
        cacheSize = new javax.swing.JTextField();
        purgeCache = new javax.swing.JButton();
        leadsheetPreferences = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTabbedPane5 = new javax.swing.JTabbedPane();
        leadsheetSpecificPanel = new javax.swing.JPanel();
        breakpointLabel = new javax.swing.JLabel();
        breakpointTF = new javax.swing.JTextField();
        scoreTitleTF = new javax.swing.JTextField();
        measuresPerPartLabel = new javax.swing.JLabel();
        prefMeasTF = new javax.swing.JTextField();
        tempoTF = new javax.swing.JTextField();
        tempoLabel = new javax.swing.JLabel();
        composerLabel = new javax.swing.JLabel();
        leadsheetTitleLabel = new javax.swing.JLabel();
        composerField = new javax.swing.JTextField();
        chordIInstLabel = new javax.swing.JLabel();
        bassInstLabel = new javax.swing.JLabel();
        keySignatureLabel = new javax.swing.JLabel();
        keySignatureTF = new javax.swing.JTextField();
        commentsLabel = new javax.swing.JLabel();
        commentsTF = new javax.swing.JTextField();
        timeSignatureLabel = new javax.swing.JLabel();
        timeSignaturePanel = new javax.swing.JPanel();
        timeSignatureTopTF = new javax.swing.JTextField();
        timeSignatureBottomTF = new javax.swing.JTextField();
        chordInstPanel = chordInst;
        bassInstPanel = bassInst;
        chorusPreferences = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        chorusSpecificPanel = new javax.swing.JPanel();
        partTitleLabel = new javax.swing.JLabel();
        partTitleTF = new javax.swing.JTextField();
        melodyInsttLabel = new javax.swing.JLabel();
        partComposerLabel = new javax.swing.JLabel();
        partComposerTF = new javax.swing.JTextField();
        layoutLabel = new javax.swing.JLabel();
        layoutTF = new javax.swing.JTextField();
        staveButtonPanel = new javax.swing.JPanel();
        autoStaveBtn = new javax.swing.JRadioButton();
        trebleStaveBtn = new javax.swing.JRadioButton();
        bassStaveBtn = new javax.swing.JRadioButton();
        grandStaveBtn = new javax.swing.JRadioButton();
        melodyInstPanel = melodyInst;
        stylePreferences = new javax.swing.JPanel();
        stylePrefLabel = new javax.swing.JLabel();
        styleTabs = new javax.swing.JTabbedPane();
        currentStyleTab = new javax.swing.JPanel();
        selectAStyleLabel = new javax.swing.JLabel();
        swingLabel = new javax.swing.JLabel();
        swingTF = new javax.swing.JTextField();
        styleListScrollPane = new javax.swing.JScrollPane();
        styleList = new javax.swing.JList();
        sectionListScrollPane = new javax.swing.JScrollPane();
        sectionList = new javax.swing.JList();
        newSectionButton = new javax.swing.JButton();
        measureLabel = new javax.swing.JLabel();
        measureTF = new javax.swing.JTextField();
        sectionLabel = new javax.swing.JLabel();
        delSectionButton = new javax.swing.JButton();
        setMeasureButton = new javax.swing.JButton();
        contourPreferences = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        generalContourTab = new javax.swing.JPanel();
        contToneChoices = new javax.swing.JPanel();
        drawScaleTonesCheckBox = new javax.swing.JCheckBox();
        drawChordTonesCheckBox = new javax.swing.JCheckBox();
        drawColorTonesCheckBox = new javax.swing.JCheckBox();
        defaultDrawingMutedCheckBox = new javax.swing.JCheckBox();
        midiPreferences = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        devicesTab = new javax.swing.JPanel();
        midiLabel = new javax.swing.JLabel();
        midiOutPanel = new javax.swing.JPanel();
        midiOutComboBox = new javax.swing.JComboBox();
        midiOutStatus = new javax.swing.JLabel();
        midiInPanel = new javax.swing.JPanel();
        midiInComboBox = new javax.swing.JComboBox();
        midiInStatus = new javax.swing.JLabel();
        echoMidiCheckBox = new javax.swing.JCheckBox();
        reloadDevices = new javax.swing.JButton();
        latencyTab = new javax.swing.JPanel();
        midiLatencyPanel = new javax.swing.JPanel();
        midiLatencyLabel = new javax.swing.JLabel();
        midiLatencyTF = new javax.swing.JTextField();
        midiLatencyUnitsLabel = new javax.swing.JLabel();
        midiCalibrationPanel = new MidiLatencyMeasurementTool(this);
        jLabel4 = new javax.swing.JLabel();
        staveButtonGroup = new javax.swing.ButtonGroup();
        popupMenu = new javax.swing.JPopupMenu();
        overrideMeasPMI = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JSeparator();
        undoPMI = new javax.swing.JMenuItem();
        undoPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMIActionPerformed(evt);
            }
        });
        redoPMI = new javax.swing.JMenuItem();
        redoPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMIActionPerformed(evt);
            }
        });
        jSeparator8 = new javax.swing.JSeparator();
        cutBothPMI = new javax.swing.JMenuItem();
        cutBothPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutBothMIActionPerformed(evt);
            }
        });
        copyBothPMI = new javax.swing.JMenuItem();
        copyBothPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBothMIActionPerformed(evt);
            }
        });
        pasteBothPMI = new javax.swing.JMenuItem();
        pasteBothPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteBothMIActionPerformed(evt);
            }
        });
        jSeparator9 = new javax.swing.JSeparator();
        advicePMI = new javax.swing.JMenuItem();
        advicePMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adviceMIActionPerformed(evt);
            }
        });
        adviceFrame = new javax.swing.JFrame();
        adviceTabbedPane = new javax.swing.JTabbedPane();
        adviceScroll0 = new javax.swing.JScrollPane();
        adviceTree = new javax.swing.JTree();
        scrollScales = new javax.swing.JScrollPane();
        adviceScrollListScales = new javax.swing.JList();
        scrollCells = new javax.swing.JScrollPane();
        adviceScrollListCells = new javax.swing.JList();
        scrollIdioms = new javax.swing.JScrollPane();
        adviceScrollListIdioms = new javax.swing.JList();
        scrollLicks = new javax.swing.JScrollPane();
        adviceScrollListLicks = new javax.swing.JList();
        scrollQuotes = new javax.swing.JScrollPane();
        adviceScrollListQuotes = new javax.swing.JList();
        keySigBtnGroup = new javax.swing.ButtonGroup();
        saveLickFrame = new javax.swing.JFrame();
        enterLickTitle = new javax.swing.JTextField();
        lineLabel1 = new javax.swing.JLabel();
        lickTItleLabel = new javax.swing.JLabel();
        cancelLickTitle = new javax.swing.JButton();
        okSaveButton = new javax.swing.JButton();
        cellRadioButton = new javax.swing.JRadioButton();
        idiomRadioButton = new javax.swing.JRadioButton();
        lickRadioButton = new javax.swing.JRadioButton();
        quoteRadioButton = new javax.swing.JRadioButton();
        defLoadStaveBtnGroup = new javax.swing.ButtonGroup();
        duplicateLickDialog = new javax.swing.JDialog();
        ignoreDuplicate = new javax.swing.JButton();
        saveDuplicate = new javax.swing.JButton();
        duplicateLickScroll = new javax.swing.JScrollPane();
        duplicateLickText = new javax.swing.JTextPane();
        duplicateLickLabel = new javax.swing.JLabel();
        overwriteLickButton = new javax.swing.JButton();
        saveTypeButtonGroup = new javax.swing.ButtonGroup();
        staveChoiceButtonGroup = new javax.swing.ButtonGroup();
        chordColorBtnGrp = new javax.swing.ButtonGroup();
        colorColorBtnGrp = new javax.swing.ButtonGroup();
        approachColorBtnGrp = new javax.swing.ButtonGroup();
        otherColorBtnGrp = new javax.swing.ButtonGroup();
        prefsTabBtnGrp = new javax.swing.ButtonGroup();
        mixerDialog = new javax.swing.JDialog();
        allPanel = new javax.swing.JPanel();
        allVolumeMixerSlider = new javax.swing.JSlider();
        allMuteMixerBtn = new javax.swing.JCheckBox();
        jSeparator28 = new javax.swing.JSeparator();
        entryPanel = new javax.swing.JPanel();
        entryVolume = new javax.swing.JSlider();
        entryMute = new javax.swing.JCheckBox();
        jSeparator29 = new javax.swing.JSeparator();
        bassPanel = new javax.swing.JPanel();
        bassVolume = new javax.swing.JSlider();
        bassMute = new javax.swing.JCheckBox();
        drumPanel = new javax.swing.JPanel();
        drumVolume = new javax.swing.JSlider();
        drumMute = new javax.swing.JCheckBox();
        chordPanel = new javax.swing.JPanel();
        chordVolume = new javax.swing.JSlider();
        chordMute = new javax.swing.JCheckBox();
        melodyPanel = new javax.swing.JPanel();
        melodyVolume = new javax.swing.JSlider();
        melodyMute = new javax.swing.JCheckBox();
        overrideFrame = new javax.swing.JFrame();
        enterMeasures = new javax.swing.JTextField();
        lineLabel = new javax.swing.JLabel();
        okMeasBtn = new javax.swing.JButton();
        measErrorLabel = new javax.swing.JLabel();
        voicingTestFrame = new javax.swing.JFrame();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        chordRootLabel = new javax.swing.JLabel();
        chordRootTF = new javax.swing.JTextField();
        bassNoteLabel = new javax.swing.JLabel();
        bassNoteTF = new javax.swing.JTextField();
        voicingRangeLabel = new javax.swing.JLabel();
        lowRangeTF = new javax.swing.JTextField();
        highRangeTF = new javax.swing.JTextField();
        rangeToLabel = new javax.swing.JLabel();
        rootRangeLabel = new javax.swing.JLabel();
        lowRangeTF2 = new javax.swing.JTextField();
        rangeToLabel2 = new javax.swing.JLabel();
        highRangeTF2 = new javax.swing.JTextField();
        rootEqualBassCheckbox = new javax.swing.JCheckBox();
        rootEqualBassLabel = new javax.swing.JLabel();
        voicingScrollPane = new javax.swing.JScrollPane();
        voicingTable = new javax.swing.JTable(voicingTableModel);
        voicingSequencePanel = new javax.swing.JPanel();
        voicingSequenceLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        voicingSequenceList = new javax.swing.JList();
        voicingSequenceAddButton = new javax.swing.JButton();
        voicingSequenceRemoveButton = new javax.swing.JButton();
        voicingSequencePlayButton = new javax.swing.JButton();
        voicingSequenceUpArrow = new javax.swing.JLabel();
        voicingSequenceDownArrow = new javax.swing.JLabel();
        chordSearchLabel = new javax.swing.JLabel();
        chordSearchTF = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        buildTableButton = new javax.swing.JButton();
        pianoKeyboardButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        extEntryTF = new javax.swing.JTextField();
        extEntryLabel = new javax.swing.JLabel();
        insertVoicingButton = new javax.swing.JButton();
        playVoicingButton = new javax.swing.JButton();
        voicingEntryTF = new javax.swing.JTextField();
        voicingEntryLabel = new javax.swing.JLabel();
        newVoicingButton = new javax.swing.JButton();
        voicingDeleteButton = new javax.swing.JButton();
        dummyPanel = new javax.swing.JPanel();
        newVoicingDialog = new javax.swing.JDialog();
        newVoicingNameLabel = new javax.swing.JLabel();
        newVoicingNameTF = new javax.swing.JTextField();
        newVoicingTypeLabel = new javax.swing.JLabel();
        newVoicingChordLabel = new javax.swing.JLabel();
        newVoicingChordTF = new javax.swing.JTextField();
        newVoicingSaveButton = new javax.swing.JButton();
        newVoicingCancelButton = new javax.swing.JButton();
        newVoicingTypeCB = new javax.swing.JComboBox();
        deleteVoicingDialog = new javax.swing.JDialog();
        deleteVoicingOKButton = new javax.swing.JButton();
        deleteVoicingCancelButton = new javax.swing.JButton();
        deleteVoicingLabel = new javax.swing.JLabel();
        generatorButtonGroup = new javax.swing.ButtonGroup();
        midiStyleSpec = new javax.swing.JFrame();
        jPanel10 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        bassTabPanel = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        bassStyleSpecScrollPane = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jPanel17 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jPanel21 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jPanel19 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        jTextField6 = new javax.swing.JTextField();
        jPanel20 = new javax.swing.JPanel();
        jLabel15 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        okcancelPanel1 = new javax.swing.JPanel();
        cancelBtn1 = new javax.swing.JButton();
        resetBtn1 = new javax.swing.JButton();
        savePrefsBtn1 = new javax.swing.JButton();
        drumTabPanel = new javax.swing.JPanel();
        jPanel22 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jPanel23 = new javax.swing.JPanel();
        jPanel24 = new javax.swing.JPanel();
        jPanel25 = new javax.swing.JPanel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel26 = new javax.swing.JPanel();
        jPanel27 = new javax.swing.JPanel();
        jLabel21 = new javax.swing.JLabel();
        jTextField10 = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        okcancelPanel2 = new javax.swing.JPanel();
        cancelBtn2 = new javax.swing.JButton();
        resetBtn2 = new javax.swing.JButton();
        savePrefsBtn2 = new javax.swing.JButton();
        chordTabPanel = new javax.swing.JPanel();
        jPanel28 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList4 = new javax.swing.JList();
        jPanel29 = new javax.swing.JPanel();
        jPanel30 = new javax.swing.JPanel();
        jPanel31 = new javax.swing.JPanel();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jPanel32 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jTextField9 = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jPanel33 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jTextField12 = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jTextField13 = new javax.swing.JTextField();
        okcancelPanel3 = new javax.swing.JPanel();
        cancelBtn3 = new javax.swing.JButton();
        resetBtn3 = new javax.swing.JButton();
        savePrefsBtn3 = new javax.swing.JButton();
        truncatePartDialog = new javax.swing.JDialog();
        cancelTruncate = new javax.swing.JButton();
        acceptTruncate = new javax.swing.JButton();
        truncatePartLabel = new javax.swing.JLabel();
        productionBtnGrp = new javax.swing.ButtonGroup();
        grammarExtractionButtonGroup = new javax.swing.ButtonGroup();
        jPopupMenu1 = new javax.swing.JPopupMenu();
        toolbarPanel = new javax.swing.JPanel();
        standardToolbar = new javax.swing.JToolBar();
        newBtn = new javax.swing.JButton();
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMIActionPerformed(evt);
            }
        });
        openBtn = new javax.swing.JButton();
        saveBtn = new javax.swing.JButton();
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLeadsheetMIActionPerformed(evt);
            }
        });
        printBtn = new javax.swing.JButton();
        printBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMIActionPerformed(evt);
            }
        });
        cutBothBtn = new javax.swing.JButton();
        cutBothBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutBothMIActionPerformed(evt);
            }
        });
        copyBothBtn = new javax.swing.JButton();
        copyBothBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBothMIActionPerformed(evt);
            }
        });
        pasteBothBtn = new javax.swing.JButton();
        pasteBothBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteBothMIActionPerformed(evt);
            }
        });
        undoBtn = new javax.swing.JButton();
        undoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMIActionPerformed(evt);
            }
        });
        redoBtn = new javax.swing.JButton();
        redoBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMIActionPerformed(evt);
            }
        });
        drawButton = new javax.swing.JButton();
        showAdviceButton = new javax.swing.JToggleButton();
        openGeneratorButton = new javax.swing.JButton();
        generateToolbarBtn = new javax.swing.JButton();
        freezeLayoutButton = new javax.swing.JToggleButton();
        colorationButton = new javax.swing.JToggleButton();
        smartEntryButton = new javax.swing.JToggleButton();
        beamButton = new javax.swing.JToggleButton();
        chordFontSizeSpinner = new javax.swing.JSpinner();
        addTabBtn = new javax.swing.JButton();
        addTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTabMIActionPerformed(evt);
            }
        });
        delTabBtn = new javax.swing.JButton();
        preferencesBtn = new javax.swing.JButton();
        programStatusTF = new javax.swing.JTextField();
        playToolBar = new javax.swing.JToolBar();
        countInPanel = new javax.swing.JPanel();
        countInCheckBox = new javax.swing.JCheckBox();
        playBtn = new javax.swing.JButton();
        pauseBtn = new javax.swing.JToggleButton();
        stopBtn = new javax.swing.JButton();
        recordBtn = new javax.swing.JButton();
        stepInputBtn = new javax.swing.JToggleButton();
        mixerBtn = new javax.swing.JButton();
        playbackPanel = new javax.swing.JPanel();
        playbackTime = new javax.swing.JLabel();
        playbackTotalTime = new javax.swing.JLabel();
        playbackSlider = new javax.swing.JSlider();
        loopPanel = new javax.swing.JPanel();
        loopButton = new javax.swing.JToggleButton();
        loopSet = new javax.swing.JTextField();
        masterVolumePanel = new javax.swing.JPanel();
        allMuteToolBarBtn = new javax.swing.JToggleButton();
        allVolumeToolBarSlider = new javax.swing.JSlider();
        tempoPanel = new javax.swing.JPanel();
        tempoSet = new javax.swing.JTextField();
        tempoSlider = new javax.swing.JSlider();
        transposeSpinner = new javax.swing.JSpinner();
        partBarsPanel = new javax.swing.JPanel();
        partBarsTF1 = new javax.swing.JTextField();
        trackerDelayPanel = new javax.swing.JPanel();
        trackerDelayTextField2 = new javax.swing.JTextField();
        earlyScrollBtn = new javax.swing.JToggleButton();
        parallaxSpinner = new javax.swing.JSpinner();
        textEntryToolBar = new javax.swing.JToolBar();
        textEntryLabel = new javax.swing.JLabel();
        textEntry = new javax.swing.JTextField();
        clearButton = new javax.swing.JButton();
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayMIActionPerformed(evt);
            }
        });
        scoreTab = new javax.swing.JTabbedPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        aboutMI = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JSeparator();
        newMI = new javax.swing.JMenuItem();
        openLeadsheetMI = new javax.swing.JMenuItem();
        revertToSavedMI = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        saveLeadsheetMI = new javax.swing.JMenuItem();
        saveAsLeadsheetMI = new javax.swing.JMenuItem();
        exportAllToMidi = new javax.swing.JMenuItem();
        exportChorusToMusicXML = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        loadAdvMI = new javax.swing.JMenuItem();
        saveAdvice = new javax.swing.JMenuItem();
        saveAsAdvice = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        printMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        quitMI = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        selectAllMI = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JSeparator();
        undoMI = new javax.swing.JMenuItem();
        redoMI = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        cutMelodyMI = new javax.swing.JMenuItem();
        cutChordsMI = new javax.swing.JMenuItem();
        cutBothMI = new javax.swing.JMenuItem();
        copyMelodyMI = new javax.swing.JMenuItem();
        copyChordsMI = new javax.swing.JMenuItem();
        copyBothMI = new javax.swing.JMenuItem();
        pasteMelodyMI = new javax.swing.JMenuItem();
        pasteChordsMI = new javax.swing.JMenuItem();
        pasteBothMI = new javax.swing.JMenuItem();
        pasteOverMI = new javax.swing.JCheckBoxMenuItem();
        jSeparator16 = new javax.swing.JSeparator();
        enterMelodyMI = new javax.swing.JMenuItem();
        enterChordsMI = new javax.swing.JMenuItem();
        enterBothMI = new javax.swing.JMenuItem();
        jSeparator13 = new javax.swing.JSeparator();
        reverseMelody = new javax.swing.JMenuItem();
        invertMelody = new javax.swing.JMenuItem();
        expandMelodyBy2 = new javax.swing.JMenuItem();
        expandMelodyBy3 = new javax.swing.JMenuItem();
        contractMelodyBy2 = new javax.swing.JMenuItem();
        contractMelodyBy3 = new javax.swing.JMenuItem();
        jSeparator14 = new javax.swing.JSeparator();
        copyMelodySelectionToTextWindow = new javax.swing.JMenuItem();
        copyChordSelectionToTextWindow = new javax.swing.JMenuItem();
        copyBothSelectionToTextWindow = new javax.swing.JMenuItem();
        saveSelectionAsLick = new javax.swing.JMenuItem();
        generateLickInSelection = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JSeparator();
        insertRestMeasure = new javax.swing.JMenuItem();
        addRestMI = new javax.swing.JMenuItem();
        resolvePitches = new javax.swing.JMenuItem();
        transposeMenu = new javax.swing.JMenu();
        transposeMelodyUpSemitone = new javax.swing.JMenuItem();
        transposeChordsUpSemitone = new javax.swing.JMenuItem();
        transposeBothUpSemitone = new javax.swing.JMenuItem();
        transposeMelodyUpHarmonically = new javax.swing.JMenuItem();
        transposeMelodyUpOctave = new javax.swing.JMenuItem();
        transposeMelodyDownSemitone = new javax.swing.JMenuItem();
        transposeChordsDownSemitone = new javax.swing.JMenuItem();
        transposeBothDownSemitone = new javax.swing.JMenuItem();
        transposeMelodyDownHarmonically = new javax.swing.JMenuItem();
        transposeMelodyDownOctave = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        oneAutoMI = new javax.swing.JMenuItem();
        autoAdjustMI = new javax.swing.JCheckBoxMenuItem();
        showTitlesMI = new javax.swing.JCheckBoxMenuItem();
        showEmptyTitlesMI = new javax.swing.JCheckBoxMenuItem();
        barNumsMI = new javax.swing.JCheckBoxMenuItem();
        measureCstrLinesMI = new javax.swing.JCheckBoxMenuItem();
        allCstrLinesMI = new javax.swing.JCheckBoxMenuItem();
        playMenu = new javax.swing.JMenu();
        playAllMI = new javax.swing.JMenuItem();
        stopPlayMI = new javax.swing.JMenuItem();
        pausePlayMI = new javax.swing.JMenuItem();
        utilitiesMenu = new javax.swing.JMenu();
        openLeadsheetEditorMI = new javax.swing.JMenuItem();
        lickGeneratorMI = new javax.swing.JMenuItem();
        pianoKeyboardMI = new javax.swing.JMenuItem();
        styleGenerator1 = new javax.swing.JMenuItem();
        voicingTestMI = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();
        notateGrammarMenu = new javax.swing.JMenu();
        preferencesMenu = new javax.swing.JMenu();
        preferencesAcceleratorMI = new javax.swing.JMenuItem();
        globalPrefsMI = new javax.swing.JMenuItem();
        leadsheetPrefsMI = new javax.swing.JMenuItem();
        chorusPrefsMI = new javax.swing.JMenuItem();
        stylePrefsMI = new javax.swing.JMenuItem();
        midiPrefsMI = new javax.swing.JMenuItem();
        contourPrefsMI = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        helpMI = new javax.swing.JMenuItem();
        jSeparator32 = new javax.swing.JSeparator();
        helpAboutMI = new javax.swing.JMenuItem();

        preferencesDialog.setTitle("Preferences");
        preferencesDialog.setFocusCycleRoot(false);
        preferencesDialog.getRootPane().setDefaultButton(savePrefsBtn);
        preferencesDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                preferencesDialogWindowClosing(evt);
            }
        });

        buttonPanel.setBackground(new java.awt.Color(255, 255, 255));
        buttonPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        buttonPanel.setMaximumSize(new java.awt.Dimension(200, 32767));
        buttonPanel.setPreferredSize(new java.awt.Dimension(112, 70));

        prefsTabBtnGrp.add(globalBtn);
        globalBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/global.png"))); // NOI18N
        globalBtn.setText("Global");
        globalBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        globalBtn.setIconTextGap(6);
        globalBtn.setNextFocusableComponent(leadsheetPreferences);
        globalBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        globalBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        globalBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(globalBtn);

        prefsTabBtnGrp.add(leadsheetBtn);
        leadsheetBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/leadsheet.png"))); // NOI18N
        leadsheetBtn.setSelected(true);
        leadsheetBtn.setText("LeadSheet");
        leadsheetBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        leadsheetBtn.setNextFocusableComponent(chorusPreferences);
        leadsheetBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        leadsheetBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        leadsheetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leadsheetBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(leadsheetBtn);

        prefsTabBtnGrp.add(chorusBtn);
        chorusBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/chorus.png"))); // NOI18N
        chorusBtn.setText("Chorus");
        chorusBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chorusBtn.setNextFocusableComponent(stylePreferences);
        chorusBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        chorusBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chorusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chorusBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(chorusBtn);

        prefsTabBtnGrp.add(styleBtn);
        styleBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/style.png"))); // NOI18N
        styleBtn.setText("Style");
        styleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        styleBtn.setIconTextGap(0);
        styleBtn.setNextFocusableComponent(midiPreferences);
        styleBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        styleBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        styleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(styleBtn);

        prefsTabBtnGrp.add(midiBtn);
        midiBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/keys.png"))); // NOI18N
        midiBtn.setText("MIDI");
        midiBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        midiBtn.setIconTextGap(0);
        midiBtn.setNextFocusableComponent(contourPreferences);
        midiBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        midiBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        midiBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(midiBtn);

        prefsTabBtnGrp.add(contourBtn);
        contourBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/pencilCursor.png"))); // NOI18N
        contourBtn.setText("Contour");
        contourBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        contourBtn.setIconTextGap(0);
        contourBtn.setNextFocusableComponent(globalPreferences);
        contourBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        contourBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        contourBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contourBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(contourBtn);

        preferencesDialog.getContentPane().add(buttonPanel, java.awt.BorderLayout.WEST);

        okcancelPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cancelBtn.setText("Cancel");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });
        okcancelPanel.add(cancelBtn);

        resetBtn.setText("Reset");
        resetBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtnActionPerformed(evt);
            }
        });
        okcancelPanel.add(resetBtn);

        savePrefsBtn.setText("Save Preferences");
        savePrefsBtn.setOpaque(true);
        savePrefsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePrefsBtnActionPerformed(evt);
            }
        });
        okcancelPanel.add(savePrefsBtn);

        preferencesDialog.getContentPane().add(okcancelPanel, java.awt.BorderLayout.SOUTH);
        preferencesDialog.getContentPane().add(preferencesScrollPane, java.awt.BorderLayout.CENTER);

        globalPreferences.setBackground(new java.awt.Color(255, 255, 255));
        globalPreferences.setMinimumSize(new java.awt.Dimension(675, 600));
        globalPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/global.png"))); // NOI18N
        jLabel2.setText("  Global Program Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        globalPreferences.add(jLabel2, gridBagConstraints);

        defaultsTab.setLayout(new java.awt.GridBagLayout());

        defVocabFileLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        defVocabFileLabel.setText("Default Vocabulary File:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(defVocabFileLabel, gridBagConstraints);

        defVocabFile.setText(Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE));
        defVocabFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defVocabFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 0);
        defaultsTab.add(defVocabFile, gridBagConstraints);

        defMelodyInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        defMelodyInstLabel.setText("Default Melody Instrument:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(defMelodyInstLabel, gridBagConstraints);

        chordDistLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chordDistLabel.setText("Chord Distance Above Root:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(chordDistLabel, gridBagConstraints);

        defChordInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        defChordInstLabel.setText("Default Chord Instrument:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(defChordInstLabel, gridBagConstraints);

        chordDist.setText(Preferences.getPreference(Preferences.CHORD_DIST_ABOVE_ROOT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 10);
        defaultsTab.add(chordDist, gridBagConstraints);

        defBassInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        defBassInstLabel.setText("Default Bass Instrument:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(defBassInstLabel, gridBagConstraints);

        defStyleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        defStyleLabel.setText("Default Style:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(defStyleLabel, gridBagConstraints);

        defStyleComboBox.setModel(defStyleComboBoxModel);
        defStyleComboBox.setSelectedItem(Advisor.getStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE)));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 0);
        defaultsTab.add(defStyleComboBox, gridBagConstraints);

        alwaysUseChord.setText("Always use this");
        alwaysUseChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysUseChordActionPerformed(evt);
            }
        });
        defChordPanel.add(alwaysUseChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        defaultsTab.add(defChordPanel, gridBagConstraints);

        alwaysUseMelody.setText("Always use this");
        alwaysUseMelody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysUseMelodyActionPerformed(evt);
            }
        });
        defMelodyPanel.add(alwaysUseMelody);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        defaultsTab.add(defMelodyPanel, gridBagConstraints);

        alwaysUseBass.setText("Always use this");
        alwaysUseBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysUseBassActionPerformed(evt);
            }
        });
        defBassInstPanel.add(alwaysUseBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 1, 0, 1);
        defaultsTab.add(defBassInstPanel, gridBagConstraints);

        voicingLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        voicingLabel.setText("Max. Notes in Voicing: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(voicingLabel, gridBagConstraints);

        voicing.setText(Preferences.getPreference(Preferences.MAX_NOTES_IN_VOICING));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 10);
        defaultsTab.add(voicing, gridBagConstraints);

        jLabel11.setText("Default Volumes:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        defaultsTab.add(jLabel11, gridBagConstraints);

        defAllPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "All", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defAllPanel.setOpaque(false);
        defAllPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defAllPanel.setLayout(new java.awt.GridBagLayout());

        defMasterVolSlider.setFont(new java.awt.Font("Arial", 0, 8));
        defMasterVolSlider.setMajorTickSpacing(20);
        defMasterVolSlider.setMaximum(127);
        defMasterVolSlider.setMinorTickSpacing(5);
        defMasterVolSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        defMasterVolSlider.setPaintTicks(true);
        defMasterVolSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        defAllPanel.add(defMasterVolSlider, gridBagConstraints);

        defVolumes.add(defAllPanel);

        jSeparator30.setForeground(new java.awt.Color(153, 255, 255));
        jSeparator30.setOrientation(javax.swing.SwingConstants.VERTICAL);
        defVolumes.add(jSeparator30);

        defEntryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entry", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defEntryPanel.setOpaque(false);
        defEntryPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defEntryPanel.setLayout(new java.awt.GridBagLayout());

        defEntryVolSlider.setFont(new java.awt.Font("Arial", 0, 8));
        defEntryVolSlider.setMajorTickSpacing(20);
        defEntryVolSlider.setMaximum(127);
        defEntryVolSlider.setMinorTickSpacing(5);
        defEntryVolSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        defEntryVolSlider.setPaintTicks(true);
        defEntryVolSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        defEntryPanel.add(defEntryVolSlider, gridBagConstraints);

        defVolumes.add(defEntryPanel);

        jSeparator31.setForeground(new java.awt.Color(153, 255, 255));
        jSeparator31.setOrientation(javax.swing.SwingConstants.VERTICAL);
        defVolumes.add(jSeparator31);

        defBassVolPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bass", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defBassVolPanel.setOpaque(false);
        defBassVolPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defBassVolPanel.setLayout(new java.awt.GridBagLayout());

        defBassVolSlider.setFont(new java.awt.Font("Arial", 0, 8));
        defBassVolSlider.setMajorTickSpacing(20);
        defBassVolSlider.setMaximum(127);
        defBassVolSlider.setMinorTickSpacing(5);
        defBassVolSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        defBassVolSlider.setPaintTicks(true);
        defBassVolSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        defBassVolPanel.add(defBassVolSlider, gridBagConstraints);

        defVolumes.add(defBassVolPanel);

        defDrumVolPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Drums", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defDrumVolPanel.setOpaque(false);
        defDrumVolPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defDrumVolPanel.setLayout(new java.awt.GridBagLayout());

        defDrumVolSlider.setFont(new java.awt.Font("Arial", 0, 8));
        defDrumVolSlider.setMajorTickSpacing(20);
        defDrumVolSlider.setMaximum(127);
        defDrumVolSlider.setMinorTickSpacing(5);
        defDrumVolSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        defDrumVolSlider.setPaintTicks(true);
        defDrumVolSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        defDrumVolSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                defDrumVolSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        defDrumVolPanel.add(defDrumVolSlider, gridBagConstraints);

        defVolumes.add(defDrumVolPanel);

        defChordVolPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chords", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defChordVolPanel.setOpaque(false);
        defChordVolPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defChordVolPanel.setLayout(new java.awt.GridBagLayout());

        defChordVolSlider.setFont(new java.awt.Font("Arial", 0, 8));
        defChordVolSlider.setMajorTickSpacing(20);
        defChordVolSlider.setMaximum(127);
        defChordVolSlider.setMinorTickSpacing(5);
        defChordVolSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        defChordVolSlider.setPaintTicks(true);
        defChordVolSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        defChordVolPanel.add(defChordVolSlider, gridBagConstraints);

        defVolumes.add(defChordVolPanel);

        defMelodyVolPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Melody", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defMelodyVolPanel.setOpaque(false);
        defMelodyVolPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defMelodyVolPanel.setLayout(new java.awt.GridBagLayout());

        defMelodyVolSlider.setFont(new java.awt.Font("Arial", 0, 8));
        defMelodyVolSlider.setMajorTickSpacing(20);
        defMelodyVolSlider.setMaximum(127);
        defMelodyVolSlider.setMinorTickSpacing(5);
        defMelodyVolSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        defMelodyVolSlider.setPaintTicks(true);
        defMelodyVolSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        defMelodyVolPanel.add(defMelodyVolSlider, gridBagConstraints);

        defVolumes.add(defMelodyVolPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        defaultsTab.add(defVolumes, gridBagConstraints);

        defaultStaveTypePanel.setLayout(new java.awt.GridBagLayout());

        defLoadStaveBtnGroup.add(trebleStave);
        trebleStave.setSelected(true);
        trebleStave.setText("Treble");
        trebleStave.setToolTipText("");
        trebleStave.setMaximumSize(new java.awt.Dimension(75, 18));
        trebleStave.setMinimumSize(new java.awt.Dimension(75, 18));
        trebleStave.setPreferredSize(new java.awt.Dimension(75, 18));
        if (Preferences.getPreference(Preferences.DEFAULT_LOAD_STAVE).equals(String.valueOf(StaveType.TREBLE.ordinal())))
        trebleStave.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        defaultStaveTypePanel.add(trebleStave, gridBagConstraints);

        defLoadStaveBtnGroup.add(bassStave);
        bassStave.setText("Bass");
        bassStave.setMaximumSize(new java.awt.Dimension(60, 18));
        bassStave.setMinimumSize(new java.awt.Dimension(60, 18));
        bassStave.setPreferredSize(new java.awt.Dimension(60, 18));
        if (Preferences.getPreference(Preferences.DEFAULT_LOAD_STAVE).equals(String.valueOf(StaveType.BASS.ordinal())))
        bassStave.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        defaultStaveTypePanel.add(bassStave, gridBagConstraints);

        defLoadStaveBtnGroup.add(grandStave);
        grandStave.setText("Grand");
        grandStave.setMaximumSize(new java.awt.Dimension(70, 18));
        grandStave.setMinimumSize(new java.awt.Dimension(70, 18));
        grandStave.setPreferredSize(new java.awt.Dimension(70, 18));
        if (Preferences.getPreference(Preferences.DEFAULT_LOAD_STAVE).equals(String.valueOf(StaveType.GRAND.ordinal())))
        grandStave.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        defaultStaveTypePanel.add(grandStave, gridBagConstraints);

        defLoadStaveBtnGroup.add(autoStave);
        autoStave.setText("Automatic");
        autoStave.setMaximumSize(new java.awt.Dimension(100, 18));
        autoStave.setMinimumSize(new java.awt.Dimension(100, 18));
        autoStave.setPreferredSize(new java.awt.Dimension(100, 18));
        if (Preferences.getPreference(Preferences.DEFAULT_LOAD_STAVE).equals(String.valueOf(StaveType.AUTO.ordinal())))
        autoStave.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.3;
        defaultStaveTypePanel.add(autoStave, gridBagConstraints);

        alwaysUseStave.setText("Always use this");
        alwaysUseStave.setMaximumSize(new java.awt.Dimension(130, 18));
        alwaysUseStave.setMinimumSize(new java.awt.Dimension(130, 18));
        alwaysUseStave.setPreferredSize(new java.awt.Dimension(130, 18));
        alwaysUseStave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alwaysUseStaveActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.4;
        defaultStaveTypePanel.add(alwaysUseStave, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(10, 7, 0, 0);
        defaultsTab.add(defaultStaveTypePanel, gridBagConstraints);

        jLabel13.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel13.setText("Default Stave Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        defaultsTab.add(jLabel13, gridBagConstraints);

        trackerDelayLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        trackerDelayLabel.setText("Tracker Delay:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(trackerDelayLabel, gridBagConstraints);

        trackerDelayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trackerDelayTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 6, 0, 11);
        defaultsTab.add(trackerDelayTextField, gridBagConstraints);

        defaultTempoTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultTempoTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        defaultsTab.add(defaultTempoTF, gridBagConstraints);

        defTempoLabel.setText("Default Tempo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        defaultsTab.add(defTempoLabel, gridBagConstraints);

        globalTabs.addTab("Defaults", defaultsTab);

        appearanceTab.setLayout(new java.awt.GridBagLayout());

        visAdvicePanel.setLayout(new java.awt.GridBagLayout());

        cells.setText("Cells");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 64) != 0)     cells.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(cells, gridBagConstraints);

        chordTones.setText("Chord Tones");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 1) != 0)
        chordTones.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        visAdvicePanel.add(chordTones, gridBagConstraints);

        quotes.setText("Quotes");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 512) != 0)     quotes.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(quotes, gridBagConstraints);

        colorTones.setText("Color Tones");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 2) != 0)     colorTones.setSelected(true);
        colorTones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorTonesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        visAdvicePanel.add(colorTones, gridBagConstraints);

        licks.setText("Licks");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 256) != 0)     licks.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(licks, gridBagConstraints);

        idioms.setText("Idioms");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 128) != 0)     idioms.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(idioms, gridBagConstraints);

        chordExtns.setText("Chord Extensions");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 32) != 0)     chordExtns.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(chordExtns, gridBagConstraints);

        approachTones.setText("Approach Tones");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 8) != 0)     approachTones.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(approachTones, gridBagConstraints);

        chordSubs.setText("Chord Substitutions");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 16) != 0)     chordSubs.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        visAdvicePanel.add(chordSubs, gridBagConstraints);

        scaleTones.setText("Scale Tones");
        if ((Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS)) & 4) != 0)     scaleTones.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        visAdvicePanel.add(scaleTones, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        appearanceTab.add(visAdvicePanel, gridBagConstraints);

        noteColoringLabel.setText("Note Coloring:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        appearanceTab.add(noteColoringLabel, gridBagConstraints);

        chordToneLabel.setText("Chord Tones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        appearanceTab.add(chordToneLabel, gridBagConstraints);

        colorToneLabel.setText("Color Tones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        appearanceTab.add(colorToneLabel, gridBagConstraints);

        approachToneLabel.setText("Approach Tones");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        appearanceTab.add(approachToneLabel, gridBagConstraints);

        otherLabel.setText("Other");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 30, 0, 0);
        appearanceTab.add(otherLabel, gridBagConstraints);

        chordColorBtnGrp.add(blackChordBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(0) == '1')
        blackChordBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        appearanceTab.add(blackChordBtn, gridBagConstraints);

        blackLabel.setText("Black");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        appearanceTab.add(blackLabel, gridBagConstraints);

        redLabel.setText("Red");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        appearanceTab.add(redLabel, gridBagConstraints);

        greenLabel.setText("Green");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        appearanceTab.add(greenLabel, gridBagConstraints);

        blueLabel.setText("Blue");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        appearanceTab.add(blueLabel, gridBagConstraints);

        colorColorBtnGrp.add(blackColorBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(1) == '1')
        blackColorBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        appearanceTab.add(blackColorBtn, gridBagConstraints);

        approachColorBtnGrp.add(blackApproachBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(2) == '1')
        blackApproachBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        appearanceTab.add(blackApproachBtn, gridBagConstraints);

        otherColorBtnGrp.add(blackOtherBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(3) == '1')
        blackOtherBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 9;
        appearanceTab.add(blackOtherBtn, gridBagConstraints);

        chordColorBtnGrp.add(redChordBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(0) == '2')              redChordBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        appearanceTab.add(redChordBtn, gridBagConstraints);

        colorColorBtnGrp.add(redColorBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(1) == '2')              redColorBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        appearanceTab.add(redColorBtn, gridBagConstraints);

        approachColorBtnGrp.add(redApproachBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(2) == '2')              redApproachBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 8;
        appearanceTab.add(redApproachBtn, gridBagConstraints);

        otherColorBtnGrp.add(redOtherBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(3) == '2')              redOtherBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 9;
        appearanceTab.add(redOtherBtn, gridBagConstraints);

        chordColorBtnGrp.add(greenChordBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(0) == '3')              greenChordBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        appearanceTab.add(greenChordBtn, gridBagConstraints);

        colorColorBtnGrp.add(greenColorBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(1) == '3')              greenColorBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        appearanceTab.add(greenColorBtn, gridBagConstraints);

        approachColorBtnGrp.add(greenApproachBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(2) == '3')              greenApproachBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 8;
        appearanceTab.add(greenApproachBtn, gridBagConstraints);

        otherColorBtnGrp.add(greenOtherBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(3) == '3')              greenOtherBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 9;
        appearanceTab.add(greenOtherBtn, gridBagConstraints);

        chordColorBtnGrp.add(blueChordBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(0) == '4')              blueChordBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        appearanceTab.add(blueChordBtn, gridBagConstraints);

        colorColorBtnGrp.add(blueColorBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(1) == '4')              blueColorBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        appearanceTab.add(blueColorBtn, gridBagConstraints);

        approachColorBtnGrp.add(blueApproachBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(2) == '4')              blueApproachBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        appearanceTab.add(blueApproachBtn, gridBagConstraints);

        otherColorBtnGrp.add(blueOtherBtn);
        if (Preferences.getPreference(Preferences.NOTE_COLORING).charAt(3) == '4')              blueOtherBtn.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 9;
        appearanceTab.add(blueOtherBtn, gridBagConstraints);

        visAdviceLabel.setText("Visible Advice Components:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(15, 0, 10, 0);
        appearanceTab.add(visAdviceLabel, gridBagConstraints);

        trackCheckBox.setText("Show Tracking Line");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        appearanceTab.add(trackCheckBox, gridBagConstraints);

        defaultChordFontSizeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Default Chord Font", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        defaultChordFontSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                defaultChordFontChange(evt);
            }
        });
        defaultChordFontSizeSpinner.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                defaultChordFontSizeSpinnerKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        appearanceTab.add(defaultChordFontSizeSpinner, gridBagConstraints);

        globalTabs.addTab("Appearance", appearanceTab);

        cachePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Cache Settings", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        cachePanel.setLayout(new java.awt.GridBagLayout());

        enableCache.setSelected(true);
        enableCache.setText("Enable Cache");
        if (Preferences.getPreference(Preferences.ADV_CACHE_ENABLED).equals("true"))     enableCache.setSelected(true); else     enableCache.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 11;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 10, 0);
        cachePanel.add(enableCache, gridBagConstraints);

        defCacheSizeLabel.setText("Default Cache Size: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 7;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 10, 0);
        cachePanel.add(defCacheSizeLabel, gridBagConstraints);

        cacheSize.setText(Preferences.getPreference(Preferences.ADV_CACHE_SIZE));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 10);
        cachePanel.add(cacheSize, gridBagConstraints);

        purgeCache.setText("Purge Cache");
        purgeCache.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                purgeCacheActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 0);
        cachePanel.add(purgeCache, gridBagConstraints);

        cacheTab.add(cachePanel);

        globalTabs.addTab("Cache", cacheTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        globalPreferences.add(globalTabs, gridBagConstraints);

        leadsheetPreferences.setBackground(new java.awt.Color(255, 255, 255));
        leadsheetPreferences.setPreferredSize(new java.awt.Dimension(563, 507));
        leadsheetPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/leadsheet.png"))); // NOI18N
        jLabel3.setText("  Leadsheet Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        leadsheetPreferences.add(jLabel3, gridBagConstraints);

        leadsheetSpecificPanel.setMaximumSize(new java.awt.Dimension(500, 280));
        leadsheetSpecificPanel.setMinimumSize(new java.awt.Dimension(486, 250));
        leadsheetSpecificPanel.setPreferredSize(new java.awt.Dimension(500, 280));
        leadsheetSpecificPanel.setLayout(new java.awt.GridBagLayout());

        breakpointLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        breakpointLabel.setText("Automatic Stave Breakpoint Pitch:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(breakpointLabel, gridBagConstraints);

        breakpointTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        breakpointTF.setText("54");
        breakpointTF.setMinimumSize(new java.awt.Dimension(50, 19));
        breakpointTF.setNextFocusableComponent(prefMeasTF);
        breakpointTF.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(breakpointTF, gridBagConstraints);

        scoreTitleTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        scoreTitleTF.setNextFocusableComponent(composerField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        leadsheetSpecificPanel.add(scoreTitleTF, gridBagConstraints);

        measuresPerPartLabel.setText("Measures per Chorus:");
        measuresPerPartLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(measuresPerPartLabel, gridBagConstraints);

        prefMeasTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        prefMeasTF.setText("" + defaultBarsPerPart);
        prefMeasTF.setMinimumSize(new java.awt.Dimension(50, 19));
        prefMeasTF.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(prefMeasTF, gridBagConstraints);

        tempoTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tempoTF.setText("160");
        tempoTF.setMinimumSize(new java.awt.Dimension(50, 19));
        tempoTF.setNextFocusableComponent(breakpointTF);
        tempoTF.setPreferredSize(new java.awt.Dimension(50, 19));
        tempoTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempoTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(tempoTF, gridBagConstraints);

        tempoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tempoLabel.setText("Tempo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(tempoLabel, gridBagConstraints);

        composerLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        composerLabel.setText("Composer:");
        composerLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        composerLabel.setPreferredSize(new java.awt.Dimension(90, 17));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(composerLabel, gridBagConstraints);

        leadsheetTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        leadsheetTitleLabel.setText("Leadsheet Title:");
        leadsheetTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        leadsheetSpecificPanel.add(leadsheetTitleLabel, gridBagConstraints);

        composerField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        composerField.setNextFocusableComponent(timeSignatureTopTF);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(composerField, gridBagConstraints);

        chordIInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chordIInstLabel.setText("Chord MIDI Instrument:");
        chordIInstLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(chordIInstLabel, gridBagConstraints);

        bassInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        bassInstLabel.setText("Bass MIDI Instrument:");
        bassInstLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(bassInstLabel, gridBagConstraints);

        keySignatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        keySignatureLabel.setText("Key Signature (+sharps, - flats):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(keySignatureLabel, gridBagConstraints);

        keySignatureTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        keySignatureTF.setText("0");
        keySignatureTF.setMinimumSize(new java.awt.Dimension(50, 19));
        keySignatureTF.setNextFocusableComponent(tempoTF);
        keySignatureTF.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(keySignatureTF, gridBagConstraints);

        commentsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        commentsLabel.setText("Comments:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(commentsLabel, gridBagConstraints);

        commentsTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        commentsTF.setMinimumSize(new java.awt.Dimension(40, 19));
        commentsTF.setNextFocusableComponent(partTitleTF);
        commentsTF.setPreferredSize(new java.awt.Dimension(11, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(commentsTF, gridBagConstraints);

        timeSignatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        timeSignatureLabel.setText("Time Signature:");
        timeSignatureLabel.setAlignmentX(1.0F);
        timeSignatureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(timeSignatureLabel, gridBagConstraints);

        timeSignaturePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        timeSignatureTopTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        timeSignatureTopTF.setNextFocusableComponent(timeSignatureBottomTF);
        timeSignatureTopTF.setPreferredSize(new java.awt.Dimension(50, 19));
        timeSignaturePanel.add(timeSignatureTopTF);

        timeSignatureBottomTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        timeSignatureBottomTF.setNextFocusableComponent(keySignatureTF);
        timeSignatureBottomTF.setPreferredSize(new java.awt.Dimension(50, 19));
        timeSignaturePanel.add(timeSignatureBottomTF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        leadsheetSpecificPanel.add(timeSignaturePanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        leadsheetSpecificPanel.add(chordInstPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        leadsheetSpecificPanel.add(bassInstPanel, gridBagConstraints);

        jTabbedPane5.addTab("Leadsheet", leadsheetSpecificPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        leadsheetPreferences.add(jTabbedPane5, gridBagConstraints);

        chorusPreferences.setBackground(new java.awt.Color(255, 255, 255));
        chorusPreferences.setPreferredSize(new java.awt.Dimension(563, 507));
        chorusPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel19.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/chorus.png"))); // NOI18N
        jLabel19.setText("  Chorus Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        chorusPreferences.add(jLabel19, gridBagConstraints);

        chorusSpecificPanel.setMinimumSize(new java.awt.Dimension(483, 200));
        chorusSpecificPanel.setPreferredSize(new java.awt.Dimension(500, 200));
        chorusSpecificPanel.setLayout(new java.awt.GridBagLayout());

        partTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        partTitleLabel.setText("Chorus Title:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(partTitleLabel, gridBagConstraints);

        partTitleTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        partTitleTF.setToolTipText("The title of this part");
        partTitleTF.setNextFocusableComponent(partComposerTF);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(partTitleTF, gridBagConstraints);

        melodyInsttLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        melodyInsttLabel.setText("Melody Instrument MIDI Number:");
        melodyInsttLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(melodyInsttLabel, gridBagConstraints);

        partComposerLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        partComposerLabel.setText("Chorus Composer:");
        partComposerLabel.setToolTipText("Enter the person composing this part (solo).");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(partComposerLabel, gridBagConstraints);

        partComposerTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(partComposerTF, gridBagConstraints);

        layoutLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        layoutLabel.setText("Layout (bars per line):");
        layoutLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(layoutLabel, gridBagConstraints);

        layoutTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        layoutTF.setToolTipText("Set a specific layout (bars per line).");
        layoutTF.setNextFocusableComponent(swingTF);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        chorusSpecificPanel.add(layoutTF, gridBagConstraints);

        staveButtonPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Stave Type", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        staveButtonPanel.setPreferredSize(new java.awt.Dimension(285, 48));
        staveButtonPanel.setLayout(new java.awt.GridBagLayout());

        staveChoiceButtonGroup.add(autoStaveBtn);
        autoStaveBtn.setSelected(true);
        autoStaveBtn.setText("auto");
        autoStaveBtn.setNextFocusableComponent(trebleStaveBtn);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        staveButtonPanel.add(autoStaveBtn, gridBagConstraints);

        staveChoiceButtonGroup.add(trebleStaveBtn);
        trebleStaveBtn.setText("treble");
        trebleStaveBtn.setNextFocusableComponent(bassStaveBtn);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        staveButtonPanel.add(trebleStaveBtn, gridBagConstraints);

        staveChoiceButtonGroup.add(bassStaveBtn);
        bassStaveBtn.setText("bass");
        bassStaveBtn.setNextFocusableComponent(grandStaveBtn);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        staveButtonPanel.add(bassStaveBtn, gridBagConstraints);

        staveChoiceButtonGroup.add(grandStaveBtn);
        grandStaveBtn.setText("grand");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        staveButtonPanel.add(grandStaveBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
        chorusSpecificPanel.add(staveButtonPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        chorusSpecificPanel.add(melodyInstPanel, gridBagConstraints);

        jTabbedPane4.addTab("Chorus", chorusSpecificPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        chorusPreferences.add(jTabbedPane4, gridBagConstraints);

        stylePreferences.setBackground(new java.awt.Color(255, 255, 255));
        stylePreferences.setLayout(new java.awt.GridBagLayout());

        stylePrefLabel.setFont(new java.awt.Font("Dialog", 1, 14));
        stylePrefLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/style.png"))); // NOI18N
        stylePrefLabel.setText("  Style Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        stylePreferences.add(stylePrefLabel, gridBagConstraints);

        currentStyleTab.setLayout(new java.awt.GridBagLayout());

        selectAStyleLabel.setText("Style:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(selectAStyleLabel, gridBagConstraints);

        swingLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        swingLabel.setText("Swing:");
        swingLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(swingLabel, gridBagConstraints);

        swingTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        swingTF.setText(" ");
        swingTF.setToolTipText("Enter the melody swing value (.5 for no swing, .67 for nominal swing.)");
        swingTF.setMinimumSize(new java.awt.Dimension(50, 19));
        swingTF.setNextFocusableComponent(autoStaveBtn);
        swingTF.setPreferredSize(new java.awt.Dimension(50, 19));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(swingTF, gridBagConstraints);

        styleList.setModel(styleListModel);
        styleList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        styleList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                styleListValueChanged(evt);
            }
        });
        styleListScrollPane.setViewportView(styleList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(styleListScrollPane, gridBagConstraints);

        sectionList.setModel(sectionListModel);
        sectionList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        sectionList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                sectionListValueChanged(evt);
            }
        });
        sectionListScrollPane.setViewportView(sectionList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.75;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(sectionListScrollPane, gridBagConstraints);

        newSectionButton.setText("Add New Section");
        newSectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(newSectionButton, gridBagConstraints);

        measureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        measureLabel.setText("Starting Measure:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(measureLabel, gridBagConstraints);

        measureTF.setMinimumSize(new java.awt.Dimension(50, 19));
        measureTF.setPreferredSize(new java.awt.Dimension(50, 19));
        measureTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measureTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(measureTF, gridBagConstraints);

        sectionLabel.setText("Sections:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(sectionLabel, gridBagConstraints);

        delSectionButton.setText("Delete Section");
        delSectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delSectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(delSectionButton, gridBagConstraints);

        setMeasureButton.setText("Set");
        setMeasureButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setMeasureButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(setMeasureButton, gridBagConstraints);

        styleTabs.addTab("Current Style", currentStyleTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        stylePreferences.add(styleTabs, gridBagConstraints);

        contourPreferences.setBackground(new java.awt.Color(255, 255, 255));
        contourPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/pencilCursor.png"))); // NOI18N
        jLabel10.setText("  Contour Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        contourPreferences.add(jLabel10, gridBagConstraints);

        generalContourTab.setLayout(new java.awt.GridBagLayout());

        contToneChoices.setBorder(javax.swing.BorderFactory.createTitledBorder("Fit notes to contour using:"));
        contToneChoices.setPreferredSize(new java.awt.Dimension(190, 100));
        contToneChoices.setLayout(new java.awt.GridBagLayout());

        drawScaleTonesCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        drawScaleTonesCheckBox.setSelected(true);
        drawScaleTonesCheckBox.setText("Scale tones");
        drawScaleTonesCheckBox.setContentAreaFilled(false);
        drawScaleTonesCheckBox.setIconTextGap(10);

        if (Preferences.getPreference(Preferences.DRAWING_TONES).charAt(0) == 'x')       drawScaleTonesCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contToneChoices.add(drawScaleTonesCheckBox, gridBagConstraints);

        drawChordTonesCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        drawChordTonesCheckBox.setSelected(true);
        drawChordTonesCheckBox.setIconTextGap(10);
        drawChordTonesCheckBox.setLabel("Chord tones");

        if (Preferences.getPreference(Preferences.DRAWING_TONES).charAt(1) == 'x')       drawChordTonesCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contToneChoices.add(drawChordTonesCheckBox, gridBagConstraints);

        drawColorTonesCheckBox.setFont(new java.awt.Font("Dialog", 0, 12));
        drawColorTonesCheckBox.setText("Color tones");
        drawColorTonesCheckBox.setIconTextGap(10);
        if (Preferences.getPreference(Preferences.DRAWING_TONES).charAt(2) == '1')       drawColorTonesCheckBox.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contToneChoices.add(drawColorTonesCheckBox, gridBagConstraints);

        generalContourTab.add(contToneChoices, new java.awt.GridBagConstraints());

        defaultDrawingMutedCheckBox.setFont(new java.awt.Font("Dialog", 0, 10));
        defaultDrawingMutedCheckBox.setSelected(true);
        defaultDrawingMutedCheckBox.setText("Mute drawing sound by default");
        defaultDrawingMutedCheckBox.setIconTextGap(10);
        if (Preferences.getPreference(Preferences.DEFAULT_DRAWING_MUTED).equals("false"))       defaultDrawingMutedCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.ipady = 25;
        generalContourTab.add(defaultDrawingMutedCheckBox, gridBagConstraints);

        jTabbedPane3.addTab("General", generalContourTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        contourPreferences.add(jTabbedPane3, gridBagConstraints);

        midiPreferences.setBackground(new java.awt.Color(255, 255, 255));
        midiPreferences.setAlignmentX(0.0F);
        midiPreferences.setAlignmentY(0.0F);
        midiPreferences.setPreferredSize(new java.awt.Dimension(390, 370));
        midiPreferences.setLayout(new java.awt.GridBagLayout());

        devicesTab.setLayout(new java.awt.GridBagLayout());

        midiLabel.setText("<html>Changing the MIDI devices takes effect <em>immediately</em></html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        devicesTab.add(midiLabel, gridBagConstraints);

        midiOutPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select a device for MIDI output"));
        midiOutPanel.setLayout(new java.awt.GridLayout(2, 0));

        midiOutComboBox.setModel(midiOut);
        midiOutComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiOutComboBoxActionPerformed(evt);
            }
        });
        midiOutPanel.add(midiOutComboBox);

        midiOutStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        midiOutStatus.setText("Status:");
        midiOutStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        midiOutPanel.add(midiOutStatus);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        devicesTab.add(midiOutPanel, gridBagConstraints);

        midiInPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select a device for MIDI input"));
        midiInPanel.setLayout(new java.awt.GridBagLayout());

        midiInComboBox.setModel(midiIn);
        midiInComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiInComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        midiInPanel.add(midiInComboBox, gridBagConstraints);

        midiInStatus.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        midiInStatus.setText("Status:");
        midiInStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        midiInPanel.add(midiInStatus, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        devicesTab.add(midiInPanel, gridBagConstraints);

        echoMidiCheckBox.setSelected(midiManager.getEcho());
        echoMidiCheckBox.setText("echo MIDI input (send MIDI messages from MIDI input to MIDI output)");
        echoMidiCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                echoMidiCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(10, 11, 10, 11);
        devicesTab.add(echoMidiCheckBox, gridBagConstraints);

        reloadDevices.setText("Reload MIDI Devices");
        reloadDevices.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadDevicesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        devicesTab.add(reloadDevices, gridBagConstraints);

        jTabbedPane2.addTab("Devices", devicesTab);

        latencyTab.setLayout(new java.awt.GridBagLayout());

        midiLatencyLabel.setText("MIDI Latency: ");
        midiLatencyPanel.add(midiLatencyLabel);

        midiLatencyTF.setText("0.1");
        midiLatencyTF.setPreferredSize(new java.awt.Dimension(65, 19));
        midiLatencyPanel.add(midiLatencyTF);

        midiLatencyUnitsLabel.setText("ms");
        midiLatencyPanel.add(midiLatencyUnitsLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        latencyTab.add(midiLatencyPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        latencyTab.add(midiCalibrationPanel, gridBagConstraints);

        jTabbedPane2.addTab("Latency", latencyTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        midiPreferences.add(jTabbedPane2, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/keys.png"))); // NOI18N
        jLabel4.setText("  MIDI Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        midiPreferences.add(jLabel4, gridBagConstraints);

        overrideMeasPMI.setText("Override Measures for this Line");
        overrideMeasPMI.setToolTipText("Enter the number of measure for this line.");
        overrideMeasPMI.setEnabled(false);
        overrideMeasPMI.setMinimumSize(new java.awt.Dimension(200, 100));
        overrideMeasPMI.setSelected(true);
        overrideMeasPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overrideMeasPMIActionPerformed(evt);
            }
        });
        popupMenu.add(overrideMeasPMI);
        popupMenu.add(jSeparator23);

        undoPMI.setText("Undo");
        undoPMI.setEnabled(false);
        popupMenu.add(undoPMI);

        redoPMI.setText("Redo");
        redoPMI.setEnabled(false);
        popupMenu.add(redoPMI);
        popupMenu.add(jSeparator8);

        cutBothPMI.setText("Cut Melody and Chords");
        cutBothPMI.setEnabled(false);
        popupMenu.add(cutBothPMI);

        copyBothPMI.setText("Copy Melody and Chords");
        copyBothPMI.setEnabled(false);
        popupMenu.add(copyBothPMI);

        pasteBothPMI.setText("Paste Melody and Chords");
        pasteBothPMI.setEnabled(false);
        popupMenu.add(pasteBothPMI);
        popupMenu.add(jSeparator9);

        advicePMI.setText("Advice");
        advicePMI.setEnabled(false);
        popupMenu.add(advicePMI);

        adviceFrame.setTitle("Advice Directory");
        adviceFrame.setAlwaysOnTop(true);
        adviceFrame.setFocusCycleRoot(false);
        adviceFrame.setName("adviceFrame"); // NOI18N
        adviceFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                adviceFrameComponentHidden(evt);
            }
        });
        adviceFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                adviceFocusGained(evt);
            }
        });
        adviceFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                adviceWindowClosing(evt);
            }
        });
        adviceFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        adviceTabbedPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceTabbedPaneKeyPressed(evt);
            }
        });

        adviceScroll0.setDoubleBuffered(true);
        adviceScroll0.setMinimumSize(new java.awt.Dimension(100, 100));
        adviceScroll0.setPreferredSize(new java.awt.Dimension(300, 200));
        adviceScroll0.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                adviceFocusGained(evt);
            }
        });

        adviceTree.setMaximumSize(new java.awt.Dimension(400, 800));
        adviceTree.setMinimumSize(new java.awt.Dimension(50, 50));
        adviceTree.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                adviceFocusGained(evt);
            }
        });
        adviceTree.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceTreeKeyPressed(evt);
            }
        });
        adviceTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                adviceTreeMousePressed(evt);
            }
        });
        adviceScroll0.setViewportView(adviceTree);

        adviceTabbedPane.addTab("Notes", adviceScroll0);

        scrollScales.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollScales.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListScales.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListScales.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListScalesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListScalesKeyReleased(evt);
            }
        });
        adviceScrollListScales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListScalesMouseClicked(evt);
            }
        });
        scrollScales.setViewportView(adviceScrollListScales);

        adviceTabbedPane.addTab("Scales", scrollScales);

        scrollCells.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollCells.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListCells.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListCells.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        adviceScrollListCells.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListCellsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListCellsKeyReleased(evt);
            }
        });
        adviceScrollListCells.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListCellsMouseClicked(evt);
            }
        });
        scrollCells.setViewportView(adviceScrollListCells);

        adviceTabbedPane.addTab("Cells", scrollCells);

        scrollIdioms.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollIdioms.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListIdioms.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListIdioms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListIdiomsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListIdiomsKeyReleased(evt);
            }
        });
        adviceScrollListIdioms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListIdiomsMouseClicked(evt);
            }
        });
        scrollIdioms.setViewportView(adviceScrollListIdioms);

        adviceTabbedPane.addTab("Idioms", scrollIdioms);

        scrollLicks.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollLicks.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListLicks.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListLicks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListLicksKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListLicksKeyReleased(evt);
            }
        });
        adviceScrollListLicks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListLicksMouseClicked(evt);
            }
        });
        scrollLicks.setViewportView(adviceScrollListLicks);

        adviceTabbedPane.addTab("Licks", scrollLicks);

        scrollQuotes.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollQuotes.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListQuotes.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListQuotes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListQuotesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListQuotesKeyReleased(evt);
            }
        });
        adviceScrollListQuotes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListQuotesMouseClicked(evt);
            }
        });
        scrollQuotes.setViewportView(adviceScrollListQuotes);

        adviceTabbedPane.addTab("Quotes", scrollQuotes);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        adviceFrame.getContentPane().add(adviceTabbedPane, gridBagConstraints);

        saveLickFrame.setAlwaysOnTop(true);
        saveLickFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        enterLickTitle.setFont(new java.awt.Font("Dialog", 0, 14));
        enterLickTitle.setToolTipText("The name to be given to the selection (need not be unique)");
        enterLickTitle.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                enterLickTitleGetsFocus(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(enterLickTitle, gridBagConstraints);

        lineLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lineLabel1.setText("Name this: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(lineLabel1, gridBagConstraints);

        lickTItleLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lickTItleLabel.setText("Save Selection in Vocabulary");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.ipady = 50;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        saveLickFrame.getContentPane().add(lickTItleLabel, gridBagConstraints);

        cancelLickTitle.setBackground(java.awt.Color.red);
        cancelLickTitle.setText("Cancel");
        cancelLickTitle.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancelLickTitle.setDefaultCapable(false);
        cancelLickTitle.setOpaque(true);
        cancelLickTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelLickTitleActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(cancelLickTitle, gridBagConstraints);

        okSaveButton.setBackground(java.awt.Color.green);
        okSaveButton.setText("Save This");
        okSaveButton.setToolTipText("Saves the item in the vocabulary file.");
        okSaveButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        okSaveButton.setOpaque(true);
        okSaveButton.setSelected(true);
        okSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okSaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        saveLickFrame.getContentPane().add(okSaveButton, gridBagConstraints);

        saveTypeButtonGroup.add(cellRadioButton);
        cellRadioButton.setText("Cell");
        cellRadioButton.setToolTipText("Save as cell when saving.");
        cellRadioButton.setNextFocusableComponent(idiomRadioButton);
        cellRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cellRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(cellRadioButton, gridBagConstraints);

        saveTypeButtonGroup.add(idiomRadioButton);
        idiomRadioButton.setText("Idiom");
        idiomRadioButton.setToolTipText("Save as idiom when saving.");
        idiomRadioButton.setNextFocusableComponent(lickRadioButton);
        idiomRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idiomRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(idiomRadioButton, gridBagConstraints);

        saveTypeButtonGroup.add(lickRadioButton);
        lickRadioButton.setSelected(true);
        lickRadioButton.setText("Lick");
        lickRadioButton.setToolTipText("Save as lick when saving.");
        lickRadioButton.setNextFocusableComponent(quoteRadioButton);
        lickRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lickRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(lickRadioButton, gridBagConstraints);

        saveTypeButtonGroup.add(quoteRadioButton);
        quoteRadioButton.setText("Quote");
        quoteRadioButton.setToolTipText("Save as quote when saving.");
        quoteRadioButton.setNextFocusableComponent(cellRadioButton);
        quoteRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quoteRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(quoteRadioButton, gridBagConstraints);

        duplicateLickDialog.setTitle("");
        duplicateLickDialog.setAlwaysOnTop(true);
        duplicateLickDialog.setModal(true);
        duplicateLickDialog.setName("duplicateLickDialog"); // NOI18N
        duplicateLickDialog.setResizable(false);
        duplicateLickDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                duplicateLickDialogComponentShown(evt);
            }
        });
        duplicateLickDialog.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                duplicateLickDialogKeyPressed(evt);
            }
        });
        duplicateLickDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        ignoreDuplicate.setBackground(java.awt.Color.green);
        ignoreDuplicate.setText("Ignore This One");
        ignoreDuplicate.setToolTipText("Do not save the duplicate in the vocabulary.");
        ignoreDuplicate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        ignoreDuplicate.setOpaque(true);
        ignoreDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ignoreDuplicateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        duplicateLickDialog.getContentPane().add(ignoreDuplicate, gridBagConstraints);

        saveDuplicate.setBackground(java.awt.Color.yellow);
        saveDuplicate.setText("Save This Anyway");
        saveDuplicate.setToolTipText("Saves the lick in the vocabulary");
        saveDuplicate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        saveDuplicate.setDefaultCapable(false);
        saveDuplicate.setOpaque(true);
        saveDuplicate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDuplicateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        duplicateLickDialog.getContentPane().add(saveDuplicate, gridBagConstraints);

        duplicateLickScroll.setMinimumSize(new java.awt.Dimension(200, 100));
        duplicateLickScroll.setPreferredSize(new java.awt.Dimension(500, 300));

        duplicateLickText.setEditable(false);
        duplicateLickScroll.setViewportView(duplicateLickText);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        duplicateLickDialog.getContentPane().add(duplicateLickScroll, gridBagConstraints);

        duplicateLickLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        duplicateLickLabel.setText("Duplicate Lick/Quote Warning");
        duplicateLickLabel.setToolTipText("");
        duplicateLickLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        duplicateLickDialog.getContentPane().add(duplicateLickLabel, gridBagConstraints);

        overwriteLickButton.setBackground(java.awt.Color.red);
        overwriteLickButton.setText("Overwrite Lick");
        overwriteLickButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        overwriteLickButton.setDefaultCapable(false);
        overwriteLickButton.setOpaque(true);
        overwriteLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                overwriteLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        duplicateLickDialog.getContentPane().add(overwriteLickButton, gridBagConstraints);

        mixerDialog.setTitle("Mixer");
        mixerDialog.setAlwaysOnTop(true);
        mixerDialog.setFocusable(false);
        mixerDialog.setFocusableWindowState(false);
        mixerDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        allPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "All", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        allPanel.setOpaque(false);
        allPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        allPanel.setLayout(new java.awt.GridBagLayout());

        allVolumeMixerSlider.setFont(new java.awt.Font("Arial", 0, 8));
        allVolumeMixerSlider.setMajorTickSpacing(20);
        allVolumeMixerSlider.setMaximum(127);
        allVolumeMixerSlider.setMinorTickSpacing(5);
        allVolumeMixerSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        allVolumeMixerSlider.setPaintTicks(true);
        allVolumeMixerSlider.setPreferredSize(new java.awt.Dimension(51, 150));
        allVolumeMixerSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                allVolumeMixerSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        allPanel.add(allVolumeMixerSlider, gridBagConstraints);

        allMuteMixerBtn.setFont(new java.awt.Font("Arial", 0, 10));
        allMuteMixerBtn.setText("Mute All");
        allMuteMixerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allMuteMixerBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        allPanel.add(allMuteMixerBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(allPanel, gridBagConstraints);

        jSeparator28.setForeground(new java.awt.Color(153, 255, 255));
        jSeparator28.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mixerDialog.getContentPane().add(jSeparator28, gridBagConstraints);

        entryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entry", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        entryPanel.setOpaque(false);
        entryPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        entryPanel.setLayout(new java.awt.GridBagLayout());

        entryVolume.setFont(new java.awt.Font("Arial", 0, 8));
        entryVolume.setMajorTickSpacing(20);
        entryVolume.setMaximum(127);
        entryVolume.setMinorTickSpacing(5);
        entryVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        entryVolume.setPaintTicks(true);
        entryVolume.setPreferredSize(new java.awt.Dimension(51, 150));
        entryVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                entryVolumeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        entryPanel.add(entryVolume, gridBagConstraints);

        entryMute.setFont(new java.awt.Font("Arial", 0, 10));
        entryMute.setText("Mute");
        entryMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                entryMuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        entryPanel.add(entryMute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(entryPanel, gridBagConstraints);

        jSeparator29.setForeground(new java.awt.Color(153, 255, 255));
        jSeparator29.setOrientation(javax.swing.SwingConstants.VERTICAL);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        mixerDialog.getContentPane().add(jSeparator29, gridBagConstraints);

        bassPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bass", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        bassPanel.setOpaque(false);
        bassPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        bassPanel.setLayout(new java.awt.GridBagLayout());

        bassVolume.setFont(new java.awt.Font("Arial", 0, 8));
        bassVolume.setMajorTickSpacing(20);
        bassVolume.setMaximum(127);
        bassVolume.setMinorTickSpacing(5);
        bassVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        bassVolume.setPaintTicks(true);
        bassVolume.setPreferredSize(new java.awt.Dimension(51, 150));
        bassVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bassVolumeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        bassPanel.add(bassVolume, gridBagConstraints);

        bassMute.setFont(new java.awt.Font("Arial", 0, 10));
        bassMute.setText("Mute");
        bassMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassMuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        bassPanel.add(bassMute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(bassPanel, gridBagConstraints);

        drumPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Drums", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        drumPanel.setOpaque(false);
        drumPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        drumPanel.setLayout(new java.awt.GridBagLayout());

        drumVolume.setFont(new java.awt.Font("Arial", 0, 8));
        drumVolume.setMajorTickSpacing(20);
        drumVolume.setMaximum(127);
        drumVolume.setMinorTickSpacing(5);
        drumVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        drumVolume.setPaintTicks(true);
        drumVolume.setPreferredSize(new java.awt.Dimension(51, 150));
        drumVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                drumVolumeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        drumPanel.add(drumVolume, gridBagConstraints);

        drumMute.setFont(new java.awt.Font("Arial", 0, 10));
        drumMute.setText("Mute");
        drumMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drumMuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        drumPanel.add(drumMute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(drumPanel, gridBagConstraints);

        chordPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chords", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        chordPanel.setOpaque(false);
        chordPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        chordPanel.setLayout(new java.awt.GridBagLayout());

        chordVolume.setFont(new java.awt.Font("Arial", 0, 8));
        chordVolume.setMajorTickSpacing(20);
        chordVolume.setMaximum(127);
        chordVolume.setMinorTickSpacing(5);
        chordVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        chordVolume.setPaintTicks(true);
        chordVolume.setPreferredSize(new java.awt.Dimension(51, 150));
        chordVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chordVolumeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        chordPanel.add(chordVolume, gridBagConstraints);

        chordMute.setFont(new java.awt.Font("Arial", 0, 10));
        chordMute.setText("Mute");
        chordMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordMuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        chordPanel.add(chordMute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(chordPanel, gridBagConstraints);

        melodyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Melody", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        melodyPanel.setOpaque(false);
        melodyPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        melodyPanel.setLayout(new java.awt.GridBagLayout());

        melodyVolume.setFont(new java.awt.Font("Arial", 0, 8));
        melodyVolume.setMajorTickSpacing(20);
        melodyVolume.setMaximum(127);
        melodyVolume.setMinorTickSpacing(5);
        melodyVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        melodyVolume.setPaintTicks(true);
        melodyVolume.setPreferredSize(new java.awt.Dimension(51, 150));
        melodyVolume.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                melodyVolumeStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        melodyPanel.add(melodyVolume, gridBagConstraints);

        melodyMute.setFont(new java.awt.Font("Arial", 0, 10));
        melodyMute.setText("Mute");
        melodyMute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                melodyMuteActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        melodyPanel.add(melodyMute, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(melodyPanel, gridBagConstraints);

        overrideFrame.setTitle("Override Measures");
        overrideFrame.setAlwaysOnTop(true);
        overrideFrame.setName("overrideFrame"); // NOI18N
        overrideFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                overrideFrameComponentHidden(evt);
            }
        });
        overrideFrame.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                overrideFrameadviceFocusGained(evt);
            }
        });
        overrideFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                overrideFrameadviceWindowClosing(evt);
            }
        });
        overrideFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        enterMeasures.setFont(new java.awt.Font("Dialog", 0, 14));
        enterMeasures.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        enterMeasures.setToolTipText("Set the number of measures for this line.");
        enterMeasures.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterMeasuresActionPerformed(evt);
            }
        });
        enterMeasures.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                enterMeasuresKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        overrideFrame.getContentPane().add(enterMeasures, gridBagConstraints);

        lineLabel.setText("Set number of measures in this line to:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 0.5;
        overrideFrame.getContentPane().add(lineLabel, gridBagConstraints);

        okMeasBtn.setText("OK");
        okMeasBtn.setOpaque(true);
        okMeasBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okMeasBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        overrideFrame.getContentPane().add(okMeasBtn, gridBagConstraints);

        measErrorLabel.setForeground(new java.awt.Color(255, 0, 51));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        overrideFrame.getContentPane().add(measErrorLabel, gridBagConstraints);

        voicingTestFrame.setTitle("Chord Voicing Utility");
        voicingTestFrame.getRootPane().setDefaultButton(buildTableButton);
        voicingTestFrame.setSize(600, 400);
        voicingTestFrame.getContentPane().setLayout(new java.awt.GridLayout(1, 0));

        jPanel2.setMinimumSize(new java.awt.Dimension(760, 700));
        jPanel2.setPreferredSize(new java.awt.Dimension(760, 700));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jPanel1.setLayout(new java.awt.GridBagLayout());

        chordRootLabel.setText("Chord Root: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        jPanel1.add(chordRootLabel, gridBagConstraints);

        chordRootTF.setText("C");
        chordRootTF.setMinimumSize(new java.awt.Dimension(40, 22));
        chordRootTF.setPreferredSize(new java.awt.Dimension(40, 22));
        chordRootTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordRootTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(chordRootTF, gridBagConstraints);

        bassNoteLabel.setText("Bass: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(bassNoteLabel, gridBagConstraints);

        bassNoteTF.setText("C");
        bassNoteTF.setMinimumSize(new java.awt.Dimension(40, 22));
        bassNoteTF.setPreferredSize(new java.awt.Dimension(40, 22));
        bassNoteTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassNoteTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(bassNoteTF, gridBagConstraints);

        voicingRangeLabel.setText("Voicing Range: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        jPanel1.add(voicingRangeLabel, gridBagConstraints);

        lowRangeTF.setText("c-");
        lowRangeTF.setMinimumSize(new java.awt.Dimension(40, 22));
        lowRangeTF.setPreferredSize(new java.awt.Dimension(40, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(lowRangeTF, gridBagConstraints);

        highRangeTF.setText("a");
        highRangeTF.setMinimumSize(new java.awt.Dimension(40, 22));
        highRangeTF.setPreferredSize(new java.awt.Dimension(40, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 10);
        jPanel1.add(highRangeTF, gridBagConstraints);

        rangeToLabel.setText(" to ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel1.add(rangeToLabel, gridBagConstraints);

        rootRangeLabel.setText("Bass Range: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(rootRangeLabel, gridBagConstraints);

        lowRangeTF2.setText("c--");
        lowRangeTF2.setMinimumSize(new java.awt.Dimension(40, 22));
        lowRangeTF2.setPreferredSize(new java.awt.Dimension(40, 22));
        lowRangeTF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lowRangeTF2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(lowRangeTF2, gridBagConstraints);

        rangeToLabel2.setText(" to ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        jPanel1.add(rangeToLabel2, gridBagConstraints);

        highRangeTF2.setText("b--");
        highRangeTF2.setMinimumSize(new java.awt.Dimension(40, 22));
        highRangeTF2.setPreferredSize(new java.awt.Dimension(40, 22));
        highRangeTF2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                highRangeTF2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 10);
        jPanel1.add(highRangeTF2, gridBagConstraints);

        rootEqualBassCheckbox.setSelected(true);
        rootEqualBassCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rootEqualBassCheckboxActionPerformed(evt);
            }
        });
        rootEqualBassCheckbox.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                rootEqualBassCheckboxKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 25);
        jPanel1.add(rootEqualBassCheckbox, gridBagConstraints);

        rootEqualBassLabel.setText("Bass is Root");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(rootEqualBassLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        jPanel2.add(jPanel1, gridBagConstraints);

        voicingScrollPane = new javax.swing.JScrollPane(voicingTable);
        voicingTable.setAutoCreateColumnsFromModel(true);
        voicingTable.createDefaultColumnsFromModel();
        voicingScrollPane.setColumnHeaderView(voicingTable.getTableHeader());
        voicingTable.setPreferredScrollableViewportSize(new Dimension(600,10));
        //voicingTable.getTableHeader().resizeAndRepaint();

        voicingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        voicingTable.setDefaultRenderer(Object.class, voicingRenderer);

        voicingTable.setModel(voicingTableModel);
        voicingTable.setName("Voicing Table"); // NOI18N
        voicingTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voicingTableKeyPressed(evt);
            }
        });
        voicingTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                voicingTableMouseClicked(evt);
            }
        });
        voicingScrollPane.setViewportView(voicingTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(voicingScrollPane, gridBagConstraints);

        voicingSequencePanel.setMinimumSize(new java.awt.Dimension(350, 130));
        voicingSequencePanel.setPreferredSize(new java.awt.Dimension(380, 130));
        voicingSequencePanel.setLayout(new java.awt.GridBagLayout());

        voicingSequenceLabel.setText("Sequence:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
        voicingSequencePanel.add(voicingSequenceLabel, gridBagConstraints);

        jScrollPane2.setMinimumSize(new java.awt.Dimension(250, 75));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(250, 75));

        voicingSequenceList.setModel(voicingSequenceListModel);
        voicingSequenceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        voicingSequenceList.setToolTipText("Create & play a sequence of voicings");
        voicingSequenceList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voicingSequenceListKeyPressed(evt);
            }
        });
        voicingSequenceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                voicingSequenceListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(voicingSequenceList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        voicingSequencePanel.add(jScrollPane2, gridBagConstraints);

        voicingSequenceAddButton.setText("Add");
        voicingSequenceAddButton.setMaximumSize(new java.awt.Dimension(71, 25));
        voicingSequenceAddButton.setMinimumSize(new java.awt.Dimension(71, 25));
        voicingSequenceAddButton.setPreferredSize(new java.awt.Dimension(80, 25));
        voicingSequenceAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingSequenceAddButtonActionPerformed(evt);
            }
        });
        voicingSequenceAddButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voicingSequenceAddButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        voicingSequencePanel.add(voicingSequenceAddButton, gridBagConstraints);

        voicingSequenceRemoveButton.setText("Remove");
        voicingSequenceRemoveButton.setMaximumSize(new java.awt.Dimension(71, 25));
        voicingSequenceRemoveButton.setMinimumSize(new java.awt.Dimension(71, 25));
        voicingSequenceRemoveButton.setPreferredSize(new java.awt.Dimension(80, 25));
        voicingSequenceRemoveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingSequenceRemoveButtonActionPerformed(evt);
            }
        });
        voicingSequenceRemoveButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voicingSequenceRemoveButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        voicingSequencePanel.add(voicingSequenceRemoveButton, gridBagConstraints);

        voicingSequencePlayButton.setText("Play");
        voicingSequencePlayButton.setMaximumSize(new java.awt.Dimension(71, 25));
        voicingSequencePlayButton.setMinimumSize(new java.awt.Dimension(71, 25));
        voicingSequencePlayButton.setPreferredSize(new java.awt.Dimension(80, 25));
        voicingSequencePlayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingSequencePlayButtonActionPerformed(evt);
            }
        });
        voicingSequencePlayButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voicingSequencePlayButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        voicingSequencePanel.add(voicingSequencePlayButton, gridBagConstraints);

        voicingSequenceUpArrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/uparrow.png"))); // NOI18N
        voicingSequenceUpArrow.setToolTipText("Move voicing up in sequence");
        voicingSequenceUpArrow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                voicingSequenceUpArrowMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 10);
        voicingSequencePanel.add(voicingSequenceUpArrow, gridBagConstraints);

        voicingSequenceDownArrow.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/downarrow.png"))); // NOI18N
        voicingSequenceDownArrow.setToolTipText("Move voicing down in sequence");
        voicingSequenceDownArrow.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                voicingSequenceDownArrowMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(20, 0, 0, 10);
        voicingSequencePanel.add(voicingSequenceDownArrow, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        jPanel2.add(voicingSequencePanel, gridBagConstraints);

        chordSearchLabel.setText("Chord Search:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel2.add(chordSearchLabel, gridBagConstraints);

        chordSearchTF.setMinimumSize(new java.awt.Dimension(175, 22));
        chordSearchTF.setPreferredSize(new java.awt.Dimension(175, 22));
        chordSearchTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordSearchTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        jPanel2.add(chordSearchTF, gridBagConstraints);

        jPanel3.setMinimumSize(new java.awt.Dimension(200, 100));
        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 90));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        buildTableButton.setText("Build Table");
        buildTableButton.setMaximumSize(new java.awt.Dimension(250, 25));
        buildTableButton.setMinimumSize(new java.awt.Dimension(250, 25));
        buildTableButton.setPreferredSize(new java.awt.Dimension(250, 25));
        buildTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildTableButtonActionPerformed(evt);
            }
        });
        buildTableButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                buildTableButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 25, 0, 0);
        jPanel3.add(buildTableButton, gridBagConstraints);

        pianoKeyboardButton.setText("Piano Keyboard");
        pianoKeyboardButton.setMaximumSize(new java.awt.Dimension(250, 25));
        pianoKeyboardButton.setMinimumSize(new java.awt.Dimension(250, 25));
        pianoKeyboardButton.setPreferredSize(new java.awt.Dimension(250, 25));
        pianoKeyboardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pianoKeyboardButtonActionPerformed(evt);
            }
        });
        pianoKeyboardButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                pianoKeyboardButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(30, 25, 0, 0);
        jPanel3.add(pianoKeyboardButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.ipady = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel2.add(jPanel3, gridBagConstraints);

        jPanel5.setMinimumSize(new java.awt.Dimension(400, 100));
        jPanel5.setPreferredSize(new java.awt.Dimension(400, 100));
        jPanel5.setLayout(new java.awt.GridBagLayout());

        extEntryTF.setMinimumSize(new java.awt.Dimension(175, 22));
        extEntryTF.setPreferredSize(new java.awt.Dimension(175, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel5.add(extEntryTF, gridBagConstraints);

        extEntryLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        extEntryLabel.setText("Extension: ");
        extEntryLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel5.add(extEntryLabel, gridBagConstraints);

        insertVoicingButton.setText("Insert");
        insertVoicingButton.setToolTipText("Insert voicing into leadsheet");
        insertVoicingButton.setMaximumSize(new java.awt.Dimension(100, 25));
        insertVoicingButton.setMinimumSize(new java.awt.Dimension(100, 25));
        insertVoicingButton.setPreferredSize(new java.awt.Dimension(100, 25));
        insertVoicingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertVoicingButtonActionPerformed(evt);
            }
        });
        insertVoicingButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                insertVoicingButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jPanel5.add(insertVoicingButton, gridBagConstraints);

        playVoicingButton.setText("Play");
        playVoicingButton.setMaximumSize(new java.awt.Dimension(100, 25));
        playVoicingButton.setMinimumSize(new java.awt.Dimension(100, 25));
        playVoicingButton.setPreferredSize(new java.awt.Dimension(100, 25));
        playVoicingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playVoicingButtonActionPerformed(evt);
            }
        });
        playVoicingButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                playVoicingButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        jPanel5.add(playVoicingButton, gridBagConstraints);

        voicingEntryTF.setMinimumSize(new java.awt.Dimension(175, 22));
        voicingEntryTF.setPreferredSize(new java.awt.Dimension(175, 22));
        voicingEntryTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingEntryTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel5.add(voicingEntryTF, gridBagConstraints);

        voicingEntryLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        voicingEntryLabel.setText("Voicing: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel5.add(voicingEntryLabel, gridBagConstraints);

        newVoicingButton.setText("Add New");
        newVoicingButton.setToolTipText("Create a new voicing and add to vocabulary");
        newVoicingButton.setMaximumSize(new java.awt.Dimension(100, 25));
        newVoicingButton.setMinimumSize(new java.awt.Dimension(100, 25));
        newVoicingButton.setPreferredSize(new java.awt.Dimension(100, 25));
        newVoicingButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVoicingButtonActionPerformed(evt);
            }
        });
        newVoicingButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                newVoicingButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        jPanel5.add(newVoicingButton, gridBagConstraints);

        voicingDeleteButton.setText("Delete");
        voicingDeleteButton.setToolTipText("Remove selected voicing");
        voicingDeleteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        voicingDeleteButton.setMaximumSize(new java.awt.Dimension(100, 25));
        voicingDeleteButton.setMinimumSize(new java.awt.Dimension(100, 25));
        voicingDeleteButton.setPreferredSize(new java.awt.Dimension(100, 25));
        voicingDeleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingDeleteButtonActionPerformed(evt);
            }
        });
        voicingDeleteButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                voicingDeleteButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 110, 0, 0);
        jPanel5.add(voicingDeleteButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.ipadx = 5;
        gridBagConstraints.ipady = 5;
        jPanel2.add(jPanel5, gridBagConstraints);

        dummyPanel.setMinimumSize(new java.awt.Dimension(200, 10));
        dummyPanel.setPreferredSize(new java.awt.Dimension(200, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel2.add(dummyPanel, gridBagConstraints);

        voicingTestFrame.getContentPane().add(jPanel2);

        newVoicingDialog.setTitle("Add Chord Voicing");
        newVoicingDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        newVoicingNameLabel.setText("Name:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        newVoicingDialog.getContentPane().add(newVoicingNameLabel, gridBagConstraints);

        newVoicingNameTF.setMinimumSize(new java.awt.Dimension(100, 20));
        newVoicingNameTF.setPreferredSize(new java.awt.Dimension(100, 20));
        newVoicingNameTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVoicingNameTFActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 20);
        newVoicingDialog.getContentPane().add(newVoicingNameTF, gridBagConstraints);

        newVoicingTypeLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        newVoicingTypeLabel.setText("Type:");
        newVoicingTypeLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        newVoicingTypeLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        newVoicingDialog.getContentPane().add(newVoicingTypeLabel, gridBagConstraints);

        newVoicingChordLabel.setText("Chord:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        newVoicingDialog.getContentPane().add(newVoicingChordLabel, gridBagConstraints);

        newVoicingChordTF.setMinimumSize(new java.awt.Dimension(82, 20));
        newVoicingChordTF.setPreferredSize(new java.awt.Dimension(100, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 20);
        newVoicingDialog.getContentPane().add(newVoicingChordTF, gridBagConstraints);

        newVoicingSaveButton.setText("Save Voicing");
        newVoicingSaveButton.setMaximumSize(new java.awt.Dimension(110, 25));
        newVoicingSaveButton.setMinimumSize(new java.awt.Dimension(110, 25));
        newVoicingSaveButton.setOpaque(true);
        newVoicingSaveButton.setPreferredSize(new java.awt.Dimension(110, 25));
        newVoicingSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVoicingSaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 0, 0);
        newVoicingDialog.getContentPane().add(newVoicingSaveButton, gridBagConstraints);

        newVoicingCancelButton.setText("Cancel");
        newVoicingCancelButton.setMaximumSize(new java.awt.Dimension(110, 25));
        newVoicingCancelButton.setMinimumSize(new java.awt.Dimension(110, 25));
        newVoicingCancelButton.setPreferredSize(new java.awt.Dimension(110, 25));
        newVoicingCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVoicingCancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 20);
        newVoicingDialog.getContentPane().add(newVoicingCancelButton, gridBagConstraints);

        newVoicingTypeCB.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "open", "closed", "shout" }));
        newVoicingTypeCB.setPreferredSize(new java.awt.Dimension(100, 20));
        newVoicingTypeCB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newVoicingTypeCBActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 20);
        newVoicingDialog.getContentPane().add(newVoicingTypeCB, gridBagConstraints);

        deleteVoicingDialog.setTitle("Delete");
        deleteVoicingDialog.setResizable(false);
        deleteVoicingDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        deleteVoicingOKButton.setText("OK");
        deleteVoicingOKButton.setMaximumSize(new java.awt.Dimension(75, 25));
        deleteVoicingOKButton.setMinimumSize(new java.awt.Dimension(75, 25));
        deleteVoicingOKButton.setOpaque(true);
        deleteVoicingOKButton.setPreferredSize(new java.awt.Dimension(75, 25));
        deleteVoicingOKButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVoicingOKButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        deleteVoicingDialog.getContentPane().add(deleteVoicingOKButton, gridBagConstraints);

        deleteVoicingCancelButton.setText("Cancel");
        deleteVoicingCancelButton.setMaximumSize(new java.awt.Dimension(75, 25));
        deleteVoicingCancelButton.setMinimumSize(new java.awt.Dimension(75, 25));
        deleteVoicingCancelButton.setPreferredSize(new java.awt.Dimension(75, 25));
        deleteVoicingCancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteVoicingCancelButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 80, 0, 0);
        deleteVoicingDialog.getContentPane().add(deleteVoicingCancelButton, gridBagConstraints);

        deleteVoicingLabel.setText("Delete voicing? This action cannot be undone.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 20, 0);
        deleteVoicingDialog.getContentPane().add(deleteVoicingLabel, gridBagConstraints);

        midiStyleSpec.getContentPane().setLayout(new java.awt.GridBagLayout());

        jPanel10.setBorder(javax.swing.BorderFactory.createTitledBorder("Style Specification"));
        jPanel10.setMinimumSize(new java.awt.Dimension(500, 700));
        jPanel10.setPreferredSize(new java.awt.Dimension(500, 700));
        jPanel10.setLayout(new java.awt.GridBagLayout());

        jTabbedPane1.setMinimumSize(new java.awt.Dimension(475, 675));
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(475, 675));

        bassTabPanel.setMinimumSize(new java.awt.Dimension(470, 670));
        bassTabPanel.setPreferredSize(new java.awt.Dimension(470, 670));
        bassTabPanel.setLayout(new java.awt.GridBagLayout());

        jPanel15.setMinimumSize(new java.awt.Dimension(460, 650));
        jPanel15.setPreferredSize(new java.awt.Dimension(460, 650));
        jPanel15.setLayout(new java.awt.GridBagLayout());

        bassStyleSpecScrollPane.setMinimumSize(new java.awt.Dimension(450, 470));
        bassStyleSpecScrollPane.setPreferredSize(new java.awt.Dimension(450, 470));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        bassStyleSpecScrollPane.setViewportView(jList1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel15.add(bassStyleSpecScrollPane, gridBagConstraints);

        jPanel17.setMinimumSize(new java.awt.Dimension(450, 170));
        jPanel17.setPreferredSize(new java.awt.Dimension(450, 170));
        jPanel17.setLayout(new java.awt.GridBagLayout());

        jPanel18.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel18.setMinimumSize(new java.awt.Dimension(450, 100));
        jPanel18.setPreferredSize(new java.awt.Dimension(450, 120));
        jPanel18.setLayout(new java.awt.GridBagLayout());

        jPanel21.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced"));
        jPanel21.setMinimumSize(new java.awt.Dimension(115, 105));
        jPanel21.setPreferredSize(new java.awt.Dimension(115, 105));
        jPanel21.setLayout(new java.awt.GridBagLayout());

        jButton1.setText("Re-Generate");
        jPanel21.add(jButton1, new java.awt.GridBagConstraints());

        jButton2.setText("Advanced");
        jButton2.setMaximumSize(new java.awt.Dimension(95, 23));
        jButton2.setMinimumSize(new java.awt.Dimension(95, 23));
        jButton2.setPreferredSize(new java.awt.Dimension(95, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel21.add(jButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel18.add(jPanel21, gridBagConstraints);

        jPanel19.setBorder(javax.swing.BorderFactory.createTitledBorder("Bass Settings"));
        jPanel19.setMinimumSize(new java.awt.Dimension(150, 105));
        jPanel19.setPreferredSize(new java.awt.Dimension(150, 105));
        jPanel19.setLayout(new java.awt.GridBagLayout());

        jLabel16.setText("Bass High: ");
        jLabel16.setMaximumSize(new java.awt.Dimension(65, 15));
        jLabel16.setMinimumSize(new java.awt.Dimension(65, 15));
        jLabel16.setPreferredSize(new java.awt.Dimension(65, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel19.add(jLabel16, gridBagConstraints);

        jTextField5.setText("jTextField5");
        jTextField5.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField5.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel19.add(jTextField5, gridBagConstraints);

        jLabel17.setText("Bass Low:");
        jLabel17.setMaximumSize(new java.awt.Dimension(48, 15));
        jLabel17.setMinimumSize(new java.awt.Dimension(48, 15));
        jLabel17.setPreferredSize(new java.awt.Dimension(48, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel19.add(jLabel17, gridBagConstraints);

        jTextField6.setText("jTextField6");
        jTextField6.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField6.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel19.add(jTextField6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel18.add(jPanel19, gridBagConstraints);

        jPanel20.setBorder(javax.swing.BorderFactory.createTitledBorder("Global Settings"));
        jPanel20.setMinimumSize(new java.awt.Dimension(150, 105));
        jPanel20.setPreferredSize(new java.awt.Dimension(150, 105));
        jPanel20.setLayout(new java.awt.GridBagLayout());

        jLabel15.setText("Style Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel20.add(jLabel15, gridBagConstraints);

        jTextField4.setText("jTextField4");
        jTextField4.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField4.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel20.add(jTextField4, gridBagConstraints);

        jLabel14.setText("Swing Value: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel20.add(jLabel14, gridBagConstraints);

        jTextField7.setText("jTextField7");
        jTextField7.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField7.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel20.add(jTextField7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel18.add(jPanel20, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel17.add(jPanel18, gridBagConstraints);

        okcancelPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        okcancelPanel1.setMinimumSize(new java.awt.Dimension(450, 40));
        okcancelPanel1.setPreferredSize(new java.awt.Dimension(450, 40));
        okcancelPanel1.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cancelBtn1.setText("Cancel");
        cancelBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtn1ActionPerformed(evt);
            }
        });
        okcancelPanel1.add(cancelBtn1);

        resetBtn1.setText("Reset");
        resetBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtn1ActionPerformed(evt);
            }
        });
        okcancelPanel1.add(resetBtn1);

        savePrefsBtn1.setText("Save Style");
        savePrefsBtn1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePrefsBtn1ActionPerformed(evt);
            }
        });
        okcancelPanel1.add(savePrefsBtn1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel17.add(okcancelPanel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel15.add(jPanel17, gridBagConstraints);

        bassTabPanel.add(jPanel15, new java.awt.GridBagConstraints());

        jTabbedPane1.addTab("Bass", bassTabPanel);

        drumTabPanel.setMinimumSize(new java.awt.Dimension(470, 670));
        drumTabPanel.setPreferredSize(new java.awt.Dimension(470, 670));
        drumTabPanel.setLayout(new java.awt.GridBagLayout());

        jPanel22.setMinimumSize(new java.awt.Dimension(460, 650));
        jPanel22.setPreferredSize(new java.awt.Dimension(460, 650));
        jPanel22.setLayout(new java.awt.GridBagLayout());

        jScrollPane3.setMinimumSize(new java.awt.Dimension(450, 470));
        jScrollPane3.setPreferredSize(new java.awt.Dimension(450, 470));

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel22.add(jScrollPane3, gridBagConstraints);

        jPanel23.setMinimumSize(new java.awt.Dimension(450, 170));
        jPanel23.setPreferredSize(new java.awt.Dimension(450, 170));
        jPanel23.setLayout(new java.awt.GridBagLayout());

        jPanel24.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel24.setMinimumSize(new java.awt.Dimension(450, 100));
        jPanel24.setPreferredSize(new java.awt.Dimension(450, 120));
        jPanel24.setLayout(new java.awt.GridBagLayout());

        jPanel25.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced"));
        jPanel25.setMinimumSize(new java.awt.Dimension(115, 105));
        jPanel25.setPreferredSize(new java.awt.Dimension(115, 105));
        jPanel25.setLayout(new java.awt.GridBagLayout());

        jButton3.setText("Re-Generate");
        jPanel25.add(jButton3, new java.awt.GridBagConstraints());

        jButton4.setText("Advanced");
        jButton4.setMaximumSize(new java.awt.Dimension(95, 23));
        jButton4.setMinimumSize(new java.awt.Dimension(95, 23));
        jButton4.setPreferredSize(new java.awt.Dimension(95, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel25.add(jButton4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel24.add(jPanel25, gridBagConstraints);

        jPanel26.setBorder(javax.swing.BorderFactory.createTitledBorder("Drum Settings"));
        jPanel26.setMinimumSize(new java.awt.Dimension(150, 105));
        jPanel26.setPreferredSize(new java.awt.Dimension(150, 105));
        jPanel26.setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel24.add(jPanel26, gridBagConstraints);

        jPanel27.setBorder(javax.swing.BorderFactory.createTitledBorder("Global Settings"));
        jPanel27.setMinimumSize(new java.awt.Dimension(150, 105));
        jPanel27.setPreferredSize(new java.awt.Dimension(150, 105));
        jPanel27.setLayout(new java.awt.GridBagLayout());

        jLabel21.setText("Style Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel27.add(jLabel21, gridBagConstraints);

        jTextField10.setText("jTextField4");
        jTextField10.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField10.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel27.add(jTextField10, gridBagConstraints);

        jLabel22.setText("Swing Value: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel27.add(jLabel22, gridBagConstraints);

        jTextField11.setText("jTextField7");
        jTextField11.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField11.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel27.add(jTextField11, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel24.add(jPanel27, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel23.add(jPanel24, gridBagConstraints);

        okcancelPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        okcancelPanel2.setMinimumSize(new java.awt.Dimension(450, 40));
        okcancelPanel2.setPreferredSize(new java.awt.Dimension(450, 40));
        okcancelPanel2.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cancelBtn2.setText("Cancel");
        cancelBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtn2ActionPerformed(evt);
            }
        });
        okcancelPanel2.add(cancelBtn2);

        resetBtn2.setText("Reset");
        resetBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtn2ActionPerformed(evt);
            }
        });
        okcancelPanel2.add(resetBtn2);

        savePrefsBtn2.setText("Save Style");
        savePrefsBtn2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePrefsBtn2ActionPerformed(evt);
            }
        });
        okcancelPanel2.add(savePrefsBtn2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel23.add(okcancelPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel22.add(jPanel23, gridBagConstraints);

        drumTabPanel.add(jPanel22, new java.awt.GridBagConstraints());

        jTabbedPane1.addTab("Drum", drumTabPanel);

        chordTabPanel.setMinimumSize(new java.awt.Dimension(470, 670));
        chordTabPanel.setPreferredSize(new java.awt.Dimension(470, 670));
        chordTabPanel.setLayout(new java.awt.GridBagLayout());

        jPanel28.setMinimumSize(new java.awt.Dimension(460, 650));
        jPanel28.setPreferredSize(new java.awt.Dimension(460, 650));
        jPanel28.setLayout(new java.awt.GridBagLayout());

        jScrollPane4.setMinimumSize(new java.awt.Dimension(450, 470));
        jScrollPane4.setPreferredSize(new java.awt.Dimension(450, 470));

        jList4.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane4.setViewportView(jList4);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel28.add(jScrollPane4, gridBagConstraints);

        jPanel29.setMinimumSize(new java.awt.Dimension(450, 170));
        jPanel29.setPreferredSize(new java.awt.Dimension(450, 170));
        jPanel29.setLayout(new java.awt.GridBagLayout());

        jPanel30.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jPanel30.setMinimumSize(new java.awt.Dimension(450, 100));
        jPanel30.setPreferredSize(new java.awt.Dimension(450, 120));
        jPanel30.setLayout(new java.awt.GridBagLayout());

        jPanel31.setBorder(javax.swing.BorderFactory.createTitledBorder("Advanced"));
        jPanel31.setMinimumSize(new java.awt.Dimension(115, 105));
        jPanel31.setPreferredSize(new java.awt.Dimension(115, 105));
        jPanel31.setLayout(new java.awt.GridBagLayout());

        jButton5.setText("Re-Generate");
        jPanel31.add(jButton5, new java.awt.GridBagConstraints());

        jButton6.setText("Advanced");
        jButton6.setMaximumSize(new java.awt.Dimension(95, 23));
        jButton6.setMinimumSize(new java.awt.Dimension(95, 23));
        jButton6.setPreferredSize(new java.awt.Dimension(95, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel31.add(jButton6, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel30.add(jPanel31, gridBagConstraints);

        jPanel32.setBorder(javax.swing.BorderFactory.createTitledBorder("Chordal Settings"));
        jPanel32.setMinimumSize(new java.awt.Dimension(150, 105));
        jPanel32.setPreferredSize(new java.awt.Dimension(150, 105));
        jPanel32.setLayout(new java.awt.GridBagLayout());

        jLabel18.setText("Chord High: ");
        jLabel18.setMaximumSize(new java.awt.Dimension(53, 15));
        jLabel18.setMinimumSize(new java.awt.Dimension(53, 15));
        jLabel18.setPreferredSize(new java.awt.Dimension(53, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel32.add(jLabel18, gridBagConstraints);

        jTextField8.setText("jTextField5");
        jTextField8.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField8.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel32.add(jTextField8, gridBagConstraints);

        jLabel20.setText("Chord Low:");
        jLabel20.setMaximumSize(new java.awt.Dimension(48, 15));
        jLabel20.setMinimumSize(new java.awt.Dimension(48, 15));
        jLabel20.setPreferredSize(new java.awt.Dimension(48, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel32.add(jLabel20, gridBagConstraints);

        jTextField9.setText("jTextField6");
        jTextField9.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField9.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel32.add(jTextField9, gridBagConstraints);

        jLabel25.setText("Chord Voicing:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel32.add(jLabel25, gridBagConstraints);

        jRadioButton1.setText("Open");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel32.add(jRadioButton1, gridBagConstraints);

        jRadioButton2.setText("Closed");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel32.add(jRadioButton2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel30.add(jPanel32, gridBagConstraints);

        jPanel33.setBorder(javax.swing.BorderFactory.createTitledBorder("Global Settings"));
        jPanel33.setMinimumSize(new java.awt.Dimension(150, 105));
        jPanel33.setPreferredSize(new java.awt.Dimension(150, 105));
        jPanel33.setLayout(new java.awt.GridBagLayout());

        jLabel23.setText("Style Name: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel33.add(jLabel23, gridBagConstraints);

        jTextField12.setText("jTextField4");
        jTextField12.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField12.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        jPanel33.add(jTextField12, gridBagConstraints);

        jLabel24.setText("Swing Value: ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel33.add(jLabel24, gridBagConstraints);

        jTextField13.setText("jTextField7");
        jTextField13.setMinimumSize(new java.awt.Dimension(60, 20));
        jTextField13.setPreferredSize(new java.awt.Dimension(60, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel33.add(jTextField13, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel30.add(jPanel33, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel29.add(jPanel30, gridBagConstraints);

        okcancelPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        okcancelPanel3.setMinimumSize(new java.awt.Dimension(450, 40));
        okcancelPanel3.setPreferredSize(new java.awt.Dimension(450, 40));
        okcancelPanel3.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cancelBtn3.setText("Cancel");
        cancelBtn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtn3ActionPerformed(evt);
            }
        });
        okcancelPanel3.add(cancelBtn3);

        resetBtn3.setText("Reset");
        resetBtn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetBtn3ActionPerformed(evt);
            }
        });
        okcancelPanel3.add(resetBtn3);

        savePrefsBtn3.setText("Save Style");
        savePrefsBtn3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePrefsBtn3ActionPerformed(evt);
            }
        });
        okcancelPanel3.add(savePrefsBtn3);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTHEAST;
        jPanel29.add(okcancelPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        jPanel28.add(jPanel29, gridBagConstraints);

        chordTabPanel.add(jPanel28, new java.awt.GridBagConstraints());

        jTabbedPane1.addTab("Chordal", chordTabPanel);

        jPanel10.add(jTabbedPane1, new java.awt.GridBagConstraints());

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        midiStyleSpec.getContentPane().add(jPanel10, gridBagConstraints);

        truncatePartDialog.setTitle("");
        truncatePartDialog.setAlwaysOnTop(true);
        truncatePartDialog.setModal(true);
        truncatePartDialog.setName("duplicateLickDialog"); // NOI18N
        truncatePartDialog.setResizable(false);
        truncatePartDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                truncatePartDialogComponentShown(evt);
            }
        });
        truncatePartDialog.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                truncatePartDialogKeyPressed(evt);
            }
        });
        truncatePartDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        cancelTruncate.setBackground(java.awt.Color.green);
        cancelTruncate.setText("Cancel truncation");
        cancelTruncate.setToolTipText("Do not truncate the part.");
        cancelTruncate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cancelTruncate.setOpaque(true);
        cancelTruncate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelTruncateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        truncatePartDialog.getContentPane().add(cancelTruncate, gridBagConstraints);

        acceptTruncate.setBackground(java.awt.Color.red);
        acceptTruncate.setText("Truncate Anyway");
        acceptTruncate.setToolTipText("Truncates the part as specified.");
        acceptTruncate.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        acceptTruncate.setDefaultCapable(false);
        acceptTruncate.setOpaque(true);
        acceptTruncate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                acceptTruncateActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        truncatePartDialog.getContentPane().add(acceptTruncate, gridBagConstraints);

        truncatePartLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        truncatePartLabel.setText("<html>\n<h2>\nTruncation Warning!\n</h2>\n\n<h3>\nYou are about to truncate the chorus,<br>\nan operation that can't be undone.<br><br>\nYou may lose chords or notes.\n</h3>\n</html>");
        truncatePartLabel.setToolTipText("");
        truncatePartLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 15;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        truncatePartDialog.getContentPane().add(truncatePartLabel, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage((new ImageIcon(getClass().getResource("/imp/gui/graphics/icons/trumpetsmall.png"))).getImage());
        setName("notateFrame"); // NOI18N
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                formMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                formMouseEntered(evt);
            }
        });
        addWindowFocusListener(new java.awt.event.WindowFocusListener() {
            public void windowGainedFocus(java.awt.event.WindowEvent evt) {
                formWindowGainedFocus(evt);
            }
            public void windowLostFocus(java.awt.event.WindowEvent evt) {
                formWindowLostFocus(evt);
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        toolbarPanel.setOpaque(false);
        toolbarPanel.setPreferredSize(new java.awt.Dimension(3002, 130));
        toolbarPanel.setLayout(new java.awt.GridBagLayout());

        standardToolbar.setFloatable(false);
        standardToolbar.setEnabled(false);
        standardToolbar.setMaximumSize(new java.awt.Dimension(400, 45));
        standardToolbar.setMinimumSize(new java.awt.Dimension(130, 45));
        standardToolbar.setPreferredSize(new java.awt.Dimension(130, 45));
        standardToolbar.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                standardToolbarComponentMoved(evt);
            }
        });

        newBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/new.gif"))); // NOI18N
        newBtn.setToolTipText("Start a new leadsheet, in addition to the current one (Ctrl+N).");
        newBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        newBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        newBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        newBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(newBtn);

        openBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/open.gif"))); // NOI18N
        openBtn.setToolTipText("Open a leadsheet in place of the current one (Ctrl+O).");
        openBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        openBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        openBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        openBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(openBtn);

        saveBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/save.gif"))); // NOI18N
        saveBtn.setToolTipText("Save the current Leadsheet (Ctrl+S).");
        saveBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        saveBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        saveBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        saveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(saveBtn);

        printBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/print.gif"))); // NOI18N
        printBtn.setToolTipText("Print the current chorus (Ctrl+P).");
        printBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        printBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        printBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        printBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        standardToolbar.add(printBtn);

        cutBothBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/cut.gif"))); // NOI18N
        cutBothBtn.setToolTipText("Cut the currently selected melody and chords (Ctrl+X).");
        cutBothBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cutBothBtn.setEnabled(false);
        cutBothBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        cutBothBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        cutBothBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        standardToolbar.add(cutBothBtn);

        copyBothBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/copy.gif"))); // NOI18N
        copyBothBtn.setToolTipText("Copy the currently selected melody and chords (Ctrl+C).");
        copyBothBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        copyBothBtn.setEnabled(false);
        copyBothBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        copyBothBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        copyBothBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        standardToolbar.add(copyBothBtn);

        pasteBothBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/paste.gif"))); // NOI18N
        pasteBothBtn.setToolTipText("Paste melody and chords from the clipboard (Ctrl-V).");
        pasteBothBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pasteBothBtn.setEnabled(false);
        pasteBothBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        pasteBothBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        pasteBothBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        standardToolbar.add(pasteBothBtn);

        undoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/undo.gif"))); // NOI18N
        undoBtn.setToolTipText("Undo the previous action (Ctrl+Z)");
        undoBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        undoBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        undoBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        undoBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        standardToolbar.add(undoBtn);

        redoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/redo.gif"))); // NOI18N
        redoBtn.setToolTipText("Redo the previous action (Ctrl+Y)");
        redoBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        redoBtn.setEnabled(false);
        redoBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        redoBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        redoBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        standardToolbar.add(redoBtn);

        drawButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pencil.gif"))); // NOI18N
        drawButton.setToolTipText("Toggle drawing mode.");
        drawButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        drawButton.setMaximumSize(new java.awt.Dimension(30, 30));
        drawButton.setMinimumSize(new java.awt.Dimension(30, 30));
        drawButton.setPreferredSize(new java.awt.Dimension(30, 30));
        drawButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(drawButton);

        showAdviceButton.setBackground(adviceBtnColorClosed);
        showAdviceButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/advice.gif"))); // NOI18N
        showAdviceButton.setToolTipText("Show advice for chords.");
        showAdviceButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        showAdviceButton.setMaximumSize(new java.awt.Dimension(30, 30));
        showAdviceButton.setMinimumSize(new java.awt.Dimension(30, 30));
        showAdviceButton.setOpaque(true);
        showAdviceButton.setPreferredSize(new java.awt.Dimension(30, 30));
        showAdviceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAdviceButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(showAdviceButton);

        openGeneratorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/triage.gif"))); // NOI18N
        openGeneratorButton.setToolTipText("Open the lick generator dialog.");
        openGeneratorButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openGeneratorButton.setMaximumSize(new java.awt.Dimension(30, 30));
        openGeneratorButton.setMinimumSize(new java.awt.Dimension(30, 30));
        openGeneratorButton.setOpaque(true);
        openGeneratorButton.setPreferredSize(new java.awt.Dimension(30, 30));
        openGeneratorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openGeneratorButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(openGeneratorButton);

        generateToolbarBtn.setBackground(new java.awt.Color(255, 204, 0));
        generateToolbarBtn.setFont(new java.awt.Font("Arial", 0, 11));
        generateToolbarBtn.setText("Generate");
        generateToolbarBtn.setToolTipText("Generate melody over selected chords.");
        generateToolbarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        generateToolbarBtn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        generateToolbarBtn.setMaximumSize(new java.awt.Dimension(55, 30));
        generateToolbarBtn.setMinimumSize(new java.awt.Dimension(55, 30));
        generateToolbarBtn.setOpaque(true);
        generateToolbarBtn.setPreferredSize(new java.awt.Dimension(55, 30));
        generateToolbarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateToolbarBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(generateToolbarBtn);

        freezeLayoutButton.setBackground(new java.awt.Color(0, 255, 0));
        freezeLayoutButton.setFont(new java.awt.Font("Arial", 0, 11));
        freezeLayoutButton.setText("<html><center>Freeze</center></html>");
        freezeLayoutButton.setToolTipText("Freeze or thaw the current layout");
        freezeLayoutButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        freezeLayoutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        freezeLayoutButton.setMaximumSize(new java.awt.Dimension(45, 30));
        freezeLayoutButton.setMinimumSize(new java.awt.Dimension(45, 30));
        freezeLayoutButton.setOpaque(true);
        freezeLayoutButton.setPreferredSize(new java.awt.Dimension(45, 30));
        freezeLayoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freezeLayoutButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(freezeLayoutButton);

        colorationButton.setBackground(new java.awt.Color(153, 204, 255));
        colorationButton.setFont(new java.awt.Font("Arial", 0, 11));
        colorationButton.setText("<html><center>B/W</center></html>");
        colorationButton.setToolTipText("Turn note coloration off or on.");
        colorationButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        colorationButton.setFocusable(false);
        colorationButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        colorationButton.setMaximumSize(new java.awt.Dimension(40, 30));
        colorationButton.setMinimumSize(new java.awt.Dimension(40, 30));
        colorationButton.setOpaque(true);
        colorationButton.setPreferredSize(new java.awt.Dimension(40, 30));
        colorationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                colorationButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(colorationButton);

        smartEntryButton.setBackground(new java.awt.Color(255, 153, 255));
        smartEntryButton.setFont(new java.awt.Font("Arial", 0, 11));
        smartEntryButton.setSelected(true);
        smartEntryButton.setText("<html><center>Simple</center></html>");
        smartEntryButton.setToolTipText("Use simple or harmonic note entry (the latter observing chords).");
        smartEntryButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        smartEntryButton.setFocusable(false);
        smartEntryButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        smartEntryButton.setMaximumSize(new java.awt.Dimension(55, 30));
        smartEntryButton.setMinimumSize(new java.awt.Dimension(55, 30));
        smartEntryButton.setOpaque(true);
        smartEntryButton.setPreferredSize(new java.awt.Dimension(55, 30));
        smartEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smartEntryButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(smartEntryButton);

        beamButton.setBackground(new java.awt.Color(51, 255, 255));
        beamButton.setFont(new java.awt.Font("Arial", 0, 11));
        beamButton.setSelected(true);
        beamButton.setText("<html><center>No Beam</center></html>");
        beamButton.setToolTipText("Beam multiple notes shorter than quarter-note.");
        beamButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        beamButton.setFocusable(false);
        beamButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        beamButton.setMaximumSize(new java.awt.Dimension(55, 30));
        beamButton.setMinimumSize(new java.awt.Dimension(55, 30));
        beamButton.setOpaque(true);
        beamButton.setPreferredSize(new java.awt.Dimension(55, 30));
        beamButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                beamButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(beamButton);

        chordFontSizeSpinner.setToolTipText("Specifies the chord font size.");
        chordFontSizeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chord Font", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        chordFontSizeSpinner.setInheritsPopupMenu(true);
        chordFontSizeSpinner.setMaximumSize(new java.awt.Dimension(75, 45));
        chordFontSizeSpinner.setMinimumSize(new java.awt.Dimension(75, 45));
        chordFontSizeSpinner.setPreferredSize(new java.awt.Dimension(75, 45));
        chordFontSizeSpinner.setValue(16);
        chordFontSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chordFontStateChanged(evt);
            }
        });
        standardToolbar.add(chordFontSizeSpinner);

        addTabBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/addtab.gif"))); // NOI18N
        addTabBtn.setToolTipText("Add a new chorus tab.");
        addTabBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addTabBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        addTabBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        addTabBtn.setOpaque(true);
        addTabBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        addTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTabBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(addTabBtn);

        delTabBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/deltab.gif"))); // NOI18N
        delTabBtn.setToolTipText("Delete the current  chorus tab (can't be undone).");
        delTabBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        delTabBtn.setEnabled(false);
        delTabBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        delTabBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        delTabBtn.setOpaque(true);
        delTabBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        delTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delTabBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(delTabBtn);

        preferencesBtn.setBackground(new java.awt.Color(51, 51, 255));
        preferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/globalprefs.gif"))); // NOI18N
        preferencesBtn.setToolTipText("Open Preferences dialog.");
        preferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        preferencesBtn.setFocusable(false);
        preferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        preferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        preferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        preferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        preferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        preferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(preferencesBtn);

        programStatusTF.setBackground(new java.awt.Color(238, 238, 238));
        programStatusTF.setEditable(false);
        programStatusTF.setFont(new java.awt.Font("Dialog", 1, 10));
        programStatusTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        programStatusTF.setText("Starting ...");
        programStatusTF.setToolTipText("Tells what the program is doing.");
        programStatusTF.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Program Status", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        programStatusTF.setMargin(new java.awt.Insets(0, 5, 2, 4));
        programStatusTF.setMaximumSize(new java.awt.Dimension(400, 50));
        programStatusTF.setMinimumSize(new java.awt.Dimension(250, 30));
        programStatusTF.setPreferredSize(new java.awt.Dimension(250, 30));
        programStatusTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                programStatusTFActionPerformed(evt);
            }
        });
        programStatusTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                programStatusTFFocusLost(evt);
            }
        });
        standardToolbar.add(programStatusTF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        toolbarPanel.add(standardToolbar, gridBagConstraints);

        playToolBar.setFloatable(false);
        playToolBar.setMaximumSize(new java.awt.Dimension(985, 47));
        playToolBar.setMinimumSize(new java.awt.Dimension(985, 47));
        playToolBar.setPreferredSize(new java.awt.Dimension(985, 47));
        playToolBar.setRequestFocusEnabled(false);
        playToolBar.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentMoved(java.awt.event.ComponentEvent evt) {
                playToolBarComponentMoved(evt);
            }
        });
        playToolBar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                playToolBarKeyPressed(evt);
            }
        });

        countInPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Count", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.ABOVE_TOP, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        countInPanel.setToolTipText("Check to count in two measures before tune is played.");
        countInPanel.setMaximumSize(new java.awt.Dimension(40, 42));
        countInPanel.setMinimumSize(new java.awt.Dimension(40, 42));
        countInPanel.setPreferredSize(new java.awt.Dimension(40, 42));
        countInPanel.setLayout(new java.awt.GridBagLayout());

        countInCheckBox.setToolTipText("Check to count in two measures before tune is played.");
        countInCheckBox.setMaximumSize(new java.awt.Dimension(28, 25));
        countInCheckBox.setMinimumSize(new java.awt.Dimension(28, 25));
        countInCheckBox.setPreferredSize(new java.awt.Dimension(28, 25));
        countInCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countInCheckBoxActionPerformed(evt);
            }
        });
        countInPanel.add(countInCheckBox, new java.awt.GridBagConstraints());

        playToolBar.add(countInPanel);

        playBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
        playBtn.setToolTipText("Play the entire leadsheet.");
        playBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        playBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        playBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        playBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playBtnActionPerformed(evt);
            }
        });
        playToolBar.add(playBtn);

        pauseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pause.gif"))); // NOI18N
        pauseBtn.setToolTipText("Pause or resume playback.");
        pauseBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pauseBtn.setEnabled(false);
        pauseBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        pauseBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        pauseBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        pauseBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseBtnActionPerformed(evt);
            }
        });
        playToolBar.add(pauseBtn);

        stopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/stop.gif"))); // NOI18N
        stopBtn.setToolTipText("Stop playback.");
        stopBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stopBtn.setEnabled(false);
        stopBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        stopBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        stopBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopBtnActionPerformed(evt);
            }
        });
        playToolBar.add(stopBtn);

        recordBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/record.gif"))); // NOI18N
        recordBtn.setToolTipText("Record from MIDI source.");
        recordBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        recordBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        recordBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        recordBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        recordBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordBtnActionPerformed(evt);
            }
        });
        playToolBar.add(recordBtn);

        stepInputBtn.setFont(new java.awt.Font("Dialog", 0, 10));
        stepInputBtn.setText("Step");
        stepInputBtn.setToolTipText("Step record from MIDI source.");
        stepInputBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stepInputBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        stepInputBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        stepInputBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        stepInputBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepInputBtnActionPerformed(evt);
            }
        });
        playToolBar.add(stepInputBtn);

        mixerBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/mixer.gif"))); // NOI18N
        mixerBtn.setToolTipText("Open Volume Mixer");
        mixerBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mixerBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        mixerBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        mixerBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        mixerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mixerBtnActionPerformed(evt);
            }
        });
        playToolBar.add(mixerBtn);

        playbackPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Playback Location", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        playbackPanel.setMaximumSize(new java.awt.Dimension(180, 50));
        playbackPanel.setMinimumSize(new java.awt.Dimension(120, 50));
        playbackPanel.setOpaque(false);
        playbackPanel.setPreferredSize(new java.awt.Dimension(180, 30));
        playbackPanel.setLayout(new java.awt.GridBagLayout());

        playbackTime.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        playbackTime.setText("0:00");
        playbackTime.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        playbackTime.setFocusable(false);
        playbackTime.setMaximumSize(new java.awt.Dimension(40, 22));
        playbackTime.setMinimumSize(new java.awt.Dimension(30, 22));
        playbackTime.setPreferredSize(new java.awt.Dimension(30, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(13, 0, 8, 0);
        playbackPanel.add(playbackTime, gridBagConstraints);

        playbackTotalTime.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        playbackTotalTime.setText("0:00");
        playbackTotalTime.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        playbackTotalTime.setFocusable(false);
        playbackTotalTime.setMaximumSize(new java.awt.Dimension(40, 22));
        playbackTotalTime.setMinimumSize(new java.awt.Dimension(30, 22));
        playbackTotalTime.setPreferredSize(new java.awt.Dimension(30, 22));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 12;
        gridBagConstraints.ipady = -2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.insets = new java.awt.Insets(12, 2, 0, 0);
        playbackPanel.add(playbackTotalTime, gridBagConstraints);

        playbackSlider.setValue(0);
        playbackSlider.setMinimumSize(new java.awt.Dimension(100, 20));
        playbackSlider.setPreferredSize(new java.awt.Dimension(140, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(12, 0, 8, 0);
        playbackPanel.add(playbackSlider, gridBagConstraints);

        playToolBar.add(playbackPanel);

        loopPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Looping", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        loopPanel.setMaximumSize(new java.awt.Dimension(90, 50));
        loopPanel.setMinimumSize(new java.awt.Dimension(90, 50));
        loopPanel.setOpaque(false);
        loopPanel.setPreferredSize(new java.awt.Dimension(90, 50));
        loopPanel.setLayout(new java.awt.GridBagLayout());

        loopButton.setBackground(new java.awt.Color(0, 255, 0));
        loopButton.setFont(new java.awt.Font("Dialog", 1, 10));
        loopButton.setText("<html><center>Loop</center></html>");
        loopButton.setToolTipText("Toggle playback looping.");
        loopButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loopButton.setMaximumSize(new java.awt.Dimension(30, 20));
        loopButton.setMinimumSize(new java.awt.Dimension(30, 20));
        loopButton.setOpaque(true);
        loopButton.setPreferredSize(new java.awt.Dimension(30, 20));
        loopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopButtonActionPerformed(evt);
            }
        });
        loopButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                loopButtonKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 5);
        loopPanel.add(loopButton, gridBagConstraints);

        loopSet.setFont(new java.awt.Font("Dialog", 1, 12));
        loopSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        loopSet.setText("2");
        loopSet.setToolTipText("Loop  on playback specified number of times (0 means loop forever; press k or stop button to stop).");
        loopSet.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        loopSet.setMaximumSize(new java.awt.Dimension(30, 20));
        loopSet.setMinimumSize(new java.awt.Dimension(24, 20));
        loopSet.setPreferredSize(new java.awt.Dimension(24, 20));
        loopSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopSetActionPerformed(evt);
            }
        });
        loopSet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                loopSetFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                loopSetFocusLost(evt);
            }
        });
        loopSet.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                loopSetKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                loopSetKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                loopSetKeyReleased(evt);
            }
        });
        loopSet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                loopSetMousePressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        loopPanel.add(loopSet, gridBagConstraints);

        playToolBar.add(loopPanel);

        masterVolumePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        masterVolumePanel.setMaximumSize(new java.awt.Dimension(130, 50));
        masterVolumePanel.setMinimumSize(new java.awt.Dimension(100, 50));
        masterVolumePanel.setOpaque(false);
        masterVolumePanel.setPreferredSize(new java.awt.Dimension(120, 50));
        masterVolumePanel.setLayout(new java.awt.GridBagLayout());

        allMuteToolBarBtn.setBackground(new java.awt.Color(0, 255, 0));
        allMuteToolBarBtn.setFont(new java.awt.Font("Dialog", 1, 10));
        allMuteToolBarBtn.setText("<html><center>Mute</center></html>");
        allMuteToolBarBtn.setToolTipText("Play or not play notes as they are inserted?");
        allMuteToolBarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        allMuteToolBarBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        allMuteToolBarBtn.setMaximumSize(new java.awt.Dimension(30, 20));
        allMuteToolBarBtn.setMinimumSize(new java.awt.Dimension(32, 20));
        allMuteToolBarBtn.setOpaque(true);
        allMuteToolBarBtn.setPreferredSize(new java.awt.Dimension(32, 20));
        allMuteToolBarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allMuteToolBarBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        masterVolumePanel.add(allMuteToolBarBtn, gridBagConstraints);

        allVolumeToolBarSlider.setMajorTickSpacing(5);
        allVolumeToolBarSlider.setMaximum(127);
        allVolumeToolBarSlider.setToolTipText("Set the volume for sounds on entry.");
        allVolumeToolBarSlider.setMaximumSize(new java.awt.Dimension(120, 20));
        allVolumeToolBarSlider.setMinimumSize(new java.awt.Dimension(80, 20));
        allVolumeToolBarSlider.setPreferredSize(new java.awt.Dimension(90, 20));
        allVolumeToolBarSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                allVolumeToolBarSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 0.8;
        masterVolumePanel.add(allVolumeToolBarSlider, gridBagConstraints);

        playToolBar.add(masterVolumePanel);

        tempoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tempo (Beats per Minute)\n", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        tempoPanel.setMaximumSize(new java.awt.Dimension(160, 50));
        tempoPanel.setMinimumSize(new java.awt.Dimension(160, 50));
        tempoPanel.setOpaque(false);
        tempoPanel.setPreferredSize(new java.awt.Dimension(160, 50));
        tempoPanel.setLayout(new java.awt.GridBagLayout());

        tempoSet.setFont(new java.awt.Font("Dialog", 1, 12));
        tempoSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tempoSet.setToolTipText("Set the tempo for the sheet in beats per minute.");
        tempoSet.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tempoSet.setMaximumSize(new java.awt.Dimension(40, 20));
        tempoSet.setMinimumSize(new java.awt.Dimension(40, 20));
        tempoSet.setPreferredSize(new java.awt.Dimension(40, 20));
        tempoSet.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tempoSetMousePressed(evt);
            }
        });
        tempoSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tempoSetActionPerformed(evt);
            }
        });
        tempoSet.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                tempoSetFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                tempoSetFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 0);
        tempoPanel.add(tempoSet, gridBagConstraints);

        tempoSlider.setMaximum(300);
        tempoSlider.setMinimum(30);
        tempoSlider.setMinorTickSpacing(4);
        tempoSlider.setValue(160);
        tempoSlider.setMaximumSize(new java.awt.Dimension(120, 30));
        tempoSlider.setMinimumSize(new java.awt.Dimension(36, 20));
        tempoSlider.setPreferredSize(new java.awt.Dimension(100, 20));
        tempoSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                tempoSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 60;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 6, 5);
        tempoPanel.add(tempoSlider, gridBagConstraints);

        playToolBar.add(tempoPanel);

        transposeSpinner.setToolTipText("Transposes the playback the specified number of half steps (e.g. use -2 for Bb instruments, +3 for Eb).");
        transposeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transpose", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
        transposeSpinner.setMaximumSize(new java.awt.Dimension(70, 45));
        transposeSpinner.setMinimumSize(new java.awt.Dimension(70, 45));
        transposeSpinner.setPreferredSize(new java.awt.Dimension(70, 45));
        transposeSpinner.setValue(0);
        transposeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                transposeSpinnerStateChanged(evt);
            }
        });
        playToolBar.add(transposeSpinner);

        partBarsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bars/Chorus", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        partBarsPanel.setToolTipText("Set the number of measures in one chorus.");
        partBarsPanel.setMaximumSize(new java.awt.Dimension(70, 50));
        partBarsPanel.setMinimumSize(new java.awt.Dimension(70, 50));
        partBarsPanel.setOpaque(false);
        partBarsPanel.setPreferredSize(new java.awt.Dimension(70, 50));
        partBarsPanel.setLayout(new java.awt.BorderLayout());

        partBarsTF1.setFont(new java.awt.Font("Dialog", 1, 12));
        partBarsTF1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        partBarsTF1.setToolTipText("Set the number of bars in one chorus (the same for all choruses)");
        partBarsTF1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        partBarsTF1.setMaximumSize(new java.awt.Dimension(60, 15));
        partBarsTF1.setMinimumSize(new java.awt.Dimension(60, 15));
        partBarsTF1.setPreferredSize(new java.awt.Dimension(60, 15));
        partBarsTF1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                partBarsTF1ActionPerformed(evt);
            }
        });
        partBarsTF1.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                partBarsTF1FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                partBarsTF1FocusLost(evt);
            }
        });
        partBarsTF1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                partBarsTF1KeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                partBarsTF1KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                partBarsTF1KeyReleased(evt);
            }
        });
        partBarsTF1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                partBarsTF1MousePressed(evt);
            }
        });
        partBarsPanel.add(partBarsTF1, java.awt.BorderLayout.CENTER);

        playToolBar.add(partBarsPanel);

        trackerDelayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tracker Delay", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        trackerDelayPanel.setMaximumSize(new java.awt.Dimension(80, 63));
        trackerDelayPanel.setMinimumSize(new java.awt.Dimension(80, 63));
        trackerDelayPanel.setOpaque(false);
        trackerDelayPanel.setPreferredSize(new java.awt.Dimension(80, 45));
        trackerDelayPanel.setLayout(new java.awt.BorderLayout());

        trackerDelayTextField2.setFont(new java.awt.Font("Dialog", 1, 12));
        trackerDelayTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackerDelayTextField2.setToolTipText("Set the delay between the tracker and playback.");
        trackerDelayTextField2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        trackerDelayTextField2.setMaximumSize(new java.awt.Dimension(60, 20));
        trackerDelayTextField2.setMinimumSize(new java.awt.Dimension(20, 20));
        trackerDelayTextField2.setPreferredSize(new java.awt.Dimension(50, 20));
        trackerDelayTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trackerDelayTextField2ActionPerformed(evt);
            }
        });
        trackerDelayTextField2.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                trackerDelayTextField2FocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                trackerDelayTextField2FocusLost(evt);
            }
        });
        trackerDelayTextField2.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                trackerDelayTextField2KeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                trackerDelayTextField2KeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                trackerDelayTextField2KeyReleased(evt);
            }
        });
        trackerDelayTextField2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                trackerDelayTextField2MousePressed(evt);
            }
        });
        trackerDelayPanel.add(trackerDelayTextField2, java.awt.BorderLayout.CENTER);

        playToolBar.add(trackerDelayPanel);

        earlyScrollBtn.setBackground(new java.awt.Color(51, 255, 255));
        earlyScrollBtn.setFont(new java.awt.Font("Arial", 0, 11));
        earlyScrollBtn.setSelected(true);
        earlyScrollBtn.setText("<html>\n<center>\nEarly\n<br>\nScroll\n</center>\n</html>\n");
        earlyScrollBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        earlyScrollBtn.setFocusable(false);
        earlyScrollBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        earlyScrollBtn.setMaximumSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.setMinimumSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.setOpaque(true);
        earlyScrollBtn.setPreferredSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.setSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                earlyScrollBtnActionPerformed(evt);
            }
        });
        playToolBar.add(earlyScrollBtn);

        parallaxSpinner.setToolTipText("Sets the vertical parallax for mouse clicks on staves.");
        parallaxSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Parallax", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
        parallaxSpinner.setMaximumSize(new java.awt.Dimension(55, 45));
        parallaxSpinner.setMinimumSize(new java.awt.Dimension(55, 45));
        parallaxSpinner.setPreferredSize(new java.awt.Dimension(55, 45));
        playToolBar.add(parallaxSpinner);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        toolbarPanel.add(playToolBar, gridBagConstraints);

        textEntryToolBar.setFloatable(false);
        textEntryToolBar.setToolTipText("Enter chords and melody via this text entry window");
        textEntryToolBar.setMinimumSize(new java.awt.Dimension(117, 40));
        textEntryToolBar.setPreferredSize(new java.awt.Dimension(1050, 40));

        textEntryLabel.setText("Textual Entry ");
        textEntryToolBar.add(textEntryLabel);

        textEntry.setFont(new java.awt.Font("Dialog", 0, 14));
        textEntry.setToolTipText("Enter chords or melody in leadsheet notation.");
        textEntry.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        textEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textEntryActionPerformed(evt);
            }
        });
        textEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textEntryGainsFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textEntryLosesFocus(evt);
            }
        });
        textEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textEntryKeyPressed(evt);
            }
        });
        textEntryToolBar.add(textEntry);

        clearButton.setBackground(new java.awt.Color(255, 255, 51));
        clearButton.setText("Clear");
        clearButton.setToolTipText("Clear the textual entry field.");
        clearButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        clearButton.setMaximumSize(new java.awt.Dimension(46, 38));
        clearButton.setMinimumSize(new java.awt.Dimension(46, 38));
        clearButton.setOpaque(true);
        clearButton.setPreferredSize(new java.awt.Dimension(46, 38));
        clearButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearButtonActionPerformed(evt);
            }
        });
        textEntryToolBar.add(clearButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        toolbarPanel.add(textEntryToolBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(toolbarPanel, gridBagConstraints);

        scoreTab.setBackground(new java.awt.Color(255, 255, 255));
        scoreTab.setOpaque(true);
        scoreTab.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scoreTabStateChanged(evt);
            }
        });
        scoreTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scoreTabKeyPressed(evt);
            }
        });
        scoreTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseEnteredTabPanel(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(scoreTab, gridBagConstraints);

        fileMenu.setMnemonic('f');
        fileMenu.setText("File");
        fileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileMenuActionPerformed(evt);
            }
        });

        aboutMI.setText("About Impro-Visor");
        aboutMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMIActionPerformed(evt);
            }
        });
        fileMenu.add(aboutMI);
        fileMenu.add(jSeparator22);

        newMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMI.setMnemonic('n');
        newMI.setText("New Leadsheet");
        newMI.setToolTipText("Start a new leadsheet in its own window.");
        newMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMIActionPerformed(evt);
            }
        });
        fileMenu.add(newMI);

        openLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openLeadsheetMI.setMnemonic('o');
        openLeadsheetMI.setText("Open Leadsheet");
        openLeadsheetMI.setToolTipText("Open a leadsheet in the current window.");
        openLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLeadsheetMIActionPerformed(evt);
            }
        });
        fileMenu.add(openLeadsheetMI);

        revertToSavedMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        revertToSavedMI.setText("Revert to Saved Leadsheet");
        revertToSavedMI.setToolTipText("Revert leadsheet to saved version, discarding any changes.");
        revertToSavedMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertLeadsheetActionPerformed(evt);
            }
        });
        fileMenu.add(revertToSavedMI);
        fileMenu.add(jSeparator6);

        saveLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveLeadsheetMI.setMnemonic('s');
        saveLeadsheetMI.setText("Save Leadsheet");
        saveLeadsheetMI.setToolTipText("Save the current leadsheet.");
        saveLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLeadsheetMIActionPerformed(evt);
            }
        });
        fileMenu.add(saveLeadsheetMI);

        saveAsLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, java.awt.event.InputEvent.CTRL_MASK));
        saveAsLeadsheetMI.setText("Save Leadsheet As");
        saveAsLeadsheetMI.setToolTipText("Save the current leadsheet under a specified file name.");
        saveAsLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsLeadsheetMIActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsLeadsheetMI);

        exportAllToMidi.setText("Export Leadsheet to MIDI");
        exportAllToMidi.setToolTipText("Create a MIDI file of the playback of the current leadsheet.");
        exportAllToMidi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportAllToMidiActionPerformed(evt);
            }
        });
        fileMenu.add(exportAllToMidi);

        exportChorusToMusicXML.setText("Export Chorus to MusicXML");
        exportChorusToMusicXML.setToolTipText("Create a MusicXML file for the current chorus.");
        exportChorusToMusicXML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportChorusToMusicXMLActionPerformed(evt);
            }
        });
        fileMenu.add(exportChorusToMusicXML);
        fileMenu.add(jSeparator2);

        loadAdvMI.setText("Load Vocabulary");
        loadAdvMI.setToolTipText("Load a new vocabulary.");
        loadAdvMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadAdvMIActionPerformed(evt);
            }
        });
        fileMenu.add(loadAdvMI);

        saveAdvice.setText("Save Vocabulary");
        saveAdvice.setToolTipText("Save the current vocabulary.");
        saveAdvice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAdviceActionPerformed(evt);
            }
        });
        fileMenu.add(saveAdvice);

        saveAsAdvice.setText("Save Vocabulary As");
        saveAsAdvice.setToolTipText("Save the current vocabulary in a file.");
        saveAsAdvice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsAdviceActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsAdvice);
        fileMenu.add(jSeparator17);

        printMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_MASK));
        printMI.setText("Print Leadsheet");
        printMI.setToolTipText("Print the current leadsheet.");
        printMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMIActionPerformed(evt);
            }
        });
        fileMenu.add(printMI);
        fileMenu.add(jSeparator5);

        quitMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_MASK));
        quitMI.setMnemonic('q');
        quitMI.setText("Quit");
        quitMI.setToolTipText("Quit Impro-Visor.");
        quitMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMIActionPerformed(evt);
            }
        });
        fileMenu.add(quitMI);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('e');
        editMenu.setText("Edit");

        selectAllMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, java.awt.event.InputEvent.CTRL_MASK));
        selectAllMI.setText("Select All");
        selectAllMI.setToolTipText("Select all notes and chords on the sheet.");
        selectAllMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllMIActionPerformed(evt);
            }
        });
        editMenu.add(selectAllMI);
        editMenu.add(jSeparator18);

        undoMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, 0));
        undoMI.setMnemonic('u');
        undoMI.setText("Undo");
        undoMI.setToolTipText("Undo the most recent undoable action.");
        undoMI.setEnabled(false);
        undoMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMIActionPerformed(evt);
            }
        });
        editMenu.add(undoMI);

        redoMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, 0));
        redoMI.setText("Redo");
        redoMI.setToolTipText("Redo the most recent action that was undone.");
        redoMI.setEnabled(false);
        redoMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMIActionPerformed(evt);
            }
        });
        editMenu.add(redoMI);
        editMenu.add(jSeparator7);

        cutMelodyMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, 0));
        cutMelodyMI.setText("Cut Melody");
        cutMelodyMI.setToolTipText("Cut the selected melody (saves in clipboard).");
        cutMelodyMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMelodyMIActionPerformed(evt);
            }
        });
        editMenu.add(cutMelodyMI);

        cutChordsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.SHIFT_MASK));
        cutChordsMI.setText("Cut Chords");
        cutChordsMI.setToolTipText("Cut the selected  chords (saves in clipboard).");
        cutChordsMI.setEnabled(false);
        cutChordsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutChordsMIActionPerformed(evt);
            }
        });
        editMenu.add(cutChordsMI);

        cutBothMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutBothMI.setText("Cut Melody and Chords");
        cutBothMI.setToolTipText("Cut the selected melody and chords (saves in clipboard).");
        cutBothMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutBothMIActionPerformed(evt);
            }
        });
        editMenu.add(cutBothMI);

        copyMelodyMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0));
        copyMelodyMI.setLabel("Copy Melody");
        copyMelodyMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMelodyMIActionPerformed(evt);
            }
        });
        editMenu.add(copyMelodyMI);

        copyChordsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.SHIFT_MASK));
        copyChordsMI.setText("Copy Chords");
        copyChordsMI.setToolTipText("Copy chords to clipboard.");
        copyChordsMI.setEnabled(false);
        copyChordsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyChordsMIActionPerformed(evt);
            }
        });
        editMenu.add(copyChordsMI);

        copyBothMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyBothMI.setText("Copy Melody and Chords");
        copyBothMI.setToolTipText("Copy melody and chords to clipboard.");
        copyBothMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBothMIActionPerformed(evt);
            }
        });
        editMenu.add(copyBothMI);

        pasteMelodyMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        pasteMelodyMI.setText("Paste Melody");
        pasteMelodyMI.setToolTipText("Paste melody from clipboard and selected grid line.");
        pasteMelodyMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMelodyMIActionPerformed(evt);
            }
        });
        editMenu.add(pasteMelodyMI);

        pasteChordsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.SHIFT_MASK));
        pasteChordsMI.setText("Paste Chords");
        pasteChordsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteChordsMIActionPerformed(evt);
            }
        });
        editMenu.add(pasteChordsMI);

        pasteBothMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pasteBothMI.setToolTipText("Paste chords and melody from clipboard and selected grid line.");
        pasteBothMI.setLabel("Paste Melody and Chords");
        pasteBothMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteBothMIActionPerformed(evt);
            }
        });
        editMenu.add(pasteBothMI);

        pasteOverMI.setSelected(true);
        pasteOverMI.setText("Always Overwrite when Pasting");
        pasteOverMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteOverMIActionPerformed(evt);
            }
        });
        editMenu.add(pasteOverMI);
        editMenu.add(jSeparator16);

        enterMelodyMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, 0));
        enterMelodyMI.setText("Enter Melody from Text\n");
        enterMelodyMI.setToolTipText("Enter melody currently in the text entry window.");
        enterMelodyMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterMelodyMIActionPerformed(evt);
            }
        });
        editMenu.add(enterMelodyMI);

        enterChordsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK));
        enterChordsMI.setText("Enter Chords from Text\n");
        enterChordsMI.setToolTipText("Enter chords currently in text entry window.");
        enterChordsMI.setActionCommand("Enter Chords2");
        enterChordsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterChordsMIActionPerformed(evt);
            }
        });
        editMenu.add(enterChordsMI);

        enterBothMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
        enterBothMI.setText("Enter Melody and Chords from Text\n");
        enterBothMI.setToolTipText("Enter chords and melody currently in the text entry window.");
        enterBothMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterBothMIActionPerformed(evt);
            }
        });
        editMenu.add(enterBothMI);
        editMenu.add(jSeparator13);

        reverseMelody.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SLASH, 0));
        reverseMelody.setText("Reverse selected melody");
        reverseMelody.setToolTipText("Reverse the notes in the selected melody.");
        reverseMelody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reverseMelodyActionPerformed(evt);
            }
        });
        editMenu.add(reverseMelody);

        invertMelody.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SLASH, 0));
        invertMelody.setText("Invert selected melody");
        invertMelody.setToolTipText("Invert the notes in the selected melody.");
        invertMelody.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                invertMelodyActionPerformed(evt);
            }
        });
        editMenu.add(invertMelody);

        expandMelodyBy2.setText("Expand melody by 2");
        expandMelodyBy2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandMelodyBy2ActionPerformed(evt);
            }
        });
        editMenu.add(expandMelodyBy2);

        expandMelodyBy3.setText("Expand melody by 3");
        expandMelodyBy3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandMelodyBy3ActionPerformed1(evt);
            }
        });
        editMenu.add(expandMelodyBy3);

        contractMelodyBy2.setText("Contract melody by 2");
        contractMelodyBy2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contractMelodyBy2ActionPerformed1(evt);
            }
        });
        editMenu.add(contractMelodyBy2);

        contractMelodyBy3.setText("Contract melody by 3");
        contractMelodyBy3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contractMelodyBy3ActionPerformed11(evt);
            }
        });
        editMenu.add(contractMelodyBy3);
        editMenu.add(jSeparator14);

        copyMelodySelectionToTextWindow.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, 0));
        copyMelodySelectionToTextWindow.setText("Copy Melody to Text Window");
        copyMelodySelectionToTextWindow.setToolTipText("Copy the melody in the selection to text window.");
        copyMelodySelectionToTextWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMelodySelectionToTextWindowActionPerformed(evt);
            }
        });
        editMenu.add(copyMelodySelectionToTextWindow);

        copyChordSelectionToTextWindow.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.SHIFT_MASK));
        copyChordSelectionToTextWindow.setText("Copy Chords to Text Window");
        copyChordSelectionToTextWindow.setToolTipText("Copy the chords in selection to text window.");
        copyChordSelectionToTextWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyChordSelectionToTextWindowActionPerformed(evt);
            }
        });
        editMenu.add(copyChordSelectionToTextWindow);

        copyBothSelectionToTextWindow.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_J, java.awt.event.InputEvent.CTRL_MASK));
        copyBothSelectionToTextWindow.setText("Copy Selection to Text Window");
        copyBothSelectionToTextWindow.setToolTipText("Copy the selection to text window.");
        copyBothSelectionToTextWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBothSelectionToTextWindowActionPerformed(evt);
            }
        });
        editMenu.add(copyBothSelectionToTextWindow);

        saveSelectionAsLick.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, 0));
        saveSelectionAsLick.setText("Save Selection as Lick, Cell, Idiom, or Quote");
        saveSelectionAsLick.setToolTipText("Save the selection as a lick.\n");
        saveSelectionAsLick.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSelectionAsLickActionPerformed(evt);
            }
        });
        editMenu.add(saveSelectionAsLick);

        generateLickInSelection.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_MASK));
        generateLickInSelection.setText("Generate Lick in Selection");
        generateLickInSelection.setToolTipText("Save the selection as a lick.\n");
        generateLickInSelection.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLickInSelectionActionPerformed(evt);
            }
        });
        editMenu.add(generateLickInSelection);
        editMenu.add(jSeparator21);

        insertRestMeasure.setText("Insert a Measure of Rest");
        insertRestMeasure.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertRestMeasureActionPerformed(evt);
            }
        });
        editMenu.add(insertRestMeasure);

        addRestMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, 0));
        addRestMI.setText("Add Rest");
        addRestMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRestMIActionPerformed(evt);
            }
        });
        editMenu.add(addRestMI);

        resolvePitches.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.SHIFT_MASK));
        resolvePitches.setLabel("Rectify Melody to Harmony");
        resolvePitches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resolvePitchesActionPerformed(evt);
            }
        });
        editMenu.add(resolvePitches);

        menuBar.add(editMenu);

        transposeMenu.setText("Transpose");

        transposeMelodyUpSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        transposeMelodyUpSemitone.setText("Transpose Melody Up Semitone");
        transposeMelodyUpSemitone.setToolTipText("Transpose the selected melody up one half-step.");
        transposeMelodyUpSemitone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeMelodyUpSemitoneActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeMelodyUpSemitone);

        transposeChordsUpSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK));
        transposeChordsUpSemitone.setText("Transpose Chords Up Semitone");
        transposeChordsUpSemitone.setToolTipText("Transpose the selected chords up one half-step.");
        transposeChordsUpSemitone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeChordsUpSemitoneActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeChordsUpSemitone);

        transposeBothUpSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
        transposeBothUpSemitone.setText("Transpose Both Up Semitone");
        transposeBothUpSemitone.setToolTipText("Transpose the selected melody and chords up one half-step.");
        transposeBothUpSemitone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeBothUpSemitoneActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeBothUpSemitone);

        transposeMelodyUpHarmonically.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_W, 0));
        transposeMelodyUpHarmonically.setText("Transpose Melody Up Harmonically");
        transposeMelodyUpHarmonically.setToolTipText("Transpose the selected melody upward, consistently with the chord/scale.");
        transposeMelodyUpHarmonically.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeMelodyUpHarmonicallyActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeMelodyUpHarmonically);

        transposeMelodyUpOctave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, 0));
        transposeMelodyUpOctave.setText("Transpose Melody Up Octave");
        transposeMelodyUpOctave.setToolTipText("Transpose the selected melody up one octave.");
        transposeMelodyUpOctave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeMelodyUpOctaveActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeMelodyUpOctave);

        transposeMelodyDownSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        transposeMelodyDownSemitone.setText("Transpose Melody Down Semitone");
        transposeMelodyDownSemitone.setToolTipText("Transpose the selected melody down one half-step.");
        transposeMelodyDownSemitone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeMelodyDownSemitoneActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeMelodyDownSemitone);

        transposeChordsDownSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK));
        transposeChordsDownSemitone.setText("Transpose Chords Down Semitone");
        transposeChordsDownSemitone.setToolTipText("Transpose the selected chords down one half-step.");
        transposeChordsDownSemitone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeChordsDownSemitoneActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeChordsDownSemitone);

        transposeBothDownSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
        transposeBothDownSemitone.setText("Transpose Both Down Semitone");
        transposeBothDownSemitone.setToolTipText("Transpose the selected melody and chords down one half-step.");
        transposeBothDownSemitone.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeBothDownSemitoneActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeBothDownSemitone);

        transposeMelodyDownHarmonically.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, 0));
        transposeMelodyDownHarmonically.setText("Transpose Melody Down Harmonically\n");
        transposeMelodyDownHarmonically.setToolTipText("Transpose the selected melody down, consistently with the chord/scale.");
        transposeMelodyDownHarmonically.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeMelodyDownHarmonicallyActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeMelodyDownHarmonically);

        transposeMelodyDownOctave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, 0));
        transposeMelodyDownOctave.setText("Transpose Melody Down Octave");
        transposeMelodyDownOctave.setToolTipText("Transpose the selected melody down one octave.");
        transposeMelodyDownOctave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeMelodyDownOctaveActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeMelodyDownOctave);

        menuBar.add(transposeMenu);

        viewMenu.setMnemonic('v');
        viewMenu.setText("View");

        oneAutoMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, java.awt.event.InputEvent.CTRL_MASK));
        oneAutoMI.setText("Perform a Single Layout Adjustment");
        oneAutoMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneAutoMIActionPerformed(evt);
            }
        });
        viewMenu.add(oneAutoMI);

        autoAdjustMI.setText("Continuously Auto-Adjust the Stave Layout");
        autoAdjustMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoAdjustMIActionPerformed(evt);
            }
        });
        viewMenu.add(autoAdjustMI);

        showTitlesMI.setSelected(true);
        showTitlesMI.setText("Show Leadsheet Title");
        showTitlesMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTitlesMIActionPerformed(evt);
            }
        });
        viewMenu.add(showTitlesMI);

        showEmptyTitlesMI.setSelected(true);
        showEmptyTitlesMI.setText("Show Empty Title Placeholders");
        showEmptyTitlesMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showEmptyTitlesMIActionPerformed(evt);
            }
        });
        viewMenu.add(showEmptyTitlesMI);

        barNumsMI.setSelected(true);
        barNumsMI.setText("Show Bar Numbers");
        barNumsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barNumsMIActionPerformed(evt);
            }
        });
        viewMenu.add(barNumsMI);

        measureCstrLinesMI.setSelected(true);
        measureCstrLinesMI.setText("Show Construction Lines on Current Measure");
        measureCstrLinesMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                measureCstrLinesMIActionPerformed(evt);
            }
        });
        viewMenu.add(measureCstrLinesMI);

        allCstrLinesMI.setText("Show All Construction Lines");
        allCstrLinesMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                allCstrLinesMIActionPerformed(evt);
            }
        });
        viewMenu.add(allCstrLinesMI);

        menuBar.add(viewMenu);

        playMenu.setMnemonic('p');
        playMenu.setText("Play");
        playMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMenuActionPerformed(evt);
            }
        });

        playAllMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, 0));
        playAllMI.setMnemonic('p');
        playAllMI.setText("Play All");
        playAllMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playAllMIActionPerformed(evt);
            }
        });
        playMenu.add(playAllMI);

        stopPlayMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, 0));
        stopPlayMI.setText("Stop Playback");
        stopPlayMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayMIActionPerformed(evt);
            }
        });
        playMenu.add(stopPlayMI);

        pausePlayMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_L, 0));
        pausePlayMI.setText("Pause/Unpause Playback");
        pausePlayMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseMIActionPerformed(evt);
            }
        });
        playMenu.add(pausePlayMI);

        menuBar.add(playMenu);

        utilitiesMenu.setMnemonic('U');
        utilitiesMenu.setText("Utilities");

        openLeadsheetEditorMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        openLeadsheetEditorMI.setMnemonic('l');
        openLeadsheetEditorMI.setText("Leadsheet Textual Editor");
        openLeadsheetEditorMI.setToolTipText("Open file editor for leadsheet.");
        openLeadsheetEditorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLeadsheetEditorMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(openLeadsheetEditorMI);

        lickGeneratorMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.CTRL_MASK));
        lickGeneratorMI.setMnemonic('g');
        lickGeneratorMI.setText("Lick Generator");
        lickGeneratorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lickGeneratorMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(lickGeneratorMI);

        pianoKeyboardMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        pianoKeyboardMI.setMnemonic('K');
        pianoKeyboardMI.setText("Piano Keyboard");
        pianoKeyboardMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pianoKeyboardMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(pianoKeyboardMI);

        styleGenerator1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        styleGenerator1.setMnemonic('S');
        styleGenerator1.setText("Style Editor & Extractor");
        styleGenerator1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleGenerator1ActionPerformed(evt);
            }
        });
        utilitiesMenu.add(styleGenerator1);

        voicingTestMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        voicingTestMI.setMnemonic('v');
        voicingTestMI.setText("Voicing Editor");
        voicingTestMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingTestMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(voicingTestMI);

        menuBar.add(utilitiesMenu);

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

        menuBar.add(windowMenu);

        notateGrammarMenu.setText("Grammar: My");
        notateGrammarMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notateGrammarMenuActionPerformed(evt);
            }
        });
        notateGrammarMenu.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                notateGrammarMenuStateChanged(evt);
            }
        });
        notateGrammarMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                notateGrammarMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });
        menuBar.add(notateGrammarMenu);

        preferencesMenu.setMnemonic('R');
        preferencesMenu.setText("Preferences");
        preferencesMenu.setNextFocusableComponent(leadsheetPreferences);
        preferencesMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesMenuActionPerformed(evt);
            }
        });

        preferencesAcceleratorMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, 0));
        preferencesAcceleratorMI.setText("Open Preferences");
        preferencesAcceleratorMI.setToolTipText("Open the preferences dialog.");
        preferencesAcceleratorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesAcceleratorMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(preferencesAcceleratorMI);

        globalPrefsMI.setMnemonic('G');
        globalPrefsMI.setText("Global");
        globalPrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalPrefsMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(globalPrefsMI);

        leadsheetPrefsMI.setMnemonic('L');
        leadsheetPrefsMI.setText("Leadsheet");
        leadsheetPrefsMI.setSelected(true);
        leadsheetPrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leadsheetPrefsMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(leadsheetPrefsMI);

        chorusPrefsMI.setMnemonic('C');
        chorusPrefsMI.setText("Chorus");
        chorusPrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chorusPrefsMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(chorusPrefsMI);

        stylePrefsMI.setMnemonic('S');
        stylePrefsMI.setText("Style");
        stylePrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stylePrefsMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(stylePrefsMI);

        midiPrefsMI.setMnemonic('M');
        midiPrefsMI.setText("MIDI");
        midiPrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiPrefsMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(midiPrefsMI);

        contourPrefsMI.setMnemonic('T');
        contourPrefsMI.setText("Contour");
        contourPrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contourPrefsMIActionPerformed(evt);
            }
        });
        preferencesMenu.add(contourPrefsMI);

        menuBar.add(preferencesMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");
        helpMenu.setToolTipText("Open the help dialog.");
        helpMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMenuActionPerformed(evt);
            }
        });

        helpMI.setMnemonic('I');
        helpMI.setText("Impro-Visor Help");
        helpMI.setToolTipText("Shows the Help Dialog");
        helpMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpMIActionPerformed(evt);
            }
        });
        helpMenu.add(helpMI);
        helpMenu.add(jSeparator32);

        helpAboutMI.setMnemonic('A');
        helpAboutMI.setText("About Impro-Visor");
        helpAboutMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                helpAboutMIActionPerformed(evt);
            }
        });
        helpMenu.add(helpAboutMI);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void chorusPrefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chorusPrefsMIActionPerformed
      changePrefTab(chorusBtn, chorusPreferences);

      showPreferencesDialog();
    }//GEN-LAST:event_chorusPrefsMIActionPerformed

    private void chorusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chorusBtnActionPerformed
      changePrefTab(chorusBtn, chorusPreferences);
    }//GEN-LAST:event_chorusBtnActionPerformed

  private void styleGenerator1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleGenerator1ActionPerformed

    StyleEditor se = getStyleEditor();
    // se.setLocationRelativeTo(this);
    se.pack();
    WindowRegistry.registerWindow(se);
    se.setLocation(
            this.getX() + WindowRegistry.defaultXnewWindowStagger,
            this.getY() + WindowRegistry.defaultYnewWindowStagger);
    se.setVisible(true);
  }//GEN-LAST:event_styleGenerator1ActionPerformed

    private void savePrefsBtn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePrefsBtn3ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_savePrefsBtn3ActionPerformed

    private void resetBtn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtn3ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_resetBtn3ActionPerformed

    private void cancelBtn3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtn3ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_cancelBtn3ActionPerformed

    private void savePrefsBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePrefsBtn2ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_savePrefsBtn2ActionPerformed

    private void resetBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtn2ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_resetBtn2ActionPerformed

    private void cancelBtn2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtn2ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_cancelBtn2ActionPerformed

    private void savePrefsBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePrefsBtn1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_savePrefsBtn1ActionPerformed

    private void resetBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtn1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_resetBtn1ActionPerformed

    private void cancelBtn1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtn1ActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_cancelBtn1ActionPerformed

  private void mouseEnteredTabPanel(java.awt.event.MouseEvent evt)//GEN-FIRST:event_mouseEnteredTabPanel
  {//GEN-HEADEREND:event_mouseEnteredTabPanel
    requestFocusInWindow();
  }//GEN-LAST:event_mouseEnteredTabPanel

  private void loopSetMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_loopSetMousePressed
  {//GEN-HEADEREND:event_loopSetMousePressed
// TODO add your handling code here:
  }//GEN-LAST:event_loopSetMousePressed

  private void loopSetKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_loopSetKeyTyped
  {//GEN-HEADEREND:event_loopSetKeyTyped
// TODO add your handling code here:
  }//GEN-LAST:event_loopSetKeyTyped

  private void loopSetKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_loopSetKeyReleased
  {//GEN-HEADEREND:event_loopSetKeyReleased
// TODO add your handling code here:
  }//GEN-LAST:event_loopSetKeyReleased

  private void loopSetKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_loopSetKeyPressed
  {//GEN-HEADEREND:event_loopSetKeyPressed
// TODO add your handling code here:
  }//GEN-LAST:event_loopSetKeyPressed

  private void loopSetFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_loopSetFocusLost
  {//GEN-HEADEREND:event_loopSetFocusLost
// TODO add your handling code here:
  }//GEN-LAST:event_loopSetFocusLost

  private void loopSetFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_loopSetFocusGained
  {//GEN-HEADEREND:event_loopSetFocusGained
// TODO add your handling code here:
  }//GEN-LAST:event_loopSetFocusGained

  private void loopSetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loopSetActionPerformed
  {//GEN-HEADEREND:event_loopSetActionPerformed
     staveRequestFocus();
     getCurrentStave().playSelection(false, getLoopCount());
  }//GEN-LAST:event_loopSetActionPerformed

  private void loopButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loopButtonActionPerformed
  {//GEN-HEADEREND:event_loopButtonActionPerformed
    if( loopButton.isSelected() )
      {
      setToLoop();
      }
    else
      {
      setToNotLoop();
      }
 
  }//GEN-LAST:event_loopButtonActionPerformed

  private void setToLoop()
  {
      toLoop = true;
      loopButton.setText("<html><center>Straight</center></html>");
      loopButton.setBackground(Color.RED);
  }

  private void setToNotLoop()
  {
      toLoop = false;
      stopPlaying();
      loopButton.setText("<html><center>Loop</center></html>");
      loopButton.setBackground(Color.GREEN);
  }

    private void generateToolbarBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_generateToolbarBtnActionPerformed
    {//GEN-HEADEREND:event_generateToolbarBtnActionPerformed
      generate(lickgen);
    }//GEN-LAST:event_generateToolbarBtnActionPerformed

  public void showCritic()
  {
       criticDialog.setVisible(true);
  }

  private boolean initLocationStyleEditor = false;

  public void reloadStyles()
    {

    reCaptureCurrentStyle();

    styleListModel.reset();

    sectionListModel.reset();

    }

  public void reCaptureCurrentStyle()
    {
    score.getChordProg().getSectionInfo().reloadStyles();
    }

    private void helpAboutMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpAboutMIActionPerformed
      showAboutDialog();       
    }//GEN-LAST:event_helpAboutMIActionPerformed

    private void helpMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMIActionPerformed

      openHelpDialog();
        
    }//GEN-LAST:event_helpMIActionPerformed

    private void preferencesBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesBtnActionPerformed

      showPreferencesDialog();
        
    }//GEN-LAST:event_preferencesBtnActionPerformed

    private void contourPrefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contourPrefsMIActionPerformed

      changePrefTab(contourBtn, contourPreferences);
      showPreferencesDialog();
        
    }//GEN-LAST:event_contourPrefsMIActionPerformed

    private void midiPrefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midiPrefsMIActionPerformed

      changePrefTab(midiBtn, midiPreferences);
      showPreferencesDialog();
        
    }//GEN-LAST:event_midiPrefsMIActionPerformed

    private void stylePrefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stylePrefsMIActionPerformed
      changePrefTab(styleBtn, stylePreferences);
      showPreferencesDialog();
        
    }//GEN-LAST:event_stylePrefsMIActionPerformed

    private void leadsheetPrefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leadsheetPrefsMIActionPerformed

      changePrefTab(leadsheetBtn, leadsheetPreferences);
      showPreferencesDialog();
        
    }//GEN-LAST:event_leadsheetPrefsMIActionPerformed

    private void globalPrefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalPrefsMIActionPerformed

      changePrefTab(globalBtn, globalPreferences);
      showPreferencesDialog();
        
    }//GEN-LAST:event_globalPrefsMIActionPerformed

  private String tempoSetOldTempo;

    private void tempoSetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tempoSetFocusGained

      tempoSetOldTempo = tempoSet.getText();
        
    }//GEN-LAST:event_tempoSetFocusGained

    private void setMeasureButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setMeasureButtonActionPerformed

      setSectionPrefs();

      sectionListModel.refresh();
        
    }//GEN-LAST:event_setMeasureButtonActionPerformed

    private void newSectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSectionButtonActionPerformed

      sectionInfo.newSection(sectionList.getSelectedIndex());

      sectionListModel.refresh();
        
    }//GEN-LAST:event_newSectionButtonActionPerformed

    private void delSectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delSectionButtonActionPerformed

      int index = sectionList.getSelectedIndex();

      sectionInfo.deleteSection(index);

      if( index >= sectionInfo.size() )
        {
        sectionList.setSelectedIndex(sectionInfo.size() - 1);
        }

      sectionListModel.refresh();
        
    }//GEN-LAST:event_delSectionButtonActionPerformed

    private void sectionListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_sectionListValueChanged

      sectionListModel.refresh();
        
    }//GEN-LAST:event_sectionListValueChanged

    public void toCritic()
  {
      if( lickgenFrame.toCriticSelected() )
        {

        getCurrentStave().lockSelectionWidth(16 * EIGHTH);

        getCurrentStave().repaint();

        }
      else
        {

        getCurrentStave().unlockSelectionWidth();

        }

    }
    private void stepInputBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepInputBtnActionPerformed

      setStepInput(stepInputBtn.isSelected());
        
    }//GEN-LAST:event_stepInputBtnActionPerformed

    private void addRestMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRestMIActionPerformed

      addRest();
        
    }//GEN-LAST:event_addRestMIActionPerformed

    private void styleListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_styleListValueChanged

      updateStyle();
        
    }//GEN-LAST:event_styleListValueChanged

    private void defDrumVolSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_defDrumVolSliderStateChanged

// TODO add your handling code here:
        
    }//GEN-LAST:event_defDrumVolSliderStateChanged

    private void showEmptyTitlesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showEmptyTitlesMIActionPerformed

      for( int i = 0; i < staveScrollPane.length; i++ )
        {

        Stave stave = staveScrollPane[i].getStave();

        stave.setShowEmptyTitles(showEmptyTitlesMI.isSelected());

        stave.repaint();
        }        
    }//GEN-LAST:event_showEmptyTitlesMIActionPerformed

    private void preferencesDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_preferencesDialogWindowClosing

      hideFakeModalDialog(preferencesDialog);
        
    }//GEN-LAST:event_preferencesDialogWindowClosing

    private void overwriteLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_overwriteLickButtonActionPerformed

    {//GEN-HEADEREND:event_overwriteLickButtonActionPerformed

      ignoreDuplicateLick = OVERWRITE;

      duplicateLickDialog.setVisible(false);
        
    }//GEN-LAST:event_overwriteLickButtonActionPerformed

    private void insertVoicingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertVoicingButtonActionPerformed
     
      Style currentStyle =
              ImproVisor.getCurrentWindow().score.getChordProg().getStyle();

      String v = voicingEntryTF.getText();

      String e = extEntryTF.getText();

      if( v.equals("") )
        {
        ErrorLog.log(ErrorLog.WARNING, "No voicing entered.");

        return;
        }

      StringReader voicingReader = new StringReader(v);

      Tokenizer in = new Tokenizer(voicingReader);

      Object o = in.nextSexp();

      int index = ImproVisor.getCurrentWindow().getCurrentSelectionStart();

      if( o instanceof Polylist && index != 1 )
        {

        Polylist voicing = (Polylist)o;

        if( voicing.length() == 0 )
          {
          ErrorLog.log(ErrorLog.WARNING, "Empty voicing entered.");

          return;
          }

        Polylist invalid = Key.invalidNotes(voicing);

        if( invalid.nonEmpty() )
          {
          ErrorLog.log(ErrorLog.WARNING, "Invalid notes in voicing: " + invalid);

          return;
          }

        voicing = NoteSymbol.makeNoteSymbolList(voicing);

        Polylist extension;

        if( e.equals("") )
          {
          extension = Polylist.nil;
          }
        else
          {
          StringReader extReader = new StringReader(e);

          in = new Tokenizer(extReader);

          o = in.nextSexp();

          if( o instanceof Polylist )
            {
            extension = (Polylist)o;

            invalid = Key.invalidNotes(extension);

            if( invalid.nonEmpty() )
              {
              ErrorLog.log(ErrorLog.WARNING,
                      "Invalid notes in extension: " + invalid);
              return;
              }

            extension = NoteSymbol.makeNoteSymbolList(extension);
            }
          else
            {
            ErrorLog.log(ErrorLog.WARNING, "Malformed extension: " + e);
            return;
            }
          }

        int row = voicingTable.getSelectedRow();

        if( row == -1 )
        {
            populateChordSelMenu();
            chordSelectionMenu.show(voicingTestFrame, 525, 75);
            return;
        }

        ChordSymbol c =
                ChordSymbol.makeChordSymbol(
                voicingTableModel.getValueAt(row, VoicingTableChordColumn).toString());
        
        //ChordSymbol c = ChordSymbol.makeChordSymbol(v);
        
        c.setVoicing(voicing);

        c.setExtension(extension);

        if( !ChordPattern.goodVoicing(c, currentStyle) )
          {
          ErrorLog.log(ErrorLog.WARNING,
                  "Voicing does not fit within range of leadsheet: " + voicing);
          return;
          }

        playVoicing(c);

        insertVoicing(c, index);
        }
      else if( o instanceof Polylist )
        {
        ErrorLog.log(ErrorLog.WARNING, "No slot selected for insertion.");
        return;
        }
      else
        {
        ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
        return;
        }
    }//GEN-LAST:event_insertVoicingButtonActionPerformed

    /**
     * Plays a string of pitches as a chord over a given symbol.
     *
     @param v
     */
    public void constructAndPlayChord(String symbol, String v)
    {
     if( v.equals("") )
        {
        return;
        }

      Style currentStyle =
              ImproVisor.getCurrentWindow().score.getChordProg().getStyle();

      StringReader voicingReader = new StringReader(v);

      Tokenizer in = new Tokenizer(voicingReader);

      Object o = in.nextSexp();

      if( o instanceof Polylist )
        {
        Polylist voicing = (Polylist)o;

        if( voicing.length() == 0 )
          {
          return;
          }

        Polylist invalid = Key.invalidNotes(voicing);

        if( invalid.nonEmpty() )
          {
          ErrorLog.log(ErrorLog.WARNING, "Invalid notes in voicing: " + invalid);
          return;
          }

        voicing = NoteSymbol.makeNoteSymbolList(voicing);

        ChordSymbol c = ChordSymbol.makeChordSymbol(symbol);
        
        if (voicing == null || c == null)
        {
            return;
        }

        c.setVoicing(voicing);

        playVoicing(c);
        }
      else
        {
        ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
        return;
        }
     }
    
    private void playVoicingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playVoicingButtonActionPerformed
      String c = getChordRootTFText();
      String v = voicingEntryTFText();
      constructAndPlayChord(c,v);
    }//GEN-LAST:event_playVoicingButtonActionPerformed

public void playCurrentVoicing()
{
  
      String v = voicingEntryTF.getText();

      String e = extEntryTF.getText();

      if( v.equals("") )
        {
        //ErrorLog.log(ErrorLog.WARNING, "No voicing entered.");

        return;
        }

      StringReader voicingReader = new StringReader(v);

      Tokenizer in = new Tokenizer(voicingReader);

      Object o = in.nextSexp();

      if( o instanceof Polylist )
        {

        Polylist voicing = (Polylist)o;

        if( voicing.length() == 0 )
          {
          //ErrorLog.log(ErrorLog.WARNING, "Empty voicing entered.");

          return;
          }


        Polylist invalid = Key.invalidNotes(voicing);

        if( invalid.nonEmpty() )
          {
          ErrorLog.log(ErrorLog.WARNING, "Invalid notes in voicing: " + invalid);

          return;
          }

        voicing = NoteSymbol.makeNoteSymbolList(voicing);

        Polylist extension;

        if( e.equals("") )
          {
          extension = Polylist.nil;
          }
        else
          {
          StringReader extReader = new StringReader(e);

          in = new Tokenizer(extReader);

          o = in.nextSexp();

          if( o instanceof Polylist )
            {
            extension = (Polylist)o;

            invalid = Key.invalidNotes(extension);

            if( invalid.nonEmpty() )
              {

              ErrorLog.log(ErrorLog.WARNING,
                      "Invalid notes in extension: " + invalid);
              return;
             }

            extension = NoteSymbol.makeNoteSymbolList(extension);
            }
          else
            {
            ErrorLog.log(ErrorLog.WARNING, "Malformed extension: " + e);

            return;
            }
          }


        int row = voicingTable.getSelectedRow();

        if( row == -1 )
          {
          ErrorLog.log(ErrorLog.WARNING, "Chord must be selected.");
          return;
          }

        ChordSymbol c =
                ChordSymbol.makeChordSymbol(voicingTableModel.getValueAt(row, VoicingTableChordColumn).toString());
        
        String s = voicingTableModel.getValueAt(row, VoicingTableChordColumn).toString();
                
        c.setVoicing(voicing);

        c.setExtension(extension);

        playVoicing(c);
        }
      else
        {
        ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
        return;
        }
        
}

/**
 *Determine whether the chord name in the indicated row of the voicing table
 *is a synonym for another name (e.g. by specifiying (uses ...)). If so,
 * return that name. Otherwise return null.
 @param row
 @return
 */
private String getChordRedirectName(int row)
  {
  if( row == -1 )
    {
    return null;
    }

  String voicingName =
          voicingTableModel.getValueAt(row, VoicingTableNameColumn).toString();

  if( voicingName.startsWith(VOICING_REDIRECT_PREFIX) )
    {
    String target = voicingName.substring(VOICING_REDIRECT_PREFIX.length(), voicingName.length()-1);
    return target;
    }
  return null;
  }

 public void saveGrammarAs()
  {
    grammarfc.setDialogTitle("Save Grammar As");

    File oldDirectory = grammarfc.getCurrentDirectory();

    grammarfc.setCurrentDirectory(grammarDir);

    // If never saved before, used the name specified in vocFile.
    // Otherwise use previous file.

    if( grammarFile == null )
      {
        grammarfc.setSelectedFile(new File(grammarFile));
      }

    grammarfc.resetChoosableFileFilters();

    grammarfc.addChoosableFileFilter(new GrammarFilter());

    if( grammarfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        lickgenFrame.saveTriageParameters();

        if( grammarfc.getSelectedFile().getName().endsWith(
            GrammarFilter.EXTENSION) )
          {
            grammarFile = grammarfc.getSelectedFile().getAbsolutePath();

            lickgen.saveGrammar(grammarFile);
          }
        else
          {
            grammarFile =
                grammarfc.getSelectedFile().getAbsolutePath() + GrammarFilter.EXTENSION;

            lickgen.saveGrammar(grammarFile);
          }
      }

    lickgenFrame.toFront();

 }

 public void useGrammarAction()
  {
      useGrammar = lickgenFrame.useGrammarSelected();
/* REVISIT:
      minDurationField.setEnabled(!useGrammar);

      maxDurationField.setEnabled(!useGrammar);

      restProbField.setEnabled(!useGrammar);
 */
 }
    public void editGrammar()
    {
      grammarEditor.fillEditor();
      grammarEditor.setSize(new Dimension(650, 600));
      grammarEditor.setLocationRelativeTo(this);
      grammarEditor.setVisible(true);
      grammarEditor.toFront();
    }
    public void loadGrammar()
    {
      lickgen.loadGrammar(grammarFile);

      lickgenFrame.resetTriageParameters(true);
    }

    public void openGrammar()
    {
      grammarfc.setDialogTitle("Load Grammar File");

      File oldDirectory = grammarfc.getCurrentDirectory();

      grammarfc.setCurrentDirectory(grammarDir);

      grammarfc.resetChoosableFileFilters();

      grammarfc.addChoosableFileFilter(new GrammarFilter());

      if( grammarfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
        grammarFile = grammarfc.getSelectedFile().getAbsolutePath();

        lickgen.loadGrammar(grammarFile);
        }

      grammarfc.setCurrentDirectory(oldDirectory);
      lickgenFrame.toFront();
    }

    private static int VoicingTableChordColumn     = 0;
    private static int VoicingTableNameColumn      = 1;
    private static int VoicingTableTypeColumn      = 2;
    private static int VoicingTableVoicingColumn   = 3;
    private static int VoicingTableExtensionColumn = 4;
    
    private void voicingTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_voicingTableMouseClicked

      keyboard.setPlayback(false);
      int clicks = evt.getClickCount();
      if( clicks == 2 )
        {
        addToVoicingSequence();
        }
      else
        {
        ListSelectionModel rowSM = voicingTable.getSelectionModel();

        if( !rowSM.isSelectionEmpty() )
          {
          int selectedRow = rowSM.getMinSelectionIndex();

          Object o = voicingTableModel.getValueAt(selectedRow,
                  VoicingTableChordColumn);

          String redirectName = getChordRedirectName(selectedRow);

          if( redirectName != null )
            {
            chordSearchTF.setText(redirectName);
            }

          if( o instanceof ChordSymbol )
            {

            ChordSymbol c = (ChordSymbol)o;

            String v = "";

            String e = "";

            Style s =
                    ImproVisor.getCurrentWindow().score.getChordProg().getStyle();

            if( ChordPattern.goodVoicing(c, s) )
              {
              Polylist L = 
                  ChordPattern.findFirstVoicingAndExtension(c, s.getChordBase(), s, false);
              
              c.setVoicing((Polylist)L.first());

              c.setExtension((Polylist)L.second());

              v = NoteSymbol.makePitchStringList((Polylist)L.first()).toString();

              e = NoteSymbol.makePitchStringList((Polylist)L.second()).toString();
              
              }
            else
              {
              Object voicingOb = voicingTableModel.getValueAt(selectedRow,
                      VoicingTableVoicingColumn);

              if( voicingOb != null )
                {
                Polylist L = (Polylist)voicingOb;

                s = s.copy();

                s.setChordLow(NoteSymbol.makeNoteSymbol("c-----"));

                s.setChordHigh(NoteSymbol.makeNoteSymbol("g+++++"));

                L = ChordPattern.findFirstVoicingAndExtension(
                        c, s.getChordBase(), s, false);

                c.setVoicing((Polylist)L.first());

                c.setExtension((Polylist)L.second());

                v = NoteSymbol.makePitchStringList((Polylist)L.first()).toString();

                e = NoteSymbol.makePitchStringList((Polylist)L.second()).toString();
                
                }
              }
            
            if( keyboard != null )
              {
              clearKeyboard();
              clearVoicingEntryTF();
              }
            
            if( e.equals("()" ) )
              {
              extEntryTF.setText("");
              }
            else
              {
              extEntryTF.setText(e);
              }
            
            keyboard.setPresentChordDisplayText(c.toString());
            voicingEntryTF.setText(v);
            keyboard.showVoicingOnKeyboard(v);
            chordSearchTF.setText(c.toString());
            
            String root = chordRootTF.getText();
            String bass = bassNoteTF.getText();
            
            int midi = keyboard.findBass();
            String note = keyboard.findBassName(midi);
            if (!note.equals(bass))
            {
                bassNoteTF.setText(bass);
            }
            keyboard.setBass(bass, midi);
            
            playVoicing(c);
            }
/*
          // Column 5 is not used, so what is this for?

          if( o instanceof ChordSymbol 
               && (Boolean)voicingTableModel.getValueAt(selectedRow,5) )
            {
            int index = ImproVisor.getCurrentWindow().getCurrentSelectionStart();

            if( evt.isShiftDown() && index != -1 )
              {
              insertVoicing((ChordSymbol)o, index);
              }
            }
 */
        }
      }
    }//GEN-LAST:event_voicingTableMouseClicked

  private void insertVoicing(ChordSymbol o, int index)
    {

    Chord c = new Chord(o, BEAT * 2);

    new SetChordCommand(index, c,
            ImproVisor.getCurrentWindow().score.getChordProg()).execute();



    ImproVisor.getCurrentWindow().getCurrentStaveActionHandler().moveSelectionRight(ImproVisor.getCurrentWindow().getCurrentSelectionStart() + BEAT * 2);

    ImproVisor.getCurrentWindow().redoAdvice();

    }

  private void playVoicing(ChordSymbol o)
    {

    playVoicing(o, true);

    }

  private void playVoicing(ChordSymbol o, boolean literal)
    {

    Chord c = new Chord(o, BEAT * 4);

    Style s =
            ImproVisor.getCurrentWindow().score.getChordProg().getStyle().copy();

    s.setNoStyle(true);
    
    // I hope these are overridden subsequently:

    s.setChordLow(NoteSymbol.makeNoteSymbol("c-----")); 

    s.setChordHigh(NoteSymbol.makeNoteSymbol("g+++++"));

    if( literal )
      {
      s.setChordBase(o.getVoicing());
      }

    Score cScore = new Score();

    cScore.addPart();

    cScore.getPart(0).addRest(new Rest(c.getRhythmValue()));

    cScore.getChordProg().addChord(c);

    cScore.getChordProg().setStyle(s);

    new PlayScoreCommand(cScore, 0, false, 0, getTransposition(), false, 4*BEAT).execute();

    }
  
  
 public void setTransposition(int transposition)
    {
     if( transposeSpinner != null )
     {
       transposeSpinner.setValue(transposition);
     }
   
    if( score != null )
      {
      score.setTransposition(transposition);
      }
    }
 
    private void buildVoicingTable()
    {
        String root = chordRootTF.getText();
        String bass = bassNoteTF.getText();
        String low = lowRangeTF.getText();
        String high = highRangeTF.getText();
        
        PitchClass rootClass = PitchClass.getPitchClass(root);
        
        if(root.equals("")) {
            ErrorLog.log(ErrorLog.WARNING, "No chord root entered.");
            return;
        }
        
        else if(rootClass == null) {
            ErrorLog.log(ErrorLog.WARNING, "Invalid chord root: " + root);
            return;
        }
        
        PitchClass bassClass = PitchClass.getPitchClass(bass);
        
        if(!bass.equals("") && bassClass == null) {
            ErrorLog.log(ErrorLog.WARNING, "Invalid bass note: " + bass);
            return;
        }
        
        NoteSymbol lowNote = NoteSymbol.makeNoteSymbol(low);
        
        if(low.equals("")) {
            ErrorLog.log(ErrorLog.WARNING, "No lower range entered.");
            return;
        }
        
        else if(lowNote == null) {
            ErrorLog.log(ErrorLog.WARNING, "Invalid lower range: " + low);
            return;
        }
        
        NoteSymbol highNote = NoteSymbol.makeNoteSymbol(high);
        
        if(high.equals("")) {
            ErrorLog.log(ErrorLog.WARNING, "No higher range entered.");
            return;
        }
        
        else if(highNote == null) {
            ErrorLog.log(ErrorLog.WARNING, "Invalid higher range: " + high);
            return;
        }
        
        voicingTableModel.setChordRoot(root,bass,lowNote,highNote);
    }
 
    
    private void buildTableButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_buildTableButtonActionPerformed
    
    {
        
        buildVoicingTable();
        
    }//GEN-LAST:event_buildTableButtonActionPerformed
    
    
    
    /**
     *
     * flag to reset position of dialog the first time it is displayed only
     *
     * means that the dialog gets centered the first time it is shown,
     *
     * when the dialog is shown a second time, it remembers it's last position
     *
     */
    
    private boolean initLocationVoicingFrame = false;
    
    private void voicingTestMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingTestMIActionPerformed
        
        if(!initLocationVoicingFrame) {
            
            voicingTestFrame.setLocationRelativeTo(this);
            
            initLocationVoicingFrame = true;
            
        }
        
        buildVoicingTable();
        
        voicingTestFrame.setVisible(true);
        
        if (getPlaying() == MidiPlayListener.Status.STOPPED)
        {
            openKeyboard();
            keyboard.showBass();
        }
        
    }//GEN-LAST:event_voicingTestMIActionPerformed
    
    
        
    
    
    private void pauseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseBtnActionPerformed

      pauseScore();
      if( keyboard != null )
        {
        String v = keyboard.voicingFromKeyboard();
        String currentChord = keyboard.getPresentChordDisplayText();

        if( voicingTestFrame != null && voicingTestFrame.isVisible() )
          {
          selectVoicing(v, currentChord);
          }
        }

    }//GEN-LAST:event_pauseBtnActionPerformed
    
    
    
    public void pauseScore() {
        
        if(getPlaying() != MidiPlayListener.Status.STOPPED) {
            
            midiSynth.pause();
            
        }
        
    }
    
    
    
    private void tempoSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tempoSliderStateChanged
        
        if(jSliderIgnoreStateChangedEvt)
            
            return;
        
        int value = tempoSlider.getValue();
        
        value = 2 * Math.round(value / 2);
        
        setTempo((double) value);

        setPlaybackManagerTime();
        
        if(!tempoSlider.getValueIsAdjusting()) {
            
            staveRequestFocus();
        }        
    }//GEN-LAST:event_tempoSliderStateChanged
    

    private void setPlaybackManagerTime()
    {
        establishCountIn();
        playbackManager.setTotalTime(million*score.getTotalTime());
    }


    private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMIActionPerformed
        
        closeWindow();
        
    }//GEN-LAST:event_closeWindowMIActionPerformed
    
    
    
    private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMIActionPerformed
        
        WindowRegistry.cascadeWindows(this);
        
    }//GEN-LAST:event_cascadeMIActionPerformed
    
    
    
    /**
     *
     * Set the status message of the Program Status field
     *
     */
    
    public void setStatus(String text) {
        
        programStatusTF.setText(text);
        
    }
    
    
    
    /**
     *
     * Get the status message of the Program Status field
     *
     */
    
    public String getStatus() {
        
        return programStatusTF.getText();
        
    }
    
    
    
    /**
     *
     * Set the text color of the Program Status field
     *
     */
    
    public void setStatusColor(Color c) {
        
        programStatusTF.setForeground(c);
        
    }
    
    
    
    /**
     *
     * Set the mode flag internally for Notate
     *
     * Nothing actually happens when the mode is changed, so it is up
     *
     * to the calling method to perform the necessary changes that accompany
     *
     * a mode switch, and hence this method is private.
     *
     */
    
    private void setMode(Mode mode) {
        
        if(this.mode == mode)
            
            return;
        
        
        
        previousMode = this.mode;
        
        if(mode == null)
            
            mode = Mode.NORMAL;
        
        this.mode = mode;
        
    }
    
    
    
    /**
     *
     * Tells other classes what mode the Notate object is in
     *
     */
    
    public Mode getMode() {
        
        return this.mode;
        
    }
    
    
    
    /**
     *
     * Stops recording: unregisteres the midiRecorder and changes the mode
     *
     */
    
    public void stopRecording() {
        
        if(mode == Mode.RECORDING) {
            
            setMode(previousMode);
            
        }
        
        playBtn.setEnabled(true);
        
        recordBtn.setIcon(recordImageIcon);
        
        recordBtn.setBackground(null);
        
        midiSynth.unregisterReceiver(midiRecorder);
        
        if(stepInputActive) {
            
            // if step input was active, reenable it since it is disabled during recording
            
            midiSynth.registerReceiver(midiStepInput);
            
        }
        
        stopPlaying();
        
    }
    
    
    
    /**
     *
     * Starts recording: registers the midiRecorder and changes the mode
     *
     */
    
    private void startRecording() {
        
        if(midiManager.getInDevice() == null) {
            
            ErrorLog.log(ErrorLog.COMMENT, "No valid MIDI in devices found.  \n\nPlease check your device connection and the MIDI Preferences. It is possible another program is currently using this device.");
            
            return;
            
        }
        
        
        
        setMode(Mode.RECORDING);
        
        playBtn.setEnabled(false);
        
        recordBtn.setIcon(recordActiveImageIcon);
        
        recordBtn.setBackground(Color.RED);
        
        
        
        midiSynth.registerReceiver(midiRecorder);
        
        
        
        staveRequestFocus();
        
        playScore();
        
        
        
        midiSynth.unregisterReceiver(midiStepInput);  // disable step input during recording
        
        midiSynth.registerReceiver(midiRecorder);
        
        midiRecorder.start();   // set time to 0
        
    }
    
    
    
    void stopPlaying() {
        
        midiSynth.stop("stop in Notate");
        
        if(mode == Mode.RECORDING) {
            
            stopRecording();
            
        }
        
    }
    
    
    
    private void setStepInput(boolean active) {
        
        stepInputActive = active;
        
        if(active) {
            
            midiSynth.registerReceiver(midiStepInput);
            
        } else {
            
            midiSynth.unregisterReceiver(midiStepInput);
            
        }
        
    }
    
    
    
    private void recordBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordBtnActionPerformed
        
        if(mode == Mode.RECORDING) {
            
            stopRecording();
            
        } else {
            
            startRecording();
            
        }
        
    }//GEN-LAST:event_recordBtnActionPerformed
    
    
    
    private void playBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtnActionPerformed
        
        if (keyboard != null && keyboard.isVisible())
        {
            keyboard.setPlayback(true);
        }
        playScore();
        
    }//GEN-LAST:event_playBtnActionPerformed
    
    
    
    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_windowMenuMenuSelected
        
        windowMenu.removeAll();
        
        windowMenu.add(closeWindowMI);
        
        windowMenu.add(cascadeMI);
        
        windowMenu.add(windowMenuSeparator);
        
        for(WindowMenuItem w : WindowRegistry.getWindows()) {
            
            windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
            
        }
        
        windowMenu.repaint();
        
    }//GEN-LAST:event_windowMenuMenuSelected
    

private void chordToneWeightFieldFocusLost(java.awt.event.FocusEvent evt)   
    {
        verifyTriageFields();
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
        
    }

    void FillProbsButtonActionPerformed(java.awt.event.ActionEvent evt)
    {

    }
    
    private void openGeneratorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openGeneratorButtonActionPerformed

    {//GEN-HEADEREND:event_openGeneratorButtonActionPerformed
        
        lickGeneratorMIActionPerformed(evt);
        
    }//GEN-LAST:event_openGeneratorButtonActionPerformed
    
    

    /**
     * Sets the abstract melody field (formerly called "rhythm" field).
     @param string
    
    public void setRhythmFieldText(String string)
    {
    rhythmField.setText(string);
    rhythmField.setCaretPosition(0);
    rhythmScrollPane.getViewport().setViewPosition(new Point(0,0));
    }
     */


    public void setLickGenStatus(String string)
    {
        if( logDialog != null )
        {
        logDialog.append(string+"\n");
        }
    }

    private void contourBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contourBtnActionPerformed
        
        changePrefTab(contourBtn, contourPreferences);
        
    }//GEN-LAST:event_contourBtnActionPerformed
    
    
    
    private void overrideFrameadviceWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_overrideFrameadviceWindowClosing

    {//GEN-HEADEREND:event_overrideFrameadviceWindowClosing
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_overrideFrameadviceWindowClosing
    
    
    
    private void overrideFrameadviceFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_overrideFrameadviceFocusGained

    {//GEN-HEADEREND:event_overrideFrameadviceFocusGained
                
    }//GEN-LAST:event_overrideFrameadviceFocusGained
    
    
    
    private void overrideFrameComponentHidden(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_overrideFrameComponentHidden

    {//GEN-HEADEREND:event_overrideFrameComponentHidden
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_overrideFrameComponentHidden
    
    
    
    private void formFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_formFocusGained

    {//GEN-HEADEREND:event_formFocusGained
        
        
        
    }//GEN-LAST:event_formFocusGained
    
    
    
    public void focus() {
        
        setVisible(true);
        
        toFront();
        
        requestFocus();
        
        staveRequestFocus();
        
    }
    
    
    
    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed
        
        setPrefsDialog();
        
    }//GEN-LAST:event_resetBtnActionPerformed
    
    
    
    private void drumMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drumMuteActionPerformed
        
        drumVolumeChanged();
        
    }//GEN-LAST:event_drumMuteActionPerformed
    
    
    
    private void drumVolumeChanged() {
        
        
        
        // set flag to for unsaved changes
        
        cm.changedSinceLastSave(true);
        
        
        
        int v = drumVolume.getValue();
        
        score.setDrumVolume(v);
        
        score.setDrumMuted(drumMute.isSelected());
        
        drumVolume.setEnabled(!drumMute.isSelected());
        
        
        
        if(score.getDrumMuted()) {
            
            midiSynth.setChannelVolume(score.getChordProg().getStyle().getDrumChannel(), 0);
            
        } else {
            
            midiSynth.setChannelVolume(score.getChordProg().getStyle().getDrumChannel(), v);
            
        }
        
    }
    
    
    
    private void bassVolumeChanged() {
        
        
        
        // set flag to for unsaved changes
        
        cm.changedSinceLastSave(true);
        
        
        
        int v = bassVolume.getValue();
        
        score.setBassVolume(v);
        
        score.setBassMuted(bassMute.isSelected());
        
        bassVolume.setEnabled(!bassMute.isSelected());
        
        
        
        if(score.getBassMuted())
            
            midiSynth.setChannelVolume(score.getChordProg().getStyle().getBassChannel(), 0);
        
        else
            
            midiSynth.setChannelVolume(score.getChordProg().getStyle().getBassChannel(), v);
        
    }
    
    
    
    private void melodyVolumeChanged() {
        
        
        
        // set flag to for unsaved changes
        
        cm.changedSinceLastSave(true);
        
        
        
        int v = melodyVolume.getValue();
        
        score.setMelodyVolume(v);
        
        score.setMelodyMuted(melodyMute.isSelected());
        
        melodyVolume.setEnabled(!melodyMute.isSelected());
        
        
        
        if(score.getMelodyMuted())
            
            midiSynth.setChannelVolume(score.getMelodyChannel(), 0);
        
        else
            
            midiSynth.setChannelVolume(score.getMelodyChannel(), v);
        
    }
    
    
    
public void chordVolumeChanged()
  {
    cm.changedSinceLastSave(true);

    int v = chordVolume.getValue();

    score.setChordVolume(v);

    score.setChordMuted(chordMute.isSelected());

    chordVolume.setEnabled(!chordMute.isSelected());

    Style style = score.getChordProg().getStyle();

    if( style != null )
      {
        if( score.getChordMuted() )
          {
            midiSynth.setChannelVolume(style.getChordChannel(), 0);
          }
        else
          {
            midiSynth.setChannelVolume(style.getChordChannel(), v);
          }
      }
  }
    
    
    
    private void setMuteAll(boolean muted) {
        
        score.setMasterVolumeMuted(muted);
        
        if(muted) {
            
            allMuteMixerBtn.setSelected(true);
            
            allMuteToolBarBtn.setSelected(true);
            
            allMuteToolBarBtn.setBackground(Color.red);
            
            allMuteToolBarBtn.setText("Play");
            
        } else {
            
            allMuteMixerBtn.setSelected(false);
            
            allMuteToolBarBtn.setSelected(false);
            
            allMuteToolBarBtn.setBackground(Color.green);
            
            allMuteToolBarBtn.setText("Mute");
         }
        
        masterVolumeChanged();
        
    }
    
    
    
    private void masterVolumeChanged() {
        
        int v = allVolumeMixerSlider.getValue();
        
        score.setMasterVolume(v);
        
        
        
        if(score.getMasterVolumeMuted()) {
            
            allVolumeMixerSlider.setEnabled(false);
            
            allVolumeToolBarSlider.setEnabled(false);
            
            
            
            midiSynth.setMasterVolume(0);
            
        } else {
            
            allVolumeMixerSlider.setEnabled(true);
            
            allVolumeToolBarSlider.setEnabled(true);
            
            
            
            midiSynth.setMasterVolume(v);
            
        }
        
    }
    
    
    
    private void setVolumeDefaults() {
        
        allVolumeToolBarSlider.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_ALL)));
        
        allVolumeMixerSlider.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_ALL)));
        
        melodyVolume.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_MELODY)));
        
        chordVolume.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_CHORDS)));
        
        bassVolume.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_BASS)));
        
        drumVolume.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_DRUMS)));
        
        entryVolume.setValue(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_ENTRY)));
        
    }
    
    
    
    private void allMuteMixerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allMuteMixerBtnActionPerformed
        
        setMuteAll(allMuteMixerBtn.isSelected());
        
    }//GEN-LAST:event_allMuteMixerBtnActionPerformed
    
    
    
    private void drumVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_drumVolumeStateChanged
        
        drumVolumeChanged();
        
    }//GEN-LAST:event_drumVolumeStateChanged
    
    
    
    private void allVolumeMixerSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_allVolumeMixerSliderStateChanged
        
        if(jSliderIgnoreStateChangedEvt)
            
            return;
        
        
        
        jSliderIgnoreStateChangedEvt = true;
        
        allVolumeToolBarSlider.setValue(allVolumeMixerSlider.getValue());
        
        jSliderIgnoreStateChangedEvt = false;
        
        
        
        masterVolumeChanged();
        
    }//GEN-LAST:event_allVolumeMixerSliderStateChanged
    
    
    
    private void preferencesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_preferencesMenuActionPerformed
    
    
    
    private void showMixer() {
        
        mixerDialog.pack();
        
        mixerDialog.setVisible(true);
        
    }
    
    
    
    private void mixerBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mixerBtnActionPerformed
        
        showMixer();
        
    }//GEN-LAST:event_mixerBtnActionPerformed
    
    
    
    private void allMuteToolBarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allMuteToolBarBtnActionPerformed
        
        setMuteAll(allMuteToolBarBtn.isSelected());
        
        staveRequestFocus();
        
    }//GEN-LAST:event_allMuteToolBarBtnActionPerformed
    
    
    
    private void allVolumeToolBarSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_allVolumeToolBarSliderStateChanged
        
        if(jSliderIgnoreStateChangedEvt)
            
            return;
        
        
        
        jSliderIgnoreStateChangedEvt = true;
        
        allVolumeMixerSlider.setValue(allVolumeToolBarSlider.getValue());
        
        jSliderIgnoreStateChangedEvt = false;
        
        
        
        masterVolumeChanged();
        
        
        
        if(!allVolumeMixerSlider.getValueIsAdjusting()) {
            
            staveRequestFocus();
            
        }
        
    }//GEN-LAST:event_allVolumeToolBarSliderStateChanged
    
    
    
    private void entryMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entryMuteActionPerformed
        
        entryVolume.setEnabled(!entryMute.isSelected());
        
        impro.setPlayEntrySounds(!entryMute.isSelected());
        
        if(getMode() == Mode.DRAWING) {
            
            drawingEntryMuted = entryMute.isSelected();
            
        }
        
    }//GEN-LAST:event_entryMuteActionPerformed
    
    
    
    private void entryVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_entryVolumeStateChanged
        
        ImproVisor.setEntryVolume(entryVolume.getValue());
        
    }//GEN-LAST:event_entryVolumeStateChanged
    
    
    
    private void chordMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordMuteActionPerformed
        
        chordVolumeChanged();
        
    }//GEN-LAST:event_chordMuteActionPerformed
    
    
    
    private void chordVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chordVolumeStateChanged
        
        chordVolumeChanged();
        
    }//GEN-LAST:event_chordVolumeStateChanged
    
    
    
    private void melodyMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_melodyMuteActionPerformed
        
        melodyVolumeChanged();
        
    }//GEN-LAST:event_melodyMuteActionPerformed
    
    
    
    private void melodyVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_melodyVolumeStateChanged
        
        melodyVolumeChanged();
        
    }//GEN-LAST:event_melodyVolumeStateChanged
    
    
    
    private void bassVolumeStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_bassVolumeStateChanged
        
        bassVolumeChanged();
        
    }//GEN-LAST:event_bassVolumeStateChanged
    
    
    
    private void bassMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassMuteActionPerformed
        
        
        
        bassVolumeChanged();
        
    }//GEN-LAST:event_bassMuteActionPerformed
    
    
    
    private void midiBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midiBtnActionPerformed
        
        changePrefTab(midiBtn, midiPreferences);
        
    }//GEN-LAST:event_midiBtnActionPerformed
    
    
    
    private void styleBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleBtnActionPerformed
        
        changePrefTab(styleBtn, stylePreferences);
       
    }//GEN-LAST:event_styleBtnActionPerformed
    
    
    
    private void leadsheetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leadsheetBtnActionPerformed
        
        changePrefTab(leadsheetBtn, leadsheetPreferences);
        
    }//GEN-LAST:event_leadsheetBtnActionPerformed
    
    
    
    private void globalBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalBtnActionPerformed
        
        changePrefTab(globalBtn, globalPreferences);
        
    }//GEN-LAST:event_globalBtnActionPerformed
    
    
    
    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        
        resetDrawingPrefs();
        
        preferencesDialog.setVisible(false);

        requestFocusInWindow();
    }//GEN-LAST:event_cancelBtnActionPerformed
    
    
    
    private void savePrefsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePrefsBtnActionPerformed
        Preferences.setPreference(Preferences.DEFAULT_CHORD_FONT_SIZE, "" + newChordFontSize);
        savePrefs(); 
        
    }//GEN-LAST:event_savePrefsBtnActionPerformed
    
    
        
    
    
    private Component currentPrefTab = null;
    
    private JToggleButton currentPrefButton = null;
    
    private Component currentGenTab = null;
    private JToggleButton currentGenButton = null;
    
    
    
    /* Used to provide access to switching prefences tabs from another class file
     
     */
    
    public enum PrefTab { MIDI };
    
    void changePrefTab(PrefTab panel) {
        
        switch(panel) {
            
            case MIDI:
                
                changePrefTab(midiBtn, midiPreferences);
                
                break;
                
        }
        
    }
    
    
    
    void changePrefTab(JToggleButton button, JPanel tab) {
        
        button.setSelected(true);
        
        currentPrefButton = button;

        
        currentPrefTab = tab;

        //System.out.println("setting tab to " + tab);
        
        preferencesScrollPane.setViewportView(tab);
        
        preferencesDialog.setSize(preferencesDialogDimension);

        preferencesDialog.repaint();
        
    }
    
    void changeGenTab(JToggleButton button, JPanel tab) {
        currentGenButton = button;
        
        if(currentGenTab == tab) {
            return;
        }
        
        currentGenTab = tab;
        //generatorScrollPane.setViewportView(tab);
        
        lickgenFrame.pack();
        lickgenFrame.repaint();
        
    }
    
    
    
    public JDialog getPreferencesDialog() {
        
        return preferencesDialog;
        
    }
    
    
    
    private void echoMidiCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_echoMidiCheckBoxActionPerformed
        
        midiManager.setEcho(echoMidiCheckBox.isSelected());
        
    }//GEN-LAST:event_echoMidiCheckBoxActionPerformed
    
    
    
    private void midiInComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midiInComboBoxActionPerformed
        
        devicesChanged();
        
    }//GEN-LAST:event_midiInComboBoxActionPerformed
    
    
    
    private void midiOutComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_midiOutComboBoxActionPerformed
        
        devicesChanged();
        
    }//GEN-LAST:event_midiOutComboBoxActionPerformed
    
    
    
    private void reloadDevicesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadDevicesActionPerformed
        
        midiManager.findInstalledDevices();
        
        devicesChanged();
        
    }//GEN-LAST:event_reloadDevicesActionPerformed
    
    
    
    private void devicesChanged() {
        
//        MidiDevice.Info oldInDevice = midiManager.getInDeviceInfo();
        
//        Object oldOutDevice = midiManager.getOutDeviceInfo();
        
        
        
//        midiManager.clearErrorMsgLog();
        
        midiManager.setInDevice((MidiDevice.Info) midiInComboBox.getSelectedItem());
        
        
        
        
        
//        midiManager.clearErrorMsgLog();
        
        midiManager.setOutDevice(midiOutComboBox.getSelectedItem());
        
        
        
        refreshMidiStatus();
        
        
        
//        // did the device change:
        
//        boolean inChanged = oldInDevice != midiManager.getInDeviceInfo();
        
//        boolean outChanged = oldOutDevice != midiManager.getOutDeviceInfo();
        
//        boolean nothingChanged = !inChanged && !outChanged;
        
//
        
//
        
//        /* In no particular order:
        
//         * 1. if there was an error and nothing changed, we show the error
        
//         * 2. if there was an error on a device that didn't change, and
        
//         *    the reload of devices was caused by a change to a different
        
//         *    device, then we don't show the error since it probably isn't a
        
//         *    new error
        
//         * 3. if there was an error on a device, and the user just changed that
        
//         *    device, we should always show the error
        
//         */
        
//
        
//        if((outChanged || nothingChanged)
        
//                    && midiOutComboBox.getSelectedItem() != null
        
//                    && midiManager.getOutDevice() == null
        
//                || (inChanged || nothingChanged)
        
//                    && midiInComboBox.getSelectedItem() != null
        
//                    && midiManager.getInDevice() == null)
        
//        {
        
//            JOptionPane.showMessageDialog(preferencesDialog, "Error setting device:\n" + midiManager.getError(), "Device not ready", JOptionPane.ERROR_MESSAGE);
        
//        }
        
        
        
//        private void showErrors() {
        
//            if(midiManager.getError() == null) {
        
//                JOptionPane.showMessageDialog(preferencesDialog, "No errors occurred during the last attempt to load the MIDI devices", "No Errors", JOptionPane.INFORMATION_MESSAGE);
        
//            } else {
        
//                JOptionPane.showMessageDialog(preferencesDialog, midiManager.getError(), "Error Log", JOptionPane.ERROR_MESSAGE);
        
//            }
        
//        }
    }
    
    
    
    String okMsg = "<html>Status: <em><font color='green'>Device ready</font></em></html>";
    
    String noDevSelectedMsg = "<html>Status: <em><font color='red'>No Device Selected</font></em></html>";
    
    String noDev = "<html>No devices found.</html>";
    
    String failMsgStart = "<html>Status: <em><font color='red'>";
    
    String failMsgEnd = "</font></em></html>";
    
    public void refreshMidiStatus() {
        
        // get midi latency
        
        midiLatencyTF.setText(String.valueOf(midiRecorder.getLatency()));
        
        
        
        // update midiIn status:
        
        if(midiOutComboBox.getItemCount() == 0) {
            
            midiOutPanel.remove(midiOutComboBox);
            
            
            
            // no MIDI out devices?  Shouldn't be here anymore since we have a default device now
            
            midiOutStatus.setText(noDev);
            
        } else {
            
            if(!midiOutComboBox.isVisible()) {
                
                midiOutPanel.remove(midiOutStatus);
                
                midiOutPanel.add(midiOutComboBox);
                
                midiOutPanel.add(midiOutStatus);
                
            }
            
            
            
            midiOutStatus.setText(midiManager.getOutDeviceError().equals("")?
                
                okMsg
                    
                    : failMsgStart + midiManager.getOutDeviceError() + failMsgEnd);
            
        }
        
        
        
        // update midiOut status:
        
        if(midiInComboBox.getItemCount() == 0) {
            
            midiInPanel.remove(midiInComboBox);
            
            midiInStatus.setText(noDev);
            
        } else {
            
            if(!midiInComboBox.isVisible()) {
                
                midiInPanel.remove(midiInStatus);
                
                midiInPanel.add(midiInComboBox);
                
                midiInPanel.add(midiInStatus);
                
            }
            
            
            
            midiInStatus.setText((midiManager.getInDevice() != null)?
                
                okMsg
                    
                    : failMsgStart + midiManager.getInDeviceError() + failMsgEnd);
            
        }
        
    }
    
    
    
public class VoicingTableModel
    extends AbstractTableModel
{

private String[] columnNames =
  {
    "Chord", "Name", "Type", "Voicing", "Extension"
  };

private Class[] columnClasses =
  {
    ChordSymbol.class,
    String.class,
    String.class,
    Polylist.class,
    Polylist.class
  };

private String chordRoot = "";

private Vector<Polylist> data = new Vector<Polylist>();


public VoicingTableModel()
  {

    data.add(Polylist.list("", "", "", "", "", false));
  }

public void setChordRoot(String root, String bass, NoteSymbol low,
                         NoteSymbol high)
  {

    chordRoot = root;

    Style s = score.getChordProg().getStyle().copy();

    s.setChordHigh(high);

    s.setChordLow(low);

    data = Advisor.getVoicingTable(chordRoot, bass, s);

    fireTableDataChanged();

  }
        
 public String getChordRoot()
  {
    return chordRoot;
  }

public int getColumnCount()
  {
    return columnNames.length;
  }

public String getColumnName(int col)
  {
    return columnNames[col];
  }

public Class getColumnClass(int col)
  {
    return columnClasses[col];
  }

public int getRowCount()
  {
    return data.size();
  }

public Object getValueAt(int row, int col)
  {
    Object o = data.get(row).nth(col);

    if( o instanceof Polylist && !((Polylist) o).nonEmpty() )
      {
        return "";
      }
    else
      {
        return o;
      }
  }
}
    
    
 public class StyleListModel
    extends AbstractListModel
{

private Polylist styles = null;

public int getSize()
  {
    styles = Advisor.getAllStyles();
    if( styles == null )
      {
        return 0;
      }
    return styles.length();

  }

public Object getElementAt(int index)
  {

    return (Style) ((Polylist) styles.nth(index)).second();

  }

public void reset()
  {

    fireContentsChanged(this, 0, getSize());

  }

}

public class SectionListModel
    extends AbstractListModel
{

public int getSize()
  {

    /**
     *
     * Stephen TODO: null pointer exception raised here
     *
     */
    if( sectionInfo == null )
      {
        return 0;
      }

    return sectionInfo.size();

  }

public Object getElementAt(int index)
  {

    return sectionInfo.getInfo(index);

  }

public void reset()
  {

    sectionInfo = score.getChordProg().getSectionInfo().copy();

    refresh();

  }

public void refresh()
  {

    fireContentsChanged(this, 0, sectionInfo.size());

    int index = sectionList.getSelectedIndex();

    if( index > -1 )
      {

        styleList.setSelectedValue(sectionInfo.getStyle(index), true);

        measureTF.setText(String.valueOf(sectionInfo.getSectionMeasure(index)));

      }

    delSectionButton.setEnabled(sectionInfo.size() > 1);

    setNewSectionEnabled();

  }

}

public class StyleComboBoxModel
    extends AbstractListModel
    implements ComboBoxModel
{

Polylist styles;

int len;

public int getSize()
  {
    styles = Advisor.getAllStyles();

    if( styles == null )
      {
        return 0;
      }

    len = styles.length();

    return len;
  }

public Object getElementAt(int index)
  {

    return (Style) ((Polylist) styles.nth(index)).second();
  }

private Object selectedItem = null;

public void setSelectedItem(Object anItem)
  {

    selectedItem = anItem;
  }

public Object getSelectedItem()
  {

    return selectedItem;

  }

}

public class MidiDeviceChooser
    extends AbstractListModel
    implements ComboBoxModel
{

private Vector<MidiDevice.Info> devices;

private Object selectedItem = null;

public MidiDeviceChooser(Vector<MidiDevice.Info> devices)
  {

    this.devices = devices;

  }

public int getSize()
  {

    return devices.size();

  }

public Object getElementAt(int index)
  {

    Object o = devices.elementAt(index);

    if( o == null )
      {

        return midiManager.defaultDeviceLabel;

      }

    return o;

  }

public void setSelectedItem(Object anItem)
  {

    selectedItem = anItem;

  }

public Object getSelectedItem()
  {

    return selectedItem;

  }
        
}
    
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
  
    
    /*    */
    
    
    /**
     *
     * flag to reset position of dialog the first time it is displayed only
     *
     * means that the dialog gets centered the first time it is shown,
     *
     * when the dialog is shown a second time, it remembers it's last position
     *
     */
    
    private boolean initLocationPreferencesDialog = false;
    
    private void showPreferencesDialog() {
        
        setPrefsDialog();
        
        // center dialog only the first time it is shown
        
        if(!initLocationPreferencesDialog) {

           preferencesDialog.setLocationRelativeTo(this);
            
           initLocationPreferencesDialog = true;

        }
        
        showFakeModalDialog(preferencesDialog);
    }
    
    
    
    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        
        stopPlaying();
        if (keyboard != null && keyboard.isVisible())
        {
            keyboard.setPlayback(false);
            clearVoicingEntryTF();
        }
        
    }//GEN-LAST:event_stopBtnActionPerformed
    
    
    
  private void setTempo(double value)
    {
    if( value >= MIN_TEMPO && value <= MAX_TEMPO )
      {
      tempoTF.setText("" + value); // keep these in sync
      tempoSet.setText("" + value);

      tempoSlider.setValue((int)value);

      score.setTempo(value);

      //System.out.println("notate setTempo to " + value);

      midiSynth.setTempo((float)value);
      }
    else
      {
      ErrorLog.log(ErrorLog.COMMENT,
              "The tempo must be in the range " + MIN_TEMPO + " to " + MAX_TEMPO 
            + ",\nusing default: " + getDefaultTempo() + ".");
      return;
      }
    }

    
    
    private void tempoSetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tempoSetFocusLost
        
        updateTempoFromTextField();
        
        staveRequestFocus();
        
    }//GEN-LAST:event_tempoSetFocusLost
    
    
        
    
    
private void updateTempoFromTextField()
  {
    try
      {
        double value = Double.valueOf(tempoSet.getText()).doubleValue();

        jSliderIgnoreStateChangedEvt = true;

        setTempo(value);

        jSliderIgnoreStateChangedEvt = false;
      }
    catch( NumberFormatException e )
      {
      tempoSet.setForeground(Color.RED);

      return;
      }

    tempoSet.setForeground(Color.BLACK);
  }
    
    
    
    private void tempoSetMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tempoSetMousePressed

      tempoSet.requestFocusInWindow();
        
    }//GEN-LAST:event_tempoSetMousePressed

  private void updateLoopFromTextField()
    {
    try
      {
      int value = Integer.valueOf(loopSet.getText()).intValue();

      setLoopCount(value);
      }
    catch( NumberFormatException e )
      {
      loopSet.setForeground(Color.RED);

      return;
      }
    loopSet.setForeground(Color.BLACK);
    }

    private void standardToolbarComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_standardToolbarComponentMoved
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_standardToolbarComponentMoved
    
  /**
   * Sets the number of bars in a chorus, per given text string.
   @param text
   */
    
  public void setPartBars(String text)
    {
    int scoreBarLength = Integer.parseInt(text);
    if( scoreBarLength >= score.getActiveBarsInChorus() )
      {
      // Lengthening score is always ok.
      setBars(scoreBarLength);
      setStatus("Chorus length set to " + scoreBarLength);
      repaintAndStaveRequestFocus();
      return;
      }

    if( scoreBarLength < 1 )
      {
      setStatus("You're kidding, right?");
      return;
      }

    // Truncating score might not be ok.  Ask the user.

    truncatePartDialog.setSize(new Dimension(400, 300));
    truncatePartDialog.setLocation(200, 200);
    truncatePartDialog.setLocationRelativeTo(this);
    truncatePartDialog.setVisible(true);

    // Let the user decide whether or not to cancel truncation.

    if( cancelTruncation )
      {
      setBars(score.getBarsPerChorus());
      }
    else
      {
      setBars(scoreBarLength);
      setStatus("Chorus length set to " + scoreBarLength);
      }
    repaintAndStaveRequestFocus();
    }
        
    
 private void repaintAndStaveRequestFocus()
 {
   repaint();
   staveRequestFocus();
 }
    
    
    private void addTabBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabBtnActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_addTabBtnActionPerformed
    
    
    
    private void freezeLayoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_freezeLayoutButtonActionPerformed
        
        toggleFreezeLayout();
        
    }//GEN-LAST:event_freezeLayoutButtonActionPerformed
    
    
    
    private Color adviceBtnColorOpen = Color.green;
    
    private Color adviceBtnColorClosed = new Color(238, 212, 212);
    
    private void showAdviceButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showAdviceButtonActionPerformed
        
        boolean value = showAdviceButton.isSelected();
        
        Trace.log(2, "AdviceButton selected = " + value);
        
        impro.setShowAdvice(value);
        
        showAdviceButton.setBackground(value? adviceBtnColorOpen : adviceBtnColorClosed);
        
        if( value ) {
            
            redoAdvice();
            
        } else {
            
            closeAdviceFrame();
            
        }
        
    }//GEN-LAST:event_showAdviceButtonActionPerformed
    
    
    
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus
        
        //System.out.println("notate focus gained");
        imp.ImproVisor.windowHasFocus(this);
        
        checkFakeModalDialog();
        
    }//GEN-LAST:event_formWindowGainedFocus
    
    
    
    private void insertRestMeasureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertRestMeasureActionPerformed
        
        Part singleMeasure = new Part(measureLength);
        
        int measureStart = (getCurrentSelectionStart() / measureLength) * measureLength;
        
        cm.execute(new InsertPartCommand(this, getCurrentOrigPart(), measureStart, singleMeasure));
        
    }//GEN-LAST:event_insertRestMeasureActionPerformed
    
    
    
    private void drawButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawButtonActionPerformed

        Stave stave = getCurrentStave();

        if( getMode() != Mode.DRAWING )
          {
            setMode(Mode.DRAWING);

            preDrawingEntryMuted = entryMute.isSelected();

            entryMute.setSelected(drawingEntryMuted);

            entryMuteActionPerformed(null);

            drawButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "graphics/toolbar/cursor.gif")));
          }
        else
          {
            setMode(Mode.NORMAL);

            stave.clearCurvePoints();

            entryMute.setSelected(preDrawingEntryMuted);

            entryMuteActionPerformed(null);

            drawButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                "graphics/toolbar/pencil.gif")));
          }

        stave.requestFocusInWindow();

        stave.getActionHandler().setCursor();

        stave.repaint();        
    }//GEN-LAST:event_drawButtonActionPerformed
    
    
    private void advicePMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advicePMIActionPerformed
        
        adviceMIActionPerformed(null);

    }//GEN-LAST:event_advicePMIActionPerformed
        
    
    private void playMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playMenuActionPerformed
    
    }//GEN-LAST:event_playMenuActionPerformed
    
    
    
    private void newBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBtnActionPerformed
   
    }//GEN-LAST:event_newBtnActionPerformed
        
        
    
        
    
    
    private void adviceWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_adviceWindowClosing
        
        showAdviceButton.setSelected(false);
        
        showAdviceButtonActionPerformed(null);
        
    }//GEN-LAST:event_adviceWindowClosing
    
    
    private void adviceFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_adviceFocusGained

        setStatus("Select advice option.");
        
    }//GEN-LAST:event_adviceFocusGained
    
    
    private void programStatusTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_programStatusTFFocusLost
         
    }//GEN-LAST:event_programStatusTFFocusLost
    
    
    
    private void programStatusTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_programStatusTFActionPerformed
        
    }//GEN-LAST:event_programStatusTFActionPerformed
    
    
    
    private void exportAllToMidiActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_exportAllToMidiActionPerformed

    {//GEN-HEADEREND:event_exportAllToMidiActionPerformed
        midfc.setDialogTitle("Export Leadsheet to Midi:");

        exportToMidi(ALL);
    }//GEN-LAST:event_exportAllToMidiActionPerformed
    
    
    
    private void fileMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fileMenuActionPerformed

    {//GEN-HEADEREND:event_fileMenuActionPerformed
          
    }//GEN-LAST:event_fileMenuActionPerformed
    
    
    
    private void enterMeasuresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterMeasuresActionPerformed
    
    }//GEN-LAST:event_enterMeasuresActionPerformed
    
    
    
    private void helpMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuActionPerformed
        
                    }//GEN-LAST:event_helpMenuActionPerformed
    
    
    
    private void enterLickTitleAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterLickTitleAction
        
        okSaveButtonActionPerformed(evt);
        
    }//GEN-LAST:event_enterLickTitleAction
    
    
    
    private void cellRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cellRadioButtonActionPerformed
        
        saveSelectionMode = ExtractMode.CELL;
        
    }//GEN-LAST:event_cellRadioButtonActionPerformed
    
    
    
    private void idiomRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idiomRadioButtonActionPerformed
        
        saveSelectionMode = ExtractMode.IDIOM;
        
    }//GEN-LAST:event_idiomRadioButtonActionPerformed
    
    
    
    private void quoteRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quoteRadioButtonActionPerformed
        
        saveSelectionMode = ExtractMode.QUOTE;
        
    }//GEN-LAST:event_quoteRadioButtonActionPerformed
    
    
    
    private void lickRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lickRadioButtonActionPerformed
        
        saveSelectionMode = ExtractMode.LICK;
        
    }//GEN-LAST:event_lickRadioButtonActionPerformed
    
    
    
    private void saveDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveDuplicateActionPerformed
        
        ignoreDuplicateLick = SAVE;
        
        duplicateLickDialog.setVisible(false);
        
    }//GEN-LAST:event_saveDuplicateActionPerformed
    
    
    
    private void duplicateLickDialogKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_duplicateLickDialogKeyPressed
   
    }//GEN-LAST:event_duplicateLickDialogKeyPressed
    
    
    
    private void duplicateLickDialogComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_duplicateLickDialogComponentShown

    }//GEN-LAST:event_duplicateLickDialogComponentShown
    
    
    
    private void ignoreDuplicateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ignoreDuplicateActionPerformed
        
        ignoreDuplicateLick = IGNORE;
        
        duplicateLickDialog.setVisible(false);
        
    }//GEN-LAST:event_ignoreDuplicateActionPerformed
    
    
    
    private void colorTonesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_colorTonesActionPerformed

    {//GEN-HEADEREND:event_colorTonesActionPerformed
     
    }//GEN-LAST:event_colorTonesActionPerformed
    
    
    
    private void purgeCacheActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_purgeCacheActionPerformed

    {//GEN-HEADEREND:event_purgeCacheActionPerformed
        
        Advisor.purgeCache();
        
    }//GEN-LAST:event_purgeCacheActionPerformed
    
    
    
    private void enterMeasuresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterMeasuresKeyPressed
        
        if( evt.getKeyCode() == KeyEvent.VK_ENTER )
          {

            Trace.log(2, "enter measures key pressed");

            enterMeasuresCore();
          }
    }//GEN-LAST:event_enterMeasuresKeyPressed
    
    
    
    private void okMeasBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okMeasBtnActionPerformed
        
        Trace.log(2, "enter measures Ok pressed");
        
        enterMeasuresCore();
        
    }//GEN-LAST:event_okMeasBtnActionPerformed
    
    
    
    /**
     *
     * flag to reset position of dialog the first time it is displayed only
     *
     * means that the dialog gets centered the first time it is shown,
     *
     * when the dialog is shown a second time, it remembers it's last position
     *
     */
    
    private boolean initLocationLickGenerator = false;
    
    private void lickGeneratorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_lickGeneratorMIActionPerformed

    {//GEN-HEADEREND:event_lickGeneratorMIActionPerformed
      redrawTriage();

      lickgenFrame.redoScales();

      lickgenFrame.resetTriageParameters(true);

      lickgenFrame.pack();

      // center dialog only the first time it is shown

      if( !initLocationLickGenerator )
        {
        lickgenFrame.setLocationRelativeTo(this);

        initLocationLickGenerator = true;

        WindowRegistry.registerWindow(lickgenFrame);

        lickgenFrame.setLocation(
            (int) this.getLocation().getX() + WindowRegistry.defaultXnewWindowStagger,
            (int) this.getLocation().getY() + WindowRegistry.defaultYnewWindowStagger);
        }

      lickgenFrame.setVisible(true);

      entryMuteActionPerformed(null);
    }//GEN-LAST:event_lickGeneratorMIActionPerformed
    
    
    
    /**
     *
     * flag to reset position of dialog the first time it is displayed only
     *
     * means that the dialog gets centered the first time it is shown,
     *
     * when the dialog is shown a second time, it remembers it's last position
     *
     */
    
    private boolean initLocationLeadsheetEditor = false;
    
    private void openLeadsheetEditorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openLeadsheetEditorMIActionPerformed

    {//GEN-HEADEREND:event_openLeadsheetEditorMIActionPerformed
        
        // center dialog only the first time it is shown
        
        if(!initLocationLeadsheetEditor) {
            
            leadsheetEditor.setLocationRelativeTo(this);
            
            initLocationLeadsheetEditor = true;
            
        }
        
        leadsheetEditor.setSize(leadsheetEditorDimension);
        leadsheetEditor.fillEditor();
        leadsheetEditor.setVisible(true);
        
    }//GEN-LAST:event_openLeadsheetEditorMIActionPerformed
    
    
    
    private void revertLeadsheetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertLeadsheetActionPerformed
        
        revertLeadsheet(evt);
        
    }//GEN-LAST:event_revertLeadsheetActionPerformed
    
    
    
    private void contractMelodyBy3ActionPerformed11(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractMelodyBy3ActionPerformed11
        
        timeWarpMelody(1, 3);
        
    }//GEN-LAST:event_contractMelodyBy3ActionPerformed11
    
    
    
    private void expandMelodyBy3ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandMelodyBy3ActionPerformed1
        
        timeWarpMelody(3, 1);
        
    }//GEN-LAST:event_expandMelodyBy3ActionPerformed1
    
    
    
    private void contractMelodyBy2ActionPerformed1(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_contractMelodyBy2ActionPerformed1
        
        timeWarpMelody(1, 2);
        
    }//GEN-LAST:event_contractMelodyBy2ActionPerformed1
    
    
    
    private void expandMelodyBy2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_expandMelodyBy2ActionPerformed
        
        timeWarpMelody(2, 1);
        
    }//GEN-LAST:event_expandMelodyBy2ActionPerformed
    
    
    
    private void invertMelodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_invertMelodyActionPerformed
        
        invertMelody();
        
    }//GEN-LAST:event_invertMelodyActionPerformed
    
    
    
    private void reverseMelodyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reverseMelodyActionPerformed
        
        reverseMelody();
        
    }//GEN-LAST:event_reverseMelodyActionPerformed
    
    
    
    private void saveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveBtnActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_saveBtnActionPerformed
    
    
    private void copyChordSelectionToTextWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordSelectionToTextWindowActionPerformed
        
        String saveSelection = getCurrentStave().getSaveSelection(lickTitle, Stave.ExtractMode.CHORDS);
        
        textEntry.setText(saveSelection.trim());
        
        staveRequestFocus();        
    }//GEN-LAST:event_copyChordSelectionToTextWindowActionPerformed
    
    
    
    private void copyMelodySelectionToTextWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMelodySelectionToTextWindowActionPerformed
        
        String saveSelection = getCurrentStave().getSaveSelection(lickTitle, Stave.ExtractMode.MELODY);
        
        textEntry.setText(saveSelection.trim());
        
        staveRequestFocus();        
    }//GEN-LAST:event_copyMelodySelectionToTextWindowActionPerformed
    
    
    
    private void copyBothSelectionToTextWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBothSelectionToTextWindowActionPerformed
        
        String saveSelection = getCurrentStave().getSaveSelection(lickTitle, Stave.ExtractMode.BOTH);
        
        textEntry.setText(saveSelection.trim());
        
        staveRequestFocus();        
    }//GEN-LAST:event_copyBothSelectionToTextWindowActionPerformed
    
    
    
    private void saveSelectionAsLickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSelectionAsLickActionPerformed
        
        openSaveLickFrame();        
    }//GEN-LAST:event_saveSelectionAsLickActionPerformed
    
    
    
    private void enterLickTitleGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_enterLickTitleGetsFocus
        
        disableAccelerators();        // to prevent keys from triggering        
    }//GEN-LAST:event_enterLickTitleGetsFocus
    
    
    
    private void okSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okSaveButtonActionPerformed
        
        lickTitle = enterLickTitle.getText();
        
        String saveSelection = getCurrentStave().getSaveSelection(lickTitle, saveSelectionMode, 0);
        
        saveLick(saveSelection);        
    }//GEN-LAST:event_okSaveButtonActionPerformed
    
    
    
    // Return an array of labels that have appropriate enharmonics
    
    // for the black notes.
    
    public String[] getNoteLabels(int location)
    
    {
        
        String[] notes = new String[12];
        
        Polylist preferredScale = lickgen.getPreferredScale();
        
        Polylist scaleTones = new Polylist();
        
        Polylist scales = chordProg.getCurrentChord(location).getScales();
        
        
        
        if (scales == null || scales.isEmpty() || preferredScale.isEmpty() ||
                
                ((String)preferredScale.second()).equals(NONE))
            
            scaleTones = Polylist.nil;
        
        else if (((String)preferredScale.second()).equals(FIRST_SCALE))
            
            scaleTones = chordProg.getCurrentChord(location).getFirstScale();
        
        else
            
            scaleTones = Advisor.getScale((String)preferredScale.first(), (String)preferredScale.second());
        
        
        
        boolean[] enh = score.getCurrentEnharmonics(location, scaleTones.append(chordProg.getCurrentChord(location).getPriority()));
        
        
        
        notes[0] = "C";
        
        notes[2] = "D";
        
        notes[4] = "E";
        
        notes[5] = "F";
        
        notes[7] = "G";
        
        notes[9] = "A";
        
        notes[11] = "B";
        
        
        
        if (enh[CSHARP] == true)
            
            notes[1] = "C#";
        
        else
            
            notes[1] = "Db";
        
        
        
        if (enh[DSHARP] == true)
            
            notes[3] = "D#";
        
        else
            
            notes[3] = "Eb";
        
        
        
        if (enh[FSHARP] == true)
            
            notes[6] = "F#";
        
        else
            
            notes[6] = "Gb";
        
        
        
        if (enh[GSHARP] == true)
            
            notes[8] = "G#";
        
        else
            
            notes[8] = "Ab";
        
        
        
        if (enh[ASHARP] == true)
            
            notes[10] = "A#";
        
        else
            
            notes[10] = "Bb";
        
        
        
        return notes;
        
    }
    
    
 /*
    // Make sure that the values in the probability fields are between 0.0 and 1.0
    
    public void verifyProbs()
    
    {
        
        for (int i = 0; i < lickPrefs.size(); ++i)
            
            for (int j = 0; j < 12; ++j)
                
                doubleFromTextField(lickPrefs.get(i)[j], 0.0, Double.POSITIVE_INFINITY, 1.0);
        
    }
 */
    
    
    // Make sure the user has entered acceptable values for each of the other fields   
    // in the triage frame.
    
    private void verifyTriageFields()
    
    {

        lickgenFrame.verifyTriageFields();
        lickgenFrame.setTotalBeats(totalBeats);

 
        getCurrentStave().repaint();
    }
        
    
    private void saveLick(String saveSelection)
    
    {
        
        saveLickFrame.setVisible(false);
        
        if( saveSelection != null )
            
        {
            
            Polylist selectionAsList = parseListFromString(saveSelection);
            
            
            
            if( adv.addUserRule(selectionAsList) )
                
            {
                
                saveAdviceActionPerformed(null);        // automatically save advice
                
                // lickSavedLabel.setText("Lick saved!");
                
            }
            
        }
        
        staveRequestFocus();
        
    }
    
    
    
    public void triageLick(String lickName, int grade) {
        
        String saveSelection = getCurrentStave().getSaveSelection(lickName, Stave.ExtractMode.LICK, grade);
        
        if(lickgenFrame.toCriticSelected()) {
            
            criticDialog.add(saveSelection, grade);
            
            criticDialog.setVisible(true);
            
        } else {
            
            saveLick(saveSelection);
            
        }
        
    }
    
    
    
    boolean isPowerOf2(int x)
    {
        // trust me, it works!
        return ((x > 0) && ((x & (x - 1)) == 0));
    }
    
    

    
    private MelodyPart makeLick(Polylist rhythm) {
        verifyTriageFields();
         
        if (rhythm == null || rhythm.isEmpty()) {
            ErrorLog.log(ErrorLog.SEVERE, "Null rhythm argument.  No lick will be generated.");
            return null;
        }
        
        lickgen.setProbs(lickgenFrame.readProbs());
        MelodyPart lick;
        
        int len = lickgen.parseLength(rhythm);
        int scoreLen = score.getLength();
        int diff = len + getCurrentSelectionStart() - scoreLen;
        if (diff > 0 ) {
            ErrorLog.log(ErrorLog.WARNING, "Lick is " + diff + " slots longer than available space of " + scoreLen + " slots.  Aborting.");
              
            return null;
        }
        
        // Fill in a melody according to the provided rhythm.
        // FIX - Currently, the lick generator doesn't support half beats; thus,
        // it can only generate things in terms of number of quarter notes.
        // This is why BEAT is getting passed into the generator.

        return lickgenFrame.fillMelody(BEAT, rhythm, chordProg, getCurrentSelectionStart());
    }
    
    private void putLick(MelodyPart lick) {
        if (lick == null) {
            ErrorLog.log(ErrorLog.WARNING, "No lick was generated.");
            return;
        }
        
        // Figure out which enharmonics to use based on
        // the current chord and key signature.
        setLickEnharmonics(lick);
        
        // Paste the melody into the stave and play the selection.
        // We turn play off temporarily, or we get an erroneous sound
        // as ImproVisor plays the inserted note at the same time
        // it plays the selection.
        impro.setPlayEntrySounds(false);
        pasteMelody(lick);

        if( lickgenFrame.rectifySelected() )
         {
         rectifySelection(getCurrentStave(), getCurrentSelectionStart(), getCurrentSelectionEnd());
         }

        // Wait for playing to stop

        getCurrentStave().playSelection();
        impro.setPlayEntrySounds(true);
    }
    
     public void generateLick(Polylist rhythm) {
        MelodyPart lick = makeLick(rhythm);
        if(lickgenFrame.useHeadSelected())
            adjustLickToHead(lick);
        putLick(lick);
    }
    
    private void adjustLickToHead(MelodyPart lick) {
        Vector<Score> heads = lickgen.getHeadData();
        Score head = null;
        for(int i = 0; i < heads.size(); i++) {
            
            //select the head with matching title and length, if there is one
            if (heads.get(i).getTitle().equals(this.getTitle())  &&
                    heads.get(i).getBarsPerChorus() == this.score.getBarsPerChorus()) {
                
                head = heads.get(i);
            }
        }

        //if we don't have the head, leave the lick as it is
        
        if(head == null) {
            setLickGenStatus("No head available for this song");
            return;
        }
        

        MelodyPart headMelody = head.getPart(0);
        int start = getCurrentSelectionStart();
        int end = getCurrentSelectionEnd();
        
        
        
        //note in lick
        Note n = null;
        //tracks position in lick
        int position = 0;
        int oldpitch = 0;;
        
        int numChanged = 0;
        int numSame = 0;
        
        while(lick.getNextNote(position) != null) {
            n = lick.getNextNote(position);
            int duration = n.getRhythmValue();;
            int pitch = n.getPitch();
            int headPitch = headMelody.getPitchSounding(position+start);
            int nextIndex = lick.getNextIndex(position + n.getRhythmValue()-1);
            int nextPitch = -10;
            if(lick.getNote(nextIndex) != null)
                nextPitch = lick.getNote(nextIndex).getPitch();
            
            //don't create repeated notes
            if(headPitch != pitch && headPitch != oldpitch && headPitch != nextPitch) {
                if (Math.abs(headPitch - pitch) < 7 && Math.random() < 0.1 && duration >= 60 ||
                    Math.abs(headPitch - pitch) < 4 && Math.random() < 0.6 ||
                    Math.abs(headPitch - pitch) < 2 && Math.random() < 0.9)     {
                n.setPitch(headPitch);
                numChanged++;
                }
                numSame++;
            }
            else {
                numSame++;
            }         
            oldpitch = n.getPitch();
            position += n.getRhythmValue() -1 ;
            
        }
        
        //System.out.println(numChanged + " pitches changed out of " + (numChanged + numSame));
    }
    
    
    // Calculate the current lick enharmonics based on the chord progression
    
    // and they key signature.
    
    private void setLickEnharmonics(MelodyPart lick)
    
    {
        
        int index = 0;
        
        while (index < lick.size())
            
        {
            
            Note current = lick.getNote(index);
            if( current != null )
              {
              current.setEnharmonic(score.getCurrentEnharmonics(getCurrentSelectionStart() + index));
              index += current.getRhythmValue();
             }
            else
              {
              index++;
              }
        }
        
    }
    
    
    
    private void cancelLickTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelLickTitleActionPerformed
        
        saveLickFrame.setVisible(false);
        
        staveRequestFocus();
        
    }//GEN-LAST:event_cancelLickTitleActionPerformed
    
    
    
    public void openSaveLickFrame() {
        
        Trace.log(2, "Opening save lick selection frame");
        
        adviceFrame.setVisible(false);        // avoid interference of key strokes
        
        setStatus("Edit save information.");
        
        disableAccelerators();
        
        saveLickFrame.setSize(new Dimension(500, 250));
        
        saveLickFrame.setLocation(40, 60);
        
        saveLickFrame.setVisible(true);
        
        saveLickFrame.getRootPane().setDefaultButton(okSaveButton);
        
        saveLickFrame.requestFocus();
        
        enterLickTitle.requestFocusInWindow();
        
    }
    
    
    
    public String getLickTitle() {
        
        Trace.log(2, "Saved selection title is: " + lickTitle);
        
        return lickTitle;
        
    }
    
    
    
    private void saveAsAdviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsAdviceActionPerformed
        
        vocfc.setDialogTitle("Save Vocabluary As");
        
        
        
        File oldDirectory = vocfc.getCurrentDirectory();
        
        vocfc.setCurrentDirectory(vocabDir);
        
        
        // If never saved before, used the name specified in vocFile.
        
        // Otherwise use previous file.
        
        
        
        if( savedVocab == null ) {
            
            vocfc.setSelectedFile(new File(vocFile));
            
        }
        
        
        
        vocfc.resetChoosableFileFilters();
        
        vocfc.addChoosableFileFilter(new VocabFilter());
        
        
        
        if (vocfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            
            if (vocfc.getSelectedFile().getName().endsWith(vocabExt)) {
                
                
                
                cm.execute(new SaveAdviceCommand(vocfc.getSelectedFile(), adv));
                
                savedVocab = vocfc.getSelectedFile();
                
                
                
            } else {
                
                String file = vocfc.getSelectedFile().getAbsolutePath() + vocabExt;
                
                savedVocab = new File(file);
                
                cm.execute(new SaveAdviceCommand(savedVocab, adv));
                
            }
            
        }
        
        
        
    }//GEN-LAST:event_saveAsAdviceActionPerformed
    
    
    
    private void saveAdviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAdviceActionPerformed
        
        if (savedVocab != null)
            
            cm.execute(new SaveAdviceCommand(savedVocab, adv));
        
        else
            
            saveAsAdviceActionPerformed(evt);
        
        
        
    }//GEN-LAST:event_saveAdviceActionPerformed
    
    
        
    
    
    private void textEntryLosesFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textEntryLosesFocus
        
        textEntryLabel.setForeground(Color.red);
        
    }//GEN-LAST:event_textEntryLosesFocus
    
    
    
    private void textEntryGainsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textEntryGainsFocus
        
        textEntryLabel.setForeground(Color.black);
        
        setStatus("Enter text for chords and/or melody.");
        
    }//GEN-LAST:event_textEntryGainsFocus
    
    
    
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        
        textEntry.setText("");
        
    }//GEN-LAST:event_clearButtonActionPerformed
   
  private void setPlayTransposed()
    {
    setTransposition(getTransposition());
    }

    public int getTransposition()
    {
        return Integer.parseInt(transposeSpinner.getValue().toString());
    }
    
    private void tempoSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempoSetActionPerformed

      setTempo(doubleFromTextField(tempoSet, MIN_TEMPO, MAX_TEMPO, getDefaultTempo()));
      
                    }//GEN-LAST:event_tempoSetActionPerformed
    
    double getDefaultTempo()
    {
    return Double.parseDouble(Preferences.getPreference(Preferences.DEFAULT_TEMPO));
    }
    
     
    public void closeAdviceFrame()
    
    {
        
        showAdviceButton.setSelected(false);
        
        showAdviceButton.setBackground(adviceBtnColorClosed);
        
        adviceFrame.setVisible(false);
        
        staveRequestFocus();
        
    }
    
    
    
    private void openAdviceFrame()
    
    {
        
        showAdviceButton.setSelected(true);
        
        showAdviceButton.setBackground(adviceBtnColorOpen);
        
        adviceFrame.setVisible(true);
        
        //adviceTree.requestFocusInWindow();
        
        setStatus("Select advice option.");
        
    }
    
    
  private void redoAdvice()
    {
    Trace.log(2, "redo advice");

    getCurrentStaveActionHandler().redoAdvice(getCurrentSelectionStart());

    getCurrentStave().repaint();

    redrawTriage();
    }

  /*
  public void redoScales()
    {
    DefaultComboBoxModel dcbm = (DefaultComboBoxModel)scaleComboBox.getModel();

    dcbm.removeAllElements();

    Polylist scales = Advisor.getAllScales();

    dcbm.addElement("None");

    dcbm.addElement("Use First Scale");

    while( scales.nonEmpty() )
      {
      Polylist scale = (Polylist)scales.first();

      dcbm.addElement(scale.first());

      scales = scales.rest();
      }
    }
 */
    
    
  public void updateSelection()
    {
    if( getCurrentStave() == null )
      {
      return;
      }

    totalSlots =
            paddingSlots + getCurrentSelectionEnd() - getCurrentSelectionStart();
    
    totalBeats = Math.round(totalSlots/roundTo);
    
    totalSlots = (int)(totalBeats * BEAT);
    
    lickgenFrame.redrawTriage();

    lickgenFrame.setTotalBeats(totalBeats);
    }
    
    private void staveTypeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staveTypeMenuActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_staveTypeMenuActionPerformed
    
    
    
    public void transposeMelodyUpOctaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeMelodyUpOctaveActionPerformed
        
        getCurrentStave().transposeMelodyUpOctave();
        
    }//GEN-LAST:event_transposeMelodyUpOctaveActionPerformed
    
    
    
    private void textEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textEntryActionPerformed
        
        staveRequestFocus();
        
    }//GEN-LAST:event_textEntryActionPerformed
    
    
    
    private void openBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openBtnActionPerformed
        
        openLeadsheet(false);
        
    }//GEN-LAST:event_openBtnActionPerformed
    
    
    
    public void enterChordsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterChordsMIActionPerformed
        
        enterChords();
        
    }//GEN-LAST:event_enterChordsMIActionPerformed
    
    
    
    public void transposeBothDownSemitoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeBothDownSemitoneActionPerformed
        
        getCurrentStave().transposeChordsDownSemitone();
        
        getCurrentStave().transposeMelodyDownSemitone();
        
    }//GEN-LAST:event_transposeBothDownSemitoneActionPerformed
    
    
    
    public void transposeChordsDownSemitoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeChordsDownSemitoneActionPerformed
        
        getCurrentStave().transposeChordsDownSemitone();
        
    }//GEN-LAST:event_transposeChordsDownSemitoneActionPerformed
    
    
    
    public void transposeChordsUpSemitoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeChordsUpSemitoneActionPerformed
        
        getCurrentStave().transposeChordsUpSemitone();
        
    }//GEN-LAST:event_transposeChordsUpSemitoneActionPerformed
    
    
    
    public void transposeBothUpSemitoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeBothUpSemitoneActionPerformed
        
        getCurrentStave().transposeChordsUpSemitone();
        
        getCurrentStave().transposeMelodyUpSemitone();
        
    }//GEN-LAST:event_transposeBothUpSemitoneActionPerformed
    
    
    
    public void transposeMelodyDownOctaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeMelodyDownOctaveActionPerformed
        
        getCurrentStave().transposeMelodyDownOctave();
        
    }//GEN-LAST:event_transposeMelodyDownOctaveActionPerformed
    
    
    
    public void transposeMelodyDownSemitoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeMelodyDownSemitoneActionPerformed
        
        getCurrentStave().transposeMelodyDownSemitone();
        
    }//GEN-LAST:event_transposeMelodyDownSemitoneActionPerformed
    
    
    
    public void transposeMelodyUpSemitoneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeMelodyUpSemitoneActionPerformed
        
        getCurrentStave().transposeMelodyUpSemitone();
        
    }//GEN-LAST:event_transposeMelodyUpSemitoneActionPerformed
    
    
    
    public void pasteBothMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteBothMIActionPerformed
        
        pasteMelody();
        
        pasteChords();
        
    }//GEN-LAST:event_pasteBothMIActionPerformed
    
    
    
    public void copyBothMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBothMIActionPerformed
        
        copyMelody();
        
        copyChords();
        
    }//GEN-LAST:event_copyBothMIActionPerformed
    
    
    
    public void cutBothMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutBothMIActionPerformed
        
        cutChords();
        
        cutMelody();
        
    }//GEN-LAST:event_cutBothMIActionPerformed
    
    
    
    public void enterMelodyMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterMelodyMIActionPerformed
        
        enterMelody();
        
    }//GEN-LAST:event_enterMelodyMIActionPerformed
    
    
    
    public void enterBothMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterBothMIActionPerformed
        
        enterBoth();
        
    }//GEN-LAST:event_enterBothMIActionPerformed
    
    
    
    private void textEntryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textEntryKeyPressed

      textEntryLabel.setForeground(Color.black);

      if( evt.getKeyCode() == KeyEvent.VK_ENTER )
        {
        if( slotIsSelected() )
          {

          String enteredText = textEntry.getText();

          if( enteredText.length() > 0 )
            {
            cm.execute(
                    new SetChordsCommand(getCurrentSelectionStart(),
                    parseListFromString(enteredText),
                    chordProg,
                    partList.get(currTabIndex) ));
            }
          else
            {
            cm.execute(
                    new DeleteUnitsCommand(chordProg,
                    getCurrentSelectionStart(),
                    getCurrentSelectionEnd()));
            }

         redoAdvice();

          // set the menu and button states

          setItemStates();

          if( evt.isShiftDown() )
            {
            // If shift is down when entry pressed, shift focus to stave.

            staveRequestFocus();
            }
          }
        else
          {
          ErrorLog.log(ErrorLog.COMMENT,
                  "Text entry has no effect unless a unique grid line is selected.");
          }
        }
      else
        {
        disableAccelerators();        // to prevent keys from triggering
        }        
    }//GEN-LAST:event_textEntryKeyPressed
    
    
    public void saveLeadsheetMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLeadsheetMIActionPerformed
        
        saveLeadsheet();        
    }//GEN-LAST:event_saveLeadsheetMIActionPerformed
    
    
    
    /**
     *
     * Common point within notate for invoking saveLeadsheet command
     *
     */
    
    
    
    private boolean saveLeadsheet() {
        
        if (savedLeadsheet != null)
            
            return saveLeadsheet(savedLeadsheet, score);
        
        else
            
            return saveAsLeadsheet();
        
    }
    
    
    
    private boolean saveLeadsheet(File file, Score score) {
        
        SaveLeadsheetCommand s = new SaveLeadsheetCommand(file, score, cm);
        
        cm.execute(s);
        
        if(s.getError() instanceof IOException) {
            
            JOptionPane.showMessageDialog(this, "There was an IO Exception during saving:\n" + s.getError().getMessage(), "An error occurred when attmepting to save", JOptionPane.WARNING_MESSAGE);
            
            return false;
            
        }
        
        return true;
        
    }
    
      private void exportToMusicXML()
    {
 	    if( savedMusicXML != null )
 	      {
 	      musicxmlfc.setSelectedFile(savedMusicXML);
 	      }
 	    else
 	      {
 	      if( savedLeadsheet != null )
 	        {
 	        String name = savedLeadsheet.getName();
 	        if( name.endsWith(leadsheetExt) )
 	          {
 	          name = name.substring(0, name.length() - 3);
 	          }
 	        musicxmlfc.setSelectedFile(new File(name + musicxmlExt));
 	        }
 	      else
 	        {
 	    	  musicxmlfc.setSelectedFile(new File(musicxmlDef));
 	        }
 	      }

 	    if( musicxmlfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
 	      {
 	      File file;
 	      if( musicxmlfc.getSelectedFile().getName().endsWith(musicxmlExt) )
 	        {
 	        file = musicxmlfc.getSelectedFile();
 	        }
 	      else
 	        {
 	        String fileName = musicxmlfc.getSelectedFile().getAbsolutePath();
 	        fileName += musicxmlExt;
 	        file = new File(fileName);
 	        }

 	      ExportToMusicXMLCommand exportCmd = new ExportToMusicXMLCommand(file, score , scoreTab.getSelectedIndex(), getTransposition());
 	      cm.execute(exportCmd);

 	      if( exportCmd.getError() instanceof IOException )
 	        {
 	        JOptionPane.showMessageDialog(this,
 	                "There was an IO Exception during saving:\n" + exportCmd.getError().getMessage(),
 	                "An error occurred when attmepting to save",
 	                JOptionPane.WARNING_MESSAGE);
 	        return;
 	        }

 	      savedMusicXML = file;
 	      }
 	    }

    
  private void exportToMidi(int toExport)
    {
    if( savedMidi != null )
      {
      midfc.setSelectedFile(savedMidi);
      }
    else
      {
      if( savedLeadsheet != null )
        {
        String name = savedLeadsheet.getName();
        if( name.endsWith(leadsheetExt) )
          {
          name = name.substring(0, name.length() - 3);
          }
        midfc.setSelectedFile(new File(name + midiExt));
        }
      else
        {
        midfc.setSelectedFile(new File(midDef));
        }
      }

    if( midfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {
      File file;
      if( midfc.getSelectedFile().getName().endsWith(midiExt) )
        {
        file = midfc.getSelectedFile();
        }
      else
        {
        String fileName = midfc.getSelectedFile().getAbsolutePath();
        fileName += midiExt;
        file = new File(fileName);
        }

      ExportToMidiCommand exportCmd = new ExportToMidiCommand(file, score,
              toExport, getTransposition());
      cm.execute(exportCmd);

      if( exportCmd.getError() instanceof IOException )
        {
        JOptionPane.showMessageDialog(this,
                "There was an IO Exception during saving:\n" + exportCmd.getError().getMessage(),
                "An error occurred when attmepting to save",
                JOptionPane.WARNING_MESSAGE);
        return;
        }

      savedMidi = file;
      }
    }
    
    
    
    /**
     *
     * Toggles if pasting should always overwrite notes
     *
     */
    
    private void pasteOverMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteOverMIActionPerformed
        
        alwaysPasteOver = !alwaysPasteOver;
        
    }//GEN-LAST:event_pasteOverMIActionPerformed
    
    
    /**
     *
     * Resizes the toolBarPanel so that all toolbars can be shown
     *
     */
    
    private void playToolBarComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_playToolBarComponentMoved
        
        //calcToolBarSize();
        
    }//GEN-LAST:event_playToolBarComponentMoved
    
    
  /**
   *
   * Sets the part volume
   *
   */
  static private void invalidInteger(String text)
    {

    text = text.trim();

    if( !text.equals("") )
      {
      ErrorLog.log(ErrorLog.COMMENT,
              "Invalid integer entered: '" + text + "'.");
      }
    }
    
    /**
     * Get the current Stave.
     */

    public Stave getCurrentStave()
    {
      //System.out.println("staveScrollPane = " + staveScrollPane + ", currTabIndex = " + currTabIndex);
    return staveScrollPane[currTabIndex].getStave();
    }

  /**
   * Give focus to the current Stave.
   */
    
  void staveRequestFocus()
    {
    // Show that textEntry no longer has focus if it had.

    Trace.log(3, "focus to stave");

    textEntryLabel.setForeground(Color.red);

    getCurrentStave().requestFocusInWindow();

    setItemStates();
    }


     /**
      *This override is intended to fix requestFocusInWindow, which was
      * only worked some of the time, for reasons I don't understand.
      @return
      */

    public boolean requestFocusInWindow()
    {
        requestFocus();

        boolean value = true; // super.requestFocusInWindow();
        return value;
    }
    
    /**
     * Get the ActionHandler for the current Stave.
     */
    
  StaveActionHandler getCurrentStaveActionHandler()
    {
    return getCurrentStave().getActionHandler();
    }

  
  /**
   * Get the current Selection start.
   */
  
  int getCurrentSelectionStart(Stave stave)
    {
    return stave.getSelectionStart();
    }

  
  /**
   * Get the current Selection start.
   */
  
  int getCurrentSelectionStart()
    {
    return getCurrentStave().getSelectionStart();
    }

  
  /**
   * Get the current Selection end.
   */
  
  int getCurrentSelectionEnd(Stave stave)
    {
    return stave.getSelectionEnd();
    }

  
  /**
   * Get the current Selection end.
   */
  
  int getCurrentSelectionEnd()
    {
    return getCurrentStave().getSelectionEnd();
    }
    
  
  /**
   * Set the current Selection start.
   */
  
  void setCurrentSelectionStart(int index)
    {
    getCurrentStave().setSelectionStart(index);

    getCurrentStaveActionHandler().redoAdvice(index);
    }
    
    
    
  /**
   *
   * Set the current Selection end.
   *
   */
  
  void setCurrentSelectionEnd(int index)
    {
    getCurrentStave().setSelectionEnd(index);
    }
    
    
    
  /**
   *
   * Indicate whether a slot is current selected or not.
   *
   */
  
  private boolean slotIsSelected()
    {
    return getCurrentStave().somethingSelected();
    }

    
    
  /**
   *
   * Indicate whether a unique slot is current selected or not.
   *
   */
  
  private boolean oneSlotSelected()
    {
    return getCurrentStave().oneSlotSelected();
    }
    
    
    
  /**
   *
   * Get the current Selection end.
   *
   */
  
  public MelodyPart getCurrentOrigPart(Stave stave)
    {
    return stave.getOrigPart();
    }
    
    
    
  /**
   *
   * Get the current Selection end.
   *
   */
  
  public MelodyPart getCurrentOrigPart()
    {
    return getCurrentStave().getOrigPart();
    }

    
    
  /**
   *
   * Selects all construction lines on the current Stave
   *
   */
  
    public void selectAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllMIActionPerformed

      selectAll(); 
    }//GEN-LAST:event_selectAllMIActionPerformed

  public void selectAll()
    {
    Trace.log(2, "select all");

    Stave stave = getCurrentStave();

    stave.setSelection(0, stave.getOrigPart().size() - 1);

    redoAdvice();

    staveRequestFocus();
    }

  /**
   * Select all without redoing advice, or requesting
   */
  public void selectAll2()
    {
    Stave stave = getCurrentStave();

    stave.setSelection(0, stave.getOrigPart().size() - 1);
    }
  
  /**
   *
   * Copies the selection of chords from the Stave to the clipboard
   *
   */
  
    public void copyChordsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordsMIActionPerformed

      copyChords();
        
    }//GEN-LAST:event_copyChordsMIActionPerformed

    
  void copyChords()
    {
    Trace.log(2, "copy chords");

    if( slotIsSelected() )
      {

      Stave stave = getCurrentStave();

      cm.execute(
              new CopyCommand(stave.getChordProg(),
              impro.getChordsClipboard(),
              getCurrentSelectionStart(),
              getCurrentSelectionEnd()));

      stave.repaint();

      // set the menu and button states

      setItemStates();
      }
    }
  
    
    /**
     *
     * Cuts the selection of chords from the Stave to the clipboard
     *
     */
    
    public void cutChordsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutChordsMIActionPerformed
        
        cutChords();
        
    }//GEN-LAST:event_cutChordsMIActionPerformed
    
    
    
  void cutChords()
    {
    Trace.log(2, "cut chords");

    if( slotIsSelected() )
      {

      Stave stave = getCurrentStave();

      cm.execute(
              new CutCommand(stave.getChordProg(),
              impro.getChordsClipboard(),
              getCurrentSelectionStart(),
              getCurrentSelectionEnd()));

      redoAdvice();

      // set the menu and button states

      setItemStates();
      }
    }

    
    /**
     *
     * Executes as the form is closing to make sure every component is properly
     *
     * disposed
     *
     */
    
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        
        closeWindow();
        
    }//GEN-LAST:event_formWindowClosing
    
    
    
    public boolean unsavedChanges() {
        
        if(cm.changedSinceLastSave()) {
            if(savedLeadsheet == null || !savedLeadsheet.exists())
                return true;
            
            // create string representing score
            StringWriter strWriter = new StringWriter();
            
            try {
                BufferedWriter out = new BufferedWriter(strWriter);
                Leadsheet.saveLeadSheet(out, score);
                out.close();
            } catch(IOException e) {
                return true;
            }
            
            // read in file
            FileInputStream file;
            try {
                file = new FileInputStream(savedLeadsheet);
            } catch(FileNotFoundException e) {
                return true;
            }
            
            byte[] b;
            try {
                b = new byte[file.available()];
                file.read(b);file.close();
            } catch(IOException e) {
                return true;
            }
            
            String result = new String(b);
            
            /**
             * This comparison is a literal comparison, so
             * extra white space or formatting will cause it
             * to fail.
             */
            
            // file is identical to current score
            if(result.trim().equals(strWriter.toString().trim()))
                return false;
            
            return true;
        }
        
        return false;
    }
    
    
public void closeWindow()
  {

  boolean redisplay = true;

  while( redisplay )
    {
    redisplay = false;

    if( unsavedChanges() )
      {

      Object[] options =
        {
        "<html><b><u>Y</u>es</b>, save modifications.</html>",
        "<html><b><u>N</u>o</b>, do not save modifications.</html>",
        "<html><b>Cancel</b>, do not close this leadsheet.</html>"
        };

      UnsavedChanges dialog = new UnsavedChanges(this,
              "Save changes before closing?", options);

      dialog.setVisible(true);

      dialog.dispose();

      UnsavedChanges.Value choice = dialog.getValue();

      switch( choice )
        {
        case YES:

          if( !saveLeadsheet() )
            {
            redisplay = true;
            }
          break;

        case NO:

          break;

        case CANCEL:

          return;
        }
      }
    }

  midiSynth.close();

  this.dispose();

  adviceFrame.dispose();

  //helpDialog.dispose();

  mixerDialog.dispose();

  preferencesDialog.dispose();

  WindowRegistry.unregisterWindow(this);
  }
    
    
    
    /**
     *
     * Shows the error dialog centered relative to the current window
     *
     */
    
    
     /**
     *
     * Opens the help dialog.
     *
     */
    
    public void openHelpDialog() {
        
        int height = 600;
        
        if(height > getHeight())
            
            height = getHeight();
        
        if(height < 400)
            
            height = 400;
        
        
        
        int width = 600;
        
        if(width > getWidth())
            
            width = getWidth();
        
        if(width < 400)
            
            width = 400;
        
        helpDialog.setSize(width, height);
                
        helpDialog.setLocationRelativeTo(this);
        
        helpDialog.setVisible(true);
        }
    
    /**
     *
     * Opens the About dialog.
     *
     */
    
    private void aboutMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMIActionPerformed
        showAboutDialog();    
    }//GEN-LAST:event_aboutMIActionPerformed
    
    
    public void showAboutDialog()
    {
        aboutDialog.setSize(aboutDialogWidth, aboutDialogHeight);
        aboutDialog.setLocationRelativeTo(this);
        aboutDialog.setVisible(true);
 
    }
    
    public void setMidiLatency(double latency) {
        
        midiLatencyTF.setText(String.valueOf(latency));
        
        
        
    }
    
    
    
    /**
     *
     * Get integer value from string.
     *
     */
    
    
    
    public static int intFromString(String string) throws NumberFormatException {
        
        return Integer.decode(string).intValue();
        
    }
    
    
    
    /**
     *
     * Get integer value from string, with range check
     *
     */
    
    static int intFromStringInRange(String string, int low, int high, int error) {
        
        try {
            
            int value = intFromString(string);
            
            if( value >= low &&  value <= high ) {
                
                return value;
                
            }
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "The number you have entered is out of range: must be between "
                    
                    + low + " and " + high);
            
            
            
            return error; // range error indicator
            
        } catch (NumberFormatException e) {
            
            invalidInteger(string);
            
            return error; // format error indicator
            
        }
        
    }
    
    
    
    /**
     *
     * Get integer value from TextField, with range check.
     *
     * Updates text field with value actually used.
     *
     */
    
    
    
    static int intFromTextField(javax.swing.JTextField field, int low, int high, int error) {
        
        int value = intFromStringInRange(field.getText(), low, high, error);

        if( value >= low && value <= high )
        {
            return value;
        }
        field.setText("" + error);
        return error;
        
    }
    
    
    
    
    
    /**
     *
     * Get double value from string.
     *
     */
    
    
    
    static double doubleFromString(String string) throws NumberFormatException {
        
        return new Double(string).doubleValue();
        
    }
    
    
    
  /**
   *
   * Get double value from string, with range check
   *
   */
  static double doubleFromStringInRange(String string, double low, double high,
                                         double error)
    {
    try
      {
      double value = doubleFromString(string);

      if( value >= low && value <= high )
        {
        return value;
        }

      ErrorLog.log(ErrorLog.COMMENT,
              "The number you have entered: " + value + " is out of range: must be between " + low + " and " + high
              
              + ",\nusing instead the default: " + error);

      return error; // range error indicator

      }
    catch( NumberFormatException e )
      {

      ErrorLog.log(ErrorLog.COMMENT,
              "Invalid double entered: '" + string + ",\nusing instead the default: " + error);

      return error; // format error indicator
      }
    }
    
  /**
   *
   * Get double value from string, with range check, but no warning dialog
   *
   */
  static double quietDoubleFromStringInRange(String string, double low, double high,
                                         double error)
    {
    try
      {
      double value = doubleFromString(string);

      if( value >= low && value <= high )
        {
        return value;
        }

      return error; // range error indicator

      }
    catch( NumberFormatException e )
      {
      return error; // format error indicator
      }
    }
    
    
    /**
     *
     * Get double value from TextField, with range check.
     *
     * Updates text field with value actually used.
     *
     */
    
    
    
    static double doubleFromTextField(javax.swing.JTextField field, double low, double high, double error)
    
    {
        String text = field.getText().trim();

        if( text.equals("") )
          {
            text = "0";
          }
        return doubleFromStringInRange(text, low, high, error);
        
    }
    
    /**
     *
     * Get double value from TextField, with range check.
     *
     * No warning is given if input is in error and error value is used.
     */
    
    
    
    static double quietDoubleFromTextField(javax.swing.JTextField field, double low, double high, double error)
    
    {
        String text = field.getText().trim();

        if( text.equals("") )
          {
            text = "0";
          }
        return quietDoubleFromStringInRange(text, low, high, error);
        
    }
    
    
    
    /**
     *
     * Accepts the preferences the user has inputted through the "Preferences"
     *
     * dialog.
     *
     */
    
    private void savePrefs() {
        
        /* this should be rewritten...
         * should probably validate all data first in separate functions, and then
         * save it...
         * Right now, it validates and saves as it goes.
         */
        
        boolean closeDialog = true;
        
        closeDialog = closeDialog && saveGlobalPreferences();
        
        int tempTabIndex = currTabIndex;
        
        Rectangle tempView = getCurrentScrollPosition();
        
        cm.changedSinceLastSave(true);
        
        // Initialize all the preferences except for the length and metre;
        
        saveLeadsheetPreferences();
        
        closeDialog = closeDialog && saveMidi();
        
        closeDialog = closeDialog && saveMetre();
        
        closeDialog = closeDialog && saveStylePrefs();
        
        closeDialog = closeDialog && saveSectionInfo();
        
        if (closeDialog == true) {
            
            hideFakeModalDialog(preferencesDialog);
            
            scoreTab.setSelectedIndex(tempTabIndex);
            
            setCurrentScrollPosition(tempView);
            
            requestFocusInWindow(); //staveRequestFocus();

        }
    }
    
    
    
    private boolean saveMidi() {
        
        double latency = Double.parseDouble(midiLatencyTF.getText());
        
        if(latency >= 0) {
            
            midiRecorder.setLatency(latency);
            
        } else {
            
            ErrorLog.log(ErrorLog.WARNING,
                    
                    "The latency value must be greater than or equal to 0.");
            
            return false;
            
        }
        
        return true;
        
    }
    
    
    
    private boolean saveSectionInfo() {
        
        score.getChordProg().setSectionInfo(sectionInfo.copy());
        
        return true;
        
    }
    
    
    
    private boolean setSectionPrefs() {
        
        try {
            
            int measure = Integer.valueOf(measureTF.getText());
            
            int index = sectionList.getSelectedIndex();
            
            int currentMeasure = sectionInfo.getSectionMeasure(index);
            
            if(measure > 0 && measure <= sectionInfo.measures()) {
                
                if(measure != currentMeasure && currentMeasure == 1) {
                    
                    ErrorLog.log(ErrorLog.WARNING, "Cannot move the first section.");
                    
                    return false;
                    
                }
                
                sectionInfo.moveSection(index,measure);
                
            } else {
                
                ErrorLog.log(ErrorLog.WARNING, "That measure number doesn't exist.");
                
                return false;
                
            }
            
        } catch (NumberFormatException e) {
            
            invalidInteger(measureTF.getText());
            
            return false;
            
        }
        
        
        
        return true;
        
    }
    
    
    
    private boolean saveStylePrefs() {
        
        Style style = sectionInfo.getStyle(sectionList.getSelectedIndex());
        if( style == null )
        {
          return false;
        }
        
        try {
            
            double swingVal = Double.valueOf(swingTF.getText()).doubleValue();
            
            if(0 <= swingVal && swingVal <= 1) {
                
                style.setSwing(swingVal);
                
            } else {
                
                ErrorLog.log(ErrorLog.WARNING,
                        
                        "The swing value must be between 0 and 1.");
                
                return false;
                
            }
            
        } catch (NumberFormatException e) {
            
            invalidInteger(swingTF.getText());
            
            return false;
            
        }
        
        
        return true;
        
    }
    
    
    
public void updateStyle()
  {
    Style style = (Style) styleList.getSelectedValue();

    if( style == null )
      {
        return; // FIX!
      }
    swingTF.setText("" + style.getSwing());

    Style sectionStyle = sectionInfo.getStyle(sectionList.getSelectedIndex());

    if( sectionStyle == null || !style.getName().equals(sectionStyle.getName()) )
      {
        //         System.out.println("updateStyle called with style = " + style);

        sectionInfo.addSection(style, sectionInfo.getStyleIndex(
            sectionList.getSelectedIndex()));

        sectionList.repaint();
      }
  }


/** 
 * Set up the metre and length of the leadsheet.  This needs to be separated out
 * because we handle things differently depending on whether we're opening a new leadsheet
 * or just changing the current leadsheet.
 */
    
private boolean saveMetre()
  {
    try
      {

        int timeSignatureTop = Integer.parseInt(timeSignatureTopTF.getText());

        int timeSignatureBottom = Integer.parseInt(
            timeSignatureBottomTF.getText());

        if( timeSignatureTop < MIN_TS || timeSignatureTop > MAX_TS )
          {
           ErrorLog.log(ErrorLog.COMMENT, "Beats per measure must be between " + MIN_TS + " and "
                + MAX_TS + ".");

            return false;
          }

        if( !isPowerOf2(timeSignatureBottom) || timeSignatureBottom < MIN_TS
            || timeSignatureBottom > MAX_TS )
          {

            ErrorLog.log(ErrorLog.COMMENT,
                         "Beat value must be a power of two between " + MIN_TS + " and " + MAX_TS + ".");

            return false;
          }

        beatValue = ((BEAT * 4) / timeSignatureBottom);

        measureLength = beatValue * timeSignatureTop;

        initMetreAndLength(timeSignatureTop, timeSignatureBottom, false);
      }
    catch( NumberFormatException e )
      {

        ErrorLog.log(ErrorLog.COMMENT, "Invalid time signature entered: " + timeSignatureTopTF.getText() + "/"
            + timeSignatureBottomTF.getText() + ".");

        return false;
      }

    return true;
  }
    
    
    
    private boolean saveGlobalPreferences() {
        
        boolean close = true;
        
        
        
        if (enableCache.isSelected())
            
            Preferences.setPreference(Preferences.ADV_CACHE_ENABLED, "true");
        
        else
            
            Preferences.setPreference(Preferences.ADV_CACHE_ENABLED, "false");
        
        
        
        try {
            
            String strCache = cacheSize.getText();
            
            String strStyle = defStyleComboBox.getSelectedItem().toString();
            
            String strMelody = defMelodyInst.getText();
            
            String strChord = defChordInst.getText();
            
            String strBass = defBassInst.getText();
            
            String strVoicing = voicing.getText();
            
            String strDist = chordDist.getText();
            
            
            
            int cache = intFromString(strCache.trim());
            
            double tempo = doubleFromTextField(defaultTempoTF, MIN_TEMPO, MAX_TEMPO, getDefaultTempo());
            
            int melody = intFromString(strMelody.trim());
            
            int chord = intFromString(strChord.trim());
            
            int bass = intFromString(strBass.trim());
            
            int voice = intFromString(strVoicing.trim());
            
            int dist = intFromString(strDist.trim());
            
            
            
            int masterVol = defMasterVolSlider.getValue();
            
            int entryVol = defEntryVolSlider.getValue();
            
            int chordVol = defChordVolSlider.getValue();
            
            int bassVol = defBassVolSlider.getValue();
            
            int drumVol = defDrumVolSlider.getValue();
            
            int melodyVol = defMelodyVolSlider.getValue();
            
            
            
            if (cache < 0) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Cache size must be a positive number.");
                
                close = false;
                
            } else {
                
                Advisor.setCacheCapacity(cache);
                
                Preferences.setPreference(Preferences.ADV_CACHE_SIZE, strCache);
                
            }
            
            
            
            Preferences.setPreference(Preferences.DEFAULT_STYLE, strStyle);
            
            Preferences.setPreference(Preferences.DEFAULT_TEMPO, "" + tempo);
            
            
            if (melody < 1 || melody > 128) {
                
                ErrorLog.log(ErrorLog.SEVERE, "The melody instrument must be between 1 and 128");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MELODY_INSTRUMENT, defMelodyInst.getText());
            
            
            
            if (chord < 1 || chord > 128) {
                
                ErrorLog.log(ErrorLog.SEVERE, "The chord instrument must be between 1 and 128");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_CHORD_INSTRUMENT, defChordInst.getText());
            
            
            
            if (bass < 1 || bass > 128) {
                
                ErrorLog.log(ErrorLog.SEVERE, "The bass instrument must be between 1 and 128");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_BASS_INSTRUMENT, defBassInst.getText());
            
            
            
            if (voice < 0) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Voicing must be a positive number.");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.MAX_NOTES_IN_VOICING, voicing.getText());
            
            
            
            if (dist < 0) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Chord distance must be a positive number.");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.CHORD_DIST_ABOVE_ROOT, chordDist.getText());
            
            
            
            if (masterVol < 0 || masterVol > 127) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Master volume must be between 0 and 127");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MIXER_ALL, String.valueOf(masterVol));
            
            
            
            if (entryVol < 0 || entryVol > 127) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Entry volume must be between 0 and 127");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MIXER_ENTRY, String.valueOf(entryVol));
            
            
            
            if (chordVol < 0 || chordVol > 127) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Chord volume must be between 0 and 127");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MIXER_CHORDS, String.valueOf(chordVol));
            
            
            
            if (bassVol < 0 || bassVol > 127) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Bass volume must be between 0 and 127");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MIXER_BASS, String.valueOf(bassVol));
            
            
            
            if (drumVol < 0 || drumVol > 127) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Drum volume must be between 0 and 127");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MIXER_DRUMS, String.valueOf(drumVol));
            
            
            
            if (melodyVol < 0 || melodyVol > 127) {
                
                ErrorLog.log(ErrorLog.SEVERE, "Melody volume must be between 0 and 127");
                
                close = false;
                
            } else
                
                Preferences.setPreference(Preferences.DEFAULT_MIXER_MELODY, String.valueOf(melodyVol));
            
        } catch (NumberFormatException e) {
            
            ErrorLog.log(ErrorLog.SEVERE, e.getMessage());
            
            close = false;
            
        }
        
        
        setStavePreferenceFromButtons();
        
        Preferences.setPreference(Preferences.DEFAULT_VOCAB_FILE, defVocabFile.getText());
        
        
        
        int visAdvice = 0;
        
        if (chordTones.isSelected())
            
            visAdvice = visAdvice | 1;
        
        if (colorTones.isSelected())
            
            visAdvice = visAdvice | 2;
        
        if (scaleTones.isSelected())
            
            visAdvice = visAdvice | 4;
        
        if (approachTones.isSelected())
            
            visAdvice = visAdvice | 8;
        
        if (chordSubs.isSelected())
            
            visAdvice = visAdvice | 16;
        
        if (chordExtns.isSelected())
            
            visAdvice = visAdvice | 32;
        
        if (cells.isSelected())
            
            visAdvice = visAdvice | 64;
        
        if (idioms.isSelected())
            
            visAdvice = visAdvice | 128;
        
        if (licks.isSelected())
            
            visAdvice = visAdvice | 256;
        
        if (quotes.isSelected())
            
            visAdvice = visAdvice | 512;
        
        
        
        Preferences.setPreference(Preferences.VIS_ADV_COMPONENTS, String.valueOf(visAdvice));
        
        
        
        char[] coloringElts= {'1', '1', '1', '1'};
        
        
        
        if (redChordBtn.isSelected())
            
            coloringElts[0] = '2';
        
        if (greenChordBtn.isSelected())
            
            coloringElts[0] = '3';
        
        if (blueChordBtn.isSelected())
            
            coloringElts[0] = '4';
        
        if (redColorBtn.isSelected())
            
            coloringElts[1] = '2';
        
        if (greenColorBtn.isSelected())
            
            coloringElts[1] = '3';
        
        if (blueColorBtn.isSelected())
            
            coloringElts[1] = '4';
        
        if (redApproachBtn.isSelected())
            
            coloringElts[2] = '2';
        
        if (greenApproachBtn.isSelected())
            
            coloringElts[2] = '3';
        
        if (blueApproachBtn.isSelected())
            
            coloringElts[2] = '4';
        
        if (redOtherBtn.isSelected())
            
            coloringElts[3] = '2';
        
        if (greenOtherBtn.isSelected())
            
            coloringElts[3] = '3';
        
        if (blueOtherBtn.isSelected())
            
            coloringElts[3] = '4';
        
        
        
        String coloring = new String(coloringElts);
        
        Preferences.setPreference(Preferences.NOTE_COLORING, coloring);
        
        
        
        /* Drawing preferences */
        
        char[] drawTones = { '1', '1', 'x' };
        
        
        
        if (!drawScaleTonesCheckBox.isSelected())
            
            drawTones[0] = 'x';
        
        if (!drawChordTonesCheckBox.isSelected())
            
            drawTones[1] = 'x';
        
        if (drawColorTonesCheckBox.isSelected())
            
            drawTones[2] = '1';
        
                
        String tonePrefs = new String(drawTones);
        
        Preferences.setPreference(Preferences.DRAWING_TONES, tonePrefs);
                
        Preferences.setPreference(Preferences.DEFAULT_DRAWING_MUTED,
                
                "" + defaultDrawingMutedCheckBox.isSelected());
        
        Preferences.setPreference(Preferences.SHOW_TRACKING_LINE, "" + trackCheckBox.isSelected());
        
        Preferences.setPreference(Preferences.TRACKER_DELAY, "" + trackerDelayTextField.getText());
        
        return close;
    }
    
    /**
     * Set the value of the preference for Stave
     */
    
  private void setStavePreferenceFromButtons()
    {
    if( autoStave.isSelected() )
      {
      Preferences.setPreference(Preferences.DEFAULT_LOAD_STAVE, String.valueOf(
              StaveType.AUTO.ordinal()));
      }
    else if( bassStave.isSelected() )
      {
      Preferences.setPreference(Preferences.DEFAULT_LOAD_STAVE, String.valueOf(
              StaveType.BASS.ordinal()));
      }
    else if( trebleStave.isSelected() )
      {
      Preferences.setPreference(Preferences.DEFAULT_LOAD_STAVE, String.valueOf(
              StaveType.TREBLE.ordinal()));
      }
    else if( grandStave.isSelected() )
      {
      Preferences.setPreference(Preferences.DEFAULT_LOAD_STAVE, String.valueOf(
              StaveType.GRAND.ordinal()));
      }
  
    if( Preferences.getAlwaysUseStave() )
      {
      setCurrentStaveType(Preferences.getStaveTypeFromPreferences());
      }
    }
    
  
  public void setTrackerDelay(String text)
    {
     trackerDelay = doubleFromStringInRange(text, -Double.MAX_VALUE, +Double.MAX_VALUE, 0);
     trackerDelayTextField.setText(text);
     trackerDelayTextField2.setText(text);
    }
    
  public static final int DEFAULT_CHORD_FONT_SIZE_VALUE = 16;


  /**
   * Ensure that there is a chord-font-size preference.
   * Iif there wasn't one, create one.
   */

  private void ensureChordFontSize()
    {
    String chordFontSize = Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE);

    if( chordFontSize.equals("") )
      {
      chordFontSize = "" + DEFAULT_CHORD_FONT_SIZE_VALUE;
      }

    int chordFontSizeValue = Integer.valueOf(chordFontSize);

    Preferences.setPreference(Preferences.DEFAULT_CHORD_FONT_SIZE, "" + chordFontSizeValue);

    defaultChordFontSizeSpinner.setValue(chordFontSizeValue);
    }
    
  private void setPrefsDialog()
    {
    // ===== update Midi panel

    refreshMidiStatus();

    // ===== update Global panel

    enableCache.setSelected(!Preferences.getPreference(Preferences.ADV_CACHE_ENABLED).equals("false"));

    cacheSize.setText(Preferences.getPreference(Preferences.ADV_CACHE_SIZE));

    defaultTempoTF.setText(Preferences.getPreference(Preferences.DEFAULT_TEMPO));

    voicing.setText(Preferences.getPreference(Preferences.MAX_NOTES_IN_VOICING));

    chordDist.setText(Preferences.getPreference(Preferences.CHORD_DIST_ABOVE_ROOT));

    ensureChordFontSize();

    setTrackerDelay(Preferences.getPreference(Preferences.TRACKER_DELAY));

    defStyleComboBox.setSelectedItem(Advisor.getStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE)));

    defStyleComboBox.repaint();

    defBassInst.setText(Preferences.getPreference(Preferences.DEFAULT_BASS_INSTRUMENT));

    defChordInst.setText(Preferences.getPreference(Preferences.DEFAULT_CHORD_INSTRUMENT));

    defMelodyInst.setText(Preferences.getPreference(Preferences.DEFAULT_MELODY_INSTRUMENT));


    defMasterVolSlider.setValue(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_MIXER_ALL)));

    defEntryVolSlider.setValue(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_MIXER_ENTRY)));

    defBassVolSlider.setValue(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_MIXER_BASS)));

    defDrumVolSlider.setValue(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_MIXER_DRUMS)));

    defChordVolSlider.setValue(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_MIXER_CHORDS)));

    defMelodyVolSlider.setValue(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_MIXER_MELODY)));
    

    // ===== set stave buttons
    
    alwaysUseStave.setSelected(Preferences.getAlwaysUseStave());

    int stave =
            Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_LOAD_STAVE));

    autoStave.setSelected(stave == StaveType.AUTO.ordinal());

    bassStave.setSelected(stave == StaveType.BASS.ordinal());

    trebleStave.setSelected(stave == StaveType.TREBLE.ordinal());

    grandStave.setSelected(stave == StaveType.GRAND.ordinal());

    defVocabFile.setText(Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE));


    int visAdvice =
            Integer.parseInt(Preferences.getPreference(Preferences.VIS_ADV_COMPONENTS));

    chordTones.setSelected((visAdvice & 1) != 0);

    colorTones.setSelected((visAdvice & 2) != 0);

    scaleTones.setSelected((visAdvice & 4) != 0);

    approachTones.setSelected((visAdvice & 8) != 0);

    chordSubs.setSelected((visAdvice & 16) != 0);

    chordExtns.setSelected((visAdvice & 32) != 0);

    cells.setSelected((visAdvice & 64) != 0);

    idioms.setSelected((visAdvice & 128) != 0);

    licks.setSelected((visAdvice & 256) != 0);

    quotes.setSelected((visAdvice & 512) != 0);


    String coloring = Preferences.getPreference(Preferences.NOTE_COLORING);

    switch( Integer.parseInt(coloring.substring(0, 1)) )
      {
      default:

      case 1:

        blackChordBtn.setSelected(true);
        break;

      case 2:

        redChordBtn.setSelected(true);
        break;

      case 3:

        greenChordBtn.setSelected(true);
        break;

      case 4:

        blueChordBtn.setSelected(true);
        break;
      }



    switch( Integer.parseInt(new String(coloring.substring(1, 2))) )
      {
      default:

      case 1:

        blackColorBtn.setSelected(true);
        break;

      case 2:

        redColorBtn.setSelected(true);
        break;

      case 3:

        greenColorBtn.setSelected(true);
        break;

      case 4:

        blueColorBtn.setSelected(true);
        break;
      }



    switch( Integer.parseInt(new String(coloring.substring(2, 3))) )
      {
      default:

      case 1:

        blackApproachBtn.setSelected(true);
        break;

      case 2:

        redApproachBtn.setSelected(true);
        break;

      case 3:

        greenApproachBtn.setSelected(true);
        break;

      case 4:

        blueApproachBtn.setSelected(true);
        break;
      }



    switch( Integer.parseInt(new String(coloring.substring(3, 4))) )
      {
      default:

      case 1:

        blackOtherBtn.setSelected(true);
        break;

      case 2:

        redOtherBtn.setSelected(true);
        break;

      case 3:

        greenOtherBtn.setSelected(true);
        break;

      case 4:

        blueOtherBtn.setSelected(true);
        break;
      }


    // ===== update drawing panel


    String drawTones = Preferences.getPreference(Preferences.DRAWING_TONES);

    drawScaleTonesCheckBox.setSelected(drawTones.charAt(0) == '1');
    drawChordTonesCheckBox.setSelected(drawTones.charAt(1) == '1');
    drawColorTonesCheckBox.setSelected(drawTones.charAt(2) == '1');

    defaultDrawingMutedCheckBox.setSelected(Preferences.getPreference(Preferences.DEFAULT_DRAWING_MUTED).equals("true"));

    
    // ===== update Style panel

    sectionList.setSelectedIndex(0);


    if( sectionInfo != null )
      {
      styleList.setSelectedValue(sectionInfo.getStyle(0), true);
      }

    sectionListModel.reset();


    // ===== update Leadsheet panel

    // display the current part title

    partTitleTF.setText(getCurrentStave().getPartTitle());


    // display the composer

    composerField.setText(score.getComposer());


    // display the part composer

    partComposerTF.setText(getCurrentOrigPart().getComposer());

    String inst = "";

    if( alwaysUseMelody.isSelected() )
      {
      inst = Preferences.getPreference(Preferences.DEFAULT_MELODY_INSTRUMENT);
      int instNumber = midiInstFromText(inst, 1);
      getCurrentOrigPart().setInstrument(instNumber);

      melodyInst.setText(inst);
      //System.out.println("assigning melody instrument from default: " + inst);
      }
    else
      {
      melodyInst.setText("" + (getCurrentOrigPart().getInstrument() + 1));
      //System.out.println("assigning melody instrument from score: " + inst);
      }

    // display the current score title

    scoreTitleTF.setText(score.getTitle());

    // display score comments

    commentsTF.setText(score.getComments());

    // display the score tempo

    tempoTF.setText("" + score.getTempo());

    keySignatureTF.setText("" + getCurrentOrigPart().getKeySignature());

    timeSignatureTopTF.setText("" + getCurrentOrigPart().getMetre()[0]);

    timeSignatureBottomTF.setText("" + getCurrentOrigPart().getMetre()[1]);

    Style style = score.getChordProg().getStyle();

    if( alwaysUseChord.isSelected() )
      {
      inst = Preferences.getPreference(Preferences.DEFAULT_CHORD_INSTRUMENT);
      int instNumber = midiInstFromText(inst, 1);
      score.setChordInstrument(instNumber);

      chordInst.setText(inst);
      //System.out.println("assigning chord instrument from default: " + inst);
      }
    else
      {
      chordInst.setText("" + (score.getChordInstrument() + 1));
      //System.out.println("assigning chord instrument from score: " + inst);
      }

    if( alwaysUseBass.isSelected() )
      {
      inst = Preferences.getPreference(Preferences.DEFAULT_BASS_INSTRUMENT);
      int instNumber = midiInstFromText(inst, 34);
      score.setBassInstrument(instNumber);

      bassInst.setText(inst);
      //System.out.println("assigning bass instrument from default: " + inst);
      }
    else
      {
      bassInst.setText("" + (score.getBassInstrument() + 1));
      //System.out.println("assigning bass instrument from score: " + inst);
      }


    // display the current breakpoint

    breakpointTF.setText("" + score.getBreakpoint());


    // display the current layout

    Polylist layout = score.getLayoutList();

    if( layout != null && layout.nonEmpty() )
      {
      setLayoutPreference(layout);

      setFreezeLayout(true);
      }
    else
      {
      setFreezeLayout(false);
      }

    // set the stave type buttons

    StaveType tempStaveType = StaveType.TREBLE;
    
     // If "always use this" is selected, over-ride with default stave preference
    
   if( alwaysUseStave.isSelected() )
      {
      tempStaveType = Preferences.getStaveTypeFromPreferences();
      }
    else
      {
      tempStaveType = getCurrentOrigPart().getStaveType();
      }
    setCurrentStaveType(tempStaveType);
    }
    
  public int getBreakpoint()
    {
    return score.getBreakpoint();
    }

  private void setNewSectionEnabled()
    {
    int measureLength = score.getChordProg().getMeasureLength();

    int sectionIndex = sectionList.getSelectedIndex();

    if( sectionIndex == -1 )
      {
      newSectionButton.setEnabled(false);

      return;
      }

    int m1 = sectionInfo.getSectionMeasure(sectionIndex);

    int m2 = sectionInfo.measures();

    if( sectionIndex + 1 < sectionInfo.size() )
      {
      m2 = sectionInfo.getSectionMeasure(sectionIndex + 1) - 1;
      }

    newSectionButton.setEnabled((m2 - m1) > 0);
    }
    
    
    
  private void setFreezeLayout(Boolean frozen)
    {
    if( frozen )
      {
      freezeLayoutButton.setText("<html><center>Thaw</center></html>");

      freezeLayoutButton.setBackground(Color.RED);
      }
    else
      {
      score.setLayoutList(Polylist.nil);

      freezeLayoutButton.setText("<html><center>Freeze</center></html>");

      freezeLayoutButton.setBackground(Color.GREEN);
      }
    }

    
    
  private void toggleFreezeLayout()
    {
    String current = layoutTF.getText().trim();

    if( current.equals("") )
      {
      // was not frozen, freeze it

      setFreezeLayout(true);

      Polylist layout = getCurrentStave().getLayoutList();

      adjustLayout(layout);
      }
    else
      {
      // was frozen, now thaw it

      setFreezeLayout(false);

      clearLayoutPreference();
      }
    }

  public void refreshCurrentTabTitle()
    {
    refreshTabTitle(currTabIndex);
    }

  public void refreshTabTitle(int i)
    {
    String title = staveScrollPane[i].getStave().getPartTitle();

    if( title.trim().equals("") )
      {
      title = "Chorus " + (i + 1);
      }
    scoreTab.setTitleAt(i, title);
    }
    
    
    
  private void saveLeadsheetPreferences()
    {
    // set the part title

    getCurrentStave().setPartTitle(partTitleTF.getText());

    refreshCurrentTabTitle();

    getCurrentOrigPart().setComposer(partComposerTF.getText());

    // set the part composer

    getCurrentStave().setComposer(partComposerTF.getText());


    // set instrument numbers

    int instSetting;
 
    
    // set the melody instrument number

    if( alwaysUseMelody.isSelected() )
      {
      instSetting = midiInstFromText(defMelodyInst.getText(), 1);
      }
    else
      {
      instSetting = midiInstFromText(melodyInst.getText(), 1);
      }
    getCurrentOrigPart().setInstrument(instSetting);
    
    
    // set the chord instrument number

    if( alwaysUseChord.isSelected() )
      {
      instSetting = midiInstFromText(defChordInst.getText(), 1);
      }
    else
      {
      instSetting = midiInstFromText(chordInst.getText(), 1);
      }
    score.setChordInstrument(instSetting);


    // set the bass instrument number
    if( alwaysUseBass.isSelected() )
      {
      instSetting = midiInstFromText(defBassInst.getText(), 1);
      }
    else
      {
      instSetting = midiInstFromText(bassInst.getText(), 1);
      }
    score.setBassInstrument(instSetting);


    // set the new score title

    setTitle(scoreTitleTF.getText());

    score.setComposer(composerField.getText());

    score.setComments(commentsTF.getText());

    // set the key

    score.setKeySignature(keyNumberFromText(keySignatureTF.getText(), 0));
    

    // set the new breakpoint

    int proposedBreakpoint = 0;

    try
      {
      proposedBreakpoint = Integer.decode(breakpointTF.getText()).intValue();

      if( proposedBreakpoint > 0 && proposedBreakpoint < 127 )
        {
        score.setBreakpoint(proposedBreakpoint);
        }
      else if( proposedBreakpoint < 0 || proposedBreakpoint > 127 )
        {
        ErrorLog.log(ErrorLog.COMMENT,
                "Breakpoint pitch out of bounds: '" + breakpointTF.getText() + "'.");
        return;
        }
      }
    catch( NumberFormatException e )
      {

      ErrorLog.log(ErrorLog.COMMENT,
              "Breakpoint cannot equal: '" + breakpointTF.getText() + "'.");
      return;
      }

    setTempo(doubleFromTextField(tempoTF, MIN_TEMPO, MAX_TEMPO, getDefaultTempo()));
    
    commentsTF.setText(score.getComments());
    
    if( autoStaveBtn.isSelected() )
      {
      autoStaveMIActionPerformed(null);
      }
    else if( trebleStaveBtn.isSelected() )
      {
      trebleStaveMIActionPerformed(null);
      }
    else if( bassStaveBtn.isSelected() )
      {
      bassStaveMIActionPerformed(null);
      }
    else if( grandStaveBtn.isSelected() )
      {
      grandStaveMIActionPerformed(null);
      }
    else
      {
      ErrorLog.log(ErrorLog.WARNING,
              "Currently, having no stave is not an option");
      }
    
    // adjust the layout if a layout is specified

    Polylist layout = parseListFromString(layoutTF.getText());

    adjustLayout(layout);

    setFreezeLayout(layout.nonEmpty());

    // set the menu and button states

    setItemStates();
    
    repaint();
 }
    
  /**
   *
   * Get an external midi instrument number 1=128 from text
   * If not possible, or the number is out of range, an error message given
   * and the default instrument number is used. Both numbers should be 1 greater
   * than the internal values.
   * @param text
   * @param defaultInstNumber
   * @return
   */
  public static int midiInstFromText(String text, int defaultInstNumber)
    {
    // External MIDI numbers are 1 greater than the values used here.

    try
      {
      int value = Integer.decode(text);
      if( value >= 1 && value <= 128 )
        {
        return value - 1;
        }
      else
        {

        ErrorLog.log(ErrorLog.COMMENT,
                "The instrument number " + value + " is out of range: must be between 1-128");

        return defaultInstNumber - 1;
        }
      }
    catch( NumberFormatException e )
      {
       ErrorLog.log(ErrorLog.COMMENT,
                "The instrument number " + text + " is badly formatted, using default.");
      return defaultInstNumber - 1;

     }
    }
    

 /**
   *
   * Get a key number from text
   * If not possible, or the number is out of range, an error message given
   * and the default key  number is used.
   * @param text
   * @param defaultInstNumber
   * @return
   */
  public static int keyNumberFromText(String text, int defaultKey)
    {
    // External MIDI numbers are 1 greater than the values used here.

    try
      {
      int value = Integer.decode(text);
      if( value >= MIN_KEY && value <= MAX_KEY )
        {
        return value;
        }
      else
        {

        ErrorLog.log(ErrorLog.COMMENT,
                "The key number " + value + " is out of range: must be between " 
                + MIN_KEY + " and " + MAX_KEY);

        return defaultKey;
        }
      }
    catch( NumberFormatException e )
      {
       ErrorLog.log(ErrorLog.COMMENT,
                "The key number " + text + " is badly formatted, using default.");
      return defaultKey;

     }
   }
    
 
  
// Set up the metre and length of the leadsheet:
    
// top and bottom are the numerator and denominator of the time signature,
    
// and opened is a flag that tells whether we're setting the metre from a file
    
// or from the preferences dialogue box.
    
    
    
    public void initMetreAndLength(int top, int bottom, boolean opened) {
        
        beatValue = ((BEAT*4)/bottom);
        
        measureLength = beatValue * top;
        
        // If we set this from a file, we basically want to ignore everything in
        
        // the preferences box, and set those up from the file.
        
        if (opened) {
            
            int scoreBarLength = score.getLength() / measureLength;
            
            setBars(scoreBarLength);
            
            setTotalMeasures(scoreBarLength);
        }
        
        // However, if we change things from the preferences box, we want to update
        // the score length so it matches.
        
        else {
            
            int scoreBarLength = Integer.parseInt(prefMeasTF.getText());
            
            score.setMetre(top, bottom);
 
            setBars(scoreBarLength);
            }
        
        setupArrays();
        }
    
public void setBars(int bars)
  {
    partBarsTF1.setText("" + bars);
    prefMeasTF.setText("" + bars);

    setTotalMeasures(bars);

    score.setLength(bars * measureLength);

    sectionInfo.setSize(bars * measureLength);
  }

private void adjustLayout(Polylist layout)
  {

    // System.out.println("adjustLayout to " + layout);

    if( layout == null || layout.isEmpty() || noLockedMeasures() )
      {
        score.setLayoutList(Polylist.nil);

        // auto adjust and leave

        autoAdjustStaves = true;

        staveScrollPane[0].getStave().repaint();

        setLockedMeasures(staveScrollPane[0].getStave().getLineMeasures(),
                          "adjustLayout");

        autoAdjustStaves = autoAdjustMI.isSelected();

        return;

      }

    score.setLayoutList(layout);

    setLayoutPreference(layout);

    autoAdjustStaves = false;

    // Determine how many measures there are currently

    int currentMeasures = 0;

    for( int k = lockedMeasures.length - 1; k >= 0; k-- )
      {

        currentMeasures += lockedMeasures[k];
      }



    // Now determine the size of the new array.

    // This has to be enough to accomodate the current measures,

    // and the number of elements has to conform to the list

    // specification.  If there are more elements than specified

    // in the list, the last entry in the list is used as the size

    // for all remaining lines.  So obviously the list has to have

    // at least one element.  There should not be any zero or

    // negative elements.



    int measuresLeft = currentMeasures;

    int arrayElements = 0;

    Polylist T = layout;

    int lastLineLength = 0;

    int thisLineLength = 0;

    for( arrayElements = 0; measuresLeft > 0; arrayElements++ )
      {

        if( T.nonEmpty() )
          {

            if( !(T.first() instanceof Long) )
              {

                ErrorLog.log(ErrorLog.WARNING,
                             "Non-integer " + T.first() + " in layout specification");

                return;
              }

            lastLineLength = ((Long) T.first()).intValue();

            if( lastLineLength <= 0 )
              {

                ErrorLog.log(ErrorLog.WARNING,
                             "Non-positive line length " + lastLineLength + " in layout specification");
                return;
              }

            T = T.rest();
          }

        thisLineLength = lastLineLength < measuresLeft ? lastLineLength : measuresLeft;

        measuresLeft -= thisLineLength;
      }

    int newLockedMeasures[] = new int[arrayElements];

    // Finally populate the new array

    measuresLeft = currentMeasures;

    T = layout;

    for( int k = 0; measuresLeft > 0; k++ )
      {
        if( T.nonEmpty() )
          {
            lastLineLength = ((Long) T.first()).intValue();

            T = T.rest();
          }

        thisLineLength = lastLineLength < measuresLeft ? lastLineLength : measuresLeft;

        newLockedMeasures[k] = thisLineLength;

        measuresLeft -= thisLineLength;
      }

    setLockedMeasures(newLockedMeasures, "adjustLayout2");
    
    paintCurrentStaveImmediately("adjustLayout");

  }

    /**
     * Force the stave to paint.
     */

    private void paintCurrentStaveImmediately(String id)
    {
     // The try was to try to workaround a bug in the Java libraries,
     // which causes a stack trace unnecessarily. However, it doesn't
     // fix the problem
        try
        {
            Stave stave = getCurrentStave();
            stave.paintImmediately(0, 0, stave.getWidth(), stave.getHeight());
        }
        catch(Error e)
        {
            System.out.println("paintImmediately error caught at " + id);
        }
    }

    
public void setLockedMeasures(int[] _lockedMeasures, String msg)
  {
    lockedMeasures = _lockedMeasures;
  }

public int[] getLockedMeasures()
  {
    return lockedMeasures;
  }

public boolean noLockedMeasures()
  {
    return lockedMeasures == null;
  }

public boolean hasLockedMeasures()
  {
    return lockedMeasures != null;
  }

private void setTotalMeasures(int measures)
  {
    if( measures == 0 || noLockedMeasures() )
      {
        return;
      }

//  score.setLength(measures * measureLength);

    int defaultMeasPerLine = 4;

    int[] tempLockedMeasures = new int[lockedMeasures.length +
        (measures - lockedMeasures.length) / 4];

    for( int i = 0; i < tempLockedMeasures.length; i++ )
      {
        if( i < lockedMeasures.length )
          {
            tempLockedMeasures[i] = lockedMeasures[i];
          }
        else
          {
            tempLockedMeasures[i] = defaultMeasPerLine;
          }
      }
    setLockedMeasures(tempLockedMeasures, "setTotalMeasures");
  }
    
    
    /**
     *
     * Sets the layout text field in the preferences dialog.
     *
     */
    
private void clearLayoutPreference()
  {
    layoutTF.setText("");
  }

private void setLayoutPreference(Polylist layout)
  {
    if( layout != null )
      {
        StringBuffer buffer = new StringBuffer();

        while( layout.nonEmpty() )
          {
            buffer.append(layout.first() + " ");

            layout = layout.rest();
          }

        layoutTF.setText(buffer.toString());
      }
  }
    
    
    /**
     *
     * Loads the advice rules for the current score
     *
     */
    
    private void loadAdvMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadAdvMIActionPerformed

        vocfc.setDialogTitle("Load Vocabulary File");

        vocfc.resetChoosableFileFilters();

        vocfc.addChoosableFileFilter(new VocabFilter());

        if( vocfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            cm.execute(new LoadAdviceCommand(vocfc.getSelectedFile(), adv, this,
                                             true, true));
          }

        savedVocab = vocfc.getSelectedFile();
    }//GEN-LAST:event_loadAdvMIActionPerformed
    
       
    /**
     *
     * Checks to see if the tabs in scoreTab have changed states, and sets the
     *
     *
     *
     * current tabbed index if so.
     *
     */
    
    private void scoreTabStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scoreTabStateChanged

      if( scoreTab.getSelectedIndex() != OUT_OF_BOUNDS )
        {

        // Get the previous Stave panel's location

        staveScrollPane[scoreTab.getSelectedIndex()].setBGlocation(
                staveScrollPane[currTabIndex].getBGlocation().x,
                staveScrollPane[currTabIndex].getBGlocation().y);



        // update the current tab index

        currTabIndex = scoreTab.getSelectedIndex();



        /* If the tab gets changed during playback, disable autoscrolling
         * since the playback indicator is no longer on the screen
         */

        if( getPlaying() != MidiPlayListener.Status.STOPPED )
          {

          autoScrollOnPlayback = (currentPlaybackTab == currTabIndex);

          }

        if( Preferences.getAlwaysUseStave() )
        {
          getCurrentStave().changeType(Preferences.getStaveTypeFromPreferences());
        }
 
        // reset the viewport

        staveScrollPane[currTabIndex].resetViewportView();

        staveRequestFocus();



        // set the volume sliders

        allVolumeMixerSlider.setValue(midiSynth.getMasterVolume());

        allVolumeToolBarSlider.setValue(midiSynth.getMasterVolume());

        melodyVolume.setValue(getScore().getMelodyVolume());

        chordVolume.setValue(getScore().getChordVolume());

        drumVolume.setValue(getScore().getDrumVolume());

        bassVolume.setValue(getScore().getBassVolume());


        // set the menu and button states

        setItemStates();

        }

    }//GEN-LAST:event_scoreTabStateChanged
    
    
    
    public Rectangle getCurrentScrollPosition() {
        
        return staveScrollPane[currTabIndex].getViewport().getViewRect();
        
    }
    
    
    
    public void setCurrentScrollPosition(Rectangle r) {
   //System.out.println("setCurrentScrollPosition(" + r + ")");
        int maxwidth = staveScrollPane[currTabIndex].getStave().getPanelWidth();
        
        int curwidth = (int) staveScrollPane[currTabIndex].getViewport().getExtentSize().getWidth();
        
        if(r.x + curwidth >= maxwidth) {
            
            r.x = maxwidth - curwidth;
        }
        
        if(r.x < 0)
            
            r.x = 0;
        
        staveScrollPane[currTabIndex].getViewport().setViewPosition(r.getLocation());
    }
    

    /**
     *
     * Adds a new, blank tabbed part to the score.
     *
     */
    
    private void addTabMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabMIActionPerformed

      int keySig = getCurrentStave().getKeySignature();

      if( partList != null )
        {
        score.addPart();
        }
      else
        {
       MelodyPart mp = new MelodyPart(16 * measureLength);

        mp.setInstrument(score.getPart(0).getInstrument());

        score.addPart(mp);
        }

      // reset the current scoreFrame

      setupArrays();

      getCurrentStave().setKeySignature(keySig);

      getCurrentStave().repaint();
    }//GEN-LAST:event_addTabMIActionPerformed
    
    
    
    
    
    /**
     *
     * Open up the "Preferences" dialog.
     *
     */
    
    private void prefsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefsMIActionPerformed
        
        showPreferencesDialog();
        
    }//GEN-LAST:event_prefsMIActionPerformed
    
    
    
    
    
    /**
     *
     * Acknowledges the information, then closes the window.
     *
     */
        
    
     
    // The following code was refactored as it appeared to be cut-and-paste.
    
  private void enterMeasuresCore()
    {
    try
      {
      String measuresText = enterMeasures.getText();

      if( measuresText.length() > 0 && Integer.decode(measuresText).intValue() > 0 )
        {
        int newMeasures = Integer.decode(measuresText).intValue();

        if( newMeasures <= maxMeasuresPerLine )
          {
          measureOverride(newMeasures, getCurrentStave().currentLine);

          getCurrentStave().repaint();

          overrideFrame.setVisible(false);

          staveRequestFocus();
          }
        else
          {
         measErrorLabel.setText(
                  "This would exceed the maximum number of " + maxMeasuresPerLine);
         }
       }
      else
        {
        measErrorLabel.setText("Invalid number!");
        }
      }
    catch( NumberFormatException e )
      {
      measErrorLabel.setText("Invalid number!");
      }
    }
    
    
    /**
     *
     * Resets the focus if the advice frame is "closed".
     *
     */
    
    private void adviceFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_adviceFrameComponentHidden
        
        staveRequestFocus();
        
    }//GEN-LAST:event_adviceFrameComponentHidden
    
    
    
    /**
     *
     * This variable is used to control undoing within a single instance
     *
     * of an advice menu.
     *
     */
    
    private boolean adviceUsed = false;
    
    
    
    /**
     *
     * Reset the advice undo memory.
     *
     */
    
    public void resetAdviceUsed() {
        
        if( adviceUsed ) {
            
            Trace.log(2, "adviceUsed set to false");
            
            adviceUsed = false;
            
        }
        
    }
    
    
    /**
     *
     * Set the advice undo memory.
     *
     */
    
    public void setAdviceUsed() {
        
        Trace.log(2, "adviceUsed set to true");
        
        adviceUsed = true;
    }
    
    /**
     *
     * Now advice is entered by clicking only.
     *
     * Keystrokes should behave the same as if pressed in the stave window.
     *
     */
    
    private void adviceTreeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adviceTreeKeyPressed

        //System.out.println("keycode = " + evt.getKeyCode() + ", evt = " + evt);

        boolean up = false;

        switch( evt.getKeyCode() )
          {
            case KeyEvent.VK_ENTER:

                staveRequestFocus();
                break;

            case KeyEvent.VK_UP:
                up = true;

            case KeyEvent.VK_DOWN:
                {
                DefaultMutableTreeNode node =
                    (DefaultMutableTreeNode) adviceTree.getSelectionPath().getLastPathComponent();
                
                 TreeNode parent = node.getParent();
                 
                 Enumeration<DefaultMutableTreeNode>e = parent.children();
                 
                 DefaultMutableTreeNode prev = null;
                 
                 DefaultMutableTreeNode next = null;

                 // This hack is necessary because the only path component available
                 // is the one FROM which we moved, not to which.
                 // So if we are moving UP from an element, we need to get the element before the current,
                 // while if we are moving DOWN from an element, we need to get the element after the current.
                 // In either case, we have to go through the enumeration of siblings to find where the
                 // node from which we moved is.

                 // Note that if weWERE at a non-leaf node and arrive at a leaf, nothing
                 // will be selected below. This can be FIXed later when there is time.
                 
                 while( e.hasMoreElements() && !node.equals(next) )
                 {
                     prev = next;
                     
                     next = e.nextElement();
                 }

                 // Now next is the node to which we just moved
                 // and prev is the one before it, if any.

                 // Moving up
                 
                 if( up && prev != null )
                 {
                     adviceSelected(prev.getUserObject());
                 }

                 // Moving down

                 else if( !up && e.hasMoreElements() )
                 {
                     adviceSelected(e.nextElement().getUserObject());
                 }


                // set the menu and button states

                setItemStates();
                }
              break;

            // I think the following case is vestigial, from a former model in which the full advice tree was handled as a tree.
            // It should probably go.

            case KeyEvent.VK_M:        // handle marking and unmarking
                  {
                    DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) adviceTree.getLastSelectedPathComponent();

                    if( node.isLeaf() )
                      {
                        Object object = node.getUserObject();

                        if( object instanceof Advice )
                          {
                            Advice advice = (Advice) object;

                            if( advice instanceof AdviceForMelody )
                              {
                                AdviceForMelody cellAdvice = (AdviceForMelody) advice;

                                if( evt.isShiftDown() )
                                  {
                                    adv.setMark(cellAdvice.getSerial());
                                  }
                                else
                                  {
                                    adv.unsetMark(cellAdvice.getSerial());
                                  }
                                saveAdviceActionPerformed(null);
                              }
                          }
                      }
                  }
                break;

            default:

                adviceKeyPressed(evt);
          }
    }//GEN-LAST:event_adviceTreeKeyPressed

    
    
    
    
  /**
   *
   * This is called from within and also from StaveActionHandler
   *
   * so that Mac command key (= meta key) behaves as control key would
   *
   * on other platforms.
   *
   */
    
  public void controlDownBehavior(KeyEvent e)
    {
    Trace.log(2, "controlDownBehavior " + e);

    switch( e.getKeyCode() )
      {

      case KeyEvent.VK_A:
        selectAllMIActionPerformed(null);
        break;

      case KeyEvent.VK_B:
        enterBothMIActionPerformed(null);
        break;

      case KeyEvent.VK_C:
        copyBothMIActionPerformed(null);
        break;

      case KeyEvent.VK_D:
        transposeBothDownSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_E:
        transposeBothUpSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_L:
        oneAutoMIActionPerformed(null);
        break;

      case KeyEvent.VK_N:
        newMIActionPerformed(null);
        break;

      case KeyEvent.VK_O:
        openLeadsheetMIActionPerformed(null);
        break;

      case KeyEvent.VK_P:
        printMIActionPerformed(null);
        break;

      case KeyEvent.VK_Q:
        quitMIActionPerformed(null);
        break;

      case KeyEvent.VK_S:
        saveLeadsheetMIActionPerformed(null);
        break;

      case KeyEvent.VK_U:
        generate(lickgen);
        break;

      case KeyEvent.VK_V:
        pasteBothMIActionPerformed(null);
        break;

      case KeyEvent.VK_W:
        saveAsLeadsheetMIActionPerformed(null);
        break;

      case KeyEvent.VK_X:
        cutBothMIActionPerformed(null);
        break;

      case KeyEvent.VK_Y:
        redoMIActionPerformed(null);
        break;

      case KeyEvent.VK_Z:
        undoMIActionPerformed(null);
        break;

      case KeyEvent.VK_SPACE:
        toggleBothEnharmonics();
        break;
      }
    staveRequestFocus();
    }

  /**
   *
   * Behavior when shift key is held down.
   *
   */
    
  public void shiftDownBehavior(KeyEvent e)
  {
    switch( e.getKeyCode() )
      {
      case KeyEvent.VK_B:
        enterChordsMIActionPerformed(null);
        break;

      case KeyEvent.VK_C:
        copyChordsMIActionPerformed(null);
        break;

      case KeyEvent.VK_D:
        transposeChordsDownSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_E:
        transposeChordsUpSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_G:
        transposeMelodyDownHarmonicallyActionPerformed(null);
        break;

      case KeyEvent.VK_T:
        transposeMelodyUpHarmonicallyActionPerformed(null);
        break;

      case KeyEvent.VK_M:
        transposeChordsUpSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_R:
        resolvePitchesActionPerformed(null);
        break;

      case KeyEvent.VK_V:
        pasteChordsMIActionPerformed(null);
        break;

      case KeyEvent.VK_X:
        cutChordsMIActionPerformed(null);
        break;

      case KeyEvent.VK_Y:
        redoMIActionPerformed(null);
        break;

      case KeyEvent.VK_Z:
        undoMIActionPerformed(null);
        break;

      case KeyEvent.VK_SPACE:
        toggleChordEnharmonics();
        getCurrentStave().repaint();
        break;

      case KeyEvent.VK_ENTER:
        keyboard.setPlayback(true);
        getCurrentStave().playSelection(true, 0);
        break;
      }
    staveRequestFocus();
  }

 
  /**
   *
   * Behavior when neither shift nor control key is held down.
   *
   */
    
  public void nothingDownBehavior(KeyEvent e)
  {
    switch( e.getKeyCode() )
      {
      // neither shift nor control

      case KeyEvent.VK_A:
        moveLeft();
        break;

      case KeyEvent.VK_B:
        enterMelodyMIActionPerformed(null);
        break;

      case KeyEvent.VK_C:
        copyMelodyMIActionPerformed(null);
        break;

      case KeyEvent.VK_D:
        transposeMelodyDownSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_E:
        transposeMelodyUpSemitoneActionPerformed(null);
        break;

      case KeyEvent.VK_F:
        moveRight();
        break;

      case KeyEvent.VK_G:
        transposeMelodyDownOctaveActionPerformed(null);
        break;

      case KeyEvent.VK_I:
        playAllMIActionPerformed(null);
        break;

      case KeyEvent.VK_K:
        stopPlayMIActionPerformed(null);
        break;

      case KeyEvent.VK_R:
        addRest();
        break;

      case KeyEvent.VK_T:
        transposeMelodyUpOctaveActionPerformed(null);
        break;

      case KeyEvent.VK_U:
        openSaveLickFrame();
        break;

      case KeyEvent.VK_V:
        pasteMelodyMIActionPerformed(null);
        break;

      case KeyEvent.VK_DELETE:
      case KeyEvent.VK_BACK_SPACE:
      case KeyEvent.VK_X:
        cutMelody();
        break;

      case KeyEvent.VK_Y:
        redoMIActionPerformed(null);
        break;

      case KeyEvent.VK_Z:
        undoMIActionPerformed(null);
        break;

      case KeyEvent.VK_SLASH:
        invertMelodyActionPerformed(null);
        break;

      case KeyEvent.VK_BACK_SLASH:
        reverseMelodyActionPerformed(null);
        break;

      case KeyEvent.VK_ENTER:
        getCurrentStave().playSelection(false, 0);
        break;

      case KeyEvent.VK_SPACE:
        toggleMelodyEnharmonics();
        getCurrentStave().repaint();
        break;

      case KeyEvent.VK_ESCAPE:
        getCurrentStave().unselectAll();
        getCurrentStave().repaint();
        break;
      }
    staveRequestFocus();
   }
  
  
  /**
   *
   * Key pressed in advice menu.  Note that this replicates some functions
   *
   * that are handled by shortcuts here.
   *
   */
  public void adviceKeyPressed(KeyEvent e)
    {
    Trace.log(2, "key " + e.getKeyCode() + " pressed in Advice");

    resetAdviceUsed();

    int subDivs = 2;

    // Checks to see if a note or group of notes is selected

    if( getCurrentSelectionStart() != OUT_OF_BOUNDS && getCurrentSelectionEnd() != OUT_OF_BOUNDS )
      {
      if( e.isControlDown() )
        {
        controlDownBehavior(e);
        }
      else if( e.isShiftDown() )
        {
        shiftDownBehavior(e);
        }
      else
        {
        nothingDownBehavior(e);
        }
      getCurrentStaveActionHandler().handleGridLineSpacing(e);
      }
    }
    
    
    public void toggleBothEnharmonics() {
        toggleChordEnharmonics();
        toggleMelodyEnharmonics();
        getCurrentStave().repaint();
    }
    
    public void toggleChordEnharmonics() {
        cm.execute(new ToggleEnharmonicCommand(
                getCurrentStave().getChordProg(),
                getCurrentSelectionStart(),
                getCurrentSelectionEnd()));
    }
    
    /**
     * Toggle enharmonics of a group of notes
     */
    
    public void toggleMelodyEnharmonics() {
        cm.execute(new ToggleEnharmonicCommand(
                getCurrentOrigPart(),
                getCurrentSelectionStart(),
                getCurrentSelectionEnd()));
    }
    
    
    
  public void addMeasures(int numNewMeasures)
    {
    // increase score length by getting/setting text field in preferences

    int oldScoreBarLength = Integer.parseInt(prefMeasTF.getText());

    int newScoreBarLength = oldScoreBarLength + numNewMeasures;

    prefMeasTF.setText(String.valueOf(newScoreBarLength));

    Rectangle r = getCurrentScrollPosition();


    // then reinit the length of the score

    int[] metre = getCurrentOrigPart().getMetre();

    initMetreAndLength(metre[0], metre[1], false);

    setCurrentScrollPosition(r);
    }

    
    
  /**
   *
   * Add a rest at the current selection start.
   *
   */
    
  public void addRest()
    {
    //Trace.log(2, "add rest");

    if( slotIsSelected() )
      {

      cm.execute(new SetRestCommand(getCurrentSelectionStart(),
                                    getCurrentOrigPart()));
      redoAdvice();
      getCurrentStave().repaint();
      }
    }
    
    
    
  /**
   *
   * Move current selection to the right.
   *
   */
  
  public void moveRight()
    {
    getCurrentStaveActionHandler().moveSelectionRight(getCurrentSelectionStart() + 1);
    redoAdvice();
    }
    
    
    
    
    
  /**
   *
   * Move current selection to the left.
   *
   */
  
  public void moveLeft()
    {

    getCurrentStaveActionHandler().moveSelectionLeft(getCurrentSelectionStart() - 1);

    redoAdvice();
    }

    
    
  /**
   *
   * Inserts advice if the user clicks on an appropriate node.
   *
   */
  
    private void adviceTreeMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adviceTreeMousePressed

      Trace.log(2, "mouse pressed in advice tree: evt = " + evt);

      //rk FIX: Sorry about this.

      // The following is a crude hack to overcome some JTree nonsense; that the same event

      // is triggered when the menu item is collapsed as when one of its leaves is clicked

      // (resulting in annoying note play when the menu is closed).

      // It is not going to work if nesting of sub-menus gets to be 3 deep

      if( evt.getX() < 50 )
        {

        Trace.log(2, "thwarted");

        return;
        }

      DefaultMutableTreeNode node =
              (DefaultMutableTreeNode)adviceTree.getLastSelectedPathComponent();

      if( evt.getClickCount() == 1 && node.isLeaf() )
        {

        Object object = node.getUserObject();

        adviceSelected(object);
        }

      // set the menu and button states

      setItemStates();
    }//GEN-LAST:event_adviceTreeMousePressed

  void adviceSelected(Object object)
    {
    if( object instanceof Advice && getCurrentSelectionStart() != OUT_OF_BOUNDS )
      {
      if( adviceUsed )
        {

        Trace.log(2, "advice used => undo");

        cm.undo();
        }

      setAdviceUsed();

      ((Advice)object).insertInPart(score.getPart(currTabIndex),
              getCurrentSelectionStart(), cm, this);

      if( ImproVisor.getPlay() )
        {
        ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.NODRUMS);
        }
      getCurrentStave().repaint();
      }
    }
    
    
    
/**
 *
 * Displays an internal frame that allows the user to override the number
 *
 * of measures in a line.
 *
 */
    private void overrideMeasPMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_overrideMeasPMIActionPerformed

        displayOverrideMeasures();

    }//GEN-LAST:event_overrideMeasPMIActionPerformed
    
    
    /**
     *
     * Continuously auto-adjusts every Stave in the Score.
     *
     */
    
    private void autoAdjustMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoAdjustMIActionPerformed

        autoAdjustStaves = !autoAdjustStaves;



        // set the locked measures to the current layout

        setLockedMeasures(getCurrentStave().getLineMeasures(), "autoAdjustMI1"); // who do we need two of these?



        paintCurrentStaveImmediately("autoAdjust");



        // set the locked measures to the newly calculated layout

        setLockedMeasures(getCurrentStave().getLineMeasures(), "autoAdjustMI2");



        // set the menu and button states

        setItemStates();

    }//GEN-LAST:event_autoAdjustMIActionPerformed
    
    
    
    
    
    /**
     *
     * A one-time auto adjust of the current stave that is carried throughout
     *
     * the parts.
     *
     */
    
    private void oneAutoMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneAutoMIActionPerformed
        
        autoAdjustStaves = true;
        
        paintCurrentStaveImmediately("oneAuto");
                
        // reset the locked measures to the newly calculated ones
        
        setLockedMeasures(getCurrentStave().getLineMeasures(), "oneAutoMIAction");
                
        autoAdjustStaves = autoAdjustMI.isSelected();        
    }//GEN-LAST:event_oneAutoMIActionPerformed
    
    
    
    
    
    public static Polylist parseListFromString(String input) {
        
        Tokenizer in = new Tokenizer(new StringReader(input));
        
        Object ob;
        
        Polylist result = Polylist.nil;

        while( (ob = in.nextSexp()) != Tokenizer.eof ) {
            result = result.cons(ob);
        }

        return result.reverse();
        
    }
    
    
    
    
    
    /**
     *
     * Enter chords and melody from text field
     *
     */
    
    
    
    private void enterBoth()
    
    {
        
        if( slotIsSelected() )
            
        {
            
            String chordText = textEntry.getText();
            
            
            
            if( !chordText.equals("") ) {
                
                Trace.log(2, "Entering chords and melody: " + chordText);
                
                cm.execute(new SetChordsCommand(
                        
                        getCurrentSelectionStart(),
                        
                        parseListFromString(chordText),
                        
                        chordProg,
                        
                        partList.get(currTabIndex) ));
                
                
                
                redoAdvice();
                
            }
            
        }
        
    }
    
    
    
    
    
    /**
     *
     * Enter chords from text field
     *
     */
    
    
    
    void enterChords()
    
    {
        
        if( slotIsSelected() ) {
            
            String windowText = textEntry.getText();
            
            
            
            if( !windowText.equals("") ) {
                
                Trace.log(2, "Entering chords from: " + windowText);
                
                
                
                cm.execute(new SetChordsCommand(
                        
                        getCurrentSelectionStart(),
                        
                        parseListFromString(windowText),
                        
                        chordProg,
                        
                        null) );
                
                
                
                redoAdvice();
                
            }
            
        }
        
    }
    
    
    
    
    
    /**
     *
     * Enter melody only from text field
     *
     */
    
    
    
    void enterMelody() {
        
        if( slotIsSelected() ) {
            
            String windowText = textEntry.getText();
            
            
            
// The difference between this and the previous command is found
            
// in how the arguments to the SetChordCommands constructor are
            
// handled.  It is what separates chords from melody.
            
            
            
            if( !windowText.equals("") ) {
                
                
                
                Trace.log(2, "Entering melody from: " + windowText);
                
                
                
                cm.execute(new SetChordsCommand(
                        
                        getCurrentSelectionStart(),
                        
                        parseListFromString(windowText),
                        
                        null,
                        
                        partList.get(currTabIndex) ));
                
                
                
                redoAdvice();
                
            }
            
        }
        
    }
    
    
    
    /**
     *
     * Shows the advice frame or removes it.
     *
     */
    
    private void adviceMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adviceMIActionPerformed
        
        if ( oneSlotSelected() )
        {
          int slot = getCurrentSelectionStart();
          displayAdviceTree(slot, 0, getCurrentStave().getOrigPart().getNote(slot));
        }

        
    }//GEN-LAST:event_adviceMIActionPerformed
    
    
    
    
    
    /**
     *
     * Pastes the selection to the Stave to the clipboard
     *
     */
    
    public void pasteMelodyMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMelodyMIActionPerformed
        
        pasteMelody();
        
    }//GEN-LAST:event_pasteMelodyMIActionPerformed
    
    
    
    
    
    void pasteMelody() {
        
        Trace.log(2, "paste melody");
        
        // paste notes
        
        if (impro.melodyClipboardNonEmpty()) {
            
            pasteMelody(impro.getMelodyClipboard());
            
        }
        
    }
    
    
    
private void pasteMelody(Part part, Stave stave)
  {

    if( slotIsSelected() )
      {
        int i = getCurrentSelectionStart(stave) / beatValue;

        for( ; i < getCurrentSelectionEnd(stave) / beatValue; ++i )
          {
            stave.setSubDivs(i, 2);
          }

        cm.execute(new SafePasteCommand(part,
                                        getCurrentOrigPart(stave),
                                        getCurrentSelectionStart(stave),
                                        !alwaysPasteOver, true, this));

        justPasted = true;

        paintCurrentStaveImmediately("pasteMelody");

        // set the menu and button states

        setItemStates();
      }

  }
    
    
    
    public void pasteMelody(Part part) {
        
        pasteMelody(part, getCurrentStave());
        
    }
    
    
    
    /**
     *
     * Pastes chord selection to the Stave to the clipboard
     *
     */
    
    public void pasteChordsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteChordsMIActionPerformed
        
        pasteChords();
        
    }//GEN-LAST:event_pasteChordsMIActionPerformed
    
    
    
    void pasteChords() {
        
        Trace.log(2, "paste chords");
        
        if ( slotIsSelected() ) {
            
            // paste chords
            
            if (impro.chordsClipboardNonEmpty() )
                
                cm.execute(new SafePasteCommand(impro.getChordsClipboard(),
                        
                        getCurrentStave().getChordProg(),
                        
                        getCurrentSelectionStart(), !alwaysPasteOver, true, this));
            
            paintCurrentStaveImmediately("paste Chords");

            // set the menu and button states
            redoAdvice();
            setItemStates();
            
        }
        
    }
    
    
    
    /**
     *
     * Copys the selection of notes from the Stave to the clipboard
     *
     */
    
    public void copyMelodyMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMelodyMIActionPerformed
        
        copyMelody();
        
    }//GEN-LAST:event_copyMelodyMIActionPerformed
    
    
    
    void copyMelody() {
        
        Trace.log(2, "copy melody");
        
        if ( slotIsSelected() ) {
            
            cm.execute(new CopyCommand(getCurrentOrigPart(),
                    
                    impro.getMelodyClipboard(),
                    
                    getCurrentSelectionStart(),
                    
                    getCurrentSelectionEnd()));
            
            
            
            getCurrentStave().setPasteFromStart(
                    
                    getCurrentSelectionStart());
            
            getCurrentStave().setPasteFromEnd(
                    
                    getCurrentSelectionEnd());
            
            
            
            getCurrentStave().repaint();
            
            
            
            // set the menu and button states
            
            setItemStates();
            
        }
        
    }
    
    
    /**
     *
     * Cuts the selection of notes from the Stave to the clipboard
     *
     */
    
    public void cutMelodyMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMelodyMIActionPerformed
        
        cutMelody();
        
    }//GEN-LAST:event_cutMelodyMIActionPerformed
    
  
  void cutMelody()
    {
    Trace.log(2, "cut melody");

    if( slotIsSelected() )
      {
      cm.execute(new CutCommand(getCurrentOrigPart(),
              impro.getMelodyClipboard(),
              getCurrentSelectionStart(),
              getCurrentSelectionEnd()));
      
      getCurrentStave().setPasteFromStart(
              getCurrentSelectionStart());

      getCurrentStave().setPasteFromEnd(
              getCurrentSelectionEnd());

      setCurrentSelectionEnd(getCurrentSelectionStart());

      setItemStates();
      }
    
    redoAdvice();
    }
    
    
    
  void reverseMelody()
    {
    Trace.log(2, "reverse melody");

    if( slotIsSelected() )
      {
      if( !getCurrentStave().trimSelection() )
        {
        return;  // all rests
        }
      
      noCountIn();

      cm.execute(new ReverseCommand(getCurrentOrigPart(),
              getCurrentSelectionStart(),
              getCurrentSelectionEnd(), true));

      getCurrentStave().setPasteFromStart(
              getCurrentSelectionStart());

      getCurrentStave().setPasteFromEnd(
              getCurrentSelectionEnd());

      redoAdvice();

      // set the menu and button states

      setItemStates();
      }
    }
    
    
    
  void invertMelody()
    {
    Trace.log(2, "invert melody");

    if( slotIsSelected() )
      {
      if( !getCurrentStave().trimSelection() )
        {
        return;  // all rests
        }

      noCountIn();
      cm.execute(new InvertCommand(getCurrentOrigPart(),
              getCurrentSelectionStart(),
              getCurrentSelectionEnd(), true));

      getCurrentStave().setPasteFromStart(
              getCurrentSelectionStart());

      getCurrentStave().setPasteFromEnd(
              getCurrentSelectionEnd());

      redoAdvice();

      // set the menu and button states

      setItemStates();
      }
    }
    
    
    
  void timeWarpMelody(int num, int denom)
    {
    Trace.log(2, "time-warp melody by " + num + "/" + denom);

    if( slotIsSelected() && num > 0 && denom > 0 )
      {
      if( !getCurrentStave().trimSelection() )
        {
        return;  // all rests
        }

      cm.execute(new TimeWarpCommand(getCurrentOrigPart(),
              getCurrentSelectionStart(),
              getCurrentSelectionEnd(), true, num, denom));

      getCurrentStave().setPasteFromStart(
              getCurrentSelectionStart());

      getCurrentStave().setPasteFromEnd(
              getCurrentSelectionEnd());

      redoAdvice();

      // set the menu and button states

      setItemStates();
      }
    }

    
    
  /**
   *
   * Redo the previous command
   *
   */
    public void redoMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMIActionPerformed

      Trace.log(2, "redo command");

      cm.redo();

      redoAdvice();

      // set the menu and button states

      setItemStates();
    }//GEN-LAST:event_redoMIActionPerformed
    
    
    
    
  /**
   *
   * Undo the previous command
   *
   */
    public void undoMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMIActionPerformed

      Trace.log(2, "undo command in Notate");

      cm.undo();

      redoAdvice();

      // set the menu and button states

      setItemStates();
        
    }//GEN-LAST:event_undoMIActionPerformed

  /**
   *
   * Shows the construction lines when the user's mouse is over a measure
   *
   */
    private void measureCstrLinesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_measureCstrLinesMIActionPerformed

      for( int i = 0; i < staveScrollPane.length; i++ )
        {
        Stave stave = staveScrollPane[i].getStave();
        stave.setShowMeasureCL(!stave.getShowMeasureCL());

        stave.repaint();
        }        
    }//GEN-LAST:event_measureCstrLinesMIActionPerformed

  /**
   *
   * Prints the current stave shown. Temporarily removes all construction
   *
   * lines and selections
   *
   * from the stave, while making sure the bar numbers, and title are shown.
   *
   */
    private void printMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printMIActionPerformed

      boolean tempShowAllCL = getCurrentStave().getShowAllCL();

      boolean tempShowMeasureCL = getCurrentStave().getShowMeasureCL();

      int tempStartIndex = getCurrentSelectionStart();

      int tempEndIndex = getCurrentSelectionEnd();

      getCurrentStave().setShowAllCL(false);

      getCurrentStave().setShowMeasureCL(false);

      getCurrentStave().setSelection(OUT_OF_BOUNDS);

      getCurrentStave().setPrinting(true);

      setCursor(new Cursor(Cursor.WAIT_CURSOR));


      PrintUtilities.printComponent(getCurrentStave());


      getCurrentStave().setShowAllCL(tempShowAllCL);

      getCurrentStave().setShowMeasureCL(tempShowMeasureCL);

      getCurrentStave().setSelection(tempStartIndex);

      getCurrentStave().setPrinting(false);

      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));      
    }//GEN-LAST:event_printMIActionPerformed

  /**
   *
   * Displays the construction lines on the score if checked in the menu
   *
   */
    private void allCstrLinesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCstrLinesMIActionPerformed

      for( int i = 0; i < staveScrollPane.length; i++ )
        {

        Stave stave = staveScrollPane[i].getStave();

        stave.setShowAllCL(!stave.getShowAllCL());

        stave.repaint();

        }
        
    }//GEN-LAST:event_allCstrLinesMIActionPerformed

  /**
   *
   * Displays the bar numbers on the score if checked in the menu
   *
   */
    private void barNumsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barNumsMIActionPerformed

      for( int i = 0; i < staveScrollPane.length; i++ )
        {

        Stave stave = staveScrollPane[i].getStave();

        stave.setShowBarNums(!stave.getShowBarNums());

        stave.repaint();

        }
        
    }//GEN-LAST:event_barNumsMIActionPerformed

  /**
   *
   * Displays the title of the score if checked in the menu
   *
   */
    private void showTitlesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTitlesMIActionPerformed

      for( int i = 0; i < staveScrollPane.length; i++ )
        {

        Stave stave = staveScrollPane[i].getStave();

        // Show sheet title on first tab only
        stave.setShowSheetTitle(i == 0 && showTitlesMI.isSelected());
        stave.setShowPartTitle(showTitlesMI.isSelected());

        stave.repaint();

        }
        
    }//GEN-LAST:event_showTitlesMIActionPerformed

  /**
   *
   * Stops the playback of the score if selected
   *
   */
    public void stopPlayMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopPlayMIActionPerformed

      stopPlaying();
        
    }//GEN-LAST:event_stopPlayMIActionPerformed

  /**
   *
   * Displays the score in a particular <code>type</code> --grand, treble, or
   *
   * bass-- depending on the variety of pitches in the score
   *
   */
    private void autoStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoStaveMIActionPerformed

      setCursor(new Cursor(Cursor.WAIT_CURSOR));

      for( int i = 0; i < staveScrollPane.length; i++ )
        {

        Stave stave = staveScrollPane[i].getStave();

        stave.changeType(StaveType.AUTO);
        }

      getCurrentOrigPart().setStaveType(StaveType.AUTO);

      setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  
    }//GEN-LAST:event_autoStaveMIActionPerformed

  private void setCurrentStaveType(StaveType t)
  {
    //System.out.println("setCurrentStaveType(" + t + ")");

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    Stave stave = getCurrentStave();

    // set stave buttons

    switch( t )
      {
      case TREBLE:
        trebleStaveBtn.setSelected(true);
        break;

      case BASS:
        bassStaveBtn.setSelected(true);
        break;

      case GRAND:
        grandStaveBtn.setSelected(true);
        break;

      case AUTO:
        autoStaveBtn.setSelected(true);
        break;

      //  case NONE:   noStaveBtn.setSelected(true);     break;
      }

    stave.changeType(t);
    getCurrentOrigPart().setStaveType(t);
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }
  
  
  /**
   *
   * Displays the score as a grand stave
   *
   */
    private void grandStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grandStaveMIActionPerformed
      setCurrentStaveType(StaveType.GRAND);
    }//GEN-LAST:event_grandStaveMIActionPerformed

  /**
   *
   * Displays the score in bass clef format
   *
   */
    private void bassStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassStaveMIActionPerformed
      setCurrentStaveType(StaveType.BASS);
    }//GEN-LAST:event_bassStaveMIActionPerformed

  /**
   *
   * Displays the score in treble clef format
   *
   */
    private void trebleStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trebleStaveMIActionPerformed
      setCurrentStaveType(StaveType.TREBLE);
    }//GEN-LAST:event_trebleStaveMIActionPerformed

  /**
   *
   * Plays the score until the score's end
   *
   */
    public void playAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playAllMIActionPerformed

        if (keyboard != null)
        {
            keyboard.setPlayback(true);
        }
        playScore();
        
    }//GEN-LAST:event_playAllMIActionPerformed

 public void establishCountIn()
 {
 score.setCountIn(countInCheckBox.isSelected() ? makeCountIn() : null );
 }

 public void noCountIn()
 {
 score.noCountIn();
 }

 public void playScore()
  {
     establishCountIn();
     playScoreBody(0);
  }

 
 public void playScore(int startAt)
    {
     noCountIn();
     playScoreBody(startAt);
    }

 
public void playScoreBody(int startAt)
    {
    if( getPlaying() == MidiPlayListener.Status.PAUSED )
      {

      Trace.log(2, "Notate: playScore() - unpausing");

      pauseScore();

      }
    else
      {

      Trace.log(2, "Notate: playScore() - starting or restarting playback");

      // makes playback indicator always visible
      // set to false upon user scroll

      autoScrollOnPlayback = true;


      if( getPlaying() == MidiPlayListener.Status.STOPPED )
        {
        // possible loss of precision below: check this
        startAt = (int)playbackManager.getMicrosecondsFromSlider();

        clearKeyboard();
        clearVoicingEntryTF();
        resetChordDisplay();
        }

      // reset playback offset

      initCurrentPlaybackTab(0, 0);

      loopsRemaining = getLoopCount();
      getCurrentStave().play(startAt);
      }
    }


  /**
   * Construct count-in pattern if one is specified
   */

public ChordPart makeCountIn()
  {
    String instrument[] =
      {
        "High Tom", "Side Stick"

      // possible alternates:
      // "Low Bongo", "Hi Bongo"
      // "Acoustic Bass Drum", "Acoustic Snare"
      };

    double tempo = score.getTempo();

    int[] metre = score.getMetre();

    int measures = 2;

    int beatsInMeasure = metre[0];

    int oneBeat = metre[1];

    double tempoFactor = oneBeat/4.0;

    double apparentTempo = tempo*tempoFactor;

    StyleEditor se = getStyleEditor();

    DrumPatternDisplay drumPattern = new DrumPatternDisplay(this, cm, se);

    // Treat 4/4 as a special case, for jazz-style count-in, 2-bar pattern:
    // 1-2-1234

    if( beatsInMeasure == 4 && oneBeat == 4 )
      {
        String pattern1 = "X4 R4 X4 R4 X4 X4 X4 X4";
        DrumRuleDisplay drumRule1 =
            new DrumRuleDisplay(pattern1, instrument[1], this, cm,
                            drumPattern, se);

        drumPattern.addRule(drumRule1);
      }
    else
      {
        StringBuffer buffer[] =
          {
            new StringBuffer(), new StringBuffer()
          };

        // Accumulate percussion hits for two tracks,
        // based on the number of measures and beats per measure.

        for( int measure = 0; measure < measures; measure++ )
          {
            // Handle downbeat

            buffer[0].append("X8 R8 ");
            buffer[1].append("R4 ");

            // Handle other beats
            for( int beat = 1; beat < beatsInMeasure; beat++ )
              {
                buffer[0].append("R4 ");
                buffer[1].append("X8 R8 ");
              }
          }


    String pattern[] =
         {
         buffer[0].toString(),
         buffer[1].toString()
         };

       DrumRuleDisplay drumRule[] =
         {
         new DrumRuleDisplay(pattern[0], instrument[0], this, cm,
                            drumPattern, se),
         new DrumRuleDisplay(pattern[1], instrument[1], this, cm,
                            drumPattern, se)
         };

      // Add the created rules to the drum pattern.

      drumPattern.addRule(drumRule[0]);
      drumPattern.addRule(drumRule[1]);
      }

    return drumPattern.makeCountIn(0.5, 0, apparentTempo);
  }


 
  public void initCurrentPlaybackTab(int offset)
    {

    initCurrentPlaybackTab(offset, currTabIndex);

    }

  private int playbackOffset = 0;

  public void initCurrentPlaybackTab(int offset, int tab)
    {

    playbackOffset = tab * getScore().getLength() + offset;

    currentPlaybackTab = tab;

    }

  public void enableStopButton(boolean enabled)
    {

    stopBtn.setEnabled(enabled);

    }

  public void setPlaying(MidiPlayListener.Status playing, int transposition)
    {

    Trace.log(2, "Notate: Play Status Changed to " + playing);

    // update the playbackManager
     
    playbackManager.setPlaying(playing, transposition);


    isPlaying = playing;

    switch( playing )
      {

      case PLAYING:

        pauseBtn.setEnabled(true);

        stopBtn.setEnabled(true);

        pauseBtn.setSelected(false);

        recordBtn.setEnabled(false);

        break;

      case PAUSED:

        pauseBtn.setEnabled(true);

        stopBtn.setEnabled(true);

        pauseBtn.setSelected(true);

        recordBtn.setEnabled(false);

        break;

      case STOPPED:

        //setShowPlayLine(false);

        pauseBtn.setEnabled(false);

        stopBtn.setEnabled(false);

        pauseBtn.setSelected(false);

        recordBtn.setEnabled(true);

        if( mode == Mode.RECORDING )
          {

          stopRecording();

          }

        setPlaybackManagerTime();

        getCurrentStave().repaint();

        break;

      }

    staveRequestFocus();
    }

  public MidiPlayListener.Status getPlaying()
    {
    return isPlaying;
    }

  /**
   *
   * Closes the current notateFrame
   *
   */
    private void quitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMIActionPerformed

      // initialize the option pane

      Object[] options = {"Yes", "No"};

      int choice = JOptionPane.showOptionDialog(this,
              "Do you wish to quit Impro-Visor?", "Quit",
              JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
              null, options, options[1]);



      // the user selected yes

      if( choice == 0 )
        {

        midiSynth.stop("quit");

        adv.showMarkedItems();

        System.exit(0);

        }
        
    }//GEN-LAST:event_quitMIActionPerformed

  public MidiSynth getMidiSynth()
    {
    return midiSynth;
    }

  /**
   *
   * Saves the current score as a leadsheet file
   *
   * Assumes the user will put leadsheetExt as their file extension
   *
   */
    public void saveAsLeadsheetMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsLeadsheetMIActionPerformed

      saveAsLeadsheet();
        
    }//GEN-LAST:event_saveAsLeadsheetMIActionPerformed

  /** Saves the current leadsheet.
   * This is the general function to call to do Save As...
   * @see saveAsLeadsheetAWT()
   * @see saveAsLeadsheetSwing()
   */
  public boolean saveAsLeadsheet()
    {
    int isMac = System.getProperty("os.name").toLowerCase().indexOf("mac");
    if( isMac > -1 )
      {
      return saveAsLeadsheetAWT();
      }
    return saveAsLeadsheetSwing();
    }

  /** Save the current leadsheet using AWT interface.
   * This works better on Macs.
   * @see saveAsLeadsheetSwing()
   */
  public boolean saveAsLeadsheetAWT()
    {
    if( saveAWT.getDirectory().equals("/") )
      {
      saveAWT.setDirectory(leadsheetDir.getAbsolutePath());
      }

    saveAWT.setVisible(true);
    String selected = saveAWT.getFile();
    String dir = saveAWT.getDirectory();
    if( selected != null )
      {

      boolean noErrors = true;

      if( !selected.endsWith(leadsheetExt) )
        {

        selected += leadsheetExt;

        }

      noErrors = saveLeadsheet(new File(dir + selected), score);
      setTitle(score.getTitle());
      savedLeadsheet = new File(dir + selected);

      if( !savedLeadsheet.exists() )
        {
        savedLeadsheet = null;
        return false;
        }

      return noErrors;
      }

    return false;
    }

  /** Save current leadsheet using Swing interface.
   * This doesn't appear to work on Macs nicely.
   * @see saveAsLeadsheetAWT()
   */
public boolean saveAsLeadsheetSwing()
  {
    if( saveLSFC.getCurrentDirectory().getAbsolutePath().equals("/") )
      {
        saveLSFC.setCurrentDirectory(leadsheetDir);
      }

    if( saveLSFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {

        boolean noErrors = true;

        if( saveLSFC.getSelectedFile().getName().endsWith(leadsheetExt) )
          {
            noErrors = saveLeadsheet(saveLSFC.getSelectedFile(), score);

            setTitle(score.getTitle());

            savedLeadsheet = saveLSFC.getSelectedFile();
          }
        else
          {
            String file = saveLSFC.getSelectedFile().getAbsolutePath();

            file += leadsheetExt;

            noErrors = saveLeadsheet(new File(file), score);

            setTitle(score.getTitle());

            savedLeadsheet = new File(file);
          }


        if( !savedLeadsheet.exists() )
          {
            savedLeadsheet = null;

            return false;
          }

        return noErrors;
      }

    return false;
  }

  /**
   *
   * Allows a Leadsheet file to be opened.
   *
   * Assumes they will only open files with the extension leadsheetExt.
   *
   */
    public void openLeadsheetMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openLeadsheetMIActionPerformed
      openLeadsheet(false);        
    }//GEN-LAST:event_openLeadsheetMIActionPerformed


  /**
   *
   * Shows open leadsheet dialog and opens the leadsheet if the user reqeusts
   *
   * it through the dialog
   *
   */
public void openLeadsheet(boolean openCorpus)
  {
    if( openLSFC.getCurrentDirectory().getAbsolutePath().equals("/") )
      {
        openLSFC.setCurrentDirectory(leadsheetDir);
      }

    // stopPlaying(); experimental

    // show open file dialog

    if( openLSFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        // if user wants to open in a new window (via checkbox in dialog)

        if( lsOpenPreview.getCheckbox().isSelected() )
          {
            // load the file

            Score newScore = new Score();

            score.setChordFontSize(Integer.valueOf(Preferences.getPreference(
                Preferences.DEFAULT_CHORD_FONT_SIZE)).intValue());

            (new OpenLeadsheetCommand(openLSFC.getSelectedFile(), newScore)).execute();



            // create a new window and show the score

            Notate newNotate =
                new Notate(newScore,
                           this.adv,
                           this.impro,
                           (int) this.getLocation().getX() + WindowRegistry.defaultXnewWindowStagger,
                           (int) this.getLocation().getY() + WindowRegistry.defaultYnewWindowStagger);

            setNotateFrameHeight(newNotate);
          }
        else
          {
            // if not a new window

            boolean redisplay = true;

            while( redisplay )
              {
                redisplay = false;

                // check to see if we will lose changes

                if( unsavedChanges() )
                  {
                    // if we are going to lose changes, prompt the user

                    Object[] options =
                      {
                        "<html><b><u>Y</u>es</b>, save these modifications</html>",
                        "<html><b><u>N</u>o</b>, do not save these modifications</html>",
                        "<html><b>Cancel</b>, do not open a different leadsheet</html>"
                      };

                    UnsavedChanges dialog =
                        new UnsavedChanges(this,
                                           "Save modifications before opening " + lsOpenPreview.getTitle() + "?",
                                           options);

                    dialog.setVisible(true);

                    dialog.dispose();

                    UnsavedChanges.Value choice = dialog.getValue();

                    switch( choice )
                      {
                        case YES:   // save before opening

                            if( !saveLeadsheet() )
                              {
                                redisplay = true;
                              }

                            break;

                        case NO:    // open without saving

                            break;

                        case CANCEL:// don't open

                            return;
                      }
                  }
              }

            //get the contours of all leadsheets in the directory
            if( openCorpus )
              {
                File file = openLSFC.getSelectedFile();
                setLickGenStatus("Learning from directory containing: " + file);
                /* Note: If these are not called as a thread, annoying paintImmediately messages
                can be avoided, although heap space can become an issue. */
                System.gc();
                getAllContours(file);
                setupLeadsheet(file, true);
                setLickGenStatus("Done learning from corpus: " + file);
                /*
                try {
                new Thread() {
                public void run() {
                getAllContours(openLSFC.getSelectedFile());
                setupLeadsheet(openLSFC.getSelectedFile(), true);
                }
                }.start();
                }
                catch (Exception e) {
                //this is a normal bug
                 */

              }
            else
              {
                // open the file
                setupLeadsheet(openLSFC.getSelectedFile(), false);
              }
            // clear undo/redo history

            cm.clearHistory();

            // mark sheet as saved in it's current state (no unsaved changes)

            cm.changedSinceLastSave(false);
          }

        setChordFontSizeSpinner(score.getChordFontSize());
      }

    staveRequestFocus();
  }
    
  /**
   *
   * Reverts the leadsheet file to the previous saved version.
   *
   */
  public void revertLeadsheet(java.awt.event.ActionEvent evt)
    {

    if( savedLeadsheet == null )
      {
      return;        // nothing save
      }

    revertLSFC.setSelectedFile(savedLeadsheet);
    revertLSFC.resetChoosableFileFilters();
    revertLSFC.addChoosableFileFilter(new SingleFileFilter(savedLeadsheet));

    if( revertLSFC.showDialog(this, "Revert without saving!") == JFileChooser.APPROVE_OPTION )
      {
      setupLeadsheet(savedLeadsheet, false);

      // clear undo/redo history

      cm.clearHistory();

      // mark sheet as saved in it's current state (no unsaved changes)

      cm.changedSinceLastSave(false);
      }
    staveRequestFocus();
    }

  /**
   *
   * Do stuff that is common to open and revert file.
   *
   */
    public void setupLeadsheet(File file, boolean openCorpus) {

        Advisor.useBackupStyles();
        Score newScore = new Score();

        cm.execute(new OpenLeadsheetCommand(file, newScore));

        savedLeadsheet = file;
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        setupScore(newScore);
        if (openCorpus) {

            // Note that learning is only occurring with 4/4 time files currently!!
            
            // if (newNotate.getMetre()[1] == 4 && newNotate.getMetre()[0] == 4)
              {
                //System.out.println(newNotate.getTitle());
                getAllMeasures(newScore);
                setLickGenStatus("Reading leadsheet from file " + file);
            }
        }
    }
    
    public void getAllMeasures(Score s) {
        allMeasures = true;
        
        int HEAD = 0;                
        melodyData = new ArrayList<String>();
            
        for (int i = 0; i < staveScrollPane.length; i++) {
            //System.out.println("Chorus " + i+1 + ":");
            scoreTab.setSelectedComponent(staveScrollPane[i]);
            melodyData = getMelodyData(s, i);
            //get the abstract melodies of all except the head
            //if(i != 0) {
            selectAll2();
            lickgenFrame.getAbstractMelody();
            //}
            //get the exact melodies
            //if (i == HEAD) {
            //    headData = getMelodyData(s,i);
            //}
            
            melodyData = getMelodyData(s, i);
        }
        allMeasures = false;
    }

        public ArrayList<String> getMelodyData(int chorusNumber)
              {
                  return getMelodyData(score, chorusNumber);
              }
    
    /*Returns a vector of Strings representing a section of the melody of a
     *chorus and containing the notes in the section     */
    public ArrayList<String> getMelodyData(Score s, int chorusNumber) {
        MelodyPart melPart = s.getPart(chorusNumber).copy();
        ArrayList<String> sections = new ArrayList<String>();
        int numSlots = melPart.getSize();
        int slotsPerSection = lickgenFrame.getWindowSize()* BEAT;
        int windowSlide = lickgenFrame.getWindowSlide() * BEAT;
        //loop through sections
        for(int window = 0; window < slotsPerSection; window += windowSlide) {
        //for (int i = 0; (i * slotsPerSection) + (window * BEAT) + slotsPerSection <= melPart.getSize(); i++) {
            for (int i = 0 + (window); i <= numSlots - slotsPerSection; i += slotsPerSection) {
            String measure = Integer.toString(i) + " ";
            int tracker = 0;
            melPart.truncateEndings(true);
            MelodyPart p = melPart.extract(i, i + slotsPerSection - 1);
            melPart.truncateEndings(false);
            //if note is held from previous section, add that first
            if(melPart.getPrevNote(i) != null && melPart.getPrevNote(i).getRhythmValue() > i - melPart.getPrevIndex(i)) {
                Note currentNote = melPart.getPrevNote(i);
                measure = measure.concat(Integer.toString(currentNote.getPitch()));
                measure = measure.concat(" ");
                int len = currentNote.getRhythmValue();
                len -= (i-melPart.getPrevIndex(i));
                //measure = measure.concat (Integer.toString(i - melPart.getPrevIndex(i)));
                measure = measure.concat(Integer.toString(len));
                measure = measure.concat(" ");
                tracker = p.getNextIndex(0);
            }
            //System.out.println("p.size: " + p.getSize());
            //set tracker to index of first note
            if (p.getNote(0) == null) {
                tracker = p.getNextIndex(0);
            //truncate notes tied to next measure
            }
            int sumOfRhythmValues = 0;
            //add representations of all notes in measure to a string
            while (tracker < p.getSize()) {
                //System.out.println("Tracker = " + tracker);
                Note currentNote = p.getNote(tracker);
                measure = measure.concat(Integer.toString(currentNote.getPitch()));
                measure = measure.concat(" ");
                int length = currentNote.getRhythmValue();
                if (sumOfRhythmValues + length > slotsPerSection) {
                    length = slotsPerSection - sumOfRhythmValues;
                }
                sumOfRhythmValues += length;
                measure = measure.concat(Integer.toString(length));
                measure = measure.concat(" ");
                tracker = p.getNextIndex(tracker);
            }
            sections.add(measure);
        }
        }
        return sections;
    }

    
    
    public void getAllContours (File leadsheet) {
        File directory = leadsheet.getParentFile();
        //System.out.println(leadsheet.getParentFile());
        File[] files = directory.listFiles();
        for(int i = 0; i < files.length; i++) {
            String tempFile = files[i].getPath();
            if(!(files[i].getAbsolutePath().equals(leadsheet.getAbsolutePath())) && tempFile.endsWith(".ls")) {
                setupLeadsheet(files[i], true);
            }
        }
    }
    
    public void writeHeadData() {
        setLickGenStatus("Writing head data");
        //get the list of files
        File directory = new File("leadsheets" + File.separator + "transcriptions" + File.separator +
                "TunesWithHeads");
        File[] files = directory.listFiles();

        //set up the output stream and output file
        FileOutputStream fileOut = null;
        ObjectOutputStream objOut = null;

        File outFile = new File("Vocab" + File.separator + "HeadData.data");

        try {
            fileOut = new FileOutputStream(outFile);
            objOut = new ObjectOutputStream(fileOut);

            //open each leadsheet file, and write the chord part and melody part to the file
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                if (file.toString().endsWith(".ls")) {
                    Score newScore = new Score();

                    cm.execute(new OpenLeadsheetCommand(file, newScore));

                    savedLeadsheet = file;
                    setCursor(new Cursor(Cursor.WAIT_CURSOR));
                    //setupScore(newNotate);

                    ChordPart chords = newScore.getChordProg();
                    MelodyPart melody = newScore.getPart(0);

                    objOut.writeObject(newScore);
                }
            }

            objOut.close();

        } catch (IOException ex) {
            ex.printStackTrace();
        }

    }
    
public void WriteLeadsheetToFile(File file) {
        Advisor.useBackupStyles();
        
    }
 
     /* 
     * Normalizes a vector of segments to start on 0
     */ 
    private Vector<int[]> normalizePhrase(Vector<int[]> segments) {
        Vector<int[]> normalSegments = new Vector<int[]>();
        int firstPitch = segments.get(0)[0];
        for(int i=0; i<segments.size(); i++) {
           int[] nextNote = segments.get(i);
           nextNote[0] -= firstPitch;
           normalSegments.add(nextNote);
        }
        return normalSegments;
    }
    
    /*
     * Translates a midi number to a note character
     */

    public String translateMidi(int value) {
        int midiValue = value % 12;
        switch (midiValue) {
            case 0:
                return "C";
            case 1:
                return "C#";
            case 2:
                return "D";
            case 3:
                return "D#";
            case 4:
                return "E";
            case 5:
                return "F";
            case 6:
                return "F#";
            case 7:
                return "G";
            case 8:
                return "G#";
            case 9:
                return "A";
            case 10:
                return "A#";
            case 11:
                return "B";
            default:
                System.out.println("Midi number bad.");
                return "";
        }
    }

    /* Takes index and finds last index with note in it
     */
    public int getLastNoteIndex(int slot, MelodyPart melpart) {
        while (melpart.getNote(slot).isRest()) {
            slot = melpart.getPrevIndex(slot);
        }
        return slot;
    }
        
    /* Takes indices of two slots which define a segment
     * Gets all intervals in the segment
     * returns an array with the min interval in index 0 and max in index 1
     */
    public int[] getIntervals(int startSlot, int endSlot, MelodyPart melpart) {
        int interval = 0;
        int minInt = 0; 
        int maxInt = 0;
        boolean firstTimeThrough = true;
        
        
        int[] intervals = new int[2];
        while (startSlot < endSlot) {
            Note firstNote = melpart.getNote(startSlot);
            startSlot = getNextNoteIndex(melpart, startSlot);
            Note secondNote = melpart.getNote(startSlot);

            interval = secondNote.getPitch() - firstNote.getPitch();
            if (firstTimeThrough) {
                minInt = interval;
                maxInt = interval;
                firstTimeThrough = false;
            }
            if (interval < minInt) {
                minInt = interval;
            }
            if (interval > maxInt) {
                maxInt = interval;
            }

        }
         
        intervals[0] = minInt;
        intervals[1] = maxInt;
        return intervals;
    }
    
    /*
     * Takes a melody part and an index 
     * Returns the next note after the index that is not a rest
     */

    public int getNextNoteIndex(MelodyPart melpart, int tracker) {
        Note nextNote;
        do {
            tracker = melpart.getNextIndex(tracker);
            nextNote = melpart.getNote(tracker);
        } while (nextNote.isRest());
        return tracker;
    }

    /**
     * takes a melody part and an index
     * returns whether or not the note at the index ends a phrase
     */
    public boolean phraseEnd(MelodyPart melpart, int tracker) {
        Note currentNote = melpart.getNote(tracker);
        if (currentNote.isRest() && currentNote.getRhythmValue() >= 60) {
            return true;
        } else if (currentNote.getRhythmValue() >= 240) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *  Takes a melody part and an index
     *  Returns 3 element array of ints
     *  first elt. Returns whether the pitch is increasing or decreasing at that point
     *  Returns 2 for increasing and 0 for decreasing
     *  second elt. returns index of first note of phrase
     *  third elt. returns index of next note of phrase with different pitch
     */
    public int[] getDirection(MelodyPart melpart, int tracker) {
        int[] startSpot = new int[3];
        if (tracker != 0) {
            tracker = melpart.getNextIndex(tracker);
        }
        int firstPitch = melpart.getNote(tracker).getPitch();
        while (firstPitch == -1) {
            tracker = melpart.getNextIndex(tracker);
            firstPitch = melpart.getNote(tracker).getPitch();
        }
        startSpot[1] = tracker;
        tracker = melpart.getNextIndex(tracker);
        int secondPitch = melpart.getNote(tracker).getPitch();
        while (firstPitch == secondPitch || secondPitch == -1) {
            tracker = melpart.getNextIndex(tracker);
            secondPitch = melpart.getNote(tracker).getPitch();
        }
        startSpot[2] = tracker;
        if (secondPitch > firstPitch) {
            startSpot[0] = 2;
        } else {
            startSpot[0] = 0;
        }
        return startSpot;
    }
  
  /**
   *
   * Do stuff that is common to open file, revert file, and
   *
   * transferring contents from editor.
   *
   */
  public void setupScore(Score score)
    {
    // set the new score

    this.score = score;

    setTitle(score.getTitle());

    // reset the current scoreFrame

    setupArrays();
    
    setTempo(score.getTempo());
    
    clearLayoutPreference();

    Polylist layout = score.getLayoutList();

    adjustLayout(layout);

    closeAdviceFrame();

    int bars = score.getLength();

    setPrefsDialog();

    saveLeadsheetPreferences();

    initMetreAndLength(score.getMetre()[0], score.getMetre()[1], true);

    // set the menu and button states

    setItemStates();

    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

    currTabIndex = 0;

    scoreTab.setSelectedIndex(currTabIndex);
    
    setTransposition(score.getTransposition());
    }

  public boolean adviceIsShowing()
    {

    return adviceFrame != null && adviceFrame.isShowing();
    }

  /**
   * Creates a new Notate with a blank sheet for editing
   *
   */
    public void newMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMIActionPerformed
      newNotate();
    }//GEN-LAST:event_newMIActionPerformed

  public void newNotate()
    {
    Score newScore = new Score("");
    
    ensureChordFontSize();

    int chordFontSize = Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE)).intValue();

    newScore.setChordFontSize(chordFontSize);
    
    newScore.setTempo(getDefaultTempo());

    newScore.setChordProg(new ChordPart());

    newScore.addPart(new MelodyPart(defaultBarsPerPart * measureLength));

    // open a new window

    Notate newNotate =
            new Notate(newScore,
            this.adv, this.impro,
            (int)this.getLocation().getX() + WindowRegistry.defaultXnewWindowStagger,
            (int)this.getLocation().getY() + WindowRegistry.defaultYnewWindowStagger);

    setNotateFrameHeight(newNotate);

    // set the menu and button states

    setItemStates();
    }


  /**
   * Set the height of specified notate frame so that it fills the screen.
   * This seems to work fine when the dock is at the right, but when
   * it is at the bottom, for some reason vertical staggering does not happen.
   @param notate
   */
  
  public void setNotateFrameHeight(Notate notate)
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices(); // Get size of each screen
    DisplayMode dm = gs[0].getDisplayMode();
//System.out.println("height = " + dm.getHeight() + ", y = " + notate.getY());
    notate.setSize(fWidth, dm.getHeight() - notate.getY());
  }

    private void trackerDelayTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_trackerDelayTextFieldActionPerformed
    {//GEN-HEADEREND:event_trackerDelayTextFieldActionPerformed
      setTrackerDelay(trackerDelayTextField.getText());
    }//GEN-LAST:event_trackerDelayTextFieldActionPerformed

    private void cancelTruncateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelTruncateActionPerformed
    {//GEN-HEADEREND:event_cancelTruncateActionPerformed
      cancelTruncation = true;
      truncatePartDialog.setVisible(false);
}//GEN-LAST:event_cancelTruncateActionPerformed

    private void acceptTruncateActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_acceptTruncateActionPerformed
    {//GEN-HEADEREND:event_acceptTruncateActionPerformed
      cancelTruncation = false;
      truncatePartDialog.setVisible(false);
}//GEN-LAST:event_acceptTruncateActionPerformed

    private void truncatePartDialogComponentShown(java.awt.event.ComponentEvent evt)//GEN-FIRST:event_truncatePartDialogComponentShown
    {//GEN-HEADEREND:event_truncatePartDialogComponentShown
      // TODO add your handling code here:
}//GEN-LAST:event_truncatePartDialogComponentShown

    private void truncatePartDialogKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_truncatePartDialogKeyPressed
    {//GEN-HEADEREND:event_truncatePartDialogKeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_truncatePartDialogKeyPressed

    private void trackerDelayTextField2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_trackerDelayTextField2ActionPerformed
    {//GEN-HEADEREND:event_trackerDelayTextField2ActionPerformed
      setTrackerDelay(trackerDelayTextField2.getText());
}//GEN-LAST:event_trackerDelayTextField2ActionPerformed

    private void trackerDelayTextField2FocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_trackerDelayTextField2FocusGained
    {//GEN-HEADEREND:event_trackerDelayTextField2FocusGained
      // TODO add your handling code here:
}//GEN-LAST:event_trackerDelayTextField2FocusGained

    private void trackerDelayTextField2FocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_trackerDelayTextField2FocusLost
    {//GEN-HEADEREND:event_trackerDelayTextField2FocusLost
      // TODO add your handling code here:
}//GEN-LAST:event_trackerDelayTextField2FocusLost

    private void trackerDelayTextField2KeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_trackerDelayTextField2KeyTyped
    {//GEN-HEADEREND:event_trackerDelayTextField2KeyTyped
      // TODO add your handling code here:
}//GEN-LAST:event_trackerDelayTextField2KeyTyped

    private void trackerDelayTextField2KeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_trackerDelayTextField2KeyPressed
    {//GEN-HEADEREND:event_trackerDelayTextField2KeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_trackerDelayTextField2KeyPressed

    private void trackerDelayTextField2KeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_trackerDelayTextField2KeyReleased
    {//GEN-HEADEREND:event_trackerDelayTextField2KeyReleased
      // TODO add your handling code here:
}//GEN-LAST:event_trackerDelayTextField2KeyReleased

    private void trackerDelayTextField2MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_trackerDelayTextField2MousePressed
    {//GEN-HEADEREND:event_trackerDelayTextField2MousePressed
      // TODO add your handling code here:
}//GEN-LAST:event_trackerDelayTextField2MousePressed

    private void partBarsTF1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_partBarsTF1ActionPerformed
    {//GEN-HEADEREND:event_partBarsTF1ActionPerformed
      setPartBars(partBarsTF1.getText());
}//GEN-LAST:event_partBarsTF1ActionPerformed

    private void partBarsTF1FocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_partBarsTF1FocusGained
    {//GEN-HEADEREND:event_partBarsTF1FocusGained
      // TODO add your handling code here:
}//GEN-LAST:event_partBarsTF1FocusGained

    private void partBarsTF1FocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_partBarsTF1FocusLost
    {//GEN-HEADEREND:event_partBarsTF1FocusLost
      // TODO add your handling code here:
}//GEN-LAST:event_partBarsTF1FocusLost

    private void partBarsTF1KeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_partBarsTF1KeyTyped
    {//GEN-HEADEREND:event_partBarsTF1KeyTyped
      // TODO add your handling code here:
}//GEN-LAST:event_partBarsTF1KeyTyped

    private void partBarsTF1KeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_partBarsTF1KeyPressed
    {//GEN-HEADEREND:event_partBarsTF1KeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_partBarsTF1KeyPressed

    private void partBarsTF1KeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_partBarsTF1KeyReleased
    {//GEN-HEADEREND:event_partBarsTF1KeyReleased
      // TODO add your handling code here:
}//GEN-LAST:event_partBarsTF1KeyReleased

    private void partBarsTF1MousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_partBarsTF1MousePressed
    {//GEN-HEADEREND:event_partBarsTF1MousePressed
      // TODO add your handling code here:
}//GEN-LAST:event_partBarsTF1MousePressed

    private void prefMeasTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_prefMeasTFActionPerformed
    {//GEN-HEADEREND:event_prefMeasTFActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_prefMeasTFActionPerformed

    private void colorationButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_colorationButtonActionPerformed
    {//GEN-HEADEREND:event_colorationButtonActionPerformed
      if( noteColoration )
        {
        noteColoration = false;
        colorationButton.setBackground(Color.red);
        colorationButton.setText("Color");
        }
      else
        {
        noteColoration = true;
        colorationButton.setBackground(new Color(153, 204, 255));
        colorationButton.setText("B/W");
        }
      
}//GEN-LAST:event_colorationButtonActionPerformed

    private void smartEntryButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_smartEntryButtonActionPerformed
    {//GEN-HEADEREND:event_smartEntryButtonActionPerformed
      if( smartEntry )
        {
        smartEntry = false;
        smartEntryButton.setBackground(Color.red);
        smartEntryButton.setText("Harmonic");
        }
      else
        {
        smartEntry = true;
        smartEntryButton.setBackground(new Color(255, 153, 255));
        smartEntryButton.setText("Simple");
        }
}//GEN-LAST:event_smartEntryButtonActionPerformed

    private void transposeMelodyUpHarmonicallyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_transposeMelodyUpHarmonicallyActionPerformed
    {//GEN-HEADEREND:event_transposeMelodyUpHarmonicallyActionPerformed
      getCurrentStave().transposeMelodyUpHarmonically();
}//GEN-LAST:event_transposeMelodyUpHarmonicallyActionPerformed

    private void transposeMelodyDownHarmonicallyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_transposeMelodyDownHarmonicallyActionPerformed
    {//GEN-HEADEREND:event_transposeMelodyDownHarmonicallyActionPerformed
      getCurrentStave().transposeMelodyDownHarmonically();

}//GEN-LAST:event_transposeMelodyDownHarmonicallyActionPerformed

    private void alwaysUseBassActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alwaysUseBassActionPerformed
    {//GEN-HEADEREND:event_alwaysUseBassActionPerformed
      setCheckBoxPreferences(0, alwaysUseBass);
}//GEN-LAST:event_alwaysUseBassActionPerformed

    private void alwaysUseChordActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alwaysUseChordActionPerformed
    {//GEN-HEADEREND:event_alwaysUseChordActionPerformed
      setCheckBoxPreferences(1, alwaysUseChord);
}//GEN-LAST:event_alwaysUseChordActionPerformed

    private void alwaysUseMelodyActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_alwaysUseMelodyActionPerformed
    {//GEN-HEADEREND:event_alwaysUseMelodyActionPerformed
      setCheckBoxPreferences(2, alwaysUseMelody);
}//GEN-LAST:event_alwaysUseMelodyActionPerformed

    private void measureTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_measureTFActionPerformed
    {//GEN-HEADEREND:event_measureTFActionPerformed
      setSectionPrefs();
      sectionListModel.refresh();
    }//GEN-LAST:event_measureTFActionPerformed

    private void adviceScrollListCellsMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_adviceScrollListCellsMouseClicked
    {//GEN-HEADEREND:event_adviceScrollListCellsMouseClicked
      adviceSelected(adviceScrollListCells.getSelectedValue());
}//GEN-LAST:event_adviceScrollListCellsMouseClicked

    private void adviceScrollListCellsKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListCellsKeyPressed
    {//GEN-HEADEREND:event_adviceScrollListCellsKeyPressed
}//GEN-LAST:event_adviceScrollListCellsKeyPressed

    private void adviceScrollListCellsKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListCellsKeyReleased
    {//GEN-HEADEREND:event_adviceScrollListCellsKeyReleased
      switch( evt.getKeyCode() )
        {
        case java.awt.event.KeyEvent.VK_UP:
        case java.awt.event.KeyEvent.VK_DOWN:
          adviceSelected(adviceScrollListCells.getSelectedValue());
          break;

        default:
          // Delegate to main window
          adviceKeyPressed(evt);
          break;
        }             
}//GEN-LAST:event_adviceScrollListCellsKeyReleased

    private void adviceScrollListIdiomsKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListIdiomsKeyPressed
    {//GEN-HEADEREND:event_adviceScrollListIdiomsKeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_adviceScrollListIdiomsKeyPressed

    private void adviceScrollListIdiomsKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListIdiomsKeyReleased
    {//GEN-HEADEREND:event_adviceScrollListIdiomsKeyReleased
      switch( evt.getKeyCode() )
        {
        case java.awt.event.KeyEvent.VK_UP:
        case java.awt.event.KeyEvent.VK_DOWN:
          adviceSelected(adviceScrollListIdioms.getSelectedValue());
          break;

        default:
          // Delegate to main window
          adviceKeyPressed(evt);
          break;
        }
}//GEN-LAST:event_adviceScrollListIdiomsKeyReleased

    private void adviceScrollListIdiomsMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_adviceScrollListIdiomsMouseClicked
    {//GEN-HEADEREND:event_adviceScrollListIdiomsMouseClicked
      adviceSelected(adviceScrollListIdioms.getSelectedValue());
}//GEN-LAST:event_adviceScrollListIdiomsMouseClicked

    private void adviceScrollListLicksKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListLicksKeyPressed
    {//GEN-HEADEREND:event_adviceScrollListLicksKeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_adviceScrollListLicksKeyPressed

    private void adviceScrollListLicksKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListLicksKeyReleased
    {//GEN-HEADEREND:event_adviceScrollListLicksKeyReleased
      switch( evt.getKeyCode() )
        {
        case java.awt.event.KeyEvent.VK_UP:
        case java.awt.event.KeyEvent.VK_DOWN:
          adviceSelected(adviceScrollListLicks.getSelectedValue());
          break;

        default:
          // Delegate to main window
          adviceKeyPressed(evt);
          break;
        }
}//GEN-LAST:event_adviceScrollListLicksKeyReleased

    private void adviceScrollListLicksMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_adviceScrollListLicksMouseClicked
    {//GEN-HEADEREND:event_adviceScrollListLicksMouseClicked
      adviceSelected(adviceScrollListLicks.getSelectedValue());
}//GEN-LAST:event_adviceScrollListLicksMouseClicked

    private void adviceScrollListQuotesKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListQuotesKeyPressed
    {//GEN-HEADEREND:event_adviceScrollListQuotesKeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_adviceScrollListQuotesKeyPressed

    private void adviceScrollListQuotesKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListQuotesKeyReleased
    {//GEN-HEADEREND:event_adviceScrollListQuotesKeyReleased
      switch( evt.getKeyCode() )
        {
        case java.awt.event.KeyEvent.VK_UP:
        case java.awt.event.KeyEvent.VK_DOWN:
          adviceSelected(adviceScrollListQuotes.getSelectedValue());
          break;

        default:
          // Delegate to main window
          adviceKeyPressed(evt);
          break;
        }
}//GEN-LAST:event_adviceScrollListQuotesKeyReleased

    private void adviceScrollListQuotesMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_adviceScrollListQuotesMouseClicked
    {//GEN-HEADEREND:event_adviceScrollListQuotesMouseClicked
      adviceSelected(adviceScrollListQuotes.getSelectedValue());
}//GEN-LAST:event_adviceScrollListQuotesMouseClicked

    private void adviceScrollListScalesKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListScalesKeyPressed
    {//GEN-HEADEREND:event_adviceScrollListScalesKeyPressed
      // TODO add your handling code here:
}//GEN-LAST:event_adviceScrollListScalesKeyPressed

    private void adviceScrollListScalesKeyReleased(java.awt.event.KeyEvent evt)//GEN-FIRST:event_adviceScrollListScalesKeyReleased
    {//GEN-HEADEREND:event_adviceScrollListScalesKeyReleased
      switch( evt.getKeyCode() )
        {
        case java.awt.event.KeyEvent.VK_UP:
        case java.awt.event.KeyEvent.VK_DOWN:
          adviceSelected(adviceScrollListScales.getSelectedValue());
          break;

        default:
          // Delegate to main window
          adviceKeyPressed(evt);
          break;
        }
}//GEN-LAST:event_adviceScrollListScalesKeyReleased

    private void adviceScrollListScalesMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_adviceScrollListScalesMouseClicked
    {//GEN-HEADEREND:event_adviceScrollListScalesMouseClicked
      adviceSelected(adviceScrollListScales.getSelectedValue());
}//GEN-LAST:event_adviceScrollListScalesMouseClicked

    private void generateLickInSelectionActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_generateLickInSelectionActionPerformed
    {//GEN-HEADEREND:event_generateLickInSelectionActionPerformed
     generate(lickgen);
}//GEN-LAST:event_generateLickInSelectionActionPerformed

    private void resolvePitchesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resolvePitchesActionPerformed
    {//GEN-HEADEREND:event_resolvePitchesActionPerformed
    rectifySelection();
}//GEN-LAST:event_resolvePitchesActionPerformed

public void rectifySelection()
  {
    Stave stave = getCurrentStave();
    rectifySelection(stave, getCurrentSelectionStart(), getCurrentSelectionEnd());
    stave.playSelection(false, getLoopCount(), PlayScoreCommand.USEDRUMS);
  }

    private void beamButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_beamButtonActionPerformed
    {//GEN-HEADEREND:event_beamButtonActionPerformed
      if( beamButton.isSelected() )
        {
        beamButton.setBackground(new java.awt.Color(51, 255, 255));
        beamButton.setText("No Beam");
        beamButton.setSelected(true);
        }
      else
        {
        beamButton.setBackground(Color.red);
        beamButton.setText("Beam");
        beamButton.setSelected(false);
        }
      getCurrentStave().setBeaming(beamButton.isSelected());   
}//GEN-LAST:event_beamButtonActionPerformed

    private void defaultTempoTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_defaultTempoTFActionPerformed
    {//GEN-HEADEREND:event_defaultTempoTFActionPerformed
      double tempo = doubleFromTextField(defaultTempoTF, MIN_TEMPO, MAX_TEMPO, getDefaultTempo());
      Preferences.setPreference(Preferences.DEFAULT_TEMPO, "" + tempo);
      preferencesDialog.setVisible(false);
    }//GEN-LAST:event_defaultTempoTFActionPerformed

    
    private void defVocabFileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_defVocabFileActionPerformed
    {//GEN-HEADEREND:event_defVocabFileActionPerformed
      // TODO add your handling code here:
    }//GEN-LAST:event_defVocabFileActionPerformed

    private void tempoTFActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tempoTFActionPerformed
    {//GEN-HEADEREND:event_tempoTFActionPerformed
     setTempo(doubleFromTextField(tempoTF, MIN_TEMPO, MAX_TEMPO, getDefaultTempo()));
     preferencesDialog.setVisible(false);
    }//GEN-LAST:event_tempoTFActionPerformed

    private void preferencesAcceleratorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_preferencesAcceleratorMIActionPerformed
    {//GEN-HEADEREND:event_preferencesAcceleratorMIActionPerformed
     showPreferencesDialog();
}//GEN-LAST:event_preferencesAcceleratorMIActionPerformed

private void alwaysUseStaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alwaysUseStaveActionPerformed
      setCheckBoxPreferences(3, alwaysUseStave);
      setStavePreferenceFromButtons();

}//GEN-LAST:event_alwaysUseStaveActionPerformed

private void newVoicingCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVoicingCancelButtonActionPerformed
newVoicingDialog.setVisible(false);
}//GEN-LAST:event_newVoicingCancelButtonActionPerformed

private void newVoicingSaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVoicingSaveButtonActionPerformed

    // To save a new voicing to the vocabulary
    
    String name = newVoicingNameTF.getText();
    String chord = newVoicingChordTF.getText();
    String voice = voicingEntryTF.getText();
    String ext = extEntryTF.getText();
    String type = (String)newVoicingTypeCB.getSelectedItem();
    
    if (name.equals("") || chord.equals("")){
        //ErrorLog.log(ErrorLog.WARNING, "Empty field.");
        return;
          
    }
    // making the voicing and extensions Polylists and checking possible errors
    StringReader voicingReader = new StringReader(voice);
    Tokenizer in = new Tokenizer(voicingReader);
    Object o = in.nextSexp();
    Polylist v = (Polylist)o;
    if( o instanceof Polylist )
    {
        if( v.length() == 0 )
        {
            //ErrorLog.log(ErrorLog.WARNING, "Empty voicing entered.");
            return;
        }
    }

    Polylist invalid = Key.invalidNotes(v);
    if( invalid.nonEmpty() )
    {
        //ErrorLog.log(ErrorLog.WARNING, "Invalid notes in voicing: " + invalid);
        return;
    }
    Polylist extension;
    if( ext.equals("") )
    {
        extension = Polylist.nil;
    }
    else
    {
        StringReader extReader = new StringReader(ext);
        in = new Tokenizer(extReader);
        o = in.nextSexp();
        if( o instanceof Polylist )
        {
            extension = (Polylist)o;
            invalid = Key.invalidNotes(extension);
            if( invalid.nonEmpty() )
            {
                //ErrorLog.log(ErrorLog.WARNING, "Invalid notes in extension: " + invalid);
                return;
            }
            extension = NoteSymbol.makeNoteSymbolList(extension);
        }
        else
        {
            ErrorLog.log(ErrorLog.WARNING, "Malformed extension: " + ext);
            return;
        }
    }
    
    // transposing to a root of c
        
    String fromRoot = chordRootTF.getText();
    
    String toRoot = "c";
        
    int rise = PitchClass.findRise(fromRoot,toRoot);
        
    v = NoteSymbol.makeNoteSymbolList(v,rise);
    
    extension = NoteSymbol.makeNoteSymbolList(extension,rise);
   
    
    adv.addVoicing(chord,name,type,v,extension);
    
    saveAdviceActionPerformed(null);
          
    newVoicingDialog.setVisible(false);
    
    newVoicingNameTF.setText("");
    
    newVoicingChordTF.setText("");
    
    newVoicingTypeCB.setSelectedItem("open");
    
    
    // to reset the table
    
    String root = chordRootTF.getText();
        
    String bass = bassNoteTF.getText();
        
    String low = lowRangeTF.getText();
        
    String high = highRangeTF.getText();
        
        
        
    PitchClass rootClass = PitchClass.getPitchClass(root);
        
    if(root.equals("")) {
        
        ErrorLog.log(ErrorLog.WARNING, "No chord root entered.");
            
        return;
            
    }
        
    else if(rootClass == null) {
            
    ErrorLog.log(ErrorLog.WARNING, "Invalid chord root: " + root);
            
        return;
            
    }
        
    PitchClass bassClass = PitchClass.getPitchClass(bass);
        
    if(!bass.equals("") && bassClass == null) {
            
        ErrorLog.log(ErrorLog.WARNING, "Invalid bass note: " + bass);
            
        return;
            
    }
        
    NoteSymbol lowNote = NoteSymbol.makeNoteSymbol(low);
        
    if(low.equals("")) {
            
        ErrorLog.log(ErrorLog.WARNING, "No lower range entered.");
            
        return;
            
    }
        
    else if(lowNote == null) {
            
        ErrorLog.log(ErrorLog.WARNING, "Invalid lower range: " + low);
            
        return;
            
    }
        
    NoteSymbol highNote = NoteSymbol.makeNoteSymbol(high);
        
    if(high.equals("")) {
            
       ErrorLog.log(ErrorLog.WARNING, "No higher range entered.");
            
        return;
            
    }
        
    else if(highNote == null) {
            
        ErrorLog.log(ErrorLog.WARNING, "Invalid higher range: " + high);
            
        return;
            
    }
       
    voicingTableModel.setChordRoot(root,bass,lowNote,highNote);
    
        
}//GEN-LAST:event_newVoicingSaveButtonActionPerformed

private void newVoicingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVoicingButtonActionPerformed

    String v = voicingEntryTF.getText();
    
    if( v.equals("") )
        {
            return;
        }
    
    showNewVoicingDialog();
    
}//GEN-LAST:event_newVoicingButtonActionPerformed

private void newVoicingTypeCBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVoicingTypeCBActionPerformed

    String type = (String)newVoicingTypeCB.getSelectedItem();
}//GEN-LAST:event_newVoicingTypeCBActionPerformed

private void voicingDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingDeleteButtonActionPerformed

    String v = voicingEntryTF.getText();
    
    if (v.equals(""))
    {
        return;
    }
    
    showDeleteVoicingDialog();
    
}//GEN-LAST:event_voicingDeleteButtonActionPerformed

private void deleteVoicingOKButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVoicingOKButtonActionPerformed

    int rowIndex = voicingTable.getSelectedRow();
    if( rowIndex < 0 )
    {
      return; // nothing selected
    }

    String chordName = voicingTable.getValueAt(rowIndex, VoicingTableChordColumn).toString();

    // Find out the index of this voicing for the chord, counting from 0 for the first.

    int position = 0;

    for( int index = 0; index < rowIndex; index++ )
    {
      String name = voicingTable.getValueAt(index, VoicingTableChordColumn).toString();
      if( chordName.equals(name) )
      {
        position++;
      }
    }

    if( position == 0 )
    {
      return; // can't remove generated voicing; not in the form
    }

    getAdvisor().removeNthVoicing(chordName, position-1);
    saveAdviceActionPerformed(null);
    buildVoicingTable();
    deleteVoicingDialog.setVisible(false);
    
}//GEN-LAST:event_deleteVoicingOKButtonActionPerformed

private void deleteVoicingCancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteVoicingCancelButtonActionPerformed

    deleteVoicingDialog.setVisible(false);
    
}//GEN-LAST:event_deleteVoicingCancelButtonActionPerformed

public void pauseToKeyboard()
{
    pauseScore();
        
    String v = keyboard.voicingFromKeyboard();
    String currentChord = keyboard.getPresentChordDisplayText();

    if (voicingTestFrame != null && voicingTestFrame.isVisible())
    {
        selectVoicing(v, currentChord);
    }
}

/**
 * Gets the text from the voicing entry text field.
 * @return a string
 */
public String voicingEntryTFText()
{
    return voicingEntryTF.getText();
}

public String extEntryTFText()
{
    return extEntryTF.getText();
}

public void setExtEntryTFText(String text)
{
    extEntryTF.setText(text);
}

/**
 * Adds text to the voicing entry text field
 * @param text
 */
public void addVoicingEntryTFText(String text)
{
    String s = voicingEntryTF.getText();
    if (s.equals(""))
    {
        voicingEntryTF.setText("(" + text + ")");
    }
    else {
        voicingEntryTF.setText(s.replace(')', ' ') + text + ")");
    }
}

public void addExtEntryTFText(String text)
{
    String s = extEntryTF.getText();
    if (s.equals(""))
    {
        extEntryTF.setText("(" + text + ")");
    }
    else
    {
        extEntryTF.setText(s.replace(')', ' ') + text + ")");
    }
}

/**
 * clears any text in the voicing and extension entry text fields.
 */
public void clearVoicingEntryTF()
{
  voicingEntryTF.setText("");
  extEntryTF.setText("");
  
}

JPopupMenu chordSelectionMenu = null;

public void populateChordSelMenu()
{
    chordSelectionMenu = new JPopupMenu();

    String root = chordRootTF.getText();

    int semitones = PitchClass.findRise(root);

    JMenuItem chordMI = new JMenuItem("Possible chords on root " + root);
    
    chordSelectionMenu.add(chordMI);
    
    HashMap map = new HashMap<String, JPopupMenu>();

    Polylist chords = Advisor.getAllChords();
    //System.out.println("Chords: " + chords);
    
    for (Polylist L = chords; L.nonEmpty(); L = L.rest())
      {
        Polylist piece = (Polylist)L.first();
        ChordForm form = (ChordForm)piece.second();
        
        String cName = form.getName();
        String family = form.getFamily();
        
        // Get the subMenu for this family, or create one if non-existent
        // If a new menu is created, add it as a sub-menu.
        
        JMenu subMenu = (JMenu)map.get(family);
        if( subMenu == null )
        {
          subMenu = new JMenu(family);
          chordSelectionMenu.add(subMenu);
          map.put(family, subMenu);
        }
        
        //System.out.println("form = " + form + ", family = " + family + ", subMenu = " + subMenu);
       
        ChordSymbol cs = ChordSymbol.makeChordSymbol(cName);
        cs = cs.transpose(semitones);
        String chord = cs.toString();

        JMenuItem leaf = new JMenuItem(chord);
        subMenu.add(leaf);
        leaf.addActionListener(new java.awt.event.ActionListener()
          {
          public void actionPerformed(java.awt.event.ActionEvent evt) {
            chordToInsert = evt.getActionCommand();
               //System.out.println("You selected " + chordToInsert);
            
            // Insert the voicing into the leadsheet   
            Style currentStyle =
                ImproVisor.getCurrentWindow().score.getChordProg().getStyle();
            String v = voicingEntryTF.getText();
            String e = extEntryTF.getText();

            StringReader voicingReader = new StringReader(v);
            Tokenizer in = new Tokenizer(voicingReader);
            Object o = in.nextSexp();
            int index = ImproVisor.getCurrentWindow().getCurrentSelectionStart();

            if( o instanceof Polylist && index != 1 )
            {
                      Polylist voicing = (Polylist)o;
                      voicing = NoteSymbol.makeNoteSymbolList(voicing);
                      Polylist extension = new Polylist();
                      if( e.equals("") ) { extension = Polylist.nil; }
                      
                      else {
                          StringReader extReader = new StringReader(e);
                          in = new Tokenizer(extReader);
                          o = in.nextSexp();
                          if( o instanceof Polylist ) {
                          extension = (Polylist)o;
                                  extension = NoteSymbol.makeNoteSymbolList(extension);
                          }
                      }

                    ChordSymbol c = ChordSymbol.makeChordSymbol(chordToInsert);
                    c.setVoicing(voicing);
                    c.setExtension(extension);

                    if( !ChordPattern.goodVoicing(c, currentStyle) )
                    {
                        ErrorLog.log(ErrorLog.WARNING,
                        "Voicing does not fit within range of leadsheet: " + voicing);
                        return;
                    }

                    playVoicing(c);
                    insertVoicing(c, index);
            }
            else if( o instanceof Polylist )
            {
                ErrorLog.log(ErrorLog.WARNING, "No slot selected for insertion.");
                return;
            }
            else
            {
                ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
                return;
            }   
          }
        });
      }
    chordSelectionMenu.add("cancel insert");
}

/**
 * Removes duplicats from a Polylist (for making future chords list in keyboard
 * playback)
 * 
 * @param L
 * @return a Polylist with no duplicates
 */
static Polylist removeDuplicates(Polylist L)
{
    Object last = "";

    PolylistBuffer buffer = new PolylistBuffer();

    for( ; L.nonEmpty(); L = L.rest() )
       {
       Object next = L.first();
       if( !next.equals(last) )
           {
           buffer.append(next);
           last = (String)next;
           }
       }

    return buffer.toPolylist();
}

public Polylist makeFutureChordsList()
  {
  return makeFutureChordsList(0);
  }

public Polylist makeFutureChordsList(int startingSlot)
  {
    int chorusLength = chordProg.size();
    Polylist result = getCurrentStave().extractChordNamePolylist(startingSlot % chorusLength, chorusLength-1);
    result = removeDuplicates(result);
    return result;
        
  }

public void setKeyboardPlayback(boolean on)
{
    if( keyboard != null )
    {
        keyboard.setPlayback(on);
    }
}

/**
 * Used in actionPerformed to update the keyboard during playback.
 * @param tab, current chorus tab
 * @param slotInPlayback
 */
public void keyboardPlayback(Chord currentChord, int tab, int slotInPlayback, int slot, int totalSlots)
{
    String currentChordName = currentChord == null ? "NC" : currentChord.getName();
    Polylist v;
    
    // Code for keyboard playback.
    if (currentChord != null && (v = currentChord.getVoicing()) != null)
    {        
        String EMPTY = "";

        String v1 = v.toString();
        v1 = v1.replaceAll("8", EMPTY);
        String v2 = keyboard.voicingFromKeyboard();

        voicingEntryTF.setText(v1);

        presentChord = keyboard.getPresentChordDisplayText();

        //Determining the bass note and chord root.
        String bass = keyboard.nameToBass(currentChordName);
        String root = bass;
        rootEqualBassCheckbox.setSelected(true);
        if (currentChordName.contains("/"))
        {
            int bassInd = currentChordName.indexOf("/") + 1;
            bass = currentChordName.substring(bassInd,currentChordName.length());
            bass = keyboard.nameToBass(bass);
            rootEqualBassCheckbox.setSelected(false);
        }
        
        // If the playback is just beginning and the keyboard is blank
        if (v2.equals(EMPTY) )
        {
            keyboard.showVoicingOnKeyboard(v1);

            setBassAndRootTFs(bass,root);

            futureChords = makeFutureChordsList(slotInPlayback);
            if (futureChords.nonEmpty() && currentChordName.equals(futureChords.first()))
            {
                futureChords = futureChords.rest();
            }
            
            String future = futureChords.toStringSansParens();
            future = future.replaceAll(" ", "   ");

            setPresentChordDisplay(currentChordName);
            setFutureChordDisplay(future);
        }

        // If the current chord voicing and keyboard voicing are different
        // (if the chord has changed)
        else if (!keyboard.voicingsAreEqual(v1,v2) || !currentChordName.equals(presentChord))
        {
            keyboard.showVoicingOnKeyboard(v1);

            setBassAndRootTFs(bass,root);

            pastChords = Polylist.list(keyboard.getPresentChordDisplayText());

            if (futureChords.nonEmpty() && !currentChordName.equals(presentChord))
            {
                futureChords = makeFutureChordsList(slotInPlayback);
                if (futureChords.nonEmpty() && 
                        currentChordName.equals(futureChords.first()))
                {
                    futureChords = futureChords.rest();
                }
            }
            
            String future = futureChords.toStringSansParens();
            future = future.replaceAll(" ", "   ");
            String past = pastChords.toStringSansParens();
            past = past.replaceAll(" ", "   ");

            setPresentChordDisplay(currentChordName);
            setFutureChordDisplay(future);
            setPastChordDisplay(past);
        }

        // If changing between chorus tabs
        if (tab != currentPlaybackTab)
        {
            keyboard.showVoicingOnKeyboard(v1);

            setBassAndRootTFs(bass,root);

            currentChordName = keyboard.getPresentChordDisplayText();

            futureChords = makeFutureChordsList();

            futureChords = futureChords.rest();
            
            String future = futureChords.toStringSansParens();
            future = future.replaceAll(" ", "   ");

            setPresentChordDisplay(currentChordName);
            setFutureChordDisplay(future);
        }
        
        // the current slot number never seems to EXACTLY equal the total
        // slot number, but is usually (experimentally determined) within 20
        if (totalSlots - slot < 20 )
        {
            keyboard.setPlayback(false);
            buildVoicingTable();
        }
        
    }
    // End code for keyboard playback.
}

/**
 * Finds the row number of the first occurrance of currentChord in the voicing
 * table.
 * 
 * @param currentChord
 * @return
 */
public int findChordinTable(String currentChord)
{
    int selectedRow = -1;
    for (int i=0; i<voicingTable.getRowCount(); i++)
    {
        Object o = voicingTableModel.getValueAt(i, VoicingTableChordColumn);
        String chord = o.toString();
        
        Object p = voicingTableModel.getValueAt(i, VoicingTableVoicingColumn);
        String v = p.toString();
        
        if (chord.equals(currentChord))
        {
            selectedRow = i;
            break;
        }

    }
    
    return selectedRow;
}

/**
 * Finds the row number of a certain voicing in the voicing table.
 * 
 * @param chordRow
 * @param voicing
 * @param currentChord
 * @return
 */
public int findVoicinginTable(int chordRow, String voicing, String currentChord)
{
    int selectedRow = chordRow;
    
    for (int i=chordRow; i<voicingTable.getRowCount(); i++)
    {
        Object o = voicingTableModel.getValueAt(i, VoicingTableChordColumn);
        String chord = o.toString();
        
        Object p = voicingTableModel.getValueAt(i, VoicingTableVoicingColumn);
        String cell = p.toString();
        
        if (chord.equals(currentChord))
        {
            Polylist v1 = voicingToList(voicing);
            Polylist v2 = voicingToList(cell);
            
            PitchClass v1Pitch = PitchClass.getPitchClass(v1.first().toString());
            PitchClass v2Pitch = PitchClass.getPitchClass(v2.first().toString());

            String pitch1 = v1Pitch.toString();
            pitch1 = keyboard.nameToBass(pitch1);
            String pitch2 = v2Pitch.toString();
            pitch2 = keyboard.nameToBass(pitch2);
            
            String uCell = cell;
            String dCell = cell;
            
            if (keyboard.voicingsAreEqual(voicing,cell))
            {
                selectedRow = i;
                return selectedRow;
            }
            else
            {
                while (!keyboard.voicingsAreEqual(voicing,uCell) &&
                        !keyboard.voicingsAreEqual(voicing,dCell))
                {
                    String u = uCell;
                    String d = dCell;
                    uCell = keyboard.transposeVoicing(uCell,"up");
                    dCell = keyboard.transposeVoicing(dCell,"down");
                     
                    if (d.equals(dCell) || u.equals(uCell))
                    {
                        break;
                    }
                }
                if (keyboard.voicingsAreEqual(voicing, dCell) || 
                        keyboard.voicingsAreEqual(voicing, uCell))
                {
                    selectedRow = i;
                    return selectedRow;
                }
            }
        }
    }
    ErrorLog.log(ErrorLog.WARNING, "Voicing not found in table!");
    selectedRow = chordRow;
    return selectedRow;
    
}


/**
 * Selects a given row number in the voicing table.
 * 
 * @param v
 * @param currentChord
 */
public void selectVoicing(String v, String currentChord)
{
    buildVoicingTable();
            
    String bassNote = keyboard.getPresentChordDisplayText();

    int selectedRow = findChordinTable(bassNote);
    
    Object o = voicingTableModel.getValueAt(selectedRow, VoicingTableVoicingColumn);
    String n = o.toString();
    
    if (n.equals(""))
    {
        Object p = voicingTableModel.getValueAt(selectedRow, VoicingTableNameColumn);
        String s = p.toString();
        int space = s.indexOf(" ") + 1;
        s = s.substring(space, s.length() - 1);
        currentChord = s;

        for (int i=0; i<voicingTable.getRowCount(); i++)
        {
            Object x = voicingTableModel.getValueAt(i, VoicingTableChordColumn);
            String y = x.toString();
            if (currentChord.equals(y))
            {
                selectedRow = i;
            }
        }
    }
        
    if (selectedRow == -1)
    {
        ErrorLog.log(ErrorLog.WARNING, "Chord not found in table!");
        return;
    }
    
    selectedRow = findVoicinginTable(selectedRow, v, currentChord);

    voicingTable.setColumnSelectionAllowed(false);
    voicingTable.setRowSelectionAllowed(true);
    voicingTable.setRowSelectionInterval(selectedRow, selectedRow);
    voicingTable.changeSelection(selectedRow,selectedRow,true,true);
}


public void setBassAndRootTFs(String bass, String root)
{
    bassNoteTF.setText(bass);
    chordRootTF.setText(root);
    int midi = keyboard.findBass();
    String note = keyboard.findBassName(midi);
    if (!note.equals(bass))
    {
        bassNoteTF.setText(bass);
    }
    keyboard.setBass(bass, midi);
}

/**
 * Determines what to do when a user clicks the keyboard.
 * 
 * @param evt
 */    
private void pianoKeyboardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pianoKeyboardButtonActionPerformed
//GEN-LAST:event_pianoKeyboardButtonActionPerformed
    
    String v = voicingEntryTF.getText();
    if (getPlaying() == MidiPlayListener.Status.STOPPED)
    {
        openKeyboard();
        keyboard.showBass();
    }
    
    if (!v.equals(""))
    {
        keyboard.showVoicingOnKeyboard(v);
    }
}

/**
 * opens up the piano keyboard
 */
public void openKeyboard()
{
    if( keyboard == null ) 
    {
        keyboard = new VoicingKeyboard(this);
    }
    String current = keyboard.getPresentChordDisplayText();
    if (current.equals(""))
    {
        clearKeyboard();
        clearVoicingEntryTF();
    }
    
    keyboard.setVisible(true);
    
}

/**
 * Sets the keyboard to its original state
 */
public void clearKeyboard() 
{
  
  if( keyboard != null )
    {
    keyboard.clearKeyboard();
    }
}

/**
 * Turns the current voicing into a polylist.
 * 
 * @return polylist representing the current voicing and extension
 */
public Polylist voicingToList(String v)
{
    if (v.equals(""))
    {
        return Polylist.nil;
    }
    
    StringReader voicingReader = new StringReader(v);
    Tokenizer in = new Tokenizer(voicingReader);
    Object o = in.nextSexp();

    Polylist selVoicing = o instanceof Polylist ? (Polylist)o : Polylist.list(o);
    selVoicing = NoteSymbol.makeNoteSymbolList(selVoicing);

    Polylist L = selVoicing;
    
    return L;
    
}

/**
 * Turns the current extension into a polylist.
 * 
 * @return polylist representing the current extension
 */
public Polylist extensionToList(String e)
{
    Polylist extension = Polylist.nil;

    if (!e.equals("")) 
    {
        StringReader extReader = new StringReader(e);
        Tokenizer in = new Tokenizer(extReader);
        Object o = in.nextSexp();

        extension = (Polylist)o;
    }

    extension = NoteSymbol.makeNoteSymbolList(extension);

    Polylist L = extension;
    
    return L;
}



public void addVoicingToSeq(String v)
{
    ListSelectionModel rowSM = voicingTable.getSelectionModel();

    if (!rowSM.isSelectionEmpty()){
    
        int selectedRow = rowSM.getMinSelectionIndex();
        Object o = voicingTableModel.getValueAt(selectedRow, VoicingTableVoicingColumn);
        Object p = voicingTableModel.getValueAt(selectedRow,VoicingTableExtensionColumn);
        
        if (v.equals("")){
            return;
        }
        
        String e = p.toString();
        String c = chordRootTF.getText();
        
        v = v.substring(1,v.length());
        v = "(" + c + " " + v;

        int index = voicingSequenceList.getSelectedIndex();

        index++;
        
        String s = v + e;

        voicingSequenceListModel.add(index, s);
        voicingSequenceList.setSelectedIndex(index);
    }
    
    else {
        String e = extEntryTF.getText();
        String c = "{" + chordRootTF.getText() + "}";
        String s = c + v + e;
        int index = voicingSequenceListModel.size();
        voicingSequenceListModel.add(index, s);
        voicingSequenceList.setSelectedIndex(index);
    }
}

/**
 * Adds a voicing to the voicing sequence list.
 */
public void addToVoicingSequence()
{
    String v = voicingEntryTF.getText();
    addVoicingToSeq(v);
}

private void voicingSequenceAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingSequenceAddButtonActionPerformed

    addToVoicingSequence();
    
}//GEN-LAST:event_voicingSequenceAddButtonActionPerformed

/**
 * Removes the selected voicing from the voicing sequence list.
 * 
 * @param evt
 */
private void voicingSequenceRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingSequenceRemoveButtonActionPerformed

    int index = voicingSequenceList.getSelectedIndex();
    if (index == -1)
    {
        return;
    }
    
    else if (index == 0)
    {
        voicingSequenceList.clearSelection();
        voicingSequenceListModel.remove(index);
    }
    
    else
    {
        int newindex = index - 1;
        voicingSequenceList.setSelectedIndex(newindex);
        voicingSequenceListModel.remove(index);
    }
    
    
}//GEN-LAST:event_voicingSequenceRemoveButtonActionPerformed

/**
 * Swaps the selected voicing with the one above it in the voicing sequence.
 * 
 * @param evt
 */
private void voicingSequenceUpArrowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_voicingSequenceUpArrowMouseClicked

    int index = voicingSequenceList.getSelectedIndex();//GEN-LAST:event_voicingSequenceUpArrowMouseClicked
    
    if (index == -1 || index == 0)
    {
        return;
    }
    
    int newindex = index - 1;
    
    Object i = voicingSequenceListModel.getElementAt(index);
    Object j = voicingSequenceListModel.getElementAt(newindex);
    
    voicingSequenceListModel.remove(index);
    voicingSequenceListModel.remove(newindex);
    
    voicingSequenceListModel.add(newindex, i);
    voicingSequenceListModel.add(index, j);
    
    voicingSequenceList.setSelectedIndex(newindex);

}

/**
 * Swaps the selected voicing with the one below it in the voicing sequence.
 * 
 * @param evt
 */
private void voicingSequenceDownArrowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_voicingSequenceDownArrowMouseClicked

    int index = voicingSequenceList.getSelectedIndex();
    if (index == voicingSequenceListModel.size() - 1)
    {
        return;
    }
    
    int newindex = index + 1;
    
    Object i = voicingSequenceListModel.getElementAt(index);
    Object j = voicingSequenceListModel.getElementAt(newindex);
    
    voicingSequenceListModel.remove(newindex);
    voicingSequenceListModel.remove(index);
    
    voicingSequenceListModel.add(index, j);
    voicingSequenceListModel.add(newindex, i);
    
    voicingSequenceList.setSelectedIndex(newindex);
    
}//GEN-LAST:event_voicingSequenceDownArrowMouseClicked

/**
 * Finds the chord root given in the Chord Root Text Field.
 * 
 * @return a string, the chord root
 */
public String getChordRootTFText()
{
    return chordRootTF.getText();
}

public void setChordRootTFText(String root)
{
    chordRootTF.setText(root);
}

/**
 * Uses the range text fields on the voicing window to find the MIDI value
 * of the lowest note in the range.
 * 
 * @return an integer, MIDI value
 */
public int getLowerBound()
{
    String s = lowRangeTF2.getText();
    NoteSymbol c = NoteSymbol.makeNoteSymbol(s);
    int l = c.getMIDI();
    return l;
}

/**
 * Uses the range text fields on the voicing window to find the MIDI value
 * of the highest note in the range.
 * 
 * @return an integer, MIDI value
 */
public int getUpperBound()
{
    String s = highRangeTF2.getText();
    NoteSymbol c = NoteSymbol.makeNoteSymbol(s);
    int l = c.getMIDI();
    return l;
}

/**
 * Plays the current sequence of voicings and displays them on the keyboard.
 * 
 * @param evt
 */
private void voicingSequencePlayButtonActionPerformed(java.awt.event.ActionEvent evt) {                                                          

    for (int i=0; i<voicingSequenceListModel.size(); i++)
    {
        voicingSequenceList.setSelectedIndex(i);

        String s = getChordRootTFText();
        Object o = voicingSequenceListModel.getElementAt(i);
        String v = o.toString();
        
        int j = v.indexOf(")(") + 1;
        
        if (j == 0)
        {
            v = v.substring(3);
            v = "(" + v;
        }
        
        else
        {
            v = v.substring(3,j);
            v = "(" + v;
        }

        voicingEntryTF.setText(v);
        keyboard.showVoicingOnKeyboard(v);

        try 
        { 
            constructAndPlayChord(s,v);
            Thread.sleep(1000);
        }
        catch(InterruptedException e)   
        {      
            System.out.println("Sleep interrupted:" + e);      
        }
    }
}

/**
 * Displays the current voicing if the selection index in the sequence list changes.
 */
private void displayVoicingfromList()
{
    int index = voicingSequenceList.getSelectedIndex();
        
    if (index == -1)
    {
        clearVoicingEntryTF();
        clearKeyboard();
        return;
    }

    Object o = voicingSequenceListModel.getElementAt(index);
    String v = o.toString();

    int i = v.indexOf(")(") + 1;
    int j = v.length();

    String e = v.substring(i,j);
    String c = v.substring(1,3);

    if (i == 0)
    {
        e = "";
        v = v.substring(3);
        v = "(" + v;
    }

    else
    {
        v = v.substring(3,i);
        v = "(" + v;
    }

    voicingEntryTF.setText(v);
    extEntryTF.setText(e);
    chordRootTF.setText(c);
    buildVoicingTable();

    int r = keyboard.findBass();
    String note = keyboard.findBassName(r);
    keyboard.setBass(note, r);
    keyboard.showVoicingOnKeyboard(v);
}

/**
 * If the selection index of the voicing sequence list changes, the new voicing
 * is displayed in the voicing entry text field and on the keyboard.
 * 
 * @param evt
 */
private void voicingSequenceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_voicingSequenceListValueChanged

    displayVoicingfromList();
    
}//GEN-LAST:event_voicingSequenceListValueChanged

public void setFutureChordDisplay(String chords)
{
    keyboard.setFutureChordDisplayText(chords);
}

public void setPresentChordDisplay(String chords)
{
    keyboard.setPresentChordDisplayText(chords);
}

public void setPastChordDisplay(String chords)
{
    keyboard.setPastChordDisplayText(chords);
}

public Polylist futureChords;
public String presentChord;
public Polylist pastChords;

public String getChordList()
{
    selectAll();
    String saveSelection = getCurrentStave().getSaveSelection(lickTitle, Stave.ExtractMode.CHORDS);
    String result = saveSelection.trim();
    staveRequestFocus();
    getCurrentStave().unselectAll();
    return result;
}

public void resetChordDisplay()
{
  if( keyboard != null )
  {
    keyboard.resetChordDisplay();
  }
}

public void rebuildVoicingTable()
{
    buildVoicingTable();
}

public void voicingFrameKeyPressed(KeyEvent e)
{
    String v = voicingEntryTF.getText();
    String c = chordRootTF.getText();
    
    switch( e.getKeyCode())
    {
        case KeyEvent.VK_A:
            addToVoicingSequence();
            break;
        case KeyEvent.VK_C:
            keyboard.clearKeyboard();
            clearVoicingEntryTF();
            break;
        case KeyEvent.VK_P:
            constructAndPlayChord(c,v);
            break;
        
        case KeyEvent.VK_E:
            keyboard.transposeUpHalfStep(v);
            break;
        case KeyEvent.VK_D:
            keyboard.transposeDownHalfStep(v);
            break;
        case KeyEvent.VK_T:
            keyboard.transposeUpOctave(v);
            break;
        case KeyEvent.VK_G:
            keyboard.transposeDownOctave(v);
            break;
        
        case KeyEvent.VK_1:
            keyboard.setSingleNoteMode(true);
            break;
        case KeyEvent.VK_2:
            keyboard.setSingleNoteMode(false);
            break;
    }
}



    
private void chordRootTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordRootTFActionPerformed

    String chordRoot = chordRootTF.getText();
    PitchClass rootClass = PitchClass.getPitchClass(chordRoot);
    if(rootClass == null) 
    {
        ErrorLog.log(ErrorLog.WARNING, "Invalid chord root: " + chordRoot);
        return;
    }
    if (rootEqualBassCheckbox.isSelected())
    {
        bassNoteTF.setText(chordRoot);
    }
    int bass = keyboard.findBass();
    String note = keyboard.findBassName(bass);
    if (keyboard.enharmonic(note, chordRoot))
    {
        note = chordRoot;
    }
    keyboard.setBass(note,bass);
    buildVoicingTable();
    
}//GEN-LAST:event_chordRootTFActionPerformed

private void voicingEntryTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingEntryTFActionPerformed

    keyboard.showVoicingOnKeyboard(voicingEntryTF.getText());
    
}//GEN-LAST:event_voicingEntryTFActionPerformed

private void buildTableButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buildTableButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            buildTableButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }
    
}//GEN-LAST:event_buildTableButtonKeyPressed

private void pianoKeyboardButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_pianoKeyboardButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            pianoKeyboardButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }
    
}//GEN-LAST:event_pianoKeyboardButtonKeyPressed

private void playVoicingButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_playVoicingButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            playVoicingButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_playVoicingButtonKeyPressed

private void insertVoicingButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_insertVoicingButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            insertVoicingButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_insertVoicingButtonKeyPressed

private void newVoicingButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_newVoicingButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            newVoicingButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_newVoicingButtonKeyPressed

private void voicingDeleteButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingDeleteButtonKeyPressed

    switch(evt.getKeyCode())//GEN-LAST:event_voicingDeleteButtonKeyPressed
    {
        case KeyEvent.VK_ENTER:
            voicingDeleteButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }
    
}

private void voicingSequenceAddButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingSequenceAddButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            voicingSequenceAddButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_voicingSequenceAddButtonKeyPressed

private void voicingSequenceRemoveButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingSequenceRemoveButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            voicingSequenceRemoveButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_voicingSequenceRemoveButtonKeyPressed

private void voicingSequencePlayButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingSequencePlayButtonKeyPressed

    switch(evt.getKeyCode())
    {
        case KeyEvent.VK_ENTER:
            voicingSequencePlayButtonActionPerformed(null);
            break;
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_voicingSequencePlayButtonKeyPressed

private void voicingSequenceListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingSequenceListKeyPressed

    switch(evt.getKeyCode())
    {
        default:
            voicingFrameKeyPressed(evt);
            break;
    }

}//GEN-LAST:event_voicingSequenceListKeyPressed

private void voicingTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingTableKeyPressed

    switch(evt.getKeyCode())
    {
        default:
            voicingFrameKeyPressed(evt);
            break;
    }
    
}//GEN-LAST:event_voicingTableKeyPressed

private void bassNoteTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassNoteTFActionPerformed

    String bassNote = bassNoteTF.getText();
    String chordRoot = chordRootTF.getText();
    PitchClass bassClass = PitchClass.getPitchClass(bassNote);
        
    if(bassClass == null) 
    {
        ErrorLog.log(ErrorLog.WARNING, "Invalid bass note: " + bassNote);
        return;
    }
    if (bassNote.equals(""))
    {
        bassNoteTF.setText(chordRoot);
    }
    if (rootEqualBassCheckbox.isSelected())
    {
        chordRootTF.setText(bassNote);
    }
    int bass = keyboard.findBass();
    String note = keyboard.findBassName(bass);
    
    if (keyboard.enharmonic(note, bassNote))
    {
        note = bassNote;
    }
    
    keyboard.setBass(note, bass);
    buildVoicingTable();
    
}//GEN-LAST:event_bassNoteTFActionPerformed

private void rootEqualBassCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootEqualBassCheckboxActionPerformed

    if (rootEqualBassCheckbox.isSelected())
    {
        String bass = bassNoteTF.getText();
        chordRootTF.setText(bass);
        buildVoicingTable();
    }
    
}//GEN-LAST:event_rootEqualBassCheckboxActionPerformed

private void rootEqualBassCheckboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rootEqualBassCheckboxKeyPressed

    switch(evt.getKeyCode())
    {
        default:
            voicingFrameKeyPressed(evt);
            break;
    }   
    
}//GEN-LAST:event_rootEqualBassCheckboxKeyPressed

private void pianoKeyboardMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pianoKeyboardMIActionPerformed

    if (getPlaying() == MidiPlayListener.Status.STOPPED)
    {
        openKeyboard();                                               
    }
}//GEN-LAST:event_pianoKeyboardMIActionPerformed

// This is for safe-keeping.
// The revert file-menu item has been deleted.
public void revertGrammarToBackup()
{

    String outFile = getGrammarFileName();
    File f = new File(outFile);
    //Must continuously loop this, not quite sure why - garbage collecting seems to fix it, not sure why either
    if (f.exists()) {
        System.gc();
        boolean deleted = f.delete();
        while (!deleted) {
            deleted = f.delete();
            //System.out.println(deleted);
        }
    }
    String backupFile = f.getParentFile().getPath() + "/Backup.grammar";
    File f2 = new File(backupFile);
    try {
        FileWriter out = new FileWriter(f, true);
        FileReader in = new FileReader(f2);
        char[] buf = new char[(int) f2.length()];
        in.read(buf);
        String input = new String(buf);
        out.write(input);
        out.close();
        in.close();
    }
    catch (Exception e) {
        System.out.println(e.getMessage());
    }

    refreshGrammarEditor();

}

public void refreshGrammarEditor()
{
    grammarEditor.fillEditor();
    grammarEditor.performEditorToSourceButton(null);
}

public void generate(LickGen lickgen)
{
    setLickGenStatus("Generating melody ...");

    Stave stave = getCurrentStave();
    boolean nothingWasSelected = !stave.somethingSelected();
    boolean oneSlotWasSelected = stave.oneSlotSelected();
    int selectionStart = stave.getSelectionStart();

    if( nothingWasSelected )
      {
        setStatus("Nothing selected, generating entire chorus.");
        selectAll();
      }
    else if( oneSlotWasSelected )
      {
        setStatus("One slot selected, generating to end of chorus.");
        stave.setSelectionToEnd();
      }

    verifyTriageFields();

    Polylist rhythm = null;

    boolean useOutlines = lickgenFrame.useSoloistSelected();

    if( useOutlines )
      {
        lickgenFrame.fillMelody(BEAT, rhythm, chordProg, 0);

            // lickgen.getFillMelodyParameters(minPitch, maxPitch, minInterval,
            //                           maxInterval, BEAT, leapProb, chordProg,
            //                           0, avoidRepeats);

        MelodyPart solo = lickgen.generateSoloFromOutline(totalSlots);
        if( solo != null )
          {
            rhythm = lickgen.getRhythmFromSoloist(); //get the abstract melody for display
            if( lickgenFrame.useHeadSelected() )
              {
                adjustLickToHead(solo);
              }
            putLick(solo);
          }

      }

    //If the outlines is unable to generate a solo, which might
    //happen if there are no outlines of the correct length or the soloist
    //file was not correctly loaded, use the grammar

    if( rhythm == null || useOutlines == false )
      {

        if( true ) // useGrammar )
          {
            rhythm = lickgen.generateRhythmFromGrammar(totalSlots);
          }
        else
          {
            rhythm = lickgen.generateRandomRhythm(totalSlots, minDuration,
                                                  maxDuration,
                                                  restProb);
          }
        generateLick(rhythm);
      }

    if( rhythm != null )
      {
        lickgenFrame.setRhythmFieldText(Formatting.prettyFormat(rhythm));
      }

    if( nothingWasSelected )
      {
        stave.unselectAll();
      }
    else if( oneSlotWasSelected )
      {
        stave.setSelection(selectionStart);
      }

    setLickGenStatus("Done generating melody");
  }

/**
 * Rectify the current selection, aligning pitches to harmony.
 @param stave
 @param selectionStart
 @param selectionEnd
 */

private void rectifySelection(Stave stave, int selectionStart, int selectionEnd)
{
    ///System.out.println("rectifying from  " + selectionStart + " to " + selectionEnd);
    stave.resolvePitch(selectionStart, selectionEnd, false, false);
    
}

private void lowRangeTF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowRangeTF2ActionPerformed

    String lowNote = lowRangeTF2.getText();//GEN-LAST:event_lowRangeTF2ActionPerformed
    NoteSymbol n = NoteSymbol.makeNoteSymbol(lowNote);
    int midiValue = n.getMIDI();
    
    String highNote = highRangeTF2.getText();
    NoteSymbol h = NoteSymbol.makeNoteSymbol(highNote);
    int midiValueHigh = h.getMIDI();
    
    if (highNote.equals(""))
    {
        PianoKey high = keyboard.pianoKeys()[midiValue - 10];
        highRangeTF2.setText(high.getName());
    }
    else if (midiValue > midiValueHigh)
    {
        lowRangeTF2.setText(highNote);
        highRangeTF2.setText(lowNote);
    }
    
    int root = keyboard.findBass();
    String name = keyboard.findBassName(root);
    keyboard.setBass(name, root);
    buildVoicingTable();
    

}

private void highRangeTF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_highRangeTF2ActionPerformed

    String highNote = highRangeTF2.getText();
    NoteSymbol n = NoteSymbol.makeNoteSymbol(highNote);
    int midiValue = n.getMIDI();
    
    String lowNote = lowRangeTF2.getText();
    NoteSymbol l = NoteSymbol.makeNoteSymbol(lowNote);
    int midiValueLow = l.getMIDI();    
    
    if (lowNote.equals(""))
    {
        PianoKey low = keyboard.pianoKeys()[midiValue - 32];
        lowRangeTF2.setText(low.getName());
    }
    
    else if (midiValue < midiValueLow)
    {
        lowRangeTF2.setText(highNote);
        highRangeTF2.setText(lowNote);
    }
    
    int root = keyboard.findBass();
    String name = keyboard.findBassName(root);
    keyboard.setBass(name, root);
    buildVoicingTable();

}//GEN-LAST:event_highRangeTF2ActionPerformed
    
private void chordSearchTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordSearchTFActionPerformed

    String chord = chordSearchTF.getText();
    String root = keyboard.nameToBass(chord);
    String bass = root;
    chordRootTF.setText(root);
    bassNoteTF.setText(bass);
    rootEqualBassCheckbox.setSelected(true);
    
    if (chord.contains("/"))
    {
        int slash = chord.indexOf("/") + 1;
        bass = chord.substring(slash, chord.length());
        bassNoteTF.setText(bass);
        rootEqualBassCheckbox.setSelected(false);
    }
    
    int midi = keyboard.findBass();
    String note = keyboard.findBassName(midi);
    if (!note.equals(bass))
    {
        bassNoteTF.setText(bass);
    }
    keyboard.setBass(bass, midi);
    
    buildVoicingTable();

    int selectedRow = findChordinTable(chord);
    
    if (selectedRow == -1)
    {
        ErrorLog.log(ErrorLog.WARNING, "Chord not found in table!");
        return;
    }
    
    voicingTable.setColumnSelectionAllowed(false);
    voicingTable.setRowSelectionAllowed(true);
    voicingTable.setRowSelectionInterval(selectedRow, selectedRow);
    voicingTable.changeSelection(selectedRow,selectedRow,true,true);
    
    // Determine whether the chord name is a synonym.
    // If so, set the search field to the target chord name for the
    // user's convenience.
    
    String redirectName = getChordRedirectName(selectedRow);
    if( redirectName != null )
      {

      chordSearchTF.setText(redirectName);
      }
}//GEN-LAST:event_chordSearchTFActionPerformed

private void newVoicingNameTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newVoicingNameTFActionPerformed

    newVoicingSaveButtonActionPerformed(null);//GEN-LAST:event_newVoicingNameTFActionPerformed
}

private void delTabBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delTabBtnActionPerformed
        // initialize the option pane

        Object[] options = {"Yes", "No"};

        int choice = JOptionPane.showOptionDialog(this,

                "Do you wish to delete the current chorus?\n\nThis can't be undone.",

                "Delete Current Chorus?", JOptionPane.YES_NO_OPTION,

                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);



        // the user selected yes

        if (choice == 0 && currTabIndex >= 0

                && scoreTab.getTabCount() > 1) {



            score.delPart(currTabIndex);

            setupArrays();



            // set the menu and button states

            setItemStates();

        }

}//GEN-LAST:event_delTabBtnActionPerformed

private void pauseMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseMIActionPerformed
pauseBtnActionPerformed(null);
}//GEN-LAST:event_pauseMIActionPerformed


private void notateGrammarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notateGrammarMenuActionPerformed
 
}//GEN-LAST:event_notateGrammarMenuActionPerformed

private void notateGrammarMenuStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_notateGrammarMenuStateChanged


}//GEN-LAST:event_notateGrammarMenuStateChanged

private void notateGrammarMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_notateGrammarMenuMenuSelected

}//GEN-LAST:event_notateGrammarMenuMenuSelected

private void adviceTabbedPaneKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adviceTabbedPaneKeyPressed
    // TODO add your handling code here:
}//GEN-LAST:event_adviceTabbedPaneKeyPressed

private void transposeSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_transposeSpinnerStateChanged
setPlayTransposed();
}//GEN-LAST:event_transposeSpinnerStateChanged

private void loopButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_loopButtonKeyPressed
    keyPressed(evt);
}//GEN-LAST:event_loopButtonKeyPressed

private void playToolBarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_playToolBarKeyPressed
    keyPressed(evt);
}//GEN-LAST:event_playToolBarKeyPressed

private void exportChorusToMusicXMLActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportChorusToMusicXMLActionPerformed

    musicxmlfc.setDialogTitle("Export Leadsheet to MusicXML:");

    exportToMusicXML();

}//GEN-LAST:event_exportChorusToMusicXMLActionPerformed

private void countInCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_countInCheckBoxActionPerformed
    establishCountIn();
}//GEN-LAST:event_countInCheckBoxActionPerformed

private void formFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_formFocusLost

}//GEN-LAST:event_formFocusLost

private void formMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseEntered
    requestFocusInWindow();
}//GEN-LAST:event_formMouseEntered

private void formMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_formMouseClicked
   requestFocusInWindow();
}//GEN-LAST:event_formMouseClicked

private void formWindowLostFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowLostFocus
    //System.out.println("notate focus lost");
}//GEN-LAST:event_formWindowLostFocus

private void scoreTabKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_scoreTabKeyPressed
    keyPressed(evt);
}//GEN-LAST:event_scoreTabKeyPressed

private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
    keyPressed(evt);
}//GEN-LAST:event_formKeyPressed

private void chordFontStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_chordFontStateChanged
    int newSize = Integer.parseInt(chordFontSizeSpinner.getValue().toString());
    if( newSize < 1 )
      {
      newSize = 1;
      setChordFontSizeSpinner(newSize);
      }
    else if( newSize > 200 )
      {
      newSize = 200;
      setChordFontSizeSpinner(newSize);
      }

    setChordFontSize(newSize);
}//GEN-LAST:event_chordFontStateChanged

private int newChordFontSize = DEFAULT_CHORD_FONT_SIZE_VALUE;

private void defaultChordFontChange(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_defaultChordFontChange
    newChordFontSize = ((Integer)defaultChordFontSizeSpinner.getValue()).intValue();
}//GEN-LAST:event_defaultChordFontChange

private void defaultChordFontSizeSpinnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_defaultChordFontSizeSpinnerKeyReleased
    if( evt.getKeyCode() == KeyEvent.VK_ENTER )
      {
      newChordFontSize = ((Integer)defaultChordFontSizeSpinner.getValue()).intValue();
      savePrefs();
      }
}//GEN-LAST:event_defaultChordFontSizeSpinnerKeyReleased

private void earlyScrollBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_earlyScrollBtnActionPerformed
    if( earlyScrollBtn.isSelected() )
        {
        earlyScrollBtn.setBackground(new java.awt.Color(51, 255, 255));
        earlyScrollBtn.setText("<html><center>Early<br>Scroll</center></html>");
        earlyScrollBtn.setSelected(true);
        }
      else
        {
        earlyScrollBtn.setBackground(Color.red);
        earlyScrollBtn.setText("<html><center>Late<br>Scroll</center></html>");
        earlyScrollBtn.setSelected(false);
        }
}//GEN-LAST:event_earlyScrollBtnActionPerformed

private void setChordFontSize(int newSize)
{
    score.setChordFontSize(newSize);
    setStavesChordFontSize();
    getCurrentStave().repaint();
}

private void setChordFontSizeSpinner(int newSize)
{
    chordFontSizeSpinner.setValue(newSize);
}

// For key pressed in various places:

public void keyPressed(java.awt.event.KeyEvent evt)
{
//System.out.println("notate key pressed " + evt);
     requestFocusInWindow();
    getCurrentStaveActionHandler().keyPressed(evt);
}

private void notateGrammarMenuAction(java.awt.event.ActionEvent evt) {
    JMenuItem item = (JMenuItem)evt.getSource();
    String stem = item.getText();
    notateGrammarMenu.setText("Grammar: " + stem);
    grammarFile = basePath + File.separator +  "vocab"+ File.separator +  stem + GrammarFilter.EXTENSION;
    lickgen.loadGrammar(grammarFile);
}

public void openCorpus()
{
    openLeadsheet(true);
}

 /**
  * Populate the grammar menu in the Notate window
  */

private void populateNotateGrammarMenu()
  {
    File directory = grammarfc.getCurrentDirectory();
    if( directory.isDirectory() )
      {
        String fileName[] = directory.list();
        for( int i = 0; i < fileName.length; i++ )
          {
            String name = fileName[i];

            if( name.endsWith(GrammarFilter.EXTENSION) )
              {
                int len = name.length();
                String stem = name.substring(0, len - GrammarFilter.EXTENSION.length());
                JMenuItem item = new JMenuItem(stem);
                notateGrammarMenu.add(item);
                item.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                    notateGrammarMenuAction(evt);
                    }
                });
              }
          }
      }
  }


/**
 * Clear productions saved in file.
 */

public void clearAccumulatedProductions()
{
    String outFile = getGrammarFileName();
    File f = new File(outFile);
    File f2 = new File(f.getParentFile().getPath() + File.separator + Directories.accumulatedProductions);
    if (f2.exists()) {
        System.gc();
        boolean deleted = f2.delete();
        while (!deleted) {
            deleted = f2.delete();
            //System.out.println(deleted);
        }
    }

    setLickGenStatus("Accumulated productions cleared.");

}


public boolean rootEqualBassCheckboxChecked()
{
    return rootEqualBassCheckbox.isSelected();
}

public void setBassLowRangeTF(String text)
{
    lowRangeTF2.setText(text);
}

public void setBassHighRangeTF(String text)
{
    highRangeTF2.setText(text);
}

public String bassLowRangeTFText()
{
    return lowRangeTF2.getText();
}

public String bassHighRangeTFText()
{
    return highRangeTF2.getText();
}

public void setBassNoteTFText(String text)
{
    bassNoteTF.setText(text);
}

public String getBassNoteTFText()
{
    return bassNoteTF.getText();
}
  
public void showDeleteVoicingDialog()
{
    deleteVoicingDialog.setSize(325,200);
    
    deleteVoicingDialog.setLocationRelativeTo(this);
    
    deleteVoicingDialog.setVisible(true);
}

public void showNewVoicingDialog()
    {
        newVoicingDialog.setSize(350, 200);
        
        newVoicingDialog.setLocationRelativeTo(this);
        
        newVoicingDialog.setVisible(true);
        
        ListSelectionModel rowSM = voicingTable.getSelectionModel();

        if( !rowSM.isSelectionEmpty() ) {
        
            int rowIndex = voicingTable.getSelectedRow();
        
            int colIndex = 0;
        
            Object o = voicingTable.getValueAt(rowIndex, colIndex);
        
            String chord = o.toString();
        
            newVoicingChordTF.setText(chord);
        }
        
        else {
            newVoicingChordTF.setText("");
        }
 
    }



  /**
   * Set the indexed preference with a value from the corresponding CheckBox.
  @param index
  @param checkbo
   */
  void setCheckBoxPreferences(int index, JCheckBox checkbox)
    {
    char boxStates[] =
            Preferences.getPreference(Preferences.ALWAYS_USE_BUTTONS).toCharArray();
    boxStates[index] = checkbox.isSelected() ? TRUE_CHECK_BOX : FALSE_CHECK_BOX;
    Preferences.setPreference(Preferences.ALWAYS_USE_BUTTONS,
            new String(boxStates));
    }

  /**
   *
   * Sets up arrays for Part and Stave classes, which will be displayed in
   *
   * scoreFrame. The array of Staves initializes every <code>stave</code> to
   *
   * the <code>type</code> TREBLE.
   *
   *
   *
   * @see Stave#Stave(String, Notate)
   *
   */
  public void setupArrays()
    {

    int size = score.size();

    if( size <= 0 )
      {

      return;
      }

    // setup the arrays

    partList = new PartList(size);
    

    // set the chord progression for the score

    chordProg = score.getChordProg();

    scoreTab.removeAll();

    currTabIndex = 0;

    staveScrollPane = new StaveScrollPane[size];


    // initialize each array with the appropriate part and as a grand stave

    for( int i = 0; i < staveScrollPane.length; i++ )
      {

      // construct the specific scoreBG and staveScrollPane

      StaveScrollPane pane = new StaveScrollPane(i);

      staveScrollPane[i] = pane;

      AdjustmentListener scrollListener = new AdjustmentListener()
        {
        public void adjustmentValueChanged(AdjustmentEvent e)
          {

          if( e.getValueIsAdjusting() )
            {
            autoScrollOnPlayback = false;
            }

          }

        };

      pane.getHorizontalScrollBar().addAdjustmentListener(scrollListener);

      pane.getVerticalScrollBar().addAdjustmentListener(scrollListener);



      // Set the scroll bars in have more response to up and down arrows

      pane.getHorizontalScrollBar().setUnitIncrement(30);

      pane.getVerticalScrollBar().setUnitIncrement(30);



      // Set the notation components for this particular stave

      pane.setBGcolor(new java.awt.Color(255, 255, 255));

      pane.setBackground(new java.awt.Color(204, 204, 204));

      pane.resetViewportView();

      pane.getAccessibleContext().setAccessibleParent(scoreTab);



      // Setup the Stave component in the pane

      partList.add(score.getPart(i));

      Stave stave = new Stave(partList.get(i).getStaveType(), this,
              score.getTitle());

      pane.setStave(stave);

      stave.setChordProg(chordProg);

      stave.setPart(partList.get(i));

      stave.setKeySignature(partList.get(i).getKeySignature());

      stave.setMetre(score.getMetre()[0], score.getMetre()[1]);

      stave.setShowBarNums(true);

      stave.setShowPartTitle(true);

      // Only Show title on first page

      stave.setShowSheetTitle(i == 0 && showTitlesMI.isSelected());


      // Set the stave into a new tab

      scoreTab.addTab(staveScrollPane[i].getStave().getPartTitle(), pane);

      refreshTabTitle(i);

      scoreTab.setSelectedComponent(pane);

      }


    /*
     * update GUI to reflect total time of the score
     */

    setPlaybackManagerTime();

    updateAllStaves();
    }

  /**
   *
   * Updates the JPanel with all of the staves and redraws it
   *
   * @see Stave#paint(Graphics)
   *
   */
  public void updateAllStaves()
    {

    // cycle through the array and update all of the staves

    for( int i = 0; i < staveScrollPane.length; i++ )
      {

      StaveScrollPane pane = staveScrollPane[i];

      // clear the panel area of the previous stave image

      pane.removeAllBG();

      // add to display

      pane.addStave(staveScrollPane[i].getStave());

      pane.repaint();
      }
    }

    /**
     * Change the chord font size in each Stave.
     */

    public void setStavesChordFontSize()
    {
    for( int i = 0; i < staveScrollPane.length; i++ )
      {
      staveScrollPane[i].getStave().setChordFontSize();
      }
    }

  /**
   *
   * Converts a polylist to the form required by the advice tree
   *
   *
   *
   * @param item                          a polylist
   *
   * @return DefaultMutableTreeNode       the root of a tree
   *
   */
  public DefaultMutableTreeNode polylistToTree(Polylist item)
    {

    DefaultMutableTreeNode tree = new DefaultMutableTreeNode(item.first());

    item = item.rest();

    while( !item.isEmpty() )
      {

      if( item.first() instanceof Polylist )
        {
        tree.add(polylistToTree((Polylist)item.first()));
        }
      else
        {
        tree.add(new DefaultMutableTreeNode(item.first()));
        }

      item = item.rest();

      }
    return tree;
    }

  public void polylistToMenus(Polylist item)
    {

    // ignore for now: DefaultMutableTreeNode root = new DefaultMutableTreeNode(item.first());


    item = item.rest();

    while( !item.isEmpty() )
      {
      Object node = item.first();

      if( node instanceof Polylist )
        {
        polylistToMenus((Polylist)node);
        }
      else
        {
        if( node instanceof AdviceForScale )  // avoid single note advice here
          {
          adviceMenuItemsScales.add(node);
          }
        else if( node instanceof AdviceForCell )
          {
          adviceMenuItemsCells.add(node);
          }
        else if( node instanceof AdviceForIdiom )
          {
          adviceMenuItemsIdioms.add(node);
          }
        else if( node instanceof AdviceForLick )
          {
          adviceMenuItemsLicks.add(node);
          }
        else if( node instanceof AdviceForQuote )
          {
          adviceMenuItemsQuotes.add(node);
          }
        else
          {
          }
        }
      item = item.rest();

      }

    final Object[] menuContentsScales = adviceMenuItemsScales.toArray();

    adviceScrollListScales.setModel(new javax.swing.AbstractListModel()
      {
      public int getSize()
        {
        return menuContentsScales.length;
        }

      public Object getElementAt(int i)
        {
        return menuContentsScales[i];
        }

      });


    final Object[] menuContentsCells = adviceMenuItemsCells.toArray();

    adviceScrollListCells.setModel(new javax.swing.AbstractListModel()
      {
      public int getSize()
        {
        return menuContentsCells.length;
        }

      public Object getElementAt(int i)
        {
        return menuContentsCells[i];
        }

      });

    final Object[] menuContentsIdioms = adviceMenuItemsIdioms.toArray();

    adviceScrollListIdioms.setModel(new javax.swing.AbstractListModel()
      {
      public int getSize()
        {
        return menuContentsIdioms.length;
        }

      public Object getElementAt(int i)
        {
        return menuContentsIdioms[i];
        }

      });

    final Object[] menuContentsLicks = adviceMenuItemsLicks.toArray();

    adviceScrollListLicks.setModel(new javax.swing.AbstractListModel()
      {
      public int getSize()
        {
        return menuContentsLicks.length;
        }

      public Object getElementAt(int i)
        {
        return menuContentsLicks[i];
        }

      });

    final Object[] menuContentsQuotes = adviceMenuItemsQuotes.toArray();

    adviceScrollListQuotes.setModel(new javax.swing.AbstractListModel()
      {
      public int getSize()
        {
        return menuContentsQuotes.length;
        }

      public Object getElementAt(int i)
        {
        return menuContentsQuotes[i];
        }

      });

    }

  /**
   * major sub-trees to be excluded from Notes advice tree
   */
  static Polylist excludeFromTree = Polylist.list(" scale tones", " cells",
          " idioms", " licks", " quotes");

  /**
   *
   * Displays the advice tree for the chords around the given index.
   *
   *
   *
   * @param selectedIndex         the index currently selected on the stave
   *
   * @param row                   the row to be initially selected
   *
   * @param focus                 if the advice frame should receive focus or
   *
   *                              not
   *
   */
  public void displayAdviceTree(int selectedIndex, int row, Note note)
    {

    setStatus("Getting Advice");

    Trace.log(2, "displayAdviceTree");

    adviceFrame.setTitle("Advice for " + score.getTitle());

    DefaultTreeCellRenderer tcr =
            (DefaultTreeCellRenderer)adviceTree.getCellRenderer();

    tcr.setBorderSelectionColor(Color.black);

    tcr.setBackgroundSelectionColor(Color.white);

    tcr.setTextSelectionColor(Color.red);

    tcr.setTextNonSelectionColor(Color.black);

    Trace.log(2, "advice tree display requested, resetting adviceUsed");

    resetAdviceUsed();

    if( adv != null )
      {

      MelodyPart part = getCurrentStave().getDisplayPart();
      //Note note = part.getNote(selectedIndex);
      adviceList = adv.getAdviceTree(score, selectedIndex, note);

      if( adviceList == null )
        {

        Trace.log(2, "adviceList is null");

        setStatus("");

        return;
        }


      adviceMenuItemsScales = new ArrayList<Object>();
      adviceMenuItemsCells  = new ArrayList<Object>();
      adviceMenuItemsIdioms = new ArrayList<Object>();
      adviceMenuItemsLicks  = new ArrayList<Object>();
      adviceMenuItemsQuotes = new ArrayList<Object>();

      polylistToMenus(adviceList);

      adviceFrame.setTitle((String)adviceList.first());

      DefaultMutableTreeNode tree = new DefaultMutableTreeNode("Notes");

      ((DefaultTreeModel)adviceTree.getModel()).setRoot(tree);

      adviceList = adviceList.rest();
      while( adviceList.nonEmpty() )
        {
        Object first = adviceList.first();
        if( first instanceof Polylist )
          {
          Polylist list = (Polylist)first;
          String title = list.first().toString().trim();
          if( nonmember(title, excludeFromTree) )
            {
            tree.add(polylistToTree(list));
            }
          }
        adviceList = adviceList.rest();
        }

      }

    if( row == OUT_OF_BOUNDS )
      {
      adviceTree.setSelectionRow(0);
      }
    else
      {
      adviceTree.expandRow(row);

      adviceTree.setSelectionRow(row);
      }

    openAdviceFrame();
    //Trace.log(2, "advice tree display completed");
    }

  static boolean nonmember(String string, Polylist list)
    {
    while( !list.isEmpty() )
      {
      if( string.equals(list.first().toString().trim()) )
        {
        return false;
        }
      list = list.rest();
      }
    return true;
    }

  /**
   *
   * Displays the window allowing overriding of measures
   *
   */
  public void displayOverrideMeasures()
    {

    disableAccelerators();

    overrideFrame.setSize(new Dimension(210, 100));

    overrideFrame.setLocationRelativeTo(this);


    lineLabel.setText("Number of measures in line " + (getCurrentStave().currentLine + 1) + ":   ");

    enterMeasures.setText("" + lockedMeasures[getCurrentStave().currentLine]);

    measErrorLabel.setText("");


    overrideFrame.setVisible(true);

    enterMeasures.requestFocusInWindow();

    Trace.log(2, "override measures frame has focus");
    }

  /**
   *
   * Overrides the number of measures within lockedMeasures. The behavior now
   *
   * is to add any measures from a "shrunk" line to the end of the part, or to
   *
   * take measures from the end of a part in order to "expand" a line. The
   *
   * default number of measures at the end of a part is set to 4, meaning
   *
   * that if 6 measures would be tacked onto the end of a part they are added
   *
   * as a line of 4 measures and a line of 2 measures.
   *
   *
   *
   * @param measures          number of measures to set the current line to
   *
   * @param currLine          the current line to set the measures on
   *
   */

  public void measureOverride(int measures, int currLine)
    {

    int diff = lockedMeasures[currLine] - measures;

    lockedMeasures[currLine] = measures;


    // if the difference is negative, subtract the difference

    // from the ending stave lines

    if( diff < 0 )
      {
      while( diff < 0 )
        {

        if( lockedMeasures[lockedMeasures.length - 1] > Math.abs(diff) )
          {

          lockedMeasures[lockedMeasures.length - 1] += diff;

          diff = 0;
          }
        else
          {
          diff = diff + lockedMeasures[lockedMeasures.length - 1];

          lockedMeasures[lockedMeasures.length - 1] = 0;

          int[] tempLockedMeasures =
                  new int[lockedMeasures.length - 1];


          for( int k = 0; k < tempLockedMeasures.length; k++ )
            {
            tempLockedMeasures[k] = lockedMeasures[k];
            }

          setLockedMeasures(tempLockedMeasures, "measureOverride1");
          }
        }
      }
    // if the difference is positive, add the difference to the
    // ending stave lines
    else
      {
      while( diff > 0 )
        {
        // increase the array if the last line already has

        // 4 measures in it

        if( lockedMeasures[lockedMeasures.length - 1] >= 4 )
          {

          int[] tempLockedMeasures =
                  new int[lockedMeasures.length + 1];



          for( int k = 0; k < lockedMeasures.length; k++ )
            {
            tempLockedMeasures[k] = lockedMeasures[k];
            }

          tempLockedMeasures[lockedMeasures.length] = 0;

          setLockedMeasures(tempLockedMeasures, "measureOverride2");
          }


        // add 4 or less measures to an empty last line

        if( lockedMeasures[lockedMeasures.length - 1] == 0 )
          {
          if( diff <= 4 )
            {

            lockedMeasures[lockedMeasures.length - 1] = diff;

            diff = 0;
            }
          else
            {
            lockedMeasures[lockedMeasures.length - 1] = 4;

            diff = diff - 4;
            }
          }
        // add some measures to a partially filled last line
        else if( lockedMeasures[lockedMeasures.length - 1] > 0 && lockedMeasures[lockedMeasures.length - 1] <= 4 )
          {
          if( diff + lockedMeasures[lockedMeasures.length - 1] <= 4 )
            {
            lockedMeasures[lockedMeasures.length - 1] += diff;

            diff = 0;
            }
          else
            {
            diff -= (4 - lockedMeasures[lockedMeasures.length - 1]);

            lockedMeasures[lockedMeasures.length - 1] = 4;
            }
          }
        }
      }
    }

  /**
   *
   * Sets all of the menu and button states
   *
   */
  protected void setItemStates()
    {

    playAllMI.setEnabled(true);

    stopPlayMI.setEnabled(true);



    // check to see if undo & redo can be enabled

    if( cm.canUndo() )
      {

      undoMI.setEnabled(true);

      undoPMI.setEnabled(true);

      undoBtn.setEnabled(true);

      }
    else
      {

      undoMI.setEnabled(false);

      undoPMI.setEnabled(false);

      undoBtn.setEnabled(false);

      }



    if( cm.canRedo() )
      {

      redoMI.setEnabled(true);

      redoPMI.setEnabled(true);

      redoBtn.setEnabled(true);

      }
    else
      {

      redoMI.setEnabled(false);

      redoPMI.setEnabled(false);

      redoBtn.setEnabled(false);

      }



    // checks if a construction line is selected



    if( slotIsSelected() )
      {

      cutChordsMI.setEnabled(true);

      copyChordsMI.setEnabled(true);

      // cut enabled

      cutMelodyMI.setEnabled(true);

      cutBothMI.setEnabled(true);

      cutBothPMI.setEnabled(true);

      cutBothBtn.setEnabled(true);

      // copy enabled

      copyMelodyMI.setEnabled(true);



      copyBothMI.setEnabled(true);

      copyBothPMI.setEnabled(true);

      copyBothBtn.setEnabled(true);



      boolean melodyClipboardNonEmpty = impro.melodyClipboardNonEmpty();

      boolean chordsClipboardNonEmpty = impro.chordsClipboardNonEmpty();

      boolean eitherNonEmpty =
              melodyClipboardNonEmpty || chordsClipboardNonEmpty;



      pasteMelodyMI.setEnabled(melodyClipboardNonEmpty);

      pasteChordsMI.setEnabled(chordsClipboardNonEmpty);

      pasteBothMI.setEnabled(eitherNonEmpty);



      pasteBothPMI.setEnabled(eitherNonEmpty);

      pasteBothBtn.setEnabled(eitherNonEmpty);



      enterMelodyMI.setEnabled(true);

      enterChordsMI.setEnabled(true);

      enterBothMI.setEnabled(true);



      addRestMI.setEnabled(true);

      selectAllMI.setEnabled(true);



      transposeBothDownSemitone.setEnabled(true);

      transposeBothUpSemitone.setEnabled(true);

      transposeChordsUpSemitone.setEnabled(true);

      transposeChordsDownSemitone.setEnabled(true);

      transposeMelodyDownOctave.setEnabled(true);

      transposeMelodyDownSemitone.setEnabled(true);

      transposeMelodyUpOctave.setEnabled(true);

      transposeMelodyUpSemitone.setEnabled(true);

      transposeMelodyUpHarmonically.setEnabled(true);

      transposeMelodyDownHarmonically.setEnabled(true);


      copyMelodySelectionToTextWindow.setEnabled(true);

      copyChordSelectionToTextWindow.setEnabled(true);

      copyBothSelectionToTextWindow.setEnabled(true);

      resolvePitches.setEnabled(true);


      // REVISIT generateLickButton.setEnabled(true);


      saveSelectionAsLick.setEnabled(true);


      // advice enabled

      advicePMI.setEnabled(true);


      reverseMelody.setEnabled(true);

      invertMelody.setEnabled(true);

      }
    else
      {
      // No slot selected

      cutMelodyMI.setEnabled(false);

      cutBothMI.setEnabled(false);

      cutBothPMI.setEnabled(false);

      cutBothBtn.setEnabled(false);



      copyMelodyMI.setEnabled(false);

      copyBothMI.setEnabled(false);

      copyBothPMI.setEnabled(false);

      copyBothBtn.setEnabled(false);



      pasteBothMI.setEnabled(false);

      pasteBothPMI.setEnabled(false);

      pasteBothBtn.setEnabled(false);



      enterMelodyMI.setEnabled(false);

      enterChordsMI.setEnabled(false);

      enterBothMI.setEnabled(false);



      transposeBothDownSemitone.setEnabled(false);

      transposeBothUpSemitone.setEnabled(false);

      transposeChordsUpSemitone.setEnabled(false);

      transposeChordsDownSemitone.setEnabled(false);

      transposeMelodyDownOctave.setEnabled(false);

      transposeMelodyDownSemitone.setEnabled(false);

      transposeMelodyUpOctave.setEnabled(false);

      transposeMelodyUpSemitone.setEnabled(false);

      transposeMelodyUpHarmonically.setEnabled(false);

      transposeMelodyDownHarmonically.setEnabled(false);


      advicePMI.setEnabled(false);


      reverseMelody.setEnabled(false);

      invertMelody.setEnabled(false);
      }

    // checks if "delete tab" should be enabled

    if( score.size() < 2 )
      {
       delTabBtn.setEnabled(false);
      }
    else
      {
       delTabBtn.setEnabled(true);
      }

 
    // checks if "Override Measures" should be allowed

    if( autoAdjustMI.isSelected() )
      {
      overrideMeasPMI.setEnabled(false);
      }
    else
      {
      overrideMeasPMI.setEnabled(true);
      }

    }

  /**
   *
   * Disable accelerators.  We do this for a work-around: When letters are typed into the
   *
   * chord/melody text entry window 
   *
   * if any of those letters correspond to accelerators, the corresponding method will
   *
   * be invoked.  This seems like a bug in swing to me, but I don't know another way of
   *
   * getting around it at present.  We will rely on the call to setItemStates() to
   *
   * re-enable the accelerators upon hitting return in the text entry field.
   *
   */
  protected void disableAccelerators()
    {

    addRestMI.setEnabled(false);


    playAllMI.setEnabled(false);

    stopPlayMI.setEnabled(false);


    undoMI.setEnabled(false);

    redoMI.setEnabled(false);


    cutMelodyMI.setEnabled(false);

    cutChordsMI.setEnabled(false);

    cutBothMI.setEnabled(false);


    copyMelodyMI.setEnabled(false);

    copyChordsMI.setEnabled(false);

    copyBothMI.setEnabled(false);


    pasteMelodyMI.setEnabled(false);

    pasteChordsMI.setEnabled(false);

    pasteBothMI.setEnabled(false);


    enterMelodyMI.setEnabled(false);

    enterChordsMI.setEnabled(false);

    enterBothMI.setEnabled(false);


    transposeBothDownSemitone.setEnabled(false);

    transposeBothUpSemitone.setEnabled(false);

    transposeChordsUpSemitone.setEnabled(false);

    transposeChordsDownSemitone.setEnabled(false);

    transposeMelodyDownOctave.setEnabled(false);

    transposeMelodyDownSemitone.setEnabled(false);

    transposeMelodyUpOctave.setEnabled(false);

    transposeMelodyUpSemitone.setEnabled(false);

    transposeMelodyUpHarmonically.setEnabled(false);

    transposeMelodyDownHarmonically.setEnabled(false);


    copyMelodySelectionToTextWindow.setEnabled(false);

    copyChordSelectionToTextWindow.setEnabled(false);

    copyBothSelectionToTextWindow.setEnabled(false);


    reverseMelody.setEnabled(false);

    invertMelody.setEnabled(false);
    
    resolvePitches.setEnabled(false);


    saveSelectionAsLick.setEnabled(false);
    
    // REVISIT generateLickButton.setEnabled(false);

    }

  private void resetDrawingPrefs()
    {

    drawScaleTonesCheckBox.setSelected(
            Preferences.getPreference(Preferences.DRAWING_TONES).charAt(0) == '1');

    drawChordTonesCheckBox.setSelected(
            Preferences.getPreference(Preferences.DRAWING_TONES).charAt(1) == '1');

    drawColorTonesCheckBox.setSelected(
            Preferences.getPreference(Preferences.DRAWING_TONES).charAt(2) == '1');

    defaultDrawingMutedCheckBox.setSelected(
            Preferences.getPreference(Preferences.DEFAULT_DRAWING_MUTED).equals("true"));

    }

  private ImageIcon playButton =
          new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"));

  private ImageIcon pauseButton =
          new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pause.gif"));

    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMI;
    private javax.swing.JButton acceptTruncate;
    private javax.swing.JMenuItem addRestMI;
    private javax.swing.JButton addTabBtn;
    protected javax.swing.JFrame adviceFrame;
    private javax.swing.JMenuItem advicePMI;
    private javax.swing.JScrollPane adviceScroll0;
    private javax.swing.JList adviceScrollListCells;
    private javax.swing.JList adviceScrollListIdioms;
    private javax.swing.JList adviceScrollListLicks;
    private javax.swing.JList adviceScrollListQuotes;
    private javax.swing.JList adviceScrollListScales;
    private javax.swing.JTabbedPane adviceTabbedPane;
    protected javax.swing.JTree adviceTree;
    private javax.swing.JCheckBoxMenuItem allCstrLinesMI;
    private javax.swing.JCheckBox allMuteMixerBtn;
    private javax.swing.JToggleButton allMuteToolBarBtn;
    private javax.swing.JPanel allPanel;
    private javax.swing.JSlider allVolumeMixerSlider;
    private javax.swing.JSlider allVolumeToolBarSlider;
    private javax.swing.JCheckBox alwaysUseBass;
    private javax.swing.JCheckBox alwaysUseChord;
    private javax.swing.JCheckBox alwaysUseMelody;
    private javax.swing.JCheckBox alwaysUseStave;
    private javax.swing.JPanel appearanceTab;
    private javax.swing.ButtonGroup approachColorBtnGrp;
    private javax.swing.JLabel approachToneLabel;
    private javax.swing.JCheckBox approachTones;
    private javax.swing.JCheckBoxMenuItem autoAdjustMI;
    private javax.swing.JRadioButton autoStave;
    private javax.swing.JRadioButton autoStaveBtn;
    private javax.swing.JCheckBoxMenuItem barNumsMI;
    private javax.swing.JLabel bassInstLabel;
    private javax.swing.JPanel bassInstPanel;
    private javax.swing.JCheckBox bassMute;
    private javax.swing.JLabel bassNoteLabel;
    private javax.swing.JTextField bassNoteTF;
    private javax.swing.JPanel bassPanel;
    private javax.swing.JRadioButton bassStave;
    private javax.swing.JRadioButton bassStaveBtn;
    private javax.swing.JScrollPane bassStyleSpecScrollPane;
    private javax.swing.JPanel bassTabPanel;
    private javax.swing.JSlider bassVolume;
    private javax.swing.JToggleButton beamButton;
    private javax.swing.JRadioButton blackApproachBtn;
    private javax.swing.JRadioButton blackChordBtn;
    private javax.swing.JRadioButton blackColorBtn;
    private javax.swing.JLabel blackLabel;
    private javax.swing.JRadioButton blackOtherBtn;
    private javax.swing.JRadioButton blueApproachBtn;
    private javax.swing.JRadioButton blueChordBtn;
    private javax.swing.JRadioButton blueColorBtn;
    private javax.swing.JLabel blueLabel;
    private javax.swing.JRadioButton blueOtherBtn;
    private javax.swing.JLabel breakpointLabel;
    private javax.swing.JTextField breakpointTF;
    private javax.swing.JButton buildTableButton;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JPanel cachePanel;
    private javax.swing.JTextField cacheSize;
    private javax.swing.JPanel cacheTab;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JButton cancelBtn1;
    private javax.swing.JButton cancelBtn2;
    private javax.swing.JButton cancelBtn3;
    private javax.swing.JButton cancelLickTitle;
    private javax.swing.JButton cancelTruncate;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JRadioButton cellRadioButton;
    private javax.swing.JCheckBox cells;
    private javax.swing.ButtonGroup chordColorBtnGrp;
    private javax.swing.JTextField chordDist;
    private javax.swing.JLabel chordDistLabel;
    private javax.swing.JCheckBox chordExtns;
    private javax.swing.JSpinner chordFontSizeSpinner;
    private javax.swing.JLabel chordIInstLabel;
    private javax.swing.JPanel chordInstPanel;
    private javax.swing.JCheckBox chordMute;
    private javax.swing.JPanel chordPanel;
    private javax.swing.JLabel chordRootLabel;
    private javax.swing.JTextField chordRootTF;
    private javax.swing.JLabel chordSearchLabel;
    private javax.swing.JTextField chordSearchTF;
    private javax.swing.JCheckBox chordSubs;
    private javax.swing.JPanel chordTabPanel;
    private javax.swing.JLabel chordToneLabel;
    private javax.swing.JCheckBox chordTones;
    private javax.swing.JSlider chordVolume;
    private javax.swing.JToggleButton chorusBtn;
    private javax.swing.JPanel chorusPreferences;
    private javax.swing.JMenuItem chorusPrefsMI;
    private javax.swing.JPanel chorusSpecificPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.ButtonGroup colorColorBtnGrp;
    private javax.swing.JLabel colorToneLabel;
    private javax.swing.JCheckBox colorTones;
    private javax.swing.JToggleButton colorationButton;
    private javax.swing.JLabel commentsLabel;
    private javax.swing.JTextField commentsTF;
    private javax.swing.JTextField composerField;
    private javax.swing.JLabel composerLabel;
    private javax.swing.JPanel contToneChoices;
    private javax.swing.JToggleButton contourBtn;
    private javax.swing.JPanel contourPreferences;
    private javax.swing.JMenuItem contourPrefsMI;
    private javax.swing.JMenuItem contractMelodyBy2;
    private javax.swing.JMenuItem contractMelodyBy3;
    private javax.swing.JButton copyBothBtn;
    private javax.swing.JMenuItem copyBothMI;
    private javax.swing.JMenuItem copyBothPMI;
    private javax.swing.JMenuItem copyBothSelectionToTextWindow;
    private javax.swing.JMenuItem copyChordSelectionToTextWindow;
    private javax.swing.JMenuItem copyChordsMI;
    private javax.swing.JMenuItem copyMelodyMI;
    private javax.swing.JMenuItem copyMelodySelectionToTextWindow;
    private javax.swing.JCheckBox countInCheckBox;
    private javax.swing.JPanel countInPanel;
    private javax.swing.JPanel currentStyleTab;
    private javax.swing.JButton cutBothBtn;
    private javax.swing.JMenuItem cutBothMI;
    private javax.swing.JMenuItem cutBothPMI;
    private javax.swing.JMenuItem cutChordsMI;
    private javax.swing.JMenuItem cutMelodyMI;
    private javax.swing.JPanel defAllPanel;
    private javax.swing.JLabel defBassInstLabel;
    private javax.swing.JPanel defBassInstPanel;
    private javax.swing.JPanel defBassVolPanel;
    private javax.swing.JSlider defBassVolSlider;
    private javax.swing.JLabel defCacheSizeLabel;
    private javax.swing.JLabel defChordInstLabel;
    private javax.swing.JPanel defChordPanel;
    private javax.swing.JPanel defChordVolPanel;
    private javax.swing.JSlider defChordVolSlider;
    private javax.swing.JPanel defDrumVolPanel;
    private javax.swing.JSlider defDrumVolSlider;
    private javax.swing.JPanel defEntryPanel;
    private javax.swing.JSlider defEntryVolSlider;
    private javax.swing.ButtonGroup defLoadStaveBtnGroup;
    private javax.swing.JSlider defMasterVolSlider;
    private javax.swing.JLabel defMelodyInstLabel;
    private javax.swing.JPanel defMelodyPanel;
    private javax.swing.JPanel defMelodyVolPanel;
    private javax.swing.JSlider defMelodyVolSlider;
    private javax.swing.JComboBox defStyleComboBox;
    private javax.swing.JLabel defStyleLabel;
    private javax.swing.JLabel defTempoLabel;
    private javax.swing.JTextField defVocabFile;
    private javax.swing.JLabel defVocabFileLabel;
    private javax.swing.JPanel defVolumes;
    private javax.swing.JSpinner defaultChordFontSizeSpinner;
    private javax.swing.JCheckBox defaultDrawingMutedCheckBox;
    private javax.swing.JPanel defaultStaveTypePanel;
    private javax.swing.JTextField defaultTempoTF;
    private javax.swing.JPanel defaultsTab;
    private javax.swing.JButton delSectionButton;
    private javax.swing.JButton delTabBtn;
    private javax.swing.JButton deleteVoicingCancelButton;
    private javax.swing.JDialog deleteVoicingDialog;
    private javax.swing.JLabel deleteVoicingLabel;
    private javax.swing.JButton deleteVoicingOKButton;
    private javax.swing.JPanel devicesTab;
    private javax.swing.JButton drawButton;
    private javax.swing.JCheckBox drawChordTonesCheckBox;
    private javax.swing.JCheckBox drawColorTonesCheckBox;
    private javax.swing.JCheckBox drawScaleTonesCheckBox;
    private javax.swing.JCheckBox drumMute;
    private javax.swing.JPanel drumPanel;
    private javax.swing.JPanel drumTabPanel;
    private javax.swing.JSlider drumVolume;
    private javax.swing.JPanel dummyPanel;
    private javax.swing.JDialog duplicateLickDialog;
    private javax.swing.JLabel duplicateLickLabel;
    private javax.swing.JScrollPane duplicateLickScroll;
    private javax.swing.JTextPane duplicateLickText;
    private javax.swing.JToggleButton earlyScrollBtn;
    private javax.swing.JCheckBox echoMidiCheckBox;
    private javax.swing.JMenu editMenu;
    private javax.swing.JCheckBox enableCache;
    private javax.swing.JMenuItem enterBothMI;
    private javax.swing.JMenuItem enterChordsMI;
    private javax.swing.JTextField enterLickTitle;
    private javax.swing.JTextField enterMeasures;
    private javax.swing.JMenuItem enterMelodyMI;
    private javax.swing.JCheckBox entryMute;
    private javax.swing.JPanel entryPanel;
    private javax.swing.JSlider entryVolume;
    private javax.swing.JMenuItem expandMelodyBy2;
    private javax.swing.JMenuItem expandMelodyBy3;
    private javax.swing.JMenuItem exportAllToMidi;
    private javax.swing.JMenuItem exportChorusToMusicXML;
    private javax.swing.JLabel extEntryLabel;
    private javax.swing.JTextField extEntryTF;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToggleButton freezeLayoutButton;
    private javax.swing.JPanel generalContourTab;
    private javax.swing.JMenuItem generateLickInSelection;
    private javax.swing.JButton generateToolbarBtn;
    private javax.swing.ButtonGroup generatorButtonGroup;
    private javax.swing.JToggleButton globalBtn;
    private javax.swing.JPanel globalPreferences;
    private javax.swing.JMenuItem globalPrefsMI;
    private javax.swing.JTabbedPane globalTabs;
    private javax.swing.ButtonGroup grammarExtractionButtonGroup;
    private javax.swing.JRadioButton grandStave;
    private javax.swing.JRadioButton grandStaveBtn;
    private javax.swing.JRadioButton greenApproachBtn;
    private javax.swing.JRadioButton greenChordBtn;
    private javax.swing.JRadioButton greenColorBtn;
    private javax.swing.JLabel greenLabel;
    private javax.swing.JRadioButton greenOtherBtn;
    private javax.swing.JMenuItem helpAboutMI;
    private javax.swing.JMenuItem helpMI;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JTextField highRangeTF;
    private javax.swing.JTextField highRangeTF2;
    private javax.swing.JRadioButton idiomRadioButton;
    private javax.swing.JCheckBox idioms;
    private javax.swing.JButton ignoreDuplicate;
    private javax.swing.JMenuItem insertRestMeasure;
    private javax.swing.JButton insertVoicingButton;
    private javax.swing.JMenuItem invertMelody;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel19;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel20;
    private javax.swing.JPanel jPanel21;
    private javax.swing.JPanel jPanel22;
    private javax.swing.JPanel jPanel23;
    private javax.swing.JPanel jPanel24;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel26;
    private javax.swing.JPanel jPanel27;
    private javax.swing.JPanel jPanel28;
    private javax.swing.JPanel jPanel29;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel30;
    private javax.swing.JPanel jPanel31;
    private javax.swing.JPanel jPanel32;
    private javax.swing.JPanel jPanel33;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator21;
    private javax.swing.JSeparator jSeparator22;
    private javax.swing.JSeparator jSeparator23;
    private javax.swing.JSeparator jSeparator28;
    private javax.swing.JSeparator jSeparator29;
    private javax.swing.JSeparator jSeparator30;
    private javax.swing.JSeparator jSeparator31;
    private javax.swing.JSeparator jSeparator32;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JSeparator jSeparator6;
    private javax.swing.JSeparator jSeparator7;
    private javax.swing.JSeparator jSeparator8;
    private javax.swing.JSeparator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTabbedPane jTabbedPane3;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JTabbedPane jTabbedPane5;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField13;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.ButtonGroup keySigBtnGroup;
    private javax.swing.JLabel keySignatureLabel;
    private javax.swing.JTextField keySignatureTF;
    private javax.swing.JPanel latencyTab;
    private javax.swing.JLabel layoutLabel;
    private javax.swing.JTextField layoutTF;
    private javax.swing.JToggleButton leadsheetBtn;
    private javax.swing.JPanel leadsheetPreferences;
    private javax.swing.JMenuItem leadsheetPrefsMI;
    private javax.swing.JPanel leadsheetSpecificPanel;
    private javax.swing.JLabel leadsheetTitleLabel;
    private javax.swing.JMenuItem lickGeneratorMI;
    private javax.swing.JRadioButton lickRadioButton;
    private javax.swing.JLabel lickTItleLabel;
    private javax.swing.JCheckBox licks;
    private javax.swing.JLabel lineLabel;
    private javax.swing.JLabel lineLabel1;
    private javax.swing.JMenuItem loadAdvMI;
    private javax.swing.JToggleButton loopButton;
    private javax.swing.JPanel loopPanel;
    private javax.swing.JTextField loopSet;
    private javax.swing.JTextField lowRangeTF;
    private javax.swing.JTextField lowRangeTF2;
    private javax.swing.JPanel masterVolumePanel;
    private javax.swing.JLabel measErrorLabel;
    private javax.swing.JCheckBoxMenuItem measureCstrLinesMI;
    private javax.swing.JLabel measureLabel;
    private javax.swing.JTextField measureTF;
    private javax.swing.JLabel measuresPerPartLabel;
    private javax.swing.JPanel melodyInstPanel;
    private javax.swing.JLabel melodyInsttLabel;
    private javax.swing.JCheckBox melodyMute;
    private javax.swing.JPanel melodyPanel;
    private javax.swing.JSlider melodyVolume;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JToggleButton midiBtn;
    private javax.swing.JPanel midiCalibrationPanel;
    private javax.swing.JComboBox midiInComboBox;
    private javax.swing.JPanel midiInPanel;
    private javax.swing.JLabel midiInStatus;
    private javax.swing.JLabel midiLabel;
    private javax.swing.JLabel midiLatencyLabel;
    private javax.swing.JPanel midiLatencyPanel;
    private javax.swing.JTextField midiLatencyTF;
    private javax.swing.JLabel midiLatencyUnitsLabel;
    private javax.swing.JComboBox midiOutComboBox;
    private javax.swing.JPanel midiOutPanel;
    private javax.swing.JLabel midiOutStatus;
    private javax.swing.JPanel midiPreferences;
    private javax.swing.JMenuItem midiPrefsMI;
    private javax.swing.JFrame midiStyleSpec;
    private javax.swing.JButton mixerBtn;
    private javax.swing.JDialog mixerDialog;
    private javax.swing.JButton newBtn;
    private javax.swing.JMenuItem newMI;
    private javax.swing.JButton newSectionButton;
    private javax.swing.JButton newVoicingButton;
    private javax.swing.JButton newVoicingCancelButton;
    private javax.swing.JLabel newVoicingChordLabel;
    private javax.swing.JTextField newVoicingChordTF;
    private javax.swing.JDialog newVoicingDialog;
    private javax.swing.JLabel newVoicingNameLabel;
    private javax.swing.JTextField newVoicingNameTF;
    private javax.swing.JButton newVoicingSaveButton;
    private javax.swing.JComboBox newVoicingTypeCB;
    private javax.swing.JLabel newVoicingTypeLabel;
    private javax.swing.JMenu notateGrammarMenu;
    private javax.swing.JLabel noteColoringLabel;
    private javax.swing.JButton okMeasBtn;
    private javax.swing.JButton okSaveButton;
    private javax.swing.JPanel okcancelPanel;
    private javax.swing.JPanel okcancelPanel1;
    private javax.swing.JPanel okcancelPanel2;
    private javax.swing.JPanel okcancelPanel3;
    private javax.swing.JMenuItem oneAutoMI;
    private javax.swing.JButton openBtn;
    private javax.swing.JButton openGeneratorButton;
    private javax.swing.JMenuItem openLeadsheetEditorMI;
    private javax.swing.JMenuItem openLeadsheetMI;
    private javax.swing.ButtonGroup otherColorBtnGrp;
    private javax.swing.JLabel otherLabel;
    protected javax.swing.JFrame overrideFrame;
    private javax.swing.JMenuItem overrideMeasPMI;
    private javax.swing.JButton overwriteLickButton;
    private javax.swing.JSpinner parallaxSpinner;
    private javax.swing.JPanel partBarsPanel;
    private javax.swing.JTextField partBarsTF1;
    private javax.swing.JLabel partComposerLabel;
    private javax.swing.JTextField partComposerTF;
    private javax.swing.JLabel partTitleLabel;
    private javax.swing.JTextField partTitleTF;
    private javax.swing.JButton pasteBothBtn;
    private javax.swing.JMenuItem pasteBothMI;
    private javax.swing.JMenuItem pasteBothPMI;
    private javax.swing.JMenuItem pasteChordsMI;
    private javax.swing.JMenuItem pasteMelodyMI;
    private javax.swing.JCheckBoxMenuItem pasteOverMI;
    private javax.swing.JToggleButton pauseBtn;
    private javax.swing.JMenuItem pausePlayMI;
    private javax.swing.JButton pianoKeyboardButton;
    private javax.swing.JMenuItem pianoKeyboardMI;
    private javax.swing.JMenuItem playAllMI;
    private javax.swing.JButton playBtn;
    private javax.swing.JMenu playMenu;
    private javax.swing.JToolBar playToolBar;
    private javax.swing.JButton playVoicingButton;
    private javax.swing.JPanel playbackPanel;
    private javax.swing.JSlider playbackSlider;
    private javax.swing.JLabel playbackTime;
    private javax.swing.JLabel playbackTotalTime;
    protected javax.swing.JPopupMenu popupMenu;
    private javax.swing.JTextField prefMeasTF;
    private javax.swing.JMenuItem preferencesAcceleratorMI;
    private javax.swing.JButton preferencesBtn;
    private javax.swing.JDialog preferencesDialog;
    private javax.swing.JMenu preferencesMenu;
    private javax.swing.JScrollPane preferencesScrollPane;
    private javax.swing.ButtonGroup prefsTabBtnGrp;
    private javax.swing.JButton printBtn;
    private javax.swing.JMenuItem printMI;
    private javax.swing.ButtonGroup productionBtnGrp;
    private javax.swing.JTextField programStatusTF;
    private javax.swing.JButton purgeCache;
    private javax.swing.JMenuItem quitMI;
    private javax.swing.JRadioButton quoteRadioButton;
    private javax.swing.JCheckBox quotes;
    private javax.swing.JLabel rangeToLabel;
    private javax.swing.JLabel rangeToLabel2;
    private javax.swing.JButton recordBtn;
    private javax.swing.JRadioButton redApproachBtn;
    private javax.swing.JRadioButton redChordBtn;
    private javax.swing.JRadioButton redColorBtn;
    private javax.swing.JLabel redLabel;
    private javax.swing.JRadioButton redOtherBtn;
    private javax.swing.JButton redoBtn;
    private javax.swing.JMenuItem redoMI;
    private javax.swing.JMenuItem redoPMI;
    private javax.swing.JButton reloadDevices;
    private javax.swing.JButton resetBtn;
    private javax.swing.JButton resetBtn1;
    private javax.swing.JButton resetBtn2;
    private javax.swing.JButton resetBtn3;
    private javax.swing.JMenuItem resolvePitches;
    private javax.swing.JMenuItem reverseMelody;
    private javax.swing.JMenuItem revertToSavedMI;
    private javax.swing.JCheckBox rootEqualBassCheckbox;
    private javax.swing.JLabel rootEqualBassLabel;
    private javax.swing.JLabel rootRangeLabel;
    private javax.swing.JMenuItem saveAdvice;
    private javax.swing.JMenuItem saveAsAdvice;
    private javax.swing.JMenuItem saveAsLeadsheetMI;
    private javax.swing.JButton saveBtn;
    private javax.swing.JButton saveDuplicate;
    private javax.swing.JMenuItem saveLeadsheetMI;
    private javax.swing.JFrame saveLickFrame;
    private javax.swing.JButton savePrefsBtn;
    private javax.swing.JButton savePrefsBtn1;
    private javax.swing.JButton savePrefsBtn2;
    private javax.swing.JButton savePrefsBtn3;
    private javax.swing.JMenuItem saveSelectionAsLick;
    private javax.swing.ButtonGroup saveTypeButtonGroup;
    private javax.swing.JCheckBox scaleTones;
    private javax.swing.JTabbedPane scoreTab;
    private javax.swing.JTextField scoreTitleTF;
    private javax.swing.JScrollPane scrollCells;
    private javax.swing.JScrollPane scrollIdioms;
    private javax.swing.JScrollPane scrollLicks;
    private javax.swing.JScrollPane scrollQuotes;
    private javax.swing.JScrollPane scrollScales;
    private javax.swing.JLabel sectionLabel;
    private javax.swing.JList sectionList;
    private javax.swing.JScrollPane sectionListScrollPane;
    private javax.swing.JLabel selectAStyleLabel;
    private javax.swing.JMenuItem selectAllMI;
    private javax.swing.JButton setMeasureButton;
    private javax.swing.JToggleButton showAdviceButton;
    private javax.swing.JCheckBoxMenuItem showEmptyTitlesMI;
    private javax.swing.JCheckBoxMenuItem showTitlesMI;
    private javax.swing.JToggleButton smartEntryButton;
    private javax.swing.JToolBar standardToolbar;
    private javax.swing.ButtonGroup staveButtonGroup;
    private javax.swing.JPanel staveButtonPanel;
    private javax.swing.ButtonGroup staveChoiceButtonGroup;
    private javax.swing.JToggleButton stepInputBtn;
    private javax.swing.JButton stopBtn;
    private javax.swing.JMenuItem stopPlayMI;
    private javax.swing.JToggleButton styleBtn;
    private javax.swing.JMenuItem styleGenerator1;
    private javax.swing.JList styleList;
    private javax.swing.JScrollPane styleListScrollPane;
    private javax.swing.JLabel stylePrefLabel;
    private javax.swing.JPanel stylePreferences;
    private javax.swing.JMenuItem stylePrefsMI;
    private javax.swing.JTabbedPane styleTabs;
    private javax.swing.JLabel swingLabel;
    private javax.swing.JTextField swingTF;
    private javax.swing.JLabel tempoLabel;
    private javax.swing.JPanel tempoPanel;
    private javax.swing.JTextField tempoSet;
    private javax.swing.JSlider tempoSlider;
    private javax.swing.JTextField tempoTF;
    private javax.swing.JTextField textEntry;
    private javax.swing.JLabel textEntryLabel;
    private javax.swing.JToolBar textEntryToolBar;
    private javax.swing.JTextField timeSignatureBottomTF;
    private javax.swing.JLabel timeSignatureLabel;
    private javax.swing.JPanel timeSignaturePanel;
    private javax.swing.JTextField timeSignatureTopTF;
    private javax.swing.JPanel toolbarPanel;
    private javax.swing.JCheckBox trackCheckBox;
    private javax.swing.JLabel trackerDelayLabel;
    private javax.swing.JPanel trackerDelayPanel;
    private javax.swing.JTextField trackerDelayTextField;
    private javax.swing.JTextField trackerDelayTextField2;
    private javax.swing.JMenuItem transposeBothDownSemitone;
    private javax.swing.JMenuItem transposeBothUpSemitone;
    private javax.swing.JMenuItem transposeChordsDownSemitone;
    private javax.swing.JMenuItem transposeChordsUpSemitone;
    private javax.swing.JMenuItem transposeMelodyDownHarmonically;
    private javax.swing.JMenuItem transposeMelodyDownOctave;
    private javax.swing.JMenuItem transposeMelodyDownSemitone;
    private javax.swing.JMenuItem transposeMelodyUpHarmonically;
    private javax.swing.JMenuItem transposeMelodyUpOctave;
    private javax.swing.JMenuItem transposeMelodyUpSemitone;
    private javax.swing.JMenu transposeMenu;
    private javax.swing.JSpinner transposeSpinner;
    private javax.swing.JRadioButton trebleStave;
    private javax.swing.JRadioButton trebleStaveBtn;
    private javax.swing.JDialog truncatePartDialog;
    private javax.swing.JLabel truncatePartLabel;
    private javax.swing.JButton undoBtn;
    private javax.swing.JMenuItem undoMI;
    private javax.swing.JMenuItem undoPMI;
    private javax.swing.JMenu utilitiesMenu;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JLabel visAdviceLabel;
    private javax.swing.JPanel visAdvicePanel;
    private javax.swing.JTextField voicing;
    private javax.swing.JButton voicingDeleteButton;
    private javax.swing.JLabel voicingEntryLabel;
    private javax.swing.JTextField voicingEntryTF;
    private javax.swing.JLabel voicingLabel;
    private javax.swing.JLabel voicingRangeLabel;
    private javax.swing.JScrollPane voicingScrollPane;
    private javax.swing.JButton voicingSequenceAddButton;
    private javax.swing.JLabel voicingSequenceDownArrow;
    private javax.swing.JLabel voicingSequenceLabel;
    private javax.swing.JList voicingSequenceList;
    private javax.swing.JPanel voicingSequencePanel;
    private javax.swing.JButton voicingSequencePlayButton;
    private javax.swing.JButton voicingSequenceRemoveButton;
    private javax.swing.JLabel voicingSequenceUpArrow;
    private javax.swing.JTable voicingTable;
    private javax.swing.JFrame voicingTestFrame;
    private javax.swing.JMenuItem voicingTestMI;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables
  
  public int getIgnoreDuplicateLick()
    {

    return ignoreDuplicateLick;

    }

  public JPanel getToolbarPanel()
    {

    return toolbarPanel;

    }

  public JTextField getTextEntry()
    {

    return textEntry;

    }

  public boolean getScaleTonesSelected()
    {

    return drawScaleTonesCheckBox.isSelected();

    }

  public boolean getChordTonesSelected()
    {

    return drawChordTonesCheckBox.isSelected();

    }

  public boolean getColorTonesSelected()
    {

    return drawColorTonesCheckBox.isSelected();

    }

  public String getGrammarFileName()
    {

    return grammarFile;

    }

  public void reloadGrammar()
    {

    lickgen.loadGrammar(grammarFile);

    }

  public void reloadGrammar2()
    {
    lickgen.loadGrammar(grammarFile);
    }

  void playbackGoToTab(int tab)
    {

    if( tab >= scoreTab.getTabCount() )
      {
      return;
      }



    currTabIndex = tab;

    scoreTab.setSelectedIndex(tab);

    staveRequestFocus();

    }

  public void checkFakeModalDialog()
    {

    Trace.log(2, "Notate: focusing fakeModalDialog");

    ((CapturingGlassPane)getRootPane().getGlassPane()).focus();

    }

  public void showFakeModalDialog(JDialog d)
    {

    Trace.log(2, "Notate: showFakeModalDialog() - " + d);

    CapturingGlassPane gp = (CapturingGlassPane)getRootPane().getGlassPane();



    gp.setFocusOn(d);

    d.setVisible(true);

    }

  public void hideFakeModalDialog(JDialog d)
    {

    Trace.log(2, "Notate: hideFakeModalDialog() - " + d);

    CapturingGlassPane gp = (CapturingGlassPane)getRootPane().getGlassPane();

    gp.setFocusOn(null);

    d.setVisible(false);

    }

  public static boolean bernoulli(double prob)
    {
    return Math.random() > (1. - prob);
    }

  private class CapturingGlassPane
          extends JComponent
          implements MouseListener, MouseMotionListener, FocusListener
    {
    JDialog focusItem;

    public CapturingGlassPane()
      {

      addMouseListener(this);

      addMouseMotionListener(this);

      addFocusListener(this);

      }

    public void setFocusOn(JDialog c)
      {

      focusItem = c;

      setVisible(c != null);

      }

    public void focus()
      {

      if( focusItem != null && focusItem.isVisible() )
        {
        focusItem.requestFocus();
        }
      else
        {
        setFocusOn(null);
        }

      }

    public void mouseClicked(MouseEvent e)
      {
      focus();
      }

    public void mousePressed(MouseEvent e)
      {
      focus();
      }

    public void mouseReleased(MouseEvent e)
      {
      focus();
      }

    public void mouseEntered(MouseEvent e)
      {
      }

    public void mouseExited(MouseEvent e)
      {
      }

    public void mouseDragged(MouseEvent e)
      {
      focus();
      }

    public void mouseMoved(MouseEvent e)
      {
      }

    public void focusGained(FocusEvent e)
      {
      focus();
      }

    public void focusLost(FocusEvent e)
      {
      }

    }

  public int getLoopCount()
    {
    if( toLoop )
      {
      updateLoopFromTextField();

      if( loopCount <= 0 )
        {
        return -1; // <= 0 means forever
        }
      else
        {
        return loopCount - 1; // let 1 mean only once, not twice, etc.
        }
      }
    else
      {
      return 0; // don't loop
      }
    }

  public boolean getToLoop()
    {
    return toLoop;
    }

  public void setLoopCount(int value)
    {
    loopCount = value;
    }

  public int getParallax()
    {
    return Integer.parseInt(parallaxSpinner.getValue().toString());
    }

  public boolean getColoration()
    {
    return noteColoration;
    }

  public boolean getSmartEntry()
    {
    return smartEntry;
    }
  
  private int getRecurrentIteration()
    {
    return recurrentIteration;
    }

  public StyleEditor getStyleEditor()
  {
      if( styleEditor == null )
      {
          styleEditor = new StyleEditor(this, cm);
      }

      return styleEditor;
  }

  public ChordPart getChordProg()
  {
      return score.getChordProg();
  }

  public void setLickTitle(String title)
  {
      enterLickTitle.setText(title);
  }

  public int getTotalSlots()
  {
      return totalSlots;
  }

  public int getSelectedIndex()
  {
      return scoreTab.getSelectedIndex();
  }

  public void redrawTriage()
  {
      lickgenFrame.redrawTriage();
  }

  public void openLog()
  {
      logDialog.setVisible(true);
  }

  public int getScoreLength()
  {
      return score.getLength();
  }


public void toGrammar()
  {
    String outFile = getGrammarFileName();

    File f = new File(outFile);

    String inFile = f.getParentFile().getPath() + File.separator + Directories.accumulatedProductions;

    File in = new File(inFile);

    if( !in.exists() )
      {
      setLickGenStatus("Can't do this step as " + inFile + " does not exist.");
      return;
      }

    //System.out.println("Writing productions to grammar file: " + outFile);
    setLickGenStatus("Writing productions to grammar file: " + outFile);
    CreateGrammar.create(chordProg,
                         inFile,
                         outFile,
                         lickgenFrame.getNumClusterReps(),
                         lickgenFrame.useMarkovSelected(),
                         lickgenFrame.getMarkovFieldLength(),
                         this);
    //System.out.println("Done writing productions to grammar file: " + outFile);
    setLickGenStatus("Done writing productions to grammar file: " + outFile);

    refreshGrammarEditor();
  }

}

