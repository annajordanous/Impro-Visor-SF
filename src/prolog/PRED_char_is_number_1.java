package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>char_is_number/1</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_char_is_number_1 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(48);
    static IntegerTerm si2 = new IntegerTerm(57);

    public Term arg1;

    public PRED_char_is_number_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_char_is_number_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "char_is_number(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // char_is_number(A):-A>=48,A=<57
        engine.setB0();
        Term a1;
        a1 = arg1;
    // char_is_number(A):-['$greater_or_equal'(A,48),'$less_or_equal'(A,57)]
        //START inline expansion of $greater_or_equal(a(1),si(1))
        try {
            if (Arithmetic.evaluate(a1).arithCompareTo(si1) < 0) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        //START inline expansion of $less_or_equal(a(1),si(2))
        try {
            if (Arithmetic.evaluate(a1).arithCompareTo(si2) > 0) {
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
