import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_attr_statistics/0</code> defined in rule_initialize.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_attr_statistics_0 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("density");
    static IntegerTerm si2 = new IntegerTerm(5);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("[]");
    static ListTerm s4 = new ListTerm(si2, s3);
    static ListTerm s5 = new ListTerm(s1, s4);
    static SymbolTerm s6 = SymbolTerm.makeSymbol("leap_size");
    static IntegerTerm si7 = new IntegerTerm(4);
    static ListTerm s8 = new ListTerm(si7, s3);
    static ListTerm s9 = new ListTerm(s6, s8);
    static ListTerm s10 = new ListTerm(s9, s3);
    static ListTerm s11 = new ListTerm(s5, s10);
    static ListTerm s12 = new ListTerm(s1, s8);
    static ListTerm s13 = new ListTerm(s6, s4);
    static ListTerm s14 = new ListTerm(s13, s3);
    static ListTerm s15 = new ListTerm(s12, s14);
    static IntegerTerm si16 = new IntegerTerm(3);
    static ListTerm s17 = new ListTerm(si16, s3);
    static ListTerm s18 = new ListTerm(s1, s17);
    static IntegerTerm si19 = new IntegerTerm(6);
    static ListTerm s20 = new ListTerm(si19, s3);
    static ListTerm s21 = new ListTerm(s6, s20);
    static ListTerm s22 = new ListTerm(s21, s3);
    static ListTerm s23 = new ListTerm(s18, s22);
    static ListTerm s24 = new ListTerm(s23, s3);
    static ListTerm s25 = new ListTerm(s15, s24);
    static ListTerm s26 = new ListTerm(s11, s25);
    static SymbolTerm s27 = SymbolTerm.makeSymbol("median");
    static SymbolTerm s28 = SymbolTerm.makeSymbol("results: ");
    static SymbolTerm s29 = SymbolTerm.makeSymbol("types: ");

    public PRED_test_attr_statistics_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_attr_statistics_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_attr_statistics";
    }

    public Predicate exec(Prolog engine) {
    // test_attr_statistics:-attr_statistics([[[density,5],[leap_size,4]],[[density,4],[leap_size,5]],[[density,3],[leap_size,6]]],median,A,B),write('results: '),writeln(A),write('types: '),write(B)
        engine.setB0();
        Term a1, a2;
        Predicate p1, p2, p3, p4;
    // test_attr_statistics:-[attr_statistics([[[density,5],[leap_size,4]],[[density,4],[leap_size,5]],[[density,3],[leap_size,6]]],median,A,B),write('results: '),writeln(A),write('types: '),write(B)]
        a1 = new VariableTerm(engine);
        a2 = new VariableTerm(engine);
        p1 = new PRED_write_1(a2, cont);
        p2 = new PRED_write_1(s29, p1);
        p3 = new PRED_writeln_1(a1, p2);
        p4 = new PRED_write_1(s28, p3);
        return new PRED_attr_statistics_4(s26, s27, a1, a2, p4);
    }
}
