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
package bagaturchess.bitboard.api;

public enum IGameStatus {
	NONE,
	DRAW_3_STATES_REPETITION,
	MATE_WHITE_WIN,
	MATE_BLACK_WIN,
	UNDEFINED,
	STALEMATE_WHITE_NO_MOVES,
	STALEMATE_BLACK_NO_MOVES,
	DRAW_50_MOVES_RULE,
	NO_SUFFICIENT_MATERIAL,
	PASSER_WHITE,
	PASSER_BLACK,
	NO_SUFFICIENT_WHITE_MATERIAL,
	NO_SUFFICIENT_BLACK_MATERIAL
}
