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
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;
import bagaturchess.bitboard.impl.plies.checking.QueenUniqueChecks;
import bagaturchess.bitboard.impl.zobrist.ConstantStructure;

/**
 * Do not use OOP in moves generators,
 * because of performance degradation caused by the impossibility to inline abstract methods at compile time. 
 */
public class QueenMovesGen extends QueenUniqueChecks {

	
	public static final int genAllMoves(
			final long excludedToFieldsIDs,
			final boolean interuptAtFirstExclusionHit,
			final int figureID,
			final int fromFieldID,
			final long freeBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		count += OfficerMovesGen.genAllMoves(excludedToFieldsIDs, interuptAtFirstExclusionHit, figureID,
				fromFieldID,
				freeBitboard,
				allOpponentBitboard,
				figuresIDsPerFieldsIDs,
				list,
				maxCount);
		if (count >= maxCount) {
			return count;
		}
		
		count += CastleMovesGen.genAllMoves(excludedToFieldsIDs, interuptAtFirstExclusionHit, figureID,
				fromFieldID,
				freeBitboard,
				allOpponentBitboard,
				figuresIDsPerFieldsIDs,
				list,
				maxCount);

		return count;
	}
	
	public static final int genCaptureMoves(
			final Board bitboard,
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long freeBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		long attacks = OfficerPlies.ALL_OFFICER_MOVES[fromFieldID];
		if ((allOpponentBitboard & attacks) != 0L) {
			count += OfficerMovesGen.genCaptureMoves(excludedToFieldsIDs, true, figureID, 
					fromFieldID,
					freeBitboard,
					allOpponentBitboard,
					figuresIDsPerFieldsIDs,
					list,
					maxCount);
			if (count >= maxCount) {
				return count;
			}
		}
		
		attacks = CastlePlies.ALL_CASTLE_MOVES[fromFieldID];
		if ((allOpponentBitboard & attacks) != 0L) {
			count += CastleMovesGen.genCaptureMoves(bitboard, excludedToFieldsIDs, true, figureID,
					fromFieldID,
					freeBitboard,
					allOpponentBitboard,
					figuresIDsPerFieldsIDs,
					list,
					maxCount);
		}
		
		return count;
	}
	
	public static final int genNonCaptureMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long freeBitboard,
			final long allOpponentBitboard,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		count += OfficerMovesGen.genNonCaptureMoves(excludedToFieldsIDs, true, figureID,
				fromFieldID,
				freeBitboard,
				allOpponentBitboard,
				list,
				maxCount);
		if (count >= maxCount) {
			return count;
		}
		
		count += CastleMovesGen.genNonCaptureMoves(excludedToFieldsIDs, true, figureID,
				fromFieldID,
				freeBitboard,
				allOpponentBitboard,
				list,
				maxCount);
		
		return count;
	}
	
	public static final boolean isPossible(int move,
			final int[] figuresIDsPerFieldsIDs,
			final long free) {
		
		//if (move[3] != Figures.TYPE_QUEEN) {
		//	throw new IllegalStateException();
		//}
		
		int figureDirType = MoveInt.getDirType(move);
		if (figureDirType == Figures.TYPE_CASTLE) {
			return CastleMovesGen.isPossible(move, figuresIDsPerFieldsIDs, free);
		} else if (figureDirType == Figures.TYPE_OFFICER) {
			return OfficerMovesGen.isPossible(move, figuresIDsPerFieldsIDs, free);
		} else {
			throw new IllegalStateException();
		}
	}
	
	public static final int genCheckMoves(
			final long excludedToFieldsIDs,
			final int figureID,
			final int fromFieldID,
			final long opponentKingBitboard,
			final int opponentKingFieldID,
			final long freeBitboard,
			final long allMineBitboard,
			final long allOpponentBitboard,
			final int[] figuresIDsPerFieldsIDs,
			final IInternalMoveList list,
			final int maxCount) {
		
		int count = 0;
		
		count += OfficerMovesGen.genCheckMoves(excludedToFieldsIDs, figureID,
				fromFieldID,
				opponentKingBitboard,
				opponentKingFieldID,
				freeBitboard,
				allMineBitboard,
				allOpponentBitboard,
				figuresIDsPerFieldsIDs,
				list,
				maxCount);
		if (count >= maxCount) {
			return count;
		}
		
		count += CastleMovesGen.genCheckMoves(excludedToFieldsIDs, figureID,
				fromFieldID,
				opponentKingFieldID,
				freeBitboard,
				allMineBitboard,
				allOpponentBitboard,
				figuresIDsPerFieldsIDs,
				list,
				maxCount);
		if (count >= maxCount) {
			return count;
		}
		
		int[] fields =  CHECK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
		int[] dirs = CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][opponentKingFieldID];
		int[] dirTypes = CHECK_MIDDLE_FIELDS_DIR_TYPES[fromFieldID][opponentKingFieldID];
		int[] seqs = CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][opponentKingFieldID];
		long[] fieldBoards =  CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
		long[] path = FIELDS_WHOLE_PATH[fromFieldID][opponentKingFieldID];
		
		if (fields != null) {
			final int size = fields.length;
			for (int i=0; i<size; i++) {
				int toFieldID = fields[i];
				long middleFieldBitboard = fieldBoards[i];
				long curPath = path[i];
				
				if ((excludedToFieldsIDs & middleFieldBitboard) != NUMBER_0) {
					continue;
				}
				
				if ((allMineBitboard & curPath) == 0L && (allOpponentBitboard & curPath) == 0L) {
					if ((middleFieldBitboard & freeBitboard) != NUMBER_0) {
						
						/**
						 * Non-Capture
						 */
						
						if (list != null) {
							int dirID = dirs[i];
							int seq = seqs[i];
							list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID, dirID, seq));
						}
						count++;
						
						if (count >= maxCount) {
							return count;
						}
						
					} else if ((middleFieldBitboard & allOpponentBitboard) != NUMBER_0) {
						
						/**
						 * Capture
						 */
						
						if (list != null) {
							int dirID = dirs[i];
							int seq = seqs[i];
							int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
							list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, dirID, seq, capturedFigureID));
						}
						count++;
						
						if (count >= maxCount) {
							return count;
						}
						
						continue;
					} else {
						
						/**
						 * Defend
						 */
						
						continue;
					}
				}
			}
		}
		
		return count;
	}
}
