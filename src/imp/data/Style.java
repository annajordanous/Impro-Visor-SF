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
import imp.com.PlayScoreCommand;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Random;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.PolylistEnum;


/**
 * An object that contains patterns and parameters for generating an
 * accompaniment.
 * Contains functions to create a Style from text, output a Style to text,
 * and, given a ChordPart, arrange patterns to construct an accompaniment.
 * @see         Pattern
 * @see         BassPattern
 * @see         DrumPattern
 * @see         ChordPattern
 * @see         ChordPart
 * @author      Stephen Jones, Robert Keller
 */
public class Style
        implements Constants, Serializable
  {
  private static LinkedHashMap<String, Style> allStyles = new LinkedHashMap<String, Style>();
  
  private static ArrayList<Style> orderedStyles = null;
  
  private static String defaultStyleName = "no-style";
  
  public static final String USE_PREVIOUS_STYLE = "*";
  
  private static int defaultDrumPatternDuration = 480;

  /**
   * the random number generator for styles
   */
  private static Random gen = new Random();

  /**
   * a String containing the name
   */
  private String name = defaultStyleName;

  /**
   * a String containing the default name of a Style (a so-called NULL Style)
   */
  public static String NULL = "";

  /**
   * a boolean that determines whether to use "no-style" behavior
   */
  private boolean noStyle = false;

  /**
   * a String containing comments on the Style
   */
  private String comments = "";

  /**
   * a double containing the swing value
   */
  private double swing = 0.5;

  /**
   * a double containing the swing value
   */
  private double accompanimentSwing = 0.5;

  /**
   * a String determining the voicing type
   */
  private String voicingType = "closed";

  /**
   * a boolean that determines whether to automatically extend chords
   */
  private boolean useExtensions = false;

  /**
   * a Polylist of NoteSymbol objects that determine the base chord from 
   * which to voice-lead
   */
  private Polylist chordBase = Polylist.list(
          NoteSymbol.makeNoteSymbol("c-"),
          NoteSymbol.makeNoteSymbol("e-"),
          NoteSymbol.makeNoteSymbol("g-"));

  /**
   * a NoteSymbol determining the lower range of the chord progression
   */
  private NoteSymbol chordLow = NoteSymbol.makeNoteSymbol("c-");

  /**
   * a NoteSymbol determining the upper range of the chord progresion
   */
  private NoteSymbol chordHigh = NoteSymbol.makeNoteSymbol("a");

  /**
   * an int determining the MIDI channel for chords
   */
  static private int chordChannel = 3;

  /**
   * an int determining the MIDI instrument for chords
   */
  static private int chordInstrument = 1;

  /**
   * an int determining the MIDI channel for drums
   */
  static private int drumChannel = 9;

  /**
   * an int determining the MIDI instrument for drums
   */
  static private int drumInstrument = 1;

  /**
   * an int determining the MIDI channel for bass
   */
  static private int bassChannel = 6;

  /**
   * an int determining the MIDI instrument for bass
   */
  static private int bassInstrument = 33;

  /**
   * a NoteSymbol determining the lower range for bass
   */
  private NoteSymbol bassLow = NoteSymbol.makeNoteSymbol("g---");

  /**
   * a NoteSymbol determining the higher range for bass
   */
  private NoteSymbol bassHigh = NoteSymbol.makeNoteSymbol("g-");

  /**
   * a NoteSymbol determining the base bass note to start a bassline from
   */
  private NoteSymbol bassBase = NoteSymbol.makeNoteSymbol("c--");

  /**
   * a ArrayList of this Style's BassPattern objects
   */
  private ArrayList<BassPattern> bassPatterns = new ArrayList<BassPattern>();

  /**
   * a ArrayList of this Style's DrumPattern objects
   */
  private ArrayList<DrumPattern> drumPatterns = new ArrayList<DrumPattern>();

  /**
   * a ArrayList of this Style's ChordPattern objects
   */
  private ArrayList<ChordPattern> chordPatterns = new ArrayList<ChordPattern>();

  /**
   * a String array containing keywords used in Style specifications
   */
  private static String keyword[] = {"name", "bass-pattern", "bass-high",
                                       "bass-low", "bass-base", "swing",
                                       "drum-pattern", "chord-pattern",
                                       "chord-high", "chord-low", "chord-base",
                                       "use-extensions", "no-style",
                                       "voicing-type", "comments",
                                       "comp-swing"
  };

  // indices into the keyword array
  private static final int NAME = 0;

  private static final int BASS_PATTERN = 1;

  private static final int BASS_HIGH = 2;

  private static final int BASS_LOW = 3;

  private static final int BASS_BASE = 4;

  private static final int SWING = 5;

  private static final int DRUM_PATTERN = 6;

  private static final int CHORD_PATTERN = 7;

  private static final int CHORD_HIGH = 8;

  private static final int CHORD_LOW = 9;

  private static final int CHORD_BASE = 10;

  private static final int USE_EXTENSIONS = 11;

  private static final int NO_STYLE = 12;

  private static final int VOICING_TYPE = 13;

  private static final int COMMENTS = 14;

  private static final int ACCOMPANIMENT_SWING = 15;


  public boolean usePreviousStyle()
    {
      return name.equals(USE_PREVIOUS_STYLE);
    }
  
  public static Style getStyle(String name)
    {
      return allStyles.get(name);
    }
  
  public static void setStyle(String name, Style style)
    {
      allStyles.put(name, style);
    }
  
  public static boolean noStyles()
    {
      return numberOfStyles() == 0;
    }
  
  public static int numberOfStyles()
    {
      ensureStyleArray();
      return orderedStyles.size(); 
    }
  
  /**
   * Used by StyleList in Notate.
   * @param index
   * @return 
   */
  
    public static Style getNth(int index)
      {
        ensureStyleArray();
        return orderedStyles.get(index);
      }
    
    private static void ensureStyleArray()
      {
        //if( orderedStyles == null )
            {
            orderedStyles = new ArrayList<Style>(allStyles.values());
            }       
      }
  
  /**
   * Gets the voicing type.
   * @return the voicing type
   */
  public String getVoicingType()
    {
    return voicingType;
    }

  /**
   * Gets the bass channel.
   * @return the bass channel
   */
  public int getBassChannel()
    {
    return bassChannel;
    }

  public ArrayList<BassPattern> getBP()
    {
    return bassPatterns;
    }

  public ArrayList<DrumPattern> getDP()
    {
    return drumPatterns;
    }

  public ArrayList<ChordPattern> getCP()
    {
    return chordPatterns;
    }

  public int getDrumPatternDuration()
    {
    if( drumPatterns.size() > 0 )
      {
      return drumPatterns.get(0).getDuration();
      }
    else
      {
      return defaultDrumPatternDuration;
      }
    }

  /**
   * Returns the number of total patterns--all of bass, chords, and drums.
   *
   */
  public int getTotalPatterns()
    {
    return bassPatterns.size() + chordPatterns.size() + drumPatterns.size();
    }

  /**
   * Gets the drum channel.
   * @return the drum channel
   */
  public int getDrumChannel()
    {
    return drumChannel;
    }

  /**
   * Gets the chord channel.
   * @return the chord channel
   */
  public int getChordChannel()
    {
    return chordChannel;
    }

  /**
   * Gets the name.
   * @return the name
   */
  public String getName()
    {
    return name;
    }
  
  public void setName(String name)
    {
      this.name = name;
    }

  /**
   * Gets the comments.
   * @return the comments
   */
  public String getComments()
    {
    return comments;
    }

  /**
   * Sets the comments.
   * @param c         a String containing the comments
   */
  public void setComments(String c)
    {
    comments = c;
    }

  /**
   * Returns the name.
   * @return the name of this Style
   */
  public String toString()
    {
    return getName();
    }

  /**
   * Returns the swing value.
   * @return the swing value
   */
  public double getSwing()
    {
    return swing;
    }

  /**
   * Returns the accompaniment swing value.
   * @return the accompaniment swing value
   */
  public double getAccompanimentSwing()
    {
    //System.out.println("accompanimentSwing = " + accompanimentSwing);
    return accompanimentSwing;
    }

  /**
   * Sets the swing value.
   * @param s         a double containing the swing value
   */
  public void setSwing(double s)
    {
      //System.out.println("setting swing of " + name + " to " + s);
    swing = s;
    }

  /**
   * Sets the accompaniment swing value.
   * @param s         a double containing the accompaniment swing value
   */
  public void setAccompanimentSwing(double s)
    {
    accompanimentSwing = s;
    }

  /**
   * Sets the chord instrument.
   * @param inst      an int containing the chord instrument
   */
  public void setChordInstrument(int inst, String caller)
    {
    chordInstrument = inst;
    }

  /**
   * Gets the chord instrument.
   * @return the chord instrument
   */
  public int getChordInstrument()
    {
    return chordInstrument;
    }

  /**
   * Sets the bass instrument.
   * @param inst      an int containing the chord instrument
   */
  public void setBassInstrument(int inst)
    {
    bassInstrument = inst;
    }

  /**
   * Gets the bass instrument.
   * @return the bass instrument
   */
  public int getBassInstrument()
    {
    return bassInstrument;
    }

  /**
   * Sets the drum instrument.
   * @param inst      an int containing the drum instrument
   */
  public void setDrumInstrument(int inst)
    {
    drumInstrument = inst;
    }

  /**
   * Gets the drum instrument.
   * @return the drum instrument
   */
  public int getDrumInstrument()
    {
    return drumInstrument;
    }

  /**
   * Returns the noStyle parameter.
   * @return determines whether this is a "no-Style"
   */
  public boolean noStyle()
    {
    return noStyle;
    }

  /**
   * Sets the noStyle parameter.
   * @param b         a boolean determining whether this is a "no-style"
   */
  public void setNoStyle(boolean b)
    {
    noStyle = b;
    }

  /**
   * Creates a default Style (considered a NULL Style).
   */
  public Style()
    {
    }

  /**
   * Returns a copy of the Style.
   * @return a copy of the Style
   */
  public Style copy()
    {
    Style style = new Style();

    style.noStyle = noStyle;
    style.setSwing(swing);
    style.setAccompanimentSwing(accompanimentSwing);
    style.chordBase = chordBase;
    style.chordLow = chordLow;
    style.chordHigh = chordHigh;
    style.bassLow = bassLow;
    style.bassHigh = bassHigh;
    style.bassBase = bassBase;
    style.comments = comments;
    style.voicingType = voicingType;
    style.useExtensions = useExtensions;

    style.name = name;

    style.bassPatterns = bassPatterns;
    style.drumPatterns = drumPatterns;
    style.chordPatterns = chordPatterns;
    return style;
    }

  /**
   * A factory for creating a new Style from a Polylist.
   * @param  L        a Polylist containing Style information
   * @return the Style created from the Polylist, or null if there
   *         was a problem
   */
  public static Style makeStyle(Polylist L)
    {
    Style style = new Style();

    while( L.nonEmpty() )
      {

      if( (L.first() instanceof Polylist) )
        {
        Polylist item = (Polylist)L.first();
        L = L.rest();

        if( item.nonEmpty() )
          {
          Object dispatcher = item.first();
          item = item.rest();

          switch( Leadsheet.lookup((String)dispatcher, keyword) )
            {
            case CHORD_PATTERN:
              {
              ChordPattern cp = ChordPattern.makeChordPattern(item);
              cp.setStyle(style);
              if( cp != null )
                {
                style.chordPatterns.add(cp);
                }
              else
                {
                return null;
                }
              break;
              }
            case DRUM_PATTERN:
              {
              DrumPattern dp = DrumPattern.makeDrumPattern(item);
              dp.setStyle(style);
              if( dp != null )
                {
                style.drumPatterns.add(dp);
                }
              else
                {
                return null;
                }
              break;
              }
            case BASS_PATTERN:
              {
              BassPattern bp = BassPattern.makeBassPattern(item);
              if( bp != null )
                {
                bp.setStyle(style);
                style.bassPatterns.add(bp);
                }
              else
                {
                return null;
                }
              break;
              }
            case VOICING_TYPE:
              {
              style.voicingType = (String)item.first();
              break;
              }
            case COMMENTS:
              {
              String commentsString = Leadsheet.concatElements(item);
              style.comments = commentsString;
              break;
              }
            case NAME:
              {
              style.name = (String)item.first();
              break;
              }
            default:
              {
              style.load((String)dispatcher, item);
              break;
              }
            }
          }
        }
      else
        {
        L = L.rest();
        }
      }

    return style;
    }

  /**
   * A method to change parameters of an already constructed Style
   * from text specification.
   * @param dispatcher        a String containing a Style keyword
   * @param item              a Polylist containing the arguments for
   *                          dispatcher's Style keyword
   */
  public void load(String dispatcher, Polylist item)
    {
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case BASS_HIGH:
        {
        bassHigh =
                NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case BASS_LOW:
        {
        bassLow =
                NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case BASS_BASE:
        {
        bassBase =
                NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case CHORD_HIGH:
        {
        chordHigh =
                NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case CHORD_LOW:
        {
        chordLow =
                NoteSymbol.makeNoteSymbol((String)item.first());
        break;
        }
      case CHORD_BASE:
        {
        PolylistEnum chord = item.elements();
        
        PolylistBuffer base = new PolylistBuffer();
        
        while( chord.hasMoreElements() )
          {
          NoteSymbol note =
                  NoteSymbol.makeNoteSymbol((String)chord.nextElement());
          
          base.append(note);
          }
        chordBase = base.toPolylist();
        break;
        }
      case SWING:
        {
        swing = (Double)item.first();
        break;
        }
      case ACCOMPANIMENT_SWING:
        {
        accompanimentSwing = (Double)item.first();
        break;
        }
      case USE_EXTENSIONS:
        {
        useExtensions = true;
        break;
        }
      case NO_STYLE:
        {
        noStyle = true;
        break;
        }
      }
    }

  /**
   * Saves a Style to text format used in Leadsheets.
   * @param out       a BufferedWriter to write the Style to
   */
  public void saveLeadsheet(BufferedWriter out) throws IOException
    {
    out.write("(style " + name);
    out.newLine();
    out.write("    (" + keyword[SWING] + " " + swing + ")");
    out.newLine();
    out.write("    (" + keyword[ACCOMPANIMENT_SWING] + " " + swing + ")");
    out.newLine();
    out.write("    (" + keyword[BASS_HIGH] + " " + bassHigh.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[BASS_LOW] + " " + bassLow.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[BASS_BASE] + " " + bassBase.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[CHORD_HIGH] + " " + chordHigh.toPitchString() + ")");
    out.newLine();
    out.write("    (" + keyword[CHORD_LOW] + " " + chordLow.toPitchString() + ")");
    out.newLine();
    out.write("    " + NoteSymbol.makePitchStringList(chordBase).cons(keyword[CHORD_BASE]));
    out.newLine();

    if( noStyle )
      {
      out.write("    (" + keyword[NO_STYLE] + ")");
      }

    out.write(")");
    out.newLine();
    }

  /**
   * Sets the base chord.
   * @param list      a Polylist containing NoteSymbol objects that make up
   *                  the base chord
   */
  public void setChordBase(Polylist list)
    {
    chordBase = list;
    }

  /**
   * Sets the lower range for the chords.
   * @param low       a NoteSymbol determining the lower range for the chords
   */
  public void setChordLow(NoteSymbol low)
    {
    chordLow = low;
    }

  /**
   * Sets the higher range for the chords.
   * @param high      a NoteSymbol determining the higher range for the
   *                  chords
   */
  public void setChordHigh(NoteSymbol high)
    {
    chordHigh = high;
    }

  /**
   * Gets the chord base.
   * @return the chord base
   */
  public Polylist getChordBase()
    {
    return chordBase;
    }

  /**
   * Gets the upper range NoteSymbol.
   * @return the upper range
   */
  public NoteSymbol getChordHigh()
    {
    return chordHigh;
    }

  /**
   * Gets the lower range NoteSymbol.
   * @return the lower range
   */
  public NoteSymbol getChordLow()
    {
    return chordLow;
    }

  /**
   * Gets the base bass note.
   * @return the bass base
   */
  public NoteSymbol getBassBase()
    {
    return bassBase;
    }

  /**
   * Gets the upper range bass note.
   * @return the bass upper range
   */
  public NoteSymbol getBassHigh()
    {
    return bassHigh;
    }

  /**
   * Gets the lower range bass note.
   * @return the bass lower range
   */
  public NoteSymbol getBassLow()
    {
    return bassLow;
    }

  /**
   * Function that takes a ArrayList of Pattern objects and a duration, 
   * randomly chooses from the largest Patterns that will fit in that
   * duration, and returns that Pattern.
   * @param <T>       a type variable (referring to a type of Pattern)
   * @param patterns  a ArrayList of T objects to choose from
   * @param duration  an int determining the duration to fill
   * @return the Pattern chosen
   */
  private static <T extends Pattern> T getPattern(ArrayList<T> patterns,
                                                    int duration)
    {
    // this ArrayList will hold patterns that are the correct duration
    ArrayList<T> goodPatterns = new ArrayList<T>();

    // find the largest pattern duration that is less than duration
    int largestDuration = 0;
    for( int i = 0; i < patterns.size(); i++ )
      {
      T temp = patterns.get(i);
      int tempDuration = temp.getDuration();

      if( tempDuration > largestDuration &&
              tempDuration <= duration )
        {
        largestDuration = tempDuration;
        }
      }

    // if we don't have a short enough pattern, we'll play nothing
    if( largestDuration == 0 )
      {
      // NEW: Instead of playing nothing, find the shortest pattern
      // that is longer than duration and truncate it.
      int shortestDuration = Integer.MAX_VALUE;
      T shortestPattern = null;

      for( int i = 0; i < patterns.size(); i++ )
        {
        T temp = patterns.get(i);
        int tempDuration = temp.getDuration();

        if( tempDuration >= duration &&
                tempDuration < shortestDuration )
          {
          shortestDuration = tempDuration;
          shortestPattern = temp;
          }
        }
      return null;
      }

    // sum the weights of the patterns we are choosing from
    double sum = 0;
    for( int i = 0; i < patterns.size(); i++ )
      {
      if( patterns.get(i).getDuration() == largestDuration )
        {
        sum += patterns.get(i).getWeight();
        goodPatterns.add(patterns.get(i));
        }
      }

    // randomly choose one of the "good patterns"
    int random = gen.nextInt((int)sum);
    double weights = 0;
    for( int i = 0; i < goodPatterns.size(); i++ )
      {
      weights += goodPatterns.get(i).getWeight();
      if( random < weights )
        {
        return goodPatterns.get(i);
        }
      }

    return null;
    }

  /**
   * Using the DrumPattern objects of this Style, sequences a drumline
   * of a specified duration onto the track.
   * @param seq       the Sequence that contains the Track
   * @param track     the Track to put drum events on
   * @param time      a long containing the time to start the drumline
   * @param duration  an int containing the duration of the drumline
   */
  private void makeDrumline(Sequence seq, Track track, long time,
                             int duration, int endLimitIndex )
          throws InvalidMidiDataException
    {
    // tracing render info
    //System.out.println("drumline: time = " + time + " duration = " + duration
    // + " endLimitIndex = " + endLimitIndex);

    // loop until we've found patterns to fill up the duration
    while( duration > 0 )
      {
      // Get a drum pattern, if any

      DrumPattern pattern = getPattern(drumPatterns, duration);
      //System.out.println("pattern = " + pattern + ", duration = " + duration);
      // if there's no suitable pattern, play nothing
      if( pattern == null )
        {
        break;
        }

      int patternDuration = pattern.getDuration();
      duration -= patternDuration;

      // we get a polylist containing drum parts
      Polylist drumline = pattern.applyRules();

      // each element of the polylist is a drum part
      // so we go through and render each element
      while( drumline.nonEmpty() )
        {
        MelodyPart d = (MelodyPart)drumline.first();

        d.setSwing(accompanimentSwing);
        d.setInstrument(drumInstrument);
        d.makeSwing();
        d.render(seq, drumChannel, time, d.getVolume(), track, 0, endLimitIndex);
        drumline = drumline.rest();
        }
      time += (patternDuration * seq.getResolution()) / BEAT;
      }
    }

  /**
   * Below is a check to decide whether to continue sequencing.
   * It is used in multiple files.
   * Sequencing should continue if either play-to-end was specified
   * or the end of select is reached.
   * In the latter case, is not desired to generate a midi render for the
   * full score, as that would have to be cut off and causes blips in the
   * sound.
   */

  public static int magicFactor = 4;

  public static boolean limitNotReached(long time, int endLimitIndex)
  {
  return true || endLimitIndex == ENDSCORE // i.e. play to end
      || time <= magicFactor*endLimitIndex; // limit not reached
  }

  public static int getMagicFactor()
  {
      return magicFactor;
  }

  /**
   * Using the ChordPattern objects of this Style, sequences a chordline
   * of a specified duration onto the track.
   * @param seq       the Sequence that contains the Track
   * @param track     the Track to put currentChord events on
   * @param time      a long containing the time to start the chordline
   * @param currentChord     a ChordSymbol containing the currentChord currentChord to render
   * @param previousChord a Polylist containing the previous currentChord
   * @param duration  an int containing the duration of the chordline
   * @return a Polylist containing the last currentChord used in the chordline
   */
private Polylist makeChordline(
        Sequence seq,
        Track track,
        long time,
        Chord currentChord,
        Polylist previousChord,
        int duration,
        int transposition,
        int endLimitIndex)
        throws InvalidMidiDataException
  {
    // To trace rendering info:
    System.out.println("makeChordLine: time = " + time + " duration = "
        + duration + " endLimitIndex = " + endLimitIndex);

    // Because we have no data structure to hold multi-voice parts, 
    // we manually render polylists for each currentChord in this method.

    // Select Bank 0 before program change. 
    // Not sure this is correct. Check before releasing!
    
    track.add(MidiSynth.createBankSelectEventMSB(0, time));
    track.add(MidiSynth.createBankSelectEventLSB(0, time));

    track.add(MidiSynth.createProgramChangeEvent(chordChannel,
                                                 chordInstrument, 
                                                 time));

    ChordSymbol symbol = currentChord.getChordSymbol();

    // The while loop is in case one pattern does not fill
    // the required duration. We may need multiple patterns.
    
    boolean beginning = true;
    while( duration > 0 && limitNotReached(time, endLimitIndex) )
      {
        // Get a pattern for this chord.
        // A pattern can contain volume information.
        
        ChordPattern pattern = getPattern(chordPatterns, duration);

    System.out.println("\nmakeChordLine on " + currentChord + " using ChordPattern " + pattern);
        Polylist c;
        if( pattern == null )
          {
            // if there's no pattern, and we haven't used a previous
            // pattern on this currentChord, then just play the currentChord for the 
            // duration
            if( !beginning )
              {
                break;
              }
            Polylist v = ChordPattern.findVoicing(symbol, previousChord, this);
            MelodyPart dM = new MelodyPart();
            dM.addNote(new Rest(duration));
            duration = 0;
            c = Polylist.list(Polylist.list(v), dM);
          }
        else
          {
            if( beginning )
              {
               // Accommodate possible "pushing" of first currentChord.
               // The amount is given in slots.
               int pushAmount = pattern.getPushAmount();
               int deltaT = pushAmount * seq.getResolution() / BEAT;
               time -= deltaT;
               if( time < 0 )
                  {
                    time = 0;
                    deltaT = 0;
                  }
                
                duration -= (deltaT + pattern.getDuration());
              }
            else
              {
              duration -= pattern.getDuration();
              }
            // we get a polylist containing the chords (each in a polylist)
            // and a "duration melody" which is a MelodyPart representing
            // the durations of each currentChord
            c = pattern.applyRules(symbol, previousChord);
          }

        System.out.println("result of applying, c (chords, duration melody) = " + c);
        
        // since we can't run the swing algorithm on a Polylist of 
        // NoteSymbols, we can use this "duration melody" which
        // corresponds to the chords in the above Polylist to find
        // the correct swung durations of the notes

        // chords is a list of lists of notes, each outer list representing
        // a chord voicing. Note that volume settings can be amongst these
        // notes.
        
        // durationMelody is the pattern, consisting of rests of various
        // durations.
        
        Polylist chords = (Polylist) c.first();
        MelodyPart durationMelody = (MelodyPart) c.second();
        
        durationMelody.setSwing(accompanimentSwing);
        durationMelody.makeSwing();

        Part.PartIterator i = durationMelody.iterator();
        PolylistEnum e = chords.elements();

System.out.println("chords = " + chords);

        int volume = 127;
        
        while( e.hasMoreElements() )
          {
            Object voicing = e.nextElement(); // A single currentChord's voicing

             // Note that voicing should be a Polylist, and may contain volume
                    
            Note note = (Note) i.next();      // Note from the "duration melody"
            
           System.out.println("voicing = " + voicing + ", note = " + note);

            int dur = note.getRhythmValue();  // A single currentChord's duration

            long offTime = time + dur * seq.getResolution() / BEAT;
        
            // render each NoteSymbol in the currentChord
            if( voicing instanceof Polylist )
              {
                Polylist v = (Polylist) voicing;
                currentChord.setVoicing(v);
                Polylist L = v;
                
                // All notes in the voicing are rendered at the same start time
                
                while( L.nonEmpty() )
                  {
                    Object ob = L.first();
                    if( ob instanceof NoteSymbol )
                      {
                      NoteSymbol ns = (NoteSymbol)ob;
                      note = ns.toNote();
                      note.setRhythmValue(dur);
System.out.println("rendering chord note " + note + " with volume " + volume);
                      note.render(seq, track, time, offTime, chordChannel, volume, transposition);
                      }
                    else if( ob instanceof String )
                      {
                        // Possibly volume information
                        String st = (String) ob;
                        if( st.startsWith("v") )
                          {
                            volume = Integer.parseInt(st.substring(1));
                          }
                      }
                    L = L.rest();
                  }

                previousChord = v;
              }

            time = offTime;
          }

        beginning = false;
      }

    // Un-comment this to see voicings
    //System.out.println("voicing " + currentChord + " as " + previousChord);

    //System.out.println("MIDI sequence = " + MidiFormatting.prettyFormat(sequence2polylist(seq)));
    return previousChord;
  }


  /**
   * Using the BassPattern objects of this Style, sequences a bassline
   * of a specified duration onto the track.
   * @param bassline  a Polylist of NoteSymbols makeing up the bassline so far
   * @param chord     a ChordSymbol containing the currentChord chord to render
   * @param nextChord a ChordSymbol containing the next chord
   * @param previousBassNote  a NoteSymbol containing the previous note
   * @param duration  an int containing the duration of the chordline
   * @return a Polylist of NoteSymbols to be sequenced
   */
  private Polylist makeBassline(
          Polylist bassline,
          ChordSymbol chord, 
          ChordSymbol nextChord,
          NoteSymbol lastNote, 
          int duration,
          int transposition)
          throws InvalidMidiDataException
    {
    while( duration > 0 )
      {
      BassPattern pattern = getPattern(bassPatterns, duration);
System.out.println("makeBassLine pattern = " + pattern + ", duration = " + duration);

      // just skip this area if there is no appropriate pattern
      if( pattern == null )
        {
        Rest r = new Rest(duration);
        bassline = bassline.addToEnd(NoteSymbol.makeNoteSymbol(r.toLeadsheet()));
        break;
        }

      duration -= pattern.getDuration();

      // we get a Polylist of NoteSymbols back from the applyRules 
      // function
      Polylist b;
      if( duration > 0 )
        {
        b = pattern.applyRules(chord, chord, lastNote);
        }
      else
        {
        b = pattern.applyRules(chord, nextChord, lastNote);
        }

System.out.println("noteSymbols = " + b);

      // set previousBassNote to the correct value
      Polylist d = b.reverse();
      while( d.nonEmpty() )
        {
        if( d.first() instanceof NoteSymbol )
          {
          NoteSymbol ns = (NoteSymbol)d.first();
          d = d.rest();
          if( !ns.isRest() )
            {
            lastNote = ns;  // Not used ??
            break;
            }
          }
        else
          {
          d = d.rest();
          }
        }

      if( bassline.nonEmpty() &&
              bassline.reverse().first() instanceof Polylist )
        {
        Polylist L = (Polylist)bassline.last();
        String dur = (String)L.first();
        bassline = bassline.allButLast();
        NoteSymbol ns = (NoteSymbol)b.first();
        int pDur = Duration.getDuration0(dur) + ns.toNote().getRhythmValue();
        ns = new NoteSymbol(ns.getPitchClass(), ns.getOctave(), pDur, ns.getVolume());
        b = b.rest().cons(ns);
        }

      bassline = bassline.append(b);
      }
System.out.println("returned bassline = " + bassline);
    return bassline;
    }

  /**
   * Using the Pattern objects of this Style, sequences an accompaniment
   * for the given ChordPart.
   * @param seq       the Sequence that contains the Track
   * @param track     the Track to put the accompaniment on
   * @param time      a long containing the time to start the accompaniment
   * @param chordPart the ChordPart to render
   * @return a long containing the ending time of the accompaniment
   */
  public long render(Sequence seq, 
                     long time, 
                     Track track,
                      ChordPart chordPart, 
                      int startIndex, 
                      int endIndex, 
                      int transposition, 
                      int endLimitIndex)
          throws InvalidMidiDataException
  {
      // refactored to direct to the method that follows with hasStyle parameter
      
      return render(seq, 
                    time, 
                    track, 
                    chordPart, 
                    startIndex, 
                    endIndex, 
                    transposition, 
                    PlayScoreCommand.USEDRUMS, 
                    endLimitIndex);
  }



    /**
     * Ripped from above, to allow non-style, hence no drums...
     *
   * Using the Pattern objects of this Style, sequences an accompaniment
   * for the given ChordPart.
   * @param seq       the Sequence that contains the Track
   * @param track     the Track to put the accompaniment on
   * @param time      a long containing the time to start the accompaniment
   * @param chordPart the ChordPart to render
   * @return a long containing the ending time of the accompaniment
   */

  public long render(Sequence seq, 
                     long time, 
                     Track track,
                     ChordPart chordPart, 
                     int startIndex, 
                     int endIndex, 
                     int transposition, 
                     boolean useDrums, 
                     int endLimitIndex)
          throws InvalidMidiDataException
    {
    boolean hasStyle = !noStyle();

    // to trace sequencing info:
    //System.out.println("Sequencing Style: " + this + " startIndex = " + startIndex
    // + " endIndex = " + endIndex + " endLimitIndex = " + endLimitIndex + " useDrums = " + useDrums + " hasStyle = " + hasStyle);


    Part.PartIterator i =
            chordPart.iterator(chordPart.getCurrentChordIndex(startIndex));

    long startTime = time;

    if( hasStyle && useDrums )
      {
      // Introduce drums, if there is a Style

      makeDrumline(seq, track, startTime, endIndex - startIndex, endLimitIndex);
      }

    Chord next = null;
    Chord prev = null;

    Polylist bassline = Polylist.nil;

    int index = startIndex;
    ChordSymbol chord;
    ChordSymbol nextChord;
    ChordSymbol previousExtension = null;
    NoteSymbol previousBassNote = bassBase;
    Polylist previousChord = Polylist.nil; // rk 8/06/07 was: chordBase;

    int numNotes = 1;

    // Iterating over one ChordPart with i
    
    while( (i.hasNext() || next != null) && (endLimitIndex == ENDSCORE || index <= endLimitIndex) )
      {
      if( next == null )
        {
        index = i.nextIndex();
        next = (Chord)i.next();
        }

      Chord currentChord = next;

      int rhythmValue = currentChord.getRhythmValue();
      if( startIndex > index )
        {
        rhythmValue -= startIndex - index;
        }

      if( i.hasNext() )
        {
        index = i.nextIndex();

        next = (Chord)i.next();
        }
      else
        {
        next = null;
        index = chordPart.size();
        }

      if( endIndex <= index )
        {
        rhythmValue -= index - endIndex;
        }

      if( !hasStyle )
        {
        time = currentChord.render(seq, 
                              track, 
                              time, 
                              getChordChannel(), 
                              this, 
                              prev,
                              rhythmValue, 
                              transposition, 
                              endLimitIndex);
        prev = currentChord;
        if( endIndex <= index )
          {
          break;
          }
        else
          {
          continue;
          }
        }

      chord = currentChord.getChordSymbol();
      if( next == null || next.getChordSymbol().isNOCHORD() )
        {
        nextChord = chord;
        }
      else
        {
        nextChord = next.getChordSymbol();
        }


      if( !chord.isNOCHORD() && hasStyle )
        {
        if( useExtensions )
          {
          if( gen.nextInt(3) == 0 )
            {
            chord = extend(chord, previousExtension);
            }
          previousExtension = chord;
          }

        previousChord = makeChordline(seq, 
                                  track, 
                                  time,
                                  currentChord, 
                                  previousChord, 
                                  rhythmValue, 
                                  transposition, 
                                  endLimitIndex);
        }

      // adjust bass octave between patterns only, not within
      if( previousBassNote.higher(getBassHigh()) )
        {
        previousBassNote = previousBassNote.transpose(-12);
        //System.out.println("downward to " + previousBassNote);
        }
      else if( getBassLow().higher(previousBassNote) )
        {
        previousBassNote = previousBassNote.transpose(12);
        //System.out.println("upward to " + previousBassNote);
        }
//System.out.println("\nAbout to make bassline, chord = " + chord + ", hasStyle = " + hasStyle);
      if( !chord.isNOCHORD() && hasStyle )
        {
        bassline = makeBassline(bassline,
                                chord, 
                                nextChord, 
                                previousBassNote, 
                                rhythmValue, 
                                transposition);
System.out.println("Finished making bassline = " + bassline);
        Polylist d = bassline.reverse();
        while( d.nonEmpty() )
          {
          if( d.first() instanceof NoteSymbol )
            {
            NoteSymbol ns = (NoteSymbol)d.first();
            d = d.rest();
            if( !ns.isRest() )
              {
              previousBassNote = ns;
              break;
              }
            }
          else
            {
            d = d.rest();
            }
          }
        }
      else
        {
        Rest r = new Rest(rhythmValue);
        NoteSymbol rest = NoteSymbol.makeNoteSymbol(r.toLeadsheet());
        bassline = bassline.addToEnd(rest); // was reverse().cons(rest).reverse();
        }

      time += rhythmValue * seq.getResolution() / BEAT;
      if( endIndex <= index )
        {
        break;
        }
      }
System.out.println("\nbassline = " + bassline);

    if( bassline.nonEmpty() )
      {
       //System.out.println("sequencing bassline " + bassline);
       
      MelodyPart bassLine = new MelodyPart();

      Object last = bassline.last(); // was reverse().first();

      if( last instanceof Polylist )
        {
        Polylist L = (Polylist)last;
        NoteSymbol ns = (NoteSymbol)L.second();
        bassline = bassline.replaceLast(ns);
        }

      int volume = 127;

      // add each note to our bassline melody
      while( bassline.nonEmpty() )
        {
        NoteSymbol noteSymbol = (NoteSymbol)bassline.first();
        bassLine.addNote(noteSymbol.toNote());
        bassline = bassline.rest();
        }

      bassLine.setSwing(accompanimentSwing);
      bassLine.setInstrument(bassInstrument);
      bassLine.setVolume(MAX_VOLUME);
      bassLine.makeSwing();
      bassLine.render(seq, 
                      bassChannel, 
                      startTime, 
                      MAX_VOLUME,   // FIX
                      track, 
                      transposition, 
                      endLimitIndex);
      }

    return time;
    }


  /**
   * Extend the currentChord chord based on a previous chord.
   * @param chord     a ChordSymbol containing the chord to extend
   * @param previousChord a ChordSymbol containing the previous chord
   * @return a ChordSymbol containing the extended chord
   */
  public static ChordSymbol extend(ChordSymbol chord, ChordSymbol lastChord)
    {
    int rise = PitchClass.findRise(chord.getRootString());
    Polylist extensions;

    // get a random extension if there is no previous chord
    if( lastChord == null )
      {
      extensions = chord.getChordForm().getExtensions();
      extensions = ChordSymbol.chordSymbolsFromStrings(extensions);
      extensions = ChordSymbol.transpose(extensions, rise);
      extensions = extensions.cons(chord);

      return (ChordSymbol)BassPattern.getRandomItem(extensions);
      }

    extensions = Advisor.getExtensions(Advisor.getFinalName(
            chord.toString()));
    extensions = ChordSymbol.chordSymbolsFromStrings(extensions);
    extensions = ChordSymbol.transpose(extensions, rise);
    extensions = extensions.cons(chord);

    // check for appropriate extensions based on previous chord
    Polylist goodExtensions = Polylist.nil;
    int highCommon = -20;
    while( extensions.nonEmpty() )
      {
      ChordSymbol c = (ChordSymbol)extensions.first();
      extensions = extensions.rest();
          int common = commonPitches(lastChord, c) -
              uncommonPitches(lastChord, c);

      if( common == highCommon )
        {
        goodExtensions = goodExtensions.cons(c);
        }
      else if( common > highCommon )
        {
        highCommon = common;
        goodExtensions = Polylist.list(c);
        }
      }

    return (ChordSymbol)BassPattern.getRandomItem(goodExtensions);
    }

  /**
   * Takes two chords and returns the number of pitches the second one
   * has that the first one doesn't.
   * @param c1        a ChordSymbol to compare
   * @param c2        a chordSymbol to compare
   * @return an int containing the number of pitches the second chord
   *         has that the first one doesn't
   */
  public static int uncommonPitches(ChordSymbol c1, ChordSymbol c2)
    {
    Polylist s1 = c1.getChordForm().getSpell(c1.getRootString());
    Polylist s2 = c2.getChordForm().getSpell(c2.getRootString());
    
    int sum = 0;
    while( s2.nonEmpty() )
      {
      NoteSymbol n = (NoteSymbol)s2.first();
      s2 = s2.rest();

      if( !n.enhMember(s1) )
        {
        sum++;
        }

      }
    return sum;
    }

  /**
   * Returns the number of pitches two chords have in common.
   * @param c1        a ChordSymbol to compare
   * @param c2        a ChordSymbol to compare
   * @return an int containing the number of pitches the two chords have
   *         in common
   */
  public static int commonPitches(ChordSymbol c1, ChordSymbol c2)
    {
    Polylist s1 = c1.getChordForm().getSpell(c1.getRootString());
    Polylist s2 = c2.getChordForm().getSpell(c2.getRootString());

    int sum = 0;
    while( s2.nonEmpty() )
      {
      NoteSymbol n = (NoteSymbol)s2.first();
      s2 = s2.rest();

      if( n.enhMember(s1) )
        {
        sum++;
        }

      }
    return sum;
    }

    @Override
    public boolean equals(Object obj)
      {
        if( obj == null )
          {
            return false;
          }
        if( getClass() != obj.getClass() )
          {
            return false;
          }
        final Style other = (Style) obj;
        if( (this.name == null) ? (other.name != null) : !this.name.equals(other.name) )
          {
            return false;
          }
        return true;
      }

    @Override
    public int hashCode()
      {
        int hash = 5;
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
      }

  }
