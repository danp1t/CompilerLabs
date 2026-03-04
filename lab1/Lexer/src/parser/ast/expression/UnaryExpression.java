package parser.ast.expression;

import parser.ast.Expression;
import types.Type;

public class UnaryExpression extends Expression {
    public Type operator;
    public Expression operand;

    public UnaryExpression(Type op, Expression operand)
    {
        operator = op;
        operand = operand;
    }
}
