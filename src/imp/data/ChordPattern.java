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

import imp.util.ErrorLog;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import polya.Polylist;
import polya.PolylistBuffer;
import polya.PolylistEnum;

/**
 * Contains a rhythmic pattern for use in a chord accompaniment and methods
 * needed to realize that rhythmic pattern with voice leading according
 * to a chord progression.
 * @see Style
 * @author Stephen Jones
 */

public class ChordPattern
        extends Pattern implements Serializable
{
/**
 * the rules for the pattern, stored as indices into the ruleTypes array
 */
private ArrayList<Integer> rules;

/**
 * the durations for the pattern, stored as leadsheet representation of
 * rhythm
 */
private ArrayList<String> durations;

/**
 * array containing the types of rules
 */
private static String ruleTypes[] =
  {
  "X", "R", "V"
  };
// indices into the ruleTypes array
private static final int STRIKE = 0;

private static final int REST = 1;

private static final int VOLUME = 2;

/**
 * array containing ChordPattern keywords
 */
private static String keyword[] =
  {
  "rules", "weight", "push"
  };

// indices into the keyword array
private static final int RULES = 0;

private static final int WEIGHT = 1;

private static final int PUSH = 2;


private String pushString = "";

private int pushAmount = 0; // push amount, in slots


/**
 * Creates a new ChordPattern (only used by the factory).
 */
private ChordPattern()
  {
  rules = new ArrayList<Integer>();
  durations = new ArrayList<String>();
  }


/**
 * A factory for creating a ChordPattern from a Polylist.
 * @param L         a Polylist containing ChordPattern information
 * @return the ChordPattern created from the Polylist, or null if there
 *         was a problem
 */
public static ChordPattern makeChordPattern(Polylist L)
  {
    // Example of L:
    // 	(chord-pattern (rules P8 X1 R4 X2 X4)(weight 5)(push 8/3)
    //
    // X = "hit", R = "rest"
    // The notation for push is the same as a duration.
    // For example, 8/3 is an eighth-note triplet
    
  Polylist M = L;
    
  ChordPattern cp = new ChordPattern();

  while( L.nonEmpty() )
    {
    Polylist item = (Polylist)L.first();
    L = L.rest();

    String dispatcher = (String)item.first();
    item = item.rest();
    switch( Leadsheet.lookup(dispatcher, keyword) )
      {
      case RULES:
        {
        while( item.nonEmpty() )
          {
          String s = (String)item.first();

          String rule = s.substring(0, 1);
          String dur = s.substring(1);

          cp.addRule(rule, dur);
          item = item.rest();
          }
        break;
        }
      case WEIGHT:
        {
        Number w = (Number)item.first();
        cp.setWeight(w.intValue());
        break;
        }
      case PUSH:
        {
        cp.pushString = item.first().toString();
        cp.pushAmount = Duration.getDuration(cp.pushString);
        //System.out.println("pushAmount " + pushString + " = " + cp.pushAmount + " slots");
        }
      }
    }
  //System.out.println("makeChordPattern on " + M + " returns " + cp);
  return cp;
  }


/**
 * Adds a rule and duration to this ChordPattern.
 * @param rule      a String containing the rule
 * @param duration  a String containing the duration
 */
private void addRule(String rule, String duration)
  {

    rules.add(Leadsheet.lookup(rule, ruleTypes));
    durations.add(duration);
  }


@Override
/**
 * Get the duration, in slots
 * @return 
 */
public int getDuration()
  {
    int duration = 0;
    
    Iterator<Integer> r = rules.iterator();
    Iterator<String> d = durations.iterator();
    
    while( r.hasNext() )
      {
        Integer rule = r.next();
        String dur = d.next();
        if( rule.intValue() != 2 )
          {
            // Ignore volume in computing duration
            duration += Duration.getDuration(dur);
          }
      }
    
    return duration;
  }


/**
 * Realizes the Pattern into a sequencable Polylist.
 * @param chord     the ChordSymbol to voice
 * @param lastChord a Polylist containing the last chord voicing
 * @return A Polylist that can be sequenced.  This Polylist has two elements.
 * 
 *         The first element is another Polylist that contains
 *         a sequence of chord voicings (each of which is a Polylist of
 *         NoteSymbols, including possibly volume settings.)  
 * 
 *         The second element is a MelodyPart containing
 *         containing rests, each of which is a duration corresponding to
 *         the voicings.
 */

public Polylist applyRules(ChordSymbol chord, Polylist lastChord)
  {
  Iterator<Integer> i = rules.iterator();
  Iterator<String> j = durations.iterator();
  
  lastChord = BassPattern.filterOutStrings(lastChord);

  System.out.println("applyRules in: Chord = " + chord + ", rules = " + rules + ", durations = " + durations);

  String chordRoot = chord.getRootString();
  ChordForm chordForm = chord.getChordForm();
  Key key = chordForm.getKey(chordRoot);
  int rise = PitchClass.findRise(chordRoot);

  // FIXME: this is sort of a hacky way to do the durations since we
  // don't really have a proper way to store music with multiple voices
  MelodyPart durationMelody = new MelodyPart();

  PolylistBuffer chordLine = new PolylistBuffer();
  
  int volume = 127;

  while( i.hasNext() )
    {
    int rule = i.next();
    String duration = j.next();

    System.out.println("     rule = " + rule + ", duration = " + duration);
    // Process the symbols in the pattern into notes and rests,
    // inserting volume indication when the volume changes.
    
    switch( rule )
      {
      case STRIKE:
        {
//System.out.println("STRIKE");
        // Add the volume indicator to the front of the voicing.
        durationMelody.addNote(new Rest(Duration.getDuration(duration)));
         
        Polylist voicing = findVoicing(chord, lastChord, style);
//System.out.println("findVoicing " + chord + " vs " + lastChord + " gives " + (voicing == null ? "null" : voicing));
        if( voicing == null )
          {
          voicing = Polylist.nil;
          //break;
          }

        chordLine.append(voicing.cons("v" + volume));
        lastChord = voicing;
        break;
        }
      case REST:
        {
//System.out.println("REST");
        durationMelody.addNote(new Rest(Duration.getDuration(duration)));
        chordLine.append(NoteSymbol.makeNoteSymbol("r" + duration));
        break;
        }
          
      case VOLUME:
        {
//System.out.println("VOLUME");
        volume = Integer.parseInt(duration);
        break;
        }
      }
    }

  Polylist result = Polylist.list(chordLine.toPolylist(), durationMelody);

  System.out.println("applyRules: Chord = " + chord + ", rules = " + rules + ", durations = " + durations + ", result (chordline, durations) = " + result);
  return result;
  }


/**
 * Returns a boolean determining whether the given chord can be voiced
 * based on the given Style.
 * @param chord     the ChordSymbol to voice
 * @param style     the Style to use to voice the ChordSymbol
 * @return a boolean determining whether the given chord can be voiced
 *         based on the given Style
 */
public static boolean goodVoicing(ChordSymbol chord, Style style)
  {
  return goodVoicing(chord, style.getChordBase(), style);
  }


/**
 * Returns a boolean determining whether the given chord can be voiced
 * based on the given Style and a previous chord.
 * @param chord     the ChordSymbol to voice
 * @param lastChord a Polylist containing the last chord voicing
 * @param style     the Style to use to voice the ChordSymbol
 * @return a boolean determining whether the given chord can be voiced
 *         based on the given Style and a previous chord
 */
public static boolean goodVoicing(ChordSymbol chord, Polylist lastChord,
                                  Style style)
  {
  Polylist L = findVoicing(chord, lastChord, style, false);
  if( L == null )
    {
    return false;
    }
  else
    {
    return true;
    }
  }


/**
 * Returns a voicing for a chord.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @return a Polylist containing the chord voicing
 */
public static Polylist findVoicing(ChordSymbol chord, Polylist lastChord,
                                   Style style)
  {
  return findVoicing(chord, lastChord, style, true);
  }


/**
 * Returns a voicing for a chord.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing
 */
public static Polylist findVoicing(ChordSymbol chord, Polylist lastChord,
                                   Style style, boolean verbose)
  {
  Polylist voicing = findVoicingAndExtension(chord, lastChord, style, verbose);

  if( voicing == null )
    {
    return null;    // append the voicing and the extension
    }
  voicing = ((Polylist)voicing.first()).append(
          (Polylist)voicing.second());

  return voicing;
  }


/**
 * Returns A voicing for a chord, separating out the voicing and its 
 * extension.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing and its extension
 */
public static Polylist findVoicingAndExtension(ChordSymbol chord,
                                               Polylist lastChord, Style style,
                                               boolean verbose)
  {
  Polylist voicings = getVoicingAndExtensionList(chord, lastChord, style,
          verbose);
  if( voicings == null )
    {
    return null;
    }
  return (Polylist)BassPattern.getRandomItem(voicings);
  }


/**
 * Returns A voicing for a chord, separating out the voicing and its 
 * extension.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing and its extension
 */
public static Polylist findFirstVoicingAndExtension(ChordSymbol chord,
                                                    Polylist lastChord,
                                                    Style style, boolean verbose)
  {
  Polylist voicings = getVoicingAndExtensionList(chord, lastChord, style,
          verbose);
  if( voicings == null )
    {
    return null;
    }
  return (Polylist)voicings.first();
  }


/**
 * Returns a list of acceptable voicings for a chord, separating out the voicing and its 
 * extension.
 * @param chord     a ChordSymbol to voice
 * @param lastChord the previous chord voicing in the progression
 * @param style     the Style to voice the chord in
 * @param verbose   a boolean deciding whether to show error messages
 * @return a Polylist containing the chord voicing and its extension
 */
public static Polylist getVoicingAndExtensionList(ChordSymbol chord,
                                                  Polylist lastChord,
                                                  Style style, boolean verbose)
  {
  Polylist voicing = chord.getVoicing();
  String chordRoot = chord.getRootString();
  ChordForm chordForm = chord.getChordForm();
  Key key = chordForm.getKey(chordRoot);
  int rise = PitchClass.findRise(chordRoot);
//System.out.println("getVoicingsAndExtensionList " + chord + " style = " + style + " getVoicing() = " + voicing);

  if( voicing.nonEmpty() )
    {
    // if the voicing is already specified in the chord, then
    // put the voicing and extension near the previous chord
    
    Polylist extension = chord.getExtension();
    Polylist v = ChordPattern.placeVoicing(lastChord, voicing, extension,
            style.getChordLow(), style.getChordHigh());

    if( v == null )
      {
      // if the specified voicing doesn't fit in range, error
      // and don't voice this chord
      if( verbose )
        {
        ErrorLog.log(ErrorLog.WARNING,
                "Voicing does not fit within range: " + voicing);
        }
      return null;
      }
    else
      {
      return Polylist.list(v);
      }

    }
  else
    {
    // if there is no voicing specified, then find one!
    // get the voicings from the vocabulary file for this chord type
    
    Polylist voicings = chordForm.getVoicings(chordRoot, key,
            style.getVoicingType());
//System.out.println("chord = " + chord + ", voicings = " + voicings);
    // pick out the good voicings based on the previous chord and
    // the range
    
    voicings = chooseVoicings(lastChord, voicings,
            style.getChordLow(), style.getChordHigh());
//System.out.println("chord = " + chord + ", voicings after choosing = " + voicings);

    // if none of the specified voicings fit in the range
    // or no voicings are specified, then generate voicings
    if( voicings.isEmpty() )
      {
      voicings = chordForm.generateVoicings(chordRoot, key);

      if( voicings.isEmpty() )
        {
        return null;
        }

      Polylist preferredVoicings = chooseVoicings(lastChord, voicings,
              style.getChordLow(), style.getChordHigh());

      if( preferredVoicings.nonEmpty() )
        {
        voicings = preferredVoicings;
        }

      // if there still is no good voicing, print out an error and return null
      if( voicings.isEmpty() )
        {
        if( verbose )
          {
          ErrorLog.log(ErrorLog.SEVERE,
                  "Range too small to voice chord: " + chord);
          }
        return null;
        }
      }
    return voicings;
    }
  }


/**
 * Choose appropriate voicings from a list of voicings.
 * @param lastChord a Polylist containing the last chord voicing
 * @param voicings  a Polylist containing voicings to choose from
 * @param lowNote   a NoteSymbol determining the lower end of the range
 * @param highNote  a NoteSymbol determining the high end of the range
 * @return a Polylist of appropriate voicings
 */
public static Polylist chooseVoicings(Polylist lastChord, Polylist voicings,
                                      NoteSymbol lowNote, NoteSymbol highNote)
  {
  PolylistBuffer goodVoicings = new PolylistBuffer();

  int smallestAverageLeap = 127;

  for( PolylistEnum venum = voicings.elements(); venum.hasMoreElements();)
    {
    Polylist voicing = (Polylist)venum.nextElement();
    
//System.out.println("in chooseVoicings " + voicing + ", lastChord = " + lastChord);
    Polylist v = (Polylist)voicing.first();
    Polylist e = (Polylist)voicing.second();

    // put the voicing near the previous chord and within range
    Polylist L = placeVoicing(lastChord, v, e, lowNote, highNote);

    // if the voicing can't be placed, it is bad, so we just continue 
    if( L == null )
      {
      continue;
      }

    v = (Polylist)L.first();
    e = (Polylist)L.second();

    // find the averageLeap between the last chord and this
    // voicing plus its extension

    int leap = averageLeap(v.append(e), lastChord);

    /*
    leap += averageLeap(lastChord,v.append(e));
    leap /= 2;
     */

    if( leap < smallestAverageLeap )
      {
      smallestAverageLeap = leap;
      goodVoicings.append(Polylist.list(v, e));
      }
    else if( leap == smallestAverageLeap )
      {
      goodVoicings.append(Polylist.list(v, e));
      }
    }
  return goodVoicings.toPolylist();
  }


/**
 * Takes a voicing and places it just above another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @return a Polylist containing the placed voicing
 */
public static Polylist placeVoicingAbove(Polylist lastChord,
                                         Polylist voicing)
  {
  NoteSymbol lastNote = (NoteSymbol)lastChord.first();
  NoteSymbol voicingNote = (NoteSymbol)voicing.first();

  int difference = lastNote.getMIDI() - voicingNote.getMIDI();

  Polylist newVoicing;
  if( difference > 0 )
    {
    int octaves = (difference / 12) + 1;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else if( difference <= -12 )
    {
    int octaves = difference / 12;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else
    {
    newVoicing = voicing;
    }
  return newVoicing;
  }


/**
 * Takes a voicing and places it just below another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @return a Polylist containing the placed voicing
 */
public static Polylist placeVoicingBelow(Polylist lastChord,
                                         Polylist voicing)
  {
  NoteSymbol lastNote = (NoteSymbol)lastChord.first();
  NoteSymbol voicingNote = (NoteSymbol)voicing.first();

  int difference = lastNote.getMIDI() - voicingNote.getMIDI();

  Polylist newVoicing;
  
  if( difference < 0 )
    {
    int octaves = (difference / 12) - 1;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else if( difference >= 12 )
    {
    int octaves = difference / 12;
    newVoicing = NoteSymbol.transposeNoteSymbolList(voicing, 12 * octaves);
    }
  else
    {
    newVoicing = voicing;
    }
  return newVoicing;
  }


/**
 * Takes a voicing and places it near another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @param extension a Polylist containing a voicing extension
 * @param low       a NoteSymbol determining the low end of the range
 * @param high      a NoteSymbol determining the high end of the range
 * @return a Polylist containing the placed voicing and its extension
 */
public static Polylist placeVoicing(Polylist lastChord, Polylist voicing,
                                    Polylist extension,
                                    NoteSymbol low, NoteSymbol high)
  {
  NoteSymbol oldNote = (NoteSymbol)voicing.first();
  voicing = placeVoicing(lastChord, voicing, low, high);
  if( voicing == null )
    {
    return null;
    }
  NoteSymbol newNote = (NoteSymbol)voicing.first();
  int diff = newNote.getMIDI() - oldNote.getMIDI();
  extension = NoteSymbol.transposeNoteSymbolList(extension, diff);
  return Polylist.list(voicing, extension);
  }


/**
 * Takes a voicing and places it near another voicing.
 * @param lastChord a Polylist containing previous voicing
 * @param voicing   a Polylist containing the voicing to place
 * @param low       a NoteSymbol determining the low end of the range
 * @param high      a NoteSymbol determining the high end of the range
 * @return a Polylist containing the placed voicing
 */
public static Polylist placeVoicing(Polylist lastChord, Polylist voicing,
                                    NoteSymbol low, NoteSymbol high)
  {
  if( lastChord.isEmpty() )
    {
    lastChord = Polylist.list(low, high); // rk 8/6/07
    }

  NoteSymbol lastNote = (NoteSymbol)lastChord.first();
  NoteSymbol voicingNote = (NoteSymbol)voicing.first();

  int semitones = lastNote.getSemitonesAbove(voicingNote);
  Polylist v;

  if( semitones >= 6 )
    {
    v = placeVoicingBelow(lastChord, voicing);
    }
  else
    {
    v = placeVoicingAbove(lastChord, voicing);
    }
  NoteSymbol lowest = NoteSymbol.getLowest(v);
  NoteSymbol highest = NoteSymbol.getHighest(v);

  while( lowest.getMIDI() < low.getMIDI() )
    {
    v = NoteSymbol.transposeNoteSymbolList(v, 12);
    lowest = NoteSymbol.getLowest(v);
    highest = NoteSymbol.getHighest(v);
    if( highest.getMIDI() > high.getMIDI() )
      {
      return null;
      }
    }

  while( highest.getMIDI() > high.getMIDI() )
    {
    v = NoteSymbol.transposeNoteSymbolList(v, -12);
    lowest = NoteSymbol.getLowest(v);
    highest = NoteSymbol.getHighest(v);
    if( lowest.getMIDI() < low.getMIDI() )
      {
      return null;
      }
    }

  return v;
  }


/**
 * Takes two chord voicings and calculates the "average leap" 
 * between the two.
 * The "average leap" is the average "smallest leap" between individual
 * notes in chord2 and all of chord1.
 * @param chord1     a Polylist of a chord to compare
 * @param chord2     a Polylist of a chord to compare
 * @return the "average leap" between the two chords
 * @see #smallestLeap(Polylist,NoteSymbol)
 */
public static int averageLeap(Polylist chord1, Polylist chord2)
  {
  int sum = 0;
  int num = chord2.length();
  
  while( chord2.nonEmpty() ) //( int i = 0; i < chord2.length(); i++ )
    {
    NoteSymbol note = (NoteSymbol)chord2.first();
    int leap = smallestLeap(chord1, note);
    sum += leap;
    chord2 = chord2.rest();
    }

  return (int)((double)sum / num);
  }


/**
 * Takes a chord and a note and computes the smallest leap from
 * the note to a note in the chord.
 * @param chord     a Polylist containing the chord to compare
 * @param note      a NoteSymbol of the note to compare
 */
public static int smallestLeap(Polylist chord, NoteSymbol note)
  {
  int noteMIDI = note.getMIDI();
  int smallestLeap = 127;
  while( chord.nonEmpty() )
    {
    NoteSymbol chordNote = (NoteSymbol)chord.first();
    int leap = Math.abs(chordNote.getMIDI() - noteMIDI);
    if( leap < smallestLeap )
      {
      smallestLeap = leap;
      }
    chord = chord.rest();
    }

  return smallestLeap;
  }

//Added summer2007 for use with Style GUI
public String forGenerator()
  {
  StringBuilder rule = new StringBuilder();
  
  for( int i = 0; i < durations.size(); i++ )
    {
    String nextNote = ruleTypes[rules.get(i)];
    rule.append(nextNote);
    rule.append(durations.get(i));
    rule.append(" ");
    }
  return rule.toString();
  }


/**
 * Get the "push" amount for this pattern, in slots
 */

public int getPushAmount()
  {
    return pushAmount;
  }


public String getPushString()
  {
    return pushString;
  }

@Override
public String toString()
  {
    return "ChordPattern: " + rules + " " + durations;
  }
}
