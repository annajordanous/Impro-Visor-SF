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
import imp.gui.Notate;
import polya.*;
import java.util.*;
/**
 *
 * @author Alex Putman
 */
public class Transform 
{

private MelodyPart startingNotes;
public ArrayList<Substitution> substitutions;
public boolean debug;
public boolean hasChanged;

public Transform()
{
    debug = false;
    hasChanged = false;
    substitutions = new ArrayList<Substitution>();
}

public Transform(String subs)
{
    debug = false;
    hasChanged = false;
    substitutions = new ArrayList<Substitution>();
    Polylist polysubs = Polylist.PolylistFromString(subs);
    
    while(polysubs.nonEmpty())
    {
        if(polysubs.first() instanceof Polylist)
        {
            Substitution sub = new Substitution((Polylist) polysubs.first());
            substitutions.add(sub);
        }
        polysubs=polysubs.rest();
    }
}

public MelodyPart applySubstitutionsToMelodyPart(MelodyPart melody, ChordPart chords, Notate notate)
{
    startingNotes = melody.copy();
    
    ArrayList<ArrayList<Substitution>> subTypes = new ArrayList<ArrayList<Substitution>>();
    ArrayList<Substitution> substitutionsMotif = new ArrayList<Substitution>(); 
    ArrayList<Substitution> substitutionsEmbellish = new ArrayList<Substitution>(); 
    
    for(Substitution sub: substitutions)
    {
        String type = sub.getType();
        if(type.equals("motif"))
            substitutionsMotif.add(sub);
        else if(type.equals("embellishment"))
            substitutionsEmbellish.add(sub);
    }
    subTypes.add(substitutionsMotif);
    subTypes.add(substitutionsEmbellish);
    
    MelodyPart transMelody = melody.copy();
    MelodyPart transformed;
    for(ArrayList<Substitution> subs: subTypes)
    {
        if(subs.size()>0)
        {
            transformed = applySubstitutionType(subs, transMelody, chords);
            transMelody = transformed.copy();
        }
        
    }
    
    return transMelody;
}

private MelodyPart applySubstitutionType(ArrayList<Substitution> substitutions, 
                                         MelodyPart transNotes, 
                                         ChordPart chords)
{
    
    
    MelodyPart subbedMP = new MelodyPart();
    
    int[] startingSlot = new int[1];
    startingSlot[0] = 0;
    
    while(transNotes.size() > transNotes.getNextIndex(startingSlot[0]-1))
    {
        if(debug)
        {
            System.out.println("\tYet to parse: " + transNotes.extract(startingSlot[0], transNotes.getSize())
                + "\n\tSubstituted: " + subbedMP.extract(0, startingSlot[0] - 1));
        }
        
        ArrayList<Substitution> full = new ArrayList<Substitution>();
        for(Substitution sub : substitutions)
        {
            if(sub.getEnabled())
            {
                int weight = sub.getWeight();
                for(int i = 0; i < weight; i++)
                    full.add(sub);
            }
        }
        
        if(full.size() < 1)
            return transNotes;
        
        Collections.shuffle(full);

        ArrayList<Substitution> sortedSubs = new ArrayList<Substitution>();
        do {
            Substitution sub = full.get(0);
            sortedSubs.add(sub);
            full.removeAll(Collections.singleton(sub));
        } while(!full.isEmpty());
        
        MelodyPart substituted = null;
        int initSlot = startingSlot[0];
        for(Substitution sub: sortedSubs)
        {
            if(debug)
            {
                System.out.println("\t\tTrying sub: " + sub.getName());
            }
            substituted = sub.apply(transNotes, chords, startingSlot);
            if(substituted != null)
            {
                break;
            }
            if(debug)
            {
                System.out.println("");
            }
        }
        if(substituted == null)
        {
            Note addNote = transNotes.getNote(initSlot);
            MelodyPart temp = new MelodyPart();
            temp.addNote(addNote);
            transNotes.pasteOver(temp, initSlot);
            startingSlot[0] = transNotes.getNextIndex(initSlot);
        }
        else
        {
            transNotes.pasteOver(substituted, initSlot);
        }
    }
    return transNotes;
}

public void findDuplicatesAndAddToWeight()
{
    for(int i = 0; i < substitutions.size(); i++)
    {
        Substitution sub = substitutions.get(i);
        substitutions.remove(sub);
        int newWeight = sub.getWeight();
        while(substitutions.contains(sub))
        {
            int subIndex = substitutions.indexOf(sub);
            Substitution copy = substitutions.remove(subIndex);
            newWeight += copy.getWeight();
            hasChanged = true;
        }
        sub.setWeight(newWeight);
        substitutions.add(i, sub);
    }
}

public int getTotalMotifWeight()
{
    int totalWeight = 0;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("motif"))
            totalWeight += sub.getWeight();
    }
    return totalWeight;
}
public int getTotalEmbWeight()
{
    int totalWeight = 0;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("embellishment"))
            totalWeight += sub.getWeight();
    }
    return totalWeight;
}
public void scaleMotifWeights(double scale)
{
    if(scale != 1.0)
        hasChanged = true;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("motif"))
        {
            double newWeight = sub.getWeight()*scale;
            sub.setWeight((int)newWeight);
        }
    }
}
public void scaleEmbWeights(double scale)
{
    if(scale != 1.0)
        hasChanged = true;
    for(Substitution sub: substitutions)
    {
        if(sub.getType().equals("embellishment"))
        {
            double newWeight = sub.getWeight()*scale;
            sub.setWeight((int)newWeight);
        }
    }
}

public Polylist melodyPartToNoteList(MelodyPart melody)
{
    int slotvalue = 0;
    MelodyPart.PartIterator it = melody.iterator();
    Polylist nList = new Polylist();
    while(it.hasNext())
    {
        Object next = (Note) it.next();
        nList.addToEnd(next);
    }
    return nList;
}

public MelodyPart getOriginalMelodyPart()
{
    return startingNotes;
}

public void toFile(StringBuilder buf)
{
    for(Substitution sub: substitutions)
    {
        buf.append("\n");
        sub.toFile(buf);
    }
}

public Substitution addNewSubstitution()
{
    Substitution newSub = new Substitution();
    substitutions.add(newSub);
    hasChanged = true;
    return newSub;
}

public boolean hasChanged()
{
    if(hasChanged)
        return true;
    else
    {
        for(Substitution sub: substitutions)
        {
            if(sub.hasChanged())
                return true;
        }
    }
    return false;
}
}
