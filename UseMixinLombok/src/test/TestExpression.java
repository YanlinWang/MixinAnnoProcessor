package test;

import java.util.ArrayList;
import java.util.List;
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

//BEGIN_EXPRESSION_COLLECTLIT
interface ExpC extends Exp { List<Integer> collectLit(); }
@Mixin interface LitC extends Lit, ExpC {
    default List<Integer> collectLit() {
        List<Integer> list = new ArrayList<Integer>();
        list.add(x());
        return list;
    }
}
@Mixin interface AddC extends Add, ExpC {
    ExpC e1(); ExpC e2();
    default List<Integer> collectLit() {
        List<Integer> list = new ArrayList<Integer>();
        list.addAll(e1().collectLit());
        list.addAll(e2().collectLit());
        return list;
    }
}
//END_EXPRESSION_COLLECTLIT

//BEGIN_INDEPENDENT_EXTENSIBILITY
interface ExpPC extends ExpP, ExpC {}
/**
 * rejected.
 * error message: The return types are incompatible for the inherited 
 *                methods LitP.clone(), LitC.clone(), Object.clone()
 * TODO to Haoyuan: the prototype is still generating clone methods (even if user is not asking)              
 */
interface LitPC extends ExpPC, LitP, LitC {}
interface AddPC extends ExpPC, AddP, AddC {
    ExpPC e1(); ExpPC e2();
}
//END_INDEPENDENT_EXTENSIBILITY
