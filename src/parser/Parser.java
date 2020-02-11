package parser;

import java.util.ArrayList;

import lexer.Token;
import lexer.TokenType;

import parser.Expression;

public class Parser {

        public static Token previous;
        public static Token current;
        public static ArrayList<Token> tokens;

        enum Precedence {
                NONE,
                ASSIGNMENT,
                OR,
                AND,
                EQUALITY,
                COMPARISON,
                TERM,
                FACTOR,
                UNARY,
                CALL,
                PRIMARY
        }

        /*
         * 09 Feb 2020
         *
         * I know I made this commit, but I've done some investigating
         * since then, so I wanted to leave a little comment so that
         * everyone else can weigh in their thoughts on how this
         * parse table should be done.
         *
         * It might be easier (if somewhat hackish) to make ParseInfix
         * and ParsePrefix into members of type Runnable.
         * Otherwise we will have to have a subclass for every
         * parse rule, and that could get pretty hairy.
         *
         * See this example here:
         * https://programming.guide/java/function-pointers-in-java.html
         *
         * -- Garrett
         */
        static class ParseRule {

                public abstract void ParseInfix();
                public abstract void ParsePrefix();
                public Precedence precedence;
        }
}

