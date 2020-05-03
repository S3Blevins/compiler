package parser;

import lexer.Token;
import lexer.TokenType;
import parser.treeObjects.Declaration;

import java.util.*;
//TODO add new scope levels for if else and for loops(done in parser.java)

public class SymbolTable {
    // Symbol table stack structure used for construction of the symbol table record
    Stack<HashMap<String, String>>symbolStack;
    SymbolRecord symbolRecord;

    boolean parentFlag = false;

    public SymbolTable() {
        symbolStack = new Stack<>();
        symbolRecord = new SymbolRecord();
    }

    public void addSymbolTable() {
        // don't add a new record if the stack is empty
        // so as to use the first record
        if (!symbolStack.empty()) {
            symbolRecord.lastTable(symbolStack.size() - 1).addChild();
        }

        symbolStack.push(new HashMap<>());
    }

    public void addSymbolTable(boolean flag) {
        parentFlag = flag;

        // don't add a new record if the stack is empty
        // so as to use the first record
        if (!symbolStack.empty()) {
            symbolRecord.lastTable(symbolStack.size() - 1).addChild();
        }

        symbolStack.push(new HashMap<>());
    }

    public void removeSymbolTable() {
        if(!symbolStack.empty()) {
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
        this.symbolRecord.lastTable(symbolStack.size() - 1).addVariable(ID.str, type.str);
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
}