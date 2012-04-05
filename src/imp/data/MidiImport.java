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
import imp.util.LeadsheetFileView;
import imp.util.MidiFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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
private static double denominator = 4;
private static ArrayList<jm.music.data.Part> allParts;
public static int whole;
public static int half;
public static int halftriplet; // rk
public static int quarter;
public static int quartertriplet; //rk
public static int eighth;
public static int eighthtriplet;
public static int sixteenth;
public static int sixteenthtriplet;
public static int thirtysecond;
public static int thirtysecondtriplet;
public static int sixtyfourth;
public static int sixtyfourthtriplet;
public static int beat = 120;
public static int precision = 5;
private JFileChooser midiFileChooser = new JFileChooser();

public MidiImport(Notate notate)
  {
    this.notate = notate;
    initFileChooser();
  }


public void importMidi()
  {
    File file = getFile();
    if( file != null )
      {
        readMidiFile(file.getAbsolutePath());
      }
  }


/**
 * @param String midiFile
 * @param String chordFile This method needs to be called before anything else
 * is done with MIDIBeast. It will take the midi and chord file and get basic
 * info about the song, Time Signature, note rhythm values, and all instruments
 * found in the song.
 */

public void readMidiFile(String midiFileName)
  {
    score = new jm.music.data.Score();
    allParts = new ArrayList<jm.music.data.Part>();

    jm.util.Read.midi(score, midiFileName);

    //System.out.println("score from MIDI = " + score);

    denominator = score.getDenominator();

    calculateNoteTypes();

    jm.music.data.Part[] temp = score.getPartArray();

    allParts.addAll(Arrays.asList(temp));

    ImportMelody importMelody = new ImportMelody(score);

    //System.out.println("importMelody = " + importMelody);

    int partNo = 0;
    for( int i = 0; i < importMelody.size(); i++ )
      {
        jm.music.data.Part part = importMelody.getPart(i);
        //System.out.println("---------------------------------------------");
        //System.out.println("part " + i + " raw = " + part);
        int numTracks = part.getSize();
        for( int j = 0; j < numTracks; j++ )
          {
            //System.out.println("part " + i + " track " + j + " converted = ");
            MelodyPart partOut = new MelodyPart();
            importMelody.convertToImpPart(part, j, partOut);
            //System.out.println(partOut);
            notate.addChorus(partOut);
          }
      }
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


/**
 * Given a time signature and the number of slots specified per beat, this
 * method calulates how many slots each type of note should get. If a note type
 * is note possible it is assigned a value of -1.
 *
 * This and the next method were fouling on half note triplets and quarter note
 * triplets. I changed them on 12/1/2007. However, the whole thing should be
 * checked over carefully. RK
 */
public static void calculateNoteTypes()
  {
    whole = (int) (denominator * beat);

    if( whole % 3 == 0 )
      {
        halftriplet = whole / 3;
        precision = halftriplet;
      }
    else
      {
        halftriplet = -1;
      }

    half = whole / 2;

    if( half % 3 == 0 )
      {
        quartertriplet = half / 3;
        precision = quartertriplet;
      }
    else
      {
        quartertriplet = -1;
      }

    quarter = whole / 4;

    precision = eighth = whole / 8;

    if( quarter % 3 == 0 )
      {
        eighthtriplet = quarter / 3;
        precision = eighthtriplet;
      }
    else
      {
        eighthtriplet = -1;
      }

    if( whole % 16 == 0 )
      {
        sixteenth = whole / 16;
        precision = sixteenth;
      }
    else
      {
        sixteenth = -1;
      }

    if( eighth % 3 == 0 )
      {
        sixteenthtriplet = eighth / 3;
        precision = sixteenthtriplet;
      }
    else
      {
        sixteenthtriplet = -1;
      }

    if( whole % 32 == 0 )
      {
        thirtysecond = whole / 32;
        precision = thirtysecond;
      }
    else
      {
        thirtysecond = -1;
      }

    if( sixteenth % 3 == 0 )
      {
        thirtysecondtriplet = sixteenth / 3;
        precision = sixteenth;
      }
    else
      {
        thirtysecondtriplet = -1;
      }

    if( whole % 64 == 0 )
      {
        sixtyfourth = whole / 64;
        precision = whole;
      }
    else
      {
        sixtyfourth = -1;
      }

    if( thirtysecond % 3 == 0 )
      {
        sixtyfourthtriplet = thirtysecond / 3;
        precision = sixtyfourthtriplet;
      }
    else
      {
        sixtyfourthtriplet = -1;
      }
  }

}
