package asm;

import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AsmGenerator {

    private static AsmGenerator instance;
    /*private class IRRef {

        private final IRExpression expr;
        private final int location;

        public IRRef(IRExpression expr, int location) {

            this.expr = expr;
            this.location = location;
        }

        public IRExpression getExpr() {
            return expr;
        }

        public int getLocation() {
            return location;
        }
    }

    ArrayList<IRRef> constants;*/

    HashMap<Token, Integer> constants;

    private AsmGenerator() {

        //constants = new ArrayList<>();
        constants = new HashMap<>();
    }

    static AsmGenerator getInstance() {

        instance = (instance == null) ? new AsmGenerator() : instance;
        return instance;
    }

    ArrayList<String> generateAssembly(IRList irList) {

        ArrayList<String> source = new ArrayList<>();
        optimize(irList.IRExprList);

        return source;
    }

    private void optimize(List<IRExpression> irExprList) {

        // just DO IT.
        //          ..
        //        .;:;.
        // ..    .;:;.
        // .;:;..;:;.
        //  .;::::;.
        //   .;::;.
        //     ..

        for(int i = 0; i < irExprList.size(); i++) {

            if(irExprList.get(i).inst == Instruction.LOAD) {

                constants.putIfAbsent(irExprList.get(i).dest, i);
            }
        }

        for(int i = 0; i < irExprList.size(); i++) {

            switch(irExprList.get(i).inst) {

                case ADD:
                case SUB:
                case MUL:
                case DIV:
                case NOT: {

                    Token s0 = irExprList.get(i).sources.get(0);
                    Token s1 = irExprList.get(i).sources.get(1);
                    Token d = irExprList.get(i).dest;

                    if(constants.containsKey(s0) && constants.containsKey(s1)) {

                        int s0_index = constants.get(s0);
                        int s1_index = constants.get(s1);

                        // This will break how the loop works..
                        // FFFFFFFUUUU ---
                        irExprList.remove(s0_index);
                        irExprList.remove(s1_index);
                    }

                } break;
                default:
                    break;
            }
        }
    }
}
