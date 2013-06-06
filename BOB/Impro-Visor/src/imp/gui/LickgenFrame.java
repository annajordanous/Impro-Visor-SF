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
import imp.util.NonExistentParameterException;
import imp.util.ProfileFilter;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import javax.swing.*;
import javax.swing.plaf.metal.MetalButtonUI;
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

/**
 * ArrayList of JTextField arrays, used to display probabilities used in lick generation
 */

private ArrayList<JTextField[]> lickPrefs = new ArrayList<JTextField[]>();

/**
 * this will be set to true during extraction of all measures in a corpus
 */
private boolean allMeasures = false;

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

    initComponents();
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
        ProbFillClearPanel = new javax.swing.JPanel();
        clearProbsButton = new javax.swing.JButton();
        FillProbsButton = new javax.swing.JButton();
        autoFillCheckBox = new javax.swing.JCheckBox();
        pitchProbabilitiesPanel = new javax.swing.JPanel();
        chordProbPanel = new javax.swing.JPanel();
        grammarLearningPanel = new javax.swing.JPanel();
        finalLabel = new javax.swing.JLabel();
        windowParametersPanel = new javax.swing.JPanel();
        windowSizeLabel = new javax.swing.JLabel();
        windowSlideLabel = new javax.swing.JLabel();
        numClusterRepsLabel = new javax.swing.JLabel();
        windowSizeField = new javax.swing.JTextField();
        windowSlideField = new javax.swing.JTextField();
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
        setMinimumSize(new java.awt.Dimension(1000, 700));
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
        generateLickButton.setOpaque(true);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
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
        maxLabel.setMaximumSize(new java.awt.Dimension(200, 15));
        maxLabel.setMinimumSize(new java.awt.Dimension(150, 15));
        maxLabel.setPreferredSize(new java.awt.Dimension(150, 15));
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
        gapField.setText("1.05");
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        lickGenPanel.add(pitchProbabilitiesPanel, gridBagConstraints);

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
        windowSlideField.setText("2");
        windowSlideField.setToolTipText("The number of beats to slide window by");
        windowSlideField.setMaximumSize(null);
        windowSlideField.setMinimumSize(new java.awt.Dimension(60, 30));
        windowSlideField.setPreferredSize(new java.awt.Dimension(60, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 0.5;
        windowParametersPanel.add(windowSlideField, gridBagConstraints);

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
        useMarkovCheckbox.setText("Use Markov (ordered connection of phrases) Chain length:");
        useMarkovCheckbox.setToolTipText("Use Markov chains when adding productions to Grammar");
        useMarkovCheckbox.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        windowParametersPanel.add(useMarkovCheckbox, gridBagConstraints);

        MarkovLengthField.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        MarkovLengthField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        MarkovLengthField.setText("3");
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
        loadBaseGrammarBtn.setText("<html><b>Step 1</b>: Load the grammar on which you wish to build, such as Bare.grammar.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;If you do nothing, Impro-Visor will build on whatever grammar is current.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;This step also clears any accumulated productions from prior use of the learning tool.</html>  ");
        loadBaseGrammarBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loadBaseGrammarBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        loadBaseGrammarBtn.setMaximumSize(new java.awt.Dimension(9999, 9999));
        loadBaseGrammarBtn.setMinimumSize(new java.awt.Dimension(105, 60));
        loadBaseGrammarBtn.setOpaque(true);
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
        saveGrammarAsButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        saveGrammarAsButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        saveGrammarAsButton.setLabel("<html><b>Step 2</b>: <b>IMPORTANT</b>: This step will use <b>Save as ...</b> in the Grammar menu to save your new grammar under a new name, <br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; in case you want to return to the old grammar.\n<br>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; It will also ask you to save your leadsheet if you need it, as the leadsheet window will be used as a workspace.</html>  ");
        saveGrammarAsButton.setMaximumSize(new java.awt.Dimension(9999, 9999));
        saveGrammarAsButton.setOpaque(true);
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
        openCorpusBtn.setText("<html><b>Step 4</b>: Select a corpus of solos from which to learn. Each solo is a leadsheet file.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <b>Note: Selecting any leadsheet file in a folder is equivalent to selecting the entire folder. </b><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The leadsheet you selected will be left in the window at the end.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<b>The process is over when the last chorus of that leadsheet appears</b>.</html>");
        openCorpusBtn.setActionCommand("<html><b>Step 5</b>: Next select a corpus of solos from which to learn. Each solo is a leadsheet file.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Selecting any file any a folder is equivalent to selecting the entire folder.  <br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The leadsheet you selected will be left in the window at the end. The process is over when the last chorus appears.</html>");
        openCorpusBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openCorpusBtn.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        openCorpusBtn.setMaximumSize(new java.awt.Dimension(9999, 9999));
        openCorpusBtn.setMinimumSize(new java.awt.Dimension(240, 75));
        openCorpusBtn.setOpaque(true);
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
        toGrammarBtn.setOpaque(true);
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
        testGeneration.setOpaque(true);
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
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                generatorWindowMenu1MenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
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


    private void generateLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateLickButtonActionPerformed
        notate.generateFromButton();
}//GEN-LAST:event_generateLickButtonActionPerformed


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

private void writeProduction(String production, int measureWindow, int location,
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

            out.write("(rule (Seg" + measureWindow + ") " + production + " ) "
                + melodyToWrite + " " + chords + "\n");
          }
        out.close();
      }
    catch( IOException e )
      {
        System.out.println("IO EXCEPTION!");
      }
  }

//add the production to the grammar file
private void addProduction(String production, int measureWindow, double prob)
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


    if( !part.getPrevNote(selStart + 1).equals(null) )
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

    private void playLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playLickButtonActionPerformed
    playSelection();
        
    }//GEN-LAST:event_playLickButtonActionPerformed

 private void playSelection()
    {
    notate.getCurrentStave().playSelection(false, notate.getLoopCount(), PlayScoreCommand.USEDRUMS, "LickGenFrame");
    }
    private void stopLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopLickButtonActionPerformed

        stopPlaying();
    }//GEN-LAST:event_stopLickButtonActionPerformed

public void stopPlaying()
  {
  notate.stopPlaying();
  }
    private void saveLickButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveLickButtonActionPerformed
        notate.setLickTitle("<Generated Lick>");

        notate.openSaveLickFrame();
}//GEN-LAST:event_saveLickButtonActionPerformed

    private void maxPitchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxPitchFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_maxPitchFieldActionPerformed

    private void maxPitchFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxPitchFieldGetsFocus
        
    }//GEN-LAST:event_maxPitchFieldGetsFocus

    private void maxPitchFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxPitchFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_maxPitchFieldFocusLost

    private void maxPitchFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxPitchFieldenterLickKeyPressed
        
    }//GEN-LAST:event_maxPitchFieldenterLickKeyPressed

    private void minPitchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minPitchFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_minPitchFieldActionPerformed

    private void minPitchFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minPitchFieldGetsFocus
        
    }//GEN-LAST:event_minPitchFieldGetsFocus

    private void minPitchFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minPitchFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_minPitchFieldFocusLost

    private void minPitchFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minPitchFieldenterLickKeyPressed
        
    }//GEN-LAST:event_minPitchFieldenterLickKeyPressed

    private void minIntervalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minIntervalFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_minIntervalFieldActionPerformed

    private void minIntervalFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minIntervalFieldGetsFocus
        
    }//GEN-LAST:event_minIntervalFieldGetsFocus

    private void minIntervalFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minIntervalFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_minIntervalFieldFocusLost

    private void minIntervalFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minIntervalFieldenterLickKeyPressed
        
    }//GEN-LAST:event_minIntervalFieldenterLickKeyPressed

    private void maxIntervalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxIntervalFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_maxIntervalFieldActionPerformed

    private void maxIntervalFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxIntervalFieldGetsFocus
        
    }//GEN-LAST:event_maxIntervalFieldGetsFocus

    private void maxIntervalFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxIntervalFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_maxIntervalFieldFocusLost

    private void maxIntervalFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxIntervalFieldenterLickKeyPressed
        
    }//GEN-LAST:event_maxIntervalFieldenterLickKeyPressed

    private void minDurationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minDurationFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_minDurationFieldActionPerformed

    private void minDurationFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minDurationFieldGetsFocus
        
    }//GEN-LAST:event_minDurationFieldGetsFocus

    private void minDurationFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_minDurationFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_minDurationFieldFocusLost

    private void minDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_minDurationFieldenterLickKeyPressed
        
    }//GEN-LAST:event_minDurationFieldenterLickKeyPressed

    private void maxDurationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxDurationFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_maxDurationFieldActionPerformed

    private void maxDurationFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxDurationFieldGetsFocus
        
    }//GEN-LAST:event_maxDurationFieldGetsFocus

    private void maxDurationFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxDurationFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_maxDurationFieldFocusLost

    private void maxDurationFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_maxDurationFieldenterLickKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_maxDurationFieldenterLickKeyPressed

    private void totalBeatsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_totalBeatsFieldActionPerformed

        verifyTriageFields();

        notate.setCurrentSelectionEnd(
            notate.getCurrentSelectionStart() + totalSlots - 1);

        redrawTriage();

        pack();
    }//GEN-LAST:event_totalBeatsFieldActionPerformed

    private void totalBeatsFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalBeatsFieldGetsFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldGetsFocus

    private void totalBeatsFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_totalBeatsFieldFocusLost

        verifyTriageFields();

        notate.setCurrentSelectionEnd(
            notate.getCurrentSelectionStart() + totalSlots - 1);

        redrawTriage();

        pack();
    }//GEN-LAST:event_totalBeatsFieldFocusLost

    private void totalBeatsFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_totalBeatsFieldenterLickKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_totalBeatsFieldenterLickKeyPressed

    private void restProbFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restProbFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_restProbFieldActionPerformed

    private void restProbFieldGetsFocus(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_restProbFieldGetsFocus
        // TODO add your handling code here:
    }//GEN-LAST:event_restProbFieldGetsFocus

    private void restProbFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_restProbFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_restProbFieldFocusLost

    private void restProbFieldenterLickKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_restProbFieldenterLickKeyPressed
    }//GEN-LAST:event_restProbFieldenterLickKeyPressed

    private void leapProbFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_leapProbFieldActionPerformed

        verifyTriageFields();
    }//GEN-LAST:event_leapProbFieldActionPerformed

    private void leapProbFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_leapProbFieldFocusLost

        verifyTriageFields();
    }//GEN-LAST:event_leapProbFieldFocusLost

    private void avoidRepeatsCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_avoidRepeatsCheckboxActionPerformed
        avoidRepeats = avoidRepeatsCheckbox.isSelected();
}//GEN-LAST:event_avoidRepeatsCheckboxActionPerformed

    private void recurrentCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_recurrentCheckboxActionPerformed
        notate.setRecurrent(recurrentCheckbox.isSelected());
}//GEN-LAST:event_recurrentCheckboxActionPerformed

    private void gapFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gapFieldActionPerformed
    notate.setGenerationGap(Notate.doubleFromTextField(gapField, 0, 9.99, 1));
}//GEN-LAST:event_gapFieldActionPerformed

    private void regenerateHeadDataBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_regenerateHeadDataBtnActionPerformed
        notate.writeHeadData();
}//GEN-LAST:event_regenerateHeadDataBtnActionPerformed


/**
 *Make sure the user has entered acceptable values for each of the other fields
 * in the triage frame.
 */

public void verifyTriageFields()
  {
    notate.toCritic();

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

private void verifyAndFill()
  {
  verifyTriageFields();

  if( autoFill )
    {
    FillProbsButtonActionPerformed(null);
    }
  }

    private void chordToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordToneWeightFieldActionPerformed
        verifyAndFill();
        }//GEN-LAST:event_chordToneWeightFieldActionPerformed

        private void chordToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chordToneWeightFieldFocusLost
            // TODO add your handling code here:
        }//GEN-LAST:event_chordToneWeightFieldFocusLost

        private void colorToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_colorToneWeightFieldActionPerformed
        verifyAndFill();
            }//GEN-LAST:event_colorToneWeightFieldActionPerformed

            private void colorToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_colorToneWeightFieldFocusLost
         verifyAndFill();
                }//GEN-LAST:event_colorToneWeightFieldFocusLost

                private void scaleToneWeightFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleToneWeightFieldActionPerformed
        verifyAndFill();
                    }//GEN-LAST:event_scaleToneWeightFieldActionPerformed

                    private void scaleToneWeightFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_scaleToneWeightFieldFocusLost
        verifyAndFill();
                        }//GEN-LAST:event_scaleToneWeightFieldFocusLost

                        private void chordToneDecayFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordToneDecayFieldActionPerformed
        verifyAndFill();
                        }//GEN-LAST:event_chordToneDecayFieldActionPerformed

                        private void chordToneDecayFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_chordToneDecayFieldFocusLost
        verifyAndFill();
                        }//GEN-LAST:event_chordToneDecayFieldFocusLost

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

                        private void rootComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rootComboBoxActionPerformed

                            lickgen.setPreferredScale(
                                (String) rootComboBox.getSelectedItem(),
                                                      (String) scaleComboBox.getSelectedItem());

                            redrawTriage();
                        }//GEN-LAST:event_rootComboBoxActionPerformed

private void triageAndGenerate(int number)
  {
  triageLick(saveLickTF.getText(), number);
  generateLickButtonActionPerformed(null);
}
                        private void grade1BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade1BtnActionPerformed
                            triageAndGenerate(1);
}//GEN-LAST:event_grade1BtnActionPerformed

                        private void grade2BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade2BtnActionPerformed
                            triageAndGenerate(2);
                        }//GEN-LAST:event_grade2BtnActionPerformed

                        private void grade3BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade3BtnActionPerformed
                            triageAndGenerate(3);
                        }//GEN-LAST:event_grade3BtnActionPerformed

                        private void grade4BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade4BtnActionPerformed
                            triageAndGenerate(3);
}//GEN-LAST:event_grade4BtnActionPerformed

                        private void grade5BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade5BtnActionPerformed
                            triageAndGenerate(5);
}//GEN-LAST:event_grade5BtnActionPerformed

                        private void grade6BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade6BtnActionPerformed
                            triageAndGenerate(6);
}//GEN-LAST:event_grade6BtnActionPerformed

                        private void grade7BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade7BtnActionPerformed
                            triageAndGenerate(7);
}//GEN-LAST:event_grade7BtnActionPerformed

                        private void grade8BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade8BtnActionPerformed
                            triageAndGenerate(8);
}//GEN-LAST:event_grade8BtnActionPerformed

                        private void grade9BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade9BtnActionPerformed
                            triageAndGenerate(9);
}//GEN-LAST:event_grade9BtnActionPerformed

                        private void grade10BtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_grade10BtnActionPerformed
                            triageAndGenerate(10);
}//GEN-LAST:event_grade10BtnActionPerformed

                        private void clearProbsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearProbsButtonActionPerformed

                            for( int i = 0; i < lickPrefs.size(); ++i )
                              {
                                for( int j = 0; j < 12; ++j )
                                  {
                                    lickPrefs.get(i)[j].setText("0");
                                  }
                              }
                        }//GEN-LAST:event_clearProbsButtonActionPerformed

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

                        private void autoFillCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoFillCheckBoxActionPerformed
                            autoFill = autoFillCheckBox.isSelected();

                            if( autoFill )
                              {
                                redrawTriage();
                              }
}//GEN-LAST:event_autoFillCheckBoxActionPerformed

                        private void useMarkovCheckboxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_useMarkovCheckboxActionPerformed
}//GEN-LAST:event_useMarkovCheckboxActionPerformed

                        private void MarkovLengthFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MarkovLengthFieldActionPerformed
                            // TODO add your handling code here:
}//GEN-LAST:event_MarkovLengthFieldActionPerformed

                        private void loadBaseGrammarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadBaseGrammarBtnActionPerformed
                            notate.openGrammar();
                            notate.clearAccumulatedProductions();
}//GEN-LAST:event_loadBaseGrammarBtnActionPerformed

                        private void saveGrammarAsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGrammarAsButtonActionPerformed
                            notate.saveGrammarAs();
}//GEN-LAST:event_saveGrammarAsButtonActionPerformed

                        private void openCorpusBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openCorpusBtnActionPerformed
                            notate.openCorpus();
                            toFront();
}//GEN-LAST:event_openCorpusBtnActionPerformed

                        private void toGrammarBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toGrammarBtnActionPerformed
                            notate.toGrammar();
}//GEN-LAST:event_toGrammarBtnActionPerformed

                        private void testGenerationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_testGenerationActionPerformed
                            notate.generateFromButton();
}//GEN-LAST:event_testGenerationActionPerformed

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

                        private void generateThemeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateThemeButtonActionPerformed
                            generateTheme();
}//GEN-LAST:event_generateThemeButtonActionPerformed

                        private void genSoloThemeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_genSoloThemeBtnActionPerformed
                            MelodyPart theme = generateTheme();
                            generateSolo(theme, cm);
                            playSelection();
}//GEN-LAST:event_genSoloThemeBtnActionPerformed

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

                        private void stopSoloPlayBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopSoloPlayBtnActionPerformed
                            stopPlaying();
                        }//GEN-LAST:event_stopSoloPlayBtnActionPerformed

                        private void playSoloBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playSoloBtnActionPerformed
                            playSelection();
                        }//GEN-LAST:event_playSoloBtnActionPerformed

                        private void closeWindow(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_closeWindow
                            closeWindow();
                        }//GEN-LAST:event_closeWindow

    private void rectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_rectifyCheckBoxActionPerformed
      {//GEN-HEADEREND:event_rectifyCheckBoxActionPerformed
        rectify = rectifyCheckBox.isSelected();
      }//GEN-LAST:event_rectifyCheckBoxActionPerformed

private void useSoloistCheckBoxActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_useSoloistCheckBoxActionPerformed
  {//GEN-HEADEREND:event_useSoloistCheckBoxActionPerformed
    // TODO add your handling code here:
  }//GEN-LAST:event_useSoloistCheckBoxActionPerformed

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
    private javax.swing.JCheckBox autoFillCheckBox;
    private javax.swing.JCheckBox avoidRepeatsCheckbox;
    private javax.swing.JMenuItem cascadeMI2;
    private javax.swing.JPanel chordProbPanel;
    private javax.swing.JTextField chordToneDecayField;
    private javax.swing.JLabel chordToneDecayRateLabel;
    private javax.swing.JLabel chordToneProbLabel;
    private javax.swing.JTextField chordToneWeightField;
    private javax.swing.JButton clearProbsButton;
    private javax.swing.JMenuItem closeWindowMI2;
    private javax.swing.JLabel colorToneProbLabel;
    private javax.swing.JTextField colorToneWeightField;
    private javax.swing.JLabel disclaimer;
    private javax.swing.JLabel durationLabel;
    private javax.swing.JMenuItem editGrammarMI1;
    private javax.swing.JButton fillMelodyButton;
    private javax.swing.JLabel finalLabel;
    private javax.swing.JTextField gapField;
    private javax.swing.JButton genRhythmButton;
    private javax.swing.JButton genSoloThemeBtn;
    private javax.swing.JButton generateLickButton;
    private javax.swing.JButton generateSoloButton;
    private javax.swing.JButton generateThemeButton;
    private javax.swing.JLabel generationGapLabel;
    private javax.swing.JMenuBar generatorMenuBar1;
    private javax.swing.JTabbedPane generatorPane;
    private javax.swing.JMenu generatorWindowMenu1;
    private javax.swing.JButton getAbstractMelodyButton;
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
    private javax.swing.JPanel grammarLearningPanel;
    private javax.swing.JMenu grammarMenu1;
    private javax.swing.JLabel intervalLabel;
    private javax.swing.JTextField invertProbabilityField;
    private javax.swing.JTextField leapProbField;
    private javax.swing.JLabel leapProbLabel;
    private javax.swing.JLabel learningStep0Label;
    private javax.swing.JPanel lickGenPanel;
    private javax.swing.JPanel lickGenerationButtonsPanel;
    private javax.swing.JPanel lickGradeButtonsPanel;
    private javax.swing.JLabel lickSavedLabel;
    private javax.swing.JPanel lickgenParametersPanel;
    private javax.swing.JButton loadBaseGrammarBtn;
    private javax.swing.JTextField maxDurationField;
    private javax.swing.JTextField maxIntervalField;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JTextField maxPitchField;
    private javax.swing.JTextField minDurationField;
    private javax.swing.JTextField minIntervalField;
    private javax.swing.JLabel minLabel;
    private javax.swing.JTextField minPitchField;
    private javax.swing.JTextField numClusterRepsField;
    private javax.swing.JLabel numClusterRepsLabel;
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
    private javax.swing.JMenuItem reloadGrammarMI1;
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
    private javax.swing.JPanel soloGenPanel;
    private javax.swing.JButton stopLickButton;
    private javax.swing.JButton stopSoloPlayBtn;
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
    private javax.swing.JLabel transposeProbLabel;
    private javax.swing.JTextField transposeProbabilityField;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JCheckBoxMenuItem useGrammarMI1;
    private javax.swing.JCheckBox useHeadCheckBox;
    private javax.swing.JCheckBox useMarkovCheckbox;
    private javax.swing.JCheckBox useSoloistCheckBox;
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