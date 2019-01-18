
# Overview

As a chess programmer,
you want to have the <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources">sources</a> of Bagatur chess engine as a part of the distribution so that you can easily compile and run them inside the Eclipse development environment.
This source distribution is exactly what you want. It is an Eclipse workspace with simple main method inside the EnginesRunner sub-project.

# How to run the engine

- Download the sources in a new directory.
- Import the existing projects into the Eclipse workspace.
- Run the main class bagaturchess.engines.run.MTDSchedulerMain (it is inside the EnginesRunner sub-project).

# Interesting sources

- <a href="https://github.com/bagaturchess/Bagatur/blob/master/Sources/Bitboard/src/bagaturchess/bitboard/impl/Board.java">Board representation</a>
- <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/LearningImpl/src/bagaturchess/learning/goldmiddle/impl/cfg/bagatur_allfeatures/eval">Evaluation function</a>
- <a href="https://github.com/bagaturchess/Bagatur/blob/master/Sources/Search/src/bagaturchess/search/impl/alg/impl0/Search_PVS_NWS.java">Search algorithm</a>
