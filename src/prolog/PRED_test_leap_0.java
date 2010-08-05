package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_leap/0</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_leap_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(300);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("slope", 3);
    static IntegerTerm si3 = new IntegerTerm(4);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("t", 2);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("c");
    static IntegerTerm si6 = new IntegerTerm(120);
    static Term[] s7 = {s5, si6};
    static StructureTerm s8 = new StructureTerm(s4, s7);
    static IntegerTerm si9 = new IntegerTerm(60);
    static Term[] s10 = {s5, si9};
    static StructureTerm s11 = new StructureTerm(s4, s10);
    static SymbolTerm s12 = SymbolTerm.makeSymbol("[]");
    static ListTerm s13 = new ListTerm(s11, s12);
    static ListTerm s14 = new ListTerm(s8, s13);
    static Term[] s15 = {si3, si3, s14};
    static StructureTerm s16 = new StructureTerm(s2, s15);
    static IntegerTerm si17 = new IntegerTerm(5);
    static ListTerm s18 = new ListTerm(s11, s13);
    static Term[] s19 = {si17, si17, s18};
    static StructureTerm s20 = new StructureTerm(s2, s19);
    static ListTerm s21 = new ListTerm(s20, s12);
    static ListTerm s22 = new ListTerm(s16, s21);
    static DoubleTerm sf23 = new DoubleTerm(0.03);
    static IntegerTerm si24 = new IntegerTerm(480);
    static IntegerTerm si25 = new IntegerTerm(-6);
    static Term[] s26 = {si25, si25, s14};
    static StructureTerm s27 = new StructureTerm(s2, s26);
    static ListTerm s28 = new ListTerm(s27, s12);
    static ListTerm s29 = new ListTerm(s20, s28);
    static ListTerm s30 = new ListTerm(s16, s29);
    static DoubleTerm sf31 = new DoubleTerm(0.03125);
    static IntegerTerm si32 = new IntegerTerm(8);
    static Term[] s33 = {si32, si32, s18};
    static StructureTerm s34 = new StructureTerm(s2, s33);
    static ListTerm s35 = new ListTerm(s34, s12);
    static ListTerm s36 = new ListTerm(s16, s35);
    static DoubleTerm sf37 = new DoubleTerm(0.04);
    static IntegerTerm si38 = new IntegerTerm(1);
    static IntegerTerm si39 = new IntegerTerm(2);
    static Term[] s40 = {si38, si39, s14};
    static StructureTerm s41 = new StructureTerm(s2, s40);
    static IntegerTerm si42 = new IntegerTerm(-1);
    static IntegerTerm si43 = new IntegerTerm(-2);
    static Term[] s44 = {si42, si43, s18};
    static StructureTerm s45 = new StructureTerm(s2, s44);
    static ListTerm s46 = new ListTerm(s45, s12);
    static ListTerm s47 = new ListTerm(s41, s46);
    static DoubleTerm sf48 = new DoubleTerm(0.0);

    public PRED_test_leap_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_leap_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_leap";
    }

    public Predicate exec(Prolog engine) {
    // test_leap:-leap_size(300,[slope(4,4,[t(c,120),t(c,60)]),slope(5,5,[t(c,60),t(c,60)])],0.03),leap_size(480,[slope(4,4,[t(c,120),t(c,60)]),slope(5,5,[t(c,60),t(c,60)]),slope(-6,-6,[t(c,120),t(c,60)])],0.03125),leap_size(300,[slope(4,4,[t(c,120),t(c,60)]),slope(8,8,[t(c,60),t(c,60)])],0.04),leap_size(300,[slope(1,2,[t(c,120),t(c,60)]),slope(-1,-2,[t(c,60),t(c,60)])],0.0)
        engine.setB0();
        Predicate p1, p2, p3;
    // test_leap:-[leap_size(300,[slope(4,4,[t(c,120),t(c,60)]),slope(5,5,[t(c,60),t(c,60)])],0.03),leap_size(480,[slope(4,4,[t(c,120),t(c,60)]),slope(5,5,[t(c,60),t(c,60)]),slope(-6,-6,[t(c,120),t(c,60)])],0.03125),leap_size(300,[slope(4,4,[t(c,120),t(c,60)]),slope(8,8,[t(c,60),t(c,60)])],0.04),leap_size(300,[slope(1,2,[t(c,120),t(c,60)]),slope(-1,-2,[t(c,60),t(c,60)])],0.0)]
        p1 = new PRED_leap_size_3(si1, s47, sf48, cont);
        p2 = new PRED_leap_size_3(si1, s36, sf37, p1);
        p3 = new PRED_leap_size_3(si24, s30, sf31, p2);
        return new PRED_leap_size_3(si1, s22, sf23, p3);
    }
}
