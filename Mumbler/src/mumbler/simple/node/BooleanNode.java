package mumbler.simple.node;

import mumbler.simple.env.Environment;

public class BooleanNode extends Node {

    public static final BooleanNode TRUE = new BooleanNode(Boolean.TRUE);
    public static final BooleanNode FALSE = new BooleanNode(Boolean.FALSE);

    protected final Boolean value;

    protected BooleanNode(Boolean value) {
        this.value = value;
    }

    @Override
    public Object eval(Environment env) {
        return this.value;
    }
}
