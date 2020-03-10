package parser;

import lexer.Token;
import parser.treeObjects.treeList;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Node {

    public treeList<Node> children;

    public boolean addChild(Node child) {
        if(this.children == null && child != null) {
            this.children = new treeList<>();
        }
        
        if(child != null) {
            this.children.add(child);
            return true;
        }

        return false;
    }

    public int childSize() {
        if(this.children == null) {
            return 0;
        }
        return children.size();
    }

    public boolean hasChildren() {
        if(this.children == null) {
            return false;
        }
        return true;
    }

    public Node getLastChild(){
        return this.children.get(this.children.size()-1);
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

        for(int i = 0; i < fields.length; i++) {
            if(i == 0) {
                System.out.print(" <");
            } else {
                System.out.print(" ");
            }

            try {
                System.out.print(((Token) fields[i].get(this)).str);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

            if(i == fields.length - 1) {
                System.out.print(">");
            }
        }

        System.out.println();
    }

    public void printNode(ArrayList<Boolean> depthTracker) {
        this.printClass();
        this.printAttributes();

        printDepth(depthTracker);

        for (int i = 0; i < this.childSize(); i++) {

            if (i == this.childSize() - 1) {
                System.out.print("`-- ");
                if (this.children.get(i).hasChildren()) {
                    depthTracker.add(false);
                } else {
                    // while the last flag of the array is false, remove boolean flags.
                    while(depthTracker.size() != 0 && !depthTracker.get(depthTracker.size() - 1)) {
                        depthTracker.remove(depthTracker.size() - 1);
                    }

                    // remove a true element to realign node in tree
                    if(depthTracker.size() != 0) {
                        depthTracker.remove(depthTracker.size() - 1);
                    }
                }
            } else {
                System.out.print("|-- ");
                if (this.children.get(i).hasChildren()) {
                    depthTracker.add(true);
                }
            }

            this.children.get(i).printNode(depthTracker);

        }

    }

    public void printDepth(ArrayList<Boolean> depthTracker) {
        for(int i = 0; i < depthTracker.size(); i++) {
            if(depthTracker.get(i)) {
                System.out.print("|   ");
            } else {
                System.out.print("    ");
            }
        }
    }

    void accept(IVisitor visitor) throws Exception {

        throw new Exception();
    }
}

