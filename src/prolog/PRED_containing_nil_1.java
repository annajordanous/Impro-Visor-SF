package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>containing_nil/1</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_containing_nil_1 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static ListTerm s2 = new ListTerm(s1, s1);

    public Term arg1;

    public PRED_containing_nil_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_containing_nil_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "containing_nil(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // containing_nil(A):-A=..[B,[]]
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
    // containing_nil(A):-['$univ'(A,[B,[]])]
        a2 = new ListTerm(new VariableTerm(engine), s2);
        return new PRED_$univ_2(a1, a2, cont);
    }
}
