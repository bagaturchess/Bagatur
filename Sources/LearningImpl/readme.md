# Overview

The **LearningImpl** project is using <a href="http://neuroph.sourceforge.net/">Neuroph</a> Java framework in order to apply supervised machine learning and tune the Bagatur's evaluation function.
It is using <a href="https://en.wikipedia.org/wiki/Multilayer_perceptron">Multilayer perceptron (MLP)</a> feedforward artificial neural network. It has one layer with many features as the evaluation function becomes too slow to calculate with more layers and cannot compensate the better quality achieved by more layers.  

# How to run

If you want to run one of the supervised learning <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/deeplearning/run">main classes</a> first you have to generate training chess positions with evaluations using the <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/UCITracker">UCITracker project</a>, which saves these positions in a file.
For that purpose you need a strong chess engine like Stockfish, Komodo, Houdini or other. Than use the UCITracker to run self-play games of this engine and track down the positions played and their evaluations so later a supervised learning can take place.

# Interesting sources
- <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/deeplearning/run">Main classes</a>, which iterate the training set. They are for three different networks, which have different features.
- <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/deeplearning/impl/visitors">Position visitors</a>, which iterate the positions and apply the learning with training sets. They also print the current accuracy.
- <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/deeplearning/api">Utility classes</a>, which create the Multilayer perceptron and fill the initial input signals.

# Details

- <a href="https://github.com/bagaturchess/Bagatur/blob/master/Sources/LearningImpl/src/bagaturchess/deeplearning/impl/visitors/DeepLearningVisitorImpl_PST.java">DeepLearningVisitorImpl_PST.java</a> is optimizing the piece square tables (PST) only. It leads to weaker version but still playing good chess. The filling of the network inputs could be found in <a href="https://github.com/bagaturchess/Bagatur/blob/master/Sources/LearningImpl/src/bagaturchess/deeplearning/api/NeuralNetworkUtils_PST.java">NeuralNetworkUtils_PST.java</a>
- <a href="https://github.com/bagaturchess/Bagatur/blob/master/Sources/LearningImpl/src/bagaturchess/deeplearning/impl/visitors/DeepLearningVisitorImpl_AllFeatures.java">DeepLearningVisitorImpl_AllFeatures.java</a> is optimizing a lot of features like king safety, pieces mobility, pawn structure and many others. The filling of the network inputs could be found in <a href="https://github.com/bagaturchess/Bagatur/blob/master/Sources/LearningImpl/src/bagaturchess/learning/goldmiddle/impl/cfg/bagatur_allfeatures/filler/Bagatur_ALL_SignalFiller_InArray.java">Bagatur_ALL_SignalFiller_InArray.java</a>

