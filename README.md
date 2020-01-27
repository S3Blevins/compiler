# [Spring 2020] CSE423 Compiler Project
### Summary:
This document will serve as a constantly updated instructional document for running the compiler project with a description of the contents of our repo.
##### Team members (in alphabetical order):
* Garrett Bates (gbbofh)
* Sterling Blevins (S3Blevins)
* Damon Estrada (damon-estrada)
* Jacob Santillanes (Ulteelectrom)

---
### Setup and Compilation of the Project:
##### Compatible with Java JDK 11
Currently our project is set up to be easily imported directly into the IntelliJ IDE. Just build the project through the menu.

To compile without the IDE, build via the command `javac *.java` from within the `src` folder

Every week for the duration of the project a running build will be available for download for ease of use.

### Instructional Use:

Currently our compiler is only in the scanner phase so functionality is limited. You can run the program as `java compiler`, with the output labeling the tokens within the main class. (It is still in the testing phase.)

### Limitations:

In it's current implementation, running the program will provide a simple tokenized assessment of the contents within an entry function located in the source code. The token supported so far are:

*ID*  | *Token*
--- | :---:
**TK_TYPE** | *int, long, char*
**TK_IDENTIFIER** | *x, a, p1*
**TK_SEMICOLON** | *;*
**TK_PLUS** | *+*
**TK_MINUS** | *-*

**Next Update:**
The next update will allow for reading in of individual 'c' files and exporting the tokens to a file with an optional argument.
