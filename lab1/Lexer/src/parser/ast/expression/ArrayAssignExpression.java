package parser.ast.expression;

import parser.ast.Expression;

public class ArrayAssignExpression extends Expression {
    public final Expression array;
    public final Expression index;
    public final Expression value;

    public ArrayAssignExpression(Expression array, Expression index, Expression value) {
        this.array = array;
        this.index = index;
        this.value = value;
    }
}
