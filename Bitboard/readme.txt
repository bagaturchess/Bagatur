
As a chess programmer,

I want to use a representation of the chess board and pieces such as:
* It provides move generation functionality
* It provides Make/Unmake move functionality
* It provides Incheck functionality
* It provides other functionalities
* It works with high performance (nodes per second)

This software is designed to help you in that direction in the context of java programming language.
In order to use it, you just need to instantiate an implementation of the IBitBoard interface.
You can use one of the constructors inside the bagaturchess.bitboard.impl.Board class.

Example of simple main method could be found here: bagaturchess.bitboard.run.Simulate

Digging into Bitboard sub-project one can learn a lot about the "Java Basics",
it is mainly low-level programming - bitwise operations and data structures with limited memory consumption
and operations' execution with a constant complexity.

Have a nice usage ... and feel free to contribute.
