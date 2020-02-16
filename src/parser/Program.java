package parser;

import java.util.ArrayList;

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
    public void printNode(String progName, int depth) {
        System.out.println("program <" +  progName + ">");

        for(int i = 0; i < declarationList.size(); i++) {
            if(i == declarationList.size() - 1) {
                System.out.print(" `--");
            } else {
                System.out.print(" |--");
            }
            declarationList.get(i).printNode(depth + 1);
        }
    }
}
