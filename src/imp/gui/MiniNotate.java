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

import imp.util.LeadsheetFileView;
import imp.util.LeadsheetPreview;

import java.awt.event.*;

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.plaf.metal.*;
import javax.swing.table.*;
import java.io.*;
import java.util.*;



import imp.Constants;
import imp.Constants.ExtractMode;
import imp.Constants.StaveType;

import imp.ImproVisor;

import imp.data.*;

import imp.com.*;

import imp.util.*;

import imp.lickgen.*;

import javax.sound.midi.*;

import imp.util.MidiManager;

import polya.*;



/**
 *
 * MiniNotate creates a mini-stave.
 *
 *
 * @author      Aaron Wolin, Bob Keller, Sayuri Soejima
 *
 * @version     1.0, 28th June 2005
 *
 */

public class MiniNotate
        extends javax.swing.JFrame
        implements Constants, MidiPlayListener {
    
  /**
   * The number of bars in one display tab
   */
  
    public static final int defaultBarsPerPart = 8;
    
    
    /**
     *
     * Used as a prefix on window titles
     *
     */
    
    
    public static String windowTitlePrefix = "Mini-Stave";
    
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
     * Standard sub-directory for vocabulary
     *
     */
    
    public String vocabDirName = "vocab";
    
    File vocabDir; // set within constructor
    
    
    /**
     *
     * Standard file for vocabulary
     *
     */
    
    
    public String vocFile = "My.voc";
    
    public String grammarFile = "vocab" + File.separator + "My.grammar";
    
    
    
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
    
    private  int lsCount = 1;
    
    
    
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
     * Default Filenames
     *
     */
    
    public String lsDef = "untitled.ls";
    
    public String midDef = "untitled.mid";
    
    
    
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
    
    public static StaveType DEFAULT_STAVE_TYPE = StaveType.GRAND; // values()[Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_LOAD_STAVE))];
    
    
    
    /**
     *
     * The array of JScrollPanes that hold scoreBG panels, which hold Staves.
     *
     */
    
    protected MiniStaveScrollPane[] staveScrollPane;
    
    
    
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
    
    private int scoreLength = 32;
    
    
    
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
    
    protected MelodyPart[] partArray;
    

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
     * Lick Generator
     *
     */
    
    private LickGen lickgen;
    
    
    
    /**
     *
     * Default values pertinent to lick generation
     *
     */
    
    private int minPitch = 60;
    
    private int maxPitch = 82;
    
    private int minInterval = 0;
    
    private int maxInterval = 6;
    
    private int minDuration = 8;
    
    private int maxDuration = 8;
    
    private int totalBeats = 8;
    
    private double restProb = 0.1;
    
    private double leapProb = 0.2;
    
    private double chordToneWeight = 0.7;
    
    private double scaleToneWeight = 0.1;
    
    private double colorToneWeight = 0.05;
    
    private double chordToneDecayRate = 0.1;
    
    private boolean avoidRepeats = true;
    
    private boolean useGrammar = true;
    
    private boolean autoFill = true;
    
    private ExtractMode saveSelectionMode = ExtractMode.LICK;
    

    private int themeLength = 8;
    private double themeProb = 0.4;
    private double transposeProb = 0.5;
    private double invertProb = 0.1;
    private double reverseProb = 0.1;
    
    private boolean toLoop = false;
    private int loopCount = 1;

    
    
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
    
    private Point curvePta, curvePtb;
    
    
    
    
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
     * Midi Preferences reference to the midiManager and JComboBox models
     *
     */
    
    private MidiSynth midiSynth = null; // one midiSynth is created for each Notote instance for volume control and MIDI sequencing
    
    private MidiManager midiManager = null; // reference to global midiManager contained in ImproVisor
    
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
    
    private ImageIcon recordImageIcon = new ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/record.gif"));
    
    private ImageIcon recordActiveImageIcon = new ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/recordActive.gif"));
    
    private StyleComboBoxModel defStyleComboBoxModel = new StyleComboBoxModel();
    
    public StyleListModel styleListModel = new StyleListModel();
    
    private SectionListModel sectionListModel = new SectionListModel();
    
    private SectionInfo sectionInfo;

    
    /**
     *
     * Various Instrument Chooser objects for the different preferences
     *
     */
    
    private InstrumentChooser melodyInst, chordInst, bassInst, defMelodyInst, defChordInst, defBassInst;
    
    
    
    private SourceEditorDialog leadsheetEditor;
    
    private SourceEditorDialog grammarEditor;
    
    private SourceEditorDialog styleEditor;
    
    
    
    /**
     *
     * Various input mode names
     *
     */
    
    public enum Mode {
        
        NORMAL,

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
    
    
    
    /**
     *
     * Constructs a new Notate JFrame.
     *
     * Recursively calls Notate(Score, int, int) with a default blank Score and
     *
     * the (0,0) origin.
     *
     */
    
    public MiniNotate() {
        
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
    
    public MiniNotate(int x, int y) {
        
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
    
    public MiniNotate(Score score) {
        
        this(score, -1, -1);
        
    }
    
    
    
    public MiniNotate(Score score, Advisor adv, ImproVisor impro) {
        
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
    
    public MiniNotate(Score score, int x, int y) {
        
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
    
    public MiniNotate(Score score, Advisor adv, ImproVisor impro, int x, int y) {
        
        super();
        
        
        
        setTitle(score.getTitle());
        
        
        
        
        
        // the glass pane, when set visible, will disable mouse events
        
        this.getRootPane().setGlassPane(new CapturingGlassPane());
        
        this.getRootPane().getGlassPane().setVisible(false);
        
        
        
        this.score = score;
        
        this.adv = adv;
        
        
        
        beatValue = BEAT * 4/4;
        
        measureLength = beatValue * 4;
        
        
        
        this.impro = impro;
        
        this.cm = new CommandManager();
        

        openLSFC = new JFileChooser();
        
        saveLSFC = new JFileChooser();
        
        saveAWT = new FileDialog(this, "Save Leadsheet As...", FileDialog.SAVE);
        
        revertLSFC = new JFileChooser();
        
        vocfc = new JFileChooser();
        
        midfc = new JFileChooser();

        

        // MIDI Preferences Dialog init
        
        midiManager = imp.ImproVisor.getMidiManager();
        
        midiSynth = new MidiSynth(midiManager);
        
        setStepInput(false);
        
        
        // Establish Directories

        try
          {
          basePath = baseDir.getCanonicalPath().substring(0, baseDir.getCanonicalPath().length()-5);
          }
        catch( Exception e)    
          {
          assert(false);
          }
        
//        System.out.println("basePath = " + basePath);          
        
        vocabDir = new File(basePath + vocabDirName);
        
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
        
        
        

        
        // set the initial file to be null
        
        savedLeadsheet = null;
        

        melodyInst = new InstrumentChooser();
        
        chordInst = new InstrumentChooser();
        
        bassInst = new InstrumentChooser();
        
        defMelodyInst = new InstrumentChooser();
        
        defChordInst = new InstrumentChooser();
        
        defBassInst = new InstrumentChooser();
        
        repainter = new ActionListener() {
            
            public void actionPerformed(ActionEvent evt) {
                
                if(getPlaying() == MidiPlayListener.Status.STOPPED)
                    
                    return;
                
                
                
                int slotInPlayback = midiSynth.getSlot();
                
                if(slotInPlayback == midiSynth.getTotalSlots())
                    
                    return;
                
                
                
                slotInPlayback += playbackOffset;
                
                
                
                int chorusSize = getScore().getLength();
                
                currentPlaybackTab = slotInPlayback / chorusSize;
                
                slotInPlayback %= chorusSize;
                
                
                
                if(autoScrollOnPlayback && currentPlaybackTab != currTabIndex && showPlayLine()) {
                    
                    playbackGoToTab(currentPlaybackTab);
                    
                }
                
                
                
                // only draw the playback indicator if it is on the current tab
                
                if(currentPlaybackTab == currTabIndex) {
                    
                    staveScrollPane[currentPlaybackTab].getStave().repaintDuringPlayback(slotInPlayback);
                    
                    if(autoScrollOnPlayback && showPlayLine()) {
                        
                        Rectangle p = getCurrentStave().getPlayLine();
                        
                        if(p.height == 0)
                            
                            return;
                        
                        Rectangle r = getCurrentScrollPosition();
                        
                        if(!r.contains(p)) {
                            
                            // If out of view, try adjusting x-coordinate first
                            
                            int adjust = getCurrentStave().leftMargin + 10;
                            
                            if(r.width < adjust)
                                
                                adjust = 0;
                            
                            
                            
                            r.x = p.x - adjust;
                            
                            
                            
                            if(r.x < 0)
                                
                                r.x = 0;
                            
                            
                            
                            // If still out of view, try adjusting the y-coordinate
                            
                            if(!r.contains(p)) {
                                
                                r.y = p.y;
                                
                                
                                
                                if(p.y < 0)
                                    
                                    p.y = 0;
                                
                            }
                            
                            if(r.contains(p))
                                
                                setCurrentScrollPosition(r);
                            
                        }
                        
                    }
                    
                }
                
            }
            
        };
        
        

        initComponents();
        
        postInitComponents();
        
        
        
        // sets the title of the frames to the score title
        
        setTitle(score.getTitle());
        
        
        
        // setup the stave and part arrays and draw them
        
        setupArrays();
        
        
        
        tempoSet.setText("" + score.getTempo());
        
//        tempoSlider.setValue((int) score.getTempo());
        
        midiSynth.setTempo((float) score.getTempo());
        
        
        
        // set the menu and button states
        
        setItemStates();
        
        
        
        //okErrorBtn = ErrorLog.setDialog(errorDialog);

        
        score.getChordProg().setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));
        
        sectionInfo = score.getChordProg().getSectionInfo().copy();
        
        
        
        playTransposed.setText("0");
        
  
/*        
        impro.setPlayEntrySounds(true);
        
        impro.setTranspose(0);
        
        
        getCurrentOrigPart().setInstrument(
                
                Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_MELODY_INSTRUMENT)) - 1);
        
        
        
        score.setChordInstrument(
                
                Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_CHORD_INSTRUMENT)) - 1);
        
        
        
        //Style style = score.getChordProg().getStyle();
        
        score.setBassInstrument(
                
                Integer.parseInt(Preferences.getPreference(Preferences.DEFAULT_BASS_INSTRUMENT)) - 1);
        
*/        
       
        
//        updateSelection();
        
        
        
        Toolkit.getDefaultToolkit().addAWTEventListener(new AWTEventListener(){
            
            public void eventDispatched(AWTEvent event) {
                
                if(((KeyEvent)event).getKeyCode() == KeyEvent.VK_ESCAPE) {
                    
                 };
                
            }}, AWTEvent.KEY_EVENT_MASK);
            
            
            
            
            
            setVolumeDefaults();
            
            
            
            cm.changedSinceLastSave(false);
            
            
            
            if(x == -1 && y == -1) {
                
                this.setLocationRelativeTo(null);
                
            } else {
                
                this.setLocation(x, y);
                
            }
            
            
            
            setVisible(true);
            
            staveRequestFocus();
            
    }
    
    
    
    public void postInitComponents() {
        
        
        // NOTE: This is tricky, because the IDE doesn't do it for you.
        
        // resize the frames and make the appropriate frames visible
        
        this.setSize(fWidth, fHeight);
        
        
        
        
        
        /**
         *
         * configure the playback manager with the components that it should manage
         *
         */
        
        playbackManager = new PlaybackSliderManager(midiSynth, playbackTime, playbackTotalTime, playbackSlider, repainter);
        
    }
    
    
    
    public Advisor getAdvisor() {
        
        return adv;
        
    }
    
    
    
    public Score getScore() {
        
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
    
    public boolean getAutoAdjust() {
        
        return autoAdjustStaves;
        
    }
    
    
    
    private boolean drawPlayLine;
    
    public void setShowPlayLine(boolean showLine) {
        
        drawPlayLine = showLine;
        
    }
    
    public boolean showPlayLine() {
        
      return false;  
    }
    
    
    
    private String windowTitle;
    
    public void setTitle(String title) {
        
        super.setTitle(windowTitlePrefix + (title.trim().equals("")?"":windowTitlePrefixSeparator) + title.trim());
        
        if( score != null ) 
        {
          score.setTitle(title);
        }
        
        windowTitle = title;
        
    }
    
    
    
    public String getTitle() {
        
        return windowTitle;
        
    }
    
    
    
    /**
     *
     * Returns whether paste should always overwrite notes
     *
     * @return boolean          true if paste should always overwrite
     *
     */
    
    public boolean getAlwaysPasteOver() {
        
        return alwaysPasteOver;
        
    }
    
    
    
    
    
    /**
     *
     * Returns the currently selected tab
     *
     * @return int              the currently selected tab
     *
     */
    
    public int getCurrTabIndex() {
        
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
    
    public MiniStave getStaveAtTab(int index) {
        
        if (index >= 0 && index < staveScrollPane.length)
            
            return staveScrollPane[index].getStave();
        
        else
            
            return null;
        
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
    helpDialog = new javax.swing.JDialog();
    helpDialog.setSize(500, 500);
    helpTabbedPane = new javax.swing.JTabbedPane();
    alphaCommandPane = new javax.swing.JScrollPane();
    alphaCommandList = new javax.swing.JTextArea();
    helpByTopic = new javax.swing.JScrollPane();
    helpByTopicList = new javax.swing.JTextArea();
    melodyNotation = new javax.swing.JScrollPane();
    melodyNotationHelp = new javax.swing.JTextArea();
    styleHelpPane = new javax.swing.JScrollPane();
    styleHelpList1 = new javax.swing.JTextArea();
    lickGenSettingsPane = new javax.swing.JScrollPane();
    lickGenSettings = new javax.swing.JTextArea();
    drawingHelpPane = new javax.swing.JScrollPane();
    drawingHelp = new javax.swing.JTextArea();
    chordListingPane = new javax.swing.JScrollPane();
    chordList = new javax.swing.JTextArea();
    errorDialog = new javax.swing.JDialog();
    errorDialog.setSize(440, 310);
    okErrorBtn = new javax.swing.JButton();
    errorScroll = new javax.swing.JScrollPane();
    errorText = new javax.swing.JTextPane();
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
    melodyInstAllChangeCB = new javax.swing.JCheckBox();
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
    keySigBtnGroup = new javax.swing.ButtonGroup();
    newScoreDialog = new javax.swing.JDialog();
    newScoreDialog.setSize(450, 340);
    newScorePanel = new javax.swing.JPanel();
    measuresTF = new javax.swing.JTextField();
    jLabel5 = new javax.swing.JLabel();
    jLabel6 = new javax.swing.JLabel();
    keySigTF = new javax.swing.JTextField();
    sharpsRBtn = new javax.swing.JRadioButton();
    flatsRBtn = new javax.swing.JRadioButton();
    jLabel7 = new javax.swing.JLabel();
    metreTF = new javax.swing.JTextField();
    jLabel8 = new javax.swing.JLabel();
    jLabel9 = new javax.swing.JLabel();
    titleTF = new javax.swing.JTextField();
    okNewScoreBtn = new javax.swing.JButton();
    cancelNewScoreBtn = new javax.swing.JButton();
    defLoadStaveBtnGroup = new javax.swing.ButtonGroup();
    saveTypeButtonGroup = new javax.swing.ButtonGroup();
    staveChoiceButtonGroup = new javax.swing.ButtonGroup();
    helpTreeScroll1 = new javax.swing.JScrollPane();
    alphabeticKeyBindingsHelp = new javax.swing.JTextArea();
    prefsTabBtnGrp = new javax.swing.ButtonGroup();
    staveMenu = new javax.swing.JMenu();
    staveTypeMenu = new javax.swing.JMenu();
    trebleStaveMI = new javax.swing.JRadioButtonMenuItem();
    bassStaveMI = new javax.swing.JRadioButtonMenuItem();
    grandStaveMI = new javax.swing.JRadioButtonMenuItem();
    autoStaveMI = new javax.swing.JRadioButtonMenuItem();
    jSeparator3 = new javax.swing.JSeparator();
    addTabMI = new javax.swing.JMenuItem();
    delTabMI = new javax.swing.JMenuItem();
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
    lickGenPanel = new javax.swing.JPanel();
    chordProbSeparator = new javax.swing.JSeparator();
    chordProbPanel = new javax.swing.JPanel();
    triageOptionsPanel = new javax.swing.JPanel();
    gradeLabel = new javax.swing.JLabel();
    saveLickTF = new javax.swing.JTextField();
    jLabel1 = new javax.swing.JLabel();
    jPanel8 = new javax.swing.JPanel();
    rhythmScrollPane = new javax.swing.JScrollPane();
    rhythmField = new javax.swing.JTextArea();
    generateLickButton = new javax.swing.JButton();
    genRhythmButton = new javax.swing.JButton();
    fillMelodyButton = new javax.swing.JButton();
    getSelRhythmButton = new javax.swing.JButton();
    playLickButton = new javax.swing.JButton();
    stopLickButton = new javax.swing.JButton();
    saveLickButton = new javax.swing.JButton();
    jPanel3 = new javax.swing.JPanel();
    grade1Btn = new javax.swing.JButton();
    grade1Btn.setUI(new MetalButtonUI());
    grade2Btn = new javax.swing.JButton();
    grade2Btn.setUI(new MetalButtonUI());
    grade3Btn = new javax.swing.JButton();
    grade3Btn.setUI(new MetalButtonUI());
    grade4Btn = new javax.swing.JButton();
    grade4Btn.setUI(new MetalButtonUI());
    grade5Btn = new javax.swing.JButton();
    grade5Btn.setUI(new MetalButtonUI());
    grade6Btn = new javax.swing.JButton();
    grade6Btn.setUI(new MetalButtonUI());
    grade7Btn = new javax.swing.JButton();
    grade7Btn.setUI(new MetalButtonUI());
    grade8Btn = new javax.swing.JButton();
    grade8Btn.setUI(new MetalButtonUI());
    grade9Btn = new javax.swing.JButton();
    grade9Btn.setUI(new MetalButtonUI());
    grade10Btn = new javax.swing.JButton();
    grade10Btn.setUI(new MetalButtonUI());
    jPanel6 = new javax.swing.JPanel();
    jPanel4 = new javax.swing.JPanel();
    pitchLabel = new javax.swing.JLabel();
    maxLabel = new javax.swing.JLabel();
    maxPitchField = new javax.swing.JTextField();
    minLabel = new javax.swing.JLabel();
    minPitchField = new javax.swing.JTextField();
    intervalLabel = new javax.swing.JLabel();
    minIntervalField = new javax.swing.JTextField();
    maxIntervalField = new javax.swing.JTextField();
    durationLabel = new javax.swing.JLabel();
    minDurationField = new javax.swing.JTextField();
    maxDurationField = new javax.swing.JTextField();
    totalBeatsField = new javax.swing.JTextField();
    totalBeatsLabel = new javax.swing.JLabel();
    restProbLabel = new javax.swing.JLabel();
    restProbField = new javax.swing.JTextField();
    leapProbLabel = new javax.swing.JLabel();
    leapProbField = new javax.swing.JTextField();
    jPanel5 = new javax.swing.JPanel();
    weightLabel = new javax.swing.JLabel();
    chordToneProbLabel = new javax.swing.JLabel();
    colorToneProbLabel = new javax.swing.JLabel();
    scaleToneProbLabel = new javax.swing.JLabel();
    chordToneDecayRateLabel = new javax.swing.JLabel();
    chordToneWeightField = new javax.swing.JTextField();
    colorToneWeightField = new javax.swing.JTextField();
    scaleToneWeightField = new javax.swing.JTextField();
    chordToneDecayField = new javax.swing.JTextField();
    jPanel7 = new javax.swing.JPanel();
    scaleLabel = new javax.swing.JLabel();
    typeLabel = new javax.swing.JLabel();
    scaleComboBox = new javax.swing.JComboBox();
    rootLabel = new javax.swing.JLabel();
    rootComboBox = new javax.swing.JComboBox();
    FillProbsButton = new javax.swing.JButton();
    clearProbsButton = new javax.swing.JButton();
    autoFillCheckBox = new javax.swing.JCheckBox();
    lickSavedLabel = new javax.swing.JLabel();
    toolbarPanel = new javax.swing.JPanel();
    standardToolbar = new javax.swing.JToolBar();
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
    jSeparator25 = new javax.swing.JToolBar.Separator();
    addTabBtn = new javax.swing.JButton();
    addTabBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addTabMIActionPerformed(evt);
      }
    });
    delTabBtn = new javax.swing.JButton();
    delTabBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        delTabMIActionPerformed(evt);
      }
    });
    helpBtn = new javax.swing.JButton();
    jSeparator27 = new javax.swing.JToolBar.Separator();
    playBtn = new javax.swing.JButton();
    pauseBtn = new javax.swing.JToggleButton();
    stopBtn = new javax.swing.JButton();
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
    BPM = new javax.swing.JLabel();
    tempoSlider = new javax.swing.JSlider();
    playTransposed = new javax.swing.JTextField();
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
    jSeparator15 = new javax.swing.JSeparator();
    pasteOverMI = new javax.swing.JCheckBoxMenuItem();
    jSeparator16 = new javax.swing.JSeparator();
    enterMelodyMI = new javax.swing.JMenuItem();
    enterChordsMI = new javax.swing.JMenuItem();
    enterBothMI = new javax.swing.JMenuItem();
    jSeparator13 = new javax.swing.JSeparator();
    transposeMelodyUpSemitone = new javax.swing.JMenuItem();
    transposeChordsUpSemitone = new javax.swing.JMenuItem();
    transposeBothUpSemitone = new javax.swing.JMenuItem();
    transposeMelodyDownSemitone = new javax.swing.JMenuItem();
    transposeChordsDownSemitone = new javax.swing.JMenuItem();
    transposeBothDownSemitone = new javax.swing.JMenuItem();
    transposeMelodyUpOctave = new javax.swing.JMenuItem();
    transposeMelodyDownOctave = new javax.swing.JMenuItem();
    jSeparator19 = new javax.swing.JSeparator();
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
    jSeparator21 = new javax.swing.JSeparator();
    insertRestMeasure = new javax.swing.JMenuItem();
    addRestMI = new javax.swing.JMenuItem();
    viewMenu = new javax.swing.JMenu();
    oneAutoMI = new javax.swing.JMenuItem();
    autoAdjustMI = new javax.swing.JCheckBoxMenuItem();
    jSeparator4 = new javax.swing.JSeparator();
    showTitlesMI = new javax.swing.JCheckBoxMenuItem();
    showEmptyTitlesMI = new javax.swing.JCheckBoxMenuItem();
    barNumsMI = new javax.swing.JCheckBoxMenuItem();
    measureCstrLinesMI = new javax.swing.JCheckBoxMenuItem();
    allCstrLinesMI = new javax.swing.JCheckBoxMenuItem();
    playMenu = new javax.swing.JMenu();
    playAllMI = new javax.swing.JMenuItem();
    stopPlayMI = new javax.swing.JMenuItem();
    windowMenu = new javax.swing.JMenu();
    closeWindowMI = new javax.swing.JMenuItem();
    cascadeMI = new javax.swing.JMenuItem();
    windowMenuSeparator = new javax.swing.JSeparator();
    helpMenu = new javax.swing.JMenu();
    helpMI = new javax.swing.JMenuItem();

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

    copyBothPMI.setText("Copy");
    copyBothPMI.setEnabled(false);
    popupMenu.add(copyBothPMI);

    pasteBothPMI.setText("Paste Melody and Chords");
    pasteBothPMI.setEnabled(false);
    popupMenu.add(pasteBothPMI);

    helpDialog.setTitle("Impro-Visor Help");
    helpDialog.setName("helpDialog"); // NOI18N
    helpDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    helpTabbedPane.setToolTipText("Alphabetic Listing of Key Commands");
    helpTabbedPane.setMinimumSize(new java.awt.Dimension(500, 700));
    helpTabbedPane.setPreferredSize(new java.awt.Dimension(600, 800));

    alphaCommandPane.setMinimumSize(new java.awt.Dimension(500, 800));
    alphaCommandPane.setPreferredSize(new java.awt.Dimension(600, 900));

    alphaCommandList.setColumns(20);
    alphaCommandList.setFont(new java.awt.Font("Monospaced", 0, 13));
    alphaCommandList.setRows(5);
    alphaCommandList.setText("Impro-Visor Key Bindings Alphabetically (^ means hold control key)\n\nNote: Key commands are case-sensitive.\n\n a\tMove selection backward to previous grid line.\n^a\tSelect all chords and melody.\n\n b\tEnter melody from text entry field.\n B\tEnter chords from text entry field.\n^b\tEnter melody and chords from text entry field.\n\n c\tCopy melody in selection to clipboard.\n C\tCopy chords in selection to clipboard.\n^c\tCopy melody and chords in selection to clipboard.\n \n d\tTranspose selected melody down 1/2 step.\n D\tTranspose selected chords down 1/2 step.\n^d\tTranspose selected melody and chords down 1/2 step.\n\n e\tTranspose selected melody up 1/2 step.\n E\tTranspose selected chords up 1/2 step.\n^e\transpose selected melody and chords up 1/2 step.\n\n f\tMove selection forward to next grid line.\n\n g\tTranspose selected melody down one octave.\n\n i\tStart playback of entire sheet.\n\n j\tCopy melody of selection to text window.\n J\tCopy chords of selection to text window.\n\n^j\tCopy chords and melody of selection to text window.\n\n k\tStop playback.\n\n^l\tPerform one-time auto-layout adjustment.\n\n M\tMark advice cell, idiom, lick, or quote.\n\n m\tUnmark advice cell, idiom, lick, or quote.\n\n^n\tStart a new leadsheet in addition to the current one.\n\n^o\tOpen another leadsheet in place of the current one.\n\n^q\tQuit Impro-Visor.\n\n r\tAdd  a rest at selected grid line.\n^r\tRevert file to previous save.\n\n^s\tSave leadsheet file.\n\n t\tTranspose selected melody up one octave.\n\n u\tSave selection in the vocabulary as a cell, lick, idiom, or quote.\n\n v\tPaste melody from clipboard.\n V\tPaste chords from clipboard.\n^v\tPaste melody and chords from clipboard.\n\n^w\tWrite leadsheet file as a specified name.\n\n x\tCut melody in selection, saving to clipboard.\n X\tCut chords in selection, saving to clipboard.\n^x\tCut melody and chords in selection, saving to clipboard.\n\n y\tRedo undone command.\n\n z\tUndo previous command.\n\n /\tReverse melody in selection.\n \\\tInvert melody in selection.\n\nspace\tToggle enharmonic notes in selection.\n\nescape\tUnselect everything.\n\nenter\tPlay selection.\nshift-Enter Play from selection to end of sheet.\n");
    alphaCommandPane.setViewportView(alphaCommandList);

    helpTabbedPane.addTab("Keys", alphaCommandPane);

    helpByTopic.setMinimumSize(new java.awt.Dimension(500, 800));
    helpByTopic.setPreferredSize(new java.awt.Dimension(600, 900));

    helpByTopicList.setColumns(20);
    helpByTopicList.setFont(new java.awt.Font("Palace Script MT", 0, 13));
    helpByTopicList.setRows(5);
    helpByTopicList.setText("Impro-Visor Help By Topic\n\nWe give the key-stroke version of most commands. The menus can also be used to refresh your memory.\n\nGrid Lines:\n\n\tGrid lines indicate potential note, rest, and chord starting and ending points. \n\tGrid lines appear in a measure when the mouse is moved over it.\n\tMultiple adjacent grid lines may be selected.\n\tSome commands, such as Advice, only function if a single grid line is selected.\n\tIf one or more grid lines are selected, we call this \"the selection\".\n\nTo select a grid line, or to extend a selection:\n\n\tSelected grid lines are shown by high-lighting the line and any note on it.\n\n\tIf no grid line is already selected, shift-click on the desired grid line.\n\tIf a grid line is already selected, shift-click twice on the desired grid line.\n\n\tShift-clicking on a grid line inside the selection causes just that grid line to be selected.\n\tShift-clicking on a grid line outside the selection extends the selection to include that grid line.\n\tControl-a selects all grid lines at once.\n\tEscape unselects everything.\n\tIf a single grid line is selected, it can be moved back and forth using keys f and a.\n\nEntering notes:\n\n\tClick on the intersection of any grid line and staff line or space.\n\tAlternatively, notes can be entered in the text entry field using leadsheet notation.\n\tAccidentals:\n\t\tUse keys e and d to raise and lower notes by half step.\n\t\tUse the space bar to change an accidental enharmonically.\n\t\tUse keys t and g to raise and lower notes by octave.\n\tIf you make a mistake entering a note, just click another note on the same grid line.\n\tYou may also undo one or more note entry actions with key z.\n\tThe duration values of notes will be long when you first click.\n\tClick over the tied portion to enter the next note in a sequence.\n\tIf you want a rest, click a note or select a grid line, then press key r.\n\t\nRemoving notes:\n\n\tSelect the grid line that begins the note you want to remove. Then press either r or x.\n\tPressing r will replace the note with a rest.\n\tPressing x will remove the note, but extend the value of the previous note over it.\n\nExtending the duration of a note:\n\n\tIf the note to be extended is followed by a rest, select the grid line of that starts the rest,then press x.\n\nEntering chords:\n\n\tEnter the chord names in the text entry field, pressing enter when desired.\n\tChords will be entered starting at the first grid line selected.\n\tIf there is no grid line selected, nothing will happen.\n\tUse bars | to indicate measures.\n\tChords are spaced evenly within a measure. \n\tUse / to indicate more time for the previous chord.\n\tExample:\t| A B C / |\tmeans that C gets twice the value that A and B get.\n\tUse keys E and D to raise or lower a chord by half steps.\n\nCopying a selection:\n\n\tFirst create the selection by shift clicking as described above.\n\tPress c to copy the notes in the selection.\n\tPress C to copy the chords in the selection.\n\tPress control-c to copy both notes and chords.\n\tCopying takes place to a \"clip-board\" that is not visible.\n\tThe copied selection is not removed.\n\nPasting a selection:\n\n\tPasting is done from the invisible \"clip-board\" mentioned above.\n\tFirst select a single grid line.\n\tPress v to paste the notes previously copied or cut.\n\tPress V to paste the chords previously copied or cut.\n\tPress control-v to paste both notes and chords.\n\tThe selection can be pasted multiple times in different positions.\n\nCutting a selection:\n\n\tCutting is like copying, except the selection is removed as well.\n\tFirst create the selection by shift clicking as described above.\n\tPress x to copy the notes in the selection.\n\tPress X to copy the chords in the selection.\n\tPress control-x to copy both notes and chords.\n\tBoth cut and copy put notes and chords into the \"clip-board\".\n\nUndoing a paste or cut:\n\n\tUse key z to undo.\n\tMultiple undos are allowed.\n\tUse key y to redo something you just undid.\n\nPasting from the text entry field:\n\n\tBoth chords and notes can be entered through the text entry field.\n\tPressing return enters the contents at the current selection.\n\tOne can also select a grid line then re-enter the text entry items at that point as follows:\n\t\tPress b to enter the notes only.\n\t\tPress B to enter the chords only.\n\t\tPress control-b to enter both notes and chords.\n\nCopying a selection from the staff to the text entry:\n\n\tThis is a good way to learn leadsheet notation.\n\tCreate a selection.\n\tPress j to copy the notes in the selection to the text entry.\n\tPress J to copy the chords in the selection to the text entry.\n\tPress control-j to copy the notes and chords in the selection to the text entry.\n\nTransposing an entire selection:\n\n\tUse d to transpose selected notes down 1/2 step.\n \tUse D to transpose selected chords down 1/2 step.\n\tUse control-d to transpose selected notes and chords down 1/2 step.\n\tUse g to transpose selected notes down an octave.\n\n\tUse e to transpose selected notes up 1/2 step.\n \tUse E to transpose selected chords up 1/2 step.\n\tUse control-e to transpose selected notes and chords up 1/2 step.\n\tUse t to transpose selected notes up an octave.\n\nPlaying a selection or the entire piece:\n\n\tWith the mouse in the staff window, press enter to play the selection (notes and chords).\n\tPress shift-enter to play from the selection to the end of the leadsheet.\n\tPress i to play the entire leadsheet.\n\tPress k to stop playback.\n \nSaving licks, cells, idioms, or quotes:\n\n\tSelect what you want to save.\n\tPress u. A dialog will open that allows you to select the type and name the item.\n\nMarking and unmarking licks, cells, idioms, or quotes:\n\n\tMarking allows you to find the marked item in the vocabulary file using a text editor.\n\tUse M to mark a lick, cell, idiom, or quote.\n\tUse m to unmark an already-marked item.\n\nOther composer functions:\n\n\tUse / to reverse the notes in the selected  melody.\n\tUse \\ to invert the notes in the selected melody.\n\nLeadsheet file commands:\n\n\tUse control-o to open an existing leadsheet.\n\tUse control-s to save the current leadsheet under the same name.\n\tUse control-w to save the current leadsheet under a new name.\n\tUse control-r to revert the leadsheet to the last saved version. Changes since then are lost.\n\tUse control-n to open a new leadsheet. The current one stays in its own window.\n\nMiscellaneous:\n\tUse control-l to perform a one-time auto-layout adjustment of the measures per line.\n\tTo set a specific number of measures per line: shift click on the line, then select \"Over-ride ... \".\n\tControl-q quits improvisor.\n\n\n");
    helpByTopic.setViewportView(helpByTopicList);

    helpTabbedPane.addTab("Topics", helpByTopic);

    melodyNotation.setToolTipText("Melody notation");
    melodyNotation.setMinimumSize(new java.awt.Dimension(500, 800));
    melodyNotation.setPreferredSize(new java.awt.Dimension(600, 900));

    melodyNotationHelp.setColumns(20);
    melodyNotationHelp.setFont(new java.awt.Font("Monospaced", 0, 13));
    melodyNotationHelp.setRows(5);
    melodyNotationHelp.setText("Help for the Melody Part of Leadsheet Notation\n\nNote: Melody notation is case-sensitive.\n\nNotes must begin with a lower-case letter: a, b, c, d, e, f, g.\n\nA modifier # (sharp) or b (flat) may follow optionally.\n\nNotes are in the octave middle c and just above. \nTo specify a higher octave add a plus (+) for each octave. \nTo specify a lower octave add a minus (-) for each octave lower.\n\nThe duration of a note is specified by numbers:\n\n    1  = whole note\n    2  = half note\n    4  = quarter note\n    8  = eighth note\n    16 = sixteenth note\n    32 = thirty-second note\n\nFollowing a number by a dot (.) multiplies the value by 1.5. \n\nFollowing a number by /3 gives the value of a triplet (2/3 of the original value).\n\nDurations can be augmented by following a basic duration with a plus (+)\nthen the added duration.\n\nIf no duration is specified, an eighth note will be used.\n\nExamples:\n\n    a8    an 'a' above middle c, eighth note\n    eb8   an e-flat above middle c, eighth note\n    d+4   a 'd' in the second octave above middle c, quarter note\n    g-4/3 a 'g' in the octave below middle c, quarter-note triplet\n    c#4.  a c-sharp in the octave above middle c, dotted quarter note\n    f+2+8 an f in the second octave above middle c, half note plus an eighth\n\nTo see how any given note can be rendered in this notation, \nenter it in the GUI using point-and-click,\nthen use j to transfer it to the text entry field.\n\n");
    melodyNotation.setViewportView(melodyNotationHelp);

    helpTabbedPane.addTab("Melody", melodyNotation);

    styleHelpPane.setMinimumSize(new java.awt.Dimension(500, 800));
    styleHelpPane.setPreferredSize(new java.awt.Dimension(600, 900));

    styleHelpList1.setColumns(20);
    styleHelpList1.setFont(new java.awt.Font("Monospaced", 0, 13));
    styleHelpList1.setRows(5);
    styleHelpList1.setText("Help for Style Notation\n\nStyles are responsible for Impro-Visor's playback accompaniment.  Styles \nare specified in the vocabulary file as a set of patterns for each instrument\nof the accompaniment.  There are also style parameters that determine how\nthe patterns are used.\n\nPatterns\n\nEach pattern is defined by a set of rules.  The rules are similar to\nleadsheet notation, except the pitch is replaced with a capital letter\nindicating the rule.  Each pattern also has a weight that determines how\noften that pattern is used in relation to other patterns of the same length.\n\nChord Patterns\n\nChord patterns determine only the rhythm of the chord accompaniment. (The \nvoicings to use are decided based on a voice-leading algorithm.)\n\nChord patterns use the following rules:\n\nX       Strike the chord\nR       A rest\n\nBass Patterns\n\nBass patterns determine both the rhythm of the bassline and the type of\nnotes that are chosen.\n\nBass patterns use the following rules:\n\nB       The bass note (either the root of the chord, or the note specified\n        as part of a slash chord)\nC       A note from the current chord\nS       A note from the first scale associated with the current chord\nA       A tone that approaches the bass note of the next chord\nN       The bass note of the next chord (if at the end of a pattern\n        it will tie over to the next pattern)\nR       A rest\nX(n)    A specific note from the scale or chord (eg X(5) will give you the \n        fifth of the chord, X(6) will give you the sixth of the scale)\n\nDrum Patterns\n\nDrum patterns are a bit more complicated in that for one pattern you must\nspecify a drumline for each drum you want in the pattern.  Here is the\nformat for a drumline specification:\n\n(drum DRUM-MIDI-NUMBER RULE RULE ...)\n\nDrum patterns use the following rules:\n\nX       Strike the drum\nR       A rest\n\nGeneral MIDI Drum Numbers:\n\n35      Acoustic Bass Drum      59      Ride Cymbal 2\n36      Bass Drum 1             60      Hi Bongo\n37      Side Stick              61      Low Bongo\n38      Acoustic Snare          62      Mute Hi Conga\n39      Hand Clap               63      Open Hi Conga\n40      Electric Snare          64      Low Conga\n41      Low Floor Tom           65      High Timbale\n42      Closed Hi-Hat           66      Low Timbale\n43      High Floor Tom          67      High Agogo\n44      Pedal Hi-Hat            68      Low Agogo\n45      Low Tom                 69      Cabasa\n46      Open Hi-Hat             70      Maracas\n47      Low-Mid Tom             71      Short Whistle\n48      Hi-Mid Tom              72      Long Whistle\n49      Crash Cymbal 1          73      Short Guiro\n50      High Tom                74      Long Guiro\n51      Ride Cymbal 1           75      Claves\n52      Chinese Cymbal          76      Hi Wood Block\n53      Ride Bell               77      Low Wood Block\n54      Tambourine              78      Mute Cuica\n55      Splash Cymbal           79      Open Cuica\n56      Cowbell                 80      Mute Triangle\n57      Crash Cymbal 2          81      Open Triangle\n58      Vibraslap\n\nStyle Parameters\n\nname            The name of the style\nswing           The swing value for the style\naccompaniment-swing\tThe swing value for the accompaniment parts of the style\nbass-high       The highest note in the bass's range\nbass-low        The lowest note in the bass's range\nbass-base       A note that specifies the starting area for the bassline\nchord-high      The highest note in the chord's range\nchord-low       The lowest note in the chord's range\nvoicing-type    Sets the voicing type to be used\n\nExample Style\n\n(style\n    (name swing)\n    (swing 0.67)\n    (accompaniment-swing 0.67)\n    \n    (bass-high g-)\n    (bass-low g---)\n    (bass-base c--)\n    \n    (bass-pattern (rules B4 S4 C4 A4) (weight 10))\n    (bass-pattern (rules B4 C4 C4 A4) (weight 5))\n    (bass-pattern (rules B4 S4 C4 S4) (weight 3))\n    (bass-pattern (rules B4 S4 C4 A8 A8) (weight 3))\n    (bass-pattern (rules B4 S4 C4 S8 A8) (weight 3))\n    (bass-pattern (rules B4 C4) (weight 5))\n    (bass-pattern (rules B4 C8 A8) (weight 5))\n    (bass-pattern (rules B4 S4) (weight 3))\n    (bass-pattern (rules B4) (weight 5))\n    (bass-pattern (rules B4 A4) (weight 2))\n    \n    (drum-pattern\n        (drum 51 X4 X8 X8 X4 X8 X8)\n        (weight 10)\n        )\n    (drum-pattern\n        (drum 51 X4 X8 X8)\n        (weight 10)\n        )\n    (drum-pattern\n        (drum 51 X4)\n        (weight 10)\n        )\n        \n    (voicing-type closed)\n    (chord-high a)\n    (chord-low c-)\n    \n    (chord-pattern (rules X1) (weight 7))\n    (chord-pattern (rules X2) (weight 7))\n    (chord-pattern (rules X4) (weight 7))\n    (chord-pattern (rules X2+4 X4) (weight 5))\n    (chord-pattern (rules X4 R2+4) (weight 5))\n    (chord-pattern (rules X2 R2) (weight 7))\n    (chord-pattern (rules R4) (weight 1))\n    (chord-pattern (rules R8 X8+4 X4 R4) (weight 3))\n    (chord-pattern (rules R4 X8+4 X8 R4) (weight 5))\n    (chord-pattern (rules X8+2 X4 X8) (weight 2))\n    )\n\nStyle Tips\n\n   * The lengths of the patterns are important.  The software will attempt\n     to fill space with the largest pattern it can.  Try to correspond pattern\n     lengths to the probable chord durations.  If chords will mostly last for\n     4 beats, then have a 4 beat pattern.  If chords will last for 8 beats,\n     the software can fill that in with two 4 beat patterns.  Having a 1 beat\n     pattern for each accompaniment instrument is a good idea as a failsafe\n     for unconventional chord durations.\n   * Adding the parameter (no-style) to your style will cause the software\n     to leave out any bassline or drum part and play chords exactly where \n     they are specified on the leadsheet.\n   * Look to the styles included in the vocabulary file as a guide to\n     creating your own styles.\n   * Don't be afraid to experiment, but also don't be afraid to keep \n     things simple.\n");
    styleHelpPane.setViewportView(styleHelpList1);

    helpTabbedPane.addTab("Style", styleHelpPane);

    lickGenSettingsPane.setMinimumSize(new java.awt.Dimension(500, 800));
    lickGenSettingsPane.setPreferredSize(new java.awt.Dimension(600, 900));

    lickGenSettings.setColumns(20);
    lickGenSettings.setFont(new java.awt.Font("Monospaced", 0, 13));
    lickGenSettings.setRows(5);
    lickGenSettings.setText("Help for the Lick Generator\n\nThe lick generator and triage tool is a powerful and customizable\nutility that will create original licks over a given series of chord\nchanges.\n\nThe lick generator creates things in two steps; first, it generates\na rhythm using a context-free grammar.  Then, it fills in this\nrhythm probabilistically.  This allows for a large variety of \ndifferent melodys.  The grammar is specified in the text field under\n\"Grammar File.\"  This file is customizable and uses the following\nformat for terminal values:\n\n<Note type><Value>\n\nNote type can be one of\n     * X - any note\n     * R - a rest\n     * C - a chord tone (as specified in the vocabulary file)\n     * L - a color tone (as specified in the vocabulary file)\n     * S - a scale tone (as specified by the user.  If \"None\" is chosen, \n\tthis is ignored.  If use first scale is chosen, it looks\n\tat the first scale in the vocabulary file associated \n\twith the given chord.  If there is no such scale, this\n\tis ignored).\n     * A - an approach tone (forces the following note to be a\n \tchord tone, and approaches from a half step\n\tabove or below).\n\nValue can be from 1 (4 beats) to 16/3 (sixteenth note triplets).  \nLonger note values are not currently supported in the grammar.\n\nFor a more detailed explanation of how to modify the grammar\nfile, please see the \"Lick Generator\" tutorial.\n\nIf you do not wish to use the context-free grammar to generate a\nrhythm, deselect the \"Use Grammar\" button.  This tends to create\nless listenable fragments, however.\n\nBy default, the lick generator will tend to avoid repeating the same\nnote multiple times in a row.  If you would like to turn this off,\ndeselect the \"Avoid Repeat Pitch\" button.\n\n\"Generate\" creates a rhythm and fills it in using the probabilities.\n\n\"Generate Rhythm\" only creates a rhythm.\n\n\"Fill Melody\" generates a melody for a given rhythm (the rhythm\nis specified in the large text box; note that you can manually enter\nlonger note values here using leadsheet notation, as in X2+4 for\na three beat note)\n\n\"Get Selected Rhythm\" will read the rhythmic pattern of whatever\nis currently selected.\n\n\"Replay\" replays the current selection.\n\n\"Help\" displays this dialog.\n\n\"Reload\" will re-parse the grammar file.\n\n\"Save Lick\" saves the lick into the vocabulary.\n\nThe lick generator will automatically determine note probabilities \nfor all chords in the from the current selection start up to the\ntotal number of beats specified.  If you would like to disable this\nfeature, deselect the \"Auto-fill probabilities\" check box.  The\n\"Fill\" button will reset them to their defaults, and the \"Clear\" button\nsets them all to zero.\n\nOther options:\n     * Min/Max Pitch: Do not use pitches higher or lower than these\n\tvalues.\n     * Min/Max Interval: Do not jump higher or lower than this interval\n\tunless a jump probability is set.\n     * Min/Max Duration (only used if the grammar is disabled): Do\n\tnot generate notes with rhythmic values longer or\n\tshorter than these; note that these are inverse, so\n\t32 indicates a 32nd note and 1 indicates a whole note.\n     * Beats: The length of the lick to generate in beats.  This value is \n\tbased off of the current selection.\n     * Rest Probability (only used if the grammar is disabled): The\n\tprobability of a given note being a rest.\n     * Leap Probability: The probability of jumping outside the\n\tmaximum interval (see above).");
    lickGenSettingsPane.setViewportView(lickGenSettings);

    helpTabbedPane.addTab("Lick Generator", lickGenSettingsPane);

    drawingHelpPane.setMinimumSize(new java.awt.Dimension(500, 800));
    drawingHelpPane.setPreferredSize(new java.awt.Dimension(600, 900));

    drawingHelp.setColumns(20);
    drawingHelp.setFont(new java.awt.Font("Monospaced", 0, 13));
    drawingHelp.setRows(5);
    drawingHelp.setText("Help for Using the Draw (pencil) Tool\n\nThe Draw (pencil) tool allows you to input notes quickly by dragging the \nmouse continuously over the stave.  This makes it easy to sketch out \nthe 'melody contour' of your solo fluidly, without having to click at each\nindividual space.\n\nNote Fitting\n\nThe Draw tool does its best to fit notes along the curve you draw.  That is,\nit will add the pitch that roughly correponds to the stave note under\nthe cursor.  Since a melody contour defines the shape of the pitches\nin the music - but not necessarily specific pitches - the Draw tool allows\nyou to ignore harmonic rules when sketching out this shape; it will match\npitches to the nearest harmonious ones, for the current chord.  When\nno chord (\"NC\") is in effect, the Draw tool fits notes using the major\nscale of the score's key.\n\nNote Duration\n\nThe duration of notes on the drawn contour is determined by the note-\nspacing you can set below each measure.  If you want a triplet, for \nexample, in a particular measure, you can simply set the rhythm there\n(by dragging out the note spacing) and draw over that measure.  Notes\nwill automatically be fitted along the curve you draw, to that rhythm.\n\nDrawing Tips\n\n   * You can extend a note while drawing by holding the Shift key.  This\n     will 'flatline' the melody contour and increase the duration of the\n     current note being drawn. \n   * To add rest while drawing, you can hold the Control key.  Drawing\n     over the stave with Control down will insert rest there.  You may\n     also press 'R' while a given note is selected to insert rest there.\n   * When drawing over chord changes, you may want to force a tone\n     that 'approaches' the change before a given chord.  By holding\n     Shift-A, the Draw tool will always add an approach tone before a\n     chord change, and then resolve to the nearest chord tone.\n   * If you draw too quickly, the tool might \"miss\" a note, and not fit\n     it to the curve right away.  Don't panic!  Any notes that aren't fitted\n     properly will conform to the curve once you release the mouse button.\n   * Post-edit!  If you want to change a note or two, edit them after you\n     finish sketching.  Adjusting the pitch is easy; just drag up and down\n     on a note.  Using the keyboard controls, it's also easy to add rest\n     or extend notes afterwards.\n   * Want to hear the notes as they're being drawn?  Unmute the entry\n     sounds on the mixer, and Impro-Visor will remember your \n     preference during this session.  If you always want to hear them\n     by default, you may toggle the default settings under \n     \"Preferences -> Contour Drawing\".\n   * Maybe you only want the Draw tool to add chord tones or color\n     tones, rather than all scale tones.  Impro-Visor allows you to \n     set the Draw tool pitches under \"Preferences -> Contour Drawing\".\n");
    drawingHelpPane.setViewportView(drawingHelp);

    helpTabbedPane.addTab("Drawing", drawingHelpPane);

    chordListingPane.setToolTipText("Listing of Available Chords");
    chordListingPane.setMinimumSize(new java.awt.Dimension(500, 800));
    chordListingPane.setPreferredSize(new java.awt.Dimension(600, 900));

    chordList.setColumns(20);
    chordList.setEditable(false);
    chordList.setFont(new java.awt.Font("Monospaced", 0, 13));
    chordList.setRows(5);
    chordList.setText("These are the chord definitions supplied in the current release. \nIf you add your own chord definitions, they will not appear here.\n\nNote: Chord notation is case-sensitive.\n\nWe use root C, but any root may be used. The acceptable roots are:\nA, B, C, D, E, F, G, each of which may be followed by # (sharp) or b (flat).\n\nSlash chords (with bass other than the root) are allowed, e.g. Cm7/D means\nCm7 over a D bass. (The bass note may or may not be in the chord already.)\n\nPolychords use back-slash, e.g. Ab\\C7 means an Ab chord over a C7 chord.\n\nMajor Chords\n\nC.............. same as CM\nCM............. C major (c e g)\nC2............. same as CMadd9\nC4............. C four (c f bb)\nC5............. C five (c g)\nC6............. same as CM6\nC69............ same as CM69\nCM13#11........ C major thirteen sharp eleven (c e g b d f# a)\nCM13........... C major thirteen (c e g b d a)\nCM6............ C major six (c e g a)\nCM69#11........ C major six nine sharp eleven (c e g a d f#)\nCM69........... C major six nine (c e g a d)\nCM7#11......... C major seven sharp eleven (c e g b f#)\nCM7............ C major seven (c e g b)\nCM7add13....... C major seven add 13 (c e g a b d)\nCM7b5.......... C major seven flat five (c e gb b)\nCM7b6.......... C major seven flat six (c e g ab b)\nCM7b9.......... C major seven flat nine (c e g b db)\nCM9#11......... C major nine sharp eleven (c e g b d f#)\nCM9............ C major nine (c e g b d)\nCM9b5.......... C major nine flat five (c e gb b d)\nCMadd9......... C major add nine (c e g d)\nCMb5........... C major flat five (c e gb)\nCMb6........... C major flat six (c e ab)\nCadd2.......... same as CMadd9\nCadd9.......... same as CMadd9\n\nMinor Chords\n\nCm#5........... C minor sharp five (c eb g#)\nCm+............ same as Cm#5\nCm............. C minor (c eb g)\nCm11#5......... C minor eleven sharp five (c eb ab bb d f)\nCm11........... C minor eleven (c eb g bb d f)\nCm11b5......... C minor eleven flat five (c eb bb gb d f)\nCm13........... C minor thirteen (c eb g bb d f a)\nCm6............ C minor six (c eb g a)\nCm69........... C minor six nine (c eb g a d)\nCm7#5.......... C minor seven sharp five (c eb ab bb)\nCm7............ C minor seven (c eb g bb)\nCm7b5.......... C minor seven flat five (c eb gb bb)\nCh............. same as Cm7b5 (h for \"half-diminished\")\nCm9#5.......... C minor nine sharp five (c eb ab bb d)\nCm9............ C minor nine (c eb g bb d)\nCm9b5.......... C minor nine flat five (c eb bb gb d)\nCmM7........... C minor major seven (c eb g b)\nCmM7b6......... C minor major seven flat six (c eb g ab b)\nCmM9........... C minor major nine (c eb g b d)\nCmadd9......... C minor add nine (c eb g d)\nCmb6........... C minor flat six (c eb ab)\nCmb6M7......... C minor flat six major 7 (c eb ab b)\nCmb6b9......... C minor flat six flat nine (c eb ab db)\n\nAugmented Chords\n\nCM#5........... C major sharp five (c e g#)\nC+............. same as CM#5\nCaug........... same as CM#5\nC+7............ same as C7#5\nCM#5add9....... C major sharp five add 9 (c e g# d)\nCM7#5.......... C major seven sharp five (c e g# b)\nCM7+........... same as CM7#5\nCM9#5.......... C major nine sharp five (c e g# b d)\nC+add9......... same as CM#5add9\n\nDominant Chords\n\nC7............. C seven (c e g bb)\nC7#5........... C seven sharp five (c e g# bb)\nC7+............ same as C7#5\nCaug7.......... same as C7#5\nC7aug.......... same as C7#5\nC7#5#9......... C seven sharp five sharp nine (c e g# bb d#)\nC7alt.......... same as C7#5#9\nC7b13.......... C seven flat thirteen (c e g bb ab)\nC7b5#9......... same as C7#9#11\nC7b5........... C seven flat five (c e gb bb)\nC7b5b13........ same as C7#11b13\nC7b5b9......... same as C7b9#11\nC7b5b9b13...... same as C7b9#11b13\nC7b6........... C seven flat six (c e g ab bb)\nC7b9#11........ C seven flat nine sharp eleven (c e g bb db f#)\nC7b9#11b13..... C seven flat nine sharp eleven flat thirteen (c e g bb db f# ab)\nC7b9........... C seven flat nine (c e g bb db)\nC7b9b13#11..... C seven flat nine flat thirteen sharp eleven (c e g bb db f# ab)\nC7b9b13........ C seven flat nine flat thirteen (c e g bb db ab)\nC7no5.......... C seven no five (c e bb)\nC7#11.......... C seven sharp eleven (c e g bb f#)\nC7#11b13....... C seven sharp eleven flat thirteen (c e g bb f# ab)\nC7#5b9#11...... C seven sharp five flat nine sharp 11 (c e g# bb db f#)\nC7#5b9......... C seven sharp five flat nine (c e g# bb db)\nC7#9#11........ C seven sharp nine sharp eleven (c e g bb d# f#)\nC7#9#11b13..... C seven sharp nine sharp eleven flat thirteen (c e g bb d# f# ab)\nC7#9........... C seven sharp nine (c e g bb d#)\nC7#9b13........ C seven sharp nine flat thirteen (c e g bb d# ab)\n\nC9............. C nine (c e g bb d)\nC9#5........... C nine sharp five (c e g# bb d)\nC9+............ same as C9#5\nC9#11.......... C nine sharp eleven (c e g bb d f#)\nC9#11b13....... C nine sharp eleven flat thirteen (c e g bb d f# ab)\nC9#5#11........ C nine sharp five sharp eleven (c e g# bb d f#)\nC9b13.......... C nine flat thirteen (c e g bb d ab)\nC9b5........... C nine flat five (c e gb bb d)\nC9b5b13........ same as C9#11b13\nC9no5.......... C nine no five (c e bb d)\n\nC13#11......... C thirteen sharp eleven (c e g bb d f# a)\nC13#9#11....... C thirteen sharp nine sharp eleven (c e g bb d# f# a)\nC13#9.......... C thirteen sharp nine (c e g bb d# a)\nC13............ C thirteen (c e g bb d a)\nC13b5.......... C thirteen flat five (c e gb a bb)\nC13b9#11....... C thirteen flat nine sharp eleven (c e g bb db f# a)\nC13b9.......... C thirteen flat nine (c e g bb db a)\n\nSuspensions\n\nCMsus2......... C major sus two (c d g)\nCMsus4......... C major sus four (c f g)\nCsus2.......... same as CMsus2\nCsus4.......... same as CMsus4\nCsusb9......... C sus flat nine (c db f g)\nC7b9b13sus4.... same as C7sus4b9b13\nC7b9sus........ same as C7susb9\nC7b9sus4....... same as C7sus4b9\nC7b9sus4....... same as C7susb9\nC7sus.......... same as C7sus4\nC7sus4......... C seven sus four (c f g bb)\nC7sus4b9....... C seven sus four flat nine (c f g bb db)\nC7sus4b9b13.... C seven sus four flat nine flat thirteen (c f g bb db ab)\nC7susb9........ C seven sus flat nine (c db f g bb)\nC9sus4......... C nine sus four (c f g bb d)\nC9sus.......... same as C9sus4\nC11............ C eleven (c e g bb d f)\nC13sus......... same as C13sus4\nC13sus4........ C thirteen sus four (c f g bb d a)\n\nMiscellaneous\n\nCBlues......... C Blues (c eb f gb g bb) (Use upper case to avoid confusion with Cb = C flat)\nCBass.......... C Bass (c) (Use upper case to avoid confusion with Cb = C flat)\n");
    chordListingPane.setViewportView(chordList);

    helpTabbedPane.addTab("Chords", chordListingPane);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    helpDialog.getContentPane().add(helpTabbedPane, gridBagConstraints);

    errorDialog.setTitle("");
    errorDialog.setModal(true);
    errorDialog.setName("errorDialog"); // NOI18N
    errorDialog.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentShown(java.awt.event.ComponentEvent evt) {
        errorDialogComponentShown(evt);
      }
    });
    errorDialog.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        errorDialogKeyPressed(evt);
      }
    });
    errorDialog.getContentPane().setLayout(null);

    okErrorBtn.setText("OK");
    okErrorBtn.setSelected(true);
    okErrorBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okErrorBtnActionPerformed(evt);
      }
    });
    errorDialog.getContentPane().add(okErrorBtn);
    okErrorBtn.setBounds(340, 240, 70, 29);

    errorText.setEditable(false);
    errorScroll.setViewportView(errorText);

    errorDialog.getContentPane().add(errorScroll);
    errorScroll.setBounds(20, 20, 400, 210);

    errorDialog.getAccessibleContext().setAccessibleName("Notice");

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
    gridBagConstraints.gridy = 2;
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
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    chorusSpecificPanel.add(layoutLabel, gridBagConstraints);

    layoutTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    layoutTF.setToolTipText("Set a specific layout (bars per line).");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
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
    autoStaveBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
    autoStaveBtn.setNextFocusableComponent(trebleStaveBtn);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    staveButtonPanel.add(autoStaveBtn, gridBagConstraints);

    staveChoiceButtonGroup.add(trebleStaveBtn);
    trebleStaveBtn.setText("treble");
    trebleStaveBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
    trebleStaveBtn.setNextFocusableComponent(bassStaveBtn);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    staveButtonPanel.add(trebleStaveBtn, gridBagConstraints);

    staveChoiceButtonGroup.add(bassStaveBtn);
    bassStaveBtn.setText("bass");
    bassStaveBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
    bassStaveBtn.setNextFocusableComponent(grandStaveBtn);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    staveButtonPanel.add(bassStaveBtn, gridBagConstraints);

    staveChoiceButtonGroup.add(grandStaveBtn);
    grandStaveBtn.setText("grand");
    grandStaveBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.weightx = 1.0;
    staveButtonPanel.add(grandStaveBtn, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 0);
    chorusSpecificPanel.add(staveButtonPanel, gridBagConstraints);

    melodyInstAllChangeCB.setText("set as Default");
    melodyInstAllChangeCB.setMargin(new java.awt.Insets(0, 0, 0, 0));
    melodyInstPanel.add(melodyInstAllChangeCB);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
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
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
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
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(breakpointTF, gridBagConstraints);

    scoreTitleTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    scoreTitleTF.setNextFocusableComponent(composerField);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
    leadsheetSpecificPanel.add(scoreTitleTF, gridBagConstraints);

    measuresPerPartLabel.setText("Measures per Chorus:");
    measuresPerPartLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(measuresPerPartLabel, gridBagConstraints);

    prefMeasTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    prefMeasTF.setText("32");
    prefMeasTF.setMinimumSize(new java.awt.Dimension(50, 19));
    prefMeasTF.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(prefMeasTF, gridBagConstraints);

    tempoTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    tempoTF.setText("160");
    tempoTF.setMinimumSize(new java.awt.Dimension(50, 19));
    tempoTF.setNextFocusableComponent(breakpointTF);
    tempoTF.setPreferredSize(new java.awt.Dimension(50, 19));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(tempoTF, gridBagConstraints);

    tempoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    tempoLabel.setText("Tempo:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
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
    gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
    leadsheetSpecificPanel.add(leadsheetTitleLabel, gridBagConstraints);

    composerField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    composerField.setNextFocusableComponent(timeSignatureTopTF);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(composerField, gridBagConstraints);

    chordIInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    chordIInstLabel.setText("Chord MIDI Instrument:");
    chordIInstLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(chordIInstLabel, gridBagConstraints);

    bassInstLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    bassInstLabel.setText("Bass MIDI Instrument:");
    bassInstLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(bassInstLabel, gridBagConstraints);

    keySignatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    keySignatureLabel.setText("Key Signature (+sharps, - flats):");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
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
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(keySignatureTF, gridBagConstraints);

    commentsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    commentsLabel.setText("Comments:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(commentsLabel, gridBagConstraints);

    commentsTF.setHorizontalAlignment(javax.swing.JTextField.LEFT);
    commentsTF.setMinimumSize(new java.awt.Dimension(40, 19));
    commentsTF.setNextFocusableComponent(partTitleTF);
    commentsTF.setPreferredSize(new java.awt.Dimension(11, 23));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 7;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    leadsheetSpecificPanel.add(commentsTF, gridBagConstraints);

    timeSignatureLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    timeSignatureLabel.setText("Time Signature:");
    timeSignatureLabel.setAlignmentX(1.0F);
    timeSignatureLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
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
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 100;
    leadsheetSpecificPanel.add(timeSignaturePanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.gridwidth = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    leadsheetSpecificPanel.add(chordInstPanel, gridBagConstraints);
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 6;
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

    newScoreDialog.setTitle("New Score");
    newScoreDialog.setModal(true);
    newScoreDialog.setName("newScoreDialog"); // NOI18N
    newScoreDialog.getContentPane().setLayout(null);

    newScorePanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
    newScorePanel.setLayout(null);

    measuresTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    measuresTF.setText("32");
    newScorePanel.add(measuresTF);
    measuresTF.setBounds(210, 80, 180, 20);

    jLabel5.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel5.setText("Total Measures:");
    newScorePanel.add(jLabel5);
    jLabel5.setBounds(30, 80, 140, 20);

    jLabel6.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel6.setText("Key Signature:");
    newScorePanel.add(jLabel6);
    jLabel6.setBounds(30, 130, 140, 20);

    keySigTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    keySigTF.setText("0");
    newScorePanel.add(keySigTF);
    keySigTF.setBounds(210, 130, 80, 20);

    keySigBtnGroup.add(sharpsRBtn);
    sharpsRBtn.setSelected(true);
    sharpsRBtn.setText("Sharps");
    newScorePanel.add(sharpsRBtn);
    sharpsRBtn.setBounds(320, 130, 80, 22);

    keySigBtnGroup.add(flatsRBtn);
    flatsRBtn.setText("Flats");
    newScorePanel.add(flatsRBtn);
    flatsRBtn.setBounds(320, 160, 80, 22);

    jLabel7.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel7.setText("Time Signature:");
    newScorePanel.add(jLabel7);
    jLabel7.setBounds(30, 200, 140, 20);

    metreTF.setEditable(false);
    metreTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    metreTF.setText("4");
    newScorePanel.add(metreTF);
    metreTF.setBounds(210, 200, 80, 22);

    jLabel8.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel8.setText("/ 4");
    newScorePanel.add(jLabel8);
    jLabel8.setBounds(300, 200, 45, 17);

    jLabel9.setFont(new java.awt.Font("Dialog", 1, 14));
    jLabel9.setText("Leadsheet Title:");
    newScorePanel.add(jLabel9);
    jLabel9.setBounds(30, 30, 150, 20);

    titleTF.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    titleTF.setName("titleTF"); // NOI18N
    newScorePanel.add(titleTF);
    titleTF.setBounds(210, 30, 180, 22);

    newScoreDialog.getContentPane().add(newScorePanel);
    newScorePanel.setBounds(10, 10, 430, 250);

    okNewScoreBtn.setText("OK");
    okNewScoreBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        okNewScoreBtnActionPerformed(evt);
      }
    });
    newScoreDialog.getContentPane().add(okNewScoreBtn);
    okNewScoreBtn.setBounds(250, 270, 80, 29);

    cancelNewScoreBtn.setText("Cancel");
    cancelNewScoreBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        cancelNewScoreBtnActionPerformed(evt);
      }
    });
    newScoreDialog.getContentPane().add(cancelNewScoreBtn);
    cancelNewScoreBtn.setBounds(350, 270, 80, 29);

    helpTreeScroll1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
    helpTreeScroll1.setFont(new java.awt.Font("Monospaced", 0, 13));
    helpTreeScroll1.setPreferredSize(new java.awt.Dimension(300, 2000));

    alphabeticKeyBindingsHelp.setColumns(20);
    alphabeticKeyBindingsHelp.setRows(5);
    alphabeticKeyBindingsHelp.setText("Key Bindings Alphabetically (^ means hold control key)\n\n a     Move selection backward to previous grid line.\n^a    Select all chords and melody.\n\n b     Enter melody from text entry field.\n B     Enter chords from text entry field.\n^b    Enter melody and chords from text entry field.\n\n c     Copy melody in selection to clipboard.\n C     Copy chords in selection to clipboard.\n^c    Copy melody and chords in selection to clipboard.\n \n d     Transpose selected melody down 1/2 step.\n D     Transpose selected chords down 1/2 step.\n^d    Transpose selected melody and chords down 1/2 step.\n\n e     Transpose selected melody up 1/2 step.\n E     Transpose selected chords up 1/2 step.\n^e    Transpose selected melody and chords up 1/2 step.\n\n f     Move selection forward to next grid line.\n\n g     Transpose selected melody down one octave.\n\n h     Help.\n\n i     Start playback.\n\n j     Copy melody of selection to text window.\n J     Copy chords of selection to text window.\n\n^j    Copy chords and melody of selection to text window.\n\n k     Stop playback.\n\n^l    Perform one-time auto-layout adjustment.\n\nM     Mark advice cell, idiom, lick, or quote.\n\nm     Unmark advice cell, idiom, lick, or quote.\n\n^n    Start a new leadsheet in addition to the current one.\n\n^o    Open another leadsheet in place of the current one.\n\n^q    Quit Impro-Visor.\n\n r     Add  a rest at selected grid line.\n^r    Revert file to previous save.\n\n^s    Save leadsheet file.\n\n t     Transpose selected melody up one octave.\n\n u     Save selection in the vocabulary as a cell, lick, idiom, or quote.\n\n v     Paste melody from clipboard.\n V     Paste chords from clipboard.\n^v    Paste melody and chords from clipboard.\n\n^w    Write leadsheet file as a specified name.\n\n x     Cut melody in selection, saving to clipboard.\n X     Cut chords in selection, saving to clipboard.\n^x    Cut melody and chords in selection, saving to clipboard.\n\n y      Redo undone command.\n\n z     Undo previous command.\n\n /      Reverse melody in selection.\n \\      Invert melody in selection.\n\nspace Toggle enharmonic notes in selection.\nenter Play selection.\nshift-Enter Play from selection to end of sheet.\nescape Unselect everything.\n");
    helpTreeScroll1.setViewportView(alphabeticKeyBindingsHelp);

    staveMenu.setMnemonic('s');
    staveMenu.setText("Stave");
    staveMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        staveMenuActionPerformed(evt);
      }
    });

    staveTypeMenu.setText("Stave Type");
    staveTypeMenu.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        staveTypeMenuActionPerformed(evt);
      }
    });

    staveButtonGroup.add(trebleStaveMI);
    trebleStaveMI.setSelected(true);
    trebleStaveMI.setText("Treble Stave");
    trebleStaveMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        trebleStaveMIActionPerformed(evt);
      }
    });
    staveTypeMenu.add(trebleStaveMI);

    staveButtonGroup.add(bassStaveMI);
    bassStaveMI.setText("Bass Stave");
    bassStaveMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        bassStaveMIActionPerformed(evt);
      }
    });
    staveTypeMenu.add(bassStaveMI);

    staveButtonGroup.add(grandStaveMI);
    grandStaveMI.setText("Grand Stave");
    grandStaveMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grandStaveMIActionPerformed(evt);
      }
    });
    staveTypeMenu.add(grandStaveMI);

    staveButtonGroup.add(autoStaveMI);
    autoStaveMI.setText("Automatic");
    autoStaveMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        autoStaveMIActionPerformed(evt);
      }
    });
    staveTypeMenu.add(autoStaveMI);

    staveMenu.add(staveTypeMenu);
    staveMenu.add(jSeparator3);

    addTabMI.setMnemonic('t');
    addTabMI.setText("Add a Tabbed Part");
    addTabMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addTabMIActionPerformed(evt);
      }
    });
    staveMenu.add(addTabMI);

    delTabMI.setMnemonic('d');
    delTabMI.setText("Delete Current Tabbed Part");
    delTabMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        delTabMIActionPerformed(evt);
      }
    });
    staveMenu.add(delTabMI);

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
    allMuteMixerBtn.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
    entryMute.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
    bassMute.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
    drumMute.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
    chordMute.setMargin(new java.awt.Insets(0, 0, 0, 0));
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
    melodyMute.setMargin(new java.awt.Insets(0, 0, 0, 0));
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

    lickGenPanel.setLayout(new java.awt.GridBagLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
    lickGenPanel.add(chordProbSeparator, gridBagConstraints);

    chordProbPanel.setLayout(new java.awt.BorderLayout());
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    lickGenPanel.add(chordProbPanel, gridBagConstraints);

    triageOptionsPanel.setLayout(new java.awt.GridBagLayout());

    gradeLabel.setText("Grade:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 0);
    triageOptionsPanel.add(gradeLabel, gridBagConstraints);

    saveLickTF.setText("<Generated Lick>");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
    triageOptionsPanel.add(saveLickTF, gridBagConstraints);

    jLabel1.setText("Save Label:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 0);
    triageOptionsPanel.add(jLabel1, gridBagConstraints);

    jPanel8.setLayout(new java.awt.GridBagLayout());

    rhythmScrollPane.setPreferredSize(new java.awt.Dimension(223, 48));

    rhythmField.setColumns(20);
    rhythmField.setLineWrap(true);
    rhythmField.setRows(25);
    rhythmScrollPane.setViewportView(rhythmField);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.ipadx = 20;
    gridBagConstraints.weighty = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 3, 0);
    jPanel8.add(rhythmScrollPane, gridBagConstraints);

    generateLickButton.setText("Generate");
    generateLickButton.setToolTipText("Generate a lick.");
    generateLickButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        generateLickButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
    jPanel8.add(generateLickButton, gridBagConstraints);

    genRhythmButton.setText("Generate Rhythm");
    genRhythmButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        genRhythmButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
    jPanel8.add(genRhythmButton, gridBagConstraints);

    fillMelodyButton.setText("Fill Melody");
    fillMelodyButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        fillMelodyButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
    jPanel8.add(fillMelodyButton, gridBagConstraints);

    getSelRhythmButton.setText("Get Selected Rhythm");
    getSelRhythmButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        getSelRhythmButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
    jPanel8.add(getSelRhythmButton, gridBagConstraints);

    playLickButton.setText("Play");
    playLickButton.setToolTipText("Play the lick again.");
    playLickButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        playLickButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
    jPanel8.add(playLickButton, gridBagConstraints);

    stopLickButton.setText("Stop");
    stopLickButton.setToolTipText("Play the lick again.");
    stopLickButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        stopLickButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 5;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 0);
    jPanel8.add(stopLickButton, gridBagConstraints);

    saveLickButton.setText("Save Lick");
    saveLickButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        saveLickButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 6;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
    jPanel8.add(saveLickButton, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 10);
    triageOptionsPanel.add(jPanel8, gridBagConstraints);

    jPanel3.setLayout(new java.awt.GridLayout(1, 0));

    grade1Btn.setText("1");
    grade1Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade1Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade1Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade1Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade1BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade1Btn);

    grade2Btn.setText("2");
    grade2Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade2Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade2Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade2Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade2BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade2Btn);

    grade3Btn.setText("3");
    grade3Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade3Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade3Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade3Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade3BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade3Btn);

    grade4Btn.setText("4");
    grade4Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade4Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade4Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade4Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade4BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade4Btn);

    grade5Btn.setText("5");
    grade5Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade5Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade5Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade5Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade5BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade5Btn);

    grade6Btn.setText("6");
    grade6Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade6Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade6Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade6Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade6BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade6Btn);

    grade7Btn.setText("7");
    grade7Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade7Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade7Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade7Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade7BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade7Btn);

    grade8Btn.setText("8");
    grade8Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade8Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade8Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade8Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade8BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade8Btn);

    grade9Btn.setText("9");
    grade9Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade9Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade9Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade9Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade9BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade9Btn);

    grade10Btn.setText("10");
    grade10Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
    grade10Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
    grade10Btn.setPreferredSize(new java.awt.Dimension(23, 21));
    grade10Btn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        grade10BtnActionPerformed(evt);
      }
    });
    jPanel3.add(grade10Btn);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 0);
    triageOptionsPanel.add(jPanel3, gridBagConstraints);

    jPanel6.setLayout(new java.awt.GridBagLayout());

    jPanel4.setLayout(new java.awt.GridBagLayout());

    pitchLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    pitchLabel.setText("Pitch");
    pitchLabel.setToolTipText("The minimum duration of a note (1, 2, 4, 8, 16).");
    pitchLabel.setPreferredSize(new java.awt.Dimension(80, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    jPanel4.add(pitchLabel, gridBagConstraints);

    maxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    maxLabel.setText("Max");
    maxLabel.setToolTipText("");
    maxLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    maxLabel.setMaximumSize(new java.awt.Dimension(30, 15));
    maxLabel.setMinimumSize(new java.awt.Dimension(30, 15));
    maxLabel.setPreferredSize(new java.awt.Dimension(30, 15));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel4.add(maxLabel, gridBagConstraints);

    maxPitchField.setFont(new java.awt.Font("Dialog", 0, 14));
    maxPitchField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    maxPitchField.setMinimumSize(new java.awt.Dimension(40, 24));
    maxPitchField.setPreferredSize(new java.awt.Dimension(40, 24));
    maxPitchField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        maxPitchFieldActionPerformed(evt);
      }
    });
    maxPitchField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        maxPitchFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        maxPitchFieldFocusLost(evt);
      }
    });
    maxPitchField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        maxPitchFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(maxPitchField, gridBagConstraints);

    minLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
    minLabel.setText("Min");
    minLabel.setToolTipText("");
    minLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
    minLabel.setMaximumSize(new java.awt.Dimension(30, 15));
    minLabel.setMinimumSize(new java.awt.Dimension(30, 15));
    minLabel.setPreferredSize(new java.awt.Dimension(30, 15));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    jPanel4.add(minLabel, gridBagConstraints);

    minPitchField.setFont(new java.awt.Font("Dialog", 0, 14));
    minPitchField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    minPitchField.setMinimumSize(new java.awt.Dimension(40, 24));
    minPitchField.setPreferredSize(new java.awt.Dimension(40, 24));
    minPitchField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        minPitchFieldActionPerformed(evt);
      }
    });
    minPitchField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        minPitchFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        minPitchFieldFocusLost(evt);
      }
    });
    minPitchField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        minPitchFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(minPitchField, gridBagConstraints);

    intervalLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    intervalLabel.setText("Interval");
    intervalLabel.setToolTipText("");
    intervalLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    intervalLabel.setPreferredSize(new java.awt.Dimension(80, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    jPanel4.add(intervalLabel, gridBagConstraints);

    minIntervalField.setFont(new java.awt.Font("Dialog", 0, 14));
    minIntervalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    minIntervalField.setToolTipText("Minimum inteval in melody");
    minIntervalField.setMinimumSize(new java.awt.Dimension(40, 24));
    minIntervalField.setPreferredSize(new java.awt.Dimension(40, 24));
    minIntervalField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        minIntervalFieldActionPerformed(evt);
      }
    });
    minIntervalField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        minIntervalFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        minIntervalFieldFocusLost(evt);
      }
    });
    minIntervalField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        minIntervalFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(minIntervalField, gridBagConstraints);

    maxIntervalField.setFont(new java.awt.Font("Dialog", 0, 14));
    maxIntervalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    maxIntervalField.setMinimumSize(new java.awt.Dimension(40, 24));
    maxIntervalField.setPreferredSize(new java.awt.Dimension(40, 24));
    maxIntervalField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        maxIntervalFieldActionPerformed(evt);
      }
    });
    maxIntervalField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        maxIntervalFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        maxIntervalFieldFocusLost(evt);
      }
    });
    maxIntervalField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        maxIntervalFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(maxIntervalField, gridBagConstraints);

    durationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    durationLabel.setText("Duration");
    durationLabel.setToolTipText("The maximum interval between two pitches in the lick");
    durationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    durationLabel.setPreferredSize(new java.awt.Dimension(80, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
    jPanel4.add(durationLabel, gridBagConstraints);

    minDurationField.setFont(new java.awt.Font("Dialog", 0, 14));
    minDurationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    minDurationField.setEnabled(false);
    minDurationField.setMinimumSize(new java.awt.Dimension(40, 24));
    minDurationField.setPreferredSize(new java.awt.Dimension(40, 24));
    minDurationField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        minDurationFieldActionPerformed(evt);
      }
    });
    minDurationField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        minDurationFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        minDurationFieldFocusLost(evt);
      }
    });
    minDurationField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        minDurationFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(minDurationField, gridBagConstraints);

    maxDurationField.setFont(new java.awt.Font("Dialog", 0, 14));
    maxDurationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    maxDurationField.setEnabled(false);
    maxDurationField.setMinimumSize(new java.awt.Dimension(40, 24));
    maxDurationField.setPreferredSize(new java.awt.Dimension(40, 24));
    maxDurationField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        maxDurationFieldActionPerformed(evt);
      }
    });
    maxDurationField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        maxDurationFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        maxDurationFieldFocusLost(evt);
      }
    });
    maxDurationField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        maxDurationFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(maxDurationField, gridBagConstraints);

    totalBeatsField.setFont(new java.awt.Font("Dialog", 0, 14));
    totalBeatsField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    totalBeatsField.setMinimumSize(new java.awt.Dimension(40, 24));
    totalBeatsField.setPreferredSize(new java.awt.Dimension(40, 24));
    totalBeatsField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        totalBeatsFieldActionPerformed(evt);
      }
    });
    totalBeatsField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        totalBeatsFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        totalBeatsFieldFocusLost(evt);
      }
    });
    totalBeatsField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        totalBeatsFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(totalBeatsField, gridBagConstraints);

    totalBeatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    totalBeatsLabel.setText("Quarter Notes:");
    totalBeatsLabel.setToolTipText("The total number of beats for the lick.");
    totalBeatsLabel.setPreferredSize(new java.awt.Dimension(100, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
    jPanel4.add(totalBeatsLabel, gridBagConstraints);

    restProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    restProbLabel.setText("Rest Prob.");
    restProbLabel.setToolTipText("The probability of generating a rest");
    restProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    restProbLabel.setPreferredSize(new java.awt.Dimension(80, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
    jPanel4.add(restProbLabel, gridBagConstraints);

    restProbField.setFont(new java.awt.Font("Dialog", 0, 14));
    restProbField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
    restProbField.setEnabled(false);
    restProbField.setMinimumSize(new java.awt.Dimension(40, 24));
    restProbField.setPreferredSize(new java.awt.Dimension(40, 24));
    restProbField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        restProbFieldActionPerformed(evt);
      }
    });
    restProbField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        restProbFieldGetsFocus(evt);
      }
      public void focusLost(java.awt.event.FocusEvent evt) {
        restProbFieldFocusLost(evt);
      }
    });
    restProbField.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(java.awt.event.KeyEvent evt) {
        restProbFieldenterLickKeyPressed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(restProbField, gridBagConstraints);

    leapProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    leapProbLabel.setText("Leap Prob.");
    leapProbLabel.setPreferredSize(new java.awt.Dimension(80, 14));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 3;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
    jPanel4.add(leapProbLabel, gridBagConstraints);

    leapProbField.setMinimumSize(new java.awt.Dimension(40, 24));
    leapProbField.setPreferredSize(new java.awt.Dimension(40, 24));
    leapProbField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        leapProbFieldActionPerformed(evt);
      }
    });
    leapProbField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        leapProbFieldFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 4;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
    jPanel4.add(leapProbField, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    jPanel6.add(jPanel4, gridBagConstraints);

    jPanel5.setLayout(new java.awt.GridBagLayout());

    weightLabel.setText("Weights:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 5, 0);
    jPanel5.add(weightLabel, gridBagConstraints);

    chordToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    chordToneProbLabel.setText("<html>Chord <br>Tone</html");
    chordToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    chordToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    jPanel5.add(chordToneProbLabel, gridBagConstraints);

    colorToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    colorToneProbLabel.setText("<html>Color<br>Tone</html>");
    colorToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    colorToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    jPanel5.add(colorToneProbLabel, gridBagConstraints);

    scaleToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    scaleToneProbLabel.setText("<html>Scale <br>Tone</html>");
    scaleToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    scaleToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    jPanel5.add(scaleToneProbLabel, gridBagConstraints);

    chordToneDecayRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    chordToneDecayRateLabel.setText("<html><align=center>Chord Tone <br> Decay Rate </align></html>");
    chordToneDecayRateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    chordToneDecayRateLabel.setPreferredSize(new java.awt.Dimension(80, 30));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 1;
    jPanel5.add(chordToneDecayRateLabel, gridBagConstraints);

    chordToneWeightField.setMinimumSize(new java.awt.Dimension(40, 24));
    chordToneWeightField.setPreferredSize(new java.awt.Dimension(40, 24));
    chordToneWeightField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chordToneWeightFieldActionPerformed(evt);
      }
    });
    chordToneWeightField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        chordToneWeightFieldFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    jPanel5.add(chordToneWeightField, gridBagConstraints);

    colorToneWeightField.setMinimumSize(new java.awt.Dimension(40, 24));
    colorToneWeightField.setPreferredSize(new java.awt.Dimension(40, 24));
    colorToneWeightField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        colorToneWeightFieldActionPerformed(evt);
      }
    });
    colorToneWeightField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        colorToneWeightFieldFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    jPanel5.add(colorToneWeightField, gridBagConstraints);

    scaleToneWeightField.setMinimumSize(new java.awt.Dimension(40, 24));
    scaleToneWeightField.setPreferredSize(new java.awt.Dimension(40, 24));
    scaleToneWeightField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        scaleToneWeightFieldActionPerformed(evt);
      }
    });
    scaleToneWeightField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        scaleToneWeightFieldFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    jPanel5.add(scaleToneWeightField, gridBagConstraints);

    chordToneDecayField.setToolTipText("Decrease chord tone probability by this amount for each tone.");
    chordToneDecayField.setMinimumSize(new java.awt.Dimension(40, 24));
    chordToneDecayField.setPreferredSize(new java.awt.Dimension(40, 24));
    chordToneDecayField.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        chordToneDecayFieldActionPerformed(evt);
      }
    });
    chordToneDecayField.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        chordToneDecayFieldFocusLost(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    jPanel5.add(chordToneDecayField, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
    jPanel6.add(jPanel5, gridBagConstraints);

    jPanel7.setLayout(new java.awt.GridBagLayout());

    scaleLabel.setText("Scale:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 20);
    jPanel7.add(scaleLabel, gridBagConstraints);

    typeLabel.setText("Type:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    jPanel7.add(typeLabel, gridBagConstraints);

    scaleComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        scaleComboBoxActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
    jPanel7.add(scaleComboBox, gridBagConstraints);

    rootLabel.setText("Root:");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
    jPanel7.add(rootLabel, gridBagConstraints);

    rootComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C", "C#/Db", "D", "D#/Eb", "E", "F", "F#", "Gb", "G", "G#/Ab", "A", "A#/Bb", "B" }));
    rootComboBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        rootComboBoxActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    jPanel7.add(rootComboBox, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
    jPanel6.add(jPanel7, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.gridwidth = 2;
    gridBagConstraints.weightx = 1.0;
    triageOptionsPanel.add(jPanel6, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridwidth = 4;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.weightx = 1.0;
    lickGenPanel.add(triageOptionsPanel, gridBagConstraints);

    FillProbsButton.setText("Fill");
    FillProbsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        FillProbsButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 2;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    lickGenPanel.add(FillProbsButton, gridBagConstraints);

    clearProbsButton.setText("Clear All");
    clearProbsButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        clearProbsButtonActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
    lickGenPanel.add(clearProbsButton, gridBagConstraints);

    autoFillCheckBox.setSelected(true);
    autoFillCheckBox.setText("Auto-fill Weights");
    autoFillCheckBox.setMargin(new java.awt.Insets(0, 0, 0, 0));
    autoFillCheckBox.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        autoFillCheckBoxActionPerformed(evt);
      }
    });
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 3;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.weightx = 1.0;
    lickGenPanel.add(autoFillCheckBox, gridBagConstraints);

    lickSavedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
    lickSavedLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    lickSavedLabel.setMinimumSize(new java.awt.Dimension(0, 200));
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.ipadx = 100;
    gridBagConstraints.weightx = 1.0;
    lickGenPanel.add(lickSavedLabel, gridBagConstraints);

    setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
    setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    setIconImage((new ImageIcon(getClass().getResource("/imp/gui/graphics/icons/trumpetsmall.png"))).getImage());
    setName("notateFrame"); // NOI18N
    addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(java.awt.event.FocusEvent evt) {
        formFocusGained(evt);
      }
    });
    addWindowFocusListener(new java.awt.event.WindowFocusListener() {
      public void windowGainedFocus(java.awt.event.WindowEvent evt) {
        formWindowGainedFocus(evt);
      }
      public void windowLostFocus(java.awt.event.WindowEvent evt) {
      }
    });
    addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowClosing(java.awt.event.WindowEvent evt) {
        formWindowClosing(evt);
      }
    });
    getContentPane().setLayout(new java.awt.GridBagLayout());

    toolbarPanel.setOpaque(false);
    toolbarPanel.setPreferredSize(new java.awt.Dimension(3002, 130));
    toolbarPanel.setLayout(new java.awt.GridBagLayout());

    standardToolbar.setFloatable(false);
    standardToolbar.setEnabled(false);
    standardToolbar.setMaximumSize(new java.awt.Dimension(400, 40));
    standardToolbar.setMinimumSize(new java.awt.Dimension(130, 40));
    standardToolbar.setPreferredSize(new java.awt.Dimension(130, 40));
    standardToolbar.addComponentListener(new java.awt.event.ComponentAdapter() {
      public void componentMoved(java.awt.event.ComponentEvent evt) {
        standardToolbarComponentMoved(evt);
      }
    });

    cutBothBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/cut.gif"))); // NOI18N
    cutBothBtn.setToolTipText("Cut the currently selected melody and chords (Ctrl+X).");
    cutBothBtn.setEnabled(false);
    cutBothBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    cutBothBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    cutBothBtn.setOpaque(false);
    cutBothBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    standardToolbar.add(cutBothBtn);

    copyBothBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/copy.gif"))); // NOI18N
    copyBothBtn.setToolTipText("Copy the currently selected melody and chords (Ctrl+C).");
    copyBothBtn.setEnabled(false);
    copyBothBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    copyBothBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    copyBothBtn.setOpaque(false);
    copyBothBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    standardToolbar.add(copyBothBtn);

    pasteBothBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/paste.gif"))); // NOI18N
    pasteBothBtn.setToolTipText("Paste melody and chords from the clipboard (Ctrl-V).");
    pasteBothBtn.setEnabled(false);
    pasteBothBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    pasteBothBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    pasteBothBtn.setOpaque(false);
    pasteBothBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    standardToolbar.add(pasteBothBtn);

    undoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/undo.gif"))); // NOI18N
    undoBtn.setToolTipText("Undo the previous action (Ctrl+Z)");
    undoBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    undoBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    undoBtn.setOpaque(false);
    undoBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    standardToolbar.add(undoBtn);

    redoBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/redo.gif"))); // NOI18N
    redoBtn.setToolTipText("Redo the previous action (Ctrl+Y)");
    redoBtn.setEnabled(false);
    redoBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    redoBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    redoBtn.setOpaque(false);
    redoBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    standardToolbar.add(redoBtn);
    standardToolbar.add(jSeparator25);

    addTabBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/addtab.gif"))); // NOI18N
    addTabBtn.setToolTipText("Add a new chorus tab.");
    addTabBtn.setMaximumSize(new java.awt.Dimension(30, 30));
    addTabBtn.setMinimumSize(new java.awt.Dimension(30, 30));
    addTabBtn.setOpaque(false);
    addTabBtn.setPreferredSize(new java.awt.Dimension(30, 30));
    addTabBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        addTabBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(addTabBtn);

    delTabBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/deltab.gif"))); // NOI18N
    delTabBtn.setToolTipText("Delete the current  chorus tab (can't be undone).");
    delTabBtn.setEnabled(false);
    delTabBtn.setMaximumSize(new java.awt.Dimension(30, 30));
    delTabBtn.setMinimumSize(new java.awt.Dimension(30, 30));
    delTabBtn.setOpaque(false);
    delTabBtn.setPreferredSize(new java.awt.Dimension(30, 30));
    standardToolbar.add(delTabBtn);

    helpBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/help.gif"))); // NOI18N
    helpBtn.setToolTipText("Open the Help dialog.");
    helpBtn.setMaximumSize(new java.awt.Dimension(30, 30));
    helpBtn.setMinimumSize(new java.awt.Dimension(30, 30));
    helpBtn.setOpaque(false);
    helpBtn.setPreferredSize(new java.awt.Dimension(30, 30));
    helpBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        helpBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(helpBtn);
    standardToolbar.add(jSeparator27);

    playBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
    playBtn.setToolTipText("Play the entire leadsheet.");
    playBtn.setFocusable(false);
    playBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    playBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    playBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    playBtn.setOpaque(false);
    playBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    playBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    playBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        playBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(playBtn);

    pauseBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pause.gif"))); // NOI18N
    pauseBtn.setToolTipText("Pause or resume playback.");
    pauseBtn.setEnabled(false);
    pauseBtn.setFocusable(false);
    pauseBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    pauseBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    pauseBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    pauseBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    pauseBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    pauseBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        pauseBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(pauseBtn);

    stopBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/stop.gif"))); // NOI18N
    stopBtn.setToolTipText("Stop playback.");
    stopBtn.setEnabled(false);
    stopBtn.setFocusable(false);
    stopBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    stopBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    stopBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    stopBtn.setOpaque(false);
    stopBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    stopBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    stopBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        stopBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(stopBtn);

    stepInputBtn.setFont(new java.awt.Font("Dialog", 0, 10));
    stepInputBtn.setText("Step");
    stepInputBtn.setToolTipText("Step record from MIDI source.");
    stepInputBtn.setFocusable(false);
    stepInputBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    stepInputBtn.setMaximumSize(new java.awt.Dimension(35, 32));
    stepInputBtn.setMinimumSize(new java.awt.Dimension(35, 32));
    stepInputBtn.setPreferredSize(new java.awt.Dimension(35, 32));
    stepInputBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    stepInputBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        stepInputBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(stepInputBtn);

    mixerBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/mixer.gif"))); // NOI18N
    mixerBtn.setToolTipText("Open Volume Mixer");
    mixerBtn.setFocusable(false);
    mixerBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    mixerBtn.setMaximumSize(new java.awt.Dimension(32, 32));
    mixerBtn.setMinimumSize(new java.awt.Dimension(32, 32));
    mixerBtn.setOpaque(false);
    mixerBtn.setPreferredSize(new java.awt.Dimension(32, 32));
    mixerBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    mixerBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        mixerBtnActionPerformed(evt);
      }
    });
    standardToolbar.add(mixerBtn);

    playbackPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Playback Location", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
    playbackPanel.setMaximumSize(new java.awt.Dimension(180, 50));
    playbackPanel.setMinimumSize(new java.awt.Dimension(120, 50));
    playbackPanel.setOpaque(false);
    playbackPanel.setPreferredSize(new java.awt.Dimension(180, 60));
    playbackPanel.setLayout(null);

    playbackTime.setText("0:00");
    playbackTime.setMaximumSize(new java.awt.Dimension(40, 22));
    playbackTime.setMinimumSize(new java.awt.Dimension(22, 22));
    playbackTime.setPreferredSize(new java.awt.Dimension(30, 22));
    playbackPanel.add(playbackTime);
    playbackTime.setBounds(10, 13, 30, 22);

    playbackTotalTime.setText("0:00");
    playbackTotalTime.setMaximumSize(new java.awt.Dimension(40, 22));
    playbackTotalTime.setMinimumSize(new java.awt.Dimension(22, 22));
    playbackTotalTime.setPreferredSize(new java.awt.Dimension(30, 22));
    playbackPanel.add(playbackTotalTime);
    playbackTotalTime.setBounds(140, 12, 30, 20);

    playbackSlider.setValue(0);
    playbackSlider.setMinimumSize(new java.awt.Dimension(80, 20));
    playbackSlider.setPreferredSize(new java.awt.Dimension(100, 20));
    playbackPanel.add(playbackSlider);
    playbackSlider.setBounds(38, 15, 100, 20);

    standardToolbar.add(playbackPanel);

    loopPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Looping", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
    loopPanel.setMaximumSize(new java.awt.Dimension(140, 50));
    loopPanel.setMinimumSize(new java.awt.Dimension(120, 50));
    loopPanel.setOpaque(false);
    loopPanel.setPreferredSize(new java.awt.Dimension(120, 63));
    loopPanel.setLayout(null);

    loopButton.setBackground(new java.awt.Color(0, 255, 0));
    loopButton.setFont(new java.awt.Font("Dialog", 1, 10));
    loopButton.setText("<html><center>Loop</center></html>");
    loopButton.setToolTipText("Toggle playback looping.");
    loopButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    loopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    loopButton.setMaximumSize(new java.awt.Dimension(30, 20));
    loopButton.setMinimumSize(new java.awt.Dimension(20, 20));
    loopButton.setPreferredSize(new java.awt.Dimension(30, 20));
    loopButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        loopButtonActionPerformed(evt);
      }
    });
    loopPanel.add(loopButton);
    loopButton.setBounds(10, 15, 60, 20);

    loopSet.setFont(new java.awt.Font("Dialog", 1, 12));
    loopSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    loopSet.setText("2");
    loopSet.setToolTipText("Loop  on playback specified number of times (0 means loop forever).");
    loopSet.setMinimumSize(new java.awt.Dimension(50, 20));
    loopSet.setPreferredSize(new java.awt.Dimension(50, 20));
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
    loopPanel.add(loopSet);
    loopSet.setBounds(80, 15, 30, 20);

    standardToolbar.add(loopPanel);

    masterVolumePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
    masterVolumePanel.setMaximumSize(new java.awt.Dimension(140, 50));
    masterVolumePanel.setMinimumSize(new java.awt.Dimension(100, 50));
    masterVolumePanel.setOpaque(false);
    masterVolumePanel.setPreferredSize(new java.awt.Dimension(120, 50));
    masterVolumePanel.setLayout(null);

    allMuteToolBarBtn.setBackground(new java.awt.Color(0, 255, 0));
    allMuteToolBarBtn.setFont(new java.awt.Font("Dialog", 1, 10));
    allMuteToolBarBtn.setText("<html><center>Mute</center></html>");
    allMuteToolBarBtn.setToolTipText("Play or not play notes as they are inserted?");
    allMuteToolBarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
    allMuteToolBarBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
    allMuteToolBarBtn.setMaximumSize(new java.awt.Dimension(30, 20));
    allMuteToolBarBtn.setMinimumSize(new java.awt.Dimension(30, 20));
    allMuteToolBarBtn.setPreferredSize(new java.awt.Dimension(30, 20));
    allMuteToolBarBtn.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        allMuteToolBarBtnActionPerformed(evt);
      }
    });
    masterVolumePanel.add(allMuteToolBarBtn);
    allMuteToolBarBtn.setBounds(10, 15, 30, 20);

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
    masterVolumePanel.add(allVolumeToolBarSlider);
    allVolumeToolBarSlider.setBounds(40, 15, 70, 20);

    standardToolbar.add(masterVolumePanel);

    tempoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tempo", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
    tempoPanel.setMaximumSize(new java.awt.Dimension(228, 63));
    tempoPanel.setMinimumSize(new java.awt.Dimension(160, 63));
    tempoPanel.setOpaque(false);
    tempoPanel.setPreferredSize(new java.awt.Dimension(200, 63));
    tempoPanel.setLayout(null);

    tempoSet.setFont(new java.awt.Font("Dialog", 1, 12));
    tempoSet.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
    tempoSet.setToolTipText("Set the tempo for the sheet in beats per minute.");
    tempoSet.setMaximumSize(new java.awt.Dimension(60, 20));
    tempoSet.setMinimumSize(new java.awt.Dimension(20, 20));
    tempoSet.setPreferredSize(new java.awt.Dimension(50, 20));
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
    tempoSet.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyTyped(java.awt.event.KeyEvent evt) {
        tempoSetKeyTyped(evt);
      }
      public void keyPressed(java.awt.event.KeyEvent evt) {
        tempoSetKeyPressed(evt);
      }
      public void keyReleased(java.awt.event.KeyEvent evt) {
        tempoSetKeyReleased(evt);
      }
    });
    tempoSet.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mousePressed(java.awt.event.MouseEvent evt) {
        tempoSetMousePressed(evt);
      }
    });
    tempoPanel.add(tempoSet);
    tempoSet.setBounds(10, 15, 50, 20);

    BPM.setText("BPM");
    BPM.setPreferredSize(new java.awt.Dimension(30, 14));
    tempoPanel.add(BPM);
    BPM.setBounds(65, 18, 30, 14);

    tempoSlider.setMaximum(300);
    tempoSlider.setMinimum(30);
    tempoSlider.setMinorTickSpacing(4);
    tempoSlider.setValue(120);
    tempoSlider.setMaximumSize(new java.awt.Dimension(120, 30));
    tempoSlider.setMinimumSize(new java.awt.Dimension(36, 20));
    tempoSlider.setPreferredSize(new java.awt.Dimension(100, 20));
    tempoSlider.addChangeListener(new javax.swing.event.ChangeListener() {
      public void stateChanged(javax.swing.event.ChangeEvent evt) {
        tempoSliderStateChanged(evt);
      }
    });
    tempoPanel.add(tempoSlider);
    tempoSlider.setBounds(95, 15, 100, 20);

    standardToolbar.add(tempoPanel);

    playTransposed.setBackground(new java.awt.Color(238, 238, 238));
    playTransposed.setFont(new java.awt.Font("Dialog", 1, 12));
    playTransposed.setHorizontalAlignment(javax.swing.JTextField.CENTER);
    playTransposed.setText("0");
    playTransposed.setToolTipText("Transpose playback by this number of semitones.");
    playTransposed.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transpose", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 0, 10))); // NOI18N
    playTransposed.setMargin(new java.awt.Insets(0, 5, 2, 4));
    playTransposed.setMaximumSize(new java.awt.Dimension(60, 50));
    playTransposed.setMinimumSize(new java.awt.Dimension(60, 50));
    playTransposed.setOpaque(false);
    playTransposed.setPreferredSize(new java.awt.Dimension(60, 50));
    playTransposed.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        playTransposedActionPerformed(evt);
      }
    });
    playTransposed.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(java.awt.event.FocusEvent evt) {
        playTransposedFocusLost(evt);
      }
    });
    standardToolbar.add(playTransposed);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
    gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
    gridBagConstraints.weightx = 1.0;
    toolbarPanel.add(standardToolbar, gridBagConstraints);

    textEntryToolBar.setFloatable(false);
    textEntryToolBar.setToolTipText("Enter chords and melody via this text entry window");
    textEntryToolBar.setMinimumSize(new java.awt.Dimension(117, 40));
    textEntryToolBar.setPreferredSize(new java.awt.Dimension(1050, 40));

    textEntryLabel.setText("Textual Entry ");
    textEntryToolBar.add(textEntryLabel);

    textEntry.setFont(new java.awt.Font("Dialog", 0, 14));
    textEntry.setToolTipText("Enter chords or melody in leadsheet notation.");
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
    clearButton.setMaximumSize(new java.awt.Dimension(46, 40));
    clearButton.setMinimumSize(new java.awt.Dimension(46, 40));
    clearButton.setPreferredSize(new java.awt.Dimension(46, 40));
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
    editMenu.add(jSeparator15);

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
    enterMelodyMI.setText("Enter Melody");
    enterMelodyMI.setToolTipText("Enter melody currently in the text entry window.");
    enterMelodyMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        enterMelodyMIActionPerformed(evt);
      }
    });
    editMenu.add(enterMelodyMI);

    enterChordsMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.SHIFT_MASK));
    enterChordsMI.setText("Enter Chords");
    enterChordsMI.setToolTipText("Enter chords currently in text entry window.");
    enterChordsMI.setActionCommand("Enter Chords2");
    enterChordsMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        enterChordsMIActionPerformed(evt);
      }
    });
    editMenu.add(enterChordsMI);

    enterBothMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_B, java.awt.event.InputEvent.CTRL_MASK));
    enterBothMI.setText("Enter Melody and Chords");
    enterBothMI.setToolTipText("Enter chords and melody currently in the text entry window.");
    enterBothMI.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        enterBothMIActionPerformed(evt);
      }
    });
    editMenu.add(enterBothMI);
    editMenu.add(jSeparator13);

    transposeMelodyUpSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
    transposeMelodyUpSemitone.setText("Transpose Melody Up Semitone");
    transposeMelodyUpSemitone.setToolTipText("Transpose the selected melody up one half-step.");
    transposeMelodyUpSemitone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeMelodyUpSemitoneActionPerformed(evt);
      }
    });
    editMenu.add(transposeMelodyUpSemitone);

    transposeChordsUpSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.SHIFT_MASK));
    transposeChordsUpSemitone.setText("Transpose Chords Up Semitone");
    transposeChordsUpSemitone.setToolTipText("Transpose the selected chords up one half-step.");
    transposeChordsUpSemitone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeChordsUpSemitoneActionPerformed(evt);
      }
    });
    editMenu.add(transposeChordsUpSemitone);

    transposeBothUpSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, java.awt.event.InputEvent.CTRL_MASK));
    transposeBothUpSemitone.setText("Transpose Both Up Semitone");
    transposeBothUpSemitone.setToolTipText("Transpose the selected melody and chords up one half-step.");
    transposeBothUpSemitone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeBothUpSemitoneActionPerformed(evt);
      }
    });
    editMenu.add(transposeBothUpSemitone);

    transposeMelodyDownSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
    transposeMelodyDownSemitone.setText("Transpose Melody Down Semitone");
    transposeMelodyDownSemitone.setToolTipText("Transpose the selected melody down one half-step.");
    transposeMelodyDownSemitone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeMelodyDownSemitoneActionPerformed(evt);
      }
    });
    editMenu.add(transposeMelodyDownSemitone);

    transposeChordsDownSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.SHIFT_MASK));
    transposeChordsDownSemitone.setText("Transpose Chords Down Semitone");
    transposeChordsDownSemitone.setToolTipText("Transpose the selected chords down one half-step.");
    transposeChordsDownSemitone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeChordsDownSemitoneActionPerformed(evt);
      }
    });
    editMenu.add(transposeChordsDownSemitone);

    transposeBothDownSemitone.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_MASK));
    transposeBothDownSemitone.setText("Transpose Both Down Semitone");
    transposeBothDownSemitone.setToolTipText("Transpose the selected melody and chords down one half-step.");
    transposeBothDownSemitone.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeBothDownSemitoneActionPerformed(evt);
      }
    });
    editMenu.add(transposeBothDownSemitone);

    transposeMelodyUpOctave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_T, 0));
    transposeMelodyUpOctave.setText("Transpose Melody Up Octave");
    transposeMelodyUpOctave.setToolTipText("Transpose the selected melody up one octave.");
    transposeMelodyUpOctave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeMelodyUpOctaveActionPerformed(evt);
      }
    });
    editMenu.add(transposeMelodyUpOctave);

    transposeMelodyDownOctave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_G, 0));
    transposeMelodyDownOctave.setText("Transpose Melody Down Octave");
    transposeMelodyDownOctave.setToolTipText("Transpose the selected melody down one octave.");
    transposeMelodyDownOctave.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(java.awt.event.ActionEvent evt) {
        transposeMelodyDownOctaveActionPerformed(evt);
      }
    });
    editMenu.add(transposeMelodyDownOctave);
    editMenu.add(jSeparator19);

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

    menuBar.add(editMenu);

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
    viewMenu.add(jSeparator4);

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

    menuBar.add(playMenu);

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
    helpMenu.add(helpMI);

    menuBar.add(helpMenu);

    setJMenuBar(menuBar);

    pack();
  }// </editor-fold>//GEN-END:initComponents


  private void mouseEnteredTabPanel(java.awt.event.MouseEvent evt)//GEN-FIRST:event_mouseEnteredTabPanel
  {//GEN-HEADEREND:event_mouseEnteredTabPanel
  staveRequestFocus();
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
  
  }//GEN-LAST:event_loopSetActionPerformed

  private void loopButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_loopButtonActionPerformed
  {//GEN-HEADEREND:event_loopButtonActionPerformed
    setToLoop(loopButton.isSelected());
    if( getToLoop() )
    {
      loopButton.setText("<html><center>Straight</center></html>");
      loopButton.setBackground(Color.RED);
    }
    else
    {
      loopButton.setText("<html><center>Loop</center></html>");
      loopButton.setBackground(Color.GREEN);
    }
 
  }//GEN-LAST:event_loopButtonActionPerformed
                                    
    
    
    private boolean initLocationStyleEditor = false;
        
    
    
    public void reloadStyles() {
        
        reCaptureCurrentStyle();
        
        styleListModel.reset();
        
        sectionListModel.reset();
        
    }
    
    public void reCaptureCurrentStyle()
    {
    score.getChordProg().getSectionInfo().reloadStyles();
     }
    
    
    
    private void helpBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpBtnActionPerformed
        
        openHelpDialog();
        
    }//GEN-LAST:event_helpBtnActionPerformed
    
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
    
    private String tempoSetOldTempo;
    
    private void tempoSetFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tempoSetFocusGained
        
        tempoSetOldTempo = tempoSet.getText();
        
    }//GEN-LAST:event_tempoSetFocusGained
    
    
    
    private void tempoSetKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tempoSetKeyReleased
        
        updateTempoFromTextField();
        
    }//GEN-LAST:event_tempoSetKeyReleased
    
    
    
    private void tempoSetKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tempoSetKeyTyped
        
        updateTempoFromTextField();
        
    }//GEN-LAST:event_tempoSetKeyTyped
    
    
        
    
        
    
        
    
        
    
    
    
        
    
    
    
    
    private void stepInputBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stepInputBtnActionPerformed
        
        setStepInput(stepInputBtn.isSelected());
        
    }//GEN-LAST:event_stepInputBtnActionPerformed
    
    
    
    private void addRestMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRestMIActionPerformed
        
        addRest();
        
    }//GEN-LAST:event_addRestMIActionPerformed
    
    
        
    
        
    
        
    
        
    
    
    private void showEmptyTitlesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showEmptyTitlesMIActionPerformed
        
        for (int i=0; i<staveScrollPane.length; i++) {
          
            MiniStave stave = staveScrollPane[i].getStave();
            
            stave.setShowEmptyTitles(showEmptyTitlesMI.isSelected());
            
            stave.repaint();
        }        
    }//GEN-LAST:event_showEmptyTitlesMIActionPerformed
        
    private void pauseBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseBtnActionPerformed
        
        pauseScore();
        
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
        
        
        
        tempoSet.setText(String.valueOf(value));
        
        setTempo((double) value);
        
        if(!tempoSlider.getValueIsAdjusting()) {
            
            staveRequestFocus();
            
        }
        
    }//GEN-LAST:event_tempoSliderStateChanged
    
  
    
    private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMIActionPerformed
        
        closeWindow();
        
    }//GEN-LAST:event_closeWindowMIActionPerformed
    
    
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

    void stopPlaying() {
        
        midiSynth.stop();
        
        
    }
    
    
    
    private void setStepInput(boolean active) {
        
        stepInputActive = active;
        
        if(active) {
            
            midiSynth.registerReceiver(midiStepInput);
            
        } else {
            
            midiSynth.unregisterReceiver(midiStepInput);
            
        }
        
    }
    
    
        
    
    
    private void playBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playBtnActionPerformed
        
        playScore();
        
    }//GEN-LAST:event_playBtnActionPerformed
    
    
    
    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_windowMenuMenuSelected
        
        windowMenu.removeAll();
        
        windowMenu.add(closeWindowMI);
        
        windowMenu.add(windowMenuSeparator);
        
        
        windowMenu.repaint();
        
    }//GEN-LAST:event_windowMenuMenuSelected
    
    
        
    
        
    
        
    
        
    
        
    
        
    
    
    private void chordToneWeightFieldFocusLost(java.awt.event.FocusEvent evt)
    
    {
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
        
    }
    
    
        
    
        
    
        
    
        
    
        
    
        
    
    
    private void overrideFrameadviceWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_overrideFrameadviceWindowClosing

    {//GEN-HEADEREND:event_overrideFrameadviceWindowClosing
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_overrideFrameadviceWindowClosing
    
    
    
    private void overrideFrameadviceFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_overrideFrameadviceFocusGained

    {//GEN-HEADEREND:event_overrideFrameadviceFocusGained
        
// TODO add your handling code here:
        
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
    
    
    
    private void chordVolumeChanged() {
        
        
        
        // set flag to for unsaved changes
        
        cm.changedSinceLastSave(true);
        
        
        
        int v = chordVolume.getValue();
        
        score.setChordVolume(v);
        
        score.setChordMuted(chordMute.isSelected());
        
        chordVolume.setEnabled(!chordMute.isSelected());
        
        Style style = score.getChordProg().getStyle();
        
        if( style != null )
          {
          if(score.getChordMuted())
              midiSynth.setChannelVolume(style.getChordChannel(), 0);
          else
            midiSynth.setChannelVolume(style.getChordChannel(), v);
          }        
    }
    
    
    
    private void setMuteAll(boolean muted) {
        
        score.setMasterVolumeMuted(muted);
        
        if(muted) {
            
            allMuteMixerBtn.setSelected(true);
            
            allMuteToolBarBtn.setSelected(true);
            
        } else {
            
            allMuteMixerBtn.setSelected(false);
            
            allMuteToolBarBtn.setSelected(false);
            
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
        
        
/*        
        if(!allVolumeMixerSlider.getValueIsAdjusting()) {
            
            staveRequestFocus();
        }
*/            
        
    }//GEN-LAST:event_allVolumeToolBarSliderStateChanged
    
    
    
    private void entryMuteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_entryMuteActionPerformed
        
        entryVolume.setEnabled(!entryMute.isSelected());
        

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
                
                
                break;
                
        }
        
    }
    
    
    
    void changePrefTab(JToggleButton button, JPanel tab) {
        
        button.setSelected(true);
        
        currentPrefButton = button;
        
        
        
        if(currentPrefTab == tab) {
            
            return;
            
        }
        
        currentPrefTab = tab;
        
        
    }
    
    void changeGenTab(JToggleButton button, JPanel tab) {
        currentGenButton = button;
        
        if(currentGenTab == tab) {
            return;
        }
        
        currentGenTab = tab;
    }

    
    private void devicesChanged() {
        
        refreshMidiStatus();
        
    }
    
    
    
    String okMsg = "<html>Status: <em><font color='green'>Device ready</font></em></html>";
    
    String noDevSelectedMsg = "<html>Status: <em><font color='red'>No Device Selected</font></em></html>";
    
    String noDev = "<html>No devices found.</html>";
    
    String failMsgStart = "<html>Status: <em><font color='red'>";
    
    String failMsgEnd = "</font></em></html>";
    
    public void refreshMidiStatus() {
        
    }

        
    public class StyleListModel extends AbstractListModel {
        private Polylist styles = null;
        
        public int getSize() {
            styles = Advisor.getAllStyles();
            if(styles == null)
                return 0;
            return styles.length();
            
        }
        
        
        
        public Object getElementAt(int index) {
            
            return (Style)((Polylist)styles.nth(index)).second();
            
        }
        
        
        
        public void reset() {
            
            fireContentsChanged(this,0,getSize());
            
        }
        
        
        
    }
    
    
    
    public class SectionListModel extends AbstractListModel {
        
        public int getSize() {
            
            /**
             *
             * Stephen TODO: null pointer exception raised here
             *
             */
            
            if(sectionInfo == null)
                
                return 0;
            
            return sectionInfo.size();
            
        }
        
        
        
        public Object getElementAt(int index) {
            
            return sectionInfo.getInfo(index);
            
        }
        
        
        
        public void reset() {
            
            sectionInfo = score.getChordProg().getSectionInfo().copy();
            
            refresh();
            
        }
        
        
        
        public void refresh() {
            
            fireContentsChanged(this,0,sectionInfo.size());
/* rk            
            int index = sectionList.getSelectedIndex();
            
            if(index > -1)  {
                
                styleList.setSelectedValue(sectionInfo.getStyle(index),true);
                
                measureTF.setText(String.valueOf(sectionInfo.getSectionMeasure(index)));
                
            }
            
            delSectionButton.setEnabled(sectionInfo.size() > 1);
            
            setNewSectionEnabled();
*/            
        }
        
    }
    
    
    
    public class StyleComboBoxModel extends AbstractListModel implements ComboBoxModel {
        
        Polylist styles; 
        int len;

        public int getSize() {
            styles = Advisor.getAllStyles();
            
            if(styles == null)
                return 0;
            
            len = styles.length();

            return len;
        }
        
        
        public Object getElementAt(int index) {
            
            return (Style)((Polylist)styles.nth(index)).second();
        }
        
        private Object selectedItem = null;
        

        public void setSelectedItem(Object anItem) {
            
            selectedItem = anItem;
        }

        
        public Object getSelectedItem() {
            
            return selectedItem;
            
        }
        
    }
    
    
    
    public class MidiDeviceChooser extends AbstractListModel implements ComboBoxModel {
        
        private Vector<MidiDevice.Info> devices;
        
        private Object selectedItem = null;
        
        
        
        public MidiDeviceChooser(Vector<MidiDevice.Info> devices) {
            
            this.devices = devices;
            
        }
        
        
        
        public int getSize() {
            
            return devices.size();
            
        }
        
        
        
        public Object getElementAt(int index) {
            
            Object o = devices.elementAt(index);
            
            if(o == null) {
                
                return midiManager.defaultDeviceLabel;
                
            }
            
            return o;
            
        }
        
        
        
        public void setSelectedItem(Object anItem) {
            
            selectedItem = anItem;
            
        }
        
        
        
        public Object getSelectedItem() {
            
            return selectedItem;
            
        }
        
    }
    
    private void stopBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopBtnActionPerformed
        
        stopPlaying();
        
    }//GEN-LAST:event_stopBtnActionPerformed
    
    
    
    private void setTempo(double value) {
        
        if(value > 0) {
            
            score.setTempo(value);
            
            midiSynth.setTempo((float) value);
            
        } else if (Double.valueOf(tempoSet.getText()).doubleValue() < 0) {
            
            ErrorLog.log(ErrorLog.COMMENT, "The tempo cannot be below 0");
            
            return;
            
        }
        
    }
    
    
    
    private void tempoSetFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_tempoSetFocusLost
        
        updateTempoFromTextField();
        
        staveRequestFocus();
        
    }//GEN-LAST:event_tempoSetFocusLost
    
    
    
    private void tempoSetKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tempoSetKeyPressed
        
        if(evt.getKeyCode() == KeyEvent.VK_ENTER) {
            
            staveRequestFocus();
            
        } else if(evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            
            tempoSet.setText(tempoSetOldTempo);
            
            staveRequestFocus();
            
        }
        
        updateTempoFromTextField();
        
    }//GEN-LAST:event_tempoSetKeyPressed
    
    
    
    private void updateTempoFromTextField() {
        
        try {
            
            double value = Double.valueOf(tempoSet.getText()).doubleValue();
            
            int sliderValue = (int) Math.round(value);
            
            //if(sliderValue > tempoSlider.getMinimum() && sliderValue < tempoSlider.getMaximum()) {
            
            jSliderIgnoreStateChangedEvt = true;
            
            tempoSlider.setValue((int) sliderValue);
            
            jSliderIgnoreStateChangedEvt = false;
            
             setTempo(value);
            
            //}
            
        } catch (NumberFormatException e) {
            
            tempoSet.setForeground(Color.RED);
            
            return;
            
        }
        
        tempoSet.setForeground(Color.BLACK);
        
    }
    
    
    
    private void tempoSetMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tempoSetMousePressed
        
        tempoSet.requestFocusInWindow();
        
    }//GEN-LAST:event_tempoSetMousePressed
    
    
       private void updateLoopFromTextField() {
        
        try {
            
            int value = Integer.valueOf(loopSet.getText()).intValue();
            
            setLoopCount(value);
            
            //}
            
        } catch (NumberFormatException e) {
            
            loopSet.setForeground(Color.RED);
            
            return;
            
        }
        
        tempoSet.setForeground(Color.BLACK);
        
    }

    
    private void standardToolbarComponentMoved(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_standardToolbarComponentMoved
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_standardToolbarComponentMoved

        
    private Color adviceBtnColorOpen = Color.green;
    
    private Color adviceBtnColorClosed = new Color(238, 212, 212);
        
    
    
    private void formWindowGainedFocus(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowGainedFocus

        
        checkFakeModalDialog();
        
    }//GEN-LAST:event_formWindowGainedFocus
    
    
    
    private void insertRestMeasureActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_insertRestMeasureActionPerformed
        
        Part singleMeasure = new Part(measureLength);
        
        int measureStart = (getCurrentSelectionStart() / measureLength) * measureLength;
        
    }//GEN-LAST:event_insertRestMeasureActionPerformed
    
    
        
    
    
    
    
    private void advicePMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_advicePMIActionPerformed
        
        
    }//GEN-LAST:event_advicePMIActionPerformed
    
    
    
    private void playMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playMenuActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_playMenuActionPerformed
    
    
        
    
    
    
        
    
        
    
        
    
    
    
    
    
    
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
    
    private void enterMeasuresActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterMeasuresActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_enterMeasuresActionPerformed
    
    
        
    
    
    private void enterLickTitleAction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_enterLickTitleAction
        
        
    }//GEN-LAST:event_enterLickTitleAction
    
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
        
    
    
    private void enterMeasuresKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_enterMeasuresKeyPressed
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            
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
    
    
        
    
        
    
        
    
    
    private void copyChordSelectionToTextWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordSelectionToTextWindowActionPerformed
        
        
        staveRequestFocus();
        
    }//GEN-LAST:event_copyChordSelectionToTextWindowActionPerformed
    
    
    
    private void copyMelodySelectionToTextWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMelodySelectionToTextWindowActionPerformed

        
        staveRequestFocus();
        
    }//GEN-LAST:event_copyMelodySelectionToTextWindowActionPerformed
    
    
    
    private void copyBothSelectionToTextWindowActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyBothSelectionToTextWindowActionPerformed
  
        staveRequestFocus();
        
    }//GEN-LAST:event_copyBothSelectionToTextWindowActionPerformed
    
    
    
    private void saveSelectionAsLickActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSelectionAsLickActionPerformed
        
        
    }//GEN-LAST:event_saveSelectionAsLickActionPerformed

    
    // Return an array of labels that have appropriate enharmonics
    
    // for the black notes.
    
    private String[] getNoteLabels(int location)
    
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
    
    
    
    // Make sure that the values in the probability fields are between 0.0 and 1.0
    
    private void verifyProbs()
    
    {
 /*       
        for (int i = 0; i < lickPrefs.size(); ++i)
            
            for (int j = 0; j < 12; ++j)
                
                doubleFromTextField(lickPrefs.get(i)[j], 0.0, Double.POSITIVE_INFINITY, 1.0);
 */       
    }
    
    
    
    
    boolean isPowerOf2(int x)
    
    {
        // trust me, it works!
        return ((x > 0) && ((x & (x - 1)) == 0));
    }

        
    private void errorDialogKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_errorDialogKeyPressed
        
        if( evt.getKeyCode() == KeyEvent.VK_ENTER )
            
            okErrorBtnActionPerformed(null);
        
    }//GEN-LAST:event_errorDialogKeyPressed
    
    
    
    private void textEntryLosesFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textEntryLosesFocus
        
        textEntryLabel.setForeground(Color.red);
        
    }//GEN-LAST:event_textEntryLosesFocus
    
    
    
    private void textEntryGainsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_textEntryGainsFocus
        
        
    }//GEN-LAST:event_textEntryGainsFocus
    
    
    
    private void clearButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearButtonActionPerformed
        
        textEntry.setText("");
        
    }//GEN-LAST:event_clearButtonActionPerformed
    
    
    
    private void playTransposedFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_playTransposedFocusLost
        
        setPlayTransposed();
        
    }//GEN-LAST:event_playTransposedFocusLost
    
    
    
    private void playTransposedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playTransposedActionPerformed
        
        setPlayTransposed();
        
    }//GEN-LAST:event_playTransposedActionPerformed
    
    
    
  private void setPlayTransposed()
    {
    }
    
    
    
    private void tempoSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tempoSetActionPerformed
        
                    }//GEN-LAST:event_tempoSetActionPerformed
    
    
    
    public void closeAdviceFrame()
    
    {

        
        staveRequestFocus();
        
    }
    
    
    
    private void openAdviceFrame()
    
    {
   
        
    }
    
    
    
    private void redoAdvice()
    
    {
        
        Trace.log(2, "redo advice");
        
        getCurrentStaveActionHandler().redoAdvice(getCurrentSelectionStart());
        
        getCurrentStave().repaint();
        
    }
    
    
    
    public void redoScales()
    
    {
        
        DefaultComboBoxModel dcbm = (DefaultComboBoxModel)scaleComboBox.getModel();
        
        dcbm.removeAllElements();
        
        Polylist scales = Advisor.getAllScales();
        
        dcbm.addElement("None");
        
        dcbm.addElement("Use First Scale");
        
        while (scales.nonEmpty())
            
        {
            
            Polylist scale = (Polylist)scales.first();
            
            dcbm.addElement(scale.first());
            
            scales = scales.rest();
            
        }
        
    }
    
    
    
    public void updateSelection() {
        
        if (getCurrentStave() == null)
            
            return;
        
    }
    
    
    
    private void staveMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staveMenuActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_staveMenuActionPerformed
    
    
    
    private void staveTypeMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_staveTypeMenuActionPerformed
        
// TODO add your handling code here:
        
    }//GEN-LAST:event_staveTypeMenuActionPerformed
    
    
    
    public void transposeMelodyUpOctaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeMelodyUpOctaveActionPerformed
        
        getCurrentStave().transposeMelodyUpOctave();
        
    }//GEN-LAST:event_transposeMelodyUpOctaveActionPerformed
    
    
    
    private void textEntryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textEntryActionPerformed
        
        staveRequestFocus();
        
    }//GEN-LAST:event_textEntryActionPerformed
    
    
        
    
    
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
        
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            
            if( slotIsSelected() ) {
                
                String enteredText = textEntry.getText();
                
                
                
                MiniStave stave = getCurrentStave();
                
                
                
                if (enteredText.length() > 0) {
                    
                    cm.execute(
                            
                            new SetChordsCommand(getCurrentSelectionStart(),
                            
                            parseListFromString(enteredText),
                            
                            chordProg,
                            
                            partArray[currTabIndex]) );
                    
                    
                    
                    redoAdvice();
                    
                }
                
                else {
                    
                    cm.execute(
                            
                            new DeleteUnitsCommand(chordProg,
                            
                            getCurrentSelectionStart(),
                            
                            getCurrentSelectionEnd()));
                    
                    redoAdvice();
                    
                }
                
                // set the menu and button states
                
                setItemStates();
                
                if( evt.isShiftDown() ) {
                    
                    // If shift is down when entry pressed, shift focus to stave.
                    
                    staveRequestFocus();
                    
                }
                
            } else {
                
                ErrorLog.log(ErrorLog.COMMENT, "Text entry has no effect unless a unique grid line is selected.");
                
            }
            
        } else {
            
            disableAccelerators();        // to prevent keys from triggering
            
        }
        
    }//GEN-LAST:event_textEntryKeyPressed

    
    private void exportToMidi(int toExport) {

            if (savedLeadsheet != null) {
                String name = savedLeadsheet.getName();
                if (name.endsWith(leadsheetExt))
                    name = name.substring(0, name.length() - 3);
                midfc.setSelectedFile(new File(name + midiExt));
            } else
                midfc.setSelectedFile(new File(midDef));
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
    
    
    
    /**
     *
     * Resizes the toolBarPanel so that all toolbars can be shown
     *
     */
    
    
    
    /**
     *
     * Resizes the toolBarPanel so that all toolbars can be shown
     *
     */
    
    
    
    /**
     *
     * Resizes the toolBarPanel so that all toolbars can be shown
     *
     */
        
    
    
    
    
    /**
     *
     * Sets the chord volume
     *
     */
    
    
    
    /**
     *
     * Sets the part volume
     *
     */
    
    
    
    static private void invalidInteger(String text) {
        
        text = text.trim();
        
        if( !text.equals("") ) {
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "Invalid integer entered: '"
                    
                    + text + "'.");
            
        }
        
    }
    
    /**
     * Get the current Stave.
     */

    public MiniStave getCurrentStave()
    {
    return staveScrollPane[currTabIndex].getStave();

    }

  /**
   * Give focus to the current Stave.
   */
  void staveRequestFocus()
    {

    // Show that textEntry no longer has focus if it had.

    Trace.log(2, "focus to stave");

    textEntryLabel.setForeground(Color.red);

    getCurrentStave().requestFocusInWindow();

    setItemStates();
    }

    
    
    /**
     * Get the ActionHandler for the current Stave.
     */
    
  MiniStaveActionHandler getCurrentStaveActionHandler()
    {
    return getCurrentStave().getActionHandler();
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
  int getCurrentSelectionEnd()
    {
   return getCurrentStave().getSelectionEnd();
    }
    
  
    /**
     * Set the current Selection start.
     */
    
    void setCurrentSelectionStart(int index) {
        
        getCurrentStave().setSelectionStart(index);
        
        getCurrentStaveActionHandler().redoAdvice(index);
        
    }
    
    
    
    /**
     *
     * Set the current Selection end.
     *
     */
    
    
    
    void setCurrentSelectionEnd(int index) {
        
        getCurrentStave().setSelectionEnd(index);
        
    }
    
    
    
    /**
     *
     * Indicate whether a slot is current selected or not.
     *
     */
    
    
    
    private boolean slotIsSelected() {
        
        return getCurrentStave().somethingSelected();
        
    }
    
    
    
    /**
     *
     * Indicate whether a unique slot is current selected or not.
     *
     */
    
    
    
    private boolean oneSlotSelected() {
        
        return getCurrentStave().oneSlotSelected();
        
    }
    
    
    
    /**
     *
     * Get the current Selection end.
     *
     */
    
    
    
    public MelodyPart getCurrentOrigPart() {
        
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
         
        MiniStave stave = getCurrentStave();
        
        stave.setSelectionStart(0);
        
        stave.setSelectionEnd(stave.getOrigPart().size() - 1);
         
        redoAdvice();
        
        staveRequestFocus();
   }
    
    
    /**
     *
     * Copies the selection of chords from the Stave to the clipboard
     *
     */
    
    public void copyChordsMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordsMIActionPerformed
        
        copyChords();
        
    }//GEN-LAST:event_copyChordsMIActionPerformed
    
    
    
    void copyChords() {
        
        Trace.log(2, "copy chords");
        
        if ( slotIsSelected() ) {
            
            MiniStave stave = getCurrentStave();
            
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
    
    
    
    
    
    void cutChords() {
        
        Trace.log(2, "cut chords");
        
        if ( slotIsSelected() ) {
            
            MiniStave stave = getCurrentStave();
            
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
    
    
    
    public void closeWindow() {
        
        boolean redisplay = true;
        
        
        
        while(redisplay) {
            
            redisplay = false;
            
            
            
            if(unsavedChanges()) {
                
                Object[] options = {"<html><b><u>Y</u>es</b>, save modifications.</html>", "<html><b><u>N</u>o</b>, do not save modifications.</html>", "<html><b>Cancel</b>, do not close this leadsheet.</html>"};
                
                UnsavedChanges dialog = new UnsavedChanges(this, "Save changes before closing?", options);
                
                dialog.setVisible(true);
                
                dialog.dispose();
                
                UnsavedChanges.Value choice = dialog.getValue();
                
                
                
                switch(choice) {
                    
                    case YES:
                        
                            
                            redisplay = true;
                        
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
        
        helpDialog.dispose();
        
        errorDialog.dispose();
        
        mixerDialog.dispose();
        
     }
    
    
    
    /**
     *
     * Shows the error dialog centered relative to the current window
     *
     */
    
    private void errorDialogComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_errorDialogComponentShown
        
        errorDialog.setLocationRelativeTo(this);
        
    }//GEN-LAST:event_errorDialogComponentShown
    
    
    
    
    
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
     * Opens a dialog allowing the user to delete the current tabbed part.
     *
     */
    
    private void delTabMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delTabMIActionPerformed
        
        // initialize the option pane
        
        Object[] options = {"Yes", "No"};
        
        int choice = JOptionPane.showOptionDialog(this,
                
                "Do you wish to delete the tabbed part, " +
                
                getCurrentStave().getPartTitle() + "?",
                
                "Delete Current Tab?", JOptionPane.YES_NO_OPTION,
                
                JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        
        
        
        // the user selected yes
        
        if (choice == 0 && currTabIndex >= 0
                
                && scoreTab.getTabCount() > 1) {
            
            
            
            score.delPart(currTabIndex);
            
            setupArrays();
            
            
            
            // set the menu and button states
            
            setItemStates();
            
        }
        
    }//GEN-LAST:event_delTabMIActionPerformed
    
    
    
    
    
    public void setMidiLatency(double latency) {
        
        
    }
    
    
    
    /**
     *
     * Get integer value from string.
     *
     */
    
    
    
    static int intFromString(String string) throws NumberFormatException {
        
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
        
        field.setText("" + value);
        
        return value;
        
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
    
    static double doubleFromStringInRange(String string, double low, double high, double error) {
        
        try {
            
            double value = doubleFromString(string);
            
            if( value >= low &&  value <= high ) {
                
                return value;
                
            }
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "The number you have entered is out of range: must be between "
                    
                    + low + " and " + high);
            
            
            
            return error; // range error indicator
            
        } catch (NumberFormatException e) {
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "Invalid double entered: '"
                    
                    + string + "'.");
            
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
        
        return doubleFromStringInRange(field.getText(), low, high, error);
        
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
            
            scoreTab.setSelectedIndex(tempTabIndex);
            
            setCurrentScrollPosition(tempView);
            
            
            
            staveRequestFocus();
            
        }
        
    }
    
    
    
    private boolean saveMidi() {
        
        
         return true;
        
    }
    
    
    
    private boolean saveSectionInfo() {
        
        score.getChordProg().setSectionInfo(sectionInfo.copy());
        
        return true;
        
    }
    
    
    
    private boolean setSectionPrefs() {
        
         return true;
        
    }
    
    
    
    private boolean saveStylePrefs() {
        
        return true;
        
    }
    
    
    
    public void updateStyle() {
     }
    
    
    
    private boolean saveMetre() {
        
        
        
        // Here we set up the metre and length of the leadsheet.  This needs to be separated out
        
        // because we handle things differently depending on whether we're opening a new leadsheet
        
        // or just changing the current leadsheet.
        
        try {
            
            int timeSignatureTop = (4); // FIX: get from parent object
            
            int timeSignatureBottom = (4);
            
            
            
            if (timeSignatureTop < MIN_TS || timeSignatureTop > MAX_TS) {
                
                ErrorLog.log(ErrorLog.COMMENT, "Beats per measure must be between " + MIN_TS + " and "
                        
                        + MAX_TS + ".");
                
                return false;
                
            }
            
            
            
            if (!isPowerOf2(timeSignatureBottom) || timeSignatureBottom < MIN_TS ||
                    
                    timeSignatureBottom > MAX_TS) {
                
                ErrorLog.log(ErrorLog.COMMENT, "Beat value must be a power of two between " + MIN_TS + " and " + MAX_TS + ".");
                
                return false;
                
            }
            
            
            
            beatValue = ((BEAT * 4)/timeSignatureBottom);
            
            measureLength = beatValue * timeSignatureTop;
            
            
            
            initMetreAndLength(timeSignatureTop, timeSignatureBottom, false);
            
        }
        
        
        
        catch (NumberFormatException e) {
            
            ErrorLog.log(ErrorLog.COMMENT, "Invalid time signature entered: ");
            
            return false;
            
        }
        
        
        
        return true;
        
    }
    
    
    
    private boolean saveGlobalPreferences() {
        
        boolean close = true;
        
        
        return close;
        
    }
    
    
    
    private void setPrefsDialog() {
        
       
    }
    
    
    
    private void setNewSectionEnabled() {
        
        
    }
    
    
    
    private void setFreezeLayout(Boolean frozen) {
        
        if( frozen ) {
            

            
        } else {
            
            score.setLayoutList(Polylist.nil);
         
        }
    }
    
    
    public void refreshTabTitle(int i) {
 /*       
        String title = staveScrollPane[i].getStave().getPartTitle();
        
        if(title.trim().equals(""))
          {
          title = "Chorus " + (i + 1);
          }
        scoreTab.setTitleAt(i, title);
  */
    }
    
    
    
    private void saveLeadsheetPreferences() {
        
        
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
            
            
            
           
            setTotalMeasures(scoreBarLength);
            
        }
        
        
        
        // However, if we change things from the preferences box, we want to update
        
        // the score length so it matches.
        
        else {
            
            int scoreBarLength = 4; // FIX!! 

            setTotalMeasures(scoreBarLength);
            
            score.setMetre(top, bottom);
            
            score.setLength(scoreBarLength * measureLength);
            
            sectionInfo.setSize(scoreBarLength * measureLength);
            
        }
        
        
        
        setupArrays();
        
    }
    
    
    
    
    
    private void adjustLayout(Polylist layout) {
        
        // System.out.println("adjustLayout to " + layout);
        
        
        
        if( layout == null || layout.isEmpty() || noLockedMeasures() ) {
            
            score.setLayoutList(Polylist.nil);
            
            // auto adjust and leave
            
            autoAdjustStaves = true;
            
            staveScrollPane[0].getStave().repaint();
            
            setLockedMeasures(staveScrollPane[0].getStave().getLineMeasures(), "adjustLayout");
            
            autoAdjustStaves = autoAdjustMI.isSelected();
            
            return;
            
        }
        
        score.setLayoutList(layout);
        
        setLayoutPreference(layout);
        
        autoAdjustStaves = false;
        
        // Determine how many measures there are currently
        
        int currentMeasures = 0;
        
        for( int k = lockedMeasures.length-1; k >= 0 ; k-- ) {
            
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
        
        
        
        for( arrayElements = 0; measuresLeft > 0 ; arrayElements++ ) {
            
            if( T.nonEmpty() ) {
                
                if( !(T.first() instanceof Long) ) {
                    
                    ErrorLog.log(ErrorLog.WARNING, "Non-integer " + T.first()
                    
                    + " in layout specification");
                    
                    return;
                    
                }
                
                
                
                lastLineLength = ((Long)T.first()).intValue();
                
                
                
                if( lastLineLength <= 0 ) {
                    
                    ErrorLog.log(ErrorLog.WARNING, "Non-positive line length "
                            
                            + lastLineLength + " in layout specification");
                    
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
        
        
        
        for( int k = 0; measuresLeft > 0 ; k++ ) {
            
            if( T.nonEmpty() ) {
                
                lastLineLength = ((Long)T.first()).intValue();
                
                T = T.rest();
                
            }
            
            
            
            thisLineLength= lastLineLength < measuresLeft ? lastLineLength : measuresLeft;
            
            newLockedMeasures[k] = thisLineLength;
            
            
            
            measuresLeft -= thisLineLength;
            
        }
        
        
        
        setLockedMeasures(newLockedMeasures, "adjustLayout2");
        
        
        
        // forces the stave to paint
        
        getCurrentStave().paintImmediately(0, 0,
                
                getCurrentStave().getWidth(),
                
                getCurrentStave().getHeight());
        
    }
    
    
    
    public void setLockedMeasures(int[] _lockedMeasures, String msg) {
        
        
        
/*
 
System.out.print("lockedMeasures in " + msg + " = " );
 
if( _lockedMeasures != null )
 
  {
 
  for( int i = 0; i < _lockedMeasures.length; i++ )
 
    {
 
    System.out.print(_lockedMeasures[i] + " ");
 
    }
 
  }
 
System.out.println();
 
 */
        
        
        
        lockedMeasures = _lockedMeasures;
        
    }
    
    
    
    public int[] getLockedMeasures() {
        
        return lockedMeasures;
        
    }
    
    
    
    public boolean noLockedMeasures() {
        
        return lockedMeasures == null;
        
    }
    
    
    
    public boolean hasLockedMeasures() {
        
        return lockedMeasures != null;
        
    }
    
    
    
    private void setTotalMeasures(int measures) {
        
        if( measures == 0 || noLockedMeasures() ) {
            
            return;
            
        }
        
//  score.setLength(measures * measureLength);
        
        
        
        int defaultMeasPerLine = 4;
        
        int[] tempLockedMeasures = new int[lockedMeasures.length +
                
                (measures - lockedMeasures.length) / 4];
        
        for (int i=0; i<tempLockedMeasures.length; i++) {
            
            if (i < lockedMeasures.length)
                
                tempLockedMeasures[i] = lockedMeasures[i];
            
            else
                
                tempLockedMeasures[i] = defaultMeasPerLine;
            
        }
        
        
        
        setLockedMeasures(tempLockedMeasures, "setTotalMeasures");
        
    }
    
    
    
    /**
     *
     * Cancels out of hte preferences dialog.
     *
     */
    
    
    
    /**
     *
     * Initializes the "Preferences" dialog by setting all the text fields to the
     *
     * current values.
     *
     */
    
    
    
    /**
     *
     * Sets the layout text field in the preferences dialog.
     *
     */
    
    
    
    private void clearLayoutPreference() {
        
     }
    
    
    
    private void setLayoutPreference(Polylist layout) {
        
        if( layout != null ) {
            
            StringBuffer buffer = new StringBuffer();
            
            
            
            while( layout.nonEmpty() ) {
                
                buffer.append(layout.first() + " ");
                
                layout = layout.rest();
                
            }
            
            
            
         }
        
    }
    
    
    
    
    
    /**
     *
     * Loads the advice rules for the current score
     *
     */
        
    
    
    
    
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
        
        if (scoreTab.getSelectedIndex() != OUT_OF_BOUNDS) {
            
            // Get the previous Stave panel's location
            
           staveScrollPane[scoreTab.getSelectedIndex()].setBGlocation(
                    
                    staveScrollPane[currTabIndex].getBGlocation().x,
                    
                    staveScrollPane[currTabIndex].getBGlocation().y );
            
            
            
            // update the current tab index
            
            currTabIndex = scoreTab.getSelectedIndex();
            
            
            
            /* If the tab gets changed during playback, disable autoscrolling
             
             * since the playback indicator is no longer on the screen
             
             */
            
            if(getPlaying() != MidiPlayListener.Status.STOPPED) {
                
                autoScrollOnPlayback = (currentPlaybackTab == currTabIndex);
                
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
            
            
            
//            ImproVisor.setPlayMelody(melodyMute.isSelected());
            
//            ImproVisor.setPlayChords(chordMute.isSelected());
            
//            ImproVisor.setPlayBass(bassMute.isSelected());
            
            
            
            // set the menu and button states
            
            setItemStates();
            
        }
        
    }//GEN-LAST:event_scoreTabStateChanged
    
    
    
    public Rectangle getCurrentScrollPosition() {
        
        return staveScrollPane[currTabIndex].getViewport().getViewRect();
        
    }
    
    
    
    public void setCurrentScrollPosition(Rectangle r) {
        
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
     * Cancels out of the "New Score" dialog.
     *
     */
        
    
    
    
    
    /**
     *
     * Opens a new score in a new Notate window.
     *
     */
        
    
    
    
    
    /**
     *
     * Adds a new, blank tabbed part to the score.
     *
     */
    
    private void addTabMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabMIActionPerformed
        
        int keySig = getCurrentStave().getKeySignature();
        
        
        
        if (partArray != null)
            
            score.addPart();
        
        else {
            
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
     * Acknowledges the information, then closes the window.
     *
     */
    
    private void okErrorBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okErrorBtnActionPerformed
        
        errorDialog.setVisible(false);
        
    }//GEN-LAST:event_okErrorBtnActionPerformed
    
    
    
    
    
    /**
     *
     * Opens the help dialog.
     *
     */
    
    
    
    // The following code was refactored as it appeared to be cut-and-paste.
    
    
    
    private void enterMeasuresCore() {
        
        try {
            
            String measuresText = enterMeasures.getText();
            
            if (measuresText.length() > 0
                    
                    && Integer.decode(measuresText).intValue() > 0) {
                
                
                
                int newMeasures = Integer.decode(measuresText).intValue();
                
                
                
                if( newMeasures <= maxMeasuresPerLine ) {
                    
                    measureOverride(newMeasures, getCurrentStave().currentLine);
                    
                    
                    
                    getCurrentStave().repaint();
                    
                    
                    
                    overrideFrame.setVisible(false);
                    
                    
                    
                    staveRequestFocus();
                    
                } else {
                    
                    measErrorLabel.setText("This would exceed the maximum number of "
                            
                            + maxMeasuresPerLine);
                    
                }
                
            } else
                
                measErrorLabel.setText("Invalid number!");
            
            
            
        } catch (NumberFormatException e) {
            
            measErrorLabel.setText("Invalid number!");
            
        }
        
    }
    
    
    
    /**
     *
     * Accepts the new number of measures for the line.
     *
     */
    
    
    
    /**
     *
     * Accepts the new chord for the construction line.
     *
     */
    
    
    
    /**
     *
     * Resets the focus if the advice frame is "closed".
     *
     */
        
    
    
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
    
    
    
    
    
    public void adviceRequest() {
        
        
    }
    
    
    
    /**
     *
     * Now advice is entered by clicking only.
     *
     * Keystrokes should behave the same as if pressed in the stave window.
     *
     */
    
    
        
    
    
    
    
    /**
     *
     * This is called from within and also from StaveActionHandler
     *
     * so that Mac command key (= meta key) behaves as control key would
     *
     * on other platforms.
     *
     */
    
    
    
    public void controlDownBehavior(KeyEvent e) {
        
        Trace.log(2, "controlDownBehavior " + e);
        
        switch( e.getKeyCode() ) {
            
            case KeyEvent.VK_A: selectAllMIActionPerformed(null);                break;
            
            case KeyEvent.VK_B: enterBothMIActionPerformed(null);                break;
            
            case KeyEvent.VK_C: copyBothMIActionPerformed(null);                 break;
            
            case KeyEvent.VK_D: transposeBothDownSemitoneActionPerformed(null);  break;
            
            case KeyEvent.VK_E: transposeBothUpSemitoneActionPerformed(null);    break;
            
            case KeyEvent.VK_L: oneAutoMIActionPerformed(null);                  break;
            
            case KeyEvent.VK_V: pasteBothMIActionPerformed(null);                break;
            
            case KeyEvent.VK_X: cutBothMIActionPerformed(null);                  break;
            
            case KeyEvent.VK_Y: redoMIActionPerformed(null);                     break;
            
            case KeyEvent.VK_Z: undoMIActionPerformed(null);                     break;
            
            case KeyEvent.VK_SPACE: toggleBothEnharmonics();                     break;
            
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
    
    
    
    public void adviceKeyPressed(KeyEvent e) {
        
        
        
        Trace.log(2, "key " + e.getKeyCode() + " pressed in Advice");
        
        
        
        resetAdviceUsed();
        
        
        
        int subDivs = 2;
        
        
        
        // Checks to see if a note or group of notes is selected
        
        if (getCurrentSelectionStart() != OUT_OF_BOUNDS
                
                && getCurrentSelectionEnd() != OUT_OF_BOUNDS) {
            
            
            
            if( e.isControlDown() ) {
                controlDownBehavior(e);
            } else if( e.isShiftDown() ) {
                
                switch( e.getKeyCode() ) {
                    
                    case KeyEvent.VK_B: enterChordsMIActionPerformed(null);               break;
                    
//                    case KeyEvent.VK_A: moveLeft();                                       break;
                    
                    case KeyEvent.VK_C: copyChordsMIActionPerformed(null);                break;
                    
                    case KeyEvent.VK_D: transposeChordsDownSemitoneActionPerformed(null); break;
                    
                    case KeyEvent.VK_E: transposeChordsUpSemitoneActionPerformed(null);   break;
                    
                    case KeyEvent.VK_M: transposeChordsUpSemitoneActionPerformed(null);   break;
                    
                    case KeyEvent.VK_V: pasteChordsMIActionPerformed(null);               break;
                    
                    case KeyEvent.VK_X: cutChordsMIActionPerformed(null);                 break;
                    
                    case KeyEvent.VK_Y: redoMIActionPerformed(null);                      break;
                    
                    case KeyEvent.VK_Z: undoMIActionPerformed(null);                      break;
                    
                    case KeyEvent.VK_SPACE:
                        toggleChordEnharmonics();
                        getCurrentStave().repaint();                   break;
                        
                    case KeyEvent.VK_ENTER: getCurrentStave().playSelection(true, 0);     break;
                    
                }
                
            } else {
                
                switch( e.getKeyCode() ) {
                    
                    // neither shift nor control
                    
                    
                    
                    case KeyEvent.VK_A: moveLeft();                                       break;
                    
                    case KeyEvent.VK_B: enterMelodyMIActionPerformed(null);               break;
                    
                    case KeyEvent.VK_C: copyMelodyMIActionPerformed(null);                break;
                    
                    case KeyEvent.VK_D: transposeMelodyDownSemitoneActionPerformed(null); break;
                    
                    case KeyEvent.VK_E: transposeMelodyUpSemitoneActionPerformed(null);   break;
                    
                    case KeyEvent.VK_F: moveRight();                                      break;
                    
                    case KeyEvent.VK_G: transposeMelodyDownOctaveActionPerformed(null);   break;
                    
                    case KeyEvent.VK_I: playAllMIActionPerformed(null);                   break;
                    
                    case KeyEvent.VK_K: stopPlayMIActionPerformed(null);                  break;
                    
                    case KeyEvent.VK_R: addRest();                                        break;
                    
                    case KeyEvent.VK_T: transposeMelodyUpOctaveActionPerformed(null);     break;
                    
                     case KeyEvent.VK_V: pasteMelodyMIActionPerformed(null);               break;
                    
                    case KeyEvent.VK_X: cutMelodyMIActionPerformed(null);                 break;
                    
                    case KeyEvent.VK_Y: redoMIActionPerformed(null);                      break;
                    
                    case KeyEvent.VK_Z: undoMIActionPerformed(null);                      break;
                    
                    
                    
                    case KeyEvent.VK_SLASH: invertMelodyActionPerformed(null);            break;
                    
                    case KeyEvent.VK_BACK_SLASH: reverseMelodyActionPerformed(null);      break;
                    
                    case KeyEvent.VK_ENTER: getCurrentStave().playSelection(false, 0);    break;
                    
                    case KeyEvent.VK_DELETE:
                        
                    case KeyEvent.VK_BACK_SPACE:
                        
                        
                        
                        // delete note
                        
                        cm.execute(new DeleteUnitsCommand(getCurrentOrigPart(),
                                
                                getCurrentSelectionStart(),
                                
                                getCurrentSelectionEnd()));
                        
                        redoAdvice();
                        
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
            }
            staveRequestFocus();
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
    
    
    
    public void addMeasures(int numNewMeasures) {
        
/*        // increase score length by getting/setting text field in preferences
        
        int oldScoreBarLength = Integer.parseInt(prefMeasTF.getText());
        
        int newScoreBarLength = oldScoreBarLength + numNewMeasures;
        
        prefMeasTF.setText(String.valueOf(newScoreBarLength));
        
        
        
        Rectangle r = getCurrentScrollPosition();
        
        
        
        // then reinit the length of the score
        
        int[] metre = getCurrentOrigPart().getMetre();
        
        initMetreAndLength(metre[0], metre[1], false);
        
        
        
        setCurrentScrollPosition(r);
*/        
    }
    
    
    
    /**
     *
     * Add a rest at the current selection start.
     *
     */
    
    
    
    public void addRest()
    
    {
        
        Trace.log(2, "add rest");
        
        if( slotIsSelected() )
            
        {
            
            cm.execute(new SetRestCommand(getCurrentSelectionStart(),
                    
                    getCurrentOrigPart()));
            
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
        
// wrong, and probably not needed    getCurrentStaveActionHandler().redoAdvice(getCurrentSelectionStart() + 1);
        
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
        
    
    
    
    
    /**
     *
     * Allows the user to override the number of measures in a line.
     *
     */
    
    
    
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
        
        
        
        // forces the stave to paint
        
        getCurrentStave().paintImmediately(0, 0,
                
                getCurrentStave().getWidth(),
                
                getCurrentStave().getHeight());
        
        
        
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
        
        
        
        // forces the stave to paint
        
        getCurrentStave().paintImmediately(0, 0,
                
                getCurrentStave().getWidth(),
                
                getCurrentStave().getHeight());
        
        
        
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
                        
                        partArray[currTabIndex]) );
                
                
                
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
                        
                        partArray[currTabIndex]) );
                
                
                
                redoAdvice();
                
            }
            
        }
        
    }
    
    
    
    /**
     *
     * Shows the advice frame or removes it.
     *
     */
        
    
    
    
    
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
    
    
    
    private void pasteMelody(Part part) {
        
        if ( slotIsSelected() ) {
            
            
            
            int i = getCurrentSelectionStart() / beatValue;
            
            
            
            for (; i < getCurrentSelectionEnd() / beatValue; ++i)
                
                getCurrentStave().setSubDivs(i, 2);
            
            
            

            
            justPasted = true;
            
            
            
            getCurrentStave().paintImmediately(0, 0,
                    
                    getCurrentStave().getWidth(),
                    
                    getCurrentStave().getHeight());
            
            
            
            // set the menu and button states
            
            setItemStates();
            
        }
        
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
            

            
            getCurrentStave().paintImmediately(0, 0,
                    
                    getCurrentStave().getWidth(),
                    
                    getCurrentStave().getHeight());
            
            
            
            // set the menu and button states
            
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
    
    
    
    
    void  cutMelody() {
        
        Trace.log(2, "cut melody");
        
        
        
        if ( slotIsSelected() ) {
            
            
            
            cm.execute(new CutCommand(getCurrentOrigPart(),
                    
                    impro.getMelodyClipboard(),
                    
                    getCurrentSelectionStart(),
                    
                    getCurrentSelectionEnd()));
            
            
            
            getCurrentStave().setPasteFromStart(
                    
                    getCurrentSelectionStart());
            
            getCurrentStave().setPasteFromEnd(
                    
                    getCurrentSelectionEnd());
            
            
            
            setCurrentSelectionEnd(getCurrentSelectionStart());
            
            
            
            redoAdvice();
            
            
            
            // set the menu and button states
            
            setItemStates();
            
        }
        
        
        
    }
    
    
    
    void  reverseMelody() {
        
        Trace.log(2, "reverse melody");
        
        
        
        if ( slotIsSelected() ) {
            
            
            
            if( !getCurrentStave().trimSelection() ) {
                
                return;  // all rests
                
            }
            
            
            
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
    
    
    
    void  invertMelody() {
        
        Trace.log(2, "invert melody");
        
        
        
        if ( slotIsSelected() ) {
            
            
            
            if( !getCurrentStave().trimSelection() ) {
                
                return;  // all rests
                
            }
            
            
            
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
    
    
    
    void  timeWarpMelody(int num, int denom) {
        
        Trace.log(2, "time-warp melody by " + num + "/" + denom);
        
        
        
        if ( slotIsSelected() && num > 0 && denom > 0 ) {
            
            
            
            if( !getCurrentStave().trimSelection() ) {
                
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
        
        for (int i=0; i < staveScrollPane.length; i++ ) {
            
            MiniStave stave = staveScrollPane[i].getStave();
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
        
    
    
    
    
    /**
     *
     * Displays the construction lines on the score if checked in the menu
     *
     */
    
    private void allCstrLinesMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allCstrLinesMIActionPerformed
        
        for (int i=0; i < staveScrollPane.length; i++ ) {
          
             MiniStave stave = staveScrollPane[i].getStave();
           
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
        
        for (int i=0; i < staveScrollPane.length; i++ ) {
            
            MiniStave stave = staveScrollPane[i].getStave();

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
        
        for (int i=0; i < staveScrollPane.length; i++) {
            
            MiniStave stave = staveScrollPane[i].getStave();

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
        
        
        
        for (int i=0; i < staveScrollPane.length; i++) {
            
            MiniStave stave = staveScrollPane[i].getStave();

            stave.changeType(StaveType.AUTO);
            
            stave.repaint();
            
        }
        
        
        
        getCurrentOrigPart().setStaveType(StaveType.AUTO);
        
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        
        
    }//GEN-LAST:event_autoStaveMIActionPerformed
    
    
    
    
    
    /**
     *
     * Displays the score as a grand stave
     *
     */
    
    private void grandStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grandStaveMIActionPerformed
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        getCurrentStave().changeType(StaveType.GRAND);
        
        getCurrentStave().repaint();
        
        getCurrentOrigPart().setStaveType(StaveType.GRAND);
        
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
    }//GEN-LAST:event_grandStaveMIActionPerformed
    
    
    
    
    
    /**
     *
     * Displays the score in bass clef format
     *
     */
    
    private void bassStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassStaveMIActionPerformed
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        getCurrentStave().changeType(StaveType.BASS);
        
        getCurrentStave().repaint();
        
        getCurrentOrigPart().setStaveType(StaveType.BASS);
        
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
    }//GEN-LAST:event_bassStaveMIActionPerformed
    
    
    
    
    
    /**
     *
     * Displays the score in treble clef format
     *
     */
    
    private void trebleStaveMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trebleStaveMIActionPerformed
        
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        
        
        getCurrentStave().changeType(StaveType.TREBLE);
        
        getCurrentStave().repaint();
        
        
        
        getCurrentOrigPart().setStaveType(StaveType.TREBLE);
        
        
        
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
    }//GEN-LAST:event_trebleStaveMIActionPerformed
    
    
    
    
    
    /**
     *
     * Plays the score until the score's end
     *
     */
    
    public void playAllMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playAllMIActionPerformed
        
        playScore();
        
    }//GEN-LAST:event_playAllMIActionPerformed
    
    
    
    public void playScore() {
        
        if(getPlaying() == MidiPlayListener.Status.PAUSED) {
            
            Trace.log(2, "Notate: playScore() - unpausing");
            
            
            
            pauseScore();
            
        } else {
            
            Trace.log(2, "Notate: playScore() - starting or restarting playback");
            
            
            
            // makes playback indicator always visible
            
            // set to false upon user scroll
            
            autoScrollOnPlayback = true;
            
            
            
            long startAt = 0;
            
            if(getPlaying() == MidiPlayListener.Status.STOPPED) {
                
                startAt = playbackManager.getMicrosecondsFromSlider();
                
            }
            
            
            
            // reset playback offset
            
            initCurrentPlaybackTab(0, 0);
            
            
            
            getCurrentStave().play(startAt);
            
        }
        
    }
    
    
    
    public void initCurrentPlaybackTab(int offset) {
        
        initCurrentPlaybackTab(offset, currTabIndex);
        
    }
    
    
    
    private int playbackOffset = 0;
    
    public void initCurrentPlaybackTab(int offset, int tab) {
        
        playbackOffset = tab * getScore().getLength() + offset;
        
        currentPlaybackTab = tab;
        
    }
    
    
    
    public void enableStopButton(boolean enabled) {
        
        stopBtn.setEnabled(enabled);
        
    }
    
  public int getTransposition()
    {
    return 0;
    }

    
    public void setPlaying(MidiPlayListener.Status playing, int transposition) {
        
        Trace.log(2, "Notate: Play Status Changed to " + playing);
        
        
        
        /**
         *
         * update the playbackManager
         *
         */
        
        playbackManager.setPlaying(playing, transposition);
        
        
        
        isPlaying = playing;
        
        switch(playing) {
            
            case PLAYING:
                
                pauseBtn.setEnabled(true);
                
                stopBtn.setEnabled(true);
                
                pauseBtn.setSelected(false);
                
                break;
                
            case PAUSED:
                
                pauseBtn.setEnabled(true);
                
                stopBtn.setEnabled(true);
                
                pauseBtn.setSelected(true);

                
                break;
                
            case STOPPED:
                
                setShowPlayLine(false);
                
                pauseBtn.setEnabled(false);
                
                stopBtn.setEnabled(false);
                
                pauseBtn.setSelected(false);
                
                
                playbackManager.setTotalTime(score.getTotalTime());
                
                getCurrentStave().repaint();
                
                break;
                
        }
        
        
        
        staveRequestFocus();
        
    }
    
    
    
    public MidiPlayListener.Status getPlaying() {
        
        return isPlaying;
        
    }
    
    
    
    /**
     *
     * Closes the current notateFrame
     *
     */
        
    
    
    
    
    public MidiSynth getMidiSynth() {
        
        return midiSynth;
        
    }
    
    
    
    /**
     *
     * Saves the current score as a leadsheet file
     *
     * Assumes the user will put leadsheetExt as their file extension
     *
     */
    
    private void autoFillCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoFillCheckBoxActionPerformed
        autoFill = autoFillCheckBox.isSelected();
        
             
    }//GEN-LAST:event_autoFillCheckBoxActionPerformed

    private void clearProbsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearProbsButtonActionPerformed

    }//GEN-LAST:event_clearProbsButtonActionPerformed

    private void FillProbsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FillProbsButtonActionPerformed
 
    }//GEN-LAST:event_FillProbsButtonActionPerformed

    private void rootComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootComboBoxActionPerformed
        lickgen.setPreferredScale((String)rootComboBox.getSelectedItem(), (String)scaleComboBox.getSelectedItem());
        
    }//GEN-LAST:event_rootComboBoxActionPerformed

    private void scaleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleComboBoxActionPerformed
        String root = (String)rootComboBox.getSelectedItem();
        
        String type = (String)scaleComboBox.getSelectedItem();
        
        
        
        if (root == null || type == null)
            
            return;
        
        
        
        if (type.equals("None") || type.equals("Use First Scale"))
            
            rootComboBox.setEnabled(false);
        
        else
            
            rootComboBox.setEnabled(true);
        
        
        
        lickgen.setPreferredScale((String)rootComboBox.getSelectedItem(), (String)scaleComboBox.getSelectedItem());
        
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_scaleComboBoxActionPerformed

    private void chordToneDecayFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chordToneDecayFieldFocusLost
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_chordToneDecayFieldFocusLost

    private void chordToneDecayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordToneDecayFieldActionPerformed
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_chordToneDecayFieldActionPerformed

    private void scaleToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scaleToneWeightFieldFocusLost
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_scaleToneWeightFieldFocusLost

    private void scaleToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleToneWeightFieldActionPerformed
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_scaleToneWeightFieldActionPerformed

    private void colorToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_colorToneWeightFieldFocusLost
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_colorToneWeightFieldFocusLost

    private void colorToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToneWeightFieldActionPerformed
        
        if (autoFill)
            
            FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_colorToneWeightFieldActionPerformed
//GEN-FIRST:event_chordToneWeightFieldFocusLost
//GEN-LAST:event_chordToneWeightFieldFocusLost

    private void cancelNewScoreBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelNewScoreBtnActionPerformed
        newScoreDialog.setVisible(false);
    }//GEN-LAST:event_cancelNewScoreBtnActionPerformed

    private void okNewScoreBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okNewScoreBtnActionPerformed
        Score newScore = new Score(titleTF.getText());
        
        newScore.setChordProg(new ChordPart());
        
        // set the metre
        
        try {
            
            if (Integer.decode(metreTF.getText()).intValue() > 0
                    
                    && Integer.decode(metreTF.getText()).intValue() < 9) {
                
                
                // For right now, set the default metre to 4/4.
                
                // Eventually, get rid of magic numbers here and allow the user to
                
                // create a new score with a different time signature.
                
                newScore.setMetre(4, 4);
                
            } else {
                
                ErrorLog.log(ErrorLog.COMMENT,
                        
                        "Metre out of bounds: '" + metreTF.getText()
                        
                        + "'. Setting metre to 4/4.");
                
            }
            
        } catch (NumberFormatException e) {
            
            
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "Metre cannot equal: '" + metreTF.getText()
                    
                    + "'. Setting metre to 4/4.");
            
            newScore.setMetre(4, 4);
            
        }
        
        
        
        // set the number of measures
        
        try {
            
            if (Integer.decode(measuresTF.getText()).intValue() > 0)
                
                newScore.addPart( new MelodyPart(
                        
                        Integer.decode(measuresTF.getText()).intValue() * measureLength) );
            
            
            
        } catch (NumberFormatException e) {
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "Measures cannot equal " + measuresTF.getText()
                    
                    + ". Default number of measures setting to 32.");
            
            newScore.addPart( new MelodyPart(32 * measureLength ));
            
        }
        
        
        
        
        
        // set the key signature
        
        try {
            
            if (sharpsRBtn.isSelected())
                
                newScore.setKeySignature(
                        
                        Integer.decode(keySigTF.getText()).intValue() );
            
            else
                
                newScore.setKeySignature(
                        
                        -Integer.decode(keySigTF.getText()).intValue() );
            
            
            
        } catch (NumberFormatException e) {
            
            ErrorLog.log(ErrorLog.COMMENT,
                    
                    "Invalid number of sharps or flats entered. Setting the "
                    
                    + "key signature to 2 sharps.");
            
        }
        
        
        
        // open a new window
        
        MiniNotate newNotate = new MiniNotate(newScore,
                
                this.adv, this.impro, (int)this.getLocation().getX() + 40,
                
                (int)this.getLocation().getY() + 40);
        
        
        
        // huh? what is this doing here:
        
        //        tempoSet.setText("" + score.getTempo());
        
        //        chordVolume.setValue(getScore().getChordVolume());
        
        
        
        // set the menu and button states
        
        setItemStates();
        
        
        
        newScoreDialog.setVisible(false);
    }//GEN-LAST:event_okNewScoreBtnActionPerformed

    private void addTabBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTabBtnActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addTabBtnActionPerformed

    private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMIActionPerformed
        
    }//GEN-LAST:event_cascadeMIActionPerformed

    private void helpMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_helpMenuActionPerformed
        
    }//GEN-LAST:event_helpMenuActionPerformed

    private void chordToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_chordToneWeightFieldActionPerformed
    {//GEN-HEADEREND:event_chordToneWeightFieldActionPerformed
      
      if (autoFill)
        
        FillProbsButtonActionPerformed(null);
    }//GEN-LAST:event_chordToneWeightFieldActionPerformed

    private void leapProbFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_leapProbFieldFocusLost
    {//GEN-HEADEREND:event_leapProbFieldFocusLost
    }//GEN-LAST:event_leapProbFieldFocusLost

    private void leapProbFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_leapProbFieldActionPerformed
    {//GEN-HEADEREND:event_leapProbFieldActionPerformed
    }//GEN-LAST:event_leapProbFieldActionPerformed

    private void restProbFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_restProbFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_restProbFieldenterLickKeyPressed
      
    }//GEN-LAST:event_restProbFieldenterLickKeyPressed

    private void restProbFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_restProbFieldFocusLost
    {//GEN-HEADEREND:event_restProbFieldFocusLost
    }//GEN-LAST:event_restProbFieldFocusLost

    private void restProbFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_restProbFieldGetsFocus
    {//GEN-HEADEREND:event_restProbFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_restProbFieldGetsFocus

    private void restProbFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_restProbFieldActionPerformed
    {//GEN-HEADEREND:event_restProbFieldActionPerformed
    }//GEN-LAST:event_restProbFieldActionPerformed

    private void totalBeatsFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_totalBeatsFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_totalBeatsFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldenterLickKeyPressed

    private void totalBeatsFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_totalBeatsFieldFocusLost
    {//GEN-HEADEREND:event_totalBeatsFieldFocusLost
      
      setCurrentSelectionEnd(getCurrentSelectionStart() + BEAT * totalBeats - 1);
      
    }//GEN-LAST:event_totalBeatsFieldFocusLost

    private void totalBeatsFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_totalBeatsFieldGetsFocus
    {//GEN-HEADEREND:event_totalBeatsFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldGetsFocus

    private void totalBeatsFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_totalBeatsFieldActionPerformed
    {//GEN-HEADEREND:event_totalBeatsFieldActionPerformed
      setCurrentSelectionEnd(getCurrentSelectionStart() + BEAT * totalBeats - 1);
      
    }//GEN-LAST:event_totalBeatsFieldActionPerformed

    private void maxDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_maxDurationFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_maxDurationFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_maxDurationFieldenterLickKeyPressed

    private void maxDurationFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxDurationFieldFocusLost
    {//GEN-HEADEREND:event_maxDurationFieldFocusLost
    }//GEN-LAST:event_maxDurationFieldFocusLost

    private void maxDurationFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxDurationFieldGetsFocus
    {//GEN-HEADEREND:event_maxDurationFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_maxDurationFieldGetsFocus

    private void maxDurationFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_maxDurationFieldActionPerformed
    {//GEN-HEADEREND:event_maxDurationFieldActionPerformed
    }//GEN-LAST:event_maxDurationFieldActionPerformed

    private void minDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_minDurationFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_minDurationFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_minDurationFieldenterLickKeyPressed

    private void minDurationFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minDurationFieldFocusLost
    {//GEN-HEADEREND:event_minDurationFieldFocusLost
    }//GEN-LAST:event_minDurationFieldFocusLost

    private void minDurationFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minDurationFieldGetsFocus
    {//GEN-HEADEREND:event_minDurationFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_minDurationFieldGetsFocus

    private void minDurationFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minDurationFieldActionPerformed
    {//GEN-HEADEREND:event_minDurationFieldActionPerformed
    }//GEN-LAST:event_minDurationFieldActionPerformed

    private void maxIntervalFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_maxIntervalFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_maxIntervalFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_maxIntervalFieldenterLickKeyPressed

    private void maxIntervalFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxIntervalFieldFocusLost
    {//GEN-HEADEREND:event_maxIntervalFieldFocusLost
    }//GEN-LAST:event_maxIntervalFieldFocusLost

    private void maxIntervalFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxIntervalFieldGetsFocus
    {//GEN-HEADEREND:event_maxIntervalFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_maxIntervalFieldGetsFocus

    private void maxIntervalFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_maxIntervalFieldActionPerformed
    {//GEN-HEADEREND:event_maxIntervalFieldActionPerformed
    }//GEN-LAST:event_maxIntervalFieldActionPerformed

    private void minIntervalFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_minIntervalFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_minIntervalFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_minIntervalFieldenterLickKeyPressed

    private void minIntervalFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minIntervalFieldFocusLost
    {//GEN-HEADEREND:event_minIntervalFieldFocusLost
    }//GEN-LAST:event_minIntervalFieldFocusLost

    private void minIntervalFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minIntervalFieldGetsFocus
    {//GEN-HEADEREND:event_minIntervalFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_minIntervalFieldGetsFocus

    private void minIntervalFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minIntervalFieldActionPerformed
    {//GEN-HEADEREND:event_minIntervalFieldActionPerformed
    }//GEN-LAST:event_minIntervalFieldActionPerformed

    private void minPitchFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_minPitchFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_minPitchFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_minPitchFieldenterLickKeyPressed

    private void minPitchFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minPitchFieldFocusLost
    {//GEN-HEADEREND:event_minPitchFieldFocusLost
    }//GEN-LAST:event_minPitchFieldFocusLost

    private void minPitchFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_minPitchFieldGetsFocus
    {//GEN-HEADEREND:event_minPitchFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_minPitchFieldGetsFocus

    private void minPitchFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_minPitchFieldActionPerformed
    {//GEN-HEADEREND:event_minPitchFieldActionPerformed
    }//GEN-LAST:event_minPitchFieldActionPerformed

    private void maxPitchFieldenterLickKeyPressed(java.awt.event.KeyEvent evt)//GEN-FIRST:event_maxPitchFieldenterLickKeyPressed
    {//GEN-HEADEREND:event_maxPitchFieldenterLickKeyPressed
      // TODO add your handling code here:
    }//GEN-LAST:event_maxPitchFieldenterLickKeyPressed

    private void maxPitchFieldFocusLost(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxPitchFieldFocusLost
    {//GEN-HEADEREND:event_maxPitchFieldFocusLost
    }//GEN-LAST:event_maxPitchFieldFocusLost

    private void maxPitchFieldGetsFocus(java.awt.event.FocusEvent evt)//GEN-FIRST:event_maxPitchFieldGetsFocus
    {//GEN-HEADEREND:event_maxPitchFieldGetsFocus
      // TODO add your handling code here:
    }//GEN-LAST:event_maxPitchFieldGetsFocus

    private void maxPitchFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_maxPitchFieldActionPerformed
    {//GEN-HEADEREND:event_maxPitchFieldActionPerformed
    }//GEN-LAST:event_maxPitchFieldActionPerformed

    private void grade10BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade10BtnActionPerformed
    {//GEN-HEADEREND:event_grade10BtnActionPerformed
      
    }//GEN-LAST:event_grade10BtnActionPerformed

    private void grade9BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade9BtnActionPerformed
    {//GEN-HEADEREND:event_grade9BtnActionPerformed
      
    }//GEN-LAST:event_grade9BtnActionPerformed

    private void grade8BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade8BtnActionPerformed
    {//GEN-HEADEREND:event_grade8BtnActionPerformed
      
    }//GEN-LAST:event_grade8BtnActionPerformed

    private void grade7BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade7BtnActionPerformed
    {//GEN-HEADEREND:event_grade7BtnActionPerformed
      
    }//GEN-LAST:event_grade7BtnActionPerformed

    private void grade6BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade6BtnActionPerformed
    {//GEN-HEADEREND:event_grade6BtnActionPerformed
      
    }//GEN-LAST:event_grade6BtnActionPerformed

    private void grade5BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade5BtnActionPerformed
    {//GEN-HEADEREND:event_grade5BtnActionPerformed
      
    }//GEN-LAST:event_grade5BtnActionPerformed

    private void grade4BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade4BtnActionPerformed
    {//GEN-HEADEREND:event_grade4BtnActionPerformed
      
    }//GEN-LAST:event_grade4BtnActionPerformed

    private void grade3BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade3BtnActionPerformed
    {//GEN-HEADEREND:event_grade3BtnActionPerformed
      
    }//GEN-LAST:event_grade3BtnActionPerformed

    private void grade2BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade2BtnActionPerformed
    {//GEN-HEADEREND:event_grade2BtnActionPerformed
      
    }//GEN-LAST:event_grade2BtnActionPerformed

    private void grade1BtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_grade1BtnActionPerformed
    {//GEN-HEADEREND:event_grade1BtnActionPerformed
      
    }//GEN-LAST:event_grade1BtnActionPerformed

    private void saveLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_saveLickButtonActionPerformed
    {//GEN-HEADEREND:event_saveLickButtonActionPerformed
      
    }//GEN-LAST:event_saveLickButtonActionPerformed

    private void stopLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopLickButtonActionPerformed
    {//GEN-HEADEREND:event_stopLickButtonActionPerformed
      stopPlaying();
    }//GEN-LAST:event_stopLickButtonActionPerformed

    private void playLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playLickButtonActionPerformed
    {//GEN-HEADEREND:event_playLickButtonActionPerformed
      getCurrentStave().playSelection(false, getLoopCount());
    }//GEN-LAST:event_playLickButtonActionPerformed

    private void getSelRhythmButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_getSelRhythmButtonActionPerformed
    {//GEN-HEADEREND:event_getSelRhythmButtonActionPerformed
      int selStart = getCurrentSelectionStart();
      
      int selEnd = getCurrentSelectionEnd();
      
      MelodyPart part = getCurrentOrigPart();
      
      int current = selStart;
      
      Polylist rhythmString = new Polylist();
      
      
      
      while (current <= selEnd)
        
      {
        
        StringBuffer sb = new StringBuffer();
        
        int value = part.getNote(current).getDurationString(sb, part.getNote(current).getRhythmValue());
        
        int rhythm = 0;
        
        
        
        if (part.getNote(current).getPitch() == REST)
          
          rhythmString = rhythmString.cons("R" + sb.substring(1));
        
        else
          
          rhythmString = rhythmString.cons("X" + sb.substring(1));
        
        
        
        current += part.getNote(current).getRhythmValue();
        
      }
      
      
      
      rhythmString = rhythmString.reverse();
      
      rhythmField.setText(rhythmString.toString());
    }//GEN-LAST:event_getSelRhythmButtonActionPerformed

    private void fillMelodyButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_fillMelodyButtonActionPerformed
    {//GEN-HEADEREND:event_fillMelodyButtonActionPerformed
      String r = rhythmField.getText().trim();
      
      if (r.charAt(0) != '(')
        r = "(".concat(r);
      
      if (r.charAt(r.length() - 1) != ')')
        r = r.concat(")");
      
      rhythmField.setText(r);
      
      Polylist rhythm = new Polylist();
      StringReader rhythmReader = new StringReader(r);
      Tokenizer in = new Tokenizer(rhythmReader);
      Object ob = null;
      
      while ((ob = in.nextSexp()) != Tokenizer.eof)
        if (ob instanceof Polylist)
          rhythm = (Polylist)ob;
      
    }//GEN-LAST:event_fillMelodyButtonActionPerformed

    private void genRhythmButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_genRhythmButtonActionPerformed
    {//GEN-HEADEREND:event_genRhythmButtonActionPerformed
    }//GEN-LAST:event_genRhythmButtonActionPerformed

    private void generateLickButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_generateLickButtonActionPerformed
    {//GEN-HEADEREND:event_generateLickButtonActionPerformed
      boolean nothingWasSelected = !getCurrentStave().somethingSelected();
      if( nothingWasSelected )
      {
        selectAll();
      }
      
      
    }//GEN-LAST:event_generateLickButtonActionPerformed
    

    
    /**
     *
     * Do stuff that is common to open and revert file.
     *
     */
    
    public void setupLeadsheet(File file) {
        
        Advisor.useBackupStyles();
        Score newScore = new Score();
        
        cm.execute(new OpenLeadsheetCommand(file, newScore));
        
        savedLeadsheet = file;
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        setupScore(newScore);
    }
    
    
    
    /**
     *
     * Do stuff that is common to open file, revert file, and
     *
     * transferring contents from editor.
     *
     */
    
    public void setupScore(Score score) {
        
        // set the new score
        
        this.score = score;
        setTitle(score.getTitle());
        

        // reset the current scoreFrame
        
        setupArrays();
        
        midiSynth.setTempo((float) score.getTempo());
        
        tempoSet.setText("" + score.getTempo());
        
        tempoSlider.setValue((int) score.getTempo());
        
        // moved to scoreTabStateChanged

        // set layout if specified
        
        // FIX: reconcile with the above auto adjust
        
        clearLayoutPreference();
        
        Polylist layout =  score.getLayoutList();
        
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
    }
    
    
    
    public boolean adviceIsShowing() {
        
        return false;
        
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
    
    public void setupArrays() {
        int size = score.size();
       
        if( size <= 0 ) {
            
            return;            
        }
        
        // setup the arrays
        
        partArray  = new MelodyPart[size];
        
               
        // set the chord progression for the score
        
        chordProg = score.getChordProg();
                      
        scoreTab.removeAll();
        
        currTabIndex = 0;
                
        staveScrollPane = new MiniStaveScrollPane[size];
        
        
        // initialize each array with the appropriate part and as a grand stave
        
        for (int i=0; i < staveScrollPane.length; i++) {
            
            // construct the specific scoreBG and staveScrollPane
          
            MiniStaveScrollPane pane = new MiniStaveScrollPane(i);
            
            staveScrollPane[i] = pane;
                        
            AdjustmentListener scrollListener = new AdjustmentListener() {
                
                public void adjustmentValueChanged(AdjustmentEvent e) {
                    
                    if(e.getValueIsAdjusting())
                        
                        autoScrollOnPlayback = false;
                    
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
            
            partArray[i]  = score.getPart(i);
            
            MiniStave stave = new MiniStave(StaveType.GRAND, this, score.getTitle());
            //MiniStave stave = new MiniStave(partArray[i].getStaveType(), this, score.getTitle());
            
            pane.setStave(stave);
            
            stave.setChordProg(chordProg);
            
            stave.setPart(partArray[i]);
            
            stave.setKeySignature(partArray[i].getKeySignature());
            
            stave.setMetre(score.getMetre()[0], score.getMetre()[1]);
            
            stave.setShowBarNums(true);
            
            stave.setShowPartTitle(true);
            
            // Only Show title on first page
            
            stave.setShowSheetTitle(false);
           
            
            scoreTab.addTab("", pane);
            
            refreshTabTitle(i);
            
            scoreTab.setSelectedComponent(pane);
            
        }
        
        
        
        /**
         *
         * update GUI to reflect total time of the score
         *
         */
        
        playbackManager.setTotalTime(score.getTotalTime());
        
        updateAllStaves();
        
    }
    
    
    
    
    
    /**
     *
     * Updates the JPanel with all of the staves and redraws it
     *
     * @see Stave#paint(Graphics)
     *
     */
    
    public void updateAllStaves() {
        
        // cycle through the array and update all of the staves
        
        for (int i=0; i < staveScrollPane.length; i++) {
          
            MiniStaveScrollPane pane = staveScrollPane[i];
            
            // clear the panel area of the previous stave image
            
            pane.removeAllBG();
            
            // add to display
            
            pane.addStave(staveScrollPane[i].getStave());
            
            pane.repaint();
            
        }
        
    }
    
    
    
    
    
    /**
     *
     * Converts a polylist to a tree
     *
     *
     *
     * @param item                          a polylist
     *
     * @return DefaultMutableTreeNode       the root of a tree
     *
     */
    
    public DefaultMutableTreeNode polylistToTree(Polylist item) {
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode(item.first());
        
        
        
        item = item.rest();
        
        while(!item.isEmpty()) {
            
            if(item.first() instanceof Polylist)
                
                root.add(polylistToTree((Polylist)item.first()));
            
            else
                
                root.add(new DefaultMutableTreeNode(item.first()));
            
            item = item.rest();
            
        }
        
        return root;
        
    }
    
     
    
    
    
    /**
     *
     * Displays the window allowing overriding of measures
     *
     */
    
    public void displayOverrideMeasures() {
        
        disableAccelerators();
        
        overrideFrame.setSize(new Dimension(210, 100));
        
        overrideFrame.setLocationRelativeTo(this);
        
        
        
        lineLabel.setText("Number of measures in line "
                
                + (getCurrentStave().currentLine + 1) + ":   ");
        
        enterMeasures.setText(""
                
                + lockedMeasures[getCurrentStave().currentLine] );
        
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
    
    public void measureOverride(int measures, int currLine) {
        
        int diff = lockedMeasures[currLine] - measures;
        
        lockedMeasures[currLine] = measures;
        
        
        
        // if the difference is negative, subtract the difference
        
        // from the ending stave lines
        
        if (diff < 0)
            
            while (diff < 0) {
            
            if (lockedMeasures[lockedMeasures.length - 1]
                    
                    > Math.abs(diff)) {
                
                lockedMeasures[lockedMeasures.length - 1] += diff;
                
                diff = 0;
                
            }
            
            
            
            else {
                
                diff = diff + lockedMeasures[lockedMeasures.length - 1];
                
                lockedMeasures[lockedMeasures.length - 1] = 0;
                
                
                
                int[] tempLockedMeasures =
                        
                        new int[lockedMeasures.length - 1];
                
                
                
                for (int k=0; k<tempLockedMeasures.length; k++)
                    
                    tempLockedMeasures[k] = lockedMeasures[k];
                
                setLockedMeasures(tempLockedMeasures, "measureOverride1");
                
            }
            
            }
        
        
        
        // if the difference is positive, add the difference to the
        
        // ending stave lines
        
        else
            
            while (diff > 0) {
            
            // increase the array if the last line already has
            
            // 4 measures in it
            
            if (lockedMeasures[lockedMeasures.length - 1] >= 4) {
                
                int[] tempLockedMeasures =
                        
                        new int[lockedMeasures.length + 1];
                
                
                
                for (int k=0; k<lockedMeasures.length; k++)
                    
                    tempLockedMeasures[k] = lockedMeasures[k];
                
                tempLockedMeasures[lockedMeasures.length] = 0;
                
                
                
                setLockedMeasures(tempLockedMeasures, "measureOverride2");
                
            }
            
            
            
            // add 4 or less measures to an empty last line
            
            if (lockedMeasures[lockedMeasures.length - 1] == 0) {
                
                if (diff <= 4) {
                    
                    lockedMeasures[lockedMeasures.length - 1] = diff;
                    
                    diff = 0;
                    
                } else {
                    
                    lockedMeasures[lockedMeasures.length - 1] = 4;
                    
                    diff = diff - 4;
                    
                }
                
            }
            
            
            
            // add some measures to a partially filled last line
            
            else if (lockedMeasures[lockedMeasures.length - 1] > 0
                    
                    && lockedMeasures[lockedMeasures.length - 1] <= 4) {
                
                if (diff + lockedMeasures[lockedMeasures.length - 1] <= 4) {
                    
                    lockedMeasures[lockedMeasures.length - 1]
                            
                            += diff;
                    
                    diff = 0;
                    
                } else {
                    
                    diff -= (4 - lockedMeasures[lockedMeasures.length - 1]);
                    
                    lockedMeasures[lockedMeasures.length - 1] = 4;
                    
                }
                
            }
            
            }
        
    }
    
    
    
    
    
    /**
     *
     * Calculates the size of the toolBarPanel needed
     *
     */
    
//    public void calcToolBarSize() {
    
//        double maxY = fileToolBar.getLocation().getY()
    
//        + fileToolBar.getHeight();
    
//
    
//        if (editToolBar.getLocation().getY() + editToolBar.getHeight() > maxY)
    
//            maxY = editToolBar.getLocation().getY() + editToolBar.getHeight();
    
//        if (staveToolBar.getLocation().getY() + staveToolBar.getHeight() > maxY)
    
//            maxY = staveToolBar.getLocation().getY() + staveToolBar.getHeight();
    
//        if (playToolBar.getLocation().getY() + playToolBar.getHeight() > maxY)
    
//            maxY = playToolBar.getLocation().getY() + playToolBar.getHeight();
    
//        if (textEntryToolBar.getLocation().getY() + textEntryToolBar.getHeight() > maxY)
    
//            maxY = textEntryToolBar.getLocation().getY() + textEntryToolBar.getHeight();
    
//
    
//
    
//        toolBarPanel.setPreferredSize(new Dimension(toolBarPanel.getWidth(),
    
//                (int)maxY + 5));
    
//    }
    
    
    
    
    
    /**
     *
     * Sets all of the menu and button states
     *
     */
    
    protected void setItemStates() {
        
        playAllMI.setEnabled(true);
        
        stopPlayMI.setEnabled(true);
        
        
        
        // check to see if undo & redo can be enabled
        
        if (cm.canUndo()) {
            
            undoMI.setEnabled(true);
            
            undoPMI.setEnabled(true);
            
            undoBtn.setEnabled(true);
            
        } else {
            
            undoMI.setEnabled(false);
            
            undoPMI.setEnabled(false);
            
            undoBtn.setEnabled(false);
            
        }
        
        
        
        if (cm.canRedo()) {
            
            redoMI.setEnabled(true);
            
            redoPMI.setEnabled(true);
            
            redoBtn.setEnabled(true);
            
        } else {
            
            redoMI.setEnabled(false);
            
            redoPMI.setEnabled(false);
            
            redoBtn.setEnabled(false);
            
        }
        
        
        
        
        
        // checks if "delete tab" should be enabled
        
        if (score.size() < 2) {
            
            delTabMI.setEnabled(false);

            
        } else {
            
            delTabMI.setEnabled(true);
            
            
        }
        
        
        
        // checks if "Override Measures" should be allowed
        
        if (autoAdjustMI.isSelected())
            
            overrideMeasPMI.setEnabled(false);
        
        else
            
            overrideMeasPMI.setEnabled(true);
        
    }
    
    
    
    
    
    /**
     *
     * Disable accelerators.  We do this for a work-around: When letters are typed into the
     *
     * chord/melody text entry window (and the latter is docked, which it is at the start)
     *
     * if any of those letters correspond to accelrators, the corresponding method will
     *
     * be invoked.  This seems like a bug in swing to me, but I don't know another way of
     *
     * getting around it at present.  We will rely on the call to setItemStates() to
     *
     * re-enable the accelerators upon hitting return in the text entry field.
     *
     */
    
    
    
    protected void disableAccelerators() {
        
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
        
        
        
        copyMelodySelectionToTextWindow.setEnabled(false);
        
        copyChordSelectionToTextWindow.setEnabled(false);
        
        copyBothSelectionToTextWindow.setEnabled(false);
        
        
        
        reverseMelody.setEnabled(false);
        
        invertMelody.setEnabled(false);
        
        
        
        saveSelectionAsLick.setEnabled(false);
        
    }
    
    
    
    private void resetDrawingPrefs() {
 /*       
        drawScaleTonesCheckBox.setSelected(
                
                Preferences.getPreference(Preferences.DRAWING_TONES).charAt(0) == '1');
        
        drawChordTonesCheckBox.setSelected(
                
                Preferences.getPreference(Preferences.DRAWING_TONES).charAt(1) == '1');
        
        drawColorTonesCheckBox.setSelected(
                
                Preferences.getPreference(Preferences.DRAWING_TONES).charAt(2) == '1');
        
        defaultDrawingMutedCheckBox.setSelected(
                
                Preferences.getPreference(Preferences.DEFAULT_DRAWING_MUTED).equals("true"));
  */      
    }
    
    
    
    private ImageIcon playButton = new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"));
    
    private ImageIcon pauseButton = new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/pause.gif"));
    
    
    
  // Variables declaration - do not modify//GEN-BEGIN:variables
  private javax.swing.JLabel BPM;
  private javax.swing.JButton FillProbsButton;
  private javax.swing.JMenuItem addRestMI;
  private javax.swing.JButton addTabBtn;
  private javax.swing.JMenuItem addTabMI;
  private javax.swing.JCheckBoxMenuItem allCstrLinesMI;
  private javax.swing.JCheckBox allMuteMixerBtn;
  private javax.swing.JToggleButton allMuteToolBarBtn;
  private javax.swing.JPanel allPanel;
  private javax.swing.JSlider allVolumeMixerSlider;
  private javax.swing.JSlider allVolumeToolBarSlider;
  private javax.swing.JTextArea alphaCommandList;
  private javax.swing.JScrollPane alphaCommandPane;
  private javax.swing.JTextArea alphabeticKeyBindingsHelp;
  private javax.swing.JCheckBoxMenuItem autoAdjustMI;
  private javax.swing.JCheckBox autoFillCheckBox;
  private javax.swing.JRadioButton autoStaveBtn;
  private javax.swing.JRadioButtonMenuItem autoStaveMI;
  private javax.swing.JCheckBoxMenuItem barNumsMI;
  private javax.swing.JLabel bassInstLabel;
  private javax.swing.JPanel bassInstPanel;
  private javax.swing.JCheckBox bassMute;
  private javax.swing.JPanel bassPanel;
  private javax.swing.JRadioButton bassStaveBtn;
  private javax.swing.JRadioButtonMenuItem bassStaveMI;
  private javax.swing.JSlider bassVolume;
  private javax.swing.JLabel breakpointLabel;
  private javax.swing.JTextField breakpointTF;
  private javax.swing.JButton cancelNewScoreBtn;
  private javax.swing.JMenuItem cascadeMI;
  private javax.swing.JLabel chordIInstLabel;
  private javax.swing.JPanel chordInstPanel;
  private javax.swing.JTextArea chordList;
  private javax.swing.JScrollPane chordListingPane;
  private javax.swing.JCheckBox chordMute;
  private javax.swing.JPanel chordPanel;
  private javax.swing.JPanel chordProbPanel;
  private javax.swing.JSeparator chordProbSeparator;
  private javax.swing.JTextField chordToneDecayField;
  private javax.swing.JLabel chordToneDecayRateLabel;
  private javax.swing.JLabel chordToneProbLabel;
  private javax.swing.JTextField chordToneWeightField;
  private javax.swing.JSlider chordVolume;
  private javax.swing.JPanel chorusPreferences;
  private javax.swing.JPanel chorusSpecificPanel;
  private javax.swing.JButton clearButton;
  private javax.swing.JButton clearProbsButton;
  private javax.swing.JMenuItem closeWindowMI;
  private javax.swing.JLabel colorToneProbLabel;
  private javax.swing.JTextField colorToneWeightField;
  private javax.swing.JLabel commentsLabel;
  private javax.swing.JTextField commentsTF;
  private javax.swing.JTextField composerField;
  private javax.swing.JLabel composerLabel;
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
  private javax.swing.JButton cutBothBtn;
  private javax.swing.JMenuItem cutBothMI;
  private javax.swing.JMenuItem cutBothPMI;
  private javax.swing.JMenuItem cutChordsMI;
  private javax.swing.JMenuItem cutMelodyMI;
  private javax.swing.ButtonGroup defLoadStaveBtnGroup;
  private javax.swing.JButton delTabBtn;
  private javax.swing.JMenuItem delTabMI;
  private javax.swing.JTextArea drawingHelp;
  private javax.swing.JScrollPane drawingHelpPane;
  private javax.swing.JCheckBox drumMute;
  private javax.swing.JPanel drumPanel;
  private javax.swing.JSlider drumVolume;
  private javax.swing.JLabel durationLabel;
  private javax.swing.JMenu editMenu;
  private javax.swing.JMenuItem enterBothMI;
  private javax.swing.JMenuItem enterChordsMI;
  private javax.swing.JTextField enterMeasures;
  private javax.swing.JMenuItem enterMelodyMI;
  private javax.swing.JCheckBox entryMute;
  private javax.swing.JPanel entryPanel;
  private javax.swing.JSlider entryVolume;
  private javax.swing.JDialog errorDialog;
  private javax.swing.JScrollPane errorScroll;
  private javax.swing.JTextPane errorText;
  private javax.swing.JMenuItem expandMelodyBy2;
  private javax.swing.JMenuItem expandMelodyBy3;
  private javax.swing.JButton fillMelodyButton;
  private javax.swing.JRadioButton flatsRBtn;
  private javax.swing.JButton genRhythmButton;
  private javax.swing.JButton generateLickButton;
  private javax.swing.JButton getSelRhythmButton;
  private javax.swing.JButton grade10Btn;
  private javax.swing.JButton grade1Btn;
  private javax.swing.JButton grade2Btn;
  private javax.swing.JButton grade3Btn;
  private javax.swing.JButton grade4Btn;
  private javax.swing.JButton grade5Btn;
  private javax.swing.JButton grade6Btn;
  private javax.swing.JButton grade7Btn;
  private javax.swing.JButton grade8Btn;
  private javax.swing.JButton grade9Btn;
  private javax.swing.JLabel gradeLabel;
  private javax.swing.JRadioButton grandStaveBtn;
  private javax.swing.JRadioButtonMenuItem grandStaveMI;
  private javax.swing.JButton helpBtn;
  private javax.swing.JScrollPane helpByTopic;
  private javax.swing.JTextArea helpByTopicList;
  private javax.swing.JDialog helpDialog;
  private javax.swing.JMenuItem helpMI;
  private javax.swing.JMenu helpMenu;
  private javax.swing.JTabbedPane helpTabbedPane;
  private javax.swing.JScrollPane helpTreeScroll1;
  private javax.swing.JMenuItem insertRestMeasure;
  private javax.swing.JLabel intervalLabel;
  private javax.swing.JMenuItem invertMelody;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel jLabel19;
  private javax.swing.JLabel jLabel3;
  private javax.swing.JLabel jLabel5;
  private javax.swing.JLabel jLabel6;
  private javax.swing.JLabel jLabel7;
  private javax.swing.JLabel jLabel8;
  private javax.swing.JLabel jLabel9;
  private javax.swing.JPanel jPanel3;
  private javax.swing.JPanel jPanel4;
  private javax.swing.JPanel jPanel5;
  private javax.swing.JPanel jPanel6;
  private javax.swing.JPanel jPanel7;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JSeparator jSeparator13;
  private javax.swing.JSeparator jSeparator14;
  private javax.swing.JSeparator jSeparator15;
  private javax.swing.JSeparator jSeparator16;
  private javax.swing.JSeparator jSeparator18;
  private javax.swing.JSeparator jSeparator19;
  private javax.swing.JSeparator jSeparator21;
  private javax.swing.JSeparator jSeparator23;
  private javax.swing.JSeparator jSeparator25;
  private javax.swing.JSeparator jSeparator27;
  private javax.swing.JSeparator jSeparator28;
  private javax.swing.JSeparator jSeparator29;
  private javax.swing.JSeparator jSeparator3;
  private javax.swing.JSeparator jSeparator4;
  private javax.swing.JSeparator jSeparator7;
  private javax.swing.JSeparator jSeparator8;
  private javax.swing.JTabbedPane jTabbedPane4;
  private javax.swing.JTabbedPane jTabbedPane5;
  private javax.swing.ButtonGroup keySigBtnGroup;
  private javax.swing.JTextField keySigTF;
  private javax.swing.JLabel keySignatureLabel;
  private javax.swing.JTextField keySignatureTF;
  private javax.swing.JLabel layoutLabel;
  private javax.swing.JTextField layoutTF;
  private javax.swing.JPanel leadsheetPreferences;
  private javax.swing.JPanel leadsheetSpecificPanel;
  private javax.swing.JLabel leadsheetTitleLabel;
  private javax.swing.JTextField leapProbField;
  private javax.swing.JLabel leapProbLabel;
  private javax.swing.JPanel lickGenPanel;
  private javax.swing.JTextArea lickGenSettings;
  private javax.swing.JScrollPane lickGenSettingsPane;
  private javax.swing.JLabel lickSavedLabel;
  private javax.swing.JLabel lineLabel;
  private javax.swing.JToggleButton loopButton;
  private javax.swing.JPanel loopPanel;
  private javax.swing.JTextField loopSet;
  private javax.swing.JPanel masterVolumePanel;
  private javax.swing.JTextField maxDurationField;
  private javax.swing.JTextField maxIntervalField;
  private javax.swing.JLabel maxLabel;
  private javax.swing.JTextField maxPitchField;
  private javax.swing.JLabel measErrorLabel;
  private javax.swing.JCheckBoxMenuItem measureCstrLinesMI;
  private javax.swing.JLabel measuresPerPartLabel;
  private javax.swing.JTextField measuresTF;
  private javax.swing.JCheckBox melodyInstAllChangeCB;
  private javax.swing.JPanel melodyInstPanel;
  private javax.swing.JLabel melodyInsttLabel;
  private javax.swing.JCheckBox melodyMute;
  private javax.swing.JScrollPane melodyNotation;
  private javax.swing.JTextArea melodyNotationHelp;
  private javax.swing.JPanel melodyPanel;
  private javax.swing.JSlider melodyVolume;
  private javax.swing.JMenuBar menuBar;
  private javax.swing.JTextField metreTF;
  private javax.swing.JTextField minDurationField;
  private javax.swing.JTextField minIntervalField;
  private javax.swing.JLabel minLabel;
  private javax.swing.JTextField minPitchField;
  private javax.swing.JButton mixerBtn;
  private javax.swing.JDialog mixerDialog;
  private javax.swing.JDialog newScoreDialog;
  private javax.swing.JPanel newScorePanel;
  private javax.swing.JButton okErrorBtn;
  private javax.swing.JButton okMeasBtn;
  private javax.swing.JButton okNewScoreBtn;
  private javax.swing.JMenuItem oneAutoMI;
  protected javax.swing.JFrame overrideFrame;
  private javax.swing.JMenuItem overrideMeasPMI;
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
  private javax.swing.JLabel pitchLabel;
  private javax.swing.JMenuItem playAllMI;
  private javax.swing.JButton playBtn;
  private javax.swing.JButton playLickButton;
  private javax.swing.JMenu playMenu;
  private javax.swing.JTextField playTransposed;
  private javax.swing.JPanel playbackPanel;
  private javax.swing.JSlider playbackSlider;
  private javax.swing.JLabel playbackTime;
  private javax.swing.JLabel playbackTotalTime;
  protected javax.swing.JPopupMenu popupMenu;
  private javax.swing.JTextField prefMeasTF;
  private javax.swing.ButtonGroup prefsTabBtnGrp;
  private javax.swing.JButton redoBtn;
  private javax.swing.JMenuItem redoMI;
  private javax.swing.JMenuItem redoPMI;
  private javax.swing.JTextField restProbField;
  private javax.swing.JLabel restProbLabel;
  private javax.swing.JMenuItem reverseMelody;
  private javax.swing.JTextArea rhythmField;
  private javax.swing.JScrollPane rhythmScrollPane;
  private javax.swing.JComboBox rootComboBox;
  private javax.swing.JLabel rootLabel;
  private javax.swing.JButton saveLickButton;
  private javax.swing.JTextField saveLickTF;
  private javax.swing.JMenuItem saveSelectionAsLick;
  private javax.swing.ButtonGroup saveTypeButtonGroup;
  private javax.swing.JComboBox scaleComboBox;
  private javax.swing.JLabel scaleLabel;
  private javax.swing.JLabel scaleToneProbLabel;
  private javax.swing.JTextField scaleToneWeightField;
  private javax.swing.JTabbedPane scoreTab;
  private javax.swing.JTextField scoreTitleTF;
  private javax.swing.JMenuItem selectAllMI;
  private javax.swing.JRadioButton sharpsRBtn;
  private javax.swing.JCheckBoxMenuItem showEmptyTitlesMI;
  private javax.swing.JCheckBoxMenuItem showTitlesMI;
  private javax.swing.JToolBar standardToolbar;
  private javax.swing.ButtonGroup staveButtonGroup;
  private javax.swing.JPanel staveButtonPanel;
  private javax.swing.ButtonGroup staveChoiceButtonGroup;
  private javax.swing.JMenu staveMenu;
  private javax.swing.JMenu staveTypeMenu;
  private javax.swing.JToggleButton stepInputBtn;
  private javax.swing.JButton stopBtn;
  private javax.swing.JButton stopLickButton;
  private javax.swing.JMenuItem stopPlayMI;
  private javax.swing.JTextArea styleHelpList1;
  private javax.swing.JScrollPane styleHelpPane;
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
  private javax.swing.JTextField titleTF;
  private javax.swing.JPanel toolbarPanel;
  private javax.swing.JTextField totalBeatsField;
  private javax.swing.JLabel totalBeatsLabel;
  private javax.swing.JMenuItem transposeBothDownSemitone;
  private javax.swing.JMenuItem transposeBothUpSemitone;
  private javax.swing.JMenuItem transposeChordsDownSemitone;
  private javax.swing.JMenuItem transposeChordsUpSemitone;
  private javax.swing.JMenuItem transposeMelodyDownOctave;
  private javax.swing.JMenuItem transposeMelodyDownSemitone;
  private javax.swing.JMenuItem transposeMelodyUpOctave;
  private javax.swing.JMenuItem transposeMelodyUpSemitone;
  private javax.swing.JRadioButton trebleStaveBtn;
  private javax.swing.JRadioButtonMenuItem trebleStaveMI;
  private javax.swing.JPanel triageOptionsPanel;
  private javax.swing.JLabel typeLabel;
  private javax.swing.JButton undoBtn;
  private javax.swing.JMenuItem undoMI;
  private javax.swing.JMenuItem undoPMI;
  private javax.swing.JMenu viewMenu;
  private javax.swing.JLabel weightLabel;
  private javax.swing.JMenu windowMenu;
  private javax.swing.JSeparator windowMenuSeparator;
  // End of variables declaration//GEN-END:variables
  
  
  
  
  public JPanel getToolbarPanel() {
      
      return toolbarPanel;
      
  }
  
  
  
  public JTextField getTextEntry()
  
  {
      
      return textEntry;
      
  }
  
  
  
  public boolean getScaleTonesSelected() {
      
      return false; //drawScaleTonesCheckBox.isSelected();
      
  }
  
  
  
  public boolean getChordTonesSelected() {
      
      return false; //drawChordTonesCheckBox.isSelected();
      
  }
  
  
  
  public boolean getColorTonesSelected() {
      
      return false; //drawColorTonesCheckBox.isSelected();
      
  }
  
  
  
  public String getGrammarFileName() {
      
      return grammarFile;
      
  }
  
  
  
  public void reloadGrammar() {
      
      
  }
  
  
  
  void playbackGoToTab(int tab) {
      
      if(tab >= scoreTab.getTabCount())
          
          return;
      
      
      
      currTabIndex = tab;
      
      scoreTab.setSelectedIndex(tab);
      
      staveRequestFocus();
      
  }
  
  
  
  public void checkFakeModalDialog() {
      
      Trace.log(2, "Notate: focusing fakeModalDialog");
      
      ((CapturingGlassPane) getRootPane().getGlassPane()).focus();
      
  }
  
  
  
  public void showFakeModalDialog(JDialog d) {
      
      Trace.log(2, "Notate: showFakeModalDialog() - " + d);
      
      CapturingGlassPane gp = (CapturingGlassPane) getRootPane().getGlassPane();
      
      
      
      gp.setFocusOn(d);
      
      d.setVisible(true);
      
  }
  
  
  
  public void hideFakeModalDialog(JDialog d) {
      
      Trace.log(2, "Notate: hideFakeModalDialog() - " + d);
      
      CapturingGlassPane gp = (CapturingGlassPane) getRootPane().getGlassPane();
      
      gp.setFocusOn(null);
      
      d.setVisible(false);
      
  }
  
  private boolean bernoulli(double prob) {
      return Math.random() > (1. - prob);
  }
  
  
  
  private class CapturingGlassPane extends JComponent implements MouseListener, MouseMotionListener, FocusListener {
      
      JDialog focusItem;
      
      public CapturingGlassPane() {
          
          addMouseListener(this);
          
          addMouseMotionListener(this);
          
          addFocusListener(this);
          
      }
      
      
      
      public void setFocusOn(JDialog c) {
          
          focusItem = c;
          
          setVisible(c != null);
          
      }
      
      
      
      public void focus() {
          
          if(focusItem != null && focusItem.isVisible())
              
              focusItem.requestFocus();
          
          else
              
              setFocusOn(null);
          
      }
      
      
      
      public void mouseClicked(MouseEvent e) { focus(); }
      
      
      
      public void mousePressed(MouseEvent e) { focus(); }
      
      
      
      public void mouseReleased(MouseEvent e) { focus(); }
      
      
      
      public void mouseEntered(MouseEvent e) { }
      
      
      
      public void mouseExited(MouseEvent e) { }
      
      
      
      public void mouseDragged(MouseEvent e) { focus(); }
      
      
      
      public void mouseMoved(MouseEvent e) { }
      
      
      
      public void focusGained(FocusEvent e) { focus(); }
      
      
      
      public void focusLost(FocusEvent e) {}
      
  }
  
  
  
  /** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
   *
   * static window management functions
   *
   */
  
  
  
  /**
   *
   * class for containing a single window instance
   *
   *    contains:
   *
   *      a reference to the notate object
   *
   *      a JMenuItem for the Window menu
   *
   *      an identifying number for distinguishing windows with the same title
   *
   */
  
  private static class WindowMenuItem {
      
      private MiniNotate window;
      
      private JMenuItem menuItem;
      
      private int number;
      
      
      
      /**
       *
       * constructs the Window menu JMenuItem for a particular Notate object
       *
       */
      
      public WindowMenuItem(MiniNotate w) {
          
          window = w;
          
          menuItem = new JMenuItem();
          
          number = windowNumber++;
          
          
          
          // on click, call the focus method which brings the window to front
          
          menuItem.addActionListener(new ActionListener() {
              
              public void actionPerformed(java.awt.event.ActionEvent evt) {
                  
                  window.focus();
                  
              }
              
          });
          
          
          
          // add a mnemonic if it is a known number
          
          switch(number) {
              
              case 1: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_1); break;
              
              case 2: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_2); break;
              
              case 3: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_3); break;
              
              case 4: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_4); break;
              
              case 5: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_5); break;
              
              case 6: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_6); break;
              
              case 7: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_7); break;
              
              case 8: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_8); break;
              
              case 9: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_9); break;
              
              case 10: menuItem.setMnemonic(java.awt.event.KeyEvent.VK_0); break;
              
              default: break;
              
          }
          
      }
      
      
      
      // get the menu item for this object
      
      public JMenuItem getMI() {
          
          return getMI(null);
          
      }
      
      
      
      public JMenuItem getMI(MiniNotate current) {
          
          String title = window.getTitle();
          
          if(title.equals(""))
              
              title = "Untitled Leadsheet";
          
          
          
          if(current == window) {
              
              menuItem.setText(number + ": " + title + " (current window)");
              
          } else {
              
              menuItem.setText(number + ": " + title);
              
          }
          
          return menuItem;
          
      }
      
      
      
      public MiniNotate getWindow() {
          
          return window;
          
      }
      
  }
  
  
  
  /**
   *
   * A vector to hold all the windows
   *
   */
  
  private static Vector<WindowMenuItem> window = new Vector<WindowMenuItem>();
  
  
  
  /**
   *
   * A counter for assigning a unique id to each new window
   *
   */
  
  private static int windowNumber = 1;
  
  
  
  /**
   *
   * A function to register a window (used by Notate's constructor)
   *
   * Adds the window to the vector of windows, allowing it to appear in the Window menu
   *
   */
  
  private static void registerWindow(MiniNotate w) {
      
      Trace.log(2, "Notate: window registered: " + w);
      
      WindowMenuItem wmi = new WindowMenuItem(w);
      
      window.add(wmi);
      
  }
  
  
  
  /**
   *
   * A function to unregister a window (when close window is called)
   *
   * Handles removal of a window from the vector of windows
   *
   * Closes midi devices and exits when all windows are removed from the
   *
   * vector of windows
   *
   */
  
  private static void unregisterWindow(MiniNotate w) {
      
      Trace.log(2, "Notate: window unregistered: " + w);
      
      
      
      for(WindowMenuItem i : window) {
          
          if(i.getWindow() == w) {
              
              window.remove(i);
              
              break;
              
          }
          
      }
      
      
      
      if(window.size() == 0) {
          
          Trace.log(2, "Notate: No more registered windows, exiting.");
          
          imp.ImproVisor.getMidiManager().closeDevices();
          
          System.exit(0);
          
      }
      
  }
  
  
  
  /**
   *
   * Organizes windows
   *
   */
  
  public static void cascadeWindows(MiniNotate w) {
      
      Trace.log(2, "Notate: cascadeWindows()");
      
      
      
      int x = 0, y = 0;
      
      int diff = 40; // each window is cascaded by this amount in the x and y direction from the previous window
      
      
      
      for(WindowMenuItem i : window) {
          
          if(i.getWindow() == w)
              
              continue;
          
          
          
          i.getWindow().setLocation(x, y);
          
          i.getWindow().toFront();
          
          x += diff;
          
          y += diff;
          
      }
      
      
      
      w.setLocation(x, y);
      
      w.focus();
      
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
          return loopCount-1; // let 1 mean only once, not twice, etc.
        }
      }
        else
        {
        return 0; // don't loop
        }
      }

    public void setToLoop(boolean value)
      {
      toLoop = value;
      }

    public boolean getToLoop()
      {
      return toLoop;
      }
    
    public void setLoopCount(int value)
      {
      loopCount = value;
      }
  
  
}

