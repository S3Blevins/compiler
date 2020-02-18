package parser.treeObjects;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lexer.Token;
import lexer.TokenType;
import parser.Node;

public abstract class Statement extends Node {
        // TODO: expression statement
        // compound/block statement
        // TODO: selection statement
        // iteration statement
        // return statement
        // break statement

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
                        this.condition = condition;
                        this.body = body;
                }

                Expression condition;
                Block body;
        }

        public static class Selection extends Statement {

                public Selection() {
                        this.ifElse = new LinkedHashMap<>();
                }

                public LinkedHashMap<Expression, Block> ifElse;
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

                public Break() {
                        this.token = new Token("break", TokenType.TK_KEYWORDS);
                }
                Token token;
        }



}

