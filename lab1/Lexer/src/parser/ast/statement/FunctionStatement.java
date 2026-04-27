package parser.ast.statement;

import parser.ast.Statement;
import java.util.List;

public class FunctionStatement extends Statement {
    public final String name;
    public final List<String> parameters;
    public final BlockStatement body;

    public FunctionStatement(String name, List<String> parameters,
                             BlockStatement body) {
        this.name = name;
        this.parameters = parameters;
        this.body = body;
    }
}