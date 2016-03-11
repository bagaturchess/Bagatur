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
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class PSTConstants {
	
	private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial ( new int[]{	
			   0,   1,   2,   3,   4,   5,   6,   7,
			   8,   9,  10,  11,  12,  13,  14,  15,
			  16,  17,  18,  19,  20,  21,  22,  23,
			  24,  25,  26,  27,  28,  29,  30,  31,
			  32,  33,  34,  35,  36,  37,  38,  39,
			  40,  41,  42,  43,  44,  45,  46,  47,
			  48,  49,  50,  51,  52,  53,  54,  55,
			  56,  57,  58,  59,  60,  61,  62,  63,
	});
	
	private IBoardConfig boardCfg;
	
	private double MIN_SCORES_PAWN_O;
	private double MIN_SCORES_PAWN_E;
	private double MIN_SCORES_KING_O;
	private double MIN_SCORES_KING_E;
	private double MIN_SCORES_KNIGHT_O;
	private double MIN_SCORES_KNIGHT_E;
	private double MIN_SCORES_BISHOP_O;
	private double MIN_SCORES_BISHOP_E;
	private double MIN_SCORES_ROOK_O;
	private double MIN_SCORES_ROOK_E;
	private double MIN_SCORES_QUEEN_O;
	private double MIN_SCORES_QUEEN_E;
	
	private double MAX_SCORES_PAWN_O;
	private double MAX_SCORES_PAWN_E;
	private double MAX_SCORES_KING_O;
	private double MAX_SCORES_KING_E;
	private double MAX_SCORES_KNIGHT_O;
	private double MAX_SCORES_KNIGHT_E;
	private double MAX_SCORES_BISHOP_O;
	private double MAX_SCORES_BISHOP_E;
	private double MAX_SCORES_ROOK_O;
	private double MAX_SCORES_ROOK_E;
	private double MAX_SCORES_QUEEN_O;
	private double MAX_SCORES_QUEEN_E;
	
	
	public PSTConstants(IBoardConfig _boardCfg) {
		boardCfg = _boardCfg;
		
		MAX_SCORES_PAWN_O = getMax(boardCfg.getPST_PAWN_O()) * boardCfg.getWeight_PST_PAWN_O();
		MAX_SCORES_PAWN_E = getMax(boardCfg.getPST_PAWN_E()) * boardCfg.getWeight_PST_PAWN_E();

		MAX_SCORES_KING_O = getMax(boardCfg.getPST_KING_O()) * boardCfg.getWeight_PST_KING_O();
		MAX_SCORES_KING_E = getMax(boardCfg.getPST_KING_E()) * boardCfg.getWeight_PST_KING_E();

		MAX_SCORES_KNIGHT_O = getMax(boardCfg.getPST_KNIGHT_O()) * boardCfg.getWeight_PST_KNIGHT_O();
		MAX_SCORES_KNIGHT_E = getMax(boardCfg.getPST_KNIGHT_E()) * boardCfg.getWeight_PST_KNIGHT_E();

		MAX_SCORES_BISHOP_O = getMax(boardCfg.getPST_BISHOP_O()) * boardCfg.getWeight_PST_BISHOP_O();
		MAX_SCORES_BISHOP_E = getMax(boardCfg.getPST_BISHOP_E()) * boardCfg.getWeight_PST_BISHOP_E();

		MAX_SCORES_ROOK_O = getMax(boardCfg.getPST_ROOK_O()) * boardCfg.getWeight_PST_ROOK_O();
		MAX_SCORES_ROOK_E = getMax(boardCfg.getPST_ROOK_E()) * boardCfg.getWeight_PST_ROOK_E();

		MAX_SCORES_QUEEN_O = getMax(boardCfg.getPST_QUEEN_O()) * boardCfg.getWeight_PST_QUEEN_O();
		MAX_SCORES_QUEEN_E = getMax(boardCfg.getPST_QUEEN_E()) * boardCfg.getWeight_PST_QUEEN_E();
		
		MIN_SCORES_PAWN_O = getMin(boardCfg.getPST_PAWN_O()) * boardCfg.getWeight_PST_PAWN_O();
		MIN_SCORES_PAWN_E = getMin(boardCfg.getPST_PAWN_E()) * boardCfg.getWeight_PST_PAWN_E();

		MIN_SCORES_KING_O = getMin(boardCfg.getPST_KING_O()) * boardCfg.getWeight_PST_KING_O();
		MIN_SCORES_KING_E = getMin(boardCfg.getPST_KING_E()) * boardCfg.getWeight_PST_KING_E();

		MIN_SCORES_KNIGHT_O = getMin(boardCfg.getPST_KNIGHT_O()) * boardCfg.getWeight_PST_KNIGHT_O();
		MIN_SCORES_KNIGHT_E = getMin(boardCfg.getPST_KNIGHT_E()) * boardCfg.getWeight_PST_KNIGHT_E();

		MIN_SCORES_BISHOP_O = getMin(boardCfg.getPST_BISHOP_O()) * boardCfg.getWeight_PST_BISHOP_O();
		MIN_SCORES_BISHOP_E = getMin(boardCfg.getPST_BISHOP_E()) * boardCfg.getWeight_PST_BISHOP_E();

		MIN_SCORES_ROOK_O = getMin(boardCfg.getPST_ROOK_O()) * boardCfg.getWeight_PST_ROOK_O();
		MIN_SCORES_ROOK_E = getMin(boardCfg.getPST_ROOK_E()) * boardCfg.getWeight_PST_ROOK_E();

		MIN_SCORES_QUEEN_O = getMin(boardCfg.getPST_QUEEN_O()) * boardCfg.getWeight_PST_QUEEN_O();
		MIN_SCORES_QUEEN_E = getMin(boardCfg.getPST_QUEEN_E()) * boardCfg.getWeight_PST_QUEEN_E();
	}
	
	
	private double getMax(double[] arr) {
		double max = -1000000;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] > max) { 
				max = arr[i];
			}
		}
		return max;
	}
	
	private double getMin(double[] arr) {
		double min = 1000000;
		for (int i=0; i<arr.length; i++) {
			if (arr[i] < min) { 
				min = arr[i];
			}
		}
		return min;
	}
	
	public final double getMoveScores_o(int move) {
		int type = MoveInt.getFigureType(move);
		int from = MoveInt.getFromFieldID(move);
		int to = MoveInt.getToFieldID(move);
		if (!MoveInt.isWhite(move)) {
			from = HORIZONTAL_SYMMETRY[from];
			to = HORIZONTAL_SYMMETRY[to];
		}
		double[] pst = getArray_o(type);
		return pst[to] - pst[from];
	}
	
	public final double getMoveScores_e(int move) {
		int type = MoveInt.getFigureType(move);
		int from = MoveInt.getFromFieldID(move);
		int to = MoveInt.getToFieldID(move);
		if (!MoveInt.isWhite(move)) {
			from = HORIZONTAL_SYMMETRY[from];
			to = HORIZONTAL_SYMMETRY[to];
		}
		double[] pst = getArray_e(type);
		return pst[to] - pst[from];
	}
	
	public final double getMoveMaxScores_o(int move) {
		int type = MoveInt.getFigureType(move);
		switch(type) {
			case Constants.TYPE_PAWN:
				return MAX_SCORES_PAWN_O;
			case Constants.TYPE_KING:
				return MAX_SCORES_KING_O;
			case Constants.TYPE_KNIGHT:
				return MAX_SCORES_KNIGHT_O;
			case Constants.TYPE_BISHOP:
				return MAX_SCORES_BISHOP_O;
			case Constants.TYPE_ROOK:
				return MAX_SCORES_ROOK_O;
			case Constants.TYPE_QUEEN:
				return MAX_SCORES_QUEEN_O;
			default:
				throw new IllegalStateException();
		}
	}
	
	public final double getMoveMaxScores_e(int move) {
		int type = MoveInt.getFigureType(move);
		switch(type) {
			case Constants.TYPE_PAWN:
				return MAX_SCORES_PAWN_E;
			case Constants.TYPE_KING:
				return MAX_SCORES_KING_E;
			case Constants.TYPE_KNIGHT:
				return MAX_SCORES_KNIGHT_E;
			case Constants.TYPE_BISHOP:
				return MAX_SCORES_BISHOP_E;
			case Constants.TYPE_ROOK:
				return MAX_SCORES_ROOK_E;
			case Constants.TYPE_QUEEN:
				return MAX_SCORES_QUEEN_E;
			default:
				throw new IllegalStateException();
		}
	}
	
	public final double getMoveMinScores_o(int move) {
		int type = MoveInt.getFigureType(move);
		switch(type) {
			case Constants.TYPE_PAWN:
				return MIN_SCORES_PAWN_O;
			case Constants.TYPE_KING:
				return MIN_SCORES_KING_O;
			case Constants.TYPE_KNIGHT:
				return MIN_SCORES_KNIGHT_O;
			case Constants.TYPE_BISHOP:
				return MIN_SCORES_BISHOP_O;
			case Constants.TYPE_ROOK:
				return MIN_SCORES_ROOK_O;
			case Constants.TYPE_QUEEN:
				return MIN_SCORES_QUEEN_O;
			default:
				throw new IllegalStateException();
		}
	}
	
	public final double getMoveMinScores_e(int move) {
		int type = MoveInt.getFigureType(move);
		switch(type) {
			case Constants.TYPE_PAWN:
				return MIN_SCORES_PAWN_E;
			case Constants.TYPE_KING:
				return MIN_SCORES_KING_E;
			case Constants.TYPE_KNIGHT:
				return MIN_SCORES_KNIGHT_E;
			case Constants.TYPE_BISHOP:
				return MIN_SCORES_BISHOP_E;
			case Constants.TYPE_ROOK:
				return MIN_SCORES_ROOK_E;
			case Constants.TYPE_QUEEN:
				return MIN_SCORES_QUEEN_E;
			default:
				throw new IllegalStateException();
		}
	}
	
	public final double getPieceScores_o(int field, int type) {
		double[] pst = getArray_o(type);
		return pst[field];
	}
	
	public final double getPieceScores_e(int field, int type) {
		double[] pst = getArray_e(type);
		return pst[field];

	}
	
	public final double[] getArray_o(int type) {
		switch(type) {
			case Constants.TYPE_PAWN:
				return boardCfg.getPST_PAWN_O();
			case Constants.TYPE_KING:
				return boardCfg.getPST_KING_O();
			case Constants.TYPE_KNIGHT:
				return boardCfg.getPST_KNIGHT_O();
			case Constants.TYPE_BISHOP:
				return boardCfg.getPST_BISHOP_O();
			case Constants.TYPE_ROOK:
				return boardCfg.getPST_ROOK_O();
			case Constants.TYPE_QUEEN:
				return boardCfg.getPST_QUEEN_O();
			default:
				throw new IllegalStateException();
		}
	}
	
	public final double[] getArray_e(int type) {
		switch(type) {
			case Constants.TYPE_PAWN:
				return boardCfg.getPST_PAWN_E();
			case Constants.TYPE_KING:
				return boardCfg.getPST_KING_E();
			case Constants.TYPE_KNIGHT:
				return boardCfg.getPST_KNIGHT_E();
			case Constants.TYPE_BISHOP:
				return boardCfg.getPST_BISHOP_E();
			case Constants.TYPE_ROOK:
				return boardCfg.getPST_ROOK_E();
			case Constants.TYPE_QUEEN:
				return boardCfg.getPST_QUEEN_E();
			default:
				throw new IllegalStateException();
		}
	}
	
	private final double getWeight_o(int type) {
		switch(type) {
			case Constants.TYPE_PAWN:
				return boardCfg.getWeight_PST_PAWN_O();
			case Constants.TYPE_KING:
				return boardCfg.getWeight_PST_KING_O();
			case Constants.TYPE_KNIGHT:
				return boardCfg.getWeight_PST_KNIGHT_O();
			case Constants.TYPE_BISHOP:
				return boardCfg.getWeight_PST_BISHOP_O();
			case Constants.TYPE_ROOK:
				return boardCfg.getWeight_PST_ROOK_O();
			case Constants.TYPE_QUEEN:
				return boardCfg.getWeight_PST_QUEEN_O();
			default:
				throw new IllegalStateException();
		}
	}
	
	private final double getWeight_e(int type) {
		switch(type) {
			case Constants.TYPE_PAWN:
				return boardCfg.getWeight_PST_PAWN_E();
			case Constants.TYPE_KING:
				return boardCfg.getWeight_PST_KING_E();
			case Constants.TYPE_KNIGHT:
				return boardCfg.getWeight_PST_KNIGHT_E();
			case Constants.TYPE_BISHOP:
				return boardCfg.getWeight_PST_BISHOP_E();
			case Constants.TYPE_ROOK:
				return boardCfg.getWeight_PST_ROOK_E();
			case Constants.TYPE_QUEEN:
				return boardCfg.getWeight_PST_QUEEN_E();
			default:
				throw new IllegalStateException();
		}
	}
}
