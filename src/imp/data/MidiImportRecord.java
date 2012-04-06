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

/**
 * Records relevant information about one track, including
 * the Impro-Visor MelodyPart to which the track is translated
 * @author keller
 */
public class MidiImportRecord implements Comparable
{
int channel;
int trackNumber;
MelodyPart melodyPart;

public MidiImportRecord(int channel, int trackNumber, MelodyPart melodyPart)
{
  this.channel = channel;
  this.trackNumber = trackNumber;
  this.melodyPart = melodyPart;
}
   
public MelodyPart getPart()
  {
    return melodyPart;
  }

@Override
public String toString()
  {
  return "channel " + (channel+1) + ", track " + trackNumber + ": " + melodyPart.toString();   
  }

@Override
public int compareTo(Object ob)
  {
   if( !(ob instanceof Comparable) )
     {
       return -1;
     }
   MidiImportRecord that = (MidiImportRecord)ob;
   if( channel < that.channel )
     {
       return -1;
     }
   if( channel == that.channel )
     {
       if( trackNumber < that.trackNumber )
         {
           return -1;
         }
       if( trackNumber == that.trackNumber )
         {
           return 0;
         }
     }
   return 1;
  }
}
