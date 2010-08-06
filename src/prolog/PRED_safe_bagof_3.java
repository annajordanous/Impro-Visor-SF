import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>safe_bagof/3</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_safe_bagof_3 extends Predicate {

    public Term arg1, arg2, arg3;

    public PRED_safe_bagof_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_safe_bagof_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "safe_bagof(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
    // safe_bagof(A,B,C):-'$dummy_20_functional.pro'(B,A,C)
        engine.setB0();
        Term a1, a2, a3;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    // safe_bagof(A,B,C):-['$dummy_20_functional.pro'(B,A,C)]
        return new PRED_$dummy_20_functional$002Epro_3(a2, a1, a3, cont);
    }
}
