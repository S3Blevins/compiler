package parser;

import java.util.ArrayList;

import lexer.Token;
import lexer.TokenType;

import parser.ASTNode;
import parser.Expression;

public abstract class AStatement extends ASTNode {

        public static class Block extends AStatement {

                public Block(ArrayList<AStatement> statements) {

                        this.statements = statements;
                }

                ArrayList<AStatement> statements;

        }

        public static class While extends AStatement {

                public While(Expression condition, Block body) {

                        this.condition = condition;
                        this.body = body;
                }

                Expression condition;
                Block body;
        }

        public static class Statement {

                public Statement(Expression expr) {

                        this.expr = expr;
                }

                Expression expr;
        }
}

