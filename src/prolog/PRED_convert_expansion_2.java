package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>convert_expansion/2</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_convert_expansion_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _fail_0 = new PRED_fail_0();
    static Predicate _convert_expansion_2_var = new PRED_convert_expansion_2_var();
    static Predicate _convert_expansion_2_var_1 = new PRED_convert_expansion_2_var_1();
    static Predicate _convert_expansion_2_1 = new PRED_convert_expansion_2_1();
    static Predicate _convert_expansion_2_2 = new PRED_convert_expansion_2_2();

    public Term arg1, arg2;

    public PRED_convert_expansion_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_convert_expansion_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "convert_expansion(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_convert_expansion_2_var, _fail_0, _fail_0, _convert_expansion_2_1, _fail_0, _convert_expansion_2_2);
    }
}

class PRED_convert_expansion_2_var extends PRED_convert_expansion_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_convert_expansion_2_1, _convert_expansion_2_var_1);
    }
}

class PRED_convert_expansion_2_var_1 extends PRED_convert_expansion_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_convert_expansion_2_2);
    }
}

class PRED_convert_expansion_2_1 extends PRED_convert_expansion_2 {
    public Predicate exec(Prolog engine) {
    // convert_expansion([],[]):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // convert_expansion([],[]):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s1))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isSymbol()){
            if (! a2.equals(s1))
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_convert_expansion_2_2 extends PRED_convert_expansion_2 {
    public Predicate exec(Prolog engine) {
    // convert_expansion([A|B],C):-terminal_or_slope_or_nonterminal(A,D),C=[D|E],convert_expansion(B,E)
        Term a1, a2, a3, a4, a5, a6, a7;
        Predicate p1, p2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // convert_expansion([A|B],C):-[terminal_or_slope_or_nonterminal(A,D),'$unify'(C,[D|E]),convert_expansion(B,E)]
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a3 = args[0];
            a4 = args[1];
        } else if (a1.isVariable()){
            a3 = new VariableTerm(engine);
            a4 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a3, a4), engine.trail);
        } else {
            return engine.fail();
        }
        a5 = new VariableTerm(engine);
        a6 = new VariableTerm(engine);
        a7 = new ListTerm(a5, a6);
        p1 = new PRED_convert_expansion_2(a4, a6, cont);
        p2 = new PRED_$unify_2(a2, a7, p1);
        return new PRED_terminal_or_slope_or_nonterminal_2(a3, a5, p2);
    }
}
