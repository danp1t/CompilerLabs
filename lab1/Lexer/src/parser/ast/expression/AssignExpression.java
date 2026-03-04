package parser.ast.expression;

import parser.ast.Expression;

public class AssignExpression extends Expression {
    public String name;
    public Expression value;

    public AssignExpression(String name, Expression value) {
        this.name = name;
        this.value = value;
    }
}
