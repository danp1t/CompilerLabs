package types;

public class KeyWords {
    public static Boolean isKeyword(String token) {
        switch (token) {
            case "var": return true;
            case "if": return true;
            case "else": return true;
            case "while": return true;
            case "print": return true;
            default: return false;
        }
    }
}
