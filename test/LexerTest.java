import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class LexerTest {

    @Test
    public void testLexar() {

        String expected = "[(int, TK_TYPE)," +
                " (x, TK_IDENTIFIER)," +
                " (;, TK_SEMICOLON)]";

        String[] lines = {"int x;"};

        /* To String because some objects have trailing space which fails even if both are equal. */
        Assert.assertEquals(expected, Lexer.tokenize(lines).toString());
    }

    @Test
    public void testComplex() {

        String expected = "[(int, TK_TYPE)," +
                " (foobar1, TK_IDENTIFIER)," +
                " (=, TK_EQUALS)," +
                " (1680, TK_NUMBER)," +
                " (+, TK_PLUS)," +
                " (200, TK_NUMBER)," +
                " (-, TK_MINUS)," +
                " (12, TK_NUMBER)," +
                " (;, TK_SEMICOLON)]";

        String[] lines = {"int foobar1 = 1680 + 200 - 12;"};

        /* To String because some objects have trailing space which fails even if both are equal. */
        Assert.assertEquals(expected, Lexer.tokenize(lines).toString());
    }

    @Test
    public void testSimpleForLoop() {

        String[] lines = {"int a = 0;\nfor (int i = 0; i < 10; i += 1) {a += 1;}"};
        TokenType[] tokensTypes = {
                TokenType.TK_TYPE, TokenType.TK_IDENTIFIER, TokenType.TK_EQUALS, TokenType.TK_NUMBER, TokenType.TK_SEMICOLON,
                TokenType.TK_KEYWORDS, TokenType.TK_LPAREN, TokenType.TK_TYPE, TokenType.TK_IDENTIFIER, TokenType.TK_EQUALS,
                TokenType.TK_NUMBER, TokenType.TK_SEMICOLON, TokenType.TK_IDENTIFIER, TokenType.TK_LESS, TokenType.TK_NUMBER,
                TokenType.TK_SEMICOLON, TokenType.TK_IDENTIFIER, TokenType.TK_PLUSEQ, TokenType.TK_NUMBER, TokenType.TK_RPAREN,
                TokenType.TK_LBRACE, TokenType.TK_IDENTIFIER, TokenType.TK_PLUSEQ, TokenType.TK_NUMBER, TokenType.TK_SEMICOLON,
                TokenType.TK_RBRACE};
        String[] strs = {"int", "a", "=", "0", ";",
                        "for", "(", "int", "i", "=", "0", ";",
                        "i", "<", "10", ";",
                        "i", "+=", "1", ")",
                        "{", "a", "+=", "1", ";", "}"};
        ArrayList<Token> expected = new ArrayList<>();

        for (int i = 0; i < strs.length; i++) {
            expected.add(new Token(strs[i], tokensTypes[i]));
        }

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldIgnoreCommentAndReturnNothing() {
        String[] lines = {"/* THIS IS A COMMENT AND SHOULD BE IGNORED. */"};

        ArrayList<String> expected = new ArrayList<>(){{add("");}};

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldTokenizeParenthesis() {

        String[] lines = {"int parens(int a, int b);"};
        TokenType[] tokensTypes = {TokenType.TK_TYPE, TokenType.TK_IDENTIFIER, TokenType.TK_LPAREN,
                TokenType.TK_TYPE, TokenType.TK_IDENTIFIER, TokenType.TK_COMMA, TokenType.TK_TYPE,
                TokenType.TK_IDENTIFIER, TokenType.TK_RPAREN, TokenType.TK_SEMICOLON};
        String[] strs = {"int", "parens", "(", "int", "a", ",", "int", "b", ")", ";"};
        ArrayList<Token> expected = new ArrayList<>();

        for (int i = 0; i < strs.length; i++) {
            expected.add(new Token(strs[i], tokensTypes[i]));
        }

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldTokenizeFunctionPrototype() {
        String[] lines = {"int prototype_test(int abcdefghijklmnopqrstuvwxyz, int qwerty, int third, int fourth);"};
        String[] strs = {"int", "prototype_test", "(", "int", "abcdefghijklmnopqrstuvwxyz", ",", "int", "qwerty",
                        ",", "int", "third", ",", "int", "fourth", ")", ";"};
        TokenType[] tokenTypes = {TokenType.TK_TYPE, TokenType.TK_IDENTIFIER, TokenType.TK_LPAREN, TokenType.TK_TYPE,
                TokenType.TK_IDENTIFIER, TokenType.TK_COMMA, TokenType.TK_TYPE, TokenType.TK_IDENTIFIER,
                TokenType.TK_COMMA, TokenType.TK_TYPE, TokenType.TK_IDENTIFIER, TokenType.TK_COMMA, TokenType.TK_TYPE,
                TokenType.TK_IDENTIFIER, TokenType.TK_RPAREN, TokenType.TK_SEMICOLON};

        ArrayList<Token> excepted = new ArrayList<>();

        for (int i = 0; i < tokenTypes.length; i++) {
            excepted.add(new Token(strs[i], tokenTypes[i]));
        }

        Assert.assertEquals(excepted.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldTokenizeEveryOperation() {

        String[] lines = {"+= -= *= /= == + - * / = ; : ? ! ( ) . ,"};

        String[] strs = lines[0].split(" ");
        TokenType[] tokenTypes = {TokenType.TK_PLUSEQ, TokenType.TK_MINUSEQ, TokenType.TK_STAREQ, TokenType.TK_SLASHEQ,
                TokenType.TK_EQEQUAL, TokenType.TK_PLUS, TokenType.TK_MINUS, TokenType.TK_STAR, TokenType.TK_SLASH,
                TokenType.TK_EQUALS, TokenType.TK_SEMICOLON, TokenType.TK_COLON, TokenType.TK_QMARK, TokenType.TK_BANG,
                TokenType.TK_LPAREN, TokenType.TK_RPAREN, TokenType.TK_DOT, TokenType.TK_COMMA};

        ArrayList<Token> expected = new ArrayList<>();

        for (int i = 0; i < strs.length; i++) {
            expected.add(new Token(strs[i], tokenTypes[i]));
        }

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldTokenizeEveryType() {
        String[] lines = {"int char void double float long short"};
        String[] strs = lines[0].split(" ");
        TokenType[] tokenTypes = new TokenType[strs.length];

        for (int i = 0; i < strs.length - 1; i++) {
            tokenTypes[i] = TokenType.TK_TYPE;
        }

        /* Same reason as in 'shouldTokenizeEveryKeyword() */
        tokenTypes[strs.length - 1] = TokenType.TK_IDENTIFIER;

        ArrayList<Token> expected = new ArrayList<>();

        for (int i = 0; i < tokenTypes.length; i++) {
            expected.add(new Token(strs[i], tokenTypes[i]));
        }

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldTokenizeEveryKeyword() {
        String[] lines = {"for goto break case struct continue default do else extern register signed sizeof static switch"};
        String[] strs = lines[0].trim().split(" ");
        TokenType[] tokenTypes = new TokenType[strs.length];

        for (int i = 0; i < strs.length - 1; i++) {
            tokenTypes[i] = TokenType.TK_KEYWORDS;
        }

        /* This is technically false but there is never an instance where keywords will be besides each other. */
        tokenTypes[strs.length - 1] = TokenType.TK_IDENTIFIER;

        ArrayList<Token> expected = new ArrayList<>();

        for (int i = 0; i < tokenTypes.length; i++) {
            expected.add(new Token(strs[i], tokenTypes[i]));
        }

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }

    @Test
    public void shouldTokenizeEveryNumber() {

        String[] lines = {"0123456789089876543221 12 333 3 4235 23532535252 4643634 123498765 12345 23432 42435625235141"};

        String[] strs = lines[0].trim().split(" ");
        TokenType[] tokenTypes = new TokenType[strs.length];

        for (int i = 0; i < strs.length; i++) {
            tokenTypes[i] = TokenType.TK_NUMBER;
        }

        ArrayList<Token> expected = new ArrayList<>();

        for (int i = 0; i < tokenTypes.length; i++) {
            expected.add(new Token(strs[i], tokenTypes[i]));
        }

        Assert.assertEquals(expected.toString(), Lexer.tokenize(lines).toString());
    }
}