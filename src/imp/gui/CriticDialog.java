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
import imp.ImproVisor;
import imp.com.PlayScoreCommand;
import imp.com.SetChordsCommand;
import imp.data.*;
import imp.util.Preferences;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import polya.Polylist;

/**
 * Created on August 2, 2006, 1:50 PM
 *
 * @author  Martin Hunt
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
        
        @Override
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

            new PlayScoreCommand(score, 
                                 0, 
                                 true, 
                                 ImproVisor.getLastMidiSynth(),
                                 ImproVisor.getCurrentWindow(),
                                 0, 
                                 0,
                                 false,
                                 BEAT*4).execute();
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
    
    // HB- FIX: Null pointer when clicking "save" after opening a file
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
        
        // FIX: Split into separate helper method
        Scanner in;
        BufferedWriter outTemp;
        BufferedWriter outWeight;
        try {
            in = new Scanner(f);
            File fileTemp = new File(f.getAbsolutePath() + "_temp");
            outTemp = new BufferedWriter(new FileWriter(fileTemp));
            File fileWeight = new File(f.getAbsolutePath() + "_for_weights");
            outWeight = new BufferedWriter(new FileWriter(fileWeight));
           
            int maxSize = 0;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                int index = line.indexOf("(lick");
                line = line.substring(0, index);
                if (line.length() > maxSize)
                    maxSize = line.length();
                outTemp.write(line);
                outTemp.newLine();
            }
            outTemp.flush();
            in.close();

            // Write beginning info
            outWeight.write("1 ");
            int numInputs = (maxSize - 4) / 2;
            outWeight.write("" + numInputs);
            outWeight.newLine();
            
            in = new Scanner(fileTemp);
            while (in.hasNextLine()) {
                String data = in.nextLine();
                while (data.length() < maxSize)
                {
                    // Add a whole note rest landing on beat 1 
                    data += "1 0 0 0 0 0 0 1 1 1 1 1 1 0 0 0 0 0 ";
                }
                
                outWeight.write(data);
                outWeight.newLine();
            }
            outWeight.flush();
            in.close();
            fileTemp.delete();
            
            
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
    
    /**
     * Saves a lick for the critic, later to be used as input for a neural network.
     * Major changes include a smaller bit vector and a new method 
     * for representing the data.
     * Needs at least one chord from the leadsheet, assumes at most two chords at the moment.
     * Also needs only a two measure selection. 
     * @param out
     * @param row
     * @throws IOException 
     */
    private void saveRow(BufferedWriter out, int row) throws IOException {
        Polylist r = dataModel.getRow(row);
        Polylist name = Polylist.list("name", r.first());
        Polylist notes = (Polylist) r.second();
        Polylist chords = (Polylist) r.third();
        int grade = (int) (Integer) r.fourth();
        Polylist lick = Polylist.list("lick", notes.cons("notes"), chords.cons("sequence"), name, Polylist.list("grade", grade));

        // Boolean used atomically to track potential errors
        AtomicBoolean error = new AtomicBoolean(false);
        ArrayList<Note> noteList = new ArrayList<Note>();
        ArrayList<Chord> chordList = new ArrayList<Chord>();
        StringBuilder output = new StringBuilder();
        int beatPosition = 0;
        int chordLengthAccum = 0;

        output.append(String.valueOf(grade / 10.0));
        output.append(' ');
        
        // Prepare a score so that Chord lengths can be determined
        ChordPart chordsList = new ChordPart(BEAT*8);
        MelodyPart melody = new MelodyPart(BEAT*8);
        Polylist combined = chords.append(notes);
        (new SetChordsCommand(0, combined, chordsList, melody)).execute();
        chordsList.setStyle(Preferences.getPreference(Preferences.DEFAULT_STYLE));
        Score score = new Score();
        score.setChordProg(chordsList);
        score.addPart(melody);
        ArrayList<ChordSymbol> symbols = score.getChordProg().getChordSymbols();
        ArrayList<Integer> durations = score.getChordProg().getChordDurations();
        
        // Add all chords to the chord list
        for (int i = 0; i < symbols.size(); i++)
        {
            chordList.add(new Chord(symbols.get(i), durations.get(i)));
        }
        
        // Add all notes to the note list
        while(!notes.isEmpty()) {
            while(!notes.isEmpty() && notes.first() == null) {
                notes = notes.rest();
            }
            
            if(!notes.isEmpty()) {
                noteList.add(NoteSymbol.toNote(notes.first().toString()));
                notes = notes.rest();
            }
        }
        
        // Print all note data for all notes within one lick
        for (int index = 0; index < noteList.size(); index++)
        {
            int indexPrev = index - 1;
            int indexNext = index + 1;
            Chord chordCurr = null;
            Chord chordNext= null;
            
            if (!chordList.isEmpty())
            {
                chordCurr = chordList.get(0);
                if(chordList.size() > 1)
                {
                    chordNext = chordList.get(1);
                }
            }
            
            if (indexPrev < 0)
            {
                beatPosition = printNoteData(output, null, noteList.get(index), 
                        noteList.get(indexNext),
                        chordCurr, chordNext, beatPosition, error);
            }
            else if (indexNext >= noteList.size())
            {
                beatPosition = printNoteData(output, noteList.get(indexPrev), 
                        noteList.get(index), null,
                        chordCurr, chordNext, beatPosition, error);
            }
            else
            {
                beatPosition = printNoteData(output, noteList.get(indexPrev), 
                        noteList.get(index), noteList.get(indexNext),
                        chordCurr, chordNext, beatPosition, error);
            }
            
            // Updates the current chord based on the current slot.
            if(!chordList.isEmpty() && chordCurr != null 
                    && chordCurr.getRhythmValue() + chordLengthAccum <= beatPosition)
            {
                chordLengthAccum += chordCurr.getRhythmValue();
                chordList.remove(0);
            }
        }
        
        output.append(lick.toString());
        
        if (!error.get())
        {
            out.write(output.toString());
            out.newLine();
        }
        else
        {
            System.out.println(output.toString());
            System.out.println("Error from parsing data: Will not save this lick.");
        }
    }

    // FIX: Split lots into seperate sequences
    /**
     * Saves note data as an 18-bit vector.
     * Bit 1      - Rest/Not rest
     * Bit 2      - Sonorous/Dissonant
     * Bit 3      - Chord/Color or Approach/Foreign Tone
     * Bit 4-7    - Distance from previous note, capped at 15
     * Bit 8-12   - Thermometer encoding for beat placement
     * Beat 13-18 - Represents type of note in the following way:
     *              WHOLE-HALF-QUARTER-EIGHTH-SIXTEENTH-TRIPLET
     * @param out
     * @param notePrev Used for note distance
     * @param noteCurr
     * @param noteNext Used for note classification
     * @param chordCurr Used for note classification
     * @param chordNext Used for note classification
     * @param beatPos
     * @param error Keeps track of any potential note parsing errors
     * @return beatPos, to keep track of the current slot
     * @throws IOException 
     */
    public static int printNoteData(StringBuilder out, Note notePrev, Note noteCurr, Note noteNext, 
                              Chord chordCurr, Chord chordNext, int beatPos, AtomicBoolean error) {
        
        char[] bits = {'0', ' ', '0', ' ', '0', ' ',
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ', 
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' ', 
                       '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' ', '0', ' '};
        int currPos = 0;

        // The first three bits are for note classification   
        if (noteCurr.isRest())
        {
            bits[currPos] = '1';
            currPos += 6;
        }
        
        else
        {
            currPos += 2;
            
            int classification = chordCurr.classify(noteCurr, noteNext, chordNext);

            if (classification == CHORD_TONE)
            {
                bits[currPos] = '1';
                currPos += 2;
                bits[currPos] = '1';
                currPos += 2;
            }
            else if (classification == COLOR_TONE)
            {
                bits[currPos] = '1';
                currPos += 2;
                bits[currPos] = '0';
                currPos += 2;
            }
            else if (classification == APPROACH_TONE)
            {
                bits[currPos] = '0';
                currPos += 2;
                bits[currPos] = '1';
                currPos += 2;
            }
            else if (classification == FOREIGN_TONE)
            {
                bits[currPos] = '0';
                currPos += 2;
                bits[currPos] = '0';
                currPos += 2;
            }
        }
        
        // The next 4 bits dictate the distance of the note from a preceding note
        int distance = 0;
        if(notePrev != null)
             if(!noteCurr.isRest() && !notePrev.isRest())
        {
            distance = Math.abs(noteCurr.getPitch() - notePrev.getPitch());
        }
        // In case the note distance is too large
        if (distance > 15)
        {
            distance = 15;
        }

        char[] binaryInts = {'0', '0', '0', '0'};
        char[] intToBinary = Integer.toBinaryString(distance).toCharArray();
        
        int difference = binaryInts.length - intToBinary.length;
        System.arraycopy(intToBinary, 0, binaryInts, difference, intToBinary.length);
        for (char b : binaryInts)
        {
            bits[currPos] = b;
            currPos += 2;
        }
        
        // Determine a 5-bit thermometer encoding for the placement of the note on the beat
        // FIX: For quarter triplets, what am I encoding them as?
        String encoding;
        if (beatPos % WHOLE == 0) //Beat 1
        {
            encoding = "11111";
        }
        else if (beatPos % WHOLE == HALF) //Beat 3
        {
            encoding = "11110";
        }
        else if (beatPos % WHOLE == QUARTER 
                 || beatPos % 480 == QUARTER + HALF) //Beat 2 or 4
        {
            encoding = "11100";
        }
        else if (beatPos % WHOLE == EIGHTH 
                 || beatPos % WHOLE == EIGHTH + QUARTER
                 || beatPos % WHOLE == EIGHTH + HALF
                 || beatPos % WHOLE == EIGHTH + QUARTER + HALF) //Eighth note beats
        {
            encoding = "11000";
        }
        else
        {
            encoding = "10000";
        }
        char[] encodingBits = encoding.toCharArray();
        for (char b : encodingBits)
        {
            bits[currPos] = b;
            currPos += 2;
        }
        
        // Determine 6-bit beat durations and encode them as such:
        // WHOLE HALF QUARTER EIGHTH SIXTEENTH TRIPLET
        char[] durationEncoding = {'0', '0',  '0', '0', '0', '0'};
        
        StringBuilder buffer = new StringBuilder();
        int duration = noteCurr.getRhythmValue();
        int value = Note.getDurationString(buffer, duration);

        if (value != 0)
        {
            System.out.println("Extra residual unaccounted for.");
        }
        
        String noteValues;
        if(buffer.toString().matches("\\+.*"))
        {
            noteValues = buffer.toString().substring(1); //Trims leading "+"
        }
        else
        {
            noteValues = buffer.toString();
        }
        String[] values = noteValues.split("\\+");
        
        for (String note : values)
        {
            // FIX: Split into separate method for clarity
            if (note.equals("1"))
                durationEncoding[0] = '1'; //WHOLE note
            else if (note.equals("2"))
                durationEncoding[1] = '1'; //HALF note
            else if (note.equals("4")) 
                durationEncoding[2] = '1'; //QUARTER note
            else if (note.equals("8")) 
                durationEncoding[3] = '1'; //EIGHTH note
            else if (note.equals("16")) 
                durationEncoding[4] = '1'; //SIXTEENTH note
            else if (note.equals("2/3"))
            {
                durationEncoding[1] = '1'; //HALF_TRIPLET
                durationEncoding[5] = '1';
            }
            else if (note.equals("4/3"))
            {
                durationEncoding[2] = '1'; //QUARTER_TRIPLET
                durationEncoding[5] = '1';
            }
            else if (note.equals("8/3"))
            {
                durationEncoding[3] = '1'; //EIGHTH_TRIPLET
                durationEncoding[5] = '1';
            }
            else if (note.equals("16/3"))
            {
                durationEncoding[4] = '1'; //SIXTEENTH_TRIPLET
                durationEncoding[5] = '1';
            }
                
            else
            {
                System.out.println("Note resolution of " + note + " is currently unsupported.");
                error.set(true);
            }
        }
        for (char b : durationEncoding)
        {
            bits[currPos] = b;
            currPos += 2;
        }
        
        // Used for debugging
        /*
        for (char b : bits)
            System.out.print(b);
        System.out.println();
        */
        
        // Write the data
        out.append(bits);
        
        // Move the beat position up by the current duration.
        beatPos += duration;
        return beatPos;
    }
    
    /**
     * Previous way to save data for the critic. 
     * Uses longer bit vectors comprised of chords and individual notes.
     * Changed to accommodate the use of new printing methods.
     * @param out
     * @param row
     * @throws IOException 
     */
    private void saveRowOld(BufferedWriter out, int row) throws IOException {
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
    
    /** Variables for printing a note symbol at a particular resolution. */
    private final int BOTTOMNOTE = 60;
    private final int TOPNOTE = 83;
    //Changed from EIGHTH to THIRTYSECOND_TRIPLET in order to obtain a finer note resolution.
    private final int MINDURATION = THIRTYSECOND_TRIPLET;
    
    /**
     * Previous way of saving note data, as a bit vector.
     * Resulted in very large inputs for the neural network, 
     * making it hard to train. 
     * Changed to a new bit vector input method.
     * @param out
     * @param note
     * @throws IOException 
     */
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
    
    /**
     * Saves chord in a bit vector representation.
     * 12 bits long, unique for every chord.
     * @param out
     * @param chord
     * @throws IOException 
     */
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

        private ArrayList<Polylist> data = new ArrayList<Polylist>(); //Changed from Vector

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

        @Override
        public String getColumnName(int col) {
            return TCol.values()[col].toString();
        }
        
        @Override
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

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex != TCol.PLAYBTN.ordinal();
        }

        @Override
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
