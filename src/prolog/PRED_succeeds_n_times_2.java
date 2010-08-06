package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>succeeds_n_times/2</code> defined in plcafe_defs.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_succeeds_n_times_2 extends Predicate {

    public Term arg1, arg2;

    public PRED_succeeds_n_times_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_succeeds_n_times_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "succeeds_n_times(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // succeeds_n_times(A,B):-'$dummy_6_plcafe_defs.pro'(B,A,C,D,E,F)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
        a2 = arg2;
    // succeeds_n_times(A,B):-['$dummy_6_plcafe_defs.pro'(B,A,C,D,E,F)]
        return new PRED_$dummy_6_plcafe_defs$002Epro_6(a2, a1, new VariableTerm(engine), new VariableTerm(engine), new VariableTerm(engine), new VariableTerm(engine), cont);
    }
}
