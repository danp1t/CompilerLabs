package types;

public class Numbers {
    public static Boolean isNumber(String token) {
        for (int i = 0; i < token.length(); i++) {
            if (!Character.isDigit(token.charAt(i))) return false;
        }
        return true;
    }
}
