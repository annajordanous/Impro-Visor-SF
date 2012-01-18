/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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
 *
 * You should have received a copy of the GNU General Public License along with
 * Impro-Visor; if not, write to the Free Software Foundation, Inc., 51 Franklin
 * St, Fifth Floor, Boston, MA 02110-1301 USA
 */
/*
 * MidiManager.java
 *
 * Created on June 22, 2006, 3:40 PM
 *
 * @author Martin Hunt
 */
package imp.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import javax.sound.midi.*;

import imp.Constants;

public class MidiManager implements Constants
{

private static String midiError = null;
// vectors of all devices available
private Vector<MidiDevice.Info> seqInfo;
private Vector<MidiDevice.Info> synthInfo;
private Vector<MidiDevice.Info> midiInInfo;
private Vector<MidiDevice.Info> midiOutInfo;
// specific devices and receiver used by this program for MIDI IO
private MidiDevice out = null;
private MidiDevice in = null;   // right now, we only support listening to (1) MIDI IN device at a time
private MidiDevice.Info outInfo = null;
private MidiDevice.Info inInfo = null;
private Receiver currentReceiver = null;
public String defaultDeviceLabel = "Default Device (System)";

MidiDevice.Info[] infos;
        
/**
 * The publicReceiver provides a receiver to attach transmitters to that will
 * always forward messages to the currently selected MIDI out device. The
 * transmitters are attached in the registerTransmitter function
 */
private MidiRelay publicReceiver = new MidiRelay();
/**
 * The publicTransmitter forwards all messages coming from the selected MIDI in
 * device to all registered receivers
 */
private MidiMultiTransmit publicTransmitter = new MidiMultiTransmit();
;
    
    // flag for echoing MIDI IN to the MIDI OUT
    private boolean doEcho = false;

// ========================================
public MidiManager()
  {
    init();
  }

private void init()
  {
    findInstalledDevices();
    try
      {
        // try to open devices by default (use the first output and input device)
        MidiDevice.Info output = null;
        MidiDevice.Info input = null;
        if( midiOutInfo.size() > 0 )
          {
            output = midiOutInfo.get(midiOutInfo.size()-1);
          }
        if( midiInInfo.size() > 0 )
          {
            input = midiInInfo.get(midiInInfo.size()-1);
          }
        setDevices(output, input);
      }
    catch( Exception e )
      {
        logError(e.getMessage());
      }
    //System.out.println("*** MidiManager initialized");
  }


// attempts to list all MIDI IO devices in the system
public void findInstalledDevices()
  {

    // Obtain information about all the installed synthesizers.
    infos = MidiSystem.getMidiDeviceInfo();

    System.out.println("\nFound " + infos.length + " MIDI devices:");
    // reinit vectors of device info to store found devices
    synthInfo = new Vector<MidiDevice.Info>();
    seqInfo = new Vector<MidiDevice.Info>();
    midiInInfo = new Vector<MidiDevice.Info>();
    midiOutInfo = new Vector<MidiDevice.Info>();
    midiOutInfo.add(null);

    // Scan all found devices and check to see what type they are
    MidiDevice device;
    //for( int i = 0; i < infos.length; i++ )
    for( int i = infos.length - 1; i >= 0; i--)
      {
        try
          {
            device = MidiSystem.getMidiDevice(infos[i]);

            System.out.println(i + ": " + getDeviceName(device));
          }
        catch( MidiUnavailableException e )
          {
            continue;
          }
        if( device instanceof Synthesizer )
          {
            synthInfo.add(infos[i]);
          }
        if( device instanceof Sequencer )
          {
            seqInfo.add(infos[i]);
          }
        if( !(device instanceof Synthesizer) && !(device instanceof Sequencer) )
          {
            int numReceivers = device.getMaxReceivers();
            int numTransmitters = device.getMaxTransmitters();

            if( numReceivers > 0 || numReceivers == -1 )
              {
                midiOutInfo.add(infos[i]);
              }

            if( numTransmitters > 0 || numTransmitters == -1 )
              {
                midiInInfo.add(infos[i]);
              }
          }
      }

    Trace.log(0, "Devices found: \n Synthesizers:\n" + synthInfo + "\n\n Sequencers:\n" + seqInfo + "\n\n MIDI In:\n" + midiInInfo + "\n\n MIDI Out:\n" + midiOutInfo);
  }

// returns the last error message
public String getError()
  {
    return midiError;
  }

/**
 * Registering receivers and transmitters should probably be done by the
 * MidiSynth The MidiSynth and similar classes can use these functions to access
 * the MIDI devices at a global level, while the MidiSynth provides access to
 * the MIDI devices on the per-window level (provides a mixer for each window
 * and device sharing across windows)
 */
public void registerTransmitter(Transmitter t)
  {
    t.setReceiver(publicReceiver);
  }

public void registerReceiver(Receiver r)
  {
    publicTransmitter.addReceiver(r);
  }

public void unregisterReceiver(Receiver r)
  {
    publicTransmitter.removeReceiver(r);
  }

// getters for device info populated by findInstalledDevices()
public Vector<MidiDevice.Info> getSequencerInfo()
  {
    return seqInfo;
  }

public Vector<MidiDevice.Info> getSeqInfo()
  {
    return synthInfo;
  }

public Vector<MidiDevice.Info> getMidiInInfo()
  {
    return midiInInfo;
  }

public Vector<MidiDevice.Info> getMidiOutInfo()
  {
    return midiOutInfo;
  }

// getters for current devices and their corresponding info
public MidiDevice getOutDevice()
  {
    return out;
  }

public MidiDevice getInDevice()
  {
    return in;
  }

public Object getOutDeviceInfo()
  {
    if( outInfo == null )
      {
        return defaultDeviceLabel;
      }
    return outInfo;
  }

public MidiDevice.Info getInDeviceInfo()
  {
    return inInfo;
  }

public Receiver getReceiver()
  {
    return publicReceiver;
  }

// getter and setter for echoing midi input messages to the midi output device
public boolean getEcho()
  {
    return doEcho;
  }

public boolean setEcho(boolean state)
  {
    // only do something if the state has changed
    if( doEcho != state )
      {
        doEcho = state;

        Trace.log(2, "Echo " + (doEcho ? "start" : "stop") + " requested");
        if( doEcho )
          {
            publicTransmitter.addReceiver(publicReceiver);
          }
        else
          {
            publicTransmitter.removeReceiver(publicReceiver);
          }
      }
    return true;
  }


// closes all devices that we opened, called before changing devices, for example: see setDevices
public void closeDevices()
  {
    closeInDevice();
    closeOutDevice();
  }

private void closeInDevice()
  {
    if( in != null )
      {
        in.close();
      }
  }

private void closeOutDevice()
  {
    sendAllSoundsOffMsg();

    if( currentReceiver != null )
      {
        System.out.println("closing currentReceiver " + currentReceiver);
        currentReceiver.close();
      }
    
    if( out != null )
      {
        System.out.println("closing out " + currentReceiver);
        out.close();
      }
  }

private void logError(String error)
  {
    error = (new SimpleDateFormat("h:mm:ssa")).format(new Date()) + ": " + error;

    if( midiError == null )
      {
        midiError = error;
      }
    else
      {
        midiError = error + "\n" + midiError;
      }

    ErrorLog.log(ErrorLog.COMMENT, error, false);
  }

public void clearErrorMsgLog()
  {
    midiError = null;
  }

public void sendAllSoundsOffMsg()
  {
    ShortMessage msg = new ShortMessage();
    try
      {
        msg.setMessage(120);
        currentReceiver.send(msg, -1);
      }
    catch( InvalidMidiDataException ex )
      {
      }
  }

public void sendSysExMasterVolumeMsg(int value)
  {
    /*
     * 0xF0 SysEx 0x7F Realtime 0x7F The SysEx channel. Could be from 0x00 to
     * 0x7F. Here we set it to "disregard channel". 0x04 Sub-ID -- Device
     * Control 0x01 Sub-ID2 -- Master Volume 0xLL Bits 0 to 6 of a 14-bit volume
     * 0xMM Bits 7 to 13 of a 14-bit volume 0xF7 End of SysEx
     */

    if( value < 0 )
      {
        value = 0;
      }
    if( value > MAX_VOLUME )
      {
        value = MAX_VOLUME;
      }

    double gain = (double) value / MAX_VOLUME;

    try
      {
        int volume = (int) (gain * 16383);

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        b.write(SysexMessage.SYSTEM_EXCLUSIVE);
        b.write(new byte[]
                  {
                    0x7F, 0x7F, 0x04, 0x01
                  });

        /*
         * apparently, a MIDI byte only has 7 bits... so we code things assuming
         * that and it seems to work, though I don't know exactly how... the
         * last bit in the byte array must just be disgarded
         */
        b.write(volume % 128);  // first 7 bits of volume
        b.write(volume >> 7);   // the rest of the bits of volume
        b.write(SysexMessage.SPECIAL_SYSTEM_EXCLUSIVE);

//          DEBUG CODE:
//            byte[] bytes = new byte[2]; bytes[0] = (byte) (volume >> 7); bytes[1] = (byte) (volume % 128);
//            BigInteger bi = new BigInteger(bytes);
//            System.out.println(volume + "  " + bi.toString(2));

        SysexMessage myMsg = new SysexMessage();
        myMsg.setMessage(b.toByteArray(), 8);
        long timeStamp = -1;
        currentReceiver.send(myMsg, timeStamp);
      }
    catch( Exception e )
      {
        logError("setMasterVolume Error: " + e.getMessage());
      }
  }

/**
 * sets the input device and reestablishes echo if doEcho is true
 */
private String inDeviceError;

public void setInDevice(MidiDevice.Info inInfo)
  {
    closeInDevice();
    this.inInfo = inInfo;

    inDeviceError = "";

    // get control of the input device
    if( inInfo != null )
      {
        try
          {
            this.in = MidiSystem.getMidiDevice(inInfo);
            in.open();

            // setup the public transmitter
            in.getTransmitter().setReceiver(publicTransmitter);

            Trace.log(2, "MIDI in device set: " + inInfo);
          }
        catch( Exception e )
          {
            in = null;
            logError(inInfo + " - " + e.getMessage());
            inDeviceError = e.getMessage();
          }
      }
    else
      {
        in = null;
      }
  }

/**
 * sets the output device and prepares a receiver for the volumeControl
 * transmitted messages
 */
private String outDeviceError;

public void setOutDevice(Object outInfoObject)
  {
    MidiDevice.Info outInfo;

    if( outInfoObject == defaultDeviceLabel )
      {
        outInfo = null;
      }
    else
      {
        outInfo = (MidiDevice.Info) outInfoObject;
      }

    closeOutDevice();
    this.outInfo = outInfo;

    outDeviceError = "";
    try
      {
        if( outInfo == null )
          {
            // use the default receiver
            setCurrentReceiver(MidiSystem.getReceiver());
          }
        else
          {
            // get control of the output device
            this.out = MidiSystem.getMidiDevice(outInfo);
            out.open();

            // open a receiver on the output device for this program
            try
              {
                setCurrentReceiver(out.getReceiver());
              }
            catch( Exception e )
              {
                setCurrentReceiver(null);
                logError(outInfo + " - " + e.getMessage());
                outDeviceError = e.getMessage();
              }

            Trace.log(2, "MIDI out device set: " + outInfo);
          }

        // setup the publicReceiver
        publicReceiver.setReceiver(currentReceiver);
      }
    catch( Exception e )
      {
        out = null;
        logError(outInfo + " - " + e.getMessage());
        outDeviceError += (outDeviceError == "" ? "" : "\n") + e.getMessage();
      }
  }

// open requested MIDI devices and prepare a receiver on the output device
public void setDevices(MidiDevice.Info outInfo, MidiDevice.Info inInfo)
  {
    setInDevice(inInfo);
    setOutDevice(outInfo);
  }

public String getOutDeviceError()
  {
    return outDeviceError;
  }

public String getInDeviceError()
  {
    return inDeviceError;
  }

private void setCurrentReceiver(Receiver receiver)
  {
    System.out.println("\nSetting currentReceiver from " + currentReceiver + " to " + receiver);
    currentReceiver = receiver;
  }

public String getDeviceName(MidiDevice device)
  {
    return device.getDeviceInfo().toString();
  }
/**
 * plays a middle C on the receiver
 */
public boolean test()
  {
    try
      {
        ShortMessage myMsg = new ShortMessage();
        // Start playing the note Middle C (60), 
        // moderately loud (velocity = 93).
        myMsg.setMessage(ShortMessage.NOTE_ON, 0, 60, 93);
        long timeStamp = -1;
        publicReceiver.send(myMsg, timeStamp);
      }
    catch( Exception e )
      {
        logError("Test failed: " + e.getMessage());
        return false;
      }
    return true;
  }

private class MidiRelay implements Receiver, Transmitter
{

Receiver receiver;

public void send(MidiMessage message, long timeStamp)
  {
    try
      {
        receiver.send(message, timeStamp);
      }
    catch( Exception e )
      {
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

private class MidiMultiTransmit implements Receiver
{

Vector<Receiver> receiver = new Vector<Receiver>();
int numReceivers = 0;

public void send(MidiMessage message, long timeStamp)
  {
    try
      {
        for( Receiver r : receiver )
          {
            try
              {
                r.send(message, timeStamp);
              }
            catch( IllegalStateException e )
              {
                if( r == null )
                  {
                    receiver.remove(null);
                  }
              }
          }
      }
    catch( java.util.ConcurrentModificationException e )
      {
        // RK 6/11/2010 Getting exceptions here at the end.
        // This silently catches them.
        return;
      }
  }

public void close()
  {
  }

public void addReceiver(Receiver receiver)
  {
    if( !this.receiver.contains(receiver) )
      {
        this.receiver.add(receiver);
      }
  }

public void removeReceiver(Receiver receiver)
  {
    while( this.receiver.contains(receiver) )
      {
        this.receiver.remove(receiver);
      }
  }

}
}
