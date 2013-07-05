package imp.audio;

import java.awt.Frame;
import java.io.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Anna Turner
 * @author mkyong, from
 * http://www.mkyong.com/java/how-to-detect-os-in-java-systemgetpropertyosname/
 * @since June 27 2013
 */

public class SCHandler {
    
    private static String opSystem = System.getProperty("os.name").toLowerCase();
    private static boolean isWindows, isOsX, isLinux;
    private static Process process;
    
    /**
     * According to OS, open SuperCollider.
     * 
     * The Startup file included in the download runs automatically upon
     * opening of SC.
     */
    public void openSC(){
        //Making sure to reset the booleans
            isWindows = false;
            isOsX = false;
            isLinux = false;
        
        //Now check for os
            if (opSystem.contains("win")){
                isWindows = true;
            } else if (opSystem.contains("mac")){
                isOsX = true;
            } else if (opSystem.contains("linux")){
                isLinux = true;
            } else {
                System.out.println("Your OS is not currently supported.");
                return;
            }           
        
        //Handle SuperCollider opening based on os
		try {
			Runtime runTime = Runtime.getRuntime();
                        
                        //@TODO fix os stuff
                        if(isWindows){
                            //process = null;
                            process = runTime.exec("SuperCollider");
                        } else if (isOsX){
                            process = runTime.exec("open /Applications/SuperCollider/SuperCollider.app");
                        } else {//is linux
                            process = null;
                            //process = runTime.exec();
                        }
                        //@TODO add GUI functionality in notate
		} catch (IOException e) {
			e.printStackTrace();
		}               
    
    }
 
       
}