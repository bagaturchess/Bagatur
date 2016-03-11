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
package bagaturchess.bitboard.impl.attacks.fast;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;

public class Knights extends Fields {

	public static final long genAttacks(int colour, int fromFieldID, FieldsStateMachine fac, boolean add) {
		
		long attacks = 0L;

		final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
		final int[][] dirFieldIDs = KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
		final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			attacks |= toBitboard;
			if (add) { 
				fac.addAttack(colour, Figures.TYPE_KNIGHT, dirFieldIDs[dirID][0], toBitboard);
			} else {
				fac.removeAttack(colour, Figures.TYPE_KNIGHT, dirFieldIDs[dirID][0], toBitboard);
			}
		}
		
		return attacks;
	}

	public static long genAttacks(int fromFieldID) {
		
		long attacks = 0L;

		final int [] validDirIDs = KnightPlies.ALL_KNIGHT_VALID_DIRS[fromFieldID];
		final long[][] dirs = KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			attacks |= toBitboard;
		}
		
		return attacks;
	}
}
