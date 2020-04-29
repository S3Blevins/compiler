package lexer;

public class Token {

    public String str;
    public TokenType tokenType;
    public Integer lineNumber;
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
    public Token() {
    }

    public Token(String str) {
        this.str = str;
    }

    public Token(Integer line) {
        this.lineNumber = line;
    }

    /**
     * Constructor to assign fields in class.
     *
     * @param str       The string that corresponds to the token.
     * @param tokenType the token generated from the string.
     */
    public Token(String str, TokenType tokenType) {
        this.str = str;
        this.tokenType = tokenType;
        this.lineNumber = null;
    }

    public Token(String str, TokenType tokenType, Integer line) {
        this.str = str;
        this.tokenType = tokenType;
        this.lineNumber = line;
    }

    public void tokFullError() {
        System.err.println("ERROR: There was an error on line: " + this.lineNumber + " with token '" + this.str + "'");

    }

    public String tokError() {
        return ("on line: " + this.lineNumber + " with token '" + this.str + "'");
    }
}
