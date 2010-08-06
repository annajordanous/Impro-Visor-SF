package prolog;

import prolog.PRED_matrix_transpose_2;
import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>map_list_fn/3</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_map_list_fn_3 extends Predicate {

    public Term arg1, arg2, arg3;

    public PRED_map_list_fn_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_map_list_fn_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "map_list_fn(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
    // map_list_fn(A,B,C):-map_list_fn_helper(A,B,D),matrix_transpose(D,C)
        engine.setB0();
        Term a1, a2, a3, a4;
        Predicate p1;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    // map_list_fn(A,B,C):-[map_list_fn_helper(A,B,D),matrix_transpose(D,C)]
        a4 = new VariableTerm(engine);
        p1 = new PRED_matrix_transpose_2(a4, a3, cont);
        return new PRED_map_list_fn_helper_3(a1, a2, a4, p1);
    }
}
