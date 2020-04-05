package common;

import parser.treeObjects.*;

public interface IVisitor<T> {

    T visitUnary(Expression.Unary unary);

    T visitBinary(Expression.Binary binary);

    T visitTernary(Expression.Ternary ternary);

    T visitGroup(Expression.Group group);

    T visitNumber(Expression.Number number);

    T visitIdentifier(Expression.Identifier identifier);

    T visitBlock(Statement.Block block);

    T visitReturn(Statement.Return statement);

    T visitBreak(Statement.Break statement);

    T visitIteration(Statement.Iteration statement);

    T visitConditional(Statement.Conditional conditional);

    T visitExpressionStatement(Statement.ExpressionStatement expr);

    T visitGotoLabel(Statement.gotoLabel label);

    T visitGoto(Statement.gotoStatement statement);

    T visitVarDecl(Declaration.varDeclaration decl);

    T visitFunDecl(Declaration.funDeclaration decl);

    T visitVariable(Declaration.Variable decl);

    T visitParameter(Declaration.Parameter decl);

    T visitTypeDecl(Declaration.TypeDeclaration decl);

    T visitProgram(Program program);

    T visitBoolean(Expression.Boolean bool);
}
