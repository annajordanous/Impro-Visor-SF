/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
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



/**
 * Written by Martin Hunt
 *
 * Encapsulates all events of the playback slider and corresponding time labels
 * To use:
 *    Pass in correct components into the constructor
 *    Call setPlayStatus when the playStatus changes
 *    Call setTotalTime when the score or total time changes
 */

package imp.util;

import imp.data.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

/**
 *
 * @author Martin
 */
public class PlaybackSliderManager implements MidiPlayListener, ChangeListener, ActionListener {
    MidiSynth midiSynth;
    JLabel currentTimeLabel;
    JLabel totalTimeLabel;
    JSlider slider;
    javax.swing.Timer timer = null;
    ActionListener secondaryListener = null;
    boolean ignoreEvent = false;

    //long totalTimeMicroSeconds = 0;

    static long million = 1000000;
    static double dmillion = million;

    static int timerInterval = 50; // interval delay for timer, in milliseconds

    //loat rememberedTempo;

    MidiPlayListener.Status status = MidiPlayListener.Status.STOPPED;

    /** Creates a new instance of PlaybackSliderManager */

    public PlaybackSliderManager(MidiSynth midiSynth, JLabel currentTime, JLabel totalTime, JSlider slider) {
        this(midiSynth, currentTime, totalTime, slider, null);
    }

    public PlaybackSliderManager(MidiSynth midiSynth, JLabel currentTime, JLabel totalTime, JSlider slider,
                                 ActionListener playbackRefreshTimerListener) {
        this.midiSynth = midiSynth;
        this.currentTimeLabel = currentTime;
        this.totalTimeLabel = totalTime;
        this.slider = slider;

        this.secondaryListener = playbackRefreshTimerListener;

        slider.addChangeListener(this);
        timer = new javax.swing.Timer(timerInterval, this);

        //rememberedTempo = midiSynth.getTempo();
    }

     /**
     * Called on playback position slider change,
     * since this class implements ChangeListener
     */

    public void stateChanged(ChangeEvent evt) {
        if(ignoreEvent)
            return;
        
        //long duration = midiSynth.getTotalMicroseconds();
//        long duration = (long)(midiSynth.getTotalMicrosecondsWithCountIn() / dmillion);
        long duration = (long)(midiSynth.getTotalMicroseconds() / dmillion);

        long newValue = (long) (duration * (slider.getValue()/(double)slider.getMaximum()));

        //System.out.println("slider state changed duration " + duration + " newValue = " + newValue);

        long newValueMicroseconds = newValue*million;

        updateTimeSlider(newValueMicroseconds, false, "playback slider state changed");

        if(!slider.getValueIsAdjusting())
        {
           midiSynth.setMicrosecond(newValueMicroseconds);
        //System.out.println("slider state Changed, setMicrosecond to newValue = " + newValueMicroseconds);
        }
    }
    
    /**
     * Called on timer firing
     */

    public void actionPerformed(ActionEvent e) {
        final ActionEvent evt = e;
        
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                if(status != MidiPlayListener.Status.STOPPED) {

                    if(!slider.getValueIsAdjusting()) {

                        long microsecond = midiSynth.getMicrosecond();
                        /*
                        long countIn = midiSynth.getCountInMicroseconds();
                        microsecond -= countIn;
                        if( microsecond < countIn )
                        {
                            microsecond = countIn;
                        }
                        */

                        updateTimeSlider(microsecond, true, "timer firing, set time to " + (microsecond/million) + " seconds");
/*
                        System.out.println("slot " + midiSynth.getSlot() + ": " +
                            (midiSynth.getMicrosecond()/dmillion) + " sec, out of "
                            + (midiSynth.getTotalMicrosecondsWithCountIn()/dmillion));
*/
                        if(secondaryListener != null)
                            secondaryListener.actionPerformed(evt);
                    }
                } else {
                    timer.stop();
                }
            }
        });
    }
    
    public long getMicrosecondsFromSlider() {
        return (long) ((slider.getValue() / (double) slider.getMaximum()) * midiSynth.getTotalMicroseconds()); // totalTimeMicroSeconds * million);
//        return (long) ((slider.getValue() / (double) slider.getMaximum()) * midiSynth.getTotalMicrosecondsWithCountIn()); // totalTimeMicroSeconds * million);
    }
    
    public void setCurrentTimeSeconds(int seconds) {
        currentTimeLabel.setText(formatSecond(seconds));
        //totalTimeMicroSeconds = seconds*million;
    //System.out.println("setCurrentTimeLabel to " + seconds + " seconds");
    }
    

    public void setTotalTimeSeconds(int seconds) {
        totalTimeLabel.setText(formatSecond(seconds));
        //totalTimeMicroSeconds = seconds*million;
    //System.out.println("setTotalTimeLabel to " + seconds + " seconds");
    }


    /**
     * Update the time slider, according to specified number of microseconds
     * into the piece.
     @param microseconds
     @param updateSlider
     */

    public void updateTimeSlider(long microseconds, boolean updateSlider, String reason) {

        microseconds -= midiSynth.getCountInMicroseconds();

        if( microseconds < 0 )
        {
            microseconds = 0;
        }

        setCurrentTimeSeconds((int)(microseconds/dmillion));
        if(updateSlider && !slider.getValueIsAdjusting()) {

            ignoreEvent = true;
            slider.setValue((int) (slider.getMaximum() * microseconds / (double) midiSynth.getTotalMicroseconds()));
            ignoreEvent = false;
        }
    }
    
    public void setPlaying(MidiPlayListener.Status playing, int transposition) {
        MidiPlayListener.Status oldStatus = status;
        status = playing;
        switch(playing) {
            case PLAYING:
                setTotalTimeSeconds((int)(midiSynth.getTotalMicroseconds() / million));
                timer.start();
                break;
            case STOPPED:
                timer.stop();
                if(oldStatus != MidiPlayListener.Status.STOPPED)
                    slider.setValue(0);
                   break;
            case PAUSED:
                timer.start();
                break;
        }
    }

    public MidiPlayListener.Status getPlaying() {
        return status;
    }
    
    public static String formatSecond(int seconds) {
        int minutes = seconds / 60;
        seconds %= 60;
        return minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
    }
    
    public static String formatMicrosecond(long microseconds) {
        return formatSecond((int) (microseconds / million));
    }

}
