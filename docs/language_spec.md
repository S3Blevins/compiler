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
Below is a table containing tokens supported by the lexer/scanner component of the compiler. Although a token may be supported this ***DOES NOT*** mean that we will full support or implement the `C- language` feature associated with that token. We reserve the right to remove or ignore support for tokens at our discretion although within the bounds of the assignment requirement.

*ID*  | *Token*
--- | :---:
**TK_PLUSEQ** | +=
**TK_MINUSEQ** | -=
**TK_STAREQ** | *=
**TK_SLASHEQ** | /=
**TK_EQEQUAL** | ==
**TK_NEQUAL** | !=
**TK_PPLUS** | ++
**TK_MMINUS**| --
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
**TK_LOGOR** | \|\|
**TK_LOGAND** | &&
**TK_BOOL** | true, false

##### Comment Support:
Comments are supported in both their multi-line `/*...*/` and single line `//...` implementation.

# Compiler Grammar
Our grammar is classed into production rule categories. The production rules located here are a reflection of what we deem to be implemented.

**BOLD** terms are terminal/tokens.

---
### Program

>declarationList &rightarrow; declarationList | declaration

>declaration &rightarrow; varDeclaration | funDeclaration

>enum &rightarrow; **ID**, enum | **ID** = constant, enum | **ε**

---

### Declarations

#### varDeclaration

>varDeclaration &rightarrow; typeSpecifier varDecList;

>scopedVarDeclaration &rightarrow; typeSpecifier varDecList

>varDecList &rightarrow; varDecList **,** varDecID | varDecID

>varDecInit &rightarrow; varDecID | varDecID **=** expression

>varDecID &rightarrow; **ID**

>typeSpecifier &rightarrow; **int**

#### funDeclaration

>funDeclaration &rightarrow; typeSpecifier *ID* (params) statement | *ID* (params) statement

>params &rightarrow; paramList | **ε**

>paramList &rightarrow; paramlist **;** paramTypeList | paramTypeList

>paramTypeList &rightarrow; typeSpecifier paramIDList

>paramIDList &rightarrow; paramIDList **,** paramID | paramID

>paramID &rightarrow; **ID**

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

>binOp &rightarrow; <b> + | - | * | / | = | == | += | -= | *= | /=  </b>

>unaryExpression &rightarrow; unaryop expression

>unaryOp &rightarrow; **- | * | ? | ++ | --**

>call &rightarrow; **ID** ( args )

>args &rightarrow; argList | **ε**

>argList &rightarrow; argList **,** expression | expression

>constant &rightarrow; **NUMCONST**

# Intermediate Representation Instructions

For example sources, ie. `SRCN`, the `N` denotes the number position of the source value. If the instruction can have variable number of sources, the `SRC[N]` denotes an optional number of sources, where `N` denotes the number of option sources (zero to `N`).

| Instruction | Meaning | Example
|:---:|:---|:---|
|**ADD**|Add two values and store into a variable| `ADD SRC1, SRC2, DEST`
|**SUB**|Subtract two values and store| `SUB SRC1, SRC[1], DEST`
|**MUL**|Multiple two values and store| `MUL SRC1, SRC2, DEST`
|**DIV**|Divide two values and store| `DIV SRC1, SRC2, DEST`
|**ASSIGN**|Assign a value into a variable| `ASSIGN SRC1, DEST`
|**LABEL**|Create a label for jumping| `LABEL DEST`
|**JMP**|Jump to label operation| `JMP DEST`
|**RET**|Return the final value specified| `RET dest`
|**CALL**|Call a function| `CALL func SRC[8], DEST`
|**NOP**|No operation| `NOP`
|**LOAD**|Load a variable or parameter| `LOAD SRC1`
|**BREAK**|Break out of loop| `BREAK`
|**INC**|Increment a value by one| `INC SRC1`
|**DEC**|Decrement a value by one| `DEC SRC1`
|**NOT**|Logical `NOT` a variable| `NOT SRC1`
|**AND**|Logical `AND` a variable| `AND SRC1, SRC2, DEST`
|**OR**|Logical `OR` a variable| `OR SRC1, SRC2, DEST`
|**EQUAL**|Check equivalent cond. and jump to label| `EQUAL SRC1, SRC2, DEST`
|**GREQ**|Check a grtr-than-equal and jump to label| `GREQ SRC1, SRC2, DEST`
|**LSEQ**|Check a less-than-equal cond. and jump to label| `LSEQ SRC1, SRC2, DEST`
|**GRTR**|Check a grtr-than cond. and jump to label| `GRTR SRC1, SRC2, DEST`
|**LESS**|Check a less-than cond. and jump to label| `LESS SRC1, SRC2, DEST`
|**EVAL**|Check to boolean values and jump to label| `EVAL SRC1, SRC2, DEST`
