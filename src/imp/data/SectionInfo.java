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
import imp.brickdictionary.Block;
import imp.brickdictionary.ChordBlock;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Vector;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;

/**
 * SectionInfo was originally done by Stephen Jones when sections were
 * first added to the software. On July 27, 2011, Robert Keller refactored
 * the code, by transcribing the separate Vectors into a single Vector
 * of SectionRecord. The purpose was to enable phrases, and possibly other
 * information to be added more easily.
 * @author keller
 */

public class SectionInfo implements Constants, Serializable {
    private ChordPart chords;
    
    private ArrayList<SectionRecord> records = new ArrayList<SectionRecord>();

    public SectionInfo(ChordPart chords) {
        this.chords = chords;
        //Style style = new Style();

        // RK 1/4/2010 The following was causing problems with countin resetting
        // the chord instrument, as reported by a user. It is not clear
        // why this was needed, but it seems to be causing an undesirable
        // instrument change through an indirect path.
        // It should be revisited.

        //style.setChordInstrument(chords.getInstrument(), "SectionInfo");

        addSection(Style.USE_PREVIOUS_STYLE, 0, false);
    }

    public SectionInfo copy() {
        SectionInfo si = new SectionInfo(chords);
        si.records = new ArrayList<SectionRecord>();
        
        for(SectionRecord record: records )
          {
            si.records.add(new SectionRecord(record));
          }

        return si;
    }
    
//    public boolean addSection(String name, int n, boolean isPhrase) {
//        Style s = Advisor.getStyle(name);
//        if(s == null)
//            return false;
//        else {
//            addSection(s,n,isPhrase);
//            return true;
//        }
//    }
    
    public void addSection(String styleName, int n, boolean isPhrase) {
        ListIterator<SectionRecord> k = records.listIterator();
        
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            int index = record.getIndex();
            if( index == n )
              {
                k.remove();
                break;
              }
            else if( index > n )
              {
                k.previous();
                break;
              }
          }
        k.add(new SectionRecord(styleName, n, isPhrase));
    }
    
    public void reloadStyles() {
        ListIterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            k.remove();
            k.add(new SectionRecord(record.getStyleName(), 
                                    record.getIndex(), 
                                    record.getIsPhrase()));
          }
    }
    
    public void newSection(int index) {
        int measureLength = chords.getMeasureLength();
        
        SectionRecord record = records.get(index);
        int startIndex = record.getIndex();
        
        int endIndex = chords.size();
        
        if(index + 1 < size())
          {
          SectionRecord nextRecord = records.get(index+1);
          endIndex = nextRecord.getIndex();
          }
        
        int measure = (endIndex - startIndex) / measureLength;
        
        if(measure%2 == 0)
            measure /= 2;
        else
            measure = measure/2 + 1;
        
        addSection(record.getStyleName(), 
                   startIndex + measure*measureLength,
                   record.getIsPhrase());
    }

    public Integer getPrevSectionIndex(int n) {
        ListIterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            int index = record.getIndex();
            if( index > n )
              {
                k.previous();
                index = k.previous().getIndex();
                if( index == n && k.hasPrevious() )
                  {
                    return k.previousIndex();
                  }
                else if( index == n )
                  {
                    return -1;
                  }
                return index;
              }
          }

        return -1;
    }
    
    public Integer getNextSectionIndex(int n) {
        ListIterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            int index = k.next().getIndex();
            if( index > n )
              {
                return index;
              }
          }
        return null;
    }

    public int sectionAtSlot(int n) {
       Iterator<SectionRecord> k = records.listIterator();
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            if( record.getIndex() == n )
              {
                if( record.getIsPhrase() )
                  {
                    return Block.PHRASE_END;
                  }
                else
                  {
                    return Block.SECTION_END;
                  }
              }
          }
        return Block.NO_END;
    }
    
public Style getStyleFromSlots(int n)
  {

    return getSectionRecord(n).getStyle();

  }

public SectionRecord getSectionRecord(int n)
  {
//    ListIterator<SectionRecord> k = records.listIterator();
//    SectionRecord s = k.next();
//    SectionRecord previous = s;
//    while( s.getIndex() <= n && k.hasNext() )
//      {
//        previous = s;
//        s = k.next();
//      }
//
//    s = s.getIndex() <= n ? s : previous;
//
//    //System.out.println("n = " + n + " using s = " + s);
//    return s;
//    
   return records.get(n);
  }

    public SectionInfo extract(int first, int last, ChordPart chords) {
        SectionInfo si = new SectionInfo(chords);
        
        si.records = new ArrayList<SectionRecord>();
        
        Iterator<SectionRecord> k = records.iterator();
        
        while( k.hasNext() )
          {
            SectionRecord record = k.next();
            String styleName = record.getStyleName();
            int index = record.getIndex() - first;
            if( index < 0 )
              {
                si.records.add(new SectionRecord(styleName, 0, record.getIsPhrase()));
              }
            else if( index <= last - first )
              {
                si.records.add(new SectionRecord(styleName, index, record.getIsPhrase()));
              }
          }

        return si;
    }

    public Style getStyle(int n) {
        if( records == null )
          {
            return null;
          }
        return records.get(n).getStyle();
    }

    public String getStyleName(int n) {
        if( records == null )
          {
            return null;
          }
        return records.get(n).getStyleName();
    }
        
    public int getStyleIndex(int n) {
        return records.get(n).getIndex();
    }
    
    public boolean getIsPhrase(int n) {
        return records.get(n).getIsPhrase();
    }    
    
    public void setIsPhrase(int n, boolean value)
      {
        records.get(n).setIsPhrase(value);
      }
    
    public int size() {
        return records.size();
    }
    
    public boolean hasOneSection()
      {
        return records.size() == 1;
      }

    public String getInfo(int index) {
        SectionRecord record = records.get(index);
        
        String styleName = record.getStyleName();
        int startIndex = getSectionMeasure(index);
        int endIndex = measures();
        if(index + 1 < size())
            endIndex = getSectionMeasure(index+1) - 1;
        
        String info = "mm. " + startIndex + "-" + endIndex + ": " + styleName;
        if(startIndex == endIndex)
            info = "m. " + startIndex + ": " + styleName;

        return info;
    }
    
    public int getSectionMeasure(int index) {
        return slotIndexToMeasure(records.get(index).getIndex());
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
    
    public void adjustSection(int index, int newMeasure, boolean isPhrase, boolean usePreviousStyleChecked) {
         //System.out.println("1 records = " + records);
         
        // Do not move first record
        // Its phrase value can be set in place
        
        
         SectionRecord record = records.get(index);

         if(getSectionMeasure(index) == newMeasure)
          {
            record.setIsPhrase(isPhrase);
            if( usePreviousStyleChecked )
              {
                record.setUsePreviousStyle();
              }
            return;
          }

        String styleName = usePreviousStyleChecked ? Style.USE_PREVIOUS_STYLE : record.getStyleName();
        deleteSection(index);
        addSection(styleName, measureToSlotIndex(newMeasure), isPhrase);
    }
  
    // Not sure about this:
    
    public void deleteSection(int index) {
        if(size() <= 1)
            return;
        
        ListIterator<SectionRecord> k = records.listIterator(index);
        SectionRecord record = k.next();
        k.remove();
        
        String styleName = record.getStyleName();
        
        if( index == 0 )
          {
            k = records.listIterator(0);
            k.next();
            k.remove();
            k.add(new SectionRecord(styleName, 0, false));
          }
        
    }
    
    public void setSize(int size) {
        
        Iterator<SectionRecord> k = records.iterator();
        
        while(k.hasNext()) {
            SectionRecord record = k.next();
            int n = record.getIndex();
            if( n >= size ) {
                k.remove();
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
        records = new ArrayList<SectionRecord>();
        addSection(s.getName(),0, false);
    }

    public Style getStyle() {
        return getStyle(0);
    }

public long sequence(Sequence seq, int ch, long time, Track track,
                     int transposition, boolean useDrums, int endLimitIndex)
    throws InvalidMidiDataException
  {
    // to trace sequencing info:
    //System.out.println("Sequencing SectionInfo time = "
    // + time + " endLimitIndex = " + endLimitIndex + " useDrums = " + useDrums);

    // Iterate over list of sections, each a Style

    int chordsSize = chords.size();
    
    int endIndex = chordsSize;
    
    // m is a second iterator intended to stay one step ahead of k
    // so as to get the start of the next section
    
    Style mostRecentStyle = new Style();
    
    ListIterator<SectionRecord> k = records.listIterator();
    ListIterator<SectionRecord> m = records.listIterator();
    if( m.hasNext() )
      {
        m.next();
      }

    while( k.hasNext() ) //&& (endLimitIndex == ENDSCORE || endIndex <= endLimitIndex) )
      {
        SectionRecord record = k.next();
        Style style = record.getStyle();
        if( style == null )
          {
            style = mostRecentStyle;
          }
        mostRecentStyle = style;
        
        int startIndex = record.getIndex();
        
        endIndex = m.hasNext() ? m.next().getIndex() : chordsSize;
        
        if( style != null )
          {
          time = style.sequence(seq, time, track, chords, startIndex, endIndex, transposition, useDrums, endLimitIndex);
          }
       }
    return time;
  }

public ArrayList<imp.brickdictionary.Block> toBlockList()
{
    ArrayList<imp.brickdictionary.Block> blocks = new ArrayList();
    int chordsSize = chords.size();
    
    int endIndex = chordsSize;
    
    // m is a second iterator intended to stay one step ahead of k
    // so as to get the start of the next section
    
    ListIterator<SectionRecord> k = records.listIterator();
    ListIterator<SectionRecord> m = records.listIterator();
    if( m.hasNext() )
      {
        m.next();
      }

    while( k.hasNext() ) //&& (endLimitIndex == ENDSCORE || endIndex <= endLimitIndex) )
      {
        SectionRecord record = k.next();
        Style style = record.getStyle();
        int startIndex = record.getIndex();
        
        endIndex = m.hasNext() ? m.next().getIndex() : chordsSize;
        
        ChordBlock block = null;
        
        for( int slot = startIndex; slot < endIndex; slot++ )
          {
          Chord chord = chords.getChord(slot);
          
          if( chord != null )
            {
              block = new ChordBlock(chord.getName(), chord.getRhythmValue());
              blocks.add(block);
            }
          }
        
        // For last block in section
        if( block != null )
            {
            block.setSectionEnd(record.getIsPhrase()? Block.PHRASE_END : Block.SECTION_END);
            }
       }
    
    return blocks;
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

    ListIterator<SectionRecord> k = records.listIterator();
    while( k.hasNext() && index >= accumulatedSlots )
    {
        if( index == accumulatedSlots )
        {
            return true;
        }
        accumulatedSlots += k.next().getIndex();
    }
    return false;
  
}

public Vector<Integer> getSectionStartIndices()
{
  Vector<Integer> result = new Vector<Integer>();
  for( SectionRecord record: records )
    {
      result.add(record.getIndex());
    }
    return result;
}

public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    for( SectionRecord record: records )
    {
        buffer.append("(");
        buffer.append(record.getStyleName());
        buffer.append(" ");
        buffer.append(record.getIndex());
        buffer.append(" ");
        buffer.append(record.getIsPhrase());
        buffer.append(") ");
    }
    return buffer.toString();
  }

public Iterator<SectionRecord> iterator()
  {
    return records.iterator();
  }
}
