package parser.ast;

import parser.ast.expression.*;
import parser.ast.statement.*;
import types.Type;

import java.util.List;

public class AstPrinter {
    public static void print(List<Statement> statements) {
        StringBuilder sb = new StringBuilder();
        for (Statement stmt : statements) {
            printStatement(stmt, 0, sb);
            sb.append("\n");
        }
        System.out.print(sb.toString()); // реально выводим на консоль
    }

    private static void printStatement(Statement stmt, int indent, StringBuilder sb) {
        String indentation = "  ".repeat(indent);
        sb.append(indentation);

        switch (stmt) {
            case VarStatement var -> {
                sb.append("var ").append(var.name);
                if (var.initializer != null) {
                    sb.append(" = ");
                    printExpression(var.initializer, sb);
                }
                sb.append(";");
            }
            case IfStatement ifStmt -> {
                sb.append("if (");
                printExpression(ifStmt.condition, sb);
                sb.append(") ");
                sb.append("\n");
                printStatement(ifStmt.thenBranch, indent + 1, sb);
                if (ifStmt.elseBranch != null) {
                    sb.append("\n").append(indentation).append("else ");
                    sb.append("\n");
                    printStatement(ifStmt.elseBranch, indent + 1, sb);
                }
            }
            case WhileStatement whileStmt -> {
                sb.append("while (");
                printExpression(whileStmt.condition, sb);
                sb.append(") ");
                sb.append("\n");
                printStatement(whileStmt.body, indent + 1, sb);
            }
            case PrintStatement printStmt -> {
                sb.append("print ");
                printExpression(printStmt.expression, sb);
                sb.append(";");
            }
            case BlockStatement block -> {
                sb.append("{\n");
                for (Statement s : block.statements) {
                    printStatement(s, indent + 1, sb);
                    sb.append("\n");
                }
                sb.append(indentation).append("}");
            }
            case ExpressionStatement exprStmt -> {
                printExpression(exprStmt.expression, sb);
                sb.append(";");
            }
            case FunctionStatement func -> {
                sb.append("fun ").append(func.name).append("(");
                for (int i = 0; i < func.parameters.size(); i++) {
                    if (i > 0) sb.append(", ");
                    sb.append(func.parameters.get(i));
                }
                sb.append(") ");
                printStatement(func.body, indent, sb);
            }
            case ReturnStatement ret -> {
                sb.append("return");
                if (ret.value != null) {
                    sb.append(" ");
                    printExpression(ret.value, sb);
                }
                sb.append(";");
            }
            case null, default -> sb.append("Unknown statement: ").append(stmt.getClass().getSimpleName());
        }
    }

    private static void printExpression(Expression expr, StringBuilder sb) {
        if (expr == null) {
            sb.append("null");
            return;
        }
        switch (expr) {
            case AssignExpression assign -> {
                sb.append(assign.name).append(" = ");
                printExpression(assign.value, sb);
            }
            case BinaryExpression binary -> {
                sb.append("(");
                printExpression(binary.left, sb);
                sb.append(" ").append(operatorToString(binary.operator)).append(" ");
                printExpression(binary.right, sb);
                sb.append(")");
            }
            case UnaryExpression unary -> {
                sb.append(operatorToString(unary.operator));
                printExpression(unary.operand, sb);
            }
            case NumberExpression num -> sb.append(num.value);
            case StringExpression str -> sb.append("\"").append(str.value).append("\"");
            case VariableExpression var -> sb.append(var.name);
            case CallExpression call -> {
                sb.append(call.calleeName).append("(");
                for (int i = 0; i < call.arguments.size(); i++) {
                    if (i > 0) sb.append(", ");
                    printExpression(call.arguments.get(i), sb);
                }
                sb.append(")");
            }
            case null, default -> sb.append("?expr?");
        }
    }

    private static String operatorToString(Type op) {
        return switch (op) {
            case PLUS -> "+";
            case MINUS -> "-";
            case STAR -> "*";
            case SLASH -> "/";
            case EQ -> "=";
            case EQEQ -> "==";
            case NEQ -> "!=";
            case LT -> "<";
            case LTEQ -> "<=";
            case GT -> ">";
            case GTEQ -> ">=";
            case AND -> "&&";
            case OR -> "||";
            case EXCL -> "!";
            default -> "?";
        };
    }
}