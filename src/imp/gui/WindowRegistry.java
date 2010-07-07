/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2009 Robert Keller and Harvey Mudd College
 *
 * Impro-Visor is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Impro-Visor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * merchantability or fitness for a particular purpose.  See the
 * GNU General Public License for more details.
 *

 * You should have received a copy of the GNU General Public License
 * along with Impro-Visor; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */

package imp.gui;

import javax.swing.*;
import java.util.Vector;
import imp.util.Trace;

/**
 * This is a singleton class to hold information about Notate and StyleEditor
 * windows.
 *
 * @author Martin Hunt, Robert Keller
 */

public class WindowRegistry {
  
  public static final int defaultXnewWindowStagger = 40;
    
  public static final int defaultYnewWindowStagger = 40;

 /**
   *
   * Return the vector of all registered windows
   *
   */
  
   public static Vector<WindowMenuItem> getWindows()
   {
     return window;
   }

 /**
   *
   * A vector to hold all the windows
   *
   */
  
  private static Vector<WindowMenuItem> window = new Vector<WindowMenuItem>();
  
  
  
  /**
   *
   * A counter for assigning a unique id to each new window
   *
   */
  
  public static int windowNumber = 1;
  
  
  
  /**
   *
   * A function to register a window (used by Notate's constructor)
   *
   * Adds the window to the vector of windows, allowing it to appear in the Window menu
   *
   */
  
  public static void registerWindow(JFrame w) {
      
      Trace.log(2, "Notate: window registered: " + w);
      
      WindowMenuItem wmi = new WindowMenuItem(w);
      
      window.add(wmi);
      
  }
  
  
  
  /**
   *
   * A function to unregister a window (when close window is called)
   *
   * Handles removal of a window from the vector of windows
   *
   * Closes midi devices and exits when all windows are removed from the
   *
   * vector of windows
   *
   */
  
  public static void unregisterWindow(JFrame w) {
      
      Trace.log(2, "Window unregistered: " + w);
      
      
      for(WindowMenuItem i : window) {
          
          if(i.getWindow() == w) {
              
              window.remove(i);
              
              break;
           }
      }
      
      
      if(window.size() == 0) {
          
          Trace.log(2, "Notate: No more registered windows, exiting.");
          
          imp.ImproVisor.getMidiManager().closeDevices();
          
          System.exit(0);
          
      }
      
  }
  
  
  /**
   *
   * Organizes windows
   *
   */
  
  public static void cascadeWindows(JFrame w)
    {
    int x = 0, y = 0;

    for( WindowMenuItem i : window )
      {

      if( i.getWindow() == w )
        {
        continue;
        }

      i.getWindow().setLocation(x, y);

      i.getWindow().toFront();

      x += defaultXnewWindowStagger;

      y += defaultYnewWindowStagger;
      }

    w.setLocation(x, y);

    w.requestFocus();

    w.toFront();
    }
  
}
