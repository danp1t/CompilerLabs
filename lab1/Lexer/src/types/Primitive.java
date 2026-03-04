package types;

public class Primitive {
    public static Boolean isNumber(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) return false;
        }
        return true;
    }

    public static boolean isStringLiteral(String token) {
        if (token == null || token.length() < 2) return false;

        char first = token.charAt(0);
        char last = token.charAt(token.length() - 1);

        return (first == '"' || first == '\'') && first == last;
    }
}
