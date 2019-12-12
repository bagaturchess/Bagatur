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
package bagaturchess.bitboard.impl.movegen;


import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;


/**
*
*	Promotions and other moves are coded in different ways:
*
*	1. Promotions
*		00001000 00000000 00000000 00000000 - prom flag set to true
*		01000000 00000000 00000000 00000000 - capture flag
* 		00000111 10000000 00000000 00000000 - cap_pid
* 		00000000 01111000 00000000 00000000 - prom_pid
*       10110000 00000111 00000000 00000000 - FREE 
* 		00000000 00000000 11110000 00000000 - dir
*  		00000000 00000000 00001111 11000000 - from
*   	00000000 00000000 00000000 00111111 - to
*   
*   
*	2. Other moves
*		00001000 00000000 00000000 00000000 - prom flag set to false
*		01000000 00000000 00000000 00000000 - capture flag
*		00100000 00000000 00000000 00000000 - enpassant flag
*		00010000 00000000 00000000 00000000 - castling flag
*   	00000000 00000000 00000000 00000000 - FREE
*		00000111 10000000 00000000 00000000 - cap_pid
* 		00000000 01111000 00000000 00000000 - pid
* 		00000000 00000111 00000000 00000000 - seq (1 or 2 -> castling) 
* 		00000000 00000000 11110000 00000000 - dir
*  		00000000 00000000 00001111 11000000 - from
*   	00000000 00000000 00000000 00111111 - to
*   
*/
public class MoveInt {
	
	private static int PROM_FLAG_SHIFT = 27;
	private static int CAP_FLAG_SHIFT  = 30;
	private static int ENP_FLAG_SHIFT  = 29;
	private static int CAST_FLAG_SHIFT  = 28;
	//private static int CAST_KING_FLAG_SHIFT  = 27;
	private static int PID1_SHIFT      = 23;
	private static int PID2_SHIFT      = 19;
	private static int SEQ_SHIFT       = 16;
	private static int DIR_SHIFT       = 12;
	private static int FROM_SHIFT      = 6;
	//private static int TO_SHIFT        = 0;
	
	//private static int FLAG_MASK       = 1;
	private static int PID_MASK        = 15;
	private static int FIGTYPE_MASK    = 7;
	private static int FIELD_MASK      = 63;
	
	private static int INIT_CAP = (1 << CAP_FLAG_SHIFT);
	private static int INIT_PROM = (1 << PROM_FLAG_SHIFT);
	private static int INIT_CAP_PROM = INIT_CAP | INIT_PROM;

	private static int INIT_ENPAS = INIT_CAP | (1 << ENP_FLAG_SHIFT) | (1 << SEQ_SHIFT);
	//private static int INIT_CAST_KING = (1 << CAST_FLAG_SHIFT) | (1 << CAST_KING_FLAG_SHIFT);
	//private static int INIT_CAST_QUEEN = (1 << CAST_FLAG_SHIFT);
	private static int INIT_CAST = (1 << CAST_FLAG_SHIFT);
	
	private static final int ENPAS_CHECK =  (1 << ENP_FLAG_SHIFT);
	
	private static int ORDERING_SHIFT      = 32;
	
	/**
	 * Encode move
	 */
	public static int createCapturePromotion(int from, int to, int cap_pid, int prom_pid) {
		return INIT_CAP_PROM | (from << FROM_SHIFT) | to | (cap_pid << PID1_SHIFT) | (prom_pid << PID2_SHIFT);
	}
	
	public static int createPromotion(int from, int to, int prom_pid) {
		return INIT_PROM | (from << FROM_SHIFT) | to | (prom_pid << PID2_SHIFT);
	}
	
	public static int createCapture(int pid, int from, int to, int cap_pid) {
		return INIT_CAP | (from << FROM_SHIFT) | to | (pid << PID2_SHIFT) | (cap_pid << PID1_SHIFT);
	}
	
	public static int createNonCapture(int pid, int from, int to) {
		return (from << FROM_SHIFT) | to | (pid << PID2_SHIFT);
	}
	
	public static int createEnpassant(int pid, int from, int to, int dir, int cap_pid) {
		return INIT_ENPAS | (from << FROM_SHIFT) | to | (dir << DIR_SHIFT) | (pid << PID2_SHIFT) | (cap_pid << PID1_SHIFT);
	}
	
	public static int createKingSide(int kingPID, int from, int to) {
		return INIT_CAST | (from << FROM_SHIFT) | to  | (kingPID << PID2_SHIFT);
	}
	
	public static int createQueenSide(int kingPID, int from, int to) {
		return INIT_CAST | (from << FROM_SHIFT) | to | (kingPID << PID2_SHIFT);
	}
	
	public static long addOrderingValue(int move, long ord_val) {
		return (ord_val << ORDERING_SHIFT) | move;
	}
	
	public static int getOrderingValue(long move) {
		return (int) (move >> ORDERING_SHIFT);
	}
	
	/**
	 * Decode move 
	 */
	
	public static final boolean isPromotion(int move) {
		return (INIT_PROM & move) != 0; 
	}
	
	public static final boolean isCapture(int move) {
		return (INIT_CAP & move) != 0; 
	}
	
	public static final boolean isCastling(int move) {
		return (INIT_CAST & move) != 0; 
	}
	
	public static final boolean isEnpassant(int move) {
		return (ENPAS_CHECK & move) != 0; 
	}
	
	public static final int getDir(int move) {
		return (move >> DIR_SHIFT) & FIGTYPE_MASK;
	}
	
	public static final int getSeq(int move) {
		return (move >> SEQ_SHIFT) & FIGTYPE_MASK;
	}
	
	public static final int getCapturedFigurePID(int move) {
		return (move >> PID1_SHIFT) & PID_MASK;	
	}
	
	public static final int getPromotionFigurePID(int move) {
		if (isPromotion(move)) {
			return (move >> PID2_SHIFT) & PID_MASK;
		} else return 0;
	}
	
	public static final int getFromFieldID(int move) {
		return (move >> FROM_SHIFT) & FIELD_MASK;
	}
	
	public static final int getToFieldID(int move) {
		return move & FIELD_MASK;
	}
	
	/**
	 * Dynamic getters 
	 */
	
	public static final int getFigurePID(int move) {
		if (isPromotion(move)) {
			return (Fields.ALL_ORDERED_A1H1[getToFieldID(move)] & Fields.DIGIT_8) != 0 ?  Constants.PID_W_PAWN : Constants.PID_B_PAWN;
		} else {
			return (move >> PID2_SHIFT) & PID_MASK;
		}
	}
	
	public static final int getColour(int move) {
		return Constants.getColourByPieceIdentity(getFigurePID(move));
	}
	
	public static final int getFigureType(int move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getFigurePID(move)];
	}
	
	public static final boolean isWhite(int move) {
		return getColour(move) == Figures.COLOUR_WHITE;
	}
	
	public static final int getEnpassantCapturedFieldID(int move) {
		return Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[getColour(move)][getFromFieldID(move)][getDir(move)];
	}

	public static final int getCapturedFigureType(int move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getCapturedFigurePID(move)];
	}
	
	public static final boolean isCastleKingSide(int move) {
		//return isCastling(move) && ((move >> CAST_KING_FLAG_SHIFT) & FLAG_MASK) != 0;
		if (isCastling(move)) {
			int toFieldID = getToFieldID(move);
			if (toFieldID == Fields.G1_ID || toFieldID == Fields.G8_ID) {
				return true;
			}
		}
		return false;
	}
	
	public static final boolean isCastleQueenSide(int move) {
		//return isCastling(move) && ((move >> CAST_KING_FLAG_SHIFT) & FLAG_MASK) == 0;
		if (isCastling(move)) {
			int toFieldID = getToFieldID(move);
			if (toFieldID == Fields.C1_ID || toFieldID == Fields.C8_ID) {
				return true;
			}
		}
		return false;
	}
	
	public static final boolean isQueen(int move) {
		return getFigureType(move) == Figures.TYPE_QUEEN;
	}
	
	public static final boolean isPawnCapture(int move) {
		return getCapturedFigureType(move) == Figures.TYPE_PAWN;
	}
	
	public static final int getDirType(int move) { 
		
		//if (true) return (int) move[4];

		if (!isQueen(move)) {
			return getFigureType(move);
		} else {
			int from = getFromFieldID(move);
			int to = getToFieldID(move);
			if ((CastlePlies.ALL_CASTLE_MOVES[from] & Fields.ALL_ORDERED_A1H1[to]) != 0L) {
				return Figures.TYPE_CASTLE;
			} else {
				return Figures.TYPE_OFFICER;
			}
		}
	}
	
	public static final int getPromotionFigureType(int move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
	}
	
	public static final boolean isPawn(int move) {
		int pid = getFigurePID(move);
		return pid == Constants.PID_W_PAWN || pid == Constants.PID_B_PAWN;
	}

	public static final boolean isCaptureOrPromotion(int move) {
		return isCapture(move) || isPromotion(move);
	}

	public static final int getOpponentColour(int move) {
		return Figures.OPPONENT_COLOUR[getColour(move)];
	}

	public static final long getToFieldBitboard(int move) {
		return Fields.ALL_ORDERED_A1H1[getToFieldID(move)];
	}
	
	public static final long getFromFieldBitboard(int move) {
		return Fields.ALL_ORDERED_A1H1[getFromFieldID(move)];
	}
	
	
	public static final int getCastlingRookPID(int move) {
		return getColour(move) == Figures.COLOUR_WHITE ? Constants.PID_W_ROOK : Constants.PID_B_ROOK;
	}
	
	public static final int getCastlingRookFromID(int move) {
		//return MoveInt.isCastleKingSide(move) ? Castling.getRookFromFieldID_king(getColour(move)) : Castling.getRookFromFieldID_queen(getColour(move));
		int toFieldID = getToFieldID(move);
		if (getColour(move) == Constants.COLOUR_WHITE) {
			if (toFieldID == Fields.C1_ID) {
				return Fields.A1_ID;
			} else if (toFieldID == Fields.G1_ID) {
				return Fields.H1_ID;
			} else {
				throw new IllegalStateException();
			}
		} else {
			if (toFieldID == Fields.C8_ID) {
				return Fields.A8_ID;
			} else if (toFieldID == Fields.G8_ID) {
				return Fields.H8_ID;
			} else {
				throw new IllegalStateException();
			}
		}
	}
	
	public static final int getCastlingRookToID(int move) {
		//return MoveInt.isCastleKingSide(move) ? Castling.getRookToFieldID_king(getColour(move)) : Castling.getRookToFieldID_queen(getColour(move));
		int toFieldID = getToFieldID(move);
		if (getColour(move) == Constants.COLOUR_WHITE) {
			if (toFieldID == Fields.C1_ID) {
				return Fields.D1_ID;
			} else if (toFieldID == Fields.G1_ID) {
				return Fields.F1_ID;
			} else {
				throw new IllegalStateException();
			}
		} else {
			if (toFieldID == Fields.C8_ID) {
				return Fields.D8_ID;
			} else if (toFieldID == Fields.G8_ID) {
				return Fields.F8_ID;
			} else {
				throw new IllegalStateException();
			}
		}
	}	

	public static boolean isEquals(int move1, int move2) {
		return move1 == move2;
	}
}
