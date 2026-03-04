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
        System.out.print(sb.toString());
    }

    private static void printStatement(Statement stmt, int indent, StringBuilder sb) {
        String indentation = "  ".repeat(indent);
        sb.append(indentation);

        if (stmt instanceof VarStatement) {
            VarStatement var = (VarStatement) stmt;
            sb.append("var ").append(var.name);
            if (var.initializer != null) {
                sb.append(" = ");
                printExpression(var.initializer, 0, sb);
            }
            sb.append(";");
        } else if (stmt instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) stmt;
            sb.append("if (");
            printExpression(ifStmt.condition, 0, sb);
            sb.append(") ");
            sb.append("\n");
            printStatement(ifStmt.thenBranch, indent + 1, sb);
            if (ifStmt.elseBranch != null) {
                sb.append("\n").append(indentation).append("else ");
                sb.append("\n");
                printStatement(ifStmt.elseBranch, indent + 1, sb);
            }
        } else if (stmt instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) stmt;
            sb.append("while (");
            printExpression(whileStmt.condition, 0, sb);
            sb.append(") ");
            sb.append("\n");
            printStatement(whileStmt.body, indent + 1, sb);
        } else if (stmt instanceof PrintStatement) {
            PrintStatement printStmt = (PrintStatement) stmt;
            sb.append("print ");
            printExpression(printStmt.expression, 0, sb);
            sb.append(";");
        } else if (stmt instanceof BlockStatement) {
            BlockStatement block = (BlockStatement) stmt;
            sb.append("{\n");
            for (Statement s : block.statements) {
                printStatement(s, indent + 1, sb);
                sb.append("\n");
            }
            sb.append(indentation).append("}");
        } else if (stmt instanceof ExpressionStatement) {
            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            printExpression(exprStmt.expression, 0, sb);
            sb.append(";");
        } else {
            sb.append("Unknown statement: ").append(stmt.getClass().getSimpleName());
        }
    }

    private static void printExpression(Expression expr, int indent, StringBuilder sb) {
        if (expr instanceof AssignExpression) {
            AssignExpression assign = (AssignExpression) expr;
            sb.append(assign.name).append(" = ");
            printExpression(assign.value, 0, sb);
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expr;
            sb.append("(");
            printExpression(binary.left, 0, sb);
            sb.append(" ").append(operatorToString(binary.operator)).append(" ");
            printExpression(binary.right, 0, sb);
            sb.append(")");
        } else if (expr instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expr;
            sb.append(operatorToString(unary.operator));
            printExpression(unary.operand, 0, sb);
        } else if (expr instanceof NumberExpression) {
            NumberExpression num = (NumberExpression) expr;
            sb.append(num.value);
        } else if (expr instanceof VariableExpression) {
            VariableExpression var = (VariableExpression) expr;
            sb.append(var.name);
        } else {
            sb.append("?expr?");
        }
    }

    private static String operatorToString(Type op) {
        switch (op) {
            case PLUS: return "+";
            case MINUS: return "-";
            case STAR: return "*";
            case SLASH: return "/";
            case EQ: return "=";
            case EQEQ: return "==";
            case NEQ: return "!=";
            case LT: return "<";
            case LTEQ: return "<=";
            case GT: return ">";
            case GTEQ: return ">=";
            case AND: return "&&";
            case OR: return "||";
            case EXCL: return "!";
            default: return "?";
        }
    }
}