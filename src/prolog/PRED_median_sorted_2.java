import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>median_sorted/2</code> defined in statistics.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_median_sorted_2 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(2);

    public Term arg1, arg2;

    public PRED_median_sorted_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_median_sorted_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "median_sorted(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // median_sorted(A,B):-length(A,C),D is C//2,nth0(D,A,B)
        engine.setB0();
        Term a1, a2, a3, a4;
        Predicate p1, p2;
        a1 = arg1;
        a2 = arg2;
    // median_sorted(A,B):-[length(A,C),'$int_quotient'(C,2,D),nth0(D,A,B)]
        a3 = new VariableTerm(engine);
        a4 = new VariableTerm(engine);
        p1 = new PRED_nth0_3(a4, a1, a2, cont);
        p2 = new PRED_$int_quotient_3(a3, si1, a4, p1);
        return new PRED_length_2(a1, a3, p2);
    }
}
