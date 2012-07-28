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

import imp.util.ErrorLog;
import java.io.File;
import java.util.*;

/**
 * Midi File Importing
 *
 * @author Robert Keller, partly adapted from code in MIDIBeast 
 * by Brandy McMenamy and Jim Herold
 */

public class MidiImport
{
public final static int DRUM_CHANNEL = 9;
File file;
private int defaultResolution = 1;
private int resolution;
private static jm.music.data.Score score;
private static ArrayList<jm.music.data.Part> allParts;
private LinkedList<MidiImportRecord> melodies;
private Map<Integer, String> channelNames = new HashMap<Integer, String>();
private int chordChannel = -1;
private int bassChannel = -1;

public MidiImport()
  {
    setResolution(defaultResolution);
   }

public int getResolution()
  {
    return resolution;
  }

public final void setResolution(int newResolution)
  {
    resolution = newResolution;
    //System.out.println("setting resolution to " + resolution);
  }

public void importMidi(File file)
  {
    if( file != null )
      {
        readMidiFile(file.getAbsolutePath());
      }
  }


/**
 * @param String midiFileName
 * 
 */

public void readMidiFile(String midiFileName)
  {
    score = new jm.music.data.Score();
    
    try
      {
      jm.util.Read.midi(score, midiFileName);
      }
    catch( Error e )
      {
        ErrorLog.log(ErrorLog.WARNING, "reading of MIDI file " + midiFileName 
                     + " failed for some reason (jMusic exception).");
        return;
      }
    
    scoreToMelodies();
  }

    
public void scoreToMelodies()
  {
    //System.out.println("score from MIDI = " + score);
  if( score != null )
    {
    //MIDIBeast.setResolution(resolution);

    allParts = new ArrayList<jm.music.data.Part>();

    allParts.addAll(Arrays.asList(score.getPartArray()));

    ImportMelody importMelody = new ImportMelody(score);

    //System.out.println("importMelody = " + importMelody);

    melodies = new LinkedList<MidiImportRecord>();
    
    //create channel names
    channelNames.clear();
    
    for( int i = 0; i < importMelody.size(); i++ )
      {
      try
        {
        jm.music.data.Part part = importMelody.getPart(i);
        int channel = part.getChannel();
        //System.out.println("part " + i + " raw = " + part);
        int numTracks = part.getSize();
        
        //add instrument names to channel
        if (part!=null&&channel!=9)
        {
            String instrumentName = MIDIBeast.getInstrumentForPart(part);
            instrumentName = instrumentName.replaceAll("_"," ");
            channelNames.put(channel+1,instrumentName);
            
            //choose chord and bass channel
            int instrumentNum = part.getInstrument();
            int channelNum = part.getChannel();
                if (instrumentNum >= 0 && instrumentNum <=5) {
                    //If the instrument is a kind of keyboard or guitar, it is read as a chords part.
                    chordChannel = channelNum;
                }
                else if (instrumentNum >= 24 && instrumentNum <= 28)
                {
                    chordChannel = channelNum;
                }
                else if (instrumentNum >= 32 && instrumentNum <= 38) {
                    bassChannel = channelNum;
                }
            }
        
        for( int j = 0; j < numTracks; j++ )
          {
            //System.out.println("---------------------------------------------");
            //System.out.println("part " + i + " track " + j + " conversion: ");
            MelodyPart partOut = new MelodyPart();
            importMelody.convertToImpPart(part, j, partOut, resolution);
            
            String instrumentString = MIDIBeast.getInstrumentForPart(part);
            
            if( channel != DRUM_CHANNEL )
               {
                partOut.setInstrument(part.getInstrument());
               }
            
            MidiImportRecord record = new MidiImportRecord(channel, j, partOut, instrumentString);
            melodies.add(record);
          }
        }
      catch( java.lang.OutOfMemoryError e )
        {
        ErrorLog.log(ErrorLog.SEVERE, "There is not enough memory to continue importing this MIDI file.");
        return;
        }
      }
    
    Collections.sort(melodies);
    
//    for( MidiImportRecord record: melodies )
//      {
//        System.out.println(record);
//      }
    
    }
  }
    
    public class ChannelInfo {

        private int channelNum;
        private String channelName;
        
        public ChannelInfo(int num, String name) {
            this.channelNum = num;
            this.channelName = name;
        }
        
        public ChannelInfo() {
            this.channelNum = 0;
            this.channelName = null;
        }
        
        public int getChannelNum() {
            return this.channelNum;
        }
        
        public void setChannelNum(int num) {
            this.channelNum = num;
        }
        
        public void setChannelName(String name) {
            this.channelName = name;
        }
        
        @Override
        public String toString() {
            if (channelNum != 0) {
                return channelNum + " : " + channelName;
            } else {
                return channelName;
            }
        }
    }
    
    public jm.music.data.Score getScore() {
        return score;
    }

    public LinkedList<MidiImportRecord> getMelodies() {
        return melodies;
    }

    public ChannelInfo[] getChannelInfo() {
        ChannelInfo[] channelInfo = null;
        channelInfo = new ChannelInfo[channelNames.size() + 1];
        int index = 0;
        channelInfo[index] = new ChannelInfo();
        channelInfo[index].setChannelNum(0);
        channelInfo[index].setChannelName("None");
        index = index + 1;
        for (Map.Entry<Integer, String> entry : channelNames.entrySet()) {
            channelInfo[index] = new ChannelInfo();
            channelInfo[index].setChannelNum(entry.getKey());
            channelInfo[index].setChannelName(entry.getValue());
            index = index + 1;
    }
        return channelInfo;
    }
    
    public int getChordChannel(){
        return chordChannel;
    }
    public int getBassChannel(){
        return bassChannel;
    }
}
