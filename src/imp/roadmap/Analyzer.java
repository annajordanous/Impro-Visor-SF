/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.roadmap;

/**
 *
 * @author keller
 */
public class Analyzer extends Thread
{
    private int Xoffset = 200;
    private int Yoffset = 50;
    
    RoadMapFrame frame;
    WaitAnalysisDialog dialog;
    boolean showJoinsOnCompletion;
    
    public Analyzer(RoadMapFrame frame, boolean showJoinsOnCompletion)
      {
        this.frame = frame;
        this.dialog = new WaitAnalysisDialog(frame, false, this);
        this.showJoinsOnCompletion = showJoinsOnCompletion;
      }
    
    @Override
    public void run()
      {
        dialog.setLocation(frame.getX()+Xoffset, frame.getY()+Yoffset);
        dialog.setVisible(true);
        frame.analyze(showJoinsOnCompletion);
        dialog.setVisible(false);
      }
    
    public void cancel()
      {
      }
    
}
