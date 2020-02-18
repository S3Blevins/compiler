package parser;

import lexer.Token;
import parser.treeObjects.treeList;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class Node {

    public treeList<Node> children;

    public void addChild(Node child) {
        if(this.children == null) {
            this.children = new treeList<>();
        }
        this.children.add(child);
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

    public void printNode(int depth) {
        this.printClass();
        this.printAttributes();

        for(int i=0; i < this.childSize(); i++) {

            System.out.print("   ".repeat(depth));

            if(i == this.childSize() - 1) {
                System.out.print(" `--");
            } else {
                System.out.print(" |--");
            }

            this.children.get(i).printNode(depth + 1);
        }
    }

}

