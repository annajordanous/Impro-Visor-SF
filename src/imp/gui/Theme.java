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
import static imp.Constants.LCROOT;
import imp.data.Note;
import imp.data.Part;
import imp.data.Unit;
import polya.Polylist;

/**
 *
 * @author Nava Dallal
 */
public class Theme {
    
 MelodyPart melody; 

 String name;
 
 int ThemeLength;
 
 
 int serial;
 
 int numDiscriminators = 0;
 
 String discriminator[] = new String[maxDiscriminators];
 
 static int maxDiscriminators = 2;
 
 int discriminatorOffset[] = new int[maxDiscriminators];
 
 public Theme(MelodyPart melody)
 {
     this.melody = melody;
 }
 private static LinkedHashMap<String, Theme> allThemes = new LinkedHashMap<String, Theme>();
 private static ArrayList<Theme> orderedThemes = null;
 

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
     
     String getName() { return name; } 
     
     Polylist getNotes()
  {
  return Polylist.list(melodyToString(melody));
  }
     
     public static String melodyToString(MelodyPart melody){
         Part.PartIterator i = melody.iterator(); //iterate over lick
                    String theme = ""; //set theme as empty to start
                    
                    while (i.hasNext()) //while you can still iterate through the lick
                    {
                        Unit unit = i.next();
                        if (unit != null) //if next isn't empty
                        {
                            theme += unit.toLeadsheet() + " "; //add it to the theme
                        }
                    }
                    return theme;
     } 
     
     public Polylist ThemetoPolylist(Theme theme){
        return Polylist.list("theme", Polylist.list("name", theme.name), Polylist.list("notes", theme.melodyToString(theme.melody)));
     } 
     
     public Theme(Polylist list) {
         String nameTheme = (String)list.first();
         MelodyPart melodyTheme = (MelodyPart)list.last();
         this.name = nameTheme;
         this.melody = melodyTheme;
     }
     
     
     
    public void showForm(java.io.PrintStream out) {
        out.println("(theme "
                + "(name " + getName()
                + ")(notes " + melodyToString(melody)
                + "))");
    }
 
}
