package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>symbol_duration/2</code> defined in rule_database.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_symbol_duration_2 extends Predicate {

    public Term arg1, arg2;

    public PRED_symbol_duration_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_symbol_duration_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "symbol_duration(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // symbol_duration(A,B):-'$dummy_1_rule_database.pro'(C,D,B,A)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
        a2 = arg2;
    // symbol_duration(A,B):-['$dummy_1_rule_database.pro'(C,D,B,A)]
        return new PRED_$dummy_1_rule_database$002Epro_4(new VariableTerm(engine), new VariableTerm(engine), a2, a1, cont);
    }
}
