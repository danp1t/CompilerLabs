package parser;

import parser.ast.Expression;
import parser.ast.Statement;
import parser.ast.expression.*;
import parser.ast.statement.*;
import types.Token;
import types.Type;

import java.util.ArrayList;
import java.util.List;

public class Parser {
    private List<Token> tokens;
    private int position;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
        this.position = 0;
    }

    public List<Statement> parse() {
        List<Statement> statements = new ArrayList<>();
        while (!isAtEnd()) {
            statements.add(parseDeclaration());
        }
        return statements;
    }

    private Statement parseDeclaration() {
        if (match(Type.VAR)) {
            return parseVarDeclaration();
        }
        return parseStatement();
    }

    private Statement parseStatement() {
        if (match(Type.IF)) return parseIfStatement();
        if (match(Type.WHILE)) return parseWhileStatement();
        if (match(Type.PRINT)) return parsePrintStatement();
        if (match(Type.LBRACE)) return new BlockStatement(parseBlock());

        return parseExpressionStatement();
    }

    private Statement parseVarDeclaration() {
        Token name = consume(Type.ID, "Expected variable name.");
        Expression initializer = null;

        if (match(Type.EQ)) {
            initializer = parseExpression();
        }

        consume(Type.SEMICOLON, "Expected ';' after variable declaration.");
        return new VarStatement(name.getValue(), initializer);
    }

    private Statement parseIfStatement() {
        consume(Type.LPAREN, "Expected '(' after 'if'.");
        Expression condition = parseExpression();
        consume(Type.RPAREN, "Expected ')' after if condition.");

        Statement thenBranch = parseStatement();
        Statement elseBranch = null;

        if (match(Type.ELSE)) {
            elseBranch = parseStatement();
        }

        return new IfStatement(condition, thenBranch, elseBranch);
    }

    private Statement parseWhileStatement() {
        consume(Type.LPAREN, "Expected '(' after 'while'.");
        Expression condition = parseExpression();
        consume(Type.RPAREN, "Expected ')' after while condition.");

        Statement body = parseStatement();
        return new WhileStatement(condition, body);
    }

    private Statement parsePrintStatement() {
        Expression value = parseExpression();
        consume(Type.SEMICOLON, "Expected ';' after value.");
        return new PrintStatement(value);
    }

    private Statement parseExpressionStatement() {
        Expression expr = parseExpression();
        consume(Type.SEMICOLON, "Expected ';' after expression.");
        return new ExpressionStatement(expr);
    }

    private List<Statement> parseBlock() {
        List<Statement> statements = new ArrayList<>();

        while (!check(Type.RBRACE) && !isAtEnd()) {
            statements.add(parseDeclaration());
        }

        consume(Type.RBRACE, "Expected '}' after block.");
        return statements;
    }

    private Expression parseExpression() {
        return parseAssignment();
    }

    private Expression parseAssignment() {
        Expression expr = parseLogicalOr();

        if (match(Type.EQ)) {
            Token equals = previous();
            Expression value = parseAssignment();

            if (expr instanceof VariableExpression) {
                String name = ((VariableExpression) expr).name;
                return new AssignExpression(name, value);
            }

            throw new RuntimeException("[Parser Error] Line " + equals.getPosition() +
                    ": Invalid assignment target.");
        }

        return expr;
    }

    private Expression parseLogicalOr() {
        Expression expr = parseLogicalAnd();

        while (match(Type.OR)) {
            Type op = previous().getType();
            Expression right = parseLogicalAnd();
            expr = new BinaryExpression(expr, right, op);
        }

        return expr;
    }

    private Expression parseLogicalAnd() {
        Expression expr = parseEquality();

        while (match(Type.AND)) {
            Type op = previous().getType();
            Expression right = parseEquality();
            expr = new BinaryExpression(expr, right, op);
        }

        return expr;
    }

    private Expression parseEquality() {
        Expression expr = parseComparison();

        while (match(Type.EQEQ, Type.NEQ)) {
            Type op = previous().getType();
            Expression right = parseComparison();
            expr = new BinaryExpression(expr, right, op);
        }

        return expr;
    }

    private Expression parseComparison() {
        Expression expr = parseTerm();

        while (match(Type.LT, Type.LTEQ, Type.GT, Type.GTEQ)) {
            Type op = previous().getType();
            Expression right = parseTerm();
            expr = new BinaryExpression(expr, right, op);
        }

        return expr;
    }

    private Expression parseTerm() {
        Expression expr = parseFactor();

        while (match(Type.PLUS, Type.MINUS)) {
            Type op = previous().getType();
            Expression right = parseFactor();
            expr = new BinaryExpression(expr, right, op);
        }

        return expr;
    }

    private Expression parseFactor() {
        Expression expr = parseUnary();

        while (match(Type.STAR, Type.SLASH)) {
            Type op = previous().getType();
            Expression right = parseUnary();
            expr = new BinaryExpression(expr, right, op);
        }

        return expr;
    }

    private Expression parseUnary() {
        if (match(Type.EXCL, Type.MINUS)) {
            Type op = previous().getType();
            Expression right = parseUnary();
            return new UnaryExpression(op, right);
        }

        return parsePrimary();
    }

    private Expression parsePrimary() {
        if (match(Type.NUMBER)) {
            double value = Double.parseDouble(previous().getValue());
            return new NumberExpression(value);
        }

        if (match(Type.ID)) {
            return new VariableExpression(previous().getValue());
        }

        if (match(Type.LPAREN)) {
            Expression expr = parseExpression();
            consume(Type.RPAREN, "Expected ')' after expression.");
            return expr;
        }

        Token peek = peek();
        throw new RuntimeException("[Parser Error] Line " + peek.getPosition() + ": Expected expression.");
    }

    private boolean match(Type... types) {
        for (Type type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token peek() {
        return tokens.get(position);
    }

    private boolean check(Type type) {
        if (isAtEnd()) return false;
        return peek().getType() == type;
    }

    private boolean isAtEnd() {
        return peek().getType() == Type.EOF;
    }

    private Token previous() {
        return tokens.get(position - 1);
    }

    private Token advance() {
        if (!isAtEnd()) position++;
        return previous();
    }

    private Token consume(Type type, String message) {
        if (check(type)) return advance();
        Token token = peek();
        throw new RuntimeException("[Parser Error] " + token.getPosition() + message);
    }
}
