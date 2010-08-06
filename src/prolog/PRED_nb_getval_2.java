import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>nb_getval/2</code> defined in plcafe_defs.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_nb_getval_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("user");
    static SymbolTerm s3 = SymbolTerm.makeSymbol("setvalues", 2);

    public Term arg1, arg2;

    public PRED_nb_getval_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_nb_getval_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "nb_getval(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // nb_getval(A,B):-setvalues(A,B)
        engine.setB0();
        Term a1, a2, a3, a4;
        a1 = arg1;
        a2 = arg2;
    // nb_getval(A,B):-[call(user:setvalues(A,B))]
        Term[] y1 = {a1, a2};
        a3 = new StructureTerm(s3, y1);
        Term[] y2 = {s2, a3};
        a4 = new StructureTerm(s1, y2);
        return new PRED_call_1(a4, cont);
    }
}
