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

import imp.brickdictionary.ChordBlock;
import imp.roadmap.RoadMapFrame;
import imp.util.ErrorLog;
import imp.util.Trace;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;
import polya.Polylist;

/**
 * An extension of the Part class that contains only Chord objects.
 * This is useful to separate simple chord drawing (names only,) from
 * the more complex melody drawing.
 * @see         Chord
 * @see         Part
 * @author      Stephen Jones
*/
@SuppressWarnings("serial")

public class ChordPart extends Part implements Serializable{

    private SectionInfo sectionInfo;

    /**
     * the default chord volume
     */
    public static final int DEFAULT_CHORD_VOLUME = DEFAULT_VOLUME - 20;
    
    public static final String DEFAULT_TITLE = "Untitled Part";

    /**
     * Creates an empty ChordPart.
     */
    public ChordPart() {
        super();
        sectionInfo = new SectionInfo(this);
        volume = DEFAULT_CHORD_VOLUME;
    }

    /**
     * Creates a ChordPart with the given size.
     * @param size      the number of slots in the ChordPart
     */
    public ChordPart(int size) {
        super(size);
        Trace.log(3, "creating new chord part of size " + size);
        sectionInfo = new SectionInfo(this);
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
  
  public void setBars(int bars)
      {
        int sizeInSlots = bars*metre[0]*BEAT;
        setSize(sizeInSlots);
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
public int getNextUniqueChordIndex(int slotIndex)
  {
    if( slotIndex < 0 || slotIndex >= size )
      {
        return -1;
      }
    
    int currentChordIndex = getCurrentChordIndex(slotIndex);
    Chord currentChord = getChord(currentChordIndex);
    
    if( currentChord == null )
      {
        return -1;
      }
    
    Chord nextChord = currentChord;
    int nextChordIndex = currentChordIndex;

    while( nextChord.getName().equals(currentChord.getName()) )
      {
        nextChordIndex = getNextIndex(nextChordIndex);
        if( nextChordIndex >= size )
          {
            return -1;
          }
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
    
    public int[] getChordMetre(){
        return super.getMetre();
    }
    
    public void setChordMetre(int top, int bottom){
        super.setMetre(top, bottom);
        sectionInfo.setMetre(top, bottom);
    }
    
 
/**
 *
 * @return an exact copy of this ChordPart
 */

@Override
public ChordPart copy()
  {
    ChordPart newPart = new ChordPart(size);
    PartIterator i = iterator();
    while( i.hasNext() )
      {
        int nextIndex = i.nextIndex();
        Unit unit = i.next();
        if( unit == null )
          {
            newPart.slots.set(nextIndex, null);
          }
        else
          {
            newPart.slots.set(nextIndex, unit.copy());
          }
      }

    newPart.sectionInfo = sectionInfo.chordlessCopy();
    newPart.unitCount = unitCount;
    newPart.title = title;
    newPart.composer = composer;
    newPart.volume = volume;
    newPart.setInstrument(instrument);
    newPart.keySig = keySig;
    newPart.setMetre(metre[0], metre[1]);
    newPart.swing = swing;
    
    newPart.sectionInfo.setChordPart(newPart);
    return newPart;
  }

public void setChordInstrument(int instrument)
  {
    //System.out.println("chordPart setChordInstrument to " + instrument);
    super.setInstrument(instrument);
  }

public boolean setStyle(String name)
  {
    return sectionInfo.setStyle(name);
  }

public void setStyle(Style s)
  {
    sectionInfo.setStyle(s);
  }

public void addSection(String styleName, int n, boolean isPhrase)
  {
    sectionInfo.addSection(styleName, n, isPhrase);
  }

public Style getStyle()
  {
    if( sectionInfo == null )
      {
        return null;
      }
    return sectionInfo.getStyle();
  }

public SectionInfo getSectionInfo()
  {
    return sectionInfo;
  }

public void setSectionInfo(SectionInfo si)
  {
    sectionInfo = si;
  }

public boolean hasOneSection()
  {
    return sectionInfo.hasOneSection();
  }

public long render(MidiSequence seq,
                   long time,
                   Track track,
                   int transposition,
                   boolean useDrums,
                   int endLimitIndex)
        throws InvalidMidiDataException
  {
    // to trace sequencing info:
    // System.out.println("ChordPart time = " + time + ", endLimitIndex = " + endLimitIndex);

    return sectionInfo.render(seq, time, track, transposition, useDrums, endLimitIndex);
  }


    /**
     * Returns a ChordPart that contains the Units within the slot range specified.
     * @param first     the first slot in the range
     * @param last      the last slot in the range
     * @return Part     the Part that contains the extracted chunk
     */
    @Override
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
        newPart.sectionInfo = sectionInfo.chordlessCopy();
        newPart.sectionInfo.setChordPart(newPart);

        return newPart;
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


public ArrayList<imp.brickdictionary.Block> toBlockList()
{
return sectionInfo.toBlockList();
}

/**
 * Populate a RoadMapFrame with this ChordPart
 * @param roadmap 
 */

public void toRoadMapFrame(RoadMapFrame roadmap)
  {
    roadmap.addBlocks(0, toBlockList());
  }


/**
 * Add chords in the current selection in RoadMapFrame to this ChordPart.
 */

public void addFromRoadMapFrame(RoadMapFrame roadmap)
  {
    ArrayList<imp.brickdictionary.ChordBlock> chords = roadmap.getChordsInSelection();

    Iterator<imp.brickdictionary.ChordBlock> i = chords.iterator();

    int totalSlots = 0;
    int sectionStart = 0;
    
    while( i.hasNext() )
      {
        ChordBlock chordBlock = i.next();
        
        Chord chord = new Chord(chordBlock);
        
        String name = chord.getName();
        if( chord.getRhythmValue() > 0 ) {
            // Note: 0 duration causes addUnit to fail.
            totalSlots += chordBlock.getDuration();
            addChord(chord);
            if( chordBlock.isSectionEnd() ) {
                addSection(roadmap.getStyle().getName(),sectionStart,chordBlock.isPhraseEnd());
                sectionStart = totalSlots;
            }
        }
      }
  }

    public void fixDuplicateChords(ChordPart chordpart, int resolution) {
        Chord prevChord = null;
        String prevChordName;
        Chord thisChord;
        String thisChordName;
        for (int i = 0; i < chordpart.size; i = i + resolution) {
            thisChord = chordpart.getChord(i);
            if (prevChord != null && thisChord != null) {
                prevChordName = prevChord.getName();
                thisChordName = thisChord.getName();
                if (prevChordName.equals(thisChordName)) {
                    chordpart.delUnit(i);
                }
            }
            prevChord = chordpart.getCurrentChord(i);
        }
    }

    /**
     * Writes the ChordPart to the passed BufferedWriter in Leadsheet notation.
     * @param out       the BufferedWriter to write the Part onto
     */
    
public void saveToLeadsheet(BufferedWriter out) throws IOException
  {
    out.write("(part");
    out.newLine();
    out.write("    (type chords)");
    out.newLine();
    out.write("    (title " + title + ")");
    out.newLine();
    out.write("    (composer " + composer + ")");
    out.newLine();
    out.write("    (instrument " + instrument + ")");
    out.newLine();
    out.write("    (volume " + volume + ")");
    out.newLine();
    out.write("    (key " + keySig + ")");
    out.newLine();

    out.write(")");
    out.newLine();

    Note.initializeSaveLeadsheet();

    if( sectionInfo == null )
      {
      ErrorLog.log(ErrorLog.SEVERE, 
                   "cannot save ChordPart " + getTitle() 
                + " with no sectionInfo");
      return;
      }
    
    PartIterator i = iterator();
    
    Iterator<SectionRecord> sec = sectionInfo.iterator();
    
    if( !sec.hasNext() )
      {
     ErrorLog.log(ErrorLog.SEVERE, 
                   "cannot save ChordPart " + getTitle() 
                 + " with empty sectionInfo");
      return;        
      }

    SectionRecord record = sec.next();

    int slot = 0;

    int slotLimit = size();

    int nextSectionStart;

    //iSystem.out.println("slotLimit = " + slotLimit);

    Chord chord = null;

    int sectionsToGo = sectionInfo.size();

    //System.out.println("saving  " + getTitle() + ", sectionInfo.size() = " + sectionsToGo);

    do // do-while
      {
        //System.out.println("\nrecord = " + record);

        // Save the section record
        record.saveToLeadsheet(out);

        // Get the next section record, if any.
        if( sec.hasNext() )
          {
            record = sec.next();
            nextSectionStart = record.getIndex();
          }
        else
          {
            nextSectionStart = slotLimit;
          }

        //System.out.println("next section start = " + nextSectionStart);

        // Pack Chords into section

        while( (chord != null || i.hasNext()) && slot < nextSectionStart )
          {
            if( chord == null )
              {
                Chord nextChord = (Chord) i.next();
                if( nextChord != null )
                  {
                    chord = nextChord.copy();
                  }
              }
            // Otherwise use the residue of previous chord

            // Where the next slot would normally be
            int nextSlot = slot + chord.getRhythmValue();

            if( nextSlot <= nextSectionStart )
              {
                // This chord fits in the current section.

                chord.saveLeadsheet(out, metre);
                chord = null;
                slot = nextSlot;
              }
            else
              {
                // This chord does not fit in the current section.
                // Calculate how much of this section can be used.
                int available = nextSectionStart - slot;
                chord.setRhythmValue(available);
                chord.saveLeadsheet(out, metre);

                // Determine what is left over.
                int residual = nextSlot - nextSectionStart;
                chord.setRhythmValue(residual);

                //System.out.println("overflow at slot " + slot + ", next section start = " + nextSectionStart + " " + chord + ", residual = " + residual);

                // This should force the end of this while, among other things

                slot = nextSectionStart;
              }

          }
        sectionsToGo--;
      }
    while( sectionsToGo > 0 ); // end of do-while

  }
   
@Override
public String toString()
  {
    Chord.initSaveToLeadsheet();
    StringWriter stringWriter = new StringWriter();
    BufferedWriter bufferedWriter = new BufferedWriter(stringWriter);
    try
      {
       saveToLeadsheet(bufferedWriter);
       bufferedWriter.close();
      }
    catch( IOException e)
      {
        
      }
    
    return stringWriter.toString();
  }
}