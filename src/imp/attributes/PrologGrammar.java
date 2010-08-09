package imp.attributes;

import jp.ac.kobe_u.cs.prolog.builtin.*;
import jp.ac.kobe_u.cs.prolog.lang.*;
import java.util.ArrayList;
import prolog.*;
import polya.*;

public class PrologGrammar {
    protected PrologControl prolog;
    protected static ListTerm NIL = new ListTerm(SymbolTerm.makeSymbol( "" ),
						 SymbolTerm.makeSymbol( "[]" ));


    public PrologGrammar(ArrayList<String> attributeNames,
                         ArrayList<ArrayList<Double>> exponents,
                         ArrayList<ArrayList<Double>> avgs,
                         int timeStep,
                         int measureLength,
                         Polylist rules) {

	SymbolTerm      name;
	ListTerm        exponentList, avgList;

	prolog = new PrologControl();
        System.out.println("constructor: prolog control: "+prolog);

	assertFunctor("time_step", 	new Term[] {new IntegerTerm(timeStep)});
	assertFunctor("measure_length", new Term[] {new IntegerTerm(measureLength)});

	// for each exponent and avg, assert it:
        for(int i = 0; i < attributeNames.size(); i++)
        {
	    name         = SymbolTerm.makeSymbol(attributeNames.get(i));
            exponentList = doubleArrayListToTerm(exponents.get(i));
            avgList      = doubleArrayListToTerm(avgs.get(i));

	    assertFunctor("exponent",new Term[]{exponentList});
	    assertFunctor("avg"     ,new Term[]{avgList});
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
	    run,new Term[]{ new IntegerTerm(duration),
			     output});

        return listTermToPolylist((ListTerm)output);
    }

    // convert a ListTerm into a Polylist of strings
    protected static Polylist listTermToPolylist(ListTerm L) {
	Polylist list = Polylist.nil;
	Term elem;
	Term nilSym = SymbolTerm.makeSymbol("[]");

	if (L.equals(NIL))
	    return list;

	do {
	    list = list.cons(L.car());
	} while (! L.cdr().equals(nilSym));

	return list;
    }

    public void assertFunctor(String name, Term[] body) {
        System.out.println("body length: "+body.length);
        System.out.println("prolog control: "+prolog);
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



    protected ListTerm rulesToList(Polylist rules) {
	if(rules.isEmpty())
	    return NIL;

	Polylist rule = (Polylist) (rules.first());
	String   type = (String)   (rule.first());

	if (! rule.equals("rule"))
	    return rulesToList(rules.rest());

	// convert the rule into a list
	return new ListTerm(ruleToFunctor(rule),
			    rulesToList(rules.rest()));
    }

    // Makes the argument list for a rule.
    // Returns: a list of Terms representing the rule.  Since we don't have
    // gensym, we have to leave the expression without a variable.  This wil
    protected StructureTerm ruleToFunctor(Polylist ruleList) {
        Term name;
	ListTerm expansion;
	DoubleTerm weight;
	Term expression;

        PolylistEnum rule     = new PolylistEnum(ruleList);
        SymbolTerm ruleSymbol = SymbolTerm.makeSymbol(
	    (String) rule.nextElement());
        Object maybeName      = rule.nextElement();

        if (maybeName instanceof String) //  not a production.
        {
	    name       = SymbolTerm.makeSymbol((String) maybeName);
	    expansion  = convertPolylistExpansion((Polylist) rule.nextElement());
	    weight     = new DoubleTerm((Double) rule.nextElement());
	    expression = SymbolTerm.makeSymbol("true");

	    return new StructureTerm( (SymbolTerm) name,
				      new Term[]{expansion, weight, expression});
	}

        // otherwise, it is a production.
	VariableTerm argument = new VariableTerm();
	name = new StructureTerm(
	    SymbolTerm.makeSymbol((String) ((Polylist) maybeName).first()),
	    new Term[] {argument});

        // convert the expansion correctly.  This means turning the Polylist
	// production term into a functor of the name and a generated
	// symbol.

	Term[] expansionAndExpression =
	    productionExpansion((Polylist) rule.nextElement());
	expansion  = (ListTerm) expansionAndExpression[0];
	expression = expansionAndExpression[1];

	weight     = new DoubleTerm((Double) rule.nextElement());

	// return the functor form
	return new StructureTerm(
	    SymbolTerm.makeSymbol( "rule"),
	    new Term[] {name, expansion, weight, expression});

    }

    // converts a polylist expansion into a ListTerm.  It changes the Improvisor
    // note notation into t(<type>, <duration>) functors.
    public ListTerm convertPolylistExpansion(Polylist poly) {
	if (poly.isEmpty())
	    return NIL;

	return new ListTerm(

	    SymbolTerm.makeSymbol((String) poly.first()),
	    convertPolylistExpansion(poly.rest()));
    }


    public static Term[] productionExpansion(Polylist exp) {
	return productionExpansionHelper(exp, NIL, NIL);
    }

    public static Term[] productionExpansionHelper(
	Polylist expansion,
	Term expressionAccum,
	Term expansionAccum) {

	if(expansion.isEmpty())
	    return new Term[] {expressionAccum, expansionAccum};

	Object elem = expansion.first();

	if(elem instanceof String) { // not a production
	    return productionExpansionHelper(
		expansion.rest(),
		expressionAccum,
		new ListTerm(SymbolTerm.makeSymbol((String) elem),
			     expansionAccum));
	}



        // otherwise, it is a production.
	// make a new variable for the recursive argument
	VariableTerm oldVar = new VariableTerm();
	VariableTerm newVar = new VariableTerm();

	return productionExpansionHelper(
	    ((Polylist) expansion).rest(),
	    // the expression is
	    generateExpression((Polylist) elem, newVar, oldVar),
	    new ListTerm(	//  make the expansion
		new StructureTerm(
		    SymbolTerm.makeSymbol((String) ((Polylist) elem).first()),
		    new Term[] {newVar}),
		expansionAccum));
    }

    protected static StructureTerm generateExpression(Polylist production,
						      VariableTerm newVar,
						      VariableTerm oldVar) {

	// production is of the form (- X constant). Get the constant:
	Double constant = (Double) production.third();

	// make the expression "X-const" as a Prolog structure.
	StructureTerm expression = new StructureTerm(
	    SymbolTerm.makeSymbol("-"),
	    new Term[] {oldVar,
			new DoubleTerm(constant)});

	// make the functor "is(Y,X-const)" aka "Y is X-const"
	return new StructureTerm(
	    SymbolTerm.makeSymbol("is"),
	    new Term[] {newVar,expression});

    }

    public static void main(String[] args) {
	return;
    }
}
