package semantic;

import parser.ast.Statement;
import parser.ast.Expression;
import parser.ast.statement.*;
import parser.ast.expression.*;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {
    private SemanticEnvironment environment = new SemanticEnvironment();
    private final List<String> errors = new ArrayList<>();

    public void analyze(List<Statement> statements) {
        for (Statement stmt : statements) {
            visitStatement(stmt);
        }
        checkUnusedInCurrentScope();
    }

    private void visitStatement(Statement stmt) {
        switch (stmt) {
            case VarStatement varSomething -> {
                if (varSomething.initializer != null) {
                    visitExpression(varSomething.initializer);
                }
                if (!environment.defineVariable(varSomething.name)) {
                    errors.add("Variable '" + varSomething.name + "' is already defined.");
                }
            }
            case PrintStatement printSomething -> visitExpression(printSomething.expression);
            case ExpressionStatement exprSomething -> visitExpression(exprSomething.expression);
            case BlockStatement blockSomething -> {
                SemanticEnvironment previous = environment;
                environment = new SemanticEnvironment(previous);
                for (Statement inner : blockSomething.statements) {
                    visitStatement(inner);
                }
                checkUnusedInCurrentScope();
                environment = previous;
            }
            case IfStatement ifSomething -> {
                visitExpression(ifSomething.condition);
                visitStatement(ifSomething.thenBranch);
                if (ifSomething.elseBranch != null) {
                    visitStatement(ifSomething.elseBranch);
                }
            }
            case WhileStatement whileSomething -> {
                visitExpression(whileSomething.condition);
                visitStatement(whileSomething.body);
            }
            case null, default -> errors.add("Unsupported statement type: " + stmt.getClass().getName());
        }
    }

    private void visitExpression(Expression expr) {
        switch (expr) {
            case NumberExpression n -> {}
            case StringExpression s -> {}
            case VariableExpression variableExpression -> {
                if (!environment.isVariableDefined(variableExpression.name)) {
                    errors.add("Variable '" + variableExpression.name + "' is not defined.");
                } else {
                    environment.markUsed(variableExpression.name);
                }
            }
            case AssignExpression assignExpression -> {
                visitExpression(assignExpression.value);
                if (!environment.isVariableDefined(assignExpression.name)) {
                    errors.add("Variable '" + assignExpression.name + "' is not defined.");
                } else {
                    environment.markUsed(assignExpression.name);
                }
            }
            case BinaryExpression binaryExpression -> {
                visitExpression(binaryExpression.left);
                visitExpression(binaryExpression.right);
            }
            case UnaryExpression unaryExpression -> visitExpression(unaryExpression.operand);
            case null, default -> errors.add("Unsupported expression type: " + expr.getClass().getName());
        }
    }

    private void checkUnusedInCurrentScope() {
        for (String varName : environment.getUnusedVariables()) {
            errors.add("Variable '" + varName + "' is declared but never used.");
        }
    }

    public List<String> getErrors() {
        return errors;
    }
}