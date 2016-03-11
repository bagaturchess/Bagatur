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
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.attacks.control.FieldsStateMachine;

public class AttacksBuilder {
	
	/*static long genAttacks(Board bitboard,
			int colour,
			int type,
			int fieldID,
			int dirID,
			int dirType,
			FieldsStateMachine fieldAttacksCollector, boolean add) {
		//int colour = Figures.getFigureColour(figureID);
		//int type = Figures.getFigureType(figureID;
		//int figureFieldID = bitboard.fieldIDPerFigureID[figureID];
		return genAttacks(bitboard, colour, type, figureFieldID, dirID, dirType, fieldAttacksCollector, add);
	}*/
	
	static long genAttacks(Board bitboard,
			int figureColour, int figureType,
			int fieldID,
			int dirID,
			int dirType,
			FieldsStateMachine fieldAttacksCollector, boolean add) {
		
		switch (figureType) {
			case Figures.TYPE_KING:
				return King.genAttacks(figureColour, fieldID, fieldAttacksCollector, add);
			case Figures.TYPE_KNIGHT:
				return Knights.genAttacks(figureColour, fieldID, fieldAttacksCollector, add);
			case Figures.TYPE_PAWN:
				return Pawns.genAttacks(figureColour, fieldID, fieldAttacksCollector, add);
			case Figures.TYPE_OFFICER:
				return Officers.genAttacks(figureColour, fieldID, Figures.TYPE_OFFICER, dirID, bitboard, fieldAttacksCollector, add);
			case Figures.TYPE_CASTLE:
				return Castles.genAttacks(figureColour, fieldID, Figures.TYPE_CASTLE, dirID, bitboard, fieldAttacksCollector, add);
			case Figures.TYPE_QUEEN:
				return Queens.genAttacks(figureColour, fieldID, dirID, dirType, bitboard, fieldAttacksCollector, add);
			default:
				throw new IllegalStateException();
		}
	}
	
	static long genAttacks(Board bitboard,
			int figureColour, int figureType,
			int fieldID,
			int dirID,
			int dirType) {
		
		switch (figureType) {
			case Figures.TYPE_KING:
				return King.genAttacks(fieldID);
			case Figures.TYPE_KNIGHT:
				return Knights.genAttacks(fieldID);
			case Figures.TYPE_PAWN:
				return Pawns.genAttacks(figureColour, fieldID);
			case Figures.TYPE_OFFICER:
				return Officers.genAttacks(figureColour, fieldID, dirID, bitboard);
			case Figures.TYPE_CASTLE:
				return Castles.genAttacks(figureColour, fieldID, dirID, bitboard);
			case Figures.TYPE_QUEEN:
				return Queens.genAttacks(figureColour, fieldID, dirID, dirType, bitboard);
			default:
				throw new IllegalStateException();
		}
	}
}
