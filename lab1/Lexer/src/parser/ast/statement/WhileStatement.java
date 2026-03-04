package parser.ast.statement;

import parser.ast.Expression;
import parser.ast.Statement;

public class WhileStatement extends Statement {
    public final Expression condition;
    public Statement body;

    public WhileStatement(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }
}
