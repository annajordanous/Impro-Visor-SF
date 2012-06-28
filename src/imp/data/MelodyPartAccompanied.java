/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

/**
 *
 * @author summer
 */
public class MelodyPartAccompanied extends MelodyPart {
    
    private ChordPart chordProg;
    
    public MelodyPartAccompanied(int size, ChordPart chordProg){
        super(size);
        this.chordProg = chordProg;
    }
    
    public ChordPart getChordProg() {
        return chordProg;
    }
    
}
