/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2015 Robert Keller and Harvey Mudd College XML export code
 * is also Copyright (C) 2009-2015 Nicolas Froment (aka Lasconic).
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
package imp.gui;

import imp.Constants;
import imp.Constants.ExtractMode;
import imp.Constants.StaveType;
import imp.ImproVisor;
import imp.RecentFiles;
import imp.audio.AudioSettings;
import imp.audio.PitchExtractor;
import imp.audio.SCDelayOffsetter;
import imp.audio.SCHandler;
import imp.brickdictionary.Block;
import imp.com.*;
import imp.data.*;
import imp.data.musicXML.ChordDescription;
import imp.lickgen.LickGen;
import imp.neuralnet.*;
import imp.roadmap.RoadMapFrame;
import imp.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.Sequencer;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import polya.Formatting;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.Tokenizer;

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
 * @author Aaron Wolin, Bob Keller
 *
 * Music XMLaspects contributed by Lasconic (Nicolas Froment) Aug. 15, 2009.
 *
 */
public class Notate
        extends javax.swing.JFrame
        implements Constants, MidiPlayListener
{
public GuideToneLineDialog guideToneLineDialog;
public static int midiImportXoffset = 200;
public static int midiImportYoffset = 200;
public static final int HELP_DIALOG_WIDTH = 980;
public static final int HELP_DIALOG_HEIGHT = 600;
private static final long serialVersionUID = 1L;
private int DEFAULT_SLIDER_VOLUME = 80;
RoadMapFrame roadmapFrame = null;
LickgenFrame lickgenFrame;
MidiImportFrame midiImportFrame = null;
MidiImport midiImport;
PitchExtractor extractor;
private int captureInterval;//# of slots per audio capture interval
//java.util.Timer captureTimer;
//CaptureTimerTask task;
//SuperCollider and Audio Input
private boolean superColliderMode;
private boolean audioLatencyRegistered = false;
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
 * the corresponding number of slots before the tracker starts moving. If it is
 * negative, the tracker will start with the corresponding number of slots from
 * the time playback is triggered.
 */
double trackerDelay = 0;
public static final int defaultBarsPerPart = 72;
public static final Dimension leadsheetEditorDimension =
        new Dimension(500, 600);
public static final Dimension lickGenFrameDimension =
        new Dimension(1100, 750);
public static final int defaultMeasPerLine = 4;
public static final int defaultMetre = 4;
public static final Dimension preferencesDialogDimension =
        new Dimension(800, 700);
private boolean noteColoration = true;
// Determines whether or not notes are rectified
// based on the current chord
private boolean smartEntry = true;
private int parallax = 0;
public static final char TRUE_CHECK_BOX = 'y';
public static final char FALSE_CHECK_BOX = 'n';
private static final int ALWAYS_USE_BASS_INDEX = 0;
private static final int ALWAYS_USE_CHORD_INDEX = 1;
private static final int ALWAYS_USE_MELODY_INDEX = 2;
private static final int ALWAYS_USE_STAVE_INDEX = 3;
public static final int MIN_TEMPO = 30;
public static final int MAX_TEMPO = 300;
private static int aboutDialogWidth = 600;
private static int aboutDialogHeight = 750;
private static int million = 1000000;
/**
 * Tell whether advice is initially open or not.
 */
public static boolean adviceInitiallyOpen = false;
/**
 * Used as a prefix on window titles
 */
public static String windowTitlePrefix = "Impro-Visor";
public static String windowTitlePrefixSeparator = ": ";
/**
 * Used as a prefix on leadsheet titles
 */
public static String leadsheetTitlePrefix = "Leadsheet: ";
/**
 * Standard file for vocabulary
 */
public String vocFile = "My.voc";
/**
 * file for musicXML chord description
 */
public String musicxmlFile = "chord_musicxml.xml";
/**
 * Standard file for leadsheet
 */
public String lsFile = "untitled";
/**q
 * Counter for untitled leadsheets
 */
private int lsCount = 1;
/**
 * Standard sub-directory for leadsheets and midi
 */
String leadsheetDirName = ImproVisor.getLeadsheetDirectory().toString(); //"leadsheets";
//File leadsheetDir;
String midiDir; // = "leadsheets" + File.separator + "midi";
/**
 * Standard extension vocabulary
 */
public String vocabExt = ".voc";
/**
 * Standard extension for leadsheets
 */
public String leadsheetExt = ".ls";
/**
 * Midi extension
 */
public String midiExt = ".mid";
/**
 * MusicXML extension
 */
public String musicxmlExt = ".xml";
/**
 * Default Filenames
 */
public String lsDef = "untitled.ls";
public String midDef = "untitled.mid";
public String musicxmlDef = "untitled.xml";
/**
 * The maximum number of measures per line. I'm not sure quite why, but things
 *
 * break if this is exceeded.
 */
public static final int maxMeasuresPerLine = 15;
/**
 * The default stave type.
 */
public StaveType DEFAULT_STAVE_TYPE = Preferences.getStaveTypeFromPreferences();
protected Polylist adviceList;
ArrayList<Object> adviceMenuItemsScales;
ArrayList<Object> adviceMenuItemsCells;
ArrayList<Object> adviceMenuItemsIdioms;
ArrayList<Object> adviceMenuItemsLicks;
ArrayList<Object> adviceMenuItemsQuotes;
ArrayList<Object> adviceMenuItemsBricks;
/**
 * The array of JScrollPanes that hold scoreBG panels, which hold Staves.
 */
protected StaveScrollPane[] staveScrollPane;
/**
 * beatValue is represents how many slots a beat takes up. To account for
 * different
 *
 * time signatures, it is scaled to (BEAT*4)/(timeSignatureBottom). (Thus, for
 * 4/4,
 *
 * beatValue is simply BEAT, and for 6/8, beatValue is BEAT/2.
 */
private int beatValue = BEAT;
/**
 * measureLength is the number of slots a measure takes up. It's basically
 * timeSignatureTop * beatValue.
 *
 * Thus, for 4/4, it's 4 * BEAT.
 */
private int measureLength = 4 * BEAT;
/**
 * The Score to be displayed
 */
protected Score score;
/**
 * An array of Parts in the Score to be displayed
 */
protected PartList partList;
/**
 * The chord progression of the Score
 */
protected ChordPart chordProg;
/**
 * The main Impro-Visor class. Needs to be passed for the clipboard
 *
 * variables.
 */
protected ImproVisor impro;
/**
 * Command manager for the Notate frame
 */
public CommandManager cm;
/**
 * Advisor for improvising
 */
private Advisor adv;
/**
 * Chord to insert from popup
 */
private String chordToInsert = null;
/**
 * Lick Generator
 */
private LickGen lickgen;
private ArrayList<String> melodyData = new ArrayList<String>();
/**
 * Theme Weaver
 */
private ThemeWeaver themeWeaver = null;
/**
 * this will be set to true during extraction of all measures in a corpus
 */
private boolean allMeasures = false;
/**
 * Default values pertinent to lick generation
 */
private double roundTo = BEAT;
private int paddingSlots = BEAT / 2;
private int minPitch = 60;
private int maxPitch = 82;
private int minInterval = 0;
private int maxInterval = 6;
private int minDuration = 8;
private int maxDuration = 8;
private double totalBeats = 8;
private int totalSlots;
private double restProb = 0.1;
private double leapProb = 0.2;
private boolean avoidRepeats = true;
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
private int stopPlaybackAtSlot = 16 * BEAT; // in case StyleEditor used first
private int numStavesPP = 0;
private static int QUANTUM = BEAT / 2;
private boolean useNoteCursor = false;
/**
 * Set this value if a roadmap created this frame.
 */
private RoadMapFrame createdByRoadmap = null;
private AudioSettings audioSettings;

synchronized public void setPlaybackStop(int slot, String message)
  {
    stopPlaybackAtSlot = slot;
    //System.out.println("setPlaybackStop to " + slot + " " + message);
  }

/**
 * The file chooser for opening and saving leadsheets.
 */
private JFileChooser openLSFC;
private JFileChooser saveLSFC;
private FileDialog saveAWT;
private JFileChooser revertLSFC;
private JFileChooser personalizeFC;
private LeadsheetPreview lsOpenPreview;
private LeadsheetPreview lsSavePreview;
/**
 * The file chooser for opening and saving vocabulary
 */
private JFileChooser vocfc;
/**
 * The file chooser for opening and saving midi files
 */
private JFileChooser midfc;
/**
 * The file chooser for opening and saving musicXML files
 */
private JFileChooser musicxmlfc;
/**
 * The file chooser for opening and saving the grammar
 */
private JFileChooser grammarfc;
/**
 * The width of the main frame
 */
public static final int NOTATE_WIDTH = 1130;
/**
 * Index of the current tab
 */
private int currTabIndex;
/**
 * The locked number of measures on each line
 */
protected int[] lockedMeasures = null;
/**
 * If the stave layout is locked or not
 */
private boolean autoAdjustStaves = false;
/**
 * If pasting should always overwrite notes
 */
private boolean alwaysPasteOver = true;
/**
 * Flag for whether the entry sound was muted before drawing mode.
 *
 * Remembered between mode toggles.
 */
private boolean preDrawingEntryMuted = false;
/**
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
 * and then immediately set back to false. Since events dispatched on the
 *
 * setValue call are not threaded, this is guaranteed to work and not
 *
 * interfere, even if this boolean is used for multiple jSliders. The
 *
 * possible exception is if one changeEvent listener fires another
 *
 * changeEvent listener, but this probably won't happen anyway.
 *
 * But it does happen with the addition of sliders in other windows, 9/24/11
 */
private boolean jSliderIgnoreStateChangedEvt = false;
private boolean toolbarVolumeSliderIgnoreStateChangedEvt = false;
private boolean mixerSliderIgnoreStateChangedEvt = false;
/**
 * Flag for if the user has just pasted. Needed to display blue/green
 *
 * construction lines
 */
protected boolean justPasted = false;
/**
 * The file of the leadsheet if it is saved.
 *
 * Null if it is not saved.
 */
private File savedLeadsheet;
/**
 * The file of the advice if it is saved.
 *
 * Null if it is not saved.
 */
private File savedVocab;
private File savedMidi;
private File savedMusicXML;
private String grammarFilename = null;
private String lickTitle = "unnamed";
/**
 * Midi Preferences reference to the midiManager and JComboBox models
 */
private MidiSynth midiSynth = null; // one midiSynth is created for each Notate instance for volume control and MIDI sequencing
private MidiSynth midiSynth2 = null;
private MidiSynth midiSynth3 = null;
private MidiManager midiManager = null; // reference to global midiManager contained in ImproVisor
private MidiDeviceChooser midiInChooser, midiOutChooser; // combo box models for device choosing in the midi preferences
private MidiRecorder midiRecorder = null; // action handler for recording from midi
private MidiStepEntryActionHandler midiStepInput = null; // action handler for step input from midi
private MidiStepEntryActionHandler voicingInput = null;
private boolean stepInputActive = false;
/**
 * Stores the index of stave tab where the playback indicator is currently
 *
 * located
 */
private int currentPlaybackTab = 0;
/**
 * Icons for the record button
 */
private ImageIcon recordImageIcon =
        new ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/record.gif"));
private ImageIcon recordActiveImageIcon =
        new ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/recordActive.gif"));
private StyleComboBoxModel defStyleComboBoxModel = new StyleComboBoxModel();
public StyleListModel styleListModel = new StyleListModel();
private RecentStyleListModel recentStyleListModel = new RecentStyleListModel();
private SectionTableModel sectionTableModel = new SectionTableModel(
        new Object[][]
  {
    {
        null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null
      },
    {
        null, null, null, null, null, null, null
      }
  },
        new String[]
  {
    "Phrase", "Start", "End", "Bars", "Style",
    /*"Tempo", "Time Sig.", "Key Sig.",*/
    "Options"
  });
private NWaySplitComboBoxModel nWaySplitComboBoxModel = new NWaySplitComboBoxModel();
private SectionInfo sectionInfo;
private VoicingTableModel voicingTableModel = new VoicingTableModel();
private DefaultListModel voicingSequenceListModel = new DefaultListModel();
private imp.gui.VoicingKeyboard keyboard = null;

public JTable getSectionTable(){
    return sectionTable;
}

public imp.gui.VoicingKeyboard getKeyboard(){
    return keyboard;
}

private imp.gui.StepEntryKeyboard stepKeyboard = null;
private static DefaultTableCellRenderer voicingRenderer = new DefaultTableCellRenderer()
{
@Override
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

    c.setEnabled((Boolean) table.getModel().getValueAt(row, 5));

    return c;
  }

};
/**
 * Various Instrument Chooser objects for the different preferences
 */
private InstrumentChooser melodyInst,
        auxInst,
        chordInst,
        bassInst,
        defMelodyInst,
        defAuxInst,
        defChordInst,
        defBassInst;
private SourceEditorDialog leadsheetEditor = null;
private SourceEditorDialog grammarEditor = null;
;

  private StyleEditor styleEditor = null;


/**
 * Various input mode names
 */
public enum Mode
{

NORMAL,
DRAWING,
RECORDING,
STEP_INPUT,
GENERATING,
GENERATED,
GENERATION_FAILED,
ROADMAP,
ROADMAP_DONE,
ADVICE,
LEADSHEET_SAVED,
STYLE_EDIT,
STYLE_SAVED,
EDIT_LEADSHEET,
PLAYING,
PLAYING_PAUSED,
IMPORTING_MIDI
}
/**
 * current mode, previous mode, play status
 */
private Mode mode = Mode.NORMAL;
private Mode previousMode = Mode.NORMAL;
private MidiPlayListener.Status playingStatus = MidiPlayListener.Status.STOPPED;
/**
 * latency measurement tool for the preferences dialog
 */
private MidiLatencyMeasurementTool midiLatencyMeasurement = new MidiLatencyMeasurementTool(this);

/**
 * Trading prefs
 */
TradingWindow trader;
TradingWindow2 trader2;

/**
 * If playback indicator goes off the screen, autoscroll to show it again
 *
 * if this value is true. This is always set to true when playback starts,
 *
 * but if the user scrolls it is temporarily set to false until the next
 *
 * playback.
 */
private boolean autoScrollOnPlayback;
private CriticDialog criticDialog;
/**
 * Handles slider events and playback time label updates
 */
PlaybackSliderManager playbackManager;
ActionListener repainter;
int recurrentIteration = 1;
/**
 * Use to decide whether to trigger scrolling early. Declare final, as it is
 * accessed from inner class
 */
final int earlyScrollMargin = 160;
private String improvMenuSelection;
// Initializes a network, specific to one leadsheet.
private Critic critic;

/**
 * Constructs a new Notate JFrame.
 *
 * Recursively calls Notate(Score, int, int) with a default blank Score and
 *
 * the (0,0) origin.
 */
public Notate()
  {
    this(new Score(), -1, -1);
  }

/**
 * Constructs a new Notate JFrame.
 *
 * Recursively calls Notate(Score, int, int) with a default blank part and
 *
 * the given x, y-coordinates.
 *
 *
 * @param x x-coordinate of the top left corner of the frame
 *
 * @param y y-coordinate of the top left corner of the frame
 */
public Notate(int x, int y)
  {
    this(new Score(), x, y);
  }

/**
 * Constructs a new Notate JFrame.
 *
 * Recursively calls Notate(Score, int, int) with a given part inputted and
 *
 * the (0,0) origin.
 *
 *
 * @param score the score to be displayed in the scoreFrame
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
 * Needed in order to call the constructor with advice
 *
 *
 * @param score the score to be displayed in the scoreFrame
 *
 * @param x x-coordinate of the top left corner of the frame
 *
 * @param y y-coordinate of the top left corner of the frame
 */
public Notate(Score score, int x, int y)
  {
    this(score, null, ImproVisor.getInstance(), x, y);
  }

/**
 * Constructs a new Notate JFrame.
 *
 * Sets the title to the score title, the starting location of the frame to
 *
 * the x, y-coordinates.
 *
 *
 * @param score the score to be displayed in the scoreFrame
 *
 * @param adv the advice directory to be used
 *
 * @param impro the main Impro-Visor program
 *
 * @param x x-coordinate of the top left corner of the frame
 *
 * @param y y-coordinate of the top left corner of the frame
 *
 *
 * @see #initComponents()
 *
 * @see #setupArrays()
 */
public Notate(Score score, Advisor adv, ImproVisor impro, int x, int y)
  {
    super();

    setTitle(score.getTitle());

    setTransposition(score.getTransposition());

    // all windows should be registered when created
    // so that the window menu can construct a list of windows

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

    setGrammarFilename(ImproVisor.getGrammarFile().getAbsolutePath());

    midiLatencyMeasurement = new MidiLatencyMeasurementTool(this);
    
    trader2 = new TradingWindow2(this);

    leadsheetEditor = new SourceEditorDialog(null, false, this, cm,
                                             SourceEditorDialog.LEADSHEET);

    leadsheetEditor.setRows(LEADSHEET_EDITOR_ROWS);

    grammarEditor = new SourceEditorDialog(null, false, this, cm,
                                           SourceEditorDialog.GRAMMAR);

    grammarEditor.setRows(GRAMMAR_EDITOR_ROWS);


    // MIDI Preferences Dialog init

    midiManager = imp.ImproVisor.getMidiManager();

    midiInChooser = new MidiDeviceChooser(midiManager, midiManager.getMidiInInfo());

    midiOutChooser = new MidiDeviceChooser(midiManager, midiManager.getMidiOutInfo());

    midiInChooser.setSelectedItem(midiManager.getInDeviceInfo());

    midiOutChooser.setSelectedItem(midiManager.getOutDeviceInfo());


    midiSynth = new MidiSynth(midiManager);

    midiSynth2 = new MidiSynth(midiManager);

    midiSynth3 = new MidiSynth(midiManager);

    autoImprovisation = new Trading(this);

    midiStepInput = new MidiStepEntryActionHandler(this);

    voicingInput = new MidiStepEntryActionHandler(this);

    setStepInput(false);

    criticDialog = new CriticDialog(lickgenFrame);


    // setup the file choosers' initial paths

    LeadsheetFileView lsfv = new LeadsheetFileView();

    lsOpenPreview = new LeadsheetPreview(openLSFC);

    lsOpenPreview.getCheckbox().setText("Open in new window");


    openLSFC.setCurrentDirectory(ImproVisor.getLeadsheetDirectory());

    openLSFC.setDialogType(JFileChooser.OPEN_DIALOG);

    openLSFC.setDialogTitle("Open Leadsheet");

    openLSFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

    openLSFC.resetChoosableFileFilters();

    openLSFC.addChoosableFileFilter(new LeadsheetFilter());

    openLSFC.setFileView(lsfv);

    openLSFC.setAccessory(lsOpenPreview);

    lsSavePreview = new LeadsheetPreview(saveLSFC);

    lsSavePreview.getCheckbox().setVisible(false);

    saveLSFC.setCurrentDirectory(ImproVisor.getLeadsheetDirectory());

    saveLSFC.setDialogType(JFileChooser.SAVE_DIALOG);

    saveLSFC.setDialogTitle("Save Leadsheet As");

    saveLSFC.setFileSelectionMode(JFileChooser.FILES_ONLY);

    saveLSFC.resetChoosableFileFilters();

    saveLSFC.addChoosableFileFilter(new LeadsheetFilter());

    saveLSFC.setSelectedFile(new File(lsDef));

    saveLSFC.setFileView(lsfv);

    saveLSFC.setAccessory(lsSavePreview);

    saveAWT.setDirectory(ImproVisor.getLeadsheetDirectory().getAbsolutePath());

    revertLSFC.setDialogType(JFileChooser.CUSTOM_DIALOG);

    revertLSFC.setDialogTitle("Revert without saving?");

    revertLSFC.setFileSelectionMode(JFileChooser.FILES_ONLY);


    // Set directories of file choosers

    vocfc.setCurrentDirectory(ImproVisor.getVocabDirectory());

    midfc.setCurrentDirectory(ImproVisor.getLeadsheetDirectory());

    midfc.setDialogType(JFileChooser.SAVE_DIALOG);

    midfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    midfc.resetChoosableFileFilters();

    midfc.addChoosableFileFilter(new MidiFilter());

    musicxmlfc.setCurrentDirectory(ImproVisor.getLeadsheetDirectory());

    musicxmlfc.setDialogType(JFileChooser.SAVE_DIALOG);

    musicxmlfc.setFileSelectionMode(JFileChooser.FILES_ONLY);

    musicxmlfc.resetChoosableFileFilters();

    musicxmlfc.addChoosableFileFilter(new MusicXMLFilter());


    grammarfc.setCurrentDirectory(ImproVisor.getVocabDirectory()); // original

    //attempted change grammarfc.setCurrentDirectory(grammarDir);

    // set the initial file to be null

    setSavedLeadsheet(null);

    savedVocab =
            new File(ImproVisor.getVocabDirectory(),
                     Preferences.getPreference(Preferences.DEFAULT_VOCAB_FILE));

    melodyInst = new InstrumentChooser();

    auxInst = new InstrumentChooser();

    chordInst = new InstrumentChooser();

    bassInst = new InstrumentChooser();

    defMelodyInst = new InstrumentChooser();

    defAuxInst = new InstrumentChooser();

    defChordInst = new InstrumentChooser();

    defBassInst = new InstrumentChooser();


    repainter = new PlayActionListener();


    lickgen = new LickGen(ImproVisor.getGrammarFile().getAbsolutePath(), this); //orig

    ChordDescription.load(ImproVisor.getVocabDirectory() + File.separator + musicxmlFile);


    initComponents();

    sectionTable.setModel(sectionTableModel);
    sectionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    sectionTable.addMouseListener(new MouseAdapter()
    {
    @Override
    public void mouseReleased(MouseEvent e)
      {
        if( e.getClickCount() >= 1 )
          {
            int row = sectionTable.rowAtPoint(e.getPoint());
            sectionTable.getSelectionModel().setSelectionInterval(row, row);
          }
      }

    });

    replaceWithPhi.setState(false);
    replaceWithDelta.setState(false);

    critic = new Critic();
    lickgenFrame = new LickgenFrame(this, lickgen, cm);

    populateTradingMenu();
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

    /* Why is this here? I don't know. It resets the section info of the score,
     * which seems dumb. I commented it out so that scores can retain their sections
     *
     * Answer: Without this, new leadsheets open with Style: unknown, which is not good.
     *
     * See Score.setStyle() for an attempted fix.
     */

    //score.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));

    sectionInfo = score.getChordProg().getSectionInfo().copy();

    showAdviceButton.setSelected(adviceInitiallyOpen);

    impro.setPlayEntrySounds(true);

    impro.setShowAdvice(adviceInitiallyOpen);

    getCurrentMelodyPart().setInstrument(
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
        if( ((KeyEvent) event).getKeyCode() == KeyEvent.VK_ESCAPE )
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

    setSliderVolumes(DEFAULT_SLIDER_VOLUME);

    midiRecordSnapSpinner.setValue(Preferences.getMidiRecordSnap());

    setNormalMode();

    setAutoImprovisation(false);

    try
      {
        improvMenuSelection =
                Preferences.getPreferenceQuietly(Preferences.IMPROV_MENU_SETTING);
      }
    catch( NonExistentParameterException e )
      {
        setUseImproviseCheckBox(); // DEFAULT
      }

    audioSettings = new AudioSettings(this);
    guideToneLineDialog = new GuideToneLineDialog(this, false);
    guideToneLineDialog.setVisible(false);
  } // end of Notate constructor

boolean showConstructionLinesAndBoxes = true;
boolean saveConstructionLineState;

private int setMenuSelection(JMenu jMenu, String string)
  {
    int n = jMenu.getItemCount();

    for( int i = 0; i < n; i++ )
      {
        JMenuItem item = jMenu.getItem(i);
        if( item != null )
          {
            item.setSelected(false);
          }
      }

    for( int i = 0; i < n; i++ )
      {
        JMenuItem item = jMenu.getItem(i);
        if( item != null && item.getText().equals(string) )
          {
            item.setSelected(true);
            return i;
          }
      }
    return -1;
  }

public boolean getShowConstructionLinesAndBoxes()
  {
    return !isPlaying() && showConstructionLinesAndBoxes;
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
    notateGrammarMenu.setText(getDefaultGrammarName());

    voicingTestFrame.pack();

    voicingTestFrame.setSize(875, 525);

    voicingTestFrame.setLocationRelativeTo(this);

    melodyInst.setDialog(preferencesDialog);

    auxInst.setDialog(preferencesDialog);

    chordInst.setDialog(preferencesDialog);

    bassInst.setDialog(preferencesDialog);

    defMelodyInst.setDialog(preferencesDialog);

    defAuxInst.setDialog(preferencesDialog);

    defChordInst.setDialog(preferencesDialog);

    defBassInst.setDialog(preferencesDialog);

    // The following determines how the preferences dialog will open
    // in terms of which tab is presented initially.

    changePrefTab(styleBtn, stylePreferences);

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

    playbackManager = new PlaybackSliderManager(midiSynth,
                                                playbackTime,
                                                playbackTotalTime,
                                                playbackSlider,
                                                repainter);
  }

public Advisor getAdvisor()
  {
    return adv;
  }

public MidiManager getMidiManager(){
    return this.midiManager;
}

public Score getScore()
  {
    return score;
  }

public StaveScrollPane[] getStaveScrollPane()
  {
    return staveScrollPane;
  }

public LickGen getLickGen()
  {
    return lickgen;
  }

/**
 * Returns if the staves continuously auto-adjust their layout
 *
 * @return boolean flag for if the staves continuously adjust
 *
 * their layout
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
private String nameOfOpenFile;

public void setFileName(String name)
  {
    nameOfOpenFile = name;
  }

@Override
public final void setTitle(String title)
  {
    String setTitle = windowTitlePrefix;
    boolean titleExists = !title.trim().equals("");
    boolean fileNameExists = nameOfOpenFile != null;
    setTitle += (titleExists || fileNameExists
            ? windowTitlePrefixSeparator
            + (titleExists ? title.trim() + " " : "")
            + (fileNameExists ? "(" + nameOfOpenFile + ")" : "")
            : "");
    super.setTitle(setTitle);

    if( score != null )
      {
        score.setTitle(title);
      }

    windowTitle = title;
  }

@Override
public String getTitle()
  {
    return windowTitle;
  }

/**
 * Returns whether paste should always overwrite notes
 *
 * @return boolean true if paste should always overwrite
 */
public boolean getAlwaysPasteOver()
  {
    return alwaysPasteOver;
  }

/**
 * Returns the currently selected tab
 *
 * @return int the currently selected tab
 */
public int getCurrTabIndex()
  {
    return currTabIndex;
  }

/**
 * Returns the Stave at the given tab index
 *
 * @param index the index of the StaveScrollPane
 *
 * @return Stave the Stave at the given index
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

public boolean stepInputSelected()
  {
    return stepInputBtn.isSelected();
  }

public LickgenFrame getLickgenFrame()
  {
    return lickgenFrame;
  }

public Critic getCritic()
  {
    return critic;
  }

/**
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
        stylePreferences = new javax.swing.JPanel();
        stylePrefLabel = new javax.swing.JLabel();
        styleTabs = new javax.swing.JTabbedPane();
        currentStyleTab = new javax.swing.JPanel();
        selectAStyleLabel = new javax.swing.JLabel();
        styleListScrollPane = new javax.swing.JScrollPane();
        styleList = new javax.swing.JList();
        newSectionButton = new javax.swing.JButton();
        sectionLabel = new javax.swing.JLabel();
        delSectionButton = new javax.swing.JButton();
        recentStyleLabel = new javax.swing.JLabel();
        recentStyleListScrollPane = new javax.swing.JScrollPane();
        recentStyleList = new javax.swing.JList();
        SectionTableScrollPane = new javax.swing.JScrollPane();
        sectionTable = new javax.swing.JTable();
        usePreviousStyleButton = new javax.swing.JButton();
        nWaySplitComboBox = new javax.swing.JComboBox();
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
        stavesPerPageLabel = new javax.swing.JLabel();
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
        numStavesPerPage = new javax.swing.JTextField();
        replaceWithDeltaCheckBox = new javax.swing.JCheckBox();
        replaceWithPhiCheckBox = new javax.swing.JCheckBox();
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
        midiRecordSnapSpinner = new javax.swing.JSpinner();
        sendSetBankCheckBox = new javax.swing.JCheckBox();
        jLabel1 = new javax.swing.JLabel();
        rangeFilterBtn = new javax.swing.JButton();
        clearRangeBtn = new javax.swing.JButton();
        latencyTab = new javax.swing.JPanel();
        midiLatencyPanel = new javax.swing.JPanel();
        midiLatencyLabel = new javax.swing.JLabel();
        midiLatencyTF = new javax.swing.JTextField();
        jSeparator3 = new javax.swing.JSeparator();
        midiLatencyUnitsLabel = new javax.swing.JLabel();
        midiCalibrationPanel = new MidiLatencyMeasurementTool(this);
        audioTab = new javax.swing.JPanel();
        audioInputLabel = new javax.swing.JLabel();
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(35, 35), new java.awt.Dimension(35, 35), new java.awt.Dimension(35, 35));
        audioInputLabel1 = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(35, 35), new java.awt.Dimension(35, 35), new java.awt.Dimension(35, 35));
        useSuperColliderCheckboxText = new javax.swing.JLabel();
        ReloadSuperColliderButton = new javax.swing.JButton();
        audioInputLabel2 = new javax.swing.JLabel();
        OpenHelpText = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(26, 26), new java.awt.Dimension(26, 26), new java.awt.Dimension(26, 26));
        jLabel4 = new javax.swing.JLabel();
        contourPreferences = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jTabbedPane3 = new javax.swing.JTabbedPane();
        generalContourTab = new javax.swing.JPanel();
        contToneChoices = new javax.swing.JPanel();
        drawScaleTonesCheckBox = new javax.swing.JCheckBox();
        drawChordTonesCheckBox = new javax.swing.JCheckBox();
        drawColorTonesCheckBox = new javax.swing.JCheckBox();
        staveButtonGroup = new javax.swing.ButtonGroup();
        popupMenu = new javax.swing.JPopupMenu();
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
        overrideMeasPMI = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        mergeSectionPreviousPMI = new javax.swing.JMenuItem();
        mergeSectionFollowingPMI = new javax.swing.JMenuItem();
        insertSectionBeforePMI = new javax.swing.JMenuItem();
        insertPhraseBeforePMI = new javax.swing.JMenuItem();
        insertSectionAfterPMI = new javax.swing.JMenuItem();
        insertPhraseAfterPMI = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JSeparator();
        advicePMI = new javax.swing.JMenuItem();
        advicePMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adviceMIActionPerformed(evt);
            }
        });
        jSeparator11 = new javax.swing.JPopupMenu.Separator();
        autoFillMI = new javax.swing.JCheckBoxMenuItem();
        adviceFrame = new javax.swing.JFrame();
        adviceTabbedPane = new javax.swing.JTabbedPane();
        scrollNotes = new javax.swing.JScrollPane();
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
        scrollBricks = new javax.swing.JScrollPane();
        adviceScrollListBricks = new javax.swing.JList();
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
        brickRadioButton = new javax.swing.JRadioButton();
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
        entryPanel = new javax.swing.JPanel();
        entryVolume = new javax.swing.JSlider();
        entryMute = new javax.swing.JCheckBox();
        jSeparator29 = new javax.swing.JSeparator();
        melodyPanel = new javax.swing.JPanel();
        melodyVolume = new javax.swing.JSlider();
        melodyMute = new javax.swing.JCheckBox();
        melodyChannelSpinner = new javax.swing.JSpinner();
        chordPanel = new javax.swing.JPanel();
        chordVolume = new javax.swing.JSlider();
        chordMute = new javax.swing.JCheckBox();
        chordChannelSpinner = new javax.swing.JSpinner();
        bassPanel = new javax.swing.JPanel();
        bassVolume = new javax.swing.JSlider();
        bassMute = new javax.swing.JCheckBox();
        bassChannelSpinner = new javax.swing.JSpinner();
        drumPanel = new javax.swing.JPanel();
        drumVolume = new javax.swing.JSlider();
        drumMute = new javax.swing.JCheckBox();
        drumChannelSpinner = new javax.swing.JSpinner();
        channelSelectLabel = new javax.swing.JLabel();
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
        fileStepDialog = new javax.swing.JDialog();
        stepBackButton = new javax.swing.JButton();
        stepForwardButton = new javax.swing.JButton();
        fileStepLabel = new javax.swing.JLabel();
        currDirectoryLabel = new javax.swing.JLabel();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jCheckBox1 = new javax.swing.JCheckBox();
        audioPreferencesOld = new javax.swing.JPanel();
        jTabbedPane7 = new javax.swing.JTabbedPane();
        audioInputTab1 = new javax.swing.JPanel();
        audioInputResolutionComboBox1 = new javax.swing.JComboBox();
        frameSizeComboBox1 = new javax.swing.JComboBox();
        pollRateComboBox1 = new javax.swing.JComboBox();
        playTripletsCheckBox1 = new javax.swing.JCheckBox();
        k_constantSlider1 = new javax.swing.JSlider();
        rmsThresholdSlider1 = new javax.swing.JSlider();
        confidenceThresholdSlider1 = new javax.swing.JSlider();
        minPitchSpinner1 = new javax.swing.JSpinner();
        maxPitchSpinner1 = new javax.swing.JSpinner();
        pitchRangePresetComboBox1 = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();
        tabPopupMenu = new javax.swing.JPopupMenu();
        addTabPopupMenuItem = new javax.swing.JMenuItem();
        delTabPopupMenuItem = new javax.swing.JMenuItem();
        renameTabPopupMenuItem = new javax.swing.JMenuItem();
        delTabCheckBox = new javax.swing.JCheckBox();
        noteLenDialog = new javax.swing.JDialog();
        noteLenPanel = new javax.swing.JPanel();
        noteLen32Btn = new javax.swing.JToggleButton();
        noteLen16Btn = new javax.swing.JToggleButton();
        noteLen8Btn = new javax.swing.JToggleButton();
        noteLen4Btn = new javax.swing.JToggleButton();
        noteLen2Btn = new javax.swing.JToggleButton();
        noteLen1Btn = new javax.swing.JToggleButton();
        noteLenModPanel = new javax.swing.JPanel();
        noteLenTripletCheckBox = new javax.swing.JCheckBox();
        noteLenDottedCheckBox = new javax.swing.JCheckBox();
        noteLenBtnGrp = new javax.swing.ButtonGroup();
        quantizePopupMenu = new javax.swing.JPopupMenu();
        toolbarPanel = new javax.swing.JPanel();
        standardToolbar = new javax.swing.JToolBar();
        newBtn = new javax.swing.JButton();
        newBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMIActionPerformed(evt);
            }
        });
        fileStepBackBtn = new javax.swing.JButton();
        fileStepForwardBtn = new javax.swing.JButton();
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
        noteCursorBtn = new javax.swing.JButton();
        showAdviceButton = new javax.swing.JToggleButton();
        improviseButton = new javax.swing.JToggleButton();
        useSubstitutorCheckBox = new javax.swing.JCheckBox();
        generationGapSpinner = new javax.swing.JSpinner();
        openGeneratorButton = new javax.swing.JButton();
        freezeLayoutButton = new javax.swing.JToggleButton();
        colorationButton = new javax.swing.JToggleButton();
        smartEntryButton = new javax.swing.JToggleButton();
        quantizeComboBox = new javax.swing.JComboBox();
        chordFontSizeSpinner = new javax.swing.JSpinner();
        addTabBtn = new javax.swing.JButton();
        addTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTabMIActionPerformed(evt);
            }
        });
        delTabBtn = new javax.swing.JButton();
        globalPreferencesBtn = new javax.swing.JButton();
        leadsheetPreferencesBtn = new javax.swing.JButton();
        chorusPreferencesBtn = new javax.swing.JButton();
        sectionPreferencesBtn = new javax.swing.JButton();
        midiPreferencesBtn = new javax.swing.JButton();
        contourPreferencesBtn = new javax.swing.JButton();
        playToolBar = new javax.swing.JToolBar();
        loopPanel = new javax.swing.JPanel();
        loopButton = new javax.swing.JToggleButton();
        loopSet = new javax.swing.JTextField();
        countInCheckBox = new javax.swing.JCheckBox();
        playBtn = new javax.swing.JButton();
        pauseBtn = new javax.swing.JToggleButton();
        stopBtn = new javax.swing.JButton();
        recordBtn = new javax.swing.JButton();
        stepInputBtn = new javax.swing.JToggleButton();
        chordStepBackButton = new javax.swing.JButton();
        chordReplayButton = new javax.swing.JButton();
        chordStepForwardButton = new javax.swing.JButton();
        playbackPanel = new javax.swing.JPanel();
        playbackTime = new javax.swing.JLabel();
        playbackTotalTime = new javax.swing.JLabel();
        playbackSlider = new javax.swing.JSlider();
        masterVolumePanel = new javax.swing.JPanel();
        allMuteToolBarBtn = new javax.swing.JToggleButton();
        allVolumeToolBarSlider = new javax.swing.JSlider();
        mixerBtn = new javax.swing.JButton();
        tempoPanel = new javax.swing.JPanel();
        tempoSet = new javax.swing.JTextField();
        tempoSlider = new javax.swing.JSlider();
        transposeSpinner = new javax.swing.JSpinner();
        partBarsPanel = new javax.swing.JPanel();
        partBarsTF1 = new javax.swing.JTextField();
        trackerDelayPanel = new javax.swing.JPanel();
        trackerDelayTextField2 = new javax.swing.JTextField();
        parallaxSpinner = new javax.swing.JSpinner();
        earlyScrollBtn = new javax.swing.JToggleButton();
        textEntryToolBar = new javax.swing.JToolBar();
        textEntryLabel = new javax.swing.JLabel();
        textEntry = new javax.swing.JTextField();
        clearButton = new javax.swing.JButton();
        stopBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayMIActionPerformed(evt);
            }
        });
        scoreTab = new javax.swing.JTabbedPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if(tabDragged){
                    int currentIndex = indexAtLocation(currentMouseLocation.x,currentMouseLocation.y);
                    if(currentIndex != -1){
                        Rectangle bounds = scoreTab.getBoundsAt(currentIndex);
                        Graphics2D g2d = (Graphics2D)g;
                        Stroke oldStroke = g2d.getStroke();
                        g2d.setStroke(new BasicStroke(3));
                        g2d.setColor(Color.GREEN);
                        g2d.drawRect(bounds.x,bounds.y,bounds.width,bounds.height);
                        g2d.setStroke(oldStroke);
                    }
                }
            }
        };
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        aboutMI = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JSeparator();
        newMI = new javax.swing.JMenuItem();
        openLeadsheetMI = new javax.swing.JMenuItem();
        openRecentLeadsheetMenu = new javax.swing.JMenu();
        mostRecentLeadsheetMI = new javax.swing.JMenuItem();
        openRecentLeadsheetNewWindowMenu = new javax.swing.JMenu();
        mostRecentLeadsheetNewWindowMI = new javax.swing.JMenuItem();
        fileStepMI = new javax.swing.JMenuItem();
        revertToSavedMI = new javax.swing.JMenuItem();
        clearHistoryMI = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JSeparator();
        saveLeadsheetMI = new javax.swing.JMenuItem();
        saveAsLeadsheetMI = new javax.swing.JMenuItem();
        exportAllToMidi = new javax.swing.JMenuItem();
        importMidiMI = new javax.swing.JMenuItem();
        exportChorusToMusicXML = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JSeparator();
        loadAdvMI = new javax.swing.JMenuItem();
        saveAdvice = new javax.swing.JMenuItem();
        saveAsAdvice = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JSeparator();
        printMI = new javax.swing.JMenuItem();
        printAllMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JSeparator();
        quitMI = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        selectAllMI = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JSeparator();
        undoMI = new javax.swing.JMenuItem();
        redoMI = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JSeparator();
        delAllMI = new javax.swing.JMenuItem();
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
        enterTextMI = new javax.swing.JMenuItem();
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
        insertChorusTabMI = new javax.swing.JMenuItem();
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
        phrasemarksMI = new javax.swing.JCheckBoxMenuItem();
        showBracketsCurrentMeasureMI = new javax.swing.JCheckBoxMenuItem();
        showBracketsAllMeasuresMI = new javax.swing.JCheckBoxMenuItem();
        showConstructionLinesMI = new javax.swing.JCheckBoxMenuItem();
        showNoteNameAboveCursorMI = new javax.swing.JCheckBoxMenuItem();
        useBeamsMI = new javax.swing.JCheckBoxMenuItem();
        replaceWithPhi = new javax.swing.JCheckBoxMenuItem();
        replaceWithDelta = new javax.swing.JCheckBoxMenuItem();
        playMenu = new javax.swing.JMenu();
        playSelectionMI = new javax.swing.JMenuItem();
        playSelectionToEndMI = new javax.swing.JMenuItem();
        playAllMI = new javax.swing.JMenuItem();
        stopPlayMI = new javax.swing.JMenuItem();
        pausePlayMI = new javax.swing.JMenuItem();
        recordMI = new javax.swing.JMenuItem();
        useAudioInputMI = new javax.swing.JCheckBoxMenuItem();
        utilitiesMenu = new javax.swing.JMenu();
        stepKeyboardMI = new javax.swing.JMenuItem();
        guideToneLine = new javax.swing.JMenuItem();
        openLeadsheetEditorMI = new javax.swing.JMenuItem();
        lickGeneratorMI = new javax.swing.JMenuItem();
        styleGenerator1 = new javax.swing.JMenuItem();
        soloGeneratorMI = new javax.swing.JMenuItem();
        voicingTestMI = new javax.swing.JMenuItem();
        pianoKeyboardMI = new javax.swing.JMenuItem();
        roadmapMenu = new javax.swing.JMenu();
        roadMapThisAnalyze = new javax.swing.JMenuItem();
        reAnalyzeMI = new javax.swing.JMenuItem();
        emptyRoadMapMI = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        createRoadMapCheckBox = new javax.swing.JCheckBoxMenuItem();
        tradingMenu = new javax.swing.JMenu();
        tradingWindow = new javax.swing.JMenuItem();
        tradingWindow2 = new javax.swing.JMenuItem();
        notateGrammarMenu = new javax.swing.JMenu();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();
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
        firstTimePrefsMI = new javax.swing.JMenuItem();
        jSeparator32 = new javax.swing.JSeparator();
        helpAboutMI = new javax.swing.JMenuItem();
        statusMenu = new javax.swing.JMenu();

        preferencesDialog.setTitle("Preferences and Settings");
        preferencesDialog.setAlwaysOnTop(true);
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
        buttonPanel.setNextFocusableComponent(styleBtn);
        buttonPanel.setPreferredSize(new java.awt.Dimension(112, 70));

        prefsTabBtnGrp.add(globalBtn);
        globalBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/global.png"))); // NOI18N
        globalBtn.setText("Global");
        globalBtn.setToolTipText("Global preferences");
        globalBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        globalBtn.setIconTextGap(6);
        globalBtn.setNextFocusableComponent(midiBtn);
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
        leadsheetBtn.setText("Leadsheet");
        leadsheetBtn.setToolTipText("Leadsheet preferences");
        leadsheetBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        leadsheetBtn.setNextFocusableComponent(globalBtn);
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
        chorusBtn.setToolTipText("Chorus preferences");
        chorusBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chorusBtn.setNextFocusableComponent(leadsheetBtn);
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
        styleBtn.setSelected(true);
        styleBtn.setText("Section & Style");
        styleBtn.setToolTipText("Section and style preferences");
        styleBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        styleBtn.setIconTextGap(0);
        styleBtn.setNextFocusableComponent(chorusBtn);
        styleBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        styleBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        styleBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(styleBtn);

        prefsTabBtnGrp.add(midiBtn);
        midiBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/largeMidi.png"))); // NOI18N
        midiBtn.setText("MIDI");
        midiBtn.setToolTipText("MIDI preferences");
        midiBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        midiBtn.setIconTextGap(0);
        midiBtn.setNextFocusableComponent(contourBtn);
        midiBtn.setPreferredSize(new java.awt.Dimension(100, 85));
        midiBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        midiBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiBtnActionPerformed(evt);
            }
        });
        buttonPanel.add(midiBtn);

        prefsTabBtnGrp.add(contourBtn);
        contourBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/largePencil.png"))); // NOI18N
        contourBtn.setText("Contour");
        contourBtn.setToolTipText("Drawing (\"contour\") tool preferences");
        contourBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        contourBtn.setIconTextGap(0);
        contourBtn.setNextFocusableComponent(styleBtn);
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
        savePrefsBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                savePrefsBtnActionPerformed(evt);
            }
        });
        okcancelPanel.add(savePrefsBtn);

        preferencesDialog.getContentPane().add(okcancelPanel, java.awt.BorderLayout.SOUTH);
        preferencesDialog.getContentPane().add(preferencesScrollPane, java.awt.BorderLayout.CENTER);

        stylePreferences.setBackground(new java.awt.Color(255, 255, 255));
        stylePreferences.setToolTipText("Style settings by section");
        stylePreferences.setLayout(new java.awt.GridBagLayout());

        stylePrefLabel.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        stylePrefLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/style.png"))); // NOI18N
        stylePrefLabel.setText("Section and Style Settings");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        stylePreferences.add(stylePrefLabel, gridBagConstraints);

        currentStyleTab.setMaximumSize(new java.awt.Dimension(400, 2147483647));
        currentStyleTab.setMinimumSize(new java.awt.Dimension(300, 374));
        currentStyleTab.setPreferredSize(new java.awt.Dimension(300, 376));
        currentStyleTab.setLayout(new java.awt.GridBagLayout());

        selectAStyleLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        selectAStyleLabel.setText("Style");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(selectAStyleLabel, gridBagConstraints);

        styleListScrollPane.setMinimumSize(new java.awt.Dimension(200, 130));
        styleListScrollPane.setName(""); // NOI18N
        styleListScrollPane.setPreferredSize(new java.awt.Dimension(200, 130));

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
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(styleListScrollPane, gridBagConstraints);

        newSectionButton.setText("Sub-Divide Section ");
        newSectionButton.setToolTipText("Sub-divides the currently-selected section into the specified number of segments, if possible, setting starting measures appropriately.");
        newSectionButton.setMaximumSize(new java.awt.Dimension(134, 40));
        newSectionButton.setMinimumSize(new java.awt.Dimension(134, 40));
        newSectionButton.setPreferredSize(new java.awt.Dimension(134, 40));
        newSectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newSectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        currentStyleTab.add(newSectionButton, gridBagConstraints);

        sectionLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        sectionLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        sectionLabel.setText("Sections");
        sectionLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(sectionLabel, gridBagConstraints);

        delSectionButton.setText("Delete Section");
        delSectionButton.setToolTipText("Deletes the selected section.");
        delSectionButton.setMaximumSize(new java.awt.Dimension(100, 40));
        delSectionButton.setMinimumSize(new java.awt.Dimension(100, 40));
        delSectionButton.setPreferredSize(new java.awt.Dimension(100, 40));
        delSectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delSectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        currentStyleTab.add(delSectionButton, gridBagConstraints);

        recentStyleLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        recentStyleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        recentStyleLabel.setText("Recent Styles");
        recentStyleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(recentStyleLabel, gridBagConstraints);

        recentStyleListScrollPane.setMinimumSize(new java.awt.Dimension(200, 130));
        recentStyleListScrollPane.setPreferredSize(new java.awt.Dimension(200, 130));

        recentStyleList.setModel(recentStyleListModel);
        recentStyleList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        recentStyleList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                recentStyleListValueChanged(evt);
            }
        });
        recentStyleListScrollPane.setViewportView(recentStyleList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        currentStyleTab.add(recentStyleListScrollPane, gridBagConstraints);

        SectionTableScrollPane.setAlignmentY(1.0F);

        sectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Phrase", "Start", "End", "Bars", "Style", "Custom Voicing Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        sectionTable.setAlignmentY(1.0F);
        sectionTable.setGridColor(new java.awt.Color(153, 153, 153));
        sectionTable.setName(""); // NOI18N
        sectionTable.setRowSelectionAllowed(false);
        SectionTableScrollPane.setViewportView(sectionTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 1;
        gridBagConstraints.weightx = 0.5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        currentStyleTab.add(SectionTableScrollPane, gridBagConstraints);

        usePreviousStyleButton.setText("Use Previous Style");
        usePreviousStyleButton.setMaximumSize(new java.awt.Dimension(158, 40));
        usePreviousStyleButton.setMinimumSize(new java.awt.Dimension(158, 40));
        usePreviousStyleButton.setPreferredSize(new java.awt.Dimension(158, 40));
        usePreviousStyleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usePreviousStyleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        currentStyleTab.add(usePreviousStyleButton, gridBagConstraints);

        nWaySplitComboBox.setMaximumRowCount(11);
        nWaySplitComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", " " }));
        nWaySplitComboBox.setToolTipText("Number of ways to sub-divide section.");
        nWaySplitComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Sub-divisions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11))); // NOI18N
        nWaySplitComboBox.setMaximumSize(new java.awt.Dimension(75, 50));
        nWaySplitComboBox.setMinimumSize(new java.awt.Dimension(75, 50));
        nWaySplitComboBox.setPreferredSize(new java.awt.Dimension(75, 50));
        nWaySplitComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nWaySplitComboBoxActionHandler(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.25;
        currentStyleTab.add(nWaySplitComboBox, gridBagConstraints);

        styleTabs.addTab("Styles by Section", currentStyleTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        stylePreferences.add(styleTabs, gridBagConstraints);

        chorusPreferences.setBackground(new java.awt.Color(255, 255, 255));
        chorusPreferences.setToolTipText("Chorus settings");
        chorusPreferences.setPreferredSize(new java.awt.Dimension(563, 507));
        chorusPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel19.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
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
        melodyInsttLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
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
        layoutTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layoutTFActionPerformed(evt);
            }
        });
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
        grandStaveBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grandStaveBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.weightx = 1.0;
        staveButtonPanel.add(grandStaveBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
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
        gridBagConstraints.weighty = 0.1;
        chorusSpecificPanel.add(melodyInstPanel, gridBagConstraints);

        jTabbedPane4.addTab("Chorus", chorusSpecificPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        chorusPreferences.add(jTabbedPane4, gridBagConstraints);

        leadsheetPreferences.setBackground(new java.awt.Color(255, 255, 255));
        leadsheetPreferences.setToolTipText("Leadsheet settings");
        leadsheetPreferences.setPreferredSize(new java.awt.Dimension(563, 507));
        leadsheetPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(breakpointTF, gridBagConstraints);

        scoreTitleTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        scoreTitleTF.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        scoreTitleTF.setNextFocusableComponent(composerField);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        leadsheetSpecificPanel.add(scoreTitleTF, gridBagConstraints);

        measuresPerPartLabel.setText("Measures per Chorus:");
        measuresPerPartLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(tempoTF, gridBagConstraints);

        tempoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        tempoLabel.setText("Tempo:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(tempoLabel, gridBagConstraints);

        composerLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        composerLabel.setText("Composer:");
        composerLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        composerLabel.setMaximumSize(new java.awt.Dimension(68, 28));
        composerLabel.setMinimumSize(new java.awt.Dimension(68, 28));
        composerLabel.setPreferredSize(new java.awt.Dimension(90, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(composerLabel, gridBagConstraints);

        leadsheetTitleLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        leadsheetTitleLabel.setText("Leadsheet Title:");
        leadsheetTitleLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        leadsheetTitleLabel.setMaximumSize(new java.awt.Dimension(99, 28));
        leadsheetTitleLabel.setMinimumSize(new java.awt.Dimension(99, 28));
        leadsheetTitleLabel.setPreferredSize(new java.awt.Dimension(99, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        leadsheetSpecificPanel.add(leadsheetTitleLabel, gridBagConstraints);

        composerField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        composerField.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        composerField.setNextFocusableComponent(timeSignatureTopTF);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(composerField, gridBagConstraints);

        chordIInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        chordIInstLabel.setText("Chord MIDI Instrument:");
        chordIInstLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(chordIInstLabel, gridBagConstraints);

        bassInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        bassInstLabel.setText("Bass MIDI Instrument:");
        bassInstLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(bassInstLabel, gridBagConstraints);

        keySignatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        keySignatureLabel.setText("Key Signature (+sharps, - flats):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(keySignatureTF, gridBagConstraints);

        commentsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        commentsLabel.setText("Comments:");
        commentsLabel.setMaximumSize(new java.awt.Dimension(72, 28));
        commentsLabel.setMinimumSize(new java.awt.Dimension(72, 28));
        commentsLabel.setPreferredSize(new java.awt.Dimension(72, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(commentsLabel, gridBagConstraints);

        commentsTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        commentsTF.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        commentsTF.setMinimumSize(new java.awt.Dimension(40, 28));
        commentsTF.setNextFocusableComponent(partTitleTF);
        commentsTF.setPreferredSize(new java.awt.Dimension(11, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(commentsTF, gridBagConstraints);

        timeSignatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        timeSignatureLabel.setText("Time Signature:");
        timeSignatureLabel.setAlignmentX(1.0F);
        timeSignatureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        timeSignatureLabel.setMaximumSize(new java.awt.Dimension(98, 28));
        timeSignatureLabel.setMinimumSize(new java.awt.Dimension(98, 28));
        timeSignatureLabel.setPreferredSize(new java.awt.Dimension(98, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        leadsheetSpecificPanel.add(timeSignatureLabel, gridBagConstraints);

        timeSignaturePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        timeSignatureTopTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        timeSignatureTopTF.setText("4");
        timeSignatureTopTF.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        timeSignatureTopTF.setNextFocusableComponent(timeSignatureBottomTF);
        timeSignatureTopTF.setPreferredSize(new java.awt.Dimension(50, 28));
        timeSignatureTopTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeSignatureTopTFActionPerformed(evt);
            }
        });
        timeSignaturePanel.add(timeSignatureTopTF);

        timeSignatureBottomTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        timeSignatureBottomTF.setText("4");
        timeSignatureBottomTF.setMaximumSize(new java.awt.Dimension(2147483647, 28));
        timeSignatureBottomTF.setNextFocusableComponent(keySignatureTF);
        timeSignatureBottomTF.setPreferredSize(new java.awt.Dimension(50, 28));
        timeSignaturePanel.add(timeSignatureBottomTF);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.1;
        leadsheetSpecificPanel.add(timeSignaturePanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        leadsheetSpecificPanel.add(chordInstPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 0.1;
        leadsheetSpecificPanel.add(bassInstPanel, gridBagConstraints);

        jTabbedPane5.addTab("Leadsheet", leadsheetSpecificPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        leadsheetPreferences.add(jTabbedPane5, gridBagConstraints);

        globalPreferences.setBackground(new java.awt.Color(255, 255, 255));
        globalPreferences.setToolTipText("Global settings");
        globalPreferences.setMinimumSize(new java.awt.Dimension(675, 600));
        globalPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel2.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
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

        jLabel11.setHorizontalAlignment(javax.swing.SwingConstants.TRAILING);
        jLabel11.setText("Default Volumes:");
        jLabel11.setFocusable(false);
        jLabel11.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        jLabel11.setMaximumSize(new java.awt.Dimension(30, 14));
        jLabel11.setMinimumSize(new java.awt.Dimension(30, 14));
        jLabel11.setPreferredSize(new java.awt.Dimension(30, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        defaultsTab.add(jLabel11, gridBagConstraints);

        stavesPerPageLabel.setText("Print Staves Per Page:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        defaultsTab.add(stavesPerPageLabel, gridBagConstraints);

        defAllPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "All", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        defAllPanel.setOpaque(false);
        defAllPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        defAllPanel.setLayout(new java.awt.GridBagLayout());

        defMasterVolSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        defEntryVolSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        defBassVolSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        defDrumVolSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        defChordVolSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        defMelodyVolSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 10;
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 10);
        defaultsTab.add(numStavesPerPage, gridBagConstraints);

        replaceWithDeltaCheckBox.setText("Use \u0394 for M7");
        replaceWithDeltaCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceWithDeltaCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 10;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        defaultsTab.add(replaceWithDeltaCheckBox, gridBagConstraints);

        replaceWithPhiCheckBox.setText("Use \u03D5 for m7b5");
        replaceWithPhiCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceWithPhiCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        defaultsTab.add(replaceWithPhiCheckBox, gridBagConstraints);

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

        trackCheckBox.setSelected(Preferences.getShowTrackingLine());
        trackCheckBox.setText("Show Tracking Line");
        trackCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showTrackingLineCheckBoxActionPerformed(evt);
            }
        });
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

        midiOutComboBox.setModel(midiOutChooser);
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

        midiInComboBox.setModel(midiInChooser);
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
        echoMidiCheckBox.setLabel("Echo MIDI input (send MIDI messages from MIDI input to MIDI output).");
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

        midiRecordSnapSpinner.setModel(new javax.swing.SpinnerListModel(new String[] {"2", "3", "4", "6", "8", "12", "24", "48", "60", "96", "120"}));
        midiRecordSnapSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "MIDI Record Beat Sub-Divisions", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        midiRecordSnapSpinner.setMinimumSize(new java.awt.Dimension(250, 56));
        midiRecordSnapSpinner.setPreferredSize(new java.awt.Dimension(250, 56));
        midiRecordSnapSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                midiRecordSnapChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        devicesTab.add(midiRecordSnapSpinner, gridBagConstraints);

        sendSetBankCheckBox.setSelected(Preferences.getMidiSendBankSelect());
        sendSetBankCheckBox.setLabel("Send SetBank-to-0 MIDI messages with each note.");
        sendSetBankCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendSetBankCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(10, 11, 10, 11);
        devicesTab.add(sendSetBankCheckBox, gridBagConstraints);

        jLabel1.setText("MIDI channel assignments are found in the Mixer panel.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 2;
        gridBagConstraints.ipady = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 10, 0);
        devicesTab.add(jLabel1, gridBagConstraints);

        rangeFilterBtn.setText("Midi Input Range");
        rangeFilterBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rangeFilterBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(0, 50, 0, 0);
        devicesTab.add(rangeFilterBtn, gridBagConstraints);

        clearRangeBtn.setText("Clear Range");
        clearRangeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearRangeBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_END;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 50);
        devicesTab.add(clearRangeBtn, gridBagConstraints);

        jTabbedPane2.addTab("MIDI Devices", devicesTab);

        latencyTab.setLayout(new java.awt.GridBagLayout());

        midiLatencyLabel.setText("MIDI Latency: ");
        midiLatencyPanel.add(midiLatencyLabel);

        midiLatencyTF.setText("" + Preferences.getMidiInLatency());
        midiLatencyTF.setPreferredSize(new java.awt.Dimension(65, 19));
        midiLatencyTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiLatencyTFactionPerformed(evt);
            }
        });
        midiLatencyPanel.add(midiLatencyTF);
        midiLatencyPanel.add(jSeparator3);

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

        audioTab.setLayout(new java.awt.GridBagLayout());

        audioInputLabel.setText("Audio Input is Provided by Running an Auxilliary ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        audioTab.add(audioInputLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        audioTab.add(filler3, gridBagConstraints);

        audioInputLabel1.setText("SuperCollider Program,  which is to be used as a MIDI ");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        audioTab.add(audioInputLabel1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        audioTab.add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        audioTab.add(useSuperColliderCheckboxText, gridBagConstraints);

        ReloadSuperColliderButton.setText("Open Pitch Tracker");
        ReloadSuperColliderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ReloadSuperColliderButtonActionPerformed(evt);
            }
        });
        audioTab.add(ReloadSuperColliderButton, new java.awt.GridBagConstraints());

        audioInputLabel2.setText("input device. This device must be selected in MIDI Preferences.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        audioTab.add(audioInputLabel2, gridBagConstraints);

        OpenHelpText.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        OpenHelpText.setForeground(new java.awt.Color(0, 51, 255));
        OpenHelpText.setText("Open Help Dialog");
        OpenHelpText.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                OpenHelpTextMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 14;
        audioTab.add(OpenHelpText, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        audioTab.add(filler2, gridBagConstraints);

        jTabbedPane2.addTab("Audio Input", audioTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        midiPreferences.add(jTabbedPane2, gridBagConstraints);

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/largeMidi.png"))); // NOI18N
        jLabel4.setText("  MIDI Settings");
        jLabel4.setToolTipText("Settings for MIDI input and output devices");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        midiPreferences.add(jLabel4, gridBagConstraints);

        contourPreferences.setBackground(new java.awt.Color(255, 255, 255));
        contourPreferences.setToolTipText("Settings for the drawing (\"contour\") tool\n");
        contourPreferences.setLayout(new java.awt.GridBagLayout());

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/largePencil.png"))); // NOI18N
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

        drawScaleTonesCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        drawScaleTonesCheckBox.setSelected(true);
        drawScaleTonesCheckBox.setText("Scale tones");
        drawScaleTonesCheckBox.setContentAreaFilled(false);
        drawScaleTonesCheckBox.setIconTextGap(10);

        if (Preferences.getPreference(Preferences.DRAWING_TONES).charAt(0) == 'x')       drawScaleTonesCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contToneChoices.add(drawScaleTonesCheckBox, gridBagConstraints);

        drawChordTonesCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        drawChordTonesCheckBox.setSelected(true);
        drawChordTonesCheckBox.setIconTextGap(10);
        drawChordTonesCheckBox.setLabel("Chord tones");

        if (Preferences.getPreference(Preferences.DRAWING_TONES).charAt(1) == 'x')       drawChordTonesCheckBox.setSelected(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contToneChoices.add(drawChordTonesCheckBox, gridBagConstraints);

        drawColorTonesCheckBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        drawColorTonesCheckBox.setText("Color tones");
        drawColorTonesCheckBox.setIconTextGap(10);
        if (Preferences.getPreference(Preferences.DRAWING_TONES).charAt(2) == '1')       drawColorTonesCheckBox.setSelected(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        contToneChoices.add(drawColorTonesCheckBox, gridBagConstraints);

        generalContourTab.add(contToneChoices, new java.awt.GridBagConstraints());

        jTabbedPane3.addTab("General", generalContourTab);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        contourPreferences.add(jTabbedPane3, gridBagConstraints);

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
        popupMenu.add(jSeparator12);

        mergeSectionPreviousPMI.setText("Merge Section with Previous");
        mergeSectionPreviousPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeSectionPreviousPMIActionPerformed(evt);
            }
        });
        popupMenu.add(mergeSectionPreviousPMI);

        mergeSectionFollowingPMI.setText("Merge Section with Following");
        mergeSectionFollowingPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mergeSectionFollowingPMIActionPerformed(evt);
            }
        });
        popupMenu.add(mergeSectionFollowingPMI);

        insertSectionBeforePMI.setText("Insert Section Before");
        insertSectionBeforePMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSectionBeforePMIActionPerformed(evt);
            }
        });
        popupMenu.add(insertSectionBeforePMI);
        insertSectionBeforePMI.getAccessibleContext().setAccessibleDescription("");

        insertPhraseBeforePMI.setText("Insert Phrase Before");
        insertPhraseBeforePMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertPhraseBeforePMIActionPerformed(evt);
            }
        });
        popupMenu.add(insertPhraseBeforePMI);

        insertSectionAfterPMI.setText("Insert Section After");
        insertSectionAfterPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertSectionAfterPMIActionPerformed(evt);
            }
        });
        popupMenu.add(insertSectionAfterPMI);

        insertPhraseAfterPMI.setText("Insert Phrase After");
        insertPhraseAfterPMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertPhraseAfterPMIActionPerformed(evt);
            }
        });
        popupMenu.add(insertPhraseAfterPMI);
        popupMenu.add(jSeparator23);

        advicePMI.setText("Advice");
        advicePMI.setEnabled(false);
        popupMenu.add(advicePMI);
        popupMenu.add(jSeparator11);

        autoFillMI.setSelected(true);
        autoFillMI.setText("Auto Fill Notes");
        autoFillMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoFillMIActionPerformed(evt);
            }
        });
        popupMenu.add(autoFillMI);

        adviceFrame.setTitle("Advice Directory");
        adviceFrame.setAlwaysOnTop(true);
        adviceFrame.setFocusCycleRoot(false);
        adviceFrame.setMinimumSize(new java.awt.Dimension(600, 400));
        adviceFrame.setName("adviceFrame"); // NOI18N
        adviceFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                adviceWindowClosing(evt);
            }
        });
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
        adviceFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        adviceTabbedPane.setMinimumSize(new java.awt.Dimension(500, 400));
        adviceTabbedPane.setPreferredSize(new java.awt.Dimension(500, 400));
        adviceTabbedPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceTabbedPaneMouseClicked(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                adviceTabbedPaneMouseReleased(evt);
            }
        });

        scrollNotes.setDoubleBuffered(true);
        scrollNotes.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollNotes.setPreferredSize(new java.awt.Dimension(300, 200));
        scrollNotes.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                adviceFocusGained(evt);
            }
        });

        adviceTree.setMaximumSize(new java.awt.Dimension(400, 800));
        adviceTree.setMinimumSize(new java.awt.Dimension(50, 50));
        adviceTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                adviceTreeMousePressed(evt);
            }
        });
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
        scrollNotes.setViewportView(adviceTree);

        adviceTabbedPane.addTab("Notes", scrollNotes);

        scrollScales.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollScales.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListScales.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListScales.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListScalesMouseClicked(evt);
            }
        });
        adviceScrollListScales.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListScalesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListScalesKeyReleased(evt);
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
        adviceScrollListCells.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListCellsMouseClicked(evt);
            }
        });
        adviceScrollListCells.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListCellsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListCellsKeyReleased(evt);
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
        adviceScrollListIdioms.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListIdiomsMouseClicked(evt);
            }
        });
        adviceScrollListIdioms.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListIdiomsKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListIdiomsKeyReleased(evt);
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
        adviceScrollListLicks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListLicksMouseClicked(evt);
            }
        });
        adviceScrollListLicks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListLicksKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListLicksKeyReleased(evt);
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
        adviceScrollListQuotes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListQuotesMouseClicked(evt);
            }
        });
        adviceScrollListQuotes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListQuotesKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListQuotesKeyReleased(evt);
            }
        });
        scrollQuotes.setViewportView(adviceScrollListQuotes);

        adviceTabbedPane.addTab("Quotes", scrollQuotes);

        scrollBricks.setMinimumSize(new java.awt.Dimension(100, 100));
        scrollBricks.setPreferredSize(new java.awt.Dimension(300, 200));

        adviceScrollListBricks.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        adviceScrollListBricks.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                adviceScrollListBricksMouseClicked(evt);
            }
        });
        adviceScrollListBricks.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adviceScrollListBricksKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                adviceScrollListBricksKeyReleased(evt);
            }
        });
        scrollBricks.setViewportView(adviceScrollListBricks);

        adviceTabbedPane.addTab("Bricks", scrollBricks);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        adviceFrame.getContentPane().add(adviceTabbedPane, gridBagConstraints);

        saveLickFrame.setAlwaysOnTop(true);
        saveLickFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        enterLickTitle.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        enterLickTitle.setToolTipText("The name to be given to the selection (need not be unique)");
        enterLickTitle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterLickTitleActionPerformed(evt);
            }
        });
        enterLickTitle.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                enterLickTitleGetsFocus(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 4);
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
        gridBagConstraints.gridwidth = 6;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        saveLickFrame.getContentPane().add(cancelLickTitle, gridBagConstraints);

        okSaveButton.setBackground(java.awt.Color.green);
        okSaveButton.setText("Save This");
        okSaveButton.setToolTipText("Saves the item in the vocabulary file.");
        okSaveButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        okSaveButton.setSelected(true);
        okSaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okSaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
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

        saveTypeButtonGroup.add(brickRadioButton);
        brickRadioButton.setText("Brick");
        brickRadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brickRadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        saveLickFrame.getContentPane().add(brickRadioButton, gridBagConstraints);

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
        mixerDialog.setMinimumSize(new java.awt.Dimension(521, 400));
        mixerDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        allPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "All", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        allPanel.setOpaque(false);
        allPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        allPanel.setLayout(new java.awt.GridBagLayout());

        allVolumeMixerSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        allVolumeMixerSlider.setMajorTickSpacing(20);
        allVolumeMixerSlider.setMaximum(127);
        allVolumeMixerSlider.setMinorTickSpacing(5);
        allVolumeMixerSlider.setOrientation(javax.swing.JSlider.VERTICAL);
        allVolumeMixerSlider.setPaintTicks(true);
        allVolumeMixerSlider.setValue(80);
        allVolumeMixerSlider.setMaximumSize(new java.awt.Dimension(38, 256));
        allVolumeMixerSlider.setMinimumSize(new java.awt.Dimension(38, 256));
        allVolumeMixerSlider.setPreferredSize(new java.awt.Dimension(51, 256));
        allVolumeMixerSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                allVolumeMixerSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        allPanel.add(allVolumeMixerSlider, gridBagConstraints);

        allMuteMixerBtn.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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

        entryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Entry", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        entryPanel.setOpaque(false);
        entryPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        entryPanel.setLayout(new java.awt.GridBagLayout());

        entryVolume.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        entryVolume.setMajorTickSpacing(20);
        entryVolume.setMaximum(127);
        entryVolume.setMinorTickSpacing(5);
        entryVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        entryVolume.setPaintTicks(true);
        entryVolume.setMaximumSize(new java.awt.Dimension(38, 150));
        entryVolume.setMinimumSize(new java.awt.Dimension(38, 150));
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

        entryMute.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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
        gridBagConstraints.gridx = 1;
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

        melodyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Melody", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        melodyPanel.setOpaque(false);
        melodyPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        melodyPanel.setLayout(new java.awt.GridBagLayout());

        melodyVolume.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        melodyMute.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(melodyPanel, gridBagConstraints);

        melodyChannelSpinner.setModel(new javax.swing.SpinnerNumberModel(7, 1, 16, 1));
        melodyChannelSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Channel", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        melodyChannelSpinner.setValue(Preferences.getMelodyChannel());
        melodyChannelSpinner.setVerifyInputWhenFocusTarget(false);
        melodyChannelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                melodyChannelSpinnerChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        mixerDialog.getContentPane().add(melodyChannelSpinner, gridBagConstraints);

        chordPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chords", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        chordPanel.setOpaque(false);
        chordPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        chordPanel.setLayout(new java.awt.GridBagLayout());

        chordVolume.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        chordMute.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(chordPanel, gridBagConstraints);

        chordChannelSpinner.setModel(new javax.swing.SpinnerNumberModel(7, 1, 16, 1));
        chordChannelSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Channel", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        chordChannelSpinner.setValue(Preferences.getChordChannel());
        chordChannelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chordChannelSpinnerChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        mixerDialog.getContentPane().add(chordChannelSpinner, gridBagConstraints);

        bassPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bass", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        bassPanel.setOpaque(false);
        bassPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        bassPanel.setLayout(new java.awt.GridBagLayout());

        bassVolume.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        bassVolume.setMajorTickSpacing(20);
        bassVolume.setMaximum(127);
        bassVolume.setMinorTickSpacing(5);
        bassVolume.setOrientation(javax.swing.JSlider.VERTICAL);
        bassVolume.setPaintTicks(true);
        bassVolume.setMaximumSize(new java.awt.Dimension(38, 150));
        bassVolume.setMinimumSize(new java.awt.Dimension(38, 150));
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

        bassMute.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(bassPanel, gridBagConstraints);

        bassChannelSpinner.setModel(new javax.swing.SpinnerNumberModel(7, 1, 16, 1));
        bassChannelSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Channel", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        bassChannelSpinner.setValue(Preferences.getBassChannel());
        bassChannelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                bassChannelSpinnerChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        mixerDialog.getContentPane().add(bassChannelSpinner, gridBagConstraints);

        drumPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Drums", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        drumPanel.setOpaque(false);
        drumPanel.setPreferredSize(new java.awt.Dimension(60, 180));
        drumPanel.setLayout(new java.awt.GridBagLayout());

        drumVolume.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
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

        drumMute.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
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
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        mixerDialog.getContentPane().add(drumPanel, gridBagConstraints);

        drumChannelSpinner.setModel(new javax.swing.SpinnerNumberModel(7, 1, 16, 1));
        drumChannelSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Channel", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 0, 10))); // NOI18N
        drumChannelSpinner.setValue(Preferences.getDrumChannel());
        drumChannelSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                drumChannelSpinnerChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        mixerDialog.getContentPane().add(drumChannelSpinner, gridBagConstraints);

        channelSelectLabel.setText("<html>\n<b>Caution:</b> Changing a<br>\nchannel setting<br>\nimmediately saves it<br>\nas a preference.\n</html>");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        mixerDialog.getContentPane().add(channelSelectLabel, gridBagConstraints);

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

        enterMeasures.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
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
        voicingTestFrame.setAlwaysOnTop(true);
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

        truncatePartLabel.setBackground(new java.awt.Color(255, 255, 102));
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

        fileStepDialog.setTitle("File Step");
        fileStepDialog.setAlwaysOnTop(true);
        fileStepDialog.setFocusCycleRoot(false);
        fileStepDialog.setMinimumSize(new java.awt.Dimension(300, 100));
        fileStepDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                fileStepDialogWindowClosing(evt);
            }
        });
        fileStepDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        stepBackButton.setText("Step Back");
        stepBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepBackButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        fileStepDialog.getContentPane().add(stepBackButton, gridBagConstraints);

        stepForwardButton.setText("Step Forward");
        stepForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepForwardButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        fileStepDialog.getContentPane().add(stepForwardButton, gridBagConstraints);

        fileStepLabel.setText("File Step");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        fileStepDialog.getContentPane().add(fileStepLabel, gridBagConstraints);

        currDirectoryLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        currDirectoryLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        fileStepDialog.getContentPane().add(currDirectoryLabel, gridBagConstraints);

        jMenuItem1.setText("jMenuItem1");

        jMenuItem2.setText("jMenuItem2");

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        jCheckBox1.setText("jCheckBox1");

        audioPreferencesOld.setBackground(new java.awt.Color(255, 255, 255));
        audioPreferencesOld.setToolTipText("settings for audio input");
        audioPreferencesOld.setAlignmentX(0.0F);
        audioPreferencesOld.setAlignmentY(0.0F);
        audioPreferencesOld.setPreferredSize(new java.awt.Dimension(390, 370));
        audioPreferencesOld.setLayout(new java.awt.GridBagLayout());

        audioInputTab1.setBackground(new java.awt.Color(225, 225, 225));
        audioInputTab1.setToolTipText("Sets the minimum MIDI pitch value recognized when capturing audio.");
        audioInputTab1.setLayout(new java.awt.GridBagLayout());

        audioInputResolutionComboBox1.setMaximumRowCount(16);
        audioInputResolutionComboBox1.setModel(AudioInputResolutionComboBoxModel.getAudioInputResolutionComboBoxModel());
        audioInputResolutionComboBox1.setSelectedIndex(4);
        audioInputResolutionComboBox1.setSelectedItem(AudioInputResolutionComboBoxModel.getSelection());
        audioInputResolutionComboBox1.setToolTipText("Sets the resolution with which the monophonic audio input is converted to Impro-Visor notes. Select the highest number of slots that gives satisfactory results. ");
        audioInputResolutionComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        audioInputResolutionComboBox1.setMinimumSize(new java.awt.Dimension(300, 50));
        audioInputResolutionComboBox1.setPreferredSize(new java.awt.Dimension(300, 50));
        audioInputResolutionComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                audioInputResolutionComboBox1Changed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        audioInputTab1.add(audioInputResolutionComboBox1, gridBagConstraints);

        frameSizeComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1024", "2048", "4096", "8192", " " }));
        frameSizeComboBox1.setSelectedIndex(1);
        frameSizeComboBox1.setToolTipText("Number of bytes in one audio sample");
        frameSizeComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Frame Size", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        frameSizeComboBox1.setMinimumSize(new java.awt.Dimension(88, 50));
        frameSizeComboBox1.setPreferredSize(new java.awt.Dimension(88, 50));
        frameSizeComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                frameSizeComboBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        audioInputTab1.add(frameSizeComboBox1, gridBagConstraints);

        pollRateComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "20", "40", " " }));
        pollRateComboBox1.setSelectedIndex(1);
        pollRateComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Poll Rate", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pollRateComboBox1.setMinimumSize(new java.awt.Dimension(72, 50));
        pollRateComboBox1.setPreferredSize(new java.awt.Dimension(72, 50));
        pollRateComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pollRateComboBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        audioInputTab1.add(pollRateComboBox1, gridBagConstraints);

        playTripletsCheckBox1.setText("Recognize Triplets");
        playTripletsCheckBox1.setToolTipText("If checked, triplets will be recognized. The smallest triplet subdivision allowed will correspond to the note resolution.");
        playTripletsCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playTripletsCheckBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        audioInputTab1.add(playTripletsCheckBox1, gridBagConstraints);

        k_constantSlider1.setMajorTickSpacing(10);
        k_constantSlider1.setMaximum(1000);
        k_constantSlider1.setMinimum(800);
        k_constantSlider1.setPaintLabels(true);
        k_constantSlider1.setPaintTicks(true);
        k_constantSlider1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Max-Finder Threshold (k_constant) x 1000", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        k_constantSlider1.setMaximumSize(new java.awt.Dimension(32767, 100));
        k_constantSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                k_constantSlider1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        audioInputTab1.add(k_constantSlider1, gridBagConstraints);

        rmsThresholdSlider1.setMajorTickSpacing(5);
        rmsThresholdSlider1.setMaximum(70);
        rmsThresholdSlider1.setMinimum(25);
        rmsThresholdSlider1.setPaintLabels(true);
        rmsThresholdSlider1.setPaintTicks(true);
        rmsThresholdSlider1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "RMS Threshold x 10", javax.swing.border.TitledBorder.LEFT, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        rmsThresholdSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                rmsThresholdSlider1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        audioInputTab1.add(rmsThresholdSlider1, gridBagConstraints);

        confidenceThresholdSlider1.setMajorTickSpacing(5);
        confidenceThresholdSlider1.setMaximum(65);
        confidenceThresholdSlider1.setMinimum(30);
        confidenceThresholdSlider1.setPaintLabels(true);
        confidenceThresholdSlider1.setPaintTicks(true);
        confidenceThresholdSlider1.setValue(45);
        confidenceThresholdSlider1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Confidence Threshold x 100", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        confidenceThresholdSlider1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                confidenceThresholdSlider1confidenceThresholdStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        audioInputTab1.add(confidenceThresholdSlider1, gridBagConstraints);

        minPitchSpinner1.setModel(new javax.swing.SpinnerNumberModel(45, 0, 100, 1));
        minPitchSpinner1.setToolTipText("Sets the minimum MIDI pitch value recognized when capturing audio. Pitches outside this range will be counted as rests.");
        minPitchSpinner1.setBorder(javax.swing.BorderFactory.createTitledBorder("Min. Pitch"));
        minPitchSpinner1.setMinimumSize(new java.awt.Dimension(84, 50));
        minPitchSpinner1.setName("Minimum MIDI Pitch"); // NOI18N
        minPitchSpinner1.setPreferredSize(new java.awt.Dimension(84, 50));
        minPitchSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                minPitchSpinner1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        audioInputTab1.add(minPitchSpinner1, gridBagConstraints);

        maxPitchSpinner1.setModel(new javax.swing.SpinnerNumberModel(110, 50, 127, 1));
        maxPitchSpinner1.setToolTipText("Sets the maximum MIDI pitch value recognized when capturing audio. Pitches outside this range will be counted as rests.");
        maxPitchSpinner1.setBorder(javax.swing.BorderFactory.createTitledBorder("Max. Pitch"));
        maxPitchSpinner1.setMinimumSize(new java.awt.Dimension(84, 50));
        maxPitchSpinner1.setName("Maximum MIDI Pitch"); // NOI18N
        maxPitchSpinner1.setPreferredSize(new java.awt.Dimension(84, 50));
        maxPitchSpinner1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                maxPitchSpinner1StateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        audioInputTab1.add(maxPitchSpinner1, gridBagConstraints);

        pitchRangePresetComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Default", "Soprano", "Alto", "Tenor", "Bass", "High Pass", "Low Pass", "Full Range" }));
        pitchRangePresetComboBox1.setToolTipText("Presets that specify the minimum and maximum MIDI pitch to be detected when capturing audio.");
        pitchRangePresetComboBox1.setBorder(javax.swing.BorderFactory.createTitledBorder("Pitch Range Presets"));
        pitchRangePresetComboBox1.setMinimumSize(new java.awt.Dimension(140, 50));
        pitchRangePresetComboBox1.setName("rangePresetComboBox"); // NOI18N
        pitchRangePresetComboBox1.setPreferredSize(new java.awt.Dimension(140, 50));
        pitchRangePresetComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pitchRangePresetComboBox1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 5, 10);
        audioInputTab1.add(pitchRangePresetComboBox1, gridBagConstraints);

        jTabbedPane7.addTab("Audio Input", audioInputTab1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        audioPreferencesOld.add(jTabbedPane7, gridBagConstraints);

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/largeMicrophone.png"))); // NOI18N
        jLabel6.setText("  Audio Settings");
        jLabel6.setToolTipText("Settings for MIDI input and output devices");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 0);
        audioPreferencesOld.add(jLabel6, gridBagConstraints);

        addTabPopupMenuItem.setText("Add Chorus");
        addTabPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTabPopupMenuItemActionPerformed(evt);
            }
        });
        tabPopupMenu.add(addTabPopupMenuItem);

        delTabPopupMenuItem.setText("Delete Chorus");
        delTabPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delTabPopupMenuItemActionPerformed(evt);
            }
        });
        tabPopupMenu.add(delTabPopupMenuItem);

        renameTabPopupMenuItem.setText("Rename Chorus");
        renameTabPopupMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameTabPopupMenuItemActionPerformed(evt);
            }
        });
        tabPopupMenu.add(renameTabPopupMenuItem);

        delTabCheckBox.setText("Do not show this message again.");
        delTabCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delTabCheckBoxActionPerformed(evt);
            }
        });

        noteLenDialog.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        noteLenDialog.setTitle("Select Note Length");
        noteLenDialog.setAlwaysOnTop(true);
        noteLenDialog.setMinimumSize(new java.awt.Dimension(370, 140));
        noteLenDialog.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                noteLenDialogWindowClosed(evt);
            }
        });
        noteLenDialog.getContentPane().setLayout(new javax.swing.BoxLayout(noteLenDialog.getContentPane(), javax.swing.BoxLayout.PAGE_AXIS));

        noteLenBtnGrp.add(noteLen32Btn);
        noteLen32Btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blacknotes/demisemiquaverUp.png"))); // NOI18N
        noteLen32Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLen32BtnActionPerformed(evt);
            }
        });
        noteLenPanel.add(noteLen32Btn);

        noteLenBtnGrp.add(noteLen16Btn);
        noteLen16Btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blacknotes/semiquaverUp.png"))); // NOI18N
        noteLen16Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLen16BtnActionPerformed(evt);
            }
        });
        noteLenPanel.add(noteLen16Btn);

        noteLenBtnGrp.add(noteLen8Btn);
        noteLen8Btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blacknotes/quaverUp.png"))); // NOI18N
        noteLen8Btn.setSelected(true);
        noteLen8Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLen8BtnActionPerformed(evt);
            }
        });
        noteLenPanel.add(noteLen8Btn);

        noteLenBtnGrp.add(noteLen4Btn);
        noteLen4Btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blacknotes/crotchetUp.png"))); // NOI18N
        noteLen4Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLen4BtnActionPerformed(evt);
            }
        });
        noteLenPanel.add(noteLen4Btn);

        noteLenBtnGrp.add(noteLen2Btn);
        noteLen2Btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blacknotes/minimUp.png"))); // NOI18N
        noteLen2Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLen2BtnActionPerformed(evt);
            }
        });
        noteLenPanel.add(noteLen2Btn);

        noteLenBtnGrp.add(noteLen1Btn);
        noteLen1Btn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/blacknotes/semibreve.png"))); // NOI18N
        noteLen1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLen1BtnActionPerformed(evt);
            }
        });
        noteLenPanel.add(noteLen1Btn);

        noteLenDialog.getContentPane().add(noteLenPanel);

        noteLenTripletCheckBox.setText("Triplet");
        noteLenTripletCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLenTripletCheckBoxActionPerformed(evt);
            }
        });
        noteLenModPanel.add(noteLenTripletCheckBox);

        noteLenDottedCheckBox.setText("Dotted");
        noteLenDottedCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteLenDottedCheckBoxActionPerformed(evt);
            }
        });
        noteLenModPanel.add(noteLenDottedCheckBox);

        noteLenDialog.getContentPane().add(noteLenModPanel);

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage((new ImageIcon(getClass().getResource("/imp/gui/graphics/icons/trumpetsmall.png"))).getImage());
        setMinimumSize(new java.awt.Dimension(800, 600));
        setName("notateFrame"); // NOI18N
        addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                formFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                formFocusLost(evt);
            }
        });
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
            public void windowClosed(java.awt.event.WindowEvent evt) {
                notateWIndowClosed(evt);
            }
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
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

        fileStepBackBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperBack.png"))); // NOI18N
        fileStepBackBtn.setToolTipText("Browse previous leadsheet file in the current folder. (Does nothing if this is the first leadsheet in the folder.)\n");
        fileStepBackBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepBackBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepBackBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(fileStepBackBtn);

        fileStepForwardBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperFront.png"))); // NOI18N
        fileStepForwardBtn.setToolTipText("Browse next leadsheet file in the current folder. (Does nothing if this is the last leadsheet in the folder.)\n\n");
        fileStepForwardBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepForwardBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepForwardBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(fileStepForwardBtn);

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

        noteCursorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/cursors/blueNoteLineCursor.png"))); // NOI18N
        noteCursorBtn.setToolTipText("Toggle note cursor.");
        noteCursorBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        noteCursorBtn.setFocusable(false);
        noteCursorBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        noteCursorBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        noteCursorBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        noteCursorBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        noteCursorBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        noteCursorBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteCursorBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(noteCursorBtn);

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

        improviseButton.setBackground(new java.awt.Color(0, 255, 0));
        improviseButton.setText("Improv");
        improviseButton.setToolTipText("Press to start improvisation.");
        improviseButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        improviseButton.setFocusable(false);
        improviseButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        improviseButton.setIconTextGap(0);
        improviseButton.setMaximumSize(new java.awt.Dimension(50, 30));
        improviseButton.setMinimumSize(new java.awt.Dimension(50, 30));
        improviseButton.setOpaque(true);
        improviseButton.setPreferredSize(new java.awt.Dimension(50, 30));
        improviseButton.setRequestFocusEnabled(false);
        improviseButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        improviseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                improviseButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(improviseButton);

        useSubstitutorCheckBox.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        useSubstitutorCheckBox.setText("xfm");
        useSubstitutorCheckBox.setToolTipText("Transform generated melody line when checked.");
        useSubstitutorCheckBox.setBorder(null);
        useSubstitutorCheckBox.setBorderPaintedFlat(true);
        useSubstitutorCheckBox.setFocusable(false);
        useSubstitutorCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        useSubstitutorCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        useSubstitutorCheckBox.setIconTextGap(0);
        useSubstitutorCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
        useSubstitutorCheckBox.setMaximumSize(new java.awt.Dimension(30, 28));
        useSubstitutorCheckBox.setMinimumSize(new java.awt.Dimension(30, 28));
        useSubstitutorCheckBox.setPreferredSize(new java.awt.Dimension(30, 28));
        useSubstitutorCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        standardToolbar.add(useSubstitutorCheckBox);

        generationGapSpinner.setModel(new javax.swing.SpinnerNumberModel(4.0d, -20.0d, 20.0d, 0.01d));
        generationGapSpinner.setToolTipText("Specifies the lead time, in beats, for generating next chorus before the end of the current chorus, if Recur is toggled on.");
        generationGapSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lead", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        generationGapSpinner.setInheritsPopupMenu(true);
        generationGapSpinner.setMaximumSize(new java.awt.Dimension(70, 45));
        generationGapSpinner.setMinimumSize(new java.awt.Dimension(70, 45));
        generationGapSpinner.setPreferredSize(new java.awt.Dimension(70, 45));
        generationGapSpinner.setValue(0.5);
        generationGapSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                generationLeadSpinnerChanged(evt);
            }
        });
        standardToolbar.add(generationGapSpinner);

        openGeneratorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/triage.gif"))); // NOI18N
        openGeneratorButton.setToolTipText("Open the Lick Generator dialog.");
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

        freezeLayoutButton.setBackground(new java.awt.Color(0, 255, 0));
        freezeLayoutButton.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        freezeLayoutButton.setText("<html><center>Freeze<br>Layout</center></html>");
        freezeLayoutButton.setToolTipText("Freeze or thaw the current layout");
        freezeLayoutButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        freezeLayoutButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        freezeLayoutButton.setMaximumSize(new java.awt.Dimension(45, 30));
        freezeLayoutButton.setMinimumSize(new java.awt.Dimension(45, 30));
        freezeLayoutButton.setOpaque(true);
        freezeLayoutButton.setPreferredSize(new java.awt.Dimension(45, 20));
        freezeLayoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                freezeLayoutButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(freezeLayoutButton);

        colorationButton.setBackground(new java.awt.Color(153, 204, 255));
        colorationButton.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        colorationButton.setText("<html><center>Black&<br>White</center></html>");
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
        smartEntryButton.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        smartEntryButton.setSelected(true);
        smartEntryButton.setText("<html><center>Simple<br>Entry</center></html>");
        smartEntryButton.setToolTipText("Use simple or harmonic note entry (the latter observing chords).");
        smartEntryButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        smartEntryButton.setFocusable(false);
        smartEntryButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        smartEntryButton.setMaximumSize(new java.awt.Dimension(45, 30));
        smartEntryButton.setMinimumSize(new java.awt.Dimension(45, 30));
        smartEntryButton.setOpaque(true);
        smartEntryButton.setPreferredSize(new java.awt.Dimension(45, 30));
        smartEntryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                smartEntryButtonActionPerformed(evt);
            }
        });
        standardToolbar.add(smartEntryButton);

        quantizeComboBox.setMaximumRowCount(24);
        quantizeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1/4", "1/3", "1/2", "3/4", "1", "3/2", "2", "3", "4", "6", "8", "12", "24", "60", "120" }));
        quantizeComboBox.setSelectedIndex(14);
        quantizeComboBox.setToolTipText("Quantize melody to specified number of subdivisions per beat (for MIDI input)."); // NOI18N
        quantizeComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Quantize to", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        quantizeComboBox.setMaximumSize(new java.awt.Dimension(90, 45));
        quantizeComboBox.setMinimumSize(new java.awt.Dimension(90, 45));
        quantizeComboBox.setPreferredSize(new java.awt.Dimension(90, 45));
        quantizeComboBox.setRequestFocusEnabled(false);
        quantizeComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                quantizeComboBoxscaleComboReleased(evt);
            }
        });
        quantizeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quantizeComboBoxscaleChosen(evt);
            }
        });
        standardToolbar.add(quantizeComboBox);

        chordFontSizeSpinner.setToolTipText("Specifies the chord font size.");
        chordFontSizeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chord Font", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11))); // NOI18N
        chordFontSizeSpinner.setInheritsPopupMenu(true);
        chordFontSizeSpinner.setMaximumSize(new java.awt.Dimension(80, 45));
        chordFontSizeSpinner.setMinimumSize(new java.awt.Dimension(80, 45));
        chordFontSizeSpinner.setPreferredSize(new java.awt.Dimension(80, 45));
        chordFontSizeSpinner.setValue(16);
        chordFontSizeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                chordFontStateChanged(evt);
            }
        });
        standardToolbar.add(chordFontSizeSpinner);

        addTabBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/addtab.gif"))); // NOI18N
        addTabBtn.setToolTipText("Add a new chorus tab, at the end.");
        addTabBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addTabBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        addTabBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        addTabBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        addTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTabBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(addTabBtn);

        delTabBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/deltab.gif"))); // NOI18N
        delTabBtn.setToolTipText("Delete the current  chorus tab (can't be undone). If there is only one chorus, you can't delete it.\n");
        delTabBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        delTabBtn.setEnabled(false);
        delTabBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        delTabBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        delTabBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        delTabBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delTabBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(delTabBtn);

        globalPreferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/smallGlobe.png"))); // NOI18N
        globalPreferencesBtn.setToolTipText("Open Global Preferences dialog.");
        globalPreferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        globalPreferencesBtn.setFocusable(false);
        globalPreferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        globalPreferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        globalPreferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        globalPreferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        globalPreferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        globalPreferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalPreferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(globalPreferencesBtn);

        leadsheetPreferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/leadsheet.png"))); // NOI18N
        leadsheetPreferencesBtn.setToolTipText("Open Leadsheet Preferences dialog.");
        leadsheetPreferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        leadsheetPreferencesBtn.setFocusable(false);
        leadsheetPreferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        leadsheetPreferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        leadsheetPreferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        leadsheetPreferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        leadsheetPreferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        leadsheetPreferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                leadsheetPreferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(leadsheetPreferencesBtn);

        chorusPreferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/trebleClef.png"))); // NOI18N
        chorusPreferencesBtn.setToolTipText("Open Chorus Preferences dialog.");
        chorusPreferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        chorusPreferencesBtn.setFocusable(false);
        chorusPreferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chorusPreferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        chorusPreferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        chorusPreferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        chorusPreferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chorusPreferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chorusPreferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(chorusPreferencesBtn);

        sectionPreferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/smallStyle.png"))); // NOI18N
        sectionPreferencesBtn.setToolTipText("Open Section and Style Preferences dialog.");
        sectionPreferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sectionPreferencesBtn.setFocusable(false);
        sectionPreferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        sectionPreferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        sectionPreferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        sectionPreferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        sectionPreferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        sectionPreferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sectionPreferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(sectionPreferencesBtn);

        midiPreferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/smallMidi.png"))); // NOI18N
        midiPreferencesBtn.setToolTipText("Open MIDI Preferences dialog.");
        midiPreferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        midiPreferencesBtn.setFocusable(false);
        midiPreferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        midiPreferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        midiPreferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        midiPreferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        midiPreferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        midiPreferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                midiPreferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(midiPreferencesBtn);

        contourPreferencesBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/preferences/pencilCursor.png"))); // NOI18N
        contourPreferencesBtn.setToolTipText("Open Contour Preferences dialog.");
        contourPreferencesBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        contourPreferencesBtn.setFocusable(false);
        contourPreferencesBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        contourPreferencesBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        contourPreferencesBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        contourPreferencesBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        contourPreferencesBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        contourPreferencesBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contourPreferencesBtnActionPerformed(evt);
            }
        });
        standardToolbar.add(contourPreferencesBtn);

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

        loopPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Looping", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        loopPanel.setMaximumSize(new java.awt.Dimension(90, 50));
        loopPanel.setMinimumSize(new java.awt.Dimension(90, 50));
        loopPanel.setOpaque(false);
        loopPanel.setPreferredSize(new java.awt.Dimension(90, 50));
        loopPanel.setLayout(new java.awt.GridBagLayout());

        loopButton.setBackground(new java.awt.Color(0, 255, 0));
        loopButton.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 10;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 1, 5);
        loopPanel.add(loopButton, gridBagConstraints);

        loopSet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        loopSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        loopSet.setText("0");
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

        countInCheckBox.setText("count");
        countInCheckBox.setToolTipText("Check to count in two measures before tune is played.");
        countInCheckBox.setFocusable(false);
        countInCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        countInCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        countInCheckBox.setIconTextGap(0);
        countInCheckBox.setMaximumSize(new java.awt.Dimension(50, 30));
        countInCheckBox.setMinimumSize(new java.awt.Dimension(50, 30));
        countInCheckBox.setPreferredSize(new java.awt.Dimension(50, 30));
        countInCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        countInCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                countInCheckBoxActionPerformed(evt);
            }
        });
        playToolBar.add(countInCheckBox);

        playBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
        playBtn.setToolTipText("Play the entire leadsheet, starting with the first chorus.\nTo play just the current chorus, select the first beat of that chorus and press Shift-Enter.");
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

        stepInputBtn.setBackground(new java.awt.Color(0, 255, 0));
        stepInputBtn.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        stepInputBtn.setText("<html><center>Step<br>Input</center></html>");
        stepInputBtn.setToolTipText("Step record from MIDI source. Each step uses the current slot showing and advances to the next. Use f and a keys to move forward and backward among slots, if desired, for making corrections.");
        stepInputBtn.setActionCommand("");
        stepInputBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stepInputBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        stepInputBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        stepInputBtn.setOpaque(true);
        stepInputBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        stepInputBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepInputBtnActionPerformed(evt);
            }
        });
        playToolBar.add(stepInputBtn);

        chordStepBackButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/playReversedGreen.gif"))); // NOI18N
        chordStepBackButton.setToolTipText("Move back to the previous chord (without playing).\n");
        chordStepBackButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        chordStepBackButton.setFocusable(false);
        chordStepBackButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordStepBackButton.setMaximumSize(new java.awt.Dimension(30, 30));
        chordStepBackButton.setMinimumSize(new java.awt.Dimension(30, 30));
        chordStepBackButton.setPreferredSize(new java.awt.Dimension(30, 30));
        chordStepBackButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chordStepBackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordStepBackButtonActionPerformed(evt);
            }
        });
        playToolBar.add(chordStepBackButton);

        chordReplayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/replayGreen.gif"))); // NOI18N
        chordReplayButton.setToolTipText("Replays chord.");
        chordReplayButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        chordReplayButton.setFocusable(false);
        chordReplayButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordReplayButton.setMaximumSize(new java.awt.Dimension(30, 30));
        chordReplayButton.setMinimumSize(new java.awt.Dimension(30, 30));
        chordReplayButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chordReplayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordReplayButtonActionPerformed(evt);
            }
        });
        playToolBar.add(chordReplayButton);

        chordStepForwardButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/playGreen.gif"))); // NOI18N
        chordStepForwardButton.setToolTipText("Move to, and play, the next chord.");
        chordStepForwardButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        chordStepForwardButton.setFocusable(false);
        chordStepForwardButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordStepForwardButton.setMaximumSize(new java.awt.Dimension(30, 30));
        chordStepForwardButton.setMinimumSize(new java.awt.Dimension(30, 30));
        chordStepForwardButton.setPreferredSize(new java.awt.Dimension(30, 30));
        chordStepForwardButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        chordStepForwardButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordStepForwardButtonActionPerformed(evt);
            }
        });
        playToolBar.add(chordStepForwardButton);

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

        masterVolumePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        masterVolumePanel.setMaximumSize(new java.awt.Dimension(130, 50));
        masterVolumePanel.setMinimumSize(new java.awt.Dimension(100, 50));
        masterVolumePanel.setOpaque(false);
        masterVolumePanel.setPreferredSize(new java.awt.Dimension(130, 50));
        masterVolumePanel.setLayout(new java.awt.GridBagLayout());

        allMuteToolBarBtn.setBackground(new java.awt.Color(0, 255, 0));
        allMuteToolBarBtn.setFont(new java.awt.Font("Arial", 0, 10)); // NOI18N
        allMuteToolBarBtn.setText("<html><center>Mute</center></html>");
        allMuteToolBarBtn.setToolTipText("Play or not play notes as they are inserted?");
        allMuteToolBarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        allMuteToolBarBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        allMuteToolBarBtn.setMaximumSize(new java.awt.Dimension(40, 20));
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
        allVolumeToolBarSlider.setToolTipText("Set the master volume.");
        allVolumeToolBarSlider.setValue(80);
        allVolumeToolBarSlider.setMaximumSize(new java.awt.Dimension(120, 20));
        allVolumeToolBarSlider.setMinimumSize(new java.awt.Dimension(80, 20));
        allVolumeToolBarSlider.setPreferredSize(new java.awt.Dimension(100, 20));
        allVolumeToolBarSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                allVolumeToolBarSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.6;
        masterVolumePanel.add(allVolumeToolBarSlider, gridBagConstraints);

        mixerBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/mixer.gif"))); // NOI18N
        mixerBtn.setToolTipText("Open Volume Mixer");
        mixerBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        mixerBtn.setFocusable(false);
        mixerBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        mixerBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        mixerBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        mixerBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        mixerBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        mixerBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mixerBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.2;
        masterVolumePanel.add(mixerBtn, gridBagConstraints);

        playToolBar.add(masterVolumePanel);

        tempoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tempo (Beats/Minute) ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        tempoPanel.setMaximumSize(new java.awt.Dimension(160, 50));
        tempoPanel.setMinimumSize(new java.awt.Dimension(120, 50));
        tempoPanel.setOpaque(false);
        tempoPanel.setPreferredSize(new java.awt.Dimension(130, 50));
        tempoPanel.setLayout(new java.awt.GridBagLayout());

        tempoSet.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        tempoSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        tempoSet.setToolTipText("Set the tempo for the sheet in beats per minute.");
        tempoSet.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        tempoSet.setMaximumSize(new java.awt.Dimension(40, 20));
        tempoSet.setMinimumSize(new java.awt.Dimension(30, 20));
        tempoSet.setPreferredSize(new java.awt.Dimension(30, 20));
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
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.insets = new java.awt.Insets(3, 5, 5, 0);
        tempoPanel.add(tempoSet, gridBagConstraints);

        tempoSlider.setMaximum(300);
        tempoSlider.setMinimum(30);
        tempoSlider.setMinorTickSpacing(4);
        tempoSlider.setValue(160);
        tempoSlider.setMaximumSize(new java.awt.Dimension(120, 30));
        tempoSlider.setMinimumSize(new java.awt.Dimension(80, 20));
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
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 6, 5);
        tempoPanel.add(tempoSlider, gridBagConstraints);

        playToolBar.add(tempoPanel);

        transposeSpinner.setToolTipText("Transposes the playback the specified number of half steps (e.g. use -2 for Bb instruments, +3 for Eb).");
        transposeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transpose", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11))); // NOI18N
        transposeSpinner.setMaximumSize(new java.awt.Dimension(75, 45));
        transposeSpinner.setMinimumSize(new java.awt.Dimension(75, 45));
        transposeSpinner.setPreferredSize(new java.awt.Dimension(75, 45));
        transposeSpinner.setValue(0);
        transposeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                transposeSpinnerStateChanged(evt);
            }
        });
        playToolBar.add(transposeSpinner);

        partBarsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bars", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        partBarsPanel.setToolTipText("Set the number of measures in one chorus.");
        partBarsPanel.setMaximumSize(new java.awt.Dimension(50, 50));
        partBarsPanel.setMinimumSize(new java.awt.Dimension(40, 50));
        partBarsPanel.setOpaque(false);
        partBarsPanel.setPreferredSize(new java.awt.Dimension(45, 50));
        partBarsPanel.setLayout(new java.awt.BorderLayout());

        partBarsTF1.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        partBarsTF1.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        partBarsTF1.setToolTipText("Set the number of bars in one chorus (the same for all choruses)");
        partBarsTF1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        partBarsTF1.setMaximumSize(new java.awt.Dimension(45, 15));
        partBarsTF1.setMinimumSize(new java.awt.Dimension(30, 15));
        partBarsTF1.setPreferredSize(new java.awt.Dimension(35, 15));
        partBarsTF1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                partBarsTF1MousePressed(evt);
            }
        });
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
        partBarsPanel.add(partBarsTF1, java.awt.BorderLayout.CENTER);

        playToolBar.add(partBarsPanel);

        trackerDelayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Delay", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
        trackerDelayPanel.setMaximumSize(new java.awt.Dimension(50, 50));
        trackerDelayPanel.setMinimumSize(new java.awt.Dimension(40, 50));
        trackerDelayPanel.setOpaque(false);
        trackerDelayPanel.setPreferredSize(new java.awt.Dimension(40, 50));
        trackerDelayPanel.setRequestFocusEnabled(false);
        trackerDelayPanel.setLayout(new java.awt.BorderLayout());

        trackerDelayTextField2.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        trackerDelayTextField2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        trackerDelayTextField2.setToolTipText("Set the delay between the tracker and playback.");
        trackerDelayTextField2.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        trackerDelayTextField2.setMaximumSize(new java.awt.Dimension(40, 20));
        trackerDelayTextField2.setMinimumSize(new java.awt.Dimension(30, 20));
        trackerDelayTextField2.setPreferredSize(new java.awt.Dimension(30, 20));
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

        parallaxSpinner.setToolTipText("Sets the vertical parallax for mouse clicks on staves.");
        parallaxSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Parallax", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 10))); // NOI18N
        parallaxSpinner.setMaximumSize(new java.awt.Dimension(60, 45));
        parallaxSpinner.setMinimumSize(new java.awt.Dimension(60, 45));
        parallaxSpinner.setPreferredSize(new java.awt.Dimension(60, 45));
        playToolBar.add(parallaxSpinner);

        earlyScrollBtn.setBackground(new java.awt.Color(51, 255, 255));
        earlyScrollBtn.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        earlyScrollBtn.setSelected(true);
        earlyScrollBtn.setText("<html>\n<center>\nEarly\n<br>\nScroll\n</center>\n</html>\n");
        earlyScrollBtn.setToolTipText("Causes the staff display to scroll to the top earlier.");
        earlyScrollBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        earlyScrollBtn.setFocusable(false);
        earlyScrollBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        earlyScrollBtn.setMaximumSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.setMinimumSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.setOpaque(true);
        earlyScrollBtn.setPreferredSize(new java.awt.Dimension(40, 35));
        earlyScrollBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                earlyScrollBtnActionPerformed(evt);
            }
        });
        playToolBar.add(earlyScrollBtn);

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

        textEntry.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        textEntry.setToolTipText("Enter chords or melody in leadsheet notation.");
        textEntry.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        textEntry.setNextFocusableComponent(scoreTab);
        textEntry.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                textEntryGainsFocus(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                textEntryLosesFocus(evt);
            }
        });
        textEntry.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                textEntryMouseClicked(evt);
            }
        });
        textEntry.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textEntryActionPerformed(evt);
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
        scoreTab.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        scoreTab.setOpaque(true);
        scoreTab.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                scoreTabStateChanged(evt);
            }
        });
        scoreTab.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                scoreTabMouseDragged(evt);
            }
        });
        scoreTab.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                scoreTabMouseClicked(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                mouseEnteredTabPanel(evt);
            }
            public void mousePressed(java.awt.event.MouseEvent evt) {
                scoreTabMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                scoreTabMouseReleased(evt);
            }
        });
        scoreTab.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                scoreTabKeyPressed(evt);
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

        openRecentLeadsheetMenu.setText("Open Recent Leadsheet (same window)");
        openRecentLeadsheetMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                populateRecentFileMenu(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        mostRecentLeadsheetMI.setText("Most Recent Leadsheet");
        mostRecentLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostRecentLeadsheetMIActionPerformed(evt);
            }
        });
        openRecentLeadsheetMenu.add(mostRecentLeadsheetMI);

        fileMenu.add(openRecentLeadsheetMenu);

        openRecentLeadsheetNewWindowMenu.setText("Open Recent Leadsheet (new window)");
        openRecentLeadsheetNewWindowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                populateRecentLeadsheetNewWindow(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        mostRecentLeadsheetNewWindowMI.setText("Most Recent Leadsheets(new window)");
        mostRecentLeadsheetNewWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostRecentLeadsheetNewWindowMIActionPerformed(evt);
            }
        });
        openRecentLeadsheetNewWindowMenu.add(mostRecentLeadsheetNewWindowMI);

        fileMenu.add(openRecentLeadsheetNewWindowMenu);

        fileStepMI.setText("File Stepper");
        fileStepMI.setToolTipText("Open separate window for file-stepping.\n");
        fileStepMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepMIActionPerformed(evt);
            }
        });
        fileMenu.add(fileStepMI);

        revertToSavedMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_R, java.awt.event.InputEvent.CTRL_MASK));
        revertToSavedMI.setText("Revert to Saved Leadsheet");
        revertToSavedMI.setToolTipText("Revert leadsheet to saved version, discarding any changes.");
        revertToSavedMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertLeadsheetActionPerformed(evt);
            }
        });
        fileMenu.add(revertToSavedMI);

        clearHistoryMI.setText("Clear Command History\n");
        clearHistoryMI.setToolTipText("Clears the history, so that previous commands are forgotten.\n");
        clearHistoryMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearHistoryMIrevertLeadsheetActionPerformed(evt);
            }
        });
        fileMenu.add(clearHistoryMI);
        fileMenu.add(jSeparator6);

        saveLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveLeadsheetMI.setText("Save");
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

        importMidiMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        importMidiMI.setText("Import MIDI Tracks from File");
        importMidiMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMidiMIActionPerformed(evt);
            }
        });
        fileMenu.add(importMidiMI);

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
        printMI.setText("Print the Current Chorus");
        printMI.setToolTipText("Print the current leadsheet.");
        printMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printMIActionPerformed(evt);
            }
        });
        fileMenu.add(printMI);

        printAllMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        printAllMI.setText("Print All Choruses");
        printAllMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printAllMIActionPerformed(evt);
            }
        });
        fileMenu.add(printAllMI);
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

        delAllMI.setText("Delete All Melodies");
        delAllMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delAllMIActionPerformed(evt);
            }
        });
        editMenu.add(delAllMI);

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

        enterTextMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_COMMA, 0));
        enterTextMI.setText("Enter Text");
        enterTextMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enterTextMIActionPerformed(evt);
            }
        });
        editMenu.add(enterTextMI);

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

        expandMelodyBy2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.SHIFT_MASK));
        expandMelodyBy2.setText("Expand melody by 2");
        expandMelodyBy2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandMelodyBy2ActionPerformed(evt);
            }
        });
        editMenu.add(expandMelodyBy2);

        expandMelodyBy3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.SHIFT_MASK));
        expandMelodyBy3.setText("Expand melody by 3");
        expandMelodyBy3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                expandMelodyBy3ActionPerformed1(evt);
            }
        });
        editMenu.add(expandMelodyBy3);

        contractMelodyBy2.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_2, java.awt.event.InputEvent.CTRL_MASK));
        contractMelodyBy2.setText("Contract melody by 2");
        contractMelodyBy2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                contractMelodyBy2ActionPerformed1(evt);
            }
        });
        editMenu.add(contractMelodyBy2);

        contractMelodyBy3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_3, java.awt.event.InputEvent.CTRL_MASK));
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

        insertChorusTabMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, java.awt.event.InputEvent.CTRL_MASK));
        insertChorusTabMI.setText("Insert New Chorus Tab");
        insertChorusTabMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                insertChorusTabMIActionPerformed(evt);
            }
        });
        editMenu.add(insertChorusTabMI);

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

        phrasemarksMI.setSelected(true);
        phrasemarksMI.setText("Show Phrase Marks");
        phrasemarksMI.setToolTipText("Check to show phrase marks on the leadsheet. Phrase marks are used in roadmap analysis.");
        phrasemarksMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phrasemarksMIActionPerformed(evt);
            }
        });
        viewMenu.add(phrasemarksMI);

        showBracketsCurrentMeasureMI.setSelected(true);
        showBracketsCurrentMeasureMI.setText("Show Brackets on Current Measure");
        showBracketsCurrentMeasureMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBracketsCurrentMeasureMIActionPerformed(evt);
            }
        });
        viewMenu.add(showBracketsCurrentMeasureMI);

        showBracketsAllMeasuresMI.setText("Show Brackets on All Measures");
        showBracketsAllMeasuresMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showBracketsAllMeasuresMIActionPerformed(evt);
            }
        });
        viewMenu.add(showBracketsAllMeasuresMI);

        showConstructionLinesMI.setSelected(true);
        showConstructionLinesMI.setText("Show Construction Lines and Boxes");
        showConstructionLinesMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showConstructionLinesMIActionPerformed(evt);
            }
        });
        viewMenu.add(showConstructionLinesMI);

        showNoteNameAboveCursorMI.setSelected(true);
        showNoteNameAboveCursorMI.setText("Show Note Names above Cursor");
        showNoteNameAboveCursorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showNoteNameAboveCursorMIActionPerformed(evt);
            }
        });
        viewMenu.add(showNoteNameAboveCursorMI);

        useBeamsMI.setSelected(true);
        useBeamsMI.setText("Use Beams");
        useBeamsMI.setToolTipText("Connect notes of same duration with beams where possible.");
        useBeamsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useBeamsMIActionPerformed(evt);
            }
        });
        viewMenu.add(useBeamsMI);

        replaceWithPhi.setSelected(true);
        replaceWithPhi.setText("Use \u03D5 for m7b5");
        replaceWithPhi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceWithPhiActionPerformed(evt);
            }
        });
        viewMenu.add(replaceWithPhi);

        replaceWithDelta.setSelected(true);
        replaceWithDelta.setText("Use \u0394 for M7");
        replaceWithDelta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                replaceWithDeltaActionPerformed(evt);
            }
        });
        viewMenu.add(replaceWithDelta);

        menuBar.add(viewMenu);

        playMenu.setMnemonic('p');
        playMenu.setText("Play");
        playMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMenuActionPerformed(evt);
            }
        });

        playSelectionMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        playSelectionMI.setText("Play Selection");
        playSelectionMI.setToolTipText("Play only the selection.");
        playSelectionMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playSelectionMIActionPerformed(evt);
            }
        });
        playMenu.add(playSelectionMI);

        playSelectionToEndMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_MASK));
        playSelectionToEndMI.setText("Play Selection to End");
        playSelectionToEndMI.setToolTipText("Play from the selection to the end of the chorus.");
        playSelectionToEndMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playSelectionToEndMIActionPerformed(evt);
            }
        });
        playMenu.add(playSelectionToEndMI);

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

        recordMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        recordMI.setText("Record");
        recordMI.setToolTipText("Record from a MIDI instrument.");
        recordMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recordMIActionPerformed(evt);
            }
        });
        playMenu.add(recordMI);

        useAudioInputMI.setSelected(false);
        useAudioInputMI.setText("Use Audio Input");
        playMenu.add(useAudioInputMI);

        menuBar.add(playMenu);

        utilitiesMenu.setMnemonic('U');
        utilitiesMenu.setText("Utilities");

        stepKeyboardMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.SHIFT_MASK | java.awt.event.InputEvent.CTRL_MASK));
        stepKeyboardMI.setText("Advising Keyboard");
        stepKeyboardMI.setToolTipText("Note entry keyboard that can advise on note choices");
        stepKeyboardMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stepKeyboardMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(stepKeyboardMI);

        guideToneLine.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, java.awt.event.InputEvent.SHIFT_MASK));
        guideToneLine.setText("Guide Tone Line");
        guideToneLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guideToneLineActionPerformed(evt);
            }
        });
        utilitiesMenu.add(guideToneLine);

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
        lickGeneratorMI.setToolTipText("Control panel for lick generation");
        lickGeneratorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lickGeneratorMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(lickGeneratorMI);

        styleGenerator1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        styleGenerator1.setMnemonic('S');
        styleGenerator1.setText("Style Editor & Extractor");
        styleGenerator1.setToolTipText("Editor for styles and extractor for styles from MIDI");
        styleGenerator1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleGenerator1ActionPerformed(evt);
            }
        });
        utilitiesMenu.add(styleGenerator1);

        soloGeneratorMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_MASK));
        soloGeneratorMI.setText("Theme Weaver");
        soloGeneratorMI.setToolTipText("Control panel for solo generation");
        soloGeneratorMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                soloGeneratorMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(soloGeneratorMI);

        voicingTestMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_I, java.awt.event.InputEvent.CTRL_MASK));
        voicingTestMI.setMnemonic('v');
        voicingTestMI.setText("Voicing Editor");
        voicingTestMI.setToolTipText("Editor for chord voicings");
        voicingTestMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                voicingTestMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(voicingTestMI);

        pianoKeyboardMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_K, java.awt.event.InputEvent.CTRL_MASK));
        pianoKeyboardMI.setMnemonic('K');
        pianoKeyboardMI.setText("Voicing Keyboard");
        pianoKeyboardMI.setToolTipText("Keyboard for viewing and creating voicings");
        pianoKeyboardMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pianoKeyboardMIActionPerformed(evt);
            }
        });
        utilitiesMenu.add(pianoKeyboardMI);

        menuBar.add(utilitiesMenu);

        roadmapMenu.setText("Roadmap\n");
        roadmapMenu.setToolTipText("Options for creating a roadmap of the chord progression.");

        roadMapThisAnalyze.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON, 0));
        roadMapThisAnalyze.setText("Show Roadmap");
        roadMapThisAnalyze.setToolTipText("Show Roadmap");
        roadMapThisAnalyze.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                roadMapThisAnalyzeAction(evt);
            }
        });
        roadmapMenu.add(roadMapThisAnalyze);

        reAnalyzeMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON, java.awt.event.InputEvent.SHIFT_MASK));
        reAnalyzeMI.setText("Re-Analyze Roadmap");
        reAnalyzeMI.setToolTipText("Perform a new roadmap analysis.");
        reAnalyzeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reAnalyzeMIAction(evt);
            }
        });
        roadmapMenu.add(reAnalyzeMI);

        emptyRoadMapMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_SEMICOLON, java.awt.event.InputEvent.CTRL_MASK));
        emptyRoadMapMI.setText("Open Empty Roadmap ");
        emptyRoadMapMI.setToolTipText("Opens a blank roadmap unrelated to this leadsheet");
        emptyRoadMapMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                EmptyRoadMapAction(evt);
            }
        });
        roadmapMenu.add(emptyRoadMapMI);
        roadmapMenu.add(jSeparator1);

        createRoadMapCheckBox.setText("Generate Roadmap on Opening Leadsheet");
        createRoadMapCheckBox.setToolTipText("Create roadmap of leadsheet if checked.");
        createRoadMapCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createRoadMapCheckBoxActionPerformed(evt);
            }
        });
        roadmapMenu.add(createRoadMapCheckBox);

        menuBar.add(roadmapMenu);

        tradingMenu.setText("Trading");
        tradingMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradingMenuActionPerformed(evt);
            }
        });

        tradingWindow.setText("Open Trading Window");
        tradingWindow.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradingWindowActionPerformed(evt);
            }
        });
        tradingMenu.add(tradingWindow);

        tradingWindow2.setText("Open Trading Window 2");
        tradingWindow2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradingWindow2ActionPerformed(evt);
            }
        });
        tradingMenu.add(tradingWindow2);

        menuBar.add(tradingMenu);

        notateGrammarMenu.setText(getDefaultGrammarName());
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
        notateGrammarMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                notateGrammarMenuMousePressed(evt);
            }
        });
        notateGrammarMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                notateGrammarMenuActionPerformed(evt);
            }
        });
        menuBar.add(notateGrammarMenu);

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

        firstTimePrefsMI.setText("Show First-Launch Message");
        firstTimePrefsMI.setToolTipText("Shows the message that appeared on the first launch of this version.");
        firstTimePrefsMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstTimePrefsMIActionPerformed(evt);
            }
        });
        helpMenu.add(firstTimePrefsMI);
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

        statusMenu.setText("Status:");
        menuBar.add(statusMenu);

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
      setMode(Mode.STYLE_EDIT);
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
      playCurrentSelection(false, getLoopCount(), PlayScoreCommand.USEDRUMS, "loopSetAction");
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

public void playCurrentSelection(boolean playToEndOfChorus, int loopCount, boolean useDrums, String message)
  {
    setMode(Mode.PLAYING);
    getCurrentStave().playSelection(playToEndOfChorus, loopCount, useDrums, "Notate playCurrentSelection: " + message);
  }

private void setToLoop()
  {
    toLoop = true;
    loopButton.setText("<html><center>Straight</center></html>");
    loopButton.setBackground(Color.RED);
  }

private void setToNotLoop()
  {
    toLoop = false;
    stopPlaying("set not to Loop");
    loopButton.setText("<html><center>Loop</center></html>");
    loopButton.setBackground(Color.GREEN);
  }

public void showCritic()
  {
    criticDialog.setVisible(true);
  }

public void reloadStyles()
  {
    reCaptureCurrentStyle();

    styleListModel.reset();

    sectionTableModel.tableRefresh();
  }

public void setTableColumnWidths()
  {
    for( int j = 0; j < sectionTableModel.getColumnCount(); j++ )
      {
        sectionTable.getColumnModel().getColumn(j).
                setPreferredWidth(sectionTableModel.getColumnWidths(j));
      }

    DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

    for( int j = 1; j < sectionTableModel.getColumnCount(); j++ )
      {
        renderer.setHorizontalAlignment(sectionTableModel.getColumnAdjustments(j));
        sectionTable.getColumnModel().getColumn(j).setCellRenderer(renderer);
      }
    //sectionTable.getColumnModel().getColumn(0).setCellRenderer( sectionCellRenderer );
  }

public void updatePhiAndDelta(boolean phi, boolean delta)
  {
    setPhiStatus(phi);
    setDeltaStatus(delta);
    replaceWithPhiCheckBox.setSelected(phi);
    replaceWithDeltaCheckBox.setSelected(delta);
    getCurrentStave().setPhi(phi);
    getCurrentStave().setDelta(delta);
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

private void setSectionParameters()
  {
    setSectionPrefs();

    sectionTableModel.tableRefresh();
  }

/**
 * Split or sub-divide section
 * @param evt 
 */
    private void newSectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newSectionButtonActionPerformed
        int index = sectionTable.getSelectedRow();
        if( index < 0 || index >= sectionInfo.size() )
          {
            return;
          }
        
        // +2 because elements are 2, 3, ...., 12
        
        int split = nWaySplitComboBox.getSelectedIndex() + 2;

        if( !sectionInfo.nWaySplit(index, split) )
          {
            // Not possible to split into the indicated number
            return;
          }

        for( int j = 0; j < split; j++ )
          {
            sectionTableModel.addARow();
          }

        sectionTableModel.tableRefresh();
        sectionTable.getSelectionModel().setSelectionInterval(index, index);
    }//GEN-LAST:event_newSectionButtonActionPerformed

    private void delSectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delSectionButtonActionPerformed
        int index = sectionTable.getSelectedRow();
        if( index < 0 )
          {
            return;
          }
        sectionInfo.deleteSection(index);
        sectionTableModel.tableRefresh();
        if( index == sectionInfo.size() )
          {
            index -= 1;
            sectionTable.getSelectionModel().setSelectionInterval(index, index);
          }
        else
          {
            sectionTable.getSelectionModel().setSelectionInterval(index, index);
          }
    }//GEN-LAST:event_delSectionButtonActionPerformed

public void toCritic()
  {
    if( lickgenFrame.toCriticSelected() )
      {
        String s = JOptionPane.showInputDialog("Select the number of measures\n"
                + "for the graded licks", 2);

        if( s != null && s.length() > 0 )
          {
            int measureNum;

            try
              {
                measureNum = Integer.parseInt(s);
              }
            catch( Exception e )
              {
                measureNum = 2;
              }

            lickgenFrame.showCriticGrades();

            getCurrentStave().lockSelectionWidth(measureNum * WHOLE);

            getCurrentStave().repaint();
          }
        else
          {
            lickgenFrame.setToCriticDialog(false);
          }
      }
    else
      {
        getCurrentStave().unlockSelectionWidth();

        lickgenFrame.showAllGrades();
      }
  }

private void setStepInputBtn(boolean selected)
  {
    if( selected )
      {
        stepInputBtn.setText("<html><center>Stop</center></html>");

        stepInputBtn.setBackground(Color.RED);
        stopRecording();
        setMode(Mode.STEP_INPUT);
      }
    else
      {
        stepInputBtn.setText("<html><center>Step<br>Input</center></html>");

        stepInputBtn.setBackground(Color.GREEN);
        setNormalStatus();
      }

    setStepInput(selected);
    staveRequestFocus();
  }

    private void stepInputBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepInputBtnActionPerformed

        setStepInputBtn(stepInputBtn.isSelected());
    }//GEN-LAST:event_stepInputBtnActionPerformed

/**
 * This if for use by other features that need to turn step input off.
 */
private void turnStepInputOff()
  {
    stepInputBtn.setText("<html><center>Step<br>Input</center></html>");
    stepInputBtn.setBackground(Color.GREEN);
    setStepInput(false);
    stepInputBtn.setSelected(false);
  }

    private void addRestMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRestMIActionPerformed

        addRest();

    }//GEN-LAST:event_addRestMIActionPerformed

    private void styleListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_styleListValueChanged
        Style style = (Style) styleList.getSelectedValue();
        int currentIndex = sectionTable.getSelectionModel().getLeadSelectionIndex();
        updateStyleList(style, currentIndex);
        sectionTableModel.tableRefresh();
        sectionTable.getSelectionModel().setSelectionInterval(currentIndex, currentIndex);
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

            Polylist voicing = (Polylist) o;

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
                    extension = (Polylist) o;

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

            c.setVoicing(voicing);

            c.setExtension(extension);

//            if( !ChordPattern.goodVoicing(c, currentStyle) )
//              {
//                ErrorLog.log(ErrorLog.WARNING,
//                             "Voicing does not fit within range of leadsheet: " + voicing);
//                return;
//              }

            playVoicing(c);

            insertVoicing(c, index);
          }
        else if( o instanceof Polylist )
          {
            ErrorLog.log(ErrorLog.WARNING, "No slot selected for insertion.");
          }
        else
          {
            ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
          }
    }//GEN-LAST:event_insertVoicingButtonActionPerformed

/**
 * Get the Style of the currently-playing score. Not exactly clear what this
 * means when a score has multiple sections, each with its own style.
 */
public Style getCurrentStyle()
  {
    return ImproVisor.getCurrentWindow().score.getChordProg().getStyle();
  }

/**
 * Plays a string of pitches as a chord over a given symbol.
 *
 * @param v
 */
public void constructAndPlayChord(String symbol, String v)
  {
    if( v.equals("") )
      {
        return;
      }

    Style currentStyle = getCurrentStyle();

    StringReader voicingReader = new StringReader(v);

    Tokenizer in = new Tokenizer(voicingReader);

    Object o = in.nextSexp();

    if( o instanceof Polylist )
      {
        Polylist voicing = (Polylist) o;

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

        if( voicing == null || c == null )
          {
            return;
          }

        c.setVoicing(voicing);

        playVoicing(c);
      }
    else
      {
        ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
      }
  }

/**
 * Plays the chord at the given index, if any
 *
 * @param index
 */
public void playChordAtIndex(int index)
  {
    Chord chordToPlay = chordProg.getChord(index);
    if( chordToPlay == null )
      {
        return;
      }
    Style currStyle = ImproVisor.getCurrentWindow().score.getChordProg().getStyle();
    Score tempScore = new Score();
    tempScore.addPart();
    tempScore.getPart(0).addRest(new Rest(chordToPlay.getRhythmValue()));
    tempScore.addChord(chordToPlay);
    tempScore.setStyle("no-style");
    int temp = chordToPlay.getRhythmValue();
    chordToPlay.setRhythmValue(480);
    try
      {
        midiSynth2.play(tempScore, 0, 0, 0, false, 480, 0);
      }
    catch( Exception e )
      {
        //not exactly sure what to put here
      }
    chordToPlay.setRhythmValue(temp);
  }

    private void playVoicingButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playVoicingButtonActionPerformed
        String c = getChordRootTFText();
        String v = voicingEntryTFText();
        constructAndPlayChord(c, v);
    }//GEN-LAST:event_playVoicingButtonActionPerformed

public void playCurrentVoicing()
  {
    String v = voicingEntryTF.getText();

    String e = extEntryTF.getText();

    if( v.equals("") )
      {
        //ErrorLog.log(ErrorLog.WARNING, "No currentVoicing entered.");

        return;
      }

    StringReader voicingReader = new StringReader(v);

    Tokenizer in = new Tokenizer(voicingReader);

    Object o = in.nextSexp();

    if( o instanceof Polylist )
      {
        Polylist currentVoicing = (Polylist) o;

        if( currentVoicing.length() == 0 )
          {
            //ErrorLog.log(ErrorLog.WARNING, "Empty currentVoicing entered.");

            return;
          }

        Polylist invalid = Key.invalidNotes(currentVoicing);

        if( invalid.nonEmpty() )
          {
            ErrorLog.log(ErrorLog.WARNING, "Invalid notes in voicing: " + invalid);

            return;
          }

        currentVoicing = NoteSymbol.makeNoteSymbolList(currentVoicing);

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
                extension = (Polylist) o;

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

        c.setVoicing(currentVoicing);

        c.setExtension(extension);

        playVoicing(c);
      }
    else
      {
        ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
      }
  }

/**
 * Determine whether the chord name in the indicated row of the currentVoicing
 * table is a synonym for another name (e.g. by specifiying (uses ...)). If so,
 * return that name. Otherwise return null.
 *
 * @param row
 * @return
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
        String target = voicingName.substring(VOICING_REDIRECT_PREFIX.length(), voicingName.length() - 1);
        return target;
      }
    return null;
  }

public void saveGrammarAs()
  {
    grammarfc.setDialogTitle("Save Grammar As");

    File oldDirectory = grammarfc.getCurrentDirectory();

    grammarfc.setCurrentDirectory(ImproVisor.getGrammarDirectory());

    // If never saved before, used the name specified in vocFile.
    // Otherwise use previous file.

    if( grammarFilename == null )
      {
        setGrammarFilename(ImproVisor.getGrammarFile().getAbsolutePath());
      }

    grammarfc.setSelectedFile(new File(grammarFilename));

    grammarfc.resetChoosableFileFilters();

    grammarfc.addChoosableFileFilter(new GrammarFilter());

    if( grammarfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        //lickgenFrame.saveTriageParameters(); // Not wanted here

        if( grammarfc.getSelectedFile().getName().endsWith(
                GrammarFilter.EXTENSION) )
          {
            setGrammarFilename(grammarfc.getSelectedFile().getAbsolutePath());

            lickgen.saveGrammar(grammarFilename);
          }
        else
          {
            setGrammarFilename(grammarfc.getSelectedFile() + GrammarFilter.EXTENSION);

            lickgen.saveGrammar(grammarFilename);
          }
      }

    lickgenFrame.toFront();
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
    lickgen.loadGrammar(grammarFilename);

    lickgenFrame.resetTriageParameters(true);
  }

public void openGrammar()
  {
    grammarfc.setDialogTitle("Load Grammar File");

    File oldDirectory = grammarfc.getCurrentDirectory();

    grammarfc.setCurrentDirectory(ImproVisor.getGrammarDirectory());

    grammarfc.resetChoosableFileFilters();

    grammarfc.addChoosableFileFilter(new GrammarFilter());

    if( grammarfc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        String filename = grammarfc.getSelectedFile().getAbsolutePath();

        setGrammarFilename(filename);

        lickgen.loadGrammar(filename);
      }

    grammarfc.setCurrentDirectory(oldDirectory);
    lickgenFrame.toFront();
  }

public void setGrammar(String grammarName)
  {
    notateGrammarMenu.setText(grammarName);
    String extendedName = grammarName + GrammarFilter.EXTENSION;
    grammarFilename = ImproVisor.getGrammarDirectory() + File.separator + extendedName;
    lickgen.loadGrammar(grammarFilename);
    lickgenFrame.resetTriageParameters(false);
    Preferences.setPreference(Preferences.DEFAULT_GRAMMAR_FILE, extendedName);
  }

private static int VoicingTableChordColumn = 0;
private static int VoicingTableNameColumn = 1;
private static int VoicingTableTypeColumn = 2;
private static int VoicingTableVoicingColumn = 3;
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

                    ChordSymbol c = (ChordSymbol) o;

                    String v = "";

                    String e = "";

                    Style s =
                            ImproVisor.getCurrentWindow().score.getChordProg().getStyle();

                    if( ChordPattern.goodVoicing(c, s) )
                      {
                        Polylist L =
                                ChordPattern.findFirstVoicingAndExtension(c, s.getChordBase(), s, false);

                        c.setVoicing((Polylist) L.first());

                        c.setExtension((Polylist) L.second());

                        v = NoteSymbol.makePitchStringList((Polylist) L.first()).toString();

                        e = NoteSymbol.makePitchStringList((Polylist) L.second()).toString();

                      }
                    else
                      {
                        Object voicingOb = voicingTableModel.getValueAt(selectedRow,
                                                                        VoicingTableVoicingColumn);

                        if( voicingOb != null )
                          {
                            s = s.copy();

                            s.setChordLow(NoteSymbol.makeNoteSymbol("c-----"));

                            s.setChordHigh(NoteSymbol.makeNoteSymbol("g+++++"));

                            Polylist L = ChordPattern.findFirstVoicingAndExtension(
                                    c, s.getChordBase(), s, false);

                            c.setVoicing((Polylist) L.first());

                            c.setExtension((Polylist) L.second());

                            v = NoteSymbol.makePitchStringList((Polylist) L.first()).toString();

                            e = NoteSymbol.makePitchStringList((Polylist) L.second()).toString();

                          }
                      }

                    if( keyboard != null )
                      {
                        clearKeyboard();
                        clearVoicingEntryTF();
                      }

                    if( e.equals("()") )
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
                    if( !note.equals(bass) )
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

    Style s = score.getChordProg().getStyle().copy();

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

    cScore.addChord(c);

    cScore.setStyle(s);

    new PlayScoreCommand(cScore,
                         0,
                         false,
                         midiSynth,
                         this,
                         0,
                         getTransposition(),
                         false,
                         4 * BEAT).execute();
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

    if( root.equals("") )
      {
        ErrorLog.log(ErrorLog.WARNING, "No chord root entered.");
        return;
      }
    else if( rootClass == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Invalid chord root: " + root);
        return;
      }

    PitchClass bassClass = PitchClass.getPitchClass(bass);

    if( !bass.equals("") && bassClass == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Invalid bass note: " + bass);
        return;
      }

    NoteSymbol lowNote = NoteSymbol.makeNoteSymbol(low);

    if( low.equals("") )
      {
        ErrorLog.log(ErrorLog.WARNING, "No lower range entered.");
        return;
      }
    else if( lowNote == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Invalid lower range: " + low);
        return;
      }

    NoteSymbol highNote = NoteSymbol.makeNoteSymbol(high);

    if( high.equals("") )
      {
        ErrorLog.log(ErrorLog.WARNING, "No higher range entered.");
        return;
      }
    else if( highNote == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Invalid higher range: " + high);
        return;
      }

    voicingTableModel.setChordRoot(root, bass, lowNote, highNote);
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

        if( !initLocationVoicingFrame )
          {
            voicingTestFrame.setLocationRelativeTo(this);

            initLocationVoicingFrame = true;
          }

        buildVoicingTable();

        voicingTestFrame.setVisible(true);

        if( playingStopped() )
          {
            openKeyboard();
            keyboard.showBass();
          }
    }//GEN-LAST:event_voicingTestMIActionPerformed

    private void pauseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseBtnActionPerformed
        if( mode == Mode.PLAYING_PAUSED )
          {
            setMode(Mode.PLAYING);
          }
        else
          {
            setMode(Mode.PLAYING_PAUSED);
          }
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

public void pauseScore()
  {
    if( !playingStopped() )
      {
        midiSynth.pause();
      }
  }

    private void tempoSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_tempoSliderStateChanged

        if( jSliderIgnoreStateChangedEvt )
          {
            return;
          }

        int value = tempoSlider.getValue();

        value = 2 * Math.round(value / 2);

        setTempo((double) value);

        setPlaybackManagerTime();

        if( !tempoSlider.getValueIsAdjusting() )
          {
            staveRequestFocus();
          }
    }//GEN-LAST:event_tempoSliderStateChanged

private void setPlaybackManagerTime()
  {
    establishCountIn();
    playbackManager.setTotalTime(million * score.getTotalTime());
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
public void setStatus(String text)
  {
    statusMenu.setOpaque(true);
    statusMenu.setBackground(Color.green);
    statusMenu.setText(text);
    statusMenu.repaint();
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
public void setMode(Mode mode)
  {
    previousMode = this.mode;

    if( mode == null )
      {
        mode = Mode.NORMAL;
      }

    this.mode = mode;

    switch( mode )
      {
        case NORMAL:
            setStatus("Play, Enter chords & melody, Open file, etc.");
            break;
        case RECORDING:
            setStatus("Chorus " + recurrentIteration);
            break;
        case STEP_INPUT:
            setStatus("Step-recording.");
            break;
        case DRAWING:
            setStatus("Draw notes with the mouse (set slots first).");
            break;
        case GENERATING:
            setStatus("Generating melody");
            break;
        case GENERATED:
            setStatus("Melody generated");
            break;
        case ROADMAP:
            setStatus("Creating Roadmap");
            break;
        case ROADMAP_DONE:
            setStatus("Roadmap Created");
            break;
        case ADVICE:
            setStatus("Select Advice.");
            break;
        case LEADSHEET_SAVED:
            setStatus("Leadsheet Saved");
            break;
        case STYLE_EDIT:
            setStatus("Editing Style");
            break;
        case EDIT_LEADSHEET:
            setStatus("Edit leadsheet textually");
            break;
        case PLAYING:
            setStatus("Playing");
            break;
        case PLAYING_PAUSED:
            setStatus("Playing Paused");
            break;
        case IMPORTING_MIDI:
            setStatus("Importing MIDI");
            break;
      }

    //repaintAndStaveRequestFocus();
  }

public void setNormalMode()
  {
    setMode(Mode.NORMAL);
  }

/**
 *
 * Tells other classes what mode the Notate object is in
 *
 */
public Mode getMode()
  {
    return this.mode;
  }

/**
 * gets the value of midiRecorder
 * @return instance of MidiRecorder
 */
public MidiRecorder getMidiRecorder(){
    return this.midiRecorder;
}

public void initTradingRecorder(MelodyPart aMelodyPart){
    if (this.midiRecorder == null) {
        this.midiRecorder = new MidiRecorder(this, this.score);
        this.midiRecorder.setDestination(aMelodyPart);
    } else{
        this.midiRecorder.setDestination(aMelodyPart);
    }
}

/**
 *
 * Stops recording: unregisters the midiRecorder and changes the mode
 *
 */
public void stopRecording()
  {
    playBtn.setEnabled(true);

    recordBtn.setIcon(recordImageIcon);

    if( midiRecorder != null )
      {
        midiSynth.unregisterReceiver(midiRecorder);
      }

    if( stepInputActive )
      {
        // if step input was active, reenable it since it is disabled during recording

        midiSynth.registerReceiver(midiStepInput);
      }
  }

private void startAudioCapture()
  {
    captureInterval = score.getMetre()[0] * BEAT;
    int startingPosition = (Math.round(midiSynth.getSlot()
            / getTradeLength())) * getTradeLength();
    System.out.println("Starting position " + startingPosition
            + " passed to PitchExtractor.");
    extractor = new PitchExtractor(this,
                                   score,
                                   midiSynth,
                                   audioSettings,
                                   startingPosition,
                                   captureInterval,
                                   true);
    extractor.captureAudio();
  }

/*Used by the SCDelayOffsetter*/
public AudioSettings getAudioSettings()
  {
    return audioSettings;
  }

/**
 * Starts recording: registers the midiRecorder and changes the mode.
 */
public void startRecording(){
    setFirstChorus(true);
    //Take care of first time recording audio preferences
    if( !audioLatencyRegistered )
      {
        Preferences.setAudioInLatency(1.0);
        audioLatencyRegistered = true;//Can also be true from saveAudioLatency()
      }

    turnStepInputOff();

    if( midiManager.getInDevice() == null )
      {
        ErrorLog.log(ErrorLog.COMMENT, "No valid MIDI in devices found.  \n\nPlease check your device connection and the MIDI Preferences. It is possible another program is currently using this device.");
      }
    
    /*
    this section is from the old implementation of audio input
    */
//    else if( superColliderMode ) //User wants to use SuperCollider. Works if checkbox selected
//      {
//
//        String devName = midiManager.getInDeviceInfo().getName();
//        //Check for valid Input Device associated with using SuperCollider
//        boolean validSCInDevice = devName.equals("IAC Bus 1")
//                || devName.equals("Bus 1")
//                || devName.equals("LoopBe Internal MIDI")
//                || devName.contains("VirMIDI");
//
//        //If valid device selected, okay to go through with recording. Else,
//        //yell at user. @TODO potential trouble spot for user-defined 
//        //workarounds.            
//        if( validSCInDevice )
//          {
//            SCHandler handler = new SCHandler();
//            handler.openSC();
//            startRecordingHelper();
//          }
//        else
//          {
//            //@TODO Yell at user. Like through a dialog. Then quit.
//            ErrorLog.log(ErrorLog.WARNING, "You need a valid MIDI input device "
//                    + "to do this! See Help->Audio Input for instructions/details.");
//          }
//      }
    else
      {
        startRecordingHelper();//below
      }
  }




/**
 * Takes care of midiSynth and midiRecorder material for startRecording().
 * Called separately from startRecording() so SuperCollider has time to run.
 * @param thisScore score into which midi is actually recorded
 */
private void startRecordingHelper()
  {
    playBtn.setEnabled(false);

    recordBtn.setIcon(recordActiveImageIcon);

    //recordBtn.setBackground(Color.RED);

    if( midiRecorder == null )
      {
        midiRecorder = new MidiRecorder(this, this.score);
      }

//    no longer used
//    Deal with latency
//    if( superColliderMode )
//      {//Set latency to default
//        double latency = Preferences.getAudioInLatency();
//        midiRecorder.setLatency(latency);
//      }

    midiSynth.registerReceiver(midiRecorder);

    staveRequestFocus();

    playScore();

    setMode(Mode.RECORDING);

    midiSynth.unregisterReceiver(midiStepInput);  // disable step input during recording

    midiSynth.registerReceiver(midiRecorder);

    midiRecorder.start(this.score.getCountInOffset());   // set time to 0
  }


/**
 * This is like startRecording() without the playback.
 */
public void enableRecording()
  {
    //debug System.out.println("enableRecording()");
    turnStepInputOff();

    if( midiManager.getInDevice() == null )
      {
        ErrorLog.log(ErrorLog.COMMENT, "No valid MIDI in devices found.  \n\nPlease check your device connection and the MIDI Preferences. It is possible another program is currently using this device.");

        return;
      }

    playBtn.setEnabled(false);

    recordBtn.setIcon(recordActiveImageIcon);

    //recordBtn.setBackground(Color.RED);

    if( midiRecorder == null )
      {
        midiRecorder = new MidiRecorder(this, score);
      }

    midiSynth.registerReceiver(midiRecorder);

    staveRequestFocus();

    establishCountIn();
    //playScore();

    setMode(Mode.RECORDING);

    midiSynth.unregisterReceiver(midiStepInput);  // disable step input during recording

    // redundant midiSynth.registerReceiver(midiRecorder);

    midiRecorder.start(score.getCountInOffset());
  }

void stopPlaying()
  {
    stopPlaying(" unlabeled");
  }

int savNum = 1;
void stopPlaying(String reason)
  {
    //System.out.println("stopPlaying called in Notate for reason: " + reason);
    midiSynth.stop("stop in Notate: " + reason);
    lickgenFrame.setRecurrent(false);
    if( mode == Mode.RECORDING )
      {
        stopRecording();
      }
    improvisationOff();
    setNormalMode();
    setShowConstructionLinesAndBoxes(showConstructionLinesMI.isSelected());
    //System.out.println("stopPlaying()");
    //requestFocusInWindow();
    
    //from here end enables saving improv in the lickgenframe
    if(lickgenFrame.shouldSaveImp() && improvOn){
        Notate savNot = cloneLS();
        while (!melodyList.isEmpty())
        {
            melodyList.get(0).setInstrument(instr);
            savNot.addChorus(melodyList.get(0));
            melodyList.remove(0);
        }
        savNot.score.delPart(0);
        String point = ImproVisor.getLastLeadsheetFileStem();
        String fin = point.substring(0, point.length()-3);
        String secFin = fin;
        //if -1 already
        if (Character.isDigit(fin.charAt(fin.length()-1))){
            savNum = Character.getNumericValue(fin.charAt(fin.length()-1));
            if (fin.endsWith("-"+savNum)){
                while (fin.endsWith("-"+savNum)){savNum++;}
                if (savNum > 9){secFin = fin.substring(0, fin.length()-3);}
                else if (savNum > 99){savNum = 1; secFin = fin.substring(0, fin.length()-2);}
                else{secFin = fin.substring(0, fin.length()-2);}
            }
            else if (fin.endsWith("- "+savNum)){
                while (fin.endsWith("- "+savNum)){savNum++;}
                if (savNum > 9){secFin = fin.substring(0, fin.length()-4);}
                else if (savNum > 99){savNum = 1; secFin = fin.substring(0, fin.length()-3);}
                else{secFin = fin.substring(0, fin.length()-3);}
            }
            else{
                savNum = 1;
            }
        }
        if (secFin.endsWith(" ")){secFin=secFin.substring(0, secFin.length()-1);}
        ImproVisor.setLastLeadsheetFileStem(secFin.concat("-"+savNum+".ls"));
        savNum = 1;
        savNot.saveAsLeadsheet(); 
        ImproVisor.setLastLeadsheetFileStem(point);
    }
  }


public Notate cloneLS()
{
    Score newScore = getScore();

    //ensureChordFontSize();

    //int chordFontSize = Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE)).intValue();

    //newScore.setChordFontSize(chordFontSize);

    //newScore.setTempo(getDefaultTempo());

    //newScore.setChordProg(new ChordPart());

    //newScore.addPart(new MelodyPart(defaultBarsPerPart * measureLength));

    //newScore.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));

    // open a new window
    
    for (int x=1; x<scoreTab.getTabCount(); x++)
    {
        newScore.delPart(0);
    }
    
    Notate newNotate =
            new Notate(newScore,
                       this.adv,
                       this.impro,
                       getX(),
                       getY());

    newNotate.updatePhiAndDelta(this.getPhiStatus(), this.getDeltaStatus());

    //newNotate.makeVisible(this);//

    newNotate.setPrefsDialog();
    
    

    // set the menu and button states

    setItemStates();
    return newNotate;
}



private void setStepInput(boolean active)
  {
    stepInputActive = active;

    if( active )
      {
        midiSynth.registerReceiver(midiStepInput);
      }
    else
      {
        midiSynth.unregisterReceiver(midiStepInput);
      }
  }

    private void recordBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recordBtnActionPerformed
        recordFromMidi();
    }//GEN-LAST:event_recordBtnActionPerformed

private void recordFromMidi()
  {
    if( mode == Mode.RECORDING )
      {
        stopRecording();
      }
    else
      {
        startRecording();
      }
  }

    private void playBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtnActionPerformed
        improvisationOn = false;
        improvOn = false;
        playAll();
    }//GEN-LAST:event_playBtnActionPerformed

    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_windowMenuMenuSelected

        windowMenu.removeAll();

        windowMenu.add(closeWindowMI);

        windowMenu.add(cascadeMI);

        windowMenu.add(windowMenuSeparator);

        for( WindowMenuItem w : WindowRegistry.getWindows() )
          {

            windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed

          }

        windowMenu.repaint();
    }//GEN-LAST:event_windowMenuMenuSelected

private void chordToneWeightFieldFocusLost(java.awt.event.FocusEvent evt)
  {
    verifyAndFill();
  }

void FillProbsButtonActionPerformed(java.awt.event.ActionEvent evt)
  {
    verifyAndFill();
  }

    private void openGeneratorButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openGeneratorButtonActionPerformed

    {//GEN-HEADEREND:event_openGeneratorButtonActionPerformed
        openLickGenerator();
    }//GEN-LAST:event_openGeneratorButtonActionPerformed

public void openLickGenerator()
  {
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

    lickgenFrame.setSize(lickGenFrameDimension);
    lickgenFrame.setVisible(true);

    entryMuteActionPerformed(null);
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

public void focus()
  {
    setVisible(true);

    toFront();

    requestFocus();

    staveRequestFocus();
  }

    private void resetBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetBtnActionPerformed

        setPrefsDialog();
        numStavesPerPage.setText(Preferences.DEFAULT_STAVES_PER_PAGE);

    }//GEN-LAST:event_resetBtnActionPerformed

public void setVolumes(MidiSynth midiSynth)
  {
    midiSynth.setChannelVolume(getBassChannel(), bassVolume.getValue());
    midiSynth.setChannelVolume(getDrumChannel(), drumVolume.getValue());
    midiSynth.setChannelVolume(getChordChannel(), chordVolume.getValue());
    midiSynth.setChannelVolume(getMelodyChannel(), melodyVolume.getValue());
  }

public int getMelodyChannel()
  {
    return ((Integer) melodyChannelSpinner.getValue()).intValue() - 1;
  }

public int getChordChannel()
  {
    return ((Integer) chordChannelSpinner.getValue()).intValue() - 1;
  }

public int getBassChannel()
  {
    return ((Integer) bassChannelSpinner.getValue()).intValue() - 1;
  }

public int getDrumChannel()
  {
    return ((Integer) drumChannelSpinner.getValue()).intValue() - 1;
  }

public void setMelodyChannel(int value)
  {
    melodyChannelSpinner.setValue(value + 1);
  }

public void setChordChannel(int value)
  {
    chordChannelSpinner.setValue(value + 1);
  }

public void setBassChannel(int value)
  {
    bassChannelSpinner.setValue(value + 1);
  }

public void setDrumChannel(int value)
  {
    drumChannelSpinner.setValue(value + 1);
  }

private void setChannelVolumes(int channel, int volume)
  {
    if( midiSynth != null )
      {
        midiSynth.setChannelVolume(channel, volume);
      }
    if( midiSynth2 != null )
      {
        midiSynth2.setChannelVolume(channel, volume);
      }
    if( midiSynth3 != null )
      {
        midiSynth3.setChannelVolume(channel, volume);
      }
  }

    private void drumMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drumMuteActionPerformed

        drumVolumeChanged();
    }//GEN-LAST:event_drumMuteActionPerformed

private void drumVolumeChanged()
  {
    // set flag to for unsaved changes

    cm.changedSinceLastSave(true);

    int v = drumVolume.getValue();

    score.setDrumVolume(v);

    score.setDrumMuted(drumMute.isSelected());

    drumVolume.setEnabled(!drumMute.isSelected());

    if( score.getDrumMuted() )
      {
        setDrumVolume(0);
      }
    else
      {
        setDrumVolume(v);
      }
  }

private void bassVolumeChanged()
  {
    // set flag to for unsaved changes

    cm.changedSinceLastSave(true);

    int v = bassVolume.getValue();

    score.setBassVolume(v);

    score.setBassMuted(bassMute.isSelected());

    bassVolume.setEnabled(!bassMute.isSelected());

    if( score.getBassMuted() )
      {
        setBassVolume(0);
      }
    else
      {
        setBassVolume(v);
      }
  }

private void melodyVolumeChanged()
  {
    // set flag to for unsaved changes

    cm.changedSinceLastSave(true);

    int v = melodyVolume.getValue();

    score.setMelodyVolume(v);

    score.setMelodyMuted(melodyMute.isSelected());

    melodyVolume.setEnabled(!melodyMute.isSelected());

    if( score.getMelodyMuted() )
      {
        setMelodyVolume(0);
      }
    else
      {
        setMelodyVolume(v);
      }
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
            setChordVolume(0);
          }
        else
          {
            setChordVolume(v);
          }
      }
  }

public void setMelodyVolume(int v)
  {
    setChannelVolumes(getMelodyChannel(), v);
  }

public void setChordVolume(int v)
  {
    setChannelVolumes(getChordChannel(), v);
  }

public void setBassVolume(int v)
  {
    setChannelVolumes(getBassChannel(), v);
  }

public void setDrumVolume(int v)
  {
    setChannelVolumes(getDrumChannel(), v);
  }

private void setMuteAll(boolean muted)
  {
    score.setMasterVolumeMuted(muted);

    if( muted )
      {
        allMuteMixerBtn.setSelected(true);

        allMuteToolBarBtn.setSelected(true);

        allMuteToolBarBtn.setBackground(Color.red);

        allMuteToolBarBtn.setText("Play");
      }
    else
      {
        allMuteMixerBtn.setSelected(false);

        allMuteToolBarBtn.setSelected(false);

        allMuteToolBarBtn.setBackground(Color.green);

        allMuteToolBarBtn.setText("Mute");
      }

    mixerMasterVolumeChanged();
  }

/**
 * Set the volume of all sliders and the score.
 *
 * @param value
 */
public void setSliderVolumes(int value)
  {
    allVolumeMixerSlider.setValue(value);
    allVolumeToolBarSlider.setValue(value);

    if( roadmapFrame != null )
      {
        roadmapFrame.setVolumeSlider(value);
      }

    if( score != null )
      {
        score.setMasterVolume(value);
      }
  }

private void mixerMasterVolumeChanged()
  {
    int v = allVolumeMixerSlider.getValue();

    setSliderVolumes(v);

    if( score.getMasterVolumeMuted() )
      {
        allVolumeMixerSlider.setEnabled(false);

        allVolumeToolBarSlider.setEnabled(false);

        setMasterVolumes(0);
      }
    else
      {
        allVolumeMixerSlider.setEnabled(true);

        allVolumeToolBarSlider.setEnabled(true);

        setMasterVolumes(v);
      }
  }

public void setMasterVolumes(int v)
  {
    if( midiSynth != null )
      {
        midiSynth.setMasterVolume(v);
      }

    if( midiSynth2 != null )
      {
        midiSynth2.setMasterVolume(v);
      }

    if( midiSynth3 != null )
      {
        midiSynth3.setMasterVolume(v);
      }
  }

private void setVolumeDefaults()
  {
    setSliderVolumes(Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MIXER_ALL)));

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

        if( mixerSliderIgnoreStateChangedEvt )
          {
            return;
          }

        mixerSliderIgnoreStateChangedEvt = true;

        mixerMasterVolumeChanged();

        mixerSliderIgnoreStateChangedEvt = false;
    }//GEN-LAST:event_allVolumeMixerSliderStateChanged

    private void preferencesMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuActionPerformed
// TODO add your handling code here:
    }//GEN-LAST:event_preferencesMenuActionPerformed

private void showMixer()
  {
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

        volumeSliderChanged(allVolumeToolBarSlider);
    }//GEN-LAST:event_allVolumeToolBarSliderStateChanged

public void volumeSliderChanged(JSlider volumeSlider)
  {
    if( toolbarVolumeSliderIgnoreStateChangedEvt )
      {
        return;
      }

    toolbarVolumeSliderIgnoreStateChangedEvt = true;

    int value = volumeSlider.getValue();

    setSliderVolumes(value);

    toolbarVolumeSliderIgnoreStateChangedEvt = false;

    if( !allVolumeToolBarSlider.getValueIsAdjusting() )
      {
        staveRequestFocus();
      }
  }

    private void entryMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entryMuteActionPerformed

        entryVolume.setEnabled(!entryMute.isSelected());

        impro.setPlayEntrySounds(!entryMute.isSelected());
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
        //stop midi latency test
        this.midiLatencyMeasurement.stop();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void savePrefsBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_savePrefsBtnActionPerformed

        savePrefs();
    }//GEN-LAST:event_savePrefsBtnActionPerformed

private Component currentPrefTab = null;
private JToggleButton currentPrefButton = null;
private Component currentGenTab = null;
private JToggleButton currentGenButton = null;

/**
 * Used to provide access to switching preferences tabs from another class file
 */
void changePrefTab(JToggleButton button, JPanel tab)
  {

    button.setSelected(true);

    currentPrefButton = button;

    currentPrefTab = tab;

    //System.out.println("setting tab to " + tab);

    if( tab.equals(stylePreferences) )
      {
        setTableColumnWidths();
        sectionTable.setRowSelectionAllowed(true);
        sectionTable.setColumnSelectionAllowed(false);
        /*
         if(sectionInfo != null)
         {
         for(int j = 0; j < sectionInfo.size(); j++)
         System.out.print(sectionInfo.getSectionRecordByIndex(j).toString() + " ");
         System.out.println("-----");
         }
         */
      }

    preferencesScrollPane.setViewportView(tab);

    preferencesDialog.setSize(preferencesDialogDimension);

    preferencesDialog.repaint();

  }

void changeGenTab(JToggleButton button, JPanel tab)
  {
    currentGenButton = button;

    if( currentGenTab == tab )
      {
        return;
      }

    currentGenTab = tab;
    //generatorScrollPane.setViewportView(tab);

    lickgenFrame.pack();
    lickgenFrame.repaint();
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

private void devicesChanged()
  {
    midiManager.setInDevice((MidiDevice.Info) midiInComboBox.getSelectedItem());
    midiManager.setOutDevice(midiOutComboBox.getSelectedItem());

    refreshMidiStatus();
  }

String okMsg = "<html>Status: <em><font color='green'>Device ready</font></em></html>";
String noDevSelectedMsg = "<html>Status: <em><font color='red'>No Device Selected</font></em></html>";
String noDev = "<html>No devices found.</html>";
String failMsgStart = "<html>Status: <em><font color='red'>";
String failMsgEnd = "</font></em></html>";

public void refreshMidiStatus()
  {
    // get midi latency

    saveMidiLatency();

    //midiLatencyTF.setText(String.valueOf(midiRecorder.getLatency()));

    // update midiInChooser status:

    if( midiOutComboBox.getItemCount() == 0 )
      {
        midiOutPanel.remove(midiOutComboBox);

        midiOutStatus.setText(noDev);
      }
    else
      {
        if( !midiOutComboBox.isVisible() )
          {
            midiOutPanel.remove(midiOutStatus);

            midiOutPanel.add(midiOutComboBox);

            midiOutPanel.add(midiOutStatus);
          }

        midiOutStatus.setText(midiManager.getOutDeviceError().equals("")
                ? okMsg
                : failMsgStart + midiManager.getOutDeviceError() + failMsgEnd);
      }

    // update midiOutChooser status:

    if( midiInComboBox.getItemCount() == 0 )
      {
        midiInPanel.remove(midiInComboBox);

        midiInStatus.setText(noDev);
      }
    else
      {
        if( !midiInComboBox.isVisible() )
          {
            midiInPanel.remove(midiInStatus);

            midiInPanel.add(midiInComboBox);

            midiInPanel.add(midiInStatus);
          }

        midiInStatus.setText((midiManager.getInDevice() != null)
                ? okMsg
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
private ArrayList<Polylist> data = new ArrayList<Polylist>();

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

@Override
public String getColumnName(int col)
  {
    return columnNames[col];
  }

@Override
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

/**
 * This is the Model for the list of Styles that appear in the Style Preferences
 * Dialog. The list contains references to the actual style objects, but
 * displays their names.
 */
@SuppressWarnings("serial")
public class StyleListModel
        extends AbstractListModel
{

public int getSize()
  {
    int number = Style.numberOfStyles();
    //System.out.println("in StyleListModel number = " + number);
    return number;
  }

public Object getElementAt(int index)
  {
    //System.out.println("requesting " + index + " of " + getSize() + " " + Style.getNth(index));
    return Style.getNth(index);
  }

public void reset()
  {
    //System.out.println("reset");
    fireContentsChanged(this, 0, getSize());
  }

public void adjust()
  {
    //System.out.println("adjust");
    fireIntervalAdded(this, 0, getSize());
  }

}
ArrayList<Style> recentStyles = new ArrayList<Style>();
int recentStyleIndex = 0;

public void addRecentStyle(Style style)
  {
    if( !recentStyles.contains(style) )
      {
        recentStyles.add(style);
        recentStyleListModel.reset();
      }
    recentStyleIndex = recentStyles.indexOf(style);
  }

@SuppressWarnings("serial")
public class RecentStyleListModel
        extends AbstractListModel
{

public int getSize()
  {
    int number = recentStyles.size();
    //System.out.println("in StyleListModel number = " + number);
    return number;
  }

public Object getElementAt(int index)
  {
    //System.out.println("requesting " + index + " of " + getSize() + " " + Style.getNth(index));
    return recentStyles.get(index);
  }

public void reset()
  {
    fireContentsChanged(this, 0, getSize());
  }

public void adjust()
  {
    fireIntervalAdded(this, 0, getSize());
  }

}

/**
 * This is the Model for the list of Sections that appear in the Style
 * Preferences Dialog.
 *
 * This is no longer needed; has been replaced with SectionTableModel
 */
@SuppressWarnings("serial")
public class SectionListModel extends AbstractListModel
{

public int getSize()
  {
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

}

//row gets sectionInfo's sectionRecord, column gets within sectionRecord
/**
 * When adjusting the number of columns, make sure: to add the columns to the
 * design/gui to go to line ~697 where sectionTableModel is instantiated and
 * uncomment/add the appropriate strings to change setTableColumnWidths()
 * accordingly to change the boolean [] canEdit accordingly to change the int []
 * columnWidths and columnAdjustment accordingly to change getValueAt
 * accordingly
 */
public class SectionTableModel extends DefaultTableModel
{

private static final int columnCount = 6;
boolean[] canEdit = new boolean[]
  {
    //phrase, start, end  , bars , style, options
    true, true, true, true, false, true
  };
int[] columnWidths = new int[]
  {
    30, 25, 25, 20, 110, 45
  };
int[] columnAdjustment = new int[]
  {
    JLabel.CENTER, JLabel.RIGHT, JLabel.RIGHT, JLabel.RIGHT,
    JLabel.LEFT, JLabel.LEFT
  };

public SectionTableModel(Object[][] myTable, String[] columnHeaders)
  {
    super(myTable, columnHeaders);
  }

public int getColumnWidths(int index)
  {
    return columnWidths[index];
  }

public int getColumnAdjustments(int index)
  {
    return columnAdjustment[index];
  }

@Override
public boolean isCellEditable(int rowIndex, int columnIndex)
  {
    return canEdit[columnIndex];
  }

public void tableRefresh()
  {
    int index = sectionTable.getSelectionModel().getLeadSelectionIndex();
    fireTableDataChanged();
    if( index >= 0 && index < sectionInfo.size() )
      {
        if( index == sectionInfo.size() )
          {
            addRecentStyle(sectionInfo.getSectionRecordByIndex(index - 1).getStyle());
          }
        else
          {
            addRecentStyle(sectionInfo.getSectionRecordByIndex(index).getStyle());
          }
      }
  }

public void tableReset()
  {
    sectionInfo = score.getChordProg().getSectionInfo().copy();

    //tableRefresh();
  }

@Override
public Object getValueAt(int row, int column)
  {
    switch( column )
      {
        case 0: //phrase
            return sectionInfo.getSectionRecordByIndex(row).getIsPhrase();
        case 1: //startIndex
            return sectionInfo.getSectionMeasure(row);
        case 2: //endIndex
          {
            if( row + 1 < sectionInfo.size() )
              {
                return (sectionInfo.getSectionMeasure(row + 1) - 1);
              }
            return sectionInfo.measures();
          }
        case 3: //bars
          {
            if( row + 1 < sectionInfo.size() )
              {
                return (sectionInfo.getSectionMeasure(row + 1) - 1) - (sectionInfo.getSectionMeasure(row)) + 1;
              }
            return sectionInfo.measures() - sectionInfo.getSectionMeasure(row) + 1;
          }
        case 4: //styleName
            return " " + sectionInfo.getSectionRecordByIndex(row).getStyleName();
        case 5: //custom
            return sectionInfo.getSectionRecordByIndex(row).getUseCustomVoicing();
        case 6: //Time Sig.
            return null;
        case 7: //Key Sig.
            return null;
        case 8: //Options
            return null;
        default:
            return null;
      }
  }

@Override
public void setValueAt(Object aValue, int row, int column)
  {
    switch( column )
      {
        case 0:
            sectionInfo.getSectionRecordByIndex(row).setIsPhrase((Boolean) aValue);
            sectionInfo.setSpecificCell(aValue, row, column);
            break;
        case 5:
            sectionInfo.getSectionRecordByIndex(row).setUseCustomVoicing(aValue.equals("true"));
            sectionInfo.setSpecificCell(aValue, row, column);
            break;
        default:
            sectionInfo.setSpecificCell(aValue, row, column);
            break;
      }
    tableRefresh();
  }

@Override
public int getRowCount()
  {
    //return 0 if sectionInfo is null, else return sectionInfo.size()
    return sectionInfo == null ? 0 : sectionInfo.size();
  }

@Override
public int getColumnCount()
  {
    return columnCount;
  }

public void addARow()
  {
    sectionTableModel.insertRow(0, new Object[]
      {
        new Integer(0),
        new Integer(0),
        new Integer(0),
        "",
        new JCheckBox()
      });
  }

@Override
public Class getColumnClass(int column)
  {
    switch( column )
      {
        case 0:
            return Boolean.class;
      }
    return Object.class;
  }

/**
 * Gets the row index of the section based on the measure input to it
 *
 * @param measure - measure in the leadsheet
 * @return - row index
 */
public int getSectionFromMeasure(int measure)
  {
    for( int i = 0; i < sectionTable.getRowCount(); i++ )
      {
        int startIndex = (Integer) getValueAt(i, 1);
        int endIndex = (Integer) getValueAt(i, 2);
        if( measure >= startIndex && measure <= endIndex )
          {
            return i;
          }
      }
    return -1;
  }

/**
 * Inserts a section or phrase at the start or end of a measure
 *
 * @param isAfter - true starts section after the measure, false starts before
 * the measure
 * @param isPhrase - true splits the section into a phrase, false splits it into
 * a section
 */
public void insertSection(boolean isAfter, boolean isPhrase)
  {
    int after = isAfter ? 1 : 0;
    int measure = getCurrentStave().mouseOverMeasure + 1 + after;
    int index = sectionTableModel.getSectionFromMeasure(measure);
    if( index < 0 )
      {
        return;
      }
    // This code is suspect. May cause class cast exception
    int value = ((Integer) getValueAt(index, 1)).intValue();
    if( measure != value )
      {
        addARow();
        sectionInfo.newSection(index);
        setValueAt(measure, index + 1, 1);
      }
    else
      {
        index--;
      }
    if( index < 0 )
      {
        return;
      }
    setValueAt(isPhrase, index, 0);
    tableRefresh();
  }

/**
 * Merges the section with the one before it
 *
 * @param isAfter - true merges with the following section, false merges with
 * the previous section
 */
public void mergeSection(boolean isAfter)
  {
    int after = isAfter ? 1 : 0;
    int measure = getCurrentStave().mouseOverMeasure + 1;
    int index = sectionTableModel.getSectionFromMeasure(measure) + after;
    if( index == 0 || index >= sectionTable.getRowCount() )
      {
        return;
      }
    boolean isPhrase = (Boolean) getValueAt(index, 0);
    sectionInfo.deleteSection(index);
    setValueAt(isPhrase, index - 1, 0);
    tableRefresh();
  }

}

@SuppressWarnings("serial")
public class NWaySplitComboBoxModel
        extends AbstractListModel
        implements ComboBoxModel
{

ArrayList<String> items = new ArrayList<String>();
String selectedItem;

public int getSize()
  {
    return items.size();
  }

public Object getElementAt(int i)
  {
    return items.get(i);
  }

public void setSelectedItem(Object o)
  {
    selectedItem = o.toString();
  }

public Object getSelectedItem()
  {
    return selectedItem;
  }

public void createItems(int row)
  {
  }

//public void createItems(int row)
//  {
//    int bars = (Integer) (sectionTableModel.getValueAt(row, 3));
//    //System.out.println(bars+"");
//    if( bars == 1 )
//      {
//        items = new ArrayList<String>();
//      }
//    else if( bars - 1 < items.size() )
//      {
//        while( bars < Integer.parseInt(items.get(items.size() - 1)) )
//          {
//            items.remove(items.size() - 1);
//          }
//      }
//    else if( bars - 1 > items.size() )
//      {
//        int temp = items.isEmpty() ? 1 : Integer.parseInt(items.get(items.size() - 1));
//        for( int j = temp + 1; j <= bars; j++ )
//          {
//            items.add("" + j);
//          }
//      }
//
//    //checkItems();
//    splitRefresh();
//  }
//
//public void splitRefresh()
//  {
//    fireContentsChanged(this, 0, getSize());
//  }

}

@SuppressWarnings("serial")
static public class StyleComboBoxModel
        extends AbstractListModel
        implements ComboBoxModel
{

Polylist styles;
int len;

public int getSize()
  {
    return Style.numberOfStyles();
  }

public Object getElementAt(int index)
  {
    return Style.getNth(index);
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

private void showPreferencesDialog()
  {
    setPrefsDialog();

    // center dialog only the first time it is shown

    if( !initLocationPreferencesDialog )
      {
        preferencesDialog.setLocationRelativeTo(this);

        initLocationPreferencesDialog = true;
      }

    showFakeModalDialog(preferencesDialog);
  }

private boolean initLocationFileStepDialog = false;

private void showFileStepDialog()
  {
    if( !initLocationFileStepDialog )
      {
        fileStepDialog.setLocationRelativeTo(this);
        initLocationFileStepDialog = true;
      }
    showFakeModalDialog(fileStepDialog);
  }

    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        stopButtonPressed();
    }//GEN-LAST:event_stopBtnActionPerformed

public void stopButtonPressed()
  {
    stopPlaying("stop Btn action");
    if( keyboard != null && keyboard.isVisible() )
      {
        keyboard.setPlayback(false);
        clearVoicingEntryTF();
      }
    lickgenFrame.setRecurrent(false);
  }

private void setTempo(double value)
  {
    if( value >= MIN_TEMPO && value <= MAX_TEMPO )
      {
        tempoTF.setText("" + (int) value); // keep these in sync
        tempoSet.setText("" + (int) value);

        tempoSlider.setValue((int) value);

        score.setTempo(value);

        //System.out.println("notate setTempo to " + value);

        midiSynth.setTempo((float) value);
      }
    else
      {
        ErrorLog.log(ErrorLog.COMMENT,
                     "The tempo must be in the range " + MIN_TEMPO + " to " + MAX_TEMPO
                + ",\nusing default: " + getDefaultTempo() + ".");
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
 *
 * @param text
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
        setStatus("Invalid Action");
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

public void repaintAndStaveRequestFocus()
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

        ImproVisor.setShowAdvice(value);

        showAdviceButton.setBackground(value ? adviceBtnColorOpen : adviceBtnColorClosed);

        if( value )
          {
            redoAdvice();
          }
        else
          {
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

        cm.execute(new InsertPartCommand(this, getCurrentMelodyPart(), measureStart, singleMeasure));
    }//GEN-LAST:event_insertRestMeasureActionPerformed

    private void drawButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawButtonActionPerformed

        Stave stave = getCurrentStave();

        if( getMode() != Mode.DRAWING ) // either in cursor or note mode
          {
            // switch to pencil mode
            setMode(Mode.DRAWING);

            preDrawingEntryMuted = entryMute.isSelected();
            drawButton.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "graphics/toolbar/noPencil.gif")));
          }
        else
          { // switch to note mode

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

        setMode(Mode.ADVICE);
    }//GEN-LAST:event_adviceFocusGained

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
        openLickGenerator();
    }//GEN-LAST:event_lickGeneratorMIActionPerformed

/**
 *
 * flag to reset position of dialog the first time it is displayed only
 *
 * means that the dialog gets centered the first time it is shown,
 *
 * when the dialog is shown a second time, it remembers its last position
 *
 */
private boolean initLocationLeadsheetEditor = false;

    private void openLeadsheetEditorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openLeadsheetEditorMIActionPerformed

    {//GEN-HEADEREND:event_openLeadsheetEditorMIActionPerformed
        setMode(Mode.EDIT_LEADSHEET);

        // center dialog only the first time it is shown

        if( !initLocationLeadsheetEditor )
          {

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
        //System.out.println("saveSelection = ");

        saveLick(saveSelection);
    }//GEN-LAST:event_okSaveButtonActionPerformed

/**
 * Return an array of labels that have appropriate enharmonics for the black
 * notes.
 */
public String[] getNoteLabels(int location)
  {
    String[] notes = new String[12];

    Polylist preferredScale = lickgen.getPreferredScale();

    Polylist scaleTones;

    Polylist scales = chordProg.getCurrentChord(location).getScales();

    if( scales == null
            || scales.isEmpty()
            || preferredScale.isEmpty()
            || ((String) preferredScale.second()).equals(NONE) )
      {
        scaleTones = Polylist.nil;
      }
    else if( ((String) preferredScale.second()).equals(FIRST_SCALE) )
      {
        scaleTones = chordProg.getCurrentChord(location).getFirstScale();
      }
    else
      {
        scaleTones = Advisor.getScale((String) preferredScale.first(), (String) preferredScale.second());
      }

    boolean[] enh = score.getCurrentEnharmonics(location,
                                                scaleTones.append(chordProg.getCurrentChord(location).getPriority()));

    notes[0] = "C";

    notes[2] = "D";

    notes[4] = "E";

    notes[5] = "F";

    notes[7] = "G";

    notes[9] = "A";

    notes[11] = "B";

    if( enh[CSHARP] == true )
      {
        notes[1] = "C#";
      }
    else
      {
        notes[1] = "Db";
      }

    if( enh[DSHARP] == true )
      {
        notes[3] = "D#";
      }
    else
      {
        notes[3] = "Eb";
      }

    if( enh[FSHARP] == true )
      {
        notes[6] = "F#";
      }
    else
      {
        notes[6] = "Gb";
      }

    if( enh[GSHARP] == true )
      {
        notes[8] = "G#";
      }
    else
      {
        notes[8] = "Ab";
      }

    if( enh[ASHARP] == true )
      {
        notes[10] = "A#";
      }
    else
      {
        notes[10] = "Bb";
      }

    return notes;
  }

/**
 * Make sure the user has entered acceptable values for each of the other fields
 * in the triage frame.
 */
private void verifyTriageFields()
  {
    lickgenFrame.verifyTriageFields();
    lickgenFrame.setTotalBeats(totalBeats);
    getCurrentStave().repaint();
  }

private void verifyAndFill()
  {
    //System.out.println("verifyAndFill()");
    lickgenFrame.verifyAndFill();
    lickgenFrame.setTotalBeats(totalBeats);
    redrawTriage();
    getCurrentStave().repaint();
  }

/**
 * Save the lick in the vocabulary.
 *
 * @param saveSelection
 */
private void saveLick(String saveSelection)
  {
    saveLickFrame.setVisible(false);

    if( saveSelection != null )
      {
        Polylist selectionAsList = parseListFromString(saveSelection);

        if( Advisor.addUserRule(selectionAsList) )
          {
            saveAdviceActionPerformed(null);        // automatically save advice

            // lickSavedLabel.setText("Lick saved!");
          }
      }

    staveRequestFocus();
  }

public void triageLick(String lickName, int grade)
  {
    String saveSelection = getCurrentStave().getSaveSelection(lickName, Stave.ExtractMode.LICK, grade);

    if( lickgenFrame.toCriticSelected() )
      {
        criticDialog.add(saveSelection, grade);

        criticDialog.setVisible(true);
      }
    else
      {
        saveLick(saveSelection);
      }
  }

boolean isPowerOf2(int x)
  {
    // trust me, it works!
    return ((x > 0) && ((x & (x - 1)) == 0));
  }

private MelodyPart makeLick(Polylist rhythm, int start, int stop)
  {
    //System.out.println("makeLick for " + start + " to " + stop);
    //verifyAndFill();

    if( rhythm == null || rhythm.isEmpty() )
      {
        // redundant? ErrorLog.log(ErrorLog.SEVERE, "Null rhythm argument.  No lick will be generated.");
        return null;
      }

    lickgen.setProbs(lickgenFrame.readProbs());

    // Fill in a melody according to the provided rhythm.
    // FIX - Currently, the lick generator doesn't support half beats; thus,
    // it can only generate things in terms of number of quarter notes.
    // This is why BEAT is getting passed into the generator.

    MelodyPart lick = lickgenFrame.fillMelody(BEAT, rhythm, chordProg, start);

    int actualSize = lick.size();
    int desiredSize = stop - start + 1;

    if( actualSize > desiredSize )
      {
        //System.out.println("makeLick: reducing size from " + actualSize + " to desired " + desiredSize);
        lick = lick.extract(0, desiredSize - 1, true);
      }

    return lick;
  }

private MelodyPart makeLick(Polylist rhythm)
  {
    //verifyAndFill();

    if( rhythm == null || rhythm.isEmpty() )
      {
        // redundant? ErrorLog.log(ErrorLog.SEVERE, "Null rhythm argument.  No lick will be generated.");
        return null;
      }

    lickgen.setProbs(lickgenFrame.readProbs());

    // Fill in a melody according to the provided rhythm.
    // FIX - Currently, the lick generator doesn't support half beats; thus,
    // it can only generate things in terms of number of quarter notes.
    // This is why BEAT is getting passed into the generator.

    MelodyPart lick = lickgenFrame.fillMelody(BEAT, rhythm, chordProg, getCurrentSelectionStart());

    int actualSize = lick.size();
    int desiredSize = score.getLength() - getCurrentSelectionStart() + 1;

    if( actualSize > desiredSize )
      {
        //System.out.println("makeLick: reducing size from " + actualSize + " to desired " + desiredSize);
        lick = lick.extract(0, desiredSize - 1, true);
      }

    return lick;
  }

/**
 * putLick puts the lick into the MelodyPart at the current selection
 *
 * @param lick
 * @return
 */
public boolean putLick(MelodyPart lick)
  {
    //System.out.println("putLick " + lick);

    if( lick == null )
      {
        // redundant ErrorLog.log(ErrorLog.WARNING, "No lick was generated.");
        return true;
      }
    // Figure out which enharmonics to use based on
    // the current chord and key signature.
    setLickEnharmonics(lick);

    // Paste the melody into the stave and play the selection.
    // We turn play off temporarily, or we get an erroneous sound
    // as ImproVisor plays the inserted note at the same time
    // it plays the selection.
    ImproVisor.setPlayEntrySounds(false);


    int start = getCurrentSelectionStart();

    int stop = getCurrentSelectionEnd();

    //System.out.println("putLick into " + start + " " + stop);

    int chorusSize = getChordProg().getSize();

    if( start >= chorusSize || stop >= chorusSize )
      {
        //debug System.out.println("chorus size " + chorusSize + " exceeded, start = " + start + ", stop = " + stop + ", resetting");
        start %= chorusSize;
        stop %= chorusSize;
      }

    // FIX:
    // stop < start does happen. It seems to be due to some kind of data race.
    // Without the return, improvisation will grind to a halt.

    if( stop < start )
      {
        System.out.println("stop, start inverted: start = " + start + ", stop = " + stop + ", resetting");
        start = 0;
        stop = chorusSize - 1;
      }

    Stave stave = getCurrentStave();

    // Ideally, would wait for previous generation to finish before starting
    // a new one, but attempts to do this have been unsuccessful so far.

    getMelodyPart(stave).newPasteOver(lick, getCurrentSelectionStart(stave));

    if( lickgenFrame.rectifySelected() )
      {
        rectifySelection(stave, start, stop);
      }

    playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "putLick " + start + " - " + stop);
    ImproVisor.setPlayEntrySounds(true);
    return true;
  }

// HB - 7-8-13
// Simple fix, worried that if I uncomment rectifySelected() above,
// it might break code elsewhere
public boolean putLickWithoutRectify(MelodyPart lick)
  {
    return putLickWithoutRectify(lick, true);
  }

/**
 * putLick puts the lick into the MelodyPart at the current selection without
 * rectifying
 *
 * @param lick play - whether or not the lick should be played after being
 * placed in.
 * @return
 */
public boolean putLickWithoutRectify(MelodyPart lick, boolean play)
  {
    //System.out.println("putLick " + lick);
    if( lick == null )
      {
        // redundant ErrorLog.log(ErrorLog.WARNING, "No lick was generated.");
        return true;
      }
    // Figure out which enharmonics to use based on
    // the current chord and key signature.
    setLickEnharmonics(lick);

    // Paste the melody into the stave and play the selection.
    // We turn play off temporarily, or we get an erroneous sound
    // as ImproVisor plays the inserted note at the same time
    // it plays the selection.
    ImproVisor.setPlayEntrySounds(false);


    int start = getCurrentSelectionStart();

    int stop = getCurrentSelectionEnd();

    int chorusSize = getChordProg().getSize();

    if( start >= chorusSize || stop >= chorusSize )
      {
        //debug System.out.println("chorus size " + chorusSize + " exceeded, start = " + start + ", stop = " + stop + ", resetting");
        start %= chorusSize;
        stop %= chorusSize;
      }

    // FIX:
    // stop < start does happen. It seems to be due to some kind of data race.
    // Without the return, improvisation will grind to a halt.

    if( stop < start )
      {
        System.out.println("stop, start inverted: start = " + start + ", stop = " + stop + ", resetting");
        start = 0;
        stop = chorusSize - 1;
      }

    Stave stave = getCurrentStave();

    // Formerly used SafePasteCommand, then DynamicPasteCommand, both of which
    // carry unnecessary baggage.

    getMelodyPart(stave).newPasteOver(lick, getCurrentSelectionStart(stave));

    if( play )
      {
        playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "putLick " + start + " - " + stop);
        ImproVisor.setPlayEntrySounds(true);
      }

    return true;
  }

public int getSlotInPlayback()
  {
    return midiSynth.getSlot();
  }

/**
 * generateLick returns a lick generated from a rhythm
 *
 * @param rhythm
 * @return
 */
public MelodyPart generateLick(Polylist rhythm)
  {
    MelodyPart lick = makeLick(rhythm);
    if( lickgenFrame.useHeadSelected() )
      {
        adjustLickToHead(lick);
      }

    return lick;
  }

/**
 * generateLick returns a lick generated from a rhythm to fit start to end
 *
 * @param rhythm
 * @return
 */
public MelodyPart generateLick(Polylist rhythm, int start, int end)
  {
    //System.out.println("generateLick for " + start + " to " + end);
    MelodyPart lick = makeLick(rhythm, start, end);
    if( lickgenFrame.useHeadSelected() )
      {
        adjustLickToHead(lick);
      }

    return lick;
  }

/**
 * Only called from LickgenFrame
 *
 * @param rhythm
 */
public void generateAndPutLick(Polylist rhythm)
  {
    putLick(generateLick(rhythm));
  }

private void adjustLickToHead(MelodyPart lick)
  {
    ArrayList<Score> heads = lickgen.getHeadData();
    Score head = null;
    for( int i = 0; i < heads.size(); i++ )
      {
        //select the head with matching title and length, if there is one
        if( heads.get(i).getTitle().equals(this.getTitle())
                && heads.get(i).getBarsPerChorus() == this.score.getBarsPerChorus() )
          {
            head = heads.get(i);
          }
      }

    //if we don't have the head, leave the lick as it is

    if( head == null )
      {
        setLickGenStatus("No head available for this song");
        return;
      }

    MelodyPart headMelody = head.getPart(0);
    int start = getCurrentSelectionStart();
    int end = getCurrentSelectionEnd();

    //note in lick
    Note n;
    //tracks position in lick
    int position = 0;
    int oldpitch = 0;

    int numChanged = 0;
    int numSame = 0;

    while( lick.getNextNote(position) != null )
      {
        n = lick.getNextNote(position);
        int duration = n.getRhythmValue();
        int pitch = n.getPitch();
        int headPitch = headMelody.getPitchSounding(position + start);
        int nextIndex = lick.getNextIndex(position + n.getRhythmValue() - 1);
        int nextPitch = -10;
        if( lick.getNote(nextIndex) != null )
          {
            nextPitch = lick.getNote(nextIndex).getPitch();
          }

        //don't create repeated notes
        if( headPitch != pitch && headPitch != oldpitch && headPitch != nextPitch )
          {
            if( Math.abs(headPitch - pitch) < 7 && Math.random() < 0.1 && duration >= 60
                    || Math.abs(headPitch - pitch) < 4 && Math.random() < 0.6
                    || Math.abs(headPitch - pitch) < 2 && Math.random() < 0.9 )
              {
                n.setPitch(headPitch);
                numChanged++;
              }
            numSame++;
          }
        else
          {
            numSame++;
          }
        oldpitch = n.getPitch();
        position += n.getRhythmValue() - 1;
      }
    //System.out.println(numChanged + " pitches changed out of " + (numChanged + numSame));
  }

/**
 * Calculate the current lick enharmonics based on the chord progression and
 * they key signature.
 */
private void setLickEnharmonics(MelodyPart lick)
  {
    int index = 0;

    while( index < lick.size() )
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

public void openSaveLickFrame()
  {
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

public String getLickTitle()
  {
    Trace.log(2, "Saved selection title is: " + lickTitle);

    return lickTitle;
  }

    private void saveAsAdviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsAdviceActionPerformed

        if( vocfc == null )
          {
            return;
          }

        vocfc.setDialogTitle("Save Vocabluary As");
        vocfc.setCurrentDirectory(ImproVisor.getVocabDirectory());

        // If never saved before, used the name specified in vocFile.
        // Otherwise use previous file.

        if( savedVocab == null )
          {
            vocfc.setSelectedFile(new File(vocFile));
          }

        vocfc.resetChoosableFileFilters();
        vocfc.addChoosableFileFilter(new VocabFilter());

        if( vocfc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            if( vocfc.getSelectedFile().getName().endsWith(vocabExt) )
              {
                new SaveAdviceCommand(vocfc.getSelectedFile(), adv).execute();

                savedVocab = vocfc.getSelectedFile();
              }
            else
              {
                String file = vocfc.getSelectedFile().getAbsolutePath() + vocabExt;

                savedVocab = new File(file);

                new SaveAdviceCommand(savedVocab, adv).execute();
              }
          }
    }//GEN-LAST:event_saveAsAdviceActionPerformed
    public void saveAdvice()
    {
        saveAdviceActionPerformed(null);
    }
    private void saveAdviceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAdviceActionPerformed

        if( savedVocab != null )
          {
            new SaveAdviceCommand(savedVocab, adv).execute();
          }
        else
          {
            saveAsAdviceActionPerformed(evt);
          }
    }//GEN-LAST:event_saveAdviceActionPerformed

    private void textEntryLosesFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textEntryLosesFocus
        staveRequestFocus();
    }//GEN-LAST:event_textEntryLosesFocus

    private void textEntryGainsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textEntryGainsFocus
        textRequestFocus();
        setNormalStatus();
    }//GEN-LAST:event_textEntryGainsFocus

private void setNormalStatus()
  {
    setMode(Mode.NORMAL);
  }

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

public double getDefaultTempo()
  {
    return Double.parseDouble(Preferences.getPreference(Preferences.DEFAULT_TEMPO));
  }

public void closeAdviceFrame()
  {
    showAdviceButton.setSelected(false);

    showAdviceButton.setBackground(adviceBtnColorClosed);

    adviceFrame.setVisible(false);
    setNormalMode();
    staveRequestFocus();
  }

private void openAdviceFrame()
  {
    showAdviceButton.setSelected(true);

    showAdviceButton.setBackground(adviceBtnColorOpen);

    adviceFrame.setVisible(true);

    //adviceTree.requestFocusInWindow();

    setMode(Mode.ADVICE);
  }

private void redoAdvice()
  {
    Trace.log(2, "redo advice");

    getCurrentStaveActionHandler().redoAdvice(getCurrentSelectionStart());

    getCurrentStave().repaint();

    redrawTriage();
  }

public boolean adviceVisible()
  {
    return adviceFrame.isVisible();
  }

public void updateSelection()
  {
    if( getCurrentStave() == null )
      {
        return;
      }
    totalSlots =
            paddingSlots + getCurrentSelectionEnd() - getCurrentSelectionStart();

    totalBeats = Math.round(totalSlots / roundTo);

    totalSlots = (int) (totalBeats * BEAT);

    if( lickgenFrame != null )
      {
        lickgenFrame.redrawTriage();

        lickgenFrame.setTotalBeats(totalBeats);
      }
    if( stepKeyboard != null )
      {
        stepKeyboard.resetAdvice();
      }
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
            if( !slotIsSelected() )
              {
                // If no slot is selected, force the first slot to be selected.
                getCurrentStave().setSelection(0, 0);
              }

            String enteredText = textEntry.getText();

            if( enteredText.length() > 0 )
              {
                cm.execute(
                        new SetChordsCommand(getCurrentSelectionStart(),
                                             parseListFromString(enteredText),
                                             chordProg,
                                             partList.get(currTabIndex)));
                //update scale degree buttons if first chord changed
                if(getCurrentSelectionStart()==0){
                    guideToneLineDialog.updateButtons();
                }
              }
            
            staveRequestFocus();

            redoAdvice();

            // set the menu and button states

            setItemStates();
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
public boolean saveLeadsheet()
  {
    if( savedLeadsheet != null )
      {
        return saveLeadsheet(savedLeadsheet, score);
      }
    else
      {
        return saveAsLeadsheet();
      }
  }

private boolean saveLeadsheet(File file, Score score)
  {
    SaveLeadsheetCommand s = new SaveLeadsheetCommand(file, score, cm);

    s.execute();

    if( s.getError() instanceof IOException )
      {
        JOptionPane.showMessageDialog(this, "There was an IO Exception during saving:\n" + s.getError().getMessage(), "An error occurred when attmepting to save", JOptionPane.WARNING_MESSAGE);

        return false;
      }

    setMode(Mode.LEADSHEET_SAVED);
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

        ExportToMusicXMLCommand exportCmd = new ExportToMusicXMLCommand(file, score, scoreTab.getSelectedIndex(), getTransposition());
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
    midfc.setCurrentDirectory(ImproVisor.getMidiDirectory());
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
 * Refresh the current Stave.
 */
public void refreshCurrentStave()
  {
    //System.out.println("staveScrollPane = " + staveScrollPane + ", currTabIndex = " + currTabIndex);
    staveScrollPane[currTabIndex].getStave().repaint();
  }

/**
 * Refresh the current StaveScrollpane. ? Doesn't work?
 */
public void refreshCurrentStaveScrollPane()
  {
    //System.out.println("repainting staveScrollPane " + currTabIndex);
    staveScrollPane[currTabIndex].repaint();
  }

/**
 * Give focus to the current Stave.
 */
public void staveRequestFocus()
  {
    // Show that textEntry no longer has focus if it had.

    textEntryLabel.setForeground(Color.red);
    textEntry.setEnabled(false);

    getCurrentStave().requestFocusInWindow();

    setItemStates();
  }

/**
 * This override is intended to fix requestFocusInWindow, which only worked some
 * of the time, for reasons I don't understand.
 *
 * @return
 */
@Override
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

    getCurrentStaveActionHandler().redoAdvice(index);  // unnecessary?
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
 * Set the current Selection start and end.
 *
 */
synchronized void setCurrentSelectionStartAndEnd(int index)
  {
    Stave stave = getCurrentStave();
    stave.setSelectionStart(index);
    stave.setSelectionEnd(index);
  }

/**
 *
 * Indicate whether a slot is current selected or not.
 *
 */
private boolean slotIsSelected()
  {
    Stave stave = getCurrentStave();
    return stave != null && stave.somethingSelected();
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
 * Get the melody part of a specific stave.
 *
 */
public MelodyPart getMelodyPart(Stave stave)
  {
    return stave.getMelodyPart();
  }

/**
 * Get the melody part of the current stave.
 *
 */
public MelodyPart getCurrentMelodyPart()
  {
    return getCurrentStave().getMelodyPart();
  }

int improvMelodyIndex = 0;

public MelodyPart getImprovMelodyPart()
  {
    if( improvMelodyIndex >= staveScrollPane.length )
      {
        return null;
      }
    return staveScrollPane[improvMelodyIndex].getStave().getMelodyPart();
  }

public void incrementMelodyPartIndex()
  {
    improvMelodyIndex++;
    //System.out.println("incrementMelodyPartIndex to " + improvMelodyIndex);
  }

/**
 * Select all construction lines on the current Stave.
 *
 */
    public void selectAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllMIActionPerformed

        selectAll();
    }//GEN-LAST:event_selectAllMIActionPerformed

public void selectAll()
  {
    Trace.log(2, "select all");

    Stave stave = getCurrentStave();

    stave.setSelection(0, stave.getMelodyPart().size() - 1);

    redoAdvice();

    staveRequestFocus();
  }

/**
 * Select all without redoing advice, or requesting
 */
public void selectAll2()
  {
    Stave stave = getCurrentStave();

    stave.setSelection(0, stave.getMelodyPart().size() - 1);
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

public boolean unsavedChanges()
  {

    if( cm.changedSinceLastSave() )
      {
        if( !savedLeadsheetExists() )
          {
            return true;
          }

        // create string representing score
        StringWriter strWriter = new StringWriter();

        try
          {
            BufferedWriter out = new BufferedWriter(strWriter);
            Leadsheet.saveLeadSheet(out, score);
            out.close();
          }
        catch( IOException e )
          {
            return true;
          }

        // read in file
        FileInputStream file;
        try
          {
            file = new FileInputStream(savedLeadsheet);
          }
        catch( FileNotFoundException e )
          {
            return true;
          }

        byte[] b;
        try
          {
            b = new byte[file.available()];
            file.read(b);
            file.close();
          }
        catch( IOException e )
          {
            return true;
          }

        String result = new String(b);

        /**
         * This comparison is a literal comparison, so extra white space or
         * formatting will cause it to fail.
         */
        // file is identical to current score
        if( result.trim().equals(strWriter.toString().trim()) )
          {
            return false;
          }

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

    if( midiImportFrame != null )
      {
        midiImportFrame.dispose();
      }

    WindowRegistry.unregisterWindow(this);
  }

/**
 *
 * Opens the help dialog.
 *
 */
public void openHelpDialog()
  {
    int height = HELP_DIALOG_HEIGHT;

    if( height > getHeight() )
      {
        height = getHeight();
      }

    int width = HELP_DIALOG_WIDTH;

    if( width > getWidth() )
      {
        width = getWidth();
      }

    helpDialog.setSize(width, height);

    helpDialog.setLocationRelativeTo(this);

    helpDialog.setVisible(true);
  }

    private void aboutMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMIActionPerformed
        showAboutDialog();
    }//GEN-LAST:event_aboutMIActionPerformed

/**
 *
 * Opens the About dialog.
 *
 */
public void showAboutDialog()
  {
    aboutDialog.setSize(aboutDialogWidth, aboutDialogHeight);
    aboutDialog.setLocationRelativeTo(this);
    aboutDialog.setVisible(true);
  }

/**
 * * Set the MIDI latency.
 *
 * @param latency
 */
public void setMidiLatency(double latency)
  {

    midiLatencyTF.setText(String.valueOf(latency));
  }

/**
 *
 * Get integer value from string.
 *
 */
public static int intFromString(String string) throws NumberFormatException
  {
    return Integer.decode(string).intValue();
  }

/**
 *
 * Get integer value from string, with range check
 *
 */
static int intFromStringInRange(String string, int low, int high, int error)
  {
    try
      {
        int value = intFromString(string);

        if( value >= low && value <= high )
          {
            return value;
          }

        ErrorLog.log(ErrorLog.COMMENT,
                     "The number you have entered is out of range: must be between "
                + low + " and " + high);

        return error; // range error indicator
      }
    catch( NumberFormatException e )
      {
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
public static int intFromTextField(javax.swing.JTextField field, int low, int high, int error)
  {
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
static double doubleFromString(String string) throws NumberFormatException
  {
    return new Double(string).doubleValue();
  }

/**
 *
 * Get double value from string, with range check
 *
 */
static double doubleFromStringInRange(String string,
                                      double low,
                                      double high,
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
public static double quietDoubleFromStringInRange(String string,
                                                  double low,
                                                  double high,
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
public static double doubleFromTextField(javax.swing.JTextField field,
                                         double low,
                                         double high,
                                         double error)
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
public static double quietDoubleFromTextField(javax.swing.JTextField field,
                                              double low,
                                              double high,
                                              double error)
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
private void savePrefs()
  {
    /*
     * this should be rewritten... should probably validate all data first in
     * separate functions, and then save it... Right now, it validates and saves
     * as it goes.
     */

    boolean closeDialog = true;

    closeDialog = closeDialog && saveGlobalPreferences();

    int tempTabIndex = currTabIndex;

    Rectangle tempView = getCurrentScrollPosition();

    cm.changedSinceLastSave(true);

    // Initialize all the preferences except for the length and metre;

    saveLeadsheetPreferences();

    closeDialog = closeDialog && saveMidiLatency();

    closeDialog = closeDialog && saveMetre();

    closeDialog = closeDialog && saveStylePrefs();

    closeDialog = closeDialog && saveSectionInfo();

    if( closeDialog )
      {
        hideFakeModalDialog(preferencesDialog);

        scoreTab.setSelectedIndex(tempTabIndex);

        setCurrentScrollPosition(tempView);

        requestFocusInWindow();
      }
    this.midiLatencyMeasurement.stop();
  }


/*
 * Midi latency is constant. Audio latency is not on top of midi latency.
 */
private boolean saveMidiLatency()
  {
    double latency = doubleFromTextField(midiLatencyTF, 0, Double.POSITIVE_INFINITY, 0);
    if( midiRecorder != null )
      {
        midiRecorder.setLatency(latency);
      }
    Preferences.setMidiInLatency(latency);
    return true;
  }

/**
 * Saves audio latency in milliseconds.
 *
 * Gets latency in terms of beats from user input, converts based on current
 * tempo.
 *
 * @return
 
private boolean saveAudioLatency()
  {
    System.out.println("Saved audio latency");
    double beatLatency = doubleFromTextField(beatDelayInputBox, 0, Double.POSITIVE_INFINITY, 0);
    SCDelayOffsetter delayOffsetter = new SCDelayOffsetter(this);
    double msLatency = delayOffsetter.gatherDelay(beatLatency);
    Preferences.setAudioInLatency(msLatency);
    audioLatencyRegistered = true;//Have edited audio latency at least once
    return true;
  }
*/
private boolean saveSectionInfo()
  {
    score.getChordProg().setSectionInfo(sectionInfo.copy());

    return true;
  }

private boolean setSectionPrefs()
  {
    try
      {
        int index = sectionTable.getSelectionModel().getLeadSelectionIndex();

        int currentMeasure = sectionInfo.getSectionMeasure(index);

        sectionInfo.adjustSection(index, 0, false, false);
      }
    catch( NumberFormatException e )
      {
        return false;
      }

    return true;
  }

private boolean saveStylePrefs()
  {
    if( sectionTable.getSelectionModel().getLeadSelectionIndex() < 0 )
      {
        sectionTable.getSelectionModel().setSelectionInterval(0, 0);
      }

    SectionRecord record = sectionInfo.getSectionRecordByIndex(
            sectionTable.getSelectionModel().getLeadSelectionIndex());

    if( !record.getUsePreviousStyle() )
      {
        Style style = record.getStyle();
      }
    return true;
  }

/**
 * Called when the StyleList is changed
 */
public void updateStyleList(Style style, int currentIndex)
  {
    if( style != null )
      {
        if( currentIndex < 0 )
          {
            sectionInfo.modifySection(style, 0, false);
          }
        else if( currentIndex >= sectionInfo.size() )
          {
            sectionInfo.modifySection(style, sectionInfo.size() - 1, false);
          }
        else
          {
            sectionInfo.modifySection(style, currentIndex, false);
          }
      }
  }

public int getTimeSigTop()
  {
    return Integer.parseInt(timeSignatureTopTF.getText());
  }

/**
 * Set up the metre and length of the leadsheet. This needs to be separated out
 * because we handle things differently depending on whether we're opening a new
 * leadsheet or just changing the current leadsheet.
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

private boolean saveGlobalPreferences()
  {
    boolean close = true;

    if( enableCache.isSelected() )
      {
        Preferences.setPreference(Preferences.ADV_CACHE_ENABLED, "true");
      }
    else
      {
        Preferences.setPreference(Preferences.ADV_CACHE_ENABLED, "false");
      }

    try
      {
        String strCache = cacheSize.getText();

        String strStyle = defStyleComboBox.getSelectedItem().toString();

        String strMelody = defMelodyInst.getText();

        String strAux = strMelody; // for now defAuxInst.getText();

        String strChord = defChordInst.getText();

        String strBass = defBassInst.getText();

        String strVoicing = voicing.getText();

        String strDist = chordDist.getText();

        numStavesPP = Integer.parseInt(numStavesPerPage.getText());

        int cache = intFromString(strCache.trim());

        double tempo = doubleFromTextField(defaultTempoTF, MIN_TEMPO, MAX_TEMPO, getDefaultTempo());

        int melody = intFromString(strMelody.trim());

        int aux = intFromString(strAux.trim());

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


        if( cache < 0 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Cache size must be a positive number.");

            close = false;
          }
        else
          {
            Advisor.setCacheCapacity(cache);

            Preferences.setPreference(Preferences.ADV_CACHE_SIZE, strCache);
          }


        Preferences.setPreference(Preferences.DEFAULT_STYLE, strStyle);

        Preferences.setPreference(Preferences.DEFAULT_TEMPO, "" + tempo);

        if( melody < 1 || melody > 128 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "The melody instrument must be between 1 and 128");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MELODY_INSTRUMENT, defMelodyInst.getText());
          }

        if( aux < 1 || aux > 128 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "The auxiliary instrument must be between 1 and 128");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_AUX_INSTRUMENT, defAuxInst.getText());
          }



        if( chord < 1 || chord > 128 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "The chord instrument must be between 1 and 128");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_CHORD_INSTRUMENT, defChordInst.getText());
          }



        if( bass < 1 || bass > 128 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "The bass instrument must be between 1 and 128");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_BASS_INSTRUMENT, defBassInst.getText());
          }



        if( voice < 0 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Voicing must be a positive number.");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.MAX_NOTES_IN_VOICING, voicing.getText());
          }



        if( dist < 0 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Chord distance must be a positive number.");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.CHORD_DIST_ABOVE_ROOT, chordDist.getText());
          }



        if( masterVol < 0 || masterVol > 127 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Master volume must be between 0 and 127");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MIXER_ALL, String.valueOf(masterVol));
          }



        if( entryVol < 0 || entryVol > 127 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Entry volume must be between 0 and 127");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MIXER_ENTRY, String.valueOf(entryVol));
          }



        if( chordVol < 0 || chordVol > 127 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Chord volume must be between 0 and 127");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MIXER_CHORDS, String.valueOf(chordVol));
          }



        if( bassVol < 0 || bassVol > 127 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Bass volume must be between 0 and 127");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MIXER_BASS, String.valueOf(bassVol));
          }


        if( drumVol < 0 || drumVol > 127 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Drum volume must be between 0 and 127");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MIXER_DRUMS, String.valueOf(drumVol));
          }


        if( melodyVol < 0 || melodyVol > 127 )
          {
            ErrorLog.log(ErrorLog.SEVERE, "Melody volume must be between 0 and 127");

            close = false;
          }
        else
          {
            Preferences.setPreference(Preferences.DEFAULT_MIXER_MELODY, String.valueOf(melodyVol));
          }
      }
    catch( NumberFormatException e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Saving global preferences: " + e.getMessage());
        e.printStackTrace();
        close = false;
      }

    setStavePreferenceFromButtons();

    Preferences.setPreference(Preferences.DEFAULT_VOCAB_FILE, defVocabFile.getText());

    int visAdvice = 0;

    if( chordTones.isSelected() )
      {
        visAdvice = visAdvice | 1;
      }

    if( colorTones.isSelected() )
      {
        visAdvice = visAdvice | 2;
      }

    if( scaleTones.isSelected() )
      {
        visAdvice = visAdvice | 4;
      }

    if( approachTones.isSelected() )
      {
        visAdvice = visAdvice | 8;
      }

    if( chordSubs.isSelected() )
      {
        visAdvice = visAdvice | 16;
      }

    if( chordExtns.isSelected() )
      {
        visAdvice = visAdvice | 32;
      }

    if( cells.isSelected() )
      {
        visAdvice = visAdvice | 64;
      }

    if( idioms.isSelected() )
      {
        visAdvice = visAdvice | 128;
      }

    if( licks.isSelected() )
      {
        visAdvice = visAdvice | 256;
      }

    if( quotes.isSelected() )
      {
        visAdvice = visAdvice | 512;
      }

    Preferences.setPreference(Preferences.VIS_ADV_COMPONENTS, String.valueOf(visAdvice));

    char[] coloringElts =
      {
        '1', '1', '1', '1'
      };


    if( redChordBtn.isSelected() )
      {
        coloringElts[0] = '2';
      }

    if( greenChordBtn.isSelected() )
      {
        coloringElts[0] = '3';
      }

    if( blueChordBtn.isSelected() )
      {
        coloringElts[0] = '4';
      }

    if( redColorBtn.isSelected() )
      {
        coloringElts[1] = '2';
      }

    if( greenColorBtn.isSelected() )
      {
        coloringElts[1] = '3';
      }

    if( blueColorBtn.isSelected() )
      {
        coloringElts[1] = '4';
      }

    if( redApproachBtn.isSelected() )
      {
        coloringElts[2] = '2';
      }

    if( greenApproachBtn.isSelected() )
      {
        coloringElts[2] = '3';
      }

    if( blueApproachBtn.isSelected() )
      {
        coloringElts[2] = '4';
      }

    if( redOtherBtn.isSelected() )
      {
        coloringElts[3] = '2';
      }

    if( greenOtherBtn.isSelected() )
      {
        coloringElts[3] = '3';
      }

    if( blueOtherBtn.isSelected() )
      {
        coloringElts[3] = '4';
      }

    String coloring = new String(coloringElts);

    Preferences.setPreference(Preferences.NOTE_COLORING, coloring);

    /*
     * Drawing preferences
     */

    char[] drawTones =
      {
        '1', '1', 'x'
      };


    if( !drawScaleTonesCheckBox.isSelected() )
      {
        drawTones[0] = 'x';
      }

    if( !drawChordTonesCheckBox.isSelected() )
      {
        drawTones[1] = 'x';
      }

    if( drawColorTonesCheckBox.isSelected() )
      {
        drawTones[2] = '1';
      }

    String tonePrefs = new String(drawTones);

    Preferences.setPreference(Preferences.DRAWING_TONES, tonePrefs);

    Preferences.setPreference(Preferences.SHOW_TRACKING_LINE, Preferences.booleanToYesNo(trackCheckBox.isSelected()));

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
    
    //update guide tone line range to match stave
    guideToneLineDialog.updateRange();
  }

public void setTrackerDelay(String text)
  {
    trackerDelay = doubleFromStringInRange(text, -Double.MAX_VALUE, +Double.MAX_VALUE, 0);
    trackerDelayTextField.setText(text);
    trackerDelayTextField2.setText(text);
  }

public static final int DEFAULT_CHORD_FONT_SIZE_VALUE = 16;

/**
 * Ensure that there is a chord-font-size preference. If there wasn't one,
 * create one.
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

/**
 * Set up the preferences dialog box.
 */
private void setPrefsDialog()
  {
    // System.out.println("setPrefsDialog");

    // ===== update Midi panel

    refreshMidiStatus();

    // ===== update Global panel

    enableCache.setSelected(!Preferences.getPreference(Preferences.ADV_CACHE_ENABLED).equals("false"));

    cacheSize.setText(Preferences.getPreference(Preferences.ADV_CACHE_SIZE));

    defaultTempoTF.setText(Preferences.getPreference(Preferences.DEFAULT_TEMPO));

    voicing.setText(Preferences.getPreference(Preferences.MAX_NOTES_IN_VOICING));

    chordDist.setText(Preferences.getPreference(Preferences.CHORD_DIST_ABOVE_ROOT));

    // nuisance: ensureChordFontSize();

    setTrackerDelay(Preferences.getPreference(Preferences.TRACKER_DELAY));

    defStyleComboBox.setSelectedItem(Advisor.getStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE)));

    defStyleComboBox.repaint();

    defBassInst.setText(Preferences.getPreference(Preferences.DEFAULT_BASS_INSTRUMENT));

    defChordInst.setText(Preferences.getPreference(Preferences.DEFAULT_CHORD_INSTRUMENT));

    defMelodyInst.setText(Preferences.getPreference(Preferences.DEFAULT_MELODY_INSTRUMENT));

    try
      {
        defAuxInst.setText(Preferences.getPreferenceQuietly(Preferences.DEFAULT_AUX_INSTRUMENT));
      }
    catch( NonExistentParameterException e )
      {
        defAuxInst.setText(Preferences.DAI_VAL);
      }


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

    // ===== update Style table

    if( sectionInfo != null )
      {
        Style style = sectionInfo.getStyle(0);
        if( style != null )
          {
            styleList.setSelectedValue(style, true);
          }
      }
    sectionTableModel.tableReset();

    // ===== update Leadsheet panel

    // display the current part title

    partTitleTF.setText(getCurrentStave().getPartTitle());


    // display the composer

    composerField.setText(score.getComposer());


    // display the part composer

    partComposerTF.setText(getCurrentMelodyPart().getComposer());

    String inst;

    if( alwaysUseMelody.isSelected() )
      {
        inst = Preferences.getPreference(Preferences.DEFAULT_MELODY_INSTRUMENT);
        int instNumber = midiInstFromText(inst, 1);
        getCurrentMelodyPart().setInstrument(instNumber);

        melodyInst.setText(inst);
        //System.out.println("assigning melody instrument from default: " + inst);
      }
    else
      {
        melodyInst.setText("" + (getCurrentMelodyPart().getInstrument() + 1));
        //System.out.println("assigning melody instrument from score: " + inst);
      }

    try
      {
        auxInst.setText(Preferences.getPreferenceQuietly(Preferences.DEFAULT_AUX_INSTRUMENT));
      }
    catch( NonExistentParameterException e )
      {
        auxInst.setText(Preferences.DAI_VAL);
      }


    // display the current score title

    scoreTitleTF.setText(score.getTitle());

    // display score comments

    commentsTF.setText(score.getComments());

    // display the score tempo

    tempoTF.setText("" + score.getTempo());

    keySignatureTF.setText("" + getCurrentMelodyPart().getKeySignature());

    timeSignatureTopTF.setText("" + getCurrentMelodyPart().getMetre()[0]);

    timeSignatureBottomTF.setText("" + getCurrentMelodyPart().getMetre()[1]);

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

    StaveType tempStaveType;

    // If "always use this" is selected, over-ride with default stave preference

    if( alwaysUseStave.isSelected() )
      {
        tempStaveType = Preferences.getStaveTypeFromPreferences();
      }
    else
      {
        tempStaveType = getCurrentMelodyPart().getStaveType();
      }
    setCurrentStaveType(tempStaveType);
    if( numStavesPP == 0 )
      {
        numStavesPerPage.setText(Preferences.DEFAULT_STAVES_PER_PAGE);
      }
    else
      {
        numStavesPerPage.setText(Integer.toString(numStavesPP));
      }
    sectionTableModel.tableRefresh();
    sectionTable.getSelectionModel().setSelectionInterval(0, 0);
  }

public void setRoadMapCheckBox(boolean value)
  {
    //System.out.println("setRoadMapCheckBox " + roadMap);
    createRoadMapCheckBox.setSelected(value);
  }

public int getBreakpoint()
  {
    return score.getBreakpoint();
  }

private void setFreezeLayout(Boolean frozen)
  {
    if( frozen )
      {
        freezeLayoutButton.setText("<html><center>Thaw<br>Layout</center></html>");

        freezeLayoutButton.setBackground(Color.RED);
      }
    else
      {
        score.setLayoutList(Polylist.nil);

        freezeLayoutButton.setText("<html><center>Freeze<br>Layout</center></html>");

        freezeLayoutButton.setBackground(Color.GREEN);
      }
  }

private void toggleFreezeLayout()
  {
    String current = getLayoutTF();

    current = current.trim();

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

    getCurrentMelodyPart().setComposer(partComposerTF.getText());

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
    getCurrentMelodyPart().setInstrument(instSetting);


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

    setKeySignature(keyNumberFromText(keySignatureTF.getText(), 0));


    // set the new breakpoint

    int proposedBreakpoint;

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

    Polylist layout = parseListFromString(getLayoutTF());

    if( layout.nonEmpty() )
      {
        adjustLayout(layout);

        setFreezeLayout(true);
      }

    // set the menu and button states

    setItemStates();

    repaint();

    // For illustration purposes:

    // ChordPart chordPart = score.getChordProg();

    // System.out.println("chord symbols: " + chordPart.getChordSymbols());
    // System.out.println("chord durations: " + chordPart.getChordDurations());
  }

public void setKeySignature(int key)
  {
    for( StaveScrollPane s : staveScrollPane )
      {
        if( s != null )
          {
            s.setKeySignature(key);
          }
      }
    score.setKeySignature(key);
  }

/**
 *
 * Get an external midi instrument number 1=128 from text If not possible, or
 * the number is out of range, an error message given and the default instrument
 * number is used. Both numbers should be 1 greater than the internal values.
 *
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
 * Get a key number from text If not possible, or the number is out of range, an
 * error message given and the default key number is used.
 *
 * @param text
 * @param defaultInstNumber
 * @return
 */
public static int keyNumberFromText(String text, int defaultKey)
  {
    // External MIDI numbers are 1 greater than the values used here.

    String correctFormat = text.trim();
    if( correctFormat.length() == 2 )
      {
        if( correctFormat.substring(0, 1).equals("+") )
          {
            correctFormat = correctFormat.substring(1);
          }
      }
    try
      {
        int value = Integer.decode(correctFormat);
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

/**
 * Set up the metre and length of the leadsheet: top and bottom are the
 * numerator and denominator of the time signature, and opened is a flag that
 * tells whether we're setting the metre from a file or from the preferences
 * dialogue box.
 */
public void initMetreAndLength(int top, int bottom, boolean opened)
  {

    beatValue = ((BEAT * 4) / bottom);

    measureLength = beatValue * top;

    // If we set this from a file, we basically want to ignore everything in

    // the preferences box, and set those up from the file.

    if( opened )
      {

        int scoreBarLength = score.getLength() / measureLength;

        setBars(scoreBarLength);

        setTotalMeasures(scoreBarLength);
      }
    // However, if we change things from the preferences box, we want to update
    // the score length so it matches.
    else
      {

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

    if( layout == null || layout.isEmpty() /* || noLockedMeasures() */ )
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

    int currentMeasures = staveScrollPane[0].getStave().getNumMeasures();

    //int measureArray[] = staveScrollPane[0].getStave().getLineMeasures();
    /*
     int measureArray[] = lockedMeasures;

     for( int k = measureArray.length - 1; k >= 0; k-- )
     {

     currentMeasures += measureArray[k];
     }

     */

    // Now determine the size of the new array.

    // This has to be enough to accomodate the current measures,
    // and the number of elements has to conform to the list
    // specification.  If there are more elements than specified
    // in the list, the last entry in the list is used as the size
    // for all remaining lines.  So obviously the list has to have
    // at least one element.  There should not be any zero or
    // negative elements.

    int measuresLeft = score.getBarsPerChorus(); //currentMeasures;

    int arrayElements;

    Polylist T = layout;

    //System.out.println("setting layout using " + layout + " measuresLeft = " + measuresLeft);

    int lastLineLength = 0;

    int thisLineLength;

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

    //System.out.println("arrayElements = " + arrayElements);

    int newLockedMeasures[] = new int[arrayElements];

    // Finally populate the new array

    measuresLeft = score.getBarsPerChorus(); //;

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
    catch( Error e )
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

/**
 * FIX: Not sure if this logic is correct. Locked measures sometimes ends up
 * being longer than necessary. This should be checked, although it seems
 * harmless at the moment.
 *
 * @param measures
 */
private void setTotalMeasures(int measures)
  {
    if( measures == 0 || noLockedMeasures() )
      {
        return;
      }

    int[] tempLockedMeasures = new int[lockedMeasures.length
            + (measures - lockedMeasures.length) / defaultMeasPerLine];

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
 * Clear the layout preferences for the leadsheet.
 *
 * @param layout
 */
private void clearLayoutPreference()
  {
    setLayoutTF("");
  }

/**
 * Set the layout preferences for the leadsheet.
 *
 * @param layout
 */
private void setLayoutPreference(Polylist layout)
  {
    //System.out.println("setLayoutPreference " + layout);

    if( layout != null )
      {
        StringBuilder buffer = new StringBuilder();

        while( layout.nonEmpty() )
          {
            buffer.append(layout.first());
            buffer.append(" ");

            layout = layout.rest();
          }

        setLayoutTF(buffer.toString());
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


/*
 * Checks to see if the tabs in scoreTab have changed states, and sets the
 * current tabbed index if so.
 */
    private void scoreTabStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_scoreTabStateChanged

        if( scoreTab.getSelectedIndex() != OUT_OF_BOUNDS )
          {

            // Get the previous Stave panel's location

            staveScrollPane[scoreTab.getSelectedIndex()].
                    setBGlocation(
                    staveScrollPane[currTabIndex].getBGlocation().x,
                    staveScrollPane[currTabIndex].getBGlocation().y);



            // update the current tab index
            currTabIndex = scoreTab.getSelectedIndex();

            //lickgenFrame.stavesChanged(currTabIndex); //make sure to update which tab the complexity window is looking at

            /* If the tab gets changed during playback, disable autoscrolling
             * since the playback indicator is no longer on the screen
             */

            if( !playingStopped() )
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

            int value = midiSynth.getMasterVolume();

            setSliderVolumes(value);

            melodyVolume.setValue(getScore().getMelodyVolume());

            chordVolume.setValue(getScore().getChordVolume());

            drumVolume.setValue(getScore().getDrumVolume());

            bassVolume.setValue(getScore().getBassVolume());


            // set the menu and button states

            setItemStates();

          }
        
        //UPDATE TRANSFORM BUTTONS IN TRANSFORM PANEL AND GUIDETONELINEDIALOG
        this.lickgenFrame.getTransformPanel().updateButtons();
        if(guideToneLineDialog!=null){
            guideToneLineDialog.updateTransformButtons();
        }
        
    }//GEN-LAST:event_scoreTabStateChanged

public Rectangle getCurrentScrollPosition()
  {

    return staveScrollPane[currTabIndex].getViewport().getViewRect();
  }

public void setCurrentScrollPosition(Rectangle r)
  {
    //System.out.println("setCurrentScrollPosition(" + r + ")");
    int maxwidth = staveScrollPane[currTabIndex].getStave().getPanelWidth();

    int curwidth = (int) staveScrollPane[currTabIndex].getViewport().getExtentSize().getWidth();

    if( r.x + curwidth >= maxwidth )
      {

        r.x = maxwidth - curwidth;
      }

    if( r.x < 0 )
      {
        r.x = 0;
      }

    staveScrollPane[currTabIndex].getViewport().setViewPosition(r.getLocation());
  }

/**
 *
 * Adds a new, blank tabbed part to the score.
 *
 */
    private void addTabMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabMIActionPerformed
        addTab();
    }//GEN-LAST:event_addTabMIActionPerformed

public void addTab()
  {
    int length = score.getLength();
    MelodyPart mp = new MelodyPart(length);
    mp.setInstrument(score.getPart(0).getInstrument());
    addChorus(mp);
    cm.changedSinceLastSave(true);
  }

public void addChorus(MelodyPart mp)
  {
    Stave s = getCurrentStave();

    int progSize = score.getChordProg().size();
    int keySig = s.getKeySignature();

    if( mp.size() < progSize )
      {
        // Pad the new melody part with rests so that there is no gap.
        mp.addRest(new Rest(progSize - mp.size()));
      }
    else if( progSize < mp.size() )
      {
        // Set the size of the inserted part to match other parts.
        mp.setSize(progSize);
        //score.setLength(mp.size());
        //setBars(score.getBarsPerChorus());
      }

    partList.add(mp);
    score.addPart(mp);

    // reset the current scoreFrame

    setupArrays();

    getCurrentStave().setKeySignature(keySig);

    getCurrentStave().repaint();
  }

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
public void resetAdviceUsed()
  {

    if( adviceUsed )
      {

        Trace.log(2, "adviceUsed set to false");

        adviceUsed = false;
      }
  }

/**
 *
 * Set the advice undo memory.
 *
 */
public void setAdviceUsed()
  {
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

                playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "adviceTreeKeyPressed");
                break;

            case KeyEvent.VK_UP:
                up = true;

            case KeyEvent.VK_DOWN:
              {
                DefaultMutableTreeNode node =
                        (DefaultMutableTreeNode) adviceTree.getSelectionPath().getLastPathComponent();

                TreeNode parent = node.getParent();

                Enumeration<DefaultMutableTreeNode> e = parent.children();

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
            generateFromButton();
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

      }
    staveRequestFocus();
  }

/**
 * Behavior on key stroke when neither shift nor control key is held down.
 * Currently called only from within adviceKeyPressed(KeyEvent e)
 */
public void nothingDownBehavior(KeyEvent e)
  {
    switch( e.getKeyCode() )
      {
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

        case KeyEvent.VK_S:
            getCurrentStave().transposeMelodyDownHarmonically();
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

        case KeyEvent.VK_W:
            getCurrentStave().transposeMelodyUpHarmonically();
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
            /* Now handled by accelerator
             getCurrentStave().playSelection(false, 0);
             */
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
 * Key pressed in advice menu. Note that this replicates some functions
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
        if( e.getKeyChar() == java.awt.event.KeyEvent.VK_ENTER )
          {
            playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "adviceKeyPressed");
          }
        else if( e.isControlDown() )
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

public void toggleBothEnharmonics()
  {
    toggleChordEnharmonics();
    toggleMelodyEnharmonics();
    getCurrentStave().repaint();
  }

public void toggleChordEnharmonics()
  {
    cm.execute(new ToggleEnharmonicCommand(
            getCurrentStave().getChordProg(),
            getCurrentSelectionStart(),
            getCurrentSelectionEnd()));
  }

/**
 * Toggle enharmonics of a group of notes
 */
public void toggleMelodyEnharmonics()
  {
    cm.execute(new ToggleEnharmonicCommand(
            getCurrentMelodyPart(),
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

    int[] metre = getCurrentMelodyPart().getMetre();

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
                                      getCurrentMelodyPart()));
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
                (DefaultMutableTreeNode) adviceTree.getLastSelectedPathComponent();

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

        ((Advice) object).insertInPart(score.getPart(currTabIndex),
                                       getCurrentSelectionStart(), cm, this);

        if( ImproVisor.getPlay() )
          {
            ImproVisor.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS);
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

public static Polylist parseListFromString(String input)
  {

    Tokenizer in = new Tokenizer(new StringReader(input));

    Object ob;

    Polylist result = Polylist.nil;

    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
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

        if( !chordText.equals("") )
          {
            Trace.log(2, "Entering chords and melody: " + chordText);

            cm.execute(new SetChordsCommand(
                    getCurrentSelectionStart(),
                    parseListFromString(chordText),
                    chordProg,
                    partList.get(currTabIndex)));
            
            //update scale degree buttons if first chord changed
            if(getCurrentSelectionStart()==0){
                guideToneLineDialog.updateButtons();
            }

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

    if( slotIsSelected() )
      {
        String windowText = textEntry.getText();

        if( !windowText.equals("") )
          {
            Trace.log(2, "Entering chords from: " + windowText);

            cm.execute(new SetChordsCommand(
                    getCurrentSelectionStart(),
                    parseListFromString(windowText),
                    chordProg,
                    null));

            //update scale degree buttons if first chord changed
            if(getCurrentSelectionStart()==0){
                guideToneLineDialog.updateButtons();
            }
            
            redoAdvice();
          }
      }
  }

/**
 *
 * Enter melody only from text field
 *
 */
void enterMelody()
  {
    if( slotIsSelected() )
      {
        String windowText = textEntry.getText();

// The difference between this and the previous command is found
// in how the arguments to the SetChordCommands constructor are
// handled.  It is what separates chords from melody.

        if( !windowText.equals("") )
          {
            Trace.log(2, "Entering melody from: " + windowText);

            cm.execute(new SetChordsCommand(
                    getCurrentSelectionStart(),
                    parseListFromString(windowText),
                    null,
                    partList.get(currTabIndex)));
            
            //update scale degree buttons if first chord changed
            if(getCurrentSelectionStart()==0){
                guideToneLineDialog.updateButtons();
            }
            
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

        if( oneSlotSelected() )
          {
            int slot = getCurrentSelectionStart();
            displayAdviceTree(slot, 0, getCurrentStave().getMelodyPart().getNote(slot));
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

void pasteMelody()
  {
    Trace.log(2, "paste melody");

    // paste notes

    if( impro.melodyClipboardNonEmpty() )
      {
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
                                        getMelodyPart(stave),
                                        getCurrentSelectionStart(stave),
                                        !alwaysPasteOver, true, this));
        justPasted = true;

        paintCurrentStaveImmediately("pasteMelody");

        // set the menu and button states

        setItemStates();
      }
  }

public void pasteMelody(Part part)
  {
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

void pasteChords()
  {
    Trace.log(2, "paste chords");

    if( slotIsSelected() )
      {
        // paste chords

        if( impro.chordsClipboardNonEmpty() )
          {
            cm.execute(new SafePasteCommand(impro.getChordsClipboard(),
                                            getCurrentStave().getChordProg(),
                                            getCurrentSelectionStart(), !alwaysPasteOver, true, this));
          }

        paintCurrentStaveImmediately("paste Chords");

        // set the menu and button states
        redoAdvice();
        setItemStates();
      }
  }

/**
 *
 * Copy the selection of notes from the Stave to the clipboard
 *
 */
    public void copyMelodyMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMelodyMIActionPerformed

        copyMelody();
    }//GEN-LAST:event_copyMelodyMIActionPerformed

void copyMelody()
  {
    Trace.log(2, "copy melody");

    if( slotIsSelected() )
      {
        cm.execute(new CopyCommand(getCurrentMelodyPart(),
                                   impro.getMelodyClipboard(),
                                   getCurrentSelectionStart(),
                                   getCurrentSelectionEnd()));

        getCurrentStave().setPasteFromStart(getCurrentSelectionStart());

        getCurrentStave().setPasteFromEnd(getCurrentSelectionEnd());

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
        cm.execute(new CutCommand(getCurrentMelodyPart(),
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

        cm.execute(new ReverseCommand(getCurrentMelodyPart(),
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
        cm.execute(new InvertCommand(getCurrentMelodyPart(),
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

        cm.execute(new TimeWarpCommand(getCurrentMelodyPart(),
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

        redoCommand();
    }//GEN-LAST:event_redoMIActionPerformed

/**
 *
 * Undo the previous command
 *
 */
    public void undoMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMIActionPerformed

        undoCommand();
    }//GEN-LAST:event_undoMIActionPerformed

public void undoCommand()
  {
    Trace.log(2, "undo command in Notate");

    cm.undo();

    redoAdvice();

    // set the menu and button states

    setItemStates();
  }

public void redoCommand()
  {
    Trace.log(2, "redo command");

    cm.redo();

    redoAdvice();

    // set the menu and button states

    setItemStates();
  }

/**
 *
 * Shows the construction lines when the user's mouse is over a measure
 *
 */
    private void showBracketsCurrentMeasureMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBracketsCurrentMeasureMIActionPerformed

        for( int i = 0; i < staveScrollPane.length; i++ )
          {
            Stave stave = staveScrollPane[i].getStave();
            stave.setShowMeasureCL(!stave.getShowMeasureCL());

            stave.repaint();
          }
    }//GEN-LAST:event_showBracketsCurrentMeasureMIActionPerformed

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


        PrintUtilities.printComponent(getCurrentStave(),
                                      staveScrollPane[currTabIndex].getNumLines(),
                                      numStavesPP,
                                      grandStaveBtn.isSelected());

        // printAllStaves();

        getCurrentStave().setShowAllCL(tempShowAllCL);

        getCurrentStave().setShowMeasureCL(tempShowMeasureCL);

        getCurrentStave().setSelection(tempStartIndex);

        getCurrentStave().setPrinting(false);

        getCurrentStave().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_printMIActionPerformed

/**
 * Print all staves, rather than just the current one.
 */
private void printAllStaves()
  {
    // Make array of staves, then print each one.

    Stave component[] = new Stave[staveScrollPane.length];
    for( int i = 0; i < staveScrollPane.length; i++ )
      {
        component[i] = staveScrollPane[i].getStave();
      }
    Boolean bracketsAll[] = new Boolean[staveScrollPane.length];
    Boolean bracketsMeasure[] = new Boolean[staveScrollPane.length];
    for( int i = 0; i < staveScrollPane.length; i++ )
      {
        bracketsAll[i] = component[i].getShowAllCL();
        bracketsMeasure[i] = component[i].getShowMeasureCL();
        setUpStavesToPrint(component[i]);
      }
    try
      {
        PrintUtilities.printMultipleComponents(component,
                                               staveScrollPane[currTabIndex].getNumLines(),
                                               numStavesPP,
                                               grandStaveBtn.isSelected());
      }
    catch( OutOfMemoryError e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "Not enough memory to print this many choruses, try printing each chorus individually");
      }
    for ( int i = 0; i < staveScrollPane.length; i++ )
      {
        staveScrollPane[i].getStave().setShowAllCL(bracketsAll[i]);
        staveScrollPane[i].getStave().setShowMeasureCL(bracketsMeasure[i]);
      }
    
  }

private void setUpStavesToPrint(Stave stv)
  {
    stv.setShowAllCL(false);

    stv.setShowMeasureCL(false);

    stv.setSelection(OUT_OF_BOUNDS);

    stv.setPrinting(true);
  }

/**
 *
 * Display the construction lines on the score if checked in the menu
 *
 */
    private void showBracketsAllMeasuresMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showBracketsAllMeasuresMIActionPerformed

        for( int i = 0; i < staveScrollPane.length; i++ )
          {
            Stave stave = staveScrollPane[i].getStave();

            stave.setShowAllCL(!stave.getShowAllCL());

            stave.repaint();
          }
    }//GEN-LAST:event_showBracketsAllMeasuresMIActionPerformed

/**
 *
 * Display the bar numbers on the score if checked in the menu
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
 * Display the title of the score if checked in the menu
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
 * Stop the playback of the score if selected
 *
 */
    public void stopPlayMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopPlayMIActionPerformed

        stopPlaying("stop Play MI");

    }//GEN-LAST:event_stopPlayMIActionPerformed

/**
 *
 * Display the score in a particular
 * <code>type</code> --grand, treble, or
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

        getCurrentMelodyPart().setStaveType(StaveType.AUTO);

        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }//GEN-LAST:event_autoStaveMIActionPerformed

private void setCurrentStaveType(StaveType t)
  {
    //System.out.println("setCurrentStaveType(" + t + ")");

    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    Stave stave = getCurrentStave();
    StaveActionHandler handler = stave.getActionHandler();
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
    getCurrentMelodyPart().setStaveType(t);
    if( useNoteCursor )
      {
        handler.setCursor(handler.getNoteCursor());
      }
    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
  }

/**
 *
 * Display the score as a grand stave
 *
 */
    private void grandStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grandStaveMIActionPerformed
        setCurrentStaveType(StaveType.GRAND);
    }//GEN-LAST:event_grandStaveMIActionPerformed

/**
 *
 * Display the score in bass clef format
 *
 */
    private void bassStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassStaveMIActionPerformed
        setCurrentStaveType(StaveType.BASS);
    }//GEN-LAST:event_bassStaveMIActionPerformed

/**
 *
 * Display the score in treble clef format
 *
 */
    private void trebleStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trebleStaveMIActionPerformed
        setCurrentStaveType(StaveType.TREBLE);
    }//GEN-LAST:event_trebleStaveMIActionPerformed

boolean firstChorus = true;

private void setFirstChorus(boolean value)
  {
    firstChorus = value;
  }

public boolean getFirstChorus()
  {
    return firstChorus;
  }

/**
 *
 * Plays the score until the score's end
 *
 */
    public void playAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playAllMIActionPerformed

        playAll();
    }//GEN-LAST:event_playAllMIActionPerformed

public void playAll()
  {
    if( keyboard != null && keyboard.isVisible() )
      {
        keyboard.setPlayback(true);
      }
    setFirstChorus(true);
    playScore();

  }

public void establishCountIn()
  {
    boolean countInSelected = countInCheckBox.isSelected();
    score.setCountIn(countInSelected ? makeCountIn() : null);
  }

public void noCountIn()
  {
    score.noCountIn();
  }

int slotDelay;
boolean continuousImprovisation = true; // Box is initially checked.

/**
 * playScore() calls either improviseContinuously() or playScoreBody(0)
 * depending on whether improvisation is on.
 */
public void playScore()
  {
    slotDelay =
            (int) (midiSynth.getTotalSlots() * (1e6 * trackerDelay / midiSynth.getTotalMicroseconds()));

    totalSlots = midiSynth.getTotalSlots();

    //System.out.println("slotDelay = " + slotDelay + ", totalSlots = " + totalSlots);

    totalSlotsElapsed = 0;
    previousSynthSlot = 0;

    improvMelodyIndex = 0;
    if( improvisationOn )
      {
            if (themeWeave)
                {   
                    if (themeWeaver == null)
                    {
                        themeWeaver = new ThemeWeaver(lickgen, this, cm);
                    }
                    themeWeaver.generateThemeWovenSolo();
                }
            else{
        improviseContinuously();}
      }
    else
      {
        establishCountIn();
        playScoreBody(0);
      }
  }

public void playScore(Style style)
  {
    slotDelay =
            (int) (midiSynth.getTotalSlots() * (1e6 * trackerDelay / midiSynth.getTotalMicroseconds()));

    totalSlots = midiSynth.getTotalSlots();

    //System.out.println("slotDelay = " + slotDelay + ", totalSlots = " + totalSlots);

    totalSlotsElapsed = 0;
    previousSynthSlot = 0;

    improvMelodyIndex = 0;

    establishCountIn();
    playScoreBody(0, style);
  }

/**
 * playScore plays the Score at a designated starting point, without a countin
 *
 * @param startAt
 */
public void playScore(int startAt)
  {
    noCountIn();
    playScoreBody(startAt);
  }

/**
 * playScoreBody plays the Score at a designated starting point. It calls
 * playSelection in the current Stave. which executes a PlayScore command. The
 * PlayScore command calls the play method on a MidiSynth that is passed to the
 * command constructor. The midiSynth is obtained from this Notate using
 * getMidiSynth().
 *
 * @param startAt
 */
public void playScoreBody(int startAt)
  {
    if( playingPaused() )
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

        if( playingStopped() )
          {
            // possible loss of precision below: check this
            startAt = (int) playbackManager.getMicrosecondsFromSlider();

            clearKeyboard();
            clearVoicingEntryTF();
            resetChordDisplay();
          }

        // reset playback offset

        initCurrentPlaybackTab(0, 0);
        //sets up a Timer to handle audio capture
        if( useAudioInputMI.isSelected() )
          {
            setMode(Mode.RECORDING);
//            startAudioTimer();
//            System.out.println("Capture timer started.");
          }

        getStaveAtTab(0).playSelection(startAt, score.getTotalLength() - 1, getLoopCount(), true, "playScoreBody");
        //getCurrentStave().play(startAt);
      }
    setMode(Mode.PLAYING);
  }

public void playScoreBody(int startAt, Style style)
  {
    if( playingPaused() )
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

        if( playingStopped() )
          {
            // possible loss of precision below: check this
            startAt = (int) playbackManager.getMicrosecondsFromSlider();

            clearKeyboard();
            clearVoicingEntryTF();
            resetChordDisplay();
          }

        // reset playback offset

        initCurrentPlaybackTab(0, 0);
        //sets up a Timer to handle audio capture
        if( useAudioInputMI.isSelected() )
          {
            setMode(Mode.RECORDING);
//            startAudioTimer();
//            System.out.println("Capture timer started.");
          }

        getStaveAtTab(0).playSelection(startAt, score.getTotalLength() - 1, getLoopCount(), true, "playScoreBody", style);
        //getCurrentStave().play(startAt);
      }
    setMode(Mode.PLAYING);
  }

/**
 * Play a score, not necessarily the one in this Notate window. Use the
 * "no-style" style, with no looping.
 */
public void playAscore(Score score)
  {
    playAscore(score, 0);
  }

/**
 * Play a score, not necessarily the one in this Notate window. Use the
 * "no-style" style.
 */
public void playAscore(Score score, int loopCount)
  {
    playAscore(score, "no-style", loopCount);
  }

/**
 * Play a score, not necessarily the one in this Notate window. Use the
 * "no-style" style.
 */
public void playAscoreInCurrentStyle(Score score, int loopCount)
  {
    playAscore(score, getCurrentStyle().getName(), loopCount);
  }

/**
 * Play a score with a specified style, not necessarily the one in this Notate
 * window.
 */
public void playAscore(Score score, String style, int loopCount)
  {
    score.setStyle(style);

    int volume = allVolumeToolBarSlider.getValue();

    int startTime = 0;
    boolean swing = true;
    int transposition = 0;
    boolean useDrums = true;
    int endLimitIndex = -1; // score.getLength()-1;
    //System.out.println("playing score of length " + score.getLength());

    if( midiSynth3 == null )
      {
        //midiSynth3 = midiSynth;
        midiSynth3 = new MidiSynth(midiManager);
      }

    midiSynth3.setMasterVolume(volume);

    new PlayScoreCommand(score,
                         startTime,
                         swing,
                         midiSynth3,
                         this,
                         loopCount,
                         transposition,
                         useDrums,
                         endLimitIndex).execute();
  }

/**
 * Plays a score using the style and tempo of the score
 *
 * @param score
 * @param loopCount
 */
public void playAscoreWithStyle(Score score, int loopCount)
  {
    int volume = allVolumeToolBarSlider.getValue();
    int startTime = 0;
    boolean swing = true;
    int transposition = 0;
    boolean useDrums = true;
    int endLimitIndex = -1; // score.getLength()-1;
    //System.out.println("playing score of length " + score.getLength());

    if( midiSynth3 == null )
      {
        midiSynth3 = new MidiSynth(midiManager);
      }

    midiSynth3.setMasterVolume(volume);

    new PlayScoreCommand(score,
                         startTime,
                         swing,
                         midiSynth3,
                         this,
                         loopCount,
                         transposition,
                         useDrums,
                         endLimitIndex).execute();
  }

public void stopPlayAscore()
  {
    midiSynth3.stop("stopPlayAscore called");
    stopPlaying("stopPlayAscore called");
  }

/**
 * Construct count-in pattern if one is specified
 */
public ChordPart makeCountIn()
  {
    String instrument[] =
      {
        "High_Tom", "Side_Stick"
    // possible alternates:
    // "Low_Bongo", "Hi_Bongo"
    // "Acoustic_Bass_Drum", "Acoustic_Snare"
      };

    double tempo = score.getTempo();

    int[] metre = score.getMetre();

    int measures = 2;

    int beatsInMeasure = metre[0];

    int oneBeat = metre[1];

    double tempoFactor = oneBeat / 4.0;

    double apparentTempo = tempo * tempoFactor;

    StyleEditor se = getStyleEditor();

    DrumPatternDisplay drumPattern = new DrumPatternDisplay(this, cm, se);

    // Treat 4/4 as a special case, for jazz-style count-in, 2-bar pattern:
    // 1-2-1234

    if( beatsInMeasure == 4 && oneBeat == 4 )
      {
        String pattern1 = "X4 R4 X4 R4 X4 X4 X4 X4";
        DrumRuleDisplay drumRule1 =
                new DrumRuleDisplay(pattern1, instrument[1], this, cm, se);

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
            new DrumRuleDisplay(pattern[0], instrument[0], this, cm, se),
            new DrumRuleDisplay(pattern[1], instrument[1], this, cm, se)
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

    playingStatus = playing;

    switch( playing )
      {

        case PLAYING:

            pauseBtn.setEnabled(true);

            stopBtn.setEnabled(true);

            pauseBtn.setSelected(false);

            recordBtn.setEnabled(false);

            setMode(Mode.PLAYING);

            break;

        case PAUSED:

            pauseBtn.setEnabled(true);

            stopBtn.setEnabled(true);

            pauseBtn.setSelected(true);

            recordBtn.setEnabled(false);

            setMode(Mode.PLAYING_PAUSED);

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

            setNormalMode();

            getCurrentStave().repaint();

            break;
      }

    staveRequestFocus();
  }

public MidiPlayListener.Status getPlaying()
  {
    return playingStatus;
  }

public boolean isPlaying()
  {
    return playingStatus == MidiPlayListener.Status.PLAYING;
  }

public boolean playingStopped()
  {
    return playingStatus == MidiPlayListener.Status.STOPPED;
  }

public boolean playingPaused()
  {
    return playingStatus == MidiPlayListener.Status.PAUSED;
  }

/**
 *
 * Closes the current notateFrame
 *
 */
    private void quitMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMIActionPerformed

        // initialize the option pane

        Object[] options =
          {
            "Yes", "No"
          };

        int choice = JOptionPane.showOptionDialog(this,
                                                  "Do you wish to quit Impro-Visor?", "Quit",
                                                  JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                                                  null, options, options[0]);

        // the user selected yes

        if( choice == 0 )
          {
            midiSynth.stop("quit Impro-Visor");

            adv.showMarkedItems();
            System.exit(0);
          }
    }//GEN-LAST:event_quitMIActionPerformed

public MidiSynth getMidiSynth()
  {
    return midiSynth;
  }

public MidiSynth getMidiSynth2()
  {
    return midiSynth2;
  }

public Sequencer getSequencer()
  {
    return midiSynth.getSequencer();
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

/**
 * Saves the current leadsheet. This is the general function to call to do Save
 * As...
 *
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

/**
 * Save the current leadsheet using AWT interface. This works better on Macs.
 *
 * @see saveAsLeadsheetSwing()
 */
public boolean saveAsLeadsheetAWT()
  {

    //System.out.println("using AWT: " + lastLeadsheetFileStem);
    if( saveAWT.getDirectory().equals("/") )
      {
        saveAWT.setDirectory(ImproVisor.getLeadsheetDirectory().getAbsolutePath());
      }
    String lastLeadsheetFileStem = ImproVisor.getLastLeadsheetFileStem();

    if( lastLeadsheetFileStem != null )
      {
        saveAWT.setFile(lastLeadsheetFileStem);
      }

    saveAWT.setVisible(true);

    String selected = saveAWT.getFile();
    String dir = saveAWT.getDirectory();

    ImproVisor.setLastLeadsheetFileStem(selected);
    nameOfOpenFile = dir + selected;
    setTitle(score.getTitle());

    if( selected != null )
      {
        boolean noErrors;

        if( !selected.endsWith(leadsheetExt) )
          {

            selected += leadsheetExt;
          }

        File newFile = new File(dir + selected);

        noErrors = saveLeadsheet(newFile, score);
        setTitle(score.getTitle());

        setSavedLeadsheet(newFile);

        if( !savedLeadsheet.exists() )
          {
            setSavedLeadsheet(null);
            return false;
          }

        return noErrors;
      }

    return false;
  }

/**
 * Save current leadsheet using Swing interface. This doesn't appear to work on
 * Macs nicely.
 *
 * @see saveAsLeadsheetAWT()
 */
public boolean saveAsLeadsheetSwing()
  {
    //System.out.println("using Swing :" + lastLeadsheetFileStem);
    if( saveLSFC.getCurrentDirectory().getAbsolutePath().equals("/") )
      {
        saveLSFC.setCurrentDirectory(ImproVisor.getLeadsheetDirectory());
      }

    String lastLeadsheetFileStem = ImproVisor.getLastLeadsheetFileStem();

    if( lastLeadsheetFileStem != null )
      {
        saveLSFC.setSelectedFile(new File(lastLeadsheetFileStem));
      }


    if( saveLSFC.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
      {

        boolean noErrors;

        File selectedFile = saveLSFC.getSelectedFile();

        if( selectedFile.getName().endsWith(leadsheetExt) )
          {
            noErrors = saveLeadsheet(saveLSFC.getSelectedFile(), score);


            setSavedLeadsheet(saveLSFC.getSelectedFile());

            ImproVisor.setLastLeadsheetFileStem(selectedFile.getAbsolutePath());
            nameOfOpenFile = selectedFile.getAbsolutePath();
            setTitle(score.getTitle());
          }
        else
          {
            String file = selectedFile.getAbsolutePath();

            file += leadsheetExt;

            File newFile = new File(file);

            noErrors = saveLeadsheet(newFile, score);

            setSavedLeadsheet(newFile);

            ImproVisor.setLastLeadsheetFileStem(file);
            nameOfOpenFile = file;
            setTitle(score.getTitle());
          }

        if( !savedLeadsheet.exists() )
          {
            setSavedLeadsheet(null);

            return false;
          }

        return noErrors;
      }

    return false;
  }

/**
 * Get X location for new frame cascaded from original.
 *
 * @return
 */
public int getNewXlocation()
  {
    return (int) getLocation().getX() + WindowRegistry.defaultXnewWindowStagger;
  }

/**
 * Get Y location for new frame cascaded from original.
 *
 * @return
 */
public int getNewYlocation()
  {
    return (int) getLocation().getY() + WindowRegistry.defaultYnewWindowStagger;
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
    boolean phi = getCurrentStave().getPhi();
    boolean delta = getCurrentStave().getDelta();
    if( openLSFC.getCurrentDirectory().getAbsolutePath().equals("/") )
      {
        openLSFC.setCurrentDirectory(ImproVisor.getLeadsheetDirectory());
      }

    // show open file dialog

    if( openLSFC.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
      {
        // if user wants to open in a new window (via checkbox in dialog)

        if( lsOpenPreview.getCheckbox().isSelected() )
          {
            // loadMenu the file

            Score newScore = new Score();

            File file = openLSFC.getSelectedFile();

            ImproVisor.setLastLeadsheetFileStem(file.getName());
            nameOfOpenFile = file.getAbsolutePath();

            new OpenLeadsheetCommand(file, newScore).execute();

            // create a new window and show the score

            Notate newNotate =
                    new Notate(newScore,
                               this.adv,
                               this.impro,
                               getNewXlocation(),
                               getNewYlocation());

            newNotate.makeVisible(this);
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
                                               "Save modifications?",
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
              }
            else
              {
                // open the file

                File file = openLSFC.getSelectedFile();

                setupLeadsheet(file, false);

                ImproVisor.setLastLeadsheetFileStem(file.getName());
                nameOfOpenFile = file.getAbsolutePath();

                if( createRoadMapCheckBox.isSelected() )
                  {
                    roadMapThisAnalyze();
                  }
              }
            // creset the command manager

            cmReset();
            
            
            
          }

        setChordFontSizeSpinner(score.getChordFontSize());
      }
    //markermarkermarker
    updatePhiAndDelta(phi, delta);

    setNormalStatus();
    staveRequestFocus();
  }

/**
 * Reset the command manager
 */
public void cmReset()
  {
    // clear undo/redo history

    cm.clearHistory();

    // mark sheet as saved in its current state (no unsaved changes).

    cm.changedSinceLastSave(false);
  }

/**
 *
 * Reverts the leadsheet file to the previous saved version.
 *
 */
public void revertLeadsheet(java.awt.event.ActionEvent evt)
  {
    if( !savedLeadsheetExists() )
      {
        return;        // nothing saved
      }

    revertLSFC.setSelectedFile(savedLeadsheet);
    revertLSFC.resetChoosableFileFilters();
    revertLSFC.addChoosableFileFilter(new SingleFileFilter(savedLeadsheet));

    if( revertLSFC.showDialog(this, "Revert without saving!") == JFileChooser.APPROVE_OPTION )
      {
        setupLeadsheet(savedLeadsheet, false);

        // Reset the command manager

        cmReset();
      }

    staveRequestFocus();
  }

/**
 *
 * Do stuff that is common to open and revert file.
 *
 */
public boolean setupLeadsheet(File file, boolean openCorpus)
  {
    boolean phi = getCurrentStave().getPhi();
    boolean delta = getCurrentStave().getDelta();

    Score newScore = new Score();

    //cm.execute(new OpenLeadsheetCommand(file, newScore));

    // System.out.println("reading file " + file);

    if( !readLeadsheetFile(file, newScore) )
      {
        return false;
      }

    setSavedLeadsheet(file);
    setCursor(new Cursor(Cursor.WAIT_CURSOR));
    setupScore(newScore);
    if( openCorpus )
      {
        //System.out.println(newNotate.getTitle());
        getAllMeasures(newScore);
        setLickGenStatus("Reading leadsheet from file " + file);
      }
    //markermarkermarker
    updatePhiAndDelta(phi, delta);

    staveRequestFocus();
    
    //update scale degree buttons when setting up new leadsheet
    guideToneLineDialog.updateButtons();
    
    return true;
  }

/**
 * Reads the File into the Score.
 */
public boolean readLeadsheetFile(File file, Score score)
  {
    FileInputStream leadStream;

    try
      {
        leadStream = new FileInputStream(file);
        RecentFiles recFile = new RecentFiles(file.getAbsolutePath());
        recFile.writeNewFile();
      }
    catch( Exception e )
      {
        ErrorLog.log(ErrorLog.SEVERE, "File does not exist: " + file);
        return false;
        // e.printStackTrace();
      }

    return Leadsheet.readLeadSheet(new Tokenizer(leadStream), score);
  }

/**
 * I think this is only used in grammar learning.
 *
 * @param s
 */
public void getAllMeasures(Score s)
  {
    allMeasures = true;

    int HEAD = 0;

    melodyData = new ArrayList<String>();

    for( int i = 0; i < staveScrollPane.length; i++ )
      {
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

/**
 * I think this is only used in grammar learning.
 *
 * @param s
 */
public ArrayList<String> getMelodyData(int chorusNumber)
  {
    return getMelodyData(score, chorusNumber);
  }

/**
 * Returns an ArrayList of Strings representing a section of the melody of a
 * chorus and containing the notes in the section
 */
public ArrayList<String> getMelodyData(Score s, int chorusNumber)
  {
    MelodyPart melPart = s.getPart(chorusNumber).copy();
    ArrayList<String> sections = new ArrayList<String>();
    int numSlots = melPart.getSize();
    int slotsPerSection = lickgenFrame.getWindowSize() * BEAT;
    int windowSlide = lickgenFrame.getWindowSlide() * BEAT;
    //loop through sections
    for( int window = 0; window < slotsPerSection; window += windowSlide )
      {
        //for (int i = 0; (i * slotsPerSection) + (window * BEAT) + slotsPerSection <= melPart.getSize(); i++) {
        for( int i = 0 + (window); i <= numSlots - slotsPerSection; i += slotsPerSection )
          {
            String measure = Integer.toString(i) + " ";
            int tracker = 0;
            MelodyPart p = melPart.extract(i, i + slotsPerSection - 1, true, true);
            //if note is held from previous section, add that first
            if( melPart.getPrevNote(i) != null && melPart.getPrevNote(i).getRhythmValue() > i - melPart.getPrevIndex(i) )
              {
                Note currentNote = melPart.getPrevNote(i);
                measure = measure.concat(Integer.toString(currentNote.getPitch()));
                measure = measure.concat(" ");
                int len = currentNote.getRhythmValue();
                len -= (i - melPart.getPrevIndex(i));
                //measure = measure.concat (Integer.toString(i - melPart.getPrevIndex(i)));
                if( len > melPart.getMeasureLength() )
                  { //note is also tied to next measure (so it must be a whole note tied on either end)
                    len = melPart.getMeasureLength(); //we'll add the note to which it is tied later
                  }
                measure = measure.concat(Integer.toString(len));
                measure = measure.concat(" ");
                tracker = p.getNextIndex(0);
              }
            //System.out.println("p.size: " + p.getSize());
            //set tracker to index of first note
            if( p.getNote(0) == null )
              {
                tracker = p.getNextIndex(0);
                //truncate notes tied to next measure
              }
            int sumOfRhythmValues = 0;
            //add representations of all notes in measure to a string
            while( tracker < p.getSize() )
              {
                //System.out.println("Tracker = " + tracker);
                Note currentNote = p.getNote(tracker);
                measure = measure.concat(Integer.toString(currentNote.getPitch()));
                measure = measure.concat(" ");
                int length = currentNote.getRhythmValue();
                if( sumOfRhythmValues + length > slotsPerSection )
                  {
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

public void getAllContours(File leadsheet)
  {
    File directory = leadsheet.getParentFile();
    //System.out.println(leadsheet.getParentFile());
    File[] files = directory.listFiles();
    for( int i = 0; i < files.length; i++ )
      {
        String tempFile = files[i].getPath();
        if( !(files[i].getAbsolutePath().equals(leadsheet.getAbsolutePath())) && tempFile.endsWith(".ls") )
          {
            setupLeadsheet(files[i], true);
          }
      }
  }

public void writeHeadData()
  {
    setLickGenStatus("Writing head data");
    //get the list of files
    File directory = new File("leadsheets" + File.separator + "transcriptions" + File.separator
            + "TunesWithHeads");
    File[] files = directory.listFiles();

    //set up the output stream and output file
    FileOutputStream fileOut;
    ObjectOutputStream objOut;

    File outFile = new File(ImproVisor.getGrammarDirectory(), "HeadData.data");

    try
      {
        fileOut = new FileOutputStream(outFile);
        objOut = new ObjectOutputStream(fileOut);

        //open each leadsheet file, and write the chord part and melody part to the file
        for( int i = 0; i < files.length; i++ )
          {
            File file = files[i];
            if( file.toString().endsWith(".ls") )
              {
                Score newScore = new Score();

                cm.execute(new OpenLeadsheetCommand(file, newScore));

                setSavedLeadsheet(file);
                setCursor(new Cursor(Cursor.WAIT_CURSOR));
                //setupScore(newNotate);

                ChordPart chords = newScore.getChordProg();
                MelodyPart melody = newScore.getPart(0);

                objOut.writeObject(newScore);
              }
          }

        objOut.close();

      }
    catch( IOException ex )
      {
        ex.printStackTrace();
      }
  }

/**
 * Takes index and finds last index with note in it
 */
public int getLastNoteIndex(int slot, MelodyPart melpart)
  {
    while( melpart.getNote(slot).isRest() )
      {
        slot = melpart.getPrevIndex(slot);
      }
    return slot;
  }

/**
 * Takes indices of two slots which define a segment Gets all intervals in the
 * segment returns an array with the min interval in index 0 and max in index 1
 */
public int[] getIntervals(int startSlot, int endSlot, MelodyPart melpart)
  {
    int interval;
    int minInt = 0;
    int maxInt = 0;
    boolean firstTimeThrough = true;


    int[] intervals = new int[2];
    while( startSlot < endSlot )
      {
        Note firstNote = melpart.getNote(startSlot);
        startSlot = getNextNoteIndex(melpart, startSlot);
        Note secondNote = melpart.getNote(startSlot);

        interval = secondNote.getPitch() - firstNote.getPitch();
        if( firstTimeThrough )
          {
            minInt = interval;
            maxInt = interval;
            firstTimeThrough = false;
          }
        if( interval < minInt )
          {
            minInt = interval;
          }
        if( interval > maxInt )
          {
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
public int getNextNoteIndex(MelodyPart melpart, int tracker)
  {
    Note nextNote;
    do
      {
        tracker = melpart.getNextIndex(tracker);
        nextNote = melpart.getNote(tracker);
      }
    while( nextNote.isRest() );
    return tracker;
  }

/**
 * takes a melody part and an index returns whether or not the note at the index
 * ends a phrase
 */
public boolean phraseEnd(MelodyPart melpart, int tracker)
  {
    Note currentNote = melpart.getNote(tracker);
    if( currentNote.isRest() && currentNote.getRhythmValue() >= 60 )
      {
        return true;
      }
    else if( currentNote.getRhythmValue() >= 240 )
      {
        return true;
      }
    else
      {
        return false;
      }
  }

/**
 * Takes a melody part and an index Returns 3 element array of ints first elt.
 * Returns whether the pitch is increasing or decreasing at that point Returns 2
 * for increasing and 0 for decreasing second elt. returns index of first note
 * of phrase third elt. returns index of next note of phrase with different
 * pitch
 */
public int[] getDirection(MelodyPart melpart, int tracker)
  {
    int[] startSpot = new int[3];
    if( tracker != 0 )
      {
        tracker = melpart.getNextIndex(tracker);
      }
    int firstPitch = melpart.getNote(tracker).getPitch();
    while( firstPitch == -1 )
      {
        tracker = melpart.getNextIndex(tracker);
        firstPitch = melpart.getNote(tracker).getPitch();
      }
    startSpot[1] = tracker;
    tracker = melpart.getNextIndex(tracker);
    int secondPitch = melpart.getNote(tracker).getPitch();
    while( firstPitch == secondPitch || secondPitch == -1 )
      {
        tracker = melpart.getNextIndex(tracker);
        secondPitch = melpart.getNote(tracker).getPitch();
      }
    startSpot[2] = tracker;
    if( secondPitch > firstPitch )
      {
        startSpot[0] = 2;
      }
    else
      {
        startSpot[0] = 0;
      }
    return startSpot;
  }

/**
 * Set up a new score.
 *
 * Do stuff that is common to open file, revert file, and
 *
 * transferring contents from editor.
 *
 */
public void setupScore(Score score)
  {
    this.score = score;

    setTitle(score.getTitle());

    // reset the current scoreFrame

    setupArrays();

    setTempo(score.getTempo());

    clearLayoutPreference();

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

    newScore.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));

    // open a new window

    Notate newNotate =
            new Notate(newScore,
                       this.adv,
                       this.impro,
                       getNewXlocation(),
                       getNewYlocation());

    newNotate.updatePhiAndDelta(this.getPhiStatus(), this.getDeltaStatus());

    newNotate.makeVisible(this);

    newNotate.setPrefsDialog();

    // set the menu and button states

    setItemStates();
  }

/**
 * Set the height of specified notate frame so that it fills the screen. This
 * seems to work fine when the dock is at the right, but when it is at the
 * bottom, for some reason vertical staggering does not happen.
 *
 * @param notate
 */
public void setNotateFrameHeight()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices(); // Get size of each screen
    DisplayMode dm = gs[0].getDisplayMode();

    setSize(NOTATE_WIDTH, dm.getHeight() - (isVisible() ? getY() : 0));
  }

public void forceNotateFrameHeight()
  {
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] gs = ge.getScreenDevices(); // Get size of each screen
    DisplayMode dm = gs[0].getDisplayMode();

    setSize(NOTATE_WIDTH, dm.getHeight() - getY());
  }

public void setSavedLeadsheet(File f)
  {
    savedLeadsheet = f;
    if( f != null )
      {
        ImproVisor.setLastLeadsheetFileStem(f.getName());
        nameOfOpenFile = f.getAbsolutePath();
      }
  }

public boolean savedLeadsheetExists()
  {
    return savedLeadsheet != null && savedLeadsheet.exists();
  }

public boolean countInCheckboxIsSelected()
  {
    return countInCheckBox.isSelected();
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
            colorationButton.setText("<html><center>Color</center></html>");
          }
        else
          {
            noteColoration = true;
            colorationButton.setBackground(new Color(153, 204, 255));
            colorationButton.setText("<html><center>Black&<br>White</center></html>");
          }

}//GEN-LAST:event_colorationButtonActionPerformed

    private void smartEntryButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_smartEntryButtonActionPerformed
    {//GEN-HEADEREND:event_smartEntryButtonActionPerformed
        if( smartEntry )
          {
            smartEntry = false;
            smartEntryButton.setBackground(Color.red);
            smartEntryButton.setText("<html><center>Harmonic<br>Entry</center></html>");
          }
        else
          {
            smartEntry = true;
            smartEntryButton.setBackground(new Color(255, 153, 255));
            smartEntryButton.setText("<html><center>Simple<br>Entry</center></html>");
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
        generateFromButton();
}//GEN-LAST:event_generateLickInSelectionActionPerformed

    private void resolvePitchesActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_resolvePitchesActionPerformed
    {//GEN-HEADEREND:event_resolvePitchesActionPerformed
        rectifySelection();
}//GEN-LAST:event_resolvePitchesActionPerformed

public void generateFromButton()
  {
    Stave stave = getCurrentStave();
    originalGenerate(lickgen, stave.getSelectionStart(), stave.getSelectionEnd());
  }

public void rectifySelection()
  {
    Stave stave = getCurrentStave();
    rectifySelection(stave, getCurrentSelectionStart(), getCurrentSelectionEnd());
    stave.playSelection(false, 0, PlayScoreCommand.USEDRUMS, "Notate rectifySelection");
  }

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

    // To save a new currentVoicing to the vocabulary

    String name = newVoicingNameTF.getText();
    String chord = newVoicingChordTF.getText();
    String voice = voicingEntryTF.getText();
    String ext = extEntryTF.getText();
    String type = (String) newVoicingTypeCB.getSelectedItem();

    if( name.equals("") || chord.equals("") )
      {
        //ErrorLog.log(ErrorLog.WARNING, "Empty field.");
        return;

      }
    // making the currentVoicing and extensions Polylists and checking possible errors
    StringReader voicingReader = new StringReader(voice);
    Tokenizer in = new Tokenizer(voicingReader);
    Object o = in.nextSexp();
    Polylist v = (Polylist) o;
    if( o instanceof Polylist )
      {
        if( v.length() == 0 )
          {
            //ErrorLog.log(ErrorLog.WARNING, "Empty currentVoicing entered.");
            return;
          }
      }

    Polylist invalid = Key.invalidNotes(v);
    if( invalid.nonEmpty() )
      {
        //ErrorLog.log(ErrorLog.WARNING, "Invalid notes in currentVoicing: " + invalid);
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
            extension = (Polylist) o;
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

    int rise = PitchClass.findRise(fromRoot, toRoot);

    v = NoteSymbol.makeNoteSymbolList(v, rise);

    extension = NoteSymbol.makeNoteSymbolList(extension, rise);


    adv.addVoicing(chord, name, type, v, extension);

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

    if( root.equals("") )
      {

        ErrorLog.log(ErrorLog.WARNING, "No chord root entered.");

        return;
      }
    else if( rootClass == null )
      {

        ErrorLog.log(ErrorLog.WARNING, "Invalid chord root: " + root);

        return;
      }

    PitchClass bassClass = PitchClass.getPitchClass(bass);

    if( !bass.equals("") && bassClass == null )
      {

        ErrorLog.log(ErrorLog.WARNING, "Invalid bass note: " + bass);

        return;
      }

    NoteSymbol lowNote = NoteSymbol.makeNoteSymbol(low);

    if( low.equals("") )
      {

        ErrorLog.log(ErrorLog.WARNING, "No lower range entered.");

        return;
      }
    else if( lowNote == null )
      {

        ErrorLog.log(ErrorLog.WARNING, "Invalid lower range: " + low);

        return;
      }

    NoteSymbol highNote = NoteSymbol.makeNoteSymbol(high);

    if( high.equals("") )
      {

        ErrorLog.log(ErrorLog.WARNING, "No higher range entered.");

        return;
      }
    else if( highNote == null )
      {

        ErrorLog.log(ErrorLog.WARNING, "Invalid higher range: " + high);

        return;
      }

    voicingTableModel.setChordRoot(root, bass, lowNote, highNote);
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

    String type = (String) newVoicingTypeCB.getSelectedItem();
}//GEN-LAST:event_newVoicingTypeCBActionPerformed

private void voicingDeleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingDeleteButtonActionPerformed

    String v = voicingEntryTF.getText();

    if( v.equals("") )
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

    // Find out the index of this currentVoicing for the chord, counting from 0 for the first.

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
        return; // can't remove generated currentVoicing; not in the form
      }

    getAdvisor().removeNthVoicing(chordName, position - 1);
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

    if( voicingTestFrame != null && voicingTestFrame.isVisible() )
      {
        selectVoicing(v, currentChord);
      }
  }

/**
 * Gets the text from the currentVoicing entry text field.
 *
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
 * Adds text to the currentVoicing entry text field
 *
 * @param text
 */
public void addVoicingEntryTFText(String text)
  {
    String s = voicingEntryTF.getText();
    if( s.equals("") )
      {
        voicingEntryTF.setText("(" + text + ")");
      }
    else
      {
        voicingEntryTF.setText(s.replace(')', ' ') + text + ")");
      }
  }

public void addExtEntryTFText(String text)
  {
    String s = extEntryTF.getText();
    if( s.equals("") )
      {
        extEntryTF.setText("(" + text + ")");
      }
    else
      {
        extEntryTF.setText(s.replace(')', ' ') + text + ")");
      }
  }

/**
 * clears any text in the currentVoicing and extension entry text fields.
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

    for( Polylist L = chords; L.nonEmpty(); L = L.rest() )
      {
        Polylist piece = (Polylist) L.first();
        ChordForm form = (ChordForm) piece.second();

        String cName = form.getName();
        String family = form.getFamily();

        // Get the subMenu for this family, or create one if non-existent
        // If a new menu is created, add it as a sub-menu.

        JMenu subMenu = (JMenu) map.get(family);
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
        public void actionPerformed(java.awt.event.ActionEvent evt)
          {
            chordToInsert = evt.getActionCommand();
            //System.out.println("You selected " + chordToInsert);

            // Insert the currentVoicing into the leadsheet
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
                Polylist voicing = (Polylist) o;
                voicing = NoteSymbol.makeNoteSymbolList(voicing);
                Polylist extension = new Polylist();
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
                        extension = (Polylist) o;
                        extension = NoteSymbol.makeNoteSymbolList(extension);
                      }
                  }

                ChordSymbol c = ChordSymbol.makeChordSymbol(chordToInsert);
                c.setVoicing(voicing);
                c.setExtension(extension);

//                if( !ChordPattern.goodVoicing(c, currentStyle) )
//                  {
//                    ErrorLog.log(ErrorLog.WARNING,
//                                 "Voicing does not fit within range of leadsheet: " + voicing);
//                    return;
//                  }

                playVoicing(c);
                insertVoicing(c, index);
              }
            else if( o instanceof Polylist )
              {
                ErrorLog.log(ErrorLog.WARNING, "No slot selected for insertion.");
              }
            else
              {
                ErrorLog.log(ErrorLog.WARNING, "Malformed voicing: " + v);
              }
          }

        });
      }
    chordSelectionMenu.add("cancel insert");
  }

/**
 * Removes duplicates from a Polylist (for making future chords list in keyboard
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
            last = (String) next;
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
    Polylist result = getCurrentStave().extractChordNamePolylist(startingSlot % chorusLength, chorusLength - 1);
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
 *
 * @param tab, current chorus tab
 * @param slotInPlayback
 */
public void keyboardPlayback(Chord currentChord, int tab, int slotInPlayback, int slot, int totalSlots)
  {
    String currentChordName = currentChord == null ? "NC" : currentChord.getName();
    Polylist v;

    // Code for keyboard playback.
    if( currentChord != null && (v = currentChord.getVoicing()) != null )
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
        if( currentChordName.contains("/") )
          {
            int bassInd = currentChordName.indexOf("/") + 1;
            bass = currentChordName.substring(bassInd, currentChordName.length());
            bass = keyboard.nameToBass(bass);
            rootEqualBassCheckbox.setSelected(false);
          }

        // If the playback is just beginning and the keyboard is blank
        if( v2.equals(EMPTY) )
          {
            keyboard.showVoicingOnKeyboard(v1);

            setBassAndRootTFs(bass, root);

            futureChords = makeFutureChordsList(slotInPlayback);
            if( futureChords.nonEmpty() && currentChordName.equals(futureChords.first()) )
              {
                futureChords = futureChords.rest();
              }

            String future = futureChords.toStringSansParens();
            future = future.replaceAll(" ", "   ");

            setPresentChordDisplay(currentChordName);
            setFutureChordDisplay(future);
          }
        // If the current chord currentVoicing and keyboard currentVoicing are different
        // (if the chord has changed)
        else if( !keyboard.voicingsAreEqual(v1, v2) || !currentChordName.equals(presentChord) )
          {
            keyboard.showVoicingOnKeyboard(v1);

            setBassAndRootTFs(bass, root);

            pastChords = Polylist.list(keyboard.getPresentChordDisplayText());

            if( futureChords.nonEmpty() && !currentChordName.equals(presentChord) )
              {
                futureChords = makeFutureChordsList(slotInPlayback);
                if( futureChords.nonEmpty()
                        && currentChordName.equals(futureChords.first()) )
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
        if( tab != currentPlaybackTab )
          {
            keyboard.showVoicingOnKeyboard(v1);

            setBassAndRootTFs(bass, root);

            currentChordName = keyboard.getPresentChordDisplayText();

            futureChords = makeFutureChordsList();

            futureChords = futureChords.rest();

            String future = futureChords.toStringSansParens();
            future = future.replaceAll(" ", "   ");

            setPresentChordDisplay(currentChordName);
            setFutureChordDisplay(future);
          }
      }
    // End code for keyboard playback.
  }

/**
 * Finds the row number of the first occurrence of currentChord in the
 * currentVoicing table
 *
 * @param currentChord
 * @return
 */
public int findChordinTable(String currentChord)
  {
    int selectedRow = -1;
    for( int i = 0; i < voicingTable.getRowCount(); i++ )
      {
        Object o = voicingTableModel.getValueAt(i, VoicingTableChordColumn);
        String chord = o.toString();

        Object p = voicingTableModel.getValueAt(i, VoicingTableVoicingColumn);
        String v = p.toString();

        if( chord.equals(currentChord) )
          {
            selectedRow = i;
            break;
          }
      }
    return selectedRow;
  }

/**
 * Finds the row number of a certain currentVoicing in the currentVoicing table.
 *
 * @param chordRow
 * @param currentVoicing
 * @param currentChord
 * @return
 */
public int findVoicinginTable(int chordRow, String voicing, String currentChord)
  {
    int selectedRow;

    for( int i = chordRow; i < voicingTable.getRowCount(); i++ )
      {
        Object o = voicingTableModel.getValueAt(i, VoicingTableChordColumn);
        String chord = o.toString();

        Object p = voicingTableModel.getValueAt(i, VoicingTableVoicingColumn);
        String cell = p.toString();

        if( chord.equals(currentChord) )
          {
            Polylist v1 = voicingToList(voicing);
            Polylist v2 = voicingToList(cell);

            PitchClass v1Pitch = PitchClass.getPitchClass(v1.first().toString());
            PitchClass v2Pitch = PitchClass.getPitchClass(v2.first().toString());

            String pitch1 = v1Pitch.toString();
            String pitch2 = v2Pitch.toString();

            String uCell = cell;
            String dCell = cell;

            if( keyboard.voicingsAreEqual(voicing, cell) )
              {
                selectedRow = i;
                return selectedRow;
              }
            else
              {
                while( !keyboard.voicingsAreEqual(voicing, uCell)
                        && !keyboard.voicingsAreEqual(voicing, dCell) )
                  {
                    String u = uCell;
                    String d = dCell;
                    uCell = keyboard.transposeVoicing(uCell, "up");
                    dCell = keyboard.transposeVoicing(dCell, "down");

                    if( d.equals(dCell) || u.equals(uCell) )
                      {
                        break;
                      }
                  }
                if( keyboard.voicingsAreEqual(voicing, dCell)
                        || keyboard.voicingsAreEqual(voicing, uCell) )
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
 * Selects a given row number in the currentVoicing table.
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

    if( n.equals("") )
      {
        Object p = voicingTableModel.getValueAt(selectedRow, VoicingTableNameColumn);
        String s = p.toString();
        int space = s.indexOf(" ") + 1;
        s = s.substring(space, s.length() - 1);
        currentChord = s;

        for( int i = 0; i < voicingTable.getRowCount(); i++ )
          {
            Object x = voicingTableModel.getValueAt(i, VoicingTableChordColumn);
            String y = x.toString();
            if( currentChord.equals(y) )
              {
                selectedRow = i;
              }
          }
      }

    if( selectedRow == -1 )
      {
        ErrorLog.log(ErrorLog.WARNING, "Chord not found in table!");
        return;
      }

    selectedRow = findVoicinginTable(selectedRow, v, currentChord);

    voicingTable.setColumnSelectionAllowed(false);
    voicingTable.setRowSelectionAllowed(true);
    voicingTable.setRowSelectionInterval(selectedRow, selectedRow);
    voicingTable.changeSelection(selectedRow, selectedRow, true, true);
  }

public void setBassAndRootTFs(String bass, String root)
  {
    bassNoteTF.setText(bass);
    chordRootTF.setText(root);
    int midi = keyboard.findBass();
    String note = keyboard.findBassName(midi);
    if( !note.equals(bass) )
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
    if( playingStopped() )
      {
        openKeyboard();
        keyboard.showBass();
      }

    if( !v.equals("") )
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
        keyboard = new VoicingKeyboard(this, getNewXlocation(), getNewYlocation());
      }

    String current = keyboard.getPresentChordDisplayText();
    if( current.equals("") )
      {
        clearKeyboard();
        clearVoicingEntryTF();
      }

    midiSynth.registerReceiver(voicingInput);
    keyboard.setVisible(true);
  }

public void closeKeyboard()
  {
    keyboard = null;
  }

public void openStepKeyboard()
  {
    if( stepKeyboard == null )
      {
        stepKeyboard = new StepEntryKeyboard(this, getNewXlocation(), getNewYlocation());
      }

    stepKeyboard.setVisible(true);
    if( stepInputActive == false )
      {
        boolean selected = !stepInputBtn.isSelected();
        stepInputBtn.setSelected(selected);
        setStepInputBtn(selected);
      }

    stepKeyboard.resetAdvice();
  }

public void closeStepKeyboard()
  {
    stepKeyboard = null;
  }

public VoicingKeyboard getCurrentVoicingKeyboard()
  {
    return keyboard;
  }

public StepEntryKeyboard getCurrentStepKeyboard()
  {
    return stepKeyboard;
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

public void clearStepKeyboard()
  {
    if( stepKeyboard != null )
      {
        stepKeyboard.clearKeyboard();
      }
  }

/**
 * Turns the current currentVoicing into a polylist.
 *
 * @return polylist representing the current currentVoicing and extension
 */
public Polylist voicingToList(String v)
  {
    if( v.equals("") )
      {
        return Polylist.nil;
      }

    StringReader voicingReader = new StringReader(v);
    Tokenizer in = new Tokenizer(voicingReader);
    Object o = in.nextSexp();

    Polylist selVoicing = o instanceof Polylist ? (Polylist) o : Polylist.list(o);
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

    if( !e.equals("") )
      {
        StringReader extReader = new StringReader(e);
        Tokenizer in = new Tokenizer(extReader);
        Object o = in.nextSexp();

        extension = (Polylist) o;
      }

    extension = NoteSymbol.makeNoteSymbolList(extension);

    Polylist L = extension;

    return L;
  }

public void addVoicingToSeq(String v)
  {
    ListSelectionModel rowSM = voicingTable.getSelectionModel();

    if( !rowSM.isSelectionEmpty() )
      {

        int selectedRow = rowSM.getMinSelectionIndex();
        Object o = voicingTableModel.getValueAt(selectedRow, VoicingTableVoicingColumn);
        Object p = voicingTableModel.getValueAt(selectedRow, VoicingTableExtensionColumn);

        if( v.equals("") )
          {
            return;
          }

        String e = p.toString();
        String c = chordRootTF.getText();

        v = v.substring(1, v.length());
        v = "(" + c + " " + v;

        int index = voicingSequenceList.getSelectedIndex();

        index++;

        String s = v + e;

        voicingSequenceListModel.add(index, s);
        voicingSequenceList.setSelectedIndex(index);
      }
    else
      {
        String e = extEntryTF.getText();
        String c = "{" + chordRootTF.getText() + "}";
        String s = c + v + e;
        int index = voicingSequenceListModel.size();
        voicingSequenceListModel.add(index, s);
        voicingSequenceList.setSelectedIndex(index);
      }
  }

/**
 * Adds a currentVoicing to the currentVoicing render list.
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
 * Removes the selected currentVoicing from the currentVoicing render list.
 *
 * @param evt
 */
private void voicingSequenceRemoveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingSequenceRemoveButtonActionPerformed

    int index = voicingSequenceList.getSelectedIndex();
    if( index == -1 )
      {
        return;
      }
    else if( index == 0 )
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
 * Swaps the selected currentVoicing with the one above it in the currentVoicing
 * render.
 *
 * @param evt
 */
private void voicingSequenceUpArrowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_voicingSequenceUpArrowMouseClicked

    int index = voicingSequenceList.getSelectedIndex();//GEN-LAST:event_voicingSequenceUpArrowMouseClicked

    if( index == -1 || index == 0 )
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
 * Swaps the selected currentVoicing with the one below it in the currentVoicing
 * render.
 *
 * @param evt
 */
private void voicingSequenceDownArrowMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_voicingSequenceDownArrowMouseClicked

    int index = voicingSequenceList.getSelectedIndex();
    if( index == voicingSequenceListModel.size() - 1 )
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
 * Uses the range text fields on the currentVoicing window to find the MIDI
 * value of the lowest note in the range.
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
 * Uses the range text fields on the currentVoicing window to find the MIDI
 * value of the highest note in the range.
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
 * Plays the current render of voicings and displays them on the keyboard.
 *
 * @param evt
 */
private void voicingSequencePlayButtonActionPerformed(java.awt.event.ActionEvent evt)
  {

    for( int i = 0; i < voicingSequenceListModel.size(); i++ )
      {
        voicingSequenceList.setSelectedIndex(i);

        String s = getChordRootTFText();
        Object o = voicingSequenceListModel.getElementAt(i);
        String v = o.toString();

        int j = v.indexOf(")(") + 1;

        if( j == 0 )
          {
            v = v.substring(3);
            v = "(" + v;
          }
        else
          {
            v = v.substring(3, j);
            v = "(" + v;
          }

        voicingEntryTF.setText(v);
        keyboard.showVoicingOnKeyboard(v);

        try
          {
            constructAndPlayChord(s, v);
            Thread.sleep(1000);
          }
        catch( InterruptedException e )
          {
            System.out.println("Sleep interrupted:" + e);
          }
      }
  }

/**
 * Displays the current currentVoicing if the selection index in the render list
 * changes.
 */
private void displayVoicingfromList()
  {
    int index = voicingSequenceList.getSelectedIndex();

    if( index == -1 )
      {
        clearVoicingEntryTF();
        clearKeyboard();
        return;
      }

    Object o = voicingSequenceListModel.getElementAt(index);
    String v = o.toString();

    int i = v.indexOf(")(") + 1;
    int j = v.length();

    String e = v.substring(i, j);
    String c = v.substring(1, 3);

    if( i == 0 )
      {
        e = "";
        v = v.substring(3);
        v = "(" + v;
      }
    else
      {
        v = v.substring(3, i);
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
 * If the selection index of the currentVoicing render list changes, the new
 * currentVoicing is displayed in the currentVoicing entry text field and on the
 * keyboard.
 *
 * @param evt
 */
private void voicingSequenceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_voicingSequenceListValueChanged

    displayVoicingfromList();
}//GEN-LAST:event_voicingSequenceListValueChanged

public void setFutureChordDisplay(String chords)
  {
    // keyboard.setFutureChordDisplayText(chords);
  }

public void setPresentChordDisplay(String chords)
  {
    keyboard.setPresentChordDisplayText(chords);
  }

public void setPastChordDisplay(String chords)
  {
    // keyboard.setPastChordDisplayText(chords);
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

    switch( e.getKeyCode() )
      {
        case KeyEvent.VK_A:
            addToVoicingSequence();
            break;
        case KeyEvent.VK_C:
            keyboard.clearKeyboard();
            clearVoicingEntryTF();
            break;
        case KeyEvent.VK_P:
            constructAndPlayChord(c, v);
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
    if( rootClass == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Invalid chord root: " + chordRoot);
        return;
      }
    if( rootEqualBassCheckbox.isSelected() )
      {
        bassNoteTF.setText(chordRoot);
      }
    int bass = keyboard.findBass();
    String note = keyboard.findBassName(bass);
    if( keyboard.enharmonic(note, chordRoot) )
      {
        note = chordRoot;
      }
    keyboard.setBass(note, bass);
    buildVoicingTable();
}//GEN-LAST:event_chordRootTFActionPerformed

private void voicingEntryTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_voicingEntryTFActionPerformed

    keyboard.showVoicingOnKeyboard(voicingEntryTF.getText());
}//GEN-LAST:event_voicingEntryTFActionPerformed

private void buildTableButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_buildTableButtonKeyPressed

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
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

    switch( evt.getKeyCode() )
      {
        default:
            voicingFrameKeyPressed(evt);
            break;
      }
}//GEN-LAST:event_voicingSequenceListKeyPressed

private void voicingTableKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_voicingTableKeyPressed

    switch( evt.getKeyCode() )
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

    if( bassClass == null )
      {
        ErrorLog.log(ErrorLog.WARNING, "Invalid bass note: " + bassNote);
        return;
      }
    if( bassNote.equals("") )
      {
        bassNoteTF.setText(chordRoot);
      }
    if( rootEqualBassCheckbox.isSelected() )
      {
        chordRootTF.setText(bassNote);
      }
    int bass = keyboard.findBass();
    String note = keyboard.findBassName(bass);

    if( keyboard.enharmonic(note, bassNote) )
      {
        note = bassNote;
      }

    keyboard.setBass(note, bass);
    buildVoicingTable();
}//GEN-LAST:event_bassNoteTFActionPerformed

private void rootEqualBassCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootEqualBassCheckboxActionPerformed

    if( rootEqualBassCheckbox.isSelected() )
      {
        String bass = bassNoteTF.getText();
        chordRootTF.setText(bass);
        buildVoicingTable();
      }
}//GEN-LAST:event_rootEqualBassCheckboxActionPerformed

private void rootEqualBassCheckboxKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_rootEqualBassCheckboxKeyPressed

    switch( evt.getKeyCode() )
      {
        default:
            voicingFrameKeyPressed(evt);
            break;
      }
}//GEN-LAST:event_rootEqualBassCheckboxKeyPressed

private void pianoKeyboardMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pianoKeyboardMIActionPerformed

    if( playingStopped() )
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
    if( f.exists() )
      {
        System.gc();
        boolean deleted = f.delete();
        while( !deleted )
          {
            deleted = f.delete();
            //System.out.println(deleted);
          }
      }
    String backupFile = f.getParentFile().getPath() + "/Backup.grammar";
    File f2 = new File(backupFile);
    try
      {
        FileWriter out = new FileWriter(f, true);
        FileReader in = new FileReader(f2);
        char[] buf = new char[(int) f2.length()];
        in.read(buf);
        String input = new String(buf);
        out.write(input);
        out.close();
        in.close();
      }
    catch( Exception e )
      {
        System.out.println(e.getMessage());
      }

    refreshGrammarEditor();
  }

public void refreshGrammarEditor()
  {
    grammarEditor.fillEditor();
    grammarEditor.performEditorToSourceButton(null);
  }

public void adjustSelection()
  {
    Stave stave = getCurrentStave();
    boolean nothingWasSelected = !stave.somethingSelected();
    boolean oneSlotWasSelected = stave.oneSlotSelected();

    if( nothingWasSelected )
      {
        selectAll();
      }
    else if( oneSlotWasSelected )
      {
        stave.setSelectionToEnd();
      }
    else if( (stave.getSelectionEnd() - 1) % BEAT != 0 )
      {
        stave.setSelectionEnd(BEAT * (1 + stave.getSelectionEnd() / BEAT) - 1);
      }
  }

int cycCount = 0;
int shufCount = 0;
ArrayList<MelodyPart> melodyList = new ArrayList<MelodyPart>();
MelodyPart pointr = new MelodyPart();
int instr;

/**
 * Original version of generate: does not return MelodyPart
 *
 * @param lickgen
 * @param improviseStartSlot
 * @param improviseEndSlot
 */
public void originalGenerate(LickGen lickgen, int improviseStartSlot, int improviseEndSlot)
  {
    instr = getCurrentMelodyPart().getInstrument();
    if (ifCycle){
        String temp;
        temp = gramList.get(cycCount).substring(0, gramList.get(cycCount).length() - GrammarFilter.EXTENSION.length());
        notateGrammarMenu.setText(temp + "(Cycle)");
        grammarFilename = ImproVisor.getGrammarDirectory() + File.separator + gramList.get(cycCount);
        fullName = gramList.get(cycCount);
        lickgen.loadGrammar(grammarFilename);
        lickgenFrame.resetTriageParameters(false);
        Preferences.setPreference(Preferences.DEFAULT_GRAMMAR_FILE, gramList.get(cycCount));
        cycCount++;
        if (cycCount == gramList.size()){cycCount = 0;} 
    }
    
    if (ifShuffle){
       
        String temp;
        temp = shufGramList.get(shufCount).substring(0, shufGramList.get(shufCount).length() - GrammarFilter.EXTENSION.length());
        notateGrammarMenu.setText(temp + "(Shuffle)");
        grammarFilename = ImproVisor.getGrammarDirectory() + File.separator + shufGramList.get(shufCount);
        fullName = shufGramList.get(shufCount);
        lickgen.loadGrammar(grammarFilename);
        lickgenFrame.resetTriageParameters(false);
        Preferences.setPreference(Preferences.DEFAULT_GRAMMAR_FILE, shufGramList.get(shufCount));
        shufCount++;
        if (shufCount == shufGramList.size()){shufCount = 0; Collections.shuffle(shufGramList);} 
    }


    saveConstructionLineState = showConstructionLinesMI.isSelected();
    // Don't construction show lines while generating
    setShowConstructionLinesAndBoxes(false);

    setMode(Mode.GENERATING);

    adjustSelection();

    Stave stave = getCurrentStave();

    //if nothing is selected, the user probably meant to select everything
    if( improviseStartSlot == OUT_OF_BOUNDS
            || improviseEndSlot == OUT_OF_BOUNDS
            || improviseStartSlot == improviseEndSlot )
      {
        improviseStartSlot = 0;
        improviseEndSlot = getChordProg().size();
      }
    else
      {
        stave.setSelection(improviseStartSlot, improviseEndSlot);
      }

    totalSlots = improviseEndSlot - improviseStartSlot + 1;

    int beatsRequested = totalSlots / BEAT;

    //System.out.println("\ngenerate: " + improviseStartSlot + " to " + improviseEndSlot + ", requesting " + beatsRequested + " beats");

    verifyTriageFields();

    Polylist rhythm = null;

    boolean useOutlines = lickgenFrame.useSoloistSelected();

    boolean useCritic = lickgenFrame.useCritic();

    boolean enableRecording = !useCritic;

    double criticGrade = lickgenFrame.getCriticGrade();

    // To prevent lag from too many interations, we limit the number of times
    // the critic can test licks. We also limit the time of the iterations.
    final int criticLimit = 999;
    long currTime = System.currentTimeMillis();
    long totalTime = currTime + 20000;

    // outLines is the same as soloist
    if( useOutlines )
      {
        // was new lickgenFrame.fillMelody(BEAT, rhythm, chordProg, 0);
        // was commented out:
        lickgen.getFillMelodyParameters(minPitch,
                                        maxPitch,
                                        minInterval,
                                        maxInterval,
                                        BEAT,
                                        leapProb,
                                        chordProg,
                                        0,
                                        avoidRepeats);

        MelodyPart solo = lickgen.generateSoloFromOutline(totalSlots);
        pointr = solo; //TEST
        if( solo != null )
          {
            rhythm = lickgen.getRhythmFromSoloist(); //get the abstract melody for display
            if( lickgenFrame.useHeadSelected() )
              {
                adjustLickToHead(solo);
              }

            if( useSubstitutorCheckBox.isSelected() )
              {
                ChordPart chords = getChordProg().extract(improviseStartSlot,
                                                          improviseEndSlot);
                lickgenFrame.applySubstitutions(solo, chords);
              }
            else
              {
                putLick(solo);
              }
          }
      }
    // If we should use the critic...
    else if( useCritic )
      {
        // Keep track of the number of lick generations
        int count = 0;

        while( useCritic )
          {
            rhythm = lickgen.generateRhythmFromGrammar(improviseStartSlot, totalSlots);

            MelodyPart lick = generateLick(rhythm, improviseStartSlot, improviseEndSlot);
            pointr = lick;//TEST

            if( lick != null )
              {
                //Increment the count
                count++;
                currTime = System.currentTimeMillis();

                ArrayList<Unit> units = lick.getUnitList();
                ArrayList<ChordSymbol> symbols = stave.getChordProg().getChordSymbols();
                ArrayList<Integer> durations = stave.getChordProg().getChordDurations();

                ArrayList<Note> noteList = new ArrayList<Note>();
                ArrayList<Chord> chordList = new ArrayList<Chord>();

                // Add all notes and chords to the lists
                for( Unit u : units )
                  {
                    noteList.add((Note) u);
                  }
                for( int i = 0; i < symbols.size(); i++ )
                  {
                    chordList.add(new Chord(symbols.get(i), durations.get(i)));
                  }

                Double gradeFromCritic = critic.gradeFromCritic(noteList, chordList);

                // Stop the generation if we've gone too many times
                if( gradeFromCritic != null
                        && (count >= criticLimit || currTime >= totalTime) )
                  {
                    JOptionPane.showMessageDialog(null,
                                                  new JLabel("<html><div style=\"text-align: center;\">"
                            + "Too many generation attempts, <br/>"
                            + "cannot generate lick with desired grade."),
                                                  "Alert", JOptionPane.PLAIN_MESSAGE);

                    if( useSubstitutorCheckBox.isSelected() )
                      {
                        ChordPart chords = getChordProg().extract(improviseStartSlot,
                                                                  improviseEndSlot);
                        lickgenFrame.applySubstitutions(lick, chords);
                      }
                    else
                      {
                        putLick(lick);
                      }
                    useCritic = false;
                    lickgenFrame.setCounterForCriticTextField(count);
                    lickgenFrame.setLickFromStaveGradeTextField(gradeFromCritic);
                  }
                // If the grade is high enough, pass it through the filter
                else if( gradeFromCritic != null && gradeFromCritic >= criticGrade )
                  {
                    if( useSubstitutorCheckBox.isSelected() )
                      {
                        ChordPart chords = getChordProg().extract(improviseStartSlot,
                                                                  improviseEndSlot);
                        lickgenFrame.applySubstitutions(lick, chords);
                      }
                    else
                      {
                        putLick(lick);
                      }
                    useCritic = false;
                    lickgenFrame.setCounterForCriticTextField(count);
                    lickgenFrame.setLickFromStaveGradeTextField(gradeFromCritic);
                  }
              }
            else
              {
                //debug System.out.println("panic: generated null lick");
                setMode(Mode.GENERATION_FAILED);
                return;
              }
          }
      }
    // If the outline is unable to generate a solo, which might
    // happen if there are no outlines of the correct length or the soloist
    // file was not correctly loaded, use the grammar.
    else if( rhythm == null || !useOutlines )
      {
        if( lickgenFrame.getUseGrammar() )
          {
            rhythm = lickgen.generateRhythmFromGrammar(improviseStartSlot, totalSlots);
          }
        else
          {
            rhythm = lickgen.generateRandomRhythm(totalSlots,
                                                  lickgenFrame.getMinDuration(),
                                                  lickgenFrame.getMaxDuration(),
                                                  lickgenFrame.getRestProb());
          }

        MelodyPart lick = generateLick(rhythm, improviseStartSlot, improviseEndSlot);
        pointr = lick; //test

        // Critical point for recurrent generation
        if( lick != null )
          {
            int beatsGenerated = lick.size() / BEAT;

            if( beatsGenerated > beatsRequested )
              {
                //debug
                //System.out.println("generated " + beatsGenerated
                //             + " beats, but " + beatsRequested + " requested (fewer)");

                lick = lick.extract(0, BEAT * beatsRequested - 1, true);
              }
            else if( beatsGenerated < beatsRequested )
              {
                //debug
                //System.out.println("generated " + beatsGenerated
                //             + " beats, but " + beatsRequested + " requested (more)");
              }

            if( useSubstitutorCheckBox.isSelected() )
              {
                ChordPart chords = getChordProg().extract(improviseStartSlot,
                                                          improviseEndSlot);
                lickgenFrame.applySubstitutions(lick, chords);
              }
            else
              {
                putLick(lick);
              }
            //System.out.println("lick = " + lick);
          }
        else
          {
            //debug System.out.println("panic: generated null lick");
            setMode(Mode.GENERATION_FAILED);
            return;
          }
      }

    if( rhythm != null )
      {
        lickgenFrame.setRhythmFieldText(Formatting.prettyFormat(rhythm));
      }

    setMode(Mode.GENERATED);

    if( enableRecording )
      {
        enableRecording(); // TRIAL
      }

    if (lickgenFrame.shouldSaveImp()){
        melodyList.add(pointr);
    } 
  }

/**
 * Rectify the current selection, aligning pitches to harmony.
 *
 * @param stave
 * @param selectionStart
 * @param selectionEnd
 */
public void rectifySelection(Stave stave, int selectionStart, int selectionEnd)
  {
    ///System.out.println("rectifying from  " + selectionStart + " to " + selectionEnd);
    stave.rectifySelection(selectionStart, selectionEnd, false, false);
  }

private void lowRangeTF2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lowRangeTF2ActionPerformed

    String lowNote = lowRangeTF2.getText();//GEN-LAST:event_lowRangeTF2ActionPerformed
    NoteSymbol n = NoteSymbol.makeNoteSymbol(lowNote);
    int midiValue = n.getMIDI();

    String highNote = highRangeTF2.getText();
    NoteSymbol h = NoteSymbol.makeNoteSymbol(highNote);
    int midiValueHigh = h.getMIDI();

    if( highNote.equals("") )
      {
        PianoKey high = keyboard.pianoKeys()[midiValue - 10];
        highRangeTF2.setText(high.getName());
      }
    else if( midiValue > midiValueHigh )
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

    if( lowNote.equals("") )
      {
        PianoKey low = keyboard.pianoKeys()[midiValue - 32];
        lowRangeTF2.setText(low.getName());
      }
    else if( midiValue < midiValueLow )
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

    if( chord.contains("/") )
      {
        int slash = chord.indexOf("/") + 1;
        bass = chord.substring(slash, chord.length());
        bassNoteTF.setText(bass);
        rootEqualBassCheckbox.setSelected(false);
      }

    int midi = keyboard.findBass();
    String note = keyboard.findBassName(midi);
    if( !note.equals(bass) )
      {
        bassNoteTF.setText(bass);
      }
    keyboard.setBass(bass, midi);

    buildVoicingTable();

    int selectedRow = findChordinTable(chord);

    if( selectedRow == -1 )
      {
        ErrorLog.log(ErrorLog.WARNING, "Chord not found in table!");
        return;
      }

    voicingTable.setColumnSelectionAllowed(false);
    voicingTable.setRowSelectionAllowed(true);
    voicingTable.setRowSelectionInterval(selectedRow, selectedRow);
    voicingTable.changeSelection(selectedRow, selectedRow, true, true);

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
    deleteTab();
}//GEN-LAST:event_delTabBtnActionPerformed

private boolean hideDelTabDialog;

public void deleteTab()
  {
    // initialize the option pane

    Object[] options =
      {
        "Yes", "No"
      };

    Object[] messages =
      {
        "Do you wish to delete the current chorus?\nThis can't be undone.\n", delTabCheckBox
      };

    int choice = hideDelTabDialog ? 0 : JOptionPane.showOptionDialog(this,
                                                                     messages,
                                                                     "Delete Current Chorus?", JOptionPane.YES_NO_OPTION,
                                                                     JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

    // int choice = 0;
    // the user selected yes

    if( choice == 0 && currTabIndex >= 0
            && scoreTab.getTabCount() > 1 )
      {
        score.delPart(currTabIndex);
        partList.remove(currTabIndex);
        cm.changedSinceLastSave(true);

        setupArrays();

        // set the menu and button states

        setItemStates();
      }
  }

private void pauseMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseMIActionPerformed
    pauseBtnActionPerformed(null);
}//GEN-LAST:event_pauseMIActionPerformed

private void notateGrammarMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_notateGrammarMenuActionPerformed
    //populateNotateGrammarMenu();
}//GEN-LAST:event_notateGrammarMenuActionPerformed

private void notateGrammarMenuStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_notateGrammarMenuStateChanged
}//GEN-LAST:event_notateGrammarMenuStateChanged

private void notateGrammarMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_notateGrammarMenuMenuSelected
}//GEN-LAST:event_notateGrammarMenuMenuSelected

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
    //requestFocusInWindow();
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
    newChordFontSize = ((Integer) defaultChordFontSizeSpinner.getValue()).intValue();
}//GEN-LAST:event_defaultChordFontChange

private void defaultChordFontSizeSpinnerKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_defaultChordFontSizeSpinnerKeyReleased
    if( evt.getKeyCode() == KeyEvent.VK_ENTER )
      {
        newChordFontSize = ((Integer) defaultChordFontSizeSpinner.getValue()).intValue();
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

private void mostRecentLeadsheetMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostRecentLeadsheetMIActionPerformed
}//GEN-LAST:event_mostRecentLeadsheetMIActionPerformed

/**
 * Populate the menu of recent files by reading from a saved text file
 * containing pathnames to those files.
 *
 * @param evt
 */
private void populateRecentFileMenu(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_populateRecentFileMenu

    RecentFiles recFiles = new RecentFiles();

    String filenames[] =
      {
        "No Recent Leadsheets to Open"
      };

    if( recFiles.getSize() == 0 )
      {
        openRecentLeadsheetMenu.removeAll();

        for( String name : filenames )
          {
            final JMenuItem item = new JMenuItem(name);

            openRecentLeadsheetMenu.add(item);
          }
      }
    else
      {
        filenames = recFiles.convertToArray();
        openRecentLeadsheetMenu.removeAll();

        // Add each filename to the menu.

        for( String name : filenames )
          {
            final JMenuItem item = new JMenuItem(name);

            item.addActionListener(
                    new ActionListener()
            {
            public void actionPerformed(ActionEvent evt)
              {
                File selected = new File(item.getText());
                if( selected.exists() )
                  {
                    ImproVisor.setLastLeadsheetFileStem(selected.getName());
                    nameOfOpenFile = selected.getAbsolutePath();
                    try
                      {
                        setupLeadsheet(selected, false);
                        makeVisible(createRoadMapCheckBox.getState());
                      }
                    catch( Exception ij )
                      {
                      }
                  }
              }

            } // end of ActionListener embedded
                    );

            openRecentLeadsheetMenu.add(item);
          }

        openRecentLeadsheetMenu.add(new JSeparator());
        JMenuItem clear = new JMenuItem("clear all recent history");
        openRecentLeadsheetMenu.add(clear);
        clear.addActionListener(
                new ActionListener()
        {
        public void actionPerformed(ActionEvent evt)
          {
            try
              {
                File file = ImproVisor.getRecentFilesFile();
                if( file != null )
                  {
                    BufferedWriter recentFiles = new BufferedWriter(new FileWriter(file));
                    recentFiles.write("");
                    recentFiles.close();
                  }
              }
            catch( Exception e )
              {
              }
          }

        });
      }
}//GEN-LAST:event_populateRecentFileMenu

private void mostRecentLeadsheetNewWindowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostRecentLeadsheetNewWindowMIActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_mostRecentLeadsheetNewWindowMIActionPerformed

private void populateRecentLeadsheetNewWindow(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_populateRecentLeadsheetNewWindow
    RecentFiles recFiles = new RecentFiles();
    String filenames[] =
      {
        "No Recent Leadsheets to Open"
      };
    if( recFiles.getSize() == 0 || recFiles.getSize() == 1 )
      {
        openRecentLeadsheetNewWindowMenu.removeAll();

        for( String name : filenames )
          {
            final JMenuItem item = new JMenuItem(name);

            openRecentLeadsheetNewWindowMenu.add(item);
          }
      }
    else
      {
        filenames = recFiles.convertToArray();
        openRecentLeadsheetNewWindowMenu.removeAll();

        for( String name : filenames )
          {

            final JMenuItem item = new JMenuItem(name);

            item.addActionListener(
                    new ActionListener()
            {
            public void actionPerformed(ActionEvent evt)
              {
                File selected = new File(item.getText());
                if( selected.exists() )
                  {
                    try
                      {
                        openInNewWindow(selected);
                      }
                    catch( Exception ij )
                      {
                      }
                  }
                else
                  {
                  }
              }

            } // end of ActionListener embedded
                    );
            openRecentLeadsheetNewWindowMenu.add(item);
          }
        openRecentLeadsheetNewWindowMenu.add(new JSeparator());
        JMenuItem clear = new JMenuItem("Clear All Recent History");
        openRecentLeadsheetNewWindowMenu.add(clear);
        clear.addActionListener(
                new ActionListener()
        {
        public void actionPerformed(ActionEvent evt)
          {
            try
              {
                File file = ImproVisor.getRecentFilesFile();
                if( file != null )
                  {
                    BufferedWriter recentFiles = new BufferedWriter(new FileWriter(file));
                    recentFiles.write("");
                    recentFiles.close();
                  }
              }
            catch( Exception e )
              {
              }
          }

        });
      }
}//GEN-LAST:event_populateRecentLeadsheetNewWindow
private boolean skippedBack = false;

public void playAndCaptureChordAtIndex(int index)
  {
    indexOfLastChordPlayed = index;
    playChordAtIndex(index);
  }

public void chordStepForwardDo(){
    chordStepForwardButton.doClick();
}

private void chordStepForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordStepForwardButtonActionPerformed
    int currIndex;
    int nextChordIndex = 0;
    int indexOfChordToPlay = 0;
    int modedIndex = 0;
    int increment;
    autoScrollOnPlayback = true;
    if( skippedBack )
      {
        // will play current chord iff back button was pressed; will not move forward
        currIndex = midiSynth.getSlot();
        if( currIndex > chordProg.getSize() )
          {
            modedIndex = currIndex % chordProg.getSize();
          }
        midiSynth.setSlot(currIndex);
        playAndCaptureChordAtIndex(modedIndex);
        skippedBack = false;
      }
    else
      {
        //will move to and play next chord
        switch( playingStatus )
          {
            case PLAYING:
                midiSynth.pause();
                currIndex = midiSynth.getSlot();
                nextChordIndex = chordProg.getNextChordIndex(currIndex);
                if( currIndex >= chordProg.getSize() )
                  {
                    modedIndex = currIndex % chordProg.getSize();
                    indexOfChordToPlay = chordProg.getNextChordIndex(modedIndex);
                    increment = indexOfChordToPlay - modedIndex;
                    nextChordIndex = currIndex + increment;
                    indexOfChordToPlay = nextChordIndex % chordProg.getSize();
                    if( nextChordIndex >= -1 && increment >= 0 )
                      {
                        midiSynth.setSlot((long) nextChordIndex);
                      }
                    else
                      {
                        indexOfChordToPlay = (indexOfChordToPlay + 1) % chordProg.getSize();
                        nextChordIndex = nextChordIndex + 1;
                        midiSynth.setSlot((long) nextChordIndex);
                      }
                  }
                else
                  {
                    if( nextChordIndex >= 0 )
                      {
                        midiSynth.setSlot((long) nextChordIndex);
                        indexOfChordToPlay = nextChordIndex;
                      }
                    else
                      {
                        midiSynth.setSlot((long) 0);
                        playAndCaptureChordAtIndex(0);
                      }
                  }
                break;
            case PAUSED:
                currIndex = midiSynth.getSlot();
                nextChordIndex = chordProg.getNextChordIndex(currIndex);
                if( currIndex >= chordProg.getSize() )
                  {
                    modedIndex = currIndex % chordProg.getSize();
                    indexOfChordToPlay = chordProg.getNextChordIndex(modedIndex);
                    increment = indexOfChordToPlay - modedIndex;
                    nextChordIndex = currIndex + increment;
                    indexOfChordToPlay = nextChordIndex % chordProg.getSize();
                    if( nextChordIndex >= -1 && increment >= 0 )
                      {
                        midiSynth.setSlot((long) nextChordIndex);
                      }
                    else
                      {
                        indexOfChordToPlay = (indexOfChordToPlay + 1) % chordProg.getSize();
                        nextChordIndex = nextChordIndex + 1;
                        midiSynth.setSlot((long) nextChordIndex);
                      }
                  }
                else
                  {

                    if( nextChordIndex >= 0 )
                      {
                        midiSynth.setSlot((long) nextChordIndex);
                        indexOfChordToPlay = nextChordIndex;
                      }
                    else
                      {
                        midiSynth.setSlot((long) 0);
                        playAndCaptureChordAtIndex(0);
                      }
                  }
                break;
            case STOPPED:
                Stave tempStave = getCurrentStave();
                if( tempStave.getSelectionStart() >= 0 )
                  {
                    currIndex = tempStave.getSelectionStart() + ((chordProg.getSize()) * (currTabIndex));
                    playScoreBody(currIndex);
                    midiSynth.pause();
                    if( currIndex >= chordProg.getSize() )
                      {
                        modedIndex = currIndex % chordProg.getSize();
                        indexOfChordToPlay = chordProg.getCurrentChordIndex(modedIndex);
                        increment = indexOfChordToPlay - modedIndex;
                        nextChordIndex = currIndex + increment;
                        indexOfChordToPlay = nextChordIndex % chordProg.getSize();
                        if( nextChordIndex >= -1 )
                          {
                            midiSynth.setSlot((long) nextChordIndex);
                          }
                        else
                          {
                            indexOfChordToPlay = (indexOfChordToPlay + 1) % chordProg.getSize();
                            nextChordIndex = nextChordIndex + 1;
                            midiSynth.setSlot((long) nextChordIndex);
                          }
                      }
                    else
                      {
                        nextChordIndex = chordProg.getCurrentChordIndex(currIndex);
                        if( nextChordIndex >= 0 )
                          {
                            midiSynth.setSlot((long) nextChordIndex);
                          }
                        else
                          {
                            midiSynth.setSlot((long) 0);
                          }
                        indexOfChordToPlay = nextChordIndex;
                      }
                  }
                else
                  {
                    currIndex = 0;
                    nextChordIndex = currIndex;
                    playScoreBody(0);
                    midiSynth.pause();
                    midiSynth.setSlot(0);
                    indexOfChordToPlay = nextChordIndex;
                  }
                break;
          }
        if( nextChordIndex != -1 )
          {
            playAndCaptureChordAtIndex(indexOfChordToPlay);
          }
        else
          {
          }
      }
}//GEN-LAST:event_chordStepForwardButtonActionPerformed

public void chordStepBackDo(){
    chordStepBackButton.doClick();
}

private void chordStepBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordStepBackButtonActionPerformed
    int currChordIndex;
    autoScrollOnPlayback = true;
    skippedBack = true;
    currChordIndex = midiSynth.getSlot();
    int prevChordIndex = chordProg.getPrevUniqueChordIndex(currChordIndex);
    if( currChordIndex >= chordProg.getSize() )
      {
        int modedIndex = currChordIndex % chordProg.getSize();
        int prevChordMod = chordProg.getPrevUniqueChordIndex(modedIndex);
        int interval;
        if( prevChordMod != -1 )
          {
            interval = modedIndex - prevChordMod;
          }
        else
          {
            interval = 0;
          }
        prevChordIndex = currChordIndex - interval;
      }
    if( currChordIndex != -1 )
      {
        switch( playingStatus )
          {
            case PLAYING:
                midiSynth.pause();
                break;
            case PAUSED:
                break;
            case STOPPED:
                prevChordIndex = 0;
                playScoreBody(0);
                midiSynth.pause();
                midiSynth.setSlot(0);
                break;
          }
        midiSynth.setSlot((long) prevChordIndex);
        currChordIndex = prevChordIndex;
        indexOfLastChordPlayed = prevChordIndex;
      }
    else
      {
        switch( playingStatus )
          {
            case PLAYING:
                midiSynth.pause();
                break;
            case PAUSED:
                break;
            case STOPPED:
                playScoreBody(0);
                midiSynth.pause();
                break;
          }
      }
}//GEN-LAST:event_chordStepBackButtonActionPerformed

private void EmptyRoadMapAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_EmptyRoadMapAction
    openEmptyRoadmap();
}//GEN-LAST:event_EmptyRoadMapAction

private void roadMapThisAnalyzeAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_roadMapThisAnalyzeAction
    if( roadmapFrame == null )
      {
        roadMapThisAnalyze();
      }
    roadmapFrame.setVisible(true);
}//GEN-LAST:event_roadMapThisAnalyzeAction

private void printAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printAllMIActionPerformed
    printAllStaves();
}//GEN-LAST:event_printAllMIActionPerformed

private void grandStaveBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grandStaveBtnActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_grandStaveBtnActionPerformed

private void timeSignatureTopTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeSignatureTopTFActionPerformed
    // TODO add your handling code here:
}//GEN-LAST:event_timeSignatureTopTFActionPerformed

private void notateWIndowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_notateWIndowClosed
    closingThisWindow();
}//GEN-LAST:event_notateWIndowClosed

private void fileStepMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepMIActionPerformed
    //RecentFiles recFiles = new RecentFiles();
    //String first = recFiles.getFirstPathName();
    //File file = new File(first);
    //String dir = file.getParent();
    //currDirectoryLabel.setText("Current Directory: " + dir);
    showFileStepDialog();
}//GEN-LAST:event_fileStepMIActionPerformed

private void fileStepDialogWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_fileStepDialogWindowClosing
    hideFakeModalDialog(fileStepDialog);
}//GEN-LAST:event_fileStepDialogWindowClosing

private void stepForwardButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepForwardButtonActionPerformed
    fileStepForward();
}//GEN-LAST:event_stepForwardButtonActionPerformed

private void stepBackButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepBackButtonActionPerformed
    fileStepBackward();
}//GEN-LAST:event_stepBackButtonActionPerformed

private void fileStepForwardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepForwardBtnActionPerformed
    fileStepForward();
}//GEN-LAST:event_fileStepForwardBtnActionPerformed

private void fileStepBackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepBackBtnActionPerformed
    fileStepBackward();
}//GEN-LAST:event_fileStepBackBtnActionPerformed

private void createRoadMapCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createRoadMapCheckBoxActionPerformed
    String value = createRoadMapCheckBox.isSelected() ? "y" : "n";
    Preferences.setPreference(Preferences.CREATE_ROADMAP, value);
}//GEN-LAST:event_createRoadMapCheckBoxActionPerformed

public boolean getCreateRoadMapState()
  {
    return createRoadMapCheckBox.isSelected();
  }

private void layoutTFActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layoutTFActionPerformed
    savePrefs();
}//GEN-LAST:event_layoutTFActionPerformed

private void playSelectionMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playSelectionMIActionPerformed
  {//GEN-HEADEREND:event_playSelectionMIActionPerformed
      noCountIn();
      playCurrentSelection(false, getLoopCount(), PlayScoreCommand.USEDRUMS, "playSelectionMI");
  }//GEN-LAST:event_playSelectionMIActionPerformed

private void playSelectionToEndMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playSelectionToEndMIActionPerformed
  {//GEN-HEADEREND:event_playSelectionToEndMIActionPerformed
      noCountIn();
      playCurrentSelection(true, getLoopCount(), PlayScoreCommand.USEDRUMS, "playSelectionToEndMI");
  }//GEN-LAST:event_playSelectionToEndMIActionPerformed

private void phrasemarksMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_phrasemarksMIActionPerformed
  {//GEN-HEADEREND:event_phrasemarksMIActionPerformed
      // TODO add your handling code here:
  }//GEN-LAST:event_phrasemarksMIActionPerformed

private void midiLatencyTFactionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_midiLatencyTFactionPerformed
  {//GEN-HEADEREND:event_midiLatencyTFactionPerformed
      saveMidiLatency();
  }//GEN-LAST:event_midiLatencyTFactionPerformed

private void useBeamsMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useBeamsMIActionPerformed
  {//GEN-HEADEREND:event_useBeamsMIActionPerformed
      getCurrentStave().setBeaming(useBeamsMI.isSelected());
  }//GEN-LAST:event_useBeamsMIActionPerformed

private void recentStyleListValueChanged(javax.swing.event.ListSelectionEvent evt)//GEN-FIRST:event_recentStyleListValueChanged
  {//GEN-HEADEREND:event_recentStyleListValueChanged
      int currentIndex = sectionTable.getSelectionModel().getLeadSelectionIndex();
      updateStyleList((Style) recentStyleList.getSelectedValue(), currentIndex);

      sectionTableModel.tableRefresh();
      sectionTable.getSelectionModel().setSelectionInterval(currentIndex, currentIndex);
  }//GEN-LAST:event_recentStyleListValueChanged

private void clearHistoryMIrevertLeadsheetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_clearHistoryMIrevertLeadsheetActionPerformed
  {//GEN-HEADEREND:event_clearHistoryMIrevertLeadsheetActionPerformed
      cmReset();
  }//GEN-LAST:event_clearHistoryMIrevertLeadsheetActionPerformed

private void leadsheetPreferencesBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leadsheetPreferencesBtnActionPerformed
  {//GEN-HEADEREND:event_leadsheetPreferencesBtnActionPerformed
      changePrefTab(leadsheetBtn, leadsheetPreferences);
      showPreferencesDialog();
  }//GEN-LAST:event_leadsheetPreferencesBtnActionPerformed

private void chorusPreferencesBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chorusPreferencesBtnActionPerformed
  {//GEN-HEADEREND:event_chorusPreferencesBtnActionPerformed
      changePrefTab(chorusBtn, chorusPreferences);
      showPreferencesDialog();
  }//GEN-LAST:event_chorusPreferencesBtnActionPerformed

private void sectionPreferencesBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sectionPreferencesBtnActionPerformed
  {//GEN-HEADEREND:event_sectionPreferencesBtnActionPerformed
      changePrefTab(styleBtn, stylePreferences);
      showPreferencesDialog();
  }//GEN-LAST:event_sectionPreferencesBtnActionPerformed

private void globalPreferencesBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_globalPreferencesBtnActionPerformed
  {//GEN-HEADEREND:event_globalPreferencesBtnActionPerformed
      changePrefTab(globalBtn, globalPreferences);
      showPreferencesDialog();
  }//GEN-LAST:event_globalPreferencesBtnActionPerformed

private void midiPreferencesBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_midiPreferencesBtnActionPerformed
  {//GEN-HEADEREND:event_midiPreferencesBtnActionPerformed
      changePrefTab(midiBtn, midiPreferences);
      showPreferencesDialog();
  }//GEN-LAST:event_midiPreferencesBtnActionPerformed

private void contourPreferencesBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_contourPreferencesBtnActionPerformed
  {//GEN-HEADEREND:event_contourPreferencesBtnActionPerformed
      changePrefTab(contourBtn, contourPreferences);
      showPreferencesDialog();
  }//GEN-LAST:event_contourPreferencesBtnActionPerformed

private void showConstructionLinesMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showConstructionLinesMIActionPerformed
  {//GEN-HEADEREND:event_showConstructionLinesMIActionPerformed
      setShowConstructionLinesAndBoxes(showConstructionLinesMI.isSelected());
  }//GEN-LAST:event_showConstructionLinesMIActionPerformed

private void generationLeadSpinnerChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_generationLeadSpinnerChanged
  {//GEN-HEADEREND:event_generationLeadSpinnerChanged
      lickgenFrame.setGap(Double.parseDouble(generationGapSpinner.getValue().toString()));
  }//GEN-LAST:event_generationLeadSpinnerChanged

private void scoreTabMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_scoreTabMousePressed
  {//GEN-HEADEREND:event_scoreTabMousePressed
      requestFocusInWindow();
      int indexAtLocation = scoreTab.indexAtLocation(evt.getX(), evt.getY());
      if( indexAtLocation > -1 )
        {
          scoreTab.setSelectedIndex(indexAtLocation);
        }
      if( evt.isPopupTrigger() )
        {
          delTabPopupMenuItem.setEnabled(scoreTab.getTabCount() > 1);
          tabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
        }
  }//GEN-LAST:event_scoreTabMousePressed

private void sendSetBankCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_sendSetBankCheckBoxActionPerformed
  {//GEN-HEADEREND:event_sendSetBankCheckBoxActionPerformed
      Preferences.setMidiSendBankSelect(sendSetBankCheckBox.isSelected());
  }//GEN-LAST:event_sendSetBankCheckBoxActionPerformed

private void showTrackingLineCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showTrackingLineCheckBoxActionPerformed
  {//GEN-HEADEREND:event_showTrackingLineCheckBoxActionPerformed
      Preferences.setShowTrackingLine(trackCheckBox.isSelected());
  }//GEN-LAST:event_showTrackingLineCheckBoxActionPerformed

private void melodyChannelSpinnerChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_melodyChannelSpinnerChanged
  {//GEN-HEADEREND:event_melodyChannelSpinnerChanged
      ImproVisor.setMelodyChannel(((Integer) melodyChannelSpinner.getValue()).intValue() - 1);
  }//GEN-LAST:event_melodyChannelSpinnerChanged

private void chordChannelSpinnerChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_chordChannelSpinnerChanged
  {//GEN-HEADEREND:event_chordChannelSpinnerChanged
      ImproVisor.setChordChannel(((Integer) chordChannelSpinner.getValue()).intValue() - 1);
  }//GEN-LAST:event_chordChannelSpinnerChanged

private void bassChannelSpinnerChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_bassChannelSpinnerChanged
  {//GEN-HEADEREND:event_bassChannelSpinnerChanged
      ImproVisor.setBassChannel(((Integer) bassChannelSpinner.getValue()).intValue() - 1);
  }//GEN-LAST:event_bassChannelSpinnerChanged

private void drumChannelSpinnerChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_drumChannelSpinnerChanged
  {//GEN-HEADEREND:event_drumChannelSpinnerChanged
      ImproVisor.setDrumChannel(((Integer) drumChannelSpinner.getValue()).intValue() - 1);
  }//GEN-LAST:event_drumChannelSpinnerChanged

private void improviseButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_improviseButtonActionPerformed
  {//GEN-HEADEREND:event_improviseButtonActionPerformed
      improviseButtonToggled();
  }//GEN-LAST:event_improviseButtonActionPerformed

private void importMidiMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_importMidiMIActionPerformed
  {//GEN-HEADEREND:event_importMidiMIActionPerformed
      importMIDI();
  }//GEN-LAST:event_importMidiMIActionPerformed

public void importMIDI()
  {
    setMode(Mode.IMPORTING_MIDI);
    if( midiImportFrame == null )
      {
        midiImportFrame = new MidiImportFrame(this);
      }
    midiImportFrame.loadFileAndMenu();
    midiImportFrame.setLocation(midiImportXoffset, midiImportYoffset);
    midiImportFrame.setVisible(true);
  }

public void closeMidiImportFrame()
  {
    midiImportFrame = null;
  }

private void insertChorusTabMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_insertChorusTabMIActionPerformed
  {//GEN-HEADEREND:event_insertChorusTabMIActionPerformed
      addTab();
  }//GEN-LAST:event_insertChorusTabMIActionPerformed

private void textEntryMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_textEntryMouseClicked
  {//GEN-HEADEREND:event_textEntryMouseClicked
      textRequestFocus();
  }//GEN-LAST:event_textEntryMouseClicked

private void enterTextMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_enterTextMIActionPerformed
  {//GEN-HEADEREND:event_enterTextMIActionPerformed
      textRequestFocus();
  }//GEN-LAST:event_enterTextMIActionPerformed

private void firstTimePrefsMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_firstTimePrefsMIActionPerformed
  {//GEN-HEADEREND:event_firstTimePrefsMIActionPerformed
      ImproVisor.openFirstTimeDialog(this);
  }//GEN-LAST:event_firstTimePrefsMIActionPerformed

private void recordMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_recordMIActionPerformed
  {//GEN-HEADEREND:event_recordMIActionPerformed
      recordFromMidi();
  }//GEN-LAST:event_recordMIActionPerformed

private void midiRecordSnapChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_midiRecordSnapChanged
  {//GEN-HEADEREND:event_midiRecordSnapChanged
      Preferences.setMidiRecordSnap(midiRecordSnapSpinner.getValue().toString());
  }//GEN-LAST:event_midiRecordSnapChanged

    private void stepKeyboardMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepKeyboardMIActionPerformed
        openStepKeyboard();
    }//GEN-LAST:event_stepKeyboardMIActionPerformed

private int indexOfLastChordPlayed = 0;

public void chordReplayDo(){
    chordReplayButton.doClick();
}

    private void chordReplayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordReplayButtonActionPerformed

        midiSynth.setSlot(indexOfLastChordPlayed);
        playAndCaptureChordAtIndex(indexOfLastChordPlayed);
    }//GEN-LAST:event_chordReplayButtonActionPerformed

    private void usePreviousStyleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_usePreviousStyleButtonActionPerformed

        int index = sectionTable.getSelectionModel().getLeadSelectionIndex();
        if( index <= 0 || index >= sectionInfo.size() )
          {
            return;
          }
        SectionRecord record = sectionInfo.getSectionRecordByIndex(index);
        record.setUsePreviousStyle();
        sectionTableModel.tableRefresh();
        sectionTable.getSelectionModel().setSelectionInterval(index, index);
    }//GEN-LAST:event_usePreviousStyleButtonActionPerformed

private void setUseImproviseCheckBox()
  {
    setAutoImprovisation(false);
    setContinuousImprovisation(false);
  }

private void setContinuousImprovisation(boolean value)
  {
    continuousImprovisation = value;
  }

/**
 * Returns the length of trading
 *
 * @return
 */
public int getTradeLength()
  {
    int tradeLength = autoImprovisation.getImproInterval() / 2;
    return tradeLength;
  }

/**
 * Returns true if trading
 *
 * @return
 */
public boolean getAutoImprovisation()
  {
    return !originalGeneration;
  }

/**
 * Enable or disable auto improvisation. Disabled means that only the original
 * style of improvisation is used, pressing a button to generate. Enabled means
 * that improvisation may occur automatically with playback, trading in a manner
 * specified in the menu.
 *
 * @param value
 */
public void setAutoImprovisation(boolean value)
  {
    //originalGeneration = !value;
    autoImprovisation.setSelected(value);
    if( value )
      {
        //openLickGenerator();  //FIX.
        //lickgenFrame.toBack();
      }
  }

public void setFrameSize(int value)
  {
    //frameSizeComboBox.setSelectedItem(new Integer(value));
  }

public void setPollRate(int value)
  {
    //pollRateComboBox.setSelectedItem(new Integer(value));
  }

public void setAudioplayTriplets(boolean value)
  {
    //playTripletsCheckBox.setSelected(value);
  }

public void setAudioNoteResolution(int subdivisions)
  {
    // Need to search for menu item of desired resolution
    AudioInputResolutionComboBoxModel model = AudioInputResolutionComboBoxModel.getAudioInputResolutionComboBoxModel();
    int n = model.getSize();
    for( int i = 0; i < n; i++ )
      {
        NoteResolutionInfo info = (NoteResolutionInfo) model.getElementAt(i);
        if( info.getWholeNoteSubdivisions() == subdivisions )
          {
            model.setSelectedItem(info);
            return;
          }
      }
    ErrorLog.log(ErrorLog.WARNING, "audio: specified resolution " + subdivisions + " subdivisions not found");
  }

public void setKconstantSlider(double value)
  {
    int intValue = (int) (1000 * value);
    //k_constantSlider.setValue(intValue);
  }

boolean firstTime = true;

    private void nWaySplitComboBoxActionHandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nWaySplitComboBoxActionHandler

    }//GEN-LAST:event_nWaySplitComboBoxActionHandler

    private void noteCursorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteCursorBtnActionPerformed

        if( useNoteCursor )
          {
            setUseNoteCursor(false);
            noteCursorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "graphics/cursors/blueNoteLineCursor.png")));
          }
        else
          {
            setUseNoteCursor(true);
            noteCursorBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource(
                    "graphics/toolbar/cursor.gif")));
          }
    }//GEN-LAST:event_noteCursorBtnActionPerformed

public void setMinPitch(int value)
  {
    //minPitchSpinner.setValue(value);
  }

public void setMaxPitch(int value)
  {
    //maxPitchSpinner.setValue(value);
  }
//view selection
    private void replaceWithPhiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithPhiActionPerformed
        boolean checked = replaceWithPhi.getState();
        updatePhiAndDelta(checked, replaceWithDelta.getState());
    }//GEN-LAST:event_replaceWithPhiActionPerformed

    private void replaceWithDeltaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithDeltaActionPerformed
        boolean checked = replaceWithDelta.getState();
        updatePhiAndDelta(replaceWithPhi.getState(), checked);
    }//GEN-LAST:event_replaceWithDeltaActionPerformed
//global pref
    private void replaceWithPhiCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithPhiCheckBoxActionPerformed
        //markermarkermarker
        boolean checked = replaceWithPhiCheckBox.isSelected();
        updatePhiAndDelta(checked, replaceWithDeltaCheckBox.isSelected());
    }//GEN-LAST:event_replaceWithPhiCheckBoxActionPerformed

    private void replaceWithDeltaCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithDeltaCheckBoxActionPerformed
        //markermarkermarker
        boolean checked = replaceWithDeltaCheckBox.isSelected();
        updatePhiAndDelta(replaceWithPhiCheckBox.isSelected(), checked);
    }//GEN-LAST:event_replaceWithDeltaCheckBoxActionPerformed

    private void autoFillMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoFillMIActionPerformed
        boolean isSelected = autoFillMI.isSelected();
        MelodyPart melody = getCurrentStave().getMelodyPart();
        melody.setAutoFill(isSelected);
        noteLenDialog.setLocationRelativeTo(this);
        noteLenDialog.setVisible(!isSelected);
    }//GEN-LAST:event_autoFillMIActionPerformed

    private void adviceScrollListBricksMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adviceScrollListBricksMouseClicked
        adviceSelected(adviceScrollListBricks.getSelectedValue());
    }//GEN-LAST:event_adviceScrollListBricksMouseClicked

    private void adviceScrollListBricksKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adviceScrollListBricksKeyPressed
    }//GEN-LAST:event_adviceScrollListBricksKeyPressed

    private void adviceScrollListBricksKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adviceScrollListBricksKeyReleased
        switch( evt.getKeyCode() )
          {
            case java.awt.event.KeyEvent.VK_UP:
            case java.awt.event.KeyEvent.VK_DOWN:
                adviceSelected(adviceScrollListBricks.getSelectedValue());
                break;

            default:
                // Delegate to main window
                adviceKeyPressed(evt);
                break;
          }
    }//GEN-LAST:event_adviceScrollListBricksKeyReleased

    private void brickRadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brickRadioButtonActionPerformed
        saveSelectionMode = ExtractMode.BRICK;
        System.out.println("selectionMode = " + saveSelectionMode);
    }//GEN-LAST:event_brickRadioButtonActionPerformed

    private void enterLickTitleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterLickTitleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_enterLickTitleActionPerformed

    private void adviceTabbedPaneMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adviceTabbedPaneMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_adviceTabbedPaneMouseClicked

    private void adviceTabbedPaneMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_adviceTabbedPaneMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_adviceTabbedPaneMouseReleased

    private void scoreTabMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTabMouseClicked
    }//GEN-LAST:event_scoreTabMouseClicked

    private void audioInputResolutionComboBox1Changed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_audioInputResolutionComboBox1Changed
    {//GEN-HEADEREND:event_audioInputResolutionComboBox1Changed
        // TODO add your handling code here:
    }//GEN-LAST:event_audioInputResolutionComboBox1Changed

    private void frameSizeComboBox1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_frameSizeComboBox1ActionPerformed
    {//GEN-HEADEREND:event_frameSizeComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_frameSizeComboBox1ActionPerformed

    private void pollRateComboBox1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pollRateComboBox1ActionPerformed
    {//GEN-HEADEREND:event_pollRateComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pollRateComboBox1ActionPerformed

    private void playTripletsCheckBox1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playTripletsCheckBox1ActionPerformed
    {//GEN-HEADEREND:event_playTripletsCheckBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_playTripletsCheckBox1ActionPerformed

    private void k_constantSlider1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_k_constantSlider1StateChanged
    {//GEN-HEADEREND:event_k_constantSlider1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_k_constantSlider1StateChanged

    private void rmsThresholdSlider1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_rmsThresholdSlider1StateChanged
    {//GEN-HEADEREND:event_rmsThresholdSlider1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_rmsThresholdSlider1StateChanged

    private void confidenceThresholdSlider1confidenceThresholdStateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_confidenceThresholdSlider1confidenceThresholdStateChanged
    {//GEN-HEADEREND:event_confidenceThresholdSlider1confidenceThresholdStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_confidenceThresholdSlider1confidenceThresholdStateChanged

    private void minPitchSpinner1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_minPitchSpinner1StateChanged
    {//GEN-HEADEREND:event_minPitchSpinner1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_minPitchSpinner1StateChanged

    private void maxPitchSpinner1StateChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_maxPitchSpinner1StateChanged
    {//GEN-HEADEREND:event_maxPitchSpinner1StateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_maxPitchSpinner1StateChanged

    private void pitchRangePresetComboBox1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_pitchRangePresetComboBox1ActionPerformed
    {//GEN-HEADEREND:event_pitchRangePresetComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pitchRangePresetComboBox1ActionPerformed

private Point currentMouseLocation;
    private void scoreTabMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTabMouseDragged
        tabDragged = true;
        currentMouseLocation = evt.getPoint();
        scoreTab.repaint();
    }//GEN-LAST:event_scoreTabMouseDragged

private boolean tabDragged;
    private void scoreTabMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scoreTabMouseReleased
        if( evt.isPopupTrigger() )
          {
            delTabPopupMenuItem.setEnabled(scoreTab.getTabCount() > 1);
            tabPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
          }
        else if( tabDragged )
          {
            destinationTabIndex = scoreTab.indexAtLocation(evt.getX(), evt.getY());
            moveTab();
          }
        tabDragged = false;
        scoreTab.repaint();
    }//GEN-LAST:event_scoreTabMouseReleased

    private void addTabPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabPopupMenuItemActionPerformed
        addTab();
    }//GEN-LAST:event_addTabPopupMenuItemActionPerformed

    private void delTabPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delTabPopupMenuItemActionPerformed
        deleteTab();
    }//GEN-LAST:event_delTabPopupMenuItemActionPerformed

    private void delTabCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delTabCheckBoxActionPerformed
        hideDelTabDialog = delTabCheckBox.isSelected();
    }//GEN-LAST:event_delTabCheckBoxActionPerformed

private int destinationTabIndex;

public void moveTab()
  {
    if( destinationTabIndex >= 0 && currTabIndex != destinationTabIndex )
      {
        partList.move(currTabIndex, destinationTabIndex);
        score.movePart(currTabIndex, destinationTabIndex);
        cm.changedSinceLastSave(true);
        setupArrays();
        scoreTab.setSelectedIndex(destinationTabIndex);
      }
  }

    private void reAnalyzeMIAction(java.awt.event.ActionEvent evt)//GEN-FIRST:event_reAnalyzeMIAction
    {//GEN-HEADEREND:event_reAnalyzeMIAction
        reAnalyze();
    }//GEN-LAST:event_reAnalyzeMIAction

    private void renameTabPopupMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_renameTabPopupMenuItemActionPerformed
        getCurrentStave().partTitleFocus();
    }//GEN-LAST:event_renameTabPopupMenuItemActionPerformed

    private void insertSectionBeforePMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSectionBeforePMIActionPerformed
        sectionTableModel.insertSection(false, false);
        savePrefs();
    }//GEN-LAST:event_insertSectionBeforePMIActionPerformed

    private void insertPhraseBeforePMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertPhraseBeforePMIActionPerformed
        sectionTableModel.insertSection(false, true);
        savePrefs();
    }//GEN-LAST:event_insertPhraseBeforePMIActionPerformed

    private void mergeSectionPreviousPMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeSectionPreviousPMIActionPerformed
        sectionTableModel.mergeSection(false);
        savePrefs();
    }//GEN-LAST:event_mergeSectionPreviousPMIActionPerformed

    private void insertPhraseAfterPMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertPhraseAfterPMIActionPerformed
        sectionTableModel.insertSection(true, true);
        savePrefs();
    }//GEN-LAST:event_insertPhraseAfterPMIActionPerformed

    private void insertSectionAfterPMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertSectionAfterPMIActionPerformed
        sectionTableModel.insertSection(true, false);
        savePrefs();
    }//GEN-LAST:event_insertSectionAfterPMIActionPerformed

    private void mergeSectionFollowingPMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mergeSectionFollowingPMIActionPerformed
        sectionTableModel.mergeSection(true);
        savePrefs();
    }//GEN-LAST:event_mergeSectionFollowingPMIActionPerformed

private int noteLength = 60;
private boolean isTriplet = false;
private boolean isDotted = false;

    private void noteLen16BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLen16BtnActionPerformed
        noteLength = 30;
        setNoteLength();
    }//GEN-LAST:event_noteLen16BtnActionPerformed

    private void noteLen8BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLen8BtnActionPerformed
        noteLength = 60;
        setNoteLength();
    }//GEN-LAST:event_noteLen8BtnActionPerformed

    private void noteLen4BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLen4BtnActionPerformed
        noteLength = 120;
        setNoteLength();
    }//GEN-LAST:event_noteLen4BtnActionPerformed

    private void noteLen2BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLen2BtnActionPerformed
        noteLength = 240;
        setNoteLength();
    }//GEN-LAST:event_noteLen2BtnActionPerformed

    private void noteLen1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLen1BtnActionPerformed
        noteLength = 480;
        setNoteLength();
    }//GEN-LAST:event_noteLen1BtnActionPerformed

    private void noteLenDialogWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_noteLenDialogWindowClosed
        MelodyPart melody = getCurrentStave().getMelodyPart();
        melody.setAutoFill(true);
        noteLenDialog.setVisible(false);
        autoFillMI.setSelected(true);
    }//GEN-LAST:event_noteLenDialogWindowClosed

    private void noteLen32BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLen32BtnActionPerformed
        noteLength = 15;
        setNoteLength();
    }//GEN-LAST:event_noteLen32BtnActionPerformed

    private void noteLenDottedCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLenDottedCheckBoxActionPerformed
        noteLenTripletCheckBox.setSelected(false);
        updateDottedAndTriplet();
    }//GEN-LAST:event_noteLenDottedCheckBoxActionPerformed

    private void noteLenTripletCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noteLenTripletCheckBoxActionPerformed
        noteLenDottedCheckBox.setSelected(false);
        updateDottedAndTriplet();
    }//GEN-LAST:event_noteLenTripletCheckBoxActionPerformed

    private void ReloadSuperColliderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ReloadSuperColliderButtonActionPerformed
      
        SCHandler handler = new SCHandler();
        handler.openSC();

    }//GEN-LAST:event_ReloadSuperColliderButtonActionPerformed

    private void OpenHelpTextMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_OpenHelpTextMouseClicked
        //Pull up help menu
        openHelpDialog();
    }//GEN-LAST:event_OpenHelpTextMouseClicked

    private void showNoteNameAboveCursorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_showNoteNameAboveCursorMIActionPerformed
    {//GEN-HEADEREND:event_showNoteNameAboveCursorMIActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_showNoteNameAboveCursorMIActionPerformed

    private void soloGeneratorMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_soloGeneratorMIActionPerformed
    {//GEN-HEADEREND:event_soloGeneratorMIActionPerformed
        if( themeWeaver == null )
          {
          themeWeaver = new ThemeWeaver(lickgen, this, cm);
          }
        themeWeaver.setVisible(true);
    }//GEN-LAST:event_soloGeneratorMIActionPerformed

int quantizeResolution = 60;

    private void quantizeComboBoxscaleComboReleased(java.awt.event.MouseEvent evt)//GEN-FIRST:event_quantizeComboBoxscaleComboReleased
    {//GEN-HEADEREND:event_quantizeComboBoxscaleComboReleased
    }//GEN-LAST:event_quantizeComboBoxscaleComboReleased

    private void quantizeComboBoxscaleChosen(java.awt.event.ActionEvent evt)//GEN-FIRST:event_quantizeComboBoxscaleChosen
    {//GEN-HEADEREND:event_quantizeComboBoxscaleChosen
        MelodyPart originalPart = getCurrentMelodyPart();

        MelodyPart quantizedPart = new MelodyPart();
        String quantizeString;
        int quantizeResolution;

        quantizeString = ((String) quantizeComboBox.getSelectedItem());

// Due to misuse of iterator in quantize and quantizeNoRes,
// this doesn't really work.
//        // case: nothing valid selected, attempt to select null option
//        if( quantizeString.equals("-") )
//          {
//            System.out.println("Please make a valid selection.");
//          }
//        // case: selected option to quantize the string according to the best 
//        // resolution found and other factors of MelodyPart.quantize()
//        
//        else if( quantizeString.equals("Best") )
//          {
//            quantizeResolution = originalPart.getBestResolution();
//            quantizeComboBox.setSelectedItem((Integer.toString(quantizeResolution)));
//            System.out.println("Best resolution toString: " + Integer.toString(quantizeResolution));
//            quantizedPart = MelodyPart.quantize(originalPart);
//            addChorus(quantizedPart);
//          }
//        //case: selected integer value, resolution applied
//        else
          {
            quantizeResolution = getSlotsFromString(quantizeString);
            if( quantizeResolution > 0 )
              {
              quantizedPart = originalPart.applyResolution(quantizeResolution);
              //System.out.println("Applied Resolution = " + quantizeResolution);
              // Doesn't work; see above
              //quantizedPart = MelodyPart.quantizeNoRes(quantizedPart);
              quantizedPart.setInstrument(getCurrentMelodyPart().getInstrument()); //fix bug 60
              addChorus(quantizedPart);
              }
          }
    }//GEN-LAST:event_quantizeComboBoxscaleChosen

    private void notateGrammarMenuMousePressed(java.awt.event.MouseEvent evt)//GEN-FIRST:event_notateGrammarMenuMousePressed
    {//GEN-HEADEREND:event_notateGrammarMenuMousePressed
        //populateNotateGrammarMenu();
    }//GEN-LAST:event_notateGrammarMenuMousePressed

    private void tradingMenuActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_tradingMenuActionPerformed
    {//GEN-HEADEREND:event_tradingMenuActionPerformed
        populateTradingMenu();
    }//GEN-LAST:event_tradingMenuActionPerformed

    
    /**
     * guideToneLineActionPerformed
     * Makes guideToneLineDialog visible
     * @param evt event triggered when guide tone line menu item is clicked
     */
    private void guideToneLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guideToneLineActionPerformed
        guideToneLineDialog.setVisible(true);
    }//GEN-LAST:event_guideToneLineActionPerformed

    private void playBtnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_playBtnStateChanged
        guideToneLineDialog.updatePlayButtons();
    }//GEN-LAST:event_playBtnStateChanged

    private void pauseBtnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_pauseBtnStateChanged
        guideToneLineDialog.updatePlayButtons();
    }//GEN-LAST:event_pauseBtnStateChanged

    private void stopBtnStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_stopBtnStateChanged
        guideToneLineDialog.updatePlayButtons();
    }//GEN-LAST:event_stopBtnStateChanged

    private int [] range = {-1, -1};
    private boolean shouldFilter = false;
    private void rangeFilterBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rangeFilterBtnActionPerformed
        RangeChooser midiFilter = new RangeChooser(this, range[0], range[1], 1, true);
        range = midiFilter.getRange();
        shouldFilter = true;
    }//GEN-LAST:event_rangeFilterBtnActionPerformed

    private void clearRangeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearRangeBtnActionPerformed
        range[0] = 0;
        range[1] = 128;
        shouldFilter = false;
    }//GEN-LAST:event_clearRangeBtnActionPerformed

    private void tradingWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradingWindowActionPerformed
        // Open New Trading window
        trader = new TradingWindow(this);
        trader.setVisible(true);
    }//GEN-LAST:event_tradingWindowActionPerformed

    private void delAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delAllMIActionPerformed
        delAllMelody();
    }//GEN-LAST:event_delAllMIActionPerformed

    private void tradingWindow2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradingWindow2ActionPerformed
        // TODO add your handling code here:
        trader2.setVisible(true);
        System.out.println("Opening the second trading window");
    }//GEN-LAST:event_tradingWindow2ActionPerformed
void delAllMelody()
  {
    Trace.log(2, "delete all melody");
    
        int prevTab = scoreTab.getSelectedIndex();
        
        cm.execute(new DeleteAllCommand(scoreTab.getTabCount(), this));

        getCurrentStave().setPasteFromStart(
                getCurrentSelectionStart());

        getCurrentStave().setPasteFromEnd(
                getCurrentSelectionEnd());

        setCurrentSelectionEnd(getCurrentSelectionStart());
        
       
        for (int i=0; i<scoreTab.getTabCount(); i++){
            scoreTab.setSelectedComponent(staveScrollPane[i]);
            setCurrentSelectionEnd(getCurrentSelectionStart());
        }
        scoreTab.setSelectedComponent(staveScrollPane[prevTab]);

        setItemStates();

    redoAdvice();
  }
    
    
public boolean getFilter(){
    return shouldFilter;
}
    
public int getHigh(){
    return range[1];
}

public int getLow(){
    return range[0];
}
    
private void updateDottedAndTriplet()
  {
    isDotted = noteLenDottedCheckBox.isSelected();
    isTriplet = noteLenTripletCheckBox.isSelected();
    setNoteLength();
  }

public int getSlotsFromString(String s)
  {
    if( s.equals("1/4") )
      {
        return 480;
      }
    else if( s.equals("1/3") )
      {
        return 360;
      }
    else if( s.equals("1/2") )
      {
        return 240;
      }
    else if( s.equals("3/4") )
      {
        return 160;
      }
    else if( s.equals("1") )
      {
        return 120;
      }
    else if( s.equals("3/2") )
      {
        return 80;
      }
    else if( s.equals("2") )
      {
        return 60;
      }
    else if( s.equals("3") )
      {
        return 40;
      }
    else if( s.equals("4") )
      {
        return 30;
      }
    else if( s.equals("6") )
      {
        return 20;
      }
    else if( s.equals("8") )
      {
        return 15;
      }
    else if( s.equals("12") )
      {
        return 10;
      }
    else if( s.equals("24") )
      {
        return 5;
      }
    else if( s.equals("60") )
      {
        return 2;
      }
    else if( s.equals("120") )
      {
        return 1;
      }
    else
      {
        System.out.println("String not understood as valid resolution");
        return 0;
      }
  }

public void setNoteLength()
  {
    getCurrentStave().getMelodyPart().setNoteLength(noteLength, isTriplet, isDotted);
  }

public boolean getPhiStatus()
  {
    return replaceWithPhi.getState();
  }

public void setPhiStatus(boolean phi)
  {
    replaceWithPhi.setState(phi);
  }

public boolean getDeltaStatus()
  {
    return replaceWithDelta.getState();
  }

public void setDeltaStatus(boolean delta)
  {
    replaceWithDelta.setState(delta);
  }

public void setRMSThreshold(double value)
  {
    int intValue = (int) (10 * value);
    //rmsThresholdSlider.setValue(intValue);
  }

public void setConfidenceThreshold(double value)
  {
    int intValue = (int) (100 * value);
    //confidenceThresholdSlider.setValue(intValue);
  }

/**
 * Focus on input from textEntry field, until return is pressed, at which point
 * staveRequestFocus() will be called (in textEntryActionHandler).
 */
public void textRequestFocus()
  {
    String text = textEntry.getText();

    textEntryLabel.setForeground(Color.black);
    textEntry.requestFocusInWindow();
    textEntry.setEnabled(true);
    int length = text.length();
    //textEntry.setSelectionStart(length-1);
    //textEntry.setSelectionEnd(length);
  }

int improviseStartSlot, improviseEndSlot;
boolean improvisationOn = false;
boolean improvOn = false;

public void improviseButtonToggled()
  {
    improvisationOn = improviseButton.isSelected();
    improvOn = true;
    if( improvisationOn )
      {
        improviseButton.setBackground(new Color(255, 0, 0));
        improviseButton.setText("<html><center>Stop</center></html>");

        playAll();
      }
    else
      {
        stopPlaying();
      }
  }

public void improvisationOff()
  {
    //System.out.println("improvisationOff");
    improvisationOn = false;
    improviseButton.setSelected(false);
    lickgenFrame.setRecurrent(false);
    improviseButton.setBackground(new Color(0, 255, 0));
    improviseButton.setText("<html><center>Improv</center></html>");
  }

public void improviseContinuously()
  {
    //System.out.println("\nImprovise Continuously");
    // Looping is also automatically implied with improvisation.
    loopButton.setSelected(false);
    lickgenFrame.setRecurrent(true);

    adjustSelection();
    Stave stave = getCurrentStave();
    improviseStartSlot = stave.getSelectionStart();
    improviseEndSlot = stave.getSelectionEnd();
    
    
    recurrentIteration = 1;
    originalGenerate(lickgen, improviseStartSlot, improviseEndSlot);
    //System.out.println("*** return from improviseContinuously");
  }

public void setShowConstructionLinesAndBoxes(boolean value)
  {
    showConstructionLinesAndBoxes = value;
  }

public void setGenerationGap(double value)
  {
    generationGapSpinner.setValue(0.01 * (int) (100 * value));
  }

public double getGenerationGap()
  {
    return (Double) generationGapSpinner.getValue();
  }

boolean recurrentImprovisation = false;

/**
 * * This is for calling from lickgen frame
 *
 * @return
 */
public void setRecurrent(boolean value)
  {
    recurrentImprovisation = value;
  }

public boolean showPhrasemarks()
  {
    return phrasemarksMI.getState();
  }

public void fileStepForward()
  {
    stopPlaying("file step forward");
    RecentFiles recFiles = new RecentFiles();
    String first = recFiles.getFirstPathName();
    File file;
    String dir;
    if( first == null )
      {
        file = new File(leadsheetDirName);
        dir = leadsheetDirName;
      }
    else
      {
        file = new File(first);
        dir = file.getParent();
      }
    File[] lsFiles = getLeadsheetsFromDir(dir);
    int currPos = findPosOfCurrLs(lsFiles, first);
    File nextFile;
    if( !(currPos + 1 >= lsFiles.length) )
      {
        nextFile = lsFiles[currPos + 1];
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
                                           "Save modifications?",
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
        setupLeadsheet(nextFile, false);
        if( getAutoCreateRoadMap() )
          {
            roadMapThisAnalyze();
          }
      }
  }

public void fileStepBackward()
  {
    stopPlaying("file step backward");
    RecentFiles recFiles = new RecentFiles();
    String first = recFiles.getFirstPathName();
    File file;
    String dir;
    if( first == null )
      {
        file = new File(leadsheetDirName);
        dir = leadsheetDirName;
      }
    else
      {
        file = new File(first);
        dir = file.getParent();
      }
    File[] lsFiles = getLeadsheetsFromDir(dir);
    int currPos = findPosOfCurrLs(lsFiles, first);
    File nextFile;
    if( !(currPos - 1 < 0) )
      {
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
                                           "Save modifications?",
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
        nextFile = lsFiles[currPos - 1];
        setupLeadsheet(nextFile, false);
        if( getAutoCreateRoadMap() )
          {
            roadMapThisAnalyze();
          }
      }
  }

private int findPosOfCurrLs(File[] lsFiles, String name)
  {
    String[] lsFilesName = new String[lsFiles.length];
    for( int i = 0; i < lsFiles.length; i++ )
      {
        lsFilesName[i] = lsFiles[i].getAbsolutePath();
      }
    int result = 0;
    for( int i = 0; i < lsFiles.length; i++ )
      {
        if( lsFilesName[i].equals(name) )
          {
            result = i;
          }
      }
    return result;
  }

static FileFilter lsFilter = new FileFilter()
{
public boolean accept(File file)
  {
    return file.getName().endsWith(".ls");
  }

};
static FileFilter dirFilter = new FileFilter()
{
public boolean accept(File file)
  {
    return file.isDirectory();
  }

};

private File[] getLeadsheetsFromDir(String dir)
  {
    File file = new File(dir);
    return file.listFiles(lsFilter);
  }

private File[] getDirsFromDir(String dir)
  {
    File file = new File(dir);
    return file.listFiles(dirFilter);
  }

/**
 * If this Notate frame was created from a roadmap, tell that roadmap about its
 * closing.
 */
private void closingThisWindow()
  {
    if( createdByRoadmap != null )
      {
        createdByRoadmap.resetAuxNotate();
      }
    noteLenDialog.dispose();
  }

public void openInNewWindow(File selectedFile)
  {
    Score newScore = new Score();
    if( readLeadsheetFile(selectedFile, newScore) )
      {
        //create a new window and show the score
        Notate newNotate = new Notate(newScore,
                                      this.adv,
                                      this.impro,
                                      getNewXlocation(),
                                      getNewYlocation());
        newNotate.setFileName(selectedFile.getName());
        newNotate.setTitle(newScore.getTitle());
        newNotate.setPrefsDialog();
        newNotate.makeVisible(this);
      }
  }

public Notate newNotateWithScore(Score newScore, int x, int y)
  {
    //create a new window and show the score

    Notate newNotate = new Notate(newScore,
                                  this.adv,
                                  this.impro,
                                  x,
                                  y);

    newNotate.setupScore(newScore);
    return newNotate;
  }

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

private void tradingMenuAction(java.awt.event.ActionEvent evt)
  {
    JMenuItem item = (JMenuItem) evt.getSource();
    String stem = item.getText();
    tradingMenu.setText(stem);
  }

boolean ifCycle = false;
boolean ifShuffle = false;
//action for cycle/shuffle buttons
private void notateGrammarMenuActOpt(java.awt.event.ActionEvent evt)
  { 
    JMenuItem item = (JMenuItem) evt.getSource();
    String stem = item.getText();
    if (stem.equals("Cycle")){ifCycle = true; ifShuffle = false; shufCount = 0;
        for (int x = 0; x < gramList.size(); x++){
        if (gramList.get(x).equals(fullName)){
            cycCount = x;
        }
    }
        String temp = gramList.get(cycCount).substring(0, gramList.get(cycCount).length() - GrammarFilter.EXTENSION.length());
        notateGrammarMenu.setText(temp + "(Cycle)");
    }
    if (stem.equals("Shuffle")){ifShuffle = true; ifCycle = false; cycCount = 0;
        Collections.shuffle(shufGramList);
        for (int x = 0; x < shufGramList.size(); x++){
            if (fullName.equals(shufGramList.get(x))){
                shufCount = x;
            }
        }
        String temp = shufGramList.get(shufCount).substring(0, shufGramList.get(shufCount).length() - GrammarFilter.EXTENSION.length());
        notateGrammarMenu.setText(temp + "(Shuffle)");
    }
  }

String fullName = getDefaultGrammarName() + GrammarFilter.EXTENSION;
private void notateGrammarMenuAction(java.awt.event.ActionEvent evt)
  {
    group.clearSelection();
    ifCycle = false;
    ifShuffle = false;
    shufCount = 0;
    JMenuItem item = (JMenuItem) evt.getSource();
    String stem = item.getText();
    notateGrammarMenu.setText(stem);
    fullName = stem + GrammarFilter.EXTENSION;
    grammarFilename = ImproVisor.getGrammarDirectory() + File.separator + fullName;
    lickgen.loadGrammar(grammarFilename);
    lickgenFrame.resetTriageParameters(false);
    Preferences.setPreference(Preferences.DEFAULT_GRAMMAR_FILE, fullName);

    if (stem.startsWith("_")){
        cycCount = 0;
        shufCount = 0;
    }
  }



public void openCorpus()
  {
    openLeadsheet(true);
  }

/**
 * Menu item indicating whether to use theme weaver 
 */
JCheckBoxMenuItem themeWovenCheckBox = new JCheckBoxMenuItem();

/**
 * Menu item indicating whether or not to trade
 */
JCheckBoxMenuItem whetherToTradeCheckBox = new JCheckBoxMenuItem();

/**
 * Menu item indicating which trades first, Impro-Visor or user
 */
JCheckBoxMenuItem tradingCheckBox = new JCheckBoxMenuItem();

/**
 * Indicate whether or not to trade.
 */
boolean whetherToTrade = false;

/**
 * Indicate whether or not to theme weave.
 */
boolean themeWeave = false;

/**
 * When trading is on, indicates that Impro-Visor will go first.
 * Otherwise user will go first.
 */
boolean improVisorFirst = true;

/**
 * The index of the trading option currently selected.
 */
int tradingIndex = 2; // four

/**
 * Textual representations for the number of bars to be traded.
 * The suffix "bar" is added later.
 */
private static String tradingOption[] =
  {
    "one", "two", "four", "eight", "twelve", "sixteen"
  };

/**
 * The numeric values of the bars to be traded.
 */
private static int tradingQuantum[] =
  {
    1, 2, 4, 8, 12, 16
  };

/**
 * Checkboxes that will be placed in the grammar menu at run time
 * to indicate which trading option is being used.
 */
private QuantumSelectionCheckBox quantumSelectionCheckBox[] =
  {
    new QuantumSelectionCheckBox(0),
    new QuantumSelectionCheckBox(1),
    new QuantumSelectionCheckBox(2),
    new QuantumSelectionCheckBox(3),
    new QuantumSelectionCheckBox(4),
    new QuantumSelectionCheckBox(5),    
  };

/**
 * Get the indication of whether to use theme weaver or not
 * @return 
 */
public boolean getWhetherToThemeWeave()
  {
    return themeWovenCheckBox.isSelected();
  }

/**
 * Get the indication of who will trade first.
 * @return 
 */
public boolean getWhetherToTrade()
  {
    return whetherToTradeCheckBox.isSelected();
  }

/**
 * Get the indication of who will trade first.
 * @return 
 */
public boolean getImprovisorTradeFirst()
  {
    return tradingCheckBox.isSelected();
  }

/**
 * Get the number of slots to be used in one-half of the trade.
 * @return 
 */
public int getTradingQuantum()
  {
    return tradingQuantum[tradingIndex]*score.getSlotsPerMeasure();
  }


/**
 * Populate the Trading menu
 */
private void populateTradingMenu()
  {
    whetherToTradeCheckBox.setText("Trade");
    whetherToTradeCheckBox.setSelected(whetherToTrade);
    
    whetherToTradeCheckBox.addActionListener(new ActionListener()
    {
    public void actionPerformed(ActionEvent event)
      {
        whetherToTrade = !whetherToTrade;
        whetherToTradeCheckBox.setSelected(whetherToTrade);
      }

    });

    themeWovenCheckBox.setText("Theme Weave Solo");
    themeWovenCheckBox.setSelected(themeWeave);
    
    themeWovenCheckBox.addActionListener(new ActionListener()
    {
    public void actionPerformed(ActionEvent event)
      {
        themeWeave = !themeWeave;
        themeWovenCheckBox.setSelected(themeWeave);
        System.out.println("theme weave off");
      }

    });
    
    tradingCheckBox.setText("Impro-Visor first");
    tradingCheckBox.setSelected(improVisorFirst);
    
    tradingCheckBox.addActionListener(new ActionListener()
    {
    public void actionPerformed(ActionEvent event)
      {
        improVisorFirst = !improVisorFirst;
        tradingCheckBox.setSelected(improVisorFirst);
      }

    });

    tradingMenu.removeAll();
    tradingMenu.add(tradingWindow);
    tradingMenu.add(tradingWindow2);
    tradingMenu.add(themeWovenCheckBox);
    tradingMenu.add(whetherToTradeCheckBox);
    tradingMenu.add(tradingCheckBox);

    for( int i = 0; i < tradingOption.length; i++ )
      {
        tradingMenu.add(quantumSelectionCheckBox[i]);
        quantumSelectionCheckBox[i].setSelected(i == tradingIndex);
      }
  }


ArrayList<String> gramList = new ArrayList<String>();
ArrayList<String> shufGramList = new ArrayList<String>();
JCheckBoxMenuItem cycle = new JCheckBoxMenuItem("Cycle");
JCheckBoxMenuItem shuffle = new JCheckBoxMenuItem("Shuffle");
ButtonGroup group = new ButtonGroup();

private void populateNotateGrammarMenu()
  {
    File directory = ImproVisor.getGrammarDirectory();
    //System.out.println("populating from " + directory);
    if( directory.isDirectory() )
      {
        String fileName[] = directory.list();

        // 6-25-13 Hayden Blauzvern
        // Fix for Linux, where the file list is not in alphabetic order
        Arrays.sort(fileName, new Comparator<String>()
        {
        public int compare(String s1, String s2)
          {
            return s1.toUpperCase().compareTo(s2.toUpperCase());
          }

        });

        // Setup grammar menu items involving trading
        notateGrammarMenu.removeAll();
        notateGrammarMenu.add(new JLabel("Grammar"));
        
       //Add Cycle and Shuffle options at top
       
       cycle.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                notateGrammarMenuActOpt(e);
            }
        });
       
       shuffle.addActionListener(new java.awt.event.ActionListener() {

            public void actionPerformed(ActionEvent e) {
                notateGrammarMenuActOpt(e);
            }
        });
       
       group.add(cycle);
       group.add(shuffle);
       notateGrammarMenu.add(cycle);
       notateGrammarMenu.add(shuffle);
       
       grammarMenuSeparator = new javax.swing.JSeparator();
       notateGrammarMenu.add(grammarMenuSeparator);
       
        // Add names of grammar files

        for( int i = 0; i < fileName.length; i++ )
          {
            String name = fileName[i];

            if( name.endsWith(GrammarFilter.EXTENSION) )
              {
                if( !name.startsWith("_")){
                    gramList.add(name);
                    shufGramList.add(name);
                }
                  
                int len = name.length();
                String stem = name.substring(0, len - GrammarFilter.EXTENSION.length());
                JMenuItem item = new JMenuItem(stem);
                notateGrammarMenu.add(item);
                item.addActionListener(new java.awt.event.ActionListener()
                {
                public void actionPerformed(java.awt.event.ActionEvent evt)
                  {
                    notateGrammarMenuAction(evt);
                  }

                });
                
              }
          }
      }
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
    deleteVoicingDialog.setSize(325, 200);

    deleteVoicingDialog.setLocationRelativeTo(this);

    deleteVoicingDialog.setVisible(true);
  }

public void showNewVoicingDialog()
  {
    newVoicingDialog.setSize(350, 200);

    newVoicingDialog.setLocationRelativeTo(this);

    newVoicingDialog.setVisible(true);

    ListSelectionModel rowSM = voicingTable.getSelectionModel();

    if( !rowSM.isSelectionEmpty() )
      {

        int rowIndex = voicingTable.getSelectedRow();

        int colIndex = 0;

        Object o = voicingTable.getValueAt(rowIndex, colIndex);

        String chord = o.toString();

        newVoicingChordTF.setText(chord);
      }
    else
      {
        newVoicingChordTF.setText("");
      }

  }

/**
 * Set the indexed preference with a value from the corresponding CheckBox.
 *
 * @param index
 * @param checkbo
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
 * Set up arrays for Part and Stave classes, which will be displayed in
 *
 * scoreFrame. The array of Staves initializes every
 * <code>stave</code> to
 *
 * the
 * <code>type</code> TREBLE.
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
    //lickgenFrame.stavesAdded(size); //make sure that the complexity windows sync with new chorus tabs


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

        MelodyPart melodyPart = score.getPart(i);

        partList.add(melodyPart);

        Stave stave = new Stave(melodyPart, melodyPart.getStaveType(), this, score.getTitle());
        //Stave stave = new Stave(melodyPart.getStaveType(), this, score.getTitle());

        pane.setStave(stave);

        stave.setChordProg(chordProg);

        stave.setPart(partList.get(i));

        stave.setKeySignature(score.getKeySignature());

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


    /**
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
 * Convert a polylist to the form required by the advice tree
 *
 * @param item a polylist
 *
 * @return DefaultMutableTreeNode the root of a tree
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
            tree.add(polylistToTree((Polylist) item.first()));
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
//      if (node instanceof AdviceForBrick)
//          System.out.println("The brick node is " + node);

        if( node instanceof Polylist )
          {
            polylistToMenus((Polylist) node);
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
            else if( node instanceof AdviceForBrick )
              {
                adviceMenuItemsBricks.add(node);
              }
            else
              {
                //System.out.println("unidentified advice node: " + node);
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

    final Object[] menuContentsBricks = adviceMenuItemsBricks.toArray();


    adviceScrollListBricks.setModel(new javax.swing.AbstractListModel()
    {
    public int getSize()
      {
        return menuContentsBricks.length;
      }

    public Object getElementAt(int i)
      {
        return menuContentsBricks[i];
      }

    });
  }

/**
 * major sub-trees to be excluded from Notes advice tree
 */
static Polylist excludeFromTree = Polylist.list(" scale tones", " cells",
                                                " idioms", " licks", " quotes", " bricks"); // TODO: Should " bricks" be in this?

/**
 * Display the advice tree for the chords around the given index
 *
 * @param selectedIndex the index currently selected on the stave
 *
 * @param row the row to be initially selected
 *
 * @param focus if the advice frame should receive focus or not
 *
 */
public void displayAdviceTree(int selectedIndex, int row, Note note)
  {
    setMode(Mode.ADVICE);

    Trace.log(2, "displayAdviceTree");

    adviceFrame.setTitle("Advice for " + score.getTitle());

    DefaultTreeCellRenderer tcr =
            (DefaultTreeCellRenderer) adviceTree.getCellRenderer();

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

            return;
          }

        adviceMenuItemsScales = new ArrayList<Object>();
        adviceMenuItemsCells = new ArrayList<Object>();
        adviceMenuItemsIdioms = new ArrayList<Object>();
        adviceMenuItemsLicks = new ArrayList<Object>();
        adviceMenuItemsQuotes = new ArrayList<Object>();
        adviceMenuItemsBricks = new ArrayList<Object>();

        // Where polylistToMenus is called

        polylistToMenus(adviceList);

        adviceFrame.setTitle((String) adviceList.first());

        DefaultMutableTreeNode tree = new DefaultMutableTreeNode("Notes");

        ((DefaultTreeModel) adviceTree.getModel()).setRoot(tree);

        adviceList = adviceList.rest();
        while( adviceList.nonEmpty() )
          {
            Object first = adviceList.first();
            if( first instanceof Polylist )
              {
                Polylist list = (Polylist) first;
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
 * Display the window allowing overriding of measures
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
 * Override the number of measures within lockedMeasures. The behavior now
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
 * @param measures number of measures to set the current line to
 *
 * @param currLine the current line to set the measures on
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
    pausePlayMI.setEnabled(true);
    preferencesAcceleratorMI.setEnabled(true);

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
        expandMelodyBy2.setEnabled(true);
        expandMelodyBy3.setEnabled(true);
        contractMelodyBy2.setEnabled(true);
        contractMelodyBy3.setEnabled(true);
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
        expandMelodyBy2.setEnabled(true);
        expandMelodyBy3.setEnabled(true);
        contractMelodyBy2.setEnabled(true);
        contractMelodyBy3.setEnabled(true);
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
 * Disable accelerators. We do this for a work-around: When letters are typed
 * into the
 *
 * chord/melody text entry window
 *
 * if any of those letters correspond to accelerators, the corresponding method
 * will
 *
 * be invoked. This seems like a bug in swing to me, but I don't know another
 * way of
 *
 * getting around it at present. We will rely on the call to setItemStates() to
 *
 * re-enable the accelerators upon hitting return in the text entry field.
 *
 */
protected void disableAccelerators()
  {
    addRestMI.setEnabled(false);

    playAllMI.setEnabled(false);
    stopPlayMI.setEnabled(false);
    pausePlayMI.setEnabled(false);

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
    expandMelodyBy2.setEnabled(false);
    expandMelodyBy3.setEnabled(false);
    contractMelodyBy2.setEnabled(false);
    contractMelodyBy3.setEnabled(false);
    resolvePitches.setEnabled(false);

    saveSelectionAsLick.setEnabled(false);

    preferencesAcceleratorMI.setEnabled(false);

    // REVISIT generateLickButton.setEnabled(false);
  }

/**
 * Set the preferences for contour drawing.
 */
private void resetDrawingPrefs()
  {
    drawScaleTonesCheckBox.setSelected(
            Preferences.getPreference(Preferences.DRAWING_TONES).charAt(0) == '1');

    drawChordTonesCheckBox.setSelected(
            Preferences.getPreference(Preferences.DRAWING_TONES).charAt(1) == '1');

    drawColorTonesCheckBox.setSelected(
            Preferences.getPreference(Preferences.DRAWING_TONES).charAt(2) == '1');
  }

private ImageIcon playButton =
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"));
private ImageIcon pauseButton =
        new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pause.gif"));

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel OpenHelpText;
    private javax.swing.JButton ReloadSuperColliderButton;
    private javax.swing.JScrollPane SectionTableScrollPane;
    private javax.swing.JMenuItem aboutMI;
    private javax.swing.JButton acceptTruncate;
    private javax.swing.JMenuItem addRestMI;
    private javax.swing.JButton addTabBtn;
    private javax.swing.JMenuItem addTabPopupMenuItem;
    protected javax.swing.JFrame adviceFrame;
    private javax.swing.JMenuItem advicePMI;
    private javax.swing.JList adviceScrollListBricks;
    private javax.swing.JList adviceScrollListCells;
    private javax.swing.JList adviceScrollListIdioms;
    private javax.swing.JList adviceScrollListLicks;
    private javax.swing.JList adviceScrollListQuotes;
    private javax.swing.JList adviceScrollListScales;
    private javax.swing.JTabbedPane adviceTabbedPane;
    protected javax.swing.JTree adviceTree;
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
    private javax.swing.JLabel audioInputLabel;
    private javax.swing.JLabel audioInputLabel1;
    private javax.swing.JLabel audioInputLabel2;
    private javax.swing.JComboBox audioInputResolutionComboBox1;
    private javax.swing.JPanel audioInputTab1;
    private javax.swing.JPanel audioPreferencesOld;
    private javax.swing.JPanel audioTab;
    private javax.swing.JCheckBoxMenuItem autoAdjustMI;
    private javax.swing.JCheckBoxMenuItem autoFillMI;
    private javax.swing.JRadioButton autoStave;
    private javax.swing.JRadioButton autoStaveBtn;
    private javax.swing.JCheckBoxMenuItem barNumsMI;
    private javax.swing.JSpinner bassChannelSpinner;
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
    private javax.swing.JRadioButton brickRadioButton;
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
    private javax.swing.JLabel channelSelectLabel;
    private javax.swing.JSpinner chordChannelSpinner;
    private javax.swing.ButtonGroup chordColorBtnGrp;
    private javax.swing.JTextField chordDist;
    private javax.swing.JLabel chordDistLabel;
    private javax.swing.JCheckBox chordExtns;
    private javax.swing.JSpinner chordFontSizeSpinner;
    private javax.swing.JLabel chordIInstLabel;
    private javax.swing.JPanel chordInstPanel;
    private javax.swing.JCheckBox chordMute;
    private javax.swing.JPanel chordPanel;
    private javax.swing.JButton chordReplayButton;
    private javax.swing.JLabel chordRootLabel;
    private javax.swing.JTextField chordRootTF;
    private javax.swing.JLabel chordSearchLabel;
    private javax.swing.JTextField chordSearchTF;
    private javax.swing.JButton chordStepBackButton;
    private javax.swing.JButton chordStepForwardButton;
    private javax.swing.JCheckBox chordSubs;
    private javax.swing.JPanel chordTabPanel;
    private javax.swing.JLabel chordToneLabel;
    private javax.swing.JCheckBox chordTones;
    private javax.swing.JSlider chordVolume;
    private javax.swing.JToggleButton chorusBtn;
    private javax.swing.JPanel chorusPreferences;
    private javax.swing.JButton chorusPreferencesBtn;
    private javax.swing.JMenuItem chorusPrefsMI;
    private javax.swing.JPanel chorusSpecificPanel;
    private javax.swing.JButton clearButton;
    private javax.swing.JMenuItem clearHistoryMI;
    private javax.swing.JButton clearRangeBtn;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.ButtonGroup colorColorBtnGrp;
    private javax.swing.JLabel colorToneLabel;
    private javax.swing.JCheckBox colorTones;
    private javax.swing.JToggleButton colorationButton;
    private javax.swing.JLabel commentsLabel;
    private javax.swing.JTextField commentsTF;
    private javax.swing.JTextField composerField;
    private javax.swing.JLabel composerLabel;
    private javax.swing.JSlider confidenceThresholdSlider1;
    private javax.swing.JPanel contToneChoices;
    private javax.swing.JToggleButton contourBtn;
    private javax.swing.JPanel contourPreferences;
    private javax.swing.JButton contourPreferencesBtn;
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
    private javax.swing.JCheckBoxMenuItem createRoadMapCheckBox;
    private javax.swing.JLabel currDirectoryLabel;
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
    private javax.swing.JPanel defaultStaveTypePanel;
    private javax.swing.JTextField defaultTempoTF;
    private javax.swing.JPanel defaultsTab;
    private javax.swing.JMenuItem delAllMI;
    private javax.swing.JButton delSectionButton;
    private javax.swing.JButton delTabBtn;
    private javax.swing.JCheckBox delTabCheckBox;
    private javax.swing.JMenuItem delTabPopupMenuItem;
    private javax.swing.JButton deleteVoicingCancelButton;
    private javax.swing.JDialog deleteVoicingDialog;
    private javax.swing.JLabel deleteVoicingLabel;
    private javax.swing.JButton deleteVoicingOKButton;
    private javax.swing.JPanel devicesTab;
    private javax.swing.JButton drawButton;
    private javax.swing.JCheckBox drawChordTonesCheckBox;
    private javax.swing.JCheckBox drawColorTonesCheckBox;
    private javax.swing.JCheckBox drawScaleTonesCheckBox;
    private javax.swing.JSpinner drumChannelSpinner;
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
    private javax.swing.JMenuItem emptyRoadMapMI;
    private javax.swing.JCheckBox enableCache;
    private javax.swing.JMenuItem enterBothMI;
    private javax.swing.JMenuItem enterChordsMI;
    private javax.swing.JTextField enterLickTitle;
    private javax.swing.JTextField enterMeasures;
    private javax.swing.JMenuItem enterMelodyMI;
    private javax.swing.JMenuItem enterTextMI;
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
    private javax.swing.JButton fileStepBackBtn;
    private javax.swing.JDialog fileStepDialog;
    private javax.swing.JButton fileStepForwardBtn;
    private javax.swing.JLabel fileStepLabel;
    private javax.swing.JMenuItem fileStepMI;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.JMenuItem firstTimePrefsMI;
    private javax.swing.JComboBox frameSizeComboBox1;
    private javax.swing.JToggleButton freezeLayoutButton;
    private javax.swing.JPanel generalContourTab;
    private javax.swing.JMenuItem generateLickInSelection;
    private javax.swing.JSpinner generationGapSpinner;
    private javax.swing.ButtonGroup generatorButtonGroup;
    private javax.swing.JToggleButton globalBtn;
    private javax.swing.JPanel globalPreferences;
    private javax.swing.JButton globalPreferencesBtn;
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
    private javax.swing.JMenuItem guideToneLine;
    private javax.swing.JMenuItem helpAboutMI;
    private javax.swing.JMenuItem helpMI;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JTextField highRangeTF;
    private javax.swing.JTextField highRangeTF2;
    private javax.swing.JRadioButton idiomRadioButton;
    private javax.swing.JCheckBox idioms;
    private javax.swing.JButton ignoreDuplicate;
    private javax.swing.JMenuItem importMidiMI;
    private javax.swing.JToggleButton improviseButton;
    private javax.swing.JMenuItem insertChorusTabMI;
    private javax.swing.JMenuItem insertPhraseAfterPMI;
    private javax.swing.JMenuItem insertPhraseBeforePMI;
    private javax.swing.JMenuItem insertRestMeasure;
    private javax.swing.JMenuItem insertSectionAfterPMI;
    private javax.swing.JMenuItem insertSectionBeforePMI;
    private javax.swing.JButton insertVoicingButton;
    private javax.swing.JMenuItem invertMelody;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JLabel jLabel1;
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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList4;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
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
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JSeparator jSeparator13;
    private javax.swing.JSeparator jSeparator14;
    private javax.swing.JSeparator jSeparator16;
    private javax.swing.JSeparator jSeparator17;
    private javax.swing.JSeparator jSeparator18;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator21;
    private javax.swing.JSeparator jSeparator22;
    private javax.swing.JSeparator jSeparator23;
    private javax.swing.JSeparator jSeparator29;
    private javax.swing.JSeparator jSeparator3;
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
    private javax.swing.JTabbedPane jTabbedPane7;
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
    private javax.swing.JSlider k_constantSlider1;
    private javax.swing.ButtonGroup keySigBtnGroup;
    private javax.swing.JLabel keySignatureLabel;
    private javax.swing.JTextField keySignatureTF;
    private javax.swing.JPanel latencyTab;
    private javax.swing.JLabel layoutLabel;
    private javax.swing.JTextField layoutTF;
    private javax.swing.JToggleButton leadsheetBtn;
    private javax.swing.JPanel leadsheetPreferences;
    private javax.swing.JButton leadsheetPreferencesBtn;
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
    private javax.swing.JSpinner maxPitchSpinner1;
    private javax.swing.JLabel measErrorLabel;
    private javax.swing.JLabel measuresPerPartLabel;
    private javax.swing.JSpinner melodyChannelSpinner;
    private javax.swing.JPanel melodyInstPanel;
    private javax.swing.JLabel melodyInsttLabel;
    private javax.swing.JCheckBox melodyMute;
    private javax.swing.JPanel melodyPanel;
    private javax.swing.JSlider melodyVolume;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem mergeSectionFollowingPMI;
    private javax.swing.JMenuItem mergeSectionPreviousPMI;
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
    private javax.swing.JButton midiPreferencesBtn;
    private javax.swing.JMenuItem midiPrefsMI;
    private javax.swing.JSpinner midiRecordSnapSpinner;
    private javax.swing.JFrame midiStyleSpec;
    private javax.swing.JSpinner minPitchSpinner1;
    private javax.swing.JButton mixerBtn;
    private javax.swing.JDialog mixerDialog;
    private javax.swing.JMenuItem mostRecentLeadsheetMI;
    private javax.swing.JMenuItem mostRecentLeadsheetNewWindowMI;
    private javax.swing.JComboBox nWaySplitComboBox;
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
    private javax.swing.JButton noteCursorBtn;
    private javax.swing.JToggleButton noteLen16Btn;
    private javax.swing.JToggleButton noteLen1Btn;
    private javax.swing.JToggleButton noteLen2Btn;
    private javax.swing.JToggleButton noteLen32Btn;
    private javax.swing.JToggleButton noteLen4Btn;
    private javax.swing.JToggleButton noteLen8Btn;
    private javax.swing.ButtonGroup noteLenBtnGrp;
    private javax.swing.JDialog noteLenDialog;
    private javax.swing.JCheckBox noteLenDottedCheckBox;
    private javax.swing.JPanel noteLenModPanel;
    private javax.swing.JPanel noteLenPanel;
    private javax.swing.JCheckBox noteLenTripletCheckBox;
    private javax.swing.JTextField numStavesPerPage;
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
    private javax.swing.JMenu openRecentLeadsheetMenu;
    private javax.swing.JMenu openRecentLeadsheetNewWindowMenu;
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
    private javax.swing.JCheckBoxMenuItem phrasemarksMI;
    private javax.swing.JButton pianoKeyboardButton;
    private javax.swing.JMenuItem pianoKeyboardMI;
    private javax.swing.JComboBox pitchRangePresetComboBox1;
    private javax.swing.JMenuItem playAllMI;
    private javax.swing.JButton playBtn;
    private javax.swing.JMenu playMenu;
    private javax.swing.JMenuItem playSelectionMI;
    private javax.swing.JMenuItem playSelectionToEndMI;
    private javax.swing.JToolBar playToolBar;
    private javax.swing.JCheckBox playTripletsCheckBox1;
    private javax.swing.JButton playVoicingButton;
    private javax.swing.JPanel playbackPanel;
    private javax.swing.JSlider playbackSlider;
    private javax.swing.JLabel playbackTime;
    private javax.swing.JLabel playbackTotalTime;
    private javax.swing.JComboBox pollRateComboBox1;
    protected javax.swing.JPopupMenu popupMenu;
    private javax.swing.JTextField prefMeasTF;
    private javax.swing.JMenuItem preferencesAcceleratorMI;
    private javax.swing.JDialog preferencesDialog;
    private javax.swing.JMenu preferencesMenu;
    private javax.swing.JScrollPane preferencesScrollPane;
    private javax.swing.ButtonGroup prefsTabBtnGrp;
    private javax.swing.JMenuItem printAllMI;
    private javax.swing.JButton printBtn;
    private javax.swing.JMenuItem printMI;
    private javax.swing.ButtonGroup productionBtnGrp;
    private javax.swing.JButton purgeCache;
    private javax.swing.JComboBox quantizeComboBox;
    private javax.swing.JPopupMenu quantizePopupMenu;
    private javax.swing.JMenuItem quitMI;
    private javax.swing.JRadioButton quoteRadioButton;
    private javax.swing.JCheckBox quotes;
    private javax.swing.JButton rangeFilterBtn;
    private javax.swing.JLabel rangeToLabel;
    private javax.swing.JLabel rangeToLabel2;
    private javax.swing.JMenuItem reAnalyzeMI;
    private javax.swing.JLabel recentStyleLabel;
    private javax.swing.JList recentStyleList;
    private javax.swing.JScrollPane recentStyleListScrollPane;
    private javax.swing.JButton recordBtn;
    private javax.swing.JMenuItem recordMI;
    private javax.swing.JRadioButton redApproachBtn;
    private javax.swing.JRadioButton redChordBtn;
    private javax.swing.JRadioButton redColorBtn;
    private javax.swing.JLabel redLabel;
    private javax.swing.JRadioButton redOtherBtn;
    private javax.swing.JButton redoBtn;
    private javax.swing.JMenuItem redoMI;
    private javax.swing.JMenuItem redoPMI;
    private javax.swing.JMenuItem renameTabPopupMenuItem;
    private javax.swing.JCheckBoxMenuItem replaceWithDelta;
    private javax.swing.JCheckBox replaceWithDeltaCheckBox;
    private javax.swing.JCheckBoxMenuItem replaceWithPhi;
    private javax.swing.JCheckBox replaceWithPhiCheckBox;
    private javax.swing.JButton resetBtn;
    private javax.swing.JButton resetBtn1;
    private javax.swing.JButton resetBtn2;
    private javax.swing.JButton resetBtn3;
    private javax.swing.JMenuItem resolvePitches;
    private javax.swing.JMenuItem reverseMelody;
    private javax.swing.JMenuItem revertToSavedMI;
    private javax.swing.JSlider rmsThresholdSlider1;
    private javax.swing.JMenuItem roadMapThisAnalyze;
    private javax.swing.JMenu roadmapMenu;
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
    private javax.swing.JScrollPane scrollBricks;
    private javax.swing.JScrollPane scrollCells;
    private javax.swing.JScrollPane scrollIdioms;
    private javax.swing.JScrollPane scrollLicks;
    private javax.swing.JScrollPane scrollNotes;
    private javax.swing.JScrollPane scrollQuotes;
    private javax.swing.JScrollPane scrollScales;
    private javax.swing.JLabel sectionLabel;
    private javax.swing.JButton sectionPreferencesBtn;
    private javax.swing.JTable sectionTable;
    private javax.swing.JLabel selectAStyleLabel;
    private javax.swing.JMenuItem selectAllMI;
    private javax.swing.JCheckBox sendSetBankCheckBox;
    private javax.swing.JToggleButton showAdviceButton;
    private javax.swing.JCheckBoxMenuItem showBracketsAllMeasuresMI;
    private javax.swing.JCheckBoxMenuItem showBracketsCurrentMeasureMI;
    private javax.swing.JCheckBoxMenuItem showConstructionLinesMI;
    private javax.swing.JCheckBoxMenuItem showEmptyTitlesMI;
    private javax.swing.JCheckBoxMenuItem showNoteNameAboveCursorMI;
    private javax.swing.JCheckBoxMenuItem showTitlesMI;
    private javax.swing.JToggleButton smartEntryButton;
    private javax.swing.JMenuItem soloGeneratorMI;
    private javax.swing.JToolBar standardToolbar;
    private javax.swing.JMenu statusMenu;
    private javax.swing.ButtonGroup staveButtonGroup;
    private javax.swing.JPanel staveButtonPanel;
    private javax.swing.ButtonGroup staveChoiceButtonGroup;
    private javax.swing.JLabel stavesPerPageLabel;
    private javax.swing.JButton stepBackButton;
    private javax.swing.JButton stepForwardButton;
    private javax.swing.JToggleButton stepInputBtn;
    private javax.swing.JMenuItem stepKeyboardMI;
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
    private javax.swing.JPopupMenu tabPopupMenu;
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
    private javax.swing.JMenu tradingMenu;
    private javax.swing.JMenuItem tradingWindow;
    private javax.swing.JMenuItem tradingWindow2;
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
    private javax.swing.JCheckBoxMenuItem useAudioInputMI;
    private javax.swing.JCheckBoxMenuItem useBeamsMI;
    private javax.swing.JButton usePreviousStyleButton;
    private javax.swing.JCheckBox useSubstitutorCheckBox;
    private javax.swing.JLabel useSuperColliderCheckboxText;
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
    private javax.swing.JSeparator grammarMenuSeparator;
    // Real end of variables declaration
    
    public boolean getPlayEnabled(){
        return playBtn.isEnabled();
    }
    
    public boolean getPauseEnabled(){
        return pauseBtn.isEnabled();
    }
    
    public boolean getStopEnabled(){
        return stopBtn.isEnabled();
    }
    
    public javax.swing.JFrame getVoicingTestFrame(){
        return voicingTestFrame;
    }
    
    public JButton getPlay(){
        return playBtn;
    }
    
    public JToggleButton getPause(){
        return pauseBtn;
    }
    
    public JButton getStop(){
        return stopBtn;
    }
    
    public boolean getPlaySelected(){
        return playBtn.isSelected();
    }
    
    public boolean getPauseSelected(){
        return pauseBtn.isSelected();
    }
    
    public boolean getStopSelected(){
        return stopBtn.isSelected();
    }
    
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
    return grammarFilename;
  }

public void setGrammarFilename(String name)
  {
    grammarFilename = name;
  }

public void reloadGrammar()
  {
    lickgen.loadGrammar(grammarFilename);
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

    ((CapturingGlassPane) getRootPane().getGlassPane()).focus();
  }

public void showFakeModalDialog(JDialog d)
  {
    Trace.log(2, "Notate: showFakeModalDialog() - " + d);

    CapturingGlassPane gp = (CapturingGlassPane) getRootPane().getGlassPane();

    gp.setFocusOn(d);

    d.setVisible(true);
  }

public void hideFakeModalDialog(JDialog d)
  {
    Trace.log(2, "Notate: hideFakeModalDialog() - " + d);

    CapturingGlassPane gp = (CapturingGlassPane) getRootPane().getGlassPane();

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
    int result;
    if( toLoop )
      {
        updateLoopFromTextField();

        if( loopCount <= 0 )
          {
            result = -1; // <= 0 means forever
          }
        else
          {
            result = loopCount - 1; // let 1 mean only once, not twice, etc.
          }
      }
    else
      {
        result = 0; // don't loop
      }
    //System.out.println("getLoopCount() returns " + result);

    return result;
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
        File styleFile = ImproVisor.getRecentStyleFile();

        //System.out.println("styleFile = " + styleFile);

        if( styleFile == null )
          {
            styleEditor = new StyleEditor(this);
          }
        else
          {
            styleEditor = new StyleEditor(this, styleFile);
          }
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

public long getTotalSlotsElapsed()
  {
    return totalSlotsElapsed;
  }

public double getTempo()
  {
    return score.getTempo();
  }

public InstrumentChooser getMelodyInstrument()
  {
    return melodyInst;
  }

public InstrumentChooser getAuxInstrument()
  {
    return auxInst;
  }

public int getSelectedIndex()
  {
    return scoreTab.getSelectedIndex();
  }

public void redrawTriage()
  {
    lickgenFrame.redrawTriage();
  }

public int getScoreLength()
  {
    return score.getLength();
  }

public int getBeatValue()
  {
    return beatValue;
  }

public int getBeatsPerMeasure()
  {
    return score.getBeatsPerMeasure();
  }

/**
 * Open a new RoadMapFrame and transfer all chords in the chorus to it.
 *
 * @param roadmap
 */
public void chordPartToRoadMapFrame(RoadMapFrame roadmap)
  {
    ChordPart chordPart = score.getChordProg();
    chordPart.toRoadMapFrame(roadmap);
  }

/**
 * Add selected bars from roadmap to the current Score displayed.
 *
 * @param roadmap
 */
public void addToChordPartFromRoadMapFrame(RoadMapFrame roadmap)
  {
    score.fromRoadMapFrame(roadmap);
    setBars(score.getBarsPerChorus());
    //TODO style isn't set correctly
    //TODO set name
    repaint();
  }

/**
 * Execute a command in the context of this Notate frame.
 */
public void execute(Command command)
  {
    cm.execute(command);
  }

/**
 * Make this Notate frame visible, after setting certain state elements from
 * oldNotate.
 */
public void makeVisible(Notate oldNotate)
  {
    makeVisible();
    setAutoCreateRoadMap(oldNotate.getAutoCreateRoadMap());
  }

@Override
public void setVisible(boolean value)
  {
    super.setVisible(value);
  }

/**
 * Make this Notate frame visible
 */
public void makeVisible()
  {
    makeVisible(createRoadMapCheckBox.isSelected());
  }

public void makeVisible(boolean createRoadMap)
  {
    setNotateFrameHeight(); // Needed
    setVisible(true);

    staveRequestFocus();
    if( createRoadMap )
      {
        roadMapThisAnalyze();
      }
  }

/**
 * Set the selection value on the CheckBox that will automatically create a
 * roadmap when this Notate is opened.
 *
 * @param value
 */
public void setAutoCreateRoadMap(boolean value)
  {
    createRoadMapCheckBox.setSelected(value);
  }

/**
 * Indicate whether or not this Notate frame has the create roadmap box checked.
 *
 * @return
 */
public boolean getAutoCreateRoadMap()
  {
    return createRoadMapCheckBox.isSelected();
  }

/**
 * Create a roadmap for the current Notate frame.
 */
public void roadMapThis()
  {
    setMode(Mode.ROADMAP);
    establishRoadMapFrame();
    score.toRoadMapFrame(roadmapFrame);
    roadmapFrame.setRoadMapTitle(getTitle());
    roadmapFrame.makeVisible(false);
  }

/**
 * Create a roadmap for the current Notate frame and analyze the contents.
 */
public void roadMapThisAnalyze()
  {
    setMode(Mode.ROADMAP);
    repaint();
    setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    establishRoadMapFrame();
    score.toRoadMapFrame(roadmapFrame);
    roadmapFrame.setRoadMapTitle(getTitle());
    roadmapFrame.setStyleNames(getChordProg().getSectionRecordStyleNames());
    roadmapFrame.updatePhiAndDelta(getPhiStatus(), getDeltaStatus());
    roadmapFrame.makeVisible(true);
    setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    setMode(Mode.ROADMAP_DONE);
    staveRequestFocus();
  }

public ArrayList<Block> getRoadMapBlocks()
  {
    roadMapThisAnalyze();
    return roadmapFrame.getFullRoadMap();
  }

// currently disconnected
public void ensureRoadmap()
  {
    //System.out.println("roadmapPoly is " + chordProg.getRoadmapPoly() );
    if( chordProg.getRoadMap() == null )
      {
        roadMapThisAnalyze();
        chordProg.setRoadmap(null);
        chordProg.setRoadmap(roadmapFrame.getRoadMap());
      }
  }

public void reAnalyze()
  {
    roadMapThisAnalyze();
  }

/**
 * Create an empty road map tied to the current Notate frame.
 */
public void openEmptyRoadmap()
  {
    setMode(Mode.ROADMAP);
    roadmapFrame = new RoadMapFrame(this);
    roadmapFrame.setRoadMapFrameHeight();
    roadmapFrame.setRoadMapTitle("Untitled");
    roadmapFrame.updatePhiAndDelta(getPhiStatus(), getDeltaStatus());
    roadmapFrame.makeVisible(false);
    roadmapFrame.setVisible(true);
    setMode(Mode.ROADMAP_DONE);
  }

/**
 * If a roadmapFrame exists, clear it out. Otherwise create a roadmapFrame.
 */
public void establishRoadMapFrame()
  {
    if( roadmapFrame == null )
      {
        roadmapFrame = new RoadMapFrame(this);
        roadmapFrame.setRoadMapFrameHeight();
      }
    else
      {
        roadmapFrame.selectAllBricks();
        roadmapFrame.deleteSelection();
      }
  }

/**
 * Call if roadMapFrame is no longer to be remembered.
 */
public void disestablishRoadMapFrame()
  {
    roadmapFrame = null;
  }

/**
 * Copy this Notate's Score's metre setting into the argument array of dimension
 * 2.
 *
 * @param metre
 */
public void getMetre(int metre[])
  {
    score.getMetre(metre);
  }

public void setRoadMapMidiListener(MidiPlayListener listener)
  {
    midiSynth3.setPlayListener(listener);
  }

public int getMidiSlot()
  {
    return midiSynth3.getSlot();
  }

public MidiSynth getMidiSynthRM()
  {
    return midiSynth3;
  }

public int getBarsPerChorus()
  {
    return score.getBarsPerChorus();
  }

public SectionInfo getSectionInfo()
  {
    return sectionInfo;
  }

public void setChordProg(ChordPart chordPart)
  {
    score.setChordProg(chordPart);
    score.setLength(chordPart.getSize());
    setBars(chordPart.getBars());
    setupArrays();
  }

public String getLayoutTF()
  {
    String text = layoutTF.getText();
    //System.out.println("getLayoutTF returns " + text);
    return text;
  }

public Polylist getLayoutTFasPolylist()
  {
    String text = getLayoutTF().trim();

    if( text.equals("") )
      {
        return Polylist.nil;
      }
    return parseListFromString(getLayoutTF());
  }

public void setLayoutTF(String text)
  {
    //System.out.println("setLayoutTF gets " + text);

    layoutTF.setText(text);
  }

public boolean getUseNoteCursor()
  {
    return useNoteCursor;
  }

public void setUseNoteCursor(boolean on)
  {
    useNoteCursor = on;
    Cursor noteCursor = getCurrentStaveActionHandler().getNoteCursor();
    getCurrentStaveActionHandler().setCursor(noteCursor);
  }

public boolean getShowNoteNamesAboveCursor()
  {
    return showNoteNameAboveCursorMI.isSelected();
  }

public String getDefaultGrammarName()
  {
    String fileName = ImproVisor.getGrammarFile().getName();
    fileName = fileName.subSequence(0, fileName.length() - ".grammar".length()).toString();
    //System.out.println("fileName = " + fileName);
    return fileName;
  }

public void setBorderColor(Color color)
  {
    scoreTab.setBorder(new javax.swing.border.LineBorder(color, 3));
  }

public void openMidiPreferences()
  {
    setPrefsDialog();
    changePrefTab(midiBtn, midiPreferences);
    preferencesDialog.setVisible(true);
  }

public int getRecordSnapValue()
  {
    return Integer.parseInt(midiRecordSnapSpinner.getValue().toString());
  }

/**
 * return slot and bar as string (assuming 4/4 time)
 *
 * @param slot
 * @return
 */
public String bar(long slot)
  {
    return "slot " + slot + " (bar " + (slot % getCurrentMelodyPart().size()) / 480 + ")";
  }

/**
 * Select which improvisation type to use.
 */
private boolean originalGeneration = true;
private Trading autoImprovisation = null;
private long totalSlotsElapsed = 0;
private int previousSynthSlot = 0;

/*
 * This was formerly embedded inside executable code.
 */
class PlayActionListener implements ActionListener
{

/**
 * This is called repeatedly as play of the leadsheet progresses.
 *
 * @param evt
 */
public void actionPerformed(ActionEvent evt) {

        //this is used to pass info for interactive trading
        if (Notate.this.trader != null) {
            Notate.this.trader.trackPlay(evt);
        }

    if( playingStopped() )
      {
        if( keyboard != null && keyboard.isVisible() )
          {
            keyboard.setPlayback(false);
          }
      }

    int slotDelay =
            (int) (midiSynth.getTotalSlots() * (1e6 * trackerDelay / midiSynth.getTotalMicroseconds()));

    int slotInPlayback = midiSynth.getSlot() - slotDelay;
    int slot = slotInPlayback;

    int synthSlot = midiSynth.getSlot();

    if( previousSynthSlot > synthSlot )
      {
        // wrap-around has occurred
        int slotsSkipped = getCurrentMelodyPart().size() - 1 - previousSynthSlot;
        long newSlotsElapsed = totalSlotsElapsed + synthSlot + slotsSkipped;

        //System.out.println("\ntotalSlotsElapsed " + bar(totalSlotsElapsed) + " -> " + bar(newSlotsElapsed));

        totalSlotsElapsed = newSlotsElapsed;
      }
    else
      {
        // accumulate the difference
        totalSlotsElapsed += (synthSlot - previousSynthSlot);
      }
    previousSynthSlot = synthSlot;


    //handleAudioInput(slotInPlayback);
    if( improvisationOn )
      {
        handleAutoImprov(synthSlot);
      }

    // The following variant was originally added to stop playback at the end of a selection
    // However, it also truncates the drum patterns etc. so that needs to be fixed.

    if( midiSynth.finishedPlaying() )
      {
        stopPlaying("midiSynth finished playing");
        return;
      }

    // Stop playback when a specified slot is reached.

    if( slotInPlayback > stopPlaybackAtSlot )
      {
//        System.out.println("stop at " + slotInPlayback + " vs. " + stopPlaybackAtSlot);

        stopPlaying("slotInPlayback > stopPlaybackAtSlot " + slotInPlayback + " > " + stopPlaybackAtSlot);
        return;
      }

    int chorusSize = getScore().getLength();

    int slotInChorus = slotInPlayback % chorusSize;

    Chord currentChord = chordProg.getCurrentChord(slotInChorus);

    //currentChord.getChordForm(); // value not used?

    currentPlaybackTab = slotInPlayback / chorusSize;

    int tab = currentPlaybackTab;

    if( keyboard != null && keyboard.isVisible() && keyboard.isPlaying() )
      {
        keyboardPlayback(currentChord, tab, slotInChorus, slot, totalSlots);
      }

    handlePlayline(slotInChorus);

    if( stepKeyboard != null )
      {
        stepKeyboard.resetAdvice(slot);
      }
  } // actionPerformed

/**
 * Handle automatic improvisation This is called at practically every slot
 *
 * @param slotInPlayback
 */
private void handleAutoImprov(int slotInPlayback)
  {
    // Gap between the start of next generation and end of previous playback.

    int gap = lickgenFrame.getGap();

    // System.out.println("\nhandleAutoImprov at " + slotInPlayback);
    // Recurrent generation option
    // There are two separate branches for the time being, reflecting
    // an intended change

    if( lickgenFrame.getRecurrent() // recurrentCheckbox.isSelected()
            && (slotInPlayback >= stopPlaybackAtSlot - gap) ) // was totalSlots - gap) )
      {
        recurrentIteration++;
        // firstChorus indicator is used by MidiRecorder to deal with countin
        setFirstChorus(false);
        setStatus("Chorus " + recurrentIteration);
        int start = improviseStartSlot;
        originalGenerate(lickgen, start, improviseEndSlot);
      }
  } // handleAutoImprov

/**
 * Handle updating of tracker, aka "playline"
 *
 * @param slotInChorus
 */
private void handlePlayline(int slotInChorus)
  {
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

            Rectangle adjustedPlayline = (Rectangle) playline.clone();

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
  } // handlePlayline

} // PlayActionListener

public void setLickGenStatus(String string)
  {
    if( lickgenFrame != null )
      {
        lickgenFrame.setLickGenStatus(string);
      }
  }

/**
 * QuantumSelectionCheckBox is used in the grammar menu
 * to indicate the trading quantum. These menu items are not in the
 * GUI builder, as they are put in the menu at runtime.
 */
private class QuantumSelectionCheckBox extends JCheckBoxMenuItem
{
/**
 * the index of this box (used to index arrays tradingOption etc.
 */
private final int index;

/**
 * Construct checkbox with the desired index.
 * @param i 
 */
public QuantumSelectionCheckBox(int i)
  {
    super();
    index = i;
    setText(tradingOption[index] + " bars");
    addActionListener(new ActionListener()
    {
    /**
     * Other checkboxes are unchecked and this one is is checked.
     */
    public void actionPerformed(ActionEvent event)
      {
        tradingIndex = index;
        for( int i = 0; i < quantumSelectionCheckBox.length; i++ )
          {
            quantumSelectionCheckBox[i].setSelected(i == index);
          }
      }
    });
  }
} //QuantumSelectionCheckBox
/**
 * Return the first note in the score, or null if there is none.
 * @return 
 */
public Note getFirstNote()
  {
    if( score == null )
      {
        return null;
      }
    
    return score.getFirstNote();
  }

public String getComposer()
{
    return score.getComposer();
}

public void playAscoreWithStyle(Score score, int loopCount, int newTranspos)
  {
    int volume = allVolumeToolBarSlider.getValue();
    int startTime = 0;
    boolean swing = true;
    boolean useDrums = true;
    int endLimitIndex = -1; // score.getLength()-1;
    //System.out.println("playing score of length " + score.getLength());

    if( midiSynth3 == null )
      {
        midiSynth3 = new MidiSynth(midiManager);
      }

    midiSynth3.setMasterVolume(volume);

    new PlayScoreCommand(score,
                         startTime,
                         swing,
                         midiSynth3,
                         this,
                         loopCount,
                         newTranspos,
                         useDrums,
                         endLimitIndex).execute();
  }
} //Notate
