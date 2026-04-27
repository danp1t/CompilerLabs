package semantic;

import parser.ast.statement.FunctionStatement;
import types.ValueType;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SemanticEnvironment {
    private final SemanticEnvironment parent;
    private final Map<String, VariableInfo> variables;
    private final Map<String, FunctionStatement> functions = new HashMap<>();

    private static class VariableInfo {
        ValueType type;
        boolean used;
        boolean isParameter;

        VariableInfo(ValueType type, boolean isParameter) {
            this.type = type;
            this.used = false;
            this.isParameter = isParameter;
        }
    }

    public SemanticEnvironment() {
        this(null);
    }

    public SemanticEnvironment(SemanticEnvironment parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
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

    public boolean defineVariable(String name, ValueType type) {
        return defineVariable(name, type, false);
    }

    public boolean defineVariable(String name, ValueType type, boolean isParameter) {
        if (variables.containsKey(name)) return false;
        variables.put(name, new VariableInfo(type, isParameter));
        return true;
    }

    public boolean isVariableDefined(String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        return parent != null && parent.isVariableDefined(name);
    }

    public boolean isVariableParameter(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name).isParameter;
        }
        return parent != null && parent.isVariableParameter(name);
    }

    public ValueType getVariableType(String name) {
        if (variables.containsKey(name)) {
            return variables.get(name).type;
        }
        if (parent != null) {
            return parent.getVariableType(name);
        }
        return null;
    }

    public boolean assignVariable(String name, ValueType valueType) {
        if (variables.containsKey(name)) {
            VariableInfo info = variables.get(name);
            if (info.type == null) {
                info.type = valueType;
                return true;
            } else return info.type == valueType;
        }
        if (parent != null) {
            return parent.assignVariable(name, valueType);
        }
        return false;
    }

    public void markUsed(String name) {
        if (variables.containsKey(name)) {
            variables.get(name).used = true;
            return;
        }
        if (parent != null) {
            parent.markUsed(name);
        }
    }

    public List<String> getUnusedVariables() {
        List<String> unused = new ArrayList<>();
        for (Map.Entry<String, VariableInfo> entry : variables.entrySet()) {
            VariableInfo info = entry.getValue();
            if (!info.used && !info.isParameter) {
                unused.add(entry.getKey());
            }
        }
        return unused;
    }
}