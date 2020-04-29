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

    // moved the assembly expression into the memory handler in order to handle
    // adding intermediary expressions where the register is locked and needs to be moved
    public String asmExpr;

    public memHandler() {
        this.registers = new ArrayList<>();

        this.stack = new Stack<>();
        this.stack.push(new ArrayList<>());

        for(Register reg: Register.values()) {
            this.registers.add(new memContent(reg.name(), ""));
        }
    }

    public void newExpr() {
        asmExpr = "";
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
        System.out.println(refCounter.entrySet());
    }

    /* ------------------------------------------------- */

    private void removeReference(Token var, String location) {
        System.out.println("LOCATION: removeReference(Token, String)");
        try {
            Integer.parseInt(var.str);
            try {
                System.out.println("The memory location " + stack.peek().get(Integer.parseInt(location)).getName() + " holding the number " + var.str + " will be unlocked");
                stack.peek().get(Integer.parseInt(location)).setLock(false);
            } catch(NumberFormatException e) {
                System.out.println("The register " + registers.get(Register.valueOf(location).ordinal()).getName() + " holding the number " + var.str + " will be unlocked");
                registers.get(Register.valueOf(location).ordinal()).setLock(false);
            }
        } catch (NumberFormatException e) {
            System.out.print("The reference count for var " + var.str + " is " + refCounter.get(var.str));

            // take current references and remove one
            int curCount = refCounter.get(var.str) - 1;
            refCounter.put(var.str, curCount);

            System.out.println(" and will be reduced to " + curCount);
            // if the references hit zero and stored in a register, then unlock it for future use
            if(curCount == 0) {
                refCounter.remove(var.str);
                // unlock register or memory for future use
                try {
                    System.out.println("The memory location " + stack.peek().get(Integer.parseInt(location)).getName() + " holding the variable " + var.str + " will be unlocked");
                    stack.peek().get(Integer.parseInt(location)).setLock(false);
                } catch(NumberFormatException e2) {
                    System.out.println("The register " + registers.get(Register.valueOf(location).ordinal()).getName() + " holding the variable " + var.str + " will be unlocked");
                    registers.get(Register.valueOf(location).ordinal()).setLock(false);
                }
            }
        }
    }

    public void unlockParameters(int parameters) {
        // max of 6
        for(int i = 0; i < parameters; i++) {
            System.out.println("The register " + registers.get(i).getName() + " holding the parameter " + registers.get(i).var + " is being unlocked");
            registers.get(i).setLock(false);
        }
    }

    /* ------------------------------------------------- */

    public memContent nextMem(Token var) {
        this.stack.peek().add(new memContent(memIndex.toString(), var.str));
        this.stack.peek().get(memIndex).setLock(true);
        System.out.println("The variable " + var.str + " will be stored in memory location " + this.stack.peek().get(memIndex).getName());
        memIndex = this.stack.peek().size();

        return this.stack.peek().get(this.stack.peek().size() - 1);
    }

    // add variable and respective place in memory
    public memContent addVarToMem(Token var) {
        memContent tmp = nextMem(var);
        removeReference(var, memIndex.toString());

        return tmp;
    }

    public memContent nextAvailReg(Token var) {
        // we're playing round-robin with the registers until we find one that's open
        int i = 0;

        System.out.println("Adding the variable " + var.str + " in the next available register.");

        // Loop until we find a open register to store whatever we need.
        for (; i < Register.values().length; i++) {
            if (!registers.get(i).lock) {
                // once we find a open register, the i-th index
                // will be the where the var is stored to.
                break;
            }
        }

        // if we end up where regIndex = start, then we need to store the variable in a register and move the
        // current value retained in the register into memory - if the moved value needs to be in a register in the future,
        // the value will be moved accordingly
        if (i == Register.values().length) {
            System.out.println("All registers are full!");
            return nextMem(var);
        }

        // if we end up here, we put the variable in a register
        registers.get(i).setVar(var.str);
        System.out.println("The variable " + var.str + " is now held in the register " + registers.get(i).getName());

        // SET LOCK
        registers.get(i).setLock(true);
        System.out.println("The variable register " + registers.get(i).getName() + " is being locked");

        return registers.get(i);
    }

    // add variable to next available register
    public memContent addVarToReg(Token var) {
        // Decrement variable instances.
        memContent tmp = nextAvailReg(var);
        removeReference(var, tmp.nameRef);

        return tmp;
    }

    // NOTE - some registers are overwritten regardless of lock
    // %rax when returning
    // %rax when after a call (see above)
    // %rax after division
    public memContent addVarToReg(Register reg, Token var) {
        System.out.println("LOCATION: addVarToReg(Register, Token)");
        Integer index = reg.ordinal();
        System.out.println("Adding the variable " + var.str + " specifically to register " + registers.get(index).getName());
        // if the explicitly requested register is locked,
        // relocate the contents.

        // if the register has a lock and the contents are still being used, find a new register to put it in
        if(registers.get(index).lock) {
            // && refCount.get(registers.get(index).var)) > 0
            System.out.println("The register " + registers.get(index).getName() + " is locked, so the contents (" + registers.get(index).var  + ") will be relocated to the next available register");
            // we need to inject an instruction where we move the contents of the
            // otherwise we return the original register

            this.asmExpr += "\tmovl\t" + registers.get(index).getName() + ", " + nextAvailReg(new Token (registers.get(index).var, TokenType.TK_IDENTIFIER)).getName() + "\n";
            //System.out.println(this.asmExpr);
        }

        // place content into specifically requested register
        System.out.println("The register " + registers.get(index).getName() + " now contains the variable " + var.str);
        registers.get(index).setVar(var.str);

        if(refCounter.get(var.str) != null && refCounter.get(var.str) != 0) {
            System.out.println("The register " + registers.get(index).getName() + " is now locked");
            registers.get(index).setLock(true);
            removeReference(var, registers.get(index).nameRef);
        }

        return registers.get(index);
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
        for(Integer i = 0; i < tmpMem.size(); i++) {
            if(tmpMem.get(i).var.equals(var.str)) {
                //removeReference(var, i.toString());
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
                removeReference(var, registers.get(i).nameRef);
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

