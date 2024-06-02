# Purpose

This sub-project aims to act as Java NNUE framework for both acting as Java porting as well as JNI bridge of the C code located at https://github.com/dshawul/nnue-probe.
So, Bagatur or any other Java chess engine could integrate NNUE evaluation function in a Java native way or via JNI calling the C functions.

# Java porting
After some hard times with bytes coding, types and indexes, finally the C code is ported to Java and works correctly without the logic for incremential updates.
The incremential updates logic is in todo list and might be implemented in future.
The Java porting NNUE works much faster than the JNI version. About 4 times faster.

# JNI bridge
It is compiled under Windows and Linux and JNNUE.dll and libJNNUE.so are available in the root of the project.
The compilation commands, which was performed in src directory, are located in /src/info.txt

# Recognized NNUE files versions
The library is compatible with the nn-6b4236f2ec01.nnue network, uploaded by user vdv on 21-05-01 10:24:00 here: https://tests.stockfishchess.org/nns.
This is the latest network recognized successfully by the C code, provided by dshawul and by the ported Java code.
Currently, the newest networks cannot be loaded with this library.

# Run it via main function
To give it a try, you could run the main functions inside bagaturchess.nnue: NNUEMain for Java porting and ProbeMain for JNI version.
Have in mind that both versions may provide a bit different evaluations just because of NNUE biases.

# Test evaluation with Bagatur engine
In order to test NNUE as Bagatur evaluation function on code level, there is special evaluation configuration classes
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/java_eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.java_eval.EvaluationConfig</a>
and
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/jni_eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.jni_eval.EvaluationConfig</a>
under <a href="https://github.com/bagaturchess/Bagatur/tree/master/LearningImpl">LearningImpl</a> sub-project.
When Java porting is used in production, Bagatur plays with around 2-3 times slower NPS speed and is stronger than classic version with 50 Elo strength.
When JNI version is used in production, Bagatur plays with around 12 times slower NPS speed and is comparable to classic version in Elo strength.

# How to run it out of the box
Having in mind the strongest version using the NNUE Java porting code, now NNUE is used as evaluation function and the classic version is no more supported with its custom evaluation function.
REM Since version 4.0, there are also separate exe and bat files in the distribution for running the NNUE version: e.g. Bagatur_NNUE_1_core.exe and /bin/Bagatur_NNUE_1core.bat. 
The downloads of Bagatur versions could be found in the release section: https://github.com/bagaturchess/Bagatur/releases

# References & Thanks

1. https://github.com/dshawul/nnue-probe

2. https://tests.stockfishchess.org/nns

3. https://hxim.github.io/Stockfish-Evaluation-Guide/




