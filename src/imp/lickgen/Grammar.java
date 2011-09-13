/**
 * This Java Class is part of the Impro-Visor Application
 *
 * Copyright (C) 2005-2011 Robert Keller and Harvey Mudd College
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

import java.io.*;
import java.util.*;
import imp.ImproVisor;
import imp.util.*;
import imp.gui.Notate;
import polya.*;

/*
 * @author David Morrison
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

// Operators:
public static final String PLUS = "+";

public static final String MINUS = "-";

public static final String TIMES = "*";

public static final String DIVIDE = "/";

Vector<String> terminals = new Vector<String>();

Polylist rules = new Polylist();

Polylist terminalString;

String startSymbol = null; // to be set

private int retryCount = 0;


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

public Polylist run(Object data, Notate notate)
  {
    boolean failure = true;
    int savedRetryCount = retryCount;
    int maxRetries = 20;


    while( failure && (retryCount - savedRetryCount) <= maxRetries )
      {
        try
          {
            terminalString = new Polylist();
            Polylist gen = addStart(data);

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
          }
      notate.setLickGenStatus("Retrying lick generation (" + (++retryCount) + " cumulative).");
      }
    notate.setLickGenStatus("Unable to generate in " + maxRetries + " retries.");
    return null;
  }


public Polylist addStart()
  {
  return addStart(null);
  }

// Search through the rules and find the start symbol.  Note that it will
// take the first start symbol it finds.  Returns null if there is an error.

public Polylist addStart(Object data)
  {
  Polylist gen = new Polylist();
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
          
          Polylist s = new Polylist();
          if( data != null )
            {
            s = s.cons(data);
            }
          s = s.cons(startSymbol);
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
        
        while( (pop.first() instanceof String &&
                terminals.contains((String)(pop.first()))) )
          {
          if( pop.first().equals("slope") )
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
      Vector<Polylist> ruleArray = new Vector<Polylist>(5);
      Vector<Polylist> baseArray = new Vector<Polylist>(5);
      Vector<Double> ruleWeights = new Vector<Double>(5);
      Vector<Double> baseWeights = new Vector<Double>(5);

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
            Number weight = (Number)next.fourth();
            baseWeights.add(weight.doubleValue());
            }
          }
        // Most objects will have type RULE.
        else if( type.equals(RULE) && next.length() == 4 )
          {
          Object symbol = next.second();
          Polylist derivation = (Polylist)next.third();

          // The first symbol can never be a variable, it will give the "name" of the
          // rule.  All additional symbols will contain information.

          if( pop.first() instanceof String )
            {

            if( ((String)(pop.first())).equals(((Polylist)symbol).first()) )
              {
              // Fill in variables with their given numeric values.
              derivation = setVars((Polylist)pop, (Polylist)symbol, derivation);

              //System.out.print("evaluating " + derivation);
              
              // Evaluate any expressions that need to be evaluated.
              derivation = (Polylist)evaluate(derivation);
              
              //System.out.println(" to  " + derivation);

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
              Number weight = (Number)next.fourth();
              ruleWeights.add(weight.doubleValue());
              }
              }
            }
          else
            {
            // This RHS element is not a string. Just carry it and hope for the best!
            // It is probably an S-expression such as (slope M N ...)
            ruleArray.add(derivation);
            Number weight = (Number)next.fourth();
            ruleWeights.add(weight.doubleValue());

            }

          }
        search = search.rest();
        }

      // Randomly choose a rule to follow.
      double total = 0.0;

      Vector<Polylist> rulesList;
      Vector<Double> weightArray;

      // If any base cases exist, we ignore all rules.
      if( !baseWeights.isEmpty() )
        {
        rulesList = new Vector(baseArray);
        weightArray = new Vector(baseWeights);
        }
      else
        {
        rulesList = new Vector(ruleArray);
        weightArray = new Vector(ruleWeights);
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

    throw new RuleApplicationException("applyRules, no such rule for " + gen);

    //ErrorLog.log(ErrorLog.SEVERE,
    //        "No rule exists beginning with " + gen.first() + ".  Abort.");
    //return null;
    }
  // If we get here, there's some syntax error with the grammar file.
  catch( ClassCastException e )
    {
    ErrorLog.log(ErrorLog.SEVERE, "Malformed grammar file, exception: " + e);
    throw new RuleApplicationException("Malformed grammar file " + e);
    //return null;
    }
  }

// Load in any terminal values specified in the user file.  Returns a vector containing
// all terminal values.

public Vector<Object> getAllOfType(String t)
  {
  Polylist search = rules;
  Vector<Object> elements = new Vector<Object>();
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


public Vector<String> getTerms()
  {
  Collection terms = (Collection)getAllOfType(TERMINAL);
  if( terminals == null )
    {
    return new Vector<String>(); // empty
    }
  return new Vector<String>(terms);
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


public Vector<Polylist> getParams()
  {
  return new Vector<Polylist>((Collection)getAllOfType(PARAM));
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
    Object ob = null;

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

private Object evaluate(Object toParse)
  {
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
      if( PLUS.equals(parsing.first()) )
        {
        return (Long)evaluate(parsing.second()) + (Long)evaluate(parsing.third());
        }
      else if( MINUS.equals(parsing.first()) )
        {
        return (Long)evaluate(parsing.second()) - (Long)evaluate(parsing.third());
        }
      else if( TIMES.equals(parsing.first()) )
        {
        return (Long)evaluate(parsing.second()) * (Long)evaluate(parsing.third());
        }
      else if( DIVIDE.equals(parsing.first()) )
        {
        return (Long)evaluate(parsing.second()) / (Long)evaluate(parsing.third());
        }
      else
        {
        Polylist p = new Polylist();
        for( int i = 0; i < parsing.length(); ++i )
          {
          p = Polylist.cons(evaluate(parsing.nth(i)), p);
          }
        p = p.reverse();
        return p;
        }
      }
    catch( ClassCastException e )
      {
      ErrorLog.log(ErrorLog.SEVERE, "Bad cast operation in evaluate.");
      return null;
      }
    }
  else
    {
    return null;
    }
  }

// Recursively replace all instances of varName with value in toReplace

private Polylist replace(String varName, Long value, Polylist toReplace)
  {
  Polylist toReturn = new Polylist();
  for( int i = 0; i < toReplace.length(); ++i )
    {
    if( toReplace.nth(i) instanceof Polylist )
      {
      toReturn = toReturn.cons(replace(varName, value,
              (Polylist)toReplace.nth(i)));
      }
    else if( varName.equals(toReplace.nth(i)) )
      {
      toReturn = toReturn.cons(value);
      }
    else
      {
      toReturn = toReturn.cons(toReplace.nth(i));
      }
    }

  toReturn = toReturn.reverse();
  ;
  return toReturn;
  }

}



