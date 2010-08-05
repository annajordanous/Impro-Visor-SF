package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>attrs_weighted_avg/3</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_attrs_weighted_avg_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static ListTerm s2 = new ListTerm(s1, s1);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("weighted_avg");
    static Predicate _attrs_weighted_avg_3_var = new PRED_attrs_weighted_avg_3_var();
    static Predicate _attrs_weighted_avg_3_var_1 = new PRED_attrs_weighted_avg_3_var_1();
    static Predicate _attrs_weighted_avg_3_1 = new PRED_attrs_weighted_avg_3_1();
    static Predicate _attrs_weighted_avg_3_2 = new PRED_attrs_weighted_avg_3_2();

    public Term arg1, arg2, arg3;

    public PRED_attrs_weighted_avg_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_attrs_weighted_avg_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "attrs_weighted_avg(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_attrs_weighted_avg_3_var, _attrs_weighted_avg_3_2, _attrs_weighted_avg_3_2, _attrs_weighted_avg_3_2, _attrs_weighted_avg_3_2, _attrs_weighted_avg_3_var);
    }
}

class PRED_attrs_weighted_avg_3_var extends PRED_attrs_weighted_avg_3 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_attrs_weighted_avg_3_1, _attrs_weighted_avg_3_var_1);
    }
}

class PRED_attrs_weighted_avg_3_var_1 extends PRED_attrs_weighted_avg_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_attrs_weighted_avg_3_2);
    }
}

class PRED_attrs_weighted_avg_3_1 extends PRED_attrs_weighted_avg_3 {
    public Predicate exec(Prolog engine) {
    // attrs_weighted_avg([[]],A,[]):-!
        Term a1, a2, a3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // attrs_weighted_avg([[]],A,[]):-['$neck_cut']
        if (! s2.unify(a1, engine.trail))
            return engine.fail();
        a3 = a3.dereference();
        if (a3.isSymbol()){
            if (! a3.equals(s1))
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_attrs_weighted_avg_3_2 extends PRED_attrs_weighted_avg_3 {
    public Predicate exec(Prolog engine) {
    // attrs_weighted_avg(A,B,C):-transpose_attrs_and_map(B,weighted_avg,A,C)
        Term a1, a2, a3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // attrs_weighted_avg(A,B,C):-[transpose_attrs_and_map(B,weighted_avg,A,C)]
        return new PRED_transpose_attrs_and_map_4(a2, s3, a1, a3, cont);
    }
}
