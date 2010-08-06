import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>squish_rests/2</code> defined in squish_rests.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_squish_rests_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("t", 2);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("r");
    static Predicate _fail_0 = new PRED_fail_0();
    static Predicate _squish_rests_2_var = new PRED_squish_rests_2_var();
    static Predicate _squish_rests_2_var_1 = new PRED_squish_rests_2_var_1();
    static Predicate _squish_rests_2_var_2 = new PRED_squish_rests_2_var_2();
    static Predicate _squish_rests_2_var_3 = new PRED_squish_rests_2_var_3();
    static Predicate _squish_rests_2_lis = new PRED_squish_rests_2_lis();
    static Predicate _squish_rests_2_lis_1 = new PRED_squish_rests_2_lis_1();
    static Predicate _squish_rests_2_lis_2 = new PRED_squish_rests_2_lis_2();
    static Predicate _squish_rests_2_1 = new PRED_squish_rests_2_1();
    static Predicate _squish_rests_2_2 = new PRED_squish_rests_2_2();
    static Predicate _squish_rests_2_3 = new PRED_squish_rests_2_3();
    static Predicate _squish_rests_2_4 = new PRED_squish_rests_2_4();

    public Term arg1, arg2;

    public PRED_squish_rests_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_squish_rests_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "squish_rests(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_squish_rests_2_var, _fail_0, _fail_0, _squish_rests_2_1, _fail_0, _squish_rests_2_lis);
    }
}

class PRED_squish_rests_2_var extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_squish_rests_2_1, _squish_rests_2_var_1);
    }
}

class PRED_squish_rests_2_var_1 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_squish_rests_2_2, _squish_rests_2_var_2);
    }
}

class PRED_squish_rests_2_var_2 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_squish_rests_2_3, _squish_rests_2_var_3);
    }
}

class PRED_squish_rests_2_var_3 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_squish_rests_2_4);
    }
}

class PRED_squish_rests_2_lis extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_squish_rests_2_2, _squish_rests_2_lis_1);
    }
}

class PRED_squish_rests_2_lis_1 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_squish_rests_2_3, _squish_rests_2_lis_2);
    }
}

class PRED_squish_rests_2_lis_2 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_squish_rests_2_4);
    }
}

class PRED_squish_rests_2_1 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
    // squish_rests([],[]):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // squish_rests([],[]):-[]
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

class PRED_squish_rests_2_2 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
    // squish_rests([A],[A]):-!
        Term a1, a2, a3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // squish_rests([A],[A]):-['$neck_cut']
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a3 = args[0];
            if (! s1.unify(args[1], engine.trail))
                return engine.fail();
        } else if (a1.isVariable()){
            a3 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a3, s1), engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isList()){
            Term[] args = {((ListTerm)a2).car(), ((ListTerm)a2).cdr()};
            if (! a3.unify(args[0], engine.trail))
                return engine.fail();
            if (! s1.unify(args[1], engine.trail))
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(new ListTerm(a3, s1), engine.trail);
        } else {
            return engine.fail();
        }
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_squish_rests_2_3 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
    // squish_rests([A,B|C],D):-A=t(r,E),!,'$dummy_0_squish_rests.pro'(B,F,A,G,E,H,I,C,D)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // squish_rests([A,B|C],D):-['$get_level'(E),'$unify'(A,t(r,F)),'$cut'(E),'$dummy_0_squish_rests.pro'(B,G,A,H,F,I,J,C,D)]
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
        a4 = a4.dereference();
        if (a4.isList()){
            Term[] args = {((ListTerm)a4).car(), ((ListTerm)a4).cdr()};
            a5 = args[0];
            a6 = args[1];
        } else if (a4.isVariable()){
            a5 = new VariableTerm(engine);
            a6 = new VariableTerm(engine);
            ((VariableTerm) a4).bind(new ListTerm(a5, a6), engine.trail);
        } else {
            return engine.fail();
        }
        a7 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(7))
        if (! a7.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a8 = new VariableTerm(engine);
        Term[] y1 = {s3, a8};
        a9 = new StructureTerm(s2, y1);
        //START inline expansion of $unify(a(3),a(9))
        if (! a3.unify(a9, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $cut(a(7))
        a7 = a7.dereference();
        if (! a7.isInteger()) {
            throw new IllegalTypeException("integer", a7);
        } else {
            engine.cut(((IntegerTerm) a7).intValue());
        }
        //END inline expansion
        return new PRED_$dummy_0_squish_rests$002Epro_9(a5, new VariableTerm(engine), a3, new VariableTerm(engine), a8, new VariableTerm(engine), new VariableTerm(engine), a6, a2, cont);
    }
}

class PRED_squish_rests_2_4 extends PRED_squish_rests_2 {
    public Predicate exec(Prolog engine) {
    // squish_rests([A,B|C],D):-A\=t(r,E),squish_rests_helper([A,B|C],D)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // squish_rests([A,B|C],D):-['$not_unifiable'(A,t(r,E)),squish_rests_helper([A,B|C],D)]
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
        a4 = a4.dereference();
        if (a4.isList()){
            Term[] args = {((ListTerm)a4).car(), ((ListTerm)a4).cdr()};
            a5 = args[0];
            a6 = args[1];
        } else if (a4.isVariable()){
            a5 = new VariableTerm(engine);
            a6 = new VariableTerm(engine);
            ((VariableTerm) a4).bind(new ListTerm(a5, a6), engine.trail);
        } else {
            return engine.fail();
        }
        Term[] y1 = {s3, new VariableTerm(engine)};
        a7 = new StructureTerm(s2, y1);
        //START inline expansion of $not_unifiable(a(3),a(7))
        if (a3.unify(a7, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a8 = new ListTerm(a5, a6);
        a9 = new ListTerm(a3, a8);
        return new PRED_squish_rests_helper_2(a9, a2, cont);
    }
}
