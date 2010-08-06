import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>assoc/3</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_assoc_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");

    public Term arg1, arg2, arg3;

    public PRED_assoc_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_assoc_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "assoc(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
    // assoc(A,[[B,C]|D],E):-'$dummy_12_functional.pro'(C,B,E,A,D)
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6, a7, a8;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
    // assoc(A,[[B,C]|D],E):-['$dummy_12_functional.pro'(C,B,E,A,D)]
        a2 = a2.dereference();
        if (a2.isList()){
            Term[] args = {((ListTerm)a2).car(), ((ListTerm)a2).cdr()};
            a4 = args[0];
            a5 = args[1];
        } else if (a2.isVariable()){
            a4 = new VariableTerm(engine);
            a5 = new VariableTerm(engine);
            ((VariableTerm) a2).bind(new ListTerm(a4, a5), engine.trail);
        } else {
            return engine.fail();
        }
        a4 = a4.dereference();
        if (a4.isList()){
            Term[] args = {((ListTerm)a4).car(), ((ListTerm)a4).cdr()};
            a6 = args[0];
            a7 = args[1];
        } else if (a4.isVariable()){
            a6 = new VariableTerm(engine);
            a7 = new VariableTerm(engine);
            ((VariableTerm) a4).bind(new ListTerm(a6, a7), engine.trail);
        } else {
            return engine.fail();
        }
        a7 = a7.dereference();
        if (a7.isList()){
            Term[] args = {((ListTerm)a7).car(), ((ListTerm)a7).cdr()};
            a8 = args[0];
            if (! s1.unify(args[1], engine.trail))
                return engine.fail();
        } else if (a7.isVariable()){
            a8 = new VariableTerm(engine);
            ((VariableTerm) a7).bind(new ListTerm(a8, s1), engine.trail);
        } else {
            return engine.fail();
        }
        return new PRED_$dummy_12_functional$002Epro_5(a8, a6, a3, a1, a5, cont);
    }
}
