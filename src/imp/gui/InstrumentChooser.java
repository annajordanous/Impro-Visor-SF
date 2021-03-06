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
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.*;

/**
 *
 * @author  Martin
 */
public class InstrumentChooser extends javax.swing.JPanel {
    JDialog parent = null;
    static private InstrumentChooserDialog dialog = null;
    
    /** Creates new form InstrumentChooser */
    public InstrumentChooser() {
        initComponents();
        addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                instTF.setNextFocusableComponent(instBtn);
                instBtn.setNextFocusableComponent(InstrumentChooser.this.getNextFocusableComponent());
                instTF.requestFocus();
            }
            public void focusLost(FocusEvent e){}
        });
    }
    
    /**
     * This is a hack to get around NetBeans GUI builder not liking the free form project
     * and not allowing us to add custom components to the palette...
     * We set the dialog parent before the InstrumentChooserDialog is created, even though
     * this limits us to only showing the dialog with a single parent...
     * To get around this, we could just make the dialog not static.
     */
    public void setDialog(JDialog parent) {
        this.parent = parent;
        if(dialog == null)
        {
            dialog = new InstrumentChooserDialog(parent, true);
        }
    }
    
    public String getText() {
        return instTF.getText();
    }
    
    public void setText(String value) {
        try {
            instTF.setText(String.valueOf(Integer.parseInt(value)));
        } catch(NumberFormatException e) {
            instTF.setText("");
        }
        updateButton();
    }
    
    public int getValue() {
        if(instTF.getText().length() > 0) {
            try {
                return Integer.parseInt(instTF.getText());
            } catch(NumberFormatException e) {
            }
        }
        return 0;
    }
    
    public void setValue(int value) {
        instTF.setText(String.valueOf(value));
        updateButton();
    }
    
    private void updateButton() {
        if(instTF != null && instTF.getText().length() > 0) {
            instBtn.setText(dialog.doLookup(getValue()));
        }
    }
    
    private static Dimension prefferedTFSize = null;
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        instTF = new javax.swing.JTextField();
        instBtn = new javax.swing.JButton();

        setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        instTF.setMinimumSize(new java.awt.Dimension(11, 23));
        instTF.setPreferredSize(new java.awt.Dimension(50, 23));
        instTF.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                instTFFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                instTFFocusLost(evt);
            }
        });
        instTF.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                instTFCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
            }
        });
        instTF.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                instTFKeyReleased(evt);
            }
        });

        add(instTF);

        instBtn.setText(" ");
        instBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                instBtnActionPerformed(evt);
            }
        });
        instBtn.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                instBtnComponentResized(evt);
            }
        });

        add(instBtn);

    }// </editor-fold>//GEN-END:initComponents

    private void instBtnComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_instBtnComponentResized
        int height = instBtn.getHeight() > 23?instBtn.getHeight():23;
        
        if(prefferedTFSize == null || prefferedTFSize.height < height) 
            prefferedTFSize = new Dimension(50, height);
        
        instTF.setSize(50, height);
        instTF.setPreferredSize(prefferedTFSize);
    }//GEN-LAST:event_instBtnComponentResized

    private void instTFKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_instTFKeyReleased
        updateButton();
    }//GEN-LAST:event_instTFKeyReleased

    private void instTFFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_instTFFocusLost
        updateButton();
    }//GEN-LAST:event_instTFFocusLost

    private void instTFFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_instTFFocusGained
        updateButton();
    }//GEN-LAST:event_instTFFocusGained

    private void instTFCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event_instTFCaretPositionChanged
        updateButton();
    }//GEN-LAST:event_instTFCaretPositionChanged

    private void instBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_instBtnActionPerformed
        dialog.setValue(getValue());
        dialog.setTarget(instTF, instBtn);
        dialog.setVisible(true);
        // The following helps keep the default button alive,
        // but it loses the aqua highlighting after the first time.
        dialog.establishDefaultButton();
    }//GEN-LAST:event_instBtnActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton instBtn;
    private javax.swing.JTextField instTF;
    // End of variables declaration//GEN-END:variables
    
}
