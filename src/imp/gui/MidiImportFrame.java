/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.data.MidiImportRecord;
import java.util.LinkedList;
import javax.swing.DefaultListModel;

/**
 *
 * @author keller
 */
public class MidiImportFrame extends javax.swing.JFrame
{
Notate notate;

DefaultListModel trackListModel;

/**
 * Creates new form MidiImportFrame
 */
public MidiImportFrame(Notate notate, String filenameDisplay)
  {
    trackListModel = new DefaultListModel();
    initComponents();
    this.notate = notate;
    this.setTitle("MIDI Tracks in " + filenameDisplay);
  }

public void load(LinkedList<MidiImportRecord> records)
  {
    trackListModel.clear();
    
    int channelNumber = 1;
    for( final MidiImportRecord record: records)
      {
        if(record.getChannel() > channelNumber )
          {
            trackListModel.addElement("-------------------------------------");
            channelNumber = record.getChannel();
          }
        trackListModel.addElement(record);
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

        selectTracksLabel = new javax.swing.JLabel();
        trackSelectScrollPane = new javax.swing.JScrollPane();
        importedTrackList = new javax.swing.JList();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(204, 204, 204));
        getContentPane().setLayout(new java.awt.GridBagLayout());

        selectTracksLabel.setBackground(new java.awt.Color(153, 255, 0));
        selectTracksLabel.setFont(new java.awt.Font("Lucida Grande", 1, 14)); // NOI18N
        selectTracksLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        selectTracksLabel.setText("Please select the tracks to be imported one at a time. Each track will be put in a separate chorus in the leadsheet.");
        selectTracksLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectTracksLabel.setOpaque(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        getContentPane().add(selectTracksLabel, gridBagConstraints);

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
        });
        trackSelectScrollPane.setViewportView(importedTrackList);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(trackSelectScrollPane, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void importTrackSelected(java.awt.event.MouseEvent evt)//GEN-FIRST:event_importTrackSelected
  {//GEN-HEADEREND:event_importTrackSelected
    int index = importedTrackList.getSelectedIndex();
    Object ob = trackListModel.get(index);
    if( ob instanceof MidiImportRecord )
      {
      notate.addChorus(((MidiImportRecord)ob).getPart());
      }
  }//GEN-LAST:event_importTrackSelected


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList importedTrackList;
    private javax.swing.JLabel selectTracksLabel;
    private javax.swing.JScrollPane trackSelectScrollPane;
    // End of variables declaration//GEN-END:variables
}
