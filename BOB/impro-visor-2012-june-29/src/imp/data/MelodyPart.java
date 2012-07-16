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
import imp.ImproVisor;
import imp.com.InsertPartCommand;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.Preferences;
import imp.util.Trace;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.Track;


/**
 * An extension of the Part class that contains only Note (or, by extension, 
 * Rest) objects.  This is useful to contain drawing functions that are
 * specific to Notes, such as ties and accidentals.
 * @see         Note
 * @see         Rest
 * @see         Part
 * @author      Stephen Jones
 */
@SuppressWarnings("serial")

public class MelodyPart
        extends Part
  {
  /**
   *
   * This is a "fudge-factor" to keep display from getting weird
   * during extraction. It limits the smallest note to a set
   * duration.  If the copied duration is less, then it is artificially
   * set to this value. Otherwise, the duration could be, e.g. 1,
   * which creates a really weird display with 120 sub-divisions, and
   * from which it is hard to recover.
   */
  public static int MIN_EXTRACT_DURATION = BEAT / 4; //

  public static int magicFactor = 4;

  /**
   * Lowest pitch in the MelodyPart
   */
  private int lowestPitch = 127;

  /**
   * Highest pitch in the MelodyPart
   */
  private int highestPitch = 0;

  private static int[] knownRestValue =
          {0, 10, 12, 15, 20, 24, 30, 40, 60, 80, 120, 160, 240, 480};

  private boolean fudgeEnding = false;
  
  /* Move to Constants.java
  // 48 is a 5-tuple eigth note... 
  private static int[] knownNoteValue =
          {0, 10, 12, 15, 20, 24, 30, 40, 45, 48, 50, 60, 80, 90, 100, 120, 160, 180,
           240, 360, 480
  };
*/
  
  /**
   * Volume to be explicitly given for one-time melodies, for
   * use with entered melodies.  "Playback" melodies will instead
   * rely on the volume slider in the toolbar.
   */
  public int getSpecifiedVolume()
    {
    return volume;
    }

  /**
   * Creates an empty MelodyPart.
   */
  public MelodyPart()
    {
    super();

    this.volume = ImproVisor.getEntryVolume();
    }

  /** 
   * Creates a MelodyPart with the given size.
   * @param size      the number of slots in the MelodyPart
   */
  public MelodyPart(int size)
    {
    super(size);
    Trace.log(3, "creating new melody part of size " + size);

    // Use the entry volume as the default.  Any non-entered/pasted/generated
    // sample of code will will override this volume setting from the
    // playback slider, but for instant sounds we want the entry volume.
    this.volume = ImproVisor.getEntryVolume();
    }

  /**
   * Creates a MelodyPart from a given string of notes
   * @param notes     the list of notes
   */
  public MelodyPart(String notes)
    {
    super();
    this.volume = ImproVisor.getEntryVolume();
    String[] noteList = notes.split(" ");
    for( int i = 0; i < Array.getLength(noteList); ++i )
      {
      addNote(NoteSymbol.makeNoteSymbol(noteList[i]).toNote());
      }
    }

/**
 * Adds a Note to the end of the MelodyPart, extending its length.
 *
 * @param note the Note to add
 */
public void addNote(Note note)
  {
    // Ideally, if the note being added is a rest, and the last note is a rest,
    // the new rest should be merged in with the existing one. But my attempt
    // at this failed for some reason.
    
    if( note.isRest() )
      {
      }
    else
      {
        if( note.getPitch() < lowestPitch )
          {
            lowestPitch = note.getPitch();
          }
        if( note.getPitch() > highestPitch )
          {
            highestPitch = note.getPitch();
          }
      }
    addUnit(note);
    //System.out.println("melody part gets note " + note);
  }

  /**
   * Adds a Rest to the end of the MelodyPart, extending its length.
   * @param rest      the Rest to add
   */
  public void addRest(Rest rest)
    {
    addUnit(rest);
    }

  /**
   * Sets the specified slot to the given note.
   * @param slotIndex         the index of the slot to set at
   * @param note              the note to put at the slot index
   */
  public void setNote(int slotIndex, Note note)
    {
    if( slotIndex < 0 )
      {
        slotIndex = 0;
      }
    if( note != null && note.nonRest() )
      {
      //System.out.println("setNote at " + slotIndex + " to " + note);

      int currentMeasure = slotIndex / measureLength;

      int stopIndex = measureLength * (currentMeasure + 2);

      if( stopIndex < size && getNextIndex(slotIndex) > stopIndex )
        {
        setRest(stopIndex);
        }

      if( note.getPitch() < lowestPitch )
        {
        lowestPitch = note.getPitch();
        }
      else if( note.getPitch() > highestPitch )
        {
        highestPitch = note.getPitch();
        }
      }
    setUnit(slotIndex, note);
    }

  /**
   *
   */
  public synchronized void setNoteAndLength(int slotIndex, Note note,
                                              Notate parent)
    {
    int origLength = note.getRhythmValue();

    if( slotIndex + origLength >= size )
      {
      int measureLength = getMeasureLength();
      double lengthInMeasures =
              (slotIndex + origLength - size) / (double)measureLength;
      int measuresToAdd = (int)Math.ceil(lengthInMeasures);

      Part newMeasures = new Part(measuresToAdd * measureLength);
      (new InsertPartCommand(parent, this, size, newMeasures)).execute();
      }
       
    // make room if needed by setting rests at the locations of notes that will be covered by the new note
    setRest(slotIndex);
    int freeSlots = getFreeSlots(slotIndex);
    int nextIndex = slotIndex + freeSlots;
    while( freeSlots < origLength )
      {
      setRest(slotIndex + freeSlots);
      freeSlots = getFreeSlots(slotIndex);
      }

    synchronized( slots )
      {
      // merge the rests
      mergeFreeSlots(slotIndex);

      // add the note
      setNote(slotIndex, note);
      }

    if( note.getRhythmValue() > origLength )
      {
      // cut the note length to correct length
      setRest(slotIndex + origLength);
      }
    }

  
  /**
   * Sets the specified slot to the given rest
   * @param slotIndex         the index of the slot to set at
   * @param rest              the rest to put at the slot index
   */
  public void setRest(int slotIndex, Rest rest)
    {
    Trace.log(2, "setRest at " + slotIndex + " to " + rest);
    setUnit(slotIndex, rest);
    }

  
  /**
   * sets a Rest at the given slot index
   * @param slotIndex         the index of the slot to set at
   */
  public void setRest(int slotIndex)
    {
    setRest(slotIndex, new Rest());
    }

  
  /**
   * Returns the note at the given slot index
   * @param slotIndex         the index of the note to get
   * @return Note             the Note at the given index
   */
  public Note getNote(int slotIndex)
    {
    return (Note)getUnit(slotIndex);
    }

  
  /**
   * Returns the Note after the indicated slot index.
   * @param slotIndex         the index to start searching at
   * @return Note             the Note after the given index
   */
  public Note getNextNote(int slotIndex)
    {
    return (Note)getNextUnit(slotIndex);
    }

  
  /**
   * Returns the Note before the indicated slot index.
   * @param slotIndex         the index to start searching at
   * @return Note             the Note before the given index
   */
  public Note getPrevNote(int slotIndex)
    {
    return (Note)getPrevUnit(slotIndex);
    }

  
  /**
   * Gets the lowest pitch in the MelodyPart
   * @return int              lowest pitch
   */
  public int getLowestPitch()
    {
    return lowestPitch;
    }

  
  /**
   * Gets the highest pitch in the MelodyPart
   * @return int              highest pitch
   */
  public int getHighestPitch()
    {
    return highestPitch;
    }

  
  /**
   * Returns the number of slots from the index to the next Note.
   * @param index     the index to get the free slots
   * @return int      the number of free slots
   */
  public int getFreeSlots(int index)
    {
    if( getNote(index) != null && getNote(index).nonRest() )
      {
      return 0;
      }

    if( index < 0 )
      {
        return size;
      }
    int nextIndex = getNextIndex(index);
    while( nextIndex < size - 1 )
      {
      if( getNote(nextIndex) != null &&
              getNote(nextIndex).nonRest() )
        {
        return nextIndex - index;
        }
      nextIndex = getNextIndex(nextIndex);
      }
    return size - index;
    }

  
  public int getFreeSlotsFromEnd()
    {
    int slotIndex = getSize() - 1;
    int restSlots = 0;
    Note current;

    while( slotIndex >= 0 )
      {
      current = getNote(slotIndex);
      // if we find a unit that is not a rest, we return the rest slots found so far
      if( current != null )
        {
        if( current.nonRest() )
          {
          return restSlots;
          }
        restSlots += current.getRhythmValue();
        }
      slotIndex--;
      }

    if( restSlots != getSize() )
      {
      Trace.log(0,
              "Possible error calculating number of rests from the end of the piece in Part.getRestSlotsFromEnd");
      }
    return restSlots;
    }

  
  public synchronized void mergeFreeSlots(int index)
    {
    if( index < 0 )
      {
        return;
      }
    if( getNote(index) == null )
      {
      index = getPrevIndex(index);
      if( getNote(index).nonRest() )
        {
        return;
        }
      }
    else if( getNote(index).nonRest() )
      {
      return;
      }

    Note firstNote = getNote(index);

    int nextIndex = getNextIndex(index);
    Note nextNote = getNote(nextIndex);

    while( nextIndex < size - 1 )
      {
      if( nextNote != null && nextNote.nonRest() )
        {
        return;
        }

      slots.set(nextIndex, null);
      unitCount--;

      firstNote.setRhythmValue(firstNote.getRhythmValue() + nextNote.getRhythmValue());

      nextIndex = getNextIndex(nextIndex);
      nextNote = getNote(nextIndex);
      }
    }

  
  /**
   * Returns an exact copy of this Part
   * @return Part   copy
   */
  @Override
  public MelodyPart copy()
    {
      return copy(0);
    }
    
  /**
   * Returns an exact copy of this Part from startingIndex
   * @return Part   copy
   */

  public MelodyPart copy(int startingIndex)
{
  return copy(startingIndex, size-1);
}
  

//      int newSize = size - startingIndex;
//      
//      if( newSize <= 0 )
//        {
//          return new MelodyPart(0);
//        }
//
//      int newUnitCount = 0;
//      
//      try
//      {
//      MelodyPart newPart = new MelodyPart(newSize);
// 
//       int i = startingIndex;
//       
//       if( slots.get(startingIndex) == null )
//         {
//           // Find first non-null slot, and place a rest at
//           // startingIndex.
//           
//           int j = startingIndex + 1;
//           
//           for( ; j < size; j++ )
//             {
//               if( slots.get(j) != null )
//                 {
//                   break;
//                 }
//             }
//           
//           newPart.slots.set(0, new Rest(j - startingIndex));
// 
//           unitCount = 1;
//           i = j;
//         }
//       
//       for( ; i < size; i++ )
//        {
//          Unit unit = slots.get(i);
//          if( unit != null )
//            {
//            unit = unit.copy();
//            newUnitCount++;
//            }
//          newPart.slots.set(i - startingIndex, unit);
//        }
//
//    newPart.unitCount = newUnitCount;
//    newPart.title = title;
//    newPart.volume = volume;
//    newPart.keySig = keySig;
//    newPart.metre[0] = metre[0];
//    newPart.metre[1] = metre[1];
//    newPart.beatValue = beatValue;
//    newPart.measureLength = measureLength;
//    newPart.swing = swing;
//    newPart.instrument = instrument;
//    return newPart;
//      }
//    catch( Error e )
//      {
//        ErrorLog.log(ErrorLog.FATAL, 
//                     "Not enough memory to copy part of size " + newSize + ".");
//        return null;
//      }
//    }
//
//  
//  
  /**
   * Returns an exact copy of this Part from startingIndex to endingIndex
   * @return 
   */

public MelodyPart copy(int startingIndex, int endingIndex)
{
      int newSize = endingIndex + 1 - startingIndex;
      
      if( newSize <= 0 )
        {
          return new MelodyPart(0);
        }

      int newUnitCount = 0;
      
      try
      {
      MelodyPart newPart = new MelodyPart(newSize);
      //System.out.println("melodyPart with size " + newPart.getSize() + " start = " + startingIndex + ", end = " + endingIndex); 
       int i = startingIndex;
       
       if( slots.get(startingIndex) == null )
         {
           // Find first non-null slot, and place a rest at
           // startingIndex.
           
           i++;
           
           for( ; i <= endingIndex; i++ )
             {
               if( slots.get(i) != null )
                 {
                   break;
                 }
             }
           
           newPart.slots.set(0, new Rest(i - startingIndex));
 
           unitCount = 1;

         }
       
       for( ; i <= endingIndex && i < size ; i++ )
        {
          Unit unit = slots.get(i);

          if( unit != null )
            {
            unit = unit.copy();
            newUnitCount++;
            if( i + unit.getRhythmValue() - 1 > endingIndex )
              {
              // In case the last unit extends beyond endingIndex;
              unit.setRhythmValue(endingIndex - i + 1);
              }
            }
          newPart.slots.set(i - startingIndex, unit);

        }

    newPart.unitCount = newUnitCount;
    newPart.title = title;
    newPart.volume = volume;
    newPart.keySig = keySig;
    newPart.metre[0] = metre[0];
    newPart.metre[1] = metre[1];
    newPart.beatValue = beatValue;
    newPart.measureLength = measureLength;
    newPart.swing = swing;
    newPart.instrument = instrument;
    return newPart;
      }
    catch( Error e )
      {
        ErrorLog.log(ErrorLog.FATAL, 
                     "Not enough memory to copy part of size " + newSize + ".");
        return null;
      }
    }
  
  
      public int getPitchSounding(int index) {
        //if there is a note struck at the index, return its pitch
        Note curr = this.getNote(index);
        if (curr != null) {
            return curr.getPitch();        //otherwise look for the last pitch
        }
        int prevIndex = 0;
        try {
            prevIndex = this.getPrevIndex(index);
        } catch (Exception e) {
            System.out.println("error in MelodyPart.getPitchSounding: index: " + index);
            //e.printStackTrace();
        }
        Note prevNote = this.getNote(prevIndex);
        if (prevNote.isRest()) {
            return -1;        //if previous note is still sounding, return its pitch
        }
        if (prevNote.getRhythmValue() > (index - prevIndex)) {
            return prevNote.getPitch();
        //default case
        }
        return -1;
    }
  
  
  /**
   * Returns a reverse copy of this Part
   * @return Part   copy
   */
  public MelodyPart copyReverse()
    {
    Trace.log(3, "copying in  reverse melody part of size " + size);

    MelodyPart newPart = extractReverse(0, size);

    newPart.unitCount = unitCount;
    newPart.title = title;
    newPart.volume = volume;
    newPart.keySig = keySig;
    newPart.metre[0] = metre[0];
    newPart.metre[1] = metre[1];
    newPart.beatValue = beatValue;
    newPart.measureLength = measureLength;
    newPart.swing = swing;
    newPart.instrument = instrument;
    return newPart;
    }

  public void dump(PrintStream out)
    {
    Part.PartIterator i = iterator();
    try
      {
      while( i.hasNext() )
        {
        Note note = (Note)i.next();
        out.print(note.getRhythmValue());
        out.print(" ");
        out.print(note.getPitch());
        out.println();
        }
      }
     catch(Exception e)
      {
      // Both NullpointerExecption and ArrayIndexOutOfBoundsException have occurred
      // here. This should be investigated.
      // For now, let's use it as an early exit.
      }
    }
 
  /**
   * Changes Notes in the Part so ties are proper and the Part
   * can be drawn.
   */
  public void makeTies()
    {
    Part.PartIterator i = iterator();
    while( i.hasNext() )
      {
      int j = i.nextIndex();

     try {
      Note n = (Note)i.next();
      tieThis(j, n, 0);
     }
     catch(Exception e)
       {
         // RK 6/11/2010 Getting exception here on MIDI entry.
         // This might not be the best solution, however.
       return;
       }
      }
    }

  /**
   * This is a recursive routine, originally created by Martin Hunt, I think.
   @param slotIndex
   @param note
   @param level
   */
  
  public void tieThis(int slotIndex, Note note, int level)
    {
    if( note == null )
      {
      // Don't know why we are getting here, but maybe this should be looked into - Martin, 2006-07-12
      Trace.log(2,
              "Warning: tieThis attempted to tie a null note.  This is probably a bug.");
      return;
      }

    int rhythmValue = note.getRhythmValue();

    // Note: we need rest index even if it is a note, for the purpose of
    // non-tied exit.
    
    int knownRestIndex = java.util.Arrays.binarySearch(knownRestValue, rhythmValue);
    int knownNoteIndex = java.util.Arrays.binarySearch(knownNoteValue, rhythmValue);
    int knownTupletIndex = java.util.Arrays.binarySearch(knownTupletValue, rhythmValue);

    int knownRhythmIndex = note.isRest() ? knownRestIndex : knownNoteIndex;

    // Use integer division to get the start of the current measure and the
    // next measure.
    int currentMeasure = (slotIndex / measureLength) * measureLength;
    int nextMeasure = currentMeasure + measureLength;

    // Use integer division to get the start of the current beat and the next
    // beat.
    int currentBeat = (slotIndex / beatValue) * beatValue;
    int nextBeat = currentBeat + beatValue;

    int firstNoteRV = -1;
    int secondNoteRV = -1;
    
    if( slotIndex + rhythmValue <= nextMeasure  && (knownTupletIndex >= 0 ) )
      {
      return;
      }
    
     // If the note starts on an off beat and crosses over the next beat, then
    // we want to cut off the note at the beat, and create a new note with the
    // residual value.
    
    // What about notes such as tuplets? Not sure how it works then.
    
    if( slotIndex % beatValue != 0 && slotIndex + rhythmValue > nextBeat )
      {
      firstNoteRV = nextBeat - slotIndex;
      secondNoteRV = rhythmValue - firstNoteRV;

      //DEBUG
      //if(note.getPitch() != Note.REST)
      //    System.out.println("BEAT: " + firstNoteRV + " " + secondNoteRV);
      }
    
    // If the note crosses a barline, then we cut the note off at the end of the
    // measure and create a new note with the residual duration.
    // Ok.
    
    else if( slotIndex + rhythmValue > nextMeasure )
      {
      firstNoteRV = nextMeasure - slotIndex;
      secondNoteRV = rhythmValue - firstNoteRV;

      //DEBUG
//            if(note.getPitch() != Note.REST)
//                System.out.println("MEASURE: " + firstNoteRV + " " + secondNoteRV);
      }
    
    // If the note is longer than a beat, and the next note is not on a beat ...
    // Do we always want to do this? Why?

    else if( rhythmValue > beatValue && (slotIndex + rhythmValue) % beatValue != 0 )
      {
      secondNoteRV = rhythmValue % beatValue;
      firstNoteRV = rhythmValue - secondNoteRV;

      //DEBUG
//            if(note.getPitch() != Note.REST)
//                System.out.println("Overlap: beat: " + beatValue + ", 1: " + firstNoteRV + ", 2: " + secondNoteRV + ", total: " + rhythmValue);
      }
    
    // special case:  triplets:  value: 5/8 * BEAT
    // Why this case ??
    
    else if( !note.isRest() && rhythmValue < beatValue )
      {
      if( knownRhythmIndex < 0 )
        {
        //System.out.println("Resolving note (" + rhythmValue + "), beatValue: " + beatValue);
        
        knownRhythmIndex = -knownRhythmIndex - 2;

        // if it's less than a beat, try to split it evenly
        if( rhythmValue < beatValue )
          {
          for( int i = 2; i < 20; i++ )
            {
            int div = beatValue / i;
            // if partitioning a BEAT into i pieces evenly divides the rhythmValue...
            if( (rhythmValue / div) * div == rhythmValue )
              {
              firstNoteRV = div * (i / 2);    // this could be changed to simply div, this would shift the ties so the smallest tied section in a tied tuplet note comes first
              secondNoteRV = rhythmValue - firstNoteRV;
              break;
              }
            }
          }
        }
      }
    
    // for now, only apply the next bit of code to rests... 
    // If the note is not a known value, we need to split it into known
    // values
    else if( note.isRest() && (knownRhythmIndex =
            java.util.Arrays.binarySearch(knownRestValue, rhythmValue)) < 0 )
      {
      //System.out.println("Resolving note (" + rhythmValue + "), beatValue: " + beatValue);

      // if it's less than a beat, try to split it evenly
      if( rhythmValue < beatValue )
        {
        for( int i = 2; i < 20; i++ ) // Why the magic number 20 ???
          {
          int div = beatValue / i;
          // System.out.println(" -> "+i+" / "+div);
          // if partitioning a BEAT into i pieces evenly divides the rhythmValue...
          if( (rhythmValue / div) * div == rhythmValue )
            {
            firstNoteRV = div;
            secondNoteRV = rhythmValue - div;
            break;
            }
          }
        }
      }

    // If the note is longer than a whole note, we need to split it up (but only if it's
    // an actual note -- this keeps with the convention that a full measure of rests only
    // has one whole rest in it).
    if( (firstNoteRV > measureLength || (firstNoteRV == -1 && rhythmValue > measureLength)) && !note.isRest() )
      {
      firstNoteRV = measureLength;
      secondNoteRV = rhythmValue - firstNoteRV;
      }

    /* if none of the other cases get caught, this should probably be the
     * LAST check... since the other cases are more important, and this is
     * a final verification
     *
     * Check that each note is a known valid value
     *
     * Notes larger than a beatValue get split using the largest known note
     *    that is a multiple of a beatValue
     *
     * Notes smaller than a beatValue are split using the largest beatValue
     */
    // Choose one of two arrays:
    
    int[] knownRhythmValue = 
            note.isRest() ? knownRestValue : knownNoteValue;
    
    // Get the duration of the note or rest; 
    // If if it is a note and too long, then we need to tie.
    

//        if(note.nonRest())
//            System.out.println(rhythmValue);
    
    if( firstNoteRV == -1 && (knownRhythmIndex =
            java.util.Arrays.binarySearch(knownRhythmValue, rhythmValue)) < 0 )
      {
      // not a known rhythm value, so figure out the index of the largest known rhythmValue
      // not larger than the current RhythmValue:
      //  "-knownRhythmIndex - 1" is the first index with value greater than rhythmValue
      // so "-knownRhythmIndex - 2" is the first index with largest value not greater than rhythmValue
      
      knownRhythmIndex = -knownRhythmIndex - 2;

//            System.out.println("Note of value " + rhythmValue);

      // this is the secondNoteRV... assuming we continue to split
      int remainder = rhythmValue - knownRhythmValue[knownRhythmIndex];

      if( rhythmValue > beatValue )
        {
        // if the note is greater than a beat, we take the largest multiple of the beat
        for( int i = knownRhythmIndex; i > 2; i-- )
          {
          if( (knownRhythmValue[i] / beatValue) * beatValue == knownRhythmValue[i] )
            {
            firstNoteRV = (knownRhythmValue[i] / beatValue) * beatValue;
            secondNoteRV = rhythmValue - firstNoteRV;
            break;
            }
          }
        }
      else
        {
        // Only continue with the split if it doesn't lead to an extremely small note (the remainder has to be at least as large 
        // as the first known note
        if( knownRhythmIndex > 0 && remainder >= knownRhythmValue[1] )
          {
          firstNoteRV = knownRhythmValue[knownRhythmIndex];
          secondNoteRV = remainder;

          // I don't think this line ever gets called cause it was
          // not commented out... and I never saw it print anything
          // System.out.println("Note: " + firstNoteRV + " " + secondNoteRV);
          }
        else
          {
          //FIX: ErrorLog.log(ErrorLog.WARNING, "Possible rendering error: small rhythm value found: " + rhythmValue, false);
          }
        }
      }

    // If none of the above cases are hit, we don't want to tie anything.
    if( firstNoteRV <= 0 || secondNoteRV <= 0 )
      {
      return;
      }

    // If it's the first note, set it as the first tie.
    if( !note.isTied() )
      {
      note.setTie(true);
      note.setFirstTie(true);
      }

    // Set the note to the truncated duration we calculated above.
    note.setRhythmValue(firstNoteRV);

    // Create the new note as a copy of the first with the residual duration.
    Note newNote = note.copy();
    newNote.setFirstTie(false);
    newNote.setAccidental(Constants.Accidental.NOTHING);
    newNote.setRhythmValue(secondNoteRV);


    // Add the new note to our array at the appropriate position.
    slots.set(slotIndex + firstNoteRV, newNote);
    unitCount++;

    tieThis(slotIndex, note, level + 1);

    // Check to see if the new note needs to be tied.
    tieThis(slotIndex + firstNoteRV, newNote, level + 1);
    }

  /**
   * Edits the Accidental field on each Note to represent what should be
   * drawn.
   */
  public void makeAccidentals()
    {
    ArrayList<Constants.Accidental> accidentalVector = getKeySigVector();

    // Go through each slot so that we can reset the accidentalVector
    // at the start of each measure
    for( int i = 0; i < size; i++ )
      {
      int thisBeat = beatValue * (i / beatValue);

      // Reset the accidentalVector at the start of each measure
      if( thisBeat == i && (i / beatValue) % metre[0] == 0 )
        {
        accidentalVector = getKeySigVector();
        }

      Note note;

      // No accidental stuff in an empty slot or a rest
      if( getUnit(i) == null || ((Note)getUnit(i)).isRest() )
        {
        continue;
        }
      note = (Note)getUnit(i);

      int pitch = note.getPitch();

      // The easiest way to do this is to adjust the pitch so that
      // it is on the letter, independent of accidental
      if( note.getAccidental() == Constants.Accidental.SHARP )
        {
        note.setDrawnPitch(pitch - 1);
        }
      else if( note.getAccidental() == Constants.Accidental.FLAT )
        {
        note.setDrawnPitch(pitch + 1);
        }
      else
        {
        note.setDrawnPitch(pitch);
        }

      // If the note's accidental has already been set
      // correctly by a previous note in the measure, or
      // by the key signature, draw nothing
      if( note.getAccidental() == accidentalVector.get(note.getDrawnPitch()) )
        {
        note.setAccidental(Constants.Accidental.NOTHING);
        }

      // If the note's accidental needs to be drawn, leave
      // the accidental field as it is and make the accidentalVector
      // reflect that note's new default accidental
      else
        {
        accidentalVector.set(note.getDrawnPitch(), note.getAccidental());
        }
      }
    }

  /**
   * Creates a new Track for this Part on the specified channel, and adds
   * it to the specified Sequence, called by Score.render.
   * @param seq     the Sequence to add a Track to
   * @param ch      the channel to put the Track on
   */
  public long render(MidiSequence seq, 
                     int ch, 
                     long time,
                     Track track,
                     int transposition, 
                     int endLimitIndex)
          throws InvalidMidiDataException
    {
      
    boolean sendBankSelect = Preferences.getMidiSendBankSelect();
    // to trace sequencing:
    //System.out.println("Sequencing MelodyPart on track " + track + " time = " + time + " endLimitIndex = " + endLimitIndex);
      
    Part.PartIterator i = iterator();

    // Note that you can't have two instruments playing on the same
    // channel at the same time.  So we can never have more than 16
    // instruments playing at once, and we should add code somewhere
    // to make sure instruments are sorted to the right channels
    
    // Select Bank 0 before program change. 
    // Not sure this is correct. Check before releasing
    // both here and in Style.java
    
    // set program change. Do we need this for every call?
    
    track.add(MidiSynth.createProgramChangeEvent(ch, instrument, time));

    if( sendBankSelect )
      { 
      track.add(MidiSynth.createBankSelectEventMSB(0, time));
      track.add(MidiSynth.createBankSelectEventLSB(0, time));
      }
    
    // the absolute time is advanced and returned by the next render
    // function

    endLimitIndex *= magicFactor; 

    while( i.hasNext() && Style.limitNotReached(time,  endLimitIndex) )
      {
      Note note = (Note)i.next();
      time = note.render(seq.getSequence(), track, time, ch, transposition, sendBankSelect);
      }

    return time;
    }
  

  /**
   * Returns an ArrayList of Accidentals corresponding to all possible pitches.
   * The weird thing about this is that we only end up marking the 
   * accidental independent pitches: C1, D1, E1, G4, A4, etc.
   * This is to make an easy mapping between the pitches as they are
   * stored for playback and elements of this Vector.
   * @return ArrayList<Accidental>       the accidental vector
   */
  public ArrayList<Constants.Accidental> getKeySigVector()
    {
    ArrayList<Constants.Accidental> keySigVector = new ArrayList<Constants.Accidental>(TOTALPITCHES); 
    
     for( int i = 0; i < TOTALPITCHES; i++ )
      {
        keySigVector.add(Constants.Accidental.NOTHING);
      }    
     
    switch( keySig )
      {
      case CBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case GBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case DBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case ABMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case EBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case BBMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case FMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.FLAT);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case CMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          keySigVector.set(i, Constants.Accidental.NATURAL);
          }
        break;

      case GMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODF )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case DMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case AMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case EMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case BMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case FSMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;

      case CSMAJOR:
        for( int i = 0; i < TOTALPITCHES; i++ )
          {
          if( i % SEMITONES == MODC || i % SEMITONES == MODF ||
              i % SEMITONES == MODG || i % SEMITONES == MODD ||
              i % SEMITONES == MODA || i % SEMITONES == MODE ||
              i % SEMITONES == MODB )
            {
            keySigVector.set(i, Constants.Accidental.SHARP);
            }
          else
            {
            keySigVector.set(i, Constants.Accidental.NATURAL);
            }
          }
        break;
      }
    return keySigVector;
    }

  /**
   * Sets whether in extracting a melodypart, the ending should be truncated
   * if necessary
   * @param b   boolean whether or not to truncate the endings
   */ 
  public void truncateEndings(boolean b) {
      fudgeEnding = b;
  }
  
  /**
   * Returns a MelodyPart that contains the Units within the slot range specified.
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  
  @Override
  public MelodyPart extract(int first, int last)
    {
    //System.out.println("extract melody from " + first + " to " + last);
    MelodyPart newPart = new MelodyPart();
    int i = first;
    int lastUnitIndex = first;
    if( getUnit(first) == null )
      {
      // Create a rest up to the first non-null unit
      for( i = first + 1; i <= last; i++ )
        {
        if( getUnit(i) != null )
          {
          break;
          }
        }
      // Add the synthetic rest to the new part
      // equivalent to space before the first actual note
      newPart.addNote(new Rest(i - first));

      }

    // Add other Units

    for(; i <= last; i++ )
      {
      if( getUnit(i) != null )
        {
        newPart.addUnit((Note)getUnit(i).copy());
        lastUnitIndex = i;
        }
      }
    
    if(fudgeEnding) {
        // Truncate the last Unit, if necessary.
        Unit lastUnit = newPart.getPrevUnit(newPart.getSize());
        
        if( lastUnit != null && lastUnitIndex + lastUnit.getRhythmValue() > last)
        {
            int oldRhythmValue = lastUnit.getRhythmValue();
            lastUnit.setRhythmValue(last + 1 - lastUnitIndex);
            int amountTruncated = oldRhythmValue - lastUnit.getRhythmValue();
            //set the size of newPart to account for the truncation
            newPart.setSize(newPart.size() - amountTruncated);
        }
    }
     

    return newPart;
    }

  /**
   * Returns a MelodyPart that contains the Units within the slot range specified,
   *         but in reverse order.
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  public MelodyPart extractReverse(int first, int last)
    {
    MelodyPart newPart = new MelodyPart();
    for( int i = last; i >= first; i-- )
      {
      if( getUnit(i) != null )
        {
        newPart.addUnit(getUnit(i).copy());
        }
      }
    return newPart;
    }

  /**
   * Returns a MelodyPart that contains the Units within the slot range specified,
   *         but time-warped by num/denom
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  public MelodyPart extractTimeWarped(int first, int last, int num, int denom)
    {
    MelodyPart newPart = new MelodyPart();
    Trace.log(2,
            "extractTimeWarped from " + first + " to " + last + " by " + num + "/" + denom);
    for( int i = first; i < last; i++ )
      {
      if( getUnit(i) != null )
        {
        Unit newUnit = getUnit(i).copy();
        newUnit.setRhythmValue((newUnit.getRhythmValue() * num) / denom);
        newPart.addUnit(newUnit);
        }
      }
    newPart.addUnit(new Rest(15));
    return newPart;
    }

  /**
   * Returns a MelodyPart that contains the Units within the slot range specified,
   *         but inverted.
   * Inversion is with respect to the highest and lowest notes in the series
   * and is done by ordering the notes, not using a mathematical formula.
   *
   * @param first     the first slot in the range
   * @param last      the last slot in the range
   * @return MelodyPart     the MelodyPart that contains the extracted chunk
   */
  public MelodyPart extractInverse(int first, int last)
    {
    // insertion sort pitches into sorted linked list

    LinkedList<Note> ordered = new LinkedList<Note>();
    for( int i = first; i <= last; i++ )
      {
      Unit unit = getUnit(i);
      if( unit != null && unit instanceof Note )
        {
        Note note = (Note)unit;
        int pitch = note.getPitch();
        if( pitch != REST )
          {
          boolean found = false;
          ListIterator<Note> it = ordered.listIterator(0);
          int position = 0;
          while( !found && it.hasNext() )
            {
            Note element = it.next();
            int thatPitch = element.getPitch();
            if( pitch < thatPitch )
              {
              ordered.add(position, note);
              found = true;
              }
            else if( thatPitch == pitch )
              {
              found = true;
              }
            position++;
            }
          if( !found )
            {
            // add at end
            ordered.add(note);
            }
          }
        }
      }

    Object orderedArray = ordered.toArray();

    int length = Array.getLength(orderedArray);

    MelodyPart newPart = new MelodyPart();

    // invert each element

    for( int i = first; i <= last; i++ )
      {
      if( getUnit(i) != null )
        {
        Note note = (Note)getUnit(i);
        Note newNote = null;
        int pitch = note.getPitch();
        if( pitch == REST )
          {
          newNote = note.copy();
          }
        else
          {
          boolean found = false;
          // Find this notes pitch in the array
          for( int j = 0; !found && j < length; j++ )
            {
            if( ((Note)Array.get(orderedArray, j)).getPitch() == pitch )
              {
              Note inverse = (Note)Array.get(orderedArray, length - 1 - j);

              newNote = inverse.copy();

              // New note has the same rhythm value as old,
              // but the pitch is inverted.

              newNote.setRhythmValue(note.getRhythmValue());
              found = true;
              break;
              }
            }
          assert (found);
          }
        newPart.addUnit(newNote);
        }
      }
    return newPart;
    }
 
 /**
  * returns the last note in the melodypart that is not a rest
  */
 public Note getLastNote() {
     int tracker = this.getPrevIndex(size);
 
     Note n = this.getNote(tracker);
     
     while(n.isRest()) {
         tracker = this.getPrevIndex(tracker);
         n = this.getNote(tracker);
     }
     
     return n;
 }
 
 /**
  * Currently only used in ChordExtract. Normalizes the MelodyPart such that it
  *     contains Units for each resolution slot. eg With a quarter note resolution,
  *     a whole note will be normalized into 4 quarter notes.
  * @param resolution   
  * 
  */
 public void normalize(int resolution)
 {
     for (int i = 0; i < this.size; i = i + resolution)
     {
         Note note = this.getNote(i);
         if (note==null && i==0)
         {
             return;
         }
         if (note==null)
         {
             Note prevNote = this.getNote(i-resolution);
             prevNote.setRhythmValue(resolution);
             this.setNote(i, prevNote);
         }
     }
 }

     /**
     * The only current use is in LickgenFrame.
     * @param selectionStart
     * @param numSlots
     * @returns whether a melody is empty from selectionStart to selectionStart + numSlots
     */
     public boolean melodyIsEmpty(int selectionStart, int numSlots) {
        if(selectionStart < 0) return false;
        int tracker = this.getPrevIndex(selectionStart);
        Note n = this.getNote(tracker);
        //if note is held from before, it's not empty
        if(n != null && (!n.isRest()) && n.getRhythmValue() > selectionStart - tracker) {
            return false;
        }
        //if there is a note at the start, it's not empty
        n = this.getNote(selectionStart);
        if(n != null && (!n.isRest()) ) 
            return false;
        
        //now check for notes in the rest
        tracker = this.getNextIndex(selectionStart);
        while(tracker < selectionStart + numSlots) {
            n = this.getNote(tracker);
            tracker = this.getNextIndex(tracker);
            if(n == null) return true; //there is no next note
            if(!n.isRest()) return false;
        }

        return true;
    }
 
public int getInitialBeatsRest()
  {
  int count = 0;
  for( Unit note: slots )
    {
      if( note != null && !((Note)note).isRest() )
        {
          break;
        }
      count++;
    }
   return count/BEAT;
   }
     
 @Override
 public String toString()
  {
  StringBuilder buffer = new StringBuilder();
  
  int n = slots.size();
  
  int currentVolume = 127;
  
  for( int i = 0; i < n; i++ )
    {
    Note note = (Note)slots.get(i);
    if( note != null )
      {
      //System.out.println("note volume = " + note.getVolume());
      if( note.getVolume() != currentVolume )
        {
          currentVolume = note.getVolume();
          buffer.append("v");
          buffer.append(currentVolume);
          buffer.append(" ");
        }
      buffer.append(note.toLeadsheet());
      buffer.append(" ");
      }
    }
  return buffer.toString ();
  }
 
 //Unfinished
//public void insertPartAt(int start, int finish, MelodyPart insert)
//  {
//    int j = 0;
//    for( int i = start; i <= finish; i++, j++ )
//      {
//        slots.set(i, insert.getUnit(j));
//      }
//  }
 
 
/**
 * getSyncVector gets an array of 1's and 0's from a MelodyPart
 * representing note onsets, for the purpose of a synchronization
 * algorithm being implemented by David Halpern. Currently every slotSpacing-th
 * slot is polled to see whether there is a note onset there. If so,
 * a 1 is returned in that position. Otherwise a 0 is returned.
 * For this purpose, rests are not considered to be notes.
 * For example, slotSpacing might be 15.
 * @return 
 */
 
public int[] getSyncVector(int slotSpacing, int maxSize)
  {
    int n = Math.min(size(), maxSize);
    int[] result = new int[(int)Math.ceil(n/slotSpacing)];
    for( int i = 0, j = 0; i < n; i+= slotSpacing, j++ )
      {
        Note note = (Note)slots.get(i);
        result[j] = note != null && note.nonRest() ? 1 : 0;
      }
    return result; 
  }


/**
 * Clears all units from this part, leaving size as is and placing a rest in
 * the first slot.
 */

public void clear()
  {
    slots = new ArrayList<Unit>(size);
    for( int i = 0; i < size; i++ )
      {
        slots.add(null);
      }
    
    slots.set(0, new Rest(size));
  }

public Note getFirstNote()
  {
    return (Note)slots.get(0);
  }
}