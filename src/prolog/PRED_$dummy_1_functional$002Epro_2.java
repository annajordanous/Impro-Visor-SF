import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_1_functional.pro'/2</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_1_functional$002Epro_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _$dummy_1_functional$002Epro_2_sub_1 = new PRED_$dummy_1_functional$002Epro_2_sub_1();
    static Predicate _$dummy_1_functional$002Epro_2_1 = new PRED_$dummy_1_functional$002Epro_2_1();
    static Predicate _$dummy_1_functional$002Epro_2_2 = new PRED_$dummy_1_functional$002Epro_2_2();

    public Term arg1, arg2;

    public PRED_$dummy_1_functional$002Epro_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_$dummy_1_functional$002Epro_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "$dummy_1_functional.pro(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_1_functional$002Epro_2_1, _$dummy_1_functional$002Epro_2_sub_1);
    }
}

class PRED_$dummy_1_functional$002Epro_2_sub_1 extends PRED_$dummy_1_functional$002Epro_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_1_functional$002Epro_2_2);
    }
}

class PRED_$dummy_1_functional$002Epro_2_1 extends PRED_$dummy_1_functional$002Epro_2 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_functional.pro'(A,B):-B=[]
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // '$dummy_1_functional.pro'(A,B):-['$unify'(B,[])]
        //START inline expansion of $unify(a(2),s(1))
        if (! a2.unify(s1, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}

class PRED_$dummy_1_functional$002Epro_2_2 extends PRED_$dummy_1_functional$002Epro_2 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_functional.pro'(A,B):-A=[]
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // '$dummy_1_functional.pro'(A,B):-['$unify'(A,[])]
        //START inline expansion of $unify(a(1),s(1))
        if (! a1.unify(s1, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}
