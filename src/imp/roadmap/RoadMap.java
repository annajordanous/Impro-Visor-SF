/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

import java.util.ArrayList;
import java.util.List;

import imp.cykparser.PostProcessing;
import imp.brickdictionary.*;

/**
 *
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
        blocks = cloneBlocks(roadMap.getBricks());
        System.out.println(blocks);
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
    public Block getBrick(int index)
    {
        return blocks.get(index);
    }
    
    /**
     * returns the list of bricks.
     * @return
     * list of bricks
     */
    public ArrayList<Block> getBricks()
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
    public ArrayList<Block> getBricks(int start, int end)
    {
        return new ArrayList(blocks.subList(start, end));
    }
    
    /**
     * removes and returns all bricks in the roadmap
     * @return 
     * bricks contained in the roadmap
     */
    public ArrayList<Block> removeBricks()
    {
        ArrayList bricks = new ArrayList(blocks);
        blocks.clear();
        process();
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
    public ArrayList<Block> removeBricks(int start, int end)
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
    public ArrayList<Chord> getChords()
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
    public ArrayList<Chord> getChordsInRange(int start, int end)
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
    public static ArrayList<Chord> getChords(List<Block> blocks)
    {
        ArrayList<Chord> chords = new ArrayList();
        for( Block block : blocks )
            chords.addAll(block.flattenBlock());
        return chords;
    }
    
    public static ArrayList<Block> cloneBlocks(ArrayList<Block> blocks)
    {
        ArrayList<Block> clones = new ArrayList();
        for( Block block : blocks ) {
            if( block instanceof Brick )
                clones.add(new Brick((Brick)block));
            if( block instanceof Chord )
                clones.add(new Chord((Chord)block));
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
    
    public void process()
    {
        joins = PostProcessing.findJoins(blocks);
        keyMap = PostProcessing.findKeys(blocks);
    }
    
    @Override
    public String toString()
    {
        String output = "Roadmap ";
        for(Block block : blocks)
            output += block + " ";
        return output;
    }
}
