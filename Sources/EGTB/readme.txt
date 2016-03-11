

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
 
 
For the latest and greatest version of this readme file you can visit the SVN repository and check the EGTB sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


As a java programmer,
you want to have a high level java API able to work with Gaviota Tablebases, so that:
 * has an easy to use representation of the chess board (and examples)
 * has a java cache based on the LRU discipline for better efficiency
 * supports asynchronous usage of the java cache for non-blocking operations 
This software is designed to help you in that direction.

It is based on the 'Gaviota Endgame Tablebases Java API' project (http://sourceforge.net/projects/egtb-in-java/)
and operating systems support is inherited from there (check its latest version for more info).

Examples could be found in the main class bagaturchess.egtb.gaviota.run.GaviotaTest:
1. Initialization of the board representation by given FEN
2. Initialize the path to Gaviota EGTB Files as well as the native cache size in megabytes
3. The blocking probing, which returns also a move
4. The non-blocking probing which returns the game result only

In order to run it, you have to download and use the 'Gaviota Endgame Tablebases Java API' binaries in the following way:
1. download http://sourceforge.net/projects/egtb-in-java/files/latest/download
2. get ./egtbprobe.dll from the archive and copy it to "<workspace>\EGTB" directory
3. get ./bin/egtbprobe.jar from the archive and copy it to "<workspace>\EGTB\res" directory
4. compile the sources (the project needs also the classes from Bitboard sub-project)
5. run the main in bagaturchess.egtb.gaviota.run.GaviotaTest

Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop

