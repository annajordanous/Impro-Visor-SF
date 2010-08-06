package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>map_list_fn_helper/3</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_map_list_fn_helper_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("nil");
    static SymbolTerm s3 = SymbolTerm.makeSymbol("fn", 2);
    static Predicate _map_list_fn_helper_3_sub_1 = new PRED_map_list_fn_helper_3_sub_1();
    static Predicate _map_list_fn_helper_3_1 = new PRED_map_list_fn_helper_3_1();
    static Predicate _map_list_fn_helper_3_2 = new PRED_map_list_fn_helper_3_2();

    public Term arg1, arg2, arg3;

    public PRED_map_list_fn_helper_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_map_list_fn_helper_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "map_list_fn_helper(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_map_list_fn_helper_3_1, _map_list_fn_helper_3_sub_1);
    }
}

class PRED_map_list_fn_helper_3_sub_1 extends PRED_map_list_fn_helper_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_map_list_fn_helper_3_2);
    }
}

class PRED_map_list_fn_helper_3_1 extends PRED_map_list_fn_helper_3 {
    public Predicate exec(Prolog engine) {
    // map_list_fn_helper(A,B,[]):-some(nil,A),!
        Term a1, a2, a3, a4;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // map_list_fn_helper(A,B,[]):-['$get_level'(C),some(nil,A),'$cut'(C)]
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

class PRED_map_list_fn_helper_3_2 extends PRED_map_list_fn_helper_3 {
    public Predicate exec(Prolog engine) {
    // map_list_fn_helper(A,fn(B,C),[D|E]):-map_first(A,F),append(F,D,G),apply_fn(B,C,G),map_rest(A,H),map_list_fn_helper(H,fn(B,C),E)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11;
        Predicate p1, p2, p3, p4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // map_list_fn_helper(A,fn(B,C),[D|E]):-[map_first(A,F),append(F,D,G),apply_fn(B,C,G),map_rest(A,H),map_list_fn_helper(H,fn(B,C),E)]
        a2 = a2.dereference();
        if (a2.isStructure()){
            if (! s3.equals(((StructureTerm)a2).functor()))
                return engine.fail();
            Term[] args = ((StructureTerm)a2).args();
            a4 = args[0];
            a5 = args[1];
        } else if (a2.isVariable()){
            a4 = new VariableTerm(engine);
            a5 = new VariableTerm(engine);
            Term[] args = {a4, a5};
            ((VariableTerm) a2).bind(new StructureTerm(s3, args), engine.trail);
        } else {
            return engine.fail();
        }
        a3 = a3.dereference();
        if (a3.isList()){
            Term[] args = {((ListTerm)a3).car(), ((ListTerm)a3).cdr()};
            a6 = args[0];
            a7 = args[1];
        } else if (a3.isVariable()){
            a6 = new VariableTerm(engine);
            a7 = new VariableTerm(engine);
            ((VariableTerm) a3).bind(new ListTerm(a6, a7), engine.trail);
        } else {
            return engine.fail();
        }
        a8 = new VariableTerm(engine);
        a9 = new VariableTerm(engine);
        a10 = new VariableTerm(engine);
        Term[] y1 = {a4, a5};
        a11 = new StructureTerm(s3, y1);
        p1 = new PRED_map_list_fn_helper_3(a10, a11, a7, cont);
        p2 = new PRED_map_rest_2(a1, a10, p1);
        p3 = new PRED_apply_fn_3(a4, a5, a9, p2);
        p4 = new PRED_append_3(a8, a6, a9, p3);
        return new PRED_map_first_2(a1, a8, p4);
    }
}
