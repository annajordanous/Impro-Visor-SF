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

package imp.data;

import java.util.*;
import imp.util.*;
import imp.Constants;
import java.io.Serializable;
import polya.*;

/**
 * Contains a rhythmic pattern for use in a bassline and methods needed to
 * realize that bassline according to the rules of the pattern.
 * @see Style
 * @author Stephen Jones
 */
public class BassPattern
        extends Pattern
        implements Constants, Serializable
  {
  /**
   * the random number generator for getRandomItem
   */
  private static Random gen = new Random();

  /**
   * the rules for the pattern, stored as indices into the ruleTypes array
   */
  private Vector<Integer> rules;

  /**
   * the durations for the pattern, stored as leadsheet representation of
   * rhythm
   */
  private Vector<String> durations;

  /**
   * the modifiers for the pattern, e.g. U, D
   */

  private Vector<String> modifiers;

  /**
   * array containing the types of rules
   */
  private static String ruleTypes[] = {"X", "1", "2", "3", "4", "5",
                                         "6", "7", "B", "C", "S", "A", "N", "R",
                                            "="
  };

  /**
   * indices into the ruleTypes array
   */
  private static final int PITCH    = 0;

  private static final int BASS     = 8;

  private static final int CHORD    = 9;

  private static final int SCALE    = 10;

  private static final int APPROACH = 11;

  private static final int NEXT     = 12;

  private static final int REST     = 13;

  private static final int EQUAL    = 14;

  private static final int UP       = 15;

  private static final int DOWN     = 16;



// These are added onto the above rule values to piggy-back accidentals

  private static final int FLATTEN = 100;
  private static final int SHARPEN = 200;

  private static final String FLAT_STRING = "b";
  private static final String SHARP_STRING = "#";

  // Strings for the up/down addition to style editor syntax
  private static final String NOTEPLUS = "U";
  private static final String NOTEMINUS = "D";

  // indicators for the up/down addition to style editor syntax
  private static final int STAY = 0;


  /**
   * If note skip is this interval or beyond, consider it "not near"
   */
  private static final int BIG_RISE = 6;

  /**
   * Allow internal bass to exceed limits by this amount.
   */
  private static final int MARGIN = 8;

  private static final int SOFTMARGIN = 6;


 /**
   * array containing BassPattern keywords
   */
  private static String keyword[] = {"rules", "weight"};

  // indices into the keyword array
  private static final int RULES = 0;

  private static final int WEIGHT = 1;

  /**
   * Creates a new BassPatern (only used by the factory).
   */
  private BassPattern()
    {
    rules = new Vector<Integer>();
    durations = new Vector<String>();
    modifiers = new Vector<String>();
    }

  /**
   * A factory for creating a BassPattern from a Polylist
   * @param L         a Polylist containing BassPattern information
   * @return the BassPattern created from the Polylist, or null if there
   *         was a problem
   */
  public static BassPattern makeBassPattern(Polylist L)
    {
    BassPattern bp = new BassPattern();
    // Example pattern:
    //
    //         (bass-pattern (rules B4+8 (X 5 4) B4 A8) (weight 10))
    //

    while( L.nonEmpty() )
      {
      Object segment = L.first();
      L = L.rest();
      if( segment instanceof Polylist && ((Polylist)segment).nonEmpty() ) // e.g. (rules B4+8 (X 5 4) B4 A8)
        {
        Polylist item = (Polylist)segment;

        if( item.nonEmpty() && item.first() instanceof String )
          {
          String dispatcher = (String)item.first(); // e.g. rules or weight
          item = item.rest();                       // e.g. (B4+8 (X 5 4) B4 A8)

          switch( Leadsheet.lookup(dispatcher, keyword) )
            {
            case RULES:
              while( item.nonEmpty() )
                {
                Object entry = item.first(); // e.g. B4+8
                item = item.rest();          // e.g. ((X 5 4) B4 A8)
//System.out.println("entry = " + entry);
                if( entry instanceof Polylist )
                  {
                  // e.g. (X 5 4)
                  Polylist plist = (Polylist)entry;
                  int len = plist.length();
                  if( len >= 3 && plist.first().equals(ruleTypes[PITCH]) )
                    {
                    String rule = plist.second().toString();
                    String duration = plist.third().toString();
                    String modifier = "";
                    if( len == 4 )
                    {
                        // optional modifier
                    modifier = plist.fourth().toString();
                    }
                    bp.addRule(rule, duration, modifier);
                    }
                  else
                    {
                    return unrecognizedItemError(segment);
                    }
                  }
                else if( entry instanceof String )
                  {
                  String rule = (String)entry;

                  // simply add +/- onto bp
                  if( rule.equals(NOTEPLUS) || rule.equals(NOTEMINUS) )
                    {
                    //System.out.println("entry is " + (String)entry);
                    bp.addRule(rule, "");
                    }
                  
                  else
                   {
                  // e.g. B4+8 or A8
                  String duration = rule.substring(1);
                  rule = rule.substring(0, 1);
                  bp.addRule(rule, duration);
                   }
                  }
                else
                  {
                  return unrecognizedItemError(segment);
                  }
                }
              break;

            case WEIGHT:
              Long w = (Long)item.first();
              bp.setWeight(w.intValue());
              break;

            default:
              return unrecognizedItemError(dispatcher);
            }
          }
        else
          {
          return unrecognizedItemError(segment);
          }
        }
      else
        {
        return unrecognizedItemError(L.first());
        }
      }

    return bp;
    }

  /**
   * Report an error.
   */
  static private BassPattern unrecognizedItemError(Object irritant)
    {
    ErrorLog.log(ErrorLog.WARNING,
            "Ignoring unrecognized item in bass pattern: " + irritant, false);
    return null;
    }

  /**
   * Adds a rule and duration to this BassPattern.
   * @param rule      a String containing the rule
   * @param duration  a String containing the duration
   */
  private void addRule(String rule, String durationString)
  {
      addRule(rule, durationString, "");
  }

      /**
   * Adds a rule and duration to this BassPattern.
   * @param rule      a String containing the rule
   * @param duration  a String containing the duration
   */
  private void addRule(String rule, String durationString, String modifier)
    {
    if( rule.length() == 2 )       // e.g. b5, #4
      {                             // get the rule, alter it
       String prefix = rule.substring(0,1);
       rule = rule.substring(1);
       if( FLAT_STRING.equals(prefix) )
       {
            int Number = Leadsheet.lookup(rule, ruleTypes) + FLATTEN;
            rules.add(Number);
       }
       else if( SHARP_STRING.equals(prefix) )
       {
            int Number = Leadsheet.lookup(rule, ruleTypes) + SHARPEN;
            rules.add(Number);
       }
       else
       {
        ErrorLog.log(ErrorLog.WARNING,
            "Unknown bass rule: " +  prefix + rule, false);
        return;
       }
    }
    else
    {
      rules.add(Leadsheet.lookup(rule, ruleTypes));
    }
    durations.add(durationString);
    modifiers.add(modifier);
    }

  public int getDuration()
    {
    int duration = 0;
    int n = durations.size();
    for( int i = 0; i < n; i++ )
      {
      String durationString = durations.get(i);
      duration += Duration.getDuration0(durationString);
      }
    return duration;
    }

  /**
   * Realizes the Pattern into a sequencable Polylist
   * @param chord     the ChordSymbol to use for the bassline
   * @param nextChord the ChordSymbol that comes next in the progression
   * @param lastNote  a NoteSymbol containing the previous bassline note
   * @return A Polylist of NoteSymbol objects that make up the bassline.
   * Note: Bassline is built in reverse by consing, then reversed as
   *       the final step.
   */
  public Polylist applyRules(ChordSymbol chord, ChordSymbol nextChord,
                              NoteSymbol lastNote)
    {
    //System.out.println("last Note is " + lastNote.getMIDI() );
    Iterator<Integer> i = rules.iterator();
    Iterator<String> j = durations.iterator();
    Iterator<String> m = modifiers.iterator();

    // the returned bassline is a Polylist of NoteSymbols
    Polylist bassLine = Polylist.nil;

    String chordRoot = chord.getRootString();
    ChordForm chordForm = chord.getChordForm();
    Key key = chordForm.getKey(chordRoot);
    int rise = PitchClass.findRise(chordRoot);
  
    // indicator for directional placement
    int indicator = STAY;
    
    while( i.hasNext() )
      {
      int rule = i.next();
      String duration = j.next();
      String modifier = m.next();
      NoteSymbol pitch;

      switch( rule )
        {
        case PITCH: // Allow X for bass too, as a convenience in cutting and pasting in editor
        case BASS:
          {
          pitch = new NoteSymbol(chord.getBass());
          break;
          }
        case NEXT:
          {
          pitch = new NoteSymbol(nextChord.getBass());
          if( !i.hasNext() )
            {
            pitch = new NoteSymbol(
                    pitch.getPitchClass(),
                    pitch.getOctave(),
                    Duration.getDuration0(duration));
            Polylist L = Polylist.list(duration, pitch);
            return bassLine.cons(L).reverse();
            }

          break;
          }
        case CHORD:
          {
          Polylist chordTones =
                  (Polylist)chordForm.getSpell(chordRoot, key);
          if( chordTones.length() > 1 )
            {
            chordTones = lastNote.enhDrop(chordTones);
            }
          pitch = (NoteSymbol)getRandomItem(chordTones);

          break;
          }
        case SCALE:
          {
          Polylist scales = (Polylist)chordForm.getScales();
          if( scales == null || scales.isEmpty() )
            {
            Polylist chordTones =
                    (Polylist)chordForm.getSpell(chordRoot, key);
            if( chordTones.length() > 1 )
              {
              chordTones = lastNote.enhDrop(chordTones);
              }
            pitch = (NoteSymbol)getRandomItem(chordTones);
            break;
            }
          Polylist scale = (Polylist)scales.first();

          NoteSymbol tonic =
                  NoteSymbol.makeNoteSymbol((String)scale.first());

          String scaleType =
                  Advisor.concatListWithSpaces(scale.rest());

          ScaleForm scaleForm = Advisor.getScale(scaleType);

          Polylist tones = scaleForm.getSpell(tonic);
          tones = NoteSymbol.transposeNoteSymbolList(tones, rise);
          tones = tones.reverse().rest().reverse();

          Polylist seconds = getIntervals(2, tones, lastNote);
          Polylist thirds = getIntervals(3, tones, lastNote);
          tones = seconds.append(thirds);

          if( tones.length() > 1 )
            {
            tones = lastNote.enhDrop(tones);
            }
          pitch = (NoteSymbol)getRandomItem(tones);


          break;
          }
        case APPROACH:
          {
          pitch = new NoteSymbol(nextChord.getBass());
          Polylist approach =
                  Polylist.list(pitch.transpose(1), pitch.transpose(-1));
          if( approach.length() > 1 )
            {
            approach = lastNote.enhDrop(approach);
            }
          pitch = (NoteSymbol)getRandomItem(approach);
          break;
          }
        case REST:
          {
          pitch = NoteSymbol.makeNoteSymbol("r");
          break;
          }
        case EQUAL:
           {
            pitch = null;         // initialize
            pitch = placePitch(pitch, lastNote, rule);
            break;
           }
        default:
          {                             // higher than 99 means flat/sharp
          if( (rule > 0 && rule < 8) || rule > 99 ) 
            {
            Polylist scales = (Polylist)chordForm.getScales();

            if( scales == null || scales.isEmpty() )
              {
              Polylist chordTones =
                      (Polylist)chordForm.getSpell(chordRoot, key);
              if( chordTones.length() > 1 )
                {
                chordTones = lastNote.enhDrop(chordTones);
                }
              pitch = (NoteSymbol)getRandomItem(chordTones);
              break;
              }

            Polylist scale = (Polylist)scales.first();

            NoteSymbol tonic =
                    NoteSymbol.makeNoteSymbol((String)scale.first());

            String scaleType =
                    Advisor.concatListWithSpaces(scale.rest());

            ScaleForm scaleForm = Advisor.getScale(scaleType);

            Polylist tones = scaleForm.getSpell(tonic);
            tones = NoteSymbol.transposeNoteSymbolList(tones, rise);
            tones = tones.reverse().rest().reverse();

                // flattened notes
            if( rule > FLATTEN && rule < FLATTEN + 8 )
            {
                rule = rule - FLATTEN;
                pitch = getInterval(rule, tones);
                pitch = pitch.transpose(-1);
            }   // sharpened notes
            else if( rule > SHARPEN && rule < SHARPEN + 8 )
            {
                rule = rule - SHARPEN;
                pitch = getInterval(rule, tones);
                pitch = pitch.transpose(1);
            }
            else
            {
              pitch = getInterval(rule, tones);
            }
           }
          else
            {
            pitch = new NoteSymbol(chord.getBass());
            }

          break;
          }

        }

      if( !pitch.isRest() && rule != EQUAL )
        {
       // System.out.println("Original pitch is " + pitch.getMIDI() );

          // Why -24??

          pitch = pitch.transpose(-24);
          //pitch = placePitchNear(pitch, lastNote, style);

        if( modifier.equals("U") )
          {
          pitch = placePitch(pitch, lastNote, UP);
          }
         else if( modifier.equals("D") )
          {
          pitch = placePitch(pitch, lastNote, DOWN);
          }
         else
          {
          pitch = placePitchNear(pitch, lastNote, style);
          pitch = pressure(pitch, style);
          }
        }

      NoteSymbol note = new NoteSymbol(
              pitch.getPitchClass(),
              pitch.getOctave(),
              Duration.getDuration0(duration));

      bassLine = bassLine.cons(note);

      if( !note.isRest() )
        {
        lastNote = note;
        }
     }
      
     

    return bassLine.reverse();
    }

  /**
   * Returns a random item from a given Polylist.
   * @param L         a Polylist to return an item from
   * @return a random Object from the Polylist
   */
  public static Object getRandomItem(Polylist L)
    {
    return L.nth(gen.nextInt(L.length()));
    }

  /**
   * Takes a list of notes and an index interval and returns the
   * NoteSymbol at that index.
   * @param interval  an int representing the index interval to access
   * @param notes     a Polylist of NoteSymbol objects
   * @return a NoteSymbol at the proper index interval
   */
  public static NoteSymbol getInterval(int interval, Polylist notes)
    {
    interval = (interval % 8 - 1) % notes.length();// FIX!
    return (NoteSymbol)notes.nth(interval);
    }

  /**
   * Returns the notes that are a certaing index interval away from a given
   * root.
   * @param interval  an int representing the index interval to access
   * @param notes     a Polylist of NoteSymbol objects
   * @param root      a NoteSymbol being the reference point to index from
   * @return a Polylist of NoteSymbols representing the index interval in
   *         either direction from the root
   */
  public static Polylist getIntervals(int interval, Polylist notes,
                                        NoteSymbol root)
    {
    interval = interval % 7 - 1;

    int rootPos = -1;
    for( int i = 0; i < notes.length(); i++ )
      {
      if( notes.nth(i) instanceof NoteSymbol &&
              ((NoteSymbol)notes.nth(i)).enharmonic(root) )
        {
        rootPos = i;
        break;
        }
      }

    assert (rootPos != -1);

    Polylist L = Polylist.nil;
    L = L.cons(notes.nth((rootPos + interval) % notes.length()));
    L =
            L.cons(notes.nth((rootPos - interval + notes.length()) % notes.length()));

    return L;
    }



  /**
   * Takes a pitch NoteSymbol and a base NoteSymbol and transposes the
   * pitch to be within the octave above or below the base depending on
   * the indicator passed in.
   * @param pitch     a NoteSymbol that is the pitch to place
   * @param base      a NoteSymbol that is the base note
   * @param indicator  an int determining direction of placement
   * @return a NoteSymbol that is the placed pitch
   */
  public static NoteSymbol placePitch(NoteSymbol pitch,
                                        NoteSymbol base,
                                          int indicator)
    { 
      switch( indicator )
      {
        case UP:
        {
         return placePitchAbove(pitch, base);
        }
        case DOWN:
        {
         return placePitchBelow(pitch, base);
        }
        case EQUAL:
        {
         return base;
        }
      }
      // shouldn't get here
      return null;
    }



  /**
   * Takes a pitch NoteSymbol and a base NoteSymbol and transposes the
   * pitch to be within the octave above the base.
   * @param pitch     a NoteSymbol that is the pitch to place
   * @param base      a NoteSymbol that is the base note
   * @return a NoteSymbol that is the placed pitch
   */
  public static NoteSymbol placePitchAbove(NoteSymbol pitch,
                                             NoteSymbol base)
    {
    int semitones = base.getSemitonesAbove(pitch);
    return base.transpose(semitones);
    }

  /**
   * Takes a pitch NoteSymbol and a base NoteSymbol and transposes the
   * pitch to be within the octave below the base.
   * @param pitch     a NoteSymbol that is the pitch to place
   * @param base      a NoteSymbol that is the base note
   * @return a NoteSymbol that is the placed pitch
   */
  public static NoteSymbol placePitchBelow(NoteSymbol pitch,
                                             NoteSymbol base)
    {
    // Note the role reversal of pitch and base from the previous method
    int semitones = pitch.getSemitonesAbove(base);
    return base.transpose(-semitones);
    }

  /**
   * Takes a pitch NoteSymbol and a base NoteSymbol and transposes the
   * pitch to be near the base and within the given range.
   * @param pitch     a NoteSymbol that is the pitch to place
   * @param base      a NoteSymbol that is the base note
   * @param low       a NoteSymbol that is the lower range
   * @param high      a NoteSymbol that is the upper range
   * @return a NoteSymbol that is the placed pitch
   */
  public static NoteSymbol placePitchNear(NoteSymbol pitch,
                                            NoteSymbol base,
                                            Style style)
    {
    NoteSymbol low = style.getBassLow();
    NoteSymbol high = style.getBassHigh();
    //System.out.println("placePitchNear " + pitch + ", style = " + style + ", low = " + low + ", base = " + base + ", high = " + high);
    int rise = base.getSemitonesAbove(pitch);
    NoteSymbol note;

    // Pitch octave placement is the subject of some experimentation.
  /*  
    boolean drop_down = base.getDuration() >= BEAT || rise >= BIG_RISE;

    if( drop_down )
      {
      note = placePitchBelow(pitch, base);
      if( note.getMIDI() < low.getMIDI() - MARGIN )
        {
        note = note.transpose(12);
        // note = placePitchAbove(pitch, base);
        }
     //System.out.println("base = " + base + ", rise = " + rise + ", note = " + note + " below");
     }
    else
      {
      note = placePitchAbove(pitch, base);
      if( note.getMIDI() > high.getMIDI() + MARGIN )
        {
        note = note.transpose(-12);
        // note = placePitchBelow(pitch, base);
        }
     //System.out.println("base = " + base + ", rise = " + rise + ", note = " + note + " above");
      }*/

      while( pitch.getMIDI() > base.getMIDI() + MARGIN ||
                pitch.getMIDI() < base.getMIDI() - MARGIN)
      {
      double rand = java.lang.Math.random();
      if( rand < 0.5 )
      {
       if( pitch.getMIDI() > base.getMIDI() + MARGIN )
        pitch = pitch.transpose(-12);
       else if( pitch.getMIDI() < base.getMIDI() - MARGIN )
        pitch = pitch.transpose(12);
      }
      else
      {
       if( pitch.getMIDI() < base.getMIDI() - MARGIN )
        pitch = pitch.transpose(12);
       else if( pitch.getMIDI() > base.getMIDI() + MARGIN )
        pitch = pitch.transpose(-12);
      }
      //  System.out.println("PITCH IS " + pitch.getMIDI());
      }
      return pitch;
    }



  /**
   * Takes a pitch NoteSymbol and a range and transposes the
   * pitch probabilistically based on its position in the range.
   * @param pitch     a NoteSymbol that is the pitch to place
   * @param low       a NoteSymbol that is the lower range
   * @param high      a NoteSymbol that is the upper range
   * @return a NoteSymbol that is the placed pitch
   */
  public static NoteSymbol pressure( NoteSymbol pitch, Style style )
    {
    NoteSymbol low = style.getBassLow();
    NoteSymbol high = style.getBassHigh();

    // find the center by going up from the low
   int hardmargin = high.getSemitonesAbove(low);
   // For some reason hardmargin is only half as large as I'd like
   NoteSymbol center = low.transpose(hardmargin);
 //  System.out.println("high " + high.getMIDI() + " low " + low.getMIDI() + " center " + center.getMIDI());

   NoteSymbol softmarginhigh = center.transpose(SOFTMARGIN);
   NoteSymbol softmarginlow = center.transpose(-SOFTMARGIN);

   // take probability linearly based on pitch position in margins
   if( pitch.getMIDI() > softmarginhigh.getMIDI() ) 
    {
    int numerator = pitch.getMIDI() - softmarginhigh.getMIDI();
    int denominator = high.getMIDI() - softmarginhigh.getMIDI();
    double prob = (double)numerator / (double)denominator;
    prob = prob*prob*prob*prob;
    double rand = java.lang.Math.random();
    if( prob > rand )
     {
        pitch = pitch.transpose(-12);
     }
    }
   else if( pitch.getMIDI() < softmarginlow.getMIDI() )
    {
    int numerator = softmarginlow.getMIDI() - pitch.getMIDI();
    int denominator = softmarginlow.getMIDI() - low.getMIDI();
    double prob = (double)numerator / (double)denominator;
    prob = prob*prob*prob*prob;
    double rand = java.lang.Math.random();
    if( prob > rand )
     {
     pitch = pitch.transpose(12);
     }
    }
    //System.out.println("New pitch is " + pitch.getMIDI());
    return pitch;
   }


  //Added summer2007 for use with Style GUI
  public String forGenerator()
    {
    String rule = "";
    for( int i = 0; i < durations.size(); i++ )
      {
      //System.out.println("i: " + i);

      int ruleIndex = rules.get(i);
      // Note that ruleIndex can have FLATTEN or SHARPEN added to it.
      // Need to subtract these before indexing array.

      String accidental = "";

      if( ruleIndex > SHARPEN )
      {
          ruleIndex -= SHARPEN;
          accidental = "#";
      }
      else if( ruleIndex > FLATTEN )
      {
          ruleIndex -= FLATTEN;
          accidental = "b";
      }

      String nextNote = ruleTypes[ruleIndex];
      try
        {
        Integer.parseInt(nextNote);
        rule += "(X " +  accidental + nextNote + " " + durations.get(i) + " " + modifiers.get(i) + ") ";
        }
      catch( NumberFormatException e )
        {
        rule += nextNote + durations.get(i) + " ";
        }
      }
    return rule;
    }

  }
