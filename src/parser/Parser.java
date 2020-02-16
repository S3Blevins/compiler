package parser;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class Parser {

        private Token previous;
        private ArrayList<Token> tokens;
        private HashMap<TokenType, ParseRule> rules;

        /*
         * 14 Feb 2020
         *
         * Turned Parser into a singleton.
         * I may go in and do the same refactoring
         * to the Lexer because it will make things
         * a little more manageable.
         * By turning parser into a singleton we
         * won't have to pass the Parser instance
         * to the ParseRule delegates. Instead
         * we can just access Parser.Instance() from
         * within the current delegate.
         * P nifty, I would say.
         *
         * -- Garrett
         */
        private static Parser instance = null;

        enum Precedence {
                NONE,
                ASSIGNMENT,
                OR,
                AND,
                EQUALITY,
                COMPARISON,
                TERM,
                FACTOR,
                UNARY,
                CALL,
                PRIMARY
        }

        /**
         * ParseRule follows the functionality of a Pratt Parser
         *
         * A Null Denotation (Nud) is a token that does not care about the tokens to the left
         *      Typically variables, literals, and prefix operators
         *
         * A Left Denotation (Led) is a token where the left token is relevant
         *      Typically infix and suffix operators
         */
        static class ParseRule {

                // NULL Denotation
                public static abstract class Nud {

                        abstract Expression exec();
                }

                // Left Denotation
                public static abstract class Led {

                        abstract Expression exec(Expression left);
                }

                public ParseRule(Nud nud, Led led, Precedence precedence) {

                        this.nud = nud;
                        this.led = led;
                        this.precedence = precedence;
                }

                public Nud nud; // Delegate for tokens with no left context
                public Led led; // Delegate for tokens with a left context
                public Precedence precedence;
        }

        private Parser() {

                // initialize rule table
                rules = new HashMap<TokenType, ParseRule>();

                rules.put(TokenType.TK_LPAREN, new ParseRule(
                        new ParseRule.Nud() {
                                Expression exec() {
                                        Parser parser = Parser.Instance();
                                        Expression expr = parser.Expression();
                                        parser.previous = parser.tokens.remove(0);
                                        return new Expression.Group(expr);
                                }
                        }, new ParseRule.Led() {
                                Expression exec(Expression left) {
                                        return null;
                                }
                        }, Precedence.CALL));

                rules.put(TokenType.TK_MINUS, new ParseRule(
                        new ParseRule.Nud() {
                                Expression exec() {
                                        Parser parser = Parser.Instance();
                                        return parser.Unary();
                                }
                        }, new ParseRule.Led() {
                                Expression exec(Expression left) {
                                        Parser parser = Parser.Instance();
                                        return parser.Binary(left);
                                }
                        }, Precedence.TERM));

                rules.put(TokenType.TK_PLUS, new ParseRule(
                        new ParseRule.Nud() {
                                Expression exec() {
                                        return null;
                                }
                        }, new ParseRule.Led() {
                        Expression exec(Expression left) {
                                Parser parser = Parser.Instance();
                                return parser.Binary(left);
                        }
                }, Precedence.TERM));

                rules.put(TokenType.TK_NUMBER, new ParseRule(
                        new ParseRule.Nud() {
                                Expression exec() {
                                        return null;
                                }
                        }, new ParseRule.Led() {
                        Expression exec(Expression left) {
                                return null;
                        }
                }, Precedence.PRIMARY));
        }

        // singleton pattern only has one instance of the object
        public static Parser Instance() {
                if(Parser.instance == null) {
                        Parser.instance = new Parser();
                }

                return Parser.instance;
        }

        Expression.Number Number() {
                return null;
        }

        Expression.Unary Unary() {
                Token operator = this.previous;
                Expression expr = this.Expression();
                this.previous = this.tokens.remove(0);

                return new Expression.Unary(expr, operator);
        }

        Expression.Binary Binary(Expression left) {
                Token operator = this.previous;

                ParseRule rule = this.rules.get(operator.tokenType);
                int precedence = rule.precedence.ordinal() + 1;
                Expression expr = ParsePrecedence(Precedence.values()[precedence]);

                return new Expression.Binary(left, operator, expr);
        }

        Expression ParsePrecedence(Precedence prec) {
                Expression left = null;

                ParseRule rule = rules.get(previous.tokenType);
                if(rule.nud == null) {

                        /*
                         * If this happens, we were expecting
                         * to encounter an expression.
                         * Raise an exception / handle the error
                         */
                        return null;
                }

                /*
                 * I'm relatively certain that this loop is set up correctly
                 * as the implementation of compareTo subtracts the other
                 * operand from the calling object. We are looking for
                 * while(prec <= rules.get(...).precedence)
                 */
                while(prec.compareTo(rules.get(tokens.get(0).tokenType).precedence) <= 0) {

                        previous = tokens.remove(0);
                        left = rules.get(previous.tokenType).led.exec(left);
                }

                return left;
        }

        Expression Expression() {
                //System.out.println("Expression()");

                Expression expr = null;

                // This is temporary implementation of expression until it is combined with the
                // precedence parsing above
                if(tokens.get(0).tokenType == TokenType.TK_NUMBER) {
                        expr = new Expression.Number(Integer.parseInt(tokens.get(0).str));

                        // remove number
                        tokens.remove(0);
                }

                return expr;

                // return ParsePrecedence(Precedence.ASSIGNMENT);
        }

        ASTNode Statement() {
                //System.out.println("Statement()");

                previous = tokens.get(0);
                AStatement statement = null;

                if(previous.tokenType == TokenType.TK_LBRACE) {
                        // compound/block statement
                        statement = new AStatement.Block();

                        while(tokens.get(0).tokenType != TokenType.TK_RBRACE) {
                                previous = tokens.remove(0);

                                // has to either be a statement or a declaration
                                if(previous.tokenType == TokenType.TK_TYPE) {
                                        ((AStatement.Block) statement).addDeclaration((Declaration) Declaration());
                                } else {
                                        ((AStatement.Block) statement).addStatement((AStatement) Statement());
                                }
                        }

                        // removes right brace
                        tokens.remove(0);

                } else if(previous.tokenType == TokenType.TK_KEYWORDS) {
                        String keywordIndicator = previous.str.toLowerCase();

                        switch(keywordIndicator) {
                                case "if":
                                        // selection statement
                                        //TODO: further implementation
                                        statement = new AStatement.Selection();
                                        break;
                                case "while":
                                        // iteration statement
                                        //TODO: further implementation
                                        Expression condition = Expression();
                                        AStatement.Block body = (AStatement.Block) Statement();

                                        statement = new AStatement.Iteration(condition, body);

                                        break;
                                case "return":
                                        // return statement
                                        statement = new AStatement.Return();

                                        // remove return
                                        tokens.remove(0);

                                        // in the event that the next token after the return is not a semicolon,
                                        // then it is an expression
                                        if(tokens.get(0).tokenType != TokenType.TK_SEMICOLON) {
                                                ((AStatement.Return) statement).setExpression(Expression());
                                        }

                                        // remove semicolon
                                        tokens.remove(0);
                                        break;
                                case "break":
                                        // break statement is empty
                                        statement = new AStatement.Break();
                                        break;
                                default:
                                        System.out.println("Somethin's wrong and Imma head out...");
                                        exit(1);
                        }

                } else {
                        // expression statement

                        // if the next token is not a semicolon, then it is an expression
                        if(previous.tokenType != TokenType.TK_SEMICOLON) {
                                //TODO: implement

                                //statement = Expression();
                        }

                        // remove remaining semicolon
                        tokens.remove(0);
                }

                return statement;
        }

        /**
         * Check for either a variable declaration or a function declaration
         * @return
         */
        ASTNode Declaration() {
                //System.out.println("Declaration()");
                Token typeSpec;
                Token decID;

                // check for error
                if(tokens.get(0).tokenType != TokenType.TK_TYPE && tokens.get(1).tokenType != TokenType.TK_IDENTIFIER) {
                        System.err.println("Invalid Declaration!");
                        exit(1);
                }

                // remove the specified element, and store it into the attributes
                typeSpec = tokens.remove(0);
                decID = tokens.remove(0);

                previous = tokens.remove(0);

                // check to see if a parenthesis exists (means a function declaration)
                if(previous.tokenType == TokenType.TK_LPAREN) {
                        Declaration.paramList parList = new Declaration.paramList();

                        while(tokens.get(0).tokenType != TokenType.TK_RPAREN) {
                                if(parList.size() > 255) {
                                        System.err.println("Too many parameters in list!");
                                        exit(1);
                                }

                                Token type = tokens.remove(0);

                                if(type.tokenType != TokenType.TK_TYPE && tokens.get(0).tokenType != TokenType.TK_IDENTIFIER) {
                                        System.err.println("Invalid parameter!");
                                        exit(1);
                                }

                                Token paramID = tokens.remove(0);

                                // create a parameter, and add it to the list
                                Declaration.paramList.Param parameter = new Declaration.paramList.Param(type, paramID);
                                parList.addParam(parameter);

                                // check to see if next token is a comma, if it is, then continue the loop.
                                if(tokens.remove(0).tokenType != TokenType.TK_COMMA) {
                                        break;
                                }
                        }

                        // remove the right parenthesis
                        tokens.remove(0);

                        AStatement funDecStmnt = (AStatement) Statement();
                        return new Declaration.funDeclaration(typeSpec, decID, parList, funDecStmnt);
                }

                // Made it to this point, must be a variable declaration
                // Loop through tokens until we see a semi colon. This is to handle
                // the case where we have multiple variables defined on the same line.
                Declaration.varDeclaration varDeclaration = new Declaration.varDeclaration(typeSpec);

                // Add the first varID str as the first variable.
                varDeclaration.varDecList.variables.add(decID);

                // Previous holds the last comma at this point
                while (previous.tokenType != TokenType.TK_SEMICOLON) {

                        // Get next varID
                        previous = tokens.remove(0);

                        System.out.println("previous = " + previous);

                        // This condition handles this --> // [int column], row, index;
                        if (previous.tokenType == TokenType.TK_IDENTIFIER) {
                                // We should get the next varID here with the same type.
                                varDeclaration.varDecList.variables.add(previous);
                        } else if (previous.tokenType == TokenType.TK_EQUALS) {
                                // This condition handles this --> [int column] = 0, row = 0, index = 0;

                        } else {
                                System.err.println("Declaration(): There was a type or TOKEN that does not follow the grammar.");
                                System.exit(1);
                        }

                        // Remove next token, will break out if semi-colon
                        previous = tokens.remove(0);

                }

                System.out.println("tokens = " + tokens);

                // Testing if this is working as intended.
                System.out.print("\nSame line variables\nint ");
                for (int i = 0; i < varDeclaration.varDecList.variables.size(); i++) {

                        System.out.print(varDeclaration.varDecList.variables.get(i).str);

                        if (i + 1 == varDeclaration.varDecList.variables.size()) {
                                System.out.println(";");
                                break;
                        } else
                                System.out.print(", ");
                }

                System.out.println("");

                return varDeclaration;
        }

        /**
         * Program calls upon Declaration(), looking for declarations until the token list is empty
         * @return ASTNode containing the head to the Abstract Syntax
         */
        ASTNode Program(String fileName) {
                // initialize a new list of declarations
                Program ASThead = new Program();

                // run until tokens list is empty
                while(!tokens.isEmpty()) {
                        //System.out.println("tokens = " + tokens);
                        //System.out.println("Program()");
                        ASThead.addDeclaration((Declaration) Declaration());
                }

                ASThead.printNode(fileName, 0);

                return ASThead;
        }

        public ASTNode Parse(ArrayList<Token> tokens, String fileName) {
                this.tokens = tokens;



                return Program(fileName);
        }
}

