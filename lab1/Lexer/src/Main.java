import types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        //Вводиться строка
        //Делим на токены - разделителем является пробел внутри строки,
        // разделителем между строками является символ ;
        // Конец файла - EOF

        //Числа: 0..9
        //Операнды: / * - +
        //Переменные: xxx, yy, z, zs1113
        //KEY_WORDS: var print if else while

        //Вводиться программа построчно
        Scanner scanner = new Scanner(System.in);
        scanner.useDelimiter(";");
        List<Token> tokens = new ArrayList<>();
        int lineNumber = 0;

        while (scanner.hasNext()) {
            String line = scanner.next();
            lineNumber++;

            int pos = 0;
            while (pos < line.length()) {
                while (pos < line.length() && Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                }
                if (pos >= line.length()) break;

                int start = pos;
                while (pos < line.length() && !Character.isWhitespace(line.charAt(pos))) {
                    pos++;
                }
                String tokenStr = line.substring(start, pos);

                //Определяем класс токена
                if (Punctuation.isPunctuation(tokenStr)) {
                    switch (tokenStr) {
                        case "(" -> tokens.add(new Token(Type.LPAREN, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case ")" -> tokens.add(new Token(Type.RPAREN, tokenStr, "(" + pos + ")"));
                        case "{" -> tokens.add(new Token(Type.LBRACE, tokenStr, "(" + pos + ")"));
                        case "}" -> tokens.add(new Token(Type.RBRACE, tokenStr, "(" + pos + ")"));
                        case ";" -> tokens.add(new Token(Type.SEMICOLON, tokenStr, "(" + pos + ")"));
                    }
                }

                if (KeyWords.isKeyword(tokenStr)) {
                    switch (tokenStr) {
                        case "print" -> tokens.add(new Token(Type.PRINT, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "if" -> tokens.add(new Token(Type.IF, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "else" -> tokens.add(new Token(Type.ELSE, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "while" -> tokens.add(new Token(Type.WHILE, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "var" -> tokens.add(new Token(Type.VAR, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                    }
                } else if (Operands.isOperand(tokenStr)) {
                    switch (tokenStr) {
                        case "+" -> tokens.add(new Token(Type.PLUS, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "-" -> tokens.add(new Token(Type.MINUS, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "*" -> tokens.add(new Token(Type.STAR, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "/" -> tokens.add(new Token(Type.SLASH, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "=" -> tokens.add(new Token(Type.EQ, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "==" -> tokens.add(new Token(Type.EQEQ, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "!" -> tokens.add(new Token(Type.EXCL, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "!=" -> tokens.add(new Token(Type.NEQ, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "<" -> tokens.add(new Token(Type.LT, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case ">" -> tokens.add(new Token(Type.GT, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "<=" -> tokens.add(new Token(Type.LTEQ, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case ">=" -> tokens.add(new Token(Type.GTEQ, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "&&" -> tokens.add(new Token(Type.AND, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                        case "||" -> tokens.add(new Token(Type.OR, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                    }
                } else if (Primitive.isNumber(tokenStr)) {
                    tokens.add(new Token(Type.NUMBER, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                }
                else if (Primitive.isStringLiteral(tokenStr)) {
                    tokens.add(new Token(Type.STRING, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                }
                else if (Vars.isVariable(tokenStr)) {
                    tokens.add(new Token(Type.VAR_NAME, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                }
            }
        }

        for (Token token : tokens) {
            System.out.println(token);
        }
        scanner.close();

    }
}