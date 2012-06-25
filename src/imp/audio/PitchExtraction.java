package imp.audio;

import imp.data.MelodyPart;
import imp.data.Note;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import javax.sound.sampled.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
//import org.apache.commons.math3.complex.Complex;

/**
 * Class to test audio input.
 *
 * @author Brian Howell
 * @version 22 June, 2012
 */
public class PitchExtraction extends JFrame {

    boolean stopCapture = false;
    private AudioInputStream inputStream;
    private ByteArrayOutputStream outputStream;
    private AudioFormat format;
    private SourceDataLine source;
    private TargetDataLine target;
    private byte[] capturedAudioData;
    public static final float SAMPLE_RATE = 44100.0F;
    public static final int SAMPLE_SIZE = 16;
    public static final int FRAME_SIZE = 2048; //# of bytes examined per poll
    public static final float POLL_RATE = 20; //in milliseconds
    public static int RESOLUTION = 8;
    public static boolean TRIPLETS = false;
    public static double RMS_THRESHOLD = 5.5;
    public static double CONFIDENCE = 0;
    //DoubleFFT_1D fft;
    //sets threshold for detecting peaks in normalized data
    public static final double K_CONSTANT = 0.925;
    public final TextField tempoField;
    
    MelodyPart melody;

    public static void main(String args[]) {
        new PitchExtraction();
    }//end main

    public PitchExtraction() {
        final JButton captureBtn = new JButton("Capture");
        final JButton stopBtn = new JButton("Stop");
        final JButton playBtn = new JButton("Play");
        final JButton transformBtn = new JButton("Transform");

        final JToggleButton tripletBtn = new JToggleButton("Triplets?");
        final JRadioButton quarterBtn = new JRadioButton("Quarter Notes");
        final JRadioButton eighthBtn = new JRadioButton("8th Notes", true);
        final JRadioButton sixteenthBtn = new JRadioButton("16th Notes");
        final JRadioButton thirtySecondBtn = new JRadioButton("32nd Notes");

        final Label tempoLabel = new Label("Tempo:");
        tempoField = new TextField("120.0");

        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        playBtn.setEnabled(false);
        transformBtn.setEnabled(false);

        format = getAudioFormat();

        //Register anonymous listeners
        captureBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                captureBtn.setEnabled(false);
                stopBtn.setEnabled(true);
                playBtn.setEnabled(false);
                //Capture input data from the
                // microphone until the Stop button is clicked.
                captureAudio(format);
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(captureBtn);

        stopBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                captureBtn.setEnabled(true);
                stopBtn.setEnabled(false);
                playBtn.setEnabled(true);
                transformBtn.setEnabled(true);
                //Terminate the capturing of
                // input data from the microphone.
                stopCapture = true;
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(stopBtn);

        playBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Play back all of the data that was saved during capture.
                playAudio();
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(playBtn);

        transformBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                //Play back all of the data that was saved during capture.                                                
                parseNotes(capturedAudioData);
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(transformBtn);

        getContentPane().add(tempoLabel);

        getContentPane().add(tempoField);

        quarterBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RESOLUTION = 4;
                eighthBtn.setSelected(false);
                sixteenthBtn.setSelected(false);
                thirtySecondBtn.setSelected(false);
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(quarterBtn);

        eighthBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RESOLUTION = 8;
                quarterBtn.setSelected(false);
                sixteenthBtn.setSelected(false);
                thirtySecondBtn.setSelected(false);
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(eighthBtn);

        sixteenthBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RESOLUTION = 16;
                quarterBtn.setSelected(false);
                eighthBtn.setSelected(false);
                thirtySecondBtn.setSelected(false);
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(sixteenthBtn);

        thirtySecondBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                RESOLUTION = 32;
                quarterBtn.setSelected(false);
                eighthBtn.setSelected(false);
                sixteenthBtn.setSelected(false);
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()   
        getContentPane().add(thirtySecondBtn);

        tripletBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                TRIPLETS = !TRIPLETS;
            }//end actionPerformed
        }//end ActionListener
                );//end addActionListener()
        getContentPane().add(tripletBtn);

        getContentPane().setLayout(new FlowLayout());
        setTitle("Audio Capture Test");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(340, 134);

        setVisible(true);

        // fft = new DoubleFFT_1D(FRAME_SIZE / 2);
    }//end constructor

    private void captureAudio(AudioFormat audioFormat) {
        try {
            //Get everything set up for capture
            DataLine.Info dataLineInfo =
                    new DataLine.Info(TargetDataLine.class, audioFormat);
            target = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            target.open(audioFormat);
            target.start();
            //Create a thread to capture the microphone data and start it
            // running. It will run until the Stop button is clicked.
            Thread captureThread =
                    new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }//end catch
    }//end captureAudio method

    private void playAudio() {
        try {
            //Get everything set up for playback.
            //Get the previously-saved data into a byte array object.
            byte audioData[] = outputStream.toByteArray();
            //Get an input stream on the
            // byte array containing the data
            InputStream byteInputStream = new ByteArrayInputStream(audioData);
            //format = getAudioFormat();
            inputStream = new AudioInputStream(byteInputStream, format,
                    audioData.length / format.getFrameSize());
            DataLine.Info dataLineInfo =
                    new DataLine.Info(SourceDataLine.class, format);
            source = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            source.open(format);
            source.start();

            //Create a thread to play back the data and start it
            // running.  It will run until all the data has been played back.
            Thread playThread = new Thread(new PlayThread());
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }//end catch
    }//end playAudio

    private double getTempo() {
        double tempo = 0;
        try {
            tempo = Double.valueOf(tempoField.getText());
        } catch (Exception e) {
            System.out.println("Invalid tempo value.");
        }
        if (tempo < 60.0 || tempo > 300.0) {
            System.out.println("Tempo must be between 60 and 300 bpm. "
                    + "Tempo has defaulted to 120 bpm.");
        }
        return tempo;
    }

    /**
     * Breaks input data into frames and determines pitch for each frame.
     *
     * @param streamInput the array of bytes delivered by the TargetDataLine.
     */
    private void parseNotes(byte[] streamInput) {
        //ignore first 5ms of input data; begin polling thereafter
        //int index = (int)((5.0 / 1000.0) * SAMPLE_RATE * 2.0);
        int index = 0;
        double interval = ((POLL_RATE / 1000.0) * SAMPLE_RATE) * 2.0;
        int size = FRAME_SIZE / 2;
        //convert tempo to ms per measure
        float tempo = (float) (4 * 60000.0 / getTempo());
        int slotSize = RESOLUTION; //smallest subdivision allowed
        if (TRIPLETS) { //subdivide minimum slot size if triplets are allowed
            slotSize *= (3.0 / 2.0);
        }
        int lastSlotNumber = 1;
        int currentSlotNumber = 0;
        int lastPitch = 0; //initialize most recent pitch to a rest
        int slotsFilled = 0;
        int readings = 0; //number of polls per slot
        int[] oneSlot = new int[20];
        boolean first = true;
        melody = new MelodyPart();
        while (index + FRAME_SIZE < streamInput.length) {
            byte[] oneFrame = new byte[FRAME_SIZE];
            //break input into frames
            for (int i = index; i < index + FRAME_SIZE; i++) {
                oneFrame[i - index] = streamInput[i];
            }
            ByteBuffer bBuf = ByteBuffer.wrap(oneFrame);
            //new array to store double values
            double[] preCorrelatedData = new double[size];
            for (int i = 0; i < size; i++) { //populate array
                short s = bBuf.getShort(); //change to short (16bit signed)
                preCorrelatedData[i] = (double) s; //cast as double          
            }
            double fundamentalFrequency;
            //only attempt to determine pitch if RMS is above threshold
            if (checkRMS(preCorrelatedData)) {
                double[] correlatedData = new double[size];
                correlatedData = computeAutocorrelationNoFFT(preCorrelatedData);
                double[] computedData = new double[size];
                computedData = computeNSD(preCorrelatedData, correlatedData);
                fundamentalFrequency = pickPeakWithoutFFT(computedData);
            } else //otherwise, assign fundamental to zero
            {
                fundamentalFrequency = 0;
            }
            currentSlotNumber = resolveSlot(index / interval * POLL_RATE,
                    tempo / slotSize, slotSize) + 1;
            int slotPitch = 0;
            //check to see if pitch is valid
            if (fundamentalFrequency > 34.0) {
                slotPitch = getMidiPitch(fundamentalFrequency);
            }
            //keep track of all pitch results for this slot
            //check to see if this window is part of the current slot
            if (currentSlotNumber == lastSlotNumber
                    && index + FRAME_SIZE + interval < streamInput.length) {
                oneSlot[readings] = slotPitch;
                readings++;
            } else { //otherwise, determine pitch for the whole slot.
                //first, increment the number of slots filled by this pitch.
                int pitch = calculatePitch(oneSlot);
                if ((!first && pitch != lastPitch)
                        || index + FRAME_SIZE + interval > streamInput.length) {
                    int duration = 120 / slotsFilled;
                    if (pitch < 25) //count as a rest if pitch is out of range
                    {
                        Note newRest = Note.makeRest(duration);
                        melody.addNote(newRest);
                        System.out.println("rest, duration = "
                                + duration + " slots.");
                    } else {
                        Note newNote = new Note(pitch, duration);
                        melody.addNote(newNote);
                        System.out.println(", duration = "
                                + duration + " slots.");
                    }
                    slotsFilled = 1; //reset slotsFilled when pitch changes
                } //if this pitch is the same as that of the last slot,
                //continue building duration until pitch changes
                else {
                    if (first) {
                        first = false;
                    }
                    slotsFilled++;
                }
                lastPitch = pitch;
                lastSlotNumber = currentSlotNumber % slotSize;
                for (int s = 0; s < oneSlot.length; s++) //zero previous slot's 
                {
                    oneSlot[s] = 0;   //readings before adding new pitch
                }
                oneSlot[0] = slotPitch;
                readings = 0;
            }
            //increase the index by the designated interval
            index += (int) interval;
        } //end while
        System.out.println("checked all.");
    }

    /**
     * Determines the root mean square for the input array.
     *
     * @param data The array time-domain data (in double format).
     * @return A boolean value representing whether or not the RMS is above the
     * threshold.
     */
    private boolean checkRMS(double[] data) {
        //calculate root mean square of this window
        //to check for adequate sample data
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
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
    private int calculatePitch(int[] pitches) {
        //Check for discrepancies in this slot
        int testPitch = 0;
        boolean allZero = true;
        boolean d = false; //discrepancy test boolean
        int i = 0;
        while (!d && i < pitches.length) { //search for discrepancies in data
            if (pitches[i] != 0) {
                if (pitches[i] != testPitch && testPitch != 0) {
                    d = true; //multiple nonzero pitches have been found
                } else {
                    testPitch = pitches[i];
                }
                allZero = false;
            }
            i++;
        }
        int[] occurrences = new int[pitches.length];
        int maxLoc = 0;
        if (!d || allZero) //if there are no discrepancies, return the pitch
        {
            return testPitch;
        } else { //otherwise, find most frequently detected pitch
            testPitch = 0;
            for (i = 0; i < pitches.length; i++) {
                if (pitches[i] != 0 && pitches[i] != testPitch) {
                    testPitch = pitches[i]; //don't check same pitch twice...
                    for (int j = i + 1; j < pitches.length; j++) {
                        if (pitches[i] == pitches[j]) {
                            occurrences[i] += 1;
                        }
                    } //end for (j)
                } //end if
            } //end for (i)
            int maxO = 0;
            maxLoc = 0;
            for (i = 0; i < occurrences.length; i++) {
                if (occurrences[i] > maxO) {
                    maxO = occurrences[i];
                    maxLoc = i;
                } //end if
            } //end for
        } //end else
        return pitches[maxLoc];
    }

    /**
     * Resolves the current window into a slot.
     *
     * @param timeElapsed The amount of time that has elapsed since examination
     * first began (in milliseconds).
     * @param msPerSlot The number of milliseconds in each slot based on the
     * current minimum slot size (resolution).
     * @param slotSize The number of slots in each measure (resolution).
     * @return The slot in which this window falls.
     */
    private int resolveSlot(double timeElapsed,
            double msPerSlot, int slotSize) {
        int slot = (int) Math.round(timeElapsed / msPerSlot);
        return slot % slotSize;
    }
    
    /**
     * Gives the MIDI pitch for the provided frequency based on the equal
     * tempered scale.
     * 
     * @param freq the fundamental frequency of the musical tone.
     * @return the MIDI number corresponding to the given fundamental frequency.
     */
    private int getMidiPitch(double freq) {
        if(freq < 30.0 || freq > 12000.0)
            return 0;
        else return (int)(69.0 + 12.0 * (Math.log(freq/440.0)
                                                    / Math.log(2.0)));
    }

    /**
     * Computes autocorrelation for the given input.
     *
     * @param input the array of audio samples (in double format).
     * @return the correlated data.
     */
//    private double[] computeAutocorrelation(double[] input) {
//        int initialSize = input.length;
//        double[] correlated = new double[initialSize * 2];
//        for (int i = 0; i < initialSize; i++) {
//            correlated[i * 2] = input[i]; //expand array to accommodate 
//            correlated[i * 2 + 1] = 0; //imaginary parts and pad w/ zeros
//        }
//        fft.complexForward(correlated);
//        //after FFT, data is in the following form:
//        //a[2*k] = real[k] 
//        //a[2*k+1] = imaginary[k], 0<=k<n
//        double[] postCorrelated = new double[correlated.length * 2];
//        for (int i = 0; i < initialSize; i++) { //multiply each complex number
//            //by its conjugate
//            Complex com = new Complex(correlated[i * 2], correlated[i * 2 + 1]);
//            Complex conj = com.conjugate();
//            com = com.multiply(conj);
//            postCorrelated[i * 2] = com.getReal();
//        }
//        fft.complexInverse(postCorrelated, true);
//        for(int i = 0; i < correlated.length; i++)
//            correlated[i] = postCorrelated[i*2];
//        return correlated;
//    }
    /**
     * Computes the autocorrelation function for the given input as a function
     * of lag (tau).
     *
     * @param input The array of audio samples.
     * @return An array representing the autocorrelation function of the input
     * data.
     */
    private double[] computeAutocorrelationNoFFT(double[] input) {
        int size = input.length;
        double correlated[] = new double[size];
        //this is the m'(tau) component of the SDF
        for (int tau = 0; tau < size - 1; tau++) {
            double sum = 0;
            for (int j = 0; j < size - 1 - tau; j++) {
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
            double[] correlated) {
        int size = original.length;
        double squared[] = new double[size];
        //this is the m'(tau) component of the SDF
        for (int tau = 0; tau < size - 1; tau++) {
            double sum = 0;
            for (int j = 0; j < size - 1 - tau; j++) {
                sum += Math.pow(original[j], 2.0)
                        + Math.pow(original[j + tau], 2.0);
            }
            squared[tau] = sum;
        }
        //Normalization: n'(tau) = 2 * r'(tau) / m'(tau).
        //Range should be [-1, 1].
        for (int i = 0; i < size; i++) {
            original[i] = (2.0 * correlated[i]) / squared[i];
        }
        return original;
    }

    /**
     * Attempts to identify the fundamental frequency of the normalized data.
     *
     * @param input The array of normalized data derived from original
     * time-domain sample data.
     * @return The fundamental frequency determined for the input data.
     */
    private double pickPeakWithoutFFT(double[] input) {
        boolean negativeZeroCrossing = false;   //<<--checks for negatively         
        double[] localMaxima = new double[1000];        //sloped zero crossings
        //indices holds locations of positively sloped zero crossings
        int[] indices = new int[localMaxima.length];
        int numberOfMaxima = 0; //# of local maxima discovered thus far
        //look for positively sloped zero crossings in input data
        for (int i = 1; i < input.length; i++) {
            //if a pos. sloped zero crossing is found, mark its location
            if (input[i] > 0 && input[i - 1] <= 0) {
                indices[numberOfMaxima] = i;
                numberOfMaxima++;
                negativeZeroCrossing = !negativeZeroCrossing;
            } else if (input[i] <= 0 && input[i - 1] > 0) {
                negativeZeroCrossing = !negativeZeroCrossing;
            }
        }
        //look for local maxima between indices 
        int index = 0;
        double localMax;
        int localMaxIndex;
        while (index < numberOfMaxima - 1 && indices[index] != 0) {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices[index]; i < indices[index + 1]; i++) {
                if (input[i] > localMax) {
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
        if (negativeZeroCrossing && index > 1) {
            localMax = 0;
            localMaxIndex = 0;
            for (int i = indices[index]; i < input.length; i++) {
                if (input[i] > localMax) {
                    localMax = input[i];
                    localMaxIndex = i;
                } //end if
            } //end for
            localMaxima[index] = localMax;
            indices[index] = localMaxIndex;
            index++;
        } //end while
        else {
            numberOfMaxima--;  //otherwise, decrement # of maxima
        }
        //find highest local maximum
        double highestMax = localMaxima[0];
        int j = 1;
        while (localMaxima[j] != 0 && j < localMaxima.length) {
            if (localMaxima[j] > highestMax) {
                highestMax = localMaxima[j];
                CONFIDENCE = highestMax;
            }
            j++;
        }
        double threshold = highestMax * K_CONSTANT;
        j = 0;
        double testPitch = localMaxima[0];
        while (testPitch < threshold) { //find first local maximum 
            j++;                        //above threshold 
            testPitch = localMaxima[j];
        }
        if (highestMax < 0.325) { //clarity/confidence check
            return 0; //ignore frequency if confidence is below above value
        }
        //perform cubic interpolation to refine index of pitch period
        double refinedIndex = SAMPLE_RATE;
        if (0 < indices[j] - 1 && indices[j] + 2 < input.length) {
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
     * corresponds to the fundamental frequency. Adapted from C++ code
     * originally written by Dominic Mazzoni.
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
    private double refineMax(double y0, double y1, double y2, double y3) {
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
        if (discriminant < 0.0) {
            return -1; // error
        }
        double x1 = (-db + Math.sqrt(discriminant)) / (2 * da);
        double x2 = (-db - Math.sqrt(discriminant)) / (2 * da);

        // The one which corresponds to a local _maximum_ in the
        // cubic is the one we want - the one with a negative
        // second derivative  
        double dda = 2 * da;
        double ddb = db;

        if (dda * x1 + ddb < 0) {
            return x1;
        } else {
            return x2;
        }
    }

    /**
     * Determines the slot for this window.
     *
     * @param timeElapsed the amount of time elapsed thus far in milliseconds.
     * @param msPerSlot the number of slots in each measure.
     *
     * @return the slot for this window.
     */
    private AudioFormat getAudioFormat() {
        float sampleRate = (float) SAMPLE_RATE;
        int sampleSizeInBits = SAMPLE_SIZE;
        int channels = 1;
        boolean signed = true;
        boolean bigEndian = true;
        return new AudioFormat(sampleRate, sampleSizeInBits,
                channels, signed, bigEndian);
    }

    class CaptureThread extends Thread {
        //An arbitrary-size temporary holding buffer

        byte tempBuffer[] = new byte[FRAME_SIZE];

        public void run() {
            outputStream = new ByteArrayOutputStream();
            stopCapture = false;
            try {//Loop until stopCapture is set by 
                //another thread that services the Stop button.
                while (!stopCapture) {
                    //Read data from the internal buffer of the data line.
                    //cnt = # of bytes read
                    int cnt = target.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        //Save data in output stream object.
                        outputStream.write(tempBuffer, 0, cnt);
                    }//end if
                }//end while
                capturedAudioData = outputStream.toByteArray();
                outputStream.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }//end catch
        }//end run
    }//end inner class CaptureThread  

    class PlayThread extends Thread {

        byte tempBuffer[] = new byte[FRAME_SIZE];

        public void run() {
            try {
                int cnt;
                //Keep looping until the input read method 
                //returns -1 for empty stream.
                while ((cnt = inputStream.read(tempBuffer, 0,
                        tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        //Write data to the internal buffer of the data line
                        // where it will be delivered to the speaker.
                        source.write(tempBuffer, 0, cnt);
                    }//end if
                }//end while
                //Block and wait for internal buffer of the data line to empty.
                source.drain();
                source.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }//end catch
        }//end run
    }//end inner class PlayThread
}