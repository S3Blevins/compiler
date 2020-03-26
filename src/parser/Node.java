package parser;

import common.IVisitor;
import java.util.ArrayList;

public abstract class Node {

    public ArrayList<Node> children;

    public boolean addChild(Node child) {
        if (this.children == null && child != null) {
            this.children = new ArrayList<Node>();
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
        return (Node) this.children.get(this.children.size() - 1);
    }

    public void printNode() {

    }

    public abstract <T> T accept(IVisitor visitor);
}

