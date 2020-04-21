package parser.treeObjects;

import lexer.Token;
import lexer.TokenType;
import common.IVisitor;
import parser.Node;

public abstract class Statement extends Node {

    public static class Block extends Statement {

        public Block() {

        }

        public Block(Statement statement, Declaration declaration) {
            this.addChild(statement);
            this.addChild(declaration);
        }

        public void addStatement(Statement statement) {
            if (statement != null) {
                this.addChild(statement);
            }
        }

        public void addDeclaration(Declaration declaration) {
            if (declaration != null) {
                this.addChild(declaration);
            }
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitBlock(this);
        }

    }

    public static class Iteration extends Statement {

        public Token statementType;

        public Iteration(Expression condition, Block body, String type) {

            // if the condition is null, default it to equivalent of true
            if (condition == null) {
                condition = new Expression.Number(1);

            }

            this.addChild(condition);
            this.addChild(body);
            this.statementType = new Token(type, TokenType.TK_KEYWORDS);
        }

        public Iteration(Declaration dec, Expression decExpr, Expression condition, Block body, String type) {

            // if the condition is null, default it to equivalent of true
            if (condition == null) {
                condition = new Expression.Number(1);

            }

            this.addChild(dec);
            this.addChild(decExpr);
            this.addChild(condition);
            this.addChild(body);
            this.statementType = new Token(type, TokenType.TK_KEYWORDS);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitIteration(this);
        }
    }

    public static class Conditional extends Statement {
        public Conditional() {

        }

        public Conditional(Expression condition, Block body) {
            this.addChild(condition);
            this.addChild(body);
        }

        public void addConditional(Expression condition, Block body) {
            this.addChild(condition);
            this.addChild(body);
        }

        public void addConditional(Statement statement) {
            this.addChild(statement.children.get(0));
            this.addChild(statement.children.get(1));
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitConditional(this);
        }
    }

    public static class Return extends Statement {

        public Token token;

        public Return() {
            this.token = new Token("return", TokenType.TK_KEYWORDS);
        }

        public Return(Expression expr) {
            this.token = new Token("return", TokenType.TK_KEYWORDS);
            this.addChild(expr);
        }

        public void setExpression(Expression expr) {
            this.addChild(expr);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitReturn(this);
        }
    }

    public static class Break extends Statement {

        public Token token;

        public Break() {
            this.token = new Token("break", TokenType.TK_KEYWORDS);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitBreak(this);
        }
    }

    public static class ExpressionStatement extends Statement {

        public Token token;

        public ExpressionStatement() {
            this.token = new Token("Expression Statement", TokenType.TK_KEYWORDS);
        }

        public ExpressionStatement(Expression expr) {
            this.token = new Token("Expression Statement", TokenType.TK_KEYWORDS);
            this.addChild(expr);
        }

        public void setExpression(Expression expr) {
            this.addChild(expr);
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitExpressionStatement(this);
        }
    }

    public static class gotoStatement extends Statement {
        public Token label;

        public gotoStatement(Token label) {
            this.label = label;
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitGoto(this);
        }
    }

    public static class gotoLabel extends Statement {
        public Token label;

        public gotoLabel(Token label) {
            this.label = label;
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitGotoLabel(this);
        }
    }
}

