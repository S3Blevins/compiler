package ir;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class servers as the object that will make up the IR format.
 * Format design our group chose is : (ASSEMBLY_INST, FIRST_PARAM, SEC_PARAM, IR_VAR)
 * A more extensive look into other populated syntax can be found in our GitHub documentation.
 *
 * @author Sterling Blevins, Damon Estrada, Garrett Bates, Jacob Santillanes
 * @version 1.0
 * @since 2020-03-23
 */
public class IRExpression {
    public Instruction inst;
    public ArrayList<Token> sources = null;
    public Token dest;

    public IRExpression(Instruction instruction, Token src1, Token src2, Token dest) {
        this.inst = instruction;
        this.dest = dest;

        this.sources = new ArrayList<>();
        sources.add(src1);
        sources.add(src2);
    }

    public IRExpression(Instruction inst, Token src1, Token dest) {
        this.inst = inst;
        this.dest = dest;

        this.sources = new ArrayList<>();
        sources.add(src1);
    }

    public IRExpression(Instruction inst, Token dest, ArrayList<Token> sources) {
        this.inst = inst;
        this.dest = dest;
        this.sources = sources;
    }

    public IRExpression(Instruction inst, Token dest) {
        this.inst = inst;
        this.dest = dest;
        this.sources = null;
    }

    public IRExpression(Instruction inst) {
        this.inst = inst;
        this.dest = null;
        this.sources = null;
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
    }

    public StringBuilder printInstruction() {

        StringBuilder irString = new StringBuilder();

        if(!(inst == Instruction.LABEL || inst == Instruction.FUNC)) {
            irString.append("\t");
        }

        irString.append("(" + inst);

        if(sources != null) {
            if(sources.get(0) != null) {
                irString.append(" " + sources.get(0).str);
            }

            for (int i = 1; i < sources.size(); i++) {
                if (sources.get(i) != null) {
                    irString.append(", " + sources.get(i).str);
                }
            }
        }

        if(dest != null) {
            if(sources != null) {
                irString.append(",");
            }

            irString.append(" " + dest.str);
        }

        irString.append(")\n");
        return irString;
    }
}
