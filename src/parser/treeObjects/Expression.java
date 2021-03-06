package parser.treeObjects;

import lexer.Token;
import lexer.TokenType;
import common.IVisitor;
import parser.Node;

import static java.lang.System.exit;

/**
 * <h1>Expression</h1>
 * This class represents the expressions that can be constructed.
 * This also adds them to the nodal class to be added as children to other
 * nodes and eventually construct the parse tree/IR.
 *
 * @author Sterling Blevins, Damon Estrada, Garrett Bates, Jacob Santillanes
 * @version 1.0
 * @since 2020-03-28
 */
public abstract class Expression extends Node {

    public static class Ternary extends Expression {

        public Ternary(Expression left, Expression onTrue, Expression onFalse) {

            this.addChild(left);
            this.addChild(onTrue);
            this.addChild(onFalse);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitTernary(this);
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
            if (!this.hasChildren()) {
                System.err.println("Left expression does not exist");
                return null;
            }

            return this.children.get(0);
        }

        public Node getRightExpr() {
            if (!this.hasChildren() || this.childSize() != 2) {
                System.err.println("Right expression does not exist");
                return null;
            }

            return this.children.get(1);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitBinary(this);
        }
    }

    public static class Unary extends Expression {

        public Token op;

        public Unary(Expression expr, Token op) {
            this.op = op;
            this.addChild(expr);
        }

        public Node getExpr() {
            if (!this.hasChildren() || this.childSize() != 1) {
                System.err.println("Expression does not exist");
                return null;
            }

            return this.children.get(0);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitUnary(this);
        }
    }

    public static class Group extends Expression {

        public Group(Expression expr) {
            this.addChild(expr);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitGroup(this);
        }
    }

    public static class Number extends Expression {

        public Token value;

        public Number(Integer value) {
            this.value = new Token(value.toString(), TokenType.TK_NUMBER);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitNumber(this);
        }
    }

    public static class Boolean extends Expression {
        public Token bool;

        public Boolean(String value) {
            this.bool = new Token(value, TokenType.TK_BOOL);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitBoolean(this);
        }
    }


    public static class Identifier extends Expression {

        public Token value;

        public Identifier(String value) {
            this.value = new Token(value, TokenType.TK_IDENTIFIER);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitIdentifier(this);
        }
    }

    public static class funCall extends Expression {
        public Token functionName;

        public funCall(String functionName) {
            this.functionName = new Token(functionName, TokenType.TK_IDENTIFIER);
        }

        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitfunCall(this);
        }
    }

    public void isIdentifier() {
        if(!(this instanceof Expression.Identifier)) {
            System.err.println("ERROR: Assignment to constant or number is invalid");
            exit(1);
        }
    }
}
