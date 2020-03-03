package intermediateRep_stage1;

import lexer.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Intermediate {
        public Instruction inst;
        public List<Token> idInfo;

        public Intermediate(Instruction instruction, Token ...tk) {
                this.inst = instruction;
                this.idInfo = Arrays.asList(tk);
        }

        public void printInstruction() {
                System.out.print("(" + inst + ",");
                for(Token token: idInfo) {
                        System.out.print(token.str + ",");
                }

                System.out.println(")");
        }

}
