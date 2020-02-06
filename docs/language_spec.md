# Language Specification
The language features required for the compiler project is addressed in the table below, accompanied by descriptions, and whether the feature has been implemented or supported by our team. The development states for each feature can be categorized as *N/A* (not-attempted), *pending* (not started), *developing* (in progress), or *completed*.

### Required Support

Feature | Fully Supported | Development
--- | :---: | :---:
Identifiers | **NO** | *developing*
Variables | **NO** | *developing*
Functions | **NO** | *developing*
Keywords | **NO** | *developing*
Arithmetic Exp. | **NO** | *developing*
Assignments | **NO** | *developing*
Boolean Exp. | **NO** | *pending*
goto Statements | **NO** | *developing*
If/Else Control Flow | **NO** | *developing*
Unary Operators | **NO** | *developing*
Return Statements | **NO** | *developing*
Break Statements | **NO** | *developing*
While Loop | **NO** | *developing*

### Optional Support

Feature | Fully Supported | Development
--- | :---: | :---:
Other Types | **NO** | *N/A*
++, -=, +=, *=, /= | **NO** | *developing*
for-loops | **NO** | *N/A*
binary-operators | **NO** | *N/A*
switch-statements | **NO** | *N/A*

### Functionality and Limitations
The compiler is insofar built to only read in a file, and provide a list of tokens to the user. All of the required `C-` functions are not fully implemented, though most of them are capable of being recognized as tokens such as like keywords, and symbols.

Our team has left open the option of supporting additional number types, and support for binary-operators but nothing is concrete.

Again, although a token may be supported this ***DOES NOT*** mean that we will full support or implement the C- language feature associated with that token. We reserve the right to remove or ignore support for tokens at our discretion although within the bounds of the assignment requirement.

### Documentation Table of Contents:
* [Program Overview and Usage](../README.md)
* [Design Discussion, Limitations, and Tradeoffs](design_spec.md)
* [Language Specifications](language_spec.md) (here)
