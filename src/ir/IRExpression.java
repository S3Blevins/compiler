package ir;

import lexer.Token;

import java.util.Arrays;
import java.util.List;

/**
 * This class servers as the object that will make up the IR format.
 * Format design our group chose is (pending): (ASSEMBLY_INST, FIRST_PARAM, SEC_PARAM, IR_VAR)
 */
public class IRExpression {
    public Instruction inst;
    public Token source1;
    public Token source2;
    public Token dest;

    public IRExpression(Instruction instruction, Token src1, Token src2, Token dest) {
        this.inst = instruction;
        this.source1 = src1;
        this.source2 = src2;
        this.dest = dest;
    }

    public IRExpression(Instruction inst, Token dest) {
        this.inst = inst;
        this.dest = dest;
        source1 = null;
        source2 = null;
    }

    public void printInstruction() {
        System.out.print("(" + inst + ",");
        System.out.print(" " + source1 + ",");
        System.out.print(" " + source2);
        System.out.print(", " + dest);
        System.out.println(")");
    }


}
