package types;

public class Punctuation {
    public static Boolean isPunctuation(String token) {
        return switch (token) {
            case "(", ")", "{", "}", ";", "[", "]" -> true;
            default -> false;
        };
    }
}
