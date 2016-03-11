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
package bagaturchess.bitboard.impl.eval.pawns.model;


import bagaturchess.bitboard.api.IBoard;
import bagaturchess.bitboard.api.IPiecesLists;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.state.PiecesList;


public class PawnsModel extends Fields {
	
	private static boolean GEN_PST = false;
	
	private int w_count;
	private int b_count;
	private int w_passed_count;
	private int b_passed_count;
	private int w_max_passed_rank;
	private int b_max_passed_rank;
	
	private Pawn[] w_pawns;
	private Pawn[] b_pawns;
	private Pawn[] w_passed;
	private Pawn[] b_passed;
	
	private int w_islands_count;
	private int b_islands_count;
	
	private long w_king_verticals;
	private long b_king_verticals;
	private long opened_files;
	private long w_half_opened_files;
	private long b_half_opened_files;
	private long w_weak_fields;
	private long b_weak_fields;
	
	private int w_king_opened_files_count;
	private int w_king_semi_opened_files_count_own;
	private int w_king_semi_opened_files_count_op;
	private int b_king_opened_files_count;
	private int b_king_semi_opened_files_count_own;
	private int b_king_semi_opened_files_count_op;
	
	private long w_attacks;
	private long b_attacks;
	
	private int w_unstoppablePasser_rank;
	private int b_unstoppablePasser_rank;
	
	int wKingFieldID;
	int bKingFieldID;

	private long w_space;
	private long b_space;
	
	private int[] w_pstKnight;
	private int[] b_pstKnight;
	private int[] w_pstBishop;
	private int[] b_pstBishop;
	private int[] w_pstRooks;
	private int[] b_pstRooks;
	
	
	public PawnsModel() {
		w_pawns = new Pawn[8];
		b_pawns = new Pawn[8];
		w_passed = new Pawn[8];
		b_passed = new Pawn[8];
		
		for (int i=0; i<8; i++) {
			w_pawns[i] = new Pawn();
			b_pawns[i] = new Pawn();
		}
		
		if (GEN_PST) {
			w_pstKnight = new int[64];
			b_pstKnight = new int[64];
			w_pstBishop = new int[64];
			b_pstBishop = new int[64];
			w_pstRooks = new int[64];
			b_pstRooks = new int[64];
		}
		
		reinit();
	}
	
	private void reinit() {
		w_count = 0;
		b_count = 0;
		
		w_passed_count = 0;
		b_passed_count = 0;
		
		w_max_passed_rank = 0;
		b_max_passed_rank = 0;
		
		w_islands_count = 0;
		b_islands_count = 0;
		
		w_king_verticals = 0;
		b_king_verticals = 0;
		
		opened_files = ALL_FIELDS;
		w_half_opened_files = ALL_FIELDS;
		b_half_opened_files = ALL_FIELDS;
		
		w_weak_fields = DIGIT_3 | DIGIT_4;
		b_weak_fields = DIGIT_5 | DIGIT_6;
		
		w_king_opened_files_count = 0;
		w_king_semi_opened_files_count_own = 0;
		w_king_semi_opened_files_count_op = 0;
		
		b_king_opened_files_count = 0;
		b_king_semi_opened_files_count_own = 0;
		b_king_semi_opened_files_count_op = 0;
		
		w_attacks = 0L;
		b_attacks = 0L;
		
		w_unstoppablePasser_rank = 0;
		b_unstoppablePasser_rank = 0;
		
		wKingFieldID = 0;
		bKingFieldID = 0;
	}
	
	public void rebuild(IBoard _bitboard) { 
		
		reinit();
		
		long w_pawns_board = _bitboard.getFiguresBitboardByPID(Constants.PID_W_PAWN);
		long b_pawns_board = _bitboard.getFiguresBitboardByPID(Constants.PID_B_PAWN);
		
		IPiecesLists lists = _bitboard.getPiecesLists();
		wKingFieldID = lists.getPieces(Constants.PID_W_KING).getData()[0];
		bKingFieldID = lists.getPieces(Constants.PID_B_KING).getData()[0];
		
		PiecesList w_pawns = lists.getPieces(Constants.PID_W_PAWN);
		PiecesList b_pawns = lists.getPieces(Constants.PID_B_PAWN);
		
		int w_pawns_count = w_pawns.getDataSize();
		int[] w_pawns_arr = w_pawns.getData();
		for (int i=0; i<w_pawns_count; i++) {
			int w_pawnFieldID = w_pawns_arr[i];
			Pawn cur = getForFilling(Figures.COLOUR_WHITE);
			cur.initialize(Figures.COLOUR_WHITE, _bitboard.getColourToMove(), w_pawnFieldID, w_pawns_board, b_pawns_board, wKingFieldID, bKingFieldID);
			if (cur.isPassed()) {
				w_passed[w_passed_count] = cur;
				w_passed_count++;
				if (cur.rank > w_max_passed_rank) {
					w_max_passed_rank = cur.rank;
				}
				if (cur.isPassedUnstoppable()) {
					if (cur.rank > w_unstoppablePasser_rank) {
						w_unstoppablePasser_rank = cur.rank;
					}
				}
			}
			w_half_opened_files &= ~cur.vertical;
			opened_files &= ~cur.vertical;
			w_weak_fields &= ~cur.front_neighbour;
			w_attacks |= cur.attacks;
		}
		
		int b_pawns_count = b_pawns.getDataSize();
		int[] b_pawns_arr = b_pawns.getData();
		for (int i=0; i<b_pawns_count; i++) {
			int b_pawnFieldID = b_pawns_arr[i];
			Pawn cur = getForFilling(Figures.COLOUR_BLACK);
			cur.initialize(Figures.COLOUR_BLACK, _bitboard.getColourToMove(), b_pawnFieldID, b_pawns_board, w_pawns_board, bKingFieldID, wKingFieldID);
			if (cur.isPassed()) {
				b_passed[b_passed_count] = cur;
				b_passed_count++;
				if (cur.rank > b_max_passed_rank) {
					b_max_passed_rank = cur.rank;
				}
				if (cur.isPassedUnstoppable()) {
					if (cur.rank > b_unstoppablePasser_rank) {
						b_unstoppablePasser_rank = cur.rank;
					}
				}
			}
			b_half_opened_files &= ~cur.vertical;
			opened_files &= ~cur.vertical;
			b_weak_fields &= ~cur.front_neighbour;
			b_attacks |= cur.attacks;
		}
		
		w_islands_count = fillIslands(w_pawns_board);
		b_islands_count = fillIslands(b_pawns_board);
		
		
		/**
		 * King opened and semi opened files
		 */
		fillKingOpenedAndSemiOpened(wKingFieldID, bKingFieldID);
		
		if (GEN_PST) {
			//PSTGenKnights.fillPST(w_pstKnight, Figures.COLOUR_WHITE, this, _bitboard);
			//PSTGenKnights.fillPST(b_pstKnight, Figures.COLOUR_BLACK, this, _bitboard);
			//PSTGenBishops.fillPST(w_pstBishop, Figures.COLOUR_WHITE, this, _bitboard);
			//PSTGenBishops.fillPST(b_pstBishop, Figures.COLOUR_BLACK, this, _bitboard);
			//PSTGenRooks.fillPST(w_pstRooks, Figures.COLOUR_WHITE, this, _bitboard);
			//PSTGenRooks.fillPST(b_pstRooks, Figures.COLOUR_BLACK, this, _bitboard);
		}
		
		
		/**
		 * Space of both players
		 */
		long w_space_noownpawns_and_notattacked = SPACE_WHITE
						& ~w_pawns_board //No white pawn
						& ~b_attacks; //Not attacked by black
		
		// Find all squares which are at most three squares behind some friendly pawn or are on free vertical.
		long w_behindFriendlyPawns = w_pawns_board;
		w_behindFriendlyPawns |= w_behindFriendlyPawns << 8;
		w_behindFriendlyPawns |= w_behindFriendlyPawns << 16;
		
		w_space = w_space_noownpawns_and_notattacked & (w_behindFriendlyPawns | w_half_opened_files | opened_files);
		
		long b_space_noownpawns_and_notattacked = SPACE_BLACK
		& ~b_pawns_board //No black pawn
		& ~w_attacks; //Not attacked by white

		// Find all squares which are at most three squares behind some friendly pawn or are on free vertical.
		long b_behindFriendlyPawns = b_pawns_board;
		b_behindFriendlyPawns |= b_behindFriendlyPawns >> 8;
		b_behindFriendlyPawns |= b_behindFriendlyPawns >> 16;
		
		b_space = b_space_noownpawns_and_notattacked & (b_behindFriendlyPawns | b_half_opened_files | opened_files);
	}
	

	private void fillKingOpenedAndSemiOpened(int wKingFieldID, int bKingFieldID) {
		w_king_verticals = LETTERS_NEIGHBOURS_BY_FIELD_ID[wKingFieldID] | LETTERS_BY_FIELD_ID[wKingFieldID];
		b_king_verticals = LETTERS_NEIGHBOURS_BY_FIELD_ID[bKingFieldID] | LETTERS_BY_FIELD_ID[bKingFieldID];
		
		if ((w_king_verticals & opened_files) != 0L) {
			if ((opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) != 0L) {
				w_king_opened_files_count++;
			}
			if ((opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) != 0L) {
				w_king_opened_files_count++;
			}
			if ((opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) != 0L) {
				w_king_opened_files_count++;
			}
		}
		if ((w_king_verticals & w_half_opened_files) != 0L) {
			if ((w_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) != 0L
					&& (opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) == 0L) {
				w_king_semi_opened_files_count_own++;
			}
			if ((w_half_opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) != 0L
					&& (opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) == 0L) {
				w_king_semi_opened_files_count_own++;
			}
			if ((w_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) != 0L
					&& (opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) == 0L) {
				w_king_semi_opened_files_count_own++;
			}
		}
		if ((w_king_verticals & b_half_opened_files) != 0L) {
			if ((b_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) != 0L
					&& (opened_files & LETTERS_LEFT_BY_FIELD_ID[wKingFieldID]) == 0L) {
				w_king_semi_opened_files_count_op++;
			}
			if ((b_half_opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) != 0L
					&& (opened_files & LETTERS_BY_FIELD_ID[wKingFieldID]) == 0L) {
				w_king_semi_opened_files_count_op++;
			}
			if ((b_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) != 0L
					&& (opened_files & LETTERS_RIGHT_BY_FIELD_ID[wKingFieldID]) == 0L) {
				w_king_semi_opened_files_count_op++;
			}
		}
		
		if ((b_king_verticals & opened_files) != 0L) {
			if ((opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) != 0L) {
				b_king_opened_files_count++;
			}
			if ((opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) != 0L) {
				b_king_opened_files_count++;
			}
			if ((opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) != 0L) {
				b_king_opened_files_count++;
			}
		}
		if ((b_king_verticals & b_half_opened_files) != 0L) {
			if ((b_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) != 0L
					&& (opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) == 0L) {
				b_king_semi_opened_files_count_own++;
			}
			if ((b_half_opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) != 0L
					&& (opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) == 0L) {
				b_king_semi_opened_files_count_own++;
			}
			if ((b_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) != 0L
					&& (opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) == 0L) {
				b_king_semi_opened_files_count_own++;
			}
		}
		if ((b_king_verticals & w_half_opened_files) != 0L) {
			if ((w_half_opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) != 0L
					&& (opened_files & LETTERS_LEFT_BY_FIELD_ID[bKingFieldID]) == 0L) {
				b_king_semi_opened_files_count_op++;
			}
			if ((w_half_opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) != 0L
					&& (opened_files & LETTERS_BY_FIELD_ID[bKingFieldID]) == 0L) {
				b_king_semi_opened_files_count_op++;
			}
			if ((w_half_opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) != 0L
					&& (opened_files & LETTERS_RIGHT_BY_FIELD_ID[bKingFieldID]) == 0L) {
				b_king_semi_opened_files_count_op++;
			}
		}
	}
	
	private Pawn getForFilling(int colour) { 
		if (colour == Figures.COLOUR_WHITE) { 
			return w_pawns[w_count++];
		} else {
			return b_pawns[b_count++];
		}
	}
	
	private static int fillIslands(long pawns) {
		int switchesCount = 0;
		
		boolean hasPawn;
		boolean hasPawn_backup = false;
		
		hasPawn = (pawns & LETTER_A) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_B) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_C) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_D) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_E) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_F) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_G) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}
		hasPawn_backup = hasPawn;
		
		hasPawn = (pawns & LETTER_H) != 0;
		if (hasPawn && !hasPawn_backup) {
			switchesCount++;
		}

		int result = switchesCount;
		
		return result;
	}
	
	public int getBCount() {
		return b_count;
	}
	
	public Pawn[] getBPawns() {
		return b_pawns;
	}
	
	public int getWCount() {
		return w_count;
	}
	
	public Pawn[] getWPawns() {
		return w_pawns;
	}

	public int getBIslandsCount() {
		return b_islands_count;
	}
	
	public int getWIslandsCount() {
		return w_islands_count;
	}
	
	public long getOpenedFiles() {
		return opened_files;
	}
	
	public long getWHalfOpenedFiles() {
		return w_half_opened_files;
	}

	public long getWKingVerticals() {
		return w_king_verticals;
	}
	
	public long getBHalfOpenedFiles() {
		return b_half_opened_files;
	}
	
	public long getBKingVerticals() {
		return b_king_verticals;
	}
	
	public int getWKingOpenedFiles() {
		return w_king_opened_files_count;
	}
	
	public int getBKingOpenedFiles() {
		return b_king_opened_files_count;
	}
	
	public int getWKingSemiOwnOpenedFiles() {
		return w_king_semi_opened_files_count_own;
	}
	
	public int getBKingSemiOwnOpenedFiles() {
		return b_king_semi_opened_files_count_own;
	}
	
	public int getWKingSemiOpOpenedFiles() {
		return w_king_semi_opened_files_count_op;
	}
	
	public int getBKingSemiOpOpenedFiles() {
		return b_king_semi_opened_files_count_op;
	}

	public int getBWeakFields() {
		return Utils.countBits(b_weak_fields);
	}
	
	public int getWWeakFields() {
		return Utils.countBits(w_weak_fields);
	}
	
	public long getBattacks() {
		return b_attacks;
	}

	public long getWattacks() {
		return w_attacks;
	}

	public int[] getBpstKnight() {
		return b_pstKnight;
	}

	public int[] getWpstKnight() {
		return w_pstKnight;
	}

	public int[] getBpstBishop() {
		return b_pstBishop;
	}

	public int[] getWpstBishop() {
		return w_pstBishop;
	}
	
	public int[] getBpstRook() {
		return b_pstRooks;
	}

	public int[] getWpstRook() {
		return w_pstRooks;
	}

	public Pawn[] getBPassed() {
		return b_passed;
	}

	public int getBPassedCount() {
		return b_passed_count;
	}

	public Pawn[] getWPassed() {
		return w_passed;
	}

	public int getWPassedCount() {
		return w_passed_count;
	}

	public int getBUnstoppablePasserRank() {
		return b_unstoppablePasser_rank;
	}

	public int getWUnstoppablePasserRank() {
		return w_unstoppablePasser_rank;
	}

	public int getBKingFieldID() {
		return bKingFieldID;
	}

	public int getWKingFieldID() {
		return wKingFieldID;
	}
	
	public int getWMaxPassedRank() {
		return w_max_passed_rank;
	}

	public int getBMaxPassedRank() {
		return b_max_passed_rank;
	}
	
	public long getWspace() {
		return w_space;
	}

	public long getBspace() {
		return b_space;
	}
}
