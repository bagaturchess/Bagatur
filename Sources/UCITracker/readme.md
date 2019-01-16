
# Overview

As a java programmer interested in chess,
you want to capture games in a binary format of any chess engine, so later you could iterate this games for different purposes.
This software tool is designed to help you in that direction.

The tool acts as program which understands the UCI protocol and
runs arbitrary chess engines like any other UCI platform (e.g. Arena).
It is designed to be used in order to track down the PVs (best lines) and evaluations of UCI chess engine
during random tournaments (self-play). All the information is saved as file on the file system
and is available latter by the programming API written in java. One possible usage of this tool is for
realization of artificial intelligence's techniques for machine learning like 'Temporal difference learning'
or any other similar methods.

# How to run

There are two main classes in the <a href="https://github.com/bagaturchess/Bagatur/tree/master/Sources/UCITracker/src/bagaturchess/ucitracker/run/GamesGenerator.java">run package</a>:
1. GamesGenerator - this one, generates and saves the games by given parameters (engine path, output file and games count)
2. GamesTraverser - traverse all the games saved in a specified file and calls your own implementation of PositionsVisitor interface
					by calling its visitPosition(IBitBoard bitboard, IGameStatus status, int eval) method.

For the last time I have used this tool to run Houdini_15a_w32.exe engine. For 12 hours (1 night) it generated 10 000 games, which
contained more than 30 000 000 positions (+ their evaluations provided by Houdini).
After that the traverser succeeded to iterate all of them for less then 90 seconds.

Extremely useful tool for the fans of Artificial Intelligence and Machine Learning!
