/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2012 Robert Keller and Harvey Mudd College
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

/**
 * MelodyPartAccompanied is an extension of the MelodyPart class that is used
 * so that a MelodyPart can access information contained in the ChordPart class.
 * Introduced as a basis for the implementation of mixed choruses (different
 * lengths, progressions, sections, styles).
 * @author Jack Davison, Robert Keller
 */

@SuppressWarnings("serial")
public class MelodyPartAccompanied extends MelodyPart {
    
    private ChordPart chordPart;
    
    public MelodyPartAccompanied(){
        super();
    }
    
    public MelodyPartAccompanied(int size, ChordPart chordPart){
        super(size);
        this.chordPart = chordPart;
        }
    
    public MelodyPartAccompanied(ChordPart chordPart){
        super();
        this.chordPart = chordPart;
    }
    
    /**
     * CAUTION: This constructor does not copy the actual melody. It only
     * gives a melody of size identical to the original, with a copy of the
     * original chord part.
     * @param melodyPartAccompanied 
     */
    public MelodyPartAccompanied(MelodyPartAccompanied melodyPartAccompanied){
        super(melodyPartAccompanied.size());
        this.chordPart = melodyPartAccompanied.getChordPart().copy();
    }
    
    @Override
    public MelodyPartAccompanied copy()
    {
      return copy(0);
    }
    
    @Override
    public MelodyPartAccompanied copy(int startingIndex)
    {
        return copy(startingIndex, size-1);
    }
    
    @Override
    public MelodyPartAccompanied copy(int startingIndex, int endingIndex)
{
       int newSize = endingIndex + 1 - startingIndex;
      
      if( newSize <= 0 )
        {
          return new MelodyPartAccompanied(0, chordPart);
        }

      MelodyPartAccompanied result = new MelodyPartAccompanied(newSize, chordPart);
      copy(startingIndex, endingIndex, result);
      return result;
}
    public ChordPart getChordPart() {
        return chordPart;
    }
    
    public void setChordPart(ChordPart chordPart){
        this.chordPart = chordPart;
     //System.out.println("setting chordPart of " + title + " to " + chordPart.getTitle());
     }
    
    @Override
    public MelodyPartAccompanied extractTimeWarped(int first, int last, int num, int denom){
      MelodyPartAccompanied newPart = new MelodyPartAccompanied();
      extractTimeWarped(newPart, first, last, num, denom);
      return newPart;
  }
    
@Override
public void setBars(int bars)
  {
    if( chordPart != null )
      {
        chordPart.setBars(bars);
      }
    
    super.setBars(bars);

  }

@Override
public int getBars()
  {
    return chordPart.getBars();
  }

public SectionInfo getSectionInfo()
  {
    if( chordPart != null )
      {
        return chordPart.getSectionInfo();
      }
    
    return null;
  }

public String getChordPartTitle()
  {
    return chordPart.getTitle();
  }

@Override
public String toString()
  {
    StringBuilder buffer = new StringBuilder();
    
    buffer.append("\nMelody part ");
    buffer.append(getTitle());
    buffer.append(":\n");
    buffer.append(super.toString());
    buffer.append("\nChord accompaniment:\n");
    buffer.append(chordPart.toString());
    
    return buffer.toString();
  }
}
