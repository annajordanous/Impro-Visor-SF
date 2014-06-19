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

import imp.data.Chord;
import imp.data.Duration;
import imp.data.Note;
import imp.lickgen.LickGen;
import imp.lickgen.NotesToRelativePitch;
import imp.util.ErrorLog;
import polya.*;
import java.util.*;
/**
 *
 * @author Alex Putman
 */
public class Evaluate implements Function1{
    
private Polylist frame;
String op;
Object val;
private LickGen lickGen;
private boolean trace;

Evaluate(LickGen lickGen, Polylist frame)
{
    this(lickGen, frame, null, null);
}

Evaluate(LickGen lickGen, Polylist frame, String op)
{
    this(lickGen, frame, op, null);
}

Evaluate(LickGen lickGen, Polylist frame, String op, Object val)
{
    this.trace = false;
    this.lickGen = lickGen;
    this.frame = frame;
    this.op = op;
    this.val = val;
}

public Object getVar(Object var)
{
    return frame.assoc(var).second();
}

public void setVar(Object var, Object value)
{
    this.frame = frame.cons(Polylist.list(var,value));
}

public void setNoteVar(Object var, Note note, Chord chord)
{
    NoteChordPair pair = new NoteChordPair(note,chord);
    setVar(var, pair);
}

/**
 * Checks if an object is a constant
 */  
public boolean isConstant(Object obj)
{
    if(!isVariable(obj) && !isFunction(obj))
        return true;
    return false;
}

/**
 * Checks if an object is a variable
 */  
public boolean isVariable(Object obj)
{
    
    return (frame.assoc(obj)!=null);
}

public boolean isFunction(Object obj)
{
    if(obj instanceof Polylist && !((Polylist)obj).isEmpty() && Operators.fromGrammarName(((Polylist)obj).first().toString())!=null)
        return true;
    return false;
}

public Polylist getFrame()
{
    return frame;
}

public Object apply(Object x) {
    return evaluate(x);
}

/**
 * Enum that holds the string values that represent each operator in the grammar
 * and their corresponding string representation in the java implementation.
 * Allows for the use of switch case.
 */ 
public enum Operators {
  // Idiomatic Java names. You could ignore those if you really want,
  // and overload the constructor to have a parameterless one which calls
  // name() if you really want.
  PRINT_NOTE("print-note"), // only for debugging, not program
  AND("and"),
  OR("or"),
  NOT("not"),
  IF("if"),
  EQUALS("="),
  ABSOLUTE_VALUE("abs"),
  MEMBER("member"),
  CHORD_FAMILY("chord-family"),
  TRIPLET("triplet?"),
  QUINTUPLET("quintuplet?"),
  DURATION("duration"),
  DURATION_ADDITION("duration+"),
  DURATION_SUBTRACTION("duration-"),
  DURATION_GR("duration>"),
  DURATION_GR_EQ("duration>="),
  DURATION_LT("duration<"),
  DURATION_LT_EQ("duration<="),
  PITCH_ADDITION("pitch+"),
  PITCH_SUBTRACTION("pitch-"),
  PITCH_GR("pitch>"),
  PITCH_GR_EQ("pitch>="),
  PITCH_LT("pitch<"),
  PITCH_LT_EQ("pitch<="),
  NOTE_CATEGORY("note-category"),
  RELATIVE_PITCH("relative-pitch"),
  ABSOLUTE_PITCH("absolute-pitch"),
  SCALE_DURATION("scale-duration"),
  NOTE_DURATION_ADDITION("add-duration"),
  NOTE_DURATION_SUBTRACTION("subtract-duration"),
  SET_DURATION("set-duration"),
  SET_RELATIVE_PITCH("set-relative-pitch"),
  TRANSPOSE_DIATONIC("transpose-diatonic"),
  TRANSPOSE_CHROMATIC("transpose-chromatic"),
  GET_NOTE("get-note");

  private static final Map<String, Operators> nameToValueMap;

  static {
    // Really I'd use an immutable map from Guava...
    nameToValueMap = new HashMap<String, Operators>();
    for (Operators op : EnumSet.allOf(Operators.class)) {
      nameToValueMap.put(op.grammarName, op);
    }
  }

  private final String grammarName;

  private Operators(String grammarName) {
    this.grammarName = grammarName;
  }

  public String getGrammarName() {
    return grammarName;
  }

  public static Operators fromGrammarName(String grammarName) {
    return nameToValueMap.get(grammarName);
  }
}


/**
 * Functions for mapping
 */ 


/**
 * Functions that are applied in the transformations
 */    
public Object evaluate(Object sent)
{
    Polylist statement;
    //System.out.println(sent.toString());
    if(isConstant(sent) && op == null)
        return sent;
    else if(isVariable(sent) && op == null)
        return getVar(sent);
    if(op != null)
        statement = new Polylist(sent,new Polylist());
    else
        statement = (Polylist) sent;
    Object operator;
    Polylist args;
    if(op == null)
    {
        operator = statement.first();
        args = statement.rest();
    }
    else
    {
        if(val == null)
            args = statement;
        else
            args = statement.cons(val);
        operator = op;
    }
    if(trace)
    {
        System.out.println("\t\t\t\t\tOperator: " + operator.toString() + 
                "\n\t\t\t\t\tArgs: " + args.toString());
    }
    Polylist evaledArgs = args.map(new Evaluate(lickGen, frame)).flatten();
    
    Object returnVal = null;
    
    if(operator instanceof String)
    {
        Object firstArg;
        Object secondArg;
        switch(Operators.fromGrammarName(operator.toString()))
        {
            case AND:
                returnVal = and(evaledArgs);
                break;
                
            case OR:
                returnVal = or(evaledArgs);
                break;
                
            case NOT:
                returnVal = not(evaledArgs);
                break;
                
            case IF:
                returnVal = if_statement(evaledArgs);
                break;
                
            case EQUALS:
                returnVal = equals(evaledArgs);
                break;
                
            case ABSOLUTE_VALUE:
                returnVal = absolute_value(evaledArgs);
                break;
                
            case MEMBER:
                returnVal = member(evaledArgs);
                break;
                
            case CHORD_FAMILY:
                returnVal = chord_family(evaledArgs);
                break;
                
            case TRIPLET:
                returnVal = triplet(evaledArgs);
                break;
                
            case QUINTUPLET:
                returnVal = quintuplet(evaledArgs);
                break;
            
            case DURATION:
                returnVal = duration(evaledArgs);
                break;
            
            case DURATION_ADDITION:
                returnVal = duration_addition(evaledArgs);
                break;
                
            case DURATION_SUBTRACTION:
                returnVal = duration_subtraction(evaledArgs);
                break;
                
            case DURATION_GR:
                returnVal = duration_gr(evaledArgs);
                break;
                
            case DURATION_GR_EQ:
                returnVal = duration_gr_eq(evaledArgs);
                break;
                
            case DURATION_LT:
                returnVal = duration_lt(evaledArgs);
                break;
                
            case DURATION_LT_EQ:
                returnVal = duration_lt_eq(evaledArgs);
                break;
                
            case NOTE_CATEGORY:
                returnVal = note_category(evaledArgs);
                break;
                
            case RELATIVE_PITCH:
                returnVal = relative_pitch(evaledArgs);
                break;
                
            case ABSOLUTE_PITCH:
                returnVal = absolute_pitch(evaledArgs);
                break;
            
            case PITCH_ADDITION:
                returnVal = pitch_addition(evaledArgs);
                break;
                
            case PITCH_SUBTRACTION:
                returnVal = pitch_subtraction(evaledArgs);
                break;
                
            case PITCH_GR:
                returnVal = pitch_gr(evaledArgs);
                break;
                
             case PITCH_GR_EQ:
                returnVal = pitch_gr_eq(evaledArgs);
                break;
                
             case PITCH_LT:
                returnVal = pitch_lt(evaledArgs);
                break;
                
             case PITCH_LT_EQ:
                returnVal = pitch_lt_eq(evaledArgs);
                break;
                
            case SCALE_DURATION:
                double firstNum = readNumber(evaledArgs.first().toString());
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.SCALE_DURATION.getGrammarName(), firstNum)).flatten();
                else
                {
                    returnVal = scale_duration((NoteChordPair)evaledArgs.rest().first(),(double)firstNum);
                }
                break;
            case NOTE_DURATION_ADDITION:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.NOTE_DURATION_ADDITION.getGrammarName(), firstArg)).flatten();
                else
                {
                    returnVal = note_duration_addition((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case NOTE_DURATION_SUBTRACTION:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.NOTE_DURATION_SUBTRACTION.getGrammarName(), firstArg)).flatten();
                else
                {
                    returnVal = note_duration_subtraction((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case SET_DURATION:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.SET_DURATION.getGrammarName(), firstArg)).flatten();
                else
                {
                    returnVal = set_duration((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case SET_RELATIVE_PITCH:
                firstArg = evaledArgs.first();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.SET_RELATIVE_PITCH.getGrammarName(), firstArg)).flatten();
                else
                {
                    returnVal = set_relative_pitch((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case TRANSPOSE_DIATONIC:
                firstArg = evaledArgs.first().toString();
                
                
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.TRANSPOSE_DIATONIC.getGrammarName(), firstArg)).flatten();
                else
                {
                    returnVal = transpose_diatonic((NoteChordPair)evaledArgs.rest().first(),firstArg.toString());
                }
                break;
            case TRANSPOSE_CHROMATIC:
                firstArg = evaledArgs.first().toString();
                if(evaledArgs.rest().length() != 1)
                    returnVal = evaledArgs.rest().map(new Evaluate(lickGen, frame, Operators.TRANSPOSE_CHROMATIC.getGrammarName(), firstArg)).flatten();
                else
                {
                    returnVal = transpose_chromatic((NoteChordPair)evaledArgs.rest().first(),readNumber(firstArg.toString()));
                }
                break;
            case GET_NOTE:
                if(evaledArgs.length() > 1)
                    returnVal = evaledArgs.map(new Evaluate(lickGen, frame, Operators.GET_NOTE.getGrammarName())).flatten();
                else
                {
                    returnVal = new Polylist(get_note(evaledArgs), new Polylist());
                }
                break;
            default:
        }
    }
    else
        returnVal = null;
    if(trace)
    {
        if(returnVal == null)
            System.out.println("\t\t\t\t\tReturned: NULL");
        else
            System.out.println("\t\t\t\t\tReturned: " + returnVal.toString());
    }
    return returnVal;
}

/**
 * No argument can return false
 */ 
private Boolean and(Polylist evaledArgs)
{
    if(evaledArgs.member(Boolean.FALSE))
        return false;
    return true;
}
/**
 * Atleast one argument must return true
 */
private Boolean or(Polylist evaledArgs)
{
    if(evaledArgs.member(true))
        return true;
    return false;
}
/**
 * Returns the opposite of the boolean value given
 */
private Boolean not(Polylist evaledArgs)
{
    Boolean firstVal = (Boolean) evaledArgs.first();
    return !firstVal;
}
/**
 * If the first argument returns true, return the second argument, else the
 * third argument
 */
private Object if_statement(Polylist evaledArgs)
{
    if(evaledArgs.length() < 3)
        ErrorLog.log(ErrorLog.WARNING, "Not enough arguments for if statement "
                + "in transformational grammar");
    if(evaledArgs.length() > 3)
        ErrorLog.log(ErrorLog.WARNING, "Too many arguments for if statement "
                + "in transformational grammar");
    
    if((Boolean)evaledArgs.first())
        return evaledArgs.second();
    else
        return evaledArgs.third();
}
/**
 * Returns true if the first argument equals the second argument
 */
private Boolean equals(Polylist evaledArgs)
{
    if(evaledArgs.length() < 2)
        ErrorLog.log(ErrorLog.WARNING, "Not enough arguments for = statement "
                + "in transformational grammar");
    if(evaledArgs.length() > 2)
        ErrorLog.log(ErrorLog.WARNING, "Too many arguments for = statement "
                + "in transformational grammar");
    
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    
    if(firstArg.toString().matches("[\\d]*\\.?[\\d]*") && secondArg.toString().matches("[\\d]*\\.?[\\d]*"))
    {
        double firstNum = Double.parseDouble(firstArg.toString());
        double secondNum = Double.parseDouble(secondArg.toString());
        return (firstNum == secondNum);
    }
    else
        return firstArg.equals(secondArg);
}
/**
 * returns the absolute value of a number in double form
 */ 
private double absolute_value(Polylist evaledArgs)
{
    double firstArg = readNumber(evaledArgs.first().toString());
    return Math.abs(firstArg);
}
/**
 * if the first object is contained in the Polylist second argument, return true
 */ 
private Boolean member(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    
    Polylist secondArg = evaledArgs.rest();
    
    if(secondArg.member(firstArg))
        return true;
    else 
        return false;
}
/**
 * returns true if the given note or duration string is a triplet
 */ 
private Boolean triplet(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    
    String dur;
    
    if(firstArg instanceof NoteChordPair)
        dur = Note.getDurationString(((NoteChordPair)firstArg).note.getRhythmValue());
    else
        dur = firstArg.toString();
    
    return dur.contains("/3");
}
/**
 * if the first object is contained in the Polylist second argument, return true
 */ 
private Boolean quintuplet(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    
    String dur;
    
    if(firstArg instanceof NoteChordPair)
        dur = Note.getDurationString(((NoteChordPair)firstArg).note.getRhythmValue());
    else
        dur = firstArg.toString();
    
    return dur.contains("/5");
}
/**
 * return the chord family of a note
 */ 
private String chord_family(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    Chord chord = pair.chord;
    return chord.getFamily();
}
/**
 * return the duration of a note in lead sheet notation
 */ 
private String duration(Polylist evaledArgs)
{
    Note note = ((NoteChordPair) evaledArgs.first()).note.copy();
    return Note.getDurationString(note.getRhythmValue());
}
/**
 * return the string duration of the duration addition of both args
 */ 
private int duration_addition(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    
    int firstDur = 0;
    int secondDur = 0;
    
    if(firstArg instanceof NoteChordPair)
        firstDur = ((NoteChordPair)firstArg).note.getRhythmValue();
    else
        firstDur = Duration.getDuration0(firstArg.toString());
    
    if(secondArg instanceof NoteChordPair)
        secondDur = ((NoteChordPair)secondArg).note.getRhythmValue();
    else
        secondDur = Duration.getDuration0(secondArg.toString());
    
    return firstDur + secondDur;
}
/**
 * return the string duration of the second arg subtracted from the first arg
 */ 
private int duration_subtraction(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    
    int firstDur = 0;
    int secondDur = 0;
    
    if(firstArg instanceof NoteChordPair)
        firstDur = ((NoteChordPair)firstArg).note.getRhythmValue();
    else
        firstDur = Duration.getDuration0(firstArg.toString());
    
    if(secondArg instanceof NoteChordPair)
        secondDur = ((NoteChordPair)secondArg).note.getRhythmValue();
    else
        secondDur = Duration.getDuration0(secondArg.toString());
    
    return firstDur - secondDur;
}
/**
 * return true if the duration of arg1 is greater than the duration of arg2
 */ 
private Boolean duration_gr(Polylist evaledArgs)
{
    int subtraction = duration_subtraction(evaledArgs);
    
    if(subtraction > 0)
        return true;
    else   
        return false;
}
/**
 * return true if the duration of arg1 is greater than or equal to 
 * the duration of arg2
 */ 
private Boolean duration_gr_eq(Polylist evaledArgs)
{
    int subtraction = duration_subtraction(evaledArgs);
    
    if(subtraction >= 0)
        return true;
    else   
        return false;
}
/**
 * return true if the duration of arg1 is less than the duration of arg2
 */ 
private Boolean duration_lt(Polylist evaledArgs)
{
    int subtraction = duration_subtraction(evaledArgs);
    
    if(subtraction < 0)
        return true;
    else   
        return false;
}
/**
 * return true if the duration of arg1 is less than or equal to 
 * the duration of arg2
 */ 
private Boolean duration_lt_eq(Polylist evaledArgs)
{
    int subtraction = duration_subtraction(evaledArgs);
    
    if(subtraction <= 0)
        return true;
    else   
        return false;
}
/**
 * return the note category of a note in a NoteChordPair
 */ 
private String note_category(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    int classify = lickGen.classifyNote(pair.note, pair.chord);
    switch(classify)
    {
        case LickGen.CHORD:
            return "C";
        case LickGen.COLOR:
            return "L";
        case LickGen.NOTE:
            return "X";
        default:
            return "R";
    }
}
/**
 * return the relative pitch of a note in a NoteChordPair
 */ 
private Object relative_pitch(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    Note note = pair.note;
    Chord chord = pair.chord;

    Polylist relNoteList = NotesToRelativePitch.noteToRelativePitch(note, chord);
    String relPitch = relNoteList.second().toString();
    if(relPitch.matches("[\\d]*"))
        return Integer.parseInt(relPitch);
    else
        return relPitch;
}
/**
 * return the absolute pitch of a note in a NoteChordPair
 */ 
private String absolute_pitch(Polylist evaledArgs)
{
    NoteChordPair pair = (NoteChordPair) evaledArgs.first();
    Note note = pair.note.copy();
    note.setRhythmValue(0);
    return note.toLeadsheet();
}
/**
 * Takes in 2 of the following: NoteChordPairs or a String relative pitch.
 * 
 * If at least one is a relative pitch, then the result will be both converted 
 * to relative pitches and added together.
 * 
 * Else the result will be both absolute pitches added together
 */ 
private Object pitch_addition(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    // Choose the first chord of the set of notes incase neither args are notes
    // if the first arg is not a note
    
    if(firstArg instanceof String && firstArg.toString().matches("[b#]?[\\d|1[0123]]"))
    {
        if(secondArg instanceof NoteChordPair)
        {
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            Chord chord = ((NoteChordPair)secondArg).chord;
            Polylist relNote = NotesToRelativePitch.noteToRelativePitch(secondNote, chord);
            return addRelPitch(relNote.second().toString(), firstArg.toString());
        }
        else
        {
            return addRelPitch(firstArg.toString(), secondArg.toString());
        }
    }
    else if(firstArg instanceof NoteChordPair)
    {
        if(secondArg instanceof String && secondArg.toString().matches("[b#]?[\\d|1[0123]]"))
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Chord chord = ((NoteChordPair)firstArg).chord;
            Polylist relNote = NotesToRelativePitch.noteToRelativePitch(firstNote, chord);
            return addRelPitch(relNote.second().toString(), secondArg.toString());
        }
        else
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            return (firstNote.getPitch()+secondNote.getPitch());
       }
    }
    else
        return null;
}
/**
 * Takes in 2 of the following: NoteChordPairs or String absolute pitch
 * or String relative pitch.
 * 
 * If at least one is a relative pitch, then the result will be both converted 
 * to relative pitches and subtracted.
 * 
 * Else the result will be both absolute pitches subtracted.
 */ 
private Object pitch_subtraction(Polylist evaledArgs)
{
    Object firstArg = evaledArgs.first();
    Object secondArg = evaledArgs.second();
    // Choose the first chord of the set of notes incase neither args are notes
    // if the first arg is not a note
    
    if(firstArg instanceof String && firstArg.toString().matches("[b#]?[\\d|1[0123]]"))
    {
        if(secondArg instanceof NoteChordPair)
        {
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            Chord chord = ((NoteChordPair)secondArg).chord;
            Polylist relNote = NotesToRelativePitch.noteToRelativePitch(secondNote, chord);
            String relPitch = relNote.second().toString();
            StringBuilder minusPitch = new StringBuilder();
            char firstChar = relPitch.charAt(0);
            if(firstChar == 'b')
            {
                relPitch = relPitch.substring(1);
                firstChar = relPitch.charAt(0);
                minusPitch.append("#");
            }
            else if(firstChar == '#')
            {
                relPitch = relPitch.substring(1);
                firstChar = relPitch.charAt(0);
                minusPitch.append("b");
            }
            
            if(firstChar != '-')
                minusPitch.append("-");
            else
                relPitch = relPitch.substring(1);
            minusPitch.append(relPitch);
            return addRelPitch(firstArg.toString(), minusPitch.toString());
        }
        else
        {
            String relPitch = secondArg.toString();
            StringBuilder minusPitch = new StringBuilder();
            char firstChar = relPitch.charAt(0);
            if(firstChar == 'b')
            {
                relPitch = relPitch.substring(1);
                firstChar = relPitch.charAt(0);
                minusPitch.append("#");
            }
            else if(firstChar == '#')
            {
                relPitch = relPitch.substring(1);
                firstChar = relPitch.charAt(0);
                minusPitch.append("b");
            }
            
            if(firstChar != '-')
                minusPitch.append("-");
            else
                relPitch = relPitch.substring(1);
            minusPitch.append(relPitch);
            return addRelPitch(firstArg.toString(), minusPitch.toString());
        }
    }
    else if(firstArg instanceof NoteChordPair)
    {
        if(secondArg instanceof String && secondArg.toString().matches("[b#]?[\\d|1[0123]]"))
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Chord chord = ((NoteChordPair)firstArg).chord;
            Polylist relNote = NotesToRelativePitch.noteToRelativePitch(firstNote, chord);
            
            String relPitch = secondArg.toString();
            StringBuilder minusPitch = new StringBuilder();
            char firstChar = relPitch.charAt(0);
            if(firstChar == 'b')
            {
                relPitch = relPitch.substring(1);
                firstChar = relPitch.charAt(0);
                minusPitch.append("#");
            }
            else if(firstChar == '#')
            {
                relPitch = relPitch.substring(1);
                firstChar = relPitch.charAt(0);
                minusPitch.append("b");
            }
            
            if(firstChar != '-')
                minusPitch.append("-");
            else
                relPitch = relPitch.substring(1);
            minusPitch.append(relPitch);
            return addRelPitch(relNote.second().toString(), minusPitch.toString());
        }
        else if(secondArg instanceof NoteChordPair)
        {
            Note firstNote = ((NoteChordPair)firstArg).note.copy();
            Note secondNote = ((NoteChordPair)secondArg).note.copy();
            return (firstNote.getPitch()-secondNote.getPitch())/2.0;
        }
        else
            return null;
    }
    else
        return null;
}
/**
 * returns if the first note or relative pitch given is higher than the second
 */ 
private Boolean pitch_gr(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return false;
    else if(subtraction.indexOf("0") != -1)
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == '#')
            return true;
        else
            return false;
    }
    else
        return true;
}
/**
 * returns if the first note or relative pitch given is higher than or equal to 
 * the second
 */ 
private Boolean pitch_gr_eq(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return false;
    else if(subtraction.indexOf("0") != -1)
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == 'b')
            return false;
        else
            return true;
    }
    else
        return true;
}
/**
 * returns if the first note or relative pitch given is lower than the second
 */ 
private Boolean pitch_lt(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return true;
    else if(subtraction.indexOf("0") != -1)
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == 'b')
            return true;
        else
            return false;
    }
    else
        return false;
}
/**
 * returns if the first note or relative pitch given is lower than or equal to 
 * the second
 */ 
private Boolean pitch_lt_eq(Polylist evaledArgs)
{
    Object pitchSub = pitch_subtraction(evaledArgs);
    if (pitchSub == null)
        return null;
    String subtraction = pitchSub.toString();
    if(subtraction.indexOf("-") != -1)
        return true;
    else if(subtraction.indexOf("0") != -1)
    {
        char firstChar = subtraction.charAt(0);
        if(firstChar == '#')
            return false;
        else
            return true;
    }
    else
        return false;
}
/**
 * returns a NoteChordPair with its note's duration multiplied by scale
 */ 
private NoteChordPair scale_duration(NoteChordPair pair, double scale)
{
    Note note = pair.note.copy();
    int dur = (int) (note.getRhythmValue()*scale);
    String durString = Note.getDurationString(dur);
    note.setRhythmValue(Duration.getDuration0(durString));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with its note's duration set to duration
 */ 
private NoteChordPair set_duration(NoteChordPair pair, String duration)
{
    Note note = pair.note.copy();
    note.setRhythmValue(Duration.getDuration0(duration));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with its note's duration added with duration
 */ 
private NoteChordPair note_duration_addition(NoteChordPair pair, String duration)
{
    Note note = pair.note.copy();
    int dur = note.getRhythmValue()+Duration.getDuration0(duration);
    String durString = Note.getDurationString(dur);
    note.setRhythmValue(Duration.getDuration0(durString));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with duration subtracted from pair's note's duration
 */ 
private NoteChordPair note_duration_subtraction(NoteChordPair pair, String duration)
{
    Note note = pair.note.copy();
    int dur = note.getRhythmValue()-Duration.getDuration0(duration);
    String durString = Note.getDurationString(dur);
    note.setRhythmValue(Duration.getDuration0(durString));
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with the note created from a relative pitch
 */ 
private NoteChordPair set_relative_pitch(NoteChordPair pair, String relPitch)
{
    Note note = pair.note.copy();
    Polylist transposeNoteList = Polylist.PolylistFromString("X " + relPitch + " " + Note.getDurationString(note.getRhythmValue()));
    Chord chord = pair.chord;
    Note newNote = lickGen.makeRelativeNote(transposeNoteList, chord);
    return new NoteChordPair(newNote, chord);
}
/**
 * returns a NoteChordPair with the note pitch transposed diatonically 
 */ 
private NoteChordPair transpose_diatonic(NoteChordPair pair, String relPitch)
{
    String returns = relative_pitch(new Polylist(pair, new Polylist())).toString();
    String relPitchInit;
    if(returns == null)
    {
        return null;
    }
    relPitchInit = returns;
    String initArg = relPitchInit;
    char initAugment = initArg.charAt(0);
    int initNum = 0;
    if(initAugment == '#' || initAugment == 'b')
    {
        initArg = initArg.substring(1);
    }
    else
    {
        initAugment = 'f';
    }
    initNum = Integer.parseInt(initArg.toString());
    String totalSum = addRelPitch(returns, relPitch);
    int shiftOctaves = 0;
    if(initNum < 0)
        shiftOctaves++;
    if(!totalSum.substring(0, 1).matches("\\-?[\\d]*"))
    {
        char insert = totalSum.charAt(0);
        int origNumber = Integer.parseInt(totalSum.substring(1));
        int number = 0;
        if(origNumber < 0)
        {
            shiftOctaves += ((origNumber / 7) - 1);
            number = origNumber % 7;
            number += 8;
        }
        else
        {
            shiftOctaves += ((origNumber - 1) / 7);
            number = (origNumber - 1)%7 + 1;
        }
        totalSum = "" + insert + number; 
    }
    else
    {
        int origNumber = Integer.parseInt(totalSum);
        int number = 0;
        if(origNumber<0)
        {
            shiftOctaves += ((origNumber / 7) - 1);
            number = origNumber % 7;
            number += 8;
        }
        else
        {
            shiftOctaves += ((origNumber - 1) / 7);
            number = (origNumber - 1)%7 + 1;
        }
        totalSum = "" + number; 
    }
    Polylist transposeNoteList = Polylist.PolylistFromString("X " + totalSum + " 4");
    Polylist transposeNoteListInit = Polylist.PolylistFromString("X " + ((initAugment != 'f')?
            initAugment :
            "") + initNum + " 4");
    Chord chord = pair.chord;
    Note fakeNote = lickGen.makeRelativeNote(transposeNoteList, chord);
    Note fakeInitNote = lickGen.makeRelativeNote(transposeNoteListInit, chord);
    Note note = pair.note.copy();
    int newPitch = fakeNote.getPitch()-fakeInitNote.getPitch();
    newPitch += Note.OCTAVE*shiftOctaves;
    note.shiftPitch(newPitch, 0);
    return new NoteChordPair(note, chord);
}
/**
 * returns a NoteChordPair with the note transposed chromatically 
 */ 
private NoteChordPair transpose_chromatic(NoteChordPair pair, double pitches)
{
    pitches *= 2;
    Note note = pair.note.copy();
    note.shiftPitch((int)pitches, 0);
    return new NoteChordPair(note, pair.chord);
}
/**
 * returns a NoteChordPair with the note transposed chromatically 
 */ 
private Note get_note(Polylist evaledArgs)
{
    Note note = ((NoteChordPair)evaledArgs.first()).note.copy();
    return note;
}

private double readNumber(String num)
{
    if(num.indexOf("/") == -1)
        return Double.parseDouble(num);
    String[] halves = num.split("/");
    return Arith.divide(Double.parseDouble(halves[0]), Double.parseDouble(halves[1])).doubleValue();
}
private String addRelPitch(String num1, String num2)
{
    char augment1 = num1.charAt(0);
    
    int numVal1 = 0;
    if(augment1 == '#' || augment1 == 'b')
    {
        num1 = num1.substring(1);
    }
    else
    {
        augment1 = 'f';
    }
    numVal1 = Integer.parseInt(num1);
    
    char augment2 = num2.charAt(0);
    
    int numVal2 = 0;
    if(augment2 == '#')
    {
        if(augment1 == 'b')
            augment2 = 'f';
        else if(augment1 == '#')
        {
            augment2 = 'f';
            numVal1 += 1;
            if(numVal1 == 0)
                numVal1 += 1;
        }
        else
            augment2 = '#';
        
        num2 = num2.substring(1);
    }
    else if(augment2 == 'b')
    {
        
        if(augment1 == '#')
            augment2 = 'f';
        else if(augment1 == 'b')
        {
            augment2 = 'f';
            numVal1 -= 1;
            if(numVal1 == 0)
                numVal1 -= 1;
        }
        else
            augment2 = 'b';
        
        num2 = num2.substring(1);
    }
    else
    {
        augment2 = augment1;
    }
    numVal2 = Integer.parseInt(num2);
    int addTotal = numVal2 + numVal1;
    if(numVal1 > 0 && numVal2 > 0)
        addTotal --;
    String returnString;
    if(addTotal <= 0)
        addTotal --;
    returnString = ((augment2=='f')? "" : augment2) + "" + addTotal;
    return returnString;
}
/**
 * A data-structure that holds a note and its chord. 
 */
public class NoteChordPair{
    public final Note note;
    public final Chord chord;
    
    public NoteChordPair(Note note, Chord chord)
    {
        this.note = note;
        this.chord = chord;
    }
}
}