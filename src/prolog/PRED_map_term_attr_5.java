import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>map_term_attr/5</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_map_term_attr_5 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _fail_0 = new PRED_fail_0();
    static Predicate _map_term_attr_5_var = new PRED_map_term_attr_5_var();
    static Predicate _map_term_attr_5_var_1 = new PRED_map_term_attr_5_var_1();
    static Predicate _map_term_attr_5_1 = new PRED_map_term_attr_5_1();
    static Predicate _map_term_attr_5_2 = new PRED_map_term_attr_5_2();

    public Term arg1, arg2, arg3, arg4, arg5;

    public PRED_map_term_attr_5(Term a1, Term a2, Term a3, Term a4, Term a5, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        this.cont = cont;
    }

    public PRED_map_term_attr_5(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        this.cont = cont;
    }

    public int arity() { return 5; }

    public String toString() {
        return "map_term_attr(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.aregs[5] = arg5;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_map_term_attr_5_var, _fail_0, _fail_0, _map_term_attr_5_1, _fail_0, _map_term_attr_5_2);
    }
}

class PRED_map_term_attr_5_var extends PRED_map_term_attr_5 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_map_term_attr_5_1, _map_term_attr_5_var_1);
    }
}

class PRED_map_term_attr_5_var_1 extends PRED_map_term_attr_5 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_map_term_attr_5_2);
    }
}

class PRED_map_term_attr_5_1 extends PRED_map_term_attr_5 {
    public Predicate exec(Prolog engine) {
    // map_term_attr([],A,B,A,B):-!
        Term a1, a2, a3, a4, a5;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // map_term_attr([],A,B,A,B):-['$neck_cut']
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s1))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        if (! a2.unify(a4, engine.trail))
            return engine.fail();
        if (! a3.unify(a5, engine.trail))
            return engine.fail();
        //START inline expansion of $neck_cut
        engine.neckCut();
        //END inline expansion
        return cont;
    }
}

class PRED_map_term_attr_5_2 extends PRED_map_term_attr_5 {
    public Predicate exec(Prolog engine) {
    // map_term_attr([A|B],C,D,E,F):-attr(G,A,H),'$dummy_7_attributes.pro'(A,D,B,F,I,J,K,C,E,H)
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        a5 = engine.aregs[5];
        cont = engine.cont;
    // map_term_attr([A|B],C,D,E,F):-[attr(G,A,H),'$dummy_7_attributes.pro'(A,D,B,F,I,J,K,C,E,H)]
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a6 = args[0];
            a7 = args[1];
        } else if (a1.isVariable()){
            a6 = new VariableTerm(engine);
            a7 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a6, a7), engine.trail);
        } else {
            return engine.fail();
        }
        a8 = new VariableTerm(engine);
        p1 = new PRED_$dummy_7_attributes$002Epro_10(a6, a3, a7, a5, new VariableTerm(engine), new VariableTerm(engine), new VariableTerm(engine), a2, a4, a8, cont);
        return new PRED_attr_3(new VariableTerm(engine), a6, a8, p1);
    }
}
