package types;

public class Equals {
    public static Boolean isEquals(String token) {
        switch (token) {
            case "=": return true;
            default: return false;
        }
    }
}
