package ir;

public enum Instruction {
    ADD("add\t"),
    SUB("sub\t"),
    MUL("imul"),
    DIV("idivl"),

    ASSIGN("movl"),
    LABEL(null),
    FUNC(null),
    JMP("jmp"),
    RET("ret"),
    CALL("call"),
    NOP("nop"),
    LOAD("movl"),
    LOADP("movl"),
    BREAK("movl"),

    INC("inc"),
    DEC("dec"),

    NOT("not"),
    AND("and"),
    OR("or"),

    NEQUAL("jne"),
    EQUAL("je"),
    GREQ("jge"),
    LSEQ("jle"),
    GRTR("jg"),
    LESS("jl"),
    EVAL("jnz");

    private final String instr;

    Instruction(String instr) {
        this.instr = instr;
    }

    public String getAsm() {
        return this.instr;
    }
}
