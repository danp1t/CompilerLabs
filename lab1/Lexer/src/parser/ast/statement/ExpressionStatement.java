package parser.ast.statement;

import parser.ast.Expression;
import parser.ast.Statement;

public class ExpressionStatement extends Statement {
    public final Expression expression;
    public ExpressionStatement(Expression expression) {
        this.expression = expression;
    }
}
