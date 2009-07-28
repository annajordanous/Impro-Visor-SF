/**
 * This Java Class is part of the Impro-Visor Application
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

package imp.data;

import imp.util.*;
import imp.Constants;

import java.util.*;
import javax.sound.midi.*;

/**
 * Created: Mon May 07 11:21:30 2001
 *
 * @author Mark Elston (enhanced by Andrew Brown and 
 *                      endSequence by Stephen Jones)
 *
 * Integrated with the MidiManager by Martin Hunt, June 2006
 *    added volume control
 *    support for multiple windows, each with it's own mixer
 *
 * Notes: 
 *  - if you close the sequencer, you can't just reopen it because the transmitter
 *    is no longer in existence, so you need to get a new transmitter and 
 *    register it with the MidiSystem so that receiver can play the notes 
 *    transmitted
 */

public class MidiSynth implements Constants, MetaEventListener
{

/** Pulses per quarter note value */
private short m_ppqn;

/** The overall (Score) tempo value */
private float tempo;

/** The diff between the score and part tempi */
private double trackTempoRatio = 1.0;

/** The diff between the score and phrase tempi */
private double elementTempoRatio = 1.0;

/** The name of the jMusic score */
private String scoreTitle;

/** sets the MIDI resolution */
private final static short DEFAULT_PPQ = 480;

/** end of track */
private final static int StopType = 47;

private MidiManager midiManager;

private Mixer volumeControl = null;

private Sequencer sequencer = null;

private boolean paused = false;

private boolean playing = false;

private MidiPlayListener playListener = null;

private static long playCounter = 0;

public MidiSynth(MidiManager midiManager)
  {
    this(midiManager, DEFAULT_PPQ);
  }

public MidiSynth(MidiManager midiManager, short ppqn)
  {
    this.midiManager = midiManager;
    m_ppqn = ppqn;

    volumeControl = new Mixer(NUM_CHANNELS);

    /* the instance receives messages, sends them through the mixer (volumeControl),
     * and the volumeControl transmits them to the midiManager for tranmission to
     * the actual device
     */
    midiManager.registerTransmitter(volumeControl);
  }

public void setTempo(float value)
  {
    tempo = value;
    if( sequencer != null )
      {
        sequencer.setTempoInBPM(value);
      }
  }

public long getMicrosecond()
  {
    if( sequencer == null )
      {
        return 0;
      }
    return (long) (sequencer.getMicrosecondPosition() * 120.0 / tempo);
  }

public long getTotalMicroseconds()
  {
    if( sequencer == null )
      {
        return 0;
      }
    return (long) (sequencer.getMicrosecondLength() * 120.0 / tempo);
  }

public void setMicrosecond(long position)
  {
    if( sequencer == null )
      {
        return;
      }
    midiManager.sendAllSoundsOffMsg();
    sequencer.setMicrosecondPosition((long) (position * tempo / 120.0));
    sequencer.setTempoInBPM(tempo);
  }

public void setSlot(long slot)
  {
    if( sequencer == null )
      {
        return;
      }
    sequencer.setTickPosition((long) (slot * m_ppqn / BEAT));
    sequencer.setTempoInBPM(tempo);
  }

public int getSlot()
  {
    if( playing )
      {
        return (int) Math.floor(
            BEAT * sequencer.getTickPosition() / (double) m_ppqn);
      }
    else
      {
        return 0;
      }
  }

public int getTotalSlots()
  {
    if( playing )
      {
        return (int) Math.floor(
            BEAT * sequencer.getTickLength() / (double) m_ppqn);
      }
    else
      {
        return 0;
      }
  }

public boolean finishedPlaying()
  {
    return sequencer.getTickPosition() >= sequencer.getTickLength();
  }

public void play(Score score, long startTime, int loopCount, int transposition)
    throws InvalidMidiDataException
  {
    play(score, startTime, loopCount, transposition, true);
  }

public void play(Score score, long startTime, int loopCount, int transposition,
                 boolean useDrums)
    throws InvalidMidiDataException
  {
    play(score, startTime, loopCount, transposition, useDrums);
  }

static int magic_looping_factor = 4; // Why this?


/**
 * Plays the score data via a MIDI synthesizer
 * @param score   Score data to change to SMF
 * @exception Exception
 */
public void play(Score score, long startIndex, int loopCount, int transposition,
                 boolean useDrums, int endLimitIndex)
    throws InvalidMidiDataException
  {
    Trace.log(2,
              (++playCounter) + ": Starting MIDI sequencer, startTime = " 
              + startIndex + " loopCount = " + loopCount + " endIndex = "
              + endLimitIndex);

    if( sequencer == null )
      {
        setSequencer();
      }

    scoreTitle = score.getTitle();

    tempo = (float) score.getTempo();

    Sequence seq = score.sequence(m_ppqn, transposition, useDrums, endLimitIndex);

    if( null != seq )
      {
        try
          {
            sequencer.open();
          }
        catch( MidiUnavailableException e )
          {
            ErrorLog.log(ErrorLog.SEVERE, "MIDI System Unavailable:" + e);
            return;
          }
        sequencer.setSequence(seq);
        sequencer.addMetaEventListener(this);
        sequencer.setTempoInBPM(tempo);

        // Clear possible old values
        sequencer.setLoopStartPoint(0);
        sequencer.setLoopEndPoint(-1);
        sequencer.setLoopCount(0);

        setSlot(startIndex);

        System.runFinalization();
        System.gc();

        if( loopCount != 0 )
        {
                // set end time first, otherwise there might be an exception because
                // the start time is too large.

        int magicFactor = Style.getMagicFactor();

        if( endLimitIndex > 0 )
          {
          sequencer.setLoopEndPoint(endLimitIndex * magicFactor);
          }


        sequencer.setLoopStartPoint(startIndex * magicFactor);

        if( loopCount < 0 )
          {
            sequencer.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
          }
        else if( loopCount > 0 )
          {
            try
              {
                sequencer.setLoopCount(loopCount);

              }
            catch( IllegalArgumentException e )
              {
                ErrorLog.log(ErrorLog.SEVERE,
                             "internal problem looping: start = "
                             + startIndex + ", end = " + endLimitIndex
                             + " tempoFactor = " + sequencer.getTempoFactor());
              }
          }
        }

        // Here's where the playback actually starts:

        sequencer.start();

        playing = true;
        paused = false;
        if( playListener != null && sequencer.isRunning() )
          {
            playListener.setPlaying(MidiPlayListener.Status.PLAYING,
                                    transposition);
          }
      }
  }

/**
 * Invoked when a Sequencer has encountered and processed a MetaMessage
 * in the Sequence it is processing.
 * @param metaEvent      the MetaMessage that the sequencer encountered
 */
public void meta(MetaMessage metaEvent)
  {
    Trace.log(2, playCounter + ": MidiSynth metaEvent: " + metaEvent);
    if( metaEvent.getType() == StopType )
      {
        stop();
      }
  }

public void pause()
  {
    Trace.log(2, playCounter + ": Pausing MidiSynth, paused was " + paused);
    if( paused )
      {
        sequencer.start();
        sequencer.setTempoInBPM(tempo);
        paused = false;
        playListener.setPlaying(MidiPlayListener.Status.PLAYING, 0);
      }
    else
      {
        sequencer.stop();
        paused = true;
        playListener.setPlaying(MidiPlayListener.Status.PAUSED, 0);
      }
  }

public void setPlayListener(MidiPlayListener listener)
  {
    Trace.log(2, playCounter + ": Setting MidiPlayListener ");
    if( playListener != null )
      {

        playListener.setPlaying(MidiPlayListener.Status.STOPPED, 0);
      }
    playListener = listener;
  }

/**
 * Stop sequencer object
 */
public void stop()
  {
    Trace.log(2, playCounter + ": Stopping MIDI sequencer ");
    playing = false;
    paused = false;

    // This seems to be causing problems in the style editor cell and column play
    if( sequencer != null && sequencer.isOpen() )
      {
        sequencer.stop();
      }


    // this should be the LAST thing this function does before returning
    if( playListener != null )
      {
        playListener.setPlaying(MidiPlayListener.Status.STOPPED, 0);
      }

  }

public void close()
  {
    stop();

    if( sequencer != null && sequencer.isOpen() )
      {
        sequencer.close();
      }
  }

/**
 * Create a Note On Event
 * @param channel   the channel to change
 * @param pitch     the pitch of the note
 * @param velocity  the velocity of the note
 * @param tick      the time this event occurs
 */
protected static MidiEvent createNoteOnEvent(int channel,
                                             int pitch, int velocity,
                                             long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0x90 + channel, pitch, velocity);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

/**
 * Create a Note Off Event
 * @param channel   the channel to change
 * @param pitch     the pitch of the note
 * @param velocity  the velocity of the note
 * @param tick      the time this event occurs
 */
protected static MidiEvent createNoteOffEvent(int channel,
                                              int pitch, int velocity,
                                              long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0x80 + channel, pitch, velocity);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

/**
 * Create a Program Change Event
 * @param channel  the channel to change
 * @param value    the new value to use
 * @param tick     the time this event occurs
 */
protected static MidiEvent createProgramChangeEvent(int channel,
                                                    int value,
                                                    long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0xC0 + channel, value, 0);
    MidiEvent evt = new MidiEvent(msg, tick);

    return evt;
  }

/**
 * Create a Control Change event
 * @param channel     the channel to use
 * @param controlNum  the control change number to use
 * @param value       the value of the control change
 * @param tick        the time this event occurs
 */
protected static MidiEvent createCChangeEvent(int channel,
                                              int controlNum,
                                              int value,
                                              long tick)
    throws InvalidMidiDataException
  {

    ShortMessage msg = new ShortMessage();
    msg.setMessage(0xB0 + channel, controlNum, value);
    MidiEvent evt = new MidiEvent(msg, tick);
    return evt;
  }

/**
 * Takes the specified Sequence, finds the longest track and
 * adds an event to end the Sequence.
 * @param seq       the Sequence to end
 */
protected static void endSequence(Sequence seq)
    throws InvalidMidiDataException
  {
    Track longestTrack = null;
    long longestTime = 0;
    List<Track> tracks = Arrays.asList(seq.getTracks());
    ListIterator<Track> i = tracks.listIterator();
    while( i.hasNext() )
      {
        Track track = i.next();
        if( track.ticks() > longestTime )
          {
            longestTime = track.ticks();
            longestTrack = track;
          }
      }

    if( longestTime > 0 && longestTrack != null )
      {
        MetaMessage msg = new MetaMessage();
        byte[] data = new byte[0];
        msg.setMessage(StopType, data, 0);
        MidiEvent evt = new MidiEvent(msg, longestTime);
        longestTrack.add(evt);
      }

  }

public Sequencer getSequencer()
  {
    return sequencer;
  }

private void setSequencer()
  {
    Trace.log(2, "Getting MIDI sequencer");
    sequencer = null;
    try
      {
        /* pass getSequencer false so that it doesn't autoconnect to
         * default receiver, we hook it into our own MidiManager that
         * will update the transmitter when the receiver changes
         */
        sequencer = MidiSystem.getSequencer(false);

        /* add the transmitter to the midi manager so that it is always
         * hooked to the correct receiver (allows the user to change
         * the receiver by changing the MIDI out device)
         */
        registerTransmitter(sequencer.getTransmitter());
      }
    catch( MidiUnavailableException e )
      {
        Trace.log(2, "Couldn't get sequencer:" + e.getMessage());
      }
  }

public int getMasterVolume()
  {
    return volumeControl.getMasterVolume();
  }

public void setMasterVolume(int volume)
  {
    volumeControl.setMasterVolume(volume);
  }

public void setChannelVolume(int channel, int volume)
  {
    volumeControl.setChannelVolume(channel, volume);
  }

public void registerReceiver(Receiver r)
  {
    midiManager.registerReceiver(r);
  }

public void unregisterReceiver(Receiver r)
  {
    midiManager.unregisterReceiver(r);
  }

void registerTransmitter(Transmitter t)
  {
    t.setReceiver(volumeControl);
  }

Vector<MidiNoteListener> noteListeners = new Vector<MidiNoteListener>();

public void registerNoteListener(MidiNoteListener m)
  {
    noteListeners.add(m);
  }

public void unregisterNoteListener(MidiNoteListener m)
  {
    while( noteListeners.contains(m) )
      {
        noteListeners.remove(m);
      }
  }

private class Mixer implements Receiver, Transmitter
{

Receiver receiver;

private int numChannels;

private double[] channelVolume;

private double volume = 1;

public Mixer(int numChannels)
  {
    this.numChannels = numChannels;
    channelVolume = new double[numChannels];
    for( int i = 0; i < numChannels; i++ )
      {
        channelVolume[i] = 1;
      }
  }

public void setMasterVolume(int volume)
  {
    this.volume = (double) volume / MAX_VOLUME;
  }

public int getMasterVolume()
  {
    return (int) (this.volume * MAX_VOLUME);
  }

public void setChannelVolume(int channel, int volume)
  {
    if( channel < numChannels )
      {
        channelVolume[channel] = (double) volume / MAX_VOLUME;
      }
  }

public void send(MidiMessage message, long timeStamp)
  {
    byte[] msg = message.getMessage();
    int highNibble = (msg[0] & 0xF0) >> 4;

    // process note on events only
    if( highNibble == 9 )
      {
        int channel = msg[0] & 0x0F;
        int note = msg[1];
        int velocity = (int) (msg[2] * channelVolume[channel] * volume);

        //DEBUG: System.out.println(note + " " + channel + " " + msg[2] + " -> " + velocity);

        ShortMessage newMsg = new ShortMessage();
        try
          {
            newMsg.setMessage(ShortMessage.NOTE_ON, channel, note, velocity);
            receiver.send(newMsg, timeStamp);
          }
        catch( Exception e )
          {
          }

        /* TODO: should this be threaded to prevent lag?
         * as long as the noteOn function calls don't take too long this
         * should be ok...
         */
        for( MidiNoteListener n: noteListeners )
          {
            n.noteOn(note, channel);
          }
      }
    else
      {
        try
          {
            receiver.send(message, timeStamp);
          }
        catch( Exception e )
          {
          }
      }
  }

public void close()
  {
  }

public void setReceiver(Receiver receiver)
  {
    this.receiver = receiver;
  }

public Receiver getReceiver()
  {
    return receiver;
  }

}

/*

protected void printSeqInfo(Sequence seq) {
//System.out.println("Score Title: " + scoreTitle);
//System.out.println("Score TempoEvent: " + tempo + " BPM");
//System.out.print("Sequence Division Type = ");
float type = seq.getDivisionType();
if (Sequence.PPQ == type)
System.out.println("PPQ");
else if (Sequence.SMPTE_24 == type)
System.out.println("SMPTE 24 (24 fps)");
else if (Sequence.SMPTE_25 == type)
System.out.println("SMPTE 25 (25 fps)");
else if (Sequence.SMPTE_30 == type)
System.out.println("SMPTE 30 (30 fps)");
else if (Sequence.SMPTE_30DROP == type)
System.out.println("SMPTE 30 Drop (29.97 fps)");
else
System.out.println("Unknown");

System.out.println("Sequence Resolution = " +
seq.getResolution());
System.out.println("Sequence TickLength = " +
seq.getTickLength());
System.out.println("Sequence Microsecond Length = " +
seq.getMicrosecondLength());
System.out.println("Sequencer TempoEvent (BPM) = " +
sequencer.getTempoInBPM());
System.out.println("Sequencer TempoEvent (MPQ) = " +
sequencer.getTempoInMPQ());
System.out.println("Sequencer TempoFactor = " +
sequencer.getTempoFactor());
}
 */
}// MidiSynth
