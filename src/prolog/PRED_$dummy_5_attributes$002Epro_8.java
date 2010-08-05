package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_5_attributes.pro'/8</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_5_attributes$002Epro_8 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("t", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("[]");
    static Predicate _$dummy_5_attributes$002Epro_8_sub_1 = new PRED_$dummy_5_attributes$002Epro_8_sub_1();
    static Predicate _$dummy_5_attributes$002Epro_8_1 = new PRED_$dummy_5_attributes$002Epro_8_1();
    static Predicate _$dummy_5_attributes$002Epro_8_2 = new PRED_$dummy_5_attributes$002Epro_8_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8;

    public PRED_$dummy_5_attributes$002Epro_8(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Term a7, Term a8, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        arg6 = a6;
        arg7 = a7;
        arg8 = a8;
        this.cont = cont;
    }

    public PRED_$dummy_5_attributes$002Epro_8(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        arg6 = args[5];
        arg7 = args[6];
        arg8 = args[7];
        this.cont = cont;
    }

    public int arity() { return 8; }

    public String toString() {
        return "$dummy_5_attributes.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + ")";
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
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_5_attributes$002Epro_8_1, _$dummy_5_attributes$002Epro_8_sub_1);
    }
}

class PRED_$dummy_5_attributes$002Epro_8_sub_1 extends PRED_$dummy_5_attributes$002Epro_8 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_5_attributes$002Epro_8_2);
    }
}

class PRED_$dummy_5_attributes$002Epro_8_1 extends PRED_$dummy_5_attributes$002Epro_8 {
    public Predicate exec(Prolog engine) {
    // '$dummy_5_attributes.pro'(A,B,C,D,E,F,G,H):-G=t(H,F),!,C=[G|A],attr_helper(B,C,D,E)
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
        cont = engine.cont;
    // '$dummy_5_attributes.pro'(A,B,C,D,E,F,G,H):-['$get_level'(I),'$unify'(G,t(H,F)),'$cut'(I),'$unify'(C,[G|A]),attr_helper(B,C,D,E)]
        a9 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(9))
        if (! a9.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a8, a6};
        a10 = new StructureTerm(s1, y1);
        //START inline expansion of $unify(a(7),a(10))
        if (! a7.unify(a10, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $cut(a(9))
        a9 = a9.dereference();
        if (! a9.isInteger()) {
            throw new IllegalTypeException("integer", a9);
        } else {
            engine.cut(((IntegerTerm) a9).intValue());
        }
        //END inline expansion
        a11 = new ListTerm(a7, a1);
        //START inline expansion of $unify(a(3),a(11))
        if (! a3.unify(a11, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return new PRED_attr_helper_4(a2, a3, a4, a5, cont);
    }
}

class PRED_$dummy_5_attributes$002Epro_8_2 extends PRED_$dummy_5_attributes$002Epro_8 {
    public Predicate exec(Prolog engine) {
    // '$dummy_5_attributes.pro'(A,B,C,D,E,F,G,H):-E=[]
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        a7 = engine.aregs[7];
        a8 = engine.aregs[8];
        cont = engine.cont;
    // '$dummy_5_attributes.pro'(A,B,C,D,E,F,G,H):-['$unify'(E,[])]
        //START inline expansion of $unify(a(5),s(2))
        if (! a5.unify(s2, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}
