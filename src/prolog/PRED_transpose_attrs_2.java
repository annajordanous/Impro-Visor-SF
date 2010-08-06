import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>transpose_attrs/2</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_transpose_attrs_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("user");
    static SymbolTerm s3 = SymbolTerm.makeSymbol("attribute", 1);

    public Term arg1, arg2;

    public PRED_transpose_attrs_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_transpose_attrs_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "transpose_attrs(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // transpose_attrs(A,B):-bagof(C,attribute(C),D),grab_assocs(D,A,B)
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6;
        Predicate p1;
        a1 = arg1;
        a2 = arg2;
    // transpose_attrs(A,B):-[bagof(C,user:attribute(C),D),grab_assocs(D,A,B)]
        a3 = new VariableTerm(engine);
        Term[] y1 = {a3};
        a4 = new StructureTerm(s3, y1);
        Term[] y2 = {s2, a4};
        a5 = new StructureTerm(s1, y2);
        a6 = new VariableTerm(engine);
        p1 = new PRED_grab_assocs_3(a6, a1, a2, cont);
        return new PRED_bagof_3(a3, a5, a6, p1);
    }
}
