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

import imp.brickdictionary.Block;

/**
 * SectionRecord records the information about a section. It was added when
 * StyleInfo was refactored July 27, 2011
 * @author keller
 */

public class SectionRecord
{
String styleName;
Style style = null;
int index;
boolean isPhrase;

SectionRecord(String styleName, int index, boolean isPhrase)
  {
    this.styleName = styleName;
    if( !styleName.equals(Style.USE_PREVIOUS_STYLE) )
      {
        style = Advisor.getStyle(styleName);
      }
    this.index = index;
    this.isPhrase = isPhrase;
  }


SectionRecord(SectionRecord orig)
  {
    this(orig.getStyleName(), orig.getIndex(), orig.getIsPhrase());
  }

public boolean getIsPhrase()
  {
    return isPhrase;
  }

public boolean getUsePreviousStyle()
  {
    return styleName.equals(Style.USE_PREVIOUS_STYLE);
  }

public void setIsPhrase(boolean isPhrase)
  {
    this.isPhrase = isPhrase;
  }

public void setUsePreviousStyle()
  {
  styleName = Style.USE_PREVIOUS_STYLE;
  style = null;
  }

public Style getStyle()
  {
    return style;
  }

public String getStyleName()
  {
    return styleName;
  }

public void setStyle(Style style)
  {
    this.style = style;
  }

public int getSectionMeasure(ChordPart chords) 
  {
  int measureLength = chords.getMeasureLength();
  return index / measureLength + 1;
    }

public int getIndex()
  {
    return index;
  }

public void setIndex(int index)
  {
    this.index = index;
  }

public boolean hasIndex(int index)
  {
    return index == this.index;
  }

public int getSectionType()
  {
    return isPhrase? Block.PHRASE_END : Block.SECTION_END;
  }

@Override
public String toString()
  {
    return "(Section " + styleName + " " + index + " " + isPhrase + ")";
  }

}
