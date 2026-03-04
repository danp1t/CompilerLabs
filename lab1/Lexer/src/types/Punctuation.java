package types;

public class Punctuation {
    public static Boolean isPunctuation(String token) {
        switch (token) {
            case "(": return true;
            case ")": return true;
            case "{": return true;
            case "}": return true;
            case ";": return true;
            default: return false;
        }
    }
}
