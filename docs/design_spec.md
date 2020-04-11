re-declaredBeginning# Design Choices and Process
The team chose Java as the language of choice for our compiler. The main reasoning behind this is that each of us has a familiarity with the language, easy I/O implementation, data structures, and (ultimately) garbage collection.

### Documentation Table of Contents:
* [Program Overview and Usage](../README.md)
* [Design Discussion, Limitations, and Tradeoffs](design_spec.md)  (here)
* [Language Specifications](language_spec.md)

## Assignment 1 *(Front End)*
A more in-depth explanation of how the implementation details of each section of the compiler.

### Stage 0: Command Line Interface
Because Java does not have a built-in method of managing command line arguments, a library was chosen to handle this user-facing part of the compiler. Rather than hand-roll it ourselves the option of using a library that is well tested and less error prone seemed appropriate. The library chosen was the [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/) library, mainly due to the fact that it requires no other dependencies.

See [here](../README.md#usage) for details on supported arguments.

### Stage 1: Scanner
The scanner reads the file into a String Array, matching the beginning of the line against an ordered set of regular expressions mapped with corresponding token type (enumeration). If a match is found, the token is added to a structure and stored, with the line being truncated from the front of the string. If a token is not matched, the program errors with the corresponding line number and character which caused the error. The precedence order of the token regular expression and enumeration does matter in some cases, with tokens like `<=` or `>=` occurring before `=`.

Although maybe not entirely efficient, being able to keep track of the line number was a feature we chose to retain for this initial part of the compiler.

See [here](language_spec.md#supported-tokens) for supported tokens.

### Stage 2.A: Parser
We decided that the parser, designed with a top-down recursive approach made practical sense for ease of implementation and understanding. Designating a method for the processing of each major section of the grammar (Program, Declarations, Statements, and Expressions), we built our parse tree using an abstract class node, which allows us to process and print the tree in either a general or specific manner depending on our requirement.

Utilizing the Pratt Parsing approach, we provide the a precedence to tokens when processing an expression. A `Left Denotative` and `Null Denotative` consideration is given to each relevant token, handling through delegate classes through Java anonymous functions (no function pointers here) for handling the processing.

See [here](https://books.google.com/books?id=gJrmszNHQV4C&lpg=PA134&ots=rNW-wxV7lg&dq=null%20denotative%20vs%20left%20denotative&pg=PA134#v=onepage&q=null%20denotative%20vs%20left%20denotative&f=false) for more information on the Pratt Parser structure.

The parser uses a visitor pattern for printing by allowing us to traverse the parse tree generically. This visitor pattern is again utilized in the IRBuilder classes for Assignment 2.

See below for functionality and limitations.

![](doc_images/parse_tree.png)

See [here](language_spec.md#program) for an in-depth look at our grammar.

## Assignment 2 *(IR Generation)*
A more in-depth explanation of the implementation of the intermediate representation stages.

### Stage 2.B: Symbol Table
The symbol table involves a set of hash-maps with the unique variable name as a key, and the respective variable type serving as the value. The structure is built as a wrapper class with "pointers" to lower scoped symbol tables. With each block statement (encompassed in braces `{` and `}`), a new table is created as a child of the outer block's symbol table.

The symbol table logic includes functions that add a new table, add a new key/value pair to the current table, retrieve the size of one of the tables, error checks to make sure already declared variables in outer scopes are not re-declared in inner scopes.

### Stage 3: IR Generation

The compiler holds an enumerated class that keeps track of instructions that are x86 compliant. This is utilized when building an IR object (the first parameter of the IR object).

IR Expressions serve as the basis for our IR object creation. Most IR expressions can consist up to four parameters `INSTRUCTION`, `SOURCE1`, `SOURCE2`, `DESTINATION`, with the exception of the `CALL` instruction which can support up to 8 (our own restriction) source value for parameters. The different IR expressions can be represented in the following format:

```
(INSTRUCTION SOURCE1, SOURCE2, DESTINATION)

(INSTRUCTION SOURCE1, DESTINATION)

(INSTRUCTION DESTINATION)

(INSTRUCTION)
```

The instructions supported can be seen [here.](language_spec.md#intermediate-representation-instructions)

The IR expressions are made using the IRBuilder class, which utilizes the visitor pattern to traverse the parse tree in a similar fashion to how the parse tree is printed. Each node type has an associated method which creates an IR expression from the IR expression class and then adds it to the linear IR expression list (described below). Most expression nodes return the temporary variable of where the expression previously had been evaluated into.

The IRList class behaves as a wrapper class that keeps track of every IR expression object created in addition to generating new labels when necessary. It keeps track of an ArrayList which represents our IR in a linear fashion which will be also help optimize the IRList in the next stage.

Below is an example of some nonsensical code with our equivalent intermediate representation. The IR is almost assembly like in form.

![](doc_images/ir_code.png)

# Functionality and Limitations
The compiler is built read in a `c` file, and an IR file containing a series of IR expressions ready to be converted to assembly in the next assignment.

Currently our compiler allows for the print out (to console) and write out (to file) of the tokens list, parse tree, symbol table, and IR expression list.

Some restrictions we place on the `C-code` compiled by our program:
* Function calls are limited to 8 parameters
* Function prototypes are **NOT** supported
* The only supported type is `int`, and pointers are not supported
* Braces are required for all blocks (even one line blocks)
* Imports and macros are **NOT** supported.
* Switch statements, and binary operators are **NOT** supported
* Enumeration (specifically typedef defined) are tokenized, parsed, and generated in the IR stage. However we have not implemented a way to reference them yet to be utilized (since user must provide variable name of the new type created). See example above.
* We do **NOT** support return checking. Meaning if a function of type int returns an integer or if a return statement exists.
* We do **NOT** support the continue keyword.


Although somewhat limited, our compiler should meet most of the required specifications, albeit with the occasional hiccup.
