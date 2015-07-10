/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.data;

import imp.com.RectifyPitchesCommand;
import imp.lickgen.transformations.Transform;
import imp.lickgen.transformations.TransformLearning;

/**
 *
 * @author muddCS15
 */
public class ResponseGenerator {
    
    private MelodyPart response;
    private ChordPart responseChords;
    private final BeatFinder beatFinder;
    private final TransformLearning flattener;
    
    public ResponseGenerator(MelodyPart response, ChordPart responseChords, int [] metre){
        this.response = response;
        this.responseChords = responseChords;
        this.beatFinder = new BeatFinder(metre);
        this.flattener = new TransformLearning();
    }
    
    //STEP 0
    
    //set response
    public void setResponse(MelodyPart response){
        this.response = response;
    }

    //set chords
    public void setChords(ChordPart responseChords){
        this.responseChords = responseChords;
    }
    
    //STEP 1
    
    //Flatten a solo to the default resolution
    //currently flatten to every beat
    public void flattenSolo(){
        flattenSolo(beatFinder.EVERY_BEAT);
    }

    //Flatten solo to a specified resolution
    //Resolutions specified by strings must be converted
    //Examples:
    //beatFinder.EVERY_BEAT
    //beatFinder.MEASURE_LENGTH
    //beatFinder.STRONG_BEATS
    public void flattenSolo(String resolution){
        flattenSolo(beatFinder.getResolution(resolution));
    }

    //Flatten solo to specified resolution
    //(flattens based on response chords)
    //Examples:
    //Constants.WHOLE
    //Constants.HALF
    public void flattenSolo(int resolution){
        //this could be wrong
        int start = 0;
        int stop = response.size()-1;
        
        response = flattener.flattenByChord(response, responseChords, resolution, start, stop, false);
    }

    //STEP 2
    
    //transform solo using specified transform
    //(in gui, select this from a drop down menu)
    public void transformSolo(Transform musician){
        response = musician.applySubstitutionsToMelodyPart(response, responseChords, true);
    }
    
    //STEP 3
    
    //rectify solo to response chords
    //allows chord, color, and approach tones
    //allows repeat pitches
    public void rectifySolo(){
        RectifyPitchesCommand cmd = new RectifyPitchesCommand(response, 0, response.size()-1, responseChords, false, false);
        cmd.execute();
    }
    
    //STEP 4
    
    //retreive response
    public MelodyPart getResponse(){
        return response;
    }
    
    //ALL THE STEPS TOGETHER
    public MelodyPart getResponse(MelodyPart response, ChordPart responseChords, Transform musician){
        //STEP 0
        setResponse(response);
        setChords(responseChords);
        //STEP 1
        flattenSolo();
        //STEP 2
        transformSolo(musician);
        //STEP 3
        rectifySolo();
        //STEP 4
        return getResponse();
    }
    
    
}
