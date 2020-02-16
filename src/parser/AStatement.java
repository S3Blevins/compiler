package parser;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import lexer.Token;
import lexer.TokenType;

import parser.ASTNode;
import parser.Expression;

public abstract class AStatement extends ASTNode {
        // TODO: expression statement
        // compound/block statement
        // TODO: selection statement
        // iteration statement
        // return statement
        // break statement

        public static class Block extends AStatement {

                public Block() {
                        this.statements = new ArrayList<AStatement>();
                        this.declarations = new ArrayList<Declaration>();
                }

                public void addStatement(AStatement statement) {
                        this.statements.add(statement);
                }

                public void addDeclaration(Declaration declaration) {
                        this.declarations.add(declaration);
                }

                ArrayList<AStatement> statements;
                ArrayList<Declaration> declarations;

        }

        public static class Iteration extends AStatement {

                public Iteration(Expression condition, Block body) {
                        this.condition = condition;
                        this.body = body;
                }

                Expression condition;
                Block body;
        }

        public static class Selection extends AStatement {

                public Selection() {
                        this.ifElse = new LinkedHashMap<>();
                }

                // set of condition and body
                public LinkedHashMap<Expression, Block> ifElse;
        }

        public static class Return extends AStatement {

                // expression is set when
                public Return() {
                        this.token = new Token("return", TokenType.TK_KEYWORDS);
                        this.expr = null;
                }

                public void setExpression(Expression expr) {
                        this.expr = expr;
                }

                Token token;
                Expression expr;
        }

        public static class Break extends AStatement {

                public Break() {
                        this.token = new Token("break", TokenType.TK_KEYWORDS);
                }
                Token token;
        }

        public static class Statement {

                public Statement(Expression expr) {
                        this.expr = expr;
                }

                Expression expr;
        }

        //TODO: override print method specific to statement node

}

