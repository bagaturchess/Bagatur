

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


For the latest and greatest version of this readme file you can visit the SVN repository and check the OpeningGenerator sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


This is the proprietary Opening book's generator of Bagatur chess engine. 
It also serves as a good example of how to use BagaturPGNTool.

Here are the steps necessary to generate the Bagatur's opening book: 

1. First at all you need to have pgn files which will be the source for the opening book.
For example, you can download ~ 1 000 000 games from here http://www.chess.co.uk/twic/twic
(PGN format is the fourth column of the table)

2. Now you have to filter these games, for example by players' ELO and game result (white wins or black win).
For that purpose you can use the main class bagaturchess.tools.pgn.run.FilterPGNFiles in the 
PGNProcessor sub-project.

3. Generate w.ob and b.ob files, they will contain all the moves inside the filtered pgn games. For the generation you can use
bagaturchess.tools.opening.generator.run.OpeningsGenerator located inside the OpeningGenerator sub-project.

For more information about the Bagatur's Opening Book have a look at the readme file inside the Opening sub-project.


Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop

