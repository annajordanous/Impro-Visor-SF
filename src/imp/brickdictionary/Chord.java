
package imp.brickdictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * purpose: Definition definition
 * @author Zachary Merritt
 */


public class Chord extends Block{
    
    // Type of chord, ie. "m7b5" or "7"
    private String quality;
    private long slashChord;
    public static final long NC = -1;
    
    // Constructor for chord
    // Uses parseChordName to interpret chord's name, finding root (key) and
    // quality
    public Chord(String chordName, long duration) {
        super(chordName);
        this.duration = duration;
        this.parseChordName();
        isEnd = false;
    }
    
    public Chord(String chordName, long duration, boolean sectionend) {
        super(chordName);
        this.duration = duration;
        this.parseChordName();
        isEnd = sectionend;
    }
    
    public Chord(Chord chord) {
        super(chord.name);
        this.duration = chord.duration;
        this.parseChordName();
    }
    
    // Get duration of current chord
    @Override
    public Long getDuration() {
        return this.duration;
    }
    
    @Override
    public void adjustDuration(long scale) {
        if(scale > 0)
            duration = duration * scale;
        else
            duration = duration / -scale;
    }
    
    public void changeChordDuration(float ratio) {
        duration = Math.round(ratio * duration);
    }
    
    // Get current chord's quality (ie. "mM7")
    public String getQuality() {
        if (isSlashChord()) 
            return this.quality + "/" + BrickLibrary.keyNumToName(slashChord);
        return this.quality;
    }
    
    public String getSymbol() {
        if (isSlashChord()) 
            return this.quality + "/" + slashChord;
        return this.quality;
    }
    
    public boolean isSlashChord() {
        if (this.slashChord != this.key) return true;
        return false;
    }
    
    
    @Override
    public List<Block> getSubBlocks() {
    ArrayList<Block> subBlocks = new ArrayList<Block>();
        subBlocks.add(this);
        return subBlocks;
    }
    
    // Return a one member list of this chord
    // Overrides corresponding methid in Block
    @Override
    public List<Chord> flattenBlock() {
        List<Chord> chordList = new ArrayList<Chord>();
        if (this.duration != 0)
            chordList.add(this);
        
        return chordList;
    }
    
    // Change chord's root (key) by diff
    @Override
    public void transpose(long diff) {
        this.key = moduloSteps(this.key + diff);
        this.slashChord = moduloSteps(this.slashChord + diff);
        this.name = BrickLibrary.keyNumToName(key) + this.getQuality();
    }
    
    public String transposeName(long diff) {
        transpose(diff);
        String tranName = this.name.intern();
        transpose(-1*diff);
        return tranName;
    }
    
    @Override
    public String toString() {
        return name + " " + duration;
    }
    
    
    public long matches(Chord c) {
        if (c.getQuality().equals(quality) || 
                (c.isSlashChord() && 
                c.getQuality().split("/")[0].equals(quality)))
            return moduloSteps(c.getKey() - key );
        return NC;
    }
    
    // Extract chord's root (key) and quality from its name
    private void parseChordName() {
        String chordName = this.getName();
        if(chordName.startsWith("NC")) {
            this.key = -1;
            this.quality = "";
        }
        else if(chordName.length() > 1 && (chordName.charAt(1) == 'b'|| 
                chordName.charAt(1) == '#'))
        {
            String chordKeyString = chordName.substring(0, 2);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (!(chordName.equals(chordKeyString))) {
                this.quality = chordName.substring(2);
            }
            else
                this.quality = "";
        }
        else
        {
            String chordKeyString = chordName.substring(0, 1);
            this.key = BrickLibrary.keyNameToNum(chordKeyString);
            if (chordName.length() > 1) {
                this.quality = chordName.substring(1);
            }
            else
                this.quality = "";
        }
        
        this.slashChord = this.key;
        String[] qualitySplit = this.quality.split("/", 2);
        if (qualitySplit.length > 1) {
            quality = qualitySplit[0];
            slashChord = BrickLibrary.keyNameToNum(qualitySplit[1]);
        }
        
        this.mode = this.quality;
            
    }
    
    public long moduloSteps(long l) {
        return (l + 12)%12;
}
    
    
}
