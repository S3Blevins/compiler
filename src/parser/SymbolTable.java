package parser;

import lexer.Token;
import parser.treeObjects.Declaration;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
//TODO add new scope levels for if else and for loops(done in parser.java)

public class SymbolTable {
    //Create a new HasMap <ID, Type>
    HashMap<String, String> ST;

    //Creates a new array of hash maps
    ArrayList<SymbolTable> children;

    public SymbolTable() {
        this.ST = new HashMap<String, String>();
    }

    //create a new hash map
    public void addChildTable(SymbolTable child) {
        if (this.children == null) {
            this.children = new ArrayList<>();
        }
        this.children.add(child);
    }

    //add a new element to current hash map
    public void addSymbol(Token type, Token ID) {

        if (ST.containsKey(ID.str)) {
            System.err.println("This variable already exists : " + ID.tokError());
            System.exit(1);
        }

        this.ST.put(ID.str, type.str);
    }

    public void addSymbol(Declaration.varDeclaration varDec) {
        Declaration.Variable var = varDec.getVarDec();

        Token type = var.getType();
        Token ID = var.getVariableID();
        if (ST.containsKey(ID.str)) {
            System.err.println("This variable already exists : " + ID.tokError());
            System.exit(1);
        }

        this.ST.put(ID.str, type.str);
    }

    //checks the size of a child table
    public int childTableSize() {
        if (this.children == null) {
            return 0;
        }
        return this.children.size();
    }

    //Checks if a table has a child table or not
    public boolean hasTableChildren() {
        if (this.children == null) {
            return false;
        }
        return true;
    }

    // prints symbol table with some printf magic
    public void printTable(int scope) {
        String indent = ":\t".repeat(scope);

        // table header line
        System.out.printf("%s+------------------------------------+\n", indent);

        // name the scope
        if (scope == 0) {
            System.out.printf("%s| Scope Level: %-3s %20s", indent, scope, "|\n");
        } else {
            System.out.printf("%s%s| Scope Level: %-3s %20s", ":\t".repeat(scope - 1), ": ->", scope, "|\n");
        }

        // print out the type and associated variable
        for (Map.Entry<String, String> set : this.ST.entrySet()) {
            System.out.printf("%s| %4s | %-22s %6s\n", indent, set.getValue(), set.getKey(), "|");
        }

        // indicate the number of children, and print out the table formatting
        if (this.hasTableChildren()) {
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

    // prints symbol table to a file without some printf magic
    public void printTableFile(int scope, PrintWriter pw) {
        String indent = ":\t".repeat(scope);

        // table header line
        pw.printf("%s+------------------------------------+\n", indent);

        // name the scope
        if (scope == 0) {
            pw.printf("%s| Scope Level: %-3s %20s", indent, scope, "|\n");
        } else {
            pw.printf("%s%s| Scope Level: %-3s %20s", ":\t".repeat(scope - 1), ": ->", scope, "|\n");
        }

        // print out the type and associated variable
        for (Map.Entry<String, String> set : this.ST.entrySet()) {
            pw.printf("%s| %4s | %-22s %6s\n", indent, set.getValue(), set.getKey(), "|");
        }

        // indicate the number of children, and print out the table formatting
        if (this.hasTableChildren()) {
            pw.printf("%s|------------------------------------|\n", indent);
            pw.printf("%s| This table has %s inner scope(s) %5s", indent, this.children.size(), "|\n");
            pw.printf("%s+------------------------------------+\n", indent);
            for (int i = 0; i < this.children.size(); i++) {
                this.children.get(i).printTableFile(scope + 1, pw);
            }
        } else {
            pw.printf("%s+------------------------------------+\n", indent);
        }
    }
}