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

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.specials.Enpassanting;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;

public class Move extends Fields { 
	
	public static int MOVE_LONGS_COUNT = 32;
	
	public static final long[] NULL = new long[MOVE_LONGS_COUNT];
	public static final long[] SUBSTITUTED = new long[MOVE_LONGS_COUNT];
	
	static {
		for (int i=0; i<NULL.length; i++) {
			NULL[i] = -1;
		}
	}
	
	public static final long MASK_EMPTY = NUMBER_0;
	
	public static final long MASK_CAPTURE = BIT_0;
	public static final long MASK_CASTLE_SIDE = BIT_1;
	public static final long MASK_CASTLE_QUEEN_SIDE = BIT_2;
	public static final long MASK_CASTLE_KING_SIDE = BIT_3;
	public static final long MASK_ENPASSANT = BIT_4;
	public static final long MASK_PROMOTION = BIT_5;
	
	public static final int CASTLING_KING_SEQ = 1;
	public static final int CASTLING_QUEEN_SEQ = 2;
	public static final int ENPASSANT_SEQ = 1;
	
	/**
	 * Not initilized by default
	 * At least one call to Board.isCheck/isDirectCheck/isHiddenCheck is needed.
	 */
	//public static final long MASK_CHECK = BIT_6;
	//public static final long MASK_CHECK_DIRECT = BIT_7;
	//public static final long MASK_CHECK_HIDDEN = BIT_8;
	
	public static final String moveToString(long[] move) {
		
		if (move == null) {
			return "OOOO";
		}
		
		if (move == SUBSTITUTED) {
			return "SM";
		}
		
		String moveStr = "";
		
		int figureType = getFigureType(move);
		String figureSign = "";
		if (figureType != Figures.TYPE_PAWN) {
			figureSign = Figures.TYPES_SIGN[figureType];
		}
	
		String fieldsSeparator = "-";
		if ((move[0] & MASK_CAPTURE) != 0) {
			fieldsSeparator = "x";
		}
		
		moveStr += figureSign;
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[(int) move[9]]];
		moveStr += fieldsSeparator;
		moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[(int) move[10]]];
		
		if ((move[0] & MASK_PROMOTION) != 0) {
			moveStr += "=";
			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[Move.getPromotionFigurePID(move)];
			moveStr += Figures.TYPES_SIGN[promotionFigureType];
		}
		
		moveStr += "(" + move[25]+ ")";
		
		return moveStr;
	}
	
	public static String movesToString(long[][] moves, int count) {
		String result = "";
		for (int i=0; i<count; i++) {
			long[] move = moves[i];
			result += Move.moveToString(move) + ", ";
		}
		return result;
	}
	
	/*public static final String moveToString(long[] move) {
		String moveStr = "";
		int figureType = (int) move[3];
		String sign = Figures.TYPES_SIGN[figureType];
		
		if ((move[0] & MASK_CASTLE_KING_SIDE) != 0) {
			moveStr += "O-O";
		} else if ((move[0] & MASK_CASTLE_QUEEN_SIDE) != 0) {
			moveStr += "O-O-O";
		} else {
			moveStr += Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[(int) move[9]]];
			moveStr += "-" + Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[(int) move[10]]];
	
			if ((move[0] & MASK_CAPTURE) != 0) {
				moveStr += "X";
			}
			
			if ((move[0] & MASK_PROMOTION) != 0) {
				moveStr += "=";
				moveStr += move[21];
			}
		}
		
		return moveStr;
	}*/
	
	
	/**
	 * Static values coded in move
	 */
	public static final int getFigurePID(long[] move) {
		return (int) move[1];
	}
	
	public static int getDirType(long[] move) { 
		
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
	
	public static int getDir(long[] move) {
		return (int) move[5];
	}
	
	public static int getSeq(long[] move) {
		return (int) move[6];
	}
	
	public static int getCapturedFigurePID(long[] move) {
		return (int) move[11];
	}
	
	public static int getPromotionFigurePID(long[] move) {
		return (int) move[21];
	}
	
	public static final int getFromFieldID(long[] move) {
		return (int) move[9];
	}
	
	public static final int getToFieldID(long[] move) {
		return (int) move[10];
	}
	
	public static void check(long[] move) {
		for (int i=0; i<move.length; i++) {
			if (i == 0     // flags
					|| i == 1  // piece id
					|| i == 4  // dir type
					|| i == 5  // dir
					|| i == 6  // seq
					|| i == 11 // captured piece id
					|| i == 21 // promoted piece id
					|| i == 9  // from field id
					|| i == 10 // to field id
				) {
				continue;
			}
			
			if (move[i] != 0) {
				throw new IllegalStateException();
			}
		}
	}
	
	/**
	 * Dinamic values
	 */
	
	public static int getColour(long[] move) {
		return Constants.getColourByPieceIdentity(getFigurePID(move));
	}
	
	public static int getFigureType(long[] move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getFigurePID(move)];
	}
	
	public static boolean isWhite(long[] move) {
		return getColour(move) == Figures.COLOUR_WHITE;
	}

	public static boolean isMinorPiece(long[] move) {
		return getFigureType(move) == Figures.TYPE_KNIGHT || getFigureType(move) == Figures.TYPE_OFFICER;
	}

	public static boolean isMajorPiece(long[] move) {
		return getFigureType(move) == Figures.TYPE_CASTLE || getFigureType(move) == Figures.TYPE_QUEEN;
	}

	public static boolean isQueen(long[] move) {
		return getFigureType(move) == Figures.TYPE_QUEEN;
	}
	
	public static boolean isCastle(long[] move) {
		return getFigureType(move) == Figures.TYPE_CASTLE;
	}
	
	public static boolean isPawn(long[] move) {
		return getFigureType(move) == Figures.TYPE_PAWN;
	}

	public static boolean isKnight(long[] move) {
		return getFigureType(move) == Figures.TYPE_KNIGHT;
	}
	
	public static boolean isOfficer(long[] move) {
		return getFigureType(move) == Figures.TYPE_OFFICER;
	}
	
	public static boolean isKing(long[] move) {
		return getFigureType(move) == Figures.TYPE_KING;
	}
	
	public static boolean isKingOrPawn(long[] move) {
		return getFigureType(move) == Figures.TYPE_KING || getFigureType(move) == Figures.TYPE_PAWN;
	}
	
	public static boolean isCapture(long[] move) {
		return (move[0] & Move.MASK_CAPTURE) != Bits.NUMBER_0;
	}

	public static boolean isEnpassant(long[] move) {
		return (move[0] & Move.MASK_ENPASSANT) != Bits.NUMBER_0;
	}
	
	public static boolean isCastleSide(long[] move) {
		return (move[0] & Move.MASK_CASTLE_SIDE) != Bits.NUMBER_0;
	}
	
	public static boolean isCastleKingSide(long[] move) {
		return (move[0] & Move.MASK_CASTLE_KING_SIDE) != Bits.NUMBER_0;
	}
	
	public static boolean isCastleQueenSide(long[] move) {
		return (move[0] & Move.MASK_CASTLE_QUEEN_SIDE) != Bits.NUMBER_0;
	}
	
	/*public static long getEnpassantOpponentPawn(long[] move) {
		return Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[getColour(move)][getFromFieldID(move)][getDir(move)];
	}*/
	
	public static int getEnpassantCapturedFieldID(long[] move) {
		return Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[getColour(move)][getFromFieldID(move)][getDir(move)];
	}

	public static int getCapturedFigureType(long[] move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getCapturedFigurePID(move)];
	}
	
	public static boolean isCapturingPawn(long[] move) {
		return isCapture(move) && getCapturedFigureType(move) == Figures.TYPE_PAWN;
	}
	
	public static boolean isCapturingQueen(long[] move) {
		return isCapture(move) && getCapturedFigureType(move) == Figures.TYPE_QUEEN;
	}
	
	public static boolean isCapturingCastle(long[] move) {
		return isCapture(move) && getCapturedFigureType(move) == Figures.TYPE_CASTLE;
	}
	
	public static boolean isCapturingMinor(long[] move) {
		int capturedType = getCapturedFigureType(move);
		return isCapture(move) && (capturedType == Figures.TYPE_OFFICER || capturedType == Figures.TYPE_KNIGHT);
	}
	
	public static boolean isPromotion(long[] move) {
		return (move[0] & Move.MASK_PROMOTION) != Bits.NUMBER_0;
	}
	
	public static boolean isCaptureOrPromotion(long[] move) {
		return isCapture(move) || isPromotion(move);
	}
	
	public static int getPromotionFigureType(long[] move) {
		return Constants.PIECE_IDENTITY_2_TYPE[getPromotionFigurePID(move)];
	}
	
	public static final long getFromFieldBitboard(long[] move) {
		throw new IllegalStateException();
		//return move[7];
	}

	public static final long getToFieldBitboard(long[] move) {
		throw new IllegalStateException();
//		/return move[8];
	}
	                                                   	
	public static final boolean isEquals(long[] move1, long[] move2) {
		return move1[0] == move2[0] && move1[1] == move2[1] && move1[9] == move2[9] && move1[10] == move2[10];
	}
	
	public static long getMoveHash(long[] move) {
		
		long result = 0L;
		
		long masks = move[0];
		int pid = getFigurePID(move);
		//int colour = Constants.getColourByPieceIdentity(pid);
		int type = Constants.PIECE_IDENTITY_2_TYPE[pid];
		int dirID = Move.getDir(move);
		int seq = Move.getSeq(move);
		int fromFieldID = Move.getFromFieldID(move);
		int toFieldID = Move.getToFieldID(move);
		int figureDirType = Move.getDirType(move);
		
		if ((Move.MASK_PROMOTION & masks) != 0) {

			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[Move.getPromotionFigurePID(move)];
			if (type == Figures.TYPE_PAWN && (Move.MASK_CAPTURE & masks) != 0) {
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
				if (type == Figures.TYPE_PAWN && (Move.MASK_CAPTURE & masks) != 0) {
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
	
	public static int getMoveIndex(long[] move) {
		
		int result = 0;
		
		long masks = move[0];
		int pid = getFigurePID(move);
		//int colour = Constants.getColourByPieceIdentity(pid);
		int type = Constants.PIECE_IDENTITY_2_TYPE[pid];
		int dirID = Move.getDir(move);
		int seq = Move.getSeq(move);
		int fromFieldID = Move.getFromFieldID(move);
		//int toFieldID = Move.getToFieldID(move);
		int figureDirType = Move.getDirType(move);
		
		if ((Move.MASK_PROMOTION & masks) != 0) {

			int promotionFigureType = Constants.PIECE_IDENTITY_2_TYPE[Move.getPromotionFigurePID(move)];
			if (type == Figures.TYPE_PAWN && (Move.MASK_CAPTURE & masks) != 0) {
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
				if (type == Figures.TYPE_PAWN && (Move.MASK_CAPTURE & masks) != 0) {
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
}
