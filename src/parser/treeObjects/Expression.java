package parser.treeObjects;

import lexer.Token;
import lexer.TokenType;
import parser.IVisitor;
import parser.Node;

public abstract class Expression extends Node {

        public static class Ternary extends Expression {

                public Ternary(Expression left, Expression onTrue, Expression onFalse) {

                        this.addChild(left);
                        this.addChild(onTrue);
                        this.addChild(onFalse);
                }

                void accept(IVisitor visitor) {
                        visitor.visitTernary(this);
                }
        }

        public static class Binary extends Expression {

                public Token op;

                public Binary(Expression left, Token op, Expression right) {
                        this.addChild(left);
                        this.op = op;
                        this.addChild(right);
                }

                public Node getLeftExpr() {
                        if(!this.hasChildren()) {
                                System.err.println("Left expression does not exist");
                                return null;
                        }

                        return this.children.get(0);
                }

                public Node getRightExpr() {
                        if(!this.hasChildren() || this.childSize() != 2) {
                                System.err.println("Right expression does not exist");
                                return null;
                        }

                        return this.children.get(1);
                }
                public void accept(IVisitor visitor) {
                        visitor.visitBinary(this);
                }
        }

        public static class Unary extends Expression {

                public Token op;

                public Unary(Expression expr, Token op) {
                        this.op = op;
                        this.addChild(expr);
                }

                public Node getExpr() {
                        if(!this.hasChildren() || this.childSize() != 1) {
                                System.err.println("Expression does not exist");
                                return null;
                        }

                        return this.children.get(0);
                }
                public void accept(IVisitor visitor) {
                        visitor.visitUnary(this);
                }
        }

        public static class Group extends Expression {

                public Group(Expression expr) {
                        this.addChild(expr);
                }
                public void accept(IVisitor visitor) {
                        visitor.visitGroup(this);
                }
        }

        public static class Number extends Expression {

                public Token value;

                public Number(Integer value) {
                        this.value = new Token(value.toString(), TokenType.TK_NUMBER);
                }
                public void accept(IVisitor visitor) {
                        visitor.visitNumber(this);
                }
        }


        public static class Identifier extends Expression {

                public Token value;

                public Identifier(String value) {
                        this.value = new Token(value, TokenType.TK_IDENTIFIER);
                }
                public void accept(IVisitor visitor) {
                        visitor.visitIdentifier(this);
                }
        }
}
