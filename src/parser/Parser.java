package parser;

import java.util.ArrayList;
import java.util.HashMap;

import lexer.Token;
import lexer.TokenType;

import parser.ASTNode;
import parser.Expression;

public class Parser {

        private Token previous;
        private ArrayList<Token> tokens;
        private HashMap<TokenType, ParseRule> rules;

        /*
         * 14 Feb 2020
         *
         * Turned Parser into a singleton.
         * I may go in and do the same refactoring
         * to the Lexer because it will make things
         * a little more manageable.
         * By turning parser into a singleton we
         * won't have to pass the Parser instance
         * to the ParseRule delegates. Instead
         * we can just access Parser.Instance() from
         * within the current delegate.
         * P nifty, I would say.
         *
         * -- Garrett
         */
        private static Parser instance = null;

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
         * I think this will work better than having to have
         * a subclass of ParseRule for every token. Instead
         * we have delegates objects to handle it for us.
         * Maybe not beautiful, but maybe more manageable?
         */
        static class ParseRule {

                public static abstract class Nud {

                        abstract ASTNode exec();
                }

                public static abstract class Led {

                        abstract ASTNode exec(ASTNode left);
                }

                public ParseRule(Nud nud, Led led, Precedence precedence) {

                        this.nud = nud;
                        this.led = led;
                        this.precedence = precedence;
                }

                public Nud nud; // Delegate for tokens with no left context
                public Led led; // Delegate for tokens with a left context
                public Precedence precedence;
        }

        private Parser() {

                rules = new HashMap<TokenType, ParseRule>();
                rules.put(TokenType.TK_LPAREN, new ParseRule(
                        new ParseRule.Nud() {
                                ASTNode exec() {
                                        return null;
                                }
                        }, new ParseRule.Led() {
                                ASTNode exec(ASTNode left) {
                                        return null;
                                }
                        }, Precedence.CALL));
        }

        public static Parser Instance() {

                if(Parser.instance == null) {

                        Parser.instance = new Parser();
                }

                return Parser.instance;
        }

        ASTNode program() {

                return null;
        }

        public ASTNode Parse(ArrayList<Token> tokens) {

                tokens = tokens;

                return program();
        }
}

