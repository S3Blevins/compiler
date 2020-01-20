import java.util.regex.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.LinkedHashMap;

public class Lexer {

        private static LinkedHashMap<Pattern, TokenType> patterns = new LinkedHashMap<Pattern, TokenType>();

        /**
         * I Hate.
         */
        static {

                patterns.put(Pattern.compile("^\\+"), TokenType.TK_PLUS);
                patterns.put(Pattern.compile("^-"), TokenType.TK_MINUS);
                patterns.put(Pattern.compile("^;"), TokenType.TK_SEMICOLON);
                patterns.put(Pattern.compile("^(int)"), TokenType.TK_TYPE);
                patterns.put(Pattern.compile("^[A-Za-z_][A-Za-z0-9_]*"), TokenType.TK_IDENTIFIER);
        }

        public static enum TokenType {

                TK_PLUS,
                TK_MINUS,
                TK_SEMICOLON,
                TK_TYPE,
                TK_IDENTIFIER

        }

        public static class Token {

                String str;

                TokenType tokenType;

                public String toString() {

                        return str.toString() + tokenType.toString();
                }
        }

        public static ArrayList<Token> tokenize(String[] lines) {

                ArrayList<Token> tokens = new ArrayList<Token>();

                for(String line : lines) {

                        while(line.length() > 0) {

                                int end = 0;
                                while(Character.isWhitespace(line.charAt(end))) end++;

                                if(line.charAt(end) == '/' && line.charAt(end + 1) == '*') {

                                        end += 2;

                                        while(line.charAt(end) != '*' && line.charAt(end + 1) != '/') end++;
                                }

                                if(end != 0) {

                                        line = line.substring(end, line.length());
                                        System.out.println(line);
                                }

                                for(Map.Entry<Pattern, TokenType> e : Lexer.patterns.entrySet()) {

                                        Matcher m = e.getKey().matcher(line);
                                        System.out.println(m);
                                        if(m.find()) {
                                        System.out.println(m);

                                                Token tk = new Token();
                                                tk.str = line.substring(m.start(), m.end());
                                                tk.tokenType = e.getValue();
                                                tokens.add(tk);

                                                line = line.substring(m.end(), line.length());
                                        }
                                }
                        }

                }


                return tokens;
        }

        public static void main(String[] args) {

                String[] lines = {
                        "int x;"
                };

                ArrayList<Token> tokens = Lexer.tokenize(lines);
                System.out.println(tokens.toString());
        }
}
