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

import java.util.ArrayList;
import jm.music.data.Phrase;

/**
 * ImportMelody is adapted by Robert Keller from ImportBass, 
 * by Brandy McMenamy and James Herold 7/18/2007
 * Reads and parses a channel of a midi file as a melody line.
 * The program assumes that all notes are played in sequential order (no
 * chords) and therefore only reads the first Phrase.
 * It then interprets the rhythm duration of each note as the closest
 * musical value that can be represented by 120 slots per beat for the
 * given time signature.  Pitches are kept as integers representing
 * Hertz values.  The final melody was originally stored in 
 * MIDIBeast.originalBassNotes, which of course breaks encapsulation.
 */

public class ImportMelody{

        private ArrayList<Note> originalMelodyNotes = new ArrayList<Note>();
        private jm.music.data.Score score;
        private jm.music.data.Part parts[];
        
	boolean debug = false;
        /**
         * false if an error occurs that is serious enough that the rest of the
         * style generation for the melody line must be prevented from firing.
         **/
        public boolean canContinue = true;
	/**
	 * The phrase of sequential, non-chordal notes for the melody line
	 */
	private jm.music.data.Phrase mainPhrase;
	/**
	 * The initial notes from the melody line
	 */
	private ArrayList<jm.music.data.Note> noteArray = new ArrayList<jm.music.data.Note>();
	/**
	 * The final notes that are used as the "original notes" by each
	 * successive step in the style generation.  (Contains corrected
	 * rhythm durations).
	 */
	private ArrayList<Note> roundedNoteArray = new ArrayList<Note>();
	/**
	 * constructor.  Reads a score and adjusts the rhythm durations
	 * in the melody line.
	 */
	public ImportMelody(jm.music.data.Score score){
            this.score = score;
            parts = score.getPartArray();
	}
        
        public String convertToImpString(jm.music.data.Part melodyPart) {
            noteArray = new ArrayList<jm.music.data.Note>();
	    roundedNoteArray = new ArrayList<Note>();

                try{
                    mainPhrase = melodyPart.getPhraseArray()[0];
                    getNoteArray();
                    //if(MIDIBeast.mergeMelodyRests) mergeRests();
                    
                    roundDurations(MIDIBeast.precision);
                    if(roundedNoteArray.size() == 0) {
                        System.out.println("note array of size zero, unable to continue");   
                    }
                    else
                        originalMelodyNotes = roundedNoteArray;
                    }
                catch(ArrayIndexOutOfBoundsException e) {
                        System.out.println("An array index out of bounds exception was raised");

                }
           StringBuilder buffer = new StringBuilder();
           
                for( int i = 0; i < noteArray.size(); i++ )
                  {
                    jm.music.data.Note noteIn = noteArray.get(i);
                    buffer.append(convertToImpNote(noteIn).toLeadsheet().toString());
                    buffer.append(" ");
                  }
                
            return buffer.toString();
            }

        
        public static Note convertToImpNote(jm.music.data.Note noteIn)
          {
           int pitch = noteIn.getPitch();
           int numberOfSlots = MIDIBeast.doubleValToSlots(noteIn.getRhythmValue());
           if( pitch < 0 )
             {
               return new Rest(numberOfSlots);
             }
           
           Note note = new Note(pitch, numberOfSlots);
           return note;
          }	
//         
//       /**
//         * @param startMeasure
//         * @param selectedPart
//         * Constructor used when the start measure and part are specified
//         * by the user
//         */
//        public ImportMelody(double startBeat, jm.music.data.Part selectedPart){
//            noteArray = new ArrayList<jm.music.data.Note>();
//            roundedNoteArray = new ArrayList<SlottedNote>();
//            melodyPart = selectedPart;
//            mainPhrase = melodyPart.getPhraseArray()[0];
//            setPhraseStartAndEnd(startBeat,0);
//            getNoteArray();
//            roundDurations(MIDIBeast.precision);
//		
//            originalMelodyNotes = roundedNoteArray;
//        }
//        
//        public ImportMelody(double startBeat, double endBeat){
//            noteArray = new ArrayList<jm.music.data.Note>();
//            roundedNoteArray = new ArrayList<SlottedNote>();
//            createMelodyPart();
//            mainPhrase = melodyPart.getPhraseArray()[0];
//            if(debug){
//                System.out.println("## Before changing start position ##");
//                System.out.println(mainPhrase);
//            }
//            setPhraseStartAndEnd(startBeat, endBeat);
//            getNoteArray();
//            roundDurations(MIDIBeast.precision);
//		
//            originalMelodyNotes = roundedNoteArray;
//        }
//                
//        public ImportMelody(double startBeat, double endBeat, jm.music.data.Part selectedPart){
//            noteArray = new ArrayList<jm.music.data.Note>();
//            roundedNoteArray = new ArrayList<SlottedNote>();
//            melodyPart = selectedPart;
//            mainPhrase = melodyPart.getPhraseArray()[0];
//            if(debug){
//                System.out.println("## Before changing start position ##");
//                System.out.println(mainPhrase);
//            }
//            setPhraseStartAndEnd(startBeat, endBeat);
//            getNoteArray();
//            roundDurations(MIDIBeast.precision);
//		
//            originalMelodyNotes = roundedNoteArray;
//        }
        
       /**
         * @param startMeasure
         * @param endMeasure
         * This method chops off the beginning and end of the main phrase
         * to match the user selected start and end measures
         */
        public void setPhraseStartAndEnd(double startBeat, double endBeat){
            jm.music.data.Note[] noteArray = mainPhrase.getNoteArray();
            double beatCount = 0;
            int noteIndex = 0, endNoteIndex = 0, startNoteIndex = 0; 
            if(endBeat == 0) endBeat = mainPhrase.getEndTime();
            boolean start = false;
            while(beatCount < endBeat && noteIndex < noteArray.length){
                beatCount += noteArray[noteIndex].getRhythmValue();
                if(beatCount > startBeat && !start){
                    double remainder = beatCount - startBeat;
                    noteArray[noteIndex] = new jm.music.data.Note(noteArray[noteIndex].getPitch(), remainder);
                    startNoteIndex = noteIndex;
                    start = true;
                }
                noteIndex++;
            }
            endNoteIndex = noteIndex;
            jm.music.data.Note[] newNoteArray = new jm.music.data.Note[endNoteIndex - startNoteIndex];
            for(int i = startNoteIndex, j = 0; i < endNoteIndex; i++, j++)
                newNoteArray[j] = noteArray[i];
            mainPhrase = new Phrase(startBeat);
            mainPhrase.addNoteList(newNoteArray);
             if(debug){
             System.out.println("## After setPhraseStartAndEnd() ##");
             System.out.println(mainPhrase);
            }
            
        }
        
      int size()
        {
          return parts.length;
        }

      /**
	* Finds the ith melody instrument and returns the part
        * 
 	* NOTE: This method could be a lot more intelligent, such as
	* find the longest melody part or "the most likely melody part".
        */
	public jm.music.data.Part getPart(int i){

            return parts[i];

	}
	
	/**
	 * Reads the notes of the main phrase into noteArray
	 */
	public void getNoteArray(){
		jm.music.data.Note[] notes = mainPhrase.getNoteArray();
		for(int i = 0; i < notes.length; i++)
			noteArray.add(notes[i]);
		
	}
	
        public void mergeRests(){
            for(int i = 1; i < noteArray.size(); i ++){
                if(!(noteArray.get(i).isRest())) continue;
                noteArray.get(i-1).setRhythmValue(noteArray.get(i).getRhythmValue() + noteArray.get(i-1).getRhythmValue());
                noteArray.remove(i);
                i--;
            }
        }
	/**
	 * Rounds the rhythm duration of each note to its closest musical value
	 * equivalent using a 120 slots per beat format.
	 * @param precision
	 */
	public void roundDurations(int precision){
		for(int i = 0; i < noteArray.size(); i++){
                        Note noteOut = convertToImpNote(noteArray.get(i));
//			int numberOfSlots = MIDIBeast.doubleValToSlots(noteArray.get(i).getRhythmValue());
//			String pitch = MIDIBeast.pitchOf(noteArray.get(i).getPitch());
//			SlottedNote toBeAdded = new SlottedNote(numberOfSlots, pitch);
			roundedNoteArray.add(noteOut);
		}
		
		if(debug) {
			System.out.println("## After roundDurations() ##");
			int totalNoteDuration = 0;
                        for(int i = 0; i < roundedNoteArray.size(); i++) {
				System.out.println(roundedNoteArray.get(i));
                                totalNoteDuration += roundedNoteArray.get(i).getRhythmValue();
			}
                        System.out.println("Total note duration: " + totalNoteDuration);
                                 
		}
	}
        

            
}