package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_4_rule_database.pro'/8</code> defined in rule_database.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_4_rule_database$002Epro_8 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("slope", 3);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("symbol_duration");
    static SymbolTerm s3 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s4 = SymbolTerm.makeSymbol("user");
    static SymbolTerm s5 = SymbolTerm.makeSymbol("symbol_duration_memo", 2);
    static Predicate _$dummy_4_rule_database$002Epro_8_sub_1 = new PRED_$dummy_4_rule_database$002Epro_8_sub_1();
    static Predicate _$dummy_4_rule_database$002Epro_8_1 = new PRED_$dummy_4_rule_database$002Epro_8_1();
    static Predicate _$dummy_4_rule_database$002Epro_8_2 = new PRED_$dummy_4_rule_database$002Epro_8_2();

    public Term arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8;

    public PRED_$dummy_4_rule_database$002Epro_8(Term a1, Term a2, Term a3, Term a4, Term a5, Term a6, Term a7, Term a8, Predicate cont) {
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

    public PRED_$dummy_4_rule_database$002Epro_8(){}

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
        return "$dummy_4_rule_database.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + "," + arg6 + "," + arg7 + "," + arg8 + ")";
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
        return engine.jtry(_$dummy_4_rule_database$002Epro_8_1, _$dummy_4_rule_database$002Epro_8_sub_1);
    }
}

class PRED_$dummy_4_rule_database$002Epro_8_sub_1 extends PRED_$dummy_4_rule_database$002Epro_8 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_4_rule_database$002Epro_8_2);
    }
}

class PRED_$dummy_4_rule_database$002Epro_8_1 extends PRED_$dummy_4_rule_database$002Epro_8 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_rule_database.pro'(A,B,C,D,E,F,G,H):-H=slope(E,A,B),!,map_fast(B,symbol_duration,F),sumlist(F,G)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10;
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
        cont = engine.cont;
    // '$dummy_4_rule_database.pro'(A,B,C,D,E,F,G,H):-['$get_level'(I),'$unify'(H,slope(E,A,B)),'$cut'(I),map_fast(B,symbol_duration,F),sumlist(F,G)]
        a9 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(9))
        if (! a9.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a5, a1, a2};
        a10 = new StructureTerm(s1, y1);
        //START inline expansion of $unify(a(8),a(10))
        if (! a8.unify(a10, engine.trail)) {
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
        p1 = new PRED_sumlist_2(a6, a7, cont);
        return new PRED_map_fast_3(a2, s2, a6, p1);
    }
}

class PRED_$dummy_4_rule_database$002Epro_8_2 extends PRED_$dummy_4_rule_database$002Epro_8 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_rule_database.pro'(A,B,C,D,E,F,G,H):-expand_one_level(H,D,C),!,map_fast(D,symbol_duration,F),sumlist(F,G),asserta(symbol_duration_memo(H,G))
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;
        Predicate p1, p2, p3, p4;
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
    // '$dummy_4_rule_database.pro'(A,B,C,D,E,F,G,H):-['$get_level'(I),expand_one_level(H,D,C),'$cut'(I),map_fast(D,symbol_duration,F),sumlist(F,G),asserta(user:symbol_duration_memo(H,G))]
        a9 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(9))
        if (! a9.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {a8, a7};
        a10 = new StructureTerm(s5, y1);
        Term[] y2 = {s4, a10};
        a11 = new StructureTerm(s3, y2);
        p1 = new PRED_asserta_1(a11, cont);
        p2 = new PRED_sumlist_2(a6, a7, p1);
        p3 = new PRED_map_fast_3(a4, s2, a6, p2);
        p4 = new PRED_$cut_1(a9, p3);
        return new PRED_expand_one_level_3(a8, a4, a3, p4);
    }
}
