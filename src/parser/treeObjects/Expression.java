package parser.treeObjects;

import lexer.Token;
import lexer.TokenType;
import parser.Node;

public abstract class Expression extends Node {

        public static class Binary extends Expression {

                Token op;
                Expression left;
                Expression right;

                public Binary(Expression left, Token op, Expression right) {
                        this.left = left;
                        this.op = op;
                        this.right = right;
                }
/*
                public void printNode(int depth) {
                        System.out.println("<" + this.op.str + ">");
                        left.printNode(depth + 1);
                        right.printNode(depth + 1);
                }*/
        }

        public static class Unary extends Expression {

                Token op;
                Expression expr;

                public Unary(Expression expr, Token op) {
                        this.op = op;
                        this.expr = expr;
                }
/*
                public void printNode(int depth) {
                        System.out.println("<" + this.op.str + ">");
                        this.expr.printNode(depth + 1);
                }*/
        }

        public static class Group extends Expression {

                Expression expr;

                public Group(Expression expr) {
                        this.expr = expr;
                }
/*
                public void printNode(int depth) {
                        this.expr.printNode(depth + 1);
                }
 */       }

        public static class Number extends Expression {

                public Token value;

                public Number(Integer value) {
                        this.value = new Token(value.toString(), TokenType.TK_NUMBER);
                }

        }



}
