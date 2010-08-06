import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_matrix_transpose/1</code> defined in transpose.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_matrix_transpose_1 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("matrix_transpose of ");
    static SymbolTerm s2 = SymbolTerm.makeSymbol("         is ");

    public Term arg1;

    public PRED_test_matrix_transpose_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_test_matrix_transpose_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "test_matrix_transpose(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
    // test_matrix_transpose(A):-matrix_transpose(A,B),write('matrix_transpose of '),write(A),nl,write('         is '),write(B),nl,nl
        engine.setB0();
        Term a1, a2;
        Predicate p1, p2, p3, p4, p5, p6, p7;
        a1 = arg1;
    // test_matrix_transpose(A):-[matrix_transpose(A,B),write('matrix_transpose of '),write(A),nl,write('         is '),write(B),nl,nl]
        a2 = new VariableTerm(engine);
        p1 = new PRED_nl_0(cont);
        p2 = new PRED_nl_0(p1);
        p3 = new PRED_write_1(a2, p2);
        p4 = new PRED_write_1(s2, p3);
        p5 = new PRED_nl_0(p4);
        p6 = new PRED_write_1(a1, p5);
        p7 = new PRED_write_1(s1, p6);
        return new PRED_matrix_transpose_2(a1, a2, p7);
    }
}
