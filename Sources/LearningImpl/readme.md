# Overview

The LearningImpl project is using Neuroph Java framework in order to apply supervised machine learning and tune the Bagatur's evaluation function. It is using <a href="https://en.wikipedia.org/wiki/Multilayer_perceptron">Multilayer perceptron (MLP)</a> feedforward artificial neural network.

# How to run

If you want to run one of the supervised learning <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/deeplearning/run">main classes</a> first you have to generate training chess positions with evaluations using the <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/UCITracker">UCITracker project</a>, which saves these positions in a file. For that purpose you need a strong chess engine like Stockfish, Komodo, Houdini or other. Than use the UCITracker to run self-play games of this engine and track down the positions played and their evaluations so later a supervised learning can take place.
