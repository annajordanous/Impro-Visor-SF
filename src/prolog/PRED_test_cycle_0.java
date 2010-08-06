import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_cycle/0</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_cycle_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(2);
    static IntegerTerm si3 = new IntegerTerm(3);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("[]");
    static ListTerm s5 = new ListTerm(si3, s4);
    static ListTerm s6 = new ListTerm(si2, s5);
    static ListTerm s7 = new ListTerm(si1, s6);
    static ListTerm s8 = new ListTerm(si3, s7);
    static ListTerm s9 = new ListTerm(si2, s8);
    static ListTerm s10 = new ListTerm(si1, s9);
    static ListTerm s11 = new ListTerm(si3, s10);
    static ListTerm s12 = new ListTerm(si2, s11);
    static ListTerm s13 = new ListTerm(si1, s12);

    public PRED_test_cycle_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_cycle_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_cycle";
    }

    public Predicate exec(Prolog engine) {
    // test_cycle:-cycle([1,2,3],3,[1,2,3,1,2,3,1,2,3])
        engine.setB0();
    // test_cycle:-[cycle([1,2,3],3,[1,2,3,1,2,3,1,2,3])]
        return new PRED_cycle_3(s7, si3, s13, cont);
    }
}
