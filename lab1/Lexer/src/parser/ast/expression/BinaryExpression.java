package parser.ast.expression;

import parser.ast.Expression;
import types.Type;

public class BinaryExpression extends Expression {
    public Expression left;
    public Expression right;
    public Type operator;

    public BinaryExpression(Expression left, Expression right, Type operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }
}
