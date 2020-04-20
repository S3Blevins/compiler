package ir;

public enum Instruction {
    ADD,
    SUB,
    MUL,
    DIV,

    ASSIGN,
    LABEL,
    FUNC,
    JMP,
    RET,
    CALL,
    NOP,
    LOAD,
    LOADP,
    BREAK,

    INC,
    DEC,

    NOT,
    AND,
    OR,

    EQUAL,
    GREQ,
    LSEQ,
    GRTR,
    LESS,
    EVAL
}
