package optimizer;

import parser.ast.Expression;
import parser.ast.Statement;
import parser.ast.expression.*;
import parser.ast.statement.*;
import types.Type;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AstOptimizer {

    public static List<Statement> optimize(List<Statement> statements) {
        List<Statement> optimized = new ArrayList<>();
        for (Statement stmt : statements) {
            optimized.add(optimizeStatement(stmt));
        }
        return optimized;
    }

    private static Statement optimizeStatement(Statement stmt) {
        switch (stmt) {
            case BlockStatement block -> {
                List<Statement> newStmts = new ArrayList<>();
                for (Statement s : block.statements) {
                    newStmts.add(optimizeStatement(s));
                }
                return new BlockStatement(newStmts);
            }
            case IfStatement ifStmt -> {
                Expression cond = optimizeExpression(ifStmt.condition);
                Statement thenBr = optimizeStatement(ifStmt.thenBranch);
                Statement elseBr = ifStmt.elseBranch != null ? optimizeStatement(ifStmt.elseBranch) : null;
                return new IfStatement(cond, thenBr, elseBr);
            }
            case WhileStatement whileStmt -> {
                Expression cond = optimizeExpression(whileStmt.condition);
                Statement body = optimizeStatement(whileStmt.body);
                return new WhileStatement(cond, body);
            }
            case ExpressionStatement exprStmt -> {
                Expression expr = optimizeExpression(exprStmt.expression);
                return new ExpressionStatement(expr);
            }
            case VarStatement varStmt -> {
                Expression init = varStmt.initializer != null ? optimizeExpression(varStmt.initializer) : null;
                return new VarStatement(varStmt.name, init);
            }
            case ReturnStatement retStmt -> {
                Expression val = retStmt.value != null ? optimizeExpression(retStmt.value) : null;
                return new ReturnStatement(val);
            }
            case FunctionStatement funcStmt -> {
                BlockStatement optimizedBody = (BlockStatement) optimizeStatement(funcStmt.body);
                return new FunctionStatement(funcStmt.name, funcStmt.parameters, optimizedBody);
            }
            case null, default -> {
                return stmt;
            }
        }
    }

    private static Expression optimizeExpression(Expression expr) {
        switch (expr) {
            case null -> {
                return null;
            }
            case BinaryExpression bin -> {
                Expression leftOpt = optimizeExpression(bin.left);
                Expression rightOpt = optimizeExpression(bin.right);
                BinaryExpression newBin = new BinaryExpression(leftOpt, rightOpt, bin.operator);

                Expression folded = constantFoldBinary(newBin);
                return Objects.requireNonNullElse(folded, newBin);
            }
            case UnaryExpression unary -> {
                Expression operandOpt = optimizeExpression(unary.operand);
                Expression folded = constantFoldUnary(unary.operator, operandOpt);
                return Objects.requireNonNullElseGet(folded, () -> new UnaryExpression(unary.operator, operandOpt));
            }
            case CallExpression call -> {
                List<Expression> optArgs = new ArrayList<>();
                for (Expression arg : call.arguments) {
                    optArgs.add(optimizeExpression(arg));
                }
                return new CallExpression(call.calleeName, optArgs);
            }
            case AssignExpression assign -> {
                Expression valueOpt = optimizeExpression(assign.value);
                return new AssignExpression(assign.name, valueOpt);
            }
            default -> {
                return expr;
            }
        }

    }
    private static Expression constantFoldBinary(BinaryExpression bin) {
        Expression left = bin.left;
        Expression right = bin.right;
        Type op = bin.operator;

        if (left instanceof NumberExpression && right instanceof NumberExpression) {
            double l = ((NumberExpression) left).value;
            double r = ((NumberExpression) right).value;
            double result;
            switch (op) {
                case PLUS:
                    result = l + r;
                    break;
                case MINUS:
                    result = l - r;
                    break;
                case STAR:
                    result = l * r;
                    break;
                case SLASH:
                    if (r == 0) {
                        return null;
                    }
                    result = l / r;
                    break;
                default:
                    return null;
            }
            return new NumberExpression(result);
        }

        if (op == Type.PLUS && left instanceof StringExpression && right instanceof StringExpression) {
            String l = ((StringExpression) left).value;
            String r = ((StringExpression) right).value;
            return new StringExpression(l + r);
        }

        return null;
    }

    private static Expression constantFoldUnary(Type op, Expression operand) {
        if (op == Type.MINUS && operand instanceof NumberExpression) {
            double val = ((NumberExpression) operand).value;
            return new NumberExpression(-val);
        }
        return null;
    }
}