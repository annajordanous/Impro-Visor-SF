package imp.audio;

import imp.data.MelodyPart;
import imp.data.MidiSynth;
import imp.data.Rest;
import imp.data.Score;
import imp.gui.Notate;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sound.sampled.*;
//import org.apache.commons.math3.complex.Complex;

/**
 * Class to capture and interpret audio input. The McLeod Pitch Method is
 * implemented as a means of pitch detection.
 *
 * @author Brian Howell
 * @version 22 June, 2012
 */
public class PitchExtractor
{

    public volatile Boolean stopCapture = true;
    public volatile boolean isCapturing;
    boolean stopAnalysis;
    public volatile Boolean thisMeasure = false;
    AudioInputStream inputStream;
    AudioFormat format;
    SourceDataLine source;
    TargetDataLine target;
    Score score;
    Notate notate;
    MidiSynth midiSynth;
    long startTime;
    int additionalSamples;
    boolean processingStarted = false;
    double swingVal;
    int captureInterval;
    int startingPosition = 0;
    int positionOffset;
    //int analysesCompleted = 0;
    boolean firstCapture = true;
    private final float SAMPLE_RATE = 44100.0F; //in Hertz
    private final int SAMPLE_SIZE = 16; //1 sample = SAMPLE_SIZE bits
    private final int FRAME_SIZE = 2048; //# of BYTES examined per poll
    private final float POLL_RATE = 20; //in milliseconds
    private final int tenMSOffset = (int) (10.0 / 1000.0 * SAMPLE_RATE * 2.0);
    private final double interval = POLL_RATE / 1000.0 * SAMPLE_RATE * 2.0;
    private int RESOLUTION = 8; //smallest subdivision allowed
    int slotConversion = 480 / RESOLUTION;
    private boolean TRIPLETS = false;
    //only windows with a RMS above this threshold will be examined
    private double RMS_THRESHOLD = 4.75;
    private double CONFIDENCE = 0;
    //DoubleFFT_1D fft;
    private boolean noteOff; //flag indicating terminal note
    //sets threshold for detecting peaks in normalized SDF
    private final double K_CONSTANT = 0.875;
    //private final TextField tempoField;
    private Queue<byte[]> processingQueue;

    public static void main(String args[])
    {
        //new PitchExtraction();
    }//end main

    public PitchExtractor(Notate notate, Score score, MidiSynth midiSynth, int captureInterval)
    {
        this.notate = notate;
        this.score = score;
        this.midiSynth = midiSynth;
        //swingVal = score.getChordProg().getStyle().getSwing();
        this.captureInterval = captureInterval;
        format = getAudioFormat();
        processingQueue = new ConcurrentLinkedQueue<byte[]>();
    }

    public void openTargetLine()
    {
        try
        {
            DataLine.Info dataLineInfo =
                    new DataLine.Info(TargetDataLine.class, format);
            target = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            target.open(format);
            target.start();
        } catch (Exception e)
        {
            System.out.println(e);
        }
    }

    public void closeTargetLine()
    {
        try
        {
            target.close();
        } catch (Exception e)
        {
            System.out.println("TargetLine error.");
        }
    }

    public int getCaptureInterval()
    {
        return this.captureInterval;
    }

    public void setResolution(int newResolution)
    {
        if(newResolution > 2 && newResolution < 64
                && newResolution % 2 == 0) {
            RESOLUTION = newResolution;
        }
    }

    public void captureAudio()
    {
        stopCapture = false;
        stopAnalysis = false;
        try
        {
            CaptureThread captureThread = new CaptureThread();
            //captureThread.setPriority(Thread.MAX_PRIORITY);
            captureThread.start();
            AnalyzeThread analyzeThread = new AnalyzeThread();
            analyzeThread.setPriority(Thread.MAX_PRIORITY);
            analyzeThread.start();
        } catch (Exception e)
        {
            System.out.println(e);
            System.exit(0);
        }//end catch
    }//end captureAudio method

    /**
     * Breaks input data into frames and determines pitch for each frame.
     *
     * @param streamInput the array of bytes delivered by the TargetDataLine.
     */
    private void parseNotes(byte[] streamInput)
    {
        MelodyPart melodyPart = notate.getCurrentMelodyPart();
        //where the first note is to be inserted in the melody
        //startingPosition = analysesCompleted * captureInterval;
        int index;
        if (firstCapture)
        {
            index = positionOffset + tenMSOffset;
            //firstMeasure = false;
        } else
        { //ignore first 10ms of input data; begin polling thereafter
            index = tenMSOffset;
        }
        //System.out.println("index = " + index + ", positionOffset = " + positionOffset);
        int size = FRAME_SIZE / 2;
        //convert tempo to ms per measure
        float tempo = (float) (score.getMetre()[0] * 60000.0 / score.getTempo());
        int slotSize = RESOLUTION; //smallest subdivision allowed
        if (TRIPLETS)
        { //adjust minimum slot size if triplets are allowed
            slotSize *= 3;
        }
        boolean firstSlot = true;
        int lastSlotNumber = 1;
        int currentSlotNumber;
        int lastPitch = 0; //initialize most recent pitch to a rest
        int slotsFilled = 1; //# of slots filled before pitch change is detected
        List<Integer> oneSlot = new ArrayList<Integer>();
        int duration = 0;
        while (index + FRAME_SIZE < streamInput.length)
        {
            byte[] oneFrame = new byte[FRAME_SIZE];
            //break input into frames
            for (int i = index; i < index + FRAME_SIZE; i++)
            {
                //System.out.println("i = " + i);
                oneFrame[i - index] = streamInput[i];
            }
            ByteBuffer bBuf = ByteBuffer.wrap(oneFrame);
            //new array to store double values
            double[] preCorrelatedData = new double[size];
            for (int i = 0; i < size; i++)
            { //populate array
                preCorrelatedData[i] = (double) bBuf.getShort();
            }
            double fundamentalFrequency;
            //only attempt to determine pitch if RMS is above threshold
            if (checkRMS(preCorrelatedData))
            {
                double[] correlatedData = new double[size];
                correlatedData = computeAutocorrelationNoFFT(preCorrelatedData);
                double[] computedData = new double[size];
                computedData = computeNSD(preCorrelatedData, correlatedData);
                fundamentalFrequency = pickPeakWithoutFFT(computedData);
            } else
            { //otherwise, assign fundamental to zero
                fundamentalFrequency = 0;
            }
            double timeElapsed = 0;
            if(firstCapture) {
                timeElapsed = ((index - positionOffset) / interval) * POLL_RATE;
            }
            else {
                timeElapsed = index / interval * POLL_RATE;
            }
            //System.out.println("Time elapsed = " + timeElapsed);
            currentSlotNumber = resolveSlot(timeElapsed,
                    tempo / slotSize, slotSize) + 1;
            int slotPitch = 0;
            //check to see if pitch is valid
            if (fundamentalFrequency > 40.0)
            { //calculate equivalent MIDI pitch value for freq.
                slotPitch =
                        jm.music.data.Note.freqToMidiPitch(fundamentalFrequency);
            }
            //if all windows for this slot have been examined, determine pitch
            //check to see if this window is part of the current slot
            if (currentSlotNumber != lastSlotNumber)
            {
                System.out.println("Slot = " + lastSlotNumber);
                int pitch = calculatePitch(oneSlot);
                int altPitch = calculateDumbPitch(oneSlot);
                if (pitch != altPitch)
                {
                    System.out.println("Pitch = " + pitch
                            + ", Alt. Pitch = " + altPitch);
                }
                //check to see whether or not pitch has changed from that
                //which fills the previous slot
                if (!firstSlot)
                {
                    if (pitch != lastPitch || noteOff)
                    {
                        duration = slotsFilled * slotConversion;
                        setNote(lastPitch,
                                startingPosition,
                                duration,
                                melodyPart);
                        incrementStartingPosition(duration);
                        slotsFilled = 1; //reset slotsFilled when pitch changes
                    } //if this pitch is the same as that of the last slot,
                    //continue building duration until pitch changes
                    else
                    { //if pitch hasn't changed, increment # of slots filled
                        slotsFilled++;
                        System.out.println("Duration for " + pitch + " extended.");
                    }
                } else
                {
                    firstSlot = false;
                }
                lastPitch = pitch;
                if (currentSlotNumber % slotSize == 0)
                {
                    lastSlotNumber = RESOLUTION;
                } else
                {
                    lastSlotNumber = currentSlotNumber;
                }
                oneSlot.clear(); //get rid of old list
                oneSlot.add(slotPitch);
                //handle the last slot in this capture interval
            } else if (index + FRAME_SIZE + interval >= streamInput.length)
            {
                oneSlot.add(slotPitch);
                System.out.println("Slot = " + currentSlotNumber);
                int pitch = calculatePitch(oneSlot);
                int altPitch = calculateDumbPitch(oneSlot);
                if (pitch != altPitch)
                {
                    System.out.println("Pitch = " + pitch
                            + ", Alt. Pitch = " + altPitch);
                }
                if (pitch == lastPitch)
                {
                    slotsFilled++;
                    duration = slotsFilled * slotConversion;
                    setNote(pitch, startingPosition, duration, melodyPart);
                    incrementStartingPosition(duration);
                } else
                {
                    duration = slotsFilled * slotConversion;
                    setNote(lastPitch,
                            startingPosition,
                            duration,
                            melodyPart);
                    incrementStartingPosition(duration);
                    duration = slotConversion;
                    setNote(pitch,
                            startingPosition,
                            duration,
                            melodyPart);
                    incrementStartingPosition(duration);
                }
                //if the slot hasn't changed, count this window as part of the slot
            } else if (currentSlotNumber == lastSlotNumber)
            {
                oneSlot.add(slotPitch); //if so, continue collecting data
            }
            //increase the index by the designated interval
            index += (int) interval;
        } //end while
        firstCapture = false;
        //analysesCompleted++;
        System.out.println("Checked all.");
        synchronized (processingQueue)
                        {
                            try
                            {
                                processingQueue.notifyAll();
                            }
                            catch (Exception e) {
                                System.out.println(e);
                            }
                        }
    }

    private void incrementStartingPosition(int duration)
    {
        startingPosition += duration;
    }

    private void setNote(int pitch, int startingPosition, int duration, MelodyPart melodyPart)
    {
        if (pitch < 25) //count as a rest if pitch is out of range
        {
            imp.data.Note newRest = new Rest(duration);
            melodyPart.setNote(startingPosition, newRest);
            System.out.println("______________________________\n"
                    + " rest, duration = " + duration + " slots.\n"
                    + "______________________________");
        } else
        {
            imp.data.Note newNote = new imp.data.Note(pitch, duration);
            melodyPart.setNote(startingPosition, newNote);
            System.out.println("______________________________\n"
                    + newNote.getPitchClassName()
                    + (newNote.getPitch() / 12 - 1) + "(" + newNote.getPitch() + ")"
                    + ", duration = " + duration + " slots.\n"
                    + "______________________________");
        }
    }

    /**
     * Determines the root mean square for the input array.
     *
     * @param data The array time-domain data (in double format).
     * @return A boolean value representing whether or not the RMS is above the
     * threshold.
     */
    private boolean checkRMS(double[] data)
    {
        //calculate root mean square of this window
        //to check for adequate sample data
        double sum = 0.0;
        for (int i = 0; i < data.length; i++)
        {
            sum += (data[i] * data[i]);
        }
        double rms = Math.log(Math.sqrt(sum / data.length));
        //System.out.println("RMS = " + rms);
        return rms > RMS_THRESHOLD;
    }

    /**
     * Examines the pitches detected for the current slot and determines pitch
     *
     * @param pitches The array of pitches detected for this slot
     */
    private int calculatePitch(List<Integer> pitchList)
    {
        //System.out.println("New pitch calculated...");
        noteOff = false;
        int[] pitches = new int[pitchList.size()];
        for (int a = 0; a < pitchList.size(); a++)
        {
            pitches[a] = pitchList.get(a);
        }
        //Check for discrepancies in this slot
        int testPitch = 0;
        int numZeros = 0;
        boolean allZero = true;
        boolean tie = false;
        boolean d = false; //discrepancy test boolean
        int i = 0;
        while (!d && i < pitches.length)
        { //search for discrepancies in data
            if (pitches[i] == 0)
            {
                numZeros++;
            } else
            {
                if (pitches[i] != testPitch && testPitch != 0)
                {
                    d = true; //more than one nonzero pitch has been found
                } else
                {
                    testPitch = pitches[i];
                }
                allZero = false;
            }
            i++;
        } //end while
        int[] occurrences = new int[pitches.length];
        int maxLoc = 0;
        int maxO = 0;
        int secondPlaceLoc = -1;
        int secondPlaceO = -1;
        if (!d || allZero) //if there are no discrepancies, return the pitch
        {
            return testPitch;
        } else
        { //otherwise, find most frequently detected pitch
            testPitch = 0;
            for (i = 0; i < pitches.length; i++)
            {
                if (pitches[i] != 0 && pitches[i] != testPitch)
                {
                    occurrences[i] = 1;
                    testPitch = pitches[i]; //don't check same pitch twice...
                    for (int j = i + 1; j < pitches.length; j++)
                    {
                        if (pitches[i] == pitches[j])
                        {
                            occurrences[i] += 1;
                        }
                    } //end for (j)
                } //end if
            } //end for (i)
            for (i = 0; i < occurrences.length; i++)
            {
                if (occurrences[i] > maxO)
                {
                    if (maxO > 0)
                    {
                        secondPlaceLoc = maxLoc;
                        secondPlaceO = occurrences[secondPlaceLoc];
                    }
                    maxO = occurrences[i];
                    maxLoc = i;
                } //end if
                else if (occurrences[i] != 0
                        && occurrences[i] == maxO && maxLoc != i)
                {
                    tie = true;
                    secondPlaceLoc = i;
                    secondPlaceO = occurrences[i];
                }
            } //end for
        } //end else
        //check to see whether or not this tone is terminal
        if ((pitches.length > 2 && pitches[pitches.length - 2] == 0
                && pitches[pitches.length - 1] == 0)
                || (pitches.length < 3 && pitches[pitches.length - 1] == 0))
        {
            noteOff = true;
        }
        //if the pitch has at least 5 windows and no pitch
        if (pitches.length > 4 && maxO < 3 && numZeros >= pitches.length * 0.75)
        {
            return 0;
        } else if (secondPlaceO > 0)
        {
            int absDifference = Math.abs(pitches[maxLoc] - pitches[secondPlaceLoc]);
            if (!tie && maxO - secondPlaceO > 1 && absDifference < 11)
            {
                return pitches[maxLoc];
            } //check for false readings in lower octaves
            else if (!tie && absDifference > 11)
            {
                return Math.max(pitches[maxLoc], pitches[secondPlaceLoc]);
            } //end if
            else if (tie)
            {
                //algorithm is more prone to sub-fundamental errors
                if (secondPlaceLoc >= pitches.length / 2 && absDifference < 12)
                {
                    return pitches[secondPlaceLoc];
                } else
                {
                    return Math.max(pitches[maxLoc], pitches[secondPlaceLoc]);
                }
            }
        }
        return pitches[maxLoc];
    }

    /**
     * Examines the pitches detected for the current slot and determines pitch
     *
     * @param pitches The array of pitches detected for this slot
     */
    private int calculateDumbPitch(List<Integer> pitchList)
    {
        //noteOff = false;
        int[] pitches = new int[pitchList.size()];
        for (int a = 0; a < pitchList.size(); a++)
        {
            pitches[a] = pitchList.get(a);
        }
        //Check for discrepancies in this slot
        int testPitch = 0;
        boolean allZero = true;
        boolean tie = false;
        boolean d = false; //discrepancy test boolean
        int i = 0;
        while (!d && i < pitches.length)
        { //search for discrepancies in data
            if (pitches[i] != 0)
            {
                if (pitches[i] != testPitch && testPitch != 0)
                {
                    d = true; //more than one nonzero pitch has been found
                } else
                {
                    testPitch = pitches[i];
                }
                allZero = false;
            }
            i++;
        } //end while
        int[] occurrences = new int[pitches.length];
        int maxLoc = 0;
        int maxO = 0;
        int secondPlaceLoc = -1;
        int secondPlaceO = -1;
        if (!d || allZero) //if there are no discrepancies, return the pitch
        {
            return testPitch;
        } else
        { //otherwise, find most frequently detected pitch
            testPitch = 0;
            for (i = 0; i < pitches.length; i++)
            {
                if (pitches[i] != 0 && pitches[i] != testPitch)
                {
                    occurrences[i] = 1;
                    testPitch = pitches[i]; //don't check same pitch twice...
                    for (int j = i + 1; j < pitches.length; j++)
                    {
                        if (pitches[i] == pitches[j])
                        {
                            occurrences[i] += 1;
                        }
                    } //end for (j)
                } //end if
            } //end for (i)
            for (i = 0; i < occurrences.length; i++)
            {
                if (occurrences[i] > maxO)
                {
                    if (maxO > 0)
                    {
                        secondPlaceLoc = maxLoc;
                        secondPlaceO = occurrences[secondPlaceLoc];
                    }
                    maxO = occurrences[i];
                    maxLoc = i;
                } //end if
                else if (occurrences[i] != 0
                        && occurrences[i] == maxO && maxLoc != i)
                {
                    tie = true;
                    secondPlaceLoc = i;
                    secondPlaceO = occurrences[i];
                }
            } //end for
        } //end else
        //check to see whether or not this tone is terminal
//        if ((pitches.length > 2 && pitches[pitches.length - 2] == 0
//                && pitches[pitches.length - 1] == 0)
//                || (pitches.length < 3 && pitches[pitches.length - 1] == 0))
//        {
//            noteOff = true;
//        }
        if (secondPlaceLoc > 0
                && Math.abs(pitches[maxLoc] - pitches[secondPlaceLoc]) > 11)
        {
            int max = 0;
            for (int n = 0; n < pitches.length; n++)
            {
                if (pitches[n] > max)
                {
                    max = pitches[n];
                }
            }
            return max;
        } else
        {
            return pitches[maxLoc];
        }
    }

    /**
     * Determines which slot the current window falls into.
     *
     * @param timeElapsed The amount of time in milliseconds that has elapsed
     * since sampling began.
     * @param msPerSlot The number of milliseconds in each slot based on the
     * current minimum slot size (resolution).
     * @param slotSize The number of slots in each measure (also resolution).
     * @return The slot in which this window falls.
     */
    private int resolveSlot(double timeElapsed,
                            double msPerSlot, int slotSize)
    {
        int slot = (int) (Math.floor(timeElapsed / msPerSlot)) % slotSize;
        if (TRIPLETS && (slot % (RESOLUTION / 2) == RESOLUTION / 2 - 1
                || slot % (RESOLUTION / 2) == 1))
        {
            return slot - 1;
        } //        if(swingVal > 0.5 && RESOLUTION >= 8)
        //        {
        //            if(slot % (2 ^ (RESOLUTION / 8)) == (2 ^ (RESOLUTION / 8 - 1)) + 1) {
        //                if(timeElapsed / msPerSlot < (slot + 1) * swingVal){
        //                    return slot - 1;
        //                }
        //            }
        //        }
        else
        {
            return slot;
        }
    }

    /**
     * Computes the autocorrelation function for the given input as a function
     * of lag (tau).
     *
     * @param input The array of audio samples.
     * @return An array representing the autocorrelation function of the input
     * data.
     */
    private double[] computeAutocorrelationNoFFT(double[] input)
    {
        int size = input.length;
        double correlated[] = new double[size];
        //this is the m'(tau) component of the SDF
        for (int tau = 0; tau < size - 1; tau++)
        {
            double sum = 0;
            for (int j = 0; j < size - 1 - tau; j++)
            {
                sum += input[j] * input[j + tau];
            }
            correlated[tau] = sum;
        }
        return correlated;
    }

    /**
     * Computes normalized square difference function for the given input.
     *
     * @param original The array of original time-domain data.
     * @param correlated The array holding the autocorrelation function for the
     * input data. Used to make calculating the NSDF more efficient.
     * @return The NSDF for the input data.
     */
    private double[] computeNSD(double[] original,
                                double[] correlated)
    {
        int size = original.length;
        double squared[] = new double[size];
        //this is the m'(tau) component of the SDF
        for (int tau = 0; tau < size - 1; tau++)
        {
            double sum = 0;
            for (int j = 0; j < size - 1 - tau; j++)
            {
                sum += Math.pow(original[j], 2.0)
                        + Math.pow(original[j + tau], 2.0);
            }
            squared[tau] = sum;
        }
        //Normalization: n'(tau) = 2 * r'(tau) / m'(tau).
        //Range should be [-1, 1].
        for (int i = 0; i < size; i++)
        {
            original[i] = (2.0 * correlated[i]) / squared[i];
        }
        return original;
    }

    /**
     * Attempts to identify the fundamental frequency of the normalized data.
     * Implements the peak picking described in "A Smarter Way to Find Pitch,"
     * by Philip McLeod and Geoff Wyvill:
     * http://miracle.otago.ac.nz/tartini/papers/A_Smarter_Way_to_Find_Pitch.pdf
     *
     * @param input The array of normalized data derived from original
     * time-domain sample data.
     * @return The fundamental frequency determined for the input data.
     */
    private double pickPeakWithoutFFT(double[] input)
    {
        boolean negativeZeroCrossing = false;   //<<--checks for negatively
        //double[] localMaxima = new double[1000];        //sloped zero crossings
        List<Double> localMaxima = new ArrayList<Double>();
        //indices holds locations of positively sloped zero crossings
        //int[] indices = new int[localMaxima.size()];
        List<Integer> indices = new ArrayList<Integer>();
        int numberOfMaxima = 0; //# of local maxima discovered thus far
        //look for positively sloped zero crossings in input data
        for (int i = 1; i < input.length; i++)
        {
            //if a pos. sloped zero crossing is found, mark its location
            if (input[i] > 0 && input[i - 1] <= 0)
            {
                indices.add(i);
                numberOfMaxima++;
                negativeZeroCrossing = !negativeZeroCrossing;
            } else if (input[i] <= 0 && input[i - 1] > 0)
            {
                negativeZeroCrossing = !negativeZeroCrossing;
            }
        }
        //look for local maxima between indices
        int index = 0;
        double localMax;
        int localMaxIndex;
        while (index < numberOfMaxima - 1 && indices.get(index) != null)
        {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices.get(index); i < indices.get(index + 1); i++)
            {
                if (input[i] > localMax)
                {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima.add(index, localMax);
            indices.remove(index);
            indices.add(index, localMaxIndex);
            index++;
        } //end while
        //if the last local max was followed by a negatively sloped
        //zero crossing, add it to the array.
        if (negativeZeroCrossing && index > 1)
        {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices.get(index); i < input.length; i++)
            {
                if (input[i] > localMax)
                {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima.add(index, localMax);
            indices.remove(index);
            indices.add(index, localMaxIndex);
            index++;
        } //end while
        else
        {
            numberOfMaxima--;  //otherwise, decrement # of maxima
        }
        //find highest local maximum
        double highestMax = localMaxima.get(0);
        int j = 1;
        while (j < localMaxima.size())
        {
            double toCheck = localMaxima.get(j);
            if (toCheck > highestMax)
            {
                highestMax = toCheck;
                CONFIDENCE = highestMax;
            }
            j++;
        }
        //System.out.println("Confidence = " + CONFIDENCE);
        if (highestMax < 0.325)
        { //clarity/confidence check
            return 0; //ignore frequency if confidence is below above value
        }
        double threshold = highestMax * K_CONSTANT;
        j = 0;
        double testPitch = localMaxima.get(0);
        while (testPitch < threshold)
        { //find first local maximum
            j++;                        //above threshold
            testPitch = localMaxima.get(j);
        }
        //perform cubic interpolation to refine index of pitch period
        double refinedIndex = SAMPLE_RATE;
        if (0 < indices.get(j) - 1 && indices.get(j) + 2 < input.length)
        {
            int currentMaxLocation = indices.get(j);
            double newMaxLocation = (currentMaxLocation - 1)
                    + refineMax(input[currentMaxLocation - 1],
                    input[currentMaxLocation], input[currentMaxLocation + 1],
                    input[currentMaxLocation + 2]);
            refinedIndex = newMaxLocation;
        }
        return SAMPLE_RATE / refinedIndex;
    }

    /**
     * Uses cubic interpolation to refine the location of the lag value that
     * corresponds to the fundamental frequency. Adapted from code originally
     * written by Dominic Mazzoni.
     *
     * @param y0 The index of the sample taken just before the one that
     * corresponds to the fundamental frequency.
     * @param y1 The index of the sample that corresponds to the fundamental
     * frequency.
     * @param y2 The index of the sample taken just after the one that
     * corresponds to the fundamental frequency.
     * @param y3 The index of the second sample taken after the one that
     * corresponds to the fundamental frequency.
     * @return A refined value for the index of the sample that corresponds to
     * the fundamental frequency.
     */
    private double refineMax(double y0, double y1, double y2, double y3)
    {
        // Find coefficients of cubic
        double a = (y0 / -6.0 + y1 / 2.0 - y2 / 2.0 + y3 / 6.0);
        double b = (y0 - 5.0 * y1 / 2.0 + 2.0 * y2 - y3 / 2.0);
        double c = (-11.0 * y0 / 6.0 + 3.0 * y1 - 3.0 * y2 / 2.0 + y3 / 3.0);
        double d = y0;

        // Take derivative
        double da, db, dc;
        da = 3 * a;
        db = 2 * b;
        dc = c;

        // Find zeroes of derivative using quadratic equation
        double discriminant = db * db - 4 * da * dc;
        if (discriminant < 0.0)
        {
            return -1; // error
        }
        double x1 = (-db + Math.sqrt(discriminant)) / (2 * da);
        double x2 = (-db - Math.sqrt(discriminant)) / (2 * da);

        // The one which corresponds to a local _maximum_ in the
        // cubic is the one we want - the one with a negative
        // second derivative
        double dda = 2 * da;
        double ddb = db;

        if (dda * x1 + ddb < 0)
        {
            return x1;
        } else
        {
            return x2;
        }
    }

    private AudioFormat getAudioFormat()
    {
        float sampleRate = (float) SAMPLE_RATE;
        int sampleSizeInBits = SAMPLE_SIZE;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }

    public void stopCapture()
    {
        stopCapture = true;
    }

    public void setThisMeasure(boolean isIt)
    {
        thisMeasure = isIt;
    }

    public class CaptureThread extends Thread
    {

        public void run()
        {
            //number of samples to capture before putting data in the queue
            double samplesToCapture = (SAMPLE_RATE
                    / (score.getTempo() / score.getMetre()[0] / 60.0))
                    * captureInterval / 480;
            try
            {//Loop until stopCapture is set.
                while (!stopCapture)
                {
                    //wait for notification from Notate/Timer
                    synchronized (thisMeasure)
                    {
                        thisMeasure.wait();
                    }
                    ByteArrayOutputStream outputStream =
                            new ByteArrayOutputStream();
                    byte tempBuffer[] = new byte[FRAME_SIZE];
                    int limit = (int) (samplesToCapture / (FRAME_SIZE / 2) - 1);
                    if (!processingStarted)
                    {
                        limit -= 3;
                        startTime = System.currentTimeMillis();
                        System.out.println("First capture start = "
                                + startTime);
                    }
                    else {
                        System.out.println("Next capture started at time "+
                                System.currentTimeMillis());
                    }
                    //collect 1 captureInterval's worth of data
                    for (int n = 0; n < limit; n++)
                    {
                        //byte tempBuffer[] = new byte[FRAME_SIZE];
                        //cnt = number of bytes read
                        int cnt = target.read(tempBuffer, 0, tempBuffer.length);
                        if (cnt > 0)
                        {   //Save data in output stream object.
                            outputStream.write(tempBuffer, 0, cnt);
                        }
                        if (n >= limit - 3) {
                            System.out.println("Frame " + n + " finished at time "
                                    + System.currentTimeMillis());
                        }
                    }
                    System.out.println("Audio capture interval finished.");
                    if (!processingStarted)
                    {
                        long difference = (midiSynth.getPlaybackStartTime()
                                + score.getCountInTime() * 1000 - startTime);
                        System.out.println("difference = " + difference);
                        if (difference < 0)
                        {
                            difference = 0;
                        }
                        positionOffset = (int) (difference / 1000.
                                                    * SAMPLE_RATE * 2.);
                        System.out.println("positionOffset = " + positionOffset);
                    }
                    if (outputStream.size() > 0)
                    {
                        byte[] capturedAudioData = outputStream.toByteArray();
                        synchronized (processingQueue)
                        {
                            try
                            {
                                processingQueue.add(capturedAudioData);
                                System.out.println("Array containing " +
                                        capturedAudioData.length + " elements added "
                                        + "to processing queue. " + "Queue now contains "
                                        + processingQueue.size() + " element(s).");
                                processingQueue.notify();
                                isCapturing = false;
                            } catch (Exception e)
                            {
                                System.out.println("Processing queue error: \n" + e);
                            }
                        }
                    } else
                    {
                        //CaptureThread.sleep(0, 10);
                    }
                }//end while
            } catch (Exception e)
            {
                System.out.println("Capture thread error: \n" + e);
            }//end catch
        }//end run
    }//end inner class CaptureThread

    /**
     * Analyzes data from the processing queue.
     */
    public class AnalyzeThread extends Thread
    {

        public void run()
        {
            while (!stopAnalysis)
            {
                synchronized (processingQueue)
                {
                    try
                    {
                        processingQueue.wait();
                    } catch (Exception e)
                    {
                        System.out.println("wait error: \n" + e);
                    }
                }
                try
                {
                    if (!processingQueue.isEmpty()) {
                        byte result[] = processingQueue.poll();
                        System.out.println("Array removed from processing queue.");
                        System.out.println("__________New Capture___________");
                        processingStarted = true;
                        parseNotes(result);
                    }
                    else
                    {
                        System.out.println("Queue is empty.");
                    }
                } catch (Exception e)
                {
                    System.out.println("AnalyzeThread error: \n" + e);
                    firstCapture = false;
                    startingPosition += captureInterval;
                }
            }
        } //end run
    }//end AnalyzeThread
}