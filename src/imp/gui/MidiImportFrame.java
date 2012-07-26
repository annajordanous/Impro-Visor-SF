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
import imp.ImproVisor;
import imp.data.*;
import imp.data.MidiImport.ChannelInfo;
import imp.util.LeadsheetFileView;
import imp.util.MidiFilter;
import java.io.File;
import java.util.*;
import javax.sound.midi.InvalidMidiDataException;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import jm.midi.MidiSynth;

/**
 *
 * @author keller
 */

@SuppressWarnings("serial")

public class MidiImportFrame extends javax.swing.JFrame implements Constants
{
Notate notate;
File file;
File midiDirectory;
MidiImport midiImport;
DefaultListModel trackListModel;
private LinkedList<MidiImportRecord> melodies;
MelodyPart selectedPart = null;
// used in chord extract:
private int melodyChannel = 0;
private int bassChannel = 0;
private int chordChannel = 0;
private int chordResolution = HALF;

private ChannelInfo[] channelInfo = null;

String filenameDisplay;

private JFileChooser midiFileChooser = new JFileChooser();


/**
 * Note that this is a jMusic MidiSynth and not an Impro-Visor MidiSynth.
 * We also use a jMusic score, in addition to an Impro-Visor score later.
 */

MidiSynth jmSynth;
jm.music.data.Score jmScore;

int INITIAL_RESOLUTION_COMBO = 3; // 32nd note triplets

/**
 * Creates new form MidiImportFrame.
 * Imported choruses are sent to notate once they are selected from the menu.
 */
public MidiImportFrame(Notate notate)
  {
    trackListModel = new DefaultListModel();
    initComponents();
    this.notate = notate;
    midiImport = new MidiImport();
    initFileChooser();

    WindowRegistry.registerWindow(this);
    
    volumeSpinner.setVisible(false); // volume setting doesn't work yet
    
    noteResolutionComboBox.setSelectedIndex(NoteResolutionComboBoxModel.getSelectedIndex());
    
    midiDirectory = ImproVisor.getMidiDirectory();
  }

public void loadFileAndMenu()
  {
    getFile();
    if( file != null )
      {
      setTitle("MIDI Import: " + getFilenameDisplay());
      loadFile();
      loadMenu();
      initChannelInfo();
      }
  }

public void getFile()
  {
    file = null;
    try
      {
        midiFileChooser.setCurrentDirectory(midiDirectory);
        int midiChoice = midiFileChooser.showOpenDialog(notate);
        if( midiChoice == JFileChooser.CANCEL_OPTION )
          {
            return;
          }
        if( midiChoice == JFileChooser.APPROVE_OPTION )
          {
            file = midiFileChooser.getSelectedFile();
            midiDirectory = midiFileChooser.getCurrentDirectory();
          }
        filenameDisplay = file.getName();
      }
    catch( Exception e )
      {
      }
  }

public void loadFile()
  {
  midiImport.importMidi(file); 
  }

public void loadMenu()
  {
  melodies = midiImport.getMelodies();
  
  if( melodies != null )
    {
    setResolution();
    trackListModel.clear();
    
    int channelNumber = 0;
    for( final MidiImportRecord record: melodies )
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
  }

private void reloadMenu()
  {
    //midiImport.importMidi();
    setResolution();
    int saveIndex = importedTrackList.getSelectedIndex();
    midiImport.scoreToMelodies();
    
    loadMenu();
    selectTrack(saveIndex);
    
  }


private void initFileChooser()
  {
    LeadsheetFileView fileView = new LeadsheetFileView();
    midiFileChooser.setCurrentDirectory(ImproVisor.getUserDirectory());
    midiFileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
    midiFileChooser.setDialogTitle("Open MIDI file");
    midiFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
    midiFileChooser.resetChoosableFileFilters();
    midiFileChooser.addChoosableFileFilter(new MidiFilter());
    midiFileChooser.setFileView(fileView);
  }


public String getFilenameDisplay()
  {
    return filenameDisplay;
  }

private void initChannelInfo()
{
    channelInfo = midiImport.getChannelInfo();
    bassChannelNumberComboBox.setModel(new javax.swing.DefaultComboBoxModel(channelInfo));
    chordChannelNumberComboBox.setModel(new javax.swing.DefaultComboBoxModel(channelInfo));
    melodyChannelNumberComboBox.setModel(new javax.swing.DefaultComboBoxModel(channelInfo));

        bassChannel = midiImport.getBassChannel() + 1;
        chordChannel = midiImport.getChordChannel() + 1;
        for (int j = 0; j < channelInfo.length; j++) {
            if (channelInfo[j].getChannelNum() == bassChannel) {
                bassChannelNumberComboBox.setSelectedItem(channelInfo[j]);
            }
            if (channelInfo[j].getChannelNum() == chordChannel) {
                chordChannelNumberComboBox.setSelectedItem(channelInfo[j]);
            }
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
        meterSpinner = new javax.swing.JSpinner();
        chordExtractPanel = new javax.swing.JPanel();
        melodyChannelNumberComboBox = new javax.swing.JComboBox();
        chordChannelNumberComboBox = new javax.swing.JComboBox();
        bassChannelNumberComboBox = new javax.swing.JComboBox();
        extractChords = new javax.swing.JButton();
        resolutionPanel = new javax.swing.JPanel();
        noteResolutionComboBox = new javax.swing.JComboBox();
        chordResolutionComboBox = new javax.swing.JComboBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        MIDIimportFileMenu = new javax.swing.JMenu();
        openAnotherFileMI = new javax.swing.JMenuItem();

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
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(playMIDIfile, gridBagConstraints);

        playMIDIimportTrack.setText("Play Track");
        playMIDIimportTrack.setToolTipText("Plays the selected MIDI track individually, not in the leadsheet context. The selected resolution is used. Changing the resolution may change the result.");
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
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(stopPlayingTrackButton, gridBagConstraints);

        importTrackToLeadsheet.setText("Copy Track to Leadsheet");
        importTrackToLeadsheet.setToolTipText("Shortcut: Double-click the track selection. Copies the track selected  to the leadsheet as a new chorus. If Start Beat and End Beat are set, will import just the selected range of beats.\n");
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

        volumeSpinner.setModel(new javax.swing.SpinnerNumberModel(70, 0, 127, 5));
        volumeSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Volume", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Lucida Grande", 0, 11))); // NOI18N
        volumeSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                volumeSpinnerChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(volumeSpinner, gridBagConstraints);

        startBeatSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        startBeatSpinner.setToolTipText("Sets the starting beat for playback or importing to the leadsheet.");
        startBeatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder("Start Beat"));
        startBeatSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        startBeatSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(startBeatSpinner, gridBagConstraints);

        endBeatSpinner.setModel(new javax.swing.SpinnerNumberModel(Integer.valueOf(1), Integer.valueOf(1), null, Integer.valueOf(1)));
        endBeatSpinner.setToolTipText("Sets the ending beat for playback or importing to the leadsheet.");
        endBeatSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "End Beat", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        endBeatSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        endBeatSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(endBeatSpinner, gridBagConstraints);

        meterSpinner.setModel(new javax.swing.SpinnerNumberModel(4, 1, 16, 1));
        meterSpinner.setToolTipText("");
        meterSpinner.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Meter", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        meterSpinner.setMinimumSize(new java.awt.Dimension(75, 56));
        meterSpinner.setPreferredSize(new java.awt.Dimension(75, 56));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 7;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(meterSpinner, gridBagConstraints);

        chordExtractPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Channel #", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION));
        chordExtractPanel.setMinimumSize(new java.awt.Dimension(180, 140));
        chordExtractPanel.setPreferredSize(new java.awt.Dimension(180, 170));
        chordExtractPanel.setRequestFocusEnabled(false);
        chordExtractPanel.setLayout(new java.awt.GridBagLayout());

        melodyChannelNumberComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder("Melody"));
        melodyChannelNumberComboBox.setMinimumSize(new java.awt.Dimension(160, 40));
        melodyChannelNumberComboBox.setPreferredSize(new java.awt.Dimension(160, 40));
        melodyChannelNumberComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                melodyChannelNumberComboBoxActionPerformed(evt);
            }
        });
        chordExtractPanel.add(melodyChannelNumberComboBox, new java.awt.GridBagConstraints());

        chordChannelNumberComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder("Chord"));
        chordChannelNumberComboBox.setMinimumSize(new java.awt.Dimension(160, 40));
        chordChannelNumberComboBox.setPreferredSize(new java.awt.Dimension(160, 40));
        chordChannelNumberComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordChannelNumberComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        chordExtractPanel.add(chordChannelNumberComboBox, gridBagConstraints);

        bassChannelNumberComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder("Bass"));
        bassChannelNumberComboBox.setMinimumSize(new java.awt.Dimension(160, 50));
        bassChannelNumberComboBox.setPreferredSize(new java.awt.Dimension(160, 40));
        bassChannelNumberComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bassChannelNumberComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        chordExtractPanel.add(bassChannelNumberComboBox, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 10;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        midiImportButtonPanel.add(chordExtractPanel, gridBagConstraints);

        extractChords.setText("Extract Chords");
        extractChords.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                extractChordsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 11;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(extractChords, gridBagConstraints);

        resolutionPanel.setMinimumSize(new java.awt.Dimension(320, 120));
        resolutionPanel.setPreferredSize(new java.awt.Dimension(320, 120));

        noteResolutionComboBox.setMaximumRowCount(16);
        noteResolutionComboBox.setModel(NoteResolutionComboBoxModel.getNoteResolutionComboBoxModel());
        noteResolutionComboBox.setSelectedIndex(NoteResolutionComboBoxModel.getSelectedIndex());
        noteResolutionComboBox.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[NoteResolutionComboBoxModel.getSelectedIndex()]);
        noteResolutionComboBox.setToolTipText("Sets the resolution with which MIDI tracks are converted to Impro-Visor notes. Select the highest number of slots that gives satisfactory results. Low numbers take more memory and may fail.");
        noteResolutionComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Note Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N
        noteResolutionComboBox.setMinimumSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBox.setPreferredSize(new java.awt.Dimension(300, 50));
        noteResolutionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noteResolutionComboBoxChanged(evt);
            }
        });
        resolutionPanel.add(noteResolutionComboBox);

        chordResolutionComboBox.setMaximumRowCount(16);
        chordResolutionComboBox.setModel(ChordResolutionComboBoxModel.getChordResolutionComboBoxModel());
        chordResolutionComboBox.setSelectedIndex(ChordResolutionComboBoxModel.getSelectedIndex());
        chordResolutionComboBox.setSelectedItem(NoteResolutionInfo.getNoteResolutions()[ChordResolutionComboBoxModel.getSelectedIndex()]);
        chordResolutionComboBox.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chord Resolution", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 13))); // NOI18N
        chordResolutionComboBox.setMinimumSize(new java.awt.Dimension(300, 50));
        chordResolutionComboBox.setPreferredSize(new java.awt.Dimension(300, 50));
        chordResolutionComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chordResolutionComboBoxActionPerformed(evt);
            }
        });
        resolutionPanel.add(chordResolutionComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 9;
        gridBagConstraints.gridy = 0;
        midiImportButtonPanel.add(resolutionPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.1;
        getContentPane().add(midiImportButtonPanel, gridBagConstraints);

        MIDIimportFileMenu.setText("File");

        openAnotherFileMI.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_M, java.awt.event.InputEvent.CTRL_MASK));
        openAnotherFileMI.setText("Open Another MIDI File");
        openAnotherFileMI.setToolTipText("Opens a MIDI File, usually a different one from the current.");
        openAnotherFileMI.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openAnotherFileMIActionPerformed(evt);
            }
        });
        MIDIimportFileMenu.add(openAnotherFileMI);

        jMenuBar1.add(MIDIimportFileMenu);

        setJMenuBar(jMenuBar1);

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

private void noteResolutionComboBoxChanged(java.awt.event.ActionEvent evt)//GEN-FIRST:event_noteResolutionComboBoxChanged
  {//GEN-HEADEREND:event_noteResolutionComboBoxChanged
    NoteResolutionComboBoxModel.setSelectedIndex(noteResolutionComboBox.getSelectedIndex());
    reloadMenu();
  }//GEN-LAST:event_noteResolutionComboBoxChanged

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

private void openAnotherFileMIActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_openAnotherFileMIActionPerformed
  {//GEN-HEADEREND:event_openAnotherFileMIActionPerformed
    loadFileAndMenu();
  }//GEN-LAST:event_openAnotherFileMIActionPerformed

    private void extractChordsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_extractChordsActionPerformed
        extractChords();
    }//GEN-LAST:event_extractChordsActionPerformed

    private void chordResolutionComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordResolutionComboBoxActionPerformed
        ChordResolutionComboBoxModel.setSelectedIndex(chordResolutionComboBox.getSelectedIndex());
        chordResolution = ChordResolutionComboBoxModel.getResolution();
    }//GEN-LAST:event_chordResolutionComboBoxActionPerformed

    private void bassChannelNumberComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bassChannelNumberComboBoxActionPerformed
        ChannelInfo bassInfo = (ChannelInfo) bassChannelNumberComboBox.getSelectedItem();
        bassChannel = bassInfo.getChannelNum()-1;
        bassChannelNumberComboBox.setSelectedIndex(bassChannelNumberComboBox.getSelectedIndex());
    }//GEN-LAST:event_bassChannelNumberComboBoxActionPerformed

    private void chordChannelNumberComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chordChannelNumberComboBoxActionPerformed
        ChannelInfo chordInfo = (ChannelInfo) chordChannelNumberComboBox.getSelectedItem();
        chordChannel = chordInfo.getChannelNum()-1;
        chordChannelNumberComboBox.setSelectedIndex(chordChannelNumberComboBox.getSelectedIndex());
}//GEN-LAST:event_chordChannelNumberComboBoxActionPerformed

    private void melodyChannelNumberComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_melodyChannelNumberComboBoxActionPerformed
        ChannelInfo melodyInfo = (ChannelInfo) melodyChannelNumberComboBox.getSelectedItem();
        melodyChannel = melodyInfo.getChannelNum() - 1;
        melodyChannelNumberComboBox.setSelectedIndex(melodyChannelNumberComboBox.getSelectedIndex());
    }//GEN-LAST:event_melodyChannelNumberComboBoxActionPerformed

private void setJmVolume()
  {
   int value = (Integer)volumeSpinner.getValue();
   if( jmScore != null )
     {
     //jmScore.setVolume(value);
   
     //System.out.println("jmVolume = " + jmScore.getVolume());
     }
  }


private void setResolution()
  {
    int index = noteResolutionComboBox.getSelectedIndex();
    NoteResolutionComboBoxModel.setSelectedIndex(index);
    midiImport.setResolution(NoteResolutionComboBoxModel.getResolution());
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
  if( index < 0 || index >= trackListModel.size() )
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
       int beatsPerMeasure = (Integer)meterSpinner.getValue();
       int initialRestBeats = initialRestSlots/BEAT;
       int initialRestMeasures = initialRestBeats/beatsPerMeasure;
       int initialIntegralBeats = beatsPerMeasure*initialRestMeasures;
       int startBeat = 1+initialIntegralBeats;

       startBeatSpinner.setValue(startBeat);
       
       int numBeats = record.getBeats();
       endBeatSpinner.setValue(numBeats);
       ((javax.swing.SpinnerNumberModel)endBeatSpinner.getModel()).setMaximum(numBeats);
       }
  }

private MelodyPart getSelectedTrackMelody()
  {
    if( selectedPart != null )
      {
          int startBeat = (Integer) startBeatSpinner.getValue() - 1;
          int endBeat = (Integer) endBeatSpinner.getValue();
          int bpmeasure = (Integer) meterSpinner.getValue();
          while ((endBeat - startBeat)%bpmeasure!=0)
          {
              endBeat++;
          }
          endBeatSpinner.setValue(endBeat);
          int startSlot = BEAT * startBeat;
          int endSlot = (BEAT * endBeat) - 1;
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

//method which extracts the chords from the given midi file, must specify bass track, chord track, and/or melody track
private void extractChords()
{
    
    
    int startBeat = (Integer) startBeatSpinner.getValue() - 1;
    int endBeat = (Integer) endBeatSpinner.getValue();
    int bpmeasure = (Integer) meterSpinner.getValue();
    while ((endBeat - startBeat)%bpmeasure!=0)
          {
              endBeat++;
          }
    endBeatSpinner.setValue(endBeat);
    //add cases for melody, bass, and chords
    int startSlot = BEAT*startBeat;

    //crude melodyimport
    
    if (melodyChannel != 0) {
        LinkedList<MidiImportRecord> newMelodies = midiImport.getMelodies();
        //create two separate arrays for the bass MelodyParts and the chord MelodyParts
        List<MelodyPart> melodyMelodyParts = new ArrayList<MelodyPart>();
        MelodyPart melodyPart;
        for (final MidiImportRecord newRecord : newMelodies) {
            melodyPart = newRecord.getPart();
            int endSlot = Math.min(melodyPart.getSize() - 1, (BEAT * endBeat) - 1);
            if (newRecord.getChannel() == melodyChannel && melodyPart != null) {
                melodyPart = melodyPart.copy(startSlot, endSlot);
                melodyMelodyParts.add(melodyPart);
            }
        }
        notate.addChorus(melodyMelodyParts.get(0));
        notate.requestFocusInWindow();
    }

    //sets the note resolution to 1/8
    MidiImport newImport = new MidiImport();
    newImport.importMidi(file);
    newImport.setResolution(EIGHTH);
    LinkedList<MidiImportRecord> newMelodies = newImport.getMelodies();

    //create two separate arrays for the bass MelodyParts and the chord MelodyParts
    List<MelodyPart> bassMelodyParts = new ArrayList<MelodyPart>();
    List<MelodyPart> chordMelodyParts = new ArrayList<MelodyPart>();
    MelodyPart melodyPart;
    for (final MidiImportRecord newRecord : newMelodies) {
        melodyPart = newRecord.getPart();
        int endSlot = Math.min(melodyPart.getSize() - 1, (BEAT * endBeat) - 1);
        if (newRecord.getChannel() == bassChannel && melodyPart != null) {
            melodyPart = melodyPart.copy(startSlot, endSlot);
            bassMelodyParts.add(melodyPart);
        }
        if (newRecord.getChannel() == chordChannel && melodyPart != null) {
            melodyPart = melodyPart.copy(startSlot, endSlot);
            chordMelodyParts.add(melodyPart);
        }
    }
    //combine the bass melodypart and the chord melodypart into a single array of melodyparts
    if (!bassMelodyParts.isEmpty()||!chordMelodyParts.isEmpty())
    {
        MelodyPart[] arrayBassMelodyParts = bassMelodyParts.toArray(new MelodyPart[bassMelodyParts.size()]);
        MelodyPart[] arrayChordMelodyParts = chordMelodyParts.toArray(new MelodyPart[chordMelodyParts.size()]);
        
        //extract the chords
        ChordExtract chordExtract = new ChordExtract();
        ChordPart chords = chordExtract.importChords(arrayBassMelodyParts, arrayChordMelodyParts, chordResolution);

        if (chords != null) {
            notate.setChordProg(chords);
        }
    }
}

@Override
public void dispose()
  {
    stopPlaying();
    notate.closeMidiImportFrame();
    WindowRegistry.unregisterWindow(this);
    super.dispose();
  }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu MIDIimportFileMenu;
    private javax.swing.JComboBox bassChannelNumberComboBox;
    private javax.swing.JComboBox chordChannelNumberComboBox;
    private javax.swing.JPanel chordExtractPanel;
    private javax.swing.JComboBox chordResolutionComboBox;
    private javax.swing.JSpinner endBeatSpinner;
    private javax.swing.JButton extractChords;
    private javax.swing.JButton importTrackToLeadsheet;
    private javax.swing.JList importedTrackList;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JComboBox melodyChannelNumberComboBox;
    private javax.swing.JSpinner meterSpinner;
    private javax.swing.JPanel midiImportButtonPanel;
    private javax.swing.JPanel midiImportTopPanel;
    private javax.swing.JComboBox noteResolutionComboBox;
    private javax.swing.JMenuItem openAnotherFileMI;
    private javax.swing.JButton playMIDIfile;
    private javax.swing.JButton playMIDIimportTrack;
    private javax.swing.JPanel resolutionPanel;
    private javax.swing.JLabel selectTracksLabel;
    private javax.swing.JSpinner startBeatSpinner;
    private javax.swing.JButton stopPlayingTrackButton;
    private javax.swing.JScrollPane trackSelectScrollPane;
    private javax.swing.JSpinner volumeSpinner;
    // End of variables declaration//GEN-END:variables
}
