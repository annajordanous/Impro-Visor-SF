/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * Impro-Visor is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of merchantability or fitness
 * for a particular purpose. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */

package imp.gui;

import imp.Constants;
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.data.*;
import imp.util.ErrorLog;
import java.awt.Color;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.DefaultListModel;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.Tokenizer;


/**
 * @author Robert Keller, Caitlin Chen
 * 
 * Use of public access to elements in MIDIBeast should
 * be changed to use proper methods.
 */

@SuppressWarnings("serial")

public class StyleMixer extends javax.swing.JDialog implements Constants
{
/**
 * name used in drum rules
 */
public static final String DRUM_SYMBOL = "drum";

Notate notate;
StyleEditor styleEditor;

/**
 * Models for the raw and selected JLists
 */
DefaultListModel rawRulesModelBass;
DefaultListModel rawRulesModelChord;
DefaultListModel rawRulesModelDrum;


/**
 * minimum duration (in slots) for a note not to be counted as a rest.
 */
private int minDuration = 0;


public static final int BASS = 0;
public static final int DRUM = 1;
public static final int CHORD = 2;

/**
 * Creates new form ExtractionEditor
 */

public StyleMixer(java.awt.Frame parent, 
                        boolean modal, 
                        StyleEditor p)
  {
    super(parent, modal);
    this.styleEditor = p;
    this.notate = p.getNotate();
    
    rawRulesModelBass       = new DefaultListModel();
    rawRulesModelChord      = new DefaultListModel();
    rawRulesModelDrum       = new DefaultListModel();

    initComponents();
    initComponents2();
    setSize(900, 425);

    SpinnerModel model = new SpinnerNumberModel(1, 1, 100, 1);
    loadStyleMixerPatterns();
  }

public void setBass()
  {
    styleMixerPanel.setBackground(Color.orange);
    setBassRawRules();
  }

public void setChords()
  {
    setChordRawRules();
  }

public void setDrums()
  {
    setDrumRawRules();
  }



public void setBassRawRules()
  {
    rawRulesModelBass.clear();
    rawRulesJListBass.setModel(rawRulesModelBass);
  }

public void setChordRawRules()
  {
    rawRulesModelChord.clear();
    rawRulesJListChord.setModel(rawRulesModelChord);
  }

public void setDrumRawRules()
  {
    rawRulesModelDrum.clear();    
    rawRulesJListDrum.setModel(rawRulesModelDrum);
   }

//private RepresentativeDrumRules.DrumPattern makeDrumPattern(String string)
//  {
//    String[] split = string.split("\n");
//    RepresentativeDrumRules.DrumPattern drumPattern = repDrumRules.makeDrumPattern();
//    for( int i = 1; i < split.length - 1; i++ )
//      {
//        RepresentativeDrumRules.DrumRule drumRule = repDrumRules.makeDrumRule();
//        int instrumentNumber = Integer.parseInt(split[i].substring(split[i].indexOf('m') + 2, split[i].indexOf('m') + 4));
//        drumRule.setInstrumentNumber(instrumentNumber);
//        int startIndex = split[i].indexOf('m') + 2;
//        int endIndex = split[i].indexOf(')');
//        String elements = split[i].substring(startIndex, endIndex);
//        String[] split2 = elements.split(" ");
//        // Start at 1 rather than 0, to skip over the drum number
//        for( int j = 1; j < split2.length; j++ )
//          {
//            drumRule.addElement(split2[j]);
//          }
//        String weightString = split[split.length - 1];
//
//        drumPattern.setWeight(1);
//        //System.out.println("adding drumPattern " + drumPattern);
//        drumPattern.addRule(drumRule);
//      }
//    return drumPattern;
//  }


private void initComponents2()
  {
    java.awt.GridBagConstraints gridBagConstraints;

    errorDialog = new javax.swing.JDialog();
    errorMessage = new javax.swing.JLabel();
    errorButton = new javax.swing.JButton();

    errorDialog.getContentPane().setLayout(new java.awt.GridBagLayout());

    errorDialog.setTitle("Error");
    errorDialog.setBackground(java.awt.Color.white);
    errorMessage.setForeground(new java.awt.Color(255, 0, 51));
    errorMessage.setText("jLabel1");
    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.insets = new java.awt.Insets(8, 0, 0, 0);
    errorDialog.getContentPane().add(errorMessage, gridBagConstraints);

    errorButton.setText("OK");
    errorButton.addActionListener(new java.awt.event.ActionListener()
    {

    public void actionPerformed(java.awt.event.ActionEvent evt)
      {
        errorButtonActionPerformed(evt);
      }
    });
  }

private void errorButtonActionPerformed(java.awt.event.ActionEvent evt)
  {//GEN-FIRST:event_errorButtonActionPerformed
    errorDialog.setVisible(false);
  }//GEN-LAST:event_errorButtonActionPerformed



/**
 * This method is called from within the constructor to initialize the form.
 * WARNING: Do NOT modify this code. The content of this method is always
 * regenerated by the Form Editor.
 */
@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        styleMixerPanel = new javax.swing.JPanel();
        rawPatternsPanelBass = new javax.swing.JScrollPane();
        rawRulesJListBass = new javax.swing.JList();
        playPatternBtnBass = new javax.swing.JButton();
        selectPatternBtnBass = new javax.swing.JButton();
        deletePatternBtnBass = new javax.swing.JButton();
        rawPatternsPanelChord = new javax.swing.JScrollPane();
        rawRulesJListChord = new javax.swing.JList();
        selectPatternBtnChord = new javax.swing.JButton();
        playPatternBtnChord = new javax.swing.JButton();
        deletePatternBtnChord = new javax.swing.JButton();
        rawPatternsPanelDrum = new javax.swing.JScrollPane();
        rawRulesJListDrum = new javax.swing.JList();
        selectPatternBtnDrum = new javax.swing.JButton();
        playPatternBtnDrum = new javax.swing.JButton();
        deletePatternBtnDrum = new javax.swing.JButton();
        extractionEditorMenuBar = new javax.swing.JMenuBar();
        windowMenu = new javax.swing.JMenu();
        closeWindowMI = new javax.swing.JMenuItem();
        cascadeMI = new javax.swing.JMenuItem();
        windowMenuSeparator = new javax.swing.JSeparator();

        setTitle("Style Mixer");
        setMinimumSize(new java.awt.Dimension(800, 600));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        styleMixerPanel.setLayout(new java.awt.GridBagLayout());

        rawPatternsPanelBass.setBorder(javax.swing.BorderFactory.createTitledBorder("Bass Patterns"));
        rawPatternsPanelBass.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelBass.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListBass.setBackground(java.awt.Color.orange);
        rawRulesJListBass.setModel(rawRulesModelBass);
        rawRulesJListBass.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListBassrawRulesJListMouseClickedChord(evt);
            }
        });
        rawPatternsPanelBass.setViewportView(rawRulesJListBass);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.9;
        styleMixerPanel.add(rawPatternsPanelBass, gridBagConstraints);

        playPatternBtnBass.setText("Play Bass Pattern");
        playPatternBtnBass.setPreferredSize(new java.awt.Dimension(300, 23));
        playPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnBassActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(playPatternBtnBass, gridBagConstraints);

        selectPatternBtnBass.setText("Copy Bass Pattern to Style Editor");
        selectPatternBtnBass.setToolTipText("Move the selected Bass Pattern to the next column of the Style Editor.");
        selectPatternBtnBass.setPreferredSize(new java.awt.Dimension(300, 23));
        selectPatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyBassPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(selectPatternBtnBass, gridBagConstraints);

        deletePatternBtnBass.setText("Delete Bass Pattern");
        deletePatternBtnBass.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteBassPattern(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(deletePatternBtnBass, gridBagConstraints);

        rawPatternsPanelChord.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord Patterns"));
        rawPatternsPanelChord.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelChord.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListChord.setBackground(java.awt.Color.green);
        rawRulesJListChord.setModel(rawRulesModelChord);
        rawRulesJListChord.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListChordsMouseClicked(evt);
            }
        });
        rawPatternsPanelChord.setViewportView(rawRulesJListChord);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.2;
        gridBagConstraints.weighty = 0.9;
        styleMixerPanel.add(rawPatternsPanelChord, gridBagConstraints);

        selectPatternBtnChord.setText("Copy Chord Pattern to Style Editor");
        selectPatternBtnChord.setToolTipText("Move the selected Chord Pattern to the next column of the Style Editor.");
        selectPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyChordPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(selectPatternBtnChord, gridBagConstraints);

        playPatternBtnChord.setText("Play Chord Pattern");
        playPatternBtnChord.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        playPatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnChordActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(playPatternBtnChord, gridBagConstraints);

        deletePatternBtnChord.setText("Delete Chord Pattern");
        deletePatternBtnChord.setToolTipText("Delete Chord Pattern");
        deletePatternBtnChord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteChordPattern(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(deletePatternBtnChord, gridBagConstraints);

        rawPatternsPanelDrum.setBorder(javax.swing.BorderFactory.createTitledBorder("Drum Patterns"));
        rawPatternsPanelDrum.setMinimumSize(new java.awt.Dimension(300, 200));
        rawPatternsPanelDrum.setPreferredSize(new java.awt.Dimension(300, 200));

        rawRulesJListDrum.setBackground(java.awt.Color.yellow);
        rawRulesJListDrum.setModel(rawRulesModelDrum);
        rawRulesJListDrum.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rawRulesJListDrumrawPatternsMouseClickedBass(evt);
            }
        });
        rawPatternsPanelDrum.setViewportView(rawRulesJListDrum);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 0.6;
        gridBagConstraints.weighty = 0.9;
        styleMixerPanel.add(rawPatternsPanelDrum, gridBagConstraints);

        selectPatternBtnDrum.setText("Copy Drum Pattern to Style Editor");
        selectPatternBtnDrum.setToolTipText("Move the selected Drum Pattern to the next column of the Style Editor.");
        selectPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyDrumPatternToStyleEditor(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(selectPatternBtnDrum, gridBagConstraints);

        playPatternBtnDrum.setText("Play Drum Pattern");
        playPatternBtnDrum.setToolTipText("Play the selected pattern (also can achieve with a double-click).");
        playPatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playPatternBtnDrumActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.33;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(playPatternBtnDrum, gridBagConstraints);

        deletePatternBtnDrum.setText("Delete Drum Pattern");
        deletePatternBtnDrum.setToolTipText("Delete Drum Pattern");
        deletePatternBtnDrum.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteDrumPattern(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weighty = 0.05;
        styleMixerPanel.add(deletePatternBtnDrum, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(styleMixerPanel, gridBagConstraints);

        windowMenu.setMnemonic('W');
        windowMenu.setText("Window");
        windowMenu.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                windowMenuMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        closeWindowMI.setMnemonic('C');
        closeWindowMI.setText("Close Window");
        closeWindowMI.setToolTipText("Closes the current window (exits program if there are no other windows)");
        closeWindowMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeWindowMIActionPerformed(evt);
            }
        });
        windowMenu.add(closeWindowMI);

        cascadeMI.setMnemonic('A');
        cascadeMI.setText("Cascade Windows");
        cascadeMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cascadeMIActionPerformed(evt);
            }
        });
        windowMenu.add(cascadeMI);
        windowMenu.add(windowMenuSeparator);

        extractionEditorMenuBar.add(windowMenu);

        setJMenuBar(extractionEditorMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void closeWindowMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_closeWindowMIActionPerformed
  {//GEN-HEADEREND:event_closeWindowMIActionPerformed
    dispose();
  }//GEN-LAST:event_closeWindowMIActionPerformed

private void cascadeMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cascadeMIActionPerformed
  {//GEN-HEADEREND:event_cascadeMIActionPerformed
    WindowRegistry.cascadeWindows(this);
  }//GEN-LAST:event_cascadeMIActionPerformed

private void windowMenuMenuSelected(javax.swing.event.MenuEvent evt)//GEN-FIRST:event_windowMenuMenuSelected
  {//GEN-HEADEREND:event_windowMenuMenuSelected
    windowMenu.removeAll();

    windowMenu.add(closeWindowMI);

    windowMenu.add(cascadeMI);

    windowMenu.add(windowMenuSeparator);

    for( WindowMenuItem w : WindowRegistry.getWindows() )
      {
        windowMenu.add(w.getMI(this));      // these are static, and calling getMI updates the name on them too in case the window title changed
      }

    windowMenu.repaint();
  }//GEN-LAST:event_windowMenuMenuSelected

    private void playPatternBtnDrumActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnDrumActionPerformed
        
    }//GEN-LAST:event_playPatternBtnDrumActionPerformed

    private void copyDrumPatternToStyleEditor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyDrumPatternToStyleEditor
        Object selectedOb = rawRulesJListDrum.getSelectedValue();
        if (selectedOb instanceof String) 
          {
            //widePatternTextField.setText(selectedOb.toString());
            styleEditor.setNextDrumPattern((String)selectedOb);
          }
    }//GEN-LAST:event_copyDrumPatternToStyleEditor

    private void playPatternBtnChordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnChordActionPerformed
        Polylist polylist = (Polylist)rawRulesJListChord.getSelectedValue();
        if( polylist == null )
          {
            return;
          }
        String pattern = polylist.toStringSansParens();
        if( pattern != null )
          {
          playPattern(CHORD, pattern);
          }          
    }//GEN-LAST:event_playPatternBtnChordActionPerformed

    private void copyChordPatternToStyleEditor(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyChordPatternToStyleEditor
        Object selectedOb = rawRulesJListChord.getSelectedValue();
        if (selectedOb instanceof String) 
          {
            //widePatternTextField.setText(selectedOb.toString());
            styleEditor.setNextChordPattern((String)selectedOb);
          }
    }//GEN-LAST:event_copyChordPatternToStyleEditor

    private void rawRulesJListDrumrawPatternsMouseClickedBass(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawRulesJListDrumrawPatternsMouseClickedBass
        Object selectedOb = rawRulesJListDrum.getSelectedValue();

    }//GEN-LAST:event_rawRulesJListDrumrawPatternsMouseClickedBass

    private void rawRulesJListChordsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawRulesJListChordsMouseClicked
        playPatternBtnChordActionPerformed(null);
    }//GEN-LAST:event_rawRulesJListChordsMouseClicked

    private void rawRulesJListBassrawRulesJListMouseClickedChord(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_rawRulesJListBassrawRulesJListMouseClickedChord
         playPatternBtnBassActionPerformed(null);
    }//GEN-LAST:event_rawRulesJListBassrawRulesJListMouseClickedChord

    private void playPatternBtnBassActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playPatternBtnBassActionPerformed
        Polylist polylist = (Polylist)rawRulesJListBass.getSelectedValue();
        if( polylist == null )
          {
            return;
          }
        String pattern = polylist.toStringSansParens();
        if( pattern != null )
          {
          playPattern(BASS, pattern);
          }
    }//GEN-LAST:event_playPatternBtnBassActionPerformed

private void copyBassPatternToStyleEditor(java.awt.event.ActionEvent evt)//GEN-FIRST:event_copyBassPatternToStyleEditor
  {//GEN-HEADEREND:event_copyBassPatternToStyleEditor
        Object selectedOb = rawRulesJListBass.getSelectedValue();
  //System.out.println("selected " + selectedOb);
        if (selectedOb instanceof String) 
          {
            //widePatternTextField.setText(selectedOb.toString());
            styleEditor.setNextBassPattern((String)selectedOb);
          }
  }//GEN-LAST:event_copyBassPatternToStyleEditor

    private void deleteBassPattern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteBassPattern
    
  rawRulesModelBass.removeElement(rawRulesJListBass.getSelectedValue());
   saveStylePatterns();  
    }//GEN-LAST:event_deleteBassPattern

    private void deleteChordPattern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteChordPattern
       rawRulesModelChord.removeElement(rawRulesJListChord.getSelectedValue());
        saveStylePatterns();
    }//GEN-LAST:event_deleteChordPattern

    private void deleteDrumPattern(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteDrumPattern
        rawRulesModelDrum.removeElement(rawRulesJListDrum.getSelectedValue());
         saveStylePatterns();
    }//GEN-LAST:event_deleteDrumPattern


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem cascadeMI;
    private javax.swing.JMenuItem closeWindowMI;
    private javax.swing.JButton deletePatternBtnBass;
    private javax.swing.JButton deletePatternBtnChord;
    private javax.swing.JButton deletePatternBtnDrum;
    private javax.swing.JMenuBar extractionEditorMenuBar;
    private javax.swing.JButton playPatternBtnBass;
    private javax.swing.JButton playPatternBtnChord;
    private javax.swing.JButton playPatternBtnDrum;
    private javax.swing.JScrollPane rawPatternsPanelBass;
    private javax.swing.JScrollPane rawPatternsPanelChord;
    private javax.swing.JScrollPane rawPatternsPanelDrum;
    private javax.swing.JList rawRulesJListBass;
    private javax.swing.JList rawRulesJListChord;
    private javax.swing.JList rawRulesJListDrum;
    private javax.swing.JButton selectPatternBtnBass;
    private javax.swing.JButton selectPatternBtnChord;
    private javax.swing.JButton selectPatternBtnDrum;
    private javax.swing.JPanel styleMixerPanel;
    private javax.swing.JMenu windowMenu;
    private javax.swing.JSeparator windowMenuSeparator;
    // End of variables declaration//GEN-END:variables

private javax.swing.JButton errorButton;
private javax.swing.JDialog errorDialog;
private javax.swing.JLabel errorMessage;

  /**
   * Override dispose so as to unregister this window first.
   */
  
  @Override
  public void dispose()
    {
    WindowRegistry.unregisterWindow(this);
    super.dispose();
    }
  
/**
 * Copy a rectangle of cells for copying to the Style Mixer
 * @param cells
 * @param rowNumber
 * @param instrumentName 
 */
public void copyCellsForStyleMixer(Polylist cells, int rowNumber, String instrumentName[])
  {
    // cells are organized by column, so put each column into an array 
    // element.
    
    Polylist column[] = new Polylist[cells.length()];
    
    int j = 0;
    while( cells.nonEmpty() )
      {
        column[j++] = (Polylist)cells.first();
        cells = cells.rest();
      }
    
    int numColumns = j;
    
    int numRows = numColumns > 0 ? column[0].length() : 0;
    
    //System.out.println(numRows + " rows, " + numColumns + " columns");
    
    // Buffers for concatenating any drum rules by column
    PolylistBuffer buffer[] = new PolylistBuffer[numColumns];
    
    for( j = 0; j < numColumns; j++ )
      {
         buffer[j] = new PolylistBuffer();
       }

    for( int i = 0; i < numRows; i++ )
      {
        for( j = 0; j < numColumns; j++ )
          {
            int trueRow = rowNumber + i;
            Polylist item = (Polylist)column[j].first();
            
            if( item.nonEmpty() && !item.toString().equals("()") )
              {
              //System.out.println("row " + trueRow + ", column " + j + ": " + item);
              switch(trueRow)
                {
                case StyleTableModel.BASS_PATTERN_ROW:
                    if( !containsAsString(rawRulesModelBass, item) )
                    {
                    rawRulesModelBass.addElement(item);
                    }
                 break;
                  
                case StyleTableModel.CHORD_PATTERN_ROW:
                    if( !containsAsString(rawRulesModelChord, item) )
                    {
                    rawRulesModelChord.addElement(item);
                    }
                 break;
                  
                default:
                 // Buffer drum rules as belonging to a paJttern in a given
                 // column. At the end of transfer, create drum patterns out
                 // of rules stored in a specific buffer.
                 if( trueRow >= StyleTableModel.FIRST_PERCUSSION_INSTRUMENT_ROW)
                      {
                      buffer[j].append(item.cons(instrumentName[i]).cons(DRUM_SYMBOL));    
                      }
                }
              }
            
            column[j] = column[j].rest();
          }
      }
    
    for( j = 0; j < numColumns; j++ )
      {
        Polylist L = buffer[j].toPolylist();
        if( L.nonEmpty() )
          {
            if ( !containsAsString(rawRulesModelDrum, L) )
            {
             rawRulesModelDrum.addElement(L);
            }
          }
      }
    saveStylePatterns();
  }

  public void saveStylePatterns()
    {
    String eol = System.getProperty( "line.separator" );
  
    File file = ImproVisor.getStyleMixerFile();
    try
      {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));

      StringBuilder buffer = new StringBuilder();
        
      for( Enumeration e = rawRulesModelBass.elements(); e.hasMoreElements(); )
      {
          buffer.append("(bass-pattern ");
          buffer.append(e.nextElement().toString());
          buffer.append(")");
          buffer.append(eol);
      }
      
      for( Enumeration e = rawRulesModelChord.elements(); e.hasMoreElements(); )
      {
          buffer.append("(chord-pattern ");
          buffer.append(e.nextElement().toString());
          buffer.append(")");
          buffer.append(eol);
      }
            
      for( Enumeration e = rawRulesModelDrum.elements(); e.hasMoreElements(); )
      {
          buffer.append("(drum-pattern ");
          buffer.append((e.nextElement()).toString().substring(1));
          buffer.append(eol);
      }            
      
      String styleResult = buffer.toString();
      
      System.out.println("Saving mixer patterns to file: " + eol + styleResult);
      
      out.write(styleResult);
      out.close();

       }
    catch( Exception e )
      {
      }
    }
  
  private void loadStyleMixerPatterns()
  {
  File mixerFile = ImproVisor.getStyleMixerFile();
  try
    {
    FileInputStream fis = new FileInputStream(mixerFile);
    Tokenizer in = new Tokenizer(fis);
    Object token;
         
    // Read in S expressions until end of file is reached
    while ((token = in.nextSexp()) != Tokenizer.eof)
     {
      //System.out.println("token = " + token);
      Polylist tokenP = (Polylist)token;
      if(tokenP.first().equals("bass-pattern"))
        {
          rawRulesModelBass.addElement(tokenP.second());
        }
      else if(tokenP.first().equals("chord-pattern"))
        {
          rawRulesModelChord.addElement(tokenP.second());
        }
      else if(tokenP.first().equals("drum-pattern"))
        {
          rawRulesModelDrum.addElement(tokenP.rest());
        }   
    }
  }
  catch( java.io.FileNotFoundException e )
        { 
            ErrorLog.log(ErrorLog.WARNING, "StyleMixer file not found");
        }
  }
  
private void playPattern(int type, String string)
  {
   PatternDisplay display;
   switch( type )
     {
       case BASS:
           display = new BassPatternDisplay(string, 1.0f, styleEditor.getNotate(), null, styleEditor);
           //System.out.println("display = " + display);
           display.playMe();
           break;
           
        case CHORD:
           display = new ChordPatternDisplay(string, 1.0f, "", styleEditor.getNotate(), null, styleEditor);
           //System.out.println("display = " + display);
           display.playMe();
           break;
           
           
       case DRUM:
     }
  }

/**
 * Checks to see if model contains p, using String equivalence as a basis
 * of comparison.
 * @param model
 * @param p
 * @return 
 */
boolean containsAsString(DefaultListModel model, Polylist p)
  {
    String s = p.toString();
    for( Enumeration e = model.elements(); e.hasMoreElements(); )
      {
        if( s.equals(e.nextElement().toString() ))
          {
            return true;
          }
      }
    return false;
  }
}
