import org.junit.Assert;
import org.junit.Test;

public class LexerTest {

    @Test
    public void testLexar() {

        String expected = "[(int, TK_TYPE)," +
                " ( , TK_SPACE)," +
                " (x, TK_IDENTIFIER)," +
                " (;, TK_SEMICOLON)]";

        String[] lines = {"int x;"};

        /* To String because some objects have trailing space which fails even if both are equal. */
        Assert.assertEquals(expected, new Lexer().tokenize(lines).toString());
    }

    @Test
    public void testComplex() {

        String expected = "[(int, TK_TYPE)," +
                " ( , TK_SPACE)," +
                " (foobar1, TK_IDENTIFIER)," +
                " ( , TK_SPACE)," +
                " (=, TK_EQUALS)," +
                " ( , TK_SPACE)," +
                " (1680, TK_NUMBER)," +
                " ( , TK_SPACE)," +
                " (+, TK_PLUS)," +
                " ( , TK_SPACE)," +
                " (200, TK_NUMBER)," +
                " ( , TK_SPACE)," +
                " (-, TK_MINUS)," +
                " ( , TK_SPACE)," +
                " (12, TK_NUMBER)," +
                " (;, TK_SEMICOLON)]";

        String[] lines = {"int foobar1 = 1680 + 200 - 12;"};

        /* To String because some objects have trailing space which fails even if both are equal. */
        Assert.assertEquals(expected, new Lexer().tokenize(lines).toString());

    }
}