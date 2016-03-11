

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


For the latest and greatest version of this readme file you can visit the SVN repository and check the Bitboard sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


As a chess programmer,
I want to use a representation of the chess board and pieces such as:
* It provides move generation functionality
* It provides Make/Unmake move functionality
* It provides Incheck functionality
* It provides other functionalities
* It works with high performance (nodes per second)

This software is designed to help you in that direction in the context of java programming language.
In order to use it, you just need to instantiate an implementation of the IBitBoard interface.
You can use one of the constructors inside the bagaturchess.bitboard.impl.Board class.

Example of simple main method could be found here: bagaturchess.bitboard.run.Simulate

Digging into Bitboard sub-project one can learn a lot about the "Java Basics",
it is mainly low-level programming - bitwise operations and data structures with limited memory consumption
and operations' execution with a constant complexity.

Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop

