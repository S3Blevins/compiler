package ir;

/**
 * <h1>Instruction</h1>
 * This class holds all asm instructions that will be outputed when generating
 * asm. Each instruction corresponds with their actual counter part that will
 * be utilized (instead of the uppercase name) when needed.
 *
 * @author Sterling Blevins, Damon Estrada, Garrett Bates, Jacob Santillanes
 * @version 1.0
 * @since 2020-03-23
 */
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

    NOT("notl"),
    AND("and"),
    OR("or"),

    JMP("jmp"),
    LABEL(null),
    BREAK("jmp"),
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
