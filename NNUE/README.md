# Purpose

This sub-project aims to act as JNI bridge to nnue-probe code located at https://github.com/dshawul/nnue-probe.
So, Bagatur or any other Java chess engine could integrate NNUE evaluation function.

Currently it is compiled under Windows only and JNNUE.dll is available in the root of the project.
The compilation command, which was performed in src directory, is located in /src/info.txt

The library is compatible with the nn-6b4236f2ec01.nnue network, uploaded by user vdv on 21-05-01 10:24:00 here: https://tests.stockfishchess.org/nns.
This is the latest network recognized successfully by the C code, provided by dshawul.
Currently, the newest networks cannot be loaded with this library.

To give it a try, you could run the main function inside bagaturchess.nnue.ProbeMain.

In order to test NNUE as Bagatur evaluation function on code level, there is special evaluation configuration class
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.eval.EvaluationConfig</a>
under <a href="https://github.com/bagaturchess/Bagatur/tree/master/LearningImpl">LearningImpl</a> sub-project.
When used in production, Bagatur plays with around 12 times slower NPS speed and is a bit weaker in Elo strength, compared to classic version.

Since version 4.0, there are also separate exe and bat files in the distribution for running the NNUE version: Bagatur_NNUE_1_core.exe and /bin/Bagatur_NNUE_1core.bat. 
The downloads of Bagatur versions could be found in the release section: https://github.com/bagaturchess/Bagatur/releases

# References & Thanks

1. https://www.youtube.com/watch?v=59Fp4JVNob0

2. https://github.com/dshawul/nnue-probe

3. https://tests.stockfishchess.org/nns

4. https://hxim.github.io/Stockfish-Evaluation-Guide/



