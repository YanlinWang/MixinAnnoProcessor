package mumbler.simple;

import mumbler.simple.env.Environment;
import mumbler.simple.node.*;

public abstract class Eval2 extends Node implements Print {}

abstract class Function2 extends Eval2 {
    public Object eval(Environment env) {
        return this;
    }
    public abstract Object apply(Object... args);
}

abstract class BuiltinFn2 extends Function2 {
    static final Function2 EQUALS = new BuiltinFn2("EQUALS") {
        @Override
        public Object apply(Object... args) {
            Long last = (Long) args[0];
            for (Object arg : args) {
                Long current = (Long) arg;
                if (!last.equals(current)) {
                    return false;
                } else {
                    last = current;
                }
            }
            return true;
        }
    };

    static final Function2 LESS_THAN = new BuiltinFn2("LESS-THAN") {
        @Override
        public Object apply(Object... args) {
            assert args.length > 1;
            long num = (Long) args[args.length - 1];
            for (int i=args.length - 2; i>=0; i--) {
                long n = (Long) args[i];
                if (n >= num) {
                    return false;
                }
                num = n;
            }
            return true;
        }
    };

    static final Function2 GREATER_THAN = new BuiltinFn2("GREATER-THAN") {
        @Override
        public Object apply(Object... args) {
            assert args.length > 1;
            long num = (Long) args[args.length - 1];
            for (int i=args.length - 2; i>=0; i--) {
                long n = (Long) args[i];
                if (n <= num) {
                    return false;
                }
                num = n;
            }
            return true;
        }
    };

    static final Function2 DIV = new BuiltinFn2("DIV") {
        @Override
        public Object apply(Object... args) {
            if (args.length == 1) {
                return 1 / (Long) args[0];
            }
            long quotient = (Long) args[0];
            for (int i=1; i<args.length; i++) {
                quotient /= (Long) args[i];
            }
            return quotient;
        }
    };

    static final Function2 MULT = new BuiltinFn2("MULT") {
        @Override
        public Object apply(Object... args) {
            long product = 1;
            for (Object arg : args) {
                product *= (Long) arg;
            }
            return product;
        }
    };

    static final Function2 MINUS = new BuiltinFn2("MINUS") {
        @Override
        public Object apply(Object... args) {
            if (args.length < 1) {
                throw new RuntimeException(this.name + " requires an argument");
            }
            switch (args.length) {
            case 1:
                return -((Long) args[0]);
            default:
                long diff = (Long) args[0];
                for (int i=1; i<args.length; i++) {
                    diff -= (Long) args[i];
                }
                return diff;
            }
        }
    };

    static final Function2 PLUS = new BuiltinFn2("PLUS") {
        @Override
        public Object apply(Object... args) {
            long sum = 0;
            for (Object arg : args) {
                sum += (Long) arg;
            }
            return sum;
        }
    };

    static final Function2 LIST = new BuiltinFn2("list") {
        @Override
        public Object apply(Object... args) {
            return MumblerListNode.list(args);
        }
    };

    static final Function2 CAR = new BuiltinFn2("car") {
        @Override
        public Object apply(Object... args) {
            assert args.length == 1;
            return ((MumblerListNode<?>) args[0]).car;
        }
    };

    static final Function2 CDR = new BuiltinFn2("cdr") {
        @Override
        public Object apply(Object... args) {
            assert args.length == 1;
            return ((MumblerListNode<?>) args[0]).cdr;
        }
    };

    static final Function2 PRINTLN = new BuiltinFn2("println") {
        @Override
        public Object apply(Object... args) {
            for (Object arg : args) {
                System.out.println(arg);
            }
            return MumblerListNode.EMPTY;
        }
    };

    static final Function2 NOW = new BuiltinFn2("now") {
        @Override
        public Object apply(Object... args) {
            return System.currentTimeMillis();
        }
    };
    
    protected final String name;

    public BuiltinFn2(String name) {
        this.name = name;
    }

    public String print() {
        return "<procedure: " + this.name;
    }
}

class BooleanNode2 extends Eval2 {
   public static final BooleanNode2 TRUE = new BooleanNode2(Boolean.TRUE);
   public static final BooleanNode2 FALSE = new BooleanNode2(Boolean.FALSE);
  
    private BooleanNode2(Boolean value) {
        this.value = value;
    }
    public String print() {
        return value.toString();
    }

    protected final Boolean value;

    public Object eval(Environment env) {
        return this.value;
    }
}

class NumberNode2 extends Eval2 {
    protected final long num;

    @Override
    public boolean equals(Object other) {
        return other instanceof NumberNode2 &&
            this.num == ((NumberNode2) other).num;
    }

    @Override
    public Object eval(Environment env) {
        return new Long(this.num);
    }
    public NumberNode2(long num) {
        this.num = num;
    }

    public String print() {
        return Long.toString(num);
    }
}

abstract class SpecialForm2 extends Eval2 {
    private static class DefineSpecialForm2 extends SpecialForm2 {
        public DefineSpecialForm2(MumblerListNode<Node> listNode) {
            super(listNode);
        }
        public Object eval(Environment env) {
            SymbolNode sym = (SymbolNode) this.node.cdr.car; // 2nd element
            env.putValue(sym.name,
                    this.node.cdr.cdr.car.eval(env)); // 3rd element
            return null;
        }
    }

    private static class LambdaSpecialForm2 extends SpecialForm2 {
        public LambdaSpecialForm2(MumblerListNode<Node> listNode) {
            super(listNode);
        }
        public Object eval(final Environment parentEnv) {
            @SuppressWarnings("unchecked")
            final MumblerListNode<Eval2> formalParams =
            (MumblerListNode<Eval2>) this.node.cdr.car;
            final MumblerListNode<Node> body = this.node.cdr.cdr;
            return new Function() {
                @Override
                public Object apply(Object... args) {
                    Environment lambdaEnv = new Environment(parentEnv);
                    if (args.length != formalParams.length()) {
                        throw new RuntimeException(
                                "Wrong number of arguments. Expected: " +
                                        formalParams.length() + ". Got: " +
                                        args.length);
                    }

                    // Map parameter values to formal parameter names
                    int i = 0;
                    for (Eval2 param : formalParams) {
                        SymbolNode2 paramSymbol = (SymbolNode2) param;
                        lambdaEnv.putValue(paramSymbol.name, args[i]);
                        i++;
                    }

                    // Evaluate body
                    Object output = null;
                    for (Node node : body) {
                        output = node.eval(lambdaEnv);
                    }

                    return output;
                }
            };
        }
    }

    private static class IfSpecialForm2 extends SpecialForm2 {
        public IfSpecialForm2(MumblerListNode<Node> listNode) {
            super(listNode);
        }
        @Override
        public Object eval(Environment env) {
            Eval2 testNode = (Eval2) this.node.cdr.car;
            Eval2 thenNode = (Eval2) this.node.cdr.cdr.car;
            Eval2 elseNode = (Eval2) this.node.cdr.cdr.cdr.car;

            Object result = testNode.eval(env);
            if (result == MumblerListNode.EMPTY || Boolean.FALSE == result) {
                return elseNode.eval(env);
            } else {
                return thenNode.eval(env);
            }
        }
    }

    private static class QuoteSpecialForm2 extends SpecialForm2 {
        public QuoteSpecialForm2(MumblerListNode<Node> listNode) {
            super(listNode);
        }
        @Override
        public Object eval(Environment env) {
            return this.node.cdr.car;
        }
    }

    public SpecialForm2(MumblerListNode<Node> listNode) {
        this.node = listNode;
    }

    public String print() {
        return node.print();
    }
    private static final SymbolNode DEFINE = new SymbolNode("define");
    private static final SymbolNode LAMBDA = new SymbolNode("lambda");
    private static final SymbolNode IF = new SymbolNode("if");
    private static final SymbolNode QUOTE = new SymbolNode("quote");
    
    protected final MumblerListNode<Node> node; 
    
        public static Node check(MumblerListNode<Node> l) {
        if (l == MumblerListNode.EMPTY) {
            return l;
        } else if (l.car.equals(DEFINE)) {
            return new DefineSpecialForm2(l);
        } else if (l.car.equals(LAMBDA)) {
            return new LambdaSpecialForm2(l);
        } else if (l.car.equals(IF)) {
            return new IfSpecialForm2(l);
        } else if (l.car.equals(QUOTE)) {
            return new QuoteSpecialForm2(l);
        }
        return l;
    }
}


class SymbolNode2 extends Eval2 {
    public final String name;

    public SymbolNode2(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "'" + this.name;
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof SymbolNode2 &&
                this.name.equals(((SymbolNode2) other).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public Object eval(Environment env) {
        return env.getValue(this.name);
    }

    public String print() {
        return "'" + this.name;
    }
}