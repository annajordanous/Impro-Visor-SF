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

package imp.lickgen.transformations;

import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.NoteSymbol;
import imp.data.Unit;
import imp.lickgen.LickGen;
import imp.util.ErrorLog;
import polya.*;
import java.util.*;
/**
 *
 * @author Alex Putman
 */
public class Transformation {
    
private Polylist sourceNotes;
private Polylist conditionGuard;
private Polylist targetNotes;
private int weight;
private boolean enabled;
private String description;
private LickGen lickGen;
public boolean debug;
public Transformation(LickGen lickGen)
{
    debug = false;
    this.lickGen = lickGen;
    description = (String) "new-transformation"; 
    sourceNotes = Polylist.PolylistFromString("n1");
    targetNotes = Polylist.PolylistFromString("n1");
    conditionGuard = Polylist.PolylistFromString("duration>= n1 128");
    weight = 1;
    enabled = true;
}
public Transformation(LickGen lickGen, String transString)
{
    this(lickGen);
    setTransformation((Polylist)Polylist.PolylistFromString(transString).first());
}
public Transformation(LickGen lickGen, Polylist trans)
{
    this(lickGen);
    setTransformation(trans);
}
public Transformation copy()
{
    StringBuilder copyString = new StringBuilder();
    toFile(copyString, "");
    Transformation copy = new Transformation(lickGen, copyString.toString());
    return copy;
}
public boolean setTransformation(Polylist trans)
{   
    StringBuilder copyString = new StringBuilder();
    
    toFile(copyString, "");
    String transCopy = copyString.toString();
    try{
        description = (String) trans.assoc("description").second(); 
        sourceNotes = (Polylist) trans.assoc("source-notes").rest();
        targetNotes = (Polylist) trans.assoc("target-notes").rest();
        conditionGuard = (Polylist) trans.assoc("guard-condition").second();
        weight = ((Long) trans.assoc("weight").second()).intValue();
        if(trans.assoc("enabled") != null)
        {
            enabled = Boolean.parseBoolean(trans.assoc("enabled").second().toString());
        }
        else
        {
            enabled = true;
        }
        enabled = true;
        return true;
    }
    catch( Exception e )
    {
        setTransformation((Polylist)Polylist.PolylistFromString(transCopy).first());
        ErrorLog.log(ErrorLog.SEVERE, "Syntax Error in Transformation File");
        return false;
    }
}

public MelodyPart apply(MelodyPart notes, ChordPart chords, int startingSlot)
{
    Evaluate eval = new Evaluate(lickGen, new Polylist());

    MelodyPart.PartIterator transferNotes = notes.iterator(startingSlot);
    
    PolylistEnum transSourceNotes = new PolylistEnum(sourceNotes);
    
    int newStartingSlot = startingSlot;
    int totalDurBefore = 0;
    while(transSourceNotes.hasMoreElements() && transferNotes.hasNext())
    {
        
        String varName = (String) transSourceNotes.nextElement();
        
        Note varNote = (Note) transferNotes.next();
        
        totalDurBefore += varNote.getRhythmValue();
        
        Chord varChord = chords.getCurrentChord(startingSlot);
        
        eval.setNoteVar(varName, varNote, varChord);
        
        newStartingSlot = notes.getNextIndex(newStartingSlot);
    }
    if(debug)
    {
        System.out.println("\t\t\tTrying Trans On: " + notes.extract(startingSlot, newStartingSlot - 1).toString());
    }
    if (transSourceNotes.hasMoreElements())
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Too Few Notes For Trans");
        }
        return null;
    }
    
    Boolean condition = (Boolean) eval.evaluate(conditionGuard);
    if(condition == null)
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Condition Error");
        }
        return null;
    }
    if(!condition)
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Condition False");
        }
        return null;
    }
    Evaluate targetEval = new Evaluate(lickGen, eval.getFrame(),"get-note");
    Polylist result = targetNotes.map(targetEval).flatten();
    MelodyPart resultingMP = new MelodyPart();
    PolylistEnum resultEnum = result.elements();
    int totalDurFinal = 0;
    while(resultEnum.hasMoreElements())
    {
        Note finalNote = (Note) resultEnum.nextElement();
        if(finalNote != null)
        {
            totalDurFinal += finalNote.getRhythmValue();
            resultingMP.addNote(finalNote);
        }
    }
    if(debug)
    {
        System.out.println("\t\t\t\tBefore time: " + totalDurBefore + "\t\t\t\tAfter time: "+ totalDurFinal);
    }
    if(totalDurBefore != totalDurFinal)
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Failed: Incorrect Time");
        }
        return null;
    }
    else
    {
        if(debug)
        {
            System.out.println("\t\t\tTrans Result: " + resultingMP.toString());
        }
        return resultingMP;
    }
}

public int numSourceNotes()
{
    return sourceNotes.length();
}

public int getWeight()
{
    return weight;
}

public void setWeight(int weight)
{
    this.weight = weight;
}

public boolean getEnabled()
{
    return enabled;
}

public void setEnabled(boolean en)
{
    enabled = en;
}

public String getDescription()
{
    return description;
}

public String toString()
{
    StringBuilder buf = new StringBuilder();
    buf.append(description);
    buf.append("       weight = ");
    buf.append(weight);
    
    return buf.toString();
}

public void toFile(StringBuilder buf, String tabs)
{
    if(tabs.length()>0)
        buf.append("\n");
    buf.append(tabs).append("(transformation");
    buf.append("\n").append(tabs).append("\t(description ").append(description).append(")");
    buf.append("\n").append(tabs).append("\t(weight ").append(weight).append(")");
    buf.append("\n").append(tabs).append("\t(enabled ").append(enabled).append(")");
    buf.append("\n").append(tabs).append("\t");
    Polylist printSourceNotes = new Polylist("source-notes",sourceNotes);
    printPrettyPolylist("", tabs+"\t", buf, printSourceNotes);
    buf.append("\n").append(tabs).append("\t");
    Polylist printGuardCondition = Polylist.list("guard-condition",conditionGuard);
    printPrettyPolylist("", tabs+"\t", buf, printGuardCondition);
    buf.append("\n").append(tabs).append("\t");
    Polylist printTargetNotes = new Polylist("target-notes",targetNotes);
    printPrettyPolylist("", tabs+"\t", buf, printTargetNotes);
    buf.append(")");
}
    
public void printPrettyPolylist(String leftSide, String tabs, StringBuilder buf, Polylist list)
{
    Object first = list.first();
    String newLeftSide = "(" + first.toString();
    
    buf.append(newLeftSide);
    newLeftSide += "_";
    boolean separate = false;
    list = list.rest();
    int size = list.length();
    
    PolylistEnum elements = list.elements();
    while(elements.hasMoreElements())
    {
        if(elements.nextElement() instanceof Polylist && size > 1)
                separate = true;
    }
    if(separate)
    {   
        while((newLeftSide.length() + (leftSide.length() % 8)) % 8 != 0)
            newLeftSide = newLeftSide + "_";
    }
    else
        newLeftSide = newLeftSide + "_";
    int numTabs = (leftSide + newLeftSide).length()/8;
    String addTabs = "";
    while(numTabs > 0)
    {
        addTabs = addTabs + "\t";
        numTabs--;
    }
    elements = list.elements();
    
    if(separate)
        buf.append("\t");
    else
        buf.append(" ");
    
    while(elements.hasMoreElements())
    {
        
        if(!separate)
            buf.append(" ");
        Object elem = elements.nextElement();
        if(elem instanceof Polylist)
        {
            printPrettyPolylist(leftSide + newLeftSide, tabs,buf,(Polylist) elem);
        }
        else
        {
            buf.append(elem.toString());
        }
        if(elements.hasMoreElements())
        {
            if(separate)
                buf.append("\n").append(tabs).append(addTabs);
        }
    }
    buf.append(")");
}
}
