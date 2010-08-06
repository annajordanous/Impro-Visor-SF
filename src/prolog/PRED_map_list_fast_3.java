import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>map_list_fast/3</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_map_list_fast_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("nil");
    static Predicate _map_list_fast_3_sub_1 = new PRED_map_list_fast_3_sub_1();
    static Predicate _map_list_fast_3_1 = new PRED_map_list_fast_3_1();
    static Predicate _map_list_fast_3_2 = new PRED_map_list_fast_3_2();

    public Term arg1, arg2, arg3;

    public PRED_map_list_fast_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_map_list_fast_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "map_list_fast(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_map_list_fast_3_1, _map_list_fast_3_sub_1);
    }
}

class PRED_map_list_fast_3_sub_1 extends PRED_map_list_fast_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_map_list_fast_3_2);
    }
}

class PRED_map_list_fast_3_1 extends PRED_map_list_fast_3 {
    public Predicate exec(Prolog engine) {
    // map_list_fast(A,B,[]):-some(nil,A),!
        Term a1, a2, a3, a4;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // map_list_fast(A,B,[]):-['$get_level'(C),some(nil,A),'$cut'(C)]
        a3 = a3.dereference();
        if (a3.isSymbol()){
            if (! a3.equals(s1))
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        a4 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(4))
        if (! a4.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        p1 = new PRED_$cut_1(a4, cont);
        return new PRED_some_2(s2, a1, p1);
    }
}

class PRED_map_list_fast_3_2 extends PRED_map_list_fast_3 {
    public Predicate exec(Prolog engine) {
    // map_list_fast(A,B,[C|D]):-map_first(A,E),append(E,[C],F),apply(B,F),map_rest(A,G),map_list_fast(G,B,D)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Predicate p1, p2, p3, p4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // map_list_fast(A,B,[C|D]):-[map_first(A,E),append(E,[C],F),apply(B,F),map_rest(A,G),map_list_fast(G,B,D)]
        a3 = a3.dereference();
        if (a3.isList()){
            Term[] args = {((ListTerm)a3).car(), ((ListTerm)a3).cdr()};
            a4 = args[0];
            a5 = args[1];
        } else if (a3.isVariable()){
            a4 = new VariableTerm(engine);
            a5 = new VariableTerm(engine);
            ((VariableTerm) a3).bind(new ListTerm(a4, a5), engine.trail);
        } else {
            return engine.fail();
        }
        a6 = new VariableTerm(engine);
        a7 = new ListTerm(a4, s1);
        a8 = new VariableTerm(engine);
        a9 = new VariableTerm(engine);
        p1 = new PRED_map_list_fast_3(a9, a2, a5, cont);
        p2 = new PRED_map_rest_2(a1, a9, p1);
        p3 = new PRED_apply_2(a2, a8, p2);
        p4 = new PRED_append_3(a6, a7, a8, p3);
        return new PRED_map_first_2(a1, a6, p4);
    }
}
