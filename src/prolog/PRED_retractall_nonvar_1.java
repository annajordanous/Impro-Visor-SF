package prolog;


import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>retractall_nonvar/1</code> defined in rule_database.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_retractall_nonvar_1 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("user");
    static Predicate _retractall_nonvar_1_sub_1 = new PRED_retractall_nonvar_1_sub_1();
    static Predicate _retractall_nonvar_1_1 = new PRED_retractall_nonvar_1_1();
    static Predicate _retractall_nonvar_1_2 = new PRED_retractall_nonvar_1_2();

    public Term arg1;

    public PRED_retractall_nonvar_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_retractall_nonvar_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "retractall_nonvar(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_retractall_nonvar_1_1, _retractall_nonvar_1_sub_1);
    }
}

class PRED_retractall_nonvar_1_sub_1 extends PRED_retractall_nonvar_1 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_retractall_nonvar_1_2);
    }
}

class PRED_retractall_nonvar_1_1 extends PRED_retractall_nonvar_1 {
    public Predicate exec(Prolog engine) {
    // retractall_nonvar(A):-A,A=..[B|C],'$dummy_0_rule_database.pro'(C,D),retract(A),fail
        Term a1, a2, a3, a4, a5;
        Predicate p1, p2, p3, p4;
        Predicate cont;
        a1 = engine.aregs[1];
        cont = engine.cont;
    // retractall_nonvar(A):-[call(user:A),'$univ'(A,[B|C]),'$dummy_0_rule_database.pro'(C,D),retract(user:A),fail]
        Term[] y1 = {s2, a1};
        a2 = new StructureTerm(s1, y1);
        a3 = new VariableTerm(engine);
        a4 = new ListTerm(new VariableTerm(engine), a3);
        Term[] y2 = {s2, a1};
        a5 = new StructureTerm(s1, y2);
        p1 = new PRED_fail_0(cont);
        p2 = new PRED_retract_1(a5, p1);
        p3 = new PRED_$dummy_0_rule_database$002Epro_2(a3, new VariableTerm(engine), p2);
        p4 = new PRED_$univ_2(a1, a4, p3);
        return new PRED_call_1(a2, p4);
    }
}

class PRED_retractall_nonvar_1_2 extends PRED_retractall_nonvar_1 {
    public Predicate exec(Prolog engine) {
    // retractall_nonvar(A):-true
        Term a1;
        Predicate cont;
        a1 = engine.aregs[1];
        cont = engine.cont;
    // retractall_nonvar(A):-[]
        return cont;
    }
}
