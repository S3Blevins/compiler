package asm;

import ir.IRExpression;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.SymbolRecord;

import java.util.*;

import static java.lang.System.exit;

public class memHandler {

    // reference counter
    public HashMap<String, Integer> refCounter;

    public ArrayList<memContent> registers;
    public Stack<ArrayList<memContent>> stack;

    public int regIndex;

    public SymbolRecord record;
    public Stack<SymbolRecord> tablePtr;
    public int depth;
    public Stack<Integer> child;

    // moved the assembly expression into the memory handler in order to handle
    // adding intermediary expressions where the register is locked and needs to be moved
    public String asmExpr;

    public memHandler() {
        this.registers = new ArrayList<>();

        this.stack = new Stack<>();
        this.stack.push(new ArrayList<>());

        this.depth = 0;
        this.child = new Stack<>();
        this.child.push(0);
        this.regIndex = 0;

        this.record = Parser.Instance().getRecord();

        this.tablePtr = new Stack<>();
        this.tablePtr.push(record);

        for(Register reg: Register.values()) {
            this.registers.add(new memContent(reg.name(), ""));
        }
    }

    // create new expression for each loop through IR
    public void newExpr() {
        asmExpr = "";
    }

    // create a new scope for loops
    public void newLoopScope() {
        this.depth++;
        Integer childCount;
        if(depth > this.child.size()) {
            this.child.push(0);
            childCount = 0;
        } else {
            childCount = this.child.pop();
            this.child.push(++childCount);
        }

        // get relevant table from symbol table record
        if(this.tablePtr.peek().hasChildren()) {
            //System.out.println("VARIABLES: " + this.tablePtr.peek().children.get(childCount).table.entrySet());
            this.tablePtr.push(this.tablePtr.peek().children.get(childCount));
        } else {
            this.tablePtr.push(new SymbolRecord());
        }

        //System.out.println("child = " + this.child);
        //System.out.println("depth = " + this.depth);
    }

    // remove loop scope and restore the relevant record table
    public void endLoopScope() {
        if(depth < this.child.size()) {
            this.child.pop();
        }

        for(Map.Entry<String, String> set: this.tablePtr.peek().table.entrySet()) {
            getMemory(new Token(set.getKey())).setLock(false);
        }

        //this.registers.get(6).setLock(false);

        this.tablePtr.pop();

        this.depth--;

        //System.out.println("child = " + this.child);
        //System.out.println("depth = " + this.depth);
    }

    // create a new scope for functions and populate the reference counter
    public void newScope(List<IRExpression> irList, int index) {
        this.stack.push(new ArrayList<>());
        this.refCounter = new HashMap<>();

        this.depth++;
        index++; // skip over function instruction
        // run loop for all IR Expressions in the function to populate reference count table
        while(index < irList.size() && irList.get(index).inst != Instruction.FUNC) {
            if(irList.get(index).sources != null) {

                // start at zero
                int i = 0;
                // if the instruction is a call, skip the first source because it's the function name
                if (irList.get(index).inst == Instruction.CALL) {
                    i = 1;
                }

                // continue loop
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

            Instruction inst = irList.get(index).inst;
            if(!EnumSet.of(Instruction.JMP, Instruction.LABEL, Instruction.BREAK,
                    Instruction.NEQUAL, Instruction.EQUAL, Instruction.GRTR, Instruction.GREQ, Instruction.LESS,
                    Instruction.LSEQ, Instruction.EVAL).contains(inst)) {

                Token dest = irList.get(index).dest;
                if (dest != null) {
                    if (refCounter.containsKey(dest.str)) {
                        refCounter.put(dest.str, refCounter.get(dest.str) + 1);
                    } else {
                        refCounter.put(dest.str, 1);
                    }
                }
            }
            // increment the loop
            index++;
        }
        //System.out.println(refCounter.entrySet());

        Integer childCount = this.child.pop();
        this.child.push(++childCount);

        if(this.tablePtr.peek().hasChildren()) {
            this.tablePtr.push(this.tablePtr.peek().children.get(childCount - 1));
        } else {
            this.tablePtr.push(new SymbolRecord());
        }

        //System.out.println("child = " + this.child);
        //System.out.println("depth = " + this.depth);
    }

    // remove the scope for a function
    public void endScope() {
        if(depth != 0) {
            this.stack.pop();

            if(depth < this.child.size()) {
                this.child.pop();
            }
            this.tablePtr.pop();

            this.depth--;

            //System.out.println("child = " + this.child);
            //System.out.println("depth = " + this.depth);
        }
    }

    /* ------------------------------------------------- */

    /**
     * Decrement the reference of the variable from memory
     * @param var variable to get in memory
     * @param location location in memory
     */
    public void removeReference(Token var, String location) {
        //System.out.println("LOCATION: removeReference(Token, String)");

        // outer try block is for integers which means memory locations
        try {
            Integer.parseInt(var.str);
            try {
                //System.out.println("The memory location " + stack.peek().get(Integer.parseInt(location)).getName() + " holding the number " + var.str + " will be unlocked");
                stack.peek().get(Integer.parseInt(location)).setLock(false);
            } catch(NumberFormatException e) {
                //System.out.println("The register " + registers.get(Register.valueOf(location).ordinal()).getName() + " holding the number " + var.str + " will be unlocked");
                registers.get(Register.valueOf(location).ordinal()).setLock(false);
            }
        } catch (NumberFormatException e) {
            //System.out.print("The reference count for var " + var.str + " is " + refCounter.get(var.str));

            // take current references and remove one
            int curCount = refCounter.get(var.str) - 1;
            refCounter.put(var.str, curCount);

            //System.out.println(" and will be reduced to " + curCount);
            // if the references hit zero and stored in a register, then unlock it for future use
            if(curCount == 0) {
                refCounter.remove(var.str);
                // unlock register or memory for future use
                try {
                    //System.out.println("The memory location " + stack.peek().get(Integer.parseInt(location)).getName() + " holding the variable " + var.str + " will be unlocked");
                    stack.peek().get(Integer.parseInt(location)).setLock(false);
                } catch(NumberFormatException e2) {
                    //System.out.println("The register " + registers.get(Register.valueOf(location).ordinal()).getName() + " holding the variable " + var.str + " will be unlocked");
                    //registers.get(Register.valueOf(location).ordinal()).setLock(false);
                }
            }
        }
    }

    /**
     * Unlock all registers related to parameters in function call
     * @param parameters number of parameters
     */
    public void unlockParameters(int parameters) {
        // max of 6
        for(int i = 0; i < parameters; i++) {
            //System.out.println("The register " + registers.get(i).getName() + " holding the parameter " + registers.get(i).var + " is being unlocked");
            registers.get(i).setLock(false);
        }
    }

    /* ------------------------------------------------- */


    /**
     * Find the next available spot in memory
     * @param var variable to store in memory
     * @return memory content of variable stored
     */
    public memContent nextOpenMem(Token var) {

        // look for the next variable in the stack
        for(int i = 0; i < this.stack.peek().size(); i++) {
            //System.out.println(refCounter.get(var.str));
            if(this.stack.peek().get(i).lock == false) {
                // this will be an open memory address
                this.stack.peek().get(i).var = var.str;
                if(this.refCounter.get(var.str) != null) {
                    this.stack.peek().get(i).setLock(true);
                }
                //TODO possibly create a reference decrement
                return this.stack.peek().get(i);
            }
        }

        Integer pos = this.stack.peek().size();
        this.stack.peek().add(new memContent(pos.toString(), var.str));
        this.stack.peek().get(pos).setLock(true);
        //System.out.println("The variable " + var.str + " will be stored in memory location " + this.stack.peek().get(pos).getName());
        //memIndex = this.stack.peek().size();

        return this.stack.peek().get(this.stack.peek().size() - 1);
    }

    /**
     * Add variable to next open place in memory and decrement the reference count
     * @param var variable to place into memory
     * @return variable memory contents
     */
    public memContent addVarToMem(Token var) {
        memContent tmp = nextOpenMem(var);
        removeReference(var, tmp.nameRef);

        return tmp;
    }

    /**
     * Place variable in the next available register location
     * @param var variable to place into memory
     * @return variable register contents
     */
    public memContent nextAvailReg(Token var) {
        // we're playing round-robin with the registers until we find one that's open
        int i = this.regIndex;

        //System.out.println("Adding the variable " + var.str + " in the next available register.");

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
            //System.out.println("All registers are full!");
            return nextOpenMem(var);
        }

        // if we end up here, we put the variable in a register
        registers.get(i).setVar(var.str);
        //System.out.println("The variable " + var.str + " is now held in the register " + registers.get(i).getName());

        // SET LOCK

        if(refCounter.get(var.str) != null) {
            registers.get(i).setLock(true);
            //System.out.println("The variable register " + registers.get(i).getName() + " is being locked");
        }

        return registers.get(i);
    }

    /**
     * Add variable to next available register location and decrement reference
     * @param var
     * @return
     */
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
        //System.out.println("LOCATION: addVarToReg(Register, Token)");
        Integer index = reg.ordinal();
        //System.out.println("Adding the variable " + var.str + " specifically to register " + registers.get(index).getName());
        // if the explicitly requested register is locked,
        // relocate the contents.

        // if the register has a lock and the contents are still being used, find a new register to put it in
        if(registers.get(index).lock && refCounter.get(registers.get(index).var) != null) {
            // && refCount.get(registers.get(index).var)) > 0
            //System.out.println("The register " + registers.get(index).getName() + " is locked, so the contents (" + registers.get(index).var  + ") will be relocated to the next available register");
            // we need to inject an instruction where we move the contents of the
            // otherwise we return the original register

            this.asmExpr += "\t## Register " + registers.get(index).getName() + " is used and is being relocated\n";
            this.asmExpr += "\tmovl\t" + registers.get(index).getName() + ", " + nextAvailReg(new Token (registers.get(index).var, TokenType.TK_IDENTIFIER)).getName() + "\n";
            //System.out.println(this.asmExpr);
        }

        // place content into specifically requested register
        //System.out.println("The register " + registers.get(index).getName() + " now contains the variable " + var.str);
        registers.get(index).setVar(var.str);

        if(refCounter.get(var.str) != null && refCounter.get(var.str) != 0) {
            //System.out.println("The register " + registers.get(index).getName() + " is now locked");
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

    /**
     * get memory location based on variable
     * @param var variable to get from memory
     * @return variable memory content
     */
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

    /**
     * get register based on variable
     * @param index index of register (ordered by enum
     * @return
     */
    public memContent getRegister(int index) {
        return registers.get(index);
    }

    // get register based on variable saved in it
    public memContent getRegister(Token var) {
        for(int i = 0; i < registers.size(); i++) {
            if(registers.get(i).var.equals(var.str)) {
                registers.get(i).setLock(false);
                removeReference(var, registers.get(i).nameRef);
                return registers.get(i);
            }
        }

        return null;
    }

    /**
     * get the variable location from either memory or register
     * @param var variable to get from either register or memory
     * @return memory or register contents
     */
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

