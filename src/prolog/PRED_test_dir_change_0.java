import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_dir_change/0</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_dir_change_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(240);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("slope", 3);
    static IntegerTerm si3 = new IntegerTerm(1);
    static IntegerTerm si4 = new IntegerTerm(2);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("t", 2);
    static SymbolTerm s6 = SymbolTerm.makeSymbol("c");
    static IntegerTerm si7 = new IntegerTerm(120);
    static Term[] s8 = {s6, si7};
    static StructureTerm s9 = new StructureTerm(s5, s8);
    static SymbolTerm s10 = SymbolTerm.makeSymbol("[]");
    static ListTerm s11 = new ListTerm(s9, s10);
    static ListTerm s12 = new ListTerm(s9, s11);
    static Term[] s13 = {si3, si4, s12};
    static StructureTerm s14 = new StructureTerm(s2, s13);
    static ListTerm s15 = new ListTerm(s14, s10);
    static DoubleTerm sf16 = new DoubleTerm(0.0);
    static IntegerTerm si17 = new IntegerTerm(360);
    static IntegerTerm si18 = new IntegerTerm(-1);
    static IntegerTerm si19 = new IntegerTerm(-2);
    static Term[] s20 = {si18, si19, s11};
    static StructureTerm s21 = new StructureTerm(s2, s20);
    static ListTerm s22 = new ListTerm(s21, s10);
    static ListTerm s23 = new ListTerm(s14, s22);
    static IntegerTerm si24 = new IntegerTerm(17);
    static IntegerTerm si25 = new IntegerTerm(42);
    static Term[] s26 = {si24, si25, s12};
    static StructureTerm s27 = new StructureTerm(s2, s26);
    static IntegerTerm si28 = new IntegerTerm(-17);
    static IntegerTerm si29 = new IntegerTerm(-42);
    static Term[] s30 = {si28, si29, s11};
    static StructureTerm s31 = new StructureTerm(s2, s30);
    static ListTerm s32 = new ListTerm(s31, s10);
    static ListTerm s33 = new ListTerm(s27, s32);
    static IntegerTerm si34 = new IntegerTerm(480);
    static IntegerTerm si35 = new IntegerTerm(5);
    static IntegerTerm si36 = new IntegerTerm(6);
    static Term[] s37 = {si35, si36, s11};
    static StructureTerm s38 = new StructureTerm(s2, s37);
    static ListTerm s39 = new ListTerm(s38, s10);
    static ListTerm s40 = new ListTerm(s21, s39);
    static ListTerm s41 = new ListTerm(s14, s40);
    static ListTerm s42 = new ListTerm(s31, s39);
    static ListTerm s43 = new ListTerm(s27, s42);

    public PRED_test_dir_change_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_dir_change_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_dir_change";
    }

    public Predicate exec(Prolog engine) {
    // test_dir_change:-direction_change(240,[slope(1,2,[t(c,120),t(c,120)])],0.0),direction_change(360,[slope(1,2,[t(c,120),t(c,120)]),slope(-1,-2,[t(c,120)])],A),direction_change(360,[slope(17,42,[t(c,120),t(c,120)]),slope(-17,-42,[t(c,120)])],A),direction_change(480,[slope(1,2,[t(c,120),t(c,120)]),slope(-1,-2,[t(c,120)]),slope(5,6,[t(c,120)])],B),direction_change(480,[slope(17,42,[t(c,120),t(c,120)]),slope(-17,-42,[t(c,120)]),slope(5,6,[t(c,120)])],B)
        engine.setB0();
        Term a1, a2;
        Predicate p1, p2, p3, p4;
    // test_dir_change:-[direction_change(240,[slope(1,2,[t(c,120),t(c,120)])],0.0),direction_change(360,[slope(1,2,[t(c,120),t(c,120)]),slope(-1,-2,[t(c,120)])],A),direction_change(360,[slope(17,42,[t(c,120),t(c,120)]),slope(-17,-42,[t(c,120)])],A),direction_change(480,[slope(1,2,[t(c,120),t(c,120)]),slope(-1,-2,[t(c,120)]),slope(5,6,[t(c,120)])],B),direction_change(480,[slope(17,42,[t(c,120),t(c,120)]),slope(-17,-42,[t(c,120)]),slope(5,6,[t(c,120)])],B)]
        a1 = new VariableTerm(engine);
        a2 = new VariableTerm(engine);
        p1 = new PRED_direction_change_3(si34, s43, a2, cont);
        p2 = new PRED_direction_change_3(si34, s41, a2, p1);
        p3 = new PRED_direction_change_3(si17, s33, a1, p2);
        p4 = new PRED_direction_change_3(si17, s23, a1, p3);
        return new PRED_direction_change_3(si1, s15, sf16, p4);
    }
}
