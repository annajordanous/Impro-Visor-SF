package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>matrix_transpose/2</code> defined in transpose.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_matrix_transpose_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _matrix_transpose_2_var = new PRED_matrix_transpose_2_var();
    static Predicate _matrix_transpose_2_var_1 = new PRED_matrix_transpose_2_var_1();
    static Predicate _matrix_transpose_2_var_2 = new PRED_matrix_transpose_2_var_2();
    static Predicate _matrix_transpose_2_con = new PRED_matrix_transpose_2_con();
    static Predicate _matrix_transpose_2_con_1 = new PRED_matrix_transpose_2_con_1();
    static Predicate _matrix_transpose_2_lis = new PRED_matrix_transpose_2_lis();
    static Predicate _matrix_transpose_2_lis_1 = new PRED_matrix_transpose_2_lis_1();
    static Predicate _matrix_transpose_2_1 = new PRED_matrix_transpose_2_1();
    static Predicate _matrix_transpose_2_2 = new PRED_matrix_transpose_2_2();
    static Predicate _matrix_transpose_2_3 = new PRED_matrix_transpose_2_3();

    public Term arg1, arg2;

    public PRED_matrix_transpose_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_matrix_transpose_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "matrix_transpose(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_matrix_transpose_2_var, _matrix_transpose_2_3, _matrix_transpose_2_3, _matrix_transpose_2_con, _matrix_transpose_2_3, _matrix_transpose_2_lis);
    }
}

class PRED_matrix_transpose_2_var extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_matrix_transpose_2_1, _matrix_transpose_2_var_1);
    }
}

class PRED_matrix_transpose_2_var_1 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_matrix_transpose_2_2, _matrix_transpose_2_var_2);
    }
}

class PRED_matrix_transpose_2_var_2 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_matrix_transpose_2_3);
    }
}

class PRED_matrix_transpose_2_con extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_matrix_transpose_2_1, _matrix_transpose_2_con_1);
    }
}

class PRED_matrix_transpose_2_con_1 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_matrix_transpose_2_3);
    }
}

class PRED_matrix_transpose_2_lis extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_matrix_transpose_2_2, _matrix_transpose_2_lis_1);
    }
}

class PRED_matrix_transpose_2_lis_1 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_matrix_transpose_2_3);
    }
}

class PRED_matrix_transpose_2_1 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
    // matrix_transpose([],[]):-!
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // matrix_transpose([],[]):-['$neck_cut']
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
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_matrix_transpose_2_2 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
    // matrix_transpose([[]|A],[]):-!
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // matrix_transpose([[]|A],[]):-['$neck_cut']
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            if (! s1.unify(args[0], engine.trail))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(new ListTerm(s1, new VariableTerm(engine)), engine.trail);
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
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_matrix_transpose_2_3 extends PRED_matrix_transpose_2 {
    public Predicate exec(Prolog engine) {
    // matrix_transpose(A,[B|C]):-A=[[D|E]|F],map_first(A,B),map_rest(A,G),matrix_transpose(G,C)
        Term a1, a2, a3, a4, a5, a6, a7;
        Predicate p1, p2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // matrix_transpose(A,[B|C]):-['$unify'(A,[[D|E]|F]),map_first(A,B),map_rest(A,G),matrix_transpose(G,C)]
        a2 = a2.dereference();
        if (a2.isList()){
            Term[] args = {((ListTerm)a2).car(), ((ListTerm)a2).cdr()};
            a3 = args[0];
            a4 = args[1];
        } else if (a2.isVariable()){
            a3 = new VariableTerm(engine);
            a4 = new VariableTerm(engine);
            ((VariableTerm) a2).bind(new ListTerm(a3, a4), engine.trail);
        } else {
            return engine.fail();
        }
        a5 = new ListTerm(new VariableTerm(engine), new VariableTerm(engine));
        a6 = new ListTerm(a5, new VariableTerm(engine));
        //START inline expansion of $unify(a(1),a(6))
        if (! a1.unify(a6, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a7 = new VariableTerm(engine);
        p1 = new PRED_matrix_transpose_2(a7, a4, cont);
        p2 = new PRED_map_rest_2(a1, a7, p1);
        return new PRED_map_first_2(a1, a3, p2);
    }
}
