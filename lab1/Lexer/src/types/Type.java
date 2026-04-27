package types;

public enum Type {
    NUMBER,
    ID,
    STRING,

    VAR,
    PRINT,
    IF, ELSE,
    WHILE,      // while
    FUNC, RETURN,

    // Operators
    PLUS, MINUS, STAR, SLASH,   // + - * /
    EQ, EQEQ, EXCL, NEQ,        // = == ! !=
    LT, GT, LTEQ, GTEQ,         // < > <= >=
    AND, OR, COMMA,                 // && ||

    // Grouping & Punctuation
    LPAREN, RPAREN, // ( )
    LBRACE, RBRACE, // { }
    SEMICOLON,      // ;

    EOF             // Конец файла
}
