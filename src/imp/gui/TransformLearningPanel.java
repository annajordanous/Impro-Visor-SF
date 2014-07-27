/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Part.PartIterator;
import imp.lickgen.LickGen;
import imp.lickgen.transformations.Evaluate;
import imp.lickgen.transformations.TransformLearning;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.Transformation;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import polya.*;

/**
 *
 * @author Alex Putman
 */
public class TransformLearningPanel extends javax.swing.JPanel {
    
    /**
     * The class that contains the methods used to learn and flatten
     */
    private TransformLearning transformLearning;
    
    private Notate notate;
    
    /**
     * The melody that is saved and is used to compare against when flattening,
     * subtracting and learning
     */
    private MelodyPart original;
    
    /**
     * The melody result after flattening that is used in subtracting and 
     * learning
     */
    private MelodyPart flattened;
    
    /**
     * The latest resolution used to flatten a melody 
     */
    private int resolution;
    
    /**
     * The transform created after learning
     */
    private Transform transform;
    
    /**
     * the TransformPanel in LickgenFrame that is to put the learned 
     * transform
     */
    TransformPanel transformPanel;
    
    /**
     * Creates new form TransformLearningPanel
     */
    public TransformLearningPanel(Notate notate, 
                                  TransformPanel subPanel) {
        initComponents();
        this.notate = notate;
        // the class that contains the actual flattening and learning methods
        transformLearning = new TransformLearning();
        original = new MelodyPart();
        flattened = new MelodyPart();
        resolution = 120;
        transform = new Transform();
        this.transformPanel = subPanel;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        step1Panel = new javax.swing.JPanel();
        saveOriginalButton = new javax.swing.JButton();
        replaceWithOriginalButton = new javax.swing.JButton();
        step1Label1 = new javax.swing.JLabel();
        step1Label3 = new javax.swing.JLabel();
        step1Label2 = new javax.swing.JLabel();
        step2Panel = new javax.swing.JPanel();
        step2Label1 = new javax.swing.JLabel();
        step2Label2 = new javax.swing.JLabel();
        step2Label3 = new javax.swing.JLabel();
        flattenValueComboBox = new javax.swing.JComboBox();
        flattenButton = new javax.swing.JButton();
        allowRepeatsCheckBox = new javax.swing.JCheckBox();
        jLabel2 = new javax.swing.JLabel();
        step3Panel = new javax.swing.JPanel();
        subFlatFromOrigButton = new javax.swing.JButton();
        createTransformButton = new javax.swing.JButton();
        step3Label1 = new javax.swing.JLabel();
        step3Label2 = new javax.swing.JLabel();
        step3Label3 = new javax.swing.JLabel();
        generateTransformMethodComboBox = new javax.swing.JComboBox();
        step4Panel = new javax.swing.JPanel();
        showTransformButton = new javax.swing.JButton();
        setTransformButton = new javax.swing.JButton();
        step4Label1 = new javax.swing.JLabel();
        step4Label2 = new javax.swing.JLabel();
        step4Label3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setAlignmentX(0.0F);
        setMinimumSize(new java.awt.Dimension(250, 25));
        setPreferredSize(new java.awt.Dimension(250, 25));
        setLayout(new java.awt.GridBagLayout());

        step1Panel.setBackground(new java.awt.Color(252, 91, 63));
        step1Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 1", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        step1Panel.setToolTipText("");
        step1Panel.setLayout(new java.awt.GridBagLayout());

        saveOriginalButton.setText("Save Original");
        saveOriginalButton.setToolTipText("Save the currently selected melody");
        saveOriginalButton.setMinimumSize(new java.awt.Dimension(300, 25));
        saveOriginalButton.setPreferredSize(new java.awt.Dimension(300, 25));
        saveOriginalButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                saveOriginalButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        step1Panel.add(saveOriginalButton, gridBagConstraints);

        replaceWithOriginalButton.setText("Replace Current Melody with Original");
        replaceWithOriginalButton.setToolTipText("replace the current melody with the saved melody");
        replaceWithOriginalButton.setEnabled(false);
        replaceWithOriginalButton.setMinimumSize(new java.awt.Dimension(300, 25));
        replaceWithOriginalButton.setPreferredSize(new java.awt.Dimension(300, 25));
        replaceWithOriginalButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                replaceWithOriginalButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        step1Panel.add(replaceWithOriginalButton, gridBagConstraints);

        step1Label1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step1Label1.setText("<html><b> Important: </b>First, save the selected melody you want to learn transformations from.");
        step1Label1.setMaximumSize(new java.awt.Dimension(485, 15));
        step1Label1.setMinimumSize(new java.awt.Dimension(485, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        step1Panel.add(step1Label1, gridBagConstraints);

        step1Label3.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step1Label3.setText("<html>You can also place your saved melody back into the leadsheet and start over.</html>  ");
        step1Label3.setMinimumSize(new java.awt.Dimension(448, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 0);
        step1Panel.add(step1Label3, gridBagConstraints);

        step1Label2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step1Label2.setText("If you do not save a melody, you will not be able to generate transformations.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        step1Panel.add(step1Label2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(step1Panel, gridBagConstraints);

        step2Panel.setBackground(new java.awt.Color(252, 176, 60));
        step2Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 2", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        step2Panel.setToolTipText("");
        step2Panel.setLayout(new java.awt.GridBagLayout());

        step2Label1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step2Label1.setText("Next, to make transformations you will need a basic outline from the original melody to transform from.");
        step2Label1.setMaximumSize(new java.awt.Dimension(672, 15));
        step2Label1.setMinimumSize(new java.awt.Dimension(672, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 2, 0);
        step2Panel.add(step2Label1, gridBagConstraints);

        step2Label2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step2Label2.setText("<html><b> Important: </b>Select a resolution, or minimum duration each note in the outline must satisfy, then click the Flatten button.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        step2Panel.add(step2Label2, gridBagConstraints);

        step2Label3.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step2Label3.setText("Flattening will happen on the currently selected melody, so an original melody is not required to be saved.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 6, 0);
        step2Panel.add(step2Label3, gridBagConstraints);

        flattenValueComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Whole Note", "Half Note", "Quarter Note", "Eight Note", "Sixteenth Note" }));
        flattenValueComboBox.setSelectedIndex(1);
        flattenValueComboBox.setToolTipText("select the resolution to flatten at");
        flattenValueComboBox.setMinimumSize(new java.awt.Dimension(145, 25));
        flattenValueComboBox.setPreferredSize(new java.awt.Dimension(145, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        step2Panel.add(flattenValueComboBox, gridBagConstraints);

        flattenButton.setText("Flatten");
        flattenButton.setToolTipText("flatten the currently selected melody with resolution chosen to the left");
        flattenButton.setMinimumSize(new java.awt.Dimension(145, 25));
        flattenButton.setPreferredSize(new java.awt.Dimension(145, 25));
        flattenButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                flattenButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        step2Panel.add(flattenButton, gridBagConstraints);

        allowRepeatsCheckBox.setBackground(new java.awt.Color(252, 176, 60));
        allowRepeatsCheckBox.setSelected(true);
        allowRepeatsCheckBox.setText("Allow Repeat Pitches");
        allowRepeatsCheckBox.setToolTipText("Whether ajacent notes with the same pitch in the flattened melody will be combined or not");
        allowRepeatsCheckBox.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                allowRepeatsCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.insets = new java.awt.Insets(6, 0, 0, 0);
        step2Panel.add(allowRepeatsCheckBox, gridBagConstraints);

        jLabel2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        jLabel2.setText("<html> <b>Advice:</b> It is recommended to select Allow Repeat Pitches when learning by windowning");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        step2Panel.add(jLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        add(step2Panel, gridBagConstraints);

        step3Panel.setBackground(new java.awt.Color(130, 217, 151));
        step3Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 3", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        step3Panel.setToolTipText("");
        step3Panel.setLayout(new java.awt.GridBagLayout());

        subFlatFromOrigButton.setText("Subtract Flattening from Original");
        subFlatFromOrigButton.setToolTipText("show just the transformations in the leadsheet");
        subFlatFromOrigButton.setEnabled(false);
        subFlatFromOrigButton.setMinimumSize(new java.awt.Dimension(300, 25));
        subFlatFromOrigButton.setPreferredSize(new java.awt.Dimension(300, 25));
        subFlatFromOrigButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                subFlatFromOrigButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        step3Panel.add(subFlatFromOrigButton, gridBagConstraints);

        createTransformButton.setText("Generate Transform");
        createTransformButton.setToolTipText("create a transform file based on transforming the flattened melody into the original melody");
        createTransformButton.setEnabled(false);
        createTransformButton.setMargin(new java.awt.Insets(2, 12, 2, 12));
        createTransformButton.setMaximumSize(new java.awt.Dimension(135, 25));
        createTransformButton.setMinimumSize(new java.awt.Dimension(145, 25));
        createTransformButton.setPreferredSize(new java.awt.Dimension(145, 25));
        createTransformButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                createTransformButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        step3Panel.add(createTransformButton, gridBagConstraints);

        step3Label1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step3Label1.setText("<html><b> Important: </b>To create the transformations, select a generation method and click the Generate Transform button.");
        step3Label1.setMaximumSize(new java.awt.Dimension(435, 15));
        step3Label1.setMinimumSize(new java.awt.Dimension(452, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        step3Panel.add(step3Label1, gridBagConstraints);

        step3Label2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step3Label2.setText("To just get just a visual representation of where transformations are happening, you can subtract.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 8, 0);
        step3Panel.add(step3Label2, gridBagConstraints);

        step3Label3.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step3Label3.setText("<html>Methods:<br>&nbsp;&nbsp;&nbsp;&nbsp;Windowing&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;- Learns transformations by just associating notes in a windowed section<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;(sized by the resolution used in flattening) with their flattened note.<br>&nbsp;&nbsp;&nbsp;&nbsp;Trend Detection&nbsp;-&nbsp;Detects groups of notes that follow a trend and generates transfromations<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;that builds the group of notes from important notes in the flattened outline.<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;CURRENTLY ONLY DETECTS CHROMATIC DIFFERENCE TRENDS\n");
        step3Label3.setMaximumSize(new java.awt.Dimension(2147483647, 85));
        step3Label3.setMinimumSize(new java.awt.Dimension(600, 85));
        step3Label3.setPreferredSize(new java.awt.Dimension(600, 85));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        step3Panel.add(step3Label3, gridBagConstraints);

        generateTransformMethodComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Windowing", "Trend Detection" }));
        generateTransformMethodComboBox.setToolTipText("select the method for learning");
        generateTransformMethodComboBox.setMinimumSize(new java.awt.Dimension(145, 25));
        generateTransformMethodComboBox.setPreferredSize(new java.awt.Dimension(145, 25));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        step3Panel.add(generateTransformMethodComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        add(step3Panel, gridBagConstraints);

        step4Panel.setBackground(new java.awt.Color(153, 255, 153));
        step4Panel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Step 4", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        step4Panel.setToolTipText("");
        step4Panel.setMinimumSize(new java.awt.Dimension(690, 154));
        step4Panel.setPreferredSize(new java.awt.Dimension(1111, 154));
        step4Panel.setLayout(new java.awt.GridBagLayout());

        showTransformButton.setText("Show Generated Transform");
        showTransformButton.setToolTipText("show the transform file generated from above");
        showTransformButton.setEnabled(false);
        showTransformButton.setMinimumSize(new java.awt.Dimension(300, 25));
        showTransformButton.setPreferredSize(new java.awt.Dimension(300, 25));
        showTransformButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                showTransformButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        step4Panel.add(showTransformButton, gridBagConstraints);

        setTransformButton.setText("Put Transform into Transform tab");
        setTransformButton.setToolTipText("put the transform generated above into the transform tab");
        setTransformButton.setEnabled(false);
        setTransformButton.setMinimumSize(new java.awt.Dimension(300, 25));
        setTransformButton.setPreferredSize(new java.awt.Dimension(300, 25));
        setTransformButton.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                setTransformButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        step4Panel.add(setTransformButton, gridBagConstraints);

        step4Label1.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step4Label1.setText("Open a window with the generated Transform in grammar form.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 8, 0);
        step4Panel.add(step4Label1, gridBagConstraints);

        step4Label2.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step4Label2.setText("<html><b> Important: </b>To further use the transformations, go to the Transform Tab and click Apply.");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
        step4Panel.add(step4Label2, gridBagConstraints);

        step4Label3.setFont(new java.awt.Font("Lucida Grande", 0, 12)); // NOI18N
        step4Label3.setText("<html><b> Important: </b>To use the generated Transform, click this button to make it the opened transform in the Tranform Tab. Then go to the Transform tab and click Apply.");
        step4Label3.setMaximumSize(new java.awt.Dimension(652, 15));
        step4Label3.setMinimumSize(new java.awt.Dimension(678, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 8, 0);
        step4Panel.add(step4Label3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 1.0;
        add(step4Panel, gridBagConstraints);

        jLabel1.setText("Follow every step to learn a transform for a solo. The comments labeled important are the strictly required instructions to generate a learned transform.");
        jLabel1.setAlignmentY(0.0F);
        jLabel1.setMaximumSize(new java.awt.Dimension(1000, 15));
        jLabel1.setMinimumSize(new java.awt.Dimension(1000, 15));
        jLabel1.setPreferredSize(new java.awt.Dimension(1000, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 9, 4, 0);
        add(jLabel1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void saveOriginalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveOriginalButtonActionPerformed
        
        notate.adjustSelection();
        original = notate.getCurrentMelodyPart().copy();
        notate.repaint();
        
        replaceWithOriginalButton.setEnabled(true);
        subFlatFromOrigButton.setEnabled(true);
        createTransformButton.setEnabled(true);
    }//GEN-LAST:event_saveOriginalButtonActionPerformed

    private void subFlatFromOrigButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_subFlatFromOrigButtonActionPerformed
        subtractOutline();
    }//GEN-LAST:event_subFlatFromOrigButtonActionPerformed

    private void showTransformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_showTransformButtonActionPerformed
        showTransform();
    }//GEN-LAST:event_showTransformButtonActionPerformed

    private void setTransformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setTransformButtonActionPerformed
        transformPanel.setTransform(transform);
    }//GEN-LAST:event_setTransformButtonActionPerformed

    private void replaceWithOriginalButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_replaceWithOriginalButtonActionPerformed
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop  = notate.getCurrentSelectionEnd();
        MelodyPart replace = original.extract(start, stop, true, true);
        notate.getCurrentMelodyPart().pasteOver(replace, start);
        notate.repaint();
    }//GEN-LAST:event_replaceWithOriginalButtonActionPerformed

    private void flattenButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_flattenButtonActionPerformed
        String flattenValue = (String)flattenValueComboBox.getSelectedItem();
        if(flattenValue.equals("Whole Note"))
        {
            flatten(480);
        }
        else if(flattenValue.equals("Half Note"))
        {
            flatten(240);
        }
        else if(flattenValue.equals("Quarter Note"))
        {
            flatten(120);
        }
        else if(flattenValue.equals("Eight Note"))
        {
            flatten(60);
        }
        else if(flattenValue.equals("Sixteenth Note"))
        {
            flatten(30);
        }
    }//GEN-LAST:event_flattenButtonActionPerformed

    private void createTransformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createTransformButtonActionPerformed
        
        String method = 
                generateTransformMethodComboBox.getSelectedItem().toString();
        if(method.equals("Windowing"))
        {
            transform = learnByWindowing();
            transform.hasChanged = true;
            showTransformButton.setEnabled(true);
            setTransformButton.setEnabled(true);
        }
        else if(method.equals("Trend Detection"))
        {
            transform = learnByTrendDetection();
            transform.hasChanged = true;
            showTransformButton.setEnabled(true);
            setTransformButton.setEnabled(true);
        }
    }//GEN-LAST:event_createTransformButtonActionPerformed

    private void allowRepeatsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_allowRepeatsCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_allowRepeatsCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox allowRepeatsCheckBox;
    private javax.swing.JButton createTransformButton;
    private javax.swing.JButton flattenButton;
    private javax.swing.JComboBox flattenValueComboBox;
    private javax.swing.JComboBox generateTransformMethodComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton replaceWithOriginalButton;
    private javax.swing.JButton saveOriginalButton;
    private javax.swing.JButton setTransformButton;
    private javax.swing.JButton showTransformButton;
    private javax.swing.JLabel step1Label1;
    private javax.swing.JLabel step1Label2;
    private javax.swing.JLabel step1Label3;
    private javax.swing.JPanel step1Panel;
    private javax.swing.JLabel step2Label1;
    private javax.swing.JLabel step2Label2;
    private javax.swing.JLabel step2Label3;
    private javax.swing.JPanel step2Panel;
    private javax.swing.JLabel step3Label1;
    private javax.swing.JLabel step3Label2;
    private javax.swing.JLabel step3Label3;
    private javax.swing.JPanel step3Panel;
    private javax.swing.JLabel step4Label1;
    private javax.swing.JLabel step4Label2;
    private javax.swing.JLabel step4Label3;
    private javax.swing.JPanel step4Panel;
    private javax.swing.JButton subFlatFromOrigButton;
    // End of variables declaration//GEN-END:variables

    /**
     * Opens the generated transform in a new TextArea Dialogue
     */
    public void showTransform()
    {
        TransformDialogue transEditor = 
                new TransformDialogue(notate.lickgenFrame, transform);
        
        transEditor.setLocationRelativeTo(this);
        transEditor.toFront();
    }
    
    /**
     * Sets the desired resolution and calls one of the flattening methods
     */
    private void flatten(int resolution)
    {
        this.resolution = resolution;
        boolean allowRepeats = allowRepeatsCheckBox.isSelected();
        flattenByResolution(resolution, !allowRepeats);
    }
    
    /**
     * Divides the melody into sections of length determined by the length of
     * the chords in the melody, it then proceeds to flatten by resolution.
     */
    private void flattenByChord(int resolution, boolean concatRepeats)
    {
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        MelodyPart melody = notate.getCurrentMelodyPart();
        ChordPart chords = notate.getChordProg();
        MelodyPart flattenedPart = 
                transformLearning.flattenByChord(melody, 
                                                 chords, 
                                                 resolution, 
                                                 start, 
                                                 stop, 
                                                 concatRepeats);
        
        MelodyPart replace = flattenedPart.extract(start, stop, true, true);
        notate.getCurrentMelodyPart().pasteOver(replace, start);
        notate.repaint();
    }
    
    /**
     * Divides the melody into segments of length resolution, gets the best note
     * in each segment and set every note in the segment to that note.
     */
    private void flattenByResolution(int resolution, boolean concatRepeats)
    {
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        MelodyPart melody = notate.getCurrentMelodyPart();
        ChordPart chords = notate.getChordProg();
        MelodyPart flattenedPart = 
                transformLearning.flattenByResolution(melody, 
                                                      chords,
                                                      resolution, 
                                                      start,
                                                      stop,
                                                      concatRepeats);
        
        MelodyPart replace = flattenedPart.extract(start, stop, true, true);
        notate.getCurrentMelodyPart().pasteOver(replace, start);
        notate.repaint();
    }
    
    /**
     * Learns transformations by taking each original melody segment of length
     * resolution and builds a transformation that can make them from their 
     * associated flattened note. 
     */
    private Transform learnByWindowing()
    {
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        MelodyPart outline = notate.getCurrentMelodyPart();
        ChordPart chords = notate.getChordProg();
        Polylist transformList = 
                transformLearning.createBlockTransform(outline, 
                                                       original.copy(), 
                                                       chords, 
                                                       start, 
                                                       stop);
        
        return new Transform(transformList.toStringSansParens());
    }
    /**
     * Learns transformations by going through the notes in a melody
     * sequentially and detecting sections that contain a trend. Then a
     * transformations is created by the definition of the trend detected.
     * TRENDS CURRENTLY BEING DETECTED: Diatonic Displacement
     */
    private Transform learnByTrendDetection()
    {
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        MelodyPart outline = notate.getCurrentMelodyPart();
        ChordPart chords = notate.getChordProg();
        Polylist transformList = 
                transformLearning.createTrendTransform(outline, 
                                                       original.copy(), 
                                                       chords, 
                                                       start, 
                                                       stop);
        
        return new Transform(transformList.toStringSansParens());
    }
    /**
     * Goes through each note in the original melody and if it equals the note 
     * at the same place in the flattened melody, turn it into a rest. This 
     * shows you the notes in the original melody that could be transformed
     * from the outline notes. 
     */
    private void subtractOutline()
    {
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        
        MelodyPart origSel = original.extract(start, stop, true, true);
        MelodyPart outlineSel = notate.getCurrentMelodyPart().extract(start, 
                                                                      stop, 
                                                                      true, 
                                                                      true);
        
        flattened = outlineSel.copy();
        MelodyPart subFromOrig = new MelodyPart();
        
        // First look at the index of each note in the original selection
        for(int i = 0; i < origSel.size(); i = origSel.getNextIndex(i))
        {
            Note origNote = origSel.getCurrentNote(i);
            Note outlineNote = outlineSel.getCurrentNote(i);
            // if the note is the same as the note in the outline location,
            // make it a rest
            if(origNote.samePitch(outlineNote))
            {
                origNote = Note.makeRest(origNote.getRhythmValue());
            }
            subFromOrig.addNote(origNote);
        }
        // Then make sure the new melody doesn't have notes that extend into
        // being the same pitch as the outline note in the same location
        for(int i = 0; i < outlineSel.size(); i = outlineSel.getNextIndex(i))
        {
            Note origNote = subFromOrig.getCurrentNote(i);
            Note outlineNote = outlineSel.getCurrentNote(i);
            // if the note is the same as the note in the outline location,
            // then cut off the duration of the note so it doesn't overlap
            // with the outlined note
            if(origNote.samePitch(outlineNote))
            {
                int diff = origNote.getRhythmValue() - outlineNote.getRhythmValue();
                origNote.setRhythmValue(diff);
                Note addRest = Note.makeRest(outlineNote.getRhythmValue());
                subFromOrig.setNote(i, addRest);
            }
            
        }
        notate.getCurrentMelodyPart().pasteOver(subFromOrig, start);
        notate.repaint();
    }
    
    /**
     * Shows the generated transform file in a new Dialogue. 
     */
    public class TransformDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transform trans;
    private javax.swing.JTextArea contents;
    
    public TransformDialogue(javax.swing.JFrame frame, Transform trans)
    {
        super(frame, "Transformation Editor", false);
        super.setSize(800,600);
        this.trans = trans;
        StringBuilder transFile = new StringBuilder();
        trans.toFile(transFile);
        contents = new javax.swing.JTextArea();
        contents.setFont(new Font("monospaced", Font.PLAIN, 14));
        contents.setTabSize(8);
        contents.setText(transFile.toString());
        super.setLocationRelativeTo(frame);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane();
        scroll.setPreferredSize(new Dimension(820,620));
        scroll.setViewportView(contents);
        getContentPane().add(scroll);
        
        javax.swing.JButton saveButton = new javax.swing.JButton("Close"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        dispose(); 
        setVisible(false); 
    }
}       
}