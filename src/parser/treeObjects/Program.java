package parser.treeObjects;

import parser.IVisitor;
import parser.Node;

import javax.swing.*;
import java.util.ArrayList;

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
    public void addDeclaration(Declaration dec){
        this.addChild(dec);
    }

    public void printAttributes() {
        System.out.println(" <"+this.progName+">");
    }

    public void printClass() {
        System.out.print("Program");
    }

    void accept(IVisitor visitor) {
        visitor.visitProgram(this);
    }
}
