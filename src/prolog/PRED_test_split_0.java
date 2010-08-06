package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_split/0</code> defined in syncopation.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_split_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(0);
    static IntegerTerm si2 = new IntegerTerm(1);
    static IntegerTerm si3 = new IntegerTerm(2);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("[]");
    static ListTerm s5 = new ListTerm(si2, s4);
    static ListTerm s6 = new ListTerm(si3, s5);
    static ListTerm s7 = new ListTerm(si2, s6);
    static ListTerm s8 = new ListTerm(si1, s7);
    static ListTerm s9 = new ListTerm(si2, s5);
    static ListTerm s10 = new ListTerm(si1, s9);
    static ListTerm s11 = new ListTerm(si3, s4);

    public PRED_test_split_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_split_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_split";
    }

    public Predicate exec(Prolog engine) {
    // test_split:-A=[0,1,2,1],split(A,[0,1,1],[1,1]),split(A,2,B,C),split_multiple(A,[2],[B,C])
        engine.setB0();
        Term a1, a2, a3, a4, a5;
        Predicate p1, p2;
    // test_split:-['$unify'(A,[0,1,2,1]),split(A,[0,1,1],[1,1]),split(A,2,B,C),split_multiple(A,[2],[B,C])]
        a1 = new VariableTerm(engine);
        //START inline expansion of $unify(a(1),s(8))
        if (! a1.unify(s8, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a2 = new VariableTerm(engine);
        a3 = new VariableTerm(engine);
        a4 = new ListTerm(a3, s4);
        a5 = new ListTerm(a2, a4);
        p1 = new PRED_split_multiple_3(a1, s11, a5, cont);
        p2 = new PRED_split_4(a1, si3, a2, a3, p1);
        return new PRED_split_3(a1, s10, s9, p2);
    }
}
