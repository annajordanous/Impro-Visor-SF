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
import java.util.ArrayList;
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
 * a Polylist of drum Polylists, the structure of each drum Polylist is: (DRUM
 * RULES DURATIONS)
 */

private ArrayList<DrumRuleRep> drums;


/**
 * array containing the types of rules
 */

public static final String ruleTypes[] =
  {
    "X", "R", "V"
  };


// indices into the ruleTypes array
public static final int STRIKE = 0;
public static final int REST = 1;
public static final int VOLUME = 2;


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
    drums = new ArrayList<DrumRuleRep>();
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
                dp.addRule(new DrumRuleRep(item));
                break;
              }
                
            case WEIGHT: // weight of entire pattern
              {
                Number w = (Number) item.first();
                dp.setWeight(w.floatValue());
                break;
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

private void addRule(DrumRuleRep rep)
  {
    for( DrumRuleRep existing : drums )
      {
        if( existing.getInstrument() == rep.getInstrument() )
          {
            drums.remove(existing);
            break;
          }
      }
    drums.add(rep);
  }


public ArrayList<DrumRuleRep> getDrums()
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

    int maxDuration = 0;

    for( DrumRuleRep rep: drums )
      {
        // drum.first() is the instrument number, not used here

        int duration = rep.getDuration();

        if( duration > maxDuration )
          {
            maxDuration = duration;
          }
      }
    
    return maxDuration;
  }
  

/**
 * Renders this drum pattern as a Polylist of MelodyPart objects to be
 * sequenced.
 *
 * @return a Polylist of MelodyPart objects
 */
    
public Polylist applyRules()
  {
    Polylist drumline = Polylist.nil;

    for( DrumRuleRep rep: drums )
      {
        MelodyPart m = new MelodyPart();
        int pitch = rep.getInstrument();
        
        int localVolume = 127;

        for( DrumRuleRep.Element element: rep.getElements() )
          {
            switch( element.getType() )
              {
                case 'X':
                  {
                   int dur = Duration.getDuration(element.getSuffix());
                   Note note = new Note(pitch, dur);
                   note.setVolume(localVolume);
                   m.addNote(note);
                   
                   //System.out.println("drum " + pitch + " vol = " + localVolume + " dur = " + dur);
                   break;
                  }
                case 'R':
                  {
                   int dur = Duration.getDuration(element.getSuffix());
                   m.addNote(new Rest(dur));
                   break;
                  }
                    
                case 'V':
                  {
                   localVolume = Integer.parseInt(element.getSuffix());
                   break;
                  }
              }
            
          }
        drumline = drumline.cons(m);
      }

    return drumline;
  }

}
