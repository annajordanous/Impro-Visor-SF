import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>call/5</code> defined in plcafe_defs.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_call_5 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static SymbolTerm s2 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("user");

    public Term arg1, arg2, arg3, arg4, arg5;

    public PRED_call_5(Term a1, Term a2, Term a3, Term a4, Term a5, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        arg5 = a5;
        this.cont = cont;
    }

    public PRED_call_5(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        arg5 = args[4];
        this.cont = cont;
    }

    public int arity() { return 5; }

    public String toString() {
        return "call(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + "," + arg5 + ")";
    }

    public Predicate exec(Prolog engine) {
    // call(A,B,C,D,E):-F=..[A,B,C,D,E],call(F)
        engine.setB0();
        Term a1, a2, a3, a4, a5, a6, a7, a8, a9, a10, a11, a12;
        Predicate p1;
        a1 = arg1;
        a2 = arg2;
        a3 = arg3;
        a4 = arg4;
        a5 = arg5;
    // call(A,B,C,D,E):-['$univ'(F,[A,B,C,D,E]),call(user:F)]
        a6 = new VariableTerm(engine);
        a7 = new ListTerm(a5, s1);
        a8 = new ListTerm(a4, a7);
        a9 = new ListTerm(a3, a8);
        a10 = new ListTerm(a2, a9);
        a11 = new ListTerm(a1, a10);
        Term[] y1 = {s3, a6};
        a12 = new StructureTerm(s2, y1);
        p1 = new PRED_call_1(a12, cont);
        return new PRED_$univ_2(a6, a11, p1);
    }
}
