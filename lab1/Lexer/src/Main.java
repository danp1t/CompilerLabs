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
                if (KeyWords.isKeyword(tokenStr)) {
                    tokens.add(new Token(Type.KEYWORD, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                } else if (Operands.isOperand(tokenStr)) {
                    tokens.add(new Token(Type.OPERAND, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                } else if (Equals.isEquals(tokenStr)) {
                    tokens.add(new Token(Type.EQUAL, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                } else if (Numbers.isNumber(tokenStr)) {
                    tokens.add(new Token(Type.NUMBER, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                } else if (Vars.isVariable(tokenStr)) {
                    tokens.add(new Token(Type.VARIABLE, tokenStr, "(" + lineNumber + ", " + pos + ")"));
                }
            }
        }

        for (Token token : tokens) {
            System.out.println(token);
        }
        scanner.close();

    }
}