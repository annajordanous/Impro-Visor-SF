/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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
 * @author  Robert Keller
 */

@SuppressWarnings("serial")

public class FirstTimeDialog extends javax.swing.JDialog
{
Notate notate;

/**
 * Creates new form AboutDialog
 */
public FirstTimeDialog(Notate notate, boolean modal)
  {
    super(notate, modal);
    this.notate = notate;
    initComponents();
  }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        menuBar1 = new java.awt.MenuBar();
        menu1 = new java.awt.Menu();
        menu2 = new java.awt.Menu();
        aboutDialogPanel = new javax.swing.JPanel();
        aboutLabel = new javax.swing.JLabel();
        aboutText = new javax.swing.JTextPane();
        openMidiPrefsBtn = new javax.swing.JButton();
        okAboutBtn = new javax.swing.JButton();

        menu1.setLabel("File");
        menuBar1.add(menu1);

        menu2.setLabel("Edit");
        menuBar1.add(menu2);

        setTitle("about Impro-Visor\n");
        setFocusCycleRoot(false);
        setName("aboutDialog"); // NOI18N
        getContentPane().setLayout(new java.awt.GridBagLayout());

        aboutDialogPanel.setBackground(new java.awt.Color(255, 255, 51));
        aboutDialogPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        aboutDialogPanel.setMinimumSize(new java.awt.Dimension(500, 900));
        aboutDialogPanel.setPreferredSize(new java.awt.Dimension(500, 900));
        aboutDialogPanel.setLayout(new java.awt.GridBagLayout());

        aboutLabel.setBackground(new java.awt.Color(255, 255, 51));
        aboutLabel.setFont(new java.awt.Font("Dialog", 3, 36)); // NOI18N
        aboutLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        aboutLabel.setText("Impro-Visor version 7.0");
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

        aboutText.setEditable(false);
        aboutText.setBackground(new java.awt.Color(255, 255, 102));
        aboutText.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        aboutText.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        aboutText.setText("  Welcome to Improvisation Advisor version 7.0.\n\n  This version improves MIDI recording capability, \n  including along with generated improvisation (\"trading\").\n\n  Tutorials: http://www.cs.hmc.edu/~keller/jazz/improvisor/tutorials.html\n\n  This message will appear only the first time you launch a new version.\n  It can be revisited from the Help menu.\n\n  On first launch, Impro-Visor will copy various files from the\n  installation directory to your home directory, in folder:\n\n       impro-visor-version-7.0-files\n\n  These include leadsheets, styles, and vocabulary. If you want to preserve \n  files from previous versions, you will need to move or copy them manually. \n  This is so Impro-Visor does not overwrite files that you might have\n  modified. When that directory is present, it tells Impro-Visor not to copy\n  the files on next launch and not to show this message again.\n  \n  To get sound, Windows users will need to set the MIDI Output Preference.\n  (You can use the button below.) It should be set to one of:\n\n        Microsoft GS Wavetable SW Synth  \n  or\n       Microsoft MIDI Mapper\n  \n  if multiple MIDI devices are installed.\n\n  Other information can be found in the release notes, in README.txt, and \n  also on the web at https://sourceforge.net/projects/impro-visor/files/\n ");
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

        openMidiPrefsBtn.setBackground(new java.awt.Color(153, 255, 102));
        openMidiPrefsBtn.setText("Open MIDI Preferences Now");
        openMidiPrefsBtn.setToolTipText("Close the About dialog.");
        openMidiPrefsBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        openMidiPrefsBtn.setMaximumSize(new java.awt.Dimension(300, 30));
        openMidiPrefsBtn.setMinimumSize(new java.awt.Dimension(300, 30));
        openMidiPrefsBtn.setOpaque(true);
        openMidiPrefsBtn.setPreferredSize(new java.awt.Dimension(300, 30));
        openMidiPrefsBtn.setSelected(true);
        openMidiPrefsBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                openMidiPrefsBtnActionPerformed(evt);
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
        aboutDialogPanel.add(openMidiPrefsBtn, gridBagConstraints);

        okAboutBtn.setBackground(new java.awt.Color(250, 0, 0));
        okAboutBtn.setText("Close");
        okAboutBtn.setToolTipText("Close the About dialog.");
        okAboutBtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        okAboutBtn.setMaximumSize(new java.awt.Dimension(300, 30));
        okAboutBtn.setMinimumSize(new java.awt.Dimension(300, 30));
        okAboutBtn.setOpaque(true);
        okAboutBtn.setPreferredSize(new java.awt.Dimension(300, 30));
        okAboutBtn.setSelected(true);
        okAboutBtn.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                okAboutBtnActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
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

private void openMidiPrefsBtnActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openMidiPrefsBtnActionPerformed
  {//GEN-HEADEREND:event_openMidiPrefsBtnActionPerformed
    dispose();
    notate.openMidiPreferences();
  }//GEN-LAST:event_openMidiPrefsBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel aboutDialogPanel;
    private javax.swing.JLabel aboutLabel;
    private javax.swing.JTextPane aboutText;
    private java.awt.Menu menu1;
    private java.awt.Menu menu2;
    private java.awt.MenuBar menuBar1;
    private javax.swing.JButton okAboutBtn;
    private javax.swing.JButton openMidiPrefsBtn;
    // End of variables declaration//GEN-END:variables
}
