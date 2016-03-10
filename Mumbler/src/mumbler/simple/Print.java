package mumbler.simple;

import mumbler.simple.node.MumblerListNode;
import mumbler.simple.node.SymbolNode;

//============= Added operation (begin) ============//
public interface Print {
    public String print();
}

abstract class BuiltinFnP extends FunctionP {
    static final FunctionP EQUALS = new BuiltinFnP("EQUALS") {};
    static final FunctionP LESS_THAN = new BuiltinFnP("LESS-THAN") {};
    static final FunctionP GREATER_THAN = new BuiltinFnP("GREATER-THAN") {};
    static final FunctionP DIV = new BuiltinFnP("DIV") {};
    static final FunctionP MULT = new BuiltinFnP("MULT") {};
    static final FunctionP MINUS = new BuiltinFnP("MINUS") {};
    static final FunctionP PLUS = new BuiltinFnP("PLUS") {};
    static final FunctionP LIST = new BuiltinFnP("list") {};
    static final FunctionP CAR = new BuiltinFnP("car") {};
    static final FunctionP CDR = new BuiltinFnP("cdr") {};
    static final FunctionP PRINTLN = new BuiltinFnP("println") {};
    static final FunctionP NOW = new BuiltinFnP("now") {};
    
    protected final String name;

    public BuiltinFnP(String name) {
        this.name = name;
    }

    public String print() {
        return "<procedure: " + this.name;
    }
}

class BooleanNodeP implements Print {
   public static final BooleanNodeP TRUE = new BooleanNodeP(Boolean.TRUE);
   public static final BooleanNodeP FALSE = new BooleanNodeP(Boolean.FALSE);
  
    private final Boolean value;

    private BooleanNodeP(Boolean value) {
        this.value = value;
    }
    public String print() {
        return value.toString();
    }
}

class NumberNodeP implements Print {
    private final long num;

    public NumberNodeP(long num) {
        this.num = num;
    }

    public String print() {
        return Long.toString(num);
    }
}

abstract class SpecialFormP implements Print {
    private static class DefineSpecialFormP extends SpecialFormP {
        public DefineSpecialFormP(MumblerListNode<Print> listNode) {
            super(listNode);
        }
    }

    private static class LambdaSpecialFormP extends SpecialFormP {
        public LambdaSpecialFormP(MumblerListNode<Print> listNode) {
            super(listNode);
        }
    }

    private static class IfSpecialFormP extends SpecialFormP {
        public IfSpecialFormP(MumblerListNode<Print> listNode) {
            super(listNode);
        }
    }

    private static class QuoteSpecialFormP extends SpecialFormP {
        public QuoteSpecialFormP(MumblerListNode<Print> listNode) {
            super(listNode);
        }
    }

    protected final MumblerListNode<Print> node;

    private SpecialFormP(MumblerListNode<Print> listNode) {
        this.node = listNode;
    }

    public String print() {
        return node.print();
    }
    private static final SymbolNode DEFINE = new SymbolNode("define");
    private static final SymbolNode LAMBDA = new SymbolNode("lambda");
    private static final SymbolNode IF = new SymbolNode("if");
    private static final SymbolNode QUOTE = new SymbolNode("quote");

    public static Print check(MumblerListNode<Print> l) {
        if (l == MumblerListNode.EMPTY) {
            return (Print) l;
        } else if (l.car.equals(DEFINE)) {
            return new DefineSpecialFormP(l);
        } else if (l.car.equals(LAMBDA)) {
            return new LambdaSpecialFormP(l);
        } else if (l.car.equals(IF)) {
            return new IfSpecialFormP(l);
        } else if (l.car.equals(QUOTE)) {
            return new QuoteSpecialFormP(l);
        }
        return (Print) l;
    }
}


class SymbolNodeP implements Print {
    public final String name;

    public SymbolNodeP(String name) {
        this.name = name;
    }

    public String print() {
        return "'" + this.name;
    }
}
//============= Added operation (end) ============//