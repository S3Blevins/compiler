package parser;

import parser.treeObjects.*;

public interface IVisitor {

    void visitUnary(Expression.Unary unary);
    void visitBinary(Expression.Binary binary);
    void visitTernary(Expression.Ternary ternary);
    void visitGroup(Expression.Group group);
    void visitNumber(Expression.Number number);
    void visitIdentifier(Expression.Identifier identifier);

    void visitBlock(Statement.Block block);
    void visitReturn(Statement.Return statement);
    void visitBreak(Statement.Break statement);
    void visitIteration(Statement.Iteration statement);
    void visitConditional(Statement.Conditional conditional);
    void visitExpressionStatement(Statement.ExpressionStatement expr);
    void visitGotoLabel(Statement.gotoLabel label);
    void visitGoto(Statement.gotoStatement statement);

    void visitVarDecl(Declaration.varDeclaration decl);
    void visitFunDecl(Declaration.funDeclaration decl);
    void visitVariable(Declaration.Variable decl);
    void visitParameter(Declaration.Parameter decl);
    void visitTypeDecl(Declaration.TypeDeclaration decl);
    void visitEnumVar(Declaration.TypeDeclaration.EnumVar decl);

    void visitParamList(treeList.ParameterList list);

    void visitProgram(Program progam);
}
