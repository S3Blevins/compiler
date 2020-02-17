package parser;

import lexer.Token;

import java.lang.reflect.Field;
import java.util.ArrayList;

public abstract class Declaration extends ASTNode{

    public static class varDeclaration extends Declaration {

        // (1) When varDeclaration gets init, it will call varDecList.
        public varDeclaration(Token typeSpecifier, Token firstVar) {
            this.variables = new varDecList(typeSpecifier, firstVar);
            this.typeSpecifier = typeSpecifier;
        }

        Token typeSpecifier;
        varDecList variables; // The list of variables declared.
    }

    // (2) once called, varDecList will  init an arraylist for the variables.
    public static class varDecList extends Declaration {

        // (3) This is holding all the variables (tokens) on single line (i.e int i, j, k ,....)
        ArrayList<Variable> varDecList;

        public varDecList(Token type, Token firstVar) {
            this.varDecList = new ArrayList<>(){{add(new Variable(type, firstVar));}};
        }

        public void addVarDec(Variable variable) {
            this.varDecList.add(variable);
        }

        public static class Variable {
            public Variable(Token type, Token varID) {
                this.type = type;
                this.varID = varID;
            }

            Token type;
            Token varID;
        }

        // TODO: Implement this piece of the rabbit hole.
        public static class varDecInitialize {

        }
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

    public static class paramList extends Declaration {

        ArrayList<Param> parameterList;

        public paramList() {
            this.parameterList = new ArrayList<Param>();
        }

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
    // prints the entire tree, node-by-node pretty print

}
