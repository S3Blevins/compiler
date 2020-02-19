# Design Choices and Process
The team chose Java as the language of choice for our compiler. The main reasoning behind this is that each of us has a familiarity with the language, easy I/O implementation, data structures, and (ultimately) garbage collection.

### Documentation Table of Contents:
* [Program Overview and Usage](../README.md)
* [Design Discussion, Limitations, and Tradeoffs](design_spec.md)  (here)
* [Language Specifications](language_spec.md)

## Assignment 1 *(Front End)*
A more in-depth explanation of how the implementation details of each section of the compiler came to be.

### Stage 0: Command Line Interface
Because Java does not have a built-in method of managing command line arguments, a library was chosen to handle this user-facing part of the compiler. Rather than hand-roll it ourselves the option of using a library that is well tested and less error prone seemed appropriate. The library chosen was the [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/) library, mainly due to the fact that it requires no other dependencies.

See [here](../README.md#usage) for details on supported arguments.

### Stage 1: Scanner
The scanner reads the file into a String Array, matching the beginning of the line against an ordered set of regular expressions mapped with corresponding token type (enumeration). If a match is found, the token is added to a structure and stored, with the line being truncated from the front of the string. If a token is not matched, the program errors with the corresponding line number and character which caused the error. The precedence order of the token regular expression and enumeration does matter in some cases, with tokens like `<=` or `>=` occurring before `=`.

Although maybe not entirely efficient, being able to keep track of the line number was a feature we chose to retain for this initial part of the compiler.

See [here](language_spec.md#supported-tokens) for supported tokens.

### Stage 2: Parser
We decided that the parser, designed with a top-down recursive approach made practical sense for ease of implementation and understanding. Designating a method for the processing of each major section of the grammar (Program, Declarations, Statements, and Expressions), we built our parse tree using an abstract class node, which allows us to process and print the tree in either a general or specific manner depending on our requirement.

Utilizing the Pratt Parsing approach, we provide the a precedence to tokens when processing an expression. A `Left Denotative` and `Null Denotative` consideration is given to each relevant token, handling through delegate classes through Java anonymous functions (no function pointers here) for handling the processing.

The parser is not quite finished but is close to being as fully featured as we can get within our given time frame. Below is an example of how the parser outputs based on a nonsensical C-program that is only syntactically correct.

See below for functionality and limitations.

![](doc_images/parse_tree.png)

See [here](language_spec.md#program) for an in-depth look at our grammar.

# Functionality and Limitations
The compiler is insofar built to only read in a file, and provide a list of tokens, and a basic modified parse tree to the user. All of the required `C-` functions are not fully implemented, though most of them are capable of being recognized as tokens such as keywords, and symbols.

We can support variable declarations (not definitions/initializations), while loops, if, if-else, and return statements and that is about it.
