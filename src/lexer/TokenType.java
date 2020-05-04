package lexer;

import ir.Instruction;

/**
 * <h1>TokenType</h1>
 * This enum is a list of valid TokenTypes we support for our compiler.
 * Anything not listed will not be recognized. Each TokenType corresponds
 * to an asm instruction that we utilize when generating assembly.
 *
 * @author Sterling Blevins, Damon Estrada, Garrett Bates, Jacob Santillanes
 * @version 1.0
 * @since 2020-02-05
 */
public enum TokenType {
    TK_PLUSEQ(Instruction.ADD),
    TK_MINUSEQ(Instruction.SUB),
    TK_STAREQ(Instruction.MUL),
    TK_SLASHEQ(Instruction.DIV),
    TK_EQEQUAL(Instruction.EQUAL),
    TK_PPLUS(Instruction.INC),
    TK_MMINUS(Instruction.DEC),
    TK_NEQUAL(Instruction.NEQUAL),
    TK_RPAREN(null),
    TK_LPAREN(null),
    TK_RBRACE(null),
    TK_LBRACE(null),
    TK_RBRACKET(null),
    TK_LBRACKET(null),
    TK_PLUS(Instruction.ADD),
    TK_MINUS(Instruction.SUB),
    TK_STAR(Instruction.MUL),
    TK_SLASH(Instruction.DIV),
    TK_SEMICOLON(null),
    TK_COLON(null),
    TK_QMARK(null),
    TK_BANG(Instruction.NOT),
    TK_DOT(null),
    TK_COMMA(null),
    TK_DQUOTE(null),
    TK_KEYWORDS(null),
    TK_TYPE(null),
    TK_IDENTIFIER(null),
    TK_NUMBER(null),
    TK_EQUALS(Instruction.ASSIGN),
    TK_LESS(Instruction.LESS),
    TK_GREATER(Instruction.GRTR),
    TK_LESSEQ(Instruction.LSEQ),
    TK_GREATEREQ(Instruction.GREQ),
    TK_LOGAND(Instruction.AND),
    TK_LOGOR(Instruction.OR),
    TK_BOOL(Instruction.EVAL);

    private final Instruction instr;

    TokenType(Instruction instr) {
        this.instr = instr;
    }

    public Instruction getInstruction() {
        return this.instr;
    }

    public String toString() {
        return this.name();
    }
}
