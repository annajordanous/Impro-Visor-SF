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
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Score;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Receiver;
import javax.sound.midi.Sequencer;

/**
 *
 * @author Martin Hunt. Robert Keller added countInOffset stuff 7/12/2010
 */

public class MidiRecorder implements Constants, Receiver
{
Notate notate;

MelodyPart melodyPart;

int[] metre;

Score score;
Sequencer sequencer = null;
int countInOffset;
long noteOn = 0, noteOff = 0, lastEvent = 0;
boolean notePlaying = false;
int prevNote = 0;
int resolution;
int snapTo = BEAT/4;
int beatValue;
int measureLength;

double latency = 0;

public MidiRecorder(Notate notate, Score score)
  {
    this.notate = notate;
    this.score = score;
    this.melodyPart = notate.getCurrentOrigPart();
    metre = melodyPart.getMetre();
    beatValue = (WHOLE/metre[1]);
    measureLength = metre[0] * beatValue;
  }

public double getLatency()
  {
    return latency;
  }

public void setLatency(double latency)
  {
    this.latency = latency;
  }

public long getTime()
  {
    if( sequencer != null && sequencer.isRunning() )
      {
        return sequencer.getMicrosecondPosition();
      }
    else
      {
        notate.stopRecording();
        return -1;
      }
  }

public long getTick()
  {
    if( sequencer != null && sequencer.isRunning() )
      {
        int res = sequencer.getSequence().getResolution();             // ticks per beat
        double bpms = ((double) sequencer.getTempoInBPM()) / 60000;    // beats per millisecond
        long latencyTicks = Math.round(bpms * res * latency);

        return sequencer.getTickPosition() - latencyTicks;
      }
    else
      {
        notate.stopRecording();
        return -1;
      }
  }

    
void start()
  {
    snapTo = BEAT/notate.getRecordSnapValue();
    this.sequencer = notate.getSequencer();
    if( sequencer.getSequence() == null )
      {
        return;
      }
    resolution = sequencer.getSequence().getResolution();

    while( (noteOn = getTick()) < 0 )
      {
      }

    noteOff = noteOn = getTick();
    notePlaying = false;
    notate.setCurrentSelectionStartAndEnd(0);

    countInOffset = score.getCountInOffset();
  }


/**
 * This function is called by others to send a MIDI message to this object for
 * processing.
 */
    
public void send(MidiMessage message, long timeStamp)
  {
    //System.out.println("received " + MidiFormatting.midiMessage2polylist(message));

    byte[] m = message.getMessage();
    int note, channel, velocity;
    int highNibble = (m[0] & 0xF0) >> 4;
    int lowNibble = m[0] & 0x0F;

    switch( highNibble )
      {
        case 9: // note on
            lastEvent = getTick();
            if( lastEvent < 0 )
              {
                break;
              }

            channel = lowNibble;
            note = m[1];
            velocity = m[2];
            if( velocity == 0 )
              {
                // this is actually a note-off event, done to allow 
                // 'running status': 
                // http://www.borg.com/~jglatt/tech/midispec/run.htm
                
                handleNoteOff(note, velocity, channel);
              }
            else
              {
//              System.out.println("Note: " + note + "; Velocity: " + velocity + "; Channel: " + channel);
                handleNoteOn(note, velocity, channel);
              }
            break;
            
        case 8: // note off
            lastEvent = getTick();
            if( lastEvent < 0 )
              {
                break;
              }

            channel = lowNibble;
            note = m[1];
            velocity = m[2];
            
            handleNoteOff(note, velocity, channel);
            break;
      }
    //System.out.println("done with " + MidiFormatting.midiMessage2polylist(message));
  }
    
    
void handleNoteOn(int note, int velocity, int channel)
  {
    //System.out.println("noteOn: " + noteOn + "; noteOff: " + noteOff + "; event: " + lastEvent);
    // new note played, so finish up previous notes or insert rests up to the current note
    int noteOffIndex;
    if( notePlaying )
      {
        handleNoteOff(prevNote, velocity, channel);
      }
    else
      {
        int duration = snapSlots(tickToSlots(noteOff, lastEvent));

        // this try is here because a function a few steps up in the call hierarchy tends to capture error messages
        try
          {
            noteOffIndex = snapSlots(tickToSlots(noteOff)) - countInOffset;

            // add rests since nothing was played between now and the previous note
            if( duration > 0 && noteOffIndex >= 0 )
              {
                // Note noteToAdd = new Rest(duration);
                // setNote(noteOnIndex, noteToAdd);
              }

            if( noteOffIndex >= 0 )
              {
                notate.setCurrentSelectionStartAndEnd(noteOffIndex);
              }
          }
        catch( Exception e )
          {
            //ErrorLog.log(ErrorLog.SEVERE, "Internal exception in MidiRecorder: " + e);
          }
      }

    noteOn = lastEvent;
    noteOffIndex = snapSlots(tickToSlots(noteOn)) - countInOffset;

    // add current note
    Note noteToAdd = new Note(note, snapTo);

    try
      {
        noteToAdd.setEnharmonic(score.getCurrentEnharmonics(noteOffIndex));
        setNote(noteOffIndex, noteToAdd);
      }
    catch( Exception e )
      {
        //ErrorLog.log(ErrorLog.SEVERE, "Internal exception in MidiRecorder: " + e);
      }

    //notate.repaint();

    prevNote = note;
    notePlaying = true;
  }
 
 void handleNoteOff(int note, int velocity, int channel)
  {
    //System.out.println("noteOff: " + noteOff + "; event: " + lastEvent);

    if( note != prevNote )
      {
        return;
      }

    // use the one in constructor: Notate notate = imp.ImproVisor.getCurrentWindow();
    noteOff = lastEvent;
    
    notePlaying = false;

    int noteOnIndex = snapSlots(tickToSlots(noteOn)) - countInOffset;

    if( noteOnIndex < 0 )
      {
        return;
      }

    int duration = snapSlots(tickToSlots(noteOn, noteOff));

    if( duration == 0 )
      {
      }
    else
      {
        Note noteToAdd = new Note(note, duration);
        noteToAdd.setEnharmonic(score.getCurrentEnharmonics(noteOnIndex));
        setNote(noteOnIndex, noteToAdd);
      }
    
    noteOnIndex += duration;

    notate.setCurrentSelectionStartAndEnd(noteOnIndex);

    // System.out.println("duration: " + duration + "; corrected: " + ((double) slots) / BEAT);

    //notate.repaint();
  }
 
 private void setNote(int index, Note noteToAdd)
  {
  melodyPart.setNoteAndLength(index, noteToAdd, notate); // more than needed
  //melodyPart.setNote(noteOnIndex, noteToAdd);
 }
   
int snapToMultiple(int input, int base)
  {
    return base * (int) Math.round(((double) input) / base);
  }

int microsecondsToSlots(long start, long finish)
  {
    return microsecondsToSlots(finish - start);
  }

int microsecondsToSlots(long duration)
  {
    double tempo = score.getTempo();
    return (int) (duration / 1000000.0 * (tempo / 60) * BEAT);
  }

int tickToSlots(long start, long finish)
  {
    return tickToSlots(finish - start);
  }

int tickToSlots(long duration)
  {
    return (int) (BEAT * duration / resolution);
  }

int snapSlots(int slots)
  {
    slots = snapToMultiple(slots, snapTo);
    return slots;
  }

public void close()
  {
  }
}
