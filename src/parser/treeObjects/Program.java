package parser.treeObjects;

import common.IVisitor;
import parser.Node;

/**
 *
 */
public class Program extends Node {
    public String progName = null;

    // initialize new declaration list
    public Program(String progName) {
        this.progName = progName;
    }

    // add item to current existing declaration list
    public void addDeclaration(Declaration dec) {
        this.addChild(dec);
    }

    public void printAttributes() {
        System.out.println(" <" + this.progName + ">");
    }

    public void printClass() {
        System.out.print("Program");
    }

    @Override
    public <T> T accept(IVisitor visitor) {
        return (T) visitor.visitProgram(this);
    }

    /*
    public <T> T accept(IVisitor builder) {
        return builder.visitProgram(this);
    }
     */
}
