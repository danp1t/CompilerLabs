package interpreter;

import parser.ast.Expression;
import parser.ast.Statement;
import parser.ast.expression.*;
import parser.ast.statement.*;
import types.Type;

import java.util.List;

public class Interpreter {
    private RuntimeEnvironment environment = new RuntimeEnvironment();

    public void interpret(List<Statement> statements) {
        for (Statement stmt : statements) {
            execute(stmt);
        }
    }

    private void execute(Statement stmt) {
        switch (stmt) {
            case VarStatement var -> {
                Object value = null;
                if (var.initializer != null) {
                    value = evaluate(var.initializer);
                }
                environment.define(var.name, value);
            }
            case PrintStatement print -> {
                Object value = evaluate(print.expression);
                System.out.println(stringify(value));
            }
            case ExpressionStatement exprStmt -> evaluate(exprStmt.expression);
            case BlockStatement block -> {
                RuntimeEnvironment previous = environment;
                environment = environment.pushScope();
                try {
                    for (Statement inner : block.statements) {
                        execute(inner);
                    }
                } finally {
                    environment = previous;
                }
            }
            case IfStatement ifStmt -> {
                Object cond = evaluate(ifStmt.condition);
                if (isTruthy(cond)) {
                    execute(ifStmt.thenBranch);
                } else if (ifStmt.elseBranch != null) {
                    execute(ifStmt.elseBranch);
                }
            }
            case WhileStatement whileStmt -> {
                while (isTruthy(evaluate(whileStmt.condition))) {
                    execute(whileStmt.body);
                }
            }
            case null, default -> throw new RuntimeException("Unsupported statement: " + stmt.getClass().getName());
        }
    }

    private Object evaluate(Expression expr) {
        switch (expr) {
            case NumberExpression num -> {
                return num.value;
            }
            case StringExpression str -> {
                return str.value;
            }
            case VariableExpression var -> {
                return environment.get(var.name);
            }
            case AssignExpression assign -> {
                Object value = evaluate(assign.value);
                environment.assign(assign.name, value);
                return value;
            }
            case BinaryExpression bin -> {
                Object left = evaluate(bin.left);
                Object right = evaluate(bin.right);
                return applyBinary(bin.operator, left, right);
            }
            case UnaryExpression unary -> {
                Object operand = evaluate(unary.operand);
                return applyUnary(unary.operator, operand);
            }
            case null, default -> throw new RuntimeException("Unsupported expression: " + expr.getClass().getName());
        }
    }

    private Object applyBinary(Type operator, Object left, Object right) {
        switch (operator) {
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left + (Double) right;
                } else if (left instanceof String && right instanceof String) {
                    return (String) left + (String) right;
                }
                break;
            case MINUS:
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left - (Double) right;
                }
                break;
            case STAR:
                if (left instanceof Double && right instanceof Double) {
                    return (Double) left * (Double) right;
                }
                break;
            case SLASH:
                if (left instanceof Double && right instanceof Double) {
                    double divisor = (Double) right;
                    if (divisor == 0.0) {
                        throw new RuntimeException("Division by zero");
                    }
                    return (Double) left / divisor;
                }
                break;
            case EQEQ:
                return isEqual(left, right);
            case NEQ:
                return !isEqual(left, right);
            case LT:
                return compare(left, right) < 0;
            case LTEQ:
                return compare(left, right) <= 0;
            case GT:
                return compare(left, right) > 0;
            case GTEQ:
                return compare(left, right) >= 0;
            case AND:
                return isTruthy(left) && isTruthy(right);
            case OR:
                return isTruthy(left) || isTruthy(right);
            default:
                throw new RuntimeException("Unknown binary operator: " + operator);
        }
        throw new RuntimeException("Type mismatch for operator " + operator);
    }

    private Object applyUnary(Type operator, Object operand) {
        switch (operator) {
            case MINUS:
                if (operand instanceof Double) {
                    return -(Double) operand;
                }
                break;
            case EXCL:
                return !isTruthy(operand);
            default:
                throw new RuntimeException("Unknown unary operator: " + operator);
        }
        throw new RuntimeException("Type mismatch for unary operator " + operator);
    }

    private boolean isTruthy(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        return value != null;
    }

    private boolean isEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a instanceof Double && b instanceof Double) {
            return ((Double) a).doubleValue() == ((Double) b).doubleValue();
        }
        return a.equals(b);
    }

    private int compare(Object a, Object b) {
        if (a instanceof Double && b instanceof Double) {
            return Double.compare((Double) a, (Double) b);
        } else if (a instanceof String && b instanceof String) {
            return ((String) a).compareTo((String) b);
        } else {
            throw new RuntimeException("Cannot compare " + a.getClass() + " with " + b.getClass());
        }
    }

    private String stringify(Object value) {
        if (value == null) return "null";
        if (value instanceof Double) {
            double d = (Double) value;
            if (d == (long) d) {
                return String.valueOf((long) d);
            }
            return value.toString();
        }
        return value.toString();
    }
}