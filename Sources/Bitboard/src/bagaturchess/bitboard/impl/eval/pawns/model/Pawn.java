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

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.movegen.Move;


public class Pawn extends PawnStructureConstants {
	
	int fieldID; //ok
	int rank; //ok
	
	long field; //ok
	long vertical; //ok
	long attacks; //ok
	long front; //ok
	long front_immediate; //ok
	long front_neighbour; //ok
	long front_passer; //ok
	
	int mykingDistance; //ok
	int opkingDistance; //ok
	
	boolean isolated; //ok
	boolean backward; //ok
	boolean doubled; //ok
	boolean supported; //ok
	boolean cannot_be_supported; //ok
	boolean candidate; //ok
	boolean guard; //ok
	boolean storm; //ok
	boolean passed; //ok
	boolean passed_unstoppable; //ok
	boolean passer_hidden_couple_participant;
	int guard_remoteness; //ok
	int storm_closeness; //ok
	
	/*private Pawn(int colour, int pawnFieldID, long pawnField,
								long myPawns, long opPawns,
								int myKingFieldID, //, long myKingField,
								int opKingFieldID //long opKingField,
								//int colourToMove,
								//int myCastlingType, int opCastlingType
								) {
		
		initialize(colour, pawnFieldID, pawnField, myPawns, opPawns, myKingFieldID, opKingFieldID);
		
	}*/

	void initialize(int pawnColour, int colourToMove, int pawnFieldID, long myPawns, long opPawns, int myKingFieldID, int opKingFieldID) {
		
		isolated = false;
		backward = false;
		doubled = false;
		supported = false;
		cannot_be_supported = false;
		candidate = false;
		guard = false;
		storm = false;
		passed = false;
		passed_unstoppable = false;
		passer_hidden_couple_participant = false;
		guard_remoteness = 0;
		storm_closeness = 0;
		
		attacks = 0;
		
		this.fieldID = pawnFieldID;
		this.field = Fields.ALL_ORDERED_A1H1[pawnFieldID];
		
		vertical = LETTERS_BY_FIELD_ID[fieldID];
		
		if (pawnColour == Figures.COLOUR_WHITE) {
			
			rank = DIGITS[fieldID];
			
			attacks = (field & LETTER_A) == 0L ? field >> 7 : 0;
			attacks |= (field & LETTER_H) == 0L ? field >> 9 : 0;
			
			front_immediate = field >> 8;
			front = WHITE_FRONT_FULL[fieldID];
			front_neighbour = ~front & WHITE_PASSED[fieldID];
			
			//BACKWARD
			long backwardBoard = WHITE_BACKWARD[pawnFieldID] & myPawns;
			int backwardHits = Utils.countBits(backwardBoard);
			if (backwardHits == 0) {
				backward = true;
			}
			
			//SUPPORTED
			long supporters = WHITE_SUPPORT[pawnFieldID];
			long supportersBoard = supporters & myPawns;
			int supportersHits = Utils.countBits(supportersBoard);
			if (supportersHits > 0) {
				supported = true;
			}
			
			//CAN BE SUPPORTED
			long l_supporters = WHITE_CAN_BE_SUPPORTED_LEFT[pawnFieldID];
			if ((l_supporters & myPawns) != 0L && (opPawns & l_supporters) == 0L) {
				cannot_be_supported = true;
			}
			if (!cannot_be_supported) {
				long r_supporters = WHITE_CAN_BE_SUPPORTED_RIGHT[pawnFieldID];
				if ((r_supporters & myPawns) != 0L && (opPawns & r_supporters) == 0L) {
					cannot_be_supported = true;
				}
			}
			
			//PASSERS and UNSTOPPABLE PASSERS
			long passedBoard = WHITE_PASSED[pawnFieldID] & opPawns;
			int passedHits = Utils.countBits(passedBoard);
			if (passedHits == 0) {
				passed = true;
				
				if (colourToMove == Figures.COLOUR_WHITE) {
					long perimeter = WHITE_PASSER_PARAM[pawnFieldID];
					long opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
					passed_unstoppable = (perimeter & opKing) == 0L;
				} else {
					long perimeter = WHITE_PASSER_EXT_PARAM[pawnFieldID];
					long opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
					passed_unstoppable = (perimeter & opKing) == 0L;
				}
			}
			
			//CANDIDATE
			if (!passed && (front & opPawns) == 0) {
				if (Utils.countBits_less1s(myPawns & ~BLACK_FRONT_FULL[fieldID] & (BLACK_PASSED[pawnFieldID] | WHITE_SUPPORT[pawnFieldID])) 
						>= Utils.countBits_less1s(opPawns & WHITE_PASSED[pawnFieldID])) {
					candidate = true;
				}
			}
			
			long mykingfront = WHITE_PASSED[myKingFieldID];
			if ((field & mykingfront) != 0L ) {       
				guard = true;
				guard_remoteness = getDigitsDiff(pawnFieldID, 0); //Field id = 0 means the last white line
			}
			
			long opkingfront = BLACK_PASSED[opKingFieldID];
			if ((field & opkingfront) != 0L ) {       
				storm = true;
				storm_closeness = getDigitsDiff(pawnFieldID, opKingFieldID);
			}
			
			mykingDistance = getDistance(pawnFieldID, myKingFieldID);
			opkingDistance = getDistance(pawnFieldID, opKingFieldID);
			
		} else {
			rank = 7 - DIGITS[fieldID];
			
			attacks = (field & LETTER_H) == 0L ? field << 7 : 0;
			attacks |= (field & LETTER_A) == 0L ? field << 9 : 0;
			
			front_immediate = field << 8;
			front = BLACK_FRONT_FULL[fieldID];
			front_neighbour = ~front & BLACK_PASSED[fieldID];		
			
			//BACKWARD
			long backwardBoard = BLACK_BACKWARD[pawnFieldID] & myPawns;
			int backwardHits = Utils.countBits(backwardBoard);
			if (backwardHits == 0) {
				backward = true;
			}
			
			//SUPPORTED
			long supporters = BLACK_SUPPORT[pawnFieldID];
			long supportersBoard = supporters & myPawns;
			int supportersHits = Utils.countBits(supportersBoard);
			if (supportersHits > 0) {
				supported = true;
			}
			
			//CAN BE SUPPORTED
			long l_supporters = BLACK_CAN_BE_SUPPORTED_LEFT[pawnFieldID];
			if ((l_supporters & myPawns) != 0L && (opPawns & l_supporters) == 0L) {
				cannot_be_supported = true;
			}
			if (!cannot_be_supported) {
				long r_supporters = BLACK_CAN_BE_SUPPORTED_RIGHT[pawnFieldID];
				if ((r_supporters & myPawns) != 0L && (opPawns & r_supporters) == 0L) {
					cannot_be_supported = true;
				}
			}
			
			//PASSERS and UNSTOPPABLE PASSERS
			long passedBoard = BLACK_PASSED[pawnFieldID] & opPawns;
			int passedHits = Utils.countBits(passedBoard);
			if (passedHits == 0) {
				passed = true;
				
				if (colourToMove == Figures.COLOUR_BLACK) {
					long perimeter = BLACK_PASSER_PARAM[pawnFieldID];
					long opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
					passed_unstoppable = (perimeter & opKing) == 0L;
				} else {
					long perimeter = BLACK_PASSER_EXT_PARAM[pawnFieldID];
					long opKing = Fields.ALL_ORDERED_A1H1[opKingFieldID];
					passed_unstoppable = (perimeter & opKing) == 0L;
				}
			}
			
			//CANDIDATE
			if (!passed && (front & opPawns) == 0) {
				if (Utils.countBits_less1s(myPawns & ~WHITE_FRONT_FULL[fieldID] & (WHITE_PASSED[pawnFieldID] | BLACK_SUPPORT[pawnFieldID])) 
						>= Utils.countBits_less1s(opPawns & BLACK_PASSED[pawnFieldID])) {
					candidate = true;
				}
			}
			
			long mykingfront = BLACK_PASSED[myKingFieldID];
			if ((field & mykingfront) != 0L ) {       
				guard = true;
				guard_remoteness = getDigitsDiff(pawnFieldID, 63); //Field id = 63 means the last black line
			}
			
			long opkingfront = WHITE_PASSED[opKingFieldID];
			if ((field & opkingfront) != 0L ) {       
				storm = true;
				storm_closeness = getDigitsDiff(pawnFieldID, opKingFieldID);
			}
			
			mykingDistance = getDistance(pawnFieldID, myKingFieldID);
			opkingDistance = getDistance(pawnFieldID, opKingFieldID);
		}
		
		front_passer = front | front_neighbour;
		
		long isolatedBoard = LETTERS_NEIGHBOURS_BY_FIELD_ID[pawnFieldID] & myPawns;
		int isolatedHits = Utils.countBits(isolatedBoard);
		if (isolatedHits == 0) {
			isolated = true;
		}
		
		long doubledBoard = front & myPawns;
		int doubledHits = Utils.countBits(doubledBoard);
		if (doubledHits > 0) {
			doubled = true;
		}
		
		/*if (supported && !cannot_be_supported) {
			throw new IllegalStateException();
		}*/
	}

	public static void main(String[] args) {
		long field = H2;
		System.out.println(Move.toBinaryStringMatrix(field));
		
		long left = (field & LETTER_H) == 0L ? field << 7 : 0;
		System.out.println(Move.toBinaryStringMatrix(left));
		
		long right = (field & LETTER_A) == 0L ? field << 9 : 0;
		System.out.println(Move.toBinaryStringMatrix(right));
	}

	public long getField() {
		return field;
	}

	public int getFieldID() {
		return fieldID;
	}
	
	public boolean isPassed() {
		return passed;
	}

	public boolean isCandidate() {
		return candidate;
	}
	
	public boolean isPassedUnstoppable() {
		return passed_unstoppable;
	}

	public boolean isGuard() {
		return guard;
	}

	public int getGuardRemoteness() {
		return guard_remoteness;
	}

	public boolean isStorm() {
		return storm;
	}

	public int getStormCloseness() {
		return storm_closeness;
	}

	public int getRank() {
		return rank;
	}

	public boolean isDoubled() {
		return doubled;
	}

	public boolean isIsolated() {
		return isolated;
	}

	public boolean isBackward() {
		return backward;
	}

	public boolean cannotBeSupported() {
		return cannot_be_supported;
	}

	public boolean isSupported() {
		return supported;
	}

	public long getVertical() {
		return vertical;
	}
	
	public long getFront() {
		return front;
	}
}
