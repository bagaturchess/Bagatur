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
package bagaturchess.bitboard.impl.plies.specials;


import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;


public class Castling extends Fields {
	
	public static final long MASK_WHITE_KING_SIDE = F1 | G1;
	public static final long MASK_WHITE_QUEEN_SIDE = B1 | C1 | D1;
	public static final long MASK_BLACK_KING_SIDE = F8 | G8;
	public static final long MASK_BLACK_QUEEN_SIDE = B8 | C8 | D8;
	
  public static final int[] KINGS_PIDS_BY_COLOUR = new int[Figures.COLOUR_MAX];
  static {
	  KINGS_PIDS_BY_COLOUR[Figures.COLOUR_WHITE] = Constants.PID_W_KING;
	  KINGS_PIDS_BY_COLOUR[Figures.COLOUR_BLACK] = Constants.PID_B_KING;
  }
  
  /*public static final int[] CASTLE_PID_FOR_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
  static {
	  CASTLE_PID_FOR_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = Constants.PID_W_ROOK;
	  CASTLE_PID_FOR_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = Figures.BLACK_LEFT_CASTLE_ID;
  }*/
  
  /*public static final int[] CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
  static {
	  CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = Figures.WHITE_LEFT_CASTLE_ID;
	  CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = Figures.BLACK_RIGHT_CASTLE_ID;
  }*/
	
	public static final long[] MASK_KING_CASTLE_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		MASK_KING_CASTLE_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = MASK_WHITE_KING_SIDE;
		MASK_KING_CASTLE_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = MASK_BLACK_KING_SIDE;
	}
	
	public static final long[] MASK_QUEEN_CASTLE_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		MASK_QUEEN_CASTLE_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = MASK_WHITE_QUEEN_SIDE;
		MASK_QUEEN_CASTLE_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = MASK_BLACK_QUEEN_SIDE;
	}
	
	/*public static final boolean[][] castleSideInvolvedFiguresIDsByColour = new boolean[Figures.COLOUR_MAX][Figures.ID_MAX];
	static {
		castleSideInvolvedFiguresIDsByColour[Figures.COLOUR_WHITE][KINGS_IDS_BY_COLOUR[Figures.COLOUR_WHITE]] = true;
		castleSideInvolvedFiguresIDsByColour[Figures.COLOUR_WHITE][CASTLE_ID_FOR_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE]] = true;
		castleSideInvolvedFiguresIDsByColour[Figures.COLOUR_WHITE][CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE]] = true;
		castleSideInvolvedFiguresIDsByColour[Figures.COLOUR_BLACK][KINGS_IDS_BY_COLOUR[Figures.COLOUR_BLACK]] = true;
		castleSideInvolvedFiguresIDsByColour[Figures.COLOUR_BLACK][CASTLE_ID_FOR_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK]] = true;
		castleSideInvolvedFiguresIDsByColour[Figures.COLOUR_BLACK][CASTLE_ID_FOR_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK]] = true;
	}*/
	
	/*public static final long[] KING_FROM_BITBOARD_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		KING_FROM_BITBOARD_BY_COLOUR[Figures.COLOUR_WHITE] = E1;
		KING_FROM_BITBOARD_BY_COLOUR[Figures.COLOUR_BLACK] = E8;
	}*/

	public static final int[] KING_FROM_FIELD_ID_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KING_FROM_FIELD_ID_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(E1);
		KING_FROM_FIELD_ID_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(E8);
	}

	
	//Castles on king side
	/*public static final long[] KING_TO_BITBOARD_ON_KING_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		KING_TO_BITBOARD_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = G1;
		KING_TO_BITBOARD_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = G8;
	}*/
	
	public static final int[] KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(G1);
		KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(G8);
	}
	
	/*public static final long[] CASTLE_FROM_BITBOARD_ON_KING_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		CASTLE_FROM_BITBOARD_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = H1;
		CASTLE_FROM_BITBOARD_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = H8;
	}*/
	
	/*public static final long[] CASTLE_TO_BITBOARD_ON_KING_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		CASTLE_TO_BITBOARD_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = F1;
		CASTLE_TO_BITBOARD_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = F8;
	}*/
	
	public static final int[] CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(H1);
		CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(H8);
	}

	public static final int[] CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(F1);
		CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(F8);
	}
	
	//Castles on queen side
	/*public static final long[] KING_TO_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		KING_TO_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = C1;
		KING_TO_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = C8;
	}*/

	public static final int[] KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(C1);
		KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(C8);
	}
	
	/*public static final long[] CASTLE_FROM_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		CASTLE_FROM_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = A1;
		CASTLE_FROM_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = A8;
	}
	
	public static final long[] CASTLE_TO_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
	static {
		CASTLE_TO_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = D1;
		CASTLE_TO_BITBOARD_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = D8;
	}*/
	
	public static final int[] CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(A1);
		CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(A8);
	}

	public static final int[] CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
	static {
		CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE] = get67IDByBitboard(D1);
		CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK] = get67IDByBitboard(D8);
	}
	
	public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][3];
	static {
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = get67IDByBitboard(E1);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][1] = get67IDByBitboard(F1);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][2] = get67IDByBitboard(G1);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = get67IDByBitboard(E8);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][1] = get67IDByBitboard(F8);
		CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][2] = get67IDByBitboard(G8);
	}
	
	public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][3];
	static {
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = get67IDByBitboard(C1);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][1] = get67IDByBitboard(D1);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][2] = get67IDByBitboard(E1);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = get67IDByBitboard(C8);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][1] = get67IDByBitboard(D8);
		CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][2] = get67IDByBitboard(E8);
	}
	
	public static final long[][] CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX][3];
	static {
		CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = E1;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][1] = F1;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][2] = G1;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = E8;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][1] = F8;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][2] = G8;
	}
	
	public static final long[][] CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX][3];
	static {
		CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][0] = C1;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][1] = D1;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_WHITE][2] = E1;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][0] = C8;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][1] = D8;
		CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[Figures.COLOUR_BLACK][2] = E8;
	}
	
	
	public static final int getRookFromFieldID_queen(int colour) {
		return CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[colour];
	}
	
	public static final int getRookToFieldID_queen(int colour) {
		return CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[colour];
	}
	
	public static final int getRookFromFieldID_king(int colour) {
		return CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR[colour];
	}
	
	public static final int getRookToFieldID_king(int colour) {
		return CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[colour];
	}
}
