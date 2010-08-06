import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>onset_list_helper/3</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_onset_list_helper_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static Predicate _fail_0 = new PRED_fail_0();
    static Predicate _onset_list_helper_3_var = new PRED_onset_list_helper_3_var();
    static Predicate _onset_list_helper_3_var_1 = new PRED_onset_list_helper_3_var_1();
    static Predicate _onset_list_helper_3_1 = new PRED_onset_list_helper_3_1();
    static Predicate _onset_list_helper_3_2 = new PRED_onset_list_helper_3_2();

    public Term arg1, arg2, arg3;

    public PRED_onset_list_helper_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_onset_list_helper_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "onset_list_helper(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_onset_list_helper_3_var, _fail_0, _fail_0, _onset_list_helper_3_1, _fail_0, _onset_list_helper_3_2);
    }
}

class PRED_onset_list_helper_3_var extends PRED_onset_list_helper_3 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_onset_list_helper_3_1, _onset_list_helper_3_var_1);
    }
}

class PRED_onset_list_helper_3_var_1 extends PRED_onset_list_helper_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_onset_list_helper_3_2);
    }
}

class PRED_onset_list_helper_3_1 extends PRED_onset_list_helper_3 {
    public Predicate exec(Prolog engine) {
    // onset_list_helper([],A,B):-reverse(A,B)
        Term a1, a2, a3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // onset_list_helper([],A,B):-[reverse(A,B)]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s1))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        return new PRED_reverse_2(a2, a3, cont);
    }
}

class PRED_onset_list_helper_3_2 extends PRED_onset_list_helper_3 {
    public Predicate exec(Prolog engine) {
    // onset_list_helper([A|B],C,D):-first(C,E),F is A+E,G=[F|C],onset_list_helper(B,G,D)
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Predicate p1, p2, p3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // onset_list_helper([A|B],C,D):-[first(C,E),'$plus'(A,E,F),'$unify'(G,[F|C]),onset_list_helper(B,G,D)]
        a1 = a1.dereference();
        if (a1.isList()){
            Term[] args = {((ListTerm)a1).car(), ((ListTerm)a1).cdr()};
            a4 = args[0];
            a5 = args[1];
        } else if (a1.isVariable()){
            a4 = new VariableTerm(engine);
            a5 = new VariableTerm(engine);
            ((VariableTerm) a1).bind(new ListTerm(a4, a5), engine.trail);
        } else {
            return engine.fail();
        }
        a6 = new VariableTerm(engine);
        a7 = new VariableTerm(engine);
        a8 = new VariableTerm(engine);
        a9 = new ListTerm(a7, a2);
        p1 = new PRED_onset_list_helper_3(a5, a8, a3, cont);
        p2 = new PRED_$unify_2(a8, a9, p1);
        p3 = new PRED_$plus_3(a4, a6, a7, p2);
        return new PRED_first_2(a2, a6, p3);
    }
}
