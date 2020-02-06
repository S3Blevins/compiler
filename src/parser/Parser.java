package parser;

import java.util.ArrayList;

import lexer.Token;
import lexer.TokenType;

import parser.Expression;

public class Parser {

        static Token previous;
        static Token current;
        static ArrayList<Token> tokens;

        public static Expression parse(ArrayList<Token> tokens) {

                Parser.tokens = tokens;
                Parser.current = tokens.remove(0);
                Parser.previous = Parser.current;
                
                return null;
        }
}

