//jmusic for synthesis
package imp;

import java.util.ArrayList;

/**
 * Instructions: Initialize with priorities in constructor. 
 * Chord notes, hand bounds, and number of notes per hand should be set with each new chord.
 * The number of notes/hand, and hand bounds should have some degree of randomness.
 * Call calculate to actually calculate chord tones, and then get the integer array of midi notes to be played.
 *
 * @author Daniel Scanteianu
 */
public class VoicingGenerator {

    public VoicingGenerator() {
    }

    /**
     * 
     * @param colorPriority-the weight the color notes should have. Make it between 0 and a big-ish number
     * @param maxPriority-the maximum weight the priority notes should have. should be greater than the max number of priority notes*the priority multiplier.
     * @param previousVoicingMultiplier- amount to multiply the weight of the notes in the previous voicing's weightings by. Default: 1. Make greater than 1 for voice leading.
     * @param halfStepAwayMultiplier-amount to multiply the weight of the notes half a step away from the previous chord Default: 1. Make greater than 1 for voice leading.
     * @param fullStepAwayMultiplier-amount to multiply the weight of the notes a full step away from the previous chord. Default: 1. Make greater than 1 for voice leading.
     * @param priorityMultiplier - amount of weight to remove from notes as priority decreases. Default 0 for equal probability.
     * @param repeatMultiplier - the amount to reduce(or increase) the priority of notes already selected for the chord in other octaves. for reduction, make between 0 and 1. Default 1.
     */
        public VoicingGenerator(int leftColorPriority,int rightColorPriority, int maxPriority, double previousVoicingMultiplier, double halfStepAwayMultiplier, double fullStepAwayMultiplier, double priorityMultiplier, double repeatMultiplier, double halfStepReducer, double fullStepReducer) {
        this.leftColorPriority = leftColorPriority;
        this.rightColorPriority=rightColorPriority;
        this.maxPriority = maxPriority;
        this.previousVoicingMultiplier = previousVoicingMultiplier;
        this.halfStepAwayMultiplier = halfStepAwayMultiplier;
        this.fullStepAwayMultiplier = fullStepAwayMultiplier;
        this.priorityMultiplier = priorityMultiplier;
        this.repeatMultiplier = repeatMultiplier;
        this.halfStepReducer = halfStepReducer;
        this.fullStepReducer = fullStepReducer;
    }
    public void calculate()
    {
        
        allLeftValues=new ArrayList<Integer>();
        allRightValues=new ArrayList<Integer>();
        leftHand=new ArrayList<Integer>();
        rightHand=new ArrayList<Integer>();
        initAllMidiValues();
        if(previousVoicing!=null)
            weightPreviousVoicing();
        int noteToAdd;
        for(int i=0; i<numNotesLeft || i<numNotesRight; i++)
        {
            setupAllLeftValues();   
            if(!allLeftValues.isEmpty())
            {
                if(i<numNotesLeft)
                {
                    noteToAdd=allLeftValues.get((int)(Math.random()*allLeftValues.size()));
                    leftHand.add(noteToAdd);
                    allMidiValues[noteToAdd]=0;
                    allMidiValues[noteToAdd+1]*=halfStepReducer;
                    allMidiValues[noteToAdd-1]*=halfStepReducer;
                    allMidiValues[noteToAdd+2]*=fullStepReducer;
                    allMidiValues[noteToAdd-2]*=fullStepReducer;
                    multiplyNotes(noteToAdd,repeatMultiplier);
                                     
                }
            }
            setupAllRightValues();
            if(!allRightValues.isEmpty())
            {
                if(i<numNotesRight)
                {
                    noteToAdd=allRightValues.get((int)(Math.random()*allRightValues.size()));
                    rightHand.add(noteToAdd);
                    allMidiValues[noteToAdd]=0;
                    allMidiValues[noteToAdd+1]*=halfStepReducer;
                    allMidiValues[noteToAdd-1]*=halfStepReducer;
                    allMidiValues[noteToAdd+2]*=fullStepReducer;
                    allMidiValues[noteToAdd-2]*=fullStepReducer;
                    multiplyNotes(noteToAdd,repeatMultiplier);
                    
                }
            }
            
            
        }
        
    }

    public double getHalfStepReducer() {
        return halfStepReducer;
    }

    public void setHalfStepReducer(double halfStepReducer) {
        this.halfStepReducer = halfStepReducer;
    }

    public double getFullStepReducer() {
        return fullStepReducer;
    }

    public void setFullStepReducer(double fullStepReducer) {
        this.fullStepReducer = fullStepReducer;
    }
    private void weightPreviousVoicing()
    {
        for(int n: previousVoicing)
        {
            allMidiValues[n]=(int) (allMidiValues[n]*previousVoicingMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n+1]=(int) (allMidiValues[n+1]*halfStepAwayMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n-1]=(int) (allMidiValues[n-1]*halfStepAwayMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n-2]=(int) (allMidiValues[n-2]*fullStepAwayMultiplier);
        }
        for(int n: previousVoicing)
        {
            allMidiValues[n+2]=(int) (allMidiValues[n+2]*fullStepAwayMultiplier);
        }
    }
    private void initAllMidiValues()
    {
        //start with everything at zero
        for(int i=0; i<allMidiValues.length; i++)
        {
            allMidiValues[i]=0;
        }
        for(int i=0; i<color.length; i++)
        {
            setupNote(color[i],leftColorPriority*10);
        }
        for(int i=0; i<color.length; i++)
        {
            setupNote(color[i],rightColorPriority*10,lowerRightBound);
        }
        for(int p=0; p<priority.length; p++)
        {
            setupNote(priority[p], (int)(maxPriority*10-p*10*priorityMultiplier));
        }
    }
    private void setupAllLeftValues() {
       allLeftValues=new ArrayList<Integer>();
       for(int i=lowerLeftBound; i<=upperLeftBound; i++)
       {
           for(int j=0; j<allMidiValues[i]; j++)
           {
               allLeftValues.add(i);
           }
       }
    }
    private void setupAllRightValues() {
       allRightValues=new ArrayList<Integer>();
       for(int i=lowerRightBound; i<=upperRightBound; i++)
       {
           for(int j=0; j<allMidiValues[i]; j++)
           {
               allRightValues.add(i);
           }
       }
    }
    private void setupNote(int midiValue, int priority)
    {
        midiValue=midiValue%12;
        for(int i=midiValue; i<allMidiValues.length; i+=12)
        {
            allMidiValues[i]=priority;
        }
    }
    private void setupNote(int midiValue, int priority, int start)
    {
        midiValue=midiValue%12;
        for(int i=start; i<allMidiValues.length; i++)
        {
            if(i%12==midiValue)
                allMidiValues[i]=priority;
        }
    }
     public int[] getColor() {
        return color;
    }

    public void setColor(int[] color) {
        this.color = color;
    }

    public int[] getPriority() {
        return priority;
    }

    public void setPriority(int[] priority) {
        this.priority = priority;
    }
    public int getLowerLeftBound() {
        return lowerLeftBound;
    }

    public void setLowerLeftBound(int lowerLeftBound) {
        this.lowerLeftBound = lowerLeftBound;
    }

    public int getUpperLeftBound() {
        return upperLeftBound;
    }

    public void setUpperLeftBound(int upperLeftBound) {
        this.upperLeftBound = upperLeftBound;
    }

    public int getLowerRightBound() {
        return lowerRightBound;
    }

    public void setLowerRightBound(int lowerRightBound) {
        this.lowerRightBound = lowerRightBound;
    }

    public int getUpperRightBound() {
        return upperRightBound;
    }

    public void setUpperRightBound(int upperRightBound) {
        this.upperRightBound = upperRightBound;
    }

    public int getNumNotesLeft() {
        return numNotesLeft;
    }

    public void setNumNotesLeft(int numNotesLeft) {
        this.numNotesLeft = numNotesLeft;
    }

    public int getNumNotesRight() {
        return numNotesRight;
    }

    public void setNumNotesRight(int numNotesRight) {
        this.numNotesRight = numNotesRight;
    }

    public int getLeftColorPriority() {
        return leftColorPriority;
    }

    public void setLeftColorPriority(int leftColorPriority) {
        this.leftColorPriority = leftColorPriority;
    }

    public int getRightColorPriority() {
        return rightColorPriority;
    }

    public void setRightColorPriority(int rightColorPriority) {
        this.rightColorPriority = rightColorPriority;
    }

   
    public int getMaxPriority() {
        return maxPriority;
    }

    public void setMaxPriority(int maxPriority) {
        this.maxPriority = maxPriority;
    }

    public int[] getPreviousVoicing() {
        return previousVoicing;
    }

    public void setPreviousVoicing(int[] previousVoicing) {
        this.previousVoicing = previousVoicing;
    }

    public double getPreviousVoicingMultiplier() {
        return previousVoicingMultiplier;
    }

    public void setPreviousVoicingMultiplier(double previousVoicingMultiplier) {
        this.previousVoicingMultiplier = previousVoicingMultiplier;
    }

    public double getHalfStepAwayMultiplier() {
        return halfStepAwayMultiplier;
    }

    public void setHalfStepAwayMultiplier(double halfStepAwayMultiplier) {
        this.halfStepAwayMultiplier = halfStepAwayMultiplier;
    }

    public double getFullStepAwayMultiplier() {
        return fullStepAwayMultiplier;
    }

    public void setFullStepAwayMultiplier(double fullStepAwayMultiplier) {
        this.fullStepAwayMultiplier = fullStepAwayMultiplier;
    }

    public double getPriorityMultiplier() {
        return priorityMultiplier;
    }

    public void setPriorityMultiplier(double priorityMultiplier) {
        this.priorityMultiplier = priorityMultiplier;
    }

    public double getRepeatMultiplier() {
        return repeatMultiplier;
    }

    public void setRepeatMultiplier(double repeatMultiplier) {
        this.repeatMultiplier = repeatMultiplier;
    }
    private void multiplyNotes(int midiValue, double multiplier)
    {
        midiValue=midiValue%12;
        for(int i=midiValue; i<allMidiValues.length; i+=12)
        {
            allMidiValues[i]=(int)(allMidiValues[i]*multiplier);
        }
    }
    public int[] getLeftHand()
    {
        int[] leftArray=new int[leftHand.size()];
        for(int i=0; i<leftHand.size(); i++)
        {
            leftArray[i]=leftHand.get(i);
        }
        return leftArray;
    }
    public int[] getRightHand()
    {
        int[] rightArray=new int[rightHand.size()];
        for(int i=0; i<rightHand.size(); i++)
        {
            rightArray[i]=rightHand.get(i);
        }
        return rightArray;
    }
    public int[] getChord()
    {
        int[] chord=new int[rightHand.size()+leftHand.size()];
        ArrayList<Integer> chordList=new ArrayList<Integer>();
        chordList.addAll(leftHand);
        chordList.addAll(rightHand);
        for(int i=0; i<chordList.size();i++)
        {
            chord[i]=chordList.get(i);
        }
        return chord;
        
    }
    private int allMidiValues[]= new int[128];
    private int color[];
    private int priority[];
    private ArrayList<Integer> leftHand;
    private ArrayList<Integer> rightHand;
    private ArrayList<Integer> allLeftValues;
    private ArrayList<Integer> allRightValues;
    private int lowerLeftBound;
    private int upperLeftBound;
    private int lowerRightBound;
    private int upperRightBound;
    private int numNotesLeft;
    private int numNotesRight;
    private int leftColorPriority;//priority of any color note
    private int rightColorPriority;
    private int maxPriority;//max priority a note in the priority array can have
    private int previousVoicing[];
    private double previousVoicingMultiplier;// multiplier for notes used in previous voicing
    private double halfStepAwayMultiplier;
    private double fullStepAwayMultiplier;
    private double priorityMultiplier;//should be between 0 and 1, multiply this by the index in priority array, subtract result from max priority to get note priority
    private double repeatMultiplier;
    private double halfStepReducer;
    private double fullStepReducer;

}