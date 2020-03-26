package ir;

import java.util.ArrayList;
import java.util.List;

/**
 * The wrapper class that will house all of the IR
 * in an arraylist.
 */
public class IRList {

    public List<IRExpression> IRs = new ArrayList<>();

    public String label_name;
    public int label_id;

    public IRList() {
        label_name = "L";
        label_id = 0;
    }

    public String getLabelName() {
        // create a new label and increment afterwards
        String newLabel = label_name + label_id;
        label_id++;

        return newLabel;
    }

    public void printIR() {
        for(IRExpression expr: IRs) {
            expr.printInstruction();
        }
    }
}
