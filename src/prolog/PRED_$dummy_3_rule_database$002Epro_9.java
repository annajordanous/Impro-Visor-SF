package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_3_rule_database.pro'/9</code> defined in rule_database.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_3_rule_database$002Epro_9 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("t", 2);
    static Predicate _$dummy_3_rule_database$002Epro_9_sub_1 = new PRED_$dummy_3_rule_database$002Epro_9_sub_1();
    static Predicate _$dummy_3_rule_database$002Epro_9_1 = new PRED_$dummy_3_rule_database$002Epro_9_1();
    static Predicate _$dummy_3_rule_database$002Epro_9_2 = new PRED_$dummy_3_rule_database$002Epro_9_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9;

    public PRED_$dummy_3_rule_database$002Epro_9(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Term a7, Term a8, Term a9, Predicate cont) {
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

    public PRED_$dummy_3_rule_database$002Epro_9(){}

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
        return "$dummy_3_rule_database.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + "," + arg9 + ")";
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
        return engine.jtry(_$dummy_3_rule_database$002Epro_9_1, _$dummy_3_rule_database$002Epro_9_sub_1);
    }
}

class PRED_$dummy_3_rule_database$002Epro_9_sub_1 extends PRED_$dummy_3_rule_database$002Epro_9 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_3_rule_database$002Epro_9_2);
    }
}

class PRED_$dummy_3_rule_database$002Epro_9_1 extends PRED_$dummy_3_rule_database$002Epro_9 {
    public Predicate exec(Prolog engine) {
    // '$dummy_3_rule_database.pro'(A,B,C,D,E,F,G,H,I):-(note_functor(B),B=t(D,C)),!,true
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;
        Predicate p1, p2;
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
    // '$dummy_3_rule_database.pro'(A,B,C,D,E,F,G,H,I):-['$get_level'(J),note_functor(B),'$unify'(B,t(D,C)),'$cut'(J)]
        a10 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(10))
        if (! a10.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a4, a3};
        a11 = new StructureTerm(s1, y1);
        p1 = new PRED_$cut_1(a10, cont);
        p2 = new PRED_$unify_2(a2, a11, p1);
        return new PRED_note_functor_1(a2, p2);
    }
}

class PRED_$dummy_3_rule_database$002Epro_9_2 extends PRED_$dummy_3_rule_database$002Epro_9 {
    public Predicate exec(Prolog engine) {
    // '$dummy_3_rule_database.pro'(A,B,C,D,E,F,G,H,I):-'$dummy_4_rule_database.pro'(G,I,A,H,E,F,C,B)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9;
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
    // '$dummy_3_rule_database.pro'(A,B,C,D,E,F,G,H,I):-['$dummy_4_rule_database.pro'(G,I,A,H,E,F,C,B)]
        return new PRED_$dummy_4_rule_database$002Epro_8(a7, a9, a1, a8, a5, a6, a3, a2, cont);
    }
}
