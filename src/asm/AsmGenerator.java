package asm;

import ir.IRExpression;
import ir.IRList;

import java.util.ArrayList;
import java.util.List;

public class AsmGenerator {

    private static AsmGenerator instance;

    private AsmGenerator() {
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
    }
}
