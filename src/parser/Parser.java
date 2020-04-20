package parser;

import lexer.Token;
import lexer.TokenType;
import parser.treeObjects.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;

public class Parser {

    private Token previous;
    private ArrayList<Token> tokens;
    private HashMap<TokenType, ParseRule> rules;
    private SymbolTable symbolTable = new SymbolTable();
    private Token enumVal = new Token("0", TokenType.TK_NUMBER); // This is used to increment enum variables.

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

    // singleton pattern only has one instance of the object
    public static Parser Instance() {
        if (Parser.instance == null) {
            Parser.instance = new Parser();
        }

        return Parser.instance;
    }

    enum Precedence {
        NONE,
        ASSIGNMENT,
        TERNARY,
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
     * <p>
     * A Null Denotation (Nud) is a token that does not care about the tokens to the left
     * Typically variables, literals, and prefix operators
     * <p>
     * A Left Denotation (Led) is a token where the left token is relevant
     * Typically infix and suffix operators
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
                    Expression expr = parser.expressionGrammar();
                    parser.previous = parser.tokens.remove(0);
                    return new Expression.Group(expr);
                }
            }, new ParseRule.Led() {
                Expression exec(Expression left) {
                    Parser parser = Parser.Instance();

                    Expression.Identifier expr = (Expression.Identifier) left;

                    Expression functionCall = new Expression.funCall(expr.value.str);

                    while(parser.previous.tokenType != TokenType.TK_RPAREN) {
                        // consume the argument and add it to the functionCall node as children
                        functionCall.addChild(ParsePrecedence(Precedence.PRIMARY));
                        // consume either the comma or the right parenthesis
                        parser.previous = parser.tokens.remove(0);
                    }

                    return functionCall;
                }
        }, Precedence.CALL));

        rules.put(TokenType.TK_RPAREN, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
                Expression exec(Expression left) {
                return null;
            }
        }, Precedence.NONE));

        rules.put(TokenType.TK_SEMICOLON, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                return null;
            }
        }, Precedence.NONE));

        rules.put(TokenType.TK_COMMA, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                return null;
            }
        }, Precedence.NONE));

        rules.put(TokenType.TK_BANG, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    Parser parser = Parser.Instance();
                    return parser.Unary();
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                return null;
            }
        }, Precedence.UNARY));

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

        /* TODO:
         * 25 February 2020
         * This does NOT retain info on whether it was a pre
         * or post increment operation! We should probably add
         * a special Expression node type to denote that information.
         * Otherwise x++ and ++x are the same operation! This also applies
         * to the following rule for TK_MMINUS
         * -- Garrett
         */
        rules.put(TokenType.TK_PPLUS, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    Parser parser = Parser.Instance();
                    return parser.Unary();
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.UnaryPost(left);
            }
        }, Precedence.UNARY));

        rules.put(TokenType.TK_MMINUS, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    Parser parser = Parser.Instance();
                    return parser.Unary();
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.UnaryPost(left);
            }
        }, Precedence.UNARY));

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

        rules.put(TokenType.TK_SLASH, new ParseRule(
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
        }, Precedence.FACTOR));

        rules.put(TokenType.TK_STAR, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }
        }, Precedence.FACTOR));

        rules.put(TokenType.TK_EQUALS, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.ASSIGNMENT));

        rules.put(TokenType.TK_STAREQ, new ParseRule(
                new ParseRule.Nud() {
                    Expression exec() {
                        return null;
                    }
                }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.ASSIGNMENT));

        rules.put(TokenType.TK_MINUSEQ, new ParseRule(
                new ParseRule.Nud() {
                    Expression exec() {
                        return null;
                    }
                }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.ASSIGNMENT));

        rules.put(TokenType.TK_PLUSEQ, new ParseRule(
                new ParseRule.Nud() {
                    Expression exec() {
                        return null;
                    }
                }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.ASSIGNMENT));

        rules.put(TokenType.TK_SLASHEQ, new ParseRule(
                new ParseRule.Nud() {
                    Expression exec() {
                        return null;
                    }
                }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.ASSIGNMENT));

        rules.put(TokenType.TK_EQEQUAL, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.EQUALITY));

        rules.put(TokenType.TK_NEQUAL, new ParseRule(
                new ParseRule.Nud() {
                    Expression exec() {
                        return null;
                    }
                }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.EQUALITY));

        rules.put(TokenType.TK_LESSEQ, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.COMPARISON));

        rules.put(TokenType.TK_GREATEREQ, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.COMPARISON));

        rules.put(TokenType.TK_LESS, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.COMPARISON));

        rules.put(TokenType.TK_GREATER, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.COMPARISON));

        rules.put(TokenType.TK_LOGOR, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.OR));

        rules.put(TokenType.TK_LOGAND, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Binary(left);
            }

        }, Precedence.AND));

        rules.put(TokenType.TK_QMARK, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                Parser parser = Parser.Instance();
                return parser.Ternary(left);
            }

        }, Precedence.TERNARY));


        rules.put(TokenType.TK_COLON, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    return null;
                }
            }, new ParseRule.Led() {
            Expression exec(Expression left) {
                return null;
            }

        }, Precedence.NONE));

        rules.put(TokenType.TK_NUMBER, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    Parser parser = Parser.Instance();
                    return parser.Number();
                }
            }, new ParseRule.Led() {
                Expression exec(Expression left) {
                    return null;
                }
        }, Precedence.PRIMARY));

        rules.put(TokenType.TK_BOOL, new ParseRule(
                new ParseRule.Nud() {
                    Expression exec() {
                        Parser parser = Parser.Instance();
                        return parser.Boolean();
                    }
                }, new ParseRule.Led() {
                    Expression exec(Expression left) {
                        return null;
                }
        }, Precedence.PRIMARY));

        rules.put(TokenType.TK_IDENTIFIER, new ParseRule(
            new ParseRule.Nud() {
                Expression exec() {
                    Parser parser = Parser.Instance();
                        return parser.Identifier();
                }
            }, new ParseRule.Led() {
                Expression exec(Expression left) {
                    return null;
                }
        }, Precedence.PRIMARY));

        rules.put(TokenType.TK_KEYWORDS, new ParseRule(
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

    Expression.Identifier Identifier() {
        symbolTable.checkExistence(previous);

        return new Expression.Identifier(previous.str);
    }

    Expression.Number Number() {
        //Expression expr = this.expressionGrammar();
        //this.previous = this.tokens.remove(0);

        //return new Expression.Number(Integer.parseInt(this.tokens.get(0).str));
        return new Expression.Number(Integer.parseInt(previous.str));
    }

    Expression.Boolean Boolean() {
        //Expression expr = this.expressionGrammar();
        //this.previous = this.tokens.remove(0);

        //return new Expression.Number(Integer.parseInt(this.tokens.get(0).str));
        return new Expression.Boolean(previous.str);
    }

    Expression.Unary Unary() {
        Token operator = this.previous;
        Expression expr = ParsePrecedence(Precedence.UNARY);
        return new Expression.Unary(expr, operator);
    }

    Expression.Unary UnaryPost(Expression left) {
        Token operator = this.previous;
        return new Expression.Unary(left, operator);
    }

    Expression.Binary Binary(Expression left) {
        Token operator = this.previous;

        ParseRule rule = this.rules.get(operator.tokenType);
        int precedence = rule.precedence.ordinal() + 1;
        Expression expr = ParsePrecedence(Precedence.values()[precedence]);

        return new Expression.Binary(left, operator, expr);
    }

    Expression.Ternary Ternary(Expression left) {

        Token operator = this.previous;

        ParseRule rule = this.rules.get(operator.tokenType);
        int precedence = rule.precedence.ordinal() + 1;
        Expression onTrue = ParsePrecedence(Precedence.values()[precedence]);

        Token colon = this.tokens.remove(0);
        this.previous = colon;

        rule = this.rules.get(colon.tokenType);
        precedence = rule.precedence.ordinal() + 1;
        Expression onFalse = ParsePrecedence(Precedence.values()[precedence]);

        return new Expression.Ternary(left, onTrue, onFalse);
    }

    Expression ParsePrecedence(Precedence prec) {
        Expression left = null;

        previous = tokens.remove(0);

        ParseRule rule = rules.get(previous.tokenType);

        if (rule.nud == null) {

            /*
             * If this happens, we were expecting
             * to encounter an expression.
             * Raise an exception / handle the error
             */
            return null;
        }

        left = rule.nud.exec();

        /*
         * I'm relatively certain that this loop is set up correctly
         * as the implementation of compareTo subtracts the other
         * operand from the calling object. We are looking for
         * while(prec <= rules.get(...).precedence)
         */
        int i = prec.compareTo(rules.get(tokens.get(0).tokenType).precedence);
        while (prec.compareTo(rules.get(tokens.get(0).tokenType).precedence) <= 0) {

            previous = tokens.remove(0);
            left = rules.get(previous.tokenType).led.exec(left);
        }

        return left;
    }

    Expression expressionGrammar() {

        return ParsePrecedence(Precedence.ASSIGNMENT);
    }

    Statement statementGrammar() {

        previous = tokens.get(0);
        Statement statement = null;

        if (previous.tokenType == TokenType.TK_LBRACE) {
            // compound/block statement
            statement = new Statement.Block();

            // new block means new scope unless it's a function
            if (!symbolTable.isParent()) {
                symbolTable.addSymbolTable();
            }

            tokens.remove(0);
            while (tokens.get(0).tokenType != TokenType.TK_RBRACE) {

                // has to either be a statement or a declaration
                if (tokens.get(0).tokenType == TokenType.TK_TYPE) {
                    Declaration dec = declarationGrammar();
                    ((Statement.Block) statement).addDeclaration(dec);

                } else {
                    // add a normal for-loop
                    ((Statement.Block) statement).addStatement((Statement) statementGrammar());

                }
            }

            // need to specify scope
            // currently adds symbol table as child to main symbol table
            // may need to add the current table as a parameter to the statementGrammar() method

            // removes right brace
            tokens.remove(0);

            symbolTable.removeSymbolTable();
        } else if (previous.tokenType == TokenType.TK_KEYWORDS) {
            String keywordIndicator = previous.str.toLowerCase();

            switch (keywordIndicator) {
                case "else":
                    // falls through
                case "if":
                    boolean selectFlag = true;
                    statement = new Statement.Conditional();

                    while (selectFlag) {
                        // remove keyword
                        tokens.remove(0);

                        if (tokens.get(0).str.equals("if")) {
                            tokens.remove(0);
                        }

                        // remove token, it should be a left parenthesis
                        if (tokens.remove(0).tokenType != TokenType.TK_LPAREN) {
                            System.out.println("Malformed if-else!");
                            exit(1);
                        }

                        // populate the expression with the contents parenthesis
                        Expression condition = expressionGrammar();

                        // remove remaining parenthesis
                        tokens.remove(0);

                        // use a block statement for the next section
                        Statement.Block body = (Statement.Block) statementGrammar();

                        statement.addChild(condition);
                        statement.addChild(body);

                        if (!tokens.get(0).str.equals("else")) {
                            selectFlag = false;
                        } else {
                            tokens.remove(0);
                            if (!tokens.get(0).str.equals("if")) {
                                statement.addChild(statementGrammar());
                                selectFlag = false;
                            }
                        }
                    }

                    break;
                case "for":
                    // handle the for-loop here so if there happens to be a declaration, the declaration can be added to the parent scope
                    // but we can use the same structure as a while-loop

                    // remove keyword
                    tokens.remove(0);

                    // will remove the first parenthesis
                    // remove token, it should be a left parenthesis
                    if (tokens.remove(0).tokenType != TokenType.TK_LPAREN) {
                        System.err.println("ERROR: Malformed for-loop() " + tokens.get(0).tokError());
                        exit(1);
                    }

                    symbolTable.addSymbolTable(true);

                    Declaration dec = null;
                    // if the token is a type on the first loop, it's a declaration
                    if (tokens.get(0).tokenType == TokenType.TK_TYPE) {
                        dec = declarationGrammar();
                    } else if (tokens.get(0).tokenType != TokenType.TK_SEMICOLON) {
                    // expression statement in place of a declaration (in theory)
                        ((Statement.Block) statement).addStatement(statementGrammar());
                    } else {
                        // only possible thing left would be a semicolon (which would normally be consumed in above conditions)
                        tokens.remove(0);
                    }

                    Expression expr = null;
                    if (tokens.get(0).tokenType != TokenType.TK_SEMICOLON) {
                        expr = expressionGrammar();
                    }

                    // remove the semicolon
                    tokens.remove(0);

                    Statement increment = null;
                    if (tokens.get(0).tokenType != TokenType.TK_RPAREN) {
                        increment = statementGrammar();
                    } else {
                        tokens.remove(0);
                    }

                    // recursively call to build block of for-loop
                    Statement forLoopBlock = statementGrammar();

                    // add the increment, if it exists (handled internally)
                    forLoopBlock.addChild(increment);

                    // create new statement with declaration, expression, and for-loop block
                    statement = new Statement.Iteration(dec, expr, (Statement.Block) forLoopBlock, "for");
                    break;
            case "while":
                    // remove keyword
                    tokens.remove(0);

                    // remove token, it should be a left parenthesis
                    if (tokens.remove(0).tokenType != TokenType.TK_LPAREN) {
                        System.err.println("ERROR: Malformed while-loop() " + tokens.get(0).tokError());
                        exit(1);
                    }

                    // populate the expression with the contents parenthesis
                    Expression condition = expressionGrammar();

                    // remove remaining parenthesis
                    tokens.remove(0);

                    // use a block statement for the next section
                    Statement.Block body = (Statement.Block) statementGrammar();

                    statement = new Statement.Iteration(condition, body, keywordIndicator);

                    break;
                case "return":
                    // return statement
                    statement = new Statement.Return();

                    // remove return
                    tokens.remove(0);

                    // in the event that the next token after the return is not a semicolon,
                    // then it is an expression
                    if (tokens.get(0).tokenType != TokenType.TK_SEMICOLON) {
                        ((Statement.Return) statement).setExpression(expressionGrammar());
                    }

                    // remove semicolon
                    tokens.remove(0);
                    previous = tokens.get(0);
                    break;
                case "break":
                    // break statement is empty
                    statement = new Statement.Break();

                    // get rid of "break"
                    tokens.remove(0);
                    // get rid of the ';' token.
                    tokens.remove(0);

                    break;
                case "goto":
                    // remove "goto"
                    tokens.remove(0);

                    // populate the tree object with the name of the label
                    statement = new Statement.gotoStatement(tokens.remove(0));

                    // remove the semicolon
                    tokens.remove(0);
                    break;
                default:
                    System.out.println("ERROR: unrecognized " + tokens.get(0).tokError());
                    exit(1);
            }

        } else {
            // expression statement

            // goto label
            if (tokens.get(1).tokenType == TokenType.TK_COLON) {
                // remove label
                statement = new Statement.gotoLabel(tokens.remove(0));

                // check to make sure not at the end of a block
                if (tokens.get(1).tokenType == TokenType.TK_RPAREN) {
                    System.err.println("ERROR: goto label precedes empty statement " + tokens.get(0).tokError());
                }
            } else if (previous.tokenType != TokenType.TK_SEMICOLON) {
                // if the next token is not a semicolon, then it is an expression
                statement = new Statement.ExpressionStatement(expressionGrammar());

            }

            // remove remaining semicolon/colon
            tokens.remove(0);
        }

        return statement;
    }

    /**
     * Check for either a variable declaration or a function declaration
     *
     * @return
     */
    Declaration declarationGrammar() {
        Token typeSpec;
        Token decID;

        // check for error
        if (tokens.get(0).tokenType != TokenType.TK_TYPE && tokens.get(1).tokenType != TokenType.TK_IDENTIFIER) {
            if (tokens.get(0).tokenType != TokenType.TK_KEYWORDS && tokens.get(1).tokenType != TokenType.TK_KEYWORDS) {
                System.err.println("declarationGrammar(): TYPE or TOKEN that does not follow the grammar " + tokens.get(0).tokError());
                exit(1);
            }
        }

        // remove the specified element, and store it into the attributes
        typeSpec = tokens.remove(0);
        decID = tokens.remove(0);
        previous = tokens.remove(0);

        // check to see if a parenthesis exists (means a function declaration)
        if (previous.tokenType == TokenType.TK_LPAREN) {

            // Add function to map of functions (will do internal checking for dup).
            symbolTable.addFun(decID);
            symbolTable.addSymbolTable(true);

            Declaration.funDeclaration funDec = new Declaration.funDeclaration(typeSpec, decID);

            while (tokens.get(0).tokenType != TokenType.TK_RPAREN) {

                if (funDec.getParamSize() > 6) {
                    System.err.println("ERROR: Unsupported number of parameters in function " + decID.str);
                    exit(1);
                }

                Token type = tokens.remove(0);

                if (type.tokenType != TokenType.TK_TYPE && tokens.get(0).tokenType != TokenType.TK_IDENTIFIER) {
                    tokens.get(0).tokError();
                    exit(1);
                }

                Token paramID = tokens.remove(0);

                // create a parameter, and add it to the list
                Declaration.Parameter parameter = new Declaration.Parameter(type, paramID);
                symbolTable.addSymbol(type, paramID);

                funDec.addParameter(parameter);

                // check to see if next token is a comma, if it is, then continue the loop.
                if (tokens.get(0).tokenType != TokenType.TK_COMMA) {
                    break;
                }

                tokens.remove(0);
            }

            // remove the right parenthesis
            tokens.remove(0);

            funDec.addStatement(statementGrammar());

            return funDec;
        }

        /**
         * Example of enum w/ typedef
         * typedef enum {Sun, Mon, Tue} dow_type;
         * dow_type today = Sun;
         *
         * W/o typedef
         * enum strategy {RANDOM, IMMEDIATE, SEARCH};
         * enum strategy my_strategy = IMMEDIATE;
         */
        else if (previous.tokenType == TokenType.TK_LBRACE) {

            Declaration.TypeDeclaration enumerationDec = new Declaration.TypeDeclaration();
            Declaration.varDeclaration varDecEnum = new Declaration.varDeclaration();
            Token Int = new Token("int", TokenType.TK_TYPE);

            enumVal.str = "0"; // Reset enumVal to 0 if we have multiple enums.

            /* This section handles typedef */
            if (typeSpec.str.equals("typedef") && tokens.get(0).tokenType != TokenType.TK_RBRACE) {

                decID = tokens.remove(0); // get first element of enum

                // If we have our first enum value as varDecInit, we need to set prev to '=' initially.
                if (tokens.get(0).tokenType == TokenType.TK_EQUALS)
                    previous = tokens.get(0);

                do {
                    if (previous.tokenType == TokenType.TK_EQUALS) {
                        varDecEnum = varDecInit(varDecEnum, Int, decID); // Init our var with the correct value.

                        if (tokens.size() > 1 && tokens.get(1).tokenType == TokenType.TK_EQUALS) {
                            previous = tokens.get(1);
                        } else {
                            previous = tokens.get(0);
                        }

                        decID = tokens.get(0);

                    } else {
                        // Technically the user is not initializing a var but under the hood we need to.
                        varDecEnum = varDecNoInit(varDecEnum, Int, decID, true);

                        // varDecEnum = varDecInit(varDecEnum, Int, decID);

                        if (tokens.size() > 1 && tokens.get(1).tokenType == TokenType.TK_EQUALS) {
                            previous = tokens.get(1);
                        }
                        if (tokens.get(0).tokenType != TokenType.TK_RBRACE)
                            decID = tokens.get(0);
                    }

                    // Increment enumVal to add to net variable if un assigned.
                    enumVal.str = Integer.toString(Integer.parseInt(enumVal.str) + 1);
                } while (tokens.get(0).tokenType != TokenType.TK_RBRACE);

                /* Add all of the enum vars to tree. */
                enumerationDec.addEnumVars(varDecEnum);

                tokens.remove(0); // remove RBRACE
                enumerationDec.enumType = tokens.remove(0); // Get Enum type

                enumerationDec.enumID = new Token("enumName");
                ; // TEMPORARY SINCE WE DO NOT KNOW ID NAME YET.
                tokens.remove(0); // remove ';'

            } else if (typeSpec.str.equals("enum")) {
                // Non typedef enum

            } else {
                if (tokens.get(0).tokenType == TokenType.TK_RBRACE)
                    System.err.println("ERROR: declarationGrammar(): enum body cannot be empty..." + tokens.get(0).tokError());
                exit(1);
            }

            return enumerationDec;
        }

        // Made it to this point, must be a variable declaration
        // Loop through tokens until we see a semi colon. This is to handle
        // the case where we have multiple variables defined on the same line.
        // Add the first varID as the first variable.

        // Consume everything until the comma to get the potential var value.
        // Paired programmed with Garrett Bates
        else {

            Declaration.varDeclaration varDeclaration = new Declaration.varDeclaration();

            do {
                if (previous.tokenType == TokenType.TK_EQUALS) {
                    varDeclaration = varDecInit(varDeclaration, typeSpec, decID); // Init our var with the correct value.

                    symbolTable.addSymbol(varDeclaration);

                    if (tokens.size() > 1 && tokens.get(1).tokenType == TokenType.TK_EQUALS) {
                        previous = tokens.get(1);
                    } else {
                        previous = tokens.get(0);
                    }

                    decID = tokens.get(0);

                } else {
                    varDeclaration = varDecNoInit(varDeclaration, typeSpec, decID, false);
                    symbolTable.addSymbol(typeSpec, decID);

                    if (previous.tokenType == TokenType.TK_SEMICOLON) {
                        return varDeclaration;
                    }

                    if (tokens.size() > 0) {
                        if (tokens.size() > 1 && tokens.get(1).tokenType == TokenType.TK_EQUALS) {
                            previous = tokens.get(1);
                        }

                        decID = tokens.get(0);

                        // if in the case the declaration ID happens to be a semicolon, do nothing and return immediately
                        // applicable for the for-loop variable declaration
                        if (decID.tokenType == TokenType.TK_SEMICOLON) {
                            tokens.remove(0);
                            return varDeclaration;
                        }
                    }
                }

                // Need this since in the case 'int a;' previous == ;
                // Which means if this is a condition in while() then
                // we will never make it inside of here.
                if (previous.tokenType == TokenType.TK_SEMICOLON) break;
            } while (tokens.get(0).tokenType != TokenType.TK_SEMICOLON);

            // remove semicolon
            if (tokens.size() > 0 && (previous.tokenType != TokenType.TK_SEMICOLON ||
                tokens.get(0).tokenType == TokenType.TK_SEMICOLON))
                tokens.remove(0);


            return varDeclaration;
        }
    }

    public Declaration.varDeclaration varDecInit(Declaration.varDeclaration varDeclaration, Token typeSpec, Token decID) {

        // Used for regular var init
        if (tokens.get(1).tokenType == TokenType.TK_EQUALS) {
            tokens.remove(0); // remove ID
            tokens.remove(0); // remove '='

        } else if (tokens.get(0).tokenType == TokenType.TK_EQUALS) {
            tokens.remove(0);
        }

        enumVal = tokens.get(0); // Update the value as other enums can have assignments.

        varDeclaration.addVarDec(typeSpec, decID, expressionGrammar());
        if (tokens.get(0).tokenType == TokenType.TK_COMMA) {
            //remove ','
            tokens.remove(0);
        }

        return varDeclaration;
    }
    public Declaration.varDeclaration varDecNoInit(Declaration.varDeclaration varDeclaration, Token typeSpec, Token decID, boolean inEnum) {

        // We set decID outside after first pass so we need to get rid of the redundant token.
        if (varDeclaration.hasChildren())
            tokens.remove(0);

        // For enums that have no init value.
        if (inEnum) {
            previous = enumVal;
            varDeclaration.addVarDec(typeSpec, decID, Number());

        } else
            varDeclaration.addVarDec(typeSpec, decID);

        if (tokens.size() > 0 && tokens.get(0).tokenType == TokenType.TK_COMMA) {
            // remove ','
            tokens.remove(0);
        }

        return varDeclaration;
    }

    /**
     * Program calls upon Declaration(), looking for declarations until the token list is empty
     *
     * @return Node containing the head to the Abstract Syntax
     */
    Node programGrammar(String fileName) {
        // initialize a new list of declarations
        Program headNode = new Program(fileName);
        symbolTable.addSymbolTable();
        Declaration dec;
        try {
            // run until tokens list is empty
            while (!tokens.isEmpty()) {
                dec = declarationGrammar();
                headNode.addDeclaration(dec);
            }
        } catch (NullPointerException e) {
            System.err.println("ERROR: Malformed code " + tokens.get(0).tokError());
            exit(1);
        }


        symbolTable.removeSymbolTable();
        return headNode;
    }

    public Node Parse(ArrayList<Token> tokens, String fileName) {
        this.tokens = tokens;

        return programGrammar(fileName);
    }

    public String getTable() {
        return symbolTable.symbolString.toString();
    }
}

