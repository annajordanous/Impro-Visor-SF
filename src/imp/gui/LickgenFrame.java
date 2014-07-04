/**
 * This Java Class is part of the Impro-Visor Application.
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

/*
 * LickgenFrame.java
 *
 * Created on Jun 24, 2010, 4:57:53 PM
 */

package imp.gui;

import imp.Directories;
import imp.ImproVisor;
import imp.cluster.PolylistComparer;
import imp.com.*;
import imp.data.*;
import imp.lickgen.Grammar;
import imp.lickgen.LickGen;
import imp.neuralnet.Critic;
import imp.util.ProfileFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeMap;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.metal.MetalButtonUI;
import javax.swing.table.DefaultTableModel;
import polya.Polylist;
import polya.Tokenizer;


/**
 *
 * @author David Morrison, Robert Keller
 */
public class LickgenFrame
    extends javax.swing.JFrame
    implements imp.Constants
{

private int themeLength = 8;

private double themeProb = 0.4;

private double transposeProb = 0.5;

private double invertProb = 0.1;

private double reverseProb = 0.1;

private Notate notate;

private ArrayList<String> melodyData = new ArrayList<String>();

private double roundTo = BEAT;

private int paddingSlots = BEAT / 2;

private int minPitch = 60;

private int maxPitch = 82;

private int minInterval = 0;

private int maxInterval = 6;

private int minDuration = 8;

private int maxDuration = 8;

private double totalBeats = 8;

private int totalSlots = (int) (BEAT * totalBeats);

private int slotsPerBeat = 120;

private double restProb = 0.1;

private double leapProb = 0.2;

private double chordToneWeight = 0.7;

private double scaleToneWeight = 0.1;

private double colorToneWeight = 0.05;

private double chordToneDecayRate = 0.1;

private boolean avoidRepeats = true;

private boolean useGrammar = true;

private boolean autoFill = true;

private int recurrentIteration = 1;

private LickGen lickgen;

private CommandManager cm;

// Complexity graph variables
private int numControllers;
private int curController;
private ComplexityWindowController[] compControllers;

/** Number of beats per measure in the piece */
private int beatsPerBar;

/** Total number of beats to represent in the solo curve graph */
private int attrTotal;

/** Granularity at which to look at the bars, i.e. how many beats per division */
private int attrGranularity; 

/** File extension for solo profiles */
private String profileExt;

/** Default profile curve for the reset button */
private File defaultProfile;

/** JFile Chooser for saving solo profiles */
private JFileChooser saveCWFC;

/** JFile Chooser for opening solo profiles */
private JFileChooser openCWFC;

private boolean rectify = true;

private boolean useCritic = false;

private static final int DEFAULT_GRADE = 7; //Default criticGrade for filter
private int criticGrade = DEFAULT_GRADE;

private boolean continuallyGenerate = true;

/**
 * ArrayList of JTextField arrays, used to display probabilities used in lick generation
 */

private ArrayList<JTextField[]> lickPrefs = new ArrayList<JTextField[]>();

/**
 * this will be set to true during extraction of all measures in a corpus
 */
private boolean allMeasures = false;

/*
 * Initialize critic, from Notate leadsheet.
 */
 private Critic critic;
 
 /*
  * TreeMap for usage with style recognition
  */
 private TreeMap<String, Critic> critics;
 
 /*
  * Number of expected weight files, will be used to encourage users to
  * download the rest of the weights if they desire style recognition
  */
 private static int numCritics = 22;
 
  /**
  * Create the panel for the substitutor
  */
 private SubstitutorTabPanel substitutorTab;

/**
 * Creates new LickgenFrame
 */
public LickgenFrame(Notate notate, LickGen lickgen, CommandManager cm)
  {
    this.notate = notate;
    this.lickgen = lickgen;
    this.cm = cm;

    //beatsPerBar = notate.getMelodyPart().getMetre()[0]; //get the top number in the time sig
    beatsPerBar = 4; //default for now, since it appears there's no way to get the actual time sig at init time
    attrTotal = 288; //max size of a selection (one chorus)
    attrGranularity = 1; //default

    critic = notate.getCritic();
    initComponents();
    
    substitutorTab = new SubstitutorTabPanel(lickgen, notate);
    substitutorPanel.add(substitutorTab, new GridLayout(1,1,1,1));
   }

public void applySubstitutions(MelodyPart part)
{
    substitutorTab.applySubstitutionsToPart(part);
}
/**
 * Initializes the solo profile file choosers.
 */
private void initCompFileChoosers() {
    ProfileFilter pFilter = new ProfileFilter();
    profileExt = ProfileFilter.EXTENSION;
    defaultProfile = new File(ImproVisor.getProfileDirectory(), "default." + profileExt);

    saveCWFC = new JFileChooser();
    openCWFC = new JFileChooser();

    saveCWFC.setDialogType(JFileChooser.SAVE_DIALOG);
    saveCWFC.setDialogTitle("Save Solo Profile");
    saveCWFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
    saveCWFC.resetChoosableFileFilters();
    saveCWFC.addChoosableFileFilter(pFilter);

    openCWFC.setDialogType(JFileChooser.OPEN_DIALOG);
    openCWFC.setDialogTitle("Open Solo Profile");
    openCWFC.setFileSelectionMode(JFileChooser.FILES_ONLY);
    openCWFC.resetChoosableFileFilters();
    openCWFC.addChoosableFileFilter(pFilter);
}


/** This method is called from within the constructor to
 * initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is
 * always regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        generatorPane = new javax.swing.JTabbedPane();
        lickGenPanel = new javax.swing.JPanel();
        rhythmPanel = new javax.swing.JPanel();
        rhythmScrollPane = new javax.swing.JScrollPane();
        rhythmField = new javax.swing.JTextArea();
        lickGenerationButtonsPanel = new javax.swing.JPanel();
        generateLickButton = new javax.swing.JButton();
        genRhythmButton = new javax.swing.JButton();
        fillMelodyButton = new javax.swing.JButton();
        getAbstractMelodyButton = new javax.swing.JButton();
        getSelRhythmButton = new javax.swing.JButton();
        playLickButton = new javax.swing.JButton();
        stopLickButton = new javax.swing.JButton();
        saveLickButton = new javax.swing.JButton();
        lickgenParametersPanel = new javax.swing.JPanel();
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
        avoidRepeatsCheckbox = new javax.swing.JCheckBox();
        recurrentCheckbox = new javax.swing.JCheckBox();
        generationGapLabel = new javax.swing.JLabel();
        gapField = new javax.swing.JTextField();
        rectifyCheckBox = new javax.swing.JCheckBox();
        useSoloistCheckBox = new javax.swing.JCheckBox();
        useHeadCheckBox = new javax.swing.JCheckBox();
        regenerateHeadDataBtn = new javax.swing.JButton();
        continuallyGenerateCheckBox = new javax.swing.JCheckBox();
        generationSelectionButton = new javax.swing.JButton();
        styleRecognitionButton = new javax.swing.JButton();
        toneProbabilityPanel = new javax.swing.JPanel();
        chordToneProbLabel = new javax.swing.JLabel();
        colorToneProbLabel = new javax.swing.JLabel();
        scaleToneProbLabel = new javax.swing.JLabel();
        chordToneDecayRateLabel = new javax.swing.JLabel();
        chordToneWeightField = new javax.swing.JTextField();
        colorToneWeightField = new javax.swing.JTextField();
        scaleToneWeightField = new javax.swing.JTextField();
        chordToneDecayField = new javax.swing.JTextField();
        scaleChoicePanel = new javax.swing.JPanel();
        scaleLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        rootLabel = new javax.swing.JLabel();
        rootComboBox = new javax.swing.JComboBox();
        lickGradeButtonsPanel = new javax.swing.JPanel();
        lickSavedLabel = new javax.swing.JLabel();
        gradeLabel = new javax.swing.JLabel();
        saveLickTF = new javax.swing.JTextField();
        saveLickWithLabelLabel = new javax.swing.JLabel();
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
        gradeBadBtn = new javax.swing.JButton();
        gradeAverageBtn = new javax.swing.JButton();
        gradeGoodBtn = new javax.swing.JButton();
        ProbFillClearPanel = new javax.swing.JPanel();
        clearProbsButton = new javax.swing.JButton();
        FillProbsButton = new javax.swing.JButton();
        autoFillCheckBox = new javax.swing.JCheckBox();
        pitchProbabilitiesPanel = new javax.swing.JPanel();
        chordProbPanel = new javax.swing.JPanel();
        soloCorrectionPanel = new javax.swing.JPanel();
        offsetByMeasureGradeSoloButton = new javax.swing.JButton();
        forwardGradeSoloButton = new javax.swing.JButton();
        backwardGradeSoloButton = new javax.swing.JButton();
        resetSelectionButton = new javax.swing.JButton();
        gradeAllMeasuresButton = new javax.swing.JButton();
        regenerateLickForSoloButton = new javax.swing.JButton();
        gradeLickFromStaveButton = new javax.swing.JButton();
        lickFromStaveGradeTextField = new javax.swing.JTextField();
        useCriticCheckBox = new javax.swing.JCheckBox();
        criticGradeTextField = new javax.swing.JTextField();
        counterForCriticTextField = new javax.swing.JTextField();
        criticGradeLabel = new javax.swing.JLabel();
        counterForCriticLabel = new javax.swing.JLabel();
        loadRandomGrammarButton = new javax.swing.JButton();
        grammarLearningPanel = new javax.swing.JPanel();
        finalLabel = new javax.swing.JLabel();
        windowParametersPanel = new javax.swing.JPanel();
        windowSizeLabel = new javax.swing.JLabel();
        windowSlideLabel = new javax.swing.JLabel();
        numClusterRepsLabel = new javax.swing.JLabel();
        windowSizeField = new javax.swing.JTextField();
        windowSlideField = new javax.swing.JTextField();
        useRelativeCheckbox = new javax.swing.JCheckBox();
        numClusterRepsField = new javax.swing.JTextField();
        useMarkovCheckbox = new javax.swing.JCheckBox();
        MarkovLengthField = new javax.swing.JTextField();
        loadBaseGrammarBtn = new javax.swing.JButton();
        saveGrammarAsButton = new javax.swing.JButton();
        openCorpusBtn = new javax.swing.JButton();
        toGrammarBtn = new javax.swing.JButton();
        learningStep0Label = new javax.swing.JLabel();
        testGeneration = new javax.swing.JButton();
        soloGenPanel = new javax.swing.JPanel();
        generateSoloButton = new javax.swing.JButton();
        generateThemeButton = new javax.swing.JButton();
        themeField = new javax.swing.JTextField();
        themeProbabilityField = new javax.swing.JTextField();
        themeProbabilityField.setText(themeProb + "");
        themeLengthField = new javax.swing.JTextField();
        themeLengthField.setText(themeLength + "");
        themeLengthLabel = new javax.swing.JLabel();
        themeProbLabel = new javax.swing.JLabel();
        themeLabel = new javax.swing.JLabel();
        genSoloThemeBtn = new javax.swing.JButton();
        transposeProbLabel = new javax.swing.JLabel();
        InvertProbLabel = new javax.swing.JLabel();
        ReverseProbLabel = new javax.swing.JLabel();
        pasteThemeBtn = new javax.swing.JButton();
        playSoloBtn = new javax.swing.JButton();
        stopSoloPlayBtn = new javax.swing.JButton();
        transposeProbabilityField = new javax.swing.JTextField();
        transposeProbabilityField.setText(transposeProb + "");
        invertProbabilityField = new javax.swing.JTextField();
        invertProbabilityField.setText(invertProb + "");
        reverseProbabilityField = new javax.swing.JTextField();
        reverseProbabilityField.setText(reverseProb + "");
        disclaimer = new javax.swing.JLabel();
        neuralNetworkPanel = new javax.swing.JPanel();
        nnetOutputPanel = new javax.swing.JPanel();
        nnetScrollPane = new javax.swing.JScrollPane();
        nnetOutputTextField = new javax.swing.JTextArea();
        layerInfoScrollPane = new javax.swing.JScrollPane();
        layerDataTable = new javax.swing.JTable();
        nnetWeightGenerationPanel = new javax.swing.JPanel();
        generateWeightFileButton = new javax.swing.JButton();
        getNetworkStatsButton = new javax.swing.JButton();
        clearWeightFileButton = new javax.swing.JButton();
        loadWeightFileButton = new javax.swing.JButton();
        resetNnetInstructionsButton = new javax.swing.JButton();
        resetDefaultValuesButton = new javax.swing.JButton();
        resetNetworkButton = new javax.swing.JButton();
        nnetParametersPanel = new javax.swing.JPanel();
        trainingFileButton = new javax.swing.JButton();
        trainingFileTextField = new javax.swing.JTextField();
        epochLimitLabel = new javax.swing.JLabel();
        epochLimitTextField = new javax.swing.JTextField();
        learningRateLabel = new javax.swing.JLabel();
        learningRateTextField = new javax.swing.JTextField();
        mseGoalLabel = new javax.swing.JLabel();
        mseGoalTextField = new javax.swing.JTextField();
        modeLabel = new javax.swing.JLabel();
        modeComboBox = new javax.swing.JComboBox();
        weightFileTextField = new javax.swing.JTextField();
        numberOfLayersLabel = new javax.swing.JLabel();
        numberOfLayersTextField = new javax.swing.JTextField();
        addLayerToTableButton = new javax.swing.JButton();
        removeLayerFromTableButton = new javax.swing.JButton();
        moveLayerUpTableButton = new javax.swing.JButton();
        moveLayerDownTableButton = new javax.swing.JButton();
        weightFileButton = new javax.swing.JButton();
        substitutorPanel = new javax.swing.JPanel();
        generatorMenuBar1 = new javax.swing.JMenuBar();
        grammarMenu1 = new javax.swing.JMenu();
        openGrammarMI1 = new javax.swing.JMenuItem();
        showLogMI1 = new javax.swing.JMenuItem();
        saveGrammarMI1 = new javax.swing.JMenuItem();
        editGrammarMI1 = new javax.swing.JMenuItem();
        reloadGrammarMI1 = new javax.swing.JMenuItem();
        toCriticMI1 = new javax.swing.JCheckBoxMenuItem();
        showCriticMI1 = new javax.swing.JMenuItem();
        useGrammarMI1 = new javax.swing.JCheckBoxMenuItem();
        generatorWindowMenu1 = new javax.swing.JMenu();
        closeWindowMI2 = new javax.swing.JMenuItem();
        cascadeMI2 = new javax.swing.JMenuItem();
        windowMenuSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Lick Generator Controls");
        setMinimumSize(new java.awt.Dimension(1000, 800));
        setPreferredSize(new java.awt.Dimension(1000, 800));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                closeWindow(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        generatorPane.setMinimumSize(new java.awt.Dimension(900, 700));
        generatorPane.setPreferredSize(new java.awt.Dimension(950, 700));

        lickGenPanel.setLayout(new java.awt.GridBagLayout());

        rhythmPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Abstract Melody", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        rhythmPanel.setMinimumSize(new java.awt.Dimension(850, 200));
        rhythmPanel.setPreferredSize(new java.awt.Dimension(850, 200));
        rhythmPanel.setLayout(new java.awt.GridBagLayout());

        rhythmScrollPane.setBorder(null);
        rhythmScrollPane.setMinimumSize(new java.awt.Dimension(223, 180));
        rhythmScrollPane.setPreferredSize(new java.awt.Dimension(223, 180));

        rhythmField.setColumns(20);
        rhythmField.setLineWrap(true);
        rhythmField.setRows(500);
        rhythmField.setBorder(null);
        rhythmField.setMinimumSize(new java.awt.Dimension(800, 100));
        rhythmField.setPreferredSize(new java.awt.Dimension(800, 1000));
        rhythmScrollPane.setViewportView(rhythmField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        rhythmPanel.add(rhythmScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        lickGenPanel.add(rhythmPanel, gridBagConstraints);

        lickGenerationButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lick Generation and Extraction", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        lickGenerationButtonsPanel.setMinimumSize(new java.awt.Dimension(300, 230));
        lickGenerationButtonsPanel.setPreferredSize(new java.awt.Dimension(300, 230));
        lickGenerationButtonsPanel.setLayout(new java.awt.GridBagLayout());

        generateLickButton.setText("Generate Melody");
        generateLickButton.setToolTipText("Generate a melody using the current grammar.");
        generateLickButton.setMaximumSize(new java.awt.Dimension(135, 29));
        generateLickButton.setMinimumSize(new java.awt.Dimension(135, 29));
        generateLickButton.setPreferredSize(new java.awt.Dimension(135, 29));
        generateLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(generateLickButton, gridBagConstraints);

        genRhythmButton.setText("Generate Abstract Melody Only");
        genRhythmButton.setToolTipText("Generate the rhythm pattern for a lick, without the actual notes.");
        genRhythmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genRhythmButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(genRhythmButton, gridBagConstraints);

        fillMelodyButton.setText("Fill Abstract Melody");
        fillMelodyButton.setToolTipText("Fill the notes for the given pattern.");
        fillMelodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fillMelodyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(fillMelodyButton, gridBagConstraints);

        getAbstractMelodyButton.setText("Extract Abstract Melody");
        getAbstractMelodyButton.setToolTipText("Extract the rhythm from the leadsheet.");
        getAbstractMelodyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getAbstractMelodyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(getAbstractMelodyButton, gridBagConstraints);

        getSelRhythmButton.setText("Extract Rhythm");
        getSelRhythmButton.setToolTipText("Extract the rhythm from the leadsheet.");
        getSelRhythmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getSelRhythmButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(getSelRhythmButton, gridBagConstraints);

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
        lickGenerationButtonsPanel.add(playLickButton, gridBagConstraints);

        stopLickButton.setText("Stop");
        stopLickButton.setToolTipText("Stop playing.");
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
        lickGenerationButtonsPanel.add(stopLickButton, gridBagConstraints);

        saveLickButton.setText("Save");
        saveLickButton.setToolTipText("Save the lick in the vocabulary.");
        saveLickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveLickButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        lickGenerationButtonsPanel.add(saveLickButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        lickGenPanel.add(lickGenerationButtonsPanel, gridBagConstraints);

        lickgenParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Generation Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        lickgenParametersPanel.setMinimumSize(new java.awt.Dimension(520, 220));
        lickgenParametersPanel.setPreferredSize(new java.awt.Dimension(520, 220));
        lickgenParametersPanel.setLayout(new java.awt.GridBagLayout());

        pitchLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        pitchLabel.setText("Pitch");
        pitchLabel.setToolTipText("Pitch of a note in the lick\n");
        pitchLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pitchLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(pitchLabel, gridBagConstraints);

        maxLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        maxLabel.setText("Max");
        maxLabel.setToolTipText("");
        maxLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        maxLabel.setMaximumSize(new java.awt.Dimension(30, 15));
        maxLabel.setMinimumSize(new java.awt.Dimension(30, 15));
        maxLabel.setPreferredSize(new java.awt.Dimension(30, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lickgenParametersPanel.add(maxLabel, gridBagConstraints);

        maxPitchField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        maxPitchField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxPitchField.setToolTipText("The maximum pitch in a generated lick.");
        maxPitchField.setMinimumSize(new java.awt.Dimension(60, 24));
        maxPitchField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(maxPitchField, gridBagConstraints);

        minLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        minLabel.setText("Min");
        minLabel.setToolTipText("");
        minLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        minLabel.setMaximumSize(new java.awt.Dimension(30, 15));
        minLabel.setMinimumSize(new java.awt.Dimension(30, 15));
        minLabel.setPreferredSize(new java.awt.Dimension(30, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        lickgenParametersPanel.add(minLabel, gridBagConstraints);

        minPitchField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        minPitchField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        minPitchField.setToolTipText("The minimum pitch in a generated lick.");
        minPitchField.setMinimumSize(new java.awt.Dimension(60, 24));
        minPitchField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(minPitchField, gridBagConstraints);

        intervalLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        intervalLabel.setText("Interval");
        intervalLabel.setToolTipText("The maximum interval between two pitches in the lick");
        intervalLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        intervalLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(intervalLabel, gridBagConstraints);

        minIntervalField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        minIntervalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        minIntervalField.setToolTipText("The minimum interval from one note to the next, if not a leap.");
        minIntervalField.setMinimumSize(new java.awt.Dimension(60, 24));
        minIntervalField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(minIntervalField, gridBagConstraints);

        maxIntervalField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        maxIntervalField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxIntervalField.setToolTipText("The maximum interval from one note to the next, if not a leap.");
        maxIntervalField.setMinimumSize(new java.awt.Dimension(60, 24));
        maxIntervalField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(maxIntervalField, gridBagConstraints);

        durationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        durationLabel.setText("Duration");
        durationLabel.setToolTipText("Duration of beats in generated lick\n");
        durationLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        durationLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(durationLabel, gridBagConstraints);

        minDurationField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        minDurationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        minDurationField.setToolTipText("The minimum duration of a generated note.");
        minDurationField.setEnabled(false);
        minDurationField.setMinimumSize(new java.awt.Dimension(60, 24));
        minDurationField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(minDurationField, gridBagConstraints);

        maxDurationField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        maxDurationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxDurationField.setToolTipText("The minimum duration of a generated note.");
        maxDurationField.setEnabled(false);
        maxDurationField.setMinimumSize(new java.awt.Dimension(60, 24));
        maxDurationField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 5, 10);
        lickgenParametersPanel.add(maxDurationField, gridBagConstraints);

        totalBeatsField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        totalBeatsField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        totalBeatsField.setToolTipText("The number of beats in the lick.");
        totalBeatsField.setMinimumSize(new java.awt.Dimension(60, 24));
        totalBeatsField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        lickgenParametersPanel.add(totalBeatsField, gridBagConstraints);

        totalBeatsLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        totalBeatsLabel.setText("Generate Beats");
        totalBeatsLabel.setToolTipText("The total number of beats for the lick.");
        totalBeatsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        totalBeatsLabel.setMaximumSize(new java.awt.Dimension(160, 16));
        totalBeatsLabel.setMinimumSize(new java.awt.Dimension(160, 16));
        totalBeatsLabel.setPreferredSize(new java.awt.Dimension(160, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
        lickgenParametersPanel.add(totalBeatsLabel, gridBagConstraints);

        restProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        restProbLabel.setText("Rest Probability");
        restProbLabel.setToolTipText("The probability of generating a rest");
        restProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        restProbLabel.setPreferredSize(new java.awt.Dimension(80, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 2, 10);
        lickgenParametersPanel.add(restProbLabel, gridBagConstraints);

        restProbField.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        restProbField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        restProbField.setToolTipText("The probability of a rest vs. a note.");
        restProbField.setEnabled(false);
        restProbField.setMinimumSize(new java.awt.Dimension(60, 24));
        restProbField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        lickgenParametersPanel.add(restProbField, gridBagConstraints);

        leapProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        leapProbLabel.setText("Leap Probability");
        leapProbLabel.setMaximumSize(new java.awt.Dimension(220, 16));
        leapProbLabel.setMinimumSize(new java.awt.Dimension(220, 16));
        leapProbLabel.setPreferredSize(new java.awt.Dimension(220, 16));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        lickgenParametersPanel.add(leapProbLabel, gridBagConstraints);

        leapProbField.setToolTipText("The probability of making a leap outside the maximum interval.");
        leapProbField.setMaximumSize(new java.awt.Dimension(60, 2147483647));
        leapProbField.setMinimumSize(new java.awt.Dimension(60, 24));
        leapProbField.setPreferredSize(new java.awt.Dimension(60, 24));
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
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(leapProbField, gridBagConstraints);

        avoidRepeatsCheckbox.setSelected(true);
        avoidRepeatsCheckbox.setToolTipText("Avoid generating repeated pitches");
        avoidRepeatsCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        avoidRepeatsCheckbox.setLabel("Avoid repeat pitches");
        avoidRepeatsCheckbox.setMaximumSize(new java.awt.Dimension(220, 22));
        avoidRepeatsCheckbox.setMinimumSize(new java.awt.Dimension(220, 22));
        avoidRepeatsCheckbox.setPreferredSize(new java.awt.Dimension(220, 22));
        avoidRepeatsCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                avoidRepeatsCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        lickgenParametersPanel.add(avoidRepeatsCheckbox, gridBagConstraints);

        recurrentCheckbox.setText("Recurrent");
        recurrentCheckbox.setToolTipText("If checked, keep generating licks until stop is pressed. Licks may be recovered using undo. This will eventually fill up memory.\n");
        recurrentCheckbox.setMaximumSize(new java.awt.Dimension(150, 23));
        recurrentCheckbox.setMinimumSize(new java.awt.Dimension(150, 23));
        recurrentCheckbox.setPreferredSize(new java.awt.Dimension(150, 23));
        recurrentCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                recurrentCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        lickgenParametersPanel.add(recurrentCheckbox, gridBagConstraints);
        recurrentCheckbox.getAccessibleContext().setAccessibleName("");

        generationGapLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        generationGapLabel.setText("Recurrent Lead (beats):");
        generationGapLabel.setToolTipText("Gap in beats before end of chorus, at which point the next chorus is generated\n");
        generationGapLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        generationGapLabel.setMaximumSize(new java.awt.Dimension(220, 16));
        generationGapLabel.setMinimumSize(new java.awt.Dimension(220, 16));
        generationGapLabel.setPreferredSize(new java.awt.Dimension(220, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 10);
        lickgenParametersPanel.add(generationGapLabel, gridBagConstraints);
        generationGapLabel.getAccessibleContext().setAccessibleName("");
        generationGapLabel.getAccessibleContext().setAccessibleDescription("");

        gapField.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        gapField.setText("0.99");
        gapField.setToolTipText("Sets the leading gap between when the next lick is generated and the previous one ends.");
        gapField.setMaximumSize(new java.awt.Dimension(45, 24));
        gapField.setMinimumSize(new java.awt.Dimension(45, 24));
        gapField.setPreferredSize(new java.awt.Dimension(45, 24));
        gapField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gapFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 10);
        lickgenParametersPanel.add(gapField, gridBagConstraints);

        rectifyCheckBox.setSelected(true);
        rectifyCheckBox.setText("Rectify\n");
        rectifyCheckBox.setToolTipText("Rectify the generated melody.\n");
        rectifyCheckBox.setMaximumSize(new java.awt.Dimension(160, 23));
        rectifyCheckBox.setMinimumSize(new java.awt.Dimension(160, 23));
        rectifyCheckBox.setPreferredSize(new java.awt.Dimension(160, 23));
        rectifyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rectifyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        lickgenParametersPanel.add(rectifyCheckBox, gridBagConstraints);

        useSoloistCheckBox.setText("Use Soloist");
        useSoloistCheckBox.setMaximumSize(new java.awt.Dimension(150, 23));
        useSoloistCheckBox.setMinimumSize(new java.awt.Dimension(150, 23));
        useSoloistCheckBox.setPreferredSize(new java.awt.Dimension(150, 23));
        useSoloistCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useSoloistCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(useSoloistCheckBox, gridBagConstraints);

        useHeadCheckBox.setLabel("Use Head");
        useHeadCheckBox.setMaximumSize(new java.awt.Dimension(140, 23));
        useHeadCheckBox.setMinimumSize(new java.awt.Dimension(140, 23));
        useHeadCheckBox.setPreferredSize(new java.awt.Dimension(140, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickgenParametersPanel.add(useHeadCheckBox, gridBagConstraints);

        regenerateHeadDataBtn.setText("Regenerate Head Data");
        regenerateHeadDataBtn.setMaximumSize(new java.awt.Dimension(180, 29));
        regenerateHeadDataBtn.setMinimumSize(new java.awt.Dimension(180, 29));
        regenerateHeadDataBtn.setPreferredSize(new java.awt.Dimension(180, 29));
        regenerateHeadDataBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regenerateHeadDataBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.2;
        lickgenParametersPanel.add(regenerateHeadDataBtn, gridBagConstraints);

        continuallyGenerateCheckBox.setSelected(true);
        continuallyGenerateCheckBox.setText("Continually Generate ");
        continuallyGenerateCheckBox.setToolTipText("After grading, continually generate new licks.");
        continuallyGenerateCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                continuallyGenerateCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        lickgenParametersPanel.add(continuallyGenerateCheckBox, gridBagConstraints);

        generationSelectionButton.setText("Size of Selection");
        generationSelectionButton.setToolTipText("Lock the selection for lick generation.");
        generationSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generationSelectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        lickgenParametersPanel.add(generationSelectionButton, gridBagConstraints);

        styleRecognitionButton.setText("Prepare Critics");
        styleRecognitionButton.setToolTipText("Attempts to guess the musician of the selection based off parellel trained networks.");
        styleRecognitionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                styleRecognitionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        lickgenParametersPanel.add(styleRecognitionButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        lickGenPanel.add(lickgenParametersPanel, gridBagConstraints);

        toneProbabilityPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pitch Category Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        toneProbabilityPanel.setMinimumSize(new java.awt.Dimension(500, 90));
        toneProbabilityPanel.setPreferredSize(new java.awt.Dimension(500, 90));
        toneProbabilityPanel.setLayout(new java.awt.GridBagLayout());

        chordToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chordToneProbLabel.setText("<html>Chord <br>Tone</html");
        chordToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(chordToneProbLabel, gridBagConstraints);

        colorToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        colorToneProbLabel.setText("<html>Color<br>Tone</html>");
        colorToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        colorToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(colorToneProbLabel, gridBagConstraints);

        scaleToneProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        scaleToneProbLabel.setText("<html>Scale <br>Tone</html>");
        scaleToneProbLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        scaleToneProbLabel.setPreferredSize(new java.awt.Dimension(65, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(scaleToneProbLabel, gridBagConstraints);

        chordToneDecayRateLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        chordToneDecayRateLabel.setText("<html><align=center>Chord Tone <br> Decay Rate </align></html>");
        chordToneDecayRateLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        chordToneDecayRateLabel.setMinimumSize(new java.awt.Dimension(120, 32));
        chordToneDecayRateLabel.setPreferredSize(new java.awt.Dimension(120, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        toneProbabilityPanel.add(chordToneDecayRateLabel, gridBagConstraints);

        chordToneWeightField.setToolTipText("The amount of weight to give to chord tones (vs. scale or color tones).");
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(chordToneWeightField, gridBagConstraints);

        colorToneWeightField.setToolTipText("The amount of weight to give to color tones (vs. chord or scale tones).");
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(colorToneWeightField, gridBagConstraints);

        scaleToneWeightField.setToolTipText("The amount of weight to give to scale tones (vs. chord or color tones).");
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(scaleToneWeightField, gridBagConstraints);

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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        toneProbabilityPanel.add(chordToneDecayField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        lickGenPanel.add(toneProbabilityPanel, gridBagConstraints);

        scaleChoicePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Scale Tone Type", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        scaleChoicePanel.setMinimumSize(new java.awt.Dimension(500, 100));
        scaleChoicePanel.setPreferredSize(new java.awt.Dimension(500, 100));
        scaleChoicePanel.setLayout(new java.awt.GridBagLayout());

        scaleLabel.setText("Scale:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 20);
        scaleChoicePanel.add(scaleLabel, gridBagConstraints);

        typeLabel.setText("Type:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        scaleChoicePanel.add(typeLabel, gridBagConstraints);

        scaleComboBox.setToolTipText("The type of scale to use in scale tones. The default is the first scale associated with the chord.\n");
        scaleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 5, 0);
        scaleChoicePanel.add(scaleComboBox, gridBagConstraints);

        rootLabel.setText("Root:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        scaleChoicePanel.add(rootLabel, gridBagConstraints);

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
        scaleChoicePanel.add(rootComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.4;
        lickGenPanel.add(scaleChoicePanel, gridBagConstraints);

        lickGradeButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lick Saving and Grading", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        lickGradeButtonsPanel.setMaximumSize(new java.awt.Dimension(500, 125));
        lickGradeButtonsPanel.setMinimumSize(new java.awt.Dimension(350, 100));
        lickGradeButtonsPanel.setPreferredSize(new java.awt.Dimension(350, 100));
        lickGradeButtonsPanel.setLayout(new java.awt.GridBagLayout());

        lickSavedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lickSavedLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        lickSavedLabel.setMinimumSize(new java.awt.Dimension(0, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 100;
        gridBagConstraints.weightx = 1.0;
        lickGradeButtonsPanel.add(lickSavedLabel, gridBagConstraints);

        gradeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        gradeLabel.setText("Save Lick with Grade:");
        gradeLabel.setToolTipText("Provides a grade for the quality of lick. Used in machine learning experiments.");
        gradeLabel.setMaximumSize(new java.awt.Dimension(130, 14));
        gradeLabel.setMinimumSize(new java.awt.Dimension(110, 14));
        gradeLabel.setPreferredSize(new java.awt.Dimension(110, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 0);
        lickGradeButtonsPanel.add(gradeLabel, gridBagConstraints);

        saveLickTF.setText("<Generated Lick>");
        saveLickTF.setMinimumSize(new java.awt.Dimension(250, 25));
        saveLickTF.setPreferredSize(new java.awt.Dimension(250, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 10;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        lickGradeButtonsPanel.add(saveLickTF, gridBagConstraints);

        saveLickWithLabelLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        saveLickWithLabelLabel.setText("Save Lick with Label:");
        saveLickWithLabelLabel.setToolTipText("The label that will be used when graded licks are saved.");
        saveLickWithLabelLabel.setMinimumSize(new java.awt.Dimension(110, 14));
        saveLickWithLabelLabel.setPreferredSize(new java.awt.Dimension(110, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 2, 0);
        lickGradeButtonsPanel.add(saveLickWithLabelLabel, gridBagConstraints);

        grade1Btn.setText("1");
        grade1Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade1Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade1Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade1Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade1BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickGradeButtonsPanel.add(grade1Btn, gridBagConstraints);

        grade2Btn.setText("2");
        grade2Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade2Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade2Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade2Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade2BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade2Btn, gridBagConstraints);

        grade3Btn.setText("3");
        grade3Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade3Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade3Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade3Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade3BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade3Btn, gridBagConstraints);

        grade4Btn.setText("4");
        grade4Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade4Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade4Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade4Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade4BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade4Btn, gridBagConstraints);

        grade5Btn.setText("5");
        grade5Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade5Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade5Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade5Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade5BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade5Btn, gridBagConstraints);

        grade6Btn.setText("6");
        grade6Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade6Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade6Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade6Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade6BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade6Btn, gridBagConstraints);

        grade7Btn.setText("7");
        grade7Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade7Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade7Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade7Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade7BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade7Btn, gridBagConstraints);

        grade8Btn.setText("8");
        grade8Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade8Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade8Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade8Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade8BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 12;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade8Btn, gridBagConstraints);

        grade9Btn.setText("9");
        grade9Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade9Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade9Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade9Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade9BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 13;
        gridBagConstraints.gridy = 1;
        lickGradeButtonsPanel.add(grade9Btn, gridBagConstraints);

        grade10Btn.setText("10");
        grade10Btn.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        grade10Btn.setMargin(new java.awt.Insets(2, 2, 2, 2));
        grade10Btn.setPreferredSize(new java.awt.Dimension(23, 21));
        grade10Btn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grade10BtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 14;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        lickGradeButtonsPanel.add(grade10Btn, gridBagConstraints);

        gradeBadBtn.setText("Bad");
        gradeBadBtn.setToolTipText("Grade for a bad jazz lick.");
        gradeBadBtn.setVisible(false);
        gradeBadBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeBadBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        lickGradeButtonsPanel.add(gradeBadBtn, gridBagConstraints);

        gradeAverageBtn.setText("Average");
        gradeAverageBtn.setToolTipText("Grade for an average jazz lick.");
        gradeAverageBtn.setVisible(false);
        gradeAverageBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeAverageBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        lickGradeButtonsPanel.add(gradeAverageBtn, gridBagConstraints);

        gradeGoodBtn.setText("Good");
        gradeGoodBtn.setToolTipText("Grade for a good jazz lick.");
        gradeGoodBtn.setVisible(false);
        gradeGoodBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeGoodBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        lickGradeButtonsPanel.add(gradeGoodBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        lickGenPanel.add(lickGradeButtonsPanel, gridBagConstraints);

        ProbFillClearPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pitch Probabilities Fill and Clear", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        ProbFillClearPanel.setMinimumSize(new java.awt.Dimension(300, 67));
        ProbFillClearPanel.setPreferredSize(new java.awt.Dimension(300, 67));
        ProbFillClearPanel.setLayout(new java.awt.GridBagLayout());

        clearProbsButton.setToolTipText("Clear all pitch probabilities.");
        clearProbsButton.setLabel("Clear All Probabilities");
        clearProbsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearProbsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        ProbFillClearPanel.add(clearProbsButton, gridBagConstraints);

        FillProbsButton.setText("Fill");
        FillProbsButton.setToolTipText("Fill pitch probabilities from chords.\n");
        FillProbsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FillProbsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        ProbFillClearPanel.add(FillProbsButton, gridBagConstraints);

        autoFillCheckBox.setSelected(true);
        autoFillCheckBox.setText("Auto-Fill");
        autoFillCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoFillCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        ProbFillClearPanel.add(autoFillCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        lickGenPanel.add(ProbFillClearPanel, gridBagConstraints);

        pitchProbabilitiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pitch Probabilities by Chord", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        pitchProbabilitiesPanel.setMinimumSize(new java.awt.Dimension(950, 200));
        pitchProbabilitiesPanel.setPreferredSize(new java.awt.Dimension(950, 200));
        pitchProbabilitiesPanel.setLayout(new java.awt.GridBagLayout());

        chordProbPanel.setMinimumSize(new java.awt.Dimension(800, 50));
        chordProbPanel.setPreferredSize(new java.awt.Dimension(800, 400));
        chordProbPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        pitchProbabilitiesPanel.add(chordProbPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        lickGenPanel.add(pitchProbabilitiesPanel, gridBagConstraints);

        soloCorrectionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Critic Options (Using Neural Network)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        soloCorrectionPanel.setMinimumSize(new java.awt.Dimension(600, 83));
        soloCorrectionPanel.setPreferredSize(new java.awt.Dimension(600, 83));
        soloCorrectionPanel.setLayout(new java.awt.GridBagLayout());

        offsetByMeasureGradeSoloButton.setText("Offset By Measure");
        offsetByMeasureGradeSoloButton.setToolTipText("Moves the selection one measure forward. To be used with automated correction if there is an odd number of measures.");
        offsetByMeasureGradeSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offsetByMeasureGradeSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(offsetByMeasureGradeSoloButton, gridBagConstraints);

        forwardGradeSoloButton.setText("Step Forward");
        forwardGradeSoloButton.setToolTipText("Move the selection two measures forward.");
        forwardGradeSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forwardGradeSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(forwardGradeSoloButton, gridBagConstraints);

        backwardGradeSoloButton.setText("Step Backward");
        backwardGradeSoloButton.setToolTipText("Move the selection two measures back.");
        backwardGradeSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                backwardGradeSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(backwardGradeSoloButton, gridBagConstraints);

        resetSelectionButton.setText("Reset Selection");
        resetSelectionButton.setToolTipText("Undo a change.");
        resetSelectionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetSelectionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(resetSelectionButton, gridBagConstraints);

        gradeAllMeasuresButton.setText("Correct All");
        gradeAllMeasuresButton.setToolTipText("Moves two measures at a time, correcting licks if the correct grade is insufficient.");
        gradeAllMeasuresButton.setMaximumSize(new java.awt.Dimension(117, 29));
        gradeAllMeasuresButton.setMinimumSize(new java.awt.Dimension(117, 29));
        gradeAllMeasuresButton.setPreferredSize(new java.awt.Dimension(117, 29));
        gradeAllMeasuresButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeAllMeasuresButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(gradeAllMeasuresButton, gridBagConstraints);

        regenerateLickForSoloButton.setText("Generate Better Lick");
        regenerateLickForSoloButton.setToolTipText("Generate a lick that passes through the filter, with a grade that is high enough..");
        regenerateLickForSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                regenerateLickForSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(regenerateLickForSoloButton, gridBagConstraints);

        gradeLickFromStaveButton.setText("Grade Selected Lick");
        gradeLickFromStaveButton.setToolTipText("Use the critic to grade the current two measure selection.");
        gradeLickFromStaveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gradeLickFromStaveButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(gradeLickFromStaveButton, gridBagConstraints);

        lickFromStaveGradeTextField.setEditable(false);
        lickFromStaveGradeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        lickFromStaveGradeTextField.setText("Grade");
        lickFromStaveGradeTextField.setToolTipText("Grade from the critic for the current lick.");
        lickFromStaveGradeTextField.setMinimumSize(new java.awt.Dimension(156, 27));
        lickFromStaveGradeTextField.setPreferredSize(new java.awt.Dimension(156, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 10);
        soloCorrectionPanel.add(lickFromStaveGradeTextField, gridBagConstraints);

        useCriticCheckBox.setText("Use Critic");
        useCriticCheckBox.setToolTipText("Filter lick generation with a trained network.");
        useCriticCheckBox.setMaximumSize(new java.awt.Dimension(110, 23));
        useCriticCheckBox.setMinimumSize(new java.awt.Dimension(110, 23));
        useCriticCheckBox.setPreferredSize(new java.awt.Dimension(110, 23));
        useCriticCheckBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                useCriticCheckBoxMouseClicked(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(useCriticCheckBox, gridBagConstraints);

        criticGradeTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        criticGradeTextField.setText("Grade");
        criticGradeTextField.setToolTipText("Lowest grade acceptable by the filter.");
        criticGradeTextField.setEnabled(false);
        criticGradeTextField.setMinimumSize(new java.awt.Dimension(60, 24));
        criticGradeTextField.setPreferredSize(new java.awt.Dimension(60, 24));
        criticGradeTextField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                criticGradeTextFieldFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                criticGradeTextFieldFocusLost(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(criticGradeTextField, gridBagConstraints);

        counterForCriticTextField.setEditable(false);
        counterForCriticTextField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        counterForCriticTextField.setText("Counter");
        counterForCriticTextField.setToolTipText("Counter for how many times the critic generates a lick.");
        counterForCriticTextField.setEnabled(false);
        counterForCriticTextField.setMinimumSize(new java.awt.Dimension(80, 24));
        counterForCriticTextField.setName(""); // NOI18N
        counterForCriticTextField.setPreferredSize(new java.awt.Dimension(80, 24));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(counterForCriticTextField, gridBagConstraints);

        criticGradeLabel.setText("Grade");
        criticGradeLabel.setToolTipText("Lowest grade acceptable by the filter.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        soloCorrectionPanel.add(criticGradeLabel, gridBagConstraints);

        counterForCriticLabel.setText("Counter");
        counterForCriticLabel.setToolTipText("Counter for how many times the critic generates a lick.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 10);
        soloCorrectionPanel.add(counterForCriticLabel, gridBagConstraints);

        loadRandomGrammarButton.setText("Load Random");
        loadRandomGrammarButton.setToolTipText("Loads the random grammar for lick generation.");
        loadRandomGrammarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadRandomGrammarButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 2, 0);
        soloCorrectionPanel.add(loadRandomGrammarButton, gridBagConstraints);

        soloCorrectionPanel.setVisible(false);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.6;
        lickGenPanel.add(soloCorrectionPanel, gridBagConstraints);

        generatorPane.addTab("Lick Generator", lickGenPanel);

        grammarLearningPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Grammar Learning", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        grammarLearningPanel.setMinimumSize(new java.awt.Dimension(500, 300));
        grammarLearningPanel.setPreferredSize(new java.awt.Dimension(500, 500));
        grammarLearningPanel.setLayout(new java.awt.GridBagLayout());

        finalLabel.setBackground(Color.green
        );
        finalLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        finalLabel.setText("<html>You can try your grammar at generation immediately without further loading, on the current or any other leadsheet,<br>however it will not appear in the main window until you restart the program.</html>");
        finalLabel.setMaximumSize(new java.awt.Dimension(400, 9999));
        finalLabel.setMinimumSize(new java.awt.Dimension(400, 40));
        finalLabel.setPreferredSize(new java.awt.Dimension(400, 40));
        finalLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 12;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(finalLabel, gridBagConstraints);

        windowParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 3: (Optional) Set the parameters below:", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 14))); // NOI18N
        windowParametersPanel.setMinimumSize(new java.awt.Dimension(500, 148));
        windowParametersPanel.setLayout(new java.awt.GridBagLayout());

        windowSizeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSizeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        windowSizeLabel.setText("Window Size (beats)");
        windowSizeLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        windowSizeLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        windowSizeLabel.setMinimumSize(new java.awt.Dimension(120, 30));
        windowSizeLabel.setPreferredSize(new java.awt.Dimension(120, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSizeLabel, gridBagConstraints);

        windowSlideLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSlideLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        windowSlideLabel.setText("Window Slide (beats)");
        windowSlideLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        windowSlideLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        windowSlideLabel.setMinimumSize(new java.awt.Dimension(120, 30));
        windowSlideLabel.setPreferredSize(new java.awt.Dimension(120, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSlideLabel, gridBagConstraints);

        numClusterRepsLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        numClusterRepsLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        numClusterRepsLabel.setText("Number of Representatives per Cluster");
        numClusterRepsLabel.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        numClusterRepsLabel.setMaximumSize(new java.awt.Dimension(9999, 9999));
        numClusterRepsLabel.setMinimumSize(new java.awt.Dimension(400, 30));
        numClusterRepsLabel.setPreferredSize(new java.awt.Dimension(400, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(numClusterRepsLabel, gridBagConstraints);

        windowSizeField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSizeField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        windowSizeField.setText("4");
        windowSizeField.setToolTipText("The number of beats for the size of the window");
        windowSizeField.setMaximumSize(null);
        windowSizeField.setMinimumSize(new java.awt.Dimension(60, 30));
        windowSizeField.setPreferredSize(new java.awt.Dimension(60, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSizeField, gridBagConstraints);

        windowSlideField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        windowSlideField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        windowSlideField.setText("4");
        windowSlideField.setToolTipText("The number of beats to slide window by");
        windowSlideField.setMaximumSize(null);
        windowSlideField.setMinimumSize(new java.awt.Dimension(60, 30));
        windowSlideField.setPreferredSize(new java.awt.Dimension(60, 30));
        windowSlideField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                windowSlideFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSlideField, gridBagConstraints);

        useRelativeCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useRelativeCheckbox.setSelected(true);
        useRelativeCheckbox.setToolTipText("Use Markov chains when adding productions to Grammar");
        useRelativeCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useRelativeCheckbox.setLabel("Use relative pitches");
        useRelativeCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useRelativeCheckbox.setMinimumSize(new java.awt.Dimension(435, 30));
        useRelativeCheckbox.setPreferredSize(new java.awt.Dimension(435, 30));
        useRelativeCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useRelativeCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        windowParametersPanel.add(useRelativeCheckbox, gridBagConstraints);

        numClusterRepsField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        numClusterRepsField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        numClusterRepsField.setText("12");
        numClusterRepsField.setToolTipText("The number of beats for the size of the window");
        numClusterRepsField.setMaximumSize(null);
        numClusterRepsField.setMinimumSize(new java.awt.Dimension(100, 30));
        numClusterRepsField.setPreferredSize(new java.awt.Dimension(60, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(numClusterRepsField, gridBagConstraints);

        useMarkovCheckbox.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        useMarkovCheckbox.setSelected(true);
        useMarkovCheckbox.setToolTipText("Use Markov chains when adding productions to Grammar");
        useMarkovCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        useMarkovCheckbox.setLabel("Use Markov (ordered connection of phrases)             Markov chain length");
        useMarkovCheckbox.setMaximumSize(new java.awt.Dimension(9999, 9999));
        useMarkovCheckbox.setMinimumSize(new java.awt.Dimension(435, 30));
        useMarkovCheckbox.setPreferredSize(new java.awt.Dimension(435, 30));
        useMarkovCheckbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useMarkovCheckboxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        windowParametersPanel.add(useMarkovCheckbox, gridBagConstraints);

        MarkovLengthField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        MarkovLengthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        MarkovLengthField.setText("4");
        MarkovLengthField.setToolTipText("The number of previous states on which the Markov chain depends.");
        MarkovLengthField.setMaximumSize(new java.awt.Dimension(9999, 9999));
        MarkovLengthField.setMinimumSize(new java.awt.Dimension(100, 30));
        MarkovLengthField.setPreferredSize(new java.awt.Dimension(60, 30));
        MarkovLengthField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MarkovLengthFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        windowParametersPanel.add(MarkovLengthField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.02;
        grammarLearningPanel.add(windowParametersPanel, gridBagConstraints);

        loadBaseGrammarBtn.setBackground(Color.yellow
        );
        loadBaseGrammarBtn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        loadBaseGrammarBtn.setText("<html><b>Step 1</b>: Load the grammar on which you wish to build, such as _Empty.grammar.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If you do nothing, Impro-Visor will build on whatever grammar is current.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This step also clears any accumulated productions from prior use of the learning tool.</html>  ");
        loadBaseGrammarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loadBaseGrammarBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        loadBaseGrammarBtn.setMaximumSize(new java.awt.Dimension(9999, 9999));
        loadBaseGrammarBtn.setMinimumSize(new java.awt.Dimension(105, 60));
        loadBaseGrammarBtn.setPreferredSize(new java.awt.Dimension(173, 60));
        loadBaseGrammarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadBaseGrammarBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(loadBaseGrammarBtn, gridBagConstraints);

        saveGrammarAsButton.setBackground(Color.yellow
        );
        saveGrammarAsButton.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        saveGrammarAsButton.setText("<html><b>Step 2</b>: <b>IMPORTANT</b>: This step will use <b>Save as . . .</b> in the Grammar menu to save your new grammar under a new name, <br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; in case you want to return to the old grammar.\n<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; It will also ask you to save your leadsheet if you need it, as the leadsheet window will be used as a workspace.</html>  ");
        saveGrammarAsButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        saveGrammarAsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveGrammarAsButton.setMaximumSize(new java.awt.Dimension(9999, 9999));
        saveGrammarAsButton.setPreferredSize(new java.awt.Dimension(173, 60));
        saveGrammarAsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGrammarAsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(saveGrammarAsButton, gridBagConstraints);

        openCorpusBtn.setBackground(Color.orange);
        openCorpusBtn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        openCorpusBtn.setText("<html><b>Step 4</b>: Select a corpus of solos from which to learn. Each solo is a leadsheet file.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Note: Selecting any leadsheet file in a folder is equivalent to selecting the entire folder. </b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The leadsheet you selected will be left in the window at the end.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>The process is complete when the last chorus of that leadsheet appears</b>.</html>");
        openCorpusBtn.setActionCommand("<html><b>Step 5</b>: Next select a corpus of solos from which to learn. Each solo is a leadsheet file.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Selecting any file any a folder is equivalent to selecting the entire folder.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The leadsheet you selected will be left in the window at the end. The process is over when the last chorus appears.</html>");
        openCorpusBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openCorpusBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        openCorpusBtn.setMaximumSize(new java.awt.Dimension(9999, 9999));
        openCorpusBtn.setMinimumSize(new java.awt.Dimension(240, 75));
        openCorpusBtn.setPreferredSize(new java.awt.Dimension(240, 75));
        openCorpusBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openCorpusBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 9;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(openCorpusBtn, gridBagConstraints);

        toGrammarBtn.setBackground(Color.green);
        toGrammarBtn.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        toGrammarBtn.setText("<html><b>Step 5</b>: Click this button to create and save the grammar and Soloist file. \n<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;There are two <b>other alternatives</b> at this point:\n<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a, Quit by closing the window, with no changes.\n<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b. Return to Step 4 and learn from other corpuses of solos.\n</html>");
        toGrammarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        toGrammarBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        toGrammarBtn.setMaximumSize(new java.awt.Dimension(9999, 70));
        toGrammarBtn.setMinimumSize(new java.awt.Dimension(240, 75));
        toGrammarBtn.setPreferredSize(new java.awt.Dimension(240, 75));
        toGrammarBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toGrammarBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 11;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(toGrammarBtn, gridBagConstraints);

        learningStep0Label.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        learningStep0Label.setText("<html>Please follow these steps to learn a new grammar from a corpus of solos as a folder of leadsheets. <br>Click the rectangular buttons below from top to bottom.</html>  ");
        learningStep0Label.setMaximumSize(new java.awt.Dimension(2147483647, 90));
        learningStep0Label.setMinimumSize(new java.awt.Dimension(400, 85));
        learningStep0Label.setPreferredSize(new java.awt.Dimension(400, 85));
        learningStep0Label.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(learningStep0Label, gridBagConstraints);

        testGeneration.setBackground(Color.green);
        testGeneration.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        testGeneration.setText("<html><b>Step 6</b>: Press this button to generate solos with your Learned grammar</html>");
        testGeneration.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        testGeneration.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        testGeneration.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);
        testGeneration.setMaximumSize(new java.awt.Dimension(9999, 9999));
        testGeneration.setMinimumSize(new java.awt.Dimension(240, 29));
        testGeneration.setPreferredSize(new java.awt.Dimension(240, 29));
        testGeneration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                testGenerationActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 13;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 0.05;
        grammarLearningPanel.add(testGeneration, gridBagConstraints);

        generatorPane.addTab("Grammar Learning", grammarLearningPanel);

        soloGenPanel.setLayout(new java.awt.GridBagLayout());

        generateSoloButton.setText("Generate Solo from Current Theme");
        generateSoloButton.setMaximumSize(new java.awt.Dimension(100, 30));
        generateSoloButton.setMinimumSize(new java.awt.Dimension(100, 30));
        generateSoloButton.setPreferredSize(new java.awt.Dimension(100, 30));
        generateSoloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateSoloButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(generateSoloButton, gridBagConstraints);

        generateThemeButton.setText("Generate New Theme Only");
        generateThemeButton.setMaximumSize(new java.awt.Dimension(100, 30));
        generateThemeButton.setMinimumSize(new java.awt.Dimension(100, 30));
        generateThemeButton.setPreferredSize(new java.awt.Dimension(100, 30));
        generateThemeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateThemeButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(generateThemeButton, gridBagConstraints);

        themeField.setEditable(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        soloGenPanel.add(themeField, gridBagConstraints);

        themeProbabilityField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        soloGenPanel.add(themeProbabilityField, gridBagConstraints);

        themeLengthField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        soloGenPanel.add(themeLengthField, gridBagConstraints);

        themeLengthLabel.setText("Theme Length (beats):");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(themeLengthLabel, gridBagConstraints);

        themeProbLabel.setText("Probability to use Theme:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        soloGenPanel.add(themeProbLabel, gridBagConstraints);

        themeLabel.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        themeLabel.setText("Theme in Leadsheet notation:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(themeLabel, gridBagConstraints);

        genSoloThemeBtn.setText("Generate Solo and Theme");
        genSoloThemeBtn.setMaximumSize(new java.awt.Dimension(100, 30));
        genSoloThemeBtn.setMinimumSize(new java.awt.Dimension(100, 30));
        genSoloThemeBtn.setPreferredSize(new java.awt.Dimension(100, 30));
        genSoloThemeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                genSoloThemeBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(genSoloThemeBtn, gridBagConstraints);

        transposeProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        transposeProbLabel.setText("     Transposition Probability:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(transposeProbLabel, gridBagConstraints);

        InvertProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        InvertProbLabel.setText("     Inversion Probability:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(InvertProbLabel, gridBagConstraints);

        ReverseProbLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        ReverseProbLabel.setText("     Reversal Probability:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(ReverseProbLabel, gridBagConstraints);

        pasteThemeBtn.setText("Use Current Selection in Leadsheet Window as Theme");
        pasteThemeBtn.setMaximumSize(new java.awt.Dimension(100, 30));
        pasteThemeBtn.setMinimumSize(new java.awt.Dimension(100, 30));
        pasteThemeBtn.setPreferredSize(new java.awt.Dimension(100, 30));
        pasteThemeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteThemeBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(pasteThemeBtn, gridBagConstraints);

        playSoloBtn.setLabel("Play Solo");
        playSoloBtn.setMaximumSize(new java.awt.Dimension(100, 30));
        playSoloBtn.setMinimumSize(new java.awt.Dimension(100, 30));
        playSoloBtn.setPreferredSize(new java.awt.Dimension(100, 30));
        playSoloBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playSoloBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(playSoloBtn, gridBagConstraints);

        stopSoloPlayBtn.setLabel("Stop Playing");
        stopSoloPlayBtn.setMaximumSize(new java.awt.Dimension(100, 30));
        stopSoloPlayBtn.setMinimumSize(new java.awt.Dimension(100, 30));
        stopSoloPlayBtn.setPreferredSize(new java.awt.Dimension(100, 30));
        stopSoloPlayBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopSoloPlayBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 8;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(stopSoloPlayBtn, gridBagConstraints);

        transposeProbabilityField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(transposeProbabilityField, gridBagConstraints);

        invertProbabilityField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(invertProbabilityField, gridBagConstraints);

        reverseProbabilityField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 7;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipadx = 30;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        soloGenPanel.add(reverseProbabilityField, gridBagConstraints);

        disclaimer.setBackground(new java.awt.Color(255, 255, 0));
        disclaimer.setFont(new java.awt.Font("Lucida Grande", 0, 18)); // NOI18N
        disclaimer.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        disclaimer.setText("<html>The Solo Generator is work in progress. <br><br>The idea is to generate a solo by using a theme several times,<br> in different places within the solo. <br><br> The theme itself can be generated, or it can be imported as the current selection in the leadsheet. <br><br> The theme may also be reversed, inverted, or transposed<br> by specifying non-zero probabilities below.  </html>");
        disclaimer.setMaximumSize(new java.awt.Dimension(600, 200));
        disclaimer.setMinimumSize(new java.awt.Dimension(600, 200));
        disclaimer.setPreferredSize(new java.awt.Dimension(600, 200));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 0.5;
        soloGenPanel.add(disclaimer, gridBagConstraints);

        generatorPane.addTab("Solo Generator", soloGenPanel);

        neuralNetworkPanel.setLayout(new java.awt.GridBagLayout());

        nnetOutputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Neural Network Output", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        nnetOutputPanel.setMinimumSize(new java.awt.Dimension(850, 200));
        nnetOutputPanel.setPreferredSize(new java.awt.Dimension(850, 200));
        nnetOutputPanel.setLayout(new java.awt.GridBagLayout());

        nnetScrollPane.setBorder(null);
        nnetScrollPane.setMinimumSize(new java.awt.Dimension(223, 180));
        nnetScrollPane.setPreferredSize(new java.awt.Dimension(223, 180));

        nnetOutputTextField.setColumns(20);
        nnetOutputTextField.setLineWrap(true);
        nnetOutputTextField.setRows(20000);
        nnetOutputTextField.setText("To generate a weight file:\n-Select training file (File name will end with \".training.data\")\n-Weight file name with automatically be set\n--Weight file will save to personal settings folder, in vocab\n-Change the epoch limit if desired\n-Change the default learning rate if desired\n-Change the default MSE goal if desired\n-Change the default mode if desired\n-In the table to the right:\n--Set the layer size for each layer\n---Input (first) layer size determinted at runtime from input size\n---The last layer, for output, should be of size 1\n--Set the function for each layer\n--Reorder rows as desired. Empty rows will be ignored.\n-Press \"Generate Weight File\"\n\nTo load network:\n-Select the weight file, from the vocab folder, under \"Weight File\"\n-Press \"Load Weight\"\n-Network will be initialized per leadsheet\n\nTo clear a weight file:\n-Select the weight file, from the vocab folder, under \"Weight File\"\n-Press \"Clear Weight File\"\n\n***There is a sample weight file in impro-visor-version-X.xx-files/vocab\n   for general use. The licks used to create it were subjectively graded,\n   and therefore may not reflect the preferences of the user.");
        nnetOutputTextField.setBorder(null);
        nnetOutputTextField.setMinimumSize(new java.awt.Dimension(800, 100));
        nnetScrollPane.setViewportView(nnetOutputTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.2;
        nnetOutputPanel.add(nnetScrollPane, gridBagConstraints);

        layerInfoScrollPane.setMinimumSize(new java.awt.Dimension(469, 402));

        layerDataTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                { new Integer(1),  new Integer(64), "Logsig"},
                { new Integer(2),  new Integer(1), "Logsig"}
            },
            new String [] {
                "Layer Index", "Layer Size", "Layer Type"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if (columnIndex == 1
                    && rowIndex == layerDataTable.getRowCount() - 1)
                {
                    return false;
                }
                return canEdit [columnIndex];
            }
        });
        layerDataTable.setMaximumSize(new java.awt.Dimension(2147483647, 2147483647));
        layerDataTable.setMinimumSize(new java.awt.Dimension(150, 900));
        layerDataTable.setPreferredSize(new java.awt.Dimension(150, 900));
        layerDataTable.getTableHeader().setReorderingAllowed(false);
        layerDataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                layerDataTableMouseClicked(evt);
            }
        });
        layerInfoScrollPane.setViewportView(layerDataTable);
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Logsig");
        comboBox.addItem("Tansig");
        comboBox.addItem("Elliot");
        comboBox.addItem("Elliots");
        comboBox.addItem("Hardlim");
        comboBox.addItem("Hardlims");
        comboBox.addItem("Purelin");
        comboBox.addItem("Satlin");
        comboBox.addItem("Satlins");
        layerDataTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.2;
        nnetOutputPanel.add(layerInfoScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        neuralNetworkPanel.add(nnetOutputPanel, gridBagConstraints);

        nnetWeightGenerationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Neural Network Operations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        nnetWeightGenerationPanel.setMinimumSize(new java.awt.Dimension(300, 230));
        nnetWeightGenerationPanel.setPreferredSize(new java.awt.Dimension(300, 230));
        nnetWeightGenerationPanel.setLayout(new java.awt.GridBagLayout());

        generateWeightFileButton.setText("Generate Weight File");
        generateWeightFileButton.setToolTipText("Generate a weight file from the input to the neural network.");
        generateWeightFileButton.setMaximumSize(new java.awt.Dimension(300, 29));
        generateWeightFileButton.setMinimumSize(new java.awt.Dimension(300, 29));
        generateWeightFileButton.setPreferredSize(new java.awt.Dimension(300, 29));
        generateWeightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateWeightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(generateWeightFileButton, gridBagConstraints);

        getNetworkStatsButton.setText("Get Network Statistics");
        getNetworkStatsButton.setToolTipText("Show network statistics.");
        getNetworkStatsButton.setMaximumSize(new java.awt.Dimension(300, 29));
        getNetworkStatsButton.setMinimumSize(new java.awt.Dimension(300, 29));
        getNetworkStatsButton.setPreferredSize(new java.awt.Dimension(300, 29));
        getNetworkStatsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                getNetworkStatsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(getNetworkStatsButton, gridBagConstraints);

        clearWeightFileButton.setText("Clear Weight File");
        clearWeightFileButton.setToolTipText("Delete the selected weight file.");
        clearWeightFileButton.setMaximumSize(new java.awt.Dimension(300, 29));
        clearWeightFileButton.setMinimumSize(new java.awt.Dimension(300, 29));
        clearWeightFileButton.setPreferredSize(new java.awt.Dimension(300, 29));
        clearWeightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearWeightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(clearWeightFileButton, gridBagConstraints);

        loadWeightFileButton.setText("Load Weight File");
        loadWeightFileButton.setToolTipText("Load the selected weight file.");
        loadWeightFileButton.setMaximumSize(new java.awt.Dimension(300, 29));
        loadWeightFileButton.setMinimumSize(new java.awt.Dimension(300, 29));
        loadWeightFileButton.setPreferredSize(new java.awt.Dimension(300, 29));
        loadWeightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadWeightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(loadWeightFileButton, gridBagConstraints);

        resetNnetInstructionsButton.setText("Reset Instructions");
        resetNnetInstructionsButton.setToolTipText("Reset the instructions for how to use the neural network.");
        resetNnetInstructionsButton.setMaximumSize(new java.awt.Dimension(300, 29));
        resetNnetInstructionsButton.setMinimumSize(new java.awt.Dimension(300, 29));
        resetNnetInstructionsButton.setPreferredSize(new java.awt.Dimension(300, 29));
        resetNnetInstructionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetNnetInstructionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(resetNnetInstructionsButton, gridBagConstraints);

        resetDefaultValuesButton.setText("Reset Default Values");
        resetDefaultValuesButton.setToolTipText("Reset all default values for all fields.");
        resetDefaultValuesButton.setMaximumSize(new java.awt.Dimension(300, 29));
        resetDefaultValuesButton.setMinimumSize(new java.awt.Dimension(300, 29));
        resetDefaultValuesButton.setPreferredSize(new java.awt.Dimension(300, 29));
        resetDefaultValuesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetDefaultValuesButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(resetDefaultValuesButton, gridBagConstraints);

        resetNetworkButton.setText("Reset Network");
        resetNetworkButton.setToolTipText("Reset the network to load a new network.");
        resetNetworkButton.setMaximumSize(new java.awt.Dimension(300, 29));
        resetNetworkButton.setMinimumSize(new java.awt.Dimension(300, 29));
        resetNetworkButton.setPreferredSize(new java.awt.Dimension(300, 29));
        resetNetworkButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetNetworkButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.weightx = 1.0;
        nnetWeightGenerationPanel.add(resetNetworkButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.3;
        neuralNetworkPanel.add(nnetWeightGenerationPanel, gridBagConstraints);

        nnetParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Neural Network Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        nnetParametersPanel.setMinimumSize(new java.awt.Dimension(520, 220));
        nnetParametersPanel.setPreferredSize(new java.awt.Dimension(520, 220));
        nnetParametersPanel.setLayout(new java.awt.GridBagLayout());

        trainingFileButton.setText("Training File");
        trainingFileButton.setToolTipText("Select the training file for the network.");
        trainingFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trainingFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(trainingFileButton, gridBagConstraints);

        trainingFileTextField.setEditable(false);
        trainingFileTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        trainingFileTextField.setPreferredSize(new java.awt.Dimension(115, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(trainingFileTextField, gridBagConstraints);

        epochLimitLabel.setText("Epoch Limit");
        epochLimitLabel.setToolTipText("Limit the amount of iterations during training.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(epochLimitLabel, gridBagConstraints);

        epochLimitTextField.setText("20000");
        epochLimitTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        epochLimitTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(epochLimitTextField, gridBagConstraints);

        learningRateLabel.setText("Learning Rate");
        learningRateLabel.setToolTipText("Set the learning rate.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(learningRateLabel, gridBagConstraints);

        learningRateTextField.setText("0.01");
        learningRateTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        learningRateTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(learningRateTextField, gridBagConstraints);

        mseGoalLabel.setText("MSE Goal");
        mseGoalLabel.setToolTipText("MSE goal for training. Training will stop once this goal is reached.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(mseGoalLabel, gridBagConstraints);

        mseGoalTextField.setText("0.01");
        mseGoalTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        mseGoalTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(mseGoalTextField, gridBagConstraints);

        modeLabel.setText("Mode");
        modeLabel.setToolTipText("Select the mode for the training from the dropdown menu.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(modeLabel, gridBagConstraints);

        modeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "On-Line", "Batch", "RProp" }));
        modeComboBox.setSelectedIndex(2);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(modeComboBox, gridBagConstraints);

        weightFileTextField.setEditable(false);
        weightFileTextField.setMinimumSize(new java.awt.Dimension(93, 27));
        weightFileTextField.setPreferredSize(new java.awt.Dimension(93, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 3);
        nnetParametersPanel.add(weightFileTextField, gridBagConstraints);

        numberOfLayersLabel.setText("Num Layers");
        numberOfLayersLabel.setToolTipText("Number of layers of the network. Automatically set from the table.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(numberOfLayersLabel, gridBagConstraints);

        numberOfLayersTextField.setEditable(false);
        numberOfLayersTextField.setText("2");
        numberOfLayersTextField.setMinimumSize(new java.awt.Dimension(68, 27));
        numberOfLayersTextField.setPreferredSize(new java.awt.Dimension(68, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(numberOfLayersTextField, gridBagConstraints);

        addLayerToTableButton.setText("Add Layer");
        addLayerToTableButton.setToolTipText("Add a layer to the end of the network. If a layer is selected, add it below that one.");
        addLayerToTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLayerToTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(addLayerToTableButton, gridBagConstraints);

        removeLayerFromTableButton.setText("Remove Layer");
        removeLayerFromTableButton.setToolTipText("Delete the selected layer.");
        removeLayerFromTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLayerFromTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(removeLayerFromTableButton, gridBagConstraints);

        moveLayerUpTableButton.setText("Layer Up");
        moveLayerUpTableButton.setToolTipText("Move the selected layer up.");
        moveLayerUpTableButton.setMaximumSize(new java.awt.Dimension(100, 29));
        moveLayerUpTableButton.setMinimumSize(new java.awt.Dimension(100, 29));
        moveLayerUpTableButton.setPreferredSize(new java.awt.Dimension(100, 29));
        moveLayerUpTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLayerUpTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(moveLayerUpTableButton, gridBagConstraints);

        moveLayerDownTableButton.setText("Layer Down");
        moveLayerDownTableButton.setToolTipText("Move the selected layer down.");
        moveLayerDownTableButton.setMaximumSize(new java.awt.Dimension(120, 29));
        moveLayerDownTableButton.setMinimumSize(new java.awt.Dimension(120, 29));
        moveLayerDownTableButton.setPreferredSize(new java.awt.Dimension(120, 29));
        moveLayerDownTableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                moveLayerDownTableButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        nnetParametersPanel.add(moveLayerDownTableButton, gridBagConstraints);

        weightFileButton.setText("Weight File");
        weightFileButton.setToolTipText("Name automatically set from Training File. If you are only loading weights into the critic, use this.");
        weightFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                weightFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 3, 5, 0);
        nnetParametersPanel.add(weightFileButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 0.7;
        neuralNetworkPanel.add(nnetParametersPanel, gridBagConstraints);

        generatorPane.addTab("Neural Network", neuralNetworkPanel);

        substitutorPanel.setMinimumSize(new java.awt.Dimension(32767, 32767));
        substitutorPanel.setPreferredSize(new java.awt.Dimension(0, 0));
        substitutorPanel.setLayout(new java.awt.GridLayout());
        generatorPane.addTab("Substitutor", substitutorPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 50;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(generatorPane, gridBagConstraints);

        generatorMenuBar1.setMinimumSize(new java.awt.Dimension(115, 23));

        grammarMenu1.setMnemonic('G');
        grammarMenu1.setText("Grammar Options");
        grammarMenu1.setToolTipText("Edit or change the current grammar file.");
        grammarMenu1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                grammarMenu1ActionPerformed(evt);
            }
        });

        openGrammarMI1.setText("Load Grammar");
        openGrammarMI1.setToolTipText("Selects which grammar file to used.");
        openGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(openGrammarMI1);

        showLogMI1.setText("Show Log");
        showLogMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showLogMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(showLogMI1);

        saveGrammarMI1.setText("Save Grammar As ...");
        saveGrammarMI1.setToolTipText("Saves the grammar file under a specified name.");
        saveGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(saveGrammarMI1);

        editGrammarMI1.setText("Edit Grammar");
        editGrammarMI1.setToolTipText("Edit the current grammar using a text editor.");
        editGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(editGrammarMI1);

        reloadGrammarMI1.setText("Reload Grammar");
        reloadGrammarMI1.setToolTipText("Reloads the grammar file.");
        reloadGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reloadGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(reloadGrammarMI1);

        toCriticMI1.setText("Send Licks to Critic");
        toCriticMI1.setToolTipText("Copies licks in a special format for learning by critic (a separate tool).");
        toCriticMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toCriticMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(toCriticMI1);

        showCriticMI1.setText("Show Critic Exporter");
        showCriticMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showCriticMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(showCriticMI1);

        useGrammarMI1.setSelected(true);
        useGrammarMI1.setText("Use Grammar");
        useGrammarMI1.setToolTipText("Indicates whether or not a grammar should be used in lick generation. Without this, generation will be governed only by probabilities set in the fields below.");
        useGrammarMI1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                useGrammarMI1ActionPerformed(evt);
            }
        });
        grammarMenu1.add(useGrammarMI1);

        generatorMenuBar1.add(grammarMenu1);

        generatorWindowMenu1.setLabel("Window");
        generatorWindowMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                generatorWindowMenu1MenuSelected(evt);
            }
        });

        closeWindowMI2.setMnemonic('C');
        closeWindowMI2.setText("Close Window");
        closeWindowMI2.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMI2ActionPerformed(evt);
            }
        });
        generatorWindowMenu1.add(closeWindowMI2);

        cascadeMI2.setMnemonic('A');
        cascadeMI2.setText("Cascade Windows");
        cascadeMI2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMI2ActionPerformed(evt);
            }
        });
        generatorWindowMenu1.add(cascadeMI2);
        generatorWindowMenu1.add(windowMenuSeparator2);

        generatorMenuBar1.add(generatorWindowMenu1);

        setJMenuBar(generatorMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents


/**
 * Get the pitch-class probabilities from array of text fields.
 @return pitch-class probabilities
 */

public ArrayList<double[]> readProbs()
  {
    ArrayList<double[]> probs = new ArrayList<double[]>();

    for( int i = 0; i < lickPrefs.size(); ++i )
      {
        double[] p = new double[12];
        
        JTextField tf[] = lickPrefs.get(i);

        for( int j = 0; j < tf.length; ++j )
          {
            p[j] = Notate.quietDoubleFromTextField(tf[j], 0.0,
                                                Double.POSITIVE_INFINITY, 0.0);
          }
        probs.add(p);
      }

    return probs;
  }


/**
 * Make sure that the values in the probability fields are between 0.0 and 1.0
 */

public void verifyProbs()
  {

    for( int i = 0; i < lickPrefs.size(); ++i )
      {
        JTextField tf[] = lickPrefs.get(i);
        for( int j = 0; j < tf.length; ++j )
          {
            Notate.doubleFromTextField(tf[j], 0.0,
                                       Double.POSITIVE_INFINITY, 1.0);
          }
      }
  }


/**
 * Redraw the triage frame based on where we are and how much of the current 
 * chord progression we're examining.
 */

public void redrawTriage()
  {
    lickSavedLabel.setText("");

    chordProbPanel.removeAll();

    GridBagLayout gbl = new GridBagLayout();

    JPanel panel = new JPanel(gbl);

    // We need to keep track of both the chords we've already looked at
    // and the old probability values.

    ArrayList<String> chordUsed = new ArrayList<String>();

    ArrayList<JTextField[]> oldProbs = (ArrayList<JTextField[]>) lickPrefs.clone();


    int start = notate.getCurrentSelectionStart();

    if( start == -1 )
      {
        return;
      }

    int end = notate.getCurrentSelectionEnd(); // start + notate.getTotalSlots();


    // Add the locations of every chord change in the section that we're
    // examining.

    ChordPart chordPart = notate.getChordProg();

    ArrayList<Integer> chordChanges = new ArrayList<Integer>();

    chordChanges.add(start);


    int next = chordPart.getNextUniqueChordIndex(start);

    while( next < end && next != -1 )
      {
        chordChanges.add(next);

        next = chordPart.getNextUniqueChordIndex(next);
      }

    // Clear out the old values.

    lickPrefs.clear();


    // Loop through every chord

    int numChords = chordChanges.size();

    for( int i = 0; i < numChords; ++i )
      {

        // If we've added stuff for this chord already, move on; otherwise,

        // add it to the list of chords that we've processed.

        Chord currentChord = chordPart.getCurrentChord(chordChanges.get(i));
        
        if( currentChord != null )
          {
          String currentChordName = currentChord.getName();
  
          if( chordUsed.contains(currentChordName) )
            {
            continue;
            }
          else
            {
            chordUsed.add(currentChordName);
            }

        // Add in a label specifing which chord these text boxes correspond to.

        GridBagConstraints labelConstraints = new GridBagConstraints();

        labelConstraints.gridx = 0;

        labelConstraints.gridwidth = 4;

        labelConstraints.gridy = (i * 3);

        labelConstraints.fill = GridBagConstraints.HORIZONTAL;

        labelConstraints.ipadx = 5;

        labelConstraints.insets = new Insets(5, 5, 5, 5);

        labelConstraints.weightx = 1.0;


        JLabel label = new JLabel(currentChordName + " probabilities:");

        panel.add(label, labelConstraints);



        // Create a new array of text boxes and note labels

        JTextField[] prefs = new JTextField[12];

        String[] notes = notate.getNoteLabels(chordChanges.get(i));

        // Since there are twelve chromatic pitches we need to consider,
        // loop through twelve times.

        for( int j = 0; j < 12; ++j )
          {
            // First we need to draw the note labels; set up the drawing constraints

            // for them.  They get added to every other row, just above the text

            // boxes we're about to draw.

            GridBagConstraints lbc = new GridBagConstraints();

            lbc.anchor = GridBagConstraints.CENTER;

            lbc.gridx = j;

            lbc.gridy = (i * 3) + 1;

            lbc.fill = GridBagConstraints.NONE;

            lbc.ipadx = 15;

            lbc.weightx = 1.0;



            JLabel l = new JLabel(notes[j], JLabel.CENTER);

            panel.add(l, lbc);


            // Create the text field and set the value to the old value, if
            // it exists.

            prefs[j] = new JTextField(1);

            prefs[j].setHorizontalAlignment(javax.swing.JTextField.TRAILING);

            if( oldProbs == null || oldProbs.size() > i )
              {
                prefs[j].setText(oldProbs.get(i)[j].getText());
              }
            else
              {
                prefs[j].setText("1.0");
              }

             prefs[j].setCaretPosition(0);



            // Add event listeners to watch this field's input; we
            // need to make sure that we don't allow bad strings to be
            // input.

            prefs[j].addActionListener(new java.awt.event.ActionListener()
            {

            public void actionPerformed(java.awt.event.ActionEvent evt)
              {
               verifyProbs();
              }

            });

            prefs[j].addFocusListener(new java.awt.event.FocusAdapter()
            {

            public void focusLost(java.awt.event.FocusEvent evt)
              {
                verifyProbs();
              }
            });


            // Add the new box just below its corresponding label.

            GridBagConstraints gbc = new GridBagConstraints();

            gbc.gridx = j;

            gbc.gridy = (i * 3) + 2;

            gbc.fill = GridBagConstraints.NONE;

            gbc.ipadx = 25;

            gbc.weightx = 1.0;

            panel.add(prefs[j], gbc);
          }
        lickPrefs.add(prefs);
      }
      }
    
    JScrollPane sp = new JScrollPane(panel);

    sp.getVerticalScrollBar().setUnitIncrement(10);

    chordProbPanel.add(sp);

    chordProbPanel.setPreferredSize(new Dimension(600, 200));

   // We have to call validate before anything will appear on the screen.

    validate();

    // If we have auto-fill turned on, then calculate the new probabilities

    if( autoFill )
      {
        FillProbsButtonActionPerformed(null);
      }
    // Otherwise, we need to store the old ones and use those instead, but we need
    // to make sure that we don't try to write into text fields that aren't there.
    else
      {
        ArrayList<double[]> probs = new ArrayList<double[]>();

        for( int i = 0; i < lickPrefs.size(); ++i )
          {

            double[] p = new double[12];

            for( int j = 0; j < 12; ++j )
              {
                p[j] = Notate.quietDoubleFromTextField(lickPrefs.get(i)[j], 0.0, Double.POSITIVE_INFINITY, 0.0);
              }
            probs.add(p);
          }
         lickgen.setProbs(probs);
     
      }
     // This causes the frame to be resized, which is annoying: generatorFrame.pack();
  }



/**
 * Set the abstract melody field (formerly called "rhythm" field).
@param string
 */

public void setRhythmFieldText(String string)
  {
    rhythmField.setText(string);
    rhythmField.setCaretPosition(0);
    rhythmScrollPane.getViewport().setViewPosition(new Point(0, 0));
  }

    
public MelodyPart fillMelody(int beatValue,
                             Polylist rhythmString, 
                             ChordPart chordProg,
                             int start)
  {
   //debug System.out.println("LickgenFrame: fillMelody");

    MelodyPart result = lickgen.fillMelody(minPitch, 
                                           maxPitch, 
                                           minInterval, 
                                           maxInterval,
                                           beatValue, 
                                           leapProb, 
                                           rhythmString, 
                                           chordProg,
                                           start, 
                                           avoidRepeats);

    //debug System.out.println("fillMelody returns");
    return result;
  }

 private void playSelection()
    {
    notate.getCurrentStave().playSelection(false, notate.getLoopCount(), PlayScoreCommand.USEDRUMS, "LickGenFrame");
    }
public void stopPlaying()
  {
  notate.stopPlaying();
  }

/**
 *Make sure the user has entered acceptable values for each of the other fields
 * in the triage frame.
 */

public void verifyTriageFields()
  {
    //notate.toCritic();

    minPitch = notate.intFromTextField(minPitchField, LickGen.MIN_PITCH,
                                       maxPitch, minPitch);

    maxPitch = notate.intFromTextField(maxPitchField, minPitch,
                                       LickGen.MAX_PITCH, maxPitch);

    minInterval = notate.intFromTextField(minIntervalField,
                                          LickGen.MIN_INTERVAL_SIZE, maxInterval,
                                          minInterval);

    maxInterval = notate.intFromTextField(maxIntervalField, minInterval,
                                          LickGen.MAX_INTERVAL_SIZE, maxInterval);

    minDuration = notate.intFromTextField(minDurationField, maxDuration,
                                          LickGen.MIN_NOTE_DURATION, minDuration);

    maxDuration = notate.intFromTextField(maxDurationField,
                                          LickGen.MAX_NOTE_DURATION, minDuration,
                                          maxDuration);

    restProb = notate.doubleFromTextField(restProbField, 0.0, 1.0, restProb);

    leapProb = notate.doubleFromTextField(leapProbField, 0.0, 1.0, leapProb);

    chordToneWeight = notate.doubleFromTextField(chordToneWeightField, 0.0,
                                                 Double.POSITIVE_INFINITY,
                                                 chordToneWeight);

    scaleToneWeight = notate.doubleFromTextField(scaleToneWeightField, 0.0,
                                                 Double.POSITIVE_INFINITY,
                                                 scaleToneWeight);

    colorToneWeight = notate.doubleFromTextField(colorToneWeightField, 0.0,
                                                 Double.POSITIVE_INFINITY,
                                                 colorToneWeight);

    chordToneDecayRate = notate.doubleFromTextField(chordToneDecayField, 0.0,
                                                    Double.POSITIVE_INFINITY,
                                                    chordToneDecayRate);

    totalBeats = notate.doubleFromTextField(totalBeatsField, 0.0,
                                            Double.POSITIVE_INFINITY, 0.0);
    /*

    Integer.parseInt(partBarsTF1.getText()) * score.getMetre()[0] - (getCurrentSelectionStart() / beatValue),

    Math.min(totalBeats, Integer.parseInt(partBarsTF1.getText()) * score.getMetre()[0] - (getCurrentSelectionStart() / beatValue)));
     */
    totalBeats = Math.round(totalBeats);

    totalSlots = (int) (BEAT * totalBeats);

    notate.getCurrentStave().repaint();
  }

public void resetTriageParameters(boolean menu)
  {
  try
    {
    minPitchField.setText(lickgen.getParameter(LickGen.MIN_PITCH_STRING));

    minPitch = Integer.parseInt(lickgen.getParameter(LickGen.MIN_PITCH_STRING));

    maxPitchField.setText(lickgen.getParameter(LickGen.MAX_PITCH_STRING));

    maxPitch = Integer.parseInt(lickgen.getParameter(LickGen.MAX_PITCH_STRING));

    minDurationField.setText(lickgen.getParameter(LickGen.MIN_DURATION));

    minDuration = Integer.parseInt(lickgen.getParameter(LickGen.MIN_DURATION));

    maxDurationField.setText(lickgen.getParameter(LickGen.MAX_DURATION));

    maxDuration = Integer.parseInt(lickgen.getParameter(LickGen.MAX_DURATION));

    minIntervalField.setText(lickgen.getParameter(LickGen.MIN_INTERVAL));

    minInterval = Integer.parseInt(lickgen.getParameter(LickGen.MIN_INTERVAL));

    maxIntervalField.setText(lickgen.getParameter(LickGen.MAX_INTERVAL));

    maxInterval = Integer.parseInt(lickgen.getParameter(LickGen.MAX_INTERVAL));

    restProbField.setText(lickgen.getParameter(LickGen.REST_PROB));

    restProb = Double.parseDouble(lickgen.getParameter(LickGen.REST_PROB));

    leapProbField.setText(lickgen.getParameter(LickGen.LEAP_PROB));

    leapProb = Double.parseDouble(lickgen.getParameter(LickGen.LEAP_PROB));

    chordToneWeightField.setText(lickgen.getParameter(LickGen.CHORD_TONE_WEIGHT));

    chordToneWeight = Double.parseDouble(lickgen.getParameter(
        LickGen.CHORD_TONE_WEIGHT));

    colorToneWeightField.setText(lickgen.getParameter(LickGen.COLOR_TONE_WEIGHT));

    colorToneWeight = Double.parseDouble(lickgen.getParameter(
        LickGen.COLOR_TONE_WEIGHT));

    scaleToneWeightField.setText(lickgen.getParameter(LickGen.SCALE_TONE_WEIGHT));

    scaleToneWeight = Double.parseDouble(lickgen.getParameter(
        LickGen.SCALE_TONE_WEIGHT));

    chordToneDecayField.setText(lickgen.getParameter(LickGen.CHORD_TONE_DECAY));

    chordToneDecayRate = Double.parseDouble(lickgen.getParameter(
        LickGen.CHORD_TONE_DECAY));

    autoFillCheckBox.setSelected(Boolean.parseBoolean(lickgen.getParameter(
        LickGen.AUTO_FILL)));

    autoFill = Boolean.parseBoolean(lickgen.getParameter(LickGen.AUTO_FILL));

    rectify = Boolean.parseBoolean(lickgen.getParameter(LickGen.RECTIFY));

    rectifyCheckBox.setSelected(rectify);

    useGrammar = true; // Boolean.parseBoolean(lickgen.getParameter(LickGen.USE_GRAMMAR));
    
    useGrammarMI1.setSelected(useGrammar);
    useGrammarAction();

    avoidRepeats = Boolean.parseBoolean(lickgen.getParameter(
        LickGen.AVOID_REPEATS));

    if( menu )
      {
        int rootIndex = ((DefaultComboBoxModel) rootComboBox.getModel()).getIndexOf(
            lickgen.getParameter(LickGen.SCALE_ROOT));

        int scaleIndex = ((DefaultComboBoxModel) scaleComboBox.getModel()).getIndexOf(
            lickgen.getParameter(LickGen.SCALE_TYPE));

        rootComboBox.setSelectedIndex(rootIndex);

        scaleComboBox.setSelectedIndex(scaleIndex);

        lickgen.setPreferredScale(lickgen.getParameter(LickGen.SCALE_ROOT),
                                  lickgen.getParameter(LickGen.SCALE_TYPE));
      }
    }
  catch( Exception e )
    {
      
    }
  }

/**
 * Builds an association list with all of the parameters of the grammar.
 * On saving a file, additional parameters may be added within Lickgen.
 */

 
public void saveTriageParameters()
  {
    lickgen.clearParams();

    lickgen.setParameter(LickGen.MIN_PITCH_STRING, minPitch);

    lickgen.setParameter(LickGen.MAX_PITCH_STRING, maxPitch);

    lickgen.setParameter(LickGen.MIN_DURATION, minDuration);

    lickgen.setParameter(LickGen.MAX_DURATION, maxDuration);

    lickgen.setParameter(LickGen.MIN_INTERVAL, minInterval);

    lickgen.setParameter(LickGen.MAX_INTERVAL, maxInterval);

    lickgen.setParameter(LickGen.REST_PROB, restProb);

    lickgen.setParameter(LickGen.LEAP_PROB, leapProb);

    lickgen.setParameter(LickGen.CHORD_TONE_WEIGHT, chordToneWeight);

    lickgen.setParameter(LickGen.COLOR_TONE_WEIGHT, colorToneWeight);

    lickgen.setParameter(LickGen.SCALE_TONE_WEIGHT, scaleToneWeight);

    lickgen.setParameter(LickGen.CHORD_TONE_DECAY, chordToneDecayRate);

    lickgen.setParameter(LickGen.AUTO_FILL, autoFill);

    lickgen.setParameter(LickGen.RECTIFY, rectifyCheckBox.isSelected());
    
    lickgen.setParameter(LickGen.USE_GRAMMAR, useGrammar);

    lickgen.setParameter(LickGen.AVOID_REPEATS, avoidRepeats);

    lickgen.setParameter(LickGen.SCALE_ROOT, rootComboBox.getSelectedItem());

    lickgen.setParameter(LickGen.SCALE_TYPE, scaleComboBox.getSelectedItem());
    
    // These should not have to go to Lickgen to set stuff back in Lickgen,
    // but it's convenient to do it this way for now.
    
    lickgen.setParameter(LickGen.USE_SYNCOPATION, lickgen.getUseSyncopation());
    
    lickgen.setParameter(LickGen.SYNCOPATION_TYPE, lickgen.getSyncopationType());
    
    lickgen.setParameter(LickGen.SYNCOPATION_MULTIPLIER, lickgen.getSyncopationMultiplier());
    
    lickgen.setParameter(LickGen.SYNCOPATION_CONSTANT, lickgen.getSyncopationConstant());
    
    lickgen.setParameter(LickGen.EXPECTANCY_MULTIPLIER, lickgen.getExpectancyMultiplier());
    
    lickgen.setParameter(LickGen.EXPECTANCY_CONSTANT, lickgen.getExpectancyConstant());
  }

public void verifyAndFill()
  {
  verifyTriageFields();

  if( autoFill )
    {
    FillProbsButtonActionPerformed(null);
    }
  }

private void triageAndGenerate(int number)
{
    triageLick(saveLickTF.getText(), number);
    if (continuallyGenerate)
    {
       generateLickButtonActionPerformed(null);
    }
}
                        private void openGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openGrammarMI1ActionPerformed
                            notate.openGrammar();
                        }//GEN-LAST:event_openGrammarMI1ActionPerformed

                        private void showLogMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showLogMI1ActionPerformed
                            notate.openLog();
                        }//GEN-LAST:event_showLogMI1ActionPerformed

                        private void saveGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGrammarMI1ActionPerformed
                            notate.saveGrammarAs();
                        }//GEN-LAST:event_saveGrammarMI1ActionPerformed

                        private void editGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editGrammarMI1ActionPerformed
                            notate.editGrammar();
                        }//GEN-LAST:event_editGrammarMI1ActionPerformed

                        private void reloadGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reloadGrammarMI1ActionPerformed
                            notate.loadGrammar();
                            updateUseSoloist();
                        }//GEN-LAST:event_reloadGrammarMI1ActionPerformed

                        private void toCriticMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toCriticMI1ActionPerformed
                            notate.toCritic();
                        }//GEN-LAST:event_toCriticMI1ActionPerformed

                        private void showCriticMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showCriticMI1ActionPerformed
                            notate.showCritic();
                        }//GEN-LAST:event_showCriticMI1ActionPerformed

                        private void useGrammarMI1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useGrammarMI1ActionPerformed
                            useGrammarAction();
                        }//GEN-LAST:event_useGrammarMI1ActionPerformed

                        private void grammarMenu1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grammarMenu1ActionPerformed
                            // TODO add your handling code here:
                        }//GEN-LAST:event_grammarMenu1ActionPerformed

                        private void closeWindowMI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMI2ActionPerformed
                            closeWindow();
                        }//GEN-LAST:event_closeWindowMI2ActionPerformed
public void closeWindow()
  {
  this.setVisible(false);
  
  // Used to prevent the improvise button from using the critic filter
  // since the Improvise button uses the same lick generation method.
  useCriticCheckBox.setSelected(false);
  criticGrade = DEFAULT_GRADE; // Reset default grade
  useCriticCheckBoxMouseClicked(null);
  
  WindowRegistry.unregisterWindow(this);
  }

private void useGrammarAction()
  {
    useGrammar = useGrammarMI1.isSelected();

    fillMelodyButton.setEnabled(useGrammar);

    genRhythmButton.setEnabled(useGrammar);

    minDurationField.setEnabled(!useGrammar);

    maxDurationField.setEnabled(!useGrammar);

    restProbField.setEnabled(!useGrammar);
  }

public boolean getUseGrammar()
  {
    
    //System.out.println("useGrammar = " + useGrammar);
    return useGrammar;
  }

                        private void cascadeMI2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMI2ActionPerformed
                                    WindowRegistry.cascadeWindows(this);
                        }//GEN-LAST:event_cascadeMI2ActionPerformed

                        private void generatorWindowMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_generatorWindowMenu1MenuSelected

    generatorWindowMenu1.removeAll();

        generatorWindowMenu1.add(closeWindowMI2);

        generatorWindowMenu1.add(cascadeMI2);

        generatorWindowMenu1.add(windowMenuSeparator2);

        for(WindowMenuItem w : WindowRegistry.getWindows()) {

            generatorWindowMenu1.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
        }

        generatorWindowMenu1.repaint();

                        }//GEN-LAST:event_generatorWindowMenu1MenuSelected

                        private void closeWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeWindow
                            closeWindow();
                        }//GEN-LAST:event_closeWindow

    private void weightFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_weightFileButtonActionPerformed
        JFileChooser openDialog = new JFileChooser(ImproVisor.getVocabDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Weight File", "save");
        openDialog.setFileFilter(filter);
        openDialog.setDialogType(JFileChooser.OPEN_DIALOG);

        if(openDialog.showDialog(this, "Open") != JFileChooser.APPROVE_OPTION)
        return;

        File file = openDialog.getSelectedFile();
        weightFileTextField.setText(file.getName());
    }//GEN-LAST:event_weightFileButtonActionPerformed

    private void moveLayerDownTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveLayerDownTableButtonActionPerformed
        int row = layerDataTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
        if (row != layerDataTable.getRowCount() - 1 && row != - 1
            && row != layerDataTable.getRowCount() - 2)
        {
            model.moveRow(row, row, row + 1);

            // Update index values
            resetIndexColumn(model);

            model.fireTableDataChanged();

            layerDataTable.getSelectionModel().setSelectionInterval(row + 1, row + 1);
        }
    }//GEN-LAST:event_moveLayerDownTableButtonActionPerformed

    private void moveLayerUpTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_moveLayerUpTableButtonActionPerformed
        int row = layerDataTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
        if (row != 0 && row != - 1 && row != layerDataTable.getRowCount() - 1)
        {
            model.moveRow(row, row, row - 1);

            // Update index values
            resetIndexColumn(model);

            model.fireTableDataChanged();

            layerDataTable.getSelectionModel().setSelectionInterval(row - 1, row - 1);
        }
    }//GEN-LAST:event_moveLayerUpTableButtonActionPerformed

    private void removeLayerFromTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLayerFromTableButtonActionPerformed
        if (layerDataTable.getRowCount() <= 2)
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Must have at least two layers for network."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }
        else
        {
            int[] indices = layerDataTable.getSelectedRows();
            int lastIndex = layerDataTable.getRowCount() - 1;
            if (indices.length > layerDataTable.getRowCount() - 2)
            {
                JOptionPane.showMessageDialog(null,
                    new JLabel("<html><div style=\"text-align: center;\">"
                        + "Selected too many layers for deletion,<br/>"
                        + "must have at least two layers for network."),
                    "Alert", JOptionPane.PLAIN_MESSAGE);
            }
            else if (indices.length != 0)
            {
                DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
                for (int i = indices.length - 1; i >= 0; i--)
                {
                    int index = indices[i];
                    if (index != lastIndex)
                    model.removeRow(index);
                    model.fireTableRowsDeleted(index, index);
                }

                // Update index values
                resetIndexColumn(model);

                model.fireTableDataChanged();

                numberOfLayersTextField.setText(String.valueOf(layerDataTable.getRowCount()));
            }
        }
    }//GEN-LAST:event_removeLayerFromTableButtonActionPerformed

    private void addLayerToTableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLayerToTableButtonActionPerformed
        int index = layerDataTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();

        int nextIndex = layerDataTable.getRowCount();
        model.insertRow(nextIndex - 1, new Object[]{new Integer(nextIndex), new Integer(64), "Logsig"});
        model.setValueAt(new Integer(nextIndex + 1), nextIndex, 0);
        model.fireTableRowsInserted(nextIndex, nextIndex);
        layerDataTable.getSelectionModel().setSelectionInterval(nextIndex, nextIndex);
        numberOfLayersTextField.setText(String.valueOf(layerDataTable.getRowCount()));

        // Move row up to insert it below currently selected.
        if (index != -1 && index != layerDataTable.getRowCount() - 2)
        {
            model.moveRow(nextIndex -1 , nextIndex - 1, index + 1);

            // Update index values
            resetIndexColumn(model);

            model.fireTableDataChanged();

            layerDataTable.getSelectionModel().setSelectionInterval(index, index);
        }
    }//GEN-LAST:event_addLayerToTableButtonActionPerformed

    private void trainingFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trainingFileButtonActionPerformed
        JFileChooser openDialog = new JFileChooser(ImproVisor.getNNetDataDirectory());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Training Data", "data");
        openDialog.setFileFilter(filter);
        openDialog.setDialogType(JFileChooser.OPEN_DIALOG);

        if(openDialog.showDialog(this, "Open") != JFileChooser.APPROVE_OPTION)
        return;

        File file = openDialog.getSelectedFile();
        trainingFileTextField.setText(file.getAbsolutePath());
        String fileName = file.getName();
        int pos = fileName.lastIndexOf(".training.data");
        if (pos > 0)
        fileName = fileName.substring(0, pos);
        weightFileTextField.setText(fileName + ".weights.save");
    }//GEN-LAST:event_trainingFileButtonActionPerformed

    private void resetNetworkButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetNetworkButtonActionPerformed
        critic.resetNetwork();
        soloCorrectionPanel.setVisible(false);
        resetNnetInstructionsButtonActionPerformed(null);
        resetDefaultValuesButtonActionPerformed(null);
    }//GEN-LAST:event_resetNetworkButtonActionPerformed

    private void resetDefaultValuesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetDefaultValuesButtonActionPerformed
        resetNnetInstructionsButtonActionPerformed(null);
        trainingFileTextField.setText("");
        epochLimitTextField.setText("20000");
        learningRateTextField.setText("0.01");
        mseGoalTextField.setText("0.01");
        modeComboBox.setSelectedIndex(2);
        weightFileTextField.setText("");

        DefaultTableModel model = (DefaultTableModel) layerDataTable.getModel();
        model.setRowCount(0);
        model.addRow(new Object[]{new Integer(1),  new Integer(64), "Logsig"});
        model.addRow(new Object[]{new Integer(2),  new Integer(1), "Logsig"});
        numberOfLayersTextField.setText("2");

    }//GEN-LAST:event_resetDefaultValuesButtonActionPerformed

    private void resetNnetInstructionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetNnetInstructionsButtonActionPerformed
        nnetOutputTextField.setCaretPosition(0);
        nnetScrollPane.getVerticalScrollBar().setValue(0);
        nnetOutputTextField.setText("To generate a weight file:\n"
            + "-Select training file (File name will end with \".training.data\")\n"
            + "-Weight file name with automatically be set\n"
            + "--Weight file will save to personal settings folder, in vocab\n"
            + "-Change the epoch limit if desired\n"
            + "-Change the default learning rate if desired\n"
            + "-Change the default MSE goal if desired\n"
            + "-Change the default mode if desired\n"
            + "-In the table to the right:\n"
            + "--Set the layer size for each layer\n"
            + "---Input (first) layer size determinted at runtime from input size\n"
            + "---The last layer, for output, should be of size 1\n"
            + "--Set the function for each layer\n"
            + "--Reorder rows as desired. Empty rows will be ignored.\n"
            + "-Press \"Generate Weight File\"\n\nTo load network:\n"
            + "-Select the weight file, from the vocab folder, under \"Weight File\"\n"
            + "-Press \"Load Weight\"\n-Network will be initialized per leadsheet\n\n"
            + "To clear a weight file:\n-Select the weight file, from the vocab folder, "
            + "under \"Weight File\"\n-Press \"Clear Weight File\""
            + "\n\n***There is a sample weight file in impro-visor-version-X.xx-files/vocab\n"
            + "   for general use. The licks used to create it were subjectively graded,\n"
            + "   and therefore may not reflect the preferences of the user.");
    }//GEN-LAST:event_resetNnetInstructionsButtonActionPerformed

    private void loadWeightFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadWeightFileButtonActionPerformed
        // Attempt to initialize the network if it hasn't been initialize
        if (critic.getNetwork() == null)
        {
            try
            {
                StringBuilder weightOutput =
                critic.prepareNetwork(weightFileTextField.getText());

                nnetOutputTextField.setText(weightOutput.toString());
                nnetOutputTextField.setCaretPosition(0);
                nnetScrollPane.getVerticalScrollBar().setValue(0);
            }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(null,
                    new JLabel("<html><div style=\"text-align: center;\">"
                        + "Missing the weight file, <br/>"
                        + "need to train the network offline first<br/>"
                        + "and generate a weight file.<br/>"
                        + "Then enter the name of the file <br/>"
                        + "in the \"Weight File\" text field."),
                    "Alert", JOptionPane.PLAIN_MESSAGE);
            }
        }

        if (critic.getNetwork() != null && soloCorrectionPanel.isVisible())
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Network already initialized.<br/>"
                    + "Reset the network to load a new weight file."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }

        // If the network has been initialized, allow for critic use
        if (critic.getNetwork() != null)
        {
            soloCorrectionPanel.setVisible(true);
        }
    }//GEN-LAST:event_loadWeightFileButtonActionPerformed

    private void clearWeightFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearWeightFileButtonActionPerformed
        if (!weightFileTextField.getText().isEmpty())
        {
            try
            {
                String text = weightFileTextField.getText();
                if (text.endsWith(".weights.save"))
                {
                    File file = new File(ImproVisor.getVocabDirectory(), text);
                    if (file.exists())
                    file.delete();
                }
                else
                {
                    JOptionPane.showMessageDialog(null,
                        new JLabel("<html><div style=\"text-align: center;\">"
                            + "Attempting to delete a file<br/>"
                            + "that is not a weight file."),
                        "Alert", JOptionPane.PLAIN_MESSAGE);
                }
            }
            catch (Exception e)
            {
                // File won't exist
            }
        }
    }//GEN-LAST:event_clearWeightFileButtonActionPerformed

    private void getNetworkStatsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getNetworkStatsButtonActionPerformed
        if (critic.getNetwork() == null)
        {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Network not initialized,<br/>"
                    + "need to load the weights file."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }
        else
        {
            StringBuilder statOutput = critic.getNetwork().getStatistics();
            nnetOutputTextField.setCaretPosition(0);
            nnetScrollPane.getVerticalScrollBar().setValue(0);
            nnetOutputTextField.setText(statOutput.toString());
        }
    }//GEN-LAST:event_getNetworkStatsButtonActionPerformed

    private void generateWeightFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateWeightFileButtonActionPerformed
        int numRows = layerDataTable.getRowCount();
        int count = 0;
        ArrayList<Object[]> data = new ArrayList<Object[]>();
        boolean incompleteRows = false;

        for (int i = 0; i < numRows ; i++)
        {
            try
            {
                int size = (Integer) layerDataTable.getValueAt(i, 1);
                String type = (String) layerDataTable.getValueAt(i, 2);
                Object[] items = new Object[2];
                items[0] = size;
                items[1] = type;
                data.add(items);
                count++;
            }
            catch (Exception e)
            {
                incompleteRows = true;
            }
        }

        boolean badTextField = false;
        try
        {
            int i = Integer.parseInt(epochLimitTextField.getText());
            double j = Double.parseDouble(learningRateTextField.getText());
            double k = Double.parseDouble(mseGoalTextField.getText());
        }
        catch (Exception e)
        {
            badTextField = true;
        }

        if (trainingFileTextField.getText().isEmpty()
            || epochLimitTextField.getText().isEmpty()
            || learningRateTextField.getText().isEmpty()
            || mseGoalTextField.getText().isEmpty()
            || weightFileTextField.getText().isEmpty()
            || data.size() < 2
            || incompleteRows
            || badTextField)
        {
            StringBuilder output = new StringBuilder();
            if (trainingFileTextField.getText().isEmpty())
            output.append("    ").append("Training File").append("<br/>");
            if (epochLimitTextField.getText().isEmpty())
            output.append("    ").append("Epoch Limit").append("<br/>");
            if (learningRateTextField.getText().isEmpty())
            output.append("    ").append("Learning Rate").append("<br/>");
            if (mseGoalTextField.getText().isEmpty())
            output.append("    ").append("MSE Goal").append("<br/>");
            if (weightFileTextField.getText().isEmpty())
            output.append("    ").append("Weight File").append("<br/>");
            if (data.size() < 2)
            output.append("    ").append("Too Few Layers").append("<br/>");
            if (incompleteRows)
            output.append("    ").append("Incomplete layer, delete or complete").append("<br/>");
            if (badTextField)
            output.append("    ").append("Incorrect value(s) for numeric field(s)").append("<br/>");

            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Missing the following needed values:<br/>"
                    + output.toString()),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }

        else
        {
            critic.trainNetwork(trainingFileTextField.getText(),
                epochLimitTextField.getText(),
                learningRateTextField.getText(),
                mseGoalTextField.getText(),
                Integer.toString(modeComboBox.getSelectedIndex()),
                weightFileTextField.getText(),
                count,
                data);
        }
    }//GEN-LAST:event_generateWeightFileButtonActionPerformed

    private void layerDataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_layerDataTableMouseClicked
        int row = layerDataTable.rowAtPoint(evt.getPoint());
        int column = layerDataTable.columnAtPoint(evt.getPoint());

        if (row == -1 || column == -1)
        {
            ListSelectionModel model = layerDataTable.getSelectionModel();
            model.removeSelectionInterval(0, layerDataTable.getRowCount());
            model.removeSelectionInterval(0, layerDataTable.getColumnCount());
        }
    }//GEN-LAST:event_layerDataTableMouseClicked

    private void stopSoloPlayBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopSoloPlayBtnActionPerformed
        stopPlaying();
    }//GEN-LAST:event_stopSoloPlayBtnActionPerformed

    private void playSoloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playSoloBtnActionPerformed
        playSelection();
    }//GEN-LAST:event_playSoloBtnActionPerformed

    private void pasteThemeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteThemeBtnActionPerformed
        MelodyPart sel =
        notate.getCurrentStave().getDisplayPart().extract(
            notate.getCurrentSelectionStart(),
            notate.getCurrentSelectionEnd());
        Part.PartIterator i = sel.iterator();
        String theme = "";
        while( i.hasNext() )
        {
            theme += i.next().toLeadsheet() + " ";
        }

        themeField.setText(theme);
        themeLengthField.setText(sel.getSize() / BEAT + "");
    }//GEN-LAST:event_pasteThemeBtnActionPerformed

    private void genSoloThemeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSoloThemeBtnActionPerformed
        MelodyPart theme = generateTheme();
        generateSolo(theme, cm);
        playSelection();
    }//GEN-LAST:event_genSoloThemeBtnActionPerformed

    private void generateThemeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateThemeButtonActionPerformed
        generateTheme();
    }//GEN-LAST:event_generateThemeButtonActionPerformed

    private void generateSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateSoloButtonActionPerformed
        MelodyPart theme;
        if( themeField.getText().equals("") )
        {
            theme = generateTheme();
        }
        else
        {
            theme = new MelodyPart(
                themeField.getText().trim());
        }
        generateSolo(theme, cm);
        playSelection();
    }//GEN-LAST:event_generateSoloButtonActionPerformed

    private void testGenerationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testGenerationActionPerformed
        notate.generateFromButton();
    }//GEN-LAST:event_testGenerationActionPerformed

    private void toGrammarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toGrammarBtnActionPerformed
        notate.toGrammar();
    }//GEN-LAST:event_toGrammarBtnActionPerformed

    private void openCorpusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCorpusBtnActionPerformed
        notate.openCorpus();
        toFront();
    }//GEN-LAST:event_openCorpusBtnActionPerformed

    private void saveGrammarAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGrammarAsButtonActionPerformed
        notate.saveGrammarAs();
    }//GEN-LAST:event_saveGrammarAsButtonActionPerformed

    private void loadBaseGrammarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBaseGrammarBtnActionPerformed
        notate.openGrammar();
        notate.clearAccumulatedProductions();
    }//GEN-LAST:event_loadBaseGrammarBtnActionPerformed

    private void MarkovLengthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MarkovLengthFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_MarkovLengthFieldActionPerformed

    private void useMarkovCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useMarkovCheckboxActionPerformed

    }//GEN-LAST:event_useMarkovCheckboxActionPerformed

    private void useRelativeCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useRelativeCheckboxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_useRelativeCheckboxActionPerformed

    private void windowSlideFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_windowSlideFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_windowSlideFieldActionPerformed

    private void loadRandomGrammarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadRandomGrammarButtonActionPerformed
        // Load Random grammar for neural network lick generation
        notate.setGrammar("Random");
    }//GEN-LAST:event_loadRandomGrammarButtonActionPerformed

    private void criticGradeTextFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_criticGradeTextFieldFocusLost
        // Set lower limit for criticGrade filter
        String gradeField = criticGradeTextField.getText();
        if (!gradeField.equals(""))
        {
            try
            {
                int grade = Integer.parseInt(gradeField);
                // Boundary cases for lick filter
                if (grade < 1)
                {
                    criticGrade = 1;
                    criticGradeTextField.setText(String.valueOf(criticGrade));
                }
                else if (grade > 9)
                {
                    criticGrade = 9;
                    criticGradeTextField.setText(String.valueOf(criticGrade));
                }
                else
                {
                    criticGrade = grade;
                }
            }
            // Reset if the entry isn't an integer value
            catch (Exception e)
            {
                criticGradeTextField.setText("Grade");
            }
        }
        // Reset if empty entry
        else
        {
            criticGrade = DEFAULT_GRADE;
            criticGradeTextField.setText("Grade");
        }
    }//GEN-LAST:event_criticGradeTextFieldFocusLost

    private void criticGradeTextFieldFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_criticGradeTextFieldFocusGained
        criticGradeTextField.setText("");
    }//GEN-LAST:event_criticGradeTextFieldFocusGained

    private void useCriticCheckBoxMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_useCriticCheckBoxMouseClicked
        useCritic = useCriticCheckBox.isSelected();
        criticGradeTextField.setEnabled(useCritic);
        counterForCriticTextField.setEnabled(useCritic);
        // Reset all text fields
        if (!useCritic)
        {
            criticGradeTextField.setText("Grade");
            counterForCriticTextField.setText("Counter");
            lickFromStaveGradeTextField.setText("Grade");
        }
    }//GEN-LAST:event_useCriticCheckBoxMouseClicked

    private void gradeLickFromStaveButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeLickFromStaveButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        ArrayList<Note> noteList = new ArrayList<Note>();
        ArrayList<Chord> chordList = new ArrayList<Chord>();

        // Generate notes and chords over the lick
        critic.generateNotesAndChords(noteList, chordList, start, end);

        // Grade the lick, passing it through the critic filter
        Double gradeFromFilter = critic.gradeFromCritic(noteList, chordList);
        if (gradeFromFilter != null)
        {
            String formattedGrade = String.format("%.3f", gradeFromFilter);
            lickFromStaveGradeTextField.setText(formattedGrade);
        }

        else
        {
            lickFromStaveGradeTextField.setText("Error");
        }
    }//GEN-LAST:event_gradeLickFromStaveButtonActionPerformed

    private void regenerateLickForSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regenerateLickForSoloButtonActionPerformed
        gradeLickFromStaveButtonActionPerformed(null);
        double currGrade = Double.parseDouble(lickFromStaveGradeTextField.getText());
        if (currGrade < criticGrade)
        generateLickButtonActionPerformed(null);
    }//GEN-LAST:event_regenerateLickForSoloButtonActionPerformed

    private void gradeAllMeasuresButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeAllMeasuresButtonActionPerformed
        final int totalMeasures = notate.getCurrentStave().getNumMeasures();
        if (totalMeasures % 2 == 1)
        {
            offsetByMeasureGradeSoloButtonActionPerformed(null);
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Odd number of measures,<br/>"
                    + "offsetting grading by one measure."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }

        new Thread(new Runnable(){
            public void run() {

                // Mute since putlick() will play what it places on the leadsheet
                int volume = notate.getScore().getMasterVolume();
                notate.getScore().setMasterVolumeMuted(true);
                notate.setMasterVolumes(0);

                int thisTotalSlots = totalMeasures * WHOLE;
                int start = notate.getCurrentStave().getSelectionStart();
                int end = notate.getCurrentStave().getSelectionEnd();

                // Round to the nearest measure
                int numSlotsSelected = notate.getCurrentStave().roundToMultiple(end - start, WHOLE);

                // Iterate through all two measure selections
                while (start < thisTotalSlots && end < thisTotalSlots)
                {
                    ArrayList<Note> noteList = new ArrayList<Note>();
                    ArrayList<Chord> chordList = new ArrayList<Chord>();

                    // Generate notes and chords over the lick
                    critic.generateNotesAndChords(noteList, chordList, start, end);

                    // Grade the lick, passing it through the critic filter
                    Double gradeFromFilter = critic.gradeFromCritic(noteList, chordList);

                    // Default grade guarentees generating a new lick if there
                    // if an error
                    double grade = 0;
                    if (gradeFromFilter != null)
                    grade = gradeFromFilter;

                    if (grade < criticGrade)
                    generateLickButtonActionPerformed(null);

                    // Move forward by the selection length
                    start += numSlotsSelected;
                    end += numSlotsSelected;
                    notate.getCurrentStave().setSelection(start, end);
                }

                // Restore volume
                notate.getScore().setMasterVolumeMuted(false);
                notate.setMasterVolumes(volume);

                notate.getCurrentStave().play(0);

            } // End of Runnable
        }).start(); // End of Thread
    }//GEN-LAST:event_gradeAllMeasuresButtonActionPerformed

    private void resetSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetSelectionButtonActionPerformed
        notate.getCurrentStave().setSelection(0, 16 * EIGHTH - 1);
        notate.getCurrentStave().repaint();
    }//GEN-LAST:event_resetSelectionButtonActionPerformed

    private void backwardGradeSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_backwardGradeSoloButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        int numSlotsSelected = notate.getCurrentStave().roundToMultiple(end - start, WHOLE);

        // Move backwards by the selection length
        start -= numSlotsSelected;
        end -= numSlotsSelected;

        if (start >= 0)
        {
            notate.getCurrentStave().setSelection(start, end);
            notate.getCurrentStave().repaint();
        }
    }//GEN-LAST:event_backwardGradeSoloButtonActionPerformed

    private void forwardGradeSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forwardGradeSoloButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        int numSlotsSelected = notate.getCurrentStave().roundToMultiple(end - start, WHOLE);

        // Move forwards by the selection length
        start += numSlotsSelected;
        end += numSlotsSelected;
        int thisTotalSlots = notate.getCurrentStave().getNumMeasures() * WHOLE;

        if (start < thisTotalSlots && end < thisTotalSlots)
        {
            notate.getCurrentStave().setSelection(start, end);
            notate.getCurrentStave().repaint();
        }
    }//GEN-LAST:event_forwardGradeSoloButtonActionPerformed

    private void offsetByMeasureGradeSoloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offsetByMeasureGradeSoloButtonActionPerformed
        int start = notate.getCurrentStave().getSelectionStart();
        int end = notate.getCurrentStave().getSelectionEnd();

        // Move the selection two measures ahead
        start += 8 * EIGHTH;
        end += 8 * EIGHTH;
        int totalSlotsOffset = (notate.getCurrentStave().getNumMeasures() * WHOLE) - WHOLE;

        if (start < totalSlotsOffset)
        {
            notate.getCurrentStave().setSelection(start, end);
            notate.getCurrentStave().repaint();
        }
    }//GEN-LAST:event_offsetByMeasureGradeSoloButtonActionPerformed

    private void autoFillCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoFillCheckBoxActionPerformed
        autoFill = autoFillCheckBox.isSelected();

        if( autoFill )
        {
            redrawTriage();
        }
    }//GEN-LAST:event_autoFillCheckBoxActionPerformed

    private void FillProbsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FillProbsButtonActionPerformed

        if( notate.getCurrentSelectionStart() == -1 )
        {
            return;
        }

        ArrayList<double[]> probs = lickgen.fillProbs(
            notate.getChordProg(),
            chordToneWeight,
            scaleToneWeight,
            colorToneWeight,
            chordToneDecayRate,
            notate.getCurrentSelectionStart(),
            notate.getTotalSlots());

        for( int i = 0; i < Math.min(probs.size(), lickPrefs.size()); ++i )
        {
            double[] pArray = probs.get(i);
            JTextField[] tfArray = lickPrefs.get(i);
            for( int j = 0; j < Math.max(pArray.length, tfArray.length); ++j )
            {
                String p = ((Double) pArray[j]).toString();
                JTextField field = tfArray[j];
                field.setText(p);   //.substring(0, Math.min(p.length(), 5)));
            field.setCaretPosition(0);
        }
        }
    }//GEN-LAST:event_FillProbsButtonActionPerformed

    private void clearProbsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearProbsButtonActionPerformed

        for( int i = 0; i < lickPrefs.size(); ++i )
        {
            for( int j = 0; j < 12; ++j )
            {
                lickPrefs.get(i)[j].setText("0");
            }
        }
    }//GEN-LAST:event_clearProbsButtonActionPerformed

    private void gradeGoodBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeGoodBtnActionPerformed
        triageAndGenerate(9);
    }//GEN-LAST:event_gradeGoodBtnActionPerformed

    private void gradeAverageBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeAverageBtnActionPerformed
        triageAndGenerate(5);
    }//GEN-LAST:event_gradeAverageBtnActionPerformed

    private void gradeBadBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gradeBadBtnActionPerformed
        triageAndGenerate(1);
    }//GEN-LAST:event_gradeBadBtnActionPerformed

    private void grade10BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade10BtnActionPerformed
        triageAndGenerate(10);
    }//GEN-LAST:event_grade10BtnActionPerformed

    private void grade9BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade9BtnActionPerformed
        triageAndGenerate(9);
    }//GEN-LAST:event_grade9BtnActionPerformed

    private void grade8BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade8BtnActionPerformed
        triageAndGenerate(8);
    }//GEN-LAST:event_grade8BtnActionPerformed

    private void grade7BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade7BtnActionPerformed
        triageAndGenerate(7);
    }//GEN-LAST:event_grade7BtnActionPerformed

    private void grade6BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade6BtnActionPerformed
        triageAndGenerate(6);
    }//GEN-LAST:event_grade6BtnActionPerformed

    private void grade5BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade5BtnActionPerformed
        triageAndGenerate(5);
    }//GEN-LAST:event_grade5BtnActionPerformed

    private void grade4BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade4BtnActionPerformed
        triageAndGenerate(4);
    }//GEN-LAST:event_grade4BtnActionPerformed

    private void grade3BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade3BtnActionPerformed
        triageAndGenerate(3);
    }//GEN-LAST:event_grade3BtnActionPerformed

    private void grade2BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade2BtnActionPerformed
        triageAndGenerate(2);
    }//GEN-LAST:event_grade2BtnActionPerformed

    private void grade1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade1BtnActionPerformed
        triageAndGenerate(1);
    }//GEN-LAST:event_grade1BtnActionPerformed

    private void rootComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootComboBoxActionPerformed

        lickgen.setPreferredScale(
            (String) rootComboBox.getSelectedItem(),
            (String) scaleComboBox.getSelectedItem());

        redrawTriage();
    }//GEN-LAST:event_rootComboBoxActionPerformed

    private void scaleComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleComboBoxActionPerformed
        String root = (String) rootComboBox.getSelectedItem();

        String type = (String) scaleComboBox.getSelectedItem();

        if( root == null || type == null )
        {
            return;
        }

        if( type.equals("None") || type.equals(
            "Use First Scale") )
    {
        rootComboBox.setEnabled(false);
        }
        else
        {
            rootComboBox.setEnabled(true);
        }

        lickgen.setPreferredScale(
            (String) rootComboBox.getSelectedItem(),
            (String) scaleComboBox.getSelectedItem());

        redrawTriage();

        if( autoFill )
        {
            FillProbsButtonActionPerformed(null);
        }
    }//GEN-LAST:event_scaleComboBoxActionPerformed

    private void chordToneDecayFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chordToneDecayFieldFocusLost
        verifyAndFill();
    }//GEN-LAST:event_chordToneDecayFieldFocusLost

    private void chordToneDecayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordToneDecayFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_chordToneDecayFieldActionPerformed

    private void scaleToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scaleToneWeightFieldFocusLost
        verifyAndFill();
    }//GEN-LAST:event_scaleToneWeightFieldFocusLost

    private void scaleToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleToneWeightFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_scaleToneWeightFieldActionPerformed

    private void colorToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_colorToneWeightFieldFocusLost
        verifyAndFill();
    }//GEN-LAST:event_colorToneWeightFieldFocusLost

    private void colorToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToneWeightFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_colorToneWeightFieldActionPerformed

    private void chordToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chordToneWeightFieldFocusLost
        // TODO add your handling code here:
    }//GEN-LAST:event_chordToneWeightFieldFocusLost

    private void chordToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordToneWeightFieldActionPerformed
        verifyAndFill();
    }//GEN-LAST:event_chordToneWeightFieldActionPerformed

    private void styleRecognitionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_styleRecognitionButtonActionPerformed
        // First prepare critics for usage
        if (styleRecognitionButton.getText().equals("Prepare Critics")) {

            new Thread(new Runnable() {
                public void run() {

                    // Create list of critics for grading, paired with a musician's name
                    critics = new TreeMap<String, Critic>();

                    // Iterate through every weight file
                    File folder = ImproVisor.getStyleRecognitionDirectory();
                    File[] files = folder.listFiles();

                    Arrays.sort(files, new Comparator<File>() {
                        public int compare(File f1, File f2) {
                            return f1.getName().compareTo(f2.getName());
                        }
                    });

                    setRhythmFieldText("Preparing critics for grading...");

                    // Prepare all critics, and pair them with a file name
                    for (File f : files)
                    {
                        if (f.getName().endsWith(".weights.save"))
                        {
                            try
                            {
                                Critic currCritic = new Critic();
                                currCritic.prepareNetworkFromFile(f);

                                String fileName = f.getName();
                                int pos = fileName.lastIndexOf(".weights.save");
                                if (pos > 0)
                                fileName = fileName.substring(0, pos);
                                critics.put(fileName, currCritic);
                            }
                            catch (Exception e)
                            {
                                System.out.println("Problem with one file: " + f.getName());
                            }
                        }
                    }

                    setRhythmFieldText("");

                    if (critics.size() != numCritics)
                    {
                        JOptionPane.showMessageDialog(null,
                            new JLabel("<html><div style=\"text-align: center;\">"
                                + "This feature works best with the full set of critics.<br/>"
                                + "You have " + critics.size() + " out of the total " + numCritics + " critics.<br/>"
                                + "Please download the rest of the critics."),
                            "Using Critics", JOptionPane.PLAIN_MESSAGE);
                    }

                    styleRecognitionButton.setText("Guess Musician");

                } // End of Runnable
            }).start(); // End of Thread
        }

        // Do only if there is some selection
        else if(notate.getCurrentStave().getSelectionLength() != 0) {

            new Thread(new Runnable() {
                public void run() {

                    TreeMap<String, Double> grades = new TreeMap<String, Double>();

                    // Use all critics to get all grades for each network
                    for (String name : critics.keySet())
                    {
                        Critic thisCritic = critics.get(name);
                        int start = notate.getCurrentStave().getSelectionStart();
                        int end = notate.getCurrentStave().getSelectionEnd();

                        ArrayList<Note> noteList = new ArrayList<Note>();
                        ArrayList<Chord> chordList = new ArrayList<Chord>();

                        // Generate notes and chords over the lick
                        thisCritic.generateNotesAndChords(noteList, chordList, start, end);

                        // Grade the lick, passing it through the critic filter
                        Double gradeFromFilter = thisCritic.gradeFromCritic(noteList, chordList);
                        if (gradeFromFilter != null)
                        {
                            grades.put(name, gradeFromFilter);
                        }

                        else
                        {
                            System.out.println("Error from grading.");
                        }
                    }

                    // Output for extra content from critics
                    StringBuilder criticsOutput = new StringBuilder();

                    // Guess on stylistic similarity based on highest grade
                    double highestGrade = 0.0;
                    String likelyName = "";
                    for (String name : grades.keySet())
                    {
                        double currGrade = grades.get(name);

                        criticsOutput.append(fixName(name)).append(": ").append(String.format("%.3f", currGrade)).append("\n\n");

                        if (currGrade > highestGrade)
                        {
                            highestGrade = currGrade;
                            likelyName = name;
                        }
                    }

                    // Clean up formatting
                    String cleanName = fixName(likelyName);
                    String cleanGrade = String.format("%.3f", highestGrade);

                    setNetworkOutputTextField(criticsOutput.toString());

                    Object[] options = {"Yes, to Neural Network tab",
                        "Cancel"};
                    String label = "<html><div style=\"text-align: center;\">" +
                    "The musician whose style is most similar: <br/>" +
                    cleanName + "<br/><br/>" +
                    "Grade: " + cleanGrade + "<br/><br/>" +
                    "Choose \"Yes\" if you want to see more output";
                    int n = JOptionPane.showOptionDialog(null,
                        label,
                        "Style Recogntion",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                    if (n == 0)
                    {
                        // Avoids using a specific index for setting the tab
                        int index = 0;
                        for (int i = 0; i < generatorPane.getTabCount(); i++)
                        if (generatorPane.getTitleAt(i).contains("Network"))
                        index = i;
                        generatorPane.setSelectedIndex(index);
                    }

                } // End of Runnable
            }).start(); // End of Thread
        }

        else {
            JOptionPane.showMessageDialog(null,
                new JLabel("<html><div style=\"text-align: center;\">"
                    + "Choose a selection of measures before guessing."),
                "Alert", JOptionPane.PLAIN_MESSAGE);
        }
    }//GEN-LAST:event_styleRecognitionButtonActionPerformed

    private void generationSelectionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generationSelectionButtonActionPerformed
        if (notate.getCurrentStave().getLockSelectionWidth() == -1)
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
                catch( Exception e)
                {
                    measureNum = 2;
                }

                notate.getCurrentStave().lockSelectionWidth(measureNum * WHOLE);

                notate.getCurrentStave().repaint();

                generationSelectionButton.setText("Unlock selection");
            }
        }
        else
        {
            notate.getCurrentStave().unlockSelectionWidth();

            generationSelectionButton.setText("Size of Selection");
        }
    }//GEN-LAST:event_generationSelectionButtonActionPerformed

    private void continuallyGenerateCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_continuallyGenerateCheckBoxActionPerformed
        continuallyGenerate = continuallyGenerateCheckBox.isSelected();
    }//GEN-LAST:event_continuallyGenerateCheckBoxActionPerformed

    private void regenerateHeadDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regenerateHeadDataBtnActionPerformed
        notate.writeHeadData();
    }//GEN-LAST:event_regenerateHeadDataBtnActionPerformed

    private void useSoloistCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useSoloistCheckBoxActionPerformed
        updateUseSoloist();
    }//GEN-LAST:event_useSoloistCheckBoxActionPerformed

    private void rectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rectifyCheckBoxActionPerformed
        rectify = rectifyCheckBox.isSelected();
    }//GEN-LAST:event_rectifyCheckBoxActionPerformed

    private void gapFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gapFieldActionPerformed
        notate.setGenerationGap(Notate.doubleFromTextField(gapField, 0, 9.99, 1));
    }//GEN-LAST:event_gapFieldActionPerformed

    private void recurrentCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recurrentCheckboxActionPerformed
        notate.setRecurrent(recurrentCheckbox.isSelected());
    }//GEN-LAST:event_recurrentCheckboxActionPerformed

    private void avoidRepeatsCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avoidRepeatsCheckboxActionPerformed
        avoidRepeats = avoidRepeatsCheckbox.isSelected();
    }//GEN-LAST:event_avoidRepeatsCheckboxActionPerformed

    private void leapProbFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_leapProbFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_leapProbFieldFocusLost

    private void leapProbFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leapProbFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_leapProbFieldActionPerformed

    private void restProbFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_restProbFieldenterLickKeyPressed

    }//GEN-LAST:event_restProbFieldenterLickKeyPressed

    private void restProbFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_restProbFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_restProbFieldFocusLost

    private void restProbFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_restProbFieldGetsFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_restProbFieldGetsFocus

    private void restProbFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restProbFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_restProbFieldActionPerformed

    private void totalBeatsFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalBeatsFieldenterLickKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldenterLickKeyPressed

    private void totalBeatsFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalBeatsFieldFocusLost

        verifyTriageFields();

        notate.setCurrentSelectionEnd(
            notate.getCurrentSelectionStart() + totalSlots - 1);

        redrawTriage();

        pack();
    }//GEN-LAST:event_totalBeatsFieldFocusLost

    private void totalBeatsFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalBeatsFieldGetsFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldGetsFocus

    private void totalBeatsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalBeatsFieldActionPerformed

        verifyTriageFields();

        notate.setCurrentSelectionEnd(
            notate.getCurrentSelectionStart() + totalSlots - 1);

        redrawTriage();

        pack();
    }//GEN-LAST:event_totalBeatsFieldActionPerformed

    private void maxDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxDurationFieldenterLickKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxDurationFieldenterLickKeyPressed

    private void maxDurationFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxDurationFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_maxDurationFieldFocusLost

    private void maxDurationFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxDurationFieldGetsFocus

    }//GEN-LAST:event_maxDurationFieldGetsFocus

    private void maxDurationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDurationFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_maxDurationFieldActionPerformed

    private void minDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minDurationFieldenterLickKeyPressed

    }//GEN-LAST:event_minDurationFieldenterLickKeyPressed

    private void minDurationFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minDurationFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_minDurationFieldFocusLost

    private void minDurationFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minDurationFieldGetsFocus

    }//GEN-LAST:event_minDurationFieldGetsFocus

    private void minDurationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minDurationFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_minDurationFieldActionPerformed

    private void maxIntervalFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxIntervalFieldenterLickKeyPressed

    }//GEN-LAST:event_maxIntervalFieldenterLickKeyPressed

    private void maxIntervalFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxIntervalFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_maxIntervalFieldFocusLost

    private void maxIntervalFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxIntervalFieldGetsFocus

    }//GEN-LAST:event_maxIntervalFieldGetsFocus

    private void maxIntervalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxIntervalFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_maxIntervalFieldActionPerformed

    private void minIntervalFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minIntervalFieldenterLickKeyPressed

    }//GEN-LAST:event_minIntervalFieldenterLickKeyPressed

    private void minIntervalFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minIntervalFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_minIntervalFieldFocusLost

    private void minIntervalFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minIntervalFieldGetsFocus

    }//GEN-LAST:event_minIntervalFieldGetsFocus

    private void minIntervalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minIntervalFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_minIntervalFieldActionPerformed

    private void minPitchFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minPitchFieldenterLickKeyPressed

    }//GEN-LAST:event_minPitchFieldenterLickKeyPressed

    private void minPitchFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minPitchFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_minPitchFieldFocusLost

    private void minPitchFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minPitchFieldGetsFocus

    }//GEN-LAST:event_minPitchFieldGetsFocus

    private void minPitchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minPitchFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_minPitchFieldActionPerformed

    private void maxPitchFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxPitchFieldenterLickKeyPressed

    }//GEN-LAST:event_maxPitchFieldenterLickKeyPressed

    private void maxPitchFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxPitchFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_maxPitchFieldFocusLost

    private void maxPitchFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxPitchFieldGetsFocus

    }//GEN-LAST:event_maxPitchFieldGetsFocus

    private void maxPitchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxPitchFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_maxPitchFieldActionPerformed

    private void saveLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLickButtonActionPerformed
        notate.setLickTitle("<Generated Lick>");

        notate.openSaveLickFrame();
    }//GEN-LAST:event_saveLickButtonActionPerformed

    private void stopLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopLickButtonActionPerformed

        stopPlaying();
    }//GEN-LAST:event_stopLickButtonActionPerformed

    private void playLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playLickButtonActionPerformed
        playSelection();

    }//GEN-LAST:event_playLickButtonActionPerformed

    private void getSelRhythmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getSelRhythmButtonActionPerformed
        int selStart = notate.getCurrentSelectionStart();

        int selEnd = notate.getCurrentSelectionEnd();

        MelodyPart part = notate.getCurrentMelodyPart();

        int current = selStart;

        Polylist rhythmString = new Polylist();

        while( current <= selEnd )
        {
            StringBuilder sb = new StringBuilder();

            int value = part.getNote(current).
            getDurationString(sb, part.getNote(current).getRhythmValue());

            int rhythm = 0;

            if( part.getNote(current).isRest() )
            {
                rhythmString = rhythmString.cons("R" + sb.substring(1));
            }
            else
            {
                rhythmString = rhythmString.cons("X" + sb.substring(1));
            }
            current += part.getNote(current).getRhythmValue();
        }

        rhythmString = rhythmString.reverse();

        setRhythmFieldText(rhythmString.toString());
    }//GEN-LAST:event_getSelRhythmButtonActionPerformed

    private void getAbstractMelodyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_getAbstractMelodyButtonActionPerformed

        getAbstractMelody();

        }

        public void getAbstractMelody()
        {

            if( !allMeasures )
            {
                melodyData = notate.getMelodyData(notate.getSelectedIndex());
            }

            int minMeasureWindow = Integer.parseInt(windowSizeField.getText());
            int maxMeasureWindow = Integer.parseInt(windowSizeField.getText());

            int beatsToSlide = Integer.parseInt(windowSlideField.getText());

            //int measureWindow = 2;

            int selStart = notate.getCurrentSelectionStart();

            int selEnd = notate.getCurrentSelectionEnd();

            for( int measureWindow = minMeasureWindow; measureWindow <= maxMeasureWindow;
                measureWindow++ )
            {
                //int slotsPerMeasure = score.getMetre()[0] * BEAT; //assume something/4 time

                int slotsPerMeasure = BEAT;

                int slotsPerSection = slotsPerMeasure * measureWindow;

                int start = selStart - (selStart % slotsPerMeasure);

                int end = selEnd - (selEnd % slotsPerMeasure) + slotsPerMeasure - 1;

                int numMeasures = (end + 1 - start) / slotsPerSection;

                //writeBeatsToSlide(beatsToSlide);
                //loop through places to start the measure window
                for( int window = 0; window < measureWindow; window += beatsToSlide )
                {
                    //extract all sections of size measureWindow
                    for( int i = 0;
                        (i * slotsPerSection) + (window * BEAT) + slotsPerSection <= (numMeasures) * slotsPerSection;
                        i++ )
                    {
                        //System.out.println("Window: " + window);
                        //System.out.println("i: " + i);
                        String production = addMeasureToAbstractMelody(
                            start + (i * slotsPerSection) + (window * BEAT),
                            measureWindow,
                            i == 0);
                        if( production != null )
                        {
                            writeProduction(production, measureWindow,
                                (i * slotsPerSection) + (window * BEAT),
                                true);
                        }
                    }

                }

                lickgen.loadGrammar(notate.getGrammarFileName());
                updateUseSoloist();
                Grammar g = lickgen.getGrammar();
                Polylist rules = g.getRules();

                ArrayList<Polylist> ruleList = new ArrayList<Polylist>();
                for( Polylist L = rules; L.nonEmpty(); L = L.rest() )
                {
                    ruleList.add((Polylist) L.first());
                }
                Collections.sort(ruleList, new PolylistComparer());

                ArrayList<Polylist> newRules = new ArrayList<Polylist>();

                Polylist previous = Polylist.nil;
                float accumulatedProbability = 0;

                //Note - rules must have form similar to (rule (V4) (N4) 0.22)

                for( Iterator<Polylist> e = ruleList.iterator(); e.hasNext(); )
                {
                    Polylist current = e.next();
                    if( current.first().equals("rule") || current.first().equals("base") )
                    {
                        if( (!previous.equals(Polylist.nil)) && current.allButLast().equals(
                            previous.allButLast()) )
                    {
                        accumulatedProbability += ((Number) current.last()).floatValue();
                        int round = (int) (accumulatedProbability * 100);
                        accumulatedProbability = (float) (round / 100.0);
                    }
                    else
                    {
                        if( previous.nonEmpty() )
                        {
                            newRules.add(
                                Polylist.list(previous.first(), previous.second(),
                                    previous.third(),
                                    accumulatedProbability));
                        }
                        accumulatedProbability = ((Number) current.last()).floatValue();
                        previous = current;
                    }
                }
                else
                {
                    newRules.add(current);
                }
            }
            if( previous.nonEmpty() )
            {
                newRules.add(Polylist.list(previous.first(),
                    previous.second(),
                    previous.third(),
                    accumulatedProbability));
        }

        try
        {
            File f = new File(notate.getGrammarFileName());
            if( f.exists() )
            {
                System.gc();
                boolean deleted = f.delete();
                while( !deleted )
                {
                    deleted = f.delete();
                }
            }

            File f_out = new File(notate.getGrammarFileName());
            FileWriter out = new FileWriter(f_out, true);

            notate.setLickGenStatus(
                "Writing " + newRules.size() + " grammar rules to " + notate.getGrammarFileName());

            for( int i = 0; i < newRules.size(); i++ )
            {
                out.write(newRules.get(i).toString() + "\n");
            }
            out.close();

            notate.refreshGrammarEditor();

        }
        catch( Exception e )
        {
            System.out.println(e.getMessage());
        }

        }

        //Enter the whole selection into the window
        int slotsPerMeasure = BEAT;
        int start = selStart - (selStart % slotsPerMeasure);
        int end = selEnd - (selEnd % slotsPerMeasure) + slotsPerMeasure;
        int measureWindow = (end - start) / BEAT;

        String production = addMeasureToAbstractMelody(start, measureWindow, false);

        if( production != null )
        {
            if( production.contains("STARTER") )
            {
                production = production.replace("STARTER", "");
            }
            if( production.contains("ENDTIED") )
            {
                production = production.replace("ENDTIED ", "");
            }
            if( production.contains("STARTTIED") )
            {
                production = production.replace("STARTTIED ", "");
            }
            if( production.contains("CHORDS") )
            {
                production = production.substring(0, production.indexOf("CHORDS"));
            }
            setRhythmFieldText(production.toString());
        }

        }

        /**
        * add the production to file
        */

        public void writeProduction(String production, int measureWindow, int location,
            boolean writeExactMelody)
        {

            if( production == null )
            {
                return;
            }

            String chords = "";

            if( production.contains("CHORDS") )
            {
                chords = production.substring(production.indexOf("CHORDS"));
                production = production.substring(0, production.indexOf("CHORDS"));
            }

            try
            {
                File f = new File(notate.getGrammarFileName());
                String dir = f.getParentFile().getPath();
                BufferedWriter out = new BufferedWriter(new FileWriter(
                    dir + File.separator + Directories.accumulatedProductions, true));
            if( !writeExactMelody )
            {
                out.write(
                    "(rule (Seg" + measureWindow + ") " + production + " ) " + chords + "\n");
            }
            else
            {
                //check that index of exact melody matches index of abstract melody
                //then concatenate the two and write them to the file
                String melodyToWrite;
                String relativePitchMelody = "";
                String exactMelody = null; //= melodyData.get(location);
                String[] splitMel; // = exactMelody.split(" ");
                //if(!splitMel[0].equals(Integer.toString(location))) {
                    boolean foundMatch = false;
                    for( int i = 0; i < melodyData.size(); i++ )
                    {
                        splitMel = melodyData.get(i).split(" ");
                        if( splitMel[0].equals(Integer.toString(location)) )
                        {
                            exactMelody = melodyData.get(i);
                            foundMatch = true;
                            break;
                        }
                    }
                    if( foundMatch == false )
                    {
                        System.out.println("Weird. This shouldn't happen: " + location);
                    }
                    //}
                if( notate.getSelectedIndex() == 0 ) //head
                {
                    melodyToWrite = "Head " + exactMelody;
                }
                else
                {
                    melodyToWrite = "Chorus" + (notate.getSelectedIndex() + 1) + " " + exactMelody;
                }

                //this section converts a slice of melody to relative pitch notation (X notation)
                //get the chords for the section we want to convert to X notation
                int slotsPerSection = measureWindow*slotsPerBeat;
                ChordPart chordProg = notate.getChordProg().extract(location,
                    location + slotsPerSection - 1);
                ArrayList<Chord> allChords = chordProg.getChords();

                //split up the string containing melody info
                String[] exactMelodyData = exactMelody.split(" ");

                //first item is tells us the starting slot of this section of melody
                int startSlot = Integer.parseInt(exactMelodyData[0]);

                int chordNumber = 0; //index of the i-th chord in this measure we've looked at as a possible match for this note
                int totalChordDurationInMeasure = allChords.get(0).getRhythmValue(); //total number of slots belonging to chords we've looked at as a possible match for this note
                int totalNoteDurationInMeasure = 0; //total number of slots that have gone by in this measure up to this note
                for (int i = 1; i < exactMelodyData.length; i += 2) {
                    int pitch = Integer.parseInt(exactMelodyData[i]); //every odd index item is a note
                    int duration = Integer.parseInt(exactMelodyData[i + 1]); //every even index item (after 0) is a duration
                    while (totalNoteDurationInMeasure >= totalChordDurationInMeasure) { //we need to move on to the next chord
                        chordNumber++;
                        totalChordDurationInMeasure += allChords.get(chordNumber).getRhythmValue();
                    }
                    try {
                        if (pitch >= 0) { //pitch is a note
                            Note note = new Note(pitch, duration);
                            Polylist relativePitch = imp.lickgen.NotesToRelativePitch.noteToRelativePitch(note, allChords.get(chordNumber));
                            relativePitchMelody = relativePitchMelody.concat(relativePitch.toString());
                        } else { //"pitch" is a rest
                            String rest = "R" + imp.data.Note.getDurationString(duration) + " ";
                            relativePitchMelody = relativePitchMelody.concat(rest.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    totalNoteDurationInMeasure += duration;
                }
                //System.out.println("Relative pitch melody: " + relativePitchMelody);

                out.write("(rule (Seg" + measureWindow + ") " + production + " ) "
                    + "Xnotation " + relativePitchMelody + " " + melodyToWrite + " " + chords + "\n");
            }
            out.close();
        }

        catch( IOException e )
        {
            System.out.println("IO EXCEPTION!");
        }
        }

        //add the production to the grammar file
        public void addProduction(String production, int measureWindow, double prob) //formerly private
        {
            try
            {
                BufferedWriter out = new BufferedWriter(new FileWriter(
                    notate.getGrammarFileName(), true));
            out.write(
                "(rule (Seg" + measureWindow + ") " + production + " " + prob + ") \n");
            out.close();
        }
        catch( IOException e )
        {
            System.out.println("IO EXCEPTION!");
        }
        }

        public String addMeasureToAbstractMelody(int selStart, int measureWindow,
            boolean isSongStart)
        {
            //int slotsPerMeasure = score.getMetre()[0] * BEAT; //assume something/4 time
            int slotsPerSection = BEAT * measureWindow;
            //boolean isSongStart = (selStart == 0);
            int selEnd = selStart + slotsPerSection;
            MelodyPart part = notate.getCurrentMelodyPart().copy();

            if( part.melodyIsEmpty(selStart, slotsPerSection) )
            {
                //if this is empty, the last measure is empty,
                //and the rest of the chorus is empty, return null
                if( part.getFreeSlotsFromEnd() >= (part.size() - selEnd)
                    && part.melodyIsEmpty(selStart - slotsPerSection, slotsPerSection) )
                {
                    return null;
                }
                //otherwise return a section of rests
                else
                {
                    StringBuilder sb = new StringBuilder();
                    Note n = new Note(72, 1);
                    n.getDurationString(sb, slotsPerSection);
                    String returnString = "((slope 0 0 R" + sb.substring(1) + "))";
                    if( isSongStart )
                    {
                        returnString = returnString.concat("STARTER");
                    }
                    return returnString;
                }
            }

            int current = selStart;

            Polylist rhythmString = new Polylist();

            //pitches of notes in measure not including rests
            ArrayList<Integer> notes = new ArrayList<Integer>();

            //System.out.println("selStart: " + selStart);
            //System.out.println(part.getPrevNote(current));
            //System.out.print("rhythm of prevnote: " + part.getPrevNote(current).getRhythmValue());
            //System.out.println("slots per section: " + slotsPerSection);
            //System.out.println("Prev index: " + part.getPrevIndex(current));
            //if(part.getPrevNote(current) != null) System.out.println("not null.");
            //System.out.println("thing: " + (slotsPerSection - part.getPrevIndex(current) % slotsPerSection));

            boolean tiedAtStart = false, tiedAtEnd = false;

            //untie first note if it is tied from last measure
            if( part.getPrevNote(current) != null && part.getPrevNote(current).getRhythmValue() > current - part.getPrevIndex(
                current)/*slotsPerSection - part.getPrevIndex(current) % slotsPerSection*/ )
        {

            tiedAtStart = true;
            //System.out.println("Got here.");
            //
            //untie and set the previous note
            Note untiedNote = part.getPrevNote(current).copy();
            int originalRhythmVal = untiedNote.getRhythmValue();
            int rhythmVal = slotsPerSection - part.getPrevIndex(current) % slotsPerSection;
            untiedNote.setRhythmValue(rhythmVal);
            part.setNote(part.getPrevIndex(current), untiedNote);

            //set the current note
            rhythmVal = originalRhythmVal - rhythmVal;
            Note currNote = part.getPrevNote(current).copy();
            currNote.setRhythmValue(rhythmVal);
            part.setNote(current, currNote);
        }

        if( part.getPrevNote(selEnd) != null )
        {
            //untie notes at end of measure and beginning of next measure
            if( part.getPrevNote(selEnd).getRhythmValue() > selEnd - part.getPrevIndex(
                selEnd) )
        {
            tiedAtEnd = true;
            //System.out.println("Untying notes at end.");
            int tracker = part.getPrevIndex(selEnd);
            Note untiedNote = part.getNote(tracker).copy();
            int originalRhythmVal = untiedNote.getRhythmValue();
            int rhythmVal = slotsPerSection - (tracker % slotsPerSection);
            untiedNote.setRhythmValue(rhythmVal);
            part.setNote(tracker, untiedNote);
            int secondRhythmVal = originalRhythmVal - rhythmVal;
            untiedNote = part.getNote(tracker).copy();
            untiedNote.setRhythmValue(secondRhythmVal);
            part.setNote(selEnd, untiedNote);
        }
        }

        if( part.getPrevNote(selStart + 1) != null )
        {
            if( (part.getPrevIndex(selStart + 1) != selStart) && !(part.getPrevNote(
                selStart + 1).isRest()) )
    {
        //System.out.println("prev index: " + part.getPrevIndex(selStart + 1) + "note: " + part.getPrevNote(selStart + 1).getPitch());
        return null;
        }
        }

        //if(part.melodyIsEmpty(selStart, slotsPerSection)) {
            //    if(selStart - slotsPerSection >= slotsPerSection && part.melodyIsEmpty(selStart - slotsPerSection, slotsPerSection))
            // }
        while( current < selEnd )
        {

            //if the is a null note, make it a rest
            if( part.getNote(current) == null )
            {
                int next = part.getNextIndex(current);
                Note n = Note.makeRest(next - current);
                part.setNote(current, n);
            }

            StringBuilder sb = new StringBuilder();

            int value = part.getNote(current).getDurationString(sb, part.getNote(
                current).getRhythmValue());

        int pitch = part.getNote(current).getPitch();

        int rhythm = 0;

        if( part.getNote(current).isRest() )
        {
            rhythmString = rhythmString.cons("R" + sb.substring(1));
        }
        else
        {

            //add pitch to notes
            notes.add(pitch);
            //get note type
            char notetype;
            int[] notetone = lickgen.getNoteTypes(current, pitch, pitch,
                notate.getChordProg());
            switch( notetone[0] )
            {
                case LickGen.CHORD:
                notetype = 'C';
                break;
                case LickGen.COLOR:
                notetype = 'L';
                break;
                default:
                notetype = 'X';
                break;
            }
            if( notetype == 'X' && part.getNextNote(current) != null )
            {

                int nextPitch = part.getNextNote(current).getPitch();
                int nextIndex = part.getNextIndex(current);
                if( nextIndex <= selEnd )
                {
                    int pitchdiff = nextPitch - pitch;
                    if( Math.abs(pitchdiff) == 1 )
                    {
                        notetype = 'A';
                    }
                }
            }
            rhythmString = rhythmString.cons(notetype + sb.substring(1));
        }

        current = part.getNextIndex(current);

        }

        rhythmString = rhythmString.reverse();

        /*
        //add in goal notes to the rhythmString
        Polylist goalString = new Polylist();
        for (Polylist L = rhythmString; L.length() > 1; L = L.rest()) {

            String first = (String) L.first();
            String duration = first.substring(1);

            //get duration of slots of the first note
            int slots = Key.getDuration(duration);

            String second = (String) L.rest().first();

            //make chord tone goal note if followed by rest
            if (second.startsWith("R") && first.startsWith("C")) {
                first = first.replace('C', 'G');
            }

            //make chord tone quarter note or longer a goal note
            if (first.startsWith("C") && slots >= 120) {
                first = first.replace('C', 'G');
            }

            //make color tone quarter note or longer a goal note
            //if (first.startsWith("L") && slots >= 120) {
                //    first = first.replace('L', 'G');
                //}

            //make random quarter note or longer a goal note
            if(first.startsWith("X") && slots >= 120) {
                first = first.replace('X', 'G');
            }

            goalString = goalString.cons(first);

            //check last note
            if (L.length() == 2) {
                String lastDuration = second.substring(1);

                //get duration of slots of the first note
                int lastSlots = Key.getDuration(lastDuration);

                //make chord tone quarter note or longer a goal note
                if (second.startsWith("C") && lastSlots >= 120) {
                    second = second.replace('C', 'G');
                }

                //make color tone quarter note or longer a goal note
                //if (second.startsWith("L") && lastSlots >= 120) {
                    //    second = second.replace('L', 'G');
                    //}
                goalString = goalString.cons(second);
            }
        }

        //set rhythm string to have replaced the correct notes with goal notes
        goalString = goalString.reverse();
        rhythmString = goalString;

        */
        //process intervals
        ArrayList<Integer> intervals = new ArrayList<Integer>();
        intervals.add(0);
        for( int i = 1; i < notes.size(); i++ )
        {
            intervals.add(notes.get(i) - notes.get(i - 1));
        }
        //System.out.println("Intervals: " + intervals.size());
        //test intervals
        //for (int i = 0; i < intervals.size(); i++) {
            //    System.out.println("Interval: " + intervals.get(i));
            //}

        //process slopes
        ArrayList<int[]> slopes = new ArrayList<int[]>();
        int[] slope = new int[3];
        int tracker = 0;

        //get the slope from the note before this section to the first note in the measure
        int prevIndex = part.getPrevIndex(selStart);
        Note lastNote = part.getNote(prevIndex);
        while( lastNote != null && lastNote.isRest() )
        {
            prevIndex = part.getPrevIndex(prevIndex);
            lastNote = part.getNote(prevIndex);
        }
        int lastpitch = 0;
        if( lastNote != null && !lastNote.isRest() )
        {
            lastpitch = lastNote.getPitch();
        }
        int pitch = notes.get(0);
        int pitchChange;
        if( lastpitch == 0 )
        {
            pitchChange = 0;
        }
        else
        {
            pitchChange = pitch - lastpitch;
        }
        int minPitchChange = 0, maxPitchChange = 0;
        //avoid random notes and repeated notes
        if( pitchChange != 0 )
        {
            if( pitchChange == 1 )
            {
                minPitchChange = 1;
                maxPitchChange = 2;
            }
            else if( pitchChange == -1 )
            {
                minPitchChange = -2;
                maxPitchChange = -1;
            }
            else
            {
                minPitchChange = pitchChange - 1;
                maxPitchChange = pitchChange + 1;
            }
        }

        //if there is only 1 note, return it with its slope
        if( intervals.size() <= 1 )
        {

            String rhythm = rhythmString.toString();
            rhythm = rhythm.substring(1, rhythm.length() - 1);

            //handle case of only 1 note
            if( rhythm.equals("") )
            {
                char thisPitch = lickgen.getNoteType(selStart, notes.get(0), notes.get(
                    0), notate.getChordProg());
            String len = Note.getDurationString(slotsPerSection);
            rhythm = thisPitch + len;
        }
        String returnString =
        "((slope " + minPitchChange + " " + maxPitchChange + " " + rhythm + "))";
        if( isSongStart )
        {
            returnString = returnString.concat("STARTER");
        }
        if( tiedAtEnd )
        {
            returnString = returnString.concat(" ENDTIED");
        }
        if( tiedAtStart )
        {
            returnString = returnString.concat(" STARTTIED");
        }
        return returnString;
        }

        for( int i = 0; i < intervals.size(); i++ )
        {
            tracker = i;
            if( intervals.get(i) != 0 )
            {
                i = intervals.size();
            }
        }

        //direction is -1 if slope is going down, 0 for repeated note, 1 for up
        int direction = 0;
        if( intervals.get(tracker) > 0 )
        {
            direction = 1;
        }
        else if( intervals.get(tracker) < 0 )
        {
            direction = -1;
        }
        //initialize stuff - first note is in its own slope
        slope[0] = minPitchChange;
        slope[1] = maxPitchChange;
        slope[2] = 1;
        slopes.add(slope.clone());

        slope[0] = intervals.get(1);
        slope[1] = intervals.get(1);
        slope[2] = 0;
        for( int i = 1; i < intervals.size(); i++ )
        {
            //slope was going up but not any more
            if( direction == 1 && intervals.get(i) <= 0 )
            {
                if( intervals.get(i) == 0 )
                {
                    direction = 0;
                }
                else
                {
                    direction = -1;
                }
                if( slope[2] != 0 )
                {
                    slopes.add(slope.clone());
                }

                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
                //slope was going down but not any more
            }
            else if( direction == -1 && intervals.get(i) >= 0 )
            {
                if( intervals.get(i) == 0 )
                {
                    direction = 0;
                }
                else
                {
                    direction = 1;
                }
                if( slope[2] != 0 )
                {
                    slopes.add(slope.clone());
                }
                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
                //slope was 0 but not any more
            }
            else if( direction == 0 && intervals.get(i) != 0 )
            {
                if( intervals.get(i) > 0 )
                {
                    direction = 1;
                }
                else
                {
                    direction = -1;
                }
                if( slope[2] != 0 )
                {
                    slopes.add(slope.clone());
                }
                slope[0] = intervals.get(i);
                slope[1] = intervals.get(i);
                slope[2] = 1;
            }
            else
            {
                slope[2]++;
                if( intervals.get(i) > slope[1] )
                {
                    slope[1] = intervals.get(i);
                }
                if( intervals.get(i) < slope[0] )
                {
                    slope[0] = intervals.get(i);
                }
            }

            if( i == intervals.size() - 1 )
            {
                if( slope[2] != 0 )
                {
                    slopes.add(slope.clone());
                }
            }
        }

        //add in slopes
        StringBuilder strbuf = new StringBuilder();
        strbuf.append("(");
        Polylist tempString = rhythmString;
        for( int i = 0; i < slopes.size(); i++ )
        {
            slope = slopes.get(i);
            strbuf.append("(slope ");
            strbuf.append(slope[0] );
            strbuf.append(" ");
            strbuf.append(slope[1]);
            strbuf.append(" ");

            int j = 0;
            //get all of notes if last slope
            if( i == slopes.size() - 1 )
            {
                while( tempString.nonEmpty() )
                {
                    strbuf.append(tempString.first().toString());
                    strbuf.append(" ");
                    tempString = tempString.rest();
                }
            }
            else
            {
                while( j < slope[2] )
                {
                    String temp = tempString.first().toString();
                    strbuf.append(temp);
                    strbuf.append(" ");
                    //System.out.println(strbuf.toString());
                    tempString = tempString.rest();
                    if( temp.charAt(0) != 'R' )
                    {
                        j++;
                    }
                }
            }
            strbuf.deleteCharAt(strbuf.length() - 1);
            strbuf.append(")");
        }
        strbuf.append(")");
        //System.out.println("Abstract melody: " + strbuf.toString() + " Start: " + selStart + " End: " + selEnd);
        //if we are writing to file, write the chords, start data, and tie data
        /* Now the only option: if (writeProductionBtn.isSelected()) */
        {

            //Mark measure as 'songStarter' if it is the first of a song
            if( isSongStart )
            {
                strbuf.append("STARTER");
            }
            strbuf.append("CHORDS ");

            ChordPart chords = notate.getChordProg().extract(selStart,
                selStart + slotsPerSection - 1);
            ArrayList<Unit> chordList = chords.getUnitList();
            if( chordList.isEmpty() )
            {
                System.out.println("No chords");
            }
            for( int i = 0; i < chordList.size(); i++ )
            {
                String nextChord = ((Chord) chordList.get(i)).toLeadsheet();
                strbuf.append(nextChord);
                strbuf.append(" ");
            }
        }

        return strbuf.toString();
    }//GEN-LAST:event_getAbstractMelodyButtonActionPerformed

    private void fillMelodyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fillMelodyButtonActionPerformed

        String r = rhythmField.getText().trim();
        if( r.equals("") )
        {
            return; // no text specified
        }
        if( r.charAt(0) != '(' )
            {
                r = "(".concat(r);
            }

            if( r.charAt(r.length() - 1) != ')' )
        {
            r = r.concat(")");
        }

        setRhythmFieldText(r);

        Polylist rhythm = new Polylist();
        StringReader rhythmReader = new StringReader(r);
        Tokenizer in = new Tokenizer(rhythmReader);
        Object ob;

        while( (ob = in.nextSexp()) != Tokenizer.eof )
        {
            if( ob instanceof Polylist )
            {
                rhythm = (Polylist) ob;
            }
        }

        notate.generateAndPutLick(rhythm);
    }//GEN-LAST:event_fillMelodyButtonActionPerformed

    private void genRhythmButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genRhythmButtonActionPerformed

        verifyTriageFields();

        if( useGrammar )
        {
            setRhythmFieldText(
                lickgen.generateRhythmFromGrammar(0, notate.getTotalSlots()).toString());
        }
        else
        {
            setRhythmFieldText(lickgen.generateRandomRhythm(totalSlots,
                minDuration,
                maxDuration,
                restProb).toString());
        }
    }//GEN-LAST:event_genRhythmButtonActionPerformed

    private void generateLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateLickButtonActionPerformed
        notate.generateFromButton();
    }//GEN-LAST:event_generateLickButtonActionPerformed

private void updateUseSoloist()
  {
    if( useSoloistCheckBox.isSelected() && lickgen.soloistIsLoaded() )
      {
        notate.setLickGenStatus("Using Soloist file");
      }
    else
      {
        useSoloistCheckBox.setSelected(false);
        notate.setLickGenStatus("Non-Matching Soloist file or No Soloist file exists");        
      }    
  }  
    public void showCriticGrades()
    {
        grade1Btn.setVisible(false);
        grade2Btn.setVisible(false);
        grade3Btn.setVisible(false);
        grade4Btn.setVisible(false);
        grade5Btn.setVisible(false);
        grade6Btn.setVisible(false);
        grade7Btn.setVisible(false);
        grade8Btn.setVisible(false);
        grade9Btn.setVisible(false);
        grade10Btn.setVisible(false);
        
        gradeBadBtn.setVisible(true);
        gradeAverageBtn.setVisible(true);
        gradeGoodBtn.setVisible(true);
    }
    
    public void showAllGrades()
    {
        grade1Btn.setVisible(true);
        grade2Btn.setVisible(true);
        grade3Btn.setVisible(true);
        grade4Btn.setVisible(true);
        grade5Btn.setVisible(true);
        grade6Btn.setVisible(true);
        grade7Btn.setVisible(true);
        grade8Btn.setVisible(true);
        grade9Btn.setVisible(true);
        grade10Btn.setVisible(true);
        
        gradeBadBtn.setVisible(false);
        gradeAverageBtn.setVisible(false);
        gradeGoodBtn.setVisible(false);
    }
    
    // Return min duration text field
    public int getMinDuration()
    {
        return minDuration;
    }
    
    // Return max duration text field
    public int getMaxDuration()
    {
        return maxDuration;
    }
    
    // Return rest prob
    public double getRestProb()
    {
        return restProb;
    }
 
    // Return critic
    public Critic getCritic()
    {
        return critic;
    }
    
    // Return if the critic is selected and should be used
    public boolean useCritic()
    {
        return useCritic;
    }
    
    // Returns the lower-limit grade for the critic filter
    public int getCriticGrade()
    {
        return criticGrade;
    }
    
    // Sets the counter for the number of generations of licks
    public void setCounterForCriticTextField(int count)
    {
        counterForCriticTextField.setText(String.valueOf(count));
    }
    
    // Sets the name for the lick generator name text field
    public void setSaveLickTextField(String text)
    {
        saveLickTF.setText(text);
    }
    
    // Sets the grade text field with a given grade
    public void setLickFromStaveGradeTextField(Double grade)
    {
        lickFromStaveGradeTextField.setText(String.format("%.3f", grade));
    } 
    
    // Sets the neural network output text field
    public void setNetworkOutputTextField(String text)
    {
        nnetOutputTextField.setText(text);
        nnetOutputTextField.setCaretPosition(nnetOutputTextField.getText().length());
    }
    
    // Appends text to the neural network output text field
    public void appendNetworkOutputTextField(String text)
    {
        String currentText = nnetOutputTextField.getText();
        nnetOutputTextField.setText(currentText + text);
        nnetOutputTextField.setCaretPosition(nnetOutputTextField.getText().length());
    }
    
    // Changes if we are sending licks to the critic panel
    public void setToCriticDialog(boolean bool)
    {
        toCriticMI1.setSelected(bool);
    }
    
    // Re-number all rows, reseting the index of each row
    private void resetIndexColumn(DefaultTableModel model)
    {
        for (int i = 0; i < layerDataTable.getRowCount(); i++)
        {
            model.setValueAt(new Integer(i + 1), i, 0);
        }
    }
    
    // Cleanly print a musician's name
    private String fixName(String name)
    {
        int pos = 0;
        char[] chars = name.toCharArray();
        for (int i = chars.length - 1; i >= 0; i--)
            pos += Character.isUpperCase(chars[i]) ? i : 0;

        // Display in format "Firstname Lastname"
        name = name.substring(0, 1).toUpperCase() + 
                    name.substring(1, pos) + 
                    " " + 
                    name.substring(pos);
        return name;
    }
    
                        /**
                         * Generates a melody from the solo profile window. Takes the attribute ranges into account
                         * and uses the new rule expander paradigm.
                         */
                        /**
                         * If the no compute box is checked for a specific attribute, change the number of attributes to compute.
                         * @param evt
                         */                        /**
                         * Generates an abstract melody from the solo profile window. Takes the attribute ranges into account
                         * and uses the new rule expander paradigm.
                         */                        /**
                         * Fills the abstract melody in the abstract melody rhythm text field.
                         */

                        /**
                         * Saves a profile curve.
                         */                        /**
                         * Loads a profile curve.
                         */                        /**
                         * Resets graphs to flat lines, clears all check boxes and text fields.
                         */
    /**
     * Checks how many beats are selected in the current leadsheet.
     */
                        
    public void verifyBeats() {
        totalBeats = notate.doubleFromTextField(totalBeatsField, 0.0,
                Double.POSITIVE_INFINITY, 0.0);
        totalBeats = Math.round(totalBeats);
        totalSlots = (int) (BEAT * totalBeats);
        notate.getCurrentStave().repaint();
     }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FillProbsButton;
    private javax.swing.JLabel InvertProbLabel;
    private javax.swing.JTextField MarkovLengthField;
    private javax.swing.JPanel ProbFillClearPanel;
    private javax.swing.JLabel ReverseProbLabel;
    private javax.swing.JButton addLayerToTableButton;
    private javax.swing.JCheckBox autoFillCheckBox;
    private javax.swing.JCheckBox avoidRepeatsCheckbox;
    private javax.swing.JButton backwardGradeSoloButton;
    private javax.swing.JMenuItem cascadeMI2;
    private javax.swing.JPanel chordProbPanel;
    private javax.swing.JTextField chordToneDecayField;
    private javax.swing.JLabel chordToneDecayRateLabel;
    private javax.swing.JLabel chordToneProbLabel;
    private javax.swing.JTextField chordToneWeightField;
    private javax.swing.JButton clearProbsButton;
    private javax.swing.JButton clearWeightFileButton;
    private javax.swing.JMenuItem closeWindowMI2;
    private javax.swing.JLabel colorToneProbLabel;
    private javax.swing.JTextField colorToneWeightField;
    private javax.swing.JCheckBox continuallyGenerateCheckBox;
    private javax.swing.JLabel counterForCriticLabel;
    private javax.swing.JTextField counterForCriticTextField;
    private javax.swing.JLabel criticGradeLabel;
    private javax.swing.JTextField criticGradeTextField;
    private javax.swing.JLabel disclaimer;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JMenuItem editGrammarMI1;
    private javax.swing.JLabel epochLimitLabel;
    private javax.swing.JTextField epochLimitTextField;
    private javax.swing.JButton fillMelodyButton;
    private javax.swing.JLabel finalLabel;
    private javax.swing.JButton forwardGradeSoloButton;
    private javax.swing.JTextField gapField;
    private javax.swing.JButton genRhythmButton;
    private javax.swing.JButton genSoloThemeBtn;
    private javax.swing.JButton generateLickButton;
    private javax.swing.JButton generateSoloButton;
    private javax.swing.JButton generateThemeButton;
    private javax.swing.JButton generateWeightFileButton;
    private javax.swing.JLabel generationGapLabel;
    private javax.swing.JButton generationSelectionButton;
    private javax.swing.JMenuBar generatorMenuBar1;
    private javax.swing.JTabbedPane generatorPane;
    private javax.swing.JMenu generatorWindowMenu1;
    private javax.swing.JButton getAbstractMelodyButton;
    private javax.swing.JButton getNetworkStatsButton;
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
    private javax.swing.JButton gradeAllMeasuresButton;
    private javax.swing.JButton gradeAverageBtn;
    private javax.swing.JButton gradeBadBtn;
    private javax.swing.JButton gradeGoodBtn;
    private javax.swing.JLabel gradeLabel;
    private javax.swing.JButton gradeLickFromStaveButton;
    private javax.swing.JPanel grammarLearningPanel;
    private javax.swing.JMenu grammarMenu1;
    private javax.swing.JLabel intervalLabel;
    private javax.swing.JTextField invertProbabilityField;
    private javax.swing.JTable layerDataTable;
    private javax.swing.JScrollPane layerInfoScrollPane;
    private javax.swing.JTextField leapProbField;
    private javax.swing.JLabel leapProbLabel;
    private javax.swing.JLabel learningRateLabel;
    private javax.swing.JTextField learningRateTextField;
    private javax.swing.JLabel learningStep0Label;
    private javax.swing.JTextField lickFromStaveGradeTextField;
    private javax.swing.JPanel lickGenPanel;
    private javax.swing.JPanel lickGenerationButtonsPanel;
    private javax.swing.JPanel lickGradeButtonsPanel;
    private javax.swing.JLabel lickSavedLabel;
    private javax.swing.JPanel lickgenParametersPanel;
    private javax.swing.JButton loadBaseGrammarBtn;
    private javax.swing.JButton loadRandomGrammarButton;
    private javax.swing.JButton loadWeightFileButton;
    private javax.swing.JTextField maxDurationField;
    private javax.swing.JTextField maxIntervalField;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JTextField maxPitchField;
    private javax.swing.JTextField minDurationField;
    private javax.swing.JTextField minIntervalField;
    private javax.swing.JLabel minLabel;
    private javax.swing.JTextField minPitchField;
    private javax.swing.JComboBox modeComboBox;
    private javax.swing.JLabel modeLabel;
    private javax.swing.JButton moveLayerDownTableButton;
    private javax.swing.JButton moveLayerUpTableButton;
    private javax.swing.JLabel mseGoalLabel;
    private javax.swing.JTextField mseGoalTextField;
    private javax.swing.JPanel neuralNetworkPanel;
    private javax.swing.JPanel nnetOutputPanel;
    private javax.swing.JTextArea nnetOutputTextField;
    private javax.swing.JPanel nnetParametersPanel;
    private javax.swing.JScrollPane nnetScrollPane;
    private javax.swing.JPanel nnetWeightGenerationPanel;
    private javax.swing.JTextField numClusterRepsField;
    private javax.swing.JLabel numClusterRepsLabel;
    private javax.swing.JLabel numberOfLayersLabel;
    private javax.swing.JTextField numberOfLayersTextField;
    private javax.swing.JButton offsetByMeasureGradeSoloButton;
    private javax.swing.JButton openCorpusBtn;
    private javax.swing.JMenuItem openGrammarMI1;
    private javax.swing.JButton pasteThemeBtn;
    private javax.swing.JLabel pitchLabel;
    private javax.swing.JPanel pitchProbabilitiesPanel;
    private javax.swing.JButton playLickButton;
    private javax.swing.JButton playSoloBtn;
    private javax.swing.JCheckBox rectifyCheckBox;
    private javax.swing.JCheckBox recurrentCheckbox;
    private javax.swing.JButton regenerateHeadDataBtn;
    private javax.swing.JButton regenerateLickForSoloButton;
    private javax.swing.JMenuItem reloadGrammarMI1;
    private javax.swing.JButton removeLayerFromTableButton;
    private javax.swing.JButton resetDefaultValuesButton;
    private javax.swing.JButton resetNetworkButton;
    private javax.swing.JButton resetNnetInstructionsButton;
    private javax.swing.JButton resetSelectionButton;
    private javax.swing.JTextField restProbField;
    private javax.swing.JLabel restProbLabel;
    private javax.swing.JTextField reverseProbabilityField;
    private javax.swing.JTextArea rhythmField;
    private javax.swing.JPanel rhythmPanel;
    private javax.swing.JScrollPane rhythmScrollPane;
    private javax.swing.JComboBox rootComboBox;
    private javax.swing.JLabel rootLabel;
    private javax.swing.JButton saveGrammarAsButton;
    private javax.swing.JMenuItem saveGrammarMI1;
    private javax.swing.JButton saveLickButton;
    private javax.swing.JTextField saveLickTF;
    private javax.swing.JLabel saveLickWithLabelLabel;
    private javax.swing.JPanel scaleChoicePanel;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JLabel scaleToneProbLabel;
    private javax.swing.JTextField scaleToneWeightField;
    private javax.swing.JMenuItem showCriticMI1;
    private javax.swing.JMenuItem showLogMI1;
    private javax.swing.JPanel soloCorrectionPanel;
    private javax.swing.JPanel soloGenPanel;
    private javax.swing.JButton stopLickButton;
    private javax.swing.JButton stopSoloPlayBtn;
    private javax.swing.JButton styleRecognitionButton;
    private javax.swing.JPanel substitutorPanel;
    private javax.swing.JButton testGeneration;
    private javax.swing.JTextField themeField;
    private javax.swing.JLabel themeLabel;
    private javax.swing.JTextField themeLengthField;
    private javax.swing.JLabel themeLengthLabel;
    private javax.swing.JLabel themeProbLabel;
    private javax.swing.JTextField themeProbabilityField;
    private javax.swing.JCheckBoxMenuItem toCriticMI1;
    private javax.swing.JButton toGrammarBtn;
    private javax.swing.JPanel toneProbabilityPanel;
    private javax.swing.JTextField totalBeatsField;
    private javax.swing.JLabel totalBeatsLabel;
    private javax.swing.JButton trainingFileButton;
    private javax.swing.JTextField trainingFileTextField;
    private javax.swing.JLabel transposeProbLabel;
    private javax.swing.JTextField transposeProbabilityField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JCheckBox useCriticCheckBox;
    private javax.swing.JCheckBoxMenuItem useGrammarMI1;
    private javax.swing.JCheckBox useHeadCheckBox;
    private javax.swing.JCheckBox useMarkovCheckbox;
    private javax.swing.JCheckBox useRelativeCheckbox;
    private javax.swing.JCheckBox useSoloistCheckBox;
    private javax.swing.JButton weightFileButton;
    private javax.swing.JTextField weightFileTextField;
    private javax.swing.JSeparator windowMenuSeparator2;
    private javax.swing.JPanel windowParametersPanel;
    private javax.swing.JTextField windowSizeField;
    private javax.swing.JLabel windowSizeLabel;
    private javax.swing.JTextField windowSlideField;
    private javax.swing.JLabel windowSlideLabel;
    // End of variables declaration//GEN-END:variables

private void triageLick(String lickName, int grade)
  {
    notate.triageLick(lickName, grade);
  }

  public boolean getRecurrent()
  {
      return recurrentCheckbox.isSelected();
  }

  public void setRecurrent(boolean value)
    {
      recurrentCheckbox.setSelected(value);
      notate.setRecurrent(value);
    }
  
  public void setTotalBeats(double beats) {
        totalBeats = beats;
        totalBeatsField.setText("" + beats);
        String b = Integer.toString((int) beats);
        
  }

  public boolean toCriticSelected()
  {
      return toCriticMI1.isSelected();
  }

  public boolean useGrammarSelected()
  {
      return useGrammarMI1.isSelected();
  }

 public boolean rectifySelected()
  {
     return rectifyCheckBox.isSelected();
  }
 
 public boolean useCriticSelected()
 {
     return useCriticCheckBox.isSelected();
 }

 public boolean useHeadSelected()
  {
     return useHeadCheckBox.isSelected();
 }

 public boolean useSoloistSelected()
  {
     return useSoloistCheckBox.isSelected();
 }

public int getGap()
  {
    return (int) (BEAT * Notate.quietDoubleFromTextField(gapField, -Double.MAX_VALUE,
                                                  +Double.MAX_VALUE, 0));
  }

public void setGap(double value)
  {
    gapField.setText("" + value);
  }

public int getWindowSize()
  {
    return Integer.parseInt(windowSizeField.getText());
}

public int getWindowSlide()
  {
    return Integer.parseInt(windowSlideField.getText());
}

public int getNumClusterReps()
  {
    return Integer.parseInt(numClusterRepsField.getText());
}

public boolean useMarkovSelected()
  {
      return useMarkovCheckbox.isSelected();
  }


public int getMarkovFieldLength()
  {
    return Integer.parseInt(MarkovLengthField.getText());
}

public boolean getUseRelativePitches()
  {
    return useRelativeCheckbox.isSelected();
  }

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



public MelodyPart generateTheme() {
        themeLength = BEAT*Notate.intFromTextField(themeLengthField, 0, notate.getScoreLength() / BEAT, themeLength);
        Polylist rhythm = lickgen.generateRhythmFromGrammar(0, themeLength);

        verifyTriageFields();
        MelodyPart lick = fillMelody(BEAT, rhythm, notate.getChordProg(), 0);

            //lickgen.fillMelody(minPitch, maxPitch, minInterval, maxInterval, BEAT,
            //    leapProb, rhythm, chordProg, 0, avoidRepeats);

        Part.PartIterator i = lick.iterator();
        String theme = "";
        while (i.hasNext())
        {
          Unit unit = i.next();
          if( unit != null )
          {
            theme += unit.toLeadsheet() + " ";
          }
        }

        themeField.setText(theme);
        return lick;
    }

    public void generateSolo(MelodyPart theme, CommandManager cm) {
        int length = theme.getSize();
        themeLength = length;
        MelodyPart solo = new MelodyPart(length);
        imp.ImproVisor.setPlayEntrySounds(false);
        themeProb = Notate.doubleFromTextField(themeProbabilityField, 0, 1, themeProb);
        transposeProb = Notate.doubleFromTextField(transposeProbabilityField, 0, 1, transposeProb);
        invertProb = Notate.doubleFromTextField(invertProbabilityField, 0, 1, invertProb);
        reverseProb = Notate.doubleFromTextField(reverseProbabilityField, 0, 1, reverseProb);

        solo.pasteSlots(theme, 0);
        for (int i = length; i <= notate.getScoreLength() - length; i += length) {
            if (Notate.bernoulli(themeProb)) {
                MelodyPart adjustedTheme = theme.copy();
                if (Notate.bernoulli(transposeProb)) {
                    ChordPart chordProg = notate.getChordProg();
                    int rise = PitchClass.findRise(PitchClass.getPitchClass(chordProg.getCurrentChord(0).getRoot()),
                            PitchClass.getPitchClass(chordProg.getCurrentChord(i).getRoot()));
                    int index = 0;
                    Note n = adjustedTheme.getNote(index);
                    while (n.isRest()) {
                        index += n.getRhythmValue();
                        n = adjustedTheme.getNote(index);
                    }
                    if (n.getPitch() >= (minPitch + maxPitch) / 2 && rise > 0)
                        cm.execute(new ShiftPitchesCommand(-1 * (12 - rise), adjustedTheme,
                                0, length, 0, 128, notate.getScore().getKeySignature()));
                    else if (n.getPitch() < (minPitch + maxPitch) / 2 && rise < 0)
                        cm.execute(new ShiftPitchesCommand((12 + rise), adjustedTheme,
                                0, length, 0, 128, notate.getScore().getKeySignature()));
                    else
                        cm.execute(new ShiftPitchesCommand(rise, adjustedTheme, 0, length, 0, 128, notate.getScore().getKeySignature()));
                }

                if (Notate.bernoulli(invertProb))
                    cm.execute(new InvertCommand(adjustedTheme, 0, length, false));

                if (Notate.bernoulli(reverseProb))
                    cm.execute(new ReverseCommand(adjustedTheme, 0, length, false));

                ChordPart themeChords = notate.getChordProg().extract(i, i + length);
                cm.execute(new RectifyPitchesCommand(adjustedTheme, 0, length, themeChords, false, false));

                solo.setSize(solo.getSize() + length);
                solo.pasteSlots(adjustedTheme, i);
            } else {
                Polylist rhythm = lickgen.generateRhythmFromGrammar(0, themeLength);

                MelodyPart lick = fillMelody(BEAT, rhythm, notate.getChordProg(), 0);

                    //lickgen.fillMelody(minPitch, maxPitch, minInterval, maxInterval, BEAT,
                    //    leapProb, rhythm, chordProg, 0, avoidRepeats);

                Part.PartIterator j = lick.iterator();
                while (j.hasNext())
                {
                  Unit unit = j.next();
                  if( unit != null )
                    {
                    solo.addNote(NoteSymbol.toNote(unit.toLeadsheet()));
                    }
                }
            }
        }
        if (notate.getScore().getLength() - solo.getSize() != 0) {
            Polylist rhythm = lickgen.generateRhythmFromGrammar(0, notate.getScore().getLength() - solo.getSize());

            MelodyPart lick =  fillMelody(BEAT, rhythm, notate.getChordProg(), 0);

                    //lickgen.fillMelody(minPitch, maxPitch, minInterval, maxInterval, BEAT,
                    //leapProb, rhythm, chordProg, 0, avoidRepeats);

             Part.PartIterator j = lick.iterator();
            while (j.hasNext())
                solo.addNote(NoteSymbol.toNote(j.next().toLeadsheet()));
        }
        notate.setCurrentSelectionStart(0);

        // Experimental: Resolve pitches in entire solo: seems to improve things, but
        // may generate some repeated notes.
        cm.execute(new RectifyPitchesCommand(solo, 0, solo.getSize(), notate.getChordProg(), false, false));

        notate.pasteMelody(solo);

        imp.ImproVisor.setPlayEntrySounds(true);
    }

}
