package parser;

import common.IVisitor;
import lexer.Token;
import parser.treeObjects.*;

import java.lang.reflect.Field;
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
        /*
             -
           /   \
          +     6
         / \
        3  5

        NOP 3
        NOP 5

        ADD 3 5 L1
        --------------------

             -
           /   \
         L1     6

       SUB L1 6 L2

       -------------------


        ADD 3 5 L1
        SUB L1 6 L2




            */
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

        //System.out.println("PROGRAM");

        this.depth.add(true);

        for (int i = 0; i < program.children.size(); i++) {
            program.children.get(i).accept(this);
        }

        this.depth.remove(this.depth.size() - 1);

        return "Program";
    }

    public void printClass() {
        String superClass = this.getClass().getSuperclass().getSimpleName();
        String subClass = this.getClass().getSimpleName();

        System.out.print(superClass + "[" + subClass + "]");
    }

    public void printAttributes() {

        // get all attributes
        Field[] fields = this.getClass().getDeclaredFields();

        // iterate through object attributes, which should only be tokens
        // unless overridden in the object's respective class.

        for (int i = 0; i < fields.length; i++) {
            if (i == 0) {
                System.out.print(" <");
            } else {
                System.out.print(" ");
            }

            try {
                System.out.print(((Token) fields[i].get(this)).str);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if (i == fields.length - 1) {
                System.out.print(">");
            }
        }

        System.out.println();
    }

    public void printNode() {
        this.printClass();
        this.printAttributes();

        printDepth();
/*
        for (int i = 0; i < this.childSize(); i++) {

            if (i == this.childSize() - 1) {
                System.out.print("`-- ");
                if (this.children.get(i).hasChildren()) {
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
                System.out.print("|-- ");
                if (this.children.get(i).hasChildren()) {
                    depth.add(true);
                }
            }

            this.children.get(i).printNode();

        }
*/
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

/*
    public void iterator(Node node) {
        for (int i = 0; i < node.children.size(); i++) {
            node.children.get(i).accept(this);
        }
    }
    */

}
