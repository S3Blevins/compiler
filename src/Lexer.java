import java.util.regex.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Lexer {

    private static LinkedHashMap<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

    // regex patterns required to match token types
    static {
        //TODO: braces, brackets, parenthesis, additional types, additional keywords, and special operators

        patterns.put(Pattern.compile("^\\+"), TokenType.TK_PLUS);
        patterns.put(Pattern.compile("^-"), TokenType.TK_MINUS);
        patterns.put(Pattern.compile("^;"), TokenType.TK_SEMICOLON);
        patterns.put(Pattern.compile("^(int)|(char)|(void)"), TokenType.TK_TYPE);
        patterns.put(Pattern.compile("^(main)|(return)"), TokenType.TK_KEYWORDS);
        patterns.put(Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*"), TokenType.TK_IDENTIFIER);
    }

    /**
     * Tokenizes the lines in the c-file by iterating over the lines,
     * @param lines
     * @return
     */
    public static ArrayList<Token> tokenize(String[] lines) {

        ArrayList<Token> tokens = new ArrayList<>();

        for(String line : lines) {
            while(line.length() > 0) {
                // end is a position marker
                int end = 0;
                // move the end marker past white-space
                while(Character.isWhitespace(line.charAt(end))) end++;

                // checking for comments within /* and */ to ignore
                if(line.charAt(end) == '/' && line.charAt(end + 1) == '*') {
                    end += 2;

                    while(line.charAt(end) != '*' && line.charAt(end + 1) != '/') end++;
                }

                // shorten string to eliminate whitespace and comments up to this point
                if(end != 0) {
                    line = line.substring(end, line.length());
                }

                // iterate through regex
                for(Map.Entry<Pattern, TokenType> e : Lexer.patterns.entrySet()) {

                    Matcher m = e.getKey().matcher(line);

                    if(m.find()) {
                        // if sequence found, add to list of tokens, and shorten the string again
                        Token tk = new Token();
                        tk.str = line.substring(m.start(), m.end()) + " --> ";
                        tk.tokenType = e.getValue();
                        tokens.add(tk);

                        System.out.println(end);
                        line = line.substring(m.end(), line.length());

                    } else {
                        // error and exit if there is an unrecognized token.
                        System.out.println("error: unrecognized token!");
                        exit(0);
                    }

                }
            }
        }
        return tokens;
    }
}
