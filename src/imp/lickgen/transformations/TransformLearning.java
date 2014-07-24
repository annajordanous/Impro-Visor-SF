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
import polya.PolylistEnum;
import polya.PolylistIterator;

/**
 *
 * @author Alex Putman
 */
public class TransformLearning{
    
    
public static final int CHROMATIC_TREND = 1000;
public static final int DIATONIC_TREND  = 1001;
public static final int SKIP_TREND      = 1002;
public static final int NO_TREND        = 999;
public static final int NO_CHROMATIC_DATA   = 998;
public static final String NO_DIATONIC_DATA   = "";
private LickGen lickgen;
public TransformLearning(LickGen lickgen)
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
    MelodyPart flattened = melody.copy();
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
        flattened = flattenByResolution(flattened,
                                        curChord,
                                        resolution, 
                                        slotIndex,
                                        (nextSlotIndex < endingSlot)? 
                                         nextSlotIndex : endingSlot);
        
    }
    return flattened.extract(startingSlot, endingSlot, true, true);
}

/**
* flattens a melody by resolution
* @param melody                      the melody to divide
* @param resolution                  the length of the resulting divisions
* @return ArrayList<MelodyPart>      an ArrayList of MelodyParts divisions
*                                    from melody
*/
public MelodyPart flattenByResolution(MelodyPart melody, 
                                       ChordPart chords,
                                       int resolution,
                                       int startingSlot,
                                       int endingSlot)
{
    MelodyPart flattenedPart = new MelodyPart();
    Note prevNote = null;
    Note bestNote;
    for(int slotIndex = startingSlot; 
        slotIndex + resolution - 1 <= endingSlot;
        slotIndex += resolution)
    {
        Chord chord = chords.getCurrentChord(slotIndex);
        ArrayList<Note> notes = getNotesInResolution(melody, resolution, slotIndex);
        bestNote = getBestNote(notes, chord, resolution, startingSlot);
        flattenedPart.addNote(bestNote);
        /*
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
        */
    }
    //flattenedPart.addNote(prevNote);
    MelodyPart newMelody = melody.copy();
    newMelody.pasteOver(flattenedPart, startingSlot);
    return newMelody;
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
                                       int startingSlot,
                                       int endingSlot)
{
    MelodyPart flattenedPart = new MelodyPart();
    Note prevNote = null;
    Note bestNote;
    for(int slotIndex = startingSlot; 
        slotIndex + resolution - 1 <= endingSlot;
        slotIndex += resolution)
    {
        
        ArrayList<Note> notes = getNotesInResolution(melody, resolution, slotIndex);
        bestNote = getBestNote(notes, chord, resolution, startingSlot);
        flattenedPart.addNote(bestNote);
        /*
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
        */
    }
    //flattenedPart.addNote(prevNote);
    MelodyPart newMelody = melody.copy();
    newMelody.pasteOver(flattenedPart, startingSlot);
    return newMelody;
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
    if(lickgen.classifyNote(note, chord) == LickGen.CHORD)
    {
        score += 120;
    }
    else if(lickgen.classifyNote(note, chord) == LickGen.COLOR)
    {
        score += 20;          
    }
    
    if(note.isRest())
        score += 40;
    return score;
}

public Polylist createBlockTransform(MelodyPart outline, MelodyPart transformed, ChordPart chords, int resolution, int start, int stop)
{
    Polylist transform = new Polylist();
    
    for(int slot = start; slot + resolution - 1 <= stop; slot+= resolution)
    {
        MelodyPart outlinePart = outline.extract(slot, slot + resolution - 1, true, true);
        MelodyPart transPart = transformed.extract(slot, slot + resolution - 1, true, true);
        Chord chord = chords.getCurrentChord(slot);
        Polylist substitution = createBlockSubstitution(outlinePart, transPart, chord);
        transform = transform.addToEnd(substitution);
        
    }
    
    return transform;
}

private Polylist createBlockSubstitution(MelodyPart outline, MelodyPart transformed, Chord chord)
{
    int numNotes = 0;
    int slot = 0;
    while(transformed.getCurrentNote(slot) != null)
    {
        numNotes++;
        slot = transformed.getNextIndex(slot);
    }
    Polylist substitution = Polylist.PolylistFromString(
            "substitution" + 
            "(name " + chord.getFamily() + "-" + numNotes + "-notes)" + 
            "(type motif)" + 
            "(weight 1)");
    Polylist transformation = createOneNoteBlockTransformation(outline, transformed, chord);
    substitution = substitution.addToEnd(transformation);
    
    return substitution;
}

private Polylist createOneNoteBlockTransformation(MelodyPart outline, MelodyPart transformed, Chord chord)
{
    
    Polylist transformation = Polylist.PolylistFromString(
            "transformation" + 
            "(description generated-transformation)" + 
            "(weight 1)" + 
            "(source-notes n1)");
    Polylist guardCondition = getOneNoteGuardCondition(outline, chord);
    Polylist targetNotes = getOneNoteTargetNotes(outline, transformed, chord);
    Polylist defaultTarget = Polylist.PolylistFromString("target-notes n1");
    
    transformation = transformation.addToEnd(guardCondition);
    if(targetNotes != null)
        transformation = transformation.addToEnd(targetNotes);
    else
        transformation = transformation.addToEnd(defaultTarget);
    
    return transformation;
}
private Polylist getOneNoteGuardCondition(MelodyPart outline, Chord chord)
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
private Polylist getOneNoteTargetNotes(MelodyPart outline, MelodyPart transformed, Chord chord)
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
                result = getTransposeDiatonic(origNote, toTransform, "n1", chord);
            if(result == null)
            {
                return null;
            }
            
            setDuration = setDuration.addToEnd(result);
            
            targetNotes = targetNotes.addToEnd(setDuration);
        }
        
        return targetNotes;
    }

private Polylist getTransposeDiatonic(Note origNote, Note toTransform, String var, Chord chord)
{
    Evaluate eval = new Evaluate(lickgen, new Polylist());
    eval.setNoteVar("n1", origNote, chord);
    eval.setNoteVar("n2", toTransform, chord);
    Polylist subHelper;
    subHelper = Polylist.PolylistFromString("pitch- (relative-pitch n2) n1");
    Object result = eval.absoluteRelPitchDiff(toTransform, origNote, chord);
    if(toTransform.isRest())
        return Polylist.PolylistFromString("make-rest "+var);
    if(result == null)
    {
        return null;
    }
    String relPitch = result.toString();//eval.modRelPitch(result.toString());
    Polylist transposePitch = Polylist.PolylistFromString("transpose-diatonic " + relPitch + " " + var);
    return transposePitch;
}

private Polylist getTransposeChromatic(Note origNote, Note toTransform, String var)
{
    double diff = (toTransform.getPitch() - origNote.getPitch())/2.0;
    if(Math.abs(diff) > 10)
        return null;
    Polylist transposePitch = Polylist.PolylistFromString("transpose-chromatic " + diff + " " + var);
    return transposePitch;
}

private Polylist getTransposeDiatonicCondition(Note origNote, Note toTransform, String var1, String var2, Chord chord)
{
    Evaluate eval = new Evaluate(lickgen, new Polylist());
    eval.setNoteVar(var1, origNote, chord);
    eval.setNoteVar(var2, toTransform, chord);
    Polylist subHelper;
    subHelper = Polylist.PolylistFromString("pitch- (relative-pitch "+var2+") "+var1);
    Object result = eval.absoluteRelPitchDiff(toTransform, origNote, chord);
    if(toTransform.isRest())
        return null;
    if(result == null)
    {
        return null;
    }
    String relPitch = result.toString();//eval.modRelPitch(result.toString());
    Polylist transposePitch = Polylist.PolylistFromString("=").addToEnd(subHelper).addToEnd(relPitch);
    return transposePitch;
}
public Polylist createTrendTransform(MelodyPart outline, MelodyPart transformed, ChordPart chords, int startingSlot, int endingSlot)
{
    // Outline NCPs have 4 components
    // first  - Note
    // second - Chord
    // third  - starting slot of note
    // fourth - variable that hold them
    
    // Result NCPs have 3 components
    // first  - Note
    // second - Chord
    // third  - starting slot of note
    
    // Currently on learns on Chromatic Trend
    
    Polylist subs = Polylist.PolylistFromString("");
    int varNumber = 1;
    
    Polylist lastNCP = createNCP(transformed.getCurrentNote(startingSlot), chords.getCurrentChord(startingSlot), startingSlot);
    Polylist lastOutNCP = createNCP(outline.getCurrentNote(startingSlot), chords.getCurrentChord(startingSlot), startingSlot, varNumber++);
    Polylist subOutline = Polylist.PolylistFromString("").addToEnd(lastOutNCP);
    Polylist subTransform = Polylist.PolylistFromString("").addToEnd(lastNCP);
    
    Polylist subTransformFrom = Polylist.PolylistFromString("");
    int outlineNoteSlot = outline.getNextIndex(startingSlot);
    
    
    
    double chromTrendData = NO_CHROMATIC_DATA;
    String diatTrendData = NO_DIATONIC_DATA;
    int addLastToFrom = 1;
    for(int slot = transformed.getNextIndex(startingSlot); slot < endingSlot; slot = transformed.getNextIndex(slot))
    {
        
        
        Note lastNote = (Note)lastNCP.first();
        Chord lastChord = (Chord)lastNCP.second();
        
        Note curNote = transformed.getCurrentNote(slot);
        Chord curChord = chords.getCurrentChord(slot);
        Polylist curNCP = createNCP(curNote, curChord, slot);
        
        int newOutSlot = outline.getCurrentNoteIndex(slot);
        Note outNote = outline.getCurrentNote(newOutSlot);
        Chord outChord = chords.getCurrentChord(newOutSlot);
        Polylist newOutNCP = createNCP(outNote, outChord, newOutSlot, varNumber);
            
        double newChromData = (curNote.getPitch() - lastNote.getPitch())/2.0;
        if(chromTrendData != NO_CHROMATIC_DATA && Math.abs(chromTrendData - newChromData) < 1.5)
        {
            addLastToFrom++;
            chromTrendData = newChromData;
            subTransform = subTransform.addToEnd(curNCP);
        }
        else if(chromTrendData == NO_CHROMATIC_DATA && curNote.nonRest() && Math.abs(newChromData) < 1.0)
        {
            addLastToFrom++;
            chromTrendData = newChromData;
            subTransform = subTransform.addToEnd(curNCP);
        }
        else 
        {
            // create substitution for the trend that just ended
            if(addLastToFrom > 1)
            {
                for(int i = 0; i < addLastToFrom; i++)
                    subTransformFrom = subTransformFrom.addToEnd(lastOutNCP);

                if(lastNote.samePitch((Note)lastOutNCP.first()))
                {
                    subTransform = subTransform.allButLast();
                    subTransformFrom = subTransformFrom.allButLast();
                }
                Polylist substitution = createTrendSubstitution(subOutline, subTransform, subTransformFrom, CHROMATIC_TREND);

                if(substitution != null)
                {
                    subs = subs.addToEnd(substitution);
                }
                // reset data
                if(Math.abs(newChromData) < 1.0)
                {
                    chromTrendData = NO_CHROMATIC_DATA;
                    slot = transformed.getPrevIndex(slot);
                    curNCP = lastNCP;
                    addLastToFrom = 1;
                    varNumber = 1;
                    newOutNCP = createNCP((Note)lastOutNCP.first(), (Chord)lastOutNCP.second(), (Integer)lastOutNCP.third(), varNumber++);
                    newOutSlot = (Integer)lastOutNCP.third();
                    outlineNoteSlot = outline.getNextIndex((Integer)newOutNCP.third());
                    subTransform = new Polylist(lastNCP, new Polylist());
                    subOutline = new Polylist(newOutNCP, new Polylist());
                    subTransformFrom = Polylist.PolylistFromString("");
                }
                else
                {
                    // reset data
                    chromTrendData = NO_CHROMATIC_DATA;
                    addLastToFrom = 1;
                    varNumber = 1;
                    newOutNCP = createNCP((Note)newOutNCP.first(), (Chord)newOutNCP.second(), (Integer)newOutNCP.third(), varNumber++);
                    outlineNoteSlot = outline.getNextIndex((Integer)newOutNCP.third());
                    subTransform = new Polylist(curNCP, new Polylist());
                    subOutline = new Polylist(newOutNCP, new Polylist());
                    subTransformFrom = Polylist.PolylistFromString("");
                }
            }
            else
            {
                // reset data
                chromTrendData = NO_CHROMATIC_DATA;
                addLastToFrom = 1;
                varNumber = 1;
                newOutNCP = createNCP((Note)newOutNCP.first(), (Chord)newOutNCP.second(), (Integer)newOutNCP.third(), varNumber++);
                outlineNoteSlot = outline.getNextIndex((Integer)newOutNCP.third());
                subTransform = new Polylist(curNCP, new Polylist());
                subOutline = new Polylist(newOutNCP, new Polylist());
                subTransformFrom = Polylist.PolylistFromString("");
            }
        }
        
        if(newOutSlot >= outlineNoteSlot)
        {
            outlineNoteSlot = outline.getNextIndex(newOutSlot);
            subOutline = subOutline.addToEnd(newOutNCP);
            varNumber++;
        }
        
        lastNCP = curNCP;
        lastOutNCP = (Polylist)subOutline.last();
    }
    
    return subs;
}


private Polylist createTrendSubstitution(Polylist outlineNCP, Polylist resultNCP, Polylist resultFromNCP, int trend)
{
    Polylist substitution = Polylist.PolylistFromString(
            "substitution" + 
            "(name " + resultNCP.length() + "-changed-GENERATED)" + 
            "(type motif)" + 
            "(weight 1)");
    
    Polylist transformation = createTrendTransformation(outlineNCP, resultNCP, resultFromNCP, trend);
    if(transformation == null)
        return null;
    return substitution.addToEnd(transformation);
}
private Polylist createTrendTransformation(Polylist outlineNCP, Polylist resultNCP, Polylist resultFromNCP, int trend)
{
    Polylist transformation = Polylist.PolylistFromString(
            "transformation" + 
            "(description generated-transformation)" + 
            "(weight 1)");
    Polylist sourceNotes = Polylist.PolylistFromString("source-notes");
    
    PolylistEnum outEn = outlineNCP.elements();
    
    while(outEn.hasMoreElements())
    {
        Polylist ncp = (Polylist)outEn.nextElement();
        String varName = (String)ncp.fourth();
        sourceNotes = sourceNotes.addToEnd(varName);
    }
    transformation = transformation.addToEnd(sourceNotes);
    
    Polylist importantNCPs = new Polylist();
    
    PolylistEnum resultFromEn = resultFromNCP.elements();
    while(resultFromEn.hasMoreElements())
    {
        Polylist ncpFrom = (Polylist)resultFromEn.nextElement();
        if(!importantNCPs.member(ncpFrom))
            importantNCPs = importantNCPs.addToEnd(ncpFrom);
    }
    Polylist guard = getTrendGuardCondition(importantNCPs, trend);
    Polylist target = getTrendTargetNotes((Polylist)outlineNCP.first(), (Polylist)outlineNCP.last(), resultNCP, resultFromNCP, trend);
    if(guard == null || target == null)
        return null;
    return transformation.addToEnd(guard).addToEnd(target);
}
private Polylist getTrendGuardCondition(Polylist importantNotesWithChords, int trend)
    {
        Polylist guardCondition = Polylist.PolylistFromString("guard-condition");
        Polylist andEquals = Polylist.PolylistFromString("and");
        
        PolylistEnum en = importantNotesWithChords.elements();
        while(en.hasMoreElements())
        {
            Polylist noteWithChord = (Polylist)en.nextElement();
            Note note = (Note)noteWithChord.first();
            Chord chord = (Chord)noteWithChord.second();
            String var = (String)noteWithChord.fourth();
            Polylist categoryEquals = Polylist.PolylistFromString("= (note-category "+var+")");
            Polylist chordFamilyEquals = Polylist.PolylistFromString("= (chord-family "+var+")");
            Polylist relPitchEquals = Polylist.PolylistFromString("= (relative-pitch "+var+")");
            relPitchEquals = relPitchEquals.addToEnd(NotesToRelativePitch.noteToRelativePitch(note, chord).second());
            int cat = lickgen.classifyNote(note, chord);
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
            andEquals = andEquals.addToEnd(categoryEquals).addToEnd(chordFamilyEquals).addToEnd(relPitchEquals);
        }
        if(trend == CHROMATIC_TREND)
        {
            en = importantNotesWithChords.elements();
            Polylist noteWithChord = (Polylist)en.nextElement();
            Note compNote = (Note)noteWithChord.first();
            String compVar = (String)noteWithChord.fourth();
            while(en.hasMoreElements())
            {
                noteWithChord = (Polylist)en.nextElement();
                Note note = (Note)noteWithChord.first();
                Chord chord = (Chord)noteWithChord.second();
                String var = (String)noteWithChord.fourth();
                Polylist pitchMinus = Polylist.PolylistFromString("pitch-").addToEnd(compVar).addToEnd(var);
                double minus = (compNote.getPitch() - note.getPitch())/2.0;
                Polylist pitchMinusEquals = Polylist.PolylistFromString("=").addToEnd(pitchMinus).addToEnd(minus);

                andEquals = andEquals.addToEnd(pitchMinusEquals);
            }
        }
        else if(trend == DIATONIC_TREND)
        {
            en = importantNotesWithChords.elements();
            Polylist noteWithChord = (Polylist)en.nextElement();
            Note compNote = (Note)noteWithChord.first();
            Chord compChord = (Chord)noteWithChord.second();
            String compVar = (String)noteWithChord.fourth();
            while(en.hasMoreElements())
            {
                noteWithChord = (Polylist)en.nextElement();
                Note note = (Note)noteWithChord.first();
                Chord chord = (Chord)noteWithChord.second();
                String var = (String)noteWithChord.fourth();
                Polylist diatonicCond = getTransposeDiatonicCondition(note, compNote, var, compVar, compChord);

                andEquals = andEquals.addToEnd(diatonicCond);
            }
        }
        guardCondition = guardCondition.addToEnd(andEquals);
        return guardCondition;
    }    
private Polylist getTrendTargetNotes(Polylist firstOutlineNCP, Polylist lastOutlineNCP, Polylist resultNCP, Polylist resultFromNCP, int trend)
    {
        Polylist targetNotes = Polylist.PolylistFromString("target-notes");
        
        PolylistEnum resultNCPen = resultNCP.elements();
        PolylistEnum resultFromNCPen = resultFromNCP.elements();
        Note firstOutNote = (Note)firstOutlineNCP.first();
        int firstOutSlot = (Integer)firstOutlineNCP.third();
        String firstOutVar = (String)firstOutlineNCP.fourth();
        Note lastOutNote = (Note)lastOutlineNCP.first();
        int lastOutSlot = (Integer)lastOutlineNCP.third();
        String lastOutVar = (String)lastOutlineNCP.fourth();
        boolean first = true;
        while(resultNCPen.hasMoreElements())
        {
            
            Polylist setDur = Polylist.PolylistFromString("set-duration");
            Polylist result = (Polylist)resultNCPen.nextElement();
            Polylist resultFrom = (Polylist)resultFromNCPen.nextElement();
            Note transNote = (Note)result.first();
            int startingSlot = (Integer)result.third();
            
            Note fromNote = (Note)resultFrom.first();
            Chord fromChord = (Chord)resultFrom.second();
            String fromVar = (String)resultFrom.fourth();
            
            // if the first note needs to be subtracted from and added
            if(first)
            {
                first = false;
                int slotsLeftFront = firstOutNote.getRhythmValue()-(startingSlot - firstOutSlot);
                if(slotsLeftFront < firstOutNote.getRhythmValue())
                {
                    Polylist addToFront = Polylist.PolylistFromString("subtract-duration");
                    String subDur = Note.getDurationString(slotsLeftFront);
                    addToFront = addToFront.addToEnd(subDur).addToEnd(firstOutVar);
                    targetNotes = targetNotes.addToEnd(addToFront);
                }
            }
            
            setDur = setDur.addToEnd(Note.getDurationString(transNote.getRhythmValue()));
            if(transNote.samePitch(fromNote))
            {
                setDur = setDur.addToEnd(fromVar);
            }
            else if(transNote.isRest() || fromNote.isRest())
            {
                Polylist rest = Polylist.PolylistFromString("make-rest " + fromVar);
                setDur = setDur.addToEnd(rest);
            }
            else
            {
                Polylist transposeNote = new Polylist();

                if(trend == CHROMATIC_TREND)
                {
                    transposeNote = getTransposeChromatic(fromNote, transNote, fromVar);
                }
                else if(trend == DIATONIC_TREND)
                {
                    transposeNote = getTransposeDiatonic(fromNote, transNote, fromVar, fromChord);
                }
                if(transposeNote == null)
                    return null;
                setDur = setDur.addToEnd(transposeNote);
            }
            targetNotes = targetNotes.addToEnd(setDur);
            
            // if the last note is not part of the actual trend or needs to be extended or reduced
            if(!resultNCPen.hasMoreElements())
            {
                int endingSlot = startingSlot + transNote.getRhythmValue();
                if(startingSlot < lastOutSlot)
                {
                    if( endingSlot > lastOutSlot)
                    {
                        Polylist subFromEnd = Polylist.PolylistFromString("add-duration");
                        String subDur = Note.getDurationString(lastOutNote.getRhythmValue() + lastOutSlot - endingSlot);
                        subFromEnd = subFromEnd.addToEnd(subDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(subFromEnd);
                    }
                    else if(endingSlot < lastOutSlot)
                    {
                        Polylist addToEnd = Polylist.PolylistFromString("add-duration");
                        String addDur = Note.getDurationString(lastOutNote.getRhythmValue() + lastOutSlot - endingSlot);
                        addToEnd = addToEnd.addToEnd(addDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(addToEnd);
                    }
                    else
                    {
                        targetNotes = targetNotes.addToEnd(lastOutVar);
                    }
                }
                else
                {
                    if(lastOutSlot + lastOutNote.getRhythmValue() !=  startingSlot + transNote.getRhythmValue())
                    {
                        Polylist addToEnd = Polylist.PolylistFromString("subtract-duration");
                        String subDur = Note.getDurationString(endingSlot - lastOutSlot);
                        addToEnd = addToEnd.addToEnd(subDur).addToEnd(lastOutVar);
                        targetNotes = targetNotes.addToEnd(addToEnd);
                    }
                }
            }
        }
        
        return targetNotes;
    }

    private Polylist createNCP(Note note, Chord chord, int slot)
    {
        Polylist list = Polylist.PolylistFromString("");
        return list.addToEnd(note).addToEnd(chord).addToEnd(slot);
    }
    private Polylist createNCP(Note note, Chord chord, int slot, int var)
    {
        Polylist list = Polylist.PolylistFromString("");
        return list.addToEnd(note).addToEnd(chord).addToEnd(slot).addToEnd("n"+var);
    }
}
