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

package imp.util;

import javax.swing.*;
import javax.swing.plaf.metal.*;
import java.awt.*;
import imp.gui.*;

/**
 *
 * @author  dmorrison
 */
public class SplashDialog extends JDialog {
    SplashPanel sp;
    Image splash;
    /** Creates new form SplashDialog */
    public SplashDialog(JFrame parent, boolean modal, boolean showSplash) {
        super(parent, modal);
        initComponents();
        try {
            UIManager.put("ProgressBar.repaintInterval", new Integer(50));
            UIManager.put("ProgressBar.cycleTime", new Integer(5000));
            loadProgressBar.setUI(new MetalProgressBarUI());
        } catch (Exception e) {
        }
        if (showSplash) {
            
            splash = ToolkitImages.getInstance().getSplash();
            sp = new SplashPanel();
            sp.setSize(300, 200);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 1.0;
            gbc.weighty = 1.0;
            gbc.insets = new Insets(2,2,0,2);
            gbc.fill = GridBagConstraints.BOTH;
            getContentPane().add(sp, gbc);
            getContentPane().setBackground(Color.black);
            setSize(300,210);
        } else {
            setSize(150, 50);
        }
    }
    
    public class SplashPanel extends JPanel {
        public SplashPanel() {
        }
        
        public void paintComponent(Graphics g) {
            super.paintComponents(g);
            g.drawImage(splash, 0, 0, 300, 200, this);
        }
    }
    
    public void setText(String s) {
        loadString.setText(s);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        loadProgressBar = new javax.swing.JProgressBar();
        loadString = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Loading Impro-Visor");
        setBackground(new java.awt.Color(0, 0, 0));
        setFocusCycleRoot(false);
        setName("SplashDialog"); // NOI18N
        setResizable(false);
        setUndecorated(true);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        loadProgressBar.setBackground(new java.awt.Color(0, 0, 0));
        loadProgressBar.setForeground(new java.awt.Color(65, 121, 97));
        loadProgressBar.setBorderPainted(false);
        loadProgressBar.setIndeterminate(true);
        loadProgressBar.setMinimumSize(new java.awt.Dimension(208, 10));
        loadProgressBar.setPreferredSize(new java.awt.Dimension(208, 10));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.5;
        getContentPane().add(loadProgressBar, gridBagConstraints);

        loadString.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        loadString.setText("Loading Vocabulary ...");
        loadString.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(180, 0, 0, 0);
        getContentPane().add(loadString, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JProgressBar loadProgressBar;
    private javax.swing.JLabel loadString;
    // End of variables declaration//GEN-END:variables
    
}
