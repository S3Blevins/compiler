package asm;

import lexer.Token;

// used in the references of the variable in the optimizer
public class varTable {
        // value of the variable
        int value;
        // index of where the variable was initialized
        int index;
        // an indicator of how many times the variable was referenced and if it can be removed;
        boolean ref;

        public varTable(Token value, int index) {
                this.value = Integer.parseInt(value.str);
                this.index = index;
                this.ref = false;
        }

        public int getValue() {
               return value;
        }

        public int getIndex() {
                return index;
        }

        public void printVar() {
                System.out.println("\tvalue = " + this.value);
                System.out.println("\tindex = " + this.index);
                System.out.println("\treferences = " + this.ref);
        }
}
