package lexer;

import java.util.regex.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import static java.lang.System.exit;

public class Lexer {

    private static LinkedHashMap<Pattern, TokenType> patterns = new LinkedHashMap<>();
    private static Lexer instance = null;

    // not convinced of the necessity of Lexer being a singleton - Sterling
    public static Lexer Instance() {
        if (Lexer.instance == null) {
            Lexer.instance = new Lexer();
        }

        return Lexer.instance;
    }

    // Regex patterns required to match token types
    static {
        //TODO: braces, brackets, parenthesis, additional types, additional keywords, and special operators

        /*
         * For any tokens which consist of multiple characters,
         * which themselves are *also* tokens (such as +=), be sure that the
         * regular expressions for the larger tokens appear *before* the rules
         * for smaller ones. Note here how += appears before the regex for +.
         */
        patterns.put(Pattern.compile("^\\+="), TokenType.TK_PLUSEQ);
        patterns.put(Pattern.compile("^-="), TokenType.TK_MINUSEQ);
        patterns.put(Pattern.compile("^\\*="), TokenType.TK_STAREQ);
        patterns.put(Pattern.compile("^/="), TokenType.TK_SLASHEQ);
        patterns.put(Pattern.compile("^=="), TokenType.TK_EQEQUAL);
        patterns.put(Pattern.compile("^&&"), TokenType.TK_LOGAND);
        patterns.put(Pattern.compile("^\\|\\|"), TokenType.TK_LOGOR);
        patterns.put(Pattern.compile("^\\+\\+"), TokenType.TK_PPLUS);
        patterns.put(Pattern.compile("^\\+"), TokenType.TK_PLUS);
        patterns.put(Pattern.compile("^--"), TokenType.TK_MMINUS);
        patterns.put(Pattern.compile("^-"), TokenType.TK_MINUS);
        patterns.put(Pattern.compile("^\\*"), TokenType.TK_STAR);
        patterns.put(Pattern.compile("^/"), TokenType.TK_SLASH);
        patterns.put(Pattern.compile("^[=]"), TokenType.TK_EQUALS);
        patterns.put(Pattern.compile("^;"), TokenType.TK_SEMICOLON);
        patterns.put(Pattern.compile("^:"), TokenType.TK_COLON);
        patterns.put(Pattern.compile("^\\?"), TokenType.TK_QMARK);
        patterns.put(Pattern.compile("^!"), TokenType.TK_BANG);
        patterns.put(Pattern.compile("^\\)"), TokenType.TK_RPAREN);
        patterns.put(Pattern.compile("^\\("), TokenType.TK_LPAREN);
        patterns.put(Pattern.compile("^}"), TokenType.TK_RBRACE);
        patterns.put(Pattern.compile("^\\{"), TokenType.TK_LBRACE);
        patterns.put(Pattern.compile("^<="), TokenType.TK_LESSEQ);
        patterns.put(Pattern.compile("^>="), TokenType.TK_GREATEREQ);
        patterns.put(Pattern.compile("^<"), TokenType.TK_LESS);
        patterns.put(Pattern.compile("^>"), TokenType.TK_GREATER);
        patterns.put(Pattern.compile("^\\."), TokenType.TK_DOT);
        patterns.put(Pattern.compile("^,"), TokenType.TK_COMMA);
        patterns.put(Pattern.compile("^\\["), TokenType.TK_LBRACKET);
        patterns.put(Pattern.compile("^]"), TokenType.TK_RBRACKET);
        patterns.put(Pattern.compile("^\"(.*)\""), TokenType.TK_DQUOTE);
        // we reserve the right to remove and alter these lists
        patterns.put(Pattern.compile("^((int)|(char)|(void)|(double)|(float)|(long)|(short))[^A-Za-z0-9_]"), TokenType.TK_TYPE);
        patterns.put(Pattern.compile("^((if)|(return)|(while)|(for)|(goto)|(break)|(case)|(struct)|(continue)|(default)|" +
            "(do)|(else)|(extern)|(register)|(signed)|(sizeof)|(static)|(switch)|(typedef)|(union)|(unsigned)|" +
            "(volatile)|(enum))[^A-Za-z0-9_]"), TokenType.TK_KEYWORDS);
        patterns.put(Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*"), TokenType.TK_IDENTIFIER);
        patterns.put(Pattern.compile("^-?[0-9]+"), TokenType.TK_NUMBER);
        // why does -?[0-9]+ work?
    }

    /**
     * Tokenizes the lines in the c-file by iterating over the lines,
     *
     * @param lines C syntax
     * @return tokens of the file on success.
     */
    public static ArrayList<Token> tokenize(String[] lines) {

        ArrayList<Token> tokens = new ArrayList<>();
        int fileLine = 1; // Used to see where in the file failure occurs.

        for (String line : lines) {

            /* Keep reference to original string to see which 'token' isn't accepted.*/
            String originalLine = line;

            while (line.length() > 0) {

                int end = 0;
                // Move the end marker past white-space
                while (Character.isWhitespace(line.charAt(end))) end++;

                // Checking for comments within '/*' and '*/' to ignore
                if (line.charAt(end) == '/' && line.charAt(end + 1) == '*') {
                    end += 2;

                    while (line.charAt(end) != '*' && line.charAt(end + 1) != '/') end++;

                    end += 2;

                    if (end == line.length()) {
                        break;
                    }
                }

                // Checking for inline comments after '//'. If encountered, go to next line
                if (line.charAt(end) == '/' && line.charAt(end + 1) == '/') {
                    break;
                }

                //System.out.println("end = " + end);

                // Shorten string to eliminate whitespace and comments up to this point
                if (end != 0) {
                    line = line.substring(end);
                    //System.out.println("line = " + line);
                }

                int counter = 1;

                // Iterate through regex
                for (Map.Entry<Pattern, TokenType> e : Lexer.patterns.entrySet()) {

                    Matcher m = e.getKey().matcher(line);

                    counter += 1;

                    if (m.find()) {
                        // if sequence found, add to list of tokens, and shorten the string again
                        Token tk = new Token(fileLine);
                        tk.tokenType = e.getValue();

                        /*
                         * Keyword and type regexes consume an extra character
                         * since we need to make sure its not an identifier...
                         *
                         * We account for that here.
                         */
                        if (tk.tokenType == TokenType.TK_KEYWORDS ||
                            tk.tokenType == TokenType.TK_TYPE) {

                            tk.str = line.substring(m.start(), m.end() - 1);
                            line = line.substring(m.end() - 1);
                        } else {

                            tk.str = line.substring(m.start(), m.end());
                            line = line.substring(m.end());
                        }

                        //System.out.println("token = " + tk.str);
                        tokens.add(tk);

                        counter = 1;

                        /*
                         * If we encounter a valid token we break the inner
                         * loop, and return to the outer one. At this point
                         * the line has been shortened, so we look at the next
                         * non-whitespace (or comment) characters, to see
                         * if they form a valid token.
                         * We have to break here, because if we don't, some
                         * sequences of tokens may give unexpected results.
                         * i.e., int int
                         * would yield: type, identifier
                         * when it should yield: type, type
                         *
                         * This sequence of tokens is of course invalid,
                         * but it isn't the job of the lexer to know that.
                         */
                        break;
                    }

                    /* If counter reaches the size of the regex map size, it isn't a supported token. */
                    if (counter > patterns.size()) {
                        // Error and exit if there is an unrecognized token. Print location of failure.
                        System.err.println("error: unrecognized token! -- > " + line);
                        System.out.println("'lexer.Token' at position " +
                            (originalLine.indexOf(line.charAt(0)) + 1) +
                            " on line " + fileLine);
                        exit(0);
                    }
                }
            }
            fileLine++; // Next line; ; meaning line was successfully tokenized.
        }
        return tokens;
    }
}
