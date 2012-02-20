/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

import imp.Constants;
import imp.com.PlayScoreCommand;
import imp.com.SetChordsCommand;
import imp.data.*;
import imp.util.Preferences;
import java.io.*;
import java.util.Vector;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import polya.Polylist;

/**
 * Created on August 2, 2006, 1:50 PM
 *
 * @author  Martin
 */
public class CriticDialog extends javax.swing.JDialog implements Constants {
 
    private CriticTableModel dataModel;
    private enum TCol {
        NAME (String.class, "Name"),
        NOTES (String.class, "Notes"),
        CHORDS (String.class, "Chords"),
        GRADE (Integer.class, "Grade", 30),
        PLAYBTN (ImageIcon.class, "", 20);
        
        private final String name;
        private final int width;
        private final Class type;
        private TCol(Class type, String name) {
            this(type, name, -1);
        }
        private TCol(Class type, String name, int width) {
            this.type = type;
            this.name = name;
            this.width = width;
        }
        public int getWidth() {
            return width;
        }
        public Class getType() {
            return type;
        }
        public String toString() {
            return name;
        }
    }
        
    /** Creates new form CriticDialog */
    public CriticDialog(java.awt.Frame parent) {
        super(parent, false);
        dataModel = new CriticTableModel();
        initComponents();
      
        
        TableColumn c;
        int width;
        
        for(TCol col : TCol.values()) {
            c = dataTable.getColumnModel().getColumn(col.ordinal());
            width = col.getWidth();
            if(width >= 0) {
                c.setMinWidth(width);
                c.setMaxWidth(width);
                c.setPreferredWidth(width);
            }
        }
    }
    
    public void updateLabel() {
        if(currentFile == null) {
            fileLabel.setText(null);
        } else {
            try {
                fileLabel.setText("file: " + currentFile.getCanonicalPath());
            } catch(IOException e) {
                errorLabel.setText("File IO Error: " + e.getMessage());
            }
        }
    }
    
    public void add(String data, int grade) {
        dataModel.add(data, grade);
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane1 = new javax.swing.JScrollPane();
        dataTable = new javax.swing.JTable();
        fileLabel = new javax.swing.JLabel();
        errorLabel = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        saveFile = new javax.swing.JButton();
        saveAsFile = new javax.swing.JButton();
        openFile = new javax.swing.JButton();
        appendFile = new javax.swing.JButton();
        deleteSelected = new javax.swing.JButton();
        deleteAll = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        dataTable.setModel(dataModel);
        dataTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dataTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(dataTable);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(jScrollPane1, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 2, 0);
        getContentPane().add(fileLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 10, 0, 0);
        getContentPane().add(errorLabel, gridBagConstraints);

        jPanel1.setLayout(new java.awt.GridBagLayout());

        saveFile.setText("Save");
        saveFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(saveFile, gridBagConstraints);

        saveAsFile.setText("Save As...");
        saveAsFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        jPanel1.add(saveAsFile, gridBagConstraints);

        openFile.setText("Open");
        openFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        jPanel1.add(openFile, gridBagConstraints);

        appendFile.setText("Append From File");
        appendFile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                appendFileActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        jPanel1.add(appendFile, gridBagConstraints);

        deleteSelected.setText("Delete Selected");
        deleteSelected.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteSelectedActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 4;
        gridBagConstraints.gridy = 0;
        jPanel1.add(deleteSelected, gridBagConstraints);

        deleteAll.setText("Delete All");
        deleteAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 5;
        gridBagConstraints.gridy = 0;
        jPanel1.add(deleteAll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        getContentPane().add(jPanel1, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void deleteAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllActionPerformed
        dataModel.clear();
    }//GEN-LAST:event_deleteAllActionPerformed

    private void deleteSelectedActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteSelectedActionPerformed
        dataModel.deleteRows(dataTable.getSelectedRows());
    }//GEN-LAST:event_deleteSelectedActionPerformed

    private void dataTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dataTableMouseClicked
        if(dataTable.getSelectedColumn() == TCol.PLAYBTN.ordinal()) {
            int row = dataTable.getSelectedRow();
            if(row == -1)
                return;
            Polylist dataRow = dataModel.getRow(row);
            ChordPart chords = new ChordPart(BEAT*8);
            MelodyPart melody = new MelodyPart(BEAT*8);

            Polylist combined = ((Polylist) (dataRow.nth(TCol.CHORDS.ordinal()))).append(
                                (Polylist) (dataRow.nth(TCol.NOTES.ordinal()))
                                );
            (new SetChordsCommand(0, combined, chords, melody)).execute();

            chords.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));

            Score score = new Score();
            score.setChordProg(chords);
            score.addPart(melody);

            new PlayScoreCommand(score, 0, true, 0, 0).execute();
        }
    }//GEN-LAST:event_dataTableMouseClicked

    private void saveFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveFileActionPerformed
        save();
    }//GEN-LAST:event_saveFileActionPerformed

    private void appendFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_appendFileActionPerformed
        addFromFile(false);
    }//GEN-LAST:event_appendFileActionPerformed

    private void openFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openFileActionPerformed
        addFromFile(true);
    }//GEN-LAST:event_openFileActionPerformed

    File currentFile = null;
    
    public void addFromFile(boolean overwrite) {
        openDialog.setDialogType(JFileChooser.OPEN_DIALOG);

        if(openDialog.showDialog(this, "Open") != JFileChooser.APPROVE_OPTION)
            return;
        
        File file = openDialog.getSelectedFile();
        if(file.exists()) {
            if(overwrite) {
                dataModel.clear();
                currentFile = file;
                updateLabel();
            }
            
            BufferedReader in;
            try {
                in = new BufferedReader(new FileReader(file));
            } catch(FileNotFoundException e) {
                errorLabel.setText("File Not Found: " + e.getMessage());
                return;
            }
            
            String line;
            
            try {
                while((line = in.readLine()) != null) {
                    int gradeStart = line.indexOf(" ");
                    int grade = (int) (10 * Double.valueOf(line.substring(0, gradeStart)));

                    int lickStart = line.indexOf("(");
                    String lick = line.substring(lickStart);
                    add(lick, grade);
                }
            } catch(IOException e) {
                errorLabel.setText("File IO Error: " + e.getMessage());
                return;
            }
        }
        
        errorLabel.setText(null);
    }
    
    JFileChooser openDialog = new JFileChooser();
    JFileChooser saveDialog = new JFileChooser();
    
    public void save() {
        if(currentFile != null)
            save(currentFile);
        else
            saveAs();
    }
    
    public void save(File f) {
        BufferedWriter out;
        try {
            out = new BufferedWriter(new FileWriter(saveDialog.getSelectedFile()));
        } catch(IOException e) {
            errorLabel.setText("File IO Error: " + e.getMessage());
            return;
        } 
        
        for(int i = 0; i < dataModel.getRowCount(); i++) {
            try {
                saveRow(out, i);
                out.newLine();
            } catch (IOException e) {
                errorLabel.setText("File IO Error: " + e.getMessage());
                return;
            }
        }
        
        try {
            out.flush();
        } catch(IOException e) {
            errorLabel.setText("File IO Error: " + e.getMessage());
            return;
        }
        
        errorLabel.setText(null);
    }
    
    public void saveAs() {
        saveDialog.setDialogType(JFileChooser.SAVE_DIALOG);
        if(saveDialog.showDialog(this, "Save") != JFileChooser.APPROVE_OPTION)
            return;
        currentFile = saveDialog.getSelectedFile();
        updateLabel();
        save(currentFile);
    }
    
    private void saveAsFileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsFileActionPerformed
        saveAs();
    }//GEN-LAST:event_saveAsFileActionPerformed

    private void saveRow(BufferedWriter out, int row) throws IOException {
        Polylist r = dataModel.getRow(row);
        Polylist name = Polylist.list("name", r.first());
        Polylist notes = (Polylist) r.second();
        Polylist chords = (Polylist) r.third();
        int grade = (int) (Integer) r.fourth();
        Polylist lick = Polylist.list("lick", notes.cons("notes"), chords.cons("sequence"), name, Polylist.list("grade", grade));
        
        out.write(String.valueOf(grade / 10.0));
        out.write(' ');
        
        while(!chords.isEmpty()) {
            while(!chords.isEmpty() && chords.first() == null) {
                chords = chords.rest();
            }
            
            if(!chords.isEmpty()) {
                printChord(out, ChordSymbol.makeChordSymbol((String) chords.first()));
                chords = chords.rest();
            }
        }
        
        Polylist noteSymbols = NoteSymbol.makeNoteSymbolList(notes);
        
        while(noteSymbols.nonEmpty()) {
            printNoteSymbol(out, (NoteSymbol) noteSymbols.first());
            noteSymbols = noteSymbols.rest();
        }
        
        out.write(lick.toString());
    }

    
    private final int BOTTOMNOTE = 60;
    private final int TOPNOTE = 83;
    private final int MINDURATION = EIGHTH;
    
    private void printNoteSymbol(BufferedWriter out, NoteSymbol note) throws IOException {
        int pitch = note.getMIDI();

        if(pitch > 0) {
            if(pitch < BOTTOMNOTE)
                pitch = BOTTOMNOTE;
            if(pitch > TOPNOTE) 
                pitch = TOPNOTE;
        }
            
        // total number of bit rows to output
        int totalRows = note.getDuration() / MINDURATION;
        
        // current bit row
        int currentRow = 0;
        
        char[] bits = {'0', ' ', '0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' '};
        while(currentRow < totalRows) {
            if(pitch == -1) {
                out.write("0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 0 ");
            } else {
                // sustain bit
                out.write( currentRow > 0 ? "1 " : "0 ");
                int index = 2 * (pitch - BOTTOMNOTE);
                bits[index] = '1';
                out.write(bits);
                bits[index] = '0';
            }
            currentRow++;
        }
    }
    
    private void printChord(BufferedWriter out, ChordSymbol chord) throws IOException {
        if(chord == null)
            return;
        
        Polylist spelling = chord.getChordForm().getSpell(chord.getRootString());
        
        // 12 bits for a chord
        char[] bitSpelling = {'0', ' ', '0', ' ', '0', ' ', '0', ' ', 
                              '0', ' ', '0', ' ', '0', ' ', '0', ' ', 
                              '0', ' ', '0', ' ', '0', ' ', '0', ' '};
        
        while(spelling.nonEmpty()) {
            NoteSymbol n = (NoteSymbol) spelling.first();
            bitSpelling[2 * (n.getMIDI() % 12)] = '1';
            spelling = spelling.rest();
        }
        
        out.write(bitSpelling);
    }
    
    private class CriticTableModel extends AbstractTableModel {
        private ImageIcon playIcon = new ImageIcon(getClass().getResource("/imp/gui/graphics/icons/play.png"));

        private Vector<Polylist> data = new Vector<Polylist>();

        public CriticTableModel() {
            
        }
        
        public void clear() {
            data.clear();
            fireTableDataChanged();
        }
        
        public void add(String lickStr, int grade) {
            Polylist lick = Notate.parseListFromString(lickStr);
            if(lick.length() == 1 && lick.first() instanceof Polylist && ((Polylist) lick.first()).length() > 1) {
                lick = (Polylist) (lick.first());
            }
            Polylist notes = Polylist.list();
            Polylist chords = Polylist.list();
            String name = "";
            while(lick.nonEmpty()) {
                Object o = lick.first();
                if(o instanceof Polylist) {
                    Polylist p = (Polylist) o;
                    String s = (String) p.first();
                    if(s.equals("notes")) {
                        notes = p.rest();
                    } else if(s.equals("sequence")) {
                        chords = p.rest();
                    } else if(s.equals("name")) {
                        name = (String) p.rest().implode(" ");
                    }
                }
                lick = lick.rest();
            }
            data.add(Polylist.list(name, notes, chords, grade));
            
            fireTableDataChanged();
        }
        
        public Polylist getRow(int row) {
            return data.get(row);
        }

        public int getColumnCount() {
            return TCol.values().length;
        }

        public String getColumnName(int col) {
            return TCol.values()[col].toString();
        }
        
        public Class getColumnClass(int col) {
            return TCol.values()[col].getType();
        }

        public int getRowCount() {
            return data.size();
        }

        public Object getValueAt(int row, int col) {
            if(col == TCol.PLAYBTN.ordinal())
                return playIcon;
            Object o = data.get(row).nth(col);
            if(o instanceof Polylist && !((Polylist)o).nonEmpty())
                return "";
            else {
                return o;
            }
        }
        
        public void deleteRows(int[] rowsToDelete) {
            java.util.Arrays.sort(rowsToDelete, 0, rowsToDelete.length);
            for(int i = rowsToDelete.length - 1; i >= 0; --i) {
                deleteRow(rowsToDelete[i]);
            }
        }
        
        public void deleteRow(int i) {
            data.remove(i);
            
            fireTableDataChanged();
        }

        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != TCol.PLAYBTN.ordinal();
        }

        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            int name = TCol.NAME.ordinal();
            int grade = TCol.GRADE.ordinal();
            int notes = TCol.NOTES.ordinal();
            int chords = TCol.CHORDS.ordinal();
            if(columnIndex == name) {
                data.get(rowIndex).setNth(name, aValue.toString());
            } else if(columnIndex == grade) {
                data.get(rowIndex).setNth(grade, Integer.valueOf(aValue.toString()));
            } else if(columnIndex == notes) {
                data.get(rowIndex).setNth(notes, (Polylist) Notate.parseListFromString(aValue.toString()).first());
            } else if(columnIndex == chords) {
                data.get(rowIndex).setNth(chords, (Polylist) Notate.parseListFromString(aValue.toString()).first());
            }
           
            fireTableRowsUpdated(rowIndex,rowIndex);
        }
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton appendFile;
    private javax.swing.JTable dataTable;
    private javax.swing.JButton deleteAll;
    private javax.swing.JButton deleteSelected;
    private javax.swing.JLabel errorLabel;
    private javax.swing.JLabel fileLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton openFile;
    private javax.swing.JButton saveAsFile;
    private javax.swing.JButton saveFile;
    // End of variables declaration//GEN-END:variables
    
}
