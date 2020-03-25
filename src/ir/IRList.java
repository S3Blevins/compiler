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

    String getLabelName() {
        return label_name + label_id;
    }
}
