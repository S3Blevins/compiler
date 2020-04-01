package ir;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;

/**
 * The wrapper class that will house all of the IR
 * in an arraylist.
 */
public class IRList {

    public List<IRExpression> IRExprList = new ArrayList<>();

    public String labelName = "L";
    public int labelID = 0;

    public Token lastBlock;

    public String condLabel = "cond";
    public int condID = 0;

    public Token lastCond;

    public String itrLabel = "iterator";
    public int itrID = 0;


    public boolean addExpr(IRExpression expr) {
        return IRExprList.add(expr);
    }

    public Token getLabelName() {
        // create a new label and increment afterwards
        String newLabel = labelName + labelID;
        labelID++;

        return new Token(newLabel, TokenType.TK_IDENTIFIER);
    }

    public Token getCondLabel() {
        // create a new label and increment afterwards
        lastCond= new Token(condLabel + condID, TokenType.TK_IDENTIFIER);
        condID++;

        return lastCond;
    }

    public Token getCondJmpLabel() {
        // create a new label and increment afterwards
        lastCond = new Token(condLabel + condID, TokenType.TK_IDENTIFIER);

        return lastCond;
    }

    public void printIR() {
        for(IRExpression expr: IRExprList) {
            expr.printInstruction();
        }
    }

    public Token getLastLabel() {
        if(IRExprList.isEmpty()) {
            return null;
        } else {
            return IRExprList.get(IRExprList.size() - 1).dest;
        }
    }

}
