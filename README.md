# [Spring 2020] CSE423 Compiler Project
  
### Summary
Our Compiler is called JxC which stands for Java ... (C)ompiler which supports a subset of the C programming language. This project is intended for the Spring 2020 semester of CSE423 at New Mexico Institute of Mining and Technology. This document will serve as a constantly updated instructional document for running the compiler project with a description of the contents of our repo.
##### Team members (in alphabetical order):
* Garrett Bates (gbbofh)
* Sterling Blevins (S3Blevins)
* Damon Estrada (damon-estrada)
* Jacob Santillanes (Ulteelectrom)

##### Working Progress: `Stage 1 (Parser) of 3`

We are currently working on implementing a fully featured parser with a "pretty-printed" syntax tree based on the [C- grammar](http://marvin.cs.uidaho.edu/Teaching/CS445/c-Grammar.pdf).

### Documentation Navigation:
* [Compilation and Usage](#compilation-and-usage)
* [Program Overview](#program-overview)
* [Design Discussion, Limitations, and Tradeoffs (design_spec.md)](docs/design_spec.md)
* [Language Specifications (language_spec.md)](docs/language_spec.md)

---
# Compilation and Usage

### Compilation of the Project
##### Compatible with Java JDK 11
Currently our project is set up to be easily imported directly into the IntelliJ IDE with the `src` and `lib` folders being designated as sources. Just build the project through the menu.

> The complexity of the project is increasing but currently our project can be compiled using `javac *.java` from within the lexer package in the `src` directory of our compiler project. We'll have a more formalized compilation method from source on a later date.

Our plan is for every week for the duration of the project, a running build will be available for download in the `bin` directory for ease of use.

[See here for a list of dependencies included in our build.](#dependencies-and-attributions)

### Usage
Please download (and untar/unzip) our repository or clone it. You may relocate the download or open our project in your OSs downloads folder. Once downloaded and untar/unziped, natigate to `compiler-master/bin/JxC_2_16_2020/` through your terminal. Please ensure you have a version of Java 8 or higher. If you do not, you can install a version [here](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html). To run JxC, please use
  <code>
    java -jar JxC_2_16_2020.jar -f "FILE.c" -p -t
  </code>
Please make sure your `.c` files are in the same directory or else you'll need to specifiy the path to the test file. For example:
<code>
  java -jar JxC_2_16_2020.jar -f ~/Desktop/test1.c -p -t
</code>

Our current iteration of the program supports the following arguments in addition to the `.c` file being read in.

*Argument* | *Long Output* | *Description*
--- | --- | :---
**-h** | -help | provides a helpful description on the usage of the program
**-t** | -token | provides tokens present in the .c file fed into the compiler
**-to** | -tokenout | prints tokens present in the .c file fed into the compiler to output file and command line
**-p** | -parsetree | provides parse tree used 
**-po** | -parsetreeout | prints parse tree used to an output file
**-f** | -file | input .c file to read from and tokenise 

**NOTE:** The argument **-t** is on by default

# Program Overview
In our current standing, JxC is capable of reading in a file and tokenizing much of the *C- language*. Command line arguments for our compiler have been integrated to ask for certain output from our compiler. Our parser is functioning at the basic level required but has a lot of implementation already setup to support more of the *C- language* grammar but has not been fully linked or polished yet.

### Supported Tokens
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

## The JxC Grammar (as of 2/16/2020) 
Our grammar is classed into production rule categories, and those categories may be further classed, with our production rules finally being used to categorize our tokens.

The following categories are arranged in precedence with none of the categories above being used in categories below.

---
### Program

declarationList &rightarrow; declarationList | declaration

declaration &rightarrow; varDeclaration | funDeclaration

---

### Declarations

#### varDeclaration

varDeclaration &rightarrow; typeSpecifier varDecList;

scopedVarDeclaration &rightarrow; typeSpecifier varDecList

varDecList &rightarrow; varDecList, varDecId | varDecId

varDecId &rightarrow; **ID**

typeSpecifier &rightarrow; **int** | **bool** | **char**

#### funDeclaration

funDeclaration &rightarrow; typeSpecifier *ID* (params) statement | *ID* (params) statement

params &rightarrow; paramList | **ε**

paramList &rightarrow; paramlist;paramTypeList | paramTypeList

paramTypeList &rightarrow; typeSpecifier paramIdList

paramIdList &rightarrow; paramIdList, paramId | paramId

paramId &rightarrow; **ID** | **ID**[ constant ]

---
### Statement

statement &rightarrow; expressionStatement | compoundStatement | returnStatement

expressionStatement &rightarrow; expression; | ;

compoundStatement &rightarrow; { localDeclarations statementList }

localDeclarations &rightarrow; localDeclarations scopedVarDeclaration | **ε**

statementList &rightarrow; statementList statement | **ε**

returnStatement &rightarrow; **return**; | **return** expression;

---
### Expression

expression &rightarrow; relExpression

relExpression &rightarrow; sumExpression

sumExpression &rightarrow; sumExpression sumop mulExpression | mulExpression

sumop &rightarrow; + | -

mulExpression &rightarrow; mulExpression mulop unaryExpression | unaryExpression

mulop &rightarrow; * | / | %

unaryExpression &rightarrow; unaryop unaryExpression | factor

unaryop &rightarrow; - | * | ?

factor &rightarrow; immutable | mutable

mutable &rightarrow; **ID**

immutable &rightarrow; expression | call | constant

call &rightarrow; **ID** ( args )

args &rightarrow; argList | **ε**

argList &rightarrow; argList, expression | expression

constant &rightarrow; **NUMCONST**

##### Comment Support:
Comments are supported in both their multi-line `/*...*/` and single line `//...` implementation.

## Dependencies and Attributions

* [*Appache Commons CLI Library*](http://commons.apache.org/proper/commons-cli/) - Used in cli-interface branch (not fully implemented)

* [*JUnit4*](https://junit.org/junit4/) - Used to write repeatable unit test for methods to ensure code base changes do not introduce new bugs.

