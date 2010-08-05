package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>syncopation_pad/2</code> defined in syncopation.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_syncopation_pad_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("functor_second");
    static SymbolTerm s2 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("user");
    static SymbolTerm s4 = SymbolTerm.makeSymbol("measure_length", 1);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("[]");

    public Term arg1, arg2;

    public PRED_syncopation_pad_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_syncopation_pad_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "syncopation_pad(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // syncopation_pad(A,B):-squish_rests(A,C),map_fast(C,functor_second,D),sumlist(D,E),init(D,F),last(D,G),measure_length(H),I is(H-E mod H)mod H,J is G+I,append(F,[J],K),syncopation_measure(H,K,B)
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15, a16;
        Predicate p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11;
        a1 = arg1;
        a2 = arg2;
    // syncopation_pad(A,B):-[squish_rests(A,C),map_fast(C,functor_second,D),sumlist(D,E),init(D,F),last(D,G),call(user:measure_length(H)),'$mod'(E,H,I),'$minus'(H,I,J),'$mod'(J,H,K),'$plus'(G,K,L),append(F,[L],M),syncopation_measure(H,M,B)]
        a3 = new VariableTerm(engine);
        a4 = new VariableTerm(engine);
        a5 = new VariableTerm(engine);
        a6 = new VariableTerm(engine);
        a7 = new VariableTerm(engine);
        a8 = new VariableTerm(engine);
        Term[] y1 = {a8};
        a9 = new StructureTerm(s4, y1);
        Term[] y2 = {s3, a9};
        a10 = new StructureTerm(s2, y2);
        a11 = new VariableTerm(engine);
        a12 = new VariableTerm(engine);
        a13 = new VariableTerm(engine);
        a14 = new VariableTerm(engine);
        a15 = new ListTerm(a14, s5);
        a16 = new VariableTerm(engine);
        p1 = new PRED_syncopation_measure_3(a8, a16, a2, cont);
        p2 = new PRED_append_3(a6, a15, a16, p1);
        p3 = new PRED_$plus_3(a7, a13, a14, p2);
        p4 = new PRED_$mod_3(a12, a8, a13, p3);
        p5 = new PRED_$minus_3(a8, a11, a12, p4);
        p6 = new PRED_$mod_3(a5, a8, a11, p5);
        p7 = new PRED_call_1(a10, p6);
        p8 = new PRED_last_2(a4, a7, p7);
        p9 = new PRED_init_2(a4, a6, p8);
        p10 = new PRED_sumlist_2(a4, a5, p9);
        p11 = new PRED_map_fast_3(a3, s1, a4, p10);
        return new PRED_squish_rests_2(a1, a3, p11);
    }
}
