import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>desired_attrs/1</code> defined in rule_expander.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_desired_attrs_1 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("density");
    static DoubleTerm sf2 = new DoubleTerm(9.0E-4);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("[]");
    static ListTerm s4 = new ListTerm(sf2, s3);
    static ListTerm s5 = new ListTerm(s1, s4);
    static ListTerm s6 = new ListTerm(s5, s3);

    public Term arg1;

    public PRED_desired_attrs_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_desired_attrs_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "desired_attrs(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // desired_attrs([[density,9.0E-4]]):-true
        engine.setB0();
        Term a1;
        a1 = arg1;
    // desired_attrs([[density,9.0E-4]]):-[]
        if (! s6.unify(a1, engine.trail))
            return engine.fail();
        return cont;
    }
}
