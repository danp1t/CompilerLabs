package types;

public class Operands {
    public static Boolean isOperand(String token) {
        return switch (token) {
            case "+", "-", "/", "*", "=", "==", "!=", "!", "<", ">", "<=", ">=", "&&", "||" -> true;
            default -> false;
        };
    }
}
