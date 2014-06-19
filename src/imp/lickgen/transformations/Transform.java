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

package imp.lickgen.transformations;

import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.gui.Notate;
import imp.lickgen.LickGen;
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
private ChordPart chords;
private LickGen lickGen;
public boolean debug;

public Transform(LickGen lickGen)
{
    debug = false;
    this.lickGen = lickGen;
    substitutions = new ArrayList<Substitution>();
}

public Transform(LickGen lickGen, String subs)
{
    debug = false;
    substitutions = new ArrayList<Substitution>();
    this.lickGen = lickGen;
    Polylist polysubs = Polylist.PolylistFromString(subs);
    
    while(polysubs.nonEmpty())
    {
        Substitution sub = new Substitution(lickGen, (Polylist) polysubs.first());
        substitutions.add(sub);
        polysubs=polysubs.rest();
    }
}

public MelodyPart applySubstitutionsToMelodyPart(MelodyPart melody, Notate notate)
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
    
    chords = notate.getChordProg();
    
    MelodyPart transMelody = melody.copy();
    MelodyPart transformed;
    for(ArrayList<Substitution> subs: subTypes)
    {
        if(subs.size()>0)
        {
            transformed = applySubstitutionType(subs, transMelody);

            System.out.println("\nANSWER FOR TYPE = " + transformed.toString());
            
            transMelody = transformed.copy();
        }
        
    }
    
    return transMelody;
}

private MelodyPart applySubstitutionType(ArrayList<Substitution> substitutions, MelodyPart transNotes)
{
    
    
    MelodyPart subbedMP = new MelodyPart();
    
    int[] startingSlot = new int[1];
    startingSlot[0] = 0;
    
    while(transNotes.size() > transNotes.getNextIndex(startingSlot[0]-1))
    {
        MelodyPart substituted = new MelodyPart();
        if(debug)
        {
            System.out.println("\tYet to parse: " + transNotes.extract(startingSlot[0], transNotes.getSize())
                + "\n\tSubstituted: " + subbedMP.extract(0, startingSlot[0] - 1));
        }
        
        ArrayList<Substitution> full = new ArrayList<Substitution>();
        for(Substitution sub : substitutions)
        {
            for(int i = 0; i < sub.getWeight(); i++)
                full.add(sub);
        }
        Collections.shuffle(full);

        ArrayList<Substitution> sortedSubs = new ArrayList<Substitution>();
        do {
            Substitution sub = full.get(0);
            sortedSubs.add(sub);
            full.removeAll(Collections.singleton(sub));
        } while(!full.isEmpty());
        
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
            Note addNote = transNotes.getNote(startingSlot[0]);
            subbedMP.addNote(addNote);

            startingSlot[0] = transNotes.getNextIndex(startingSlot[0]);
        }
        else
        {
            MelodyPart.PartIterator addNotes = substituted.iterator();
            while(addNotes.hasNext())
            {
                subbedMP.addNote((Note)addNotes.next());
                
            }
        }
    }
    return subbedMP;
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
    Substitution newSub = new Substitution(lickGen);
    substitutions.add(newSub);
    return newSub;
}

}
