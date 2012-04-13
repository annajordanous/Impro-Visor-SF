/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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
import imp.data.MelodyPart;
import imp.data.MidiImport;
import imp.data.MidiImportRecord;
import imp.data.Score;
import java.util.LinkedList;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.DefaultListModel;
import jm.midi.MidiSynth;

/**
 *
 * @author keller
 */
public class MidiImportFrame extends javax.swing.JFrame implements Constants
{
Notate notate;
MidiImport midiImport;
DefaultListModel trackListModel;
MelodyPart selectedPart = null;

/**
 * Note that this is a jMusic MidiSynth and not an Impro-Visor MidiSynth.
 * We also use a jMusic score, in addition to an Impro-Visor score later.
 */

MidiSynth jmSynth;
jm.music.data.Score jmScore;

/**
 * Creates new form MidiImportFrame
 */
public MidiImportFrame(Notate notate, MidiImport midiImport)
  {
    trackListModel = new DefaultListModel();
    initComponents();
    this.notate = notate;
    this.midiImport = midiImport;
    setTitle("MIDI Import: " + midiImport.getFilenameDisplay());
    WindowRegistry.registerWindow(this);
    
    volumeSpinner.setVisible(false); // doesn't work yet
  }

public void load(LinkedList<MidiImportRecord> records)
  {
    int saveIndex = importedTrackList.getSelectedIndex();
    //System.out.println("loading");
    trackListModel.clear();
    
    int channelNumber = 0;
    for( final MidiImportRecord record: records)
      {
        if(record.getChannel() > channelNumber )
          {
            trackListModel.addElement("-------------------------------------");
            channelNumber = record.getChannel();
          }
        trackListModel.addElement(record);
      }
    importedTrackList.setSelectedIndex(saveIndex);
  }

private void reload()
  {
    LinkedList<MidiImportRecord> records = midiImport.reImportMidi();
    if( records != null )
      {
        load(records);
      }
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

        midiImportTopPanel = new javax.swing.JPanel();
        selectTracksLabel = new javax.swing.JLabel();
        trackSelectScrollPane = new javax.swing.JScrollPane();
        importedTrackList = new javax.swing.JList();
        midiImportButtonPanel = new javax.swing.JPanel();
        playMIDIfile = new javax.swing.JButton();
        playMIDIimportTrack = new javax.swing.JButton();
        stopPlayingTrackButton = new javax.swing.JButton();
        importTrackToLeadsheet = new javax.swing.JButton();
        importResolutionComboBox = new javax.swing.JComboBox();
        volumeSpinner = new javax.swing.JSpinner();
        startBeatSpinner = new javax.swing.JSpinner();
        endBeatSpinner = new javax.swing.JSpinner();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        midiImportTopPanel.setLayout(new java.awt.GridBagLayout());

        selectTracksLabel.setBackground(new java.awt.Color(153, 255, 0));
        selectTracksLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        selectTracksLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selectTracksLabel.setText("Please select the tracks to be imported one at a time. Each track will be put in a separate chorus in the leadsheet.");
        selectTracksLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectTracksLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        midiImportTopPanel.add(selectTracksLabel, gridBagConstraints);

        getContentPane().add(midiImportTopPanel, new java.awt.GridBagConstraints());

        trackSelectScrollPane.setMinimumSize(new java.awt.Dimension(400, 100));
        trackSelectScrollPane.setOpaque(false);
        trackSelectScrollPane.setPreferredSize(new java.awt.Dimension(600, 300));

        importedTrackList.setFont(new java.awt.Font("Lucida Grande", 0, 14)); // NOI18N
        importedTrackList.setModel(trackListModel);
        importedTrackList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        importedTrackList.setToolTipText("Select the track to be imported");
        importedTrackList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                importTrackSelected(evt);
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                importedTrackListMouseClicked(evt);
            }
        });
        trackSelectScrollPane.setViewportView(importedTrackList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.8;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        getContentPane().add(trackSelectScrollPane, gridBagConstraints);

        midiImportButtonPanel.setLayout(new java.awt.GridBagLayout());

        playMIDIfile.setText("Play MIDI File");
        playMIDIfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMIDIfileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        midiImportButtonPanel.add(playMIDIfile, gridBagConstraints);

        playMIDIimportTrack.setText("Play Selected Track (or triple click)");
        playMIDIimportTrack.setToolTipText("Plays the selected MIDI track. Alternatively, triple click the entry.");
        playMIDIimportTrack.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMIDIimportTrackActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        midiImportButtonPanel.add(playMIDIimportTrack, gridBagConstraints);

        stopPlayingTrackButton.setText("Stop");
        stopPlayingTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayingTrackButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        midiImportButtonPanel.add(stopPlayingTrackButton, gridBagConstraints);

        importTrackToLeadsheet.setText("Import Selected Track to Leadsheet (or double click)");
        importTrackToLeadsheet.setToolTipText("Imports the track selected above to the leadsheet as a new chorus. Alternatively, double click the entry.");
        importTrackToLeadsheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importTrackToLeadsheetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        midiImportButtonPanel.add(importTrackToLeadsheet, gridBagConstraints);

        importResolutionComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "480", "360", "240", "120", "80", "60", "40", "30", "20", "15", "10", "5", "1" }));
        importResolutionComboBox.setSelectedIndex(8);
        importResolutionComboBox.setToolTipText("Select the highest number of slots that gives satisfactory results. Low numbers take more memory and may fail.");
        importResolutionComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution in Slots", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        importResolutionComboBox.setMinimumSize(new java.awt.Dimension(180, 50));
        importResolutionComboBox.setPreferredSize(new java.awt.Dimension(180, 50));
        importResolutionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMidiNoteResolutionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(importResolutionComboBox, gridBagConstraints);

        volumeSpinner.setModel(new javax.swing.SpinnerNumberModel(70, 0, 127, 5));
        volumeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11))); // NOI18N
        volumeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeSpinnerChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        midiImportButtonPanel.add(volumeSpinner, gridBagConstraints);

        startBeatSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        startBeatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder("Start Beat"));
        startBeatSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        startBeatSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        midiImportButtonPanel.add(startBeatSpinner, gridBagConstraints);

        endBeatSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        endBeatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder("End Beat"));
        endBeatSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        endBeatSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        midiImportButtonPanel.add(endBeatSpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        getContentPane().add(midiImportButtonPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void importTrackSelected(java.awt.event.MouseEvent evt)//GEN-FIRST:event_importTrackSelected
  {//GEN-HEADEREND:event_importTrackSelected
 
  }//GEN-LAST:event_importTrackSelected

private void playMIDIimportTrackActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playMIDIimportTrackActionPerformed
  {//GEN-HEADEREND:event_playMIDIimportTrackActionPerformed
    playSelectedTrack();
  }//GEN-LAST:event_playMIDIimportTrackActionPerformed

private void importTrackToLeadsheetActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_importTrackToLeadsheetActionPerformed
  {//GEN-HEADEREND:event_importTrackToLeadsheetActionPerformed
    importSelectedTrack();
  }//GEN-LAST:event_importTrackToLeadsheetActionPerformed

private void importMidiNoteResolutionChanged(java.awt.event.ActionEvent evt)//GEN-FIRST:event_importMidiNoteResolutionChanged
  {//GEN-HEADEREND:event_importMidiNoteResolutionChanged
    reImportWithNewResolution();
  }//GEN-LAST:event_importMidiNoteResolutionChanged

private void importedTrackListMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_importedTrackListMouseClicked
  {//GEN-HEADEREND:event_importedTrackListMouseClicked
    getFullSelectedTrackMelody(); // This serves to set endBeatSpinner
    switch(evt.getClickCount() )
      { case 1: break;
        case 2: importSelectedTrack(); break;
        case 3: playSelectedTrack();   break;
      }
  }//GEN-LAST:event_importedTrackListMouseClicked

private void stopPlayingTrackButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopPlayingTrackButtonActionPerformed
  {//GEN-HEADEREND:event_stopPlayingTrackButtonActionPerformed
    // Stop both possible synths, the track one and the score one.
    notate.stopPlayAscore();
    if( jmSynth != null )
      {
        jmSynth.stop();
      }
  }//GEN-LAST:event_stopPlayingTrackButtonActionPerformed

private void playMIDIfileActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_playMIDIfileActionPerformed
  {//GEN-HEADEREND:event_playMIDIfileActionPerformed
  try
    {
    jmScore = midiImport.getScore();
     setJmVolume();
    if( jmSynth != null )
      {
        jmSynth.stop();
      }
   jmSynth = new jm.midi.MidiSynth();
   jmSynth.play(jmScore);
    }
  catch( InvalidMidiDataException e )
    {
      
    }
  }//GEN-LAST:event_playMIDIfileActionPerformed

private void volumeSpinnerChanged(javax.swing.event.ChangeEvent evt)//GEN-FIRST:event_volumeSpinnerChanged
  {//GEN-HEADEREND:event_volumeSpinnerChanged
    setJmVolume();
  }//GEN-LAST:event_volumeSpinnerChanged

private void setJmVolume()
  {
   int value = (Integer)volumeSpinner.getValue();
   if( jmScore != null )
     {
     jmScore.setVolume(value);
   
     //System.out.println("jmVolume = " + jmScore.getVolume());
     }
  }

private void reImportWithNewResolution()
  {
    int newResolution = Integer.parseInt((String)importResolutionComboBox.getSelectedItem());
    midiImport.setResolution(newResolution);
    reload();
  }

private void getFullSelectedTrackMelody()
  {
    int index = importedTrackList.getSelectedIndex();
    if( index < 0 )
      {
        return;
      }
    Object ob = trackListModel.get(index);
    
    if( ob instanceof MidiImportRecord )
      {
       MidiImportRecord record = (MidiImportRecord)ob;
       selectedPart = record.getPart();
       int numBeats = record.getBeats();
       endBeatSpinner.setValue(numBeats);
       ((javax.swing.SpinnerNumberModel)endBeatSpinner.getModel()).setMaximum(numBeats);
       } 
  }

private MelodyPart getSelectedTrackMelody()
  {
    if( selectedPart != null )
      {
      // Note that these expression are not the same form, as the second
      // has to add a whole beat to get to the last slot.
      int startSlot = BEAT*(((Integer)startBeatSpinner.getValue())-1);
      int endSlot = BEAT*((Integer)endBeatSpinner.getValue())-1;
      
      return selectedPart.copy(startSlot, endSlot);
      } 
    return null;
  }

private void importSelectedTrack()
  {
    MelodyPart part = getSelectedTrackMelody();
    
    if( part != null )
      {
        notate.addChorus(part);
        notate.requestFocusInWindow();
      }
  }


private void playSelectedTrack()
  {
   MelodyPart part = getSelectedTrackMelody();
    
    if( part != null )
      {
        Score score = new Score();
        score.addPart(part);
        //System.out.println("score = " + score);
        notate.playAscore(score);
      }
  }

@Override
public void dispose()
  {
    WindowRegistry.unregisterWindow(this);
    super.dispose();
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSpinner endBeatSpinner;
    private javax.swing.JComboBox importResolutionComboBox;
    private javax.swing.JButton importTrackToLeadsheet;
    private javax.swing.JList importedTrackList;
    private javax.swing.JPanel midiImportButtonPanel;
    private javax.swing.JPanel midiImportTopPanel;
    private javax.swing.JButton playMIDIfile;
    private javax.swing.JButton playMIDIimportTrack;
    private javax.swing.JLabel selectTracksLabel;
    private javax.swing.JSpinner startBeatSpinner;
    private javax.swing.JButton stopPlayingTrackButton;
    private javax.swing.JScrollPane trackSelectScrollPane;
    private javax.swing.JSpinner volumeSpinner;
    // End of variables declaration//GEN-END:variables
}
