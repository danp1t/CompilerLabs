package parser.ast.statement;

import parser.ast.Expression;
import parser.ast.Statement;

public class ReturnStatement extends Statement {
    public final Expression value;

    public ReturnStatement(Expression value) {
        this.value = value;
    }
}