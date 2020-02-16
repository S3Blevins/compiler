package parser;

import lexer.Token;
import lexer.TokenType;

public abstract class Expression extends ASTNode {

        public static class Binary extends Expression {

                public Binary(Expression left, Token op, Expression right) {
                        this.left = left;
                        this.op = op;
                        this.right = right;
                }

                final Token op;
                final Expression left;
                final Expression right;
        }

        public static class Unary extends Expression {

                public Unary(Expression expr, Token op) {
                        this.op = op;
                        this.expr = expr;
                }

                final Token op;
                final Expression expr;
        }

        public static class Group extends Expression {

                public Group(Expression expr) {
                        this.expr = expr;
                }

                final Expression expr;
        }

        public static class Number extends Expression {

                public Number(Integer value) {
                        this.value = new Token(value.toString(), TokenType.TK_NUMBER);
                }

                Token value;
        }

        //TODO: override print method specific to expression node


}
