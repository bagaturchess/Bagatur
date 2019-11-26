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
package bagaturchess.bitboard.impl.movegen;

import bagaturchess.bitboard.api.IInternalMoveList;
import bagaturchess.bitboard.impl.Board;
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.checking.Checking;
import bagaturchess.bitboard.impl.plies.specials.Castling;

/**
 * Do not use OOP in moves generators,
 * because of performance degradation caused by the impossibility to inline abstract methods at compile time. 
 */
public class KingMovesGen extends KingPlies {
	
	static final int figureType = Figures.TYPE_KING;
	
	static final int[][] validDirsIDs = ALL_KING_VALID_DIRS;
	static final int[][][] dirsFieldIDs = ALL_KING_DIRS_WITH_FIELD_IDS;
	static final long[][][] dirsBitBoards = ALL_KING_DIRS_WITH_BITBOARDS;

	
	public static final int genAllMoves(final boolean checkAware,
			final Board bitboard,
			final long excludedToFieldsIDs,
			final int figureID, final int figureColour, final int opponentColour,
			final long fromBitboard,
			final int fromFieldID,
			final long freeBitboard,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final boolean kingSidePossible,
			final boolean queenSidePossible,
			final long opponentKing,
			final int opponentKingFieldID,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		final long[][] dirs = dirsBitBoards[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				continue;
			}
			
			if ((toBitboard & allMineBitboard) == 0L
				&& (toBitboard & opponentKing) == 0L
				&& (!checkAware || (checkAware && (toBitboard & opponentKingMoves) == 0L))) {
				
				final int toFieldID = dirs_ids[dirID][0];
				
				if (checkAware) {
					if (Checking.isFieldAttacked(bitboard, opponentColour, figureColour,
							toBitboard, toFieldID, freeBitboard | fromBitboard, true)) {
						continue;
					}
				}
				
				if (list != null) {
	
					if ((toBitboard & allOpponentBitboard) != 0L) {//Capture						
						int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
						list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));						
					} else {
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
				}
				count++;
				
				if (count >= maxCount) {
					return count;
				}
			}
		}
		
		if (kingSidePossible) {
			if (list != null) {
				list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], 
						Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
			}
			count++;
			
			if (count >= maxCount) {
				return count;
			}
		}
		
		if (queenSidePossible) {
			if (list != null) {
				list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour],
						Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
			}
			count++;
			
			if (count >= maxCount) {
				return count;
			}
		}
		
		return count;
	}
	
	public static final int genCaptureMoves(final Board bitboard,
			final long excludedToFieldsIDs,
			final int figureID, final int figureColour, final int opponentColour,
			final long fromBitboard,
			final int fromFieldID,
			final long freeBitboard,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final long opponentKing,
			final int opponentKingFieldID,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		final long[][] dirs = dirsBitBoards[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				continue;
			}
			
			if ((toBitboard & allMineBitboard) == 0L
				&& (toBitboard & opponentKing) == 0L
				&& (toBitboard & opponentKingMoves) == 0L
				&& (toBitboard & allOpponentBitboard) != 0L) {
				
				final int toFieldID = dirs_ids[dirID][0];
				
				if (Checking.isFieldAttacked(bitboard, opponentColour, figureColour,
						toBitboard, toFieldID, freeBitboard | fromBitboard, true)) {
					continue;
				}
				
				if (list != null) {
					int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
					list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
				}
				count++;
				
				if (count >= maxCount) {
					return count;
				}
			}
		}
		
		return count;
	}
	
	public static final int genNonCaptureMoves(final Board bitboard,
			final long excludedToFieldsIDs,
			final int figureID, final int figureColour, final int opponentColour,
			final long fromBitboard,
			final int fromFieldID,
			final long freeBitboard,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final boolean kingSidePossible,
			final boolean queenSidePossible,
			final long opponentKing,
			final int opponentKingFieldID,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
				
		long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		final long[][] dirs = dirsBitBoards[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long toBitboard = dirs[dirID][0];
			
			if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
				continue;
			}
			
			if ((toBitboard & allMineBitboard) == 0L
				&& (toBitboard & opponentKing) == 0L
				&& (toBitboard & opponentKingMoves) == 0L
				&& (toBitboard & allOpponentBitboard) == 0L) {
				
				final int toFieldID = dirs_ids[dirID][0];
				
				if (Checking.isFieldAttacked(bitboard, opponentColour, figureColour,
						toBitboard, toFieldID, freeBitboard | fromBitboard, true)) {
					continue;
				}
				
				if (list != null) {
					list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
				}
				count++;
				
				if (count >= maxCount) {
					return count;
				}
			}
		}
		
		if (kingSidePossible) {
			if (list != null) {
				list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], 
						Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
			}
			count++;
			
			if (count >= maxCount) {
				return count;
			}
		}
		
		if (queenSidePossible) {
			if (list != null) {
				list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour],
						Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
			}
			count++;
			
			if (count >= maxCount) {
				return count;
			}
		}
		
		return count;
	}
	
	public static final int genCastleSides(final int figureColour,
			final boolean kingSidePossible,
			final boolean queenSidePossible,
			final IInternalMoveList list,
			final int maxCount) {
		int count = 0;
		
		if (kingSidePossible) {
			if (list != null) {
				if (list != null) {
					list.reserved_add(MoveInt.createKingSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour], 
							Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[figureColour]));
				}
			}
			count++;
			
			if (count >= maxCount) {
				return count;
			}
		}
		
		if (queenSidePossible) {
			if (list != null) {
				list.reserved_add(MoveInt.createQueenSide(Castling.KINGS_PIDS_BY_COLOUR[figureColour], Castling.KING_FROM_FIELD_ID_BY_COLOUR[figureColour],
						Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[figureColour]));
			}
			count++;
			
			if (count >= maxCount) {
				return count;
			}
		}
		
		return count;
	}
	
	public static final boolean isPossible(final Board board, final int move,
			final int[] figuresIDsPerFieldsIDs,
			final boolean kingSidePossible,
			final boolean queenSidePossible,
			final long opponentKing,
			final int opponentKingFieldID,
			final long free
			/*final boolean hasEnpassant,
			final int enpassantColour,
			final long enpassantPawnBitboard*/) {
		
		//if (move[3] != figureType) {
		//	throw new IllegalStateException();
		//}
		
		int figureID = MoveInt.getFigurePID(move);
		final int colour = MoveInt.getColour(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		long fromBitboard =  Fields.ALL_ORDERED_A1H1[fromFieldID];
		final int opponentColour = Figures.OPPONENT_COLOUR[colour];
		
			/*int toBitboard = move[8];
			int dir = (int) move[5];
			int seq = (int) move[6];
		 */
		if (figuresIDsPerFieldsIDs[fromFieldID] != figureID) {
			return false;
		}

		int toFieldID = MoveInt.getToFieldID(move);
		long toBitboard =  Fields.ALL_ORDERED_A1H1[toFieldID];
		
		if (MoveInt.isCapture(move)) {
			int capturedFigureID = MoveInt.getCapturedFigurePID(move);
			if (figuresIDsPerFieldsIDs[toFieldID] != capturedFigureID) {
				return false;
			}
		} else {
			if (figuresIDsPerFieldsIDs[toFieldID] != Constants.PID_NONE) {
				return false;
			}
		}
		
		long opponentKingMoves = KingPlies.ALL_KING_MOVES[opponentKingFieldID];
		if ((toBitboard & opponentKing) != 0L || (toBitboard & opponentKingMoves) != 0L) {
			return false;
		}
		
		if (MoveInt.isCastleKingSide(move)) {
			return kingSidePossible;
		} else if (MoveInt.isCastleQueenSide(move)) {
			return queenSidePossible;
		}
		
		if (Checking.isFieldAttacked(board, opponentColour, colour,
				toBitboard, toFieldID, free | fromBitboard, true)) {
			return false;
		}
		
		return true;
	}
}
