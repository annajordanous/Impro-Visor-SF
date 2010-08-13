package imp.attributes;

import jp.ac.kobe_u.cs.prolog.builtin.*;
import jp.ac.kobe_u.cs.prolog.lang.*;
import java.util.ArrayList;
import prolog.*;
import polya.*;

public class PrologGrammar {
    protected PrologControl prolog;
    protected static final Term NIL_SYM = SymbolTerm.makeSymbol( "[]" );
    protected static final ListTerm NIL = new ListTerm(SymbolTerm.makeSymbol( "" ),
						 NIL_SYM);


    public PrologGrammar(ArrayList<String> attributeNames,
                         ArrayList<ArrayList<Double>> exponents,
                         ArrayList<ArrayList<Double>> avgs,
                         int timeStep,
                         int measureLength,
                         Polylist rules) {

	SymbolTerm      name;
	ListTerm        exponentList, avgList;

	prolog = new PrologControl();
//        System.out.println("constructor: prolog control: "+prolog);
//        System.out.println("rules? : "+rules.toString());


	assertFunctor("time_step", 	new Term[] {new IntegerTerm(timeStep)});
	assertFunctor("measure_length", new Term[] {new IntegerTerm(measureLength)});

	// for each exponent and avg, assert it:
        for(int i = 0; i < attributeNames.size(); i++)
        {
	    name         = SymbolTerm.makeSymbol(attributeNames.get(i));

	    assertFunctor("exponent",new Term[]{name,
                                                new JavaObjectTerm(exponents.get(i))});
	    assertFunctor("avg"     ,new Term[]{name
                                              , new JavaObjectTerm(avgs.get(i))});
	}

	// Call rule_initialize on the rules.
	Predicate init = new PRED_initialize_rules_4();
	Term ruleArg = rulesToList(rules);
	Term[] args = {ruleArg,
		       new VariableTerm(),
		       new VariableTerm(),
		       new VariableTerm()};

	prolog.setPredicate(init, args);
	if (! prolog.call())	// this runs the predicate, and returns true iff
				// it succeeds.
	    System.out.println("Rule initialization failed!");
    }

        // run an already initialized PrologGrammar
    public Polylist run(int duration) {

	Predicate run = new PRED_run_grammar_2();
	Term output = new VariableTerm();
        System.out.println("run -- prolog control: "+prolog);

	prolog.setPredicate(
	    run, new Term[]{ new DoubleTerm((double) duration),
			     output});

        if (! prolog.call()) // run the predicate.  if it fails, return nil.
            return Polylist.nil;

        // debugging output of the result
        System.out.println(output);
        System.out.println(output.toJava().toString());

        return listTermToPolylist((ListTerm)output);
    }

    // convert a ListTerm into a Polylist of strings
    protected static Polylist listTermToPolylist(ListTerm L) {
	Polylist list = Polylist.nil;

	if (L.equals(NIL))
	    return list;

	do {
	    list = list.cons(L.car());
	} while (! L.cdr().equals(NIL_SYM));

	return list;
    }

    private void assertFunctor(String name, Term[] body) {
        //System.out.println("body length: "+body.length);
        //System.out.println("prolog control: "+prolog);
	Term[] arg =
	    {new StructureTerm(
		SymbolTerm.makeSymbol(name, body.length),
		body)};
	prolog.setPredicate(new PRED_assert_1(),
			    arg);
    }

    public static ListTerm doubleArrayListToTerm(ArrayList<Double> a) {
        return doubleArrayListToTermHelper(a, 0);
    }
    private static ListTerm doubleArrayListToTermHelper(
        ArrayList<Double> a,
        int index) {

        if (a.size() == index)
            return NIL;

        return new ListTerm(
            new DoubleTerm(a.get(index)),
            doubleArrayListToTermHelper(a, index+1));
    }



    private Term rulesToList(Polylist rules) {
	if(rules.isEmpty())
	    return NIL_SYM;

	Polylist rule = (Polylist) (rules.first());
	String   type = (String)   (rule.first());

	if (type.equals("rule") || type.equals("base")) {
	// convert the rule into a list
	return new ListTerm(ruleToFunctor(rule),
			    rulesToList(rules.rest()));

        }

        // nonvalid type, ignore.
        return rulesToList(rules.rest());
    }

    // Makes the argument list for a rule.
    // Returns: a list of Terms representing the rule.  Since we don't have
    // gensym, we have to leave the expression without a variable.  This wil
    protected StructureTerm ruleToFunctor(Polylist ruleList) {
        Term name;
	Term expansion;
	DoubleTerm weight;
	Term expression;

        PolylistEnum rule     = new PolylistEnum(ruleList.rest());
        Polylist maybeName    = (Polylist) rule.nextElement();

        if (maybeName.length() == 1) //  not a production.
        {
	    name       = SymbolTerm.makeSymbol((String) maybeName.first());
	    expansion  = convertPolylistExpansion((Polylist) rule.nextElement());
	    weight     = new DoubleTerm((Double) rule.nextElement());
	    expression = SymbolTerm.makeSymbol("true");

	    return new StructureTerm(SymbolTerm.makeSymbol("rule",4),
                    		     new Term[]{name, expansion, weight, expression});
	}

        // otherwise, it is a production.

        // if it is the base case of a production,
        Polylist givenExpansion = (Polylist) rule.nextElement();
        if (givenExpansion.isEmpty()) {
            name = new StructureTerm(
                    SymbolTerm.makeSymbol((String) maybeName.first(), 1),
                    new Term[] { new DoubleTerm((Double) maybeName.second())});
            return new StructureTerm(SymbolTerm.makeSymbol("rule",4),
                new Term[] {
                  name,
                  NIL_SYM,
                  new DoubleTerm(1.0),
                  SymbolTerm.makeSymbol("true")});
        }

        // if it is the recursive case of a production,

        VariableTerm argument = new VariableTerm();
	name = new StructureTerm(
	    SymbolTerm.makeSymbol((String) ((Polylist) maybeName).first(), 1),
	    new Term[] {argument});

        // convert the expansion correctly.  This means turning the Polylist
	// production term into a functor of the name and a generated
	// symbol.


	Term[] expansionAndExpression =
	    convertProductionExpansion((Polylist) givenExpansion, argument);
	expansion  = expansionAndExpression[1];
	expression = expansionAndExpression[0];

        Object untypedWeight = rule.nextElement();
        System.out.println(untypedWeight);
        weight     = new DoubleTerm((Double) untypedWeight);

	// return the functor form
	return new StructureTerm(
	    SymbolTerm.makeSymbol("rule", 4),
	    new Term[] {name, expansion, weight, expression});

    }

    // converts a polylist expansion into a ListTerm.
    public Term convertPolylistExpansion(Polylist poly) {
	if (poly.isEmpty())
	    return NIL_SYM;

	return new ListTerm(

	    SymbolTerm.makeSymbol((String) poly.first()),
	    convertPolylistExpansion(poly.rest()));
    }

    // converts a polylist expansion of a production into a ListTerm.
    public static Term[] convertProductionExpansion(Polylist exp
                                                  , VariableTerm arg) {

        return productionExpansionHelper(exp, NIL_SYM, NIL_SYM, arg);
    }

    
    protected static Term[] productionExpansionHelper(
	Polylist expansion,
	Term expressionAccum,
	Term expansionAccum,
        VariableTerm oldVar) {

	if(expansion.isEmpty())
            return new Term[] {expressionAccum, expansionAccum};

	Object elem = expansion.first();

	if(elem instanceof String) { // not a production
	    return productionExpansionHelper(
		expansion.rest(),
		expressionAccum,
		new ListTerm(SymbolTerm.makeSymbol((String) elem),
			     expansionAccum),
                oldVar);
	}



        // otherwise, it is a production.
	// make a new variable for the recursive argument
	VariableTerm newVar = new VariableTerm();

	return productionExpansionHelper(
	    ((Polylist) expansion).rest(),
	    // the expression is
	    generateExpression((Polylist) elem, newVar, oldVar),
	    new ListTerm(	//  make the expansion
		new StructureTerm(
		    SymbolTerm.makeSymbol((String) ((Polylist) elem).first(), 1),
		    new Term[] {newVar}),
		expansionAccum),
            oldVar);
    }

    protected static StructureTerm generateExpression(Polylist production,
						      VariableTerm newVar,
						      VariableTerm oldVar) {

	// production is of the form (- X constant). Get the constant:
	Double constant = ((Long)((Polylist)production.second()).third()).doubleValue();


        // make sure it's greater than zero.
        StructureTerm baseCase = new StructureTerm(
                SymbolTerm.makeSymbol(">", 2),
                new Term[] {oldVar,
                            new DoubleTerm(0)});

        // make the expression "X-const" as a Prolog structure.
	StructureTerm expression = new StructureTerm(
	    SymbolTerm.makeSymbol("-", 2),
	    new Term[] {oldVar,
			new DoubleTerm(constant)});

	// make the functor "is(Y,X-const)" aka "Y is X-const"
	StructureTerm recurse = new StructureTerm(
	    SymbolTerm.makeSymbol("is", 2),
	    new Term[] {newVar, expression});

        // the expression we call is the base case and the recursion case
        return new StructureTerm(
                SymbolTerm.makeSymbol(",", 2),
                new Term[] {baseCase, recurse});

    }

    public static void main(String[] args) {
	return;
    }
}
