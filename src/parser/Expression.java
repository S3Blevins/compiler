package parser;

import lexer.Token;
import lexer.TokenType;

public abstract class Expression {

        static class Binary extends Expression {

                public Binary(Expression left, Token op, Expression right) {

                        this.left = left;
                        this.op = op;
                        this.right = right;
                }

                final Token op;
                final Expression left;
                final Expression right;
        }

        static class Unary extends Expression {

                public Unary(Expression expr, Token op) {

                        this.op = op;
                        this.expr = expr;
                }

                final Token op;
                final Expression expr;
        }

        static class Group extends Expression {

                public Group(Expression expr, Token op) {

                        this.op = op;
                        this.expr = expr;
                }

                final Token op;
                final Expression expr;
        }

}
