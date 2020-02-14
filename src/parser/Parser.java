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

                        abstract Expression exec();
                }

                public static abstract class Led {

                        abstract Expression exec(Expression left);
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
                                Expression exec() {
                                        Parser parser = Parser.Instance();
                                        Expression expr = parser.Expression();
                                        parser.previous = parser.tokens.remove(0);
                                        return new Expression.Group(expr);
                                }
                        }, new ParseRule.Led() {
                                /*
                                 * TODO: Handle function calls
                                 */
                                Expression exec(Expression left) {
                                        return null;
                                }
                        }, Precedence.CALL));

                rules.put(TokenType.TK_MINUS, new ParseRule(
                        new ParseRule.Nud() {
                                Expression exec() {
                                        Parser parser = Parser.Instance();
                                        return parser.Unary();
                                }
                        }, new ParseRule.Led() {
                                /*
                                 * TODO: Handle function calls
                                 */
                                Expression exec(Expression left) {
                                        Parser parser = Parser.Instance();
                                        return parser.Binary(left);
                                }
                        }, Precedence.CALL));
        }

        public static Parser Instance() {

                if(Parser.instance == null) {

                        Parser.instance = new Parser();
                }

                return Parser.instance;
        }

        Expression.Number Number() {

                return null;
        }

        Expression.Unary Unary() {

                Token operator = this.previous;
                Expression expr = this.Expression();
                this.previous = this.tokens.remove(0);

                return new Expression.Unary(expr, operator);
        }

        Expression.Binary Binary(Expression left) {

                Token operator = this.previous;

                ParseRule rule = this.rules.get(operator.tokenType);
                int precedence = rule.precedence.ordinal() + 1;
                Expression expr = ParsePrecedence(Precedence.values()[precedence]);

                return new Expression.Binary(left, operator, expr);
        }

        Expression ParsePrecedence(Precedence prec) {

                Expression left = null;

                ParseRule rule = rules.get(previous.tokenType);
                if(rule.nud == null) {

                        /*
                         * If this happens, we were expecting
                         * to encounter an expression.
                         * Raise an exception / handle the error
                         */
                        return null;
                }

                /*
                 * I'm relatively certain that this loop is set up correctly
                 * as the implementation of compareTo subtracts the other
                 * operand from the calling object. We are looking for
                 * while(prec <= rules.get(...).precedence)
                 */
                while(prec.compareTo(rules.get(tokens.get(0).tokenType).precedence) <= 0) {

                        previous = tokens.remove(0);
                        left = rules.get(previous.tokenType).led.exec(left);
                }

                return left;
        }

        Expression Expression() {

                return ParsePrecedence(Precedence.ASSIGNMENT);
        }

        AStatement Statement() {
                return null;
        }

        ASTNode Declaration() {

                return null;
        }

        ASTNode Program() {

                return null;
        }

        public ASTNode Parse(ArrayList<Token> tokens) {

                tokens = tokens;

                return Program();
        }
}

