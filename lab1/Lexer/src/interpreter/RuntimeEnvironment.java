package interpreter;

import java.util.HashMap;
import java.util.Map;

public class RuntimeEnvironment {
    private final RuntimeEnvironment parent;
    private final Map<String, Object> values = new HashMap<>();

    public RuntimeEnvironment() {
        this(null);
    }

    public RuntimeEnvironment(RuntimeEnvironment parent) {
        this.parent = parent;
    }

    public void define(String name, Object value) {
        values.put(name, value);
    }

    public void assign(String name, Object value) {
        if (values.containsKey(name)) {
            values.put(name, value);
        } else if (parent != null) {
            parent.assign(name, value);
        } else {
            throw new RuntimeException("Undefined variable '" + name + "'");
        }
    }

    public Object get(String name) {
        if (values.containsKey(name)) {
            return values.get(name);
        } else if (parent != null) {
            return parent.get(name);
        } else {
            throw new RuntimeException("Undefined variable '" + name + "'");
        }
    }

    public RuntimeEnvironment pushScope() {
        return new RuntimeEnvironment(this);
    }
}