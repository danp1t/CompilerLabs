package types;

public enum Type {
    NUMBER,
    ID,
    STRING,
    VAR_NAME,

    VAR,
    PRINT,
    IF, ELSE,
    WHILE,      // while

    // Operators
    PLUS, MINUS, STAR, SLASH,   // + - * /
    EQ, EQEQ, EXCL, NEQ,        // = == ! !=
    LT, GT, LTEQ, GTEQ,         // < > <= >=
    AND, OR,                    // && ||

    // Grouping & Punctuation
    LPAREN, RPAREN, // ( )
    LBRACE, RBRACE, // { }
    SEMICOLON,      // ;

    EOF             // Конец файла
}
