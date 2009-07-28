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
    JLabel currentTime;
    JLabel totalTime;
    JSlider slider;
    javax.swing.Timer timer = null;
    ActionListener secondaryListener = null;
    boolean ignoreEvent = false;
    long totalTimeMicroSeconds = 0;
    
    /**
     * Called on slider change
     */
    public void stateChanged(ChangeEvent evt) {
        if(ignoreEvent)
            return;
        
        long duration = midiSynth.getTotalMicroseconds();
        if(status == MidiPlayListener.Status.STOPPED) {
            duration = totalTimeMicroSeconds * 1000000;
        }
        
        long newValue = (long) (duration * (slider.getValue()/(double)slider.getMaximum()));
        
        updateTime(newValue, false);
        if(!slider.getValueIsAdjusting()) {
            midiSynth.setMicrosecond(newValue);
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
                        updateTime(midiSynth.getMicrosecond());
                        if(secondaryListener != null)
                            secondaryListener.actionPerformed(evt);
                    }
                } else {
                    timer.stop();
                }
            }
        });
    }
    
    /** Creates a new instance of PlaybackSliderManager */
    public PlaybackSliderManager(MidiSynth midiSynth, JLabel currentTime, JLabel totalTime, JSlider slider) {
        this(midiSynth, currentTime, totalTime, slider, null);
    }
    public PlaybackSliderManager(MidiSynth midiSynth, JLabel currentTime, JLabel totalTime, JSlider slider, ActionListener playbackRefreshTimerListener) {
        this.midiSynth = midiSynth;
        this.currentTime = currentTime;
        this.totalTime = totalTime;
        this.slider = slider;
        this.secondaryListener = playbackRefreshTimerListener;
        
        slider.addChangeListener(this);
        timer = new javax.swing.Timer(50, this);
    }
    
    public long getMicrosecondsFromSlider() {
        return (long) ((slider.getValue() / (double) slider.getMaximum()) * totalTimeMicroSeconds * 1000000);
    }
    
    public void setTotalTime(int seconds) {
        totalTime.setText(formatSecond(seconds));
        totalTimeMicroSeconds = seconds;
    }
    
    public void updateTime(long microseconds) {
        updateTime(microseconds, true);
    }
    public void updateTime(long microseconds, boolean updateSlider) {
        currentTime.setText(formatMicrosecond(microseconds));
        if(updateSlider && !slider.getValueIsAdjusting()) {
            ignoreEvent = true;
            slider.setValue((int) (slider.getMaximum() * microseconds / (double) midiSynth.getTotalMicroseconds()));
            ignoreEvent = false;
        }
    }
    
    MidiPlayListener.Status status = MidiPlayListener.Status.STOPPED;
    public void setPlaying(MidiPlayListener.Status playing, int transposition) {
        MidiPlayListener.Status oldStatus = status;
        status = playing;
        switch(playing) {
            case PLAYING:
                setTotalTime((int)(midiSynth.getTotalMicroseconds() / 1000000));
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
        return formatSecond((int) (microseconds / 1000000));
    }

}
