import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_take_while/0</code> defined in functional.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_take_while_0 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(2);
    static IntegerTerm si2 = new IntegerTerm(4);
    static IntegerTerm si3 = new IntegerTerm(6);
    static IntegerTerm si4 = new IntegerTerm(7);
    static IntegerTerm si5 = new IntegerTerm(8);
    static SymbolTerm s6 = SymbolTerm.makeSymbol("[]");
    static ListTerm s7 = new ListTerm(si5, s6);
    static ListTerm s8 = new ListTerm(si4, s7);
    static ListTerm s9 = new ListTerm(si3, s8);
    static ListTerm s10 = new ListTerm(si2, s9);
    static ListTerm s11 = new ListTerm(si1, s10);
    static SymbolTerm s12 = SymbolTerm.makeSymbol("even");
    static ListTerm s13 = new ListTerm(si3, s6);
    static ListTerm s14 = new ListTerm(si2, s13);
    static ListTerm s15 = new ListTerm(si1, s14);
    static IntegerTerm si16 = new IntegerTerm(1);
    static ListTerm s17 = new ListTerm(si2, s6);
    static ListTerm s18 = new ListTerm(si1, s17);
    static ListTerm s19 = new ListTerm(si16, s18);
    static ListTerm s20 = new ListTerm(si1, s6);

    public PRED_test_take_while_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_take_while_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_take_while";
    }

    public Predicate exec(Prolog engine) {
    // test_take_while:-take_while([2,4,6,7,8],even,[2,4,6],[7,8]),take_while([1,2,4],even,[],[1,2,4]),take_while([2],even,[2],[])
        engine.setB0();
        Predicate p1, p2;
    // test_take_while:-[take_while([2,4,6,7,8],even,[2,4,6],[7,8]),take_while([1,2,4],even,[],[1,2,4]),take_while([2],even,[2],[])]
        p1 = new PRED_take_while_4(s20, s12, s20, s6, cont);
        p2 = new PRED_take_while_4(s19, s12, s6, s19, p1);
        return new PRED_take_while_4(s11, s12, s15, s8, p2);
    }
}
