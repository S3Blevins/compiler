package parser;
//TODO add more Nessasary functions, implement in parser

import lexer.Token;
import parser.treeObjects.Declaration;
import parser.treeObjects.treeList;

import java.util.ArrayList;
import java.util.HashMap;

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
        this.ST.put(type, ID);
    }

    public void addSymbol(Declaration.varDeclaration varDec){
        Declaration.Variable var = varDec.getVarDec();

        Token type = var.getType();
        Token ID = var.getVariableID();
        this.ST.put(type, ID);
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

    //prints entire symbol table
    public void printTable(int scope){
        System.out.println("scope: " + scope);
        System.out.println("\nSYMBOL TABLE:");
        System.out.println(this.ST);
        System.out.println("\nSYMBOL TABLE CHILDREN:");

        if(this.hasTableChildren()) {
            for (int i = 0; i < this.children.size(); i++) {
                this.children.get(i).printTable(scope + 1);
            }
        }
    }


}
