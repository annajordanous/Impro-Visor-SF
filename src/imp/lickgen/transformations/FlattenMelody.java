/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package imp.lickgen.transformations;

import imp.Constants;
import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Note;
import imp.data.Part.PartIterator;
import imp.data.Unit;
import imp.lickgen.LickGen;
import imp.lickgen.NotesToRelativePitch;
import java.util.ArrayList;
import polya.Polylist;

/**
 *
 * @author Alex Putman
 */
public class FlattenMelody{
    
private LickGen lickgen;
public FlattenMelody(LickGen lickgen)
{
    this.lickgen = lickgen;
}

/**
* Returns the flattened version of a melody
* @param melody                  the melody to obtain the outline of
* @param chords              
* @return MelodyPart             the flattened melody
*/
public MelodyPart flatten(MelodyPart melody)
{

    return melody;
}

/**
* Divides a melody by chords and flattens each section by resolution
* @param melody                      the melody to divide
* @param chords                      the chordPart the melody is played over
* @param resolution                  the length of the resulting divisions
* @return MelodyPart                 returns the melody flattened by chord
*/
public MelodyPart flattenByChord(MelodyPart melody,
                                 ChordPart chords,
                                 int resolution,
                                 int startingSlot,
                                 int endingSlot)
{
    MelodyPart flattenResult = new MelodyPart(endingSlot - startingSlot);
    for(int slotIndex = startingSlot; 
        ((slotIndex != -1) && (slotIndex < endingSlot)); 
        slotIndex = chords.getNextChordIndex(slotIndex))
    {
        Chord curChord = chords.getChord(slotIndex);
        int nextSlotIndex = chords.getNextChordIndex(slotIndex);
        if(nextSlotIndex == -1)
        {
            nextSlotIndex = chords.size()-1;
        }
        else
        {
            nextSlotIndex--;
        }
        MelodyPart melodySection = melody.extract(slotIndex, (nextSlotIndex < endingSlot)? nextSlotIndex : endingSlot, true, true);
        MelodyPart flattened = flattenByResolution(melodySection,
                                                   curChord,
                                                   resolution, 
                                                   slotIndex);
        flattenResult.pasteOver(flattened, slotIndex);
    }
    return flattenResult;
}

/**
* flattens a melody by resolution
* @param melody                      the melody to divide
* @param resolution                  the length of the resulting divisions
* @return ArrayList<MelodyPart>      an ArrayList of MelodyParts divisions
*                                    from melody
*/
private MelodyPart flattenByResolution(MelodyPart melody, 
                                       int resolution)
{
    return null;
}

/**
* flattens a melody by resolution
* @param melody                      the melody to divide
* @param resolution                  the length of the resulting divisions
* @param startingSlot                the slot at which the melody starts
* @return ArrayList<MelodyPart>      an ArrayList of MelodyParts divisions
*                                    from melody
*/
private MelodyPart flattenByResolution(MelodyPart melody, 
                                       Chord chord,
                                       int resolution,
                                       int startingSlot)
{
    MelodyPart flattenedPart = new MelodyPart();
    Note prevNote = null;
    Note bestNote;
    for(int slotIndex = 0; 
        slotIndex + resolution - 1 < melody.size();
        slotIndex += resolution)
    {
        
        ArrayList<Note> notes = getNotesInResolution(melody, resolution, slotIndex);
        bestNote = getBestNote(notes, chord, resolution, startingSlot);
        if(prevNote == null)
        {
            prevNote = bestNote;
        }
        else if(prevNote.samePitch(bestNote))
        {
            prevNote.augmentRhythmValue(bestNote.getRhythmValue());
        }
        else
        {
            flattenedPart.addNote(prevNote);
            prevNote = bestNote;
        }
    }
    flattenedPart.addNote(prevNote);
    return flattenedPart;
}
    
private ArrayList<Note> getNotesInResolution(MelodyPart melody,
                                             int resolution,
                                             int startingSlot)
{
    MelodyPart resolutionCutPart = melody.extract(startingSlot,
                                                  startingSlot + resolution - 1, 
                                                  true, 
                                                  true);
    
    PartIterator noteIt = resolutionCutPart.iterator();
    
    ArrayList<Note> notes = new ArrayList<Note>();
    
    while(noteIt.hasNext())
    {
        notes.add((Note)noteIt.next());
    }
    return notes;
}

private Note getBestNote(ArrayList<Note> notes, Chord chord, int resolution, int startingSlot)
{
    Note bestNote = new Note(0);
    int bestScore = 0;
    int totalDur = 0;
    for(Note note: notes)
    {
        totalDur += note.getRhythmValue();
        int score = getNoteScore(note, chord, resolution, startingSlot);
        if(score > bestScore)
        {
            bestNote = note.copy();
            bestScore = score;
        }
        startingSlot += note.getRhythmValue();
    }
    bestNote.setRhythmValue(totalDur);
    return bestNote;
}

private int getNoteScore(Note note, Chord chord, int resolution, int startingSlot)
{
    int score = note.getRhythmValue();
    score += 120*(1 - (startingSlot%resolution)/(1.0*resolution));
    switch(lickgen.classifyNote(note, chord))
    {
        case(LickGen.CHORD):
            score += 120;
            break;
            
        case(LickGen.COLOR):
            score += 10;
            break;           
    }
    
    if (note.getPitch() == Note.REST)
        score += 40;
    return score;
}

public Polylist createTransform(MelodyPart outline, MelodyPart transformed, ChordPart chords, int resolution, int start, int stop)
{
    Polylist transform = new Polylist();
    
    for(int slot = start; slot + resolution - 1 <= stop; slot+= resolution)
    {
        MelodyPart outlinePart = outline.extract(slot, slot + resolution - 1, true, true);
        MelodyPart transPart = transformed.extract(slot, slot + resolution - 1, true, true);
        Chord chord = chords.getCurrentChord(slot);
        Polylist substitution = createSubstitution(outlinePart, transPart, chord);
        transform = transform.addToEnd(substitution);
        
    }
    
    return transform;
}

private Polylist createSubstitution(MelodyPart outline, MelodyPart transformed, Chord chord)
{
    Polylist substitution = Polylist.PolylistFromString(
            "substitution" + 
            "(name generated-substitution)" + 
            "(type motif)" + 
            "(weight 1)");
    Polylist transformation = createTransformation(outline, transformed, chord);
    substitution = substitution.addToEnd(transformation);
    
    return substitution;
}

private Polylist createTransformation(MelodyPart outline, MelodyPart transformed, Chord chord)
{
    Polylist transformation = Polylist.PolylistFromString(
            "transformation" + 
            "(description generated-transformation)" + 
            "(weight 1)" + 
            "(source-notes n1)");
    Polylist guardCondition = getGuardCondition(outline, chord);
    Polylist targetNotes = getTargetNotes(outline, transformed, chord);
    Polylist defaultTarget = Polylist.PolylistFromString("target-notes n1");
    
    transformation = transformation.addToEnd(guardCondition);
    if(targetNotes != null)
        transformation = transformation.addToEnd(targetNotes);
    else
        transformation = transformation.addToEnd(defaultTarget);
    
    return transformation;
}
private Polylist getGuardCondition(MelodyPart outline, Chord chord)
    {
        Polylist guardCondition = Polylist.PolylistFromString("guard-condition");
        
        Note origNote = outline.getCurrentNote(0);
        Polylist andEquals = Polylist.PolylistFromString("and (not (rest? n1))");
        Polylist categoryEquals = Polylist.PolylistFromString("= (note-category n1)");
        Polylist chordFamilyEquals = Polylist.PolylistFromString("= (chord-family n1)");
        Polylist relPitchEquals = Polylist.PolylistFromString("= (relative-pitch n1)");
        
        int cat = lickgen.classifyNote(origNote, chord);
        switch (cat){
            case(LickGen.CHORD):
                categoryEquals = categoryEquals.addToEnd("C");
                break;
            case(LickGen.COLOR):
                categoryEquals = categoryEquals.addToEnd("L");
                break;
            case(LickGen.APPROACH):
                categoryEquals = categoryEquals.addToEnd("A");
                break;
            default:
                categoryEquals = categoryEquals.addToEnd("X");
                break;
        }
        chordFamilyEquals = chordFamilyEquals.addToEnd(chord.getFamily());
        relPitchEquals = relPitchEquals.addToEnd(NotesToRelativePitch.noteToRelativePitch(origNote, chord).second());
        andEquals = andEquals.addToEnd(categoryEquals).addToEnd(chordFamilyEquals).addToEnd(relPitchEquals);
        guardCondition = guardCondition.addToEnd(andEquals);
        return guardCondition;
    }    
private Polylist getTargetNotes(MelodyPart outline, MelodyPart transformed, Chord chord)
    {
        Polylist targetNotes = Polylist.PolylistFromString("target-notes");
        
        Note origNote = outline.getCurrentNote(0);
        
        PartIterator transNotes = transformed.iterator();
        
        if(origNote.isRest())
            return null;
        while(transNotes.hasNext())
        {
            Note toTransform = (Note)transNotes.next();
            String duration = Note.getDurationString(toTransform.getRhythmValue());
            Polylist setDuration = Polylist.PolylistFromString("set-duration");
            setDuration = setDuration.addToEnd(duration);
            
            Polylist result;
            
            //if(Math.abs(toTransform.getPitch()-origNote.getPitch()) < 2)
                //result = getTransposeChromatic(origNote, toTransform, chord);
            //else
                result = getTransposeDiatonic(origNote, toTransform, chord);
            if(result == null)
            {
                return null;
            }
            
            setDuration = setDuration.addToEnd(result);
            
            targetNotes = targetNotes.addToEnd(setDuration);
        }
        
        return targetNotes;
    }

private Polylist getTransposeDiatonic(Note origNote, Note toTransform, Chord chord)
{
    Evaluate eval = new Evaluate(lickgen, new Polylist());
    eval.setNoteVar("n1", origNote, chord);
    eval.setNoteVar("n2", toTransform, chord);
    Polylist subHelper;
    subHelper = Polylist.PolylistFromString("pitch- (relative-pitch n2) n1");
    Object result = eval.absoluteRelPitchDiff(toTransform, origNote, chord);
    if(toTransform.isRest())
        return Polylist.PolylistFromString("make-rest n1");
    if(result == null)
    {
        return null;
    }
    String relPitch = result.toString();//eval.modRelPitch(result.toString());
    Polylist transposePitch = Polylist.PolylistFromString("transpose-diatonic " + relPitch + " n1");
    return transposePitch;
}

private Polylist getTransposeChromatic(Note origNote, Note toTransform, Chord chord)
{
    double diff = (origNote.getPitch() - toTransform.getPitch())/2.0;
    if(Math.abs(diff) > 10)
        return null;
    Polylist transposePitch = Polylist.PolylistFromString("transpose-chromatic " + diff + " n1");
    return transposePitch;
}
    
}
