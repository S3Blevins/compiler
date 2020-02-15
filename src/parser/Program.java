package parser;

import java.util.ArrayList;

import static java.lang.System.exit;

/**
 *
 */
public class Program extends ASTNode {
    ArrayList<Declaration> declarationList;

    // initialize new declaration list
    public Program() {
        this.declarationList = new ArrayList<Declaration>();
    }

    // insert declaration list
    public Program(ArrayList<Declaration> declarationList) {
        this.declarationList = declarationList;
    }

    // add item to current existing declaration list
    public void addDeclaration(Declaration dec){
        this.declarationList.add(dec);
    }


    //TODO: override print method specific to program node
    public void printNode(){

    }
}
