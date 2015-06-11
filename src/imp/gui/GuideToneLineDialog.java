/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.data.GuideLineGenerator;
import imp.data.MelodyPart;
import java.util.Enumeration;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import imp.data.Note;
import imp.data.NoteSymbol;

/**
 *
 * @author muddCS15
 */
public class GuideToneLineDialog extends javax.swing.JDialog {

    /**
     * Creates new form GuideToneLineDialog
     * @param parent Frame that spawned this dialog box
     * @param modal true if user cannot access main window until dialog box is closed, false otherwise
     */
    public GuideToneLineDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        this.setTitle("Generate Guide Tone Line");
        this.setResizable(false);
        initComponents();
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

        directionButtons = new javax.swing.ButtonGroup();
        numberOfLinesButtons = new javax.swing.ButtonGroup();
        scaleDegreeButtons = new javax.swing.ButtonGroup();
        linesPanel = new javax.swing.JPanel();
        numberOfLinesLabel = new javax.swing.JLabel();
        oneLine = new javax.swing.JRadioButton();
        twoLines = new javax.swing.JRadioButton();
        directionPanel = new javax.swing.JPanel();
        directionLabel = new javax.swing.JLabel();
        descending = new javax.swing.JRadioButton();
        noPreference = new javax.swing.JRadioButton();
        ascending = new javax.swing.JRadioButton();
        scaleDegPanel = new javax.swing.JPanel();
        scaleDegLabel = new javax.swing.JLabel();
        deg1 = new javax.swing.JRadioButton();
        deg2 = new javax.swing.JRadioButton();
        deg3 = new javax.swing.JRadioButton();
        deg4 = new javax.swing.JRadioButton();
        deg5 = new javax.swing.JRadioButton();
        deg6 = new javax.swing.JRadioButton();
        deg7 = new javax.swing.JRadioButton();
        buttonPanel = new javax.swing.JPanel();
        generateLine = new javax.swing.JButton();
        lowLimit = new javax.swing.JPanel();
        lowLabel = new javax.swing.JLabel();
        lowLimitSlider = new javax.swing.JSlider();
        lowNote = new javax.swing.JLabel();
        highLimit = new javax.swing.JPanel();
        highLabel = new javax.swing.JLabel();
        highLimitSlider = new javax.swing.JSlider();
        highNote = new javax.swing.JLabel();
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        filler3 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));
        filler4 = new javax.swing.Box.Filler(new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20), new java.awt.Dimension(20, 20));

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        linesPanel.setLayout(new java.awt.GridBagLayout());

        numberOfLinesLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        numberOfLinesLabel.setText("Number of Lines:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        linesPanel.add(numberOfLinesLabel, gridBagConstraints);

        numberOfLinesButtons.add(oneLine);
        oneLine.setSelected(true);
        oneLine.setText("One Line");
        oneLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                oneLineActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        linesPanel.add(oneLine, gridBagConstraints);

        numberOfLinesButtons.add(twoLines);
        twoLines.setText("Two Lines");
        twoLines.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                twoLinesActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        linesPanel.add(twoLines, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        getContentPane().add(linesPanel, gridBagConstraints);

        directionPanel.setLayout(new java.awt.GridBagLayout());

        directionLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        directionLabel.setText("Direction:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        directionPanel.add(directionLabel, gridBagConstraints);

        directionButtons.add(descending);
        descending.setText("Descending");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        directionPanel.add(descending, gridBagConstraints);

        directionButtons.add(noPreference);
        noPreference.setSelected(true);
        noPreference.setText("No Preference");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        directionPanel.add(noPreference, gridBagConstraints);

        directionButtons.add(ascending);
        ascending.setText("Ascending");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        directionPanel.add(ascending, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        getContentPane().add(directionPanel, gridBagConstraints);

        scaleDegPanel.setLayout(new java.awt.GridBagLayout());

        scaleDegLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        scaleDegLabel.setText("Start on Scale Degree:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(scaleDegLabel, gridBagConstraints);

        scaleDegreeButtons.add(deg1);
        deg1.setText("1");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg1, gridBagConstraints);

        scaleDegreeButtons.add(deg2);
        deg2.setText("2");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg2, gridBagConstraints);

        scaleDegreeButtons.add(deg3);
        deg3.setSelected(true);
        deg3.setText("3");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg3, gridBagConstraints);

        scaleDegreeButtons.add(deg4);
        deg4.setText("4");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg4, gridBagConstraints);

        scaleDegreeButtons.add(deg5);
        deg5.setText("5");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg5, gridBagConstraints);

        scaleDegreeButtons.add(deg6);
        deg6.setText("6");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg6, gridBagConstraints);

        scaleDegreeButtons.add(deg7);
        deg7.setText("7");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        scaleDegPanel.add(deg7, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        getContentPane().add(scaleDegPanel, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        generateLine.setText("Generate Guide Tone Line");
        generateLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLineActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        buttonPanel.add(generateLine, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        getContentPane().add(buttonPanel, gridBagConstraints);

        lowLimit.setLayout(new java.awt.GridBagLayout());

        lowLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        lowLabel.setText("Low Limit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        lowLimit.add(lowLabel, gridBagConstraints);

        lowLimitSlider.setMajorTickSpacing(1);
        lowLimitSlider.setMaximum(59);
        lowLimitSlider.setMinimum(52);
        lowLimitSlider.setPaintTicks(true);
        lowLimitSlider.setSnapToTicks(true);
        lowLimitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                lowLimitSliderStateChanged(evt);
            }
        });
        lowLimit.add(lowLimitSlider, new java.awt.GridBagConstraints());

        lowNote.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Note initLow = new Note(lowLimitSlider.getValue());
        NoteSymbol nsLow = NoteSymbol.makeNoteSymbol(initLow);
        lowNote.setText(nsLow.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        lowLimit.add(lowNote, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        getContentPane().add(lowLimit, gridBagConstraints);

        highLimit.setLayout(new java.awt.GridBagLayout());

        highLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        highLabel.setText("High Limit");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        highLimit.add(highLabel, gridBagConstraints);

        highLimitSlider.setMajorTickSpacing(1);
        highLimitSlider.setMaximum(92);
        highLimitSlider.setMinimum(85);
        highLimitSlider.setPaintTicks(true);
        highLimitSlider.setSnapToTicks(true);
        highLimitSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                highLimitSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        highLimit.add(highLimitSlider, gridBagConstraints);

        highNote.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        Note initHigh = new Note(highLimitSlider.getValue());
        NoteSymbol nsHigh = NoteSymbol.makeNoteSymbol(initHigh);
        highNote.setText(nsHigh.toString());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        highLimit.add(highNote, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        getContentPane().add(highLimit, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        getContentPane().add(filler2, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 7;
        getContentPane().add(filler1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        getContentPane().add(filler3, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        getContentPane().add(filler4, gridBagConstraints);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void generateLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateLineActionPerformed
        //Get which options are selected
        JRadioButton numberOfLines = getSelected(numberOfLinesButtons);
        JRadioButton direction = getSelected(directionButtons);
        JRadioButton scaleDeg = getSelected(scaleDegreeButtons);
        
        //Get paramaters to pass into constructor
        Notate notate = (Notate)this.getParent();
        String scaleDegString = scaleDeg.getText();
        boolean alternating = false;
        
        //Passing in "mix" as the scaleDegString indicates that two lines should be generated
        //Right now, the order in which the two lines appear always alternates,
        //i.e. the line has a trapezoidal shape
        if(numberOfLines.equals(twoLines)){
            scaleDegString = "mix";
            alternating = true;
        }
        
        //construct a guide tone line generator, make a guide tone line (melody part), then add it as a new chorus
        GuideLineGenerator guideLine = new GuideLineGenerator(notate.score.getChordProg(), buttonToDirection(direction), scaleDegString, alternating);
        MelodyPart guideToneLine = guideLine.makeGuideLine();
        notate.addChorus(guideToneLine);
        
    }//GEN-LAST:event_generateLineActionPerformed

    private void twoLinesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_twoLinesActionPerformed
        enableButtons(scaleDegreeButtons, false);
    }//GEN-LAST:event_twoLinesActionPerformed

    /**
     * 
     * @param group ButtonGroup to enable/disable
     * @param enabled true to enable, false to disable
     */
    private void enableButtons(ButtonGroup group, boolean enabled){
        for(Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();){
            AbstractButton b = buttons.nextElement();
            b.setEnabled(enabled);
        }
    }
    
    private void oneLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_oneLineActionPerformed
        enableButtons(scaleDegreeButtons, true);
    }//GEN-LAST:event_oneLineActionPerformed

    private void lowLimitSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_lowLimitSliderStateChanged
        JSlider slider = (JSlider)evt.getSource();
        Note n = new Note(slider.getValue());
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(n);
        lowNote.setText(ns.toString());
    }//GEN-LAST:event_lowLimitSliderStateChanged

    private void highLimitSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_highLimitSliderStateChanged
        JSlider slider = (JSlider)evt.getSource();
        Note n = new Note(slider.getValue());
        NoteSymbol ns = NoteSymbol.makeNoteSymbol(n);
        highNote.setText(ns.toString());
    }//GEN-LAST:event_highLimitSliderStateChanged

    /**
     * returns which JRadioButton in a ButtonGroup is selected
     * @param group the ButtonGroup from which you want to return the selected button
     * @return the JRadioButton that is selected
     */
    private JRadioButton getSelected(ButtonGroup group){
        for(Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();){
            AbstractButton b = buttons.nextElement();
            if(b.isSelected()){
                return (JRadioButton)b;
            }
        }
        return null;
    }
    
    /**
     * returns the direction associated with the given button
     * @param b a JRadioButton
     * @return the direction associated with that button (1 for up, 0 for same, -1 for down)
     */
    private int buttonToDirection(JRadioButton b){
        if(b.equals(ascending)){
            return 1;
        }else if(b.equals(descending)){
            return -1;
        }else if(b.equals(noPreference)){
            return 0;
        }else{
            //shouldn't happen
            return 0;
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(GuideToneLineDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                GuideToneLineDialog dialog = new GuideToneLineDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton ascending;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JRadioButton deg1;
    private javax.swing.JRadioButton deg2;
    private javax.swing.JRadioButton deg3;
    private javax.swing.JRadioButton deg4;
    private javax.swing.JRadioButton deg5;
    private javax.swing.JRadioButton deg6;
    private javax.swing.JRadioButton deg7;
    private javax.swing.JRadioButton descending;
    private javax.swing.ButtonGroup directionButtons;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JPanel directionPanel;
    private javax.swing.Box.Filler filler1;
    private javax.swing.Box.Filler filler2;
    private javax.swing.Box.Filler filler3;
    private javax.swing.Box.Filler filler4;
    private javax.swing.JButton generateLine;
    private javax.swing.JLabel highLabel;
    private javax.swing.JPanel highLimit;
    private javax.swing.JSlider highLimitSlider;
    private javax.swing.JLabel highNote;
    private javax.swing.JPanel linesPanel;
    private javax.swing.JLabel lowLabel;
    private javax.swing.JPanel lowLimit;
    private javax.swing.JSlider lowLimitSlider;
    private javax.swing.JLabel lowNote;
    private javax.swing.JRadioButton noPreference;
    private javax.swing.ButtonGroup numberOfLinesButtons;
    private javax.swing.JLabel numberOfLinesLabel;
    private javax.swing.JRadioButton oneLine;
    private javax.swing.JLabel scaleDegLabel;
    private javax.swing.JPanel scaleDegPanel;
    private javax.swing.ButtonGroup scaleDegreeButtons;
    private javax.swing.JRadioButton twoLines;
    // End of variables declaration//GEN-END:variables
}
