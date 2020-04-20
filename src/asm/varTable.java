package asm;

import lexer.Token;

import java.util.HashMap;

public class varTable {
        HashMap<String, String> scope;

        public void newScope() {
                scope = new HashMap<>();
        }

        public void addVar(Token tok, String asm) {
                scope.put(tok.str, asm);
        }

        public String getLocal (Token tok) {
                return scope.get(tok.str);
        }
}
