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
package bagaturchess.bitboard.impl1.movegen;


import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl1.plies.Castling;


public class KingMovesGen extends KingPlies {
	
	
	static final int[][] validDirsIDs = ALL_KING_VALID_DIRS;
	static final int[][][] dirsFieldIDs = ALL_KING_DIRS_WITH_FIELD_IDS;
	static final long[][][] dirsBitBoards = ALL_KING_DIRS_WITH_BITBOARDS;

	
	public static final void genAllMoves(
			final int pid,	final int fromFieldID,
			final int[] figuresIDsPerFieldsIDs,
			int opponentKingFieldID,
			boolean kingSidePossible,
			boolean queenSidePossible,
			final IInternalMoveList list
			) {
		
		final long opponentKing = KingPlies.ALL_KING_MOVES[opponentKingFieldID] | Fields.ALL_A1H1[opponentKingFieldID];
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int toFieldID = dirs_ids[dirID][0];
			final long toBitboard = Fields.ALL_A1H1[toFieldID];
			final int targetPID = figuresIDsPerFieldsIDs[toFieldID];
			
			if ((toBitboard & opponentKing) == 0L) {
				if (targetPID == Constants.PID_NONE) {
					list.reserved_add(MoveInt.createNonCapture(pid, fromFieldID, toFieldID));
				} else {
					if (Constants.hasSameColour(pid, targetPID)) {
						//Do nothing
					} else {
						list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
					}
				}
			}
		}
		
		if (kingSidePossible) {
			int figureColour = Constants.getColourByPieceIdentity(pid);
			list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], 
					Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour],
					0, 1));
		}
		
		if (queenSidePossible) {
			int figureColour = Constants.getColourByPieceIdentity(pid);
			list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour],
					Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour],
					0, 2));
		}
	}
	
	
	public static final void genCaptureMoves(
			final int pid,
			final int fromFieldID,
			final int[] figuresIDsPerFieldsIDs,
			int opponentKingFieldID,
			final IInternalMoveList list		
		) {
		
		final long opponentKing = KingPlies.ALL_KING_MOVES[opponentKingFieldID] | Fields.ALL_A1H1[opponentKingFieldID];
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			final int dirID = validDirIDs[i];
			final int toFieldID = dirs_ids[dirID][0];
			final long toBitboard = Fields.ALL_A1H1[toFieldID];
			final int targetPID = figuresIDsPerFieldsIDs[toFieldID];
			
			if ((toBitboard & opponentKing) == 0L) {
				if (targetPID == Constants.PID_NONE) {
					//Do nothing
				} else {
					if (Constants.hasSameColour(pid, targetPID)) {
						//Do nothing
					} else {
						list.reserved_add(MoveInt.createCapture(pid, fromFieldID, toFieldID, targetPID));
					}
				}
			}
		}
	}	
}
