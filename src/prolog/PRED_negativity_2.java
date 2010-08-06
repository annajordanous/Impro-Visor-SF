import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>negativity/2</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_negativity_2 extends Predicate {

    public Term arg1, arg2;

    public PRED_negativity_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_negativity_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "negativity(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // negativity(A,B):-'$dummy_17_attributes.pro'(A,B)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
        a2 = arg2;
    // negativity(A,B):-['$dummy_17_attributes.pro'(A,B)]
        return new PRED_$dummy_17_attributes$002Epro_2(a1, a2, cont);
    }
}
