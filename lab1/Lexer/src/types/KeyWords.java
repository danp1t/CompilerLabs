package types;

public class KeyWords {
    public static Boolean isKeyword(String token) {
        return switch (token) {
            case "var", "if", "else", "while", "print" -> true;
            default -> false;
        };
    }
}
