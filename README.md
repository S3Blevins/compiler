# [Spring 2020] CSE423 Compiler Project: JxC

### Summary
Our Compiler is called *JxC* which stands for (**J**)ava (**x** is undefined) (**C**)ompiler which supports a subset of the C-programming language. This document will serve as a constantly updated instructional document for running the compiler project with a description of the contents of our repo.

##### Team members (in alphabetical order):
* Garrett Bates (gbbofh)
* Sterling Blevins (S3Blevins)
* Damon Estrada (damon-estrada)
* Jacob Santillanes (Ulteelectrom)

##### Working Progress: [`Stage 2 (Parser) of Assignment 1`](docs/design_spec.md)

We are currently working on implementing a fully featured parser with a "pretty-printed" syntax tree based on the [C- grammar](http://marvin.cs.uidaho.edu/Teaching/CS445/c-Grammar.pdf).

### Documentation Navigation:
* [Compilation and Usage](#compilation-and-usage)
* [Program Overview](#program-overview)
* [Design Discussion, Limitations, and Tradeoffs (design_spec.md)](docs/design_spec.md)
* [Language Specifications (language_spec.md)](docs/language_spec.md)

---
# Compilation and Usage
##### Compatible with Java JDK 11+ only
Our plan is for each milestone in the project, a running build will be available for download in the `build` directory. We will have not established an easy way for the user to compile on their own (will update with details soon) so now we just provide jars.

Please download (and untar/unzip) our repository or clone it. You may relocate the download or open our project in your OSs downloads folder. Once downloaded and untar/unziped, natigate to `compiler-master/build/` through your terminal. Please ensure you have a version of Java 8 or higher. If you do not, you can install a version [here](https://www.oracle.com/java/technologies/javase-jdk8-downloads.html). To run JxC, please use
  <code>
    java -jar JxC_BUILD_DATE.jar -f "FILE.c" -p -t
  </code>
where **BUILD_DATE** is the lasted build date. Please make sure your `.c` files are in the same directory or else you'll need to specifiy the path to the test file. For example:
<code>
  java -jar JxC_BUILD_DATE.jar -f ~/Desktop/test1.c -p -t
</code>

[See here for a list of dependencies **included** in our build.](#dependencies-and-attributions)  

Our current iteration of the program supports the following arguments in addition to the `.c` file being read in.

*Argument* | *Long Argument* | *Description*
--- | --- | :---
**-h** | **-help** | provides a helpful description on the usage of the program
**-t** | **-token** | tokens in the c-program are displayed in the terminal
**-to** | **-tokenout** | tokens in the c-program are written to a file
**-p** | **-parse** | parse tree from c-program is displayed in the terminal
**-po** | **-parseout** | parse tree from c-program is written to a file.
**-f** | **-file** | input .c file to compile from

**Note** `-po` is currently not supported.

[You can check here for our functionality and limitations of the program.](docs/design_spec.md#functionality-and-limitations)

# Program Overview
The compiler is broken up into three parts (Front End, Intermediate/Optimizer, and Back End), each with their own set of stages. Below is a brief explanation of the current implementation.

## [Assignment 1 *(Front End)*](docs/design_spec.md)
In our current standing, JxC is capable of reading in a file and tokenizing much of the alphabet supported by the *C- language*, in addition to using a modified *C-* grammar to parse the C-program into a custom parse-tree. No additional processing of the C-program has been completed at this time.

>### Stage 0: Command Line Arguments
>The processing of command line arguments for our compiler has been delegated to the use of a library with support for reading in a file, outputting the tokens, and a displaying a parse-tree to the user. See [here](docs/design_spec.md#stage-0-command-line-interface) for a more indepth description.

>### Stage 1: Tokenizing the C-Language
>Tokenizing the C-Language alphabet is implemented using a character-by-character approach, using an ordered set of regular expressions to classify and save the tokens into a list. See [here](docs/design_spec.md#stage-1-scanner) for a more indepth description.

>### Stage 2: Building the Parser
>The program's tokens are then fed into our parser, which uses a top-down recursive approach to structure the C-program into a tree based on a modified `C-` grammar. See [here](docs/design_spec.md#stage-2-parser) for a more indepth description.

# Dependencies and Attributions
No need to install these libraries, they are already included in our build:
* [*Appache Commons CLI Library*](http://commons.apache.org/proper/commons-cli/) - Used in cli-interface branch (not fully implemented)

* [*JUnit4*](https://junit.org/junit4/) - Used to write repeatable unit test for methods to ensure code base changes do not introduce new bugs.
