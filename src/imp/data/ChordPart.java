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

import java.io.*;
import javax.sound.midi.*;
import java.util.ArrayList;

import imp.brickdictionary.ChordBlock;
import imp.roadmap.RoadMapFrame;
import imp.util.*;
import java.util.Iterator;
import polya.*;

/**
 * An extension of the Part class that contains only Chord objects.
 * This is useful to separate simple chord drawing (names only,) from
 * the more complex melody drawing.
 * @see         Chord
 * @see         Part
 * @author      Stephen Jones
*/
public class ChordPart extends Part implements Serializable{

    private SectionInfo sectionInfo = new SectionInfo(this);

    /**
     * the default chord volume
     */
    public static final int DEFAULT_CHORD_VOLUME = DEFAULT_VOLUME - 20;

    /**
     * Creates an empty ChordPart.
     */
    public ChordPart() {
        super();
        volume = DEFAULT_CHORD_VOLUME;
    }

    /**
     * Creates a ChordPart with the given size.
     * @param size      the number of slots in the ChordPart
     */
    public ChordPart(int size) {
        super(size);
        Trace.log(3, "creating new chord part of size " + size);
        if(size != 0)
            slots.set(0, new Chord(size));
        volume = DEFAULT_CHORD_VOLUME;
    }
    
    /**
     * Sets the given chord at the specified slot index.
     * @param slotIndex         the index to put the chord at
     * @param chord             the Chord to put at the index
     */
    public void setChord(int slotIndex, Chord chord) {
        setUnit(slotIndex, chord);
    }
    
  @Override
    public void setSize(int size) {
        super.setSize(size);
        sectionInfo.setSize(size);
    }
    
  @Override
    public int getMeasureLength() {
        int beatVal = WHOLE/metre[1];
        int beatsPerBar = metre[0];
        return beatsPerBar * beatVal;
    }
    
    /**
     * Adds a chord to the end of the ChordPart.
     * @param chord     the Chord to add
     */

    public void addChord(String symbol, int duration) {
        Chord chord = Chord.makeChord(symbol, duration);
        if( chord != null )
          {
          addUnit(chord);
          return;
          }

	Polylist exploded = Key.explodeChord(symbol);
	if( exploded != null )
	  {
	  String bassNote = 
	      PitchClass.upperCaseNote((String)exploded.fourth());

	  Chord bassChord = Chord.makeChord(bassNote);
	  if( bassChord != null )
	    {
	    ErrorLog.log(ErrorLog.WARNING,
			 "Chord symbol " + symbol
			 + " is unknown, using the bass note: " 
			 + bassNote);
	    addUnit(bassChord);
	    return;
	    }

	  ErrorLog.log(ErrorLog.WARNING,
		       "Chord symbol and bass " + symbol
		       + " are unknown, using " + NOCHORD);

	  addUnit(new Chord(NOCHORD, duration));
          }
	}


    /**
     * Adds a chord to the end of the ChordPart.
     * @param chord     the Chord to add
     */
    public void addChord(Chord chord) {
        addUnit(chord);
    }

    /**
     * Returns the Chord at the specified index.
     * @param slotIndex         the index of the chord to get
     * @return Chord            the chord at the index
     */
    public Chord getChord(int slotIndex) {
        if( slotIndex < 0 || slotIndex >= size )
          return null;
        return (Chord)getUnit(slotIndex);
    }

    /**
     * Returns the Chord sounding at this index.
     * @param slotIndex         the index to check at
     * @return Chord            the chord sounding at the index
     */
    public Chord getCurrentChord(int slotIndex) {
        if( slotIndex < 0 || slotIndex >= size )
          {
          return null;
          }
        return getChord(getCurrentChordIndex(slotIndex));
    }

    /**
     * Returns the index of the Chord sounding at this index.
     * @param slotIndex         the index to check at
     * @return int              the index of the sounding chord
     */
    public int getCurrentChordIndex(int slotIndex) {
        if(getChord(slotIndex) != null)
            return slotIndex;
        else
            return getPrevIndex(slotIndex);
    }
   
    public Chord getNextUniqueChord(int slotIndex) {
        int nextUniqueChordIndex = getNextUniqueChordIndex(slotIndex);
        if( nextUniqueChordIndex < 0 )
          return null;
        return getChord(nextUniqueChordIndex);
    }
    
    /**
     * Returns index of next unique chord, or -1 if none.
     */

    public int getNextUniqueChordIndex(int slotIndex) {
        if( slotIndex < 0 || slotIndex >= size )
          return -1;
        int currentChordIndex = getCurrentChordIndex(slotIndex);
        Chord currentChord = getChord(currentChordIndex);
        Chord nextChord = currentChord;
        int nextChordIndex = currentChordIndex;
        
        while(nextChord.getName().equals(currentChord.getName())) {
            nextChordIndex = getNextIndex(nextChordIndex);
            if(nextChordIndex >= size)
                return -1;
            nextChord = getChord(nextChordIndex);
        }
    return nextChordIndex;
    }
    
    public int getPrevUniqueChordIndex(int slotIndex)
    {
        if(slotIndex < 0 || slotIndex >= size)
            return -1;
        int currentChordIndex = getCurrentChordIndex(slotIndex);
        Chord currentChord = getChord(currentChordIndex);
        Chord prevChord = currentChord;
        int prevChordIndex = currentChordIndex;
        
        while(prevChord.getName().equals(currentChord.getName()))
        {
            prevChordIndex = getPrevIndex(prevChordIndex);
            if(prevChordIndex<= -1)
            {
                return -1;
            }
            prevChord = getChord(prevChordIndex);
        }
        return prevChordIndex;
    }
    
    /**
     * Returns the Chord after the indicated slot index.
     * @param slotIndex         the index to start searching at
     * @return Chord            the Chord after the specified index
     */
    public Chord getNextChord(int slotIndex) {
        return (Chord)getNextUnit(slotIndex);
    }
    
    /**
     * Returns the Chord after the indicated slot index.
     * @param slotIndex         the index to start searching at
     * @return Chord            the Chord after the specified index
     */
    public int getNextChordIndex(int slotIndex) {
        return getNextUniqueChordIndex(slotIndex);
    }

    /**
     * Returns the Chord before the indicated slot index.
     * @param slotIndex         the index to start searching at
     * @return Chord            the Chord before the specified index
     */
    public Chord getPrevChord(int slotIndex) {
        return (Chord)getPrevUnit(slotIndex);
    }
    
    /**
     * Returns an exact copy of this Part
     * @return Part   copy
     */
    public ChordPart copy() {
        ChordPart newPart = new ChordPart(size);
        PartIterator i = iterator();
        while(i.hasNext())
            newPart.slots.set(i.nextIndex(), i.next().copy());

        newPart.sectionInfo = sectionInfo.copy();
        newPart.unitCount = unitCount;
        newPart.title = title;
        newPart.composer = composer;
        newPart.volume = volume;
        newPart.setInstrument(instrument);
        newPart.keySig = keySig;
        newPart.setMetre(metre[0], metre[1]);
        newPart.swing = swing;
              
        return newPart;
    }

    public void setChordInstrument(int instrument)
    {
    //System.out.println("chordPart setChordInstrument to " + instrument);
        super.setInstrument(instrument);
    }

    public boolean setStyle(String name) {
        return sectionInfo.setStyle(name);
    }
    
    public void setStyle(Style s) {
        sectionInfo.setStyle(s);
    }

    public void addSection(Style s, int n) {
        sectionInfo.addSection(s,n);
    }
    
    public Style getStyle() {
        return sectionInfo.getStyle();
    }

    public SectionInfo getSectionInfo() {
        return sectionInfo;
    }

    public void setSectionInfo(SectionInfo si) {
        sectionInfo = si;
    }

    public long sequence(Sequence seq, 
                         int ch, 
                         long time, 
                         Track track, 
                         int transposition, 
                         boolean useDrums, 
                         int endLimitIndex)
                 throws InvalidMidiDataException 
     {
    // to trace sequencing info:
    // System.out.println("ChordPart time = " + time + ", endLimitIndex = " + endLimitIndex);
      
        return sectionInfo.sequence(seq,ch,time,track, transposition, useDrums, endLimitIndex);
    }


    /**
     * Returns a ChordPart that contains the Units within the slot range specified.
     * @param first     the first slot in the range
     * @param last      the last slot in the range
     * @return Part     the Part that contains the extracted chunk
     */
    public ChordPart extract(int first, int last) {
        ChordPart newPart = new ChordPart();

        // If there is no chord to start, search backward and use the previous chord.
        // Since there is always a chord (possibly NC) in the first slot, we are guaranteed
        // to get one.
        
        if( getUnit(first) != null )
          {
          newPart.addUnit(getUnit(first).copy());
          }
        else
          {
          for( int j = first-1; j >= 0; j-- )
            {
            if( getUnit(j) != null )
              {
              // chop of the beginning of this chord's duration that
              // isn't selected
              Unit unit = getUnit(j).copy();
              unit.setRhythmValue(getUnitRhythmValue(first));
              newPart.addUnit(unit);
              break;
              }
            }
          }

        // Complete with the remainder of the chords.

        for(int i = first+1; i <= last; i++)
            if(getUnit(i) != null)
                newPart.addUnit(getUnit(i).copy());

        // We don't want the accompaniment to play past the end
        if(newPart.size() > last - first + 1)
            newPart.setSize(last - first + 1);
        
        newPart.setSectionInfo(sectionInfo.extract(first,last,newPart));
        
        return newPart;
    }

    /**
     * Returns a copy of this ChordPart transposed by rise and targeting key
     * @return ChordPart   copy
     */
    public ChordPart transpose(int rise, Key key) {
        ChordPart newPart = new ChordPart(size);
        PartIterator i = iterator();
        while(i.hasNext()) {
            Chord oldChord = (Chord)i.next();
            Chord newChord = oldChord.copy();
            newChord.setName(Key.transposeChord(oldChord.getName(), rise, key));
        }

        newPart.unitCount = unitCount;
        newPart.title = title;
        newPart.composer = composer;
        newPart.volume = volume;
        newPart.setInstrument(instrument);
        newPart.keySig = keySig;
        newPart.setMetre(metre[0], metre[1]);
        newPart.swing = swing;
        newPart.sectionInfo = sectionInfo.copy();

        return newPart;
    }
 
   
    /**
     * Reads the passed BufferedReader and creates a new ChordPart.
     * @param in                the BufferedReader to read from
     * @return ChordPart        the ChordPart read from the BufferedReader
     */
    public static ChordPart open(BufferedReader in) throws IOException {
    	ChordPart chords = new ChordPart();

        chords.title = in.readLine();
        chords.setInstrument(Integer.decode(in.readLine()));
        chords.volume = Integer.decode(in.readLine());
        chords.keySig = Integer.decode(in.readLine());

        int metreTop = Integer.decode(in.readLine());
        int metreBottom = Integer.decode(in.readLine());
        chords.setMetre(metreTop, metreBottom);

        chords.swing = Double.parseDouble(in.readLine());
        
    	Chord chord = Chord.open(in);
    	while(chord != null) {
    		chords.addChord(chord);
    		chord = Chord.open(in);
    	}
    	
    	if(chords.unitCount == 0)
    		return null;
    		
    	return chords;
    }
    
/**
 * Get the ChordSymbols of this ChordPart as an ArrayList<ChordSymbol>
 * @return 
 */
    
public ArrayList<ChordSymbol> getChordSymbols()
  {
    ArrayList<ChordSymbol> result = new ArrayList<ChordSymbol>();

    PartIterator i = iterator();
    while( i.hasNext() )
      {
        Chord chord = (Chord) i.next();
        result.add(chord.getChordSymbol());
      }
    return result;
  }


/**
 * Get the durations of chords of this ChordPart as an ArrayList<ChordSymbol>
 * @return 
 */

public ArrayList<Integer> getChordDurations()
  {
    ArrayList<Integer> result = new ArrayList<Integer>();

    PartIterator i = iterator();
    while( i.hasNext() )
      {
        Chord chord = (Chord) i.next();
        result.add(chord.getRhythmValue());
      }
    return result;
  }


/**
 * Get the section starts corresponding to chord symbols,
 * as an ArrayList of Boolean.
 * @return 
 */

public ArrayList<Boolean> getSectionStarts()
  {
    ArrayList<Boolean> result = new ArrayList<Boolean>();

    java.util.Enumeration<Integer> startIndices =
            sectionInfo.getSectionStartIndices().elements();

    int nextStartIndex = startIndices.nextElement();

    int numSlots = slots.size();

    for( int slot = 0; slot < numSlots; slot++ )
      {
        if( slots.get(slot) != null )
          {
            if( slot == nextStartIndex )
              {
                result.add(true);
                if( startIndices.hasMoreElements() )
                  {
                    nextStartIndex = startIndices.nextElement();
                  }
              }
            else
              {
                result.add(false);
                while( slot > nextStartIndex && startIndices.hasMoreElements() )
                  {
                    nextStartIndex = startIndices.nextElement();
                  }
              }
          }
      }
    return result;
  }


/**
 * Get the section starts corresponding to chord symbols,
 * as an ArrayList of Boolean.
 * This is based on the observation that each section end immediately
 * precedes a section start, except for the very last end.
 * @return 
 */

public ArrayList<Boolean> getSectionEnds()
  {
    ArrayList<Boolean> result = new ArrayList<Boolean>();

    Iterator<Boolean> sectionStarts = getSectionStarts().iterator();
    if( sectionStarts.hasNext() )
      {
        // Ignore first start.
        sectionStarts.next();
      }
    while( sectionStarts.hasNext() )
      {
        result.add(sectionStarts.next());
      }
    result.add(true);
    return result;
  }


/**
 * Populate a RoadMapFrame with this ChordPart
 * @param roadmap 
 */

public void toRoadMapFrame(RoadMapFrame roadmap)
  {
    ArrayList<ChordSymbol> chordSymbols = getChordSymbols();
    ArrayList<Integer> durations = getChordDurations();

    PartIterator i = iterator();
    Iterator<Boolean> sectionEnds = getSectionEnds().iterator();

    while( i.hasNext() )
      {
        Chord chord = (Chord) i.next();

        ChordBlock dictChord = new ChordBlock(chord);

        dictChord.setSectionEnd(sectionEnds.next());

        roadmap.addChord(dictChord);
      }

  }


/**
 * Add chords in the current selection in RoadMapFrame to this ChordPart.
 */

public void addFromRoadMapFrame(RoadMapFrame roadmap)
  {
    ArrayList<imp.brickdictionary.ChordBlock> chords = roadmap.getChordsInSelection();

    Iterator<imp.brickdictionary.ChordBlock> i = chords.iterator();

    while( i.hasNext() )
      {
        ChordBlock chordBlock = i.next();
        
        Chord chord = new Chord(chordBlock);
        
        String name = chord.getName();
        if( chord.getRhythmValue() > 0 )
          {
            // Note: 0 duration causes addUnit to fail.

            addChord(chord);
          }
      }
  }

}
