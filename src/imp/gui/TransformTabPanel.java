/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import static imp.gui.UnsavedChanges.Value.CANCEL;
import static imp.gui.UnsavedChanges.Value.NO;
import static imp.gui.UnsavedChanges.Value.YES;
import imp.lickgen.LickGen;
import imp.lickgen.transformations.Substitution;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.Transformation;
import imp.util.ErrorLog;
import imp.util.GrammarFilter;
import imp.util.Preferences;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.Scanner;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import polya.Polylist;

/**
 *
 * @author Alex Putman
 */
public class TransformTabPanel extends javax.swing.JPanel {

    /**
     * Creates new form SubstitutorTabPanel
     */
    public final String EXTENSION = ".transform";
    private LickGen lickgen;
    private Notate notate;
    private Transform transform;
    private int editRow;
    private JFileChooser chooser;
    private String filename;
    private Stack<MelodyInContext> savedMelodies;
    private Stack<MelodyInContext> savedTrans;
    
    public TransformTabPanel(LickGen lickgen, Notate notate) {
        this.lickgen = lickgen;
        this.notate = notate;
        initComponents();

        editRow = -1; 
        
        subJTable.setTableHeader(null);
        subJTable.setRowHeight(36);
        subJTable.setDefaultRenderer(Object.class, new SubstitutionCellRenderer());
        subJTable.setDefaultEditor(Object.class, new SubstitutionCellEditor());

        transJTable.setTableHeader(null);
        transJTable.setRowHeight(36);
        transJTable.setDefaultRenderer(Object.class, new TransformationCellRenderer());
        transJTable.setDefaultEditor(Object.class, new TransformationCellEditor());
        
        chooser = new JFileChooser(){
            @Override
            public void approveSelection(){
                File f = getSelectedFile();
                if(!f.getAbsolutePath().endsWith(EXTENSION))
                    f = new File(f.getAbsolutePath()+EXTENSION);
                if(f.exists() && getDialogType() == SAVE_DIALOG){
                    int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
                    switch(result){
                        case JOptionPane.YES_OPTION:
                            super.approveSelection();
                            return;
                        case JOptionPane.NO_OPTION:
                            return;
                        case JOptionPane.CLOSED_OPTION:
                            return;
                        case JOptionPane.CANCEL_OPTION:
                            cancelSelection();
                            return;
                    }
                }
                super.approveSelection();
            }        
        };
        
        chooser.setCurrentDirectory(ImproVisor.getGrammarDirectory());
        
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Transform Files","transform");
        chooser.setFileFilter(filter);
        
        savedMelodies = new Stack();
        savedTrans = new Stack();
        
        setDefaultTrans();
    }
    
    private void setDefaultTrans()
    {
        filename = Preferences.DVF_TRANSFORM_VAL;
        String transformStr = "";
        try {
            transformStr = new Scanner(ImproVisor.getTransformFile()).useDelimiter("\\Z").next();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TransformTabPanel.class.getName()).log(Level.WARNING, null, ex);
        }
        if(transformStr.length() > 0)
        {
            transform = new Transform(lickgen, transformStr);
        }
        else
        {
            transform = new Transform(lickgen);
        }
        
        fillSubstitutionsList();
        fillTransformationsList();
        applySubstitutionsButton.setEnabled(true);
        saveSubstitutionsButton.setEnabled(true);
        cleanTransformButton.setEnabled(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        selectSubstitutionsButtonsPanel = new javax.swing.JPanel();
        createNewSubstitutionsFileButton = new javax.swing.JButton();
        openSubstitutionsFileButton = new javax.swing.JButton();
        saveSubstitutionsButton = new javax.swing.JButton();
        SubstitutorParametersPanel = new javax.swing.JPanel();
        substitutorRectifyCheckBox = new javax.swing.JCheckBox();
        useSubstitutionsButtonsPanel = new javax.swing.JPanel();
        applySubstitutionsButton = new javax.swing.JButton();
        revertSubstitutionsButton = new javax.swing.JButton();
        reapplySubstitutionsButton = new javax.swing.JButton();
        cleanTransformButton = new javax.swing.JButton();
        substitutionsPanel = new javax.swing.JPanel();
        addSubsFromOtherFileButton = new javax.swing.JButton();
        createNewSubstitutionButton = new javax.swing.JButton();
        editSubstitutionNameButton = new javax.swing.JButton();
        deleteSubstitutionButton = new javax.swing.JButton();
        substitutionFromLabel = new javax.swing.JLabel();
        subsScrollPane = new javax.swing.JScrollPane();
        subJTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        motifTotalLabel = new javax.swing.JLabel();
        scaleMotifWeightsButton = new javax.swing.JButton();
        motifTotalWeightValueLabel = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        scaleEmbWeightsButton = new javax.swing.JButton();
        embTotalLabel = new javax.swing.JLabel();
        embTotalWeightValueLabel = new javax.swing.JLabel();
        transformationsPanel = new javax.swing.JPanel();
        transformationSubstitutionNameLabel = new javax.swing.JLabel();
        createNewTransformationButton = new javax.swing.JButton();
        editSelectedTransformationButton = new javax.swing.JButton();
        deleteTransformationButton = new javax.swing.JButton();
        transScrollPane = new javax.swing.JScrollPane();
        transJTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        scaleTransWeightsButton = new javax.swing.JButton();
        transTotalWeightValueLabel = new javax.swing.JLabel();
        transTotalLabel = new javax.swing.JLabel();
        playbackPanel = new javax.swing.JPanel();
        substitutorPlayLeadsheetButton = new javax.swing.JButton();
        substitutorStopLeadsheetButton = new javax.swing.JButton();
        substitutorSaveLeadsheetButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        selectSubstitutionsButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Substitution List", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        selectSubstitutionsButtonsPanel.setMinimumSize(new java.awt.Dimension(230, 130));
        selectSubstitutionsButtonsPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        selectSubstitutionsButtonsPanel.setLayout(new java.awt.GridBagLayout());

        createNewSubstitutionsFileButton.setText("Create New Transform File");
        createNewSubstitutionsFileButton.setToolTipText("Create a new empty transform with no subsitutions");
        createNewSubstitutionsFileButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        createNewSubstitutionsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewSubstitutionsFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        selectSubstitutionsButtonsPanel.add(createNewSubstitutionsFileButton, gridBagConstraints);

        openSubstitutionsFileButton.setText("Open Transform File");
        openSubstitutionsFileButton.setToolTipText("Open transform file from grammars folder");
        openSubstitutionsFileButton.setAutoscrolls(true);
        openSubstitutionsFileButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        openSubstitutionsFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openSubstitutionsFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        selectSubstitutionsButtonsPanel.add(openSubstitutionsFileButton, gridBagConstraints);

        saveSubstitutionsButton.setText("Save Current Transform");
        saveSubstitutionsButton.setToolTipText("Save the substitutions below into a transform file");
        saveSubstitutionsButton.setEnabled(false);
        saveSubstitutionsButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        saveSubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveSubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        selectSubstitutionsButtonsPanel.add(saveSubstitutionsButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(selectSubstitutionsButtonsPanel, gridBagConstraints);

        SubstitutorParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Substitution Parameters", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        SubstitutorParametersPanel.setMinimumSize(new java.awt.Dimension(180, 60));
        SubstitutorParametersPanel.setPreferredSize(new java.awt.Dimension(180, 60));
        SubstitutorParametersPanel.setLayout(new java.awt.GridBagLayout());

        substitutorRectifyCheckBox.setSelected(true);
        substitutorRectifyCheckBox.setText("Rectify");
        substitutorRectifyCheckBox.setToolTipText("rectify selection after applying substitutions");
        substitutorRectifyCheckBox.setMaximumSize(new java.awt.Dimension(240, 23));
        substitutorRectifyCheckBox.setMinimumSize(new java.awt.Dimension(240, 23));
        substitutorRectifyCheckBox.setPreferredSize(new java.awt.Dimension(240, 23));
        substitutorRectifyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorRectifyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 4, 4);
        SubstitutorParametersPanel.add(substitutorRectifyCheckBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(SubstitutorParametersPanel, gridBagConstraints);

        useSubstitutionsButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Use Substitutions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        useSubstitutionsButtonsPanel.setMinimumSize(new java.awt.Dimension(230, 130));
        useSubstitutionsButtonsPanel.setPreferredSize(new java.awt.Dimension(230, 130));
        useSubstitutionsButtonsPanel.setLayout(new java.awt.GridBagLayout());

        applySubstitutionsButton.setText("Apply Substitutions to Melody");
        applySubstitutionsButton.setToolTipText("Apply the below substitutions to the selected melody");
        applySubstitutionsButton.setEnabled(false);
        applySubstitutionsButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        applySubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applySubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        useSubstitutionsButtonsPanel.add(applySubstitutionsButton, gridBagConstraints);

        revertSubstitutionsButton.setText("Revert Substitutions ");
        revertSubstitutionsButton.setToolTipText("Undo Apply Substitutions");
        revertSubstitutionsButton.setEnabled(false);
        revertSubstitutionsButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        revertSubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                revertSubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        useSubstitutionsButtonsPanel.add(revertSubstitutionsButton, gridBagConstraints);

        reapplySubstitutionsButton.setText("Re-Apply");
        reapplySubstitutionsButton.setToolTipText("Undo Revert Substitutions");
        reapplySubstitutionsButton.setEnabled(false);
        reapplySubstitutionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reapplySubstitutionsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        useSubstitutionsButtonsPanel.add(reapplySubstitutionsButton, gridBagConstraints);

        cleanTransformButton.setText("Clean Transform File");
        cleanTransformButton.setToolTipText("remove duplicate substitutions but add their weights together");
        cleanTransformButton.setEnabled(false);
        cleanTransformButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        cleanTransformButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanTransformButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        useSubstitutionsButtonsPanel.add(cleanTransformButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(useSubstitutionsButtonsPanel, gridBagConstraints);

        substitutionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Substitutions", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        substitutionsPanel.setMinimumSize(new java.awt.Dimension(490, 500));
        substitutionsPanel.setPreferredSize(new java.awt.Dimension(490, 500));
        substitutionsPanel.setLayout(new java.awt.GridBagLayout());

        addSubsFromOtherFileButton.setText("Add Subs From Other File");
        addSubsFromOtherFileButton.setToolTipText("Add all the substitutions in another transform file to the current transform");
        addSubsFromOtherFileButton.setEnabled(false);
        addSubsFromOtherFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addSubsFromOtherFileButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 7);
        substitutionsPanel.add(addSubsFromOtherFileButton, gridBagConstraints);

        createNewSubstitutionButton.setText("Create New Substitution");
        createNewSubstitutionButton.setToolTipText("Create a new substitution with no transformations that will be added to the currrent transform");
        createNewSubstitutionButton.setEnabled(false);
        createNewSubstitutionButton.setMargin(new java.awt.Insets(2, 5, 2, 5));
        createNewSubstitutionButton.setMaximumSize(new java.awt.Dimension(164, 26));
        createNewSubstitutionButton.setMinimumSize(new java.awt.Dimension(164, 26));
        createNewSubstitutionButton.setPreferredSize(new java.awt.Dimension(164, 26));
        createNewSubstitutionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewSubstitutionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
        substitutionsPanel.add(createNewSubstitutionButton, gridBagConstraints);

        editSubstitutionNameButton.setText("Edit Substitution Name");
        editSubstitutionNameButton.setToolTipText("Edit the name of a substitution");
        editSubstitutionNameButton.setEnabled(false);
        editSubstitutionNameButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        editSubstitutionNameButton.setMaximumSize(new java.awt.Dimension(158, 26));
        editSubstitutionNameButton.setMinimumSize(new java.awt.Dimension(158, 26));
        editSubstitutionNameButton.setPreferredSize(new java.awt.Dimension(158, 26));
        editSubstitutionNameButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSubstitutionNameButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        substitutionsPanel.add(editSubstitutionNameButton, gridBagConstraints);

        deleteSubstitutionButton.setText("Delete Substitution");
        deleteSubstitutionButton.setToolTipText("Delete the selected substitution from the transform");
        deleteSubstitutionButton.setEnabled(false);
        deleteSubstitutionButton.setMargin(new java.awt.Insets(2, 4, 2, 4));
        deleteSubstitutionButton.setMaximumSize(new java.awt.Dimension(115, 26));
        deleteSubstitutionButton.setMinimumSize(new java.awt.Dimension(115, 26));
        deleteSubstitutionButton.setPreferredSize(new java.awt.Dimension(115, 26));
        deleteSubstitutionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSubstitutionButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 7);
        substitutionsPanel.add(deleteSubstitutionButton, gridBagConstraints);

        substitutionFromLabel.setText("Substitutions From: ");
        substitutionFromLabel.setMaximumSize(new java.awt.Dimension(10000, 20));
        substitutionFromLabel.setMinimumSize(new java.awt.Dimension(300, 20));
        substitutionFromLabel.setPreferredSize(new java.awt.Dimension(300, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 6, 0, 0);
        substitutionsPanel.add(substitutionFromLabel, gridBagConstraints);

        subsScrollPane.setMaximumSize(new java.awt.Dimension(465, 32767));
        subsScrollPane.setMinimumSize(new java.awt.Dimension(465, 402));
        subsScrollPane.setPreferredSize(new java.awt.Dimension(465, 402));

        subJTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        subJTable.setModel(new javax.swing.table.AbstractTableModel() {

            Object[] subs = new Object[0];
            public Class getColumnClass(int columnIndex)
            {
                return Substitution.class;
            }
            public int getRowCount()
            {
                return subs.length;
            }
            public int getColumnCount()
            {
                return 1;
            }
            public Substitution getValueAt(int rowIndex, int columnIndex) {
                return (Substitution)subs[columnIndex];
            }
        });
        subJTable.setMaximumSize(new java.awt.Dimension(100000, 1000000));
        subJTable.setMinimumSize(new java.awt.Dimension(360, 450));
        subJTable.getTableHeader().setReorderingAllowed(false);
        subJTable.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                subJTableFocusGained(evt);
            }
        });
        subsScrollPane.setViewportView(subJTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 6, 0, 6);
        substitutionsPanel.add(subsScrollPane, gridBagConstraints);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Motif Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jPanel1.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanel1.setLayout(new java.awt.GridBagLayout());

        motifTotalLabel.setText("Total: ");
        motifTotalLabel.setMaximumSize(new java.awt.Dimension(35, 14));
        motifTotalLabel.setMinimumSize(new java.awt.Dimension(35, 20));
        motifTotalLabel.setPreferredSize(new java.awt.Dimension(35, 20));
        jPanel1.add(motifTotalLabel, new java.awt.GridBagConstraints());

        scaleMotifWeightsButton.setText("Scale All");
        scaleMotifWeightsButton.setToolTipText("scale all the weights of substitutions currently labeled as motifs");
        scaleMotifWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleMotifWeightsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel1.add(scaleMotifWeightsButton, gridBagConstraints);

        motifTotalWeightValueLabel.setMinimumSize(new java.awt.Dimension(70, 20));
        motifTotalWeightValueLabel.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(motifTotalWeightValueLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        substitutionsPanel.add(jPanel1, gridBagConstraints);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Embellishment Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jPanel2.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanel2.setLayout(new java.awt.GridBagLayout());

        scaleEmbWeightsButton.setText("Scale All");
        scaleEmbWeightsButton.setToolTipText("scale all the weights of substitutions currently labeled as embellishments");
        scaleEmbWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleEmbWeightsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel2.add(scaleEmbWeightsButton, gridBagConstraints);

        embTotalLabel.setText("Total: ");
        embTotalLabel.setMaximumSize(new java.awt.Dimension(35, 14));
        embTotalLabel.setMinimumSize(new java.awt.Dimension(35, 20));
        embTotalLabel.setPreferredSize(new java.awt.Dimension(35, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel2.add(embTotalLabel, gridBagConstraints);

        embTotalWeightValueLabel.setMinimumSize(new java.awt.Dimension(70, 20));
        embTotalWeightValueLabel.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel2.add(embTotalWeightValueLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        substitutionsPanel.add(jPanel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(substitutionsPanel, gridBagConstraints);

        transformationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transformations", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        transformationsPanel.setMinimumSize(new java.awt.Dimension(490, 500));
        transformationsPanel.setPreferredSize(new java.awt.Dimension(490, 500));
        transformationsPanel.setLayout(new java.awt.GridBagLayout());

        transformationSubstitutionNameLabel.setText("For Substitution:");
        transformationSubstitutionNameLabel.setMaximumSize(new java.awt.Dimension(400, 20));
        transformationSubstitutionNameLabel.setMinimumSize(new java.awt.Dimension(400, 20));
        transformationSubstitutionNameLabel.setPreferredSize(new java.awt.Dimension(400, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(3, 6, 3, 2);
        transformationsPanel.add(transformationSubstitutionNameLabel, gridBagConstraints);

        createNewTransformationButton.setText("Create New Transformation");
        createNewTransformationButton.setToolTipText("Create a new empty transform that will be added to the selected substitution");
        createNewTransformationButton.setEnabled(false);
        createNewTransformationButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        createNewTransformationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createNewTransformationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 6, 0, 0);
        transformationsPanel.add(createNewTransformationButton, gridBagConstraints);

        editSelectedTransformationButton.setText("Edit Transformation");
        editSelectedTransformationButton.setToolTipText("Edit the grammar for the selected transform");
        editSelectedTransformationButton.setEnabled(false);
        editSelectedTransformationButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        editSelectedTransformationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editSelectedTransformationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 4, 0, 4);
        transformationsPanel.add(editSelectedTransformationButton, gridBagConstraints);

        deleteTransformationButton.setText("Delete Transformation");
        deleteTransformationButton.setToolTipText("Delete the selected transform from the selected substitution");
        deleteTransformationButton.setEnabled(false);
        deleteTransformationButton.setMargin(new java.awt.Insets(2, 7, 2, 7));
        deleteTransformationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteTransformationButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 0, 7);
        transformationsPanel.add(deleteTransformationButton, gridBagConstraints);

        transScrollPane.setMaximumSize(new java.awt.Dimension(465, 32767));
        transScrollPane.setMinimumSize(new java.awt.Dimension(465, 402));
        transScrollPane.setPreferredSize(new java.awt.Dimension(465, 402));

        transJTable.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        transJTable.setModel(new javax.swing.table.AbstractTableModel() {

            Object[] subs = new Object[0];
            public Class getColumnClass(int columnIndex)
            {
                return Transformation.class;
            }
            public int getRowCount()
            {
                return subs.length;
            }
            public int getColumnCount()
            {
                return 1;
            }
            public Object getValueAt(int rowIndex, int columnIndex) {
                return subs[columnIndex];
            }
        });
        transJTable.setMaximumSize(new java.awt.Dimension(10000, 100000));
        transJTable.setMinimumSize(new java.awt.Dimension(360, 450));
        transScrollPane.setViewportView(transJTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(8, 6, 0, 6);
        transformationsPanel.add(transScrollPane, gridBagConstraints);

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Transformation Weights", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 12))); // NOI18N
        jPanel3.setMinimumSize(new java.awt.Dimension(200, 50));
        jPanel3.setLayout(new java.awt.GridBagLayout());

        scaleTransWeightsButton.setText("Scale All");
        scaleTransWeightsButton.setToolTipText("scale all the weights of transformations in the currently selected substitution");
        scaleTransWeightsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                scaleTransWeightsButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        jPanel3.add(scaleTransWeightsButton, gridBagConstraints);

        transTotalWeightValueLabel.setMinimumSize(new java.awt.Dimension(70, 20));
        transTotalWeightValueLabel.setPreferredSize(new java.awt.Dimension(70, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel3.add(transTotalWeightValueLabel, gridBagConstraints);

        transTotalLabel.setText("Total: ");
        transTotalLabel.setMaximumSize(new java.awt.Dimension(35, 14));
        transTotalLabel.setMinimumSize(new java.awt.Dimension(35, 20));
        transTotalLabel.setPreferredSize(new java.awt.Dimension(35, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        jPanel3.add(transTotalLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        transformationsPanel.add(jPanel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(transformationsPanel, gridBagConstraints);

        playbackPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "LeadSheet Options", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        playbackPanel.setMinimumSize(new java.awt.Dimension(187, 130));
        playbackPanel.setPreferredSize(new java.awt.Dimension(187, 130));
        playbackPanel.setLayout(new java.awt.GridBagLayout());

        substitutorPlayLeadsheetButton.setText("Play Selection");
        substitutorPlayLeadsheetButton.setToolTipText("Play the selected melody in the leadsheet");
        substitutorPlayLeadsheetButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        substitutorPlayLeadsheetButton.setMinimumSize(new java.awt.Dimension(153, 23));
        substitutorPlayLeadsheetButton.setPreferredSize(new java.awt.Dimension(153, 23));
        substitutorPlayLeadsheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorPlayLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        playbackPanel.add(substitutorPlayLeadsheetButton, gridBagConstraints);

        substitutorStopLeadsheetButton.setText("Stop Playback");
        substitutorStopLeadsheetButton.setToolTipText("Stop leadsheet playback");
        substitutorStopLeadsheetButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        substitutorStopLeadsheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorStopLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        playbackPanel.add(substitutorStopLeadsheetButton, gridBagConstraints);

        substitutorSaveLeadsheetButton.setText("Save");
        substitutorSaveLeadsheetButton.setToolTipText("Save current lick");
        substitutorSaveLeadsheetButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        substitutorSaveLeadsheetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substitutorSaveLeadsheetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        playbackPanel.add(substitutorSaveLeadsheetButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(playbackPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cleanTransformButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanTransformButtonActionPerformed
        transform.findDuplicatesAndAddToWeight();
        fillSubstitutionsList();
        fillTransformationsList();
    }//GEN-LAST:event_cleanTransformButtonActionPerformed

    private void openSubstitutionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSubstitutionsFileButtonActionPerformed
        
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            String newFilename = chooser.getSelectedFile().getName();
            String transformStr = "";
            try {
                transformStr = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TransformTabPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transformStr.length() > 0)
            {
                Transform newTrans = new Transform(lickgen, transformStr);
                changeTransform(newTrans, newFilename);
            }
        }
    }//GEN-LAST:event_openSubstitutionsFileButtonActionPerformed

    private void substitutorRectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorRectifyCheckBoxActionPerformed

    }//GEN-LAST:event_substitutorRectifyCheckBoxActionPerformed

    private void applySubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySubstitutionsButtonActionPerformed
        // TODO add your handling code here:
        applySubstitutions();
    }//GEN-LAST:event_applySubstitutionsButtonActionPerformed

    private void revertSubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertSubstitutionsButtonActionPerformed
        revertSubs();
    }//GEN-LAST:event_revertSubstitutionsButtonActionPerformed

    private void createNewSubstitutionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewSubstitutionButtonActionPerformed
        Substitution sub = new Substitution(lickgen);
        transform.substitutions.add(sub);
        fillSubstitutionsList();
        fillTransformationsList();
    }//GEN-LAST:event_createNewSubstitutionButtonActionPerformed

    private void editSubstitutionNameButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSubstitutionNameButtonActionPerformed
        if(editRow < 0)
        {
            int editIndex = subJTable.getEditingRow();
            if(editIndex >= 0)
            {
                editRow = editIndex;
                subJTable.setEditingRow(-1);
                fillSubstitutionsList();
                fillTransformationsList();
                editSubstitutionNameButton.setText("Save Substitution Name");
            }
        }
        else
        {

            resetEditNameButton();
            fillSubstitutionsList();
            fillTransformationsList();
        }

    }//GEN-LAST:event_editSubstitutionNameButtonActionPerformed

    private void deleteSubstitutionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSubstitutionButtonActionPerformed
        int editRow = subJTable.getEditingRow();
        if(editRow >= 0)
        {
            Object toDelete = subJTable.getValueAt(editRow, 0);
            transform.substitutions.remove((Substitution)toDelete);

            fillSubstitutionsList();
            fillTransformationsList();
        }
    }//GEN-LAST:event_deleteSubstitutionButtonActionPerformed

    private void subJTableFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_subJTableFocusGained
        // TODO add your handling code here:
    }//GEN-LAST:event_subJTableFocusGained

    private void createNewTransformationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewTransformationButtonActionPerformed
        
        Object toAddSub = subJTable.getValueAt(subJTable.getSelectedRow(), 0);
        if(toAddSub != null)
        {
            Substitution subToAddTo = (Substitution) toAddSub;
            subToAddTo.addNewTransformation();
        }
        fillTransformationsList();
    }//GEN-LAST:event_createNewTransformationButtonActionPerformed

    private void editSelectedTransformationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editSelectedTransformationButtonActionPerformed
        int editTransRow = transJTable.getEditingRow();
        if(editTransRow >= 0)
        {
            Transformation currentTrans = (Transformation)transJTable.getValueAt(editTransRow, 0);

            TransformationDialogue transEditor = new TransformationDialogue(notate.lickgenFrame, currentTrans);
            transEditor.setLocationRelativeTo(this);
            transEditor.toFront();
        }
    }//GEN-LAST:event_editSelectedTransformationButtonActionPerformed

    private void deleteTransformationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteTransformationButtonActionPerformed
        int editSubRow = subJTable.getEditingRow();
        if(editSubRow >= 0)
        {
            Object toDeleteSub = subJTable.getValueAt(editSubRow, 0);
            int editTransRow = transJTable.getEditingRow();
            if(editTransRow >= 0)
            {
                Object toDelete = transJTable.getValueAt(editTransRow, 0);
                ((Substitution)toDeleteSub).transformations.remove((Transformation)toDelete);

                fillTransformationsList();
            }

        }
    }//GEN-LAST:event_deleteTransformationButtonActionPerformed

    private void substitutorPlayLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorPlayLeadsheetButtonActionPerformed
        Stave stave = notate.getCurrentStave();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        notate.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "putLick " + start + " - " + stop);
        ImproVisor.setPlayEntrySounds(true);
    }//GEN-LAST:event_substitutorPlayLeadsheetButtonActionPerformed

    private void substitutorStopLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorStopLeadsheetButtonActionPerformed
        notate.stopPlaying();
    }//GEN-LAST:event_substitutorStopLeadsheetButtonActionPerformed

    private void substitutorSaveLeadsheetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorSaveLeadsheetButtonActionPerformed
        notate.setLickTitle("<Generated Lick>");

        notate.openSaveLickFrame();
    }//GEN-LAST:event_substitutorSaveLeadsheetButtonActionPerformed

    private void saveSubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveSubstitutionsButtonActionPerformed
        saveCurrentTransform();
    }//GEN-LAST:event_saveSubstitutionsButtonActionPerformed

    private void createNewSubstitutionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewSubstitutionsFileButtonActionPerformed
        Transform newTrans = new Transform(lickgen);
        String newFilename = "newTransformFile.transform";
        changeTransform(newTrans, newFilename);
    }//GEN-LAST:event_createNewSubstitutionsFileButtonActionPerformed

    private void addSubsFromOtherFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubsFromOtherFileButtonActionPerformed
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            String transformStr = "";
            try {
                transformStr = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(TransformTabPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transformStr.length() > 0)
            {
                Transform addTransform = new Transform(lickgen, transformStr);
                for(Substitution sub: addTransform.substitutions)
                {
                    transform.substitutions.add(sub);
                }
                transform.hasChanged = true;
                fillSubstitutionsList();
                fillTransformationsList();
            }
        }
    }//GEN-LAST:event_addSubsFromOtherFileButtonActionPerformed

    private void reapplySubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reapplySubstitutionsButtonActionPerformed
        revertSubs();
        applySubstitutions();
    }//GEN-LAST:event_reapplySubstitutionsButtonActionPerformed

    private void scaleMotifWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleMotifWeightsButtonActionPerformed
        ScaleMotifWeightsDialogue scale = new ScaleMotifWeightsDialogue(notate.lickgenFrame, transform);
    }//GEN-LAST:event_scaleMotifWeightsButtonActionPerformed

    private void scaleEmbWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleEmbWeightsButtonActionPerformed
        ScaleEmbWeightsDialogue scale = new ScaleEmbWeightsDialogue(notate.lickgenFrame, transform);
    }//GEN-LAST:event_scaleEmbWeightsButtonActionPerformed

    private void scaleTransWeightsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_scaleTransWeightsButtonActionPerformed
        int selectedSubRow = subJTable.getEditingRow();
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution) subJTable.getValueAt(selectedSubRow, 0);
            ScaleTransWeightsDialogue scale = new ScaleTransWeightsDialogue(notate.lickgenFrame, selectedSub);
        }
    }//GEN-LAST:event_scaleTransWeightsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SubstitutorParametersPanel;
    private javax.swing.JButton addSubsFromOtherFileButton;
    private javax.swing.JButton applySubstitutionsButton;
    private javax.swing.JButton cleanTransformButton;
    private javax.swing.JButton createNewSubstitutionButton;
    private javax.swing.JButton createNewSubstitutionsFileButton;
    private javax.swing.JButton createNewTransformationButton;
    private javax.swing.JButton deleteSubstitutionButton;
    private javax.swing.JButton deleteTransformationButton;
    private javax.swing.JButton editSelectedTransformationButton;
    private javax.swing.JButton editSubstitutionNameButton;
    private javax.swing.JLabel embTotalLabel;
    private javax.swing.JLabel embTotalWeightValueLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JLabel motifTotalLabel;
    private javax.swing.JLabel motifTotalWeightValueLabel;
    private javax.swing.JButton openSubstitutionsFileButton;
    private javax.swing.JPanel playbackPanel;
    private javax.swing.JButton reapplySubstitutionsButton;
    private javax.swing.JButton revertSubstitutionsButton;
    private javax.swing.JButton saveSubstitutionsButton;
    private javax.swing.JButton scaleEmbWeightsButton;
    private javax.swing.JButton scaleMotifWeightsButton;
    private javax.swing.JButton scaleTransWeightsButton;
    private javax.swing.JPanel selectSubstitutionsButtonsPanel;
    private javax.swing.JTable subJTable;
    private javax.swing.JScrollPane subsScrollPane;
    private javax.swing.JLabel substitutionFromLabel;
    private javax.swing.JPanel substitutionsPanel;
    private javax.swing.JButton substitutorPlayLeadsheetButton;
    private javax.swing.JCheckBox substitutorRectifyCheckBox;
    private javax.swing.JButton substitutorSaveLeadsheetButton;
    private javax.swing.JButton substitutorStopLeadsheetButton;
    private javax.swing.JTable transJTable;
    private javax.swing.JScrollPane transScrollPane;
    private javax.swing.JLabel transTotalLabel;
    private javax.swing.JLabel transTotalWeightValueLabel;
    private javax.swing.JLabel transformationSubstitutionNameLabel;
    private javax.swing.JPanel transformationsPanel;
    private javax.swing.JPanel useSubstitutionsButtonsPanel;
    // End of variables declaration//GEN-END:variables

    public void changeTransform(Transform transform, String newFilename)
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
              "<html><b>Cancel</b>, do not close this transform.</html>"
              };

            UnsavedChanges dialog = new UnsavedChanges(notate.lickgenFrame,
                    "Save changes to transform before changing?", options);

            dialog.setVisible(true);

            dialog.dispose();

            UnsavedChanges.Value choice = dialog.getValue();

            switch( choice )
              {
              case YES:

                if( !saveCurrentTransform() )
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

        this.transform = transform;
        resetEditNameButton();
        fillSubstitutionsList();
        fillTransformationsList();
        
        
        filename = newFilename;
        substitutionFromLabel.setText("Substitutions From: " + filename);
        savedMelodies = new Stack();
        savedTrans = new Stack();
        revertSubstitutionsButton.setEnabled(false);
        reapplySubstitutionsButton.setEnabled(false);

        applySubstitutionsButton.setEnabled(true);
        saveSubstitutionsButton.setEnabled(true);
        cleanTransformButton.setEnabled(true);
    }
    
    private boolean saveCurrentTransform()
    {
        chooser.setSelectedFile(new File(filename));
        
        if( chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION )
          {
            if( chooser.getSelectedFile().getName().endsWith(
                EXTENSION) )
              {
                filename = chooser.getSelectedFile().getName();

                saveTransformFile(chooser.getSelectedFile().getAbsolutePath());
              }
            else
              {
                filename = chooser.getSelectedFile().getName() + EXTENSION;

                saveTransformFile(chooser.getSelectedFile().getAbsolutePath() + EXTENSION);
              }
            substitutionFromLabel.setText("Substitutions From: " + filename);
            return true;
          }
        else
        {
            return false;
        }
    }
    private boolean unsavedChanges()
    {
        return transform.hasChanged();
    }
    private void resetEditNameButton()
    {
        editRow = -1;
        editSubstitutionNameButton.setText("Edit Substitution Name");
    }
    public void setTransform(Transform trans)
    {
        changeTransform(trans, "learnedTransform");
    }
    public void applySubstitutions()
    {
        notate.stopPlaying();
        notate.adjustSelection();
        int start = notate.getCurrentSelectionStart();
        int stop = notate.getCurrentSelectionEnd();
        MelodyPart melody = notate.getCurrentMelodyPart().extract(start,
                                                                  stop,
                                                                  false);
        ChordPart chords = notate.getChordProg().extract(start, stop);
        applySubstitutionsToPart(melody, chords);
    }
    
    public void applySubstitutionsToPart(MelodyPart melody, ChordPart chords)
    {
        
        if(transform != null)
        {
            Stave stave = notate.getCurrentStave();
            int start = notate.getCurrentSelectionStart();
            int stop = notate.getCurrentSelectionEnd();
            savedMelodies.add(new MelodyInContext(melody.copy(), 
                                                  stave, 
                                                  start, 
                                                  stop));
            
            MelodyPart transformedPart = transform.applySubstitutionsToMelodyPart(melody,
                                                                                  chords,
                                                                                  notate);
            

            
            notate.getMelodyPart(stave).newPasteOver(transformedPart, start);
            if(substitutorRectifyCheckBox.isSelected())
            {
                notate.rectifySelection(stave,start,stop);
                
            }
            notate.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "putLick " + start + " - " + stop);
            ImproVisor.setPlayEntrySounds(true);
            
            revertSubstitutionsButton.setEnabled(true);
            savedTrans = new Stack();
            reapplySubstitutionsButton.setEnabled(true);
        }
        
    }
    
    public void revertSubs()
    {
        MelodyInContext originalPart = savedMelodies.pop();
        notate.stopPlaying();
        Stave stave = originalPart.getStave();
        int start = originalPart.getStart();
        int stop = originalPart.getStop();
        
        MelodyPart oldPart = notate.getMelodyPart(stave).extract(start, stop, false);
        savedTrans.push(new MelodyInContext(oldPart, stave, start, stop));
        
        stave.setSelection(start, stop);
        notate.getMelodyPart(stave).newPasteOver(originalPart.getMelody(), start);
        
        if(savedMelodies.size() < 1)
        {
            revertSubstitutionsButton.setEnabled(false);
            reapplySubstitutionsButton.setEnabled(false);
        }
    }
    private class MelodyInContext {
        private MelodyPart melody;
        private Stave stave;
        private int start;
        private int stop;
        
        public MelodyInContext(MelodyPart melody,Stave stave, int start, int stop)
        {
            this.melody = melody;
            this.stave = stave;
            this.start = start;
            this.stop = stop;
        }
        
        public MelodyPart getMelody()
        {
            return this.melody.copy();
        }
        
        public Stave getStave()
        {
            return this.stave;
        }
        
        public int getStart()
        {
            return this.start;
        }
        
        public int getStop()
        {
            return this.stop;
        }
    }
    private void fillSubstitutionsList()
    {
        
        if(filename != null && filename.length() > 0)
        {
            substitutionFromLabel.setText("Substitutions From: " + filename);
        }
        createNewSubstitutionButton.setEnabled((editRow < 0));
        addSubsFromOtherFileButton.setEnabled((editRow < 0));
        deleteSubstitutionButton.setEnabled(false);
        editSubstitutionNameButton.setEnabled((editRow >= 0));

        deleteTransformationButton.setEnabled(false);
        editSelectedTransformationButton.setEnabled(false);
        createNewTransformationButton.setEnabled(false);
        scaleTransWeightsButton.setEnabled(false);
        
        scaleMotifWeightsButton.setEnabled((editRow < 0));
        scaleEmbWeightsButton.setEnabled((editRow < 0));
        
        setTotalSubWeights();
        
        subJTable.setModel(new javax.swing.table.AbstractTableModel() {
            ArrayList<Substitution> subs = transform.substitutions;
            
            public int getRowCount()
            {
                return subs.size();
            }
            public int getColumnCount()
            {
                return 1;
            }
            public Substitution getValueAt(int rowIndex, int columnIndex) {
                return subs.get(rowIndex);
            }
            @Override
            public boolean isCellEditable(int row, int col) {
                        if(editRow >= 0)
                        {
                            return (row == editRow);
                        }
                        else
                            return true;
		}
            @Override
            public void setValueAt(Object value, int rowIndex, int columnIndex) {
                subs.set(rowIndex, (Substitution)value);
            }
        });
    }
    
    private void fillTransformationsList()
    {
        deleteTransformationButton.setEnabled(false);
        editSelectedTransformationButton.setEnabled(false);
        
        
        int selectedSubRow = subJTable.getEditingRow();
        String subName = "";
        int totalSubWeight = 0;
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution) subJTable.getValueAt(selectedSubRow, 0);
            subName = selectedSub.getName();

            totalSubWeight = selectedSub.getTotalWeight();
            
            transJTable.setModel(new javax.swing.table.AbstractTableModel() {
                ArrayList<Transformation> trans = ((Substitution)subJTable.getValueAt(subJTable.getEditingRow(), 0)).transformations;


                public int getRowCount()
                {
                    return trans.size();
                }
                public int getColumnCount()
                {
                    return 1;
                }
                public Transformation getValueAt(int rowIndex, int columnIndex) {
                    return trans.get(rowIndex);
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                            return true;
                    }
                @Override
                public void setValueAt(Object value, int rowIndex, int columnIndex) {
                    trans.set(rowIndex, (Transformation)value);
                }
            });
            
        }
        else
        {
            transJTable.setModel(new javax.swing.table.AbstractTableModel() {
                ArrayList<Transformation> trans = new ArrayList<Transformation>();


                public int getRowCount()
                {
                    return trans.size();
                }
                public int getColumnCount()
                {
                    return 1;
                }
                public Transformation getValueAt(int rowIndex, int columnIndex) {
                    return trans.get(rowIndex);
                }
                @Override
                public boolean isCellEditable(int row, int col) {
                            return true;
                    }
                @Override
                public void setValueAt(Object value, int rowIndex, int columnIndex) {
                    trans.set(rowIndex, (Transformation)value);
                }
            });
        }
        transformationSubstitutionNameLabel.setText("For Substitution: " + subName);
        transTotalWeightValueLabel.setText(((totalSubWeight > 0)? 
                                            (totalSubWeight): 
                                            "") + "");
        
    }

    private int saveTransformFile(String filepath) {
        try
        {
            StringBuilder content = new StringBuilder();
            transform.toFile(content);
            FileWriter out = new FileWriter(new File(filepath));
            out.write(content.toString());
            out.close();
            transform.hasChanged = false;
            return 0;
        }
        catch( IOException e )
        {
            ErrorLog.log(ErrorLog.WARNING, "Error saving to " + filename);
            return -1;
        }
    }
    
public void setTotalSubWeights()
{
    if(transform != null)
    {
        motifTotalWeightValueLabel.setText(transform.getTotalMotifWeight() +"");
        embTotalWeightValueLabel.setText(transform.getTotalEmbWeight() +"");
    }
    else
    {
        motifTotalWeightValueLabel.setText("");
        embTotalWeightValueLabel.setText("");
    }
}

public void setTotalTransWeights()
{
        int selectedSubRow = subJTable.getEditingRow();
        int totalSubWeight = 0;
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution) subJTable.getValueAt(selectedSubRow, 0);

            totalSubWeight = selectedSub.getTotalWeight();
            
        }
        transTotalWeightValueLabel.setText(((totalSubWeight > 0)? 
                                            (totalSubWeight): 
                                            "") + "");
}
    
public class SubstitutionCellRenderer implements TableCellRenderer{
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        
        Substitution sub = (Substitution)value;
        
        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        SubCheckBox subEnabled = new SubCheckBox(sub);
        if(editRow >= 0)
        {
            subEnabled.setEnabled(false);
        }
        GridBagConstraints subEnabledC = new GridBagConstraints(  0, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(subEnabled, subEnabledC);
        GridBagConstraints subNameC = new GridBagConstraints( 1, 0,
                                                            1, 1,
                                                            1.0, 0.0,
            GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,4,0,0), 0, 0);
        if(editRow == row)
        {
            SubNameEditField editName = new SubNameEditField(sub);
            panel.add(editName, subNameC);
        }
        else
        {
            javax.swing.JLabel subName = new javax.swing.JLabel(sub.getName());
            subName.setMinimumSize(new Dimension(190, 25));
            subName.setPreferredSize(new Dimension(190, 25));
            panel.add(subName, subNameC);
        }
        
        SubTypeComboBox subTypesList = new SubTypeComboBox(sub);
        if(editRow >= 0)
        {
            subTypesList.setEnabled(false);
        }
        GridBagConstraints subTypesC = new GridBagConstraints(  2, 0,
                                                                1, 1,
                                                                0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(subTypesList, subTypesC);
        
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        
        GridBagConstraints subWeightLabelC = new GridBagConstraints(3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,20,0,0), 0, 0);
        panel.add(weightLabel, subWeightLabelC);
        
        SubWeightField subWeightField = new SubWeightField(sub);
        if(editRow >= 0)
        {
            subWeightField.setEnabled(false);
        }
        GridBagConstraints subWeightC = new GridBagConstraints(4, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,4), 0, 0);
        panel.add(subWeightField, subWeightC);
        
        if ((hasFocus && editRow < 0) || (editRow == row)) {
                panel.setBackground(table.getSelectionBackground());
         }
        else
        {
            if(editRow >= 0)
                panel.setBackground(new Color(240,240,240));
            else
                panel.setBackground(table.getBackground());
        }
        return panel;
    }
    }

    /**
     *
     */
public class SubstitutionCellEditor extends AbstractCellEditor implements TableCellEditor {
    Substitution sub;
    public Component getTableCellEditorComponent(javax.swing.JTable table, Object value,
        boolean isSelected, int row, int column) {
        
        if(editRow < 0)
        {
            deleteSubstitutionButton.setEnabled(true);
            createNewTransformationButton.setEnabled(true);
            scaleTransWeightsButton.setEnabled(true);
        }
        editSubstitutionNameButton.setEnabled(true);
        
        table.setEditingRow(row);
        sub = (Substitution)value;

        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        SubCheckBox subEnabled = new SubCheckBox(sub);
        if(editRow >= 0)
        {
            subEnabled.setEnabled(false);
        }
        GridBagConstraints subEnabledC = new GridBagConstraints(  0, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(subEnabled, subEnabledC);
        
        GridBagConstraints subNameC = new GridBagConstraints( 1, 0,
                                                                1, 1,
                                                                1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,4,0,0), 0, 0);
        if(editRow == row)
        {
            SubNameEditField editName = new SubNameEditField(sub);
            panel.add(editName, subNameC);
        }
        else
        {
            javax.swing.JLabel subName = new javax.swing.JLabel(sub.getName());
            subName.setMinimumSize(new Dimension(190, 25));
            subName.setPreferredSize(new Dimension(190, 25));
            panel.add(subName, subNameC);
        }
        
        SubTypeComboBox subTypesList = new SubTypeComboBox(sub);
        if(editRow >= 0)
        {
            subTypesList.setEnabled(false);
        }
        GridBagConstraints subTypesC = new GridBagConstraints(  2, 0,
                                                                1, 1,
                                                                0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(subTypesList, subTypesC);
        
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        
        GridBagConstraints subWeightLabelC = new GridBagConstraints(3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,20,0,0), 0, 0);
        panel.add(weightLabel, subWeightLabelC);
        
        SubWeightField subWeightField = new SubWeightField(sub);
        if(editRow >= 0)
        {
            subWeightField.setEnabled(false);
        }
        GridBagConstraints subWeightC = new GridBagConstraints(4, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,4), 0, 0);
        panel.add(subWeightField, subWeightC);
        
        panel.setBackground(table.getSelectionBackground());
        
        if(editRow < 0)
            fillTransformationsList();
        return panel;
    }
    
    @Override
    public boolean isCellEditable(EventObject e)
    {
        return true;
    }

    public Object getCellEditorValue() {
        //System.out.println("getting here");
        return sub;
    }
}

public class SubCheckBox extends javax.swing.JCheckBox implements ActionListener {
    private Substitution sub;
    
    public SubCheckBox(Substitution sub)
    {
        this.sub = sub;
        super.setSelected(sub.getEnabled());
        super.addActionListener(this);
        super.setBackground(Color.WHITE);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.JCheckBox cb = (javax.swing.JCheckBox)e.getSource();
        sub.setEnabled(cb.isSelected());
    }
}

public class SubNameEditField extends javax.swing.JTextField implements ActionListener, DocumentListener {
    private Substitution sub;
    
    public SubNameEditField(Substitution sub)
    {
        this.sub = sub;
        super.setText(sub.getName());
        super.setMinimumSize(new Dimension(190, 25));
        super.setPreferredSize(new Dimension(190, 25));
        super.addActionListener(this);
        super.getDocument().addDocumentListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        editRow = -1;
        fillSubstitutionsList();
        fillTransformationsList();
        editSubstitutionNameButton.setText("Edit Substitution Name");
    }

    public void insertUpdate(DocumentEvent e) {
        String str = "";
        try {
            str = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
        }
        if(sub != null)
        {
            sub.setName(str);
        }
    }

    public void removeUpdate(DocumentEvent e) {
        String str = "";
        try {
            str = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
        }
        if(sub != null)
        {
            sub.setName(str);
        }
    }

    public void changedUpdate(DocumentEvent e) {
        String str = "";
        try {
            str = e.getDocument().getText(0, e.getDocument().getLength());
        } catch (BadLocationException ex) {
        }
        if(sub != null)
        {
            sub.setName(str);
        }
    }
}

public class SubTypeComboBox extends javax.swing.JComboBox implements ActionListener {
    private Substitution sub;
    String[] subTypes = { "motif", "embellishment" };
    
    public SubTypeComboBox(Substitution sub)
    {
        super.setEditable(false);
        super.setEnabled(true);
        super.addItem("motif");
        super.addItem("embellishment");
        this.sub = sub;
        
        if(sub.getType().equals("motif"))
            super.setSelectedIndex(0);
        else if(sub.getType().equals("embellishment"))
            super.setSelectedIndex(1);   
        
        super.addActionListener(this);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.JComboBox cb = (javax.swing.JComboBox)e.getSource();
        
        if(cb.getSelectedItem() != null && sub != null)
        {
            sub.setType(cb.getSelectedItem().toString());
            setTotalSubWeights();
        }
    }
    public void itemStateChanged(ItemEvent e) 
    {
        sub.setType(e.getItem().toString());
        setTotalSubWeights();
    }
}

public class SubWeightField extends javax.swing.JSpinner implements ChangeListener {
    private Substitution sub;
    
    public SubWeightField(Substitution sub)
    {
        this.sub = sub;
        SpinnerNumberModel model = new SpinnerNumberModel(sub.getWeight(), 0, Integer.MAX_VALUE, 1);
        super.setModel(model);
        super.setMinimumSize(new Dimension(60, 25));
        super.setPreferredSize(new Dimension(60, 25));
        super.addChangeListener(this);
    }

    public void stateChanged(ChangeEvent e) {
        SpinnerModel numberModel = super.getModel();
        if(numberModel instanceof SpinnerNumberModel)
        {
            sub.setWeight((Integer)numberModel.getValue());
            setTotalSubWeights();
        }
    }
    }

public class TransformationCellRenderer implements TableCellRenderer{
    public Component getTableCellRendererComponent(javax.swing.JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        Transformation trans = (Transformation)value;
        
        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        TransCheckBox transEnabled = new TransCheckBox(trans);
        GridBagConstraints transEnabledC = new GridBagConstraints(  0, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(transEnabled, transEnabledC);
        
        javax.swing.JLabel transDescLabel = new javax.swing.JLabel(trans.getDescription());
        transDescLabel.setMinimumSize(new Dimension(307, 25));
        transDescLabel.setPreferredSize(new Dimension(307, 25));
        transDescLabel.setMaximumSize(new Dimension(100000, 25));
        GridBagConstraints transNameC = new GridBagConstraints( 1, 0,
                                                                1, 1,
                                                                1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,4,0,0), 0, 0);
        panel.add(transDescLabel, transNameC);
        
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        GridBagConstraints transWeightLabelC = new GridBagConstraints(  2, 0,
                                                                        1, 1,
                                                                        0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(weightLabel, transWeightLabelC);
        
        TransWeightField transWeightField = new TransWeightField(trans);
        GridBagConstraints transWeightC = new GridBagConstraints(   3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,4), 0, 0);
        panel.add(transWeightField, transWeightC);
        
        if (hasFocus) {
                panel.setBackground(table.getSelectionBackground());
         }
        else
        {
                panel.setBackground(table.getBackground());
        }
        return panel;
    }
}

    /**
     *
     */
public class TransformationCellEditor extends AbstractCellEditor implements TableCellEditor {
    Transformation trans;
    public Component getTableCellEditorComponent(javax.swing.JTable table, Object value,
        boolean isSelected, int row, int column) {
        trans = (Transformation)value;
        deleteTransformationButton.setEnabled(true);
        editSelectedTransformationButton.setEnabled(true);
        javax.swing.JPanel panel = new javax.swing.JPanel(new GridBagLayout());
        
        TransCheckBox transEnabled = new TransCheckBox(trans);
        GridBagConstraints transEnabledC = new GridBagConstraints(  0, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(transEnabled, transEnabledC);
        
        javax.swing.JLabel transDescLabel = new javax.swing.JLabel(trans.getDescription());
        transDescLabel.setMinimumSize(new Dimension(307, 25));
        transDescLabel.setPreferredSize(new Dimension(307, 25));
        transDescLabel.setMaximumSize(new Dimension(100000, 25));
        GridBagConstraints transNameC = new GridBagConstraints( 1, 0,
                                                                1, 1,
                                                                1.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,4,0,0), 0, 0);
        panel.add(transDescLabel, transNameC);
        
        javax.swing.JLabel weightLabel = new javax.swing.JLabel("weight = ");
        GridBagConstraints transWeightLabelC = new GridBagConstraints(  2, 0,
                                                                        1, 1,
                                                                        0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0), 0, 0);
        panel.add(weightLabel, transWeightLabelC);
        
        TransWeightField transWeightField = new TransWeightField(trans);
        GridBagConstraints transWeightC = new GridBagConstraints(   3, 0,
                                                                    1, 1,
                                                                    0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,4), 0, 0);
        panel.add(transWeightField, transWeightC);
        panel.setBackground(table.getSelectionBackground());
        return panel;
    }
    
    @Override
    public boolean isCellEditable(EventObject e)
    {
        return true;
    }

    public Object getCellEditorValue() {
        //System.out.println("getting here");
        return trans;
    }
}

public class TransCheckBox extends javax.swing.JCheckBox implements ActionListener {
    private Transformation trans;
    
    public TransCheckBox(Transformation trans)
    {
        this.trans = trans;
        super.setSelected(trans.getEnabled());
        super.addActionListener(this);
        super.setBackground(Color.WHITE);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        javax.swing.JCheckBox cb = (javax.swing.JCheckBox)e.getSource();
        trans.setEnabled(cb.isSelected());
    }
}

public class TransWeightField extends javax.swing.JSpinner implements ChangeListener {
    private Transformation trans;
    
    public TransWeightField(Transformation trans)
    {
        this.trans = trans;
        SpinnerNumberModel model = new SpinnerNumberModel(trans.getWeight(), 0, Integer.MAX_VALUE, 1);
        super.setModel(model);
        super.setMinimumSize(new Dimension(60, 25));
        super.setPreferredSize(new Dimension(60, 25));
        super.addChangeListener(this);
    }
    

    public void stateChanged(ChangeEvent e) {
        SpinnerModel numberModel = super.getModel();
        if(numberModel instanceof SpinnerNumberModel)
        {
            trans.setWeight((Integer)numberModel.getValue());
            setTotalTransWeights();
        }
    }
    }

public class TransformationDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transformation trans;
    private javax.swing.JTextArea contents;
    
    public TransformationDialogue(javax.swing.JFrame frame, Transformation trans)
    {
        super(frame, "Transformation Editor", true);
        super.setSize(800,600);
        this.trans = trans;
        StringBuilder transFile = new StringBuilder();
        trans.toFile(transFile, "");
        contents = new javax.swing.JTextArea();
        contents.setFont(new Font("monospaced", Font.PLAIN, 14));
        contents.setTabSize(8);
        contents.setText(transFile.toString());
        super.setLocationRelativeTo(frame);
        javax.swing.JScrollPane scroll = new javax.swing.JScrollPane();
        scroll.setPreferredSize(new Dimension(820,620));
        scroll.setViewportView(contents);
        getContentPane().add(scroll);
        
        javax.swing.JButton saveButton = new javax.swing.JButton("Save and Close"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        boolean result = trans.setTransformation((Polylist)Polylist.PolylistFromString(contents.getText()).first());
        if(result)
        {
            dispose(); 
            setVisible(false); 
            fillTransformationsList();
        }
    }
}    

public class ScaleMotifWeightsDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transform transform;
    private javax.swing.JTextField contents;
    
    public ScaleMotifWeightsDialogue(javax.swing.JFrame frame, Transform trans)
    {
        super(frame, "Scale Motif Weights", true);
        this.transform = trans;
        contents = new javax.swing.JTextField("1");
        super.setLocationRelativeTo(frame);
        contents.setPreferredSize(new Dimension(50,20));
        contents.addActionListener(this);
        javax.swing.JLabel scaleLabel = new javax.swing.JLabel("Scale all motif weights by: ");
        
        javax.swing.JPanel layout = new javax.swing.JPanel(new FlowLayout());
        layout.add(scaleLabel);
        layout.add(contents);
        getContentPane().add(layout);
        javax.swing.JButton saveButton = new javax.swing.JButton("Scale"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            double scale = Double.parseDouble(contents.getText());
            transform.scaleMotifWeights(scale);
            dispose(); 
            setVisible(false); 
            fillSubstitutionsList();
            fillTransformationsList();
        }
        catch (Exception ex)
        {
            
        }
    }
}    
public class ScaleEmbWeightsDialogue extends javax.swing.JDialog implements ActionListener  {

    private Transform transform;
    private javax.swing.JTextField contents;
    
    public ScaleEmbWeightsDialogue(javax.swing.JFrame frame, Transform trans)
    {
        super(frame, "Scale Embellishment Weights", true);
        this.transform = trans;
        contents = new javax.swing.JTextField("1");
        super.setLocationRelativeTo(frame);
        contents.setPreferredSize(new Dimension(50,20));
        contents.addActionListener(this);
        javax.swing.JLabel scaleLabel = new javax.swing.JLabel("Scale all embellishment weights by: ");
        
        javax.swing.JPanel layout = new javax.swing.JPanel(new FlowLayout());
        layout.add(scaleLabel);
        layout.add(contents);
        getContentPane().add(layout);
        javax.swing.JButton saveButton = new javax.swing.JButton("Scale"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            double scale = Double.parseDouble(contents.getText());
            transform.scaleEmbWeights(scale);
            dispose(); 
            setVisible(false); 
            fillSubstitutionsList();
            fillTransformationsList();
        }
        catch (Exception ex)
        {
            
        }
    }
}    
public class ScaleTransWeightsDialogue extends javax.swing.JDialog implements ActionListener  {

    private Substitution sub;
    private javax.swing.JTextField contents;
    
    public ScaleTransWeightsDialogue(javax.swing.JFrame frame, Substitution sub)
    {
        super(frame, "Scale Transformation Weights", true);
        this.sub = sub;
        contents = new javax.swing.JTextField("1");
        super.setLocationRelativeTo(frame);
        contents.setPreferredSize(new Dimension(50,20));
        contents.addActionListener(this);
        javax.swing.JLabel scaleLabel = new javax.swing.JLabel("Scale all transformation weights by: ");
        
        javax.swing.JPanel layout = new javax.swing.JPanel(new FlowLayout());
        layout.add(scaleLabel);
        layout.add(contents);
        getContentPane().add(layout);
        javax.swing.JButton saveButton = new javax.swing.JButton("Scale"); 
        saveButton.addActionListener(this);
        getContentPane().add(saveButton, BorderLayout.SOUTH);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        pack(); 
        setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        try {
            double scale = Double.parseDouble(contents.getText());
            sub.scaleTransWeights(scale);
            dispose(); 
            setVisible(false); 
            fillTransformationsList();
        }
        catch (Exception ex)
        {
            
        }
    }
}    
}
