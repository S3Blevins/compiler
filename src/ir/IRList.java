package ir;

import lexer.Token;
import lexer.TokenType;

import java.util.*;

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

    public Stack<Queue<Integer>> condScope;
    public Stack<Integer> endCondScope;
    public Stack<Integer> itrScope;

    public IRList() {
        condScope = new Stack<>();
        endCondScope = new Stack<>();
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

    public int getStartEndID() {
        endCondScope.push(endID++);
        return endCondScope.peek();
    }

    public int getEndID() {
        int tmp = endCondScope.pop();
        return tmp;
    }

    public void newCondScope() {
        condScope.push(new LinkedList<>());
    }

    public void endCondScope() {
        condScope.pop();
    }

    // increment comes first because of scoping (so starts at 1)
    public Token getCondJmpToLabel() {
        // ALWAYS CALLED BEFORE getCondLabel

        // add to queue
        condScope.peek().add(condID);

        // increment after print
        return new Token("_cond" + condID++, TokenType.TK_IDENTIFIER);
    }

    public Token getCondLabel() {
        // ALWAYS CALLED AFTER getCondJmpLabel()
        // <this> label is for the section of code to jump to

        // print top of the stack and then move back one "scope"
        Token tmp = new Token("_cond" + condScope.peek().peek(), TokenType.TK_IDENTIFIER);
        condScope.peek().remove();

        return tmp;
    }

    // increment comes first because of scoping (so starts at 1)
    public Token getItrJmpToTopLabel() {
        // 'for' or 'while'
        stateFlag = 2;

        itrScope.push(itrID++);
        return new Token("_loopExpr" + itrScope.peek(), TokenType.TK_IDENTIFIER);
    }

    public Token getItrTopLabel() {

        return new Token("_loopExpr" + itrScope.peek(), TokenType.TK_IDENTIFIER);
    }


    public Token getItrJmpToBottomLabel() {
        // 'for' or 'while'
        return new Token("_loopExit" + itrScope.peek(), TokenType.TK_IDENTIFIER);
    }

    public Token getItrBottomLabel() {
        Token tmp = new Token("_loopExit" + itrScope.peek(), TokenType.TK_IDENTIFIER);
        itrScope.pop();
        return tmp;
    }


    public String printIR() {
        StringBuilder IRString = new StringBuilder();
        for(IRExpression expr: IRExprList) {
            IRString.append(expr.printInstruction());
        }

        return IRString.toString();
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
