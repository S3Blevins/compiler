package parser;

import lexer.Token;
import java.util.ArrayList;

public abstract class Declaration extends ASTNode{

    public static class varDeclaration extends Declaration {

        public varDeclaration(Token typeSpecifier, Token varID) {
            this.typeSpecifier = typeSpecifier;
            this.varID = varID;
        }

        Token typeSpecifier;
        Token varID;
    }

    public static class funDeclaration extends Declaration {

        public funDeclaration(Token typeSpecifier, Token ID, paramList params, AStatement statement) {
            this.typeSpecifier = typeSpecifier;
            this.functionID = ID;
            this.params = params;
            this.statement = statement;
        }

        Token typeSpecifier;
        Token functionID;
        paramList params;
        AStatement statement;
    }

    public static class paramList {

        public paramList() {
            this.parameterList = new ArrayList<Param>();
        }

        ArrayList<Param> parameterList;

        public void addParam(Param parameter) {
            this.parameterList.add(parameter);
        }

        public int size() {
            return parameterList.size();
        }

        public static class Param {
            public Param(Token type, Token ID) {
                this.type = type;
                this.paramID = ID;
            }

            Token type;
            Token paramID;
        }

    }

    //TODO: override print method specific to declaration node
    public void printNode(){

    }
}
