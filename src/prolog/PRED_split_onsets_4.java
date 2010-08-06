import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>split_onsets/4</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_split_onsets_4 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(0);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("[]");

    public Term arg1, arg2, arg3, arg4;

    public PRED_split_onsets_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_split_onsets_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "split_onsets(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
    // split_onsets(A,B,C,D):-number(B),B>=0,split_onset_helper(A,B,[],C,E),'$dummy_4_longuet_higgins.pro'(F,D,B,G,E)
        engine.setB0();
        Term a1, a2, a3, a4, a5;
        Predicate p1;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
        a4 = arg4;
    // split_onsets(A,B,C,D):-[number(B),'$greater_or_equal'(B,0),split_onset_helper(A,B,[],C,E),'$dummy_4_longuet_higgins.pro'(F,D,B,G,E)]
        //START inline expansion of number(a(2))
        a2 = a2.dereference();
        if (! a2.isNumber()) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $greater_or_equal(a(2),si(1))
        try {
            if (Arithmetic.evaluate(a2).arithCompareTo(si1) < 0) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        a5 = new VariableTerm(engine);
        p1 = new PRED_$dummy_4_longuet_higgins$002Epro_5(new VariableTerm(engine), a4, a2, new VariableTerm(engine), a5, cont);
        return new PRED_split_onset_helper_5(a1, a2, s2, a3, a5, p1);
    }
}
