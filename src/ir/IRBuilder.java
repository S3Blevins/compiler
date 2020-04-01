package ir;

import common.IVisitor;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.treeObjects.*;

import static java.lang.System.exit;

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
        // DONE: visitIdentifier
        // DONE: visitBlock
        // DONE: visitBreak
        // DONE: visitReturn
        // TODO: visitIterator
        // TODO: visitConditional
        // TODO: visitExpressionStatement
        // DONE: visitGoToLabel
        // DONE: visitGoTo
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

                Token expr = unary.getExpr().accept(this);
                Instruction unInstr = unary.op.tokenType.getInstruction();

                // TODO: figure out if star is being used in a "unary capacity"

                IRs.addExpr(new IRExpression(unInstr, expr));

                return IRs.getLastLabel();
        }

        @Override
        public Token visitBinary(Expression.Binary binary) {
                // binary expression
                Token left = binary.getLeftExpr().accept(this);
                Token right = binary.getRightExpr().accept(this);
                Instruction binInstr = binary.op.tokenType.getInstruction();

                TokenType binOp = binary.op.tokenType;
                Token dest;

                // if no destination, it is placed into right side's expression
                if(binOp == TokenType.TK_STAREQ || binOp == TokenType.TK_MINUSEQ || binOp == TokenType.TK_PLUSEQ ||
                        binOp == TokenType.TK_SLASHEQ || binOp == TokenType.TK_EQUALS) {
                        dest = null;
                } else if(binOp == TokenType.TK_LESSEQ || binOp == TokenType.TK_LESS || binOp == TokenType.TK_GREATER ||
                        binOp == TokenType.TK_GREATEREQ || binOp == TokenType.TK_EQEQUAL) {
                        dest = IRs.getLastBlockLabel();
                } else {
                        dest = IRs.getLabelName();
                }

                IRs.addExpr(new IRExpression(binInstr, left, right, dest));

                return dest;
        }

        @Override
        public Token visitTernary(Expression.Ternary ternary) {
                // ternary is a container for other expression
                iterator(ternary);

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
                // just returns value token

                return number.value;
        }

        @Override
        public Token visitIdentifier(Expression.Identifier identifier) {
                // just returns identifier token
                return identifier.value;
        }

        @Override
        public Token visitBlock(Statement.Block block) {
                // no expression to add, only iterate through children
                //IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getCondName()));
                iterator(block);

                return null;
        }

        @Override
        public Token visitReturn(Statement.Return statement) {
                // create RET expression
                Token retVal = statement.children.get(0).accept(this);

                IRs.addExpr(new IRExpression(Instruction.RET, retVal));

                return null;
        }

        @Override
        public Token visitBreak(Statement.Break statement) {
                // create BREAK expression
                IRs.addExpr(new IRExpression(Instruction.BREAK));

                return null;
        }

        @Override
        public Token visitIteration(Statement.Iteration statement) {
                // insert a label and iterate through children

                IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getIteratorName()));
                iterator(statement);

                return null;
        }

        @Override
        public Token visitConditional(Statement.Conditional conditional) {
                // conditional contains a jump condition and a block

                for (int i = 0; i < conditional.children.size(); i++) {

                        if(i % 2 == 0 || i == conditional.children.size() - 1) {
                                IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getCondName()));
                        }

                        conditional.children.get(i).accept(this);
                }


                /*
                (LABEL, cond0)
                (RET, 0)
                (LOAD, mid)
                (RET, 1)


                (EVAL 1, cond0)



                 */

                return null;
        }

        @Override
        public Token visitExpressionStatement(Statement.ExpressionStatement expr) {
                // visit children and break down expression
                iterator(expr);

                return null;
        }

        @Override
        public Token visitGotoLabel(Statement.gotoLabel label) {
                // just add a label, no children to iterate through
                IRs.addExpr(new IRExpression(Instruction.LABEL, label.label));

                return null;
        }

        @Override
        public Token visitGoto(Statement.gotoStatement statement) {
                // jump to a label, no children to iterate through
                IRs.addExpr(new IRExpression(Instruction.JMP, statement.label));

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
                if(decl.hasChildren()) {
                        IRs.addExpr(new IRExpression(Instruction.LOAD, decl.children.get(0).accept(this), decl.variableID));
                } else {
                        IRs.addExpr(new IRExpression(Instruction.LOAD, decl.variableID));
                }

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

                iterator(program);

                return null; // needs to return IR
        }

        @Override
        public Token visitBoolean(Expression.Boolean bool) {
                return null;
        }

        public void iterator(Node node) {
                if(node.hasChildren()) {
                        for (int i = 0; i < node.children.size(); i++) {
                                node.children.get(i).accept(this);
                        }
                }
        }
}
