package types;

public class Token {
    private Type type;
    private String value;
    private String position;

    public Token(Type type, String value, String position) {
        this.type = type;
        this.value = value;
        this.position = position;
    }

    public Token(Type type, String value) {
        this.type = type;
        this.value = value;
    }

    public Token() {}

    public void setPosition(String position) {
        this.position = position;
    }

    public String toString() {
        return type + " " + value + " " + position;
    }
}
