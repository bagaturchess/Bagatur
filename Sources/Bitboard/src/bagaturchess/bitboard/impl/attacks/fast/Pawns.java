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
import bagaturchess.bitboard.impl.plies.BlackPawnPlies;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;

public class Pawns extends Fields {
	
	public static final long genAttacks(int pawnColour, int pawnFieldID, FieldsStateMachine fac, boolean add) {

		long attacks = NUMBER_0;
		
		int [] validDirIDs = pawnColour == Figures.COLOUR_WHITE ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[pawnFieldID] :
																													BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[pawnFieldID];
		
		long[][] dirs = pawnColour == Figures.COLOUR_WHITE ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID] :
																										 BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID];
		
		final int[][] dirFieldIDs = pawnColour == Figures.COLOUR_WHITE ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[pawnFieldID] :
																																		BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[pawnFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			
			long toBitboard = dirs[dirID][0];
			
			attacks |= toBitboard;
			if (add) { 
				fac.addAttack(pawnColour, Figures.TYPE_PAWN, dirFieldIDs[dirID][0], toBitboard);
			} else {
				fac.removeAttack(pawnColour, Figures.TYPE_PAWN, dirFieldIDs[dirID][0], toBitboard);
			}
		}
		
		return attacks;
	}

	public static long genAttacks(int pawnColour, int pawnFieldID) {

		long attacks = NUMBER_0;
		
		int [] validDirIDs = pawnColour == Figures.COLOUR_WHITE ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[pawnFieldID] :
																													BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[pawnFieldID];
		
		long[][] dirs = pawnColour == Figures.COLOUR_WHITE ? WhitePawnPlies.ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID] :
																										 BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[pawnFieldID];
		
		int size = validDirIDs.length;
		
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			
			long toBitboard = dirs[dirID][0];
			
			attacks |= toBitboard;
		}
		
		return attacks;
	}
}
