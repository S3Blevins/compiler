# [Spring 2020] CSE423 Compiler Project

## JxC
### J - Java  
### x - Unknown due to our unknown knowledge of how to build a functioning C  compiler.   
### C - (C)ompiler   

### Summary
This project is intended for the Spring 2020 semester of CSE423 at New Mexico Institute of Mining and Technology. This document will serve as a constantly updated instructional document for running the compiler project with a description of the contents of our repo.
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
Currently our compiler is only completely working in the scanner phase so functionality is limited. You can run the program as `java compiler <test>.c` with <test> being replaced with the name of your `.c` file.

Since the project is being done in Java, we are currently hand-rolling command line arguments due to no native argument handling being present in Java. We are working on implementing a more fully featured cli-interface by using Apache's [*Commons CLI*](http://commons.apache.org/proper/commons-cli/) library.

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
In our current implementation of the compiler, we are capable of reading in a file and tokenizing much of the *C- language*. Command line arguments are in their infancy stage and at the time of this posting and we are unable to parse and construct a syntax tree.

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

### The JxC Grammar (as of 2/16/2020) 
**NOTE:** All terminals are denoted in lowercase and bold words or characters.  

*# Grammar*  | *Production Rules*
--- | :---:
*1. program* | *declaration*
*2. declaration* | *aStatement* / *varDeclaration*
*3. aStatement* | --- 
*4. varDeclaration* | *typeSpecifier* *varDecList* / *varDecInit*
*5. varDecList* | *varDecList*, *varDecInit* / *varDecInit*
*6. varDecInit* | *varDecId* / *varDecId* **=** *expression*
*7. varDecId* | **ID** / **ID** [**NUMCONSTANT**]
*8. typeSpecifier* | **int**

##### Comment Support:
Comments are supported in both their multi-line `/*...*/` and single line `//...` implementation.

## Dependencies and Attributions

* [*Appache Commons CLI Library*](http://commons.apache.org/proper/commons-cli/) - Used in cli-interface branch (not fully implemented)

* [*JUnit4*](https://junit.org/junit4/) - Used to write repeatable unit test for methods to ensure code base changes do not introduce new bugs.

