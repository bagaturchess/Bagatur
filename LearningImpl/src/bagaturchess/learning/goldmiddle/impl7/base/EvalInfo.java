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
package bagaturchess.learning.goldmiddle.impl7.base;


import static bagaturchess.bitboard.impl1.internal.ChessConstants.BISHOP;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.BLACK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.KING;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.NIGHT;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.PAWN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.QUEEN;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.ROOK;
import static bagaturchess.bitboard.impl1.internal.ChessConstants.WHITE;
import bagaturchess.bitboard.impl1.internal.Bitboard;
import bagaturchess.bitboard.impl1.internal.ChessBoard;
import bagaturchess.bitboard.impl1.internal.ChessConstants;
import bagaturchess.bitboard.impl1.internal.StaticMoves;


public class EvalInfo {
	
	
	private static final int FLAG_PAWN = 1 << (ChessConstants.PAWN - 1);
	private static final int FLAG_NIGHT = 1 << (ChessConstants.NIGHT - 1);
	private static final int FLAG_BISHOP = 1 << (ChessConstants.BISHOP - 1);
	private static final int FLAG_ROOK = 1 << (ChessConstants.ROOK - 1);
	private static final int FLAG_QUEEN = 1 << (ChessConstants.QUEEN - 1);

	private static final int[] FLAGS = new int[] { 0, FLAG_PAWN, FLAG_NIGHT, FLAG_BISHOP, FLAG_ROOK, FLAG_QUEEN };
	
	
	public long bb_free;
	public long bb_all;
	public long bb_all_w_pieces;
	public long bb_all_b_pieces;
	public long bb_w_pawns;
	public long bb_b_pawns;
	public long bb_w_bishops;
	public long bb_b_bishops;
	public long bb_w_knights;
	public long bb_b_knights;
	public long bb_w_queens;
	public long bb_b_queens;
	public long bb_w_rooks;
	public long bb_b_rooks;
	public long bb_w_king;
	public long bb_b_king;
	
	public int[] kingIndex = new int[2];
	public int materialKey;
	public int colorToMove;
	public long discoveredPieces;
	public long pinnedPieces;
	
	public final long[][] attacks = new long[2][7];
	public final long[] attacksAll = new long[2];
	public final long[] doubleAttacks = new long[2];
	public final int[] kingAttackersFlag = new int[2];
	
	public long passedPawnsAndOutposts;
	
	
	public int eval_o_part1;
	public int eval_e_part1;
	public int eval_o_part2;
	public int eval_e_part2;
	public int eval_o_part3;
	public int eval_e_part3;
	public int eval_o_part4;
	public int eval_e_part4;
	public int eval_o_part5;
	public int eval_e_part5;
	
	
	public final void clearEvals() {
		eval_o_part1 = 0;
		eval_e_part1 = 0;
		eval_o_part2 = 0;
		eval_e_part2 = 0;
		eval_o_part3 = 0;
		eval_e_part3 = 0;
		eval_o_part4 = 0;
		eval_e_part4 = 0;
		eval_o_part5 = 0;
		eval_e_part5 = 0;
	}
	
	
	public final void clearAttacks() {
		kingAttackersFlag[WHITE] = 0;
		kingAttackersFlag[BLACK] = 0;
		attacks[WHITE][NIGHT] = 0;
		attacks[BLACK][NIGHT] = 0;
		attacks[WHITE][BISHOP] = 0;
		attacks[BLACK][BISHOP] = 0;
		attacks[WHITE][ROOK] = 0;
		attacks[BLACK][ROOK] = 0;
		attacks[WHITE][QUEEN] = 0;
		attacks[BLACK][QUEEN] = 0;
		attacksAll[WHITE] = 0;
		attacksAll[BLACK] = 0;
		doubleAttacks[WHITE] = 0;
		doubleAttacks[BLACK] = 0;
	}
	
	
	public final void fillBoardInfo(final ChessBoard cb) {
		bb_w_pawns = cb.pieces[WHITE][PAWN];
		bb_b_pawns = cb.pieces[BLACK][PAWN];
		bb_w_bishops = cb.pieces[WHITE][BISHOP];
		bb_b_bishops = cb.pieces[BLACK][BISHOP];
		bb_w_knights = cb.pieces[WHITE][NIGHT];
		bb_b_knights = cb.pieces[BLACK][NIGHT];
		bb_w_queens = cb.pieces[WHITE][QUEEN];
		bb_b_queens = cb.pieces[BLACK][QUEEN];
		bb_w_rooks = cb.pieces[WHITE][ROOK];
		bb_b_rooks = cb.pieces[BLACK][ROOK];
		bb_w_king = cb.pieces[WHITE][KING];
		bb_b_king = cb.pieces[BLACK][KING];
		bb_all_w_pieces = bb_w_pawns | bb_w_bishops | bb_w_knights | bb_w_queens | bb_w_rooks | bb_w_king;
		bb_all_b_pieces = bb_b_pawns | bb_b_bishops | bb_b_knights | bb_b_queens | bb_b_rooks | bb_b_king;
		bb_all = bb_all_w_pieces | bb_all_b_pieces;
		bb_free = ~bb_all;
		
		kingIndex[WHITE] = cb.kingIndex[WHITE];
		kingIndex[BLACK] = cb.kingIndex[BLACK];
		
		materialKey = cb.materialKey;
		
		colorToMove = cb.colorToMove;
		
		discoveredPieces = cb.discoveredPieces;
		pinnedPieces = cb.pinnedPieces;
	}
	
	
	public final void updatePawnAttacks() {
		updatePawnAttacks(Bitboard.getWhitePawnAttacks(getPieces(WHITE, PAWN) & ~pinnedPieces), WHITE);
		updatePawnAttacks(Bitboard.getBlackPawnAttacks(getPieces(BLACK, PAWN) & ~pinnedPieces), BLACK);
	}
	
	
	private final void updatePawnAttacks(final long pawnAttacks, final int color) {
		attacks[color][PAWN] = pawnAttacks;
		if ((pawnAttacks & ChessConstants.KING_AREA[1 - color][kingIndex[1 - color]]) != 0) {
			kingAttackersFlag[color] = FLAG_PAWN;
		}
		long pinned = getPieces(color, PAWN) & pinnedPieces;
		while (pinned != 0) {
			attacks[color][PAWN] |= StaticMoves.PAWN_ATTACKS[color][Long.numberOfTrailingZeros(pinned)]
					& ChessConstants.PINNED_MOVEMENT[Long.numberOfTrailingZeros(pinned)][kingIndex[color]];
			pinned &= pinned - 1;
		}
		attacksAll[color] = attacks[color][PAWN];
	}
	
	
	public final void updateAttacks(final long moves, final int piece, final int color, final long kingArea) {
		if ((moves & kingArea) != 0) {
			kingAttackersFlag[color] |= FLAGS[piece];
		}
		doubleAttacks[color] |= attacksAll[color] & moves;
		attacksAll[color] |= moves;
		attacks[color][piece] |= moves;
	}
	
	
	public final long getFriendlyPieces(int colour) {
		return colour == WHITE ? bb_all_w_pieces : bb_all_b_pieces;
	}
	
	
	public final long getPieces(int colour, int type) {
		switch (type) {
			case PAWN:
				return colour == WHITE ? bb_w_pawns : bb_b_pawns;
			case NIGHT:
				return colour == WHITE ? bb_w_knights : bb_b_knights;
			case BISHOP:
				return colour == WHITE ? bb_w_bishops : bb_b_bishops;
			case ROOK:
				return colour == WHITE ? bb_w_rooks : bb_b_rooks;
			case QUEEN:
				return colour == WHITE ? bb_w_queens : bb_b_queens;
			case KING:
				return colour == WHITE ? bb_w_king : bb_b_king;
			default:
				throw new IllegalStateException();
		}
	}
}
