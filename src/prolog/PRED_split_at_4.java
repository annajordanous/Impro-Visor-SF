package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>split_at/4</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_split_at_4 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(0);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("[]");
    static IntegerTerm si3 = new IntegerTerm(1);
    static Predicate _split_at_4_top = new PRED_split_at_4_top();
    static Predicate _split_at_4_var = new PRED_split_at_4_var();
    static Predicate _split_at_4_var_1 = new PRED_split_at_4_var_1();
    static Predicate _split_at_4_var_2 = new PRED_split_at_4_var_2();
    static Predicate _split_at_4_flo = new PRED_split_at_4_flo();
    static Predicate _split_at_4_flo_1 = new PRED_split_at_4_flo_1();
    static Predicate _split_at_4_1 = new PRED_split_at_4_1();
    static Predicate _split_at_4_2 = new PRED_split_at_4_2();
    static Predicate _split_at_4_3 = new PRED_split_at_4_3();

    public Term arg1, arg2, arg3, arg4;

    public PRED_split_at_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_split_at_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "split_at(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.cont = cont;
        return _split_at_4_top;
    }
}

class PRED_split_at_4_top extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
        engine.setB0();
        return engine.switch_on_term(_split_at_4_var, _split_at_4_var, _split_at_4_flo, _split_at_4_flo, _split_at_4_flo, _split_at_4_flo);
    }
}

class PRED_split_at_4_var extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_split_at_4_1, _split_at_4_var_1);
    }
}

class PRED_split_at_4_var_1 extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_split_at_4_2, _split_at_4_var_2);
    }
}

class PRED_split_at_4_var_2 extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_split_at_4_3);
    }
}

class PRED_split_at_4_flo extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_split_at_4_2, _split_at_4_flo_1);
    }
}

class PRED_split_at_4_flo_1 extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_split_at_4_3);
    }
}

class PRED_split_at_4_1 extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
    // split_at(0,A,[],A):-!
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // split_at(0,A,[],A):-['$neck_cut']
        a1 = a1.dereference();
        if (a1.isInteger()){
            if (((IntegerTerm) a1).intValue() != 0)
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(si1, engine.trail);
        } else {
            return engine.fail();
        }
        a3 = a3.dereference();
        if (a3.isSymbol()){
            if (! a3.equals(s2))
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(s2, engine.trail);
        } else {
            return engine.fail();
        }
        if (! a2.unify(a4, engine.trail))
            return engine.fail();
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_split_at_4_2 extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
    // split_at(A,[],[],[]):-!
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // split_at(A,[],[],[]):-['$neck_cut']
        a2 = a2.dereference();
        if (a2.isSymbol()){
            if (! a2.equals(s2))
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(s2, engine.trail);
        } else {
            return engine.fail();
        }
        a3 = a3.dereference();
        if (a3.isSymbol()){
            if (! a3.equals(s2))
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(s2, engine.trail);
        } else {
            return engine.fail();
        }
        a4 = a4.dereference();
        if (a4.isSymbol()){
            if (! a4.equals(s2))
                return engine.fail();
        } else if (a4.isVariable()){
            ((VariableTerm) a4).bind(s2, engine.trail);
        } else {
            return engine.fail();
        }
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_split_at_4_3 extends PRED_split_at_4 {
    public Predicate exec(Prolog engine) {
    // split_at(A,[B|C],[B|D],E):-F is A-1,split_at(F,C,D,E)
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // split_at(A,[B|C],[B|D],E):-['$minus'(A,1,F),split_at(F,C,D,E)]
        a2 = a2.dereference();
        if (a2.isList()){
            Term[] args = {((ListTerm)a2).car(), ((ListTerm)a2).cdr()};
            a5 = args[0];
            a6 = args[1];
        } else if (a2.isVariable()){
            a5 = new VariableTerm(engine);
            a6 = new VariableTerm(engine);
            ((VariableTerm) a2).bind(new ListTerm(a5, a6), engine.trail);
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
        a8 = new VariableTerm(engine);
        //START inline expansion of $minus(a(1),si(3),a(8))
        try {
            if (! a8.unify(Arithmetic.evaluate(a1).subtract(si3), engine.trail)) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        engine.aregs[1] = a8;
        engine.aregs[2] = a6;
        engine.aregs[3] = a7;
        engine.aregs[4] = a4;
        engine.cont = cont;
        return _split_at_4_top;
    }
}
