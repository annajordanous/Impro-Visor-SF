package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_4_longuet_higgins.pro'/5</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_4_longuet_higgins$002Epro_5 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _$dummy_4_longuet_higgins$002Epro_5_sub_1 = new PRED_$dummy_4_longuet_higgins$002Epro_5_sub_1();
    static Predicate _$dummy_4_longuet_higgins$002Epro_5_1 = new PRED_$dummy_4_longuet_higgins$002Epro_5_1();
    static Predicate _$dummy_4_longuet_higgins$002Epro_5_2 = new PRED_$dummy_4_longuet_higgins$002Epro_5_2();

    public Term arg1, arg2, arg3, arg4, arg5;

    public PRED_$dummy_4_longuet_higgins$002Epro_5(Term a1, Term a2, Term a3, Term a4, Term a5, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        this.cont = cont;
    }

    public PRED_$dummy_4_longuet_higgins$002Epro_5(){}

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
        return "$dummy_4_longuet_higgins.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.aregs[5] = arg5;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_4_longuet_higgins$002Epro_5_1, _$dummy_4_longuet_higgins$002Epro_5_sub_1);
    }
}

class PRED_$dummy_4_longuet_higgins$002Epro_5_sub_1 extends PRED_$dummy_4_longuet_higgins$002Epro_5 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_4_longuet_higgins$002Epro_5_2);
    }
}

class PRED_$dummy_4_longuet_higgins$002Epro_5_1 extends PRED_$dummy_4_longuet_higgins$002Epro_5 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_longuet_higgins.pro'(A,B,C,D,E):-E=[],!,B=E
        Term a1, a2, a3, a4, a5, a6;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // '$dummy_4_longuet_higgins.pro'(A,B,C,D,E):-['$get_level'(F),'$unify'(E,[]),'$cut'(F),'$unify'(B,E)]
        a6 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(6))
        if (! a6.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $unify(a(5),s(1))
        if (! a5.unify(s1, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $cut(a(6))
        a6 = a6.dereference();
        if (! a6.isInteger()) {
            throw new IllegalTypeException("integer", a6);
        } else {
            engine.cut(((IntegerTerm) a6).intValue());
        }
        //END inline expansion
        //START inline expansion of $unify(a(2),a(5))
        if (! a2.unify(a5, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}

class PRED_$dummy_4_longuet_higgins$002Epro_5_2 extends PRED_$dummy_4_longuet_higgins$002Epro_5 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_longuet_higgins.pro'(A,B,C,D,E):-split_onsets(E,C,A,D),B=[A|D]
        Term a1, a2, a3, a4, a5, a6;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // '$dummy_4_longuet_higgins.pro'(A,B,C,D,E):-[split_onsets(E,C,A,D),'$unify'(B,[A|D])]
        a6 = new ListTerm(a1, a4);
        p1 = new PRED_$unify_2(a2, a6, cont);
        return new PRED_split_onsets_4(a5, a3, a1, a4, p1);
    }
}
