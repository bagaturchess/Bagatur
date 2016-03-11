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

import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;

public class Queens extends Fields {
	
	public static final long genAttacks(int colour, int queenFieldID, int dirID, int dirType, Board bitboard, FieldsStateMachine fac, boolean add) {
		long attacks = NUMBER_0;
		
		if (dirType != -1 && dirType != Figures.TYPE_CASTLE && dirType != Figures.TYPE_OFFICER) {
			throw new IllegalStateException();
		}
		
		if (dirType == -1) {
			attacks |= Castles.genAttacks(colour, queenFieldID, Figures.TYPE_QUEEN, dirID, bitboard, fac, add);
			attacks |= Officers.genAttacks(colour, queenFieldID, Figures.TYPE_QUEEN, dirID, bitboard, fac, add);
		} else {
			if (dirType == Figures.TYPE_CASTLE) {
				attacks |= Castles.genAttacks(colour, queenFieldID, Figures.TYPE_QUEEN, dirID, bitboard, fac, add);
			} else {
				attacks |= Officers.genAttacks(colour, queenFieldID, Figures.TYPE_QUEEN, dirID, bitboard, fac, add);
			}
		}
		
		return attacks;
	}

	public static long genAttacks(int colour, int queenFieldID, int dirID, int dirType, Board bitboard) {
		long attacks = NUMBER_0;
		
		if (dirType != -1 && dirType != Figures.TYPE_CASTLE && dirType != Figures.TYPE_OFFICER) {
			throw new IllegalStateException();
		}
		
		if (dirType == -1) {
			attacks |= Castles.genAttacks(colour, queenFieldID, dirID, bitboard);
			attacks |= Officers.genAttacks(colour, queenFieldID, dirID, bitboard);
		} else {
			if (dirType == Figures.TYPE_CASTLE) {
				attacks |= Castles.genAttacks(colour, queenFieldID, dirID, bitboard);
			} else {
				attacks |= Officers.genAttacks(colour, queenFieldID, dirID, bitboard);
			}
		}
		
		return attacks;
	}
}
