package ir;

import common.IVisitor;
import parser.treeObjects.*;

public class IRBuilder implements IVisitor<IRExpression> {

        @Override
        public IRExpression visitUnary(Expression.Unary unary) {
                return null;
        }

        @Override
        public IRExpression visitBinary(Expression.Binary binary) {
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
                return null;
        }

        @Override
        public IRExpression visitFunDecl(Declaration.funDeclaration decl) {
                return null;
        }

        @Override
        public IRExpression visitVariable(Declaration.Variable decl) {
                return null;
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
        public IRExpression visitParamList(treeList.ParameterList list) {
                return null;
        }

        @Override
        public IRExpression visitProgram(Program program) {
                return null;
        }
}
