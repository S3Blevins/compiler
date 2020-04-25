package asm;

import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;

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

    // Get a global instance registers to keep track of what is being used
    // and what is free to be utilized.
    memHandler mem;

    private AsmGenerator() {
        constants = new HashMap<>();
        mem = new memHandler();
    }

    public static AsmGenerator getInstance() {

        instance = (instance == null) ? new AsmGenerator() : instance;

        return instance;
    }

    public ArrayList<String> generateAssembly(IRList irList, boolean optFlag) {
        // IR expression list
        exprList = irList.IRExprList;
        // list of assembly expressions
        ArrayList<String> assembly = new ArrayList<>();

        // String asmPrelude = ".section .data\n\n.section .bss\n\n.section .text\n\n.globl main\n\n";
        String asmPrelude = ".globl _main\n";
        assembly.add(asmPrelude);

        // register index
        int regIndex = 6;

        // optimize IR check based on flag
        if(!optFlag) optimize(irList.IRExprList);
        System.out.println(irList.printIR());

        // iterate through expression list
        for(int i = 0; i < exprList.size(); i++) {
            // assembly expression to be added to assembly arrayList
            String asmExpr = "";
            String location = "";

            // shorten expression list reference by index
            IRExpression expr = exprList.get(i);

            // produce assembly based on IR expression
            switch(expr.inst) {

                /* NO SOURCE */
                case NOP:
                    asmExpr = "\tnop\n";
                    break;

                /* ONE SOURCE */
                case RET:
                    // move whatever value in esi to eax if a return is valid
                    if(expr.dest != null) {
                        asmExpr = "\tmovl\t" + mem.location(expr.dest) + ", %eax\n";
                    }

                    // pop off the basepointer from the stack
                    asmExpr += "\tpopq\t%rbp\n";
                    asmExpr += "\tret\n";
                    break;
                case NOT:   // fall through
                case INC:   // fall through
                case DEC:
                    asmExpr = expr.inst.toString().toLowerCase() + "\t" + mem.location(expr.dest) + "\n";
                    break;
                case LABEL:
                    asmExpr = expr.dest.str + ":\n\n";
                    break;
                case FUNC:
                    // new hashmap for each function call - may need to be modified
                    mem.newScope();
                    regIndex = 0;
                    // function label
                    asmExpr = "\n_" + expr.dest.str + ":\n\n";

                    // function prolog
                    asmExpr += "\tpushq\t%rbp\n";
                    asmExpr += "\tmovq\t%rsp, %rbp\n";

                    // place parameters into memory(if they exist)
                    while(exprList.get(i+1).inst == Instruction.LOADP) {
                        // generate location for parameter
                        location = "-" + (4*(regIndex+1)) + "(%rbp)";
                        // place variable reference into a table
                        mem.addStackVar(exprList.get(i+1).dest, location);
                        // create an assembly expression
                        asmExpr += "\tmovl\t%" + mem.getRegName(regIndex) + ", " + location + "\n";

                        // increment register index and move expression
                        regIndex++;
                        i++;
                    }
                    break;
                case LOAD:
                    location = "-" + (4*(regIndex+1)) + "(%rbp)";
                    asmExpr = "\tmovl\t$" + expr.sources.get(0).str + ", " + location + "\n";
                    mem.addStackVar(expr.dest, location);
                    regIndex++;
                    break;
                case BREAK: // fall through
                //TODO: FIX THIS
                case JMP:
                    asmExpr = "\t" + expr.inst.toString().toLowerCase() + "\t" + expr.dest.str;
                    break;

                /* TWO SOURCES */
                case SUB:
                    Token src1;
                    Token src2;
                    if(expr.sources.size() != 2) {
                        src1 = expr.sources.get(0);
                        asmExpr = "\tneg\t" + mem.location(src1) + "\n";
                        break;
                    }

                case ADD:   // fall through
                case MUL:   // fall through
                case DIV:   // fall through
                case AND:   // fall through
                case OR:
                    src1 = expr.sources.get(0);
                    src2 = expr.sources.get(1);
                    String instr = expr.inst.getAsm();
                    // if the location of a is a register or a memory address, then proceed
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        asmExpr = "\tmovl\t" + mem.location(src2) + ", %" + mem.getRegName(1) + "\n";
                        asmExpr += "\t" + instr + "\t$" + src1.str + ", %" + mem.getRegName(1) + "\n";
                    } else if(src2.tokenType == TokenType.TK_NUMBER) {
                        asmExpr = "\tmovl\t" + mem.location(src1) + ", %" + mem.getRegName(1) + "\n";
                        asmExpr += "\t" + instr + "\t$" + src2.str + ", %" + mem.getRegName(1) + "\n";
                    } else {
                        asmExpr = "\tmovl\t" + mem.location(src1) + ", %" + mem.getRegName(1) + "\n";
                        asmExpr += "\t" + instr + "\t" + mem.location(src2) + ", %" + mem.getRegName(1) + "\n";
                    }
                    mem.addRegVar(mem.getReg(1), expr.dest);
                    break;
                case ASSIGN:
                    src1 = expr.sources.get(0);
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        asmExpr = "\tmovl\t$" + src1.str + ", " + mem.location(expr.dest);
                    } else {
                        asmExpr = "\tmovl\t" + mem.location(src1) + ", " + mem.location(expr.dest);
                    }
                    break;

                case EQUAL:     // fall through
                case NEQUAL:    // fall through
                case GREQ:      // fall through
                case LSEQ:      // fall through
                case GRTR:      // fall through
                case LESS:      // fall through
                case EVAL:
                    /*
                     * CMP SYNTAX
                     * cmp <reg>,<reg>
                     * cmp <reg>,<mem>
                     * cmp <mem>,<reg>
                     * cmp <reg>,<con>
                     */
                    Token var1 = expr.sources.get(0);
                    Token var2 = expr.sources.get(1);

                    // Put these variables into registers utilizing MOVE
                    // Then call cmp on both registers.
                    asmExpr = "\tcmp\t\t" + var1.str + ", " + var2.str + "\n"; // + src1, src2
                    // expr holds the current IR line we are dealing with.
                    asmExpr += "\t" + expr.inst + "\t\t" + expr.dest.str + "\n"; // + src1, src2

                    break;

                /* 'N' sources */
                case CALL:
                    if(expr.sources != null) {
                        for(int k = 1; k < expr.sources.size(); k++) {
                            asmExpr += "\tmovl\t" + mem.getVarMemory(expr.sources.get(k)) + ", %" + mem.getRegName(k-1) + "\n";
                        }
                    }

                    asmExpr += "\tcall\t_" + expr.sources.get(0).str + "\n";
                    asmExpr += "\tmovl\t%eax, %" + mem.getRegName(2) + "\n";
                    mem.addRegVar(mem.getReg(2), expr.dest);
                    break;
                default:
                    asmExpr = null;
                    break;
            }
            assembly.add(asmExpr);
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
