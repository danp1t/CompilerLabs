package semantic;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SemanticEnvironment {
    private final SemanticEnvironment parent;
    private final Map<String, Boolean> variables;

    public SemanticEnvironment() {
        this(null);
    }

    public SemanticEnvironment(SemanticEnvironment parent) {
        this.parent = parent;
        this.variables = new HashMap<>();
    }

    public boolean defineVariable(String name) {
        if (variables.containsKey(name)) {
            return false;
        }
        variables.put(name, false);
        return true;
    }

    public boolean isVariableDefined(String name) {
        if (variables.containsKey(name)) {
            return true;
        }
        return parent != null && parent.isVariableDefined(name);
    }

    public void markUsed(String name) {
        if (variables.containsKey(name)) {
            variables.put(name, true);
            return;
        }
        if (parent != null) {
            parent.markUsed(name);
        }
    }

    public List<String> getUnusedVariables() {
        List<String> unused = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : variables.entrySet()) {
            if (!entry.getValue()) {
                unused.add(entry.getKey());
            }
        }
        return unused;
    }
}