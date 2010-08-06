import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_syncopation_lh/0</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_syncopation_lh_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(0);
    static IntegerTerm si2 = new IntegerTerm(480);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("t", 2);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("c");
    static IntegerTerm si5 = new IntegerTerm(120);
    static Term[] s6 = {s4, si5};
    static StructureTerm s7 = new StructureTerm(s3, s6);
    static IntegerTerm si8 = new IntegerTerm(240);
    static Term[] s9 = {s4, si8};
    static StructureTerm s10 = new StructureTerm(s3, s9);
    static SymbolTerm s11 = SymbolTerm.makeSymbol("[]");
    static ListTerm s12 = new ListTerm(s7, s11);
    static ListTerm s13 = new ListTerm(s10, s12);
    static ListTerm s14 = new ListTerm(s7, s13);
    static IntegerTerm si15 = new IntegerTerm(1);
    static SymbolTerm s16 = SymbolTerm.makeSymbol("r");
    static Term[] s17 = {s16, si8};
    static StructureTerm s18 = new StructureTerm(s3, s17);
    static ListTerm s19 = new ListTerm(s18, s12);
    static ListTerm s20 = new ListTerm(s7, s19);
    static Term[] s21 = {s16, si5};
    static StructureTerm s22 = new StructureTerm(s3, s21);
    static ListTerm s23 = new ListTerm(s7, s12);
    static ListTerm s24 = new ListTerm(s22, s23);
    static ListTerm s25 = new ListTerm(s7, s23);
    static ListTerm s26 = new ListTerm(s22, s25);
    static IntegerTerm si27 = new IntegerTerm(60);
    static Term[] s28 = {s4, si27};
    static StructureTerm s29 = new StructureTerm(s3, s28);
    static ListTerm s30 = new ListTerm(s29, s11);
    static ListTerm s31 = new ListTerm(s7, s30);
    static ListTerm s32 = new ListTerm(s29, s31);
    static ListTerm s33 = new ListTerm(s29, s32);
    static ListTerm s34 = new ListTerm(s7, s33);
    static ListTerm s35 = new ListTerm(s29, s34);
    static IntegerTerm si36 = new IntegerTerm(2);
    static Term[] s37 = {s4, si2};
    static StructureTerm s38 = new StructureTerm(s3, s37);
    static ListTerm s39 = new ListTerm(s38, s11);
    static ListTerm s40 = new ListTerm(s10, s39);
    static ListTerm s41 = new ListTerm(s7, s40);
    static ListTerm s42 = new ListTerm(s38, s12);
    static ListTerm s43 = new ListTerm(s10, s42);
    static ListTerm s44 = new ListTerm(s7, s43);
    static IntegerTerm si45 = new IntegerTerm(360);
    static ListTerm s46 = new ListTerm(s22, s12);
    static ListTerm s47 = new ListTerm(s38, s39);
    static ListTerm s48 = new ListTerm(s38, s47);

    public PRED_test_syncopation_lh_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_syncopation_lh_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_syncopation_lh";
    }

    public Predicate exec(Prolog engine) {
    // test_syncopation_lh:-syncopation_lh(0,480,[t(c,120),t(c,240),t(c,120)],1),syncopation_lh(0,480,[t(c,120),t(r,240),t(c,120)],0),syncopation_lh(120,480,[t(r,120),t(c,120),t(c,120)],0),syncopation_lh(0,480,[t(r,120),t(c,120),t(c,120),t(c,120)],0),syncopation_lh(0,480,[t(c,60),t(c,120),t(c,60),t(c,60),t(c,120),t(c,60)],2),syncopation_lh(0,480,[t(c,120),t(c,240),t(c,480)],1),syncopation_lh(0,480,[t(c,120),t(c,240),t(c,480),t(c,120)],1),syncopation_lh(120,480,[t(c,120)],1),syncopation_lh(360,480,[t(r,120),t(c,120)],0),syncopation_lh(0,480,[t(c,480),t(c,480),t(c,480)],0)
        engine.setB0();
        Predicate p1, p2, p3, p4, p5, p6, p7, p8, p9;
    // test_syncopation_lh:-[syncopation_lh(0,480,[t(c,120),t(c,240),t(c,120)],1),syncopation_lh(0,480,[t(c,120),t(r,240),t(c,120)],0),syncopation_lh(120,480,[t(r,120),t(c,120),t(c,120)],0),syncopation_lh(0,480,[t(r,120),t(c,120),t(c,120),t(c,120)],0),syncopation_lh(0,480,[t(c,60),t(c,120),t(c,60),t(c,60),t(c,120),t(c,60)],2),syncopation_lh(0,480,[t(c,120),t(c,240),t(c,480)],1),syncopation_lh(0,480,[t(c,120),t(c,240),t(c,480),t(c,120)],1),syncopation_lh(120,480,[t(c,120)],1),syncopation_lh(360,480,[t(r,120),t(c,120)],0),syncopation_lh(0,480,[t(c,480),t(c,480),t(c,480)],0)]
        p1 = new PRED_syncopation_lh_4(si1, si2, s48, si1, cont);
        p2 = new PRED_syncopation_lh_4(si45, si2, s46, si1, p1);
        p3 = new PRED_syncopation_lh_4(si5, si2, s12, si15, p2);
        p4 = new PRED_syncopation_lh_4(si1, si2, s44, si15, p3);
        p5 = new PRED_syncopation_lh_4(si1, si2, s41, si15, p4);
        p6 = new PRED_syncopation_lh_4(si1, si2, s35, si36, p5);
        p7 = new PRED_syncopation_lh_4(si1, si2, s26, si1, p6);
        p8 = new PRED_syncopation_lh_4(si5, si2, s24, si1, p7);
        p9 = new PRED_syncopation_lh_4(si1, si2, s20, si1, p8);
        return new PRED_syncopation_lh_4(si1, si2, s14, si15, p9);
    }
}
