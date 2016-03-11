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
package bagaturchess.bitboard.impl2.movegen;


import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.specials.Castling;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;

/**
 *
 *	Promotions and other moves are coded in different ways:
 *
 *	1. Promotions
 *		00001000 00000000 00000000 00000000 - prom flag set to true
 *		01000000 00000000 00000000 00000000 - capture flag
 * 		00000111 10000000 00000000 00000000 - cap_pid
 * 		00000000 01111000 00000000 00000000 - prom_pid
 *      10110000 00000111 00000000 00000000 - FREE 
 * 		00000000 00000000 11110000 00000000 - dir
 *  	00000000 00000000 00001111 11000000 - from
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
 *  	00000000 00000000 00001111 11000000 - from
 *   	00000000 00000000 00000000 00111111 - to
 *   
 */

public class MoveInt {
	
	private static int PROM_FLAG_SHIFT = 27;//31
	private static int CAP_FLAG_SHIFT  = 30;
	private static int ENP_FLAG_SHIFT  = 29;
	private static int CAST_FLAG_SHIFT  = 28;
	//private static int CAST_KING_FLAG_SHIFT  = 27;
	private static int PID1_SHIFT      = 23;
	private static int PID2_SHIFT      = 19;
	private static int SEQ_SHIFT       = 16;
	private static int DIR_SHIFT       = 12;
	private static int FROM_SHIFT      = 6;
	private static int TO_SHIFT        = 0;
	
	private static int FLAG_MASK       = 1;
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
	
	private static final int ENPAS_CHECK 	=  (1 << ENP_FLAG_SHIFT);
	
	private static int ORDERING_SHIFT      	= 33;
	
	/**
	 * Encode move
	 */
	public static int createCapturePromotion(int from, int to,int cap_pid, int prom_pid) {
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
	
	public static int createKingSide(int kingPID, int from, int to, int dir, int seq) {
		return INIT_CAST/*INIT_CAST_KING*/ | (from << FROM_SHIFT) | to | (dir << DIR_SHIFT) | (kingPID << PID2_SHIFT) | (seq << SEQ_SHIFT);
	}
	
	public static int createQueenSide(int kingPID, int from, int to, int dir, int seq) {
		return INIT_CAST/*INIT_CAST_QUEEN*/ | (from << FROM_SHIFT) | to | (dir << DIR_SHIFT) | (kingPID << PID2_SHIFT) | (seq << SEQ_SHIFT);
	}
	
	public static long addOrderingValue(int move, int ord_val) {
		long move_long = move & (0xFFFFFFFFFFFFFFFFL);
		long ord_val_long = ((long)ord_val) << ORDERING_SHIFT;
		long result = move_long | ord_val_long;
		
		/*if (move < 0) {
			int g = 0;
		}
		
		if (ord_val > 0 && result < 0L) {
			throw new IllegalStateException();
		}*/
		
		return result;
	}
	
	public static int getOrderingValue(long move) {
		return (int) (move >> ORDERING_SHIFT);
	}
	
	/**
	 * Decode move 
	 */
	
	public static boolean isPromotion(int move) {
		return (INIT_PROM & move) != 0; 
	}
	
	public static boolean isCapture(int move) {
		return (INIT_CAP & move) != 0; 
	}
	
	public static boolean isCastling(int move) {
		return (INIT_CAST /*INIT_CAST_QUEEN*/ & move) != 0; 
	}
	
	public static boolean isEnpassant(int move) {
		return (ENPAS_CHECK & move) != 0; 
	}
	
	public static int getDir(int move) {
		return (move >> DIR_SHIFT) & FIGTYPE_MASK;
	}
	
	public static int getSeq(int move) {
		return (move >> SEQ_SHIFT) & FIGTYPE_MASK;
	}
	
	public static int getCapturedFigurePID(int move) {
		return (move >> PID1_SHIFT) & PID_MASK;	
	}
	
	public static int getPromotionFigurePID(int move) {
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
	
	public static int getColour(int move) {
		return Constants.getColourByPieceIdentity(getFigurePID(move));
	}
	
	public static int getFigureType(int move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getFigurePID(move)];
	}
	
	public static boolean isWhite(int move) {
		return getColour(move) == Figures.COLOUR_WHITE;
	}
	
	public static int getEnpassantCapturedFieldID(int move) {
		return Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[getColour(move)][getFromFieldID(move)][getDir(move)];
	}

	public static int getCapturedFigureType(int move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getCapturedFigurePID(move)];
	}
	
	public static boolean isCastleKingSide(int move) {
		//return isCastling(move) && ((move >> CAST_KING_FLAG_SHIFT) & FLAG_MASK) != 0;
		if (isCastling(move)) {
			int toFieldID = getToFieldID(move);
			if (toFieldID == Fields.G1_ID || toFieldID == Fields.G8_ID) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isCastleQueenSide(int move) {
		//return isCastling(move) && ((move >> CAST_KING_FLAG_SHIFT) & FLAG_MASK) == 0;
		if (isCastling(move)) {
			int toFieldID = getToFieldID(move);
			if (toFieldID == Fields.C1_ID || toFieldID == Fields.C8_ID) {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isQueen(int move) {
		return getFigureType(move) == Figures.TYPE_QUEEN;
	}
	
	public static int getDirType(int move) { 
		
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
	
	public static int getPromotionFigureType(int move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
	}
	
	public static boolean isPawn(int move) {
		int pid = getFigurePID(move);
		return pid == Constants.PID_W_PAWN || pid == Constants.PID_B_PAWN;
	}

	public static boolean isCaptureOrPromotion(int move) {
		return isCapture(move) || isPromotion(move);
	}

	public static int getOpponentColour(int move) {
		return Figures.OPPONENT_COLOUR[getColour(move)];
	}

	public static long getToFieldBitboard(int move) {
		return Fields.ALL_ORDERED_A1H1[getToFieldID(move)];
	}
	
	public static long getFromFieldBitboard(int move) {
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
	
	/*public static String movesToString(int[] pv, int count) {
		String pvStr = "";
		for (int i=0; i<count; i++) {
			pvStr += moveToString(pv[i]);
			if (i != count - 1) {
				pvStr += ", ";
			}
		}
		return pvStr;
	}*/
	
	public static String movesToString(int[] pv) {
		String pvStr = "";
		
		for (int i=0; i<pv.length; i++) {
			pvStr += moveToString(pv[i]);
			if (i != pv.length - 1) {
				pvStr += ", ";
			}
		}
		
		return pvStr;
	}
	
	public static final String moveToString(int move) {
		
		if (move == -1) {
			throw new IllegalStateException("move=" + move);
		}
		
		if (move == 0) {
			return "OOOO";
		}
		
		String moveStr = "";
		
		int figureType = getFigureType(move);
		String figureSign = "";
		if (figureType != Figures.TYPE_PAWN) {
			if (figureType == -1) {
				int h = 0;
			}
			figureSign = Figures.TYPES_SIGN[figureType];
		}
	
		String fieldsSeparator = "-";
		if (isCapture(move)) {
			fieldsSeparator = "x";
		}
		
		moveStr += figureSign;
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getFromFieldID(move)]];
		moveStr += fieldsSeparator;
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getToFieldID(move)]];
		
		if (isPromotion(move)) {
			moveStr += "=";
			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
			moveStr += Figures.TYPES_SIGN[promotionFigureType];
		}
		
		//moveStr += "(" + move[25]+ ")";
		
		return moveStr;
	}
	
	public static String movesToStringUCI(int[] pv) {
		String pvStr = "";
		
		for (int i=0; i<pv.length; i++) {
			pvStr += moveToStringUCI(pv[i]);
			if (i != pv.length - 1) {
				pvStr += ", ";
			}
		}
		
		return pvStr;
	}
	
	public static final String moveToStringUCI(int move) {
		
		if (move == -1) {
			throw new IllegalStateException("move=" + move);
		}
		
		if (move == 0) {
			return "OOOO";
			//throw new IllegalStateException();
		}
		
		String moveStr = "";
		
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getFromFieldID(move)]];
		if (isCapture(move)) {
		}
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getToFieldID(move)]];
		
		if (isPromotion(move)) {
			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
			moveStr += Figures.TYPES_SIGN[promotionFigureType].toLowerCase();
		}
		
		return moveStr;
	}
	
	public static String movesToStringOwn(int[] pv) {
		String pvStr = "";
		
		for (int i=0; i<pv.length; i++) {
			pvStr += moveToStringOwn(pv[i]);
			if (i != pv.length - 1) {
				pvStr += ", ";
			}
		}
		
		return pvStr;
	}
	
	public static final String moveToStringOwn(int move) {
		
		if (move == -1) {
			throw new IllegalStateException("move=" + move);
		}
		
		if (move == 0) {
			throw new IllegalStateException();
		}
		
		String moveStr = "";
		
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getFromFieldID(move)]];
		if (isCapture(move)) {
			moveStr += "x";
		} else {
			moveStr += "-";
		}
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getToFieldID(move)]];
		
		if (isPromotion(move)) {
			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
			moveStr += Figures.TYPES_SIGN[promotionFigureType].toLowerCase();
		}
		
		return moveStr;
	}
	
	public static long getMoveHash(int move) {
		
		long result = 0L;
		
		int pid = getFigurePID(move);
		//int colour = Constants.getColourByPieceIdentity(pid);
		int type = Constants.PIECE_IDENTITY_2_TYPE[pid];
		int dirID = getDir(move);
		int seq = getSeq(move);
		int fromFieldID = getFromFieldID(move);
		int toFieldID = getToFieldID(move);
		int figureDirType = getDirType(move);
		
		if (isPromotion(move)) {

			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
			if (type == Figures.TYPE_PAWN && isCapture(move)) {
				dirID += 2;
			}
			result = ConstantStructure.getMoveHash(pid, (int) promotionFigureType, (int)fromFieldID, (int)toFieldID);
			//move[29] = ConstantStructure.getMoveIndex((int) fromFieldID, (int) dirID, (int) seq, (int) promotionFigureType);
		} else {
			
			result = ConstantStructure.getMoveHash(pid, (int) (int)fromFieldID, (int)toFieldID);
			
			if (type == Figures.TYPE_CASTLE
					|| type == Figures.TYPE_OFFICER
					|| type == Figures.TYPE_QUEEN
					) {
				//move[29] = ConstantStructure.getMoveIndex((int) fromFieldID, (int) dirID, (int) seq, (int) figureDirType);
			} else {
				if (type == Figures.TYPE_PAWN && isCapture(move)) {
					dirID += 2;
				}
				/*if ((Move.MASK_CASTLE_SIDE & masks) != 0) {
					 if ((Move.MASK_CASTLE_KING_SIDE & masks) != 0) {
						 dirID = 9;
					 } else {
						 dirID = 10;
					 }
				}*/
				//move[29] = ConstantStructure.getMoveIndex((int) fromFieldID, (int) dirID, (int) seq);
			}
		}
		
		//System.out.println(""+result);
		
		return result;
	}

	public static int getMoveIndex(int move) {
		
		int result = 0;
		
		int pid = getFigurePID(move);
		//int colour = Constants.getColourByPieceIdentity(pid);
		int type = Constants.PIECE_IDENTITY_2_TYPE[pid];
		int dirID = getDir(move);
		int seq = getSeq(move);
		int fromFieldID = getFromFieldID(move);
		//int toFieldID = Move.getToFieldID(move);
		int figureDirType = getDirType(move);
		
		if (isPromotion(move)) {

			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
			if (type == Figures.TYPE_PAWN && isCapture(move)) {
				dirID += 2;
			}
			//result = ConstantStructure.getMoveHash(pid, (int) promotionFigureType, (int)fromFieldID, (int)toFieldID);
			result = ConstantStructure.getMoveIndex((int) fromFieldID, (int) dirID, (int) seq, (int) promotionFigureType);
		} else {
			
			//result = ConstantStructure.getMoveHash(pid, (int) (int)fromFieldID, (int)toFieldID);
			if (type == Figures.TYPE_CASTLE
					|| type == Figures.TYPE_OFFICER
					|| type == Figures.TYPE_QUEEN
					) {
				result = ConstantStructure.getMoveIndex((int) fromFieldID, (int) dirID, (int) seq, (int) figureDirType);
			} else {
				if (type == Figures.TYPE_PAWN && isCapture(move)) {
					dirID += 2;
				}
				/*if ((Move.MASK_CASTLE_SIDE & masks) != 0) {
					 if ((Move.MASK_CASTLE_KING_SIDE & masks) != 0) {
						 dirID = 9;
					 } else {
						 dirID = 10;
					 }
				}*/
				result = ConstantStructure.getMoveIndex((int) fromFieldID, (int) dirID, (int) seq);
			}
		}
		
		//System.out.println(""+result);
		
		return result;
	}
	

	public static boolean isEquals(int move1, int move2) {
		return move1 == move2;
	}
}
