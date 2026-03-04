package parser.ast.statement;

import parser.ast.Expression;
import parser.ast.Statement;

public class IfStatement extends Statement {
    public Expression condition;
    public Statement thenBranch;
    public Statement elseBranch;

    public IfStatement(Expression condition, Statement thenBranch, Statement elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }
}
