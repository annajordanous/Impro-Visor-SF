/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2011 Robert Keller and Harvey Mudd College
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

package imp.cykparser;
import imp.brickdictionary.*;
import imp.data.ChordSymbol;
import imp.roadmap.*;
import imp.util.ErrorLog;
import java.util.ArrayList;
import java.util.Arrays;
import polya.*;


/**
 * purpose: gain further information from list of blocks
 * @author Zachary Merritt
 */
public class PostProcessor {
    
    /**
     * Temporary fix to static method issues
     */

    private static EquivalenceDictionary dict = null;
        
    public static final int DOM_ADJUST = 5; // adjustment to root (by semitones)
                                            // when dealing with dominants
    
    public static final int OCTAVE = 12;    // semitones in octave
    
    public static final int FOURTH = 5;      // perfect fourth in semitones
    
    // Names of joins, arranged by difference in keys between which they
    // transition (with reference to dominant in first block)
    
    public static final String[] JOINS = {"Bootstrap", "Stella", "Backslider", 
        "Half Nelson", "Sidewinder", "New Horizon", "Downwinder", "Homer", 
        "Cherokee", "Woody", "Highjump", "Bauble"};
    
//    public static final String[] RESOLUTIONS = {"","Happenstance","Yardbird","",
//        "","","","","","","","Tritone"};
    
    // For launching other than straight across a section
    
    public static final String[] RESOLUTIONS = {"", "", "", "", "", "", "Tritone", "", "Happenstance", "Yardbird", "", ""};

    //public static String[] FIRST_UNSTABLE = {"Approach", "Launcher"};

    public static String[] FIRST_STABLE = {"Cadence", "CESH", /* "Dropback", */ "Ending", 
        "On", /* "On-Off", */ "On-Off+", "Opening", "Overrun"};

    public static String[] SECOND_UNSTABLE = {"Approach", "Cadence", "Launcher", "Misc", "Pullback", "Turnaround"};
    
    // Rules for finding representative chord in diatonicChordCheck
    private static ArrayList<Polylist> equivalenceRules;
    
    // Rules for which chords are diatonic depending on mode
    private static ArrayList<Polylist> diatonicRules;
    
    // Introduced to avoid reading the dictionary repeatedly in checkJoinability.
    
    static
      {
        setEquivalenceDictionary();
      }
    
    /** Default constructor
     * Constructs a new PostProcessor with empty rules lists
     */
    public PostProcessor() {
        equivalenceRules = new ArrayList<Polylist>();
        diatonicRules = new ArrayList<Polylist>();
    }
    
    /** PostProcessor / 2
     * Create rules lists from input
     * @param e : ArrayList of Polylists, each one of which is an equivalence rule
     * @param d : ArrayList of Polylists, each one of which is a diatonic rule
     */
    public PostProcessor(ArrayList<Polylist> e, ArrayList<Polylist> d) {
        equivalenceRules = e;
        diatonicRules = d;
    }
    
    /** getEquivalenceRules
     * Get the stored equivalence rules
     * @return a set of equivalence rules (ArrayList of Polylists)
     */
    public ArrayList<Polylist> getEquivalenceRules() {
        return equivalenceRules;
    }
    
    /** getDiatonicRules
     * Get the stored diatonic rules
     * @return a set of diatoniv rules (ArrayList of Polylists)
     */
    public ArrayList<Polylist> getDiatonicRules() {
        return diatonicRules;
    }
    
    /** setEquivalenceRules
     * set the equivalence rules based on input
     * @param e : a set of equivalence rules (ArrayList of Polylists)
     */
    public void setEquivalenceRules(ArrayList<Polylist> e) {
        equivalenceRules = e;
    }
    
    /** setDiatonicRules
     * set the diatonic rules based on input
     * @param e : a set of diatonic rules (ArrayList of Polylists)
     */
    public void setDiatonicRules(ArrayList<Polylist> d) {
        diatonicRules = d;
    }
    
/** 
 * Group consecutive block of same key for overarching key sections.
 * @param roadmap : a RoadMap
 * @return newMap : an altered RoadMap
 */
    
public static RoadMap findKeys(RoadMap roadmap)
  {
    //System.out.println("findKeys in " + roadmap);

    ArrayList<KeySpan> keymap = new ArrayList<KeySpan>();

    // Initialize key, mode, and duration of current block
    KeySpan current = new KeySpan();
    ArrayList<Block> blocks = roadmap.getBlocks();

    // Check for an empty roadmap
    if( blocks.isEmpty()
            || // special case for a new leadsheet
            (blocks.size() == 1 && blocks.get(0).isChord()
            && ((ChordBlock) blocks.get(0)).getChord().isNOCHORD()) )
      {
        roadmap.getKeyMap().clear();
        return roadmap;
      }

    // Create array so we can loop through correctly
    Block[] blockArray = blocks.toArray(new Block[0]);

    boolean ncFlag = false;
    int ncDuration = 0;

    int index = 1;
    Block lastBlock = blockArray[blockArray.length - index];

    while( lastBlock.isChord() && ((ChordBlock) lastBlock).getChord().isNOCHORD() )
      {
        ncDuration += lastBlock.getDuration();
        index++;
        lastBlock = blockArray[blockArray.length - index];
      }

    // Initialize KeySpan using last block
    
    current.setKey(lastBlock.getKey());
    current.setMode(lastBlock.getMode());
    current.setDuration(lastBlock.getDuration() + ncDuration);
    
    ncDuration = 0;

    // Loop through blocks backwards, 
    for( int i = blockArray.length - index - 1; i >= 0; i-- )
      {
        Block thisBlock = blockArray[i];

        // Create new KeySpan for new section. 

        if( thisBlock.isSectionEnd() )
          {
            // Note that a section end can still consist of a single chord

            KeySpan entry = current;
            keymap.add(0, entry);

            current = new KeySpan(thisBlock);

            if( thisBlock.isChord() )
              {
                ChordBlock c = (ChordBlock) thisBlock;

                if( diatonicChordCheck(c, entry.getKey(), entry.getMode()) )
                  {
                    current.setKey(entry.getKey());
                    current.setMode(entry.getMode());
                  }
                // End of current key -- add to the list
                else
                  {
                    current = new KeySpan(c);
                  }
              }
            // Match mode to second block if first block is an approach or
            // launcher that resolves to second block.
            else if( isApproachOrLauncher(thisBlock) )
              {
                ChordBlock cFirst = thisBlock.getLastChord();

                ChordBlock cSecond = blockArray[i + 1].getFirstChord();

                if( doesResolve(cFirst, cSecond) )
                  {
                    thisBlock.setMode(cSecond.getMode());
                  }
              }

            if( ncFlag )
              {
                current.augmentDuration(ncDuration);
                ncFlag = false;
                ncDuration = 0;
              }
          }
        // Case in which first block is a brick
        else if( thisBlock instanceof Brick )
          {
            // Check if current block can roll into current KeySpan
            if( current.getKey() == thisBlock.getKey()
             && current.getMode().equals(thisBlock.getMode()) )
              {
                current.augmentDuration(thisBlock.getDuration());
              }
            // Match mode to second block if first block is an approach or
            // launcher that resolves to second block
            else if( isApproachOrLauncher(thisBlock) )
              {
                ChordBlock cFirst = thisBlock.getLastChord();

                ChordBlock cSecond = blockArray[i + 1].getFirstChord();

                if( doesResolve(cFirst, cSecond) )
                  {
                    thisBlock.setMode(cSecond.getMode());
                    current.augmentDuration(thisBlock.getDuration());
                  }
                else
                  {
                    KeySpan entry = current;
                    keymap.add(0, entry);

                    current = new KeySpan(thisBlock);

                    if( ncFlag )
                      {
                        current.augmentDuration(ncDuration);
                        ncFlag = false;
                        ncDuration = 0;
                      }
                  }
              }
            // End of current key -- add to the list
            else
              {
                KeySpan entry = current;
                keymap.add(0, entry);

                current = new KeySpan(thisBlock);

                if( ncFlag )
                  {
                    current.augmentDuration(ncDuration);
                    ncFlag = false;
                    ncDuration = 0;
                  }
              }
          }
        // Case in which first block is a chord
        else
          {
            ChordBlock c = (ChordBlock) thisBlock;

            if( c.getChord().isNOCHORD() )
              {
                ncDuration += c.getDuration();
                ncFlag = true;
              }
            // Check if chord is diatonically within current KeySpan
            else if( diatonicChordCheck(c, current.getKey(), current.getMode()) )
              {
                current.augmentDuration(c.getDuration());
              }
            // End of current key -- add to the list
            else
              {
                KeySpan entry = current;
                keymap.add(0, entry);

                current = new KeySpan(c);
                if( ncFlag )
                  {
                    current.augmentDuration(ncDuration);
                    ncFlag = false;
                    ncDuration = 0;
                  }
              }
          }
      }

    // Special case for NC chord at beginning of song
    if( ncFlag )
      {
        keymap.add(0, current);
        current = new KeySpan(-1, "", ncDuration);
      }
    
    // Add first KeySpan in song to list
    
    keymap.add(0, current);
    blocks = new ArrayList<Block>(Arrays.asList(blockArray));

    // Replace current RoadMap with one that has properly merged KeySpans
    
    RoadMap newMap = new RoadMap(blocks);
    newMap.setKeyMap(keymap);

    return newMap;
  }


    /** 
     * Analyze ArrayList of Blocks to find approaches that could be launchers.
     * @param blocks : ArrayList of blocks
     * @return alteredList : ArrayList of blocks adjusted for any launchers 
     *                       found
     */

public static ArrayList<Block> findLaunchers(ArrayList<Block> blocks)
  {
    // Rebuilding original list, but with launchers in the appropriate 
    // places
    ArrayList<Block> alteredList = new ArrayList<Block>();

    for( int i = 0; i < blocks.size(); i++ )
      {

        Block thisBlock = blocks.get(i);
        
        //System.out.println("thisBlock = " + thisBlock);

        // If the current block is a brick, check if it could be a launcher
        if( thisBlock instanceof Brick )
          {
            Brick b = (Brick) thisBlock;
            ArrayList<ChordBlock> chordList = new ArrayList<ChordBlock>();

            Block postBlock;

            // If the brick is not the last one in the list, get chords from
            // next block.

            if( i != blocks.size() - 1 )
              {
                postBlock = blocks.get(i + 1);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }
            // Otherwise, loop around and get chords from first block
            else
              {
                postBlock = blocks.get(0);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }

            String brickName = b.getName();

            if( brickName.equals("Straight Approach") )
              {
               
//                String altResolution = getAlternateResolution(b, chordList.get(0));
                String altResolution = getAlternateResolution(b.getKey(), postBlock.getKey());
                
                //System.out.println(b + " vs " + postBlock + " altResolution = " + altResolution);
                
                 if( !altResolution.isEmpty() )
                  {
                    brickName = brickName.replace("Straight", altResolution);
                    b.setName(brickName);
                    b.setKey(postBlock.getKey());
                    b.setMode(postBlock.getMode());
                  }
              }

            // Check if brick is an approach is actually a Launcher.
            // In "Insights" examples, resolution is not required.

            if( b.getType().equals("Approach") && b.isSectionEnd()
                    /* && doesResolve(b, chordList.get(0))*/ ) 
              {
                // If the name has "Approach", replace it with "Launcher"
                if( brickName.contains("Approach") )
                  {
                    brickName = brickName.replace("Approach", "Launcher");
                  }
                // If not, append "(Launcher)" to the end
                else
                  {
                    // no for now: brickName = brickName + " (Launcher)";
                  }

                b.setName(brickName);
                b.setType("Launcher");

                // Add altered brick to the list
                alteredList.add(b);
              }
            // If brick is not an approach or does not resolve, add it to 
            // the list 
            else
              {
                alteredList.add(b);
              }
          }
        // Case in which current block is a dominant chord:
        else if( ((ChordBlock)thisBlock).isDominant() )
          {

            ChordBlock c = (ChordBlock) thisBlock;
            Block postBlock;
            ArrayList<ChordBlock> chordList;

            // If the brick is not the last one in the list, get chords from
            // next block.

            if( i != blocks.size() - 1 )
              {
                postBlock = blocks.get(i + 1);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }
            // Otherwise, loop around and get chords from first block.
            else
              {
                postBlock = blocks.get(0);
                chordList = (ArrayList<ChordBlock>) postBlock.flattenBlock();
              }

            String altResolution = getAlternateResolution(c, chordList.get(0));

            Block b;
            if( !altResolution.isEmpty() )
              {
                b = new Brick(c, altResolution, "Dominant");
              }
            else
              {
                b = new ChordBlock(c);
              }

            // Check for single-chord launcher.

            if( doesResolve(c, chordList.get(0)) && c.isSectionEnd() )
              {
                Brick uniLauncher = new Brick(c, chordList.get(0).getMode());

                alteredList.add(uniLauncher);
              }
            else
              {
                alteredList.add(b);
              }
          }
        // If the block is a non-dominant chord, add it to the list
        else
          {
            alteredList.add(thisBlock);
          }
      }
    return alteredList;
  }
   
    
    /**
     * Change the resolution of a Straight approach to some other kind,
     * depending on the target
     * @param approach
     * @param target
     * @return 
     */
public static String getAlternateResolution(Block approach, ChordBlock target)
  {
    //System.out.println("getAlternate " + approach + " to " + target);
    
    int BIAS = 7;
    long domRoot = approach.getLastChord().getKey();
    long resRoot = BIAS + target.getKey();

    return getAlternateResolution(domRoot, resRoot);
  }

public static String getAlternateResolution(long domRoot, long resRoot)
  {
    int domRootInt = Long.valueOf(domRoot).intValue();
    int resRootInt = Long.valueOf(resRoot).intValue();

    int diff = (resRootInt - domRootInt + OCTAVE) % OCTAVE;

    //System.out.println("domRoot = " + domRoot + ", resRoot = " + resRoot + " diff = " + diff);

    String altResolution = RESOLUTIONS[diff];

    return altResolution;
  }
  
      
/** 
 * A method that finds joins between two bricks, if any.
 * @param blocks : ArrayList of Blocks (Chords or Bricks)
 * @return joinList : ArrayList of joins between blocks
 */
      
public static ArrayList<String> findJoins(ArrayList<Block> blocks)
  {
    String[] joinArray = null;
    if( blocks.size() >= 1 )
      {
        joinArray = new String[blocks.size() - 1];
      }
    else
      {
        return null;
      }
    for( int i = 0; i < blocks.size() - 1; i++ )
      {
        joinArray[i] = getJoinString(blocks.get(i), blocks.get(i + 1));
      }
    ArrayList<String> joinList = new ArrayList(Arrays.asList(joinArray));
    return joinList;
  }



/**
 * Return a possibly-empty String representing representing a join between 
 * two blocks.
 */

public static String getJoinString(Block b, Block c)
  {
    //System.out.println("getJoinString " + b + " vs. " + c + " chords = " + b.getLastChord() + " vs. " + c.getFirstChord());

    if( b.getLastChord().same(c.getFirstChord()) )
      {
        return "";
      }

    if( c instanceof Brick )
      {
        if( !checkFirstStability(b) )
          {
            if( checkDogleg(b, (Brick) c) )
              {
                return "Dogleg";
              }
            return "";
          }

        // Check that the two bricks are joinable
        if( checkJoinability(b, (Brick) c) )
          {
            //System.out.println("joinable");
            // Default to dominant of brick's overall key

            long domKey = (c.getKey() + 7) % OCTAVE;

            ChordBlock cfirst = c.getFirstChord();

            // First check for staring with minor 7 type chord
/*
            if( cfirst.isMinor7() )
              {
                domKey = (cfirst.getKey() + FOURTH) % OCTAVE;
              }
            // Otherwise try to use first dominant in second brick
            else
*/
              {
                ArrayList<ChordBlock> chords = c.flattenBlock();

                for( ChordBlock cb : chords )
                  {
                    if( cb.isDominant() )
                      {
                        domKey = cb.getKey();
                        break;
                      }
                  }

                // Make exception if there is a sequence of dominants
                // following the cycle or chromatic, in which case use
                // the last dominant in the sequence.

                if( !chords.isEmpty() )
                  {
                    ChordBlock previous = c.getFirstChord();

                    for( ChordBlock current : chords )
                      {
                        //System.out.println("previous = " + previous + " current = " + current);
                        int diffPrevious = (OCTAVE + previous.getRootSemitones() - current.getRootSemitones()) % OCTAVE;

                        // Possible cyclic or chromatic descending dominant

                        if( current.same(previous) )
                          {
                          }
                        else if( current.isDominant() )
                          {
                            if( previous.isDominant() )
                              {
                                if( diffPrevious == 7 || diffPrevious == 1 )
                                  {
                                  }
                                else
                                  {
                                    break;
                                  }
                              }
                            else if( previous.isMinor7() )
                              {
                                if( diffPrevious == 7 )
                                  {
                                  }
                                else
                                  {
                                    break;
                                  }
                              }
                            else
                              {
                                break;
                              }
                            previous = current;
                          }
                        else if( current.isMinor7() )
                          {
                            if( previous.isDominant() )
                              {
                                if( diffPrevious == 7 )
                                  {
                                  }
                                else
                                  {
                                    break;
                                  }
                              }
                            else if( previous.isMinor7() )
                              {
                                if( diffPrevious == 7 )
                                  {
                                  }
                                else
                                  {
                                    break;
                                  }
                              }
                            else
                              {
                                break;
                              }
                            previous = current;
                          }
                        else
                          {
                            break;
                          }
                      }

                    // Break to here
                    domKey = previous.isDominant() ? previous.getKey() : (previous.getKey() - 7);
                  }
              }
            // Determine which join based on difference between first 
            // block's key and dominant found in previous step

            long keyDiff = (domKey - b.getKey() + OCTAVE) % OCTAVE;

            return joinLookup(keyDiff);
          }
        return "";
      } // instance of Brick
    else
      // c is a ChordBlock, not a Brick
      {
        if( !checkFirstStability(b) )
          {
            return "";
          }

        // Second block is a chord, but this does not mean not joinable.

        ChordBlock cb = (ChordBlock) c;

        long domKey = (cb.getKey() + 7) % OCTAVE;

        // First check for staring with minor 7 type chord

        if( cb.isMinor7() )
          {
            domKey = (c.getKey() + FOURTH) % OCTAVE;
            long keyDiff = (domKey - b.getKey() + OCTAVE) % OCTAVE;
            return joinLookup(keyDiff);
          }
        // Otherwise try to use first dominant in second brick
        else if( cb.isDominant() )
          {
            domKey = cb.getKey();
            long keyDiff = (domKey - b.getKey() + OCTAVE) % OCTAVE;
            return joinLookup(keyDiff);
          }
      }

return "";
  }

/** 
 * Check whether a Block is joinable to a Brick.
 * @param first : a Block
 * @param second : a Brick
 * @return joinable : a boolean indicating whether or not first and second 
 *                    are joinable
 */
public static boolean checkJoinability(Block first, Brick second)
  {
     // Comparing last chord of first block and first chord of second block
    
    ChordBlock firstToCheck = first.getLastChord();
    ChordBlock secondToCheck = second.getFirstChord();

    // Don't join to a tonic directly

    if( secondToCheck.isTonic() )
      {
        return false;
      }
    
    // Don't join minor tonality with minor7
    
    if( firstToCheck.isMinor() && secondToCheck.isMinor7() 
     && firstToCheck.getRootSemitones() == secondToCheck.getRootSemitones() )
      {
        return false;
      }

    // Get equivalences for the two chords

    SubstituteList firstEquivs = dict.checkEquivalence(firstToCheck);
    SubstituteList secondEquivs = dict.checkEquivalence(secondToCheck);

    String firstMode = first.getMode();
    String secondMode = second.getMode();

    String firstType = first.getType();
    String secondType = second.getType();

    // Don't join chord to the same chord

    if( firstToCheck.same(secondToCheck) )
      {
        return false;
      }

    //System.out.println("joinable? " + firstType + " to " + secondType + ": " + first + " to " + second);

    // Determine stability of first block
    // This is necesary if the blocks are to be joinable.

    if( !checkFirstStability(first) ) 
      {
        //System.out.println(" NO, first not stable");
        return false; // No point in checking further
      }
    /*
    else if( firstEquivs.hasMode(firstMode) )
      {
        // Otherwise, continue if the first block is stable.
      }
    else
      {
        //System.out.println(" NO, first wrong mode");
        return false; // Otherwise, condsider non-joinable.
      }
     */

    // Determine stability of second block

    if( !checkSecondInstability(secondToCheck) )
      {
        //System.out.println(" YES");
        return false;
      }
    /*
    else if( secondEquivs.hasMode(secondMode) )
      {
        System.out.println(" NO, second wrong ");
        return false;
      }
     */

    //System.out.println(" YES");
    return true;

  }

    /** 
     * Checks whether a possible join is a dogleg
     * @param first : a Block (before join)
     * @param second : a Brick (after join)
     * @return isDogleg : a boolean indicating whether or not join is dogleg
     */
    
    public static boolean checkDogleg(Block first, Brick second) {
        boolean isDogleg = false;
        
        ArrayList<ChordBlock> firstList  =  first.flattenBlock();
        ArrayList<ChordBlock> secondList = second.flattenBlock();
        
        // Comparing last chord of first block and first block of second block
        ChordBlock firstToCheck = firstList.get(firstList.size() - 1);
        ChordBlock secondToCheck = secondList.get(0);
        
        long firstKey = firstToCheck.getKey();
        String firstSymbol = firstToCheck.getSymbol();
        
        long secondKey = secondToCheck.getKey();
        String secondSymbol = secondToCheck.getSymbol();
        
        // Dogleg join is characterized by a dominant to a m7
        if(firstKey == secondKey && firstSymbol.startsWith("7") && 
                secondSymbol.equals("m7")) {
            isDogleg = true;
        }
        
        return isDogleg;
    }
    
    /**
     * Retrieve name of join based on difference between keys of two bricks.
     * @param keyDiff : difference of keys between two bricks
     * @return  Name of join
     */
    public static String joinLookup (long keyDiff) {
        if(keyDiff >= 0 && keyDiff < 12)
            return JOINS[(int)keyDiff];
        else
        {
            ErrorLog.log(ErrorLog.FATAL, "Difference between keys is incorrect");
            return "";
        }
    }
    
    /**
     * Check to see if chord fits diatonically within key.
     * @param c : chord to be checked
     * @param key : key that chord is checked against
     * @param mode : String that determines qualities of chords in key
     * @param dict : a BrickLibrary
     * @return isInKey : whether or not chord is in key
     */
    public static boolean diatonicChordCheck(ChordBlock c, Long key, 
            String mode) {
        
        //System.out.println("diatonicChordCheck " + c);
        
        if( c.isDiminished() )
          {
            return true;
          }
        
        boolean isInKey = false;
        ChordBlock cTemp = new ChordBlock(c);
        
        // Adjust for second brick's key
        cTemp.transpose(OCTAVE - key);
        Long offset = cTemp.getKey();
        
        // Transpose chord down to C
        cTemp.transpose(OCTAVE - offset);
        
        ChordSymbol cSym = null;
        
        // Get representative chord for c and save it in cSym
         
        for(Polylist p : equivalenceRules)
        {
            if(c.getChord().getChordSymbol().enhMember(p))
            {
                cSym = ChordSymbol.makeChordSymbol(p.first().toString());
                break;
            }
        }
        
        // Transpose cSym and c back by offset saved earlier
        if(cSym != null) {
            cSym = cSym.transpose(offset.intValue());
        }
        
        // Check if cSym is diatonically within key according to mode of second
        // block
        for(Polylist p : diatonicRules)
        {
            String modeTag = p.first().toString();
            p = p.rest();
            if(modeTag.equals(mode))
            {
                if(cSym != null && cSym.enhMember(p))
                {
                    isInKey = true;
                    break;
                }
            }
        }
        return isInKey;
    }
    
    /** 
     * Check if brick resolves to a certain block.
     * @param b1 : brick to be checked
     * @param b2 : possible tonic of b1
     * @return whether or not b1 resolves to b2
     */
    
    public static boolean doesResolve(Brick b1, Block b2) {

        if (b1.getKey() == b2.getKey()) {
            return true;
        }

        return false;
    }
       
    /** 
     * Check if ChordBlock resolves to a certain block
     * @param b1 : ChordBlock to be checked
     * @param b2 : possible tonic of b1
     * @return whether or not b1 resolves to b2
     */
    public static boolean doesResolve(ChordBlock b1, Block b2) {
        
        // Adjust for the dominant
        if ((b1.getKey() + DOM_ADJUST)%OCTAVE == b2.getKey()) {
            return true;
        }

        return false;
    }
    
    /** 
     * Treating an array of Strings as a set, determines whether or not
     * element occurs in the array.
     * @param array
     * @param element
     * @return 
     */
    public static boolean member(String element, String array[])
      {
        for( String x: array )
          {
            if( x.equals(element) )
              {
                return true;
              }
          }       
        return false;
      }
    
    
    /**
     * This only needs to be called once.
     */
    
    public static void setEquivalenceDictionary()
      {       
        dict = new EquivalenceDictionary();
        dict.loadDictionary(CYKParser.DICTIONARY_NAME);
      }
    
    /**
     * Tell whether the argument block is an Approach or a Launcher type
     * @param b
     * @return 
     */
    public static boolean isApproachOrLauncher(Block b)
      {
        String type = b.getType();
        return type.equals("Approach") || type.equals("Launcher");
      }
    
    
    public static boolean checkFirstStability(Block b)
      {
        return member(b.getType(), FIRST_STABLE);
      }
 
    public static boolean checkSecondInstability(Block b)
      {
        return member(b.getType(), SECOND_UNSTABLE) 
           || (b instanceof ChordBlock && ((ChordBlock)b).isMinor7());
      }
}
     
