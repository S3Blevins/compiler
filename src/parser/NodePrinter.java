package parser;

import parser.treeObjects.*;

import java.util.ArrayList;

public class NodePrinter implements IVisitor {

    private ArrayList<Boolean> depth;

    public NodePrinter() {

        depth = new ArrayList<>();
    }

    @Override
    public void visitUnary(Expression.Unary unary) {

    }

    @Override
    public void visitBinary(Expression.Binary binary) {

    }

    @Override
    public void visitTernary(Expression.Ternary ternary) {

    }

    @Override
    public void visitGroup(Expression.Group group) {

    }

    @Override
    public void visitNumber(Expression.Number number) {

    }

    @Override
    public void visitIdentifier(Expression.Identifier identifier) {

    }

    @Override
    public void visitBlock(Statement.Block block) {

    }

    @Override
    public void visitReturn(Statement.Return statement) {

    }

    @Override
    public void visitBreak(Statement.Break statement) {

    }

    @Override
    public void visitIteration(Statement.Iteration statement) {

    }

    @Override
    public void visitConditional(Statement.Conditional conditional) {

    }

    @Override
    public void visitExpressionStatement(Statement.ExpressionStatement expr) {

    }

    @Override
    public void visitGotoLabel(Statement.gotoLabel label) {

    }

    @Override
    public void visitGoto(Statement.gotoStatement statement) {

    }

    @Override
    public void visitVarDecl(Declaration.varDeclaration decl) {

    }

    @Override
    public void visitFunDecl(Declaration.funDeclaration decl) {

    }

    @Override
    public void visitVariable(Declaration.Variable decl) {

    }

    @Override
    public void visitParameter(Declaration.Parameter decl) {

    }

    @Override
    public void visitTypeDecl(Declaration.TypeDeclaration decl) {

    }

    @Override
    public void visitEnumVar(Declaration.TypeDeclaration.EnumVar decl) {

    }

    @Override
    public void visitParamList(treeList.ParameterList list) {

    }

    @Override
    public void visitProgram(Program program) {

        System.out.println("PROGRAM");

        this.depth.add(true);

        for(int i = 0; i < program.children.size(); i++) {

            program.children.get(i).accept(this);
        }

        this.depth.remove(this.depth.size() - 1);
    }

    private void printDepth() {

        for(Boolean printLine : this.depth) {

            if(printLine) {

                System.out.println("|   ");

            } else {

                System.out.println("    ");
            }
        }
    }
}
