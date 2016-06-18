

/*
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
 *  
 *  Open Source project location: http://sourceforge.net/projects/bagaturchess/develop
 *  SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess
 *
 *  This file is part of BagaturChess program.
 * 
 *  BagaturChess is open software: you can redistribute it and/or modify
 *  it under the terms of the Eclipse Public License version 1.0 as published by
 *  the Eclipse Foundation.
 *
 *  BagaturChess is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Eclipse Public License for more details.
 *
 *  You should have received a copy of the Eclipse Public License version 1.0
 *  along with BagaturChess. If not, see <http://www.eclipse.org/legal/epl-v10.html/>.
 *
 */


For the latest and greatest version of this readme file you can visit the SVN repository and check the UCITracker sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


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

There are two main classes in the run package:
1. GamesGenerator - this one, generates and saves the games by given parameters (engine path, output file and games count)
2. GamesTraverser - traverse all the games saved in a specified file and calls your own implementation of PositionsVisitor interface
					by calling its visitPosition(IBitBoard bitboard, IGameStatus status, int eval) method.

For the last time i have used this tool to run Houdini_15a_w32.exe engine. For 12 hours (1 night) it generates 10 000 games which
contained more than 30 000 000 positions (+ their evaluations provided by Houdini).
After that the traverser succeeded to iterate all of them for less then 90 seconds.

Extremely useful tool for the fans of Artificial Intelligence!


Have a nice usage ... and feel free to contribute at http://sourceforge.net/projects/bagaturchess/develop

