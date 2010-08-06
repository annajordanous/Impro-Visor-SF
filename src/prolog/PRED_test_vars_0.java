package prolog;

import prolog.PRED_vars_2;
import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_vars/0</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_vars_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(2);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("[]");

    public PRED_test_vars_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_vars_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_vars";
    }

    public Predicate exec(Prolog engine) {
    // test_vars:-vars([1,2,A,B],[A,B])
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6, a7, a8;
    // test_vars:-[vars([1,2,A,B],[A,B])]
        a1 = new VariableTerm(engine);
        a2 = new VariableTerm(engine);
        a3 = new ListTerm(a2, s3);
        a4 = new ListTerm(a1, a3);
        a5 = new ListTerm(si2, a4);
        a6 = new ListTerm(si1, a5);
        a7 = new ListTerm(a2, s3);
        a8 = new ListTerm(a1, a7);
        return new PRED_vars_2(a6, a8, cont);
    }
}
