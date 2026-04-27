package parser.ast.expression;

import parser.ast.Expression;
import java.util.List;

public class CallExpression extends Expression {
    public final String calleeName;
    public final List<Expression> arguments;

    public CallExpression(String calleeName, List<Expression> arguments) {
        this.calleeName = calleeName;
        this.arguments = arguments;
    }
}