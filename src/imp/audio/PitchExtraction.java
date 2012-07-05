package imp.audio;

import imp.data.MelodyPart;
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
public class PitchExtraction
{

    public volatile Boolean stopCapture = true;
    boolean stopAnalysis;
    public volatile Boolean thisMeasure = false;
    //boolean firstParse = true;
    int lastPitch;
    AudioInputStream inputStream;
    AudioFormat format;
    SourceDataLine source;
    TargetDataLine target;
    Score score;
    Notate notate;
    int captureInterval;
    int analysesCompleted = 0;
    private static final float SAMPLE_RATE = 44100.0F; //in Hertz
    private static final int SAMPLE_SIZE = 16; //1 sample = SAMPLE_SIZE bits
    private static final int FRAME_SIZE = 2048; //# of BYTES examined per poll
    private static final float POLL_RATE = 20; //in milliseconds
    private static int RESOLUTION = 8;
    private static boolean TRIPLETS = false;
    private static double RMS_THRESHOLD = 4.5;
    private static double CONFIDENCE = 0;
    //DoubleFFT_1D fft;
    private static boolean noteOff; //flag indicating terminal note
    //sets threshold for detecting peaks in normalized data
    private static final double K_CONSTANT = 0.8;
    //private final TextField tempoField;
    private Queue<byte[]> processingQueue;

    public static void main(String args[])
    {
        //new PitchExtraction();
    }//end main

    public PitchExtraction(Notate notate, Score score, int captureInterval)
    {
        this.notate = notate;
        this.score = score;
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
        target.close();
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
        //ignore first 5ms of input data; begin polling thereafter
        //int index = (int)((5.0 / 1000.0) * SAMPLE_RATE * 2.0);

        MelodyPart melodyPart = notate.getCurrentMelodyPart();
        //where the first note is to be inserted in the melody
        int startingPosition = analysesCompleted * captureInterval;
        int index = 0;
        double interval = ((POLL_RATE / 1000.0) * SAMPLE_RATE) * 2.0;
        int size = FRAME_SIZE / 2;
        //convert tempo to ms per measure
        float tempo = (float) (4.0 * 60000.0 / score.getTempo());
        int slotSize = RESOLUTION; //smallest subdivision allowed
        if (TRIPLETS)
        { //adjust minimum slot size if triplets are allowed
            slotSize *= (3.0 / 2.0);
        }
        int lastSlotNumber = 1;
        int currentSlotNumber;
        lastPitch = 0; //initialize most recent pitch to a rest
        int slotsFilled = 1; //# of slots filled before pitch change is detected
        List<Integer> oneSlot = new ArrayList<Integer>();
        while (index + FRAME_SIZE < streamInput.length)
        {
            byte[] oneFrame = new byte[FRAME_SIZE];
            //break input into frames
            for (int i = index; i < index + FRAME_SIZE; i++)
            {
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
            double timeElapsed = index / interval * POLL_RATE;
            currentSlotNumber = resolveSlot(timeElapsed,
                    tempo / slotSize, slotSize) + 1;
            System.out.println("At time " + timeElapsed
                    + ", Slot = " + currentSlotNumber);
            int slotPitch = 0;
            //check to see if pitch is valid
            if (fundamentalFrequency > 34.0)
            {
                slotPitch = //calculate equivalent MIDI pitch value for freq.
                        jm.music.data.Note.freqToMidiPitch(fundamentalFrequency);
            }
            //check to see if this window is part of the current slot
            if (currentSlotNumber == lastSlotNumber)
            {
                oneSlot.add(slotPitch); //if so, continue collecting data
            } //end if
            //if all windows for this slot have been examined, determine pitch
            else if (currentSlotNumber != lastSlotNumber || index + FRAME_SIZE
                    + interval >= streamInput.length)
            {
                int pitch = calculatePitch(oneSlot);
                //check to see whether or not pitch has changed from that
                //which fills the previous slot
                if (pitch != lastPitch || noteOff)
                {
                    int duration = slotsFilled * 480 / RESOLUTION;
                    if (pitch < 25) //count as a rest if pitch is out of range
                    {
                        imp.data.Note newRest = new Rest(duration);
                        melodyPart.setNote(startingPosition, newRest);
                        System.out.println("rest, duration = "
                                + duration + " slots.");
                    } else
                    {
                        imp.data.Note newNote = new imp.data.Note(pitch, duration);
                        melodyPart.setNote(startingPosition, newNote);
                        System.out.println(newNote.getPitch() + ", duration = "
                                + duration + " slots.");
                    }
                    startingPosition += duration;
                    slotsFilled = 1; //reset slotsFilled when pitch changes
                } //if this pitch is the same as that of the last slot,
                //continue building duration until pitch changes
                else
                { //if pitch hasn't changed, increment # of slots filled
                    slotsFilled++;
                }
                lastPitch = pitch;
                if (currentSlotNumber % slotSize == 0)
                {
                    lastSlotNumber = RESOLUTION;
                } else
                {
                    lastSlotNumber = currentSlotNumber % slotSize;
                }
                oneSlot.clear(); //get rid of old list
                oneSlot.add(slotPitch);
            } //end else if
            else
            {
                //Do something to handle final sample window?
            } //end else
            //increase the index by the designated interval
            index += (int) interval;
        } //end while
        analysesCompleted++;
        System.out.println("checked all.");
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
        return rms > RMS_THRESHOLD;
    }

    /**
     * Examines the pitches detected for the current slot and determines pitch
     *
     * @param pitches The array of pitches detected for this slot
     */
    private int calculatePitch(List<Integer> pitchList)
    {
        noteOff = false;
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
                else if (occurrences[i] == maxO && maxLoc != i)
                {
                    tie = true;
                    secondPlaceLoc = i;
                }
            } //end for
        } //end else
        //check to see whether or not this tone is terminal
        if (pitches[pitches.length - 2] == 0
                && pitches[pitches.length - 1] == 0)
        {
            noteOff = true;
        }
        if (maxO - secondPlaceLoc > 1)
        {
            return pitches[maxLoc];
        } //check for false readings in lower octaves
        else if (maxO == secondPlaceO && Math.abs(pitches[maxLoc]
                - pitches[secondPlaceLoc]) > 11)
        {
            if (pitches[maxLoc] - pitches[secondPlaceLoc] > 0)
            {
                return pitches[maxLoc];
            } else
            {
                return pitches[secondPlaceLoc];
            }
        } //end if
        else if (tie)
        {
            //algorithm is more prone to sub-fundamental errors
            if (secondPlaceLoc >= pitches.length / 2 && Math.abs(pitches[maxLoc]
                    - pitches[secondPlaceLoc]) < 12)
            {
                return pitches[secondPlaceLoc];
            } else
            {
                return Math.max(pitches[maxLoc], pitches[secondPlaceLoc]);
            }
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
        int slot = (int) Math.floor(timeElapsed / msPerSlot);
        return slot % slotSize;
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
        double[] localMaxima = new double[1000];        //sloped zero crossings
        //indices holds locations of positively sloped zero crossings
        int[] indices = new int[localMaxima.length];
        int numberOfMaxima = 0; //# of local maxima discovered thus far
        //look for positively sloped zero crossings in input data
        for (int i = 1; i < input.length; i++)
        {
            //if a pos. sloped zero crossing is found, mark its location
            if (input[i] > 0 && input[i - 1] <= 0)
            {
                indices[numberOfMaxima] = i;
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
        while (index < numberOfMaxima - 1 && indices[index] != 0)
        {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices[index]; i < indices[index + 1]; i++)
            {
                if (input[i] > localMax)
                {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima[index] = localMax;
            indices[index] = localMaxIndex;
            index++;
        } //end while
        //if the last local max was followed by a negatively sloped
        //zero crossing, add it to the array.
        if (negativeZeroCrossing && index > 1)
        {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices[index]; i < input.length; i++)
            {
                if (input[i] > localMax)
                {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima[index] = localMax;
            indices[index] = localMaxIndex;
            index++;
        } //end while
        else
        {
            numberOfMaxima--;  //otherwise, decrement # of maxima
        }
        //find highest local maximum
        double highestMax = localMaxima[0];
        int j = 1;
        while (localMaxima[j] != 0 && j < localMaxima.length)
        {
            if (localMaxima[j] > highestMax)
            {
                highestMax = localMaxima[j];
                CONFIDENCE = highestMax;
            }
            j++;
        }
        double threshold = highestMax * K_CONSTANT;
        j = 0;
        double testPitch = localMaxima[0];
        while (testPitch < threshold)
        { //find first local maximum
            j++;                        //above threshold
            testPitch = localMaxima[j];
        }
        if (highestMax < 0.325)
        { //clarity/confidence check
            return 0; //ignore frequency if confidence is below above value
        }
        //perform cubic interpolation to refine index of pitch period
        double refinedIndex = SAMPLE_RATE;
        if (0 < indices[j] - 1 && indices[j] + 2 < input.length)
        {
            int currentMaxLocation = indices[j];
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
            System.out.println("Audio capture initialized");
            //number of samples in each measure given the tempo & metre
            double samplesToCapture = SAMPLE_RATE /
                            (score.getTempo() / score.getMetre()[0] / 60.0);
            try
            {//Loop until stopCapture is set.
                while (!stopCapture)
                {
                    //wait for notification from Notate
                    synchronized (thisMeasure)
                    {
                        thisMeasure.wait();
                    }
                    ByteArrayOutputStream outputStream =
                            new ByteArrayOutputStream();
                    //collect 1 measure's worth of data
                    for (int n = 0; n < samplesToCapture / (FRAME_SIZE / 2) - 1; n++)
                    {
                        //An arbitrary-size temporary holding buffer
                        byte tempBuffer[] = new byte[FRAME_SIZE];
                        //cnt = # of bytes read
                        int cnt = target.read(tempBuffer, 0, tempBuffer.length);
                        if (cnt > 0)
                        {   //Save data in output stream object.
                            outputStream.write(tempBuffer, 0, cnt);
                        }
                    }
                    if (outputStream.size() > 0)
                    {
                        byte[] capturedAudioData = outputStream.toByteArray();
                        synchronized (processingQueue)
                        {
                            try
                            {
                                processingQueue.add(capturedAudioData);
                                processingQueue.notify();
                                System.out.println("processing queue has "
                                        + processingQueue.size() + " element(s)");
                            } catch (Exception e)
                            {
                                System.out.println(e);
                            }
                        }
                    } else
                    {
                        System.out.println("between measures...");
                        CaptureThread.sleep(0, 10);
                    }
                }//end while
                System.out.println("CaptureThread exited while loop.");
            } catch (Exception e)
            {
                System.out.println(e);
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
//                while (processingQueue.isEmpty())
//                {
//                    try
//                    {
//                        AnalyzeThread.sleep(20);
//                        //processingQueue.wait();
//                    } catch (Exception e)
//                    {
//                        System.out.println(e);
//                    }
//                }
                synchronized (processingQueue)
                {
                    try
                    {
                        processingQueue.wait();
                    } catch (Exception e)
                    {
                        System.out.println(e);
                    }
                }
                try
                {
                    byte result[] = processingQueue.poll();
                    if (result != null)
                    {
                        System.out.println("passing byte[] to parseNotes()...");
                        parseNotes(result);
                    } else
                    {
                        System.out.println("Empty result");
                    }
                } catch (Exception e)
                {
                    System.out.println(e);
                }
            }
            System.out.println("AnalyzeThread thinks it has finished.");
        } //end run
    }//end AnalyzeThread
}