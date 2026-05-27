import optimizer.AstOptimizer;
import parser.Parser;
import parser.ast.AstPrinter;
import parser.ast.Statement;
import semantic.SemanticAnalyzer;
import interpreter.Interpreter;
import types.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String fileName = "./Lexer/src/program.txt";
        List<String> lines;
        try {
            lines = Files.readAllLines(Paths.get(fileName));
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            return;
        }

        List<Token> tokens = new ArrayList<>();
        int lineNumber = 0;

        for (String line : lines) {
            int pos = 0;
            while (pos < line.length()) {
                while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                }
                if (pos >= line.length()) break;

                if (line.charAt(pos) == '"') {
                    int start = pos;
                    pos++;
                    while (pos < line.length() && line.charAt(pos) != '"') {
                        pos++;
                    }
                    if (pos >= line.length()) {
                        throw new RuntimeException("Unterminated string literal at line " + lineNumber);
                    }
                    pos++;
                    String strToken = line.substring(start, pos);
                    tokens.add(new Token(Type.STRING, strToken, "(" + lineNumber + ", " + start + ")"));
                    continue;
                }

                boolean processed = false;
                if (pos + 1 < line.length()) {
                    String twoChars = line.substring(pos, pos + 2);
                    if (twoChars.equals("==") || twoChars.equals("!=") || twoChars.equals("<=") ||
                            twoChars.equals(">=") || twoChars.equals("&&") || twoChars.equals("||")) {
                        Type type = switch (twoChars) {
                            case "==" -> Type.EQEQ;
                            case "!=" -> Type.NEQ;
                            case "<=" -> Type.LTEQ;
                            case ">=" -> Type.GTEQ;
                            case "&&" -> Type.AND;
                            case "||" -> Type.OR;
                            default -> null;
                        };
                        tokens.add(new Token(type, twoChars, "(" + lineNumber + ", " + pos + ")"));
                        pos += 2;
                        processed = true;
                    }
                }

                if (!processed) {
                    String oneChar = line.substring(pos, pos + 1);
                    if (Punctuation.isPunctuation(oneChar)) {
                        Type type = switch (oneChar) {
                            case "(" -> Type.LPAREN;
                            case ")" -> Type.RPAREN;
                            case "{" -> Type.LBRACE;
                            case "}" -> Type.RBRACE;
                            case ";" -> Type.SEMICOLON;
                            default -> null;
                        };
                        tokens.add(new Token(type, oneChar, "(" + lineNumber + ", " + pos + ")"));
                        pos++;
                        processed = true;
                    } else if (Operands.isOperand(oneChar)) {
                        Type type = switch (oneChar) {
                            case "+" -> Type.PLUS;
                            case "-" -> Type.MINUS;
                            case "*" -> Type.STAR;
                            case "/" -> Type.SLASH;
                            case "=" -> Type.EQ;
                            case "!" -> Type.EXCL;
                            case "<" -> Type.LT;
                            case ">" -> Type.GT;
                            case "," -> Type.COMMA;
                            default -> null;
                        };
                        tokens.add(new Token(type, oneChar, "(" + lineNumber + ", " + pos + ")"));
                        pos++;
                        processed = true;
                    }
                }

                if (processed) continue;

                int start = pos;
                while (pos < line.length()) {
                    char next = line.charAt(pos);
                    if (Character.isWhitespace(next) ||
                            Punctuation.isPunctuation(String.valueOf(next)) ||
                            Operands.isOperand(String.valueOf(next))) {
                        break;
                    }
                    pos++;
                }
                String tokenStr = line.substring(start, pos);

                if (KeyWords.isKeyword(tokenStr)) {
                    Type type = switch (tokenStr) {
                        case "print" -> Type.PRINT;
                        case "if" -> Type.IF;
                        case "else" -> Type.ELSE;
                        case "while" -> Type.WHILE;
                        case "var" -> Type.VAR;
                        case "fun" -> Type.FUNC;
                        case "return" -> Type.RETURN;
                        default -> null;
                    };
                    tokens.add(new Token(type, tokenStr, "(" + lineNumber + ", " + start + ")"));
                } else if (Primitive.isNumber(tokenStr)) {
                    tokens.add(new Token(Type.NUMBER, tokenStr, "(" + lineNumber + ", " + start + ")"));
                } else if (Primitive.isStringLiteral(tokenStr)) {
                    tokens.add(new Token(Type.STRING, tokenStr, "(" + lineNumber + ", " + start + ")"));
                } else if (Vars.isVariable(tokenStr)) {
                    tokens.add(new Token(Type.ID, tokenStr, "(" + lineNumber + ", " + start + ")"));
                } else {
                    throw new RuntimeException("Unknown token: " + tokenStr + " at line " + lineNumber);
                }
            }
        }
        tokens.add(new Token(Type.EOF, "EOF", "(" + lineNumber + ")"));

        Parser parser = new Parser(tokens);
        List<Statement> statements;
        try {
            statements = parser.parse();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("=== AST BEFORE OPTIMIZATION ===");
        AstPrinter.print(statements);

        statements = AstOptimizer.optimize(statements);
        System.out.println("\n=== AST AFTER OPTIMIZATION ===");
        AstPrinter.print(statements);

        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(statements);
        if (!analyzer.getErrors().isEmpty()) {
            System.err.println("Semantic errors found:");
            for (String error : analyzer.getErrors()) {
                System.err.println(error);
            }
            return;
        }

        Interpreter interpreter = new Interpreter();
        try {
            interpreter.interpret(statements);
        } catch (RuntimeException e) {
            System.err.println("Runtime error: " + e.getMessage());
        }
    }
}