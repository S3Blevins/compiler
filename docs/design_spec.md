# Design Choices and Process
The team chose Java as the language of choice for our compiler. The main reasoning behind this is that each of us has a familiarity with the language, easy I/O implementation, data structures, and (ultimately) garbage collection.

### Documentation Table of Contents:
* [Program Overview and Usage](../README.md)
* [Design Discussion, Limitations, and Tradeoffs](design_spec.md)  (here)
* [Language Specifications](language_spec.md)

## Front-End
### Stage 0: Command Line Interface
Because Java does not have a built-in method of managing command line arguments, a library was chosen to manage this user-facing part of the compiler. The library chosen was the [Apache Commons CLI](http://commons.apache.org/proper/commons-cli/) library, mainly due to the fact that it requires no other dependencies.

### Stage 1: Scanner
The scanner reads the file into a String Array, matching the beginning of the line against a set of regular expressions mapped with corresponding token type (enumeration). If a match is found, the token is added to a structure and stored, with the line being truncated from the front of the string. If a token is not matched, the program errors with the corresponding line number and character which caused the error.

Although maybe not entirely efficient, being able to keep track of the line number was

The precedence of the tokens does matter in some cases, with tokens like `<=` or `>=` occurring before `=`.

### Stage 2: Parser
We have decided that the parser should take a top-down approach. The consensus was that starting from the top and descending recursively made more practical sense. Although not fully implemented, we are on our way to having the scanner output a parse tree.
