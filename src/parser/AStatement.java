package parser;

import java.util.ArrayList;

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


        public static class Block extends AStatement {

                public Block(ArrayList<AStatement> statements) {
                        this.statements = statements;
                }

                ArrayList<AStatement> statements;
        }

        public static class Iteration extends AStatement {

                public Iteration(Expression condition, Block body) {
                        this.condition = condition;
                        this.body = body;
                }

                Expression condition;
                Block body;
        }

        public static class Return extends AStatement {

                // NOTE: expr may be null
                public Return(Token returnString, Expression expr) {
                        this.returnString = returnString;
                        this.expr = expr;
                }

                Token returnString;
                Expression expr;
        }

        public static class Statement {

                public Statement(Expression expr) {
                        this.expr = expr;
                }

                Expression expr;
        }

        //TODO: override print method specific to statement node
        public void printNode(){

        }

}

