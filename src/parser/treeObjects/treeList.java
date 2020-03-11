package parser.treeObjects;

import parser.IVisitor;
import parser.Node;

import java.util.AbstractCollection;
import java.util.ArrayList;


public class treeList<T> extends Node {
    ArrayList<T> array;

    public treeList() {
        this.array = new ArrayList<T>();
    }

    public boolean add(T element) {
        return this.array.add(element);
    }

    public T remove(int index) {
        return this.array.remove(index);
    }

    public int size() {
        return this.array.size();
    }

    public T get(int index) {
        return this.array.get(index);
    }

    public static class ParameterList extends treeList {
        public ParameterList() {

        }

        @Override
        public void accept(IVisitor visitor) {

            visitor.visitParamList(this);
        }
    }
}

