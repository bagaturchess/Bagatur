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
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.KnightPlies;

public class PathFinders extends Fields {
	
	public static boolean findKnightPaths(int maxmoves, int fromFieldID, int toFieldID) {
	
		boolean result = false;
		
		if (maxmoves == 0) {
			return fromFieldID == toFieldID;
		}
		
		final int[] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
		final int[][] dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
		//final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			int cur_toFieldID = dirs_ids[dirID][0];
			result = findKnightPaths(maxmoves - 1, cur_toFieldID, toFieldID);
			if (result) {
				break;
			}
		}
		
		return result;
	}
	
	public static long findKnightPaths(int maxmoves, int fromFieldID) {
		
		long result = 0L;
		
		if (maxmoves == 0) {
			return result;
		}
		
		final int[] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
		final int[][] dirs_ids = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
		final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			int toFieldID = dirs_ids[dirID][0];
			long board = dirs[dirID][0];
			
			result |= board;
			result |= findKnightPaths(maxmoves - 1, toFieldID);
		}
		
		return result;
	}
	
	public static void genAll() {
		for (int i=0; i<ALL_ORDERED_A1H1.length; i++) {
			int fromID = get67IDByBitboard(ALL_ORDERED_A1H1[i]);
			for (int j=0; j<ALL_ORDERED_A1H1.length; j++) {
				int toID = get67IDByBitboard(ALL_ORDERED_A1H1[j]);
				if (fromID != toID) {
					boolean res = findKnightPaths(2, fromID, toID);
					if (res) {
						System.out.println("*********************************");
						System.out.println(Bits.toBinaryStringMatrix(ALL_A1H1[fromID]));
						System.out.println(Bits.toBinaryStringMatrix(ALL_A1H1[toID]));
						System.out.println("*********************************");
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		//genAll();
		int from = get67IDByBitboard(E4);
		//int to = get67IDByBitboard(E2);
		//findKnightPaths(3, from, to);
		long res = findKnightPaths(2, from);
		String matrix = Bits.toBinaryStringMatrix(res);
		System.out.println(matrix);
	}
}
