package test;

import lombok.Mixin;

public class TestExpression {
	public static void main(String[] args) {
		Lit e1 = Lit.of(3);
		System.out.println(e1.eval());
		Lit e2 = Lit.of(4);
		Add e3 = Add.of(e1, e2);
		System.out.println(e3.eval());
		Sub e4 = Sub.of(e1, e2);
		System.out.println(e4.eval());
		LitP e5 = LitP.of(3);
		LitP e6 = LitP.of(4);
		AddP e7 = AddP.of(e5, e6);
		System.out.println(e5.print() + " = " + e5.eval());
		System.out.println(e7.print() + " = " + e7.eval());
	}
}

//BEGIN_EXPRESSION_INIT
interface Exp {
    int eval();
}

@Mixin
interface Lit extends Exp {
	int x();
	default int eval() { return x(); }
}

@Mixin
interface Add extends Exp {
	Exp e1();
	Exp e2();
	default int eval() {
		return e1().eval() + e2().eval();
	}
}
//END_EXPRESSION_INIT

//BEGIN_EXPRESSION_SUB
@Mixin
interface Sub extends Exp {
	Exp e1();
	Exp e2();
	default int eval() {
		return e1().eval() - e2().eval();
	}
}
//END_EXPRESSION_SUB

//BEGIN_EXPRESSION_PRINT
interface ExpP extends Exp {
	String print();
}

@Mixin
interface LitP extends Lit, ExpP {
	default String print() {
		return "" + x();
	}
}

@Mixin
interface AddP extends Add, ExpP {
	default String print() {
		return "(" + e1().print() + " + " + e2().print() + ")";
	}
	ExpP e1();
	ExpP e2();
}
//END_EXPRESSION_PRINT
