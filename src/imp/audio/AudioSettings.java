package imp.audio;

import imp.gui.Notate;

/**
 * Handles settings for audio capture.
 *
 * @version 27 July, 2012
 * @author Brian
 */
public class AudioSettings
{
    private int FRAME_SIZE = 2048; //# of BYTES examined per poll
    private float POLL_RATE = 20; //in milliseconds
    private int RESOLUTION = 16; //smallest subdivision allowed
    private boolean TRIPLETS = false;
    private double RMS_THRESHOLD = Math.exp(4.75);
    private double CONFIDENCE_THRESHOLD = 0.45;
    //sets threshold for detecting peaks in normalized SDF
    private double K_CONSTANT = 0.875;
    Notate notate;

    public AudioSettings(Notate notate) {
        this.notate = notate;
    }

    public void setCONFIDENCE_THRESHOLD(double CONFIDENCE_THRESHOLD)
    {
        this.CONFIDENCE_THRESHOLD = CONFIDENCE_THRESHOLD;
    }

    public void setFRAME_SIZE(int FRAME_SIZE)
    {
        this.FRAME_SIZE = FRAME_SIZE;
    }

    public void setK_CONSTANT(double K_CONSTANT)
    {
        this.K_CONSTANT = K_CONSTANT;
    }

    public void setPOLL_RATE(float POLL_RATE)
    {
        this.POLL_RATE = POLL_RATE;
    }

    public void setRESOLUTION(int RESOLUTION)
    {
        this.RESOLUTION = RESOLUTION;
    }

    public void setRMS_THRESHOLD(double RMS_THRESHOLD)
    {
        this.RMS_THRESHOLD = RMS_THRESHOLD;
    }

    public void setTRIPLETS(boolean TRIPLETS)
    {
        this.TRIPLETS = TRIPLETS;
    }

    public double getCONFIDENCE_THRESHOLD()
    {
        return CONFIDENCE_THRESHOLD;
    }

    public int getFRAME_SIZE()
    {
        return FRAME_SIZE;
    }

    public double getK_CONSTANT()
    {
        return K_CONSTANT;
    }

    public float getPOLL_RATE()
    {
        return POLL_RATE;
    }

    public int getRESOLUTION()
    {
        return RESOLUTION;
    }

    public double getRMS_THRESHOLD()
    {
        return RMS_THRESHOLD;
    }

    public boolean isTRIPLETS()
    {
        return TRIPLETS;
    }
}