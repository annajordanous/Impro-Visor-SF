package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_1_rule_expander.pro'/6</code> defined in rule_expander.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_1_rule_expander$002Epro_6 extends Predicate {
    static Predicate _$dummy_1_rule_expander$002Epro_6_sub_1 = new PRED_$dummy_1_rule_expander$002Epro_6_sub_1();
    static Predicate _$dummy_1_rule_expander$002Epro_6_1 = new PRED_$dummy_1_rule_expander$002Epro_6_1();
    static Predicate _$dummy_1_rule_expander$002Epro_6_2 = new PRED_$dummy_1_rule_expander$002Epro_6_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6;

    public PRED_$dummy_1_rule_expander$002Epro_6(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        arg6 = a6;
        this.cont = cont;
    }

    public PRED_$dummy_1_rule_expander$002Epro_6(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        arg6 = args[5];
        this.cont = cont;
    }

    public int arity() { return 6; }

    public String toString() {
        return "$dummy_1_rule_expander.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.aregs[5] = arg5;
        engine.aregs[6] = arg6;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_1_rule_expander$002Epro_6_1, _$dummy_1_rule_expander$002Epro_6_sub_1);
    }
}

class PRED_$dummy_1_rule_expander$002Epro_6_sub_1 extends PRED_$dummy_1_rule_expander$002Epro_6 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_1_rule_expander$002Epro_6_2);
    }
}

class PRED_$dummy_1_rule_expander$002Epro_6_1 extends PRED_$dummy_1_rule_expander$002Epro_6 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_rule_expander.pro'(A,B,C,D,E,F):-E=A
        Term a1, a2, a3, a4, a5, a6;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        cont = engine.cont;
    // '$dummy_1_rule_expander.pro'(A,B,C,D,E,F):-['$unify'(E,A)]
        //START inline expansion of $unify(a(5),a(1))
        if (! a5.unify(a1, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}

class PRED_$dummy_1_rule_expander$002Epro_6_2 extends PRED_$dummy_1_rule_expander$002Epro_6 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_rule_expander.pro'(A,B,C,D,E,F):-remove(A,C,B,D,F),pick_random(F,D,E)
        Term a1, a2, a3, a4, a5, a6;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        cont = engine.cont;
    // '$dummy_1_rule_expander.pro'(A,B,C,D,E,F):-[remove(A,C,B,D,F),pick_random(F,D,E)]
        p1 = new PRED_pick_random_3(a6, a4, a5, cont);
        return new PRED_remove_5(a1, a3, a2, a4, a6, p1);
    }
}
