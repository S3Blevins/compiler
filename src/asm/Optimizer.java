package asm;

import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Token;
import lexer.TokenType;

import java.util.*;

public class Optimizer {
    private Stack<HashMap<String, varTable>> constants;
    public List<IRExpression> finalIR;

    public Optimizer(IRList irList) {
        this.constants = new Stack<>();
        constants.push(new HashMap<>());
        List<IRExpression> IRPtr;

        int functionSize = 0;

        do {
            //IROuterPtr = new ArrayList<>(irList.IRExprList);
            int start = functionSize;
            this.constants.push(new HashMap<>());

            do {
                IRPtr = irList.IRExprList.subList(start, functionSize);
                System.out.println("PROPAGATION");
                functionSize = Propagation(irList.IRExprList, start);
                System.out.println("FOLDING:");
                Folding(irList.IRExprList, start, functionSize);

            } while (!IRPtr.equals(irList.IRExprList.subList(start, functionSize)));

            for(HashMap.Entry<String, varTable> set: constants.peek().entrySet()) {
                removeInit(set.getKey(), irList.IRExprList);
            }

            this.constants.pop();
            if(functionSize < irList.IRExprList.size())
                System.out.println(irList.IRExprList.get(functionSize).inst.toString() + " " + irList.IRExprList.get(functionSize).dest.str);
        } while(functionSize != irList.IRExprList.size());

        irList.IRExprList.removeAll(Collections.singleton(null));
        this.finalIR = irList.IRExprList;
    }

    private int Propagation(List<IRExpression> irExprList, int start) {

        int functionSize = 0;

        IRExpression expr;
        if(irExprList.get(start + functionSize).inst == Instruction.FUNC) {
            //this.constants.pop();
            functionSize++;
        }

        while ((start + functionSize) < irExprList.size() && irExprList.get(start + functionSize).inst != Instruction.FUNC) {
            // temp variable to shorten
            expr = irExprList.get(start + functionSize);
            if(expr == null) {
                //i++;
                continue;
            }
            if (irExprList.get(start + functionSize).inst == Instruction.LOAD) {
                // place constant in a hashmap, indexed by the location with a corresponding initialization and value
                if (expr.sources.get(0).tokenType == TokenType.TK_NUMBER) {
                    constants.peek().putIfAbsent(expr.dest.str, new varTable(expr.sources.get(0), start + functionSize));
                }
            }
            do {
                functionSize++;
            } while((start + functionSize) < irExprList.size() && irExprList.get(start + functionSize) == null);
        }

        for (int i = start; i < (functionSize + start); i++) {
            expr = irExprList.get(i);
            if(expr == null) {
                //i++;
                continue;
            }
            switch (expr.inst) {
                case INC:
                case DEC:
                    if(constants.peek().containsKey(expr.dest.str)) {
                        constants.peek().get(expr.dest.str).ref = true;
                    }
                    break;
                case SUB:
                    if (expr.sources.size() == 1) {
                        break;
                    }
                case ADD:
                case MUL:
                case DIV:
                    Token s0 = expr.sources.get(0);
                    Token s1 = expr.sources.get(1);

                    if (constants.peek().containsKey(s0.str) && !expr.dest.str.equals(s0.str)) {
                        expr.sources.set(0, replaceVar(s0));
                    }

                    if (constants.peek().containsKey(s1.str)) {
                        expr.sources.set(1, replaceVar(s1));
                    }

                    if(constants.peek().containsKey(expr.dest.str) && !expr.dest.str.equals(s0.str)) {
                            constants.peek().get(expr.dest.str).ref = true;
                    }
                    break;
                case NOT:
                    break;
                case CALL:
                    for (int j = 1; j < expr.sources.size(); j++) {
                        expr.sources.set(j, replaceVar(expr.sources.get(j)));
                    }
                    break;
                case RET:
                    expr.dest = replaceVar(expr.dest);
                    break;
                default:
                    break;
            }
        }
        return start + functionSize;
    }

    private Token replaceVar(Token tok) {
        if (constants.peek().containsKey(tok.str) && constants.peek().get(tok.str).ref == false) {
            Integer dest_value = constants.peek().get(tok.str).value;
            tok = new Token(dest_value.toString(), TokenType.TK_NUMBER);
            return tok;
        }

        return tok;
    }

    private void removeInit(String var, List<IRExpression> exprList) {
        if(constants.peek().containsKey(var) && constants.peek().get(var).ref == false) {
            exprList.set(constants.peek().get(var).index, null);
        }
    }

    private void removeExpr(int i, String var, List<IRExpression> exprList) {
        if(constants.peek().containsKey(var) && constants.peek().get(var).ref == false) {
            exprList.set(i, null);
        }
    }

    private int mathExpression(Instruction inst, Token s0, Token s1, boolean isS0, boolean isS1) {
        int s0num;
        int s1num;

        if(isS0) {
            s0num = Integer.parseInt(s0.str);
        } else {
            s0num = constants.peek().get(s0.str).value;
        }

        if(isS1) {
            s1num = Integer.parseInt(s1.str);
        } else {
            s1num = constants.peek().get(s1.str).value;
        }

        int exprVal;
        if (inst == Instruction.ADD) {
            exprVal = s0num + s1num;
        } else if (inst == Instruction.SUB) {
            exprVal = s0num - s1num;
        } else if (inst == Instruction.MUL) {
            exprVal = s0num * s1num;
        } else {
            exprVal = s0num / s1num;
        }

        return exprVal;
    }

    public void Folding(List<IRExpression> irExprList, int start, int functionSize) {
        IRExpression expr;
        for (int i = start; i < functionSize; i++) {
            // temp variable to shorten
            expr = irExprList.get(i);
            if(expr == null) {
                //i++;
                continue;
            }
            if ((start + functionSize) < irExprList.size() && irExprList.get(i).inst == Instruction.LOAD) {
                // place constant in a hashmap, indexed by the location with a corresponding initialization and value
                if (expr.sources.get(0).tokenType == TokenType.TK_NUMBER) {
                    constants.peek().putIfAbsent(expr.dest.str, new varTable(expr.sources.get(0), i));
                }
            }
        }

        for (int i = start; i < functionSize; i++) {
            expr = irExprList.get(i);
            if(expr == null) {
                //i++;
                continue;
            }
            switch (expr.inst) {
                case INC:
                    if(constants.peek().containsKey(expr.dest.str)) {
                        constants.peek().get(expr.dest.str).ref = false;
                        removeExpr(i, expr.dest.str, irExprList);
                        constants.peek().get(expr.dest.str).value++;
                    }
                    break;
                case DEC:
                    if(constants.peek().containsKey(expr.dest.str)) {
                        constants.peek().get(expr.dest.str).ref = false;
                        removeExpr(i, expr.dest.str, irExprList);
                        constants.peek().get(expr.dest.str).value--;
                    }
                    break;
                case SUB:
                    if (expr.sources.size() == 1) {
                        if(expr.sources.get(0).tokenType != TokenType.TK_NUMBER) {
                            if(constants.peek().containsKey(expr.sources.get(0).str)) {
                                    constants.peek().get(expr.sources.get(0).str).ref = true;
                            }
                        }
                        break;
                    }
                case ADD:
                case MUL:
                case DIV:
                    Token s0 = expr.sources.get(0);
                    Token s1 = expr.sources.get(1);

                    if (s0.tokenType == TokenType.TK_NUMBER && s1.tokenType == TokenType.TK_NUMBER) {
                        int exprVal = mathExpression(expr.inst, s0, s1, true, true);
                        irExprList.set(i, new IRExpression(Instruction.LOAD, new Token(Integer.toString(exprVal), TokenType.TK_NUMBER), expr.dest));
                    } else if (s0.tokenType == TokenType.TK_NUMBER) {
                        int exprVal = mathExpression(expr.inst, s0, s1, true, false);
                        if(constants.peek().containsKey(s1.str)) {
                            constants.peek().get(s1.str).ref = false;
                        }
                        irExprList.set(i, new IRExpression(Instruction.LOAD, new Token(Integer.toString(exprVal), TokenType.TK_NUMBER), expr.dest));
                    } else if (s1.tokenType == TokenType.TK_NUMBER) {
                        if(constants.peek().containsKey(s0.str)) {
                            constants.peek().get(s0.str).ref = false;
                        } else {
                            continue;
                        }

                        int exprVal = mathExpression(expr.inst, s0, s1, false, true);
                        if(s0.str.equals(expr.dest.str)) {
                            constants.peek().get(expr.dest.str).value = exprVal;
                            constants.peek().get(expr.dest.str).ref = false;
                            removeExpr(i, s0.str, irExprList);
                        } else {
                            irExprList.set(i, new IRExpression(Instruction.LOAD, new Token(Integer.toString(exprVal), TokenType.TK_NUMBER), expr.dest));
                        }
                    } else {
                        if(constants.peek().containsKey(s0.str)) {
                            constants.peek().get(s0.str).ref = false;
                        } else {
                            continue;
                        }

                        if(constants.peek().containsKey(s1.str)) {
                            constants.peek().get(s1.str).ref = false;
                        } else {
                            continue;
                        }

                        int exprVal = mathExpression(expr.inst, s0, s1, false, false);
                        if(s0.str.equals(expr.dest.str)) {
                            constants.peek().get(expr.dest.str).value = exprVal;
                            removeExpr(i, s0.str, irExprList);
                        }
                    }
/*
                    if(constants.peek().containsKey(expr.dest.str)) {
                        constants.peek().get(expr.dest.str).ref--;
                    }
*/
                    break;
                case NOT:
                    break;
                default:
                    break;
            }
        }
    }
}