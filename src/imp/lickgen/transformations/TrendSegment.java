/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations;

import java.util.ArrayList;
/**
 *
 * @author muddCS15
 */
public class TrendSegment {
    private final ArrayList<NoteChordPair> ncps;
    private int totalDuration;
    
    public TrendSegment(){
        ncps = new ArrayList<NoteChordPair>();
        totalDuration = 0;
    }

    public void add(NoteChordPair ncp){
        ncps.add(ncp);
        totalDuration += ncp.getNote().getRhythmValue();
    }

    public int getTotalDuration(){
        return totalDuration;
    }

    public int getSize(){
        return ncps.size();
    }
    
    public int getStartSlot(){
        return ncps.get(0).getSlot();
    }
    
    @Override
    public String toString(){
        String toreturn = "";
        toreturn+="Trend:\n";
        for(NoteChordPair ncp : ncps){
            toreturn += (ncp.toString()+"\n");
        }
        return toreturn;
    }
    
    public void clear(){
        ncps.clear();
        totalDuration = 0;
    }
    
    public ArrayList<TrendSegment> splitUp(int duration){
        ArrayList<TrendSegment> chunks = new ArrayList<TrendSegment>();
        TrendSegment currentChunk = new TrendSegment();
        int durationRemaining = duration;
        for(NoteChordPair ncp : ncps){
            currentChunk.add(ncp);
            durationRemaining -= ncp.getNote().getRhythmValue();
            if(durationRemaining <= 0){
                durationRemaining = duration;
                chunks.add(currentChunk);
                currentChunk.clear();
            }
        }
        return chunks;
    }

    public NCPIterator makeIterator(){
        return new NCPIterator(ncps);
    }
    
    public NoteChordPair firstNCP(){
        if(!ncps.isEmpty()){
            return ncps.get(0);
        }
        else{
            return null;
        }
    }
    
    public NoteChordPair lastNCP(){
        if(!ncps.isEmpty()){
            return ncps.get(ncps.size()-1);
        }
        else{
            return null;
        }
    }
    
}
