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
package bagaturchess.learning.goldmiddle.impl1.base;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.Constants;


public class ChessBoard implements IChessBoard {
	
	
	private IBitBoard board;
	
	
	public ChessBoard(IBitBoard _board) {
		board = _board;
	}
	
	private static final long convertBB(long bb) {
		//return Bits.reverse(bb);
		return bb;
	}
	
	@Override
	public int getColorToMove() {
		return board.getColourToMove() == Constants.COLOUR_WHITE ? 0 : 1;
	}
	
	@Override
	public int getPSQTScore_o() {
		return board.getBaseEvaluation().getPST_o();
	}

	@Override
	public int getPSQTScore_e() {
		return board.getBaseEvaluation().getPST_e();
	}
	
	@Override
	public long getPieces(int colour, int type) {
		if (colour == 0) {
			return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, type));
		} else {
			return convertBB(board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, type));
		}
	}
	
	@Override
	public long getAllPieces() {
		return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE) | board.getFiguresBitboardByColour(Constants.COLOUR_BLACK));
	}
	
	@Override
	public long getFriendlyPieces(int colour) {
		if (colour == 0) {
			return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_WHITE));
		} else {
			return convertBB(board.getFiguresBitboardByColour(Constants.COLOUR_BLACK));
		}
	}
	
	@Override
	public long getEmptySpaces() {
		return convertBB(board.getFreeBitboard());
	}
	
	private int convertIndex_b2c(int index) {
		return index;//HORIZONTAL_SYMMETRY[index];
	}
	
	private int convertIndex_c2b(int index) {
		/*for (int i=0; i<HORIZONTAL_SYMMETRY.length; i++) {
			if (HORIZONTAL_SYMMETRY[i] == index) {
				return i;
			}
		}
		throw new IllegalStateException();*/
		return index;
	}
	
	@Override
	public int getKingIndex(int colour) {
		if (colour == 0) {
			long king = board.getFiguresBitboardByColourAndType(Constants.COLOUR_WHITE, Constants.TYPE_KING);
			//if (IBitBoard.IMPL1) {
				int kingIndex = Long.numberOfTrailingZeros(king);
				return convertIndex_b2c(kingIndex);
			/*} else {
				int kingIndex = Fields.get67IDByBitboard(king);
				return convertIndex_b2c(kingIndex);
			}*/
			//return convertIndex_b2c(board.getPiecesLists().getPieces(Constants.PID_W_KING).getData()[0]);
			
		} else {
			long king = board.getFiguresBitboardByColourAndType(Constants.COLOUR_BLACK, Constants.TYPE_KING);
			//if (IBitBoard.IMPL1) {
				int kingIndex = Long.numberOfTrailingZeros(king);
				return convertIndex_b2c(kingIndex);
			/*} else {
				int kingIndex = Fields.get67IDByBitboard(king);
				return convertIndex_b2c(kingIndex);
			}*/
		}
	}
	
	@Override
	public long getKingArea(int colour) {
		int index = getKingIndex(colour);
		return ChessConstants.KING_AREA[colour][index];
	}
	
	@Override
	public long getPinnedPieces() {
		return convertBB(0);
	}
	
	@Override
	public long getDiscoveredPieces() {
		return convertBB(0);
	}
	
	@Override
	public long getCheckingPieces() {
		return convertBB(0);
	}
	
	@Override
	public int getPieceType(int index) {
		
		int pid = board.getMatrix()[convertIndex_c2b(index)];
		
		if (pid == Constants.PID_NONE) {
			return ChessConstants.EMPTY;
		}
		
		switch(pid) {
			case Constants.PID_W_PAWN:
				return ChessConstants.PAWN;
			case Constants.PID_B_PAWN:
				return ChessConstants.PAWN;
			case Constants.PID_W_KNIGHT:
				return ChessConstants.NIGHT;
			case Constants.PID_B_KNIGHT:
				return ChessConstants.NIGHT;
			case Constants.PID_W_KING:
				return ChessConstants.KING;
			case Constants.PID_B_KING:
				return ChessConstants.KING;
			case Constants.PID_W_BISHOP:
				return ChessConstants.BISHOP;
			case Constants.PID_B_BISHOP:
				return ChessConstants.BISHOP;
			case Constants.PID_W_ROOK:
				return ChessConstants.ROOK;
			case Constants.PID_B_ROOK:
				return ChessConstants.ROOK;
			case Constants.PID_W_QUEEN:
				return ChessConstants.QUEEN;
			case Constants.PID_B_QUEEN:
				return ChessConstants.QUEEN;
			default:
				throw new IllegalStateException("pid=" + pid);
		}
	}
	
	
	@Override
	public IBitBoard getBoard() {
		return board;
	}
}
