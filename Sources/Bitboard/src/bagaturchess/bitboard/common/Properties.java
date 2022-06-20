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
package bagaturchess.bitboard.common;


public class Properties {
	
	
	public static final boolean STATISTICS_MODE 						= false;
	
	public static final boolean DEBUG_MODE 								= false;
	
	public static final boolean DEBUG_MODE_IS_POSSIBLE_DURING_MOVE 		= false;
	
	public static final boolean DEBUG_MODE_IN_CHECK_AFTER_KING_ESCAPE 	= false;
	
	
	public static final int DEBUG_LEVEL1 								= 0;
	
	public static final int DEBUG_LEVEL2 								= 1;
	
	public static final int DEBUG_LEVEL3 								= 2;
	
	public static int DEBUG_LEVEL 										= DEBUG_LEVEL3;
	
	
	public static final boolean DUMP_CASTLING 							= false;
}
