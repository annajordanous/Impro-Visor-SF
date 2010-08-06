import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_26_functional.pro'/6</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_26_functional$002Epro_6 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("\\+", 1);
    static Predicate _$dummy_26_functional$002Epro_6_sub_1 = new PRED_$dummy_26_functional$002Epro_6_sub_1();
    static Predicate _$dummy_26_functional$002Epro_6_1 = new PRED_$dummy_26_functional$002Epro_6_1();
    static Predicate _$dummy_26_functional$002Epro_6_2 = new PRED_$dummy_26_functional$002Epro_6_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6;

    public PRED_$dummy_26_functional$002Epro_6(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        arg6 = a6;
        this.cont = cont;
    }

    public PRED_$dummy_26_functional$002Epro_6(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        arg6 = args[5];
        this.cont = cont;
    }

    public int arity() { return 6; }

    public String toString() {
        return "$dummy_26_functional.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.aregs[5] = arg5;
        engine.aregs[6] = arg6;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_26_functional$002Epro_6_1, _$dummy_26_functional$002Epro_6_sub_1);
    }
}

class PRED_$dummy_26_functional$002Epro_6_sub_1 extends PRED_$dummy_26_functional$002Epro_6 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_26_functional$002Epro_6_2);
    }
}

class PRED_$dummy_26_functional$002Epro_6_1 extends PRED_$dummy_26_functional$002Epro_6 {
    public Predicate exec(Prolog engine) {
    // '$dummy_26_functional.pro'(A,B,C,D,E,F):-call(C,A),!,F=[],E=[A|B]
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        Predicate p1, p2, p3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        cont = engine.cont;
    // '$dummy_26_functional.pro'(A,B,C,D,E,F):-['$get_level'(G),call(C,A),'$cut'(G),'$unify'(F,[]),'$unify'(E,[A|B])]
        a7 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(7))
        if (! a7.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a8 = new ListTerm(a1, a2);
        p1 = new PRED_$unify_2(a5, a8, cont);
        p2 = new PRED_$unify_2(a6, s1, p1);
        p3 = new PRED_$cut_1(a7, p2);
        return new PRED_call_2(a3, a1, p3);
    }
}

class PRED_$dummy_26_functional$002Epro_6_2 extends PRED_$dummy_26_functional$002Epro_6 {
    public Predicate exec(Prolog engine) {
    // '$dummy_26_functional.pro'(A,B,C,D,E,F):-F=[A|D],take_while(B,\+C,D,E)
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        a6 = engine.aregs[6];
        cont = engine.cont;
    // '$dummy_26_functional.pro'(A,B,C,D,E,F):-['$unify'(F,[A|D]),take_while(B,\+C,D,E)]
        a7 = new ListTerm(a1, a4);
        //START inline expansion of $unify(a(6),a(7))
        if (! a6.unify(a7, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a3};
        a8 = new StructureTerm(s2, y1);
        return new PRED_take_while_4(a2, a8, a4, a5, cont);
    }
}
