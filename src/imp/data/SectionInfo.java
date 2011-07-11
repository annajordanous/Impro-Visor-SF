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

import imp.*;
import java.io.Serializable;
import java.util.*;
import javax.sound.midi.*;

public class SectionInfo implements Constants, Serializable {
    private ChordPart chords;
    
    private Vector<Style> styles = new Vector<Style>();
    private Vector<Integer> styleIndices = new Vector<Integer>();

    public SectionInfo(ChordPart chords) {
        this.chords = chords;
        Style style = new Style();

        // RK 1/4/2010 The following was causing problems with countin resetting
        // the chord instrument, as reported by a user. It is not clear
        // why this was needed, but it seems to be causing an undesirable
        // instrument change through an indirect path.
        // It should be revisited.

        //style.setChordInstrument(chords.getInstrument(), "SectionInfo");

        addSection(style, 0);
    }

    public SectionInfo copy() {
        SectionInfo si = new SectionInfo(chords);
        si.styles = new Vector<Style>();
        si.styleIndices = new Vector<Integer>();
        Iterator<Style> i = styles.iterator();
        Iterator<Integer> j = styleIndices.iterator();
        while(i.hasNext()) {
            si.styles.add(i.next());
            si.styleIndices.add(j.next());
        }
        return si;
    }
    
    public boolean addSection(String name, int n) {
        Style s = Advisor.getStyle(name);
        if(s == null)
            return false;
        else {
            addSection(s,n);
            return true;
        }
    }
    
    public void addSection(Style s, int n) {
        ListIterator<Style> i = styles.listIterator();
        ListIterator<Integer> j = styleIndices.listIterator();
        while(i.hasNext()) {
            Style style = i.next();
            int index = j.next();
            if(index == n) {
                i.remove();
                j.remove();
                break;
            }
            else if(index > n) {
                i.previous();
                j.previous();
                break;
            }
                
        }
        i.add(s);
        j.add(n);
    }
    
    public void reloadStyles() {
        ListIterator<Style> i = styles.listIterator();
        while(i.hasNext()) {
            Style s = i.next();
            i.remove();
            i.add(Advisor.getStyle(s.getName()));
        }
    }
    
    public void newSection(int index) {
        int measureLength = chords.getMeasureLength();
        
        int startIndex = styleIndices.get(index);
        int endIndex = chords.size();
        if(index + 1 < size())
            endIndex = styleIndices.get(index+1);
        
        int measure = (endIndex - startIndex) / measureLength;
        
        if(measure%2 == 0)
            measure /= 2;
        else
            measure = measure/2 + 1;
        
        addSection(styles.get(index),startIndex + measure*measureLength);
    }

    public Integer getPrevSectionIndex(int n) {
        ListIterator<Integer> j = styleIndices.listIterator();
        while(j.hasNext()) {
            int index = j.next();
            if(index > n) {
                j.previous();
                index = j.previous();
                if(index == n && j.hasPrevious())
                    return j.previous();
                else if(index == n)
                    return -1;
                return index;
            }
        }
        return -1;
    }
    
    public Integer getNextSectionIndex(int n) {
        ListIterator<Integer> j = styleIndices.listIterator();
        while(j.hasNext()) {
            int index = j.next();
            if(index > n)
                return index;
        }
        return null;
    }

    public boolean sectionAtSlot(int n) {
        Iterator<Integer> j = styleIndices.listIterator();
        while(j.hasNext())
            if(j.next() == n)
                return true;
        return false;
    }
    
    public Style getStyleFromSlots(int n) {
        ListIterator<Style> i = styles.listIterator();
        ListIterator<Integer> j = styleIndices.listIterator();
        Style s = null;
        while(i.hasNext()) {
            s = i.next();
            int index = j.next();
            if(index == n)
                break;
            else if(index > n) {
                i.previous();
                s = i.previous();
                break;
            }
        }
        return s;
    }

    public SectionInfo extract(int first, int last, ChordPart chords) {
        SectionInfo si = new SectionInfo(chords);

        si.styles = new Vector<Style>();
        si.styleIndices = new Vector<Integer>();
        Iterator<Style> i = styles.iterator();
        Iterator<Integer> j = styleIndices.iterator();
        while(i.hasNext()) {
            Style s = i.next();
            int index = j.next() - first;
            if(index < 0)
                si.addSection(s,0);
            else if(index <= last - first)
                si.addSection(s,index);
        }

        return si;
    }

    public Style getStyle(int n) {
        return styles.get(n);
    }

    public int getStyleIndex(int n) {
        return styleIndices.get(n);
    }

    public int size() {
        return styles.size();
    }

    public String getInfo(int index) {
        Style s = styles.get(index);
        int startIndex = getSectionMeasure(index);
        int endIndex = measures();
        if(index + 1 < size())
            endIndex = getSectionMeasure(index+1) - 1;
        
        String info = "mm. " + startIndex + "-" + endIndex + ": " + s;
        if(startIndex == endIndex)
            info = "m. " + startIndex + ": " + s;

        return info;
    }
    
    public int getSectionMeasure(int index) {
        return slotIndexToMeasure(styleIndices.get(index));
    }
    
    public int slotIndexToMeasure(int index) {
        int measureLength = chords.getMeasureLength();
        return index / measureLength + 1;
    }
    
    public int measureToSlotIndex(int measure) {
        int measureLength = chords.getMeasureLength();
        return (measure - 1)*measureLength;
    }
    
    public int measures() {
        int measureLength = chords.getMeasureLength();
        return chords.size()/measureLength;
    }
    
    public void moveSection(int index, int newMeasure) {
        if(getSectionMeasure(index) == newMeasure)
            return;
        
        Style s = styles.get(index);
        deleteSection(index);
        addSection(s,measureToSlotIndex(newMeasure));
    }
  
    public void deleteSection(int index) {
        if(size() <= 1)
            return;
        ListIterator<Style> i = styles.listIterator(index);
        ListIterator<Integer> j = styleIndices.listIterator(index);
        i.next();
        j.next();
        i.remove();
        j.remove();
        if(index == 0) {
            j = styleIndices.listIterator(0);
            j.next();
            j.remove();
            j.add(0);
        }
    }
    
    public void setSize(int size) {
        Iterator<Style> i = styles.iterator();
        Iterator<Integer> j = styleIndices.iterator();
        while(i.hasNext()) {
            i.next();
            int n = j.next();
            if(n >= size) {
                i.remove();
                j.remove();
            }
        }
    }

    public boolean setStyle(String name) {
        Style s = Advisor.getStyle(name);
        if(s == null)
            return false;
        else {
            setStyle(s);
            return true;
        }
    }
    
    public void setStyle(Style s) {
        styles = new Vector<Style>();
        styleIndices = new Vector<Integer>();
        addSection(s,0);
    }

    public Style getStyle() {
        return styles.firstElement();
    }

public long sequence(Sequence seq, int ch, long time, Track track,
                     int transposition, boolean useDrums, int endLimitIndex)
    throws InvalidMidiDataException
  {
    // to trace sequencing info:
    // System.out.println("SectionInfo time = "
    // + time + " endLimitIndex = " + endLimitIndex);

    // Iterate over list of sections, each a Style

    ListIterator<Style> i = styles.listIterator();
    ListIterator<Integer> j = styleIndices.listIterator();
    int startIndex = 0;
    int endIndex = 0;
    if( j.hasNext() )
      {
        endIndex = j.next();
      }

    while( i.hasNext() && (endLimitIndex == ENDSCORE || endIndex <= endLimitIndex) )
      {
        Style s = i.next();
        startIndex = endIndex;
        endIndex = chords.size();
        if( j.hasNext() )
          {
            endIndex = j.next();
          }

        if( s != null )
          {
            time = s.sequence(seq, time, track, chords, startIndex, endIndex, transposition, useDrums, endLimitIndex);
           }
       }
    return time;
  }

/**
 * Determine whether a given index corresponds to the start of a Section.
 * This is done by iterating through styleIndices, accumulating slot counts,
 * until either the given index coincides with the start of a slot or
 * the accumulated count exceeds the index.
 * @param index
 * @return 
 */
public boolean isSectionStart(int index)
{
    int accumulatedSlots = 0;
    ListIterator<Integer> j = styleIndices.listIterator();
    while( j.hasNext() && index >= accumulatedSlots )
    {
        if( index == accumulatedSlots )
        {
            return true;
        }
        accumulatedSlots += j.next();
    }
    return false;
  
}

public Vector<Integer> getSectionStartIndices()
{
    return styleIndices;
}

public String toString()
  {
    ListIterator<Style> i = styles.listIterator();
    ListIterator<Integer> j = styleIndices.listIterator();
    StringBuilder buffer = new StringBuilder();
    while( i.hasNext() )
    {
        buffer.append("(");
        buffer.append(i.next().toString());
        buffer.append(" ");
        buffer.append(j.next().toString());
        buffer.append(") ");
    }
    return buffer.toString();
  }
}
