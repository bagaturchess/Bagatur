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
package bagaturchess.bitboard.impl.zobrist;

import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class ConstantStructure extends Figures {
	
	public static final long[][] MOVES_KEYS = new long[Constants.PID_MAX][Fields.ID_MAX];
	
	/**
	 * TODO: MOVES_INDEXES can be compacated. Now max size is more than 10000.
	 */
	public static final int[][][][] MOVES_INDEXES = new int[Fields.ID_MAX][DIR_MAX][SEQ_MAX][Figures.TYPE_MAX];
	public static final int MOVES_INDEXES_MAX = ID_MAX * DIR_MAX * SEQ_MAX * TYPE_MAX;
	
	public static final long[] FIGURE_TYPE = new long[Figures.TYPE_MAX];
	
	public static final long WHITE_TO_MOVE;
	//public static final long BLACK_TO_MOVE;
	
	public static final long HAS_ENPASSANT;
	//public static final long WHITE_HAS_ENPASSANT;
	//public static final long BLACK_HAS_ENPASSANT;
	
	public static final long WHITE_CASTLE_KING_SIDE;
	public static final long WHITE_CASTLE_QUEEN_SIDE;	
	public static final long BLACK_CASTLE_KING_SIDE;
	public static final long BLACK_CASTLE_QUEEN_SIDE;	
	
	public static final long[] CASTLE_KING_SIDE_BY_COLOUR = new long[COLOUR_MAX];
	public static final long[] CASTLE_QUEEN_SIDE_BY_COLOUR = new long[COLOUR_MAX]; 
	
	static {
		long[] primes = Randoms.NUMBERS;
		int count = 0;
		
		for (int pid=1; pid<Constants.PID_MAX; pid++) {
			for (int field=0; field<64; field++) {
				int field_idx = Fields.IDX_ORDERED_2_A1H1[field];
				MOVES_KEYS[pid][field_idx] = primes[count++];
			}
		}
		
		for (int i=0; i<FIGURE_TYPE.length; i++) {
			FIGURE_TYPE[i] = primes[count++];
		}
		
		WHITE_TO_MOVE = primes[count++];
		//BLACK_TO_MOVE = primes[count++];
		
		HAS_ENPASSANT = primes[count++];
		//WHITE_HAS_ENPASSANT = primes[count++];
		//BLACK_HAS_ENPASSANT = primes[count++];
		
		WHITE_CASTLE_KING_SIDE = primes[count++];
		WHITE_CASTLE_QUEEN_SIDE = primes[count++];
		BLACK_CASTLE_KING_SIDE = primes[count++];
		BLACK_CASTLE_QUEEN_SIDE = primes[count++];
		
		CASTLE_KING_SIDE_BY_COLOUR[COLOUR_WHITE] = WHITE_CASTLE_KING_SIDE;
		CASTLE_KING_SIDE_BY_COLOUR[COLOUR_BLACK] = BLACK_CASTLE_KING_SIDE;

		CASTLE_QUEEN_SIDE_BY_COLOUR[COLOUR_WHITE] = WHITE_CASTLE_QUEEN_SIDE;
		CASTLE_QUEEN_SIDE_BY_COLOUR[COLOUR_BLACK] = BLACK_CASTLE_QUEEN_SIDE;
		
		//System.out.println("Zobrist key uses " + count + " secure random numbers.");
		
		int index = 1;
		for (int i1=0; i1<Fields.ID_MAX; i1++) {
			for (int i2=0; i2<DIR_MAX; i2++) {
				for (int i3=0; i3<SEQ_MAX; i3++) {
					for (int i4=0; i4<Figures.TYPE_MAX; i4++) {
						MOVES_INDEXES[i1][i2][i3][i4] = index++;
					}
				}
			}
		}
	}
	
	public static final long getMoveHash(int pid, int fromFieldID, int toFieldID) {
		return MOVES_KEYS[pid][fromFieldID]
		       ^ MOVES_KEYS[pid][toFieldID];
	}
	
	public static final long getMoveHash(int pid, int promotionFigureType, int fromFieldID, int toFieldID) {
		return MOVES_KEYS[pid][fromFieldID]
		       ^ MOVES_KEYS[pid][toFieldID]
		       ^ FIGURE_TYPE[promotionFigureType];
	}
	
	public static final int getMoveIndex(int from, int dir, int seq) {
		return MOVES_INDEXES[from][dir][seq][0];
	}
	
	public static final int getMoveIndex(int from, int dir, int seq, int dirType) {
		return MOVES_INDEXES[from][dir][seq][dirType];
	}
	
	/*public static final String dumpParamsOfMoveIndex(int moveIndex) {
		String result = "not found";
		for (int i1=0; i1<ID_MAX; i1++) {
			for (int i2=0; i2<DIR_MAX; i2++) {
				for (int i3=0; i3<SEQ_MAX; i3++) {
					for (int i4=0; i4<TYPE_MAX; i4++) {
						if (MOVES_INDEXES[i1][i2][i3][i4] == moveIndex) {
							result = "figureID=" + i1 + " figureType=" + Figures.FIGURES_TYPES[i1] + " dir=" + i2 + " seq=" + i3 + " figureType/promotionFigureType=" + i4;
							return result;
						}
					}
				}
			}
		}
		
		return result;
	}*/
	
	public static void main(String[] args) {
		long k = MOVES_KEYS[0][0];
	}
}
