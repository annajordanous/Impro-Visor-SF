package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_1_random_helpers.pro'/1</code> defined in random_helpers.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_1_random_helpers$002Epro_1 extends Predicate {
    static Predicate _$dummy_1_random_helpers$002Epro_1_sub_1 = new PRED_$dummy_1_random_helpers$002Epro_1_sub_1();
    static Predicate _$dummy_1_random_helpers$002Epro_1_1 = new PRED_$dummy_1_random_helpers$002Epro_1_1();
    static Predicate _$dummy_1_random_helpers$002Epro_1_2 = new PRED_$dummy_1_random_helpers$002Epro_1_2();

    public Term arg1;

    public PRED_$dummy_1_random_helpers$002Epro_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_$dummy_1_random_helpers$002Epro_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "$dummy_1_random_helpers.pro(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_1_random_helpers$002Epro_1_1, _$dummy_1_random_helpers$002Epro_1_sub_1);
    }
}

class PRED_$dummy_1_random_helpers$002Epro_1_sub_1 extends PRED_$dummy_1_random_helpers$002Epro_1 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_1_random_helpers$002Epro_1_2);
    }
}

class PRED_$dummy_1_random_helpers$002Epro_1_1 extends PRED_$dummy_1_random_helpers$002Epro_1 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_random_helpers.pro'(A):-compound(A),!,fail
        Term a1, a2;
        Predicate p1, p2;
        Predicate cont;
        a1 = engine.aregs[1];
        cont = engine.cont;
    // '$dummy_1_random_helpers.pro'(A):-['$get_level'(B),compound(A),'$cut'(B),fail]
        a2 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(2))
        if (! a2.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        p1 = new PRED_fail_0(cont);
        p2 = new PRED_$cut_1(a2, p1);
        return new PRED_compound_1(a1, p2);
    }
}

class PRED_$dummy_1_random_helpers$002Epro_1_2 extends PRED_$dummy_1_random_helpers$002Epro_1 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_random_helpers.pro'(A):-true
        Term a1;
        Predicate cont;
        a1 = engine.aregs[1];
        cont = engine.cont;
    // '$dummy_1_random_helpers.pro'(A):-[]
        return cont;
    }
}
