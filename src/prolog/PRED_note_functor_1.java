package prolog;

import prolog.PRED_valid_note_type_1;
import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>note_functor/1</code> defined in rule_database.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_note_functor_1 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("t", 2);

    public Term arg1;

    public PRED_note_functor_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_note_functor_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "note_functor(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // note_functor(t(A,B)):-valid_note_type(A)
        engine.setB0();
        Term a1, a2;
        a1 = arg1;
    // note_functor(t(A,B)):-[valid_note_type(A)]
        a1 = a1.dereference();
        if (a1.isStructure()){
            if (! s1.equals(((StructureTerm)a1).functor()))
                return engine.fail();
            Term[] args = ((StructureTerm)a1).args();
            a2 = args[0];
        } else if (a1.isVariable()){
            a2 = new VariableTerm(engine);
            Term[] args = {a2, new VariableTerm(engine)};
            ((VariableTerm) a1).bind(new StructureTerm(s1, args), engine.trail);
        } else {
            return engine.fail();
        }
        return new PRED_valid_note_type_1(a2, cont);
    }
}
