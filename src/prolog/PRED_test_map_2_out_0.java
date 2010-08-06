import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_map_2_out/0</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_map_2_out_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(2);
    static IntegerTerm si3 = new IntegerTerm(3);
    static IntegerTerm si4 = new IntegerTerm(4);
    static IntegerTerm si5 = new IntegerTerm(5);
    static IntegerTerm si6 = new IntegerTerm(6);
    static SymbolTerm s7 = SymbolTerm.makeSymbol("[]");
    static ListTerm s8 = new ListTerm(si6, s7);
    static ListTerm s9 = new ListTerm(si5, s8);
    static ListTerm s10 = new ListTerm(si4, s9);
    static ListTerm s11 = new ListTerm(si3, s10);
    static ListTerm s12 = new ListTerm(si2, s11);
    static ListTerm s13 = new ListTerm(si1, s12);
    static SymbolTerm s14 = SymbolTerm.makeSymbol("test_map_2_out_helper");
    static IntegerTerm si15 = new IntegerTerm(0);
    static ListTerm s16 = new ListTerm(si15, s7);
    static ListTerm s17 = new ListTerm(si15, s16);
    static ListTerm s18 = new ListTerm(si1, s17);
    static ListTerm s19 = new ListTerm(si1, s18);
    static ListTerm s20 = new ListTerm(si1, s19);
    static ListTerm s21 = new ListTerm(si1, s20);
    static ListTerm s22 = new ListTerm(si1, s7);
    static ListTerm s23 = new ListTerm(si1, s22);
    static ListTerm s24 = new ListTerm(si15, s23);
    static ListTerm s25 = new ListTerm(si15, s24);
    static ListTerm s26 = new ListTerm(si15, s25);
    static ListTerm s27 = new ListTerm(si15, s26);

    public PRED_test_map_2_out_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_map_2_out_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_map_2_out";
    }

    public Predicate exec(Prolog engine) {
    // test_map_2_out:-map_2_out([1,2,3,4,5,6],test_map_2_out_helper,A,B),A=[1,1,1,1,0,0],B=[0,0,0,0,1,1]
        engine.setB0();
        Term a1, a2;
        Predicate p1, p2;
    // test_map_2_out:-[map_2_out([1,2,3,4,5,6],test_map_2_out_helper,A,B),'$unify'(A,[1,1,1,1,0,0]),'$unify'(B,[0,0,0,0,1,1])]
        a1 = new VariableTerm(engine);
        a2 = new VariableTerm(engine);
        p1 = new PRED_$unify_2(a2, s27, cont);
        p2 = new PRED_$unify_2(a1, s21, p1);
        return new PRED_map_2_out_4(s13, s14, a1, a2, p2);
    }
}
