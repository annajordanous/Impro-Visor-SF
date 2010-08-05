package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_remove/0</code> defined in rule_expander.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_remove_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(2);
    static IntegerTerm si3 = new IntegerTerm(3);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("[]");
    static ListTerm s5 = new ListTerm(si3, s4);
    static ListTerm s6 = new ListTerm(si2, s5);
    static ListTerm s7 = new ListTerm(si1, s6);
    static SymbolTerm s8 = SymbolTerm.makeSymbol("a");
    static SymbolTerm s9 = SymbolTerm.makeSymbol("b");
    static SymbolTerm s10 = SymbolTerm.makeSymbol("c");
    static ListTerm s11 = new ListTerm(s10, s4);
    static ListTerm s12 = new ListTerm(s9, s11);
    static ListTerm s13 = new ListTerm(s8, s12);
    static SymbolTerm s14 = SymbolTerm.makeSymbol("anything");
    static ListTerm s15 = new ListTerm(s14, s4);
    static SymbolTerm s16 = SymbolTerm.makeSymbol("anyOther");
    static ListTerm s17 = new ListTerm(s16, s4);

    public PRED_test_remove_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_remove_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_remove";
    }

    public Predicate exec(Prolog engine) {
    // test_remove:-remove(1,[1,2,3],[a,b,c],[2,3],[b,c]),remove(A,[anything],[anyOther],[],[])
        engine.setB0();
        Predicate p1;
    // test_remove:-[remove(1,[1,2,3],[a,b,c],[2,3],[b,c]),remove(A,[anything],[anyOther],[],[])]
        p1 = new PRED_remove_5(new VariableTerm(engine), s15, s17, s4, s4, cont);
        return new PRED_remove_5(si1, s7, s13, s6, s12, p1);
    }
}
