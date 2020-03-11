package parser.treeObjects;

import lexer.Token;
import parser.IVisitor;
import parser.Node;

import java.util.ArrayList;

public abstract class Declaration extends Node {

    public static class varDeclaration extends Declaration {

        public varDeclaration(Token type, Token ID) {
            this.addChild(new Variable(type, ID));
        }

        public varDeclaration() {
        }

        public void addVarDec(Token type, Token ID) {
            this.addChild(new Variable(type, ID));
        }

        public void addVarDec(Token type, Token ID, Expression expr) {
            this.addChild(new Variable(type, ID, expr));
        }

        public Variable getVarDec() {
            return (Variable) this.getLastChild();
        }

        public void fillDec(Expression expr) {
            for (int i = 0; i < this.childSize(); i++) {
                this.addChild(expr);
            }
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visitVarDecl(this);
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

        public Token getType() {
            return this.typeSpecifier;
        }

        public Token getVariableID() {
            return this.variableID;
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visitVariable(this);
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
            if (!this.hasChildren()) {
                this.addChild(new treeList.ParameterList());
            }
            this.children.get(0).addChild(parameter);
        }

        public void addStatement(Statement statement) {
            this.addChild(statement);
        }

        public int getParamSize() {
            if (!this.hasChildren()) {
                return 0;
            }

            return this.children.get(0).childSize();
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visitFunDecl(this);
        }
    }

    public static class Parameter extends Declaration {
        public Token type;
        public Token paramID;

        public Parameter(Token type, Token ID) {
            this.type = type;
            this.paramID = ID;
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visitParameter(this);
        }
    }

    public static class TypeDeclaration extends Declaration {

        public Token enumType;
        public Token enumID;

        public TypeDeclaration(Token enumTypeID, Token ID) {
            this.enumType = enumTypeID;
            this.enumID = ID;
        }

        public TypeDeclaration() {
        }

        public void addEnumVar(varDeclaration enumVars) {
            if (!this.hasChildren())
                this.addChild(new treeList.ParameterList());

            this.children = enumVars.children;
        }

        public static class EnumVar extends Declaration {
            public Token type;
            public Token enumID;

            public EnumVar(Token type, Token enumID) {
                this.type = type;
                this.enumID = enumID;
            }

            @Override
            public void accept(IVisitor visitor) {
                visitor.visitEnumVar(this);
            }
        }

        @Override
        public void accept(IVisitor visitor) {
            visitor.visitTypeDecl(this);
        }
    }
}
