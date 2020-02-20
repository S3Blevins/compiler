package parser;
//TODO add more Nessasary functions, implement in parser

import parser.treeObjects.treeList;

import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {
    //Create a new HasMap <Type, ID>
    HashMap<String, String> ST = new HashMap<String, String>();

    //Creates a new array of hash maps
    ArrayList<SymbolTable> children;

    //create a new hash map
    public void addChild(SymbolTable child) {
        if(this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    //add a new element to current hash map
    public void addSymbol(String type, String ID){
        ST.put(type, ID);
    }

    //retreve a specific element
    public String hasSymbol(String ID){
        return ST.get(ID);
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

}
