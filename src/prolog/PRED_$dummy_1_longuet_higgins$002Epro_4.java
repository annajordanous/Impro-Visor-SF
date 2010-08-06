import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_1_longuet_higgins.pro'/4</code> defined in longuet_higgins.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_1_longuet_higgins$002Epro_4 extends Predicate {
    static IntegerTerm si1 = new IntegerTerm(1);
    static IntegerTerm si2 = new IntegerTerm(0);
    static Predicate _$dummy_1_longuet_higgins$002Epro_4_sub_1 = new PRED_$dummy_1_longuet_higgins$002Epro_4_sub_1();
    static Predicate _$dummy_1_longuet_higgins$002Epro_4_1 = new PRED_$dummy_1_longuet_higgins$002Epro_4_1();
    static Predicate _$dummy_1_longuet_higgins$002Epro_4_2 = new PRED_$dummy_1_longuet_higgins$002Epro_4_2();

    public Term arg1, arg2, arg3, arg4;

    public PRED_$dummy_1_longuet_higgins$002Epro_4(Term a1, Term a2, Term a3, Term a4, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        arg4 = a4;
        this.cont = cont;
    }

    public PRED_$dummy_1_longuet_higgins$002Epro_4(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        arg4 = args[3];
        this.cont = cont;
    }

    public int arity() { return 4; }

    public String toString() {
        return "$dummy_1_longuet_higgins.pro(" + arg1 + "," + arg2 + "," + arg3 + "," + arg4 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.aregs[4] = arg4;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_1_longuet_higgins$002Epro_4_1, _$dummy_1_longuet_higgins$002Epro_4_sub_1);
    }
}

class PRED_$dummy_1_longuet_higgins$002Epro_4_sub_1 extends PRED_$dummy_1_longuet_higgins$002Epro_4 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_1_longuet_higgins$002Epro_4_2);
    }
}

class PRED_$dummy_1_longuet_higgins$002Epro_4_1 extends PRED_$dummy_1_longuet_higgins$002Epro_4 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_longuet_higgins.pro'(A,B,1,C):-B>C,!,A is B-C,1 is 1
        Term a1, a2, a3, a4, a5;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // '$dummy_1_longuet_higgins.pro'(A,B,1,C):-['$get_level'(D),'$greater_than'(B,C),'$cut'(D),'$minus'(B,C,A)]
        a3 = a3.dereference();
        if (a3.isInteger()){
            if (((IntegerTerm) a3).intValue() != 1)
                return engine.fail();
        } else if (a3.isVariable()){
            ((VariableTerm) a3).bind(si1, engine.trail);
        } else {
            return engine.fail();
        }
        a5 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(5))
        if (! a5.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $greater_than(a(2),a(4))
        try {
            if (Arithmetic.evaluate(a2).arithCompareTo(Arithmetic.evaluate(a4)) <= 0) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        //START inline expansion of $cut(a(5))
        a5 = a5.dereference();
        if (! a5.isInteger()) {
            throw new IllegalTypeException("integer", a5);
        } else {
            engine.cut(((IntegerTerm) a5).intValue());
        }
        //END inline expansion
        //START inline expansion of $minus(a(2),a(4),a(1))
        try {
            if (! a1.unify(Arithmetic.evaluate(a2).subtract(Arithmetic.evaluate(a4)), engine.trail)) {
                return engine.fail();
            }
        } catch (BuiltinException e) {
            e.goal = this;
            throw e;
        }
        //END inline expansion
        return cont;
    }
}

class PRED_$dummy_1_longuet_higgins$002Epro_4_2 extends PRED_$dummy_1_longuet_higgins$002Epro_4 {
    public Predicate exec(Prolog engine) {
    // '$dummy_1_longuet_higgins.pro'(0,A,0,B):-0 is 0,0 is 0
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        a4 = engine.aregs[4];
        cont = engine.cont;
    // '$dummy_1_longuet_higgins.pro'(0,A,0,B):-[]
        a1 = a1.dereference();
        if (a1.isInteger()){
            if (((IntegerTerm) a1).intValue() != 0)
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(si2, engine.trail);
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
