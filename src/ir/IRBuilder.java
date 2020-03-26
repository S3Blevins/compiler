package ir;

import common.IVisitor;
import lexer.Token;
import parser.treeObjects.*;

/**
 * This class handles all of the routines and details of
 * 1) Creating an IR object (IRExpression.java).
 * 2) Populating that object.
 * 3) Returning all of the objects to update IRList object (IRList.java)
 *    with all of the crafted IRs.
 */
public class IRBuilder implements IVisitor<IRExpression> {
        /*
                        +
                       /  \
                      3    5

                      LOAD 3, _1
                      LOAD 5, _2
                      ADD, _1, _2, L0
         */
        @Override
        public IRExpression visitUnary(Expression.Unary unary) {
                return null;
        }

        @Override
        public IRExpression visitBinary(Expression.Binary binary) {
                System.out.println("Visit Binary");

                IRExpression left = binary.getLeftExpr().accept(this);
                IRExpression right = binary.getRightExpr().accept(this);

                IRList IRS = new IRList();
                Token IRVarName = new Token(IRS.getLabelName());

                IRExpression result = new IRExpression(Instruction.ADD, left.dest, right.dest, IRVarName);
                //result.source0 = binary.right.token;
                //result.source1 = binary.left.token;
                //result.label_name = left.label_name;
                //result.label_id = left.label_id + 1;

                return null;
        }

        @Override
        public IRExpression visitTernary(Expression.Ternary ternary) {
                return null;
        }

        @Override
        public IRExpression visitGroup(Expression.Group group) {
                return null;
        }

        @Override
        public IRExpression visitNumber(Expression.Number number) {

                System.out.println("Here");
                return null;
        }

        @Override
        public IRExpression visitIdentifier(Expression.Identifier identifier) {
                return null;
        }

        @Override
        public IRExpression visitBlock(Statement.Block block) {
                return null;
        }

        @Override
        public IRExpression visitReturn(Statement.Return statement) {
                return null;
        }

        @Override
        public IRExpression visitBreak(Statement.Break statement) {
                return null;
        }

        @Override
        public IRExpression visitIteration(Statement.Iteration statement) {
                return null;
        }

        @Override
        public IRExpression visitConditional(Statement.Conditional conditional) {
                return null;
        }

        @Override
        public IRExpression visitExpressionStatement(Statement.ExpressionStatement expr) {
                return null;
        }

        @Override
        public IRExpression visitGotoLabel(Statement.gotoLabel label) {
                return null;
        }

        @Override
        public IRExpression visitGoto(Statement.gotoStatement statement) {
                return null;
        }

        @Override
        public IRExpression visitVarDecl(Declaration.varDeclaration decl) {

                for (int i = 0; i < decl.children.size(); i++) {
                        decl.children.get(i).accept(this);
                }

                return null;
        }

        @Override
        public IRExpression visitFunDecl(Declaration.funDeclaration decl) {
                return null;
        }

        @Override
        public IRExpression visitVariable(Declaration.Variable decl) {

                IRExpression tmp = decl.children.get(0).accept(this);
                Token var = tmp.dest;

                return new IRExpression(Instruction.LOAD, var);
        }

        @Override
        public IRExpression visitParameter(Declaration.Parameter decl) {
                return null;
        }

        @Override
        public IRExpression visitTypeDecl(Declaration.TypeDeclaration decl) {
                return null;
        }

        @Override
        public IRExpression visitEnumVar(Declaration.TypeDeclaration.EnumVar decl) {
                return null;
        }

        @Override
        public IRExpression visitProgram(Program program) {
                System.out.println("Visit Program");

                for (int i = 0; i < program.children.size(); i++) {
                        program.children.get(i).accept(this);
                }

                //for (int i = 0; i < program)

                return null; // needs to return IR
        }
}
