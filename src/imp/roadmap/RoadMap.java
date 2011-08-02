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

package imp.roadmap;

import java.util.ArrayList;
import java.util.List;

import imp.cykparser.PostProcessor;
import imp.brickdictionary.*;

import polya.Polylist;
import polya.PolylistBuffer;

/**
 * This class contains the musical data of a roadmap IE: a sequence of blocks,
 * keys and joins.
 * @author August Toman-Yih
 */
public class RoadMap {
    /** List of blocks contained in the roadmap */
    private ArrayList<Block> blocks;
    /** Key map in the form of key, duration pairs */
    private ArrayList<KeySpan> keyMap = new ArrayList();
    /** List of joins between each brick */
    private ArrayList<String> joins = new ArrayList();
    
    /**
     * Argumentless constructor that creates an empty roadmap.
     */
    public RoadMap()
    {
        blocks = new ArrayList();
    }
    
    /**
     * Constructs a roadmap from a list of blocks.
     * @param blocks 
     * list of blocks
     */
    public RoadMap(ArrayList<Block> blocks)
    {
        this.blocks = blocks;
    }
    
    /**
     * Copy constructor
     * @param roadMap 
     */
    public RoadMap(RoadMap roadMap)
    {
        blocks = cloneBlocks(roadMap.getBlocks());
    }
    
    /**
     * Returns the number of blocks in the roadmap
     * @return the number of blocks in the roadmap
     */
    public int size()
    {
        return blocks.size();
    }
    
    /**
     * Returns whether the roadmap is empty
     * @return 
     */
    public boolean isEmpty()
    {
        return blocks.isEmpty();
    }
    
    /**
     * Returns the keymap.
     * @return 
     * Keymap
     */
    public ArrayList<KeySpan> getKeyMap()
    {
        return keyMap;
    }
    
    /**
     * returns the list of joins.
     * @return 
     * list of joins
     */
    public ArrayList<String> getJoins()
    {
        return joins;
    }
    
    /**
     * returns the block at the requested index
     * @param index index of the desired block
     * @return the block
     */
    public Block getBlock(int index)
    {
        return blocks.get(index);
    }
    
    /**
     * returns the list of bricks.
     * @return
     * list of bricks
     */
    public ArrayList<Block> getBlocks()
    {
        return blocks;
    }
    
    /**
     * Returns the bricks between start and end (excluding end)
     * @param start 
     * start index
     * @param end
     * end index
     * @return  
     * list of bricks
     */
    public ArrayList<Block> getBlocks(int start, int end)
    {
        return new ArrayList(blocks.subList(start, end));
    }
    
    /**
     * Sets the blocks to b
     * @param b the new blocks
     */
    public void setBlocks(ArrayList<Block> b)
    {
        this.blocks = b;
    }
    
    /**
     * Sets the keymap to km
     * @param km the new keymap
     */
    public void setKeyMap(ArrayList<KeySpan> km) {
        this.keyMap = km;
    }
    
    /**
     * Sets the joins to j
     * @param j the joins
     */
    public void setJoins(ArrayList<String> j) {
        this.joins = j;
    }
    
    /**
     * removes and returns all bricks in the roadmap
     * @return 
     * bricks contained in the roadmap
     */
    public ArrayList<Block> removeBlocks()
    {
        ArrayList bricks = new ArrayList(blocks);
        blocks.clear();
        keyMap.clear();
        joins.clear();
        return bricks;
    }
    
    /**
     * removes and returns all bricks within a range
     * @param start
     * start index (inclusive)
     * @param end
     * end index (exclusive)
     * @return 
     * list of bricks
     */
    public ArrayList<Block> removeBlocks(int start, int end)
    {
        ArrayList bricks = new ArrayList(blocks.subList(start, end));
        blocks.subList(start, end).clear();
        process();
        return bricks;
    }
    
    /**
     * returns the flattened chords in the roadmap
     * @return 
     * list of chords
     */
    public ArrayList<ChordBlock> getChords()
    {
        return getChords(blocks);
    }
    
    /**
     * returns the flattened chords contained in a range
     * @param start
     * start index (inclusive)
     * @param end
     * end index (exclusive)
     * @return 
     * list of chords
     */
    public ArrayList<ChordBlock> getChordsInRange(int start, int end)
    {
        return getChords(blocks.subList(start, end));
    }
    
    /**
     * returns the chords contained in a list of blocks
     * @param blocks
     * blocks to get chords from
     * @return 
     * list of chords
     */
    public static ArrayList<ChordBlock> getChords(List<Block> blocks)
    {
        ArrayList<ChordBlock> chords = new ArrayList();
        for( Block block : blocks )
            chords.addAll(block.flattenBlock());
        return chords;
    }
    
    /**
     * Deep copies a list of blocks
     * @param blocks to be copied
     * @return resultant blocks
     */
    public static ArrayList<Block> cloneBlocks(ArrayList<Block> blocks)
    {
        ArrayList<Block> clones = new ArrayList();
        for( Block block : blocks ) {
            if( block instanceof Brick ) 
                clones.add(new Brick((Brick)block));
            if( block instanceof ChordBlock )
                clones.add(new ChordBlock((ChordBlock)block));
        }
        return clones;
    }
    
    /**
     * add a block to the end of the roadmap
     * @param block 
     * block to be added
     */
    public void add(Block block)
    {
        blocks.add(block);
        process();
    }
    
    /**
     * insert a block at the specified index
     * @param ind
     * where to insert the block
     * @param block 
     * block to be inserted
     */
    public void add(int ind, Block block)
    {
        blocks.add(ind, block);
        process();
    }
    
    /**
     * add a list of blocks to the end of the the roadmap
     * @param blocks 
     * blocks to be added
     */
    public void addAll(List<Block> blocks)
    {
        this.blocks.addAll(blocks);
        process();
    }
    
    /**
     * insert a list of blocks at the specified index
     * @param ind
     * where to insert the blocks
     * @param blocks 
     * blocks to be inserted
     */
    public void addAll(int ind, List<Block> blocks)
    {
        this.blocks.addAll(ind, blocks);
        process();
    }
    
    /**
     * Updates the keymap and join list.
     */
    public void process()
    {
        joins = PostProcessor.findJoins(blocks);
        RoadMap newMap = PostProcessor.findKeys(this);
        this.setBlocks(newMap.getBlocks());
        this.setKeyMap(newMap.getKeyMap());
    }
    
/* Old version    
    @Override
    public String toString()
    {
        String output = "Roadmap ";
        for(Block block : blocks)
            output += block + " ";
        return output;
    }
 */  
    
    /**
     * The String representation of a RoadMap is String version of
     * the Polylist version.
     * @return 
     */
    
    @Override
    public String toString()
    {
        return toPolylist().toString();
    }
  
   /**
     * The Polylist representation of a RoadMap captures the essential
     * elements as a single polylist. It includes the blocks,
     * joins, and keymap in that order.
     * @return 
     */
    
    
    public Polylist toPolylist()
      {
        PolylistBuffer buffer = new PolylistBuffer();
        
        buffer.append("roadmap");
        
        PolylistBuffer innerBuffer = new PolylistBuffer();
        
        for( Block b: blocks )
          {
            innerBuffer.append(b.toPolylist());
          }
        
        buffer.append(innerBuffer.toPolylist().cons("blocks"));
        
        innerBuffer = new PolylistBuffer();
        
        for( String s: joins )
          {
            innerBuffer.append(s);
          }
        
        buffer.append(innerBuffer.toPolylist().cons("joins"));
        
        innerBuffer = new PolylistBuffer();
        
        for( KeySpan k: keyMap )
          {
            innerBuffer.append(k.toPolylist());
          }
        
        buffer.append(innerBuffer.toPolylist().cons("keymap"));
        
        return buffer.toPolylist();
      }
   
}
