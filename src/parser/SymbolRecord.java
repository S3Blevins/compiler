package parser;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;

public class SymbolRecord {
        public HashMap <String, String> table;
        public ArrayList<SymbolRecord> children;

        public SymbolRecord() {
                table = new HashMap<>();
                children = null;
        }

        // add a new child to the last table of the last label
        public void  addChild() {
                if(children == null) {
                        children = new ArrayList<>();
                }
                children.add(new SymbolRecord());
        }

        public void addVariable(String var, String type) {
                table.put(var, type);
        }

        public boolean hasChildren() {
                if(this.children == null) {
                        return false;
                } else {
                        return true;
                }
        }

        // iterate to the last table of the specified label with corresponding depth
        public SymbolRecord lastTable(int depth) {
                if (depth == 0) {
                        return this;
                } else {
                        return children.get(children.size() - 1).lastTable(depth - 1);
                }
        }

        public int allChildrenScopeCount() {
                int count = this.table.size();
                if(this.hasChildren()) {
                        for(int i = 0; i < this.children.size(); i++) {
                                 count += this.children.get(i).allChildrenScopeCount();
                        }
                        return count;
                } else {
                        return count;
                }
        }



        // prints symbol table with some printf magic
        public String tablePrinter(int scope) {
                StringBuilder symbolString = new StringBuilder();
                Formatter stringFormat = new Formatter(symbolString);
                String indent = ":   ".repeat(scope);

                // table header line
                stringFormat.format("%s+------------------------------------+\n", indent);

                // name the scope
                if (scope == 0) {
                        stringFormat.format("%s| Scope Level: %-3s %20s", indent, scope, "|\n");
                } else {
                        stringFormat.format("%s%s| Scope Level: %-3s %20s", ":\t".repeat(scope - 1), ": ->", scope, "|\n");
                }

                // print out the type and associated variable
                for (Map.Entry<String, String> set : table.entrySet()) {
                        stringFormat.format("%s| %8s | %-22s %2s\n", indent, set.getValue(), set.getKey(), "|");
                }

                stringFormat.format("%s+------------------------------------+\n", indent);

                if (this.hasChildren()) {
                        stringFormat.format("%s| This table has %s inner scope(s) %5s", indent, this.children.size(), "|\n");
                        stringFormat.format("%s+------------------------------------+\n", indent);
                        for (int i = 0; i < this.children.size(); i++) {
                                symbolString.append(this.children.get(i).tablePrinter(scope + 1));
                        }
                }

                return symbolString.toString();
        }

}
