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

package imp.lickgen;

import java.util.*;
import java.io.*;

/**
 *
 * @author Steven Gomez
 */

public class Contour {
    
    /*
     * Function to create the contour
     */
    public static String createContour(LickGen lickgen) {
        Vector<Vector<int[]>> v = getData();
        Random rand = new Random();
        Vector<int[]> phrase = v.get(rand.nextInt(v.size()));
        String result = "(";
        for(int i = 0; i < phrase.size()-1; i++) {
            int duration = phrase.get(i)[1];
            if (duration % 120 != 0) duration = duration + (120 - duration % 120);
            String temp = lickgen.generateRhythmFromGrammar(duration).toString();
            //randomly add in a goal note
            if(rand.nextInt(2) == 1) {
            result = result + "(slope " + phrase.get(i)[3] + " " + phrase.get(i)[4] + " " + temp.substring(1);
            }
            else {
            result = result + "(slope " + phrase.get(i)[3] + " " + phrase.get(i)[4] + " " + temp.substring(1,temp.length()-1) + " G8)";
            }
        }
        result = result + " (slope 0 0 G4 R8))";
        return result;
    }    
    
    /*
     * Function to get representative contours
     */
    public static Vector<Vector<int[]>> getData() {
        Vector<Vector<int[]>> v = new Vector<Vector<int[]>>();
        try {
            FileInputStream f_in = new FileInputStream("C:/Documents and Settings/keller/Desktop/Improvisor339/Contours/ClusterReps.data");

            // Read object using ObjectInputStream
            ObjectInputStream obj_in = new ObjectInputStream(f_in);

            // Read an object
            Object obj = obj_in.readObject();
            
            v = (Vector<Vector<int[]>>) obj;

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return v;
    }
    
}