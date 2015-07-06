/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.data.MelodyPart;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author muddCS15
 */
public class TradingWindow2 extends javax.swing.JDialog {
    private int keyID  = 0;
    private int provID = 0;
    private Notate  notate;
    
    enum TradeTiming {
        BAR2, BAR4, BAR8
    }
    
    ArrayList<javax.swing.JTextArea> guiBarContainer = new ArrayList<javax.swing.JTextArea>(); // create and delete boxes.
    ArrayList<MelodyPart>               barContainer = new ArrayList<MelodyPart>(); // melodies
    ArrayList<MelodyPart>                 provisions = new ArrayList<MelodyPart>(); // bin of all
    
    public TradingWindow2(Notate notate) {
        initComponents();
        this.notate = notate;
    }
    
    public void addMelody(MelodyPart mp) {
        barContainer.add(mp);
        JTextArea jbox = new JTextArea();
        jbox.setColumns(20);
        jbox.setRows(1);
        jbox.setText(mp.toString());
        getContentPane().add(jbox, new java.awt.GridBagConstraints());
        guiBarContainer.add(jbox); // may want to consider a map or dictionary (TODO)
        keyID++;
    }
    
    public void removeMelody(MelodyPart mp) {
        barContainer.remove(mp);
        keyID--;
        guiBarContainer.remove(keyID);
        
    }
    /**
     * Creates new form TradingWindow2
     */
    public TradingWindow2(java.awt.Frame parent, boolean modal) {
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

        tradingChooser = new javax.swing.JComboBox();
        addMelodyBtn = new javax.swing.JButton();
        extractBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        tradingChooser.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Trade by 2 Bars", "Trade by 4 Bars", "Trade by 8 Bars" }));
        tradingChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tradingChooserActionPerformed(evt);
            }
        });
        getContentPane().add(tradingChooser, new java.awt.GridBagConstraints());

        addMelodyBtn.setText("Add Melody");
        addMelodyBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMelodyBtnActionPerformed(evt);
            }
        });
        getContentPane().add(addMelodyBtn, new java.awt.GridBagConstraints());

        extractBtn.setText("Extract Melodies");
        getContentPane().add(extractBtn, new java.awt.GridBagConstraints());

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tradingChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tradingChooserActionPerformed
        // TODO add your handling code here:
        final int bar = 480; // (TODO) change 480 to first be set by time signature
        int frame;
        
        if (tradingChooser.toString().contains("8")) 
            frame = 8;
        else if (tradingChooser.toString().contains("4"))
            frame = 4;
        else 
            frame = 2;
        
        System.out.println("Extracting by " + bar * frame + " frames per trade.");
        
    }//GEN-LAST:event_tradingChooserActionPerformed

    private void addMelodyBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMelodyBtnActionPerformed
        int proLen = provisions.size();
        if (proLen > provID) {
            System.out.println("Sucessful fetch and creating new output box.");
            System.out.println(provisions.get(provID).toString());
        }
    }//GEN-LAST:event_addMelodyBtnActionPerformed

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
            java.util.logging.Logger.getLogger(TradingWindow2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TradingWindow2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TradingWindow2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TradingWindow2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                TradingWindow2 dialog = new TradingWindow2(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton addMelodyBtn;
    private javax.swing.JButton extractBtn;
    private javax.swing.JComboBox tradingChooser;
    // End of variables declaration//GEN-END:variables
}