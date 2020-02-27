package lexer;

public class Token {

        public String str;
        public TokenType tokenType;
        /*
         * TODO: Consider adding line/column information here
         * so that we can include line/column info in parse errors
         */

        public String toString() {

            return "(" + str + ", " + tokenType + ")";
        }

    /**
     * Default constructor
     */
    public Token(){}

    public Token(String str) {
        this.str = str;
    }

    /**
     * Constructor to assign fields in class.
     * @param str The string that corresponds to the token.
     * @param tokenType the token generated from the string.
     */
    public Token(String str, TokenType tokenType) {
        this.str = str;
        this.tokenType = tokenType;
    }
}
