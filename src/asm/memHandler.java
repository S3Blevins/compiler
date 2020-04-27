package asm;

import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;

import java.util.*;

import static java.lang.System.exit;
import static java.lang.System.setOut;

public class memHandler {

    // reference counter
    HashMap<String, Integer> refCounter;

    HashMap<RegName, String> registers;
    // key: register
    // value: variable

    HashMap<String, String> stack;
    // key: variable
    // value: position in stack

    /**
     * Init the registers the compiler will keep track of to
     * convert our IR to assembly.
     */
    public memHandler() {
        this.stack = new HashMap<>();
        this.registers = new HashMap<>();

        // Add all of the registers defined in RegisterName to the array.
        for (int i = 0; i < RegName.values().length; i++) {
            registers.put(RegName.values()[i], "");
        }

        /*
            Ascii art of how the registers are represented (DELETE IF OR WHEN NEEDED).
                       -----------------------
               rax     |   INTEGER/ADDRESS   |                      register[0]
                       -----------------------

                       -----------------------
               rbx     |   INTEGER/ADDRESS   |                      register[1]
                       -----------------------
                .       .       .       .
                .       .       .       .
                .       .       .       .
                .       .       .       .
                       -----------------------
               r15     |   INTEGER/ADDRESS   |                      register[15]
                       -----------------------
         */
    }

    /* --- STACK METHODS --- */

    // index is starting place in IR for the function
    public void newScope(List<IRExpression> irList, int index) {
        this.stack = new HashMap<>();

        this.refCounter = new HashMap<>();

        index++; // skip over function instruction
        while(index < irList.size() && irList.get(index).inst != Instruction.FUNC) {
            if(irList.get(index).sources != null) {

                int i = 0;
                if (irList.get(index).inst == Instruction.CALL) {
                    i = 1;
                }

                for (; i < irList.get(index).sources.size(); i++) {
                    Token tok = irList.get(index).sources.get(i);
                    if(tok.tokenType != TokenType.TK_IDENTIFIER) {
                        continue;
                    }
                    // if the element already exists, then increment the counter
                    // otherwise add it
                    if (refCounter.containsKey(tok.str)) {
                        refCounter.put(tok.str, refCounter.get(tok.str) + 1);
                    } else {
                        refCounter.put(tok.str, 1);
                    }
                }
            }

            Token dest = irList.get(index).dest;
            if(dest != null) {
                if (refCounter.containsKey(dest.str)) {
                    refCounter.put(dest.str, refCounter.get(dest.str) + 1);
                } else {
                    refCounter.put(dest.str, 1);
                }
            }

            // increment the loop
            index++;
        }
    }

    // add variable and its place in memory
    public void addStackVar(Token variable, String place) {
        this.stack.put(variable.str, place);
    }

    // enter place in memory, get variable
    public String getVar(String place) {
        for(Map.Entry<String, String> set: stack.entrySet()) {
            if(set.getValue().equals(place)) {
                return set.getKey();
            }
        }
        return null;
    }

    // enter variable, get place in memory
    public String getVarMemory(Token tok) {
        return stack.get(tok.str);
    }

    /* --- REGISTER METHODS --- */

    // get register name as String
    public String getRegName(int index) {
        return String.valueOf(RegName.values()[index]);
    }

    // get register name as RegName
    public RegName getReg(int index) {
        return RegName.values()[index];
    }

    // add variable and it's respective register
    public void addRegVar(RegName reg, Token variable) {
        registers.put(reg, variable.str);
    }

    // enter register, get variable saved in register
    public String getVar(RegName reg) {
        return "%" + registers.get(reg);
    }

    // enter variable, get register its saved in
    public RegName getVarRegister(Token variable) {
        for(Map.Entry<RegName, String> set: registers.entrySet()) {
            if(set.getValue().equals(variable.str)) {
                return set.getKey();
            }
        }
        return null;
    }

    /* --- MEMORY METHODS -- */
    public String location(Token variable) {
        String tmp = getVarMemory(variable);
        if(tmp == null) {
            try {
                return "%" + getVarRegister(variable).toString();
            } catch (NullPointerException e) {
                System.err.println("ERROR: variable " + variable.str + " not found in register");
                exit(1);
            }
        }

        return tmp;
    }
}

