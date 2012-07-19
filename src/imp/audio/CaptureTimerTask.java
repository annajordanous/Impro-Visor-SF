/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.audio;

import imp.audio.PitchExtractor;
import java.util.TimerTask;

/**
 *
 * @author Brian Howell
 */
public class CaptureTimerTask extends TimerTask
{

    PitchExtractor extractor;

    public CaptureTimerTask(PitchExtractor extractor)
    {
        this.extractor = extractor;
    }

    public void run()
    {
            while (extractor.isCapturing)
            {
                try
                {
                    //System.out.println("Timer delayed by 2 milliseconds");
                    Thread.sleep(2);
                } catch (Exception e)
                {
                    System.out.println("Sleep error:\n" + e);
                }
            }
            extractor.isCapturing = true;
            long time = System.currentTimeMillis();
            synchronized (extractor.thisMeasure)
        {
            extractor.thisMeasure.notify();
        }
            System.out.println("Timer triggered audio capture at time " + time);
    }
}