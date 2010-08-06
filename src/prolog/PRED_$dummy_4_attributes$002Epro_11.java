import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_4_attributes.pro'/11</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_4_attributes$002Epro_11 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("slope", 3);
    static Predicate _$dummy_4_attributes$002Epro_11_sub_1 = new PRED_$dummy_4_attributes$002Epro_11_sub_1();
    static Predicate _$dummy_4_attributes$002Epro_11_1 = new PRED_$dummy_4_attributes$002Epro_11_1();
    static Predicate _$dummy_4_attributes$002Epro_11_2 = new PRED_$dummy_4_attributes$002Epro_11_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11;

    public PRED_$dummy_4_attributes$002Epro_11(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Term a7, Term a8, Term a9, Term a10, Term a11, Predicate cont) {
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
        arg11 = a11;
        this.cont = cont;
    }

    public PRED_$dummy_4_attributes$002Epro_11(){}

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
        arg11 = args[10];
        this.cont = cont;
    }

    public int arity() { return 11; }

    public String toString() {
        return "$dummy_4_attributes.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + "," + arg9 + "," + arg10 + "," + arg11 + ")";
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
        engine.aregs[11] = arg11;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_4_attributes$002Epro_11_1, _$dummy_4_attributes$002Epro_11_sub_1);
    }
}

class PRED_$dummy_4_attributes$002Epro_11_sub_1 extends PRED_$dummy_4_attributes$002Epro_11 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_4_attributes$002Epro_11_2);
    }
}

class PRED_$dummy_4_attributes$002Epro_11_1 extends PRED_$dummy_4_attributes$002Epro_11 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_attributes.pro'(A,B,C,D,E,F,G,H,I,J,K):-B=slope(D,C,G),!,append(G,I,K),attr_helper(H,K,J,A)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12, a13;
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
        a10 = engine.aregs[10];
        a11 = engine.aregs[11];
        cont = engine.cont;
    // '$dummy_4_attributes.pro'(A,B,C,D,E,F,G,H,I,J,K):-['$get_level'(L),'$unify'(B,slope(D,C,G)),'$cut'(L),append(G,I,K),attr_helper(H,K,J,A)]
        a12 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(12))
        if (! a12.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a4, a3, a7};
        a13 = new StructureTerm(s1, y1);
        //START inline expansion of $unify(a(2),a(13))
        if (! a2.unify(a13, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $cut(a(12))
        a12 = a12.dereference();
        if (! a12.isInteger()) {
            throw new IllegalTypeException("integer", a12);
        } else {
            engine.cut(((IntegerTerm) a12).intValue());
        }
        //END inline expansion
        p1 = new PRED_attr_helper_4(a8, a11, a10, a1, cont);
        return new PRED_append_3(a7, a9, a11, p1);
    }
}

class PRED_$dummy_4_attributes$002Epro_11_2 extends PRED_$dummy_4_attributes$002Epro_11 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_attributes.pro'(A,B,C,D,E,F,G,H,I,J,K):-'$dummy_5_attributes.pro'(I,H,K,J,A,E,B,F)
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
        a11 = engine.aregs[11];
        cont = engine.cont;
    // '$dummy_4_attributes.pro'(A,B,C,D,E,F,G,H,I,J,K):-['$dummy_5_attributes.pro'(I,H,K,J,A,E,B,F)]
        return new PRED_$dummy_5_attributes$002Epro_8(a9, a8, a11, a10, a1, a5, a2, a6, cont);
    }
}
