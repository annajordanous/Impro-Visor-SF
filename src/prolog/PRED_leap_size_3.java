import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>leap_size/3</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_leap_size_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("[]");
    static IntegerTerm si2 = new IntegerTerm(0);
    static Predicate _leap_size_3_sub_1 = new PRED_leap_size_3_sub_1();
    static Predicate _leap_size_3_1 = new PRED_leap_size_3_1();
    static Predicate _leap_size_3_2 = new PRED_leap_size_3_2();

    public Term arg1, arg2, arg3;

    public PRED_leap_size_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_leap_size_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "leap_size(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_leap_size_3_1, _leap_size_3_sub_1);
    }
}

class PRED_leap_size_3_sub_1 extends PRED_leap_size_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_leap_size_3_2);
    }
}

class PRED_leap_size_3_1 extends PRED_leap_size_3 {
    public Predicate exec(Prolog engine) {
    // leap_size(A,[],0):-true
        Term a1, a2, a3;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // leap_size(A,[],0):-[]
        a2 = a2.dereference();
        if (a2.isSymbol()){
            if (! a2.equals(s1))
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        a3 = a3.dereference();
        if (a3.isInteger()){
            if (((IntegerTerm) a3).intValue() != 0)
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(si2, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_leap_size_3_2 extends PRED_leap_size_3 {
    public Predicate exec(Prolog engine) {
    // leap_size(A,B,C):-leap_size_helper(B,D),C is D/A
        Term a1, a2, a3, a4;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // leap_size(A,B,C):-[leap_size_helper(B,D),'$float_quotient'(D,A,C)]
        a4 = new VariableTerm(engine);
        p1 = new PRED_$float_quotient_3(a4, a1, a3, cont);
        return new PRED_leap_size_helper_2(a2, a4, p1);
    }
}
