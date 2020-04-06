package ir;

import lexer.Token;
import lexer.TokenType;

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

    public IRExpression(Instruction inst, Token src1, Token dest) {
        this.inst = inst;
        source1 = src1;
        this.dest = dest;
        source2 = null;
    }

    public IRExpression(Instruction inst, Token dest) {
        this.inst = inst;
        this.dest = dest;
        source1 = null;
        source2 = null;
    }

    public IRExpression(Instruction inst) {
        this.inst = inst;
        this.dest = null;
        source1 = null;
        source2 = null;
    }

    public IRExpression(String inst) {
        switch(inst) {
            case "LABEL":
                this.inst = Instruction.LABEL;
                break;
            case "BREAK":
                this.inst = Instruction.BREAK;
                break;
            case "RET":
                this.inst = Instruction.RET;
                break;
        }

        this.dest = null;
        source1 = null;
        source2 = null;
    }

    public StringBuilder printInstruction() {

        StringBuilder irString = new StringBuilder();

        if(inst != Instruction.LABEL) {
            irString.append("\t");
        }

        irString.append("(" + inst);

        if(source1 != null)
            irString.append(" " + source1.str);

        if(source2 != null)
            irString.append(", " + source2.str);

        if(dest != null) {
            if (source1 != null || source2 != null) {
                irString.append(",");
            }

            irString.append(" " + dest.str);
        }

        irString.append(")\n");
        return irString;
    }


}
