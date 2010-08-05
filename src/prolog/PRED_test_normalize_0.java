package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_normalize/0</code> defined in statistics.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_normalize_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(2);
    static IntegerTerm si3 = new IntegerTerm(3);
    static IntegerTerm si4 = new IntegerTerm(4);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("[]");
    static ListTerm s6 = new ListTerm(si4, s5);
    static ListTerm s7 = new ListTerm(si3, s6);
    static ListTerm s8 = new ListTerm(si2, s7);
    static ListTerm s9 = new ListTerm(si1, s8);
    static DoubleTerm sf10 = new DoubleTerm(0.1);
    static DoubleTerm sf11 = new DoubleTerm(0.2);
    static DoubleTerm sf12 = new DoubleTerm(0.3);
    static DoubleTerm sf13 = new DoubleTerm(0.4);
    static ListTerm s14 = new ListTerm(sf13, s5);
    static ListTerm s15 = new ListTerm(sf12, s14);
    static ListTerm s16 = new ListTerm(sf11, s15);
    static ListTerm s17 = new ListTerm(sf10, s16);

    public PRED_test_normalize_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_normalize_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_normalize";
    }

    public Predicate exec(Prolog engine) {
    // test_normalize:-normalize([1,2,3,4],[0.1,0.2,0.3,0.4]),normalize([],[]),'$dummy_3_statistics.pro'(A)
        engine.setB0();
        Predicate p1, p2;
    // test_normalize:-[normalize([1,2,3,4],[0.1,0.2,0.3,0.4]),normalize([],[]),'$dummy_3_statistics.pro'(A)]
        p1 = new PRED_$dummy_3_statistics$002Epro_1(new VariableTerm(engine), cont);
        p2 = new PRED_normalize_2(s5, s5, p1);
        return new PRED_normalize_2(s9, s17, p2);
    }
}
