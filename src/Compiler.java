import java.util.ArrayList;

public class Compiler {

    public static void main(String[] args) {
        String[] lines = {
                "int x;",
        };

        Lexer lexer = new Lexer();

        ArrayList<Token> tokens = lexer.tokenize(lines);
        System.out.println("\nTokens");
        for (Token token : tokens) {
            System.out.println(token.str + " --> " + token.tokenType);
        }
    }
}
