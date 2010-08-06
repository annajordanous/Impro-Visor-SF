import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_16_attributes.pro'/3</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_16_attributes$002Epro_3 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("r");
    static IntegerTerm si2 = new IntegerTerm(0);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("/", 2);
    static IntegerTerm si4 = new IntegerTerm(1);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("*", 2);
    static Predicate _$dummy_16_attributes$002Epro_3_sub_1 = new PRED_$dummy_16_attributes$002Epro_3_sub_1();
    static Predicate _$dummy_16_attributes$002Epro_3_1 = new PRED_$dummy_16_attributes$002Epro_3_1();
    static Predicate _$dummy_16_attributes$002Epro_3_2 = new PRED_$dummy_16_attributes$002Epro_3_2();

    public Term arg1, arg2, arg3;

    public PRED_$dummy_16_attributes$002Epro_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_$dummy_16_attributes$002Epro_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "$dummy_16_attributes.pro(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_16_attributes$002Epro_3_1, _$dummy_16_attributes$002Epro_3_sub_1);
    }
}

class PRED_$dummy_16_attributes$002Epro_3_sub_1 extends PRED_$dummy_16_attributes$002Epro_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_16_attributes$002Epro_3_2);
    }
}

class PRED_$dummy_16_attributes$002Epro_3_1 extends PRED_$dummy_16_attributes$002Epro_3 {
    public Predicate exec(Prolog engine) {
    // '$dummy_16_attributes.pro'(A,B,C):-B=r,!,A=0
        Term a1, a2, a3, a4;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // '$dummy_16_attributes.pro'(A,B,C):-['$get_level'(D),'$unify'(B,r),'$cut'(D),'$unify'(A,0)]
        a4 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(4))
        if (! a4.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $unify(a(2),s(1))
        if (! a2.unify(s1, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of $cut(a(4))
        a4 = a4.dereference();
        if (! a4.isInteger()) {
            throw new IllegalTypeException("integer", a4);
        } else {
            engine.cut(((IntegerTerm) a4).intValue());
        }
        //END inline expansion
        //START inline expansion of $unify(a(1),si(2))
        if (! a1.unify(si2, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}

class PRED_$dummy_16_attributes$002Epro_3_2 extends PRED_$dummy_16_attributes$002Epro_3 {
    public Predicate exec(Prolog engine) {
    // '$dummy_16_attributes.pro'(A,B,C):-A=1/(C*C)
        Term a1, a2, a3, a4, a5;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // '$dummy_16_attributes.pro'(A,B,C):-['$unify'(A,1/(C*C))]
        Term[] y1 = {a3, a3};
        a4 = new StructureTerm(s5, y1);
        Term[] y2 = {si4, a4};
        a5 = new StructureTerm(s3, y2);
        //START inline expansion of $unify(a(1),a(5))
        if (! a1.unify(a5, engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        return cont;
    }
}
