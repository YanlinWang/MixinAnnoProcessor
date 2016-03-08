package mumbler.simple;

import java.util.HashMap;

public class Environment {
    private final HashMap<String, Object> env = new HashMap<String, Object>();

    private final Environment parent;

    public Environment() {
        this(null);
    }

    public Environment(Environment parent) {
        this.parent = parent;
    }

    public Object getValue(String name) {
        if (this.env.containsKey(name)) {
            return this.env.get(name);
        } else if (this.parent != null) {
            return this.parent.getValue(name);
        } else {
            throw new RuntimeException("No variable: " + name);
        }
    }

    public void putValue(String name, Object value) {
        this.env.put(name, value);
    }

    public static Environment getBaseEnvironment() {
        Environment env = new Environment();
        env.putValue("+", PLUS.of(""));
        env.putValue("-", MINUS.of(""));
        env.putValue("*", MULT.of(""));
        env.putValue("/", DIV.of(""));
        env.putValue("=", EQUALS.of(""));
        env.putValue("<", LESS_THAN.of(""));
        env.putValue(">", GREATER_THAN.of(""));
        env.putValue("list", LIST.of(""));
        env.putValue("car", CAR.of(""));
        env.putValue("cdr", CDR.of(""));
        env.putValue("println", PRINTLN.of(""));
        env.putValue("now", NOW.of(""));
        return env;
    }
}
