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

package imp.data;

import imp.ImproVisor;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.LeadsheetFileView;
import imp.util.MidiFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import javax.swing.JFileChooser;

/**
 * Midi File Importing
 *
 * @author keller, adapted from code in MIDIBeast by Brandy McMenamy and Jim
 * Herold
 */

public class MidiImport
{

Notate notate;
private static jm.music.data.Score score;
private static ArrayList<jm.music.data.Part> allParts;

public static int beat = 120;
public static int precision = 5;
private JFileChooser midiFileChooser = new JFileChooser();

public MidiImport(Notate notate)
  {
    this.notate = notate;
    initFileChooser();
  }


public LinkedList<MidiImportRecord> importMidi()
  {
    File file = getFile();
    if( file != null )
      {
        return readMidiFile(file.getAbsolutePath());
      }
    return null;
  }


/**
 * @param String midiFileName
 * 
 */

public LinkedList<MidiImportRecord> readMidiFile(String midiFileName)
  {
    notate.setStatus("MIDI File Imported");
    
    score = new jm.music.data.Score();
    
    allParts = new ArrayList<jm.music.data.Part>();

    jm.util.Read.midi(score, midiFileName);

    //System.out.println("score from MIDI = " + score);

    MIDIBeast.calculateNoteTypes(score.getDenominator());

    allParts.addAll(Arrays.asList(score.getPartArray()));

    ImportMelody importMelody = new ImportMelody(score);

    //System.out.println("importMelody = " + importMelody);

    LinkedList<MidiImportRecord> records = new LinkedList<MidiImportRecord>();
    
    for( int i = 0; i < importMelody.size(); i++ )
      {
      try
        {
        jm.music.data.Part part = importMelody.getPart(i);
        int channel = part.getChannel();
        //System.out.println("---------------------------------------------");
        //System.out.println("part " + i + " raw = " + part);
        int numTracks = part.getSize();
        for( int j = 0; j < numTracks; j++ )
          {
            //System.out.println("part " + i + " track " + j + " converted = ");
            MelodyPart partOut = new MelodyPart();
            importMelody.convertToImpPart(part, j, partOut);
            
            MidiImportRecord record = new MidiImportRecord(channel, j, partOut);
            records.add(record);
            //System.out.println(partOut);
            
            //notate.addChorus(partOut);
          }
        }
      catch( java.lang.OutOfMemoryError e )
        {
        ErrorLog.log(ErrorLog.SEVERE, "There is not enough memory to continue importing this MIDI file.");
        return null;
        }
      }
    
//    for( MidiImportRecord record: records )
//      {
//        System.out.println(record);
//      }
    
    return records;
  }


private File getFile()
  {
    File midiFileEntire = null;
    try
      {
        int midiChoice = midiFileChooser.showOpenDialog(notate);
        if( midiChoice == JFileChooser.CANCEL_OPTION )
          {
            return null;
          }
        if( midiChoice == JFileChooser.APPROVE_OPTION )
          {
            midiFileEntire = midiFileChooser.getSelectedFile();
          }
        String nameForDisplay = midiFileChooser.getSelectedFile().getName();
      }
    catch( Exception e )
      {
      }

    return midiFileEntire;
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

}
