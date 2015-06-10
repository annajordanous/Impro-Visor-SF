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
import javax.swing.ButtonModel;
import javax.swing.JRadioButton;

/**
 *
 * @author muddCS15
 */
public class GuideToneLineDialog extends javax.swing.JDialog {

    /**
     * Creates new form GuideToneLineDialog
     */
    public GuideToneLineDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
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

        directionButtons = new javax.swing.ButtonGroup();
        numberOfLinesButtons = new javax.swing.ButtonGroup();
        scaleDegreeButtons = new javax.swing.ButtonGroup();
        ascending = new javax.swing.JRadioButton();
        descending = new javax.swing.JRadioButton();
        noPreference = new javax.swing.JRadioButton();
        oneLine = new javax.swing.JRadioButton();
        twoLines = new javax.swing.JRadioButton();
        deg1 = new javax.swing.JRadioButton();
        deg2 = new javax.swing.JRadioButton();
        deg3 = new javax.swing.JRadioButton();
        deg4 = new javax.swing.JRadioButton();
        deg5 = new javax.swing.JRadioButton();
        deg6 = new javax.swing.JRadioButton();
        deg7 = new javax.swing.JRadioButton();
        generateLine = new javax.swing.JButton();
        numberOfLinesLabel = new javax.swing.JLabel();
        directionLabel = new javax.swing.JLabel();
        scaleDegLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        directionButtons.add(ascending);
        ascending.setText("Ascending");

        directionButtons.add(descending);
        descending.setText("Descending");
        descending.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descendingActionPerformed(evt);
            }
        });

        directionButtons.add(noPreference);
        noPreference.setSelected(true);
        noPreference.setText("No Preference");
        noPreference.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noPreferenceActionPerformed(evt);
            }
        });

        numberOfLinesButtons.add(oneLine);
        oneLine.setSelected(true);
        oneLine.setText("One Line");

        numberOfLinesButtons.add(twoLines);
        twoLines.setText("Two Lines");

        scaleDegreeButtons.add(deg1);
        deg1.setText("1");

        scaleDegreeButtons.add(deg2);
        deg2.setText("2");

        scaleDegreeButtons.add(deg3);
        deg3.setSelected(true);
        deg3.setText("3");

        scaleDegreeButtons.add(deg4);
        deg4.setText("4");

        scaleDegreeButtons.add(deg5);
        deg5.setText("5");

        scaleDegreeButtons.add(deg6);
        deg6.setText("6");

        scaleDegreeButtons.add(deg7);
        deg7.setText("7");

        generateLine.setText("Generate Guide Tone Line");
        generateLine.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                generateLineActionPerformed(evt);
            }
        });

        numberOfLinesLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        numberOfLinesLabel.setText("Number of Lines:");

        directionLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        directionLabel.setText("Direction:");

        scaleDegLabel.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        scaleDegLabel.setText("Start on Scale Degree:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(77, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scaleDegLabel)
                    .addComponent(directionLabel)
                    .addComponent(numberOfLinesLabel)
                    .addComponent(generateLine)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(oneLine)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(twoLines))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(deg1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deg2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deg3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deg4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deg5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(deg6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(deg7))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ascending)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(descending)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(noPreference)))
                .addGap(72, 72, 72))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addComponent(numberOfLinesLabel)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(oneLine)
                    .addComponent(twoLines))
                .addGap(18, 18, 18)
                .addComponent(directionLabel)
                .addGap(14, 14, 14)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(descending)
                    .addComponent(ascending)
                    .addComponent(noPreference))
                .addGap(18, 18, 18)
                .addComponent(scaleDegLabel)
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(deg1)
                    .addComponent(deg2)
                    .addComponent(deg3)
                    .addComponent(deg4)
                    .addComponent(deg5)
                    .addComponent(deg6)
                    .addComponent(deg7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addComponent(generateLine)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void descendingActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descendingActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_descendingActionPerformed

    private void noPreferenceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noPreferenceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_noPreferenceActionPerformed

    private void generateLineActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_generateLineActionPerformed
        JRadioButton numberOfLines = getSelected(numberOfLinesButtons);
        JRadioButton direction = getSelected(directionButtons);
        JRadioButton scaleDeg = getSelected(scaleDegreeButtons);
        
        Notate notate = (Notate)this.getParent();
        String scaleDegString = scaleDeg.getText();
        boolean alternating = false;
        
        if(numberOfLines.equals(twoLines)){
            scaleDegString = "mix";
            alternating = true;
        }
        
        GuideLineGenerator guideLine = new GuideLineGenerator(notate.score.getChordProg(), buttonToDirection(direction), scaleDegString, alternating);
        MelodyPart guideToneLine = guideLine.makeGuideLine();
        notate.addChorus(guideToneLine);
        
        
        
    }//GEN-LAST:event_generateLineActionPerformed

    private JRadioButton getSelected(ButtonGroup group){
        for(Enumeration<AbstractButton> buttons = group.getElements(); buttons.hasMoreElements();){
            AbstractButton b = buttons.nextElement();
            if(b.isSelected()){
                return (JRadioButton)b;
            }
        }
        return null;
    }
    
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
    private javax.swing.JButton generateLine;
    private javax.swing.JRadioButton noPreference;
    private javax.swing.ButtonGroup numberOfLinesButtons;
    private javax.swing.JLabel numberOfLinesLabel;
    private javax.swing.JRadioButton oneLine;
    private javax.swing.JLabel scaleDegLabel;
    private javax.swing.ButtonGroup scaleDegreeButtons;
    private javax.swing.JRadioButton twoLines;
    // End of variables declaration//GEN-END:variables
}
