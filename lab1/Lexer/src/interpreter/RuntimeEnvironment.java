package interpreter;

import parser.ast.statement.FunctionStatement;

import java.util.HashMap;
import java.util.Map;

public class RuntimeEnvironment {
    private final RuntimeEnvironment parent;
    private final Map<String, Object> values = new HashMap<>();
    private final Map<String, FunctionStatement> functions = new HashMap<>();

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

    public boolean defineFunction(String name, FunctionStatement function) {
        if (functions.containsKey(name)) return false;
        functions.put(name, function);
        return true;
    }

    public FunctionStatement getFunction(String name) {
        if (functions.containsKey(name)) return functions.get(name);
        if (parent != null) return parent.getFunction(name);
        return null;
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