import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>split_onset_helper/5</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_split_onset_helper_5 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("fn", 2);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("is", 2);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("-", 2);
    static Predicate _split_onset_helper_5_top = new PRED_split_onset_helper_5_top();
    static Predicate _fail_0 = new PRED_fail_0();
    static Predicate _split_onset_helper_5_var = new PRED_split_onset_helper_5_var();
    static Predicate _split_onset_helper_5_var_1 = new PRED_split_onset_helper_5_var_1();
    static Predicate _split_onset_helper_5_var_2 = new PRED_split_onset_helper_5_var_2();
    static Predicate _split_onset_helper_5_lis = new PRED_split_onset_helper_5_lis();
    static Predicate _split_onset_helper_5_lis_1 = new PRED_split_onset_helper_5_lis_1();
    static Predicate _split_onset_helper_5_1 = new PRED_split_onset_helper_5_1();
    static Predicate _split_onset_helper_5_2 = new PRED_split_onset_helper_5_2();
    static Predicate _split_onset_helper_5_3 = new PRED_split_onset_helper_5_3();

    public Term arg1, arg2, arg3, arg4, arg5;

    public PRED_split_onset_helper_5(Term a1, Term a2, Term a3, Term a4, Term a5, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        this.cont = cont;
    }

    public PRED_split_onset_helper_5(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        this.cont = cont;
    }

    public int arity() { return 5; }

    public String toString() {
        return "split_onset_helper(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.aregs[5] = arg5;
        engine.cont = cont;
        return _split_onset_helper_5_top;
    }
}

class PRED_split_onset_helper_5_top extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
        engine.setB0();
        return engine.switch_on_term(_split_onset_helper_5_var, _fail_0, _fail_0, _split_onset_helper_5_1, _fail_0, _split_onset_helper_5_lis);
    }
}

class PRED_split_onset_helper_5_var extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_split_onset_helper_5_1, _split_onset_helper_5_var_1);
    }
}

class PRED_split_onset_helper_5_var_1 extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_split_onset_helper_5_2, _split_onset_helper_5_var_2);
    }
}

class PRED_split_onset_helper_5_var_2 extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_split_onset_helper_5_3);
    }
}

class PRED_split_onset_helper_5_lis extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_split_onset_helper_5_2, _split_onset_helper_5_lis_1);
    }
}

class PRED_split_onset_helper_5_lis_1 extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_split_onset_helper_5_3);
    }
}

class PRED_split_onset_helper_5_1 extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
    // split_onset_helper([],A,B,C,[]):-reverse(B,C)
        Term a1, a2, a3, a4, a5;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // split_onset_helper([],A,B,C,[]):-[reverse(B,C)]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s1))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        a5 = a5.dereference();
        if (a5.isSymbol()){
            if (! a5.equals(s1))
                return engine.fail();
        } else if (a5.isVariable()){
            ((VariableTerm) a5).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        return new PRED_reverse_2(a3, a4, cont);
    }
}

class PRED_split_onset_helper_5_2 extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
    // split_onset_helper([A|B],C,D,E,F):-A<C,G=[A|D],split_onset_helper(B,C,G,E,F)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // split_onset_helper([A|B],C,D,E,F):-['$less_than'(A,C),'$unify'(G,[A|D]),split_onset_helper(B,C,G,E,F)]
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a6 = args[0];
            a7 = args[1];
        } else if (a1.isVariable()){
            a6 = new VariableTerm(engine);
            a7 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a6, a7), engine.trail);
        } else {
            return engine.fail();
        }
        //START inline expansion of $less_than(a(6),a(2))
        try {
            if (Arithmetic.evaluate(a6).arithCompareTo(Arithmetic.evaluate(a2)) >= 0) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        a8 = new VariableTerm(engine);
        a9 = new ListTerm(a6, a3);
        //START inline expansion of $unify(a(8),a(9))
        if (! a8.unify(a9, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        engine.aregs[1] = a7;
        engine.aregs[2] = a2;
        engine.aregs[3] = a8;
        engine.aregs[4] = a4;
        engine.aregs[5] = a5;
        engine.cont = cont;
        return _split_onset_helper_5_top;
    }
}

class PRED_split_onset_helper_5_3 extends PRED_split_onset_helper_5 {
    public Predicate exec(Prolog engine) {
    // split_onset_helper([A|B],C,D,E,F):-A>=C,reverse(D,E),map_fn([A|B],fn([G,H],H is G-C),F)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14, a15;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // split_onset_helper([A|B],C,D,E,F):-['$greater_or_equal'(A,C),reverse(D,E),map_fn([A|B],fn([G,H],H is G-C),F)]
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a6 = args[0];
            a7 = args[1];
        } else if (a1.isVariable()){
            a6 = new VariableTerm(engine);
            a7 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a6, a7), engine.trail);
        } else {
            return engine.fail();
        }
        //START inline expansion of $greater_or_equal(a(6),a(2))
        try {
            if (Arithmetic.evaluate(a6).arithCompareTo(Arithmetic.evaluate(a2)) < 0) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        a8 = new ListTerm(a6, a7);
        a9 = new VariableTerm(engine);
        a10 = new VariableTerm(engine);
        a11 = new ListTerm(a10, s1);
        a12 = new ListTerm(a9, a11);
        Term[] y1 = {a9, a2};
        a13 = new StructureTerm(s4, y1);
        Term[] y2 = {a10, a13};
        a14 = new StructureTerm(s3, y2);
        Term[] y3 = {a12, a14};
        a15 = new StructureTerm(s2, y3);
        p1 = new PRED_map_fn_3(a8, a15, a5, cont);
        return new PRED_reverse_2(a3, a4, p1);
    }
}
