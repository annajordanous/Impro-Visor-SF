import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_split_onsets/0</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_split_onsets_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(0);
    static IntegerTerm si2 = new IntegerTerm(120);
    static IntegerTerm si3 = new IntegerTerm(240);
    static IntegerTerm si4 = new IntegerTerm(360);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("[]");
    static ListTerm s6 = new ListTerm(si4, s5);
    static ListTerm s7 = new ListTerm(si3, s6);
    static ListTerm s8 = new ListTerm(si2, s7);
    static ListTerm s9 = new ListTerm(si1, s8);
    static ListTerm s10 = new ListTerm(si2, s5);
    static ListTerm s11 = new ListTerm(si1, s10);
    static ListTerm s12 = new ListTerm(s11, s5);
    static IntegerTerm si13 = new IntegerTerm(300);
    static IntegerTerm si14 = new IntegerTerm(500);
    static IntegerTerm si15 = new IntegerTerm(620);
    static ListTerm s16 = new ListTerm(si15, s5);
    static ListTerm s17 = new ListTerm(si14, s16);
    static ListTerm s18 = new ListTerm(si13, s17);
    static ListTerm s19 = new ListTerm(si3, s18);
    static IntegerTerm si20 = new IntegerTerm(480);
    static ListTerm s21 = new ListTerm(si13, s5);
    static ListTerm s22 = new ListTerm(si3, s21);
    static IntegerTerm si23 = new IntegerTerm(20);
    static IntegerTerm si24 = new IntegerTerm(140);
    static ListTerm s25 = new ListTerm(si24, s5);
    static ListTerm s26 = new ListTerm(si23, s25);
    static ListTerm s27 = new ListTerm(s26, s5);
    static IntegerTerm si28 = new IntegerTerm(840);
    static ListTerm s29 = new ListTerm(si28, s5);
    static ListTerm s30 = new ListTerm(si4, s29);
    static ListTerm s31 = new ListTerm(si2, s30);
    static ListTerm s32 = new ListTerm(si1, s31);
    static ListTerm s33 = new ListTerm(si2, s6);
    static ListTerm s34 = new ListTerm(si1, s33);
    static ListTerm s35 = new ListTerm(s6, s5);
    static ListTerm s36 = new ListTerm(si3, s5);
    static ListTerm s37 = new ListTerm(si2, s36);
    static ListTerm s38 = new ListTerm(si1, s37);
    static IntegerTerm si39 = new IntegerTerm(1);
    static IntegerTerm si40 = new IntegerTerm(3);
    static IntegerTerm si41 = new IntegerTerm(5);
    static ListTerm s42 = new ListTerm(si41, s5);
    static ListTerm s43 = new ListTerm(si40, s42);
    static ListTerm s44 = new ListTerm(si39, s43);
    static ListTerm s45 = new ListTerm(si1, s44);
    static IntegerTerm si46 = new IntegerTerm(2);
    static ListTerm s47 = new ListTerm(si39, s5);
    static ListTerm s48 = new ListTerm(si1, s47);
    static ListTerm s49 = new ListTerm(s47, s5);
    static ListTerm s50 = new ListTerm(s47, s49);

    public PRED_test_split_onsets_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_split_onsets_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_split_onsets";
    }

    public Predicate exec(Prolog engine) {
    // test_split_onsets:-split_onsets([0,120,240,360],240,[0,120],[[0,120]]),split_onsets([240,300,500,620],480,[240,300],[[20,140]]),split_onsets([0,120,360,840],480,[0,120,360],[[360]]),split_onsets([0,120,240],480,[0,120,240],[]),split_onsets([0,1,3,5],2,[0,1],[[1],[1]])
        engine.setB0();
        Predicate p1, p2, p3, p4;
    // test_split_onsets:-[split_onsets([0,120,240,360],240,[0,120],[[0,120]]),split_onsets([240,300,500,620],480,[240,300],[[20,140]]),split_onsets([0,120,360,840],480,[0,120,360],[[360]]),split_onsets([0,120,240],480,[0,120,240],[]),split_onsets([0,1,3,5],2,[0,1],[[1],[1]])]
        p1 = new PRED_split_onsets_4(s45, si46, s48, s50, cont);
        p2 = new PRED_split_onsets_4(s38, si20, s38, s5, p1);
        p3 = new PRED_split_onsets_4(s32, si20, s34, s35, p2);
        p4 = new PRED_split_onsets_4(s19, si20, s22, s27, p3);
        return new PRED_split_onsets_4(s9, si3, s11, s12, p4);
    }
}
