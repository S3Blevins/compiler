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

    public String label_name;
    public int label_id;

    public IRList() {
        label_name = "L";
        label_id = 0;
    }

    public boolean addExpr(IRExpression expr) {
        return IRExprList.add(expr);
    }

    public Token getLabelName() {
        // create a new label and increment afterwards
        String newLabel = label_name + label_id;
        label_id++;

        return new Token(newLabel, TokenType.TK_IDENTIFIER);
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
