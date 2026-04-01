package semantic;

import types.ValueType;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SemanticEnvironment {
    private final SemanticEnvironment parent;
    private final Map<String, VariableInfo> variables;

    private static class VariableInfo {
        ValueType type;
        boolean used;

        VariableInfo(ValueType type) {
            this.type = type;
            this.used = false;
        }
    }

    public SemanticEnvironment() {
        this(null);
    }

    public SemanticEnvironment(SemanticEnvironment parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }

    public boolean defineVariable(String name, ValueType type) {
        if (variables.containsKey(name)) {
            return false;
        }
        variables.put(name, new VariableInfo(type));
        return true;
    }

    public boolean isVariableDefined(String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        return parent != null && parent.isVariableDefined(name);
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
            if (!entry.getValue().used) {
                unused.add(entry.getKey());
            }
        }
        return unused;
    }
}