/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.gui;

import imp.audio.PitchExtractor;
import java.util.TimerTask;

/**
 *
 * @author Brian Howell
 */
public class CaptureTimerTask extends TimerTask
{
    PitchExtractor extractor;

    public CaptureTimerTask(PitchExtractor extractor) {
        this.extractor = extractor;
    }

    public void run()
        {
            synchronized (extractor.thisMeasure) {
                extractor.isCapturing = true;
                extractor.thisMeasure.notify();
            }
        }
}