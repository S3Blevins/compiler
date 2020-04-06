package ir;

import lexer.Token;
import lexer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * The wrapper class that will house all of the IR
 * in an arraylist.
 */
public class IRList {

    public List<IRExpression> IRExprList = new ArrayList<>();

    // used for intermediate variable labels
    public int labelID = 0;

    // conditional label used for if/else-if blocks
    public int condID = 0;
    public int endID = 0;

    // iterator label used for for/while loop blocks
    public int itrID = 0;

    // 0 - neither
    // 1 - cond
    // 2 - loop
    public int stateFlag = 0;

    public Stack<Integer> condScope;
    public Stack<Integer> itrScope;

    public IRList() {
        condScope = new Stack<>();
        itrScope = new Stack<>();
    }



    public boolean addExpr(IRExpression expr) {
        return IRExprList.add(expr);
    }

    public Token getLabelName() {
        // create a new label and increment afterwards
        String newLabel = "L"+ labelID;
        labelID++;

        return new Token(newLabel, TokenType.TK_IDENTIFIER);
    }

    public int getEndID() {
        return endID++;
    }

    // increment comes first because of scoping (so starts at 1)
    public Token getCondJmpToLabel() {
        // ALWAYS CALLED BEFORE getCondLabel
        // <this> jump label is for the next conditional
        stateFlag = 1;

        // increment counter at the top of the stack
        condScope.push(condID++);

        return new Token("cond" + condScope.peek(), TokenType.TK_IDENTIFIER);
    }

    public Token getCondLabel() {
        // ALWAYS CALLED AFTER getCondJmpLabel()
        // <this> label is for the section of code to jump to

        // print top of the stack and then move back one "scope"
        Token tmp = new Token("cond" + condScope.peek(), TokenType.TK_IDENTIFIER);
        condScope.pop();

        return tmp;
    }

    // increment comes first because of scoping (so starts at 1)
    public Token getItrJmpToTopLabel() {
        // 'for' or 'while'
        stateFlag = 2;

        itrScope.push(itrID++);
        return new Token("loopTop" + itrScope.peek(), TokenType.TK_IDENTIFIER);
    }

    public Token getItrTopLabel() {

        return new Token("loopTop" + itrScope.peek(), TokenType.TK_IDENTIFIER);
    }


    public Token getItrJmpToBottomLabel() {
        // 'for' or 'while'
        return new Token("loopBottom" + itrScope.peek(), TokenType.TK_IDENTIFIER);
    }

    public Token getItrBottomLabel() {
        Token tmp = new Token("loopBottom" + itrScope.peek(), TokenType.TK_IDENTIFIER);
        itrScope.pop();
        return tmp;
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

    // I don't like this flag logic but it works for now... -Sterling
    public Token getLastBlockLabel() {
        switch (stateFlag) {
            case 1:
                return getCondJmpToLabel();
            case 2:
                return getItrJmpToBottomLabel();
            default:
                return null;
        }
    }
}
