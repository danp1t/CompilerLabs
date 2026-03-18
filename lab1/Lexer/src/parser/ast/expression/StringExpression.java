package parser.ast.expression;

import parser.ast.Expression;

public class StringExpression extends Expression {
    public final String value;
    public StringExpression(String value) {
        this.value = value;
    }
}
