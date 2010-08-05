package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_10_attributes.pro'/10</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_10_attributes$002Epro_10 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _$dummy_10_attributes$002Epro_10_sub_1 = new PRED_$dummy_10_attributes$002Epro_10_sub_1();
    static Predicate _$dummy_10_attributes$002Epro_10_1 = new PRED_$dummy_10_attributes$002Epro_10_1();
    static Predicate _$dummy_10_attributes$002Epro_10_2 = new PRED_$dummy_10_attributes$002Epro_10_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10;

    public PRED_$dummy_10_attributes$002Epro_10(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Term a7, Term a8, Term a9, Term a10, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        arg6 = a6;
        arg7 = a7;
        arg8 = a8;
        arg9 = a9;
        arg10 = a10;
        this.cont = cont;
    }

    public PRED_$dummy_10_attributes$002Epro_10(){}

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
        arg10 = args[9];
        this.cont = cont;
    }

    public int arity() { return 10; }

    public String toString() {
        return "$dummy_10_attributes.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + "," + arg9 + "," + arg10 + ")";
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
        engine.aregs[10] = arg10;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_10_attributes$002Epro_10_1, _$dummy_10_attributes$002Epro_10_sub_1);
    }
}

class PRED_$dummy_10_attributes$002Epro_10_sub_1 extends PRED_$dummy_10_attributes$002Epro_10 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_10_attributes$002Epro_10_2);
    }
}

class PRED_$dummy_10_attributes$002Epro_10_1 extends PRED_$dummy_10_attributes$002Epro_10 {
    public Predicate exec(Prolog engine) {
    // '$dummy_10_attributes.pro'(A,B,C,D,E,F,G,H,I,J):-D>G,!,'$dummy_11_attributes.pro'(I,A,H,D,B,E,J,F)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;
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
        a10 = engine.aregs[10];
        cont = engine.cont;
    // '$dummy_10_attributes.pro'(A,B,C,D,E,F,G,H,I,J):-['$get_level'(K),'$greater_than'(D,G),'$cut'(K),'$dummy_11_attributes.pro'(I,A,H,D,B,E,J,F)]
        a11 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(11))
        if (! a11.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $greater_than(a(4),a(7))
        try {
            if (Arithmetic.evaluate(a4).arithCompareTo(Arithmetic.evaluate(a7)) <= 0) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        //START inline expansion of $cut(a(11))
        a11 = a11.dereference();
        if (! a11.isInteger()) {
            throw new IllegalTypeException("integer", a11);
        } else {
            engine.cut(((IntegerTerm) a11).intValue());
        }
        //END inline expansion
        return new PRED_$dummy_11_attributes$002Epro_8(a9, a1, a8, a4, a2, a5, a10, a6, cont);
    }
}

class PRED_$dummy_10_attributes$002Epro_10_2 extends PRED_$dummy_10_attributes$002Epro_10 {
    public Predicate exec(Prolog engine) {
    // '$dummy_10_attributes.pro'(A,B,C,D,E,F,G,H,I,J):-two_most_likely(J,H,[G,C],[F,B],A,E)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13, a14;
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
        a10 = engine.aregs[10];
        cont = engine.cont;
    // '$dummy_10_attributes.pro'(A,B,C,D,E,F,G,H,I,J):-[two_most_likely(J,H,[G,C],[F,B],A,E)]
        a11 = new ListTerm(a3, s1);
        a12 = new ListTerm(a7, a11);
        a13 = new ListTerm(a2, s1);
        a14 = new ListTerm(a6, a13);
        return new PRED_two_most_likely_6(a10, a8, a12, a14, a1, a5, cont);
    }
}
