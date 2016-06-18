

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


For the latest and greatest version of this readme file you can visit the SVN repository and check the PGNProcessor sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


As a chess programmer,
you want to programatically analyze or process a given chess games set played by chess masters and/or chess engines.
In most cases, you want to filter the games or to extract some data/statistics from them.
There are a lot of text formats in which the chess games could be represented
like Portable Game Notation (PGN): http://en.wikipedia.org/wiki/Portable_Game_Notation

A lot of games in that format could be easily found. For example
here are about to 1 000 000 games: http://www.chess.co.uk/twic/twic
(PGN format is the fourth column of the table) 

This software tool is designed to help you in that direction in the context of java programming language.
In order to use it, you just need to:

1. Specify the folder which contains zipped or extracted PGN files

2. Implement your own handler - the implementation of one of the following interfaces:

	package bagaturchess.tools.pgn.api;
	
	public interface IGameIterator {
		public void preIteration(IBitBoard bitboard); // Called only once - at the begging of the run 
		public void postIteration(); // Called only once - at the end of the run 
		public void preGame(int gameCount, PGNGame pgnGame, String pgnGameID, IBitBoard bitboard); // Called before each new game
		public void postGame(); // Called after each game
	}
	
	or
	
	public interface IPlyIterator extends IGameIterator {
		public void preMove(int colour, int move, IBitBoard bitboard, int moveNumber); // Called before each move
		public void postMove(); //called after each move
	}
	
	(normally the implementation is just few lines of code of course depending on the complexity of your goal)
	
3. Invoke the PGNParser with your handler(s) implementation

4. Enjoy the performance and the good object oriented game's representation achieved by the IBitBoard interface


Examples of simple main method could be found:
1. bagaturchess.tools.pgn.run.FilterPGNFiles is an implementation of IGameIterator
which filters games with given result ("1-0", "0-1") and having players' elo more than 2600(located in PGNProcessor sub-project)
2. bagaturchess.tools.opening.generator.run.OpeningsGenerator is an implementation of IPlyIterator
which generates Bagatur's opening book (located in the project OpeningGenerator)
(sources could be found in src.zip or in the SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess)


Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop

