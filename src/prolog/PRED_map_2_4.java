package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>map_2/4</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_map_2_4 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _map_2_4_var = new PRED_map_2_4_var();
    static Predicate _map_2_4_var_1 = new PRED_map_2_4_var_1();
    static Predicate _map_2_4_1 = new PRED_map_2_4_1();
    static Predicate _map_2_4_2 = new PRED_map_2_4_2();

    public Term arg1, arg2, arg3, arg4;

    public PRED_map_2_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_map_2_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "map_2(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_map_2_4_var, _map_2_4_1, _map_2_4_1, _map_2_4_1, _map_2_4_1, _map_2_4_var);
    }
}

class PRED_map_2_4_var extends PRED_map_2_4 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_map_2_4_1, _map_2_4_var_1);
    }
}

class PRED_map_2_4_var_1 extends PRED_map_2_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_map_2_4_2);
    }
}

class PRED_map_2_4_1 extends PRED_map_2_4 {
    public Predicate exec(Prolog engine) {
    // map_2(A,B,C,[]):-'$dummy_1_functional.pro'(B,A),!
        Term a1, a2, a3, a4, a5;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // map_2(A,B,C,[]):-['$get_level'(D),'$dummy_1_functional.pro'(B,A),'$cut'(D)]
        a4 = a4.dereference();
        if (a4.isSymbol()){
            if (! a4.equals(s1))
                return engine.fail();
        } else if (a4.isVariable()){
            ((VariableTerm) a4).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        a5 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(5))
        if (! a5.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        p1 = new PRED_$cut_1(a5, cont);
        return new PRED_$dummy_1_functional$002Epro_2(a2, a1, p1);
    }
}

class PRED_map_2_4_2 extends PRED_map_2_4 {
    public Predicate exec(Prolog engine) {
    // map_2([A|B],[C|D],E,[F|G]):-call(E,A,C,F),map_2(B,D,E,G)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // map_2([A|B],[C|D],E,[F|G]):-[call(E,A,C,F),map_2(B,D,E,G)]
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a5 = args[0];
            a6 = args[1];
        } else if (a1.isVariable()){
            a5 = new VariableTerm(engine);
            a6 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a5, a6), engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isList()){
            Term[] args = {((ListTerm)a2).car(), ((ListTerm)a2).cdr()};
            a7 = args[0];
            a8 = args[1];
        } else if (a2.isVariable()){
            a7 = new VariableTerm(engine);
            a8 = new VariableTerm(engine);
            ((VariableTerm) a2).bind(new ListTerm(a7, a8), engine.trail);
        } else {
            return engine.fail();
        }
        a4 = a4.dereference();
        if (a4.isList()){
            Term[] args = {((ListTerm)a4).car(), ((ListTerm)a4).cdr()};
            a9 = args[0];
            a10 = args[1];
        } else if (a4.isVariable()){
            a9 = new VariableTerm(engine);
            a10 = new VariableTerm(engine);
            ((VariableTerm) a4).bind(new ListTerm(a9, a10), engine.trail);
        } else {
            return engine.fail();
        }
        p1 = new PRED_map_2_4(a6, a8, a3, a10, cont);
        return new PRED_call_4(a3, a5, a7, a9, p1);
    }
}
