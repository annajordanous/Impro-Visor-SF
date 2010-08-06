package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_0_squish_rests.pro'/9</code> defined in squish_rests.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_0_squish_rests$002Epro_9 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("t", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("r");
    static Predicate _$dummy_0_squish_rests$002Epro_9_sub_1 = new PRED_$dummy_0_squish_rests$002Epro_9_sub_1();
    static Predicate _$dummy_0_squish_rests$002Epro_9_1 = new PRED_$dummy_0_squish_rests$002Epro_9_1();
    static Predicate _$dummy_0_squish_rests$002Epro_9_2 = new PRED_$dummy_0_squish_rests$002Epro_9_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9;

    public PRED_$dummy_0_squish_rests$002Epro_9(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Term a7, Term a8, Term a9, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        arg6 = a6;
        arg7 = a7;
        arg8 = a8;
        arg9 = a9;
        this.cont = cont;
    }

    public PRED_$dummy_0_squish_rests$002Epro_9(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        arg6 = args[5];
        arg7 = args[6];
        arg8 = args[7];
        arg9 = args[8];
        this.cont = cont;
    }

    public int arity() { return 9; }

    public String toString() {
        return "$dummy_0_squish_rests.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + "," + arg9 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.aregs[5] = arg5;
        engine.aregs[6] = arg6;
        engine.aregs[7] = arg7;
        engine.aregs[8] = arg8;
        engine.aregs[9] = arg9;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_0_squish_rests$002Epro_9_1, _$dummy_0_squish_rests$002Epro_9_sub_1);
    }
}

class PRED_$dummy_0_squish_rests$002Epro_9_sub_1 extends PRED_$dummy_0_squish_rests$002Epro_9 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_0_squish_rests$002Epro_9_2);
    }
}

class PRED_$dummy_0_squish_rests$002Epro_9_1 extends PRED_$dummy_0_squish_rests$002Epro_9 {
    public Predicate exec(Prolog engine) {
    // '$dummy_0_squish_rests.pro'(A,B,C,D,E,F,G,H,I):-A=t(r,D),!,B is E+D,squish_rests([t(r,B)|H],I)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        a7 = engine.aregs[7];
        a8 = engine.aregs[8];
        a9 = engine.aregs[9];
        cont = engine.cont;
    // '$dummy_0_squish_rests.pro'(A,B,C,D,E,F,G,H,I):-['$get_level'(J),'$unify'(A,t(r,D)),'$cut'(J),'$plus'(E,D,B),squish_rests([t(r,B)|H],I)]
        a10 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(10))
        if (! a10.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {s2, a4};
        a11 = new StructureTerm(s1, y1);
        //START inline expansion of $unify(a(1),a(11))
        if (! a1.unify(a11, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $cut(a(10))
        a10 = a10.dereference();
        if (! a10.isInteger()) {
            throw new IllegalTypeException("integer", a10);
        } else {
            engine.cut(((IntegerTerm) a10).intValue());
        }
        //END inline expansion
        //START inline expansion of $plus(a(5),a(4),a(2))
        try {
            if (! a2.unify(Arithmetic.evaluate(a5).add(Arithmetic.evaluate(a4)), engine.trail)) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        Term[] y2 = {s2, a2};
        a12 = new StructureTerm(s1, y2);
        a13 = new ListTerm(a12, a8);
        return new PRED_squish_rests_2(a13, a9, cont);
    }
}

class PRED_$dummy_0_squish_rests$002Epro_9_2 extends PRED_$dummy_0_squish_rests$002Epro_9 {
    public Predicate exec(Prolog engine) {
    // '$dummy_0_squish_rests.pro'(A,B,C,D,E,F,G,H,I):-A\=t(r,G),squish_rests_helper([A|H],F),I=[C|F]
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        a7 = engine.aregs[7];
        a8 = engine.aregs[8];
        a9 = engine.aregs[9];
        cont = engine.cont;
    // '$dummy_0_squish_rests.pro'(A,B,C,D,E,F,G,H,I):-['$not_unifiable'(A,t(r,G)),squish_rests_helper([A|H],F),'$unify'(I,[C|F])]
        Term[] y1 = {s2, a7};
        a10 = new StructureTerm(s1, y1);
        //START inline expansion of $not_unifiable(a(1),a(10))
        if (a1.unify(a10, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a11 = new ListTerm(a1, a8);
        a12 = new ListTerm(a3, a6);
        p1 = new PRED_$unify_2(a9, a12, cont);
        return new PRED_squish_rests_helper_2(a11, a6, p1);
    }
}
