package parser.ast.expression;

import parser.ast.Expression;

public class VariableExpression extends Expression {
    public final String name;
    public VariableExpression(String name) {
        this.name = name;
    }
}
