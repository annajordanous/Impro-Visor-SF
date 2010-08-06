package prolog;

import prolog.PRED_smallest_not_zero_2;
import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>get_min_note/2</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_get_min_note_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("functor_second");

    public Term arg1, arg2;

    public PRED_get_min_note_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_get_min_note_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "get_min_note(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // get_min_note(A,B):-map_fast(A,functor_second,C),smallest_not_zero(C,B)
        engine.setB0();
        Term a1, a2, a3;
        Predicate p1;
        a1 = arg1;
        a2 = arg2;
    // get_min_note(A,B):-[map_fast(A,functor_second,C),smallest_not_zero(C,B)]
        a3 = new VariableTerm(engine);
        p1 = new PRED_smallest_not_zero_2(a3, a2, cont);
        return new PRED_map_fast_3(a1, s1, a3, p1);
    }
}
