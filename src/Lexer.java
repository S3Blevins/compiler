import java.util.regex.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Lexer {

    private static LinkedHashMap<Pattern, TokenType> patterns = new LinkedHashMap<>();

    // Regex patterns required to match token types
    static {
        //TODO: braces, brackets, parenthesis, additional types, additional keywords, and special operators

        /**
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
        patterns.put(Pattern.compile("^\\+"), TokenType.TK_PLUS);
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
        // we reserve the right to remove and alter these lists
        patterns.put(Pattern.compile("^(int)|(char)|(void)|(double)|(float)|(long)|(short)"), TokenType.TK_TYPE);
        patterns.put(Pattern.compile("^(if)|(return)|(while)|(for)|(goto)|(break)|(case)|(struct)|(continue)|(default)|" +
                "(do)|(else)|(extern)|(register)|(signed)|(sizeof)|(static)|(switch)|(typedef)|(union)|(unsigned)|" +
                "(volatile)"), TokenType.TK_KEYWORDS);
        patterns.put(Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*"), TokenType.TK_IDENTIFIER);
        patterns.put(Pattern.compile("^-?[0-9]+"), TokenType.TK_NUMBER);
        // why does -?[0-9]+ work?
    }

    /**
     * Tokenizes the lines in the c-file by iterating over the lines,
     * @param lines C syntax
     * @return tokens of the file on success.
     */
    public static ArrayList<Token> tokenize(String[] lines) {

        ArrayList<Token> tokens = new ArrayList<>();
        int fileLine = 1; // Used to see where in the file failure occurs.

        for(String line : lines) {

            /* Keep reference to original string to see which 'token' isn't accepted.*/
            String originalLine = line;

            while(line.length() > 0) {

                int end = 0;
                // Move the end marker past white-space
                while(Character.isWhitespace(line.charAt(end))) end++;

                // Checking for comments within /* and */ to ignore
                if(line.charAt(end) == '/' && line.charAt(end + 1) == '*') {
                    end += 2;

                    while(line.charAt(end) != '*' && line.charAt(end + 1) != '/') end++;

                    end += 2;

                    if(end == line.length()) {
                        break;
                    }
                }

                System.out.println("end = " + end);

                // Shorten string to eliminate whitespace and comments up to this point
                if(end != 0) {
                    line = line.substring(end);
                    System.out.println("line = " + line);
                }

                int counter = 1;

                // Iterate through regex
                for(Map.Entry<Pattern, TokenType> e : Lexer.patterns.entrySet()) {

                    Matcher m = e.getKey().matcher(line);

                    counter += 1;

                    if(m.find()) {
                        // if sequence found, add to list of tokens, and shorten the string again
                        Token tk = new Token();
                        tk.str = line.substring(m.start(), m.end());
                        System.out.println("token = " + tk.str);
                        tk.tokenType = e.getValue();
                        tokens.add(tk);

                        line = line.substring(m.end());
                        counter = 1;
                    }

                    /* If counter reaches the size of the regex map size, it isn't a supported token. */
                    if (counter > patterns.size()){
                        // Error and exit if there is an unrecognized token. Print location of failure.
                        System.err.println("error: unrecognized token! -- > " + line);
                        System.out.println("'Token' at position " +
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
