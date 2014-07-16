/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2014 Robert Keller and Harvey Mudd College
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
import imp.data.MelodyPart;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import static imp.Constants.BEAT;
import imp.data.Note;

/**
 *
 * @author Nava Dallal
 */
public class Theme {
    
 MelodyPart melody; 

 String name;
 
 int ThemeLength;
 
 public Theme(MelodyPart melody)
 {
     this.melody = melody;
 }
 private static LinkedHashMap<String, Theme> allThemes = new LinkedHashMap<String, Theme>();
 private static ArrayList<Theme> orderedThemes = null;
 
 
//  
//  public static int numberOfThemes()
//    {
//      ensureThemeArray();
//      return orderedThemes.size(); 
//    }
//  
// public static Theme getTheme(String name)
//    {
//      return allThemes.get(name);
//    }
//  
//  public static void setTheme(String name, Theme theme)
//    {
//      allThemes.put(name, theme);
//    }
//  
//    public static Theme getNth(int index)
//      {
//        ensureThemeArray();
//        return orderedThemes.get(index);
//      }
//    
//    private static void ensureThemeArray()
//      {
//            {
//                
//           orderedThemes = new ArrayList<Theme>(allThemes.values());
//            }       
//      }
//     public static boolean add(Theme e) {
//         return orderedThemes.add(e);
//     }
//     
//     public static boolean contains(Theme e) {
//         return orderedThemes.contains(e);
//     }
//     public static Theme get(String s) {
//         return allThemes.get(s);
//     }
//     
     public static Theme makeTheme(String name, MelodyPart theme) {
         Theme newTheme = new Theme(theme);
         newTheme.name = name;
         newTheme.ThemeLength = theme.size() /BEAT;
        // newTheme.ThemeLength = theme.getUnitList();
//         for (int i = 0; i < theme.getUnitList().size(); i++){
//             Note note = (Note)theme.getUnitList().get(i);
        //     note.getNoteLength();
         
         
         return newTheme;
     }
 
}
