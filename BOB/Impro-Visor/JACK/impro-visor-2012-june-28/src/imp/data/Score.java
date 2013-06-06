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

package imp.data;

import imp.Constants;
import imp.ImproVisor;
import imp.roadmap.RoadMapFrame;
import imp.util.Preferences;
import imp.util.Trace;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.ListIterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import polya.Polylist;

/**
 * The Score class is representative of a musical score, containing several
 * parallel Parts, including melodies and chord progressions.
 * A Score contains several Parts stored in a PartList.
 * It contains information about the total Score, such as volume, tempo,
 * and title.  Parts should be added with the addMelodyPart method.
 * 
 * @see         Part
 * @author      Stephen Jones, Robert Keller
 *              (rewritten from code written by Andrew Sorensen and Andrew Brown)
 */

@SuppressWarnings("serial")
public class Score implements Constants, Serializable {

/**
 * Counter used for generating titles CP_2, CP_3, ...
 */

static int chordPartNumber = 1;

static final String chordPartBase = "CP_";

/**
 * Default bars per line layout. Note that, in general, the layout specification
 * is a list of bars per line, the last of which is used if there are more lines
 * than list entries.
 */
public static String DEFAULT_BARS_PER_LINE = "4";

/**
 * The default title
 */
public static String DEFAULT_TITLE = "Untitled Score";
public static final String TITLE_BASE = "Score #";

/**
 * The default composer
 */
public static String DEFAULT_COMPOSER = "";

/**
 * The default tempo
 */
public static final double DEFAULT_TEMPO = Double.parseDouble(Preferences.getPreference(Preferences.DEFAULT_TEMPO));

/**
 * The default volume
 */
public static final int DEFAULT_VOLUME = MAX_VOLUME;

/**
 * The default key signature
 */
public static final int DEFAULT_KEYSIG = 0;

/**
 * Name of the default chord progression
 */
public static final String DEFAULT_PROGRESSION = "Untitled Part";

/**
 * The title of the Score
 */
private String title = DEFAULT_TITLE;
private static int titleCounter = 0;

/**
 * The composer of the Score
 */
private String composer = "";

/**
 * The name of the show, if any
 */
private String showTitle = "";

/**
 * The year, if specified
 */
private String year = "";

/**
 * Comments on this score
 */
private String comments = "";

/**
 * The length of the Score
 */
private int length;

/**
 * The metre of the score
 */
private int[] metre = new int[2];

/**
 * The key signature of the score
 */
private int keySig;

/**
 * The breakpoint pitch indicating where the treble stave should start drawing
 * and the bass stave should end drawing, or vice versa.
 */
private int breakpoint = FS3;

/**
 * The Parts in the Score
 */
private PartList partList;

/*
 * Mapping from names to ChordParts
 */
private LinkedHashMap<String, ChordPart> chordPartList = new LinkedHashMap<String, ChordPart>();

/**
 * The count-in Progression for this Score, if any.
 */
private ChordPart countInProg = null;

/**
 * The tempo of the Score
 */
private double tempo;

/**
 * The playback transposition of the Score
 */
private int transposition = 0;
private int chordFontSize = 16; // Default
private String voicingType = "";

/**
 * The layout, if any. This can be null if no layout specified.
 */
private Polylist layout = Polylist.nil;

/**
 * Layout for generated roadmap. Currently it is only a single number, bars per
 * line.
 */
private int roadmapLayout = 8;

/**
 * Creates an empty Score with generated title, tempo, and volume.
 */
public Score()
  {
    this(generateTitle(), DEFAULT_TEMPO, DEFAULT_VOLUME);

  }


/**
 * Create an empty Score with the specified title, tempo, and volume.
 *
 * @param title a String containing the title of the Score
 * @param tempo a double contianing the tempo of the Score
 * @param volume a number indicating the volume of the Score
 */

public Score(String title, double tempo, int volume)
  {
    this.title = title;
    this.tempo = tempo;
    this.masterVolume = volume;
    this.length = 0;

    this.composer = DEFAULT_COMPOSER;
    this.metre[0] = DEFAULT_METRE[0];
    this.metre[1] = DEFAULT_METRE[1];
    this.keySig = DEFAULT_KEYSIG;

    this.partList = new PartList(1);
    addDefaultChordPart(new ChordPart());
    setChordFontSize(Integer.valueOf(Preferences.getPreference(Preferences.DEFAULT_CHORD_FONT_SIZE)).intValue());
  }


 /**
 * Currently used only in ImproVisor and PatternDisplay, once each.
 *
 * @param length
 */

public Score(int length)
  {
    this();
    this.length = length;
    addPart();
    addDefaultChordPart(new ChordPart(length));
  }


/**
 * Currently used in ExtractionEditor and RoadMapFrame
 *
 * @param chordPart
 */

public Score(ChordPart chordPart)
  {
    this();
    addPart();
    //setLength(chordPart.size());
    addDefaultChordPart(chordPart);
  }


    public void setCountIn(ChordPart countInProg)
    {
        this.countInProg = countInProg;
    }

    public void noCountIn()
    {
        setCountIn(null);
    }

    public int getTransposition()
    {
      return transposition;
    }
    
    public void setTransposition(int transposition)
    {
      this.transposition = transposition;
    }
    
    public int getChordFontSize()
    {
      return chordFontSize;
    }

    public void setChordFontSize(int fontSize)
    {
      this.chordFontSize = fontSize;
    }

    public int getDefaultBarsPerChorus()
    {
      return getDefaultChordPart().getBars();
    }
    
   public int getActiveBarsInChorus()
    {
      int activeBars = getDefaultChordPart().getActiveBars();
      ListIterator<MelodyPartAccompanied> i = partList.listIterator();
	
        while(i.hasNext())
        {
          int barsInChorus = i.next().getActiveBars();
          if( barsInChorus > activeBars )
            {
            activeBars = barsInChorus;
            }
        }
      //System.out.println("active bars = " + activeBars);

      return activeBars;  // TEMP!
    }
    
    private int chordVolume = 60;
    private int bassVolume = 60;
    private int drumVolume = 60;
    private int melodyVolume = MAX_VOLUME;
    private int masterVolume = MAX_VOLUME;
    private boolean chordMuted = false;
    private boolean bassMuted = false;
    private boolean drumMuted = false;
    private boolean melodyMuted = false;
    private boolean masterVolumeMuted = false;
    
    private int bassInstrument = 34; // DEFAULT_BASS_INSTRUMENT;

    public void setBassInstrument(int instrument) {
       bassInstrument = instrument;
       Style style = getDefaultChordPart().getStyle();
       if( style != null )
        {
          style.setBassInstrument(instrument);
        }
    }

   public int getBassInstrument() {
        return bassInstrument;
    }

    public void setChordInstrument(int instrument)
    {
    // System.out.println("score setChordInstrument to " + instrument);

        getDefaultChordPart().setChordInstrument(instrument);
        Style style = getDefaultChordPart().getStyle();
        if( style != null )
          {
          style.setChordInstrument(instrument, "Score");
          }
    }

   public int getChordInstrument() {
        return getDefaultChordPart().getInstrument();
    }

    public void setChordVolume(int vol) {
        chordVolume = boundVolume(vol);
    }

    public int getChordVolume() {
        return chordVolume;
    }
    
    public void setChordMuted(boolean mute) {
        chordMuted = mute;
    }
    
    public boolean getChordMuted() {
        return chordMuted;
    }

    public void setBassVolume(int vol) {
        bassVolume = boundVolume(vol);
    }

    public int getBassVolume() {
        return bassVolume;
    }
    
    public void setBassMuted(boolean mute) {
        bassMuted = mute;
    }
    
    public boolean getBassMuted() {
        return bassMuted;
    }
    
    public void setDrumVolume(int vol) {
        drumVolume = boundVolume(vol);
    }
    
    public int getDrumVolume() {
        return drumVolume;
    }
    
    public void setDrumMuted(boolean mute) {
        drumMuted = mute;
    }
    
    public boolean getDrumMuted() {
        return drumMuted;
    }
    
    public void setMelodyVolume(int vol) {
        melodyVolume = boundVolume(vol);
    }
    
    public int getMelodyVolume() {
        return melodyVolume;
    }
    
    public void setMelodyMuted(boolean mute) {
        melodyMuted = mute;
    }
    
    public boolean getMelodyMuted() {
        return melodyMuted;
    }
    
    public void setMasterVolume(int vol) {
        masterVolume = boundVolume(vol);
    }
    
    public int getMasterVolume() {
        return masterVolume;
    }
    
    public void setMasterVolumeMuted(boolean mute) {
        masterVolumeMuted = mute;
    }
    
    public boolean getMasterVolumeMuted() {
        return masterVolumeMuted;
    }
    

    public int boundVolume(int vol) {
        if(vol > MAX_VOLUME)
            return MAX_VOLUME;
        if(vol < 0)
            return 0;
        return vol;
    }
    
    /**
     * Adds an empty Part to the Score.
     */
    public void addPart() {
        addParts(1);
    }

    /**
     * Adds the specified number of empty Parts to the Score.
     * @param parts     the number of Parts to add
     */
    public void addParts(int parts) {
        Trace.log(2, "adding " + parts + " new parts to score " + title);
        for(int i = 0; i < parts; i++) {
            MelodyPartAccompanied mp = new MelodyPartAccompanied(0, getDefaultChordPart());
            if(partList.size() > 0)
                {
                mp.setInstrument(partList.get(0).getInstrument());
                }
            partList.add(mp);
        }
    }
    
    /**
     * Adds the specified Part to the Score.
     * @param part      Part to add
     */
    public void addMelodyPart(MelodyPartAccompanied part) {
        //System.out.println("adding melody part " + part.getTitle() + " to score " + title);
        partList.add(part);
    }
    
    /**
     * Deletes the part at the specified index
     * @param index     the index of the Part to delete
     */
    public void delPart(int index) {
        if (index >= 0 && index < partList.size())
            partList.remove(index);
    }
    
    /*
     * Adds a new 
     */

    /**
     * Sets the metre of the Score
     * metre is now represented in most places as a 2-elt array, where
     * the first element is the top of the time signature, and the second
     * element is the bottom.
     */
    public void setMetre(int top, int bottom) {
        getDefaultChordPart().setMetre(top, bottom);
        getDefaultChordPart().setChordMetre(top, bottom);
        getDefaultChordPart().setMetre(top, bottom);
        getDefaultChordPart().setChordMetre(top, bottom);
        ListIterator<MelodyPartAccompanied> i = partList.listIterator();
	
        while(i.hasNext())
            {
            i.next().setMetre(top, bottom);
            }
    }
    
    public void setMetre(int metre[])
      {
        setMetre(metre[0],metre[1]);
      }
    
    
    /**
     * Returns the Score's metre
     * @return int              the metre of the Score
     */
    public int[] getMetre() {
        return getDefaultChordPart().getChordMetre();
    }
    
    /**
     * Sets the key signature of the Score
     * @param keySig            the key signature to set the Score to
     */
    public void setKeySignature(int keySig) {
        Trace.log(2, "setting key signature of score to " + keySig);
        this.keySig = keySig;
        getDefaultChordPart().setKeySignature(keySig);
        getDefaultChordPart().setKeySignature(keySig);
        ListIterator<MelodyPartAccompanied> i = partList.listIterator();
        while(i.hasNext())
            {
            i.next().setKeySignature(keySig);
            }
    }
    
    /**
     * Returns the Score's key signature
     * @return int              the key signature of the Score
     */
    public int getKeySignature() {
        return keySig;
    }
  
    
    public int getLength()
      {
      return getTotalLength();
      }


    public int getTotalLength()
      {
        int sum = 0;
        Iterator<MelodyPartAccompanied> i = partList.listIterator();
        while( i.hasNext() )
            {
            sum += i.next().size();
            }
        return sum;
      }

// FIX: layoutList stuff needs to move into individual melodyParts and Staves
    public Polylist getLayoutList()
      {
      return layout;
      }

    public void setLayoutList(Polylist layout)
      {
      this.layout = layout;
      }
    
    public void setDefaultLayout()
      {
        this.layout = Polylist.list(DEFAULT_BARS_PER_LINE);
      }
    
    public int getRoadmapLayout()
      {
        return roadmapLayout;
      }
    
    public void setRoadmapLayout(int barsPerLine)
      {
        roadmapLayout = barsPerLine;
      }

    /**
     * Sets the breakpoint of the Score
     * @param breakpoint        the pitch break in between treble and bass staves
     */
    public void setBreakpoint(int breakpoint) {
        if (breakpoint < 0 || breakpoint > 127) {
            this.breakpoint = FS3;
        }
        else
            this.breakpoint = breakpoint;
    }
    
    /**
     * Returns the breakpoint of the score
     * @return int              the pitch break in between treble and bass staves
     */
    public int getBreakpoint() {
        return breakpoint;
    }
    
    /**
     * Adds the specified chord progression to the Score.
     * @param chordProg         ChordPart to set as chordProg
     * 
     */
    public void setChordPart(ChordPart chordPart) {
        setDefaultChordPart(chordPart);
    }

    /**
     * Returns the Score's chord progression.
     */
    public ChordPart getChordPart() {
        //return chordProg;
        return getDefaultChordPart();
    }

    /**
     * Returns the Part at the specified index.
     * @param index     the index of the Part to get
     * @return Part     the Part at the specified index
     */
    public MelodyPartAccompanied getPart(int index) {
        return partList.get(index);
    }

    /**
     * Returns the Note at the specified index across all parts
     * @param index     the index of the Note to get
     * @return      the Note at the specified index
     */
    public Note getNote(int index) {
        int sizeOfPart = getDefaultChordPart().size();
        int partNum = index / sizeOfPart; 
        int indexWithinPart = index % sizeOfPart;
        MelodyPart part = partList.get(partNum);
        return part.getNote(indexWithinPart);
    }

    /**
     * Returns a PartListIterator
     * @return PartListIterator iterating the parts
     */
    public ListIterator getPartIterator() {
        return partList.listIterator();
    }

    /**
     * Returns a copy of the Score.
     * @return Score    a copy of the Score
     */
    public Score copy() {
        //Trace.log(2, "copying Score of size " + size());
        Score newScore = new Score(title, tempo, DEFAULT_VOLUME);
	newScore.setMetre(metre[0], metre[1]);
        PartList newPartList = new PartList(partList.size());
        ListIterator<MelodyPartAccompanied> i = partList.listIterator();

        while(i.hasNext())
            newPartList.add(i.next().copy());

        newScore.partList = newPartList;
        newScore.addDefaultChordPart(getDefaultChordPart().copy());
        newScore.chordPartList = chordPartList; // FIX! want to copy, not share

        newScore.setChordInstrument(getChordInstrument());
        newScore.setBassInstrument(getBassInstrument());

        newScore.setBassMuted(getBassMuted());
        newScore.setDrumMuted(getDrumMuted());
        newScore.setChordMuted(getChordMuted());
        newScore.setMelodyMuted(getMelodyMuted());
        newScore.setMasterVolumeMuted(getMasterVolumeMuted());

        newScore.setBassVolume(getBassVolume());
        newScore.setDrumVolume(getDrumVolume());
        newScore.setChordVolume(getChordVolume());
        newScore.setMelodyVolume(getMelodyVolume());
        newScore.setMasterVolume(getMasterVolume());

        newScore.countInProg = countInProg == null ? null : countInProg.copy();
        
        return newScore;
    }
    
/**
 * Creates and returns a String representation of the Score.
 *
 * @return String the Score as a String
 */
    
@Override
public String toString()
  {
    StringBuilder buffer = new StringBuilder();

    buffer.append("Score ");
    buffer.append(title);
    buffer.append('\n');

    ListIterator<MelodyPartAccompanied> i = partList.listIterator();
    while( i.hasNext() )
      {
        MelodyPartAccompanied mp = i.next();
        buffer.append(mp.toString());
      }
    return buffer.toString();
  }

    /**
     * Sets the tempo of the Score to the specified double.
     * @param tempo     a double containing the tempo
     */
    public void setTempo(double tempo) {
        this.tempo = tempo;
    }
    
    /**
     * Sets the title of the Score
     * @param title   a String representing the title of the Score
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Sets the composer of the Score
     * @param composer   a String representing the composer of the Score
     */
    public void setComposer(String composer) {
        this.composer = composer;
    }
    
    /**
     * Sets the show title of the Score, if any
     * @param composer   a String representing the show title
     */
    public void setShowTitle(String title) {
        this.showTitle = title;
    }
    
    /**
     * Sets the year of the Score
     * @param composer   a String representing the year of the Score
     */
    public void setYear(String year) {
        this.year = year;
    }
    
    /**
     * Sets comments on this Score
     * @param composer   a String representing comments
     */
    public void setComments(String comments) {
        this.comments = comments;
    }
    
    /**
     * Returns the tempo of the Score.
     * @return double   the tempo of the Score
     */
    public double getTempo() {
        return tempo;
    }
    
    /**
     * Returns the total time of the score rounded to the nearest second
     * @return int      the duration of the score in seconds
     */
    public int getTotalTime() {
        checkLength();
        return (int) ((double)getTotalLength() / BEAT / tempo * 60.0);
    }

    /**
     * Returns the title of the Score.
     * @return title    the title of the Score
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the composer of the Score.
     * @return composer    the composer of the Score
     */
    public String getComposer() {
        return composer;
    }

    /**
     * Returns the showTitle of the Score.
     * @return title    the show title of the Score
     */
    public String getShowTitle() {
        return showTitle;
    }

    /**
     * Returns the year of the Score.
     * @return title    the year of the Score
     */
    public String getYear() {
        return year;
    }

    /**
     * Returns comments on the Score.
     * @return comments on the Score.
     */
    public String getComments() {
        return comments;
    }

    public int size() {
        checkLength();
        return partList.size();
    }

    public void checkLength() {
        int maxLength = length;

        if(getDefaultChordPart().size() > maxLength)
            maxLength = getDefaultChordPart().size();

        ListIterator<MelodyPartAccompanied> i = partList.listIterator();
        while(i.hasNext()) {
            MelodyPartAccompanied part = i.next();
            if(part.size() > maxLength)
                maxLength = part.size();
        }
	
        //setLength(maxLength);
    }
    
    /**
     * Creates and returns a MIDI sequence from the Score.
     * Calls Part.render on each Part and (for now) creates a new channel
     * for each Part.  This means that you can only have 16 Parts, which
     * should be changed in the future.
     * @param ppqn       the resolution for the Sequence
     * @return Sequence  the MIDI render
     */
    public Sequence render(short ppqn, 
                           int transposition)
                    throws InvalidMidiDataException {

        int endIndex = getDefaultChordPart().size();    // correct?

        return render(ppqn, 
                      transposition, 
                      true, 
                      endIndex);
    }


   /**
     * Creates and returns a MIDI sequence from the Score.
     * Calls Part.render on each Part and (for now) creates a new channel
     * for each Part.  This means that you can only have 16 Parts, which
     * should be changed in the future.
     * @param ppqn       the resolution for the Sequence
     * @return Sequence  the MIDI render
     */

    public Sequence render(short ppqn, 
                           int transposition, 
                           boolean useDrums, 
                           int endLimitIndex)
                    throws InvalidMidiDataException {
        // to trace sequencing
        //System.out.println("Score: render, start 0, endLimitIndex = " + endLimitIndex);
        MidiSequence seq = new MidiSequence(ppqn);

        long time = 0;
        
        if( countInProg != null )
        {
        // Handle count-in render

        int len = getCountInOffset();

        if( endLimitIndex != ENDSCORE )
          {
            endLimitIndex += len;
          }

        time = countInProg.render(seq,  
                                  time, 
                                  seq.getChordTrack(), 
                                  0, 
                                  true, 
                                  endLimitIndex);
        }

        //System.out.println("time = " + time);

        ListIterator<MelodyPartAccompanied> i = partList.listIterator();
        while(i.hasNext() && Style.limitNotReached(time, endLimitIndex) )
        {
            // render the chord progression in parallel with each melody chorus
            
            MelodyPartAccompanied melodyPart = i.next();
            
            long melTime = melodyPart.render(seq, 
                                           ImproVisor.getMelodyChannel(),
                                           time, 
                                           seq.getMelodyTrack(), 
                                           transposition, 
                                           endLimitIndex);
            
            long chTime = melodyPart.getChordPart().render(seq, 
                                           time, 
                                           seq.getChordTrack(), 
                                           transposition, 
                                           useDrums, 
                                           endLimitIndex);
            
            time = Math.max(melTime, chTime);
       }
        
        //System.out.println("seq = " + seq);

        // Find the longest track, and put a Stop event at the end of it
        MidiSynth.endSequence(seq.getSequence());
        //Trace.log(0, "done rendering, tickLength = " + seq.getSequence().getTickLength());

        //System.out.println("countIn size = " + getCountInOffset());
        return seq.getSequence();
    }

    public int getCountInOffset()
    {
        return countInProg == null ? 0 : countInProg.size();
    }
    
    public int getCountInTime()
    {
        return (int)(getCountInOffset() / BEAT / tempo * 60.0);
    }

/**
 * Writes the Score to the BufferedWriter passed to this method in Leadsheet
 * notation.
 *
 * @param out a BufferedWriter to save the score onto
 */
    
public void saveToLeadsheet(BufferedWriter out) throws IOException
  {
    Chord.initSaveToLeadsheet();

    // Save Chord Parts
    
    out.newLine();

    for( Iterator<String> it = chordPartList.keySet().iterator(); it.hasNext(); )
      {
        String key = it.next();
        ChordPart part = chordPartList.get(key);
        part.saveToLeadsheet(out);
        out.newLine();
      }

    // Save Melody Parts
    
    out.newLine();

    ListIterator<MelodyPartAccompanied> i = partList.listIterator();
    while( i.hasNext() )
      {
        i.next().saveToLeadsheet(out);
        out.newLine();
      }
    }
    
public void dumpMelody()
    {
      dumpMelody(System.out);
    }

      public void dumpMelody(PrintStream out)
        {
        ListIterator<MelodyPartAccompanied> i = partList.listIterator();
        while(i.hasNext()) 
          {
            i.next().dump(out);
            out.println();
          }
        }

    
    public boolean[] getCurrentEnharmonics(int index)
    {
        Polylist tones = new Polylist();
        Chord currentChord = getDefaultChordPart().getCurrentChord(index);
        if(currentChord != null && !currentChord.getName().equals(NOCHORD)) {
            try {
                tones = getDefaultChordPart().getCurrentChord(index).getPriority();
            } catch(NullPointerException e) {
                tones = new Polylist();
                Trace.log(2, "Null pointer exception should be fixed in Score.getCurrentEnharmonics.");
                Trace.log(2, e.getStackTrace().toString());
            }
        }
        return getCurrentEnharmonics(index, tones);
    }
    
    // Determine whether '#' or 'b' is visible in the lick triage utility, based on
    // whatever the current chord is.
    public boolean[] getCurrentEnharmonics(int index, Polylist tones)
    {
        boolean[] enh = new boolean[5];

        // We set the default visible accidental to '#' if we're in a sharp key; otherwise, set
        // the default to 'b'.
        if (keySig >= 0)
        {
            enh[CSHARP] = true;
            enh[DSHARP] = true;
            enh[FSHARP] = true;
            enh[GSHARP] = true;
            enh[ASHARP] = true;
        }
        else
        {
            enh[CSHARP] = false;
            enh[DSHARP] = false;
            enh[FSHARP] = false;
            enh[GSHARP] = false;
            enh[ASHARP] = false;
        }

        // Get the current chord if there is one.
	Chord current = getDefaultChordPart().getCurrentChord(index);

        if(current == null || current.getName().equals(NOCHORD))
            {
            return enh;
            }

        if (tones == null || tones.isEmpty())
            {
            return enh;
            }

        // Look at all the chord tones in the list and determine whether we need to change
        // any accidental labels from '#' to 'b'
        while (tones.nonEmpty())
        {
            NoteSymbol first = (NoteSymbol)tones.first();
            tones = tones.rest();

            if (first.getPitchString().length() > 1)
            {
                switch (first.getPitchString().charAt(0))
                {
                    case 'c':
                        if( first.getPitchString().charAt(1) == '#' )
                            {
                            enh[CSHARP] = true;
                            }
                        break;
                    case 'd':
                        if( first.getPitchString().charAt(1) == 'b' )
                            {
                            enh[CSHARP] = false;
                            }
                        else if( first.getPitchString().charAt(1) == '#' )
                            {
                            enh[DSHARP] = true;
                            }
                        break;
                    case 'e':
                        if( first.getPitchString().charAt(1) == 'b' )
                            {
                            enh[DSHARP] = false;
                            }
                        break;
                    case 'f':
                        if( first.getPitchString().charAt(1) == '#' )
                            {
                            enh[FSHARP] = true;
                            }
                    case 'g':
                        if( first.getPitchString().charAt(1) == 'b' )
                            {
                            enh[FSHARP] = false;
                            }
                        else if( first.getPitchString().charAt(1) == '#' )
                            {
                            enh[GSHARP] = true;
                            }
                        break;
                    case 'a':
                        if( first.getPitchString().charAt(1) == 'b' )
                            {
                            enh[GSHARP] = false;
                            }
                        else if( first.getPitchString().charAt(1) == '#' )
                            {
                            enh[ASHARP] = true;
                            }
                        break;
                    case 'b':
                        if( first.getPitchString().charAt(1) == 'b' )
                            {
                            enh[ASHARP] = false;
                            }
                        break;
                }
            }
        }
        return enh;
    }
    
    /**
     * Calls makeSwing on each individual Part.
     */
    public void makeSwing() {
        ListIterator<MelodyPartAccompanied> i = partList.listIterator();
        while(i.hasNext()) {
            MelodyPartAccompanied m = i.next();
//            Style style = getDefaultChordPart().getStyle();
//            if( style != null )
//              {
//              m.setSwing(style.getSwing());
//              }
            m.makeSwing(m.getSectionInfo());
//            m.makeSwing(getDefaultSectionInfo());
        }
    }
    

    /**
     * Set the style of this score, but only if there is no SectionInfo
     * already established.
     */
    
    public void setStyle(String styleName)
    {
      if( getDefaultSectionInfo() == null || getDefaultSectionInfo().hasOneSection() )
        {
        getDefaultChordPart().setStyle(styleName);
        }
    }

    /**
     * Set the style of this score, but only if there is no SectionInfo
     * already established.
     */
    
    public void setStyle(Style style)
    {
      if( getDefaultSectionInfo() == null || getDefaultSectionInfo().hasOneSection() )
        {
        getDefaultChordPart().setStyle(style);
        }
    }


  /**
   * Add chords in the current selection in RoadMapFrame to this Score.
   */
    
  public void fromRoadMapFrame(RoadMapFrame roadmap)
    {
        getDefaultChordPart().addFromRoadMapFrame(roadmap);
        //setLength(getDefaultChordPart().size());
    }

  
/**
 * Populate a RoadMapFrame with this Score
 * @param roadmap 
 */

public void toRoadMapFrame(RoadMapFrame roadmap)
  {
    roadmap.setMusicalInfo(this);
    getDefaultChordPart().toRoadMapFrame(roadmap);
  }

/**
 * Returns the style of this score
 */
public Style getStyle()
{
    return getDefaultChordPart().getStyle();
}



public void addChord(Chord chord)
  {
    getDefaultChordPart().addChord(chord);
  }

public void addChordPart(String name, ChordPart part)
  {
    if( name.equals("") )
      {
        name = DEFAULT_PROGRESSION;
      }
    chordPartList.put(name, part);
//System.out.println("adding chord part " + part.getTitle() + " to score " + title + " under name " + name);
  }

public void addDefaultChordPart(ChordPart part)
  {
    part.setTitle(DEFAULT_PROGRESSION);
    addChordPart(DEFAULT_PROGRESSION, part);
  }

public ChordPart ensureDefaultChordPart()
  {
    ChordPart defaultChordPart = getDefaultChordPart();
    
    if( defaultChordPart == null )
      {
        defaultChordPart = new ChordPart();
        addDefaultChordPart(defaultChordPart);
      }
    return defaultChordPart;
  }

public void setDefaultChordPart(ChordPart part)
  {
    if( getDefaultChordPart() == null )
      {
        addDefaultChordPart(part);
      }
    else
      {
        chordPartList.put(DEFAULT_PROGRESSION, part);
      }
  }

public ChordPart getChordPart(String name)
  {
    return chordPartList.get(name);
  }

public ChordPart getDefaultChordPart()
  {
    return getChordPart(DEFAULT_PROGRESSION);
  }

public SectionInfo getDefaultSectionInfo()
  {
    return getDefaultChordPart().getSectionInfo();
  }

public String genNewChordPartName()
  {
    String chordPartName;
    do
      {
      chordPartNumber++;
      chordPartName = chordPartBase + chordPartNumber;
      }
    while( getChordPart(chordPartName) != null );
 
    return chordPartName;
  }

/**
 * Given a slotIndex, returns array of two ints:
 * The first is index of the Part containing that slot.
 * The second is the index of a slot within that Part.
 * 
 * @param slotIndex
 * @return 
 */
public int[] partIndexFromSlot(int slotIndex)
  {
    int[] result = {0, 0};
    
    int partIndex = 0;
    int accumulatedSlots = 0;
    int previousAccumulatedSlots;
    ListIterator<MelodyPartAccompanied> i = partList.listIterator();
    while( i.hasNext() )
      {
        previousAccumulatedSlots = accumulatedSlots;
        accumulatedSlots += i.next().size();
        if( slotIndex < accumulatedSlots )
          {
            result[0] = partIndex;
            result[1] = slotIndex - previousAccumulatedSlots;
            return result;
          }
        partIndex++;
      }
    
    return result;
  }

/**
 * Returns the slot in the overall Score, given a specific part and
 * slot within the part.
 * @param partIndex
 * @param slotWithinPart
 * @return 
 */

public int slotFromPartIndices(int partIndex, int slotWithinPart)
  {
    //System.out.print("Part Indices: " + partIndex + ", " + slotWithinPart + " -> ");
    
    ListIterator<MelodyPartAccompanied> i = partList.listIterator();
    int accumulatedSlots = 0;
    for( int j = 0; j < partIndex; j++ )
      {
        accumulatedSlots += i.next().size();
      }
    
    //System.out.println(accumulatedSlots + slotWithinPart);
    
    return accumulatedSlots + slotWithinPart; 
  }


/**
 * Generates a title so that otherwise-unnamed Scores can have a title
 * for reference and debugging.
 * @return 
 */

private static String generateTitle()
  {
    titleCounter++;
    return TITLE_BASE + titleCounter;
  }
}