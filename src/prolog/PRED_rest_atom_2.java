import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>rest_atom/2</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_rest_atom_2 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(0);

    public Term arg1, arg2;

    public PRED_rest_atom_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_rest_atom_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "rest_atom(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // rest_atom(A,B):-sub_atom(A,1,C,0,B)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
        a2 = arg2;
    // rest_atom(A,B):-[sub_atom(A,1,C,0,B)]
        return new PRED_sub_atom_5(a1, si1, new VariableTerm(engine), si2, a2, cont);
    }
}
