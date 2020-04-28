package asm;

import ir.IRExpression;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;

import java.util.*;

import static java.lang.System.exit;

public class memHandler {

    // reference counter
    private HashMap<String, Integer> refCounter;

    public ArrayList<memContent> registers;
    public Integer regIndex;
    public Stack<ArrayList<memContent>> stack;
    public Integer memIndex;

    public memHandler() {
        this.registers = new ArrayList<>();

        this.stack = new Stack<>();
        this.stack.push(new ArrayList<>());

        for(Register reg: Register.values()) {
            this.registers.add(new memContent(reg.name(), ""));
        }
    }

    // index is starting place in IR for the function
    public void newScope(List<IRExpression> irList, int index) {
        this.stack.push(new ArrayList<>());
        this.refCounter = new HashMap<>();
        this.regIndex = 0;
        this.memIndex = 0;

        index++; // skip over function instruction
        // run loop for all IR Expressions in the function to populat reference count table
        while(index < irList.size() && irList.get(index).inst != Instruction.FUNC) {
            if(irList.get(index).sources != null) {

                // start at zero
                int i = 0;
                // if the instruction is a call, skip the first source because it's the function name
                if (irList.get(index).inst == Instruction.CALL) {
                    i = 1;
                }

                for (; i < irList.get(index).sources.size(); i++) {
                    Token tok = irList.get(index).sources.get(i);

                    // if the source is not an identifier, then skip to the next one
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

    /* ------------------------------------------------- */

    private void removeReference(Token var, int location, boolean memType) {
        // take current references and remove one
        int curCount = refCounter.get(var.str) - 1;
        refCounter.put(var.str, curCount);

        // if the references hit zero and stored in a register, then unlock it for future use
        if(curCount == 0) {

            // true = register
            // false = stack

            // unlock register or memory for future use
            if(memType) {
                registers.get(location).setLock(false);
            } else {
                stack.peek().get(location).setLock(false);
            }

        }
    }

    /* ------------------------------------------------- */

    // add variable and respective place in memory
    public String addVarToMem(Token var) {
        this.stack.peek().add(new memContent(memIndex.toString(), var.str));
        this.stack.peek().get(memIndex).setLock(true);
        // false is stack based memory
        removeReference(var, memIndex, false);
        memIndex = this.stack.peek().size();

        return "-" + ((memIndex) * 4) + "(%rbp)";
    }

    // add variable to next available register
    public memContent addVarToReg(Token var) {
        // we're playing round-robin with the registers until we find one that's open
        int start = regIndex;

        // loop around until we find a valid register, otherwise put the variable into memory
        while(registers.get(regIndex).lock) {
            // go to the next register
            regIndex++;

            // check if the register count needs to loop around
            if(regIndex > registers.size()) {
                regIndex = 0;
            }

            // if we end up where regIndex = start, then we need to store the variable in a register and move the
            // current value retained in the register into memory - if the moved value needs to be in a register in the future,
            // the value will be moved accordingly
            if(regIndex == start) {
                addVarToMem(var);
            }
        }

        // if we end up here, we put the variable in a register
        registers.get(regIndex).setVar(var.str);

        removeReference(var, regIndex, true);

        return registers.get(regIndex);
    }

    // NOTE - some registers are overwritten regardless of lock
    // %rax when returning
    // %rax when after a call (see above)
    // %rax after division
    public void addVarToReg(Register reg, Token var) {
        int index = reg.ordinal();
        // if the explicitly requested register is locked,
        // relocate the contents.
        if(registers.get(index).lock) {
            addVarToReg(new Token (registers.get(index).var, TokenType.TK_IDENTIFIER));
        }

        // place content into specifically requested register
        registers.get(index).setVar(var.str);

        if(refCounter.get(var.str) != 0) {
            registers.get(index).setLock(true);
        }

        removeReference(var, index, true);
    }

    /* ------------------------------------------------- */

    // get variable based on memory location
    public memContent getVar(int pos) {
        return this.stack.peek().get(pos);
    }

    // get variable based on register
    public memContent getVar(Register reg) {
        return registers.get(reg.ordinal());
    }

    /* ------------------------------------------------- */

    // get memory location based on variable saved in it
    public memContent getMemory(Token var) {
        ArrayList<memContent> tmpMem = this.stack.peek();
        for(int i = 0; i < tmpMem.size(); i++) {
            if(tmpMem.get(i).var.equals(var.str)) {
                removeReference(var, i, false);
                return tmpMem.get(i);
            }
        }
        return null;
    }

    // get register from index
    public memContent getRegister(int index) {

        return registers.get(index);
    }

    // get register based on variable saved in it
    public memContent getRegister(Token var) {
        for(int i = 0; i < registers.size(); i++) {
            if(registers.get(i).var.equals(var.str)) {
                removeReference(var, i, true);
                return registers.get(i);
            }
        }

        return null;
    }

    public memContent getVarLocation(Token var) {
        // check memory first, and then check registers.
        memContent mem = getMemory(var);
        if(mem == null) {
            try {
                return getRegister(var);
            } catch (NullPointerException e) {
                System.err.println("ERROR: variable " + var.str + " not found in register");
                exit(1);
            }
        }

        return mem;
    }
}

