package ir;

public enum Instruction {
    ADD("add\t"),
    SUB("sub\t"),
    MUL("imul"),
    DIV("idivl"),

    ASSIGN("movl"),
    FUNC(null),
    RET("ret"),
    CALL("call"),
    NOP("nop"),
    LOAD("movl"),
    LOADP("movl"),

    INC("incl"),
    DEC("decl"),

    NOT("not"),
    AND("and"),
    OR("or"),

    JMP("jmp"),
    LABEL(null),
    BREAK("movl"),
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
