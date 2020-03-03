package parser;

import lexer.Token;
import parser.treeObjects.Declaration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    //Create a new HasMap <ID, Type>
    HashMap<Token, Token> ST;

    //Creates a new array of hash maps
    ArrayList<SymbolTable> children;

    public SymbolTable(){
        this.ST = new HashMap<Token,Token>();
    }

    //create a new hash map
    public void addChildTable(SymbolTable child) {
        if(this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    //add a new element to current hash map
    public void addSymbol(Token type, Token ID){
        this.ST.put(ID, type);
    }

    public void addSymbol(Declaration.varDeclaration varDec){
        Declaration.Variable var = varDec.getVarDec();

        Token type = var.getType();
        Token ID = var.getVariableID();
        this.ST.put(ID, type);
    }

    //retreve a specific element
    public Token hasSymbol(Token ID){
        return this.ST.get(ID);
    }

    public int childTableSize() {
        if(this.children == null) {
            return 0;
        }
        return this.children.size();
    }

    public boolean hasTableChildren() {
        if(this.children == null) {
            return false;
        }
        return true;
    }

    // prints symbol table with some printf magic
    public void printTable(int scope){
        String indent = ":\t".repeat(scope);

        // table header line
        System.out.printf("%s+------------------------------------+\n", indent);

        // name the scope
        if(scope == 0) {
            System.out.printf("%s| Scope Level: %-3s %20s", indent, scope, "|\n");
        } else {
            System.out.printf("%s%s| Scope Level: %-3s %20s", ":\t".repeat(scope-1), ": ->", scope, "|\n");
        }

        // print out the type and associated variable
        for(Map.Entry<Token, Token> set: this.ST.entrySet()) {
            System.out.printf("%s| %4s | %-22s %6s\n", indent, set.getValue().str, set.getKey().str, "|");
        }

        // indicate the number of children, and print out the table formatting
        if(this.hasTableChildren()) {
            System.out.printf("%s|------------------------------------|\n", indent);
            System.out.printf("%s| This table has %s inner scope(s) %5s", indent, this.children.size(), "|\n");
            System.out.printf("%s+------------------------------------+\n", indent);
            for (int i = 0; i < this.children.size(); i++) {
                this.children.get(i).printTable(scope + 1);
            }
        } else {
            System.out.printf("%s+------------------------------------+\n", indent);
        }
    }


}
