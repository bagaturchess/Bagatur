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
package bagaturchess.opening.api;


import java.io.Serializable;


public interface OpeningBook extends Serializable {
	
	
	public static final int OPENING_BOOK_MODE_POWER0 = 1;
	public static final int OPENING_BOOK_MODE_POWER1 = 2;
	public static final int OPENING_BOOK_MODE_POWER2 = 3;
	
	public static final int OPENING_BOOK_MIN_MOVES	  = 7;
	
	
	public IOpeningEntry getEntry(long hashkey, int colour);
	public int[][] getAllMovesAndCounts(long hashkey, int colour);
	public int get(long hashkey, int colour);
	
	public void store(String outFileName);
	public void add(long hashkey, int move);
	public void add(long hashkey, int move, int result);
	
	//public int size();
	//public void unload();
}
