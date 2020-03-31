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

    public Token getCondName() {
        // create a new label and increment afterwards
        lastBlock = new Token(condLabel + condID, TokenType.TK_IDENTIFIER);
        condID++;

        return lastBlock;
    }

    public Token getIteratorName() {
        // create a new label and increment afterwards
        lastBlock = new Token(itrLabel + itrID, TokenType.TK_IDENTIFIER);
        itrID++;

        return lastBlock;
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

    public Token getLastBlockLabel() {
        return lastBlock;
    }
}
