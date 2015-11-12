package test;

import lombok.Mixin;

public class TestExpression {
	public static String runTest() {
		String res = "";
		Lit e1 = Lit.of(3);
		res += e1.eval() + "\n"; // 3
		Lit e2 = Lit.of(4);
		Add e3 = Add.of(e1, e2);
		res += e3.eval() + "\n"; // 7
		Sub e4 = Sub.of(e1, e2);
		res += e4.eval() + "\n"; // -1
		LitP e5 = LitP.of(3);
		LitP e6 = LitP.of(4);
		AddP e7 = AddP.of(e5, e6);
		res += e5.print() + " = " + e5.eval() + "\n"; // 3 = 3
		res += e7.print() + " = " + e7.eval() + "\n"; // (3 + 4) = 7
		return res;
	}
}

//BEGIN_EXPRESSION_INIT
interface Exp { int eval(); }
@Mixin interface Lit extends Exp {
	int x();
	default int eval() { return x(); }
}
@Mixin interface Add extends Exp {
	Exp e1(); Exp e2();
	default int eval() {
		return e1().eval() + e2().eval();
	}
}
//END_EXPRESSION_INIT

//BEGIN_EXPRESSION_SUB
@Mixin interface Sub extends Exp {
	Exp e1(); Exp e2();
	default int eval() {
		return e1().eval() - e2().eval();
	}
}
//END_EXPRESSION_SUB

//BEGIN_EXPRESSION_PRINT
interface ExpP extends Exp { String print(); }
@Mixin interface LitP extends Lit, ExpP {
	default String print() {return "" + x();}
}
@Mixin interface AddP extends Add, ExpP {
    ExpP e1(); ExpP e2();
	default String print() {
		return "(" + e1().print() + " + " + e2().print() + ")";
	}
}
//END_EXPRESSION_PRINT
