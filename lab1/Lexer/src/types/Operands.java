package types;

public class Operands {
    public static Boolean isOperand(String token) {
        switch (token) {
            case "+": return true;
            case "-": return true;
            case "/": return true;
            case "*": return true;
            default: return false;
        }
    }
}
