package parser.treeObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lexer.Token;
import lexer.TokenType;
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
                        this.addChild(statement);
                }

                public void addDeclaration(Declaration declaration) {
                        this.addChild(declaration);
                }

        }

        public static class Iteration extends Statement {

                public Iteration(Expression condition, Block body) {
                        this.addChild(condition);
                        this.addChild(body);
                }
        }

        public static class Selection extends Statement {
                public Selection() {

                }

                public Selection(Expression condition, Block body) {
                        this.addChild(condition);
                        this.addChild(body);
                }

                public void addSelection(Expression condition, Block body) {
                        this.addChild(condition);
                        this.addChild(body);
                }

                public void addSelection(Statement statement) {
                        this.addChild(statement.children.get(0));
                        this.addChild(statement.children.get(1));
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
        }

        public static class Break extends Statement {

                public Token token;

                public Break() {
                        this.token = new Token("break", TokenType.TK_KEYWORDS);
                }
        }

        public static class ExpressionStatement extends Statement {

                public Token token;

                public ExpressionStatement() {
                        this.token = new Token("statement", TokenType.TK_KEYWORDS);
                }

                public ExpressionStatement(Expression expr) {
                        this.token = new Token("statement", TokenType.TK_KEYWORDS);
                        this.addChild(expr);
                }

                public void setExpression(Expression expr) {
                        this.addChild(expr);
                }
        }
}

