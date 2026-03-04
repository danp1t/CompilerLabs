package parser.ast.statement;

import parser.ast.Expression;
import parser.ast.Statement;

public class VarStatement extends Statement {
    public final String name;
    public final Expression initializer;

    public VarStatement(String name, Expression initializer) {
        this.name = name;
        this.initializer = initializer;
    }
}
