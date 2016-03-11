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


public interface IFieldsAttacks {
	
	public static final int OP_ADD_ATTACK = 0;
	public static final int OP_REM_ATTACK = 1;
	public static final int OP_MAX = OP_REM_ATTACK + 1;
	
	/*public static final int MAX_PAWN_STATES = 3; // 0, 1 or 2 attacks
	public static final int MAX_KNIGHT_STATES = 3; // 0, 1 or 2 attacks
	public static final int MAX_OFFICER_STATES = 3; // 0 or 1
	public static final int MAX_ROOK_STATES = 4;
	public static final int MAX_QUEEN_STATES = 5;
	public static final int MAX_KING_STATES = 2; // 0 or 1 attack
	public static final int MAX_OTHER_STATES = 1;*/

	public static final boolean MINOR_UNION = true;
	
	public static final int MAX_KNIGHT_STATES = 3; // 0, 1 or 2 attacks
	public static final int MAX_OFFICER_STATES = 3; // 0 or 1
	public static final int MAX_MINOR_STATES = 4; // 0 or 1
	
	public static final int MAX_ROOK_STATES = 4;
	public static final int MAX_QUEEN_STATES = 5;
	public static final int MAX_OTHER_STATES = 2;
	
	public static final int MAX_PAWN_STATES = 3; // 0, 1 or 2 attacks
	public static final int MAX_KING_STATES = 2; // 0 or 1 attack
	
	
	public int[] getControlArray(int colour);
	public long getControlBitboard(int colour);
	public int getScore_BeforeMove(int colour);
	public int getScore_AfterMove(int colour);
	public int getScore_ForEval(int colour);
	
	//public long getPotentiallyHangingPieces(int colour, int type);
	
	//public long getKingSafety(int colour);
	//public long w_KingSafety();
	//public long b_KingSafety();
}
