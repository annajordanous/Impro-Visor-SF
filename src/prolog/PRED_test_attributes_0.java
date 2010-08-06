import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>test_attributes/0</code> defined in attributes.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_test_attributes_0 extends Predicate {

    public PRED_test_attributes_0(Predicate cont) {
        this.cont = cont;
    }

    public PRED_test_attributes_0(){}

    public void setArgument(Term[] args, Predicate cont) {
        this.cont = cont;
    }

    public int arity() { return 0; }

    public String toString() {
        return "test_attributes";
    }

    public Predicate exec(Prolog engine) {
    // test_attributes:-test_density,test_variety,test_consonance,test_leap,test_dir_change
        engine.setB0();
        Predicate p1, p2, p3, p4;
    // test_attributes:-[test_density,test_variety,test_consonance,test_leap,test_dir_change]
        p1 = new PRED_test_dir_change_0(cont);
        p2 = new PRED_test_leap_0(p1);
        p3 = new PRED_test_consonance_0(p2);
        p4 = new PRED_test_variety_0(p3);
        return new PRED_test_density_0(p4);
    }
}
