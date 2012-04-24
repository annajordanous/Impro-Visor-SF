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
import imp.data.*;
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
    selectTrack(0);
  }

private void reload()
  {
    int saveIndex = importedTrackList.getSelectedIndex();
    LinkedList<MidiImportRecord> records = midiImport.reImportMidi();
    if( records != null )
      {
        load(records);
        selectTrack(saveIndex);
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
        volumeSpinner = new javax.swing.JSpinner();
        startBeatSpinner = new javax.swing.JSpinner();
        endBeatSpinner = new javax.swing.JSpinner();
        offsetSpinner = new javax.swing.JSpinner();
        importResolutionComboBox = new javax.swing.JComboBox();
        startRoundingFactorComboBox = new javax.swing.JComboBox();

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
        importedTrackList.setToolTipText("These are all channels and tracks from the imported MIDI file. Select the one you wish to play or import to the leadsheet. Doube clicking imports the track, or use the button below.");
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
        playMIDIfile.setToolTipText("CAUTION: This will play the complete MIDI file at full volume. Consider reducing your computer audio volume first.");
        playMIDIfile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playMIDIfileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        midiImportButtonPanel.add(playMIDIfile, gridBagConstraints);

        playMIDIimportTrack.setText("Play Track");
        playMIDIimportTrack.setToolTipText("Plays the selected MIDI track individually, not in the leadsheet context.");
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
        stopPlayingTrackButton.setToolTipText("Stop playback of either full MIDI file or selected track, whichever is playing.");
        stopPlayingTrackButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                stopPlayingTrackButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        midiImportButtonPanel.add(stopPlayingTrackButton, gridBagConstraints);

        importTrackToLeadsheet.setToolTipText("Transfers the track selected  to the leadsheet as a new chorus. Alternatively, double click the entry. If Start Beat, +/-,  and End Beat are set, will import just the selected range of beats.\n");
        importTrackToLeadsheet.setLabel("Transfer Track (or double click)");
        importTrackToLeadsheet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importTrackToLeadsheetActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 0.5;
        midiImportButtonPanel.add(importTrackToLeadsheet, gridBagConstraints);

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
        startBeatSpinner.setToolTipText("Sets the starting beat for playback or importing to the leadsheet.");
        startBeatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder("Start Beat"));
        startBeatSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        startBeatSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        midiImportButtonPanel.add(startBeatSpinner, gridBagConstraints);

        endBeatSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        endBeatSpinner.setToolTipText("Sets the ending beat for playback or importing to the leadsheet.");
        endBeatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "End Beat", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        endBeatSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        endBeatSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 6;
        midiImportButtonPanel.add(endBeatSpinner, gridBagConstraints);

        offsetSpinner.setModel(new javax.swing.SpinnerNumberModel(0, -120, 120, 1));
        offsetSpinner.setToolTipText("");
        offsetSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder("+/- Slots"));
        offsetSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        offsetSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        midiImportButtonPanel.add(offsetSpinner, gridBagConstraints);

        importResolutionComboBox.setMaximumRowCount(16);
        importResolutionComboBox.setModel(new javax.swing.DefaultComboBoxModel(NoteResolutionInfo.getNoteResolutions()));
        importResolutionComboBox.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[5]);
        importResolutionComboBox.setToolTipText("Sets the resolution with which MIDI tracks are converted to Impro-Visor notes. Select the highest number of slots that gives satisfactory results. Low numbers take more memory and may fail.");
        importResolutionComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        importResolutionComboBox.setMinimumSize(new java.awt.Dimension(300, 50));
        importResolutionComboBox.setPreferredSize(new java.awt.Dimension(300, 50));
        importResolutionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMidiNoteResolutionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(importResolutionComboBox, gridBagConstraints);

        startRoundingFactorComboBox.setMaximumRowCount(16);
        startRoundingFactorComboBox.setModel(new javax.swing.DefaultComboBoxModel(StartRoundingFactor.getFactors()));
        startRoundingFactorComboBox.setSelectedItem(StartRoundingFactor.getFactors()[3]);
        startRoundingFactorComboBox.setToolTipText("Determines how to round off any pickup notes.");
        startRoundingFactorComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Pickup Rounding Factor", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 1, 13))); // NOI18N
        startRoundingFactorComboBox.setMinimumSize(new java.awt.Dimension(300, 50));
        startRoundingFactorComboBox.setPreferredSize(new java.awt.Dimension(300, 50));
        startRoundingFactorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startRoundingFactorComboBoximportMidiNoteResolutionChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(startRoundingFactorComboBox, gridBagConstraints);

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
  setSelectedTrack();
  //playSelectedTrack();
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
    reImport();
  }//GEN-LAST:event_importMidiNoteResolutionChanged

private void importedTrackListMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_importedTrackListMouseClicked
  {//GEN-HEADEREND:event_importedTrackListMouseClicked
    setSelectedTrack();
 
    if( evt.getClickCount() > 1 )
      {
      importSelectedTrack();
      }
  }//GEN-LAST:event_importedTrackListMouseClicked

private void stopPlayingTrackButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_stopPlayingTrackButtonActionPerformed
  {//GEN-HEADEREND:event_stopPlayingTrackButtonActionPerformed
    stopPlaying();
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

private void startRoundingFactorComboBoximportMidiNoteResolutionChanged(java.awt.event.ActionEvent evt)//GEN-FIRST:event_startRoundingFactorComboBoximportMidiNoteResolutionChanged
  {//GEN-HEADEREND:event_startRoundingFactorComboBoximportMidiNoteResolutionChanged
   reImport();
  }//GEN-LAST:event_startRoundingFactorComboBoximportMidiNoteResolutionChanged

private void setJmVolume()
  {
   int value = (Integer)volumeSpinner.getValue();
   if( jmScore != null )
     {
     jmScore.setVolume(value);
   
     //System.out.println("jmVolume = " + jmScore.getVolume());
     }
  }

private void reImport()
  {
    int index = importedTrackList.getSelectedIndex();
    
    int newResolution = ((NoteResolutionInfo)importResolutionComboBox.getSelectedItem()).getSlots();
    midiImport.setResolution(newResolution);
    int newRoundingFactor = ((StartRoundingFactor)startRoundingFactorComboBox.getSelectedItem()).getFactor();
    midiImport.setStartFactor(newRoundingFactor);
    reload();
    
    selectTrack(index);
  }

/**
 * This sets the start and end beat spinners, as well as getting the track
 * as an Impro-Visor part.
 */
private void setSelectedTrack()
  {
    int index = importedTrackList.getSelectedIndex();
    selectTrack(index);  
  }

private void selectTrack(int index)
  {
  if( index < 0 )
    {
      return;
    }
  Object ob = trackListModel.get(index);
  if( ob instanceof MidiImportRecord )
      {
       importedTrackList.setSelectedIndex(index); // establish, if not already
       MidiImportRecord record = (MidiImportRecord)ob;
       selectedPart = record.getPart();
       
       int initialRestSlots = record.getInitialRestSlots();
       int beatsPerMeasure = 4;
       int initialRestBeats = initialRestSlots/BEAT;
       int initialRestMeasures = initialRestBeats/beatsPerMeasure;
       int initialIntegralBeats = beatsPerMeasure*initialRestMeasures;
       int initialIntegralSlots = initialIntegralBeats*BEAT;
       int startBeat = 1+initialIntegralBeats;
       int offset = initialRestSlots - initialIntegralSlots;
//  System.out.println();
//  System.out.println("# initialRestSlots = " + initialRestSlots);
//  System.out.println("# initialRestBeats = " + initialRestBeats);
//  System.out.println("# initialRestMeasures = " + initialRestMeasures);
//  System.out.println("# initialIntegralBeats = " + initialIntegralBeats);
//  System.out.println("# initialIntegralSlots = " + initialIntegralSlots);
//  System.out.println("# startSlot = " + initialIntegralSlots);
//  System.out.println("# startBeat = " + startBeat);
//  System.out.println("# offset = " + offset);
   
       startBeatSpinner.setValue(startBeat);
       offsetSpinner.setValue(offset);
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
      int offset = (Integer)offsetSpinner.getValue();
      int startSlot = BEAT*(((Integer)startBeatSpinner.getValue())-1);
      int endSlot = BEAT*((Integer)endBeatSpinner.getValue());
//System.out.println("* offset = " + offset);
//System.out.println("* startSlot = " + startSlot);
//System.out.println("* endSlot = " + endSlot);

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
        stopPlaying();
        Score score = new Score();
        score.addPart(part);
        //System.out.println("score = " + score);
        notate.playAscore(score);
      }
  }

private void stopPlaying()
  {
    // Stop both possible synths, the track one and the score one.
    notate.stopPlayAscore();
    if( jmSynth != null )
      {
        jmSynth.stop();
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
    private javax.swing.JSpinner offsetSpinner;
    private javax.swing.JButton playMIDIfile;
    private javax.swing.JButton playMIDIimportTrack;
    private javax.swing.JLabel selectTracksLabel;
    private javax.swing.JSpinner startBeatSpinner;
    private javax.swing.JComboBox startRoundingFactorComboBox;
    private javax.swing.JButton stopPlayingTrackButton;
    private javax.swing.JScrollPane trackSelectScrollPane;
    private javax.swing.JSpinner volumeSpinner;
    // End of variables declaration//GEN-END:variables
}
