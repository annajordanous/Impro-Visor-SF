package prolog;













import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>slope_direction_change/3</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_slope_direction_change_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("slope", 3);

    public Term arg1, arg2, arg3;

    public PRED_slope_direction_change_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_slope_direction_change_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "slope_direction_change(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
    // slope_direction_change(A,B,C):-A=slope(D,E,F),B=slope(G,H,I),J is D*G,negativity(J,C)
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    // slope_direction_change(A,B,C):-['$unify'(A,slope(D,E,F)),'$unify'(B,slope(G,H,I)),'$multi'(D,G,J),negativity(J,C)]
        a4 = new VariableTerm(engine);
        Term[] y1 = {a4, new VariableTerm(engine), new VariableTerm(engine)};
        a5 = new StructureTerm(s1, y1);
        //START inline expansion of $unify(a(1),a(5))
        if (! a1.unify(a5, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a6 = new VariableTerm(engine);
        Term[] y2 = {a6, new VariableTerm(engine), new VariableTerm(engine)};
        a7 = new StructureTerm(s1, y2);
        //START inline expansion of $unify(a(2),a(7))
        if (! a2.unify(a7, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        a8 = new VariableTerm(engine);
        //START inline expansion of $multi(a(4),a(6),a(8))
        try {
            if (! a8.unify(Arithmetic.evaluate(a4).multiply(Arithmetic.evaluate(a6)), engine.trail)) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        return new PRED_negativity_2(a8, a3, cont);
    }
}
