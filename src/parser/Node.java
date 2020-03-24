package parser;

import common.IVisitor;
import lexer.Token;
import parser.treeObjects.treeList;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Node {

    public treeList<Node> children;

    public boolean addChild(Node child) {
        if (this.children == null && child != null) {
            this.children = new treeList<>();
        }

        if (child != null) {
            this.children.add(child);
            return true;
        }

        return false;
    }

    public int childSize() {
        if (this.children == null) {
            return 0;
        }
        return children.size();
    }

    public boolean hasChildren() {
        if (this.children == null) {
            return false;
        }
        return true;
    }

    public Node getLastChild() {
        return this.children.get(this.children.size() - 1);
    }

    public void printNode() {

    }

    public abstract <T> T accept(IVisitor visitor);
}

