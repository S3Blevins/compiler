package asm;

import lexer.Token;

public class varTable {
        // value of the variable
        int value;
        // index of where the variable was initialized
        int index;

        public varTable(Token value, int index) {
                this.value = Integer.parseInt(value.str);
                this.index = index;
        }

        public int getValue() {
               return value;
        }

        public int getIndex() {
                return index;
        }
}
