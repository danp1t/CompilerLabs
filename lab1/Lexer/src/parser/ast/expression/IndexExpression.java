package parser.ast.expression;

import parser.ast.Expression;

public class IndexExpression extends Expression {
    public final Expression array;
    public final Expression index;

    public IndexExpression(Expression array, Expression index) {
        this.array = array;
        this.index = index;
    }
}