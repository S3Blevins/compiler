package ir;

import common.IVisitor;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.treeObjects.*;

/**
 * This class handles all of the routines and details of
 * 1) Creating an IR object (IRExpression.java).
 * 2) Populating that object.
 * 3) Returning all of the objects to update IRList object (IRList.java)
 *    with all of the crafted IRs.
 */
public class IRBuilder implements IVisitor<Token> {
        // TODO: visitUnary
        // TODO: visitBinary
        // TODO: visitTernary
        // DONE: visitGroup
        // DONE: visitNumber
        // TODO: visitIdentifier
        // DONE: visitBlock
        // TODO: visitBreak
        // TODO: visitReturn
        // TODO: visitIterator
        // TODO: visitConditional
        // TODO: visitExpressionStatement
        // TODO: visitGoToLabel
        // TODO: visitGoTo
        // DONE: visitVarDec
        // DONE: visitFunDec
        // DONE: visitVar
        // DONE: visitParam
        // TODO: visitEnum
        // TODO: visitTypeDecl
        // DONE: visitProgram

        public IRList IRs;

        public IRBuilder() {
                IRs = new IRList();
        }

        @Override
        public Token visitUnary(Expression.Unary unary) {
                // similar to binary but less complex
                return null;
        }

        @Override
        public Token visitBinary(Expression.Binary binary) {

                // no need to return, just grab last dest location from the linear ir expression list
                // so create a new method to return that

                Token left = binary.getLeftExpr().accept(this);
                Token right = binary.getRightExpr().accept(this);
                Instruction binInstr = null;

                TokenType binOp = binary.op.tokenType;

                if(binOp == TokenType.TK_PLUS) {
                        binInstr = Instruction.ADD;
                } else if(binOp == TokenType.TK_MINUS){
                        binInstr = Instruction.SUB;
                } else if(binOp == TokenType.TK_SLASH) {
                        // fill in
                }

                /*
                result.source0 = binary.right.token;
                result.source1 = binary.left.token;
                result.label_name = left.label_name;
                result.label_id = left.label_id + 1;
                */

                IRs.addExpr(new IRExpression(Instruction.ADD, left, right, IRs.getLabelName()));
                return IRs.getLastLabel();
        }

        @Override
        public Token visitTernary(Expression.Ternary ternary) {
                // TBD
                return null;
        }

        @Override
        public Token visitGroup(Expression.Group group) {
                // behaves like other expressions

                iterator(group);

                return IRs.getLastLabel();
        }

        @Override
        public Token visitNumber(Expression.Number number) {
                return number.value;
        }

        @Override
        public Token visitIdentifier(Expression.Identifier identifier) {
                // will likely never be called?
                return null;
        }

        @Override
        public Token visitBlock(Statement.Block block) {
                // no expression to add, only iterate through children
                iterator(block);

                return null;
        }

        @Override
        public Token visitReturn(Statement.Return statement) {
                // create RET expression
                return null;
        }

        @Override
        public Token visitBreak(Statement.Break statement) {
                // create BREAK expression
                return null;
        }

        @Override
        public Token visitIteration(Statement.Iteration statement) {
                // break this down into a jump with an iteration statement

                // variable declaration

                // body -> iterator(this)

                // inc/dec/etc
                // jmp[cond]

                return null;
        }

        @Override
        public Token visitConditional(Statement.Conditional conditional) {

                return null;
        }

        @Override
        public Token visitExpressionStatement(Statement.ExpressionStatement expr) {
                // visit children and break down expression

                return null;
        }

        @Override
        public Token visitGotoLabel(Statement.gotoLabel label) {
                // just add a label, no children to iterate through

                return null;
        }

        @Override
        public Token visitGoto(Statement.gotoStatement statement) {
                // jump to a label, no children to iterate through

                return null;
        }

        @Override
        public Token visitVarDecl(Declaration.varDeclaration decl) {
                // no declaration to add, just iterate through children

                iterator(decl);

                return null;
        }

        @Override
        public Token visitFunDecl(Declaration.funDeclaration decl) {
                // create a label for each function, iterate through children
                IRs.addExpr(new IRExpression(Instruction.LABEL, decl.functionID));

                iterator(decl);

                return null;
        }

        @Override
        public Token visitVariable(Declaration.Variable decl) {
                IRs.addExpr(new IRExpression(Instruction.LOAD, decl.children.get(0).accept(this), decl.variableID));

                return null;
        }

        @Override
        public Token visitParameter(Declaration.Parameter decl) {
                // load each parameter, no children to iterate through
                IRs.addExpr(new IRExpression(Instruction.LOAD, decl.paramID));

                return null;
        }

        @Override
        public Token visitTypeDecl(Declaration.TypeDeclaration decl) {
                // TBD

                iterator(decl);

                return null;
        }

        @Override
        public Token visitProgram(Program program) {
                // new label with start? change this to whatever works
                IRs.addExpr(new IRExpression("START:"));

                iterator(program);

                return null; // needs to return IR
        }

        public void iterator(Node node) {
                if(node.hasChildren()) {
                        for (int i = 0; i < node.children.size(); i++) {
                                node.children.get(i).accept(this);
                        }
                }
        }
}
