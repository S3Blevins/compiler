package parser;

import parser.treeObjects.*;

import java.util.ArrayList;

public class NodePrinter implements IVisitor<String> {

    private ArrayList<Boolean> depth;

    public NodePrinter() {

        depth = new ArrayList<>();
    }

    @Override
    public String visitUnary(Expression.Unary unary) {
        return null;
    }

    @Override
    public String visitBinary(Expression.Binary binary) {
        return null;
    }

    @Override
    public String visitTernary(Expression.Ternary ternary) {
        return null;
    }

    @Override
    public String visitGroup(Expression.Group group) {
        return null;
    }

    @Override
    public String visitNumber(Expression.Number number) {
        return null;
    }

    @Override
    public String visitIdentifier(Expression.Identifier identifier) {
        return null;
    }

    @Override
    public String visitBlock(Statement.Block block) {
        return null;
    }

    @Override
    public String visitReturn(Statement.Return statement) {
        return null;
    }

    @Override
    public String visitBreak(Statement.Break statement) {
        return null;
    }

    @Override
    public String visitIteration(Statement.Iteration statement) {
        return null;
    }

    @Override
    public String visitConditional(Statement.Conditional conditional) {
        return null;
    }

    @Override
    public String visitExpressionStatement(Statement.ExpressionStatement expr) {
        return null;
    }

    @Override
    public String visitGotoLabel(Statement.gotoLabel label) {
        return null;
    }

    @Override
    public String visitGoto(Statement.gotoStatement statement) {
        return null;
    }

    @Override
    public String visitVarDecl(Declaration.varDeclaration decl) {
        return null;
    }

    @Override
    public String visitFunDecl(Declaration.funDeclaration decl) {
        return null;
    }

    @Override
    public String visitVariable(Declaration.Variable decl) {
        return null;
    }

    @Override
    public String visitParameter(Declaration.Parameter decl) {
        return null;
    }

    @Override
    public String visitTypeDecl(Declaration.TypeDeclaration decl) {
        return null;
    }

    @Override
    public String visitEnumVar(Declaration.TypeDeclaration.EnumVar decl) {
        return null;
    }

    @Override
    public String visitParamList(treeList.ParameterList list) {
        return null;
    }

    @Override
    public String visitProgram(Program program) {

        System.out.println("PROGRAM");

        this.depth.add(true);

        for (int i = 0; i < program.children.size(); i++) {

            program.children.get(i).accept(this);
        }

        this.depth.remove(this.depth.size() - 1);

        return null;
    }

    private String printDepth() {

        for (Boolean printLine : this.depth) {

            if (printLine) {

                System.out.println("|   ");

            } else {

                System.out.println("    ");
            }
        }

        return null;
    }
}
