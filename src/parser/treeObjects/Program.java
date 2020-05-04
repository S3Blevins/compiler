package parser.treeObjects;

import common.IVisitor;
import parser.Node;

/**
 * <h1>Program</h1>
 *
 * The entry point identifying object of our parser. The root of
 * the nodal class and all children descend from this object.
 *
 * @author Sterling Blevins, Damon Estrada, Garrett Bates, Jacob Santillanes
 * @version 1.0
 * @since 2020-03-28
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
