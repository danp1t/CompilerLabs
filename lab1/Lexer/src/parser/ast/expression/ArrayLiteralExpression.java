package parser.ast.expression;

import parser.ast.Expression;
import java.util.List;

public class ArrayLiteralExpression extends Expression {
    public final List<Expression> elements;

    public ArrayLiteralExpression(List<Expression> elements) {
        this.elements = elements;
    }
}