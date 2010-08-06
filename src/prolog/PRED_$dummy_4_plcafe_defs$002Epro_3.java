import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>'$dummy_4_plcafe_defs.pro'/3</code> defined in plcafe_defs.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_$dummy_4_plcafe_defs$002Epro_3 extends Predicate {
    static Predicate _$dummy_4_plcafe_defs$002Epro_3_sub_1 = new PRED_$dummy_4_plcafe_defs$002Epro_3_sub_1();
    static Predicate _$dummy_4_plcafe_defs$002Epro_3_1 = new PRED_$dummy_4_plcafe_defs$002Epro_3_1();
    static Predicate _$dummy_4_plcafe_defs$002Epro_3_2 = new PRED_$dummy_4_plcafe_defs$002Epro_3_2();

    public Term arg1, arg2, arg3;

    public PRED_$dummy_4_plcafe_defs$002Epro_3(Term a1, Term a2, Term a3, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        arg3 = a3;
        this.cont = cont;
    }

    public PRED_$dummy_4_plcafe_defs$002Epro_3(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        arg3 = args[2];
        this.cont = cont;
    }

    public int arity() { return 3; }

    public String toString() {
        return "$dummy_4_plcafe_defs.pro(" + arg1 + "," + arg2 + "," + arg3 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.aregs[3] = arg3;
        engine.cont = cont;
        engine.setB0();
        return engine.jtry(_$dummy_4_plcafe_defs$002Epro_3_1, _$dummy_4_plcafe_defs$002Epro_3_sub_1);
    }
}

class PRED_$dummy_4_plcafe_defs$002Epro_3_sub_1 extends PRED_$dummy_4_plcafe_defs$002Epro_3 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_$dummy_4_plcafe_defs$002Epro_3_2);
    }
}

class PRED_$dummy_4_plcafe_defs$002Epro_3_1 extends PRED_$dummy_4_plcafe_defs$002Epro_3 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_plcafe_defs.pro'(A,B,C):-number(C),!,number_chars(C,B),atom_chars(A,B)
        Term a1, a2, a3, a4;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // '$dummy_4_plcafe_defs.pro'(A,B,C):-['$get_level'(D),number(C),'$cut'(D),number_chars(C,B),atom_chars(A,B)]
        a4 = new VariableTerm(engine);
        //START inline expansion of $get_level(a(4))
        if (! a4.unify(new IntegerTerm(engine.B0), engine.trail)) {
            return engine.fail();
        }
        //END inline expansion
        //START inline expansion of number(a(3))
        a3 = a3.dereference();
        if (! a3.isNumber()) {
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
        p1 = new PRED_atom_chars_2(a1, a2, cont);
        return new PRED_number_chars_2(a3, a2, p1);
    }
}

class PRED_$dummy_4_plcafe_defs$002Epro_3_2 extends PRED_$dummy_4_plcafe_defs$002Epro_3 {
    public Predicate exec(Prolog engine) {
    // '$dummy_4_plcafe_defs.pro'(A,B,C):-atom_chars(A,B),number_chars(C,B)
        Term a1, a2, a3;
        Predicate p1;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        a3 = engine.aregs[3];
        cont = engine.cont;
    // '$dummy_4_plcafe_defs.pro'(A,B,C):-[atom_chars(A,B),number_chars(C,B)]
        p1 = new PRED_number_chars_2(a3, a2, cont);
        return new PRED_atom_chars_2(a1, a2, p1);
    }
}
