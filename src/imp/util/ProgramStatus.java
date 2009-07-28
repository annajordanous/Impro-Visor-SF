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

package imp.util;

import imp.ImproVisor;
import java.awt.Color;

/**
 * Recoded to work with multiple windows.  This is a quick hack so that the rest
 * of the code still works with this singleton ProgramStatus class.  Really, this
 * class should be removed and the functionality moved to Notate/Stave.
 */

/**
 * ProgramStatus  is intended as a singleton class.  It includes a static
 * methods that can be called from anywhere to indicate status.
 * It maintains and shows the status in a text field in the GUI.
 */

public class ProgramStatus
{
    private static javax.swing.JTextField statusText;

    /**
     * Call to log message of any type.
     */

    public static void setStatus(String message) {
        ImproVisor.getCurrentWindow().setStatus(message);
    }
    
    public static void setVocabularyRead(boolean value) {
        if(ImproVisor.getCurrentWindow() == null)
            return;
        
        if(value) {
            ImproVisor.getCurrentWindow().setStatusColor(Color.black);
            ImproVisor.getCurrentWindow().getCurrentStave().requestFocusInWindow();
        } else {
            ImproVisor.getCurrentWindow().setStatusColor(Color.red);
            ImproVisor.getCurrentWindow().setStatus("Please wait while vocabulary is read . . .");
        }
    }
}