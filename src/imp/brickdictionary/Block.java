
package imp.brickdictionary;

import java.util.List;

/**
 * purpose: Block definition
 * @author Zachary Merritt
 */
public class Block {
    
    protected String name;        // block's name
    protected long duration;      // how long block lasts (not in absolute units)
    protected long key;           // key is pitch-class relative to C, e.g. C=0, 
                                  // D=2, B=11, etc.
    protected String mode = null; // Broad quality of block (e.g. M, m, or 7)
    
    // Normal constructor for block
    public Block(String blockname, long blockkey, String mode) {
        this.name = blockname;
        this.key = blockkey;
        this.mode = mode;
    }
    
    // Normal constructor for block
    public Block(String blockname, long blockkey) {
        this.name = blockname;
        this.key = blockkey;
    }
    
    // Constructor given only a key
    public Block(int blockkey) {
        this.key = blockkey;
    }
    
    // Constructor given only a name
    public Block(String blockname) {
        this.name = blockname;
    }
    
    
    // Get key of brick or root of chord as a long (0 is C, 1 is C#, etc.)
    public long getKey() {
        return this.key;
    }
    
    // Get key of brick or root of chord as a String (e.g. "C")
    public String getKeyName() {
        return BrickLibrary.keyNumToName(this.key);
    }
    
    // Get block's name (ie. "Yardbird" or "Am7")
    public String getName() {
        return this.name;
    }
    
    // Get block's duration
    public Long getDuration() {
        return this.duration;
    }
    
    // Returns the subBlocks of a given block
    // Overridden by the corresponding method in Brick or Chord
    public List<Block> getSubBlocks() {
        return null;
    }
    
    public String getMode() {
        return this.mode;
    }
    
    // Transposes all the components of a block
    // Overridden by the corresponding method in Brick or Chord
    public void transpose(long diff) {
    }
    
    // Returns the individual chords that constitute this block
    // Overridden by the corresponding method in Brick or Chord
    public List<Chord> flattenBlock() {
        return null;
    }
    
    // Alters the duration of the total block
    // Overridden by the corresponding method in Brick or Chord
    public void adjustDuration(long factor) {
        duration = duration * factor;
    }
    
    public void reduceDuration(long factor) {
        duration = duration / factor;
    }
}
