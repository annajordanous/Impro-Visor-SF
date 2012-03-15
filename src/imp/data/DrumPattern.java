/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
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
import java.io.Serializable;
import polya.Polylist;

/**
 * Contains rhythmic patterns for several drums, which are used to construct a
 * drumline according to methods contained in this class.
 *
 * @see Style
 * @author Stephen Jones; Robert Keller cleaned up Polylist code to not use nth
 */

public class DrumPattern extends Pattern implements Constants, Serializable
{

public static int defaultDrumPatternDuration = 480;

/**
 * The list representation of the rules in this pattern
 */
Polylist listRules = Polylist.nil;


/**
 * a Polylist of drum Polylists, the structure of each drum Polylist is: (DRUM
 * RULES DURATIONS)
 */

private Polylist drums;


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
 * array containing DrumPattern keywords
 */

private static String keyword[] =
  {
    "drum", "weight", "volume"
  };


// indices into the keyword array
private static final int DRUM = 0;
private static final int WEIGHT = 1;


/**
 * Creates a new DrumPattern (only used by the factory).
 */
private DrumPattern()
  {
    drums = Polylist.nil;
  }

/**
 * A factory for creating a DrumPattern from a Polylist.
 *
 * @param L a Polylist containing DrumPattern information
 * @return the DrumPattern created from the Polylist, or null if there was a
 * problem
 */
public static DrumPattern makeDrumPattern(Polylist L)
  {
    DrumPattern dp = new DrumPattern();
    while( L.nonEmpty() )
      {
        Polylist item = (Polylist) L.first();
        L = L.rest();

        String dispatcher = (String) item.first();
        item = item.rest();
        
        switch( Leadsheet.lookup(dispatcher, keyword) )
          {
            case DRUM: // a single drum "rule" in the pattern
              {
                Long drum = new Long(0);
                Long volume = new Long(MAX_VOLUME);
                Polylist r = Polylist.nil;
                Polylist d = Polylist.nil;
                Polylist ruleAsList = Polylist.nil;
                
                while( item.nonEmpty() )
                  {
                    Object ob = item.first();
                    if( ob instanceof Long )
                      {
                        drum = (Long)ob;
                      }
                    else if( ob instanceof String )
                      {
                        String s = (String)ob;

                        int rule = Leadsheet.lookup(s.substring(0, 1), ruleTypes);

                        ruleAsList = ruleAsList.cons(s);  // save in list form for possible display

                        String dur = s.substring(1);
                        r = r.cons(rule);
                        d = d.cons(dur);
                      }
                    /*
                    else if( ob instanceof Polylist )
                      {
                        Polylist p = (Polylist)ob;
                        
                        if( p.first().equals(keyword[VOLUME]) )
                          {
                            Double f = 1.0;
                            if( p.second() instanceof Long )
                              {
                                f = ((Long) p.second()).doubleValue();
                              }
                            else if( p.second() instanceof Double )
                              {
                                f = (Double) p.second();
                              }
                            
                            if( f > 1.0 )
                              {
                                f = 1.0;
                              }
                            
                            f *= volume;
                            volume = f.longValue();
                          }
                       
                      }
                    */
                    
                    item = item.rest();
                  }

                dp.addRule(drum, r.reverse(), d.reverse(), volume, ruleAsList.reverse());
                break;
              }
                
            case WEIGHT: // weight of entire pattern
              {
                Number w = (Number) item.first();
                dp.setWeight(w.floatValue());
              }

          }
      }
    return dp;
  }


/**
 * Adds rules and durations for a drum to this DrumPattern. Note that there
 * should only be one rule for a given instrument in a pattern. Hence we will
 * check this and replace any previous rule with the new one
 *
 * @param drum a Long specifying the MIDI number for the drum
 * @param rules a Polylist of the rules for this drum
 * @param durations a Polylist of durations for this drum
 * @param volume a Long specifying the volume for the drum
 * @param ruleAsList the rule in Polylist form, for use in StyleEditorTableModel
 */

private void addRule(Long drum, Polylist rules, Polylist durations, Long volume, Polylist ruleAsList)
  {
    Polylist ruleToAdd = Polylist.list(drum, rules, durations, volume, ruleAsList);
    drums = addRule(drum, ruleToAdd, drums);
  }


private Polylist addRule(Long drum, Polylist ruleToAdd, Polylist drums)
  {
    //System.out.println("in addRule(), ruleToAdd = " + ruleToAdd);
    if( drums.isEmpty() )
      {
        return Polylist.list(ruleToAdd);
      }

    Polylist firstRule = (Polylist) drums.first();

    if( firstRule.first().equals(drum) )
      {
        return drums.rest().cons(ruleToAdd);
      }

    return addRule(drum, ruleToAdd, drums.rest()).cons(drums.first());
  }


public Polylist getDrums()
  {
    return drums;
  }


@Override
public int getDuration()
  {
    if( drums.isEmpty() )
      {
        return defaultDrumPatternDuration;
      }
    // here we're basing the duration of this DrumPattern on the 
    // the max duration across all drums

    Polylist L = drums;
    int maxDuration = 0;

    while( L.nonEmpty() )
      {
        Polylist pattern = (Polylist) drums.first();

        // drum.first() is the instrument number, not used here
        Polylist M = (Polylist) pattern.second();
        Polylist N = (Polylist) pattern.third();
        int duration = 0;

        while( M.nonEmpty() )
          {
            if( !M.first().equals(VOLUME) )
              {
              duration += Duration.getDuration((String) N.first());
              }
            M = M.rest();
            N = N.rest();
          }

        if( duration > maxDuration )
          {
            maxDuration = duration;
          }
        L = L.rest();
      }
    return maxDuration;
  }
  

/**
 * Realizes the drum patterns as a Polylist of MelodyPart objects to be
 * sequenced.
 *
 * @return a Polylist of MelodyPart objects
 */
    
public Polylist applyRules()
  {
    Polylist drumline = Polylist.nil;

    Polylist L = drums;
    //System.out.println("drums = " + drums);

    while( L.nonEmpty() )
      {
        MelodyPart m = new MelodyPart();
        Polylist drum = (Polylist) L.first();

        Long pitch = (Long) drum.first();
        Polylist rules = (Polylist) drum.second();
        Polylist durations = (Polylist) drum.third();

        int localVolume = 127;

        while( rules.nonEmpty() )
          {
            switch( (Integer) rules.first() )
              {
                case STRIKE:
                  {
                   int dur = Duration.getDuration((String) durations.first());
                   Note note = new Note(pitch.intValue(), dur);
                   note.setVolume(localVolume);
                   m.addNote(note);
                   
                   //System.out.println("drum " + pitch + " vol = " + localVolume + " dur = " + dur);
                   break;
                  }
                case REST:
                  {
                   int dur = Duration.getDuration((String) durations.first());
                   m.addNote(new Rest(dur));
                   break;
                  }
                    
                case VOLUME:
                  {
                   localVolume = Integer.parseInt((String)durations.first());
                   break;
                  }
              }
            
            rules = rules.rest();
            durations = durations.rest();
          }
        drumline = drumline.cons(m);

        L = L.rest();
      }

    return drumline;
  }

}
