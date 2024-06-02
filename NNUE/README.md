# Purpose

This sub-project serves as a Java NNUE framework, designed to function both as a Java port and as a JNI bridge for the C code available at https://github.com/dshawul/nnue-probe. This enables Bagatur or any other Java chess engine to integrate the NNUE evaluation function either natively in Java or through JNI calls to the C functions.

# Java porting

After overcoming challenges related to byte coding, types, and indexes, the C code has been successfully ported to Java, operating correctly but without the incremental updates logic. Implementing this logic is on the to-do list for future development. The Java-porting NNUE performs significantly faster, being about four times quicker than the JNI version.

# JNI bridge

The JNI bridge has been compiled for both Windows and Linux, with JNNUE.dll and libJNNUE.so available in the project's root directory. Compilation commands used in the src directory are documented in /src/info.txt.

# Recognized NNUE files versions

The library is compatible with the nn-6b4236f2ec01.nnue network, uploaded by user vdv on 2021-05-01 at 10:24:00, available at https://tests.stockfishchess.org/nns. This is the latest network successfully recognized by both the C code provided by dshawul and the ported Java code. Newer networks are currently not supported by this library.

# Run it via main function

To test the functionality, you can run the main functions inside bagaturchess.nnue: NNUEMain for the Java port and ProbeMain for the JNI version. Note that both versions may yield slightly different evaluations due to NNUE biases.

# Test evaluation with Bagatur engine

To test NNUE as the evaluation function in Bagatur at the code level, special evaluation configuration classes are available:
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/java_eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.java_eval.EvaluationConfig</a>
and
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/jni_eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.jni_eval.EvaluationConfig</a>
These can be found under the <a href="https://github.com/bagaturchess/Bagatur/tree/master/LearningImpl">LearningImpl</a> sub-project. Using the Java porting in production, Bagatur operates at about 2-3 times slower NPS speed but gains approximately 50 Elo in strength compared to the classic version. With the JNI version in production, Bagatur runs about 12 times slower in NPS speed and is 80 Elo weaker in strength compared to the classic version.

# How to run it out of the box

The strongest version utilizes the NNUE Java porting code. As of version 5.0, NNUE is used as the evaluation function, and the classic version with its custom evaluation function is no longer supported. Bagatur versions can be downloaded from the: https://github.com/bagaturchess/Bagatur/releases

# References & Thanks

1. https://github.com/dshawul/nnue-probe

2. https://tests.stockfishchess.org/nns

3. https://hxim.github.io/Stockfish-Evaluation-Guide/




