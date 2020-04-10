package parser;

import lexer.Token;
import lexer.TokenType;
import parser.treeObjects.Declaration;

import java.util.*;
//TODO add new scope levels for if else and for loops(done in parser.java)

public class SymbolTable {
    //Create a stack of HashMaps <ID, Type>
    Stack<HashMap<String, String>>symbolStack;

    // symbol table record for printing
    StringBuilder symbolString;
    Formatter stringFormat;

    boolean parentFlag = false;

    public SymbolTable() {
        symbolStack = new Stack<>();
        symbolString = new StringBuilder();
        stringFormat = new Formatter(symbolString);
    }

    public void addSymbolTable() {
        symbolStack.push(new HashMap<>());
    }

    public void addSymbolTable(boolean flag) {
        parentFlag = flag;
        symbolStack.push(new HashMap<>());
    }

    public void removeSymbolTable() {
        if(!symbolStack.empty()) {
            tablePrinter();
            symbolStack.pop();
        }
    }

    public void checkExistence(Token ID) {
        // iterate through parent scopes
        for(HashMap map: symbolStack) {
            if (map.containsKey(ID.str)) {
                return;
            }
        }

        System.err.println("ERROR: '" + ID.str + "' is undefined on line " + ID.lineNumber + ".");
        System.exit(1);
    }

    //add a new element to current hash map
    public void addSymbol(Token type, Token ID) {
        // iterate through parent scopes
        for(HashMap map: symbolStack) {
            if (map.containsKey(ID.str)) {
                // if a match is found, check to see if the type is the same (function or int) and if it is, error and exit
                if(type.str.equals(map.get(ID.str))) {
                    System.err.println("ERROR: The declaration of '" + type.str + " " + ID.str + "' has already been declared on line " + ID.lineNumber + "." );
                    System.exit(1);
                }
            }
        }

        this.symbolStack.peek().put(ID.str, type.str);
    }

    public void addSymbol(Declaration.varDeclaration varDec) {
        Declaration.Variable var = varDec.getVarDec();

        Token type = var.getType();
        Token ID = var.getVariableID();
        addSymbol(type, ID);
    }


    public void addFun(Token function) {
        addSymbol(new Token("function", TokenType.TK_IDENTIFIER), function);
    }

    public boolean isParent() {
        if(parentFlag) {
            parentFlag = false;
            return true;
        }
        return false;
    }

    // prints symbol table with some printf magic
    public void tablePrinter() {
        int scope = symbolStack.size() - 1;
        String indent = ":   ".repeat(scope);

        // don't build on string if scope is empty
        if(symbolStack.peek().isEmpty()) {
            return;
        }

        // table header line
        stringFormat.format("%s+------------------------------------+\n", indent);

        // name the scope
        if (scope == 0) {
            stringFormat.format("%s| Scope Level: %-3s %20s", indent, scope, "|\n");
        } else {
            stringFormat.format("%s%s| Scope Level: %-3s %20s", ":\t".repeat(scope - 1), ": ->", scope, "|\n");
        }

        // print out the type and associated variable
        for (Map.Entry<String, String> set : symbolStack.peek().entrySet()) {
            stringFormat.format("%s| %8s | %-22s %2s\n", indent, set.getValue(), set.getKey(), "|");
        }

        stringFormat.format("%s+------------------------------------+\n", indent);

    }
}