package parser;

import common.IVisitor;
import lexer.Token;
import parser.treeObjects.*;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class NodePrinter implements IVisitor {

    private ArrayList<Boolean> depth;
    private StringBuilder tree;

    public NodePrinter() {
        depth = new ArrayList<>();
        tree = new StringBuilder();
    }

    public String getTree() {
        return tree.toString();
    }

    @Override
    public Void visitUnary(Expression.Unary unary) {
        tree.append("Expression[Unary] <" + unary.op.str + ">\n");

        iterator(unary);

        return null;
    }

    @Override
    public Void visitBinary(Expression.Binary binary) {
        tree.append("Expression[Binary] <" + binary.op.str + ">\n");

        iterator(binary);

        return null;
    }

    @Override
    public Void visitTernary(Expression.Ternary ternary) {
        tree.append("Expression[Ternary]\n");

        iterator(ternary);

        return null;
    }

    @Override
    public Void visitGroup(Expression.Group group) {
        tree.append("Expression[Group]\n");

        iterator(group);

        return null;
    }

    @Override
    public Void visitNumber(Expression.Number number) {
        tree.append("Expression[Number] <" + number.value.str + ">\n");

        iterator(number);

        return null;
    }

    @Override
    public Void visitIdentifier(Expression.Identifier identifier) {
        tree.append("Expression[Identifier] <" + identifier.value.str + ">\n");

        iterator(identifier);

        return null;
    }

    @Override
    public Void visitBlock(Statement.Block block) {
        tree.append("Statement[Block]\n");

        iterator(block);

        return null;
    }

    @Override
    public Void visitReturn(Statement.Return statement) {
        tree.append("Statement[Return]\n");

        iterator(statement);

        return null;
    }

    @Override
    public Void visitBreak(Statement.Break statement) {
        tree.append("Statement[Break]\n");

        iterator(statement);

        return null;
    }

    @Override
    public Void visitIteration(Statement.Iteration statement) {
        tree.append("Statement[for-loop]\n");

        iterator(statement);

        return null;
    }

    @Override
    public Void visitConditional(Statement.Conditional conditional) {
        tree.append("Statement[Conditional]\n");

        iterator(conditional);

        return null;
    }

    @Override
    public Void visitExpressionStatement(Statement.ExpressionStatement expr) {
        tree.append("Statement[ExpressionStatement]\n");

        iterator(expr);

        return null;
    }

    @Override
    public Void visitGotoLabel(Statement.gotoLabel label) {
        tree.append("Statement[gotoLabel]\n");

        iterator(label);

        return null;
    }

    @Override
    public Void visitGoto(Statement.gotoStatement statement) {
        tree.append("Statement[gotoStatement] <" + statement.label.str + ">\n");

        iterator(statement);

        return null;
    }

    @Override
    public Void visitVarDecl(Declaration.varDeclaration decl) {
        tree.append("Declaration[varDeclaration]\n");

        iterator(decl);

        return null;
    }

    @Override
    public Void visitFunDecl(Declaration.funDeclaration decl) {
        tree.append("Declaration[funcDeclaration] <" + decl.typeSpecifier.str + " " + decl.functionID.str + ">\n");

        iterator(decl);

        return null;
    }

    @Override
    public Void visitVariable(Declaration.Variable decl) {
        tree.append("Declaration[Variable] <" + decl.typeSpecifier.str + " " + decl.variableID.str + ">\n");

        iterator(decl);

        return null;
    }

    @Override
    public Void visitParameter(Declaration.Parameter decl) {
        tree.append("Declaration[Parameter] <" + decl.type.str + " " + decl.paramID.str + ">\n");

        iterator(decl);

        return null;
    }

    @Override
    public Void visitTypeDecl(Declaration.TypeDeclaration decl) {
        tree.append("Declaration[TypeDeclaration] <" + decl.enumType.str + " " + decl.enumID.str + ">\n");

        iterator(decl);

        return null;
    }

    @Override
    public Void visitProgram(Program program) {
        tree.append("Program <" + program.progName + ">\n");

        iterator(program);

        return null;
    }

    @Override
    public Object visitBoolean(Expression.Boolean bool) {
        tree.append("Expression[Boolean] <" + bool.bool.str + ">\n");

        iterator(bool);

        return null;
    }

    private void printDepth() {
        for (Boolean printLine : this.depth) {
            if (printLine) {
                tree.append("|   ");
            } else {
                tree.append("    ");
            }
        }
    }

    public void iterator(Node node) {

        printDepth();

        for (int i = 0; i < node.childSize(); i++) {

            if (i == node.childSize() - 1) {
                tree.append("`-- ");
                if (node.children.get(i).hasChildren()) {
                    depth.add(false);
                } else {
                    // while the last flag of the array is false, remove boolean flags.
                    while (depth.size() != 0 && !depth.get(depth.size() - 1)) {
                        depth.remove(depth.size() - 1);
                    }

                    // remove a true element to realign node in tree
                    if (depth.size() != 0) {
                        depth.remove(depth.size() - 1);
                    }
                }
            } else {
                tree.append("|-- ");
                if (node.children.get(i).hasChildren()) {
                    depth.add(true);
                }
            }

            node.children.get(i).accept(this);
        }
    }
}
