package ir;

import lexer.Token;

import java.util.Arrays;
import java.util.List;

public class IRExpression {
    public Instruction inst;
    public List<Token> operands;

    public IRExpression(Instruction instruction, Token... tk) {
        this.inst = instruction;
        this.operands = Arrays.asList(tk);
    }

    public void printInstruction() {
        System.out.print("(" + inst + ",");
        for (Token token : operands) {
            System.out.print(token.str + ",");
        }

        System.out.println(")");
    }

}
