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

/**
 *
 * @author  keller
 */
public class AboutDialog extends javax.swing.JDialog {

    /** Creates new form AboutDialog */
    public AboutDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
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

        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menu2 = new java.awt.Menu();
        aboutDialogPanel = new javax.swing.JPanel();
        aboutLabel = new javax.swing.JLabel();
        aboutText = new javax.swing.JTextPane();
        okAboutBtn = new javax.swing.JButton();

        menu1.setLabel("File");
        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        setTitle("about Impro-Visor\n");
        setFocusCycleRoot(false);
        setName("aboutDialog"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        aboutDialogPanel.setBackground(new java.awt.Color(255, 204, 102));
        aboutDialogPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        aboutDialogPanel.setMinimumSize(new java.awt.Dimension(500, 900));
        aboutDialogPanel.setPreferredSize(new java.awt.Dimension(500, 900));
        aboutDialogPanel.setLayout(new java.awt.GridBagLayout());

        aboutLabel.setFont(new java.awt.Font("Dialog", 3, 36));
        aboutLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aboutLabel.setText("Impro-Visor");
        aboutLabel.setMaximumSize(new java.awt.Dimension(400, 100));
        aboutLabel.setMinimumSize(new java.awt.Dimension(400, 15));
        aboutLabel.setPreferredSize(new java.awt.Dimension(400, 15));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        aboutDialogPanel.add(aboutLabel, gridBagConstraints);

        aboutText.setBackground(new java.awt.Color(255, 204, 102));
        aboutText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        aboutText.setEditable(false);
        aboutText.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        aboutText.setText("\n  Welcome to Improvisation Advisor version 4.03 for the improviser.\n  \n  Authors: \t\n\tBob Keller, Stephen Jones, Aaron Wolin,\n\tDavid Morrison, Martin Hunt, Steven Gomez,\n\tJim Herold, Brandy McMenamy, Sayuri Soejima,\n\tEmma Carlson, Jon Gillick, Kevin Tang,\n\tStephen Lee, Chad Waters, John Goodman\n\n  For further information and tutorial, please visit the website:\n\n\thttp://www.cs.hmc.edu/~keller/jazz/improvisor\n \n  For free support, please join the Impro-Visor Yahoo! group at:\t\n\n\thttp://launch.groups.yahoo.com/group/impro-visor/\n\n   For source code, please see:\t\n\n\thttps://sourceforge.net/projects/impro-visor/\n\n  Copyright �2005-2009, Robert Keller and Harvey Mudd College \n \n  This program is free software: you can redistribute it and/or modify it\n  under the terms of the GNU General Public License as published by the\n  Free Software Foundation, either version 2 of the License, or any later\n  version. This program is distributed in the hope that it will be useful,\n  but WITHOUT ANY WARRANTY; without even the implied warranty of\n  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n  GNU General Public License for more details, which may be found at:\n\n\thttp://www.gnu.org/licenses/\n\n  We hope you enjoy using it!\n");
        aboutText.setMinimumSize(new java.awt.Dimension(400, 350));
        aboutText.setPreferredSize(new java.awt.Dimension(400, 350));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.ipadx = 4;
        gridBagConstraints.ipady = 4;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.9;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        aboutDialogPanel.add(aboutText, gridBagConstraints);

        okAboutBtn.setBackground(new java.awt.Color(153, 255, 102));
        okAboutBtn.setText("Close");
        okAboutBtn.setToolTipText("Close the About dialog.");
        okAboutBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        okAboutBtn.setMaximumSize(new java.awt.Dimension(300, 30));
        okAboutBtn.setMinimumSize(new java.awt.Dimension(300, 30));
        okAboutBtn.setOpaque(true);
        okAboutBtn.setPreferredSize(new java.awt.Dimension(300, 30));
        okAboutBtn.setSelected(true);
        okAboutBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okAboutBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 2, 2);
        aboutDialogPanel.add(okAboutBtn, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(aboutDialogPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void okAboutBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okAboutBtnActionPerformed
dispose();
}//GEN-LAST:event_okAboutBtnActionPerformed

    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AboutDialog dialog = new AboutDialog(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutDialogPanel;
    private javax.swing.JLabel aboutLabel;
    private javax.swing.JTextPane aboutText;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JButton okAboutBtn;
    // End of variables declaration//GEN-END:variables

}
