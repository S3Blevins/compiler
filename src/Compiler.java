import java.util.ArrayList;

public class Compiler {

    public static void main(String[] args) {
        String[] lines = {
                "int x;",
                "int test = x;",
        };

        // Since tokenize is a static method, and Lexer is written as a static
        // class, you probably shouldn't make an instance of it.
        // If you want to make an instance we should probably modify Lexer
        // to be a singleton. Right now this produces a warning on call to
        // lexer.tokenize()
        Lexer lexer = new Lexer();

        ArrayList<Token> tokens = lexer.tokenize(lines);
        System.out.println("\nTokens");
        for (Token token : tokens) {
            System.out.println(token.str + " --> " + token.tokenType);
        }
    }
}
