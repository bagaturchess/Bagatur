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
package bagaturchess.bitboard.impl.utils;


import java.util.Arrays;
import java.util.Comparator;

public class Sorting {
	
	private static Comparator23 comp23 = new Comparator23();
	
	public static void firstSort(int from, int to, long[][] moves, Comparator comp) {
		
		if (to - from <= 1) {
			return;
		}
		
		int index = -1;
		long[] best = null;
		
		for (int i = from; i < to; i++) {
			if (best == null) {
				best = moves[from];
				index = from;
			} else {
				if (comp.compare(moves[i], best) > 0) {
					best = moves[i];
					index = i;
				}
			}
		}
		
		if (index != from) {
			long[] tmp = moves[from];
			moves[from] = best;
			moves[index] = tmp;
		}
	}
	
	public static void bubbleSort(int from, int to, long[][] moves, Comparator<long[]> comp) {
		
		for (int i = from; i < to; i++) {
			for (int j= i + 1; j < to; j++) {
				long[] i_move = moves[i];
				long[] j_move = moves[j];
				if (comp.compare(j_move, i_move) < 0) {
					moves[i] = j_move;
					moves[j] = i_move;
				}
			}
		}
	}
	
	public static void mergeSort(int from, int to, long[][] moves, Comparator<long[]> comp) {
		if (to - from > 15) {
			Arrays.sort(moves, from, to, comp);
		} else {
			bubbleSort(from, to, moves, comp);
		}
	}
	
	public static void bubbleSort23(int from, int to, long[][] moves) {
		bubbleSort(from, to, moves, comp23);
	}
	
	public static void bubbleSortSimple(int from, int to, long[] moves) {
		for (int i = from; i < to; i++) {
			for (int j= i + 1; j < to; j++) {
				long i_val = moves[i];
				long j_val = moves[j];
				if (j_val > i_val) {
					moves[i] = j_val;
					moves[j] = i_val;
				}
			}
		}
	}
	
	public static void dump(int from, int to, long[] moves) {
		String result = "";
		for (int i=from; i<to; i++) {
			if (i != from) {
				result += ", ";
			}
			result += moves[i];
		}
		System.out.println(result);
	}
	
	public static void main(String[] args) {
		long[] moves = new long[]{1,4,2,7,5,10};
		bubbleSortSimple(0, moves.length, moves);
		dump(0, moves.length, moves);
	}
}
