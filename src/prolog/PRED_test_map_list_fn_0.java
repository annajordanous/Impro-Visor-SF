import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_map_list_fn/0</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_map_list_fn_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(2);
    static IntegerTerm si3 = new IntegerTerm(3);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("[]");
    static ListTerm s5 = new ListTerm(si3, s4);
    static ListTerm s6 = new ListTerm(si2, s5);
    static ListTerm s7 = new ListTerm(si1, s6);
    static IntegerTerm si8 = new IntegerTerm(4);
    static IntegerTerm si9 = new IntegerTerm(5);
    static IntegerTerm si10 = new IntegerTerm(6);
    static ListTerm s11 = new ListTerm(si10, s4);
    static ListTerm s12 = new ListTerm(si9, s11);
    static ListTerm s13 = new ListTerm(si8, s12);
    static ListTerm s14 = new ListTerm(s13, s4);
    static ListTerm s15 = new ListTerm(s7, s14);
    static SymbolTerm s16 = SymbolTerm.makeSymbol("fn", 2);
    static SymbolTerm s17 = SymbolTerm.makeSymbol("plus", 3);
    static IntegerTerm si18 = new IntegerTerm(7);
    static IntegerTerm si19 = new IntegerTerm(9);
    static ListTerm s20 = new ListTerm(si19, s4);
    static ListTerm s21 = new ListTerm(si18, s20);
    static ListTerm s22 = new ListTerm(si9, s21);
    static ListTerm s23 = new ListTerm(s22, s4);

    public PRED_test_map_list_fn_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_map_list_fn_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_map_list_fn";
    }

    public Predicate exec(Prolog engine) {
    // test_map_list_fn:-map_list_fn([[1,2,3],[4,5,6]],fn([A,B,C],plus(A,B,C)),[[5,7,9]])
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6, a7, a8;
    // test_map_list_fn:-[map_list_fn([[1,2,3],[4,5,6]],fn([A,B,C],plus(A,B,C)),[[5,7,9]])]
        a1 = new VariableTerm(engine);
        a2 = new VariableTerm(engine);
        a3 = new VariableTerm(engine);
        a4 = new ListTerm(a3, s4);
        a5 = new ListTerm(a2, a4);
        a6 = new ListTerm(a1, a5);
        Term[] y1 = {a1, a2, a3};
        a7 = new StructureTerm(s17, y1);
        Term[] y2 = {a6, a7};
        a8 = new StructureTerm(s16, y2);
        return new PRED_map_list_fn_3(s15, a8, s23, cont);
    }
}
