package ir;

public enum Instruction {
    ADD("addl"),
    SUB("subl"),
    MUL("mull"),
    DIV("divl"),

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

    Instruction(String instr) {
    }
}
