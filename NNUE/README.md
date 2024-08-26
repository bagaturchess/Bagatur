# Purpose

This sub-project serves as a Java NNUE framework. This enables Bagatur or any other Java chess engine to integrate the NNUE evaluation function.
Currently it has a few flavors:
1. Java NNUE based on <a href="https://github.com/jw1912/bullet">Bullet's</a> simple network example. It is 240 Elo stronger that the classic evaluation function.
2. Older Stockfish NNUE designed to function both as a Java port and as a JNI bridge for the C code available at https://github.com/dshawul/nnue-probe. The pure Java version is 100 Elo stronger that the classic evaluation function and JNI based version is 80 Elo weaker that the classic evaluation function. 
3. Newer Stockfish NNUE probing code based on JNI: <a href="https://github.com/VedantJoshi1409/stockfish_nnue_probe">stockfish_nnue_probe</a>. It is 380 Elo stronger that the classic evaluation function.

# Java NNUE based on <a href="https://github.com/jw1912/bullet">Bullet's</a> simple network example.

## Architecture
It uses the <a href="https://github.com/jw1912/bullet/blob/main/examples/simple.rs">simple</a> network from Bullet release 1.0.0 with the following modifications:
```Rust
...
const HIDDEN_SIZE: usize = 1024;
...
fn main() {
    let mut trainer = TrainerBuilder::default()
        .quantisations(&[QA, QB])
        .input(inputs::ChessBuckets::new([
            0, 0, 1, 1, 2, 2, 3, 3,
            4, 4, 4, 4, 5, 5, 5, 5,
            6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6,
            6, 6, 6, 6, 6, 6, 6, 6,
        ]))
        .output_buckets(outputs::MaterialCount::<8>::default())
        .feature_transformer(HIDDEN_SIZE)
        .activate(Activation::SCReLU)
        .add_layer(1)
        .build();

    let schedule = TrainingSchedule {
        net_id: "simple".to_string(),
        batch_size: 16_384,
        eval_scale: 400.0,
        batches_per_superbatch: 6104,
        start_superbatch: 1,
        end_superbatch: 800,
        wdl_scheduler: WdlScheduler::Constant {
            value: 0.0,
        },
        lr_scheduler: LrScheduler::Step {
            start: 0.001,
            gamma: 0.99,
            step: 1,
        },
        save_rate: 1,
    };

    let settings = LocalSettings {
        threads: 4,
        data_file_paths: vec!["./data/dataset_shuffled.bin"],
        output_directory: "checkpoints",
    };
	
    trainer.run(&schedule, &settings);
}
...
```
So the network looks like (768x7 -> 1024)x2 -> 8, having 7 king buckets on the input layer, one hidden layer with size 1024 and 8 output buckets.

## Training (supervised learning)
The training dataset is taken from https://robotmoon.com/nnue-training-data/ and converted from .binpack to .bin (Bullet format) with <a href="https://github.com/PGG106/Primer">Primer</a>.
More precisely, the dataset was https://huggingface.co/datasets/linrock/test80-2024/blob/main/test80-2024-04-apr-2tb7p.tar.zst.
The positions are also filtered with Primer, so the positions where the side to move is in check and with a best move which is capture were skiped.
Than a training was started with Bullet for 325 superbatches, each with ~100M (16384 x 6104) positions rotated sequentially from the dataset.
A CUDA setup with 1 GPU for the training was used.

## Reading and using the network
The code which handles the netowrk is located here: https://github.com/bagaturchess/Bagatur/tree/master/NNUE/src/bagaturchess/nnue_v2

## Run it via main function
To test the functionality, you can run the main function inside bagaturchess.nnue_v2.ProbeMain_V2

## Test evaluation with Bagatur engine

To test NNUE as the evaluation function in Bagatur at the code level, special evaluation configuration class is available:
<a href="https://github.com/bagaturchess/Bagatur/tree/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v3">bagaturchess.deeplearning.impl_nnue_v3.EvaluationConfig</a>
With Bullet based NNUE Bagatur plays with around 200 Elo stronger than with Older Stockfish NNUE evaluation (Java porting).
So future releases of Bagatur will be based on this implementation.

# Older Stockfish NNUE

## Java porting

After overcoming challenges related to byte coding, types, and indexes, the C code has been successfully ported to Java, operating correctly but without the incremental updates logic.
Implementing this logic is on the to-do list for future development. The Java-porting NNUE performs significantly faster, being about four times quicker than the JNI version.

## JNI bridge

The JNI bridge has been compiled for both Windows and Linux, with JNNUE.dll and libJNNUE.so available in the project's root directory. Compilation commands used in the src directory are documented in /src/c_code/info.txt.

## Recognized NNUE files versions

The library is compatible with the nn-6b4236f2ec01.nnue network, uploaded by user vdv on 2021-05-01 at 10:24:00, available at https://tests.stockfishchess.org/nns. This is the latest network successfully recognized by both the C code provided by dshawul and the ported Java code. Newer networks are currently not supported by this library.

## Run it via main function

To test the functionality, you can run the main functions inside bagaturchess.nnue: NNUEMain for the Java port and ProbeMain for the JNI version. Note that both versions may yield slightly different evaluations due to NNUE biases.

## Test evaluation with Bagatur engine

To test NNUE as the evaluation function in Bagatur at the code level, special evaluation configuration classes are available:
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/java_eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.java_eval.EvaluationConfig</a>
and
<a href="https://github.com/bagaturchess/Bagatur/blob/master/LearningImpl/src/bagaturchess/deeplearning/impl_nnue_v2/jni_eval/EvaluationConfig.java/">bagaturchess.deeplearning.impl_nnue_v2.jni_eval.EvaluationConfig</a>
These can be found under the <a href="https://github.com/bagaturchess/Bagatur/tree/master/LearningImpl">LearningImpl</a> sub-project.
Using the Java porting in production, Bagatur operates at about 2-3 times slower NPS speed but gains approximately 100 Elo in strength compared to the classic version. With the JNI version in production, Bagatur runs about 12 times slower in NPS speed and is 80 Elo weaker in strength compared to the classic version.

# Newer Stockfish NNUE probing code

Based on JNI, could be found here: https://github.com/VedantJoshi1409/stockfish_nnue_probe 
as well as the JFish chess engine which is using it: https://github.com/bagaturchess/JFish

# How to run it out of the box

The newer versions utilizes the Java NNUE based on Bullet.
As of version 5.0, NNUE is used as the evaluation function, and the classic version with its custom evaluation function is no longer supported.
Bagatur versions can be downloaded from the: https://github.com/bagaturchess/Bagatur/releases

# Credits

1. https://github.com/xu-shawn/Serendipity, thanks to Shawn for explaining to me how he has trained the NNUE network of Serendipity chess engine as well as for the reference Java code, which handles the network.

2. https://github.com/dshawul/nnue-probe

3. https://tests.stockfishchess.org/nns

4. https://hxim.github.io/Stockfish-Evaluation-Guide/

5. https://github.com/VedantJoshi1409/stockfish_nnue_probe




