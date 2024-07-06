
# stockfish_nnue_probe

## What is it?

A library for probing [Stockfish's NNUEs](https://stockfishchess.org/blog/2020/introducing-nnue-evaluation/). The code for reading parameters and the forward propagation is taken from [Stockfish](https://github.com/official-stockfish/Stockfish) with modifications to allow for easy access. Currently supports C++ and Java!

## C++ Usage Guide
At engine start up, run ```Stockfish::Probe::init(const char *bigNetName, const char *smallNetName)```\
For evaluation, run:\
```Stockfish::Probe::eval(const int pieceBoard[], bool side, int rule50)```\
 or the faster version:\
  ```Stockfish::Probe::eval(const int pieces[], const int squares[], int pieceAmount, bool side, int rule50)```

## Java Usage Guide
Import the file from the package using ```import NNUEBridge.NNUEBridge```\
At engine start up, run ```NNUEBridge.init(String bigNetName, String smallNetName)```\
For evaluation, run:\
```NNUEBridge.evalArray(int[] pieceBoard, int side, int rule50)```\
or the faster version:\
```NNUEBridge.fasterEvalArray(int[] pieces, int[] squares, int pieceAmount, int side, int rule50)``` 

## Function Parameter Explanation 

### ```int eval(const int pieceBoard[], bool side, int rule50)```
**pieceBoard** is an array of size 64, where index 0 is mapped to A1, index 1 is mapped to B1, and index 63 is mapped to H8. The value in each index is 
- 0 for no piece
- 1 for white pawn
- 2 for white knight
- 3 for white bishop
- 4 for white rook
- 5 for white queen
- 6 for white king
- 9 for black pawn
- 10 for black knight
- 11 for black bishop
- 12 for black rook
- 13 for black queen
- 14 for black king

**side** is 0 for white and 1 for black\
**rule50** is the value of the halfmove clock, which should be between 0 and 100

### ```int eval(const int pieces[], const int squares[], int pieceAmount, bool side, int rule50)```
**pieces** is an array of size 32 where the first pieceAmount indexes are set to a piece value\
**squares** is an array of size 32 where each  of the first pieceAmount indexes are the square that the piece in the pieces array are on

Ex. pieces[0] is 6 and squares[0] is 4 to represent a white king on e1\
The order that the pieces and squares do not matter, as long as the 2 arrays match each other\
Piece and square mapping is the same as the above method

**pieceAmount** is the amount of pieces on the board

Examples of both function usage are in main.cpp
## NNUE Installation Guide

Install the nets from the [Stockfish Testing Framework](https://tests.stockfishchess.org/nns) and put them inside your source directory.

Latest supported big network is **nn-b1a57edbea57.nnue** \
Latest supported small network is **nn-baff1ede1f90.nnue**\
Both are needed!

## C++ Installation Guide

Download everything except for the NNUEBridge folder and files, main.cpp, and probe.dll. Put them in your source directory. The probing functions are in probe.cpp. 

## Java Installation Guide

Download probe.dll and the NNUEBridge folder, and put them both in your source directory. The probing functions are in NNUEBridge/NNUEBridge.java.

If you need to recompile the shared library due to putting the file in a package or something similar, install all files and put NNUEBridge.java where you would like it to be

- Run ```javac -h . NNUEBridge.java```
- Replace the NNUEBridge_NNUEBridge.h file with the created file, path_to_new_NNUEBridge.h
- Change all function headers in NNUEBridge_NNUEBridge.cpp to match the ones created in path_to_new_NNUEBridge.h and rename the cpp file to match as well
- Change the SRCS and HEADERS lists in the Makefile to match the new names
- Run ```make -j build ARCH=x86-64-avx2 ```
- Then run ```g++ -lpthread -shared -o probe.dll bitboard.o evaluate.o evaluate_nnue.o half_ka_v2_hm.o misc.o path_to_new_NNUEBridge.o position.o probe.o -I${JAVA_HOME}/include -I${JAVA_HOME}/include/win32```
- Use the new probe.dll that is created

## Future plans
- I plan to implement the Efficiently Updatable part of NNUE in the future as well as clear documentation on how to easily implement it!
- Move some of the code into a singular file as some of the files have only a few lines of code kept from Stockfish
#  
If you would like to report a bug or need help with installation/implementation, create a new issue or reach me at vedantjoshi1409@gmail.com!
