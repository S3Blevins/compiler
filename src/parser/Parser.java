package parser;

import lexer.Token;
import lexer.TokenType;
import parser.treeObjects.*;

import java.util.ArrayList;
import java.util.HashMap;

import static java.lang.System.exit;

public class Parser {

        private Token previous;
        private ArrayList<Token> tokens;
        private HashMap<TokenType, ParseRule> rules;
        private SymbolTable table;
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
                                        Expression expr = parser.expressionGrammar();
                                        parser.previous = parser.tokens.remove(0);
                                        return new Expression.Group(expr);
                                }
                        }, new ParseRule.Led() {
                                Expression exec(Expression left) {
                                        return null;
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
                        }, Precedence.UNARY));

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

        // singleton pattern only has one instance of the object
        public static Parser Instance() {
                if(Parser.instance == null) {
                        Parser.instance = new Parser();
                }

                return Parser.instance;
        }

        Expression.Identifier Identifier() {

                return new Expression.Identifier(previous.str);
        }

        Expression.Number Number() {
                //Expression expr = this.expressionGrammar();
                //this.previous = this.tokens.remove(0);

                //return new Expression.Number(Integer.parseInt(this.tokens.get(0).str));
                return new Expression.Number(Integer.parseInt(previous.str));
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

                if(rule.nud == null) {

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
                while(prec.compareTo(rules.get(tokens.get(0).tokenType).precedence) <= 0) {

                        previous = tokens.remove(0);
                        left = rules.get(previous.tokenType).led.exec(left);
                }

                return left;
        }

        Expression expressionGrammar() {

                return ParsePrecedence(Precedence.ASSIGNMENT);
        }

        Statement statementGrammar(SymbolTable parentTable) {
                //System.out.println("Statement()");

                previous = tokens.get(0);
                Statement statement = null;

                if(previous.tokenType == TokenType.TK_LBRACE) {
                        // compound/block statement
                        statement = new Statement.Block();
                        tokens.remove(0);
                        while(tokens.get(0).tokenType != TokenType.TK_RBRACE) {

                                // has to either be a statement or a declaration
                                if(tokens.get(0).tokenType == TokenType.TK_TYPE) {
                                        Declaration dec = declarationGrammar(parentTable);
                                        ((Statement.Block) statement).addDeclaration(dec);
                                        parentTable.addSymbol((Declaration.varDeclaration) dec);
                                } else {
                                        ((Statement.Block) statement).addStatement((Statement) statementGrammar(parentTable));
                                }
                        }

                        // need to specify scope
                        // currently adds symbol table as child to main symbol table
                        // may need to add the current table as a parameter to the statementGrammar() method

                        // removes right brace
                        tokens.remove(0);

                } else if(previous.tokenType == TokenType.TK_KEYWORDS) {
                        String keywordIndicator = previous.str.toLowerCase();

                        switch(keywordIndicator) {
                                case "else":

                                case "if":
                                        boolean selectFlag = true;
                                        statement = new Statement.Selection();

                                        while(selectFlag) {
                                                // remove keyword
                                                tokens.remove(0);

                                                if(tokens.get(0).str.equals("if")) {
                                                        tokens.remove(0);
                                                }

                                                // remove token, it should be a left parenthesis
                                                if (tokens.remove(0).tokenType != TokenType.TK_LPAREN) {
                                                        System.out.println("Malformed statement()");
                                                        exit(1);
                                                }

                                                // populate the expression with the contents parenthesis
                                                Expression condition = expressionGrammar();

                                                // remove remaining parenthesis
                                                tokens.remove(0);

                                                // use a block statement for the next section
                                                SymbolTable childTable = new SymbolTable();
                                                Statement.Block body = (Statement.Block) statementGrammar(childTable);

                                                statement.addChild(condition);
                                                statement.addChild(body);

                                                if(!tokens.get(0).str.equals("else")) {
                                                        selectFlag = false;
                                                } else {
                                                        tokens.remove(0);
                                                        if(!tokens.get(0).str.equals("if")) {
                                                                statement.addChild(statementGrammar(childTable));
                                                                selectFlag = false;
                                                        }
                                                }

                                                parentTable.addChildTable(childTable);
                                        }

                                        break;
                                case "while":

                                        // remove while keyword
                                        tokens.remove(0);

                                        // remove token, it should be a left parenthesis
                                        if(tokens.remove(0).tokenType != TokenType.TK_LPAREN) {
                                                System.out.println("Malformed statement()");
                                                exit(1);
                                        }

                                        // populate the expression with the contents parenthesis
                                        Expression condition = expressionGrammar();

                                        // remove remaining parenthesis
                                        tokens.remove(0);

                                        SymbolTable childTable = new SymbolTable();
                                        // use a block statement for the next section
                                        Statement.Block body = (Statement.Block) statementGrammar(childTable);

                                        statement = new Statement.Iteration(condition, body);

                                        parentTable.addChildTable(childTable);
                                        break;
                                case "return":
                                        // return statement
                                        statement = new Statement.Return();

                                        // remove return
                                        tokens.remove(0);

                                        // in the event that the next token after the return is not a semicolon,
                                        // then it is an expression
                                        if(tokens.get(0).tokenType != TokenType.TK_SEMICOLON) {
                                                ((Statement.Return) statement).setExpression(expressionGrammar());
                                        }

                                        // remove semicolon
                                        tokens.remove(0);
                                        previous = tokens.get(0);
                                        break;
                                case "break":
                                        // break statement is empty
                                        statement = new Statement.Break();

                                        // get rid of break
                                        tokens.remove(0);
                                        // get rid of the ';' token.
                                        tokens.remove(0);

                                        break;
                                default:
                                        System.out.println("Invalid statement(): " + keywordIndicator);
                                        exit(1);
                        }

                } else {
                        // expression statement

                        // if the next token is not a semicolon, then it is an expression
                        if(previous.tokenType != TokenType.TK_SEMICOLON) {
                                //TODO: implement

                                statement = new Statement.ExpressionStatement(expressionGrammar());
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
        Declaration declarationGrammar(SymbolTable parentTable) {
                Token typeSpec;
                Token decID;

                // check for error
                if(tokens.get(0).tokenType != TokenType.TK_TYPE && tokens.get(1).tokenType != TokenType.TK_IDENTIFIER) {
                        System.err.println("declarationGrammar(): There was a type or TOKEN that does not follow the grammar.");
                        exit(1);
                }

                // remove the specified element, and store it into the attributes
                typeSpec = tokens.remove(0);
                decID = tokens.remove(0);
                previous = tokens.remove(0);

                // check to see if a parenthesis exists (means a function declaration)
                if(previous.tokenType == TokenType.TK_LPAREN) {
                        SymbolTable childTable = new SymbolTable();

                        Declaration.funDeclaration funDec = new Declaration.funDeclaration(typeSpec, decID);

                        while(tokens.get(0).tokenType != TokenType.TK_RPAREN) {

                                if(funDec.getParamSize() > 8) {
                                        System.err.println("Too many parameters in list!");
                                        exit(1);
                                }

                                Token type = tokens.remove(0);

                                if(type.tokenType != TokenType.TK_TYPE && tokens.get(0).tokenType != TokenType.TK_IDENTIFIER) {
                                        System.err.println("Function Declaration ERROR! : There was a type or TOKEN that does not follow the grammar.");
                                        exit(1);
                                }

                                Token paramID = tokens.remove(0);

                                // create a parameter, and add it to the list
                                Declaration.Parameter parameter = new Declaration.Parameter(type, paramID);
                                childTable.addSymbol(type, paramID);

                                funDec.addParameter(parameter);

                                // check to see if next token is a comma, if it is, then continue the loop.
                                if(tokens.get(0).tokenType != TokenType.TK_COMMA) {
                                        break;
                                }

                                tokens.remove(0);
                        }

                        // remove the right parenthesis
                        tokens.remove(0);

                        funDec.addStatement(statementGrammar(childTable));

                        parentTable.addChildTable(childTable);

                        return funDec;
                }

                // Made it to this point, must be a variable declaration
                // Loop through tokens until we see a semi colon. This is to handle
                // the case where we have multiple variables defined on the same line.
                // Add the first varID as the first variable.

                // Consume everything until the comma to get the potential var value.
                // Paired programmed with Garrett Bates

                Declaration.varDeclaration varDeclaration = new Declaration.varDeclaration();

                parentTable.addSymbol(typeSpec, decID);

                while (tokens.get(0).tokenType != TokenType.TK_SEMICOLON) {
                        if (previous.tokenType == TokenType.TK_EQUALS) {
                                varDeclaration = varDecInit(varDeclaration, typeSpec, decID); // Init our var with the correct value.

                                parentTable.addSymbol(varDeclaration);

                                if (tokens.size() > 1 && tokens.get(1).tokenType == TokenType.TK_EQUALS) {
                                        previous = tokens.get(1);
                                } else {
                                        previous = tokens.get(0);
                                }

                                decID = tokens.get(0);

                        } else {
                                varDeclaration = varDecNoInit(varDeclaration, typeSpec, decID);

                                parentTable.addSymbol(varDeclaration);

                                if (tokens.size() > 1 && tokens.get(1).tokenType == TokenType.TK_EQUALS) {
                                        previous = tokens.get(1);
                                }

                                decID = tokens.get(0);
                        }

                        // Need this since in the case 'int a;' previous == ;
                        // Which means if this is a condition in while() then
                        // we will never make it inside of here.
                        if (previous.tokenType == TokenType.TK_SEMICOLON) break;
                }

                // remove semicolon
                if (previous.tokenType != TokenType.TK_SEMICOLON || tokens.get(0).tokenType == TokenType.TK_SEMICOLON)
                        tokens.remove(0);


                return varDeclaration;
        }

        public Declaration.varDeclaration varDecInit(Declaration.varDeclaration varDeclaration, Token typeSpec, Token decID) {

                // Remove identifier and '='
                if (tokens.get(0).tokenType == TokenType.TK_IDENTIFIER) {
                        tokens.remove(0); // remove ID
                        tokens.remove(0); // remove '='
                } else if (tokens.get(0).tokenType == TokenType.TK_EQUALS){
                        tokens.remove(0);
                }

                varDeclaration.addVarDec(typeSpec, decID, expressionGrammar());
                if (tokens.get(0).tokenType == TokenType.TK_COMMA) {
                        //remove ','
                        tokens.remove(0);
                }

                return varDeclaration;
        }

        public Declaration.varDeclaration varDecNoInit(Declaration.varDeclaration varDeclaration, Token typeSpec, Token decID) {

                // If first init has already passed, successors should have ','s
                if (varDeclaration.hasChildren())
                        tokens.remove(0);

                varDeclaration.addVarDec(typeSpec, decID);

                if (tokens.get(0).tokenType == TokenType.TK_COMMA) {
                        // remove ','
                        tokens.remove(0);
                }

                return varDeclaration;
        }

        /**
         * Program calls upon Declaration(), looking for declarations until the token list is empty
         * @return Node containing the head to the Abstract Syntax
         */
        Node programGrammar(String fileName) {
                // initialize a new list of declarations
                Program headNode = new Program(fileName);
                Declaration dec;
                table = new SymbolTable();

                // run until tokens list is empty
                while(!tokens.isEmpty()) {
                        dec = declarationGrammar(table);
                        headNode.addDeclaration(dec);
/*
                        // add global variables to the symbol table
                        if(dec instanceof Declaration.varDeclaration) {
                                table.addSymbol((Declaration.varDeclaration) dec);
                               // System.out.println("There is a global variable");
                        }
*/
                }

                table.printTable(0);

                return headNode;
        }

        public Node Parse(ArrayList<Token> tokens, String fileName) {
                this.tokens = tokens;

                return programGrammar(fileName);
        }
}

