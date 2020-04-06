# Language Specification
The language features required for the compiler project is addressed in the table below, accompanied by descriptions, and whether the feature has been implemented or supported by our team.

Each of the developed stages relevant to the compilation process are provided a coolumn in correlation to a feature. The development states for each stage specific to each C-Language feature can be categorized as *pending* *(not started)*, developing *(in progress)*, or **complete**. If a spot is empty, we have not approached that feature in the preceding stage.

### Documentation Table of Contents:
* [Program Overview and Usage](../README.md)
* [Design Discussion, Limitations, and Tradeoffs](design_spec.md)
* [Language Specifications](language_spec.md) (here)


### Required Support Status

Feature | Full Support | Stage 1: Tokenize | Stage 2: Parse | Stage 3: IR Generation
--- | :---: | :---: | :---: | :---:
Identifiers | **YES** | **complete** | **complete** | **complete**
Variables | **NO** | **complete** | **complete** | **complete**
Functions | **NO** | **complete** | **complete** | **complete**
Keywords | **NO** | **complete** | **complete** | **complete**
Arithmetic Exp. | **YES** | **complete** | **complete** | **complete**
Assignments | **YES** | **complete** | **complete** | **complete**
Boolean Exp. | **YES** | **complete** | **complete** | **complete**
goto Statements | **YES** | **complete** | **complete** | **complete**
If/Else Control Flow | **YES** | **complete** | **complete** | **complete**
Unary Operators | **YES** | **complete** | **complete** | **complete**
Return Statements | **YES** | **complete** | **complete** | **complete**
Break Statements | **YES** | **complete** | **complete** | **complete**
While Loop | **YES** | **complete** | **complete** | **complete**

### Optional Support Status
In the case of optional feature support, *N/A* indicates not-attempted but does not mean will not attempt. We may attempt an implementation at our discretion.

Feature | Full Support | Stage 1: Tokenize | Stage 2: Parse | Stage 3: IR Generation
--- | :---: | :---: | :---: |  :---:
Other Types | **NO** | *N/A* | *N/A* | *N/A*
++, -=, +=, *=, /= | **YES** | **complete** | **complete** | **complete**
for-loops | **YES** | **complete** | **complete** | **complete**
binary-operators | **YES** | **complete** | **complete** | **complete**
switch-statements | **NO** | *N/A* | *N/A* | *N/A*
type-declarations (Enumerations) | **YES** | **complete** | **complete** | *pending*

# Supported Tokens
Below is a table containing tokens supported by the lexer/scanner component of the compiler. Although a token may be supported this ***DOES NOT*** mean that we will full support or implement the C- language feature associated with that token. We reserve the right to remove or ignore support for tokens at our discretion although within the bounds of the assignment requirement.

*ID*  | *Token*
--- | :---:
**TK_PLUSEQ** | +=
**TK_MINUSEQ** | -=
**TK_STAREQ** | *=
**TK_SLASHEQ** | /=
**TK_EQEQUAL** | ==
**TK_RPAREN** | )
**TK_LPAREN** | (
**TK_RBRACE** | }
**TK_LBRACE** | {
**TK_RBRACKET** | ]
**TK_LBRACKET** | [
**TK_PLUS** | +
**TK_MINUS** | -
**TK_STAR** | *
**TK_SLASH** | /
**TK_SEMICOLON** | ;
**TK_COLON** | :
**TK_QMARK** | ?
**TK_BANG** | !
**TK_DOT** | .
**TK_COMMA** | ,
**TK_DQUOTE** | "string"
**TK_KEYWORDS** | for, while, break, ...
**TK_TYPE** | int, long, short
**TK_IDENTIFIER** | foo, main, c, ...
**TK_NUMBER** | 1,2,3,...
**TK_EQUALS** | =
**TK_LESS** | <
**TK_GREATER** | >
**TK_LESSEQ** |<=
**TK_GREATEREQ** | >=

##### Comment Support:
Comments are supported in both their multi-line `/*...*/` and single line `//...` implementation.

# Compiler Grammar <sup>*(as of 2/16/2020)*</sup>
Our grammar is classed into production rule categories. The production rules located here are a reflection of what we deem to be implemented, but are **NOT** finalized <sup>*(yet)*</sup>.

**BOLD** terms are terminal/tokens.

---
### Program

>declarationList &rightarrow; declarationList | declaration

>declaration &rightarrow; varDeclaration | funDeclaration

---

### Declarations

#### varDeclaration

>varDeclaration &rightarrow; typeSpecifier varDecList;

>scopedVarDeclaration &rightarrow; typeSpecifier varDecList

>varDecList &rightarrow; varDecList **,** varDecId | varDecId

>varDecInit &rightarrow; varDecId | varDecId **=** expression

>varDecId &rightarrow; **ID**

>typeSpecifier &rightarrow; **int** | **bool** | **char**

#### funDeclaration

>funDeclaration &rightarrow; typeSpecifier *ID* (params) statement | *ID* (params) statement

>params &rightarrow; paramList | **ε**

>paramList &rightarrow; paramlist **;** paramTypeList | paramTypeList

>paramTypeList &rightarrow; typeSpecifier paramIdList

>paramIdList &rightarrow; paramIdList **,** paramId | paramId

>paramId &rightarrow; **ID** | **ID** [constant]

---
### Statement

>statement &rightarrow; expressionStatement | blockStatement | returnStatement

>expressionStatement &rightarrow; expression **;** | **;**

>blockStatement &rightarrow; **{** localDeclarations statementList **}**

>localDeclarations &rightarrow; localDeclarations scopedVarDeclaration | **ε**

>statementList &rightarrow; statementList statement | **ε**

>selectionStatement &rightarrow; **if** **(** expression **)** blockStatement

>iterationStatement &rightarrow; **while** expression **do** statement

>returnStatement &rightarrow; **return**; | **return** expression **;**

>breakStatement &rightarrow; **break;**

---
### Expression

>expression &rightarrow; BinExpression | UnaryExpression | call | constant

>BinExpression &rightarrow; expression binOp expression

>binOp &rightarrow; <b> + | - | * | / | % </b>

>unaryExpression &rightarrow; unaryop expression

>unaryOp &rightarrow; **- | * | ?**

>call &rightarrow; **ID** ( args )

>args &rightarrow; argList | **ε**

>argList &rightarrow; argList **,** expression | expression

>constant &rightarrow; **NUMCONST**

# Intermediate Representation Instructions
| Instruction | Meaning |
|:----------:|:----------|
|**ADD**|Add two values and store into a variable|
|**SUB**|Subtract two values and store into a variable|
|**MUL**|Multiple two values and store into a variable|
|**DIV**|Divide two values and store into a variable|
|**ASSIGN**|Assign a value into a variable|
|**LABEL**|Create a label for jumping|
|**JMP**|Jump to label operation|
|**RET**|Return the final value specified|
|**CALL**|Call a function|
|**NOP**|No operation|
|**LOAD**|Load a variable or parameter|
|**BREAK**|Break out of loop|
|**INC**|Increment a value by one|
|**DEC**|Decrement a value by one|
|**NOT**|Logical `NOT` a variable|
|**AND**|Logical `AND` a variable|
|**OR**|Logical `OR` a variable|
|**EQUAL**|Evaluate two values if equivalent and jump to label|
|**GREQ**|Evaluate a greater-than-equal-to condition and jump to label|
|**LSEQ**|Evaluate a less-than-equal-to condition and jump to label|
|**GRTR**|Evaluate a greater-than condition and jump to label|
|**LESS**|Evaluate a less-than condition and jump to label|
|**EVAL**|Evaluate to boolean values and jump to label|
