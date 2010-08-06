import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>note/1</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_note_1 extends Predicate {

    public Term arg1;

    public PRED_note_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_note_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "note(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // note(A):-atom(A),first_atom(A,B),valid_note_type(B),note_duration(A,C)
        engine.setB0();
        Term a1, a2;
        Predicate p1, p2;
        a1 = arg1;
    // note(A):-[atom(A),first_atom(A,B),valid_note_type(B),note_duration(A,C)]
        //START inline expansion of atom(a(1))
        a1 = a1.dereference();
        if (! a1.isSymbol()) {
            return engine.fail();
        }
        //END inline expansion
        a2 = new VariableTerm(engine);
        p1 = new PRED_note_duration_2(a1, new VariableTerm(engine), cont);
        p2 = new PRED_valid_note_type_1(a2, p1);
        return new PRED_first_atom_2(a1, a2, p2);
    }
}
