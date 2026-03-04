import parser.Parser;
import parser.ast.AstPrinter;
import parser.ast.Statement;
import types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<Token> tokens = new ArrayList<>();
        int lineNumber = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            lineNumber++;

            int pos = 0;
            while (pos < line.length()) {
                while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                }
                if (pos >= line.length()) break;

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

        scanner.close();

        Parser parser = new Parser(tokens);
        List<Statement> statements;
        try {
            statements = parser.parse();
        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            return;
        }

        System.out.println("Parsing successful! AST nodes:");
        AstPrinter.print(statements);
    }
}