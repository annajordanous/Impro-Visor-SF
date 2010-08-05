package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>functor_first/2</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_functor_first_2 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);

    public Term arg1, arg2;

    public PRED_functor_first_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_functor_first_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "functor_first(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // functor_first(A,B):-arg(1,A,B)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
        a2 = arg2;
    // functor_first(A,B):-[arg(1,A,B)]
        return new PRED_arg_3(si1, a1, a2, cont);
    }
}
