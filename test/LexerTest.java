import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

public class LexerTest {

    @Test
    public void testLexar() {

        ArrayList<Token> expected = new ArrayList<>();
        expected.add(new Token());
        expected.add(new Token());
        expected.add(new Token());

        expected.get(0).str = "int";
        expected.get(0).tokenType = TokenType.TK_TYPE;

        expected.get(1).str = "x";
        expected.get(1).tokenType = TokenType.TK_IDENTIFIER;

        expected.get(2).str = ";";
        expected.get(2).tokenType = TokenType.TK_SEMICOLON;

        String[] lines = {"int x;"};

        /* To String because some objects have trailing space which fails even if both are equal. */
        Assert.assertEquals(expected.toString(), new Lexer().tokenize(lines).toString());
    }
}