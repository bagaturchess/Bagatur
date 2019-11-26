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
import bagaturchess.bitboard.impl.Constants;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.checking.KnightChecks;

/**
 * Do not use OOP in moves generators,
 * because of performance degradation caused by the impossibility to inline abstract methods at compile time. 
 */
public class KnightMovesGen extends KnightChecks {
	
	static final int figureType = Figures.TYPE_KNIGHT;
	
	static final int[][] validDirsIDs = ALL_KNIGHT_VALID_DIRS;
	static final int[][][] dirsFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS;
	static final long[][][] dirsBitBoards = ALL_KNIGHT_DIRS_WITH_BITBOARDS;

	
	public static final int genAllMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
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
			
			if ((toBitboard & allMineBitboard) == 0L) {
				
				if (list != null) {
					final int toFieldID = dirs_ids[dirID][0];

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
		
		return count;
	}
	
	public static final int genCaptureMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
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
					&& (toBitboard & allOpponentBitboard) != 0L) {
				
				if (list != null) {
					final int toFieldID = dirs_ids[dirID][0];
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
	
	public static final int genNonCaptureMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
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
					&& (toBitboard & allOpponentBitboard) == 0L) {
				
				if (list != null) {
					final int toFieldID = dirs_ids[dirID][0];
					list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
				}
				count++;
				
				if (count >= maxCount) {
					return count;
				}
			}
		}
		
		return count;
	}
	
	public static final boolean isPossible(final int move,
			final int[] figuresIDsPerFieldsIDs
			/*final boolean hasEnpassant,
			final int enpassantColour,
			final long enpassantPawnBitboard*/) {
		
		//if (move[3] != figureType) {
		//	throw new IllegalStateException();
		//}
		
		int figureID = MoveInt.getFigurePID(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		
		/*
		  int fromBitboard = (int) move[7];
			int toBitboard = (int) move[8];
			int dir = (int) move[5];
			int seq = (int) move[6];
		 */
		if (figuresIDsPerFieldsIDs[fromFieldID] != figureID) {
			return false;
		}

		int toFieldID = MoveInt.getToFieldID(move);
		
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
		
		return true;
	}
	
	public static final int genCheckMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final int opponentKingFieldID,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		int[] fields =  CHECK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
		//int[] dirs = CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][opponentKingFieldID];
		long[] fieldBoards =  CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
		
		if (fields != null) {
			final int size = fields.length;
			for (int i=0; i<size; i++) {
				
				long middleFieldBitboard = fieldBoards[i];
				
				if ((excludedToFieldsIDs & middleFieldBitboard) != NUMBER_0) {
					continue;
				}
				
				if ((middleFieldBitboard & allMineBitboard) == 0L) {
					
					if (list != null) {
						int toFieldID = fields[i];
		
						if ((middleFieldBitboard & allOpponentBitboard) != 0L) {//Capture
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
		}
		
		return count;
	}
}
