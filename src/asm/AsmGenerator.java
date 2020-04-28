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
                    asmExpr += "\n\t## function epilog\n";
                    // move whatever value in esi to eax if a return is valid
                    if (expr.dest != null) {
                        location = mem.getVarLocation(expr.dest).getName();
                        if (!location.equals("%eax")) {
                            asmExpr += "\tmovl\t" + location + ", %eax\n";
                        }
                    }

                    // pop off the basepointer from the stack
                    asmExpr += "\tleave\n";
                    asmExpr += "\tret\t\t## return the function\n";
                    break;
                case NOT:   // fall through
                case INC:   // fall through
                case DEC:
                    asmExpr = expr.inst.toString().toLowerCase() + "\t" + mem.getVarLocation(expr.dest) + "\n";
                    break;
                case LABEL:
                    asmExpr = expr.dest.str + ":\n\n";
                    break;
                case FUNC:
                    // new hashmap for each function call - may need to be modified
                    mem.newScope(exprList, i);

                    // function label
                    asmExpr += "\n" + prefix + expr.dest.str + ":\n\n";

                    // function prolog
                    asmExpr += "\t## function prolog\n";
                    asmExpr += "\tpushq\t%rbp\n";
                    asmExpr += "\tmovq\t%rsp, %rbp\n";

                    // have to calculate the parameters first to avoid stack subtracting
                    int paramCounter = 0;
                    String params = "";

                    // loop will never exceed a count of 6 and will never run on main
                    while (exprList.get(i + 1).inst == Instruction.LOADP && !expr.dest.str.equals("main")) {
                        paramCounter++;

                        // generate location for parameter and place variable reference into a table
                        location = mem.addVarToMem(exprList.get(i+1).dest);

                        // create an assembly expression
                        params += "\tmovl\t" + mem.getRegister(mem.regIndex).getName() + ", " + location + "\n";

                        // increment register index and move expression
                        mem.regIndex++;

                        i++;
                    }

                    // symbol table contains all declarations, so remove the parameters from the count
                    stackSpacing = (record.children.get(symCounter).table.size() - paramCounter) * 4;
                    if (stackSpacing != 0) {
                        // align to 16
                        stackSpacing += stackSpacing % 16;
                        asmExpr += "\tsubq\t$" + stackSpacing + ", %rsp\n";
                    }


                    // place parameters into memory(if they exist)
                    asmExpr += "\t## load parameters into stack (if they exist)\n";
                    asmExpr += params;
                    asmExpr += "\n";

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
                    asmExpr += "\t## store variable in stack\n";

                    // if the source is a number, load it into memory
                    // otherwise load the location into memory
                    if(src.tokenType == TokenType.TK_NUMBER) {
                        asmExpr += "\tmovl\t$" + expr.sources.get(0).str + ", " + location + "\n";
                    } else {
                        asmExpr += "\tmovl\t" + mem.getVarLocation(src).getName() + ", " + location + "\n";
                    }

                    break;
                case BREAK: // fall through
                case JMP:
                    asmExpr = "\t" + expr.inst.toString().toLowerCase() + "\t" + expr.dest.str;
                    break;

                /* TWO SOURCES */
                case DIV:   // Needs to be handled differently.
                    Token div1 = expr.sources.get(0);
                    Token div2 = expr.sources.get(1);

                    String var = mem.getVarLocation(div1).getName();

                    if(!var.equals("%eax")) {
                        asmExpr += "\t## acknowledge dividend in %eax\n";
                        asmExpr += "\tmovl\t" + var + ", %eax\n";
                    }

                    asmExpr += "\t## sign-extended EAX into EDX (EDX = signedbit(EAX))\n";
                    asmExpr += "\tcdq" + "\n";                    // sign-extended EAX into EDX (EDX = signedbit(EAX))

                    asmExpr += "\t## acknowledge divisor; (EAX = (dividend / divisor); EDX = (dividend % divisor))\n";
                    asmExpr += "\tidivl\t" + mem.getVarLocation(div2).getName() + "\n";  // (EAX = (dividend / divisor); EDX = (dividend % divisor))

                    mem.addVarToReg(Register.eax, expr.dest);
                    break;
                case SUB:
                    Token src1;
                    Token src2;
                    if(expr.sources.size() != 2) {
                        src1 = expr.sources.get(0);
                        if(src1.tokenType == TokenType.TK_NUMBER) {
                            //TODO: need to get a method that will produce an empty register for numbers
                            // maybe get a register that can never be permanent?
                            asmExpr += "\tmovl\t$" + src1.str + ", " + mem.getVarLocation(src1).getName() + "\n";
                            asmExpr += "\tneg\t\t" + mem.getVarLocation(expr.dest).getName() + "\n";
                            mem.addVarToReg(expr.dest);
                        } else {
                            asmExpr = "\tneg\t\t%" + mem.getVarLocation(src1) + "\n";
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
                    memContent reg;
                    asmExpr = "\t## place variable into a register and operation\n";
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        reg = mem.addVarToReg(src2);
                        asmExpr += "\tmovl\t" + mem.getVarLocation(src2).getName() + ", " + reg.getName() + "\n";
                        asmExpr += "\t" + instr + "\t$" + src1.str + ", " + reg.getName() + "\n";
                    } else if(src2.tokenType == TokenType.TK_NUMBER) {
                        reg = mem.addVarToReg(src1);
                        asmExpr += "\tmovl\t" + mem.getVarLocation(src1).getName() + ", " + reg.getName() + "\n";
                        asmExpr += "\t" + instr + "\t$" + src2.str + ", " + reg.getName() + "\n";
                    } else {
                        memContent src1Loc = mem.getVarLocation(src1);
                        if(src1Loc.getName().startsWith("%")) {
                            asmExpr += "\t" + instr + "\t" + mem.getVarLocation(src2).getName() + ", " + src1Loc.getName() + "\n";
                            reg = src1Loc;
                        } else {
                            reg = mem.addVarToReg(src1);
                            asmExpr += "\tmovl\t" + mem.getVarLocation(src1).getName() + ", " + reg.getName() + "\n";
                            asmExpr += "\t" + instr + "\t" + mem.getVarLocation(src2).getName() + ", " + reg.getName() + "\n";
                        }
                    }
                    mem.addVarToReg(Register.valueOf(reg.nameRef), expr.dest);
                    break;
                case ASSIGN:
                    src1 = expr.sources.get(0);
                    asmExpr = "\t## assignment\n";
                    if(src1.tokenType == TokenType.TK_NUMBER) {
                        asmExpr += "\tmovl\t$" + src1.str + ", " + mem.getVarLocation(expr.dest).getName();
                    } else {
                        asmExpr += "\tmovl\t" + mem.getVarLocation(src1).getName() + ", " + mem.getVarLocation(expr.dest).getName();
                    }
                    break;
                case EVAL:
                    asmExpr = "\t## evauluate 'boolean' conditions\n";
                    asmExpr += "\tmovl\t" + "$0, %eax\n";
                    asmExpr += "\ttest\t\t%eax" + ", " + expr.sources.get(0).str + "\n"; // + src1, src2
                    // expr holds the current IR line we are dealing with.
                    asmExpr += "\t" + expr.inst + "\t\t" + expr.dest.str + "\n"; // + src1, src2

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

                    asmExpr = "\t## evauluate 'comparison' conditions\n";

                    // Put these variables into registers utilizing MOVE
                    // Then call cmp on both registers.
                    asmExpr += "\tcmp\t\t" + var1.str + ", " + var2.str + "\n"; // + src1, src2
                    // expr holds the current IR line we are dealing with.
                    asmExpr += "\t" + expr.inst + "\t\t" + expr.dest.str + "\n"; // + src1, src2

                    break;

                /* 'N' sources */
                case CALL:
                    asmExpr = "\t## load parameters into respective call registers and call function " + prefix + expr.sources.get(0).str + "\n";
                    if(expr.sources != null) {
                        for(int k = 1; k < expr.sources.size(); k++) {
                            if(expr.sources.get(k).tokenType == TokenType.TK_NUMBER) {
                                asmExpr += "\tmovl\t$" + expr.sources.get(k).str + ", " + mem.getRegister(k - 1).getName() + "\n";
                            } else {
                                asmExpr += "\tmovl\t" + mem.getMemory(expr.sources.get(k)).getName() + ", " + mem.getRegister(k - 1).getName() + "\n";
                            }
                        }
                    }

                    asmExpr += "\tcall\t" + prefix + expr.sources.get(0).str + "\n\n";
                    //asmExpr += "\tmovl\t%eax, %" + mem.getRegName(2) + "\n";

                    // mem.getReg(6) is %eax
                    mem.addVarToReg(Register.eax, expr.dest);
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
