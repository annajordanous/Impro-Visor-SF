/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.MelodyPart;
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
import javax.swing.AbstractCellEditor;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import polya.Polylist;

/**
 *
 * @author Alex Putman
 */
public class SubstitutorTabPanel extends javax.swing.JPanel {

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
    
    public SubstitutorTabPanel(LickGen lickgen, Notate notate) {
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
            Logger.getLogger(SubstitutorTabPanel.class.getName()).log(Level.WARNING, null, ex);
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
        defaultSubstitutionsForStyleButton = new javax.swing.JButton();
        openSubstitutionsFileButton = new javax.swing.JButton();
        SubstitutorParametersPanel = new javax.swing.JPanel();
        substitutorRectifyCheckBox = new javax.swing.JCheckBox();
        useSubstitutionsButtonsPanel = new javax.swing.JPanel();
        applySubstitutionsButton = new javax.swing.JButton();
        revertSubstitutionsButton = new javax.swing.JButton();
        saveSubstitutionsButton = new javax.swing.JButton();
        reapplySubstitutionsButton = new javax.swing.JButton();
        substitutionsPanel = new javax.swing.JPanel();
        addSubsFromOtherFileButton = new javax.swing.JButton();
        createNewSubstitutionButton = new javax.swing.JButton();
        editSubstitutionNameButton = new javax.swing.JButton();
        deleteSubstitutionButton = new javax.swing.JButton();
        substitutionFromLabel = new javax.swing.JLabel();
        subsScrollPane = new javax.swing.JScrollPane();
        subJTable = new javax.swing.JTable();
        transformationsPanel = new javax.swing.JPanel();
        transformationSubstitutionNameLabel = new javax.swing.JLabel();
        createNewTransformationButton = new javax.swing.JButton();
        editSelectedTransformationButton = new javax.swing.JButton();
        deleteTransformationButton = new javax.swing.JButton();
        transScrollPane = new javax.swing.JScrollPane();
        transJTable = new javax.swing.JTable();
        playbackPanel = new javax.swing.JPanel();
        substitutorPlayLeadsheetButton = new javax.swing.JButton();
        substitutorStopLeadsheetButton = new javax.swing.JButton();
        substitutorSaveLeadsheetButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        selectSubstitutionsButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Select Substitution List"));
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        selectSubstitutionsButtonsPanel.add(createNewSubstitutionsFileButton, gridBagConstraints);

        defaultSubstitutionsForStyleButton.setText("Default Substitutions for Style");
        defaultSubstitutionsForStyleButton.setEnabled(false);
        defaultSubstitutionsForStyleButton.setMaximumSize(new java.awt.Dimension(10000, 23));
        defaultSubstitutionsForStyleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                defaultSubstitutionsForStyleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        selectSubstitutionsButtonsPanel.add(defaultSubstitutionsForStyleButton, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        add(selectSubstitutionsButtonsPanel, gridBagConstraints);

        SubstitutorParametersPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Substitution Parameters"));
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

        useSubstitutionsButtonsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Use and Save Substitutions"));
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

        saveSubstitutionsButton.setText("Save Current Substitutions");
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(4, 0, 4, 0);
        useSubstitutionsButtonsPanel.add(saveSubstitutionsButton, gridBagConstraints);

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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(useSubstitutionsButtonsPanel, gridBagConstraints);

        substitutionsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Substitutions"));
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weighty = 1.0;
        add(substitutionsPanel, gridBagConstraints);

        transformationsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Transformations"));
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

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(transformationsPanel, gridBagConstraints);

        playbackPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("LeadSheet Options"));
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

        substitutorSaveLeadsheetButton.setText("Save LeadSheet");
        substitutorSaveLeadsheetButton.setToolTipText("Save current leadsheet");
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

    private void defaultSubstitutionsForStyleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_defaultSubstitutionsForStyleButtonActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_defaultSubstitutionsForStyleButtonActionPerformed

    private void openSubstitutionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openSubstitutionsFileButtonActionPerformed
        
        
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            filename = chooser.getSelectedFile().getName();
            String transformStr = "";
            try {
                transformStr = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SubstitutorTabPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transformStr.length() > 0)
            {
                transform = new Transform(lickgen, transformStr);
                fillSubstitutionsList();
                fillTransformationsList();
            }
        }
        savedMelodies = new Stack();
        savedTrans = new Stack();
        revertSubstitutionsButton.setEnabled(false);
        reapplySubstitutionsButton.setEnabled(false);
        
        applySubstitutionsButton.setEnabled(true);
        saveSubstitutionsButton.setEnabled(true);
    }//GEN-LAST:event_openSubstitutionsFileButtonActionPerformed

    private void substitutorRectifyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substitutorRectifyCheckBoxActionPerformed

    }//GEN-LAST:event_substitutorRectifyCheckBoxActionPerformed

    private void applySubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applySubstitutionsButtonActionPerformed
        // TODO add your handling code here:
        applySubstitutions();
    }//GEN-LAST:event_applySubstitutionsButtonActionPerformed

    private void revertSubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_revertSubstitutionsButtonActionPerformed
        MelodyInContext originalPart = savedMelodies.pop();
        notate.stopPlaying();
        Stave stave = originalPart.getStave();
        int start = originalPart.getStart();
        int stop = originalPart.getStop();
        
        MelodyPart oldPart = notate.getMelodyPart(stave).extract(start, stop, false);
        savedTrans.push(new MelodyInContext(oldPart, stave, start, stop));
        
        stave.setSelection(start, stop);
        notate.getMelodyPart(stave).newPasteOver(originalPart.getMelody(), start);
        
        reapplySubstitutionsButton.setEnabled(true);
        if(savedMelodies.size() < 1)
            revertSubstitutionsButton.setEnabled(false);
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

            editRow = -1;
            fillSubstitutionsList();
            fillTransformationsList();
            editSubstitutionNameButton.setText("Edit Substitution Name");
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
        Transformation trans = new Transformation(lickgen);
        Object toAddSub = subJTable.getValueAt(subJTable.getSelectedRow(), 0);
        if(toAddSub != null)
        {
            Substitution subToAddTo = (Substitution) toAddSub;
            subToAddTo.transformations.add(trans);
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
          }
    }//GEN-LAST:event_saveSubstitutionsButtonActionPerformed

    private void createNewSubstitutionsFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createNewSubstitutionsFileButtonActionPerformed
        transform = new Transform(lickgen);
        filename = "newTransformFile.transform";
        fillSubstitutionsList();
        fillTransformationsList();
        
        applySubstitutionsButton.setEnabled(true);
        saveSubstitutionsButton.setEnabled(true);
    }//GEN-LAST:event_createNewSubstitutionsFileButtonActionPerformed

    private void addSubsFromOtherFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addSubsFromOtherFileButtonActionPerformed
        if( chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION )
        {
            String transformStr = "";
            try {
                transformStr = new Scanner(chooser.getSelectedFile()).useDelimiter("\\Z").next();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(SubstitutorTabPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(transformStr.length() > 0)
            {
                Transform addTransform = new Transform(lickgen, transformStr);
                for(Substitution sub: addTransform.substitutions)
                {
                    transform.substitutions.add(sub);
                }
                fillSubstitutionsList();
                fillTransformationsList();
            }
        }
    }//GEN-LAST:event_addSubsFromOtherFileButtonActionPerformed

    private void reapplySubstitutionsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reapplySubstitutionsButtonActionPerformed
        MelodyInContext originalPart = savedTrans.pop();
        notate.stopPlaying();
        Stave stave = originalPart.getStave();
        int start = originalPart.getStart();
        int stop = originalPart.getStop();
        
        MelodyPart oldPart = notate.getMelodyPart(stave).extract(start, stop, false);
        savedMelodies.push(new MelodyInContext(oldPart, stave, start, stop));
        
        stave.setSelection(start, stop);
        notate.getMelodyPart(stave).newPasteOver(originalPart.getMelody(), start);
        
        revertSubstitutionsButton.setEnabled(true);
        if(savedTrans.size() < 1)
            reapplySubstitutionsButton.setEnabled(false);
    }//GEN-LAST:event_reapplySubstitutionsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel SubstitutorParametersPanel;
    private javax.swing.JButton addSubsFromOtherFileButton;
    private javax.swing.JButton applySubstitutionsButton;
    private javax.swing.JButton createNewSubstitutionButton;
    private javax.swing.JButton createNewSubstitutionsFileButton;
    private javax.swing.JButton createNewTransformationButton;
    private javax.swing.JButton defaultSubstitutionsForStyleButton;
    private javax.swing.JButton deleteSubstitutionButton;
    private javax.swing.JButton deleteTransformationButton;
    private javax.swing.JButton editSelectedTransformationButton;
    private javax.swing.JButton editSubstitutionNameButton;
    private javax.swing.JButton openSubstitutionsFileButton;
    private javax.swing.JPanel playbackPanel;
    private javax.swing.JButton reapplySubstitutionsButton;
    private javax.swing.JButton revertSubstitutionsButton;
    private javax.swing.JButton saveSubstitutionsButton;
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
    private javax.swing.JLabel transformationSubstitutionNameLabel;
    private javax.swing.JPanel transformationsPanel;
    private javax.swing.JPanel useSubstitutionsButtonsPanel;
    // End of variables declaration//GEN-END:variables

    public void applySubstitutions()
    {
        notate.stopPlaying();
        notate.adjustSelection();
        MelodyPart part = notate.getCurrentMelodyPart().extract(notate.getCurrentSelectionStart(), notate.getCurrentSelectionEnd(),false);
        applySubstitutionsToPart(part);
    }
    
    public void applySubstitutionsToPart(MelodyPart part)
    {
        
        if(transform != null)
        {
            Stave stave = notate.getCurrentStave();
            int start = notate.getCurrentSelectionStart();
            int stop = notate.getCurrentSelectionEnd();
            savedMelodies.add(new MelodyInContext(part.copy(), stave, start, stop));
            
            MelodyPart transformedPart = transform.applySubstitutionsToMelodyPart(part, notate);
            

            
            notate.getMelodyPart(stave).newPasteOver(transformedPart, start);
            if(substitutorRectifyCheckBox.isSelected())
            {
                notate.rectifySelection(stave,start,stop);
                
            }
            notate.playCurrentSelection(false, 0, PlayScoreCommand.USEDRUMS, "putLick " + start + " - " + stop);
            ImproVisor.setPlayEntrySounds(true);
            
            revertSubstitutionsButton.setEnabled(true);
            savedTrans = new Stack();
            reapplySubstitutionsButton.setEnabled(false);
        }
        else
            notate.putLick(part);
        
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
        if(selectedSubRow >= 0)
        {
            final Substitution selectedSub = (Substitution) subJTable.getValueAt(selectedSubRow, 0);
            subName = selectedSub.getName();

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
        
        
    }

    private int saveTransformFile(String filepath) {
        try
        {
            StringBuilder content = new StringBuilder();
            transform.toFile(content);
            FileWriter out = new FileWriter(new File(filepath));
            out.write(content.toString());
            out.close();
            return 0;
        }
        catch( IOException e )
        {
            ErrorLog.log(ErrorLog.WARNING, "Error saving to " + filename);
            return -1;
        }
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
        
        SubTextField subWeightField = new SubTextField(sub);
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
        
        SubTextField subWeightField = new SubTextField(sub);
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
        javax.swing.JTextField cb = (javax.swing.JTextField)e.getSource();
        String str = cb.getText();
        if(sub != null)
        {
            sub.setName(str);
        }
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
        }
    }
    public void itemStateChanged(ItemEvent e) 
    {
        sub.setType(e.getItem().toString());
    }
}

public class SubTextField extends javax.swing.JSpinner implements ChangeListener {
    private Substitution sub;
    
    public SubTextField(Substitution sub)
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
        
        TransTextField transWeightField = new TransTextField(trans);
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
        
        TransTextField transWeightField = new TransTextField(trans);
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

public class TransTextField extends javax.swing.JSpinner implements ChangeListener {
    private Transformation trans;
    
    public TransTextField(Transformation trans)
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

}
