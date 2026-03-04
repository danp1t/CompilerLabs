package parser.ast.statement;

import parser.ast.Expression;
import parser.ast.Statement;

public class PrintStatement extends Statement {
    public final Expression expression;
    public PrintStatement(Expression expression) {
        this.expression = expression;
    }
}
