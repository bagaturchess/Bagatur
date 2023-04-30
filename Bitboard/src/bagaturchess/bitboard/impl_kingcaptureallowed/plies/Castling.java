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
package bagaturchess.bitboard.impl_kingcaptureallowed.plies;


import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;


public class Castling extends Fields {
	
	
	public static final int[] KINGS_PIDS_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KINGS_PIDS_BY_COLOUR[Figures.COLOUR_WHITE] = Constants.PID_W_KING;
		KINGS_PIDS_BY_COLOUR[Figures.COLOUR_BLACK] = Constants.PID_B_KING;
	}
	

	public static final int[] KING_FROM_FIELD_ID_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KING_FROM_FIELD_ID_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(E1);
		KING_FROM_FIELD_ID_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(E8);
	}
	
	
	public static final int[] KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(G1);
		KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(G8);
	}
	
	
	public static final int[] KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(C1);
		KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(C8);
	}
	
	
	public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][2];
	static {
		//CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = get67IDByBitboard(E1);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = get67IDByBitboard(F1);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][1] = get67IDByBitboard(G1);
		//CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = get67IDByBitboard(E8);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = get67IDByBitboard(F8);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][1] = get67IDByBitboard(G8);
	}
	
	public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][2];
	static {
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = get67IDByBitboard(C1);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][1] = get67IDByBitboard(D1);
		///CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][2] = get67IDByBitboard(E1);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = get67IDByBitboard(C8);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][1] = get67IDByBitboard(D8);
		//CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][2] = get67IDByBitboard(E8);
	}
}
