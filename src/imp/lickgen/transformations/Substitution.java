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

import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.lickgen.LickGen;
import polya.*;
import java.util.*;
/**
 *
 * @author Alex Putman
 */
public class Substitution {
    
private int weight;
private String type;
private String name;
public ArrayList<Transformation> transformations;
public boolean debug;
private boolean enabled;
private LickGen lickGen;
private boolean hasChanged;

public Substitution (LickGen lickGen)
{
    debug = false;
    this.lickGen = lickGen;
    transformations = new ArrayList<Transformation>();
    name = "new-substitution";
    type = "embellishment";
    weight = 1;
    enabled = true;
    hasChanged = false;
}

public Substitution (LickGen lickGen, Polylist sub)
{
    debug = false;
    this.lickGen = lickGen;
    transformations = new ArrayList<Transformation>();
    name = (String) sub.assoc("name").second();
    type = (String) sub.assoc("type").second();
    weight = ((Long)sub.assoc("weight").second()).intValue();
    if(sub.assoc("enabled") != null)
    {
        enabled = Boolean.parseBoolean(sub.assoc("enabled").second().toString());
    }
    else
    {
        enabled = true;
    }
    PolylistEnum transbuilder = sub.rest().elements();
    while(transbuilder.hasMoreElements())
    {
        Object el = transbuilder.nextElement();
        if(el instanceof Polylist)
        {
            Polylist polyel = (Polylist) el;
            if(((String)polyel.first()).equals("transformation"))
            {
                transformations.add(new Transformation(lickGen, polyel));
            }
        }
    }
    hasChanged = false;
}

public MelodyPart apply(MelodyPart notes, ChordPart chords, int[] startingSlot)
{
    // for weighted random shuffling
    
    ArrayList<Transformation> full = new ArrayList<Transformation>();
    for(Transformation trans: transformations)
    {
        if(trans.getEnabled())
        {
            for(int i = 0; i < trans.getWeight(); i++)
                full.add(trans);
        }
    }
    if (full.size() < 1)
        return null;
    Collections.shuffle(full);
    
    ArrayList<Transformation> sortedTrans = new ArrayList<Transformation>();
    do {
        Transformation trans = full.get(0);
        sortedTrans.add(trans);
        full.removeAll(Collections.singleton(trans));
    } while(!full.isEmpty());
    
    for(Transformation trans: sortedTrans)
    {
        int newStartingSlot = startingSlot[0];
        if(debug)
        {
            System.out.println("\t\t\tTrying trans: " + trans.getDescription());
        }
        MelodyPart result = trans.apply(notes, chords, newStartingSlot);

        if(!(result == null))
        {
            if(debug)
            {
                System.out.println("\t\t\tTrans Worked");
                System.out.println("\t\tSub Result: " + result.toString());
            }
            
            for(int i = 0; i < trans.numSourceNotes(); i++)
                newStartingSlot = notes.getNextIndex(newStartingSlot);
            
            if(!trans.changesLastNote())
                newStartingSlot = notes.getPrevIndex(newStartingSlot);
            
            startingSlot[0] = newStartingSlot;
            
            return result;
        }
        if(debug)
        {
            System.out.println("");
        }
    }
    if(debug)
    {
        System.out.println("\t\tSub Failed");
    }
    return null;
}

public void addNewTransformation()
{
    Transformation trans = new Transformation(lickGen);
    transformations.add(trans);
    hasChanged = true;
}
public int getTotalWeight()
{
    int totalWeight = 0;
    for(Transformation trans: transformations)
    {
        totalWeight += trans.getWeight();
    }
    return totalWeight;
}
public void scaleTransWeights(double scale)
{
    if(scale != 1.0)
        hasChanged = true;
    for(Transformation trans: transformations)
    {
        double newWeight = trans.getWeight()*scale;
        trans.setWeight((int)newWeight);
    }
}
public int getWeight()
{
    return weight;
}

public void setWeight(int weight)
{
    if(this.weight != weight)
        hasChanged = true;
    this.weight = weight;
}

public String getName()
{
    return name;
}

public void setName(String name)
{
    if(!this.name.equals(name))
        hasChanged = true;
    this.name = name;
}

public String getType()
{
    return type;
}

public void setType(String str)
{
    if(!type.equals(str))
        hasChanged = true;
    type = str;
}

public boolean getEnabled()
{
    return enabled;
}

public void setEnabled(boolean en)
{
    if(enabled != en)
        hasChanged = true;
    enabled = en;
}

public boolean hasChanged()
{
    if(hasChanged)
        return true;
    else
    {
        for(Transformation trans: transformations)
        {
            if(trans.hasChanged())
                return true;
        }
    }
    return false;
}

public boolean equals(Object ob)
{
    if(!(ob instanceof Substitution))
        return false;
    
    Substitution other = (Substitution)ob;
    
    if(!type.equals(other.getType()))
        return false;
    
    if(!transformations.containsAll(other.transformations))
        return false;
    
    if(!other.transformations.containsAll(transformations))
        return false;
    
    return true;
}

public Polylist newNoteList(Polylist notelst)
{
    if(notelst.length()==1)
        return new Polylist(((Note)notelst.first()).copy(),new Polylist());
    else
        return new Polylist(((Note)notelst.first()).copy(),newNoteList(notelst.rest()));
}
    
public String toString()
{
    StringBuilder buf = new StringBuilder();
    buf.append(name);
    buf.append("       type = ");
    buf.append(type);
    buf.append("       weight = ");
    buf.append(weight);
    return buf.toString();
}

public void toFile(StringBuilder buf)
{
    buf.append("(substitution");
    buf.append("\n\t(name ").append(name).append(")");
    buf.append("\n\t(type ").append(type).append(")");
    buf.append("\n\t(enabled ").append(enabled).append(")");
    buf.append("\n\t(weight ").append(weight).append(")");
    for(Transformation trans: transformations)
        trans.toFile(buf, "\t");
    buf.append(")");
}

}
