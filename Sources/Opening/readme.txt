

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


For the latest and greatest version of this readme file you can visit the SVN repository and check the Opening sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


This sub-project consists of both the Opening Book API of Bagatur engine and the utilities to operate over the opening books.

Opening Book API consists of:
1. bagaturchess.opening.api.OpeningBook - The API
2. bagaturchess.opening.api.OpeningBookFactory - The entry point factory for creating an OpeningBook instance.
In order to work the factory needs already generated opening book files for white and black sides.
For more details about the generation have a look at the readme file inside the OpeningGenerator sub-project.

Here are the utilities inside the run package (bagaturchess.opening.run):
1. BookTruncater - reduce the w.ob and b.ob openings in order to make the files' size ~ 1MB.
Normally you can remove the board states which are rarely observed (less than 3 times for example). 

2. TraverseBook - traverse the board states presented inside the opening books and calls callback methods of
specified bagaturchess.opening.api.traverser.OpeningsVisitor implementation.

3. ShortBookConverter - it is not used at all but the idea is to keep only the hashkeys of the board positions
which are presented in the Opening Book. It takes significantly less disk space (~ 20 times).


Have a nice usage ... and feel free to contribute http://sourceforge.net/projects/bagaturchess/develop

