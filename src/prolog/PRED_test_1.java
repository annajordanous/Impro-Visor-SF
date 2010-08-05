package prolog;

import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test/1</code> defined in tester.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_1 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol(":", 2);
    static SymbolTerm s2 = SymbolTerm.makeSymbol("user");
    static SymbolTerm s3 = SymbolTerm.makeSymbol("-- failed!");
    static Predicate _test_1_sub_1 = new PRED_test_1_sub_1();
    static Predicate _test_1_1 = new PRED_test_1_1();
    static Predicate _test_1_2 = new PRED_test_1_2();

    public Term arg1;

    public PRED_test_1(Term a1, Predicate cont) {
        arg1 = a1;
        this.cont = cont;
    }

    public PRED_test_1(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        this.cont = cont;
    }

    public int arity() { return 1; }

    public String toString() {
        return "test(" + arg1 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_test_1_1, _test_1_sub_1);
    }
}

class PRED_test_1_sub_1 extends PRED_test_1 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_test_1_2);
    }
}

class PRED_test_1_1 extends PRED_test_1 {
    public Predicate exec(Prolog engine) {
    // test(A):-writeln(A),A,!
        Term a1, a2, a3;
        Predicate p1, p2;
        Predicate cont;
        a1 = engine.aregs[1];
        cont = engine.cont;
    // test(A):-['$get_level'(B),writeln(A),call(user:A),'$cut'(B)]
        a2 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(2))
        if (! a2.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        Term[] y1 = {s2, a1};
        a3 = new StructureTerm(s1, y1);
        p1 = new PRED_$cut_1(a2, cont);
        p2 = new PRED_call_1(a3, p1);
        return new PRED_writeln_1(a1, p2);
    }
}

class PRED_test_1_2 extends PRED_test_1 {
    public Predicate exec(Prolog engine) {
    // test(A):-writeln('-- failed!')
        Term a1;
        Predicate cont;
        a1 = engine.aregs[1];
        cont = engine.cont;
    // test(A):-[writeln('-- failed!')]
        return new PRED_writeln_1(s3, cont);
    }
}
