package asm;

import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;
import parser.Parser;
import parser.SymbolRecord;

import java.util.*;

public class AsmGenerator {

    private static AsmGenerator instance;
    /*private class IRRef {

        private final IRExpression expr;
        private final int location;

        public IRRef(IRExpression expr, int location) {

            this.expr = expr;
            this.location = location;
        }

        public IRExpression getExpr() {
            return expr;
        }

        public int getLocation() {
            return location;
        }
    }

    ArrayList<IRRef constants */

    HashMap<String, varTable> constants;
    List<IRExpression> exprList;

    SymbolRecord record;
    int symCounter = 0;

    // Get a global instance registers to keep track of what is being used
    // and what is free to be utilized.
    memHandler mem;

    private AsmGenerator() {
        constants = new HashMap<>();
        mem = new memHandler();
        record = Parser.Instance().getRecord();
    }

    public static AsmGenerator getInstance() {

        instance = (instance == null) ? new AsmGenerator() : instance;

        return instance;
    }

    public ArrayList<String> generateAssembly(IRList irList, boolean optFlag, boolean underscore) {
        // IR expression list
        exprList = irList.IRExprList;
        // list of assembly expressions
        ArrayList<String> assembly = new ArrayList<>();

        String prefix = underscore ? "_" : "";

        // String asmPrelude = ".section .data\n\n.section .bss\n\n.section .text\n\n.globl main\n\n";
        String asmPrelude = ".globl " + prefix + "main\n";
        assembly.add(asmPrelude);

        int stackSpacing = 0;

        // optimize IR check based on flag
        if(!optFlag) optimize(irList.IRExprList);

        // iterate through expression list
        memContent reg;
        memContent location;
        for(int i = 0; i < exprList.size(); i++) {
            // assembly expression to be added to assembly arrayList
            mem.newExpr();

            // shorten expression list reference by index
            IRExpression expr = exprList.get(i);

            // produce assembly based on IR expression
            switch(expr.inst) {

                /* NO SOURCE */
                case NOP:
                    mem.asmExpr = "\tnop\n";
                    break;

                /* ONE SOURCE */
                case RET:
                    mem.asmExpr += "\n\t## function epilog\n";
                    // move whatever value in esi to eax if a return is valid
                    if (expr.dest != null) {
                        // NOTE: any time addVarToReg() is called with a specific register and it's used to build a string, it cannot be used inline
                        // because the string building will error
                        String regName;
                        if (expr.dest.tokenType == TokenType.TK_NUMBER) {
                            regName = mem.addVarToReg(Register.eax, expr.dest).getName();
                            mem.asmExpr += "\tmovl\t$" + expr.dest.str + ", " + regName + "\n";
                        } else {
                            memContent loc = mem.getVarLocation(expr.dest);
                            if (!loc.getName().equals("%eax")) {
                                regName = mem.addVarToReg(Register.eax, new Token(loc.var)).getName();
                                mem.asmExpr += "\tmovl\t" + loc.getName() + ", " + regName + "\n";
                            }
                        }
                    }

                    // pop off the basepointer from the stack
                    mem.asmExpr += "\tleave\n";
                    mem.asmExpr += "\t## return the function\n";
                    mem.asmExpr += "\tret\n";
                    break;
                case NOT:   // fall through
                case INC:   // fall through
                case DEC:
                    mem.asmExpr = "\t" + expr.inst.getAsm() + "\t" + mem.getVarLocation(expr.dest).getName() + "\n";
                    break;
                case LABEL:
                    mem.asmExpr = expr.dest.str + ":\n\n";
                    break;
                case FUNC:
                    // new hashmap for each function call - may need to be modified
                    mem.newScope(exprList, i);

                    // function label
                    mem.asmExpr += "\n" + prefix + expr.dest.str + ":\n\n";

                    // function prolog
                    mem.asmExpr += "\t## function prolog\n";
                    mem.asmExpr += "\tpushq\t%rbp\n";
                    mem.asmExpr += "\tmovq\t%rsp, %rbp\n";

                    // have to calculate the parameters first to avoid stack subtracting
                    int paramCounter = 0;
                    String params = "";

                    // loop will never exceed a count of 6 and will never run on main
                    while (exprList.get(i + 1).inst == Instruction.LOADP && !expr.dest.str.equals("main")) {
                        paramCounter++;

                        // generate location for parameter and place variable reference into a table
                        location = mem.addVarToMem(exprList.get(i+1).dest);

                        // create an assembly expression
                        params += "\tmovl\t" + mem.getRegister(mem.regIndex).getName() + ", " + location.getName() + "\n";

                        // increment register index and move expression
                        mem.regIndex++;

                        i++;
                    }

                    // symbol table contains all declarations, so remove the parameters from the count
                    stackSpacing = (record.children.get(symCounter).table.size() - paramCounter) * 4;
                    if (stackSpacing != 0) {
                        // align to 16
                        stackSpacing += stackSpacing % 16;
                        mem.asmExpr += "\tsubq\t$" + stackSpacing + ", %rsp\n";
                    }


                    // place parameters into memory(if they exist)
                    mem.asmExpr += "\t## load parameters into stack (if they exist)\n";
                    mem.asmExpr += params;
                    mem.asmExpr += "\n";

                    // increment next scope in table
                    symCounter++;
                    break;
                case LOAD:
                    location = mem.addVarToMem(expr.dest);
                    Token src;
                    if(expr.sources == null) {
                        src = new Token("0", TokenType.TK_NUMBER);
                    } else {
                        src = expr.sources.get(0);
                    }
                    mem.asmExpr += "\t## store variable in stack\n";

                    // if the source is a number, load it into memory
                    // otherwise load the location into memory
                    if(src.tokenType == TokenType.TK_NUMBER) {
                        mem.asmExpr += "\tmovl\t$" + expr.sources.get(0).str + ", " + location.getName() + "\n";
                    } else {
                        mem.asmExpr += "\tmovl\t" + mem.getVarLocation(src).getName() + ", " + location.getName() + "\n";
                    }

                    break;
                case BREAK: // fall through
                case JMP:
                    mem.asmExpr = "\t" + expr.inst.toString().toLowerCase() + "\t" + expr.dest.str;
                    break;

                /* TWO SOURCES */
                case DIV:   // Needs to be handled differently.
                    Token div1 = expr.sources.get(0);
                    Token div2 = expr.sources.get(1);

                    String var;

                    if (div1.tokenType == TokenType.TK_NUMBER) {
                        var = mem.addVarToReg(div1).getName();
                        mem.asmExpr += "\tmovl\t$" + div1.str + ", " + var + "\n";
                    } else {
                        var = mem.getVarLocation(div1).getName();
                    }

                    if(!var.equals("%eax")) {
                        mem.asmExpr += "\t## acknowledge dividend in %eax\n";
                        mem.asmExpr += "\tmovl\t" + var + ", %eax\n";
                    }

                    mem.asmExpr += "\t## sign-extended EAX into EDX (EDX = signedbit(EAX))\n";
                    mem.asmExpr += "\tcdq" + "\n";                    // sign-extended EAX into EDX (EDX = signedbit(EAX))

                    mem.asmExpr += "\t## acknowledge divisor; (EAX = (dividend / divisor); EDX = (dividend % divisor))\n";
                    if (div2.tokenType != TokenType.TK_NUMBER) {
                        mem.asmExpr += "\tidivl\t" + mem.getVarLocation(div2).getName() + "\n";  // (EAX = (dividend / divisor); EDX = (dividend % divisor))
                    } else {
                        reg = mem.addVarToReg(div2);
                        mem.asmExpr += "\tmovl\t$" + div2.str + ", " + reg.getName() + "\n";
                        mem.asmExpr += "\tidivl\t" + reg.getName() + "\n";  // (EAX = (dividend / divisor); EDX = (dividend % divisor))
                    }

                    if (div1.str.equals(expr.dest.str)) {
                        memContent tmp = mem.getMemory(expr.dest);
                        mem.asmExpr += "\tmovl\t" +  "%eax, " + tmp.getName() + "\n";
                        // twice since we technically have 2 of the same variable in one instance
                        mem.removeReference(expr.dest, tmp.getName());
                        mem.removeReference(expr.dest, tmp.getName());
                    } else {
                        //mem.addVarToReg(Register.valueOf(reg.nameRef), expr.dest);
                        mem.addVarToReg(Register.eax, expr.dest);
                    }
                    break;
                case SUB:
                    Token src1;
                    Token src2;

                    if(expr.sources.size() != 2) {
                        src1 = expr.sources.get(0);
                        if(src1.tokenType == TokenType.TK_NUMBER) {
                            //TODO: need to get a method that will produce an empty register for numbers
                            // maybe get a register that can never be permanent?
                            mem.asmExpr += "\tmovl\t$" + src1.str + ", " + mem.addVarToReg(src1).getName() + "\n";
                            mem.asmExpr += "\tneg\t\t" + mem.getVarLocation(src1).getName() + "\n";
                            mem.addVarToReg(expr.dest);
                        } else {
                            mem.asmExpr = "\tneg\t\t%" + mem.getVarLocation(src1) + "\n";
                        }
                        break;
                    }

                case ADD:   // fall through
                case MUL:   // fall through
                case AND:   // fall through
                case OR:
                    src1 = expr.sources.get(0);
                    src2 = expr.sources.get(1);
                    String instr = expr.inst.getAsm();
                    // if the location of a is a register or a memory address, then proceed
                    mem.asmExpr = "\t## place variable into a register and operation " + instr + "\n";
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        if(src2.tokenType == TokenType.TK_NUMBER) {
                            mem.asmExpr += "\tmovl\t$" + src2.str + ", " + mem.addVarToReg(src2).getName() + "\n";
                            reg = mem.getVarLocation(src2);
                            mem.asmExpr += "\t" + instr + "\t$" + src1.str + ", " + reg.getName() + "\n";
                        } else {
                            reg = mem.addVarToReg(src1);
                            mem.asmExpr += "\tmovl\t$" + src1.str + ", " + reg.getName() + "\n";
                            mem.asmExpr += "\t" + instr + "\t" + mem.getVarLocation(src2).getName() + ", " + reg.getName() + "\n";
                        }
                    } else if(src2.tokenType == TokenType.TK_NUMBER) {
                        reg = mem.addVarToReg(src2);
                        mem.asmExpr += "\tmovl\t$" + src2.str + ", " + reg.getName() + "\n";
                        mem.asmExpr += "\t" + instr + "\t" + mem.getVarLocation(src1).getName() + ", " + reg.getName() + "\n";
                    } else {
                        //System.out.println("src1 = " + src1);
                        memContent src1Loc = mem.getVarLocation(src1);
                        //System.out.println("src1Loc = " + src1Loc);
                        if(src1Loc.getName().startsWith("%")) {
                            mem.asmExpr += "\t" + instr + "\t" + mem.getVarLocation(src2).getName() + ", " + src1Loc.getName() + "\n";
                            reg = src1Loc;
                        } else {
                            reg = mem.addVarToReg(src1);
                            mem.asmExpr += "\tmovl\t" + mem.getVarLocation(src1).getName() + ", " + reg.getName() + "\n";
                            mem.asmExpr += "\t" + instr + "\t" + mem.getVarLocation(src2).getName() + ", " + reg.getName() + "\n";
                        }
                    }

                    // first conditional used for +=, -+, etc when the src and dest (FROM IR) are the same.
                    if (src1.str.equals(expr.dest.str)) {
                        memContent tmp = mem.getMemory(expr.dest);
                        mem.asmExpr += "\tmovl\t" + reg.getName() + ", " + tmp.getName() + "\n";
                        mem.removeReference(expr.dest, tmp.getName());
                        mem.removeReference(expr.dest, tmp.getName());

                    } else {
                        mem.addVarToReg(Register.valueOf(reg.nameRef), expr.dest);
                    }
                    break;
                case ASSIGN:
                    src1 = expr.sources.get(0);
                    System.out.println("src1 = " + src1);
                    System.out.println("expr.dest = " + expr.dest);
                    mem.asmExpr = "\t## assignment\n";
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        // Move immediate assignment value into variable.
                        mem.asmExpr += "\tmovl\t$" + src1.str + ", " + mem.getVarLocation(expr.dest).getName();
                    } else {
                        // move updated assignment to previously assigned location.
                        memContent tmp = mem.getVarLocation(src1);
                        if (tmp.getName().startsWith("-")) {
                            // since we cannot mov <mem>, <mem> we need to provide an available register to move to.
                            memContent tmp2 = mem.addVarToReg(new Token(tmp.var));
                            mem.asmExpr += "\tmovl\t" + tmp.getName() + ", " + tmp2.getName() + "\n";
                            mem.asmExpr += "\tmovl\t" + tmp2.getName() + ", " + mem.getVarLocation(expr.dest).getName();
                        } else {
                            System.out.println("CONTENTS OF THE ASSIGNEMNET: " + tmp.var);
                            mem.asmExpr += "\tmovl\t" + tmp.getName() + ", " + mem.getVarLocation(expr.dest).getName();
                        }
                    }
                    break;
                case EVAL:
                    mem.asmExpr = "\t## evauluate 'boolean' conditions\n";
                    mem.asmExpr += "\tmovl\t" + "$0, %eax\n";
                    mem.asmExpr += "\ttest\t\t%eax" + ", " + expr.sources.get(0).str + "\n"; // + src1, src2
                    // expr holds the current IR line we are dealing with.
                    mem.asmExpr += "\t" + expr.inst + "\t\t" + expr.dest.str + "\n"; // + src1, src2

                    break;
                case EQUAL:     // fall through
                case NEQUAL:    // fall through
                case GREQ:      // fall through
                case LSEQ:      // fall through
                case GRTR:      // fall through
                case LESS:      // fall through
                    /*
                     * CMP SYNTAX
                     * cmp <reg>,<reg>
                     * cmp <reg>,<mem>
                     * cmp <mem>,<reg>
                     * cmp <reg>,<con>
                     */
                    Token var1 = expr.sources.get(0);
                    Token var2 = expr.sources.get(1);

                    mem.asmExpr = "\t## evauluate 'comparison' conditions\n";

                    // Put these variables into registers utilizing MOVE
                    // Then call cmp on both registers.
                    mem.asmExpr += "\tcmp\t\t" + var1.str + ", " + var2.str + "\n"; // + src1, src2
                    // expr holds the current IR line we are dealing with.
                    mem.asmExpr += "\t" + expr.inst + "\t\t" + expr.dest.str + "\n"; // + src1, src2

                    break;

                /* 'N' sources */
                case CALL:
                    mem.asmExpr = "\t## load parameters into respective call registers and call function " + prefix + expr.sources.get(0).str + "\n";
                    if(expr.sources != null) {
                        memContent regName;
                        for(int k = 1; k < expr.sources.size(); k++) {
                            if(expr.sources.get(k).tokenType == TokenType.TK_NUMBER) {
                                // NOTE: any time addVarToReg() is called with a specific register and it's used to build a string, it cannot be using inline
                                // because the string building will error
                                regName = mem.addVarToReg(Register.values()[k-1], expr.sources.get(k));
                                mem.asmExpr += "\tmovl\t$" + expr.sources.get(k).str + ", " + regName.getName() + "\n";
                            } else {
                                memContent memContent = mem.getMemory(expr.sources.get(k));
                                regName = mem.addVarToReg(Register.values()[k-1], new Token(memContent.var, TokenType.TK_IDENTIFIER));
                                mem.asmExpr += "\tmovl\t" + memContent.getName() + ", " + regName.getName() + "\n";
                            }
                            regName.setLock(true);
                        }
                        // we unlock all the parameters after use because when the register round-robbin uses the parameter specific registers,
                        // the parameter registers end up getting locked and are not able to be unlocked later because the references are not necessarily
                        // used in the future

                    }

                    mem.addVarToReg(Register.eax, expr.dest);
                    mem.unlockParameters(expr.sources.size() - 1);
                    mem.asmExpr += "\tcall\t" + prefix + expr.sources.get(0).str + "\n\n";
                    break;
                default:
                    mem.asmExpr = "";
                    break;
            }
            assembly.add(mem.asmExpr);
            System.out.println(mem.asmExpr);
        }

        return assembly;
    }

    private void optimize(List<IRExpression> irExprList) {

        // just DO IT.
        //          ..
        //        .;:;.
        // ..    .;:;.
        // .;:;..;:;.
        //  .;::::;.
        //   .;::;.
        //     ..

        IRExpression expr;
        for(int i = 0; i < irExprList.size(); i++) {
            // temp variable to shorten
            expr = irExprList.get(i);
            if(irExprList.get(i).inst == Instruction.LOAD) {
                // place constant in a hashmap, indexed by the location with a corresponding initialization and value
                if(expr.sources.get(0).tokenType == TokenType.TK_NUMBER) {
                    constants.putIfAbsent(expr.dest.str, new varTable(expr.sources.get(0), i));
                }
            }
        }

        for(int i = 0; i < irExprList.size(); i++) {
            expr = irExprList.get(i);
            switch(expr.inst) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case NOT: {

                    Token s0 = expr.sources.get(0);
                    Token s1 = expr.sources.get(1);

                    if(constants.containsKey(s0.str)) {
                        int s0_index = constants.get(s0.str).index;
                        Integer s0_value = constants.get(s0.str).value;
                        expr.sources.set(0, new Token(s0_value.toString(), TokenType.TK_NUMBER));
                        irExprList.remove(s0_index);
                    }

                    if(constants.containsKey(s1.str)) {
                        int s1_index = constants.get(s1.str).index;
                        Integer s1_value = constants.get(s1.str).value;
                        expr.sources.set(1, new Token(s1_value.toString(), TokenType.TK_NUMBER));
                        irExprList.remove(s1_index);
                    }
                } break;
                case CALL:
                    for(int j = 1; j < expr.sources.size(); j++) {
                        String variable = expr.sources.get(j).str;
                        if(constants.containsKey(variable)) {
                            Integer varValue = constants.get(variable).value;
                            expr.sources.set(j, new Token(varValue.toString(), TokenType.TK_NUMBER));
                            irExprList.remove(constants.get(variable).index);
                        }
                    }
                default:
                    break;
            }
        }
    }
}
