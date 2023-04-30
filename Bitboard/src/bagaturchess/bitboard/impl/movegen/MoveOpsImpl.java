/**
 *  BagaturChess (UCI chess engine and tools)
 *  Copyright (C) 2005 Krasimir I. Topchiyski (k_topchiyski@yahoo.com)
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
 *  along with BagaturChess. If not, see http://www.eclipse.org/legal/epl-v10.html
 *
 */
package bagaturchess.bitboard.impl.movegen;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IMoveOps;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movelist.BaseMoveList;
import bagaturchess.bitboard.impl.movelist.IMoveList;


public class MoveOpsImpl implements IMoveOps {
	
	
	private IBitBoard board;
	
	
	public MoveOpsImpl(IBitBoard _board) {
		board = _board;
	}
	
	
	@Override
	public boolean isCapture(int move) {
		return MoveInt.isCapture(move);
	}
	
	
	@Override
	public boolean isPromotion(int move) {
		return MoveInt.isPromotion(move);
	}
	
	
	@Override
	public boolean isCaptureOrPromotion(int move) {
		return isCapture(move) || isPromotion(move);
	}
	
	
	@Override
	public boolean isEnpassant(int move) {
		return MoveInt.isEnpassant(move);
	}
	
	
	@Override
	public boolean isCastling(int move) {
		return MoveInt.isCastling(move);
	}
	
	
	@Override
	public int getFigurePID(int move) {
		return MoveInt.getFigurePID(move);
	}
	
	
	@Override
	public int getToFieldID(int move) {
		return MoveInt.getToFieldID(move);
	}
	
	
	@Override
	public int getFigureType(int move) {
		return  MoveInt.getFigureType(move);
	}
	
	
	@Override
	public boolean isCastlingKingSide(int move) {
		return MoveInt.isCastleKingSide(move);
	}
	
	
	@Override
	public boolean isCastlingQueenSide(int move) {
		return MoveInt.isCastleQueenSide(move);
	}
	
	
	@Override
	public int getFromFieldID(int move) {
		return MoveInt.getFromFieldID(move);
	}
	
	
	@Override
	public int getPromotionFigureType(int move) {
		return MoveInt.getPromotionFigureType(move);
	}
	
	
	@Override
	public int getCapturedFigureType(int cur_move) {
		return MoveInt.getCapturedFigureType(cur_move);
	}
	
	
	@Override
	public String moveToString(int move) {
		StringBuilder result = new StringBuilder();
		moveToString(move, result);
		return result.toString();
	}
	
	
	@Override
	public int stringToMove(String move) {
		return uciStrToMove(board, move);
	}
	
	
	@Override
	public int getToField_File(int move) {
		return Fields.LETTERS[getToFieldID(move)];
	}
	
	
	@Override
	public int getToField_Rank(int move) {
		return Fields.DIGITS[getToFieldID(move)];
	}
	
	
	@Override
	public int getFromField_File(int move) {
		return Fields.LETTERS[getFromFieldID(move)];
	}
	
	
	@Override
	public int getFromField_Rank(int move) {
		return Fields.DIGITS[getFromFieldID(move)];
	}
	
	
	private int uciStrToMove(IBitBoard bitboard, String moveStr) {
		
		int fromFieldID = Fields.getFieldID(moveStr.substring(0, 2));
		int toFieldID = Fields.getFieldID(moveStr.substring(2, 4));
		
		IMoveList mlist = new BaseMoveList();
		if (bitboard.isInCheck()) {
			bitboard.genKingEscapes(mlist);
		} else {
			bitboard.genAllMoves(mlist);
		}
		
		int cur_move = 0;
		while ((cur_move = mlist.next()) != 0) {
			if (fromFieldID == MoveInt.getFromFieldID(cur_move)
					&& toFieldID == MoveInt.getToFieldID(cur_move)
				) {
				
				if (MoveInt.isPromotion(cur_move)) {
					if (moveStr.endsWith("q")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_QUEEN) {
							return cur_move;
						}
					} else if (moveStr.endsWith("r")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_CASTLE) {
							return cur_move;
						}
					} else if (moveStr.endsWith("b")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_OFFICER) {
							return cur_move;
						}
					} else if (moveStr.endsWith("n")) {
						if (MoveInt.getPromotionFigureType(cur_move) == Figures.TYPE_KNIGHT) {
							return cur_move;
						}
					} else {
						throw new IllegalStateException(moveStr);
					}
				} else {
					return cur_move;
				}
			}
		}
		
		throw new IllegalStateException(bitboard + "\r\n moveStr=" + moveStr);
	}
	
	
	public final void moveToString(int move, StringBuilder result) {
		
		if (move == -1) {
			throw new IllegalStateException("move=" + move);
		}
		
		if (move == 0) {
			result.append("OOOO");
			return;
			//throw new IllegalStateException();
		}
		
		result.append(Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getFromFieldID(move)]]);
		result.append(Fields.ALL_ORDERED_NAMES[Fields.IDX_2_ORDERED_A1H1[getToFieldID(move)]]);
		
		if (isPromotion(move)) {
			int promotionFigureType = getPromotionFigureType(move);
			result.append(Figures.TYPES_SIGN[promotionFigureType].toLowerCase());
		}
	}
}
