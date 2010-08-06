import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>note_consonance/2</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_note_consonance_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("x");
    static IntegerTerm si2 = new IntegerTerm(1);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("a");
    static IntegerTerm si4 = new IntegerTerm(0);
    static SymbolTerm s5 = SymbolTerm.makeSymbol("h");
    static SymbolTerm s6 = SymbolTerm.makeSymbol("c");
    static IntegerTerm si7 = new IntegerTerm(3);
    static SymbolTerm s8 = SymbolTerm.makeSymbol("l");
    static SymbolTerm s9 = SymbolTerm.makeSymbol("s");
    static IntegerTerm si10 = new IntegerTerm(2);
    static SymbolTerm s11 = SymbolTerm.makeSymbol("r");
    static Predicate _fail_0 = new PRED_fail_0();
    static Predicate _note_consonance_2_var = new PRED_note_consonance_2_var();
    static Predicate _note_consonance_2_var_1 = new PRED_note_consonance_2_var_1();
    static Predicate _note_consonance_2_var_2 = new PRED_note_consonance_2_var_2();
    static Predicate _note_consonance_2_var_3 = new PRED_note_consonance_2_var_3();
    static Predicate _note_consonance_2_var_4 = new PRED_note_consonance_2_var_4();
    static Predicate _note_consonance_2_var_5 = new PRED_note_consonance_2_var_5();
    static Predicate _note_consonance_2_var_6 = new PRED_note_consonance_2_var_6();
    static Predicate _note_consonance_2_con = new PRED_note_consonance_2_con();
    static Predicate _note_consonance_2_1 = new PRED_note_consonance_2_1();
    static Predicate _note_consonance_2_2 = new PRED_note_consonance_2_2();
    static Predicate _note_consonance_2_3 = new PRED_note_consonance_2_3();
    static Predicate _note_consonance_2_4 = new PRED_note_consonance_2_4();
    static Predicate _note_consonance_2_5 = new PRED_note_consonance_2_5();
    static Predicate _note_consonance_2_6 = new PRED_note_consonance_2_6();
    static Predicate _note_consonance_2_7 = new PRED_note_consonance_2_7();
    static java.util.Hashtable<Term, Predicate> con = new java.util.Hashtable<Term, Predicate>(7);
    static {
        con.put(s1, _note_consonance_2_1);
        con.put(s3, _note_consonance_2_2);
        con.put(s5, _note_consonance_2_3);
        con.put(s6, _note_consonance_2_4);
        con.put(s8, _note_consonance_2_5);
        con.put(s9, _note_consonance_2_6);
        con.put(s11, _note_consonance_2_7);
    }

    public Term arg1, arg2;

    public PRED_note_consonance_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_note_consonance_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "note_consonance(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
        engine.aregs[1] = arg1;
        engine.aregs[2] = arg2;
        engine.cont = cont;
        engine.setB0();
        return engine.switch_on_term(_note_consonance_2_var, _fail_0, _fail_0, _note_consonance_2_con, _fail_0, _fail_0);
    }
}

class PRED_note_consonance_2_var extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.jtry(_note_consonance_2_1, _note_consonance_2_var_1);
    }
}

class PRED_note_consonance_2_var_1 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_note_consonance_2_2, _note_consonance_2_var_2);
    }
}

class PRED_note_consonance_2_var_2 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_note_consonance_2_3, _note_consonance_2_var_3);
    }
}

class PRED_note_consonance_2_var_3 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_note_consonance_2_4, _note_consonance_2_var_4);
    }
}

class PRED_note_consonance_2_var_4 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_note_consonance_2_5, _note_consonance_2_var_5);
    }
}

class PRED_note_consonance_2_var_5 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.retry(_note_consonance_2_6, _note_consonance_2_var_6);
    }
}

class PRED_note_consonance_2_var_6 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.trust(_note_consonance_2_7);
    }
}

class PRED_note_consonance_2_con extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
        return engine.switch_on_hash(con, _fail_0);
    }
}

class PRED_note_consonance_2_1 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(x,1):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(x,1):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s1))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s1, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 1)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si2, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_note_consonance_2_2 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(a,0):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(a,0):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s3))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s3, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 0)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si4, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_note_consonance_2_3 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(h,1):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(h,1):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s5))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s5, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 1)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si2, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_note_consonance_2_4 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(c,3):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(c,3):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s6))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s6, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 3)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si7, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_note_consonance_2_5 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(l,1):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(l,1):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s8))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s8, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 1)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si2, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_note_consonance_2_6 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(s,2):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(s,2):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s9))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s9, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 2)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si10, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}

class PRED_note_consonance_2_7 extends PRED_note_consonance_2 {
    public Predicate exec(Prolog engine) {
    // note_consonance(r,3):-true
        Term a1, a2;
        Predicate cont;
        a1 = engine.aregs[1];
        a2 = engine.aregs[2];
        cont = engine.cont;
    // note_consonance(r,3):-[]
        a1 = a1.dereference();
        if (a1.isSymbol()){
            if (! a1.equals(s11))
                return engine.fail();
        } else if (a1.isVariable()){
            ((VariableTerm) a1).bind(s11, engine.trail);
        } else {
            return engine.fail();
        }
        a2 = a2.dereference();
        if (a2.isInteger()){
            if (((IntegerTerm) a2).intValue() != 3)
                return engine.fail();
        } else if (a2.isVariable()){
            ((VariableTerm) a2).bind(si7, engine.trail);
        } else {
            return engine.fail();
        }
        return cont;
    }
}
