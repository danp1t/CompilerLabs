package types;

public class Vars {
    public static Boolean isVariable(String token) {
        if (!Character.isLetter(token.charAt(0))) return false;

        for (int i = 1; i < token.length(); i++) {
            char c = token.charAt(i);
            if (!Character.isLetterOrDigit(c)) return false;
        }

        return true;
    }
}
