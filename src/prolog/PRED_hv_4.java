package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>hv/4</code> defined in plcafe_defs.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_hv_4 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _hv_4_top = new PRED_hv_4_top();
    static Predicate _hv_4_var = new PRED_hv_4_var();
    static Predicate _hv_4_var_1 = new PRED_hv_4_var_1();
    static Predicate _hv_4_var_2 = new PRED_hv_4_var_2();
    static Predicate _hv_4_int = new PRED_hv_4_int();
    static Predicate _hv_4_int_1 = new PRED_hv_4_int_1();
    static Predicate _hv_4_1 = new PRED_hv_4_1();
    static Predicate _hv_4_2 = new PRED_hv_4_2();
    static Predicate _hv_4_3 = new PRED_hv_4_3();

    public Term arg1, arg2, arg3, arg4;

    public PRED_hv_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_hv_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "hv(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.cont = cont;
        return _hv_4_top;
    }
}

class PRED_hv_4_top extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
        engine.setB0();
        return engine.switch_on_term(_hv_4_var, _hv_4_int, _hv_4_int, _hv_4_int, _hv_4_int, _hv_4_var);
    }
}

class PRED_hv_4_var extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_hv_4_1, _hv_4_var_1);
    }
}

class PRED_hv_4_var_1 extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_hv_4_2, _hv_4_var_2);
    }
}

class PRED_hv_4_var_2 extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_hv_4_3);
    }
}

class PRED_hv_4_int extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_hv_4_1, _hv_4_int_1);
    }
}

class PRED_hv_4_int_1 extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_hv_4_2);
    }
}

class PRED_hv_4_1 extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
    // hv(A,A,[],A):-true
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // hv(A,A,[],A):-[]
        if (! a1.unify(a2, engine.trail))
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
        if (! a1.unify(a4, engine.trail))
            return engine.fail();
        return cont;
    }
}

class PRED_hv_4_2 extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
    // hv(A,[B|A],[],A):-true
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // hv(A,[B|A],[],A):-[]
        a2 = a2.dereference();
        if (a2.isList()){
            Term[] args = {((ListTerm)a2).car(), ((ListTerm)a2).cdr()};
            if (! a1.unify(args[1], engine.trail))
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(new ListTerm(new VariableTerm(engine), a1), engine.trail);
        } else {
            return engine.fail();
        }
        a3 = a3.dereference();
        if (a3.isSymbol()){
            if (! a3.equals(s1))
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        if (! a1.unify(a4, engine.trail))
            return engine.fail();
        return cont;
    }
}

class PRED_hv_4_3 extends PRED_hv_4 {
    public Predicate exec(Prolog engine) {
    // hv([A|B],C,[A|D],E):-hv(B,[F|C],D,E)
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // hv([A|B],C,[A|D],E):-[hv(B,[F|C],D,E)]
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
        a3 = a3.dereference();
        if (a3.isList()){
            Term[] args = {((ListTerm)a3).car(), ((ListTerm)a3).cdr()};
            if (! a5.unify(args[0], engine.trail))
                return engine.fail();
            a7 = args[1];
        } else if (a3.isVariable()){
            a7 = new VariableTerm(engine);
            ((VariableTerm) a3).bind(new ListTerm(a5, a7), engine.trail);
        } else {
            return engine.fail();
        }
        a8 = new ListTerm(new VariableTerm(engine), a2);
        engine.aregs[1] = a6;
        engine.aregs[2] = a8;
        engine.aregs[3] = a7;
        engine.aregs[4] = a4;
        engine.cont = cont;
        return _hv_4_top;
    }
}
