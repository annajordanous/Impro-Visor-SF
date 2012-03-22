/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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
package imp.data;

import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author keller
 */

public class DrumRuleRep
{
public class Element
{
char elementType;
String suffix;

Element(char elementType, String suffix)
  {
    this.elementType = elementType;
    this.suffix = suffix;
  }

public char getType()
  {
    return elementType;
  }

public String getSuffix()
  {
    return suffix;
  }
}

private int drumNumber;

private ArrayList<Element> elements;

private Polylist ruleAsList;

/**
 * Construct a DrumRule Representation from an S-expression
 * @param raw 
 */

public DrumRuleRep(Polylist raw)
  {
    assert raw.nonEmpty();

    Object first = raw.first();
    
    if( first instanceof Long )
      {
        drumNumber = ((Long) first).intValue();
      }
    else if( first instanceof String )
      {
        drumNumber = MIDIBeast.numberFromSpacelessDrumName((String)first);
      }
    else
      {
        assert false;
      }

    raw = raw.rest();

    ruleAsList = raw;
    
    elements = new ArrayList<Element>();

    while( raw.nonEmpty() )
      {
        Object ob = raw.first();
        if( ob instanceof String )
          {
            String s = (String) ob;

            char type = s.charAt(0);
            
            assert type == DrumPattern.DRUM_STRIKE 
                || type == DrumPattern.DRUM_REST 
                || type == DrumPattern.DRUM_VOLUME;

            String suffix = s.substring(1);
            
            elements.add(new Element(type, suffix));
          }

        raw = raw.rest();
      }
  }


public static DrumRuleRep makeDrumRuleRep(String string)
  {
    Polylist L = (Polylist)(Polylist.PolylistFromString(string).first());
    //System.out.println("string = " + string + "\nlist = " + L);
    return new DrumRuleRep(L.rest());
  }

public int getInstrument()
  {
    return drumNumber;
  }

public ArrayList<Element> getElements()
  {
    return elements;
  }

/**
 * Get the duration, in slots
 * @return 
 */
public int getDuration()
  {
    int duration = 0;
    
    for( Element element: elements )
      {
        switch( element.getType() )
          {
            case DrumPattern.DRUM_STRIKE :
                // fall through
            case DrumPattern.DRUM_REST:
                duration += Duration.getDuration(element.getSuffix());
                break;
                
            case DrumPattern.DRUM_VOLUME:
                // ignore volume
                break;
          }
      }
    
    return duration;
  }

@Override
public String toString()
  {
    return ruleAsList.cons(drumNumber).toString();
  }
}
