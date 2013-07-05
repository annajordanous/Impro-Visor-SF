/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

package imp.audio;

//Imports
import imp.gui.Notate;
import imp.data.Score;
import imp.Constants;

/**
 * This class calculates the uniform delays stemming from SuperCollider 
 * communication with improVisor.
 * 
 * For details, see the included graph in the audio package. If the score tempo 
 * reaches a certain range, the uniform latency changes. Found by trial and 
 * error. May need to be in tandem with user latency definition in the audio
 * preferences.
 * 
 * @author Anna Turner
 * @since June 24 2013
 */
public class SCDelayOffsetter {
    private double tempo;
    private Score score;
    private AudioSettings audioSettings;
    private int resolution;
    private double offsetSlots;//See determineOmniOffsetSlots for explanation
    
    /**
     * Constructor. Assigns variable values.
     * @param notate same notate used everywhere
     */
    public SCDelayOffsetter(Notate notate){
    score = notate.getScore();
    tempo = score.getTempo();
    audioSettings = notate.getAudioSettings();
    resolution = audioSettings.getRESOLUTION();        
    }
    
    /**
     * Calculates certain delay for each note when capturing  audio through 
     * SuperCollider. Due to snapping from note values, depending on the 
     * tempo, we can pull each note back (so it is earlier on the score) 
     * by a certain value.
     */
    //@TODO *could* consolidate 'else = 0's.
    //@TODO finetune range boundaries.
    public double determineOffsetSlots(){
        //First, convert to ms/slot    
        double slotsPerBeat = (480/(score.getMetre()[1]));//480 slots in whole note - see Constants.java
        double msPerSlot = 1/(slotsPerBeat*tempo/60/1000);//converting tempo to ms
        
        if (tempo < 50) {
            if (resolution>=16){ 
                offsetSlots = Constants.SIXTEENTH; 
            } else { 
                offsetSlots = 0; 
            }
        } else if (tempo < 125) {
            if (resolution >= 8){
                offsetSlots = Constants.EIGHTH; 
            } else { 
                offsetSlots = 0; 
            }
        } else if (tempo < 145) {
            if (resolution >= 16) {
                offsetSlots = 3*(Constants.SIXTEENTH);
            } else if (resolution < 16 && resolution >=8) {
                offsetSlots = Constants.EIGHTH;
            } else { 
                offsetSlots = 0; 
            }   
        } else {
            if (resolution >= 4) {
                offsetSlots = Constants.QUARTER; 
            } else { 
                offsetSlots = 0; 
            }
        }
        
        offsetSlots = offsetSlots*msPerSlot;
        return offsetSlots; //in ms.
    }   
}