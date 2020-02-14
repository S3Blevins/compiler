package lexer;

public enum TokenType {
        TK_PLUSEQ("+="),
        TK_MINUSEQ("-="),
        TK_STAREQ("*="),
        TK_SLASHEQ("/="),
        TK_EQEQUAL("=="),
        TK_RPAREN(")"),
        TK_LPAREN("("),
        TK_RBRACE("}"),
        TK_LBRACE("{"),
        TK_RBRACKET("]"),
        TK_LBRACKET("["),
        TK_PLUS("+"),
        TK_MINUS("-"),
        TK_STAR("*"),
        TK_SLASH("/"),
        TK_SEMICOLON(";"),
        TK_COLON(":"),
        TK_QMARK("?"),
        TK_BANG("!"),
        TK_DOT("."),
        TK_COMMA(","),
        TK_DQUOTE("STRING"),
        TK_KEYWORDS("KEYWORD"),
        TK_TYPE("TYPE"),
        TK_IDENTIFIER("IDENTIFIER"),
        TK_NUMBER("NUMBER"),
        TK_EQUALS("="),
        TK_LESS("<"),
        TK_GREATER(">"),
        TK_LESSEQ("<="),
        TK_GREATEREQ(">="),
        TK_EOF("EOF");

        private final String token;

        public String getToken() {
                return this.token;
        }

        TokenType(final String token) {
                this.token = token;
        }

        public String toString() {return token;}
}
