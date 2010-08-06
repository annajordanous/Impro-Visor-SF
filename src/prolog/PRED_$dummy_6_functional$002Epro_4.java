import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_6_functional.pro'/4</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_6_functional$002Epro_4 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("user");
    static Predicate _$dummy_6_functional$002Epro_4_sub_1 = new PRED_$dummy_6_functional$002Epro_4_sub_1();
    static Predicate _$dummy_6_functional$002Epro_4_1 = new PRED_$dummy_6_functional$002Epro_4_1();
    static Predicate _$dummy_6_functional$002Epro_4_2 = new PRED_$dummy_6_functional$002Epro_4_2();

    public Term arg1, arg2, arg3, arg4;

    public PRED_$dummy_6_functional$002Epro_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_$dummy_6_functional$002Epro_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "$dummy_6_functional.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_6_functional$002Epro_4_1, _$dummy_6_functional$002Epro_4_sub_1);
    }
}

class PRED_$dummy_6_functional$002Epro_4_sub_1 extends PRED_$dummy_6_functional$002Epro_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_6_functional$002Epro_4_2);
    }
}

class PRED_$dummy_6_functional$002Epro_4_1 extends PRED_$dummy_6_functional$002Epro_4 {
    public Predicate exec(Prolog engine) {
    // '$dummy_6_functional.pro'(A,B,C,D):-call(A),!,C=[B|D]
        Term a1, a2, a3, a4, a5, a6, a7;
        Predicate p1, p2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // '$dummy_6_functional.pro'(A,B,C,D):-['$get_level'(E),call(user:A),'$cut'(E),'$unify'(C,[B|D])]
        a5 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(5))
        if (! a5.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {s2, a1};
        a6 = new StructureTerm(s1, y1);
        a7 = new ListTerm(a2, a4);
        p1 = new PRED_$unify_2(a3, a7, cont);
        p2 = new PRED_$cut_1(a5, p1);
        return new PRED_call_1(a6, p2);
    }
}

class PRED_$dummy_6_functional$002Epro_4_2 extends PRED_$dummy_6_functional$002Epro_4 {
    public Predicate exec(Prolog engine) {
    // '$dummy_6_functional.pro'(A,B,C,D):-C=D
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // '$dummy_6_functional.pro'(A,B,C,D):-['$unify'(C,D)]
        //START inline expansion of $unify(a(3),a(4))
        if (! a3.unify(a4, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}
