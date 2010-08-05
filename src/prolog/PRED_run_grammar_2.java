package prolog;













import jp.ac.kobe_u.cs.prolog.lang.*;
import jp.ac.kobe_u.cs.prolog.builtin.*;
/*
 This file is generated by Prolog Cafe.
 PLEASE DO NOT EDIT!
*/
/**
 <code>run_grammar/2</code> defined in rule_expander.pro<br>
 @author Mutsunori Banbara (banbara@kobe-u.ac.jp)
 @author Naoyuki Tamura (tamura@kobe-u.ac.jp)
 @version 1.0
*/
public class PRED_run_grammar_2 extends Predicate {
    static SymbolTerm s1 = SymbolTerm.makeSymbol("position");
    static IntegerTerm si2 = new IntegerTerm(0);
    static SymbolTerm s3 = SymbolTerm.makeSymbol("p", 1);

    public Term arg1, arg2;

    public PRED_run_grammar_2(Term a1, Term a2, Predicate cont) {
        arg1 = a1;
        arg2 = a2;
        this.cont = cont;
    }

    public PRED_run_grammar_2(){}

    public void setArgument(Term[] args, Predicate cont) {
        arg1 = args[0];
        arg2 = args[1];
        this.cont = cont;
    }

    public int arity() { return 2; }

    public String toString() {
        return "run_grammar(" + arg1 + "," + arg2 + ")";
    }

    public Predicate exec(Prolog engine) {
    // run_grammar(A,B):-all_rules(C),initialize_rules(C,D,E,F),flag(position,G,0),expand_rand(p(A),H,B),write(H),nl
        engine.setB0();
        Term a1, a2, a3, a4, a5;
        Predicate p1, p2, p3, p4, p5;
        a1 = arg1;
        a2 = arg2;
    // run_grammar(A,B):-[all_rules(C),initialize_rules(C,D,E,F),flag(position,G,0),expand_rand(p(A),H,B),write(H),nl]
        a3 = new VariableTerm(engine);
        Term[] y1 = {a1};
        a4 = new StructureTerm(s3, y1);
        a5 = new VariableTerm(engine);
        p1 = new PRED_nl_0(cont);
        p2 = new PRED_write_1(a5, p1);
        p3 = new PRED_expand_rand_3(a4, a5, a2, p2);
        p4 = new PRED_flag_3(s1, new VariableTerm(engine), si2, p3);
        p5 = new PRED_initialize_rules_4(a3, new VariableTerm(engine), new VariableTerm(engine), new VariableTerm(engine), p4);
        return new PRED_all_rules_1(a3, p5);
    }
}
