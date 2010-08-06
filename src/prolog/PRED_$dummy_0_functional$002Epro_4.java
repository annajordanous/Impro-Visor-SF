import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_0_functional.pro'/4</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_0_functional$002Epro_4 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("fn", 2);
    static Predicate _$dummy_0_functional$002Epro_4_sub_1 = new PRED_$dummy_0_functional$002Epro_4_sub_1();
    static Predicate _$dummy_0_functional$002Epro_4_1 = new PRED_$dummy_0_functional$002Epro_4_1();
    static Predicate _$dummy_0_functional$002Epro_4_2 = new PRED_$dummy_0_functional$002Epro_4_2();

    public Term arg1, arg2, arg3, arg4;

    public PRED_$dummy_0_functional$002Epro_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_$dummy_0_functional$002Epro_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "$dummy_0_functional.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_0_functional$002Epro_4_1, _$dummy_0_functional$002Epro_4_sub_1);
    }
}

class PRED_$dummy_0_functional$002Epro_4_sub_1 extends PRED_$dummy_0_functional$002Epro_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_0_functional$002Epro_4_2);
    }
}

class PRED_$dummy_0_functional$002Epro_4_1 extends PRED_$dummy_0_functional$002Epro_4 {
    public Predicate exec(Prolog engine) {
    // '$dummy_0_functional.pro'(A,B,C,D):-vars(D,A),!,map_fn(B,fn(A,D),C)
        Term a1, a2, a3, a4, a5, a6;
        Predicate p1, p2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // '$dummy_0_functional.pro'(A,B,C,D):-['$get_level'(E),vars(D,A),'$cut'(E),map_fn(B,fn(A,D),C)]
        a5 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(5))
        if (! a5.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a1, a4};
        a6 = new StructureTerm(s1, y1);
        p1 = new PRED_map_fn_3(a2, a6, a3, cont);
        p2 = new PRED_$cut_1(a5, p1);
        return new PRED_vars_2(a4, a1, p2);
    }
}

class PRED_$dummy_0_functional$002Epro_4_2 extends PRED_$dummy_0_functional$002Epro_4 {
    public Predicate exec(Prolog engine) {
    // '$dummy_0_functional.pro'(A,B,C,D):-map_fast(B,D,C)
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // '$dummy_0_functional.pro'(A,B,C,D):-[map_fast(B,D,C)]
        return new PRED_map_fast_3(a2, a4, a3, cont);
    }
}
