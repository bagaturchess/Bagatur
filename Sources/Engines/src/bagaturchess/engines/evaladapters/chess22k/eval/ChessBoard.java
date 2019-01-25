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
package bagaturchess.engines.evaladapters.chess22k.eval;


import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.engines.evaladapters.chess22k.ChessConstants;
import bagaturchess.engines.evaladapters.chess22k.EvalInfo;
import bagaturchess.engines.evaladapters.chess22k.IChessBoard;


class ChessBoard implements IChessBoard {
	
	
	private IBitBoard board;
	private EvalInfo evalinfo;
	
	private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial( new int[] {	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,
	});
	
	
	ChessBoard(IBitBoard _board) {
		board = _board;
		evalinfo = new EvalInfo();
	}
	
	private static final long convertBB(long bb) {
		//return bb;
		return Bits.reverse(bb);
	}
	
	@Override
	public EvalInfo getEvalInfo() {
		return evalinfo;
	}
	
	@Override
	public int getColorToMove() {
		return board.getColourToMove() == Constants.COLOUR_WHITE ? 0 : 1;
	}
	
	@Override
	public int getPSQTScore() {
		return board.getMaterialFactor().interpolateByFactor(board.getBaseEvaluation().getPST_o(), board.getBaseEvaluation().getPST_e());
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
			return convertIndex_b2c(board.getPiecesLists().getPieces(Constants.PID_W_KING).getData()[0]);
		} else {
			return convertIndex_b2c(board.getPiecesLists().getPieces(Constants.PID_B_KING).getData()[0]);
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
	public int interpolateScore(int scoreMg, int scoreEg) {
		return board.getMaterialFactor().interpolateByFactor(scoreMg, scoreEg);
	}
}
