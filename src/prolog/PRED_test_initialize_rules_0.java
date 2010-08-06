package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_initialize_rules/0</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_initialize_rules_0 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("Medians: ");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("MedianDiffs: ");

    public PRED_test_initialize_rules_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_initialize_rules_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_initialize_rules";
    }

    public Predicate exec(Prolog engine) {
    // test_initialize_rules:-all_rules(A),initialize_rules(A,B,C,D),write('Medians: '),write(D),nl,write('MedianDiffs: '),write(C),nl
        engine.setB0();
        Term a1, a2, a3;
        Predicate p1, p2, p3, p4, p5, p6, p7;
    // test_initialize_rules:-[all_rules(A),initialize_rules(A,B,C,D),write('Medians: '),write(D),nl,write('MedianDiffs: '),write(C),nl]
        a1 = new VariableTerm(engine);
        a2 = new VariableTerm(engine);
        a3 = new VariableTerm(engine);
        p1 = new PRED_nl_0(cont);
        p2 = new PRED_write_1(a2, p1);
        p3 = new PRED_write_1(s2, p2);
        p4 = new PRED_nl_0(p3);
        p5 = new PRED_write_1(a3, p4);
        p6 = new PRED_write_1(s1, p5);
        p7 = new PRED_initialize_rules_4(a1, new VariableTerm(engine), a2, a3, p6);
        return new PRED_all_rules_1(a1, p7);
    }
}
