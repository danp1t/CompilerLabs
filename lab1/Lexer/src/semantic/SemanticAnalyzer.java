package semantic;

import parser.ast.Statement;
import parser.ast.Expression;
import parser.ast.statement.*;
import parser.ast.expression.*;
import types.Type;
import types.ValueType;

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
            case VarStatement var -> {
                ValueType initType = null;
                if (var.initializer != null) {
                    initType = typeCheck(var.initializer);
                }
                if (!environment.defineVariable(var.name, initType)) {
                    errors.add("Variable '" + var.name + "' is already defined.");
                }
            }
            case PrintStatement print -> {
                typeCheck(print.expression);
            }
            case ExpressionStatement expr -> {
                typeCheck(expr.expression);
            }
            case BlockStatement block -> {
                SemanticEnvironment previous = environment;
                environment = new SemanticEnvironment(previous);
                for (Statement inner : block.statements) {
                    visitStatement(inner);
                }
                checkUnusedInCurrentScope();
                environment = previous;
            }
            case IfStatement ifStmt -> {
                ValueType condType = typeCheck(ifStmt.condition);
                if (condType != ValueType.BOOLEAN) {
                    errors.add("If condition must be boolean, got " + condType);
                }
                visitStatement(ifStmt.thenBranch);
                if (ifStmt.elseBranch != null) {
                    visitStatement(ifStmt.elseBranch);
                }
            }
            case WhileStatement whileStmt -> {
                ValueType condType = typeCheck(whileStmt.condition);
                if (condType != ValueType.BOOLEAN) {
                    errors.add("While condition must be boolean, got " + condType);
                }
                visitStatement(whileStmt.body);
            }
            case FunctionStatement func -> {
                if (!environment.defineFunction(func.name, func)) {
                    errors.add("Function '" + func.name + "' already defined in this scope.");
                } else {
                    SemanticEnvironment prev = environment;
                    environment = new SemanticEnvironment(prev);
                    for (String param : func.parameters) {
                        environment.defineVariable(param, ValueType.ERROR, true);
                    }
                    for (Statement inner : func.body.statements) {
                        visitStatement(inner);
                    }
                    checkUnusedInCurrentScope();
                    environment = prev;
                }
            }
            case ReturnStatement ret -> {
                if (ret.value != null) typeCheck(ret.value);
            }
            case null, default -> errors.add("Unsupported statement type: " + stmt.getClass().getName());
        }
    }

    private ValueType typeCheck(Expression expr) {
        return switch (expr) {
            case NumberExpression n -> ValueType.NUMBER;
            case StringExpression s -> ValueType.STRING;
            case VariableExpression var -> {
                if (!environment.isVariableDefined(var.name)) {
                    errors.add("Variable '" + var.name + "' is not defined.");
                    yield ValueType.ERROR;
                }
                ValueType type = environment.getVariableType(var.name);
                if (type == null) {
                    if (!environment.isVariableParameter(var.name)) {
                        errors.add("Variable '" + var.name + "' is not initialized.");
                    }
                    yield ValueType.ERROR;
                }
                environment.markUsed(var.name);
                yield type;
            }
            case AssignExpression assign -> {
                ValueType valueType = typeCheck(assign.value);
                if (!environment.isVariableDefined(assign.name)) {
                    errors.add("Variable '" + assign.name + "' is not defined.");
                    yield ValueType.ERROR;
                }
                if (!environment.assignVariable(assign.name, valueType)) {
                    errors.add("Type mismatch in assignment to '" + assign.name +
                            "': expected " + environment.getVariableType(assign.name) +
                            ", got " + valueType);
                    yield ValueType.ERROR;
                }
                yield valueType;
            }
            case BinaryExpression bin -> {
                ValueType leftType = typeCheck(bin.left);
                ValueType rightType = typeCheck(bin.right);
                yield checkBinaryOperation(bin.operator, leftType, rightType);
            }
            case UnaryExpression unary -> {
                ValueType operandType = typeCheck(unary.operand);
                yield checkUnaryOperation(unary.operator, operandType);
            }
            case CallExpression call -> {
                FunctionStatement func = environment.getFunction(call.calleeName);
                if (func == null) {
                    errors.add("Undefined function '" + call.calleeName + "'.");
                    yield ValueType.ERROR;
                }
                if (call.arguments.size() != func.parameters.size()) {
                    errors.add("Function '" + call.calleeName + "' expects " +
                            func.parameters.size() + " arguments, got " +
                            call.arguments.size() + ".");
                }
                for (Expression arg : call.arguments) typeCheck(arg);
                yield ValueType.ERROR;  // или возвращаемый тип, если появится
            }
            case null, default -> {
                errors.add("Unsupported expression type: " + expr.getClass().getName());
                yield ValueType.ERROR;
            }
        };
    }

    private ValueType checkBinaryOperation(Type operator, ValueType left, ValueType right) {
        if (left == ValueType.ERROR || right == ValueType.ERROR) {
            return ValueType.ERROR;
        }

        switch (operator) {
            case PLUS:
                if (left == ValueType.NUMBER && right == ValueType.NUMBER) {
                    return ValueType.NUMBER;
                }
                if (left == ValueType.STRING && right == ValueType.STRING) {
                    return ValueType.STRING;
                }
                errors.add("Operator '+' requires both operands to be numbers or both strings, got " +
                        left + " and " + right);
                return ValueType.ERROR;

            case MINUS, STAR, SLASH:
                if (left == ValueType.NUMBER && right == ValueType.NUMBER) {
                    return ValueType.NUMBER;
                }
                errors.add("Operator '" + operator + "' requires numeric operands, got " +
                        left + " and " + right);
                return ValueType.ERROR;

            case EQEQ, NEQ, LT, GT, LTEQ, GTEQ:
                if (left == right && (left == ValueType.NUMBER || left == ValueType.STRING)) {
                    return ValueType.BOOLEAN;
                }
                errors.add("Comparison operators require both operands to be numbers or both strings, got " +
                        left + " and " + right);
                return ValueType.ERROR;

            case AND, OR:
                if (left == ValueType.BOOLEAN && right == ValueType.BOOLEAN) {
                    return ValueType.BOOLEAN;
                }
                errors.add("Logical operator '" + operator + "' requires boolean operands, got " +
                        left + " and " + right);
                return ValueType.ERROR;

            default:
                errors.add("Unsupported binary operator: " + operator);
                return ValueType.ERROR;
        }
    }

    private ValueType checkUnaryOperation(Type operator, ValueType operand) {
        if (operand == ValueType.ERROR) {
            return ValueType.ERROR;
        }

        switch (operator) {
            case MINUS:
                if (operand == ValueType.NUMBER) {
                    return ValueType.NUMBER;
                }
                errors.add("Unary minus requires numeric operand, got " + operand);
                return ValueType.ERROR;

            case EXCL:
                if (operand == ValueType.BOOLEAN) {
                    return ValueType.BOOLEAN;
                }
                errors.add("Unary '!' requires boolean operand, got " + operand);
                return ValueType.ERROR;

            default:
                errors.add("Unsupported unary operator: " + operator);
                return ValueType.ERROR;
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