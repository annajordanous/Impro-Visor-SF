package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_matrix_transpose/0</code> defined in transpose.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_matrix_transpose_0 extends Predicate {
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
    static IntegerTerm si14 = new IntegerTerm(7);
    static IntegerTerm si15 = new IntegerTerm(8);
    static IntegerTerm si16 = new IntegerTerm(9);
    static ListTerm s17 = new ListTerm(si16, s4);
    static ListTerm s18 = new ListTerm(si15, s17);
    static ListTerm s19 = new ListTerm(si14, s18);
    static IntegerTerm si20 = new IntegerTerm(10);
    static IntegerTerm si21 = new IntegerTerm(11);
    static IntegerTerm si22 = new IntegerTerm(12);
    static ListTerm s23 = new ListTerm(si22, s4);
    static ListTerm s24 = new ListTerm(si21, s23);
    static ListTerm s25 = new ListTerm(si20, s24);
    static ListTerm s26 = new ListTerm(s25, s4);
    static ListTerm s27 = new ListTerm(s19, s26);
    static ListTerm s28 = new ListTerm(s13, s27);
    static ListTerm s29 = new ListTerm(s7, s28);
    static ListTerm s30 = new ListTerm(si20, s4);
    static ListTerm s31 = new ListTerm(si14, s30);
    static ListTerm s32 = new ListTerm(si8, s31);
    static ListTerm s33 = new ListTerm(si1, s32);
    static ListTerm s34 = new ListTerm(si21, s4);
    static ListTerm s35 = new ListTerm(si15, s34);
    static ListTerm s36 = new ListTerm(si9, s35);
    static ListTerm s37 = new ListTerm(si2, s36);
    static ListTerm s38 = new ListTerm(si16, s23);
    static ListTerm s39 = new ListTerm(si10, s38);
    static ListTerm s40 = new ListTerm(si3, s39);
    static ListTerm s41 = new ListTerm(s40, s4);
    static ListTerm s42 = new ListTerm(s37, s41);
    static ListTerm s43 = new ListTerm(s33, s42);
    static ListTerm s44 = new ListTerm(s7, s4);
    static ListTerm s45 = new ListTerm(si1, s4);
    static ListTerm s46 = new ListTerm(si2, s4);
    static ListTerm s47 = new ListTerm(s5, s4);
    static ListTerm s48 = new ListTerm(s46, s47);
    static ListTerm s49 = new ListTerm(s45, s48);
    static ListTerm s50 = new ListTerm(s45, s4);
    static ListTerm s51 = new ListTerm(s4, s4);

    public PRED_test_matrix_transpose_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_matrix_transpose_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_matrix_transpose";
    }

    public Predicate exec(Prolog engine) {
    // test_matrix_transpose:-test_matrix_transpose([[1,2,3],[4,5,6],[7,8,9],[10,11,12]]),test_matrix_transpose([[1,4,7,10],[2,5,8,11],[3,6,9,12]]),test_matrix_transpose([[1,2,3]]),test_matrix_transpose([[1],[2],[3]]),test_matrix_transpose([[1]]),test_matrix_transpose([[]])
        engine.setB0();
        Predicate p1, p2, p3, p4, p5;
    // test_matrix_transpose:-[test_matrix_transpose([[1,2,3],[4,5,6],[7,8,9],[10,11,12]]),test_matrix_transpose([[1,4,7,10],[2,5,8,11],[3,6,9,12]]),test_matrix_transpose([[1,2,3]]),test_matrix_transpose([[1],[2],[3]]),test_matrix_transpose([[1]]),test_matrix_transpose([[]])]
        p1 = new PRED_test_matrix_transpose_1(s51, cont);
        p2 = new PRED_test_matrix_transpose_1(s50, p1);
        p3 = new PRED_test_matrix_transpose_1(s49, p2);
        p4 = new PRED_test_matrix_transpose_1(s44, p3);
        p5 = new PRED_test_matrix_transpose_1(s43, p4);
        return new PRED_test_matrix_transpose_1(s29, p5);
    }
}
