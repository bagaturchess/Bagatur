# Purpose

This project aims to act as JNI bridge to nnue-probe code located at https://github.com/dshawul/nnue-probe.

Currently it is compiled under Windows only and JNNUE.dll is available in the root of the project.
The compilation command, which was performed in src directory, is located in /src/info.txt

The library is compatible with the nn-6b4236f2ec01.nnue network, uploaded by user vdv on 21-05-01 10:24:00 here: https://tests.stockfishchess.org/nns.
This is the latest network recognized successfully by the C code, provided by dshawul.
Currently, the newest networks cannot be loaded with this library.

To give it a try, you could run the main function inside bagaturchess.nnue.ProbeMain.

# References & Thanks

1. https://www.youtube.com/watch?v=59Fp4JVNob0

2. https://github.com/dshawul/nnue-probe

3. https://tests.stockfishchess.org/nns

4. https://hxim.github.io/Stockfish-Evaluation-Guide/



