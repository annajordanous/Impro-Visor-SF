/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110z-1301  USA
 */

package imp.roadmap;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.awt.event.*;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import imp.brickdictionary.*;
import imp.cykparser.*;
import imp.data.*;
import imp.gui.Notate;
import imp.gui.PrintUtilitiesRoadMap;
import imp.gui.WindowMenuItem;
import imp.gui.WindowRegistry;
import imp.util.ErrorLog;
import imp.util.MidiPlayListener;
import java.util.Arrays;

import polya.Tokenizer;


/** The main roadmap window. This class deals with user interaction as well as
 * interaction with other parts of improvisor.
 * @author August Toman-Yih
 */

public class RoadMapFrame extends javax.swing.JFrame implements MidiPlayListener {
    
    /** Communication with leadsheet and score is done through Notate frame. */
    private Notate notate = null;
    
    /** auxNotate is a separate notate window for converting the roadmap
     * to a leadsheet. */
    private Notate auxNotate = null;;
    
    /** Buffer for the preview panel  */
    private Image bufferPreviewPanel;
    /** Width of the preview buffer */
    private final int previewBufferWidth  = 1024;
    /** Height of the preview buffer */
    private final int previewBufferHeight = 200;

    /** Buffer for the roadmap panel */
    private Image bufferRoadMap;  
    /** Width of the roadmap buffer */
    private final int roadMapBufferWidth  = 1920;
    /** Height of the roadmap buffer */
    private final int roadMapBufferHeight = 1920;
    
    /** Panel for previewing bricks from the library */
    private PreviewPanel previewPanel;
    /** Panel where the roadmap is drawn */
    private RoadMapPanel roadMapPanel;
    /** Library of available bricks */
    private BrickLibrary brickLibrary;
    /** Parser for chord analysis */
    private CYKParser cykParser = new CYKParser();
    /** When bricks are dragged, they are removed from the roadmap and store here */
    private ArrayList<GraphicBrick> draggedBricks = new ArrayList();
    /** Stores copied bricks */
    private ArrayList<Block> clipboard = new ArrayList();
    /** Choices in the duration combobox */
    private Object[] durationChoices = {8,7,6,5,4,3,2,1};
    /** Combo box model for choosing styles */
    private Notate.StyleComboBoxModel styleComboBoxModel = new Notate.StyleComboBoxModel();
    /** Default width of the roadmap frame */
    private int RMframeWidth = 1250;
    /** Playback status */
    private MidiPlayListener.Status isPlaying = MidiPlayListener.Status.STOPPED;
    /** This timer provides updates to the roadmap panel during playback */
    private javax.swing.Timer playTimer;
    /** Tree used to store the brick library */
    private DefaultTreeModel libraryTreeModel;
    /** Graphical settings are stored here */
    private RoadMapSettings settings = new RoadMapSettings();
    /** Actions that can be undone */
    private LinkedList<RoadMapSnapShot> roadMapHistory = new LinkedList();
    /** Actions that can be redone */
    private LinkedList<RoadMapSnapShot> roadMapFuture = new LinkedList();
    /** Prefix on the frame title */
    private static String roadMapTitlePrefix = "RoadMap: ";
    /** Suffix for constrained feature width */
    private static String featureWidthSuffix = "(Constrained)";
    /** Title of this piece */
    public String roadMapTitle = "Untitled";
    /** Style of this piece */
    public Style style = Advisor.getStyle("swing");
    /** Tempo of this piece */
    public int tempo = 120;
    /** Time signature of this piece */
    public int[] metre = {4,4};
    
    
    private RoadMapFrame() {} // Not for you.
    
    /** Creates new form RoadMapFrame */
    public RoadMapFrame(Notate notate) {
        
        this.notate = notate;
        
        previewPanel = new PreviewPanel(this);
        roadMapPanel = new RoadMapPanel(this);
        brickLibrary = new BrickLibrary();
        
        try {
            brickLibrary.processDictionary();
        } catch (IOException e) {
            ErrorLog.log(ErrorLog.FATAL, "Error opening brick dictionary");
            System.exit(-1);
        }
         
        cykParser.createRules(brickLibrary);
         
        initLibraryTree();
                
        initComponents();
        
        initBuffer();
        
        initTimer();
        
        deactivateButtons();
        
        setRoadMapTitle(notate.getTitle());
        
        roadMapScrollPane.getVerticalScrollBar().setUnitIncrement(20);
        
        brickLibraryFrame.setSize(brickLibraryFrame.getPreferredSize());
    
        WindowRegistry.registerWindow(this);
        
        //settings.generateColors(.3f);
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

        addBrickDialog = new javax.swing.JDialog();
        dialogNameLabel = new javax.swing.JLabel();
        dialogKeyLabel = new javax.swing.JLabel();
        dialogNameField = new javax.swing.JTextField();
        dialogAcceptButton = new javax.swing.JButton();
        dialogModeComboBox = new javax.swing.JComboBox();
        dialogKeyComboBox = new javax.swing.JComboBox();
        dialogTypeComboBox = new javax.swing.JComboBox(brickLibrary.getTypes());
        dialogTypeLabel = new javax.swing.JLabel();
        dialogVariantLabel = new javax.swing.JLabel();
        dialogVariantField = new javax.swing.JTextField();
        chordChangeDialog = new javax.swing.JDialog();
        chordDialogNameField = new javax.swing.JTextField();
        chordDialogAcceptButton = new javax.swing.JButton();
        chordDialogDurationComboBox = new javax.swing.JComboBox(durationChoices);
        preferencesDialog = new javax.swing.JDialog();
        jPanel2 = new javax.swing.JPanel();
        prefDialogAcceptButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        prefDialogStyleComboBox = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        prefDialogMeterLabel = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        prefDialogTitleField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        prefDialogTempoField = new imp.roadmap.IntegerField();
        prefDialogMetreTopField = new imp.roadmap.IntegerField();
        prefDialogMetreBottomField = new imp.roadmap.IntegerField();
        prefDialogKeyColorCheckBox = new javax.swing.JCheckBox();
        brickLibraryFrame = new javax.swing.JFrame();
        keyComboBox = new javax.swing.JComboBox();
        libraryScrollPane = new javax.swing.JScrollPane();
        libraryTree = new javax.swing.JTree();
        deleteButton = new javax.swing.JButton();
        durationComboBox = new javax.swing.JComboBox(durationChoices);
        toolBar = new javax.swing.JToolBar();
        fileStepBackBtn = new javax.swing.JButton();
        fileStepForwardBtn = new javax.swing.JButton();
        scaleLabel = new javax.swing.JLabel();
        scaleComboBox = new javax.swing.JComboBox();
        newBrickButton = new javax.swing.JButton();
        breakButton = new javax.swing.JButton();
        selectAllBricksButton = new javax.swing.JButton();
        flattenButton = new javax.swing.JButton();
        analyzeButton = new javax.swing.JButton();
        loopToggleButton = new javax.swing.JToggleButton();
        playButton = new javax.swing.JButton();
        stopButton = new javax.swing.JButton();
        featureWidthSlider = new javax.swing.JSlider();
        roadMapTextEntry = new javax.swing.JTextField();
        roadMapScrollPane = new javax.swing.JScrollPane(roadMapPanel);
        previewScrollPane = new javax.swing.JScrollPane(previewPanel);
        roadmapMenuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        openLeadsheetMI = new javax.swing.JMenuItem();
        preferencesMenuItem = new javax.swing.JMenuItem();
        printRoadMapMI = new javax.swing.JMenuItem();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        selectAllMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        undoMenuItem = new javax.swing.JMenuItem();
        redoMenuItem = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        deleteMenuItem = new javax.swing.JMenuItem();
        flattenMenuItem = new javax.swing.JMenuItem();
        breakMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        transposeMenu = new javax.swing.JMenu();
        transposeDownMenuItem = new javax.swing.JMenuItem();
        transposeUpMenuItem = new javax.swing.JMenuItem();
        sectionMenu = new javax.swing.JMenu();
        toggleSectionMenuItem = new javax.swing.JMenuItem();
        togglePhraseMenuItem = new javax.swing.JMenuItem();
        leadsheetMenu = new javax.swing.JMenu();
        appendToLeadsheetMI = new javax.swing.JMenuItem();
        appendToNewLeadsheetMI = new javax.swing.JMenuItem();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        brickLibraryMenuItem = new javax.swing.JCheckBoxMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();

        addBrickDialog.setTitle("Add New Brick"); // NOI18N
        addBrickDialog.setMinimumSize(new java.awt.Dimension(250, 180));
        addBrickDialog.setName("addBrickDialog"); // NOI18N
        addBrickDialog.setResizable(false);
        addBrickDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        dialogNameLabel.setText("Name:"); // NOI18N
        dialogNameLabel.setName("dialogNameLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        addBrickDialog.getContentPane().add(dialogNameLabel, gridBagConstraints);

        dialogKeyLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        dialogKeyLabel.setText("Key:"); // NOI18N
        dialogKeyLabel.setName("dialogKeyLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        addBrickDialog.getContentPane().add(dialogKeyLabel, gridBagConstraints);

        dialogNameField.setText("New Brick"); // NOI18N
        dialogNameField.setName("dialogNameField"); // NOI18N
        dialogNameField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dialogNameFieldKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        addBrickDialog.getContentPane().add(dialogNameField, gridBagConstraints);

        dialogAcceptButton.setText("Accept"); // NOI18N
        dialogAcceptButton.setName("dialogAcceptButton"); // NOI18N
        dialogAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dialogAccepted(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 3, 0);
        addBrickDialog.getContentPane().add(dialogAcceptButton, gridBagConstraints);

        dialogModeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Major", "Minor", "Dominant" }));
        dialogModeComboBox.setName("dialogModeComboBox"); // NOI18N
        dialogModeComboBox.setPreferredSize(new java.awt.Dimension(120, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        addBrickDialog.getContentPane().add(dialogModeComboBox, gridBagConstraints);

        dialogKeyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C", "B", "Bb", "A", "Ab", "G", "Gb", "F", "E", "Eb", "D", "Db" }));
        dialogKeyComboBox.setName("dialogKeyComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        addBrickDialog.getContentPane().add(dialogKeyComboBox, gridBagConstraints);

        dialogTypeComboBox.setName("dialogTypeComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        addBrickDialog.getContentPane().add(dialogTypeComboBox, gridBagConstraints);

        dialogTypeLabel.setText("Type:"); // NOI18N
        dialogTypeLabel.setName("dialogTypeLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        addBrickDialog.getContentPane().add(dialogTypeLabel, gridBagConstraints);

        dialogVariantLabel.setText("Variant:"); // NOI18N
        dialogVariantLabel.setEnabled(false);
        dialogVariantLabel.setName("dialogVariantLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        addBrickDialog.getContentPane().add(dialogVariantLabel, gridBagConstraints);

        dialogVariantField.setEditable(false);
        dialogVariantField.setToolTipText("Enter a qualifying name of your brick (optional for uniquely-defined bricks)"); // NOI18N
        dialogVariantField.setName("dialogVariantField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 3);
        addBrickDialog.getContentPane().add(dialogVariantField, gridBagConstraints);

        chordChangeDialog.setTitle("Settings"); // NOI18N
        chordChangeDialog.setModal(true);
        chordChangeDialog.setName("chordChangeDialog"); // NOI18N
        chordChangeDialog.setResizable(false);
        chordChangeDialog.setSize(new java.awt.Dimension(100, 100));
        chordChangeDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

        chordDialogNameField.setText("CM7"); // NOI18N
        chordDialogNameField.setMinimumSize(new java.awt.Dimension(14, 20));
        chordDialogNameField.setName("chordDialogNameField"); // NOI18N
        chordDialogNameField.setPreferredSize(new java.awt.Dimension(42, 20));
        chordDialogNameField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordDialogNameFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        chordChangeDialog.getContentPane().add(chordDialogNameField, gridBagConstraints);

        chordDialogAcceptButton.setText("Accept"); // NOI18N
        chordDialogAcceptButton.setName("chordDialogAcceptButton"); // NOI18N
        chordDialogAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordDialogAcceptButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        chordChangeDialog.getContentPane().add(chordDialogAcceptButton, gridBagConstraints);

        chordDialogDurationComboBox.setName("chordDialogDurationComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        chordChangeDialog.getContentPane().add(chordDialogDurationComboBox, gridBagConstraints);

        preferencesDialog.setTitle("Roadmap Info"); // NOI18N
        preferencesDialog.setMinimumSize(new java.awt.Dimension(300, 200));
        preferencesDialog.setName("preferencesDialog"); // NOI18N

        jPanel2.setName("jPanel2"); // NOI18N

        prefDialogAcceptButton.setText("Accept Changes"); // NOI18N
        prefDialogAcceptButton.setName("prefDialogAcceptButton"); // NOI18N
        prefDialogAcceptButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                prefDialogAcceptButtonActionPerformed(evt);
            }
        });
        jPanel2.add(prefDialogAcceptButton);

        preferencesDialog.getContentPane().add(jPanel2, java.awt.BorderLayout.PAGE_END);

        jPanel1.setName("jPanel1"); // NOI18N
        jPanel1.setLayout(new java.awt.GridBagLayout());

        prefDialogStyleComboBox.setModel(styleComboBoxModel);
        prefDialogStyleComboBox.setName("prefDialogStyleComboBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(prefDialogStyleComboBox, gridBagConstraints);

        jLabel5.setText("Tempo:"); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel5, gridBagConstraints);

        prefDialogMeterLabel.setText("Metre:"); // NOI18N
        prefDialogMeterLabel.setName("prefDialogMeterLabel"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 20, 0, 0);
        jPanel1.add(prefDialogMeterLabel, gridBagConstraints);

        jLabel6.setText("/"); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 2;
        jPanel1.add(jLabel6, gridBagConstraints);

        prefDialogTitleField.setText("Untitled"); // NOI18N
        prefDialogTitleField.setName("prefDialogTitleField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 8;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(prefDialogTitleField, gridBagConstraints);

        jLabel7.setText("Title:"); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel1.add(jLabel7, gridBagConstraints);

        jLabel8.setText("Style:"); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        jPanel1.add(jLabel8, gridBagConstraints);

        prefDialogTempoField.setText("120"); // NOI18N
        prefDialogTempoField.setMinimumSize(new java.awt.Dimension(38, 28));
        prefDialogTempoField.setName("prefDialogTempoField"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        jPanel1.add(prefDialogTempoField, gridBagConstraints);

        prefDialogMetreTopField.setText("4"); // NOI18N
        prefDialogMetreTopField.setMinimumSize(new java.awt.Dimension(23, 28));
        prefDialogMetreTopField.setName("prefDialogMetreTopField"); // NOI18N
        prefDialogMetreTopField.setPreferredSize(new java.awt.Dimension(23, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 2;
        jPanel1.add(prefDialogMetreTopField, gridBagConstraints);

        prefDialogMetreBottomField.setText("4"); // NOI18N
        prefDialogMetreBottomField.setMinimumSize(new java.awt.Dimension(23, 28));
        prefDialogMetreBottomField.setName("prefDialogMetreBottomField"); // NOI18N
        prefDialogMetreBottomField.setPreferredSize(new java.awt.Dimension(23, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 2;
        jPanel1.add(prefDialogMetreBottomField, gridBagConstraints);

        prefDialogKeyColorCheckBox.setSelected(true);
        prefDialogKeyColorCheckBox.setText("Key Coloration"); // NOI18N
        prefDialogKeyColorCheckBox.setName("prefDialogKeyColorCheckBox"); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 8;
        gridBagConstraints.gridy = 2;
        jPanel1.add(prefDialogKeyColorCheckBox, gridBagConstraints);

        preferencesDialog.getContentPane().add(jPanel1, java.awt.BorderLayout.CENTER);

        brickLibraryFrame.setTitle("Brick Library"); // NOI18N
        brickLibraryFrame.setMinimumSize(new java.awt.Dimension(200, 200));
        brickLibraryFrame.setName("brickLibraryFrame"); // NOI18N
        brickLibraryFrame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                brickLibraryFrameWindowClosing(evt);
            }
        });
        brickLibraryFrame.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentShown(java.awt.event.ComponentEvent evt) {
                brickLibraryFrameComponentShown(evt);
            }
            public void componentHidden(java.awt.event.ComponentEvent evt) {
                brickLibraryFrameComponentHidden(evt);
            }
        });
        brickLibraryFrame.getContentPane().setLayout(new java.awt.GridBagLayout());

        keyComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "C", "B", "Bb", "A", "Ab", "G", "Gb", "F", "E", "Eb", "D", "Db" }));
        keyComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder("Key/Root"));
        keyComboBox.setMinimumSize(new java.awt.Dimension(52, 54));
        keyComboBox.setName("keyComboBox"); // NOI18N
        keyComboBox.setPreferredSize(new java.awt.Dimension(52, 54));
        keyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                keyComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.02;
        brickLibraryFrame.getContentPane().add(keyComboBox, gridBagConstraints);

        libraryScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        libraryScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        libraryScrollPane.setName("libraryScrollPane"); // NOI18N

        libraryTree.setModel(libraryTreeModel);
        libraryTree.setToolTipText("Dictionary of bricks that may be viewed and inserted"); // NOI18N
        libraryTree.setMaximumSize(new java.awt.Dimension(400, 3000));
        libraryTree.setMinimumSize(new java.awt.Dimension(400, 400));
        libraryTree.setName("Bricks"); // NOI18N
        libraryTree.setPreferredSize(new java.awt.Dimension(300, 300));
        libraryTree.setRootVisible(false);
        libraryTree.setShowsRootHandles(true);
        libraryTree.setSize(new java.awt.Dimension(400, 2000));
        libraryTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        libraryTree.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                libraryTreeMouseClicked(evt);
            }
        });
        libraryTree.addTreeExpansionListener(new javax.swing.event.TreeExpansionListener() {
            public void treeExpanded(javax.swing.event.TreeExpansionEvent evt) {
                libraryTreeTreeExpanded(evt);
            }
            public void treeCollapsed(javax.swing.event.TreeExpansionEvent evt) {
                libraryTreeTreeCollapsed(evt);
            }
        });
        libraryTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
            public void valueChanged(javax.swing.event.TreeSelectionEvent evt) {
                libraryTreeSelected(evt);
            }
        });
        libraryScrollPane.setViewportView(libraryTree);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        brickLibraryFrame.getContentPane().add(libraryScrollPane, gridBagConstraints);

        deleteButton.setText("Delete From Library"); // NOI18N
        deleteButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        deleteButton.setMaximumSize(new java.awt.Dimension(182, 30));
        deleteButton.setMinimumSize(new java.awt.Dimension(182, 30));
        deleteButton.setName("deleteButton"); // NOI18N
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 3, 0);
        brickLibraryFrame.getContentPane().add(deleteButton, gridBagConstraints);

        durationComboBox.setSelectedItem(2);
        durationComboBox.setToolTipText("Set the duration of this brick (in slots)."); // NOI18N
        durationComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Duration\n", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande 11", 0, 11))); // NOI18N
        durationComboBox.setMinimumSize(new java.awt.Dimension(52, 54));
        durationComboBox.setName("durationComboBox"); // NOI18N
        durationComboBox.setPreferredSize(new java.awt.Dimension(52, 54));
        durationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                durationComboBoxdurationChosen(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.02;
        brickLibraryFrame.getContentPane().add(durationComboBox, gridBagConstraints);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Road Map\n"); // NOI18N
        setMinimumSize(new java.awt.Dimension(830, 600));
        setName("Form"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                roadMapWindowClosing(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });
        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                formComponentResized(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);
        toolBar.setFocusable(false);
        toolBar.setMaximumSize(new java.awt.Dimension(100, 60));
        toolBar.setMinimumSize(new java.awt.Dimension(500, 50));
        toolBar.setName("toolBar"); // NOI18N
        toolBar.setPreferredSize(new java.awt.Dimension(500, 50));

        fileStepBackBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperBack.png"))); // NOI18N
        fileStepBackBtn.setToolTipText("Browse previous leadsheet file in the current directory.\n"); // NOI18N
        fileStepBackBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepBackBtn.setFocusable(false);
        fileStepBackBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fileStepBackBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepBackBtn.setName("fileStepBackBtn"); // NOI18N
        fileStepBackBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fileStepBackBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepBackBtnActionPerformed(evt);
            }
        });
        toolBar.add(fileStepBackBtn);

        fileStepForwardBtn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/icons/fileStepperFront.png"))); // NOI18N
        fileStepForwardBtn.setToolTipText("Browse next leadsheet file in the current directory.\n"); // NOI18N
        fileStepForwardBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        fileStepForwardBtn.setFocusable(false);
        fileStepForwardBtn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        fileStepForwardBtn.setMaximumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setMinimumSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setName("fileStepForwardBtn"); // NOI18N
        fileStepForwardBtn.setPreferredSize(new java.awt.Dimension(30, 30));
        fileStepForwardBtn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        fileStepForwardBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fileStepForwardBtnActionPerformed(evt);
            }
        });
        toolBar.add(fileStepForwardBtn);

        scaleLabel.setName("scaleLabel"); // NOI18N
        toolBar.add(scaleLabel);

        scaleComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "/5", "/4", "/3", "/2", "x1", "x2", "x3", "x4", "x5" }));
        scaleComboBox.setSelectedIndex(4);
        scaleComboBox.setToolTipText("Scale the length of the brick or chord by a factor."); // NOI18N
        scaleComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Scale Duration", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("Lucida Grande", 0, 9))); // NOI18N
        scaleComboBox.setMaximumSize(new java.awt.Dimension(100, 45));
        scaleComboBox.setMinimumSize(new java.awt.Dimension(100, 30));
        scaleComboBox.setName("scaleComboBox"); // NOI18N
        scaleComboBox.setPreferredSize(new java.awt.Dimension(100, 30));
        scaleComboBox.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                scaleComboBoxscaleComboReleased(evt);
            }
        });
        scaleComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleComboBoxscaleChosen(evt);
            }
        });
        toolBar.add(scaleComboBox);

        newBrickButton.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        newBrickButton.setText("New Brick"); // NOI18N
        newBrickButton.setToolTipText("Define a new brick in the dictionary."); // NOI18N
        newBrickButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        newBrickButton.setFocusable(false);
        newBrickButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        newBrickButton.setMaximumSize(new java.awt.Dimension(70, 30));
        newBrickButton.setMinimumSize(new java.awt.Dimension(70, 30));
        newBrickButton.setName("newBrickButton"); // NOI18N
        newBrickButton.setPreferredSize(new java.awt.Dimension(70, 30));
        newBrickButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        newBrickButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newBrickButtonPressed(evt);
            }
        });
        toolBar.add(newBrickButton);

        breakButton.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        breakButton.setText("Break"); // NOI18N
        breakButton.setToolTipText("Break this brick into constitutent parts."); // NOI18N
        breakButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        breakButton.setFocusable(false);
        breakButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        breakButton.setMaximumSize(new java.awt.Dimension(50, 30));
        breakButton.setMinimumSize(new java.awt.Dimension(50, 30));
        breakButton.setName("breakButton"); // NOI18N
        breakButton.setPreferredSize(new java.awt.Dimension(50, 30));
        breakButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        breakButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakButtonPressed(evt);
            }
        });
        toolBar.add(breakButton);

        selectAllBricksButton.setFont(new java.awt.Font("Lucida Grande 12", 0, 12));
        selectAllBricksButton.setText("Select All"); // NOI18N
        selectAllBricksButton.setToolTipText("Select all bricks.\n"); // NOI18N
        selectAllBricksButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        selectAllBricksButton.setFocusable(false);
        selectAllBricksButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectAllBricksButton.setMaximumSize(new java.awt.Dimension(70, 30));
        selectAllBricksButton.setMinimumSize(new java.awt.Dimension(70, 30));
        selectAllBricksButton.setName("selectAllBricksButton"); // NOI18N
        selectAllBricksButton.setPreferredSize(new java.awt.Dimension(70, 30));
        selectAllBricksButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        selectAllBricksButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllBricksButtonPressed(evt);
            }
        });
        toolBar.add(selectAllBricksButton);

        flattenButton.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        flattenButton.setText("Flatten"); // NOI18N
        flattenButton.setToolTipText("Flatten selected bricks into their constituent chords."); // NOI18N
        flattenButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        flattenButton.setFocusable(false);
        flattenButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        flattenButton.setMaximumSize(new java.awt.Dimension(50, 30));
        flattenButton.setMinimumSize(new java.awt.Dimension(50, 30));
        flattenButton.setName("flattenButton"); // NOI18N
        flattenButton.setPreferredSize(new java.awt.Dimension(50, 30));
        flattenButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        flattenButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenButtonPressed(evt);
            }
        });
        toolBar.add(flattenButton);

        analyzeButton.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        analyzeButton.setToolTipText("Analyze the selection into bricks."); // NOI18N
        analyzeButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        analyzeButton.setFocusable(false);
        analyzeButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        analyzeButton.setLabel("Analyze"); // NOI18N
        analyzeButton.setMaximumSize(new java.awt.Dimension(60, 30));
        analyzeButton.setMinimumSize(new java.awt.Dimension(60, 30));
        analyzeButton.setName("analyzeButton"); // NOI18N
        analyzeButton.setPreferredSize(new java.awt.Dimension(60, 30));
        analyzeButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        analyzeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                analyzeButtonPressed(evt);
            }
        });
        toolBar.add(analyzeButton);

        loopToggleButton.setBackground(new java.awt.Color(0, 255, 0));
        loopToggleButton.setFont(new java.awt.Font("Lucida Grande", 0, 12));
        loopToggleButton.setText("Loop"); // NOI18N
        loopToggleButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loopToggleButton.setFocusable(false);
        loopToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        loopToggleButton.setMaximumSize(new java.awt.Dimension(60, 30));
        loopToggleButton.setMinimumSize(new java.awt.Dimension(60, 30));
        loopToggleButton.setName("loopToggleButton"); // NOI18N
        loopToggleButton.setOpaque(true);
        loopToggleButton.setPreferredSize(new java.awt.Dimension(60, 30));
        loopToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        loopToggleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loopToggleButtonPressed(evt);
            }
        });
        toolBar.add(loopToggleButton);

        playButton.setFont(new java.awt.Font("Lucida Grande 12", 0, 12));
        playButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/play.gif"))); // NOI18N
        playButton.setText("\n"); // NOI18N
        playButton.setToolTipText("Play the selection.\n"); // NOI18N
        playButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        playButton.setFocusable(false);
        playButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        playButton.setMaximumSize(new java.awt.Dimension(40, 30));
        playButton.setMinimumSize(new java.awt.Dimension(30, 30));
        playButton.setName("playButton"); // NOI18N
        playButton.setPreferredSize(new java.awt.Dimension(40, 30));
        playButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playButtonPressed(evt);
            }
        });
        toolBar.add(playButton);

        stopButton.setFont(new java.awt.Font("Lucida Grande 12", 0, 12));
        stopButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/imp/gui/graphics/toolbar/stop.gif"))); // NOI18N
        stopButton.setText("\n"); // NOI18N
        stopButton.setToolTipText("Stop playing the selection.\n"); // NOI18N
        stopButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stopButton.setFocusable(false);
        stopButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        stopButton.setMaximumSize(new java.awt.Dimension(40, 30));
        stopButton.setMinimumSize(new java.awt.Dimension(40, 30));
        stopButton.setName("stopButton"); // NOI18N
        stopButton.setPreferredSize(new java.awt.Dimension(35, 30));
        stopButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopButtonPressed(evt);
            }
        });
        toolBar.add(stopButton);

        featureWidthSlider.setMaximum(200);
        featureWidthSlider.setMinimum(60);
        featureWidthSlider.setToolTipText("Slide to adjust visual width of bricks."); // NOI18N
        featureWidthSlider.setValue(settings.measureLength);
        featureWidthSlider.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Feature Width", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 9))); // NOI18N
        featureWidthSlider.setFocusable(false);
        featureWidthSlider.setMaximumSize(new java.awt.Dimension(300, 40));
        featureWidthSlider.setMinimumSize(new java.awt.Dimension(50, 40));
        featureWidthSlider.setName("featureWidthSlider"); // NOI18N
        featureWidthSlider.setPreferredSize(new java.awt.Dimension(100, 40));
        featureWidthSlider.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                featureWidthSliderMouseClicked(evt);
            }
        });
        featureWidthSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                featureWidthSliderChanged(evt);
            }
        });
        toolBar.add(featureWidthSlider);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.05;
        getContentPane().add(toolBar, gridBagConstraints);

        roadMapTextEntry.setToolTipText("Enter chords using Leadsheet Notation. Separate measures with , or |."); // NOI18N
        roadMapTextEntry.setBorder(javax.swing.BorderFactory.createTitledBorder("Textual chord entry"));
        roadMapTextEntry.setName("roadMapTextEntry"); // NOI18N
        roadMapTextEntry.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textualEntryKeyPressed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(roadMapTextEntry, gridBagConstraints);

        roadMapScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        roadMapScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        roadMapScrollPane.setIgnoreRepaint(true);
        roadMapScrollPane.setMinimumSize(new java.awt.Dimension(800, 400));
        roadMapScrollPane.setName("roadMapScrollPane"); // NOI18N
        roadMapScrollPane.setPreferredSize(new java.awt.Dimension(800, 900));
        roadMapScrollPane.setRequestFocusEnabled(false);
        roadMapScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneroadMapReleased(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneroadMapClicked(evt);
            }
        });
        roadMapScrollPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseMoved(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneMouseMoved(evt);
            }
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                roadMapScrollPaneroadMapDragged(evt);
            }
        });
        roadMapScrollPane.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                roadMapScrollPaneroadMapKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                roadMapScrollPaneroadMapKeyReleased(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.9;
        gridBagConstraints.weighty = 0.95;
        getContentPane().add(roadMapScrollPane, gridBagConstraints);

        previewScrollPane.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Brick Preview\n", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11))); // NOI18N
        previewScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        previewScrollPane.setDoubleBuffered(true);
        previewScrollPane.setMaximumSize(new java.awt.Dimension(32767, 100));
        previewScrollPane.setMinimumSize(new java.awt.Dimension(800, 80));
        previewScrollPane.setName("previewScrollPane"); // NOI18N
        previewScrollPane.setPreferredSize(new java.awt.Dimension(800, 80));
        previewScrollPane.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                previewScrollPanepreviewPaneReleased(evt);
            }
        });
        previewScrollPane.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                previewScrollPanepreviewPaneDragged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 0.1;
        gridBagConstraints.weighty = 0.1;
        getContentPane().add(previewScrollPane, gridBagConstraints);

        roadmapMenuBar.setName("roadmapMenuBar"); // NOI18N

        fileMenu.setText("File"); // NOI18N
        fileMenu.setMaximumSize(new java.awt.Dimension(50, 40));
        fileMenu.setName("fileMenu"); // NOI18N
        fileMenu.setPreferredSize(new java.awt.Dimension(50, 20));

        openLeadsheetMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openLeadsheetMI.setMnemonic('o');
        openLeadsheetMI.setText("Open Leadsheet"); // NOI18N
        openLeadsheetMI.setToolTipText("Open a leadsheet in the current window."); // NOI18N
        openLeadsheetMI.setName("openLeadsheetMI"); // NOI18N
        openLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openLeadsheetMIActionPerformed(evt);
            }
        });
        fileMenu.add(openLeadsheetMI);

        preferencesMenuItem.setText("Roadmap Information"); // NOI18N
        preferencesMenuItem.setName("preferencesMenuItem"); // NOI18N
        preferencesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                preferencesMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(preferencesMenuItem);

        printRoadMapMI.setText("Print RoadMap");
        printRoadMapMI.setName("printRoadMapMI"); // NOI18N
        printRoadMapMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                printRoadMapMIActionPerformed(evt);
            }
        });
        fileMenu.add(printRoadMapMI);

        exitMenuItem.setLabel("Quit"); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMIhandler(evt);
            }
        });
        fileMenu.add(exitMenuItem);

        roadmapMenuBar.add(fileMenu);

        editMenu.setText("Edit"); // NOI18N
        editMenu.setName("editMenu"); // NOI18N

        selectAllMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_A, 0));
        selectAllMenuItem.setText("Select All"); // NOI18N
        selectAllMenuItem.setName("selectAllMenuItem"); // NOI18N
        selectAllMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllMenuItemClicked(evt);
            }
        });
        editMenu.add(selectAllMenuItem);

        jSeparator1.setName("jSeparator1"); // NOI18N
        editMenu.add(jSeparator1);

        undoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, 0));
        undoMenuItem.setText("Undo"); // NOI18N
        undoMenuItem.setName("undoMenuItem"); // NOI18N
        undoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(undoMenuItem);

        redoMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, 0));
        redoMenuItem.setText("Redo"); // NOI18N
        redoMenuItem.setName("redoMenuItem"); // NOI18N
        redoMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(redoMenuItem);

        jSeparator2.setName("jSeparator2"); // NOI18N
        editMenu.add(jSeparator2);

        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, 0));
        cutMenuItem.setText("Cut"); // NOI18N
        cutMenuItem.setName("cutMenuItem"); // NOI18N
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(cutMenuItem);

        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0));
        copyMenuItem.setText("Copy"); // NOI18N
        copyMenuItem.setName("copyMenuItem"); // NOI18N
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(copyMenuItem);

        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, 0));
        pasteMenuItem.setText("Paste"); // NOI18N
        pasteMenuItem.setName("pasteMenuItem"); // NOI18N
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(pasteMenuItem);

        jSeparator3.setName("jSeparator3"); // NOI18N
        editMenu.add(jSeparator3);

        deleteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_BACK_SPACE, 0));
        deleteMenuItem.setText("Delete Selection"); // NOI18N
        deleteMenuItem.setName("deleteMenuItem"); // NOI18N
        deleteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(deleteMenuItem);

        flattenMenuItem.setText("Flatten Selection"); // NOI18N
        flattenMenuItem.setName("flattenMenuItem"); // NOI18N
        flattenMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                flattenMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(flattenMenuItem);

        breakMenuItem.setText("Break Selection"); // NOI18N
        breakMenuItem.setName("breakMenuItem"); // NOI18N
        breakMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                breakMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(breakMenuItem);

        jSeparator4.setName("jSeparator4"); // NOI18N
        editMenu.add(jSeparator4);

        roadmapMenuBar.add(editMenu);

        transposeMenu.setText("Transpose"); // NOI18N
        transposeMenu.setName("transposeMenu"); // NOI18N

        transposeDownMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        transposeDownMenuItem.setText("Transpose Selection Up Semitone"); // NOI18N
        transposeDownMenuItem.setName("transposeDownMenuItem"); // NOI18N
        transposeDownMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeDownMenuItemActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeDownMenuItem);

        transposeUpMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, 0));
        transposeUpMenuItem.setText("Transpose Selection Down Semitone"); // NOI18N
        transposeUpMenuItem.setName("transposeUpMenuItem"); // NOI18N
        transposeUpMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transposeUpMenuItemActionPerformed(evt);
            }
        });
        transposeMenu.add(transposeUpMenuItem);

        roadmapMenuBar.add(transposeMenu);

        sectionMenu.setText("Sections"); // NOI18N
        sectionMenu.setName("sectionMenu"); // NOI18N

        toggleSectionMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, 0));
        toggleSectionMenuItem.setText("Toggle Section"); // NOI18N
        toggleSectionMenuItem.setName("toggleSectionMenuItem"); // NOI18N
        toggleSectionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toggleSectionMenuItemActionPerformed(evt);
            }
        });
        sectionMenu.add(toggleSectionMenuItem);

        togglePhraseMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ENTER, java.awt.event.InputEvent.SHIFT_MASK));
        togglePhraseMenuItem.setText("Toggle Phrase"); // NOI18N
        togglePhraseMenuItem.setName("togglePhraseMenuItem"); // NOI18N
        togglePhraseMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                togglePhraseMenuItemActionPerformed(evt);
            }
        });
        sectionMenu.add(togglePhraseMenuItem);

        roadmapMenuBar.add(sectionMenu);

        leadsheetMenu.setText("Leadsheet"); // NOI18N
        leadsheetMenu.setToolTipText("Transfer roadmap chord changes into a leadsheet."); // NOI18N
        leadsheetMenu.setName("leadsheetMenu"); // NOI18N

        appendToLeadsheetMI.setText("Append selection chords to most recent leadsheet created\n\n"); // NOI18N
        appendToLeadsheetMI.setToolTipText("Appends the chords in the current selection to a created leadsheet, creating one if none exits."); // NOI18N
        appendToLeadsheetMI.setName("appendToLeadsheetMI"); // NOI18N
        appendToLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appendToLeadsheetMIaction(evt);
            }
        });
        leadsheetMenu.add(appendToLeadsheetMI);

        appendToNewLeadsheetMI.setText("Create a new leadsheet and add selected chords to it  "); // NOI18N
        appendToNewLeadsheetMI.setToolTipText("Create a new leadsheet and add selected chords to it."); // NOI18N
        appendToNewLeadsheetMI.setName("appendToNewLeadsheetMI"); // NOI18N
        appendToNewLeadsheetMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appendToNewLeadsheetMIaction(evt);
            }
        });
        leadsheetMenu.add(appendToNewLeadsheetMI);

        roadmapMenuBar.add(leadsheetMenu);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window"); // NOI18N
        windowMenu.setName("windowMenu"); // NOI18N
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
        closeWindowMI.setText("Close Window"); // NOI18N
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)"); // NOI18N
        closeWindowMI.setName("closeWindowMI"); // NOI18N
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows"); // NOI18N
        cascadeMI.setToolTipText("Rearrange windows into a cascade.\n"); // NOI18N
        cascadeMI.setName("cascadeMI"); // NOI18N
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);

        jSeparator5.setName("jSeparator5"); // NOI18N
        windowMenu.add(jSeparator5);

        brickLibraryMenuItem.setText("Brick Library"); // NOI18N
        brickLibraryMenuItem.setName("brickLibraryMenuItem"); // NOI18N
        brickLibraryMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                brickLibraryMenuItemActionPerformed(evt);
            }
        });
        windowMenu.add(brickLibraryMenuItem);

        windowMenuSeparator.setName("windowMenuSeparator"); // NOI18N
        windowMenu.add(windowMenuSeparator);

        roadmapMenuBar.add(windowMenu);

        setJMenuBar(roadmapMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /* IMPORTANT: Any menu item with accelerators should include the conditional
     * if(!roadMapTextEntry.isFocusOwner())
     * Otherwise it will do actions while you type in the text field.
     * I've added it to all menu items just in case they're given accelerators.
     * It's dumb, but it's for safety. Feel free to implement a better solution.
     */
    // <editor-fold defaultstate="collapsed" desc="Events">
    private void libraryTreeSelected(javax.swing.event.TreeSelectionEvent evt) {//GEN-FIRST:event_libraryTreeSelected
        setPreview();
}//GEN-LAST:event_libraryTreeSelected

    private void previewScrollPanepreviewPaneReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewScrollPanepreviewPaneReleased
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue() + previewScrollPane.getY() - roadMapScrollPane.getY();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue() + previewScrollPane.getX();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        dropFromPreview(x, y);
        System.out.println();
}//GEN-LAST:event_previewScrollPanepreviewPaneReleased

    private void previewScrollPanepreviewPaneDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_previewScrollPanepreviewPaneDragged
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue() + previewScrollPane.getY() - roadMapScrollPane.getY();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue() + previewScrollPane.getX();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        dragFromPreview(x, y);
}//GEN-LAST:event_previewScrollPanepreviewPaneDragged

    private void durationComboBoxdurationChosen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_durationComboBoxdurationChosen
        setPreviewDuration();
}//GEN-LAST:event_durationComboBoxdurationChosen

    private void roadMapScrollPaneroadMapReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapReleased
        int x = evt.getX() + roadMapScrollPane.getHorizontalScrollBar().getValue();
        int y = evt.getY() + roadMapScrollPane.getVerticalScrollBar().getValue();
        dropCurrentBrick(x, y);
}//GEN-LAST:event_roadMapScrollPaneroadMapReleased

    private void roadMapScrollPaneroadMapClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapClicked
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        int index = roadMapPanel.getBrickIndexAt(x,y);
        if(evt.getButton() == evt.BUTTON1) {
            if(index != -1) {
                int jndex = roadMapPanel.getBrick(index).getChordAt(x, y);
                if(evt.isShiftDown())
                    selectBricks(index);
                else if( roadMapPanel.getBrick(index).isSelected() &&
                        evt.getClickCount() == 2 && jndex != -1) {
                    selectChord(index,jndex);
                    activateChordDialog();
                } else if( roadMapPanel.getBrick(index).isSelected() && jndex != -1) {
                    selectChord(index,jndex);
                } else
                    selectBrick(index);
            } else //TODO, renaming and other outside clicks
                deselectBricks();
        } else if(evt.getButton() == evt.BUTTON3) {
            // Nothing
        }
        roadMapScrollPane.requestFocus();
}//GEN-LAST:event_roadMapScrollPaneroadMapClicked

    private void roadMapScrollPaneroadMapDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapDragged
        int x = evt.getX() + roadMapScrollPane.getHorizontalScrollBar().getValue();
        int y = evt.getY() + roadMapScrollPane.getVerticalScrollBar().getValue();
        dragSelectedBricks(x, y);
}//GEN-LAST:event_roadMapScrollPaneroadMapDragged

    private void roadMapScrollPaneroadMapKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapKeyPressed
        switch (evt.getKeyCode()) {
            default:                                                    break;
        }
}//GEN-LAST:event_roadMapScrollPaneroadMapKeyPressed

    private void roadMapScrollPaneroadMapKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_roadMapScrollPaneroadMapKeyReleased
        switch (evt.getKeyCode()) {
            default:                            break;
        }
}//GEN-LAST:event_roadMapScrollPaneroadMapKeyReleased

    private void flattenButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenButtonPressed
        flattenSelection();
}//GEN-LAST:event_flattenButtonPressed

    private void breakButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_breakButtonPressed
        breakSelection();
}//GEN-LAST:event_breakButtonPressed

    private void scaleComboBoxscaleComboReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_scaleComboBoxscaleComboReleased
}//GEN-LAST:event_scaleComboBoxscaleComboReleased

    private void scaleComboBoxscaleChosen(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleComboBoxscaleChosen
        scaleSelection();
        scaleComboBox.setSelectedItem("x1");
}//GEN-LAST:event_scaleComboBoxscaleChosen

    private void newBrickButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newBrickButtonPressed
        dialogNameField.setText("New Brick");
        dialogVariantField.setText("");
        dialogTypeComboBox.setSelectedIndex(0);
        dialogKeyComboBox.setSelectedIndex(0);
        addBrickDialog.setVisible(true);
}//GEN-LAST:event_newBrickButtonPressed

    private void analyzeButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_analyzeButtonPressed
        if(!roadMapPanel.hasSelection())
            roadMapPanel.selectAll();
        analyzeSelection();
}//GEN-LAST:event_analyzeButtonPressed

    private void exitMIhandler(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMIhandler
        closeWindow();
    }//GEN-LAST:event_exitMIhandler

    private void dialogAccepted(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dialogAccepted
        //TODO: maybe put warning/don't close window when brick name is taken
        addBrickDialog.setVisible(false);
        makeBrickFromSelection();
    }//GEN-LAST:event_dialogAccepted

    private void selectAllBricksButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllBricksButtonPressed
        selectAllBricks();
    }//GEN-LAST:event_selectAllBricksButtonPressed

    private void playButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playButtonPressed
        playSelection();
    }//GEN-LAST:event_playButtonPressed

    private void stopButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_stopButtonPressed
        stopPlayingSelection();
    }//GEN-LAST:event_stopButtonPressed

    private void featureWidthSliderChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_featureWidthSliderChanged
        settings.measureLength = featureWidthSlider.getValue();
        roadMapPanel.placeBricks();
        setFeatureWidthLocked(false);
    }//GEN-LAST:event_featureWidthSliderChanged

    private void selectAllMenuItemClicked(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectAllMenuItemClicked
        if(!roadMapTextEntry.isFocusOwner()) selectAllBricks();
    }//GEN-LAST:event_selectAllMenuItemClicked

    private void undoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) undo();
    }//GEN-LAST:event_undoMenuItemActionPerformed

    private void redoMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) redo();
    }//GEN-LAST:event_redoMenuItemActionPerformed

    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) cutSelection();
    }//GEN-LAST:event_cutMenuItemActionPerformed

    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) copySelection();
    }//GEN-LAST:event_copyMenuItemActionPerformed

    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) pasteSelection();
    }//GEN-LAST:event_pasteMenuItemActionPerformed

    private void deleteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) deleteSelection();
    }//GEN-LAST:event_deleteMenuItemActionPerformed

    private void flattenMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) flattenSelection();
    }//GEN-LAST:event_flattenMenuItemActionPerformed

    private void breakMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_breakMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) breakSelection();
    }//GEN-LAST:event_breakMenuItemActionPerformed

    private void toggleSectionMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toggleSectionMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) toggleSectionBreak();
    }//GEN-LAST:event_toggleSectionMenuItemActionPerformed

    private void loopToggleButtonPressed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loopToggleButtonPressed
        if( loopToggleButton.isSelected() )
          {
            loopToggleButton.setText("No Loop");
            loopToggleButton.setBackground(Color.RED);
          }
        else
          {
            loopToggleButton.setText("Loop");
            loopToggleButton.setBackground(Color.GREEN);
            stopPlayingSelection();
          }
    }//GEN-LAST:event_loopToggleButtonPressed

    
    /**
     * Add chords in leadsheet notation (with bar lines, etc.) from textual Entry
     * @param evt 
     */
    private void textualEntryKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textualEntryKeyPressed
        if( evt.getKeyCode() == KeyEvent.VK_ENTER ) {
            saveState("TextEntry");

            String entered = roadMapTextEntry.getText();

            if( !entered.isEmpty() ){
                Score score = new Score();
                score.setMetre(getMetre());
                Tokenizer tokenizer = new Tokenizer(new StringReader(entered));

                Leadsheet.readLeadSheet(tokenizer, score);

                if( score.getPart(0).size() > 0 )
                    ErrorLog.log(ErrorLog.WARNING, "Melody notes entered with chord part will be ignored.");

            roadMapPanel.addBlocksBeforeSelection(score.getChordProg().toBlockList(), true); 
            roadMapPanel.placeBricks();
            activateButtons();

            this.requestFocus();
            }
        }
    }//GEN-LAST:event_textualEntryKeyPressed

    private void chordDialogAcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordDialogAcceptButtonActionPerformed
        String name = chordDialogNameField.getText();
        if(ChordSymbol.makeChordSymbol(name) != null) {
            chordChangeDialog.setVisible(false);
            changeChord(name,
                    (Integer)chordDialogDurationComboBox.getSelectedItem() * settings.getSlotsPerBeat());
        }
    }//GEN-LAST:event_chordDialogAcceptButtonActionPerformed

    private void chordDialogNameFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordDialogNameFieldActionPerformed
        chordDialogAcceptButtonActionPerformed(evt);
    }//GEN-LAST:event_chordDialogNameFieldActionPerformed

    private void libraryTreeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_libraryTreeMouseClicked
        int clicks = evt.getClickCount();
        TreePath path = libraryTree.getPathForLocation(evt.getX(), evt.getY());
        if(path != null && previewPanel.getBrick() != null && clicks%2==0)
            addBrickFromPreview();
    }//GEN-LAST:event_libraryTreeMouseClicked

    private void transposeDownMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeDownMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) transposeSelection(1);
    }//GEN-LAST:event_transposeDownMenuItemActionPerformed

    private void transposeUpMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transposeUpMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) transposeSelection(-1);
    }//GEN-LAST:event_transposeUpMenuItemActionPerformed

    private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_closeWindowMIActionPerformed
        closeWindow();
    }//GEN-LAST:event_closeWindowMIActionPerformed

    private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cascadeMIActionPerformed
        
        WindowRegistry.cascadeWindows(this);
    }//GEN-LAST:event_cascadeMIActionPerformed

    private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_windowMenuMenuSelected
        
        windowMenu.removeAll();
        
        windowMenu.add(closeWindowMI);
        
        windowMenu.add(cascadeMI);
        
        windowMenu.add(windowMenuSeparator);
        
        windowMenu.add(brickLibraryMenuItem);
        
        windowMenu.add(jSeparator5);
        
        for(WindowMenuItem w : WindowRegistry.getWindows())
            windowMenu.add(w.getMI(this));       // these are static, and calling getMI updates the name on them too in case the window title changed
        
        windowMenu.repaint();
    }//GEN-LAST:event_windowMenuMenuSelected

    private void roadMapWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_roadMapWindowClosing
        closeWindow();
    }//GEN-LAST:event_roadMapWindowClosing

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        roadMapPanel.requestFocusInWindow();
    }//GEN-LAST:event_formWindowActivated

    private void togglePhraseMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_togglePhraseMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) togglePhraseEnd();
    }//GEN-LAST:event_togglePhraseMenuItemActionPerformed

    private void openLeadsheetMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openLeadsheetMIActionPerformed
        notate.openLeadsheet(false);
}//GEN-LAST:event_openLeadsheetMIActionPerformed

    private void appendToLeadsheetMIaction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appendToLeadsheetMIaction
        if(!roadMapTextEntry.isFocusOwner()) appendSelectionToNotate();
    }//GEN-LAST:event_appendToLeadsheetMIaction

    private void appendToNewLeadsheetMIaction(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appendToNewLeadsheetMIaction
        if(!roadMapTextEntry.isFocusOwner()) sendSelectionToNewNotate();
    }//GEN-LAST:event_appendToNewLeadsheetMIaction

    private void keyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_keyComboBoxActionPerformed
        setPreviewKey();
    }//GEN-LAST:event_keyComboBoxActionPerformed

    private void roadMapScrollPaneMouseMoved(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_roadMapScrollPaneMouseMoved
        int yOffset = roadMapScrollPane.getVerticalScrollBar().getValue();
        int xOffset = roadMapScrollPane.getHorizontalScrollBar().getValue();
        int x = evt.getX()+xOffset;
        int y = evt.getY()+yOffset;
        roadMapPanel.setRolloverPos(new Point(x,y));
        roadMapPanel.draw();
    }//GEN-LAST:event_roadMapScrollPaneMouseMoved

    private void printRoadMapMIActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_printRoadMapMIActionPerformed
        PrintUtilitiesRoadMap.printRoadMap(roadMapPanel);
    }//GEN-LAST:event_printRoadMapMIActionPerformed


    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        if (previewPanel.currentBrick != null)
        {
            brickLibrary.exileBrick((Brick)previewPanel.currentBrick.getBlock());
            initLibraryTree();
            libraryTree.setModel(libraryTreeModel);
            cykParser.createRules(brickLibrary);
        }
    }//GEN-LAST:event_deleteButtonActionPerformed


    private void preferencesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_preferencesMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) activatePreferencesDialog();
    }//GEN-LAST:event_preferencesMenuItemActionPerformed

    private void prefDialogAcceptButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_prefDialogAcceptButtonActionPerformed
        if( prefDialogMetreBottomField.getInt() % 2 == 0) {
            preferencesDialog.setVisible(false);
            setRoadMapInfo();
        } else
            ErrorLog.log(ErrorLog.COMMENT, "Metre bottom must be 1, 2, 4 or 8");
    }//GEN-LAST:event_prefDialogAcceptButtonActionPerformed

    private void brickLibraryMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_brickLibraryMenuItemActionPerformed
        if(!roadMapTextEntry.isFocusOwner()) brickLibraryFrame.setVisible(brickLibraryMenuItem.isSelected());
    }//GEN-LAST:event_brickLibraryMenuItemActionPerformed

    private void brickLibraryFrameWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_brickLibraryFrameWindowClosing
        brickLibraryMenuItem.setSelected(false);
        WindowRegistry.unregisterWindow(brickLibraryFrame);
    }//GEN-LAST:event_brickLibraryFrameWindowClosing

    private void dialogNameFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dialogNameFieldKeyReleased
        if(brickLibrary.hasBrick(BrickLibrary.dashless(dialogNameField.getText()))) {
            System.err.println(dialogNameField.getText());
            dialogVariantField.setEditable(true);
            dialogVariantLabel.setEnabled(true);
        } else {
            dialogVariantField.setEditable(false);
            dialogVariantLabel.setEnabled(false);
        }
    }//GEN-LAST:event_dialogNameFieldKeyReleased

    private void brickLibraryFrameComponentShown(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_brickLibraryFrameComponentShown
        WindowRegistry.registerWindow(brickLibraryFrame);
    }//GEN-LAST:event_brickLibraryFrameComponentShown

    private void brickLibraryFrameComponentHidden(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_brickLibraryFrameComponentHidden
        WindowRegistry.unregisterWindow(brickLibraryFrame);
    }//GEN-LAST:event_brickLibraryFrameComponentHidden

    private void featureWidthSliderMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_featureWidthSliderMouseClicked
        if(evt.getClickCount()%2 == 0) {
            scaleToWindow();
        } else {
            setFeatureWidthLocked(false);
        }
    }//GEN-LAST:event_featureWidthSliderMouseClicked

    private void formComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_formComponentResized
        javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)featureWidthSlider.getBorder();
        if(border.getTitle().endsWith(featureWidthSuffix))
            scaleToWindow();
    }//GEN-LAST:event_formComponentResized

    private void libraryTreeTreeCollapsed(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_libraryTreeTreeCollapsed
        libraryTree.setPreferredSize(
                new Dimension(libraryTree.getPreferredSize().width,
                libraryTree.getRowCount() * libraryTree.getRowHeight()));
    }//GEN-LAST:event_libraryTreeTreeCollapsed

    private void libraryTreeTreeExpanded(javax.swing.event.TreeExpansionEvent evt) {//GEN-FIRST:event_libraryTreeTreeExpanded
        libraryTree.setPreferredSize(
                new Dimension(libraryTree.getPreferredSize().width,
                libraryTree.getRowCount() * libraryTree.getRowHeight()));
    }//GEN-LAST:event_libraryTreeTreeExpanded

    private void fileStepBackBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepBackBtnActionPerformed
        notate.fileStepBackward();
        if(notate.getCreateRoadMapCheckBox())
            ;//notate.roadMapThisAnalyze();
        else
            notate.roadMapThis();
}//GEN-LAST:event_fileStepBackBtnActionPerformed

    private void fileStepForwardBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fileStepForwardBtnActionPerformed
        notate.fileStepForward();
        if(notate.getCreateRoadMapCheckBox())
            ;//notate.roadMapThisAnalyze();
        else
            notate.roadMapThis();
}//GEN-LAST:event_fileStepForwardBtnActionPerformed

//</editor-fold>
    /** Creates the play timer and adds a listener */
    private void initTimer()
    {
        playTimer = new javax.swing.Timer(10,
                new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        if(notate.getMidiSynthRM().finishedPlaying())
                            setPlaying(false);
                        roadMapPanel.draw();
                    }
                }
                );
    }
    
    /** Initializes the buffers for the roadmap and preview panel. */
    private void initBuffer()
    {
      try 
        {
        bufferPreviewPanel = new java.awt.image.BufferedImage(previewBufferWidth, previewBufferHeight, BufferedImage.TYPE_INT_RGB);
        bufferRoadMap = new java.awt.image.BufferedImage(roadMapBufferWidth, roadMapBufferHeight, BufferedImage.TYPE_INT_RGB);
        previewPanel.setBuffer(bufferPreviewPanel);
        roadMapPanel.setBuffer(bufferRoadMap);
        
        
        roadMapScrollPane.setViewportView(roadMapPanel);
        
        roadMapPanel.draw();
        previewPanel.draw();
        }
      catch( java.lang.OutOfMemoryError e)
        {
        ErrorLog.log(ErrorLog.SEVERE, "Out of memory. It will not be possible to continue.");
        }
    }
    
    /** Builds the library tree from the brick library */
    private void initLibraryTree()
    {
        LinkedList<LinkedList<Brick>> bricks = brickLibrary.getMap();
        
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        
        ArrayList<String> categoryNames = new ArrayList(Arrays.asList(brickLibrary.getTypes()));
        ArrayList<DefaultMutableTreeNode> categories = new ArrayList();
        
        for(String name : categoryNames)
            categories.add(new DefaultMutableTreeNode(name));
        
        DefaultMutableTreeNode node = null;
        
        for( LinkedList<Brick> variants : bricks )
        {
            Brick brick = variants.getFirst();
            String name = brick.getName();
            String type = brick.getType();
            
            node = new DefaultMutableTreeNode(name);
            
            if(variants.size() > 1)
                for( Brick variant : variants)
                    node.add(new DefaultMutableTreeNode(variant.getVariant()));
             
            int ind = categoryNames.indexOf(type);
            
            if(ind != -1)
                categories.get(ind).add(node);
            else
                System.err.println(type+" is not in type list.");
        }
        
        for(DefaultMutableTreeNode type : categories)
            root.add(type);
        
        libraryTreeModel = new DefaultTreeModel(root);
    }
    
    /** Recycle buffer memory by setting them to null*/
    private void disposeBuffers()
    {
        bufferPreviewPanel = null;
        bufferRoadMap = null;
        previewPanel.setBuffer(null);
        roadMapPanel.setBuffer(null);
    }

    /** Paints the image white.
     * @param image, an Image
     */
    public void setBackground(Image image)
    {
        Graphics graphics = image.getGraphics();
        graphics.setColor(Color.white);
        graphics.fillRect(0, 0, image.getWidth(null), image.getHeight(null));
    }
    
    /** setBackgrounds <p>
     * Sets the background of each bufferPreviewPanel.
     */
    public void setBackgrounds()
    {
        setBackground(bufferPreviewPanel);
        setBackground(bufferRoadMap);
    }
 
    /** Sets the roadmap and frame title to the given string */
    public void setRoadMapTitle(String title)
    {
        setTitle(roadMapTitlePrefix + title);
        roadMapTitle = title;
        roadMapPanel.draw();
    }
    
    /** Returns the current graphical settings */
    public RoadMapSettings getSettings()
    {
        return settings;
    }
    
    /** Returns the brick library */
    public BrickLibrary getLibrary()
    {
        return brickLibrary;
    }
    
    /** Returns the chords in the current selection */
    public ArrayList<ChordBlock> getChordsInSelection()
    {
        return RoadMap.getChords(roadMapPanel.getSelection());
    }
    
    /** Saves the current state of the roadmap */
    private void saveState(String name)
    {
        stopPlayingSelection(); //TODO this probably doesn't belong here,
        //but I don't want to write it over and over again and this is called for
        //in relevant actions
        if(name.equals("Transpose") &&
                roadMapHistory.getLast().getName().equals("Transpose"))
            return; //Multiple transpositions should be the same action
                    //ISSUE: changing multiple bricks in sequence undoes them all
        RoadMapSnapShot ss = new RoadMapSnapShot(name, roadMapPanel.getRoadMap());
        roadMapHistory.add(ss);
        roadMapFuture.clear();
    }
    
    /** Reverts to the previous state */
    private void stepStateBack()
    {
        if(roadMapHistory.peek() != null) {
            RoadMapSnapShot ss = roadMapHistory.removeLast();
            roadMapFuture.add(new RoadMapSnapShot(ss.getName(), roadMapPanel.getRoadMap()));
            roadMapPanel.setRoadMap(ss.getRoadMap());
        }
    }
    
    /** Verts to the next state */
    private void stepStateForward()
    {
        if(roadMapFuture.peek() != null) {
            RoadMapSnapShot ss = roadMapFuture.removeLast();
            roadMapHistory.add(new RoadMapSnapShot(ss.getName(), roadMapPanel.getRoadMap()));
            roadMapPanel.setRoadMap(ss.getRoadMap());
        }
    }
    
    /** Undoes the any actions performed */
    private void undo()
    {
        deselectBricks();
        stepStateBack();
        //System.out.println("History: " + roadMapHistory);
        //System.out.println("Future: " + roadMapFuture);
        roadMapPanel.placeBricks();
    }
    
    /** Redoes any undone action */
    private void redo()
    {
        deselectBricks();
        stepStateForward();
        //System.out.println("History: " + roadMapHistory);
        //System.out.println("Future: " + roadMapFuture);
        roadMapPanel.placeBricks();
    }
       
    /* -------- Actions -------- */
    
    /** Action to add a chord to the roadmap */
    public void addChord(ChordBlock chord)
    {
        saveState("AddChord");
        roadMapPanel.addBlock(chord);
        roadMapPanel.placeBricks();
    }
    
    /** Action to insert a list of blocks into the roadmap */
    public void addBlocks(int ind, ArrayList<Block> blocks)
    {
        saveState("AddBricks");
        roadMapPanel.addBlocks(ind, blocks);
        roadMapPanel.placeBricks();
    }
    
    /** Action to delete the selection */
    public void deleteSelection()
    {
        saveState("Delete");
        roadMapPanel.deleteSelection();
        deactivateButtons();
    }
    
    /** Action to break the selected bricks */
    public void breakSelection()
    {
        saveState("Break");
        roadMapPanel.breakSelection();
    }
    
    /** Action to create a new brick from the selection */
    public void makeBrickFromSelection()
    {
        saveState("Merge");
        long key = BrickLibrary.keyNameToNum((String) dialogKeyComboBox.getSelectedItem());
        String name = dialogNameField.getText();
        
        String variant = "";
        if(brickLibrary.hasBrick(name))
            variant = dialogVariantField.getText();
        
        String mode = (String)dialogModeComboBox.getSelectedItem();
        String type = (String)dialogTypeComboBox.getSelectedItem();
        Brick newBrick = roadMapPanel.makeBrickFromSelection(name, variant, key,
                                                             mode, type);
        addToLibrary(newBrick);
    }

    /** Action to transpose the key of the selection */
    public void transposeSelection(long diff)
    {
        saveState("Transpose");
        roadMapPanel.transposeSelection(diff);
    }
    
    /** Action to analyze the selection */
    public void analyzeSelection()
    {
        saveState("Analyze");
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        
        ArrayList<Block> blocks = roadMapPanel.getSelection();
        roadMapPanel.replaceSelection(analyze(blocks));
        
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
    
    /** Action to flatten the selected bricks */
    public void flattenSelection()
    {
        saveState("Flatten");
        roadMapPanel.flattenSelection();
    }
    
    /** Action to scale the durations of the selected bricks */
    public void scaleSelection()
    {
        saveState("Scale");
        String choice = (String)scaleComboBox.getSelectedItem();
        
        if( choice == null )
            return;
        
        int scale = choice.charAt(1) - 48; // set to integer
        
        if( choice.charAt(0) == 47) //  / = division
            scale = -scale;
        
        roadMapPanel.scaleSelection(scale);       
    }
    
    /** Action to change a chord's name and/or duration */
    public void changeChord(String name, int dur)
    {
        saveState("ChordChange");
        roadMapPanel.changeChord(name, dur);
    }
    
    /** Action to add/remove a phrase end */
    private void togglePhraseEnd()
    {
        saveState("PhraseEnd");
        roadMapPanel.togglePhrase();
    }
    
    /** Action to add/remove a section end */
    private void toggleSectionBreak()
    {
        saveState("SectionBreak");
        roadMapPanel.toggleSection();
    }
    
    /** Action to add a section end to the end of the roadmap */
    public void endSection()
    {
        saveState("SectionBreak");
        roadMapPanel.endSection();
    }

    /** Action to cut the selection, adding it to the clipboard */
    public void cutSelection()
    {
        saveState("Cut");
        clipboard = roadMapPanel.removeSelection();
        roadMapPanel.placeBricks();
    }
    
    /** Action to paste the selection, adding it to the roadmap from the clipboard */
    public void pasteSelection()
    {
        saveState("Paste");
        
        ArrayList<Block> blocks = RoadMap.cloneBlocks(clipboard);
        
        roadMapPanel.addBlocksBeforeSelection(blocks, false);
        
        roadMapPanel.placeBricks();
    }
    
    /** Action to copy the selection, adding it to the clipboard */
    public void copySelection()
    {
        clipboard = RoadMap.cloneBlocks(roadMapPanel.getSelection());        
    }
    
    /** Implements dragging behavior.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dragSelectedBricks(int x, int y)
    {   
        int index = roadMapPanel.getBrickIndexAt(x, y);
        if( draggedBricks.isEmpty() ) {
            saveState("Drag");
            roadMapPanel.setRolloverPos(null);
            if( !roadMapPanel.isSelection(index))
                selectBrick(index);
            if( roadMapPanel.hasSelection() )
                draggedBricks = roadMapPanel.makeBricks(roadMapPanel.removeSelectionNoUpdate());
        }
        
        if( !draggedBricks.isEmpty() ) {
            roadMapPanel.setInsertLine(x, y);
            roadMapPanel.draw();
            roadMapPanel.drawBricksAt(draggedBricks, x, y);
        }
    }
    
    /** Implements dropping behavior.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dropCurrentBrick(int x, int y)
    {   
        if( !draggedBricks.isEmpty() ) {
            int index = roadMapPanel.getIndexAt(x, y);
            roadMapPanel.addBlocks(index, roadMapPanel.makeBlocks(draggedBricks), true);
            draggedBricks.clear();
            roadMapPanel.setInsertLine(-1);
        }
        roadMapPanel.placeBricks();
    }
    
    /** Implements dragging behavior from the preview window.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dragFromPreview(int x, int y) 
    {        
        if( draggedBricks.isEmpty() ) {
            roadMapPanel.deselectBricks();
            
            if (previewPanel.currentBrick != null) {
                draggedBricks.add(previewPanel.getBrick());
                setPreview();
            }
        }
        dragSelectedBricks(x, y);
    }
    
    /** Implements dropping behavior from the preview window.
     * @param x, the x-coordinate of the mouse
     * @param y, the y-coordinate of the mouse */
    public void dropFromPreview(int x, int y)
    {
        saveState("Drop");
        dropCurrentBrick(x, y);
        activateButtons();
    }
    
    /** Adds the current preview brick to the roadmap */
    public void addBrickFromPreview()
    {
        saveState("Drop");
        ArrayList<Block> block = new ArrayList();
        block.add(previewPanel.getBlock());
        roadMapPanel.addBlocksBeforeSelection(block, true);
        roadMapPanel.placeBricks();
    }
    
    /** Sets the preview brick (from the library), as well as its duration and key. */
    public void setPreview()
    {
        TreePath path = libraryTree.getSelectionPath();
        if(path != null) {

            int pathLength = path.getPathCount();
            
            if( pathLength > 2 ) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode)path.getLastPathComponent();
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode)path.getParentPath().getLastPathComponent();
                Brick brick;
                
                if(pathLength > 3 )
                    brick = brickLibrary.getBrick(parent.toString(),node.toString(), 0);
                else
                    brick = brickLibrary.getBrick(node.toString(), 0);
                
                //setDurationChoices(brick);
                previewPanel.setBrick( brick );

                setPreviewKey();
                setPreviewDuration();
            }
            
        }
    }
    
    /** Sets the key of the brick in the preview pane to the key chosen by
     * the key spinner. */
    public void setPreviewKey()
    {
        String key = (String)keyComboBox.getSelectedItem();
        if(BrickLibrary.isValidKey(key))
            previewPanel.setKey( key );
    }
    
    /** Sets the duration of the brick in the preview pane to the key chosen by
     * the duration combo box. */
    public void setPreviewDuration()
    {
        previewPanel.setDuration(settings.getSlotsPerBeat()*(Integer)durationChoices[durationComboBox.getSelectedIndex()]);
        previewPanel.draw();
    }
           
    /** Adds the brick at index to the selection, either extending the selection
     * or reducing it depending on whether the brick is selected. 
     * @param index, the index of the brick to be selected */
    public void selectBricks(int index)
    {
        roadMapPanel.selectBricks(index);
        activateButtons();   
    }
    
    /** Selects all bricks in the roadmap */
    public void selectAllBricks()
    {
        roadMapPanel.selectAll();
        activateButtons();
    }
    
    /** Selects only the brick at index, deselecting all other bricks.
     * @param index, the index of the brick to be selected */
    public void selectBrick(int index)
    {
        roadMapPanel.selectBrick(index);
        activateButtons();
    }
    
    /** Selects a chord within a brick
     * @param brickInd the index of the brick
     * @param chordInd the index of the chord within the brick */
    public void selectChord(int brickInd, int chordInd)
    {
        roadMapPanel.selectChord(brickInd, chordInd);
        deactivateButtons();
        deleteMenuItem.setEnabled(true);
        sectionMenu.setEnabled(true);
    }
    
    /** Deselects all bricks. */
    public void deselectBricks()
    {
        roadMapPanel.deselectBricks();
        deactivateButtons();
    }
       
    /** Uses cykParser to analyze a list of blocks */
    public ArrayList<Block> analyze(ArrayList<Block> blocks)
    {
        long startTime = System.currentTimeMillis();
        ArrayList<Block> result = cykParser.parse(blocks, brickLibrary);
        long endTime = System.currentTimeMillis();
        System.err.println("Analysis: " + (endTime - startTime) + "ms");
        
        return result;
    }  
  
    /** Deactivates relevant buttons for selection */
    public void deactivateButtons()
    {
        setButtonEnabled(false);
    }
    
    /** Activates relevant buttons for selection */
    public void activateButtons()
    {
        setButtonEnabled(true);
    }
    
    /** Activates/Deactivates relecent buttons for selection */
    public void setButtonEnabled(boolean value)
    {
        cutMenuItem.setEnabled(value);
        copyMenuItem.setEnabled(value);
        
        transposeMenu.setEnabled(value);
        
        sectionMenu.setEnabled(value);
        
        flattenButton.setEnabled(value);
        flattenMenuItem.setEnabled(value);
       
        deleteMenuItem.setEnabled(value);
        
        breakButton.setEnabled(value);
        breakMenuItem.setEnabled(value);
        
        newBrickButton.setEnabled(value);
        scaleComboBox.setEnabled(value);
    }

    /** Adds a brick to the brick library */
    private void addToLibrary(Brick brick)
    {
        Brick scaledBrick = new Brick(brick);
        scaledBrick.reduceDurations();
        brickLibrary.addBrickDefinition(scaledBrick);
        cykParser.createRules(brickLibrary);
        initLibraryTree();
        libraryTree.setModel(libraryTreeModel);
        if (scaledBrick.getVariant().isEmpty())
            addToLibraryTree(scaledBrick.getName());
        else
            addToLibraryTree(scaledBrick.getName(), scaledBrick.getVariant());
    }
    
    /** Adds a brick name to the library tree */
    private void addToLibraryTree(String name)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)libraryTreeModel.getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getLastChild();
        
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        if(node.toString().equals("Recently Created")) {
            libraryTreeModel.insertNodeInto(newNode, node, node.getChildCount());
        } else {
            DefaultMutableTreeNode newParent = new DefaultMutableTreeNode("Recently Created");
            newParent.add(newNode);
            libraryTreeModel.insertNodeInto(newParent, root, root.getChildCount());
        }
    }
    
    /** Adds a brick by name and variant to the library tree */
    private void addToLibraryTree(String name, String variant)
    {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode)libraryTreeModel.getRoot();
        DefaultMutableTreeNode node = (DefaultMutableTreeNode)root.getLastChild();
        
        DefaultMutableTreeNode variantNode= new DefaultMutableTreeNode(variant);
        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(name);
        newNode.add(variantNode);
        if(node.toString().equals("Recently Created")) {
            libraryTreeModel.insertNodeInto(newNode, node, node.getChildCount());
        } else {
            DefaultMutableTreeNode newParent = new DefaultMutableTreeNode("Recently Created");
            newParent.add(newNode);
            libraryTreeModel.insertNodeInto(newParent, root, root.getChildCount());
        }
    }
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog addBrickDialog;
    private javax.swing.JButton analyzeButton;
    private javax.swing.JMenuItem appendToLeadsheetMI;
    private javax.swing.JMenuItem appendToNewLeadsheetMI;
    private javax.swing.JButton breakButton;
    private javax.swing.JMenuItem breakMenuItem;
    private javax.swing.JFrame brickLibraryFrame;
    private javax.swing.JCheckBoxMenuItem brickLibraryMenuItem;
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JDialog chordChangeDialog;
    private javax.swing.JButton chordDialogAcceptButton;
    private javax.swing.JComboBox chordDialogDurationComboBox;
    private javax.swing.JTextField chordDialogNameField;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JMenuItem copyMenuItem;
    private javax.swing.JMenuItem cutMenuItem;
    private javax.swing.JButton deleteButton;
    private javax.swing.JMenuItem deleteMenuItem;
    private javax.swing.JButton dialogAcceptButton;
    private javax.swing.JComboBox dialogKeyComboBox;
    private javax.swing.JLabel dialogKeyLabel;
    private javax.swing.JComboBox dialogModeComboBox;
    private javax.swing.JTextField dialogNameField;
    private javax.swing.JLabel dialogNameLabel;
    private javax.swing.JComboBox dialogTypeComboBox;
    private javax.swing.JLabel dialogTypeLabel;
    private javax.swing.JTextField dialogVariantField;
    private javax.swing.JLabel dialogVariantLabel;
    private javax.swing.JComboBox durationComboBox;
    private javax.swing.JMenu editMenu;
    private javax.swing.JSlider featureWidthSlider;
    private javax.swing.JButton fileStepBackBtn;
    private javax.swing.JButton fileStepForwardBtn;
    private javax.swing.JButton flattenButton;
    private javax.swing.JMenuItem flattenMenuItem;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JComboBox keyComboBox;
    private javax.swing.JMenu leadsheetMenu;
    private javax.swing.JScrollPane libraryScrollPane;
    private javax.swing.JTree libraryTree;
    private javax.swing.JToggleButton loopToggleButton;
    private javax.swing.JButton newBrickButton;
    private javax.swing.JMenuItem openLeadsheetMI;
    private javax.swing.JMenuItem pasteMenuItem;
    private javax.swing.JButton playButton;
    private javax.swing.JButton prefDialogAcceptButton;
    private javax.swing.JCheckBox prefDialogKeyColorCheckBox;
    private javax.swing.JLabel prefDialogMeterLabel;
    private imp.roadmap.IntegerField prefDialogMetreBottomField;
    private imp.roadmap.IntegerField prefDialogMetreTopField;
    private javax.swing.JComboBox prefDialogStyleComboBox;
    private imp.roadmap.IntegerField prefDialogTempoField;
    private javax.swing.JTextField prefDialogTitleField;
    private javax.swing.JDialog preferencesDialog;
    private javax.swing.JMenuItem preferencesMenuItem;
    private javax.swing.JScrollPane previewScrollPane;
    private javax.swing.JMenuItem printRoadMapMI;
    private javax.swing.JMenuItem redoMenuItem;
    private javax.swing.JScrollPane roadMapScrollPane;
    private javax.swing.JTextField roadMapTextEntry;
    private javax.swing.JMenuBar roadmapMenuBar;
    private javax.swing.JComboBox scaleComboBox;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JMenu sectionMenu;
    private javax.swing.JButton selectAllBricksButton;
    private javax.swing.JMenuItem selectAllMenuItem;
    private javax.swing.JButton stopButton;
    private javax.swing.JMenuItem togglePhraseMenuItem;
    private javax.swing.JMenuItem toggleSectionMenuItem;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenuItem transposeDownMenuItem;
    private javax.swing.JMenu transposeMenu;
    private javax.swing.JMenuItem transposeUpMenuItem;
    private javax.swing.JMenuItem undoMenuItem;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

    /** Appends the currently-selected blocks to a Notate window called auxNotate,
     * creating that window if none exists.
     *
     * If no blocks are selected, selects them all first.
     *
     * If the road map is empty, does nothing.
     */ 
    public void appendSelectionToNotate()
    {
        if( roadMapPanel.getNumBlocks() < 1 )
            return;

        if( !roadMapPanel.hasSelection() )
            selectAllBricks();

        if( auxNotate == null ) {
            ChordPart chordPart = new ChordPart();
            chordPart.addFromRoadMapFrame(this);
            Score score = new Score(chordPart);
            score.setMetre(getMetre());
            auxNotate = notate.newNotateWithScore(score, getNewXlocation(), getNewYlocation());
        } else
            auxNotate.addToChordPartFromRoadMapFrame(this);

        auxNotate.setCreateRoadMapCheckBox(false);
        auxNotate.setVisible(true);
    }


    /** Sends the currently-selected blocks to a new Notate window called auxNotate.
     * Any existing associate with that window is detached and lost.
     *
     * If no blocks are selected, selects them all first.
     *
     * If the road map is empty, does nothing.
     */
    public void sendSelectionToNewNotate()
    {
        if( roadMapPanel.getNumBlocks() < 1 )
            return;

        if( !roadMapPanel.hasSelection() )
            selectAllBricks();

        if( auxNotate != null ) {
            // TODO What to do here?
            // Need to prevent inconsistency caused by the closing of auxNotate.
            // It will set auxNotate to null.
        }
        ChordPart chordPart = new ChordPart();
        chordPart.addFromRoadMapFrame(this);
        Score score = new Score(chordPart);
        System.out.println(score.getChordProg().getSectionInfo());
        score.setMetre(getMetre());
        //score.setStyle(style.getName());
        score.setTempo(tempo);
        score.setTitle(roadMapTitle);
        auxNotate = notate.newNotateWithScore(score, getNewXlocation(), getNewYlocation());
        auxNotate.setCreateRoadMapCheckBox(false);
        System.out.println(auxNotate.getSectionInfo());
        auxNotate.setVisible(true);
      }


    /** Call from auxNotate when deleted to prevent dangling reference. */
    public void resetAuxNotate()
    {
        auxNotate = null;
    }

    /** Plays the currently-selected blocks. The style is determined from the
     * Notate window where this roadmap was opened.
     *
     * If not blocks are selected, selects them all first.
     *
     * If the road map is empty, does nothing.
     */
    public void playSelection() {
        if (roadMapPanel.getNumBlocks() < 1)
            return;
        
        if (!roadMapPanel.hasSelection())
            selectAllBricks();

        ChordPart chordPart = new ChordPart();
        chordPart.addFromRoadMapFrame(this);
        Score score = new Score(chordPart);
        score.setMetre(getMetre());
        score.setTempo(tempo);
         
        setPlaying(MidiPlayListener.Status.PLAYING, 0);
         
        if( loopToggleButton.isSelected() )
            notate.playAscore(score, style.getName(), -1);
        else
            notate.playAscore(score, style.getName(), 0);
    }

    /** Stops playback */
    public void stopPlayingSelection()
    {
        if(isPlaying()) {
            notate.stopPlayAscore();
            setPlaying(MidiPlayListener.Status.STOPPED, 0);
        }
    }
    
    /** Stops then restarts playback */
    public void restartPlayingSelection()
    {
        stopPlayingSelection();
        playSelection();
    }

    /** Set the playback status */
    public void setPlaying(MidiPlayListener.Status playing, int transposition)
    {
        isPlaying = playing;
        if(isPlaying()) {
            roadMapPanel.setPlayLineOffset();
            roadMapPanel.setPlaySection();
            playTimer.start();
        } else {
            playTimer.stop();
            roadMapPanel.draw();
        }
    }
    
    /** Sets the playback status */
    public void setPlaying(boolean status)
    {
        if(status)
            setPlaying(MidiPlayListener.Status.PLAYING,0);
        else
            setPlaying(MidiPlayListener.Status.STOPPED,0);
    }
    
    /** Returns the playback status */
    public MidiPlayListener.Status getPlaying()
    {
        return isPlaying;
    }

    /** Returns whether payback is active */
    public boolean isPlaying()
    {
        return isPlaying == MidiPlayListener.Status.PLAYING;
    }
    

    /** Close this RoadMapFrame and clean up. */
    public void closeWindow()
    {
        brickLibraryFrame.setVisible(false); //TODO somehow make only one window
        if(isPlaying())
            stopPlayingSelection();
        WindowRegistry.unregisterWindow(this);

        if( notate != null )
            notate.disestablishRoadMapFrame();

        disposeBuffers();
        dispose();
    }


    /** Get X location for new frame cascaded from original. */
    public int getNewXlocation()
    {
        return (int)getLocation().getX() + WindowRegistry.defaultXnewWindowStagger;
    }


    /** Get Y location for new frame cascaded from original. */
    public int getNewYlocation()
    {
        return (int)getLocation().getY() + WindowRegistry.defaultYnewWindowStagger;
    }


    /**
     * Set the height of specified RoadMapFrame so that it fills the screen.
     * This seems to work fine when the dock is at the right, but when
     * it is at the bottom, for some reason vertical staggering does not happen.
     * @param notate
     */
    public void setRoadMapFrameHeight()
    {
        int desiredWidth = RMframeWidth; // alternatively: dm.getWidth() - x
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gs = ge.getScreenDevices(); // Get size of each screen
        DisplayMode dm = gs[0].getDisplayMode();
        int x = notate.getNewXlocation();
        int y = notate.getNewYlocation();
        setLocation(x, y);
        setSize(desiredWidth, dm.getHeight() - y);
    }


    /** Make this RoadMapFrame visible */
    public void makeVisible()
    {
        setVisible(true);
        if( brickLibraryMenuItem.isSelected() )
          {
          brickLibraryFrame.setVisible(true);
          }
    }

    /** Sets the time signature of the roadmap for Americans
     * @param meter
     */
    public void setMeter(int meter[])
    {
        setMetre(meter);
    }

    /** Sets the time signature of the roadmap for the rest of the world
     * @param metre 
     */
    public void setMetre(int metre[])
    {
        this.metre[0] = metre[0];
        this.metre[1] = metre[1];
        settings.setMetre(metre);
    }

    /** Returns the time signature of the roadmap for Americans
     * @return 
     */
    public int[] getMeter()
    {
        return metre;
    }

    /** Returns the time signature of the roadmap for the rest of the world
     * @return 
     */
    public int[] getMetre()
    {
        return metre;
    }

    /** Returns the tempo */
    public int getTempo()
    {
        return tempo;
    }

    /** Returns the style */ 
    public Style getStyle()
    {
        return style;
    }

    /** Gets the current playback slot from notate */
    public int getMidiSlot()
    {
        return notate.getMidiSlot();
    }

    /** Gets the roadmap's musical info from a score */
    public void setMusicalInfo(Score score)
    {
        setMetre(score.getMetre());
        tempo = (int)score.getTempo();
        style = score.getStyle();
    }

    /** Activate the preferences dialog and set the default values */
    private void activatePreferencesDialog()
    {
        prefDialogTitleField.setText(roadMapTitle);
        prefDialogTempoField.setText(String.valueOf(tempo));
        prefDialogMetreTopField.setText(String.valueOf(getMetre()[0]));
        prefDialogMetreBottomField.setText(String.valueOf(getMetre()[1]));
        prefDialogStyleComboBox.setSelectedItem(style);
        preferencesDialog.setVisible(true);
    }
    
    /** Activate the chord change dialog and set the default values*/
    private void activateChordDialog()
    {
        ChordBlock chord = (ChordBlock)roadMapPanel.getSelection().get(0);
        chordDialogNameField.setText(chord.getName());
        chordDialogDurationComboBox.setSelectedItem(chord.getDuration()/settings.slotsPerBeat);
        chordChangeDialog.setLocation(roadMapPanel.getLocationOnScreen());
        chordChangeDialog.setVisible(true);
    }

    /** Gets the info from the preferences dialog */
    private void setRoadMapInfo()
    {
        setRoadMapTitle(prefDialogTitleField.getText());
        int metreTop = prefDialogMetreTopField.getInt();
        int metreBottom = prefDialogMetreBottomField.getInt();
        setMetre(new int[]{metreTop, metreBottom});
        tempo = prefDialogTempoField.getInt();
        style = (Style)prefDialogStyleComboBox.getSelectedItem();
        settings.keysColored = prefDialogKeyColorCheckBox.isSelected();
        roadMapPanel.updateBricks();
    }
    
    /** Scales the roadmap display to the current window size */
    private void scaleToWindow()
    {
        int width = roadMapScrollPane.getWidth()-roadMapScrollPane.getVerticalScrollBar().getWidth()-5;
        featureWidthSlider.setValue((width - 2*settings.xOffset)/settings.barsPerLine);    
        setFeatureWidthLocked(true);
    }
    
    /** Lock the feature width to scale to the window */
    private void setFeatureWidthLocked(boolean value)
    {
        javax.swing.border.TitledBorder border = (javax.swing.border.TitledBorder)featureWidthSlider.getBorder();
        String title = "Feature Width";
        if(value)
            title += " "+featureWidthSuffix;
        border.setTitle(title);
    }
};
