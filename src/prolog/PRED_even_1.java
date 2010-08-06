package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>even/1</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_even_1 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(2);

    public Term arg1;

    public PRED_even_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_even_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "even(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // even(A):-A is 2*(A//2)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
    // even(A):-['$int_quotient'(A,2,B),'$multi'(2,B,A)]
        a2 = new VariableTerm(engine);
        //START inline expansion of $int_quotient(a(1),si(1),a(2))
        try {
            if (! a2.unify(Arithmetic.evaluate(a1).intDivide(si1), engine.trail)) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        //START inline expansion of $multi(si(1),a(2),a(1))
        try {
            if (! a1.unify(si1.multiply(Arithmetic.evaluate(a2)), engine.trail)) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        return cont;
    }
}
