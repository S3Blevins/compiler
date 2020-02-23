package parser.treeObjects;

import lexer.Token;
import parser.Node;

import java.util.ArrayList;

public abstract class Declaration extends Node {

    public static class varDeclaration extends Declaration {

        public varDeclaration(Token type, Token ID) {
            this.addChild(new Variable(type, ID));
        }

        public void addVarDec(Token type, Token ID) {
            this.addChild(new Variable(type, ID));
        }

        public void addVarDec(Token type, Token ID, Expression expr) {
            this.addChild(new Variable(type, ID, expr));
        }

        public void fillDec(Expression expr) {
            for(int i = 0; i < this.childSize(); i++) {
                this.addChild(expr);
            }
        }
    }

    public static class Variable extends Declaration {
        public Token typeSpecifier;
        public Token variableID;

        public Variable(Token type, Token ID, Expression expr) {
            this.typeSpecifier = type;
            this.variableID = ID;
            this.addChild(expr);
        }

        public Variable(Token type, Token ID) {
            this.typeSpecifier = type;
            this.variableID = ID;
        }
    }

    public static class funDeclaration extends Declaration {
        public Token typeSpecifier;
        public Token functionID;

        public funDeclaration(Token typeSpecifier, Token ID) {
            this.typeSpecifier = typeSpecifier;
            this.functionID = ID;
        }

        public void addParameter(Parameter parameter) {
            if(!this.hasChildren()) {
                this.addChild(new treeList.ParameterList());
            }
            this.children.get(0).addChild(parameter);
        }

        public void addStatement(Statement statement) {
            this.addChild(statement);
        }

        public int getParamSize() {
            if(!this.hasChildren()) {
                return 0;
            }

            return this.children.get(0).childSize();
        }
    }

    public static class Parameter extends Declaration {
        public Token type;
        public Token paramID;

        public Parameter(Token type, Token ID) {
            this.type = type;
            this.paramID = ID;
        }
    }

}