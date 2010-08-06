package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>slope_leap_size/2</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_slope_leap_size_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("slope", 3);

    public Term arg1, arg2;

    public PRED_slope_leap_size_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_slope_leap_size_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "slope_leap_size(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // slope_leap_size(slope(A,B,C),D):-slope_leap_avg(A,B,D)
        engine.setB0();
        Term a1, a2, a3, a4;
        a1 = arg1;
        a2 = arg2;
    // slope_leap_size(slope(A,B,C),D):-[slope_leap_avg(A,B,D)]
        a1 = a1.dereference();
        if (a1.isStructure()){
            if (! s1.equals(((StructureTerm)a1).functor()))
                return engine.fail();
            Term[] args = ((StructureTerm)a1).args();
            a3 = args[0];
            a4 = args[1];
        } else if (a1.isVariable()){
            a3 = new VariableTerm(engine);
            a4 = new VariableTerm(engine);
            Term[] args = {a3, a4, new VariableTerm(engine)};
            ((VariableTerm) a1).bind(new StructureTerm(s1, args), engine.trail);
        } else {
            return engine.fail();
        }
        return new PRED_slope_leap_avg_3(a3, a4, a2, cont);
    }
}
