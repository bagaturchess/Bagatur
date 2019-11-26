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
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.checking.OfficerChecks;

/**
 * Do not use OOP in moves generators,
 * because of performance degradation caused by the impossibility to inline abstract methods at compile time. 
 */
public class OfficerMovesGen extends OfficerChecks {
	
	//static final int figureType = Figures.TYPE_OFFICER;
	
	static final int[][] validDirsIDs = ALL_OFFICER_VALID_DIRS;
	static final int[][][] dirsFieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS;
	static final long[][][] dirsBitBoards = ALL_OFFICER_DIRS_WITH_BITBOARDS;
	static final long[][] wholeDirsBitboards = ALL_OFFICER_DIR_MOVES;

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
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		final long[][] dirs = dirsBitBoards[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long toBitboard = dirs[dirID][seq];
				
				if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
					if ((toBitboard & freeBitboard) == NUMBER_0) {
						break;
					}
					if (interuptAtFirstExclusionHit) {
						break;
					}
					continue;
				}
				
				if ((toBitboard & freeBitboard) != NUMBER_0) {
					
					/**
					 * Non-Capture
					 */
					
					if (list != null) {
						final int toFieldID = dirs_ids[dirID][seq];
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
					
				} else if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
					
					/**
					 * Capture
					 */
					
					if (list != null) {
						final int toFieldID = dirs_ids[dirID][seq];						
						int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
						list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
					
					break;
				} else {
					
					/**
					 * Defend
					 */
					
					break;
				}
			}
		}
		
		return count;
	}
	
	public static final int genCaptureMoves(
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
		
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		final int[][] dirs_ids = dirsFieldIDs[fromFieldID];
		final long[][] dirs = dirsBitBoards[fromFieldID];
		
		final int size = validDirIDs.length;
		for (int i=0; i<size; i++) {
			
			int dirID = validDirIDs[i];
			
			//OPT
			long allInDir = wholeDirsBitboards[dirID][fromFieldID];
			if ((allInDir & allOpponentBitboard) == 0) {
				continue;
			}
			
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long toBitboard = dirs[dirID][seq];
				
				if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
					if ((toBitboard & freeBitboard) == NUMBER_0) {
						break;
					}
					if (interuptAtFirstExclusionHit) {
						break;
					}
					continue;
				}
				
				if ((toBitboard & freeBitboard) != NUMBER_0) {
					continue;
				} else if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
					
					/**
					 * Capture
					 */
					
					if (list != null) {
						final int toFieldID = dirs_ids[dirID][seq];
						int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
						list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
					
					break;
				} else {
					
					/**
					 * Defend
					 */
					
					break;
				}
			}
		}
		
		return count;
	}
	
	public static final int genNonCaptureMoves(
			final long excludedToFieldsIDs,
			final boolean interuptAtFirstExclusionHit,
			final int figureID,
			final int fromFieldID,
			final long freeBitboard,
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
			long[] dirBitboards = dirs[dirID];
			
			for (int seq=0; seq<dirBitboards.length; seq++) {
				long toBitboard = dirs[dirID][seq];
				
				if ((excludedToFieldsIDs & toBitboard) != NUMBER_0) {
					if ((toBitboard & freeBitboard) == NUMBER_0) {
						break;
					}
					if (interuptAtFirstExclusionHit) {
						break;
					}
					continue;
				}
				
				if ((toBitboard & freeBitboard) != NUMBER_0) {
					
					/**
					 * Non-Capture
					 */
					
					if (list != null) {
						final int toFieldID = dirs_ids[dirID][seq];
						list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
					}
					count++;
					
					if (count >= maxCount) {
						return count;
					}
					
				} else if ((toBitboard & allOpponentBitboard) != NUMBER_0) {
					break;
				} else {
					
					/**
					 * Defend
					 */
					
					break;
				}
			}
		}
		
		return count;
	}
	
	public static final boolean isPossible(final int move,
			final int[] figuresIDsPerFieldsIDs,
			final long free) {
		
		int figureID = MoveInt.getFigurePID(move);
		int fromFieldID = MoveInt.getFromFieldID(move);
		
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
		
		/*int dir = (int) move[5];
		final int [] validDirIDs = validDirsIDs[fromFieldID];
		for (int i=0; i<validDirIDs.length; i++) {
			if (validDirIDs[i] == dir) {
				break;
			}
			if (i == validDirIDs.length - 1) {
				return false;
			}
		}*/
		
		long path = PATHS[fromFieldID][toFieldID];
		if (path == PATH_NONE) {
			throw new IllegalStateException("Path none");
		}
		
		if ((path & free) != path) {
			return false;
		}
		
		/*int seq = (int) move[6];
		final long[] dirs = dirsBitBoards[fromFieldID][dir];
		for (int i=0; i<dirs.length; i++) {
			if (i == seq) {
				return true;
			}
			if ((dirs[i] & free) == 0L) {
				return false;
			}
		}*/
		
		return true;
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
		
		if ((Fields.ALL_OFFICERS_FIELDS[fromFieldID] & opponentKingBitboard) != 0L) {
		
			int[] fields =  CHECK_MIDDLE_FIELDS_IDS[fromFieldID][opponentKingFieldID];
			//int[] dirs = CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][opponentKingFieldID];
			//int[] seqs = CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][opponentKingFieldID];
			long[] fieldBoards =  CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][opponentKingFieldID];
			long[] path = FIELDS_WHOLE_PATH[fromFieldID][opponentKingFieldID];
			
			if (fields != null) {
				final int size = fields.length;
				for (int i=0; i<size; i++) {
					int toFieldID = fields[i];
					long middleFieldBitboard = fieldBoards[i];
					long curPath = path[i];
					
					if ((excludedToFieldsIDs & middleFieldBitboard) != NUMBER_0) {
						//break;
						continue;
					}
					
					//if (curPath == 0 || (curPath != 0 && (allMineBitboard & curPath) == 0L && (allOpponentBitboard & curPath) == 0L)) {
					if ((allMineBitboard & curPath) == 0L && (allOpponentBitboard & curPath) == 0L) {
						if ((middleFieldBitboard & freeBitboard) != NUMBER_0) {
							
							/**
							 * Non-Capture
							 */
							
							if (list != null) {
								list.reserved_add(MoveInt.createNonCapture(figureID, fromFieldID, toFieldID));
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
								int capturedFigureID = figuresIDsPerFieldsIDs[toFieldID];
								list.reserved_add(MoveInt.createCapture(figureID, fromFieldID, toFieldID, capturedFigureID));
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
		}
		
		return count;
	}

}
