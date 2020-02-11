package parser.grammar;

import lexer.Token;
import lexer.TokenType;
import parser.Expression;
import parser.Parser;

import java.util.ArrayList;
import java.util.HashMap;

public class Grammar {

    Token prev;
    Token next;
    Token current;

    ArrayList<Token> tokens;
    ArrayList<String> grammarArray;

    public Grammar(ArrayList<Token> tokens) {
        this.tokens = tokens;
        grammarArray = new ArrayList<>();
    }

    /**
     ************************************************
     *                                              *
     *               Production Rules               *
     *                                              *
     ************************************************
    */

    /**
     * C- grammar #1
     */
    public void program() {

        /* This is removing  */

        //this.prev = updatedTokensList.get(0);
        //this.current = tokens.remove(0);

        //for(int endOfList = 0; endOfList < tokens.size(); endOfList++) {
            //grammarArray.addAll(declaration());
        //}

        /* Need to parse until the tokens array is used and grammarArray is populated with production rules. */
        while (tokens.size() > 0) {
            declaration();
        }
    }

    /**
     * C- grammar #2
     */
    public void declarationList() {

    }

    /**
     * C- grammar #3
     * Here we first check if we are dealing with a declaration. If we encounter an int,
     * remove it and look ahead ahead one token beyond the new 0th element.
     * If it is a LPAREN, we have a start of a function, else, it can be a , or =.
     */
    public void declaration() {

        //TODO implement TK_STRUCT (... || tokens.get(0).tokenType == TokenType))
        if (tokens.get(0).tokenType == TokenType.TK_TYPE) {
            /* Get rid the 'Int' type */
            tokens.remove(0);

            /* If LPAREN is found, we have a function */
            if (tokens.get(1).tokenType == TokenType.TK_LPAREN) {
                funDeclaration();  // Notice we have a function.
            } else { // Call varDeclaration b/c it isn't a funDec (From grammar)
                varDeclaration();
            }
        } else {
            grammarArray.add("declaration() ERROR");
        }
    }

    /**
     * C- grammar #4
     */
    public void varDeclaration() {

    }

    /**
     * C- grammar #5
     */
    public void scopedVarDeclaration() {}

    /**
     * C- grammar #6
     */
    public void varDecList() {}

    /**
     * C- grammar #8
     */
    public void varDecId() {}

    /**
     * C- grammar #10
     */
    public void typeSpecifier() {}

    /**
     * C- grammar #11
     * At this point, tokens.get(0) holds the '(' token.
     */
    public void funDeclaration() {

        tokens.remove(0); // Remove function name from tokens; tokens.get(0) = TK_LPAREN.
        tokens.remove(0); // Removes LPAREN

        /* If the token ahead of LPAREN is RPAREN then we have no function arguments. */
        if (tokens.get(1).tokenType == TokenType.TK_RPAREN) {
            grammarArray.add("epsilon"); // Since no parameters (params can return epsilon).
        } else {
            /* We must have parameters if we do not have a closing RPAREN */
            params();
        }

        tokens.remove(0); // Removes RPAREN
        statement(); // B/c the only thing that can happen next is a statement() call from grammar.

    }

    /**
     * C- grammar #12
     */
    public void params() {

    }

    /**
     * C- grammar #13
     */
    public void paramsList() {}

    /**
     * C- grammar #17
     */
    public void statement() {

        HashMap<String, TokenType> expressions = new HashMap<>() {{
            put("=", TokenType.TK_EQUALS);
            put("+=", TokenType.TK_PLUSEQ);
            put("-=", TokenType.TK_MINUSEQ);
            put("*=", TokenType.TK_STAREQ);
            put("/=", TokenType.TK_SLASHEQ);
            //put("++", TokenType.TK_PL);
            //put("--", TokenType.TK_PL);
        }};

        HashMap<String, TokenType> selections = new HashMap<>() {{
           put("if", TokenType.TK_KEYWORDS);
           put("elseif", TokenType.TK_KEYWORDS);
           put("else", TokenType.TK_KEYWORDS);
           put("then", TokenType.TK_KEYWORDS);
        }};

        HashMap<String, TokenType> iterations = new HashMap<>() {{
            put("while", TokenType.TK_KEYWORDS);
            put("do", TokenType.TK_KEYWORDS);
            put("loop", TokenType.TK_KEYWORDS);
            put("forever", TokenType.TK_KEYWORDS);
        }};

        /* Compound statement */
        if (tokens.get(0).tokenType == TokenType.TK_LBRACE) {
            //return compoundStatement();
            compoundStatement();
        }
    }

    /**
     * C- grammar #18
     */
    public void expressionStatement() {}

    /**
     * C- production rule #19
     */
    public void compoundStatement() {

        /* Remove the LBRACE token. */
        tokens.remove(0);

    }

    /**
     * C- grammar #20
     */
    public void localDeclarations() {}

    /**
     * C- grammar #21
     */
    public void statementList() {

    }

    /**
     * C- grammar #26
     */
    public void returnStatement() {}

    /**
     * C- grammar #28
     */
    public void expression() {}

    /**
     * C- grammar #29
     */
    public void simpleExpression() {}

    /**
     * C- grammar #30
     */
    public void andExpression() {}

    /**
     * C- grammar #31
     */
    public void unaryRelExpression() {}

    /**
     * C- grammar #32
     */
    public void relExpression() {}

    /**
     * C- grammar #34
     */
    public void sumExpression() {}

    /**
     * C- grammar #36
     */
    public void mulExpression() {}

    /**
     * C- grammar #38
     */
    public void unaryExpression() {}

    /**
     * C- grammar #40
     */
    public void factor() {}

    /**
     * C- grammar #42
     */
    public void immutable() {}

    /**
     * C- grammar #46
     */
    public void constant() {}

    /* Garrett */
    public static Expression parseExpression(ArrayList<Token> tokens) {

        Parser.tokens = tokens;
        Parser.current = tokens.remove(0);
        Parser.previous = Parser.current;

        return null;
    }
}
