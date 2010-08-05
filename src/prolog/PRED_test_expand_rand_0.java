package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_expand_rand/0</code> defined in rule_expander.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_expand_rand_0 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("position");
    static IntegerTerm si2 = new IntegerTerm(0);

    public PRED_test_expand_rand_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_expand_rand_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_expand_rand";
    }

    public Predicate exec(Prolog engine) {
    // test_expand_rand:-flag(position,A,0)
        engine.setB0();
    // test_expand_rand:-[flag(position,A,0)]
        return new PRED_flag_3(s1, new VariableTerm(engine), si2, cont);
    }
}
