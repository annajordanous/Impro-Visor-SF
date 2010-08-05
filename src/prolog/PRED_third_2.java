package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>third/2</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_third_2 extends Predicate {

    public Term arg1, arg2;

    public PRED_third_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_third_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "third(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // third(A,B):-rest(A,C),rest(C,D),first(D,B)
        engine.setB0();
        Term a1, a2, a3, a4;
        Predicate p1, p2;
        a1 = arg1;
        a2 = arg2;
    // third(A,B):-[rest(A,C),rest(C,D),first(D,B)]
        a3 = new VariableTerm(engine);
        a4 = new VariableTerm(engine);
        p1 = new PRED_first_2(a4, a2, cont);
        p2 = new PRED_rest_2(a3, a4, p1);
        return new PRED_rest_2(a1, a3, p2);
    }
}
