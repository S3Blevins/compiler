package ir;

import common.IVisitor;
import lexer.Token;
import lexer.TokenType;
import parser.Node;
import parser.treeObjects.*;

import java.util.ArrayList;

/**
 * This class handles all of the routines and details of
 * 1) Creating an IR object (IRExpression.java).
 * 2) Populating that object.
 * 3) Returning all of the objects to update IRList object (IRList.java)
 *    with all of the crafted IRs.
 */
public class IRBuilder implements IVisitor<Token> {
        // DONE: visitUnary
        // DONE: visitBinary
        // DONE: visitTernary
        // DONE: visitGroup
        // DONE: visitNumber
        // DONE: visitIdentifier
        // DONE: visitBlock
        // DONE: visitBreak
        // DONE: visitReturn
        // DONE: visitIterator
        // DONE: visitConditional
        // DONE: visitExpressionStatement
        // DONE: visitGoToLabel
        // DONE: visitGoTo
        // DONE: visitVarDec
        // DONE: visitFunDec
        // DONE: visitVar
        // DONE: visitParam
        // DONE: visitTypeDecl
        // DONE: visitProgram

        public IRList IRs;

        public IRBuilder() {
                IRs = new IRList();
        }

        @Override
        public Token visitUnary(Expression.Unary unary) {
                // unary expression evaluates expression and assigns instruction according to operator token

                Token expr = unary.getExpr().accept(this);
                Instruction unInstr = unary.op.tokenType.getInstruction();
                if(unary.op.tokenType == TokenType.TK_MINUS) {
                        IRs.addExpr(new IRExpression(unInstr, expr, IRs.getLabelName()));
                } else {
                        IRs.addExpr(new IRExpression(unInstr, expr));
                }

                return IRs.getLastLabel();
        }

        @Override
        public Token visitBinary(Expression.Binary binary) {
                // binary expression evaluates left and right and assigns instruction according to operator token
                Token left = binary.getLeftExpr().accept(this);
                Token right = binary.getRightExpr().accept(this);
                Instruction binInstr = binary.op.tokenType.getInstruction();

                TokenType binOp = binary.op.tokenType;
                Token dest;

                // if no destination, it is placed into right side's expression
                if(binOp == TokenType.TK_EQUALS) {
                        dest = null;
                }
                else if(binOp == TokenType.TK_STAREQ || binOp == TokenType.TK_MINUSEQ || binOp == TokenType.TK_PLUSEQ ||
                        binOp == TokenType.TK_SLASHEQ) {
                        dest = left;
                } else if(binOp == TokenType.TK_LESSEQ || binOp == TokenType.TK_LESS || binOp == TokenType.TK_GREATER ||
                        binOp == TokenType.TK_GREATEREQ || binOp == TokenType.TK_EQEQUAL || binOp == TokenType.TK_NEQUAL) {
                        dest = IRs.getLastBlockLabel();

                        if(dest == null)
                                dest = IRs.getLabelName();

                        // stateFlag turns off so that any evaluations which don't return do not contain a label
                        IRs.stateFlag = 0;
                } else {
                        dest = IRs.getLabelName();
                }

                IRs.addExpr(new IRExpression(binInstr, left, right, dest));

                return dest;
        }

        public Token visitfunCall(Expression.funCall call) {
                Token functionCallLabel = IRs.getLabelName();

                ArrayList<Token> arguments = new ArrayList<>();
                arguments.add(call.functionName);

                if(call.hasChildren()) {
                        for (int i = 0; i < call.children.size(); i++) {
                                arguments.add(call.children.get(i).accept(this));
                        }
                }

                IRs.addExpr(new IRExpression(Instruction.CALL, functionCallLabel, arguments));

                return functionCallLabel;
        }

        @Override
        public Token visitTernary(Expression.Ternary ternary) {
                // ternary is a container for other expression

                //TODO: fix scope of ternary

                if(ternary.children.get(0).hasChildren()) {
                        ternary.children.get(0).accept(this);
                } else {
                        IRs.addExpr(new IRExpression(Instruction.EVAL, ternary.children.get(0).accept(this), IRs.getCondJmpToLabel()));
                }

                // if just a number/boolean, nothing happens
                ternary.children.get(1).accept(this);

                IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getCondLabel()));

                // if just a number/boolean, nothing happens
                ternary.children.get(2).accept(this);

                return null;
        }

        @Override
        public Token visitGroup(Expression.Group group) {
                // behaves like other expressions

                iterator(group);

                return IRs.getLastLabel();
        }

        @Override
        public Token visitNumber(Expression.Number number) {
                // just returns value token

                return number.value;
        }

        @Override
        public Token visitBoolean(Expression.Boolean bool) {
                // converts a boolean to a number
                Token boolEquivalent;

                if(bool.bool.str.toLowerCase().equals("true")){
                        boolEquivalent = new Token("1", TokenType.TK_NUMBER);
                } else {
                        boolEquivalent = new Token("0", TokenType.TK_NUMBER);
                }

                return boolEquivalent;
        }

        @Override
        public Token visitIdentifier(Expression.Identifier identifier) {
                // just returns identifier token

                return identifier.value;
        }

        @Override
        public Token visitBlock(Statement.Block block) {
                // iterates through children

                iterator(block);

                return null;
        }

        @Override
        public Token visitReturn(Statement.Return statement) {
                // create RET expression with expression if it exists

                Token retVal = statement.children.get(0).accept(this);

                IRs.addExpr(new IRExpression(Instruction.RET, retVal));

                return null;
        }

        @Override
        public Token visitBreak(Statement.Break statement) {
                // create BREAK expression

                IRs.addExpr(new IRExpression(Instruction.BREAK, new Token("_loopExit" + IRs.endID, TokenType.TK_IDENTIFIER)));

                return null;
        }

        @Override
        public Token visitIteration(Statement.Iteration statement) {
                // insert a label and iterate through children

                //TODO: switch between 'for' and 'while labels'

                int index = 0;

                // insert label for iteration statement above the iteration statement
                // if the childsize is 3, there exists a variable declaration which proceeds the label
                if(statement.childSize() == 3) {
                        statement.children.get(0).accept(this);
                        index = 1;
                }

                // turn on flag for expression and then it turns off
                IRs.stateFlag = 2;
                IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getItrJmpToTopLabel()));

                // insert conditional based on expression (NOTE: if expression does not exist, it automatically evaluates to 1
                // check for children and execute normally if they exist,
                // otherwise it's a single value or boolean and insert EVAL instruction accordingly
                if(statement.children.get(index).hasChildren()) {
                        statement.children.get(index).accept(this);
                } else {
                        IRs.addExpr(new IRExpression(Instruction.EVAL, statement.children.get(index).accept(this), IRs.getItrJmpToBottomLabel()));
                }
                index++;

                // go through block of loop
                statement.children.get(index).accept(this);

                // jump to the loop label defined at the top of the loop
                IRs.addExpr(new IRExpression(Instruction.JMP, IRs.getItrTopLabel()));

                // insert a label for the end of the loop for when it evaluates to false
                IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getItrBottomLabel()));

                IRs.stateFlag = 0;
                return null;
        }

        @Override
        public Token visitConditional(Statement.Conditional conditional) {
                // conditional adds labels and instructions which jump based on evaluation of expressions

                IRs.newCondScope();
                Token endLabel = new Token("_endCond" + IRs.getStartEndID(), TokenType.TK_IDENTIFIER);

                // iterate through conditionals
                for(int i = 0; i < conditional.children.size() - 1; i+=2) {
                        IRs.stateFlag = 1;
                        if(conditional.children.get(i).hasChildren()) {
                                conditional.children.get(i).accept(this);
                        } else {
                                Token nonExpression = conditional.children.get(i).accept(this);
                                IRs.addExpr(new IRExpression(Instruction.EVAL, nonExpression, IRs.getCondJmpToLabel()));
                        }
                }

                // if the children of the conditional statement is odd, then there is an else
                if(conditional.children.size() % 2 != 0) {
                        IRs.addExpr(new IRExpression(Instruction.JMP, IRs.getCondJmpToLabel()));
                } else {
                        IRs.addExpr(new IRExpression(Instruction.JMP, endLabel));
                }

                // iterate through block-statements
                for(int i = 1; i < conditional.children.size(); i+=2) {
                        IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getCondLabel()));
                        conditional.children.get(i).accept(this);

                        if(i != conditional.children.size() - 1) {
                                IRs.addExpr(new IRExpression(Instruction.JMP, endLabel));
                        }

                }

                // catch the else condition, which falls through
                if(conditional.children.size() % 2 != 0) {
                        IRs.addExpr(new IRExpression(Instruction.LABEL, IRs.getCondLabel()));
                        conditional.children.get(conditional.children.size() - 1).accept(this);
                }

                // throw in the end label
                IRs.addExpr(new IRExpression(Instruction.LABEL, new Token("_endCond" + IRs.getEndID(), TokenType.TK_IDENTIFIER)));

                IRs.stateFlag = 0;
                IRs.endCondScope();

                // no need to return
                return null;
        }

        @Override
        public Token visitExpressionStatement(Statement.ExpressionStatement expr) {
                // visit children and break down expression

                iterator(expr);

                return null;
        }

        @Override
        public Token visitGotoLabel(Statement.gotoLabel label) {
                // just add a label, no children to iterate through

                IRs.addExpr(new IRExpression(Instruction.LABEL, label.label));

                return null;
        }

        @Override
        public Token visitGoto(Statement.gotoStatement statement) {
                // jump to a label, no children to iterate through

                IRs.addExpr(new IRExpression(Instruction.JMP, statement.label));

                return null;
        }

        @Override
        public Token visitVarDecl(Declaration.varDeclaration decl) {
                // no declaration to add, just iterate through children

                iterator(decl);

                return null;
        }

        @Override
        public Token visitFunDecl(Declaration.funDeclaration decl) {
                // create a label for each function, iterate through children

                IRs.addExpr(new IRExpression(Instruction.FUNC, decl.functionID));

                iterator(decl);

                return null;
        }

        @Override
        public Token visitVariable(Declaration.Variable decl) {
                // if variable has a value, add the expression, otherwise load only the variable

                if(decl.hasChildren()) {
                        IRs.addExpr(new IRExpression(Instruction.LOAD, decl.children.get(0).accept(this), decl.variableID));
                } else {
                        IRs.addExpr(new IRExpression(Instruction.LOAD, decl.variableID));
                }

                return null;
        }

        @Override
        public Token visitParameter(Declaration.Parameter decl) {
                // load each parameter, no children to iterate through

                IRs.addExpr(new IRExpression(Instruction.LOADP, decl.paramID));

                return null;
        }

        @Override
        public Token visitTypeDecl(Declaration.TypeDeclaration decl) {
                // insert label and iterate through children of C-enumeration

                IRs.addExpr(new IRExpression(Instruction.LABEL, decl.enumType));

                iterator(decl);

                return null;
        }

        @Override
        public Token visitProgram(Program program) {
                // iterate through program children

                iterator(program);

                return null;
        }

        /**
         * Iterates through children for container nodes, when no return value is required by parent
         * @param node
         */
        public void iterator(Node node) {
                if(node.hasChildren()) {
                        for (int i = 0; i < node.children.size(); i++) {
                                node.children.get(i).accept(this);
                        }
                }
        }
}
