/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2012 Robert Keller and Harvey Mudd College
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

import imp.data.Chord;
import imp.data.ChordPart;
import imp.data.MelodyPart;
import imp.data.Part.PartIterator;
import imp.gui.Expectancy;
import imp.gui.Notate;
import imp.util.ErrorLog;
import imp.util.ErrorLogWithResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import polya.Arith;
import polya.Polylist;
import polya.PolylistEnum;
import polya.Tokenizer;

/*
 * @author David Morrison, modifications by Robert Keller, 21 June 2012
 */

public class Grammar
{
// Rule tags:
public static final String START = "startsymbol";

public static final String TERMINAL = "terminals";

public static final String BASE = "base";

public static final String RULE = "rule";

public static final String PARAM = "parameter";

public static final String COMMENT = "comment";

// Special forms:
public static final String BUILTIN = "builtin";

public static final String SPLICE = "splice";

public static final String LITERAL = "literal";


// Operators:
public static final String PLUS = "+";

public static final String MINUS = "-";

public static final String TIMES = "*";

public static final String DIVIDE = "/";

// Builtin variables:

public static final String EXPECTANCY = "expectancy";

public static final String SYNCOPATION = "syncopation";

public static final String HIGH = "high";

public static final String MEDIUM = "medium";

public static final String LOW = "low";


ArrayList<String> terminals = new ArrayList<String>();

Polylist rules = new Polylist();

Polylist terminalString;

String startSymbol = null; // to be set

private int retryCount = 0;

private Notate notate;

private int currentSlot;


public Grammar(String file)
  {
  loadGrammar(file);
  }


/**
 * Applies grammar rules to generate a lick
@param data the number of slots to be filled
@return
 */

// This code is kind of hacked. First gen should be called in a functional style.
// The other issue is that applyRules sometimes fails when run on a generated
// grammar. We have tried to arrange this failure to take the form of an exception,
// and will then simply try again, until a terminal string is generated.

public Polylist run(int startSlot, int numSlots, Notate myNotate)
  {
    currentSlot = startSlot;
    boolean failure = true;
    int savedRetryCount = retryCount;
    int maxRetries = 20;
    notate = myNotate;
    
    while( failure && (retryCount - savedRetryCount) <= maxRetries )
      {
        try
          {
            terminalString = new Polylist();
            Polylist gen = addStart(numSlots);

            while( gen.nonEmpty() )
              {
                // System.out.println("gen = " + gen);  // Shows derivation.
                gen = applyRules(gen);

                if( gen == null )
                  {
                  throw new RuleApplicationException();
                  //return null;
                  }
              }

            // terminalString is built up within applyRules.
            failure = false;
            return terminalString;
          }
        catch( RuleApplicationException e )
          {
          if( ErrorLogWithResponse.log(ErrorLog.SEVERE, "Problem applying rules: " + e) )
            {
              return null;
            }
          }
      notate.setLickGenStatus("Retrying lick generation (" + (++retryCount) + " cumulative).");
      }
    notate.setLickGenStatus("Unable to generate in " + maxRetries + " retries.");
    return null;
  }


// Search through the rules and find the start symbol.  Note that it will
// take the first start symbol it finds.  Returns null if there is an error.

public Polylist addStart(int numSlots)
  {
  Polylist gen = Polylist.nil;
  Polylist search = rules;

  // While the list of rules isn't empty...
  while( search.nonEmpty() )
    {
    Polylist next = (Polylist)search.first();
    search = search.rest();

    try
      {
      // ... See if the next rule contains the "startsymbol" tag.
      // If it does, pop it on the front of the string.
      if( ((String)next.first()).equals(START) )
        {
        if( next.length() == 2 && next.second() instanceof String )
          {
          startSymbol = (String)next.second();
          
          Polylist s = Polylist.list(startSymbol, numSlots);
          gen = gen.cons(s);
          return gen;
          }
        else
          {
          ErrorLog.log(ErrorLog.SEVERE,
                  "Malformed start rule: " + next + ".  Abort.");
          return null;
          }
        }
      }
    // Catch any syntax errors in the rule file.  At some point it might
    // be nice to actually print out what the problem is...
    catch( ClassCastException e )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Malformed rules file.  Abort.");
      return null;
      }
    }

  // If it didn't find a start symbol in the file, abort.
  ErrorLog.log(ErrorLog.SEVERE, "No start symbol found.  Abort.");
  return null;
  }

public static boolean isScaleDegree(Object ob)
  {
    if( !(ob instanceof Polylist ) )
      {
        return false;
      }
    
    Polylist oblist = (Polylist)ob;
    
    if( oblist.length() != 3 )
      {
        return false;
      }
    
    Object first = oblist.first();
    
    if( !(first instanceof String) )
      {
        return false;
      }
       
    if( !("X".equals((String)first)) )
      {
        return false;
      }
    
    Object second = oblist.second();
    
    if( !(second instanceof Long || second instanceof String ) )
      {
        return false;
      }
    
    Object third = oblist.third();
 
    if( !(third instanceof Long || third instanceof String ) )
      {
        return false;
      }
    return true;
  }


// Take the first character off the string, and apply the given rules to it.
// Any lower case values are treated as terminals and outputted.

public Polylist applyRules(Polylist gen) throws RuleApplicationException
  {
  // We need to be given a non-empty polylist.
  if( gen.isEmpty() )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Passed empty polylist.  Abort.");
    return null;
    }
  //System.out.println("gen = " + gen);
  try
    {

    if( gen.first() instanceof Polylist )
      {
      Polylist pop = (Polylist)gen.first();

      gen = gen.rest();
      Polylist search = rules;

      // Print out any terminal values.
      // If the user has provided us with a list of terminals, use those; otherwise,
      // we assume that terminal values are strings that begin with a lowercase letter.

      if( !terminals.isEmpty() )
        {
        //System.out.println("pop = " + pop);
        
        while( isScaleDegree(pop)
            || ( pop.first() instanceof String 
              && terminals.contains((String)(pop.first()))) )
          {
          if( isScaleDegree(pop) || pop.first().equals("slope"))
            {
            terminalString = terminalString.cons(pop); // use whole expression
           }
          else
            {
            // use first element, assuming that pop is a singleton
            terminalString = terminalString.cons(pop.first());
            }
          
          //System.out.println("terminalString = " + terminalString);

          if( !gen.isEmpty() && gen.first() instanceof Polylist )
            {
            pop = (Polylist)gen.first();
            }
          else
            {
            return gen;
            }
          gen = gen.rest();
          }
        }
      else
        {
        while( pop.first() instanceof String &&
                Character.isLowerCase((((String)pop.first()).charAt(0))) )
          {
          terminalString = terminalString.cons(pop.first());
          if( !gen.isEmpty() && gen.first() instanceof Polylist )
            {
            pop = (Polylist)gen.first();
            }
          else
            {
            return gen;
            }
          gen = gen.rest();
          }
        }

      // All applicable rules (and their corresponding weights)
      // get loaded into these arrays, to be selected from at random.
      ArrayList<Polylist> ruleArray = new ArrayList<Polylist>(5);
      ArrayList<Polylist> baseArray = new ArrayList<Polylist>(5);
      ArrayList<Double> ruleWeights = new ArrayList<Double>(5);
      ArrayList<Double> baseWeights = new ArrayList<Double>(5);

      // Now search through and find all rules that apply to the given start symbol.
      // Note that a start symbol can be a polylist.
      while( search.nonEmpty() )
        {
        //System.out.println("search = " + search);
        // Next is the next rule to compare to
        Polylist next = (Polylist)search.first();
        String type = (String)next.first();


        /*
         * RULEs and BASEs have the following S-expression format:
         * (<keyword> (<symbol>) (<production>) weight)
         * <keyword> can be RULE or BASE
         * <symbol> can be a string or a polylist of strings
         * <production> is a polylist of symbols (or if it's a RULE, some expressions
         *	to evaluate).
         * <weight> is a double expressing how "important" the rule is.  More important
         *	rules will be chosen more often than less important ones.
         */

        // The BASE keyword stops all evalution and variable substitution.
        // If a symbol matches both a RULE and a BASE, it will always choose the BASE.
        // This basically short-circuits any computation and provides an easy way
        // to find base cases.
        if( type.equals(BASE) && next.length() == 4 )
          {
          Object symbol = next.second();
          Polylist derivation = (Polylist)next.third();

          if( pop.equals(symbol) )
            {
            baseArray.add(derivation);
            Number weight = (Number)evaluate(next.fourth());
            baseWeights.add(weight.doubleValue());
            }
          }
        // Most objects will have type RULE.
        else if( type.equals(RULE) && next.length() == 4 )
          {
          Object symbol = next.second();
          Polylist derivation = (Polylist)next.third();
          
          //System.out.println(" derivation before evaluation  " + derivation);

          // The first symbol can never be a variable, it will give the "name" of the
          // rule.  All additional symbols will contain information.

          if( pop.first() instanceof String || isScaleDegree(pop.first()) )
            {

            if( ((String)(pop.first())).equals(((Polylist)symbol).first()) )
              {
              // Fill in variables with their given numeric values.
              derivation = setVars((Polylist)pop, (Polylist)symbol, derivation);

              //System.out.println(" derivation after setVars " + derivation);
              
              // Evaluate any expressions that need to be evaluated.
              derivation = (Polylist)evaluate(derivation);
              
              //System.out.println(" derivation after evaluation " + derivation);

              // Check for negative arguments in RHS,
              // In which case don't use RHS
              boolean valid = true;
              PolylistEnum L = derivation.elements();
              while( L.hasMoreElements() )
              {
                Object ob = L.nextElement();
                if( ob instanceof Polylist )
                {
                  Polylist P = (Polylist)ob;
                  
                  if( P.length() == 2 && P.first().equals(startSymbol) ) 
                  {
                  // We found the start symbol on the RHS.
                  // Only pass it if argument is non-negative.
                  // FIX: Replace this with a more sound mechanism.
                  
                    Object arg = P.second();
                    if( arg instanceof Number && ((Number)arg).intValue() < 0 )
                    {
                      valid = false;
                      //System.out.println("abandoning: " + derivation);
                      break;
                    }
                  }
                }
              }
              
              if( valid )
              {
              ruleArray.add(derivation);
              Number weight = (Number)evaluate(next.fourth());
              ruleWeights.add(weight.doubleValue());
              }
              }
            }
          else
            {
            // This RHS element is not a string. Just carry it and hope for the best!
            // It is probably an S-expression such as (slope M N ...)
            ruleArray.add(derivation);
            Number weight = (Number)evaluate(next.fourth());
            ruleWeights.add(weight.doubleValue());

            }

          }
        search = search.rest();
        }

      // Randomly choose a rule to follow.
      double total = 0.0;

      ArrayList<Polylist> rulesList;
      ArrayList<Double> weightArray;

      // If any base cases exist, we ignore all rules.
      if( !baseWeights.isEmpty() )
        {
        rulesList = new ArrayList(baseArray);
        weightArray = new ArrayList(baseWeights);
        }
      else
        {
        rulesList = new ArrayList(ruleArray);
        weightArray = new ArrayList(ruleWeights);
        }

      // System.out.println("rules = " + rules);

      // Sum up all the weights to use in a weighted average.	    
      for( int i = 0; i < weightArray.size(); ++i )
        {
        total += weightArray.get(i);

        // Generate a random number to find out which rule to use...
        }
      double rand = Math.random();
      double offset = 0.0;

      // Loop through all rules.
      for( int i = 0; i < weightArray.size(); ++i )
        {
        // If the random number falls between the range of the probability 
        // for that rule, we choose it and break out of the loop.
        if( rand >= offset && rand < offset + (weightArray.get(i) / total) )
          {
          for( int j = 0; j < rulesList.get(i).length(); ++j )
            {
            Polylist s = new Polylist();

            // Need to do this, because some things in the rule file are already
            // encoded as polylists.
            if( !(rulesList.get(i).nth(j) instanceof Polylist) )
              {
              s = s.cons(rulesList.get(i).nth(j));
              }
            else
              {
              s = (Polylist)rulesList.get(i).nth(j);
              }
            gen = gen.cons(s);
            }
          return gen;
          }
        offset += weightArray.get(i) / total;
        }
      }

    return gen; // throw new RuleApplicationException("applyRules, no such rule for " + gen);

    //ErrorLog.log(ErrorLog.SEVERE,
    //        "No rule exists beginning with " + gen.first() + ".  Abort.");
    //return null;
    }
  // If we get here, there's some syntax error with the grammar file.
  catch( ClassCastException e )
    {
    ErrorLogWithResponse.log(ErrorLog.SEVERE, "Malformed grammar file, exception: " + e);
    throw new RuleApplicationException("Error in grammar file");
    }
  }

// Load in any terminal values specified in the user file.  Returns a ArrayList containing
// all terminal values.

public ArrayList<Object> getAllOfType(String t)
  {
  Polylist search = rules;
  ArrayList<Object> elements = new ArrayList<Object>();
  while( search.nonEmpty() )
    {
    try
      {
      Polylist next = (Polylist)search.first();
      String type = (String)next.first();

      if( type.equals(t) )
        {
        for( int i = 1; i < next.length(); ++i )
          {
          elements.add(next.nth(i));
          }
        }
      search = search.rest();
      }
    catch( ClassCastException e )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Error parsing " + t);
      return null;
      }
    }

  return elements;
  }


public ArrayList<String> getTerms()
  {
  Collection terms = (Collection)getAllOfType(TERMINAL);
  if( terminals == null )
    {
    return new ArrayList<String>(); // empty
    }
  return new ArrayList<String>(terms);
  }


public void clearParams()
  {
  Polylist newRules = new Polylist();

  while( rules.nonEmpty() )
    {
    if( !((String)((Polylist)rules.first()).first()).equals(PARAM) )
      {
      newRules = newRules.cons(rules.first());
      }
    rules = rules.rest();
    }

  newRules = newRules.reverse();
  rules = newRules;
  }


public ArrayList<Polylist> getParams()
  {
  return new ArrayList<Polylist>((Collection)getAllOfType(PARAM));
  }


public Polylist addRule(Polylist toAdd)
  {
  rules = rules.cons(toAdd);
  return rules;
  }


public Polylist getRules()
  {
  return rules;
  }

// Load the rules in from a file.

public int loadGrammar(String filename)
  {
  //System.out.println("Grammar loadGrammar " + filename);
  try
    {
    Tokenizer in = new Tokenizer(new FileInputStream(filename));
    Object ob;

    while( (ob = in.nextSexp()) != Tokenizer.eof )
      {
      if( ob instanceof Polylist )
        {
        rules = rules.cons((Polylist)ob);
        }
      }
    rules = rules.reverse();
    terminals = getTerms();
    return 0;
    }
  catch( FileNotFoundException e )
    {
    ErrorLog.log(ErrorLog.SEVERE, "File " + filename + " not found.  Abort.");
    return -1;
    }
  }


public int saveGrammar(String filename)
  {
  try
    {
    Polylist toWrite = rules;
    String contents = "";
    while( toWrite.nonEmpty() )
      {
      contents += toWrite.first() + "\n";
      toWrite = toWrite.rest();
      }
    FileWriter out = new FileWriter(new File(filename));
    out.write(contents);
    out.close();
    return 0;
    }
  catch( IOException e )
    {
    ErrorLog.log(ErrorLog.WARNING, "Error saving to " + filename);
    return -1;
    }
  }


public void clear()
  {
  rules = new Polylist();
  terminalString = new Polylist();
  }

// Set all instances of variables in toSet corresponding value in getValsFrom.

private Polylist setVars(Polylist getValsFrom, Polylist getVarsFrom,
                         Polylist toSet)
  {
  try
    {
    if( getValsFrom.length() > 1 && getVarsFrom.length() > 1 )
      // Start at 1, because we ignore the first symbol.
      {
      for( int i = 1; i < getValsFrom.length(); ++i )
        {
        Number val = (Number)getValsFrom.nth(i);
        String var = (String)getVarsFrom.nth(i);
        // Replace all vars in toSet with their value.
        return replace(var, val.longValue(), toSet);
        }
      }
    }

  // Currently we only allow variables to be set to integer values.
  catch( ClassCastException e )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Cannot set variable to non-integer value");
    return null;
    }
  return toSet;
  }

// Evaluate will take an object and perform arithmetic evaluation on it.
// Expressions should be given in prefix form:
// (+ 3 4) evaluates to 7
// (+ (/ 4 2) (* 7 3)) evaluates to 23
//
// As of 21 June 2012, evaluate can accept builtins, of the form
// (builtin <some identifier>)
// Right now, the only identifier is "expectancy". Evaluation of this identifier
// is stubbed to return 1. Any other identifier will return 0.
// Eventually, the value of the builtin identifier may change depending on the
// then-current slot.

private Object evaluate(Object toParse)
  {
  //System.out.println("currentSlot = " + currentSlot);
  // Base case:
  if( toParse instanceof Number || toParse instanceof String )
    {
    return toParse;
    }
  else if( toParse instanceof Polylist && ((Polylist)toParse).nonEmpty() )
    {
    try
      {
      // Recursively evaluate until we get down to two numbers we can add.
      Polylist parsing = (Polylist)toParse;
      
      if( BUILTIN.equals(parsing.first()) )
        {
        return evaluateBuiltin(parsing.second(), parsing.third());
        }
      else if( PLUS.equals(parsing.first()) )
        {
        return Arith.add(evaluate(parsing.second()), evaluate(parsing.third()));
        }
      else if( MINUS.equals(parsing.first()) )
        {
        return Arith.subtract(evaluate(parsing.second()), evaluate(parsing.third()));
        }
      else if( TIMES.equals(parsing.first()) )
        {
        return Arith.multiply(evaluate(parsing.second()), evaluate(parsing.third()));
        }
      else if( DIVIDE.equals(parsing.first()) )
        {
        return Arith.divide(evaluate(parsing.second()), evaluate(parsing.third()));
        }
      else
        {
        Polylist p = Polylist.nil;
        Polylist L = parsing;
        while( L.nonEmpty() )
          {
            p = Polylist.cons(evaluate(L.first()), p);
            L = L.rest();
          }
        p = p.reverse();
        return p;
        }
      }
    catch( ClassCastException e )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Bad cast operation in evaluation of " + toParse);
      return null;
      }
    }
  else
    {
    return null;
    }
  }
/**
 * Evaluate the rest of a special form (splice <operator> <arg> ...)
 * (<operator> <arg> ...) must return a list.
 * Currently the only operator available is literal, e.g.
 * a rule RHS of the form
 * (C4 (splice literal C8 C8) (splice literal C16 C16 C16 C16) R4)
 * returns (C4 C8 C8 C16 C16 C16 C16 R4)
 * 
 * It is expected that additional splice-oriented operators will be added,
 * and not all will just use literal arguments.
 * 
 * @param form
 * @return 
 */

Polylist evaluateSplice(Polylist form)
  {
    if( form.isEmpty() )
      {
        return Polylist.nil;
      }
    
    Object operator = form.first();
    Polylist args = form.rest();
    
    if( LITERAL.equals(operator) )
      {
        return args;
      }
    if(SYNCOPATION.equals(operator))
    {
        MelodyPart melody = notate.getCurrentMelodyPart();    
        MelodyPart currMelody = melody.extract(currentSlot - LENGTH_OF_TRADE, currentSlot);
        int[] syncVector = currMelody.getSyncVector(15, LENGTH_OF_TRADE);
        int measures = LENGTH_OF_TRADE/SLOTS_PER_MEASURE;
        int synco = Tension.getSyncopation(syncVector, measures);
        //Generates a syncopation that matches the syncopation of the previous 4 bars
        int[] rhythm = Generator.generateSyncopation(measures, synco);
        String[] rhythmArray = Generator.generateString(rhythm, (String)args.first());
        Polylist rhythmList = Polylist.PolylistFromArray(rhythmArray);
        return rhythmList;
    }
    // default
    
    return Polylist.nil;
  }

public int getCurrentSlot()
{
    return currentSlot;
}

// For testing purposes only:

int expectancyValue = 0;
int syncopationValue = 1;

private static int LENGTH_OF_TRADE = 4*480;
private static int SLOTS_PER_MEASURE = 480;

/**
 * Evaluates the arguments following the builtin operator. 
 * So far only implemented for expectancy and syncopation
 * @param arg1
 * @param arg2
 * @return 
 */
private Object evaluateBuiltin(Object arg1, Object arg2)
{
    MelodyPart melody = notate.getCurrentMelodyPart();    
    MelodyPart currMelody = melody.extract(currentSlot - LENGTH_OF_TRADE, currentSlot);
    ChordPart chords = notate.getChordProg();
    if( EXPECTANCY.equals(arg1) )
    {
        int firstIndex = currMelody.getNextIndex(0);
        int secondIndex = currMelody.getNextIndex(firstIndex);
        if(currMelody.getNote(firstIndex) == null || currMelody.getNote(secondIndex) == null)
        {
            return new Double(1);
        }
        PartIterator pi = currMelody.iterator(secondIndex);
        int numPitches = 2;
        double totalExpectancy = 0;
        while(pi.hasNext())
        {
            Chord c = chords.getChord(pi.nextIndex());
            int first = currMelody.getNote(firstIndex).getPitch();
            int second = currMelody.getNote(secondIndex).getPitch();
            int curr = currMelody.getNote(pi.nextIndex()).getPitch();
            double expectancy = Expectancy.getExpectancy(curr, second, first, c);
            totalExpectancy += expectancy;
            numPitches ++;
            firstIndex = secondIndex;
            secondIndex = pi.nextIndex();
            pi.next();
        }
        return new Double (totalExpectancy/numPitches);
    }
    if(SYNCOPATION.equals(arg1))
    {
        int[] syncVector = currMelody.getSyncVector(15, LENGTH_OF_TRADE);
        int synco = Tension.getSyncopation(syncVector, (LENGTH_OF_TRADE/SLOTS_PER_MEASURE));
        System.out.println(synco);
        double syncoPerMeasure = synco/(LENGTH_OF_TRADE/SLOTS_PER_MEASURE);
        System.out.println("Syncopation per measure " + syncoPerMeasure);
        if(arg2.equals(HIGH))
        {
            if(syncoPerMeasure >= 8)
            {
                System.out.println("High");
                return new Double(0.8);
            }
            else
            {
                return new Double(0.1);
            }
        }
        if(arg2.equals(MEDIUM))
        {
            if(syncoPerMeasure < 8 && syncoPerMeasure >= 4)
            {
                System.out.println("Medium");
                return new Double(0.8);
            }
            else
            {
                return new Double(0.1);
            }
        }
        if(arg2.equals(LOW))
        {
            if(syncoPerMeasure < 4)
            {
                System.out.println("Low");
                return new Double(0.8);
            }
            else
            {
                return new Double(0.1);
            }
        }
        return new Double(0);
    }
    return new Double(0);
}

// Recursively replace all instances of varName with value in toReplace

private Polylist replace(String varName, Long value, Polylist toReplace)
  {
  Polylist toReturn = Polylist.nil;
  
  Polylist L = toReplace;
  
  while( L.nonEmpty() )
    {
    if( L.first() instanceof Polylist )
      {
      toReturn = toReturn.cons(replace(varName, value, (Polylist)L.first()));
      }
    else if( varName.equals(L.first()) )
      {
      toReturn = toReturn.cons(value);
      }
    else
      {
      toReturn = toReturn.cons(L.first());
      }
    L = L.rest();
    }

  return toReturn.reverse();
  }

}
