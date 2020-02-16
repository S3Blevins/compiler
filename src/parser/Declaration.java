package parser;

import lexer.Token;
import java.util.ArrayList;

public abstract class Declaration extends ASTNode{

    public static class varDeclaration extends Declaration {

        // (1) When varDeclaration gets init, itll call varDecList.
        public varDeclaration() {
            varDecList = new varDecList();
        }

        // (2) once called, varDecList will  init an arraylist for the variables.
        public static class varDecList {

            // (3) This is holding all the variables (tokens) on single line (i.e int i, j, k ,....)
            ArrayList<Token> variables;

            public varDecList() {
                this.variables = new ArrayList<>();
            }

            // TODO: Implement this piece of the rabbit hole.
            public static class varDecInitialize {

            }
        }

        varDecList varDecList; // The list of variables declared.
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
