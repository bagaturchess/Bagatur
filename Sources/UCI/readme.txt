

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


For the latest and greatest version of this readme file you can visit the SVN repository and check the UCI sub-project:
SVN repository https://bagaturchess.svn.sourceforge.net/svnroot/bagaturchess


This sub-project contains the UCI protocol implementation of Bagatur chess engine.

It represents a reusable implementation of the UCI protocol,
the main interface which decouples the UCI protocol implementation from all other parts
of the Bagatur engine is bagaturchess.uci.api.IUCISearchAdaptor.
The UCI protocol support is not full but anyway allows the engine to play chess with 
chess players or other chess engines via UCI user interfaces (like Arena).


Have a nice usage ... and feel free to contribute at http://sourceforge.net/projects/bagaturchess/develop
