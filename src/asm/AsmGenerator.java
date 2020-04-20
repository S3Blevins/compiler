package asm;

import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

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

    HashMap<Token, Integer> constants;

    List<IRExpression> exprList;


    private AsmGenerator() {
        constants = new HashMap<>();
    }

    public static AsmGenerator getInstance() {

        instance = (instance == null) ? new AsmGenerator() : instance;
        return instance;
    }

    public ArrayList<String> generateAssembly(IRList irList, boolean optFlag) {
        varTable table = new varTable();
        
        exprList = irList.IRExprList;
        ArrayList<String> source = new ArrayList<>();
        String[] registers = {"%edi","%esi", "%edx", "%ecx", "%r8", "%r9"};

        // optimize IR
        if(!optFlag) optimize(irList.IRExprList);

        //String asmPrelude = ".section .data\n\n.section .bss\n\n.section .text\n\n.globl main\n\n";
        String asmPrelude = ".globl _main\n";

        source.add(asmPrelude);

        for(int i = 0; i < exprList.size(); i++) {
            String asmExpr = "";
            IRExpression expr = exprList.get(i);
            switch(expr.inst) {
                // no source
                case NOP:
                case BREAK:
                    break;
                case RET:
                    if(expr.dest != null) {
                        //asmExpr = "\tmovq\t" + expr.dest.str + ", %eax\n";
                        asmExpr = "\tmovl\t" + "%esi" + ", %eax\n";
                    }

                    asmExpr += "\tpopq\t%rbp\n";
                    asmExpr += "\t" + expr.inst.toString().toLowerCase() + "\n";
                    break;
                // one source
                case ASSIGN:
                case INC:
                case DEC:
                case NOT:
                    break;
                case LABEL:
                    asmExpr = expr.dest.str + ":\n\n";
                    break;
                case FUNC:
                    // new hashmap for each function call - may need to be modified
                    table.newScope();
                    // function label
                    asmExpr = "\n_" + expr.dest.str + ":\n\n";

                    // function prolog
                    asmExpr += "\tpushq\t%rbp\n";
                    asmExpr += "\tmovq\t%rsp, %rbp\n";

                    // place parameters into memory(if they exist)
                    int j = 0;
                    while(exprList.get(i+1).inst == Instruction.LOADP) {
                        // generate location for parameter
                        String location = "-" + (4*(j+1)) + "(%rbp)";
                        // place variable reference into a table
                        table.addVar(exprList.get(i+1).dest, location);
                        // create an assembly expression
                        asmExpr += "\tmovq\t" + registers[0] + ", " + location + "\n";

                        // increment register index and move expression
                        j++;
                        i = i+1;
                    }

                    break;
                case LOAD:
                    asmExpr = "\tmovq\t" + table.getLocal(expr.sources.get(0)) + ", " + registers[1] + "\n";
                    table.addVar(expr.dest, registers[1]);
                    break;
                case JMP:
                    asmExpr = "\t" + expr.inst.toString().toLowerCase() + "\t" + expr.dest.str;
                    break;
                // two sources
                case ADD:
                    Token src1 = expr.sources.get(0);
                    Token src2 = expr.sources.get(1);
                    // if the location of a is a register or a memory address, then proceed
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        asmExpr = "\tmovq\t" + table.getLocal(src2) + ", " + registers[1] + "\n";
                        asmExpr += "\taddq\t$" + src1.str + ", " + registers[1] + "\n";
                    } else if(src2.tokenType == TokenType.TK_NUMBER) {
                        asmExpr = "\tmovq\t" + table.getLocal(src1) + ", " + registers[1] + "\n";
                        asmExpr += "\taddq\t$" + src2.str + ", " + registers[1] + "\n";
                    } else {
                        asmExpr = "\tmovq\t" + table.getLocal(src1) + ", " + registers[1] + "\n";
                        asmExpr += "\taddq\t" + table.getLocal(src2) + ", " + registers[1] + "\n";

                    }
                    table.addVar(expr.dest, registers[1]);
                    break;
                case MUL:
                case DIV:
                case AND:
                case OR:
                    break;
                case EQUAL:
                    asmExpr = "\tcmp\t"; // + src1, src2
                case GREQ:
                case LSEQ:
                case GRTR:
                case LESS:
                case EVAL:
                    break;
                // 'N' sources
                case SUB:
                case CALL:
                    if(expr.sources != null) {
                        for(int k = 1; k < expr.sources.size(); k++) {
                            asmExpr += "\tmovq\t$" + expr.sources.get(k).str + ", " + registers[k] + "\n";
                        }
                    }

                    asmExpr += "\tcall\t_" + expr.sources.get(0).str + "\n";
                    break;
                default:
                    break;
            }
            source.add(asmExpr);
        }

        return source;
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

        for(int i = 0; i < irExprList.size(); i++) {

            if(irExprList.get(i).inst == Instruction.LOAD) {

                constants.putIfAbsent(irExprList.get(i).dest, i);
            }
        }

        for(int i = 0; i < irExprList.size(); i++) {

            switch(irExprList.get(i).inst) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case NOT: {

                    Token s0 = irExprList.get(i).sources.get(0);
                    Token s1 = irExprList.get(i).sources.get(1);
                    Token d = irExprList.get(i).dest;

                    if(constants.containsKey(s0) && constants.containsKey(s1)) {

                        int s0_index = constants.get(s0);
                        int s1_index = constants.get(s1);

                        // This will break how the loop works..
                        // FFFFFFFUUUU ---
                        irExprList.remove(s0_index);
                        irExprList.remove(s1_index);
                    }

                } break;
                default:
                    break;
            }
        }
    }
}
