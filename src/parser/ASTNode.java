package parser;

import lexer.Token;

import java.lang.reflect.Field;
import java.util.ArrayList;


public abstract class ASTNode {

    // prints the entire tree, node-by-node pretty print
    // This was hard to figure out how to generalize...
    public void printNode(int depth) {

        // current class variation of the ASTNode
        Class currentNode = this.getClass();

        // print the name of the node, and attributes if relevant
        System.out.print(currentNode.getSimpleName());
        this.printAttributes();

        // get all attributes of the class
        Field[] fields = this.getClass().getDeclaredFields();

        // iterate through object attributes, recursively printing if the attribute is another node
        for(int i = 0; i < fields.length; i++) {
            try {
                // if the attribute to the parent object is another node, do some indentation work, and print
                if(fields[i].get(this) instanceof ASTNode) {
                    System.out.print("   ".repeat(depth));

                    if(i == fields.length - 1) {
                        System.out.print(" `--");
                    } else {
                        System.out.print(" |--");
                    }
                    ((ASTNode) fields[i].get(this)).printNode(depth + 1);
                } else if(fields[i].get(this) instanceof ArrayList<?>) {
                    // in the event that the parent object can have an infinite number of child nodes,
                    // iterate through the node list
                    ArrayList<ASTNode> nodeList = (ArrayList<ASTNode>) fields[i].get(this);

                    // Add a check to see if we are dealing with a variable.
                    ArrayList<Declaration.varDecList.Variable> variables =
                            (ArrayList<Declaration.varDecList.Variable>) fields[i].get(this);

                    if (variables.get(0) != null) {
                        // we have a variable
                    } else {
                        // Not a variable
                        for (int j = 0; j < nodeList.size(); j++) {
                            System.out.print("   ".repeat(depth));

                            if (i == fields.length - 1) {
                                System.out.print(" `--");
                            } else {
                                System.out.print(" |--");
                            }

                            // don't indent this time
                            nodeList.get(j).printNode(depth);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void printAttributes() {
        // print the attributes within <> if applicable
        System.out.print(" <");

        // get all attributes
        Field[] fields = this.getClass().getDeclaredFields();

        //System.out.print(fields[0].getName());

        // iterate through object attributes, but only look at tokens if possible
        for(int i = 0; i < fields.length; i++) {

            try {
                if (fields[i].getName().equals("varDecList")) {

                    ArrayList<Declaration.varDecList.Variable> vars =
                            (ArrayList<Declaration.varDecList.Variable>) fields[i].get(this);

                    // Print the variables from the varDecList
                    for (int ii = 0; ii < vars.size(); ii++) {
                        if (ii + 1 == vars.size())
                            System.out.print(vars.get(ii).varID.str);
                        else
                            System.out.print(vars.get(ii).varID.str + ", ");
                    }

                } else if(fields[i].get(this) instanceof Token) {
                    // if a token, it is a terminal in the grammar
                    // only add a space within the brackets if not the first terminal
                    if(i != 0) {
                        System.out.print(" ");
                    }
                    System.out.print(((Token) fields[i].get(this)).str);
                } else {
                    // break if there happen to be many attributes which are not tokens-
                    // attributes are ordered with terminals first (in theory)
                    break;
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        System.out.println(">");
    }

}

