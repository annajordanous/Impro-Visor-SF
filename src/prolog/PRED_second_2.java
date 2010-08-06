import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>second/2</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_second_2 extends Predicate {

    public Term arg1, arg2;

    public PRED_second_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_second_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "second(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // second(A,B):-rest(A,C),first(C,B)
        engine.setB0();
        Term a1, a2, a3;
        Predicate p1;
        a1 = arg1;
        a2 = arg2;
    // second(A,B):-[rest(A,C),first(C,B)]
        a3 = new VariableTerm(engine);
        p1 = new PRED_first_2(a3, a2, cont);
        return new PRED_rest_2(a1, a3, p1);
    }
}
