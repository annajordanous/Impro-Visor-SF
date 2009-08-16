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

package imp.com;

import imp.Constants;
import imp.data.*;
import imp.util.MidiPlayListener;
import imp.util.Trace;
import imp.ImproVisor;

/**
 * A Command that sequences and plays a Score, either straight or swung.
 * @see         Command
 * @see         CommandManager
 * @see         Score
 * @see         MidiSynth
 * @author      Stephen Jones
 */
public class PlayScoreCommand implements Command, Constants {

    public static final boolean USEDRUMS = true;
    public static final boolean NODRUMS = false;

    private boolean useDrums = USEDRUMS;

    public void stopPlaying() {
        ms.stop();
    }
    
    /**
     * the MidiSynth object to play the Score on
     */
    private MidiSynth ms;
    
    /**
     * the Score to play
     */
    private Score score;

    /**
     * true if the playback should be swung
     */
    private boolean swing;
    
    
   
    
    private long startTime;

    private int endLimitIndex;

    /**
     * false since this Command cannot be undone
     */
    private boolean undoable = false;
    
    private MidiPlayListener listener;
    
    private int loopCount = 0;
    
    private int transposition = 0;

   /**
    * The duration of the accompanying chord for single-note entry
    */

   private int oneNoteChordPlayValue = BEAT;


    public PlayScoreCommand(Score score, long startTime, boolean swing, MidiSynth ms, MidiPlayListener listener, int loopCount, int transposition) {
        this(score, startTime, swing, ms, listener, loopCount, transposition, USEDRUMS);
    }
    
    public PlayScoreCommand(Score score, long startTime, boolean swing, MidiSynth ms, MidiPlayListener listener, int loopCount, int transposition, boolean useDrums) {
        this(score, startTime, swing, ms, listener, loopCount, transposition, useDrums, ENDSCORE);
    }

    public PlayScoreCommand(Score score, long startTime, boolean swing, MidiSynth ms, MidiPlayListener listener, int loopCount, int transposition, boolean useDrums, int endLimitIndex) {
        this.score = score;
        this.swing = swing;
        this.ms = ms;
        this.startTime = startTime;
        this.listener = listener;
        this.loopCount = loopCount;
        this.transposition = transposition;
        this.useDrums = useDrums;
        this.endLimitIndex = endLimitIndex;
    }

    /**
     * Creates a new Command that can play a Score, either straight or swung.
     * @param score     the Score to play
     * @param swing     boolean telling if the playback should be swung
     */
    public PlayScoreCommand(Score score, long startTime, boolean swing, int loopCount, int transposition) {
        this(score, startTime, swing, ImproVisor.getLastMidiSynth(), ImproVisor.getCurrentWindow(), loopCount, transposition, false, 4*BEAT);
    }

    public PlayScoreCommand(Score score, long startTime, boolean swing, int loopCount, int transposition, boolean useDrums, int endLimitIndex) {
        this(score, startTime, swing, ImproVisor.getLastMidiSynth(), ImproVisor.getCurrentWindow(), loopCount, transposition, useDrums, endLimitIndex);
    }
    public PlayScoreCommand(Score score, long startTime, boolean swing, MidiSynth ms, int loopCount, int transposition) {
        this(score, startTime, swing, ms, ImproVisor.getCurrentWindow(), loopCount, transposition);
    }
    
   public PlayScoreCommand(Score score, long startTime, boolean swing, MidiSynth ms, int transposition) {
        this(score, startTime, swing, ms, 0, transposition);
    }
    
    /**
     * Plays the Score
     */
    public void execute() {
        Trace.log(2, "executing PlayScoreCommand, startTime = "
            + startTime + ", endLimitIndex = " + endLimitIndex);
        score = score.copy();

        // Use plain style for note entry
        
        if( !useDrums )
        {
        ChordPart chords = score.getChordProg();

        // If there is no chord on the slot starting the selection,
        // we try to find the previous chord and use it.

        int startSlot = (int)(startTime%chords.size());

        if( chords.getChord(startSlot) == null )
          {
            for( int i = startSlot - 1; i >= 0; i-- )
              {
                Chord previousChord = chords.getChord(i);

                if( previousChord != null )
                  {
                    Chord copy = previousChord.copy();
                    copy.setRhythmValue(oneNoteChordPlayValue);
                    chords.setChord(startSlot, copy);
                    break;
                  }
              }
          }

        SectionInfo info = new SectionInfo(chords);
        info.setStyle(swing ? "no-style-but-swing" : "no-style");
        chords.setSectionInfo(info);
 
       }
        
        if(swing) {
            score.makeSwing();
        }

        ms.setPlayListener(listener);

        // Note that the value of loopCount is 1 less than the number of loops
        // desired. That is, a value of 0 loops once, 1 loops twice, etc.

        int offset = score.getCountInOffset();

        startTime = startTime == 0 ? 0 : startTime + offset;

        endLimitIndex = endLimitIndex == ENDSCORE ? ENDSCORE : endLimitIndex + offset;

        //System.out.println("from " + startTime + " to " + endLimitIndex);

        try { 
            ms.play(score, startTime, loopCount, transposition, useDrums, endLimitIndex);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Undo unsupported for PlayScoreCommand.
     */
    public void undo() {
        throw new 
            UnsupportedOperationException("Undo unsupported for PlayScore.");
    }

    /**
     * Redo unsupported for PlayScoreCommand.
     */
    public void redo() {
        throw new
            UnsupportedOperationException("Redo unsupported for PlayScore.");
    }

    public boolean isUndoable() {
        return undoable;
    }
}
