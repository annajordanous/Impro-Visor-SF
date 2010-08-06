package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>pick_random/3</code> defined in rule_expander.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_pick_random_3 extends Predicate {

    public Term arg1, arg2, arg3;

    public PRED_pick_random_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_pick_random_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "pick_random(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
    // pick_random(A,B,C):-normalize(A,D),rand_float(E),pick_random_normalized(D,B,E,F),'$dummy_1_rule_expander.pro'(F,A,B,G,C,H)
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6;
        Predicate p1, p2, p3;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    // pick_random(A,B,C):-[normalize(A,D),rand_float(E),pick_random_normalized(D,B,E,F),'$dummy_1_rule_expander.pro'(F,A,B,G,C,H)]
        a4 = new VariableTerm(engine);
        a5 = new VariableTerm(engine);
        a6 = new VariableTerm(engine);
        p1 = new PRED_$dummy_1_rule_expander$002Epro_6(a6, a1, a2, new VariableTerm(engine), a3, new VariableTerm(engine), cont);
        p2 = new PRED_pick_random_normalized_4(a4, a2, a5, a6, p1);
        p3 = new PRED_rand_float_1(a5, p2);
        return new PRED_normalize_2(a1, a4, p3);
    }
}
