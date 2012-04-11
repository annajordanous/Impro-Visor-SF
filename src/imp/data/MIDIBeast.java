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
import java.util.ArrayList;

/**
 * Utilities for MIDI
 * @author Brandy McMenamee, Jim Herold, Robert Keller
 */
public class MIDIBeast{

/**
 * All of these variables, except for beat, outght to be specifically set before
 * each main style generator occurs. They are initialize to defaults in order to
 * avoid potential un-anticipated bugs.
 */
public static jm.music.data.Score score;
public static int numerator = 4;
public static int denominator = 4;
public static String midiFileName = "";
public static String chordFileName = "";
public static ArrayList<jm.music.data.Part> allParts = new ArrayList<jm.music.data.Part>();
public static jm.music.data.Part drumPart = new jm.music.data.Part();
public static jm.music.data.Part bassPart = new jm.music.data.Part();
public static jm.music.data.Part chordPart = new jm.music.data.Part();
public static int whole;
public static int half;
public static int halftriplet; // rk
public static int quarter;
public static int quartertriplet; //rk
public static int eighth;
public static int eighthtriplet;
public static int sixteenth;
public static int sixteenthtriplet;
public static int thirtysecond;
public static int thirtysecondtriplet;
public static int sixtyfourth;
public static int sixtyfourthtriplet;
public static int quarterquintuplet;
public static int eighthquintuplet;
public static int sixteenthquintuplet;
public static int thirtysecondquintuplet;
public static int beat = 120;
public static int minPrecision = 20;
public static int precision = minPrecision;
private static int roundThreshold = 20; // Used in rounding bass patterns
public static int slotsPerMeasure = 480;
public static int drumMeasureSize = 480;
// Note: The next three need to be in sync with the combo boxes 
// in gui.StyleEditor.java
public static double maxBassPatternLength = 4;  // default
public static double maxChordPatternLength = 4; // default
public static double maxDrumPatternLength = 4;  // default
public static String[] pitches =
  {
    "c", "c#", "d", "d#", "e", "f",
    "f#", "g", "g#", "a", "a#", "b"
  };
public static ArrayList<SlottedNote> originalBassNotes = new ArrayList<SlottedNote>();
public static ArrayList<String> originalBassRules = new ArrayList<String>();
public static ArrayList<String> originalChordRules = new ArrayList<String>();
public static ArrayList<String> errors;
public static ArrayList<String> savingErrors;
public static ArrayList<SlottedNote> originalChordNotes;
public static RepresentativeBassRules repBassRules;
public static RepresentativeDrumRules repDrumRules;
public static RepresentativeChordRules repChordRules;
//Generation Preferences
public static boolean showExtraction = true;
public static boolean chordTones = true;
public static boolean mergeBassRests = false;
public static boolean importDrums = true;
public static boolean importBass = true;
public static boolean importChords = true;
public static boolean doubleDrumLength = false;
public static ArrayList<RepresentativeBassRules.BassPatternObj> selectedBassRules;
public static ArrayList<RepresentativeDrumRules.DrumPattern> selectedDrumRules;
public static ArrayList<RepresentativeChordRules.ChordPattern> selectedChordRules;
public static boolean invoked = false;


static
  {
    invoke();
  }


/**
 * All drum instruments supported by general midi. The indices + 35 correspond
 * to the general midi number of the instrument (e.g.: index 0 is "acoustic Bass
 * Drum," which has midi number 35).
 *
 * These names are used in style files, so that spaces are avoided.
 */
public static final String[] spacelessDrumName =
  {
    "Acoustic_Bass_Drum",
    "Bass_Drum_1",
    "Side_Stick",
    "Acoustic_Snare",
    "Hand_Clap",
    "Electric_Snare",
    "Low_Floor_Tom",
    "Closed_Hi-Hat",
    "High_Floor_Tom",
    "Pedal_Hi-Hat",
    "Low_Tom",
    "Open_Hi-Hat",
    "Low-Mid_Tom",
    "Hi-Mid_Tom",
    "Crash_Cymbal_1",
    "High_Tom",
    "Ride_Cymbal_1",
    "Chinese_Cymbal",
    "Ride_Bell",
    "Tambourine",
    "Splash_Cymbal",
    "Cowbell",
    "Crash_Cymbal_2",
    "Vibraslap",
    "Ride_Cymbal_2",
    "Hi_Bongo",
    "Low_Bongo",
    "Mute_Hi_Conga",
    "Open_Hi_Conga",
    "Low_Conga",
    "High_Timbale",
    "Low_Timbale",
    "High_Agogo",
    "Low_Agogo",
    "Cabasa",
    "Maracas",
    "Short_Whistle",
    "Long_Whistle",
    "Short_Guiro",
    "Long_Guiro",
    "Claves",
    "Hi_Wood_Block",
    "Low_Wood_Block",
    "Mute_Cuica",
    "Open_Cuica",
    "Mute_Triangle",
    "Open_Triangle"
  };

public static void setResolution(int resolution)
  {
    minPrecision = resolution;
  }

/**
 * @param String midiFile
 * @param String chordFile This method needs to be called before anything else
 * is done with MIDIBeast. It will take the midi and chord file and get basic
 * info about the song, Time Signature, note rhythm values, and all instruments
 * found in the song.
 */

public static void initialize(String midiFile, String chordFile)
  {
    invoke();
    midiFileName = midiFile;
    chordFileName = chordFile;

    jm.util.Read.midi(score, midiFileName);

    numerator = score.getNumerator();
    denominator = score.getDenominator();

    jm.music.data.Part[] temp = score.getPartArray();

    for( int i = 0; i < temp.length; i++ )
      {
        allParts.add(temp[i]);
      }

    drumMeasureSize = slotsPerMeasure = (int) (beat * numerator);
    if( maxDrumPatternLength != 0.0 )
      {
        drumMeasureSize = (int) (maxDrumPatternLength * beat);
      }


    calculateNoteTypes(denominator);
  }


/**
 * Calls null constructors on various objects of this class
 */
        
public static void invoke()
  {
    errors = new ArrayList<String>();
    savingErrors = new ArrayList<String>();
    score = new jm.music.data.Score();
    allParts = new ArrayList<jm.music.data.Part>();
    calculateNoteTypes(denominator);
    invoked = true;
  }


/**
 * Changes the value of the denominator and recalculates the values of note
 * rhythms
 */

public static void changeDenomSig(int denom)
  {
    denominator = denom;
    calculateNoteTypes(denominator);
  }


/**
 * Changes the value of the numerator and recalculates the values of note
 * rhythms
 */

public static void changeNumSig(int num)
  {
    numerator = num;
    calculateNoteTypes(denominator);
  }


/**
 * Changes both the numerator and the denominator and recalculates the value of
 * note rhythms
 */

public static void changeTimeSig(int num, int denom)
  {
    numerator = num;
    denominator = denom;
    calculateNoteTypes(denominator);
  }


/**
 * Given a time signature and the number of slots specified per beat, this
 * method calulates how many slots each type of note should get. If a note type
 * is note possible it is assigned a value of -1.
 *
 * This and the next method were fouling on half note triplets and quarter note
 * triplets. I changed them on 12/1/2007. However, the whole thing should be
 * checked over carefully. RK
 */

public static void calculateNoteTypes(int denominator)
  {
    whole = 4*beat; // I don't think this is time signature dependent! denominator * beat;

    if( whole % 3 == 0 )
      {
        halftriplet = whole / 3;
        precision = halftriplet;
      }
    else
      {
        halftriplet = -1;
      }

    half = whole / 2;

    if( half % 3 == 0 )
      {
        quartertriplet = half / 3;
        precision = quartertriplet;
      }
    else
      {
        quartertriplet = -1;
      }

    quarter = whole / 4;

    precision = eighth = whole / 8;

    if( quarter % 3 == 0 )
      {
        eighthtriplet = quarter / 3;
        precision = eighthtriplet;
      }
    else
      {
        eighthtriplet = -1;
      }

    if( whole % 16 == 0 )
      {
        sixteenth = whole / 16;
        precision = sixteenth;
      }
    else
      {
        sixteenth = -1;
      }

    if( eighth % 3 == 0 )
      {
        sixteenthtriplet = eighth / 3;
        precision = sixteenthtriplet;
      }
    else
      {
        sixteenthtriplet = -1;
      }

    if( whole % 32 == 0 )
      {
        thirtysecond = whole / 32;
        precision = thirtysecond;
      }
    else
      {
        thirtysecond = -1;
      }

    if( sixteenth % 3 == 0 )
      {
        thirtysecondtriplet = sixteenth / 3;
        precision = sixteenth;
      }
    else
      {
        thirtysecondtriplet = -1;
      }

    if( whole % 64 == 0 )
      {
        sixtyfourth = whole / 64;
        precision = whole;
      }
    else
      {
        sixtyfourth = -1;
      }

    if( thirtysecond % 3 == 0 )
      {
        sixtyfourthtriplet = thirtysecond / 3;
        precision = sixtyfourthtriplet;
      }
    else
      {
        sixtyfourthtriplet = -1;
      }
    
    if( precision < minPrecision )
      {
        precision = minPrecision;
      }
  }

public static void calculateNoteTypes()
  {
    calculateNoteTypes(denominator);
  }


/**
 * @param numberOfSlots Takes an integer number of slots and returns a string
 * representation of it that Impro-Visor's style specification will understand.
 */

public static String stringDuration(int numberOfSlots)
  {
     if( numberOfSlots <= 0 )
      {
    System.out.print("stringDuration(" + numberOfSlots +")");
       String result = "";
        System.out.println("exception " + result);
        new Exception("non-positive duration").printStackTrace();
        return result;
      }
     
    //System.out.print("stringDuration(" + numberOfSlots +")");
    
    StringBuilder buffer = new StringBuilder();
    Note.getDurationString(buffer, numberOfSlots);
    String result = buffer.toString();

    //System.out.println(" = " + result);
    return buffer.toString().substring(1);


    /* This is the version from Brandy and Jim. It sometimes hangs.
     * It would have been better to exploit the existing technology in
     * Note.getDurationString.
     * 
    String s = "";
    if( numberOfSlots == 0 )
      {
        return "";
      }
    while( numberOfSlots > 0 )
      {
        if( numberOfSlots >= whole )
          {
            s += "1+";
            numberOfSlots -= whole;
          }
        else if( numberOfSlots >= half && half != -1 )
          {
            s += "2+";
            numberOfSlots -= half;
          }
        else if( numberOfSlots >= halftriplet && halftriplet != -1 )
          {
            s += "2/3+";
            numberOfSlots -= halftriplet;
          }
        else if( numberOfSlots >= quarter && quarter != -1 )
          {
            s += "4+";
            numberOfSlots -= quarter;
          }
        else if( numberOfSlots >= quartertriplet && quartertriplet != -1 )
          {
            s += "4/3+";
            numberOfSlots -= quartertriplet;
          }
        else if( numberOfSlots >= eighth && eighth != -1 )
          {
            s += "8+";
            numberOfSlots -= eighth;
          }
        else if( numberOfSlots >= eighthtriplet && eighthtriplet != -1 )
          {
            s += "8/3+";
            numberOfSlots -= eighthtriplet;
          }
        else if( numberOfSlots >= sixteenth && sixteenth != -1 )
          {
            s += "16+";
            numberOfSlots -= sixteenth;
          }
        else if( numberOfSlots >= sixteenthtriplet && sixteenthtriplet != -1 )
          {
            s += "16/3+";
            numberOfSlots -= sixteenthtriplet;
          }
        else if( numberOfSlots >= thirtysecond && thirtysecond != -1 )
          {
            s += "32+";
            numberOfSlots -= thirtysecond;
          }
        else if( numberOfSlots >= thirtysecondtriplet && thirtysecondtriplet != -1 )
          {
            s += "32/3+";
            numberOfSlots -= thirtysecondtriplet;
          }
        else if( numberOfSlots >= sixtyfourth && sixtyfourth != -1 )
          {
            s += "64+";
            numberOfSlots -= sixtyfourth;
          }
        else if( numberOfSlots >= sixtyfourthtriplet && sixtyfourthtriplet != -1 )
          {
            s += "64/3+";
            numberOfSlots -= sixtyfourthtriplet;
          }
      }
    String result = s.substring(0, s.length() - 1);
    
    System.out.println(" = " + result);
    return result;
    *
    */

  }




public static String spacelessDrumNameFromNumber(int number)
  {
    int index = number-35;
    
    if( index < 0 || index >= spacelessDrumName.length )
      {
        return "Unknown";
      }
    
    return spacelessDrumName[index];
  }


public static int numberFromSpacelessDrumName(String name)
  {
    for( int index = 0; index < spacelessDrumName.length; index++ )
      {
        if( name.equals(spacelessDrumName[index]) )
          {
            return index + 35;
          }
      }
    return -1; // not found
  }


/**
 * @param num Given an integer MIDI instrument number, this function returns the
 * String name of that instrument.
 */

public static String getInstrumentName(int num)
  {
    switch( num )
      {
        case 0:
            return "PIANO";
        case 3:
            return "HONKYTONK/HONKYTONK_PIANO";
        case 4:
            return "EPIANO/ELECTRIC_PIANO/ELPIANO";
        case 5:
            return "EPIANO2/DX_EPIANO";
        case 6:
            return "HARPSICHORD";
        case 7:
            return "CLAV/CLAVINET";
        case 8:
            return "CELESTE/CELESTA";
        case 9:
            return "GLOCKENSPIEL/GLOCK";
        case 10:
            return "MUSIC_BOX";
        case 11:
            return "VIBRAPHONE/VIBES";
        case 12:
            return "MARIMBA";
        case 13:
            return "XYLOPHONE";
        case 14:
            return "TUBULAR_BELL/TUBULAR_BELLS";
        case 16:
            return "ORGAN/ELECTRIC_ORGAN/";
        case 17:
            return "ORGAN2/JAZZ_ORGAN/HAMMOND_ORGAN";
        case 18:
            return "ORGAN3";
        case 19:
            return "CHURCH_ORGAN/PIPE_ORGAN";
        case 20:
            return "REED_ORGAN";
        case 21:
            return "ACCORDION/PIANO_ACCORDION/CONCERTINA";
        case 22:
            return "HARMONICA";
        case 23:
            return "BANDNEON";
        case 24:
            return "NYLON_GUITAR/NGUITAR/GUITAR/ACOUSTIC_GUITAR/AC_GUITAR";
        case 25:
            return "STEEL_GUITAR/SGUITAR";
        case 26:
            return "JAZZ_GUITAR/JGUITAR";
        case 27:
            return "CLEAN_GUITAR/CGUITAR/ELECTRIC_GUITAR/EL_GUITAR";
        case 28:
            return "MUTED_GUITAR/MGUITAR";
        case 29:
            return "OVERDRIVE_GUITAR/OGUITAR";
        case 30:
            return "DISTORTED_GUITAR/DGUITAR/DIST_GUITAR";
        case 31:
            return "GUITAR_HARMONICS/GT_HARMONICS/HARMONICS";
        case 32:
            return "ACOUSTIC_BASS/ABASS";
        case 33:
            return "FINGERED_BASS/BASS/FBASS/ELECTRIC_BASS/EL_BASS/EBASS";
        case 34:
            return "PICKED_BASS/PBASS";
        case 35:
            return "FRETLESS_BASS/FRETLESS";
        case 36:
            return "SLAP_BASS/SBASS/SLAP";
        case 38:
            return "SYNTH_BASS";
        case 40:
            return "VIOLIN";
        case 41:
            return "VIOLA";
        case 42:
            return "CELLO/VIOLIN_CELLO";
        case 43:
            return "CONTRABASS/CONTRA_BASS/DOUBLE_BASS";
        case 44:
            return "TREMOLO_STRINGS/TREMOLO";
        case 45:
            return "PIZZICATO_STRINGS/PIZZ/PITZ/PSTRINGS";
        case 46:
            return "HARP";
        case 47:
            return "TIMPANI/TIMP";
        case 48:
            return "STRINGS/STR";
        case 51:
            return "SLOW_STRINGS";
        case 50:
            return "SYNTH_STRINGS/SYN_STRINGS";
        case 52:
            return "AAH/AHHS/CHOIR";
        case 53:
            return "OOH/OOHS/VOICE";
        case 54:
            return "SYNVOX/VOX";
        case 55:
            return "ORCHESTRA_HIT";
        case 56:
            return "TRUMPET";
        case 57:
            return "TROMBONE";
        case 58:
            return "TUBA";
        case 59:
            return "MUTED_TRUMPET";
        case 60:
            return "FRENCH_HORN/HORN";
        case 61:
            return "BRASS";
        case 62:
            return "SYNTH_BRASS";
        case 64:
            return "SOPRANO_SAX/SOPRANO/SOPRANO_SAXOPHONE/SOP";
        case 65:
            return "ALTO_SAX/ALTO/ALTO_SAXOPHONE";
        case 66:
            return "TENOR_SAX/TENOR/TENOR_SAXOPHONE/SAX/SAXOPHONE";
        case 67:
            return "BARITONE_SAX/BARI/BARI_SAX/BARITONE/BARITONE_SAXOPHONE";
        case 68:
            return "OBOE";
        case 69:
            return "ENGLISH_HORN";
        case 70:
            return "BASSOON";
        case 71:
            return "CLARINET/CLAR";
        case 72:
            return "PICCOLO/PIC/PICC";
        case 73:
            return "FLUTE";
        case 74:
            return "RECORDER";
        case 75:
            return "PAN_FLUTE/PANFLUTE";
        case 76:
            return "BOTTLE_BLOW/BOTTLE";
        case 77:
            return "SHAKUHACHI";
        case 78:
            return "WHISTLE";
        case 79:
            return "OCARINA";
        case 80:
            return "SQUARE_WAVE/SQUARE";
        case 81:
            return "SAW_WAVE/SAW/SAWTOOTH";
        case 82:
            return "SYNTH_CALLIOPE/CALLOPE/SYN_CALLIOPE";
        case 83:
            return "CHIFFER_LEAD/CHIFFER";
        case 84:
            return "CHARANG";
        case 85:
            return "SOLO_VOX";
        case 88:
            return "FANTASIA";
        case 89:
            return "WARM_PAD/PAD";
        case 90:
            return "POLYSYNTH/POLY_SYNTH";
        case 91:
            return "SPACE_VOICE";
        case 92:
            return "BOWED_GLASS";
        case 93:
            return "METAL_PAD";
        case 94:
            return "HALO_PAD/HALO";
        case 95:
            return "SWEEP_PAD/SWEEP";
        case 96:
            return "ICE_RAIN/ICERAIN";
        case 97:
            return "SOUNDTRACK";
        case 98:
            return "CRYSTAL";
        case 99:
            return "ATMOSPHERE";
        case 100:
            return "BRIGHTNESS";
        case 101:
            return "GOBLIN";
        case 102:
            return "ECHO_DROPS/DROPS/ECHOS/ECHO/ECHO_DROP";
        case 103:
            return "STAR_THEME";
        case 104:
            return "SITAR";
        case 105:
            return "BANJO";
        case 106:
            return "SHAMISEN";
        case 107:
            return "KOTO";
        case 108:
            return "KALIMBA/THUMB_PIANO";
        case 109:
            return "BAGPIPES/BAG_PIPES/BAGPIPE/PIPES";
        case 110:
            return "FIDDLE";
        case 111:
            return "SHANNAI";
        case 112:
            return "TINKLE_BELL/BELL/BELLS";
        case 113:
            return "AGOGO";
        case 114:
            return "STEEL_DRUMS/STEELDRUMS/STEELDRUM/STEEL_DRUM";
        case 115:
            return "WOODBLOCK/WOODBLOCKS";
        case 116:
            return "TAIKO/DRUM";
        case 118:
            return "SYNTH_DRUM/SYNTH_DRUMS";
        case 119:
            return "TOM/TOMS/TOM_TOM/TOM_TOMS/REVERSE_CYMBAL/CYMBAL";
        case 120:
            return "FRETNOISE/FRET/FRETS";
        case 121:
            return "BREATHNOISE/BREATH";
        case 122:
            return "SEASHORE/SEA/RAIN/THUNDER/WIND/STREAM/SFX/SOUNDEFFECTS/SOUNDFX";
        case 123:
            return "BIRD";
        case 124:
            return "TELEPHONE/PHONE";
        case 125:
            return "HELICOPTER";
        case 126:
            return "APPLAUSE";
        default:
            return "invalid number";
      }
  }


/**
 * @param pitchNumber Returns the letter representation of a note given a MIDI
 * pitch number.
 */

public static String pitchOf(int pitchNumber)
  {
    int i = pitchNumber % 12;
    try
      {
        return pitches[i];
      }
    catch( ArrayIndexOutOfBoundsException e )
      {
      }

    return "r"; //rest
  }


/**
 * @param duration
 * @param precision Rounds a rhythm in double form to slots with a given
 * precision.
 */

public static int findSlots(double duration, int precision)
  {
    int slots = (int) Math.round(beat * duration / precision) * precision;
 //System.out.println("duration " + duration + " -> " + slots + " slots, precision " + precision);
    return slots;
  }


/**
 * @param d Takes a double rhythm duration and turns it into an integer value
 * representing how many slots that note takes up.
 */

public static int doubleValToSlots(double duration)
  {
    return findSlots(duration, precision);
  }


/**
 * @param s Returns the number of slots a given string rhythm representation
 * takes.
 */

public static int getSlotValueForElement(String s)
  {
    String[] split = s.split("\\+");
    int slotTotal = 0;
    for( int i = 0; i < split.length; i++ )
      {
        slotTotal += getSlotValueFor(split[i]);
      }
    return slotTotal;
  }


public static boolean belowRoundingThreshold(String s)
  {
    return getSlotValueForElement(s) < roundThreshold;
  }

/**
 * @param ruleElement Takes a rule element (eg X8+32) and returns just the
 * rhythm part of it. (eg 8+32).
 */

public static String getRhythmString(String ruleElement)
  {
    if( ruleElement.length() == 0 ) // This CAN happen!
      {
        return "";
      }

    if( ruleElement.charAt(ruleElement.length() - 1) == ')' )
      {
        ruleElement = ruleElement.substring(0, ruleElement.length() - 1);
      }


    int rhythmIndex = ruleElement.indexOf(")");
    if( rhythmIndex == -1 )
      {
        for( int i = 0; i < ruleElement.length(); i++ )
          {
            if( ruleElement.charAt(i) > 47 && ruleElement.charAt(i) < 58 )
              {
                rhythmIndex = i;
                break;
              }
          }
      }
    else
      {
        rhythmIndex++;
      }

    // FIX!!! The following can generate an index out of range exception.
    // Putting on a temporary wrapper that returns "" for now.

    try
      {
        return ruleElement.substring(rhythmIndex, ruleElement.length());
      }
    catch( StringIndexOutOfBoundsException e )
      {
      }

    return "";
  }


/**
 * @param removeIndex
 * @param s Takes an array of Strings and removes the specified index
 */

public static String[] removeRule(int removeIndex, String[] s)
  {
    String[] toReturn = new String[s.length - 1];
    for( int i = 0, index = 0; i < s.length; i++ )
      {
        if( i != removeIndex )
          {
            toReturn[index++] = s[i];
          }
      }
    return toReturn;
  }


/**
 * @param rhythmString
 * @param elementString Takes a rule element(eg X8) and adds a rhythm element to
 * it (eg 32) TODO: Simplify the string after the rhythm is added
 */

public static String addRhythm(String rhythmString, String elementString)
  {
    return elementString + "+" + rhythmString;
  }


/**
 * @param rule Finds how many slots an entire string bass rule contains
 */

public static int numBeatsInBassRule(String rule)
  {
    int slotCount = 0;
    try
      {
        if( rule.length() == 0 )
          {
            return -1;
          }
        String[] split = rule.split(" ");
        for( int i = 0; i < split.length; i++ )
          {
            String rhythmString = "";
            if( split[i].equals("(X") )
              {
                rhythmString = getRhythmString(split[i + 2]);
                slotCount += getSlotValueForElement(rhythmString);
                i += 2;
              }
            else
              {
                rhythmString = getRhythmString(split[i]);
                slotCount += getSlotValueForElement(rhythmString);
              }
          }
      }
    catch( Exception e )
      {
        e.printStackTrace();
      }
    return slotCount;

  }
        

/**
 * @param s Takes a single string rhythm value (ie 8 or 16, not 16+32) and
 * returns the number of slots it contains
 */

public static int getSlotValueFor(String s)
  {
    return Duration.getDuration0(s);

  }


/**
 * @param s
 * @param note Takes the pitch number from a provided note object and returns a
 * String representation of it (eg D++)
 */

public static String pitchToString(String s, Note note)
  {
    if( note.getPitch() < 0 )
      {
        return "r";
      }
    int offset = 57;
    char octave = '+';
    s += pitchOf(note.getPitch() % 12);
    if( note.getPitch() > 71 || note.getPitch() < 60 )
      {
        if( note.getPitch() > 71 )
          {
            offset = 71;
            octave = '+';
          }
        else if( note.getPitch() < 60 )
          {
            offset = 60;
            octave = '-';
          }
        int lcv = (((offset - note.getPitch()) / 12) + 1);
        for( int i = 0; i < lcv; i++ )
          {
            s += octave;
          }
      }
    return s;
  }


/**
 * @param r Takes a string rule and returns the rhythm value in double form.
 */

public static double numBeatsInRule(String r)
  {
    ArrayList<String> rhythms = MIDIBeast.stripPitch(r);
    double sum = 0;
    for( int i = 0; i < rhythms.size(); i++ )
      {
        String[] note = rhythms.get(i).split("\\+");
        for( int j = 0; j < note.length; j++ )
          {
            double curVal = MIDIBeast.getSlotValueFor(note[j]);
            if( curVal == -1 )
              {
                return -1;
              }
            sum += curVal;
          }
      }
    return sum;
  }


/**
 * @param s - a bass rule
 * @return s without any octave or pitch value information
 */

public static ArrayList<String> stripPitch(String s)
  {
    String[] delimited = s.split(" ");
    ArrayList<String> rule = new ArrayList<String>();
    for( int i = 0; i < delimited.length; i++ )
      {
        String value = "";
        for( int j = 1; j < delimited[i].length(); j++ )
          {
            if( delimited[i].charAt(j) > 47 && delimited[i].charAt(j) < 58 && delimited[i].charAt(j - 1) != '(' )
              {
                value = delimited[i].substring(j, delimited[i].length());
                break;
              }
          }
        if( value.length() != 0 && value.charAt(value.length() - 1) == ')' )
          {
            value = value.substring(0, value.length() - 1);
          }

        rule.add(value);
      }
    return rule;
  }

public static void addError(String error)
  {
    errors.add(error);
  }

public static void savingErrors(String error)
  {
    savingErrors.add(error);
  }

public static void newSave()
  {
    savingErrors = new ArrayList<String>();
  }

public static void addSaveError(String error)
  {
    savingErrors.add(error + "\n");
  }


}

