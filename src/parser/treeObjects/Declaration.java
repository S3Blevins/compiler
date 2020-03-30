package parser.treeObjects;

import lexer.Token;
import common.IVisitor;
import parser.Node;

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
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitVarDecl(this);
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
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitVariable(this);
        }
    }

    public static class funDeclaration extends Declaration {
        public Token typeSpecifier;
        public Token functionID;
        private int parameterCount = 0;

        public funDeclaration(Token typeSpecifier, Token ID) {
            this.typeSpecifier = typeSpecifier;
            this.functionID = ID;
        }

        public void addParameter(Parameter parameter) {
            this.addChild(parameter);
            parameterCount += 1;
        }

        public void addStatement(Statement statement) {
            this.addChild(statement);
        }

        public int getParamSize() {
            if (!this.hasChildren()) {
                return 0;
            }

            return parameterCount;
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitFunDecl(this);
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
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitParameter(this);
        }
    }

    public static class TypeDeclaration extends Declaration {

        // For identifying enums, not the body matter.
        public Token enumType;
        public Token enumID;

        public TypeDeclaration() {
        }

        public void addEnumVars(varDeclaration enumVars) {
            if (!this.hasChildren())
                this.addChild(enumVars);

            this.children = enumVars.children;
        }

        @Override
        public <T> T accept(IVisitor visitor) {
            return (T) visitor.visitTypeDecl(this);
        }
    }
}
